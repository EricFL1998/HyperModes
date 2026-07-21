package com.android.deskclock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class ViewStubFrameLayout extends FrameLayout {
    private static final String TAG = "DC:ViewStubFrameLayout";
    private OnChildStubInflatedListener mOnInflatedListener;

    public interface OnChildStubInflatedListener {
        void onChildInflated(View view, int i);
    }

    public ViewStubFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setOnChildInflatedListener(OnChildStubInflatedListener onChildStubInflatedListener) {
        this.mOnInflatedListener = onChildStubInflatedListener;
        for (final int i = 0; i < getChildCount(); i++) {
            if ((getChildAt(i) instanceof ViewStub) && this.mOnInflatedListener != null) {
                ((ViewStub) getChildAt(i)).setOnInflateListener(new ViewStub.OnInflateListener() { // from class: com.android.deskclock.view.ViewStubFrameLayout.1
                    @Override // android.view.ViewStub.OnInflateListener
                    public void onInflate(ViewStub viewStub, View view) {
                        ViewStubFrameLayout.this.mOnInflatedListener.onChildInflated(ViewStubFrameLayout.this, i);
                        Log.d(ViewStubFrameLayout.TAG, "child inflated: " + i);
                    }
                });
            }
        }
    }
}
