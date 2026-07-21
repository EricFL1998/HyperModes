package miuix.animation.controller;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.IAnimTarget;
import miuix.animation.IHoverStyle;
import miuix.animation.ViewHoverListener;
import miuix.animation.ViewTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.internal.AnimValueUtils;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.ViewProperty;
import miuix.animation.property.ViewPropertyExt;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LogUtils;
import miuix.folme.R;

/* JADX INFO: loaded from: classes2.dex */
public class FolmeHover extends FolmeBase implements IHoverStyle {
    private static final String ALIAS_HOVER_ENTER = "hoverEnter";
    private static final String ALIAS_HOVER_EXIT = "hoverExit";
    private static final int CORNER_DIS = 36;
    private static final float DEFAULT_CORNER = 0.5f;
    private static final float DEFAULT_SCALE = 1.15f;
    private static final int SCALE_DIS = 12;
    private static WeakHashMap<View, InnerViewHoverListener> sHoverRecord = new WeakHashMap<>();
    private String HoverMoveType;
    private WeakReference<View> mChildView;
    private boolean mClearTint;
    private IHoverStyle.HoverEffect mCurrentEffect;
    private TransitionListener mDefListener;
    private AnimConfig mEnterConfig;
    private AnimConfig mExitConfig;
    private float mExtraTranslationX;
    private WeakReference<View> mHoverView;
    private boolean mIsEnter;
    private boolean mIsSetAutoTranslation;
    private int[] mLocation;
    private AnimConfig mMoveConfig;
    private WeakReference<View> mParentView;
    private float mRadius;
    private Map<IHoverStyle.HoverType, Boolean> mScaleSetMap;
    private boolean mSetTint;
    private int mTargetHeight;
    private int mTargetWidth;
    private float mTranslateDist;
    private Map<IHoverStyle.HoverType, Boolean> mTranslationSetMap;

    private void clearRound() {
    }

    private float perFromVal(float f, float f2, float f3) {
        return (f - f2) / (f3 - f2);
    }

    private void setAutoRound() {
    }

    private float valFromPer(float f, float f2, float f3) {
        return f2 + ((f3 - f2) * f);
    }

