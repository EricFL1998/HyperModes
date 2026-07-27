package com.banana.hypermodes.systemserver

object PackagePresencePolicy {

    enum class MissingPackageAction {
        SKIP_ONLY,
        SHUTDOWN,
        ALLOW
    }

    fun onMissingPackage(
        state: RoutineCoreEngine.LifecycleState
    ): MissingPackageAction {
        return when (state) {
            RoutineCoreEngine.LifecycleState.RUNNING -> MissingPackageAction.SHUTDOWN
            RoutineCoreEngine.LifecycleState.REPLACING -> MissingPackageAction.SKIP_ONLY
            RoutineCoreEngine.LifecycleState.REMOVED -> MissingPackageAction.ALLOW
        }
    }
}
