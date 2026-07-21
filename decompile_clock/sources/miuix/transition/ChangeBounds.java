package miuix.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.ViewCompat;
import androidx.transition.MiuixTransitionUtils;
import java.lang.ref.WeakReference;
import java.util.Map;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.property.ViewProperty;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ChangeBounds extends MiuixTransition {
    private static final String END_TAG = "changebounds_end";
    protected static final String PROPNAME_BOUNDS = "android:transition:bounds";
    protected static final String PROPNAME_HEIGHT = "android:transition:height";
    protected static final String PROPNAME_PARENT = "android:transition:parent";
    protected static final String PROPNAME_WIDTH = "android:transition:width";
    protected static final String PROPNAME_WINDOW_X = "android:transition:windowX";
    protected static final String PROPNAME_WINDOW_Y = "android:transition:windowY";
    protected static final String PROPNAME_X = "android:transition:x";
    protected static final String PROPNAME_Y = "android:transition:y";
    private static final String START_TAG = "changebounds_start";
    private static final String[] sTransitionProperties;
    static Map<String, ViewProperty> sViewPropertyMap;
    protected final BottomProperty mBottomProperty;
    protected final LeftProperty mLeftProperty;
    protected boolean mReparent;
    protected final RightProperty mRightProperty;
    private int[] mTempLocation;
    protected final TopProperty mTopProperty;

    static {
        ArrayMap arrayMap = new ArrayMap();
        sViewPropertyMap = arrayMap;
        sTransitionProperties = new String[]{PROPNAME_BOUNDS, PROPNAME_PARENT, PROPNAME_WINDOW_X, PROPNAME_WINDOW_Y};
        arrayMap.put(PROPNAME_X, ViewProperty.X);
        sViewPropertyMap.put(PROPNAME_Y, ViewProperty.Y);
        sViewPropertyMap.put(PROPNAME_WIDTH, ViewProperty.WIDTH);
        sViewPropertyMap.put(PROPNAME_HEIGHT, ViewProperty.HEIGHT);
    }

    public ChangeBounds() {
        this.mReparent = false;
        this.mTempLocation = new int[2];
        this.mLeftProperty = new LeftProperty();
        this.mTopProperty = new TopProperty();
        this.mRightProperty = new RightProperty();
        this.mBottomProperty = new BottomProperty();
    }

    public ChangeBounds(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mReparent = false;
        this.mTempLocation = new int[2];
        this.mLeftProperty = new LeftProperty();
        this.mTopProperty = new TopProperty();
        this.mRightProperty = new RightProperty();
        this.mBottomProperty = new BottomProperty();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ChangeBounds);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.ChangeBounds_reparent, false);
        typedArrayObtainStyledAttributes.recycle();
        setReparent(z);
    }

    @Override // miuix.transition.MiuixTransition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    protected void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        if (!ViewCompat.isLaidOut(view) && view.getWidth() == 0 && view.getHeight() == 0) {
            return;
        }
        transitionValues.values.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
        for (Map.Entry<String, ViewProperty> entry : sViewPropertyMap.entrySet()) {
            transitionValues.values.put(entry.getKey(), Float.valueOf(entry.getValue().getValue(view)));
        }
        transitionValues.values.put(PROPNAME_PARENT, transitionValues.view.getParent());
        if (this.mReparent) {
            transitionValues.view.getLocationInWindow(this.mTempLocation);
            transitionValues.values.put(PROPNAME_WINDOW_X, Integer.valueOf(this.mTempLocation[0]));
            transitionValues.values.put(PROPNAME_WINDOW_Y, Integer.valueOf(this.mTempLocation[1]));
        }
    }

    @Override // miuix.transition.MiuixTransition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // miuix.transition.MiuixTransition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // miuix.transition.MiuixTransition
    public void createAnimator(final ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues == null || transitionValues2 == null) {
            return;
        }
        Map<String, Object> map = transitionValues.values;
        Map<String, Object> map2 = transitionValues2.values;
        ViewGroup viewGroup2 = (ViewGroup) map.get(PROPNAME_PARENT);
        ViewGroup viewGroup3 = (ViewGroup) map2.get(PROPNAME_PARENT);
        if (viewGroup2 == null || viewGroup3 == null) {
            return;
        }
        final View view = transitionValues2.view;
        final AnimConfig animConfig = new AnimConfig();
        animConfig.addListeners(new TransitionListener() { // from class: miuix.transition.ChangeBounds.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                if (obj == ChangeBounds.END_TAG) {
                    if (ChangeBounds.this.mNumInstances == 0) {
                        ChangeBounds.this.onTransitionStart();
                    }
                    ChangeBounds.this.mNumInstances++;
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (obj == ChangeBounds.END_TAG) {
                    ChangeBounds.this.mNumInstances--;
                    if (ChangeBounds.this.mNumInstances == 0) {
                        animConfig.removeListeners(this);
                        ChangeBounds.this.onTransitionEnd();
                    }
                }
            }
        });
        if (parentMatches(viewGroup2, viewGroup3)) {
            Rect rect = (Rect) transitionValues.values.get(PROPNAME_BOUNDS);
            Rect rect2 = (Rect) transitionValues2.values.get(PROPNAME_BOUNDS);
            int i = rect.left;
            int i2 = rect2.left;
            int i3 = rect.top;
            int i4 = rect2.top;
            int i5 = rect.right;
            int i6 = rect2.right;
            int i7 = rect.bottom;
            int i8 = rect2.bottom;
            if (i != i2 || i3 != i4 || i5 != i6 || i7 != i8) {
                final ViewGroup viewGroup4 = (ViewGroup) view.getParent();
                if (Build.VERSION.SDK_INT >= 29) {
                    ((ViewGroup) view.getParent()).suppressLayout(true);
                    animConfig.addListeners(new TransitionListener() { // from class: miuix.transition.ChangeBounds.2
                        @Override // miuix.animation.listener.TransitionListener
                        public void onComplete(Object obj) {
                            if (obj == ChangeBounds.END_TAG) {
                                animConfig.removeListeners(this);
                                viewGroup4.suppressLayout(false);
                            }
                        }
                    });
                }
                ViewBounds viewBounds = new ViewBounds(view);
                ViewUtils.setLeftTopRightBottom(view, i, i3, i5, i7);
                IStateStyle iStateStyleUseValue = Folme.useValue(viewBounds);
                AnimState animStateAdd = new AnimState(START_TAG).add((ViewProperty) this.mLeftProperty, i, 4).add((ViewProperty) this.mTopProperty, i3, 4).add((ViewProperty) this.mRightProperty, i5, 4).add((ViewProperty) this.mBottomProperty, i7, 4);
                AnimState animStateAdd2 = new AnimState(END_TAG).add((ViewProperty) this.mLeftProperty, i2, 4).add((ViewProperty) this.mTopProperty, i4, 4).add((ViewProperty) this.mRightProperty, i6, 4).add((ViewProperty) this.mBottomProperty, i8, 4);
                iStateStyleUseValue.setTo(animStateAdd);
                addTransitionRunner(new MiuixTransition.TransitionRunner(viewBounds, animStateAdd, animStateAdd2, getAnimConfig(), animConfig));
            }
            return;
        }
        viewGroup.getLocationInWindow(this.mTempLocation);
        int iIntValue = ((Integer) transitionValues.values.get(PROPNAME_WINDOW_X)).intValue() - this.mTempLocation[0];
        int iIntValue2 = ((Integer) transitionValues.values.get(PROPNAME_WINDOW_Y)).intValue() - this.mTempLocation[1];
        int iIntValue3 = ((Integer) transitionValues2.values.get(PROPNAME_WINDOW_X)).intValue() - this.mTempLocation[0];
        int iIntValue4 = ((Integer) transitionValues2.values.get(PROPNAME_WINDOW_Y)).intValue() - this.mTempLocation[1];
        float fFloatValue = ((Float) transitionValues.values.get(PROPNAME_WIDTH)).floatValue() + 0.0f;
        float fFloatValue2 = ((Float) transitionValues.values.get(PROPNAME_HEIGHT)).floatValue() + 0.0f;
        float fFloatValue3 = ((Float) transitionValues2.values.get(PROPNAME_WIDTH)).floatValue() + 0.0f;
        float fFloatValue4 = ((Float) transitionValues2.values.get(PROPNAME_HEIGHT)).floatValue() + 0.0f;
        Rect rect3 = new Rect();
        transitionValues.view.getLocalVisibleRect(rect3);
        if (rect3.top > 0 || rect3.bottom < fFloatValue2) {
            return;
        }
        if (iIntValue == iIntValue3 && iIntValue2 == iIntValue4) {
            return;
        }
        final View viewCopyViewImage = MiuixTransitionUtils.copyViewImage(viewGroup, view, viewGroup2);
        viewGroup.getOverlay().add(viewCopyViewImage);
        final float alpha = view.getAlpha();
        view.setAlpha(0.0f);
        AnimState animStateAdd3 = new AnimState(START_TAG).add(ViewProperty.X, iIntValue).add(ViewProperty.Y, iIntValue2).add(ViewProperty.WIDTH, fFloatValue).add(ViewProperty.HEIGHT, fFloatValue2);
        AnimState animStateAdd4 = new AnimState(END_TAG).add(ViewProperty.X, iIntValue3).add(ViewProperty.Y, iIntValue4).add(ViewProperty.WIDTH, fFloatValue3).add(ViewProperty.HEIGHT, fFloatValue4);
        animConfig.addListeners(new TransitionListener() { // from class: miuix.transition.ChangeBounds.3
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (obj == ChangeBounds.END_TAG) {
                    animConfig.removeListeners(this);
                    viewGroup.getOverlay().remove(viewCopyViewImage);
                    view.setAlpha(alpha);
                }
            }
        });
        Folme.useAt(viewCopyViewImage).state().setTo(animStateAdd3);
        addTransitionRunner(new MiuixTransition.TransitionRunner(viewCopyViewImage, animStateAdd3, animStateAdd4, getAnimConfig(), animConfig));
    }

    protected boolean parentMatches(View view, View view2) {
        if (!this.mReparent) {
            return true;
        }
        TransitionValues matchedTransitionValues = getMatchedTransitionValues(view, true);
        if (matchedTransitionValues == null) {
            if (view == view2) {
                return true;
            }
        } else if (view2 == matchedTransitionValues.view) {
            return true;
        }
        return false;
    }

    public ChangeBounds setReparent(boolean z) {
        this.mReparent = z;
        return this;
    }

    protected static class ViewBounds {
        private WeakReference<View> mView;
        int left = -1;
        int top = -1;
        int right = -1;
        int bottom = -1;

        ViewBounds(View view) {
            this.mView = new WeakReference<>(view);
        }

        public void setLeft(int i) {
            if (this.left != i) {
                this.left = i;
                setLeftTopRightBottom();
            }
        }

        int getLeft() {
            return this.left;
        }

        public void setRight(int i) {
            if (this.right != i) {
                this.right = i;
                setLeftTopRightBottom();
            }
        }

        int getRight() {
            return this.right;
        }

        public void setTop(int i) {
            if (this.top != i) {
                this.top = i;
                setLeftTopRightBottom();
            }
        }

        int getTop() {
            return this.top;
        }

        public void setBottom(int i) {
            if (this.bottom != i) {
                this.bottom = i;
                setLeftTopRightBottom();
            }
        }

        int getBottom() {
            return this.bottom;
        }

        private void setLeftTopRightBottom() {
            View view = this.mView.get();
            if (view != null) {
                if (view instanceof TextView) {
                    view.measure(View.MeasureSpec.makeMeasureSpec(this.right - this.left, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(this.bottom - this.top, BasicMeasure.EXACTLY));
                    view.layout(this.left, this.top, this.right, this.bottom);
                } else {
                    ViewUtils.setLeftTopRightBottom(view, this.left, this.top, this.right, this.bottom);
                }
            }
        }
    }

    protected static class LeftProperty extends ViewProperty implements IIntValueProperty<ViewBounds> {
        @Override // miuix.animation.property.FloatProperty
        public float getValue(View view) {
            return 0.0f;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(View view, float f) {
        }

        public LeftProperty() {
            super("left");
        }

        @Override // miuix.animation.property.IIntValueProperty
        public void setIntValue(ViewBounds viewBounds, int i) {
            viewBounds.setLeft(i);
        }

        @Override // miuix.animation.property.IIntValueProperty
        public int getIntValue(ViewBounds viewBounds) {
            return viewBounds.getLeft();
        }
    }

    protected static class RightProperty extends ViewProperty implements IIntValueProperty<ViewBounds> {
        @Override // miuix.animation.property.FloatProperty
        public float getValue(View view) {
            return 0.0f;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(View view, float f) {
        }

        public RightProperty() {
            super("right");
        }

        @Override // miuix.animation.property.IIntValueProperty
        public void setIntValue(ViewBounds viewBounds, int i) {
            viewBounds.setRight(i);
        }

        @Override // miuix.animation.property.IIntValueProperty
        public int getIntValue(ViewBounds viewBounds) {
            return viewBounds.getRight();
        }
    }

    protected static class TopProperty extends ViewProperty implements IIntValueProperty<ViewBounds> {
        @Override // miuix.animation.property.FloatProperty
        public float getValue(View view) {
            return 0.0f;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(View view, float f) {
        }

        public TopProperty() {
            super("top");
        }

        @Override // miuix.animation.property.IIntValueProperty
        public void setIntValue(ViewBounds viewBounds, int i) {
            viewBounds.setTop(i);
        }

        @Override // miuix.animation.property.IIntValueProperty
        public int getIntValue(ViewBounds viewBounds) {
            return viewBounds.getTop();
        }
    }

    protected static class BottomProperty extends ViewProperty implements IIntValueProperty<ViewBounds> {
        @Override // miuix.animation.property.FloatProperty
        public float getValue(View view) {
            return 0.0f;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(View view, float f) {
        }

        public BottomProperty() {
            super("bottom");
        }

        @Override // miuix.animation.property.IIntValueProperty
        public void setIntValue(ViewBounds viewBounds, int i) {
            viewBounds.setBottom(i);
        }

        @Override // miuix.animation.property.IIntValueProperty
        public int getIntValue(ViewBounds viewBounds) {
            return viewBounds.getBottom();
        }
    }
}
