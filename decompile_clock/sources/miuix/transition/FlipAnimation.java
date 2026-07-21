package miuix.transition;

import android.graphics.Point;
import android.view.View;
import java.util.ArrayList;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;
import miuix.core.util.MiuiBlurUtils;

/* JADX INFO: loaded from: classes3.dex */
public class FlipAnimation {
    private static final int HALF_ROTATION = 90;
    private FlipListener mFlipListener;
    private final ArrayList<Point> mColorModes = new ArrayList<>();
    private boolean mBlurEnabled = true;

    public interface FlipListener {
        void onFlip(boolean z, float f);
    }

    public void flip(final View view, final boolean z) {
        int rotationY;
        int rotationY2;
        AnimConfig animConfigAddListeners = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.72f, 0.5f)).addListeners(new TransitionListener() { // from class: miuix.transition.FlipAnimation.1
            private int lastRotationY = -1;
            private boolean mHasTriggeredNegative;
            private boolean mHasTriggeredPositive;

            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                this.mHasTriggeredPositive = false;
                this.mHasTriggeredNegative = false;
                this.lastRotationY = -1;
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                int rotationY3 = ((int) view.getRotationY()) % 360;
                int i = this.lastRotationY;
                int i2 = rotationY3 < 0 ? rotationY3 + 360 : rotationY3;
                int i3 = i < 0 ? i + 360 : i;
                if (i != -1) {
                    boolean z2 = z;
                    if (z2 && !this.mHasTriggeredPositive && (i2 / 90) % 2 == 1 && (rotationY3 / 90) - (i / 90) == 1) {
                        this.mHasTriggeredPositive = true;
                        if (FlipAnimation.this.mFlipListener != null) {
                            FlipAnimation.this.mFlipListener.onFlip(true, view.getRotationY());
                        }
                    } else if (!z2 && !this.mHasTriggeredNegative && (i3 / 90) % 2 == 1 && (rotationY3 / 90) - (i / 90) == -1) {
                        this.mHasTriggeredNegative = true;
                        if (FlipAnimation.this.mFlipListener != null) {
                            FlipAnimation.this.mFlipListener.onFlip(false, view.getRotationY());
                        }
                    }
                }
                this.lastRotationY = rotationY3;
                if (i2 > 90 && i2 < 270) {
                    view.setScaleX(-1.0f);
                } else {
                    view.setScaleX(1.0f);
                }
                int iAbs = Math.abs((int) (Math.sin((((double) i2) * 3.141592653589793d) / 180.0d) * 40.0d));
                FlipAnimation flipAnimation = FlipAnimation.this;
                flipAnimation.setMiSelfBlur(view, iAbs, flipAnimation.mColorModes);
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                view.setTag(R.id.miuix_animation_tag_is_flip, null);
                FlipAnimation flipAnimation = FlipAnimation.this;
                flipAnimation.setMiSelfBlur(view, 0, flipAnimation.mColorModes);
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                view.setTag(R.id.miuix_animation_tag_is_flip, null);
                FlipAnimation flipAnimation = FlipAnimation.this;
                flipAnimation.setMiSelfBlur(view, 0, flipAnimation.mColorModes);
            }
        });
        view.setTag(R.id.miuix_animation_tag_is_flip, true);
        Object tag = view.getTag(R.id.miuix_animation_tag_flip_rotation_y);
        if (z) {
            if (tag != null) {
                rotationY2 = ((Integer) tag).intValue();
            } else {
                rotationY2 = (int) view.getRotationY();
            }
            int i = ((rotationY2 + 180) / 180) * 180;
            view.setTag(R.id.miuix_animation_tag_flip_rotation_y, Integer.valueOf(i));
            Folme.use(view).state().to(ViewProperty.ROTATION_Y, Integer.valueOf(i), animConfigAddListeners);
            return;
        }
        if (tag != null) {
            rotationY = ((Integer) tag).intValue();
        } else {
            rotationY = (int) view.getRotationY();
        }
        int i2 = ((rotationY - 180) / 180) * 180;
        view.setTag(R.id.miuix_animation_tag_flip_rotation_y, Integer.valueOf(i2));
        Folme.use(view).state().to(ViewProperty.ROTATION_Y, Integer.valueOf(i2), animConfigAddListeners);
    }

    public void setFlipListener(FlipListener flipListener) {
        this.mFlipListener = flipListener;
    }

    public void setBlurEnabled(boolean z) {
        this.mBlurEnabled = z;
    }

    public void reset(View view) {
        if (view == null) {
            return;
        }
        Folme.use(view).state().setTo(ViewProperty.ROTATION_Y, 0);
        view.setTag(R.id.miuix_animation_tag_flip_rotation_y, null);
        setMiSelfBlur(view, 0, null);
        view.setScaleX(1.0f);
    }

    public static boolean isInFlipAnimation(View view) {
        return view.getTag(R.id.miuix_animation_tag_is_flip) != null;
    }

    public void setMiSelfBlur(View view, int i, ArrayList<Point> arrayList) {
        if (!this.mBlurEnabled || view == null) {
            return;
        }
        MiuiBlurUtils.setSelfBlur(view, i, arrayList);
    }
}
