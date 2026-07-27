package com.banana.hypermodes.hook.modedisplay

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.banana.hypermodes.protocol.Protocol

object ModeDisplayViewFactory {
    const val LOCKSCREEN_TAG = "hypermodes_lockscreen_mode_display"
    const val FULL_AOD_TAG = "hypermodes_full_aod_mode_display"

    fun create(context: Context): LinearLayout {
        val verticalPadding = context.dp(4f)
        val iconSize = context.dp(18f)
        val iconEndMargin = context.dp(6f)

        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            id = View.generateViewId()
            visibility = View.GONE
            setPadding(0, verticalPadding, 0, verticalPadding)

            addView(ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                    marginEnd = iconEndMargin
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            })

            addView(TextView(context).apply {
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            })
        }
    }

    fun bind(context: Context, view: LinearLayout, state: ModeDisplayState?) {
        val iconView = view.getChildAt(0) as ImageView
        val textView = view.getChildAt(1) as TextView

        if (state == null) {
            iconView.setImageDrawable(null)
            iconView.visibility = View.GONE
            textView.text = ""
            view.visibility = View.GONE
            return
        }

        val drawable = runCatching {
            val moduleContext = context.createPackageContext(
                Protocol.MODULE_PACKAGE,
                Context.CONTEXT_IGNORE_SECURITY
            )
            val iconResId = moduleContext.resources.getIdentifier(
                state.iconResName,
                "drawable",
                Protocol.MODULE_PACKAGE
            )
            if (iconResId == 0) null else moduleContext.getDrawable(iconResId)
        }.getOrNull()

        iconView.setImageDrawable(drawable)
        iconView.visibility = if (drawable == null) View.GONE else View.VISIBLE
        textView.text = state.name
        view.visibility = View.VISIBLE
    }

    private fun Context.dp(value: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        resources.displayMetrics
    ).toInt()
}
