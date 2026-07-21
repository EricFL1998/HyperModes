package com.miui.miwallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class MiuiWallpaperFileUtils {
    private static final String TAG = "MiuiWallpaperFileUtils";
    public static final int WALLPAPER_FILE_PERMISSION_RX = 509;
    public static final int WALLPAPER_PERMISSION_RWX = 511;

    public static void copyFile(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            Log.e(TAG, "copyFile fail, param error: oriPath = " + str + ", copyPath = " + str2);
            return;
        }
        if (new File(str2).exists()) {
            removeFile(str2);
        }
        try {
            Files.copy(Paths.get(str, new String[0]), Paths.get(str2, new String[0]), StandardCopyOption.REPLACE_EXISTING);
            chmod(str2, 511);
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy( " + str + " to + " + str2 + " ): " + e);
        }
    }

    public static void copyStreamToWallpaperFile(InputStream inputStream, String str) {
        if (inputStream == null || str == null) {
            Log.e(TAG, "copyStreamToWallpaperFile fail, param error: inputStream = " + inputStream + ", outPath = " + str);
            return;
        }
        File file = new File(str);
        if (file.exists()) {
            removeFile(str);
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                Class<?> cls = Class.forName("android.os.FileUtils");
                cls.getMethod("copy", InputStream.class, OutputStream.class).invoke(cls, inputStream, fileOutputStream);
                chmod(str, 511);
                fileOutputStream.close();
            } catch (Throwable th) {
                try {
                    fileOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            Log.e(TAG, "WriteToFile fail : ", e);
        }
    }

    public static void writeToFile(Bitmap bitmap, String str) {
        if (bitmap == null) {
            Log.e(TAG, "writeToFile fail, bitmap is null");
            return;
        }
        File file = new File(str);
        file.delete();
        if (file.getParent() != null) {
            File file2 = new File(file.getParent());
            if (!file2.exists()) {
                mkdirs(file2, 511);
                Log.d(TAG, "writeToFile, path = " + file.getAbsolutePath());
                chmod(file2, 511);
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Throwable th) {
                try {
                    fileOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            Log.e(TAG, "WriteToFile fail : ", e);
        }
        chmod(str, 511);
    }

    public static Bitmap readFromFile(ParcelFileDescriptor parcelFileDescriptor) {
        if (parcelFileDescriptor == null) {
            return null;
        }
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor));
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bArr = new byte[2048];
                while (bufferedInputStream.read(bArr) != -1) {
                    byteArrayOutputStream.write(bArr);
                }
                Bitmap bitmapDecodeBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(byteArrayOutputStream.toByteArray()), new ImageDecoder.OnHeaderDecodedListener() { // from class: com.miui.miwallpaper.MiuiWallpaperFileUtils$$ExternalSyntheticLambda0
                    @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                        imageDecoder.setMutableRequired(true);
                    }
                });
                bufferedInputStream.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                try {
                    bufferedInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            Log.w(TAG, "Can't decode file", e);
            return null;
        }
    }

    public static Bitmap readFromFile(ParcelFileDescriptor parcelFileDescriptor, Bitmap bitmap) {
        if (parcelFileDescriptor == null) {
            return null;
        }
        try {
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            if (bitmap != null && !bitmap.isRecycled() && bitmap.isMutable() && options.outWidth <= bitmap.getWidth() && options.outHeight <= bitmap.getHeight()) {
                options.inBitmap = bitmap;
            }
            options.inJustDecodeBounds = false;
            options.inMutable = true;
            return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        } catch (Exception e) {
            Log.w(TAG, "Can't decode file", e);
            return null;
        }
    }

    public static boolean createNewFile(File file, int i) {
        boolean zCreateNewFile;
        if (file.exists()) {
            Log.i(TAG, "createNewFile file already exists");
        }
        if (file.getParentFile() != null && !file.getParentFile().isDirectory()) {
            mkdirs(file.getParentFile(), 511);
        }
        try {
            zCreateNewFile = file.createNewFile();
            try {
                chmod(file, i);
            } catch (IOException e) {
                e = e;
                Log.e(TAG, "createNewFile fail", e);
            }
        } catch (IOException e2) {
            e = e2;
            zCreateNewFile = false;
        }
        return zCreateNewFile;
    }

    public static void move(String str, String str2) {
        try {
            new File(str).renameTo(new File(str2));
        } catch (Exception e) {
            Log.e(TAG, "move fail : ", e);
        }
    }

    public static void removeFile(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            removeFile(list.get(i));
        }
    }

    public static void removeFile(String str) {
        try {
            File file = new File(str);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "removeFile failed: ", e);
        }
    }

    public static boolean mkdirs(File file, int i) {
        if (file.exists()) {
            return file.isDirectory();
        }
        boolean zMkdir = false;
        if (!mkdirs(file.getParentFile(), i)) {
            return false;
        }
        try {
            zMkdir = file.mkdir();
            Log.d(TAG, "mkdirs::result = " + zMkdir + " file = " + file);
            chmod(file, i);
            return zMkdir;
        } catch (Exception e) {
            Log.e(TAG, "mkdirs failed: ", e);
            return zMkdir;
        }
    }

    public static void deleteFolder(File file) {
        try {
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles != null) {
                for (File file2 : fileArrListFiles) {
                    deleteFolder(file2);
                }
            }
            file.delete();
        } catch (Exception e) {
            Log.e(TAG, "delete folder failed " + e);
        }
    }

    private static boolean chmod(File file, int i) {
        if (!file.exists()) {
            Log.e(TAG, "chmod param error file does not exist: file = " + file);
            return false;
        }
        return chmod(file.getPath(), i);
    }

    public static boolean chmod(String str, int i) {
        if (str.length() < 1) {
            Log.e(TAG, "chmod param error: path = " + str);
            return false;
        }
        try {
            Os.chmod(str, i);
            return true;
        } catch (ErrnoException e) {
            Log.e(TAG, "chmod fail: mode = " + i, e);
            return false;
        }
    }
}
