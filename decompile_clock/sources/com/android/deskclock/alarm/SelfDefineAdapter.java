package com.android.deskclock.alarm;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.R;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import miuix.recyclerview.card.CardGroupAdapter;

/* JADX INFO: loaded from: classes.dex */
public class SelfDefineAdapter extends CardGroupAdapter {
    private static int mId;
    String[] dayList;
    private SparseIntArray mCheckedIdStates;
    private Activity mContext;
    private boolean mIsInitSelfDefineDialog;
    private boolean[] mLastCheckedItems;
    private OnWeekDayItemClickListener mOnWeekDayItemClickListener;
    private String[] weekdayValue;
    private String[] weekdayValueCN;
    private final ArrayList<DataBean> dataList = new ArrayList<>();
    String[] weekdays = new DateFormatSymbols().getWeekdays();

    public interface OnWeekDayItemClickListener {
        void onWeekDayItemClick(int i, boolean z);
    }

    @Override // miuix.recyclerview.card.CardGroupAdapter
    public void setHasStableIds() {
    }

    public SelfDefineAdapter(Context context, boolean[] zArr) {
        String[] shortWeekdays = new DateFormatSymbols().getShortWeekdays();
        this.dayList = shortWeekdays;
        this.mIsInitSelfDefineDialog = false;
        String[] strArr = this.weekdays;
        this.weekdayValue = new String[]{strArr[2], strArr[3], strArr[4], strArr[5], strArr[6], strArr[7], strArr[1]};
        this.weekdayValueCN = new String[]{shortWeekdays[2], shortWeekdays[3], shortWeekdays[4], shortWeekdays[5], shortWeekdays[6], shortWeekdays[7], shortWeekdays[1]};
        this.mContext = (Activity) context;
        this.mCheckedIdStates = new SparseIntArray();
        if (Locale.getDefault().getLanguage().contains("zh")) {
            this.weekdayValue = this.weekdayValueCN;
        }
    }

    @Override // miuix.recyclerview.card.CardGroupAdapter
    public int getItemViewGroup(int i) {
        return this.dataList.get(i).groupId;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WeekDayViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.self_define_weekday_item, viewGroup, false));
    }

    @Override // miuix.recyclerview.card.CardGroupAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        if (viewHolder instanceof WeekDayViewHolder) {
            bindWeekDayViewHolder((WeekDayViewHolder) viewHolder, i);
        }
    }

    private void bindWeekDayViewHolder(WeekDayViewHolder weekDayViewHolder, int i) {
        weekDayViewHolder.position = i;
        weekDayViewHolder.mWeekDayDesc.setText(this.weekdayValue[i]);
        Log.d(CardGroupAdapter.TAG, "bindWeekDayViewHolder: " + this.mIsInitSelfDefineDialog);
        weekDayViewHolder.mWeekDayDesc.setTextColor(this.mContext.getColor(R.color.alarm_repeat_text_normal_color));
        if (this.mIsInitSelfDefineDialog) {
            weekDayViewHolder.mMultiCheckBox.setChecked(this.mLastCheckedItems[i]);
        } else {
            weekDayViewHolder.mMultiCheckBox.setChecked(this.mCheckedIdStates.indexOfKey(mId) >= 0);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.dataList.size();
    }

    public void setData(ArrayList<DataBean> arrayList) {
        this.dataList.clear();
        this.dataList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void setItemChecked(int i, boolean z) {
        int itemId = (int) getItemId(i);
        if (z != (this.mCheckedIdStates.indexOfKey(itemId) >= 0)) {
            if (z) {
                this.mCheckedIdStates.put(itemId, i);
            } else {
                this.mCheckedIdStates.delete(itemId);
            }
        }
        this.mIsInitSelfDefineDialog = false;
        mId = itemId;
        notifyItemChanged(i);
    }

    public int[] getCheckedItemIds() {
        int size = this.mCheckedIdStates.size();
        int[] iArr = new int[this.mCheckedIdStates.size()];
        for (int i = 0; i < size; i++) {
            iArr[i] = this.mCheckedIdStates.keyAt(i);
        }
        return iArr;
    }

    public void setLastCheckedSelfDefineItem(boolean[] zArr, boolean z) {
        this.mLastCheckedItems = zArr;
        this.mIsInitSelfDefineDialog = z;
    }

    private class WeekDayViewHolder extends RecyclerView.ViewHolder {
        private CheckBox mMultiCheckBox;
        private TextView mWeekDayDesc;
        private RelativeLayout mWeekDayItem;
        private int position;

        public WeekDayViewHolder(View view) {
            super(view);
            this.mWeekDayItem = (RelativeLayout) view.findViewById(R.id.week_day_item);
            this.mWeekDayDesc = (TextView) view.findViewById(R.id.weekday_desc);
            this.mMultiCheckBox = (CheckBox) this.itemView.findViewById(android.R.id.checkbox);
            this.mWeekDayItem.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.alarm.SelfDefineAdapter.WeekDayViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    SelfDefineAdapter.this.mOnWeekDayItemClickListener.onWeekDayItemClick(WeekDayViewHolder.this.position, WeekDayViewHolder.this.mMultiCheckBox.isChecked());
                }
            });
        }
    }

    public void setOnWeekDayItemClickListener(OnWeekDayItemClickListener onWeekDayItemClickListener) {
        this.mOnWeekDayItemClickListener = onWeekDayItemClickListener;
    }
}
