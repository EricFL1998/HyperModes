package miuix.internal.util;

import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.utils.CommonUtils;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class AnimHelper {
    private static final int PRESS_COLOR_DARK = 620756991;
    private static final int PRESS_COLOR_LIGHT = 335544320;
    private static final String TAG = "AnimHelper";
    private static volatile boolean sIsDebugEnabled = false;

    public static void addItemPressEffect(View view) {
        if (view == null) {
            return;
        }
        view.setForeground(AttributeResolver.resolveDrawable(view.getContext(), R.attr.itemPressEffectForeground));
        if (view.isClickable()) {
            return;
        }
        view.setOnHoverListener(new View.OnHoverListener() { // from class: miuix.internal.util.AnimHelper$$ExternalSyntheticLambda0
            @Override // android.view.View.OnHoverListener
            public final boolean onHover(View view2, MotionEvent motionEvent) {
                return AnimHelper.lambda$addItemPressEffect$0(view2, motionEvent);
            }
        });
    }

    static /* synthetic */ boolean lambda$addItemPressEffect$0(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 9) {
            view.setHovered(true);
        } else if (motionEvent.getAction() == 10) {
            view.setHovered(false);
        }
        return false;
    }

    @Deprecated
    public static void addPressAnim(View view) {
        addPressAnim(view, IHoverStyle.HoverEffect.NORMAL);
    }

    @Deprecated
    public static void addPressAnim(View view, IHoverStyle.HoverEffect hoverEffect) {
        Folme.useAt(view).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).handleTouchOf(view, new AnimConfig[0]);
        Folme.useAt(view).hover().setEffect(hoverEffect).handleHoverOf(view, new AnimConfig[0]);
    }

    public static void addPressAnimWithBg(View view) {
        ITouchStyle scale = Folme.useAt(view).touch().setTint(0).setScale(1.0f, new ITouchStyle.TouchType[0]);
        IHoverStyle effect = Folme.useAt(view).hover().setTint(0).setEffect(IHoverStyle.HoverEffect.NORMAL);
        if (AttributeResolver.resolveBoolean(view.getContext(), R.attr.isLightTheme, true)) {
            scale.setBackgroundColor(PRESS_COLOR_LIGHT);
            effect.setBackgroundColor(PRESS_COLOR_LIGHT);
        } else {
            scale.setBackgroundColor(PRESS_COLOR_DARK);
            effect.setBackgroundColor(PRESS_COLOR_DARK);
        }
        scale.handleTouchOf(view, new AnimConfig[0]);
        effect.handleHoverOf(view, new AnimConfig[0]);
    }

    public static boolean loadDialogDebugInAndroidUIThreadEnabledFormSettings() {
        String str = "";
        try {
            String prop = CommonUtils.readProp("log.tag.alertdialog.debug.enable");
            if (prop != null) {
                str = prop;
            }
        } catch (Exception e) {
            Log.i(TAG, "can not access property log.tag.alertdialog.enable, undebugable", e);
        }
        Log.d(TAG, "Alert dialog debugEnable = " + str);
        sIsDebugEnabled = TextUtils.equals("true", str);
        return sIsDebugEnabled;
    }

    public static boolean isDialogDebugInAndroidUIThreadEnabled() {
        return sIsDebugEnabled;
    }
}
