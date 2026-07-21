package com.android.deskclock.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.Util;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import miuix.androidbasewidget.widget.SeekBar;
import miuix.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes.dex */
public class AlarmVolumeDialogFragment extends DialogFragment {
    private static final String TAG = "DC:AlarmVolumeDialogFragment";
    private AlertDialog mAlertDialog;
    private SeekBarVolumizer mSeekBarVolumizer;
    private FrameLayout mView;
    private SeekBar mVolumeSeekBar;

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.widget_preference_seekbar, (ViewGroup) null);
        this.mView = frameLayout;
        this.mVolumeSeekBar = (SeekBar) frameLayout.findViewById(R.id.seek_bar_clock);
        if (PadAdapterUtil.IS_PAD) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mVolumeSeekBar.getLayoutParams();
            layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.dialog_seek_bar_margin_bottom);
            this.mVolumeSeekBar.setLayoutParams(layoutParams);
        }
        this.mSeekBarVolumizer = new SeekBarVolumizer(getContext(), this.mVolumeSeekBar, this);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder view = new AlertDialog.Builder(getActivity()).setTitle(R.string.alert_volume).setView(this.mView);
        view.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.view.AlarmVolumeDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                AlarmVolumeDialogFragment.this.dismiss();
            }
        });
        view.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.view.AlarmVolumeDialogFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (AlarmVolumeDialogFragment.this.mSeekBarVolumizer != null) {
                    AlarmVolumeDialogFragment.this.mSeekBarVolumizer.stopSample();
                    AlarmVolumeDialogFragment.this.mSeekBarVolumizer.revertVolume();
                }
                AlarmVolumeDialogFragment.this.dismiss();
            }
        });
        AlertDialog alertDialogCreate = view.create();
        this.mAlertDialog = alertDialogCreate;
        alertDialogCreate.setCanceledOnTouchOutside(true);
        return this.mAlertDialog;
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        SeekBarVolumizer seekBarVolumizer = this.mSeekBarVolumizer;
        if (seekBarVolumizer != null) {
            seekBarVolumizer.stopSample();
            this.mSeekBarVolumizer.revertVolume();
        }
        dismiss();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void show(FragmentManager fragmentManager, String str) {
        AlarmVolumeDialogFragment alarmVolumeDialogFragment = (AlarmVolumeDialogFragment) fragmentManager.findFragmentByTag(str);
        FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
        if (alarmVolumeDialogFragment != null) {
            alarmVolumeDialogFragment.dismissAllowingStateLoss();
        }
        fragmentTransactionBeginTransaction.add(this, str);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        SeekBarVolumizer seekBarVolumizer = this.mSeekBarVolumizer;
        if (seekBarVolumizer != null) {
            seekBarVolumizer.release();
        }
        super.onDismiss(dialogInterface);
    }

    protected void onSampleStarting(SeekBarVolumizer seekBarVolumizer) {
        SeekBarVolumizer seekBarVolumizer2 = this.mSeekBarVolumizer;
        if (seekBarVolumizer2 == null || seekBarVolumizer == seekBarVolumizer2) {
            return;
        }
        seekBarVolumizer2.stopSample();
    }

    public static class SeekBarVolumizer implements android.widget.SeekBar.OnSeekBarChangeListener, Handler.Callback {
        private static final int CHECK_RINGTONE_PLAYBACK_DELAY_MS = 1000;
        private static final int MSG_SET_STREAM_VOLUME = 0;
        private static final int MSG_START_SAMPLE = 1;
        private static final int MSG_STOP_SAMPLE = 2;
        private AudioManager mAudioManager;
        private Handler mHandler;
        private HandlerThread mHandlerThread;
        private int mOriginalVolume;
        private WeakReference<AlarmVolumeDialogFragment> mReference;
        private volatile Ringtone mRingtone;
        private SeekBar mSeekBar;
        private int mStreamType = 4;
        private int mMinVolume = 0;
        private int mLastProgress = -1;

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
        }

        public SeekBarVolumizer(Context context, SeekBar seekBar, AlarmVolumeDialogFragment alarmVolumeDialogFragment) {
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
            this.mSeekBar = seekBar;
            HandlerThread handlerThread = new HandlerThread("VolumePreference.CallbackHandler");
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            this.mHandler = new Handler(this.mHandlerThread.getLooper(), this);
            initSeekBar(seekBar);
            this.mReference = new WeakReference<>(alarmVolumeDialogFragment);
        }

        private void initSeekBar(final SeekBar seekBar) {
            seekBar.setMax(this.mAudioManager.getStreamMaxVolume(this.mStreamType));
            if (Build.VERSION.SDK_INT >= 28) {
                this.mMinVolume = this.mAudioManager.getStreamMinVolume(this.mStreamType);
            }
            seekBar.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.view.AlarmVolumeDialogFragment.SeekBarVolumizer.1
                int mLastXIntercept = 0;
                int mLastYIntercept = 0;
                boolean intercepted = false;

                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();
                    this.intercepted = false;
                    if (motionEvent.getAction() == 2) {
                        int i = x - this.mLastXIntercept;
                        if (seekBar.getProgress() == SeekBarVolumizer.this.mMinVolume) {
                            if (Util.isRtl() && i >= 0) {
                                this.intercepted = true;
                            } else if (!Util.isRtl() && i <= 0) {
                                this.intercepted = true;
                            }
                        }
                    }
                    this.mLastXIntercept = x;
                    this.mLastYIntercept = y;
                    return this.intercepted;
                }
            });
            int streamVolume = this.mAudioManager.getStreamVolume(this.mStreamType);
            this.mOriginalVolume = streamVolume;
            seekBar.setProgress(streamVolume);
            seekBar.setOnSeekBarChangeListener(this);
            Executors.newSingleThreadExecutor().execute(new Runnable() { // from class: com.android.deskclock.view.AlarmVolumeDialogFragment$SeekBarVolumizer$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m102x82dd60b();
                }
            });
        }

        /* JADX INFO: renamed from: lambda$initSeekBar$0$com-android-deskclock-view-AlarmVolumeDialogFragment$SeekBarVolumizer, reason: not valid java name */
        /* synthetic */ void m102x82dd60b() {
            Ringtone ringtone = RingtoneManager.getRingtone(DeskClockApp.getAppDEContext(), Settings.System.DEFAULT_ALARM_ALERT_URI);
            if (ringtone != null) {
                ringtone.setStreamType(this.mStreamType);
            }
            this.mRingtone = ringtone;
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                this.mAudioManager.setStreamVolume(this.mStreamType, this.mLastProgress, 0);
            } else if (i == 1) {
                onStartSample();
            } else if (i == 2) {
                onStopSample();
                this.mHandlerThread.quitSafely();
            } else {
                Log.e("VolumePreference", "invalid SeekBarVolumizer message: " + message.what);
            }
            return true;
        }

        private void postStartSample() {
            this.mHandler.removeMessages(1);
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1), isSamplePlaying() ? 1000L : 0L);
        }

        private void onStartSample() {
            WeakReference<AlarmVolumeDialogFragment> weakReference;
            AlarmVolumeDialogFragment alarmVolumeDialogFragment;
            if (isSamplePlaying() || (weakReference = this.mReference) == null || (alarmVolumeDialogFragment = weakReference.get()) == null) {
                return;
            }
            alarmVolumeDialogFragment.onSampleStarting(this);
            if (this.mRingtone != null) {
                this.mRingtone.play();
            }
        }

        private void postStopSample() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(2));
        }

        private void onStopSample() {
            if (this.mRingtone != null) {
                this.mRingtone.stop();
            }
        }

        void postSetVolume(int i) {
            this.mLastProgress = i;
            this.mHandler.removeMessages(0);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(0));
        }

        public boolean isSamplePlaying() {
            return this.mRingtone != null && this.mRingtone.isPlaying();
        }

        public void startSample() {
            postStartSample();
        }

        public void stopSample() {
            postStopSample();
        }

        public void release() {
            postStopSample();
            this.mSeekBar.setOnSeekBarChangeListener(null);
        }

        public void revertVolume() {
            this.mAudioManager.setStreamVolume(this.mStreamType, this.mOriginalVolume, 0);
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(android.widget.SeekBar seekBar, int i, boolean z) {
            if (z) {
                int i2 = this.mMinVolume;
                if (i < i2) {
                    i = i2;
                }
                this.mSeekBar.setProgress(i);
                postSetVolume(i);
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            postStartSample();
        }
    }
}
