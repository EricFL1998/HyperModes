package miuix.animation.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import miuix.animation.internal.AndroidEngine;
import miuix.animation.internal.FolmeEngine;

/* JADX INFO: loaded from: classes2.dex */
public class ObjectPool {
    private static final long DELAY = 5000;
    private static final int MAX_POOL_SIZE = 10;
    private final Map<Class<?>, Cache> mCacheMap = new HashMap();
    private final FolmeEngine mEngine;

    public interface IPoolObject {
        void clear();
    }

    private static class Cache {
        final Map<Object, Boolean> mCacheRecord;
        boolean mPendingShrink;
        final ConcurrentLinkedQueue<Object> pool;
        final Runnable shrinkTask;

        private Cache() {
            this.pool = new ConcurrentLinkedQueue<>();
            this.mCacheRecord = new HashMap();
            this.mPendingShrink = false;
            this.shrinkTask = new Runnable() { // from class: miuix.animation.utils.ObjectPool.Cache.1
                @Override // java.lang.Runnable
                public void run() {
                    Cache.this.mPendingShrink = false;
                    Cache.this.shrink();
                }
            };
        }

        <T> T acquireObject(Class<T> cls, Object... objArr) {
            T t = (T) this.pool.poll();
            if (t == null) {
                return cls != null ? (T) ObjectPool.createObject(cls, objArr) : t;
            }
            this.mCacheRecord.remove(Integer.valueOf(System.identityHashCode(t)));
            return t;
        }

        void releaseObject(Handler handler, Object obj) {
            if (this.mCacheRecord.put(Integer.valueOf(System.identityHashCode(obj)), true) != null) {
                return;
            }
            this.pool.add(obj);
            if (handler != null) {
                if (this.mPendingShrink) {
                    handler.removeCallbacks(this.shrinkTask);
                    this.mPendingShrink = false;
                }
                if (this.pool.size() > 10) {
                    this.mPendingShrink = true;
                    handler.postDelayed(this.shrinkTask, ObjectPool.DELAY);
                    return;
                }
                return;
            }
            this.mPendingShrink = false;
            Log.w(CommonUtils.TAG, "ObjectPool.releaseObject handler is null! looper: " + Looper.myLooper());
            this.shrinkTask.run();
        }

        void shrink() {
            Object objPoll;
            while (this.pool.size() > 10 && (objPoll = this.pool.poll()) != null) {
                this.mCacheRecord.remove(Integer.valueOf(System.identityHashCode(objPoll)));
            }
        }
    }

    public ObjectPool(FolmeEngine folmeEngine) {
        this.mEngine = folmeEngine;
    }

    public static <T> T acquire(ObjectPool objectPool, Class<T> cls, Object... objArr) {
        return (T) getObjectCache(objectPool, cls, true).acquireObject(cls, objArr);
    }

    public static void release(ObjectPool objectPool, Object obj) {
        if (obj == null) {
            return;
        }
        Class<?> cls = obj.getClass();
        if (obj instanceof IPoolObject) {
            ((IPoolObject) obj).clear();
        } else if (obj instanceof Collection) {
            ((Collection) obj).clear();
        } else if (obj instanceof Map) {
            ((Map) obj).clear();
        }
        Cache objectCache = getObjectCache(objectPool, cls, false);
        if (objectCache != null) {
            FolmeEngine folmeEngine = objectPool.mEngine;
            objectCache.releaseObject(folmeEngine instanceof AndroidEngine ? ((AndroidEngine) folmeEngine).getHandler() : null, obj);
        }
    }

    private static Cache getObjectCache(ObjectPool objectPool, Class<?> cls, boolean z) {
        Cache cache = objectPool.mCacheMap.get(cls);
        if (cache != null || !z) {
            return cache;
        }
        Cache cache2 = new Cache();
        Cache cachePut = objectPool.mCacheMap.put(cls, cache2);
        return cachePut != null ? cachePut : cache2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object createObject(Class<?> cls, Object... objArr) {
        try {
            for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
                if (constructor.getParameterTypes().length == objArr.length) {
                    constructor.setAccessible(true);
                    return constructor.newInstance(objArr);
                }
            }
            return null;
        } catch (Exception e) {
            Log.w(CommonUtils.TAG, "ObjectPool.createObject failed, clz = " + cls, e);
            return null;
        }
    }
}
