package com.banana.hypermodes.protocol

import org.junit.Assert.assertEquals
import org.junit.Test

class ProtocolTest {

    @Test
    fun `every day bitmask is 127`() {
        assertEquals(127, Protocol.EVERY_DAY)
    }

    @Test
    fun `single day encodes to its bit`() {
        assertEquals(1, Protocol.daysToBitmask(setOf(0)))   // Monday
        assertEquals(64, Protocol.daysToBitmask(setOf(6)))  // Sunday
    }

    @Test
    fun `weekdays encode to 31`() {
        assertEquals(31, Protocol.daysToBitmask(setOf(0, 1, 2, 3, 4)))
    }

    @Test
    fun `empty set encodes to 0`() {
        assertEquals(0, Protocol.daysToBitmask(emptySet()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `out of range day throws`() {
        Protocol.daysToBitmask(setOf(7))
    }

    @Test
    fun `bitmask round-trips through days`() {
        val days = setOf(0, 2, 6)
        assertEquals(days, Protocol.bitmaskToDays(Protocol.daysToBitmask(days)))
    }

    @Test
    fun `bitmask 127 decodes to all seven days`() {
        assertEquals((0..6).toSet(), Protocol.bitmaskToDays(127))
    }
}
