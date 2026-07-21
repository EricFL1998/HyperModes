package com.android.deskclock.addition.other;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import com.android.deskclock.AdditionUtil;
import com.android.deskclock.console.ConsoleAlarm;
import com.android.deskclock.console.IAlarmConsoleV1;
import com.android.deskclock.console.IAlarmConsoleV1Callback;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;

/* JADX INFO: loaded from: classes.dex */
public class MiWearableHelper {
    private static String CONSOLE_ALARM_ALERT = "com.xiaomi.wearable.CONSOLE_ALARM_ALERT";
    public static final String CONSOLE_ALARM_ID_INTENT_EXTRA = "intent.extra.console.alarm.id";
    private static final int CONSOLE_ONALERT = 1;
    private static final int CONSOLE_ONDISMISS = 3;
    private static final int CONSOLE_ONSNOOZE = 2;
    private static final int CONSOLE_REGISTER = 0;
    private static final int CONSOLE_UNREGISTER = 4;
    private static final String HANDLER_THREAD_NAME = "MiWearableThread";
    private static final String TAG = "DC:MiWearableHelper";
    private static final int TYPE_ALL = 0;
    private static final int TYPE_HEALTH = 2;
    private static final int TYPE_WEARABLE = 1;
    private IAlarmConsoleV1Callback mAlarmConsoleV1Callback;
    private boolean mBindHealthSuccess;
    private boolean mBindWearableSuccess;
    private ConsoleAlarm mConsoleAlarm;
    private Context mContext;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private IAlarmConsoleV1 mHealthAlarmConsoleV1;
    private ServiceConnection mHealthServiceConnection;
    private IAlarmConsoleV1 mWearableAlarmConsoleV1;
    private ServiceConnection mWearableServiceConnection;
    private boolean mIsWearableRegisterCallBack = false;
    private boolean mIsHealthRegisterCallBack = false;
    private boolean mIsFinished = false;
    private int mFinishedAction = -1;

