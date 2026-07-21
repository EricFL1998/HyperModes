package com.android.deskclock.alarm.lifepost;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;

/* JADX INFO: loaded from: classes.dex */
public class LifePostAdapterOld extends ResourceCursorAdapter {
    @Override // android.widget.CursorAdapter, android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public LifePostAdapterOld(Context context) {
        super(context, R.layout.life_post_wake_up_item, (Cursor) null, 0);
    }

    @Override // android.widget.ResourceCursorAdapter, android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View viewNewView = super.newView(context, cursor, viewGroup);
        viewNewView.setTag(new ViewHolder(viewNewView));
        return viewNewView;
    }

    @Override // android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getPosition();
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        long j = cursor.getLong(1);
        int i = cursor.getInt(2);
        viewHolder.mData.setText(LifePostUtils.getTimeDate(j));
        viewHolder.mTime.setText(LifePostUtils.getTimeInHour(j));
        viewHolder.mPercentage.setText(String.valueOf(i));
        int count = getCount() - position;
        viewHolder.mAcumulateDay.setText(DeskClockApp.getAppDEContext().getResources().getQuantityString(R.plurals.day, count, Integer.valueOf(count)));
        viewHolder.mTopLine.setVisibility(position == 0 ? 4 : 0);
        viewHolder.mBottomLine.setVisibility(position != getCount() - 1 ? 0 : 4);
        if (LifePostUtils.isToday(j)) {
            viewHolder.mData.setTextAppearance(context, R.style.LifePostWakeUpItemDataTextStyle);
            viewHolder.mCircle.setBackgroundResource(R.drawable.wake_up_first_item_circle_ic);
        } else {
            viewHolder.mData.setTextAppearance(context, R.style.LifePostWakeUpItemTitleTextStyle);
            viewHolder.mCircle.setBackgroundResource(R.drawable.wake_up_item_circle_ic);
        }
    }

    private class ViewHolder {
        public TextView mAcumulateDay;
        public ImageView mBottomLine;
        public ImageView mCircle;
        public TextView mData;
        public TextView mPercentage;
        public TextView mTime;
        public ImageView mTopLine;

        public ViewHolder(View view) {
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
