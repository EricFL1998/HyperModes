package com.banana.hypermodes.hook

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.automation.AutomationStore
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.reflect.Method

/**
 * HyperOS 4「AI 键」接线：把 MIUI 原生 AI 键的短按/长按接到 HyperModes。
 *
 * 原生链路（SecurityCoreAdd 设置页 → system_server）：
 *   GestureShortcutAiKeySettingsActivity 写 Settings.System:
 *     click_mi_ai_key / long_press_mi_ai_key
 *   MiAiKeyRule.HandleMiAiKey.onClick/onLongPress 读取该值
 *     → getClickFunction()/getLongPressFunction() 校验功能白名单
 *     → triggerFunction(function, action) 分发到 ShortCutActionsUtils
 *
 * 这里做三件事：
 *  1. getClickFunction/getLongPressFunction：放行 HyperModes 自定义功能码
 *     （原生实现会把不在白名单里的值改回默认函数）。
 *  2. AppStatusCheck.needFallbackToNone：避免设置变更时把自定义值重置成 "none"。
 *  3. triggerFunction：识别 hypermodes_* 并执行 HyperModes 动作（开关式）。
 *
 * 动作目标另存于 Settings.System（AI_KEY_TARGET_*），由选择页写入：
 *   app:<package> / mode:<modeId> / automation:<automationId>
 */
class AiKeyHook(private val module: XposedModule) {

    private var systemContext: Context? = null

