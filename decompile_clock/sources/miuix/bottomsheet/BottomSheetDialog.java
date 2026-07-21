package miuix.bottomsheet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import miuix.internal.util.ViewUtils;
import miuix.theme.token.DimToken;

/* JADX INFO: loaded from: classes2.dex */
public class BottomSheetDialog extends AppCompatDialog {
    private static final boolean EDGE_TO_EDGE_ENABLED_DEFAULT = true;
    private BottomSheetBehavior<FrameLayout> behavior;
    private BottomSheetView bottomSheet;
    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    boolean cancelable;
    private boolean canceledOnTouchOutside;
    private boolean canceledOnTouchOutsideSet;
    private FrameLayout container;
    private CoordinatorLayout coordinator;
    boolean dismissWithAnimation;
    private boolean dragHandleViewEnabled;
    private boolean edgeToEdgeEnabled;

    static /* synthetic */ boolean lambda$wrapInBottomSheet$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public BottomSheetDialog(Context context) {
        this(context, 0);
        init();
    }

    public BottomSheetDialog(Context context, int i) {
        super(context, getThemeResId(context, i));
        this.cancelable = true;
        this.canceledOnTouchOutside = true;
        this.dragHandleViewEnabled = true;
        this.bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() { // from class: miuix.bottomsheet.BottomSheetDialog.3
            @Override // miuix.bottomsheet.BottomSheetBehavior.BottomSheetCallback
            public void onSlide(View view, float f) {
            }

            @Override // miuix.bottomsheet.BottomSheetBehavior.BottomSheetCallback
            public void onStateChanged(View view, int i2) {
                if (i2 == 5) {
                    BottomSheetDialog.this.cancel();
                }
            }
        };
        supportRequestWindowFeature(1);
        init();
    }

    protected BottomSheetDialog(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.cancelable = true;
        this.canceledOnTouchOutside = true;
        this.dragHandleViewEnabled = true;
        this.bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() { // from class: miuix.bottomsheet.BottomSheetDialog.3
            @Override // miuix.bottomsheet.BottomSheetBehavior.BottomSheetCallback
            public void onSlide(View view, float f) {
            }

            @Override // miuix.bottomsheet.BottomSheetBehavior.BottomSheetCallback
            public void onStateChanged(View view, int i2) {
                if (i2 == 5) {
                    BottomSheetDialog.this.cancel();
                }
            }
        };
        supportRequestWindowFeature(1);
        this.cancelable = z;
        init();
    }

