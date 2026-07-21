package miuix.appcompat.internal.app;

import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public class NavigatorSwitchPresenter {
    private float mAlpha;
    private final View mNavigatorSwitch;
    private boolean mSuppressAlpha;
    private boolean mSuppressVisibility;
    private int mVisibility;

    public NavigatorSwitchPresenter(View view) {
        this.mNavigatorSwitch = view;
        this.mVisibility = view.getVisibility();
        this.mAlpha = view.getAlpha();
    }

    public void suppressVisibility(boolean z, int i) {
        this.mSuppressVisibility = z;
        if (z) {
            this.mNavigatorSwitch.setVisibility(i);
        } else {
            this.mNavigatorSwitch.setVisibility(this.mVisibility);
        }
    }

    public void setVisibility(int i) {
        this.mVisibility = i;
        if (this.mSuppressVisibility) {
            return;
        }
        this.mNavigatorSwitch.setVisibility(i);
    }

    public void suppressAlpha(boolean z, float f) {
        this.mSuppressAlpha = z;
        if (z) {
            this.mNavigatorSwitch.setAlpha(f);
        } else {
            this.mNavigatorSwitch.setAlpha(this.mAlpha);
        }
    }

    public void setAlpha(float f) {
        this.mAlpha = f;
        if (this.mSuppressAlpha) {
            return;
        }
        this.mNavigatorSwitch.setAlpha(f);
    }
}
