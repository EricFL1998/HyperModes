package miuix.smooth;

import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

/* JADX INFO: loaded from: classes3.dex */
public class SmoothContainerDrawableForCardView extends SmoothContainerDrawable2 {
    private RectF rectF = new RectF();
    private Path path = new Path();

    @Override // miuix.smooth.SmoothContainerDrawable2, android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        if (Build.VERSION.SDK_INT >= 30) {
            this.path.reset();
            Rect boundsInner = getBoundsInner();
            this.rectF.left = boundsInner.left;
            this.rectF.top = boundsInner.top;
            this.rectF.right = boundsInner.right;
            this.rectF.bottom = boundsInner.bottom;
            this.path.addRoundRect(this.rectF, getCornerRadius(), getCornerRadius(), Path.Direction.CW);
            outline.setPath(this.path);
            return;
        }
        outline.setRoundRect(getBoundsInner(), getCornerRadius());
    }
}
