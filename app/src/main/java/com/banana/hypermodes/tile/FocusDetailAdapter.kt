package com.banana.hypermodes.tile

import android.app.AutomaticZenRule
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.service.notification.Condition
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Detail adapter for showing all focus modes in expandable panel (iOS-style)
 *
 * Creates a DetailAdapter that displays all configured AutomaticZenRules
 * in a scrollable list, allowing users to activate/deactivate modes.
 */
class FocusDetailAdapter(
    private val context: Context,
    private val classLoader: ClassLoader,
    private val notificationManager: NotificationManager
) {
    companion object {
        private const val TAG = "HyperModes.FocusDetailAdapter"
    }

    fun create(): Any {
        try {
            log("Creating detail adapter")

            // Load DetailAdapter interface
            val detailAdapterInterface = classLoader.loadClass(
                "com.android.systemui.plugins.qs.DetailAdapter"
            )
            log("Loaded DetailAdapter interface")

            // Create invocation handler
            val handler = DetailAdapterInvocationHandler()

            // Create proxy
            val adapter = Proxy.newProxyInstance(
                classLoader,
                arrayOf(detailAdapterInterface),
                handler
            )

            log("Detail adapter created successfully")
            return adapter
        } catch (t: Throwable) {
            log("Failed to create detail adapter: ${t.message}", t)
            throw t
        }
    }

    private inner class DetailAdapterInvocationHandler : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            return try {
                when (method.name) {
                    "getTitle" -> "Focus"
                    "getToggleState" -> isAnyModeActive()
                    "setToggleState" -> {
                        if (args != null && args.isNotEmpty()) {
                            setToggleState(args[0] as Boolean)
                        }
                        null
                    }
                    "createDetailView" -> {
                        if (args != null && args.size >= 3) {
                            createDetailView(
                                args[0] as Context,
                                args[1] as? View,
                                args[2] as ViewGroup
                            )
                        } else {
                            null
                        }
                    }
                    "getMetricsCategory" -> 118
                    "getSettingsIntent" -> Intent("android.settings.ZEN_MODE_SETTINGS")
                    "getToggleEnabled" -> true
                    else -> {
                        log("Unhandled DetailAdapter method: ${method.name}")
                        null
                    }
                }
            } catch (t: Throwable) {
                log("Error in DetailAdapter.${method.name}: ${t.message}", t)
                null
            }
        }

        private fun createDetailView(context: Context, convertView: View?, parent: ViewGroup): View {
            try {
                log("Creating detail view")

                // Create main container
                val scrollView = android.widget.ScrollView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                val layout = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    val padding = dpToPx(context, 8)
                    setPadding(padding, padding, padding, padding)
                }

                // Get all automatic zen rules
                val rules = notificationManager.automaticZenRules

                if (rules.isNullOrEmpty()) {
                    // Show empty state
                    layout.addView(createEmptyStateView(context))
                } else {
                    // Add each mode as a card
                    var addedCount = 0
                    for ((id, rule) in rules) {
                        val isActive = try {
                            notificationManager.getAutomaticZenRuleState(id) == Condition.STATE_TRUE
                        } catch (e: Exception) {
                            false
                        }

                        val itemView = createModeItemView(context, id, rule, isActive)
                        layout.addView(itemView)
                        addedCount++

                        log("Added mode: ${rule.name} (active=$isActive)")
                    }
                    log("Added $addedCount focus modes to detail view")
                }

                scrollView.addView(layout)
                return scrollView
            } catch (t: Throwable) {
                log("Failed to create detail view: ${t.message}", t)
                // Return empty view on error
                return View(context)
            }
        }

        private fun createEmptyStateView(context: Context): View {
            return LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                val padding = dpToPx(context, 32)
                setPadding(padding, padding, padding, padding)

                addView(TextView(context).apply {
                    text = "No Focus Modes"
                    textSize = 18f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                })

                addView(TextView(context).apply {
                    text = "Create focus modes in Settings"
                    textSize = 14f
                    setTextColor(Color.parseColor("#CCCCCC"))
                    gravity = Gravity.CENTER
                    val topMargin = dpToPx(context, 8)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, topMargin, 0, 0)
                    }
                })
            }
        }

        private fun createModeItemView(
            context: Context,
            ruleId: String,
            rule: AutomaticZenRule,
            isActive: Boolean
        ): View {
            val itemLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                val margin = dpToPx(context, 8)
                val padding = dpToPx(context, 16)
                setPadding(padding, padding, padding, padding)

                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, margin, 0, 0)
                }

                // Set background color based on active state
                setBackgroundColor(
                    if (isActive) {
                        getColorForRuleType(rule.type, true)
                    } else {
                        Color.parseColor("#2C2C2C")
                    }
                )

                // Make it look like a card
                elevation = dpToPx(context, 2).toFloat()

                // Set click listener to toggle mode
                setOnClickListener {
                    log("Mode clicked: ${rule.name}, current active: $isActive")
                    toggleMode(ruleId, !isActive)
                }
            }

            // Title
            itemLayout.addView(TextView(context).apply {
                text = rule.name
                textSize = 16f
                setTextColor(Color.WHITE)
                setTypeface(null, android.graphics.Typeface.BOLD)
            })

            // Description
            val description = rule.triggerDescription ?: getDefaultDescription(rule.type)
            if (description.isNotEmpty()) {
                itemLayout.addView(TextView(context).apply {
                    text = description
                    textSize = 12f
                    setTextColor(Color.parseColor("#CCCCCC"))
                    val topMargin = dpToPx(context, 4)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, topMargin, 0, 0)
                    }
                })
            }

            return itemLayout
        }

        private fun getColorForRuleType(type: Int, isActive: Boolean): Int {
            if (!isActive) return Color.parseColor("#2C2C2C")

            return when (type) {
                AutomaticZenRule.TYPE_BEDTIME -> Color.parseColor("#5D7D9D") // Blue
                AutomaticZenRule.TYPE_DRIVING -> Color.parseColor("#9D7D4A") // Orange
                AutomaticZenRule.TYPE_THEATER -> Color.parseColor("#8B7D9D") // Purple
                AutomaticZenRule.TYPE_IMMERSIVE -> Color.parseColor("#6D7D8D") // Dark
                AutomaticZenRule.TYPE_MANAGED -> Color.parseColor("#5D9D7D") // Green
                AutomaticZenRule.TYPE_SCHEDULE_TIME,
                AutomaticZenRule.TYPE_SCHEDULE_CALENDAR -> Color.parseColor("#7D8B9D") // Gray-blue
                else -> Color.parseColor("#4A7D9D") // Default blue
            }
        }

        private fun getDefaultDescription(type: Int): String {
            return when (type) {
                AutomaticZenRule.TYPE_BEDTIME -> "Relax and get some rest"
                AutomaticZenRule.TYPE_DRIVING -> "Stay focused on the road"
                AutomaticZenRule.TYPE_THEATER -> "Enjoy the show"
                AutomaticZenRule.TYPE_IMMERSIVE -> "Fully immerse yourself"
                AutomaticZenRule.TYPE_MANAGED -> "Get things done"
                AutomaticZenRule.TYPE_SCHEDULE_TIME,
                AutomaticZenRule.TYPE_SCHEDULE_CALENDAR -> "Automatically activates on schedule"
                else -> "Custom focus mode"
            }
        }

        private fun isAnyModeActive(): Boolean {
            return try {
                val filter = notificationManager.currentInterruptionFilter
                filter != NotificationManager.INTERRUPTION_FILTER_ALL
            } catch (t: Throwable) {
                log("Failed to check if any mode active: ${t.message}", t)
                false
            }
        }

        private fun setToggleState(active: Boolean) {
            log("Toggle state changed: $active")
            if (!active) {
                // Turn off all modes
                try {
                    val rules = notificationManager.automaticZenRules ?: return
                    for ((id, _) in rules) {
                        toggleMode(id, false)
                    }
                } catch (t: Throwable) {
                    log("Failed to toggle off all modes: ${t.message}", t)
                }
            }
        }

        private fun toggleMode(ruleId: String, active: Boolean) {
            try {
                val rule = notificationManager.getAutomaticZenRule(ruleId)
                if (rule == null) {
                    log("Rule not found: $ruleId")
                    return
                }

                if (active) {
                    // Turn off other modes first
                    val rules = notificationManager.automaticZenRules ?: emptyMap()
                    for ((id, _) in rules) {
                        if (id != ruleId) {
                            try {
                                val otherRule = notificationManager.getAutomaticZenRule(id)
                                if (otherRule != null) {
                                    val condition = Condition(
                                        otherRule.conditionId,
                                        otherRule.name,
                                        Condition.STATE_FALSE
                                    )
                                    notificationManager.setAutomaticZenRuleState(id, condition)
                                }
                            } catch (e: Exception) {
                                log("Failed to turn off mode $id: ${e.message}")
                            }
                        }
                    }

                    // Enable the rule if not enabled
                    if (!rule.isEnabled) {
                        val updatedRule = AutomaticZenRule(
                            rule.name,
                            rule.owner,
                            rule.configurationActivity,
                            rule.conditionId,
                            rule.zenPolicy,
                            rule.interruptionFilter,
                            true
                        )
                        notificationManager.updateAutomaticZenRule(ruleId, updatedRule)
                    }
                }

                // Set the condition
                val condition = Condition(
                    rule.conditionId,
                    rule.name,
                    if (active) Condition.STATE_TRUE else Condition.STATE_FALSE
                )
                notificationManager.setAutomaticZenRuleState(ruleId, condition)

                log("Toggled mode ${rule.name}: $active")
            } catch (t: Throwable) {
                log("Failed to toggle mode: ${t.message}", t)
            }
        }

        private fun dpToPx(context: Context, dp: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }
    }

    private fun log(msg: String, t: Throwable? = null) {
        if (t != null) {
            Log.e(TAG, msg, t)
        } else {
            Log.w(TAG, msg)
        }
    }
}
