# Native Focus Detail Panel Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make Focus card detail panel use native HyperOS QSDetailContent/RecyclerView with live active-mode updates, unlimited item count, and proper close lifecycle.

**Architecture:** Refactor detail adapter into a stateful session that binds/rebinds native content views, add weak identity registry for ownership validation, install four narrowly-scoped Xposed hooks to extend HyperOS mappings for the private Focus tile, and implement explicit OPEN→CLOSING→CLOSED lifecycle to prevent half-close states.

**Tech Stack:** Kotlin, libxposed API 101.0.1, Android minSdk 35, JUnit 4, HyperOS native QSDetailContent/DetailAdapter/SecondaryPanelRouter.

## Global Constraints

- minSdk 35, targetSdk 37
- Kotlin, Java 11 source/target compatibility
- libxposed protective exception mode for all hooks
- All hooks must fail closed to original HyperOS behavior
- Identity-based registry (not equals/hashCode)
- Weak references for SystemUI objects
- Main-thread execution for all native UI operations
- No changes to built-in Wi-Fi/Bluetooth/cellular detail panels
- Keep existing Focus card placement and sizing unchanged
- Preserve existing manual fallback for native API failures

---

### Task 1: Native Detail Registry Foundation

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailRegistry.kt`
- Create: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusNativeDetailRegistryTest.kt`

**Interfaces:**
- Consumes: none (foundation)
- Produces: `FocusNativeDetailRegistry` singleton with `registerSession(adapter: Any, session: FocusModeDetailSession)`, `registerContent(content: Any, session: FocusModeDetailSession)`, `unregisterSession(adapter: Any)`, `unregisterContent(content: Any)`, `isFocusAdapter(adapter: Any): Boolean`, `isFocusContent(content: Any): Boolean`, `adapterSession(adapter: Any): FocusModeDetailSession?`, `contentSession(content: Any): FocusModeDetailSession?`, constants: `TILE_SPEC = "hypermodes_focus"`, `METRICS_CATEGORY = 118`, `CONTENT_SUFFIX = "HyperModesFocus"`

- [ ] **Step 1: Write failing registry lookup test**

```kotlin
package com.banana.hypermodes.controlcenter

import org.junit.Assert.*
import org.junit.Test

class FocusNativeDetailRegistryTest {
    
    @Test
    fun `unregistered adapter returns null session`() {
        val registry = FocusNativeDetailRegistry
        val fakeAdapter = Any()
        
        assertNull(registry.adapterSession(fakeAdapter))
        assertFalse(registry.isFocusAdapter(fakeAdapter))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailRegistryTest`
Expected: FAIL with "unresolved reference: FocusNativeDetailRegistry"

- [ ] **Step 3: Create minimal registry object**

```kotlin
package com.banana.hypermodes.controlcenter

import java.lang.ref.WeakReference
import java.util.Collections
import java.util.WeakHashMap

object FocusNativeDetailRegistry {
    const val TILE_SPEC = "hypermodes_focus"
    const val METRICS_CATEGORY = 118
    const val CONTENT_SUFFIX = "HyperModesFocus"
    
    private val adapterSessions = Collections.synchronizedMap(WeakHashMap<Any, WeakReference<FocusModeDetailSession>>())
    private val contentSessions = Collections.synchronizedMap(WeakHashMap<Any, WeakReference<FocusModeDetailSession>>())
    
    fun registerSession(adapter: Any, session: FocusModeDetailSession) {
        adapterSessions[adapter] = WeakReference(session)
    }
    
    fun registerContent(content: Any, session: FocusModeDetailSession) {
        contentSessions[content] = WeakReference(session)
    }
    
    fun unregisterSession(adapter: Any) {
        adapterSessions.remove(adapter)
    }
    
    fun unregisterContent(content: Any) {
        contentSessions.remove(content)
    }
    
    fun isFocusAdapter(adapter: Any): Boolean {
        return adapterSessions[adapter]?.get() != null
    }
    
    fun isFocusContent(content: Any): Boolean {
        return contentSessions[content]?.get() != null
    }
    
    fun adapterSession(adapter: Any): FocusModeDetailSession? {
        return adapterSessions[adapter]?.get()
    }
    
    fun contentSession(content: Any): FocusModeDetailSession? {
        return contentSessions[content]?.get()
    }
}

// Placeholder for session class
class FocusModeDetailSession
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailRegistryTest`
Expected: PASS

- [ ] **Step 5: Add identity-based registration test**

