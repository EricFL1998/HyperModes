package miuix.appcompat.app.strategy;

import miuix.appcompat.app.DialogContract;

/* JADX INFO: loaded from: classes2.dex */
public class DialogButtonBehaviorImpl implements IDialogButtonBehavior {
    @Override // miuix.appcompat.app.strategy.IDialogButtonBehavior
    public boolean isButtonScrollable(DialogContract.ButtonScrollSpec buttonScrollSpec) {
        if (buttonScrollSpec.mButtonFVHeight <= 0) {
            return false;
        }
        float fMax = Math.max(buttonScrollSpec.mWindowHeight, 1);
        return (Math.max(buttonScrollSpec.mButtonPanelHeight, buttonScrollSpec.mButtonFVHeight) * 1.0f) / fMax >= (buttonScrollSpec.mIsLargeFont ? 0.3f : 0.4f) || (buttonScrollSpec.mTopPanelHeight * 1.0f) / fMax >= (buttonScrollSpec.mIsLargeFont ? 0.35f : 0.45f) || (!buttonScrollSpec.mHasListView && ((buttonScrollSpec.mIsFlipTiny || buttonScrollSpec.mRootViewSizeYDp <= 480) && buttonScrollSpec.mVisibleButtonCount >= 3)) || (buttonScrollSpec.mIsFlipTiny && buttonScrollSpec.mWindowOrientation == 2);
    }
}
