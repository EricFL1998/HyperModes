package miuix.autodensity;

import miuix.app.Application;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public class MiuixApplication extends Application implements IDensity {
    @Override // miuix.autodensity.IDensity
    public boolean shouldAdaptAutoDensity() {
        return true;
    }

    @Override // miuix.app.Application, android.app.Application
    public void onCreate() {
        super.onCreate();
        AutoDensityConfig.init(this);
    }
}
