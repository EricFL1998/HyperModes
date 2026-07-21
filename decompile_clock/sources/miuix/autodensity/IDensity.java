package miuix.autodensity;

/* JADX INFO: loaded from: classes2.dex */
public interface IDensity {
    default int getRatioUiBaseWidthDp() {
        return 0;
    }

    boolean shouldAdaptAutoDensity();
}
