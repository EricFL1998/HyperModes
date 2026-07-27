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
        fun resolve(
            pluginClassLoader: ClassLoader,
            systemUiClassLoader: ClassLoader = pluginClassLoader,
            onNativeDetailFailure: (Throwable) -> Unit = {}
        ): FocusCardTileClasses {
            val tile = loadClass(pluginClassLoader, "com.android.systemui.plugins.qs.QSTile")
            val state = loadClass(pluginClassLoader, "com.android.systemui.plugins.qs.QSTile\$BooleanState")
            val icon = loadClass(pluginClassLoader, "miui.systemui.controlcenter.qs.DrawableIcon")
            val detail = loadClass(pluginClassLoader, "com.android.systemui.plugins.qs.DetailAdapter")
            val nativeDetailContentApi = FocusNativeDetailContentResolver.fromClassLoader(
                systemUiClassLoader,
                onNativeDetailFailure
            )

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
