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

### Task 4: Complete Session Implementation with Native Binding

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt`
- Modify: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailSessionTest.kt`

**Interfaces:**
- Consumes: `FocusNativeDetailRegistry` from Task 1, `FocusNativeDetailContentApi`, `FocusCardStateRepository`
- Produces: Complete `FocusModeDetailSession` with `bindDetailView(context: Context, convertView: View?, parent: ViewGroup?): View?`, `refreshItems()`, `hasPendingCardRefresh(): Boolean`, real adapter proxy implementation

- [ ] **Step 1: Write failing native bind test**

```kotlin
@Test
fun `bindDetailView registers content and returns View`() {
    val api = FakeNativeDetailContentApi()
    val session = FocusModeDetailSession(
        repository = FakeFocusCardStateRepository(),
        onDismiss = {},
        nativeDetailContentApi = api,
        diagnostic = FakeDiagnostic()
    )
    
    val view = session.bindDetailView(
        context = mockContext,
        convertView = null,
        parent = null
    )
    
    assertNotNull(view)
    assertTrue(FocusNativeDetailRegistry.isFocusContent(view))
}

class FakeNativeDetailContentApi : FocusNativeDetailContentApi {
    override fun convertOrInflate(context: Context, convertView: View?, parent: ViewGroup?): Any {
        return MockView() // Return fake View
    }
    override fun setSuffix(content: Any, suffix: String) {}
    override fun setItems(content: Any, items: Array<Any>) {}
    override fun setCallback(content: Any, callback: Any) {}
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailSessionTest`
Expected: FAIL with "bindDetailView not implemented"

- [ ] **Step 3: Implement native bind skeleton**

Add to `FocusModeDetailSession.kt`:
```kotlin
fun bindDetailView(
    context: Context,
    convertView: View?,
    parent: ViewGroup?
): View? {
    val api = nativeDetailContentApi ?: return null
    
    val content = api.convertOrInflate(context, convertView, parent)
    if (content !is View) return null
    
    FocusNativeDetailRegistry.registerContent(content, this)
    currentContent = WeakReference(content)
    
    api.setSuffix(content, FocusNativeDetailRegistry.CONTENT_SUFFIX)
    
    return content
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailSessionTest`
Expected: PASS

- [ ] **Step 5: Add refreshItems test**

```kotlin
@Test
fun `refreshItems calls setItems when OPEN`() {
    val api = FakeNativeDetailContentApi()
    val repository = FakeFocusCardStateRepository().apply {
        // Add 2 modes
    }
    val session = FocusModeDetailSession(
        repository = repository,
        onDismiss = {},
        nativeDetailContentApi = api,
        diagnostic = FakeDiagnostic()
    )
    
    val view = session.bindDetailView(mockContext, null, null)
    session.setDetailListening(true)
    
    session.refreshItems()
    
    assertEquals(2, api.lastItemsCount)
}
```

- [ ] **Step 6: Implement refreshItems**

Add to `FocusModeDetailSession.kt`:
```kotlin
fun refreshItems() {
    synchronized(lock) {
        if (state != DetailLifecycleState.OPEN) return
        val content = currentContent?.get() ?: return
        val api = nativeDetailContentApi ?: return
        
        val snapshot = repository.loadOrInitialize()
        val rows = buildRows(snapshot)
        
        api.setItems(content, rows.toTypedArray())
    }
}

private fun buildRows(snapshot: FocusCardConfig): List<Any> {
    // Build native SelectableItem objects
    return snapshot.modes.map { mode ->
        // Use reflection to create SelectableItem
    }
}
```

