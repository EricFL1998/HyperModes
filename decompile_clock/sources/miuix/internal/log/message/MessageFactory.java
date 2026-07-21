package miuix.internal.log.message;

import android.util.Log;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/* JADX INFO: loaded from: classes2.dex */
public class MessageFactory {
    private static final int MAX_RECYCLED = 10;
    private static final String TAG = "MessageFactory";
    private static HashMap<Class<?>, MessageCache<?>> mCacheMap = new HashMap<>();

    public static <T extends Message> T obtain(Class<T> cls) {
        MessageCache<?> messageCacheCreateInstance = mCacheMap.get(cls);
        if (messageCacheCreateInstance == null) {
            messageCacheCreateInstance = MessageCache.createInstance(cls, 10);
            mCacheMap.put(cls, messageCacheCreateInstance);
        }
        return (T) messageCacheCreateInstance.obtain();
    }

    static <T extends Message> void recycle(T t) {
        MessageCache<?> messageCache = mCacheMap.get(t.getClass());
        if (messageCache != null) {
            messageCache.recycle(t);
        }
    }

    private static class MessageCache<T extends Message> {
        private T[] iCache;
        private Constructor<T> iConstructor;
        private int iPointer = 0;

        public static <T extends Message> MessageCache<T> createInstance(Class<T> cls, int i) {
            try {
                return new MessageCache<>(cls.getConstructor(new Class[0]), (Message[]) Array.newInstance((Class<?>) cls, i));
            } catch (NoSuchMethodException unused) {
                throw new IllegalArgumentException("Class " + cls.getName() + " must have a public empty constructor");
            }
        }

        private MessageCache(Constructor<T> constructor, T[] tArr) {
            this.iConstructor = constructor;
            this.iCache = tArr;
        }

        public synchronized T obtain() {
            int i = this.iPointer;
            if (i > 0) {
                int i2 = i - 1;
                this.iPointer = i2;
                T t = this.iCache[i2];
                t.prepareForReuse();
                return t;
            }
            return (T) create();
        }

        public synchronized void recycle(T t) {
            int i = this.iPointer;
            T[] tArr = this.iCache;
            if (i < tArr.length) {
                tArr[i] = t;
                this.iPointer = i + 1;
            }
        }

        private T create() {
            try {
                return this.iConstructor.newInstance(new Object[0]);
            } catch (Exception e) {
                Log.e(MessageFactory.TAG, "Fail to construct new instance of class: " + this.iConstructor.getDeclaringClass().getName(), e);
                return null;
            }
        }
    }
}
