package miuix.appcompat.internal.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import miuix.appcompat.R;
import miuix.graphics.shadow.DropShadowConfig;
import miuix.graphics.shadow.DropShadowHelper;
import miuix.graphics.shadow.DropShadowLayerHelper;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes2.dex */
public class OutDropShadowView extends View {
    private Path mClipOutPath;
    private float mDensityDpi;
    private DropShadowHelper mDropShadowHelper;
    private int mHostViewRadius;

    public OutDropShadowView(Context context) {
        super(context);
        this.mHostViewRadius = 0;
        this.mClipOutPath = new Path();
        init();
    }

    public OutDropShadowView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHostViewRadius = 0;
        this.mClipOutPath = new Path();
        init();
    }

    public OutDropShadowView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHostViewRadius = 0;
        this.mClipOutPath = new Path();
        init();
    }

    public OutDropShadowView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHostViewRadius = 0;
        this.mClipOutPath = new Path();
        init();
    }

    private void init() {
        this.mDensityDpi = getContext().getResources().getDisplayMetrics().densityDpi;
        DropShadowLayerHelper dropShadowLayerHelper = new DropShadowLayerHelper(getContext(), new DropShadowConfig.Builder(50.0f).setOffsetYDp(6).create(), AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true));
        this.mDropShadowHelper = dropShadowLayerHelper;
        dropShadowLayerHelper.setEnableMiShadow(false);
        this.mDropShadowHelper.updateShadowRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        setBackgroundColor(0);
        setImportantForAccessibility(4);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mDropShadowHelper.enableViewShadow(this, true, 2);
    }

    public void onWillRemoved() {
        this.mDropShadowHelper.enableViewShadow(this, false, 2);
    }

    public void setShadowHostViewRadius(int i) {
        this.mHostViewRadius = i;
        this.mClipOutPath.reset();
        Path path = this.mClipOutPath;
        RectF shadowRect = this.mDropShadowHelper.getShadowRect();
        int i2 = this.mHostViewRadius;
        path.addRoundRect(shadowRect, i2, i2, Path.Direction.CW);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        canvas.save();
        if (this.mDropShadowHelper != null) {
            canvas.clipOutPath(this.mClipOutPath);
            this.mDropShadowHelper.drawShadow(canvas, this.mHostViewRadius);
        }
        super.draw(canvas);
        canvas.restore();
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        DropShadowHelper dropShadowHelper = this.mDropShadowHelper;
        if (dropShadowHelper != null) {
            dropShadowHelper.updateShadowRect(i, i2, i3, i4);
            this.mClipOutPath.reset();
            Path path = this.mClipOutPath;
            RectF shadowRect = this.mDropShadowHelper.getShadowRect();
            int i5 = this.mHostViewRadius;
            path.addRoundRect(shadowRect, i5, i5, Path.Direction.CW);
        }
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        DropShadowHelper dropShadowHelper;
        if (configuration.densityDpi == this.mDensityDpi || (dropShadowHelper = this.mDropShadowHelper) == null) {
            return;
        }
        dropShadowHelper.onConfigChanged(this, configuration, AttributeResolver.resolveBoolean(getContext(), R.attr.isLightTheme, true));
    }
}
