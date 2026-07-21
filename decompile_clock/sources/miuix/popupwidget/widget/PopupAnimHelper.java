package miuix.popupwidget.widget;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.FolmeObject;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ValueProperty;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MiuiBlurUtils;
import miuix.popupwidget.R;
import miuix.smooth.SmoothContainerDrawable2;
import miuix.smooth.SmoothFrameLayout2;

/* JADX INFO: loaded from: classes3.dex */
public class PopupAnimHelper implements FolmeObject {
    private static final float DAMPING = 0.82f;
    private static final int DIM_ENTER_DURATION = 300;
    private static final int DIM_EXIT_DURATION = 150;
    private static final float RESPONSE = 0.33f;
    private static final int SELF_BLUR_ENHANCE_MASK = 512;
    private static final int SELF_BLUR_FREQUENCY_SYNC_APP = 512;
    private final AnimConfig mAnimConfig;
    private boolean mBackgroundBlurEnabled;
    private float mBlur;
    private boolean mBlurEnabled;
    private View mBlurView;
    private final View mContentView;
    private ColorDrawable mDimBackground;
    private View mDimMask;
    private float mDimValue;
    private final AnimConfig mEnterAlphaConfig;
    private Folme.ObjectFolmeImpl mFolmeAnimator;
    private float mFraction;
    private final View mMenuLayer;
    private int mOffsetX;
    private int mOffsetY;
    private OnAnimationListener mOnAnimationListener;
    private PopupOutlineProvider mOutlineProvider;
    private final View mRootView;
    private ScaleListener mScaleListener;
    private final View mSpringBackLayout;
    private float mWindowDim;
    WindowManager.LayoutParams mWindowLayoutParams;
    private static final ValueProperty<PopupAnimHelper> POPUP_FRACTION = new ValueProperty<PopupAnimHelper>("fraction") { // from class: miuix.popupwidget.widget.PopupAnimHelper.1
        @Override // miuix.animation.property.ValueProperty, miuix.animation.property.FloatProperty
        public float getValue(PopupAnimHelper popupAnimHelper) {
            return popupAnimHelper.mFraction;
        }

        @Override // miuix.animation.property.ValueProperty, miuix.animation.property.FloatProperty
        public void setValue(PopupAnimHelper popupAnimHelper, float f) {
            popupAnimHelper.mFraction = f;
        }
    };
    private static final ValueProperty<PopupAnimHelper> POPUP_BLUR = new ValueProperty<PopupAnimHelper>("popupBlur", 0.1f) { // from class: miuix.popupwidget.widget.PopupAnimHelper.2
        @Override // miuix.animation.property.ValueProperty, miuix.animation.property.FloatProperty
        public float getValue(PopupAnimHelper popupAnimHelper) {
            return popupAnimHelper.mBlur;
        }

        @Override // miuix.animation.property.ValueProperty, miuix.animation.property.FloatProperty
        public void setValue(PopupAnimHelper popupAnimHelper, float f) {
            popupAnimHelper.mBlur = f;
        }
    };
    private static final ValueProperty<PopupAnimHelper> POPUP_WINDOW_DIM = new ValueProperty<PopupAnimHelper>("dim") { // from class: miuix.popupwidget.widget.PopupAnimHelper.3
        @Override // miuix.animation.property.ValueProperty, miuix.animation.property.FloatProperty
        public float getValue(PopupAnimHelper popupAnimHelper) {
            return popupAnimHelper.mWindowDim;
        }

        @Override // miuix.animation.property.ValueProperty, miuix.animation.property.FloatProperty
        public void setValue(PopupAnimHelper popupAnimHelper, float f) {
            popupAnimHelper.mWindowDim = f;
        }
    };
    private boolean mInAnimation = false;
    private int mWindowManagerFlags = 2;
    private int mAnimGravity = 0;

    public interface OnAnimationListener {
        void onAnimationEnd();

        void onAnimationStart();

        void onAnimationUpdate();
    }

