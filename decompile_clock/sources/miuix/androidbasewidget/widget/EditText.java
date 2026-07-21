package miuix.androidbasewidget.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import androidx.appcompat.widget.AppCompatEditText;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;

/* JADX INFO: loaded from: classes2.dex */
public class EditText extends AppCompatEditText {
    private static final int LEVEL_ERROR = 404;
    private static final int LEVEL_NORMAL = 0;
    private static final String TAG = "EditText";
    private boolean isAddListener;
    private boolean mCanVerticalScroll;
    private int mCurrentHandleAndCursorColor;
    private TextWatcher mErrorWatcher;
    private int mOffsetHeight;
    private boolean mReachEdgeFlag;
    private int mTextHandleAndCursorColor;

    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.editTextStyle);
    }

    public EditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mReachEdgeFlag = false;
        this.mCurrentHandleAndCursorColor = -1;
        this.mErrorWatcher = new ErrorWatcher();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, miuix.androidbasewidget.R.styleable.EditText, i, miuix.androidbasewidget.R.style.Widget_EditText_DayNight);
        this.mTextHandleAndCursorColor = typedArrayObtainStyledAttributes.getColor(miuix.androidbasewidget.R.styleable.EditText_textHandleAndCursorColor, getResources().getColor(miuix.androidbasewidget.R.color.miuix_appcompat_handle_and_cursor_color_light));
        typedArrayObtainStyledAttributes.recycle();
        Drawable background = getBackground();
        if (background == null || background.getOpacity() == -2) {
            return;
        }
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
    }

    private int obtainHighlightColor() {
        return Color.argb(51, Color.red(this.mTextHandleAndCursorColor), Color.green(this.mTextHandleAndCursorColor), Color.blue(this.mTextHandleAndCursorColor));
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mCanVerticalScroll = canVerticalScroll();
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mReachEdgeFlag = false;
        }
        if (this.mReachEdgeFlag) {
            motionEvent.setAction(3);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean zOnTouchEvent = super.onTouchEvent(motionEvent);
        ViewParent parent = getParent();
        if (this.mCanVerticalScroll) {
            if (!this.mReachEdgeFlag && parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        } else if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(false);
        }
        return zOnTouchEvent;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        this.mCanVerticalScroll = canVerticalScroll();
        if (i2 == this.mOffsetHeight || i2 == 0) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(false);
            }
            this.mReachEdgeFlag = true;
        }
    }

    @Override // android.widget.TextView, android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        boolean zOnPreDraw = super.onPreDraw();
        if (Build.VERSION.SDK_INT >= 29) {
            if (getHighlightColor() != obtainHighlightColor()) {
                setHighlightColor(obtainHighlightColor());
            }
            int i = this.mCurrentHandleAndCursorColor;
            if (i == -1 || i != this.mTextHandleAndCursorColor) {
                Drawable textSelectHandleLeft = getTextSelectHandleLeft();
                Drawable textSelectHandleRight = getTextSelectHandleRight();
                Drawable textSelectHandle = getTextSelectHandle();
                Drawable textCursorDrawable = getTextCursorDrawable();
                Drawable[] drawableArr = {textSelectHandleLeft, textSelectHandleRight, textSelectHandle, textCursorDrawable};
                for (int i2 = 0; i2 < 4; i2++) {
                    Drawable drawable = drawableArr[i2];
                    if (drawable != null) {
                        drawable.setColorFilter(this.mTextHandleAndCursorColor, PorterDuff.Mode.SRC_IN);
                        this.mCurrentHandleAndCursorColor = this.mTextHandleAndCursorColor;
                    }
                }
                setTextSelectHandleLeft(textSelectHandleLeft);
                setTextSelectHandleRight(textSelectHandleRight);
                setTextSelectHandle(textSelectHandle);
                setTextCursorDrawable(textCursorDrawable);
            }
        }
        return zOnPreDraw;
    }

    public void setMiuiStyleError(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            getBackground().setLevel(0);
            return;
        }
        getBackground().setLevel(404);
        if (this.isAddListener) {
            return;
        }
        this.isAddListener = true;
        addTextChangedListener(this.mErrorWatcher);
    }

    private class ErrorWatcher implements TextWatcher {
        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        private ErrorWatcher() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            EditText.this.setMiuiStyleError(null);
            if (EditText.this.isAddListener) {
                EditText.this.isAddListener = false;
                EditText editText = EditText.this;
                editText.removeTextChangedListener(editText.mErrorWatcher);
            }
        }
    }

    private boolean canVerticalScroll() {
        int scrollY = getScrollY();
        int height = getLayout().getHeight() - ((getMeasuredHeight() - getCompoundPaddingTop()) - getCompoundPaddingBottom());
        this.mOffsetHeight = height;
        if (height == 0) {
            return false;
        }
        return scrollY > 0 || scrollY < height - 1;
    }
}
