package com.banana.hypermodes.protocol

import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PackageLifecyclePolicyTest {

    private val target = "com.banana.hypermodes"

    @Test
    fun testClassifyIgnoreUnrelatedAction() {
        val intent = Intent("android.intent.action.VIEW").apply {
            data = Uri.parse("package:$target")
        }
        assertEquals(PackageLifecyclePolicy.Action.IGNORE, PackageLifecyclePolicy.classify(intent, target))
    }

    @Test
    fun testClassifyIgnoreUnrelatedPackage() {
        val intent = Intent(Intent.ACTION_PACKAGE_REMOVED).apply {
            data = Uri.parse("package:com.other.app")
        }
        assertEquals(PackageLifecyclePolicy.Action.IGNORE, PackageLifecyclePolicy.classify(intent, target))
    }

    @Test
    fun testClassifyRemove() {
        val intent = Intent(Intent.ACTION_PACKAGE_REMOVED).apply {
            data = Uri.parse("package:$target")
            putExtra(Intent.EXTRA_REPLACING, false)
        }
        assertEquals(PackageLifecyclePolicy.Action.REMOVE, PackageLifecyclePolicy.classify(intent, target))
    }

    @Test
    fun testClassifyFullyRemove() {
        val intent = Intent(Intent.ACTION_PACKAGE_FULLY_REMOVED).apply {
            data = Uri.parse("package:$target")
        }
        assertEquals(PackageLifecyclePolicy.Action.REMOVE, PackageLifecyclePolicy.classify(intent, target))
    }

    @Test
    fun testClassifyReplacementStarted() {
        val intent = Intent(Intent.ACTION_PACKAGE_REMOVED).apply {
            data = Uri.parse("package:$target")
            putExtra(Intent.EXTRA_REPLACING, true)
        }
        assertEquals(PackageLifecyclePolicy.Action.REPLACEMENT_STARTED, PackageLifecyclePolicy.classify(intent, target))
    }

    @Test
    fun testClassifyReplacementFinished() {
        val intent = Intent(Intent.ACTION_PACKAGE_ADDED).apply {
            data = Uri.parse("package:$target")
            putExtra(Intent.EXTRA_REPLACING, true)
        }
        assertEquals(PackageLifecyclePolicy.Action.REPLACEMENT_FINISHED, PackageLifecyclePolicy.classify(intent, target))
    }

    @Test
    fun testClassifyInstall() {
        val intent = Intent(Intent.ACTION_PACKAGE_ADDED).apply {
            data = Uri.parse("package:$target")
            putExtra(Intent.EXTRA_REPLACING, false)
        }
        assertEquals(PackageLifecyclePolicy.Action.INSTALL, PackageLifecyclePolicy.classify(intent, target))
    }
}
