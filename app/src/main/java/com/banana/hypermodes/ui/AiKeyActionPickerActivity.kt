package com.banana.hypermodes.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.automation.AutomationStore
import com.banana.hypermodes.automation.SavedAutomation
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.protocol.Protocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * AI 键动作选择页。
 *
 * 由 SecurityCore 的原生「AI 键」设置页（短按/长按下拉）在用户选择
 * 「打开应用 / 开关模式 / 开关自动化」后拉起，选择结果通过
 * ACTION_SYSTEM_OP 写回 Settings.System：
 *   click_mi_ai_key / long_press_mi_ai_key = hypermodes_app|mode|automation
 *   hypermodes_ai_key_{click|long}_target   = app:<pkg> | mode:<id> | automation:<id>
 *   hypermodes_ai_key_{click|long}_label    = 展示名（原生设置页摘要用）
 */
class AiKeyActionPickerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val press = intent?.getStringExtra(Protocol.AI_KEY_EXTRA_PRESS)
            ?: Protocol.AI_KEY_PRESS_CLICK
        if (!isAllowedCaller()) {
            finish()
            return
        }

        setContent {
            MiuixTheme(
                colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            ) {
                Box(modifier = Modifier.fillMaxSize().background(MiuixTheme.colorScheme.background)) {
                    AiKeyPickerScreen(
                        press = press,
                        onPick = { kind, id, label ->
                            writeSelection(press, kind, id, label)
                            finish()
                        },
                        onCancel = { finish() }
                    )
                }
            }
        }
    }

    private fun writeSelection(press: String, kind: String, id: String, label: String) {
        val functionKey = if (press == Protocol.AI_KEY_PRESS_LONG) {
            Protocol.AI_KEY_LONG
        } else {
            Protocol.AI_KEY_CLICK
        }
        val targetKey = if (press == Protocol.AI_KEY_PRESS_LONG) {
            Protocol.AI_KEY_TARGET_LONG
        } else {
            Protocol.AI_KEY_TARGET_CLICK
        }
        val labelKey = if (press == Protocol.AI_KEY_PRESS_LONG) {
            Protocol.AI_KEY_LABEL_LONG
        } else {
            Protocol.AI_KEY_LABEL_CLICK
        }
        val function = when (kind) {
            KIND_APP -> Protocol.AI_KEY_ACTION_APP
            KIND_MODE -> Protocol.AI_KEY_ACTION_MODE
            else -> Protocol.AI_KEY_ACTION_AUTOMATION
        }
        writeSystemSetting(functionKey, function)
        writeSystemSetting(targetKey, "$kind:$id")
        writeSystemSetting(labelKey, label)
    }

    private fun writeSystemSetting(key: String, value: String) {
        sendBroadcast(
            Intent(Protocol.ACTION_SYSTEM_OP)
                .setPackage(Protocol.FRAMEWORK_PACKAGE)
                .putExtra(Protocol.EXTRA_OP, Protocol.OP_WRITE_SETTING)
                .putExtra(Protocol.EXTRA_NAMESPACE, "system")
                .putExtra(Protocol.EXTRA_KEY, key)
                .putExtra(Protocol.EXTRA_VALUE, value)
        )
    }

    /** 只允许 HyperOS 设置宿主或本应用拉起，避免第三方 App 改动 AI 键配置。 */
    private fun isAllowedCaller(): Boolean {
        val caller = try {
            val method = Activity::class.java.getMethod("getLaunchedFromPackage")
            method.invoke(this) as? String
        } catch (t: Throwable) {
            null
        } ?: return true
        return caller in ALLOWED_CALLERS
    }

    private fun currentTarget(press: String): String? {
        val key = if (press == Protocol.AI_KEY_PRESS_LONG) {
            Protocol.AI_KEY_TARGET_LONG
        } else {
            Protocol.AI_KEY_TARGET_CLICK
        }
        return runCatching {
            Settings.System.getString(contentResolver, key)
        }.getOrNull()
    }

    private data class AppEntry(
        val packageName: String,
        val label: String,
        val icon: androidx.compose.ui.graphics.ImageBitmap?
    )

    @Composable
    private fun AiKeyPickerScreen(
        press: String,
        onPick: (kind: String, id: String, label: String) -> Unit,
        onCancel: () -> Unit
    ) {
        val context = LocalContext.current
        var apps by remember { mutableStateOf<List<AppEntry>?>(null) }
        var modes by remember { mutableStateOf<List<Mode>>(emptyList()) }
        var automations by remember { mutableStateOf<List<SavedAutomation>>(emptyList()) }
        val selectedTarget = remember(press) { currentTarget(press) }
        val scrollBehavior = MiuixScrollBehavior()

        LaunchedEffect(Unit) {
            apps = withContext(Dispatchers.IO) {
                val pm = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
                    .map {
                        AppEntry(
                            packageName = it.activityInfo.packageName,
                            label = it.loadLabel(pm).toString(),
                            icon = it.loadIcon(pm)?.toImageBitmap()
                        )
                    }
                    .distinctBy { it.packageName }
                    .sortedBy { it.label.lowercase() }
            }
            modes = withContext(Dispatchers.IO) {
                ModeStore.load(context) { DefaultModes.get() }
            }
            automations = withContext(Dispatchers.IO) {
                AutomationStore.load(context)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = context.getString(R.string.ai_key_picker_title),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = MiuixIcons.Back,
                                contentDescription = context.getString(R.string.back)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(top = padding.calculateTopPadding())
            ) {
                item { SectionHeader(context.getString(R.string.ai_key_section_apps)) }
                val appList = apps
                if (appList == null) {
                    item { LoadingRow() }
                } else {
                    items(appList, key = { "app:" + it.packageName }) { app ->
                        val id = "app:${app.packageName}"
                        PickerRow(
                            title = app.label,
                            subtitle = app.packageName,
                            leadingIcon = app.icon,
                            selected = selectedTarget == id,
                            onClick = { onPick(KIND_APP, app.packageName, app.label) }
                        )
                    }
                }

                item { SectionHeader(context.getString(R.string.ai_key_section_modes)) }
                if (modes.isEmpty()) {
                    item { EmptyRow(context.getString(R.string.ai_key_no_modes)) }
                } else {
                    items(modes, key = { "mode:" + it.id }) { mode ->
                        val id = "mode:${mode.id}"
                        PickerRow(
                            title = "${mode.icon} ${mode.name}",
                            subtitle = mode.description,
                            leadingIcon = null,
                            selected = selectedTarget == id,
                            onClick = { onPick(KIND_MODE, mode.id, mode.name) }
                        )
                    }
                }

                item { SectionHeader(context.getString(R.string.ai_key_section_automations)) }
                if (automations.isEmpty()) {
                    item { EmptyRow(context.getString(R.string.ai_key_no_automations)) }
                } else {
                    items(automations, key = { "automation:" + it.id }) { automation ->
                        val id = "automation:${automation.id}"
                        PickerRow(
                            title = "${automation.icon} ${automation.name}",
                            subtitle = automation.description,
                            leadingIcon = null,
                            selected = selectedTarget == id,
                            onClick = { onPick(KIND_AUTOMATION, automation.id, automation.name) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding()) }
            }
        }
    }

    @Composable
    private fun SectionHeader(title: String) {
        Text(
            text = title,
            style = MiuixTheme.textStyles.title4,
            modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 8.dp)
        )
    }

    @Composable
    private fun LoadingRow() {
        Box(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            contentAlignment = Alignment.Center
        ) { Text(text = "…", style = MiuixTheme.textStyles.body2) }
    }

    @Composable
    private fun EmptyRow(text: String) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }

    @Composable
    private fun PickerRow(
        title: String,
        subtitle: String,
        leadingIcon: androidx.compose.ui.graphics.ImageBitmap?,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 10.dp),
            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        Image(
                            bitmap = leadingIcon,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 10.dp)
                        )
                    }
                    Column {
                        Text(text = title, style = MiuixTheme.textStyles.body1)
                        Text(
                            text = subtitle,
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                if (selected) {
                    Text(
                        text = "✓",
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    companion object {
        private const val KIND_APP = "app"
        private const val KIND_MODE = "mode"
        private const val KIND_AUTOMATION = "automation"
        private val ALLOWED_CALLERS = setOf(
            Protocol.SECURITY_CORE_PACKAGE,
            "com.android.settings",
            Protocol.MODULE_PACKAGE
        )
    }
}

private fun android.graphics.drawable.Drawable.toImageBitmap(size: Int = 96) =
    Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also { bmp ->
        val canvas = Canvas(bmp)
        setBounds(0, 0, size, size)
        draw(canvas)
    }.asImageBitmap()
