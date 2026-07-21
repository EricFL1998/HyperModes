package miuix.appcompat.internal.view.menu.action;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import miuix.animation.ViewHoverListener;
import miuix.appcompat.R;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes2.dex */
class OverflowMenuButton extends LinearLayout implements ActionMenuView.ActionMenuChildView, ViewHoverListener {
    private final ActionMenuItemViewChildren mChildren;
    private boolean mIsHover;
    private OnOverflowMenuButtonClickListener mOnOverflowMenuButtonClickListener;

    interface OnOverflowMenuButtonClickListener {
        void onOverflowMenuButtonClick();
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView.ActionMenuChildView
    public boolean needsDividerAfter() {
        return false;
    }

    @Override // miuix.appcompat.internal.view.menu.action.ActionMenuView.ActionMenuChildView
    public boolean needsDividerBefore() {
        return false;
    }

    @Override // miuix.animation.ViewHoverListener
    public void onMoveHover() {
    }

    public OverflowMenuButton(Context context) {
        this(context, null);
    }

    public OverflowMenuButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OverflowMenuButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.OverflowMenuButton, i, 0);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.OverflowMenuButton_android_drawableTop);
        CharSequence text = typedArrayObtainStyledAttributes.getText(R.styleable.OverflowMenuButton_android_text);
        String string = typedArrayObtainStyledAttributes.getString(R.styleable.OverflowMenuButton_android_contentDescription);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.OverflowMenuButton_largeFontAdaptationEnabled, true) && MiuixUIUtils.getFontLevel(context) == 2;
        typedArrayObtainStyledAttributes.recycle();
        ActionMenuItemViewChildren actionMenuItemViewChildren = new ActionMenuItemViewChildren(this);
        this.mChildren = actionMenuItemViewChildren;
        actionMenuItemViewChildren.setIcon(drawable);
        actionMenuItemViewChildren.setText(text);
        actionMenuItemViewChildren.setContentDescription(string);
        actionMenuItemViewChildren.setLargeFontEnabled(z);
        setClickable(true);
        setFocusable(true);
        setVisibility(0);
        setEnabled(true);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(Button.class.getName());
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mChildren.onConfigurationChanged(configuration);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mChildren.setEnabled(z);
    }

    private boolean isVisible() {
        ViewGroup viewGroup = this;
        while (viewGroup != null && viewGroup.getVisibility() == 0) {
            ViewParent parent = viewGroup.getParent();
            viewGroup = parent instanceof ViewGroup ? (ViewGroup) parent : null;
        }
        return viewGroup == null;
    }

    @Override // android.view.View
    public boolean performClick() {
        if (super.performClick() || !isVisible()) {
            return true;
        }
        playSoundEffect(0);
        OnOverflowMenuButtonClickListener onOverflowMenuButtonClickListener = this.mOnOverflowMenuButtonClickListener;
        if (onOverflowMenuButtonClickListener != null) {
            onOverflowMenuButtonClickListener.onOverflowMenuButtonClick();
        }
        return true;
    }

    public void setOnOverflowMenuButtonClickListener(OnOverflowMenuButtonClickListener onOverflowMenuButtonClickListener) {
        this.mOnOverflowMenuButtonClickListener = onOverflowMenuButtonClickListener;
    }

    @Override // miuix.animation.ViewHoverListener
    public boolean isHover() {
        return this.mIsHover;
    }

    @Override // miuix.animation.ViewHoverListener
    public void onEnterHover() {
        this.mIsHover = true;
    }

    @Override // miuix.animation.ViewHoverListener
    public void onExitHover() {
        this.mIsHover = false;
    }
}
