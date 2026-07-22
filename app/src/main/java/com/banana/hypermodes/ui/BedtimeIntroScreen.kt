package com.banana.hypermodes.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

private const val MI_HEALTH_PACKAGE = "com.mi.health"
private const val DESKCLOCK_PACKAGE = "com.android.deskclock"
private const val BEDTIME_GUIDE_ACTIVITY = "com.android.deskclock.alarm.bedtime.BedtimeGuideActivity"
private const val BEDTIME_MANAGE_ACTIVITY = "com.android.deskclock.alarm.bedtime.BedtimeManageActivity"

private fun isMiHealthInstalled(context: Context): Boolean = try {
    context.packageManager.getPackageInfo(MI_HEALTH_PACKAGE, 0)
    true
} catch (e: Exception) {
    false
}

private fun openBedtimeSetup(context: Context) {
    // First-time setup goes through the guide; fall back to the manage page.
    for (activity in listOf(BEDTIME_GUIDE_ACTIVITY, BEDTIME_MANAGE_ACTIVITY)) {
        try {
            context.startActivity(Intent().apply {
                setClassName(DESKCLOCK_PACKAGE, activity)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            return
        } catch (e: ActivityNotFoundException) {
            // try next
        }
    }
}

private fun openMiHealthDownload(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$MI_HEALTH_PACKAGE")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    } catch (e: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://app.mi.com/details?id=$MI_HEALTH_PACKAGE")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}

/**
 * Pixel-style sleep intro page (睡眠 / 醒来时活力满满 / 设置睡眠) rendered with
 * MIUIX components. Shown when bedtime was never set up in the Clock app.
 */
@Composable
fun BedtimeIntroScreen(onBack: () -> Unit, onSetup: () -> Unit = {}) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val healthInstalled = remember { isMiHealthInstalled(context) }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.intro_title),
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
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                TextButton(
                    text = stringResource(R.string.setup_sleep),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    onClick = {
                        onSetup()
                        openBedtimeSetup(context)
                    }
                )
            }
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
            // Hero: moon illustration + subtitle (title lives in the large top bar)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🌙",
                        fontSize = 96.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = stringResource(R.string.intro_subtitle),
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            // Mi Health guidance card (only when not installed)
            if (!healthInstalled) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.health_missing),
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                text = stringResource(R.string.download_health),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { openMiHealthDownload(context) }
                            )
                        }
                    }
                }
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
