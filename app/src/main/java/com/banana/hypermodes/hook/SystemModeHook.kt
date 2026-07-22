package com.banana.hypermodes.hook

import android.app.NotificationChannel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Privileged-operation bridge running INSIDE system_server.
 *
 * The mode engine (our app process) can't call setPackagesSuspended or edit
 * other apps' notification channels — both need system permissions. This
 * hook captures ActivityManagerService.systemReady (mContext is ready by
 * then) and registers a dynamic receiver guarded by our signature
 * permission, so only HyperModes can dispatch:
 *
 * - ACTION_SET_PACKAGES_SUSPENDED: suspend/unsuspend packages via the
 *   "package" binder (IPackageManager.setPackagesSuspendedAsUser).
 * - ACTION_SET_CHANNELS_BYPASS_DND: set/clear bypass-Dnd on every channel
 *   of the given packages via the "notification" binder. Original per-channel
 *   bypass flags are remembered in memory and restored on clear.
 *
 * All binder calls are name-matched reflectively and best-effort: a renamed
 * method on a MIUI update logs a failure instead of crashing system_server.
 */
class SystemModeHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        val ams = try {
            classLoader.loadClass(AMS)
        } catch (t: Throwable) {
            log("ActivityManagerService not found: ${t.message}")
            return
        }
        val systemReady = ams.declaredMethods.firstOrNull { it.name == "systemReady" }
        if (systemReady == null) {
            log("systemReady not found")
            return
        }
        module.hook(systemReady)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val context = ams.getDeclaredField("mContext")
                            .apply { isAccessible = true }
                            .get(chain.thisObject) as Context
                        registerBridge(context)
                    } catch (t: Throwable) {
                        log("bridge registration failed: $t")
                    }
                    return result
                }
            })
        log("systemReady hooked for mode bridge")
    }

    private fun registerBridge(context: Context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                val packages = intent.getStringArrayExtra(Protocol.EXTRA_PACKAGES)
                    ?.toList() ?: return
                when (intent.action) {
                    Protocol.ACTION_SET_PACKAGES_SUSPENDED ->
                        setPackagesSuspended(
                            packages,
                            intent.getBooleanExtra(Protocol.EXTRA_SUSPENDED, false)
                        )
                    Protocol.ACTION_SET_CHANNELS_BYPASS_DND ->
                        setChannelsBypassDnd(
                            c, packages,
                            intent.getBooleanExtra(Protocol.EXTRA_BYPASS, false)
                        )
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Protocol.ACTION_SET_PACKAGES_SUSPENDED)
            addAction(Protocol.ACTION_SET_CHANNELS_BYPASS_DND)
        }
        context.registerReceiver(
            receiver, filter,
            Protocol.PERMISSION_CONTROL, null,
            Context.RECEIVER_EXPORTED
        )
        log("mode bridge receiver registered in system_server")
    }

    /** Original bypass flag per "pkg/channelId", captured before our first
     *  override so clearing restores rather than blindly writing false. */
    private val originalBypass = mutableMapOf<String, Boolean>()

    private fun setPackagesSuspended(packages: List<String>, suspended: Boolean) {
        try {
            val ipm = binder("package", "android.content.pm.IPackageManager\$Stub")
            val method = ipm.javaClass.methods.first {
                it.name == "setPackagesSuspendedAsUser"
            }
            // Signature across API levels:
            // (String[], boolean, PersistableBundle, PersistableBundle,
            //  SuspendDialogInfo, String callingPackage, int userId)
            val args = method.parameterTypes.map { t ->
                when {
                    t == Array<String>::class.java -> packages.toTypedArray()
                    t == Boolean::class.javaPrimitiveType -> suspended
                    t == Int::class.javaPrimitiveType -> 0 // userId: system user
                    t == String::class.java -> Protocol.MODULE_PACKAGE
                    else -> null
                }
            }.toTypedArray()
            method.invoke(ipm, *args)
            log("setPackagesSuspended($suspended): ${packages.joinToString()}")
        } catch (t: Throwable) {
            log("setPackagesSuspended failed: $t")
        }
    }

    private fun setChannelsBypassDnd(context: Context, packages: List<String>, bypass: Boolean) {
        val inm = try {
            binder("notification", "android.app.INotificationManager\$Stub")
        } catch (t: Throwable) {
            log("notification binder unavailable: $t")
            return
        }
        val getChannels = inm.javaClass.methods.firstOrNull {
            it.name == "getNotificationChannelsForPackage"
        }
        val updateChannel = inm.javaClass.methods.firstOrNull {
            it.name == "updateNotificationChannelForPackage"
        }
        if (getChannels == null || updateChannel == null) {
            log("channel methods not found on INotificationManager")
            return
        }
        for (pkg in packages) {
            try {
                val uid = context.packageManager.getPackageUid(pkg, 0)
                // (String pkg, int uid, boolean includeDeleted)
                val slice = getChannels.invoke(inm, pkg, uid, false)
                val channels = slice.javaClass.getMethod("getList")
                    .invoke(slice) as List<*>
                for (ch in channels) {
                    if (ch !is NotificationChannel) continue
                    val key = "$pkg/${ch.id}"
                    if (bypass) {
                        originalBypass.putIfAbsent(key, ch.canBypassDnd())
                        ch.setBypassDnd(true)
                    } else {
                        ch.setBypassDnd(originalBypass.remove(key) ?: false)
                    }
                    updateChannel.invoke(inm, pkg, uid, ch)
                }
                log("bypassDnd($bypass): $pkg (${channels.size} channels)")
            } catch (t: Throwable) {
                log("bypassDnd failed for $pkg: $t")
            }
        }
    }

    /** ServiceManager.getService(name) + Stub.asInterface(binder), reflectively. */
    private fun binder(service: String, stubClass: String): Any {
        val binder = Class.forName("android.os.ServiceManager")
            .getMethod("getService", String::class.java)
            .invoke(null, service) as IBinder
        return Class.forName(stubClass)
            .getMethod("asInterface", IBinder::class.java)
            .invoke(null, binder)
    }

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val AMS = "com.android.server.am.ActivityManagerService"
    }
}
