package com.banana.hypermodes.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONObject

/**
 * 用官方组件直接渲染锁屏预览（“和设置个性化页一样”）。
 *
 * 原理：主题管家 / AOD APK 里直接打包了整套官方组件（com.miui.clock 的
 * MiuiClockView + ClockBean + 布局资源）。我们用 createPackageContext
 * 在 HyperModes 进程加载官方 ClassLoader + Resources，反射创建官方
 * MiuiClockView，把真实锁屏 JSON（constant_template_editor_info /
 * constant_lockscreen_info）解析出的 ClockBean 喂给它，让它渲染出和
 * 系统个性化页完全一致的时钟样式（模板布局、字体、颜色、位置全跟官方走）。
 *
 * 锁屏 mockup = 官方时钟视图 + 真实壁纸背景。桌面 mockup 走 HomeTemplateView
 * （官方桌面模板）或保留现有图标网格。
 *
 * 所有反射调用都包在 try/catch 里：加载失败或官方组件缺失时返回 null，
 * 由调用方回退到 Compose 复刻的 mockup，绝不影响 App 其它功能。
 */
object OfficialTemplatePreview {
    private const val TAG = "OfficialTemplatePreview"

    /** 优先 AOD（未混淆、方法名完整），回退主题管家（打包了全套组件）。 */
    private val SOURCE_PACKAGES = listOf(
        "com.miui.aod",
        "com.android.thememanager"
    )

    private const val CLOCK_VIEW_CLS = "com.miui.clock.MiuiClockView"
    private const val CLOCK_BEAN_CLS = "com.miui.clock.module.ClockBean"

    /**
     * 反射创建官方时钟视图并挂到 container 上。
     *
     * @param lockscreenJson 真实锁屏 JSON（constant_lockscreen_info /
     *  constant_template_editor_info 的 lockscreenInfo 均可）
     * @param wallpaper 真实锁屏壁纸位图（作为时钟背景；可为 null）
     * @return 成功返回包含官方时钟的容器；失败返回 null
     */
    fun createClockContainer(
        context: Context,
        lockscreenJson: String?,
        wallpaper: Bitmap?,
        subjectMaskPath: String?,
        targetWidthPx: Int,
        targetHeightPx: Int
    ): FrameLayout? {
        var lastError: Throwable? = null
        for (pkg in SOURCE_PACKAGES) {
            try {
                val host = createHost(context, pkg) ?: continue
                val container = buildClockContainer(
                    host, lockscreenJson, wallpaper, subjectMaskPath, targetWidthPx, targetHeightPx
                )
                if (container != null) {
                    Log.i(TAG, "official clock created from $pkg")
                    return container
                }
                lastError = IllegalStateException("buildClockContainer returned null from $pkg")
            } catch (t: Throwable) {
                lastError = t
                Log.w(TAG, "official clock failed from $pkg", t)
            }
        }
        if (lastError != null) {
            Log.w(TAG, "all official clock sources failed", lastError)
        }
        return null
    }

    /**
     * 用官方组件渲染桌面 mockup（HomeTemplateView）：真实壁纸 + 官方图标网格层，
     * 和锁屏的 MiuiClockView 一样，按真实屏幕尺寸创建 + scale 缩放到卡片。
     */
    fun createHomeContainer(
        context: Context,
        wallpaper: Bitmap?,
        targetWidthPx: Int,
        targetHeightPx: Int
    ): FrameLayout? {
        var lastError: Throwable? = null
        for (pkg in SOURCE_PACKAGES) {
            try {
                val host = createHost(context, pkg) ?: continue
                val container = buildHomeContainer(
                    host, wallpaper, targetWidthPx, targetHeightPx
                )
                if (container != null) {
                    Log.i(TAG, "official home created from $pkg")
                    return container
                }
                lastError = IllegalStateException("buildHomeContainer returned null from $pkg")
            } catch (t: Throwable) {
                lastError = t
                Log.w(TAG, "official home failed from $pkg", t)
            }
        }
        if (lastError != null) {
            Log.w(TAG, "all official home sources failed", lastError)
        }
        return null
    }

