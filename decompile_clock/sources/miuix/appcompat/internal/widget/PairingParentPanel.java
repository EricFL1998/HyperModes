package miuix.appcompat.internal.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageView;
import miuix.appcompat.R;
import miuix.core.util.SystemProperties;
import miuix.core.widget.NestedScrollView;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class PairingParentPanel extends FrameLayout {
    private static final String TAG = "PairingParentPanel";
    private AppCompatImageView mClosableIcon;
    private int mClosableIconPositionHorizontal;
    private int mClosableIconPositionTop;
    private Context mContext;
    private boolean mCustomViewVerticalCenterEnabled;
    private boolean mIsButtonPanelVisible;
    private boolean mIsDebugEnabled;
    private boolean mIsFeedbackVisible;
    private NestedScrollView mPairingScrollView;
    private int mScrollExpectedHeight;

    public PairingParentPanel(Context context) {
        this(context, null);
    }

    public PairingParentPanel(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PairingParentPanel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsDebugEnabled = false;
        this.mCustomViewVerticalCenterEnabled = true;
        this.mIsButtonPanelVisible = true;
        this.mIsFeedbackVisible = false;
        this.mScrollExpectedHeight = 0;
        this.mContext = context;
        init(context);
    }

    public void setCustomViewVerticalCenterEnabled(boolean z) {
        this.mCustomViewVerticalCenterEnabled = z;
    }

    private void init(Context context) {
        this.mClosableIconPositionTop = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_pairing_dialog_icon_position_margin_top);
        this.mClosableIconPositionHorizontal = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_pairing_dialog_icon_position_margin_horizontal);
        isDebugEnabled();
    }

    public int getScrollExpectedHeight() {
        return this.mScrollExpectedHeight;
    }

    private boolean isDebugEnabled() {
        String str = "";
        try {
            String str2 = SystemProperties.get("log.tag.alertdialog.ime.debug.enable");
            if (str2 != null) {
                str = str2;
            }
        } catch (Exception e) {
            Log.i(TAG, "can not access property log.tag.alertdialog.ime.enable, undebugable", e);
        }
        Log.d(TAG, "Alert dialog ime debugEnable = " + str);
        boolean zEquals = TextUtils.equals("true", str);
        this.mIsDebugEnabled = zEquals;
        return zEquals;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mClosableIcon = (AppCompatImageView) findViewById(R.id.pairingClosable);
        this.mPairingScrollView = (NestedScrollView) findViewById(R.id.pairingScrollView);
    }

    private int getScrollableAvailableHeight() {
        int measuredHeight;
        ViewGroup viewGroup = (ViewGroup) getParent();
        ViewGroup viewGroup2 = viewGroup != null ? (ViewGroup) viewGroup.getParent() : null;
        ViewGroup viewGroup3 = viewGroup2 instanceof NestedScrollViewExpander ? (ViewGroup) viewGroup2.getParent() : null;
        ViewGroup viewGroup4 = viewGroup3 instanceof NestedScrollViewExpandContainer ? (ViewGroup) viewGroup3.getParent() : null;
        if (viewGroup4 == null) {
            return -1;
        }
        ViewGroup viewGroup5 = (ViewGroup) viewGroup4.findViewById(R.id.pairingCheckboxContainer);
        int measuredHeight2 = 0;
        int measuredHeight3 = (viewGroup5 == null || viewGroup5.getVisibility() != 0) ? 0 : viewGroup5.getMeasuredHeight();
        int paddingBottom = viewGroup4.getPaddingBottom();
        DialogButtonPanel dialogButtonPanel = (DialogButtonPanel) viewGroup4.findViewById(R.id.buttonPanel);
        if (dialogButtonPanel != null && dialogButtonPanel.getVisibility() == 0) {
            measuredHeight = dialogButtonPanel.getMeasuredHeight();
            this.mIsButtonPanelVisible = true;
        } else {
            this.mIsButtonPanelVisible = false;
            measuredHeight = 0;
        }
        TextView textView = (TextView) viewGroup4.findViewById(R.id.pairingDialogFeedback);
        if (textView != null && textView.getVisibility() == 0) {
            measuredHeight2 = textView.getMeasuredHeight();
            this.mIsFeedbackVisible = true;
        } else {
            this.mIsFeedbackVisible = false;
        }
        if (this.mIsDebugEnabled) {
            Log.e(TAG, "getScrollableAvailableHeight: dialogParentPanel.height = " + viewGroup4.getMeasuredHeight() + ", dialogParentPanelPaddingBottom = " + paddingBottom + ", buttonPanelHeight = " + measuredHeight + ", feedbackViewHeight = " + measuredHeight2);
        }
        return (((viewGroup4.getMeasuredHeight() - paddingBottom) - measuredHeight3) - measuredHeight) - measuredHeight2;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        applyCustomViewLayoutVerticalCenterIfNeeded();
    }

    private void applyCustomViewLayoutVerticalCenterIfNeeded() {
        if (this.mCustomViewVerticalCenterEnabled) {
            int scrollableAvailableHeight = getScrollableAvailableHeight();
            NestedScrollView nestedScrollView = this.mPairingScrollView;
            if (nestedScrollView != null) {
                this.mScrollExpectedHeight = nestedScrollView.getMeasuredHeight();
            }
            if (this.mIsDebugEnabled) {
                Log.d(TAG, "onMeasure: ==> height = " + getMeasuredHeight() + ", scrollableAvailableHeight = " + scrollableAvailableHeight);
            }
            if (scrollableAvailableHeight <= 0 || getMeasuredHeight() >= scrollableAvailableHeight) {
                return;
            }
            int paddingBottom = scrollableAvailableHeight - ((this.mIsButtonPanelVisible || this.mIsFeedbackVisible) ? getPaddingBottom() : 0);
            setMeasuredDimension(getMeasuredWidth(), scrollableAvailableHeight);
            this.mScrollExpectedHeight = paddingBottom;
            if (this.mIsDebugEnabled) {
                Log.d(TAG, "onMeasure: reMeasure pairingParentPanel height = " + scrollableAvailableHeight + ", scrollViewExpectedHeight = " + paddingBottom + ", paddingBottom = " + getPaddingBottom() + ", paddingTop = " + getPaddingTop());
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        layoutClosableIcon();
    }

    private void layoutClosableIcon() {
        int measuredWidth;
        if (this.mClosableIcon == null) {
            return;
        }
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
        int i = this.mClosableIconPositionTop;
        if (zIsLayoutRtl) {
            measuredWidth = this.mClosableIconPositionHorizontal;
        } else {
            measuredWidth = (getMeasuredWidth() - this.mClosableIcon.getMeasuredWidth()) - this.mClosableIconPositionHorizontal;
        }
        AppCompatImageView appCompatImageView = this.mClosableIcon;
        appCompatImageView.layout(measuredWidth, i, appCompatImageView.getMeasuredWidth() + measuredWidth, this.mClosableIcon.getMeasuredHeight() + i);
    }
}
