package com.banana.hypermodes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun AutomationsScreen(
    onBack: () -> Unit,
    showBackButton: Boolean = false,
    showFab: Boolean = true,
    useFloatingLayout: Boolean = false
) {
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.automations),
                scrollBehavior = scrollBehavior,
                navigationIcon = if (showBackButton) {
                    {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = MiuixIcons.Back,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                } else {
                    { }
                }
            )
        },
        floatingActionButton = if (showFab) {
            {
                FloatingActionButton(
                    onClick = {
                        // TODO: Add automation creation logic
                    }
                ) {
                    Text(
                        text = "+",
                        fontSize = 32.sp,
                        color = MiuixTheme.colorScheme.onPrimary
                    )
                }
            }
        } else {
            { }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = if (useFloatingLayout) {
                // Floating layout: combine top padding from Scaffold with shared bottom geometry
                PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = BottomLayoutGeometry.contentBottomPadding().calculateBottomPadding()
                )
            } else {
                PaddingValues(top = padding.calculateTopPadding())
            }
        ) {
            // Description text
            item {
                Text(
                    text = "自动化功能即将推出...",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp)
                )
            }

            // Placeholder content
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🤖",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "自动化",
                            style = MiuixTheme.textStyles.headline1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "根据时间、地点或活动自动触发模式",
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                }
            }

            // Bottom spacer with navigation bar padding - account for bottom tab bar
            item {
                if (!useFloatingLayout) {
                    Spacer(modifier = Modifier.height(80.dp))
                }
                // When useFloatingLayout=true, bottom padding is already in contentPadding
            }
        }
    }
}