    /** 加载目标包的 ClassLoader + Resources + Context。 */
    private fun createHost(context: Context, pkg: String): Host? {
        return try {
            val target = context.createPackageContext(pkg, Context.CONTEXT_INCLUDE_CODE)
            val host = Host(target, target.classLoader)
            // 官方 BaseTemplateView 系列（含景深壁纸层 CombinedWallpaperView）的静态
            // 初始化 WallpaperEditor.<clinit> 会调 EditorApplicationProxy.getApplication()，
            // 第三方进程里 sInstance 为 null 会 NPE。sInstance 是 private static 字段，
            // 反射设为我们的 Application 即可绕过，让官方完整模板视图可以构造。
            initEditorApplicationProxy(host, context)
            host
        } catch (t: Throwable) {
            Log.w(TAG, "createPackageContext($pkg) failed", t)
            null
        }
    }

    /** 反射设置 EditorApplicationProxy.sInstance，绕过 AOD 进程单例依赖。 */
    private fun initEditorApplicationProxy(host: Host, context: Context) {
        runCatching {
            val proxyCls = host.classLoader.loadClass(
                "com.miui.keyguard.editor.EditorApplicationProxy"
            )
            val field = proxyCls.getDeclaredField("sInstance")
            field.isAccessible = true
            if (field.get(null) == null) {
                field.set(null, context.applicationContext)
                Log.i(TAG, "EditorApplicationProxy.sInstance initialized")
            }
        }.onFailure { t ->
            Log.w(TAG, "init EditorApplicationProxy.sInstance failed", t)
        }
    }

    private data class Host(
        val context: Context,
        val classLoader: ClassLoader
    )

    private fun buildClockContainer(
        host: Host,
        lockscreenJson: String?,
        wallpaper: Bitmap?,
        subjectMaskPath: String?,
        targetWidthPx: Int,
        targetHeightPx: Int
    ): FrameLayout? {
        // 1. 解析真实锁屏 JSON -> ClockBean 字段
        val fields = parseClockBeanFields(lockscreenJson) ?: return null

        // 2. 反射构造 ClockBean 并填充字段
        val beanCls = host.classLoader.loadClass(CLOCK_BEAN_CLS)
        val bean = beanCls.getConstructor(String::class.java).newInstance(fields.templateId ?: "classic")
        fillClockBean(beanCls, bean, fields)

        // 3. 反射创建官方 MiuiClockView。官方设置（SettingsMyTemplateViewHolder）
        //    是"按真实屏幕尺寸创建模板视图 + scaleX/scaleY 整体缩放到卡片"：
        //      layoutParams = 真实屏幕尺寸（1220x2656 之类）
        //      scaleX = 卡片宽 / 屏幕宽，scaleY = 卡片高 / 屏幕高
        //      并且 setPivotX(0) / setPivotY(0) 以左上角为原点缩放。
        //    这样时钟布局、字号、位置都按真机比例缩小，和系统个性化预览一致。
        val clockViewCls = host.classLoader.loadClass(CLOCK_VIEW_CLS)
        val clockView = clockViewCls.getConstructor(Context::class.java).newInstance(host.context) as View
        val screenW = host.context.resources.displayMetrics.widthPixels
        val screenH = host.context.resources.displayMetrics.heightPixels
        val targetScaleX = if (screenW > 0) targetWidthPx.toFloat() / screenW.toFloat() else 1f
        val targetScaleY = if (screenH > 0) targetHeightPx.toFloat() / screenH.toFloat() else 1f

        // 4. init(clockBean, false) -> build() -> 渲染官方时钟布局
        initClockView(clockViewCls, clockView, bean, beanCls)

        // 5. 组装：壁纸背景 + 官方时钟，套一个圆角裁切容器
        val root = FrameLayout(host.context)
        root.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        // 壁纸也按全屏尺寸 + 整体缩放（与官方预览一致：壁纸铺满真机屏幕再缩放）
        if (wallpaper != null) {
            val bg = ImageView(host.context).apply {
                setImageBitmap(wallpaper)
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = FrameLayout.LayoutParams(screenW, screenH)
                scaleX = targetScaleX
                scaleY = targetScaleY
                pivotX = 0f
                pivotY = 0f
            }
            root.addView(bg)
        } else {
            // 壁纸缺失时用深色渐变兜底（官方时钟 isAutoPrimaryColor=true
            // 时文字是白色，深色背景才能看见），与 Compose 复刻版一致。
            val bg = View(host.context).apply {
                background = GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    intArrayOf(0xFF2B3A52.toInt(), 0xFF1B2436.toInt())
                )
                layoutParams = FrameLayout.LayoutParams(screenW, screenH)
                scaleX = targetScaleX
                scaleY = targetScaleY
                pivotX = 0f
                pivotY = 0f
            }
            root.addView(bg)
        }
        // 官方时钟：全屏尺寸 + 左上角缩放，和 createScaledPreviewTemplateView 一致
        addScaledClockView(root, clockView, screenW, screenH, targetScaleX, targetScaleY)

