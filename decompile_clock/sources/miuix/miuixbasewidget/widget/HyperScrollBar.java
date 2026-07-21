package miuix.miuixbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.core.view.ViewCompat;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.FolmeObject;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.IntValueProperty;
import miuix.miuixbasewidget.R;

/* JADX INFO: loaded from: classes2.dex */
public class HyperScrollBar extends View {
    private static final int CLICK_THRESHOLD = 10;
    private static final int INIT_DELAY_MS = 100;
    private static final int MAX_INIT_RETRY = 5;
    private static final IntValueProperty PROPERTY_SCROLL_BAR_ALPHA = new IntValueProperty("scrollBarAlpha", 0.001f);
    private static final IntValueProperty PROPERTY_SCROLL_BAR_WIDTH = new IntValueProperty("scrollBarWidth", 0.001f);
    private static final IntValueProperty PROPERTY_TOUCH_INDICATOR_ALPHA = new IntValueProperty("touchIndicatorAlpha", 0.1f);
    private long autoHideDelay;
    private float cachedScrollBarHeight;
    private int cachedScrollRange;
    private float currentAlpha;
    private boolean enableHapticFeedback;
    private Runnable hideRunnable;
    private int initRetryCount;
    private boolean isDragging;
    private boolean isInitialized;
    private boolean isScrollBarTouched;
    private float lastContentHeight;
    private float lastTouchY;
    private ScrollBarAnimTarget mAlphaAnimTarget;
    private long mDefaultAutoHideDelay;
    private int mDefaultScrollBarAlpha;
    private int mDefaultScrollBarColor;
    private float mDefaultScrollBarMinHeight;
    private float mDefaultScrollBarRadius;
    private int mDefaultScrollBarTouchAlpha;
    private float mDefaultScrollBarTouchWidth;
    private float mDefaultScrollBarWidth;
    private float mDefaultTouchAreaExtendBottom;
    private float mDefaultTouchAreaExtendTop;
    private float mDefaultTouchAreaWidth;
    private ScrollBarAnimTarget mTouchIndicatorAnimTarget;
    private ScrollBarAnimTarget mWidthAnimTarget;
    private float originalScrollBarWidth;
    private Paint paint;
    private int scrollBarAlpha;
    private int scrollBarColor;
    private float scrollBarMinHeight;
    private float scrollBarRadius;
    private RectF scrollBarRect;
    private int scrollBarTouchAlpha;
    private float scrollBarTouchWidth;
    private float scrollBarWidth;
    private ScrollableView scrollableView;
    private boolean showTouchIndicator;
    private float touchAreaExtendBottom;
    private float touchAreaExtendTop;
    private float touchAreaWidth;
    private float touchDownX;
    private float touchDownY;
    private float touchIndicatorAlpha;
    private Paint touchIndicatorPaint;
    private RectF touchIndicatorRect;

    private void performHapticFeedback() {
    }

    static /* synthetic */ int access$808(HyperScrollBar hyperScrollBar) {
        int i = hyperScrollBar.initRetryCount;
        hyperScrollBar.initRetryCount = i + 1;
        return i;
    }

    private static class ScrollBarAnimTarget implements FolmeObject {
        private Folme.ObjectFolmeImpl mFolme;

        private ScrollBarAnimTarget() {
        }

        @Override // miuix.animation.FolmeObject
        public void setFolmeImpl(Folme.ObjectFolmeImpl objectFolmeImpl) {
            this.mFolme = objectFolmeImpl;
        }

        @Override // miuix.animation.FolmeObject
        public Folme.ObjectFolmeImpl folme() {
            return this.mFolme;
        }
    }

    public HyperScrollBar(Context context) {
        this(context, null);
    }

