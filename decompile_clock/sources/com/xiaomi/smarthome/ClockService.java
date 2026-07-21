package com.xiaomi.smarthome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.xiaomi.smarthome.binder.ICallback;
import com.xiaomi.smarthome.binder.IClockService;

/* JADX INFO: loaded from: classes2.dex */
public class ClockService extends Service {
    private IBinder serviceManager = new ClockServiceBinder();

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.serviceManager;
    }

    private class ClockServiceBinder extends IClockService.Stub {
        @Override // com.xiaomi.smarthome.binder.IClockService
        public void notifySleepChange(ICallback iCallback) throws RemoteException {
        }

        private ClockServiceBinder() {
        }
    }
}
