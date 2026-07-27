package com.banana.hypermodes.hook

import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.net.URLClassLoader
import java.nio.file.Files
import javax.tools.ToolProvider

class ControlCenterNativeDetailFeatureSetTest {

    @Test
    fun `bound row resolver reads holder itemView field`() {
        val row = View(null)
        val holder = PublicItemViewHolder(row)

        val result = ControlCenterCardHook.resolveBoundItemView(holder)

        assertSame(row, result)
    }

    @Test
    fun `bound row resolver falls back to getItemView method`() {
        val row = View(null)
        val holder = MethodItemViewHolder(row)

        val result = ControlCenterCardHook.resolveBoundItemView(holder)

        assertSame(row, result)
    }

    @Test
    fun `bound row resolver ignores non view holders`() {
        val holder = PublicItemViewHolder("not a view")

        val result = ControlCenterCardHook.resolveBoundItemView(holder)

        assertNull(result)
    }

    @Test
    fun `native detail feature set resolves SystemUI content and plugin routing classes from separate loaders`() {
        val fixture = SplitNativeDetailFixture.create()

        val featureSet = ControlCenterCardHook.validatedNativeDetailFeatureSet(
            pluginClassLoader = fixture.pluginClassLoader,
            systemUiClassLoader = fixture.systemUiClassLoader
        )

        assertNotNull(featureSet)
        assertEquals(
            "com.android.systemui.qs.QSDetailContent\$Adapter#getItemCount",
            "${featureSet?.getItemCountMethod?.declaringClass?.name}#${featureSet?.getItemCountMethod?.name}"
        )
        assertEquals(
            "miui.systemui.controlcenter.panel.secondary.SecondaryParamsKt#from",
            "${featureSet?.secondaryParamsFromMethod?.declaringClass?.name}#${featureSet?.secondaryParamsFromMethod?.name}"
        )
        assertEquals(String::class.java, featureSet?.secondaryParamsFromMethod?.returnType)
        assertEquals(
            "miui.systemui.controlcenter.panel.secondary.DetailPanelParams#getUseSpecificHeight",
            "${featureSet?.getUseSpecificHeightMethod?.declaringClass?.name}#${featureSet?.getUseSpecificHeightMethod?.name}"
        )
        assertEquals(
            "miui.systemui.controlcenter.panel.secondary.detail.DetailPanelDelegate#onHidden",
            "${featureSet?.onHiddenMethod?.declaringClass?.name}#${featureSet?.onHiddenMethod?.name}"
        )
        assertEquals(
            "com.android.systemui.qs.QSDetailContent${'$'}Adapter#onBindViewHolder",
            "${featureSet?.onBindViewHolderMethod?.declaringClass?.name}#${featureSet?.onBindViewHolderMethod?.name}"
        )
        assertEquals(3, featureSet?.onBindViewHolderMethod?.parameterTypes?.size)
        assertEquals(
            "androidx.recyclerview.widget.RecyclerView${'$'}ViewHolder",
            featureSet?.onBindViewHolderMethod?.parameterTypes?.getOrNull(0)?.name
        )
        assertEquals(Int::class.javaPrimitiveType, featureSet?.onBindViewHolderMethod?.parameterTypes?.getOrNull(1))
        assertEquals(
            List::class.java,
            featureSet?.onBindViewHolderMethod?.parameterTypes?.getOrNull(2)
        )
    }

    private class PublicItemViewHolder(@JvmField val itemView: Any?)

    private class MethodItemViewHolder(private val row: View) {
        fun getItemView(): View = row
    }