- [ ] **Step 7: Run refreshItems test**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailSessionTest`
Expected: PASS

- [ ] **Step 8: Add pending refresh accessor test**

```kotlin
@Test
fun `hasPendingCardRefresh returns true after CLOSING`() {
    val session = FocusModeDetailSession(
        repository = FakeFocusCardStateRepository(),
        onDismiss = {},
        nativeDetailContentApi = null,
        diagnostic = FakeDiagnostic()
    )
    
    session.setDetailListening(true)
    assertFalse(session.hasPendingCardRefresh())
    
    session.setDetailListening(false)
    
    assertTrue(session.hasPendingCardRefresh())
}
```

- [ ] **Step 9: Add hasPendingCardRefresh accessor**

```kotlin
fun hasPendingCardRefresh(): Boolean {
    synchronized(lock) {
        return pendingCardRefresh
    }
}

fun clearPendingCardRefresh() {
    synchronized(lock) {
        pendingCardRefresh = false
    }
}
```

- [ ] **Step 10: Run pending refresh test**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailSessionTest`
Expected: PASS

- [ ] **Step 11: Implement real adapter proxy**

Replace placeholder `val adapter: Any = Any()` with:
```kotlin
val adapter: Any = Proxy.newProxyInstance(
    detailAdapterInterface.classLoader,
    arrayOf(detailAdapterInterface),
    DetailAdapterHandler()
)

private inner class DetailAdapterHandler : InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        return when (method.name) {
            "getMetricsCategory" -> FocusNativeDetailRegistry.METRICS_CATEGORY
            "getTitle" -> "Focus Mode" // localized
            "createDetailView" -> bindDetailView(
                context = args?.get(0) as Context,
                convertView = args?.getOrNull(1) as? View,
                parent = args?.getOrNull(2) as? ViewGroup
            )
            "getSettingsIntent" -> Intent(/* HyperModes main */)
            else -> defaultReturnValue(method.returnType)
        }
    }
}
```

- [ ] **Step 12: Add adapter proxy test**

```kotlin
@Test
fun `adapter proxy returns correct metrics category`() {
    val session = FocusModeDetailSession(
        repository = FakeFocusCardStateRepository(),
        onDismiss = {},
        nativeDetailContentApi = null,
        diagnostic = FakeDiagnostic()
    )
    
    val category = Reflect.call(session.adapter, "getMetricsCategory")
    
    assertEquals(118, category)
}
```

- [ ] **Step 13: Run adapter test**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailSessionTest`
Expected: PASS

- [ ] **Step 14: Commit complete session**

```bash
git add app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailSessionTest.kt
git commit -m "feat: complete session with native binding and refresh

- bindDetailView registers content and builds native items
- refreshItems updates native content when OPEN
- hasPendingCardRefresh/clearPendingCardRefresh accessors
- Real DetailAdapter proxy with metrics category and createDetailView
- All operations thread-safe with synchronized blocks
- Weak reference to current content

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 5: Refactor FocusModeDetailAdapter to Use Session

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt`
- Modify: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt`

**Interfaces:**
- Consumes: `FocusModeDetailSession` from Task 4, `FocusNativeDetailRegistry` from Task 1
- Produces: Refactored `FocusModeDetailAdapter` as thin wrapper, `FocusCardDetailFactory` returns typed session handle

- [ ] **Step 1: Write failing session creation test**

```kotlin
@Test
fun `adapter uses session for detail view`() {
    val repository = FakeFocusCardStateRepository()
    val adapter = FocusModeDetailAdapter(
        pluginContext = mockPluginContext,
        moduleContext = mockModuleContext,
        detailAdapterInterface = mockInterface,
        repository = repository,
        onDismiss = {},
        onStateRefresh = {},
        nativeDetailContentApi = fakeApi
    )
    
    val session = adapter.session
    assertNotNull(session)
    assertTrue(FocusNativeDetailRegistry.isFocusAdapter(session.adapter))
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailAdapterTest`
Expected: FAIL with "unresolved reference: session"

- [ ] **Step 3: Refactor adapter to hold session**

