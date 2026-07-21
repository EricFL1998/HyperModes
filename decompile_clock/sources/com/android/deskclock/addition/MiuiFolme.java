package com.android.deskclock.addition;

import android.view.MotionEvent;
import android.view.View;
import com.android.deskclock.util.Log;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.IFolme;
import miuix.animation.IHoverStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.IVisibleStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.ValueProperty;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes.dex */
public class MiuiFolme {
    private static final String ERROR_MSG = "miui folme anim error: ";
    private static ClockSpringInterpolator FAB_RESET_ANIM = null;
    private static EaseManager.EaseStyle STYLE_HIDE_DIALOG_HEIGHT = null;
    private static Object STYLE_HIDE_DIALOG_TRANSLATE = null;
    private static Object STYLE_HIDE_TAB = null;
    private static Object STYLE_HIDE_TIMER_CIRCLE = null;
    private static Object STYLE_HIDE_TIMER_PICKER = null;
    private static EaseManager.EaseStyle STYLE_SHOW_DIALOG_HEIGHT = null;
    private static Object STYLE_SHOW_DIALOG_TIME_RESET = null;
    private static Object STYLE_SHOW_DIALOG_TRANSLATE = null;
    private static Object STYLE_SHOW_TAB = null;
    private static Object STYLE_SHOW_TIMER_CIRCLE = null;
    private static Object STYLE_SHOW_TIMER_PICKER = null;
    private static Object STYLE_SHOW_TIME_TRANSLATE = null;
    private static final String TAG = "DC:MiuiFolme";

    public static class ClockSpringInterpolator extends EaseManager.SpringInterpolator {
    }

    public static class ClockTransitionListener extends TransitionListener {
    }

