package miuix.appcompat.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import miuix.appcompat.R;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.RomUtils;
import miuix.internal.util.ViewUtils;
import miuix.internal.widget.GroupButton;
import miuix.view.DensityChangedHelper;

/* JADX INFO: loaded from: classes2.dex */
public class DialogButtonPanel extends LinearLayout {
    private boolean isContentLandscape;
    private int mButtonMarginHorizontal;
    private int mButtonMarginVertical;
    private int mButtonMinHeight;
    private float mButtonTextSize;
    private int mButtonsFullyVisibleHeight;
    private Context mContext;
    private int mCurrentDensityDpi;
    private boolean mCustomPaddingEnabled;
    private int mCustomPaddingHorizontal;
    private boolean mForceVertical;
    private boolean mHorizontalPositionConfirmed;
    private int mLastDensityDpi;
    private final List<GroupButton> mNegativeStyleButtons;
    private final List<GroupButton> mNeutralStyleButtons;
    private int mPanelPaddingHorizontal;
    private boolean mPrimaryButtonFirstEnabled;
    private final List<GroupButton> mPrimaryStyleButtons;
    private boolean mVerticalPositionConfirmed;

    public DialogButtonPanel(Context context) {
        this(context, null);
    }

    public DialogButtonPanel(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DialogButtonPanel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCustomPaddingHorizontal = 0;
        this.mCustomPaddingEnabled = false;
        this.mPrimaryButtonFirstEnabled = false;
        this.mButtonTextSize = 17.0f;
        this.mPrimaryStyleButtons = new ArrayList();
        this.mNegativeStyleButtons = new ArrayList();
        this.mNeutralStyleButtons = new ArrayList();
        this.mVerticalPositionConfirmed = false;
        this.mHorizontalPositionConfirmed = false;
        this.mContext = context;
        Resources resources = getResources();
        this.mPanelPaddingHorizontal = resources.getDimensionPixelOffset(R.dimen.miuix_appcompat_dialog_button_panel_horizontal_margin);
        this.mButtonMarginHorizontal = resources.getDimensionPixelOffset(R.dimen.miuix_appcompat_dialog_btn_margin_horizontal);
        this.mButtonMarginVertical = resources.getDimensionPixelOffset(R.dimen.miuix_appcompat_dialog_btn_margin_vertical);
        this.mButtonMinHeight = resources.getDimensionPixelOffset(R.dimen.miuix_appcompat_button_height);
        int i2 = resources.getConfiguration().densityDpi;
        this.mCurrentDensityDpi = i2;
        this.mLastDensityDpi = i2;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        handleButtonLayout(View.MeasureSpec.getSize(i));
        super.onMeasure(i, i2);
    }

    private void handleButtonLayout(int i) {
        boolean zIsVerticalNeeded = isVerticalNeeded((i - getPaddingStart()) - getPaddingEnd());
        int childCount = getChildCount();
        if (zIsVerticalNeeded) {
            resizeButtonTextSize((i - getPaddingStart()) - getPaddingEnd());
            if (RomUtils.getHyperOsVersion() > 2 || this.mPrimaryButtonFirstEnabled) {
                resortButtonPositionWhenVertical();
            }
            handleVerticalLayout(childCount);
            return;
        }
        if (RomUtils.getHyperOsVersion() > 2 || this.mPrimaryButtonFirstEnabled) {
            resortButtonPositionWhenHorizontal();
        }
        handleHorizontalLayout(childCount);
    }

    private void resortButtonPositionWhenVertical() {
        if (this.mVerticalPositionConfirmed) {
            return;
        }
        int i = 0;
        for (GroupButton groupButton : this.mPrimaryStyleButtons) {
            removeView(groupButton);
            addView(groupButton, i);
            i++;
        }
        for (GroupButton groupButton2 : this.mNeutralStyleButtons) {
            removeView(groupButton2);
            addView(groupButton2, i);
            i++;
        }
        for (GroupButton groupButton3 : this.mNegativeStyleButtons) {
            removeView(groupButton3);
            addView(groupButton3, i);
            i++;
        }
        this.mVerticalPositionConfirmed = true;
    }

