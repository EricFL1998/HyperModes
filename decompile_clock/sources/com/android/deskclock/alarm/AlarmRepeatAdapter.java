package com.android.deskclock.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.R;
import com.android.deskclock.addition.holiday.HolidayHelper;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.recyclerview.card.CardGroupAdapter;

/* JADX INFO: loaded from: classes.dex */
public class AlarmRepeatAdapter extends CardGroupAdapter {
    private static final int NORMAL_REPEAT_VIEW_TYPE = 0;
    private static final int SELF_DEFINE_VIEW_TYPE = 1;
    private static final int SHIFT_WORK_VIEW_TYPE = 2;
    private static int mCheckedItem;
    private Activity mContext;
    private boolean mIsWakeAlarmRepeat;
    private OnOtherViewItemClickListener mOtherViewItemClickListener;
    private OnSelfDefButtonViewClickListener mSelfDefButtonViewClickListener;
    private OnSelfDefViewItemClickListener mSelfDefViewItemClickListener;
    private OnShiftAlarmItemClickListener mShiftAlarmItemClickListener;
    private String[] weekdayValue = null;
    private String[] repeatType = null;
    private int[] repeatValue = null;
    private final ArrayList<DataBean> dataList = new ArrayList<>();

    public interface OnOtherViewItemClickListener {
        void onOtherViewItemClick(ArrayList<DataBean> arrayList, int i);
    }

    public interface OnSelfDefButtonViewClickListener {
        void onSelfDefButtonViewClick();
    }

    public interface OnSelfDefViewItemClickListener {
        void onSelfDefViewItemClick();
    }

    public interface OnShiftAlarmItemClickListener {
        void onShiftAlarmItemClick();
    }

    @Override // miuix.recyclerview.card.CardGroupAdapter
    public void setHasStableIds() {
    }

    public AlarmRepeatAdapter(Context context) {
        this.mContext = (Activity) context;
    }

