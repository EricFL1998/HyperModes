# Focus Tile Implementation: Hook vs Custom APK

## Overview
You have **two main approaches** to add an iOS-style Focus tile to HyperOS:

1. **Using Xposed/LSPosed Hooks** (Recommended for HyperModes)
2. **Custom SystemUI Plugin APK** (Alternative approach)

## Approach 1: Using Xposed Hooks (✅ Recommended)

Since HyperModes is already an LSPosed module, you can hook into SystemUI to inject the Focus tile dynamically.

### Advantages:
- ✅ No need to modify system APKs
- ✅ Works across ROM updates
- ✅ Easy to install/uninstall
- ✅ Can update without flashing
- ✅ Already have the infrastructure in place
- ✅ Users just install the module

### Disadvantages:
- ⚠️ Requires LSPosed framework
- ⚠️ Need to hook complex SystemUI code
- ⚠️ May break on major Android updates

### Implementation Strategy

#### Step 1: Create SystemUI Control Center Hook

Add a new hook file: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterHook.kt`

```kotlin
package com.banana.hypermodes.hook

import android.content.Context
import android.util.Log
import com.banana.hypermodes.tile.FocusTileProvider
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Hook to inject Focus tile into MIUI Control Center
 */
class ControlCenterHook(private val module: XposedModule) {

    companion object {
        private const val TAG = "HyperModes.ControlCenterHook"
    }

    fun install(classLoader: ClassLoader) {
        try {
            // Hook the QSHost to inject our tile factory
            hookQSTileFactory(classLoader)
            
            // Hook the tile specs to add our tile to the list
            hookTileSpecs(classLoader)
            
            log("Control Center hook installed successfully")
        } catch (t: Throwable) {
            log("Failed to install Control Center hook: ${t.message}", t)
        }
    }

    /**
     * Hook QSTileFactory to create our custom Focus tile
     */
    private fun hookQSTileFactory(classLoader: ClassLoader) {
        try {
            // Find the QSTile factory class
            val qsTileFactoryClass = classLoader.loadClass(
                "com.android.systemui.qs.tileimpl.QSTileFactory"
            )
            
            // Hook createTile method
            val createTileMethod = qsTileFactoryClass.getDeclaredMethod(
                "createTile",
                String::class.java
            )
            
            module.hook(createTileMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val tileSpec = chain.args[0] as? String
                        
                        // If requesting our Focus tile, create it
                        if (tileSpec == "focus") {
                            log("Creating Focus tile")
                            return createFocusTile(chain.thisObject, classLoader)
                        }
                        
                        // Otherwise proceed normally
                        return chain.proceed()
                    }
                })
            
            log("QSTileFactory hooked successfully")
        } catch (t: Throwable) {
            log("Failed to hook QSTileFactory: ${t.message}", t)
        }
    }

    /**
     * Hook tile specs to include our Focus tile in the available tiles list
     */
    private fun hookTileSpecs(classLoader: ClassLoader) {
        try {
            // Hook where default tiles are defined
            val qsControllerClass = classLoader.loadClass(
                "com.android.systemui.qs.QSTileHost"
            )
            
            // Hook loadTileSpecs or similar method that returns available tiles
            val loadTileSpecsMethod = qsControllerClass.getDeclaredMethod("loadTileSpecs")
            
            module.hook(loadTileSpecsMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        
                        // Add our focus tile spec if not already present
                        if (result is List<*>) {
                            val specs = result.toMutableList()
                            if (!specs.contains("focus")) {
                                specs.add("focus")
                            }
                            return specs
                        }
                        
                        return result
                    }
                })
            
            log("Tile specs hooked successfully")
        } catch (t: Throwable) {
            log("Failed to hook tile specs: ${t.message}", t)
        }
    }

    /**
     * Create an instance of our Focus tile using reflection
     */
    private fun createFocusTile(factory: Any, classLoader: ClassLoader): Any? {
        try {
            // Get QSHost from factory
            val hostField = factory.javaClass.getDeclaredField("mHost")
            hostField.isAccessible = true
            val host = hostField.get(factory)
            
            // Get Context
            val contextField = factory.javaClass.getDeclaredField("mContext")
            contextField.isAccessible = true
            val context = contextField.get(factory) as Context
            
            // Create our Focus tile provider
            val focusTileProvider = FocusTileProvider(context, module)
            
            // Create the tile using the provider
            return focusTileProvider.createTile(host, classLoader)
        } catch (t: Throwable) {
            log("Failed to create Focus tile: ${t.message}", t)
            return null
        }
    }

    private fun log(msg: String, t: Throwable? = null) {
        if (t != null) {
            Log.e(TAG, msg, t)
        } else {
            Log.i(TAG, msg)
        }
    }
}
```

#### Step 2: Create Focus Tile Provider

Add: `app/src/main/java/com/banana/hypermodes/tile/FocusTileProvider.kt`

```kotlin
package com.banana.hypermodes.tile

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.libxposed.api.XposedModule
import java.lang.reflect.Proxy

