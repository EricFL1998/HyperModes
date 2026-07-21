package miuix.smooth.internal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import miuix.smooth.SmoothPathProvider2;

/* JADX INFO: loaded from: classes3.dex */
public class SmoothDrawHelper {
    private static final float EXTRA_PADDING = 0.5f;
    private Path mClipPath;
    private RectF mLayer;
    private Path mOutterPath;
    private SmoothPathProvider2 mPathProvider;
    private float[] mRadii;
    private float mRadius;
    private Paint mStrokePaint;
    private int mStrokeWidth = 0;
    private int mStrokeColor = 0;
    private Paint mClipPaint = new Paint(1);

    public SmoothDrawHelper() {
        Paint paint = new Paint(1);
        this.mStrokePaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.mOutterPath = new Path();
        this.mClipPath = new Path();
        this.mPathProvider = new SmoothPathProvider2();
        this.mLayer = new RectF();
    }

    public void setStrokeWidth(int i) {
        this.mStrokeWidth = i;
    }

    public int getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public void setStrokeColor(int i) {
        this.mStrokeColor = i;
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    public void setRadii(float[] fArr) {
        this.mRadii = fArr;
    }

    public float[] getRadii() {
        return this.mRadii;
    }

    public void setRadius(float f) {
        this.mRadius = f;
    }

    public float getRadius() {
        return this.mRadius;
    }

    public void setAlpha(int i) {
        this.mStrokePaint.setAlpha(i);
    }

    public int getAlpha() {
        return this.mStrokePaint.getAlpha();
    }

    public void onBoundsChange(Rect rect) {
        float f = 0.5f;
        this.mLayer.set(rect.left - 0.5f, rect.top - 0.5f, rect.right + 0.5f, rect.bottom + 0.5f);
        if (this.mStrokeWidth != 0 && this.mStrokePaint.getAlpha() != 0 && Color.alpha(this.mStrokeColor) != 0) {
            f = 0.5f + (this.mStrokeWidth / 2.0f);
        }
        float f2 = f;
        this.mOutterPath = getSmoothPathFromProvider(this.mOutterPath, this.mLayer, this.mRadii, this.mRadius, f2, f2);
        Path path = this.mClipPath;
        if (path != null) {
            path.reset();
        } else {
            this.mClipPath = new Path();
        }
        this.mClipPath.addRect(this.mLayer, Path.Direction.CW);
        this.mClipPath.op(this.mOutterPath, Path.Op.DIFFERENCE);
    }

    private Path getSmoothPathFromProvider(Path path, RectF rectF, float[] fArr, float f, float f2, float f3) {
        SmoothPathProvider2.SmoothData smoothDataBuildSmoothData;
        if (fArr == null) {
            smoothDataBuildSmoothData = this.mPathProvider.buildSmoothData(rectF, f, f2, f3);
        } else {
            smoothDataBuildSmoothData = this.mPathProvider.buildSmoothData(rectF, fArr, f2, f3);
        }
        return this.mPathProvider.getSmoothPath(path, smoothDataBuildSmoothData);
    }

    public void drawMask(Canvas canvas, Xfermode xfermode) {
        this.mClipPaint.setXfermode(xfermode);
        canvas.drawPath(this.mClipPath, this.mClipPaint);
        this.mClipPaint.setXfermode(null);
    }

    public void drawStroke(Canvas canvas) {
        if (this.mStrokeWidth == 0 || this.mStrokePaint.getAlpha() == 0 || Color.alpha(this.mStrokeColor) == 0) {
            return;
        }
        canvas.save();
        this.mStrokePaint.setStrokeWidth(this.mStrokeWidth);
        this.mStrokePaint.setColor(this.mStrokeColor);
        canvas.drawPath(this.mOutterPath, this.mStrokePaint);
        canvas.restore();
    }

    public Path getSmoothPath(Rect rect) {
        float f = 0.5f;
        if (this.mStrokeWidth != 0 && this.mStrokePaint.getAlpha() != 0 && Color.alpha(this.mStrokeColor) != 0) {
            f = 0.5f + (this.mStrokeWidth / 2.0f);
        }
        float f2 = f;
        return getSmoothPathFromProvider(new Path(), new RectF(rect), this.mRadii, this.mRadius, f2, f2);
    }
}
