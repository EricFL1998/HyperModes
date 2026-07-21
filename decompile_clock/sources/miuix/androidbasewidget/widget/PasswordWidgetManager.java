package miuix.androidbasewidget.widget;

import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.widget.Switch;
import androidx.core.content.ContextCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes2.dex */
public class PasswordWidgetManager extends StateEditText.WidgetManager {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private Context mContext;
    private boolean mIsChecked;
    private StateEditText mMaster;
    private Drawable mWidgetDrawable;

    public PasswordWidgetManager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        this.mIsChecked = false;
        Drawable drawableResolveDrawable = AttributeResolver.resolveDrawable(context, miuix.androidbasewidget.R.attr.miuixAppcompatVisibilityIcon);
        this.mWidgetDrawable = drawableResolveDrawable;
        if (drawableResolveDrawable == null) {
            if (AttributeResolver.resolveBoolean(context, R.attr.isLightTheme, true)) {
                this.mWidgetDrawable = ContextCompat.getDrawable(context, miuix.androidbasewidget.R.drawable.miuix_appcompat_ic_visibility_selector_light);
            } else {
                this.mWidgetDrawable = ContextCompat.getDrawable(context, miuix.androidbasewidget.R.drawable.miuix_appcompat_ic_visibility_selector_dark);
            }
        }
    }

    @Override // miuix.androidbasewidget.widget.StateEditText.WidgetManager
    protected void onDetached() {
        super.onDetached();
        StateEditText stateEditText = this.mMaster;
        if (stateEditText != null) {
            stateEditText.setTransformationMethod(null);
        }
    }

    @Override // miuix.androidbasewidget.widget.StateEditText.WidgetManager
    public void onAttached(StateEditText stateEditText) {
        TransformationMethod passwordTransformationMethod;
        this.mMaster = stateEditText;
        if (stateEditText != null) {
            if (this.mIsChecked) {
                passwordTransformationMethod = HideReturnsTransformationMethod.getInstance();
            } else {
                passwordTransformationMethod = PasswordTransformationMethod.getInstance();
            }
            stateEditText.setTransformationMethod(passwordTransformationMethod);
        }
    }

    @Override // miuix.androidbasewidget.widget.StateEditText.WidgetManager
    public Drawable[] getWidgetDrawables() {
        return new Drawable[]{this.mWidgetDrawable};
    }

    @Override // miuix.androidbasewidget.widget.StateEditText.WidgetManager
    public void onWidgetClick(int i) {
        TransformationMethod passwordTransformationMethod;
        this.mIsChecked = !this.mIsChecked;
        StateEditText stateEditText = this.mMaster;
        if (stateEditText != null) {
            int selectionStart = stateEditText.getSelectionStart();
            int selectionEnd = this.mMaster.getSelectionEnd();
            StateEditText stateEditText2 = this.mMaster;
            if (this.mIsChecked) {
                passwordTransformationMethod = HideReturnsTransformationMethod.getInstance();
            } else {
                passwordTransformationMethod = PasswordTransformationMethod.getInstance();
            }
            stateEditText2.setTransformationMethod(passwordTransformationMethod);
            this.mMaster.setTextDirection(2);
            this.mMaster.setSelection(selectionStart, selectionEnd);
        }
        this.mWidgetDrawable.setState(this.mIsChecked ? CHECKED_STATE_SET : new int[0]);
    }

    @Override // miuix.androidbasewidget.widget.StateEditText.WidgetManager
    protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        accessibilityNodeInfoCompat.setCheckable(true);
        accessibilityNodeInfoCompat.setChecked(this.mIsChecked);
        accessibilityNodeInfoCompat.setClassName(Switch.class.getName());
        accessibilityNodeInfoCompat.setContentDescription(this.mContext.getString(miuix.androidbasewidget.R.string.miuix_show_password));
    }
}
