package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.view.ViewCompat;
import com.android.deskclock.R2;
import java.lang.ref.WeakReference;
import java.util.Collection;
import miuix.androidbasewidget.R;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.FolmeObject;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.physics.DynamicAnimation;
import miuix.animation.physics.SpringAnimation;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IntValueProperty;
import miuix.util.HapticFeedbackCompat;
import miuix.view.CompatViewMethod;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class SeekBar extends AppCompatSeekBar {
    private static final int ALPHA_70_PERCENT = 178;
    private static final int NO_ALPHA = 255;
    private static final IntValueProperty PROPERTY_DRAW_PROGRESS = new IntValueProperty("drawProgress", 0.001f);
    private static final IntValueProperty PROPERTY_PROGRESS = new IntValueProperty("progress", 0.1f);
    private static final String TAG = "SeekBar";
    private Drawable mBackgroundDrawable;
    private int mBackgroundPrimaryColor;
    private int mBackgroundPrimaryDisableColor;
    private boolean mBalanceEnabled;
    private int mBalanceProgress;
    private int mDefaultBackgroundPrimaryColor;
    private int mDefaultBackgroundPrimaryDisableColor;
    private int mDefaultForegroundPrimaryColor;
    private int mDefaultForegroundPrimaryDisableColor;
    private int mDefaultIconPrimaryColor;
    private int mDefaultProgressPrimaryColor;
    private int mDefaultProgressPrimaryDisableColor;
    private int mDefaultScalePrimaryColor;
    private int mDefaultScaleSecondaryColor;
    private float mDisabledProgressAlpha;
    private float mDraggableMaxPercentProcess;
    private float mDraggableMinPercentProgress;
    private float mDrawProgress;
    private final ProgressAnimTarget mDrawProgressAnimator;
    private int mForegroundPrimaryColor;
    private int mForegroundPrimaryDisableColor;
    private boolean mHasEdgeReached;
    private int mIconPrimaryColor;
    private int mIconTransparent;
    private boolean mIsDragAnimationEnabled;
    private boolean mIsDragging;
    private boolean mIsInMiddle;
    private boolean mIsThumbNeedAnimation;
    private boolean mIsThumbTheme;
    private boolean mIsTouchAnimationEnabled;
    private boolean mIsTouchUpEvent;
    private boolean mIsUseCustomDrawables;
    private Drawable mLayerDrawable;
    private float mMaxMiddle;
    private boolean mMiddleEnabled;
    private float mMinMiddle;
    private android.widget.SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private Drawable mOvalDrawable;
    private Paint mPaint;
    private int mProgress;
    private final ProgressAnimTarget mProgressAnimator;
    private ColorStateList mProgressColorStateList;
    private Drawable mProgressDrawable;
    private int mProgressPrimaryColor;
    private int mProgressPrimaryDisableColor;
    private boolean mScaleEnabled;
    private float mScaleRadius;
    private int mScaledTouchSlop;
    private int mThumbDrawOvalHeight;
    private int mThumbDrawOvalWidth;
    private Drawable mThumbDrawable;
    private int mThumbHeight;
    private int mThumbOvalHeight;
    private int mThumbOvalWidth;
    private SpringAnimation mThumbPressedAnim;
    private float mThumbPressedScale;
    private SpringAnimation mThumbPressedUpAnim;
    private Rect mThumbRect;
    private int mThumbWidth;
    private float mTouchDownX;
    private final android.widget.SeekBar.OnSeekBarChangeListener mTrainsOnSeekBarChangeListener;

    private void init() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        if (this.mIsThumbNeedAnimation) {
            float f = this.mThumbOvalWidth;
            float f2 = this.mThumbPressedScale;
            this.mThumbOvalWidth = (int) (f * f2);
            this.mThumbOvalHeight = (int) (this.mThumbOvalHeight * f2);
        }
        this.mScaleRadius = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_seekbar_icon_size) / 2.0f;
        setThumb(null);
    }

    public float getThumbScale() {
        return this.mThumbPressedScale;
    }

    public void setThumbScale(float f) {
        this.mThumbPressedScale = f;
        int i = (int) (this.mThumbOvalWidth * f);
        this.mThumbDrawOvalWidth = i;
        int i2 = (int) (this.mThumbOvalHeight * f);
        this.mThumbDrawOvalHeight = i2;
        Drawable drawable = this.mOvalDrawable;
        if (drawable == null || !(drawable instanceof GradientDrawable)) {
            return;
        }
        ((GradientDrawable) drawable).setSize(i, i2);
        LayerDrawable layerDrawable = (LayerDrawable) this.mThumbDrawable;
        layerDrawable.setDrawable(0, this.mOvalDrawable);
        setThumb(layerDrawable);
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int i) {
        super.setProgress(i);
        if (this.mIsThumbTheme && i >= getMinWrapper() && i <= getMax()) {
            this.mProgress = i;
            this.mDrawProgressAnimator.folme().setTo(PROPERTY_DRAW_PROGRESS, Integer.valueOf(i));
            this.mProgressAnimator.folme().setTo(PROPERTY_PROGRESS, Integer.valueOf(i));
        }
    }

    public void setDraggedAnimationEnable(boolean z) {
        this.mIsDragAnimationEnabled = z;
    }

    public void setTouchAnimationEnable(boolean z) {
        this.mIsTouchAnimationEnabled = z;
    }

    @Override // android.widget.AbsSeekBar
    public void setThumb(Drawable drawable) {
        super.setThumb(drawable);
        if (!this.mIsThumbTheme || drawable == null) {
            return;
        }
        this.mThumbDrawable = drawable;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        this.mThumbWidth = intrinsicWidth;
        if (intrinsicWidth % 2 != 0) {
            this.mThumbWidth = intrinsicWidth + 1;
        }
        this.mThumbHeight = this.mThumbDrawable.getIntrinsicHeight();
    }

    public SeekBar(Context context) {
        this(context, null);
    }

    public SeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.seekBarStyle);
    }

    public SeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsThumbTheme = true;
        this.mThumbPressedScale = 1.0f;
        this.mIsTouchUpEvent = false;
        android.widget.SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new android.widget.SeekBar.OnSeekBarChangeListener() { // from class: miuix.androidbasewidget.widget.SeekBar.2
            private HapticFeedbackCompat mHapticFeedbackCompat;

            private HapticFeedbackCompat getHapticFeedbackCompat() {
                if (this.mHapticFeedbackCompat == null) {
                    this.mHapticFeedbackCompat = new HapticFeedbackCompat(SeekBar.this.getContext());
                }
                return this.mHapticFeedbackCompat;
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(android.widget.SeekBar seekBar, int i2, boolean z) {
                boolean z2 = true;
                if (!SeekBar.this.mIsThumbTheme && SeekBar.this.mMiddleEnabled && z) {
                    int max = SeekBar.this.getMax() - SeekBar.this.getMinWrapper();
                    float f = max;
                    int iRound = Math.round(f * 0.5f);
                    float minWrapper = max > 0 ? (i2 - SeekBar.this.getMinWrapper()) / f : 0.0f;
                    if (minWrapper <= SeekBar.this.mMinMiddle || minWrapper >= SeekBar.this.mMaxMiddle) {
                        SeekBar.this.mProgress = i2;
                        SeekBar.this.mProgressAnimator.folme().setTo(SeekBar.PROPERTY_PROGRESS, Integer.valueOf(SeekBar.this.mProgress));
                    } else {
                        SeekBar.this.mProgress = iRound;
                    }
                    if (SeekBar.this.getProgress() != SeekBar.this.mProgress) {
                        SeekBar.this.mProgressAnimator.folme().to(SeekBar.PROPERTY_PROGRESS, Integer.valueOf(SeekBar.this.mProgress), new AnimConfig().setEase(FolmeEase.spring(0.9f, 0.15f)).addListeners(new TransitionListener() { // from class: miuix.androidbasewidget.widget.SeekBar.2.1
                            @Override // miuix.animation.listener.TransitionListener
                            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                                UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, SeekBar.PROPERTY_PROGRESS);
                                if (updateInfoFindBy != null) {
                                    SeekBar.this.setProgress(updateInfoFindBy.getIntValue());
                                }
                            }
                        }));
                    }
                }
                SeekBar seekBar2 = SeekBar.this;
                int progressForm = seekBar2.getProgressForm(seekBar2.mDraggableMinPercentProgress);
                SeekBar seekBar3 = SeekBar.this;
                int progressForm2 = seekBar3.getProgressForm(seekBar3.mDraggableMaxPercentProcess);
                if (i2 < progressForm) {
                    SeekBar.this.setProgress(progressForm);
                    i2 = progressForm;
                } else if (i2 > progressForm2) {
                    SeekBar.this.setProgress(progressForm2);
                    i2 = progressForm2;
                }
                if (SeekBar.this.mIsThumbTheme) {
                    if (z) {
                        if (SeekBar.this.mMiddleEnabled) {
                            int max2 = SeekBar.this.getMax() - SeekBar.this.getMinWrapper();
                            float f2 = max2;
                            int iRound2 = Math.round(0.5f * f2) + SeekBar.this.getMinWrapper();
                            float minWrapper2 = max2 > 0 ? (i2 - SeekBar.this.getMinWrapper()) / f2 : 0.0f;
                            if (minWrapper2 > SeekBar.this.mMinMiddle && minWrapper2 < SeekBar.this.mMaxMiddle) {
                                i2 = iRound2;
                            }
                        }
                        if (SeekBar.this.mIsTouchUpEvent) {
                            SeekBar.this.mProgress = i2;
                            if (SeekBar.this.mIsTouchAnimationEnabled) {
                                SeekBar.this.mDrawProgressAnimator.folme().to(SeekBar.PROPERTY_DRAW_PROGRESS, Integer.valueOf(i2), new AnimConfig().setEase(FolmeEase.spring(0.96f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.androidbasewidget.widget.SeekBar.2.2
                                    @Override // miuix.animation.listener.TransitionListener
                                    public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                                        UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, SeekBar.PROPERTY_DRAW_PROGRESS);
                                        if (updateInfoFindBy != null) {
                                            SeekBar.this.mDrawProgress = updateInfoFindBy.getFloatValue();
                                            SeekBar.this.invalidate();
                                        }
                                    }
                                }));
                            } else {
                                SeekBar seekBar4 = SeekBar.this;
                                seekBar4.mDrawProgress = seekBar4.mProgress;
                                SeekBar.this.mDrawProgressAnimator.folme().setTo(SeekBar.PROPERTY_DRAW_PROGRESS, Float.valueOf(SeekBar.this.mDrawProgress));
                            }
                        } else {
                            SeekBar.this.mProgress = i2;
                            if (SeekBar.this.mIsDragAnimationEnabled || SeekBar.this.mMiddleEnabled) {
                                SeekBar.this.mDrawProgressAnimator.folme().to(SeekBar.PROPERTY_DRAW_PROGRESS, Integer.valueOf(i2), new AnimConfig().setEase(FolmeEase.spring(0.9f, 0.15f)).addListeners(new TransitionListener() { // from class: miuix.androidbasewidget.widget.SeekBar.2.3
                                    @Override // miuix.animation.listener.TransitionListener
                                    public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                                        UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, SeekBar.PROPERTY_DRAW_PROGRESS);
                                        if (updateInfoFindBy != null) {
                                            SeekBar.this.mDrawProgress = updateInfoFindBy.getFloatValue();
                                            SeekBar.this.mDrawProgress = Math.max(SeekBar.this.getMinWrapper(), Math.min(SeekBar.this.getMax(), SeekBar.this.mDrawProgress));
                                            SeekBar.this.invalidate();
                                        }
                                    }

                                    @Override // miuix.animation.listener.TransitionListener
                                    public void onComplete(Object obj) {
                                        if (SeekBar.this.getProgress() != SeekBar.this.mProgress) {
                                            SeekBar.this.setProgress(SeekBar.this.mProgress);
                                        }
                                    }
                                }));
                            } else {
                                SeekBar seekBar5 = SeekBar.this;
                                seekBar5.mDrawProgress = seekBar5.mProgress;
                                SeekBar.this.mDrawProgressAnimator.folme().setTo(SeekBar.PROPERTY_DRAW_PROGRESS, Float.valueOf(SeekBar.this.mDrawProgress));
                            }
                        }
                        SeekBar.this.mIsTouchUpEvent = false;
                    } else {
                        SeekBar.this.mDrawProgress = i2;
                    }
                }
                if (i2 != progressForm && i2 != progressForm2) {
                    z2 = false;
                }
                if (z) {
                    if (!z2 || SeekBar.this.mHasEdgeReached) {
                        if (!SeekBar.this.mHasEdgeReached && HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2)) {
                            HapticCompat.performHapticFeedback(seekBar, HapticFeedbackConstants.MIUI_GEAR_LIGHT);
                        }
                    } else if (!HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2)) {
                        HapticCompat.performHapticFeedback(seekBar, HapticFeedbackConstants.MIUI_MESH_NORMAL);
                    } else if (i2 == progressForm2) {
                        getHapticFeedbackCompat().m1943x85658b2f(R2.attr.actionBarRefreshIcon);
                    } else {
                        getHapticFeedbackCompat().m1943x85658b2f(R2.attr.actionBarPopupTheme);
                    }
                }
                SeekBar.this.mHasEdgeReached = z2;
                if (SeekBar.this.mOnSeekBarChangeListener != null) {
                    SeekBar.this.mOnSeekBarChangeListener.onProgressChanged(seekBar, i2, z);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
                if (SeekBar.this.mOnSeekBarChangeListener != null) {
                    SeekBar.this.mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                if (SeekBar.this.mOnSeekBarChangeListener != null) {
                    SeekBar.this.mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        };
        this.mTrainsOnSeekBarChangeListener = onSeekBarChangeListener;
        CompatViewMethod.setForceDarkAllowed(this, false);
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SeekBar, i, R.style.Widget_SeekBar_Thumb_DayNight);
        this.mIsUseCustomDrawables = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SeekBar_useCustomDrawables, false);
        if (getThumb() == null || this.mIsUseCustomDrawables) {
            this.mIsThumbTheme = false;
        } else {
            this.mIsThumbTheme = true;
            Drawable thumb = getThumb();
            this.mThumbDrawable = thumb;
            if ((thumb instanceof LayerDrawable) && ((LayerDrawable) thumb).getNumberOfLayers() > 0 && ((LayerDrawable) this.mThumbDrawable).getDrawable(0) != null) {
                this.mIsThumbNeedAnimation = true;
                Drawable drawable = ((LayerDrawable) this.mThumbDrawable).getDrawable(0);
                if (drawable instanceof GradientDrawable) {
                    GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                    this.mOvalDrawable = gradientDrawable;
                    this.mThumbOvalWidth = gradientDrawable.getIntrinsicWidth();
                    this.mThumbOvalHeight = this.mOvalDrawable.getIntrinsicHeight();
                }
            }
            this.mThumbWidth = this.mThumbDrawable.getIntrinsicWidth();
            this.mThumbHeight = this.mThumbDrawable.getIntrinsicHeight();
            setThumb(null);
        }
        this.mDefaultForegroundPrimaryColor = context.getResources().getColor(R.color.miuix_appcompat_progress_primary_colors_light);
        this.mDefaultForegroundPrimaryDisableColor = context.getResources().getColor(R.color.miuix_appcompat_progress_disable_color_light);
        this.mDefaultProgressPrimaryColor = context.getResources().getColor(R.color.miuix_appcompat_progress_primary_colors_light);
        this.mDefaultProgressPrimaryDisableColor = context.getResources().getColor(R.color.miuix_appcompat_progress_disable_color_light);
        this.mDefaultBackgroundPrimaryColor = context.getResources().getColor(R.color.miuix_appcompat_seekbar_background_normal_color);
        this.mDefaultBackgroundPrimaryDisableColor = context.getResources().getColor(R.color.miuix_appcompat_seekbar_background_disabled_color);
        this.mDefaultScalePrimaryColor = context.getResources().getColor(R.color.miuix_appcompat_seekbar_scale_primary_color);
        this.mDefaultScaleSecondaryColor = context.getResources().getColor(R.color.miuix_appcompat_seekbar_scale_secondary_color);
        this.mDefaultIconPrimaryColor = context.getResources().getColor(R.color.miuix_appcompat_progress_background_icon_light);
        this.mMiddleEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SeekBar_middleEnabled, false);
        this.mScaleEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SeekBar_scaleEnable, false);
        this.mBalanceEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SeekBar_balanceEnable, false);
        this.mForegroundPrimaryColor = typedArrayObtainStyledAttributes.getColor(R.styleable.SeekBar_foregroundPrimaryColor, this.mDefaultForegroundPrimaryColor);
        this.mForegroundPrimaryDisableColor = typedArrayObtainStyledAttributes.getColor(R.styleable.SeekBar_foregroundPrimaryDisableColor, this.mDefaultForegroundPrimaryDisableColor);
        this.mProgressPrimaryColor = typedArrayObtainStyledAttributes.getColor(R.styleable.SeekBar_progressPrimaryColor, this.mDefaultProgressPrimaryColor);
        this.mProgressPrimaryDisableColor = typedArrayObtainStyledAttributes.getColor(R.styleable.SeekBar_progressPrimaryDisableColor, this.mDefaultProgressPrimaryDisableColor);
        this.mBackgroundPrimaryColor = typedArrayObtainStyledAttributes.getColor(R.styleable.SeekBar_backgroundPrimaryColor, this.mDefaultBackgroundPrimaryColor);
        this.mBackgroundPrimaryDisableColor = typedArrayObtainStyledAttributes.getColor(R.styleable.SeekBar_backgroundPrimaryDisableColor, this.mDefaultBackgroundPrimaryDisableColor);
        this.mIconPrimaryColor = typedArrayObtainStyledAttributes.getColor(R.styleable.SeekBar_iconPrimaryColor, this.mDefaultIconPrimaryColor);
        this.mDisabledProgressAlpha = typedArrayObtainStyledAttributes.getFloat(R.styleable.SeekBar_disabledProgressAlpha, 0.5f);
        this.mMinMiddle = typedArrayObtainStyledAttributes.getFloat(R.styleable.SeekBar_minMiddle, 0.46f);
        this.mMaxMiddle = typedArrayObtainStyledAttributes.getFloat(R.styleable.SeekBar_maxMiddle, 0.54f);
        this.mDraggableMinPercentProgress = getValueFromTypedArray(typedArrayObtainStyledAttributes, R.styleable.SeekBar_draggableMinPercentProgress, 0.0f);
        this.mDraggableMaxPercentProcess = getValueFromTypedArray(typedArrayObtainStyledAttributes, R.styleable.SeekBar_draggableMaxPercentProgress, 1.0f);
        this.mIsDragAnimationEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SeekBar_dragAnimationEnable, true);
        this.mIsTouchAnimationEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SeekBar_touchAnimationEnable, true);
        this.mBalanceProgress = typedArrayObtainStyledAttributes.getInteger(R.styleable.SeekBar_balanceProgress, 0);
        if (getProgressDrawable() != null) {
            Drawable progressDrawable = getProgressDrawable();
            this.mLayerDrawable = progressDrawable;
            if (progressDrawable instanceof LayerDrawable) {
                LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
                this.mBackgroundDrawable = layerDrawable.findDrawableByLayerId(android.R.id.background);
                this.mProgressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);
            }
        }
        setDraggableMinPercentProgress(this.mDraggableMinPercentProgress);
        setDraggableMaxPercentProcess(this.mDraggableMaxPercentProcess);
        typedArrayObtainStyledAttributes.recycle();
        this.mIconTransparent = context.getResources().getColor(R.color.miuix_appcompat_transparent);
        float f = this.mMinMiddle;
        if (f > 0.5f || f < 0.0f) {
            this.mMinMiddle = 0.46f;
        }
        float f2 = this.mMaxMiddle;
        if (f2 < 0.5f || f2 > 1.0f) {
            this.mMaxMiddle = 0.54f;
        }
        this.mProgress = getProgress();
        this.mDrawProgress = getProgress();
        ProgressAnimTarget progressAnimTarget = new ProgressAnimTarget();
        this.mDrawProgressAnimator = progressAnimTarget;
        ProgressAnimTarget progressAnimTarget2 = new ProgressAnimTarget();
        this.mProgressAnimator = progressAnimTarget2;
        Folme.use((FolmeObject) progressAnimTarget);
        Folme.use((FolmeObject) progressAnimTarget2);
        progressAnimTarget.folme().setTo(PROPERTY_DRAW_PROGRESS, Float.valueOf(this.mDrawProgress));
        progressAnimTarget2.folme().setTo(PROPERTY_PROGRESS, Integer.valueOf(this.mProgress));
        setOnSeekBarChangeListener(onSeekBarChangeListener);
        post(new ColorUpdateRunner(this));
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
        if (this.mMiddleEnabled) {
            int max = getMax() - getMinWrapper();
            boolean zIsInMiddle = isInMiddle(max, getProgress());
            this.mIsInMiddle = zIsInMiddle;
            if (zIsInMiddle) {
                int iRound = Math.round(max * 0.5f) + getMinWrapper();
                this.mProgress = iRound;
                setProgress(iRound);
            }
        }
        if (!this.mIsThumbTheme || this.mThumbDrawable == null) {
            return;
        }
        init();
        if (this.mIsThumbNeedAnimation) {
            initAnim();
        }
    }

    public void initAnim() {
        FloatProperty<SeekBar> floatProperty = new FloatProperty<SeekBar>("ThumbScale") { // from class: miuix.androidbasewidget.widget.SeekBar.1
            @Override // miuix.animation.property.FloatProperty
            public float getValue(SeekBar seekBar) {
                return seekBar.getThumbScale();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(SeekBar seekBar, float f) {
                seekBar.setThumbScale(f);
            }
        };
        SpringAnimation springAnimation = new SpringAnimation(this, floatProperty, 1.127f);
        this.mThumbPressedAnim = springAnimation;
        springAnimation.getSpring().setStiffness(986.96f);
        this.mThumbPressedAnim.getSpring().setDampingRatio(0.6f);
        this.mThumbPressedAnim.setMinimumVisibleChange(0.002f);
        this.mThumbPressedAnim.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: miuix.androidbasewidget.widget.SeekBar$$ExternalSyntheticLambda0
            @Override // miuix.animation.physics.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                this.f$0.m1777lambda$initAnim$0$miuixandroidbasewidgetwidgetSeekBar(dynamicAnimation, f, f2);
            }
        });
        SpringAnimation springAnimation2 = new SpringAnimation(this, floatProperty, 1.0f);
        this.mThumbPressedUpAnim = springAnimation2;
        springAnimation2.getSpring().setStiffness(986.96f);
        this.mThumbPressedUpAnim.getSpring().setDampingRatio(0.6f);
        this.mThumbPressedUpAnim.setMinimumVisibleChange(0.002f);
        this.mThumbPressedUpAnim.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: miuix.androidbasewidget.widget.SeekBar$$ExternalSyntheticLambda1
            @Override // miuix.animation.physics.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                this.f$0.m1778lambda$initAnim$1$miuixandroidbasewidgetwidgetSeekBar(dynamicAnimation, f, f2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initAnim$0$miuix-androidbasewidget-widget-SeekBar, reason: not valid java name */
    /* synthetic */ void m1777lambda$initAnim$0$miuixandroidbasewidgetwidgetSeekBar(DynamicAnimation dynamicAnimation, float f, float f2) {
        invalidate();
    }

    /* JADX INFO: renamed from: lambda$initAnim$1$miuix-androidbasewidget-widget-SeekBar, reason: not valid java name */
    /* synthetic */ void m1778lambda$initAnim$1$miuixandroidbasewidgetwidgetSeekBar(DynamicAnimation dynamicAnimation, float f, float f2) {
        invalidate();
    }

    @Override // android.widget.ProgressBar
    public void setProgressDrawable(Drawable drawable) {
        super.setProgressDrawable(drawable);
        if (this.mIsThumbTheme) {
            this.mLayerDrawable = drawable;
            if (drawable instanceof LayerDrawable) {
                LayerDrawable layerDrawable = (LayerDrawable) drawable;
                this.mBackgroundDrawable = layerDrawable.findDrawableByLayerId(android.R.id.background);
                this.mProgressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);
            } else {
                this.mBackgroundDrawable = null;
                this.mProgressDrawable = null;
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:101:0x0286 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:103:0x028f A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:104:0x02a8 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:106:0x02b7 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:108:0x02c7 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:109:0x02d6 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:111:0x02e7 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:114:0x02f9 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:70:0x01ea  */
    /* JADX WARN: Code duplicated, block: B:73:0x01f0 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:75:0x020a A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:77:0x020e A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:79:0x0215 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:80:0x021d  */
    /* JADX WARN: Code duplicated, block: B:82:0x0223 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:84:0x022a A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:86:0x0233 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:87:0x0239 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:89:0x023e A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:90:0x024e A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:92:0x0256 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:93:0x0261 A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:99:0x027f A[Catch: all -> 0x031a, TryCatch #0 {, blocks: (B:4:0x0005, B:6:0x0009, B:8:0x000d, B:10:0x0013, B:11:0x001b, B:12:0x0022, B:16:0x002c, B:18:0x0037, B:19:0x003b, B:21:0x004c, B:23:0x006b, B:24:0x008d, B:26:0x00ae, B:29:0x00b4, B:32:0x00fa, B:34:0x010f, B:43:0x013e, B:45:0x0160, B:48:0x0166, B:52:0x016e, B:62:0x01c0, B:53:0x0178, B:54:0x0183, B:55:0x0195, B:59:0x019d, B:60:0x01a7, B:61:0x01b2, B:63:0x01c5, B:65:0x01c9, B:67:0x01cd, B:69:0x01d9, B:71:0x01ec, B:73:0x01f0, B:75:0x020a, B:77:0x020e, B:79:0x0215, B:92:0x0256, B:94:0x0269, B:93:0x0261, B:82:0x0223, B:84:0x022a, B:86:0x0233, B:87:0x0239, B:89:0x023e, B:90:0x024e, B:95:0x0275, B:97:0x027b, B:112:0x02f5, B:114:0x02f9, B:99:0x027f, B:101:0x0286, B:103:0x028f, B:104:0x02a8, B:106:0x02b7, B:108:0x02c7, B:109:0x02d6, B:111:0x02e7, B:37:0x0116, B:39:0x011a, B:30:0x00d9, B:25:0x0090, B:20:0x003d, B:115:0x0315), top: B:121:0x0005 }] */
    @Override // androidx.appcompat.widget.AppCompatSeekBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onDraw(Canvas canvas) {
        int height;
        float max;
        int paddingStart;
        float paddingStart2;
        int i;
        float f;
        int i2;
        float f2;
        float f3;
        Drawable drawable;
        int max2;
        float paddingStart3;
        int layoutDirection;
        int i3;
        int i4;
        int i5;
        float f4;
        float f5;
        float paddingStart4;
        float f6;
        float f7;
        if (this.mIsThumbTheme) {
            if (this.mOvalDrawable != null) {
                if (!isEnabled()) {
                    this.mOvalDrawable.setAlpha(178);
                } else {
                    this.mOvalDrawable.setAlpha(255);
                }
            }
            boolean z = ViewCompat.getLayoutDirection(this) == 1;
            float width = getWidth();
            if (Build.VERSION.SDK_INT >= 29) {
                height = getMaxHeight();
            } else {
                height = (getHeight() - getPaddingBottom()) - getPaddingTop();
            }
            float f8 = height;
            float width2 = getWidth();
            float dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_seekbar_progress_custom_bg_radius);
            float paddingTop = getPaddingTop();
            float f9 = paddingTop + f8;
            int minWrapper = getMinWrapper();
            if (z) {
                max = ((getMax() - this.mDrawProgress) / ((getMax() - minWrapper) * 1.0f)) * ((width - getPaddingStart()) - getPaddingEnd());
                paddingStart = getPaddingEnd();
            } else {
                max = ((this.mDrawProgress - minWrapper) / (getMax() - minWrapper)) * ((width - getPaddingStart()) - getPaddingEnd());
                paddingStart = getPaddingStart();
            }
            float f10 = max + paddingStart;
            if (!this.mBalanceEnabled) {
                paddingStart2 = 0.0f;
            } else if (z) {
                paddingStart2 = (((getMax() - this.mBalanceProgress) / ((getMax() - minWrapper) * 1.0f)) * ((width - getPaddingStart()) - getPaddingEnd())) + getPaddingEnd();
            } else {
                paddingStart2 = getPaddingStart() + (((this.mBalanceProgress - minWrapper) / (getMax() - minWrapper)) * ((width - getPaddingStart()) - getPaddingEnd()));
            }
            int i6 = this.mDefaultScalePrimaryColor;
            int i7 = this.mDefaultScaleSecondaryColor;
            float f11 = (f8 / 2.0f) + paddingTop;
            int i8 = (int) (f11 - (this.mThumbHeight / 2.0f));
            Drawable drawable2 = this.mBackgroundDrawable;
            if (drawable2 == null) {
                i = i8;
                if (this.mProgressDrawable == null) {
                    Drawable drawable3 = this.mLayerDrawable;
                    if (drawable3 != null) {
                        drawable3.setBounds((int) (getPaddingStart() - dimensionPixelSize), (int) paddingTop, (int) ((width - getPaddingEnd()) + dimensionPixelSize), (int) f9);
                        this.mLayerDrawable.draw(canvas);
                    }
                    f11 = f11;
                    i6 = i6;
                }
                if (this.mMiddleEnabled || this.mBalanceEnabled || this.mProgress >= ((getMax() - minWrapper) / 2) + minWrapper) {
                    f = f11;
                } else {
                    this.mPaint.setColor(i7);
                    f = f11;
                    canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                }
                if (this.mScaleEnabled) {
                    max2 = getMax();
                    paddingStart3 = ((width - getPaddingStart()) - getPaddingEnd()) / max2;
                    layoutDirection = getLayoutDirection();
                    i3 = 0;
                    while (i3 <= max2) {
                        i4 = this.mProgress;
                        if (i3 != i4) {
                            f6 = i3;
                            f7 = this.mDrawProgress;
                            if (f6 < f7) {
                                i5 = i6;
                                this.mPaint.setColor(i5);
                            } else {
                                i5 = i6;
                                if (f6 > f7) {
                                    this.mPaint.setColor(i7);
                                }
                            }
                        } else {
                            i5 = i6;
                            f4 = i4;
                            f5 = this.mDrawProgress;
                            if (f4 > f5) {
                                this.mPaint.setColor(i7);
                            } else if (i4 == f5) {
                                this.mPaint.setColor(getResources().getColor(R.color.miuix_appcompat_transparent));
                            } else {
                                this.mPaint.setColor(i5);
                            }
                        }
                        if (layoutDirection == 1) {
                            paddingStart4 = (width - getPaddingEnd()) - (i3 * paddingStart3);
                        } else {
                            paddingStart4 = getPaddingStart() + (i3 * paddingStart3);
                        }
                        canvas.drawCircle(paddingStart4, f, this.mScaleRadius, this.mPaint);
                        i3++;
                        i6 = i5;
                    }
                }
                i2 = i6;
                if (!this.mMiddleEnabled || this.mBalanceEnabled) {
                    if (this.mBalanceEnabled) {
                        f2 = this.mDrawProgress;
                        if (f2 == this.mBalanceProgress) {
                            this.mPaint.setColor(getResources().getColor(R.color.miuix_appcompat_transparent));
                            canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                        } else {
                            f3 = minWrapper;
                            if (f2 <= ((getMax() - minWrapper) / 2.0f) + f3 + 0.2f || this.mDrawProgress < (((getMax() - minWrapper) / 2.0f) + f3) - 0.2f) {
                                this.mPaint.setColor(i2);
                                canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                            }
                        }
                    } else if (this.mDrawProgress > ((getMax() - minWrapper) / 2.0f) + minWrapper + 0.2f) {
                        this.mPaint.setColor(i2);
                        canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                    }
                }
                drawable = this.mThumbDrawable;
                if (drawable != null) {
                    int i9 = this.mThumbWidth;
                    drawable.setBounds((int) (f10 - (i9 / 2.0f)), i, (int) (f10 + (i9 / 2.0f)), i + this.mThumbHeight);
                    this.mThumbDrawable.draw(canvas);
                }
            } else {
                i = i8;
            }
            if (drawable2 != null) {
                drawable2.setBounds((int) (getPaddingStart() - dimensionPixelSize), (int) paddingTop, (int) ((width2 - getPaddingEnd()) + dimensionPixelSize), (int) f9);
                this.mBackgroundDrawable.draw(canvas);
            }
            Drawable drawable4 = this.mProgressDrawable;
            if (drawable4 != null) {
                if (z) {
                    if (!this.mBalanceEnabled) {
                        drawable4.setBounds((int) (f10 - dimensionPixelSize), (int) paddingTop, (int) ((width - getPaddingEnd()) + dimensionPixelSize), (int) f9);
                    } else if (f10 > paddingStart2) {
                        drawable4.setBounds((int) (paddingStart2 - dimensionPixelSize), (int) paddingTop, (int) (dimensionPixelSize + f10), (int) f9);
                    } else {
                        drawable4.setBounds((int) (f10 - dimensionPixelSize), (int) paddingTop, (int) (paddingStart2 + dimensionPixelSize), (int) f9);
                    }
                } else if (!this.mBalanceEnabled) {
                    drawable4.setBounds((int) (getPaddingStart() - dimensionPixelSize), (int) paddingTop, (int) (dimensionPixelSize + f10), (int) f9);
                } else if (f10 > paddingStart2) {
                    drawable4.setBounds((int) (paddingStart2 - dimensionPixelSize), (int) paddingTop, (int) (dimensionPixelSize + f10), (int) f9);
                } else {
                    drawable4.setBounds((int) (f10 - dimensionPixelSize), (int) paddingTop, (int) (paddingStart2 + dimensionPixelSize), (int) f9);
                }
                this.mProgressDrawable.draw(canvas);
            }
            if (this.mMiddleEnabled) {
                f = f11;
            } else {
                f = f11;
            }
            if (this.mScaleEnabled) {
                max2 = getMax();
                paddingStart3 = ((width - getPaddingStart()) - getPaddingEnd()) / max2;
                layoutDirection = getLayoutDirection();
                i3 = 0;
                while (i3 <= max2) {
                    i4 = this.mProgress;
                    if (i3 != i4) {
                        f6 = i3;
                        f7 = this.mDrawProgress;
                        if (f6 < f7) {
                            i5 = i6;
                            this.mPaint.setColor(i5);
                        } else {
                            i5 = i6;
                            if (f6 > f7) {
                                this.mPaint.setColor(i7);
                            }
                        }
                    } else {
                        i5 = i6;
                        f4 = i4;
                        f5 = this.mDrawProgress;
                        if (f4 > f5) {
                            this.mPaint.setColor(i7);
                        } else if (i4 == f5) {
                            this.mPaint.setColor(getResources().getColor(R.color.miuix_appcompat_transparent));
                        } else {
                            this.mPaint.setColor(i5);
                        }
                    }
                    if (layoutDirection == 1) {
                        paddingStart4 = (width - getPaddingEnd()) - (i3 * paddingStart3);
                    } else {
                        paddingStart4 = getPaddingStart() + (i3 * paddingStart3);
                    }
                    canvas.drawCircle(paddingStart4, f, this.mScaleRadius, this.mPaint);
                    i3++;
                    i6 = i5;
                }
            }
            i2 = i6;
            if (!this.mMiddleEnabled) {
                if (this.mBalanceEnabled) {
                    f2 = this.mDrawProgress;
                    if (f2 == this.mBalanceProgress) {
                        this.mPaint.setColor(getResources().getColor(R.color.miuix_appcompat_transparent));
                        canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                    } else {
                        f3 = minWrapper;
                        if (f2 <= ((getMax() - minWrapper) / 2.0f) + f3 + 0.2f) {
                            this.mPaint.setColor(i2);
                            canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                        } else {
                            this.mPaint.setColor(i2);
                            canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                        }
                    }
                } else if (this.mDrawProgress > ((getMax() - minWrapper) / 2.0f) + minWrapper + 0.2f) {
                    this.mPaint.setColor(i2);
                    canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                }
            } else if (this.mBalanceEnabled) {
                f2 = this.mDrawProgress;
                if (f2 == this.mBalanceProgress) {
                    this.mPaint.setColor(getResources().getColor(R.color.miuix_appcompat_transparent));
                    canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                } else {
                    f3 = minWrapper;
                    if (f2 <= ((getMax() - minWrapper) / 2.0f) + f3 + 0.2f) {
                        this.mPaint.setColor(i2);
                        canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                    } else {
                        this.mPaint.setColor(i2);
                        canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
                    }
                }
            } else if (this.mDrawProgress > ((getMax() - minWrapper) / 2.0f) + minWrapper + 0.2f) {
                this.mPaint.setColor(i2);
                canvas.drawCircle(width / 2.0f, f, this.mScaleRadius, this.mPaint);
            }
            drawable = this.mThumbDrawable;
            if (drawable != null) {
                int i10 = this.mThumbWidth;
                drawable.setBounds((int) (f10 - (i10 / 2.0f)), i, (int) (f10 + (i10 / 2.0f)), i + this.mThumbHeight);
                this.mThumbDrawable.draw(canvas);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private void onPressedInner() {
        if (this.mThumbPressedUpAnim.isRunning()) {
            this.mThumbPressedUpAnim.cancel();
        }
        if (this.mThumbPressedAnim.isRunning()) {
            return;
        }
        this.mThumbPressedAnim.start();
    }

    private void onPressedUpInner() {
        if (this.mThumbPressedAnim.isRunning()) {
            this.mThumbPressedAnim.cancel();
        }
        if (this.mThumbPressedUpAnim.isRunning()) {
            return;
        }
        this.mThumbPressedUpAnim.start();
    }

    /* JADX WARN: Code duplicated, block: B:33:0x0069  */
    /* JADX WARN: Code duplicated, block: B:40:0x007a  */
    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Rect rect;
        if (!isEnabled()) {
            return false;
        }
        if (this.mIsThumbTheme && this.mIsThumbNeedAnimation) {
            this.mThumbRect = this.mThumbDrawable.copyBounds();
        }
        float y = motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mIsDragging = false;
            float x = motionEvent.getX();
            this.mTouchDownX = x;
            if (this.mIsThumbTheme && this.mIsThumbNeedAnimation && (rect = this.mThumbRect) != null && rect.contains((int) x, (int) y)) {
                onPressedInner();
            }
        } else if (action == 1) {
            if (this.mIsThumbTheme && this.mIsThumbNeedAnimation) {
                this.mIsTouchUpEvent = true;
                onPressedUpInner();
            }
            if (this.mIsDragging) {
                this.mIsDragging = false;
            }
        } else if (action != 2) {
            if (action == 3) {
                if (this.mIsThumbTheme) {
                    this.mIsTouchUpEvent = true;
                    onPressedUpInner();
                }
                if (this.mIsDragging) {
                    this.mIsDragging = false;
                }
            }
        } else if (!this.mIsDragging) {
            float x2 = motionEvent.getX();
            if (Math.abs(x2 - this.mTouchDownX) > this.mScaledTouchSlop) {
                this.mIsDragging = true;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
            if (this.mIsThumbTheme && this.mIsThumbNeedAnimation && this.mThumbRect.contains((int) x2, (int) y)) {
                onPressedInner();
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private float getValueFromTypedArray(TypedArray typedArray, int i, float f) {
        TypedValue typedValuePeekValue = typedArray.peekValue(i);
        return (typedValuePeekValue == null || typedValuePeekValue.type != 6) ? f : typedValuePeekValue.getFraction(1.0f, 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized int getProgressForm(float f) {
        return ((int) (f * getRange())) + getMinWrapper();
    }

    private synchronized int getRange() {
        return getMax() - getMinWrapper();
    }

    @ViewDebug.ExportedProperty(category = "draggableProgress")
    public synchronized float getDraggableMinPercentProgress() {
        return this.mDraggableMinPercentProgress;
    }

    @ViewDebug.ExportedProperty(category = "draggableProgress")
    public synchronized float getDraggableMaxPercentProgress() {
        return this.mDraggableMaxPercentProcess;
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0028 A[Catch: all -> 0x0012, TryCatch #0 {all -> 0x0012, blocks: (B:5:0x0009, B:12:0x0022, B:14:0x0028, B:16:0x0031, B:18:0x003d, B:11:0x001a), top: B:23:0x0007 }] */
    /* JADX WARN: Code duplicated, block: B:15:0x0030  */
    /* JADX WARN: Code duplicated, block: B:18:0x003d A[Catch: all -> 0x0012, TRY_LEAVE, TryCatch #0 {all -> 0x0012, blocks: (B:5:0x0009, B:12:0x0022, B:14:0x0028, B:16:0x0031, B:18:0x003d, B:11:0x001a), top: B:23:0x0007 }] */
    public synchronized void setDraggableMinPercentProgress(float f) {
        int progressForm;
        double d = f;
        float f2 = 0.0f;
        try {
            if (d > 1.0d) {
                Log.e(TAG, "The draggableMinPercentProgress value should not be higher than 1.0, reset to 0.0");
            } else {
                if (d < 0.0d) {
                    Log.e(TAG, "The draggableMinPercentProgress value should not be lower than 0.0, reset to 0.0");
                }
                if (f > this.mDraggableMaxPercentProcess) {
                    Log.e(TAG, "The draggableMinPercentProgress value should not be higher than draggableMaxPercentProcess value, reset to 0.0");
                } else {
                    f2 = f;
                }
                this.mDraggableMinPercentProgress = f2;
                progressForm = getProgressForm(f2);
                if (getProgress() < progressForm) {
                    setProgress(progressForm);
                }
            }
            f = 0.0f;
            if (f > this.mDraggableMaxPercentProcess) {
                Log.e(TAG, "The draggableMinPercentProgress value should not be higher than draggableMaxPercentProcess value, reset to 0.0");
            } else {
                f2 = f;
            }
            this.mDraggableMinPercentProgress = f2;
            progressForm = getProgressForm(f2);
            if (getProgress() < progressForm) {
                setProgress(progressForm);
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0028 A[Catch: all -> 0x0013, TryCatch #0 {all -> 0x0013, blocks: (B:5:0x000a, B:12:0x0022, B:14:0x0028, B:16:0x0031, B:18:0x003d, B:11:0x001a), top: B:23:0x0008 }] */
    /* JADX WARN: Code duplicated, block: B:15:0x0030  */
    /* JADX WARN: Code duplicated, block: B:18:0x003d A[Catch: all -> 0x0013, TRY_LEAVE, TryCatch #0 {all -> 0x0013, blocks: (B:5:0x000a, B:12:0x0022, B:14:0x0028, B:16:0x0031, B:18:0x003d, B:11:0x001a), top: B:23:0x0008 }] */
    public synchronized void setDraggableMaxPercentProcess(float f) {
        int progressForm;
        float f2 = 1.0f;
        try {
            if (f > 1.0d) {
                Log.e(TAG, "The draggableMaxPercentProcess value should not be higher than the max value, reset to 1.0");
            } else {
                if (f < 0.0f) {
                    Log.e(TAG, "The draggableMaxPercentProcess value should not be lower than the min value, reset to 1.0");
                }
                if (f < this.mDraggableMinPercentProgress) {
                    Log.e(TAG, "The draggableMaxPercentProcess value should not be lower than draggableMinPercentProcess value, reset to 1.0");
                } else {
                    f2 = f;
                }
                this.mDraggableMaxPercentProcess = f2;
                progressForm = getProgressForm(f2);
                if (getProgress() > progressForm) {
                    setProgress(progressForm);
                }
            }
            f = 1.0f;
            if (f < this.mDraggableMinPercentProgress) {
                Log.e(TAG, "The draggableMaxPercentProcess value should not be lower than draggableMinPercentProcess value, reset to 1.0");
            } else {
                f2 = f;
            }
            this.mDraggableMaxPercentProcess = f2;
            progressForm = getProgressForm(f2);
            if (getProgress() > progressForm) {
                setProgress(progressForm);
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    @Override // android.widget.SeekBar
    public void setOnSeekBarChangeListener(android.widget.SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        android.widget.SeekBar.OnSeekBarChangeListener onSeekBarChangeListener2 = this.mTrainsOnSeekBarChangeListener;
        if (onSeekBarChangeListener == onSeekBarChangeListener2) {
            super.setOnSeekBarChangeListener(onSeekBarChangeListener2);
        } else {
            this.mOnSeekBarChangeListener = onSeekBarChangeListener;
        }
    }

    public void setMiddleEnabled(boolean z) {
        if (z != this.mMiddleEnabled) {
            this.mMiddleEnabled = z;
            updatePrimaryColor();
        }
    }

    public void setForegroundPrimaryColor(int i, int i2) {
        this.mForegroundPrimaryColor = i;
        this.mForegroundPrimaryDisableColor = i2;
        updatePrimaryColor();
    }

    public void setBackgroundPrimaryColor(int i, int i2) {
        this.mBackgroundPrimaryColor = i;
        this.mBackgroundPrimaryDisableColor = i2;
        updatePrimaryColor();
    }

    public void setIconPrimaryColor(int i) {
        this.mIconPrimaryColor = i;
        updatePrimaryColor();
    }

    public void setBalanceProgress(int i) {
        this.mBalanceProgress = i;
        invalidate();
    }

    public void setBalanceEnabled(boolean z) {
        this.mBalanceEnabled = z;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePrimaryColor() {
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
            Drawable drawableFindDrawableByLayerId = layerDrawable.findDrawableByLayerId(android.R.id.progress);
            Drawable drawableFindDrawableByLayerId2 = layerDrawable.findDrawableByLayerId(android.R.id.background);
            if (drawableFindDrawableByLayerId != null && (drawableFindDrawableByLayerId instanceof GradientDrawable)) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawableFindDrawableByLayerId;
                ColorStateList color = gradientDrawable.getColor();
                if (this.mProgressColorStateList == null && color != null) {
                    this.mProgressColorStateList = color;
                }
                ColorStateList colorStateList = this.mProgressColorStateList;
                if (colorStateList != null && (colorStateList.getColorForState(new int[]{-16842910}, this.mDefaultForegroundPrimaryDisableColor) != this.mForegroundPrimaryDisableColor || this.mProgressColorStateList.getColorForState(ENABLED_STATE_SET, this.mDefaultForegroundPrimaryColor) != this.mForegroundPrimaryColor)) {
                    GradientDrawable gradientDrawable2 = (GradientDrawable) gradientDrawable.mutate();
                    ColorStateList colorStateList2 = new ColorStateList(new int[][]{new int[]{-16842910}, new int[0]}, new int[]{this.mForegroundPrimaryDisableColor, this.mForegroundPrimaryColor});
                    this.mProgressColorStateList = colorStateList2;
                    gradientDrawable2.setColor(colorStateList2);
                }
            }
            if (drawableFindDrawableByLayerId instanceof ClipDrawable) {
                Drawable drawable = ((ClipDrawable) drawableFindDrawableByLayerId).getDrawable();
                if (drawable instanceof GradientDrawable) {
                    GradientDrawable gradientDrawable3 = (GradientDrawable) drawable;
                    ColorStateList color2 = gradientDrawable3.getColor();
                    if (this.mProgressColorStateList == null && color2 != null) {
                        this.mProgressColorStateList = color2;
                    }
                    ColorStateList colorStateList3 = this.mProgressColorStateList;
                    if (colorStateList3 != null && ((colorStateList3.getColorForState(new int[]{-16842910}, this.mDefaultForegroundPrimaryDisableColor) != this.mForegroundPrimaryDisableColor || this.mProgressColorStateList.getColorForState(ENABLED_STATE_SET, this.mDefaultForegroundPrimaryColor) != this.mForegroundPrimaryColor) && !this.mIsThumbTheme)) {
                        GradientDrawable gradientDrawable4 = (GradientDrawable) gradientDrawable3.mutate();
                        ColorStateList colorStateList4 = new ColorStateList(new int[][]{new int[]{-16842910}, new int[0]}, new int[]{this.mForegroundPrimaryDisableColor, this.mForegroundPrimaryColor});
                        this.mProgressColorStateList = colorStateList4;
                        gradientDrawable4.setColor(colorStateList4);
                    }
                }
            }
            if (drawableFindDrawableByLayerId2 != null && (drawableFindDrawableByLayerId2 instanceof GradientDrawable)) {
                GradientDrawable gradientDrawable5 = (GradientDrawable) drawableFindDrawableByLayerId2;
                ColorStateList color3 = gradientDrawable5.getColor();
                if (this.mProgressColorStateList == null && color3 != null) {
                    this.mProgressColorStateList = color3;
                }
                ColorStateList colorStateList5 = this.mProgressColorStateList;
                if (colorStateList5 != null && (colorStateList5.getColorForState(new int[]{-16842910}, this.mDefaultBackgroundPrimaryDisableColor) != this.mBackgroundPrimaryDisableColor || this.mProgressColorStateList.getColorForState(ENABLED_STATE_SET, this.mDefaultBackgroundPrimaryColor) != this.mBackgroundPrimaryColor)) {
                    GradientDrawable gradientDrawable6 = (GradientDrawable) gradientDrawable5.mutate();
                    ColorStateList colorStateList6 = new ColorStateList(new int[][]{new int[]{-16842910}, new int[0]}, new int[]{this.mBackgroundPrimaryDisableColor, this.mBackgroundPrimaryColor});
                    this.mProgressColorStateList = colorStateList6;
                    gradientDrawable6.setColor(colorStateList6);
                }
            }
            if (!this.mIsThumbTheme) {
                Drawable drawableFindDrawableByLayerId3 = layerDrawable.findDrawableByLayerId(android.R.id.icon);
                if (drawableFindDrawableByLayerId3 instanceof GradientDrawable) {
                    drawableFindDrawableByLayerId3.setColorFilter(this.mMiddleEnabled ? this.mIconPrimaryColor : this.mIconTransparent, PorterDuff.Mode.SRC);
                    return;
                }
                return;
            }
            invalidate();
        }
    }

    @Override // androidx.appcompat.widget.AppCompatSeekBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updatePrimaryColor();
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledProgressAlpha * 255.0f));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMinWrapper() {
        return super.getMin();
    }

    private boolean isInMiddle(int i, int i2) {
        float minWrapper = i > 0 ? (i2 - getMinWrapper()) / i : 0.0f;
        return minWrapper > this.mMinMiddle && minWrapper < this.mMaxMiddle;
    }

    private static class ProgressAnimTarget implements FolmeObject {
        private Folme.ObjectFolmeImpl mFolmeImpl;

        private ProgressAnimTarget() {
        }

        @Override // miuix.animation.FolmeObject
        public void setFolmeImpl(Folme.ObjectFolmeImpl objectFolmeImpl) {
            this.mFolmeImpl = objectFolmeImpl;
        }

        @Override // miuix.animation.FolmeObject
        public Folme.ObjectFolmeImpl folme() {
            return this.mFolmeImpl;
        }
    }

    private static class ColorUpdateRunner implements Runnable {
        private WeakReference<SeekBar> mSeekBarRef;

        public ColorUpdateRunner(SeekBar seekBar) {
            this.mSeekBarRef = new WeakReference<>(seekBar);
        }

        @Override // java.lang.Runnable
        public void run() {
            WeakReference<SeekBar> weakReference = this.mSeekBarRef;
            SeekBar seekBar = weakReference == null ? null : weakReference.get();
            if (seekBar != null) {
                seekBar.updatePrimaryColor();
            }
        }
    }
}
