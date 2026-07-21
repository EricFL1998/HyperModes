package com.android.deskclock.timer;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.widget.CommonTimerTextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.core.util.MiuixUIUtils;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes.dex */
public class TimerHistoryAdapter extends RecyclerView.Adapter {
    private static final long CLICK_INTERVAL = 500;
    private static String TAG = "DC:TimerHistoryAdapter";
    private static final int TYPE_ITEM = 0;
    public static int mTouchId = -1;
    private Context mContext;
    private PopupWindow mCurrentPopupWindow;
    private onDataListChangedListener mDataListChangedListener;
    private OnItemClickListener mItemClickListener;
    private List<TimerModel.TimerBean> mTimerHistoryDataList;
    private int mMaxTimerItemCount = 6;
    private long mLastPopupTime = 0;
    private boolean mSupportLinearMotorVibrate = Util.isSupportLinearMotorVibrate();
    private int mFontLevel = MiuixUIUtils.getFontLevel(DeskClockApp.getAppContext());

    public interface OnItemClickListener {
        void onTimerHistoryItemClick(int i, int i2);
    }

    public interface onDataListChangedListener {
        void onDataListChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return 0;
    }

    public TimerHistoryAdapter(Context context) {
        this.mContext = context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new TimerHistoryViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.timer_history_item, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (!(viewHolder instanceof TimerHistoryViewHolder) || this.mTimerHistoryDataList == null) {
            return;
        }
        final TimerHistoryViewHolder timerHistoryViewHolder = (TimerHistoryViewHolder) viewHolder;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) timerHistoryViewHolder.itemView.getLayoutParams();
        if (this.mFontLevel == 2) {
            if (this.mTimerHistoryDataList.size() == 1) {
                layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_one_item_width);
            } else {
                layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_item_width_large);
            }
        } else if (PadAdapterUtil.IS_PAD) {
            if (this.mTimerHistoryDataList.size() == 1 || this.mTimerHistoryDataList.size() == 2) {
                layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_one_item_width);
            } else {
                layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_item_width_large);
            }
        } else if (Util.isFoldDevice(this.mContext) && Util.isWideMode(this.mContext)) {
            if (this.mTimerHistoryDataList.size() == 1) {
                layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_one_item_width);
            } else if (this.mTimerHistoryDataList.size() == 2) {
                layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_item_width_large);
            } else {
                layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_item_width_large);
            }
        } else if (this.mTimerHistoryDataList.size() == 1) {
            layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_one_item_width);
        } else if (this.mTimerHistoryDataList.size() == 2) {
            layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_two_item_width);
        } else {
            layoutParams.width = (int) this.mContext.getResources().getDimension(R.dimen.timer_history_more_item_width);
        }
        timerHistoryViewHolder.itemView.setLayoutParams(layoutParams);
        timerHistoryViewHolder.mTimeView.setValue(this.mTimerHistoryDataList.get(i).seconds);
        timerHistoryViewHolder.mTimeView.setContentDescription(Util.formatTimeWithSPAN(this.mTimerHistoryDataList.get(i).seconds * 1000).toString());
        timerHistoryViewHolder.mTimeView.requestLayout();
        setFolmeAnim(timerHistoryViewHolder.mContainer);
        if (i == mTouchId) {
            viewHolder.itemView.setBackgroundResource(R.drawable.timer_history_item_touch_background);
            timerHistoryViewHolder.mTimeView.setTextColor(R.color.common_timer_item_num_touch_color);
        } else {
            viewHolder.itemView.setBackgroundResource(R.drawable.timer_history_item_background);
            timerHistoryViewHolder.mTimeView.setTextColor(R.color.common_timer_item_num_color);
        }
        timerHistoryViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.timer.TimerHistoryAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (TimerHistoryAdapter.this.mItemClickListener != null && i < TimerHistoryAdapter.this.mTimerHistoryDataList.size()) {
                    TimerHistoryAdapter.this.mItemClickListener.onTimerHistoryItemClick(i, ((TimerModel.TimerBean) TimerHistoryAdapter.this.mTimerHistoryDataList.get(i)).seconds);
                }
                if (TimerHistoryAdapter.this.mSupportLinearMotorVibrate) {
                    HapticCompat.performHapticFeedback(timerHistoryViewHolder.itemView, HapticFeedbackConstants.MIUI_MESH_NORMAL);
                }
            }
        });
        timerHistoryViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.deskclock.timer.TimerHistoryAdapter.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                TimerHistoryAdapter.this.showDeletePopup(timerHistoryViewHolder.itemView, i);
                return true;
            }
        });
        timerHistoryViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.timer.TimerHistoryAdapter.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (i == TimerHistoryAdapter.mTouchId) {
                    if (action == 0) {
                        view.setBackgroundResource(R.drawable.timer_history_item_touch_background_delete);
                        return false;
                    }
                    if (action != 1 && action != 3) {
                        return false;
                    }
                    view.setBackgroundResource(R.drawable.timer_history_item_touch_background);
                    return false;
                }
                if (action == 0) {
                    view.setBackgroundResource(R.drawable.timer_history_item_touch_background);
                    return false;
                }
                if (action != 1 && action != 3) {
                    return false;
                }
                view.setBackgroundResource(R.drawable.timer_history_item_background);
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDeletePopup(View view, final int i) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - this.mLastPopupTime < CLICK_INTERVAL) {
            return;
        }
        this.mLastPopupTime = jCurrentTimeMillis;
        PopupWindow popupWindow = this.mCurrentPopupWindow;
        if (popupWindow != null && popupWindow.isShowing()) {
            this.mCurrentPopupWindow.dismiss();
        }
        View viewInflate = LayoutInflater.from(this.mContext).inflate(R.layout.timer_history_item_popup, (ViewGroup) null);
        Button button = (Button) viewInflate.findViewById(R.id.deleteButton);
        final PopupWindow popupWindow2 = new PopupWindow(viewInflate, -2, -2);
        this.mCurrentPopupWindow = popupWindow2;
        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: com.android.deskclock.timer.TimerHistoryAdapter.4
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                TimerHistoryAdapter.this.mCurrentPopupWindow = null;
            }
        });
        popupWindow2.setOutsideTouchable(true);
        popupWindow2.setFocusable(true);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.timer.TimerHistoryAdapter.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (i == TimerHistoryAdapter.mTouchId) {
                    TimerHistoryAdapter.mTouchId = -1;
                } else if (i < TimerHistoryAdapter.mTouchId) {
                    TimerHistoryAdapter.mTouchId--;
                }
                TimerHistoryAdapter.this.removeItem(i);
                popupWindow2.dismiss();
            }
        });
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        popupWindow2.showAtLocation(view, 0, iArr[0] + ((view.getWidth() / 2) - (((int) this.mContext.getResources().getDimension(R.dimen.delete_button_width)) / 2)), iArr[1] + (-((int) this.mContext.getResources().getDimension(R.dimen.timer_history_delete_yOffset))));
    }

    public void dismissPopup() {
        PopupWindow popupWindow = this.mCurrentPopupWindow;
        if (popupWindow == null || !popupWindow.isShowing()) {
            return;
        }
        this.mCurrentPopupWindow.dismiss();
        this.mCurrentPopupWindow = null;
    }

    public void removeItem(int i) {
        if (i < 0 || i >= this.mTimerHistoryDataList.size()) {
            return;
        }
        this.mTimerHistoryDataList.remove(i);
        this.mContext.getContentResolver().delete(TimerHistoryTable.CONTENT_URI, null, null);
        ArrayList<Integer> minsList = getMinsList();
        if (minsList != null) {
            Iterator<Integer> it = minsList.iterator();
            while (it.hasNext()) {
                int iIntValue = it.next().intValue();
                ContentValues contentValues = new ContentValues();
                contentValues.put("seconds", Integer.valueOf(iIntValue));
                this.mContext.getContentResolver().insert(TimerHistoryTable.CONTENT_URI, contentValues);
            }
        }
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, this.mTimerHistoryDataList.size());
        onDataListChangedListener ondatalistchangedlistener = this.mDataListChangedListener;
        if (ondatalistchangedlistener != null) {
            ondatalistchangedlistener.onDataListChanged();
        }
    }

    public void setFolmeAnim(View view) {
        if (MiuiSdk.isLiteMode()) {
            return;
        }
        MiuiFolme.touch(view);
    }

    public void initData(List<TimerModel.TimerBean> list) {
        this.mTimerHistoryDataList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnDataListChangedListener(onDataListChangedListener ondatalistchangedlistener) {
        this.mDataListChangedListener = ondatalistchangedlistener;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<TimerModel.TimerBean> list = this.mTimerHistoryDataList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public ArrayList<Integer> getMinsList() {
        if (this.mTimerHistoryDataList == null) {
            return null;
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (TimerModel.TimerBean timerBean : this.mTimerHistoryDataList) {
            if (timerBean.id != -1) {
                arrayList.add(Integer.valueOf(timerBean.seconds));
            }
        }
        return arrayList;
    }

    public void addTimerHistory(int i) {
        ArrayList<Integer> minsList = getMinsList();
        if (minsList != null) {
            Iterator<Integer> it = minsList.iterator();
            while (it.hasNext()) {
                if (i == it.next().intValue()) {
                    Log.i(TAG, "addTimerHistory seconds have same " + i);
                    return;
                }
            }
        }
        if (minsList != null && minsList.size() < this.mMaxTimerItemCount) {
            minsList.add(0, Integer.valueOf(i));
        } else if (minsList != null) {
            minsList.remove(minsList.size() - 1);
            minsList.add(0, Integer.valueOf(i));
        }
        this.mContext.getContentResolver().delete(TimerHistoryTable.CONTENT_URI, null, null);
        if (minsList != null) {
            Iterator<Integer> it2 = minsList.iterator();
            while (it2.hasNext()) {
                int iIntValue = it2.next().intValue();
                ContentValues contentValues = new ContentValues();
                contentValues.put("seconds", Integer.valueOf(iIntValue));
                this.mContext.getContentResolver().insert(TimerHistoryTable.CONTENT_URI, contentValues);
            }
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }

    private class TimerHistoryViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mContainer;
        public CommonTimerTextView mTimeView;

        public TimerHistoryViewHolder(View view) {
            super(view);
            this.mTimeView = (CommonTimerTextView) view.findViewById(R.id.time_desc);
            this.mContainer = (LinearLayout) view.findViewById(R.id.container);
        }
    }
}
