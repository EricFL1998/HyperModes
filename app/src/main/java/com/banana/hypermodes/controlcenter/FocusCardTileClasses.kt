package com.banana.hypermodes.controlcenter

import android.graphics.drawable.Drawable

data class FocusCardTileClasses(
    val tileInterface: Class<*>,
    val booleanStateClass: Class<*>,
    val drawableIconClass: Class<*>,
    val detailAdapterInterface: Class<*>,
    val nativeDetailContentApi: FocusNativeDetailContentApi
) {
    companion object {
        fun resolve(
            systemUiClassLoader: ClassLoader,
            onNativeDetailFailure: (Throwable) -> Unit = {}
        ): FocusCardTileClasses {
            val tile = loadClass(systemUiClassLoader, "com.android.systemui.plugins.qs.QSTile")
            val state = loadClass(systemUiClassLoader, "com.android.systemui.plugins.qs.QSTile\$BooleanState")
            val icon = loadClass(systemUiClassLoader, "com.android.systemui.qs.tileimpl.QSTileImpl\$DrawableIcon")
            val detail = loadClass(systemUiClassLoader, "com.android.systemui.plugins.qs.DetailAdapter")
            val nativeDetailContentApi = FocusNativeDetailContentResolver.fromClassLoader(
                systemUiClassLoader,
                onNativeDetailFailure
            ) ?: throw IllegalStateException("OS4 QSDetailContent API is unavailable")

            try {
                icon.getDeclaredConstructor(Drawable::class.java)
            } catch (e: NoSuchMethodException) {
                throw IllegalStateException(
                    "Missing constructor com.android.systemui.qs.tileimpl.QSTileImpl\$DrawableIcon(android.graphics.drawable.Drawable)",
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
