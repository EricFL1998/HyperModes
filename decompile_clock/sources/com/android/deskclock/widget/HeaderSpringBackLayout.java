package com.android.deskclock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.android.deskclock.R;
import miuix.springback.view.SpringBackLayout;

/* JADX INFO: loaded from: classes.dex */
public class HeaderSpringBackLayout extends SpringBackLayout {
    private static final int INVALID_ID = -1;
    private static final String TAG = "HeaderSpringBackLayout";
    private View mHeader;
    private int mHeaderId;

    public HeaderSpringBackLayout(Context context) {
        this(context, null);
    }

    public HeaderSpringBackLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HeaderSpringBackLayout);
        this.mHeaderId = typedArrayObtainStyledAttributes.getResourceId(0, -1);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // miuix.springback.view.SpringBackLayout, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        int i = this.mHeaderId;
        if (i != -1) {
            this.mHeader = findViewById(i);
        }
    }

    @Override // miuix.springback.view.SpringBackLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        View view = this.mHeader;
        if (view != null) {
            view.layout(paddingLeft, paddingTop, view.getMeasuredWidth() + paddingLeft, this.mHeader.getMeasuredHeight() + paddingTop);
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    @Override // miuix.springback.view.SpringBackLayout, android.view.View
    public void onMeasure(int i, int i2) {
        View view = this.mHeader;
        if (view != null) {
            measureChild(view, i, i2);
        }
        super.onMeasure(i, i2);
    }

    @Override // android.view.View
    public int getPaddingTop() {
        return super.getPaddingTop();
    }
}
