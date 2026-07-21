package miuix.animation.styles;

import android.R;
import android.graphics.Canvas;
import android.util.StateSet;
import miuix.animation.Folme;
import miuix.animation.FolmeObject;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;

/* JADX INFO: loaded from: classes2.dex */
public abstract class DrawableStateEffect implements FolmeObject {
    private boolean mActivated;
    protected AnimState mActivatedPressedState;
    protected AnimState mActivatedState;
    private boolean mChecked;
    protected AnimState mCheckedState;
    protected AnimState mCurrentState;
    private boolean mDisabled;
    protected AnimState mDisabledState;
    protected boolean mEnableAnim;
    private boolean mFocused;
    protected AnimState mFocusedState;
    protected Folme.ObjectFolmeImpl mFolmeAnimator;
    private boolean mHovered;
    protected AnimState mHoveredActivatedState;
    protected AnimState mHoveredCheckedState;
    protected AnimState mHoveredPressedState;
    protected AnimState mHoveredState;
    protected AnimState mNormalState;
    private boolean mPressed;
    protected AnimState mPressedState;
    public static final int[] STATE_PRESSED = {R.attr.state_pressed};
    public static final int[] STATE_DRAG_HOVERED = {R.attr.state_drag_hovered};
    public static final int[] STATE_SELECTED = {R.attr.state_selected};
    public static final int[] STATE_SELECTED_PRESSED = {R.attr.state_selected, R.attr.state_pressed};
    public static final int[] STATE_HOVERED_ACTIVATED = {R.attr.state_hovered, R.attr.state_activated};
    public static final int[] STATE_HOVERED_SELECTED = {R.attr.state_hovered, R.attr.state_selected};
    public static final int[] STATE_HOVERED_PRESSED = {R.attr.state_hovered, R.attr.state_pressed};
    public static final int[] STATE_HOVERED = {R.attr.state_hovered};
    public static final int[] STATE_ACTIVATED = {R.attr.state_activated};
    public static final int[] STATE_ACTIVATED_PRESSED = {R.attr.state_activated, R.attr.state_pressed};
    public static final int[] STATE_CHECKED = {R.attr.state_checked};
    public static final int[] STATE_HOVERED_CHECKED = {R.attr.state_hovered, R.attr.state_checked};
    public static final int[] STATE_FOCUSED = {R.attr.state_focused};
    public static final int[] STATE_ENABLED = {R.attr.state_enabled};

    protected abstract AnimConfig getActivateEnterConfig();

    protected abstract AnimConfig getActivateExitConfig();

    protected abstract AnimConfig getDisableEnterConfig();

    protected abstract AnimConfig getDisableExitConfig();

    protected abstract AnimConfig getHoverEnterConfig();

    protected abstract AnimConfig getHoverExitConfig();

    protected abstract AnimConfig getNormalEnterConfig();

    protected abstract AnimConfig getNormalExitConfig();

    protected abstract AnimConfig getPressEnterConfig();

    protected abstract AnimConfig getPressExitConfig();

    public abstract void initStates();

    public abstract void jumpToCurrentState();

    public abstract void setToActivated();

    public abstract void setToActivatedPressed();

    public abstract void setToChecked();

    public abstract void setToCheckedPressed();

    public abstract void setToDisable();

    public abstract void setToFocused();

    public abstract void setToFocusedPressed();

    public abstract void setToHovered();

    public abstract void setToHoveredActivated();

    public abstract void setToHoveredChecked();

    public abstract void setToHoveredPressed();

    public abstract void setToNormal();

    public abstract void setToPressed();

    public void setEnableAnim(boolean z) {
        this.mEnableAnim = z;
    }

    public boolean isAnimEnabled() {
        return this.mEnableAnim && this.mFolmeAnimator != null;
    }

