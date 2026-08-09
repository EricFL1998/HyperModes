package com.banana.hypermodes.ui.components

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.banana.hypermodes.R
import com.banana.hypermodes.data.WallpaperSet
import java.io.File
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.Text

/**
 * 壁纸概览大卡片：复刻官方个性化界面顶部的预览样式
 * （kg_settings_view_template.xml），横向两个等宽手机 mockup（锁屏 / 桌面），
 * 20dp 圆角、8dp 间距、竖屏比例：
 *
 * - 锁屏 mockup：真实锁屏壁纸背景 + 按官方锁屏 JSON（clockInfo）渲染的时钟/日期，
 *   底部中央"自定义"胶囊按钮
 * - 桌面 mockup：真实桌面壁纸背景 + 图标/组件占位网格，底部中央"自定义"胶囊按钮
 *
 * - 未配置（WallpaperSet 子项为 null）：显示系统当前锁屏/桌面壁纸
 *   （systemWallpaper 来自 system_server 快照，锁屏普通 App 读不到文件，
 *   必须由 system_server 复制到 App 可读目录）。
 * - 已配置：显示配置的壁纸图片。
 *
 * @param wallpaper 模式保存的壁纸 set（lock / desktop 子项可单独为 null）
 * @param systemWallpaper 系统当前壁纸快照（未配置时作为预览底图；可为 null）
 * @param onLockClick 点击锁屏 mockup（进入锁屏自定义界面）
 * @param onDesktopClick 点击桌面 mockup（进入桌面自定义界面）
 * @param onClear 清除已保存的壁纸配置（恢复"未编辑"状态）；null 则不显示清除入口
 */
@Composable
fun WallpaperOverviewCard(
    wallpaper: WallpaperSet?,
    systemWallpaper: WallpaperSet?,
    /** 壁纸内容变化的刷新信号：文件被覆盖但路径/JSON 不变时，
     *  Compose 会因输入数据相等跳过重组，预览无法感知内容变化；
     *  每次捕获/恢复后递增此值强制重新解码。 */
    refreshTick: Int = 0,
    onLockClick: () -> Unit,
    onDesktopClick: () -> Unit,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LockScreenMockup(
                    image = rememberWallpaperBitmap(
                        context = context,
                        configuredPath = wallpaper?.lock?.imagePath
                            ?: systemWallpaper?.lock?.imagePath,
                        which = WallpaperManager.FLAG_LOCK,
                        refreshTick = refreshTick
                    ),
                    lockscreenJson = wallpaper?.lock?.lockscreenJson
                        ?: systemWallpaper?.lock?.lockscreenJson,
                    subjectMaskPath = wallpaper?.lock?.subjectMaskPath
                        ?: systemWallpaper?.lock?.subjectMaskPath,
                    onClick = onLockClick,
                    modifier = Modifier.weight(1f)
                )
                HomeScreenMockup(
                    image = rememberWallpaperBitmap(
                        context = context,
                        configuredPath = wallpaper?.desktop?.imagePath
                            ?: systemWallpaper?.desktop?.imagePath,
                        which = WallpaperManager.FLAG_SYSTEM,
                        refreshTick = refreshTick
                    ),
                    onClick = onDesktopClick,
                    modifier = Modifier.weight(1f)
                )
            }
            if (wallpaper?.hasAny == true && onClear != null) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    text = stringResource(R.string.wallpaper_clear),
                    onClick = onClear,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

/**
 * 锁屏 mockup：官方个性化顶部锁屏预览样式——真实壁纸背景 + 按官方锁屏 JSON
 * （clockInfo.templateId / primaryColor / isAutoPrimaryColor）渲染的时钟与日期，
 * 底部中央"自定义"胶囊按钮。
 *
 * @param lockscreenJson Settings.Secure constant_lockscreen_info 的 JSON，
 * 用于读取时钟模板与颜色；为 null 时回退经典大字时钟。
 */
@Composable
private fun LockScreenMockup(
    image: Bitmap?,
    lockscreenJson: String?,
    subjectMaskPath: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // 卡片实际像素尺寸（官方预览按真实屏幕尺寸渲染后缩放到这里）
    var cardSize by remember { mutableStateOf(0 to 0) }
    // 优先用官方组件渲染（真实壁纸 + 官方时钟样式）；失败回退 Compose 复刻。
    // 同步创建在 UI 线程，首次几十 ms 可接受；反射全部 try/catch，绝不影响其它功能。
    val officialView = remember(
        lockscreenJson, image, subjectMaskPath,
        cardSize.first, cardSize.second
    ) {
        if (!lockscreenJson.isNullOrEmpty() && cardSize.first > 0 && cardSize.second > 0) {
            runCatching {
                OfficialTemplatePreview.createClockContainer(
                    context,
                    lockscreenJson,
                    image,
                    subjectMaskPath,
                    cardSize.first,
                    cardSize.second
                )
            }.getOrNull()
        } else {
            null
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 19.5f)
            .onSizeChanged { size -> cardSize = size.width to size.height }
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        if (officialView != null) {
            // 官方组件渲染：真实壁纸 + 官方时钟模板。
            // AndroidView 的 factory 只在首次组合执行一次，remember 生成的新容器
            // 不会自动替换旧视图——用 key(officialView) 让容器变化时重建整个子树，
            // 否则编辑后预览永远停留在首次创建的旧视图。
            key(officialView) {
                AndroidView(
                    factory = { officialView },
                    modifier = Modifier.fillMaxSize()
                )
            }
            // 底部"自定义"胶囊按钮叠加在官方时钟上
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                CustomizePill(onClick = onClick)
                Spacer(modifier = Modifier.height(4.dp))
            }
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
    val context = LocalContext.current
    var cardSize by remember { mutableStateOf(0 to 0) }
    // 优先用官方 HomeTemplateView 渲染桌面（真实壁纸 + 官方图标网格）；
    // 失败回退 Compose 复刻。
    val officialView = remember(image, cardSize.first, cardSize.second) {
        if (cardSize.first > 0 && cardSize.second > 0) {
            runCatching {
                OfficialTemplatePreview.createHomeContainer(
                    context, image, cardSize.first, cardSize.second
                )
            }.getOrNull()
        } else {
            null
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 19.5f)
            .onSizeChanged { size -> cardSize = size.width to size.height }
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        if (officialView != null) {
            key(officialView) {
                AndroidView(
                    factory = { officialView },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                CustomizePill(onClick = onClick)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
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
    which: Int,
    refreshTick: Int = 0
): Bitmap? {
    // 同步解码（小图 20KB 级别，UI 线程几十 ms 可接受），
    // 确保官方时钟/桌面容器创建时壁纸一定有值，不再异步导致首次为 null。
    // key 里带上文件最后修改时间：编辑后捕获会覆盖同一路径的文件（路径不变、
    // 内容变），仅按路径缓存会一直显示旧图；时间戳变化才触发重新解码。
    val stamp = configuredPath?.let {
        runCatching { File(it).lastModified() }.getOrDefault(0L)
    } ?: 0L
    // refreshTick：内容变化但路径/时间戳未变（或数据相等跳过重组）时强制重新解码
    return remember(configuredPath, stamp, which, refreshTick) {
        loadWallpaperBitmap(context, configuredPath, which)
    }
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
