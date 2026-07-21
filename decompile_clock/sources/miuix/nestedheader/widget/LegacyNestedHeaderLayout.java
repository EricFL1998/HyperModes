package miuix.nestedheader.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.nestedheader.R;

/* JADX INFO: loaded from: classes3.dex */
public class LegacyNestedHeaderLayout extends NestedScrollingLayout {
    public static final int SCROLL_COMPRESS_TYPE = 1;
    private static final String TAG = "NestedHeaderLayout";
    private boolean isTouch;
    private boolean mAcceptHeaderRootViewAlpha;
    private boolean mAcceptTriggerRootViewAlpha;
    private boolean mAutoAnim;
    private int mHeaderBottomMargin;
    private int mHeaderContentBottomMargin;
    private int mHeaderContentId;
    private float mHeaderContentMinHeight;
    private View mHeaderContentView;
    private int mHeaderInitTop;
    private int mHeaderMeasuredHeight;
    private int mHeaderTopMargin;
    private int mHeaderTotalHeight;
    private View mHeaderView;
    private int mHeaderViewId;
    private int mLastScrollingProgress;
    private NestedHeaderChangedListener mNestedHeaderChangedListener;
    private NestedScrollingLayout.OnNestedChangedListener mOnNestedChangedListener;
    private float mRangeOffset;
    private int mTriggerBottomMargin;
    private int mTriggerContentBottomMargin;
    private int mTriggerContentId;
    private float mTriggerContentMinHeight;
    private View mTriggerContentView;
    private int mTriggerMeasuredHeight;
    private int mTriggerTopMargin;
    private View mTriggerView;
    private int mTriggerViewId;
    private String mValueTag;

    public interface NestedHeaderChangedListener {
        void onHeaderClosed(View view);

        void onHeaderOpened(View view);

        void onTriggerClosed(View view);

        void onTriggerOpened(View view);
    }

    public void setNestedHeaderChangedListener(NestedHeaderChangedListener nestedHeaderChangedListener) {
        this.mNestedHeaderChangedListener = nestedHeaderChangedListener;
    }

    public void removeNestedHeaderChangedListener() {
        this.mNestedHeaderChangedListener = null;
    }

    public LegacyNestedHeaderLayout(Context context) {
        this(context, null);
    }