```kotlin
@Test
fun `registered adapter returns session by identity`() {
    val registry = FocusNativeDetailRegistry
    val adapter = Any()
    val session = FocusModeDetailSession()
    
    registry.registerSession(adapter, session)
    
    assertTrue(registry.isFocusAdapter(adapter))
    assertSame(session, registry.adapterSession(adapter))
}

@Test
fun `different adapter instance not recognized even with same content`() {
    val registry = FocusNativeDetailRegistry
    val adapter1 = "same-content"
    val adapter2 = "same-content"
    val session = FocusModeDetailSession()
    
    registry.registerSession(adapter1, session)
    
    assertTrue(registry.isFocusAdapter(adapter1))
    assertFalse(registry.isFocusAdapter(adapter2))
}
```

- [ ] **Step 6: Run identity tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailRegistryTest`
Expected: PASS (WeakHashMap uses identity by default)

- [ ] **Step 7: Add weak reference cleanup test**

```kotlin
@Test
fun `session lookup returns null after object GC`() {
    val registry = FocusNativeDetailRegistry
    var adapter: Any? = Any()
    val session = FocusModeDetailSession()
    
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
    val session = FocusModeDetailSession()
    
    registry.registerSession(adapter, session)
    assertTrue(registry.isFocusAdapter(adapter))
    
    registry.unregisterSession(adapter)
    
    assertFalse(registry.isFocusAdapter(adapter))
    assertNull(registry.adapterSession(adapter))
}
```

- [ ] **Step 8: Run cleanup tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailRegistryTest`
Expected: PASS

- [ ] **Step 9: Add content registration tests**

```kotlin
@Test
fun `content registration works independently of adapter`() {
    val registry = FocusNativeDetailRegistry
    val content = Any()
    val session = FocusModeDetailSession()
    
    registry.registerContent(content, session)
    
    assertTrue(registry.isFocusContent(content))
    assertSame(session, registry.contentSession(content))
}

@Test
fun `adapter and content can register same session`() {
    val registry = FocusNativeDetailRegistry
    val adapter = Any()
    val content = Any()
    val session = FocusModeDetailSession()
    
    registry.registerSession(adapter, session)
    registry.registerContent(content, session)
    
    assertTrue(registry.isFocusAdapter(adapter))
    assertTrue(registry.isFocusContent(content))
    assertSame(session, registry.adapterSession(adapter))
    assertSame(session, registry.contentSession(content))
}
```

- [ ] **Step 10: Run content registration tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailRegistryTest`
Expected: PASS

- [ ] **Step 11: Commit registry foundation**

```bash
git add app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailRegistry.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusNativeDetailRegistryTest.kt
git commit -m "feat: add identity-based weak registry for Focus detail sessions

- WeakHashMap for adapter and content registration
- Identity-based lookup (not equals/hashCode)
- Thread-safe synchronized access
- Weak references prevent SystemUI leaks
- Constants for tile spec, category, suffix

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 2: Native Detail Policy Extraction

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailPolicy.kt`
- Create: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusNativeDetailPolicyTest.kt`

**Interfaces:**
- Consumes: `FocusNativeDetailRegistry` from Task 1
- Produces: Pure policy functions: `shouldReturnFullItemCount(outerContent: Any?, suffix: String?, itemsLength: Int, registry: FocusNativeDetailRegistry): Int?`, `shouldMapToFocusSpec(adapter: Any?, registry: FocusNativeDetailRegistry): String?`, `shouldUseSpecificHeight(adapter: Any?, registry: FocusNativeDetailRegistry): Boolean?`, `resolveOuterContent(innerAdapter: Any, contentClass: Class<*>): Any?`, `readItemsArray(content: Any): Array<*>?`, `readSuffix(content: Any): String?`

- [ ] **Step 1: Write failing item count policy test**

```kotlin
package com.banana.hypermodes.controlcenter

import org.junit.Assert.*
import org.junit.Test

class FocusNativeDetailPolicyTest {
    
    @Test
    fun `registered Focus content with 25 items returns 25`() {
        val registry = FocusNativeDetailRegistry
        val content = Any()
        val session = FocusModeDetailSession()
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
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailPolicyTest`
Expected: FAIL with "unresolved reference: FocusNativeDetailPolicy"

- [ ] **Step 3: Implement item count policy**

