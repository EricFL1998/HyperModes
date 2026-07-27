package com.banana.hypermodes.hook

import android.view.View
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class FullAodSignalTest {
    private val context = RuntimeEnvironment.getApplication()
    private val testTagKey = 0x7f010001

    @Test
    fun `true tag stored under the root id means full aod`() {
        val root = View(context).apply {
            id = testTagKey
            setTag(id, true)
        }

        assertTrue(FullAodSignal.isFullAod(root))
    }

    @Test
    fun `missing false wrong-type and no-id tags are rejected`() {
        val missing = View(context).apply { id = testTagKey }
        val disabled = View(context).apply {
            id = testTagKey
            setTag(id, false)
        }
        val wrongType = View(context).apply {
            id = testTagKey
            setTag(id, "true")
        }
        val noId = View(context)

        assertFalse(FullAodSignal.isFullAod(missing))
        assertFalse(FullAodSignal.isFullAod(disabled))
        assertFalse(FullAodSignal.isFullAod(wrongType))
        assertFalse(FullAodSignal.isFullAod(noId))
    }
}