/**
 * Provider that creates our Focus tile by implementing QSTile interface
 */
class FocusTileProvider(
    private val context: Context,
    private val module: XposedModule
) {
    companion object {
        private const val TAG = "HyperModes.FocusTileProvider"
    }
    
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun createTile(host: Any, classLoader: ClassLoader): Any {
        try {
            // Load QSTile interface
            val qsTileInterface = classLoader.loadClass("com.android.systemui.plugins.qs.QSTile")
            
            // Create a proxy that implements QSTile
            return Proxy.newProxyInstance(
                classLoader,
                arrayOf(qsTileInterface)
            ) { proxy, method, args ->
                when (method.name) {
                    "getTileLabel" -> getTileLabel()
                    "getState" -> getState(classLoader)
                    "handleClick" -> handleClick()
                    "handleSecondaryClick" -> handleSecondaryClick()
                    "handleLongClick" -> handleLongClick()
                    "isAvailable" -> true
                    "getDetailAdapter" -> getDetailAdapter(classLoader)
                    "getLongClickIntent" -> getLongClickIntent()
                    "getMetricsCategory" -> 118 // Same as DND
                    else -> {
                        Log.d(TAG, "Unhandled method: ${method.name}")
                        null
                    }
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to create tile proxy", t)
            throw t
        }
    }

    private fun getTileLabel(): String {
        val currentMode = getCurrentFocusMode()
        return currentMode?.name ?: "Focus"
    }

    private fun getState(classLoader: ClassLoader): Any {
        // Create a QSTile.State object
        val stateClass = classLoader.loadClass("com.android.systemui.plugins.qs.QSTile\$BooleanState")
        val state = stateClass.getDeclaredConstructor().newInstance()
        
        val isActive = isAnyModeActive()
        val currentMode = getCurrentFocusMode()
        
        // Set state fields using reflection
        stateClass.getField("value").set(state, isActive)
        stateClass.getField("state").setInt(state, if (isActive) 2 else 1) // ACTIVE : INACTIVE
        stateClass.getField("label").set(state, getTileLabel())
        
        if (currentMode != null) {
            stateClass.getField("secondaryLabel").set(state, currentMode.description)
        }
        
        return state
    }

    private fun handleClick() {
        Log.d(TAG, "Focus tile clicked")
        val currentMode = getCurrentFocusMode()
        
        if (currentMode != null) {
            // Turn off current mode
            setModeActive(currentMode.id, false)
        } else {
            // No mode active - could show detail view or activate last mode
            // For now, do nothing (user should long press to choose)
        }
    }

    private fun handleSecondaryClick() {
        Log.d(TAG, "Focus tile long pressed - showing detail")
        // This should trigger detail view
        // The system will call getDetailAdapter()
    }

    private fun handleLongClick() {
        handleSecondaryClick()
    }

    private fun getDetailAdapter(classLoader: ClassLoader): Any? {
        // Create detail adapter that shows list of focus modes
        return FocusDetailAdapter(context, classLoader, notificationManager).create()
    }

    private fun getLongClickIntent(): Intent {
        return Intent("android.settings.ZEN_MODE_SETTINGS")
    }

    // Helper methods to interact with system zen modes
    private fun getCurrentFocusMode(): FocusMode? {
        val zenMode = notificationManager.zenMode
        if (zenMode == android.provider.Settings.Global.ZEN_MODE_OFF) {
            return null
        }
        
        // Find active rule
        val rules = notificationManager.automaticZenRules
        for ((id, rule) in rules) {
            if (notificationManager.getAutomaticZenRuleState(id) == 
                android.service.notification.Condition.STATE_TRUE) {
                return FocusMode(id, rule.name, rule.triggerDescription ?: "")
            }
        }
        
        return null
    }

    private fun isAnyModeActive(): Boolean {
        return notificationManager.zenMode != android.provider.Settings.Global.ZEN_MODE_OFF
    }

    private fun setModeActive(ruleId: String, active: Boolean) {
        try {
            val rule = notificationManager.getAutomaticZenRule(ruleId) ?: return
            
            if (active) {
                if (!rule.isEnabled) {
                    rule.isEnabled = true
                    notificationManager.updateAutomaticZenRule(ruleId, rule, true)
                }
                
                val condition = android.service.notification.Condition(
                    rule.conditionId,
                    rule.name,
                    android.service.notification.Condition.STATE_TRUE
                )
                notificationManager.setAutomaticZenRuleState(ruleId, condition)
            } else {
                val condition = android.service.notification.Condition(
                    rule.conditionId,
                    rule.name,
                    android.service.notification.Condition.STATE_FALSE
                )
                notificationManager.setAutomaticZenRuleState(ruleId, condition)
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to set mode active", t)
        }
    }

    data class FocusMode(
        val id: String,
        val name: String,
        val description: String
    )
}
```

#### Step 3: Create Detail Adapter for Expandable View

Add: `app/src/main/java/com/banana/hypermodes/tile/FocusDetailAdapter.kt`

```kotlin
package com.banana.hypermodes.tile

import android.app.NotificationManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.lang.reflect.Proxy

/**
 * Detail adapter for showing all focus modes in expandable panel
 */
class FocusDetailAdapter(
    private val context: Context,
    private val classLoader: ClassLoader,
    private val notificationManager: NotificationManager
) {
    
    fun create(): Any {
        val detailAdapterInterface = classLoader.loadClass(
            "com.android.systemui.plugins.qs.DetailAdapter"
        )
        
        return Proxy.newProxyInstance(
            classLoader,
            arrayOf(detailAdapterInterface)
        ) { proxy, method, args ->
            when (method.name) {
                "getTitle" -> "Focus"
                "getToggleState" -> isAnyModeActive()
                "setToggleState" -> setToggleState(args[0] as Boolean)
                "createDetailView" -> createDetailView(args[0] as Context, args[1] as? View, args[2] as ViewGroup)
                "getMetricsCategory" -> 118
                "getSettingsIntent" -> android.content.Intent("android.settings.ZEN_MODE_SETTINGS")
                else -> null
            }
        }
    }

    private fun createDetailView(context: Context, convertView: View?, parent: ViewGroup): View {
        // Create a simple vertical list of all focus modes
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        
        // Get all automatic zen rules
        val rules = notificationManager.automaticZenRules
        
        for ((id, rule) in rules) {
            val isActive = notificationManager.getAutomaticZenRuleState(id) == 
                android.service.notification.Condition.STATE_TRUE
            
            val itemView = createModeItemView(context, rule.name, rule.triggerDescription ?: "", isActive) {
                // Toggle this mode
                toggleMode(id, !isActive)
                // Recreate view to update state
                (layout.parent as? ViewGroup)?.removeAllViews()
                (layout.parent as? ViewGroup)?.addView(createDetailView(context, null, parent))
            }
            
            layout.addView(itemView)
        }
        
        return layout
    }

    private fun createModeItemView(
        context: Context, 
        name: String, 
        description: String, 
        isActive: Boolean,
        onClick: () -> Unit
    ): View {
        // Create a simple view for each mode
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(if (isActive) 0xFF4A7D9D.toInt() else 0xFF3C3C3C.toInt())
            setOnClickListener { onClick() }
            
            addView(TextView(context).apply {
                text = name
                textSize = 16f
                setTextColor(0xFFFFFFFF.toInt())
            })
            
            addView(TextView(context).apply {
                text = description
                textSize = 12f
                setTextColor(0xFFCCCCCC.toInt())
            })
        }
    }

    private fun isAnyModeActive(): Boolean {
        return notificationManager.zenMode != android.provider.Settings.Global.ZEN_MODE_OFF
    }

    private fun setToggleState(active: Boolean) {
        if (!active) {
            // Turn off all modes
            val rules = notificationManager.automaticZenRules
            for ((id, rule) in rules) {
                toggleMode(id, false)
            }
        }
    }

    private fun toggleMode(ruleId: String, active: Boolean) {
        try {
            val rule = notificationManager.getAutomaticZenRule(ruleId) ?: return
            
            val condition = android.service.notification.Condition(
                rule.conditionId,
                rule.name,
                if (active) android.service.notification.Condition.STATE_TRUE 
                else android.service.notification.Condition.STATE_FALSE
            )
            notificationManager.setAutomaticZenRuleState(ruleId, condition)
        } catch (t: Throwable) {
            android.util.Log.e("FocusDetailAdapter", "Failed to toggle mode", t)
        }
    }
}
```

#### Step 4: Register the Hook in XposedInit

Update `XposedInit.kt`:

```kotlin
"com.android.systemui" -> {
    SystemUIHook(this).install(param.classLoader)
    ControlCenterHook(this).install(param.classLoader)  // Add this line
    log(Log.INFO, TAG, "hook installed for ${param.packageName}")
}
```

---

## Approach 2: Custom SystemUI Plugin APK

This involves modifying the actual MiuiSystemUIPlugin.apk.

### Advantages:
- ✅ Direct integration, no hooks needed
- ✅ Better performance (no reflection overhead)
- ✅ Easier to maintain tile logic
- ✅ More stable across updates

### Disadvantages:
- ⚠️ Requires decompiling and recompiling system APK
- ⚠️ Need to flash modified APK as system app
- ⚠️ Signature issues - may need Magisk module
- ⚠️ Breaks on ROM updates
- ⚠️ Harder to distribute to users

### Implementation

Follow the guide in `FocusTileImplementation_Dynamic.md`, but:

1. Decompile MiuiSystemUIPlugin.apk
2. Add the FocusTile Java classes directly
3. Register in `LocalMiuiQSTilePlugin.java`
4. Recompile and sign
5. Create Magisk module to install

---

## Recommendation

**Use Approach 1 (Xposed Hooks)** because:

1. ✅ HyperModes is already an LSPosed module
2. ✅ Much easier for users to install
3. ✅ Can be updated via app updates
4. ✅ Doesn't require system modifications
5. ✅ Compatible with the existing architecture

The hook approach integrates perfectly with your existing HyperModes infrastructure!

---

## Next Steps

1. Create the three new files:
   - `ControlCenterHook.kt`
   - `FocusTileProvider.kt`
   - `FocusDetailAdapter.kt`

2. Update `XposedInit.kt` to register the hook

3. Build and install the module

4. Enable in LSPosed for SystemUI

5. Reboot and test!
