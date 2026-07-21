package miuix.appcompat.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import miuix.appcompat.R;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.ViewUtils;
import miuix.view.DensityChangedHelper;

/* JADX INFO: loaded from: classes2.dex */
public class GroupButtonsPanel extends FrameLayout {
    public static final float BUTTON_TEXT_SIZE_NORMAL = 17.0f;
    public static final float BUTTON_TEXT_SIZE_SMALL = 14.0f;
    private int mButtonGroupDividerSize;
    private int mButtonGroupMaxWidth;
    private LinearLayout mContentView;
    private int mExtraPadding;
    private boolean mHandleWindowInsetsEnabled;
    private int mOriginPaddingBottom;
    private int mOriginPaddingLeft;
    private int mOriginPaddingRight;
    private Runnable mResetPanelPaddingBottomRunnable;

    public GroupButtonsPanel(Context context) {
        super(context);
        this.mHandleWindowInsetsEnabled = true;
        init(context);
    }

    public GroupButtonsPanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHandleWindowInsetsEnabled = true;
        init(context);
    }

    public GroupButtonsPanel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHandleWindowInsetsEnabled = true;
        init(context);
    }

    public GroupButtonsPanel(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHandleWindowInsetsEnabled = true;
        init(context);
    }

    private void init(Context context) {
        this.mButtonGroupMaxWidth = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_button_max_width);
        this.mButtonGroupDividerSize = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_group_buttons_divider_size);
        this.mOriginPaddingBottom = getPaddingBottom();
        this.mOriginPaddingLeft = getPaddingLeft();
        this.mOriginPaddingRight = getPaddingRight();
        this.mResetPanelPaddingBottomRunnable = new Runnable() { // from class: miuix.appcompat.app.GroupButtonsPanel$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1814lambda$init$0$miuixappcompatappGroupButtonsPanel();
            }
        };
        applyWindowInsets();
    }

    /* JADX INFO: renamed from: lambda$init$0$miuix-appcompat-app-GroupButtonsPanel, reason: not valid java name */
    /* synthetic */ void m1814lambda$init$0$miuixappcompatappGroupButtonsPanel() {
        WindowInsetsCompat rootWindowInsets = ViewCompat.getRootWindowInsets(this);
        ViewUtils.resetPaddingBottom(this, this.mOriginPaddingBottom + ((rootWindowInsets == null || !isLayoutHideNavigation()) ? 0 : rootWindowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom));
    }

    private boolean isLayoutHideNavigation() {
        return MiuixUIUtils.isLayoutHideNavigation(this);
    }

    private void applyWindowInsets() {
        ViewUtils.doOnApplyWindowInsets(this, new ViewUtils.OnApplyWindowInsetsListener() { // from class: miuix.appcompat.app.GroupButtonsPanel$$ExternalSyntheticLambda0
            @Override // miuix.internal.util.ViewUtils.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
                return this.f$0.m1813lambda$applyWindowInsets$1$miuixappcompatappGroupButtonsPanel(view, windowInsetsCompat, relativePadding);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$applyWindowInsets$1$miuix-appcompat-app-GroupButtonsPanel, reason: not valid java name */
    /* synthetic */ WindowInsetsCompat m1813lambda$applyWindowInsets$1$miuixappcompatappGroupButtonsPanel(View view, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
        if (this.mHandleWindowInsetsEnabled) {
            post(this.mResetPanelPaddingBottomRunnable);
        }
        return windowInsetsCompat;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContentView = (LinearLayout) getChildAt(0);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        boolean z = this.mContentView.getOrientation() == 1;
        int size = (View.MeasureSpec.getSize(i) - this.mOriginPaddingLeft) - this.mOriginPaddingRight;
        int iMin = Math.min(this.mButtonGroupMaxWidth, size);
        int i3 = this.mButtonGroupMaxWidth;
        if (i3 < size && !z) {
            this.mExtraPadding = (size - i3) / 2;
        }
        if (z) {
            resizeButtonTextSize(iMin);
        } else {
            int contentVisibleChildCount = getContentVisibleChildCount();
            if (contentVisibleChildCount >= 1) {
                resizeButtonTextSize((iMin - (this.mButtonGroupDividerSize * (contentVisibleChildCount - 1))) / contentVisibleChildCount);
            }
        }
        super.onMeasure(i, i2);
        int i4 = this.mExtraPadding;
        if (i4 > 0) {
            measureChild(this.mContentView, View.MeasureSpec.makeMeasureSpec((size - (i4 * 2)) + this.mOriginPaddingLeft + this.mOriginPaddingRight, View.MeasureSpec.getMode(i)), i2);
        }
    }

    public boolean isAllChildrenInvisible() {
        boolean z = true;
        for (int i = 0; i < this.mContentView.getChildCount(); i++) {
            z = z && (this.mContentView.getChildAt(i).getVisibility() != 0);
        }
        return z;
    }

    public int getContentVisibleChildCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.mContentView.getChildCount(); i2++) {
            if (this.mContentView.getChildAt(i2).getVisibility() != 8) {
                i++;
            }
        }
        return i;
    }

    public void setHandleWindowInsetsEnabled(boolean z) {
        if (this.mHandleWindowInsetsEnabled != z) {
            this.mHandleWindowInsetsEnabled = z;
        }
    }

    private void resizeButtonTextSize(int i) {
        for (int i2 = 0; i2 < this.mContentView.getChildCount(); i2++) {
            View childAt = this.mContentView.getChildAt(i2);
            if ((childAt instanceof Button) && childAt.getVisibility() == 0) {
                Button button = (Button) childAt;
                DensityChangedHelper.updateTextSizeSpUnit(button, 17.0f);
                if (isEllipsized(button, i)) {
                    for (int i3 = 0; i3 < this.mContentView.getChildCount(); i3++) {
                        View childAt2 = this.mContentView.getChildAt(i3);
                        if (childAt2 instanceof Button) {
                            DensityChangedHelper.updateTextSizeSpUnit((Button) childAt2, 14.0f);
                        }
                    }
                    return;
                }
            }
        }
    }

    private boolean isEllipsized(Button button, int i) {
        return ((int) button.getPaint().measureText(button.getText().toString())) > Math.min(i, Math.max(0, (button.getMaxWidth() - button.getPaddingStart()) - button.getPaddingEnd()));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Runnable runnable = this.mResetPanelPaddingBottomRunnable;
        if (runnable != null) {
            removeCallbacks(runnable);
            this.mResetPanelPaddingBottomRunnable = null;
        }
    }
}
