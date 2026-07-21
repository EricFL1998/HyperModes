package com.android.deskclock.alarm.lifepost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LifePostAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<DataModel.DataBean> mDataList;

    public LifePostAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<DataModel.DataBean> list) {
        this.mDataList = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.life_post_wake_up_item, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolder) {
            DataModel.DataBean dataBean = this.mDataList.get(i);
            long j = dataBean.time;
            int i2 = dataBean.percentage;
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            viewHolder2.mData.setText(LifePostUtils.getTimeDate(j));
            viewHolder2.mTime.setText(LifePostUtils.getTimeInHour(j));
            viewHolder2.mPercentage.setText(String.valueOf(i2));
            int itemCount = getItemCount() - i;
            viewHolder2.mAcumulateDay.setText(DeskClockApp.getAppDEContext().getResources().getQuantityString(R.plurals.day, itemCount, Integer.valueOf(itemCount)));
            viewHolder2.mTopLine.setVisibility(i == 0 ? 4 : 0);
            viewHolder2.mBottomLine.setVisibility(i != getItemCount() + (-1) ? 0 : 4);
            if (LifePostUtils.isToday(j)) {
                viewHolder2.mData.setTextAppearance(this.mContext, R.style.LifePostWakeUpItemDataTextStyle);
                viewHolder2.mCircle.setBackgroundResource(R.drawable.wake_up_first_item_circle_ic);
            } else {
                viewHolder2.mData.setTextAppearance(this.mContext, R.style.LifePostWakeUpItemTitleTextStyle);
                viewHolder2.mCircle.setBackgroundResource(R.drawable.wake_up_item_circle_ic);
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<DataModel.DataBean> list = this.mDataList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mAcumulateDay;
        public ImageView mBottomLine;
        public ImageView mCircle;
        public TextView mData;
        public TextView mPercentage;
        public TextView mTime;
        public ImageView mTopLine;

        public ViewHolder(View view) {
            super(view);
            this.mData = (TextView) view.findViewById(R.id.wake_up_data);
            this.mTime = (TextView) view.findViewById(R.id.wake_up_time);
            this.mPercentage = (TextView) view.findViewById(R.id.beat_percentage);
            this.mAcumulateDay = (TextView) view.findViewById(R.id.acumulate_day);
            this.mTopLine = (ImageView) view.findViewById(R.id.top_line);
            this.mBottomLine = (ImageView) view.findViewById(R.id.bottom_line);
            this.mCircle = (ImageView) view.findViewById(R.id.wake_up_circle);
            if (MiuiSdk.isSupportFontAnim()) {
                MiuiFont.setFont(this.mTime, MiuiFont.MI_TYPE_MONO_NORMAL);
                MiuiFont.setFont(this.mPercentage, MiuiFont.MI_TYPE_MONO_NORMAL);
            }
        }
    }
}
