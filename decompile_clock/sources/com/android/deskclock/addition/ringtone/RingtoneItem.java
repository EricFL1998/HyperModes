package com.android.deskclock.addition.ringtone;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.view.PickerPlayView;

/* JADX INFO: loaded from: classes.dex */
public class RingtoneItem extends ConstraintLayout {
    public static final int ALL_RINGTONE_ITEM = 5;
    private static final long DELAY_PLAY = 300;
    public static final int DEW_RINGTONE_ITEM = 2;
    public static final int DREAM_RINGTONE_ITEM = 4;
    public static final int FIREFLY_RINGTONE_ITEM = 3;
    private static final int MODE_BY_HEIGHT = 1;
    private static final int MODE_BY_WIDTH = 0;
    private static final int MODE_NONE = 3;
    public static final int WEATHER_RINGTONE_ITEM = 0;
    public static final int WEEK_RINGTONE_ITEM = 1;
    private static final int WHAT_PLAY = 1;
    private boolean mChecked;
    private Handler mHandler;
    private int mImageRes;
    private ImageView mImageView;
    private boolean mIsVideoPrepared;
    private TextView mMoreFlagView;
    private float mOriginalHeight;
    private float mOriginalWidth;
    private PickerPlayView mPlayView;
    private int mRatioMode;
    private TextView mRingtoneAllView;
    private LinearLayout mRootView;
    private CharSequence mTitle;
    private TextView mTitleTv;
    private int mVideoRes;
    private VideoView mVideoView;

    public RingtoneItem(Context context) {
        this(context, null);
    }

    public RingtoneItem(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RingtoneItem(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mChecked = false;
        this.mImageRes = -1;
        this.mVideoRes = -1;
        this.mIsVideoPrepared = false;
        this.mHandler = new Handler(new Handler.Callback() { // from class: com.android.deskclock.addition.ringtone.RingtoneItem.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                if (message.what != 1) {
                    return false;
                }
                RingtoneItem.this.startVideo();
                return false;
            }
        });
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RingtoneItem, 0, 0);
            this.mImageRes = typedArrayObtainStyledAttributes.getResourceId(1, -1);
            this.mVideoRes = typedArrayObtainStyledAttributes.getResourceId(5, -1);
            this.mOriginalWidth = typedArrayObtainStyledAttributes.getDimension(3, 1.0f);
            this.mOriginalHeight = typedArrayObtainStyledAttributes.getDimension(2, 1.0f);
            this.mRatioMode = typedArrayObtainStyledAttributes.getInt(4, 3);
            typedArrayObtainStyledAttributes.recycle();
        }
        View viewInflate = View.inflate(context, R.layout.ringtone_setting_item, this);
        this.mRootView = (LinearLayout) viewInflate.findViewById(R.id.ringtone_item);
        this.mVideoView = (VideoView) viewInflate.findViewById(R.id.video);
        this.mImageView = (ImageView) viewInflate.findViewById(R.id.image);
        this.mRingtoneAllView = (TextView) viewInflate.findViewById(R.id.image_ringtone_all);
        this.mPlayView = (PickerPlayView) viewInflate.findViewById(R.id.play);
        this.mTitleTv = (TextView) viewInflate.findViewById(R.id.title);
        TextView textView = (TextView) viewInflate.findViewById(R.id.more);
        this.mMoreFlagView = textView;
        MiuiFolme.touch(textView);
        VideoView videoView = this.mVideoView;
        if (videoView != null) {
            videoView.setContentDescription(this.mTitle);
        }
        this.mVideoView.setAudioFocusRequest(0);
        int i2 = this.mVideoRes;
        if (i2 != -1) {
            initVideo(i2);
        } else {
            this.mVideoView.setVisibility(8);
        }
        this.mTitleTv.setText(this.mTitle);
        setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.addition.ringtone.RingtoneItem.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MiuiFolme.touch(RingtoneItem.this, motionEvent);
                return false;
            }
        });
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int i3 = this.mRatioMode;
        if (i3 == 0) {
            i2 = View.MeasureSpec.makeMeasureSpec((int) ((this.mOriginalHeight / this.mOriginalWidth) * View.MeasureSpec.getSize(i)), BasicMeasure.EXACTLY);
        } else if (i3 == 1) {
            i = View.MeasureSpec.makeMeasureSpec((int) ((this.mOriginalWidth / this.mOriginalHeight) * View.MeasureSpec.getSize(i2)), BasicMeasure.EXACTLY);
        }
        super.onMeasure(i, i2);
    }

    public void initVideo(int i) {
        VideoView videoView;
        Uri uri = Uri.parse("android.resource://com.android.deskclock/" + i);
        if (uri == null || (videoView = this.mVideoView) == null) {
            return;
        }
        videoView.setVideoURI(uri);
        this.mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.deskclock.addition.ringtone.RingtoneItem.3
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() { // from class: com.android.deskclock.addition.ringtone.RingtoneItem.3.1
                    @Override // android.media.MediaPlayer.OnInfoListener
                    public boolean onInfo(MediaPlayer mediaPlayer2, int i2, int i3) {
                        if (i2 != 3) {
                            return true;
                        }
                        RingtoneItem.this.mVideoView.setBackgroundColor(0);
                        return true;
                    }
                });
                mediaPlayer.seekTo(0);
                mediaPlayer.setLooping(true);
                RingtoneItem.this.mImageView.setVisibility(8);
                RingtoneItem.this.mIsVideoPrepared = true;
            }
        });
    }

    public void startVideo() {
        VideoView videoView;
        if (this.mVideoRes == -1 || (videoView = this.mVideoView) == null || videoView.isPlaying()) {
            return;
        }
        if (this.mIsVideoPrepared) {
            this.mVideoView.start();
        } else {
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, 300L);
        }
    }

    public void stopVideo() {
        VideoView videoView;
        if (this.mVideoRes == -1 || (videoView = this.mVideoView) == null || !videoView.isPlaying()) {
            return;
        }
        this.mVideoView.pause();
    }

    public void onResume() {
        VideoView videoView;
        this.mIsVideoPrepared = false;
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setVisibility(0);
        }
        if (this.mVideoRes == -1 || this.mImageRes == -1 || (videoView = this.mVideoView) == null) {
            return;
        }
        videoView.setVisibility(0);
        this.mVideoView.setBackgroundResource(this.mImageRes);
        initVideo(this.mVideoRes);
    }

    public void onPause() {
        VideoView videoView;
        this.mIsVideoPrepared = false;
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setVisibility(0);
        }
        if (this.mVideoRes == -1 || this.mImageRes == -1 || (videoView = this.mVideoView) == null) {
            return;
        }
        videoView.stopPlayback();
        this.mVideoView.setOnPreparedListener(null);
        this.mVideoView.setBackgroundResource(this.mImageRes);
    }
}
