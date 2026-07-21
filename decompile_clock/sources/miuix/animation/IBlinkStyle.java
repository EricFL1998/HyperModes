package miuix.animation;

import android.graphics.RectF;
import miuix.animation.base.AnimConfig;

/* JADX INFO: loaded from: classes2.dex */
public interface IBlinkStyle extends IStateContainer {

    public enum BlinkType {
        HIGHLIGHT,
        NORMAL
    }

    IBlinkStyle resetConfig();

    IBlinkStyle setBlinkPadding(float f, float f2, float f3, float f4);

    IBlinkStyle setBlinkRadius(float f);

    IBlinkStyle setBlinkRadius(float f, float f2, float f3, float f4);

    IBlinkStyle setBlinkRect(RectF rectF, ITouchStyle.TouchRectGravity touchRectGravity);

    IBlinkStyle setInterval(long j);

    IBlinkStyle setLimitCount(int i);

    IBlinkStyle setTintMode(int i);

    IBlinkStyle setToHighlightConfig(AnimConfig animConfig);

    IBlinkStyle setToNormalConfig(AnimConfig animConfig);

    void startBlink(int i, AnimConfig... animConfigArr);

    void startBlink(AnimConfig... animConfigArr);

    void stopBlink();
}
