package miuix.appcompat.app;

import java.util.Collection;
import miuix.animation.listener.UpdateInfo;

/* JADX INFO: loaded from: classes2.dex */
public interface ActionBarTransitionListener {
    @Deprecated
    default void onActionBarMove(float f, float f2) {
    }

    void onActionBarResizing(int i, float f, int i2);

    void onExpandStateChanged(int i);

    @Deprecated
    default void onTransitionBegin(Object obj) {
    }

    @Deprecated
    default void onTransitionComplete(Object obj) {
    }

    @Deprecated
    default void onTransitionUpdate(Object obj, Collection<UpdateInfo> collection) {
    }
}
