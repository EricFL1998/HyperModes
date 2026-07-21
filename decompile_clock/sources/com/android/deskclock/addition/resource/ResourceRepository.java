package com.android.deskclock.addition.resource;

import android.content.Context;
import com.android.deskclock.util.Log;
import java.io.File;
import java.lang.reflect.Field;
import miui.module.Repository;

/* JADX INFO: loaded from: classes.dex */
public abstract class ResourceRepository extends Repository {
    public static final String MODULE_FILE_EXTENSION = ".apk";
    private static final String TAG = "DC:ResourceRepository";
    private Context mContext;

    protected abstract File getModuleFile(File file, String str);

    protected abstract File getRootFolder(Context context);

    public ResourceRepository(Context context) {
        this.mContext = context;
    }

    private boolean createSymlink(File file, File file2) {
        try {
            Field declaredField = Class.forName("libcore.io.Libcore").getDeclaredField("os");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(null);
            obj.getClass().getMethod("symlink", String.class, String.class).invoke(obj, file.getAbsolutePath(), file2.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.e("ResourceRepository", "error in createSymlink: " + e.toString());
            return false;
        }
    }

    public boolean contains(String str) {
        return getModuleFile(getRootFolder(this.mContext), str).exists();
    }

    public String fetch(File file, String str) {
        File moduleFile = getModuleFile(getRootFolder(this.mContext), str);
        if (!moduleFile.exists()) {
            return null;
        }
        File file2 = new File(file, str + MODULE_FILE_EXTENSION);
        file2.delete();
        if (createSymlink(moduleFile, file2)) {
            return file2.getName();
        }
        return null;
    }
}
