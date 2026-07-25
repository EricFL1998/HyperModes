package com.banana.hypermodes.controlcenter

import android.graphics.drawable.Drawable

data class FocusCardTileClasses(
    val tileInterface: Class<*>,
    val booleanStateClass: Class<*>,
    val drawableIconClass: Class<*>,
    val detailAdapterInterface: Class<*>,
    val nativeDetailContentApi: FocusNativeDetailContentApi? = null
) {
    companion object {
        fun resolve(classLoader: ClassLoader): FocusCardTileClasses {
            val tile = loadClass(classLoader, "com.android.systemui.plugins.qs.QSTile")
            val state = loadClass(classLoader, "com.android.systemui.plugins.qs.QSTile\$BooleanState")
            val icon = loadClass(classLoader, "miui.systemui.controlcenter.qs.DrawableIcon")
            val detail = loadClass(classLoader, "com.android.systemui.plugins.qs.DetailAdapter")
            val nativeDetailContentApi = FocusNativeDetailContentResolver.fromClassLoader(classLoader)

            try {
                icon.getDeclaredConstructor(Drawable::class.java)
            } catch (e: NoSuchMethodException) {
                throw IllegalStateException(
                    "Missing constructor miui.systemui.controlcenter.qs.DrawableIcon(android.graphics.drawable.Drawable)",
                    e
                )
            }

            return FocusCardTileClasses(
                tileInterface = tile,
                booleanStateClass = state,
                drawableIconClass = icon,
                detailAdapterInterface = detail,
                nativeDetailContentApi = nativeDetailContentApi
            )
        }

        private fun loadClass(classLoader: ClassLoader, name: String): Class<*> {
            return try {
                classLoader.loadClass(name)
            } catch (e: ClassNotFoundException) {
                throw IllegalStateException("Missing class $name", e)
            }
        }
    }
}
