package miuix.smooth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

/* JADX INFO: loaded from: classes3.dex */
public class SmoothPathProvider {
    private static final float DEFAULT_KSI = 0.46f;
    private static final float DEFAULT_SMOOTH = 0.8f;
    private float mSmooth = DEFAULT_SMOOTH;
    private float mKsi = DEFAULT_KSI;
    private SmoothData mAllData = null;

    private static boolean isHeightCollapsed(float f, float f2, float f3, double d, float f4) {
        return ((double) f) <= ((double) (f2 + f3)) * ((d * ((double) f4)) + 1.0d);
    }

    private static boolean isWidthCollapsed(float f, float f2, float f3, double d, float f4) {
        return ((double) f) <= ((double) (f2 + f3)) * ((d * ((double) f4)) + 1.0d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double radToAngle(double d) {
        return (d * 180.0d) / 3.141592653589793d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double thetaForHeight(double d) {
        return (d * 3.141592653589793d) / 4.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double thetaForWidth(double d) {
        return (d * 3.141592653589793d) / 4.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double yForHeight(double d, double d2) {
        return d * d2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double yForWidth(double d, double d2) {
        return d * d2;
    }

    void setSmooth(float f) {
        this.mSmooth = f;
    }

    float getSmooth() {
        return this.mSmooth;
    }

    void setKsi(float f) {
        this.mKsi = f;
    }

    float getKsi() {
        return this.mKsi;
    }

    public void buildSmoothData(RectF rectF, float f) {
        buildSmoothData(rectF, f, 0.0f, 0.0f);
    }

    public void buildSmoothData(RectF rectF, float f, float f2, float f3) {
        buildSmoothData(rectF, new float[]{f, f, f, f, f, f, f, f}, f2, f3);
    }

    public void buildSmoothData(RectF rectF, float[] fArr) {
        buildSmoothData(rectF, fArr, 0.0f, 0.0f);
    }

    public void buildSmoothData(RectF rectF, float[] fArr, float f, float f2) {
        float f3;
        float f4;
        float f5;
        float f6;
        float ksi = getKsi();
        float smooth = getSmooth();
        float fWidth = rectF.width();
        float fHeight = rectF.height();
        double d = smooth;
        this.mAllData = new SmoothData(fWidth, fHeight, d, ksi);
        if (fArr == null) {
            return;
        }
        float[] fArr2 = new float[8];
        fArr2[0] = 0.0f;
        fArr2[1] = 0.0f;
        fArr2[2] = 0.0f;
        fArr2[3] = 0.0f;
        fArr2[4] = 0.0f;
        fArr2[5] = 0.0f;
        fArr2[6] = 0.0f;
        fArr2[7] = 0.0f;
        for (int i = 0; i < Math.min(8, fArr.length); i++) {
            if (!Float.isNaN(fArr[i])) {
                fArr2[i] = fArr[i];
            }
        }
        float f7 = fArr2[0];
        float f8 = fArr2[1];
        float f9 = fArr2[2];
        float f10 = fArr2[3];
        float f11 = fArr2[4];
        float f12 = fArr2[5];
        float f13 = fArr2[6];
        float f14 = fArr2[7];
        if (f7 + f9 > fWidth) {
            float f15 = (fWidth * f7) / (f7 + f9);
            f9 = (fWidth * f9) / (f7 + f9);
            f7 = f15;
        }
        float f16 = f9;
        if (f10 + f12 > fHeight) {
            float f17 = (fHeight * f10) / (f10 + f12);
            f12 = (fHeight * f12) / (f10 + f12);
            f3 = f17;
        } else {
            f3 = f10;
        }
        if (f11 + f13 > fWidth) {
            float f18 = (fWidth * f11) / (f11 + f13);
            f4 = (fWidth * f13) / (f11 + f13);
            f5 = f18;
        } else {
            f4 = f13;
            f5 = f11;
        }
        if (f14 + f8 > fHeight) {
            float f19 = (fHeight * f14) / (f14 + f8);
            f8 = (fHeight * f8) / (f14 + f8);
            f6 = f19;
        } else {
            f6 = f14;
        }
        ensureFourCornerData();
        this.mAllData.topLeft.build(Math.min(f7, f8), rectF, f, f2, d, ksi, 0);
        this.mAllData.topRight.build(Math.min(f16, f3), rectF, f, f2, d, ksi, 1);
        this.mAllData.bottomRight.build(Math.min(f5, f12), rectF, f, f2, d, ksi, 2);
        this.mAllData.bottomLeft.build(Math.min(f4, f6), rectF, f, f2, d, ksi, 3);
    }

    private void ensureFourCornerData() {
        if (this.mAllData.topLeft == null) {
            this.mAllData.topLeft = new CornerData();
        }
        if (this.mAllData.topRight == null) {
            this.mAllData.topRight = new CornerData();
        }
        if (this.mAllData.bottomRight == null) {
            this.mAllData.bottomRight = new CornerData();
        }
        if (this.mAllData.bottomLeft == null) {
            this.mAllData.bottomLeft = new CornerData();
        }
    }

    public void drawPath(Canvas canvas, Paint paint, int i, int i2, int i3) {
        if (isFourCornerDataValid()) {
            paint.setColor(i);
            canvas.drawRect(new RectF(0.0f, 0.0f, this.mAllData.width, this.mAllData.height), paint);
            return;
        }
        PointF pointF = new PointF();
        paint.setColor(i2);
        canvas.drawArc(this.mAllData.topLeft.rect, (float) radToAngle(this.mAllData.topLeft.thetaForVertical + 3.141592653589793d), this.mAllData.topLeft.swapAngle, false, paint);
        pointF.x = this.mAllData.topLeft.bezierAnchorHorizontal[0].x;
        pointF.y = this.mAllData.topLeft.bezierAnchorHorizontal[0].y;
        if (this.mAllData.topLeft.smoothForHorizontal != 0.0d) {
            Path path = new Path();
            path.moveTo(pointF.x, pointF.y);
            path.cubicTo(this.mAllData.topLeft.bezierAnchorHorizontal[1].x, this.mAllData.topLeft.bezierAnchorHorizontal[1].y, this.mAllData.topLeft.bezierAnchorHorizontal[2].x, this.mAllData.topLeft.bezierAnchorHorizontal[2].y, this.mAllData.topLeft.bezierAnchorHorizontal[3].x, this.mAllData.topLeft.bezierAnchorHorizontal[3].y);
            paint.setColor(i3);
            canvas.drawPath(path, paint);
            pointF.x = this.mAllData.topLeft.bezierAnchorHorizontal[3].x;
            pointF.y = this.mAllData.topLeft.bezierAnchorHorizontal[3].y;
        }
        if (!isWidthCollapsed(this.mAllData.width, this.mAllData.topLeft.radius, this.mAllData.topRight.radius, this.mAllData.smooth, this.mAllData.ksi)) {
            paint.setColor(i);
            canvas.drawLine(pointF.x, pointF.y, this.mAllData.topRight.bezierAnchorHorizontal[0].x, this.mAllData.topRight.bezierAnchorHorizontal[0].y, paint);
            pointF.x = this.mAllData.topRight.bezierAnchorHorizontal[0].x;
            pointF.y = this.mAllData.topRight.bezierAnchorHorizontal[0].y;
        }
        if (this.mAllData.topRight.smoothForHorizontal != 0.0d) {
            Path path2 = new Path();
            path2.moveTo(pointF.x, pointF.y);
            path2.cubicTo(this.mAllData.topRight.bezierAnchorHorizontal[1].x, this.mAllData.topRight.bezierAnchorHorizontal[1].y, this.mAllData.topRight.bezierAnchorHorizontal[2].x, this.mAllData.topRight.bezierAnchorHorizontal[2].y, this.mAllData.topRight.bezierAnchorHorizontal[3].x, this.mAllData.topRight.bezierAnchorHorizontal[3].y);
            paint.setColor(i3);
            canvas.drawPath(path2, paint);
            pointF.x = this.mAllData.topRight.bezierAnchorHorizontal[3].x;
            pointF.y = this.mAllData.topRight.bezierAnchorHorizontal[3].y;
        }
        paint.setColor(i2);
        canvas.drawArc(this.mAllData.topRight.rect, (float) radToAngle(this.mAllData.topRight.thetaForHorizontal + 4.71238898038469d), this.mAllData.topRight.swapAngle, false, paint);
        pointF.x = this.mAllData.topRight.bezierAnchorVertical[0].x;
        pointF.y = this.mAllData.topRight.bezierAnchorVertical[0].y;
        if (this.mAllData.topRight.smoothForVertical != 0.0d) {
            Path path3 = new Path();
            path3.moveTo(pointF.x, pointF.y);
            path3.cubicTo(this.mAllData.topRight.bezierAnchorVertical[1].x, this.mAllData.topRight.bezierAnchorVertical[1].y, this.mAllData.topRight.bezierAnchorVertical[2].x, this.mAllData.topRight.bezierAnchorVertical[2].y, this.mAllData.topRight.bezierAnchorVertical[3].x, this.mAllData.topRight.bezierAnchorVertical[3].y);
            paint.setColor(i3);
            canvas.drawPath(path3, paint);
            pointF.x = this.mAllData.topRight.bezierAnchorVertical[3].x;
            pointF.y = this.mAllData.topRight.bezierAnchorVertical[3].y;
        }
        if (!isHeightCollapsed(this.mAllData.height, this.mAllData.topRight.radius, this.mAllData.bottomRight.radius, this.mAllData.smooth, this.mAllData.ksi)) {
            paint.setColor(i);
            canvas.drawLine(pointF.x, pointF.y, this.mAllData.bottomRight.bezierAnchorVertical[0].x, this.mAllData.bottomRight.bezierAnchorVertical[0].y, paint);
            pointF.x = this.mAllData.bottomRight.bezierAnchorVertical[0].x;
            pointF.y = this.mAllData.bottomRight.bezierAnchorVertical[0].y;
        }
        if (this.mAllData.bottomRight.smoothForVertical != 0.0d) {
            Path path4 = new Path();
            path4.moveTo(pointF.x, pointF.y);
            path4.cubicTo(this.mAllData.bottomRight.bezierAnchorVertical[1].x, this.mAllData.bottomRight.bezierAnchorVertical[1].y, this.mAllData.bottomRight.bezierAnchorVertical[2].x, this.mAllData.bottomRight.bezierAnchorVertical[2].y, this.mAllData.bottomRight.bezierAnchorVertical[3].x, this.mAllData.bottomRight.bezierAnchorVertical[3].y);
            paint.setColor(i3);
            canvas.drawPath(path4, paint);
            pointF.x = this.mAllData.bottomRight.bezierAnchorVertical[3].x;
            pointF.y = this.mAllData.bottomRight.bezierAnchorVertical[3].y;
        }
        paint.setColor(i2);
        canvas.drawArc(this.mAllData.bottomRight.rect, (float) radToAngle(this.mAllData.bottomRight.thetaForVertical), this.mAllData.bottomRight.swapAngle, false, paint);
        pointF.x = this.mAllData.bottomRight.bezierAnchorHorizontal[0].x;
        pointF.y = this.mAllData.bottomRight.bezierAnchorHorizontal[0].y;
        if (this.mAllData.bottomRight.smoothForHorizontal != 0.0d) {
            Path path5 = new Path();
            path5.moveTo(pointF.x, pointF.y);
            path5.cubicTo(this.mAllData.bottomRight.bezierAnchorHorizontal[1].x, this.mAllData.bottomRight.bezierAnchorHorizontal[1].y, this.mAllData.bottomRight.bezierAnchorHorizontal[2].x, this.mAllData.bottomRight.bezierAnchorHorizontal[2].y, this.mAllData.bottomRight.bezierAnchorHorizontal[3].x, this.mAllData.bottomRight.bezierAnchorHorizontal[3].y);
            paint.setColor(i3);
            canvas.drawPath(path5, paint);
            pointF.x = this.mAllData.bottomRight.bezierAnchorHorizontal[3].x;
            pointF.y = this.mAllData.bottomRight.bezierAnchorHorizontal[3].y;
        }
        if (!isWidthCollapsed(this.mAllData.width, this.mAllData.bottomRight.radius, this.mAllData.bottomLeft.radius, this.mAllData.smooth, this.mAllData.ksi)) {
            paint.setColor(i);
            canvas.drawLine(pointF.x, pointF.y, this.mAllData.bottomLeft.bezierAnchorHorizontal[0].x, this.mAllData.bottomLeft.bezierAnchorHorizontal[0].y, paint);
            pointF.x = this.mAllData.bottomLeft.bezierAnchorHorizontal[0].x;
            pointF.y = this.mAllData.bottomLeft.bezierAnchorHorizontal[0].y;
        }
        if (this.mAllData.bottomLeft.smoothForHorizontal != 0.0d) {
            Path path6 = new Path();
            path6.moveTo(pointF.x, pointF.y);
            path6.cubicTo(this.mAllData.bottomLeft.bezierAnchorHorizontal[1].x, this.mAllData.bottomLeft.bezierAnchorHorizontal[1].y, this.mAllData.bottomLeft.bezierAnchorHorizontal[2].x, this.mAllData.bottomLeft.bezierAnchorHorizontal[2].y, this.mAllData.bottomLeft.bezierAnchorHorizontal[3].x, this.mAllData.bottomLeft.bezierAnchorHorizontal[3].y);
            paint.setColor(i3);
            canvas.drawPath(path6, paint);
            pointF.x = this.mAllData.bottomLeft.bezierAnchorHorizontal[3].x;
            pointF.y = this.mAllData.bottomLeft.bezierAnchorHorizontal[3].y;
        }
        paint.setColor(i2);
        canvas.drawArc(this.mAllData.bottomLeft.rect, (float) radToAngle(this.mAllData.bottomLeft.thetaForHorizontal + 1.5707963267948966d), this.mAllData.bottomLeft.swapAngle, false, paint);
        pointF.x = this.mAllData.bottomLeft.bezierAnchorVertical[0].x;
        pointF.y = this.mAllData.bottomLeft.bezierAnchorVertical[0].y;
        if (this.mAllData.bottomLeft.smoothForVertical != 0.0d) {
            Path path7 = new Path();
            path7.moveTo(pointF.x, pointF.y);
            path7.cubicTo(this.mAllData.bottomLeft.bezierAnchorVertical[1].x, this.mAllData.bottomLeft.bezierAnchorVertical[1].y, this.mAllData.bottomLeft.bezierAnchorVertical[2].x, this.mAllData.bottomLeft.bezierAnchorVertical[2].y, this.mAllData.bottomLeft.bezierAnchorVertical[3].x, this.mAllData.bottomLeft.bezierAnchorVertical[3].y);
            paint.setColor(i3);
            canvas.drawPath(path7, paint);
            pointF.x = this.mAllData.bottomLeft.bezierAnchorVertical[3].x;
            pointF.y = this.mAllData.bottomLeft.bezierAnchorVertical[3].y;
        }
        if (!isHeightCollapsed(this.mAllData.height, this.mAllData.bottomLeft.radius, this.mAllData.topLeft.radius, this.mAllData.smooth, this.mAllData.ksi)) {
            paint.setColor(i);
            canvas.drawLine(pointF.x, pointF.y, this.mAllData.topLeft.bezierAnchorVertical[0].x, this.mAllData.topLeft.bezierAnchorVertical[0].y, paint);
            pointF.x = this.mAllData.topLeft.bezierAnchorVertical[0].x;
            pointF.y = this.mAllData.topLeft.bezierAnchorVertical[0].y;
        }
        if (this.mAllData.topLeft.smoothForVertical != 0.0d) {
            Path path8 = new Path();
            path8.moveTo(pointF.x, pointF.y);
            path8.cubicTo(this.mAllData.topLeft.bezierAnchorVertical[1].x, this.mAllData.topLeft.bezierAnchorVertical[1].y, this.mAllData.topLeft.bezierAnchorVertical[2].x, this.mAllData.topLeft.bezierAnchorVertical[2].y, this.mAllData.topLeft.bezierAnchorVertical[3].x, this.mAllData.topLeft.bezierAnchorVertical[3].y);
            paint.setColor(i3);
            canvas.drawPath(path8, paint);
            pointF.x = this.mAllData.topLeft.bezierAnchorVertical[3].x;
            pointF.y = this.mAllData.topLeft.bezierAnchorVertical[3].y;
        }
    }

    public Path getSmoothPath(Path path) {
        if (path == null) {
            path = new Path();
        }
        path.reset();
        if (isFourCornerDataValid()) {
            path.addRect(new RectF(0.0f, 0.0f, this.mAllData.width, this.mAllData.height), Path.Direction.CCW);
            return path;
        }
        if (this.mAllData.topLeft.swapAngle != 0.0f) {
            path.arcTo(this.mAllData.topLeft.rect, (float) radToAngle(this.mAllData.topLeft.thetaForVertical + 3.141592653589793d), this.mAllData.topLeft.swapAngle);
        } else {
            path.moveTo(this.mAllData.topLeft.bezierAnchorHorizontal[0].x, this.mAllData.topLeft.bezierAnchorHorizontal[0].y);
        }
        if (this.mAllData.topLeft.smoothForHorizontal != 0.0d) {
            path.cubicTo(this.mAllData.topLeft.bezierAnchorHorizontal[1].x, this.mAllData.topLeft.bezierAnchorHorizontal[1].y, this.mAllData.topLeft.bezierAnchorHorizontal[2].x, this.mAllData.topLeft.bezierAnchorHorizontal[2].y, this.mAllData.topLeft.bezierAnchorHorizontal[3].x, this.mAllData.topLeft.bezierAnchorHorizontal[3].y);
        }
        if (!isWidthCollapsed(this.mAllData.width, this.mAllData.topLeft.radius, this.mAllData.topRight.radius, this.mAllData.smooth, this.mAllData.ksi)) {
            path.lineTo(this.mAllData.topRight.bezierAnchorHorizontal[0].x, this.mAllData.topRight.bezierAnchorHorizontal[0].y);
        }
        if (this.mAllData.topRight.smoothForHorizontal != 0.0d) {
            path.cubicTo(this.mAllData.topRight.bezierAnchorHorizontal[1].x, this.mAllData.topRight.bezierAnchorHorizontal[1].y, this.mAllData.topRight.bezierAnchorHorizontal[2].x, this.mAllData.topRight.bezierAnchorHorizontal[2].y, this.mAllData.topRight.bezierAnchorHorizontal[3].x, this.mAllData.topRight.bezierAnchorHorizontal[3].y);
        }
        if (this.mAllData.topRight.swapAngle != 0.0f) {
            path.arcTo(this.mAllData.topRight.rect, (float) radToAngle(this.mAllData.topRight.thetaForHorizontal + 4.71238898038469d), this.mAllData.topRight.swapAngle);
        }
        if (this.mAllData.topRight.smoothForVertical != 0.0d) {
            path.cubicTo(this.mAllData.topRight.bezierAnchorVertical[1].x, this.mAllData.topRight.bezierAnchorVertical[1].y, this.mAllData.topRight.bezierAnchorVertical[2].x, this.mAllData.topRight.bezierAnchorVertical[2].y, this.mAllData.topRight.bezierAnchorVertical[3].x, this.mAllData.topRight.bezierAnchorVertical[3].y);
        }
        if (!isHeightCollapsed(this.mAllData.height, this.mAllData.topRight.radius, this.mAllData.bottomRight.radius, this.mAllData.smooth, this.mAllData.ksi)) {
            path.lineTo(this.mAllData.bottomRight.bezierAnchorVertical[0].x, this.mAllData.bottomRight.bezierAnchorVertical[0].y);
        }
        if (this.mAllData.bottomRight.smoothForVertical != 0.0d) {
            path.cubicTo(this.mAllData.bottomRight.bezierAnchorVertical[1].x, this.mAllData.bottomRight.bezierAnchorVertical[1].y, this.mAllData.bottomRight.bezierAnchorVertical[2].x, this.mAllData.bottomRight.bezierAnchorVertical[2].y, this.mAllData.bottomRight.bezierAnchorVertical[3].x, this.mAllData.bottomRight.bezierAnchorVertical[3].y);
        }
        if (this.mAllData.bottomRight.swapAngle != 0.0f) {
            path.arcTo(this.mAllData.bottomRight.rect, (float) radToAngle(this.mAllData.bottomRight.thetaForVertical), this.mAllData.bottomRight.swapAngle);
        }
        if (this.mAllData.bottomRight.smoothForHorizontal != 0.0d) {
            path.cubicTo(this.mAllData.bottomRight.bezierAnchorHorizontal[1].x, this.mAllData.bottomRight.bezierAnchorHorizontal[1].y, this.mAllData.bottomRight.bezierAnchorHorizontal[2].x, this.mAllData.bottomRight.bezierAnchorHorizontal[2].y, this.mAllData.bottomRight.bezierAnchorHorizontal[3].x, this.mAllData.bottomRight.bezierAnchorHorizontal[3].y);
        }
        if (!isWidthCollapsed(this.mAllData.width, this.mAllData.bottomRight.radius, this.mAllData.bottomLeft.radius, this.mAllData.smooth, this.mAllData.ksi)) {
            path.lineTo(this.mAllData.bottomLeft.bezierAnchorHorizontal[0].x, this.mAllData.bottomLeft.bezierAnchorHorizontal[0].y);
        }
        if (this.mAllData.bottomLeft.smoothForHorizontal != 0.0d) {
            path.cubicTo(this.mAllData.bottomLeft.bezierAnchorHorizontal[1].x, this.mAllData.bottomLeft.bezierAnchorHorizontal[1].y, this.mAllData.bottomLeft.bezierAnchorHorizontal[2].x, this.mAllData.bottomLeft.bezierAnchorHorizontal[2].y, this.mAllData.bottomLeft.bezierAnchorHorizontal[3].x, this.mAllData.bottomLeft.bezierAnchorHorizontal[3].y);
        }
        if (this.mAllData.bottomLeft.swapAngle != 0.0f) {
            path.arcTo(this.mAllData.bottomLeft.rect, (float) radToAngle(this.mAllData.bottomLeft.thetaForHorizontal + 1.5707963267948966d), this.mAllData.bottomLeft.swapAngle);
        }
        if (this.mAllData.bottomLeft.smoothForVertical != 0.0d) {
            path.cubicTo(this.mAllData.bottomLeft.bezierAnchorVertical[1].x, this.mAllData.bottomLeft.bezierAnchorVertical[1].y, this.mAllData.bottomLeft.bezierAnchorVertical[2].x, this.mAllData.bottomLeft.bezierAnchorVertical[2].y, this.mAllData.bottomLeft.bezierAnchorVertical[3].x, this.mAllData.bottomLeft.bezierAnchorVertical[3].y);
        }
        if (!isHeightCollapsed(this.mAllData.height, this.mAllData.bottomLeft.radius, this.mAllData.topLeft.radius, this.mAllData.smooth, this.mAllData.ksi)) {
            path.lineTo(this.mAllData.topLeft.bezierAnchorVertical[0].x, this.mAllData.topLeft.bezierAnchorVertical[0].y);
        }
        if (this.mAllData.topLeft.smoothForVertical != 0.0d) {
            path.cubicTo(this.mAllData.topLeft.bezierAnchorVertical[1].x, this.mAllData.topLeft.bezierAnchorVertical[1].y, this.mAllData.topLeft.bezierAnchorVertical[2].x, this.mAllData.topLeft.bezierAnchorVertical[2].y, this.mAllData.topLeft.bezierAnchorVertical[3].x, this.mAllData.topLeft.bezierAnchorVertical[3].y);
        }
        path.close();
        return path;
    }

    private boolean isFourCornerDataValid() {
        return this.mAllData.topLeft == null || this.mAllData.topRight == null || this.mAllData.bottomRight == null || this.mAllData.bottomLeft == null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double smoothForWidth(float f, float f2, double d, float f3) {
        return isWidthCollapsed(f, f2, f2, d, f3) ? Math.max(Math.min(((f / (f2 * 2.0f)) - 1.0f) / f3, 1.0f), 0.0f) : d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double smoothForHeight(float f, float f2, double d, float f3) {
        return isHeightCollapsed(f, f2, f2, d, f3) ? Math.max(Math.min(((f / (f2 * 2.0f)) - 1.0f) / f3, 1.0f), 0.0f) : d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double mForHeight(float f, double d) {
        return ((double) f) * (1.0d - Math.cos(d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double nForHeight(float f, double d) {
        return ((double) f) * (1.0d - Math.sin(d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double mForWidth(float f, double d) {
        return ((double) f) * (1.0d - Math.sin(d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double nForWidth(float f, double d) {
        return ((double) f) * (1.0d - Math.cos(d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double pForWidth(float f, double d) {
        return ((double) f) * (1.0d - Math.tan(d / 2.0d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double pForHeight(float f, double d) {
        return ((double) f) * (1.0d - Math.tan(d / 2.0d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double xForWidth(float f, double d) {
        return ((((double) f) * 1.5d) * Math.tan(d / 2.0d)) / (Math.cos(d) + 1.0d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double xForHeight(float f, double d) {
        return ((((double) f) * 1.5d) * Math.tan(d / 2.0d)) / (Math.cos(d) + 1.0d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double kForWidth(double d, double d2) {
        if (d2 == 0.0d) {
            return 0.0d;
        }
        double d3 = d2 / 2.0d;
        return (((((d * 0.46000000834465027d) + Math.tan(d3)) * 2.0d) * (Math.cos(d2) + 1.0d)) / (Math.tan(d3) * 3.0d)) - 1.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double kForHeight(double d, double d2) {
        if (d2 == 0.0d) {
            return 0.0d;
        }
        double d3 = d2 / 2.0d;
        return (((((d * 0.46000000834465027d) + Math.tan(d3)) * 2.0d) * (Math.cos(d2) + 1.0d)) / (Math.tan(d3) * 3.0d)) - 1.0d;
    }

    public static class CornerData {
        public static final int BOTTOM_LEFT = 3;
        public static final int BOTTOM_RIGHT = 2;
        public static final int TOP_LEFT = 0;
        public static final int TOP_RIGHT = 1;
        public PointF[] bezierAnchorHorizontal = new PointF[4];
        public PointF[] bezierAnchorVertical = new PointF[4];
        public float radius;
        public RectF rect;
        public double smoothForHorizontal;
        public double smoothForVertical;
        public float swapAngle;
        public double thetaForHorizontal;
        public double thetaForVertical;

        public void build(float f, RectF rectF, float f2, float f3, double d, float f4, int i) {
            this.radius = f;
            float fWidth = rectF.width();
            float fHeight = rectF.height();
            float f5 = rectF.left;
            float f6 = rectF.top;
            float f7 = rectF.right;
            float f8 = rectF.bottom;
            this.smoothForHorizontal = SmoothPathProvider.smoothForWidth(fWidth, this.radius, d, f4);
            this.smoothForVertical = SmoothPathProvider.smoothForHeight(fHeight, this.radius, d, f4);
            this.thetaForHorizontal = SmoothPathProvider.thetaForWidth(this.smoothForHorizontal);
            double dThetaForHeight = SmoothPathProvider.thetaForHeight(this.smoothForVertical);
            this.thetaForVertical = dThetaForHeight;
            this.swapAngle = (float) SmoothPathProvider.radToAngle((1.5707963267948966d - dThetaForHeight) - this.thetaForHorizontal);
            double d2 = f4;
            double dKForWidth = SmoothPathProvider.kForWidth(this.smoothForHorizontal * d2, this.thetaForHorizontal);
            double dMForWidth = SmoothPathProvider.mForWidth(this.radius, this.thetaForHorizontal);
            double dNForWidth = SmoothPathProvider.nForWidth(this.radius, this.thetaForHorizontal);
            double dPForWidth = SmoothPathProvider.pForWidth(this.radius, this.thetaForHorizontal);
            double dXForWidth = SmoothPathProvider.xForWidth(this.radius, this.thetaForHorizontal);
            double dYForWidth = SmoothPathProvider.yForWidth(dKForWidth, dXForWidth);
            double dKForHeight = SmoothPathProvider.kForHeight(this.smoothForVertical * d2, this.thetaForVertical);
            double dMForHeight = SmoothPathProvider.mForHeight(this.radius, this.thetaForVertical);
            double dNForHeight = SmoothPathProvider.nForHeight(this.radius, this.thetaForVertical);
            double dPForHeight = SmoothPathProvider.pForHeight(this.radius, this.thetaForVertical);
            double dXForHeight = SmoothPathProvider.xForHeight(this.radius, this.thetaForVertical);
            double dYForHeight = SmoothPathProvider.yForHeight(dKForHeight, dXForHeight);
            if (i == 0) {
                float f9 = f5 + f2;
                float f10 = f6 + f3;
                float f11 = this.radius;
                this.rect = new RectF(f9, f10, (f11 * 2.0f) + f9, (f11 * 2.0f) + f10);
                double d3 = f9;
                double d4 = f10;
                this.bezierAnchorHorizontal[0] = new PointF((float) (dMForWidth + d3), (float) (dNForWidth + d4));
                this.bezierAnchorHorizontal[1] = new PointF((float) (dPForWidth + d3), f10);
                double d5 = dPForWidth + dXForWidth;
                this.bezierAnchorHorizontal[2] = new PointF((float) (d5 + d3), f10);
                this.bezierAnchorHorizontal[3] = new PointF((float) (d5 + dYForWidth + d3), f10);
                double d6 = dXForHeight + dPForHeight;
                this.bezierAnchorVertical[0] = new PointF(f9, (float) (d6 + dYForHeight + d4));
                this.bezierAnchorVertical[1] = new PointF(f9, (float) (d6 + d4));
                this.bezierAnchorVertical[2] = new PointF(f9, (float) (dPForHeight + d4));
                this.bezierAnchorVertical[3] = new PointF((float) (dMForHeight + d3), (float) (dNForHeight + d4));
                return;
            }
            if (i == 1) {
                float f12 = f6 + f3;
                float f13 = this.radius;
                float f14 = f7 - f2;
                this.rect = new RectF((f7 - (f13 * 2.0f)) - f2, f12, f14, (f13 * 2.0f) + f12);
                double d7 = f7;
                double d8 = d7 - dPForWidth;
                double d9 = d8 - dXForWidth;
                double d10 = f2;
                this.bezierAnchorHorizontal[0] = new PointF((float) ((d9 - dYForWidth) - d10), f12);
                this.bezierAnchorHorizontal[1] = new PointF((float) (d9 - d10), f12);
                this.bezierAnchorHorizontal[2] = new PointF((float) (d8 - d10), f12);
                double d11 = f12;
                this.bezierAnchorHorizontal[3] = new PointF((float) ((d7 - dMForWidth) - d10), (float) (dNForWidth + d11));
                this.bezierAnchorVertical[0] = new PointF((float) ((d7 - dMForHeight) - d10), (float) (dNForHeight + d11));
                this.bezierAnchorVertical[1] = new PointF(f14, (float) (dPForHeight + d11));
                double d12 = dPForHeight + dXForHeight;
                this.bezierAnchorVertical[2] = new PointF(f14, (float) (d12 + d11));
                this.bezierAnchorVertical[3] = new PointF(f14, (float) (d12 + dYForHeight + d11));
                return;
            }
            if (i == 2) {
                float f15 = this.radius;
                float f16 = f7 - f2;
                float f17 = f8 - f3;
                this.rect = new RectF((f7 - (f15 * 2.0f)) - f2, (f8 - (f15 * 2.0f)) - f3, f16, f17);
                double d13 = f7;
                double d14 = f2;
                double d15 = f8;
                double d16 = f3;
                this.bezierAnchorHorizontal[0] = new PointF((float) ((d13 - dMForWidth) - d14), (float) ((d15 - dNForWidth) - d16));
                double d17 = d13 - dPForWidth;
                this.bezierAnchorHorizontal[1] = new PointF((float) (d17 - d14), f17);
                double d18 = d17 - dXForWidth;
                this.bezierAnchorHorizontal[2] = new PointF((float) (d18 - d14), f17);
                this.bezierAnchorHorizontal[3] = new PointF((float) ((d18 - dYForWidth) - d14), f17);
                double d19 = d15 - dPForHeight;
                double d20 = d19 - dXForHeight;
                this.bezierAnchorVertical[0] = new PointF(f16, (float) ((d20 - dYForHeight) - d16));
                this.bezierAnchorVertical[1] = new PointF(f16, (float) (d20 - d16));
                this.bezierAnchorVertical[2] = new PointF(f16, (float) (d19 - d16));
                this.bezierAnchorVertical[3] = new PointF((float) ((d13 - dMForHeight) - d14), (float) ((d15 - dNForHeight) - d16));
                return;
            }
            if (i == 3) {
                float f18 = f5 + f2;
                float f19 = this.radius;
                float f20 = f8 - f3;
                this.rect = new RectF(f18, (f8 - (f19 * 2.0f)) - f3, (f19 * 2.0f) + f18, f20);
                double d21 = dPForWidth + dXForWidth;
                double d22 = f18;
                this.bezierAnchorHorizontal[0] = new PointF((float) (d21 + dYForWidth + d22), f20);
                this.bezierAnchorHorizontal[1] = new PointF((float) (d21 + d22), f20);
                this.bezierAnchorHorizontal[2] = new PointF((float) (dPForWidth + d22), f20);
                float f21 = (float) (dMForWidth + d22);
                double d23 = f8;
                double d24 = f3;
                this.bezierAnchorHorizontal[3] = new PointF(f21, (float) ((d23 - dNForWidth) - d24));
                this.bezierAnchorVertical[0] = new PointF((float) (dMForHeight + d22), (float) ((d23 - dNForHeight) - d24));
                double d25 = d23 - dPForHeight;
                this.bezierAnchorVertical[1] = new PointF(f18, (float) (d25 - d24));
                double d26 = d25 - dXForHeight;
                this.bezierAnchorVertical[2] = new PointF(f18, (float) (d26 - d24));
                this.bezierAnchorVertical[3] = new PointF(f18, (float) ((d26 - dYForHeight) - d24));
            }
        }
    }

    public static class SmoothData {
        public float height;
        public float ksi;
        public double smooth;
        public float width;
        public CornerData topLeft = null;
        public CornerData topRight = null;
        public CornerData bottomRight = null;
        public CornerData bottomLeft = null;

        public SmoothData(float f, float f2, double d, float f3) {
            this.width = f;
            this.height = f2;
            this.smooth = d;
            this.ksi = f3;
        }
    }
}
