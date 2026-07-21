package miuix.miuixbasewidget.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.TextViewCompat;
import com.android.deskclock.R2;
import java.util.ArrayList;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.property.ViewProperty;
import miuix.miuixbasewidget.R;
import miuix.util.HapticFeedbackCompat;
import miuix.view.CompatViewMethod;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class FilterSortView extends ConstraintLayout {
    public static final int GONE = 8;
    private static final String TAG = "miuix:FilterSortView";
    public static final int VISIBLE = 0;
    private TabView mBackgroundTabView;
    private boolean mEnabled;
    private final TabView.FilterHoverListener mFilterHoverListener;
    private int mFilteredId;
    private boolean mFilteredUpdated;
    private View mHoverBgView;
    private final TabView.OnFilteredListener mOnFilteredListener;
    private final int mPadding;
    private int mTabCount;
    private final List<Integer> mTabViewChildIds;

    public FilterSortView(Context context) {
        this(context, null);
    }

    public FilterSortView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FilterSortView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTabViewChildIds = new ArrayList();
        this.mFilteredId = -1;
        this.mEnabled = true;
        this.mFilteredUpdated = false;
        this.mTabCount = 0;
        this.mOnFilteredListener = new TabView.OnFilteredListener() { // from class: miuix.miuixbasewidget.widget.FilterSortView.1
            private BackgroundTabRunnable mRunnable;
            private IStateStyle mRunningAnimation;

            @Override // miuix.miuixbasewidget.widget.FilterSortView.TabView.OnFilteredListener
            public void onFilteredChanged(TabView tabView, boolean z, boolean z2) {
                BackgroundTabRunnable backgroundTabRunnable = this.mRunnable;
                if (backgroundTabRunnable != null) {
                    backgroundTabRunnable.cancelAnimation();
                }
                if (z) {
                    FilterSortView.this.checkBackgroundTabViewAdded();
                    BackgroundTabRunnable backgroundTabRunnable2 = new BackgroundTabRunnable(FilterSortView.this.mBackgroundTabView, tabView, z2);
                    this.mRunnable = backgroundTabRunnable2;
                    tabView.post(backgroundTabRunnable2);
                }
            }

            @Override // miuix.miuixbasewidget.widget.FilterSortView.TabView.OnFilteredListener
            public void cancelAnimation(View view) {
                BackgroundTabRunnable backgroundTabRunnable = this.mRunnable;
                if (backgroundTabRunnable != null) {
                    backgroundTabRunnable.cancelAnimation();
                    view.removeCallbacks(this.mRunnable);
                    this.mRunnable = null;
                }
                IStateStyle iStateStyle = this.mRunningAnimation;
                if (iStateStyle != null) {
                    iStateStyle.cancel();
                    this.mRunningAnimation = null;
                }
            }

            /* JADX INFO: renamed from: miuix.miuixbasewidget.widget.FilterSortView$1$BackgroundTabRunnable */
            class BackgroundTabRunnable implements Runnable {
                private final boolean mAnimate;
                private TabView mBackgroundTabView;
                private boolean mCanceled = false;
                private IStateStyle mRunningAnimation;
                private TabView mSelectedTabView;

                public BackgroundTabRunnable(TabView tabView, TabView tabView2, boolean z) {
                    this.mBackgroundTabView = tabView;
                    this.mSelectedTabView = tabView2;
                    this.mAnimate = z;
                }

                @Override // java.lang.Runnable
                public void run() {
                    TabView tabView;
                    if (this.mCanceled || (tabView = this.mBackgroundTabView) == null || this.mSelectedTabView == null) {
                        return;
                    }
                    if (tabView.getVisibility() == 0) {
                        if (this.mAnimate) {
                            this.mRunningAnimation = Folme.useAt(this.mBackgroundTabView).state().setFlags(1L).to(new AnimState(TypedValues.AttributesType.S_TARGET).add(ViewProperty.X, this.mSelectedTabView.getX()).add(ViewProperty.WIDTH, this.mSelectedTabView.getWidth()), new AnimConfig[0]);
                        } else {
                            FilterSortView.this.updateFiltered(this.mSelectedTabView);
                        }
                    }
                    FilterSortView.this.mFilteredId = this.mSelectedTabView.getId();
                }

                void cancelAnimation() {
                    this.mCanceled = true;
                    IStateStyle iStateStyle = this.mRunningAnimation;
                    if (iStateStyle != null) {
                        iStateStyle.cancel();
                    }
                    TabView tabView = this.mSelectedTabView;
                    if (tabView != null) {
                        tabView.removeCallbacks(this);
                    }
                    this.mBackgroundTabView = null;
                    this.mSelectedTabView = null;
                }
            }
        };
        this.mFilterHoverListener = new TabView.FilterHoverListener() { // from class: miuix.miuixbasewidget.widget.FilterSortView.2
            @Override // miuix.miuixbasewidget.widget.FilterSortView.TabView.FilterHoverListener
            public void onHoverFilterEnter() {
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(FilterSortView.this.mBackgroundTabView, "scaleX", FilterSortView.this.mBackgroundTabView.getScaleX(), 1.05f);
                ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(FilterSortView.this.mBackgroundTabView, "scaleY", FilterSortView.this.mBackgroundTabView.getScaleY(), 1.05f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimatorOfFloat, objectAnimatorOfFloat2);
                animatorSet.setDuration(350L);
                animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
                animatorSet.start();
            }

            @Override // miuix.miuixbasewidget.widget.FilterSortView.TabView.FilterHoverListener
            public void onHoverFilterExit() {
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(FilterSortView.this.mBackgroundTabView, "scaleX", FilterSortView.this.mBackgroundTabView.getScaleX(), 1.0f);
                ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(FilterSortView.this.mBackgroundTabView, "scaleY", FilterSortView.this.mBackgroundTabView.getScaleY(), 1.0f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimatorOfFloat, objectAnimatorOfFloat2);
                animatorSet.setDuration(350L);
                animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
                animatorSet.start();
            }

            @Override // miuix.miuixbasewidget.widget.FilterSortView.TabView.FilterHoverListener
            public void onHoverEnter() {
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(FilterSortView.this.mHoverBgView, "alpha", FilterSortView.this.mHoverBgView.getAlpha(), 1.0f);
                objectAnimatorOfFloat.setDuration(350L);
                objectAnimatorOfFloat.setInterpolator(new DecelerateInterpolator(1.5f));
                objectAnimatorOfFloat.start();
            }

            @Override // miuix.miuixbasewidget.widget.FilterSortView.TabView.FilterHoverListener
            public void onHoverExit(float f, float f2) {
                if (f < FilterSortView.this.mPadding || f2 < 0.0f || f > (FilterSortView.this.getRight() - FilterSortView.this.getLeft()) - (FilterSortView.this.mPadding * 2) || f2 > (FilterSortView.this.getBottom() - FilterSortView.this.getTop()) - (FilterSortView.this.mPadding * 2)) {
                    ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(FilterSortView.this.mHoverBgView, "alpha", FilterSortView.this.mHoverBgView.getAlpha(), 0.0f);
                    objectAnimatorOfFloat.setDuration(350L);
                    objectAnimatorOfFloat.setInterpolator(new DecelerateInterpolator(1.5f));
                    objectAnimatorOfFloat.start();
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FilterSortView, i, R.style.Widget_FilterSortView_DayNight);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.FilterSortView_filterSortViewBackground);
        Drawable drawable2 = typedArrayObtainStyledAttributes.getDrawable(R.styleable.FilterSortView_filterSortTabViewCoverBg);
        this.mEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.FilterSortView_android_enabled, true);
        typedArrayObtainStyledAttributes.recycle();
        this.mPadding = getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_filter_sort_view_padding);
        setBackground(drawable);
        initHoverBgView();
        initCoverBg(drawable2);
        CompatViewMethod.setForceDarkAllowed(this, false);
    }

    private void initHoverBgView() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, 0);
        View view = new View(getContext());
        this.mHoverBgView = view;
        view.setLayoutParams(layoutParams);
        this.mHoverBgView.setId(View.generateViewId());
        this.mHoverBgView.setBackgroundResource(R.drawable.miuix_appcompat_filter_sort_hover_bg);
        this.mHoverBgView.setAlpha(0.0f);
        addView(this.mHoverBgView);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        constraintSet.connect(this.mHoverBgView.getId(), 3, getId(), 3);
        constraintSet.connect(this.mHoverBgView.getId(), 4, getId(), 4);
        constraintSet.connect(this.mHoverBgView.getId(), 6, getId(), 6);
        constraintSet.connect(this.mHoverBgView.getId(), 7, getId(), 7);
        constraintSet.applyTo(this);
    }

    private void initCoverBg(Drawable drawable) {
        TabView tabViewInflateTabView = inflateTabView();
        this.mBackgroundTabView = tabViewInflateTabView;
        tabViewInflateTabView.setBackground(drawable);
        this.mBackgroundTabView.mArrow.setVisibility(8);
        this.mBackgroundTabView.mTextView.setVisibility(8);
        this.mBackgroundTabView.setVisibility(4);
        this.mBackgroundTabView.setEnabled(this.mEnabled);
        this.mBackgroundTabView.setImportantForAccessibility(2);
        addView(this.mBackgroundTabView);
    }

    public TabView.OnFilteredListener getOnFilteredListener() {
        return this.mOnFilteredListener;
    }

    public boolean getEnabled() {
        return this.mEnabled;
    }

    public TabView.FilterHoverListener getFilterHoverListener() {
        return this.mFilterHoverListener;
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
        return addTab(charSequence, -1, true);
    }

    public TabView addTab(CharSequence charSequence, int i) {
        return addTab(charSequence, i, true);
    }

    public TabView addTab(CharSequence charSequence, boolean z) {
        return addTab(charSequence, -1, z);
    }

    public TabView addTab(CharSequence charSequence, int i, boolean z) {
        TabView tabViewInflateTabView = inflateTabView();
        addTab(tabViewInflateTabView, charSequence, i, z);
        return tabViewInflateTabView;
    }

    public void addTab(TabView tabView, CharSequence charSequence, int i, boolean z) {
        tabView.setOnFilteredListener(this.mOnFilteredListener);
        tabView.setEnabled(this.mEnabled);
        tabView.setFilterHoverListener(this.mFilterHoverListener);
        this.mFilteredUpdated = false;
        if (i < 0 || i > this.mTabCount) {
            addView(tabView, -1);
        } else {
            addView(tabView, i);
        }
        this.mTabCount++;
        this.mTabViewChildIds.add(Integer.valueOf(tabView.getId()));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        updateTabViews(constraintSet);
        constraintSet.applyTo(this);
        tabView.initView(charSequence, z);
    }

    private TabView inflateTabView() {
        return (TabView) LayoutInflater.from(getContext()).inflate(R.layout.layout_filter_tab_view, (ViewGroup) null);
    }

    public void setTabIncatorVisibility(int i) {
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof TabView) {
                ((TabView) childAt).setIndicatorVisibility(i);
            }
        }
    }

    public void setFilteredTab(int i) {
        TabView tabViewAt = getTabViewAt(i);
        if (tabViewAt != null) {
            setFilteredTab(tabViewAt);
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
                addView(tabView);
            } else {
                addView(tabView, (getChildCount() - this.mTabCount) + i);
            }
            this.mTabCount++;
        }
    }

    protected TabView getTabViewAt(int i) {
        if (i <= -1) {
            return null;
        }
        View childAt = getChildAt((getChildCount() - this.mTabCount) + i);
        if (childAt instanceof TabView) {
            return (TabView) childAt;
        }
        return null;
    }

    protected void removeTabViewAt(int i) {
        if (i <= -1) {
            return;
        }
        int childCount = (getChildCount() - this.mTabCount) + i;
        if (getChildAt(childCount) instanceof TabView) {
            removeViewAt(childCount);
        }
        this.mTabCount--;
    }

    protected void removeAllTabViews() {
        removeAllViews();
        this.mTabCount = 0;
    }

    protected int getTabCount() {
        return this.mTabCount;
    }

    protected void setFilteredUpdated(boolean z) {
        this.mFilteredUpdated = z;
    }

    protected void updateChildIdsFromXml() {
        if (this.mTabViewChildIds.size() == 0) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt instanceof TabView) {
                    TabView tabView = (TabView) childAt;
                    if (tabView.getId() != this.mBackgroundTabView.getId()) {
                        tabView.setOnFilteredListener(this.mOnFilteredListener);
                        this.mTabViewChildIds.add(Integer.valueOf(tabView.getId()));
                        tabView.setFilterHoverListener(this.mFilterHoverListener);
                    }
                }
            }
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(this);
            updateTabViews(constraintSet);
            constraintSet.applyTo(this);
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
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof TabView) {
                ((TabView) childAt).setEnabled(this.mEnabled);
            }
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mFilteredUpdated = false;
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        TabView tabView;
        super.onMeasure(i, i2);
        if (this.mFilteredId == -1 || this.mBackgroundTabView.getVisibility() == 8 || (tabView = (TabView) findViewById(this.mFilteredId)) == null) {
            return;
        }
        this.mBackgroundTabView.measure(View.MeasureSpec.makeMeasureSpec(tabView.getMeasuredWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - (this.mPadding * 2), BasicMeasure.EXACTLY));
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        TabView tabView;
        super.onLayout(z, i, i2, i3, i4);
        if (this.mBackgroundTabView.getVisibility() != 8) {
            int left = this.mBackgroundTabView.getLeft();
            int i5 = this.mPadding;
            this.mBackgroundTabView.layout(left, i5, this.mBackgroundTabView.getMeasuredWidth() + left, this.mBackgroundTabView.getMeasuredHeight() + i5);
        }
        int i6 = this.mFilteredId;
        if (i6 == -1 || this.mFilteredUpdated || (tabView = (TabView) findViewById(i6)) == null) {
            return;
        }
        updateFiltered(tabView);
        if (tabView.getWidth() > 0) {
            this.mFilteredUpdated = true;
        }
    }

    public void checkBackgroundTabViewAdded() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) == this.mBackgroundTabView) {
                return;
            }
        }
        addView(this.mBackgroundTabView, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFiltered(TabView tabView) {
        this.mOnFilteredListener.cancelAnimation(tabView);
        if (this.mBackgroundTabView.getVisibility() != 0) {
            this.mBackgroundTabView.setVisibility(0);
        }
        final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) this.mBackgroundTabView.getLayoutParams();
        this.mBackgroundTabView.setX(tabView.getX());
        this.mBackgroundTabView.setY(this.mPadding);
        post(new Runnable() { // from class: miuix.miuixbasewidget.widget.FilterSortView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1867x81c20674(layoutParams);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$updateFiltered$0$miuix-miuixbasewidget-widget-FilterSortView, reason: not valid java name */
    /* synthetic */ void m1867x81c20674(ConstraintLayout.LayoutParams layoutParams) {
        this.mBackgroundTabView.setLayoutParams(layoutParams);
    }

    protected void updateTabViews(ConstraintSet constraintSet) {
        int i = 0;
        while (i < this.mTabViewChildIds.size()) {
            int iIntValue = this.mTabViewChildIds.get(i).intValue();
            constraintSet.constrainWidth(iIntValue, 0);
            constraintSet.constrainHeight(iIntValue, -2);
            constraintSet.setHorizontalWeight(iIntValue, 1.0f);
            int iIntValue2 = i == 0 ? 0 : this.mTabViewChildIds.get(i - 1).intValue();
            int iIntValue3 = i == this.mTabViewChildIds.size() + (-1) ? 0 : this.mTabViewChildIds.get(i + 1).intValue();
            constraintSet.centerVertically(iIntValue, 0);
            constraintSet.connect(iIntValue, 6, iIntValue2, iIntValue2 == 0 ? 6 : 7, iIntValue2 == 0 ? this.mPadding : 0);
            constraintSet.connect(iIntValue, 7, iIntValue3, iIntValue3 == 0 ? 7 : 6, iIntValue3 == 0 ? this.mPadding : 0);
            constraintSet.connect(iIntValue, 3, 0, 3, this.mPadding);
            constraintSet.connect(iIntValue, 4, 0, 4, this.mPadding);
            i++;
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mFilteredUpdated = false;
    }

    public static class TabView extends LinearLayout {
        private ImageView mArrow;
        private Drawable mArrowIcon;
        private boolean mDescending;
        private boolean mDescendingEnabled;
        private FilterHoverListener mFilterHoverListener;
        private boolean mFiltered;
        private HapticFeedbackCompat mHapticFeedbackCompat;
        private int mIndicatorVisibility;
        private OnFilteredListener mOnFilteredListener;
        private int mSelectedTextAppearanceId;
        private int mTextAppearanceId;
        private ColorStateList mTextColor;
        private TextView mTextView;

        private interface FilterHoverListener {
            void onHoverEnter();

            void onHoverExit(float f, float f2);

            void onHoverFilterEnter();

            void onHoverFilterExit();
        }

        public interface OnFilteredListener {
            void cancelAnimation(View view);

            void onFilteredChanged(TabView tabView, boolean z, boolean z2);
        }

        public TabView(Context context) {
            this(context, null);
        }

        public TabView(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, 0);
        }

        public TabView(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.mDescendingEnabled = true;
            int tabLayoutResource = getTabLayoutResource();
            LayoutInflater.from(context).inflate(tabLayoutResource, (ViewGroup) this, true);
            this.mTextView = (TextView) findViewById(android.R.id.text1);
            this.mArrow = (ImageView) findViewById(R.id.arrow);
            this.mTextView.setImportantForAccessibility(2);
            this.mArrow.setImportantForAccessibility(2);
            if (tabLayoutResource == R.layout.miuix_appcompat_filter_sort_tab_view) {
                TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FilterSortTabView, i, R.style.Widget_FilterSortTabView_DayNight);
                String string = typedArrayObtainStyledAttributes.getString(R.styleable.FilterSortTabView_android_text);
                boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.FilterSortTabView_descending, true);
                this.mIndicatorVisibility = typedArrayObtainStyledAttributes.getInt(R.styleable.FilterSortTabView_indicatorVisibility, 0);
                this.mArrowIcon = typedArrayObtainStyledAttributes.getDrawable(R.styleable.FilterSortTabView_arrowFilterSortTabView);
                this.mTextColor = typedArrayObtainStyledAttributes.getColorStateList(R.styleable.FilterSortTabView_filterSortTabViewTextColor);
                this.mTextAppearanceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.FilterSortTabView_filterSortTabViewTabTextAppearance, 0);
                this.mSelectedTextAppearanceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.FilterSortTabView_filterSortTabViewTabActivatedTextAppearance, 0);
                typedArrayObtainStyledAttributes.recycle();
                initView(string, z);
            }
            this.mArrow.setVisibility(this.mIndicatorVisibility);
            if (getId() == -1) {
                setId(generateViewId());
            }
            ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() { // from class: miuix.miuixbasewidget.widget.FilterSortView.TabView.1
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    if (TabView.this.mTextView != null && !TextUtils.isEmpty(TabView.this.mTextView.getText())) {
                        accessibilityNodeInfoCompat.setContentDescription(TabView.this.mTextView.getText());
                    }
                    accessibilityNodeInfoCompat.setSelected(view.isSelected());
                    if (view.isSelected()) {
                        accessibilityNodeInfoCompat.setClickable(false);
                        accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                    } else {
                        accessibilityNodeInfoCompat.setClickable(true);
                        accessibilityNodeInfoCompat.setStateDescription(TabView.this.getContext().getResources().getString(R.string.accessibility_tab_state_description_unselect));
                    }
                }
            });
        }

        protected int getTabLayoutResource() {
            return R.layout.miuix_appcompat_filter_sort_tab_view;
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

        public void setFilterHoverListener(FilterHoverListener filterHoverListener) {
            this.mFilterHoverListener = filterHoverListener;
        }

        protected void initView(CharSequence charSequence, boolean z) {
            updateTextAppearance(false);
            setGravity(17);
            if (getBackground() == null) {
                setBackground(parseBackground());
            }
            this.mArrow.setBackground(this.mArrowIcon);
            ColorStateList colorStateList = this.mTextColor;
            if (colorStateList != null) {
                this.mTextView.setTextColor(colorStateList);
            }
            this.mTextView.setText(charSequence);
            setDescending(z);
            setOnHoverListener(new View.OnHoverListener() { // from class: miuix.miuixbasewidget.widget.FilterSortView$TabView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnHoverListener
                public final boolean onHover(View view, MotionEvent motionEvent) {
                    return this.f$0.m1868x91e1835f(view, motionEvent);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$initView$0$miuix-miuixbasewidget-widget-FilterSortView$TabView, reason: not valid java name */
        /* synthetic */ boolean m1868x91e1835f(View view, MotionEvent motionEvent) {
            if (this.mFilterHoverListener == null || motionEvent.getSource() == 4098) {
                return false;
            }
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 9) {
                if (this.mFiltered) {
                    this.mFilterHoverListener.onHoverFilterEnter();
                }
                this.mFilterHoverListener.onHoverEnter();
                return true;
            }
            if (actionMasked != 10) {
                return true;
            }
            if (this.mFiltered) {
                this.mFilterHoverListener.onHoverFilterExit();
            }
            this.mFilterHoverListener.onHoverExit(motionEvent.getX() + getLeft(), motionEvent.getY());
            return true;
        }

        public void setIndicatorVisibility(int i) {
            this.mArrow.setVisibility(i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFiltered(boolean z) {
            setFiltered(z, true);
        }

        private void setFiltered(boolean z, boolean z2) {
            TabView tabView;
            FilterSortView filterSortView = (FilterSortView) getParent();
            if (z && filterSortView != null) {
                int childCount = filterSortView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = filterSortView.getChildAt(i);
                    if ((childAt instanceof TabView) && (tabView = (TabView) childAt) != this && tabView.mFiltered) {
                        tabView.setFiltered(false);
                    }
                }
            }
            this.mFiltered = z;
            updateTextAppearance(z);
            this.mTextView.setSelected(z);
            this.mArrow.setSelected(z);
            setSelected(z);
            OnFilteredListener onFilteredListener = this.mOnFilteredListener;
            if (onFilteredListener == null || !z) {
                return;
            }
            onFilteredListener.onFilteredChanged(this, z, z2);
        }

        private void updateTextAppearance(boolean z) {
            TextView textView = this.mTextView;
            if (textView != null) {
                if (z) {
                    TextViewCompat.setTextAppearance(textView, this.mSelectedTextAppearanceId);
                } else {
                    TextViewCompat.setTextAppearance(textView, this.mTextAppearanceId);
                }
            }
        }

        private Drawable parseBackground() {
            return getResources().getDrawable(R.drawable.miuix_appcompat_filter_sort_tab_view_bg_normal);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDescending(boolean z) {
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
            super.setOnClickListener(new View.OnClickListener() { // from class: miuix.miuixbasewidget.widget.FilterSortView.TabView.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (TabView.this.mFiltered) {
                        if (TabView.this.mDescendingEnabled) {
                            TabView tabView = TabView.this;
                            tabView.setDescending(true ^ tabView.mDescending);
                        }
                    } else {
                        TabView.this.setFiltered(true);
                    }
                    onClickListener.onClick(view);
                    if (HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2)) {
                        TabView.this.getHapticFeedbackCompat().m1943x85658b2f(R2.attr.actionBarRemoveBlacklistIcon);
                    } else {
                        HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_MESH_NORMAL);
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public HapticFeedbackCompat getHapticFeedbackCompat() {
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
