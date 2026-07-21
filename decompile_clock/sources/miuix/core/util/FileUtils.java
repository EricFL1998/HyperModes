package miuix.core.util;

import android.system.ErrnoException;
import android.system.Os;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/* JADX INFO: loaded from: classes2.dex */
public class FileUtils {
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");
    public static final int S_IRGRP = 32;
    public static final int S_IROTH = 4;
    public static final int S_IRUSR = 256;
    public static final int S_IRWXG = 56;
    public static final int S_IRWXO = 7;
    public static final int S_IRWXU = 448;
    public static final int S_IWGRP = 16;
    public static final int S_IWOTH = 2;
    public static final int S_IWUSR = 128;
    public static final int S_IXGRP = 8;
    public static final int S_IXOTH = 1;
    public static final int S_IXUSR = 64;

    protected FileUtils() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static boolean sync(FileOutputStream fileOutputStream) {
        if (fileOutputStream == null) {
            return true;
        }
        try {
            fileOutputStream.getFD().sync();
            return true;
        } catch (IOException unused) {
            return false;
        }
    }

    public static boolean copyFile(File file, File file2) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                return copyToFile(fileInputStream, file2);
            } finally {
                fileInputStream.close();
            }
        } catch (IOException unused) {
            return false;
        }
    }

    public static boolean copyToFile(InputStream inputStream, File file) {
        try {
            if (file.exists() && !file.delete()) {
                return false;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int i = inputStream.read(bArr);
                    if (i < 0) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, i);
                }
                fileOutputStream.flush();
                try {
                    fileOutputStream.getFD().sync();
                } catch (IOException unused) {
                }
                try {
                    fileOutputStream.close();
                    return true;
                } catch (IOException unused2) {
                    return true;
                }
            } catch (Throwable th) {
                fileOutputStream.flush();
                try {
                    fileOutputStream.getFD().sync();
                } catch (IOException unused3) {
                }
                try {
                    fileOutputStream.close();
                    throw th;
                } catch (IOException unused4) {
                    throw th;
                }
            }
        } catch (IOException unused5) {
            return false;
        }
    }

    public static boolean isFilenameSafe(File file) {
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    public static String readTextFile(File file, int i, String str) throws IOException {
        int i2;
        boolean z;
        int i3;
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            long length = file.length();
            if (i > 0 || (length > 0 && i == 0)) {
                if (length > 0 && (i == 0 || length < i)) {
                    i = (int) length;
                }
                byte[] bArr = new byte[i + 1];
                int i4 = fileInputStream.read(bArr);
                if (i4 <= 0) {
                    fileInputStream.close();
                    return "";
                }
                if (i4 <= i) {
                    String str2 = new String(bArr, 0, i4);
                    fileInputStream.close();
                    return str2;
                }
                if (str == null) {
                    String str3 = new String(bArr, 0, i);
                    fileInputStream.close();
                    return str3;
                }
                String str4 = new String(bArr, 0, i) + str;
                fileInputStream.close();
                return str4;
            }
            if (i >= 0) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bArr2 = new byte[1024];
                do {
                    i2 = fileInputStream.read(bArr2);
                    if (i2 > 0) {
                        byteArrayOutputStream.write(bArr2, 0, i2);
                    }
                } while (i2 == 1024);
                String string = byteArrayOutputStream.toString();
                fileInputStream.close();
                return string;
            }
            byte[] bArr3 = null;
            byte[] bArr4 = null;
            boolean z2 = false;
            while (true) {
                z = true;
                if (bArr3 != null) {
                    z2 = true;
                }
                if (bArr3 == null) {
                    bArr3 = new byte[-i];
                }
                i3 = fileInputStream.read(bArr3);
                if (i3 != bArr3.length) {
                    break;
                }
                byte[] bArr5 = bArr4;
                bArr4 = bArr3;
                bArr3 = bArr5;
            }
            if (bArr4 == null && i3 <= 0) {
                fileInputStream.close();
                return "";
            }
            if (bArr4 == null) {
                String str5 = new String(bArr3, 0, i3);
                fileInputStream.close();
                return str5;
            }
            if (i3 > 0) {
                System.arraycopy(bArr4, i3, bArr4, 0, bArr4.length - i3);
                System.arraycopy(bArr3, 0, bArr4, bArr4.length - i3, i3);
            } else {
                z = z2;
            }
            if (str != null && z) {
                String str6 = str + new String(bArr4);
                fileInputStream.close();
                return str6;
            }
            String str7 = new String(bArr4);
            fileInputStream.close();
            return str7;
        } catch (Throwable th) {
            fileInputStream.close();
            throw th;
        }
    }

    public static void stringToFile(String str, String str2) throws IOException {
        FileWriter fileWriter = new FileWriter(str);
        try {
            fileWriter.write(str2);
        } finally {
            fileWriter.close();
        }
    }

    public static long checksumCrc32(File file) throws Throwable {
        CRC32 crc32 = new CRC32();
        CheckedInputStream checkedInputStream = null;
        try {
            CheckedInputStream checkedInputStream2 = new CheckedInputStream(new FileInputStream(file), crc32);
            try {
                while (checkedInputStream2.read(new byte[128]) >= 0) {
                }
                long value = crc32.getValue();
                try {
                    checkedInputStream2.close();
                } catch (IOException unused) {
                }
                return value;
            } catch (Throwable th) {
                th = th;
                checkedInputStream = checkedInputStream2;
                if (checkedInputStream != null) {
                    try {
                        checkedInputStream.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static String getExtension(String str) {
        int iLastIndexOf;
        if (str == null || str.length() == 0 || (iLastIndexOf = str.lastIndexOf(46)) <= -1) {
            return "";
        }
        return str.substring(iLastIndexOf + 1);
    }

    public static String getName(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        int iLastIndexOf = str.lastIndexOf(File.separatorChar);
        if (iLastIndexOf < 0) {
            iLastIndexOf = -1;
        }
        int iLastIndexOf2 = str.lastIndexOf(".");
        return iLastIndexOf2 < 0 ? str.substring(iLastIndexOf + 1) : str.substring(iLastIndexOf + 1, iLastIndexOf2);
    }

    public static String getFileName(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        int iLastIndexOf = str.lastIndexOf(File.separatorChar);
        return iLastIndexOf > -1 ? str.substring(iLastIndexOf + 1) : str;
    }

    public static boolean addNoMedia(String str) {
        File file = new File(str);
        if (!file.isDirectory()) {
            return false;
        }
        try {
            return new File(file, ".nomedia").createNewFile();
        } catch (IOException unused) {
            return false;
        }
    }

    public static String normalizeDirectoryName(String str) {
        return str.charAt(str.length() + (-1)) == File.separatorChar ? str : str + File.separator;
    }

    public static long getFolderSize(File file) {
        long folderSize = 0;
        if (!file.exists()) {
            return 0L;
        }
        if (file.isDirectory()) {
            File[] fileArrListFiles = file.listFiles();
            if (fileArrListFiles != null) {
                for (File file2 : fileArrListFiles) {
                    folderSize += getFolderSize(file2);
                }
            }
            return folderSize;
        }
        return file.length();
    }

    public static String readFileAsString(String str) throws IOException {
        return new String(readFileAsBytes(str), Charset.forName("UTF-8"));
    }

    private static byte[] readFileAsBytes(String str) throws Throwable {
        RandomAccessFile randomAccessFile = null;
        try {
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(str, "r");
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) randomAccessFile2.length());
                byte[] bArr = new byte[8192];
                while (true) {
                    int i = randomAccessFile2.read(bArr);
                    if (i == -1) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, i);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                try {
                    randomAccessFile2.close();
                } catch (IOException unused) {
                }
                return byteArray;
            } catch (Throwable th) {
                th = th;
                randomAccessFile = randomAccessFile2;
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean mkdirs(File file, int i, int i2, int i3) {
        boolean z;
        boolean z2;
        if (file.exists()) {
            return file.isDirectory();
        }
        if (!mkdirs(file.getParentFile(), i, i2, i3)) {
            return false;
        }
        boolean zMkdir = file.mkdir();
        try {
            Os.chmod(file.getPath(), i);
            try {
                Os.chown(file.getPath(), i2, i3);
                z2 = true;
                z = true;
            } catch (ErrnoException e) {
                e = e;
                z = true;
                e.printStackTrace();
                z2 = false;
            }
        } catch (ErrnoException e2) {
            e = e2;
            z = false;
        }
        return zMkdir && z && z2;
    }
}