    public boolean onStateChange(int[] iArr) {
        boolean zStateSetMatches = StateSet.stateSetMatches(STATE_ENABLED, iArr);
        if (zStateSetMatches && (StateSet.stateSetMatches(STATE_ACTIVATED_PRESSED, iArr) || StateSet.stateSetMatches(STATE_SELECTED_PRESSED, iArr))) {
            return toActivatedPressedState();
        }
        if (zStateSetMatches && (StateSet.stateSetMatches(STATE_PRESSED, iArr) || StateSet.stateSetMatches(STATE_DRAG_HOVERED, iArr) || StateSet.stateSetMatches(STATE_SELECTED, iArr))) {
            return toPressedState();
        }
        if (zStateSetMatches && StateSet.stateSetMatches(STATE_FOCUSED, iArr)) {
            return toFocusedState();
        }
        if (zStateSetMatches && (StateSet.stateSetMatches(STATE_HOVERED_ACTIVATED, iArr) || StateSet.stateSetMatches(STATE_HOVERED_SELECTED, iArr))) {
            return toHoveredActivatedState();
        }
        if (zStateSetMatches && StateSet.stateSetMatches(STATE_HOVERED_PRESSED, iArr)) {
            return toHoveredPressedState();
        }
        if (zStateSetMatches && StateSet.stateSetMatches(STATE_HOVERED_CHECKED, iArr)) {
            return toHoveredCheckedState();
        }
        if (zStateSetMatches && StateSet.stateSetMatches(STATE_HOVERED, iArr)) {
            return toHoveredState();
        }
        if (zStateSetMatches && StateSet.stateSetMatches(STATE_ACTIVATED, iArr)) {
            return toActivatedState();
        }
        if (zStateSetMatches && StateSet.stateSetMatches(STATE_CHECKED, iArr)) {
            return toCheckedState();
        }
        if (zStateSetMatches) {
            return toNormalState();
        }
        return toDisableState();
    }

    public void draw(Canvas canvas) {
        AnimState animState;
        if (this.mEnableAnim && this.mFolmeAnimator == null) {
            Folme.ObjectFolmeImpl objectFolmeImplUse = Folme.use((FolmeObject) this);
            this.mFolmeAnimator = objectFolmeImplUse;
            if (objectFolmeImplUse == null || (animState = this.mCurrentState) == null) {
                return;
            }
            objectFolmeImplUse.setTo(animState);
        }
    }

    @Override // miuix.animation.FolmeObject
    public void setFolmeImpl(Folme.ObjectFolmeImpl objectFolmeImpl) {
        this.mFolmeAnimator = objectFolmeImpl;
    }

    @Override // miuix.animation.FolmeObject
    public Folme.ObjectFolmeImpl folme() {
        return this.mFolmeAnimator;
    }

