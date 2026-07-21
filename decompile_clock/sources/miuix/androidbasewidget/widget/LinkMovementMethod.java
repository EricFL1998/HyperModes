package miuix.androidbasewidget.widget;

import android.text.Spannable;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/* JADX INFO: loaded from: classes2.dex */
public class LinkMovementMethod extends android.text.method.LinkMovementMethod {
    private static LinkMovementMethod sInstance;

    @Override // android.text.method.LinkMovementMethod, android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if ((action == 1 || action == 0) && ((int) motionEvent.getX()) - textView.getTotalPaddingLeft() < 0) {
            return false;
        }
        return super.onTouchEvent(textView, spannable, motionEvent);
    }

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new LinkMovementMethod();
        }
        return sInstance;
    }
}
