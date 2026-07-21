package miuix.springback.view;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes3.dex */
public class SpringBackLayoutHelper {
    private static final int INVALID_POINTER = -1;
    int mActivePointerId = -1;
    float mInitialDownX;
    float mInitialDownY;
    int mScrollOrientation;
    private ViewGroup mTarget;
    int mTargetScrollOrientation;
    private int mTouchSlop;

    public SpringBackLayoutHelper(ViewGroup viewGroup, int i) {
        this.mTarget = viewGroup;
        this.mTargetScrollOrientation = i;
        this.mTouchSlop = ViewConfiguration.get(viewGroup.getContext()).getScaledTouchSlop();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        checkOrientation(motionEvent);
        int i = this.mScrollOrientation;
        if (i != 0 && i != this.mTargetScrollOrientation) {
            this.mTarget.requestDisallowInterceptTouchEvent(true);
            return false;
        }
        this.mTarget.requestDisallowInterceptTouchEvent(false);
        return true;
    }

    public boolean isTouchInTarget(MotionEvent motionEvent) {
        int iFindPointerIndex = motionEvent.findPointerIndex(motionEvent.getPointerId(0));
        if (iFindPointerIndex < 0) {
            return false;
        }
        float y = motionEvent.getY(iFindPointerIndex);
        float x = motionEvent.getX(iFindPointerIndex);
        int[] iArr = {0, 0};
        this.mTarget.getLocationInWindow(iArr);
        int i = iArr[0];
        int i2 = iArr[1];
        return new Rect(i, i2, this.mTarget.getWidth() + i, this.mTarget.getHeight() + i2).contains((int) x, (int) y);
    }

    void checkOrientation(MotionEvent motionEvent) {
        int iFindPointerIndex;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            int pointerId = motionEvent.getPointerId(0);
            this.mActivePointerId = pointerId;
            int iFindPointerIndex2 = motionEvent.findPointerIndex(pointerId);
            if (iFindPointerIndex2 < 0) {
                return;
            }
            this.mInitialDownY = motionEvent.getY(iFindPointerIndex2);
            this.mInitialDownX = motionEvent.getX(iFindPointerIndex2);
            this.mScrollOrientation = 0;
            return;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                int i = this.mActivePointerId;
                if (i != -1 && (iFindPointerIndex = motionEvent.findPointerIndex(i)) >= 0) {
                    float y = motionEvent.getY(iFindPointerIndex);
                    float x = motionEvent.getX(iFindPointerIndex);
                    float f = y - this.mInitialDownY;
                    float f2 = x - this.mInitialDownX;
                    if (Math.abs(f2) > this.mTouchSlop || Math.abs(f) > this.mTouchSlop) {
                        this.mScrollOrientation = Math.abs(f2) <= Math.abs(f) ? 2 : 1;
                        return;
                    }
                    return;
                }
                return;
            }
            if (actionMasked != 3) {
                return;
            }
        }
        this.mScrollOrientation = 0;
        this.mTarget.requestDisallowInterceptTouchEvent(false);
    }
}
