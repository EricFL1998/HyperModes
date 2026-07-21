package miuix.module.core;

import android.util.ArrayMap;
import android.util.Log;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
class DependencyLoader extends ClassLoader {
    private static final String TAG = "DependencyLoader";
    private Map<String, ClassLoader> mDependencies;
    private Map<String, Class<?>> mDependencyClasses;
    private Set<String> mIgnoredClasses;

    public DependencyLoader(ClassLoader classLoader) {
        super(classLoader);
        this.mDependencyClasses = new ArrayMap();
        this.mDependencies = new ArrayMap();
        this.mIgnoredClasses = new HashSet();
    }

    @Override // java.lang.ClassLoader
    protected Class<?> loadClass(String str, boolean z) throws ClassNotFoundException {
        Log.d(TAG, "loading class: " + str);
        Class<?> cls = this.mDependencyClasses.get(str);
        if (cls != null) {
            return cls;
        }
        ClassLoader classLoader = this.mDependencies.get(str);
        if (classLoader != null) {
            return classLoader.loadClass(str);
        }
        if (this.mIgnoredClasses.contains(str)) {
            return null;
        }
        return super.loadClass(str, z);
    }

    void add(ClassLoader classLoader, String... strArr) {
        for (String str : strArr) {
            this.mDependencies.put(str, classLoader);
        }
    }

    void preload(Class<?>... clsArr) {
        for (Class<?> cls : clsArr) {
            this.mDependencyClasses.put(cls.getCanonicalName(), cls);
        }
    }

    void ignore(String str) {
        this.mIgnoredClasses.add(str);
    }
}
