package miui.settings.commonlib;

import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/* JADX INFO: loaded from: classes2.dex */
public class DeviceUtils {
    private static final int BUFFE_READER_SIZE = 8192;
    private static final String TAG = "Utils";
    private static int mTotalRamStr;

    public static boolean isMIUILite() {
        return getTotalRam() <= 4;
    }

    private static int getTotalRam() {
        int i = mTotalRamStr;
        if (i != 0) {
            return i;
        }
        int ramFromProcMv = getRamFromProcMv();
        mTotalRamStr = ramFromProcMv;
        return ramFromProcMv;
    }

    private static int getRamFromProcMv() {
        String[] strArrSplit;
        String str;
        try {
            FileReader fileReader = new FileReader("proc/mv");
            try {
                BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
                try {
                    if (!new File("proc/mv").exists()) {
                        Log.i(TAG, "proc/mv not exist");
                        bufferedReader.close();
                        fileReader.close();
                        return 0;
                    }
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            bufferedReader.close();
                            fileReader.close();
                            break;
                        }
                        if (!TextUtils.isEmpty(line) && line.startsWith("D:") && (strArrSplit = line.split(" ")) != null && strArrSplit.length >= 3 && (str = strArrSplit[2]) != null && TextUtils.isDigitsOnly(str)) {
                            try {
                                int i = Integer.parseInt(str);
                                bufferedReader.close();
                                fileReader.close();
                                return i;
                            } catch (Exception unused) {
                                continue;
                            }
                        }
                    }
                    return 0;
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                        throw th2;
                    }
                }
            } catch (Throwable th4) {
                try {
                    throw th4;
                } catch (Throwable th5) {
                    try {
                        fileReader.close();
                    } catch (Throwable th6) {
                        th4.addSuppressed(th6);
                    }
                    throw th5;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int formatSizeWith1024(long j) {
        float f = j;
        if (f > 921.6d) {
            float f2 = f / 1024.0f;
            if (f2 > 921.6d) {
                float f3 = f2 / 1024.0f;
                if (f3 > 921.6d) {
                    return (int) Math.ceil(f3 / 1024.0f);
                }
            }
        }
        return 0;
    }
}
