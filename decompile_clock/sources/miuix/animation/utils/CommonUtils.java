package miuix.animation.utils;

import android.animation.ArgbEvaluator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import miuix.animation.IAnimTarget;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.ViewProperty;

/* JADX INFO: loaded from: classes2.dex */
public class CommonUtils {
    public static final String D_TAG = "folme_design";
    public static final String TAG = "miuix_anim";
    public static final int UNIT_MILLIS_SECOND = 1000000000;
    public static final int UNIT_SECOND = 1000;
    private static float sTouchSlop;
    public static final ArgbEvaluator sArgbEvaluator = new ArgbEvaluator();
    private static final Class<?>[] BUILT_IN = {String.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Short.TYPE, Short.class, Float.TYPE, Float.class, Double.TYPE, Double.class};
    private static ArrayMap<String, Long> sTimeStatArray = new ArrayMap<>();

    public static boolean hasFlags(long j, long j2) {
        return (j & j2) != 0;
    }

    private CommonUtils() {
    }

    private static class OnPreDrawTask implements ViewTreeObserver.OnPreDrawListener {
        Runnable mTask;
        WeakReference<View> mView;

        OnPreDrawTask(Runnable runnable) {
            this.mTask = runnable;
        }

        public void start(View view) {
            ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
            this.mView = new WeakReference<>(view);
            viewTreeObserver.addOnPreDrawListener(this);
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            View view = this.mView.get();
            if (view != null) {
                Runnable runnable = this.mTask;
                if (runnable != null) {
                    runnable.run();
                }
                view.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            this.mTask = null;
            return true;
        }
    }

    public static void runOnPreDraw(View view, Runnable runnable) {
        if (view == null) {
            return;
        }
        new OnPreDrawTask(runnable).start(view);
    }

    public static int[] toIntArray(float[] fArr) {
        int[] iArr = new int[fArr.length];
        for (int i = 0; i < fArr.length; i++) {
            iArr[i] = (int) fArr[i];
        }
        return iArr;
    }

    public static StringBuilder setToString(Set set) {
        StringBuilder sb = new StringBuilder();
        if (set == null) {
            sb.append("null");
            return sb;
        }
        sb.append('{');
        Object[] array = set.toArray();
        for (int i = 0; i < set.size(); i++) {
            sb.append('\n');
            sb.append(i).append('.').append(array[i]);
        }
        sb.append("\n}");
        return sb;
    }

    public static String mapsToString(Map[] mapArr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < mapArr.length; i++) {
            sb.append('\n');
            sb.append(i).append('.').append((CharSequence) mapToString(mapArr[i], "    "));
        }
        sb.append("\n]");
        return sb.toString();
    }

