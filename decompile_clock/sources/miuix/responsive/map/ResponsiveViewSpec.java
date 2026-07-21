package miuix.responsive.map;

import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public class ResponsiveViewSpec {
    private int mEffectiveScreenOrientation;
    private int mHideInScreenMode;
    private View mView;
    private int mViewId;

    public ResponsiveViewSpec(int i) {
        this.mViewId = i;
    }

    public ResponsiveViewSpec(int i, int i2) {
        this.mViewId = i;
        this.mHideInScreenMode = i2;
    }

    public void onResponsiveState(ScreenSpec screenSpec) {
        int i = screenSpec.screenMode & 7;
        View view = this.mView;
        if (view != null) {
            view.setVisibility(this.mHideInScreenMode < i ? 0 : 8);
        }
    }

    public int getViewId() {
        return this.mViewId;
    }

    public int getHideInScreenMode() {
        return this.mHideInScreenMode;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public View getView() {
        return this.mView;
    }

    public int getEffectiveScreenOrientation() {
        return this.mEffectiveScreenOrientation;
    }

    public void setEffectiveScreenOrientation(int i) {
        this.mEffectiveScreenOrientation = i;
    }
}
