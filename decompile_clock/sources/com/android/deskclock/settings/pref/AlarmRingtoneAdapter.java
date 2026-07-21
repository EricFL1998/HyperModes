package com.android.deskclock.settings.pref;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.R;
import com.android.deskclock.addition.ringtone.RingtoneItem;
import com.android.deskclock.settings.AlarmRingtonePickerActivity;
import com.android.deskclock.view.PickerPlayView;

/* JADX INFO: loaded from: classes.dex */
public class AlarmRingtoneAdapter extends RecyclerView.Adapter {
    public static final int ALARM_RINGTONE_ITEM = 6;
    private static int mCheckedId;
    private static boolean mIsShowPlayView;
    private static String mOtherTitle;
    private AlarmRingtonePickerActivity mContext;
    private OnItemClickListener mItemClickListener;
    private OnAllClickListener mListener;
    private OnMoreClickListener mMoreClickListener;
    private static int[] STRING_RES = {R.string.ringtone_weather, R.string.ringtone_week, R.string.ringtone_dew, R.string.ringtone_firefly, R.string.ringtone_dream};
    private static int[] PICKER_RES = {R.drawable.weather_ringtone_bg, R.drawable.week_ringtone_bg, R.drawable.dew_ringtone_bg, R.drawable.firefly_ringtone_bg, R.drawable.dream_ringtone_bg};

    public interface OnAllClickListener {
        void onClick();
    }

    public interface OnItemClickListener {
        void onRingtoneItemClick(View view, int i);
    }

    public interface OnMoreClickListener {
        void onMoreItemClick(View view, int i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return 6;
    }