    static {
        try {
            STYLE_SHOW_DIALOG_HEIGHT = FolmeEase.spring(0.86f, 0.35f);
            STYLE_HIDE_DIALOG_HEIGHT = FolmeEase.spring(0.86f, 0.35f);
            STYLE_SHOW_DIALOG_TRANSLATE = EaseManager.getStyle(-2, 0.75f, 0.4f);
            STYLE_HIDE_DIALOG_TRANSLATE = EaseManager.getStyle(-2, 0.85f, 0.3f);
            STYLE_HIDE_TIMER_PICKER = EaseManager.getStyle(-2, 1.0f, 0.18f);
            STYLE_SHOW_TIMER_CIRCLE = EaseManager.getStyle(-2, 0.9f, 0.35f);
            STYLE_SHOW_TIMER_PICKER = EaseManager.getStyle(-2, 0.9f, 0.3f);
            STYLE_HIDE_TIMER_CIRCLE = EaseManager.getStyle(-2, 1.0f, 0.18f);
            STYLE_HIDE_TAB = EaseManager.getStyle(-2, 1.0f, 0.25f);
            STYLE_SHOW_TAB = EaseManager.getStyle(16, 300.0f);
            STYLE_SHOW_DIALOG_TIME_RESET = EaseManager.getStyle(-2, 0.9f, 0.35f);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void registerPressAnim(View view) {
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.touch().setTint(0.0f, 0.0f, 0.0f, 0.0f);
            iFolmeUseAt.touch().handleTouchOf(view, new AnimConfig[0]);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void addPressAnim(View view) {
        addPressAnim(view, IHoverStyle.HoverEffect.NORMAL);
    }

    public static void addPressAnim(View view, IHoverStyle.HoverEffect hoverEffect) {
        Folme.useAt(view).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).handleTouchOf(view, new AnimConfig[0]);
        Folme.useAt(view).hover().setEffect(hoverEffect).handleHoverOf(view, new AnimConfig[0]);
    }

    public static void registerItemAnim(View view, boolean z) {
        float f = z ? 1.0f : 0.0f;
        float f2 = z ? 0.04f : 0.08f;
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.touch().setScale(1.0f, ITouchStyle.TouchType.DOWN);
            iFolmeUseAt.touch().setScale(1.0f, ITouchStyle.TouchType.UP);
            iFolmeUseAt.touch().setTint(f2, f, f, f);
            iFolmeUseAt.touch().handleTouchOf(view, new AnimConfig[0]);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void cancelVisibleAnim(View view) {
        try {
            Folme.useAt(view).visible().cancel();
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void cancelAnim(View... viewArr) {
        try {
            for (View view : viewArr) {
                if (view != null) {
                    Folme.useAt(view).touch().cancel();
                    view.setScaleX(1.0f);
                    view.setScaleY(1.0f);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void cleanFolme(View... viewArr) {
        try {
            for (View view : viewArr) {
                if (view != null) {
                    Folme.clean(view);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void cancelFolme(View... viewArr) {
        try {
            for (View view : viewArr) {
                if (view != null) {
                    Folme.use(view).cancel();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void cleanFontAnim(View... viewArr) {
        try {
            for (View view : viewArr) {
                if (view != null) {
                    IFolme iFolmeUseAt = Folme.useAt(view);
                    iFolmeUseAt.touch().cancel();
                    iFolmeUseAt.state().cancel();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void cleanAnimArgs(View view) {
        try {
            Folme.useAt(view).state().clean();
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void visibleHide(View view, ClockTransitionListener clockTransitionListener) {
        try {
            view.clearAnimation();
            AnimState animStateAdd = new AnimState("alphaStart").add(ViewProperty.ALPHA, 1.0d);
            AnimState animStateAdd2 = new AnimState("alphaEnd").add(ViewProperty.ALPHA, 0.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            if (clockTransitionListener != null) {
                animConfig.addListeners(clockTransitionListener);
            }
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void visibleShowTest(View view) {
        Log.i("miui_anim", "-------------------> start visible show");
        try {
            view.setAlpha(0.0f);
            Folme.useAt(view).visible().show(new AnimConfig[0]);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void visibleHideTest(View view) {
        Log.i("miui_anim", "-------------------> start visible hide");
        try {
            Folme.useAt(view).visible().hide(new AnimConfig[0]);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void visibleShow1(View view, ClockTransitionListener clockTransitionListener) {
        try {
            AnimState animStateAdd = new AnimState("alphaStart").add(ViewProperty.ALPHA, 0.0d);
            AnimState animStateAdd2 = new AnimState("alphaEnd").add(ViewProperty.ALPHA, 1.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            if (clockTransitionListener != null) {
                animConfig.addListeners(clockTransitionListener);
            }
            animConfig.setEase((EaseManager.EaseStyle) STYLE_HIDE_DIALOG_TRANSLATE);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void visibleHide1(View view, ClockTransitionListener clockTransitionListener) {
        try {
            AnimState animStateAdd = new AnimState("alphaStart").add(ViewProperty.ALPHA, 1.0d);
            AnimState animStateAdd2 = new AnimState("alphaEnd").add(ViewProperty.ALPHA, 0.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            if (clockTransitionListener != null) {
                animConfig.addListeners(clockTransitionListener);
            }
            animConfig.setEase((EaseManager.EaseStyle) STYLE_HIDE_TIMER_PICKER);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void visibleShow(View view) {
        visibleShow(view, null);
    }

    public static void visibleShow(View view, ClockTransitionListener clockTransitionListener) {
        try {
            view.clearAnimation();
            AnimState animStateAdd = new AnimState("alphaStart").add(ViewProperty.ALPHA, 0.0d);
            AnimState animStateAdd2 = new AnimState("alphaEnd").add(ViewProperty.ALPHA, 1.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            if (clockTransitionListener != null) {
                animConfig.addListeners(clockTransitionListener);
            }
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void hideFab(View view, ClockTransitionListener clockTransitionListener) {
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase((EaseManager.EaseStyle) STYLE_HIDE_TAB);
            if (clockTransitionListener != null) {
                animConfig.addListeners(clockTransitionListener);
            }
            iFolmeUseAt.visible().hide(animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void showFab(View view, ClockTransitionListener clockTransitionListener) {
        try {
            view.setAlpha(0.0f);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase((EaseManager.EaseStyle) STYLE_SHOW_TAB);
            animConfig.setDelay(60L);
            if (clockTransitionListener != null) {
                animConfig.addListeners(clockTransitionListener);
            }
            iFolmeUseAt.visible().show(animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void touch(View view, MotionEvent motionEvent) {
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.touch().setTint(0.0f, 0.0f, 0.0f, 0.0f);
            iFolmeUseAt.touch().onMotionEvent(motionEvent);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void touch(View view) {
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.touch().setTint(0.0f, 0.0f, 0.0f, 0.0f);
            iFolmeUseAt.touch().handleTouchOf(view, new AnimConfig[0]);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void touchView(View view) {
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.touch().setTint(0.08f, 0.0f, 0.0f, 0.0f);
            iFolmeUseAt.touch().handleTouchOf(view, new AnimConfig[0]);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void btnTouch(View view) {
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.touch().setScale(1.0f, ITouchStyle.TouchType.DOWN);
            iFolmeUseAt.touch().handleTouchOf(view, new AnimConfig[0]);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static ClockSpringInterpolator getFabResetInterpolator() {
        if (FAB_RESET_ANIM == null) {
            ClockSpringInterpolator clockSpringInterpolator = new ClockSpringInterpolator();
            FAB_RESET_ANIM = clockSpringInterpolator;
            clockSpringInterpolator.setResponse(0.85714287f);
            FAB_RESET_ANIM.setDamping(0.9f);
        }
        return FAB_RESET_ANIM;
    }

    public static void showAlarmEditDialog(View view, int i, int i2, int i3, int i4, int i5, int i6, ClockTransitionListener clockTransitionListener) {
        try {
            view.clearAnimation();
            Folme.clean(view);
            AnimState animStateAdd = new AnimState("showDialogStart").add(ViewProperty.WIDTH, i).add(ViewProperty.HEIGHT, i2).add(ValueProperty.FRACTION, 0.0d).add(ViewProperty.Y, i3);
            AnimState animStateAdd2 = new AnimState("showDialogEnd").add(ViewProperty.WIDTH, i4).add(ViewProperty.HEIGHT, i5).add(ValueProperty.FRACTION, 1.0d).add(ViewProperty.Y, i6);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(STYLE_SHOW_DIALOG_HEIGHT);
            animConfig.addListeners(clockTransitionListener);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void showBedtimeEditDialog(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, ClockTransitionListener clockTransitionListener) {
        try {
            view.clearAnimation();
            Folme.clean(view);
            AnimState animStateAdd = new AnimState("showDialogStart").add(ViewProperty.WIDTH, i).add(ViewProperty.HEIGHT, i2).add(ViewProperty.Y, i3).add(ViewProperty.TRANSLATION_X, i7);
            AnimState animStateAdd2 = new AnimState("showDialogEnd").add(ViewProperty.WIDTH, i4).add(ViewProperty.HEIGHT, i5).add(ViewProperty.Y, i6).add(ViewProperty.TRANSLATION_X, i8);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(STYLE_SHOW_DIALOG_HEIGHT);
            AnimConfig animConfig2 = new AnimConfig();
            animConfig2.setSpecial(ViewProperty.Y, new AnimSpecialConfig());
            animConfig2.setEase((EaseManager.EaseStyle) STYLE_SHOW_DIALOG_TRANSLATE);
            animConfig2.addListeners(clockTransitionListener);
            AnimConfig animConfig3 = new AnimConfig();
            animConfig3.setSpecial(ViewProperty.TRANSLATION_X, new AnimSpecialConfig());
            animConfig3.setEase((EaseManager.EaseStyle) STYLE_SHOW_DIALOG_TRANSLATE);
            animConfig3.addListeners(clockTransitionListener);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig, animConfig2, animConfig3);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void animTimeDisplay(View view, int i, int i2) {
        try {
            AnimState animStateAdd = new AnimState("showDialogStart").add(ViewProperty.TRANSLATION_Y, i);
            AnimState animStateAdd2 = new AnimState("showDialogEnd").add(ViewProperty.TRANSLATION_Y, i2);
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.state().setTo(animStateAdd);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase((EaseManager.EaseStyle) STYLE_SHOW_DIALOG_TIME_RESET);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void hideAlarmEditDialog(View view, int i, int i2, int i3, ClockTransitionListener clockTransitionListener) {
        try {
            view.clearAnimation();
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimState animStateAdd = iFolmeUseAt.getTarget().getValue(ValueProperty.FRACTION) == Float.MAX_VALUE ? new AnimState("hideDialogStart").add(ValueProperty.FRACTION, 1.0d) : null;
            AnimState animStateAdd2 = new AnimState("hideDialogEnd").add(ViewProperty.WIDTH, i).add(ViewProperty.HEIGHT, i2).add(ValueProperty.FRACTION, 0.0d).add(ViewProperty.Y, i3);
            iFolmeUseAt.cancel();
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(STYLE_HIDE_DIALOG_HEIGHT);
            animConfig.addListeners(clockTransitionListener);
            if (animStateAdd != null) {
                iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
            } else {
                iFolmeUseAt.state().to(animStateAdd2, animConfig);
            }
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void hideBedtimeEditDialog(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, ClockTransitionListener clockTransitionListener) {
        try {
            view.clearAnimation();
            Folme.clean(view);
            AnimState animStateAdd = new AnimState("hideDialogStart").add(ViewProperty.WIDTH, i).add(ViewProperty.HEIGHT, i2).add(ViewProperty.Y, i3).add(ViewProperty.TRANSLATION_X, i7);
            AnimState animStateAdd2 = new AnimState("hideDialogEnd").add(ViewProperty.WIDTH, i4).add(ViewProperty.HEIGHT, i5).add(ViewProperty.Y, i6).add(ViewProperty.TRANSLATION_X, i8);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase(STYLE_HIDE_DIALOG_HEIGHT);
            AnimConfig animConfig2 = new AnimConfig();
            animConfig2.setSpecial(ViewProperty.Y, new AnimSpecialConfig());
            animConfig2.setEase((EaseManager.EaseStyle) STYLE_HIDE_DIALOG_TRANSLATE);
            animConfig2.addListeners(clockTransitionListener);
            AnimConfig animConfig3 = new AnimConfig();
            animConfig3.setSpecial(ViewProperty.TRANSLATION_X, new AnimSpecialConfig());
            animConfig3.setEase((EaseManager.EaseStyle) STYLE_HIDE_DIALOG_TRANSLATE);
            animConfig3.addListeners(clockTransitionListener);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig, animConfig2, animConfig3);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void hideTimerPicker(View view, ClockTransitionListener clockTransitionListener) {
        try {
            AnimState animStateAdd = new AnimState("timerPickerHideStart").add(ViewProperty.ALPHA, 1.0d);
            AnimState animStateAdd2 = new AnimState("timerPickerHideEnd").add(ViewProperty.ALPHA, 0.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase((EaseManager.EaseStyle) STYLE_HIDE_TIMER_PICKER);
            animConfig.addListeners(clockTransitionListener);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void showTimerCircle(View view, ClockTransitionListener clockTransitionListener) {
        try {
            AnimState animStateAdd = new AnimState("timerCircleStart").add(ViewProperty.SCALE_X, 0.800000011920929d).add(ViewProperty.SCALE_Y, 0.800000011920929d).add(ViewProperty.ALPHA, 0.0d);
            AnimState animStateAdd2 = new AnimState("timerCircleEnd").add(ViewProperty.SCALE_X, 1.0d).add(ViewProperty.SCALE_Y, 1.0d).add(ViewProperty.ALPHA, 1.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase((EaseManager.EaseStyle) STYLE_SHOW_TIMER_CIRCLE);
            animConfig.addListeners(clockTransitionListener);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void showTimerPicker(View view, ClockTransitionListener clockTransitionListener) {
        try {
            AnimState animStateAdd = new AnimState("timerPickerShowStart").add(ViewProperty.ALPHA, 0.0d);
            AnimState animStateAdd2 = new AnimState("timerPickerShowEnd").add(ViewProperty.ALPHA, 1.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setDelay(250L);
            animConfig.setEase((EaseManager.EaseStyle) STYLE_SHOW_TIMER_PICKER);
            animConfig.addListeners(clockTransitionListener);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void hideTimerCircle(View view, ClockTransitionListener clockTransitionListener) {
        try {
            AnimState animStateAdd = new AnimState("timerCircleHideStart").add(ViewProperty.SCALE_X, 1.0d).add(ViewProperty.SCALE_Y, 1.0d).add(ViewProperty.ALPHA, 1.0d);
            AnimState animStateAdd2 = new AnimState("timerCircleHideEnd").add(ViewProperty.SCALE_X, 0.800000011920929d).add(ViewProperty.SCALE_Y, 0.800000011920929d).add(ViewProperty.ALPHA, 0.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.setEase((EaseManager.EaseStyle) STYLE_HIDE_TIMER_CIRCLE);
            animConfig.addListeners(clockTransitionListener);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void resetFabTouch(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                touch(view);
            }
        }
    }

    public static void animateRulerIn(final View view, int i) {
        if (view == null) {
            return;
        }
        int i2 = -i;
        try {
            view.setTranslationY(i2);
            AnimState animStateAdd = new AnimState("rulerInStart").add(ViewProperty.TRANSLATION_Y, i2);
            AnimState animStateAdd2 = new AnimState("rulerInEnd").add(ViewProperty.TRANSLATION_Y, 0.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, new AnimConfig[0]);
            iFolmeUseAt.visible().setHide().show(new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.15f)).addListeners(new TransitionListener() { // from class: com.android.deskclock.addition.MiuiFolme.1
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    view.setVisibility(0);
                    view.setTranslationY(0.0f);
                    view.setAlpha(1.0f);
                    MiuiFolme.cleanFolme(view);
                }
            }));
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void animateRulerOut(final View view, int i, TransitionListener transitionListener) {
        if (view == null) {
            return;
        }
        try {
            AnimState animStateAdd = new AnimState("rulerInStart").add(ViewProperty.TRANSLATION_Y, 0.0d);
            AnimState animStateAdd2 = new AnimState("rulerInEnd").add(ViewProperty.TRANSLATION_Y, -i);
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, new AnimConfig[0]);
            AnimConfig animConfigAddListeners = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.15f)).addListeners(new TransitionListener() { // from class: com.android.deskclock.addition.MiuiFolme.2
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    view.setTranslationY(0.0f);
                    view.setAlpha(1.0f);
                    view.setVisibility(8);
                    MiuiFolme.cleanFolme(view);
                }
            });
            if (transitionListener != null) {
                animConfigAddListeners.addListeners(transitionListener);
            }
            iFolmeUseAt.visible().setShow().hide(animConfigAddListeners);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void animateRulerShow(final View view, int i, int i2) {
        if (view == null) {
            return;
        }
        try {
            AnimState animStateAdd = new AnimState("rulerShowStart").add(ViewProperty.WIDTH, i);
            AnimState animStateAdd2 = new AnimState("rulerShowEnd").add(ViewProperty.WIDTH, i2);
            AnimConfig ease = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.85f, 0.3f));
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, ease);
            ease.addListeners(new TransitionListener() { // from class: com.android.deskclock.addition.MiuiFolme.3
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    view.setVisibility(0);
                    view.setAlpha(1.0f);
                }
            });
            iFolmeUseAt.visible().setHide().show(ease);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void animateRulerHide(final View view, int i, int i2) {
        if (view == null) {
            return;
        }
        try {
            AnimState animStateAdd = new AnimState("rulerHideStart").add(ViewProperty.WIDTH, i);
            AnimState animStateAdd2 = new AnimState("rulerHideEnd").add(ViewProperty.WIDTH, i2);
            AnimConfig ease = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.85f, 0.3f));
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, ease);
            ease.addListeners(new TransitionListener() { // from class: com.android.deskclock.addition.MiuiFolme.4
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    view.setAlpha(0.0f);
                    view.setVisibility(8);
                }
            });
            iFolmeUseAt.visible().setShow().hide(ease);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void animateWhiteNoiseIn(View[] viewArr) {
        if (viewArr == null) {
            return;
        }
        for (int i = 0; i < viewArr.length; i++) {
            try {
                final View view = viewArr[i];
                if (view != null) {
                    Folme.useAt(view).state().fromTo(new AnimState("WhiteNoiseInStart").add(ViewProperty.SCALE_X, 0.800000011920929d).add(ViewProperty.SCALE_Y, 0.800000011920929d).add(ViewProperty.ALPHA, 0.0d), new AnimState("WhiteNoiseInEnd").add(ViewProperty.SCALE_X, 1.0d).add(ViewProperty.SCALE_Y, 1.0d).add(ViewProperty.ALPHA, 1.0d), new AnimConfig().addListeners(new TransitionListener() { // from class: com.android.deskclock.addition.MiuiFolme.5
                        @Override // miuix.animation.listener.TransitionListener
                        public void onComplete(Object obj) {
                            MiuiFolme.cleanFolme(view);
                        }
                    }).setDelay(i * 50).setEase(EaseManager.getStyle(-2, 0.9f, 0.3f)));
                }
            } catch (Exception e) {
                Log.e(TAG, ERROR_MSG + e.getMessage());
                return;
            }
        }
    }

    public static void animateWhiteNoiseOut(TransitionListener transitionListener, View... viewArr) {
        if (viewArr == null) {
            return;
        }
        for (int i = 0; i < viewArr.length; i++) {
            try {
                final View view = viewArr[i];
                if (view != null) {
                    AnimState animStateAdd = new AnimState("WhiteNoiseOutStart").add(ViewProperty.SCALE_X, 1.0d).add(ViewProperty.SCALE_Y, 1.0d).add(ViewProperty.ALPHA, 1.0d);
                    AnimState animStateAdd2 = new AnimState("WhiteNoiseOutEnd").add(ViewProperty.SCALE_X, 0.8999999761581421d).add(ViewProperty.SCALE_Y, 0.8999999761581421d).add(ViewProperty.ALPHA, 0.0d);
                    IFolme iFolmeUseAt = Folme.useAt(view);
                    AnimConfig ease = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.2f));
                    if (i == viewArr.length - 1 && transitionListener != null) {
                        ease.addListeners(transitionListener);
                    }
                    ease.addListeners(new TransitionListener() { // from class: com.android.deskclock.addition.MiuiFolme.6
                        @Override // miuix.animation.listener.TransitionListener
                        public void onComplete(Object obj) {
                            MiuiFolme.cleanFolme(view);
                        }
                    });
                    iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, ease);
                }
            } catch (Exception e) {
                Log.e(TAG, ERROR_MSG + e.getMessage());
                return;
            }
        }
    }

    public static void animateTimerItemOut(TransitionListener transitionListener, final View view) {
        if (view == null || view == null) {
            return;
        }
        try {
            AnimState animStateAdd = new AnimState("TimerItemOutStart").add(ViewProperty.SCALE_X, 1.0d).add(ViewProperty.SCALE_Y, 1.0d).add(ViewProperty.ALPHA, 1.0d);
            AnimState animStateAdd2 = new AnimState("TimerItemOutEnd").add(ViewProperty.SCALE_X, 0.8999999761581421d).add(ViewProperty.SCALE_Y, 0.8999999761581421d).add(ViewProperty.ALPHA, 0.0d);
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig ease = new AnimConfig().addListeners(new TransitionListener() { // from class: com.android.deskclock.addition.MiuiFolme.7
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    MiuiFolme.cleanFolme(view);
                    MiuiFolme.touch(view);
                }
            }).setEase(EaseManager.getStyle(-2, 0.9f, 0.2f));
            if (transitionListener != null) {
                ease.addListeners(transitionListener);
            }
            iFolmeUseAt.state().fromTo(animStateAdd, animStateAdd2, ease);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void quickAnimOut(final View view, TransitionListener transitionListener) {
        if (view == null) {
            return;
        }
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            AnimConfig animConfig = new AnimConfig();
            animConfig.addListeners(new TransitionListener() { // from class: com.android.deskclock.addition.MiuiFolme.8
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    MiuiFolme.cleanFolme(view);
                }
            });
            animConfig.setEase(EaseManager.getStyle(-2, 0.9f, 0.15f));
            if (transitionListener != null) {
                animConfig.addListeners(transitionListener);
            }
            iFolmeUseAt.visible().setShow().hide(animConfig);
        } catch (Exception e) {
            Log.e(TAG, ERROR_MSG + e.getMessage());
        }
    }

    public static void animHide(View view, float f, TransitionListener transitionListener) {
        if (view == null) {
            return;
        }
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.visible().cancel();
            iFolmeUseAt.visible().setScale(f, IVisibleStyle.VisibleType.HIDE).hide(new AnimConfig().addListeners(transitionListener));
        } catch (Exception e) {
            Log.w(TAG, "animShow error: " + e.getMessage());
        }
    }

    public static void animShow(View view, float f, TransitionListener transitionListener) {
        if (view == null) {
            return;
        }
        try {
            IFolme iFolmeUseAt = Folme.useAt(view);
            iFolmeUseAt.visible().cancel();
            iFolmeUseAt.visible().setScale(f, IVisibleStyle.VisibleType.SHOW).show(new AnimConfig().addListeners(transitionListener));
        } catch (Exception e) {
            Log.w(TAG, "animShow error: " + e.getMessage());
        }
    }

    public static void setTouchUp(View view) {
        try {
            Folme.useAt(view).touch().setTouchUp();
        } catch (Exception e) {
            Log.w(TAG, "setTouchUp error: " + e.getMessage());
        }
    }
}
