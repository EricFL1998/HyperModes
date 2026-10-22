package com.banana.hypermodes.hook

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * 改造 SecurityCoreAdd 的原生「AI 键」设置页，让短按/长按可以选：
 *   打开应用 / 开关模式 / 开关自动化
 *
 * 原生页面：com.miui.miinput.gesture.aikey.GestureShortcutAiKeySettingsActivity$a
 *   - gesture_shortcut_ai_key_option1（短按）/ option2（长按）为 miuix DropDownPreference
 *   - 选项由内部 LinkedHashMap(标签→功能码) 生成，选中后写 Settings.System
 *
 * 实现方式：在 onCreatePreferences/onResume 之后把三个 HyperModes 选项追加到
 * 两个下拉框，并用自定义 OnPreferenceChangeListener 接管：
 *   - 选中 hypermodes_* → 拉起 AiKeyActionPickerActivity 选择具体目标，返回 false
 *   - 其余值 → 交还原生监听器
 */
class AiKeySettingsHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        val fragmentClass = try {
            classLoader.loadClass(FRAGMENT_CLASS)
        } catch (t: Throwable) {
            log("AI key settings fragment not found: ${t.message}")
            return
        }

        hookAfter(fragmentClass, "onCreatePreferences",
            arrayOf(BundleClass.BUNDLE, String::class.java)) { fragment ->
            decorate(fragment, classLoader)
        }
        hookAfter(fragmentClass, "onResume", emptyArray()) { fragment ->
            decorate(fragment, classLoader)
        }
        hookValueGetter(fragmentClass)
        hookDropDownValueSetter(classLoader)
        log("AI key settings hook installed")
    }

    /** hook 方法并在原逻辑执行后回调。 */
    private fun hookAfter(
        cls: Class<*>,
        methodName: String,
        params: Array<Class<*>>,
        after: (Any) -> Unit
    ) {
        try {
            val method = cls.getDeclaredMethod(methodName, *params).apply { isAccessible = true }
            module.hook(method)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        try {
                            HookUtils.getThisObject(chain)?.let(after)
                        } catch (t: Throwable) {
                            log("$methodName post-processing failed: ${t.message}")
                        }
                        return result
                    }
                })
        } catch (t: Throwable) {
            log("Failed to hook $methodName: ${t.message}")
        }
    }

    /**
     * 原生 o(String) 会把不认识的值回退成默认功能（下拉框显示默认项）。
     * 自定义选择只存在于 HyperModes target 键里，这里按 target 派生功能码。
     */
    private fun hookValueGetter(cls: Class<*>) {
        try {
            val method = cls.getDeclaredMethod("o", String::class.java).apply { isAccessible = true }
            module.hook(method)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val key = chain.getArg(0) as? String
                        val fragment = HookUtils.getThisObject(chain)
                        if (key != null && fragment != null) {
                            val press = pressForKey(key)
                            derivedFunction(fragment, press)?.let { return it }
                        }
                        return chain.proceed()
                    }
                })
        } catch (t: Throwable) {
            log("Failed to hook value getter: ${t.message}")
        }
    }

    /**
     * Hook DropDownPreference.k(String)：显示自定义项时只更新 UI，不落盘
     * click_mi_ai_key / long_press_mi_ai_key（避免被原生重置逻辑覆盖）。
     */
    private fun hookDropDownValueSetter(classLoader: ClassLoader) {
        try {
            val cls = classLoader.loadClass("miuix.preference.DropDownPreference")
            val method = cls.getDeclaredMethod("k", String::class.java).apply { isAccessible = true }
            module.hook(method)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val value = chain.getArg(0) as? String
                        if (value != null && value.startsWith(HYPERMODES_PREFIX)) {
                            // 只更新内部显示值，不调用 persistString。
                            val self = HookUtils.getThisObject(chain) ?: return chain.proceed()
                            findField(self.javaClass, "l")?.set(self, value)
                            findField(self.javaClass, "m")?.setBoolean(self, true)
                            invokeMethod(self, "notifyChanged")
                            return null
                        }
                        return chain.proceed()
                    }
                })
            log("DropDownPreference.k hooked")
        } catch (t: Throwable) {
            log("Failed to hook DropDownPreference.k: ${t.message}")
        }
    }

    private fun decorate(fragment: Any, classLoader: ClassLoader) {
        val screen = invokeNoArg(fragment, "getPreferenceScreen") ?: return
        val prefClass = runCatching {
            classLoader.loadClass("androidx.preference.Preference")
        }.getOrNull() ?: return
        val findPreference = fragment.javaClass.methods.firstOrNull {
            it.name == "findPreference" && it.parameterTypes.size == 1
        } ?: return

        applyToOption(
            screen, findPreference, fragment, classLoader, prefClass,
            key = "gesture_shortcut_ai_key_option1",
            press = Protocol.AI_KEY_PRESS_CLICK
        )
        applyToOption(
            screen, findPreference, fragment, classLoader, prefClass,
            key = "gesture_shortcut_ai_key_option2",
            press = Protocol.AI_KEY_PRESS_LONG
        )
    }

    private fun applyToOption(
        screen: Any,
        findPreference: java.lang.reflect.Method,
        fragment: Any,
        classLoader: ClassLoader,
        prefClass: Class<*>,
        key: String,
        press: String
    ) {
        val pref = runCatching { findPreference.invoke(fragment, key) }.getOrNull() ?: return
        if (!pref.javaClass.name.startsWith("miuix.preference")) return

        appendHyperModesEntries(pref)
        installChangeListener(pref, fragment, classLoader, prefClass, press)
        refreshSelection(pref, fragment, key, press)
    }

    /** 把「打开应用 / 开关模式 / 开关自动化」追加到下拉框的 entries/values。 */
    private fun appendHyperModesEntries(pref: Any) {
        try {
            val entriesField = findField(pref.javaClass, "o") ?: return
            val valuesField = findField(pref.javaClass, "p") ?: return
            val oldEntries = entriesField.get(pref) as? Array<CharSequence> ?: return
            val oldValues = valuesField.get(pref) as? Array<CharSequence> ?: return

            val newEntries = oldEntries.toMutableList()
            val newValues = oldValues.toMutableList()
            // 新选项插在「无」之前，保持原生「无」始终在列表最后。
            var insertAt = newValues.indexOfFirst { it.toString() == VALUE_NONE }
            if (insertAt < 0) insertAt = newValues.size
            var cursor = insertAt
            for ((label, value) in HYPERMODE_OPTIONS) {
                if (newValues.none { it.toString() == value }) {
                    newEntries.add(cursor, label)
                    newValues.add(cursor, value)
                    cursor++
                }
            }
            if (newValues.size == oldValues.size) return

            invokeMethod(pref, "h", arrayOf<CharSequence>(*newEntries.toTypedArray()))
            invokeMethod(pref, "i", arrayOf<CharSequence>(*newValues.toTypedArray()))
            // i() 在新版适配器下才写 p，兜底直接写字段。
            valuesField.set(pref, newValues.toTypedArray())
            log("Appended HyperModes AI key options to ${pref.javaClass.simpleName}")
        } catch (t: Throwable) {
            log("appendHyperModesEntries failed: ${t.message}")
        }
    }

    private fun installChangeListener(
        pref: Any,
        fragment: Any,
        classLoader: ClassLoader,
        prefClass: Class<*>,
        press: String
    ) {
        try {
            // SecurityCoreAdd 的 androidx.preference 被混淆：监听接口不是
            // Preference$OnPreferenceChangeListener，而是 androidx.preference.o。
            // 从 setOnPreferenceChangeListener 的参数类型动态取接口最稳。
            val setListener = prefClass.methods.firstOrNull {
                it.name == "setOnPreferenceChangeListener" &&
                    it.parameterTypes.size == 1
            } ?: run {
                log("setOnPreferenceChangeListener not found")
                return
            }
            val getListener = prefClass.methods.firstOrNull {
                it.name == "getOnPreferenceChangeListener" &&
                    it.parameterTypes.isEmpty()
            } ?: run {
                log("getOnPreferenceChangeListener not found")
                return
            }
            val listenerInterface = setListener.parameterTypes[0]
            val listenerLoader = listenerInterface.classLoader ?: classLoader
            val changeMethod = listenerInterface.methods.firstOrNull {
                it.name == "onPreferenceChange"
            } ?: listenerInterface.methods.firstOrNull {
                it.parameterTypes.size == 2
            } ?: run {
                log("onPreferenceChange not found on ${listenerInterface.name}")
                return
            }
            val original = runCatching { getListener.invoke(pref) }.getOrNull()

            val proxy = Proxy.newProxyInstance(
                listenerLoader, arrayOf(listenerInterface)
            ) { proxyObj, method, args ->
                when (method.name) {
                    changeMethod.name -> {
                        val newValue = args?.getOrNull(1) as? String
                        if (newValue != null && newValue.startsWith(HYPERMODES_PREFIX)) {
                            launchPicker(fragment, press, kindOf(newValue))
                            false
                        } else {
                            // 切回原生功能：清掉自定义目标，避免 hook 侧继续派生 HyperModes 动作。
                            clearTarget(fragment, press)
                            val result = original?.let {
                                runCatching {
                                    changeMethod.invoke(
                                        it, args?.getOrNull(0), args?.getOrNull(1)
                                    )
                                }.getOrNull()
                            }
                            result as? Boolean ?: true
                        }
                    }
                    "equals" -> proxyObj === args?.getOrNull(0)
                    "hashCode" -> System.identityHashCode(proxyObj)
                    "toString" -> "HyperModesAiKeyListener"
                    else -> null
                }
            }
            setListener.invoke(pref, proxy)
            log("change listener installed (interface=${listenerInterface.name})")
        } catch (t: Throwable) {
            log("installChangeListener failed: ${t.message}")
        }
    }

    /** 刷新下拉框选中值 + 摘要（原生 onResume 会调用 o()，这里补摘要）。 */
    private fun refreshSelection(pref: Any, fragment: Any, settingKey: String, press: String) {
        try {
            val function = derivedFunction(fragment, press)
            if (function != null) {
                invokeMethod(pref, "k", function)
                val label = readSettingFrom(fragment, labelKeyFor(press))
                prefClass_setSummary(pref, buildSummary(function, label, press))
            } else if (press == Protocol.AI_KEY_PRESS_LONG) {
                // 无自定义目标时恢复原生摘要（短按的原生描述由页面自身维护）。
                prefClass_setSummary(pref, "")
            }
        } catch (t: Throwable) {
            log("refreshSelection failed: ${t.message}")
        }
    }

    private fun prefClass_setSummary(pref: Any, text: String) {
        runCatching {
            pref.javaClass.getMethod("setSummary", CharSequence::class.java)
                .invoke(pref, text)
        }
    }

    private fun buildSummary(function: String, label: String?, press: String): String {
        val action = when (function) {
            Protocol.AI_KEY_ACTION_APP -> "打开应用"
            Protocol.AI_KEY_ACTION_MODE -> "模式"
            else -> "自动化"
        }
        val name = label?.takeIf { it.isNotBlank() } ?: "未选择"
        return if (press == Protocol.AI_KEY_PRESS_CLICK) {
            "$action：$name（长按可另行设置）"
        } else {
            "$action：$name"
        }
    }

    private fun launchPicker(fragment: Any, press: String, kind: String) {
        try {
            val activity = invokeNoArg(fragment, "getActivity") as? Context
                ?: invokeNoArg(fragment, "getContext") as? Context
                ?: return
            val intent = Intent().apply {
                setClassName(
                    Protocol.MODULE_PACKAGE,
                    "com.banana.hypermodes.ui.AiKeyActionPickerActivity"
                )
                putExtra(Protocol.AI_KEY_EXTRA_PRESS, press)
                putExtra(Protocol.AI_KEY_EXTRA_KIND, kind)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            // 让原生下拉框先收起，避免返回后弹层残留。
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                runCatching { activity.startActivity(intent) }
                    .onFailure { log("launchPicker failed: ${it.message}") }
            }, 150)
        } catch (t: Throwable) {
            log("launchPicker failed: ${t.message}")
        }
    }

    private fun targetKeyFor(press: String): String =
        if (press == Protocol.AI_KEY_PRESS_LONG) {
            Protocol.AI_KEY_TARGET_LONG
        } else {
            Protocol.AI_KEY_TARGET_CLICK
        }

    private fun labelKeyFor(press: String): String =
        if (press == Protocol.AI_KEY_PRESS_LONG) {
            Protocol.AI_KEY_LABEL_LONG
        } else {
            Protocol.AI_KEY_LABEL_CLICK
        }

    private fun pressForKey(key: String): String =
        when (key) {
            Protocol.AI_KEY_LONG, Protocol.AI_KEY_TARGET_LONG -> Protocol.AI_KEY_PRESS_LONG
            else -> Protocol.AI_KEY_PRESS_CLICK
        }

    /** target 键 → 功能码；无自定义目标返回 null。 */
    private fun derivedFunction(fragment: Any, press: String): String? {
        val target = readSettingFrom(fragment, targetKeyFor(press))?.trim().orEmpty()
        return when {
            target.startsWith("app:") -> Protocol.AI_KEY_ACTION_APP
            target.startsWith("mode:") -> Protocol.AI_KEY_ACTION_MODE
            target.startsWith("automation:") -> Protocol.AI_KEY_ACTION_AUTOMATION
            else -> null
        }
    }

    private fun clearTarget(fragment: Any, press: String) {
        runCatching {
            val resolver = (fragment.javaClass.methods.firstOrNull {
                it.name == "getContentResolver" && it.parameterTypes.isEmpty()
            }?.invoke(fragment) as? android.content.ContentResolver) ?: return
            writeSettingDirect(resolver, targetKeyFor(press), "")
            writeSettingDirect(resolver, labelKeyFor(press), "")
        }
    }

    private fun writeSettingDirect(
        resolver: android.content.ContentResolver,
        key: String,
        value: String
    ) = runCatching {
        val method = Settings.System::class.java.getMethod(
            "putStringForUser",
            android.content.ContentResolver::class.java,
            String::class.java,
            String::class.java,
            Int::class.javaPrimitiveType!!
        )
        method.invoke(null, resolver, key, value, currentUserId())
    }

    private fun currentUserId(): Int = try {
        Class.forName("android.app.ActivityManager")
            .getMethod("getCurrentUser").invoke(null) as? Int ?: 0
    } catch (t: Throwable) {
        0
    }

    /** 下拉框选中的功能码 → picker 目标类型。 */
    private fun kindOf(value: String): String = when (value) {
        Protocol.AI_KEY_ACTION_APP -> KIND_APP
        Protocol.AI_KEY_ACTION_MODE -> KIND_MODE
        else -> KIND_AUTOMATION
    }

    // ==================== 反射小工具 ====================

    private fun invokeNoArg(target: Any, name: String): Any? = runCatching {
        target.javaClass.methods.firstOrNull {
            it.name == name && it.parameterTypes.isEmpty()
        }?.invoke(target)
    }.getOrNull()

    private fun invokeMethod(target: Any, name: String, vararg args: Any?): Any? = runCatching {
        val method = target.javaClass.methods.firstOrNull {
            it.name == name && it.parameterTypes.size == args.size
        } ?: return@runCatching null
        method.isAccessible = true
        method.invoke(target, *args)
    }.getOrNull()

    private fun findField(cls: Class<*>, name: String): java.lang.reflect.Field? {
        var current: Class<*>? = cls
        while (current != null) {
            runCatching {
                return current!!.getDeclaredField(name).apply { isAccessible = true }
            }
            current = current.superclass
        }
        return null
    }

    private fun readSetting(chain: XposedInterface.Chain, key: String): String? {
        val fragment = HookUtils.getThisObject(chain) ?: return null
        return readSettingFrom(fragment, key)
    }

    private fun readSettingFrom(fragment: Any, key: String): String? = runCatching {
        val resolver = (fragment.javaClass.methods.firstOrNull {
            it.name == "getContentResolver" && it.parameterTypes.isEmpty()
        }?.invoke(fragment) as? android.content.ContentResolver) ?: return@runCatching null
        val userId = try {
            Class.forName("android.app.ActivityManager")
                .getMethod("getCurrentUser").invoke(null) as? Int ?: 0
        } catch (_: Throwable) {
            0
        }
        // getStringForUser 是 hidden API，反射读取。
        val method = Settings.System::class.java.getMethod(
            "getStringForUser",
            android.content.ContentResolver::class.java,
            String::class.java,
            Int::class.javaPrimitiveType!!
        )
        method.invoke(null, resolver, key, userId) as? String
    }.getOrNull()

    private fun log(msg: String) = module.log(Log.WARN, TAG, msg)

    private object BundleClass {
        val BUNDLE: Class<*> = android.os.Bundle::class.java
    }

    companion object {
        private const val TAG = "HyperModes.AiKeySettings"
        private const val FRAGMENT_CLASS =
            "com.miui.miinput.gesture.aikey.GestureShortcutAiKeySettingsActivity\$a"
        private const val HYPERMODES_PREFIX = "hypermodes_"
        private const val VALUE_NONE = "none"
        private const val KIND_APP = "app"
        private const val KIND_MODE = "mode"
        private const val KIND_AUTOMATION = "automation"
        private val HYPERMODE_OPTIONS = listOf(
            "打开应用" to Protocol.AI_KEY_ACTION_APP,
            "模式" to Protocol.AI_KEY_ACTION_MODE,
            "自动化" to Protocol.AI_KEY_ACTION_AUTOMATION
        )
    }
}