    public AlarmRingtoneAdapter(Context context) {
        this.mContext = (AlarmRingtonePickerActivity) context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 5) {
            return new MoreRingtonePickerHolder(LayoutInflater.from(this.mContext).inflate(R.layout.alarm_ringtone_item, viewGroup, false));
        }
        return new AlarmRingtonePickerHolder(LayoutInflater.from(this.mContext).inflate(R.layout.alarm_ringtone_item, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof AlarmRingtonePickerHolder) {
            bindRingtonePickerViewHolder((AlarmRingtonePickerHolder) viewHolder, i);
        } else if (viewHolder instanceof MoreRingtonePickerHolder) {
            bindMoreRingtonePickerHolder((MoreRingtonePickerHolder) viewHolder, i);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (i == 5) {
            return 5;
        }
        return super.getItemViewType(i);
    }

    private void bindMoreRingtonePickerHolder(MoreRingtonePickerHolder moreRingtonePickerHolder, int i) {
        moreRingtonePickerHolder.position = i;
        if (mCheckedId == i) {
            moreRingtonePickerHolder.mMoreRingtoneItem.setBackgroundResource(R.drawable.bg_sound_selected);
            moreRingtonePickerHolder.mImageView.setImageDrawable(this.mContext.getDrawable(R.drawable.all_ringtone_bg));
            moreRingtonePickerHolder.mTitleTv.setText(TextUtils.isEmpty(mOtherTitle) ? "" : mOtherTitle);
            moreRingtonePickerHolder.mMoreRingtoneItem.setContentDescription(this.mContext.getString(R.string.ringtone_all) + mOtherTitle + this.mContext.getString(R.string.white_noise_selected));
            moreRingtonePickerHolder.mRingtoneAllView.setVisibility(8);
            moreRingtonePickerHolder.mMoreFlagView.setVisibility(0);
            return;
        }
        moreRingtonePickerHolder.mMoreRingtoneItem.setBackground(null);
        moreRingtonePickerHolder.mMoreRingtoneItem.setContentDescription(this.mContext.getString(R.string.ringtone_all));
        moreRingtonePickerHolder.mImageView.setImageDrawable(this.mContext.getDrawable(R.drawable.all_ringtone_bg));
        if (TextUtils.isEmpty(mOtherTitle)) {
            moreRingtonePickerHolder.mTitleTv.setText("");
            moreRingtonePickerHolder.mRingtoneAllView.setVisibility(0);
            moreRingtonePickerHolder.mMoreFlagView.setVisibility(8);
        } else {
            moreRingtonePickerHolder.mTitleTv.setText(mOtherTitle);
            moreRingtonePickerHolder.mRingtoneAllView.setVisibility(8);
            moreRingtonePickerHolder.mMoreFlagView.setVisibility(0);
        }
    }

    private void bindRingtonePickerViewHolder(AlarmRingtonePickerHolder alarmRingtonePickerHolder, int i) {
        alarmRingtonePickerHolder.position = i;
        if (mCheckedId == i) {
            alarmRingtonePickerHolder.mImageView.setImageDrawable(this.mContext.getDrawable(PICKER_RES[mCheckedId]));
            alarmRingtonePickerHolder.mAlarmRingtoneItem.setBackgroundResource(R.drawable.bg_sound_selected);
            alarmRingtonePickerHolder.mAlarmRingtoneItem.setContentDescription(this.mContext.getString(STRING_RES[mCheckedId]) + this.mContext.getString(R.string.white_noise_selected));
            alarmRingtonePickerHolder.mTitleTv.setText(this.mContext.getString(STRING_RES[mCheckedId]));
            if (mIsShowPlayView) {
                alarmRingtonePickerHolder.mPlayView.setVisibility(0);
                return;
            } else {
                alarmRingtonePickerHolder.mPlayView.setVisibility(8);
                return;
            }
        }
        alarmRingtonePickerHolder.mAlarmRingtoneItem.setBackground(null);
        alarmRingtonePickerHolder.mAlarmRingtoneItem.setContentDescription(this.mContext.getString(STRING_RES[i]));
        alarmRingtonePickerHolder.mImageView.setImageDrawable(this.mContext.getDrawable(PICKER_RES[i]));
        alarmRingtonePickerHolder.mPlayView.setVisibility(8);
        alarmRingtonePickerHolder.mTitleTv.setText(this.mContext.getString(STRING_RES[i]));
    }

    public void setRingtoneItemChecked(int i) {
        mCheckedId = i;
        notifyDataSetChanged();
    }

    public void setPlayView(boolean z) {
        mIsShowPlayView = z;
    }

    public void setOtherItemText(String str) {
        mOtherTitle = str;
    }

    private class AlarmRingtonePickerHolder extends RecyclerView.ViewHolder {
        public RingtoneItem mAlarmRingtoneItem;
        public ImageView mImageView;
        public PickerPlayView mPlayView;
        public TextView mTitleTv;
        public VideoView mVideoView;
        public int position;

        public AlarmRingtonePickerHolder(View view) {
            super(view);
            this.mAlarmRingtoneItem = (RingtoneItem) view.findViewById(R.id.alarm_ringtone_item);
            this.mVideoView = (VideoView) view.findViewById(R.id.video);
            this.mImageView = (ImageView) view.findViewById(R.id.image);
            this.mPlayView = (PickerPlayView) view.findViewById(R.id.play);
            this.mTitleTv = (TextView) view.findViewById(R.id.title);
            this.mAlarmRingtoneItem.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.settings.pref.AlarmRingtoneAdapter.AlarmRingtonePickerHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    AlarmRingtoneAdapter.this.mItemClickListener.onRingtoneItemClick(view2, AlarmRingtonePickerHolder.this.position);
                }
            });
        }
    }

    private class MoreRingtonePickerHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mMoreFlagView;
        public RingtoneItem mMoreRingtoneItem;
        public TextView mRingtoneAllView;
        public TextView mTitleTv;
        public VideoView mVideoView;
        public int position;

        public MoreRingtonePickerHolder(View view) {
            super(view);
            this.mMoreRingtoneItem = (RingtoneItem) view.findViewById(R.id.alarm_ringtone_item);
            this.mVideoView = (VideoView) view.findViewById(R.id.video);
            this.mRingtoneAllView = (TextView) view.findViewById(R.id.image_ringtone_all);
            this.mImageView = (ImageView) view.findViewById(R.id.image);
            this.mTitleTv = (TextView) view.findViewById(R.id.title);
            this.mMoreFlagView = (TextView) view.findViewById(R.id.more);
            this.mMoreRingtoneItem.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.settings.pref.AlarmRingtoneAdapter.MoreRingtonePickerHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    AlarmRingtoneAdapter.this.mMoreClickListener.onMoreItemClick(view2, MoreRingtonePickerHolder.this.position);
                }
            });
            this.mMoreFlagView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.settings.pref.AlarmRingtoneAdapter.MoreRingtonePickerHolder.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    AlarmRingtoneAdapter.this.mListener.onClick();
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.mMoreClickListener = onMoreClickListener;
    }

    public void setListener(OnAllClickListener onAllClickListener) {
        this.mListener = onAllClickListener;
    }
}