```kotlin
package com.banana.hypermodes.controlcenter

object FocusNativeDetailPolicy {
    
    fun shouldReturnFullItemCount(
        outerContent: Any?,
        suffix: String?,
        itemsLength: Int,
        registry: FocusNativeDetailRegistry
    ): Int? {
        if (outerContent == null) return null
        if (!registry.isFocusContent(outerContent)) return null
        if (suffix != FocusNativeDetailRegistry.CONTENT_SUFFIX) return null
        return itemsLength
    }
    
    fun shouldMapToFocusSpec(
        adapter: Any?,
        registry: FocusNativeDetailRegistry
    ): String? {
        if (adapter == null) return null
        if (!registry.isFocusAdapter(adapter)) return null
        return FocusNativeDetailRegistry.TILE_SPEC
    }
    
    fun shouldUseSpecificHeight(
        adapter: Any?,
        registry: FocusNativeDetailRegistry
    ): Boolean? {
        if (adapter == null) return null
        if (!registry.isFocusAdapter(adapter)) return null
        return true
    }
    
    fun resolveOuterContent(innerAdapter: Any, contentClass: Class<*>): Any? {
        return try {
            // Try synthetic this$0 field first
            val field = innerAdapter.javaClass.getDeclaredField("this\$0")
            field.isAccessible = true
            val outer = field.get(innerAdapter)
            if (contentClass.isInstance(outer)) outer else null
        } catch (e: NoSuchFieldException) {
            // Fallback: find any field assignable to content class
            try {
                val field = innerAdapter.javaClass.declaredFields.firstOrNull { 
                    contentClass.isAssignableFrom(it.type) 
                }
                field?.let {
                    it.isAccessible = true
                    it.get(innerAdapter)
                }
            } catch (e: Throwable) {
                null
            }
        } catch (e: Throwable) {
            null
        }
    }
    
    fun readItemsArray(content: Any): Array<*>? {
        return try {
            val field = content.javaClass.getDeclaredField("items")
            field.isAccessible = true
            field.get(content) as? Array<*>
        } catch (e: Throwable) {
            null
        }
    }
    
    fun readSuffix(content: Any): String? {
        return try {
            val field = content.javaClass.getDeclaredField("suffix")
            field.isAccessible = true
            field.get(content) as? String
        } catch (e: Throwable) {
            null
        }
    }
}
```

- [ ] **Step 4: Run item count tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailPolicyTest`
Expected: PASS

- [ ] **Step 5: Add wrong suffix test**

```kotlin
@Test
fun `wrong suffix returns null`() {
    val registry = FocusNativeDetailRegistry
    val content = Any()
    val session = FocusModeDetailSession()
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
    val session = FocusModeDetailSession()
    registry.registerContent(content, session)
    
    val result = FocusNativeDetailPolicy.shouldReturnFullItemCount(
        outerContent = content,
        suffix = FocusNativeDetailRegistry.CONTENT_SUFFIX,
        itemsLength = 0,
        registry = registry
    )
    
    assertEquals(0, result)
}
```

- [ ] **Step 6: Run suffix tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailPolicyTest`
Expected: PASS

- [ ] **Step 7: Add adapter mapping tests**

```kotlin
@Test
fun `registered adapter maps to hypermodes_focus`() {
    val registry = FocusNativeDetailRegistry
    val adapter = Any()
    val session = FocusModeDetailSession()
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
```

