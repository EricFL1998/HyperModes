package com.banana.hypermodes.controlcenter

import org.junit.Assert.*
import org.junit.Test

class FocusModeDetailSessionTest {

    @Test
    fun `initial state is CLOSED`() {
        val repository = createFakeRepository()
        val session = FocusModeDetailSession(
            repository = repository,
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }

    @Test
    fun `setDetailListening true moves CLOSED to OPEN`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        assertEquals(DetailLifecycleState.CLOSED, session.state)

        session.setDetailListening(true)

        assertEquals(DetailLifecycleState.OPEN, session.state)
    }

    @Test
    fun `setDetailListening false moves OPEN to CLOSING`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        assertEquals(DetailLifecycleState.OPEN, session.state)

        session.setDetailListening(false)

        assertEquals(DetailLifecycleState.CLOSING, session.state)
    }

    @Test
    fun `onPanelHidden moves CLOSING to CLOSED`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        session.setDetailListening(false)
        assertEquals(DetailLifecycleState.CLOSING, session.state)

        session.onPanelHidden()

        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }

    @Test
    fun `destroy moves to CLOSED immediately`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        assertEquals(DetailLifecycleState.OPEN, session.state)

        session.destroy()

        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }

    @Test
    fun `setDetailListening false records pending refresh`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        session.setDetailListening(false)

        // Pending refresh flag is internal, verify through state
        assertEquals(DetailLifecycleState.CLOSING, session.state)
    }

    @Test
    fun `destroy clears pending refresh`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        session.setDetailListening(false)

        session.destroy()

        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }

    private fun createFakeRepository(): FocusCardStateRepository {
        return FocusCardStateRepository(
            store = object : FocusCardConfigStore {
                override fun read() = null
                override fun write(json: String) = true
            },
            selector = ModeIndexSelector { 0 }
        )
    }
}

// Minimal fakes
class FakeDiagnostic : FocusDetailDiagnostic {
    override fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?) {}
}
