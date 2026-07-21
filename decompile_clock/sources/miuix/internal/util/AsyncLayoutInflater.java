package miuix.internal.util;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes2.dex */
public final class AsyncLayoutInflater {
    private static final int INFLATE_GROUP_SIZE = 10;
    private static final int INFLATE_MESSAGE = 1;
    private static final String TAG = "AsyncLayoutInflater";
    private final Handler mHandler;
    private final Handler.Callback mHandlerCallback;
    private final Handler mInflateHandler;
    private LayoutInflater mInflater;
    private ViewGroup mParent;

    public interface OnInflateFinishedListener {
        void onInflateFinished(View view, int i, ViewGroup viewGroup);
    }

    public AsyncLayoutInflater() {
        Handler.Callback callback = new Handler.Callback() { // from class: miuix.internal.util.AsyncLayoutInflater.2
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                for (InflateRequest inflateRequest = (InflateRequest) message.obj; inflateRequest != null; inflateRequest = inflateRequest.next) {
                    if (inflateRequest.view == null && inflateRequest.resid != 0) {
                        inflateRequest.view = inflateRequest.localInflater.inflate(inflateRequest.resid, inflateRequest.parent, false);
                    }
                    inflateRequest.callback.onInflateFinished(inflateRequest.view, inflateRequest.resid, inflateRequest.parent);
                    AsyncLayoutInflater.this.releaseRequest(inflateRequest);
                }
                return true;
            }
        };
        this.mHandlerCallback = callback;
        this.mHandler = new Handler(callback);
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        this.mInflateHandler = new Handler(handlerThread.getLooper()) { // from class: miuix.internal.util.AsyncLayoutInflater.1
            InflateRequest head;
            int index = 0;
            InflateRequest tail;

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 1 && (message.obj instanceof InflateRequest)) {
                    InflateRequest inflateRequest = (InflateRequest) message.obj;
                    try {
                        inflateRequest.view = inflateRequest.localInflater.inflate(inflateRequest.resid, inflateRequest.parent, false);
                    } catch (RuntimeException e) {
                        Log.w(AsyncLayoutInflater.TAG, "Failed to inflate resource in the background! Retrying on the UI thread", e);
                    }
                    if (this.head == null) {
                        this.head = inflateRequest;
                    } else {
                        this.tail.next = inflateRequest;
                    }
                    this.tail = inflateRequest;
                    if (this.index >= 10 || AsyncLayoutInflater.this.mInflateHandler.getLooper().getQueue().isIdle()) {
                        AsyncLayoutInflater.this.mHandler.sendMessageAtFrontOfQueue(Message.obtain(AsyncLayoutInflater.this.mHandler, 0, this.head));
                        this.head = null;
                        this.tail = null;
                        this.index = 0;
                        return;
                    }
                    this.index++;
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseRequest(InflateRequest inflateRequest) {
        inflateRequest.callback = null;
        inflateRequest.localInflater = null;
        inflateRequest.parent = null;
        inflateRequest.resid = 0;
        inflateRequest.view = null;
    }

    public void inflate(int i, ViewGroup viewGroup, OnInflateFinishedListener onInflateFinishedListener) {
        if (viewGroup == null) {
            return;
        }
        Message messageObtain = Message.obtain();
        InflateRequest inflateRequest = new InflateRequest();
        LayoutInflater layoutInflater = this.mInflater;
        if (layoutInflater == null) {
            BasicInflater basicInflater = new BasicInflater(viewGroup.getContext());
            this.mInflater = basicInflater;
            inflateRequest.localInflater = basicInflater;
        } else if (this.mParent != viewGroup) {
            this.mParent = viewGroup;
            BasicInflater basicInflater2 = new BasicInflater(viewGroup.getContext());
            this.mInflater = basicInflater2;
            inflateRequest.localInflater = basicInflater2;
        } else {
            inflateRequest.localInflater = layoutInflater;
        }
        inflateRequest.resid = i;
        inflateRequest.parent = viewGroup;
        inflateRequest.callback = onInflateFinishedListener;
        messageObtain.what = 1;
        messageObtain.obj = inflateRequest;
        this.mInflateHandler.sendMessage(messageObtain);
    }

    public void removeAllMessages() {
        this.mInflater = null;
        this.mParent = null;
        this.mHandler.removeCallbacksAndMessages(null);
        this.mInflateHandler.removeCallbacksAndMessages(null);
    }

    private static class InflateRequest {
        OnInflateFinishedListener callback;
        LayoutInflater localInflater;
        InflateRequest next;
        ViewGroup parent;
        int resid;
        View view;

        InflateRequest() {
        }
    }

    private static class BasicInflater extends LayoutInflater {
        private static final String[] sClassPrefixList = {"android.widget.", "android.webkit.", "android.app."};

        BasicInflater(Context context) {
            super(context);
        }

        @Override // android.view.LayoutInflater
        public LayoutInflater cloneInContext(Context context) {
            return new BasicInflater(context);
        }

        @Override // android.view.LayoutInflater
        protected View onCreateView(String str, AttributeSet attributeSet) throws ClassNotFoundException {
            for (String str2 : sClassPrefixList) {
                try {
                    View viewCreateView = createView(str, str2, attributeSet);
                    if (viewCreateView != null) {
                        return viewCreateView;
                    }
                } catch (ClassNotFoundException e) {
                    Log.e(AsyncLayoutInflater.TAG, "onCreateView : " + e);
                }
            }
            return super.onCreateView(str, attributeSet);
        }
    }
}
