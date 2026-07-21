package miuix.recyclerview.widget;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import miuix.animation.Folme;
import miuix.animation.controller.FolmeState;
import miuix.animation.property.FloatProperty;
import miuix.reflect.Reflects;
import miuix.spring.view.SpringHelper;

/* JADX INFO: loaded from: classes3.dex */
public abstract class SpringSnapHelper extends androidx.recyclerview.widget.RecyclerView.OnFlingListener {
    public static final int SNAP_TO_CENTER = 1;
    public static final int SNAP_TO_END = 2;
    public static final int SNAP_TO_START = 0;
    protected int mCurrentPosition;
    protected FolmeState mFolmeState;
    protected int mLastPosition;
    protected int mMax;
    protected int mMin;
    protected boolean mOutBounds;
    protected FloatProperty mProperty;
    protected RecyclerView mRecyclerView;
    private androidx.recyclerview.widget.RecyclerView.OnScrollListener mScrollListener;
    protected SpringHelper mSpringHelper;
    protected final float mMinVisibleChange = 0.2f;
    protected float mFriction = 0.61904764f;
    protected float mDamping = 1.0f;
    protected float mResponse = 0.4f;
    protected float mVelocityThreshold = 1000.0f;
    protected int mItemWidth = Integer.MAX_VALUE;
    protected int mItemHeight = Integer.MAX_VALUE;
    protected Rect mBounds = new Rect();
    protected int mSnapPreference = 0;

    static float getPredict(float f, float f2) {
        return (-f) / (f2 * (-4.2f));
    }

    abstract int computeFinalDistance(int i, int i2, int i3);

    abstract void snapFromFling(androidx.recyclerview.widget.RecyclerView.LayoutManager layoutManager, int i);

    abstract void updateConstructData();

    SpringSnapHelper() {
        init();
    }

    protected void init() {
        this.mScrollListener = new androidx.recyclerview.widget.RecyclerView.OnScrollListener() { // from class: miuix.recyclerview.widget.SpringSnapHelper.1
            boolean mScrolled = false;

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(androidx.recyclerview.widget.RecyclerView recyclerView, int i) {
                if (this.mScrolled) {
                    SpringSnapHelper.this.updateConstructData();
                    SpringSnapHelper.this.snapFromFling(recyclerView.getLayoutManager(), 0);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(androidx.recyclerview.widget.RecyclerView recyclerView, int i, int i2) {
                if (i == 0 && i2 == 0) {
                    return;
                }
                this.mScrolled = true;
            }
        };
        this.mFolmeState = (FolmeState) Folme.useValue(this).setFlags(1L);
    }

    public void onInterceptTouchEvent() {
        this.mFolmeState.cancel();
    }

    public void setSnapPreference(int i) {
        this.mSnapPreference = i;
    }

    public void setDamping(float f) {
        this.mDamping = f;
    }

    public void setResponse(float f) {
        this.mResponse = f;
    }

    public void setFriction(float f) {
        this.mFriction = f;
    }

    public void attachToRecyclerView(RecyclerView recyclerView) throws IllegalStateException {
        RecyclerView recyclerView2 = this.mRecyclerView;
        if (recyclerView2 == recyclerView) {
            return;
        }
        if (recyclerView2 != null) {
            destroyCallbacks();
        }
        this.mRecyclerView = recyclerView;
        if (recyclerView != null) {
            setupCallbacks();
            snapFromFling(this.mRecyclerView.getLayoutManager(), 0);
        }
    }

    private void setupCallbacks() throws IllegalStateException {
        if (this.mRecyclerView.getOnFlingListener() != null) {
            throw new IllegalStateException("An instance of OnFlingListener already set.");
        }
        this.mRecyclerView.addOnScrollListener(this.mScrollListener);
        this.mRecyclerView.setOnFlingListener(this);
        this.mRecyclerView.setOnTouchListener(new View.OnTouchListener() { // from class: miuix.recyclerview.widget.SpringSnapHelper$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.m1936x34d00114(view, motionEvent);
            }
        });
        this.mSpringHelper = getSpringHelper();
    }

    /* JADX INFO: renamed from: lambda$setupCallbacks$0$miuix-recyclerview-widget-SpringSnapHelper, reason: not valid java name */
    /* synthetic */ boolean m1936x34d00114(View view, MotionEvent motionEvent) {
        this.mFolmeState.cancel();
        return false;
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeOnScrollListener(this.mScrollListener);
        this.mRecyclerView.setOnFlingListener(null);
        this.mRecyclerView.setOnTouchListener(null);
        this.mSpringHelper = null;
    }

    private SpringHelper getSpringHelper() {
        if (this.mSpringHelper != null || !(this.mRecyclerView instanceof RecyclerView)) {
            return null;
        }
        try {
            return (SpringHelper) Reflects.getDeclaredField("androidx.recyclerview.widget.SpringRecyclerView", "mSpringHelper").get(this.mRecyclerView);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnFlingListener
    public boolean onFling(int i, int i2) {
        if (this.mRecyclerView.getLayoutManager() == null || this.mRecyclerView.getAdapter() == null) {
            return false;
        }
        updateConstructData();
        if (this instanceof HorizontalSnapHelper) {
            snapFromFling(this.mRecyclerView.getLayoutManager(), i);
            return true;
        }
        if (this instanceof VerticalSnapHelper) {
            snapFromFling(this.mRecyclerView.getLayoutManager(), i2);
        }
        return true;
    }

    static float predictDistance(float f, FloatProperty floatProperty, float f2, float f3) {
        float minVisibleChange = floatProperty.getMinVisibleChange();
        if (f * minVisibleChange < 0.0f) {
            minVisibleChange = -minVisibleChange;
        }
        float predict = getPredict(f, f2) - getPredict(minVisibleChange, f2);
        if (Math.abs(f) < f3) {
            return 0.0f;
        }
        return predict;
    }

    static float getFrictionTo(float f, float f2, FloatProperty floatProperty, float f3, float f4) {
        float minVisibleChange = floatProperty.getMinVisibleChange();
        if (f * minVisibleChange < 0.0f) {
            minVisibleChange = -minVisibleChange;
        }
        float f5 = f3 - f2;
        if (Math.abs(f) < f4 || f * f5 <= 0.0f) {
            return -1.0f;
        }
        return (float) (((double) ((-(f - minVisibleChange)) / f5)) / (-4.2d));
    }

    void setSpringHorizontalDistance(SpringHelper springHelper, int i) {
        if (springHelper == null) {
            return;
        }
        springHelper.setHorizontalDistance(i);
    }

    void setSpringVerticalDistance(SpringHelper springHelper, int i) {
        if (springHelper == null) {
            return;
        }
        springHelper.setVerticalDistance(i);
    }
}
