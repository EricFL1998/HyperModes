package com.banana.hypermodes.ui.components

import android.content.Context
import android.graphics.Bitmap
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
    private const val TEMPLATE_FACTORY_CLS = "com.miui.keyguard.editor.base.TemplateViewFactory"
    private const val HOME_TEMPLATE_VIEW_CLS = "com.miui.keyguard.editor.edit.base.HomeTemplateView"
    private const val COMMON_CONFIG_CLS = "com.miui.keyguard.editor.data.bean.CommonConfig"
    private const val TEMPLATE_CONFIG_CLS = "com.miui.keyguard.editor.data.bean.TemplateConfig"
    private const val HOME_CONFIG_CLS = "com.miui.keyguard.editor.data.bean.HomeConfig"
    private const val WALLPAPER_INFO_CLS = "com.miui.keyguard.editor.data.bean.WallpaperInfo"

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
        targetWidthPx: Int,
        targetHeightPx: Int
    ): FrameLayout? {
        var lastError: Throwable? = null
        for (pkg in SOURCE_PACKAGES) {
            try {
                val host = createHost(context, pkg) ?: continue
                val container = buildClockContainer(
                    host, lockscreenJson, wallpaper, targetWidthPx, targetHeightPx
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
     *
     * @param templateEditorJson constant_template_editor_info 完整 JSON
     *  （含 homeInfo），用于构造官方 CommonConfig；为 null 时用最小空配置
     */
    fun createHomeContainer(
        context: Context,
        wallpaper: Bitmap?,
        targetWidthPx: Int,
        targetHeightPx: Int,
        templateEditorJson: String?
    ): FrameLayout? {
        var lastError: Throwable? = null
        for (pkg in SOURCE_PACKAGES) {
            try {
                val host = createHost(context, pkg) ?: continue
                val container = buildHomeContainer(
                    host, wallpaper, targetWidthPx, targetHeightPx, templateEditorJson
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
    private fun createHost(context: Context, pkg: String): Host? = try {
        val target = context.createPackageContext(pkg, Context.CONTEXT_INCLUDE_CODE)
        Host(target, target.classLoader)
    } catch (t: Throwable) {
        Log.w(TAG, "createPackageContext($pkg) failed", t)
        null
    }

    private data class Host(
        val context: Context,
        val classLoader: ClassLoader
    )

    private fun buildClockContainer(
        host: Host,
        lockscreenJson: String?,
        wallpaper: Bitmap?,
        targetWidthPx: Int,
        targetHeightPx: Int
    ): FrameLayout? {
        // 1. 解析真实锁屏 JSON -> ClockBean 字段
        val fields = parseClockBeanFields(lockscreenJson) ?: return null

        // 2. 反射构造 ClockBean 并填充字段
        val beanCls = host.classLoader.loadClass(CLOCK_BEAN_CLS)
        val bean = beanCls.getConstructor(String::class.java).newInstance(fields.templateId ?: "classic")
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
        val initMethod = clockViewCls.getMethod(
            "init",
            beanCls,
            Int::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType
        )
        initMethod.invoke(clockView, bean, 0, false)

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
        clockView.layoutParams = FrameLayout.LayoutParams(screenW, screenH)
        clockView.scaleX = targetScaleX
        clockView.scaleY = targetScaleY
        clockView.pivotX = 0f
        clockView.pivotY = 0f
        root.addView(clockView)
        return root
    }

    private fun buildHomeContainer(
        host: Host,
        wallpaper: Bitmap?,
        targetWidthPx: Int,
        targetHeightPx: Int,
        templateEditorJson: String?
    ): FrameLayout? {
        val screenW = host.context.resources.displayMetrics.widthPixels
        val screenH = host.context.resources.displayMetrics.heightPixels
        val targetScaleX = if (screenW > 0) targetWidthPx.toFloat() / screenW.toFloat() else 1f
        val targetScaleY = if (screenH > 0) targetHeightPx.toFloat() / screenH.toFloat() else 1f

        // 1. 构造官方 CommonConfig（优先从真实 JSON 反序列化，失败用空配置）
        val commonConfig = buildCommonConfig(host, templateEditorJson) ?: return null

        // 2. 反射创建官方 HomeTemplateView，按官方设置预览方式缩放
        val homeView = createScaledHomeTemplateView(host, screenW, screenH, targetScaleX, targetScaleY)
            ?: return null

        // 3. loadTemplate(CommonConfig) 渲染真实壁纸层 + 图标网格层
        val loadMethod = runCatching {
            homeView.javaClass.getMethod("loadTemplate", commonConfig.javaClass)
        }.getOrNull()
        if (loadMethod != null) {
            loadMethod.invoke(homeView, commonConfig)
        }

        // 4. 组装：真实壁纸（兜底）+ 官方桌面模板
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
        homeView.layoutParams = FrameLayout.LayoutParams(screenW, screenH)
        root.addView(homeView)
        return root
    }

    /** 反射调用官方 TemplateViewFactory.createScaledPreviewHomeTemplateView。 */
    private fun createScaledHomeTemplateView(
        host: Host,
        screenW: Int,
        screenH: Int,
        scaleX: Float,
        scaleY: Float
    ): View? {
        val factoryCls = host.classLoader.loadClass(TEMPLATE_FACTORY_CLS)
        val factory = factoryCls.getField("INSTANCE").get(null)
        val lp = android.widget.FrameLayout.LayoutParams(screenW, screenH)
        val method = factoryCls.getMethod(
            "createScaledPreviewHomeTemplateView",
            Context::class.java,
            android.widget.FrameLayout.LayoutParams::class.java,
            Float::class.javaPrimitiveType,
            Float::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType
        )
        return method.invoke(factory, host.context, lp, scaleX, scaleY, false) as? View
    }

    /**
     * 构造官方 CommonConfig。优先用官方 Gson 从 constant_template_editor_info
     * 反序列化（含 lockscreenInfo + homeInfo）；失败时构造最小空配置。
     */
    private fun buildCommonConfig(host: Host, templateEditorJson: String?): Any? {
        // 方式一：官方 CommonConfig.Companion.fromJson(json, fallback)
        if (!templateEditorJson.isNullOrEmpty()) {
            val fromJson = runCatching {
                val configCls = host.classLoader.loadClass(COMMON_CONFIG_CLS)
                val companion = configCls.getField("Companion").get(null)
                val method = companion.javaClass.getMethod(
                    "fromJson",
                    String::class.java,
                    String::class.java
                )
                method.invoke(companion, templateEditorJson, null)
            }.getOrNull()
            if (fromJson != null) return fromJson
        }
        // 方式二：最小空配置 new CommonConfig(null, 0, 3, new TemplateConfig(), new HomeConfig(...))
        return runCatching {
            val configCls = host.classLoader.loadClass(COMMON_CONFIG_CLS)
            val templateConfigCls = host.classLoader.loadClass(TEMPLATE_CONFIG_CLS)
            val homeConfigCls = host.classLoader.loadClass(HOME_CONFIG_CLS)
            val wallpaperInfoCls = host.classLoader.loadClass(WALLPAPER_INFO_CLS)

            val wallpaperInfo = wallpaperInfoCls.getConstructor().newInstance()
            val homeConfig = homeConfigCls.getConstructor(
                Int::class.javaPrimitiveType,
                wallpaperInfoCls,
                wallpaperInfoCls,
                Int::class.javaPrimitiveType
            ).newInstance(0, wallpaperInfo, null, 0)
            val lockscreenInfo = templateConfigCls.getConstructor().newInstance()
            configCls.getConstructor(
                String::class.java,
                Long::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                templateConfigCls,
                homeConfigCls
            ).newInstance(null, 0L, 3, lockscreenInfo, homeConfig)
        }.getOrNull()
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
    targetWidthPx: Int,
    targetHeightPx: Int,
    onLoadResult: (View?) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val view = OfficialTemplatePreview.createClockContainer(
                ctx, lockscreenJson, wallpaper, targetWidthPx, targetHeightPx
            )
            onLoadResult(view)
            view ?: FrameLayout(ctx)
        }
    )
}
