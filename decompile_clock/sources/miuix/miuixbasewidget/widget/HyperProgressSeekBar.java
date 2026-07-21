package miuix.miuixbasewidget.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.RuntimeShader;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.SeekBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.view.ViewCompat;
import com.android.deskclock.R2;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Scanner;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.FolmeObject;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.IntValueProperty;
import miuix.core.util.MiuixUIUtils;
import miuix.device.DeviceUtils;
import miuix.miuixbasewidget.R;
import miuix.util.HapticFeedbackCompat;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class HyperProgressSeekBar extends AppCompatSeekBar {
    public static final int DEVICE_HIGHEND = 2;
    public static final int DEVICE_MIDDLE = 1;
    public static final int DEVICE_PRIMARY = 0;
    public static final int DEVICE_UNKNOWN = -1;
    private static final long MIN_TRIGGER_INTERVAL = 50;
    private static final int NO_ALPHA = 255;
    private BitmapShader bitmapShader;
    private boolean hasTriggeredDownward;
    private boolean hasTriggeredThird;
    private boolean hasTriggeredUpward;
    private int initialProgress;
    private float initialX;
    private float lastDistance;
    private long lastTriggerTime;
    private Drawable mBackgroundDrawable;
    private int mBackgroundPrimaryColor;
    private int mBackgroundPrimaryDisableColor;
    private Path mClipPath;
    private float mClipRadius;
    private int mDefaultBackgroundPrimaryColor;
    private int mDefaultBackgroundPrimaryDisableColor;
    private int mDefaultForegroundPrimaryColor;
    private int mDefaultForegroundPrimaryDisableColor;
    private int mDefaultMaxHeight;
    private int mDefaultMinHeight;
    private int mDefaultProgressPaddingOffset;
    private int mDefaultShadowColor;
    private float mDefaultShadowRadius;
    private int mDeviceLevel;
    private float mDistanceScale;
    private int mDrawProgressAlpha;
    private float mFirstDistance;
    private int mForegroundPrimaryColor;
    private int mForegroundPrimaryDisableColor;
    private boolean mHasEdgeReached;
    private ProgressAnimTarget mHeadAlphaAnimator;
    private boolean mIsDragging;
    private boolean mIsProgressChangedInternal;
    private boolean mIsTracking;
    private Drawable mLayerDrawable;
    private OnRangeChangedListener mOnRangeChangedListener;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private Paint mPaint;
    private int mProgressAlpha;
    private ProgressAnimTarget mProgressAlphaAnimator;
    private ColorStateList mProgressColorStateList;
    private Drawable mProgressDrawable;
    private int mProgressHeight;
    private ProgressAnimTarget mProgressHeightAnimator;
    private int mProgressMode;
    private int mProgressPaddingOffset;
    private int mProgressPressedAlpha;
    private int mProgressSeekBarMaxHeight;
    private int mProgressSeekBarMinHeight;
    private RectF mProgressSeekBarRect;
    private int mScaledTouchSlop;
    private float mSecondDistance;
    private int mShadowColor;
    private float mShadowHorizontalExtend;
    private float mShadowRadius;
    private RectF mShadowRect;
    private float mShadowVerticalExtend;
    private float mShadowX;
    private float mShadowY;
    private float mTouchDownX;
    private final SeekBar.OnSeekBarChangeListener mTrainsOnSeekBarChangeListener;
    private float progress;
    private RuntimeShader runtimeShader;
    private float uHeadGlowAlpha;
    private float[] uHeadSize;
    private float[] uTrackCanvasSize;
    private float[] uTrackPosition;
    private float[] uTrackSize;
    private static final IntValueProperty PROPERTY_PROGRESS_HEIGHT = new IntValueProperty("progressHeight", 0.001f);
    private static final IntValueProperty PROPERTY_HEAD_ALPHA = new IntValueProperty("headAlpha", 0.1f);
    private static final IntValueProperty PROPERTY_PROGRESS_ALPHA = new IntValueProperty("progressAlpha", 0.1f);

    public interface OnRangeChangedListener {
        void onHighHeightReached();

        void onMiddleHeightReached();

        void onNormalHeightReached();
    }

    public HyperProgressSeekBar(Context context) {
        this(context, null);
    }

    public HyperProgressSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.miuixProgressSeekBarStyle);
    }

    public HyperProgressSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.uTrackCanvasSize = new float[]{272.0f, 38.0f};
        this.uTrackPosition = new float[]{12.0f, 19.0f};
        this.uTrackSize = new float[]{220.0f, 6.0f};
        this.uHeadSize = new float[]{75.0f, 38.0f};
        this.progress = 0.7f;
        this.uHeadGlowAlpha = 1.0f;
        this.initialX = 0.0f;
        this.initialProgress = 0;
        this.lastDistance = Float.MAX_VALUE;
        this.hasTriggeredUpward = false;
        this.hasTriggeredDownward = false;
        this.hasTriggeredThird = false;
        this.lastTriggerTime = 0L;
        this.mDistanceScale = 1.0f;
        this.mFirstDistance = 22.0f;
        this.mSecondDistance = 106.0f;
        this.mDeviceLevel = 0;
        this.mTrainsOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.7
            private HapticFeedbackCompat mHapticFeedbackCompat;

            private HapticFeedbackCompat getHapticFeedbackCompat() {
                if (this.mHapticFeedbackCompat == null) {
                    this.mHapticFeedbackCompat = new HapticFeedbackCompat(HyperProgressSeekBar.this.getContext());
                }
                return this.mHapticFeedbackCompat;
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i2, boolean z) {
                if (HyperProgressSeekBar.this.mOnSeekBarChangeListener != null) {
                    if (HyperProgressSeekBar.this.mIsProgressChangedInternal) {
                        HyperProgressSeekBar.this.mIsProgressChangedInternal = false;
                        HyperProgressSeekBar.this.mOnSeekBarChangeListener.onProgressChanged(seekBar, i2, false);
                        return;
                    }
                    boolean z2 = i2 == HyperProgressSeekBar.this.getMinWrapper() || i2 == HyperProgressSeekBar.this.getMax();
                    if (z2 && !HyperProgressSeekBar.this.mHasEdgeReached) {
                        if (HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2)) {
                            if (i2 == HyperProgressSeekBar.this.getMax()) {
                                getHapticFeedbackCompat().m1943x85658b2f(R2.attr.actionBarRefreshIcon);
                            } else {
                                getHapticFeedbackCompat().m1943x85658b2f(R2.attr.actionBarPopupTheme);
                            }
                        } else {
                            HapticCompat.performHapticFeedback(seekBar, HapticFeedbackConstants.MIUI_MESH_NORMAL);
                        }
                    }
                    HyperProgressSeekBar.this.mHasEdgeReached = z2;
                    HyperProgressSeekBar.this.mOnSeekBarChangeListener.onProgressChanged(seekBar, i2, true);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (HyperProgressSeekBar.this.mOnSeekBarChangeListener != null) {
                    HyperProgressSeekBar.this.mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (HyperProgressSeekBar.this.mOnSeekBarChangeListener != null) {
                    HyperProgressSeekBar.this.mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HyperProgressSeekBar, i, R.style.Widget_HyperProgressSeekBar_DayNight);
        this.mProgressMode = typedArrayObtainStyledAttributes.getInt(R.styleable.HyperProgressSeekBar_progressBarMode, -1);
        this.mDefaultShadowColor = context.getResources().getColor(R.color.miuix_appcompat_hyper_progress_seekbar_shadow_default_color);
        this.mDefaultShadowRadius = context.getResources().getDimension(R.dimen.miuix_appcompat_hyper_progress_seekbar_shadow_radius);
        this.mShadowX = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperProgressSeekBar_shadowDx, 0.0f);
        this.mShadowY = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperProgressSeekBar_shadowDy, 0.0f);
        this.mShadowHorizontalExtend = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperProgressSeekBar_shadowHorizontalExtend, 5.0f);
        this.mShadowVerticalExtend = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperProgressSeekBar_shadowVerticalExtend, 0.0f);
        this.mShadowRadius = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperProgressSeekBar_shadowRadius, 0.0f);
        this.mShadowColor = typedArrayObtainStyledAttributes.getColor(R.styleable.HyperProgressSeekBar_shadowColor, this.mDefaultShadowColor);
        this.mShadowRadius = typedArrayObtainStyledAttributes.getDimension(R.styleable.HyperProgressSeekBar_shadowRadius, this.mDefaultShadowRadius);
        this.mClipRadius = context.getResources().getDimension(R.dimen.miuix_appcompat_hyper_progress_seekbar_shadow_clip_radius);
        this.mShadowRect = new RectF();
        this.mProgressSeekBarRect = new RectF();
        this.mClipPath = new Path();
        init(context, typedArrayObtainStyledAttributes);
        initializeDeviceLevel();
        int i2 = this.mDeviceLevel;
        if (i2 == 0) {
            initForPrimaryDevice();
        } else if (i2 == 1) {
            initForMiddleDevice();
        } else if (i2 == 2) {
            initForHighEndDevice();
        }
        int i3 = this.mDeviceLevel;
        if (i3 == 0 || i3 == 1) {
            this.mDefaultForegroundPrimaryColor = context.getResources().getColor(R.color.miuix_appcompat_hyper_progress_seekbar_foreground_normal_color_light);
            this.mDefaultForegroundPrimaryDisableColor = context.getResources().getColor(R.color.miuix_appcompat_hyper_progress_seekbar_background_disabled_color_light);
            this.mDefaultBackgroundPrimaryColor = context.getResources().getColor(R.color.miuix_appcompat_hyper_progress_seekbar_background_normal_color_light);
            this.mDefaultBackgroundPrimaryDisableColor = context.getResources().getColor(R.color.miuix_appcompat_hyper_progress_seekbar_background_disabled_color_dark);
            this.mForegroundPrimaryColor = typedArrayObtainStyledAttributes.getColor(R.styleable.HyperProgressSeekBar_foregroundPrimaryColor, this.mDefaultForegroundPrimaryColor);
            this.mForegroundPrimaryDisableColor = typedArrayObtainStyledAttributes.getColor(R.styleable.HyperProgressSeekBar_foregroundPrimaryDisableColor, this.mDefaultForegroundPrimaryDisableColor);
            this.mBackgroundPrimaryColor = typedArrayObtainStyledAttributes.getColor(R.styleable.HyperProgressSeekBar_backgroundPrimaryColor, this.mDefaultBackgroundPrimaryColor);
            this.mBackgroundPrimaryDisableColor = typedArrayObtainStyledAttributes.getColor(R.styleable.HyperProgressSeekBar_backgroundPrimaryDisableColor, this.mDefaultBackgroundPrimaryDisableColor);
            this.mProgressAlpha = typedArrayObtainStyledAttributes.getInt(R.styleable.HyperProgressSeekBar_progressAlpha, 255);
            this.mProgressPressedAlpha = typedArrayObtainStyledAttributes.getInt(R.styleable.HyperProgressSeekBar_progressPressedAlpha, 255);
            ProgressAnimTarget progressAnimTarget = new ProgressAnimTarget();
            this.mProgressAlphaAnimator = progressAnimTarget;
            Folme.use((FolmeObject) progressAnimTarget);
            this.mProgressAlphaAnimator.folme().setTo(PROPERTY_PROGRESS_ALPHA, Integer.valueOf(this.mProgressAlpha));
            this.mDrawProgressAlpha = this.mProgressAlpha;
            post(new ColorUpdateRunner(this));
        }
        int i4 = this.mDeviceLevel;
        if (i4 == 2 || i4 == 1) {
            ProgressAnimTarget progressAnimTarget2 = new ProgressAnimTarget();
            this.mHeadAlphaAnimator = progressAnimTarget2;
            Folme.use((FolmeObject) progressAnimTarget2);
            this.mHeadAlphaAnimator.folme().setTo(PROPERTY_HEAD_ALPHA, Float.valueOf(1.0f));
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    private void initializeDeviceLevel() {
        if (Build.VERSION.SDK_INT < 33) {
            this.mDeviceLevel = 0;
            return;
        }
        int i = this.mProgressMode;
        if (i == -1) {
            int deviceLevel = DeviceUtils.getDeviceLevel();
            this.mDeviceLevel = deviceLevel;
            if (deviceLevel == -1) {
                this.mDeviceLevel = 0;
                return;
            }
            return;
        }
        this.mDeviceLevel = i;
    }

    private void init(Context context, TypedArray typedArray) {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        ProgressAnimTarget progressAnimTarget = new ProgressAnimTarget();
        this.mProgressHeightAnimator = progressAnimTarget;
        Folme.use((FolmeObject) progressAnimTarget);
        this.mDefaultMinHeight = getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_hyper_progress_seekbar_min_height);
        this.mDefaultMaxHeight = getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_hyper_progress_seekbar_height);
        this.mProgressSeekBarMinHeight = typedArray.getDimensionPixelSize(R.styleable.HyperProgressSeekBar_progressSeekBarMinHeight, this.mDefaultMinHeight);
        int dimensionPixelSize = typedArray.getDimensionPixelSize(R.styleable.HyperProgressSeekBar_progressSeekBarMaxHeight, this.mDefaultMaxHeight);
        this.mProgressSeekBarMaxHeight = dimensionPixelSize;
        if (dimensionPixelSize % 2 != 0) {
            this.mProgressSeekBarMaxHeight = dimensionPixelSize - 1;
        }
        int i = this.mProgressSeekBarMinHeight;
        if (i % 2 != 0) {
            this.mProgressSeekBarMinHeight = i - 1;
        }
        this.mProgressHeight = this.mProgressSeekBarMinHeight;
        if (Build.VERSION.SDK_INT >= 29) {
            setMinHeight(this.mProgressHeight);
            setMaxHeight(this.mProgressHeight);
        }
        this.mProgressHeightAnimator.folme().setTo(PROPERTY_PROGRESS_HEIGHT, Integer.valueOf(this.mProgressHeight));
        this.mDefaultProgressPaddingOffset = getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_hyper_progress_seekbar_padding_offset);
        this.mProgressPaddingOffset = typedArray.getDimensionPixelSize(R.styleable.HyperProgressSeekBar_paddingOffset, this.mDefaultProgressPaddingOffset);
        setOnSeekBarChangeListener(this.mTrainsOnSeekBarChangeListener);
    }

    private void initShaderConfig() {
        if (Build.VERSION.SDK_INT >= 33) {
            Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.miuix_hyper_progressbar_light_head);
            this.uHeadSize[0] = bitmapDecodeResource.getWidth();
            this.uHeadSize[1] = bitmapDecodeResource.getHeight();
            this.bitmapShader = new BitmapShader(bitmapDecodeResource, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.mPaint.setShader(this.runtimeShader);
            this.runtimeShader.setInputShader("uTex", this.bitmapShader);
            this.runtimeShader.setFloatUniform("uHeadSize", this.uHeadSize);
            this.runtimeShader.setFloatUniform("uHeadGlowAlpha", this.uHeadGlowAlpha);
            this.uTrackPosition[0] = MiuixUIUtils.dp2px(getContext(), (int) this.uTrackPosition[0]);
            float[] fArr = this.uTrackPosition;
            fArr[0] = this.mProgressPaddingOffset;
            fArr[1] = MiuixUIUtils.dp2px(getContext(), (int) this.uTrackPosition[1]);
            this.uTrackSize[0] = MiuixUIUtils.dp2px(getContext(), (int) this.uTrackSize[0]);
            this.uTrackSize[1] = MiuixUIUtils.dp2px(getContext(), (int) this.uTrackSize[1]);
            this.runtimeShader.setFloatUniform("uTrackPosition", this.uTrackPosition);
            this.runtimeShader.setFloatUniform("uTrackSize", this.uTrackSize);
        }
    }

    private void initForHighEndDevice() {
        if (Build.VERSION.SDK_INT >= 33) {
            this.runtimeShader = new RuntimeShader(loadShader(getContext().getResources(), R.raw.music_player_tracker));
            initShaderConfig();
        }
    }

    private void initForMiddleDevice() {
        initForPrimaryDevice();
        if (Build.VERSION.SDK_INT >= 33) {
            this.runtimeShader = new RuntimeShader(loadShader(getContext().getResources(), R.raw.music_player_tracker_middle));
            initShaderConfig();
        }
    }

    private void initForPrimaryDevice() {
        if (getProgressDrawable() != null) {
            Drawable progressDrawable = getProgressDrawable();
            this.mLayerDrawable = progressDrawable;
            if (progressDrawable instanceof LayerDrawable) {
                LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
                this.mBackgroundDrawable = layerDrawable.findDrawableByLayerId(android.R.id.background);
                this.mProgressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);
            }
        }
    }

    protected String loadShader(Resources resources, int i) {
        Scanner scanner = new Scanner(resources.openRawResource(i));
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append("\n");
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMinWrapper() {
        return super.getMin();
    }

    public void setOnRangeChangedListener(OnRangeChangedListener onRangeChangedListener) {
        this.mOnRangeChangedListener = onRangeChangedListener;
    }

    public void setDistanceScale(float f) {
        this.mDistanceScale = f;
    }

    public void setFirstDistance(float f) {
        this.mFirstDistance = f;
    }

    public void setSecondDistance(float f) {
        this.mSecondDistance = f;
    }

    public void setShadowX(float f) {
        if (this.mShadowX != f) {
            this.mShadowX = f;
            invalidate();
        }
    }

    public void setShadowY(float f) {
        if (this.mShadowY != f) {
            this.mShadowY = f;
            invalidate();
        }
    }

    public void setShadowColor(int i) {
        if (this.mShadowColor != i) {
            this.mShadowColor = i;
            invalidate();
        }
    }

    public void setShadowRadius(float f) {
        if (this.mShadowRadius != f) {
            this.mShadowRadius = f;
            invalidate();
        }
    }

    public void setShadowHorizontalExtend(float f) {
        if (this.mShadowHorizontalExtend != f) {
            this.mShadowHorizontalExtend = f;
            invalidate();
        }
    }

    public void setShadowVerticalExtend(float f) {
        if (this.mShadowVerticalExtend != f) {
            this.mShadowVerticalExtend = f;
            invalidate();
        }
    }

    public void setForegroundPrimaryColor(int i) {
        int i2 = this.mDeviceLevel;
        if (i2 == 0 || i2 == 1) {
            this.mForegroundPrimaryColor = i;
            updatePrimaryColor();
        }
    }

    public void setForegroundPrimaryColorRes(int i) {
        int i2 = this.mDeviceLevel;
        if (i2 == 0 || i2 == 1) {
            this.mForegroundPrimaryColor = getContext().getResources().getColor(i);
            updatePrimaryColor();
        }
    }

    public void setForegroundPrimaryDisableColor(int i) {
        int i2 = this.mDeviceLevel;
        if (i2 == 0 || i2 == 1) {
            this.mForegroundPrimaryDisableColor = i;
            updatePrimaryColor();
        }
    }

    public void setBackgroundPrimaryColor(int i) {
        int i2 = this.mDeviceLevel;
        if (i2 == 0 || i2 == 1) {
            this.mBackgroundPrimaryColor = i;
            updatePrimaryColor();
        }
    }

    public void setBackgroundPrimaryDisableColor(int i) {
        int i2 = this.mDeviceLevel;
        if (i2 == 0 || i2 == 1) {
            this.mBackgroundPrimaryDisableColor = i;
            updatePrimaryColor();
        }
    }

    public void setProgressAndPressedAlpha(int i, int i2) {
        this.mProgressAlpha = i;
        this.mProgressPressedAlpha = i2;
        this.mProgressAlphaAnimator.folme().setTo(PROPERTY_PROGRESS_ALPHA, Integer.valueOf(this.mProgressAlpha));
        this.mDrawProgressAlpha = this.mProgressAlpha;
    }

    public void setProgressAndPressedAlphaRes(int i, int i2) {
        this.mProgressAlpha = getContext().getResources().getInteger(i);
        this.mProgressPressedAlpha = getContext().getResources().getInteger(i2);
        this.mProgressAlphaAnimator.folme().setTo(PROPERTY_PROGRESS_ALPHA, Integer.valueOf(this.mProgressAlpha));
        this.mDrawProgressAlpha = this.mProgressAlpha;
    }

    @Override // android.widget.ProgressBar
    public void setProgress(int i) {
        this.mIsProgressChangedInternal = true;
        super.setProgress(i);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar
    public void setMax(int i) {
        this.mIsProgressChangedInternal = true;
        super.setMax(i);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar
    public void setMin(int i) {
        this.mIsProgressChangedInternal = true;
        super.setMin(i);
    }

    @Override // android.widget.ProgressBar
    public void setProgressDrawable(Drawable drawable) {
        this.mIsProgressChangedInternal = true;
        super.setProgressDrawable(drawable);
    }

    @Override // android.widget.ProgressBar
    public void setProgress(int i, boolean z) {
        this.mIsProgressChangedInternal = true;
        super.setProgress(i, z);
    }

    /* JADX WARN: Code duplicated, block: B:102:0x01d5  */
    /* JADX WARN: Code duplicated, block: B:104:0x01d9  */
    /* JADX WARN: Code duplicated, block: B:107:0x01e3  */
    /* JADX WARN: Code duplicated, block: B:111:0x0216  */
    /* JADX WARN: Code duplicated, block: B:114:0x0284  */
    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i;
        int i2;
        float f;
        if (!isEnabled()) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            int i3 = this.mDeviceLevel;
            if (i3 == 2 || i3 == 1) {
                if (motionEvent.getX() < this.uTrackPosition[0] || motionEvent.getX() > this.uTrackPosition[0] + this.uTrackSize[0]) {
                    return false;
                }
            } else if (i3 == 0 && (motionEvent.getX() < this.mProgressPaddingOffset || motionEvent.getX() > getWidth() - this.mProgressPaddingOffset)) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= 29) {
                final int i4 = this.mProgressSeekBarMinHeight;
                final int i5 = this.mProgressSeekBarMaxHeight;
                int i6 = this.mDeviceLevel;
                if (i6 == 2 || i6 == 1) {
                    this.mHeadAlphaAnimator.folme().to(PROPERTY_HEAD_ALPHA, Float.valueOf(0.0f), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.1
                        @Override // miuix.animation.listener.TransitionListener
                        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_HEAD_ALPHA);
                            if (updateInfoFindBy != null) {
                                HyperProgressSeekBar.this.uHeadGlowAlpha = updateInfoFindBy.getFloatValue();
                                if (Build.VERSION.SDK_INT >= 33) {
                                    HyperProgressSeekBar.this.runtimeShader.setFloatUniform("uHeadGlowAlpha", HyperProgressSeekBar.this.uHeadGlowAlpha);
                                }
                                HyperProgressSeekBar.this.invalidate();
                            }
                        }
                    }));
                }
                int i7 = this.mDeviceLevel;
                if (i7 == 0 || i7 == 1) {
                    this.mProgressAlphaAnimator.folme().to(PROPERTY_PROGRESS_ALPHA, Integer.valueOf(this.mProgressPressedAlpha), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.2
                        @Override // miuix.animation.listener.TransitionListener
                        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_PROGRESS_ALPHA);
                            if (updateInfoFindBy != null) {
                                HyperProgressSeekBar.this.mDrawProgressAlpha = updateInfoFindBy.getIntValue();
                                HyperProgressSeekBar.this.invalidate();
                            }
                        }
                    }));
                }
                this.mProgressHeightAnimator.folme().to(PROPERTY_PROGRESS_HEIGHT, Integer.valueOf(this.mProgressSeekBarMaxHeight), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.3
                    @Override // miuix.animation.listener.TransitionListener
                    public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                        UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_PROGRESS_HEIGHT);
                        if (updateInfoFindBy != null) {
                            int intValue = updateInfoFindBy.getIntValue();
                            if (intValue % 2 != 0 && intValue > i4 && intValue < i5) {
                                intValue++;
                            }
                            HyperProgressSeekBar.this.mProgressHeight = intValue;
                            if (HyperProgressSeekBar.this.mDeviceLevel == 0 || HyperProgressSeekBar.this.mDeviceLevel == 1) {
                                HyperProgressSeekBar hyperProgressSeekBar = HyperProgressSeekBar.this;
                                hyperProgressSeekBar.setMinHeight(hyperProgressSeekBar.mProgressHeight);
                                HyperProgressSeekBar hyperProgressSeekBar2 = HyperProgressSeekBar.this;
                                hyperProgressSeekBar2.setMaxHeight(hyperProgressSeekBar2.mProgressHeight);
                                HyperProgressSeekBar hyperProgressSeekBar3 = HyperProgressSeekBar.this;
                                hyperProgressSeekBar3.setPadding(hyperProgressSeekBar3.getPaddingLeft(), (HyperProgressSeekBar.this.getHeight() - HyperProgressSeekBar.this.mProgressHeight) / 2, HyperProgressSeekBar.this.getPaddingRight(), (HyperProgressSeekBar.this.getHeight() - HyperProgressSeekBar.this.mProgressHeight) / 2);
                            }
                            HyperProgressSeekBar.this.invalidate();
                        }
                    }
                }));
            }
            this.mIsDragging = false;
            this.mTouchDownX = motionEvent.getX();
            return true;
        }
        if (action == 1) {
            if (Build.VERSION.SDK_INT >= 29) {
                final int i8 = this.mProgressSeekBarMinHeight;
                i = this.mDeviceLevel;
                if (i != 2 || i == 1) {
                    this.mHeadAlphaAnimator.folme().to(PROPERTY_HEAD_ALPHA, Float.valueOf(1.0f), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.4
                        @Override // miuix.animation.listener.TransitionListener
                        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_HEAD_ALPHA);
                            if (updateInfoFindBy != null) {
                                HyperProgressSeekBar.this.uHeadGlowAlpha = updateInfoFindBy.getFloatValue();
                                if (Build.VERSION.SDK_INT >= 33) {
                                    HyperProgressSeekBar.this.runtimeShader.setFloatUniform("uHeadGlowAlpha", HyperProgressSeekBar.this.uHeadGlowAlpha);
                                }
                                HyperProgressSeekBar.this.invalidate();
                            }
                        }
                    }));
                }
                i2 = this.mDeviceLevel;
                if (i2 != 0 || i2 == 1) {
                    this.mProgressAlphaAnimator.folme().to(PROPERTY_PROGRESS_ALPHA, Integer.valueOf(this.mProgressAlpha), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.5
                        @Override // miuix.animation.listener.TransitionListener
                        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_PROGRESS_ALPHA);
                            if (updateInfoFindBy != null) {
                                HyperProgressSeekBar.this.mDrawProgressAlpha = updateInfoFindBy.getIntValue();
                                HyperProgressSeekBar.this.invalidate();
                            }
                        }
                    }));
                }
                this.mProgressHeightAnimator.folme().to(PROPERTY_PROGRESS_HEIGHT, Integer.valueOf(this.mProgressSeekBarMinHeight), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.6
                    @Override // miuix.animation.listener.TransitionListener
                    public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                        UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_PROGRESS_HEIGHT);
                        if (updateInfoFindBy != null) {
                            int intValue = updateInfoFindBy.getIntValue();
                            if (intValue % 2 != 0 && intValue > i8) {
                                intValue--;
                            }
                            HyperProgressSeekBar.this.mProgressHeight = Math.max(i8, intValue);
                            if (HyperProgressSeekBar.this.mDeviceLevel == 0 || HyperProgressSeekBar.this.mDeviceLevel == 1) {
                                HyperProgressSeekBar hyperProgressSeekBar = HyperProgressSeekBar.this;
                                hyperProgressSeekBar.setMinHeight(hyperProgressSeekBar.mProgressHeight);
                                HyperProgressSeekBar hyperProgressSeekBar2 = HyperProgressSeekBar.this;
                                hyperProgressSeekBar2.setMaxHeight(hyperProgressSeekBar2.mProgressHeight);
                                HyperProgressSeekBar hyperProgressSeekBar3 = HyperProgressSeekBar.this;
                                hyperProgressSeekBar3.setPadding(hyperProgressSeekBar3.getPaddingLeft(), (HyperProgressSeekBar.this.getHeight() - HyperProgressSeekBar.this.mProgressHeight) / 2, HyperProgressSeekBar.this.getPaddingRight(), (HyperProgressSeekBar.this.getHeight() - HyperProgressSeekBar.this.mProgressHeight) / 2);
                            }
                            HyperProgressSeekBar.this.invalidate();
                        }
                    }
                }));
                this.lastDistance = Float.MAX_VALUE;
                this.hasTriggeredUpward = false;
                this.hasTriggeredDownward = false;
                this.hasTriggeredThird = false;
                this.mDistanceScale = 1.0f;
                this.mIsDragging = false;
                if (this.mIsTracking) {
                    this.mTrainsOnSeekBarChangeListener.onStopTrackingTouch(this);
                    this.mIsTracking = false;
                }
            }
        } else if (action == 2) {
            if (this.mOnRangeChangedListener != null) {
                float y = motionEvent.getY() - (getHeight() / 2.0f);
                float f2 = -(MiuixUIUtils.dp2px(getContext(), this.mFirstDistance) + (this.mProgressSeekBarMaxHeight / 2));
                float f3 = -(MiuixUIUtils.dp2px(getContext(), this.mSecondDistance) + (this.mProgressSeekBarMaxHeight / 2));
                long jCurrentTimeMillis = System.currentTimeMillis();
                float f4 = this.lastDistance;
                if (f4 != Float.MAX_VALUE) {
                    boolean z = f4 > f3 && y <= f3;
                    boolean z2 = f4 > f2 && y <= f2 && y > f3;
                    boolean z3 = f4 < f3 && y >= f3;
                    boolean z4 = f4 < f2 && y >= f2;
                    if (!z || this.hasTriggeredThird) {
                        f = y;
                    } else {
                        f = y;
                        if (jCurrentTimeMillis - this.lastTriggerTime > MIN_TRIGGER_INTERVAL) {
                            OnRangeChangedListener onRangeChangedListener = this.mOnRangeChangedListener;
                            if (onRangeChangedListener != null) {
                                onRangeChangedListener.onHighHeightReached();
                                this.mIsTracking = false;
                                this.initialX = motionEvent.getX();
                                this.initialProgress = getProgress();
                            }
                            this.hasTriggeredThird = true;
                            this.hasTriggeredUpward = false;
                            this.hasTriggeredDownward = false;
                            this.lastTriggerTime = jCurrentTimeMillis;
                        }
                    }
                    if ((z2 || z3) && !this.hasTriggeredUpward && jCurrentTimeMillis - this.lastTriggerTime > MIN_TRIGGER_INTERVAL) {
                        OnRangeChangedListener onRangeChangedListener2 = this.mOnRangeChangedListener;
                        if (onRangeChangedListener2 != null) {
                            onRangeChangedListener2.onMiddleHeightReached();
                            this.mIsTracking = false;
                            this.initialX = motionEvent.getX();
                            this.initialProgress = getProgress();
                        }
                        this.hasTriggeredUpward = true;
                        this.hasTriggeredThird = false;
                        this.hasTriggeredDownward = false;
                        this.lastTriggerTime = jCurrentTimeMillis;
                    } else if (z4 && !this.hasTriggeredDownward && jCurrentTimeMillis - this.lastTriggerTime > MIN_TRIGGER_INTERVAL) {
                        OnRangeChangedListener onRangeChangedListener3 = this.mOnRangeChangedListener;
                        if (onRangeChangedListener3 != null) {
                            onRangeChangedListener3.onNormalHeightReached();
                            this.mIsTracking = false;
                            this.initialX = motionEvent.getX();
                            this.initialProgress = getProgress();
                        }
                        this.hasTriggeredDownward = true;
                        this.hasTriggeredUpward = false;
                        this.hasTriggeredThird = false;
                        this.lastTriggerTime = jCurrentTimeMillis;
                    }
                } else {
                    f = y;
                }
                float fDp2px = MiuixUIUtils.dp2px(getContext(), 10.0f);
                if (Math.abs(f - f2) > fDp2px && Math.abs(f - f3) > fDp2px) {
                    this.hasTriggeredUpward = false;
                    this.hasTriggeredDownward = false;
                    this.hasTriggeredThird = false;
                }
                this.lastDistance = f;
            }
            if (!this.mIsDragging) {
                if (Math.abs(motionEvent.getX() - this.mTouchDownX) > this.mScaledTouchSlop) {
                    this.mIsDragging = true;
                    this.initialX = motionEvent.getX();
                    this.initialProgress = getProgress();
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                this.mIsTracking = false;
            } else {
                if (!this.mIsTracking) {
                    this.mTrainsOnSeekBarChangeListener.onStartTrackingTouch(this);
                    this.mIsTracking = true;
                }
                float x = (motionEvent.getX() - this.initialX) * this.mDistanceScale;
                if (ViewCompat.getLayoutDirection(this) == 1) {
                    x = -x;
                }
                int iMax = Math.max(0, Math.min((int) (this.initialProgress + (getWidth() > 0 ? (x / (getWidth() - (this.mProgressPaddingOffset * 2))) * (getMax() - getMinWrapper()) : 0.0f)), getMax()));
                if (iMax != getProgress()) {
                    super.setProgress(iMax);
                }
                return true;
            }
        } else if (action == 3) {
            if (Build.VERSION.SDK_INT >= 29) {
                final int i9 = this.mProgressSeekBarMinHeight;
                i = this.mDeviceLevel;
                if (i != 2) {
                    this.mHeadAlphaAnimator.folme().to(PROPERTY_HEAD_ALPHA, Float.valueOf(1.0f), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.4
                        @Override // miuix.animation.listener.TransitionListener
                        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_HEAD_ALPHA);
                            if (updateInfoFindBy != null) {
                                HyperProgressSeekBar.this.uHeadGlowAlpha = updateInfoFindBy.getFloatValue();
                                if (Build.VERSION.SDK_INT >= 33) {
                                    HyperProgressSeekBar.this.runtimeShader.setFloatUniform("uHeadGlowAlpha", HyperProgressSeekBar.this.uHeadGlowAlpha);
                                }
                                HyperProgressSeekBar.this.invalidate();
                            }
                        }
                    }));
                } else {
                    this.mHeadAlphaAnimator.folme().to(PROPERTY_HEAD_ALPHA, Float.valueOf(1.0f), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.4
                        @Override // miuix.animation.listener.TransitionListener
                        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_HEAD_ALPHA);
                            if (updateInfoFindBy != null) {
                                HyperProgressSeekBar.this.uHeadGlowAlpha = updateInfoFindBy.getFloatValue();
                                if (Build.VERSION.SDK_INT >= 33) {
                                    HyperProgressSeekBar.this.runtimeShader.setFloatUniform("uHeadGlowAlpha", HyperProgressSeekBar.this.uHeadGlowAlpha);
                                }
                                HyperProgressSeekBar.this.invalidate();
                            }
                        }
                    }));
                }
                i2 = this.mDeviceLevel;
                if (i2 != 0) {
                    this.mProgressAlphaAnimator.folme().to(PROPERTY_PROGRESS_ALPHA, Integer.valueOf(this.mProgressAlpha), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.5
                        @Override // miuix.animation.listener.TransitionListener
                        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_PROGRESS_ALPHA);
                            if (updateInfoFindBy != null) {
                                HyperProgressSeekBar.this.mDrawProgressAlpha = updateInfoFindBy.getIntValue();
                                HyperProgressSeekBar.this.invalidate();
                            }
                        }
                    }));
                } else {
                    this.mProgressAlphaAnimator.folme().to(PROPERTY_PROGRESS_ALPHA, Integer.valueOf(this.mProgressAlpha), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.5
                        @Override // miuix.animation.listener.TransitionListener
                        public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                            UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_PROGRESS_ALPHA);
                            if (updateInfoFindBy != null) {
                                HyperProgressSeekBar.this.mDrawProgressAlpha = updateInfoFindBy.getIntValue();
                                HyperProgressSeekBar.this.invalidate();
                            }
                        }
                    }));
                }
                this.mProgressHeightAnimator.folme().to(PROPERTY_PROGRESS_HEIGHT, Integer.valueOf(this.mProgressSeekBarMinHeight), new AnimConfig().setEase(FolmeEase.spring(0.95f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.HyperProgressSeekBar.6
                    @Override // miuix.animation.listener.TransitionListener
                    public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                        UpdateInfo updateInfoFindBy = UpdateInfo.findBy(collection, HyperProgressSeekBar.PROPERTY_PROGRESS_HEIGHT);
                        if (updateInfoFindBy != null) {
                            int intValue = updateInfoFindBy.getIntValue();
                            if (intValue % 2 != 0 && intValue > i9) {
                                intValue--;
                            }
                            HyperProgressSeekBar.this.mProgressHeight = Math.max(i9, intValue);
                            if (HyperProgressSeekBar.this.mDeviceLevel == 0 || HyperProgressSeekBar.this.mDeviceLevel == 1) {
                                HyperProgressSeekBar hyperProgressSeekBar = HyperProgressSeekBar.this;
                                hyperProgressSeekBar.setMinHeight(hyperProgressSeekBar.mProgressHeight);
                                HyperProgressSeekBar hyperProgressSeekBar2 = HyperProgressSeekBar.this;
                                hyperProgressSeekBar2.setMaxHeight(hyperProgressSeekBar2.mProgressHeight);
                                HyperProgressSeekBar hyperProgressSeekBar3 = HyperProgressSeekBar.this;
                                hyperProgressSeekBar3.setPadding(hyperProgressSeekBar3.getPaddingLeft(), (HyperProgressSeekBar.this.getHeight() - HyperProgressSeekBar.this.mProgressHeight) / 2, HyperProgressSeekBar.this.getPaddingRight(), (HyperProgressSeekBar.this.getHeight() - HyperProgressSeekBar.this.mProgressHeight) / 2);
                            }
                            HyperProgressSeekBar.this.invalidate();
                        }
                    }
                }));
                this.lastDistance = Float.MAX_VALUE;
                this.hasTriggeredUpward = false;
                this.hasTriggeredDownward = false;
                this.hasTriggeredThird = false;
                this.mDistanceScale = 1.0f;
                this.mIsDragging = false;
                if (this.mIsTracking) {
                    this.mTrainsOnSeekBarChangeListener.onStopTrackingTouch(this);
                    this.mIsTracking = false;
                }
            }
        }
        return true;
    }

    @Override // androidx.appcompat.widget.AppCompatSeekBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onDraw(Canvas canvas) {
        int i = this.mDeviceLevel;
        if (i == 2) {
            drawHighEndMode(canvas);
        } else if (i == 0) {
            drawPrimaryMode(canvas);
        } else if (i == 1) {
            drawMiddleMode(canvas);
        }
    }

    private void drawPrimaryMode(Canvas canvas) {
        float width = getWidth();
        float fCalculateProgressHeight = calculateProgressHeight();
        float paddingTop = getPaddingTop();
        float f = paddingTop + fCalculateProgressHeight;
        float fCalculateProgressLocation = calculateProgressLocation(width, fCalculateProgressHeight, getMinWrapper());
        if (this.mShadowRadius > 0.0f && this.mDeviceLevel == 0) {
            drawShadow(width, paddingTop, f, canvas);
        }
        drawBackground(canvas, paddingTop, f, width);
        drawProgressDrawable(canvas, fCalculateProgressHeight, paddingTop, f, width, fCalculateProgressLocation);
    }

    private float calculateProgressHeight() {
        int height;
        if (Build.VERSION.SDK_INT >= 29) {
            height = getMaxHeight();
        } else {
            height = (getHeight() - getPaddingBottom()) - getPaddingTop();
        }
        return height;
    }

    private float calculateProgressLocation(float f, float f2, int i) {
        if (isLayoutRtl()) {
            return ((f - this.mProgressPaddingOffset) - (f2 / 2.0f)) - (((getProgress() - i) / (getMax() - i)) * ((f - (this.mProgressPaddingOffset * 2)) - f2));
        }
        float progress = (getProgress() - i) / (getMax() - i);
        int i2 = this.mProgressPaddingOffset;
        return (progress * ((f - (i2 * 2)) - f2)) + i2 + (f2 / 2.0f);
    }

    private void drawShadow(float f, float f2, float f3, Canvas canvas) {
        canvas.save();
        RectF rectF = this.mProgressSeekBarRect;
        int i = this.mProgressPaddingOffset;
        rectF.set(i, (int) f2, (int) (f - i), (int) f3);
        this.mClipPath.reset();
        Path path = this.mClipPath;
        RectF rectF2 = this.mProgressSeekBarRect;
        float f4 = this.mClipRadius;
        path.addRoundRect(rectF2, f4, f4, Path.Direction.CW);
        canvas.clipOutPath(this.mClipPath);
        this.mPaint.setShadowLayer(this.mShadowRadius, this.mShadowX, this.mShadowY, this.mShadowColor);
        this.mPaint.setColor(getResources().getColor(R.color.miuix_color_transparent));
        int height = (getHeight() - this.mProgressSeekBarMaxHeight) / 2;
        this.mShadowRect.set(this.mProgressPaddingOffset - this.mShadowHorizontalExtend, height - this.mShadowVerticalExtend, (getWidth() - this.mProgressPaddingOffset) + this.mShadowHorizontalExtend, (getHeight() - height) + this.mShadowVerticalExtend);
        canvas.drawRect(this.mShadowRect, this.mPaint);
        canvas.restore();
    }

    private void drawBackground(Canvas canvas, float f, float f2, float f3) {
        Drawable drawable = this.mBackgroundDrawable;
        if (drawable != null) {
            drawable.setAlpha(255);
            Drawable drawable2 = this.mBackgroundDrawable;
            int i = this.mProgressPaddingOffset;
            drawable2.setBounds(i, (int) f, (int) (f3 - i), (int) f2);
            this.mBackgroundDrawable.draw(canvas);
        }
    }

    private void drawProgressDrawable(Canvas canvas, float f, float f2, float f3, float f4, float f5) {
        if (this.mProgressDrawable != null) {
            if (isLayoutRtl()) {
                this.mProgressDrawable.setBounds((int) (f5 - (f / 2.0f)), (int) f2, (int) (f4 - this.mProgressPaddingOffset), (int) f3);
            } else {
                this.mProgressDrawable.setBounds(this.mProgressPaddingOffset, (int) f2, (int) (f5 + (f / 2.0f)), (int) f3);
            }
            this.mProgressDrawable.setAlpha(this.mDrawProgressAlpha);
            this.mProgressDrawable.draw(canvas);
        }
    }

    private void drawMiddleMode(Canvas canvas) {
        drawPrimaryMode(canvas);
        drawHighEndMode(canvas);
    }

    private void drawHighEndMode(Canvas canvas) {
        this.uTrackSize[1] = this.mProgressHeight;
        if (Build.VERSION.SDK_INT >= 33) {
            this.runtimeShader.setFloatUniform("uTrackSize", this.uTrackSize);
            this.runtimeShader.setFloatUniform("uTrackProgress", (getProgress() - getMinWrapper()) / (getMax() - getMinWrapper()));
        }
        float[] fArr = this.uTrackCanvasSize;
        canvas.drawRect(0.0f, 0.0f, fArr[0], fArr[1], this.mPaint);
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int i5 = this.mDeviceLevel;
        if ((i5 == 2 || i5 == 1) && Build.VERSION.SDK_INT >= 33) {
            this.uTrackSize[0] = getWidth() - (this.uTrackPosition[0] * 2.0f);
            this.runtimeShader.setFloatUniform("uTrackSize", this.uTrackSize);
            this.runtimeShader.setIntUniform("uIsRtl", isLayoutRtl() ? 1 : 0);
        }
    }

    private boolean isLayoutRtl() {
        return ViewCompat.getLayoutDirection(this) == 1;
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        int i5 = this.mDeviceLevel;
        if ((i5 == 2 || i5 == 1) && Build.VERSION.SDK_INT >= 33) {
            float[] fArr = this.uTrackCanvasSize;
            fArr[0] = i;
            fArr[1] = i2;
            this.runtimeShader.setFloatUniform("uTrackCanvasSize", fArr);
            this.runtimeShader.setFloatUniform("uResolution", this.uTrackCanvasSize);
        }
    }

    @Override // androidx.appcompat.widget.AppCompatSeekBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int i = this.mDeviceLevel;
        if (i == 0 || i == 1) {
            updatePrimaryColor();
        }
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
            if (drawableFindDrawableByLayerId2 == null || !(drawableFindDrawableByLayerId2 instanceof GradientDrawable)) {
                return;
            }
            GradientDrawable gradientDrawable3 = (GradientDrawable) drawableFindDrawableByLayerId2;
            ColorStateList color2 = gradientDrawable3.getColor();
            if (this.mProgressColorStateList == null && color2 != null) {
                this.mProgressColorStateList = color2;
            }
            ColorStateList colorStateList3 = this.mProgressColorStateList;
            if (colorStateList3 != null) {
                if (colorStateList3.getColorForState(new int[]{-16842910}, this.mDefaultBackgroundPrimaryDisableColor) == this.mBackgroundPrimaryDisableColor && this.mProgressColorStateList.getColorForState(ENABLED_STATE_SET, this.mDefaultBackgroundPrimaryColor) == this.mBackgroundPrimaryColor) {
                    return;
                }
                GradientDrawable gradientDrawable4 = (GradientDrawable) gradientDrawable3.mutate();
                ColorStateList colorStateList4 = new ColorStateList(new int[][]{new int[]{-16842910}, new int[0]}, new int[]{this.mBackgroundPrimaryDisableColor, this.mBackgroundPrimaryColor});
                this.mProgressColorStateList = colorStateList4;
                gradientDrawable4.setColor(colorStateList4);
            }
        }
    }

    @Override // android.widget.SeekBar
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener2 = this.mTrainsOnSeekBarChangeListener;
        if (onSeekBarChangeListener == onSeekBarChangeListener2) {
            super.setOnSeekBarChangeListener(onSeekBarChangeListener2);
        } else {
            this.mOnSeekBarChangeListener = onSeekBarChangeListener;
        }
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
        private WeakReference<HyperProgressSeekBar> mSeekBarRef;

        public ColorUpdateRunner(HyperProgressSeekBar hyperProgressSeekBar) {
            this.mSeekBarRef = new WeakReference<>(hyperProgressSeekBar);
        }

        @Override // java.lang.Runnable
        public void run() {
            WeakReference<HyperProgressSeekBar> weakReference = this.mSeekBarRef;
            HyperProgressSeekBar hyperProgressSeekBar = weakReference == null ? null : weakReference.get();
            if (hyperProgressSeekBar != null) {
                hyperProgressSeekBar.updatePrimaryColor();
            }
        }
    }
}
