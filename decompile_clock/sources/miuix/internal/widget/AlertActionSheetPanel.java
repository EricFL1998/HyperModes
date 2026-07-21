package miuix.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import miuix.appcompat.R;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowUtils;
import miuix.os.DeviceHelper;

/* JADX INFO: loaded from: classes2.dex */
public class AlertActionSheetPanel extends LinearLayout {
    private static final float mMaxHeightMajor = 0.7f;
    private final Context mContext;
    private int mFreePhoneCompatHeight;
    private int mFreeTabletCompatHeight;
    private int mMaxHeight;
    private int mNormalMargin;
    private final Point mScreenSize;
    private int mSeparateItemMarginTop;

    public AlertActionSheetPanel(Context context) {
        this(context, null, 0);
    }

    public AlertActionSheetPanel(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AlertActionSheetPanel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mScreenSize = new Point();
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        this.mSeparateItemMarginTop = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_content_separate_item_margin_top);
        WindowUtils.getScreenSize(context, this.mScreenSize);
        this.mFreePhoneCompatHeight = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_freeform_bottom_height_phone_t);
        this.mFreeTabletCompatHeight = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_freeform_bottom_height_tablet_t);
        this.mNormalMargin = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_width_margin);
        this.mMaxHeight = (int) (this.mScreenSize.y * mMaxHeightMajor);
    }

    private int getAvailableMaxHeightInFreeForm() {
        int i;
        int i2;
        WindowInsets rootWindowInsets = getRootWindowInsets();
        if (rootWindowInsets == null || Build.VERSION.SDK_INT < 30) {
            i = 0;
            i2 = 0;
        } else {
            Insets insets = rootWindowInsets.getInsets(WindowInsets.Type.systemBars());
            i = insets.top;
            i2 = insets.bottom;
        }
        if (i == 0) {
            i = (miuix.os.Build.IS_TABLET ? this.mFreeTabletCompatHeight : this.mFreePhoneCompatHeight) + this.mNormalMargin;
        }
        if (i2 == 0) {
            i2 = (miuix.os.Build.IS_TABLET ? this.mFreeTabletCompatHeight : this.mFreePhoneCompatHeight) + this.mNormalMargin;
        }
        return EnvStateManager.getWindowSize(this.mContext).y - (i + i2);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        ViewGroup viewGroup;
        ViewGroup viewGroup2;
        int measuredHeight;
        int size = View.MeasureSpec.getSize(i2);
        int measuredHeight2 = 0;
        boolean z = miuix.os.Build.IS_FLIP && DeviceHelper.isTinyScreen(this.mContext);
        boolean z2 = this.mScreenSize.y > this.mScreenSize.x;
        boolean z3 = MiuixUIUtils.px2dp(this.mContext, (float) this.mScreenSize.y) >= 500;
        if (EnvStateManager.isFreeFormMode(this.mContext)) {
            i2 = View.MeasureSpec.makeMeasureSpec(getAvailableMaxHeightInFreeForm(), Integer.MIN_VALUE);
        } else if (!z && ((z2 || z3) && size > (i3 = this.mMaxHeight))) {
            i2 = View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE);
        }
        super.onMeasure(i, i2);
        int size2 = View.MeasureSpec.getSize(i2);
        if (getChildCount() >= 2) {
            viewGroup = (ViewGroup) getChildAt(0);
            viewGroup2 = (ViewGroup) getChildAt(1);
        } else {
            viewGroup = null;
            viewGroup2 = null;
        }
        int i4 = this.mSeparateItemMarginTop;
        if (viewGroup2 != null) {
            measureChild(viewGroup2, i, i2);
            measuredHeight = viewGroup2.getMeasuredHeight();
            i4 += measuredHeight;
        } else {
            measuredHeight = 0;
        }
        if (viewGroup != null) {
            measureChild(viewGroup, i, i2);
            measuredHeight2 = viewGroup.getMeasuredHeight();
        }
        int i5 = this.mSeparateItemMarginTop;
        if (measuredHeight2 + measuredHeight + i5 <= size2 || viewGroup == null) {
            return;
        }
        viewGroup.measure(i, View.MeasureSpec.makeMeasureSpec((size2 - measuredHeight) - i5, BasicMeasure.EXACTLY));
        setMeasuredDimension(getMeasuredWidth(), i4 + viewGroup.getMeasuredHeight());
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        WindowUtils.getScreenSize(this.mContext, this.mScreenSize);
        this.mMaxHeight = (int) (this.mScreenSize.y * mMaxHeightMajor);
    }
}
