package miuix.animation.utils;

import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: classes2.dex */
public class StyleComposer {
    private static final String TAG = "StyleComposer";

    public interface IInterceptor<T> {
        Object onMethod(Method method, Object[] objArr, T... tArr);

        boolean shouldIntercept(Method method, Object[] objArr);
    }

    public static <T> T compose(Class<T> cls, T... tArr) {
        return (T) compose(cls, null, tArr);
    }

    public static <T> T compose(final Class<T> cls, final IInterceptor iInterceptor, final T... tArr) {
        Object objNewProxyInstance = Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new InvocationHandler() { // from class: miuix.animation.utils.StyleComposer.1
            @Override // java.lang.reflect.InvocationHandler
            public Object invoke(Object obj, Method method, Object[] objArr) {
                Object objOnMethod;
                IInterceptor iInterceptor2 = iInterceptor;
                if (iInterceptor2 != null && iInterceptor2.shouldIntercept(method, objArr)) {
                    objOnMethod = iInterceptor.onMethod(method, objArr, tArr);
                } else {
                    Object objInvoke = null;
                    for (Object obj2 : tArr) {
                        try {
                            objInvoke = method.invoke(obj2, objArr);
                        } catch (Exception e) {
                            Log.w(StyleComposer.TAG, "failed to invoke " + method + " for " + obj2, e.getCause());
                        }
                    }
                    objOnMethod = objInvoke;
                }
                if (objOnMethod != null) {
                    Object[] objArr2 = tArr;
                    if (objOnMethod == objArr2[objArr2.length - 1]) {
                        return cls.cast(obj);
                    }
                }
                return objOnMethod;
            }
        });
        if (cls.isInstance(objNewProxyInstance)) {
            return cls.cast(objNewProxyInstance);
        }
        return null;
    }
}
