package miuix.animation;

import miuix.animation.base.AnimConfig;

/* JADX INFO: loaded from: classes2.dex */
public interface FolmeStyle extends ICancelableStyle {
    IStateStyle autoSetTo(Object... objArr);

    void clean();

    void end();

    @Deprecated
    IStateStyle fromTo(Object obj, Object obj2, AnimConfig... animConfigArr);

    IAnimTarget getTarget();

    long predictDuration(Object... objArr);

    IStateStyle resetTo(Object obj);

    IStateStyle resetTo(Object obj, AnimConfig... animConfigArr);

    IStateStyle resetTo(Object... objArr);

    IStateStyle setFlags(long j);

    IStateStyle setTo(Object obj);

    IStateStyle setTo(Object obj, AnimConfig... animConfigArr);

    IStateStyle setTo(Object... objArr);

    IStateStyle setup(Object obj);

    IStateStyle then(Object obj, AnimConfig... animConfigArr);

    IStateStyle then(Object... objArr);

    IStateStyle to(Object obj, AnimConfig... animConfigArr);

    IStateStyle to(Object... objArr);

    IStateStyle to(AnimConfig... animConfigArr);

    IStateStyle toWithInit(Object... objArr);
}
