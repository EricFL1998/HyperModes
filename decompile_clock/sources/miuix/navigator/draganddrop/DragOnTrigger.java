package miuix.navigator.draganddrop;

import android.view.DragEvent;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public class DragOnTrigger implements View.OnDragListener {
    private final long mDelay;
    private final Runnable mRunnable;

    public DragOnTrigger(Runnable runnable) {
        this(runnable, 500L);
    }

    public DragOnTrigger(Runnable runnable, long j) {
        this.mRunnable = runnable;
        this.mDelay = j;
    }

    @Override // android.view.View.OnDragListener
    public boolean onDrag(View view, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        if (action != 1) {
            if (action != 5) {
                if (action != 6) {
                    return false;
                }
                view.removeCallbacks(this.mRunnable);
                return true;
            }
            view.postDelayed(this.mRunnable, this.mDelay);
        }
        return true;
    }
}