    public static <K, V> StringBuilder mapToString(Map<K, V> map, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        if (map != null && map.size() > 0) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                sb.append('\n').append(str);
                sb.append(entry.getKey()).append('=').append(entry.getValue());
            }
            sb.append('\n');
        }
        sb.append("\t}");
        return sb;
    }

    @SafeVarargs
    public static <T> T[] mergeArray(T[] tArr, T... tArr2) {
        if (tArr == null) {
            return tArr2;
        }
        if (tArr2 == null) {
            return tArr;
        }
        Object objNewInstance = Array.newInstance(tArr.getClass().getComponentType(), tArr.length + tArr2.length);
        System.arraycopy(tArr, 0, objNewInstance, 0, tArr.length);
        System.arraycopy(tArr2, 0, objNewInstance, tArr.length, tArr2.length);
        return (T[]) ((Object[]) objNewInstance);
    }

    public static int toIntValue(Object obj) {
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        if (obj instanceof Float) {
            return ((Float) obj).intValue();
        }
        throw new IllegalArgumentException("toFloat failed, value is " + obj);
    }

    public static float toFloatValue(Object obj) {
        if (obj instanceof Integer) {
            return ((Integer) obj).floatValue();
        }
        if (obj instanceof Float) {
            return ((Float) obj).floatValue();
        }
        throw new IllegalArgumentException("toFloat failed, value is " + obj);
    }

    public static <T> boolean isArrayEmpty(T[] tArr) {
        return tArr == null || tArr.length == 0;
    }

    public static <T> boolean inArray(T[] tArr, T t) {
        if (t != null && tArr != null && tArr.length > 0) {
            for (T t2 : tArr) {
                if (t2.equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static float getTouchSlop(View view) {
        if (sTouchSlop == 0.0f && view != null) {
            sTouchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        }
        return sTouchSlop;
    }

    public static double getDistance(float f, float f2, float f3, float f4) {
        return Math.sqrt(Math.pow(f3 - f, 2.0d) + Math.pow(f4 - f2, 2.0d));
    }

    public static void getRect(IAnimTarget iAnimTarget, RectF rectF) {
        rectF.left = iAnimTarget.getValue(ViewProperty.X);
        rectF.top = iAnimTarget.getValue(ViewProperty.Y);
        rectF.right = rectF.left + iAnimTarget.getValue(ViewProperty.WIDTH);
        rectF.bottom = rectF.top + iAnimTarget.getValue(ViewProperty.HEIGHT);
    }

    public static float getSize(IAnimTarget iAnimTarget, FloatProperty floatProperty) {
        if (floatProperty == ViewProperty.X) {
            floatProperty = ViewProperty.WIDTH;
        } else if (floatProperty == ViewProperty.Y) {
            floatProperty = ViewProperty.HEIGHT;
        } else if (floatProperty != ViewProperty.WIDTH && floatProperty != ViewProperty.HEIGHT) {
            floatProperty = null;
        }
        if (floatProperty == null) {
            return 0.0f;
        }
        return iAnimTarget.getValue(floatProperty);
    }

    public static boolean isBuiltInClass(Class<?> cls) {
        return inArray(BUILT_IN, cls);
    }

    public static <T> void addTo(Collection<T> collection, Collection<T> collection2) {
        for (T t : collection) {
            if (!collection2.contains(t)) {
                collection2.add(t);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v2 */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v4, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.io.Closeable, java.io.InputStreamReader, java.io.Reader] */
    /* JADX WARN: Type inference failed for: r5v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v13 */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v5, types: [java.io.Closeable] */
    public static String readProp(String str) {
        Throwable th;
        IOException e;
        BufferedReader bufferedReader;
        ?? inputStreamReader = "getprop ";
        try {
            try {
                inputStreamReader = new InputStreamReader(Runtime.getRuntime().exec("getprop " + ((String) str)).getInputStream());
                try {
                    bufferedReader = new BufferedReader(inputStreamReader);
                    try {
                        String line = bufferedReader.readLine();
                        closeQuietly(bufferedReader);
                        closeQuietly(inputStreamReader);
                        return line;
                    } catch (IOException e2) {
                        e = e2;
                        Log.i(TAG, "readProp failed", e);
                        closeQuietly(bufferedReader);
                        closeQuietly(inputStreamReader);
                        return "";
                    }
                } catch (IOException e3) {
                    e = e3;
                    bufferedReader = null;
                } catch (Throwable th2) {
                    th = th2;
                    str = 0;
                    closeQuietly(str);
                    closeQuietly(inputStreamReader);
                    throw th;
                }
            } catch (IOException e4) {
                inputStreamReader = 0;
                e = e4;
                bufferedReader = null;
            } catch (Throwable th3) {
                inputStreamReader = 0;
                th = th3;
                str = 0;
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                Log.w(TAG, "close " + closeable + " failed", e);
            }
        }
    }

    public static <T> T getLocal(ObjectPool objectPool, ThreadLocal<T> threadLocal, Class cls) {
        T t = threadLocal.get();
        if (t != null || cls == null) {
            return t;
        }
        T t2 = (T) ObjectPool.acquire(objectPool, cls, new Object[0]);
        threadLocal.set(t2);
        return t2;
    }

    /* JADX WARN: Undo finally extract visitor
    java.lang.NullPointerException: Cannot invoke "Object.hashCode()" because "this.second" is null
    	at jadx.core.utils.Pair.hashCode(Pair.java:35)
    	at java.base/java.util.HashMap.hash(Unknown Source)
    	at java.base/java.util.HashMap.getNode(Unknown Source)
    	at java.base/java.util.HashMap.containsKey(Unknown Source)
    	at jadx.core.dex.visitors.finaly.traverser.state.TraverserGlobalCommonState.hasBlocksBeenCached(TraverserGlobalCommonState.java:35)
    	at jadx.core.dex.visitors.finaly.traverser.handlers.MergePathActivePathTraverserHandler.handle(MergePathActivePathTraverserHandler.java:174)
    	at jadx.core.dex.visitors.finaly.traverser.handlers.AbstractActivePathTraverserHandler.process(AbstractActivePathTraverserHandler.java:19)
    	at jadx.core.dex.visitors.finaly.traverser.TraverserController.processHandlerImplementations(TraverserController.java:43)
    	at jadx.core.dex.visitors.finaly.traverser.TraverserController.advance(TraverserController.java:156)
    	at jadx.core.dex.visitors.finaly.traverser.TraverserController.process(TraverserController.java:79)
    	at jadx.core.dex.visitors.finaly.MarkFinallyVisitor.findCommonInsns(MarkFinallyVisitor.java:404)
    	at jadx.core.dex.visitors.finaly.MarkFinallyVisitor.extractFinally(MarkFinallyVisitor.java:284)
    	at jadx.core.dex.visitors.finaly.MarkFinallyVisitor.processTryBlock(MarkFinallyVisitor.java:202)
    	at jadx.core.dex.visitors.finaly.MarkFinallyVisitor.visit(MarkFinallyVisitor.java:135)
     */
    public static Bitmap compressImage(Bitmap bitmap, int i, int i2) {
        StringBuilder sb;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmapDecodeStream = null;
        try {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, i, byteArrayOutputStream);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = i2;
                bitmapDecodeStream = BitmapFactory.decodeStream(parseToInputStream(byteArrayOutputStream), null, options);
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e = e;
                    sb = new StringBuilder("IO close fail, ");
                    Log.i(TAG, sb.append(e).toString());
                }
            } catch (Exception e2) {
                Log.w(TAG, "TintDrawable.compressImage failed, " + e2);
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e3) {
                    e = e3;
                    sb = new StringBuilder("IO close fail, ");
                    Log.i(TAG, sb.append(e).toString());
                }
            }
            return bitmapDecodeStream;
        } catch (Throwable th) {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e4) {
                Log.i(TAG, "IO close fail, " + e4);
            }
            throw th;
        }
    }

    public static ByteArrayInputStream parseToInputStream(ByteArrayOutputStream byteArrayOutputStream) throws Exception {
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public static void timeStatBegin(String... strArr) {
        if (strArr == null || strArr.length == 0) {
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        for (String str : strArr) {
            if (TextUtils.isEmpty(str)) {
                sTimeStatArray.put(str, Long.valueOf(jCurrentTimeMillis));
            }
        }
    }

    public static long timeStatEnd(String str) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j = jCurrentTimeMillis - (sTimeStatArray.get(str) != null ? jCurrentTimeMillis : 0L);
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("打印当前用时： TAG=" + str + " 用时为 " + j, new Object[0]);
        }
        return j;
    }

    public static void timeStatClear() {
        sTimeStatArray.clear();
    }
}