    fun install(classLoader: ClassLoader) {
        val ruleClass = try {
            classLoader.loadClass(MI_AI_KEY_RULE)
        } catch (t: Throwable) {
            log("MiAiKeyRule not found (AI key unsupported on this build): ${t.message}")
            return
        }

        hookFunctionGetters(classLoader)
        hookFallbackGuard(classLoader)

        try {
            val trigger = ruleClass.getDeclaredMethod(
                "triggerFunction", String::class.java, String::class.java
            ).apply { isAccessible = true }
            module.hook(trigger)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val function = chain.getArg(0) as? String
                        val action = chain.getArg(1) as? String
                        if (function != null && function.startsWith(HYPERMODES_PREFIX)) {
                            val ok = executeHyperModesAction(function, action)
                            log("AI key $action -> $function (ok=$ok)")
                            return null // 不再走 MIUI 原生分发
                        }
                        return chain.proceed()
                    }
                })
            log("MiAiKeyRule.triggerFunction hooked")
        } catch (t: Throwable) {
            log("Failed to hook MiAiKeyRule.triggerFunction: ${t.message}")
        }
    }

    /** 让原生 getter 返回 HyperModes 功能码而不是白名单默认值。 */
    private fun hookFunctionGetters(classLoader: ClassLoader) {
        val handleClass = try {
            classLoader.loadClass(HANDLE_MI_AI_KEY)
        } catch (t: Throwable) {
            log("HandleMiAiKey not found: ${t.message}")
            return
        }
        hookGetter(handleClass, "getClickFunction", Protocol.AI_KEY_TARGET_CLICK)
        hookGetter(handleClass, "getLongPressFunction", Protocol.AI_KEY_TARGET_LONG)
    }

    /**
     * 自定义选择只保存在 HyperModes 的 target 键里（app:/mode:/automation:），
     * 不写原生 click_mi_ai_key —— 原生设置页会把不认识的功能码重置回默认值。
     */
    private fun hookGetter(handleClass: Class<*>, methodName: String, targetKey: String) {
        try {
            val method = handleClass.getDeclaredMethod(methodName).apply { isAccessible = true }
            module.hook(method)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        derivedFunction(targetKey)?.let { return it }
                        return chain.proceed()
                    }
                })
            log("$methodName hooked")
        } catch (t: Throwable) {
            log("Failed to hook $methodName: ${t.message}")
        }
    }

    /** target 键 → 功能码；没有自定义目标时返回 null，交还原生逻辑。 */
    private fun derivedFunction(targetKey: String): String? {
        val target = readSetting(targetKey)?.trim().orEmpty()
        return when {
            target.startsWith("app:") -> Protocol.AI_KEY_ACTION_APP
            target.startsWith("mode:") -> Protocol.AI_KEY_ACTION_MODE
            target.startsWith("automation:") -> Protocol.AI_KEY_ACTION_AUTOMATION
            else -> null
        }
    }

    /**
     * 原生 AppStatusCheck 会把不在白名单里的功能码重置为 "none"，
     * HyperModes 功能码需要显式放行。
     */
    private fun hookFallbackGuard(classLoader: ClassLoader) {
        val className = "$HANDLE_MI_AI_KEY\$AppStatusCheck"
        try {
            val cls = classLoader.loadClass(className)
            val method = cls.getDeclaredMethod(
                "needFallbackToNone", String::class.java, String::class.java
            ).apply { isAccessible = true }
            module.hook(method)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val function = chain.getArg(0) as? String
                        if (function != null && function.startsWith(HYPERMODES_PREFIX)) {
                            return null // 跳过原生重置逻辑
                        }
                        return chain.proceed()
                    }
                })
            log("AppStatusCheck.needFallbackToNone hooked")
        } catch (t: Throwable) {
            log("Failed to hook needFallbackToNone: ${t.message}")
        }
    }

    // ==================== 动作执行 ====================

    private fun executeHyperModesAction(function: String, action: String?): Boolean {
        val context = context() ?: return false
        val targetKey = when (action) {
            Protocol.AI_KEY_CLICK -> Protocol.AI_KEY_TARGET_CLICK
            Protocol.AI_KEY_LONG -> Protocol.AI_KEY_TARGET_LONG
            else -> return false
        }
        val target = readSetting(targetKey) ?: return false
        // AI 键分发线程不是主线程；模式/自动化执行涉及 Settings 与系统服务回调，
        // 统一切回主线程执行，避免跨线程状态竞争。
        mainHandler.post {
            val ok = when (function) {
                Protocol.AI_KEY_ACTION_APP -> toggleApp(context, target)
                Protocol.AI_KEY_ACTION_MODE -> toggleMode(target)
                Protocol.AI_KEY_ACTION_AUTOMATION -> toggleAutomation(context, target)
                else -> false
            }
            log("AI key $action -> $function target=$target (ok=$ok)")
        }
        return true
    }

    private val mainHandler by lazy {
        android.os.Handler(android.os.Looper.getMainLooper())
    }

    /** 应用：已在最前台则关闭（force-stop），否则拉起。 */
    private fun toggleApp(context: Context, target: String): Boolean {
        val pkg = target.removePrefix("app:").trim()
        if (pkg.isEmpty()) return false
        if (foregroundPackage(context) == pkg) {
            return forceStop(context, pkg)
        }
        val launch = context.packageManager.getLaunchIntentForPackage(pkg) ?: return false
        launch.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        )
        return try {
            startActivityAsUser(context, launch)
            true
        } catch (t: Throwable) {
            log("launch $pkg failed: ${t.message}")
            false
        }
    }

    /** 模式：未激活 → activateMode，已激活 → deactivateMode。 */
    private fun toggleMode(target: String): Boolean {
        val modeId = target.removePrefix("mode:").trim()
        if (modeId.isEmpty()) return false
        return try {
            val engine = RoutineCoreEngine.getInstance()
            if (engine.getCurrentActiveMode()?.id == modeId) {
                engine.deactivateMode(modeId)
            } else {
                engine.activateMode(modeId)
            }
            true
        } catch (t: Throwable) {
            log("toggle mode $modeId failed: ${t.message}")
            false
        }
    }

    /** 自动化：切换 enabled 开关（启用 ↔ 停用）。 */
    private fun toggleAutomation(context: Context, target: String): Boolean {
        val id = target.removePrefix("automation:").trim()
        if (id.isEmpty()) return false
        return try {
            val automation = AutomationStore.load(context).firstOrNull { it.id == id } ?: return false
            AutomationStore.update(context, automation.copy(enabled = !automation.enabled))
            true
        } catch (t: Throwable) {
            log("toggle automation $id failed: ${t.message}")
            false
        }
    }

    // ==================== 系统辅助 ====================

    private fun readSetting(key: String): String? = try {
        val context = context() ?: return null
        // getStringForUser 是 hidden API，反射读取当前用户的值。
        val method = Settings.System::class.java.getMethod(
            "getStringForUser",
            android.content.ContentResolver::class.java,
            String::class.java,
            Int::class.javaPrimitiveType!!
        )
        method.invoke(null, context.contentResolver, key, currentUserId()) as? String
    } catch (t: Throwable) {
        null
    }

    private fun context(): Context? {
        systemContext?.let { return it }
        val ctx = try {
            val activityThread = Class.forName("android.app.ActivityThread")
            val current = activityThread.getMethod("currentActivityThread").invoke(null)
            activityThread.getMethod("getSystemContext").invoke(current) as? Context
        } catch (t: Throwable) {
            log("system context unavailable: ${t.message}")
            null
        }
        systemContext = ctx
        return ctx
    }

    private fun currentUserId(): Int = try {
        Class.forName("android.app.ActivityManager")
            .getMethod("getCurrentUser")
            .invoke(null) as? Int ?: 0
    } catch (t: Throwable) {
        0
    }

    private fun startActivityAsUser(context: Context, intent: Intent) {
        try {
            val userHandleClass = Class.forName("android.os.UserHandle")
            val userHandle = userHandleClass
                .getMethod("of", Int::class.javaPrimitiveType!!)
                .invoke(null, currentUserId())
            val method = Context::class.java.getMethod(
                "startActivityAsUser", Intent::class.java, userHandleClass
            )
            method.invoke(context, intent, userHandle)
        } catch (t: Throwable) {
            context.startActivity(intent)
        }
    }

    private fun foregroundPackage(context: Context): String? = try {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // getRunningTasks 是 hidden API（system_server 可用），编译期用反射调用。
        val method = ActivityManager::class.java.getMethod(
            "getRunningTasks", Int::class.javaPrimitiveType!!
        )
        @Suppress("UNCHECKED_CAST")
        val tasks = method.invoke(am, 1) as? List<Any>
        val top = tasks?.firstOrNull() ?: return null
        (top.javaClass.getMethod("getTopActivity").invoke(top) as? android.content.ComponentName)
            ?.packageName
    } catch (t: Throwable) {
        null
    }

    private fun forceStop(context: Context, pkg: String): Boolean = try {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val asUser = runCatching {
            ActivityManager::class.java.getMethod(
                "forceStopPackageAsUser", String::class.java, Int::class.javaPrimitiveType!!
            ).apply { isAccessible = true }
        }.getOrNull()
        if (asUser != null) {
            asUser.invoke(am, pkg, currentUserId())
        } else {
            val method = ActivityManager::class.java.getMethod(
                "forceStopPackage", String::class.java
            ).apply { isAccessible = true }
            method.invoke(am, pkg)
        }
        log("force-stopped $pkg")
        true
    } catch (t: Throwable) {
        log("force-stop $pkg failed: ${t.message}")
        false
    }

    private fun log(msg: String) = module.log(Log.WARN, TAG, msg)

    companion object {
        private const val TAG = "HyperModes.AiKey"
        private const val MI_AI_KEY_RULE =
            "com.android.server.input.shortcut.singlekeyrule.MiAiKeyRule"
        private const val HANDLE_MI_AI_KEY = "$MI_AI_KEY_RULE\$HandleMiAiKey"
        private const val HYPERMODES_PREFIX = "hypermodes_"
    }
}
