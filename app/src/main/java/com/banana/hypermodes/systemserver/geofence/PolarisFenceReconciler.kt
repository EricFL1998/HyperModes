package com.banana.hypermodes.systemserver.geofence

/**
 * Fence reconciliation operations for syncing desired state to remote Polaris state.
 */
internal sealed interface PolarisFenceOperation {
    data class Add(val fence: PolarisFenceSpec) : PolarisFenceOperation
    data class Update(val fence: PolarisFenceSpec) : PolarisFenceOperation
    data class Delete(val fenceId: String) : PolarisFenceOperation
    data class Keep(val fence: PolarisFenceSpec) : PolarisFenceOperation
}

/**
 * Pure reconciliation planner for Polaris geofences.
 *
 * Compares desired configuration against remote state and produces operations
 * to sync them. Only manages fences with the HyperModes prefix; external fences
 * are never touched.
 *
 * Pure function with no Android dependencies or side effects.
 */
internal object PolarisFenceReconciler {
    /**
     * Generate reconciliation operations to sync desired state to remote state.
     *
     * Returns operations in deterministic order: deletes first (sorted by ID),
     * then adds/updates/keeps (sorted by ID).
     */
    fun plan(
        desired: Collection<PolarisFenceSpec>,
        remote: Collection<PolarisRemoteFence>
    ): List<PolarisFenceOperation> {
        val desiredById = desired.associateBy { it.fenceId }
        val managedRemote = remote
            .filter { it.fenceId.startsWith(PolarisContract.FENCE_PREFIX) }
            .associateBy { it.fenceId }

        return buildList {
            // Delete stale managed fences (sorted for determinism)
            (managedRemote.keys - desiredById.keys).sorted().forEach {
                add(PolarisFenceOperation.Delete(it))
            }

            // Add/Update/Keep desired fences (sorted for determinism)
            desiredById.toSortedMap().forEach { (id, spec) ->
                val current = managedRemote[id]
                add(when {
                    current == null -> PolarisFenceOperation.Add(spec)
                    !current.matches(spec) -> PolarisFenceOperation.Update(spec)
                    else -> PolarisFenceOperation.Keep(spec)
                })
            }
        }
    }

    /**
     * Check if remote fence matches the desired spec exactly.
     * Uses exact double equality because persisted coordinates pass through
     * without conversion.
     */
    private fun PolarisRemoteFence.matches(spec: PolarisFenceSpec): Boolean {
        return fenceId == spec.fenceId &&
            latitude == spec.latitude &&
            longitude == spec.longitude &&
            radiusMeters == spec.radiusMeters &&
            transitionType == spec.transitionType &&
            confidence == spec.confidence &&
            packageName == PolarisContract.CLIENT_PACKAGE
    }
}
