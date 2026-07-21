package miuix.animation.controller;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import miuix.animation.FolmeEase;
import miuix.animation.IAnimTarget;
import miuix.animation.ITouchStyle;
import miuix.animation.ViewTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.internal.AnimValueUtils;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.animation.property.ViewPropertyExt;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LogUtils;
import miuix.folme.R;

/* JADX INFO: loaded from: classes2.dex */
public class FolmeTouch extends FolmeBase implements ITouchStyle {
    private static final String ALIAS_TOUCH_DOWN = "touchDown";
    private static final String ALIAS_TOUCH_UP = "touchUp";
    private static final float DEFAULT_SCALE = 0.9f;
    private static final int SCALE_DIS = 10;
    private static WeakHashMap<View, InnerViewTouchListener> sTouchRecord = new WeakHashMap<>();
    private boolean mClearTint;
    private boolean mClickInvoked;
    private View.OnClickListener mClickListener;
    private TransitionListener mDefListener;
    private AnimConfig mDownConfig;
    private int mDownWeight;
    private float mDownX;
    private float mDownY;
    private FolmeFont mFontStyle;
    private Rect mGlobalBoundInWindow;
    private boolean mIsDown;
    private boolean mIsNoScale;
    private WeakReference<View> mListView;
    private int[] mLocationInScreen;
    private boolean mLongClickInvoked;
    private View.OnLongClickListener mLongClickListener;
    private LongClickTask mLongClickTask;
    private Rect mRootGlobalBoundInWindow;
    private float mScaleDist;
    private Map<ITouchStyle.TouchType, Boolean> mScaleSetMap;
    private boolean mSetTint;
    private int mTouchIndex;
    private WeakReference<View> mTouchView;
    private AnimConfig mUpConfig;
    private int mUpWeight;

