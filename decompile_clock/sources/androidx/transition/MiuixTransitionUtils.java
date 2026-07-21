package androidx.transition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;

/* JADX INFO: loaded from: classes.dex */
public class MiuixTransitionUtils {
    private static final boolean HAS_PICTURE_BITMAP;
    private static final int MAX_IMAGE_SIZE = 1048576;

    static {
        HAS_PICTURE_BITMAP = Build.VERSION.SDK_INT >= 28;
    }

    private MiuixTransitionUtils() {
    }

    public static View copyViewImage(ViewGroup viewGroup, View view, View view2) {
        Matrix matrix = new Matrix();
        matrix.setTranslate(-view2.getScrollX(), -view2.getScrollY());
        ViewUtils.transformMatrixToGlobal(view, matrix);
        ViewUtils.transformMatrixToLocal(viewGroup, matrix);
        RectF rectF = new RectF(0.0f, 0.0f, view.getWidth(), view.getHeight());
        matrix.mapRect(rectF);
        int iRound = Math.round(rectF.left);
        int iRound2 = Math.round(rectF.top);
        int iRound3 = Math.round(rectF.right);
        int iRound4 = Math.round(rectF.bottom);
        ImageView imageView = new ImageView(view.getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap bitmapCreateViewBitmap = createViewBitmap(view, matrix, rectF, viewGroup);
        if (bitmapCreateViewBitmap != null) {
            imageView.setImageBitmap(bitmapCreateViewBitmap);
        }
        imageView.measure(View.MeasureSpec.makeMeasureSpec(iRound3 - iRound, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(iRound4 - iRound2, BasicMeasure.EXACTLY));
        imageView.layout(iRound, iRound2, iRound3, iRound4);
        return imageView;
    }

    public static View copyViewImage(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        if (!view.isAttachedToWindow() || width == 0 || height == 0) {
            return null;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmapCreateBitmap));
        Context context = view.getContext();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmapCreateBitmap);
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(bitmapDrawable);
        return imageView;
    }

    public static Bitmap createViewBitmap(View view, Matrix matrix, RectF rectF, ViewGroup viewGroup) {
        int iIndexOfChild;
        ViewGroup viewGroup2;
        boolean z = !view.isAttachedToWindow();
        Bitmap bitmapCreateBitmap = null;
        if (!z) {
            iIndexOfChild = 0;
            viewGroup2 = null;
        } else {
            if (viewGroup == null || !viewGroup.isAttachedToWindow()) {
                return null;
            }
            viewGroup2 = (ViewGroup) view.getParent();
            iIndexOfChild = viewGroup2.indexOfChild(view);
            viewGroup.getOverlay().add(view);
        }
        int iRound = Math.round(rectF.width());
        int iRound2 = Math.round(rectF.height());
        if (iRound > 0 && iRound2 > 0) {
            float fMin = Math.min(1.0f, 1048576.0f / (iRound * iRound2));
            int i = (int) (iRound * fMin);
            int i2 = (int) (iRound2 * fMin);
            matrix.postTranslate(-rectF.left, -rectF.top);
            matrix.postScale(fMin, fMin);
            if (HAS_PICTURE_BITMAP) {
                Picture picture = new Picture();
                Canvas canvasBeginRecording = picture.beginRecording(i, i2);
                canvasBeginRecording.concat(matrix);
                view.draw(canvasBeginRecording);
                picture.endRecording();
                bitmapCreateBitmap = Bitmap.createBitmap(picture);
            } else {
                bitmapCreateBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapCreateBitmap);
                canvas.concat(matrix);
                view.draw(canvas);
            }
        }
        if (z) {
            viewGroup.getOverlay().remove(view);
            if (viewGroup2 != null) {
                viewGroup2.addView(view, iIndexOfChild);
            }
        }
        return bitmapCreateBitmap;
    }
}
