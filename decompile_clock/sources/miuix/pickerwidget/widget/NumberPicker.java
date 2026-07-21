package miuix.pickerwidget.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.ViewUtils;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.GravityCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import miuix.internal.util.ReflectUtil;
import miuix.pickerwidget.R;
import miuix.pickerwidget.internal.util.SimpleNumberFormatter;
import miuix.pickerwidget.internal.util.async.WorkerThreads;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes3.dex */
public class NumberPicker extends LinearLayout {
    private static final int BACKGROUND_PADDING = 10;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int MAX_HEIGHT = 202;
    private static final float SELECTION_DIVIDERS_DISTANCE = 45.0f;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = 1;
    static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final String SOUND_PLAY_THREAD = "NumberPicker_sound_play";
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    private int MARGIN_LABEL_LEFT;
    private int MARGIN_LABEL_TOP;
    private AccessibilityNodeProviderImpl mAccessibilityNodeProvider;
    private final Scroller mAdjustScroller;
    private BeginSoftInputOnLongPressCommand mBeginSoftInputOnLongPressCommand;
    private int mBottomSelectionDividerBottom;
    private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
    private final boolean mComputeMaxWidth;
    private int mCurrentScrollOffset;
    private boolean mDecrementVirtualButtonPressed;
    private String mDisplayedMaxText;
    private float mDisplayedMaxTextWidth;
    private String[] mDisplayedValues;
    private final Scroller mFlingScroller;
    private Formatter mFormatter;
    private final boolean mHasSelectorWheel;
    private final int mId;
    private boolean mIgnoreMoveEvents;
    private boolean mIncrementVirtualButtonPressed;
    private int mInitialScrollOffset;
    private final EditText mInputText;
    private CharSequence mLabel;
    private Paint mLabelPaint;
    private int mLabelTextColor;
    private int mLabelTextSize;
    private float mLabelTextSizeThreshold;
    private float mLabelTextSizeTrimFactor;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode;
    private int mLastHoveredChildVirtualViewId;
    private long mLongPressUpdateInterval;
    private float mMaxFlingSpeedFactor;
    private final int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private boolean mMeasureBackgroundEnabled;
    private final int mMinHeight;
    private int mMinValue;
    private final int mMinWidth;
    private int mMinimumFlingVelocity;
    private String mModDevice;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private int mOriginLabelTextSize;
    private int mOriginTextSizeHighlight;
    private int mOriginTextSizeHint;
    private final PressedStateHelper mPressedStateHelper;
    private int mPreviousScrollerY;
    private int mScrollState;
    private final int mSelectionDividerHeight;
    private final int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private final int[] mSelectorIndices;
    private int mSelectorTextGapHeight;
    private final Paint mSelectorWheelPaint;
    private SetSelectionCommand mSetSelectionCommand;
    private boolean mShowSoftInputOnTap;
    private SoundPlayHandler mSoundPlayHandler;
    private int mTextColorHighlight;
    private int mTextColorHint;
    private int mTextPadding;
    private final int mTextSize;
    private int mTextSizeHighlight;
    private int mTextSizeHint;
    private float mTextSizeThreshold;
    private float mTextSizeTrimFactor;
    private int mTopSelectionDividerTop;
    private int mTouchSlop;
    private String mUpdateText;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private boolean mWrapSelectorWheel;
    private static final int DEFAULT_LAYOUT_RESOURCE_ID = R.layout.miuix_appcompat_number_picker_layout;
    private static final AtomicInteger sIdGenerator = new AtomicInteger(0);
    static final Formatter TWO_DIGIT_FORMATTER = new NumberFormatter(2);
    private static final int[] PRESSED_STATE_SET = {android.R.attr.state_pressed};
    private static final char[] DIGIT_CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public interface Formatter {
        String format(int i);
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScrollStateChange(NumberPicker numberPicker, int i);
    }

    public interface OnValueChangeListener {
        void onValueChange(NumberPicker numberPicker, int i, int i2);
    }

    private float getTextSize(float f, int i, int i2) {
        return f >= 1.0f ? i2 : (f * (i2 - i)) + i;
    }

    @Override // android.view.View
    protected float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    @Override // android.view.View
    protected float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [boolean, byte] */
    static /* synthetic */ boolean access$1180(NumberPicker numberPicker, int i) {
        ?? r2 = (i ^ (numberPicker.mIncrementVirtualButtonPressed ? 1 : 0)) == true ? (byte) 1 : (byte) 0;
        numberPicker.mIncrementVirtualButtonPressed = r2;
        return r2;
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [boolean, byte] */
    static /* synthetic */ boolean access$1380(NumberPicker numberPicker, int i) {
        ?? r2 = (i ^ (numberPicker.mDecrementVirtualButtonPressed ? 1 : 0)) == true ? (byte) 1 : (byte) 0;
        numberPicker.mDecrementVirtualButtonPressed = r2;
        return r2;
    }

    static class NumberFormatter implements Formatter {
        private final int iWidth;

        public NumberFormatter() {
            this.iWidth = -1;
        }

        public NumberFormatter(int i) {
            this.iWidth = i;
        }

        @Override // miuix.pickerwidget.widget.NumberPicker.Formatter
        public String format(int i) {
            return SimpleNumberFormatter.format(this.iWidth, i);
        }
    }

    private static class SoundPlayHandler extends Handler {
        private static final int MSG_INIT = 0;
        private static final int MSG_PLAY = 1;
        private static final int MSG_RELEASE = 2;
        private static final SoundPlayerContainer sPlayerContainer = new SoundPlayerContainer();

        private static class SoundPlayerContainer {
            private static final long INTERVAL = 50;
            private long mPrevPlayTime;
            private final Set<Integer> mRefs;
            private int mSoundId;
            private SoundPool mSoundPlayer;

            private SoundPlayerContainer() {
                this.mRefs = new CopyOnWriteArraySet();
            }

            void init(Context context, int i) {
                if (this.mSoundPlayer == null) {
                    SoundPool soundPool = new SoundPool(1, 1, 0);
                    this.mSoundPlayer = soundPool;
                    this.mSoundId = soundPool.load(context, R.raw.number_picker_value_change, 1);
                }
                this.mRefs.add(Integer.valueOf(i));
            }

            void play() {
                long jCurrentTimeMillis = System.currentTimeMillis();
                SoundPool soundPool = this.mSoundPlayer;
                if (soundPool == null || jCurrentTimeMillis - this.mPrevPlayTime <= INTERVAL) {
                    return;
                }
                soundPool.play(this.mSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
                this.mPrevPlayTime = jCurrentTimeMillis;
            }

            void release(int i) {
                SoundPool soundPool;
                if (this.mRefs.remove(Integer.valueOf(i)) && this.mRefs.isEmpty() && (soundPool = this.mSoundPlayer) != null) {
                    soundPool.release();
                    this.mSoundPlayer = null;
                }
            }
        }

        SoundPlayHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i == 0) {
                sPlayerContainer.init((Context) message.obj, message.arg1);
            } else if (i == 1) {
                sPlayerContainer.play();
            } else {
                if (i != 2) {
                    return;
                }
                sPlayerContainer.release(message.arg1);
            }
        }

        void init(Context context, int i) {
            Message messageObtainMessage = obtainMessage(0, i, 0);
            messageObtainMessage.obj = context;
            sendMessage(messageObtainMessage);
        }

        void play() {
            sendMessage(obtainMessage(1));
        }

        void stop() {
            removeMessages(1);
        }

        void release(int i) {
            sendMessage(obtainMessage(2, i, 0));
        }
    }

    public NumberPicker(Context context) {
        this(context, null);
    }

