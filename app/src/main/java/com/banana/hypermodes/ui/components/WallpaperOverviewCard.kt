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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.banana.hypermodes.R
import com.banana.hypermodes.data.WallpaperSet
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import org.json.JSONObject

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
 * @param onClick 点击卡片（进入官方壁纸界面）
 * @param onClear 清除已保存的壁纸配置（恢复"未编辑"状态）；null 则不显示清除入口
 */
@Composable
fun WallpaperOverviewCard(
    wallpaper: WallpaperSet?,
    systemWallpaper: WallpaperSet?,
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
                        configuredPath = wallpaper?.lock?.imagePath
                            ?: systemWallpaper?.lock?.imagePath,
                        which = WallpaperManager.FLAG_LOCK
                    ),
                    lockscreenJson = wallpaper?.lock?.lockscreenJson
                        ?: systemWallpaper?.lock?.lockscreenJson,
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                )
                HomeScreenMockup(
                    image = rememberWallpaperBitmap(
                        context = context,
                        configuredPath = wallpaper?.desktop?.imagePath
                            ?: systemWallpaper?.desktop?.imagePath,
                        which = WallpaperManager.FLAG_SYSTEM
                    ),
                    templateEditorJson = wallpaper?.lock?.templateEditorJson
                        ?: systemWallpaper?.lock?.templateEditorJson,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // 卡片实际像素尺寸（官方预览按真实屏幕尺寸渲染后缩放到这里）
    var cardSize by remember { mutableStateOf(0 to 0) }
    // 优先用官方组件渲染（真实壁纸 + 官方时钟样式）；失败回退 Compose 复刻。
    // 同步创建在 UI 线程，首次几十 ms 可接受；反射全部 try/catch，绝不影响其它功能。
    val officialView = remember(lockscreenJson, image, cardSize.first, cardSize.second) {
        if (!lockscreenJson.isNullOrEmpty() && cardSize.first > 0 && cardSize.second > 0) {
            runCatching {
                OfficialTemplatePreview.createClockContainer(
                    context, lockscreenJson, image, cardSize.first, cardSize.second
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
            // 官方组件渲染：真实壁纸 + 官方时钟模板
            AndroidView(
                factory = { officialView },
                modifier = Modifier.fillMaxSize()
            )
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
        } else {
            ComposeLockScreenMockup(
                image = image,
                lockscreenJson = lockscreenJson,
                onClick = onClick
            )
        }
    }
}

/** Compose 复刻版锁屏 mockup（官方组件加载失败时的回退）。 */
@Composable
private fun ComposeLockScreenMockup(
    image: Bitmap?,
    lockscreenJson: String?,
    onClick: () -> Unit
) {
    // 时钟每分钟刷新，与官方预览一致实时走动
    var now by remember { mutableStateOf(Calendar.getInstance()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            now = Calendar.getInstance()
        }
    }
    val hour = String.format("%02d", now.get(Calendar.HOUR_OF_DAY))
    val minute = String.format("%02d", now.get(Calendar.MINUTE))
    val date = SimpleDateFormat("M/d EEE", Locale.CHINA).format(now.time)
    // 解析官方锁屏 JSON 的时钟样式
    val clockStyle = remember(lockscreenJson) { parseClockStyle(lockscreenJson) }
    // isAutoPrimaryColor=true 或颜色无效时官方会从壁纸自动取色，
    // 这里回退白色（壁纸通常是深色，白色对比度最好）
    val primaryColor = if (clockStyle.isAutoPrimaryColor) {
        Color.White
    } else {
        clockStyle.primaryColor ?: Color.White
    }
    val secondaryColor = if (clockStyle.isAutoSecondaryColor) {
        primaryColor.copy(alpha = 0.75f)
    } else {
        clockStyle.secondaryColor ?: primaryColor.copy(alpha = 0.75f)
    }
    val template = clockStyle.templateId ?: "classic"
    // oversize_*：超大时钟（前景小时 + 背景分钟重叠），日期左下角
    val oversize = template.startsWith("oversize")
    // magazine_*：杂志风（日期大、时间小）
    val magazine = template.startsWith("magazine")
    // classic_*：经典大字时钟
    val classic = template.startsWith("classic") || template == "rhombus"
    // clockWeight：官方字重（clockInfo.clockWeight，如 420），映射到 Compose 字重
    val fontWeight = when {
        clockStyle.clockWeight >= 600 -> FontWeight.SemiBold
        clockStyle.clockWeight >= 500 -> FontWeight.Medium
        clockStyle.clockWeight >= 400 -> FontWeight.Normal
        clockStyle.clockWeight > 0 -> FontWeight.Light
        else -> FontWeight.Light
    }
    val hourSize = when {
        oversize -> 60.sp
        magazine -> 30.sp
        classic -> 44.sp
        else -> 44.sp
    }
    val minuteSize = when {
        oversize -> 26.sp
        magazine -> 30.sp
        classic -> 26.sp
        else -> 26.sp
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 壁纸背景（或深色渐变占位）
        MockupBackground(image)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            // 时钟：oversize 模板为前后双层时钟，小时为前景（前景小时 + 背景分钟重叠），
            // 分钟在底层、右对齐偏右半透明；小时覆盖其上。参考官方
            // OversizeBHourClock/OversizeBMinuteClock 布局。
            if (oversize) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = minute,
                        fontSize = hourSize * 1.5f,
                        fontWeight = fontWeight,
                        color = secondaryColor.copy(alpha = 0.55f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 2.dp)
                    )
                    Text(
                        text = hour,
                        fontSize = hourSize,
                        fontWeight = fontWeight,
                        color = primaryColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = hour,
                        fontSize = hourSize,
                        fontWeight = fontWeight,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = minute,
                        fontSize = minuteSize,
                        fontWeight = fontWeight,
                        color = secondaryColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            // 日期：oversize 模板官方在左下方，其它模板居中
            if (oversize) {
                Text(
                    text = date,
                    fontSize = 11.sp,
                    color = secondaryColor.copy(alpha = 0.95f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp)
                )
            } else {
                Text(
                    text = date,
                    fontSize = 11.sp,
                    color = secondaryColor.copy(alpha = 0.95f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            CustomizePill(onClick = onClick)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

/** 从官方锁屏 JSON 解析出的时钟样式（颜色按 ARGB int，可能为 null=用默认白）。 */
private data class ClockStyle(
    val templateId: String?,
    val primaryColor: Color?,
    val secondaryColor: Color?,
    val isAutoPrimaryColor: Boolean = true,
    val isAutoSecondaryColor: Boolean = true,
    val style: Int = 0,
    val clockWeight: Int = 0,
    val clockEffect: Int = 0
)

/**
 * 解析 Settings.Secure constant_lockscreen_info 的 JSON。
 * 结构：{"clockInfo":{"templateId":"classic","primaryColor":...,
 * "secondaryColor":...,"isAutoPrimaryColor":true,...}}
 * 颜色字段可能是 int(ARGB) 或 "#RRGGBB" 字符串，解析失败回退 null（白色）。
 */
private fun parseClockStyle(json: String?): ClockStyle {
    if (json.isNullOrEmpty()) return ClockStyle(null, null, null)
    return runCatching {
        val root = JSONObject(json)
        val clock = root.optJSONObject("clockInfo")
            ?: root.optJSONObject("lockscreenInfo")?.optJSONObject("clockInfo")
            ?: return@runCatching ClockStyle(null, null, null)
        ClockStyle(
            templateId = clock.optString("templateId").takeIf { it.isNotEmpty() },
            primaryColor = parseColor(clock, "primaryColor"),
            secondaryColor = parseColor(clock, "secondaryColor"),
            isAutoPrimaryColor = clock.optBoolean("isAutoPrimaryColor", true),
            isAutoSecondaryColor = clock.optBoolean("isAutoSecondaryColor", true),
            style = clock.optInt("style", 0),
            clockWeight = clock.optInt("clockWeight", 0),
            clockEffect = clock.optInt("clockEffect", 0)
        )
    }.getOrDefault(ClockStyle(null, null, null))
}

private fun parseColor(clock: JSONObject, key: String): Color? {
    if (!clock.has(key)) return null
    val v = clock.opt(key)
    // 0 表示未设置/无效颜色，回退 null（由调用方用白色）
    if (v is Int && v == 0) return null
    return when (v) {
        null -> null
        is Int -> runCatching { Color(v) }.getOrNull()
        is String -> runCatching {
            val s = v.removePrefix("#")
            when (s.length) {
                6 -> Color(0xFF000000.toInt() or s.toLong(16).toInt())
                8 -> Color(s.toLong(16).toInt())
                else -> null
            }
        }.getOrNull()
        else -> null
    }
}

/**
 * 桌面 mockup：官方个性化顶部桌面预览样式——壁纸背景 + 图标/组件占位网格，
 * 底部中央"自定义"胶囊按钮。
 */
@Composable
private fun HomeScreenMockup(
    image: Bitmap?,
    templateEditorJson: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var cardSize by remember { mutableStateOf(0 to 0) }
    // 优先用官方 HomeTemplateView 渲染桌面（真实壁纸 + 官方图标网格）；
    // 失败回退 Compose 复刻。
    val officialView = remember(image, templateEditorJson, cardSize.first, cardSize.second) {
        if (cardSize.first > 0 && cardSize.second > 0) {
            runCatching {
                OfficialTemplatePreview.createHomeContainer(
                    context, image, cardSize.first, cardSize.second, templateEditorJson
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
            AndroidView(
                factory = { officialView },
                modifier = Modifier.fillMaxSize()
            )
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
        } else {
            ComposeHomeScreenMockup(image = image, onClick = onClick)
        }
    }
}

/** Compose 复刻版桌面 mockup（官方组件加载失败时的回退）。 */
@Composable
private fun ComposeHomeScreenMockup(
    image: Bitmap?,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
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

/** 桌面 mockup 里的半透明浅蓝占位块（图标/组件，仿官方桌面预览）。 */
@Composable
private fun PlaceholderBlock(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF7EC8FF).copy(alpha = 0.35f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.35f),
                shape = RoundedCornerShape(6.dp)
            )
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
