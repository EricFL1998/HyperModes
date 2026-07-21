package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import miuix.androidbasewidget.R;

/* JADX INFO: loaded from: classes2.dex */
public final class IndicatorViewController {
    private static final int CAPTION_STATE_ERROR = 1;
    private static final int CAPTION_STATE_NONE = 0;
    static final int ERROR_INDEX = 0;
    static final int ERROR_SIZE = 14;
    private FrameLayout mCaptionArea;
    private int mCaptionDisplayed;
    private int mCaptionToShow;
    private final Context mContext;
    private CharSequence mError;
    private boolean mErrorEnabled;
    private TextView mErrorView;
    private int mErrorViewAccessibilityLiveRegion;
    private CharSequence mErrorViewContentDescription;
    private LinearLayout mIndicatorArea;
    private int mIndicatorsAdded;
    private final TextInputLayout mTextInputView;
    private final int INDICATOR_AREA_MARGIN_LEFT = 8;
    private ColorStateList mErrorViewTextColor = initErrorColor(R.attr.miuixTextInputLayoutErrorColor);
    private int mErrorTextAppearance = initErrorAppearance(R.attr.miuixTextInputLayoutErrorStyle);

    @Retention(RetentionPolicy.SOURCE)
    private @interface IndicatorIndex {
    }

    boolean isCaptionView(int i) {
        return i == 0;
    }

    public IndicatorViewController(TextInputLayout textInputLayout) {
        this.mContext = textInputLayout.getContext();
        this.mTextInputView = textInputLayout;
    }

    public void showError(CharSequence charSequence) {
        this.mError = charSequence;
        this.mErrorView.setText(charSequence);
        int i = this.mCaptionDisplayed;
        if (i != 1) {
            this.mCaptionToShow = 1;
        }
        updateCaptionViewsVisibility(i, this.mCaptionToShow);
        this.mErrorView.announceForAccessibility(charSequence);
    }

    public void hideError() {
        int i = this.mCaptionDisplayed;
        if (i == 1) {
            this.mCaptionToShow = 0;
        }
        updateCaptionViewsVisibility(i, this.mCaptionToShow);
    }

    private void updateCaptionViewsVisibility(int i, int i2) {
        if (i == i2) {
            return;
        }
        setCaptionViewVisibilities(i, i2);
    }

    private void setCaptionViewVisibilities(int i, int i2) {
        TextView captionViewFromDisplayState;
        TextView captionViewFromDisplayState2;
        if (i == i2) {
            return;
        }
        if (i2 != 0 && (captionViewFromDisplayState2 = getCaptionViewFromDisplayState(i2)) != null) {
            captionViewFromDisplayState2.setVisibility(0);
            captionViewFromDisplayState2.setAlpha(1.0f);
        }
        if (i != 0 && (captionViewFromDisplayState = getCaptionViewFromDisplayState(i)) != null) {
            captionViewFromDisplayState.setVisibility(4);
            if (i == 1) {
                captionViewFromDisplayState.setText((CharSequence) null);
            }
        }
        this.mCaptionDisplayed = i2;
    }

    private TextView getCaptionViewFromDisplayState(int i) {
        if (i != 1) {
            return null;
        }
        return this.mErrorView;
    }

