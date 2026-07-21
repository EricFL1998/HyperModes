package miuix.responsive.map;

import android.content.Context;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes3.dex */
public class ResponsiveStateManager {
    private static ConcurrentHashMap<Integer, ResponsiveState> mapState = new ConcurrentHashMap<>();
    private static volatile ResponsiveStateManager sInstance;

    public static ResponsiveStateManager getInstance() {
        if (sInstance == null) {
            synchronized (ResponsiveStateManager.class) {
                if (sInstance == null) {
                    sInstance = new ResponsiveStateManager();
                }
            }
        }
        return sInstance;
    }

    public ResponsiveState recordState(Context context, ResponsiveState.WindowInfoWrapper windowInfoWrapper) {
        if (context == null) {
            return null;
        }
        int iHashCode = context.hashCode();
        ResponsiveState responsiveState = mapState.get(Integer.valueOf(iHashCode));
        if (responsiveState == null) {
            responsiveState = new ResponsiveState();
            mapState.put(Integer.valueOf(iHashCode), responsiveState);
        }
        responsiveState.updateFromWindowInfoWrapper(windowInfoWrapper);
        return responsiveState;
    }

    @Deprecated
    public int getScreenMode(Context context) {
        if (context == null) {
            return 4103;
        }
        int iHashCode = context.hashCode();
        ResponsiveState responsiveState = mapState.get(Integer.valueOf(iHashCode));
        if (responsiveState == null) {
            responsiveState = new ResponsiveState();
            mapState.put(Integer.valueOf(iHashCode), responsiveState);
        }
        return responsiveState.getScreenMode();
    }

    public ResponsiveState getResponsiveState(Context context) {
        if (context == null) {
            return null;
        }
        int iHashCode = context.hashCode();
        ResponsiveState responsiveState = mapState.get(Integer.valueOf(iHashCode));
        if (responsiveState != null) {
            return responsiveState;
        }
        ResponsiveState responsiveState2 = new ResponsiveState();
        mapState.put(Integer.valueOf(iHashCode), responsiveState2);
        return responsiveState2;
    }

    public void remove(Context context) {
        mapState.remove(Integer.valueOf(context.hashCode()));
    }
}
