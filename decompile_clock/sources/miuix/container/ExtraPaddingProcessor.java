package miuix.container;

/* JADX INFO: loaded from: classes2.dex */
public interface ExtraPaddingProcessor {
    void addExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver);

    ExtraPaddingPolicy getExtraPaddingPolicy();

    boolean isExtraHorizontalPaddingEnable();

    void removeExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver);

    void setExtraHorizontalPaddingEnable(boolean z);

    void setExtraHorizontalPaddingInitEnable(boolean z);

    void setExtraPaddingPolicy(ExtraPaddingPolicy extraPaddingPolicy);
}
