package com.banana.hypermodes.systemserver.geofence

import android.app.Application
import android.content.Context
import android.content.Intent
import com.banana.hypermodes.protocol.Protocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PolarisCallbackReceiverTest {

    private lateinit var context: Context
    private lateinit var receiver: PolarisCallbackReceiver

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        receiver = PolarisCallbackReceiver()
        shadowOf(context as Application).clearBroadcastIntents()
    }

    @Test
    fun `valid Polaris callback forwards the protected internal payload`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_FENCE_ID, "hypermodes_abc")
            .putExtra(PolarisContract.EXTRA_EVENT, PolarisContract.EVENT_ENTER)

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf(PolarisContract.SERVICE_PACKAGE)
        )

        val forwarded = shadowOf(context as Application).broadcastIntents.single()
        assertEquals(Protocol.ACTION_POLARIS_GEOFENCE_EVENT, forwarded.action)
        assertEquals(Protocol.FRAMEWORK_PACKAGE, forwarded.`package`)
        assertEquals("hypermodes_abc", forwarded.getStringExtra(Protocol.EXTRA_POLARIS_FENCE_ID))
        assertEquals(11, forwarded.getIntExtra(Protocol.EXTRA_POLARIS_EVENT, -1))
    }

    @Test
    fun `valid exit event forwards correctly`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_FENCE_ID, "hypermodes_xyz")
            .putExtra(PolarisContract.EXTRA_EVENT, PolarisContract.EVENT_EXIT)

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf(PolarisContract.SERVICE_PACKAGE)
        )

        val forwarded = shadowOf(context as Application).broadcastIntents.single()
        assertEquals(Protocol.ACTION_POLARIS_GEOFENCE_EVENT, forwarded.action)
        assertEquals("hypermodes_xyz", forwarded.getStringExtra(Protocol.EXTRA_POLARIS_FENCE_ID))
        assertEquals(12, forwarded.getIntExtra(Protocol.EXTRA_POLARIS_EVENT, -1))
    }

    @Test
    fun `non-Polaris sender package rejects callback`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_FENCE_ID, "hypermodes_abc")
            .putExtra(PolarisContract.EXTRA_EVENT, PolarisContract.EVENT_ENTER)

        receiver.forwardValidated(
            context,
            incoming,
            "com.malicious.app",
            arrayOf("com.malicious.app")
        )

        assertTrue(shadowOf(context as Application).broadcastIntents.isEmpty())
    }

    @Test
    fun `Polaris sender package but different UID package rejects callback`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_FENCE_ID, "hypermodes_abc")
            .putExtra(PolarisContract.EXTRA_EVENT, PolarisContract.EVENT_ENTER)

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf("com.other.app", "com.malicious.app")
        )

        assertTrue(shadowOf(context as Application).broadcastIntents.isEmpty())
    }

    @Test
    fun `invalid event code rejects callback`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_FENCE_ID, "hypermodes_abc")
            .putExtra(PolarisContract.EXTRA_EVENT, 99)

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf(PolarisContract.SERVICE_PACKAGE)
        )

        assertTrue(shadowOf(context as Application).broadcastIntents.isEmpty())
    }

    @Test
    fun `legacy fence_id key name rejects callback`() {
        val incoming = Intent()
            .putExtra("fence_id", "hypermodes_abc")
            .putExtra(PolarisContract.EXTRA_EVENT, PolarisContract.EVENT_ENTER)

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf(PolarisContract.SERVICE_PACKAGE)
        )

        assertTrue(shadowOf(context as Application).broadcastIntents.isEmpty())
    }

    @Test
    fun `legacy event_type key name rejects callback`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_FENCE_ID, "hypermodes_abc")
            .putExtra("event_type", PolarisContract.EVENT_ENTER)

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf(PolarisContract.SERVICE_PACKAGE)
        )

        assertTrue(shadowOf(context as Application).broadcastIntents.isEmpty())
    }

    @Test
    fun `blank fence ID rejects callback`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_FENCE_ID, "")
            .putExtra(PolarisContract.EXTRA_EVENT, PolarisContract.EVENT_ENTER)

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf(PolarisContract.SERVICE_PACKAGE)
        )

        assertTrue(shadowOf(context as Application).broadcastIntents.isEmpty())
    }

    @Test
    fun `fence ID without hypermodes prefix rejects callback`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_FENCE_ID, "other_app_abc")
            .putExtra(PolarisContract.EXTRA_EVENT, PolarisContract.EVENT_ENTER)

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf(PolarisContract.SERVICE_PACKAGE)
        )

        assertTrue(shadowOf(context as Application).broadcastIntents.isEmpty())
    }

    @Test
    fun `missing fence ID rejects callback`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_EVENT, PolarisContract.EVENT_ENTER)

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf(PolarisContract.SERVICE_PACKAGE)
        )

        assertTrue(shadowOf(context as Application).broadcastIntents.isEmpty())
    }

    @Test
    fun `missing event rejects callback`() {
        val incoming = Intent()
            .putExtra(PolarisContract.EXTRA_FENCE_ID, "hypermodes_abc")

        receiver.forwardValidated(
            context,
            incoming,
            PolarisContract.SERVICE_PACKAGE,
            arrayOf(PolarisContract.SERVICE_PACKAGE)
        )

        assertTrue(shadowOf(context as Application).broadcastIntents.isEmpty())
    }
}