    public NumberPicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.numberPickerStyle);
    }

    public NumberPicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mId = sIdGenerator.incrementAndGet();
        this.MARGIN_LABEL_LEFT = 1;
        this.MARGIN_LABEL_TOP = 2;
        this.mMaxWidth = 400;
        this.mLongPressUpdateInterval = 300L;
        this.mSelectorIndexToStringCache = new SparseArray<>();
        this.mSelectorIndices = new int[3];
        this.mInitialScrollOffset = Integer.MIN_VALUE;
        this.mScrollState = 0;
        this.mLastHandledDownDpadKeyCode = -1;
        this.mTextSizeTrimFactor = 0.95f;
        this.mLabelTextSizeTrimFactor = 0.8f;
        this.mMaxFlingSpeedFactor = 1.0f;
        this.mMeasureBackgroundEnabled = true;
        float f = getResources().getDisplayMetrics().density;
        this.MARGIN_LABEL_LEFT = getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_number_picker_label_margin_left);
        this.MARGIN_LABEL_TOP = getResources().getDimensionPixelOffset(R.dimen.miuix_appcompat_number_picker_label_margin_top);
        parseStyle(attributeSet, i);
        initSoundPlayer();
        this.mHasSelectorWheel = true;
        this.mSelectionDividerHeight = (int) TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics());
        this.mSelectionDividersDistance = (int) (SELECTION_DIVIDERS_DISTANCE * f);
        this.mMinHeight = -1;
        this.mMaxHeight = (int) (f * 202.0f);
        this.mMinWidth = -1;
        this.mMaxWidth = -1;
        this.mComputeMaxWidth = true;
        this.mPressedStateHelper = new PressedStateHelper();
        setWillNotDraw(!true);
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.miuix_appcompat_number_picker_layout, (ViewGroup) this, true);
        EditText editText = (EditText) findViewById(R.id.number_picker_input);
        this.mInputText = editText;
        initInputText();
        initThreshHolds();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity() / 8;
        this.mTextSize = (int) editText.getTextSize();
        this.mSelectorWheelPaint = initSelectorWheelPaint();
        initLabelPaint();
        this.mFlingScroller = new Scroller(getContext(), null, true);
        this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
        updateInputTextView();
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    public void setMeasureBackgroundEnabled(boolean z) {
        this.mMeasureBackgroundEnabled = z;
    }

    private void parseStyle(AttributeSet attributeSet, int i) {
        Resources resources = getResources();
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.NumberPicker, i, R.style.Widget_NumberPicker_DayNight);
        this.mLabel = typedArrayObtainStyledAttributes.getText(R.styleable.NumberPicker_android_text);
        this.mTextSizeHighlight = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.NumberPicker_textSizeHighlight, resources.getDimensionPixelOffset(R.dimen.miuix_appcompat_number_picker_text_size_highlight_normal));
        this.mTextSizeHint = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.NumberPicker_textSizeHint, resources.getDimensionPixelOffset(R.dimen.miuix_appcompat_number_picker_text_size_hint_normal));
        this.mLabelTextSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.NumberPicker_android_labelTextSize, resources.getDimensionPixelOffset(R.dimen.miuix_appcompat_number_picker_label_text_size));
        this.mTextColorHighlight = typedArrayObtainStyledAttributes.getColor(R.styleable.NumberPicker_android_textColorHighlight, resources.getColor(R.color.miuix_appcompat_default_number_picker_highlight_color));
        this.mTextColorHint = typedArrayObtainStyledAttributes.getColor(R.styleable.NumberPicker_android_textColorHint, resources.getColor(R.color.miuix_appcompat_default_number_picker_hint_color));
        this.mLabelTextColor = typedArrayObtainStyledAttributes.getColor(R.styleable.NumberPicker_labelTextColor, resources.getColor(R.color.miuix_appcompat_number_picker_label_color));
        this.mTextPadding = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.NumberPicker_labelPadding, resources.getDimensionPixelOffset(R.dimen.miuix_appcompat_number_picker_label_padding));
        typedArrayObtainStyledAttributes.recycle();
        this.mOriginLabelTextSize = this.mLabelTextSize;
        this.mOriginTextSizeHighlight = this.mTextSizeHighlight;
        this.mOriginTextSizeHint = this.mTextSizeHint;
    }

    private void initInputText() {
        this.mInputText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: miuix.pickerwidget.widget.NumberPicker.1
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    NumberPicker.this.mInputText.selectAll();
                } else {
                    NumberPicker.this.mInputText.setSelection(0, 0);
                    NumberPicker.this.validateInputTextView(view);
                }
            }
        });
        this.mInputText.setFilters(new InputFilter[]{new InputTextFilter()});
        this.mInputText.setRawInputType(2);
        this.mInputText.setImeOptions(6);
        this.mInputText.setVisibility(4);
        this.mInputText.setGravity(GravityCompat.START);
        this.mInputText.setScaleX(0.0f);
        this.mInputText.setSaveEnabled(false);
        EditText editText = this.mInputText;
        editText.setPadding(this.mTextPadding, editText.getPaddingTop(), this.mTextPadding, this.mInputText.getPaddingRight());
    }

    private void initThreshHolds() {
        this.mLabelTextSizeThreshold = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_label_text_size_small);
        this.mTextSizeThreshold = getContext().getResources().getDimensionPixelSize(R.dimen.miuix_text_size_small);
    }

    private Paint initSelectorWheelPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(this.mTextSizeHighlight);
        paint.setTypeface(this.mInputText.getTypeface());
        paint.setColor(this.mInputText.getTextColors().getColorForState(ENABLED_STATE_SET, -1));
        return paint;
    }

    private void initLabelPaint() {
        Paint paint = new Paint();
        this.mLabelPaint = paint;
        paint.setAntiAlias(true);
        this.mLabelPaint.setFakeBoldText(true);
        this.mLabelPaint.setColor(this.mLabelTextColor);
        this.mLabelPaint.setTextSize(this.mLabelTextSize);
    }

    public void setTextColorHighlight(int i) {
        if (this.mTextColorHighlight != i) {
            this.mTextColorHighlight = i;
            invalidate();
        }
    }

    public void setLabelTextColor(int i) {
        if (this.mLabelTextColor != i) {
            this.mLabelTextColor = i;
            this.mLabelPaint.setColor(i);
            invalidate();
        }
    }

    public int getTextSizeHighlight() {
        return this.mTextSizeHighlight;
    }

    public void setTextSizeHighlight(int i) {
        this.mTextSizeHighlight = i;
        this.mSelectorWheelPaint.setTextSize(i);
        this.mDisplayedMaxTextWidth = this.mSelectorWheelPaint.measureText(this.mDisplayedMaxText);
        initializeSelectorWheel();
        invalidate();
    }

    public int getTextSizeHint() {
        return this.mTextSizeHint;
    }

    public void setTextSizeHint(int i) {
        this.mTextSizeHint = i;
        invalidate();
    }

    public int getMarginLabelLeft() {
        return this.MARGIN_LABEL_LEFT;
    }

    public int getOriginTextSizeHighlight() {
        return this.mOriginTextSizeHighlight;
    }

    public int getOriginTextSizeHint() {
        return this.mOriginTextSizeHint;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        tryComputeMaxWidth();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!this.mHasSelectorWheel) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int measuredWidth2 = this.mInputText.getMeasuredWidth();
        int measuredHeight2 = this.mInputText.getMeasuredHeight();
        int i5 = (measuredWidth - measuredWidth2) / 2;
        int i6 = (measuredHeight - measuredHeight2) / 2;
        this.mInputText.layout(i5, i6, measuredWidth2 + i5, measuredHeight2 + i6);
        if (z) {
            initializeSelectorWheel();
            initializeFadingEdges();
            int height = getHeight();
            int i7 = this.mSelectionDividersDistance;
            int i8 = this.mSelectionDividerHeight;
            int i9 = ((height - i7) / 2) - i8;
            this.mTopSelectionDividerTop = i9;
            this.mBottomSelectionDividerBottom = i9 + (i8 * 2) + i7;
        }
        trimLabelTextSize((((getRight() - getLeft()) + getPaddingLeft()) - getPaddingRight()) / 2.0f);
        Drawable background = getBackground();
        int i10 = this.mMaxWidth + 20;
        if (this.mMeasureBackgroundEnabled && (background instanceof StateListDrawable)) {
            StateListDrawable stateListDrawable = (StateListDrawable) background;
            if (Build.VERSION.SDK_INT >= 29) {
                int stateCount = stateListDrawable.getStateCount();
                for (int i11 = 0; i11 < stateCount; i11++) {
                    Drawable stateDrawable = stateListDrawable.getStateDrawable(i11);
                    if (stateDrawable instanceof LayerDrawable) {
                        LayerDrawable layerDrawable = (LayerDrawable) stateDrawable;
                        int numberOfLayers = layerDrawable.getNumberOfLayers();
                        for (int i12 = 0; i12 < numberOfLayers; i12++) {
                            Drawable drawableFindDrawableByLayerId = layerDrawable.findDrawableByLayerId(layerDrawable.getId(i12));
                            if (drawableFindDrawableByLayerId instanceof GradientDrawable) {
                                ((GradientDrawable) drawableFindDrawableByLayerId).setSize(getWidth() > i10 ? i10 : getWidth(), getHeight());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (!this.mHasSelectorWheel) {
            super.onMeasure(i, i2);
        } else {
            super.onMeasure(makeMeasureSpec(i, this.mMaxWidth), makeMeasureSpec(i2, this.mMaxHeight));
            setMeasuredDimension(resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), i), resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), i2));
        }
    }

    private void initSoundPlayer() {
        if (this.mSoundPlayHandler == null) {
            SoundPlayHandler soundPlayHandler = new SoundPlayHandler(WorkerThreads.acquireWorker(SOUND_PLAY_THREAD));
            this.mSoundPlayHandler = soundPlayHandler;
            soundPlayHandler.init(getContext().getApplicationContext(), this.mId);
        }
    }

    private void releaseSoundPlayer() {
        SoundPlayHandler soundPlayHandler = this.mSoundPlayHandler;
        if (soundPlayHandler != null) {
            soundPlayHandler.release(this.mId);
            this.mSoundPlayHandler = null;
        }
    }

    private void playSound() {
        SoundPlayHandler soundPlayHandler = this.mSoundPlayHandler;
        if (soundPlayHandler != null) {
            soundPlayHandler.play();
        }
    }

    private void stopSoundPlay() {
        SoundPlayHandler soundPlayHandler = this.mSoundPlayHandler;
        if (soundPlayHandler != null) {
            soundPlayHandler.stop();
        }
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int finalY = scroller.getFinalY() - scroller.getCurrY();
        int i = this.mInitialScrollOffset - ((this.mCurrentScrollOffset + finalY) % this.mSelectorElementHeight);
        if (i == 0) {
            return false;
        }
        int iAbs = Math.abs(i);
        int i2 = this.mSelectorElementHeight;
        if (iAbs > i2 / 2) {
            i = i > 0 ? i - i2 : i + i2;
        }
        scrollBy(0, finalY + i);
        return true;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.mHasSelectorWheel || !isEnabled() || motionEvent.getActionMasked() != 0) {
            return false;
        }
        removeAllCallbacks();
        this.mInputText.setVisibility(4);
        float y = motionEvent.getY();
        this.mLastDownEventY = y;
        this.mLastDownOrMoveEventY = y;
        this.mLastDownEventTime = motionEvent.getEventTime();
        this.mIgnoreMoveEvents = false;
        this.mShowSoftInputOnTap = false;
        float f = this.mLastDownEventY;
        if (f < this.mTopSelectionDividerTop) {
            if (this.mScrollState == 0) {
                this.mPressedStateHelper.buttonPressDelayed(2);
            }
        } else if (f > this.mBottomSelectionDividerBottom && this.mScrollState == 0) {
            this.mPressedStateHelper.buttonPressDelayed(1);
        }
        if (!this.mFlingScroller.isFinished()) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
            onScrollStateChange(0);
        } else if (!this.mAdjustScroller.isFinished()) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
        } else {
            float f2 = this.mLastDownEventY;
            if (f2 < this.mTopSelectionDividerTop) {
                postChangeCurrentByOneFromLongPress(false, ViewConfiguration.getLongPressTimeout());
            } else if (f2 > this.mBottomSelectionDividerBottom) {
                postChangeCurrentByOneFromLongPress(true, ViewConfiguration.getLongPressTimeout());
            } else {
                this.mShowSoftInputOnTap = true;
                postBeginSoftInputOnLongPressCommand();
            }
        }
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:26:0x005c  */
    /* JADX WARN: Code duplicated, block: B:28:0x0082  */
    /* JADX WARN: Code duplicated, block: B:31:0x008f  */
    /* JADX WARN: Code duplicated, block: B:32:0x0096  */
    /* JADX WARN: Code duplicated, block: B:45:0x00dd  */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int yVelocity;
        int iAbs;
        if (!isEnabled() || !this.mHasSelectorWheel) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1) {
            removeBeginSoftInputCommand();
            removeChangeCurrentByOneFromLongPress();
            this.mPressedStateHelper.cancel();
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
            yVelocity = (int) velocityTracker.getYVelocity();
            if (Math.abs(yVelocity) >= Math.abs(this.mMaximumFlingVelocity)) {
                yVelocity = (int) (yVelocity * this.mMaxFlingSpeedFactor);
            }
            if (Math.abs(yVelocity) > this.mMinimumFlingVelocity) {
                fling(yVelocity);
                onScrollStateChange(2);
            } else {
                int y = (int) motionEvent.getY();
                iAbs = (int) Math.abs(y - this.mLastDownEventY);
                long eventTime = motionEvent.getEventTime() - this.mLastDownEventTime;
                if (iAbs > this.mTouchSlop && eventTime < ViewConfiguration.getLongPressTimeout()) {
                    if (this.mShowSoftInputOnTap) {
                        this.mShowSoftInputOnTap = false;
                    } else {
                        int i = (y / this.mSelectorElementHeight) - 1;
                        if (i > 0) {
                            changeValueByOne(true);
                            this.mPressedStateHelper.buttonTapped(1);
                        } else if (i < 0) {
                            changeValueByOne(false);
                            this.mPressedStateHelper.buttonTapped(2);
                        } else {
                            ensureScrollWheelAdjusted();
                        }
                    }
                } else {
                    ensureScrollWheelAdjusted();
                }
                onScrollStateChange(0);
            }
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        } else if (actionMasked != 2) {
            if (actionMasked == 3) {
                removeBeginSoftInputCommand();
                removeChangeCurrentByOneFromLongPress();
                this.mPressedStateHelper.cancel();
                VelocityTracker velocityTracker2 = this.mVelocityTracker;
                velocityTracker2.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                yVelocity = (int) velocityTracker2.getYVelocity();
                if (Math.abs(yVelocity) >= Math.abs(this.mMaximumFlingVelocity)) {
                    yVelocity = (int) (yVelocity * this.mMaxFlingSpeedFactor);
                }
                if (Math.abs(yVelocity) > this.mMinimumFlingVelocity) {
                    fling(yVelocity);
                    onScrollStateChange(2);
                } else {
                    int y2 = (int) motionEvent.getY();
                    iAbs = (int) Math.abs(y2 - this.mLastDownEventY);
                    long eventTime2 = motionEvent.getEventTime() - this.mLastDownEventTime;
                    if (iAbs > this.mTouchSlop) {
                        ensureScrollWheelAdjusted();
                    } else {
                        ensureScrollWheelAdjusted();
                    }
                    onScrollStateChange(0);
                }
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        } else if (!this.mIgnoreMoveEvents) {
            float y3 = motionEvent.getY();
            if (this.mScrollState != 1) {
                if (((int) Math.abs(y3 - this.mLastDownEventY)) > this.mTouchSlop) {
                    removeAllCallbacks();
                    onScrollStateChange(1);
                }
            } else {
                scrollBy(0, (int) (y3 - this.mLastDownOrMoveEventY));
                invalidate();
            }
            this.mLastDownOrMoveEventY = y3;
        }
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        getParent().requestDisallowInterceptTouchEvent(true);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x004c, code lost:
    
        requestFocus();
        r5.mLastHandledDownDpadKeyCode = r0;
        removeAllCallbacks();
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x005a, code lost:
    
        if (r5.mFlingScroller.isFinished() == false) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x005c, code lost:
    
        if (r0 != 20) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x005e, code lost:
    
        r6 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0060, code lost:
    
        r6 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0061, code lost:
    
        changeValueByOne(r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0064, code lost:
    
        return true;
     */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean dispatchKeyEvent(android.view.KeyEvent r6) {
        /*
            r5 = this;
            int r0 = r6.getKeyCode()
            r1 = 19
            r2 = 20
            if (r0 == r1) goto L19
            if (r0 == r2) goto L19
            r1 = 23
            if (r0 == r1) goto L15
            r1 = 66
            if (r0 == r1) goto L15
            goto L65
        L15:
            r5.removeAllCallbacks()
            goto L65
        L19:
            boolean r1 = r5.mHasSelectorWheel
            if (r1 != 0) goto L1e
            goto L65
        L1e:
            int r1 = r6.getAction()
            r3 = 1
            if (r1 == 0) goto L30
            if (r1 == r3) goto L28
            goto L65
        L28:
            int r1 = r5.mLastHandledDownDpadKeyCode
            if (r1 != r0) goto L65
            r6 = -1
            r5.mLastHandledDownDpadKeyCode = r6
            return r3
        L30:
            boolean r1 = r5.mWrapSelectorWheel
            if (r1 != 0) goto L42
            if (r0 != r2) goto L37
            goto L42
        L37:
            int r1 = r5.getValue()
            int r4 = r5.getMinValue()
            if (r1 <= r4) goto L65
            goto L4c
        L42:
            int r1 = r5.getValue()
            int r4 = r5.getMaxValue()
            if (r1 >= r4) goto L65
        L4c:
            r5.requestFocus()
            r5.mLastHandledDownDpadKeyCode = r0
            r5.removeAllCallbacks()
            android.widget.Scroller r6 = r5.mFlingScroller
            boolean r6 = r6.isFinished()
            if (r6 == 0) goto L64
            if (r0 != r2) goto L60
            r6 = r3
            goto L61
        L60:
            r6 = 0
        L61:
            r5.changeValueByOne(r6)
        L64:
            return r3
        L65:
            boolean r6 = super.dispatchKeyEvent(r6)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.pickerwidget.widget.NumberPicker.dispatchKeyEvent(android.view.KeyEvent):boolean");
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTrackballEvent(motionEvent);
    }

    @Override // android.view.View
    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(0, currY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    @Override // android.view.View
    public void scrollBy(int i, int i2) {
        int[] iArr = this.mSelectorIndices;
        boolean z = this.mWrapSelectorWheel;
        if (!z && i2 > 0 && iArr[1] <= this.mMinValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
            return;
        }
        if (!z && i2 < 0 && iArr[1] >= this.mMaxValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
            return;
        }
        this.mCurrentScrollOffset += i2;
        while (i2 > 0) {
            int i3 = this.mCurrentScrollOffset;
            if (i3 - this.mInitialScrollOffset <= this.mSelectorTextGapHeight) {
                break;
            }
            this.mCurrentScrollOffset = i3 - this.mSelectorElementHeight;
            decrementSelectorIndices(iArr);
            setValueInternal(iArr[1], true);
            if (!this.mWrapSelectorWheel && iArr[1] <= this.mMinValue) {
                this.mCurrentScrollOffset = this.mInitialScrollOffset;
            }
        }
        while (i2 < 0) {
            int i4 = this.mCurrentScrollOffset;
            if (i4 - this.mInitialScrollOffset >= (-this.mSelectorTextGapHeight)) {
                return;
            }
            this.mCurrentScrollOffset = i4 + this.mSelectorElementHeight;
            incrementSelectorIndices(iArr);
            setValueInternal(iArr[1], true);
            if (!this.mWrapSelectorWheel && iArr[1] >= this.mMaxValue) {
                this.mCurrentScrollOffset = this.mInitialScrollOffset;
            }
        }
    }

    public void setLabel(String str) {
        CharSequence charSequence = this.mLabel;
        if ((charSequence != null || str == null) && (charSequence == null || charSequence.equals(str))) {
            return;
        }
        this.mLabel = str;
        invalidate();
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangeListener) {
        this.mOnValueChangeListener = onValueChangeListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setFormatter(Formatter formatter) {
        if (formatter == this.mFormatter) {
            return;
        }
        this.mFormatter = formatter;
        initializeSelectorWheelIndices();
        updateInputTextView();
    }

    public void setValue(int i) {
        setValueInternal(i, false);
    }

    private void tryComputeMaxWidth() {
        String str;
        float f;
        if (this.mComputeMaxWidth) {
            this.mSelectorWheelPaint.setTextSize(this.mTextSizeHighlight);
            String[] strArr = this.mDisplayedValues;
            int i = 0;
            if (strArr == null) {
                float f2 = 0.0f;
                int i2 = 0;
                while (i < 9) {
                    float fMeasureText = this.mSelectorWheelPaint.measureText(String.valueOf(i));
                    if (fMeasureText > f2) {
                        i2 = i;
                        f2 = fMeasureText;
                    }
                    i++;
                }
                int length = formatNumber(this.mMaxValue).length();
                f = (int) (length * f2);
                char[] cArr = new char[length];
                Arrays.fill(cArr, (char) (i2 + 48));
                str = new String(cArr);
            } else {
                int length2 = strArr.length;
                str = null;
                float f3 = -1.0f;
                while (i < length2) {
                    String str2 = this.mDisplayedValues[i];
                    float fMeasureText2 = this.mSelectorWheelPaint.measureText(str2);
                    if (fMeasureText2 > f3) {
                        str = str2;
                        f3 = fMeasureText2;
                    }
                    i++;
                }
                f = f3;
            }
            this.mDisplayedMaxTextWidth = f;
            this.mDisplayedMaxText = str;
            float paddingLeft = f + this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight() + getPaddingLeft() + getPaddingRight();
            if (this.mMaxWidth != paddingLeft) {
                int i3 = this.mMinWidth;
                if (paddingLeft > i3) {
                    this.mMaxWidth = (int) paddingLeft;
                } else {
                    this.mMaxWidth = i3;
                }
            }
        }
    }

    protected float getLabelWidth() {
        if (TextUtils.isEmpty(this.mLabel) || isInternationalBuild()) {
            return 0.0f;
        }
        return this.mLabelPaint.measureText(this.mLabel.toString());
    }

    public float getOriginalLabelWidth() {
        if (TextUtils.isEmpty(this.mLabel) || isInternationalBuild()) {
            return 0.0f;
        }
        float textSize = this.mLabelPaint.getTextSize();
        this.mLabelPaint.setTextSize(this.mOriginLabelTextSize);
        float fMeasureText = this.mLabelPaint.measureText(this.mLabel.toString());
        this.mLabelPaint.setTextSize(textSize);
        return fMeasureText;
    }

    protected String getDisplayedMaxText() {
        String str = this.mDisplayedMaxText;
        return str == null ? "" : str;
    }

    protected float getDisplayedMaxTextWidth() {
        float textSize = this.mSelectorWheelPaint.getTextSize();
        this.mSelectorWheelPaint.setTextSize(this.mOriginTextSizeHighlight);
        float fMeasureText = this.mSelectorWheelPaint.measureText(getDisplayedMaxText());
        this.mSelectorWheelPaint.setTextSize(textSize);
        return fMeasureText;
    }

    public void setLabelTextSizeThreshold(float f) {
        this.mLabelTextSizeThreshold = Math.max(f, 0.0f);
    }

    public void setLabelTextSizeTrimFactor(float f) {
        if (f <= 0.0f || f >= 1.0f) {
            return;
        }
        this.mLabelTextSizeTrimFactor = f;
    }

    public void setTextSizeTrimFactor(float f) {
        if (f <= 0.0f || f >= 1.0f) {
            return;
        }
        this.mTextSizeTrimFactor = f;
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    public void setWrapSelectorWheel(boolean z) {
        boolean z2 = this.mMaxValue - this.mMinValue >= this.mSelectorIndices.length;
        if ((!z || z2) && z != this.mWrapSelectorWheel) {
            this.mWrapSelectorWheel = z;
        }
        refreshWheel();
    }

    public void setOnLongPressUpdateInterval(long j) {
        this.mLongPressUpdateInterval = j;
    }

    public int getValue() {
        return this.mValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public void setMinValue(int i) {
        if (this.mMinValue == i) {
            return;
        }
        if (i < 0) {
            throw new IllegalArgumentException("minValue must be >= 0");
        }
        this.mMinValue = i;
        if (i > this.mValue) {
            this.mValue = i;
        }
        setWrapSelectorWheel(this.mMaxValue - i > this.mSelectorIndices.length);
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMaxValue(int i) {
        if (this.mMaxValue == i) {
            return;
        }
        if (i < 0) {
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
        this.mMaxValue = i;
        if (i < this.mValue) {
            this.mValue = i;
        }
        setWrapSelectorWheel(i - this.mMinValue > this.mSelectorIndices.length);
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public void setDisplayedValues(String[] strArr) {
        if (this.mDisplayedValues == strArr) {
            return;
        }
        this.mDisplayedValues = strArr;
        if (strArr != null) {
            this.mInputText.setRawInputType(524289);
        } else {
            this.mInputText.setRawInputType(2);
        }
        updateInputTextView();
        initializeSelectorWheelIndices();
        tryComputeMaxWidth();
    }

    public void setMaxFlingSpeedFactor(float f) {
        if (f >= 0.0f) {
            this.mMaxFlingSpeedFactor = f;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initSoundPlayer();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseSoundPlayer();
        removeAllCallbacks();
        WorkerThreads.releaseWorker(SOUND_PLAY_THREAD);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        if (!this.mHasSelectorWheel) {
            super.onDraw(canvas);
            return;
        }
        float right = (((getRight() - getLeft()) + getPaddingLeft()) - getPaddingRight()) / 2;
        float f = this.mInitialScrollOffset + this.mSelectorElementHeight;
        drawLabelText(canvas, right, f, drawScrollValue(canvas, right, f));
    }

    private float drawScrollValue(Canvas canvas, float f, float f2) {
        float f3 = this.mCurrentScrollOffset;
        SparseArray<String> sparseArray = this.mSelectorIndexToStringCache;
        for (int i : this.mSelectorIndices) {
            String str = sparseArray.get(i);
            float fAbs = Math.abs(f2 - f3) / this.mSelectorElementHeight;
            int i2 = this.mTextSizeHighlight;
            float f4 = i2;
            float f5 = this.mTextSizeThreshold;
            if (f4 > f5) {
                i2 = (int) f5;
            } else {
                float width = getWidth() / this.mSelectorWheelPaint.measureText(str);
                if (width < 1.0f) {
                    i2 = (int) (this.mTextSizeHighlight * width);
                }
            }
            int i3 = this.mTextSizeHint;
            int i4 = (int) (i2 * 0.85f);
            if (i3 > i4) {
                i3 = i4;
            }
            float textSize = getTextSize(fAbs, i2, i3);
            this.mSelectorWheelPaint.setTextSize(textSize);
            this.mSelectorWheelPaint.setColor(getAlphaGradient(fAbs, this.mTextColorHint, false));
            float f6 = ((textSize - i3) / 2.0f) + f3;
            canvas.drawText(str, f, f6, this.mSelectorWheelPaint);
            if (fAbs < 1.0f) {
                this.mSelectorWheelPaint.setColor(getAlphaGradient(fAbs, this.mTextColorHighlight, true));
                canvas.drawText(str, f, f6, this.mSelectorWheelPaint);
            }
            f3 += this.mSelectorElementHeight;
        }
        return f3;
    }

    private void drawLabelText(Canvas canvas, float f, float f2, float f3) {
        float fMin;
        if (TextUtils.isEmpty(this.mLabel) || isInternationalBuild()) {
            return;
        }
        float fMeasureText = this.mLabelPaint.measureText(this.mLabel.toString());
        if (ViewUtils.isLayoutRtl(this)) {
            fMin = Math.max(((f - (this.mDisplayedMaxTextWidth / 2.0f)) - this.MARGIN_LABEL_LEFT) - fMeasureText, 0.0f);
        } else {
            fMin = Math.min(f + (this.mDisplayedMaxTextWidth / 2.0f) + this.MARGIN_LABEL_LEFT, getWidth() - fMeasureText);
        }
        canvas.drawText(this.mLabel.toString(), fMin, (f2 - (this.mTextSizeHighlight / 2.0f)) + (this.mLabelTextSize / 2.0f) + this.MARGIN_LABEL_TOP, this.mLabelPaint);
    }

    private void trimLabelTextSize(float f) {
        if (getLabelWidth() > 0.0f) {
            int i = this.mOriginLabelTextSize;
            this.mLabelTextSize = i;
            this.mLabelPaint.setTextSize(i);
            while ((this.mDisplayedMaxTextWidth / 2.0f) + f + this.MARGIN_LABEL_LEFT + getLabelWidth() > getWidth()) {
                int i2 = this.mLabelTextSize;
                if (i2 <= this.mLabelTextSizeThreshold) {
                    return;
                }
                int i3 = (int) (i2 * this.mLabelTextSizeTrimFactor);
                this.mLabelTextSize = i3;
                this.mLabelPaint.setTextSize(i3);
            }
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        initThreshHolds();
    }

    private int getAlphaGradient(float f, int i, boolean z) {
        float fAlpha;
        if (f >= 1.0f) {
            return i;
        }
        if (z) {
            fAlpha = ((-f) * Color.alpha(i)) + Color.alpha(i);
        } else {
            fAlpha = f * Color.alpha(i);
        }
        return (((int) fAlpha) << 24) | (i & 16777215);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return NumberPicker.class.getName();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (isEnabled()) {
            accessibilityNodeInfo.setScrollable(true);
            accessibilityNodeInfo.addAction(8192);
            accessibilityNodeInfo.addAction(4096);
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS);
            accessibilityNodeInfo.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(0, this.mMinValue - 1, this.mMaxValue + 1, this.mValue));
            StringBuilder sb = new StringBuilder();
            String[] strArr = this.mDisplayedValues;
            accessibilityNodeInfo.setContentDescription(sb.append(strArr == null ? formatNumber(this.mValue) : strArr[this.mValue - this.mMinValue]).append(TextUtils.isEmpty(this.mLabel) ? "" : this.mLabel).toString());
            if (Build.VERSION.SDK_INT >= 30) {
                accessibilityNodeInfo.setStateDescription(getContext().getString(R.string.miuix_access_state_desc));
            }
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (super.performAccessibilityAction(i, bundle)) {
            return true;
        }
        if (!isEnabled()) {
            return false;
        }
        if (i != 4096 && i != 8192) {
            return false;
        }
        changeValueByOne(i == 4096);
        return true;
    }

    private int makeMeasureSpec(int i, int i2) {
        if (i2 == -1) {
            return i;
        }
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i);
        if (mode == Integer.MIN_VALUE) {
            return View.MeasureSpec.makeMeasureSpec(Math.min(size, i2), BasicMeasure.EXACTLY);
        }
        if (mode == 0) {
            return View.MeasureSpec.makeMeasureSpec(i2, BasicMeasure.EXACTLY);
        }
        if (mode == 1073741824) {
            return i;
        }
        throw new IllegalArgumentException("Unknown measure mode: " + mode);
    }

    private int resolveSizeAndStateRespectingMinSize(int i, int i2, int i3) {
        return i != -1 ? resolveSizeAndState(Math.max(i, i2), i3, 0) : i2;
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] iArr = this.mSelectorIndices;
        int value = getValue();
        for (int i = 0; i < this.mSelectorIndices.length; i++) {
            int wrappedSelectorIndex = (i - 1) + value;
            if (this.mWrapSelectorWheel) {
                wrappedSelectorIndex = getWrappedSelectorIndex(wrappedSelectorIndex);
            }
            iArr[i] = wrappedSelectorIndex;
            ensureCachedScrollSelectorValue(wrappedSelectorIndex);
        }
    }

    private void setValueInternal(int i, boolean z) {
        int iMin;
        if (this.mWrapSelectorWheel) {
            iMin = getWrappedSelectorIndex(i);
        } else {
            iMin = Math.min(Math.max(i, this.mMinValue), this.mMaxValue);
        }
        int i2 = this.mValue;
        if (i2 == iMin) {
            return;
        }
        this.mValue = iMin;
        updateInputTextView();
        if (z) {
            notifyChange(i2);
        }
        initializeSelectorWheelIndices();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeValueByOne(boolean z) {
        if (!this.mHasSelectorWheel) {
            if (z) {
                setValueInternal(this.mValue + 1, true);
                return;
            } else {
                setValueInternal(this.mValue - 1, true);
                return;
            }
        }
        this.mInputText.setVisibility(4);
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = 0;
        if (z) {
            this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, 300);
        } else {
            this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, 300);
        }
        invalidate();
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] iArr = this.mSelectorIndices;
        float bottom = (getBottom() - getTop()) - (iArr.length * this.mTextSize);
        if (bottom < 0.0f) {
            bottom = 0.0f;
        }
        int length = (int) ((bottom / iArr.length) + 0.5f);
        this.mSelectorTextGapHeight = length;
        this.mSelectorElementHeight = this.mTextSize + length;
        int baseline = (this.mInputText.getBaseline() + this.mInputText.getTop()) - this.mSelectorElementHeight;
        this.mInitialScrollOffset = baseline;
        this.mCurrentScrollOffset = baseline;
        updateInputTextView();
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((getBottom() - getTop()) - this.mTextSize) / 2);
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            if (!ensureScrollWheelAdjusted()) {
                updateInputTextView();
            }
            onScrollStateChange(0);
        } else if (this.mScrollState != 1) {
            updateInputTextView();
        }
    }

    private void onScrollStateChange(int i) {
        if (this.mScrollState == i) {
            return;
        }
        if (i == 0) {
            String str = this.mUpdateText;
            if (str != null && !str.equals(this.mInputText.getText().toString())) {
                this.mInputText.setText(this.mUpdateText);
            }
            this.mUpdateText = null;
            stopSoundPlay();
        }
        this.mScrollState = i;
        OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChange(this, i);
        }
    }

    private void fling(int i) {
        this.mPreviousScrollerY = 0;
        if (i > 0) {
            this.mFlingScroller.fling(0, 0, 0, i, 0, 0, 0, Integer.MAX_VALUE);
        } else {
            this.mFlingScroller.fling(0, Integer.MAX_VALUE, 0, i, 0, 0, 0, Integer.MAX_VALUE);
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getWrappedSelectorIndex(int i) {
        int i2 = this.mMaxValue;
        if (i > i2) {
            int i3 = this.mMinValue;
            return (i3 + ((i - i2) % (i2 - i3))) - 1;
        }
        int i4 = this.mMinValue;
        return i < i4 ? (i2 - ((i4 - i) % (i2 - i4))) + 1 : i;
    }

    private void incrementSelectorIndices(int[] iArr) {
        if (iArr.length - 1 >= 0) {
            System.arraycopy(iArr, 1, iArr, 0, iArr.length - 1);
        }
        int i = iArr[iArr.length - 2] + 1;
        if (this.mWrapSelectorWheel && i > this.mMaxValue) {
            i = this.mMinValue;
        }
        iArr[iArr.length - 1] = i;
        ensureCachedScrollSelectorValue(i);
    }

    private void decrementSelectorIndices(int[] iArr) {
        if (iArr.length - 1 >= 0) {
            System.arraycopy(iArr, 0, iArr, 1, iArr.length - 1);
        }
        int i = iArr[1] - 1;
        if (this.mWrapSelectorWheel && i < this.mMinValue) {
            i = this.mMaxValue;
        }
        iArr[0] = i;
        ensureCachedScrollSelectorValue(i);
    }

    private void ensureCachedScrollSelectorValue(int i) {
        String number;
        SparseArray<String> sparseArray = this.mSelectorIndexToStringCache;
        if (sparseArray.get(i) != null) {
            return;
        }
        int i2 = this.mMinValue;
        if (i < i2 || i > this.mMaxValue) {
            number = "";
        } else {
            String[] strArr = this.mDisplayedValues;
            if (strArr != null) {
                number = strArr[i - i2];
            } else {
                number = formatNumber(i);
            }
        }
        sparseArray.put(i, number);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatNumber(int i) {
        Formatter formatter = this.mFormatter;
        return formatter != null ? formatter.format(i) : SimpleNumberFormatter.format(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void validateInputTextView(View view) {
        String strValueOf = String.valueOf(((TextView) view).getText());
        if (TextUtils.isEmpty(strValueOf)) {
            updateInputTextView();
        } else {
            setValueInternal(getSelectedPos(strValueOf.toString()), true);
        }
    }

    private boolean updateInputTextView() {
        String displayedMaxText = getDisplayedMaxText();
        if (TextUtils.isEmpty(displayedMaxText)) {
            return false;
        }
        if (this.mScrollState != 0) {
            this.mUpdateText = displayedMaxText;
            return true;
        }
        if (displayedMaxText.equals(this.mInputText.getText().toString())) {
            return true;
        }
        this.mInputText.setText(displayedMaxText);
        return true;
    }

    private void notifyChange(int i) {
        sendAccessibilityEvent(4);
        playSound();
        HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_GEAR_HEAVY, HapticFeedbackConstants.MIUI_MESH_NORMAL);
        OnValueChangeListener onValueChangeListener = this.mOnValueChangeListener;
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChange(this, i, this.mValue);
        }
    }

    private void postChangeCurrentByOneFromLongPress(boolean z, long j) {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        this.mChangeCurrentByOneFromLongPressCommand.setStep(z);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, j);
    }

    private void removeChangeCurrentByOneFromLongPress() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
    }

    private void postBeginSoftInputOnLongPressCommand() {
        BeginSoftInputOnLongPressCommand beginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
        if (beginSoftInputOnLongPressCommand == null) {
            this.mBeginSoftInputOnLongPressCommand = new BeginSoftInputOnLongPressCommand();
        } else {
            removeCallbacks(beginSoftInputOnLongPressCommand);
        }
        postDelayed(this.mBeginSoftInputOnLongPressCommand, ViewConfiguration.getLongPressTimeout());
    }

    private void removeBeginSoftInputCommand() {
        BeginSoftInputOnLongPressCommand beginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
        if (beginSoftInputOnLongPressCommand != null) {
            removeCallbacks(beginSoftInputOnLongPressCommand);
        }
    }

    private void removeAllCallbacks() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        SetSelectionCommand setSelectionCommand = this.mSetSelectionCommand;
        if (setSelectionCommand != null) {
            removeCallbacks(setSelectionCommand);
        }
        BeginSoftInputOnLongPressCommand beginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
        if (beginSoftInputOnLongPressCommand != null) {
            removeCallbacks(beginSoftInputOnLongPressCommand);
        }
        this.mPressedStateHelper.cancel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSelectedPos(String str) {
        try {
            if (this.mDisplayedValues == null) {
                return Integer.parseInt(str);
            }
            for (int i = 0; i < this.mDisplayedValues.length; i++) {
                str = str.toLowerCase();
                if (this.mDisplayedValues[i].toLowerCase().startsWith(str)) {
                    return this.mMinValue + i;
                }
            }
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return this.mMinValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postSetSelectionCommand(int i, int i2) {
        SetSelectionCommand setSelectionCommand = this.mSetSelectionCommand;
        if (setSelectionCommand == null) {
            this.mSetSelectionCommand = new SetSelectionCommand();
        } else {
            removeCallbacks(setSelectionCommand);
        }
        this.mSetSelectionCommand.mSelectionStart = i;
        this.mSetSelectionCommand.mSelectionEnd = i2;
        post(this.mSetSelectionCommand);
    }

    class InputTextFilter extends NumberKeyListener {
        @Override // android.text.method.KeyListener
        public int getInputType() {
            return 1;
        }

        InputTextFilter() {
        }

        @Override // android.text.method.NumberKeyListener
        protected char[] getAcceptedChars() {
            return NumberPicker.DIGIT_CHARACTERS;
        }

        @Override // android.text.method.NumberKeyListener, android.text.InputFilter
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            if (NumberPicker.this.mDisplayedValues == null) {
                CharSequence charSequenceFilter = super.filter(charSequence, i, i2, spanned, i3, i4);
                if (charSequenceFilter == null) {
                    charSequenceFilter = charSequence.subSequence(i, i2);
                }
                String str = String.valueOf(spanned.subSequence(0, i3)) + ((Object) charSequenceFilter) + ((Object) spanned.subSequence(i4, spanned.length()));
                if ("".equals(str)) {
                    return str;
                }
                return (NumberPicker.this.getSelectedPos(str) > NumberPicker.this.mMaxValue || str.length() > String.valueOf(NumberPicker.this.mMaxValue).length()) ? "" : charSequenceFilter;
            }
            String strValueOf = String.valueOf(charSequence.subSequence(i, i2));
            if (TextUtils.isEmpty(strValueOf)) {
                return "";
            }
            String str2 = String.valueOf(spanned.subSequence(0, i3)) + ((Object) strValueOf) + ((Object) spanned.subSequence(i4, spanned.length()));
            String lowerCase = String.valueOf(str2).toLowerCase();
            for (String str3 : NumberPicker.this.mDisplayedValues) {
                if (str3.toLowerCase().startsWith(lowerCase)) {
                    NumberPicker.this.postSetSelectionCommand(str2.length(), str3.length());
                    return str3.subSequence(i3, str3.length());
                }
            }
            return "";
        }
    }

    private boolean ensureScrollWheelAdjusted() {
        int i = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (i == 0) {
            return false;
        }
        this.mPreviousScrollerY = 0;
        int iAbs = Math.abs(i);
        int i2 = this.mSelectorElementHeight;
        if (iAbs > i2 / 2) {
            if (i > 0) {
                i2 = -i2;
            }
            i += i2;
        }
        this.mAdjustScroller.startScroll(0, 0, 0, i, 800);
        invalidate();
        return true;
    }

    class PressedStateHelper implements Runnable {
        public static final int BUTTON_DECREMENT = 2;
        public static final int BUTTON_INCREMENT = 1;
        private final int MODE_PRESS = 1;
        private final int MODE_TAPPED = 2;
        private int mManagedButton;
        private int mMode;

        PressedStateHelper() {
        }

        public void cancel() {
            this.mMode = 0;
            this.mManagedButton = 0;
            NumberPicker.this.removeCallbacks(this);
            if (NumberPicker.this.mIncrementVirtualButtonPressed) {
                NumberPicker.this.mIncrementVirtualButtonPressed = false;
                NumberPicker numberPicker = NumberPicker.this;
                numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
            }
            if (NumberPicker.this.mDecrementVirtualButtonPressed) {
                NumberPicker.this.mDecrementVirtualButtonPressed = false;
                NumberPicker numberPicker2 = NumberPicker.this;
                numberPicker2.invalidate(0, 0, numberPicker2.getRight(), NumberPicker.this.mTopSelectionDividerTop);
            }
        }

        public void buttonPressDelayed(int i) {
            cancel();
            this.mMode = 1;
            this.mManagedButton = i;
            NumberPicker.this.postDelayed(this, ViewConfiguration.getTapTimeout());
        }

        public void buttonTapped(int i) {
            cancel();
            this.mMode = 2;
            this.mManagedButton = i;
            NumberPicker.this.post(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            int i = this.mMode;
            if (i == 1) {
                int i2 = this.mManagedButton;
                if (i2 == 1) {
                    NumberPicker.this.mIncrementVirtualButtonPressed = true;
                    NumberPicker numberPicker = NumberPicker.this;
                    numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                    return;
                } else {
                    if (i2 != 2) {
                        return;
                    }
                    NumberPicker.this.mDecrementVirtualButtonPressed = true;
                    NumberPicker numberPicker2 = NumberPicker.this;
                    numberPicker2.invalidate(0, 0, numberPicker2.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                    return;
                }
            }
            if (i != 2) {
                return;
            }
            int i3 = this.mManagedButton;
            if (i3 == 1) {
                if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
                    NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
                }
                NumberPicker.access$1180(NumberPicker.this, 1);
                NumberPicker numberPicker3 = NumberPicker.this;
                numberPicker3.invalidate(0, numberPicker3.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                return;
            }
            if (i3 != 2) {
                return;
            }
            if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
                NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
            }
            NumberPicker.access$1380(NumberPicker.this, 1);
            NumberPicker numberPicker4 = NumberPicker.this;
            numberPicker4.invalidate(0, 0, numberPicker4.getRight(), NumberPicker.this.mTopSelectionDividerTop);
        }
    }

    class SetSelectionCommand implements Runnable {
        private int mSelectionEnd;
        private int mSelectionStart;

        SetSelectionCommand() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mSelectionEnd < NumberPicker.this.mInputText.length()) {
                NumberPicker.this.mInputText.setSelection(this.mSelectionStart, this.mSelectionEnd);
            }
        }
    }

    class ChangeCurrentByOneFromLongPressCommand implements Runnable {
        private boolean mIncrement;

        ChangeCurrentByOneFromLongPressCommand() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setStep(boolean z) {
            this.mIncrement = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            NumberPicker.this.changeValueByOne(this.mIncrement);
            NumberPicker numberPicker = NumberPicker.this;
            numberPicker.postDelayed(this, numberPicker.mLongPressUpdateInterval);
        }
    }

    public static class CustomEditText extends AppCompatEditText {
        public CustomEditText(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override // android.widget.TextView
        public void onEditorAction(int i) {
            super.onEditorAction(i);
            if (i == 6) {
                clearFocus();
            }
        }
    }

    class BeginSoftInputOnLongPressCommand implements Runnable {
        BeginSoftInputOnLongPressCommand() {
        }

        @Override // java.lang.Runnable
        public void run() {
            NumberPicker.this.mIgnoreMoveEvents = true;
        }
    }

    class AccessibilityNodeProviderImpl extends AccessibilityNodeProvider {
        private static final int UNDEFINED = Integer.MIN_VALUE;
        private static final int VIRTUAL_VIEW_ID_DECREMENT = 3;
        private static final int VIRTUAL_VIEW_ID_INCREMENT = 1;
        private static final int VIRTUAL_VIEW_ID_INPUT = 2;
        private final Rect mTempRect = new Rect();
        private final int[] mTempArray = new int[2];
        private int mAccessibilityFocusedView = Integer.MIN_VALUE;

        AccessibilityNodeProviderImpl() {
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            if (i == -1) {
                return createAccessibilityNodeInfoForNumberPicker(NumberPicker.this.getScrollX(), NumberPicker.this.getScrollY(), NumberPicker.this.getScrollX() + (NumberPicker.this.getRight() - NumberPicker.this.getLeft()), NumberPicker.this.getScrollY() + (NumberPicker.this.getBottom() - NumberPicker.this.getTop()));
            }
            if (i == 1) {
                return createAccessibilityNodeInfoForVirtualButton(1, getVirtualIncrementButtonText(), NumberPicker.this.getScrollX(), NumberPicker.this.mBottomSelectionDividerBottom - NumberPicker.this.mSelectionDividerHeight, NumberPicker.this.getScrollX() + (NumberPicker.this.getRight() - NumberPicker.this.getLeft()), NumberPicker.this.getScrollY() + (NumberPicker.this.getBottom() - NumberPicker.this.getTop()));
            }
            if (i == 2) {
                return createAccessibiltyNodeInfoForInputText(NumberPicker.this.getScrollX(), NumberPicker.this.mTopSelectionDividerTop + NumberPicker.this.mSelectionDividerHeight, NumberPicker.this.getScrollX() + (NumberPicker.this.getRight() - NumberPicker.this.getLeft()), NumberPicker.this.mBottomSelectionDividerBottom - NumberPicker.this.mSelectionDividerHeight);
            }
            if (i == 3) {
                return createAccessibilityNodeInfoForVirtualButton(3, getVirtualDecrementButtonText(), NumberPicker.this.getScrollX(), NumberPicker.this.getScrollY(), NumberPicker.this.getScrollX() + (NumberPicker.this.getRight() - NumberPicker.this.getLeft()), NumberPicker.this.mTopSelectionDividerTop + NumberPicker.this.mSelectionDividerHeight);
            }
            return super.createAccessibilityNodeInfo(i);
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String str, int i) {
            if (TextUtils.isEmpty(str)) {
                return Collections.emptyList();
            }
            String lowerCase = str.toLowerCase();
            ArrayList arrayList = new ArrayList();
            if (i == -1) {
                findAccessibilityNodeInfosByTextInChild(lowerCase, 3, arrayList);
                findAccessibilityNodeInfosByTextInChild(lowerCase, 2, arrayList);
                findAccessibilityNodeInfosByTextInChild(lowerCase, 1, arrayList);
                return arrayList;
            }
            if (i == 1 || i == 2 || i == 3) {
                findAccessibilityNodeInfosByTextInChild(lowerCase, i, arrayList);
                return arrayList;
            }
            return super.findAccessibilityNodeInfosByText(str, i);
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public boolean performAction(int i, int i2, Bundle bundle) {
            if (i != -1) {
                if (i == 1) {
                    if (i2 == 16) {
                        if (!NumberPicker.this.isEnabled()) {
                            return false;
                        }
                        NumberPicker.this.changeValueByOne(true);
                        sendAccessibilityEventForVirtualView(i, 1);
                        return true;
                    }
                    if (i2 == 64) {
                        if (this.mAccessibilityFocusedView == i) {
                            return false;
                        }
                        this.mAccessibilityFocusedView = i;
                        sendAccessibilityEventForVirtualView(i, 32768);
                        NumberPicker numberPicker = NumberPicker.this;
                        numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                        return true;
                    }
                    if (i2 != 128 || this.mAccessibilityFocusedView != i) {
                        return false;
                    }
                    this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                    sendAccessibilityEventForVirtualView(i, 65536);
                    NumberPicker numberPicker2 = NumberPicker.this;
                    numberPicker2.invalidate(0, numberPicker2.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                    return true;
                }
                if (i == 2) {
                    if (i2 == 1) {
                        if (!NumberPicker.this.isEnabled() || NumberPicker.this.mInputText.isFocused()) {
                            return false;
                        }
                        return NumberPicker.this.mInputText.requestFocus();
                    }
                    if (i2 == 2) {
                        if (!NumberPicker.this.isEnabled() || !NumberPicker.this.mInputText.isFocused()) {
                            return false;
                        }
                        NumberPicker.this.mInputText.clearFocus();
                        return true;
                    }
                    if (i2 == 16) {
                        return NumberPicker.this.isEnabled();
                    }
                    if (i2 == 64) {
                        if (this.mAccessibilityFocusedView == i) {
                            return false;
                        }
                        this.mAccessibilityFocusedView = i;
                        sendAccessibilityEventForVirtualView(i, 32768);
                        NumberPicker.this.mInputText.invalidate();
                        return true;
                    }
                    if (i2 != 128) {
                        return NumberPicker.this.mInputText.performAccessibilityAction(i2, bundle);
                    }
                    if (this.mAccessibilityFocusedView != i) {
                        return false;
                    }
                    this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                    sendAccessibilityEventForVirtualView(i, 65536);
                    NumberPicker.this.mInputText.invalidate();
                    return true;
                }
                if (i == 3) {
                    if (i2 == 16) {
                        if (!NumberPicker.this.isEnabled()) {
                            return false;
                        }
                        NumberPicker.this.changeValueByOne(i == 1);
                        sendAccessibilityEventForVirtualView(i, 1);
                        return true;
                    }
                    if (i2 == 64) {
                        if (this.mAccessibilityFocusedView == i) {
                            return false;
                        }
                        this.mAccessibilityFocusedView = i;
                        sendAccessibilityEventForVirtualView(i, 32768);
                        NumberPicker numberPicker3 = NumberPicker.this;
                        numberPicker3.invalidate(0, 0, numberPicker3.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                        return true;
                    }
                    if (i2 != 128 || this.mAccessibilityFocusedView != i) {
                        return false;
                    }
                    this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                    sendAccessibilityEventForVirtualView(i, 65536);
                    NumberPicker numberPicker4 = NumberPicker.this;
                    numberPicker4.invalidate(0, 0, numberPicker4.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                    return true;
                }
            } else {
                if (i2 == 64) {
                    if (this.mAccessibilityFocusedView == i) {
                        return false;
                    }
                    this.mAccessibilityFocusedView = i;
                    return true;
                }
                if (i2 == 128) {
                    if (this.mAccessibilityFocusedView != i) {
                        return false;
                    }
                    this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                    return true;
                }
                if (i2 == 4096) {
                    if (!NumberPicker.this.isEnabled() || (!NumberPicker.this.getWrapSelectorWheel() && NumberPicker.this.getValue() >= NumberPicker.this.getMaxValue())) {
                        return false;
                    }
                    NumberPicker.this.changeValueByOne(true);
                    return true;
                }
                if (i2 == 8192) {
                    if (!NumberPicker.this.isEnabled() || (!NumberPicker.this.getWrapSelectorWheel() && NumberPicker.this.getValue() <= NumberPicker.this.getMinValue())) {
                        return false;
                    }
                    NumberPicker.this.changeValueByOne(false);
                    return true;
                }
            }
            return super.performAction(i, i2, bundle);
        }

        public void sendAccessibilityEventForVirtualView(int i, int i2) {
            if (i == 1) {
                if (hasVirtualIncrementButton()) {
                    sendAccessibilityEventForVirtualButton(i, i2, getVirtualIncrementButtonText());
                }
            } else {
                if (i != 2) {
                    if (i == 3 && hasVirtualDecrementButton()) {
                        sendAccessibilityEventForVirtualButton(i, i2, getVirtualDecrementButtonText());
                        return;
                    }
                    return;
                }
                sendAccessibilityEventForVirtualText(i2);
            }
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        private void sendAccessibilityEventForVirtualText(int i) {
            if (((AccessibilityManager) NumberPicker.this.getContext().getSystemService("accessibility")).isEnabled()) {
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(i);
                NumberPicker.this.mInputText.onInitializeAccessibilityEvent(accessibilityEventObtain);
                NumberPicker.this.mInputText.onPopulateAccessibilityEvent(accessibilityEventObtain);
                accessibilityEventObtain.setSource(NumberPicker.this, 2);
                NumberPicker numberPicker = NumberPicker.this;
                numberPicker.requestSendAccessibilityEvent(numberPicker, accessibilityEventObtain);
            }
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        private void sendAccessibilityEventForVirtualButton(int i, int i2, String str) {
            if (((AccessibilityManager) NumberPicker.this.getContext().getSystemService("accessibility")).isEnabled()) {
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(i2);
                accessibilityEventObtain.setClassName(Button.class.getName());
                accessibilityEventObtain.setPackageName(NumberPicker.this.getContext().getPackageName());
                accessibilityEventObtain.getText().add(str);
                accessibilityEventObtain.setEnabled(NumberPicker.this.isEnabled());
                accessibilityEventObtain.setSource(NumberPicker.this, i);
                NumberPicker numberPicker = NumberPicker.this;
                numberPicker.requestSendAccessibilityEvent(numberPicker, accessibilityEventObtain);
            }
        }

        private void findAccessibilityNodeInfosByTextInChild(String str, int i, List<AccessibilityNodeInfo> list) {
            if (i == 1) {
                String virtualIncrementButtonText = getVirtualIncrementButtonText();
                if (TextUtils.isEmpty(virtualIncrementButtonText) || !virtualIncrementButtonText.toString().toLowerCase().contains(str)) {
                    return;
                }
                list.add(createAccessibilityNodeInfo(1));
                return;
            }
            if (i != 2) {
                if (i != 3) {
                    return;
                }
                String virtualDecrementButtonText = getVirtualDecrementButtonText();
                if (TextUtils.isEmpty(virtualDecrementButtonText) || !virtualDecrementButtonText.toString().toLowerCase().contains(str)) {
                    return;
                }
                list.add(createAccessibilityNodeInfo(3));
                return;
            }
            Editable text = NumberPicker.this.mInputText.getText();
            if (TextUtils.isEmpty(text) || !text.toString().toLowerCase().contains(str)) {
                Editable text2 = NumberPicker.this.mInputText.getText();
                if (TextUtils.isEmpty(text2) || !text2.toString().toLowerCase().contains(str)) {
                    return;
                }
                list.add(createAccessibilityNodeInfo(2));
                return;
            }
            list.add(createAccessibilityNodeInfo(2));
        }

        private AccessibilityNodeInfo createAccessibiltyNodeInfoForInputText(int i, int i2, int i3, int i4) {
            AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = NumberPicker.this.mInputText.createAccessibilityNodeInfo();
            accessibilityNodeInfoCreateAccessibilityNodeInfo.setSource(NumberPicker.this, 2);
            if (this.mAccessibilityFocusedView != 2) {
                accessibilityNodeInfoCreateAccessibilityNodeInfo.addAction(64);
            }
            if (this.mAccessibilityFocusedView == 2) {
                accessibilityNodeInfoCreateAccessibilityNodeInfo.addAction(128);
            }
            Rect rect = this.mTempRect;
            rect.set(i, i2, i3, i4);
            accessibilityNodeInfoCreateAccessibilityNodeInfo.setVisibleToUser(NumberPicker.this.getVisibility() == 0);
            accessibilityNodeInfoCreateAccessibilityNodeInfo.setBoundsInParent(rect);
            int[] iArr = this.mTempArray;
            NumberPicker.this.getLocationOnScreen(iArr);
            rect.offset(iArr[0], iArr[1]);
            accessibilityNodeInfoCreateAccessibilityNodeInfo.setBoundsInScreen(rect);
            return accessibilityNodeInfoCreateAccessibilityNodeInfo;
        }

        private AccessibilityNodeInfo createAccessibilityNodeInfoForVirtualButton(int i, String str, int i2, int i3, int i4, int i5) {
            AccessibilityNodeInfo accessibilityNodeInfoObtain = AccessibilityNodeInfo.obtain();
            accessibilityNodeInfoObtain.setClassName(Button.class.getName());
            accessibilityNodeInfoObtain.setPackageName(NumberPicker.this.getContext().getPackageName());
            accessibilityNodeInfoObtain.setSource(NumberPicker.this, i);
            accessibilityNodeInfoObtain.setParent(NumberPicker.this);
            accessibilityNodeInfoObtain.setText(str);
            accessibilityNodeInfoObtain.setClickable(true);
            accessibilityNodeInfoObtain.setLongClickable(true);
            accessibilityNodeInfoObtain.setEnabled(NumberPicker.this.isEnabled());
            Rect rect = this.mTempRect;
            rect.set(i2, i3, i4, i5);
            accessibilityNodeInfoObtain.setVisibleToUser(NumberPicker.this.getVisibility() == 0);
            accessibilityNodeInfoObtain.setBoundsInParent(rect);
            int[] iArr = this.mTempArray;
            NumberPicker.this.getLocationOnScreen(iArr);
            rect.offset(iArr[0], iArr[1]);
            accessibilityNodeInfoObtain.setBoundsInScreen(rect);
            if (this.mAccessibilityFocusedView != i) {
                accessibilityNodeInfoObtain.addAction(64);
            }
            if (this.mAccessibilityFocusedView == i) {
                accessibilityNodeInfoObtain.addAction(128);
            }
            if (NumberPicker.this.isEnabled()) {
                accessibilityNodeInfoObtain.addAction(16);
            }
            return accessibilityNodeInfoObtain;
        }

        private AccessibilityNodeInfo createAccessibilityNodeInfoForNumberPicker(int i, int i2, int i3, int i4) {
            AccessibilityNodeInfo accessibilityNodeInfoObtain = AccessibilityNodeInfo.obtain();
            accessibilityNodeInfoObtain.setClassName(NumberPicker.class.getName());
            accessibilityNodeInfoObtain.setPackageName(NumberPicker.this.getContext().getPackageName());
            accessibilityNodeInfoObtain.setSource(NumberPicker.this);
            if (hasVirtualDecrementButton()) {
                accessibilityNodeInfoObtain.addChild(NumberPicker.this, 3);
            }
            accessibilityNodeInfoObtain.addChild(NumberPicker.this, 2);
            if (hasVirtualIncrementButton()) {
                accessibilityNodeInfoObtain.addChild(NumberPicker.this, 1);
            }
            accessibilityNodeInfoObtain.setParent((View) NumberPicker.this.getParentForAccessibility());
            accessibilityNodeInfoObtain.setEnabled(NumberPicker.this.isEnabled());
            accessibilityNodeInfoObtain.setScrollable(true);
            Rect rect = this.mTempRect;
            rect.set(i, i2, i3, i4);
            accessibilityNodeInfoObtain.setBoundsInParent(rect);
            accessibilityNodeInfoObtain.setVisibleToUser(NumberPicker.this.getVisibility() == 0);
            int[] iArr = this.mTempArray;
            NumberPicker.this.getLocationOnScreen(iArr);
            rect.offset(iArr[0], iArr[1]);
            accessibilityNodeInfoObtain.setBoundsInScreen(rect);
            if (this.mAccessibilityFocusedView != -1) {
                accessibilityNodeInfoObtain.addAction(64);
            }
            if (this.mAccessibilityFocusedView == -1) {
                accessibilityNodeInfoObtain.addAction(128);
            }
            if (NumberPicker.this.isEnabled()) {
                if (NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() < NumberPicker.this.getMaxValue()) {
                    accessibilityNodeInfoObtain.addAction(4096);
                }
                if (NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() > NumberPicker.this.getMinValue()) {
                    accessibilityNodeInfoObtain.addAction(8192);
                }
            }
            return accessibilityNodeInfoObtain;
        }

        private boolean hasVirtualDecrementButton() {
            return NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() > NumberPicker.this.getMinValue();
        }

        private boolean hasVirtualIncrementButton() {
            return NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() < NumberPicker.this.getMaxValue();
        }

        private String getVirtualDecrementButtonText() {
            int wrappedSelectorIndex = NumberPicker.this.mValue - 1;
            if (NumberPicker.this.mWrapSelectorWheel) {
                wrappedSelectorIndex = NumberPicker.this.getWrappedSelectorIndex(wrappedSelectorIndex);
            }
            if (wrappedSelectorIndex >= NumberPicker.this.mMinValue) {
                return NumberPicker.this.mDisplayedValues == null ? NumberPicker.this.formatNumber(wrappedSelectorIndex) : NumberPicker.this.mDisplayedValues[wrappedSelectorIndex - NumberPicker.this.mMinValue];
            }
            return null;
        }

        private String getVirtualIncrementButtonText() {
            int wrappedSelectorIndex = NumberPicker.this.mValue + 1;
            if (NumberPicker.this.mWrapSelectorWheel) {
                wrappedSelectorIndex = NumberPicker.this.getWrappedSelectorIndex(wrappedSelectorIndex);
            }
            if (wrappedSelectorIndex <= NumberPicker.this.mMaxValue) {
                return NumberPicker.this.mDisplayedValues == null ? NumberPicker.this.formatNumber(wrappedSelectorIndex) : NumberPicker.this.mDisplayedValues[wrappedSelectorIndex - NumberPicker.this.mMinValue];
            }
            return null;
        }
    }

    private void refreshWheel() {
        initializeSelectorWheelIndices();
        invalidate();
    }

    private boolean isInternationalBuild() {
        if (this.mModDevice == null) {
            this.mModDevice = (String) ReflectUtil.callStaticObjectMethod(ReflectUtil.getClass("android.os.SystemProperties"), String.class, "get", new Class[]{String.class, String.class}, "ro.product.mod_device", "");
        }
        return this.mModDevice.endsWith("_global");
    }
}
