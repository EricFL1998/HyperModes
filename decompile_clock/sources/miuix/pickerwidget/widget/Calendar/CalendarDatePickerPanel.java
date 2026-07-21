package miuix.pickerwidget.widget.Calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes3.dex */
class CalendarDatePickerPanel extends LinearLayout {
    private int mMeasuredPanelHeight;
    private boolean mWrapContent;

    public CalendarDatePickerPanel(Context context) {
        super(context);
        this.mMeasuredPanelHeight = 0;
    }

    public CalendarDatePickerPanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMeasuredPanelHeight = 0;
    }

    public CalendarDatePickerPanel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMeasuredPanelHeight = 0;
    }

    public CalendarDatePickerPanel(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mMeasuredPanelHeight = 0;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mWrapContent) {
            return;
        }
        if (this.mMeasuredPanelHeight == 0) {
            this.mMeasuredPanelHeight = getMeasuredHeight();
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(i), this.mMeasuredPanelHeight);
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mWrapContent) {
            return;
        }
        View childAt = getChildAt(0);
        View childAt2 = getChildAt(3);
        if (childAt == null || childAt2 == null || childAt2.getVisibility() != 0) {
            return;
        }
        int measuredHeight = getMeasuredHeight();
        int measuredHeight2 = childAt.getMeasuredHeight();
        int measuredHeight3 = childAt2.getMeasuredHeight();
        int i5 = i4 - (((measuredHeight - measuredHeight2) - measuredHeight3) >> 1);
        childAt2.layout(i, i5 - measuredHeight3, i3, i5);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.height = this.mMeasuredPanelHeight;
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.mMeasuredPanelHeight = savedState.height;
        } else {
            Log.w("CalendarDatePickerPanel", "Wrong state class, expecting SavedState! This usually happens when two views of different type have the same id in the same hierarchy.");
            super.onRestoreInstanceState(parcelable);
        }
    }

    void setWrapContent(boolean z) {
        if (this.mWrapContent != z) {
            this.mWrapContent = z;
            requestLayout();
        }
    }

    private static class SavedState extends View.BaseSavedState {
        public static final Parcelable.ClassLoaderCreator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: miuix.pickerwidget.widget.Calendar.CalendarDatePickerPanel.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }
        };
        int height;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        SavedState(Parcel parcel) {
            super(parcel);
            this.height = parcel.readInt();
        }

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.height = parcel.readInt();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.height);
        }
    }
}
