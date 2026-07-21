package miuix.slidingwidget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import java.util.HashMap;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.ViewProperty;

/* JADX INFO: loaded from: classes3.dex */
public class SlidingLinearLayout extends LinearLayout {
    private final HashMap<View, Pair<Float, Float>> preAddViewPairs;
    private final int[] preLayout;
    private final HashMap<View, Pair<Float, Float>> preRemoveViewPairs;

    public SlidingLinearLayout(Context context) {
        this(context, null);
    }

    public SlidingLinearLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SlidingLinearLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.preAddViewPairs = new HashMap<>();
        this.preRemoveViewPairs = new HashMap<>();
        this.preLayout = new int[4];
        setLayoutTransition(null);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float fFloatValue;
        float fFloatValue2;
        float fFloatValue3;
        float fFloatValue4;
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            int childCount = getChildCount();
            char c = 0;
            boolean z2 = getOrientation() != 1 ? Math.abs(this.preLayout[0] - i) > Math.abs(this.preLayout[2] - i3) : Math.abs(this.preLayout[1] - i2) > Math.abs(this.preLayout[3] - i4);
            int i5 = 0;
            while (i5 < childCount) {
                View childAt = getChildAt(i5);
                HashMap<View, Pair<Float, Float>> map = this.preAddViewPairs;
                if (map == null || map.size() <= 0) {
                    i5 = i5;
                } else {
                    Pair<Float, Float> pair = this.preAddViewPairs.get(childAt);
                    if (pair != null && childAt.getVisibility() != 8) {
                        if ((childAt.getX() != ((Float) pair.first).floatValue() || childAt.getY() != ((Float) pair.second).floatValue()) && !z2) {
                            fFloatValue3 = ((Float) pair.first).floatValue() - childAt.getX();
                            fFloatValue4 = ((Float) pair.second).floatValue() - childAt.getY();
                        } else if (childAt.getX() == ((Float) pair.first).floatValue() && childAt.getY() == ((Float) pair.second).floatValue() && z2) {
                            int[] iArr = this.preLayout;
                            float f = iArr[c] - i;
                            fFloatValue4 = iArr[1] - i2;
                            fFloatValue3 = f;
                        } else {
                            fFloatValue3 = 0.0f;
                            fFloatValue4 = 0.0f;
                        }
                        AnimState animStateAdd = new AnimState("start").add(ViewProperty.TRANSLATION_X, fFloatValue3).add(ViewProperty.TRANSLATION_Y, fFloatValue4);
                        Folme.useAt(childAt).state().setTo(animStateAdd).fromTo(animStateAdd, new AnimState("end").add(ViewProperty.TRANSLATION_X, 0.0d).add(ViewProperty.TRANSLATION_Y, 0.0d), new AnimConfig[0]);
                    }
                    this.preAddViewPairs.remove(childAt);
                }
                HashMap<View, Pair<Float, Float>> map2 = this.preRemoveViewPairs;
                if (map2 != null && map2.size() > 0) {
                    Pair<Float, Float> pair2 = this.preRemoveViewPairs.get(childAt);
                    if (pair2 != null && childAt.getVisibility() != 8) {
                        if ((childAt.getX() != ((Float) pair2.first).floatValue() || childAt.getY() != ((Float) pair2.second).floatValue()) && !z2) {
                            fFloatValue = ((Float) pair2.first).floatValue() - childAt.getX();
                            fFloatValue2 = ((Float) pair2.second).floatValue() - childAt.getY();
                        } else if (childAt.getX() == ((Float) pair2.first).floatValue() && childAt.getY() == ((Float) pair2.second).floatValue() && z2) {
                            int[] iArr2 = this.preLayout;
                            fFloatValue = iArr2[0] - i;
                            fFloatValue2 = iArr2[1] - i2;
                        } else {
                            fFloatValue2 = 0.0f;
                            fFloatValue = 0.0f;
                        }
                        AnimState animStateAdd2 = new AnimState("start").add(ViewProperty.TRANSLATION_X, fFloatValue).add(ViewProperty.TRANSLATION_Y, fFloatValue2);
                        Folme.useAt(childAt).state().setTo(animStateAdd2).fromTo(animStateAdd2, new AnimState("end").add(ViewProperty.TRANSLATION_X, 0.0d).add(ViewProperty.TRANSLATION_Y, 0.0d), new AnimConfig[0]);
                    }
                    this.preRemoveViewPairs.remove(childAt);
                }
                i5++;
                c = 0;
            }
            HashMap<View, Pair<Float, Float>> map3 = this.preAddViewPairs;
            if (map3 != null) {
                map3.clear();
            }
            HashMap<View, Pair<Float, Float>> map4 = this.preRemoveViewPairs;
            if (map4 != null) {
                map4.clear();
            }
            int[] iArr3 = this.preLayout;
            iArr3[0] = i;
            iArr3[1] = i2;
            iArr3[2] = i3;
            iArr3[3] = i4;
        }
    }

    public void addViewSliding(View view) {
        preAddView();
        addView(view);
        show(view);
    }

    public void addViewSliding(View view, int i) {
        preAddView();
        addView(view, i);
        show(view);
    }

    public void addViewSliding(View view, int i, int i2) {
        preAddView();
        addView(view, i, i2);
        show(view);
    }

    public void addViewSliding(View view, LinearLayout.LayoutParams layoutParams) {
        preAddView();
        addView(view, layoutParams);
        show(view);
    }

    public void addViewSliding(View view, int i, LinearLayout.LayoutParams layoutParams) {
        preAddView();
        addView(view, i, layoutParams);
        show(view);
    }

    public void removeViewSliding(final View view) {
        preRemoveView(view);
        Folme.useAt(view).visible().setFlags(1L).setShow().hide(new AnimConfig().addListeners(new TransitionListener() { // from class: miuix.slidingwidget.widget.SlidingLinearLayout.1
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                SlidingLinearLayout.this.removeView(view);
            }
        }));
    }

    public void removeViewSlidingAt(final int i) {
        View childAt = getChildAt(i);
        preRemoveView(childAt);
        Folme.useAt(childAt).visible().setFlags(1L).setShow().hide(new AnimConfig().addListeners(new TransitionListener() { // from class: miuix.slidingwidget.widget.SlidingLinearLayout.2
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (i < SlidingLinearLayout.this.getChildCount()) {
                    try {
                        SlidingLinearLayout.this.removeViewAt(i);
                    } catch (Exception e) {
                        Log.e("SlidingLinear", "error in removeViewSlidingAt, index " + i + " " + e);
                    }
                }
            }
        }));
    }

    public void removeViewsSliding(final int i, final int i2) {
        preRemoveView(i, i2);
        final int i3 = i + i2;
        for (int i4 = i; i4 < i3; i4++) {
            View childAt = getChildAt(i4);
            if (childAt.getVisibility() != 8) {
                final int i5 = i4;
                Folme.useAt(childAt).visible().setFlags(1L).setShow().hide(new AnimConfig().addListeners(new TransitionListener() { // from class: miuix.slidingwidget.widget.SlidingLinearLayout.3
                    @Override // miuix.animation.listener.TransitionListener
                    public void onComplete(Object obj) {
                        if (i5 != i3 - 1 || i + i2 > SlidingLinearLayout.this.getChildCount()) {
                            return;
                        }
                        try {
                            SlidingLinearLayout.this.removeViews(i, i2);
                        } catch (Exception e) {
                            Log.e("SlidingLinear", "error in removeViewsSliding,start " + i + " count " + i2 + " " + e);
                        }
                    }
                }));
            }
        }
    }

    private void show(View view) {
        Folme.useAt(view).visible().setFlags(1L).setFlags(1L).setShowDelay(100L).setHide().show(new AnimConfig[0]);
    }

    private void preAddView() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            this.preAddViewPairs.put(childAt, new Pair<>(Float.valueOf(childAt.getX()), Float.valueOf(childAt.getY())));
        }
    }

    private void preRemoveView(View view) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (view != childAt && childAt.getVisibility() != 8) {
                this.preRemoveViewPairs.put(childAt, new Pair<>(Float.valueOf(childAt.getX()), Float.valueOf(childAt.getY())));
            }
        }
    }

    private void preRemoveView(int i, int i2) {
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if ((i3 < i || i3 >= i + i2) && childAt.getVisibility() != 8) {
                this.preRemoveViewPairs.put(childAt, new Pair<>(Float.valueOf(childAt.getX()), Float.valueOf(childAt.getY())));
            }
        }
    }
}