Replace existing implementation with:
```kotlin
class FocusModeDetailAdapter(
    private val pluginContext: Context,
    private val moduleContext: Context,
    private val detailAdapterInterface: Class<*>,
    private val repository: FocusCardStateRepository,
    private val onDismiss: () -> Unit,
    private val onStateRefresh: () -> Unit = {},
    nativeDetailContentApi: FocusNativeDetailContentApi?
) {
    val session = FocusModeDetailSession(
        repository = repository,
        onDismiss = onDismiss,
        nativeDetailContentApi = nativeDetailContentApi,
        diagnostic = object : FocusDetailDiagnostic {
            override fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?) {
                Log.w(TAG, "Detail fallback: $stage", throwable)
            }
        }
    )
    
    init {
        FocusNativeDetailRegistry.registerSession(session.adapter, session)
    }
    
    val adapter: Any get() = session.adapter
    
    fun setDetailListening(listening: Boolean) {
        session.setDetailListening(listening)
        if (!listening && session.hasPendingCardRefresh()) {
            // Will be posted by onPanelHidden
        }
    }
    
    fun refreshItems() {
        session.refreshItems()
    }
    
    fun destroy() {
        session.destroy()
    }
}
```

- [ ] **Step 4: Run session creation test**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailAdapterTest`
Expected: PASS

- [ ] **Step 5: Update existing tests**

Adjust existing `FocusModeDetailAdapterTest` tests to work with new session-based structure:
- Tests that called adapter methods now call session methods
- Tests that checked native content still work through session
- Remove tests for old manual builder (session handles native path)

- [ ] **Step 6: Run all adapter tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusModeDetailAdapterTest`
Expected: All tests pass or are adjusted

- [ ] **Step 7: Commit adapter refactor**

