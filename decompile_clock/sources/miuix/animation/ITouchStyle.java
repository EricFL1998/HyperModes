package miuix.animation;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import miuix.animation.base.AnimConfig;

/* JADX INFO: loaded from: classes2.dex */
public interface ITouchStyle extends IStateContainer {

    public enum TouchMode {
        NORMAL,
        ROUND_CORNERS
    }

    public enum TouchRectGravity {
        TOP_LEFT,
        TOP_CENTER,
        CENTER_LEFT,
        CENTER_IN_PARENT
    }

    public enum TouchType {
        UP,
        DOWN
    }

    void bindViewOfListItem(View view, AnimConfig... animConfigArr);

    ITouchStyle clearTintColor();

    void handleNoScaleTouchOf(View view, AnimConfig... animConfigArr);

    void handleTouchOf(View view, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, AnimConfig... animConfigArr);

    void handleTouchOf(View view, View.OnClickListener onClickListener, AnimConfig... animConfigArr);

    void handleTouchOf(View view, boolean z, AnimConfig... animConfigArr);

    void handleTouchOf(View view, AnimConfig... animConfigArr);

    void ignoreTouchOf(View view);

    void onMotionEvent(MotionEvent motionEvent);

    void onMotionEventEx(View view, MotionEvent motionEvent, AnimConfig... animConfigArr);

    ITouchStyle setAlpha(float f, TouchType... touchTypeArr);

    ITouchStyle setBackgroundColor(float f, float f2, float f3, float f4);

    ITouchStyle setBackgroundColor(int i);

    ITouchStyle setNoScale(boolean z);

    ITouchStyle setScale(float f, TouchType... touchTypeArr);

    ITouchStyle setTint(float f, float f2, float f3, float f4);

    ITouchStyle setTint(int i);

    ITouchStyle setTintMode(int i);

    void setTouchDown();

    ITouchStyle setTouchPadding(float f, float f2, float f3, float f4);

    ITouchStyle setTouchRadius(float f);

    ITouchStyle setTouchRadius(float f, float f2, float f3, float f4);

    ITouchStyle setTouchRect(RectF rectF, TouchRectGravity touchRectGravity);

    void setTouchUp();

    void touchDown(AnimConfig... animConfigArr);

    void touchUp(AnimConfig... animConfigArr);

    ITouchStyle useVarFont(TextView textView, int i, int i2, int i3);
}
