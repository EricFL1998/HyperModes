package miuix.appcompat.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.ThemedSpinnerAdapter;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.view.ViewCompat;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import miuix.androidbasewidget.widget.CheckedTextView;
import miuix.appcompat.R;
import miuix.appcompat.adapter.SpinnerDoubleLineContentAdapter;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.IActivity;
import miuix.appcompat.internal.adapter.SpinnerCheckableArrayAdapter;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.TaggingDrawableUtil;
import miuix.popupwidget.widget.PopupWindow;
import miuix.view.CompatViewMethod;
import miuix.view.Fence;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class Spinner extends android.widget.Spinner {
    private static Field FORWARDING_LISTENER = null;
    private static final int MAX_ITEMS_MEASURED = 15;
    private static final int MAX_ITEMS_SHOWN = 8;
    private static final int MODE_DIALOG = 0;
    private static final int MODE_DROPDOWN = 1;
    private static final int MODE_THEME = -1;
    private static final String TAG = "Spinner";
    private boolean mDisableChildrenWhenDisabled;
    int mDropDownMaxWidth;
    int mDropDownMinWidth;
    int mDropDownWidth;
    private float mLastDensity;
    private OnSpinnerDismissListener mOnSpinnerDismissListener;
    private SpinnerPopup mPopup;
    private final Context mPopupContext;
    private final boolean mPopupSet;
    private int mSelectedPosition;
    private SpinnerAdapter mTempAdapter;
    final Rect mTempRect;

    public interface OnSpinnerDismissListener {
        void onSpinnerDismiss();
    }

    private interface SpinnerPopup {
        void dismiss();

        void enableHideSoftInput(boolean z);

        Drawable getBackground();

        CharSequence getHintText();

        int getHorizontalOffset();

        int getHorizontalOriginalOffset();

        int getVerticalOffset();

        boolean isShowing();

        void setAdapter(ListAdapter listAdapter);

        void setBackgroundDrawable(Drawable drawable);

        void setDropDownGravity(int i);

        void setHorizontalOffset(int i);

        void setHorizontalOriginalOffset(int i);

        void setPromptText(CharSequence charSequence);

        void setVerticalOffset(int i);

        void show(int i, int i2);

        @Deprecated
        void show(int i, int i2, float f, float f2);
    }

    static {
        try {
            Field declaredField = android.widget.Spinner.class.getDeclaredField("mForwardingListener");
            FORWARDING_LISTENER = declaredField;
            declaredField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "static initializer: ", e);
        }
    }

    public Spinner(Context context) {
        this(context, (AttributeSet) null);
    }

    public Spinner(Context context, int i) {
        this(context, null, R.attr.miuiSpinnerStyle, i);
    }

    public Spinner(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.miuiSpinnerStyle);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, -1);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i, i2, null);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i, int i2, Resources.Theme theme) {
        super(context, attributeSet, i);
        this.mTempRect = new Rect();
        this.mLastDensity = context.getResources().getDisplayMetrics().density;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Spinner, i, 0);
        if (theme != null) {
            this.mPopupContext = new ContextThemeWrapper(context, theme);
        } else {
            int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.Spinner_popupTheme, 0);
            if (resourceId != 0) {
                this.mPopupContext = new ContextThemeWrapper(context, resourceId);
            } else {
                this.mPopupContext = context;
            }
        }
        i2 = i2 == -1 ? typedArrayObtainStyledAttributes.getInt(R.styleable.Spinner_spinnerModeCompat, 0) : i2;
        if (i2 == 0) {
            DialogPopup dialogPopup = new DialogPopup();
            this.mPopup = dialogPopup;
            dialogPopup.setPromptText(typedArrayObtainStyledAttributes.getString(R.styleable.Spinner_android_prompt));
        } else if (i2 == 1) {
            DropdownPopup dropdownPopup = new DropdownPopup(this.mPopupContext);
            TypedArray typedArrayObtainStyledAttributes2 = this.mPopupContext.obtainStyledAttributes(attributeSet, R.styleable.Spinner, i, 0);
            this.mDropDownWidth = typedArrayObtainStyledAttributes2.getLayoutDimension(R.styleable.Spinner_android_dropDownWidth, -2);
            this.mDropDownMinWidth = typedArrayObtainStyledAttributes2.getLayoutDimension(R.styleable.Spinner_dropDownMinWidth, -2);
            this.mDropDownMaxWidth = typedArrayObtainStyledAttributes2.getLayoutDimension(R.styleable.Spinner_dropDownMaxWidth, -2);
            int resourceId2 = typedArrayObtainStyledAttributes2.getResourceId(R.styleable.Spinner_android_popupBackground, 0);
            if (resourceId2 != 0) {
                setPopupBackgroundResource(resourceId2);
            } else {
                dropdownPopup.setBackgroundDrawable(typedArrayObtainStyledAttributes2.getDrawable(R.styleable.Spinner_android_popupBackground));
            }
            dropdownPopup.setPromptText(typedArrayObtainStyledAttributes.getString(R.styleable.Spinner_android_prompt));
            typedArrayObtainStyledAttributes2.recycle();
            this.mPopup = dropdownPopup;
        }
        makeSupperForwardingListenerInvalid();
        CharSequence[] textArray = typedArrayObtainStyledAttributes.getTextArray(R.styleable.Spinner_android_entries);
        if (textArray != null) {
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.miuix_appcompat_simple_spinner_layout, android.R.id.text1, textArray);
            if (i2 == 0) {
                arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dialog_item);
            } else {
                arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dropdown_item);
            }
            setAdapter((SpinnerAdapter) arrayAdapter);
        }
        this.mDisableChildrenWhenDisabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.Spinner_disableChildrenWhenDisabled, false);
        typedArrayObtainStyledAttributes.recycle();
        this.mPopupSet = true;
        SpinnerAdapter spinnerAdapter = this.mTempAdapter;
        if (spinnerAdapter != null) {
            setAdapter(spinnerAdapter);
            this.mTempAdapter = null;
        }
        CompatViewMethod.setForceDarkAllowed(this, false);
    }

    public void enableActivatedState(boolean z) {
        if (z && isClickable()) {
            setActivated(true);
        } else {
            setActivated(false);
        }
    }

    @Override // android.view.View
    public void setActivated(boolean z) {
        if (isClickable()) {
            super.setActivated(z);
        }
    }

    @Override // android.widget.Spinner, android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (this.mDisableChildrenWhenDisabled) {
            setChildEnabled(z);
        }
    }

    private void setChildEnabled(boolean z) {
        View viewFindViewById = findViewById(android.R.id.text1);
        View viewFindViewById2 = findViewById(android.R.id.icon1);
        if (viewFindViewById != null) {
            viewFindViewById.setEnabled(z);
        }
        if (viewFindViewById2 != null) {
            viewFindViewById2.setEnabled(z);
        }
    }

    @Override // android.widget.Spinner, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean zOnTouchEvent = super.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == 0) {
            enableActivatedState(true);
        }
        if (isActivated() && !this.mPopup.isShowing() && ((motionEvent.getAction() == 1 && !isPressed()) || motionEvent.getAction() == 3)) {
            enableActivatedState(false);
        }
        return zOnTouchEvent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSpinnerDismiss() {
        enableActivatedState(false);
        notifySpinnerDismiss();
    }

    private void makeSupperForwardingListenerInvalid() {
        Field field = FORWARDING_LISTENER;
        if (field == null) {
            return;
        }
        try {
            field.set(this, null);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "makeSupperForwardingListenerInvalid: ", e);
        }
        setLongClickable(false);
    }

    @Override // android.widget.Spinner
    public Context getPopupContext() {
        return this.mPopupContext;
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundDrawable(Drawable drawable) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setBackgroundDrawable(drawable);
        } else {
            super.setPopupBackgroundDrawable(drawable);
        }
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundResource(int i) {
        setPopupBackgroundDrawable(AppCompatResources.getDrawable(getPopupContext(), i));
    }

    @Override // android.widget.Spinner
    public Drawable getPopupBackground() {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            return spinnerPopup.getBackground();
        }
        return super.getPopupBackground();
    }

    @Override // android.widget.Spinner
    public void setDropDownVerticalOffset(int i) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setVerticalOffset(i);
        } else {
            super.setDropDownVerticalOffset(i);
        }
    }

    @Override // android.widget.Spinner
    public int getDropDownVerticalOffset() {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            return spinnerPopup.getVerticalOffset();
        }
        return super.getDropDownVerticalOffset();
    }

    @Override // android.widget.Spinner
    public void setDropDownHorizontalOffset(int i) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setHorizontalOriginalOffset(i);
            this.mPopup.setHorizontalOffset(i);
        } else {
            super.setDropDownHorizontalOffset(i);
        }
    }

    public void setDropDownGravity(int i) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setDropDownGravity(i);
        }
    }

    public void enableHideSoftInput(boolean z) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.enableHideSoftInput(z);
        }
    }

    @Override // android.widget.Spinner
    public int getDropDownHorizontalOffset() {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            return spinnerPopup.getHorizontalOffset();
        }
        return super.getDropDownHorizontalOffset();
    }

    @Override // android.widget.Spinner
    public void setDropDownWidth(int i) {
        if (this.mPopup != null) {
            this.mDropDownWidth = i;
        } else {
            super.setDropDownWidth(i);
        }
    }

    @Override // android.widget.Spinner
    public int getDropDownWidth() {
        if (this.mPopup != null) {
            return this.mDropDownWidth;
        }
        return super.getDropDownWidth();
    }

    public void setDoubleLineContentAdapter(SpinnerDoubleLineContentAdapter spinnerDoubleLineContentAdapter) {
        setAdapter((SpinnerAdapter) new SpinnerCheckableArrayAdapter(getContext(), R.layout.miuix_appcompat_simple_spinner_layout, spinnerDoubleLineContentAdapter, new SpinnerCheckedProvider(this)));
    }

    @Override // android.widget.AdapterView
    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        if (!this.mPopupSet) {
            this.mTempAdapter = spinnerAdapter;
            return;
        }
        super.setAdapter(spinnerAdapter);
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DialogPopup) {
            spinnerPopup.setAdapter(new DialogPopupAdapter(spinnerAdapter, getPopupContext().getTheme()));
        } else if (spinnerPopup instanceof DropdownPopup) {
            spinnerPopup.setAdapter(new DropDownPopupAdapter(spinnerAdapter, getPopupContext().getTheme()));
        }
        post(new Runnable() { // from class: miuix.appcompat.widget.Spinner$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1845lambda$setAdapter$0$miuixappcompatwidgetSpinner();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setAdapter$0$miuix-appcompat-widget-Spinner, reason: not valid java name */
    /* synthetic */ void m1845lambda$setAdapter$0$miuixappcompatwidgetSpinner() {
        setChildEnabled(isEnabled());
    }

    public void setDimAmount(float f) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DropdownPopup) {
            ((DropdownPopup) spinnerPopup).setDimAmount(f);
        }
    }

    public void setWindowManagerFlags(int i) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DropdownPopup) {
            ((DropdownPopup) spinnerPopup).setWindowManagerFlags(i);
        }
    }

    public int getWindowManagerFlag() {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DropdownPopup) {
            return ((DropdownPopup) spinnerPopup).getWindowManagerFlags();
        }
        return -1;
    }

    @Override // android.widget.Spinner, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup == null || !spinnerPopup.isShowing()) {
            return;
        }
        this.mPopup.dismiss();
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mPopup == null || View.MeasureSpec.getMode(i) != Integer.MIN_VALUE) {
            return;
        }
        setMeasuredDimension(Math.min(Math.min(getMeasuredWidth(), compatMeasureSelectItemWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(i)), getMeasuredHeight());
    }

    public void setOnSpinnerDismissListener(OnSpinnerDismissListener onSpinnerDismissListener) {
        this.mOnSpinnerDismissListener = onSpinnerDismissListener;
    }

    private void notifySpinnerDismiss() {
        OnSpinnerDismissListener onSpinnerDismissListener = this.mOnSpinnerDismissListener;
        if (onSpinnerDismissListener != null) {
            onSpinnerDismissListener.onSpinnerDismiss();
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        float f = getContext().getResources().getDisplayMetrics().density;
        if (this.mLastDensity != f) {
            this.mLastDensity = f;
            final AdapterView.OnItemSelectedListener onItemSelectedListener = getOnItemSelectedListener();
            setOnItemSelectedListener(null);
            setAdapter(getAdapter());
            post(new Runnable() { // from class: miuix.appcompat.widget.Spinner.1
                @Override // java.lang.Runnable
                public void run() {
                    if (Spinner.this.mSelectedPosition >= 0 && Spinner.this.getAdapter() != null && Spinner.this.mSelectedPosition < Spinner.this.getAdapter().getCount()) {
                        Spinner spinner = Spinner.this;
                        spinner.setSelection(spinner.mSelectedPosition);
                    }
                    if (Spinner.this.getOnItemSelectedListener() == null) {
                        Spinner.this.setOnItemSelectedListener(onItemSelectedListener);
                    }
                }
            });
        }
    }

    @Deprecated
    public boolean performClick(float f, float f2) {
        if (isClickable() && superViewPerformClick()) {
            return true;
        }
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            if (!spinnerPopup.isShowing()) {
                if (!isActivated()) {
                    enableActivatedState(true);
                }
                showPopup(f, f2);
                HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_POPUP_LIGHT);
            }
            return true;
        }
        return super.performClick();
    }

    @Override // android.widget.Spinner, android.view.View
    public boolean performClick() {
        return performClick(0.0f, 0.0f);
    }

    public void setFenceView(View view) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DropdownPopup) {
            ((DropdownPopup) spinnerPopup).setFenceView(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void vibrate() {
        HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_BUTTON_SMALL, HapticFeedbackConstants.MIUI_MESH_NORMAL);
    }

    private boolean superViewPerformClick() {
        sendAccessibilityEvent(1);
        return false;
    }

    @Override // android.widget.Spinner
    public void setPrompt(CharSequence charSequence) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setPromptText(charSequence);
        } else {
            super.setPrompt(charSequence);
        }
    }

    @Override // android.widget.Spinner
    public CharSequence getPrompt() {
        SpinnerPopup spinnerPopup = this.mPopup;
        return spinnerPopup != null ? spinnerPopup.getHintText() : super.getPrompt();
    }

    @Override // android.widget.AbsSpinner, android.widget.AdapterView
    public void setSelection(int i) {
        this.mSelectedPosition = i;
        super.setSelection(i);
        enableActivatedState(false);
    }

    @Override // android.widget.Spinner, android.widget.AdapterView
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DropdownPopup) {
            ((DropdownPopup) spinnerPopup).setOnPopupItemClickListener(onItemClickListener);
        }
    }

    public void setOnDialogPopupItemClickListener(DialogInterface.OnClickListener onClickListener) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DialogPopup) {
            ((DialogPopup) spinnerPopup).setOnPopupItemClickListener(onClickListener);
        }
    }

    private int compatMeasureSelectItemWidth(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        if (spinnerAdapter == null || spinnerAdapter.getCount() == 0) {
            return 0;
        }
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
        View view = spinnerAdapter.getView(Math.max(0, Math.min(spinnerAdapter.getCount() - 1, getSelectedItemPosition())), null, this);
        if (view.getLayoutParams() == null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        }
        view.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
        int iMax = Math.max(0, view.getMeasuredWidth());
        if (drawable == null) {
            return iMax;
        }
        drawable.getPadding(this.mTempRect);
        return iMax + this.mTempRect.left + this.mTempRect.right;
    }

    void showPopup() {
        this.mPopup.show(getTextDirection(), getTextAlignment());
    }

    void showPopup(float f, float f2) {
        this.mPopup.show(getTextDirection(), getTextAlignment(), f, f2);
    }

    public void dismissPopup() {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.dismiss();
        }
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        SpinnerPopup spinnerPopup = this.mPopup;
        savedState.mShowDropdown = spinnerPopup != null && spinnerPopup.isShowing();
        return savedState;
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        ViewTreeObserver viewTreeObserver;
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (!savedState.mShowDropdown || (viewTreeObserver = getViewTreeObserver()) == null) {
            return;
        }
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: miuix.appcompat.widget.Spinner.2
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (!Spinner.this.mPopup.isShowing()) {
                    Spinner.this.showPopup();
                }
                ViewTreeObserver viewTreeObserver2 = Spinner.this.getViewTreeObserver();
                if (viewTreeObserver2 != null) {
                    viewTreeObserver2.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: miuix.appcompat.widget.Spinner.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean mShowDropdown;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        SavedState(Parcel parcel) {
            super(parcel);
            this.mShowDropdown = parcel.readByte() != 0;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeByte(this.mShowDropdown ? (byte) 1 : (byte) 0);
        }
    }

    private static class DialogPopupAdapter extends DropDownAdapter {
        DialogPopupAdapter(SpinnerAdapter spinnerAdapter, Resources.Theme theme) {
            super(spinnerAdapter, theme);
        }
    }

    private static class DropDownPopupAdapter extends DropDownAdapter {
        DropDownPopupAdapter(SpinnerAdapter spinnerAdapter, Resources.Theme theme) {
            super(spinnerAdapter, theme);
        }

        @Override // miuix.appcompat.widget.Spinner.DropDownAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            TaggingDrawableUtil.updateItemBackground(view2, i, getCount());
            return view2;
        }
    }

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        @Override // android.widget.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        public DropDownAdapter(SpinnerAdapter spinnerAdapter, Resources.Theme theme) {
            this.mAdapter = spinnerAdapter;
            if (spinnerAdapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) spinnerAdapter;
            }
            if (theme != null) {
                if (spinnerAdapter instanceof ThemedSpinnerAdapter) {
                    ThemedSpinnerAdapter themedSpinnerAdapter = (ThemedSpinnerAdapter) spinnerAdapter;
                    if (themedSpinnerAdapter.getDropDownViewTheme() != theme) {
                        themedSpinnerAdapter.setDropDownViewTheme(theme);
                        return;
                    }
                    return;
                }
                if (spinnerAdapter instanceof androidx.appcompat.widget.ThemedSpinnerAdapter) {
                    androidx.appcompat.widget.ThemedSpinnerAdapter themedSpinnerAdapter2 = (androidx.appcompat.widget.ThemedSpinnerAdapter) spinnerAdapter;
                    if (themedSpinnerAdapter2.getDropDownViewTheme() == null) {
                        themedSpinnerAdapter2.setDropDownViewTheme(theme);
                    }
                }
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return 0;
            }
            return spinnerAdapter.getCount();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return null;
            }
            return spinnerAdapter.getItem(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return -1L;
            }
            return spinnerAdapter.getItemId(i);
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View dropDownView = getDropDownView(i, view, viewGroup);
            if (view == null) {
                AnimHelper.addItemPressEffect(dropDownView);
            }
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter instanceof SpinnerCheckableArrayAdapter) {
                ((SpinnerCheckableArrayAdapter) spinnerAdapter).setAccessibilityDelegate(dropDownView, i);
            } else if (spinnerAdapter instanceof ArrayAdapter) {
                setAccessibilityDelegate(dropDownView);
            }
            return dropDownView;
        }

        @Override // android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return null;
            }
            return spinnerAdapter.getDropDownView(i, view, viewGroup);
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            return spinnerAdapter != null && spinnerAdapter.hasStableIds();
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter != null) {
                spinnerAdapter.registerDataSetObserver(dataSetObserver);
            }
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter != null) {
                spinnerAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.areAllItemsEnabled();
            }
            return true;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int i) {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.isEnabled(i);
            }
            return true;
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return getCount() == 0;
        }

        public void setAccessibilityDelegate(View view) {
            view.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: miuix.appcompat.widget.Spinner.DropDownAdapter.1
                @Override // android.view.View.AccessibilityDelegate
                public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfo);
                    CheckedTextView checkedTextView = (CheckedTextView) view2.findViewById(android.R.id.text1);
                    accessibilityNodeInfo.setClassName(Checkable.class.getName());
                    accessibilityNodeInfo.setCheckable(true);
                    if (checkedTextView != null) {
                        accessibilityNodeInfo.setChecked(checkedTextView.isChecked());
                        if (checkedTextView.isChecked()) {
                            accessibilityNodeInfo.setClickable(false);
                            accessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                        } else {
                            accessibilityNodeInfo.setClickable(true);
                        }
                    }
                }
            });
        }
    }

    private class DialogPopup implements SpinnerPopup, DialogInterface.OnClickListener {
        private ListAdapter mListAdapter;
        AlertDialog mPopup;
        private DialogInterface.OnClickListener mPopupItemClickListener;
        private CharSequence mPrompt;

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void enableHideSoftInput(boolean z) {
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public Drawable getBackground() {
            return null;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public int getHorizontalOffset() {
            return 0;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public int getHorizontalOriginalOffset() {
            return 0;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public int getVerticalOffset() {
            return 0;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setDropDownGravity(int i) {
        }

        private DialogPopup() {
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void dismiss() {
            AlertDialog alertDialog = this.mPopup;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.mPopup = null;
            }
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public boolean isShowing() {
            AlertDialog alertDialog = this.mPopup;
            return alertDialog != null && alertDialog.isShowing();
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setAdapter(ListAdapter listAdapter) {
            this.mListAdapter = listAdapter;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setPromptText(CharSequence charSequence) {
            this.mPrompt = charSequence;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public CharSequence getHintText() {
            return this.mPrompt;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void show(int i, int i2) {
            if (this.mListAdapter == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(Spinner.this.getPopupContext());
            CharSequence charSequence = this.mPrompt;
            if (charSequence != null) {
                builder.setTitle(charSequence);
            }
            AlertDialog alertDialogCreate = builder.setSingleChoiceItems(this.mListAdapter, Spinner.this.getSelectedItemPosition(), this).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: miuix.appcompat.widget.Spinner.DialogPopup.1
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    Spinner.this.onSpinnerDismiss();
                }
            }).create();
            this.mPopup = alertDialogCreate;
            ListView listView = alertDialogCreate.getListView();
            listView.setTextDirection(i);
            listView.setTextAlignment(i2);
            this.mPopup.show();
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        @Deprecated
        public void show(int i, int i2, float f, float f2) {
            show(i, i2);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            Spinner.this.setSelection(i);
            HapticCompat.performHapticFeedback(Spinner.this, HapticFeedbackConstants.MIUI_POPUP_LIGHT);
            if (Spinner.this.getOnItemClickListener() != null) {
                Spinner.this.performItemClick(null, i, this.mListAdapter.getItemId(i));
            }
            DialogInterface.OnClickListener onClickListener = this.mPopupItemClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(dialogInterface, i);
            }
            dismiss();
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setBackgroundDrawable(Drawable drawable) {
            Log.e(Spinner.TAG, "Cannot set popup background for MODE_DIALOG, ignoring");
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setVerticalOffset(int i) {
            Log.e(Spinner.TAG, "Cannot set vertical offset for MODE_DIALOG, ignoring");
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setHorizontalOffset(int i) {
            Log.e(Spinner.TAG, "Cannot set horizontal offset for MODE_DIALOG, ignoring");
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setHorizontalOriginalOffset(int i) {
            Log.e(Spinner.TAG, "Cannot set horizontal (original) offset for MODE_DIALOG, ignoring");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setOnPopupItemClickListener(DialogInterface.OnClickListener onClickListener) {
            this.mPopupItemClickListener = onClickListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class DropdownPopup extends PopupWindow implements SpinnerPopup {
        private static final int INVALID_VALUE = -1;
        private static final float SCREEN_MARGIN_BOTTOM_PROPORTION = 0.1f;
        private static final float SCREEN_MARGIN_TOP_PROPORTION = 0.1f;
        ListAdapter mAdapter;
        private View mFenceView;
        private CharSequence mHintText;
        private int mOriginalHorizontalOffset;
        private AdapterView.OnItemClickListener mPopupItemClickListener;
        private final Rect mVisibleRect;

        public DropdownPopup(Context context) {
            super(context, null);
            this.mVisibleRect = new Rect();
            Resources resources = context.getResources();
            this.mPopupWindowSpec.mMinHeight = ((resources.getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_padding_single_item) * 2) + resources.getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_item_min_height)) * 2;
            setDropDownGravity(8388691);
            setOnItemClickListener(new AnonymousClass1(Spinner.this));
            this.mIgnoreAnchorVisibility = true;
        }

        /* JADX INFO: renamed from: miuix.appcompat.widget.Spinner$DropdownPopup$1, reason: invalid class name */
        class AnonymousClass1 implements AdapterView.OnItemClickListener {
            final /* synthetic */ Spinner val$this$0;

            AnonymousClass1(Spinner spinner) {
                this.val$this$0 = spinner;
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Spinner.this.setSelection(i);
                Spinner.this.vibrate();
                if (Spinner.this.getOnItemClickListener() != null) {
                    Spinner.this.performItemClick(view, i, DropdownPopup.this.mAdapter.getItemId(i));
                }
                DropdownPopup.this.detachAnchorView();
                Spinner.this.postDelayed(new Runnable() { // from class: miuix.appcompat.widget.Spinner$DropdownPopup$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1846x26ae5f22();
                    }
                }, 60L);
                if (DropdownPopup.this.mPopupItemClickListener != null) {
                    DropdownPopup.this.mPopupItemClickListener.onItemClick(adapterView, view, i, j);
                }
            }

            /* JADX INFO: renamed from: lambda$onItemClick$0$miuix-appcompat-widget-Spinner$DropdownPopup$1, reason: not valid java name */
            /* synthetic */ void m1846x26ae5f22() {
                DropdownPopup.this.dismiss();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setOnPopupItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
            this.mPopupItemClickListener = onItemClickListener;
        }

        @Override // miuix.popupwidget.widget.PopupWindow
        public void setAdapter(ListAdapter listAdapter) {
            super.setAdapter(listAdapter);
            this.mAdapter = listAdapter;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public CharSequence getHintText() {
            return this.mHintText;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setPromptText(CharSequence charSequence) {
            this.mHintText = charSequence;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private void setProperFenceView() {
            if (this.mFenceView != null) {
                return;
            }
            Spinner spinner = Spinner.this;
            if ((spinner.getContext() instanceof IActivity) && ((IActivity) spinner.getContext()).isInFloatingWindowMode()) {
                setFenceView(spinner.getRootView().findViewById(R.id.action_bar_overlay_layout));
                return;
            }
            for (ViewParent parent = spinner.getParent(); parent != 0; parent = parent.getParent()) {
                if ((parent instanceof Fence) && ((Fence) parent).isFenceEnabled() && (parent instanceof View)) {
                    setFenceView((View) parent);
                    return;
                }
            }
        }

        private void showWithAnchor(View view) {
            Log.d(Spinner.TAG, this.mPopupWindowSpec.toString());
            if (getAnchor() != view) {
                setAnchorView(view);
            }
            if (this.mPopupWindowSpec.mAnchorViewBounds.centerX() <= this.mPopupWindowSpec.mDecorViewBounds.centerX()) {
                setDropDownGravity(83);
            } else {
                setDropDownGravity(85);
            }
            int xInWindow = this.mPopupWindowStrategy.getXInWindow(this.mPopupWindowSpec);
            int yInWindow = this.mPopupWindowStrategy.getYInWindow(this.mPopupWindowSpec);
            setWidth(this.mPopupWindowSpec.mFinalPopupWidth);
            setHeight(this.mPopupWindowSpec.mFinalPopupHeight);
            if (!isShowing()) {
                showAtLocation(view, 0, xInWindow, yInWindow);
            } else {
                update(xInWindow, yInWindow, getWidth(), getHeight());
            }
        }

        @Override // miuix.popupwidget.widget.PopupWindow
        protected int[][] getItemViewBounds(ListAdapter listAdapter, ViewGroup viewGroup, Context context) {
            if (listAdapter != null) {
                ListView listView = getListView();
                int iMin = Math.min(listAdapter.getCount(), 8);
                int[][] iArr = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, iMin, 2);
                for (int i = 0; i < iMin; i++) {
                    View view = listAdapter.getView(i, null, listView);
                    view.measure(View.MeasureSpec.makeMeasureSpec(this.mPopupWindowSpec.mMaxWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
                    iArr[i][0] = view.getMeasuredWidth();
                    iArr[i][1] = view.getMeasuredHeight();
                }
                return iArr;
            }
            this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(this.mPopupWindowSpec.mMaxWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
            int[][] iArr2 = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 1, 2);
            iArr2[0][0] = this.mContentView.getMeasuredWidth();
            iArr2[0][1] = this.mContentView.getMeasuredHeight();
            return iArr2;
        }

        @Override // miuix.popupwidget.widget.PopupWindow
        public boolean prepareShow(View view) {
            if (!super.prepareShow(view)) {
                return false;
            }
            setInputMethodMode(2);
            return true;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void show(int i, int i2) {
            boolean zIsShowing = isShowing();
            setProperFenceView();
            setInputMethodMode(2);
            if (prepareShow(Spinner.this)) {
                showWithAnchor(Spinner.this);
                initListView(i, i2);
            }
            if (zIsShowing) {
                return;
            }
            setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() { // from class: miuix.appcompat.widget.Spinner.DropdownPopup.2
                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    Spinner.this.onSpinnerDismiss();
                }
            });
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        @Deprecated
        public void show(int i, int i2, float f, float f2) {
            show(i, i2);
        }

        private void initListView(int i, int i2) {
            ListView listView = getListView();
            listView.setChoiceMode(1);
            listView.setTextDirection(i);
            listView.setTextAlignment(i2);
            int selectedItemPosition = Spinner.this.getSelectedItemPosition();
            listView.setSelection(selectedItemPosition);
            listView.setItemChecked(selectedItemPosition, true);
        }

        public void setFenceView(View view) {
            this.mFenceView = view;
            super.setDecorView(view);
        }

        public View getFenceView() {
            View view = this.mFenceView;
            return view != null ? view : Spinner.this.getRootView();
        }

        boolean isVisibleToUser(View view) {
            return ViewCompat.isAttachedToWindow(view) && view.getGlobalVisibleRect(this.mVisibleRect);
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setHorizontalOriginalOffset(int i) {
            this.mOriginalHorizontalOffset = i;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public int getHorizontalOriginalOffset() {
            return this.mOriginalHorizontalOffset;
        }

        @Override // miuix.popupwidget.widget.PopupWindow, miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setDropDownGravity(int i) {
            super.setDropDownGravity(i);
        }

        @Override // miuix.popupwidget.widget.PopupWindow, miuix.appcompat.widget.Spinner.SpinnerPopup
        public void enableHideSoftInput(boolean z) {
            super.enableHideSoftInput(z);
        }
    }

    private static class SpinnerCheckedProvider implements SpinnerCheckableArrayAdapter.CheckedStateProvider {
        private Spinner mSpinner;

        public SpinnerCheckedProvider(Spinner spinner) {
            this.mSpinner = spinner;
        }

        @Override // miuix.appcompat.internal.adapter.SpinnerCheckableArrayAdapter.CheckedStateProvider
        public boolean isChecked(int i) {
            return this.mSpinner.getSelectedItemPosition() == i;
        }
    }
}
