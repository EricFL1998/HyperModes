package miuix.popupwidget.internal.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.core.view.ViewCompat;
import miuix.popupwidget.R;

/* JADX INFO: loaded from: classes3.dex */
public class ArrowPopupContentWrapper extends LinearLayout {
    public static final byte ARROW_BOTTOM_LEFT_MODE = 18;
    public static final byte ARROW_BOTTOM_MODE = 16;
    public static final byte ARROW_BOTTOM_RIGHT_MODE = 17;
    public static final byte ARROW_LEFT_MODE = 32;
    public static final byte ARROW_NONE_MODE = 0;
    public static final byte ARROW_RIGHT_MODE = 64;
    public static final byte ARROW_TOP_LEFT_MODE = 9;
    public static final byte ARROW_TOP_MODE = 8;
    public static final byte ARROW_TOP_RIGHT_MODE = 10;
    public static final int LAYOUT_MODE_LTR = 0;
    public static final int LAYOUT_MODE_RTL = 1;
    public static final int LAYOUT_MODE_UNSPECIFIED = 2;
    float density;
    private float mArrowHorizonOffset;
    private int mArrowMode;
    private float mArrowVerticalOffset;
    private Paint mBackgroundPaint;
    private boolean mIsRtl;
    private Bitmap mMask1;
    private Bitmap mMask2;
    private Bitmap mMask3;
    private Bitmap mMask4;
    private Paint mPaint;
    private Path mPath;
    private int mRtlMode;
    private PointF middle;
    private PointF p0;
    private PointF p1;
    private PointF p2;
    private PointF p3;
    private PointF p4;
    private PointF p5;
    private PointF p6;
    private PointF p7;
    private PointF pA;
    private PointF pB;
    private PointF pC;
    private PointF pD;
    private PointF pE;
    private PointF pF;
    private PointF pG;
    private PointF pH;
    private PointF pI;
    private PointF pJ;
    private PointF pK;
    private PointF pL;
    private PointF pM;
    float paddingBottom;
    float paddingEnd;
    float paddingStart;
    float paddingTop;
    float radius;

    public ArrowPopupContentWrapper(Context context) {
        this(context, null);
    }

