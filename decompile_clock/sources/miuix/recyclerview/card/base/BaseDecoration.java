package miuix.recyclerview.card.base;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes3.dex */
public abstract class BaseDecoration extends RecyclerView.ItemDecoration {
    private static final int LAYER_ALPHA = 255;
    public float[] mAllRadii;
    public int mCardRadius;
    public final Paint mPaint = new Paint(1);
    public final Path mCardPath = new Path();
    public int mCardMarginStart = 0;
    public int mCardMarginEnd = 0;
    public boolean mEnableHyperMaterial = false;

    public abstract void calculateGroupRectAndDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state, RecyclerView.Adapter<?> adapter);

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        super.onDraw(canvas, recyclerView, state);
        calculateGroupRectAndDraw(canvas, recyclerView, state, recyclerView.getAdapter());
    }

    public boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == 1;
    }

    public void drawCardRect(Canvas canvas, RectF rectF, float[] fArr, Path.Direction direction) {
        this.mCardPath.reset();
        this.mCardPath.addRoundRect(rectF, fArr, direction);
        canvas.drawPath(this.mCardPath, this.mPaint);
    }

    public void clipDrawableRoundRect(Canvas canvas, RectF rectF, Path path, Drawable drawable) {
        int iSaveLayerAlpha = canvas.saveLayerAlpha(rectF, 255);
        canvas.clipPath(path);
        drawable.mutate().setBounds(new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        drawable.mutate().draw(canvas);
        canvas.restoreToCount(iSaveLayerAlpha);
    }

    public int findNearViewY(RecyclerView recyclerView, int i, int i2, boolean z) {
        int y;
        int height;
        RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition;
        RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition2;
        if (!z) {
            int i3 = i - 1;
            if (i3 < 0 && (viewHolderFindViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(i)) != null) {
                return viewHolderFindViewHolderForAdapterPosition.itemView.getTop();
            }
            while (i3 >= i2) {
                RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition3 = recyclerView.findViewHolderForAdapterPosition(i3);
                if (viewHolderFindViewHolderForAdapterPosition3 != null) {
                    View view = viewHolderFindViewHolderForAdapterPosition3.itemView;
                    y = (int) view.getY();
                    height = view.getHeight();
                } else {
                    i3--;
                }
            }
            return -1;
        }
        int i4 = i + 1;
        if (i4 < i2 || (viewHolderFindViewHolderForAdapterPosition2 = recyclerView.findViewHolderForAdapterPosition(i)) == null) {
            while (i4 < i2) {
                RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition4 = recyclerView.findViewHolderForAdapterPosition(i4);
                if (viewHolderFindViewHolderForAdapterPosition4 != null) {
                    return (int) viewHolderFindViewHolderForAdapterPosition4.itemView.getY();
                }
                i4++;
            }
            return -1;
        }
        View view2 = viewHolderFindViewHolderForAdapterPosition2.itemView;
        y = view2.getTop();
        height = view2.getHeight();
        return y + height;
    }
}
