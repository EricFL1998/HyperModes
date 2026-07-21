package miuix.responsive.interfaces;

import android.content.res.Configuration;
import java.util.List;
import miuix.responsive.map.ResponsiveViewSpec;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes3.dex */
public interface IViewResponsive {
    boolean onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z);

    default boolean onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z, List<ResponsiveViewSpec> list) {
        return false;
    }
}
