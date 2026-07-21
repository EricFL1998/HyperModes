package miuix.internal.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import java.util.Arrays;
import miuix.appcompat.R;
import miuix.smooth.SmoothCornerHelper;

/* JADX INFO: loaded from: classes2.dex */
public class RoundFrameLayout extends FrameLayout {
    private Region mAreaRegion;
    private int mBorderColor;
    private float mBorderWidth;
    private Path mClipPath;
    private RectF mLayer;
    private Paint mPaint;
    private float[] mRadii;
    private float mRadius;
    private boolean mUseSmooth;

    public RoundFrameLayout(Context context) {
        this(context, null);
    }

    public RoundFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RoundFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        float dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_immersion_menu_background_radius);
        this.mRadius = dimensionPixelSize;
        this.mRadii = new float[]{dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize};
        this.mLayer = new RectF();
        this.mUseSmooth = true;
        SmoothCornerHelper.setViewSmoothCornerEnable(this, true);
        this.mClipPath = new Path();
        this.mAreaRegion = new Region();
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(-1);
        this.mPaint.setAntiAlias(true);
    }

    public void setRadius(float f) {
        this.mRadius = f;
        setRadius(new float[]{f, f, f, f, f, f, f, f});
    }

    public void setRadius(float[] fArr) {
        if (Arrays.equals(this.mRadii, fArr)) {
            return;
        }
        this.mRadii = fArr;
        refreshRegion();
        invalidate();
    }

    public void setBorder(float f, int i) {
        this.mBorderWidth = f;
        this.mBorderColor = i;
        invalidate();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mLayer.set(0.0f, 0.0f, i, i2);
        refreshRegion();
    }

    public void enableSmoothRound(boolean z) {
        this.mUseSmooth = z;
        SmoothCornerHelper.setViewSmoothCornerEnable(this, z);
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        onClipDraw(canvas);
        super.dispatchDraw(canvas);
        onBorderDraw(canvas);
    }

    private void refreshRegion() {
        if (this.mRadii == null) {
            return;
        }
        int iWidth = (int) this.mLayer.width();
        int iHeight = (int) this.mLayer.height();
        RectF rectF = new RectF();
        rectF.left = getPaddingLeft();
        rectF.top = getPaddingTop();
        rectF.right = iWidth - getPaddingRight();
        rectF.bottom = iHeight - getPaddingBottom();
        this.mClipPath.reset();
        this.mClipPath.addRoundRect(rectF, this.mRadii, Path.Direction.CW);
        this.mAreaRegion.setPath(this.mClipPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
    }

    public void onClipDraw(Canvas canvas) {
        if (this.mRadii == null) {
            return;
        }
        canvas.clipPath(this.mClipPath);
    }

    private void onBorderDraw(Canvas canvas) {
        if (this.mRadii == null || this.mBorderWidth == 0.0f || Color.alpha(this.mBorderColor) == 0) {
            return;
        }
        int iWidth = (int) this.mLayer.width();
        int iHeight = (int) this.mLayer.height();
        RectF rectF = new RectF();
        float f = this.mBorderWidth / 2.0f;
        rectF.left = getPaddingLeft() + f;
        rectF.top = getPaddingTop() + f;
        rectF.right = (iWidth - getPaddingRight()) - f;
        rectF.bottom = (iHeight - getPaddingBottom()) - f;
        this.mPaint.reset();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mBorderColor);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(this.mBorderWidth);
        float f2 = this.mRadius - (f * 2.0f);
        canvas.drawRoundRect(rectF, f2, f2, this.mPaint);
    }
}