    public ArrowPopupContentWrapper(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ArrowPopupContentWrapper(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mArrowMode = 0;
        Paint paint = new Paint();
        this.mPaint = paint;
        this.mIsRtl = false;
        this.mRtlMode = 2;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        this.mPaint.setAntiAlias(true);
        Resources resources = getResources();
        this.mMask1 = BitmapFactory.decodeResource(resources, R.drawable.miuix_appcompat_popup_mask_1);
        this.mMask2 = BitmapFactory.decodeResource(resources, R.drawable.miuix_appcompat_popup_mask_2);
        this.mMask3 = BitmapFactory.decodeResource(resources, R.drawable.miuix_appcompat_popup_mask_3);
        this.mMask4 = BitmapFactory.decodeResource(resources, R.drawable.miuix_appcompat_popup_mask_4);
        init();
    }

    public void setArrowBackgroundPaintColor(int i) {
        Paint paint = this.mBackgroundPaint;
        if (paint != null) {
            paint.setColor(i);
        }
    }

    private void init() {
        Paint paint = new Paint();
        this.mBackgroundPaint = paint;
        paint.setAntiAlias(true);
        this.mPath = new Path();
        setWillNotDraw(false);
        this.mBackgroundPaint.setStyle(Paint.Style.FILL);
        this.mBackgroundPaint.setAntiAlias(true);
        this.mPath = new Path();
        this.p0 = new PointF();
        this.p1 = new PointF();
        this.p2 = new PointF();
        this.p3 = new PointF();
        this.p4 = new PointF();
        this.p5 = new PointF();
        this.p6 = new PointF();
        this.p7 = new PointF();
        this.pA = new PointF();
        this.pB = new PointF();
        this.pC = new PointF();
        this.pD = new PointF();
        this.pE = new PointF();
        this.pF = new PointF();
        this.pG = new PointF();
        this.pH = new PointF();
        this.pI = new PointF();
        this.pJ = new PointF();
        this.pK = new PointF();
        this.pL = new PointF();
        this.pM = new PointF();
        this.middle = new PointF();
        this.paddingStart = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_arrow_popup_view_paddingStart);
        this.paddingEnd = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_arrow_popup_view_paddingEnd);
        this.paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_arrow_popup_view_paddingTop);
        this.paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_arrow_popup_view_paddingBottom);
        this.radius = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_arrow_popup_view_round_corners);
        this.density = getResources().getDisplayMetrics().density;
    }

    public void setArrowMode(int i) {
        this.mArrowMode = i;
    }

    public void setArrowHorizonOffset(float f) {
        this.mArrowHorizonOffset = f;
    }

    public void setArrowVerticalOffset(float f) {
        this.mArrowVerticalOffset = f;
    }

    public void setRtlMode(int i) {
        this.mRtlMode = i;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(), null, 31);
        super.dispatchDraw(canvas);
        canvas.drawBitmap(this.mMask1, getPaddingLeft(), getPaddingTop(), this.mPaint);
        canvas.drawBitmap(this.mMask2, (getWidth() - this.mMask2.getWidth()) - getPaddingRight(), getPaddingTop(), this.mPaint);
        canvas.drawBitmap(this.mMask3, getPaddingLeft(), (getHeight() - this.mMask3.getHeight()) - getPaddingBottom(), this.mPaint);
        canvas.drawBitmap(this.mMask4, (getWidth() - this.mMask4.getWidth()) - getPaddingRight(), (getHeight() - this.mMask4.getHeight()) - getPaddingBottom(), this.mPaint);
        canvas.restore();
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        boolean z;
        Path path;
        super.onDraw(canvas);
        this.mIsRtl = ViewCompat.getLayoutDirection(this) == 1;
        Path path2 = this.mPath;
        if (path2 != null) {
            path2.reset();
        }
        float width = getWidth();
        float height = getHeight();
        float f = this.paddingTop;
        float f2 = height - this.paddingBottom;
        float f3 = this.paddingStart;
        float f4 = width - this.paddingEnd;
        int i = this.mArrowMode;
        if (i == 8 || i == 16) {
            float f5 = this.mArrowHorizonOffset;
            float f6 = this.radius;
            float f7 = this.density;
            float f8 = width / 2.0f;
            if (f5 < ((f3 + f6) + (f7 * 14.0f)) - f8) {
                this.mArrowHorizonOffset = ((f6 + f3) + (f7 * 14.0f)) - f8;
            } else if (f5 > ((f4 - f6) - (f7 * 14.0f)) - f8) {
                this.mArrowHorizonOffset = ((f4 - f6) - (f7 * 14.0f)) - f8;
            }
        }
        if (i == 32 || i == 64) {
            float f9 = this.mArrowVerticalOffset;
            float f10 = this.radius;
            float f11 = this.density;
            float f12 = height / 2.0f;
            if (f9 < ((f + f10) + (f11 * 8.0f)) - f12) {
                this.mArrowVerticalOffset = ((f10 + f) + (f11 * 8.0f)) - f12;
            } else if (f9 > ((f2 - f10) - (f11 * 8.0f)) - f12) {
                this.mArrowVerticalOffset = ((f2 - f10) - (f11 * 8.0f)) - f12;
            }
        }
        this.p0.set(this.radius + f3, f);
        this.p1.set(f4, this.radius + f);
        this.p2.set(f4, f2 - this.radius);
        this.p3.set(f4 - this.radius, f2);
        this.p4.set(this.radius + f3, f2);
        this.p5.set(f3, f2 - this.radius);
        this.p6.set(f3, this.radius + f);
        this.p7.set(f4 - this.radius, f);
        int i2 = this.mRtlMode;
        if (i2 != 2) {
            z = i2 == 1;
        } else {
            z = this.mIsRtl;
        }
        if ((!z && this.mArrowMode == 10) || (z && this.mArrowMode == 9)) {
            drawTopRightArrow(this.density, f3, f4, f, f2, this.p0, this.p2, this.p3, this.p4, this.p5, this.p6);
        } else {
            int i3 = this.mArrowMode;
            if (i3 == 8) {
                drawTopArrow(width, this.density, f3, f4, f, f2, this.p0, this.p1, this.p2, this.p3, this.p4, this.p5, this.p6, this.p7);
            } else if ((!z && i3 == 9) || (z && i3 == 10)) {
                drawTopLeftArrow(this.radius, this.density, f3, f4, f, f2, this.p1, this.p2, this.p3, this.p4, this.p5, this.p6, this.p7);
            } else if ((!z && i3 == 32) || (z && i3 == 64)) {
                drawLeftArrow(this.radius, this.density, f3, f4, f, f2, this.p0, this.p1, this.p2, this.p3, this.p4, this.p5, this.p6);
            } else if ((!z && i3 == 64) || (z && i3 == 32)) {
                drawRightArrow(this.radius, this.density, f3, f4, f, f2, this.p0, this.p1, this.p2, this.p3, this.p4, this.p5, this.p6);
            } else if ((!z && i3 == 17) || (z && i3 == 18)) {
                drawBottomRightArrow(this.radius, this.density, f3, f4, f, f2, this.p0, this.p1, this.p2, this.p4, this.p5, this.p6);
            } else if (i3 == 16) {
                drawBottomArrow(width, this.radius, this.density, f3, f4, f, f2, this.p0, this.p1, this.p2, this.p3, this.p4, this.p5, this.p6);
            } else if ((!z && i3 == 18) || (z && i3 == 17)) {
                drawBottomLeftArrow(this.radius, this.density, f3, f4, f, f2, this.p0, this.p1, this.p2, this.p3, this.p6);
            }
        }
        Paint paint = this.mBackgroundPaint;
        if (paint == null || (path = this.mPath) == null) {
            return;
        }
        canvas.drawPath(path, paint);
    }

    private void drawTopRightArrow(float f, float f2, float f3, float f4, float f5, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4, PointF pointF5, PointF pointF6) {
        float f6 = 28.0f * f;
        this.pA.set((f3 - this.radius) - f6, f4);
        this.pB.set(this.pA.x + (2.2988f * f), f4);
        float f7 = f4 - (0.8772f * f);
        this.pC.set(this.pA.x + (4.5169f * f), f7);
        float f8 = f4 - (2.4636f * f);
        this.pD.set(this.pA.x + (6.2295f * f), f8);
        float f9 = f4 - (8.5073f * f);
        this.pF.set(this.pA.x + (12.7541f * f), f9);
        float f10 = f4 - (9.1642f * f);
        this.pG.set(this.pA.x + (13.4633f * f), f10);
        this.pH.set(this.pA.x + (14.5367f * f), f10);
        this.pI.set(this.pA.x + (15.2459f * f), f9);
        this.pJ.set(this.pA.x + (21.7705f * f), f8);
        this.pK.set(this.pA.x + (23.4831f * f), f7);
        this.pL.set(this.pA.x + (25.7012f * f), f4);
        this.pM.set(this.pA.x + f6, f4);
        Path path = this.mPath;
        if (path != null) {
            path.moveTo(pointF.x, pointF.y);
            this.mPath.lineTo(this.pA.x, this.pA.y);
            this.mPath.cubicTo(this.pB.x, this.pB.y, this.pC.x, this.pC.y, this.pD.x, this.pD.y);
            this.mPath.lineTo(this.pF.x, this.pF.y);
            this.mPath.cubicTo(this.pG.x, this.pG.y, this.pH.x, this.pH.y, this.pI.x, this.pI.y);
            this.mPath.lineTo(this.pJ.x, this.pJ.y);
            this.mPath.cubicTo(this.pK.x, this.pK.y, this.pL.x, this.pL.y, this.pM.x, this.pM.y);
            this.mPath.quadTo(f3, f4, f3, this.radius + f4);
            this.mPath.lineTo(pointF2.x, pointF2.y);
            this.mPath.quadTo(f3, f5, pointF3.x, pointF3.y);
            this.mPath.lineTo(pointF4.x, pointF4.y);
            this.mPath.quadTo(f2, f5, pointF5.x, pointF5.y);
            this.mPath.lineTo(pointF6.x, pointF6.y);
            this.mPath.quadTo(f2, f4, pointF.x, pointF.y);
            this.mPath.close();
        }
    }

    private void drawTopArrow(float f, float f2, float f3, float f4, float f5, float f6, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4, PointF pointF5, PointF pointF6, PointF pointF7, PointF pointF8) {
        this.middle.set(f / 2.0f, f5);
        this.pA.set((this.middle.x - (14.0f * f2)) + this.mArrowHorizonOffset, this.middle.y);
        this.pB.set(this.pA.x + (2.2988f * f2), f5);
        float f7 = f5 - (0.8772f * f2);
        this.pC.set(this.pA.x + (4.5169f * f2), f7);
        float f8 = f5 - (2.4636f * f2);
        this.pD.set(this.pA.x + (6.2295f * f2), f8);
        float f9 = f5 - (8.5073f * f2);
        this.pF.set(this.pA.x + (12.7541f * f2), f9);
        float f10 = f5 - (9.1642f * f2);
        this.pG.set(this.pA.x + (13.4633f * f2), f10);
        this.pH.set(this.pA.x + (14.5367f * f2), f10);
        this.pI.set(this.pA.x + (15.2459f * f2), f9);
        this.pJ.set(this.pA.x + (21.7705f * f2), f8);
        this.pK.set(this.pA.x + (23.4831f * f2), f7);
        this.pL.set(this.pA.x + (25.7012f * f2), f5);
        this.pM.set(this.pA.x + (28.0f * f2), f5);
        Path path = this.mPath;
        if (path != null) {
            path.moveTo(pointF.x, pointF.y);
            this.mPath.lineTo(this.pA.x, this.pA.y);
            this.mPath.cubicTo(this.pB.x, this.pB.y, this.pC.x, this.pC.y, this.pD.x, this.pD.y);
            this.mPath.lineTo(this.pF.x, this.pF.y);
            this.mPath.cubicTo(this.pG.x, this.pG.y, this.pH.x, this.pH.y, this.pI.x, this.pI.y);
            this.mPath.lineTo(this.pJ.x, this.pJ.y);
            this.mPath.cubicTo(this.pK.x, this.pK.y, this.pL.x, this.pL.y, this.pM.x, this.pM.y);
            this.mPath.lineTo(pointF8.x, pointF8.y);
            this.mPath.quadTo(f4, f5, pointF2.x, pointF2.y);
            this.mPath.lineTo(pointF3.x, pointF3.y);
            this.mPath.quadTo(f4, f6, pointF4.x, pointF4.y);
            this.mPath.lineTo(pointF5.x, pointF5.y);
            this.mPath.quadTo(f3, f6, pointF6.x, pointF6.y);
            this.mPath.lineTo(pointF7.x, pointF7.y);
            this.mPath.quadTo(f3, f5, pointF.x, pointF.y);
            this.mPath.close();
        }
    }

    private void drawTopLeftArrow(float f, float f2, float f3, float f4, float f5, float f6, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4, PointF pointF5, PointF pointF6, PointF pointF7) {
        this.pA.set(f3 + f, f5);
        this.pB.set(this.pA.x + (2.2988f * f2), f5);
        float f7 = f5 - (0.8772f * f2);
        this.pC.set(this.pA.x + (4.5169f * f2), f7);
        float f8 = f5 - (2.4636f * f2);
        this.pD.set(this.pA.x + (6.2295f * f2), f8);
        float f9 = f5 - (8.5073f * f2);
        this.pF.set(this.pA.x + (12.7541f * f2), f9);
        float f10 = f5 - (9.1642f * f2);
        this.pG.set(this.pA.x + (13.4633f * f2), f10);
        this.pH.set(this.pA.x + (14.5367f * f2), f10);
        this.pI.set(this.pA.x + (15.2459f * f2), f9);
        this.pJ.set(this.pA.x + (21.7705f * f2), f8);
        this.pK.set(this.pA.x + (23.4831f * f2), f7);
        this.pL.set(this.pA.x + (25.7012f * f2), f5);
        this.pM.set(this.pA.x + (28.0f * f2), f5);
        pointF6.set(f3, f5 + f);
        Path path = this.mPath;
        if (path != null) {
            path.moveTo(this.pA.x, this.pA.y);
            this.mPath.cubicTo(this.pB.x, this.pB.y, this.pC.x, this.pC.y, this.pD.x, this.pD.y);
            this.mPath.lineTo(this.pF.x, this.pF.y);
            this.mPath.cubicTo(this.pG.x, this.pG.y, this.pH.x, this.pH.y, this.pI.x, this.pI.y);
            this.mPath.lineTo(this.pJ.x, this.pJ.y);
            this.mPath.cubicTo(this.pK.x, this.pK.y, this.pL.x, this.pL.y, this.pM.x, this.pM.y);
            this.mPath.lineTo(pointF7.x, pointF7.y);
            this.mPath.quadTo(f4, f5, pointF.x, pointF.y);
            this.mPath.lineTo(pointF2.x, pointF2.y);
            this.mPath.quadTo(f4, f6, pointF3.x, pointF3.y);
            this.mPath.lineTo(pointF4.x, pointF4.y);
            this.mPath.quadTo(f3, f6, pointF5.x, pointF5.y);
            this.mPath.lineTo(pointF6.x, pointF6.y);
            this.mPath.quadTo(f3, f5, this.pA.x, this.pA.y);
            this.mPath.close();
        }
    }

    private void drawLeftArrow(float f, float f2, float f3, float f4, float f5, float f6, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4, PointF pointF5, PointF pointF6, PointF pointF7) {
        this.pA.set(f3, ((f6 - f5) / 2.0f) + f5 + this.mArrowVerticalOffset);
        float f7 = 8.0f * f2;
        this.pB.set(this.pA.x, this.pA.y + f7);
        float f8 = 1.7716f * f2;
        this.pC.set(this.pA.x - (7.1326f * f2), this.pA.y + f8);
        float f9 = 8.2892f * f2;
        float f10 = 0.7613f * f2;
        this.pD.set(this.pA.x - f9, this.pA.y + f10);
        this.pE.set(this.pA.x - f9, this.pA.y - f10);
        this.pF.set(this.pA.x - (7.1323f * f2), this.pA.y - f8);
        this.pG.set(this.pA.x, this.pA.y - f7);
        Path path = this.mPath;
        if (path != null) {
            path.moveTo(pointF.x, pointF.y);
            this.mPath.lineTo(f4 - f, f5);
            this.mPath.quadTo(f4, f5, pointF2.x, pointF2.y);
            this.mPath.lineTo(pointF3.x, pointF3.y);
            this.mPath.quadTo(f4, f6, pointF4.x, pointF4.y);
            this.mPath.lineTo(pointF5.x, pointF5.y);
            this.mPath.quadTo(f3, f6, pointF6.x, pointF6.y);
            this.mPath.lineTo(this.pB.x, this.pB.y);
            this.mPath.lineTo(this.pC.x, this.pC.y);
            this.mPath.cubicTo(this.pD.x, this.pD.y, this.pE.x, this.pE.y, this.pF.x, this.pF.y);
            this.mPath.lineTo(this.pG.x, this.pG.y);
            this.mPath.lineTo(pointF7.x, pointF7.y);
            this.mPath.quadTo(f3, f5, pointF.x, pointF.y);
            this.mPath.close();
        }
    }

    private void drawRightArrow(float f, float f2, float f3, float f4, float f5, float f6, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4, PointF pointF5, PointF pointF6, PointF pointF7) {
        this.pA.set(f4, ((f6 - f5) / 2.0f) + f5 + this.mArrowVerticalOffset);
        float f7 = 8.0f * f2;
        this.pB.set(this.pA.x, this.pA.y - f7);
        float f8 = 1.7716f * f2;
        this.pC.set(this.pA.x + (7.1323f * f2), this.pA.y - f8);
        float f9 = 8.2892f * f2;
        float f10 = 0.7613f * f2;
        this.pD.set(this.pA.x + f9, this.pA.y - f10);
        this.pE.set(this.pA.x + f9, this.pA.y + f10);
        this.pF.set(this.pA.x + (7.1326f * f2), this.pA.y + f8);
        this.pG.set(this.pA.x, this.pA.y + f7);
        Path path = this.mPath;
        if (path != null) {
            path.moveTo(pointF.x, pointF.y);
            this.mPath.lineTo(f4 - f, f5);
            this.mPath.quadTo(f4, f5, pointF2.x, pointF2.y);
            this.mPath.lineTo(this.pB.x, this.pB.y);
            this.mPath.lineTo(this.pC.x, this.pC.y);
            this.mPath.cubicTo(this.pD.x, this.pD.y, this.pE.x, this.pE.y, this.pF.x, this.pF.y);
            this.mPath.lineTo(this.pG.x, this.pG.y);
            this.mPath.lineTo(pointF3.x, pointF3.y);
            this.mPath.quadTo(f4, f6, pointF4.x, pointF4.y);
            this.mPath.lineTo(pointF5.x, pointF5.y);
            this.mPath.quadTo(f3, f6, pointF6.x, pointF6.y);
            this.mPath.lineTo(pointF7.x, pointF7.y);
            this.mPath.quadTo(f3, f5, pointF.x, pointF.y);
            this.mPath.close();
        }
    }

    private void drawBottomRightArrow(float f, float f2, float f3, float f4, float f5, float f6, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4, PointF pointF5, PointF pointF6) {
        float f7 = f4 - f;
        this.pA.set(f7, f6);
        this.pB.set(this.pA.x - (2.2988f * f2), f6);
        float f8 = (0.8772f * f2) + f6;
        this.pC.set(this.pA.x - (4.5169f * f2), f8);
        float f9 = (2.4636f * f2) + f6;
        this.pD.set(this.pA.x - (6.2295f * f2), f9);
        float f10 = (8.5073f * f2) + f6;
        this.pE.set(this.pA.x - (12.7541f * f2), f10);
        float f11 = f6 + (9.1642f * f2);
        this.pF.set(this.pA.x - (13.4633f * f2), f11);
        this.pG.set(this.pA.x - (14.5367f * f2), f11);
        this.pH.set(this.pA.x - (15.2459f * f2), f10);
        this.pI.set(this.pA.x - (21.7705f * f2), f9);
        this.pJ.set(this.pA.x - (23.4831f * f2), f8);
        this.pK.set(this.pA.x - (25.7012f * f2), f6);
        this.pL.set(this.pA.x - (28.0f * f2), f6);
        Path path = this.mPath;
        if (path != null) {
            path.moveTo(pointF.x, pointF.y);
            this.mPath.lineTo(f7, f5);
            this.mPath.quadTo(f4, f5, pointF2.x, pointF2.y);
            this.mPath.lineTo(pointF3.x, f6 - f);
            this.mPath.quadTo(f4, f6, this.pA.x, this.pA.y);
            this.mPath.cubicTo(this.pB.x, this.pB.y, this.pC.x, this.pC.y, this.pD.x, this.pD.y);
            this.mPath.lineTo(this.pE.x, this.pE.y);
            this.mPath.cubicTo(this.pF.x, this.pF.y, this.pG.x, this.pG.y, this.pH.x, this.pH.y);
            this.mPath.lineTo(this.pI.x, this.pI.y);
            this.mPath.cubicTo(this.pJ.x, this.pJ.y, this.pK.x, this.pK.y, this.pL.x, this.pL.y);
            this.mPath.lineTo(pointF4.x, pointF4.y);
            this.mPath.quadTo(f3, f6, pointF5.x, pointF5.y);
            this.mPath.lineTo(pointF6.x, pointF6.y);
            this.mPath.quadTo(f3, f5, pointF.x, pointF.y);
            this.mPath.close();
        }
    }

    private void drawBottomArrow(float f, float f2, float f3, float f4, float f5, float f6, float f7, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4, PointF pointF5, PointF pointF6, PointF pointF7) {
        this.middle.set(f / 2.0f, f7);
        this.pA.set(this.middle.x + (14.0f * f3) + this.mArrowHorizonOffset, this.middle.y);
        this.pB.set(this.pA.x - (2.2988f * f3), f7);
        float f8 = (0.8772f * f3) + f7;
        this.pC.set(this.pA.x - (4.5169f * f3), f8);
        float f9 = (2.4636f * f3) + f7;
        this.pD.set(this.pA.x - (6.2295f * f3), f9);
        float f10 = f7 + (8.5073f * f3);
        this.pE.set(this.pA.x - (12.7541f * f3), f10);
        float f11 = f7 + (9.1642f * f3);
        this.pF.set(this.pA.x - (13.4633f * f3), f11);
        this.pG.set(this.pA.x - (14.5367f * f3), f11);
        this.pH.set(this.pA.x - (15.2459f * f3), f10);
        this.pI.set(this.pA.x - (21.7705f * f3), f9);
        this.pJ.set(this.pA.x - (23.4831f * f3), f8);
        this.pK.set(this.pA.x - (25.7012f * f3), f7);
        this.pL.set(this.pA.x - (28.0f * f3), f7);
        Path path = this.mPath;
        if (path != null) {
            path.moveTo(pointF.x, pointF.y);
            this.mPath.lineTo(f5 - f2, f6);
            this.mPath.quadTo(f5, f6, pointF2.x, pointF2.y);
            this.mPath.lineTo(pointF3.x, pointF3.y);
            this.mPath.quadTo(f5, f7, pointF4.x, pointF4.y);
            this.mPath.lineTo(this.pA.x, this.pA.y);
            this.mPath.cubicTo(this.pB.x, this.pB.y, this.pC.x, this.pC.y, this.pD.x, this.pD.y);
            this.mPath.lineTo(this.pE.x, this.pE.y);
            this.mPath.cubicTo(this.pF.x, this.pF.y, this.pG.x, this.pG.y, this.pH.x, this.pH.y);
            this.mPath.lineTo(this.pI.x, this.pI.y);
            this.mPath.cubicTo(this.pJ.x, this.pJ.y, this.pK.x, this.pK.y, this.pL.x, this.pL.y);
            this.mPath.lineTo(pointF5.x, pointF5.y);
            this.mPath.quadTo(f4, f7, pointF6.x, pointF6.y);
            this.mPath.lineTo(pointF7.x, pointF7.y);
            this.mPath.quadTo(f4, f6, pointF.x, pointF.y);
            this.mPath.close();
        }
    }

    private void drawBottomLeftArrow(float f, float f2, float f3, float f4, float f5, float f6, PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4, PointF pointF5) {
        float f7 = 28.0f * f2;
        this.pA.set(f3 + f + f7, f6);
        this.pB.set(this.pA.x - (2.2988f * f2), f6);
        float f8 = (0.8772f * f2) + f6;
        this.pC.set(this.pA.x - (4.5169f * f2), f8);
        float f9 = (2.4636f * f2) + f6;
        this.pD.set(this.pA.x - (6.2295f * f2), f9);
        float f10 = (8.5073f * f2) + f6;
        this.pE.set(this.pA.x - (12.7541f * f2), f10);
        float f11 = f6 + (9.1642f * f2);
        this.pF.set(this.pA.x - (13.4633f * f2), f11);
        this.pG.set(this.pA.x - (14.5367f * f2), f11);
        this.pH.set(this.pA.x - (15.2459f * f2), f10);
        this.pI.set(this.pA.x - (21.7705f * f2), f9);
        this.pJ.set(this.pA.x - (23.4831f * f2), f8);
        this.pK.set(this.pA.x - (25.7012f * f2), f6);
        this.pL.set(this.pA.x - f7, f6);
        Path path = this.mPath;
        if (path != null) {
            path.moveTo(pointF.x, pointF.y);
            this.mPath.lineTo(f4 - f, f5);
            this.mPath.quadTo(f4, f5, pointF2.x, pointF2.y);
            this.mPath.lineTo(pointF3.x, pointF3.y);
            this.mPath.quadTo(f4, f6, pointF4.x, pointF4.y);
            this.mPath.lineTo(this.pA.x, this.pA.y);
            this.mPath.cubicTo(this.pB.x, this.pB.y, this.pC.x, this.pC.y, this.pD.x, this.pD.y);
            this.mPath.lineTo(this.pE.x, this.pE.y);
            this.mPath.cubicTo(this.pF.x, this.pF.y, this.pG.x, this.pG.y, this.pH.x, this.pH.y);
            this.mPath.lineTo(this.pI.x, this.pI.y);
            this.mPath.cubicTo(this.pJ.x, this.pJ.y, this.pK.x, this.pK.y, this.pL.x, this.pL.y);
            this.mPath.quadTo(f3, f6, f3, f6 - f);
            this.mPath.lineTo(pointF5.x, pointF5.y);
            this.mPath.quadTo(f3, f5, pointF.x, pointF.y);
            this.mPath.close();
        }
    }
}
