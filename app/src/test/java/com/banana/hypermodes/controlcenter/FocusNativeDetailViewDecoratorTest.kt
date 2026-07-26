package com.banana.hypermodes.controlcenter

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.net.URLClassLoader
import java.nio.file.Files
import javax.tools.ToolProvider

class FocusNativeDetailViewDecoratorTest {

    @Test
    fun `decorate clears list and host backgrounds and hides scrollbar only`() {
        val list = RecordingView()
        val host = RecordingViewGroup(listOf(list))
        val root = RecordingViewGroup(listOf(host))

        val decorated = FocusNativeDetailViewDecorator.decorate(root) { view -> view === list }

        assertTrue(decorated)
        assertTrue(list.backgroundCleared)
        assertTrue(host.backgroundCleared)
        assertFalse(root.backgroundCleared)
        assertFalse(list.verticalScrollbarEnabled)
        assertFalse(list.enabledWasChanged)
    }

    @Test
    fun `decorate adds idempotent top padding and preserves other padding`() {
        val list = RecordingViewGroup(emptyList()).apply {
            setPadding(3, 5, 7, 11)
        }
        val root = RecordingViewGroup(listOf(list))

        FocusNativeDetailViewDecorator.decorate(root) { view -> view === list }
        FocusNativeDetailViewDecorator.decorate(root) { view -> view === list }

        assertEquals(3, list.paddingLeft)
        assertEquals(21, list.paddingTop)
        assertEquals(7, list.paddingRight)
        assertEquals(11, list.paddingBottom)
        assertFalse(list.clipToPadding)
    }

    @Test
    fun `decorate is non fatal when native list is absent`() {
        val root = RecordingViewGroup(emptyList())

        val decorated = FocusNativeDetailViewDecorator.decorate(root) { false }

        assertFalse(decorated)
        assertFalse(root.backgroundCleared)
    }

    @Test
    fun `decorate is non fatal when hierarchy inspection fails`() {
        val root = RecordingViewGroup(emptyList())

        val decorated = FocusNativeDetailViewDecorator.decorate(root) {
            throw IllegalStateException("changed native hierarchy")
        }

        assertFalse(decorated)
    }

    @Test
    fun `row visual cleaner clears visual state without changing click behavior`() {
        val clickListener = View.OnClickListener {}
        val row = RecordingView().apply {
            isSelected = true
            isActivated = true
            isEnabled = true
            isClickable = true
            background = ColorDrawable(0xff000000.toInt())
            foreground = ColorDrawable(0xffffffff.toInt())
            setOnClickListener(clickListener)
        }
        row.resetRecordings()

        FocusNativeRowVisualCleaner.clear(row, row.javaClass.classLoader)

        assertTrue(row.backgroundCleared)
        assertTrue(row.foregroundCleared)
        assertFalse(row.isSelected)
        assertFalse(row.isActivated)
        assertTrue(row.isEnabled)
        assertTrue(row.isClickable)
        assertFalse(row.enabledWasChanged)
        assertFalse(row.clickableWasChanged)
        assertFalse(row.onClickListenerWasChanged)
        assertSame(clickListener, row.currentOnClickListener)
    }

    @Test
    fun `row visual cleaner invokes MIUI blur and Folme cleanup when present`() {
        val classLoader = NativeVisualCleanupFixture.create()
        val row = RecordingView()

        FocusNativeRowVisualCleaner.clear(row, classLoader)

        val miBlurCompat = classLoader.loadClass("com.miui.systemui.util.MiBlurCompat")
        val folme = classLoader.loadClass("miuix.animation.Folme")
        assertSame(row, miBlurCompat.getField("lastBlurView").get(null))
        assertEquals(0, miBlurCompat.getField("lastBlurMode").getInt(null))
        assertSame(row, miBlurCompat.getField("lastClearedView").get(null))
        assertSame(row, folme.getField("lastCleanedView").get(null))
    }

    private object NativeVisualCleanupFixture {
        fun create(): ClassLoader {
            val sourceDir = Files.createTempDirectory("focus-native-visual-src")
            val outputDir = Files.createTempDirectory("focus-native-visual-out")
            writeSource(
                sourceDir,
                "com/miui/systemui/util/MiBlurCompat.java",
                """
                package com.miui.systemui.util;
                public final class MiBlurCompat {
                    public static android.view.View lastBlurView;
                    public static int lastBlurMode = -1;
                    public static android.view.View lastClearedView;
                    public static void setMiViewBlurModeCompat(int mode, android.view.View view) {
                        lastBlurMode = mode;
                        lastBlurView = view;
                    }
                    public static void clearMiBackgroundBlendColorCompat(android.view.View view) {
                        lastClearedView = view;
                    }
                }
                """.trimIndent()
            )
            writeSource(
                sourceDir,
                "miuix/animation/Folme.java",
                """
                package miuix.animation;
                public final class Folme {
                    public static android.view.View lastCleanedView;
                    public static void clean(android.view.View view) {
                        lastCleanedView = view;
                    }
                }
                """.trimIndent()
            )
            compileJava(sourceDir, outputDir)
            return URLClassLoader(arrayOf(outputDir.toUri().toURL()), FocusNativeDetailViewDecoratorTest::class.java.classLoader)
        }

