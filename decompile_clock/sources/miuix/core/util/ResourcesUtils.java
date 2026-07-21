package miuix.core.util;

import android.content.res.AssetManager;
import android.content.res.Resources;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes2.dex */
public class ResourcesUtils {
    private static Method ASSET_MANAGER_ADD_ASSET_PATH;
    private static Constructor<AssetManager> ASSET_MANAGER_CONSTRUCTOR;

    static {
        try {
            ASSET_MANAGER_ADD_ASSET_PATH = AssetManager.class.getMethod("addAssetPath", String.class);
            ASSET_MANAGER_CONSTRUCTOR = AssetManager.class.getConstructor(new Class[0]);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private ResourcesUtils() {
    }

    public static Resources createResources(String... strArr) {
        return createResources(null, strArr);
    }

    public static Resources createResources(Resources resources, String... strArr) {
        AssetManager assetManagerNewInstance;
        try {
            assetManagerNewInstance = ASSET_MANAGER_CONSTRUCTOR.newInstance(new Object[0]);
            try {
                for (String str : strArr) {
                    ASSET_MANAGER_ADD_ASSET_PATH.invoke(assetManagerNewInstance, str);
                }
            } catch (IllegalAccessException e) {
                e = e;
                e.printStackTrace();
            } catch (InstantiationException e2) {
                e = e2;
                e.printStackTrace();
            } catch (InvocationTargetException e3) {
                e = e3;
                e.printStackTrace();
            }
        } catch (IllegalAccessException e4) {
            e = e4;
            assetManagerNewInstance = null;
        } catch (InstantiationException e5) {
            e = e5;
            assetManagerNewInstance = null;
        } catch (InvocationTargetException e6) {
            e = e6;
            assetManagerNewInstance = null;
        }
        if (resources == null) {
            return new Resources(assetManagerNewInstance, null, null);
        }
        return new Resources(assetManagerNewInstance, resources.getDisplayMetrics(), resources.getConfiguration());
    }
}