    private void setIndicatorAreaMarginLeft(int i) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mIndicatorArea.getLayoutParams();
        layoutParams.setMargins((int) TypedValue.applyDimension(1, i, this.mContext.getResources().getDisplayMetrics()), 0, 0, 0);
        this.mIndicatorArea.setLayoutParams(layoutParams);
    }

    void addIndicator(TextView textView, int i) {
        if (this.mIndicatorArea == null && this.mCaptionArea == null) {
            LinearLayout linearLayout = new LinearLayout(this.mContext);
            this.mIndicatorArea = linearLayout;
            linearLayout.setOrientation(0);
            this.mTextInputView.addView(this.mIndicatorArea, -2, -2);
            setIndicatorAreaMarginLeft(8);
            this.mCaptionArea = new FrameLayout(this.mContext);
            this.mIndicatorArea.addView(this.mCaptionArea, new LinearLayout.LayoutParams(0, -2, 1.0f));
        }
        if (isCaptionView(i)) {
            this.mCaptionArea.setVisibility(0);
            this.mCaptionArea.addView(textView);
        } else {
            this.mIndicatorArea.addView(textView, new LinearLayout.LayoutParams(-2, -2));
        }
        this.mIndicatorArea.setVisibility(0);
        this.mIndicatorsAdded++;
    }

    void removeIndicator(TextView textView, int i) {
        FrameLayout frameLayout;
        if (this.mIndicatorArea == null) {
            return;
        }
        if (isCaptionView(i) && (frameLayout = this.mCaptionArea) != null) {
            frameLayout.removeView(textView);
        } else {
            this.mIndicatorArea.removeView(textView);
        }
        int i2 = this.mIndicatorsAdded - 1;
        this.mIndicatorsAdded = i2;
        setViewGroupGoneIfEmpty(this.mIndicatorArea, i2);
    }

    private void setViewGroupGoneIfEmpty(ViewGroup viewGroup, int i) {
        if (i == 0) {
            viewGroup.setVisibility(8);
        }
    }

    private ColorStateList initErrorColor(int i) {
        int i2;
        TypedValue typedValue = new TypedValue();
        if (this.mContext.getTheme().resolveAttribute(i, typedValue, true)) {
            i2 = typedValue.resourceId;
        } else {
            i2 = R.color.miuix_color_red_light_level1;
        }
        return ContextCompat.getColorStateList(this.mContext, i2);
    }

    private int initErrorAppearance(int i) {
        TypedValue typedValue = new TypedValue();
        return this.mContext.getTheme().resolveAttribute(i, typedValue, true) ? typedValue.resourceId : R.style.Widget_TextInputLayout_Error_DayNight;
    }

    public void setErrorEnabled(boolean z) {
        if (this.mErrorEnabled == z) {
            return;
        }
        if (z) {
            AppCompatTextView appCompatTextView = new AppCompatTextView(this.mContext);
            this.mErrorView = appCompatTextView;
            appCompatTextView.setId(R.id.miuix_textinput_error);
            this.mErrorView.setTextAlignment(5);
            setErrorTextAppearance(this.mErrorTextAppearance);
            setErrorViewTextColor(this.mErrorViewTextColor);
            setErrorContentDescription(this.mErrorViewContentDescription);
            setErrorAccessibilityLiveRegion(this.mErrorViewAccessibilityLiveRegion);
            this.mErrorView.setVisibility(4);
            addIndicator(this.mErrorView, 0);
        } else {
            hideError();
            removeIndicator(this.mErrorView, 0);
            this.mErrorView = null;
        }
        this.mErrorEnabled = z;
    }

    public boolean isErrorEnabled() {
        return this.mErrorEnabled;
    }

    public int getErrorViewCurrentTextColor() {
        TextView textView = this.mErrorView;
        if (textView != null) {
            return textView.getCurrentTextColor();
        }
        return -1;
    }

    public void setErrorViewTextColor(ColorStateList colorStateList) {
        this.mErrorViewTextColor = colorStateList;
        TextView textView = this.mErrorView;
        if (textView == null || colorStateList == null) {
            return;
        }
        textView.setTextColor(colorStateList);
    }

    public void setErrorTextAppearance(int i) {
        this.mErrorTextAppearance = i;
        TextView textView = this.mErrorView;
        if (textView != null) {
            this.mTextInputView.setTextAppearanceCompatWithErrorFallback(textView, i);
        }
    }

    public void setErrorContentDescription(CharSequence charSequence) {
        this.mErrorViewContentDescription = charSequence;
        TextView textView = this.mErrorView;
        if (textView != null) {
            textView.setContentDescription(charSequence);
        }
    }

    public void setErrorAccessibilityLiveRegion(int i) {
        this.mErrorViewAccessibilityLiveRegion = i;
        TextView textView = this.mErrorView;
        if (textView != null) {
            textView.setAccessibilityLiveRegion(i);
        }
    }

    public CharSequence getErrorContentDescription() {
        return this.mErrorViewContentDescription;
    }

    public int getErrorAccessibilityLiveRegion() {
        return this.mErrorViewAccessibilityLiveRegion;
    }

    public CharSequence getError() {
        return this.mError;
    }

    public int getErrorTextAppearance() {
        return this.mErrorTextAppearance;
    }

    public TextView getErrorView() {
        return this.mErrorView;
    }
}
