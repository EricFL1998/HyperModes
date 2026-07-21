package miuix.animation;

import miuix.animation.base.AnimConfig;

/* JADX INFO: loaded from: classes2.dex */
public interface IStateContainer extends ICancelableStyle {
    void addConfig(Object obj, AnimConfig... animConfigArr);

    void clean();

    void enableDefaultAnim(boolean z);
}
