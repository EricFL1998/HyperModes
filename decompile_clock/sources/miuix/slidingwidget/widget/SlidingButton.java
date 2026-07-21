package miuix.slidingwidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.widget.AppCompatCheckBox;
import miuix.slidingwidget.R;

/* JADX INFO: loaded from: classes3.dex */
public class SlidingButton extends AppCompatCheckBox {
    private SlidingButtonHelper mHelper;

    @Override // android.widget.TextView, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.View
    protected boolean onSetAlpha(int i) {
        return true;
    }

    @Override // androidx.appcompat.widget.AppCompatCheckBox, android.widget.CompoundButton
    public void setButtonDrawable(Drawable drawable) {
    }

    public SlidingButton(Context context) {
        this(context, null);
    }

    public SlidingButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.slidingButtonStyle);
    }

    public SlidingButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        SlidingButtonHelper slidingButtonHelper = new SlidingButtonHelper(this);
        this.mHelper = slidingButtonHelper;
        slidingButtonHelper.initAnim();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SlidingButton, i, R.style.Widget_SlidingButton_DayNight);
        this.mHelper.initResource(context, typedArrayObtainStyledAttributes);
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setOnPerformCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper != null) {
            slidingButtonHelper.setOnPerformCheckedChangeListener(onCheckedChangeListener);
        }
    }

    public void setTintOfBarOn(int i) {
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper != null) {
            slidingButtonHelper.getBarOn().setTint(i);
        }
    }

    @Override // android.view.View
    public void setLayerType(int i, Paint paint) {
        super.setLayerType(i, paint);
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper != null) {
            slidingButtonHelper.setLayerType(i);
        }
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean z) {
        if (isChecked() != z) {
            super.setChecked(z);
            boolean zIsChecked = isChecked();
            SlidingButtonHelper slidingButtonHelper = this.mHelper;
            if (slidingButtonHelper != null) {
                slidingButtonHelper.setChecked(zIsChecked);
            }
        }
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        super.setAlpha(f);
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper != null) {
            slidingButtonHelper.setAlpha(f);
        }
        invalidate();
    }

    @Override // android.view.View
    public float getAlpha() {
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper != null) {
            return slidingButtonHelper.getAlpha();
        }
        return super.getAlpha();
    }

    @Override // androidx.appcompat.widget.AppCompatCheckBox, android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper != null) {
            slidingButtonHelper.setSliderDrawState();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(this.mHelper.getMeasuredWidth(), this.mHelper.getMeasuredHeight());
        this.mHelper.setParentClipChildren();
    }

    @Override // android.widget.CompoundButton, android.view.View
    public boolean performClick() {
        super.performClick();
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper == null) {
            return true;
        }
        slidingButtonHelper.notifyCheckedChangeListener();
        return true;
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper == null) {
            return true;
        }
        slidingButtonHelper.onTouchEvent(motionEvent);
        return true;
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper != null) {
            slidingButtonHelper.onHoverEvent(motionEvent);
        }
        return super.onHoverEvent(motionEvent);
    }

    @Override // android.view.View
    public void setPressed(boolean z) {
        super.setPressed(z);
        invalidate();
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper == null) {
            super.onDraw(canvas);
        } else {
            slidingButtonHelper.onDraw(canvas);
        }
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        SlidingButtonHelper slidingButtonHelper;
        return super.verifyDrawable(drawable) || ((slidingButtonHelper = this.mHelper) != null && slidingButtonHelper.verifyDrawable(drawable));
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        SlidingButtonHelper slidingButtonHelper = this.mHelper;
        if (slidingButtonHelper != null) {
            slidingButtonHelper.jumpDrawablesToCurrentState();
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        String string;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(isChecked());
        accessibilityNodeInfo.setClickable(true);
        accessibilityNodeInfo.setClassName(Switch.class.getName());
        if (Build.VERSION.SDK_INT >= 30) {
            if (isChecked()) {
                string = getContext().getResources().getString(R.string.accessibility_sliding_button_state_description_on);
            } else {
                string = getContext().getResources().getString(R.string.accessibility_sliding_button_state_description_off);
            }
            accessibilityNodeInfo.setStateDescription(string);
        }
    }
}
