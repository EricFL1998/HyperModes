package miuix.androidbasewidget.widget;

import android.R;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import androidx.appcompat.widget.ViewUtils;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import java.lang.ref.WeakReference;
import java.util.List;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class ClearableEditText extends EditText {
    private static final int[] EMPTY_STATE_SET = {R.attr.state_empty};
    private AccessHelper mAccessHelper;
    private Drawable mDrawable;
    private boolean mPressed;
    private boolean mShown;
    private ShowWidgetTextWatcher mTextWatcher;

    public ClearableEditText(Context context) {
        this(context, null);
    }

    public ClearableEditText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, miuix.androidbasewidget.R.attr.clearableEditTextStyle);
    }

    public ClearableEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTextWatcher = new ShowWidgetTextWatcher(this);
        this.mDrawable = getCompoundDrawablesRelative()[2];
        AccessHelper accessHelper = new AccessHelper(this);
        this.mAccessHelper = accessHelper;
        ViewCompat.setAccessibilityDelegate(this, accessHelper);
        if (Build.VERSION.SDK_INT >= 29) {
            setForceDarkAllowed(false);
        }
        Editable text = getText();
        if (text != null) {
            if (this.mShown != (text.length() > 0)) {
                this.mShown = !this.mShown;
                refreshDrawableState();
            }
        }
        addTextChangedListener(this.mTextWatcher);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(android.widget.EditText.class.getName());
    }

    @Override // android.widget.TextView, android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        super.onRestoreInstanceState(parcelable);
        Editable text = getText();
        if (text != null) {
            if (this.mShown != (text.length() > 0)) {
                this.mShown = !this.mShown;
                refreshDrawableState();
            }
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeTextChangedListener(this.mTextWatcher);
        addTextChangedListener(this.mTextWatcher);
    }

    @Override // androidx.appcompat.widget.AppCompatEditText, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeTextChangedListener(this.mTextWatcher);
    }

    @Override // androidx.appcompat.widget.AppCompatEditText, android.widget.TextView
    public void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        if (drawable != null || drawable2 != null || drawable3 != null || drawable4 != null) {
            throw new IllegalStateException("ClearableEditText can only set drawables by setCompoundDrawablesRelative()");
        }
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
    }

    @Override // androidx.appcompat.widget.AppCompatEditText, android.widget.TextView
    public void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        this.mDrawable = drawable3;
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
    }

    @Override // miuix.androidbasewidget.widget.EditText, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return dispatchWidgetTouchEvent(motionEvent) || super.dispatchTouchEvent(motionEvent);
    }

    private boolean dispatchWidgetTouchEvent(MotionEvent motionEvent) {
        if (this.mShown) {
            Drawable drawable = this.mDrawable;
            int intrinsicWidth = drawable == null ? 0 : drawable.getIntrinsicWidth();
            if (ViewUtils.isLayoutRtl(this)) {
                if (motionEvent.getX() < intrinsicWidth + getPaddingLeft()) {
                    return onButtonTouchEvent(motionEvent);
                }
            } else if (motionEvent.getX() > (getWidth() - getPaddingRight()) - intrinsicWidth) {
                return onButtonTouchEvent(motionEvent);
            }
        }
        this.mPressed = false;
        return false;
    }

    @Override // android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] iArrOnCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (!this.mShown) {
            mergeDrawableStates(iArrOnCreateDrawableState, EMPTY_STATE_SET);
        }
        return iArrOnCreateDrawableState;
    }

    @Override // androidx.appcompat.widget.AppCompatEditText, android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mDrawable != null) {
            int[] drawableState = getDrawableState();
            Drawable drawable = this.mDrawable;
            if (drawable != null && drawable.isStateful() && this.mDrawable.setState(drawableState)) {
                invalidateDrawable(this.mDrawable);
            }
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mDrawable;
    }

    @Override // android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    private boolean onButtonTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1) {
                if (isEnabled() && this.mPressed) {
                    onClearButtonClick();
                    if (isTalkBackActive(getContext())) {
                        sendAccessibilityEvent(32768);
                    }
                    this.mPressed = false;
                    return true;
                }
            } else if (action == 3 && this.mPressed) {
                this.mPressed = false;
            }
        } else if (isEnabled() && this.mShown) {
            this.mPressed = true;
        }
        return this.mPressed;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClearButtonClick() {
        setText("");
        HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_TAP_LIGHT);
    }

    @Override // android.view.View
    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        AccessHelper accessHelper = this.mAccessHelper;
        if (accessHelper != null && this.mShown && accessHelper.dispatchHoverEvent(motionEvent)) {
            return true;
        }
        return super.dispatchHoverEvent(motionEvent);
    }

    public boolean isTalkBackActive(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        return accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }

    private class AccessHelper extends ExploreByTouchHelper {
        private static final int CHILD_ID = 0;
        private final View forView;
        private final Rect mTempParentBounds;

        public AccessHelper(View view) {
            super(view);
            this.mTempParentBounds = new Rect();
            this.forView = view;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected int getVirtualViewAt(float f, float f2) {
            return (ClearableEditText.this.mShown && isVirtualView(f, f2)) ? 0 : Integer.MIN_VALUE;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateNodeForHost(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onPopulateNodeForHost(accessibilityNodeInfoCompat);
            if (ClearableEditText.this.mShown) {
                accessibilityNodeInfoCompat.setClassName(ClearableEditText.class.getName());
            }
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void getVisibleVirtualViews(List list) {
            if (ClearableEditText.this.mShown) {
                list.add(0);
            }
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.setContentDescription(getDescription());
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setVisibleToUser(true);
            accessibilityNodeInfoCompat.setContentDescription(getDescription());
            accessibilityNodeInfoCompat.addAction(16);
            accessibilityNodeInfoCompat.setClassName(Button.class.getName());
            getChildRect(this.mTempParentBounds);
            accessibilityNodeInfoCompat.setBoundsInParent(this.mTempParentBounds);
            accessibilityNodeInfoCompat.setClickable(true);
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            if (i == Integer.MIN_VALUE || i2 != 16) {
                return false;
            }
            ClearableEditText.this.onClearButtonClick();
            View view = this.forView;
            if (view == null || !ClearableEditText.this.isTalkBackActive(view.getContext())) {
                return true;
            }
            this.forView.sendAccessibilityEvent(32768);
            return true;
        }

        private CharSequence getDescription() {
            return ClearableEditText.this.getResources().getString(miuix.androidbasewidget.R.string.clearable_edittext_clear_description);
        }

        private void getChildRect(Rect rect) {
            this.forView.getLocalVisibleRect(this.mTempParentBounds);
            int intrinsicWidth = ClearableEditText.this.mDrawable == null ? 0 : ClearableEditText.this.mDrawable.getIntrinsicWidth();
            if (ViewUtils.isLayoutRtl(ClearableEditText.this)) {
                rect.right -= (ClearableEditText.this.getWidth() - intrinsicWidth) - (ClearableEditText.this.getPaddingLeft() * 2);
            } else {
                rect.left += (ClearableEditText.this.getWidth() - (ClearableEditText.this.getPaddingRight() * 2)) - intrinsicWidth;
            }
        }

        private boolean isVirtualView(float f, float f2) {
            int intrinsicWidth = ClearableEditText.this.mDrawable == null ? 0 : ClearableEditText.this.mDrawable.getIntrinsicWidth();
            if (ViewUtils.isLayoutRtl(ClearableEditText.this)) {
                return f < ((float) (intrinsicWidth + (ClearableEditText.this.getPaddingLeft() * 2)));
            }
            return f > ((float) ((ClearableEditText.this.getWidth() - (ClearableEditText.this.getPaddingRight() * 2)) - intrinsicWidth));
        }
    }

    static class ShowWidgetTextWatcher implements TextWatcher {
        WeakReference<ClearableEditText> mRef;

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        ShowWidgetTextWatcher(ClearableEditText clearableEditText) {
            this.mRef = new WeakReference<>(clearableEditText);
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            ClearableEditText clearableEditText = this.mRef.get();
            if (clearableEditText == null) {
                return;
            }
            if (clearableEditText.mShown != (editable.length() > 0)) {
                clearableEditText.mShown = !clearableEditText.mShown;
                clearableEditText.refreshDrawableState();
                if (clearableEditText.mAccessHelper != null) {
                    clearableEditText.mAccessHelper.invalidateRoot();
                }
            }
        }
    }
}