    public MiWearableHelper(Context context) {
        this.mContext = context;
        if (this.mHandler == null) {
            HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            this.mHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: com.android.deskclock.addition.other.MiWearableHelper.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    super.handleMessage(message);
                    Log.d(MiWearableHelper.TAG, "handleMessage, action: " + message.what + ", type: " + message.arg1);
                    int i = message.what;
                    if (i == 0) {
                        MiWearableHelper miWearableHelper = MiWearableHelper.this;
                        miWearableHelper.onConsoleAlarmRegister(miWearableHelper.mConsoleAlarm, message.arg1);
                        MiWearableHelper.this.mIsFinished = false;
                        return;
                    }
                    if (i == 1) {
                        MiWearableHelper miWearableHelper2 = MiWearableHelper.this;
                        miWearableHelper2.onConsoleAlarmAlert(miWearableHelper2.mConsoleAlarm, message.arg1);
                        MiWearableHelper.this.mIsFinished = false;
                        return;
                    }
                    if (i == 2) {
                        MiWearableHelper miWearableHelper3 = MiWearableHelper.this;
                        miWearableHelper3.onConsoleAlarmSnooze(miWearableHelper3.mConsoleAlarm);
                        MiWearableHelper.this.onConsoleAlarmUnRegister(0);
                        MiWearableHelper.this.mIsFinished = true;
                        return;
                    }
                    if (i != 3) {
                        if (i != 4) {
                            return;
                        }
                        MiWearableHelper.this.onConsoleAlarmUnRegister(message.arg1);
                    } else {
                        MiWearableHelper miWearableHelper4 = MiWearableHelper.this;
                        miWearableHelper4.onConsoleAlarmDismiss(miWearableHelper4.mConsoleAlarm);
                        MiWearableHelper.this.onConsoleAlarmUnRegister(0);
                        MiWearableHelper.this.mIsFinished = true;
                    }
                }
            };
        }
    }

    public void setCallBack(IAlarmConsoleV1Callback iAlarmConsoleV1Callback) {
        this.mAlarmConsoleV1Callback = iAlarmConsoleV1Callback;
    }

    private void bindWearableService() {
        this.mWearableServiceConnection = new ServiceConnection() { // from class: com.android.deskclock.addition.other.MiWearableHelper.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                try {
                    Log.f(MiWearableHelper.TAG, "onWearableServiceConnected");
                    MiWearableHelper.this.mWearableAlarmConsoleV1 = IAlarmConsoleV1.Stub.asInterface(iBinder);
                    MiWearableHelper.this.handleConsoleAlarm(0, 1);
                    StatHelper.alarmEvent(StatHelper.EVENT_MI_WEARABLE_SERVICE_CONNECTED);
                    OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.XIAOMI_WEARABLE_SERVICE_CONNECTED);
                } catch (Exception e) {
                    Log.f(MiWearableHelper.TAG, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Log.f(MiWearableHelper.TAG, "onWearableServiceDisconnected");
                MiWearableHelper.this.handleConsoleAlarm(4, 1);
            }
        };
        Intent intent = new Intent();
        intent.setAction(CONSOLE_ALARM_ALERT);
        intent.setPackage(AdditionUtil.MIWEARABLE_PKG_NAME);
        try {
            boolean zBindService = this.mContext.bindService(intent, this.mWearableServiceConnection, 1);
            this.mBindWearableSuccess = zBindService;
            StatHelper.trackEvent(StatHelper.EVENT_MI_WEARABLE_BIND_RESULT, String.valueOf(zBindService));
            OneTrackStatHelper.trackBoolEvent(this.mBindWearableSuccess, OneTrackStatHelper.XIAOMI_WEARABLE_BIND_RESULT);
            Log.f(TAG, "bindWearableService result: " + this.mBindWearableSuccess);
        } catch (Exception e) {
            Log.e(TAG, "bindWearableService  error: " + e.getMessage());
        }
    }

    private void bindHealthService() {
        this.mHealthServiceConnection = new ServiceConnection() { // from class: com.android.deskclock.addition.other.MiWearableHelper.3
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                try {
                    Log.f(MiWearableHelper.TAG, "onHealthServiceConnected");
                    MiWearableHelper.this.mHealthAlarmConsoleV1 = IAlarmConsoleV1.Stub.asInterface(iBinder);
                    MiWearableHelper.this.handleConsoleAlarm(0, 2);
                    StatHelper.alarmEvent(StatHelper.EVENT_MI_WEARABLE_SERVICE_CONNECTED);
                    OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.XIAOMI_WEARABLE_SERVICE_CONNECTED);
                } catch (Exception e) {
                    Log.f(MiWearableHelper.TAG, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Log.f(MiWearableHelper.TAG, "onHealthServiceDisconnected");
                MiWearableHelper.this.handleConsoleAlarm(4, 2);
            }
        };
        Intent intent = new Intent();
        intent.setAction(CONSOLE_ALARM_ALERT);
        intent.setPackage(AdditionUtil.HEALTH_PKG_NAME);
        try {
            boolean zBindService = this.mContext.bindService(intent, this.mHealthServiceConnection, 1);
            this.mBindHealthSuccess = zBindService;
            StatHelper.trackEvent(StatHelper.EVENT_MI_WEARABLE_BIND_RESULT, String.valueOf(zBindService));
            OneTrackStatHelper.trackBoolEvent(this.mBindHealthSuccess, OneTrackStatHelper.XIAOMI_WEARABLE_BIND_RESULT);
            Log.f(TAG, "bindHealthService result: " + this.mBindHealthSuccess);
        } catch (Exception e) {
            Log.e(TAG, "bindHealthService  error: " + e.getMessage());
        }
    }

    public void notifyMiWearableAlert(int i, long j, String str) {
        Log.f(TAG, "notifyMiWearableAlert");
        ConsoleAlarm consoleAlarm = this.mConsoleAlarm;
        if (consoleAlarm == null) {
            this.mConsoleAlarm = new ConsoleAlarm(i, j, str);
        } else {
            consoleAlarm.setAlertTime(j);
            this.mConsoleAlarm.setLabel(str);
            this.mConsoleAlarm.setId(i);
        }
        if (this.mWearableAlarmConsoleV1 == null) {
            bindWearableService();
        } else if (!this.mIsWearableRegisterCallBack) {
            handleConsoleAlarm(0, 1);
        } else {
            handleConsoleAlarm(1, 1);
        }
        if (this.mHealthAlarmConsoleV1 == null) {
            bindHealthService();
        } else if (!this.mIsHealthRegisterCallBack) {
            handleConsoleAlarm(0, 2);
        } else {
            handleConsoleAlarm(1, 2);
        }
    }

    public void notifyMiWearable(boolean z, int i, long j, String str) {
        Log.f(TAG, "notifyMiWearable, dismiss: " + z + ", id:" + i);
        ConsoleAlarm consoleAlarm = this.mConsoleAlarm;
        if (consoleAlarm == null) {
            this.mConsoleAlarm = new ConsoleAlarm(i, j, str);
        } else {
            consoleAlarm.setId(i);
            this.mConsoleAlarm.setLabel(str);
            this.mConsoleAlarm.setAlertTime(j);
        }
        if (z) {
            Log.f(TAG, "notifyMiWearable, dismiss alarm from deskClock, id: " + i);
            handleConsoleAlarm(3, 0);
            this.mFinishedAction = 3;
        } else {
            Log.f(TAG, "notifyMiWearable, snooze alarm from deskClock, id: " + i);
            handleConsoleAlarm(2, 0);
            this.mFinishedAction = 2;
        }
    }

    public void release() {
        Log.f(TAG, "release,mIsFinished: " + this.mIsFinished);
        try {
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                this.mHandler = null;
            }
            HandlerThread handlerThread = this.mHandlerThread;
            if (handlerThread != null) {
                handlerThread.quitSafely();
            }
            if (!this.mIsFinished) {
                int i = this.mFinishedAction;
                if (i == 2) {
                    onConsoleAlarmSnooze(this.mConsoleAlarm);
                    onConsoleAlarmUnRegister(0);
                } else if (i == 3) {
                    onConsoleAlarmDismiss(this.mConsoleAlarm);
                    onConsoleAlarmUnRegister(0);
                }
                this.mIsFinished = true;
            }
            ServiceConnection serviceConnection = this.mWearableServiceConnection;
            if (serviceConnection != null) {
                this.mContext.unbindService(serviceConnection);
                this.mBindWearableSuccess = false;
                this.mWearableAlarmConsoleV1 = null;
            }
            ServiceConnection serviceConnection2 = this.mHealthServiceConnection;
            if (serviceConnection2 != null) {
                this.mContext.unbindService(serviceConnection2);
                this.mBindHealthSuccess = false;
                this.mHealthAlarmConsoleV1 = null;
            }
        } catch (Exception e) {
            Log.f(TAG, "release error : " + e.getMessage());
        }
    }

    public void directlyDismiss() {
        if (this.mWearableAlarmConsoleV1 == null || this.mConsoleAlarm == null) {
            Log.f(TAG, "directlyDismiss invalid, mAlarmConsoleV1/alarm is null!");
            return;
        }
        Log.f(TAG, "directly dismiss alarm from deskClock, id: " + this.mConsoleAlarm.getId());
        handleConsoleAlarm(3, 0);
        this.mFinishedAction = 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConsoleAlarm(int i, int i2) {
        Handler handler = this.mHandler;
        if (handler == null) {
            return;
        }
        Message messageObtainMessage = handler.obtainMessage(i);
        messageObtainMessage.arg1 = i2;
        this.mHandler.sendMessage(messageObtainMessage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConsoleAlarmRegister(ConsoleAlarm consoleAlarm, int i) {
        IAlarmConsoleV1Callback iAlarmConsoleV1Callback;
        IAlarmConsoleV1 iAlarmConsoleV1 = i == 2 ? this.mHealthAlarmConsoleV1 : this.mWearableAlarmConsoleV1;
        Log.f(TAG, "register callback for: ".concat(i == 2 ? "health" : "wearable"));
        if (consoleAlarm == null || (iAlarmConsoleV1Callback = this.mAlarmConsoleV1Callback) == null || iAlarmConsoleV1 == null) {
            return;
        }
        try {
            iAlarmConsoleV1.registerConsoleCallback(iAlarmConsoleV1Callback);
            if (i == 2) {
                this.mIsHealthRegisterCallBack = true;
            } else if (i == 1) {
                this.mIsWearableRegisterCallBack = true;
            }
            Log.f(TAG, "notify onAlert, id :" + consoleAlarm.getId());
            iAlarmConsoleV1.onAlert(consoleAlarm);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.f(TAG, "onConsoleAlarmRegister error:" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConsoleAlarmAlert(ConsoleAlarm consoleAlarm, int i) {
        IAlarmConsoleV1 iAlarmConsoleV1 = i == 2 ? this.mHealthAlarmConsoleV1 : this.mWearableAlarmConsoleV1;
        if (consoleAlarm == null || iAlarmConsoleV1 == null) {
            return;
        }
        try {
            Log.f(TAG, "notify onAlert, id : " + consoleAlarm.getId());
            iAlarmConsoleV1.onAlert(consoleAlarm);
        } catch (Exception e) {
            Log.f(TAG, "notify onAlert error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConsoleAlarmSnooze(ConsoleAlarm consoleAlarm) {
        if (consoleAlarm == null) {
            return;
        }
        try {
            Log.f(TAG, "onConsoleAlarmSnooze, id: " + consoleAlarm.getId());
            IAlarmConsoleV1 iAlarmConsoleV1 = this.mWearableAlarmConsoleV1;
            if (iAlarmConsoleV1 != null) {
                iAlarmConsoleV1.onSnooze(this.mConsoleAlarm);
            }
            IAlarmConsoleV1 iAlarmConsoleV2 = this.mHealthAlarmConsoleV1;
            if (iAlarmConsoleV2 != null) {
                iAlarmConsoleV2.onSnooze(this.mConsoleAlarm);
            }
        } catch (Exception e) {
            Log.f(TAG, "onConsoleAlarmSnooze error: " + e);
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConsoleAlarmDismiss(ConsoleAlarm consoleAlarm) {
        if (consoleAlarm == null) {
            return;
        }
        try {
            Log.f(TAG, "onConsoleAlarmDismiss, id: " + consoleAlarm.getId());
            if (this.mWearableAlarmConsoleV1 != null) {
                Log.d(TAG, "onConsoleAlarmDismiss to Wearable");
                this.mWearableAlarmConsoleV1.onDismiss(consoleAlarm);
            }
            if (this.mHealthAlarmConsoleV1 != null) {
                Log.d(TAG, "onConsoleAlarmDismiss to Health");
                this.mHealthAlarmConsoleV1.onDismiss(consoleAlarm);
            }
        } catch (Exception e) {
            Log.f(TAG, "onConsoleAlarmDismiss error: " + e);
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConsoleAlarmUnRegister(int i) {
        IAlarmConsoleV1 iAlarmConsoleV1;
        try {
            Log.f(TAG, "onConsoleAlarmUnRegister");
            if (i == 0) {
                IAlarmConsoleV1 iAlarmConsoleV2 = this.mHealthAlarmConsoleV1;
                if (iAlarmConsoleV2 != null) {
                    iAlarmConsoleV2.unregisterConsoleCallback();
                    this.mIsHealthRegisterCallBack = false;
                }
                IAlarmConsoleV1 iAlarmConsoleV3 = this.mWearableAlarmConsoleV1;
                if (iAlarmConsoleV3 != null) {
                    iAlarmConsoleV3.unregisterConsoleCallback();
                    this.mIsWearableRegisterCallBack = false;
                    return;
                }
                return;
            }
            if (i == 2) {
                IAlarmConsoleV1 iAlarmConsoleV4 = this.mHealthAlarmConsoleV1;
                if (iAlarmConsoleV4 != null) {
                    iAlarmConsoleV4.unregisterConsoleCallback();
                    this.mIsHealthRegisterCallBack = false;
                    return;
                }
                return;
            }
            if (i != 1 || (iAlarmConsoleV1 = this.mWearableAlarmConsoleV1) == null) {
                return;
            }
            iAlarmConsoleV1.unregisterConsoleCallback();
            this.mIsWearableRegisterCallBack = false;
        } catch (RemoteException e) {
            Log.f(TAG, "onConsoleAlarmUnRegister error:" + e);
            e.printStackTrace();
        }
    }
}
