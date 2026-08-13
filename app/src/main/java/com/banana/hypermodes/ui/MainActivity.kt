package com.banana.hypermodes.ui

import android.content.Intent
import android.graphics.Color
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.banana.hypermodes.protocol.Protocol

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNfcIntent(intent)

        setContent {
            val darkMode = isSystemInDarkTheme()

            DisposableEffect(darkMode) {
                // Status bar: transparent with auto light/dark icons
                // Navigation bar: transparent to allow bottom bar to extend behind it
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                    ) { darkMode },
                    navigationBarStyle = if (darkMode) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }

                onDispose {}
            }

            HyperModesApp()
        }
    }

    /** 处理 TAG_DISCOVERED 派发（含应用已在前台时的 onNewIntent）。 */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent)
    }

    /** 读取 NFC 标签 ID 并广播给 system_server 的自动化引擎。 */
    private fun handleNfcIntent(intent: Intent?) {
        if (intent == null || intent.action != NfcAdapter.ACTION_TAG_DISCOVERED) return
        try {
            @Suppress("DEPRECATION")
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val tagId = tag?.id?.joinToString("") { "%02x".format(it) } ?: ""
            sendBroadcast(Intent(Protocol.ACTION_NFC_TAG).apply {
                putExtra(Protocol.EXTRA_NFC_TAG_ID, tagId)
            })
        } catch (t: Throwable) {
            // 标签读取失败不影响 App 正常打开
        }
    }
}