    @Override // miuix.recyclerview.card.CardGroupAdapter
    public int getItemViewGroup(int i) {
        return this.dataList.get(i).groupId;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return new OtherViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alarm_repeat_other, viewGroup, false));
        }
        return new SelfDefViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alarm_repeat_self_define, viewGroup, false));
    }

    @Override // miuix.recyclerview.card.CardGroupAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        if (viewHolder instanceof SelfDefViewHolder) {
            bindSelfDefViewHolder((SelfDefViewHolder) viewHolder, i);
        } else if (viewHolder instanceof OtherViewHolder) {
            bindOtherViewHolder((OtherViewHolder) viewHolder, i);
        }
    }

    private void bindOtherViewHolder(OtherViewHolder otherViewHolder, final int i) {
        int i2;
        int i3;
        otherViewHolder.position = i;
        if (HolidayHelper.isHolidayDataInvalid(this.mContext)) {
            i2 = R.string.legal_workday_invalidate_message;
            i3 = R.string.legal_workday_invalidate_message;
        } else {
            i2 = R.string.legal_workday_message;
            i3 = R.string.legal_off_day_message;
        }
        ViewCompat.setAccessibilityDelegate(otherViewHolder.mOtherItem, new AccessibilityDelegateCompat() { // from class: com.android.deskclock.alarm.AlarmRepeatAdapter.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setChecked(AlarmRepeatAdapter.mCheckedItem == i);
            }
        });
        if (mCheckedItem == i) {
            otherViewHolder.mOtherCheckedView.setVisibility(0);
            otherViewHolder.mOtherDescView.setTextColor(this.mContext.getColor(R.color.repeat_checked_visible));
        } else {
            otherViewHolder.mOtherCheckedView.setVisibility(8);
            otherViewHolder.mOtherDescView.setTextColor(this.mContext.getColor(R.color.alarm_repeat_text_normal_color));
        }
        ArrayList<DataBean> arrayList = this.dataList;
        if (arrayList == null || i >= arrayList.size()) {
            return;
        }
        int i4 = this.dataList.get(i).repeatType;
        if (i4 != 0 && i4 != 1) {
            if (i4 == 2) {
                otherViewHolder.mOtherDescView.setText(this.repeatType[i] + this.mContext.getString(i2));
                return;
            } else if (i4 == 3) {
                otherViewHolder.mOtherDescView.setText(this.repeatType[i] + this.mContext.getString(i3));
                return;
            } else if (i4 != 4) {
                return;
            }
        }
        otherViewHolder.mOtherDescView.setText(this.repeatType[i]);
    }

    private void bindSelfDefViewHolder(SelfDefViewHolder selfDefViewHolder, final int i) {
        Activity activity = this.mContext;
        boolean z = true;
        if (activity != null && Util.isTinyScreen(activity)) {
            z = false;
        }
        if (getItemViewType(i) != 2 || !z) {
            selfDefViewHolder.mSelfTextView.setText(R.string.alarm_repeat_self_define);
            selfDefViewHolder.mSelfDefItem.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmRepeatAdapter.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    AlarmRepeatAdapter.this.mSelfDefViewItemClickListener.onSelfDefViewItemClick();
                }
            });
            selfDefViewHolder.mSelfDefButtonView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmRepeatAdapter.6
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    AlarmRepeatAdapter.this.mSelfDefButtonViewClickListener.onSelfDefButtonViewClick();
                }
            });
            ViewCompat.setAccessibilityDelegate(selfDefViewHolder.mSelfDefItem, new AccessibilityDelegateCompat() { // from class: com.android.deskclock.alarm.AlarmRepeatAdapter.7
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    accessibilityNodeInfoCompat.setCheckable(true);
                    accessibilityNodeInfoCompat.setChecked(AlarmRepeatAdapter.mCheckedItem == i);
                }
            });
            if (mCheckedItem == i) {
                selfDefViewHolder.mSelfCheckedView.setVisibility(0);
                selfDefViewHolder.mSelfTextView.setTextColor(this.mContext.getColor(R.color.repeat_checked_visible));
                return;
            } else {
                selfDefViewHolder.mSelfCheckedView.setVisibility(8);
                selfDefViewHolder.mSelfTextView.setTextColor(this.mContext.getColor(R.color.alarm_repeat_text_normal_color));
                return;
            }
        }
        selfDefViewHolder.mSelfTextView.setText(R.string.shift_alarm);
        selfDefViewHolder.mSelfDefItem.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmRepeatAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AlarmRepeatAdapter.this.jumpToShiftAlarmDialog(i);
            }
        });
        selfDefViewHolder.mSelfDefButtonView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmRepeatAdapter.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AlarmRepeatAdapter.this.jumpToShiftAlarmDialog(i);
            }
        });
        ViewCompat.setAccessibilityDelegate(selfDefViewHolder.mSelfDefItem, new AccessibilityDelegateCompat() { // from class: com.android.deskclock.alarm.AlarmRepeatAdapter.4
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setChecked(AlarmRepeatAdapter.mCheckedItem == i);
            }
        });
        if (mCheckedItem == i) {
            selfDefViewHolder.mSelfCheckedView.setVisibility(0);
            selfDefViewHolder.mSelfTextView.setTextColor(this.mContext.getColor(R.color.repeat_checked_visible));
        } else {
            selfDefViewHolder.mSelfCheckedView.setVisibility(8);
            selfDefViewHolder.mSelfTextView.setTextColor(this.mContext.getColor(R.color.alarm_repeat_text_normal_color));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void jumpToShiftAlarmDialog(int i) {
        this.mShiftAlarmItemClickListener.onShiftAlarmItemClick();
        setShiftItemChecked(i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        int size = this.dataList.size();
        Activity activity = this.mContext;
        if (activity == null || !Util.isTinyScreen(activity) || size <= 0) {
            return size;
        }
        Iterator<DataBean> it = this.dataList.iterator();
        while (it.hasNext()) {
            if (it.next().repeatType == 6) {
                return size - 1;
            }
        }
        return size;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        ArrayList<DataBean> arrayList = this.dataList;
        if (arrayList != null && i < arrayList.size()) {
            int i2 = this.dataList.get(i).repeatType;
            Log.d(CardGroupAdapter.TAG, "repeatType is " + i2);
            if (i2 == 0 || i2 == 1 || i2 == 2 || i2 == 3 || i2 == 4) {
                return 0;
            }
            if (i2 == 6) {
                return 2;
            }
            if (i2 == 7) {
                return 1;
            }
        }
        Log.d(CardGroupAdapter.TAG, "not match the repeatType! the position is " + i);
        return 0;
    }

    public void setData(ArrayList<DataBean> arrayList, boolean z) {
        if (Util.isInternational()) {
            String[] stringArray = this.mContext.getResources().getStringArray(R.array.alarm_repeat_type_no_workdays);
            this.repeatType = stringArray;
            if (z) {
                this.repeatType = new String[]{stringArray[1], stringArray[2]};
            }
        } else {
            String[] stringArray2 = this.mContext.getResources().getStringArray(R.array.alarm_repeat_type);
            this.repeatType = stringArray2;
            if (z) {
                this.repeatType = new String[]{stringArray2[1], stringArray2[2], stringArray2[3], stringArray2[4]};
            }
        }
        this.mIsWakeAlarmRepeat = z;
        this.dataList.clear();
        this.dataList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void setShiftItemChecked(int i) {
        int i2 = mCheckedItem;
        mCheckedItem = i;
        notifyItemChanged(i2);
        notifyItemChanged(i);
    }

    public void setRepeatItemChecked(int i) {
        int i2;
        if (!this.mIsWakeAlarmRepeat) {
            Resources resources = this.mContext.getResources();
            if (Util.isInternational()) {
                i2 = Util.isTinyScreen(this.mContext) ? R.array.alarm_repeat_type_no_workdays_values_isTinyScreen : R.array.alarm_repeat_type_no_workdays_values;
            } else {
                i2 = Util.isTinyScreen(this.mContext) ? R.array.alarm_repeat_type_values_isTinyScreen : R.array.alarm_repeat_type_values;
            }
            this.repeatValue = resources.getIntArray(i2);
        } else {
            this.repeatValue = this.mContext.getResources().getIntArray(Util.isInternational() ? R.array.not_alarm_repeat_type_no_workdays_values : R.array.not_alarm_repeat_type_values);
        }
        int i3 = 0;
        while (true) {
            int[] iArr = this.repeatValue;
            if (i3 >= iArr.length) {
                i3 = -1;
                break;
            } else if (i == iArr[i3]) {
                break;
            } else {
                i3++;
            }
        }
        mCheckedItem = i3;
        notifyDataSetChanged();
    }

    private class SelfDefViewHolder extends RecyclerView.ViewHolder {
        private ImageView mSelfCheckedView;
        private ImageButton mSelfDefButtonView;
        private RelativeLayout mSelfDefItem;
        private TextView mSelfTextView;

        public SelfDefViewHolder(View view) {
            super(view);
            this.mSelfDefItem = (RelativeLayout) view.findViewById(R.id.self_def_item);
            this.mSelfDefButtonView = (ImageButton) view.findViewById(R.id.self_define_image_button);
            this.mSelfTextView = (TextView) view.findViewById(R.id.self_define_desc);
            this.mSelfCheckedView = (ImageView) view.findViewById(R.id.self_define_checked);
        }
    }

    private class OtherViewHolder extends RecyclerView.ViewHolder {
        private ImageView mOtherCheckedView;
        private TextView mOtherDescView;
        private RelativeLayout mOtherItem;
        public int position;

        public OtherViewHolder(View view) {
            super(view);
            this.mOtherItem = (RelativeLayout) view.findViewById(R.id.other_item);
            this.mOtherDescView = (TextView) view.findViewById(R.id.other_desc);
            this.mOtherCheckedView = (ImageView) view.findViewById(R.id.other_checked);
            this.mOtherItem.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.AlarmRepeatAdapter.OtherViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    AlarmRepeatAdapter.this.mOtherViewItemClickListener.onOtherViewItemClick(AlarmRepeatAdapter.this.dataList, OtherViewHolder.this.position);
                }
            });
        }
    }

    public void setOnOtherViewItemClickListener(OnOtherViewItemClickListener onOtherViewItemClickListener) {
        this.mOtherViewItemClickListener = onOtherViewItemClickListener;
    }

    public void setOnSelfDefViewItemClickListener(OnSelfDefViewItemClickListener onSelfDefViewItemClickListener) {
        this.mSelfDefViewItemClickListener = onSelfDefViewItemClickListener;
    }

    public void setOnShiftAlarmItemClickListener(OnShiftAlarmItemClickListener onShiftAlarmItemClickListener) {
        this.mShiftAlarmItemClickListener = onShiftAlarmItemClickListener;
    }

    public void setOnSelfDefButtonViewClickListener(OnSelfDefButtonViewClickListener onSelfDefButtonViewClickListener) {
        this.mSelfDefButtonViewClickListener = onSelfDefButtonViewClickListener;
    }
}
