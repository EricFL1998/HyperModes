package miuix.animation.controller;

import java.util.Collection;
import miuix.animation.FolmeEase;
import miuix.animation.IAnimTarget;
import miuix.animation.IVisibleStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes2.dex */
public class FolmeVisible extends FolmeBase implements IVisibleStyle {
    private static final String ALIAS_VISIBLE_HIDE = "visibleHide";
    private static final String ALIAS_VISIBLE_SHOW = "visibleShow";
    private final AnimConfig mDefConfig;
    private boolean mHasMove;
    private boolean mHasScale;
    private boolean mSetBound;

    public FolmeVisible(IAnimTarget... iAnimTargetArr) {
        super(iAnimTargetArr);
        this.mDefConfig = new AnimConfig().addListeners(new TransitionListener() { // from class: miuix.animation.controller.FolmeVisible.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj, Collection<UpdateInfo> collection) {
                if (obj.equals(IVisibleStyle.VisibleType.SHOW) && FolmeVisible.this.mSetBound) {
                    AnimState.alignState(FolmeVisible.this.mState.getState(IVisibleStyle.VisibleType.HIDE), collection);
                }
            }
        });
        this.mState.addState(new AnimState(IVisibleStyle.VisibleType.SHOW, ALIAS_VISIBLE_SHOW));
        this.mState.addState(new AnimState(IVisibleStyle.VisibleType.HIDE, ALIAS_VISIBLE_HIDE));
        useAutoAlpha(true);
    }

    @Override // miuix.animation.controller.FolmeBase, miuix.animation.IStateContainer
    public void clean() {
        super.clean();
        this.mHasScale = false;
        this.mHasMove = false;
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle setBound(int i, int i2, int i3, int i4) {
        this.mSetBound = true;
        this.mState.getState(IVisibleStyle.VisibleType.SHOW).add(ViewProperty.X, i).add(ViewProperty.Y, i2).add(ViewProperty.WIDTH, i3).add(ViewProperty.HEIGHT, i4);
        return this;
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle useAutoAlpha(boolean z) {
        ViewProperty viewProperty = ViewProperty.AUTO_ALPHA;
        ViewProperty viewProperty2 = ViewProperty.ALPHA;
        if (z) {
            this.mState.getState(IVisibleStyle.VisibleType.SHOW).remove(viewProperty2).add(viewProperty, 1.0d);
            this.mState.getState(IVisibleStyle.VisibleType.HIDE).remove(viewProperty2).add(viewProperty, 0.0d);
        } else {
            this.mState.getState(IVisibleStyle.VisibleType.SHOW).remove(viewProperty).add(viewProperty2, 1.0d);
            this.mState.getState(IVisibleStyle.VisibleType.HIDE).remove(viewProperty).add(viewProperty2, 0.0d);
        }
        return this;
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle setFlags(long j) {
        this.mState.setFlags(j);
        return this;
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle setAlpha(float f, IVisibleStyle.VisibleType... visibleTypeArr) {
        this.mState.getState(getType(visibleTypeArr)).add(ViewProperty.AUTO_ALPHA, f);
        return this;
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle setScale(float f, IVisibleStyle.VisibleType... visibleTypeArr) {
        this.mHasScale = true;
        double d = f;
        this.mState.getState(getType(visibleTypeArr)).add(ViewProperty.SCALE_Y, d).add(ViewProperty.SCALE_X, d);
        return this;
    }

    private IVisibleStyle.VisibleType getType(IVisibleStyle.VisibleType... visibleTypeArr) {
        return visibleTypeArr.length > 0 ? visibleTypeArr[0] : IVisibleStyle.VisibleType.HIDE;
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle setMove(int i, int i2) {
        return setMove(i, i2, IVisibleStyle.VisibleType.HIDE);
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle setMove(int i, int i2, IVisibleStyle.VisibleType... visibleTypeArr) {
        boolean z = Math.abs(i) > 0 || Math.abs(i2) > 0;
        this.mHasMove = z;
        if (z) {
            this.mState.getState(getType(visibleTypeArr)).add(ViewProperty.X, i, 1).add(ViewProperty.Y, i2, 1);
        }
        return this;
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle setShowDelay(long j) {
        this.mState.getState(IVisibleStyle.VisibleType.SHOW).getConfig().delay = j;
        return this;
    }

    @Override // miuix.animation.IVisibleStyle
    public void show(AnimConfig... animConfigArr) {
        this.mState.to(IVisibleStyle.VisibleType.SHOW, getConfig(IVisibleStyle.VisibleType.SHOW, animConfigArr));
    }

    @Override // miuix.animation.IVisibleStyle
    public void hide(AnimConfig... animConfigArr) {
        this.mState.to(IVisibleStyle.VisibleType.HIDE, getConfig(IVisibleStyle.VisibleType.HIDE, animConfigArr));
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle setShow() {
        this.mState.setTo(IVisibleStyle.VisibleType.SHOW);
        return this;
    }

    @Override // miuix.animation.IVisibleStyle
    public IVisibleStyle setHide() {
        this.mState.setTo(IVisibleStyle.VisibleType.HIDE);
        return this;
    }

    private AnimConfig[] getConfig(IVisibleStyle.VisibleType visibleType, AnimConfig... animConfigArr) {
        EaseManager.EaseStyle easeStyleSpring;
        EaseManager.EaseStyle easeStyleSpring2;
        EaseManager.EaseStyle easeStyleSpring3;
        EaseManager.EaseStyle easeStyleSpring4;
        boolean z = this.mHasScale;
        if (!z && !this.mHasMove) {
            AnimConfig animConfig = this.mDefConfig;
            if (visibleType == IVisibleStyle.VisibleType.SHOW) {
                easeStyleSpring4 = FolmeEase.sinInOut(300L);
            } else {
                easeStyleSpring4 = FolmeEase.spring(1.0f, 0.15f);
            }
            animConfig.setEase(easeStyleSpring4);
        } else if (z && !this.mHasMove) {
            AnimConfig animConfig2 = this.mDefConfig;
            if (visibleType == IVisibleStyle.VisibleType.SHOW) {
                easeStyleSpring3 = FolmeEase.spring(0.6f, 0.35f);
            } else {
                easeStyleSpring3 = FolmeEase.spring(0.75f, 0.2f);
            }
            animConfig2.setEase(easeStyleSpring3);
        } else if (!z) {
            AnimConfig animConfig3 = this.mDefConfig;
            if (visibleType == IVisibleStyle.VisibleType.SHOW) {
                easeStyleSpring2 = FolmeEase.spring(0.75f, 0.35f);
            } else {
                easeStyleSpring2 = FolmeEase.spring(0.75f, 0.25f);
            }
            animConfig3.setEase(easeStyleSpring2);
        } else {
            AnimConfig animConfig4 = this.mDefConfig;
            if (visibleType == IVisibleStyle.VisibleType.SHOW) {
                easeStyleSpring = FolmeEase.spring(0.65f, 0.35f);
            } else {
                easeStyleSpring = FolmeEase.spring(0.75f, 0.25f);
            }
            animConfig4.setEase(easeStyleSpring);
        }
        return (AnimConfig[]) CommonUtils.mergeArray(animConfigArr, this.mDefConfig);
    }
}
