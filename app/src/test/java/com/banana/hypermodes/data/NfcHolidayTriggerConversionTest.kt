package com.banana.hypermodes.data

import com.banana.hypermodes.systemserver.config.ComplexTrigger
import org.junit.Assert.assertEquals
import org.junit.Test

class NfcHolidayTriggerConversionTest {

    @Test
    fun `NFC trigger round trips to complex config and back`() {
        val trigger = ModeTrigger.Nfc(tagId = "04a1b2c3d4e5")

        val complex = trigger.toComplexTrigger() as ComplexTrigger.Nfc
        assertEquals("04a1b2c3d4e5", complex.tagId)

        val restored = complex.toModeTrigger() as ModeTrigger.Nfc
        assertEquals(trigger, restored)
    }

    @Test
    fun `NFC trigger with blank tag id matches any`() {
        val trigger = ModeTrigger.Nfc(tagId = "")
        val complex = trigger.toComplexTrigger() as ComplexTrigger.Nfc
        assertEquals("", complex.tagId)
    }

    @Test
    fun `holiday trigger round trips both kinds`() {
        val holiday = ModeTrigger.Holiday(kind = "节假日")
        val workday = ModeTrigger.Holiday(kind = "工作日")

        val holidayComplex = holiday.toComplexTrigger() as ComplexTrigger.Holiday
        val workdayComplex = workday.toComplexTrigger() as ComplexTrigger.Holiday
        assertEquals("节假日", holidayComplex.kind)
        assertEquals("工作日", workdayComplex.kind)

        assertEquals(holiday, holidayComplex.toModeTrigger())
        assertEquals(workday, workdayComplex.toModeTrigger())
    }
}
