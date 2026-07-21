package miuix.appcompat.app.floatingactivity.helper;

import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.FloatingAnimHelper;
import miuix.core.util.EnvStateManager;
import miuix.core.util.WindowBaseInfo;

/* JADX INFO: loaded from: classes2.dex */
public class PadFloatingActivityHelper extends TabletFloatingActivityHelper {
    public PadFloatingActivityHelper(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper
    public boolean isFloatingModeSupport() {
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(this.mActivity);
        if (windowInfo.windowMode == 0 || windowInfo.windowMode == 8195 || windowInfo.windowMode == 4099) {
            return true;
        }
        return windowInfo.windowSizeDp.y >= 747 && windowInfo.windowSizeDp.x > 670;
    }

    @Override // miuix.appcompat.app.floatingactivity.helper.TabletFloatingActivityHelper
    public void execExitAnim() {
        if (FloatingAnimHelper.isSupportTransWithClipAnim()) {
            return;
        }
        if (isFloatingWindow()) {
            FloatingAnimHelper.clearFloatingWindowAnim(this.mActivity);
        } else if (FloatingAnimHelper.obtainPageIndex(this.mActivity) >= 0) {
            FloatingAnimHelper.execFloatingWindowExitAnimRomNormal(this.mActivity);
        }
    }
}
