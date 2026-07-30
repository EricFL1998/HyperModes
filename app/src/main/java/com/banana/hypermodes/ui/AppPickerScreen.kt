package com.banana.hypermodes.ui

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.Mode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

private fun android.graphics.drawable.Drawable.toImageBitmap(size: Int = 96) =
    Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also { bmp ->
        val canvas = Canvas(bmp)
        setBounds(0, 0, size, size)
        draw(canvas)
    }.asImageBitmap()

private data class AppEntry(
    val packageName: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.ImageBitmap?
)

/**
 * App picker. Generic version for allowed apps, paused apps, or trigger apps.
 */
@Composable
fun AppPickerScreen(
    title: String,
    initialSelection: Set<String>,
    singleSelection: Boolean = false,
    onBack: () -> Unit,
    onSelectionChanged: (Set<String>) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var selectedApps by remember { mutableStateOf(initialSelection) }
    var apps by remember { mutableStateOf<List<AppEntry>?>(null) }
    var query by remember { mutableStateOf("") }

    // Load launchable apps off the main thread.
    LaunchedEffect(Unit) {
        apps = withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                addCategory(android.content.Intent.CATEGORY_LAUNCHER)
            }
            pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
                .map { resolve ->
                    AppEntry(
                        packageName = resolve.activityInfo.packageName,
                        label = resolve.loadLabel(pm).toString(),
                        icon = resolve.loadIcon(pm)?.toImageBitmap()
                    )
                }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase() }
        }
    }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = stringResource(R.string.back)
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
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Search bar
            item {
                SearchBar(
                    modifier = Modifier.padding(bottom = 12.dp),
                    inputField = {
                        InputField(
                            query = query,
                            onQueryChange = { query = it },
                            onSearch = { },
                            expanded = false,
                            onExpandedChange = { },
                            label = stringResource(R.string.search_apps)
                        )
                    },
                    expanded = false,
                    onExpandedChange = { }
                ) { }
            }

            val list = apps
            if (list == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                val filtered = if (query.isBlank()) list else list.filter {
                    it.label.contains(query, ignoreCase = true) ||
                            it.packageName.contains(query, ignoreCase = true)
                }
                items(filtered, key = { it.packageName }) { app ->
                    val isSelected = selectedApps.contains(app.packageName)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        onClick = if (singleSelection) {
                            {
                                onSelectionChanged(setOf(app.packageName))
                                onBack()
                            }
                        } else null
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
                                if (app.icon != null) {
                                    Image(
                                        bitmap = app.icon,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .padding(end = 12.dp)
                                    )
                                }
                                Text(
                                    text = app.label,
                                    style = MiuixTheme.textStyles.body1
                                )
                            }
                            
                            if (singleSelection) {
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        style = MiuixTheme.textStyles.body1,
                                        color = MiuixTheme.colorScheme.primary
                                    )
                                }
                            } else {
                                Switch(
                                    checked = isSelected,
                                    onCheckedChange = { on ->
                                        val newApps = if (on) selectedApps + app.packageName
                                        else selectedApps - app.packageName
                                        selectedApps = newApps
                                        onSelectionChanged(newApps)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }
    }
}
