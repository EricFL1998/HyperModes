package miui.settings.commonlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class ApplicationReceiverStub extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.i(MemoryOptimizationUtil.TAG, "ApplicationReceiverStub start");
    }
}
