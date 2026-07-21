package com.android.deskclock.stopwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AnimationUtils;
import com.android.deskclock.util.TypefaceFactory;
import com.android.deskclock.util.Util;
import java.util.List;
import java.util.Locale;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;
import miuix.view.animation.CubicEaseInOutInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class LapAdapter extends RecyclerView.Adapter {
    private final int ITEM_HEIGHT;
    private Context mContext;
    private List<LapModel.LapBean> mDataList;
    private int mMaxItemCount = 5;
    boolean mNeedAnimateAdapter = false;

    public LapAdapter(Context context) {
        this.mContext = context;
        this.ITEM_HEIGHT = (int) context.getResources().getDimension(R.dimen.stopwatch_item_height);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new LapViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.stopwatch_lap_item, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        List<LapModel.LapBean> list;
        if (!(viewHolder instanceof LapViewHolder) || (list = this.mDataList) == null || i >= list.size()) {
            return;
        }
        LapModel.LapBean lapBean = this.mDataList.get(i);
        LapViewHolder lapViewHolder = (LapViewHolder) viewHolder;
        lapViewHolder.index.setText(lapBean.index);
        lapViewHolder.elapsedTime.setText(lapBean.elapsedTime);
        lapViewHolder.lapElapsedTime.setText("+ " + lapBean.lapElapsedTime);
        if (Locale.getDefault().getLanguage().contains("zh")) {
            lapViewHolder.index.setContentDescription("计次" + lapBean.index);
            long elapsedTime = parseElapsedTime(lapBean.elapsedTime);
            lapViewHolder.elapsedTime.setContentDescription(Util.formatLapItem(this.mContext, elapsedTime, R.array.time) + (elapsedTime % 10 >= 5 ? ((elapsedTime % 1000) / 10) + 1 : (elapsedTime % 1000) / 10));
            long elapsedTime2 = parseElapsedTime(lapBean.lapElapsedTime);
            long j = (elapsedTime2 % 1000) / 10;
            if (elapsedTime2 % 10 >= 5) {
                j++;
            }
            lapViewHolder.lapElapsedTime.setContentDescription("+" + Util.formatLapItem(this.mContext, elapsedTime2, R.array.time) + j);
        }
        if (this.mNeedAnimateAdapter) {
            handleItemAnim(lapViewHolder.itemView, lapViewHolder, i);
            return;
        }
        lapViewHolder.index.setTextColor(this.mContext.getResources().getColor(R.color.stopwatch_flag_index));
        lapViewHolder.lapElapsedTime.setTextColor(this.mContext.getResources().getColor(R.color.stopwatch_flag_index));
        lapViewHolder.elapsedTime.setTextColor(this.mContext.getResources().getColor(R.color.stopwatch_flag_time));
    }

    private long parseElapsedTime(String str) {
        int i;
        int i2;
        int i3;
        String[] strArrSplit = str.replace(".", MethodCodeHelper.IDENTITY_INFO_SEPARATOR).split(MethodCodeHelper.IDENTITY_INFO_SEPARATOR);
        int i4 = 0;
        if (strArrSplit.length == 4) {
            i4 = Integer.parseInt(strArrSplit[0]);
            i = Integer.parseInt(strArrSplit[1]);
            i2 = Integer.parseInt(strArrSplit[2]);
            i3 = Integer.parseInt(strArrSplit[3]);
        } else {
            i = Integer.parseInt(strArrSplit[0]);
            i2 = Integer.parseInt(strArrSplit[1]);
            i3 = Integer.parseInt(strArrSplit[2]);
        }
        return (((long) i4) * AlarmHelper.ARRIVING_ALARM_DURATION) + (((long) i) * 60000) + (((long) i2) * 1000) + ((long) (i3 * 10));
    }

    private void handleItemAnim(View view, LapViewHolder lapViewHolder, int i) {
        if (i == 0) {
            if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
                view.setVisibility(0);
                return;
            } else {
                AnimationUtils.animateAlphaIn(view, 300L, new CubicEaseInOutInterpolator());
                return;
            }
        }
        if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
            view.setTranslationY(-this.ITEM_HEIGHT);
            view.animate().translationY(0.0f).start();
        } else {
            AnimationUtils.animateTranslateY(view, -this.ITEM_HEIGHT, 0.0f, 300L, new CubicEaseInOutInterpolator());
        }
        lapViewHolder.index.setTextColor(this.mContext.getResources().getColor(R.color.stopwatch_flag_index));
        lapViewHolder.lapElapsedTime.setTextColor(this.mContext.getResources().getColor(R.color.stopwatch_flag_index));
        lapViewHolder.elapsedTime.setTextColor(this.mContext.getResources().getColor(R.color.stopwatch_flag_time));
    }

    public void cancelListAnim(View view) {
        if (view == null) {
            return;
        }
        LapViewHolder lapViewHolder = new LapViewHolder(view);
        MiuiFolme.cleanFolme(lapViewHolder.index);
        MiuiFolme.cleanFolme(lapViewHolder.lapElapsedTime);
        MiuiFolme.cleanFolme(lapViewHolder.elapsedTime);
        lapViewHolder.index.setTextColor(this.mContext.getResources().getColor(R.color.stopwatch_flag_index));
        lapViewHolder.lapElapsedTime.setTextColor(this.mContext.getResources().getColor(R.color.stopwatch_flag_index));
        lapViewHolder.elapsedTime.setTextColor(this.mContext.getResources().getColor(R.color.stopwatch_flag_time));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<LapModel.LapBean> list = this.mDataList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void initData(List<LapModel.LapBean> list) {
        this.mDataList = list;
    }

    public void setVisibleCount(int i) {
        this.mMaxItemCount = i;
    }

    public void setNeedAnimate(boolean z) {
        this.mNeedAnimateAdapter = z;
    }

    private class LapViewHolder extends RecyclerView.ViewHolder {
        public final TextView elapsedTime;
        public final TextView index;
        public final TextView lapElapsedTime;
        public final LinearLayout rootView;

        public LapViewHolder(View view) {
            super(view);
            this.rootView = (LinearLayout) view.findViewById(R.id.lapContainer);
            TextView textView = (TextView) view.findViewById(R.id.indexTime);
            this.index = textView;
            TextView textView2 = (TextView) view.findViewById(R.id.elapsedTime);
            this.elapsedTime = textView2;
            TextView textView3 = (TextView) view.findViewById(R.id.lapElapsedTime);
            this.lapElapsedTime = textView3;
            if (MiuiSdk.isSupportMiUiFont()) {
                MiuiFont.setFont(textView, MiuiFont.MI_TYPE_MONO_REGULAR);
                MiuiFont.setFont(textView2, MiuiFont.MI_TYPE_MONO_MEDIUM);
                MiuiFont.setFont(textView3, MiuiFont.MI_TYPE_MONO_REGULAR);
            } else {
                textView.setTypeface(TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_70));
                textView2.setTypeface(TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_70));
                textView3.setTypeface(TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_70));
            }
        }
    }
}
