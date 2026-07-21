package miuix.appcompat.app.floatingactivity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import miuix.appcompat.R;
import miuix.appcompat.app.floatingactivity.helper.BaseFloatingActivityHelper;
import miuix.core.util.WindowUtils;
import miuix.internal.util.AttributeResolver;
import miuix.reflect.Reflects;

/* JADX INFO: loaded from: classes2.dex */
public class FloatingABOLayoutSpec {
    private static final String TAG = "FloatingABOLayoutSpec";
    private Context mContext;
    private DisplayMetrics mDisplayMetrics;
    private TypedValue mFixedHeightMajor;
    private TypedValue mFixedHeightMinor;
    private TypedValue mFixedWidthMajor;
    private TypedValue mFixedWidthMinor;
    private boolean mFloatingTheme;
    private boolean mFloatingWindow;
    private boolean mIsInDialogMode;
    private TypedValue mMaxHeightMajor;
    private TypedValue mMaxHeightMinor;
    private TypedValue mMaxWidthMajor;
    private TypedValue mMaxWidthMinor;
    private Point mPhysicalSize;

    public FloatingABOLayoutSpec(Context context) {
        this(context, null);
    }

    public FloatingABOLayoutSpec(Context context, AttributeSet attributeSet) {
        this.mFloatingTheme = false;
        this.mFloatingWindow = false;
        this.mContext = context;
        this.mPhysicalSize = new Point();
        updatePhysicalSize(context);
        parseWindowSize(context, attributeSet);
    }

    public void updatePhysicalSize(Context context) {
        this.mDisplayMetrics = context.getResources().getDisplayMetrics();
        this.mPhysicalSize = WindowUtils.getWindowSize(context);
    }

    public void setIsInDialogMode(boolean z) {
        this.mIsInDialogMode = z;
    }