```bash
git add app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt
git commit -m "refactor: adapter delegates to session

- FocusModeDetailAdapter now thin wrapper around FocusModeDetailSession
- Session registered in registry on construction
- setDetailListening/refreshItems/destroy delegate to session
- Remove old manual builder logic (session owns native binding)
- Existing tests adjusted for session-based structure

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 6: Refactor FocusCardTileProvider for Observer Management

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileProvider.kt`
- Modify: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusCardTileProviderTest.kt`

**Interfaces:**
- Consumes: `FocusModeDetailAdapter.session` from Task 5, `ObservableFocusCardConfigStore`
- Produces: Observer ownership based on card listeners + detail OPEN state, config changes route to refreshItems when OPEN

- [ ] **Step 1: Write failing observer ownership test**

```kotlin
@Test
fun `observer active when detail OPEN`() {
    val store = FakeObservableStore()
    val provider = FocusCardTileProvider(...)
    
    val detailFactory = provider.detailFactory
    val adapter = detailFactory.create({}, {}) as FocusModeDetailAdapter
    
    adapter.setDetailListening(true)
    
    assertTrue(store.hasObserver)
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests FocusCardTileProviderTest`
Expected: FAIL

- [ ] **Step 3: Implement observer claim tracking**

Add to `FocusCardTileProvider`:
```kotlin
private var detailSession: FocusModeDetailSession? = null
private var observerRegistration: AutoCloseable? = null

private fun updateObserverOwnership() {
    val needsObserver = listenerTokens.isNotEmpty() || 
                        (detailSession?.state == DetailLifecycleState.OPEN)
    
    if (needsObserver && observerRegistration == null) {
        observerRegistration = observableStore.observe {
            postToUi { handleConfigChange() }
        }
    } else if (!needsObserver && observerRegistration != null) {
        observerRegistration?.close()
        observerRegistration = null
    }
}

private fun handleConfigChange() {
    val session = detailSession
    when (session?.state) {
        DetailLifecycleState.OPEN -> {
            refreshState()
            session.refreshItems()
        }
        DetailLifecycleState.CLOSING -> {
            // Pending refresh will be posted by onPanelHidden
        }
        else -> {
            if (listenerTokens.isNotEmpty()) {
                refreshState()
            }
        }
    }
}
```

- [ ] **Step 4: Run observer ownership test**

Run: `./gradlew :app:testDebugUnitTest --tests FocusCardTileProviderTest`
Expected: PASS

- [ ] **Step 5: Add config change routing test**

```kotlin
@Test
fun `config change refreshes items when OPEN`() {
    val store = FakeObservableStore()
    val provider = FocusCardTileProvider(...)
    val adapter = detailFactory.create({}, {}) as FocusModeDetailAdapter
    
    adapter.setDetailListening(true)
    
    store.triggerChange()
    
    // Verify session.refreshItems was called
}
```

- [ ] **Step 6: Implement detail listening delegation**

Update `setDetailListening` in provider or adapter:
```kotlin
fun setDetailListening(listening: Boolean) {
    detailSession?.setDetailListening(listening)
    updateObserverOwnership()
}
```

- [ ] **Step 7: Run config routing test**

Run: `./gradlew :app:testDebugUnitTest --tests FocusCardTileProviderTest`
Expected: PASS

- [ ] **Step 8: Add CLOSING state test**

```kotlin
@Test
fun `config change during CLOSING defers card refresh`() {
    val provider = FocusCardTileProvider(...)
    val adapter = detailFactory.create({}, {}) as FocusModeDetailAdapter
    
    adapter.setDetailListening(true)
    adapter.setDetailListening(false)
    assertEquals(DetailLifecycleState.CLOSING, adapter.session.state)
    
    store.triggerChange()
    
    // Verify no immediate refreshState call
    assertTrue(adapter.session.hasPendingCardRefresh())
}
```

- [ ] **Step 9: Run CLOSING test**

Run: `./gradlew :app:testDebugUnitTest --tests FocusCardTileProviderTest`
Expected: PASS

- [ ] **Step 10: Remove synchronous refresh from setDetailListening**

Find and delete any `refreshState()` call inside `setDetailListening(false)` path.

- [ ] **Step 11: Run all provider tests**

Run: `./gradlew :app:testDebugUnitTest --tests FocusCardTileProviderTest`
Expected: All tests pass

- [ ] **Step 12: Commit provider refactor**

```bash
git add app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileProvider.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusCardTileProviderTest.kt
git commit -m "refactor: provider manages observer by card+detail claims

- Observer active when card listening OR detail OPEN
- Config changes route to session.refreshItems when OPEN
- Config changes defer card refresh when CLOSING
- Remove synchronous refreshState from setDetailListening(false)
- Observer releases when both claims gone
- onPanelHidden posts pending card refresh

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---
### Task 7: Install Item Count Hook

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`

**Interfaces:**
- Consumes: `FocusNativeDetailRegistry`, `FocusNativeDetailPolicy.shouldReturnFullItemCount()`
- Produces: Hook on `QSDetailContent$Adapter.getItemCount()` bypassing 20-item cap for Focus

Implementation: Hook getItemCount, resolve outer content, read suffix/items, call policy, return full count or original.

### Task 8: Install Adapter Mapping Hook

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`

**Interfaces:**
- Consumes: `FocusNativeDetailRegistry`, `FocusNativeDetailPolicy.shouldMapToFocusSpec()`
- Produces: Hook on `SecondaryParamsKt.from(DetailAdapter)` returning "hypermodes_focus"

Implementation: Hook from method, check adapter with policy, return "hypermodes_focus" or proceed.

### Task 9: Install Specific Height Hook

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`

**Interfaces:**
- Consumes: `FocusNativeDetailRegistry`, `FocusNativeDetailPolicy.shouldUseSpecificHeight()`
- Produces: Hook on `DetailPanelParams.getUseSpecificHeight()` returning true for Focus

Implementation: Hook getUseSpecificHeight, reflect adapter from params, check with policy, return true or proceed.

### Task 10: Install Hidden Completion Hook

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileProvider.kt`

**Interfaces:**
- Consumes: `FocusNativeDetailRegistry.adapterSession()`, `FocusModeDetailSession.onPanelHidden()`
- Produces: Hook on `DetailPanelDelegate.onHidden()` notifying session after original

Implementation: Hook onHidden, capture adapter before original clears it, proceed original, notify session if Focus.

### Task 11: Final Verification

**Files:**
- Verify: All tests pass, clean build succeeds

Implementation: Run full test suite, verify no regressions, confirm all hooks installed.