    public LegacyNestedHeaderLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LegacyNestedHeaderLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHeaderBottomMargin = 0;
        this.mHeaderTopMargin = 0;
        this.mTriggerBottomMargin = 0;
        this.mTriggerTopMargin = 0;
        this.mHeaderInitTop = 0;
        this.mHeaderContentBottomMargin = 0;
        this.mTriggerContentBottomMargin = 0;
        this.mHeaderTotalHeight = 0;
        this.mHeaderMeasuredHeight = 0;
        this.mTriggerMeasuredHeight = 0;
        this.mLastScrollingProgress = 0;
        this.isTouch = false;
        this.mAutoAnim = true;
        this.mAcceptTriggerRootViewAlpha = false;
        this.mAcceptHeaderRootViewAlpha = false;
        this.mValueTag = Long.toString(SystemClock.elapsedRealtime());
        this.mOnNestedChangedListener = new NestedScrollingLayout.OnNestedChangedListener() { // from class: miuix.nestedheader.widget.LegacyNestedHeaderLayout.1
            @Override // miuix.nestedheader.widget.NestedScrollingLayout.OnNestedChangedListener
            public void onStartNestedScroll(int i2) {
                if (i2 == 0) {
                    LegacyNestedHeaderLayout.this.updateTouch(true);
                } else {
                    LegacyNestedHeaderLayout.this.updateTag();
                }
            }

            @Override // miuix.nestedheader.widget.NestedScrollingLayout.OnNestedChangedListener
            public void onStopNestedScrollAccepted(int i2) {
                if (LegacyNestedHeaderLayout.this.mAutoAnim) {
                    LegacyNestedHeaderLayout.this.updateAdsorption();
                }
            }

            @Override // miuix.nestedheader.widget.NestedScrollingLayout.OnNestedChangedListener
            public void onStopNestedScroll(int i2) {
                if (i2 == 0) {
                    LegacyNestedHeaderLayout.this.updateTouch(false);
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.NestedHeaderLayout);
        this.mHeaderViewId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedHeaderLayout_headerView, R.id.header_view);
        this.mTriggerViewId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedHeaderLayout_triggerView, R.id.trigger_view);
        this.mHeaderContentId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedHeaderLayout_headerContentId, R.id.header_content_view);
        this.mTriggerContentId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.NestedHeaderLayout_triggerContentId, R.id.trigger_content_view);
        this.mHeaderContentMinHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.NestedHeaderLayout_headerContentMinHeight, context.getResources().getDimension(R.dimen.miuix_nested_header_layout_content_min_height));
        this.mTriggerContentMinHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.NestedHeaderLayout_triggerContentMinHeight, context.getResources().getDimension(R.dimen.miuix_nested_header_layout_content_min_height));
        this.mRangeOffset = typedArrayObtainStyledAttributes.getDimension(R.styleable.NestedHeaderLayout_rangeOffset, 0.0f);
        typedArrayObtainStyledAttributes.recycle();
        addOnScrollListener(this.mOnNestedChangedListener);
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderView = findViewById(this.mHeaderViewId);
        View viewFindViewById = findViewById(this.mTriggerViewId);
        this.mTriggerView = viewFindViewById;
        View view = this.mHeaderView;
        if (view == null && viewFindViewById == null) {
            throw new IllegalArgumentException("The headerView or triggerView attribute is required and must refer to a valid child.");
        }
        if (view != null) {
            View viewFindViewById2 = view.findViewById(this.mHeaderContentId);
            this.mHeaderContentView = viewFindViewById2;
            if (viewFindViewById2 == null) {
                this.mHeaderContentView = this.mHeaderView.findViewById(android.R.id.inputArea);
            }
        }
        View view2 = this.mTriggerView;
        if (view2 != null) {
            View viewFindViewById3 = view2.findViewById(this.mTriggerContentId);
            this.mTriggerContentView = viewFindViewById3;
            if (viewFindViewById3 == null) {
                this.mTriggerContentView = this.mTriggerView.findViewById(android.R.id.inputArea);
            }
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        View view = this.mHeaderView;
        if (view != null) {
            this.mHeaderInitTop = view.getTop();
        }
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    protected int getHeaderProgressFrom() {
        return getScrollingFrom() + this.mContentInsetTop + this.mHeaderTotalHeight;
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    public void onUpdateScrollingRangeOnLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onUpdateScrollingRangeOnLayout(z, i, i2, i3, i4);
        updateScrollingRange(true, false, false);
    }

    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    protected void onScrollingProgressUpdated(int i) {
        int iMax;
        int i2;
        float f;
        float f2;
        int i3;
        int i4;
        float f3;
        float f4;
        super.onScrollingProgressUpdated(i);
        View view = this.mTriggerView;
        if (view == null || view.getVisibility() == 8) {
            iMax = i;
            i2 = 0;
        } else {
            iMax = i - Math.max(0, Math.min(getScrollingTo(), i));
            int iMax2 = Math.max(getScrollingFrom(), Math.min(getScrollingTo(), i));
            int i5 = this.mTriggerTopMargin;
            View view2 = this.mHeaderView;
            if (view2 != null && view2.getVisibility() != 8) {
                i3 = iMax2;
                i4 = this.mHeaderTotalHeight + this.mTriggerTopMargin;
                i2 = 0;
            } else {
                int i6 = this.mTriggerTopMargin + this.mTriggerBottomMargin + this.mTriggerMeasuredHeight;
                i3 = iMax2 + i6;
                i4 = i5;
                i2 = i6;
            }
            View view3 = this.mTriggerContentView;
            if (view3 == null) {
                view3 = this.mTriggerView;
            }
            View view4 = view3;
            relayoutContent(this.mTriggerView, view4, i4, ((i3 - this.mTriggerBottomMargin) - this.mTriggerTopMargin) - this.mTriggerMeasuredHeight, false);
            if (this.mTriggerContentView == null) {
                f3 = i3 - this.mTriggerBottomMargin;
                f4 = this.mTriggerContentMinHeight;
            } else {
                f3 = i3 - this.mTriggerContentBottomMargin;
                f4 = this.mTriggerContentMinHeight;
            }
            float f5 = f3 / f4;
            float fMax = Math.max(0.0f, Math.min(1.0f, f5));
            if (this.mAcceptTriggerRootViewAlpha) {
                this.mTriggerView.setAlpha(fMax);
            } else {
                View view5 = this.mTriggerView;
                if ((view5 instanceof ViewGroup) && ((ViewGroup) view5).getChildCount() > 0) {
                    for (int i7 = 0; i7 < ((ViewGroup) this.mTriggerView).getChildCount(); i7++) {
                        ((ViewGroup) this.mTriggerView).getChildAt(i7).setAlpha(fMax);
                    }
                }
            }
            applyContentAlpha(makeTriggerContentViewList(view4), f5 - 1.0f);
        }
        View view6 = this.mHeaderView;
        if (view6 != null && view6.getVisibility() != 8) {
            int i8 = this.mHeaderInitTop + this.mHeaderTopMargin;
            View view7 = this.mHeaderContentView;
            if (view7 == null) {
                view7 = this.mHeaderView;
            }
            View view8 = view7;
            relayoutContent(this.mHeaderView, view8, i8, iMax, false);
            if (this.mHeaderContentView == null) {
                f = iMax - this.mHeaderBottomMargin;
                f2 = this.mHeaderContentMinHeight;
            } else {
                f = iMax - this.mHeaderContentBottomMargin;
                f2 = this.mHeaderContentMinHeight;
            }
            float f6 = (f + f2) / f2;
            float fMax2 = Math.max(0.0f, Math.min(1.0f, f6 + 1.0f));
            if (this.mAcceptHeaderRootViewAlpha) {
                this.mHeaderView.setAlpha(fMax2);
            } else {
                View view9 = this.mHeaderView;
                if ((view9 instanceof ViewGroup) && ((ViewGroup) view9).getChildCount() > 0) {
                    for (int i9 = 0; i9 < ((ViewGroup) this.mHeaderView).getChildCount(); i9++) {
                        ((ViewGroup) this.mHeaderView).getChildAt(i9).setAlpha(fMax2);
                    }
                }
            }
            applyContentAlpha(makeHeaderContentViewList(view8), f6);
            i2 = this.mHeaderTotalHeight;
        }
        this.mScrollableView.offsetTopAndBottom((i + i2) - this.mScrollableView.getTop());
        int i10 = this.mLastScrollingProgress;
        if (i - i10 > 0) {
            checkSendHeaderChangeListener(i10, i, true);
        } else if (i - i10 < 0) {
            checkSendHeaderChangeListener(i10, i, false);
        }
        this.mLastScrollingProgress = i;
        updateHeaderOpen(isHeaderOpen());
    }

    private void checkSendHeaderChangeListener(int i, int i2, boolean z) {
        if (this.mNestedHeaderChangedListener == null) {
            return;
        }
        if (z) {
            if (i2 == 0 && getHeaderViewVisible()) {
                this.mNestedHeaderChangedListener.onHeaderOpened(this.mHeaderView);
            } else if (i2 == getScrollingTo() && getTriggerViewVisible()) {
                this.mNestedHeaderChangedListener.onTriggerOpened(this.mTriggerView);
            }
            if (i >= 0 || i2 <= 0 || !getHeaderViewVisible()) {
                return;
            }
            this.mNestedHeaderChangedListener.onHeaderOpened(this.mHeaderView);
            return;
        }
        if (i2 == 0 && getTriggerViewVisible()) {
            this.mNestedHeaderChangedListener.onTriggerClosed(this.mTriggerView);
        } else if (i2 == getScrollingFrom() && getHeaderViewVisible()) {
            this.mNestedHeaderChangedListener.onHeaderClosed(this.mHeaderView);
        } else if (i2 == getScrollingFrom() && !getHeaderViewVisible()) {
            this.mNestedHeaderChangedListener.onTriggerClosed(this.mTriggerView);
        }
        int scrollingFrom = getHeaderViewVisible() ? 0 : getScrollingFrom();
        if (i <= scrollingFrom || i2 >= scrollingFrom || !getTriggerViewVisible()) {
            return;
        }
        this.mNestedHeaderChangedListener.onTriggerClosed(this.mTriggerView);
    }

    private void updateScrollingRange(boolean z, boolean z2, boolean z3) {
        int i;
        boolean z4;
        int i2;
        int i3;
        View view = this.mHeaderView;
        boolean z5 = true;
        if (view == null || view.getVisibility() == 8) {
            i = 0;
            z4 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mHeaderView.getLayoutParams();
            this.mHeaderBottomMargin = marginLayoutParams.bottomMargin;
            this.mHeaderTopMargin = marginLayoutParams.topMargin;
            int measuredHeight = this.mHeaderView.getMeasuredHeight();
            this.mHeaderMeasuredHeight = measuredHeight;
            this.mHeaderTotalHeight = this.mHeaderTopMargin + measuredHeight + this.mHeaderBottomMargin;
            View view2 = this.mHeaderContentView;
            if (view2 != null) {
                this.mHeaderContentBottomMargin = ((ViewGroup.MarginLayoutParams) view2.getLayoutParams()).bottomMargin;
            }
            i = (int) (this.mRangeOffset - this.mHeaderTotalHeight);
            z4 = true;
        }
        View view3 = this.mTriggerView;
        if (view3 == null || view3.getVisibility() == 8) {
            i2 = i;
            i3 = 0;
            z5 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.mTriggerView.getLayoutParams();
            this.mTriggerBottomMargin = marginLayoutParams2.bottomMargin;
            this.mTriggerTopMargin = marginLayoutParams2.topMargin;
            this.mTriggerMeasuredHeight = this.mTriggerView.getMeasuredHeight();
            View view4 = this.mTriggerContentView;
            if (view4 != null) {
                this.mTriggerContentBottomMargin = ((ViewGroup.MarginLayoutParams) view4.getLayoutParams()).bottomMargin;
            }
            i3 = this.mTriggerMeasuredHeight + this.mTriggerTopMargin + this.mTriggerBottomMargin;
            if (!z4) {
                i = -i3;
                i3 = 0;
            }
            i2 = i;
        }
        setScrollingRange(i2, i3, z4, z5, false, z, z2, false, z3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTouch(boolean z) {
        this.isTouch = z;
        if (z) {
            updateTag();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTag() {
        this.mValueTag = Long.toString(SystemClock.elapsedRealtime());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAdsorption() {
        int scrollingTo;
        if (getScrollingProgress() == 0 || getScrollingProgress() >= getScrollingTo() || getScrollingProgress() <= getScrollingFrom()) {
            return;
        }
        if (getScrollingProgress() > getScrollingFrom() && getScrollingProgress() < getScrollingFrom() * 0.5f) {
            scrollingTo = getScrollingFrom();
        } else {
            scrollingTo = ((((float) getScrollingProgress()) < ((float) getScrollingFrom()) * 0.5f || getScrollingProgress() >= 0) && (getScrollingProgress() <= 0 || ((float) getScrollingProgress()) >= ((float) getScrollingTo()) * 0.5f) && ((float) getScrollingProgress()) >= ((float) getScrollingTo()) * 0.5f && getScrollingProgress() < getScrollingTo()) ? getScrollingTo() : 0;
        }
        autoAdsorption(scrollingTo);
    }

    private void autoAdsorption(int i) {
        final String string = Long.toString(SystemClock.elapsedRealtime());
        this.mValueTag = string;
        Folme.useValue(new Object[0]).setTo(string, Integer.valueOf(getScrollingProgress())).to(string, Integer.valueOf(i), new AnimConfig().addListeners(new TransitionListener() { // from class: miuix.nestedheader.widget.LegacyNestedHeaderLayout.2
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                UpdateInfo updateInfoFindByName = UpdateInfo.findByName(collection, string);
                if (updateInfoFindByName == null || !LegacyNestedHeaderLayout.this.isScrolling(string)) {
                    return;
                }
                LegacyNestedHeaderLayout.this.syncScrollingProgress(updateInfoFindByName.getIntValue());
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isScrolling(String str) {
        return (this.isTouch || !this.mValueTag.equals(str) || getAcceptedNestedFlingInConsumedProgress()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncScrollingProgress(int i) {
        updateScrollingProgress(i);
        onScrollingProgressUpdated(i);
    }

    private List<View> makeTriggerContentViewList(View view) {
        return makeContentViewList(view, this.mTriggerContentId == R.id.trigger_content_view || this.mTriggerContentView != null);
    }

    private List<View> makeHeaderContentViewList(View view) {
        return makeContentViewList(view, this.mHeaderContentId == R.id.header_content_view || this.mHeaderContentView != null);
    }

    private List<View> makeContentViewList(View view, boolean z) {
        if (view == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        if (z) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    arrayList.add(viewGroup.getChildAt(i));
                }
            } else if (view != null) {
                arrayList.add(view);
            }
            return arrayList;
        }
        arrayList.add(view);
        return arrayList;
    }

    private void applyContentAlpha(List<View> list, float f) {
        if (list == null) {
            return;
        }
        float fMax = Math.max(0.0f, Math.min(1.0f, f));
        Iterator<View> it = list.iterator();
        while (it.hasNext()) {
            it.next().setAlpha(fMax);
        }
    }

    private void relayoutContent(View view, View view2, int i, int i2, boolean z) {
        view.layout(view.getLeft(), i, view.getRight(), Math.max(i, view.getMeasuredHeight() + i + i2));
        if (view != view2) {
            int iMax = Math.max(view2.getTop(), 0);
            int top = view2.getTop();
            int measuredHeight = view2.getMeasuredHeight() + iMax;
            if (z) {
                i2 /= 2;
            }
            view2.layout(view2.getLeft(), iMax, view2.getRight(), Math.max(top, measuredHeight + i2));
        }
    }

    public void setAutoAnim(boolean z) {
        this.mAutoAnim = z;
    }

    public boolean isAutoAnim() {
        return this.mAutoAnim;
    }

    public void setTriggerViewVisible(boolean z) {
        View view = this.mTriggerView;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
            updateScrollingRange(false, z, false);
        }
    }

    public boolean getTriggerViewVisible() {
        View view = this.mTriggerView;
        return view != null && view.getVisibility() == 0;
    }

    public void setHeaderViewVisible(boolean z) {
        View view = this.mHeaderView;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
            updateScrollingRange(false, false, z);
        }
    }

    public boolean getHeaderViewVisible() {
        View view = this.mHeaderView;
        return view != null && view.getVisibility() == 0;
    }

    public View getHeaderView() {
        return this.mHeaderView;
    }

    public View getScrollableView() {
        return this.mScrollableView;
    }

    public void setAutoTriggerClose(boolean z) {
        int scrollingFrom;
        if (getTriggerViewVisible() && getHeaderViewVisible() && getScrollingProgress() > 0) {
            scrollingFrom = 0;
        } else {
            scrollingFrom = (!getTriggerViewVisible() || getHeaderViewVisible() || getScrollingProgress() <= getScrollingFrom()) ? -1 : getScrollingFrom();
        }
        if (scrollingFrom != -1 && z) {
            autoAdsorption(scrollingFrom);
        } else if (scrollingFrom != -1) {
            syncScrollingProgress(scrollingFrom);
        }
    }

    public void setAutoHeaderClose(boolean z) {
        if (!getHeaderViewVisible() || getScrollingProgress() <= getScrollingFrom()) {
            return;
        }
        if (z) {
            autoAdsorption(getScrollingFrom());
        } else if (getHeaderViewVisible()) {
            syncScrollingProgress(getScrollingFrom());
        }
    }

    public void setAutoAllClose(boolean z) {
        if (z && getScrollingProgress() > getScrollingFrom()) {
            autoAdsorption(getScrollingFrom());
        } else {
            syncScrollingProgress(getScrollingFrom());
        }
    }

    public void setAutoTriggerOpen(boolean z) {
        if (!getTriggerViewVisible() || getScrollingProgress() >= getScrollingTo()) {
            return;
        }
        if (z) {
            autoAdsorption(getScrollingTo());
        } else {
            syncScrollingProgress(getScrollingTo());
        }
    }

    public void setAutoHeaderOpen(boolean z) {
        if (!getHeaderViewVisible() || getScrollingProgress() >= 0) {
            return;
        }
        if (z) {
            autoAdsorption(0);
        } else {
            syncScrollingProgress(0);
        }
    }

    public void setAutoAllOpen(boolean z) {
        if (z && getScrollingProgress() < getScrollingTo()) {
            autoAdsorption(getScrollingTo());
        } else {
            syncScrollingProgress(getScrollingTo());
        }
    }

    public boolean isHeaderOpen() {
        return getHeaderViewVisible() && getScrollingProgress() >= 0;
    }

    public boolean isTriggerOpen() {
        return getTriggerViewVisible() && ((getHeaderViewVisible() && getScrollingProgress() >= getScrollingTo()) || (!getHeaderViewVisible() && getScrollingProgress() >= 0));
    }

    public void setAcceptTriggerRootViewAlpha(boolean z) {
        this.mAcceptTriggerRootViewAlpha = z;
    }

    public boolean isAcceptTriggerRootViewAlpha() {
        return this.mAcceptTriggerRootViewAlpha;
    }

    public void setHeaderRootViewAcceptAlpha(boolean z) {
        this.mAcceptHeaderRootViewAlpha = z;
    }

    public boolean isAcceptHeaderRootViewAlpha() {
        return this.mAcceptHeaderRootViewAlpha;
    }
}
