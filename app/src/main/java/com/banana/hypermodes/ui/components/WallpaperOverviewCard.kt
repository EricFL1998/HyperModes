package com.banana.hypermodes.ui.components

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.WallpaperSet
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 壁纸概览大卡片：复刻官方个性化界面顶部的预览样式
 * （kg_settings_view_template.xml），横向两个等宽手机 mockup（锁屏 / 桌面），
 * 20dp 圆角、8dp 间距、竖屏比例：
 *
 * - 锁屏 mockup：壁纸背景 + 时钟（当前时间）+ 日期，底部中央"自定义"胶囊按钮
 * - 桌面 mockup：壁纸背景 + 图标/组件占位网格，底部中央"自定义"胶囊按钮
 *
 * - 未配置（WallpaperSet 子项为 null）：显示系统当前锁屏/桌面壁纸。
 * - 已配置：显示配置的壁纸图片。
 *
 * @param wallpaper 模式保存的壁纸 set（lock / desktop 子项可单独为 null）
 * @param onClick 点击卡片（进入官方壁纸界面）
 * @param onClear 清除已保存的壁纸配置（恢复"未编辑"状态）；null 则不显示清除入口
 */
@Composable
fun WallpaperOverviewCard(
    wallpaper: WallpaperSet?,
    onClick: () -> Unit,
    onClear: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        insideMargin = PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            SmallTitle(
                text = stringResource(R.string.wallpaper),
                modifier = Modifier.padding(bottom = 4.dp),
                insideMargin = PaddingValues(0.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LockScreenMockup(
                    image = rememberWallpaperBitmap(
                        context = context,
                        configuredPath = wallpaper?.lock?.imagePath,
                        which = WallpaperManager.FLAG_LOCK
                    ),
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                )
                HomeScreenMockup(
                    image = rememberWallpaperBitmap(
                        context = context,
                        configuredPath = wallpaper?.desktop?.imagePath,
                        which = WallpaperManager.FLAG_SYSTEM
                    ),
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                )
            }
            if (wallpaper?.hasAny == true && onClear != null) {
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    text = stringResource(R.string.wallpaper_clear),
                    onClick = onClear,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 锁屏 mockup：官方个性化顶部锁屏预览样式——壁纸背景 + 时钟（大字时分，
 * 分钟略透明）+ 日期，底部中央"自定义"胶囊按钮。
 */
@Composable
private fun LockScreenMockup(
    image: Bitmap?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = remember { Calendar.getInstance() }
    val hour = remember(now) {
        String.format("%02d", now.get(Calendar.HOUR_OF_DAY))
    }
    val minute = remember(now) {
        String.format("%02d", now.get(Calendar.MINUTE))
    }
    val date = remember(now) {
        SimpleDateFormat("M/d EEE", Locale.CHINA).format(now.time)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 19.5f)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        // 壁纸背景（或深色渐变占位）
        MockupBackground(image)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            // 时钟：小时大字 + 分钟小字（仿官方杂志时钟）
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = hour,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = minute,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = date,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.weight(1f))
            CustomizePill(onClick = onClick)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

/**
 * 桌面 mockup：官方个性化顶部桌面预览样式——壁纸背景 + 图标/组件占位网格，
 * 底部中央"自定义"胶囊按钮。
 */
@Composable
private fun HomeScreenMockup(
    image: Bitmap?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 19.5f)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        MockupBackground(image)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 第一行：左侧大组件 + 右侧 2x2 小图标
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PlaceholderBlock(
                    modifier = Modifier
                        .weight(1.25f)
                        .aspectRatio(1.1f)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlaceholderBlock(modifier = Modifier.weight(1f).aspectRatio(1f))
                        PlaceholderBlock(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlaceholderBlock(modifier = Modifier.weight(1f).aspectRatio(1f))
                        PlaceholderBlock(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
            // 第二行：左侧 2 个小图标 + 右侧大组件
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlaceholderBlock(modifier = Modifier.weight(1f).aspectRatio(1f))
                        PlaceholderBlock(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
                PlaceholderBlock(
                    modifier = Modifier
                        .weight(1.25f)
                        .aspectRatio(1.1f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // 底部 Dock：4 个图标
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(4) {
                    PlaceholderBlock(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(horizontal = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            CustomizePill(onClick = onClick)
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

/** 壁纸背景：有图铺满裁剪，无图用深色渐变占位。 */
@Composable
private fun MockupBackground(image: Bitmap?) {
    if (image != null) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2B3A52),
                            Color(0xFF1B2436)
                        )
                    )
                )
        )
    }
}

/** 桌面 mockup 里的半透明白色占位块（图标/组件）。 */
@Composable
private fun PlaceholderBlock(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.28f))
    )
}

/** 底部中央"自定义"胶囊按钮（与官方 kg_settings_background_template_apply_button 风格一致）。 */
@Composable
private fun CustomizePill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.30f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 5.dp)
    ) {
        Text(
            text = stringResource(R.string.wallpaper_customize),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

/**
 * 加载预览图：优先使用配置的图片路径；未配置时读取系统当前壁纸
 * （桌面 / 锁屏按 which）。锁屏壁纸普通 App 可能读取受限，失败则返回 null。
 */
@Composable
private fun rememberWallpaperBitmap(
    context: Context,
    configuredPath: String?,
    which: Int
): Bitmap? {
    var bitmap by remember(configuredPath) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(configuredPath, which) {
        bitmap = loadWallpaperBitmap(context, configuredPath, which)
    }
    return bitmap
}

private fun loadWallpaperBitmap(
    context: Context,
    configuredPath: String?,
    which: Int
): Bitmap? {
    if (!configuredPath.isNullOrEmpty()) {
        return runCatching { BitmapFactory.decodeFile(configuredPath) }.getOrNull()
    }
    return runCatching {
        val wm = WallpaperManager.getInstance(context)
        wm.getDrawable(which)?.let { drawable ->
            // Drawable may be a BitmapDrawable; otherwise render it to a bitmap.
            if (drawable is android.graphics.drawable.BitmapDrawable) {
                drawable.bitmap
            } else {
                val w = drawable.intrinsicWidth.coerceAtLeast(1)
                val h = drawable.intrinsicHeight.coerceAtLeast(1)
                Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { bmp ->
                    val canvas = android.graphics.Canvas(bmp)
                    drawable.setBounds(0, 0, w, h)
                    drawable.draw(canvas)
                }
            }
        }
    }.getOrNull()
}
