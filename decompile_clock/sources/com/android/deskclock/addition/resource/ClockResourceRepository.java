package com.android.deskclock.addition.resource;

import android.content.Context;
import com.android.deskclock.util.Log;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class ClockResourceRepository extends ResourceRepository {
    private static final String MIUI_DATA_RECOMMENDED_APPS_OUT = "/data/miui/app/recommended/";
    private static final String TAG = "DC:ClockResourceRepository";

    public ClockResourceRepository(Context context) {
        super(context);
    }

    @Override // com.android.deskclock.addition.resource.ResourceRepository
    protected File getRootFolder(Context context) {
        return new File(MIUI_DATA_RECOMMENDED_APPS_OUT);
    }

    @Override // com.android.deskclock.addition.resource.ResourceRepository
    protected File getModuleFile(File file, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(file.getAbsolutePath()).append(File.separator).append(str).append(File.separator);
        String string = sb.toString();
        Log.i(TAG, "Repository: " + string);
        return new File(string, str + ResourceRepository.MODULE_FILE_EXTENSION);
    }
}
