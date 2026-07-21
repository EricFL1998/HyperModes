package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import miuix.appcompat.R;
import miuix.core.util.EnvStateManager;
import miuix.miuixbasewidget.widget.FilterSortView;
import miuix.os.DeviceHelper;

/* JADX INFO: loaded from: classes2.dex */
public class SecondarySegmentTabBar extends FilterSortView implements SecondaryTabBar {
    private static final int WIDE_LESS_THAN_TWO_ITEM_DP = 220;
    private static final int WIDE_MORE_THAN_FOUR_ITEM_DP = 150;
    private static final int WIDE_THREE_ITEM_DP = 180;
    private final int mDeviceType;
    private final int mLayoutConfig;
    private View.OnClickListener mOnTabClickListener;

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public ViewGroup asViewGroup() {
        return this;
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setBadgeVisibility(int i, boolean z) {
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setParentBlurEnabled(boolean z) {
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTabBadgeDisappearOnClick(int i, boolean z) {
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTabIconWithPosition(int i, int i2, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTextAppearance(int i, int i2) {
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTextAppearance(int i, int i2, int i3) {
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void updateTab(int i) {
    }

    public SecondarySegmentTabBar(Context context) {
        this(context, null);
    }

    public SecondarySegmentTabBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.segmentTabBarStyle);
    }

    public SecondarySegmentTabBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SecondarySegmentTabBar, i, 0);
        this.mLayoutConfig = typedArrayObtainStyledAttributes.getInt(R.styleable.SecondarySegmentTabBar_segmentTabBarLayoutConfig, 0);
        typedArrayObtainStyledAttributes.recycle();
        this.mDeviceType = DeviceHelper.detectType(context);
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0053  */
    /* JADX WARN: Code duplicated, block: B:19:0x0063  */
    /* JADX WARN: Code duplicated, block: B:21:0x0069 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:22:0x006b  */
    /* JADX WARN: Code duplicated, block: B:23:0x006e  */
    /* JADX WARN: Code duplicated, block: B:26:0x0074 A[ADDED_TO_REGION] */
    @Override // miuix.miuixbasewidget.widget.FilterSortView, androidx.constraintlayout.widget.ConstraintLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int tabCount;
        int i3;
        int i4;
        int i5;
        int size = View.MeasureSpec.getSize(i);
        float f = getContext().getResources().getDisplayMetrics().density;
        int i6 = this.mLayoutConfig;
        if (i6 == 0) {
            int i7 = (int) ((size * 1.0f) / f);
            int i8 = (int) ((EnvStateManager.getWindowSize(getContext()).x * 1.0f) / f);
            if (this.mDeviceType == 2 && i7 > 410 && i8 > 640) {
                tabCount = getTabCount();
                int paddingLeft = getPaddingLeft() + getPaddingRight();
                i3 = size - paddingLeft;
                if (tabCount <= 2) {
                    i4 = tabCount * 220;
                } else if (tabCount == 3) {
                    i4 = tabCount * 180;
                } else {
                    i4 = tabCount * 150;
                }
                i5 = ((int) (i4 * f)) + paddingLeft;
                if (i3 >= i5 && tabCount > 0) {
                    i = View.MeasureSpec.makeMeasureSpec(i5, BasicMeasure.EXACTLY);
                }
            }
        } else if (i6 == 1) {
            int i9 = (int) ((EnvStateManager.getWindowSize(getContext()).x * 1.0f) / f);
            if (this.mDeviceType == 2 && i9 > 640) {
                tabCount = getTabCount();
                int paddingLeft2 = getPaddingLeft() + getPaddingRight();
                i3 = size - paddingLeft2;
                if (tabCount <= 2) {
                    i4 = tabCount * 220;
                } else if (tabCount == 3) {
                    i4 = tabCount * 180;
                } else {
                    i4 = tabCount * 150;
                }
                i5 = ((int) (i4 * f)) + paddingLeft2;
                if (i3 >= i5) {
                    i = View.MeasureSpec.makeMeasureSpec(i5, BasicMeasure.EXACTLY);
                }
            }
        } else if (i6 == 3) {
            tabCount = getTabCount();
            int paddingLeft3 = getPaddingLeft() + getPaddingRight();
            i3 = size - paddingLeft3;
            if (tabCount <= 2) {
                i4 = tabCount * 220;
            } else if (tabCount == 3) {
                i4 = tabCount * 180;
            } else {
                i4 = tabCount * 150;
            }
            i5 = ((int) (i4 * f)) + paddingLeft3;
            if (i3 >= i5) {
                i = View.MeasureSpec.makeMeasureSpec(i5, BasicMeasure.EXACTLY);
            }
        }
        super.onMeasure(i, i2);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void addTab(ActionBar.Tab tab, boolean z) {
        addTab(tab, -1, z);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void addTab(ActionBar.Tab tab, int i, boolean z) {
        SecondarySegmentTabView secondarySegmentTabViewCreateTabView = createTabView(tab);
        addTab(secondarySegmentTabViewCreateTabView, tab.getText().toString(), i, z);
        if (this.mOnTabClickListener == null) {
            this.mOnTabClickListener = createOnTabClickListener();
        }
        secondarySegmentTabViewCreateTabView.setOnClickListener(this.mOnTabClickListener);
        if (z) {
            setFilteredTab(secondarySegmentTabViewCreateTabView);
        }
    }

    private SecondarySegmentTabView createTabView(ActionBar.Tab tab) {
        SecondarySegmentTabView secondarySegmentTabView = new SecondarySegmentTabView(getContext());
        secondarySegmentTabView.attach(tab);
        return secondarySegmentTabView;
    }

    private View.OnClickListener createOnTabClickListener() {
        return new View.OnClickListener() { // from class: miuix.appcompat.internal.app.widget.SecondarySegmentTabBar$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SecondarySegmentTabBar.lambda$createOnTabClickListener$0(view);
            }
        };
    }

    static /* synthetic */ void lambda$createOnTabClickListener$0(View view) {
        ActionBar.Tab tab;
        if (!(view instanceof SecondarySegmentTabView) || (tab = ((SecondarySegmentTabView) view).getTab()) == null) {
            return;
        }
        tab.select();
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void removeTabAt(int i) {
        removeTabViewAt(i);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void removeAllTabs() {
        removeAllTabViews();
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void animateToTab(int i) {
        setFilteredTab(i);
    }

    @Override // miuix.appcompat.internal.app.widget.SecondaryTabBar
    public void setTabSelected(int i) {
        setFilteredTab(i);
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageSelected(int i) {
        setFilteredTab(i);
    }

    public static class SecondarySegmentTabView extends FilterSortView.TabView {
        private ActionBar.Tab mTab;

        public SecondarySegmentTabView(Context context) {
            this(context, null);
        }

        public SecondarySegmentTabView(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, R.attr.segmentTabViewStyle);
        }

        public SecondarySegmentTabView(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }

        public void attach(ActionBar.Tab tab) {
            this.mTab = tab;
        }

        public ActionBar.Tab getTab() {
            return this.mTab;
        }
    }
}