    public PopupAnimHelper(final View view) {
        View rootView = view.getRootView();
        this.mRootView = rootView;
        this.mSpringBackLayout = rootView.findViewById(R.id.spring_back);
        this.mMenuLayer = rootView.findViewById(R.id.menu_layer);
        this.mEnterAlphaConfig = new AnimConfig().setEase(EaseManager.getStyle(1, 200.0f));
        this.mAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, DAMPING, RESPONSE)).addListeners(new TransitionListener() { // from class: miuix.popupwidget.widget.PopupAnimHelper.4
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                if (PopupAnimHelper.this.mSpringBackLayout instanceof ViewGroup) {
                    ((ViewGroup) PopupAnimHelper.this.mSpringBackLayout).suppressLayout(true);
                }
                view.setLayerType(2, null);
                if (PopupAnimHelper.this.mOnAnimationListener != null) {
                    PopupAnimHelper.this.mOnAnimationListener.onAnimationStart();
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                if (PopupAnimHelper.this.mOnAnimationListener != null) {
                    PopupAnimHelper.this.mOnAnimationListener.onAnimationUpdate();
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (PopupAnimHelper.this.mSpringBackLayout instanceof ViewGroup) {
                    ((ViewGroup) PopupAnimHelper.this.mSpringBackLayout).suppressLayout(false);
                }
                view.setLayerType(0, null);
                PopupAnimHelper.this.mInAnimation = false;
                if (PopupAnimHelper.this.mOnAnimationListener != null) {
                    PopupAnimHelper.this.mOnAnimationListener.onAnimationEnd();
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                view.setLayerType(0, null);
                PopupAnimHelper.this.mInAnimation = false;
                if (PopupAnimHelper.this.mOnAnimationListener != null) {
                    PopupAnimHelper.this.mOnAnimationListener.onAnimationEnd();
                }
            }
        });
        this.mContentView = view;
    }

    @Override // miuix.animation.FolmeObject
    public void setFolmeImpl(Folme.ObjectFolmeImpl objectFolmeImpl) {
        this.mFolmeAnimator = objectFolmeImpl;
    }

    @Override // miuix.animation.FolmeObject
    public Folme.ObjectFolmeImpl folme() {
        return this.mFolmeAnimator;
    }

    public void showWithAnim(final int i) {
        View view = this.mContentView;
        if (view == null || view.getParent() == null) {
            return;
        }
        this.mAnimGravity = i;
        this.mInAnimation = true;
        final int layoutDirection = this.mContentView.getLayoutDirection();
        this.mContentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: miuix.popupwidget.widget.PopupAnimHelper.5
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                PopupAnimHelper.this.mContentView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (!PopupAnimHelper.this.mContentView.isAttachedToWindow() || PopupAnimHelper.this.mContentView.getWidth() == 0 || PopupAnimHelper.this.mContentView.getHeight() == 0) {
                    return true;
                }
                int left = PopupAnimHelper.this.mContentView.getLeft();
                int top = PopupAnimHelper.this.mContentView.getTop();
                int right = PopupAnimHelper.this.mContentView.getRight();
                int bottom = PopupAnimHelper.this.mContentView.getBottom();
                if (PopupAnimHelper.this.mScaleListener != null) {
                    PopupAnimHelper.this.mAnimConfig.removeListeners(PopupAnimHelper.this.mScaleListener);
                }
                PopupAnimHelper.this.mScaleListener = PopupAnimHelper.this.new ScaleListener(new Rect(left, top, right, bottom), i, layoutDirection);
                PopupAnimHelper.this.mAnimConfig.addListeners(PopupAnimHelper.this.mScaleListener);
                PopupAnimHelper.this.mAnimConfig.setSpecial(PopupAnimHelper.POPUP_WINDOW_DIM, FolmeEase.sinOut(300L), new float[0]);
                Folme.use(PopupAnimHelper.this.mContentView).resetTo(ViewProperty.ALPHA, Float.valueOf(0.0f)).to(ViewProperty.ALPHA, Float.valueOf(1.0f), PopupAnimHelper.this.mEnterAlphaConfig);
                AnimState animStateAdd = new AnimState().add(PopupAnimHelper.POPUP_FRACTION, 0.0d).add(PopupAnimHelper.POPUP_WINDOW_DIM, 0.0d);
                AnimState animStateAdd2 = new AnimState("end").add(PopupAnimHelper.POPUP_FRACTION, 1.0d).add(PopupAnimHelper.POPUP_WINDOW_DIM, PopupAnimHelper.this.mDimValue);
                if (PopupAnimHelper.this.isBlurEnabled()) {
                    MiuiBlurUtils.setMiSelfBlurEnhanceFlag(PopupAnimHelper.this.getBlurView(), 512, 512);
                    animStateAdd.add(PopupAnimHelper.POPUP_BLUR, 40.0d);
                    animStateAdd2.add(PopupAnimHelper.POPUP_BLUR, 0.0d);
                    PopupAnimHelper.this.mAnimConfig.setSpecial(PopupAnimHelper.POPUP_BLUR, 1L, 200.0f);
                }
                Folme.use((FolmeObject) PopupAnimHelper.this);
                PopupAnimHelper.this.folme().resetTo(animStateAdd).to(animStateAdd2, PopupAnimHelper.this.mAnimConfig);
                return false;
            }
        });
    }

    public void dismissWithAnim(final Runnable runnable) {
        View view = this.mContentView;
        if (view == null || !view.isAttachedToWindow() || this.mContentView.getParent() == null) {
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        this.mInAnimation = true;
        if (this.mContentView instanceof ViewGroup) {
            Folme.use(this.mContentView).to(ViewProperty.ALPHA, Float.valueOf(0.0f), new AnimConfig().setEase(EaseManager.getStyle(1, 150.0f)).addListeners(new TransitionListener() { // from class: miuix.popupwidget.widget.PopupAnimHelper.6
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    super.onComplete(obj);
                    PopupAnimHelper.this.folme().end();
                }
            }));
            AnimState animStateAdd = new AnimState().add(POPUP_FRACTION, 0.0d);
            ValueProperty<PopupAnimHelper> valueProperty = POPUP_WINDOW_DIM;
            AnimState animStateAdd2 = animStateAdd.add(valueProperty, 0.0d);
            this.mAnimConfig.setSpecial(valueProperty, FolmeEase.sinOut(150L), new float[0]);
            if (isBlurEnabled()) {
                animStateAdd2.add(POPUP_BLUR, 40.0d);
            }
            this.mAnimConfig.addListeners(new TransitionListener() { // from class: miuix.popupwidget.widget.PopupAnimHelper.7
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                    PopupAnimHelper.this.mAnimConfig.removeListeners(this);
                }
            });
            Folme.useValue(this).to(animStateAdd2, this.mAnimConfig);
        }
    }

    public void update(int i) {
        if (this.mInAnimation) {
            return;
        }
        this.mAnimGravity = i;
        Rect rect = new Rect(this.mContentView.getLeft(), this.mContentView.getTop(), this.mContentView.getRight(), this.mContentView.getBottom());
        ScaleListener scaleListener = this.mScaleListener;
        if (scaleListener != null) {
            scaleListener.updateScaleBounds(rect, i, this.mContentView.getLayoutDirection());
        }
    }

    public void setDimValue(float f) {
        this.mDimValue = f;
    }

    public void setWindowManagerFlags(int i) {
        this.mWindowManagerFlags = i;
    }

    public int getAnimGravity() {
        return this.mAnimGravity;
    }

    public void doDimAnimation(final View view, boolean z) {
        if (view == null) {
            return;
        }
        AnimState animState = new AnimState();
        AnimConfig animConfig = new AnimConfig();
        if (z) {
            animState.add(POPUP_WINDOW_DIM, this.mDimValue);
            animConfig.setEase(FolmeEase.sinOut(300L));
        } else {
            animState.add(POPUP_WINDOW_DIM, 0.0d);
            animConfig.setEase(FolmeEase.sinOut(150L));
        }
        animConfig.addListeners(new TransitionListener() { // from class: miuix.popupwidget.widget.PopupAnimHelper.8
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, PopupAnimHelper.POPUP_WINDOW_DIM);
                if (updateInfoFindBy != null) {
                    PopupAnimHelper.this.changeWindowDimAmount(view, updateInfoFindBy.getFloatValue());
                }
            }
        });
        Folme.use((FolmeObject) this).to(animState, animConfig);
    }

    public void setDimMask(View view) {
        this.mDimMask = view;
    }

    public void setBlurView(View view) {
        this.mBlurView = view;
    }

    public View getBlurView() {
        View view = this.mBlurView;
        return view != null ? view : this.mContentView;
    }

    public void setBlurEnabled(boolean z) {
        this.mBlurEnabled = z;
    }

    public boolean isBlurEnabled() {
        return HyperMaterialUtils.isEnable() && this.mBlurEnabled;
    }

    public void setBackgroundBlurEnabled(boolean z) {
        this.mBackgroundBlurEnabled = z;
    }

    public boolean isBackgroundBlurEnabled() {
        return this.mBackgroundBlurEnabled;
    }

    public void setOffset(int i, int i2) {
        this.mOffsetX = i;
        this.mOffsetY = i2;
    }

    public void setOnAnimationListener(OnAnimationListener onAnimationListener) {
        this.mOnAnimationListener = onAnimationListener;
    }

    private class ScaleListener extends TransitionListener {
        private static final int END_RADIUS = 16;
        private static final float SIZE_W = 0.15f;
        private static final int START_RADIUS = 4;
        private ArrayList<Point> mColorModes = new ArrayList<>();
        private float mCurrentDim;
        private int mEndBottom;
        private int mEndHeight;
        private float mEndKGB;
        private int mEndLeft;
        private float mEndRadius;
        private int mEndRight;
        private int mEndTop;
        private int mEndWidth;
        private int mLayerBottom;
        private int mLayerLeft;
        private int mLayerRight;
        private int mLayerTop;
        private View mRootView;
        private int mStartBottom;
        private int mStartHeight;
        private final float mStartKGB;
        private int mStartLeft;
        private float mStartRadius;
        private int mStartRight;
        private int mStartTop;
        private int mStartWidth;
        private float mTargetDim;
        private float mVGrav;

        public ScaleListener(Rect rect, int i, int i2) {
            this.mEndLeft = rect.left;
            this.mEndTop = rect.top;
            this.mEndRight = rect.right;
            this.mEndBottom = rect.bottom;
            Rect startBounds = getStartBounds(rect, i, i2);
            this.mStartLeft = startBounds.left;
            this.mStartTop = startBounds.top;
            this.mStartRight = startBounds.right;
            this.mStartBottom = startBounds.bottom;
            this.mEndWidth = PopupAnimHelper.this.mContentView.getWidth();
            int height = PopupAnimHelper.this.mContentView.getHeight();
            this.mEndHeight = height;
            this.mStartKGB = 0.2f;
            int i3 = this.mEndWidth;
            this.mEndKGB = i3 == 0 ? 0.0f : height / i3;
            int i4 = (int) (i3 * SIZE_W);
            this.mStartWidth = i4;
            this.mStartHeight = (int) (i4 * 0.2f);
            View rootView = PopupAnimHelper.this.mContentView.getRootView();
            this.mRootView = rootView;
            if (rootView.getLayoutParams() instanceof WindowManager.LayoutParams) {
                PopupAnimHelper.this.mWindowLayoutParams = (WindowManager.LayoutParams) this.mRootView.getLayoutParams();
            } else {
                PopupAnimHelper.this.mWindowLayoutParams = null;
            }
            float f = this.mRootView.getContext().getResources().getDisplayMetrics().density;
            this.mStartRadius = 4.0f * f;
            this.mEndRadius = f * 16.0f;
            if (PopupAnimHelper.this.mMenuLayer != null) {
                this.mLayerLeft = PopupAnimHelper.this.mMenuLayer.getLeft();
                this.mLayerTop = PopupAnimHelper.this.mMenuLayer.getTop();
                this.mLayerRight = PopupAnimHelper.this.mMenuLayer.getRight();
                this.mLayerBottom = PopupAnimHelper.this.mMenuLayer.getBottom();
            }
        }

        void updateScaleBounds(Rect rect, int i, int i2) {
            Rect startBounds = getStartBounds(rect, i, i2);
            this.mEndLeft = rect.left;
            this.mEndTop = rect.top;
            this.mEndRight = rect.right;
            this.mEndBottom = rect.bottom;
            this.mStartLeft = startBounds.left;
            this.mStartTop = startBounds.top;
            this.mStartRight = startBounds.right;
            this.mStartBottom = startBounds.bottom;
        }

        private Rect getStartBounds(Rect rect, int i, int i2) {
            int i3;
            int i4;
            int i5;
            int i6;
            int iWidth = rect.width();
            int iHeight = rect.height();
            int i7 = (int) (iWidth * SIZE_W);
            int i8 = i7 / 5;
            int absoluteGravity = Gravity.getAbsoluteGravity(i, i2) & 7;
            this.mEndWidth = iWidth;
            this.mEndHeight = iHeight;
            this.mEndKGB = iWidth == 0 ? 0.0f : iHeight / iWidth;
            if (absoluteGravity == 3) {
                i3 = rect.left;
                i4 = rect.right - (iWidth - i7);
            } else {
                i3 = rect.left + (iWidth - i7);
                i4 = rect.right;
            }
            int absoluteGravity2 = Gravity.getAbsoluteGravity(i, i2) & 112;
            this.mVGrav = absoluteGravity2;
            if (absoluteGravity2 == 48) {
                i5 = rect.top;
                i6 = rect.bottom - (iHeight - i8);
            } else {
                i5 = rect.top + (iHeight - i8);
                i6 = rect.bottom;
            }
            return new Rect(i3, i5, i4, i6);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
            UpdateInfo updateInfoFindBy;
            UpdateInfo updateInfoFindByName = UpdateInfo.findByName(collection, "fraction");
            if (updateInfoFindByName != null) {
                float floatValue = updateInfoFindByName.getFloatValue();
                if (floatValue < 0.0f) {
                    floatValue = 0.0f;
                }
                updateViewBounds(floatValue);
                updateRadiusAnimation(floatValue);
            }
            UpdateInfo updateInfoFindBy2 = UpdateInfo.findBy(collection, PopupAnimHelper.POPUP_WINDOW_DIM);
            if (updateInfoFindBy2 != null) {
                updateDimAmount(updateInfoFindBy2.getFloatValue());
            }
            if (!PopupAnimHelper.this.isBlurEnabled() || (updateInfoFindBy = UpdateInfo.findBy(collection, PopupAnimHelper.POPUP_BLUR)) == null) {
                return;
            }
            updateBlurEffect(updateInfoFindBy.getFloatValue());
        }

        private void updateViewBounds(float f) {
            int i;
            int i2;
            int i3 = this.mStartLeft;
            int i4 = (int) (i3 + ((this.mEndLeft - i3) * f));
            int i5 = this.mStartRight;
            int i6 = (int) (i5 + ((this.mEndRight - i5) * f));
            float f2 = this.mStartKGB;
            float f3 = f2 + ((this.mEndKGB - f2) * f);
            float f4 = i6 - i4;
            int i7 = (int) ((f3 * f4) + 0.5f);
            if (this.mVGrav == 48.0f) {
                i = this.mStartTop;
                i2 = i7 + i;
            } else {
                int i8 = this.mStartBottom;
                i = i8 - i7;
                i2 = i8;
            }
            View view = (View) PopupAnimHelper.this.mSpringBackLayout.getParent();
            if (view == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 29) {
                view.setLeftTopRightBottom(i4, i, i6, i2);
                if (PopupAnimHelper.this.mMenuLayer != null) {
                    PopupAnimHelper.this.mMenuLayer.setLeftTopRightBottom(this.mLayerLeft + PopupAnimHelper.this.mOffsetX, this.mLayerTop + PopupAnimHelper.this.mOffsetY, this.mLayerRight + PopupAnimHelper.this.mOffsetX, this.mLayerBottom + PopupAnimHelper.this.mOffsetY);
                }
            }
            float f5 = f4 / this.mEndWidth;
            PopupAnimHelper.this.mSpringBackLayout.setPivotX(0.0f);
            PopupAnimHelper.this.mSpringBackLayout.setPivotY(0.0f);
            PopupAnimHelper.this.mSpringBackLayout.setScaleX(f5);
            PopupAnimHelper.this.mSpringBackLayout.setScaleY(f5);
        }

        private void updateRadiusAnimation(float f) {
            if (PopupAnimHelper.this.mContentView instanceof SmoothFrameLayout2) {
                float f2 = this.mEndRadius;
                if (f2 != 0.0f) {
                    float f3 = this.mStartRadius;
                    int i = (int) (f3 + ((f2 - f3) * f));
                    float f4 = i;
                    ((SmoothFrameLayout2) PopupAnimHelper.this.mContentView).setCornerRadius(f4);
                    Drawable background = PopupAnimHelper.this.mContentView.getBackground();
                    if (background instanceof SmoothContainerDrawable2) {
                        ((SmoothContainerDrawable2) background).setCornerRadius(f4);
                    }
                    if (PopupAnimHelper.this.isBackgroundBlurEnabled()) {
                        if (PopupAnimHelper.this.mOutlineProvider == null) {
                            PopupAnimHelper.this.mOutlineProvider = new PopupOutlineProvider();
                            PopupAnimHelper.this.mContentView.setOutlineProvider(PopupAnimHelper.this.mOutlineProvider);
                        }
                        PopupAnimHelper.this.mOutlineProvider.setRadius(i);
                    }
                }
            }
        }

        private void updateDimAmount(float f) {
            if (PopupAnimHelper.this.mDimMask == null) {
                PopupAnimHelper.this.changeWindowDimAmount(this.mRootView, f);
            } else {
                PopupAnimHelper popupAnimHelper = PopupAnimHelper.this;
                popupAnimHelper.changeBackgroundDimAmount(popupAnimHelper.mDimMask, f);
            }
        }

        private void updateBlurEffect(float f) {
            View blurView = PopupAnimHelper.this.getBlurView();
            if (blurView != null) {
                MiuiBlurUtils.setSelfBlur(blurView, (int) f, this.mColorModes);
            }
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            if (Build.VERSION.SDK_INT < 29 || !"end".equals(obj)) {
                return;
            }
            ((View) PopupAnimHelper.this.mSpringBackLayout.getParent()).setLeftTopRightBottom(this.mEndLeft, this.mEndTop, this.mEndRight, this.mEndBottom);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeWindowDimAmount(View view, float f) {
        WindowManager.LayoutParams layoutParams;
        WindowManager windowManager;
        if (view == null || !view.isAttachedToWindow() || (layoutParams = this.mWindowLayoutParams) == null) {
            return;
        }
        layoutParams.flags |= this.mWindowManagerFlags;
        this.mWindowLayoutParams.dimAmount = f;
        Context context = view.getContext();
        if (context == null || (windowManager = (WindowManager) context.getSystemService("window")) == null) {
            return;
        }
        windowManager.updateViewLayout(view, this.mWindowLayoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeBackgroundDimAmount(View view, float f) {
        if (view == null) {
            return;
        }
        if (this.mDimBackground == null) {
            ColorDrawable colorDrawable = new ColorDrawable();
            this.mDimBackground = colorDrawable;
            colorDrawable.setColor(ViewCompat.MEASURED_STATE_MASK);
            this.mDimBackground.setAlpha(0);
            view.setBackground(this.mDimBackground);
        }
        this.mDimBackground.setAlpha((int) (f * 255.0f));
    }

    static final class PopupOutlineProvider extends ViewOutlineProvider {
        int radius;

        PopupOutlineProvider() {
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), this.radius);
        }

        public void setRadius(int i) {
            this.radius = i;
        }
    }
}
