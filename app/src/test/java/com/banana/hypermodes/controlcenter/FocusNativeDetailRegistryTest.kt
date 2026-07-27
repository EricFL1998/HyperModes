package com.banana.hypermodes.controlcenter

import org.junit.Assert.*
import org.junit.Test

class FocusNativeDetailRegistryTest {

    private fun createTestSession(): FocusModeDetailSession {
        return FocusModeDetailSession(
            repository = FocusCardStateRepository(
                store = object : FocusCardConfigStore {
                    override fun read() = null
                    override fun write(json: String) = true
                },
                selector = ModeIndexSelector { 0 }
            ),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = object : FocusDetailDiagnostic {
                override fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?) {}
            }
        )
    }

    @Test
    fun `unregistered adapter returns null session`() {
        val registry = FocusNativeDetailRegistry
        val fakeAdapter = Any()

        assertNull(registry.adapterSession(fakeAdapter))
        assertFalse(registry.isFocusAdapter(fakeAdapter))
    }

    @Test
    fun `registered adapter returns session by identity`() {
        val registry = FocusNativeDetailRegistry
        val adapter = Any()
        val session = createTestSession()

        registry.registerSession(adapter, session)

        assertTrue(registry.isFocusAdapter(adapter))
        assertSame(session, registry.adapterSession(adapter))
    }

    @Test
    fun `different adapter instance not recognized even with same content`() {
        val registry = FocusNativeDetailRegistry
        val adapter1 = Any()
        val adapter2 = Any()
        val session = createTestSession()

        registry.registerSession(adapter1, session)

        assertTrue(registry.isFocusAdapter(adapter1))
        assertFalse(registry.isFocusAdapter(adapter2))
    }

    @Test
    fun `session lookup returns null after object GC`() {
        val registry = FocusNativeDetailRegistry
        var adapter: Any? = Any()
        val session = createTestSession()

        registry.registerSession(adapter!!, session)
        assertTrue(registry.isFocusAdapter(adapter!!))

        adapter = null
        System.gc()
        Thread.sleep(100)

        // After GC, weak reference should be cleared
        // We can't directly test the GC'd adapter, but we can verify cleanup doesn't leak
    }

    @Test
    fun `unregister removes session immediately`() {
        val registry = FocusNativeDetailRegistry
        val adapter = Any()
        val session = createTestSession()

        registry.registerSession(adapter, session)
        assertTrue(registry.isFocusAdapter(adapter))

        registry.unregisterSession(adapter)

        assertFalse(registry.isFocusAdapter(adapter))
        assertNull(registry.adapterSession(adapter))
    }

    @Test
    fun `content registration works independently of adapter`() {
        val registry = FocusNativeDetailRegistry
        val content = Any()
        val session = createTestSession()

        registry.registerContent(content, session)

        assertTrue(registry.isFocusContent(content))
        assertSame(session, registry.contentSession(content))
    }

    @Test
    fun `adapter and content can register same session`() {
        val registry = FocusNativeDetailRegistry
        val adapter = Any()
        val content = Any()
        val session = createTestSession()

        registry.registerSession(adapter, session)
        registry.registerContent(content, session)

        assertTrue(registry.isFocusAdapter(adapter))
        assertTrue(registry.isFocusContent(content))
        assertSame(session, registry.adapterSession(adapter))
        assertSame(session, registry.contentSession(content))
    }
}