        private fun writeSource(sourceDir: java.nio.file.Path, relativePath: String, source: String) {
            val sourceFile = sourceDir.resolve(relativePath)
            Files.createDirectories(sourceFile.parent)
            Files.writeString(sourceFile, source)
        }

        private fun compileJava(sourceDir: java.nio.file.Path, outputDir: java.nio.file.Path) {
            val sources = Files.walk(sourceDir)
                .filter { path -> path.toString().endsWith(".java") }
                .map { path -> path.toString() }
                .toList()
            val compiler = ToolProvider.getSystemJavaCompiler()
                ?: error("JDK compiler is required for native visual cleanup fixture")
            val errors = ByteArrayOutputStream()
            val result = compiler.run(
                null,
                null,
                errors,
                "-classpath",
                System.getProperty("java.class.path"),
                "-d",
                outputDir.toString(),
                *sources.toTypedArray()
            )
            check(result == 0) { errors.toString() }
        }
    }

    private open class RecordingView : View(null) {
        var backgroundCleared = false
        var foregroundCleared = false
        var backgroundClearCalls = 0
        var verticalScrollbarEnabled = true
        var enabledWasChanged = false
        var clickableWasChanged = false
        var onClickListenerWasChanged = false
        var currentOnClickListener: OnClickListener? = null
        private var enabledValue = false
        private var clickableValue = false
        private val postedCallbacks = mutableListOf<Runnable>()

        fun resetRecordings() {
            backgroundCleared = false
            foregroundCleared = false
            backgroundClearCalls = 0
            enabledWasChanged = false
            clickableWasChanged = false
            onClickListenerWasChanged = false
        }

        override fun setBackground(background: Drawable?) {
            if (background == null) {
                backgroundCleared = true
                backgroundClearCalls += 1
            } else {
                backgroundCleared = false
            }
        }

        override fun setForeground(foreground: Drawable?) {
            foregroundCleared = foreground == null
        }

        override fun setOnClickListener(l: OnClickListener?) {
            onClickListenerWasChanged = true
            currentOnClickListener = l
            super.setOnClickListener(l)
        }

        override fun post(action: Runnable): Boolean {
            postedCallbacks += action
            return true
        }

        fun runPostedCallbacks() {
            postedCallbacks.toList().forEach { it.run() }
            postedCallbacks.clear()
        }

        override fun setVerticalScrollBarEnabled(enabled: Boolean) {
            verticalScrollbarEnabled = enabled
        }

        override fun isEnabled(): Boolean = enabledValue

        override fun setEnabled(enabled: Boolean) {
            enabledWasChanged = true
            enabledValue = enabled
            super.setEnabled(enabled)
        }

        override fun isClickable(): Boolean = clickableValue

        override fun setClickable(clickable: Boolean) {
            clickableWasChanged = true
            clickableValue = clickable
            super.setClickable(clickable)
        }
    }

    private open class RecordingViewGroup(
        initialChildren: List<View>
    ) : ViewGroup(null) {
        var children = initialChildren
        var backgroundCleared = false
        private var recordedPaddingLeft = 0
        private var recordedPaddingTop = 0
        private var recordedPaddingRight = 0
        private var recordedPaddingBottom = 0
        private var recordedClipToPadding = true

        override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
            recordedPaddingLeft = left
            recordedPaddingTop = top
            recordedPaddingRight = right
            recordedPaddingBottom = bottom
        }

        override fun getPaddingLeft(): Int = recordedPaddingLeft
        override fun getPaddingTop(): Int = recordedPaddingTop
        override fun getPaddingRight(): Int = recordedPaddingRight
        override fun getPaddingBottom(): Int = recordedPaddingBottom
        override fun setClipToPadding(clipToPadding: Boolean) {
            recordedClipToPadding = clipToPadding
        }
        override fun getClipToPadding(): Boolean = recordedClipToPadding

        override fun getChildCount(): Int = children.size
        override fun getChildAt(index: Int): View = children[index]
        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) = Unit
        override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(0, 0)

        override fun setBackground(background: Drawable?) {
            backgroundCleared = background == null
        }
    }
}
