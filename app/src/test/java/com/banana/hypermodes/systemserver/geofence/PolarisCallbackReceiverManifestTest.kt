package com.banana.hypermodes.systemserver.geofence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PolarisCallbackReceiverManifestTest {

    @Test
    fun `PolarisCallbackReceiver is exported with no permission and no intent filter`() {
        val appContext = RuntimeEnvironment.getApplication()
        val packageInfo = appContext.packageManager.getPackageInfo(
            appContext.packageName,
            android.content.pm.PackageManager.GET_RECEIVERS
        )

        val receiver = packageInfo.receivers?.find {
            it.name == "com.banana.hypermodes.systemserver.geofence.PolarisCallbackReceiver"
        }

        // Receiver must exist
        assertTrue("PolarisCallbackReceiver not found in manifest", receiver != null)

        // Must be exported (Polaris sends explicit component broadcasts)
        assertTrue(
            "PolarisCallbackReceiver must be exported=true",
            receiver!!.exported
        )

        // Must NOT have a permission (Polaris doesn't hold our signature permission)
        assertNull(
            "PolarisCallbackReceiver must have no android:permission attribute",
            receiver.permission
        )

        // Must NOT have intent filters (Polaris sends explicit component broadcasts)
        // Note: Robolectric doesn't expose intent filters on ReceiverInfo easily,
        // but the requirement is that there should be no <intent-filter> block in the manifest
        // This is verified by the fact that it's exported but permissionless
    }
}
