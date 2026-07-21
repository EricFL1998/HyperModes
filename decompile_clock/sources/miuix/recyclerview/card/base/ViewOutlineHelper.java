package miuix.recyclerview.card.base;

import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewOutlineProvider;
import androidx.recyclerview.widget.RecyclerView;
import miuix.animation.Folme;

/* JADX INFO: loaded from: classes3.dex */
public class ViewOutlineHelper {
    private static final int GROUP_TYPE_FOOTER = 4;
    private static final int GROUP_TYPE_HEADER = 2;
    private static final int GROUP_TYPE_SINGLE = 1;
    public static final int SET_OUTLINE_DELAY_DURATION = 100;

    public static CardViewOutlineProvider obtainCardViewOutlineProvider(int i, float f) {
        return new CardViewOutlineProvider(i, f);
    }

    public static void setItemCardOutline(RecyclerView.ViewHolder viewHolder, final int i, final float f, boolean z, long j) {
        final View view = viewHolder.itemView;
        if (z) {
            Runnable runnable = new Runnable() { // from class: miuix.recyclerview.card.base.ViewOutlineHelper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ViewOutlineHelper.setOutline(view, i, f);
                }
            };
            if (j <= 0) {
                j = 100;
            }
            view.postDelayed(runnable, j);
            return;
        }
        setOutline(view, i, f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setOutline(View view, int i, float f) {
        if (Folme.isInDraggingState(view)) {
            return;
        }
        if (i == 2 || i == 4 || i == 1) {
            view.setOutlineProvider(obtainCardViewOutlineProvider(i, f));
            view.setClipToOutline(true);
        } else {
            view.setOutlineProvider(null);
            view.setClipToOutline(false);
        }
    }

    public static class CardViewOutlineProvider extends ViewOutlineProvider {
        float radius;
        int type;

        public CardViewOutlineProvider(int i, float f) {
            this.type = i;
            this.radius = f;
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            float[] fArr;
            Path path = new Path();
            RectF rectF = new RectF(0.0f, 0.0f, view.getWidth(), view.getHeight());
            int i = this.type;
            if (i == 2) {
                float f = this.radius;
                fArr = new float[]{f, f, f, f, 0.0f, 0.0f, 0.0f, 0.0f};
            } else if (i == 4) {
                float f2 = this.radius;
                fArr = new float[]{0.0f, 0.0f, 0.0f, 0.0f, f2, f2, f2, f2};
            } else if (i == 1) {
                float f3 = this.radius;
                fArr = new float[]{f3, f3, f3, f3, f3, f3, f3, f3};
            } else {
                fArr = null;
            }
            if (fArr != null) {
                path.addRoundRect(rectF, fArr, Path.Direction.CW);
                outline.setAlpha(0.0f);
                outline.setConvexPath(path);
            }
        }
    }
}
