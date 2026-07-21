package miuix.graphics.shadow;

import android.content.Context;
import android.graphics.BlurMaskFilter;

/* JADX INFO: loaded from: classes2.dex */
public class DropShadowMaskHelper extends DropShadowHelper {
    private BlurMaskFilter mBlurMaskFilter;

    public DropShadowMaskHelper(Context context, DropShadowConfig dropShadowConfig, boolean z) {
        super(context, dropShadowConfig, z);
    }

    @Override // miuix.graphics.shadow.DropShadowHelper
    public void updateShadowRect(int i, int i2, int i3, int i4) {
        super.updateShadowRect(i, i2, i3, i4);
        this.mShadowRect.offset(this.mOffsetXPx, this.mOffsetYPx);
    }

    @Override // miuix.graphics.shadow.DropShadowHelper
    protected void onDensityChanged(float f, DropShadowConfig dropShadowConfig) {
        super.onDensityChanged(f, dropShadowConfig);
        this.mBlurMaskFilter = new BlurMaskFilter(this.mBlurRadiusPx, dropShadowConfig.blurStyle);
        this.mShadowPaint.setMaskFilter(this.mBlurMaskFilter);
    }
}
