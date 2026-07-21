package miuix.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import androidx.transition.MiuixTransitionUtils;
import miuix.animation.listener.TransitionListener;

/* JADX INFO: loaded from: classes3.dex */
public class Visibility extends MiuixTransition {
    public static final int MODE_IN = 1;
    public static final int MODE_OUT = 2;
    private static final int OVERLAY_VIEW_TAG = 1;
    private static final String PROPNAME_SCREEN_LOCATION = "android:visibility:screenLocation";
    private boolean mForceUseOverlay;
    private int mMode;
    private static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
    private static final String PROPNAME_PARENT = "android:visibility:parent";
    private static final String[] sTransitionProperties = {PROPNAME_VISIBILITY, PROPNAME_PARENT};

    public void onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
    }

    public void onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2, TransitionListener transitionListener) {
    }

    private static class VisibilityInfo {
        ViewGroup endParent;
        int endVisibility;
        boolean fadeIn;
        ViewGroup startParent;
        int startVisibility;
        boolean visibilityChange;

        private VisibilityInfo() {
        }
    }

    public Visibility() {
        this.mMode = 3;
        this.mForceUseOverlay = false;
    }

    public Visibility(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMode = 3;
        this.mForceUseOverlay = false;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.VisibilityTransition);
        int i = typedArrayObtainStyledAttributes.getInt(R.styleable.VisibilityTransition_transitionVisibilityMode, 0);
        typedArrayObtainStyledAttributes.recycle();
        if (i != 0) {
            setMode(i);
        }
    }

    public Visibility(int i) {
        this.mMode = 3;
        this.mForceUseOverlay = false;
        setMode(i);
    }

    public void setMode(int i) {
        if ((i & (-4)) != 0) {
            throw new IllegalArgumentException("Only MODE_IN and MODE_OUT flags are allowed");
        }
        this.mMode = i;
    }

    public int getMode() {
        return this.mMode;
    }

    @Override // miuix.transition.MiuixTransition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public Visibility setForceUseOverlay(boolean z) {
        this.mForceUseOverlay = z;
        return this;
    }

    protected void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        transitionValues.values.put(PROPNAME_VISIBILITY, Integer.valueOf(view.getVisibility()));
        transitionValues.values.put(PROPNAME_PARENT, view.getParent());
        int[] iArr = new int[2];
        transitionValues.view.getLocationOnScreen(iArr);
        transitionValues.values.put(PROPNAME_SCREEN_LOCATION, iArr);
    }

    @Override // miuix.transition.MiuixTransition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // miuix.transition.MiuixTransition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public boolean isVisible(TransitionValues transitionValues) {
        if (transitionValues == null) {
            return false;
        }
        return ((Integer) transitionValues.values.get(PROPNAME_VISIBILITY)).intValue() == 0 && ((View) transitionValues.values.get(PROPNAME_PARENT)) != null;
    }

    private static VisibilityInfo getVisibilityChangeInfo(TransitionValues transitionValues, TransitionValues transitionValues2) {
        VisibilityInfo visibilityInfo = new VisibilityInfo();
        visibilityInfo.visibilityChange = false;
        visibilityInfo.fadeIn = false;
        if (transitionValues != null && transitionValues.values.containsKey(PROPNAME_VISIBILITY)) {
            visibilityInfo.startVisibility = ((Integer) transitionValues.values.get(PROPNAME_VISIBILITY)).intValue();
            visibilityInfo.startParent = (ViewGroup) transitionValues.values.get(PROPNAME_PARENT);
        } else {
            visibilityInfo.startVisibility = -1;
            visibilityInfo.startParent = null;
        }
        if (transitionValues2 != null && transitionValues2.values.containsKey(PROPNAME_VISIBILITY)) {
            visibilityInfo.endVisibility = ((Integer) transitionValues2.values.get(PROPNAME_VISIBILITY)).intValue();
            visibilityInfo.endParent = (ViewGroup) transitionValues2.values.get(PROPNAME_PARENT);
        } else {
            visibilityInfo.endVisibility = -1;
            visibilityInfo.endParent = null;
        }
        if (transitionValues != null && transitionValues2 != null) {
            if (visibilityInfo.startVisibility == visibilityInfo.endVisibility && visibilityInfo.startParent == visibilityInfo.endParent) {
                return visibilityInfo;
            }
            if (visibilityInfo.startVisibility != visibilityInfo.endVisibility) {
                if (visibilityInfo.startVisibility == 0) {
                    visibilityInfo.fadeIn = false;
                    visibilityInfo.visibilityChange = true;
                } else if (visibilityInfo.endVisibility == 0) {
                    visibilityInfo.fadeIn = true;
                    visibilityInfo.visibilityChange = true;
                }
            } else if (visibilityInfo.startParent != visibilityInfo.endParent) {
                if (visibilityInfo.endParent == null) {
                    visibilityInfo.fadeIn = false;
                    visibilityInfo.visibilityChange = true;
                } else if (visibilityInfo.startParent == null) {
                    visibilityInfo.fadeIn = true;
                    visibilityInfo.visibilityChange = true;
                }
            }
        } else if (transitionValues == null && visibilityInfo.endVisibility == 0) {
            visibilityInfo.fadeIn = true;
            visibilityInfo.visibilityChange = true;
        } else if (transitionValues2 == null && visibilityInfo.startVisibility == 0) {
            visibilityInfo.fadeIn = false;
            visibilityInfo.visibilityChange = true;
        }
        return visibilityInfo;
    }

    @Override // miuix.transition.MiuixTransition
    public void createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        VisibilityInfo visibilityChangeInfo = getVisibilityChangeInfo(transitionValues, transitionValues2);
        if (visibilityChangeInfo.visibilityChange) {
            if (visibilityChangeInfo.startParent == null && visibilityChangeInfo.endParent == null) {
                return;
            }
            if (visibilityChangeInfo.fadeIn) {
                onAppear(viewGroup, transitionValues, visibilityChangeInfo.startVisibility, transitionValues2, visibilityChangeInfo.endVisibility);
            } else {
                onDisappear(viewGroup, transitionValues, visibilityChangeInfo.startVisibility, transitionValues2, visibilityChangeInfo.endVisibility);
            }
        }
    }

    public void onAppear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        if ((this.mMode & 1) != 1 || transitionValues2 == null) {
            return;
        }
        if (transitionValues == null) {
            View view = (View) transitionValues2.view.getParent();
            if (getVisibilityChangeInfo(getMatchedTransitionValues(view, false), getTransitionValues(view, false)).visibilityChange) {
                return;
            }
        }
        onAppear(viewGroup, transitionValues2.view, transitionValues, transitionValues2);
    }

    /* JADX WARN: Code duplicated, block: B:25:0x0042  */
    /* JADX WARN: Code duplicated, block: B:45:0x008c  */
    public void onDisappear(ViewGroup viewGroup, TransitionValues transitionValues, int i, TransitionValues transitionValues2, int i2) {
        View view;
        boolean z;
        boolean z2;
        if ((this.mMode & 2) == 2 && transitionValues != null) {
            View view2 = transitionValues.view;
            final ViewGroupOverlay overlay = null;
            final View viewCopyViewImage = transitionValues2 != null ? transitionValues2.view : null;
            View view3 = (View) view2.getTag(1);
            if (view3 != null) {
                z2 = true;
                viewCopyViewImage = view3;
                view = null;
            } else {
                if (viewCopyViewImage == null || viewCopyViewImage.getParent() == null) {
                    if (viewCopyViewImage != null) {
                        view = null;
                        z = false;
                    } else {
                        viewCopyViewImage = null;
                        view = null;
                        z = true;
                    }
                } else if (i2 == 4 || view2 == viewCopyViewImage) {
                    view = viewCopyViewImage;
                    z = false;
                    viewCopyViewImage = null;
                } else {
                    viewCopyViewImage = null;
                    view = null;
                    z = true;
                }
                if (this.mForceUseOverlay || z) {
                    if (view2.getParent() == null) {
                        viewCopyViewImage = view2;
                    } else if (view2.getParent() instanceof View) {
                        View view4 = (View) view2.getParent();
                        if (!getVisibilityChangeInfo(getTransitionValues(view4, true), getMatchedTransitionValues(view4, true)).visibilityChange) {
                            viewCopyViewImage = MiuixTransitionUtils.copyViewImage(viewGroup, view2, view4);
                        } else {
                            int id = view4.getId();
                            if (view4.getParent() == null && id != -1 && viewGroup.findViewById(id) != null && this.mCanRemoveViews) {
                                viewCopyViewImage = view2;
                            }
                        }
                    }
                }
                z2 = false;
            }
            if (viewCopyViewImage == null) {
                if (view != null) {
                    view.setVisibility(0);
                    onDisappear(viewGroup, view, transitionValues, transitionValues2, (TransitionListener) null);
                    return;
                }
                return;
            }
            if (!z2) {
                overlay = viewGroup.getOverlay();
                int[] iArr = (int[]) transitionValues.values.get(PROPNAME_SCREEN_LOCATION);
                int i3 = iArr[0];
                int i4 = iArr[1];
                int[] iArr2 = new int[2];
                viewGroup.getLocationOnScreen(iArr2);
                viewCopyViewImage.offsetLeftAndRight((i3 - iArr2[0]) - viewCopyViewImage.getLeft());
                viewCopyViewImage.offsetTopAndBottom((i4 - iArr2[1]) - viewCopyViewImage.getTop());
                overlay.add(viewCopyViewImage);
            }
            view2.setTag(1);
            onDisappear(viewGroup, viewCopyViewImage, transitionValues, transitionValues2, new TransitionListener() { // from class: miuix.transition.Visibility.1
                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    overlay.remove(viewCopyViewImage);
                }
            });
        }
    }

    @Override // miuix.transition.MiuixTransition
    public boolean isTransitionRequired(TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues == null && transitionValues2 == null) {
            return false;
        }
        if (transitionValues != null && transitionValues2 != null && transitionValues2.values.containsKey(PROPNAME_VISIBILITY) != transitionValues.values.containsKey(PROPNAME_VISIBILITY)) {
            return false;
        }
        VisibilityInfo visibilityChangeInfo = getVisibilityChangeInfo(transitionValues, transitionValues2);
        if (visibilityChangeInfo.visibilityChange) {
            return visibilityChangeInfo.startVisibility == 0 || visibilityChangeInfo.endVisibility == 0;
        }
        return false;
    }
}
