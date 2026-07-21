package miuix.graphics.shadow;

import android.content.Context;

/* JADX INFO: loaded from: classes2.dex */
public class DropShadowLayerHelper extends DropShadowHelper {
    public DropShadowLayerHelper(Context context, DropShadowConfig dropShadowConfig, boolean z) {
        super(context, dropShadowConfig, z);
    }

    @Override // miuix.graphics.shadow.DropShadowHelper
    protected void onDensityChanged(float f, DropShadowConfig dropShadowConfig) {
        super.onDensityChanged(f, dropShadowConfig);
        this.mShadowPaint.setShadowLayer(this.mBlurRadiusPx, this.mOffsetXPx, this.mOffsetYPx, this.mShadowColor);
    }
}
