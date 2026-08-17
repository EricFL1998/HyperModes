package com.banana.hypermodes.hook

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ControlCenterCardHookTest {
    @Test
    fun `focus spec is the OS4 native tile spec`() {
        assertEquals("hypermodes_focus", ControlCenterCardHook.FOCUS_CARD_SPEC)
    }

    @Test
    fun `OS4 factory resolver selects createTile String overload only`() {
        val method = ControlCenterCardHook.resolveCreateTileMethod(FakeFactory::class.java)
        assertEquals("createTile", method.name)
        assertSame(String::class.java, method.parameterTypes.single())
    }

    @Test
    fun `OS4 repository resolver selects TileSpec and user id overload`() {
        val spec = FakeTileSpec("hypermodes_focus")
        val method = ControlCenterCardHook.resolveAddTileMethod(FakeInteractor::class.java, spec)
        assertEquals("addTile", method.name)
        assertSame(FakeTileSpec::class.java, method.parameterTypes[0])
        assertSame(Int::class.javaPrimitiveType, method.parameterTypes[1])
    }

    private class FakeFactory {
        fun createTile(spec: String): Any = spec
        fun createTile(spec: Any): Any = spec
    }

    private data class FakeTileSpec(val value: String)
    private class FakeInteractor {
        fun addTile(spec: FakeTileSpec, userId: Int) = Unit
        fun addTile(spec: String, userId: Int) = Unit
    }
}
