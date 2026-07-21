package com.airbnb.lottie.network;

import android.util.Pair;
import com.airbnb.lottie.utils.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class NetworkCache {
    private final LottieNetworkCacheProvider cacheProvider;

    public NetworkCache(LottieNetworkCacheProvider lottieNetworkCacheProvider) {
        this.cacheProvider = lottieNetworkCacheProvider;
    }

    public void clear() {
        File fileParentDir = parentDir();
        if (fileParentDir.exists()) {
            File[] fileArrListFiles = fileParentDir.listFiles();
            if (fileArrListFiles != null && fileArrListFiles.length > 0) {
                for (File file : fileParentDir.listFiles()) {
                    file.delete();
                }
            }
            fileParentDir.delete();
        }
    }

    Pair<FileExtension, InputStream> fetch(String str) {
        FileExtension fileExtension;
        try {
            File cachedFile = getCachedFile(str);
            if (cachedFile == null) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(cachedFile);
            if (cachedFile.getAbsolutePath().endsWith(".zip")) {
                fileExtension = FileExtension.ZIP;
            } else {
                fileExtension = FileExtension.JSON;
            }
            Logger.debug("Cache hit for " + str + " at " + cachedFile.getAbsolutePath());
            return new Pair<>(fileExtension, fileInputStream);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    File writeTempCacheFile(String str, InputStream inputStream, FileExtension fileExtension) throws IOException {
        File file = new File(parentDir(), filenameForUrl(str, fileExtension, true));
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                byte[] bArr = new byte[1024];
                while (true) {
                    int i = inputStream.read(bArr);
                    if (i != -1) {
                        fileOutputStream.write(bArr, 0, i);
                    } else {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        inputStream.close();
                        return file;
                    }
                }
            } catch (Throwable th) {
                fileOutputStream.close();
                throw th;
            }
        } catch (Throwable th2) {
            inputStream.close();
            throw th2;
        }
    }

    void renameTempFile(String str, FileExtension fileExtension) {
        File file = new File(parentDir(), filenameForUrl(str, fileExtension, true));
        File file2 = new File(file.getAbsolutePath().replace(".temp", ""));
        boolean zRenameTo = file.renameTo(file2);
        Logger.debug("Copying temp file to real file (" + file2 + ")");
        if (zRenameTo) {
            return;
        }
        Logger.warning("Unable to rename cache file " + file.getAbsolutePath() + " to " + file2.getAbsolutePath() + ".");
    }

    private File getCachedFile(String str) throws FileNotFoundException {
        File file = new File(parentDir(), filenameForUrl(str, FileExtension.JSON, false));
        if (file.exists()) {
            return file;
        }
        File file2 = new File(parentDir(), filenameForUrl(str, FileExtension.ZIP, false));
        if (file2.exists()) {
            return file2;
        }
        return null;
    }

    private File parentDir() {
        File cacheDir = this.cacheProvider.getCacheDir();
        if (cacheDir.isFile()) {
            cacheDir.delete();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    private static String filenameForUrl(String str, FileExtension fileExtension, boolean z) {
        return "lottie_cache_" + str.replaceAll("\\W+", "") + (z ? fileExtension.tempExtension() : fileExtension.extension);
    }
}