        // 6. 景深效果：壁纸主体前景层。官方渲染层级为
        //    "背景壁纸 → 分钟层（oversize_b，被主体遮挡）→ 景深主体 → 小时层
        //    （oversize_b_hour，在主体之上、不被遮挡）"。
        //    所以主体层必须插在分钟层和小时层之间。
        if (wallpaper != null && !subjectMaskPath.isNullOrEmpty()) {
            runCatching {
                val mask = BitmapFactory.decodeFile(subjectMaskPath) ?: return@runCatching
                val foreground = composeSubjectForeground(wallpaper, mask)
                mask.recycle()
                if (foreground != null) {
                    val fg = ImageView(host.context).apply {
                        setImageBitmap(foreground)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        layoutParams = FrameLayout.LayoutParams(screenW, screenH)
                        scaleX = targetScaleX
                        scaleY = targetScaleY
                        pivotX = 0f
                        pivotY = 0f
                    }
                    root.addView(fg)
                    Log.i(TAG, "depth foreground layer added (below hour layer)")
                }
            }.onFailure { t ->
                Log.w(TAG, "depth foreground layer failed", t)
            }
        }

        // 7. oversize_* 是双层时钟（官方 OversizeBTemplateView）：
        //    背景层 = templateId（如 oversize_b，分钟层），
        //    前景层 = templateId + "_hour"（如 oversize_b_hour，小时层），
        //    前景覆盖在背景+景深主体之上（小时不被主体遮挡）。缺失小时层会导致只显示分钟。
        val templateId = fields.templateId
        if (templateId != null && templateId.startsWith("oversize")) {
            runCatching {
                val foreTemplate = templateId + "_hour"
                val foreBean = beanCls.getConstructor(String::class.java).newInstance(foreTemplate)
                // 前景小时层复用主 bean 的颜色/字重字段（官方 initTemplateBean 同样复制）
                fillClockBean(beanCls, foreBean, fields)
                val foreView = clockViewCls.getConstructor(Context::class.java).newInstance(host.context) as View
                initClockView(clockViewCls, foreView, foreBean, beanCls)
                addScaledClockView(root, foreView, screenW, screenH, targetScaleX, targetScaleY)
                Log.i(TAG, "oversize fore clock layer added: $foreTemplate")
            }.onFailure { t ->
                Log.w(TAG, "oversize fore clock layer failed", t)
            }
        }
        return root
    }

    /**
     * 用 subject_mask 把壁纸主体合成到透明前景：mask 白色区域显示壁纸像素，
     * 黑色区域透明。这样叠加在时钟上时，主体会遮挡时钟（官方景深效果）。
     */
    private fun composeSubjectForeground(
        wallpaper: Bitmap,
        mask: Bitmap
    ): Bitmap? {
        val w = wallpaper.width
        val h = wallpaper.height
        // 先解码蒙版为灰度像素
        val grayMask = if (mask.width == w && mask.height == h) {
            mask
        } else {
            Bitmap.createScaledBitmap(mask, w, h, true)
        }
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        // 手动合成：前景 = 壁纸像素，alpha = 蒙版亮度（白色主体不透明、黑色背景透明）
        val wallpaperPixels = IntArray(w)
        val maskPixels = IntArray(w)
        val outPixels = IntArray(w)
        for (y in 0 until h) {
            wallpaper.getPixels(wallpaperPixels, 0, w, 0, y, w, 1)
            grayMask.getPixels(maskPixels, 0, w, 0, y, w, 1)
            for (x in 0 until w) {
                val m = maskPixels[x]
                val lum = ((m shr 16 and 0xFF) + (m shr 8 and 0xFF) + (m and 0xFF)) / 3
                // 轻微软化边缘：蒙版亮度直接作为 alpha
                val alpha = lum
                val wp = wallpaperPixels[x]
                outPixels[x] = (alpha shl 24) or (wp and 0x00FFFFFF)
            }
            out.setPixels(outPixels, 0, w, 0, y, w, 1)
        }
        if (grayMask !== mask) grayMask.recycle()
        return out
    }

    /** 填充 ClockBean 字段（颜色/字重/特效），主层与前景层共用。 */
    private fun fillClockBean(
        beanCls: Class<*>,
        bean: Any,
        fields: ClockBeanFields
    ) {
        fun setInt(name: String, value: Int) {
            runCatching { beanCls.getMethod(name, Int::class.javaPrimitiveType).invoke(bean, value) }
        }
        fun setBool(name: String, value: Boolean) {
            runCatching { beanCls.getMethod(name, Boolean::class.javaPrimitiveType).invoke(bean, value) }
        }
        setInt("setPrimaryColor", fields.primaryColor)
        setInt("setSecondaryColor", fields.secondaryColor)
        setInt("setStyle", fields.style)
        setInt("setClockWeight", fields.clockWeight)
        setInt("setClockEffect", fields.clockEffect)
        setBool("setAutoPrimaryColor", fields.isAutoPrimaryColor)
        setBool("setAutoSecondaryColor", fields.isAutoSecondaryColor)
        setBool("setDiffHourMinuteColor", fields.isDiffHourMinuteColor)
        setBool("setEnableDiffusion", fields.enableDiffusion)
    }

    /** 反射调用 MiuiClockView.init(clockBean, displayType, async)。 */
    private fun initClockView(
        clockViewCls: Class<*>,
        clockView: View,
        bean: Any,
        beanCls: Class<*>
    ) {
        val initMethod = clockViewCls.getMethod(
            "init",
            beanCls,
            Int::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType
        )
        initMethod.invoke(clockView, bean, 0, false)
    }

    /** 以全屏尺寸 + 左上角缩放挂到 root（与 createScaledPreviewTemplateView 一致）。 */
    private fun addScaledClockView(
        root: FrameLayout,
        clockView: View,
        screenW: Int,
        screenH: Int,
        scaleX: Float,
        scaleY: Float
    ) {
        clockView.layoutParams = FrameLayout.LayoutParams(screenW, screenH)
        clockView.scaleX = scaleX
        clockView.scaleY = scaleY
        clockView.pivotX = 0f
        clockView.pivotY = 0f
        root.addView(clockView)
    }

    private fun buildHomeContainer(
        host: Host,
        wallpaper: Bitmap?,
        targetWidthPx: Int,
        targetHeightPx: Int
    ): FrameLayout? {
        val screenW = host.context.resources.displayMetrics.widthPixels
        val screenH = host.context.resources.displayMetrics.heightPixels
        val targetScaleX = if (screenW > 0) targetWidthPx.toFloat() / screenW.toFloat() else 1f
        val targetScaleY = if (screenH > 0) targetHeightPx.toFloat() / screenH.toFloat() else 1f

        // 1. 组装：真实壁纸 + 官方桌面图标网格层（kg_miui_home_desktop_*）。
        //    官方 HomeTemplateView 的父类静态初始化依赖 AOD 进程单例
        //    （EditorApplicationProxy.getApplication），第三方进程加载会崩，
        //    所以直接用官方图标网格资源叠加在真实壁纸上，效果等价且零单例依赖。
        val root = FrameLayout(host.context)
        root.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        if (wallpaper != null) {
            val bg = ImageView(host.context).apply {
                setImageBitmap(wallpaper)
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = FrameLayout.LayoutParams(screenW, screenH)
                scaleX = targetScaleX
                scaleY = targetScaleY
                pivotX = 0f
                pivotY = 0f
            }
            root.addView(bg)
        } else {
            val bg = View(host.context).apply {
                background = GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    intArrayOf(0xFF2B3A52.toInt(), 0xFF1B2436.toInt())
                )
                layoutParams = FrameLayout.LayoutParams(screenW, screenH)
                scaleX = targetScaleX
                scaleY = targetScaleY
                pivotX = 0f
                pivotY = 0f
            }
            root.addView(bg)
        }
        // 官方桌面图标网格：深色壁纸用白色图标，浅色壁纸用黑色图标
        val iconRes = resolveHomeDesktopDrawable(host, wallpaper)
        if (iconRes != 0) {
            val iconLayer = ImageView(host.context).apply {
                setImageResource(iconRes)
                scaleType = ImageView.ScaleType.FIT_XY
                layoutParams = FrameLayout.LayoutParams(screenW, screenH)
                scaleX = targetScaleX
                scaleY = targetScaleY
                pivotX = 0f
                pivotY = 0f
            }
            root.addView(iconLayer)
        }
        return root
    }

    /** 解析官方桌面图标网格 drawable 资源 id（白色=深色壁纸，黑色=浅色壁纸）。 */
    private fun resolveHomeDesktopDrawable(
        host: Host,
        wallpaper: Bitmap?
    ): Int {
        // 深色壁纸用白色图标（kg_miui_home_desktop_white），浅色壁纸用黑色图标。
        // 官方判断：CommonUtils.isDarkWallpaper = MiuiWallpaperColors
        //   .generateWallpaperColors(bitmap).getColorHints() & 1 == 0
        val dark = wallpaper == null || isDarkByOfficialHints(host, wallpaper)
        val name = if (dark) "kg_miui_home_desktop_white" else "kg_miui_home_desktop_black"
        return host.context.resources.getIdentifier(
            name,
            "drawable",
            host.context.packageName
        )
    }

    /**
     * 用官方 MiuiWallpaperColors 判断深色壁纸（与 CommonUtils.isDarkWallpaper
     * 完全一致）：generateWallpaperColors(bitmap).getColorHints() & 1 == 0。
     * 反射调用，失败回退平均亮度判断。
     */
    private fun isDarkByOfficialHints(host: Host, bmp: Bitmap): Boolean {
        return runCatching {
            val colorsCls = host.classLoader.loadClass(
                "com.miui.miwallpaper.material.utils.MiuiWallpaperColors"
            )
            val method = colorsCls.getMethod("generateWallpaperColors", Bitmap::class.java)
            val colors = method.invoke(null, bmp)
            val hints = colors.javaClass.getMethod("getColorHints").invoke(colors) as Int
            (hints and 1) == 0
        }.getOrElse {
            // 回退：平均亮度低视为深色
            var sum = 0L
            var count = 0
            val step = 8
            for (y in 0 until bmp.height step step) {
                for (x in 0 until bmp.width step step) {
                    val c = bmp.getPixel(x, y)
                    val r = (c shr 16) and 0xFF
                    val g = (c shr 8) and 0xFF
                    val b = c and 0xFF
                    sum += (r + g + b) / 3
                    count++
                }
            }
            count == 0 || sum / count < 128
        }
    }

    /** 从真实锁屏 JSON 提取 ClockBean 需要的字段（字段名与官方 ClockBean 一致）。 */
    fun parseClockBeanFields(json: String?): ClockBeanFields? {
        if (json.isNullOrEmpty()) return null
        return runCatching {
            val root = JSONObject(json)
            val clock = root.optJSONObject("clockInfo")
                ?: root.optJSONObject("lockscreenInfo")?.optJSONObject("clockInfo")
                ?: return null
            ClockBeanFields(
                templateId = clock.optString("templateId").takeIf { it.isNotEmpty() },
                primaryColor = clock.optInt("primaryColor", 0),
                secondaryColor = clock.optInt("secondaryColor", 0),
                isAutoPrimaryColor = clock.optBoolean("isAutoPrimaryColor", true),
                isAutoSecondaryColor = clock.optBoolean("isAutoSecondaryColor", true),
                isDiffHourMinuteColor = clock.optBoolean("isDiffHourMinuteColor", false),
                enableDiffusion = clock.optBoolean("enableDiffusion", false),
                style = clock.optInt("style", 0),
                clockWeight = clock.optInt("clockWeight", 0),
                clockEffect = clock.optInt("clockEffect", 0)
            )
        }.getOrNull()
    }

    data class ClockBeanFields(
        val templateId: String?,
        val primaryColor: Int,
        val secondaryColor: Int,
        val isAutoPrimaryColor: Boolean,
        val isAutoSecondaryColor: Boolean,
        val isDiffHourMinuteColor: Boolean,
        val enableDiffusion: Boolean,
        val style: Int,
        val clockWeight: Int,
        val clockEffect: Int
    )
}

/**
 * Compose 宿主：尝试用官方组件渲染锁屏预览。
 *
 * @param lockscreenJson 真实锁屏 JSON；为 null 时不加载官方时钟
 * @param wallpaper 真实锁屏壁纸位图
 * @param onLoadResult null=官方组件加载失败（调用方回退 Compose mockup）
 */
@Composable
fun OfficialLockPreview(
    lockscreenJson: String?,
    wallpaper: Bitmap?,
    subjectMaskPath: String?,
    targetWidthPx: Int,
    targetHeightPx: Int,
    onLoadResult: (View?) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val view = OfficialTemplatePreview.createClockContainer(
                ctx,
                lockscreenJson,
                wallpaper,
                subjectMaskPath,
                targetWidthPx,
                targetHeightPx
            )
            onLoadResult(view)
            view ?: FrameLayout(ctx)
        }
    )
}