    public FolmeHover(IAnimTarget... iAnimTargetArr) {
        super(iAnimTargetArr);
        this.mTranslateDist = Float.MAX_VALUE;
        this.mMoveConfig = new AnimConfig().setEase(FolmeEase.spring(0.9f, 0.4f));
        this.mEnterConfig = new AnimConfig();
        this.mExitConfig = new AnimConfig();
        this.mScaleSetMap = new ArrayMap();
        this.mTranslationSetMap = new ArrayMap();
        this.mCurrentEffect = IHoverStyle.HoverEffect.NORMAL;
        this.mIsSetAutoTranslation = false;
        this.mClearTint = false;
        this.mLocation = new int[2];
        this.mRadius = 0.0f;
        this.mTargetWidth = 0;
        this.mTargetHeight = 0;
        this.mExtraTranslationX = 0.0f;
        this.HoverMoveType = "MOVE";
        this.mDefListener = new TransitionListener() { // from class: miuix.animation.controller.FolmeHover.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj, Collection<UpdateInfo> collection) {
                if (obj.equals(IHoverStyle.HoverType.ENTER)) {
                    AnimState.alignState(FolmeHover.this.mState.getState(IHoverStyle.HoverType.EXIT), collection);
                }
            }
        };
        initDist(iAnimTargetArr.length > 0 ? iAnimTargetArr[0] : null);
        this.mState.addState(new AnimState(IHoverStyle.HoverType.ENTER, ALIAS_HOVER_ENTER));
        this.mState.addState(new AnimState(IHoverStyle.HoverType.EXIT, ALIAS_HOVER_EXIT));
        updateHoverState(this.mCurrentEffect);
        this.mEnterConfig.setEase(FolmeEase.spring(0.99f, 0.6f));
        this.mEnterConfig.addListeners(this.mDefListener);
        this.mExitConfig.setEase(FolmeEase.spring(0.99f, 0.4f)).setSpecial(ViewProperty.ALPHA, FolmeEase.spring(0.9f, 0.2f), new float[0]);
    }

    private void setAutoScale() {
        this.mScaleSetMap.put(IHoverStyle.HoverType.EXIT, true);
        this.mState.getState(IHoverStyle.HoverType.EXIT).add(ViewProperty.SCALE_X, 1.0d).add(ViewProperty.SCALE_Y, 1.0d);
    }

    private void setAutoTranslation() {
        this.mIsSetAutoTranslation = true;
        this.mTranslationSetMap.put(IHoverStyle.HoverType.ENTER, true);
        this.mTranslationSetMap.put(IHoverStyle.HoverType.EXIT, true);
        this.mState.getState(IHoverStyle.HoverType.EXIT).add(ViewProperty.TRANSLATION_X, 0.0d).add(ViewProperty.TRANSLATION_Y, 0.0d);
    }

    private void setTintColor() {
        if (this.mSetTint || this.mClearTint) {
            return;
        }
        int iArgb = Color.argb(15, 0, 0, 0);
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            iArgb = ((View) targetObject).getResources().getColor(R.color.miuix_folme_color_hover_tint);
        }
        ViewPropertyExt.ForegroundProperty foregroundProperty = ViewPropertyExt.FOREGROUND;
        this.mState.getState(IHoverStyle.HoverType.ENTER).add(foregroundProperty, iArgb);
        this.mState.getState(IHoverStyle.HoverType.EXIT).add(foregroundProperty, 0.0d);
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setParentView(View view) {
        WeakReference<View> weakReference = this.mParentView;
        if (view == (weakReference != null ? weakReference.get() : null)) {
            return this;
        }
        this.mParentView = new WeakReference<>(view);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setAlpha(float f, IHoverStyle.HoverType... hoverTypeArr) {
        this.mState.getState(getType(hoverTypeArr)).add(ViewProperty.ALPHA, f);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setScale(float f, IHoverStyle.HoverType... hoverTypeArr) {
        IHoverStyle.HoverType type = getType(hoverTypeArr);
        this.mScaleSetMap.put(type, true);
        double d = f;
        this.mState.getState(type).add(ViewProperty.SCALE_X, d).add(ViewProperty.SCALE_Y, d);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setTranslate(float f, IHoverStyle.HoverType... hoverTypeArr) {
        this.mIsSetAutoTranslation = false;
        IHoverStyle.HoverType type = getType(hoverTypeArr);
        this.mTranslationSetMap.put(type, true);
        double d = f;
        this.mState.getState(type).add(ViewProperty.TRANSLATION_X, d).add(ViewProperty.TRANSLATION_Y, d);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setCorner(float f) {
        this.mRadius = f;
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            ((View) targetObject).setTag(miuix.animation.R.id.miuix_animation_tag_view_hover_corners, Float.valueOf(f));
        }
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setTint(int i) {
        this.mSetTint = true;
        this.mClearTint = i == 0;
        this.mState.getState(IHoverStyle.HoverType.ENTER).add(ViewPropertyExt.FOREGROUND, i);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setTint(float f, float f2, float f3, float f4) {
        return setTint(Color.argb((int) (f * 255.0f), (int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f)));
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setBackgroundColor(float f, float f2, float f3, float f4) {
        return setBackgroundColor(Color.argb((int) (f * 255.0f), (int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f)));
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setBackgroundColor(int i) {
        ViewPropertyExt.BackgroundProperty backgroundProperty = ViewPropertyExt.BACKGROUND;
        this.mState.getState(IHoverStyle.HoverType.ENTER).add(backgroundProperty, i);
        this.mState.getState(IHoverStyle.HoverType.EXIT).add(backgroundProperty, (int) AnimValueUtils.getValueOfTarget(this.mState.getTarget(), backgroundProperty, 0.0d));
        return this;
    }

    private void clearScale() {
        if (isScaleSet(IHoverStyle.HoverType.ENTER)) {
            this.mState.getState(IHoverStyle.HoverType.ENTER).remove(ViewProperty.SCALE_X);
            this.mState.getState(IHoverStyle.HoverType.ENTER).remove(ViewProperty.SCALE_Y);
        }
        if (isScaleSet(IHoverStyle.HoverType.EXIT)) {
            this.mState.getState(IHoverStyle.HoverType.EXIT).remove(ViewProperty.SCALE_X);
            this.mState.getState(IHoverStyle.HoverType.EXIT).remove(ViewProperty.SCALE_Y);
        }
        this.mScaleSetMap.clear();
    }

    private void clearTranslation() {
        this.mIsSetAutoTranslation = false;
        if (isTranslationSet(IHoverStyle.HoverType.ENTER)) {
            this.mState.getState(IHoverStyle.HoverType.ENTER).remove(ViewProperty.TRANSLATION_X);
            this.mState.getState(IHoverStyle.HoverType.ENTER).remove(ViewProperty.TRANSLATION_Y);
        }
        if (isTranslationSet(IHoverStyle.HoverType.EXIT)) {
            this.mState.getState(IHoverStyle.HoverType.EXIT).remove(ViewProperty.TRANSLATION_X);
            this.mState.getState(IHoverStyle.HoverType.EXIT).remove(ViewProperty.TRANSLATION_Y);
        }
        this.mTranslationSetMap.clear();
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle clearTintColor() {
        this.mClearTint = true;
        ViewPropertyExt.ForegroundProperty foregroundProperty = ViewPropertyExt.FOREGROUND;
        this.mState.getState(IHoverStyle.HoverType.ENTER).remove(foregroundProperty);
        this.mState.getState(IHoverStyle.HoverType.EXIT).remove(foregroundProperty);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setTintMode(int i) {
        this.mEnterConfig.setTintMode(i);
        this.mExitConfig.setTintMode(i);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public IHoverStyle setEffect(IHoverStyle.HoverEffect hoverEffect) {
        updateHoverState(hoverEffect);
        return this;
    }

    @Override // miuix.animation.IHoverStyle
    public void handleHoverOf(View view, AnimConfig... animConfigArr) {
        doHandleHoverOf(view, animConfigArr);
    }

    private void doHandleHoverOf(View view, AnimConfig... animConfigArr) {
        handleViewHover(view, animConfigArr);
        if (setHoverView(view) && LogUtils.isLogMainEnabled()) {
            LogUtils.debug("+ doHandleHoverOf->handleViewHover for " + view, new Object[0]);
        }
    }

    /* JADX INFO: renamed from: miuix.animation.controller.FolmeHover$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$miuix$animation$IHoverStyle$HoverEffect;

        static {
            int[] iArr = new int[IHoverStyle.HoverEffect.values().length];
            $SwitchMap$miuix$animation$IHoverStyle$HoverEffect = iArr;
            try {
                iArr[IHoverStyle.HoverEffect.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$animation$IHoverStyle$HoverEffect[IHoverStyle.HoverEffect.FLOATED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miuix$animation$IHoverStyle$HoverEffect[IHoverStyle.HoverEffect.FLOATED_WRAPPED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    private void updateHoverState(IHoverStyle.HoverEffect hoverEffect) {
        int i = AnonymousClass2.$SwitchMap$miuix$animation$IHoverStyle$HoverEffect[hoverEffect.ordinal()];
        if (i == 1) {
            if (this.mCurrentEffect == IHoverStyle.HoverEffect.FLOATED) {
                clearScale();
                clearTranslation();
            } else if (this.mCurrentEffect == IHoverStyle.HoverEffect.FLOATED_WRAPPED) {
                clearScale();
                clearTranslation();
                clearRound();
            }
            setTintColor();
            this.mCurrentEffect = hoverEffect;
            return;
        }
        if (i == 2) {
            if (this.mCurrentEffect == IHoverStyle.HoverEffect.FLOATED_WRAPPED) {
                clearRound();
            }
            setTintColor();
            setAutoScale();
            setAutoTranslation();
            this.mCurrentEffect = hoverEffect;
            return;
        }
        if (i != 3) {
            return;
        }
        if (this.mCurrentEffect == IHoverStyle.HoverEffect.NORMAL || this.mCurrentEffect == IHoverStyle.HoverEffect.FLOATED) {
            clearTintColor();
        }
        setAutoScale();
        setAutoTranslation();
        setAutoRound();
        this.mCurrentEffect = hoverEffect;
    }

    private boolean setHoverView(View view) {
        WeakReference<View> weakReference = this.mHoverView;
        if ((weakReference != null ? weakReference.get() : null) == view) {
            return false;
        }
        this.mHoverView = new WeakReference<>(view);
        return true;
    }

    private void handleViewHover(View view, AnimConfig... animConfigArr) {
        InnerViewHoverListener innerViewHoverListener = sHoverRecord.get(view);
        if (innerViewHoverListener == null) {
            innerViewHoverListener = new InnerViewHoverListener();
            sHoverRecord.put(view, innerViewHoverListener);
        }
        view.setOnHoverListener(innerViewHoverListener);
        innerViewHoverListener.addHover(this, animConfigArr);
    }

    private static class InnerViewHoverListener implements View.OnHoverListener {
        private WeakHashMap<FolmeHover, AnimConfig[]> mHoverMap;

        private InnerViewHoverListener() {
            this.mHoverMap = new WeakHashMap<>();
        }

        void addHover(FolmeHover folmeHover, AnimConfig... animConfigArr) {
            this.mHoverMap.put(folmeHover, animConfigArr);
        }

        boolean removeHover(FolmeHover folmeHover) {
            this.mHoverMap.remove(folmeHover);
            return this.mHoverMap.isEmpty();
        }

        @Override // android.view.View.OnHoverListener
        public boolean onHover(View view, MotionEvent motionEvent) {
            for (Map.Entry<FolmeHover, AnimConfig[]> entry : this.mHoverMap.entrySet()) {
                FolmeHover key = entry.getKey();
                AnimConfig[] value = entry.getValue();
                if (motionEvent.getAction() == 9 && getHoverTranslationXTag(view) != key.mExtraTranslationX) {
                    key.mExtraTranslationX = getHoverTranslationXTag(view);
                }
                key.handleMotionEvent(view, motionEvent, value);
            }
            return false;
        }

        private float getHoverTranslationXTag(View view) {
            if (view.getTag(R.id.miuix_animation_tag_hover_set_translation_x) instanceof Float) {
                return ((Float) view.getTag(R.id.miuix_animation_tag_hover_set_translation_x)).floatValue();
            }
            return 0.0f;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public void handleMotionEvent(View view, MotionEvent motionEvent, AnimConfig... animConfigArr) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 7) {
            onEventMove(view, motionEvent, animConfigArr);
            if (view instanceof ViewHoverListener) {
                ((ViewHoverListener) view).onMoveHover();
                return;
            }
            return;
        }
        if (actionMasked == 9) {
            onEventEnter(motionEvent, animConfigArr);
            if (view instanceof ViewHoverListener) {
                ((ViewHoverListener) view).onEnterHover();
                return;
            }
            return;
        }
        if (actionMasked != 10) {
            return;
        }
        onEventExit(motionEvent, animConfigArr);
        if (view instanceof ViewHoverListener) {
            ((ViewHoverListener) view).onExitHover();
        }
    }

    private void onEventEnter(MotionEvent motionEvent, AnimConfig... animConfigArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeHover.onEventEnter->hoverEnter " + motionEvent.getAction(), new Object[0]);
        }
        hoverEnter(motionEvent, animConfigArr);
    }

    private void onEventMove(View view, MotionEvent motionEvent, AnimConfig... animConfigArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeHover.onEventMove event " + motionEvent, new Object[0]);
        }
        if (this.mIsEnter && view != null && isTranslationSet(IHoverStyle.HoverType.ENTER) && this.mIsSetAutoTranslation) {
            actualTranslateDist(view, motionEvent);
        }
    }

    private void onEventExit(MotionEvent motionEvent, AnimConfig... animConfigArr) {
        if (this.mIsEnter) {
            if (LogUtils.isLogMainEnabled()) {
                LogUtils.debug("FolmeHover.onEventExit hoverExit", new Object[0]);
            }
            hoverExit(motionEvent, animConfigArr);
            resetTouchStatus();
        }
    }

    private void resetTouchStatus() {
        this.mIsEnter = false;
    }

    static boolean isOnHoverView(View view, int[] iArr, MotionEvent motionEvent) {
        if (view == null) {
            return true;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        return x >= 0 && x <= view.getWidth() && y >= 0 && y <= view.getHeight();
    }

    @Override // miuix.animation.IHoverStyle
    public void ignoreHoverOf(View view) {
        InnerViewHoverListener innerViewHoverListener = sHoverRecord.get(view);
        if (innerViewHoverListener == null || !innerViewHoverListener.removeHover(this)) {
            return;
        }
        sHoverRecord.remove(view);
    }

    @Override // miuix.animation.IHoverStyle
    public void onMotionEventEx(View view, MotionEvent motionEvent, AnimConfig... animConfigArr) {
        handleMotionEvent(view, motionEvent, animConfigArr);
    }

    @Override // miuix.animation.IHoverStyle
    public void onMotionEvent(MotionEvent motionEvent) {
        handleMotionEvent(null, motionEvent, new AnimConfig[0]);
    }

    @Override // miuix.animation.IHoverStyle
    public void hoverEnter(AnimConfig... animConfigArr) {
        hoverEnterAuto(this.mIsSetAutoTranslation, animConfigArr);
    }

    private void hoverEnterAuto(boolean z, AnimConfig... animConfigArr) {
        this.mIsSetAutoTranslation = z;
        this.mIsEnter = true;
        if (this.mCurrentEffect == IHoverStyle.HoverEffect.FLOATED_WRAPPED) {
            WeakReference<View> weakReference = this.mHoverView;
            View view = weakReference != null ? weakReference.get() : null;
            if (view != null) {
                setMagicView(view, true);
                setWrapped(view, true);
            }
        }
        if (isHideHover()) {
            setMagicView(true);
            setPointerHide(true);
        }
        setCorner(this.mRadius);
        setTintColor();
        AnimConfig[] enterConfig = getEnterConfig(animConfigArr);
        AnimState state = this.mState.getState(IHoverStyle.HoverType.ENTER);
        if (!isScaleSet(IHoverStyle.HoverType.ENTER) && this.mCurrentEffect != IHoverStyle.HoverEffect.NORMAL) {
            IAnimTarget target = this.mState.getTarget();
            float fMax = Math.max(target.getValue(ViewProperty.WIDTH), target.getValue(ViewProperty.HEIGHT));
            double dMin = Math.min((12.0f + fMax) / fMax, DEFAULT_SCALE);
            state.add(ViewProperty.SCALE_X, dMin).add(ViewProperty.SCALE_Y, dMin);
        }
        WeakReference<View> weakReference2 = this.mParentView;
        if (weakReference2 != null) {
            Folme.useAt(weakReference2.get()).state().add((FloatProperty) ViewProperty.SCALE_X, 1.0f).add((FloatProperty) ViewProperty.SCALE_Y, 1.0f).to(enterConfig);
        }
        this.mState.to(state, enterConfig);
    }

    private void hoverEnterToolType(int i, AnimConfig... animConfigArr) {
        if (i == 1 || i == 3 || i == 0) {
            hoverEnter(animConfigArr);
        } else if (i == 4 || i == 2) {
            hoverEnterAuto(false, animConfigArr);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public void hoverEnter(MotionEvent motionEvent, AnimConfig... animConfigArr) {
        hoverEnterToolType(motionEvent.getToolType(0), animConfigArr);
    }

    @Override // miuix.animation.IHoverStyle
    public void hoverMove(View view, MotionEvent motionEvent, AnimConfig... animConfigArr) {
        onEventMove(view, motionEvent, animConfigArr);
    }

    @Override // miuix.animation.IHoverStyle
    public void hoverExit(MotionEvent motionEvent, AnimConfig... animConfigArr) {
        if (this.mParentView != null && !isOnHoverView(this.mHoverView.get(), this.mLocation, motionEvent)) {
            Folme.useAt(this.mParentView.get()).hover().hoverEnter(this.mEnterConfig);
        }
        if (isTranslationSet(IHoverStyle.HoverType.EXIT) && this.mIsSetAutoTranslation) {
            this.mState.getState(IHoverStyle.HoverType.EXIT).add(ViewProperty.TRANSLATION_X, this.mExtraTranslationX).add(ViewProperty.TRANSLATION_Y, 0.0d);
        }
        hoverExit(animConfigArr);
    }

    @Override // miuix.animation.IHoverStyle
    public void hoverExit(AnimConfig... animConfigArr) {
        this.mState.to(this.mState.getState(IHoverStyle.HoverType.EXIT), getExitConfig(animConfigArr));
    }

    @Override // miuix.animation.IHoverStyle
    public void setHoverEnter() {
        setTintColor();
        this.mState.setTo(IHoverStyle.HoverType.ENTER);
    }

    @Override // miuix.animation.IHoverStyle
    public void setHoverExit() {
        this.mState.setTo(IHoverStyle.HoverType.EXIT);
    }

    @Override // miuix.animation.IHoverStyle
    public boolean isMagicView() {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            return isMagicView((View) targetObject);
        }
        return false;
    }

    @Override // miuix.animation.IHoverStyle
    public void setMagicView(boolean z) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setMagicView((View) targetObject, z);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public void setWrapped(boolean z) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setWrapped((View) targetObject, z);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public boolean isWrapped() {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            return isWrapped((View) targetObject);
        }
        return false;
    }

    @Override // miuix.animation.IHoverStyle
    public void setPointerHide(boolean z) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setPointerHide((View) targetObject, z);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public void setPointerShape(Bitmap bitmap) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setPointerShape((View) targetObject, bitmap);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public void setPointerShapeType(int i) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setPointerShapeType((View) targetObject, i);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public int getPointerShapeType() {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            return getPointerShapeType((View) targetObject);
        }
        return -1;
    }

    @Override // miuix.animation.IHoverStyle
    public void setHotXOffset(int i) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setHotXOffset((View) targetObject, i);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public void setHotYOffset(int i) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setHotYOffset((View) targetObject, i);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public void addMagicPoint(Point point) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            addMagicPoint((View) targetObject, point);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public void clearMagicPoint() {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            clearMagicPoint((View) targetObject);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public int getFeedbackColor() {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            return getFeedbackColor((View) targetObject);
        }
        return -1;
    }

    @Override // miuix.animation.IHoverStyle
    public void setFeedbackColor(int i) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setFeedbackColor((View) targetObject, i);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public int getDarkFeedbackColor() {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            return getDarkFeedbackColor((View) targetObject);
        }
        return -1;
    }

    @Override // miuix.animation.IHoverStyle
    public void setDarkFeedbackColor(int i) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setDarkFeedbackColor((View) targetObject, i);
        }
    }

    @Override // miuix.animation.IHoverStyle
    public float getFeedbackRadius() {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            return getFeedbackRadius((View) targetObject);
        }
        return -1.0f;
    }

    @Override // miuix.animation.IHoverStyle
    public void setFeedbackRadius(float f) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            setFeedbackRadius((View) targetObject, f);
        }
    }

    private boolean isScaleSet(IHoverStyle.HoverType hoverType) {
        return Boolean.TRUE.equals(this.mScaleSetMap.get(hoverType));
    }

    private boolean isTranslationSet(IHoverStyle.HoverType hoverType) {
        return Boolean.TRUE.equals(this.mTranslationSetMap.get(hoverType));
    }

    private IHoverStyle.HoverType getType(IHoverStyle.HoverType... hoverTypeArr) {
        return hoverTypeArr.length > 0 ? hoverTypeArr[0] : IHoverStyle.HoverType.ENTER;
    }

    @Override // miuix.animation.controller.FolmeBase, miuix.animation.IStateContainer
    public void clean() {
        super.clean();
        this.mScaleSetMap.clear();
        WeakReference<View> weakReference = this.mHoverView;
        if (weakReference != null) {
            resetView(weakReference);
            this.mHoverView = null;
        }
        WeakReference<View> weakReference2 = this.mChildView;
        if (weakReference2 != null) {
            resetView(weakReference2);
            this.mChildView = null;
        }
        WeakReference<View> weakReference3 = this.mParentView;
        if (weakReference3 != null) {
            resetView(weakReference3);
            this.mParentView = null;
        }
    }

    private void initDist(IAnimTarget iAnimTarget) {
        View targetObject = iAnimTarget instanceof ViewTarget ? ((ViewTarget) iAnimTarget).getTargetObject() : null;
        if (targetObject != null) {
            float fMax = Math.max(iAnimTarget.getValue(ViewProperty.WIDTH), iAnimTarget.getValue(ViewProperty.HEIGHT));
            float fMin = Math.min((12.0f + fMax) / fMax, DEFAULT_SCALE);
            this.mTargetWidth = targetObject.getWidth();
            int height = targetObject.getHeight();
            this.mTargetHeight = height;
            this.mTranslateDist = fMin != 1.0f ? Math.min(Math.min(15.0f, valFromPer(Math.max(0.0f, Math.min(1.0f, perFromVal(this.mTargetWidth - 40, 0.0f, 360.0f))), 15.0f, 0.0f)), Math.min(15.0f, valFromPer(Math.max(0.0f, Math.min(1.0f, perFromVal(height - 40, 0.0f, 360.0f))), 15.0f, 0.0f))) : 0.0f;
            int i = this.mTargetWidth;
            int i2 = this.mTargetHeight;
            if (i == i2 && i < 100 && i2 < 100) {
                setCorner((int) (i * 0.5f));
            } else {
                setCorner(36.0f);
            }
        }
    }

    private void actualTranslateDist(View view, MotionEvent motionEvent) {
        view.getLocationInWindow(this.mLocation);
        float x = this.mLocation[0] + motionEvent.getX();
        float y = this.mLocation[1] + motionEvent.getY();
        float width = this.mLocation[0] + (view.getWidth() * 0.5f);
        float height = this.mLocation[1] + (view.getHeight() * 0.5f);
        float width2 = (x - width) / view.getWidth();
        float height2 = (y - height) / view.getHeight();
        float fMax = Math.max(-1.0f, Math.min(1.0f, width2));
        float fMax2 = Math.max(-1.0f, Math.min(1.0f, height2));
        float f = this.mTranslateDist;
        this.mState.to(this.mState.getState(this.HoverMoveType).add(ViewProperty.TRANSLATION_X, (fMax * (f == Float.MAX_VALUE ? 1.0f : f)) + this.mExtraTranslationX).add(ViewProperty.TRANSLATION_Y, fMax2 * (f != Float.MAX_VALUE ? f : 1.0f)), this.mMoveConfig);
    }

    private View resetView(WeakReference<View> weakReference) {
        View view = weakReference.get();
        if (view != null) {
            view.setOnHoverListener(null);
        }
        return view;
    }

    private AnimConfig[] getEnterConfig(AnimConfig... animConfigArr) {
        return (AnimConfig[]) CommonUtils.mergeArray(animConfigArr, this.mEnterConfig);
    }

    private AnimConfig[] getExitConfig(AnimConfig... animConfigArr) {
        return (AnimConfig[]) CommonUtils.mergeArray(animConfigArr, this.mExitConfig);
    }

    public boolean isHideHover() {
        boolean z;
        return this.mTargetWidth < 100 && this.mTargetHeight < 100 && (!(z = this.mIsSetAutoTranslation) || (z && (this.mCurrentEffect == IHoverStyle.HoverEffect.FLOATED || this.mCurrentEffect == IHoverStyle.HoverEffect.FLOATED_WRAPPED)));
    }

    private static boolean isMagicView(View view) {
        try {
            return ((Boolean) Class.forName("android.view.View").getMethod("isMagicView", new Class[0]).invoke(view, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("", "isMagicView failed , e:" + e.toString());
            return false;
        }
    }

    private static void setMagicView(View view, boolean z) {
        try {
            Class.forName("android.view.View").getMethod("setMagicView", Boolean.TYPE).invoke(view, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("", "setMagicView failed , e:" + e.toString());
        }
    }

    private static void setWrapped(View view, boolean z) {
        try {
            Class.forName("android.view.View").getMethod("setWrapped", Boolean.TYPE).invoke(view, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("", "setWrapped failed , e:" + e.toString());
        }
    }

    private static boolean isWrapped(View view) {
        try {
            return ((Boolean) Class.forName("android.view.View").getMethod("isWrapped", new Class[0]).invoke(view, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("", " isWrapped failed , e:" + e.toString());
            return false;
        }
    }

    private static void setPointerHide(View view, boolean z) {
        try {
            Class.forName("android.view.View").getMethod("setPointerHide", Boolean.TYPE).invoke(view, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("", "setPointerHide failed , e:" + e.toString());
        }
    }

    private static void setPointerShape(View view, Bitmap bitmap) {
        try {
            Class.forName("android.view.View").getMethod("setPointerShape", Bitmap.class).invoke(view, bitmap);
        } catch (Exception e) {
            Log.e("", "setPointerShape failed , e:" + e.toString());
        }
    }

    private static void setPointerShapeType(View view, int i) {
        try {
            Class.forName("android.view.View").getMethod("setPointerShapeType", Integer.TYPE).invoke(view, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e("", "setPointerShapeType failed , e:" + e.toString());
        }
    }

    private static int getPointerShapeType(View view) {
        try {
            return ((Integer) Class.forName("android.view.View").getMethod("getPointerShapeType", new Class[0]).invoke(view, new Object[0])).intValue();
        } catch (Exception e) {
            Log.e("", "getPointerShapeType failed , e:" + e.toString());
            return -1;
        }
    }

    private static void setHotXOffset(View view, int i) {
        try {
            Class.forName("android.view.View").getMethod("setHotXOffset", Integer.TYPE).invoke(view, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e("", "setHotXOffset failed , e:" + e.toString());
        }
    }

    private static void setHotYOffset(View view, int i) {
        try {
            Class.forName("android.view.View").getMethod("setHotYOffset", Integer.TYPE).invoke(view, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e("", "setHotYOffset failed , e:" + e.toString());
        }
    }

    private static void addMagicPoint(View view, Point point) {
        try {
            Class.forName("android.view.View").getMethod("addMagicPoint", Point.class).invoke(view, point);
        } catch (Exception e) {
            Log.e("", "addMagicPoint failed , e:" + e.toString());
        }
    }

    private static void clearMagicPoint(View view) {
        try {
            Class.forName("android.view.View").getMethod("clearMagicPoint", new Class[0]).invoke(view, new Object[0]);
        } catch (Exception e) {
            Log.e("", "clearMagicPoint failed , e:" + e.toString());
        }
    }

    private static int getFeedbackColor(View view) {
        try {
            return ((Integer) Class.forName("android.view.View").getMethod("getFeedbackColor", new Class[0]).invoke(view, new Object[0])).intValue();
        } catch (Exception e) {
            Log.e("", "getFeedbackColor failed , e:" + e.toString());
            return -1;
        }
    }

    private static void setFeedbackColor(View view, int i) {
        try {
            Class.forName("android.view.View").getMethod("setFeedbackColor", Integer.TYPE).invoke(view, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e("", "setFeedbackColor failed , e:" + e.toString());
        }
    }

    private static int getDarkFeedbackColor(View view) {
        try {
            return ((Integer) Class.forName("android.view.View").getMethod("getDarkFeedbackColor", new Class[0]).invoke(view, new Object[0])).intValue();
        } catch (Exception e) {
            Log.e("", "getDarkFeedbackColor failed , e:" + e.toString());
            return -1;
        }
    }

    private static void setDarkFeedbackColor(View view, int i) {
        try {
            Class.forName("android.view.View").getMethod("setDarkFeedbackColor", Integer.TYPE).invoke(view, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e("", "setDarkFeedbackColor failed , e:" + e.toString());
        }
    }

    private static float getFeedbackRadius(View view) {
        try {
            return ((Float) Class.forName("android.view.View").getMethod("getFeedbackRadius", new Class[0]).invoke(view, new Object[0])).floatValue();
        } catch (Exception e) {
            Log.e("", "getFeedbackRadius failed , e:" + e.toString());
            return -1.0f;
        }
    }

    private static void setFeedbackRadius(View view, float f) {
        try {
            Class.forName("android.view.View").getMethod("setFeedbackRadius", Float.TYPE).invoke(view, Float.valueOf(f));
        } catch (Exception e) {
            Log.e("", "setFeedbackRadius failed , e:" + e.toString());
        }
    }
}
