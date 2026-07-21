package miuix.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.R;
import androidx.recyclerview.widget.SpringRecyclerView;
import miuix.recyclerview.tool.GetSpeedForDynamicRefreshRate;

/* JADX INFO: loaded from: classes3.dex */
public class RecyclerView extends SpringRecyclerView {
    private static final int MIN_FLING_VELOCITY = 300;
    private final GetSpeedForDynamicRefreshRate mGetSpeedForDynamicRefreshRate;

    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.recyclerViewStyle);
    }

    public RecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setItemAnimator(new MiuiDefaultItemAnimator());
        if (Build.VERSION.SDK_INT > 30) {
            this.mGetSpeedForDynamicRefreshRate = new GetSpeedForDynamicRefreshRate(this);
        } else {
            this.mGetSpeedForDynamicRefreshRate = null;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public boolean fling(int i, int i2) {
        if (Math.abs(i) < 300) {
            i = 0;
        }
        if (Math.abs(i2) < 300) {
            i2 = 0;
        }
        if (i == 0 && i2 == 0) {
            return false;
        }
        return super.fling(i, i2);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.mGetSpeedForDynamicRefreshRate != null && Build.VERSION.SDK_INT >= 30) {
            this.mGetSpeedForDynamicRefreshRate.touchEvent(motionEvent);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void onScrolled(int i, int i2) {
        if (this.mGetSpeedForDynamicRefreshRate != null && Build.VERSION.SDK_INT >= 30) {
            this.mGetSpeedForDynamicRefreshRate.calculateSpeed(i, i2, getDragFlingVelocityX(), getDragFlingVelocityY());
        }
        super.onScrolled(i, i2);
    }

    @Override // androidx.recyclerview.widget.SpringRecyclerView, androidx.recyclerview.widget.RecyclerView
    public void onScrollStateChanged(int i) {
        super.onScrollStateChanged(i);
        if (this.mGetSpeedForDynamicRefreshRate == null || Build.VERSION.SDK_INT < 30) {
            return;
        }
        this.mGetSpeedForDynamicRefreshRate.scrollState(this, i);
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (this.mGetSpeedForDynamicRefreshRate == null || Build.VERSION.SDK_INT < 30) {
            return;
        }
        this.mGetSpeedForDynamicRefreshRate.onFocusChange(z);
    }
}
