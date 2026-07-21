package miuix.os;

import android.util.Log;
import java.io.IOException;
import java.util.Locale;
import miuix.core.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ProcessUtils {
    private static final String TAG = "ProcessUtils";

    protected ProcessUtils() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static String getProcessNameByPid(int i) {
        String str = String.format(Locale.US, "/proc/%d/cmdline", Integer.valueOf(i));
        try {
            String fileAsString = FileUtils.readFileAsString(str);
            if (fileAsString == null) {
                return null;
            }
            int iIndexOf = fileAsString.indexOf(0);
            return iIndexOf >= 0 ? fileAsString.substring(0, iIndexOf) : fileAsString;
        } catch (IOException e) {
            Log.e(TAG, "Fail to read cmdline: " + str, e);
            return null;
        }
    }
}
