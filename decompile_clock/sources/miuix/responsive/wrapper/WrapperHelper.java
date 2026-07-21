package miuix.responsive.wrapper;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.core.view.LayoutInflaterCompat;
import java.lang.reflect.InvocationTargetException;
import miuix.reflect.ReflectionHelper;

/* JADX INFO: loaded from: classes3.dex */
public class WrapperHelper {
    public static void setFactory2(LayoutInflater layoutInflater, LayoutInflater.Factory2 factory2) {
        LayoutInflater.Factory2 factory3 = layoutInflater.getFactory2();
        if (factory3 != null || layoutInflater.getFactory() != null) {
            if (factory3 instanceof Factory2Wrapper) {
                ((Factory2Wrapper) layoutInflater.getFactory2()).setOriginFactory2(factory2);
                return;
            }
            if (factory2 instanceof Factory2Wrapper) {
                ((Factory2Wrapper) factory2).setOriginFactory2(factory3);
            }
            try {
                ReflectionHelper.setFieldValue(layoutInflater.getClass().getSuperclass(), layoutInflater, "mFactory2", factory2);
                return;
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        LayoutInflaterCompat.setFactory2(layoutInflater, factory2);
    }

    public static void setOnHierarchyChangeListener(ViewGroup viewGroup, ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener) {
        if (viewGroup != null) {
            try {
                Object fieldValue = ReflectionHelper.getFieldValue(viewGroup.getClass(), viewGroup, "mOnHierarchyChangeListener");
                if (fieldValue instanceof OnHierarchyChangeListenerWrapper) {
                    ((OnHierarchyChangeListenerWrapper) fieldValue).setOnHierarchyChangeListener(onHierarchyChangeListener);
                } else {
                    viewGroup.setOnHierarchyChangeListener(onHierarchyChangeListener);
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