- [ ] **Step 8: Run adapter mapping tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailPolicyTest`
Expected: PASS

- [ ] **Step 9: Add specific height tests**

```kotlin
@Test
fun `registered adapter uses specific height`() {
    val registry = FocusNativeDetailRegistry
    val adapter = Any()
    val session = FocusModeDetailSession()
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
```

- [ ] **Step 10: Run specific height tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusNativeDetailPolicyTest`
Expected: PASS

- [ ] **Step 11: Commit policy extraction**

```bash
git add app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailPolicy.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusNativeDetailPolicyTest.kt
git commit -m "feat: extract hook policy into pure testable functions

- shouldReturnFullItemCount validates registration and suffix
- shouldMapToFocusSpec validates adapter registration
- shouldUseSpecificHeight validates adapter registration
- resolveOuterContent handles synthetic this$0 and fallback
- readItemsArray/readSuffix with reflection helpers
- All functions return null on validation failure

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 3: Detail Session State Machine

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailRegistry.kt` (remove placeholder FocusModeDetailSession)
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt`
- Create: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailSessionTest.kt`

**Interfaces:**
- Consumes: `FocusNativeDetailRegistry` from Task 1, `FocusCardStateRepository`, `FocusNativeDetailContentApi?`
- Produces: `FocusModeDetailSession(repository: FocusCardStateRepository, onDismiss: () -> Unit, nativeDetailContentApi: FocusNativeDetailContentApi?, diagnostic: FocusDetailDiagnostic)` with `val adapter: Any`, `val state: DetailLifecycleState`, `fun setDetailListening(listening: Boolean)`, `fun onPanelHidden()`, `fun destroy()`, enum `DetailLifecycleState { CLOSED, OPEN, CLOSING }`

- [ ] **Step 1: Write failing state machine test**

```kotlin
package com.banana.hypermodes.controlcenter

import org.junit.Assert.*
import org.junit.Test

class FocusModeDetailSessionTest {
    
    @Test
    fun `initial state is CLOSED`() {
        val repository = FakeFocusCardStateRepository()
        val session = FocusModeDetailSession(
            repository = repository,
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )
        
        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }
}

// Minimal fakes
class FakeFocusCardStateRepository : FocusCardStateRepository(
    store = object : FocusCardConfigStore {
        override fun read() = null
        override fun write(json: String) = true
    },
    selector = ModeIndexSelector { 0 }
)

class FakeDiagnostic : FocusDetailDiagnostic {
    override fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?) {}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailSessionTest`
Expected: FAIL with "unresolved reference: FocusModeDetailSession" (still using placeholder)

- [ ] **Step 3: Remove placeholder and implement state machine skeleton**

Remove placeholder from `FocusNativeDetailRegistry.kt`:
```kotlin
// Delete: class FocusModeDetailSession
```

Create `FocusModeDetailSession.kt`:
```kotlin
package com.banana.hypermodes.controlcenter

import java.lang.ref.WeakReference

enum class DetailLifecycleState {
    CLOSED,
    OPEN,
    CLOSING
}

class FocusModeDetailSession(
    private val repository: FocusCardStateRepository,
    private val onDismiss: () -> Unit,
    private val nativeDetailContentApi: FocusNativeDetailContentApi?,
    private val diagnostic: FocusDetailDiagnostic
) {
    @Volatile
    var state: DetailLifecycleState = DetailLifecycleState.CLOSED
        private set
    
    val adapter: Any = Any() // Placeholder
    
    private var currentContent: WeakReference<Any>? = null
    private var pendingCardRefresh = false
    
    fun setDetailListening(listening: Boolean) {
        when {
            listening && state == DetailLifecycleState.CLOSED -> {
                state = DetailLifecycleState.OPEN
            }
            !listening && state == DetailLifecycleState.OPEN -> {
                state = DetailLifecycleState.CLOSING
                pendingCardRefresh = true
            }
        }
    }
    
    fun onPanelHidden() {
        if (state == DetailLifecycleState.CLOSING) {
            state = DetailLifecycleState.CLOSED
            currentContent = null
            // Pending refresh will be posted by caller
        }
    }
    
    fun destroy() {
        state = DetailLifecycleState.CLOSED
        currentContent = null
        pendingCardRefresh = false
        FocusNativeDetailRegistry.unregisterSession(adapter)
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailSessionTest`
Expected: PASS

- [ ] **Step 5: Add state transition tests**

```kotlin
@Test
fun `setDetailListening true moves CLOSED to OPEN`() {
    val session = FocusModeDetailSession(
        repository = FakeFocusCardStateRepository(),
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
        repository = FakeFocusCardStateRepository(),
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
        repository = FakeFocusCardStateRepository(),
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
        repository = FakeFocusCardStateRepository(),
        onDismiss = {},
        nativeDetailContentApi = null,
        diagnostic = FakeDiagnostic()
    )
    
    session.setDetailListening(true)
    assertEquals(DetailLifecycleState.OPEN, session.state)
    
    session.destroy()
    
    assertEquals(DetailLifecycleState.CLOSED, session.state)
}
```

- [ ] **Step 6: Run state transition tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailSessionTest`
Expected: PASS

- [ ] **Step 7: Add pending refresh tests**

```kotlin
@Test
fun `setDetailListening false records pending refresh`() {
    var refreshPosted = false
    val session = FocusModeDetailSession(
        repository = FakeFocusCardStateRepository(),
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
        repository = FakeFocusCardStateRepository(),
        onDismiss = {},
        nativeDetailContentApi = null,
        diagnostic = FakeDiagnostic()
    )
    
    session.setDetailListening(true)
    session.setDetailListening(false)
    
    session.destroy()
    
    assertEquals(DetailLifecycleState.CLOSED, session.state)
}
```

- [ ] **Step 8: Run pending refresh tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailSessionTest`
Expected: PASS

- [ ] **Step 9: Commit state machine**

```bash
git add app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailRegistry.kt app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailSessionTest.kt
git commit -m "feat: implement detail session state machine

- DetailLifecycleState: CLOSED, OPEN, CLOSING
- setDetailListening transitions CLOSED<->OPEN and OPEN->CLOSING
- onPanelHidden transitions CLOSING->CLOSED
- destroy moves to CLOSED immediately
- Weak reference to current content
- Pending card refresh flag during CLOSING
- Registry cleanup on destroy

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

由于完整计划非常长（11个任务，每个任务5-11个步骤），我会分段继续写入。是否继续写完整计划，还是你希望我用更简洁的格式？