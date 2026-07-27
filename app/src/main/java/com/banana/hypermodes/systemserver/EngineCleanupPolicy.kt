package com.banana.hypermodes.systemserver

object EngineCleanupPolicy {

    enum class Step {
        CANCEL_SCHEDULES,
        UNREGISTER_CONFIG_OBSERVER,
        UNREGISTER_TRIGGERS,
        REVERT_ACTIVE_MODE,
        DISABLE_DESKCLOCK_BEDTIME,
        CLEAR_MEMORY_STATE,
        REMOVE_GLOBAL_CONFIG
    }

    val packageRemovalOrder: List<Step> = listOf(
        Step.CANCEL_SCHEDULES,
        Step.UNREGISTER_CONFIG_OBSERVER,
        Step.UNREGISTER_TRIGGERS,
        Step.REVERT_ACTIVE_MODE,
        Step.DISABLE_DESKCLOCK_BEDTIME,
        Step.CLEAR_MEMORY_STATE,
        Step.REMOVE_GLOBAL_CONFIG
    )
}
