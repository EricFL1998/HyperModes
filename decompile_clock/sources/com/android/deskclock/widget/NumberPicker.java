package com.android.deskclock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationAttributes;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.GravityCompat;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.SimpleNumberFormatter;
import com.android.deskclock.util.TypefaceFactory;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.WorkerThreads;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import miuix.springback.view.SpringBackLayout;
import miuix.util.HapticFeedbackCompat;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes.dex */
public class NumberPicker extends LinearLayout {
    private static final int MAX_HEIGHT = 232;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 4;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final String SOUND_PLAY_THREAD = "NumberPicker_sound_play";
    private int MARGIN_LABEL_LEFT;
    private Runnable mAccessbilityRunnable;
    private final PhysicalVerticalScroller mAdjustScroller;
    private int mCurrentScrollOffset;
    private float mDisplayedMaxTextWidth;
    private String[] mDisplayedValues;
    private final PhysicalVerticalScroller mFlingScroller;
    private Formatter mFormatter;
    private int mHapticMesh;
    private Typeface mHighlightTypeface;
    private Typeface mHintTypeface;
    private final int mId;
    private int mInitialScrollOffset;
    private int mInterceptDownX;
    private int mInterceptDownY;
    private CharSequence mLabel;
    private Paint mLabelPaint;
    private int mLabelTextColor;
    private int mLabelTextSize;
    private int mLabelTextSizeTiny;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private long mLastPerformHapticTime;
    private long mLastPlaySoundTime;
    private Handler mMainHandler;
    private TextView mMarkTv;
    private float mMaxFlingSpeedFactor;
    private int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private final int mMinHeight;
    private int mMinValue;
    private final int mMinWidth;
    private int mMinimumFlingVelocity;
    private boolean mNeedIntercept;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private int mPreviousScrollerY;
    private final PhysicalVerticalScroller mQuickScroller;
    private int mScrollOffset;
    private int mScrollState;
    private int mSelectorDragHeight;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private int[] mSelectorIndices;
    private final Paint mSelectorWheelPaint;
    private int mSlideTimes;
    private SoundPlayHandler mSoundPlayHandler;
    private boolean mSupportLinearMotorVibrate;
    private int mTextColorHighlight;
    private int mTextColorHint;
    private int mTextSizeHighlight;
    private int mTextSizeHint;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private boolean mWrapSelectorWheel;
    private int selector_middle_item_index;
    private int selector_wheel_item_count;
    private static final AtomicInteger sIdGenerator = new AtomicInteger(0);
    public static final Formatter TWO_DIGIT_FORMATTER = new NumberFormatter(2);
    private static final Interpolator sQuinticInterpolator = new Interpolator() { // from class: com.android.deskclock.widget.NumberPicker.1
        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float f) {
            float f2 = f - 1.0f;
            return (f2 * f2 * f2 * f2 * f2) + 1.0f;
        }
    };

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

    public static class NumberFormatter implements Formatter {
        private final int iWidth;

        public NumberFormatter() {
            this.iWidth = -1;
        }

        public NumberFormatter(int i) {
            this.iWidth = i;
        }

        @Override // com.android.deskclock.widget.NumberPicker.Formatter
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
            private Set<Integer> mRefs;
            private int mSoundId;
            private SoundPool mSoundPlayer;

            private SoundPlayerContainer() {
                this.mRefs = new ArraySet();
            }

            void init(int i) {
                if (this.mSoundPlayer == null) {
                    SoundPool soundPool = new SoundPool(1, 1, 0);
                    this.mSoundPlayer = soundPool;
                    this.mSoundId = soundPool.load(DeskClockApp.getAppContext(), R.raw.numberpicker_value_change, 1);
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
                sPlayerContainer.init(message.arg1);
            } else if (i == 1) {
                sPlayerContainer.play();
            } else {
                if (i != 2) {
                    return;
                }
                sPlayerContainer.release(message.arg1);
            }
        }

        void init(int i) {
            sendMessage(obtainMessage(0, i, 0));
        }

        void play() {
            removeMessages(1);
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
        this(context, attributeSet, R.attr.NumberPickerStyle);
    }

    public NumberPicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.selector_wheel_item_count = 5;
        this.selector_middle_item_index = 5 / 2;
        this.mId = sIdGenerator.incrementAndGet();
        this.MARGIN_LABEL_LEFT = 5;
        this.mNeedIntercept = false;
        this.mLastPlaySoundTime = 0L;
        this.mLastPerformHapticTime = 0L;
        this.mSlideTimes = 0;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mHapticMesh = 268435462;
        this.mMaxWidth = 400;
        this.mSelectorIndexToStringCache = new SparseArray<>();
        this.mSelectorIndices = new int[this.selector_wheel_item_count];
        this.mInitialScrollOffset = Integer.MIN_VALUE;
        this.mScrollState = 0;
        this.mTextSizeHighlight = 25;
        this.mTextSizeHint = 14;
        this.mLabelTextSize = 10;
        this.mLabelTextSizeTiny = 28;
        this.mTextColorHighlight = -452984832;
        this.mTextColorHint = 201326592;
        this.mLabelTextColor = -303101;
        this.mMaxFlingSpeedFactor = 1.0f;
        this.mHighlightTypeface = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_60);
        this.mHintTypeface = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_50);
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.NumberPicker, i, 0);
        this.mLabel = typedArrayObtainStyledAttributes.getText(3);
        this.mTextSizeHighlight = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, this.mTextSizeHighlight);
        this.mTextSizeHint = typedArrayObtainStyledAttributes.getDimensionPixelSize(9, this.mTextSizeHint);
        if (Util.isTinyScreen(context)) {
            this.mLabelTextSize = (int) context.getResources().getDimension(R.dimen.life_post_weather_temp_text_size);
        } else {
            this.mLabelTextSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(4, this.mLabelTextSize);
        }
        this.mTextColorHighlight = typedArrayObtainStyledAttributes.getColor(0, this.mTextColorHighlight);
        this.mTextColorHint = typedArrayObtainStyledAttributes.getColor(1, this.mTextColorHint);
        this.mLabelTextColor = typedArrayObtainStyledAttributes.getColor(6, this.mLabelTextColor);
        typedArrayObtainStyledAttributes.recycle();
        initSoundPlayer();
        this.mMinHeight = -1;
        this.mMaxHeight = 232;
        this.mMinWidth = -1;
        this.mMaxWidth = -1;
        setWillNotDraw(false);
        setNestedScrollingEnabled(true);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity() / 4;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(this.mTextSizeHighlight);
        this.mSelectorWheelPaint = paint;
        this.mFlingScroller = new PhysicalVerticalScroller(getContext());
        this.mAdjustScroller = new PhysicalVerticalScroller(getContext());
        this.mQuickScroller = new PhysicalVerticalScroller(getContext(), new PhysicalScrollerQuickHelper());
        this.mSupportLinearMotorVibrate = Util.isSupportLinearMotorVibrate();
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.view_number_picker_mark, (ViewGroup) this, true);
        TextView textView = (TextView) findViewById(R.id.number_picker_mark);
        this.mMarkTv = textView;
        textView.setText(String.valueOf(this.mValue));
        this.mMarkTv.setTextColor(0);
        this.mMarkTv.setGravity(GravityCompat.START);
        this.mMarkTv.setVisibility(4);
        setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.widget.NumberPicker.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NumberPicker numberPicker = NumberPicker.this;
                numberPicker.setContentDescription(numberPicker.getDisplayValue());
                NumberPicker.this.sendAccessibilityEvent(4);
            }
        });
    }

    public void setSelectorIndicesCount(int i) {
        this.selector_wheel_item_count = i;
        this.selector_middle_item_index = i / 2;
        this.mSelectorIndices = new int[i];
        this.mSelectorIndexToStringCache.clear();
        initializeSelectorWheelIndices();
    }

    public void setMaxHeight(int i) {
        this.mMaxHeight = i;
    }

    public int getSlideTimes() {
        return this.mSlideTimes;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        tryComputeMaxWidth();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int measuredWidth2 = this.mMarkTv.getMeasuredWidth();
        int measuredHeight2 = this.mMarkTv.getMeasuredHeight();
        int i5 = (measuredWidth - measuredWidth2) / 2;
        int i6 = (measuredHeight - measuredHeight2) / 2;
        this.mMarkTv.layout(i5, i6, measuredWidth2 + i5, measuredHeight2 + i6);
        if (z) {
            initializeSelectorWheel();
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(makeMeasureSpec(i, this.mMaxWidth), makeMeasureSpec(i2, this.mMaxHeight));
        setMeasuredDimension(resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), i), resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), i2));
    }

    private void initSoundPlayer() {
        if (this.mSoundPlayHandler == null) {
            SoundPlayHandler soundPlayHandler = new SoundPlayHandler(WorkerThreads.aquireWorker(SOUND_PLAY_THREAD));
            this.mSoundPlayHandler = soundPlayHandler;
            soundPlayHandler.init(this.mId);
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

    private boolean moveToFinalScrollerPosition(PhysicalVerticalScroller physicalVerticalScroller) {
        physicalVerticalScroller.forceFinished(true);
        int finalYPosition = physicalVerticalScroller.getFinalYPosition() - physicalVerticalScroller.getCurrentYPosition();
        int i = this.mInitialScrollOffset - ((this.mCurrentScrollOffset + finalYPosition) % this.mSelectorElementHeight);
        if (i == 0) {
            return false;
        }
        int iAbs = Math.abs(i);
        int i2 = this.mSelectorElementHeight;
        if (iAbs > i2 / 2) {
            i = i > 0 ? i - i2 : i + i2;
        }
        scrollBy(0, finalYPosition + i);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mInterceptDownX = (int) motionEvent.getX();
            this.mInterceptDownY = (int) motionEvent.getY();
            requestDisallowParentInterceptTouchEvent(true);
            this.mNeedIntercept = true;
        } else if (action == 1) {
            requestDisallowParentInterceptTouchEvent(false);
        } else if (action != 2) {
            if (action == 3) {
                requestDisallowParentInterceptTouchEvent(false);
            }
        } else if (this.mNeedIntercept) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (Math.abs(x - this.mInterceptDownX) > Math.abs(y - this.mInterceptDownY)) {
                requestDisallowParentInterceptTouchEvent(false);
            }
            this.mNeedIntercept = false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled() || motionEvent.getActionMasked() != 0) {
            return false;
        }
        float y = motionEvent.getY();
        this.mLastDownEventY = y;
        this.mLastDownOrMoveEventY = y;
        this.mLastDownEventTime = motionEvent.getEventTime();
        if (!this.mFlingScroller.isScrollFinished()) {
            this.mFlingScroller.forceScrollFinished(true);
            this.mAdjustScroller.forceScrollFinished(true);
            onScrollStateChange(0);
        } else if (!this.mAdjustScroller.isScrollFinished()) {
            this.mFlingScroller.forceScrollFinished(true);
            this.mAdjustScroller.forceScrollFinished(true);
        }
        if (!this.mQuickScroller.isScrollFinished()) {
            this.mQuickScroller.forceScrollFinished(true);
        }
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:23:0x0053  */
    /* JADX WARN: Code duplicated, block: B:25:0x006f  */
    /* JADX WARN: Code duplicated, block: B:26:0x0076  */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int yVelocity;
        if (!isEnabled() || !isShown()) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1) {
            this.mSlideTimes++;
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
            yVelocity = (int) velocityTracker.getYVelocity();
            if (Math.abs(yVelocity) > this.mMinimumFlingVelocity) {
                fling(yVelocity);
                onScrollStateChange(2);
            } else {
                ensureScrollWheelAdjusted(true);
                onScrollStateChange(0);
            }
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        } else if (actionMasked == 2) {
            float y = motionEvent.getY();
            if (this.mScrollState != 1) {
                if (((int) Math.abs(y - this.mLastDownEventY)) > this.mTouchSlop) {
                    onScrollStateChange(1);
                }
            } else {
                scrollBy(0, (int) (y - this.mLastDownOrMoveEventY));
                invalidate();
            }
            this.mLastDownOrMoveEventY = y;
        } else if (actionMasked == 3) {
            this.mSlideTimes++;
            VelocityTracker velocityTracker2 = this.mVelocityTracker;
            velocityTracker2.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
            yVelocity = (int) velocityTracker2.getYVelocity();
            if (Math.abs(yVelocity) > this.mMinimumFlingVelocity) {
                fling(yVelocity);
                onScrollStateChange(2);
            } else {
                ensureScrollWheelAdjusted(true);
                onScrollStateChange(0);
            }
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        return true;
    }

    public void requestDisallowParentInterceptTouchEvent(boolean z) {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(z);
        while (parent != null) {
            if (parent instanceof SpringBackLayout) {
                ((SpringBackLayout) parent).internalRequestDisallowInterceptTouchEvent(z);
            }
            parent = parent.getParent();
        }
    }

    @Override // android.view.View
    public void computeScroll() {
        PhysicalVerticalScroller physicalVerticalScroller = this.mFlingScroller;
        if (physicalVerticalScroller.isScrollFinished()) {
            physicalVerticalScroller = this.mAdjustScroller;
            if (physicalVerticalScroller.isScrollFinished()) {
                physicalVerticalScroller = this.mQuickScroller;
                if (physicalVerticalScroller.isScrollFinished()) {
                    return;
                }
            }
        }
        physicalVerticalScroller.computeScrollOffset();
        int currentYPosition = physicalVerticalScroller.getCurrentYPosition();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = physicalVerticalScroller.getStartYPosition();
        }
        scrollBy(0, currentYPosition - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currentYPosition;
        if (physicalVerticalScroller.isScrollFinished()) {
            onScrollerFinished(physicalVerticalScroller);
        } else {
            invalidate();
        }
    }

    @Override // android.view.View
    public void scrollBy(int i, int i2) {
        int[] iArr = this.mSelectorIndices;
        boolean z = this.mWrapSelectorWheel;
        if (!z) {
            int i3 = this.mSelectorDragHeight;
            if (i2 > i3) {
                i2 = i3;
            }
            if (i2 < (-i3)) {
                i2 = -i3;
            }
        }
        int i4 = this.mCurrentScrollOffset + i2;
        this.mCurrentScrollOffset = i4;
        int i5 = this.mScrollOffset + i2;
        this.mScrollOffset = i5;
        boolean z2 = !z && i2 > 0 && iArr[this.selector_middle_item_index] <= this.mMinValue && i4 - this.mInitialScrollOffset > this.mSelectorDragHeight;
        if ((!z && i2 < 0 && iArr[this.selector_middle_item_index] >= this.mMaxValue && i4 - this.mInitialScrollOffset < (-this.mSelectorDragHeight)) || z2) {
            this.mCurrentScrollOffset = i4 - i2;
            this.mScrollOffset = i5 - i2;
            if (!this.mFlingScroller.isScrollFinished()) {
                this.mFlingScroller.forceScrollFinished(true);
                this.mAdjustScroller.forceScrollFinished(true);
                onScrollStateChange(0);
            } else if (!this.mAdjustScroller.isScrollFinished()) {
                this.mFlingScroller.forceScrollFinished(true);
                this.mAdjustScroller.forceScrollFinished(true);
            }
            if (this.mQuickScroller.isScrollFinished()) {
                return;
            }
            this.mQuickScroller.forceScrollFinished(true);
            onScrollStateChange(0);
            return;
        }
        while (true) {
            int i6 = this.mCurrentScrollOffset;
            if (i6 - this.mInitialScrollOffset <= this.mSelectorDragHeight) {
                break;
            }
            this.mCurrentScrollOffset = i6 - this.mSelectorElementHeight;
            decrementSelectorIndices(iArr);
            setValueInternal(iArr[this.selector_middle_item_index], true);
        }
        while (true) {
            int i7 = this.mCurrentScrollOffset;
            if (i7 - this.mInitialScrollOffset >= (-this.mSelectorDragHeight)) {
                break;
            }
            this.mCurrentScrollOffset = i7 + this.mSelectorElementHeight;
            incrementSelectorIndices(iArr);
            setValueInternal(iArr[this.selector_middle_item_index], true);
        }
        while (true) {
            int i8 = this.mScrollOffset;
            if (i8 - this.mInitialScrollOffset <= (this.mSelectorDragHeight * 2) - 5) {
                break;
            }
            this.mScrollOffset = i8 - this.mSelectorElementHeight;
            triggerSound();
        }
        while (true) {
            int i9 = this.mScrollOffset;
            if (i9 - this.mInitialScrollOffset >= (this.mSelectorDragHeight * (-2)) + 5) {
                return;
            }
            this.mScrollOffset = i9 + this.mSelectorElementHeight;
            triggerSound();
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
    }

    public void setValue(int i) {
        setValueInternal(i, false);
    }

    public void setValueWithAnim(int i, String str) {
        int i2 = (this.mMaxValue - this.mMinValue) + 1;
        int i3 = (((i + i2) - this.mValue) % i2) * this.mSelectorElementHeight;
        this.mPreviousScrollerY = 0;
        this.mQuickScroller.startScroll(0, -i3, str);
        invalidate();
    }

    private void tryComputeMaxWidth() {
        float length;
        this.mSelectorWheelPaint.setTextSize(this.mTextSizeHighlight);
        MiuiFont.setPaintFont(this.mSelectorWheelPaint, this.mHighlightTypeface);
        String[] strArr = this.mDisplayedValues;
        int i = 0;
        if (strArr == null) {
            float f = 0.0f;
            while (i < 9) {
                float fMeasureText = this.mSelectorWheelPaint.measureText(String.valueOf(i));
                if (fMeasureText > f) {
                    f = fMeasureText;
                }
                i++;
            }
            length = (int) (formatNumber(this.mMaxValue).length() * f);
        } else {
            int length2 = strArr.length;
            float f2 = -1.0f;
            while (i < length2) {
                float fMeasureText2 = this.mSelectorWheelPaint.measureText(this.mDisplayedValues[i]);
                if (fMeasureText2 > f2) {
                    f2 = fMeasureText2;
                }
                i++;
            }
            length = f2;
        }
        this.mDisplayedMaxTextWidth = length;
        float paddingLeft = length + getPaddingLeft() + getPaddingRight();
        if (this.mMaxWidth != paddingLeft) {
            int i2 = this.mMinWidth;
            if (paddingLeft > i2) {
                this.mMaxWidth = (int) paddingLeft;
            } else {
                this.mMaxWidth = i2;
            }
        }
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
        WorkerThreads.releaseWorker(SOUND_PLAY_THREAD);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        float right = (((getRight() - getLeft()) + getPaddingLeft()) - getPaddingRight()) / 2;
        float paddingTop = this.mCurrentScrollOffset + getPaddingTop();
        float paddingTop2 = ((this.selector_wheel_item_count * this.mSelectorElementHeight) / 2.0f) + getPaddingTop();
        SparseArray<String> sparseArray = this.mSelectorIndexToStringCache;
        for (int i : this.mSelectorIndices) {
            String str = sparseArray.get(i);
            float fAbs = Math.abs(paddingTop2 - paddingTop) / this.mSelectorElementHeight;
            if (!MiuiSdk.isSuperLiteMode() && !MiuiSdk.isLiteV1StockMode()) {
                this.mSelectorWheelPaint.setTextSize(getTextSize(fAbs, this.mTextSizeHighlight, this.mTextSizeHint));
            }
            this.mSelectorWheelPaint.setColor(evaluate(fAbs / ((this.selector_wheel_item_count - 1) / 2), this.mTextColorHighlight, this.mTextColorHint).intValue());
            MiuiFont.setPaintFont(this.mSelectorWheelPaint, fAbs < 0.5f ? this.mHighlightTypeface : this.mHintTypeface);
            Paint.FontMetricsInt fontMetricsInt = this.mSelectorWheelPaint.getFontMetricsInt();
            float f = paddingTop - ((fontMetricsInt.descent + fontMetricsInt.ascent) / 2.0f);
            if (!TextUtils.isEmpty(str)) {
                canvas.drawText(str, right, f, this.mSelectorWheelPaint);
            }
            paddingTop += this.mSelectorElementHeight;
        }
    }

    /* JADX WARN: Code duplicated, block: B:4:0x0005 A[PHI: r0
  0x0005: PHI (r0v14 float) = (r0v0 float), (r0v1 float) binds: [B:3:0x0003, B:6:0x000b] A[DONT_GENERATE, DONT_INLINE]] */
    private Integer evaluate(float f, int i, int i2) {
        float f2 = 0.0f;
        if (f < 0.0f) {
            f = f2;
        } else {
            f2 = 1.0f;
            if (f >= 1.0f) {
                f = f2;
            }
        }
        double d = f;
        float f3 = d >= 0.5d ? (float) (((double) 0.85f) + (((d - 0.5d) / 0.5d) * ((double) 0.14999998f))) : (float) ((d / 0.5d) * ((double) 0.85f));
        int i3 = (i >> 24) & 255;
        int i4 = (i >> 16) & 255;
        int i5 = (i >> 8) & 255;
        int i6 = i & 255;
        return Integer.valueOf(((i3 + ((int) ((((i2 >> 24) & 255) - i3) * f3))) << 24) | ((i4 + ((int) ((((i2 >> 16) & 255) - i4) * f3))) << 16) | ((i5 + ((int) ((((i2 >> 8) & 255) - i5) * f3))) << 8) | (i6 + ((int) (f3 * ((i2 & 255) - i6)))));
    }

    private float getTextSize(float f, int i, int i2) {
        int i3 = this.selector_middle_item_index;
        if (f >= i3) {
            f = i3;
        }
        return i - ((f / i3) * (i - i2));
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
            int wrappedSelectorIndex = (i - this.selector_middle_item_index) + value;
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
        if (z) {
            notifyChange(i2);
        }
        initializeSelectorWheelIndices();
        invalidate();
    }

    private void changeValueByOne(boolean z) {
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
        int bottom = (((getBottom() - getTop()) - getPaddingBottom()) - getPaddingTop()) / this.selector_wheel_item_count;
        this.mSelectorElementHeight = bottom;
        this.mSelectorDragHeight = bottom / 2;
        int i = bottom / 2;
        this.mInitialScrollOffset = i;
        this.mCurrentScrollOffset = i;
        this.mScrollOffset = i;
        this.mFlingScroller.setElementHeight(bottom);
        this.mAdjustScroller.setElementHeight(this.mSelectorElementHeight);
        this.mQuickScroller.setElementHeight(this.mSelectorElementHeight);
    }

    private void onScrollerFinished(OverScroller overScroller) {
        if (overScroller == this.mFlingScroller || overScroller == this.mQuickScroller) {
            ensureScrollWheelAdjusted(false);
            onScrollStateChange(0);
        }
    }

    private void onScrollStateChange(int i) {
        if (this.mScrollState == i) {
            return;
        }
        if (i == 0) {
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
        this.mFlingScroller.fling(this.mInitialScrollOffset - this.mCurrentScrollOffset, 0, 0, 0, i, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invalidate();
    }

    private int getWrappedSelectorIndex(int i) {
        int i2 = this.mMaxValue;
        if (i > i2) {
            int i3 = this.mMinValue;
            return (i3 + ((i - i2) % (i2 - i3))) - 1;
        }
        int i4 = this.mMinValue;
        return i < i4 ? (i2 - ((i4 - i) % (i2 - i4))) + 1 : i;
    }

    private void incrementSelectorIndices(int[] iArr) {
        int i = 0;
        while (i < iArr.length - 1) {
            int i2 = i + 1;
            iArr[i] = iArr[i2];
            i = i2;
        }
        int i3 = iArr[iArr.length - 2] + 1;
        if (this.mWrapSelectorWheel && i3 > this.mMaxValue) {
            i3 = this.mMinValue;
        }
        iArr[iArr.length - 1] = i3;
        ensureCachedScrollSelectorValue(i3);
    }

    private void decrementSelectorIndices(int[] iArr) {
        for (int length = iArr.length - 1; length > 0; length--) {
            iArr[length] = iArr[length - 1];
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

    private String formatNumber(int i) {
        Formatter formatter = this.mFormatter;
        return formatter != null ? formatter.format(i) : SimpleNumberFormatter.format(i);
    }

    private void notifyChange(int i) {
        OnValueChangeListener onValueChangeListener = this.mOnValueChangeListener;
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChange(this, i, this.mValue);
        }
        this.mMarkTv.setText(String.valueOf(this.mValue));
    }

    private void postAccessibilityEvent() {
        Runnable runnable = this.mAccessbilityRunnable;
        if (runnable == null) {
            this.mAccessbilityRunnable = new Runnable() { // from class: com.android.deskclock.widget.NumberPicker.3
                @Override // java.lang.Runnable
                public void run() {
                    NumberPicker.this.sendAccessibilityEvent(4);
                }
            };
        } else {
            this.mMainHandler.removeCallbacks(runnable);
        }
        this.mMainHandler.postDelayed(this.mAccessbilityRunnable, 150L);
    }

    private void triggerSound() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - this.mLastPlaySoundTime > 40) {
            this.mLastPlaySoundTime = jCurrentTimeMillis;
            playSound();
        }
        if (this.mSupportLinearMotorVibrate) {
            HapticHelper.getInstance().perform(this.mHapticMesh, this);
        }
    }

    public void setHapticMesh(int i) {
        this.mHapticMesh = i;
    }

    private int getSelectedPos(String str) {
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

    private boolean ensureScrollWheelAdjusted(boolean z) {
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
        int i3 = i;
        if (z) {
            this.mAdjustScroller.startScroll(0, 0, 0, i3, 800);
        } else {
            scrollBy(0, i3);
        }
        invalidate();
        return true;
    }

    private void refreshWheel() {
        initializeSelectorWheelIndices();
        invalidate();
    }

    public void setTypeface(Typeface typeface, Typeface typeface2) {
        if (typeface == null || typeface2 == null) {
            return;
        }
        this.mHintTypeface = typeface;
        this.mHighlightTypeface = typeface2;
        invalidate();
    }

    public void setColor(int i, int i2) {
        this.mTextColorHighlight = i;
        this.mTextColorHint = i2;
        invalidate();
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (Build.VERSION.SDK_INT < 30) {
            onPopulateAccessibilityEvent(accessibilityEvent);
            return true;
        }
        return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        if (Build.VERSION.SDK_INT < 30) {
            accessibilityEvent.getText().add(getDisplayValue());
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (Build.VERSION.SDK_INT < 30) {
            accessibilityEvent.setClassName(NumberPicker.class.getName());
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return NumberPicker.class.getName();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (Build.VERSION.SDK_INT < 30) {
            accessibilityNodeInfo.setClassName(NumberPicker.class.getName());
            return;
        }
        if (isEnabled()) {
            accessibilityNodeInfo.setScrollable(true);
            accessibilityNodeInfo.addAction(8192);
            accessibilityNodeInfo.addAction(4096);
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS);
            AccessibilityNodeInfo.RangeInfo.obtain(0, this.mMinValue - 1, this.mMaxValue + 1, this.mValue);
            accessibilityNodeInfo.setClassName(SeekBar.class.getName());
            StringBuilder sb = new StringBuilder();
            String[] strArr = this.mDisplayedValues;
            String string = sb.append(strArr == null ? formatNumber(this.mValue) : strArr[this.mValue - this.mMinValue]).append(TextUtils.isEmpty(this.mLabel) ? "" : this.mLabel).toString();
            accessibilityNodeInfo.setText(null);
            accessibilityNodeInfo.setContentDescription(string);
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (Build.VERSION.SDK_INT < 30) {
            return super.performAccessibilityAction(i, bundle);
        }
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

    /* JADX INFO: Access modifiers changed from: private */
    public String getDisplayValue() {
        String strValueOf = String.valueOf(this.mValue);
        String[] strArr = this.mDisplayedValues;
        if (strArr != null) {
            try {
                strValueOf = strArr[this.mValue];
            } catch (Exception unused) {
                strValueOf = String.valueOf(this.mValue);
            }
        }
        return !TextUtils.isEmpty(this.mLabel) ? strValueOf + this.mLabel.toString() : strValueOf;
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (!this.mFlingScroller.isScrollFinished()) {
            this.mFlingScroller.forceScrollFinished(true);
            this.mAdjustScroller.forceScrollFinished(true);
            onScrollStateChange(0);
            ensureScrollWheelAdjusted(false);
        }
        if (!this.mAdjustScroller.isScrollFinished()) {
            this.mAdjustScroller.forceScrollFinished(true);
            onScrollStateChange(0);
            ensureScrollWheelAdjusted(false);
        }
        if (this.mQuickScroller.isScrollFinished()) {
            return;
        }
        this.mQuickScroller.forceScrollFinished(true);
        onScrollStateChange(0);
        ensureScrollWheelAdjusted(false);
    }

    public void stopScroll() {
        if (!this.mFlingScroller.isScrollFinished()) {
            this.mFlingScroller.forceScrollFinished(true);
            this.mAdjustScroller.forceScrollFinished(true);
            onScrollStateChange(0);
            ensureScrollWheelAdjusted(false);
        }
        if (!this.mAdjustScroller.isScrollFinished()) {
            this.mAdjustScroller.forceScrollFinished(true);
            onScrollStateChange(0);
            ensureScrollWheelAdjusted(false);
        }
        if (this.mQuickScroller.isScrollFinished()) {
            return;
        }
        this.mQuickScroller.forceScrollFinished(true);
        onScrollStateChange(0);
        ensureScrollWheelAdjusted(false);
    }

    public void setTextSize(int i, int i2) {
        this.mTextSizeHighlight = i;
        this.mSelectorWheelPaint.setTextSize(i);
        this.mTextSizeHint = i2;
        invalidate();
        requestLayout();
    }

    public static class HapticHelper {
        private static HapticHelper mInstance;
        private VibrationAttributes attributes;
        private boolean doesSupportHaptic;
        private HapticFeedbackCompat hapticFeedbackCompat;
        private long mLastPerformHapticTime = 0;

        private HapticHelper() {
            if (Build.VERSION.SDK_INT >= 30) {
                this.attributes = new VibrationAttributes.Builder().setUsage(18).build();
                this.hapticFeedbackCompat = new HapticFeedbackCompat(DeskClockApp.getAppContext());
                this.doesSupportHaptic = HapticCompat.doesSupportHaptic(HapticCompat.HapticVersion.HAPTIC_VERSION_2);
            }
        }

        public static HapticHelper getInstance() {
            if (mInstance == null) {
                mInstance = new HapticHelper();
            }
            return mInstance;
        }

        public void perform(int i, View view) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis - this.mLastPerformHapticTime > 40) {
                this.mLastPerformHapticTime = jCurrentTimeMillis;
                if (this.doesSupportHaptic) {
                    this.hapticFeedbackCompat.performHapticFeedback(this.attributes, HapticFeedbackConstants.MIUI_GEAR_HEAVY);
                } else {
                    HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_GEAR_HEAVY, i);
                }
            }
        }
    }
}
