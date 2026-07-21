package miuix.miuixbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.TextViewCompat;
import com.android.deskclock.R2;
import java.util.ArrayList;
import miuix.core.util.EnvStateManager;
import miuix.miuixbasewidget.R;
import miuix.miuixbasewidget.widget.internal.TabViewContainerView;
import miuix.os.DeviceHelper;
import miuix.util.HapticFeedbackCompat;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class FilterSortView2 extends HorizontalScrollView {
    private final int mDeviceType;
    private boolean mEnabled;
    private int mFilteredId;
    protected boolean mIsParentApplyBlur;
    private int mLayoutConfig;
    private int mTabCount;
    private final ArrayList<Integer> mTabViewChildIds;
    private TabViewContainerView mTabViewContainerView;

    public FilterSortView2(Context context) {
        this(context, null);
    }

    public FilterSortView2(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.filterSortView2Style);
    }

    public FilterSortView2(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTabViewChildIds = new ArrayList<>();
        this.mFilteredId = -1;
        this.mIsParentApplyBlur = false;
        this.mTabCount = 0;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FilterSortView2, i, R.style.Widget_FilterSortView2_DayNight);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.FilterSortView2_filterSortViewBackground);
        this.mEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.FilterSortView2_android_enabled, true);
        this.mLayoutConfig = typedArrayObtainStyledAttributes.getInt(R.styleable.FilterSortView2_layoutConfig, 0);
        typedArrayObtainStyledAttributes.recycle();
        initContentView();
        setBackground(drawable);
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.mDeviceType = DeviceHelper.detectType(context);
        setOverScrollMode(2);
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0054  */
    /* JADX WARN: Code duplicated, block: B:21:0x005a  */
    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        float f = getContext().getResources().getDisplayMetrics().density;
        int i3 = this.mLayoutConfig;
        int i4 = 2;
        if (i3 == 0) {
            int i5 = (int) ((size * 1.0f) / f);
            int i6 = (int) ((EnvStateManager.getWindowSize(getContext()).x * 1.0f) / f);
            if (this.mDeviceType != 2 || i5 <= 410 || i6 <= 640) {
                i4 = 0;
            } else {
                i4 = 1;
            }
        } else if (i3 == 1) {
            int i7 = (int) ((EnvStateManager.getWindowSize(getContext()).x * 1.0f) / f);
            if (this.mDeviceType != 2 || i7 <= 640) {
                i4 = 0;
            } else {
                i4 = 1;
            }
        } else if (i3 == 3) {
            i4 = 1;
        } else if (i3 != 4) {
            i4 = 0;
        }
        this.mTabViewContainerView.setTabViewLayoutMode(i4);
        super.onMeasure(i, i2);
    }

    public boolean canScrollHorizontally() {
        View childAt = getChildAt(0);
        return childAt != null && childAt.getWidth() > getWidth();
    }

    public void setLayoutConfig(int i) {
        if (this.mLayoutConfig != i) {
            this.mLayoutConfig = i;
            requestLayout();
        }
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public void addView(View view) {
        addView(view, -1);
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public void addView(View view, int i) {
        if (view == null) {
            throw new IllegalArgumentException("Cannot add a null child view to a ViewGroup");
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = generateDefaultLayoutParams();
        }
        addView(view, i, layoutParams);
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup, android.view.ViewManager
    public void addView(View view, ViewGroup.LayoutParams layoutParams) {
        addView(view, -1, (ViewGroup.LayoutParams) null);
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (this.mTabViewContainerView == view) {
            super.addView(view, i, layoutParams);
        } else {
            checkView(view);
            addTab((TabView) view, i);
        }
    }

    private void checkView(View view) {
        if (!(view instanceof TabView)) {
            throw new IllegalArgumentException("Illegal View! Only support TabView!");
        }
    }

    public void setParentApplyBlur(boolean z) {
        if (this.mIsParentApplyBlur != z) {
            this.mIsParentApplyBlur = z;
        }
        TabViewContainerView tabViewContainerView = this.mTabViewContainerView;
        if (tabViewContainerView != null) {
            int childCount = tabViewContainerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = tabViewContainerView.getChildAt(i);
                if (childAt instanceof TabView) {
                    ((TabView) childAt).setSelected(z);
                }
            }
        }
    }

    private void initContentView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        TabViewContainerView tabViewContainerView = new TabViewContainerView(getContext());
        this.mTabViewContainerView = tabViewContainerView;
        tabViewContainerView.setLayoutParams(layoutParams);
        this.mTabViewContainerView.setHorizontalScrollBarEnabled(false);
        addView(this.mTabViewContainerView);
    }

    public boolean getEnabled() {
        return this.mEnabled;
    }

    public void addTabViewChildId(int i) {
        this.mTabViewChildIds.add(Integer.valueOf(i));
    }

    public void removeTabViewChildId(int i) {
        this.mTabViewChildIds.remove(Integer.valueOf(i));
    }

    public void clearTabViewChildIds() {
        this.mTabViewChildIds.clear();
    }

    public TabView addTab(CharSequence charSequence) {
        return addTab(charSequence, true);
    }

    public TabView addTab(CharSequence charSequence, boolean z) {
        TabView tabViewInflateTabView = inflateTabView();
        addTab(tabViewInflateTabView);
        tabViewInflateTabView.initView(charSequence, z);
        return tabViewInflateTabView;
    }

    private void addTab(TabView tabView) {
        addTab(tabView, -1);
    }

    private void addTab(TabView tabView, int i) {
        tabView.setEnabled(this.mEnabled);
        tabView.setSelected(this.mIsParentApplyBlur);
        addTabViewAt(tabView, i);
        this.mTabViewChildIds.add(Integer.valueOf(tabView.getId()));
    }

    private TabView inflateTabView() {
        return (TabView) LayoutInflater.from(getContext()).inflate(R.layout.layout_filter_tab_view2, (ViewGroup) null);
    }

    public void setTabIndicatorVisibility(int i) {
        for (int i2 = 0; i2 < this.mTabViewContainerView.getChildCount(); i2++) {
            View childAt = this.mTabViewContainerView.getChildAt(i2);
            if (childAt instanceof TabView) {
                ((TabView) childAt).setIndicatorVisibility(i);
            }
        }
    }

    public void setFilteredTab(TabView tabView) {
        if (this.mFilteredId != tabView.getId()) {
            this.mFilteredId = tabView.getId();
        }
        tabView.setFiltered(true);
        updateChildIdsFromXml();
    }

    protected void addTabViewAt(TabView tabView, int i) {
        if (tabView != null) {
            if (i > this.mTabCount || i < 0) {
                this.mTabViewContainerView.addView(tabView, -1, new FrameLayout.LayoutParams(-2, -2));
            } else {
                this.mTabViewContainerView.addView(tabView, i, new FrameLayout.LayoutParams(-2, -2));
            }
            this.mTabCount++;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TabView getTabViewAt(int i) {
        if (i <= -1) {
            return null;
        }
        View childAt = this.mTabViewContainerView.getChildAt((this.mTabViewContainerView.getChildCount() - this.mTabCount) + i);
        if (childAt instanceof TabView) {
            return (TabView) childAt;
        }
        return null;
    }

    protected void removeTabViewAt(int i) {
        if (i <= -1) {
            return;
        }
        View childAt = this.mTabViewContainerView.getChildAt(i);
        if (childAt instanceof TabView) {
            this.mTabViewContainerView.removeView(childAt);
            this.mTabCount--;
            removeTabViewChildId(childAt.getId());
        }
    }

    protected void removeAllTabViews() {
        this.mTabViewContainerView.removeAllViews();
        clearTabViewChildIds();
        this.mTabCount = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getTabCount() {
        return this.mTabCount;
    }

    public void setFilteredTab(int i) {
        TabView tabViewAt = getTabViewAt(i);
        if (tabViewAt != null) {
            if (this.mFilteredId != tabViewAt.getId()) {
                this.mFilteredId = tabViewAt.getId();
            }
            tabViewAt.setFiltered(true);
        }
        updateChildIdsFromXml();
    }

    protected void updateChildIdsFromXml() {
        if (this.mTabViewChildIds.isEmpty()) {
            int childCount = this.mTabViewContainerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.mTabViewContainerView.getChildAt(i);
                if (childAt instanceof TabView) {
                    this.mTabViewChildIds.add(Integer.valueOf(((TabView) childAt).getId()));
                }
            }
            requestLayout();
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (this.mEnabled != z) {
            this.mEnabled = z;
            refreshTabState();
        }
    }

    private void refreshTabState() {
        for (int i = 0; i < this.mTabViewContainerView.getChildCount(); i++) {
            View childAt = this.mTabViewContainerView.getChildAt(i);
            if (childAt instanceof TabView) {
                ((TabView) childAt).setEnabled(this.mEnabled);
            }
        }
    }

    public static class TabView extends FrameLayout {
        private int mActivatedTextAppearanceId;
        private ImageView mArrow;
        private Drawable mArrowIcon;
        private boolean mDescending;
        private boolean mDescendingEnabled;
        private boolean mFiltered;
        private HapticFeedbackCompat mHapticFeedbackCompat;
        private int mIndicatorVisibility;
        private OnFilteredListener mOnFilteredListener;
        private int mTextAppearanceId;
        private TextView mTextView;

        public interface OnFilteredListener {
            void onFilteredChanged(TabView tabView, boolean z);
        }

        public TabView(Context context) {
            this(context, null);
        }

        public TabView(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, R.attr.filterSortTabView2Style);
        }

        public TabView(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.mDescendingEnabled = true;
            LayoutInflater.from(context).inflate(getTabLayoutResource(), (ViewGroup) this, true);
            TextView textView = (TextView) findViewById(android.R.id.text1);
            this.mTextView = textView;
            textView.setMaxLines(1);
            this.mTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.mArrow = (ImageView) findViewById(R.id.arrow);
            if (attributeSet != null) {
                TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FilterSortTabView2, i, R.style.Widget_FilterSortTabView2_DayNight);
                String string = typedArrayObtainStyledAttributes.getString(R.styleable.FilterSortTabView2_android_text);
                boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.FilterSortTabView2_descending, true);
                this.mIndicatorVisibility = typedArrayObtainStyledAttributes.getInt(R.styleable.FilterSortTabView2_indicatorVisibility, 0);
                this.mArrowIcon = typedArrayObtainStyledAttributes.getDrawable(R.styleable.FilterSortTabView2_arrowFilterSortTabView);
                setBackground(typedArrayObtainStyledAttributes.getDrawable(R.styleable.FilterSortTabView2_filterSortTabViewBackground));
                setForeground(typedArrayObtainStyledAttributes.getDrawable(R.styleable.FilterSortTabView2_filterSortTabViewForeground));
                int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.FilterSortTabView2_filterSortTabViewHorizontalPadding, R.dimen.miuix_appcompat_filter_sort_tab_view2_padding_horizontal);
                int dimensionPixelSize2 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.FilterSortTabView2_filterSortTabViewVerticalPadding, R.dimen.miuix_appcompat_filter_sort_tab_view2_padding_vertical);
                findViewById(R.id.container).setPadding(dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize, dimensionPixelSize2);
                this.mTextAppearanceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.FilterSortTabView2_filterSortTabViewTabTextAppearance, 0);
                this.mActivatedTextAppearanceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.FilterSortTabView2_filterSortTabViewTabActivatedTextAppearance, 0);
                typedArrayObtainStyledAttributes.recycle();
                initView(string, z);
            }
            if (getId() == -1) {
                setId(generateViewId());
            }
            setImportantForAccessibility(1);
            ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() { // from class: miuix.miuixbasewidget.widget.FilterSortView2.TabView.1
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    accessibilityNodeInfoCompat.setSelected(view.isActivated());
                    TextView textView2 = (TextView) view.findViewById(android.R.id.text1);
                    if (textView2 != null && !TextUtils.isEmpty(textView2.getText())) {
                        accessibilityNodeInfoCompat.setContentDescription(textView2.getText());
                    }
                    if (view.isActivated()) {
                        accessibilityNodeInfoCompat.setClickable(false);
                        accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                    } else {
                        accessibilityNodeInfoCompat.setClickable(true);
                        accessibilityNodeInfoCompat.setStateDescription(TabView.this.getContext().getResources().getString(R.string.accessibility_tab_state_description_unselect));
                    }
                }
            });
        }

        public void setTextAppearance(int i) {
            this.mTextAppearanceId = i;
            updateTextAppearance();
        }

        public void setActivatedTextAppearance(int i) {
            this.mActivatedTextAppearanceId = i;
            updateTextAppearance();
        }

        private void updateTextAppearance() {
            if (this.mTextView != null) {
                if (isFiltered()) {
                    TextViewCompat.setTextAppearance(this.mTextView, this.mActivatedTextAppearanceId);
                } else {
                    TextViewCompat.setTextAppearance(this.mTextView, this.mTextAppearanceId);
                }
                requestLayout();
            }
        }

        protected int getTabLayoutResource() {
            return R.layout.miuix_appcompat_filter_sort_tab_view_2;
        }

        public TextView getTextView() {
            return this.mTextView;
        }

        public void setTextView(TextView textView) {
            this.mTextView = textView;
        }

        public ImageView getIconView() {
            return this.mArrow;
        }

        public void setIconView(ImageView imageView) {
            this.mArrow = imageView;
        }

        public void setOnFilteredListener(OnFilteredListener onFilteredListener) {
            this.mOnFilteredListener = onFilteredListener;
        }

        protected void initView(CharSequence charSequence, boolean z) {
            this.mArrow.setBackground(this.mArrowIcon);
            this.mTextView.setText(charSequence);
            this.mArrow.setVisibility(this.mIndicatorVisibility);
            setDescending(z);
            updateTextAppearance();
        }

        public void setIndicatorVisibility(int i) {
            this.mArrow.setVisibility(i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFiltered(boolean z) {
            TabView tabView;
            ViewGroup viewGroup = (ViewGroup) getParent();
            if (z && viewGroup != null) {
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if ((childAt instanceof TabView) && (tabView = (TabView) childAt) != this && tabView.mFiltered) {
                        tabView.setFiltered(false);
                    }
                }
            }
            this.mFiltered = z;
            updateTextAppearance();
            this.mTextView.setActivated(z);
            this.mArrow.setActivated(z);
            setActivated(z);
            if (viewGroup != null && z) {
                viewGroup.post(new Runnable() { // from class: miuix.miuixbasewidget.widget.FilterSortView2$TabView$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1869x7cc1fbb5();
                    }
                });
            }
        }

        /* JADX INFO: renamed from: lambda$setFiltered$0$miuix-miuixbasewidget-widget-FilterSortView2$TabView, reason: not valid java name */
        /* synthetic */ void m1869x7cc1fbb5() {
            OnFilteredListener onFilteredListener = this.mOnFilteredListener;
            if (onFilteredListener != null) {
                onFilteredListener.onFilteredChanged(this, true);
            }
        }

        public boolean isFiltered() {
            return this.mFiltered;
        }

        private void setDescending(boolean z) {
            this.mDescending = z;
            if (z) {
                this.mArrow.setRotationX(0.0f);
            } else {
                this.mArrow.setRotationX(180.0f);
            }
        }

        public boolean isDescending() {
            return this.mDescending;
        }

        public boolean getDescendingEnabled() {
            return this.mDescendingEnabled;
        }

        public void setDescendingEnabled(boolean z) {
            this.mDescendingEnabled = z;
        }

        public View getArrowView() {
            return this.mArrow;
        }

        @Override // android.view.View
        public void setOnClickListener(final View.OnClickListener onClickListener) {
            super.setOnClickListener(new View.OnClickListener() { // from class: miuix.miuixbasewidget.widget.FilterSortView2$TabView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1870xda6a4c18(onClickListener, view);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$setOnClickListener$1$miuix-miuixbasewidget-widget-FilterSortView2$TabView, reason: not valid java name */
        /* synthetic */ void m1870xda6a4c18(View.OnClickListener onClickListener, View view) {
            if (this.mFiltered) {
                if (this.mDescendingEnabled) {
                    setDescending(!this.mDescending);
                }
            } else {
                setFiltered(true);
            }
            onClickListener.onClick(view);
            if (HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2)) {
                getHapticFeedbackCompat().m1943x85658b2f(R2.attr.actionBarRemoveBlacklistIcon);
            } else {
                HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_MESH_NORMAL);
            }
        }

        private HapticFeedbackCompat getHapticFeedbackCompat() {
            if (this.mHapticFeedbackCompat == null) {
                this.mHapticFeedbackCompat = new HapticFeedbackCompat(getContext());
            }
            return this.mHapticFeedbackCompat;
        }

        @Override // android.view.View
        public void setEnabled(boolean z) {
            super.setEnabled(z);
            this.mTextView.setEnabled(z);
        }
    }
}
