package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import miuix.androidbasewidget.R;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class StateEditText extends EditText {
    private static final Class<?>[] WIDGET_MANAGER_CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private ExploreByTouchHelper mExploreByTouchHelper;
    private Drawable[] mExtraDrawables;
    private String mLabel;
    private StaticLayout mLabelLayout;
    private int mLabelLength;
    private int mLabelMaxWidth;
    private float mLabelSpacingAdd;
    private float mLabelSpacingMulti;
    private boolean mPressed;
    private WidgetManager mWidgetManager;
    private int mWidgetPadding;

    public StateEditText(Context context) {
        this(context, null);
    }

    public StateEditText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.miuixAppcompatStateEditTextStyle);
    }

    public StateEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLabelSpacingAdd = 0.0f;
        this.mLabelSpacingMulti = 1.0f;
        this.mLabelLayout = null;
        this.mExploreByTouchHelper = new ExploreByTouchHelper(this) { // from class: miuix.androidbasewidget.widget.StateEditText.1
            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected int getVirtualViewAt(float f, float f2) {
                if (StateEditText.this.mExtraDrawables == null) {
                    return Integer.MIN_VALUE;
                }
                for (int i2 = 0; i2 < StateEditText.this.mExtraDrawables.length; i2++) {
                    int scrollX = StateEditText.this.getScrollX();
                    Rect bounds = StateEditText.this.mExtraDrawables[i2].getBounds();
                    if (new Rect(bounds.left - scrollX, bounds.top, bounds.right - scrollX, bounds.bottom).contains((int) f, (int) f2) && StateEditText.this.mExtraDrawables[i2].isVisible()) {
                        return i2;
                    }
                }
                return Integer.MIN_VALUE;
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected void getVisibleVirtualViews(List<Integer> list) {
                if (StateEditText.this.mExtraDrawables == null || StateEditText.this.emptyContentDescription()) {
                    return;
                }
                for (int i2 = 0; i2 < StateEditText.this.mExtraDrawables.length; i2++) {
                    if (StateEditText.this.mExtraDrawables[i2].isVisible()) {
                        list.add(Integer.valueOf(i2));
                    }
                }
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected void onPopulateNodeForVirtualView(int i2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                if (StateEditText.this.mExtraDrawables == null) {
                    return;
                }
                for (int i3 = 0; i3 < StateEditText.this.mExtraDrawables.length; i3++) {
                    if (i2 == i3) {
                        accessibilityNodeInfoCompat.setVisibleToUser(true);
                        accessibilityNodeInfoCompat.setAccessibilityFocused(true);
                        accessibilityNodeInfoCompat.setFocusable(true);
                        accessibilityNodeInfoCompat.setClickable(true);
                        Rect bounds = StateEditText.this.mExtraDrawables[i3].getBounds();
                        accessibilityNodeInfoCompat.setText("");
                        accessibilityNodeInfoCompat.setBoundsInParent(bounds);
                        accessibilityNodeInfoCompat.setClassName(Button.class.getName());
                        accessibilityNodeInfoCompat.addAction(16);
                        StateEditText.this.mWidgetManager.onPopulateNodeForVirtualView(i3, accessibilityNodeInfoCompat);
                    }
                }
            }

            @Override // androidx.customview.widget.ExploreByTouchHelper
            protected boolean onPerformActionForVirtualView(int i2, int i3, Bundle bundle) {
                if (StateEditText.this.mExtraDrawables != null && i3 == 16) {
                    for (int i4 = 0; i4 < StateEditText.this.mExtraDrawables.length; i4++) {
                        if (i2 == i4) {
                            invalidateVirtualView(i2);
                            float fCenterX = StateEditText.this.mExtraDrawables[i4].getBounds().centerX() - StateEditText.this.getScrollX();
                            float fCenterY = StateEditText.this.mExtraDrawables[i4].getBounds().centerY();
                            MotionEvent motionEventObtain = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, fCenterX, fCenterY, 0);
                            StateEditText.this.dispatchEndDrawableTouchEvent(motionEventObtain);
                            motionEventObtain.recycle();
                            MotionEvent motionEventObtain2 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, fCenterX, fCenterY, 0);
                            StateEditText.this.dispatchEndDrawableTouchEvent(motionEventObtain2);
                            motionEventObtain2.recycle();
                            if (!StateEditText.this.mExtraDrawables[i4].isVisible()) {
                                sendEventForVirtualView(i4, 65536);
                                StateEditText.this.sendAccessibilityEvent(32768);
                                return true;
                            }
                            sendEventForVirtualView(i4, 128);
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        initView(context, attributeSet, i);
    }

    private void initView(Context context, AttributeSet attributeSet, int i) {
        String string;
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.miuixAppcompatStateEditText, i, R.style.Widget_StateEditText_DayNight);
            string = typedArrayObtainStyledAttributes.getString(R.styleable.miuixAppcompatStateEditText_miuixAppcompatWidgetManager);
            this.mLabel = typedArrayObtainStyledAttributes.getString(R.styleable.miuixAppcompatStateEditText_miuixAppcompatLabel);
            this.mLabelMaxWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.miuixAppcompatStateEditText_miuixAppcompatLabelMaxWidth, 0);
            this.mWidgetPadding = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.miuixAppcompatStateEditText_miuixAppcompatWidgetPadding, 0);
            this.mLabelSpacingAdd = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.miuixAppcompatStateEditText_miuixAppcompatLabelLineSpacingAdd, (int) this.mLabelSpacingAdd);
            this.mLabelSpacingMulti = typedArrayObtainStyledAttributes.getFloat(R.styleable.miuixAppcompatStateEditText_miuixAppcompatLabelLineSpacingMulti, this.mLabelSpacingMulti);
            typedArrayObtainStyledAttributes.recycle();
        } else {
            string = null;
        }
        setWidgetManager(createWidgetManager(context, string, attributeSet));
        this.mExtraDrawables = null;
        WidgetManager widgetManager = this.mWidgetManager;
        if (widgetManager != null) {
            this.mExtraDrawables = widgetManager.getWidgetDrawables();
        }
        setLabel(this.mLabel);
        if (!TextUtils.isEmpty(this.mLabel)) {
            setTextAlignment(6);
        }
        ViewCompat.setAccessibilityDelegate(this, this.mExploreByTouchHelper);
    }

    public void setWidgetManager(WidgetManager widgetManager) {
        WidgetManager widgetManager2 = this.mWidgetManager;
        if (widgetManager2 != null) {
            widgetManager2.onDetached();
            this.mExtraDrawables = null;
        }
        this.mWidgetManager = widgetManager;
        if (widgetManager != null) {
            this.mExtraDrawables = widgetManager.getWidgetDrawables();
            this.mWidgetManager.onAttached(this);
        }
    }

    public void setLabel(String str) {
        this.mLabel = str;
        if (Build.VERSION.SDK_INT >= 30) {
            setStateDescription(str);
        } else {
            setContentDescription(this.mLabel + ((Object) getText()));
        }
        if (this.mLabelMaxWidth > 0) {
            this.mLabelLength = TextUtils.isEmpty(this.mLabel) ? 0 : Math.min((int) getPaint().measureText(this.mLabel), this.mLabelMaxWidth);
        } else {
            this.mLabelLength = TextUtils.isEmpty(this.mLabel) ? 0 : (int) getPaint().measureText(this.mLabel);
        }
        if (!TextUtils.isEmpty(this.mLabel)) {
            createLabelLayout();
        }
        invalidate();
    }

    private WidgetManager createWidgetManager(Context context, String str, AttributeSet attributeSet) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            Constructor constructor = context.getClassLoader().loadClass(str).asSubclass(WidgetManager.class).getConstructor(WIDGET_MANAGER_CONSTRUCTOR_SIGNATURE);
            constructor.setAccessible(true);
            return (WidgetManager) constructor.newInstance(context, attributeSet);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can't find WidgetManager: " + str, e);
        } catch (IllegalAccessException e2) {
            throw new IllegalStateException("Can't access non-public constructor " + str, e2);
        } catch (InstantiationException e3) {
            throw new IllegalStateException("Could not instantiate the WidgetManager: " + str, e3);
        } catch (NoSuchMethodException e4) {
            throw new IllegalStateException("Error creating WidgetManager " + str, e4);
        } catch (InvocationTargetException e5) {
            throw new IllegalStateException("Could not instantiate the WidgetManager: " + str, e5);
        }
    }

    @Override // android.widget.TextView
    public int getCompoundPaddingRight() {
        int widgetLength;
        int compoundPaddingRight = super.getCompoundPaddingRight();
        if (ViewUtils.isLayoutRtl(this)) {
            widgetLength = getLabelLength();
        } else {
            widgetLength = getWidgetLength();
        }
        return compoundPaddingRight + widgetLength;
    }

    @Override // android.widget.TextView
    public int getCompoundPaddingLeft() {
        int labelLength;
        int compoundPaddingLeft = super.getCompoundPaddingLeft();
        if (ViewUtils.isLayoutRtl(this)) {
            labelLength = getWidgetLength();
        } else {
            labelLength = getLabelLength();
        }
        return compoundPaddingLeft + labelLength;
    }

    private int getLabelLength() {
        int i = this.mLabelLength;
        return i + (i == 0 ? 0 : this.mWidgetPadding);
    }

    private int getWidgetLength() {
        Drawable[] drawableArr = this.mExtraDrawables;
        if (drawableArr == null) {
            return 0;
        }
        int intrinsicWidth = 0;
        for (Drawable drawable : drawableArr) {
            intrinsicWidth = intrinsicWidth + drawable.getIntrinsicWidth() + this.mWidgetPadding;
        }
        return intrinsicWidth;
    }

    @Override // miuix.androidbasewidget.widget.EditText, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return dispatchEndDrawableTouchEvent(motionEvent) || super.dispatchTouchEvent(motionEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean dispatchEndDrawableTouchEvent(MotionEvent motionEvent) {
        if (this.mWidgetManager != null) {
            return isWidgetResumedEvent(motionEvent);
        }
        return false;
    }

    private boolean isWidgetResumedEvent(MotionEvent motionEvent) {
        if (this.mExtraDrawables != null) {
            int scrollX = getScrollX();
            int i = 0;
            while (true) {
                Drawable[] drawableArr = this.mExtraDrawables;
                if (i >= drawableArr.length) {
                    break;
                }
                Rect bounds = drawableArr[i].getBounds();
                if (motionEvent.getX() < bounds.right - scrollX && motionEvent.getX() > bounds.left - scrollX) {
                    return onWidgetTouchEvent(motionEvent, i);
                }
                i++;
            }
        }
        this.mPressed = false;
        return false;
    }

    private boolean onWidgetTouchEvent(MotionEvent motionEvent, int i) {
        WidgetManager widgetManager;
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mPressed = true;
        } else if (action == 1) {
            if (this.mPressed && (widgetManager = this.mWidgetManager) != null) {
                widgetManager.onWidgetClick(i);
                this.mPressed = false;
                return true;
            }
        } else if (action == 3 && this.mPressed) {
            this.mPressed = false;
        }
        return this.mPressed;
    }

    @Override // miuix.androidbasewidget.widget.EditText, android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (TextUtils.isEmpty(this.mLabel) || this.mLabelLayout == null) {
            return;
        }
        if (this.mLabelMaxWidth == 0 && this.mLabelLength > getMeasuredWidth() / 2) {
            this.mLabelLength = getMeasuredWidth() / 2;
            createLabelLayout();
        }
        int height = this.mLabelLayout.getHeight() + getPaddingTop() + getPaddingBottom();
        if (height > getMeasuredHeight()) {
            setMeasuredDimension(getMeasuredWidth(), height);
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawExtraWidget(canvas);
        drawLabel(canvas);
    }

    private void drawLabel(Canvas canvas) {
        if (TextUtils.isEmpty(this.mLabel) || this.mLabelLayout == null) {
            return;
        }
        int color = getPaint().getColor();
        getPaint().setColor(getCurrentTextColor());
        int paddingStart = getPaddingStart();
        int intrinsicWidth = 0;
        Drawable drawable = getCompoundDrawablesRelative()[0];
        if (drawable != null) {
            intrinsicWidth = this.mWidgetPadding + drawable.getIntrinsicWidth();
        }
        float fMax = Math.max(0.0f, (getMeasuredHeight() - this.mLabelLayout.getHeight()) / 2.0f);
        canvas.save();
        if (ViewUtils.isLayoutRtl(this)) {
            canvas.translate((((getScrollX() + getWidth()) - intrinsicWidth) - this.mLabelLength) - paddingStart, fMax);
        } else {
            canvas.translate(paddingStart + getScrollX() + intrinsicWidth, fMax);
        }
        this.mLabelLayout.draw(canvas);
        canvas.restore();
        getPaint().setColor(color);
    }

    private void drawExtraWidget(Canvas canvas) {
        if (this.mExtraDrawables == null) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        int scrollX = getScrollX();
        int paddingEnd = getPaddingEnd();
        Drawable drawable = getCompoundDrawablesRelative()[2];
        int i = 0;
        int intrinsicWidth = drawable == null ? 0 : drawable.getIntrinsicWidth() + this.mWidgetPadding;
        int i2 = height / 2;
        int i3 = 0;
        while (true) {
            Drawable[] drawableArr = this.mExtraDrawables;
            if (i >= drawableArr.length) {
                return;
            }
            int intrinsicWidth2 = drawableArr[i].getIntrinsicWidth();
            int intrinsicHeight = this.mExtraDrawables[i].getIntrinsicHeight();
            if (ViewUtils.isLayoutRtl(this)) {
                int i4 = scrollX + paddingEnd + intrinsicWidth;
                int i5 = intrinsicHeight / 2;
                this.mExtraDrawables[i].setBounds(i4 + i3, i2 - i5, i4 + intrinsicWidth2 + i3, i5 + i2);
            } else {
                int i6 = ((scrollX + width) - paddingEnd) - intrinsicWidth;
                int i7 = intrinsicHeight / 2;
                this.mExtraDrawables[i].setBounds((i6 - intrinsicWidth2) - i3, i2 - i7, i6 - i3, i7 + i2);
            }
            i3 = this.mWidgetPadding + intrinsicWidth2;
            this.mExtraDrawables[i].draw(canvas);
            i++;
        }
    }

    private void createLabelLayout() {
        String str = this.mLabel;
        StaticLayout.Builder builderObtain = StaticLayout.Builder.obtain(str, 0, str.length(), getPaint(), this.mLabelLength);
        builderObtain.setLineSpacing(this.mLabelSpacingAdd, this.mLabelSpacingMulti);
        this.mLabelLayout = builderObtain.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean emptyContentDescription() {
        if (Build.VERSION.SDK_INT >= 30) {
            return TextUtils.isEmpty(getContentDescription()) && TextUtils.isEmpty(getStateDescription()) && TextUtils.isEmpty(getHint()) && TextUtils.isEmpty(getText());
        }
        return TextUtils.isEmpty(getContentDescription()) && TextUtils.isEmpty(getHint()) && TextUtils.isEmpty(getText());
    }

    @Override // android.view.View
    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        if (this.mExploreByTouchHelper.dispatchHoverEvent(motionEvent)) {
            return true;
        }
        return super.dispatchHoverEvent(motionEvent);
    }

    @Override // android.widget.TextView
    public void setInputType(int i) {
        Typeface typeface = getTypeface();
        super.setInputType(i);
        setTypeface(typeface);
    }

    public static abstract class WidgetManager {
        protected abstract Drawable[] getWidgetDrawables();

        protected void onAttached(StateEditText stateEditText) {
        }

        protected void onDetached() {
        }

        protected abstract void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat);

        protected abstract void onWidgetClick(int i);

        public WidgetManager(Context context, AttributeSet attributeSet) {
        }
    }
}