    public boolean toPressedState() {
        if (this.mPressed) {
            return false;
        }
        this.mCurrentState = this.mPressedState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mPressedState, getPressEnterConfig());
        } else {
            setToPressed();
        }
        this.mPressed = true;
        this.mHovered = false;
        this.mActivated = false;
        this.mFocused = false;
        return true;
    }

    public boolean toActivatedPressedState() {
        boolean z = this.mPressed;
        if (z && this.mActivated) {
            return false;
        }
        if (z) {
            this.mActivated = true;
            this.mCurrentState = this.mActivatedPressedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mActivatedPressedState, getActivateEnterConfig());
            } else {
                setToActivatedPressed();
            }
            return true;
        }
        if (this.mActivated) {
            this.mPressed = true;
            this.mCurrentState = this.mActivatedPressedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mActivatedPressedState, getActivateEnterConfig());
            } else {
                setToActivatedPressed();
            }
            return true;
        }
        this.mPressed = true;
        this.mActivated = true;
        this.mHovered = false;
        this.mFocused = false;
        this.mCurrentState = this.mActivatedPressedState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mActivatedPressedState, getPressEnterConfig());
        } else {
            setToActivatedPressed();
        }
        return true;
    }

    public boolean toHoveredActivatedState() {
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = true;
            this.mActivated = true;
            this.mCurrentState = this.mHoveredActivatedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredActivatedState, getPressExitConfig());
            } else {
                setToHoveredActivated();
            }
            return true;
        }
        boolean z = this.mHovered;
        if (z && this.mActivated) {
            return false;
        }
        if (z) {
            this.mActivated = true;
            this.mCurrentState = this.mHoveredActivatedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredActivatedState, getActivateEnterConfig());
            } else {
                setToHoveredActivated();
            }
            return true;
        }
        if (this.mActivated) {
            this.mHovered = true;
            this.mCurrentState = this.mHoveredActivatedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredActivatedState, getHoverEnterConfig());
            } else {
                setToHoveredActivated();
            }
            return true;
        }
        this.mActivated = true;
        this.mHovered = true;
        this.mCurrentState = this.mHoveredActivatedState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mHoveredActivatedState, getHoverEnterConfig());
        } else {
            setToHoveredActivated();
        }
        return true;
    }

    public boolean toHoveredPressedState() {
        boolean z = this.mHovered;
        if (z && this.mPressed) {
            return false;
        }
        if (this.mActivated) {
            this.mActivated = false;
            this.mHovered = true;
            this.mPressed = true;
            this.mCurrentState = this.mHoveredPressedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredPressedState, getHoverEnterConfig());
            } else {
                setToHoveredPressed();
            }
            return true;
        }
        if (this.mPressed) {
            this.mHovered = true;
            this.mCurrentState = this.mHoveredPressedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredPressedState, getHoverEnterConfig());
            } else {
                setToHoveredPressed();
            }
            return true;
        }
        if (z) {
            this.mPressed = true;
            this.mCurrentState = this.mHoveredPressedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredPressedState, getPressEnterConfig());
            } else {
                setToHoveredPressed();
            }
            return true;
        }
        this.mPressed = true;
        this.mHovered = true;
        this.mCurrentState = this.mHoveredPressedState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mHoveredPressedState, getHoverEnterConfig());
        } else {
            setToHoveredPressed();
        }
        return true;
    }

    public boolean toHoveredCheckedState() {
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = true;
            this.mChecked = true;
            this.mCurrentState = this.mHoveredCheckedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredCheckedState, getPressExitConfig());
            } else {
                setToHoveredChecked();
            }
            return true;
        }
        boolean z = this.mHovered;
        if (z && this.mChecked) {
            return false;
        }
        if (z) {
            this.mChecked = true;
            this.mCurrentState = this.mHoveredCheckedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredCheckedState, getActivateEnterConfig());
            } else {
                setToHoveredChecked();
            }
            return true;
        }
        if (this.mChecked) {
            this.mHovered = true;
            this.mCurrentState = this.mHoveredCheckedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredCheckedState, getHoverEnterConfig());
            } else {
                setToHoveredChecked();
            }
            return true;
        }
        this.mChecked = true;
        this.mHovered = true;
        this.mCurrentState = this.mHoveredCheckedState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mHoveredCheckedState, getHoverEnterConfig());
        } else {
            setToHoveredChecked();
        }
        return true;
    }

    public boolean toHoveredState() {
        if (this.mPressed) {
            this.mPressed = false;
            this.mActivated = false;
            this.mChecked = false;
            this.mFocused = false;
            this.mHovered = true;
            this.mCurrentState = this.mHoveredState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredState, getPressExitConfig());
            } else {
                setToHovered();
            }
            return true;
        }
        if (this.mHovered) {
            return false;
        }
        this.mHovered = true;
        this.mActivated = false;
        this.mChecked = false;
        this.mCurrentState = this.mHoveredState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mHoveredState, getHoverEnterConfig());
        } else {
            setToHovered();
        }
        return true;
    }

    public boolean toFocusedState() {
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = false;
            this.mActivated = false;
            this.mChecked = false;
            this.mFocused = true;
            this.mCurrentState = this.mFocusedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mFocusedState, getPressExitConfig());
            } else {
                setToFocused();
            }
            return true;
        }
        if (this.mFocused) {
            return false;
        }
        this.mFocused = true;
        this.mActivated = false;
        this.mChecked = false;
        this.mCurrentState = this.mFocusedState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mFocusedState, getHoverEnterConfig());
        } else {
            setToFocused();
        }
        return true;
    }

    public boolean toActivatedState() {
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = false;
            this.mActivated = true;
            this.mCurrentState = this.mActivatedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mActivatedState, getPressExitConfig());
            } else {
                setToActivated();
            }
            return true;
        }
        if (this.mHovered) {
            this.mHovered = false;
            this.mActivated = true;
            this.mCurrentState = this.mActivatedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mActivatedState, getHoverExitConfig());
            } else {
                setToActivated();
            }
            return true;
        }
        if (this.mActivated) {
            return false;
        }
        this.mActivated = true;
        this.mCurrentState = this.mActivatedState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mActivatedState, getActivateEnterConfig());
        } else {
            setToActivated();
        }
        return true;
    }

    public boolean toCheckedState() {
        this.mDisabled = false;
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = false;
            this.mChecked = true;
            this.mCurrentState = this.mCheckedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mCheckedState, getPressExitConfig());
            } else {
                setToChecked();
            }
            return true;
        }
        if (this.mHovered) {
            this.mHovered = false;
            this.mChecked = true;
            this.mCurrentState = this.mCheckedState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mCheckedState, getHoverExitConfig());
            } else {
                setToChecked();
            }
            return true;
        }
        if (this.mChecked) {
            return false;
        }
        this.mChecked = true;
        this.mCurrentState = this.mCheckedState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mCheckedState, getActivateEnterConfig());
        } else {
            setToChecked();
        }
        return true;
    }

    public boolean toNormalState() {
        if (this.mDisabled) {
            this.mDisabled = false;
            this.mPressed = false;
            this.mHovered = false;
            this.mFocused = false;
            this.mActivated = false;
            this.mChecked = false;
            this.mCurrentState = this.mNormalState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mNormalState, getDisableExitConfig());
            } else {
                setToNormal();
            }
            return true;
        }
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = false;
            this.mFocused = false;
            this.mActivated = false;
            this.mChecked = false;
            this.mCurrentState = this.mNormalState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mNormalState, getPressExitConfig());
            } else {
                setToNormal();
            }
            return true;
        }
        if (this.mHovered) {
            this.mHovered = false;
            this.mActivated = false;
            this.mChecked = false;
            this.mCurrentState = this.mNormalState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mNormalState, getHoverExitConfig());
            } else {
                setToNormal();
            }
            return true;
        }
        if (this.mFocused) {
            this.mFocused = false;
            this.mActivated = false;
            this.mChecked = false;
            this.mCurrentState = this.mNormalState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mNormalState, getHoverExitConfig());
            } else {
                setToNormal();
            }
            return true;
        }
        if (this.mActivated) {
            this.mActivated = false;
            this.mCurrentState = this.mNormalState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mNormalState, getActivateExitConfig());
            } else {
                setToNormal();
            }
            return true;
        }
        if (this.mChecked) {
            this.mChecked = false;
            this.mCurrentState = this.mNormalState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mNormalState, getActivateExitConfig());
            } else {
                setToNormal();
            }
            return true;
        }
        this.mCurrentState = this.mNormalState;
        return false;
    }

    public boolean toDisableState() {
        this.mDisabled = true;
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = false;
            this.mFocused = false;
            this.mActivated = false;
            this.mChecked = false;
            this.mCurrentState = this.mDisabledState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mDisabledState, getPressExitConfig());
            } else {
                setToDisable();
            }
            return true;
        }
        if (this.mHovered) {
            this.mHovered = false;
            this.mActivated = false;
            this.mChecked = false;
            this.mCurrentState = this.mDisabledState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mDisabledState, getHoverExitConfig());
            } else {
                setToDisable();
            }
            return true;
        }
        if (this.mFocused) {
            this.mFocused = false;
            this.mActivated = false;
            this.mChecked = false;
            this.mCurrentState = this.mDisabledState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mDisabledState, getHoverExitConfig());
            } else {
                setToDisable();
            }
            return true;
        }
        if (this.mActivated) {
            this.mActivated = false;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mDisabledState, getActivateExitConfig());
            } else {
                setToDisable();
            }
            return true;
        }
        if (this.mChecked) {
            this.mChecked = false;
            this.mCurrentState = this.mDisabledState;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mDisabledState, getActivateExitConfig());
            } else {
                setToDisable();
            }
            return true;
        }
        this.mCurrentState = this.mDisabledState;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mDisabledState, getNormalExitConfig());
        } else {
            setToDisable();
        }
        return true;
    }
}
