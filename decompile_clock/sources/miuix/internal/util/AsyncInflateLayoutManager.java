package miuix.internal.util;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class AsyncInflateLayoutManager {
    private static final AsyncInflateLayoutManager MANAGER = new AsyncInflateLayoutManager();
    private static final String TAG = "AsyncInflateManager";
    private AsyncLayoutInflater mInflater;
    private final HashMap<Integer, List<View>> mListTypeMap = new HashMap<>();
    private boolean isEnabled = true;
    private boolean isEnabledLog = false;
    private boolean isCachedEnable = false;
    private final AsyncLayoutInflater.OnInflateFinishedListener mListener = new AsyncLayoutInflater.OnInflateFinishedListener() { // from class: miuix.internal.util.AsyncInflateLayoutManager.1
        @Override // miuix.internal.util.AsyncLayoutInflater.OnInflateFinishedListener
        public void onInflateFinished(View view, int i, ViewGroup viewGroup) {
            List arrayList = (List) AsyncInflateLayoutManager.this.mListTypeMap.get(Integer.valueOf(i));
            if (arrayList == null) {
                arrayList = new ArrayList();
            }
            if (AsyncInflateLayoutManager.this.isEnabledLog) {
                Log.i(AsyncInflateLayoutManager.TAG, "async create view is success.");
            }
            arrayList.add(view);
            AsyncInflateLayoutManager.this.mListTypeMap.put(Integer.valueOf(i), arrayList);
        }
    };

    public static AsyncInflateLayoutManager getInstance() {
        return MANAGER;
    }

    public void setEnableAsyncInflate(boolean z) {
        this.isEnabled = z;
    }

    public void setEnableAsyncInflateLog(boolean z) {
        this.isEnabledLog = z;
    }

    public View inflateViewById(Context context, Integer num) {
        List<View> list = this.mListTypeMap.get(num);
        if (list == null || list.isEmpty()) {
            return LayoutInflater.from(context).inflate(num.intValue(), (ViewGroup) null);
        }
        View viewRemove = list.remove(0);
        if (list.isEmpty()) {
            this.mListTypeMap.remove(num);
        }
        return viewRemove;
    }

    public View inflateViewById(Integer num, ViewGroup viewGroup, boolean z) {
        if (viewGroup == null) {
            return null;
        }
        List<View> list = this.mListTypeMap.get(num);
        if (list == null || list.isEmpty()) {
            if (this.isEnabledLog) {
                Log.i(TAG, "inflateCacheById is null.");
            }
            return LayoutInflater.from(viewGroup.getContext()).inflate(num.intValue(), viewGroup, z);
        }
        View viewRemove = list.remove(0);
        if (list.isEmpty()) {
            this.mListTypeMap.remove(num);
        }
        if (this.isEnabledLog) {
            Log.i(TAG, "inflateCacheById is ok.");
        }
        return viewRemove;
    }

    public View inflateViewById(Integer num, Context context, ViewGroup viewGroup, boolean z) {
        View viewRemove;
        if (viewGroup == null || context == null) {
            return null;
        }
        List<View> list = this.mListTypeMap.get(num);
        if (list == null || list.isEmpty()) {
            if (this.isEnabledLog) {
                Log.i(TAG, "inflateCacheById is null.");
            }
            return LayoutInflater.from(context).inflate(num.intValue(), viewGroup, z);
        }
        if (this.isCachedEnable) {
            viewRemove = list.get(0);
        } else {
            viewRemove = list.remove(0);
        }
        if (list.isEmpty()) {
            this.mListTypeMap.remove(num);
        }
        if (this.isEnabledLog) {
            Log.i(TAG, "inflateCacheById is ok.");
        }
        return viewRemove;
    }

    public void startAsyncInflateLayout(ViewGroup viewGroup, int[] iArr) {
        if (isMainThread() && this.isEnabled && iArr.length != 0) {
            if (this.mInflater == null) {
                this.mInflater = new AsyncLayoutInflater();
            }
            if (this.isEnabledLog) {
                Log.i(TAG, "async create view is start.");
            }
            asyncInflate(iArr, viewGroup);
        }
    }

    public void startAsyncInflateLayout(ViewGroup viewGroup, int[] iArr, boolean z) {
        if (isMainThread() && this.isEnabled && iArr.length != 0) {
            this.isCachedEnable = z;
            if (this.mInflater == null) {
                this.mInflater = new AsyncLayoutInflater();
            }
            if (this.isEnabledLog) {
                Log.i(TAG, "async create view is start.");
            }
            asyncInflate(iArr, viewGroup);
        }
    }

    public void startAsyncInflateLayout(ViewGroup viewGroup, int i, int i2) {
        if (isMainThread() && this.isEnabled && i2 > 0) {
            if (this.mInflater == null) {
                this.mInflater = new AsyncLayoutInflater();
            }
            if (this.isEnabledLog) {
                Log.i(TAG, "async create view is start.");
            }
            for (int i3 = 0; i3 < i2; i3++) {
                this.mInflater.inflate(i, viewGroup, this.mListener);
            }
        }
    }

    public void startAsyncInflateLayout(ViewGroup viewGroup, InflateRequest[] inflateRequestArr) {
        if (isMainThread() && this.isEnabled && inflateRequestArr.length != 0) {
            if (this.mInflater == null) {
                this.mInflater = new AsyncLayoutInflater();
            }
            if (this.isEnabledLog) {
                Log.i(TAG, "async create view is start.");
            }
            for (InflateRequest inflateRequest : inflateRequestArr) {
                if (inflateRequest != null) {
                    for (int i = 0; i < inflateRequest.count; i++) {
                        this.mInflater.inflate(inflateRequest.layoutId, viewGroup, this.mListener);
                    }
                }
            }
        }
    }

    private void asyncInflate(int[] iArr, ViewGroup viewGroup) {
        for (int i : iArr) {
            this.mInflater.inflate(i, viewGroup, this.mListener);
        }
    }

    private boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public void release() {
        if (this.isEnabled) {
            AsyncLayoutInflater asyncLayoutInflater = this.mInflater;
            if (asyncLayoutInflater != null) {
                asyncLayoutInflater.removeAllMessages();
            }
            this.mListTypeMap.clear();
            this.isCachedEnable = false;
        }
    }

    public static class InflateRequest {
        int count;
        int layoutId;

        public InflateRequest(int i, int i2) {
            this.layoutId = i;
            this.count = i2;
        }
    }
}
