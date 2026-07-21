package androidx.constraintlayout.core.motion.utils;

import com.android.deskclock.R2;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;

/* JADX INFO: loaded from: classes.dex */
public class Utils {
    static DebugHandle ourHandle;

    public interface DebugHandle {
        void message(String str);
    }

    private static int clamp(int i) {
        int i2 = (i & (~(i >> 31))) - 255;
        return (i2 & (i2 >> 31)) + 255;
    }

    public static void log(String str, String str2) {
        System.out.println(str + " : " + str2);
    }

    public static void loge(String str, String str2) {
        System.err.println(str + " : " + str2);
    }

    public static void socketSend(String str) {
        try {
            OutputStream outputStream = new Socket("127.0.0.1", R2.dimen.clock_information_text_top).getOutputStream();
            outputStream.write(str.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInterpolatedColor(float[] fArr) {
        return (clamp((int) (fArr[3] * 255.0f)) << 24) | (clamp((int) (((float) Math.pow(fArr[0], 0.45454545454545453d)) * 255.0f)) << 16) | (clamp((int) (((float) Math.pow(fArr[1], 0.45454545454545453d)) * 255.0f)) << 8) | clamp((int) (((float) Math.pow(fArr[2], 0.45454545454545453d)) * 255.0f));
    }

    public static int rgbaTocColor(float f, float f2, float f3, float f4) {
        int iClamp = clamp((int) (f * 255.0f));
        int iClamp2 = clamp((int) (f2 * 255.0f));
        return (iClamp << 16) | (clamp((int) (f4 * 255.0f)) << 24) | (iClamp2 << 8) | clamp((int) (f3 * 255.0f));
    }

    public static void setDebugHandle(DebugHandle debugHandle) {
        ourHandle = debugHandle;
    }

    public static void logStack(String str, int i) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        int iMin = Math.min(i, stackTrace.length - 1);
        String str2 = " ";
        for (int i2 = 1; i2 <= iMin; i2++) {
            StackTraceElement stackTraceElement = stackTrace[i2];
            str2 = str2 + " ";
            System.out.println(str + str2 + (".(" + stackTrace[i2].getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTrace[i2].getLineNumber() + ") " + stackTrace[i2].getMethodName()) + str2);
        }
    }

    public static void log(String str) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        String str2 = ".(" + stackTraceElement.getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTraceElement.getLineNumber() + ")" + "    ".substring(Integer.toString(stackTraceElement.getLineNumber()).length()) + (stackTraceElement.getMethodName() + "                  ").substring(0, 17);
        System.out.println(str2 + " " + str);
        DebugHandle debugHandle = ourHandle;
        if (debugHandle != null) {
            debugHandle.message(str2 + " " + str);
        }
    }
}
