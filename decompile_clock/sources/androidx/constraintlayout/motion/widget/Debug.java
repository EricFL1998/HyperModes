package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.CharBuffer;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;

/* JADX INFO: loaded from: classes.dex */
public class Debug {
    public static void logStack(String tag, String msg, int n) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        int iMin = Math.min(n, stackTrace.length - 1);
        String str = " ";
        for (int i = 1; i <= iMin; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            str = str + " ";
            Log.v(tag, msg + str + (".(" + stackTrace[i].getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTrace[i].getLineNumber() + ") " + stackTrace[i].getMethodName()) + str);
        }
    }

    public static void printStack(String msg, int n) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        int iMin = Math.min(n, stackTrace.length - 1);
        String str = " ";
        for (int i = 1; i <= iMin; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            str = str + " ";
            System.out.println(msg + str + (".(" + stackTrace[i].getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTrace[i].getLineNumber() + ") ") + str);
        }
    }

    public static String getName(View view) {
        try {
            return view.getContext().getResources().getResourceEntryName(view.getId());
        } catch (Exception unused) {
            return "UNKNOWN";
        }
    }

    public static void dumpPoc(Object obj) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        String str = ".(" + stackTraceElement.getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTraceElement.getLineNumber() + ")";
        Class<?> cls = obj.getClass();
        System.out.println(str + "------------- " + cls.getName() + " --------------------");
        for (Field field : cls.getFields()) {
            try {
                Object obj2 = field.get(obj);
                if (field.getName().startsWith("layout_constraint") && ((!(obj2 instanceof Integer) || !obj2.toString().equals("-1")) && ((!(obj2 instanceof Integer) || !obj2.toString().equals(WorldClockEditActivity.LOCAL_CITY_ID)) && ((!(obj2 instanceof Float) || !obj2.toString().equals("1.0")) && (!(obj2 instanceof Float) || !obj2.toString().equals("0.5")))))) {
                    System.out.println(str + "    " + field.getName() + " " + obj2);
                }
            } catch (IllegalAccessException unused) {
            }
        }
        System.out.println(str + "------------- " + cls.getSimpleName() + " --------------------");
    }

    public static String getName(Context context, int id) {
        if (id != -1) {
            try {
                return context.getResources().getResourceEntryName(id);
            } catch (Exception unused) {
                return "?" + id;
            }
        }
        return "UNKNOWN";
    }

    public static String getName(Context context, int[] id) {
        String resourceEntryName;
        try {
            String str = id.length + "[";
            int i = 0;
            while (i < id.length) {
                String str2 = str + (i == 0 ? "" : " ");
                try {
                    resourceEntryName = context.getResources().getResourceEntryName(id[i]);
                } catch (Resources.NotFoundException unused) {
                    resourceEntryName = "? " + id[i] + " ";
                }
                str = str2 + resourceEntryName;
                i++;
            }
            return str + "]";
        } catch (Exception e) {
            Log.v("DEBUG", e.toString());
            return "UNKNOWN";
        }
    }

    public static String getState(MotionLayout layout, int stateId) {
        return getState(layout, stateId, -1);
    }

    public static String getState(MotionLayout layout, int stateId, int len) {
        int length;
        if (stateId == -1) {
            return "UNDEFINED";
        }
        String resourceEntryName = layout.getContext().getResources().getResourceEntryName(stateId);
        if (len == -1) {
            return resourceEntryName;
        }
        if (resourceEntryName.length() > len) {
            resourceEntryName = resourceEntryName.replaceAll("([^_])[aeiou]+", "$1");
        }
        return (resourceEntryName.length() <= len || (length = resourceEntryName.replaceAll("[^_]", "").length()) <= 0) ? resourceEntryName : resourceEntryName.replaceAll(CharBuffer.allocate((resourceEntryName.length() - len) / length).toString().replace((char) 0, '.') + "_", "_");
    }

    public static String getActionType(MotionEvent event) {
        int action = event.getAction();
        for (Field field : MotionEvent.class.getFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(Integer.TYPE) && field.getInt(null) == action) {
                    return field.getName();
                }
            } catch (IllegalAccessException unused) {
            }
        }
        return "---";
    }

    public static String getLocation() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        return ".(" + stackTraceElement.getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTraceElement.getLineNumber() + ")";
    }

    public static String getLoc() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        return ".(" + stackTraceElement.getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTraceElement.getLineNumber() + ") " + stackTraceElement.getMethodName() + "()";
    }

    public static String getLocation2() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        return ".(" + stackTraceElement.getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTraceElement.getLineNumber() + ")";
    }

    public static String getCallFrom(int n) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[n + 2];
        return ".(" + stackTraceElement.getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTraceElement.getLineNumber() + ")";
    }

    public static void dumpLayoutParams(ViewGroup layout, String str) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        String str2 = ".(" + stackTraceElement.getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTraceElement.getLineNumber() + ") " + str + "  ";
        int childCount = layout.getChildCount();
        System.out.println(str + " children " + childCount);
        for (int i = 0; i < childCount; i++) {
            View childAt = layout.getChildAt(i);
            System.out.println(str2 + "     " + getName(childAt));
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            for (Field field : layoutParams.getClass().getFields()) {
                try {
                    Object obj = field.get(layoutParams);
                    if (field.getName().contains("To") && !obj.toString().equals("-1")) {
                        System.out.println(str2 + "       " + field.getName() + " " + obj);
                    }
                } catch (IllegalAccessException unused) {
                }
            }
        }
    }

    public static void dumpLayoutParams(ViewGroup.LayoutParams param, String str) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        String str2 = ".(" + stackTraceElement.getFileName() + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + stackTraceElement.getLineNumber() + ") " + str + "  ";
        System.out.println(" >>>>>>>>>>>>>>>>>>. dump " + str2 + "  " + param.getClass().getName());
        for (Field field : param.getClass().getFields()) {
            try {
                Object obj = field.get(param);
                String name = field.getName();
                if (name.contains("To") && !obj.toString().equals("-1")) {
                    System.out.println(str2 + "       " + name + " " + obj);
                }
            } catch (IllegalAccessException unused) {
            }
        }
        System.out.println(" <<<<<<<<<<<<<<<<< dump " + str2);
    }
}
