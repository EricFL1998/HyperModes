package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.R;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.internal.view.ActionBarPolicy;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ScrollingTabContainerView extends HorizontalScrollView implements AdapterView.OnItemClickListener, ActionBar.FragmentViewPagerChangeListener {
    public static final int MODE_COLLAPSE = 0;
    public static final int MODE_MOVABLE = 1;
    public static final int MODE_SECONDARY = 2;
    private boolean mAllowCollapse;
    private int mContentHeight;
    private final LayoutInflater mInflater;
    private boolean mIsScrolledX;
    private int mLastSelectedPosition;
    int mMaxTabWidth;
    private Paint mPaint;
    private int mSelectedTabIndex;
    int mStackedTabMaxWidth;
    private TabClickListener mTabClickListener;
    private Bitmap mTabIndicatorBitmap;
    private float mTabIndicatorPosition;
    protected LinearLayout mTabLayout;
    Runnable mTabSelector;
    private Spinner mTabSpinner;
    private WeakHashMap<TextView, Integer> mTextStyleMap;
    private float mTouchInitX;
    private final int mTouchSlop;
    private boolean mTranslucentIndicator;

    abstract int getDefaultTabTextStyle();

    abstract int getTabBarLayoutRes();

    abstract int getTabContainerHeight();

    abstract int getTabViewLayoutRes();

    abstract int getTabViewMarginHorizontal();

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    public View updateCustomTabView(ViewGroup viewGroup, View view, TextView textView, ImageView imageView) {
        return null;
    }

    void updateTabTextStyle(TextView textView) {
    }

    public ScrollingTabContainerView(Context context) {
        super(context);
        this.mPaint = new Paint();
        this.mLastSelectedPosition = -1;
        this.mIsScrolledX = false;
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(context);
        this.mInflater = layoutInflaterFrom;
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(context);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(null, R.styleable.ActionBar, android.R.attr.actionBarStyle, 0);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.ActionBar_tabIndicator);
        this.mTranslucentIndicator = typedArrayObtainStyledAttributes.getBoolean(R.styleable.ActionBar_translucentTabIndicator, true);
        this.mTabIndicatorBitmap = getTabIndicatorBitmap(drawable);
        typedArrayObtainStyledAttributes.recycle();
        if (this.mTranslucentIndicator) {
            this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        setHorizontalScrollBarEnabled(false);
        this.mStackedTabMaxWidth = actionBarPolicy.getStackedTabMaxWidth();
        LinearLayout linearLayout = (LinearLayout) layoutInflaterFrom.inflate(getTabBarLayoutRes(), (ViewGroup) this, false);
        this.mTabLayout = linearLayout;
        addView(linearLayout, new FrameLayout.LayoutParams(-2, -2));
        this.mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
    }

    int getTabTextStyleId(TextView textView) {
        WeakHashMap<TextView, Integer> weakHashMap;
        if (textView != null && (weakHashMap = this.mTextStyleMap) != null && weakHashMap.containsKey(textView)) {
            return this.mTextStyleMap.get(textView).intValue();
        }
        return AttributeResolver.resolve(getContext(), getDefaultTabTextStyle());
    }

    public void setEmbeded(boolean z) {
        setHorizontalFadingEdgeEnabled(true);
    }

    private Bitmap getTabIndicatorBitmap(Drawable drawable) {
        Bitmap bitmapCreateBitmap;
        if (drawable == null) {
            return null;
        }
        if (this.mTranslucentIndicator) {
            bitmapCreateBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ALPHA_8);
        } else {
            bitmapCreateBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap bitmap = this.mTabIndicatorBitmap;
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, this.mTabIndicatorPosition, getHeight() - this.mTabIndicatorBitmap.getHeight(), this.mPaint);
        }
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mTabLayout.getChildAt(this.mSelectedTabIndex) != null) {
            setTabIndicatorPosition(this.mSelectedTabIndex);
            scrollToTab(this.mSelectedTabIndex);
        }
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int mode = View.MeasureSpec.getMode(i);
        boolean z = mode == 1073741824;
        setFillViewport(z);
        int childCount = this.mTabLayout.getChildCount();
        if (childCount > 1 && (mode == 1073741824 || mode == Integer.MIN_VALUE)) {
            if (childCount > 2) {
                this.mMaxTabWidth = (int) (View.MeasureSpec.getSize(i) * 0.4f);
            } else {
                this.mMaxTabWidth = (int) (View.MeasureSpec.getSize(i) * 0.6f);
            }
            this.mMaxTabWidth = Math.min(this.mMaxTabWidth, this.mStackedTabMaxWidth);
        } else {
            this.mMaxTabWidth = -1;
        }
        int i3 = this.mContentHeight;
        if (i3 != -2) {
            i2 = View.MeasureSpec.makeMeasureSpec(i3, BasicMeasure.EXACTLY);
        }
        int measuredWidth = getMeasuredWidth();
        super.onMeasure(i, i2);
        int measuredWidth2 = getMeasuredWidth();
        if (!z || measuredWidth == measuredWidth2) {
            return;
        }
        setTabSelected(this.mSelectedTabIndex);
    }

    public float getTabIndicatorPosition() {
        return this.mTabIndicatorPosition;
    }

    public void setTabIndicatorPosition(int i) {
        setTabIndicatorPosition(i, 0.0f);
    }

    public void setTabIndicatorPosition(int i, float f) {
        float width;
        if (this.mTabIndicatorBitmap != null) {
            View childAt = this.mTabLayout.getChildAt(i);
            View childAt2 = this.mTabLayout.getChildAt(i + 1);
            if (childAt2 == null) {
                width = childAt.getWidth();
            } else {
                width = (childAt.getWidth() + childAt2.getWidth()) / 2.0f;
            }
            this.mTabIndicatorPosition = childAt.getLeft() + ((childAt.getWidth() - this.mTabIndicatorBitmap.getWidth()) / 2) + (width * f);
            invalidate();
        }
    }

    private boolean isCollapsed() {
        Spinner spinner = this.mTabSpinner;
        return spinner != null && spinner.getParent() == this;
    }

    public void setAllowCollapse(boolean z) {
        this.mAllowCollapse = z;
    }

    private boolean performExpand() {
        if (!isCollapsed()) {
            return false;
        }
        removeView(this.mTabSpinner);
        addView(this.mTabLayout, new ViewGroup.LayoutParams(-2, -2));
        setTabSelected(this.mTabSpinner.getSelectedItemPosition());
        return false;
    }

    public void setTabSelected(int i) {
        setTabSelected(i, true);
    }

    public void setTabSelected(int i, boolean z) {
        this.mSelectedTabIndex = i;
        int childCount = this.mTabLayout.getChildCount();
        int i2 = 0;
        while (i2 < childCount) {
            View childAt = this.mTabLayout.getChildAt(i2);
            boolean z2 = i2 == i;
            childAt.setSelected(z2);
            if (z2) {
                if (z) {
                    animateToTab(i);
                } else {
                    scrollToTab(i);
                }
            }
            i2++;
        }
    }

    public void setContentHeight(int i) {
        if (this.mContentHeight != i) {
            this.mContentHeight = i;
            requestLayout();
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(getContext());
        setContentHeight(getTabContainerHeight());
        this.mStackedTabMaxWidth = actionBarPolicy.getStackedTabMaxWidth();
    }

    public void scrollToTab(int i) {
        View childAt = this.mTabLayout.getChildAt(i);
        scrollTo(childAt.getLeft() - ((getWidth() - childAt.getWidth()) / 2), 0);
    }

    public void animateToTab(int i) {
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        TabSelectorRunnable tabSelectorRunnable = new TabSelectorRunnable(this, i);
        this.mTabSelector = tabSelectorRunnable;
        post(tabSelectorRunnable);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            post(runnable);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TabView createTabView(androidx.appcompat.app.ActionBar.Tab tab, boolean z) {
        TabView tabView = (TabView) this.mInflater.inflate(getTabViewLayoutRes(), (ViewGroup) this.mTabLayout, false);
        tabView.attach(this, tab, z);
        if (z) {
            tabView.setBackground(null);
            tabView.setLayoutParams(new LinearLayout.LayoutParams(-1, this.mContentHeight));
        } else {
            tabView.setFocusable(true);
            if (this.mTabClickListener == null) {
                this.mTabClickListener = new TabClickListener(this);
            }
            tabView.setOnClickListener(this.mTabClickListener);
        }
        if (this.mTabLayout.getChildCount() != 0) {
            ((LinearLayout.LayoutParams) tabView.getLayoutParams()).setMarginStart(getTabViewMarginHorizontal());
        }
        return tabView;
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mTouchInitX = motionEvent.getX();
            this.mIsScrolledX = false;
        } else if (motionEvent.getActionMasked() == 2) {
            this.mIsScrolledX = Math.abs(motionEvent.getX() - this.mTouchInitX) > ((float) this.mTouchSlop);
        } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
            this.mIsScrolledX = false;
        }
        if (!this.mIsScrolledX) {
            return false;
        }
        MotionEvent motionEventObtain = MotionEvent.obtain(motionEvent);
        motionEventObtain.setAction(0);
        super.onInterceptTouchEvent(motionEventObtain);
        motionEventObtain.recycle();
        return true;
    }

    public void addTab(androidx.appcompat.app.ActionBar.Tab tab, boolean z) {
        TabView tabViewCreateTabView = createTabView(tab, false);
        this.mTabLayout.addView(tabViewCreateTabView);
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (z) {
            tabViewCreateTabView.setSelected(true);
            this.mLastSelectedPosition = this.mTabLayout.getChildCount() - 1;
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void addTab(androidx.appcompat.app.ActionBar.Tab tab, int i, boolean z) {
        TabView tabViewCreateTabView = createTabView(tab, false);
        this.mTabLayout.addView(tabViewCreateTabView, i);
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (z) {
            tabViewCreateTabView.setSelected(true);
            this.mLastSelectedPosition = this.mTabLayout.getChildCount() - 1;
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void updateTab(int i) {
        ((TabView) this.mTabLayout.getChildAt(i)).update();
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void setBadgeVisibility(int i, boolean z) {
        ImageView imageView;
        if (i <= this.mTabLayout.getChildCount() - 1 && (imageView = ((TabView) this.mTabLayout.getChildAt(i)).mBadgeView) != null) {
            if (z) {
                imageView.setVisibility(0);
            } else {
                imageView.setVisibility(8);
            }
        }
    }

    public void setTabIconWithPosition(int i, int i2, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        TextView textView;
        if (i <= this.mTabLayout.getChildCount() - 1 && (textView = ((TabView) this.mTabLayout.getChildAt(i)).mTextView) != null) {
            textView.setGravity(16);
            textView.setCompoundDrawablePadding(i2);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        }
    }

    public void setTextAppearance(int i, int i2) {
        TextView textView;
        if (i < 0 || i > this.mTabLayout.getChildCount() - 1 || (textView = ((TabView) this.mTabLayout.getChildAt(i)).mTextView) == null) {
            return;
        }
        if (this.mTextStyleMap == null) {
            this.mTextStyleMap = new WeakHashMap<>();
        }
        this.mTextStyleMap.put(textView, Integer.valueOf(i2));
        textView.setTextAppearance(textView.getContext(), i2);
    }

    public void removeTabAt(int i) {
        LinearLayout linearLayout = this.mTabLayout;
        if (linearLayout != null && linearLayout.getChildAt(i) != null) {
            this.mTabLayout.removeViewAt(i);
        }
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void removeAllTabs() {
        this.mTabLayout.removeAllViews();
        Spinner spinner = this.mTabSpinner;
        if (spinner != null) {
            ((TabAdapter) spinner.getAdapter()).notifyDataSetChanged();
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        ((TabView) view).getTab().select();
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
        setTabIndicatorPosition(i, f);
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageSelected(int i) {
        TabView tabView = (TabView) this.mTabLayout.getChildAt(i);
        if (tabView != null) {
            tabView.sendAccessibilityEvent(4);
        }
        setTabIndicatorPosition(i);
        int i2 = this.mLastSelectedPosition;
        if (i2 != -1) {
            boolean z = true;
            if (Math.abs(i2 - i) == 1) {
                TabView tabView2 = (TabView) this.mTabLayout.getChildAt(this.mLastSelectedPosition);
                ScrollingTabTextView scrollingTabTextView = tabView2 != null ? (ScrollingTabTextView) tabView2.getTextView() : null;
                ScrollingTabTextView scrollingTabTextView2 = tabView != null ? (ScrollingTabTextView) tabView.getTextView() : null;
                if (scrollingTabTextView != null && scrollingTabTextView2 != null) {
                    z = ViewUtils.isLayoutRtl(this) ? false : false;
                    scrollingTabTextView.startScrollAnimation(z);
                    scrollingTabTextView2.startScrollAnimation(z);
                }
            }
        }
        this.mLastSelectedPosition = i;
    }

    public static class TabView extends LinearLayout {
        private ImageView mBadgeView;
        private View mCustomView;
        private ImageView mIconView;
        private ScrollingTabContainerView mParent;
        private androidx.appcompat.app.ActionBar.Tab mTab;
        private TextView mTextView;

        public TabView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this, new AnimConfig[0]);
            setAccessibilityDelegate();
        }

        void attach(ScrollingTabContainerView scrollingTabContainerView, androidx.appcompat.app.ActionBar.Tab tab, boolean z) {
            this.mParent = scrollingTabContainerView;
            this.mTab = tab;
            if (z) {
                setGravity(8388627);
            }
            update();
        }

        public void bindTab(androidx.appcompat.app.ActionBar.Tab tab) {
            this.mTab = tab;
            update();
        }

        public void update() {
            Drawable drawableResolveDrawable;
            int intrinsicHeight;
            int lineHeight;
            androidx.appcompat.app.ActionBar.Tab tab = this.mTab;
            View customView = tab.getCustomView();
            if (customView != null) {
                this.mCustomView = this.mParent.updateCustomTabView(this, customView, this.mTextView, this.mIconView);
            } else {
                View view = this.mCustomView;
                if (view != null) {
                    removeView(view);
                    this.mCustomView = null;
                }
                Context context = getContext();
                Drawable icon = tab.getIcon();
                CharSequence text = tab.getText();
                if (icon != null) {
                    if (this.mIconView == null) {
                        ImageView imageView = new ImageView(context);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams.gravity = 16;
                        imageView.setLayoutParams(layoutParams);
                        addView(imageView, 0);
                        this.mIconView = imageView;
                    }
                    this.mIconView.setImageDrawable(icon);
                    this.mIconView.setVisibility(0);
                } else {
                    ImageView imageView2 = this.mIconView;
                    if (imageView2 != null) {
                        imageView2.setVisibility(8);
                        this.mIconView.setImageDrawable(null);
                    }
                }
                if (text != null) {
                    if (this.mTextView == null) {
                        ScrollingTabTextView scrollingTabTextView = new ScrollingTabTextView(context, null, this.mParent.getDefaultTabTextStyle());
                        scrollingTabTextView.setEllipsize(TextUtils.TruncateAt.END);
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams2.gravity = 16;
                        scrollingTabTextView.setLayoutParams(layoutParams2);
                        addView(scrollingTabTextView);
                        this.mTextView = scrollingTabTextView;
                    }
                    this.mTextView.setText(text);
                    this.mTextView.setVisibility(0);
                    if (this.mBadgeView == null && (drawableResolveDrawable = AttributeResolver.resolveDrawable(context, R.attr.actionBarTabBadgeIcon)) != null) {
                        ImageView imageView3 = new ImageView(context);
                        imageView3.setImageDrawable(drawableResolveDrawable);
                        imageView3.setVisibility(8);
                        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams3.gravity = 48;
                        Drawable background = getBackground();
                        if (background != null && (intrinsicHeight = background.getIntrinsicHeight()) > (lineHeight = this.mTextView.getLineHeight())) {
                            layoutParams3.topMargin = (intrinsicHeight - lineHeight) / 2;
                        }
                        imageView3.setLayoutParams(layoutParams3);
                        addView(imageView3);
                        this.mBadgeView = imageView3;
                    }
                } else {
                    TextView textView = this.mTextView;
                    if (textView != null) {
                        textView.setVisibility(8);
                        this.mTextView.setText((CharSequence) null);
                    }
                }
                ImageView imageView4 = this.mIconView;
                if (imageView4 != null) {
                    imageView4.setContentDescription(tab.getContentDescription());
                }
            }
            this.mParent.updateTabTextStyle(this.mTextView);
        }

        public androidx.appcompat.app.ActionBar.Tab getTab() {
            return this.mTab;
        }

        public TextView getTextView() {
            return this.mTextView;
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            TextView textView = this.mTextView;
            if (textView != null) {
                this.mTextView.setTextAppearance(this.mParent.getTabTextStyleId(textView));
            }
            ImageView imageView = this.mIconView;
            if (imageView != null) {
                imageView.setImageDrawable(this.mTab.getIcon());
            }
        }

        private void setAccessibilityDelegate() {
            ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() { // from class: miuix.appcompat.internal.app.widget.ScrollingTabContainerView.TabView.1
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    accessibilityNodeInfoCompat.setSelected(view.isSelected());
                    if (view.isSelected()) {
                        accessibilityNodeInfoCompat.setClickable(false);
                        accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                    } else {
                        accessibilityNodeInfoCompat.setClickable(true);
                        accessibilityNodeInfoCompat.setStateDescription(TabView.this.getContext().getResources().getString(miuix.miuixbasewidget.R.string.accessibility_tab_state_description_unselect));
                    }
                }
            });
        }
    }

    private class TabAdapter extends BaseAdapter {
        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        private TabAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return ScrollingTabContainerView.this.mTabLayout.getChildCount();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return ((TabView) ScrollingTabContainerView.this.mTabLayout.getChildAt(i)).getTab();
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                return ScrollingTabContainerView.this.createTabView((androidx.appcompat.app.ActionBar.Tab) getItem(i), true);
            }
            ((TabView) view).bindTab((androidx.appcompat.app.ActionBar.Tab) getItem(i));
            return view;
        }
    }

    private static class TabSelectorRunnable implements Runnable {
        private int mPosition;
        private WeakReference<ScrollingTabContainerView> mRefs;

        TabSelectorRunnable(ScrollingTabContainerView scrollingTabContainerView, int i) {
            this.mRefs = new WeakReference<>(scrollingTabContainerView);
            this.mPosition = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            View childAt;
            WeakReference<ScrollingTabContainerView> weakReference = this.mRefs;
            ScrollingTabContainerView scrollingTabContainerView = weakReference != null ? weakReference.get() : null;
            if (scrollingTabContainerView == null || (childAt = scrollingTabContainerView.mTabLayout.getChildAt(this.mPosition)) == null) {
                return;
            }
            scrollingTabContainerView.smoothScrollTo(childAt.getLeft() - ((scrollingTabContainerView.getWidth() - childAt.getWidth()) / 2), 0);
            scrollingTabContainerView.mTabSelector = null;
        }
    }

    private static class TabClickListener implements View.OnClickListener {
        private WeakReference<ScrollingTabContainerView> mRefs;

        TabClickListener(ScrollingTabContainerView scrollingTabContainerView) {
            this.mRefs = new WeakReference<>(scrollingTabContainerView);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            WeakReference<ScrollingTabContainerView> weakReference = this.mRefs;
            ScrollingTabContainerView scrollingTabContainerView = weakReference != null ? weakReference.get() : null;
            if (scrollingTabContainerView == null) {
                return;
            }
            ((TabView) view).getTab().select();
            int childCount = scrollingTabContainerView.mTabLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = scrollingTabContainerView.mTabLayout.getChildAt(i);
                childAt.setSelected(childAt == view);
            }
        }
    }
}
