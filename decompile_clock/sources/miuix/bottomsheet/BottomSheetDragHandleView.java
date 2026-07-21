package miuix.bottomsheet;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.xiaomi.onetrack.util.z;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes2.dex */
public class BottomSheetDragHandleView extends AppCompatImageView implements AccessibilityManager.AccessibilityStateChangeListener {
    private final AccessibilityManager accessibilityManager;
    private BottomSheetBehavior<?> bottomSheetBehavior;
    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;

    public BottomSheetDragHandleView(Context context) {
        this(context, null);
    }

    public BottomSheetDragHandleView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.bottomSheetDragHandleStyle);
    }

    public BottomSheetDragHandleView(Context context, AttributeSet attributeSet, int i) {
        super(wrap(context), attributeSet, i);
        this.bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() { // from class: miuix.bottomsheet.BottomSheetDragHandleView.1
            @Override // miuix.bottomsheet.BottomSheetBehavior.BottomSheetCallback
            public void onSlide(View view, float f) {
            }

            @Override // miuix.bottomsheet.BottomSheetBehavior.BottomSheetCallback
            public void onStateChanged(View view, int i2) {
                BottomSheetDragHandleView.this.onBottomSheetStateChanged(i2);
            }
        };
        this.accessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        updateInteractableState();
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() { // from class: miuix.bottomsheet.BottomSheetDragHandleView.2
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setClickable(false);
                accessibilityNodeInfoCompat.setClassName(View.class.getName());
                accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
            }
        });
    }

    private static Context wrap(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.bottomSheetDragHandleStyle, typedValue, true);
        if (typedValue.resourceId == 0) {
            if (AttributeResolver.resolveBoolean(context, R.attr.isLightTheme, true)) {
                theme.applyStyle(R.style.Widget_Miuix_BottomSheet_DragHandle_DefaultStyle_Light, false);
            } else {
                theme.applyStyle(R.style.Widget_Miuix_BottomSheet_DragHandle_DefaultStyle_Dark, false);
            }
        }
        return context;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setBottomSheetBehavior(findParentBottomSheetBehavior());
        setContentDescription(getContentDescriptionByCurrentState());
        AccessibilityManager accessibilityManager = this.accessibilityManager;
        if (accessibilityManager != null) {
            accessibilityManager.addAccessibilityStateChangeListener(this);
            onAccessibilityStateChanged(this.accessibilityManager.isEnabled() && this.accessibilityManager.isTouchExplorationEnabled());
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        AccessibilityManager accessibilityManager = this.accessibilityManager;
        if (accessibilityManager != null) {
            accessibilityManager.removeAccessibilityStateChangeListener(this);
        }
        setBottomSheetBehavior(null);
        super.onDetachedFromWindow();
    }

    @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
    public void onAccessibilityStateChanged(boolean z) {
        updateInteractableState();
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setContentDescription(getContentDescriptionByCurrentState());
    }

    private void setBottomSheetBehavior(BottomSheetBehavior<?> bottomSheetBehavior) {
        BottomSheetBehavior<?> bottomSheetBehavior2 = this.bottomSheetBehavior;
        if (bottomSheetBehavior2 != null) {
            bottomSheetBehavior2.removeBottomSheetCallback(this.bottomSheetCallback);
            this.bottomSheetBehavior.setAccessibilityDelegateView(null);
        }
        this.bottomSheetBehavior = bottomSheetBehavior;
        if (bottomSheetBehavior != null) {
            onBottomSheetStateChanged(bottomSheetBehavior.getState());
            this.bottomSheetBehavior.addBottomSheetCallback(this.bottomSheetCallback);
        }
        updateInteractableState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBottomSheetStateChanged(int i) {
        if (i == 4) {
            announceForAccessibility(getContext().getString(R.string.miuix_bottom_sheet_accessibility_switch_to_minimized));
        } else if (i == 3) {
            announceForAccessibility(getContext().getString(R.string.miuix_bottom_sheet_accessibility_switch_to_maximized));
        } else if (i == 6) {
            announceForAccessibility(getContext().getString(R.string.miuix_bottom_sheet_accessibility_switch_to_half_screen));
        }
        setContentDescription(getContentDescriptionByCurrentState());
    }

    private void updateInteractableState() {
        ViewCompat.setImportantForAccessibility(this, this.bottomSheetBehavior != null ? 1 : 2);
        setClickable(this.bottomSheetBehavior != null);
    }

    private String getContentDescriptionByCurrentState() {
        String string;
        Context context = getContext();
        BottomSheetBehavior<?> bottomSheetBehavior = this.bottomSheetBehavior;
        if (bottomSheetBehavior == null || !bottomSheetBehavior.isDraggable()) {
            return context.getString(R.string.bottomsheet_drag_handle_content_description);
        }
        int state = this.bottomSheetBehavior.getState();
        boolean z = this.bottomSheetBehavior.hideable && this.bottomSheetBehavior.getSkipCollapsed();
        boolean z2 = (this.bottomSheetBehavior.shouldSkipHalfExpanded() || this.bottomSheetBehavior.shouldSkipHalfExpandedStateWhenDragging()) ? false : true;
        if (state == 3) {
            string = context.getString(R.string.miuix_bottom_sheet_accessibility_state_maximized);
        } else if (state == 6) {
            string = context.getString(R.string.miuix_bottom_sheet_accessibility_state_half_screen);
        } else {
            string = state == 4 ? context.getString(R.string.miuix_bottom_sheet_accessibility_state_minimized) : null;
        }
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(string)) {
            sb.append(string).append(z.b);
        }
        sb.append(context.getString(R.string.bottomsheet_drag_handle_content_description)).append(z.b);
        if (state == 3) {
            if (z) {
                return sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_down_to_close)).toString();
            }
            if (z2) {
                return sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_down_to_half_screen)).toString();
            }
            return sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_down_to_minimized)).toString();
        }
        if (state == 6) {
            sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_up_to_maximized)).append(z.b);
            if (z) {
                sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_down_to_close));
            } else {
                sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_down_to_minimized));
            }
            return sb.toString();
        }
        if (state == 4) {
            if (z2) {
                sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_up_to_half_screen)).append(z.b);
            } else {
                sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_up_to_maximized)).append(z.b);
            }
            if (this.bottomSheetBehavior.isHideable()) {
                sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_down_to_close)).append(z.b);
            }
            return sb.append(context.getString(R.string.miuix_bottom_sheet_accessibility_scroll_up_to_maximized)).toString();
        }
        return sb.toString();
    }

    private BottomSheetBehavior<?> findParentBottomSheetBehavior() {
        View parentView = this;
        while (true) {
            parentView = getParentView(parentView);
            if (parentView == null) {
                return null;
            }
            ViewGroup.LayoutParams layoutParams = parentView.getLayoutParams();
            if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior();
                if (behavior instanceof BottomSheetBehavior) {
                    return (BottomSheetBehavior) behavior;
                }
            }
        }
    }

    private static View getParentView(View view) {
        Object parent = view.getParent();
        if (parent instanceof View) {
            return (View) parent;
        }
        return null;
    }
}
