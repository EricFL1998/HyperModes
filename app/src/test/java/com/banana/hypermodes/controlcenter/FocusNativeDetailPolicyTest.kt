package com.banana.hypermodes.controlcenter

import org.junit.Assert.*
import org.junit.Test

class FocusNativeDetailPolicyTest {

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
    fun `registered Focus content with 25 items returns 25`() {
        val registry = FocusNativeDetailRegistry
        val content = Any()
        val session = createTestSession()
        registry.registerContent(content, session)

        val result = FocusNativeDetailPolicy.shouldReturnFullItemCount(
            outerContent = content,
            suffix = FocusNativeDetailRegistry.CONTENT_SUFFIX,
            itemsLength = 25,
            registry = registry
        )

        assertEquals(25, result)
    }

    @Test
    fun `unregistered content returns null`() {
        val registry = FocusNativeDetailRegistry
        val content = Any()

        val result = FocusNativeDetailPolicy.shouldReturnFullItemCount(
            outerContent = content,
            suffix = FocusNativeDetailRegistry.CONTENT_SUFFIX,
            itemsLength = 25,
            registry = registry
        )

        assertNull(result)
    }

    @Test
    fun `wrong suffix returns null`() {
        val registry = FocusNativeDetailRegistry
        val content = Any()
        val session = createTestSession()
        registry.registerContent(content, session)

        val result = FocusNativeDetailPolicy.shouldReturnFullItemCount(
            outerContent = content,
            suffix = "OtherTile",
            itemsLength = 25,
            registry = registry
        )

        assertNull(result)
    }

    @Test
    fun `zero items returns zero`() {
        val registry = FocusNativeDetailRegistry
        val content = Any()
        val session = createTestSession()
        registry.registerContent(content, session)

        val result = FocusNativeDetailPolicy.shouldReturnFullItemCount(
            outerContent = content,
            suffix = FocusNativeDetailRegistry.CONTENT_SUFFIX,
            itemsLength = 0,
            registry = registry
        )

        assertEquals(0, result)
    }

    @Test
    fun `registered adapter maps to hypermodes_focus`() {
        val registry = FocusNativeDetailRegistry
        val adapter = Any()
        val session = createTestSession()
        registry.registerSession(adapter, session)

        val result = FocusNativeDetailPolicy.shouldMapToFocusSpec(adapter, registry)

        assertEquals("hypermodes_focus", result)
    }

    @Test
    fun `unregistered adapter returns null spec`() {
        val registry = FocusNativeDetailRegistry
        val adapter = Any()

        val result = FocusNativeDetailPolicy.shouldMapToFocusSpec(adapter, registry)

        assertNull(result)
    }

    @Test
    fun `registered adapter uses specific height`() {
        val registry = FocusNativeDetailRegistry
        val adapter = Any()
        val session = createTestSession()
        registry.registerSession(adapter, session)

        val result = FocusNativeDetailPolicy.shouldUseSpecificHeight(adapter, registry)

        assertEquals(true, result)
    }

    @Test
    fun `unregistered adapter returns null for height`() {
        val registry = FocusNativeDetailRegistry
        val adapter = Any()

        val result = FocusNativeDetailPolicy.shouldUseSpecificHeight(adapter, registry)

        assertNull(result)
    }
}
