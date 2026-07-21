package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import miuix.androidbasewidget.R;

/* JADX INFO: loaded from: classes2.dex */
public class TextInputLayout extends LinearLayout {
    public final int ACCESSIBILITY_MODE_ASSERTIVE;
    public final int ACCESSIBILITY_MODE_NONE;
    public final int ACCESSIBILITY_MODE_POLITE;
    private final int PARENT_LAYOUT_PADDING_BOTTOM;
    private final int PARENT_LAYOUT_PADDING_LEFT;
    private final int PARENT_LAYOUT_PADDING_RIGHT;
    private final int PARENT_LAYOUT_PADDING_TOP;
    private android.widget.EditText mEditText;
    private CharSequence mError;
    private final IndicatorViewController mIndicatorViewController;

    public TextInputLayout(Context context) {
        this(context, null, R.attr.miuixTextInputStyle);
    }

    public TextInputLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.miuixTextInputStyle);
    }

    public TextInputLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.PARENT_LAYOUT_PADDING_TOP = 8;
        this.PARENT_LAYOUT_PADDING_BOTTOM = 16;
        this.PARENT_LAYOUT_PADDING_LEFT = 12;
        this.PARENT_LAYOUT_PADDING_RIGHT = 12;
        this.ACCESSIBILITY_MODE_NONE = 0;
        this.ACCESSIBILITY_MODE_POLITE = 1;
        this.ACCESSIBILITY_MODE_ASSERTIVE = 2;
        this.mIndicatorViewController = new IndicatorViewController(this);
        setOrientation(1);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TextInputLayout, i, R.style.Widget_TextInputLayout_DayNight);
        this.mError = typedArrayObtainStyledAttributes.hasValue(R.styleable.TextInputLayout_miuixError) ? typedArrayObtainStyledAttributes.getText(R.styleable.TextInputLayout_miuixError) : null;
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_miuixErrorTextAppearance, getErrorTextAppearance());
        CharSequence text = typedArrayObtainStyledAttributes.hasValue(R.styleable.TextInputLayout_miuixErrorContentDescription) ? typedArrayObtainStyledAttributes.getText(R.styleable.TextInputLayout_miuixErrorContentDescription) : this.mError;
        int i2 = typedArrayObtainStyledAttributes.getInt(R.styleable.TextInputLayout_miuixErrorAccessibilityLiveRegion, 0);
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.TextInputLayout_miuixErrorTextColor)) {
            setErrorTextColor(typedArrayObtainStyledAttributes.getColorStateList(R.styleable.TextInputLayout_miuixErrorTextColor));
        }
        typedArrayObtainStyledAttributes.recycle();
        setTextInputLayoutPadding(12, 12, 8, 16);
        setErrorTextAppearance(resourceId);
        setErrorContentDescription(text);
        setErrorAccessibilityLiveRegion(i2);
        setErrorEnabled(false);
    }

    private void setTextInputLayoutPadding(int i, int i2, int i3, int i4) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        setPadding((int) TypedValue.applyDimension(1, i, displayMetrics), (int) TypedValue.applyDimension(1, i3, displayMetrics), (int) TypedValue.applyDimension(1, i2, displayMetrics), (int) TypedValue.applyDimension(1, i4, displayMetrics));
    }

    public android.widget.EditText getEditText() {
        this.mEditText = null;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof android.widget.EditText) {
                this.mEditText = (android.widget.EditText) childAt;
                break;
            }
        }
        return this.mEditText;
    }

    public void setTextAppearanceCompatWithErrorFallback(TextView textView, int i) {
        try {
            TextViewCompat.setTextAppearance(textView, i);
            if (textView.getTextColors().getDefaultColor() != -65281) {
                return;
            }
        } catch (Exception unused) {
        }
        TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_AppCompat_Caption);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.design_error));
    }

    public void setError(CharSequence charSequence) {
        if (!this.mIndicatorViewController.isErrorEnabled()) {
            if (TextUtils.isEmpty(charSequence)) {
                return;
            } else {
                setErrorEnabled(true);
            }
        }
        if (!TextUtils.isEmpty(charSequence)) {
            this.mError = charSequence;
            this.mIndicatorViewController.showError(charSequence);
            android.widget.EditText editText = this.mEditText;
            if (editText != null) {
                Editable text = editText.getText();
                if (text != null) {
                    charSequence = ((Object) charSequence) + text.toString();
                }
                setContentDescription(charSequence);
                return;
            }
            return;
        }
        this.mError = null;
        this.mIndicatorViewController.hideError();
    }

    public void setErrorEnabled(boolean z) {
        this.mIndicatorViewController.setErrorEnabled(z);
    }

    public CharSequence getError() {
        return this.mError;
    }

    public void setErrorTextAppearance(int i) {
        this.mIndicatorViewController.setErrorTextAppearance(i);
    }

    public void setErrorContentDescription(CharSequence charSequence) {
        this.mIndicatorViewController.setErrorContentDescription(charSequence);
    }

    public CharSequence getErrorContentDescription() {
        return this.mIndicatorViewController.getErrorContentDescription();
    }

    public void setErrorAccessibilityLiveRegion(int i) {
        this.mIndicatorViewController.setErrorAccessibilityLiveRegion(i);
    }

    public int getErrorAccessibilityLiveRegion() {
        return this.mIndicatorViewController.getErrorAccessibilityLiveRegion();
    }

    public void setErrorTextColor(ColorStateList colorStateList) {
        this.mIndicatorViewController.setErrorViewTextColor(colorStateList);
    }

    public int getErrorCurrentTextColors() {
        return this.mIndicatorViewController.getErrorViewCurrentTextColor();
    }

    public boolean isErrorEnabled() {
        return this.mIndicatorViewController.isErrorEnabled();
    }

    private int getErrorTextAppearance() {
        return this.mIndicatorViewController.getErrorTextAppearance();
    }

    public void showError() {
        setError(getError());
    }

    public void hideError() {
        setErrorEnabled(false);
    }

    public TextView getErrorView() {
        return this.mIndicatorViewController.getErrorView();
    }
}
