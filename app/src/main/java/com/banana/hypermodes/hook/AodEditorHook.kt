package com.banana.hypermodes.hook

import android.content.Intent
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * 放行 HyperModes 打开锁屏/桌面编辑器（com.miui.aod CommonEditorActivity）。
 *
 * EditorActivity.isMiuiCall() 用 Activity.getLaunchedFromPackage()（系统记录的
 * 真实启动方，Intent extra 无法伪造）做白名单校验（CALL_PACKAGE_ALLOW），
 * 非白名单调用 saveCallingSource 立即 finish()，编辑器闪退。
 *
 * 这里 hook EditorActivity.isMiuiCall()：当系统记录的真实启动方是 HyperModes 时
 * 返回 true，其它调用方走原逻辑，不影响系统原有行为。注意不依赖 Intent extra
 * launched_from_package（跨应用启动时该值不可靠），只认 getLaunchedFromPackage()。
 */
class AodEditorHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        val editorClass = try {
            classLoader.loadClass("com.miui.keyguard.editor.EditorActivity")
        } catch (t: Throwable) {
            log("EditorActivity not found: ${t.message}")
            return
        }
        val method = try {
            editorClass.declaredMethods.firstOrNull { it.name == "isMiuiCall" && it.parameterCount == 0 }
        } catch (t: Throwable) {
            null
        }
        if (method == null) {
            log("isMiuiCall not found")
            return
        }
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val caller = callingPackage(chain)
                    if (caller == Protocol.MODULE_PACKAGE) {
                        log("isMiuiCall: allowing $caller")
                        return true
                    }
                    return chain.proceed()
                }
            })
        log("isMiuiCall hooked")
    }

    private fun callingPackage(chain: XposedInterface.Chain): String? {
        val self = HookUtils.getThisObject(chain) ?: return null
        // 1) 系统记录的真实启动方（权威）：Activity.getLaunchedFromPackage()
        return try {
            self.javaClass.getMethod("getLaunchedFromPackage").invoke(self) as? String
        } catch (t: Throwable) {
            // 2) 兜底：Intent extra（仅当反射失败时使用）
            try {
                val getIntent = self.javaClass.getMethod("getIntent")
                (getIntent.invoke(self) as? Intent)?.getStringExtra("launched_from_package")
            } catch (t2: Throwable) {
                null
            }
        }
    }

    private fun log(msg: String) {
        HyperLog.i(TAG, msg)
        module.log(android.util.Log.WARN, TAG, msg)
    }

    companion object {
        private const val TAG = "HyperModes.AodEditor"
    }
}