    public FolmeTouch(IAnimTarget... iAnimTargetArr) {
        super(iAnimTargetArr);
        this.mGlobalBoundInWindow = new Rect();
        this.mRootGlobalBoundInWindow = new Rect();
        this.mLocationInScreen = new int[2];
        this.mScaleSetMap = new ArrayMap();
        this.mDownConfig = new AnimConfig();
        this.mUpConfig = new AnimConfig();
        this.mClearTint = false;
        this.mIsNoScale = false;
        this.mDefListener = new TransitionListener() { // from class: miuix.animation.controller.FolmeTouch.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj, Collection<UpdateInfo> collection) {
                if (obj.equals(ITouchStyle.TouchType.DOWN)) {
                    AnimState.alignState(FolmeTouch.this.mState.getState(ITouchStyle.TouchType.UP), collection);
                }
            }
        };
        initScaleDist(iAnimTargetArr.length > 0 ? iAnimTargetArr[0] : null);
        ViewProperty viewProperty = ViewProperty.SCALE_X;
        ViewProperty viewProperty2 = ViewProperty.SCALE_Y;
        AnimState animState = new AnimState(ITouchStyle.TouchType.UP, ALIAS_TOUCH_UP);
        animState.add(viewProperty, 1.0d).add(viewProperty2, 1.0d);
        this.mState.addState(animState);
        this.mState.addState(new AnimState(ITouchStyle.TouchType.DOWN, ALIAS_TOUCH_DOWN));
        setTintColor();
        this.mDownConfig.setEase(FolmeEase.spring(0.99f, 0.15f));
        this.mDownConfig.addListeners(this.mDefListener);
        this.mUpConfig.setEase(FolmeEase.spring(0.99f, 0.3f)).setSpecial(ViewProperty.ALPHA, FolmeEase.spring(DEFAULT_SCALE, 0.2f), new float[0]);
    }

    private void setTintColor() {
        if (this.mSetTint || this.mClearTint) {
            return;
        }
        int iArgb = Color.argb(30, 0, 0, 0);
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            iArgb = ((View) targetObject).getResources().getColor(R.color.miuix_folme_color_touch_tint);
        }
        ViewPropertyExt.ForegroundProperty foregroundProperty = ViewPropertyExt.FOREGROUND;
        this.mState.getState(ITouchStyle.TouchType.DOWN).add(foregroundProperty, iArgb);
        this.mState.getState(ITouchStyle.TouchType.UP).add(foregroundProperty, 0.0d);
    }

    private void initScaleDist(IAnimTarget iAnimTarget) {
        View targetObject = iAnimTarget instanceof ViewTarget ? ((ViewTarget) iAnimTarget).getTargetObject() : null;
        if (targetObject != null) {
            this.mScaleDist = TypedValue.applyDimension(1, 10.0f, targetObject.getResources().getDisplayMetrics());
        }
    }

    @Override // miuix.animation.controller.FolmeBase, miuix.animation.IStateContainer
    public void clean() {
        super.clean();
        FolmeFont folmeFont = this.mFontStyle;
        if (folmeFont != null) {
            folmeFont.clean();
        }
        this.mScaleSetMap.clear();
        WeakReference<View> weakReference = this.mTouchView;
        if (weakReference != null) {
            resetView(weakReference);
            this.mTouchView = null;
        }
        WeakReference<View> weakReference2 = this.mListView;
        if (weakReference2 != null) {
            View viewResetView = resetView(weakReference2);
            if (viewResetView != null) {
                viewResetView.setTag(R.id.miuix_animation_tag_touch_listener, null);
            }
            this.mListView = null;
        }
        resetTouchStatus();
    }

    private View resetView(WeakReference<View> weakReference) {
        View view = weakReference.get();
        if (view != null) {
            view.setOnTouchListener(null);
        }
        return view;
    }

    public void setFontStyle(FolmeFont folmeFont) {
        this.mFontStyle = folmeFont;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setTintMode(int i) {
        this.mDownConfig.setTintMode(i);
        this.mUpConfig.setTintMode(i);
        return this;
    }

    static boolean isOnTouchView(View view, int[] iArr, Rect rect, Rect rect2, MotionEvent motionEvent) {
        if (view == null) {
            return true;
        }
        view.getGlobalVisibleRect(rect);
        View rootView = view.getRootView();
        if (rootView != null) {
            rootView.getLocationOnScreen(iArr);
            rootView.getGlobalVisibleRect(rect2);
        } else {
            view.getLocationOnScreen(iArr);
            rect2.set(rect);
        }
        return rect.contains(((int) motionEvent.getRawX()) - iArr[0], (((int) motionEvent.getRawY()) - iArr[1]) + rect2.top);
    }

    private boolean setTouchView(View view) {
        WeakReference<View> weakReference = this.mTouchView;
        if ((weakReference != null ? weakReference.get() : null) == view) {
            return false;
        }
        this.mTouchView = new WeakReference<>(view);
        return true;
    }

    @Override // miuix.animation.ITouchStyle
    public void bindViewOfListItem(final View view, final AnimConfig... animConfigArr) {
        if (LogUtils.isLogMoreEnable()) {
            LogUtils.debug("FolmeTouch.bindViewOfListItem for " + view + LogUtils.getStackTrace(5), new Object[0]);
        }
        if (setTouchView(view)) {
            CommonUtils.runOnPreDraw(view, new Runnable() { // from class: miuix.animation.controller.FolmeTouch.2
                @Override // java.lang.Runnable
                public void run() {
                    FolmeTouch.this.bindListView(view, false, animConfigArr);
                }
            });
        }
    }

    @Override // miuix.animation.ITouchStyle
    public void ignoreTouchOf(View view) {
        InnerViewTouchListener innerViewTouchListener = sTouchRecord.get(view);
        if (innerViewTouchListener == null || !innerViewTouchListener.removeTouch(this)) {
            return;
        }
        sTouchRecord.remove(view);
    }

    @Override // miuix.animation.ITouchStyle
    public void handleTouchOf(View view, AnimConfig... animConfigArr) {
        handleTouchOf(view, false, animConfigArr);
    }

    @Override // miuix.animation.ITouchStyle
    public void handleTouchOf(View view, View.OnClickListener onClickListener, AnimConfig... animConfigArr) {
        doHandleTouchOf(view, onClickListener, null, false, animConfigArr);
    }

    @Override // miuix.animation.ITouchStyle
    public void handleTouchOf(View view, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, AnimConfig... animConfigArr) {
        doHandleTouchOf(view, onClickListener, onLongClickListener, false, animConfigArr);
    }

    @Override // miuix.animation.ITouchStyle
    public void handleTouchOf(View view, boolean z, AnimConfig... animConfigArr) {
        doHandleTouchOf(view, null, null, z, animConfigArr);
    }

    @Override // miuix.animation.ITouchStyle
    public void handleNoScaleTouchOf(View view, AnimConfig... animConfigArr) {
        setNoScale(true);
        handleTouchOf(view, false, animConfigArr);
    }

    private void doHandleTouchOf(final View view, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, final boolean z, final AnimConfig... animConfigArr) {
        setClickAndLongClickListener(onClickListener, onLongClickListener);
        handleViewTouch(view, animConfigArr);
        if (setTouchView(view)) {
            if (LogUtils.isLogMainEnabled()) {
                LogUtils.debug("FolmeTouch.doHandleTouchOf for " + view, new Object[0]);
            }
            final boolean zIsClickable = view.isClickable();
            view.setClickable(true);
            CommonUtils.runOnPreDraw(view, new Runnable() { // from class: miuix.animation.controller.FolmeTouch.3
                @Override // java.lang.Runnable
                public void run() {
                    if (z || !FolmeTouch.this.bindListView(view, true, animConfigArr)) {
                        return;
                    }
                    FolmeTouch.this.resetViewTouch(view, zIsClickable);
                }
            });
        }
    }

    private void setClickAndLongClickListener(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        IAnimTarget target = this.mState.getTarget();
        View targetObject = target instanceof ViewTarget ? ((ViewTarget) target).getTargetObject() : null;
        if (targetObject == null) {
            return;
        }
        if (this.mClickListener != null && onClickListener == null) {
            targetObject.setOnClickListener(null);
        } else if (onClickListener != null) {
            targetObject.setOnClickListener(new View.OnClickListener() { // from class: miuix.animation.controller.FolmeTouch.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    FolmeTouch.this.invokeClick(view);
                }
            });
        }
        this.mClickListener = onClickListener;
        if (this.mLongClickListener != null && onLongClickListener == null) {
            targetObject.setOnLongClickListener(null);
        } else if (onLongClickListener != null) {
            targetObject.setOnLongClickListener(new View.OnLongClickListener() { // from class: miuix.animation.controller.FolmeTouch.5
                @Override // android.view.View.OnLongClickListener
                public boolean onLongClick(View view) {
                    if (FolmeTouch.this.mLongClickInvoked) {
                        return false;
                    }
                    FolmeTouch.this.invokeLongClick(view);
                    return true;
                }
            });
        }
        this.mLongClickListener = onLongClickListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean bindListView(View view, boolean z, AnimConfig... animConfigArr) {
        ListViewInfo listViewInfo;
        if (this.mState.getTarget() == null || (listViewInfo = getListViewInfo(view)) == null || listViewInfo.listView == null) {
            return false;
        }
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeTouch.handleListViewTouch for " + view, new Object[0]);
        }
        handleListViewTouch(listViewInfo.listView, view, z, animConfigArr);
        return true;
    }

    private static class ListViewInfo {
        View itemView;
        AbsListView listView;

        private ListViewInfo() {
        }
    }

    private ListViewInfo getListViewInfo(View view) {
        AbsListView absListView = null;
        ListViewInfo listViewInfo = new ListViewInfo();
        for (ViewParent parent = view.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof AbsListView) {
                absListView = (AbsListView) parent;
                break;
            }
            if (parent instanceof View) {
                view = parent;
            }
        }
        if (absListView != null) {
            this.mListView = new WeakReference<>(listViewInfo.listView);
            listViewInfo.listView = absListView;
            listViewInfo.itemView = view;
        }
        return listViewInfo;
    }

    public static ListViewTouchListener getListViewTouchListener(AbsListView absListView) {
        return (ListViewTouchListener) absListView.getTag(R.id.miuix_animation_tag_touch_listener);
    }

    private void handleListViewTouch(AbsListView absListView, View view, boolean z, AnimConfig... animConfigArr) {
        ListViewTouchListener listViewTouchListener = getListViewTouchListener(absListView);
        if (listViewTouchListener == null) {
            listViewTouchListener = new ListViewTouchListener(absListView);
            absListView.setTag(R.id.miuix_animation_tag_touch_listener, listViewTouchListener);
        }
        if (z) {
            absListView.setOnTouchListener(listViewTouchListener);
        }
        listViewTouchListener.putListener(view, new InnerListViewTouchListener(this, animConfigArr));
    }

    private static class InnerListViewTouchListener implements View.OnTouchListener {
        private AnimConfig[] mConfigs;
        private WeakReference<FolmeTouch> mFolmeTouchRef;

        InnerListViewTouchListener(FolmeTouch folmeTouch, AnimConfig... animConfigArr) {
            this.mFolmeTouchRef = new WeakReference<>(folmeTouch);
            this.mConfigs = animConfigArr;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            WeakReference<FolmeTouch> weakReference = this.mFolmeTouchRef;
            FolmeTouch folmeTouch = weakReference == null ? null : weakReference.get();
            if (folmeTouch == null) {
                return false;
            }
            if (motionEvent == null) {
                folmeTouch.onEventUp(this.mConfigs);
                return false;
            }
            folmeTouch.handleMotionEvent(view, motionEvent, this.mConfigs);
            return false;
        }
    }

    private void handleViewTouch(View view, AnimConfig... animConfigArr) {
        InnerViewTouchListener innerViewTouchListener = sTouchRecord.get(view);
        if (innerViewTouchListener == null) {
            innerViewTouchListener = new InnerViewTouchListener();
            sTouchRecord.put(view, innerViewTouchListener);
        }
        view.setOnTouchListener(innerViewTouchListener);
        innerViewTouchListener.addTouch(this, animConfigArr);
    }

    private static class InnerViewTouchListener implements View.OnTouchListener {
        private WeakHashMap<FolmeTouch, AnimConfig[]> mTouchMap;

        private InnerViewTouchListener() {
            this.mTouchMap = new WeakHashMap<>();
        }

        void addTouch(FolmeTouch folmeTouch, AnimConfig... animConfigArr) {
            this.mTouchMap.put(folmeTouch, animConfigArr);
        }

        boolean removeTouch(FolmeTouch folmeTouch) {
            this.mTouchMap.remove(folmeTouch);
            return this.mTouchMap.isEmpty();
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            for (Map.Entry<FolmeTouch, AnimConfig[]> entry : this.mTouchMap.entrySet()) {
                entry.getKey().handleMotionEvent(view, motionEvent, entry.getValue());
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetViewTouch(View view, boolean z) {
        view.setClickable(z);
        view.setOnTouchListener(null);
    }

    @Override // miuix.animation.ITouchStyle
    public void onMotionEventEx(View view, MotionEvent motionEvent, AnimConfig... animConfigArr) {
        handleMotionEvent(view, motionEvent, animConfigArr);
    }

    @Override // miuix.animation.ITouchStyle
    public void onMotionEvent(MotionEvent motionEvent) {
        handleMotionEvent(null, motionEvent, new AnimConfig[0]);
    }

    private void onEventDown(AnimConfig... animConfigArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("FolmeTouch.onEventDown", new Object[0]);
        }
        this.mIsDown = true;
        touchDown(animConfigArr);
    }

    private void onEventMove(MotionEvent motionEvent, View view, AnimConfig... animConfigArr) {
        if (this.mIsDown) {
            if (!isOnTouchView(view, this.mLocationInScreen, this.mGlobalBoundInWindow, this.mRootGlobalBoundInWindow, motionEvent)) {
                touchUp(animConfigArr);
                resetTouchStatus();
            } else {
                if (this.mLongClickTask == null || isInTouchSlop(view, motionEvent)) {
                    return;
                }
                this.mLongClickTask.stop(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onEventUp(AnimConfig... animConfigArr) {
        if (this.mIsDown) {
            if (LogUtils.isLogMainEnabled()) {
                LogUtils.debug("FolmeTouch.onEventUp", new Object[0]);
            }
            touchUp(animConfigArr);
            resetTouchStatus();
        }
    }

    private void resetTouchStatus() {
        LongClickTask longClickTask = this.mLongClickTask;
        if (longClickTask != null) {
            longClickTask.stop(this);
        }
        this.mIsDown = false;
        this.mTouchIndex = 0;
        this.mDownX = 0.0f;
        this.mDownY = 0.0f;
    }

    private void recordDownEvent(MotionEvent motionEvent) {
        if (this.mClickListener == null && this.mLongClickListener == null) {
            return;
        }
        this.mTouchIndex = motionEvent.getActionIndex();
        this.mDownX = motionEvent.getRawX();
        this.mDownY = motionEvent.getRawY();
        this.mClickInvoked = false;
        this.mLongClickInvoked = false;
        startLongClickTask();
    }

    private static final class LongClickTask implements Runnable {
        private WeakReference<FolmeTouch> mTouchRef;

        private LongClickTask() {
        }

        void start(FolmeTouch folmeTouch) {
            View targetObject;
            IAnimTarget target = folmeTouch.mState.getTarget();
            if (!(target instanceof ViewTarget) || (targetObject = ((ViewTarget) target).getTargetObject()) == null) {
                return;
            }
            this.mTouchRef = new WeakReference<>(folmeTouch);
            targetObject.postDelayed(this, ViewConfiguration.getLongPressTimeout());
        }

        void stop(FolmeTouch folmeTouch) {
            View targetObject;
            IAnimTarget target = folmeTouch.mState.getTarget();
            if (!(target instanceof ViewTarget) || (targetObject = ((ViewTarget) target).getTargetObject()) == null) {
                return;
            }
            targetObject.removeCallbacks(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            View view;
            FolmeTouch folmeTouch = this.mTouchRef.get();
            if (folmeTouch != null) {
                IAnimTarget target = folmeTouch.mState.getTarget();
                if (!(target instanceof ViewTarget) || (view = (View) target.getTargetObject()) == null || folmeTouch.mLongClickListener == null) {
                    return;
                }
                view.performLongClick();
                folmeTouch.invokeLongClick(view);
            }
        }
    }

    private void startLongClickTask() {
        if (this.mLongClickListener == null) {
            return;
        }
        if (this.mLongClickTask == null) {
            this.mLongClickTask = new LongClickTask();
        }
        this.mLongClickTask.start(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeLongClick(View view) {
        if (this.mLongClickInvoked) {
            return;
        }
        this.mLongClickInvoked = true;
        this.mLongClickListener.onLongClick(view);
    }

    private void handleClick(View view, MotionEvent motionEvent) {
        View targetObject;
        if (this.mIsDown && this.mClickListener != null && this.mTouchIndex == motionEvent.getActionIndex()) {
            IAnimTarget target = this.mState.getTarget();
            if ((target instanceof ViewTarget) && isInTouchSlop(view, motionEvent) && (targetObject = ((ViewTarget) target).getTargetObject()) != null) {
                targetObject.performClick();
                invokeClick(targetObject);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeClick(View view) {
        if (this.mClickInvoked || this.mLongClickInvoked) {
            return;
        }
        this.mClickInvoked = true;
        this.mClickListener.onClick(view);
    }

    private boolean isInTouchSlop(View view, MotionEvent motionEvent) {
        return CommonUtils.getDistance(this.mDownX, this.mDownY, motionEvent.getRawX(), motionEvent.getRawY()) < ((double) CommonUtils.getTouchSlop(view));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMotionEvent(View view, MotionEvent motionEvent, AnimConfig... animConfigArr) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            recordDownEvent(motionEvent);
            onEventDown(animConfigArr);
            return;
        }
        if (actionMasked == 1) {
            handleClick(view, motionEvent);
        } else if (actionMasked == 2) {
            onEventMove(motionEvent, view, animConfigArr);
            return;
        }
        onEventUp(animConfigArr);
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle useVarFont(TextView textView, int i, int i2, int i3) {
        FolmeFont folmeFont = this.mFontStyle;
        if (folmeFont != null) {
            this.mUpWeight = i2;
            this.mDownWeight = i3;
            folmeFont.useAt(textView, i, i2);
        }
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setAlpha(float f, ITouchStyle.TouchType... touchTypeArr) {
        this.mState.getState(getType(touchTypeArr)).add(ViewProperty.ALPHA, f);
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setScale(float f, ITouchStyle.TouchType... touchTypeArr) {
        ITouchStyle.TouchType type = getType(touchTypeArr);
        this.mScaleSetMap.put(type, true);
        double d = f;
        this.mState.getState(type).add(ViewProperty.SCALE_X, d).add(ViewProperty.SCALE_Y, d);
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setNoScale(boolean z) {
        this.mIsNoScale = z;
        if (z) {
            ITouchStyle.TouchType type = getType(ITouchStyle.TouchType.DOWN);
            this.mState.getState(type).remove(ViewProperty.SCALE_X);
            this.mState.getState(type).remove(ViewProperty.SCALE_Y);
            ITouchStyle.TouchType type2 = getType(ITouchStyle.TouchType.UP);
            this.mState.getState(type2).remove(ViewProperty.SCALE_X);
            this.mState.getState(type2).remove(ViewProperty.SCALE_Y);
        }
        return this;
    }

    private boolean isScaleSet(ITouchStyle.TouchType touchType) {
        return Boolean.TRUE.equals(this.mScaleSetMap.get(touchType));
    }

    private ITouchStyle.TouchType getType(ITouchStyle.TouchType... touchTypeArr) {
        return touchTypeArr.length > 0 ? touchTypeArr[0] : ITouchStyle.TouchType.DOWN;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle clearTintColor() {
        this.mClearTint = true;
        ViewPropertyExt.ForegroundProperty foregroundProperty = ViewPropertyExt.FOREGROUND;
        this.mState.getState(ITouchStyle.TouchType.DOWN).remove(foregroundProperty);
        this.mState.getState(ITouchStyle.TouchType.UP).remove(foregroundProperty);
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setTouchRect(RectF rectF, ITouchStyle.TouchRectGravity touchRectGravity) {
        setTargetValue(miuix.animation.R.id.miuix_animation_tag_view_touch_rect, rectF);
        setTargetValue(miuix.animation.R.id.miuix_animation_tag_view_touch_rect_gravity, touchRectGravity);
        setTargetValue(miuix.animation.R.id.miuix_animation_tag_view_touch_rect_location_mode, 4104);
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setTouchPadding(float f, float f2, float f3, float f4) {
        setTargetValue(miuix.animation.R.id.miuix_animation_tag_view_touch_padding_rect, new RectF(f, f2, f3, f4));
        setTargetValue(miuix.animation.R.id.miuix_animation_tag_view_touch_rect_location_mode, 4);
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setTouchRadius(float f) {
        setTargetValue(miuix.animation.R.id.miuix_animation_tag_view_touch_corners, Float.valueOf(f));
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setTouchRadius(float f, float f2, float f3, float f4) {
        setTargetValue(miuix.animation.R.id.miuix_animation_tag_view_touch_corners, new RectF(f, f2, f3, f4));
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setTint(int i) {
        this.mSetTint = true;
        this.mClearTint = i == 0;
        this.mState.getState(ITouchStyle.TouchType.DOWN).add(ViewPropertyExt.FOREGROUND, i);
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setTint(float f, float f2, float f3, float f4) {
        return setTint(Color.argb((int) (f * 255.0f), (int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f)));
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setBackgroundColor(int i) {
        ViewPropertyExt.BackgroundProperty backgroundProperty = ViewPropertyExt.BACKGROUND;
        this.mState.getState(ITouchStyle.TouchType.DOWN).add(backgroundProperty, i);
        this.mState.getState(ITouchStyle.TouchType.UP).add(backgroundProperty, (int) AnimValueUtils.getValueOfTarget(this.mState.getTarget(), backgroundProperty, 0.0d));
        return this;
    }

    @Override // miuix.animation.ITouchStyle
    public ITouchStyle setBackgroundColor(float f, float f2, float f3, float f4) {
        return setBackgroundColor(Color.argb((int) (f * 255.0f), (int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f)));
    }

    @Override // miuix.animation.ITouchStyle
    public void touchDown(AnimConfig... animConfigArr) {
        setHoverCorner(0.0f);
        setTintColor();
        AnimConfig[] downConfig = getDownConfig(animConfigArr);
        FolmeFont folmeFont = this.mFontStyle;
        if (folmeFont != null) {
            folmeFont.to(this.mDownWeight, downConfig);
        }
        AnimState state = this.mState.getState(ITouchStyle.TouchType.DOWN);
        if (!isScaleSet(ITouchStyle.TouchType.DOWN) && !this.mIsNoScale) {
            IAnimTarget target = this.mState.getTarget();
            float fMax = Math.max(target.getValue(ViewProperty.WIDTH), target.getValue(ViewProperty.HEIGHT));
            double dMax = Math.max((fMax - this.mScaleDist) / fMax, DEFAULT_SCALE);
            state.add(ViewProperty.SCALE_X, dMax).add(ViewProperty.SCALE_Y, dMax);
        }
        this.mState.to(state, downConfig);
    }

    @Override // miuix.animation.ITouchStyle
    public void touchUp(AnimConfig... animConfigArr) {
        AnimConfig[] upConfig = getUpConfig(animConfigArr);
        FolmeFont folmeFont = this.mFontStyle;
        if (folmeFont != null) {
            folmeFont.to(this.mUpWeight, upConfig);
        }
        this.mState.to(this.mState.getState(ITouchStyle.TouchType.UP), upConfig);
    }

    @Override // miuix.animation.controller.FolmeBase, miuix.animation.ICancelableStyle
    public void cancel() {
        super.cancel();
        FolmeFont folmeFont = this.mFontStyle;
        if (folmeFont != null) {
            folmeFont.cancel();
        }
    }

    @Override // miuix.animation.ITouchStyle
    public void setTouchDown() {
        setTintColor();
        this.mState.setTo(ITouchStyle.TouchType.DOWN);
    }

    @Override // miuix.animation.ITouchStyle
    public void setTouchUp() {
        this.mState.setTo(ITouchStyle.TouchType.UP);
    }

    private AnimConfig[] getDownConfig(AnimConfig... animConfigArr) {
        return (AnimConfig[]) CommonUtils.mergeArray(animConfigArr, this.mDownConfig);
    }

    private AnimConfig[] getUpConfig(AnimConfig... animConfigArr) {
        return (AnimConfig[]) CommonUtils.mergeArray(animConfigArr, this.mUpConfig);
    }

    private void setTargetValue(int i, Object obj) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            ((View) targetObject).setTag(i, obj);
        }
    }

    private void setHoverCorner(float f) {
        Object targetObject = this.mState.getTarget().getTargetObject();
        if (targetObject instanceof View) {
            ((View) targetObject).setTag(miuix.animation.R.id.miuix_animation_tag_view_hover_corners, Float.valueOf(f));
        }
    }
}