    private void resortButtonPositionWhenHorizontal() {
        if (this.mHorizontalPositionConfirmed) {
            return;
        }
        int i = 0;
        for (GroupButton groupButton : this.mNegativeStyleButtons) {
            removeView(groupButton);
            addView(groupButton, i);
            i++;
        }
        for (GroupButton groupButton2 : this.mNeutralStyleButtons) {
            removeView(groupButton2);
            addView(groupButton2, i);
            i++;
        }
        for (GroupButton groupButton3 : this.mPrimaryStyleButtons) {
            removeView(groupButton3);
            addView(groupButton3, i);
            i++;
        }
        this.mHorizontalPositionConfirmed = true;
    }

    public void setVerticalPositionConfirmed(boolean z) {
        this.mVerticalPositionConfirmed = z;
    }

    public void setHorizontalPositionConfirmed(boolean z) {
        this.mHorizontalPositionConfirmed = z;
    }

    private void resizeButtonTextSize(int i) {
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if ((childAt instanceof TextView) && childAt.getVisibility() == 0) {
                TextView textView = (TextView) childAt;
                DensityChangedHelper.updateTextSizeSpUnit(textView, 17.0f);
                if (isEllipsized(textView, i)) {
                    for (int i3 = 0; i3 < getChildCount(); i3++) {
                        View childAt2 = getChildAt(i3);
                        if (childAt2 instanceof TextView) {
                            DensityChangedHelper.updateTextSizeSpUnit((TextView) childAt2, 14.0f);
                        }
                    }
                    return;
                }
            }
        }
    }

    private void setFallbackLineSpacing(View view, boolean z) {
        if (!(view instanceof TextView) || Build.VERSION.SDK_INT < 28) {
            return;
        }
        ((TextView) view).setFallbackLineSpacing(z);
    }

    private void handleHorizontalLayout(int i) {
        setOrientation(0);
        int i2 = this.mCustomPaddingEnabled ? this.mCustomPaddingHorizontal : this.mPanelPaddingHorizontal;
        setPadding(i2, getPaddingTop(), i2, getPaddingBottom());
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
        int i3 = 0;
        for (int i4 = 0; i4 < i; i4++) {
            View childAt = getChildAt(i4);
            boolean z = childAt.getVisibility() == 0;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
            childAt.setMinimumHeight(this.mButtonMinHeight);
            layoutParams.width = 0;
            layoutParams.height = -2;
            layoutParams.weight = 1.0f;
            layoutParams.topMargin = 0;
            if (z) {
                if (zIsLayoutRtl) {
                    layoutParams.rightMargin = i3;
                } else {
                    layoutParams.leftMargin = i3;
                }
                if (MiuixUIUtils.isTallFontLang(this.mContext)) {
                    setFallbackLineSpacing(childAt, true);
                }
            } else {
                layoutParams.rightMargin = 0;
                layoutParams.leftMargin = 0;
            }
            if (z) {
                i3 = this.mButtonMarginHorizontal;
            }
        }
        this.mButtonsFullyVisibleHeight = i > 0 ? this.mButtonMinHeight : 0;
        this.mVerticalPositionConfirmed = false;
    }

    private void handleVerticalLayout(int i) {
        setOrientation(1);
        int i2 = this.mCustomPaddingEnabled ? this.mCustomPaddingHorizontal : this.mPanelPaddingHorizontal;
        setPadding(i2, getPaddingTop(), i2, getPaddingBottom());
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < i; i5++) {
            View childAt = getChildAt(i5);
            boolean z = childAt.getVisibility() == 0;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
            childAt.setMinimumHeight(this.mButtonMinHeight);
            layoutParams.width = -1;
            layoutParams.height = -2;
            layoutParams.weight = 0.0f;
            layoutParams.topMargin = z ? i4 : 0;
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
            if (z) {
                i4 = this.mButtonMarginVertical;
            }
            if (z) {
                i3++;
            }
            if (z && MiuixUIUtils.isTallFontLang(this.mContext)) {
                setFallbackLineSpacing(childAt, true);
            }
        }
        this.mButtonsFullyVisibleHeight = i3 > 0 ? (this.mButtonMinHeight * i3) + ((i3 - 1) * this.mButtonMarginVertical) : 0;
        this.mHorizontalPositionConfirmed = false;
    }

    private boolean isVerticalNeeded(int i) {
        if (this.mForceVertical) {
            return true;
        }
        int childCount = getChildCount();
        int i2 = childCount;
        for (int i3 = childCount - 1; i3 >= 0; i3--) {
            if (getChildAt(i3).getVisibility() == 8) {
                i2--;
            }
        }
        if (i2 < 2) {
            return false;
        }
        if (i2 >= 3) {
            return true;
        }
        int i4 = (i - this.mButtonMarginHorizontal) / 2;
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if ((childAt instanceof TextView) && childAt.getVisibility() == 0 && isEllipsized((TextView) childAt, i4)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEllipsized(TextView textView, int i) {
        return ((int) textView.getPaint().measureText(textView.getText().toString())) > (i - textView.getPaddingStart()) - textView.getPaddingEnd();
    }

    public void addPrimaryStyleButtons(GroupButton groupButton) {
        this.mPrimaryStyleButtons.add(groupButton);
    }

    public void addNegativeStyleButtons(GroupButton groupButton) {
        this.mNegativeStyleButtons.add(groupButton);
    }

    public void addNeutralStyleButtons(GroupButton groupButton) {
        this.mNeutralStyleButtons.add(groupButton);
    }

    public void clearPrimaryStyleButtonList() {
        this.mPrimaryStyleButtons.clear();
    }

    public void clearNegativeStyleButtonList() {
        this.mNegativeStyleButtons.clear();
    }

    public void clearNeutralStyleButtonList() {
        this.mNeutralStyleButtons.clear();
    }

    public void setForceVertical(boolean z) {
        if (this.mForceVertical != z) {
            this.mForceVertical = z;
            requestLayout();
        }
    }

    public void setPrimaryButtonFirstEnabled(boolean z) {
        this.mPrimaryButtonFirstEnabled = z;
    }

    public void setCustomPaddingHorizontal(int i) {
        this.mCustomPaddingHorizontal = i;
    }

    public void setCustomPaddingEnabled(boolean z) {
        this.mCustomPaddingEnabled = z;
    }

    public void isContentLandscape(boolean z) {
        this.isContentLandscape = z;
    }

    public int getButtonFullyVisibleHeight() {
        return this.mButtonsFullyVisibleHeight;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = this.mCurrentDensityDpi;
        this.mLastDensityDpi = i;
        if (i != configuration.densityDpi) {
            int i2 = configuration.densityDpi;
            this.mCurrentDensityDpi = i2;
            float f = (i2 * 1.0f) / this.mLastDensityDpi;
            this.mPanelPaddingHorizontal = (int) (this.mPanelPaddingHorizontal * f);
            this.mButtonMarginHorizontal = (int) (this.mButtonMarginHorizontal * f);
            this.mButtonMarginVertical = (int) (this.mButtonMarginVertical * f);
            this.mButtonMinHeight = (int) (this.mButtonMinHeight * f);
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (childAt instanceof TextView) {
                    DensityChangedHelper.updateTextSizeSpUnit((TextView) childAt, this.mButtonTextSize);
                }
            }
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        adjustButtonScrollIfNeed();
    }

    private void adjustButtonScrollIfNeed() {
        if (this.isContentLandscape) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) getParent();
        boolean z = (((float) this.mButtonsFullyVisibleHeight) * 1.0f) / ((float) Math.max(EnvStateManager.getWindowSize(this.mContext).y, 1)) >= 0.4f;
        if (viewGroup == null || !z || (viewGroup instanceof NestedScrollViewExpander) || !(viewGroup instanceof DialogParentPanel2)) {
            return;
        }
        ViewGroup viewGroup2 = (ViewGroup) viewGroup.findViewById(R.id.contentPanel);
        viewGroup.removeView(this);
        if (viewGroup2 != null) {
            viewGroup2.addView(this);
            ((NestedScrollViewExpander) viewGroup2).setExpandView(null);
        }
    }
}