    private void init() {
        TypedArray typedArrayObtainStyledAttributes = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.enableEdgeToEdge});
        try {
            this.edgeToEdgeEnabled = typedArrayObtainStyledAttributes.getBoolean(0, true);
            if (typedArrayObtainStyledAttributes != null) {
                typedArrayObtainStyledAttributes.recycle();
            }
        } catch (Throwable th) {
            if (typedArrayObtainStyledAttributes != null) {
                try {
                    typedArrayObtainStyledAttributes.recycle();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    public void setContentView(int i) {
        super.setContentView(wrapInBottomSheet(i, null, null));
    }

    @Override // androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        if (window != null) {
            window.addFlags(2);
            window.setDimAmount(ViewUtils.isNightMode(getContext()) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT);
            window.setStatusBarColor(0);
            window.addFlags(Integer.MIN_VALUE);
            window.setLayout(-1, -1);
        }
    }

    @Override // androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    public void setContentView(View view) {
        super.setContentView(wrapInBottomSheet(0, view, null));
    }

    @Override // androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        super.setContentView(wrapInBottomSheet(0, view, layoutParams));
    }

    @Override // android.app.Dialog
    public void setCancelable(boolean z) {
        super.setCancelable(z);
        if (this.cancelable != z) {
            this.cancelable = z;
            BottomSheetBehavior<FrameLayout> bottomSheetBehavior = this.behavior;
            if (bottomSheetBehavior != null) {
                bottomSheetBehavior.setHideable(z);
            }
        }
    }

    @Override // androidx.activity.ComponentDialog, android.app.Dialog
    protected void onStart() {
        super.onStart();
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = this.behavior;
        if (bottomSheetBehavior == null || bottomSheetBehavior.getState() != 5) {
            return;
        }
        this.behavior.setState(4);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        if (window != null) {
            boolean z = this.edgeToEdgeEnabled;
            FrameLayout frameLayout = this.container;
            if (frameLayout != null) {
                frameLayout.setFitsSystemWindows(!z);
            }
            CoordinatorLayout coordinatorLayout = this.coordinator;
            if (coordinatorLayout != null) {
                coordinatorLayout.setFitsSystemWindows(!z);
            }
            WindowCompat.setDecorFitsSystemWindows(window, !z);
            if (z) {
                window.setSoftInputMode(16);
                if (Build.VERSION.SDK_INT >= 28) {
                    window.getAttributes().layoutInDisplayCutoutMode = 1;
                }
                window.addFlags(AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL);
                window.addFlags(134217728);
            }
        }
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void cancel() {
        BottomSheetBehavior<FrameLayout> behavior = getBehavior();
        if (!this.dismissWithAnimation || behavior.getState() == 5) {
            super.cancel();
        } else {
            behavior.setState(5);
        }
    }

    @Override // android.app.Dialog
    public void setCanceledOnTouchOutside(boolean z) {
        super.setCanceledOnTouchOutside(z);
        if (z && !this.cancelable) {
            this.cancelable = true;
        }
        this.canceledOnTouchOutside = z;
        this.canceledOnTouchOutsideSet = true;
    }

    public BottomSheetBehavior<FrameLayout> getBehavior() {
        if (this.behavior == null) {
            ensureContainerAndBehavior();
        }
        return this.behavior;
    }

    public void setDismissWithAnimation(boolean z) {
        this.dismissWithAnimation = z;
    }

    public boolean getDismissWithAnimation() {
        return this.dismissWithAnimation;
    }

    public boolean getEdgeToEdgeEnabled() {
        return this.edgeToEdgeEnabled;
    }

    private FrameLayout ensureContainerAndBehavior() {
        if (this.container == null) {
            FrameLayout frameLayout = (FrameLayout) View.inflate(getContext(), R.layout.miuix_bottom_sheet_dialog, null);
            this.container = frameLayout;
            this.coordinator = (CoordinatorLayout) frameLayout.findViewById(R.id.coordinator);
            BottomSheetView bottomSheetView = (BottomSheetView) this.container.findViewById(R.id.bottom_sheet_view);
            this.bottomSheet = bottomSheetView;
            bottomSheetView.setEnableBlur(false);
            BottomSheetBehavior<FrameLayout> bottomSheetBehaviorFrom = BottomSheetBehavior.from(this.bottomSheet);
            this.behavior = bottomSheetBehaviorFrom;
            bottomSheetBehaviorFrom.addBottomSheetCallback(this.bottomSheetCallback);
            this.behavior.setHideable(this.cancelable);
        }
        return this.container;
    }

    private View wrapInBottomSheet(int i, View view, ViewGroup.LayoutParams layoutParams) {
        ensureContainerAndBehavior();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) this.container.findViewById(R.id.coordinator);
        if (i != 0 && view == null) {
            view = getLayoutInflater().inflate(i, (ViewGroup) coordinatorLayout, false);
        }
        this.bottomSheet.setDragHandleViewEnabled(this.dragHandleViewEnabled);
        this.bottomSheet.removeAllContentViews();
        if (layoutParams == null) {
            this.bottomSheet.addContentChildView(view);
        } else {
            this.bottomSheet.addContentChildView(view, layoutParams);
        }
        coordinatorLayout.findViewById(R.id.touch_outside).setOnClickListener(new View.OnClickListener() { // from class: miuix.bottomsheet.BottomSheetDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (BottomSheetDialog.this.cancelable && BottomSheetDialog.this.isShowing() && BottomSheetDialog.this.shouldWindowCloseOnTouchOutside()) {
                    BottomSheetDialog.this.cancel();
                }
            }
        });
        ViewCompat.setAccessibilityDelegate(this.bottomSheet, new AccessibilityDelegateCompat() { // from class: miuix.bottomsheet.BottomSheetDialog.2
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                if (BottomSheetDialog.this.cancelable) {
                    accessibilityNodeInfoCompat.addAction(1048576);
                    accessibilityNodeInfoCompat.setDismissable(true);
                } else {
                    accessibilityNodeInfoCompat.setDismissable(false);
                }
            }

            @Override // androidx.core.view.AccessibilityDelegateCompat
            public boolean performAccessibilityAction(View view2, int i2, Bundle bundle) {
                if (i2 == 1048576 && BottomSheetDialog.this.cancelable) {
                    BottomSheetDialog.this.cancel();
                    return true;
                }
                return super.performAccessibilityAction(view2, i2, bundle);
            }
        });
        this.bottomSheet.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.bottomsheet.BottomSheetDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                return BottomSheetDialog.lambda$wrapInBottomSheet$0(view2, motionEvent);
            }
        });
        return this.container;
    }

    boolean shouldWindowCloseOnTouchOutside() {
        if (!this.canceledOnTouchOutsideSet) {
            TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(new int[]{android.R.attr.windowCloseOnTouchOutside});
            this.canceledOnTouchOutside = typedArrayObtainStyledAttributes.getBoolean(0, true);
            typedArrayObtainStyledAttributes.recycle();
            this.canceledOnTouchOutsideSet = true;
        }
        return this.canceledOnTouchOutside;
    }

    private static int getThemeResId(Context context, int i) {
        if (i != 0) {
            return i;
        }
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.bottomSheetDialogTheme, typedValue, true)) {
            return typedValue.resourceId;
        }
        return R.style.Theme_Miuix_BottomSheetDialog_DayNight;
    }

    void removeDefaultCallback() {
        this.behavior.removeBottomSheetCallback(this.bottomSheetCallback);
    }

    public void setDragHandleViewEnabled(boolean z) {
        this.dragHandleViewEnabled = z;
        BottomSheetView bottomSheetView = this.bottomSheet;
        if (bottomSheetView != null) {
            bottomSheetView.setDragHandleViewEnabled(z);
        }
    }
}
