package com.banana.hypermodes.controlcenter

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import com.banana.hypermodes.R
import com.banana.hypermodes.systemserver.config.DisplayConfig
import com.banana.hypermodes.systemserver.config.DndLevel
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import com.banana.hypermodes.systemserver.config.NotificationConfig
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusModeIconResolverTest {

    @Test
    fun `resolvable status icon wins over mapped mode icon`() {
        val statusDrawable = ColorDrawable(0x11)
        val mappedDrawable = ColorDrawable(0x22)
        val moduleContext = DrawableContext(
            packageNameValue = "module.package",
            identifiers = mapOf(
                "ic_stat_work" to STATUS_ICON_ID,
                "ic_stat_game" to MAPPED_ICON_ID
            ),
            drawables = mapOf(
                STATUS_ICON_ID to statusDrawable,
                MAPPED_ICON_ID to mappedDrawable
            )
        )
        val resolver = FocusModeIconResolver(moduleContext)

        val drawable = resolver.resolve(mode(icon = "🎮", statusIcon = "ic_stat_work"))

        assertSame(statusDrawable, drawable)
    }

    @Test
    fun `blank status icon uses mapped mode icon`() {
        val mappedDrawable = ColorDrawable(0x33)
        val moduleContext = DrawableContext(
            packageNameValue = "module.package",
            identifiers = mapOf("ic_stat_game" to MAPPED_ICON_ID),
            drawables = mapOf(MAPPED_ICON_ID to mappedDrawable)
        )
        val resolver = FocusModeIconResolver(moduleContext)

        val drawable = resolver.resolve(mode(icon = "🎮", statusIcon = "   "))

        assertSame(mappedDrawable, drawable)
    }

    @Test
    fun `missing status icon falls back to mapped mode icon`() {
        val mappedDrawable = ColorDrawable(0x44)
        val moduleContext = DrawableContext(
            packageNameValue = "module.package",
            identifiers = mapOf(
                "ic_stat_missing" to STATUS_ICON_ID,
                "ic_stat_game" to MAPPED_ICON_ID
            ),
            drawables = mapOf(MAPPED_ICON_ID to mappedDrawable)
        )
        val resolver = FocusModeIconResolver(moduleContext)

        val drawable = resolver.resolve(mode(icon = "🎮", statusIcon = "ic_stat_missing"))

        assertSame(mappedDrawable, drawable)
    }

    @Test
    fun `missing mapped icon falls back to module zen drawable`() {
        val zenDrawable = ColorDrawable(0x55)
        val moduleContext = DrawableContext(
            packageNameValue = "module.package",
            identifiers = mapOf("ic_stat_work" to MAPPED_ICON_ID),
            drawables = mapOf(R.drawable.ic_stat_zen to zenDrawable)
        )
        val resolver = FocusModeIconResolver(moduleContext)

        val drawable = resolver.resolve(mode(icon = "💼", statusIcon = null))

        assertSame(zenDrawable, drawable)
    }

    @Test
    fun `unknown resources return non null transparent drawable fallback`() {
        val resolver = FocusModeIconResolver(DrawableContext("module.package"))

        val drawable = resolver.resolve(mode(icon = "💼", statusIcon = "missing_status_icon"))

        assertNotNull(drawable)
        assertTrue(drawable is ColorDrawable)
    }

    private fun mode(icon: String, statusIcon: String?): ModeConfig {
        return ModeConfig(
            id = "mode-id",
            name = "Mode",
            icon = icon,
            statusIcon = statusIcon,
            type = ModeType.SCHEDULED,
            notification = NotificationConfig(DndLevel.PRIORITY),
            display = DisplayConfig(),
            pausedApps = emptyList()
        )
    }

    private class DrawableContext(
        private val packageNameValue: String,
        identifiers: Map<String, Int> = emptyMap(),
        drawables: Map<Int, Drawable> = emptyMap()
    ) : ContextWrapper(null) {
        private val resources = DrawableResources(identifiers, drawables)

        override fun getApplicationContext(): Context = this
        override fun getPackageName(): String = packageNameValue
        override fun getResources(): Resources = resources
    }

    @Suppress("DEPRECATION")
    private class DrawableResources(
        private val identifiers: Map<String, Int>,
        private val drawables: Map<Int, Drawable>
    ) : Resources(newAssetManager(), DisplayMetrics().apply { density = 1f }, Configuration()) {
        override fun getIdentifier(name: String?, defType: String?, defPackage: String?): Int {
            return if (defType == "drawable") identifiers[name] ?: 0 else 0
        }

        override fun getDrawable(id: Int, theme: Theme?): Drawable {
            return drawables[id] ?: throw NotFoundException("drawable $id")
        }

        companion object {
            private fun newAssetManager(): AssetManager {
                val constructor = AssetManager::class.java.getDeclaredConstructor()
                constructor.isAccessible = true
                return constructor.newInstance()
            }
        }
    }

    private companion object {
        const val STATUS_ICON_ID = 1001
        const val MAPPED_ICON_ID = 1002
    }
}
