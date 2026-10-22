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
     * 原生 o(String) 会把不认识的值回退成默认功能（下拉框显示默认项），
     * HyperModes 值需要原样返回。
     */
    private fun hookValueGetter(cls: Class<*>) {
        try {
            val method = cls.getDeclaredMethod("o", String::class.java).apply { isAccessible = true }
            module.hook(method)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val key = chain.getArg(0) as? String
                        if (key != null) {
                            val raw = readSetting(chain, key)
                            if (raw != null && raw.startsWith(HYPERMODES_PREFIX)) return raw
                        }
                        return chain.proceed()
                    }
                })
        } catch (t: Throwable) {
            log("Failed to hook value getter: ${t.message}")
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
            val listenerInterface = classLoader.loadClass(
                "androidx.preference.Preference\$OnPreferenceChangeListener"
            )
            val getListener = prefClass.getMethod("getOnPreferenceChangeListener")
            val setListener = prefClass.getMethod(
                "setOnPreferenceChangeListener",
                listenerInterface
            )
            val original = runCatching { getListener.invoke(pref) }.getOrNull()
            val changeMethod = listenerInterface.methods.firstOrNull {
                it.name == "onPreferenceChange"
            } ?: return

            val proxy = Proxy.newProxyInstance(
                classLoader, arrayOf(listenerInterface)
            ) { proxyObj, method, args ->
                when (method.name) {
                    "onPreferenceChange" -> {
                        val newValue = args?.getOrNull(1) as? String
                        if (newValue != null && newValue.startsWith(HYPERMODES_PREFIX)) {
                            launchPicker(fragment, press)
                            false
                        } else {
                            val result = original?.let {
                                runCatching {
                                    changeMethod.invoke(it, args?.getOrNull(0), args?.getOrNull(1))
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
        } catch (t: Throwable) {
            log("installChangeListener failed: ${t.message}")
        }
    }

    /** 刷新下拉框选中值 + 摘要（原生 onResume 会调用 o()，这里补摘要）。 */
    private fun refreshSelection(pref: Any, fragment: Any, settingKey: String, press: String) {
        try {
            val value = readSettingFrom(fragment, settingKey)
            if (value != null && value.startsWith(HYPERMODES_PREFIX)) {
                invokeMethod(pref, "k", value)
                val labelKey = if (press == Protocol.AI_KEY_PRESS_LONG) {
                    Protocol.AI_KEY_LABEL_LONG
                } else {
                    Protocol.AI_KEY_LABEL_CLICK
                }
                val label = readSettingFrom(fragment, labelKey)
                val summary = buildSummary(value, label, press)
                prefClass_setSummary(pref, summary)
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
            Protocol.AI_KEY_ACTION_MODE -> "开关模式"
            else -> "开关自动化"
        }
        val name = label?.takeIf { it.isNotBlank() } ?: "未选择"
        return if (press == Protocol.AI_KEY_PRESS_CLICK) {
            "$action：$name（长按可另行设置）"
        } else {
            "$action：$name"
        }
    }

    private fun launchPicker(fragment: Any, press: String) {
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
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            activity.startActivity(intent)
        } catch (t: Throwable) {
            log("launchPicker failed: ${t.message}")
        }
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
        private val HYPERMODE_OPTIONS = listOf(
            "打开应用" to Protocol.AI_KEY_ACTION_APP,
            "开关模式" to Protocol.AI_KEY_ACTION_MODE,
            "开关自动化" to Protocol.AI_KEY_ACTION_AUTOMATION
        )
    }
}
