package com.android.deskclock.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.view.PlayView;

/* JADX INFO: loaded from: classes.dex */
public class RingtonePickerItem extends RelativeLayout {
    private RelativeLayout mBg;
    private boolean mChecked;
    private ImageView mIconIv;
    private View mIndicator;
    private TextView mInfoTv;
    private ImageView mLoadingView;
    private PlayView mPlayView;
    private FrameLayout mRootView;

    public RingtonePickerItem(Context context) {
        this(context, null);
    }

    public RingtonePickerItem(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RingtonePickerItem(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mChecked = false;
        LayoutInflater.from(context).inflate(R.layout.set_alarm_choose_ringtone_item, (ViewGroup) this, true);
        this.mRootView = (FrameLayout) findViewById(R.id.item_root_view);
        this.mIconIv = (ImageView) findViewById(R.id.image);
        this.mBg = (RelativeLayout) findViewById(R.id.item_background);
        this.mIndicator = findViewById(R.id.indicator);
        this.mInfoTv = (TextView) findViewById(R.id.info);
        this.mPlayView = (PlayView) findViewById(R.id.play);
        this.mLoadingView = (ImageView) findViewById(R.id.loading);
        setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.settings.RingtonePickerItem.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MiuiFolme.touch(RingtonePickerItem.this, motionEvent);
                return false;
            }
        });
    }

    public void setText(String str) {
        TextView textView = this.mInfoTv;
        if (textView != null) {
            textView.setText(str);
        }
    }

    public void setIconImage(int i) {
        this.mIconIv.setBackgroundResource(i);
    }

    public void setBgImage(int i) {
        this.mBg.setBackgroundResource(i);
    }

    public void setIndicatorVisable(boolean z) {
        if (z) {
            this.mIndicator.setVisibility(0);
        } else {
            this.mIndicator.setVisibility(8);
        }
    }

    public void setChecked(boolean z) {
        this.mChecked = z;
        if (this.mInfoTv == null || this.mIconIv == null) {
            return;
        }
        if (z) {
            this.mRootView.setBackgroundResource(R.drawable.set_alarm_bg_sound_selected);
        } else {
            this.mRootView.setBackground(null);
        }
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public boolean startPlay() {
        PlayView playView = this.mPlayView;
        if (playView != null) {
            if (playView.getVisibility() == 0) {
                return false;
            }
            this.mPlayView.setVisibility(0);
        }
        return true;
    }

    public void stopPlay() {
        PlayView playView = this.mPlayView;
        if (playView != null) {
            playView.setVisibility(8);
        }
    }

    public void startLoading() {
        ImageView imageView = this.mLoadingView;
        if (imageView != null) {
            imageView.setVisibility(0);
        }
    }

    public void stopLoading() {
        ImageView imageView = this.mLoadingView;
        if (imageView != null) {
            imageView.setVisibility(8);
        }
    }
}
