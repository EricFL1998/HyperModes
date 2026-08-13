package com.banana.hypermodes.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.protocol.Protocol
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * NFC 标签触发器配置：输入标签 ID（十六进制，留空匹配任意标签），
 * 或点「扫描学习」把 NFC 标签贴近手机自动获取 ID（App 前台扫描后广播
 * [Protocol.ACTION_NFC_TAG]，这里接收并回填）。
 */
@Composable
fun NfcTriggerPickerDialog(
    initialTagId: String = "",
    show: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (tagId: String) -> Unit
) {
    val context = LocalContext.current
    var tagId by remember(show) { mutableStateOf(initialTagId) }
    var scanning by remember(show) { mutableStateOf(false) }

    // 监听 App 前台扫描到的 NFC 标签，回填 ID
    DisposableEffect(context, show, scanning) {
        if (!show || !scanning) return@DisposableEffect onDispose {}
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val scanned = intent.getStringExtra(Protocol.EXTRA_NFC_TAG_ID) ?: return
                if (scanned.isNotBlank()) {
                    tagId = scanned
                    scanning = false
                }
            }
        }
        context.registerReceiver(
            receiver,
            IntentFilter(Protocol.ACTION_NFC_TAG),
            Context.RECEIVER_EXPORTED
        )
        onDispose { runCatching { context.unregisterReceiver(receiver) } }
    }

    OverlayDialog(
        title = stringResource(R.string.trigger_nfc),
        show = show,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = tagId,
                onValueChange = { tagId = it.trim() },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.nfc_tag_id),
                useLabelAsPlaceholder = true,
                singleLine = true
            )

            Text(
                text = stringResource(R.string.nfc_tag_id_hint),
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )

            TextButton(
                text = if (scanning) stringResource(R.string.nfc_scan_waiting)
                else stringResource(R.string.nfc_scan_capture),
                onClick = { scanning = !scanning },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    text = stringResource(R.string.done),
                    onClick = { onConfirm(tagId) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    }
}
