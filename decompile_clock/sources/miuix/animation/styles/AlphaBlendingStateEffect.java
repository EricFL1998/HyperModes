package miuix.animation.styles;

import miuix.animation.FolmeEase;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.utils.EaseManager;
import miuix.device.DeviceUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AlphaBlendingStateEffect extends DrawableStateEffect {
    private static final AnimConfig ACTIVATE_ENTER_CONFIG;
    private static final AnimConfig ACTIVATE_EXIT_CONFIG;
    private static final String ALPHA_F = "alphaF";
    private static final AnimConfig DISABLE_ENTER_CONFIG;
    private static final AnimConfig DISABLE_EXIT_CONFIG;
    private static final AnimConfig HOVER_ENTER_CONFIG;
    private static final AnimConfig HOVER_EXIT_CONFIG;
    private static final AnimConfig NORMAL_ENTER_CONFIG;
    private static final AnimConfig NORMAL_EXIT_CONFIG;
    private static final AnimConfig PRESS_ENTER_CONFIG;
    private static final AnimConfig PRESS_EXIT_CONFIG;
    public float activatedAlpha;
    public float checkedAlpha;
    public float disabledAlpha;
    public float focusedAlpha;
    public float hoveredAlpha;
    public float hoveredCheckedAlpha;
    private float mAlpha;
    private final AlphaObserver mAlphaObserver;
    public float normalAlpha;
    public float pressedAlpha;

    public interface AlphaObserver {
        void onAlphaChanged(float f);
    }

    static {
        if (!DeviceUtils.isMiuiLiteV2() && !DeviceUtils.isLiteV1StockPlus() && !DeviceUtils.isMiuiMiddle()) {
            NORMAL_ENTER_CONFIG = new AnimConfig().setEase(FolmeEase.spring(1.0f, 0.35f));
            NORMAL_EXIT_CONFIG = new AnimConfig().setEase(FolmeEase.sineOut(350L));
            DISABLE_ENTER_CONFIG = new AnimConfig().setEase(FolmeEase.spring(1.0f, 0.35f));
            DISABLE_EXIT_CONFIG = new AnimConfig().setEase(FolmeEase.sineOut(350L));
            HOVER_ENTER_CONFIG = new AnimConfig().setEase(EaseManager.getStyle(-2, 1.0f, 0.6f));
            HOVER_EXIT_CONFIG = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.96f, 0.2f));
            AnimConfig ease = new AnimConfig().setEase(EaseManager.getStyle(-2, 1.0f, 0.2f));
            PRESS_ENTER_CONFIG = ease;
            AnimConfig ease2 = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.95f, 0.35f));
            PRESS_EXIT_CONFIG = ease2;
            ACTIVATE_ENTER_CONFIG = ease;
            ACTIVATE_EXIT_CONFIG = ease2;
            return;
        }
        NORMAL_ENTER_CONFIG = null;
        NORMAL_EXIT_CONFIG = null;
        DISABLE_ENTER_CONFIG = null;
        DISABLE_EXIT_CONFIG = null;
        HOVER_ENTER_CONFIG = null;
        HOVER_EXIT_CONFIG = null;
        PRESS_ENTER_CONFIG = null;
        PRESS_EXIT_CONFIG = null;
        ACTIVATE_ENTER_CONFIG = null;
        ACTIVATE_EXIT_CONFIG = null;
    }

    public AlphaBlendingStateEffect(AlphaObserver alphaObserver) {
        this.mAlphaObserver = alphaObserver;
    }

    public void setAlphaF(float f) {
        this.mAlpha = f;
        this.mAlphaObserver.onAlphaChanged(f);
    }

    public float getAlphaF() {
        return this.mAlpha;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void initStates() {
        if (this.mEnableAnim) {
            this.mNormalState = new AnimState().add(ALPHA_F, this.normalAlpha);
            this.mPressedState = new AnimState().add(ALPHA_F, this.pressedAlpha);
            this.mHoveredState = new AnimState().add(ALPHA_F, this.hoveredAlpha);
            this.mFocusedState = new AnimState().add(ALPHA_F, this.focusedAlpha);
            this.mActivatedState = new AnimState().add(ALPHA_F, this.activatedAlpha);
            this.mActivatedPressedState = new AnimState().add(ALPHA_F, this.pressedAlpha + this.activatedAlpha);
            this.mHoveredActivatedState = new AnimState().add(ALPHA_F, this.hoveredAlpha + this.activatedAlpha);
            this.mHoveredPressedState = new AnimState().add(ALPHA_F, this.hoveredAlpha + this.pressedAlpha);
            this.mCheckedState = new AnimState().add(ALPHA_F, this.checkedAlpha);
            this.mHoveredCheckedState = new AnimState().add(ALPHA_F, this.hoveredCheckedAlpha);
            this.mDisabledState = new AnimState().add(ALPHA_F, this.disabledAlpha);
            return;
        }
        setToNormal();
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void jumpToCurrentState() {
        if (isAnimEnabled()) {
            this.mFolmeAnimator.setTo(this.mFolmeAnimator.state().getCurrentState());
        } else {
            setAlphaF(this.mAlpha);
        }
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getHoverEnterConfig() {
        return HOVER_ENTER_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getHoverExitConfig() {
        return HOVER_EXIT_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getPressEnterConfig() {
        return PRESS_ENTER_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getPressExitConfig() {
        return PRESS_EXIT_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getActivateEnterConfig() {
        return ACTIVATE_ENTER_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getActivateExitConfig() {
        return ACTIVATE_EXIT_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getNormalEnterConfig() {
        return NORMAL_ENTER_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getNormalExitConfig() {
        return NORMAL_EXIT_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getDisableEnterConfig() {
        return DISABLE_ENTER_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    protected AnimConfig getDisableExitConfig() {
        return DISABLE_EXIT_CONFIG;
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToNormal() {
        setAlphaF(this.normalAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToPressed() {
        setAlphaF(this.pressedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToActivated() {
        setAlphaF(this.activatedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToActivatedPressed() {
        setAlphaF(this.activatedAlpha + this.pressedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToHovered() {
        setAlphaF(this.hoveredAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToHoveredActivated() {
        setAlphaF(this.hoveredAlpha + this.activatedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToHoveredPressed() {
        setAlphaF(this.hoveredAlpha + this.pressedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToHoveredChecked() {
        setAlphaF(this.hoveredAlpha + this.checkedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToFocused() {
        setAlphaF(this.focusedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToFocusedPressed() {
        setAlphaF(this.focusedAlpha + this.pressedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToChecked() {
        setAlphaF(this.checkedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToCheckedPressed() {
        setAlphaF(this.checkedAlpha + this.pressedAlpha);
    }

    @Override // miuix.animation.styles.DrawableStateEffect
    public void setToDisable() {
        setAlphaF(this.disabledAlpha);
    }
}
