package com.banana.hypermodes.systemserver.geofence

import android.content.ComponentName
import com.xiaomi.gnss.polaris.geofence.IMiGeoManagerService
import com.xiaomi.gnss.polaris.geofence.MiGeofence

/**
 * Testable façade over Polaris IMiGeoManagerService.
 * Hard-codes client package to "android" for all operations.
 */
internal interface PolarisGeoService {
    fun registerComponent(component: ComponentName?)
    fun list(): List<PolarisRemoteFence>
    fun add(fence: PolarisFenceSpec): String?
    fun update(fence: PolarisFenceSpec)
    fun deleteById(fenceId: String)
    fun findById(fenceId: String): PolarisRemoteFence?
    fun status(fenceId: String): Int
    fun isAlive(): Boolean
}

/**
 * AIDL implementation that wraps IMiGeoManagerService.
 * Always uses PolarisContract.CLIENT_PACKAGE ("android") for Binder calls.
 */
internal class AidlPolarisGeoService(
    private val service: IMiGeoManagerService
) : PolarisGeoService {

    override fun registerComponent(component: ComponentName?) {
        service.registerComponent(PolarisContract.CLIENT_PACKAGE, component)
    }

    override fun list(): List<PolarisRemoteFence> {
        return try {
            service.listGeofence(PolarisContract.CLIENT_PACKAGE)
                ?.mapNotNull { it.toRemoteFence() }
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun add(fence: PolarisFenceSpec): String? {
        return try {
            val miGeofence = fence.toMiGeofence()
            service.addGeofence(PolarisContract.CLIENT_PACKAGE, miGeofence)
        } catch (e: Exception) {
            null
        }
    }

    override fun update(fence: PolarisFenceSpec) {
        try {
            val miGeofence = fence.toMiGeofence()
            service.updateGeofence(PolarisContract.CLIENT_PACKAGE, miGeofence)
        } catch (e: Exception) {
            // Update failure is non-fatal; next reconciliation will retry
        }
    }

    override fun deleteById(fenceId: String) {
        try {
            service.deleteGeofenceById(PolarisContract.CLIENT_PACKAGE, fenceId)
        } catch (e: Exception) {
            // Delete failure is non-fatal
        }
    }

    override fun findById(fenceId: String): PolarisRemoteFence? {
        return try {
            service.findGeofenceById(PolarisContract.CLIENT_PACKAGE, fenceId)
                ?.toRemoteFence()
        } catch (e: Exception) {
            null
        }
    }

    override fun status(fenceId: String): Int {
        return try {
            // Note: IMiGeoManagerService doesn't have getGeofenceStatus method
            // Return UNKNOWN for now - status tracking should be done locally
            PolarisContract.STATUS_UNKNOWN
        } catch (e: Exception) {
            PolarisContract.STATUS_UNKNOWN
        }
    }

    override fun isAlive(): Boolean {
        return try {
            service.asBinder()?.isBinderAlive ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun PolarisFenceSpec.toMiGeofence(): MiGeofence {
        return MiGeofence().apply {
            setId(fenceId)
            setLatitude(this@toMiGeofence.latitude)
            setLongitude(this@toMiGeofence.longitude)
            setRadius(radiusMeters)
            setTransitionType(this@toMiGeofence.transitionType)
            setConfidence(this@toMiGeofence.confidence)
            // Note: packageName has no setter - it's set by Polaris service
        }
    }

    private fun MiGeofence.toRemoteFence(): PolarisRemoteFence? {
        // Null ID means invalid fence
        val fenceId = this.id ?: return null
        return PolarisRemoteFence(
            fenceId = fenceId,
            latitude = this.latitude,
            longitude = this.longitude,
            radiusMeters = this.radius,
            transitionType = this.transitionType,
            confidence = this.confidence,
            packageName = this.packageName
        )
    }
}
