package miuix.appcompat.app;

import android.graphics.Rect;
import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public interface IContentInsetState {
    void bindViewWithContentInset(View view);

    Rect getContentInset();

    void onContentInsetChanged(Rect rect);

    void onDispatchNestedScrollOffset(int[] iArr);

    void onProcessBindViewWithContentInset(Rect rect);

    boolean requestDispatchContentInset();

    void setCorrectNestedScrollMotionEventEnabled(boolean z);
}
