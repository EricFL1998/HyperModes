package com.android.deskclock.worldclock;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.view.FormatClockForLocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class TimeZoneOtherAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private OnStateChangeListener mListener;
    private long mDisplayTimeMillis = System.currentTimeMillis();
    private List<CityObj> mDataList = new ArrayList();

    public interface OnStateChangeListener {
        void onDelete(CityObj cityObj);

        void onMoveUp(CityObj cityObj);
    }

    public TimeZoneOtherAdapter(Context context, RecyclerView recyclerView) {
        this.mContext = context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new TimeZoneOtherVH(LayoutInflater.from(this.mContext).inflate(R.layout.timezone_other_item, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof TimeZoneOtherVH) {
            bindTimezoneOtherVH((TimeZoneOtherVH) viewHolder, i);
        }
    }

    public void bindTimezoneOtherVH(final TimeZoneOtherVH timeZoneOtherVH, int i) {
        timeZoneOtherVH.tzDeleteIv.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.TimeZoneOtherAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int adapterPosition = timeZoneOtherVH.getAdapterPosition();
                if (adapterPosition == -1) {
                    return;
                }
                CityObj cityObj = (CityObj) TimeZoneOtherAdapter.this.mDataList.get(adapterPosition);
                TimeZoneOtherAdapter.this.mDataList.remove(adapterPosition);
                TimeZoneOtherAdapter.this.notifyDataSetChanged();
                TimeZoneOtherAdapter.this.mListener.onDelete(cityObj);
            }
        });
        timeZoneOtherVH.tzMoveUpIv.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.TimeZoneOtherAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int adapterPosition = timeZoneOtherVH.getAdapterPosition();
                if (adapterPosition == -1) {
                    return;
                }
                TimeZoneOtherAdapter.this.mListener.onMoveUp((CityObj) TimeZoneOtherAdapter.this.mDataList.get(adapterPosition));
            }
        });
        timeZoneOtherVH.digitClock.resetTimeFormat();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<CityObj> list = this.mDataList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        List<CityObj> list = this.mDataList;
        if (list == null || i >= list.size()) {
            return -1L;
        }
        return this.mDataList.get(i).id;
    }

    public void initData(List<CityObj> list) {
        this.mDataList.clear();
        this.mDataList.addAll(list);
    }

    public void setTime(long j) {
        this.mDisplayTimeMillis = j;
    }

    public void updateTimeZone(CityObj cityObj, TimeZoneOtherVH timeZoneOtherVH) {
        int i;
        String quantityString;
        TimeZone timeZone = TimeZone.getTimeZone(cityObj.mTimeZone);
        Calendar.getInstance(timeZone).setTimeInMillis(this.mDisplayTimeMillis);
        timeZoneOtherVH.digitClock.updateTime(timeZone, this.mDisplayTimeMillis);
        timeZoneOtherVH.timezoneName.setText(cityObj.mCityName);
        int offset = timeZone.getOffset(Calendar.getInstance().getTimeInMillis()) - TimeZone.getDefault().getOffset(Calendar.getInstance().getTimeInMillis());
        if (offset < 0) {
            offset = -offset;
            i = R.plurals.worldcolock_timezone_gap_slow;
        } else {
            i = R.plurals.worldcolock_timezone_gap_fast;
        }
        long j = offset;
        if (j % AlarmHelper.ARRIVING_ALARM_DURATION == 0) {
            Resources resources = this.mContext.getResources();
            int i2 = (int) (j / AlarmHelper.ARRIVING_ALARM_DURATION);
            quantityString = resources.getQuantityString(i, i2, Integer.valueOf(i2));
        } else {
            float f = offset / 3600000.0f;
            quantityString = this.mContext.getResources().getQuantityString(i, ((int) f) + 1, Float.valueOf(f));
        }
        if (offset == 0) {
            quantityString = this.mContext.getString(R.string.worldcolock_timezone_local);
        }
        timeZoneOtherVH.timezoneGap.setText(quantityString);
    }

    public void addCityData(CityObj cityObj) {
        this.mDataList.add(cityObj);
        notifyDataSetChanged();
    }

    public void addFirstCity(CityObj cityObj) {
        this.mDataList.add(0, cityObj);
        notifyDataSetChanged();
    }

    public void moveUpData(CityObj cityObj) {
        this.mDataList.remove(cityObj);
        notifyDataSetChanged();
    }

    private class TimeZoneOtherVH extends RecyclerView.ViewHolder {
        public FormatClockForLocalTime digitClock;
        public TextView timezoneGap;
        public TextView timezoneName;
        public ImageView tzDeleteIv;
        public ImageView tzMoveUpIv;

        public TimeZoneOtherVH(View view) {
            super(view);
            this.timezoneName = (TextView) view.findViewById(R.id.timezone_name);
            this.timezoneGap = (TextView) view.findViewById(R.id.timezone_gap);
            this.digitClock = (FormatClockForLocalTime) view.findViewById(R.id.timezone_time);
            if (MiuiSdk.isSupportMiUiFont()) {
                MiuiFont.setFont(this.timezoneName, MiuiFont.MI_PRO_MEDIUM);
                MiuiFont.setFont(this.timezoneGap, MiuiFont.MI_PRO_NORMAL);
                MiuiFont.setFont((TextView) this.digitClock.findViewById(R.id.time_display), MiuiFont.MI_TYPE_MONO_MEDIUM);
                MiuiFont.setFont((TextView) this.digitClock.findViewById(R.id.date_display), MiuiFont.MI_PRO_NORMAL);
                MiuiFont.setFont((TextView) this.digitClock.findViewById(R.id.am_pm), MiuiFont.MI_PRO_NORMAL);
            }
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.mListener = onStateChangeListener;
    }

    public List<CityObj> getDataList() {
        return this.mDataList;
    }
}
