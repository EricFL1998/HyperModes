package com.banana.hypermodes.systemserver

import android.database.ContentObserver
import android.os.Handler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EngineObserverOwnerTest {

    @Test
    fun `register stores observer and release unregisters it`() {
        val contentResolver = FakeContentResolver()
        val handler = Handler(android.os.Looper.getMainLooper())
        val owner = EngineObserverOwner(
            registerAction = { observer ->
                contentResolver.registerContentObserver(observer)
            },
            unregisterAction = { observer ->
                contentResolver.unregisterContentObserver(observer)
            }
        )

        val observer = object : ContentObserver(handler) {}
        owner.register(observer)

        assertEquals(observer, owner.current)
        assertTrue(contentResolver.registered)

        owner.release()

        assertEquals(null, owner.current)
        assertFalse(contentResolver.registered)
    }

    @Test
    fun `release is idempotent`() {
        val contentResolver = FakeContentResolver()
        val handler = Handler(android.os.Looper.getMainLooper())
        val owner = EngineObserverOwner(
            registerAction = { observer ->
                contentResolver.registerContentObserver(observer)
            },
            unregisterAction = { observer ->
                contentResolver.unregisterContentObserver(observer)
            }
        )
        val observer = object : ContentObserver(handler) {}

        owner.register(observer)
        owner.release()
        owner.release()

        assertFalse(contentResolver.registered)
        assertEquals(1, contentResolver.unregisterCount)
    }

    private class FakeContentResolver {
        var registered = false
        var unregisterCount = 0

        fun registerContentObserver(observer: ContentObserver) {
            registered = true
        }

        fun unregisterContentObserver(observer: ContentObserver) {
            registered = false
            unregisterCount++
        }
    }
}