    public HyperScrollBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.miuixScrollBarStyle);
    }

    public HyperScrollBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.enableHapticFeedback = true;
        this.showTouchIndicator = false;
        this.touchIndicatorAlpha = 0.0f;
        this.currentAlpha = 0.0f;
        this.cachedScrollBarHeight = 0.0f;
        this.cachedScrollRange = -1;
        this.isDragging = false;
        this.isScrollBarTouched = false;
        this.lastTouchY = 0.0f;
        this.touchDownX = 0.0f;
        this.touchDownY = 0.0f;
        this.isInitialized = false;
        this.initRetryCount = 0;
        this.lastContentHeight = -1.0f;
        initDefaultValues(context);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HyperScrollBar, i, R.style.Widget_HyperScrollBar_DayNight);
        this.scrollBarColor = typedArrayObtainStyledAttributes.getColor(R.styleable.HyperScrollBar_scrollBarColor, this.mDefaultScrollBarColor);
        this.scrollBarAlpha = typedArrayObtainStyledAttributes.getInteger(R.styleable.HyperScrollBar_scrollBarAlpha, this.mDefaultScrollBarAlpha);
        this.scrollBarTouchAlpha = typedArrayObtainStyledAttributes.getInteger(R.styleable.HyperScrollBar_scrollBarTouchAlpha, this.mDefaultScrollBarTouchAlpha);
        this.scrollBarWidth = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperScrollBar_scrollBarWidth, this.mDefaultScrollBarWidth);
        this.scrollBarTouchWidth = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperScrollBar_scrollBarTouchWidth, this.mDefaultScrollBarTouchWidth);
        this.scrollBarMinHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperScrollBar_scrollBarMinHeight, this.mDefaultScrollBarMinHeight);
        this.scrollBarRadius = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperScrollBar_scrollBarRadius, this.mDefaultScrollBarRadius);
        this.touchAreaWidth = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperScrollBar_touchAreaWidth, this.mDefaultTouchAreaWidth);
        this.touchAreaExtendTop = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperScrollBar_touchAreaExtendTop, this.mDefaultTouchAreaExtendTop);
        this.touchAreaExtendBottom = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperScrollBar_touchAreaExtendBottom, this.mDefaultTouchAreaExtendBottom);
        this.autoHideDelay = typedArrayObtainStyledAttributes.getInteger(R.styleable.HyperScrollBar_autoHideDelay, (int) this.mDefaultAutoHideDelay);
        typedArrayObtainStyledAttributes.recycle();
        init();
        setWillNotDraw(false);
        setClickable(false);
    }

    private void initDefaultValues(Context context) {
        this.mDefaultScrollBarColor = context.getResources().getColor(R.color.miuix_hyper_scrollbar_default_color_light);
        this.mDefaultScrollBarAlpha = 26;
        this.mDefaultScrollBarTouchAlpha = 77;
        this.mDefaultScrollBarWidth = context.getResources().getDimension(R.dimen.miuix_appcompat_hyper_scrollbar_default_width);
        this.mDefaultScrollBarTouchWidth = context.getResources().getDimension(R.dimen.miuix_appcompat_hyper_scrollbar_default_touch_width);
        this.mDefaultScrollBarMinHeight = context.getResources().getDimension(R.dimen.miuix_appcompat_hyper_scrollbar_default_min_height);
        this.mDefaultScrollBarRadius = context.getResources().getDimension(R.dimen.miuix_appcompat_hyper_scrollbar_default_radius);
        this.mDefaultTouchAreaWidth = context.getResources().getDimension(R.dimen.miuix_appcompat_hyper_scrollbar_default_touch_area_width);
        this.mDefaultTouchAreaExtendTop = context.getResources().getDimension(R.dimen.miuix_appcompat_hyper_scrollbar_default_touch_extend_top);
        this.mDefaultTouchAreaExtendBottom = context.getResources().getDimension(R.dimen.miuix_appcompat_hyper_scrollbar_default_touch_extend_bottom);
        this.mDefaultAutoHideDelay = 2000L;
    }

    private void init() {
        Paint paint = new Paint(1);
        this.paint = paint;
        paint.setColor(this.scrollBarColor);
        this.scrollBarRect = new RectF();
        Paint paint2 = new Paint(1);
        this.touchIndicatorPaint = paint2;
        paint2.setColor(Color.parseColor("#000000"));
        this.touchIndicatorRect = new RectF();
        ScrollBarAnimTarget scrollBarAnimTarget = new ScrollBarAnimTarget();
        this.mAlphaAnimTarget = scrollBarAnimTarget;
        Folme.use((FolmeObject) scrollBarAnimTarget);
        this.mAlphaAnimTarget.folme().setTo(PROPERTY_SCROLL_BAR_ALPHA, 0);
        ScrollBarAnimTarget scrollBarAnimTarget2 = new ScrollBarAnimTarget();
        this.mWidthAnimTarget = scrollBarAnimTarget2;
        Folme.use((FolmeObject) scrollBarAnimTarget2);
        this.mWidthAnimTarget.folme().setTo(PROPERTY_SCROLL_BAR_WIDTH, Integer.valueOf((int) this.scrollBarWidth));
        ScrollBarAnimTarget scrollBarAnimTarget3 = new ScrollBarAnimTarget();
        this.mTouchIndicatorAnimTarget = scrollBarAnimTarget3;
        Folme.use((FolmeObject) scrollBarAnimTarget3);
        this.mTouchIndicatorAnimTarget.folme().setTo(PROPERTY_TOUCH_INDICATOR_ALPHA, 0);
        this.hideRunnable = new Runnable() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.1
            @Override // java.lang.Runnable
            public void run() {
                if (HyperScrollBar.this.isDragging || HyperScrollBar.this.currentAlpha <= 0.0f) {
                    return;
                }
                HyperScrollBar.this.fadeOut();
            }
        };
        this.originalScrollBarWidth = this.scrollBarWidth;
        setBackgroundColor(0);
        setVisibility(4);
    }

    public void attachToScrollableView(ScrollableView scrollableView) {
        this.scrollableView = scrollableView;
        scrollableView.setOnScrollListener(new ScrollableView.OnScrollListener() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.2
            @Override // miuix.miuixbasewidget.widget.ScrollableView.OnScrollListener
            public void onScroll(int i, int i2) {
                HyperScrollBar.this.updateScrollBar(i, i2);
            }
        });
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.3
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (HyperScrollBar.this.getHeight() > 0) {
                    HyperScrollBar.this.post(new Runnable() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            HyperScrollBar.this.refresh();
                        }
                    });
                }
            }
        });
        this.initRetryCount = 0;
        this.isInitialized = false;
        scheduleInitialization();
    }

    public void refresh() {
        this.cachedScrollRange = -1;
        this.cachedScrollBarHeight = 0.0f;
        this.lastContentHeight = -1.0f;
        this.isInitialized = false;
        if (this.scrollableView != null) {
            post(new Runnable() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.4
                @Override // java.lang.Runnable
                public void run() {
                    int scrollY = HyperScrollBar.this.scrollableView.getScrollY();
                    int scrollRange = HyperScrollBar.this.scrollableView.getScrollRange();
                    if (scrollRange > 0) {
                        HyperScrollBar.this.updateScrollBar(scrollY, scrollRange);
                    }
                }
            });
        }
    }

    public void onContainerSizeChanged() {
        refresh();
    }

    private void scheduleInitialization() {
        postDelayed(new Runnable() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.5
            @Override // java.lang.Runnable
            public void run() {
                if (HyperScrollBar.this.tryInitialize()) {
                    HyperScrollBar.this.isInitialized = true;
                } else if (HyperScrollBar.this.initRetryCount < 5) {
                    HyperScrollBar.access$808(HyperScrollBar.this);
                    HyperScrollBar hyperScrollBar = HyperScrollBar.this;
                    hyperScrollBar.postDelayed(this, (hyperScrollBar.initRetryCount + 1) * 100);
                }
            }
        }, 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean tryInitialize() {
        if (this.scrollableView != null && getHeight() > 0) {
            int scrollY = this.scrollableView.getScrollY();
            int scrollRange = this.scrollableView.getScrollRange();
            if (scrollRange > 0) {
                this.cachedScrollRange = -1;
                this.cachedScrollBarHeight = 0.0f;
                this.lastContentHeight = -1.0f;
                updateScrollBar(scrollY, scrollRange);
                return true;
            }
        }
        return false;
    }

    private void hideScrollBarImmediately() {
        removeCallbacks(this.hideRunnable);
        this.currentAlpha = 0.0f;
        this.mAlphaAnimTarget.folme().cancel();
        this.mAlphaAnimTarget.folme().setTo(PROPERTY_SCROLL_BAR_ALPHA, 0);
        setVisibility(4);
        this.isScrollBarTouched = false;
        this.isDragging = false;
        this.scrollBarRect.setEmpty();
        this.touchIndicatorRect.setEmpty();
    }

    private boolean isRtlLayout() {
        return getLayoutDirection() == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateScrollBar(int i, int i2) {
        if (i2 <= 0) {
            hideScrollBarImmediately();
            return;
        }
        if (this.scrollableView != null) {
            int height = getHeight();
            if (height + i2 <= height) {
                hideScrollBarImmediately();
                return;
            }
        }
        setVisibility(0);
        if (this.isDragging || this.currentAlpha < 0.1f) {
            showScrollBar();
        } else {
            removeCallbacks(this.hideRunnable);
        }
        float height2 = getHeight();
        float f = i / i2;
        float fCalculateScrollBarHeight = calculateScrollBarHeight(height2, i2);
        float f2 = (height2 - fCalculateScrollBarHeight) * f;
        this.scrollBarRect.set(isRtlLayout() ? 0.0f : getWidth() - this.scrollBarWidth, f2, isRtlLayout() ? this.scrollBarWidth : getWidth(), fCalculateScrollBarHeight + f2);
        updateTouchIndicatorRect();
        invalidate();
        if (this.isDragging) {
            return;
        }
        scheduleHide();
    }

    private void updateTouchIndicatorRect() {
        if (this.scrollBarRect.isEmpty()) {
            return;
        }
        this.touchIndicatorRect.set(isRtlLayout() ? 0.0f : getWidth() - this.touchAreaWidth, Math.max(0.0f, this.scrollBarRect.top - this.touchAreaExtendTop), isRtlLayout() ? this.touchAreaWidth : getWidth(), Math.min(getHeight(), this.scrollBarRect.bottom + this.touchAreaExtendBottom));
    }

    private float calculateScrollBarHeight(float f, int i) {
        int i2;
        if (!this.isInitialized || (i2 = this.cachedScrollRange) == -1 || Math.abs(i - i2) > Math.max(i * 0.05f, 50.0f) || Math.abs(f - this.lastContentHeight) > 1.0f) {
            float fMax = Math.max(this.scrollBarMinHeight, (f * f) / (i + f));
            this.cachedScrollBarHeight = fMax;
            this.cachedScrollRange = i;
            this.lastContentHeight = f;
            return fMax;
        }
        return this.cachedScrollBarHeight;
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        boolean z2 = getHeight() != i4 - i2;
        if ((z || z2) && this.scrollableView != null) {
            this.cachedScrollRange = -1;
            this.cachedScrollBarHeight = 0.0f;
            this.lastContentHeight = -1.0f;
            this.isInitialized = false;
            postDelayed(new Runnable() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.6
                @Override // java.lang.Runnable
                public void run() {
                    if (!HyperScrollBar.this.tryInitialize() || HyperScrollBar.this.currentAlpha <= 0.0f) {
                        return;
                    }
                    HyperScrollBar.this.updateScrollBar(HyperScrollBar.this.scrollableView.getScrollY(), HyperScrollBar.this.scrollableView.getScrollRange());
                }
            }, 50L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePaintAlpha() {
        this.paint.setAlpha((int) this.currentAlpha);
    }

    private void showScrollBar() {
        if (this.currentAlpha >= 1.0f) {
            removeCallbacks(this.hideRunnable);
            return;
        }
        removeCallbacks(this.hideRunnable);
        if (this.currentAlpha < 1.0f) {
            this.mAlphaAnimTarget.folme().to(PROPERTY_SCROLL_BAR_ALPHA, Integer.valueOf(this.scrollBarAlpha), new AnimConfig().setEase(FolmeEase.linear(100L)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.7
                @Override // miuix.animation.listener.TransitionListener
                public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                    UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperScrollBar.PROPERTY_SCROLL_BAR_ALPHA);
                    if (updateInfoFindBy != null) {
                        HyperScrollBar.this.currentAlpha = updateInfoFindBy.getFloatValue();
                        HyperScrollBar.this.updatePaintAlpha();
                        HyperScrollBar.this.invalidate();
                    }
                }
            }));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fadeOut() {
        if (this.currentAlpha <= 0.0f || this.isDragging) {
            return;
        }
        this.mAlphaAnimTarget.folme().to(PROPERTY_SCROLL_BAR_ALPHA, 0, new AnimConfig().setEase(FolmeEase.linear(100L)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.8
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperScrollBar.PROPERTY_SCROLL_BAR_ALPHA);
                if (updateInfoFindBy != null) {
                    HyperScrollBar.this.currentAlpha = updateInfoFindBy.getFloatValue();
                    HyperScrollBar.this.updatePaintAlpha();
                    HyperScrollBar.this.invalidate();
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (HyperScrollBar.this.currentAlpha == 0.0f) {
                    HyperScrollBar.this.setVisibility(4);
                }
            }
        }));
    }

    private void scheduleHide() {
        if (this.isDragging) {
            return;
        }
        removeCallbacks(this.hideRunnable);
        postDelayed(this.hideRunnable, this.autoHideDelay);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.showTouchIndicator && this.touchIndicatorAlpha > 0.0f && !this.touchIndicatorRect.isEmpty()) {
            this.touchIndicatorPaint.setAlpha((int) (this.touchIndicatorAlpha * 80.0f));
            canvas.drawRoundRect(this.touchIndicatorRect, dpToPx(8), dpToPx(8), this.touchIndicatorPaint);
        }
        if (this.currentAlpha <= 0.0f || this.scrollBarRect.isEmpty()) {
            return;
        }
        RectF rectF = this.scrollBarRect;
        float f = this.scrollBarRadius;
        canvas.drawRoundRect(rectF, f, f, this.paint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateScrollBarRectPosition() {
        if (this.scrollBarRect.isEmpty()) {
            return;
        }
        float f = this.scrollBarRect.top;
        this.scrollBarRect.set(isRtlLayout() ? 0.0f : getWidth() - this.scrollBarWidth, f, isRtlLayout() ? this.scrollBarWidth : getWidth(), this.scrollBarRect.height() + f);
    }

    private void showTouchIndicator() {
        if (this.showTouchIndicator) {
            this.mTouchIndicatorAnimTarget.folme().to(PROPERTY_TOUCH_INDICATOR_ALPHA, 255, new AnimConfig().setEase(FolmeEase.spring(0.88f, 0.12f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.9
                @Override // miuix.animation.listener.TransitionListener
                public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                    UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperScrollBar.PROPERTY_TOUCH_INDICATOR_ALPHA);
                    if (updateInfoFindBy != null) {
                        HyperScrollBar.this.touchIndicatorAlpha = updateInfoFindBy.getFloatValue() / 255.0f;
                        HyperScrollBar.this.invalidate();
                    }
                }
            }));
        }
    }

    private void hideTouchIndicator() {
        this.mTouchIndicatorAnimTarget.folme().to(PROPERTY_TOUCH_INDICATOR_ALPHA, 0, new AnimConfig().setEase(FolmeEase.spring(0.88f, 0.12f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.10
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperScrollBar.PROPERTY_TOUCH_INDICATOR_ALPHA);
                if (updateInfoFindBy != null) {
                    HyperScrollBar.this.touchIndicatorAlpha = updateInfoFindBy.getFloatValue() / 255.0f;
                    HyperScrollBar.this.invalidate();
                }
            }
        }));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            return handleActionDown(motionEvent);
        }
        if (action != 1) {
            if (action == 2) {
                return handleActionMove(motionEvent);
            }
            if (action != 3) {
                return false;
            }
        }
        if (this.isScrollBarTouched) {
            handleActionUp();
            return true;
        }
        hideTouchIndicator();
        return false;
    }

    private boolean handleActionDown(MotionEvent motionEvent) {
        this.touchDownX = motionEvent.getX();
        float y = motionEvent.getY();
        this.touchDownY = y;
        if (!isPointInScrollBarArea(this.touchDownX, y)) {
            return false;
        }
        this.isScrollBarTouched = true;
        removeCallbacks(this.hideRunnable);
        animateScrollBarToTouchState();
        setVisibility(0);
        showTouchIndicator();
        performHapticFeedback();
        initScrollBarIfNeeded();
        this.lastTouchY = motionEvent.getY();
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    private void animateScrollBarToTouchState() {
        animateScrollBarWidth(this.scrollBarTouchWidth);
        animateScrollBarAlpha(this.scrollBarTouchAlpha);
    }

    private void animateScrollBarWidth(float f) {
        this.mWidthAnimTarget.folme().to(PROPERTY_SCROLL_BAR_WIDTH, Float.valueOf(f), new AnimConfig().setEase(FolmeEase.linear(100L)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.11
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperScrollBar.PROPERTY_SCROLL_BAR_WIDTH);
                if (updateInfoFindBy != null) {
                    HyperScrollBar.this.scrollBarWidth = updateInfoFindBy.getFloatValue();
                    HyperScrollBar.this.updateScrollBarRectPosition();
                    HyperScrollBar.this.invalidate();
                }
            }
        }));
    }

    private void animateScrollBarAlpha(float f) {
        this.mAlphaAnimTarget.folme().to(PROPERTY_SCROLL_BAR_ALPHA, Float.valueOf(f), new AnimConfig().setEase(FolmeEase.linear(100L)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperScrollBar.12
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperScrollBar.PROPERTY_SCROLL_BAR_ALPHA);
                if (updateInfoFindBy != null) {
                    HyperScrollBar.this.currentAlpha = updateInfoFindBy.getFloatValue();
                    HyperScrollBar.this.updatePaintAlpha();
                    HyperScrollBar.this.invalidate();
                }
            }
        }));
    }

    private void initScrollBarIfNeeded() {
        ScrollableView scrollableView;
        if (!this.scrollBarRect.isEmpty() || (scrollableView = this.scrollableView) == null) {
            return;
        }
        int scrollY = scrollableView.getScrollY();
        int scrollRange = this.scrollableView.getScrollRange();
        if (scrollRange > 0) {
            updateScrollBar(scrollY, scrollRange);
        }
    }

    private boolean handleActionMove(MotionEvent motionEvent) {
        if (this.isScrollBarTouched && !this.isDragging) {
            checkAndStartDragging(motionEvent);
        }
        if (!this.isDragging) {
            return false;
        }
        handleScrollBarDrag(motionEvent.getY() - this.lastTouchY);
        this.lastTouchY = motionEvent.getY();
        return true;
    }

    private void checkAndStartDragging(MotionEvent motionEvent) {
        float fAbs = Math.abs(motionEvent.getX() - this.touchDownX);
        if (Math.abs(motionEvent.getY() - this.touchDownY) > 10.0f || fAbs > 10.0f) {
            this.isDragging = true;
        }
    }

    private void handleActionUp() {
        this.isDragging = false;
        this.isScrollBarTouched = false;
        animateScrollBarToNormalState();
        updatePaintAlpha();
        invalidate();
        scheduleHide();
        hideTouchIndicator();
        getParent().requestDisallowInterceptTouchEvent(false);
    }

    private void animateScrollBarToNormalState() {
        animateScrollBarWidth(this.originalScrollBarWidth);
        animateScrollBarAlpha(this.scrollBarAlpha);
    }

    private boolean isPointInScrollBarArea(float f, float f2) {
        float width = isRtlLayout() ? 0.0f : getWidth() - this.touchAreaWidth;
        float width2 = isRtlLayout() ? this.touchAreaWidth : getWidth();
        if (f < width || f > width2) {
            return false;
        }
        if (this.currentAlpha <= 0.1f || this.scrollBarRect.isEmpty()) {
            return f2 >= 0.0f && f2 <= ((float) getHeight());
        }
        return f2 >= Math.max(0.0f, this.scrollBarRect.top - this.touchAreaExtendTop) && f2 <= Math.min((float) getHeight(), this.scrollBarRect.bottom + this.touchAreaExtendBottom);
    }

    private void handleScrollBarDrag(float f) {
        if (this.scrollableView == null) {
            return;
        }
        float height = getHeight();
        if (this.scrollBarRect.isEmpty()) {
            int scrollY = this.scrollableView.getScrollY();
            int scrollRange = this.scrollableView.getScrollRange();
            if (scrollRange > 0) {
                float fCalculateScrollBarHeight = calculateScrollBarHeight(height, scrollRange);
                float f2 = (height - fCalculateScrollBarHeight) * (scrollY / scrollRange);
                this.scrollBarRect.set(isRtlLayout() ? 0.0f : getWidth() - this.scrollBarWidth, f2, isRtlLayout() ? this.scrollBarWidth : getWidth(), fCalculateScrollBarHeight + f2);
                showScrollBar();
                invalidate();
            }
            if (this.scrollBarRect.isEmpty()) {
                return;
            }
        }
        float fHeight = height - this.scrollBarRect.height();
        if (fHeight <= 0.0f) {
            return;
        }
        this.scrollableView.scrollTo((int) (this.scrollableView.getScrollRange() * (Math.max(0.0f, Math.min(fHeight, this.scrollBarRect.top + f)) / fHeight)));
        if (this.currentAlpha < 1.0f) {
            showScrollBar();
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.hideRunnable);
    }

    private float dpToPx(int i) {
        return i * getResources().getDisplayMetrics().density;
    }

    public void setScrollBarWidth(int i) {
        float fDpToPx = dpToPx(i);
        this.scrollBarWidth = fDpToPx;
        this.originalScrollBarWidth = fDpToPx;
        invalidate();
    }

    public void setScrollBarColor(int i) {
        int i2 = i | ViewCompat.MEASURED_STATE_MASK;
        this.scrollBarColor = i2;
        this.paint.setColor(i2);
        updatePaintAlpha();
        invalidate();
    }

    public void setScrollBarAlpha(int i) {
        this.scrollBarAlpha = (int) Math.max(0.0f, Math.min(255, i));
        updatePaintAlpha();
        invalidate();
    }

    public void setScrollBarTouchAlpha(int i) {
        this.scrollBarTouchAlpha = (int) Math.max(0.0f, Math.min(255, i));
    }

    public void setScrollBarRadius(int i) {
        this.scrollBarRadius = dpToPx(i);
        invalidate();
    }

    public void setAutoHideDelay(long j) {
        this.autoHideDelay = j;
    }

    public void setTouchAreaWidth(int i) {
        this.touchAreaWidth = dpToPx(i);
        invalidate();
    }

    public void setEnableHapticFeedback(boolean z) {
        this.enableHapticFeedback = z;
    }

    public void setShowTouchIndicator(boolean z) {
        this.showTouchIndicator = z;
        if (z) {
            return;
        }
        hideTouchIndicator();
    }

    public void setTouchAreaExtension(int i, int i2) {
        this.touchAreaExtendTop = dpToPx(i);
        this.touchAreaExtendBottom = dpToPx(i2);
    }

    public int getScrollBarColor() {
        return this.scrollBarColor;
    }

    public float getScrollBarAlpha() {
        return this.scrollBarAlpha;
    }

    public float getScrollBarTouchAlpha() {
        return this.scrollBarTouchAlpha;
    }
}
