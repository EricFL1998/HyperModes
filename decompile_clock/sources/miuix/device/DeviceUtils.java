package miuix.device;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import dalvik.system.PathClassLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class DeviceUtils {
    static final String ARCHITECTURE = "CPU architecture";
    static final int ARM_V8 = 8;
    static final int BIG_CORE_FREQ = 2000000;
    static final int CORE_COUNT = 8;
    static final String CpuMaxInfoFormat = "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq";
    static final int D800 = 73;
    public static final int DEVICE_HIGHEND = 2;
    public static final int DEVICE_MIDDLE = 1;
    public static final int DEVICE_PRIMARY = 0;
    public static final int DEVICE_UNKNOWN = -1;
    static final String HEX = "0x";
    private static int HIGH = 0;
    static final int HIGH_FREQ = 2700000;
    static final String IMPLEMENTOR = "CPU implementer";
    private static int LOW = 0;
    private static int MIDDLE = 0;
    static final int MIDDLE_FREQ = 2300000;
    public static final int MIUI_FLAG_VERSION_NULL = -1;
    public static final int MIUI_FLAG_VERSION_V1 = 1;
    public static final int MIUI_FLAG_VERSION_V2 = 2;
    static final int MTK_DIMENSITY = 68;
    static final String OLED_SCREEN_PROPERTY = "oled";
    private static float PAD_THRESHOLD = 0.0f;
    static final String PART = "CPU part";
    private static final String PERFORMANCE_CLASS = "com.miui.performance.DeviceLevelUtils";
    private static final String PERFORMANCE_COMPU_CLASS = "com.miui.performance.ComputilityLevelUtils";
    private static final String PERFORMANCE_JAR = "/system/framework/MiuiBooster.jar";
    private static final String PERFORMANCE_JAR_EXT = "/system_ext/framework/MiuiBooster.jar";
    static final String PROCESSOR = "processor";
    static final String QUALCOMM = "Qualcomm";
    static final String SEPARATOR = ": ";
    static final String SNAPDRAGON1 = "msm";
    static final String SNAPDRAGON2 = "sdm";
    static final String SNAPDRAGON3 = "sm";
    static final String TAG = "DeviceUtils";
    private static int UNKNOWN;
    private static Application application;
    private static Context applicationContext;
    private static Constructor<Class> mCompuConstructor;
    private static Constructor<Class> mConstructor;
    private static Method mGetCompuVersion;
    private static Method mGetCurrentCompuLevel;
    private static Method mGetMiuiLiteVersion;
    private static Method mGetMiuiMiddleVersion;
    private static Method mInitCompuLevel;
    private static Class sPerfClass;
    private static PathClassLoader sPerfClassLoader;
    private static Class sPerfCompuClass;
    private static Point sScreenRealSize;
    static final Pattern SM_PATTERN = Pattern.compile("Inc ([A-Z]+)([\\d]+)");
    static final Pattern MT_PATTERN = Pattern.compile("MT([\\d]{2})([\\d]+)");
    static Boolean MIUI_LITE_ROM = null;
    static int MIUI_LITE_VERSION = -2;
    static int MIUI_MIDDLE_VERSION = -2;
    static Boolean MIUI_LITE_STOCK_PLUS = null;
    private static int sMultiDisplayType = -1;
    private static Boolean sIsTablet = null;
    private static Boolean sIsPrimaryScreenOled = null;
    private static Boolean sIsSecondaryScreenOled = null;
    static int mLevel = -1;
    static int mCpuLevel = -1;
    static int mGpuLevel = -1;
    static int mRamLevel = -1;
    static int mTotalRam = Integer.MAX_VALUE;
    private static final String[] STOCK_DEVICE = {"cactus", "cereus", "pine", "olive", "ginkgo", "olivelite", "olivewood", "willow", "wayne", "dandelion", "angelica", "angelicain", "whyred", "tulip", "onc", "onclite", "lavender", "lotus", "laurus", "merlinnfc", "merlin", "lancelot", "citrus", "pomelo", "lemon", "shiva", "lime", "cannon", "curtana", "durandal", "excalibur", "joyeuse", "gram", "sunny", "mojito", "rainbow", "cattail", "angelican", "camellia"};
    private static Object mPerf = null;
    private static Object mCompuPerf = null;
    private static Method mGetDeviceLevel = null;
    private static Method mGetDeviceLevelForWhole = null;
    private static Method mIsSupportPrune = null;
    static int mCompuLevel = -1;
    static int mCompuVersion = -1;
    public static int DEV_STANDARD_VERSION = 1;
    private static int mLastVersion = 1;
    private static boolean initDeviceLevelInfo = false;
    public static int TYPE_RAM = 1;
    public static int TYPE_CPU = 2;
    public static int TYPE_GPU = 3;

    static {
        mConstructor = null;
        mCompuConstructor = null;
        mGetMiuiLiteVersion = null;
        mGetMiuiMiddleVersion = null;
        mInitCompuLevel = null;
        mGetCompuVersion = null;
        mGetCurrentCompuLevel = null;
        try {
            if (Build.VERSION.SDK_INT > 33) {
                PathClassLoader pathClassLoader = new PathClassLoader(PERFORMANCE_JAR_EXT, ClassLoader.getSystemClassLoader());
                sPerfClassLoader = pathClassLoader;
                sPerfClass = pathClassLoader.loadClass(PERFORMANCE_CLASS);
            } else {
                PathClassLoader pathClassLoader2 = new PathClassLoader(PERFORMANCE_JAR, ClassLoader.getSystemClassLoader());
                sPerfClassLoader = pathClassLoader2;
                sPerfClass = pathClassLoader2.loadClass(PERFORMANCE_CLASS);
            }
            mConstructor = sPerfClass.getConstructor(Context.class);
            mGetMiuiLiteVersion = sPerfClass.getDeclaredMethod("getMiuiLiteVersion", new Class[0]);
        } catch (Exception e) {
            Log.e(TAG, "static init(): Load Class Exception:" + e);
        }
        PathClassLoader pathClassLoader3 = sPerfClassLoader;
        if (pathClassLoader3 != null) {
            try {
                Class clsLoadClass = pathClassLoader3.loadClass(PERFORMANCE_COMPU_CLASS);
                sPerfCompuClass = clsLoadClass;
                mCompuConstructor = clsLoadClass.getConstructor(new Class[0]);
                mInitCompuLevel = sPerfCompuClass.getDeclaredMethod("initComputilityLevel", new Class[0]);
                mGetCompuVersion = sPerfCompuClass.getDeclaredMethod("getComputilityVersion", new Class[0]);
                mGetCurrentCompuLevel = sPerfCompuClass.getDeclaredMethod("getComputilityLevel", new Class[0]);
            } catch (Exception e2) {
                Log.e(TAG, "static init: ComputilityLevel Load Class Exception:" + e2);
            }
        }
        try {
            mGetMiuiMiddleVersion = sPerfClass.getDeclaredMethod("getMiuiMiddleVersion", new Class[0]);
        } catch (Exception e3) {
            Log.e(TAG, "static init(): Load MiuiMiddle Class Exception:" + e3);
        }
        if (sPerfClass == null) {
            Log.e(TAG, "static init(): MiuiBooster is not in this rom");
        }
    }

    public static class CpuInfo {
        int architecture;
        int id;
        int implementor;
        int maxFreq;
        int part;

        public String toString() {
            return "CpuInfo{id=" + this.id + ", implementor=" + Integer.toHexString(this.implementor) + ", architecture=" + this.architecture + ", part=" + Integer.toHexString(this.part) + ", maxFreq=" + this.maxFreq + '}';
        }
    }

    public static class CpuStats {
        int bigCoreCount;
        int level = -1;
        int maxFreq;
        int smallCoreCount;

        public String toString() {
            return "CpuStats{level=" + this.level + ", maxFreq=" + this.maxFreq + ", bigCoreCount=" + this.bigCoreCount + ", smallCoreCount=" + this.smallCoreCount + '}';
        }
    }

    public static int getTotalRam() {
        if (mTotalRam == Integer.MAX_VALUE) {
            try {
                mTotalRam = (int) (((((Long) Class.forName("miui.util.HardwareInfo").getMethod("getTotalPhysicalMemory", new Class[0]).invoke(null, new Object[0])).longValue() / 1024) / 1024) / 1024);
            } catch (Throwable th) {
                Log.e(TAG, th.getMessage());
                mTotalRam = 0;
            }
        }
        return mTotalRam;
    }

    public static boolean isMiuiLiteRom() {
        if (MIUI_LITE_ROM == null) {
            try {
                MIUI_LITE_ROM = Boolean.valueOf(((Boolean) Class.forName("miui.os.Build").getDeclaredField("IS_MIUI_LITE_VERSION").get(null)).booleanValue());
            } catch (Throwable th) {
                Log.i(TAG, "isMiuiLiteRom failed", th);
                MIUI_LITE_ROM = null;
            }
        }
        return Boolean.TRUE.equals(MIUI_LITE_ROM);
    }

    public static boolean isMiuiLiteV1() {
        return isMiuiLiteRom() && getMiuiLiteVersion() < 2;
    }

    public static boolean isMiuiLiteV2() {
        return isMiuiLiteRom() && getMiuiLiteVersion() >= 2;
    }

    public static int getMiuiLiteVersion() {
        int iIntValue;
        if (!isMiuiLiteRom()) {
            MIUI_LITE_VERSION = 0;
            return 0;
        }
        int i = MIUI_LITE_VERSION;
        if (i != -2) {
            return i;
        }
        try {
            Object perf = getPerf();
            if (perf != null) {
                iIntValue = ((Integer) getMiuiLiteVersionMethod().invoke(perf, new Object[0])).intValue();
                if (iIntValue >= 2) {
                    MIUI_LITE_VERSION = iIntValue;
                } else {
                    MIUI_LITE_VERSION = 1;
                }
                return MIUI_LITE_VERSION;
            }
            throw new Exception("perf is null!");
        } catch (Exception e) {
            Log.e(TAG, "getMiuiLiteVersion failed , e:" + e.toString());
            iIntValue = -1;
        }
    }

    public static boolean isMiuiMiddle() {
        return getMiuiMiddleVersion() >= 1;
    }

    public static int getMiuiMiddleVersion() {
        if (MIUI_MIDDLE_VERSION == -2) {
            try {
                Object objInvoke = mGetMiuiMiddleVersion.invoke(getPerf(), new Object[0]);
                if (objInvoke != null) {
                    MIUI_MIDDLE_VERSION = ((Integer) objInvoke).intValue();
                } else {
                    MIUI_MIDDLE_VERSION = -1;
                }
            } catch (Exception e) {
                MIUI_MIDDLE_VERSION = -1;
                Log.e(TAG, "getMiuiMiddleVersion failed , e:" + e.toString());
            }
        }
        return MIUI_MIDDLE_VERSION;
    }

    public static int getDeviceLevel() {
        return getDeviceLevel(DEV_STANDARD_VERSION);
    }

    public static int getDeviceLevel(int i) {
        int i2;
        if (mLastVersion == i && (i2 = mLevel) != -1) {
            return i2;
        }
        mLastVersion = i;
        int deviceLevel2 = getDeviceLevel2(i);
        mLevel = deviceLevel2;
        return deviceLevel2 != -1 ? deviceLevel2 : getDeviceLevel1();
    }

    public static int getDeviceLevel(int i, int i2) {
        int i3;
        int i4;
        int i5;
        if (i2 == TYPE_CPU) {
            if (mLastVersion == i && (i5 = mCpuLevel) != -1) {
                return i5;
            }
        } else if (i2 == TYPE_GPU) {
            if (mLastVersion == i && (i4 = mGpuLevel) != -1) {
                return i4;
            }
        } else if (i2 == TYPE_RAM && mLastVersion == i && (i3 = mRamLevel) != -1) {
            return i3;
        }
        int deviceLevel2 = getDeviceLevel2(i, i2);
        if (deviceLevel2 != -1) {
            return setDeviceLevel(i, deviceLevel2, i2);
        }
        return setDeviceLevel(i, getDeviceLevel1(i2), i2);
    }

    public static boolean isLiteV1Stock() {
        String productDevice = getProductDevice();
        if (productDevice != null && productDevice.length() != 0) {
            for (String str : STOCK_DEVICE) {
                if (str.equalsIgnoreCase(productDevice)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isLiteV1StockPlus() {
        if (MIUI_LITE_STOCK_PLUS == null) {
            MIUI_LITE_STOCK_PLUS = Boolean.valueOf("true".contentEquals(LiteSystemProperties.get("ro.config.low_ram.support_miuilite_plus", "false")));
        }
        return MIUI_LITE_STOCK_PLUS.booleanValue();
    }

    public static String getProductDevice() {
        return Build.DEVICE;
    }

    public static boolean isTablet() {
        if (sIsTablet == null) {
            sIsTablet = Boolean.valueOf(LiteSystemProperties.get("ro.build.characteristics", "").contains("tablet"));
        }
        return sIsTablet.booleanValue();
    }

    public static boolean isTablet(Context context) {
        if (sScreenRealSize == null || isFoldDevice()) {
            Point point = new Point();
            sScreenRealSize = point;
            ScreenUtils.getScreenSize(context, point);
            PAD_THRESHOLD = context.getResources().getDisplayMetrics().density * 600.0f;
        }
        return ((float) Math.min(sScreenRealSize.x, sScreenRealSize.y)) >= PAD_THRESHOLD;
    }

    public static boolean isWideScreen(Context context) {
        if (sScreenRealSize == null || isFoldDevice()) {
            Point point = new Point();
            sScreenRealSize = point;
            ScreenUtils.getScreenSize(context, point);
            PAD_THRESHOLD = context.getResources().getDisplayMetrics().density * 600.0f;
        }
        return ((float) Math.min(sScreenRealSize.x, sScreenRealSize.y)) >= PAD_THRESHOLD;
    }

    public static boolean isFoldDevice() {
        if (sMultiDisplayType == -1) {
            sMultiDisplayType = LiteSystemProperties.getInt("persist.sys.muiltdisplay_type", 1);
        }
        return sMultiDisplayType == 2;
    }

    public static boolean isExternalScreen(Context context) {
        return isFoldDevice() && !isWideScreen(context);
    }

    @Deprecated
    public static boolean isInternalScreen(Context context) {
        return isFoldDevice() && isWideScreen(context);
    }

    public static boolean isOled() {
        return isOled(true);
    }

    public static boolean isOled(boolean z) {
        if (z) {
            if (sIsPrimaryScreenOled == null) {
                sIsPrimaryScreenOled = Boolean.valueOf(isScreenOled());
            }
            return Boolean.TRUE.equals(sIsPrimaryScreenOled);
        }
        if (sIsSecondaryScreenOled == null) {
            sIsSecondaryScreenOled = Boolean.valueOf(isScreenOled());
        }
        return Boolean.TRUE.equals(sIsSecondaryScreenOled);
    }

    public static boolean isSupportPrune() {
        try {
            Object perf = getPerf();
            if (perf != null) {
                if (mIsSupportPrune != null) {
                    return false;
                }
                Method declaredMethod = getPerfClass().getDeclaredMethod("isSupportPrune", new Class[0]);
                mIsSupportPrune = declaredMethod;
                return ((Boolean) declaredMethod.invoke(perf, new Object[0])).booleanValue();
            }
            throw new Exception("perf is null!");
        } catch (Exception e) {
            Log.e(TAG, "isSupportPrune failed , e:" + e);
            return false;
        }
    }

    private static int setDeviceLevel(int i, int i2, int i3) {
        mLastVersion = i;
        if (i3 == TYPE_CPU) {
            mCpuLevel = i2;
            return i2;
        }
        if (i3 == TYPE_GPU) {
            mGpuLevel = i2;
            return i2;
        }
        if (i3 != TYPE_RAM) {
            return -1;
        }
        mRamLevel = i2;
        return i2;
    }

    private static int getDeviceLevel1(int i) {
        if (i == TYPE_RAM) {
            int totalRam = getTotalRam();
            if (totalRam > 6) {
                return 2;
            }
            if (totalRam > 4) {
                return 1;
            }
            return totalRam > 0 ? 0 : -1;
        }
        if (i == TYPE_CPU) {
            return getCpuLevel();
        }
        return -1;
    }

    private static int getDeviceLevel1() {
        int i = mLevel;
        if (i != -1) {
            return i;
        }
        if (isMiuiLiteRom()) {
            mLevel = 0;
        } else {
            mLevel = getMinLevel(getDeviceLevel1(TYPE_CPU), getDeviceLevel1(TYPE_RAM), getDeviceLevel(DEV_STANDARD_VERSION, TYPE_GPU));
        }
        return mLevel;
    }

    private static int getMinLevel(int... iArr) {
        if (iArr.length == 0) {
            return -1;
        }
        int i = iArr[0];
        for (int i2 : iArr) {
            if (i2 > -1 && i2 < i) {
                i = i2;
            }
        }
        return i;
    }

    private static int getCpuLevel() {
        int mtkCpuLevel;
        String hardwareInfo = getHardwareInfo();
        if (hardwareInfo.length() <= 0) {
            mtkCpuLevel = -1;
        } else if (hardwareInfo.contains(QUALCOMM)) {
            mtkCpuLevel = getQualcommCpuLevel(hardwareInfo);
        } else {
            mtkCpuLevel = getMtkCpuLevel(hardwareInfo);
        }
        return mtkCpuLevel == -1 ? getCpuStats().level : mtkCpuLevel;
    }

    private static String getHardwareInfo() {
        try {
            Scanner scanner = new Scanner(new File("/proc/cpuinfo"));
            while (scanner.hasNextLine()) {
                try {
                    String strNextLine = scanner.nextLine();
                    if (!scanner.hasNextLine()) {
                        String[] strArrSplit = strNextLine.split(SEPARATOR);
                        if (strArrSplit.length > 1) {
                            String str = strArrSplit[1];
                            scanner.close();
                            return str;
                        }
                        Log.e(TAG, "getChipSetFromCpuInfo failed", e);
                        return "";
                    }
                } catch (Throwable th) {
                    try {
                        scanner.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
            scanner.close();
            return "";
        } catch (Exception e) {
            Log.e(TAG, "getChipSetFromCpuInfo failed", e);
            return "";
        }
    }

    private static boolean isScreenOled() {
        try {
            return OLED_SCREEN_PROPERTY.equals(LiteSystemProperties.get("ro.vendor.display.type", "lcd")) || OLED_SCREEN_PROPERTY.equals(LiteSystemProperties.get("ro.display.type", "lcd"));
        } catch (Exception e) {
            Log.e(TAG, "getProductDevice failed , e:" + e);
            return false;
        }
    }

    public static int getQualcommCpuLevel(String str) {
        String strGroup;
        String strGroup2;
        Matcher matcher = SM_PATTERN.matcher(str);
        if (!matcher.find() || (strGroup = matcher.group(1)) == null || (strGroup2 = matcher.group(2)) == null) {
            return -1;
        }
        String lowerCase = strGroup.toLowerCase(Locale.ENGLISH);
        if (lowerCase.equals(SNAPDRAGON3)) {
            int i = Integer.parseInt(strGroup2.substring(0, 1));
            if (i >= 8) {
                return 2;
            }
            return i >= 7 ? 1 : 0;
        }
        if (lowerCase.equals(SNAPDRAGON2)) {
            return Integer.parseInt(strGroup2.substring(0, 1)) >= 7 ? 1 : 0;
        }
        return lowerCase.equals(SNAPDRAGON1) ? 0 : -1;
    }

    private static int getMtkCpuLevel(String str) {
        String strGroup;
        String strGroup2;
        Matcher matcher = MT_PATTERN.matcher(str);
        if (!matcher.find() || (strGroup = matcher.group(1)) == null || (strGroup2 = matcher.group(2)) == null) {
            return -1;
        }
        return (Integer.parseInt(strGroup) != 68 || Integer.parseInt(strGroup2) < 73) ? 0 : 1;
    }

    public static CpuStats getCpuStats() {
        List<CpuInfo> cpuInfoList = getCpuInfoList();
        CpuStats cpuStats = new CpuStats();
        if (cpuInfoList.size() < 8) {
            cpuStats.level = 0;
        }
        doCpuStats(cpuStats, cpuInfoList);
        return cpuStats;
    }

    private static void doCpuStats(CpuStats cpuStats, List<CpuInfo> list) {
        for (CpuInfo cpuInfo : list) {
            if (cpuInfo.architecture < 8) {
                cpuStats.level = 0;
            }
            if (cpuInfo.maxFreq > cpuStats.maxFreq) {
                cpuStats.maxFreq = cpuInfo.maxFreq;
            }
            if (cpuInfo.maxFreq >= BIG_CORE_FREQ) {
                cpuStats.bigCoreCount++;
            } else {
                cpuStats.smallCoreCount++;
            }
        }
        decideLevel(cpuStats);
    }

    private static void decideLevel(CpuStats cpuStats) {
        if (cpuStats.level != -1) {
            return;
        }
        if (cpuStats.bigCoreCount >= 4) {
            if (cpuStats.maxFreq > HIGH_FREQ) {
                cpuStats.level = 2;
                return;
            } else if (cpuStats.maxFreq > MIDDLE_FREQ) {
                cpuStats.level = 1;
                return;
            } else {
                cpuStats.level = 0;
                return;
            }
        }
        if (cpuStats.maxFreq > MIDDLE_FREQ) {
            cpuStats.level = 1;
        } else {
            cpuStats.level = 0;
        }
    }

    public static List<CpuInfo> getCpuInfoList() {
        ArrayList arrayList = new ArrayList();
        try {
            Scanner scanner = new Scanner(new File("/proc/cpuinfo"));
            CpuInfo line = null;
            while (scanner.hasNextLine()) {
                try {
                    String[] strArrSplit = scanner.nextLine().split(SEPARATOR);
                    if (strArrSplit.length > 1) {
                        line = parseLine(strArrSplit, arrayList, line);
                    }
                } catch (Throwable th) {
                    try {
                        scanner.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
            scanner.close();
        } catch (Exception e) {
            Log.e(TAG, "getChipSetFromCpuInfo failed", e);
        }
        return arrayList;
    }

    private static CpuInfo parseLine(String[] strArr, List<CpuInfo> list, CpuInfo cpuInfo) throws Throwable {
        String strTrim = strArr[1].trim();
        if (strArr[0].contains(PROCESSOR) && TextUtils.isDigitsOnly(strTrim)) {
            CpuInfo cpuInfoCreateCpuInfo = createCpuInfo(strTrim);
            list.add(cpuInfoCreateCpuInfo);
            return cpuInfoCreateCpuInfo;
        }
        if (cpuInfo == null) {
            return cpuInfo;
        }
        getCpuInfo(strArr[0], strTrim, cpuInfo);
        return cpuInfo;
    }

    private static CpuInfo createCpuInfo(String str) throws Throwable {
        CpuInfo cpuInfo = new CpuInfo();
        cpuInfo.id = Integer.parseInt(str);
        String contentFromFileInfo = getContentFromFileInfo(String.format(Locale.ENGLISH, CpuMaxInfoFormat, Integer.valueOf(cpuInfo.id)));
        if (contentFromFileInfo != null) {
            cpuInfo.maxFreq = Integer.parseInt(contentFromFileInfo);
        }
        return cpuInfo;
    }

    private static void getCpuInfo(String str, String str2, CpuInfo cpuInfo) {
        if (str.contains(IMPLEMENTOR)) {
            cpuInfo.implementor = toInt(str2);
        } else if (str.contains(ARCHITECTURE)) {
            cpuInfo.architecture = toInt(str2);
        } else if (str.contains(PART)) {
            cpuInfo.part = toInt(str2);
        }
    }

    private static int toInt(String str) {
        if (str.startsWith(HEX)) {
            return Integer.parseInt(str.substring(2), 16);
        }
        return Integer.parseInt(str);
    }

    private static String getContentFromFileInfo(String str) throws Throwable {
        FileInputStream fileInputStream;
        Throwable th;
        BufferedReader bufferedReader;
        try {
            fileInputStream = new FileInputStream(str);
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                try {
                    String line = bufferedReader.readLine();
                    try {
                        fileInputStream.close();
                    } catch (IOException unused) {
                    }
                    try {
                        bufferedReader.close();
                    } catch (IOException unused2) {
                    }
                    return line;
                } catch (IOException unused3) {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException unused4) {
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException unused5) {
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException unused6) {
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                            throw th;
                        } catch (IOException unused7) {
                            throw th;
                        }
                    }
                    throw th;
                }
            } catch (IOException unused8) {
                bufferedReader = null;
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = null;
            }
        } catch (IOException unused9) {
            bufferedReader = null;
            fileInputStream = null;
        } catch (Throwable th4) {
            fileInputStream = null;
            th = th4;
            bufferedReader = null;
        }
    }

    private static int getDeviceLevel2(int i) {
        int iIntValue = -1;
        if (!initDeviceLevelInfo()) {
            return -1;
        }
        try {
            Object perf = getPerf();
            if (perf != null) {
                iIntValue = ((Integer) mGetDeviceLevelForWhole.invoke(perf, Integer.valueOf(i))).intValue();
                return transDeviceLevel(iIntValue);
            }
            throw new Exception("perf is null!");
        } catch (Exception e) {
            Log.e(TAG, "getDeviceLevel failed , e:" + e.toString());
        }
    }

    private static int getDeviceLevel2(int i, int i2) {
        int iIntValue = -1;
        if (!initDeviceLevelInfo()) {
            return -1;
        }
        try {
            Object perf = getPerf();
            if (perf != null) {
                iIntValue = ((Integer) mGetDeviceLevel.invoke(perf, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
                return transDeviceLevel(iIntValue);
            }
            throw new Exception("perf is null!");
        } catch (Exception e) {
            Log.e(TAG, "getDeviceLevel failed , e:" + e.toString());
        }
    }

    private static Class getPerfClass() {
        return sPerfClass;
    }

    public static void initPerf(Context context) {
        Constructor<Class> constructor;
        if (mPerf == null) {
            try {
                applicationContext = context;
                if (context != null && (constructor = mConstructor) != null) {
                    mPerf = constructor.newInstance(context);
                }
            } catch (Exception e) {
                Log.e(TAG, "initPerf DeviceUtils(): newInstance Exception:" + e);
                e.printStackTrace();
            }
            if (mPerf == null) {
                Log.e(TAG, "initPerf fail mPerf is still null");
            }
        }
        if (mCompuPerf == null) {
            try {
                Constructor<Class> constructor2 = mCompuConstructor;
                if (constructor2 != null) {
                    mCompuPerf = constructor2.newInstance(new Object[0]);
                }
            } catch (Exception e2) {
                Log.e(TAG, "initCompuPerf DeviceUtils(): newInstance Exception:" + e2);
                e2.printStackTrace();
            }
            if (mCompuPerf == null) {
                Log.e(TAG, "initCompuPerf fail mPerf is still null");
            }
        }
    }

    private static Object getPerf() {
        Constructor<Class> constructor;
        if (mPerf == null) {
            try {
                Context appContext = getAppContext();
                if (appContext != null && (constructor = mConstructor) != null) {
                    mPerf = constructor.newInstance(appContext);
                } else {
                    throw new Exception("getAppContext fail");
                }
            } catch (Exception e) {
                Log.e(TAG, "getPerf DeviceUtils(): newInstance Exception:" + e);
                e.printStackTrace();
            }
        }
        return mPerf;
    }

    private static Method getMiuiLiteVersionMethod() throws Exception {
        if (mGetMiuiLiteVersion == null) {
            mGetMiuiLiteVersion = getPerfClass().getDeclaredMethod("getMiuiLiteVersion", new Class[0]);
        }
        return mGetMiuiLiteVersion;
    }

    private static boolean initDeviceLevelInfo() {
        boolean z = initDeviceLevelInfo;
        if (z) {
            return z;
        }
        try {
            Class perfClass = getPerfClass();
            if (perfClass == null) {
                return initDeviceLevelInfo;
            }
            mGetDeviceLevel = perfClass.getDeclaredMethod("getDeviceLevel", Integer.TYPE, Integer.TYPE);
            mGetDeviceLevelForWhole = perfClass.getDeclaredMethod("getDeviceLevel", Integer.TYPE);
            TYPE_RAM = ((Integer) getStaticObjectField(perfClass, "DEVICE_LEVEL_FOR_RAM", Integer.TYPE)).intValue();
            TYPE_CPU = ((Integer) getStaticObjectField(perfClass, "DEVICE_LEVEL_FOR_CPU", Integer.TYPE)).intValue();
            TYPE_GPU = ((Integer) getStaticObjectField(perfClass, "DEVICE_LEVEL_FOR_GPU", Integer.TYPE)).intValue();
            LOW = ((Integer) getStaticObjectField(perfClass, "LOW_DEVICE", Integer.TYPE)).intValue();
            MIDDLE = ((Integer) getStaticObjectField(perfClass, "MIDDLE_DEVICE", Integer.TYPE)).intValue();
            HIGH = ((Integer) getStaticObjectField(perfClass, "HIGH_DEVICE", Integer.TYPE)).intValue();
            UNKNOWN = ((Integer) getStaticObjectField(perfClass, "DEVICE_LEVEL_UNKNOWN", Integer.TYPE)).intValue();
            initDeviceLevelInfo = true;
            return initDeviceLevelInfo;
        } catch (Exception e) {
            Log.e(TAG, "initDeviceLevelInfo Fail: Exception:" + e);
        }
    }

    private static Context getAppContext() {
        if (applicationContext == null) {
            try {
                Application application2 = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication", new Class[0]).invoke(null, null);
                application = application2;
                if (application2 != null) {
                    applicationContext = application2.getApplicationContext();
                }
            } catch (Exception e) {
                Log.e(TAG, "android.app.ActivityThread Exception:" + e);
            }
        }
        if (applicationContext == null) {
            try {
                Application application3 = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication", new Class[0]).invoke(null, null);
                application = application3;
                if (application3 != null) {
                    applicationContext = application3.getApplicationContext();
                }
            } catch (Exception e2) {
                Log.e(TAG, "android.app.AppGlobals Exception:" + e2);
            }
        }
        return applicationContext;
    }

    private static int transDeviceLevel(int i) {
        if (i == LOW) {
            return 0;
        }
        if (i == MIDDLE) {
            return 1;
        }
        return i == HIGH ? 2 : -1;
    }

    private static <T> T getStaticObjectField(Class<?> cls, String str, Class<T> cls2) throws Exception {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return (T) declaredField.get(null);
    }

    private static Object getCompuPerf() {
        if (mCompuPerf == null) {
            try {
                Constructor<Class> constructor = mCompuConstructor;
                if (constructor != null) {
                    mCompuPerf = constructor.newInstance(new Object[0]);
                }
            } catch (Exception e) {
                Log.e(TAG, "getCompuPerf DeviceUtils(): newInstance Exception:" + e);
                e.printStackTrace();
            }
        }
        return mCompuPerf;
    }

    public static void initComputilityLevel() {
        try {
            Object compuPerf = getCompuPerf();
            if (compuPerf != null) {
                mInitCompuLevel.invoke(compuPerf, new Object[0]);
                return;
            }
            throw new Exception("compu perf is null!");
        } catch (Exception e) {
            Log.e(TAG, "initComputilityLevel failed , e:" + e.toString());
        }
    }

    public static int getComputilityLevel() {
        if (mCompuLevel == -1) {
            try {
                Object compuPerf = getCompuPerf();
                if (compuPerf != null) {
                    mCompuLevel = ((Integer) mGetCurrentCompuLevel.invoke(compuPerf, new Object[0])).intValue();
                    Log.i(TAG, "getComputilityLevel(): " + mCompuLevel);
                } else {
                    throw new Exception("compu perf is null!");
                }
            } catch (Exception e) {
                Log.e(TAG, "getComputilityLevel failed , e:" + e.toString());
            }
        }
        return mCompuLevel;
    }

    public static int getComputilityVersion() {
        if (mCompuVersion == -1) {
            try {
                Object compuPerf = getCompuPerf();
                if (compuPerf != null) {
                    mCompuVersion = ((Integer) mGetCompuVersion.invoke(compuPerf, new Object[0])).intValue();
                    Log.i(TAG, "getComputilityVersion: " + mCompuVersion);
                } else {
                    throw new Exception("compu perf is null!");
                }
            } catch (Exception e) {
                Log.e(TAG, "getComputilityVersion failed , e:" + e.toString());
            }
        }
        return mCompuVersion;
    }
}