    private data class SplitNativeDetailFixture(
        val systemUiClassLoader: ClassLoader,
        val pluginClassLoader: ClassLoader
    ) {
        companion object {
            fun create(): SplitNativeDetailFixture {
                val systemSource = Files.createTempDirectory("focus-native-system-src")
                val systemOutput = Files.createTempDirectory("focus-native-system-out")
                writeSource(
                    systemSource,
                    "com/android/systemui/plugins/qs/DetailAdapter.java",
                    """
                    package com.android.systemui.plugins.qs;
                    public interface DetailAdapter {}
                    """.trimIndent()
                )
                writeSource(
                    systemSource,
                    "com/android/systemui/qs/QSDetailContent.java",
                    """
                    package com.android.systemui.qs;
                    public class QSDetailContent {}
                    """.trimIndent()
                )
                writeSource(
                    systemSource,
                    "androidx/recyclerview/widget/RecyclerView.java",
                    """
                    package androidx.recyclerview.widget;
                    public class RecyclerView {
                        public static class ViewHolder {
                            public Object itemView;
                        }
                    }
                    """.trimIndent()
                )
                writeSource(
                    systemSource,
                    "com/android/systemui/qs/QSDetailContent\$ItemHolder.java",
                    """
                    package com.android.systemui.qs;
                    public class QSDetailContent${'$'}ItemHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
                        public QSDetailContent${'$'}ItemHolder() {}
                    }
                    """.trimIndent()
                )
                writeSource(
                    systemSource,
                    "com/android/systemui/qs/QSDetailContent\$Adapter.java",
                    """
                    package com.android.systemui.qs;
                    public class QSDetailContent${'$'}Adapter {
                        public int getItemCount() { return 20; }
                        public void onBindViewHolder(QSDetailContent${'$'}ItemHolder holder, int position) {}
                        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position) {}
                        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position, java.util.List payloads) {}
                    }
                    """.trimIndent()
                )
                compileJava(systemSource, systemOutput)

                val systemLoader = URLClassLoader(arrayOf(systemOutput.toUri().toURL()), null)
                val pluginSource = Files.createTempDirectory("focus-native-plugin-src")
                val pluginOutput = Files.createTempDirectory("focus-native-plugin-out")
                writeSource(
                    pluginSource,
                    "miui/systemui/controlcenter/panel/secondary/SecondaryParamsKt.java",
                    """
                    package miui.systemui.controlcenter.panel.secondary;
                    public final class SecondaryParamsKt {
                        public static String from(com.android.systemui.plugins.qs.DetailAdapter adapter) { return null; }
                    }
                    """.trimIndent()
                )
                writeSource(
                    pluginSource,
                    "miui/systemui/controlcenter/panel/secondary/DetailPanelParams.java",
                    """
                    package miui.systemui.controlcenter.panel.secondary;
                    public final class DetailPanelParams {
                        private final com.android.systemui.plugins.qs.DetailAdapter adapter;
                        public DetailPanelParams(com.android.systemui.plugins.qs.DetailAdapter adapter) { this.adapter = adapter; }
                        public com.android.systemui.plugins.qs.DetailAdapter getAdapter() { return adapter; }
                        public boolean getUseSpecificHeight() { return false; }
                    }
                    """.trimIndent()
                )
                writeSource(
                    pluginSource,
                    "miui/systemui/controlcenter/panel/secondary/detail/DetailPanelDelegate.java",
                    """
                    package miui.systemui.controlcenter.panel.secondary.detail;
                    public final class DetailPanelDelegate {
                        private com.android.systemui.plugins.qs.DetailAdapter detailAdapter;
                        public void onHidden() { detailAdapter = null; }
                    }
                    """.trimIndent()
                )
                compileJava(pluginSource, pluginOutput, systemOutput)
                val pluginLoader = URLClassLoader(arrayOf(pluginOutput.toUri().toURL()), systemLoader)
                return SplitNativeDetailFixture(systemLoader, pluginLoader)
            }

            private fun writeSource(sourceDir: java.nio.file.Path, relativePath: String, source: String) {
                val sourceFile = sourceDir.resolve(relativePath)
                Files.createDirectories(sourceFile.parent)
                Files.writeString(sourceFile, source)
            }

            private fun compileJava(
                sourceDir: java.nio.file.Path,
                outputDir: java.nio.file.Path,
                classPath: java.nio.file.Path? = null
            ) {
                val sources = Files.walk(sourceDir)
                    .filter { path -> path.toString().endsWith(".java") }
                    .map { path -> path.toString() }
                    .toList()
                val compiler = ToolProvider.getSystemJavaCompiler()
                    ?: error("JDK compiler is required for native-detail fixture")
                val errors = ByteArrayOutputStream()
                val args = mutableListOf("-d", outputDir.toString())
                if (classPath != null) {
                    args += listOf("-classpath", classPath.toString())
                }
                args += sources
                val result = compiler.run(null, null, errors, *args.toTypedArray())
                check(result == 0) { errors.toString() }
            }
        }
    }
}
