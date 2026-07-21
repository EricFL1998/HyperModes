package com.android.deskclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.widget.ClockFloatingActionButton;

/* JADX INFO: loaded from: classes.dex */
public class FabView extends ClockFloatingActionButton {
    private boolean inOutAnim;
    PaintFlagsDrawFilter mPaintFlagsDrawFilter;

    public FabView(Context context) {
        this(context, null);
    }

    public FabView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FabView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, 3);
        this.inOutAnim = false;
        if (MiuiSdk.isLiteOrMiddleMode()) {
            return;
        }
        MiuiFolme.touch(this);
    }

    public void setNormalBackground() {
        setmShadowColor(getResources().getColor(R.color.fab_shadow_color));
        setBackground(R.drawable.fab_normal, R.drawable.fab_pressed);
    }

    public void setFullScreenModeBackground() {
        setmShadowColor(0);
        setBackground(R.drawable.btn_shape_circle_normal_full_screen, 0);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(this.mPaintFlagsDrawFilter);
        super.onDraw(canvas);
    }

    public void setOutAnim(boolean z) {
        this.inOutAnim = z;
    }

    public boolean getOutAnim() {
        return this.inOutAnim;
    }
}
