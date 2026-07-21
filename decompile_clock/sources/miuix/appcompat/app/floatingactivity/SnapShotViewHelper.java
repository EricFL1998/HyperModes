package miuix.appcompat.app.floatingactivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.FrameLayout;
import miuix.appcompat.R;
import miuix.appcompat.app.AppCompatActivity;
import miuix.graphics.BitmapFactory;
import miuix.internal.widget.RoundFrameLayout;

/* JADX INFO: loaded from: classes2.dex */
public class SnapShotViewHelper {
    private static float mRadius;

    private static void checkRadius(Context context) {
        if (mRadius == 0.0f) {
            mRadius = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_floating_window_background_radius);
        }
    }

    public static Bitmap getSnapShot(View view) {
        if (!checkViewParamsAvailable(view)) {
            return null;
        }
        checkRadius(view.getContext());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();
        if (drawingCache == null) {
            return null;
        }
        Bitmap roundBitmap = BitmapFactory.getRoundBitmap(drawingCache, mRadius);
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return roundBitmap;
    }

    public static View generateSnapShotView(Context context, AppCompatActivity appCompatActivity) {
        View floatingBrightPanel = appCompatActivity.getFloatingBrightPanel();
        return generateSnapShotView(context, floatingBrightPanel, getSnapShot(floatingBrightPanel));
    }

    public static View generateSnapShotView(View view, Bitmap bitmap) {
        return generateSnapShotView(view.getContext(), view, bitmap);
    }

    public static View generateSnapShotView(Context context, View view, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        RoundFrameLayout roundFrameLayout = new RoundFrameLayout(context);
        roundFrameLayout.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.layout(0, 0, bitmap.getWidth(), bitmap.getHeight());
        frameLayout.setBackground(new BitmapDrawable(context.getResources(), bitmap));
        roundFrameLayout.addView(frameLayout);
        return roundFrameLayout;
    }

    private static boolean checkViewParamsAvailable(View view) {
        return (view == null || view.getContext() == null) ? false : true;
    }
}
