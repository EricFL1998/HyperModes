package miuix.preference.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import miuix.internal.graphics.drawable.TaggingDrawable;

/* JADX INFO: loaded from: classes3.dex */
public class MaskTaggingDrawable extends TaggingDrawable {
    private Paint mClipPaint;
    private boolean mDrawCornerBottom;
    private boolean mDrawCornerTop;
    private int mLeft;
    private boolean mMaskEnabled;
    private int mMaskPaddingBottom;
    private int mMaskPaddingEnd;
    private int mMaskPaddingStart;
    private int mMaskPaddingTop;
    private int mMaskRadius;
    private int mRight;
    private boolean mRtl;

    public MaskTaggingDrawable(Drawable drawable) {
        super(drawable);
        this.mDrawCornerTop = false;
        this.mDrawCornerBottom = false;
        this.mMaskEnabled = false;
    }

    public MaskTaggingDrawable(Drawable drawable, int[] iArr) {
        super(drawable, iArr);
        this.mDrawCornerTop = false;
        this.mDrawCornerBottom = false;
        this.mMaskEnabled = false;
    }

    @Override // miuix.internal.graphics.drawable.DrawableWrapperCompat, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (!this.mMaskEnabled || this.mClipPaint == null) {
            return;
        }
        if (this.mLeft == 0 && this.mRight == 0) {
            return;
        }
        Rect bounds = getBounds();
        drawMask(canvas, this.mLeft, bounds.top - this.mMaskPaddingTop, this.mRight, bounds.top, false, false, true, this.mRtl);
        drawMask(canvas, this.mLeft, bounds.bottom, this.mRight, bounds.bottom + this.mMaskPaddingBottom, false, false, true, this.mRtl);
        drawMask(canvas, this.mLeft, bounds.top, this.mRight, bounds.bottom, this.mDrawCornerTop, this.mDrawCornerBottom, false, this.mRtl);
    }

    public void setClipPaint(Paint paint, int i, int i2, int i3, int i4, int i5) {
        this.mClipPaint = paint;
        this.mMaskPaddingTop = i;
        this.mMaskPaddingBottom = i2;
        this.mMaskPaddingStart = i3;
        this.mMaskPaddingEnd = i4;
        this.mMaskRadius = i5;
    }

    public void setLeftRight(int i, int i2, boolean z) {
        this.mRtl = z;
        this.mLeft = i;
        this.mRight = i2;
    }

    public void updateDrawCorner(boolean z, boolean z2) {
        this.mDrawCornerTop = z;
        this.mDrawCornerBottom = z2;
    }

    public void setMaskEnabled(boolean z) {
        this.mMaskEnabled = z;
    }

    public boolean getMaskEnabled() {
        return this.mMaskEnabled;
    }

    private void drawMask(Canvas canvas, int i, int i2, int i3, int i4, boolean z, boolean z2, boolean z3, boolean z4) {
        float f = i2;
        float f2 = i4;
        RectF rectF = new RectF(i, f, i3, f2);
        RectF rectF2 = new RectF(i + (z4 ? this.mMaskPaddingEnd : this.mMaskPaddingStart), f, i3 - (z4 ? this.mMaskPaddingStart : this.mMaskPaddingEnd), f2);
        Path path = new Path();
        float f3 = z ? this.mMaskRadius : 0.0f;
        float f4 = z2 ? this.mMaskRadius : 0.0f;
        path.addRoundRect(rectF2, new float[]{f3, f3, f3, f3, f4, f4, f4, f4}, Path.Direction.CW);
        int iSaveLayer = canvas.saveLayer(rectF, this.mClipPaint, 31);
        canvas.drawRect(rectF, this.mClipPaint);
        if (z3) {
            this.mClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        } else {
            this.mClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        }
        canvas.drawPath(path, this.mClipPaint);
        this.mClipPaint.setXfermode(null);
        canvas.restoreToCount(iSaveLayer);
    }
}
