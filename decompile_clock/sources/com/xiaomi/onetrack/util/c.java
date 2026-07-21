package com.xiaomi.onetrack.util;

import android.text.TextUtils;
import java.io.File;
import java.io.FilenameFilter;

/* JADX INFO: loaded from: classes2.dex */
final class c implements FilenameFilter {
    c() {
    }

    @Override // java.io.FilenameFilter
    public boolean accept(File file, String str) {
        return TextUtils.isDigitsOnly(str);
    }
}
