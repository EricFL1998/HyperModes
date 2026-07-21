package miuix.navigator.app;

import android.content.res.Configuration;
import miuix.responsive.interfaces.IResponsive;
import miuix.responsive.map.ScreenSpec;

/* JADX INFO: loaded from: classes3.dex */
public interface NavigatorResponsiveProvider<T> extends IResponsive<T> {
    @Override // miuix.responsive.interfaces.IResponsive
    default void onResponsiveLayout(Configuration configuration, ScreenSpec screenSpec, boolean z) {
    }
}