    private void parseWindowSize(Context context, AttributeSet attributeSet) {
        if (attributeSet == null) {
            return;
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Window);
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowFixedWidthMinor)) {
            this.mFixedWidthMinor = new TypedValue();
            typedArrayObtainStyledAttributes.getValue(R.styleable.Window_windowFixedWidthMinor, this.mFixedWidthMinor);
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowFixedHeightMajor)) {
            this.mFixedHeightMajor = new TypedValue();
            typedArrayObtainStyledAttributes.getValue(R.styleable.Window_windowFixedHeightMajor, this.mFixedHeightMajor);
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowFixedWidthMajor)) {
            this.mFixedWidthMajor = new TypedValue();
            typedArrayObtainStyledAttributes.getValue(R.styleable.Window_windowFixedWidthMajor, this.mFixedWidthMajor);
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowFixedHeightMinor)) {
            this.mFixedHeightMinor = new TypedValue();
            typedArrayObtainStyledAttributes.getValue(R.styleable.Window_windowFixedHeightMinor, this.mFixedHeightMinor);
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowMaxWidthMinor)) {
            this.mMaxWidthMinor = new TypedValue();
            typedArrayObtainStyledAttributes.getValue(R.styleable.Window_windowMaxWidthMinor, this.mMaxWidthMinor);
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowMaxWidthMajor)) {
            this.mMaxWidthMajor = new TypedValue();
            typedArrayObtainStyledAttributes.getValue(R.styleable.Window_windowMaxWidthMajor, this.mMaxWidthMajor);
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowMaxHeightMajor)) {
            this.mMaxHeightMajor = new TypedValue();
            typedArrayObtainStyledAttributes.getValue(R.styleable.Window_windowMaxHeightMajor, this.mMaxHeightMajor);
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Window_windowMaxHeightMinor)) {
            this.mMaxHeightMinor = new TypedValue();
            typedArrayObtainStyledAttributes.getValue(R.styleable.Window_windowMaxHeightMinor, this.mMaxHeightMinor);
        }
        this.mFloatingTheme = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Window_isMiuixFloatingTheme, false);
        this.mFloatingWindow = BaseFloatingActivityHelper.isFloatingWindow(context);
        typedArrayObtainStyledAttributes.recycle();
    }

    public void onFloatingModeChanged(boolean z) {
        if (this.mFloatingTheme) {
            this.mFloatingWindow = z;
        }
    }

    public int getWidthMeasureSpecForDialog(int i) {
        return getMeasureSpec(i, true, this.mFixedWidthMinor, this.mFixedWidthMajor, this.mMaxWidthMinor, this.mMaxWidthMajor);
    }

    public int getHeightMeasureSpecForDialog(int i) {
        return getMeasureSpec(i, false, this.mFixedHeightMinor, this.mFixedHeightMajor, this.mMaxHeightMinor, this.mMaxHeightMajor);
    }

    public int getWidthMeasureSpec(int i) {
        return getMeasureSpec(i, true, getFixedWidthMinor(), getFixedWidthMajor(), getMaxWidthMinor(), getMaxWidthMajor());
    }

    public int getHeightMeasureSpec(int i) {
        return getMeasureSpec(i, false, getFixedHeightMinor(), getFixedHeightMajor(), getMaxHeightMinor(), getMaxHeightMajor());
    }

    private int getMeasureSpec(int i, boolean z, TypedValue typedValue, TypedValue typedValue2, TypedValue typedValue3, TypedValue typedValue4) {
        if (View.MeasureSpec.getMode(i) != Integer.MIN_VALUE) {
            return i;
        }
        boolean zIsPortrait = isPortrait();
        if (!zIsPortrait) {
            typedValue = typedValue2;
        }
        int iResolveDimension = resolveDimension(typedValue, z);
        if (iResolveDimension > 0) {
            return View.MeasureSpec.makeMeasureSpec(iResolveDimension, BasicMeasure.EXACTLY);
        }
        if (!zIsPortrait) {
            typedValue3 = typedValue4;
        }
        int iResolveDimension2 = resolveDimension(typedValue3, z);
        return iResolveDimension2 > 0 ? View.MeasureSpec.makeMeasureSpec(Math.min(iResolveDimension2, View.MeasureSpec.getSize(i)), Integer.MIN_VALUE) : i;
    }

    private int resolveDimension(TypedValue typedValue, boolean z) {
        float fraction;
        if (typedValue != null && typedValue.type != 0) {
            if (typedValue.type == 5) {
                fraction = typedValue.getDimension(this.mDisplayMetrics);
            } else if (typedValue.type == 6) {
                float f = z ? this.mPhysicalSize.x : this.mPhysicalSize.y;
                fraction = typedValue.getFraction(f, f);
            }
            return (int) fraction;
        }
        return 0;
    }

    private boolean isPortrait() {
        return WindowUtils.isPortrait(this.mContext);
    }

    private TypedValue getFixedWidthMinor() {
        if (this.mFloatingTheme && this.mFloatingWindow) {
            return this.mFixedWidthMinor;
        }
        return null;
    }

    private TypedValue getFixedHeightMajor() {
        if (this.mFloatingTheme && this.mFloatingWindow) {
            return this.mFixedHeightMajor;
        }
        return null;
    }

    private TypedValue getFixedWidthMajor() {
        if (this.mFloatingTheme && this.mFloatingWindow) {
            return this.mFixedWidthMajor;
        }
        return null;
    }

    private TypedValue getFixedHeightMinor() {
        if (this.mFloatingTheme && this.mFloatingWindow) {
            return this.mFixedHeightMinor;
        }
        return null;
    }

    private TypedValue getMaxWidthMinor() {
        if (this.mFloatingTheme && this.mFloatingWindow) {
            return this.mMaxWidthMinor;
        }
        return null;
    }

    private TypedValue getMaxWidthMajor() {
        if (this.mFloatingTheme && this.mFloatingWindow) {
            return this.mMaxWidthMajor;
        }
        return null;
    }

    private TypedValue getMaxHeightMinor() {
        if (this.mFloatingTheme && this.mFloatingWindow) {
            return this.mMaxHeightMinor;
        }
        return null;
    }

    private TypedValue getMaxHeightMajor() {
        if (this.mFloatingTheme && this.mFloatingWindow) {
            return this.mMaxHeightMajor;
        }
        return null;
    }

    public void onConfigurationChanged() {
        int themeResourceId;
        Context contextThemeWrapper = this.mContext;
        if (this.mIsInDialogMode) {
            Context context = this.mContext;
            if ((context instanceof ContextThemeWrapper) && (themeResourceId = getThemeResourceId((ContextThemeWrapper) context)) > 0) {
                contextThemeWrapper = new ContextThemeWrapper(this.mContext.getApplicationContext(), themeResourceId);
            }
        }
        this.mFixedWidthMinor = AttributeResolver.resolveTypedValue(contextThemeWrapper, R.attr.windowFixedWidthMinor);
        this.mFixedHeightMajor = AttributeResolver.resolveTypedValue(contextThemeWrapper, R.attr.windowFixedHeightMajor);
        this.mFixedWidthMajor = AttributeResolver.resolveTypedValue(contextThemeWrapper, R.attr.windowFixedWidthMajor);
        this.mFixedHeightMinor = AttributeResolver.resolveTypedValue(contextThemeWrapper, R.attr.windowFixedHeightMinor);
        this.mMaxWidthMinor = AttributeResolver.resolveTypedValue(contextThemeWrapper, R.attr.windowMaxWidthMinor);
        this.mMaxWidthMajor = AttributeResolver.resolveTypedValue(contextThemeWrapper, R.attr.windowMaxWidthMajor);
        this.mMaxHeightMinor = AttributeResolver.resolveTypedValue(contextThemeWrapper, R.attr.windowMaxHeightMinor);
        this.mMaxHeightMajor = AttributeResolver.resolveTypedValue(contextThemeWrapper, R.attr.windowMaxHeightMajor);
        updatePhysicalSize(contextThemeWrapper);
    }

    private int getThemeResourceId(ContextThemeWrapper contextThemeWrapper) {
        try {
            return ((Integer) Reflects.invoke(contextThemeWrapper, Reflects.getMethod(contextThemeWrapper.getClass(), "getThemeResId", (Class<?>[]) null), null)).intValue();
        } catch (RuntimeException e) {
            Log.w(TAG, "catch theme resource get exception", e);
            return 0;
        }
    }
}
