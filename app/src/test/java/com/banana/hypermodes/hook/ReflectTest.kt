package com.banana.hypermodes.hook

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ReflectTest {

    @Suppress("unused")
    open class Base {
        var baseField: Int = 1
    }

    @Suppress("unused")
    class Fixture : Base() {
        var storedHour: Int = 0
        var label: Any? = null
        private var secret: Int = 7

        fun setHour(value: Int) {
            storedHour = value
        }

        fun greet(prefix: String, times: Int): String = prefix.repeat(times)

        fun secretValue(): Int = secret

        class WithCtor(val ctx: String, val count: Int)

        companion object {
            @JvmStatic
            fun add(a: Int, b: Int): Int = a + b

            @JvmStatic
            fun describe(ctx: Appendable?): String = if (ctx == null) "null-ok" else "non-null"

            @JvmStatic
            fun noArgs(): String = "none"
        }
    }

    @Test
    fun `callStatic resolves int parameters with boxed args`() {
        assertEquals(5, Reflect.callStatic(Fixture::class.java, "add", 2, 3))
    }

    @Test
    fun `callStatic with no args`() {
        assertEquals("none", Reflect.callStatic(Fixture::class.java, "noArgs"))
    }

    @Test
    fun `callStatic matches null arg to reference parameter`() {
        assertEquals("null-ok", Reflect.callStatic(Fixture::class.java, "describe", null))
    }

    @Test
    fun `callStatic throws NoSuchMethodException for unknown name`() {
        assertThrows(NoSuchMethodException::class.java) {
            Reflect.callStatic(Fixture::class.java, "missing")
        }
    }

    @Test
    fun `call invokes instance method with mixed args`() {
        assertEquals("abab", Reflect.call(Fixture(), "greet", "ab", 2))
    }

    @Test
    fun `call works for setter style methods`() {
        val f = Fixture()
        Reflect.call(f, "setHour", 42)
        assertEquals(42, f.storedHour)
    }

    @Test
    fun `setIntField writes private fields`() {
        val f = Fixture()
        Reflect.setIntField(f, "secret", 99)
        assertEquals(99, f.secretValue())
    }

    @Test
    fun `setIntField walks superclasses`() {
        val f = Fixture()
        Reflect.setIntField(f, "baseField", 9)
        assertEquals(9, f.baseField)
    }

    @Test
    fun `setObjectField writes reference fields`() {
        val f = Fixture()
        Reflect.setObjectField(f, "label", "x")
        assertEquals("x", f.label)
    }

    @Test
    fun `newInstance matches constructor by arg types`() {
        val o = Reflect.newInstance(Fixture.WithCtor::class.java, "a", 3) as Fixture.WithCtor
        assertEquals("a", o.ctx)
        assertEquals(3, o.count)
    }

    @Test
    fun `findClass loads via given classloader`() {
        assertEquals(
            String::class.java,
            Reflect.findClass("java.lang.String", javaClass.classLoader!!)
        )
    }
}
