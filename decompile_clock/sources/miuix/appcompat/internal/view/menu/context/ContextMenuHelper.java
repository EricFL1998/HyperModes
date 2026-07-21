package miuix.appcompat.internal.view.menu.context;

import android.view.ContextMenu;
import android.view.View;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import miuix.reflect.Reflects;

/* JADX INFO: loaded from: classes2.dex */
public class ContextMenuHelper {
    private static final Field mOnCreateContextMenuListener = Reflects.getDeclaredField("android.view.View$ListenerInfo", "mOnCreateContextMenuListener");
    private static final Method getContextMenuInfo = Reflects.getDeclaredMethod((Class<?>) View.class, "getContextMenuInfo", (Class<?>[]) new Class[0]);
    private static final Method onCreateContextMenu = Reflects.getDeclaredMethod((Class<?>) View.class, "onCreateContextMenu", (Class<?>[]) new Class[]{ContextMenu.class});
    private static final Field mListenerInfo = Reflects.getDeclaredField((Class<?>) View.class, "mListenerInfo");

    private ContextMenuHelper() {
    }

    public static void createContextMenu(View view, ContextMenuBuilder contextMenuBuilder) {
        View.OnCreateContextMenuListener onCreateContextMenuListener;
        ContextMenu.ContextMenuInfo contextMenuInfo = (ContextMenu.ContextMenuInfo) Reflects.invoke(view, getContextMenuInfo, new Object[0]);
        contextMenuBuilder.setCurrentMenuInfo(contextMenuInfo);
        Reflects.invoke(view, onCreateContextMenu, contextMenuBuilder);
        Object obj = Reflects.get(view, mListenerInfo);
        if (obj != null && (onCreateContextMenuListener = (View.OnCreateContextMenuListener) Reflects.get(obj, mOnCreateContextMenuListener)) != null) {
            onCreateContextMenuListener.onCreateContextMenu(contextMenuBuilder, view, contextMenuInfo);
        }
        contextMenuBuilder.setCurrentMenuInfo(null);
        Object parent = view.getParent();
        if (parent instanceof View) {
            createContextMenu((View) parent, contextMenuBuilder);
        }
    }
}
