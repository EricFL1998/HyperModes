package com.android.deskclock.util;

import android.text.TextUtils;
import com.android.deskclock.addition.resource.ExternalResourceUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import miuix.core.util.IOUtils;

/* JADX INFO: loaded from: classes.dex */
public class FileUtil {
    /* JADX WARN: Code duplicated, block: B:44:0x00b7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v5, types: [java.util.zip.ZipFile] */
    public static void unzipFile(String str, String str2, String str3) throws Throwable {
        Throwable th;
        ZipFile zipFile;
        IOException e;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            Log.e(ExternalResourceUtils.TAG, "unzip failure: zipPath or unzipPath is null");
            return;
        }
        ?? r2 = ", keyword = ";
        Log.i(ExternalResourceUtils.TAG, "zipPath = " + str + ", toPath=" + str2 + ", keyword = " + str3);
        try {
            try {
                zipFile = new ZipFile(new File(str));
                try {
                    Enumeration<? extends ZipEntry> enumerationEntries = zipFile.entries();
                    while (enumerationEntries.hasMoreElements()) {
                        ZipEntry zipEntryNextElement = enumerationEntries.nextElement();
                        if (!zipEntryNextElement.isDirectory() && !zipEntryNextElement.getName().contains("../")) {
                            File file = new File(str2 + File.separator + zipEntryNextElement.getName());
                            if (TextUtils.isEmpty(str3) || zipEntryNextElement.getName().contains(str3)) {
                                checkToCopy(file, zipFile, zipEntryNextElement);
                            }
                        }
                    }
                    Log.i(ExternalResourceUtils.TAG, "unzip success");
                } catch (IOException e2) {
                    e = e2;
                    Log.e(ExternalResourceUtils.TAG, "unzip failure", e);
                    if (zipFile == null) {
                        return;
                    }
                }
            } catch (IOException e3) {
                zipFile = null;
                e = e3;
            } catch (Throwable th2) {
                r2 = 0;
                th = th2;
                if (r2 != 0) {
                    try {
                        r2.close();
                    } catch (IOException unused) {
                    }
                }
                throw th;
            }
            try {
                zipFile.close();
            } catch (IOException unused2) {
            }
        } catch (Throwable th3) {
            th = th3;
            if (r2 != 0) {
                r2.close();
            }
            throw th;
        }
    }

    private static void checkToCopy(File file, ZipFile zipFile, ZipEntry zipEntry) {
        InputStream inputStream = null;
        try {
            try {
                inputStream = zipFile.getInputStream(zipEntry);
                if (file.exists() && file.length() == inputStream.available()) {
                    Log.d("file exists file = " + file.getAbsolutePath());
                } else {
                    createNewFile(file);
                    copyToFile(zipFile.getInputStream(zipEntry), file);
                }
            } catch (Exception e) {
                Log.e("checkToCopy fail", e);
            }
        } finally {
            IOUtils.closeQuietly((InputStream) null);
        }
    }

    public static boolean createNewFile(File file) {
        if (file != null) {
            try {
                if (!file.exists()) {
                    if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                        Log.e("createNewFile fail, mkdirs fail, file=" + file.getAbsolutePath());
                        return false;
                    }
                    return file.createNewFile();
                }
            } catch (Exception e) {
                Log.e("createNewFile error", e);
                return false;
            }
        }
        Log.d("createNewFile file exist");
        return false;
    }

    public static boolean copyToFile(InputStream inputStream, File file) throws Throwable {
        if (inputStream == null || file == null || (file.exists() && !file.delete())) {
            return false;
        }
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
            try {
                byte[] bArr = new byte[1024];
                while (true) {
                    int i = inputStream.read(bArr);
                    if (i >= 0) {
                        fileOutputStream2.write(bArr, 0, i);
                    } else {
                        try {
                            break;
                        } catch (IOException unused) {
                        }
                    }
                }
                fileOutputStream2.flush();
                try {
                    fileOutputStream2.getFD().sync();
                } catch (IOException unused2) {
                }
                try {
                    fileOutputStream2.close();
                    return true;
                } catch (IOException unused3) {
                    return true;
                }
            } catch (Exception e) {
                e = e;
                fileOutputStream = fileOutputStream2;
                Log.e("copy file error", e);
                try {
                    fileOutputStream.flush();
                } catch (IOException unused4) {
                }
                try {
                    fileOutputStream.getFD().sync();
                } catch (IOException unused5) {
                }
                try {
                    fileOutputStream.close();
                } catch (IOException unused6) {
                }
                return false;
            } catch (Throwable th) {
                th = th;
                fileOutputStream = fileOutputStream2;
                try {
                    fileOutputStream.flush();
                } catch (IOException unused7) {
                }
                try {
                    fileOutputStream.getFD().sync();
                } catch (IOException unused8) {
                }
                try {
                    fileOutputStream.close();
                    throw th;
                } catch (IOException unused9) {
                    throw th;
                }
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public static boolean delete(String str) {
        File file = new File(str);
        if (!file.exists()) {
            Log.i("delete cancel: " + str + " not exist");
            return false;
        }
        if (file.isFile()) {
            return deleteFile(str);
        }
        return deleteDirectory(str);
    }

    public static boolean deleteFile(String str) {
        File file = new File(str);
        return file.exists() && file.isFile() && file.delete();
    }

    public static boolean deleteDirectory(String str) {
        boolean zDeleteDirectory;
        if (!str.endsWith(File.separator)) {
            str = str + File.separator;
        }
        File file = new File(str);
        if (file.exists() && file.isDirectory()) {
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles != null) {
                zDeleteDirectory = true;
                for (int i = 0; i < fileArrListFiles.length; i++) {
                    if (fileArrListFiles[i].isFile()) {
                        zDeleteDirectory = deleteFile(fileArrListFiles[i].getAbsolutePath());
                        if (!zDeleteDirectory) {
                            break;
                        }
                    } else {
                        if (fileArrListFiles[i].isDirectory() && !(zDeleteDirectory = deleteDirectory(fileArrListFiles[i].getAbsolutePath()))) {
                            break;
                        }
                    }
                }
            } else {
                zDeleteDirectory = true;
            }
            if (zDeleteDirectory && file.delete()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFileExist(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return new File(str).exists();
    }

    public static long writeFile(String str, byte[] bArr) throws Throwable {
        if (!createDirectoryIfNeeded(str)) {
            Log.e("Failed to create parent directory for file: " + str);
            return -1L;
        }
        FileOutputStream fileOutputStream = null;
        try {
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(str);
                try {
                    fileOutputStream2.write(bArr);
                    long length = bArr.length;
                    try {
                        fileOutputStream2.close();
                    } catch (Exception e) {
                        Log.e("Failed to close file after write", e);
                    }
                    return length;
                } catch (Exception e2) {
                    e = e2;
                    fileOutputStream = fileOutputStream2;
                    Log.e("Failed to write data", e);
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Exception e3) {
                            Log.e("Failed to close file after write", e3);
                        }
                    }
                    return -1L;
                } catch (Throwable th) {
                    th = th;
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Exception e4) {
                            Log.e("Failed to close file after write", e4);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e5) {
            e = e5;
        }
    }

    private static boolean createDirectoryIfNeeded(String str) {
        File parentFile = new File(str).getParentFile();
        if (parentFile.exists()) {
            return parentFile.isDirectory();
        }
        return parentFile.mkdirs();
    }

    /* JADX WARN: Code duplicated, block: B:67:0x004f A[EXC_TOP_SPLITTER, PHI: r1
  0x004f: PHI (r1v7 java.io.BufferedOutputStream) = (r1v6 java.io.BufferedOutputStream), (r1v8 java.io.BufferedOutputStream) binds: [B:37:0x004d, B:43:0x0059] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    public static boolean saveByteArray(byte[] bArr, String str) throws Throwable {
        BufferedOutputStream bufferedOutputStream;
        if (bArr != null && !TextUtils.isEmpty(str)) {
            File file = new File(str);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = null;
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                try {
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream2);
                    try {
                        bufferedOutputStream.write(bArr);
                        try {
                            fileOutputStream2.close();
                        } catch (IOException unused) {
                        }
                        try {
                            bufferedOutputStream.close();
                            return true;
                        } catch (IOException unused2) {
                            return true;
                        }
                    } catch (FileNotFoundException unused3) {
                        fileOutputStream = fileOutputStream2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException unused4) {
                            }
                        }
                        if (bufferedOutputStream != null) {
                            try {
                                bufferedOutputStream.close();
                            } catch (IOException unused5) {
                            }
                        }
                        return false;
                    } catch (IOException unused6) {
                        fileOutputStream = fileOutputStream2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException unused7) {
                            }
                        }
                        if (bufferedOutputStream != null) {
                            bufferedOutputStream.close();
                        }
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        fileOutputStream = fileOutputStream2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException unused8) {
                            }
                        }
                        if (bufferedOutputStream == null) {
                            throw th;
                        }
                        try {
                            bufferedOutputStream.close();
                            throw th;
                        } catch (IOException unused9) {
                            throw th;
                        }
                    }
                } catch (FileNotFoundException unused10) {
                    bufferedOutputStream = null;
                } catch (IOException unused11) {
                    bufferedOutputStream = null;
                } catch (Throwable th2) {
                    th = th2;
                    bufferedOutputStream = null;
                }
            } catch (FileNotFoundException unused12) {
                bufferedOutputStream = null;
            } catch (IOException unused13) {
                bufferedOutputStream = null;
            } catch (Throwable th3) {
                th = th3;
                bufferedOutputStream = null;
            }
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:107:0x0063 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:79:0x0077 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:81:0x0072 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:93:0x006d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:97:0x0068 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v2, types: [java.io.BufferedInputStream] */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r7v3 */
    /* JADX WARN: Type inference failed for: r7v4 */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v6, types: [java.io.BufferedInputStream] */
    /* JADX WARN: Type inference failed for: r7v8, types: [java.io.BufferedInputStream] */
    public static boolean copyFile(File file, File file2) throws Throwable {
        BufferedOutputStream bufferedOutputStream;
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        Exception e;
        ?? bufferedInputStream;
        ?? r1 = 0;
        r1 = 0;
        try {
            fileInputStream = new FileInputStream(file);
            try {
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                try {
                    fileOutputStream = new FileOutputStream(file2);
                    try {
                        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        try {
                            try {
                                byte[] bArr = new byte[1024];
                                while (true) {
                                    int i = bufferedInputStream.read(bArr);
                                    if (i != -1) {
                                        bufferedOutputStream.write(bArr, 0, i);
                                    } else {
                                        try {
                                            break;
                                        } catch (IOException unused) {
                                        }
                                    }
                                    th = th;
                                    r1 = bufferedInputStream;
                                    if (r1 != 0) {
                                        try {
                                            r1.close();
                                        } catch (IOException unused2) {
                                        }
                                    }
                                    if (bufferedOutputStream != null) {
                                        try {
                                            bufferedOutputStream.flush();
                                        } catch (IOException unused3) {
                                        }
                                    }
                                    if (bufferedOutputStream != null) {
                                        try {
                                            bufferedOutputStream.close();
                                        } catch (IOException unused4) {
                                        }
                                    }
                                    if (fileOutputStream != null) {
                                        try {
                                            fileOutputStream.close();
                                        } catch (IOException unused5) {
                                        }
                                    }
                                    if (fileInputStream != null) {
                                        try {
                                            fileInputStream.close();
                                            throw th;
                                        } catch (IOException unused6) {
                                            throw th;
                                        }
                                    }
                                    throw th;
                                }
                                bufferedInputStream.close();
                                try {
                                    bufferedOutputStream.flush();
                                } catch (IOException unused7) {
                                }
                                try {
                                    bufferedOutputStream.close();
                                } catch (IOException unused8) {
                                }
                                try {
                                    fileOutputStream.close();
                                } catch (IOException unused9) {
                                }
                                try {
                                    fileInputStream.close();
                                    return true;
                                } catch (IOException unused10) {
                                    return true;
                                }
                            } catch (Throwable th) {
                                th = th;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            Log.e("Failed to copy file", e);
                            if (bufferedInputStream != 0) {
                                try {
                                    bufferedInputStream.close();
                                } catch (IOException unused11) {
                                }
                            }
                            if (bufferedOutputStream != null) {
                                try {
                                    bufferedOutputStream.flush();
                                } catch (IOException unused12) {
                                }
                            }
                            if (bufferedOutputStream != null) {
                                try {
                                    bufferedOutputStream.close();
                                } catch (IOException unused13) {
                                }
                            }
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException unused14) {
                                }
                            }
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException unused15) {
                                }
                            }
                            return false;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        bufferedOutputStream = null;
                    } catch (Throwable th2) {
                        th = th2;
                        bufferedOutputStream = null;
                    }
                } catch (Exception e4) {
                    fileOutputStream = null;
                    e = e4;
                    bufferedOutputStream = null;
                } catch (Throwable th3) {
                    th = th3;
                    bufferedOutputStream = null;
                    fileOutputStream = null;
                }
            } catch (Exception e5) {
                e = e5;
                bufferedOutputStream = null;
                fileOutputStream = null;
                e = e;
                bufferedInputStream = fileOutputStream;
                Log.e("Failed to copy file", e);
                if (bufferedInputStream != 0) {
                    bufferedInputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.flush();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                return false;
            } catch (Throwable th4) {
                th = th4;
                bufferedOutputStream = null;
                fileOutputStream = null;
            }
        } catch (Exception e6) {
            e = e6;
            bufferedOutputStream = null;
            fileInputStream = null;
            fileOutputStream = null;
        } catch (Throwable th5) {
            th = th5;
            bufferedOutputStream = null;
            fileInputStream = null;
            fileOutputStream = null;
        }
    }

    public static void copyDirectory(String str, String str2) {
        File[] fileArrListFiles;
        File file = new File(str2);
        if (file.exists()) {
            deleteDirectory(str2);
        }
        file.mkdir();
        String absolutePath = file.getAbsolutePath();
        File file2 = new File(str);
        if (file2.exists() && file2.isDirectory() && (fileArrListFiles = file2.listFiles()) != null) {
            for (int i = 0; i < fileArrListFiles.length; i++) {
                if (fileArrListFiles[i].isFile()) {
                    copyFile(fileArrListFiles[i], new File(absolutePath + File.separator + fileArrListFiles[i].getName()));
                }
                if (fileArrListFiles[i].isDirectory()) {
                    copyDirectory(str + File.separator + fileArrListFiles[i].getName(), str2 + File.separator + fileArrListFiles[i].getName());
                }
            }
        }
    }
}
