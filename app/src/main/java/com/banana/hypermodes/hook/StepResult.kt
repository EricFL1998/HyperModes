package com.banana.hypermodes.hook

/** Outcome of one internal-API step; formatted into the result broadcast. */
data class StepResult(val name: String, val success: Boolean, val detail: String = "") {
    fun format(): String = if (success) "$name: OK" else "$name: FAIL: $detail"

    companion object {
        fun ok(name: String) = StepResult(name, true)
        fun fail(name: String, e: Throwable) =
            StepResult(name, false, e.message ?: e.javaClass.simpleName)
        fun fail(name: String, detail: String) = StepResult(name, false, detail)
    }
}
