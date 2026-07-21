package miuix.transition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.IFolme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.ViewProperty;

/* JADX INFO: loaded from: classes3.dex */
public class LayoutTransition extends android.animation.LayoutTransition {
    private static long DEFAULT_DURATION = 300;
    private static final int FLAG_APPEARING = 1;
    private static final int FLAG_CHANGE_APPEARING = 4;
    private static final int FLAG_CHANGE_DISAPPEARING = 8;
    private static final int FLAG_CHANGING = 16;
    private static final int FLAG_DISAPPEARING = 2;
    private static final String TAG = "MIUIX LayoutTransition";
    private ArrayList<android.animation.LayoutTransition.TransitionListener> mListeners;
    private boolean mAnimateParentHierarchy = true;
    private final HashMap<View, ChangingAnimator> pendingAnimations = new HashMap<>();
    private final LinkedHashMap<View, ChangingAnimator> currentChangingAnimations = new LinkedHashMap<>();
    private final ArrayList<View> currentAppearingViews = new ArrayList<>();
    private final ArrayList<View> currentDisappearingViews = new ArrayList<>();
    private final HashMap<View, View.OnLayoutChangeListener> layoutChangeListenerMap = new HashMap<>();
    private AnimConfig mAppearingAnimConfig = new AnimConfig();
    private AnimConfig mDisappearingAnimConfig = new AnimConfig();
    private AnimConfig mChangingAppearingAnimConfig = new AnimConfig();
    private AnimConfig mChangingDisappearingAnimConfig = new AnimConfig();
    private AnimConfig mChangingAnimConfig = new AnimConfig();
    private int mTransitionTypes = 15;

    public void endChangingAnimations() {
    }

    @Override // android.animation.LayoutTransition
    public void setDuration(long j) {
    }

    public LayoutTransition() {
        this.mAppearingAnimConfig.setEase(FolmeEase.linear(500L));
        this.mDisappearingAnimConfig.setEase(FolmeEase.linear(100L));
    }

    public AnimConfig getAnimConfig(int i) {
        if (i == 0) {
            return this.mChangingAppearingAnimConfig;
        }
        if (i == 1) {
            return this.mChangingDisappearingAnimConfig;
        }
        if (i == 2) {
            return this.mAppearingAnimConfig;
        }
        if (i == 3) {
            return this.mDisappearingAnimConfig;
        }
        if (i != 4) {
            return null;
        }
        return this.mChangingAnimConfig;
    }

    @Override // android.animation.LayoutTransition
    public void enableTransitionType(int i) {
        if (i == 0) {
            this.mTransitionTypes |= 4;
            return;
        }
        if (i == 1) {
            this.mTransitionTypes |= 8;
            return;
        }
        if (i == 2) {
            this.mTransitionTypes |= 1;
        } else if (i == 3) {
            this.mTransitionTypes |= 2;
        } else {
            if (i != 4) {
                return;
            }
            this.mTransitionTypes |= 16;
        }
    }

    @Override // android.animation.LayoutTransition
    public void disableTransitionType(int i) {
        if (i == 0) {
            this.mTransitionTypes &= -5;
            return;
        }
        if (i == 1) {
            this.mTransitionTypes &= -9;
            return;
        }
        if (i == 2) {
            this.mTransitionTypes &= -2;
        } else if (i == 3) {
            this.mTransitionTypes &= -3;
        } else {
            if (i != 4) {
                return;
            }
            this.mTransitionTypes &= -17;
        }
    }

    @Override // android.animation.LayoutTransition
    public boolean isTransitionTypeEnabled(int i) {
        if (i == 0) {
            return (this.mTransitionTypes & 4) == 4;
        }
        if (i == 1) {
            return (this.mTransitionTypes & 8) == 8;
        }
        if (i == 2) {
            return (this.mTransitionTypes & 1) == 1;
        }
        if (i != 3) {
            return i == 4 && (this.mTransitionTypes & 16) == 16;
        }
        return (this.mTransitionTypes & 2) == 2;
    }

    @Override // android.animation.LayoutTransition
    public void setStartDelay(int i, long j) {
        super.setStartDelay(i, j);
    }

    @Override // android.animation.LayoutTransition
    public long getStartDelay(int i) {
        return super.getStartDelay(i);
    }

    @Override // android.animation.LayoutTransition
    public void setDuration(int i, long j) {
        super.setDuration(i, j);
    }

    @Override // android.animation.LayoutTransition
    public long getDuration(int i) {
        return super.getDuration(i);
    }

    @Override // android.animation.LayoutTransition
    public void setStagger(int i, long j) {
        super.setStagger(i, j);
    }

    @Override // android.animation.LayoutTransition
    public long getStagger(int i) {
        return super.getStagger(i);
    }

    @Override // android.animation.LayoutTransition
    public void setInterpolator(int i, TimeInterpolator timeInterpolator) {
        super.setInterpolator(i, timeInterpolator);
    }

    public void setAnimConfig(int i, AnimConfig animConfig) {
        if (i == 0) {
            this.mChangingAppearingAnimConfig = animConfig;
            return;
        }
        if (i == 1) {
            this.mChangingDisappearingAnimConfig = animConfig;
            return;
        }
        if (i == 2) {
            this.mAppearingAnimConfig = animConfig;
        } else if (i == 3) {
            this.mDisappearingAnimConfig = animConfig;
        } else {
            if (i != 4) {
                return;
            }
            this.mChangingAnimConfig = animConfig;
        }
    }

    @Override // android.animation.LayoutTransition
    public TimeInterpolator getInterpolator(int i) {
        return super.getInterpolator(i);
    }

    @Override // android.animation.LayoutTransition
    public void setAnimator(int i, Animator animator) {
        super.setAnimator(i, animator);
    }

    @Override // android.animation.LayoutTransition
    public Animator getAnimator(int i) {
        return super.getAnimator(i);
    }

    private void runChangeTransition(ViewGroup viewGroup, View view, int i) {
        ViewGroup viewGroup2;
        ViewTreeObserver viewTreeObserver = viewGroup.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            int childCount = viewGroup.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = viewGroup.getChildAt(i2);
                if (childAt != view) {
                    setupChangeAnimation(viewGroup, i, null, 0L, childAt);
                }
            }
            if (this.mAnimateParentHierarchy) {
                ViewGroup viewGroup3 = viewGroup;
                while (viewGroup3 != null) {
                    ViewParent parent = viewGroup3.getParent();
                    if (parent instanceof ViewGroup) {
                        viewGroup2 = (ViewGroup) parent;
                        setupChangeAnimation(viewGroup2, i, null, 0L, viewGroup3);
                    } else {
                        viewGroup2 = null;
                    }
                    viewGroup3 = viewGroup2;
                }
            }
            CleanupCallback cleanupCallback = new CleanupCallback(this.layoutChangeListenerMap, viewGroup);
            viewTreeObserver.addOnPreDrawListener(cleanupCallback);
            viewGroup.addOnAttachStateChangeListener(cleanupCallback);
        }
    }

    @Override // android.animation.LayoutTransition
    public void setAnimateParentHierarchy(boolean z) {
        this.mAnimateParentHierarchy = z;
    }

    private void setupChangeAnimation(final ViewGroup viewGroup, final int i, Animator animator, long j, final View view) {
        if (this.layoutChangeListenerMap.get(view) != null) {
            return;
        }
        if (view.getWidth() == 0 && view.getHeight() == 0) {
            return;
        }
        final int left = view.getLeft();
        final int top = view.getTop();
        final int right = view.getRight();
        final int bottom = view.getBottom();
        final int scrollX = view.getScrollX();
        final int scrollY = view.getScrollY();
        View.OnLayoutChangeListener onLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: miuix.transition.LayoutTransition.1
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view2, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                int left2 = view.getLeft();
                int top2 = view.getTop();
                int right2 = view.getRight();
                int bottom2 = view.getBottom();
                int scrollX2 = view.getScrollX();
                int scrollY2 = view.getScrollY();
                if (left == left2 && top == top2 && right == right2 && bottom == bottom2 && scrollX == scrollX2 && scrollY == scrollY2) {
                    return;
                }
                ChangingAnimator changingAnimator = (ChangingAnimator) LayoutTransition.this.currentChangingAnimations.get(view);
                if (changingAnimator == null) {
                    changingAnimator = new ChangingAnimator();
                    LayoutTransition.setLeftTopRightBottom(view, left, top, right, bottom);
                    changingAnimator.mTarget = view;
                    changingAnimator.initFromView(view);
                    changingAnimator.mAnimConfig = new AnimConfig().addListeners(new TransitionListener() { // from class: miuix.transition.LayoutTransition.1.1
                        @Override // miuix.animation.listener.TransitionListener
                        public void onBegin(Object obj) {
                            int i10;
                            if (LayoutTransition.this.hasListeners()) {
                                for (android.animation.LayoutTransition.TransitionListener transitionListener : (ArrayList) LayoutTransition.this.mListeners.clone()) {
                                    LayoutTransition layoutTransition = LayoutTransition.this;
                                    ViewGroup viewGroup2 = viewGroup;
                                    View view3 = view;
                                    if (i == 2) {
                                        i10 = 0;
                                    } else {
                                        i10 = i == 3 ? 1 : 4;
                                    }
                                    transitionListener.startTransition(layoutTransition, viewGroup2, view3, i10);
                                }
                            }
                        }

                        @Override // miuix.animation.listener.TransitionListener
                        public void onComplete(Object obj) {
                            int i10;
                            LayoutTransition.this.currentChangingAnimations.remove(view);
                            if (LayoutTransition.this.hasListeners()) {
                                for (android.animation.LayoutTransition.TransitionListener transitionListener : (ArrayList) LayoutTransition.this.mListeners.clone()) {
                                    LayoutTransition layoutTransition = LayoutTransition.this;
                                    ViewGroup viewGroup2 = viewGroup;
                                    View view3 = view;
                                    if (i == 2) {
                                        i10 = 0;
                                    } else {
                                        i10 = i == 3 ? 1 : 4;
                                    }
                                    transitionListener.endTransition(layoutTransition, viewGroup2, view3, i10);
                                }
                            }
                        }

                        @Override // miuix.animation.listener.TransitionListener
                        public void onCancel(Object obj) {
                            LayoutTransition.this.layoutChangeListenerMap.remove(view);
                        }
                    });
                }
                IFolme iFolmeUse = Folme.use(changingAnimator);
                iFolmeUse.setup(new AnimState().add(ChangingAnimator.LEFT_PROPERTY, left2).add(ChangingAnimator.TOP_PROPERTY, top2).add(ChangingAnimator.RIGHT_PROPERTY, right2).add(ChangingAnimator.BOTTOM_PROPERTY, bottom2).add(ChangingAnimator.SCROLLX_PROPERTY, scrollX2).add(ChangingAnimator.SCROLLY_PROPERTY, scrollY2));
                changingAnimator.mFolmeImpl = iFolmeUse;
                LayoutTransition.this.currentChangingAnimations.put(view, changingAnimator);
                LayoutTransition.this.startChangingAnimations();
                view.removeOnLayoutChangeListener(this);
                LayoutTransition.this.layoutChangeListenerMap.remove(view);
            }
        };
        view.addOnLayoutChangeListener(onLayoutChangeListener);
        this.layoutChangeListenerMap.put(view, onLayoutChangeListener);
    }

    public void startChangingAnimations() {
        Iterator it = ((LinkedHashMap) this.currentChangingAnimations.clone()).values().iterator();
        while (it.hasNext()) {
            ((ChangingAnimator) it.next()).start();
        }
    }

    @Override // android.animation.LayoutTransition
    public boolean isChangingLayout() {
        return this.currentChangingAnimations.size() > 0;
    }

    @Override // android.animation.LayoutTransition
    public boolean isRunning() {
        return this.currentChangingAnimations.size() > 0 || this.currentAppearingViews.size() > 0 || this.currentDisappearingViews.size() > 0;
    }

    public void cancel() {
        if (this.currentChangingAnimations.size() > 0) {
            Iterator it = ((LinkedHashMap) this.currentChangingAnimations.clone()).values().iterator();
            while (it.hasNext()) {
                ((ChangingAnimator) it.next()).cancel();
            }
            this.currentChangingAnimations.clear();
        }
    }

    public void cancel(int i) {
        if ((i == 0 || i == 1 || i == 4) && this.currentChangingAnimations.size() > 0) {
            Iterator it = ((LinkedHashMap) this.currentChangingAnimations.clone()).values().iterator();
            while (it.hasNext()) {
                ((ChangingAnimator) it.next()).cancel();
            }
            this.currentChangingAnimations.clear();
        }
    }

    private void runAppearingTransition(final ViewGroup viewGroup, final View view) {
        if (!this.currentDisappearingViews.contains(view)) {
            Folme.use(view).setTo(ViewProperty.TRANSITION_ALPHA, Float.valueOf(0.0f));
        }
        Folme.use(view).to(ViewProperty.TRANSITION_ALPHA, Float.valueOf(1.0f), new AnimConfig().setEase(FolmeEase.linear(300L)).addListeners(new TransitionListener() { // from class: miuix.transition.LayoutTransition.2
            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                LayoutTransition.this.currentAppearingViews.remove(view);
                if (LayoutTransition.this.hasListeners()) {
                    Iterator it = ((ArrayList) LayoutTransition.this.mListeners.clone()).iterator();
                    while (it.hasNext()) {
                        ((android.animation.LayoutTransition.TransitionListener) it.next()).endTransition(LayoutTransition.this, viewGroup, view, 2);
                    }
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                LayoutTransition.this.currentAppearingViews.remove(view);
                if (LayoutTransition.this.hasListeners()) {
                    Iterator it = ((ArrayList) LayoutTransition.this.mListeners.clone()).iterator();
                    while (it.hasNext()) {
                        ((android.animation.LayoutTransition.TransitionListener) it.next()).endTransition(LayoutTransition.this, viewGroup, view, 2);
                    }
                }
            }
        }));
        this.currentAppearingViews.add(view);
    }

    private void runDisappearingTransition(final ViewGroup viewGroup, final View view) {
        Folme.use(view).to(ViewProperty.TRANSITION_ALPHA, Float.valueOf(0.0f), new AnimConfig().setEase(FolmeEase.linear(300L)).addListeners(new TransitionListener() { // from class: miuix.transition.LayoutTransition.3
            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                LayoutTransition.this.currentDisappearingViews.remove(view);
                if (LayoutTransition.this.hasListeners()) {
                    Iterator it = ((ArrayList) LayoutTransition.this.mListeners.clone()).iterator();
                    while (it.hasNext()) {
                        ((android.animation.LayoutTransition.TransitionListener) it.next()).endTransition(LayoutTransition.this, viewGroup, view, 3);
                    }
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                LayoutTransition.this.currentDisappearingViews.remove(view);
                if (LayoutTransition.this.hasListeners()) {
                    Iterator it = ((ArrayList) LayoutTransition.this.mListeners.clone()).iterator();
                    while (it.hasNext()) {
                        ((android.animation.LayoutTransition.TransitionListener) it.next()).endTransition(LayoutTransition.this, viewGroup, view, 3);
                    }
                }
            }
        }));
        this.currentDisappearingViews.add(view);
    }

    private void addChild(ViewGroup viewGroup, View view, boolean z) {
        if (viewGroup.getWindowVisibility() != 0) {
            return;
        }
        if ((this.mTransitionTypes & 1) == 1) {
            cancel(3);
        }
        if (z && (this.mTransitionTypes & 4) == 4) {
            cancel(0);
            cancel(4);
        }
        if (hasListeners() && (this.mTransitionTypes & 1) == 1) {
            Iterator it = ((ArrayList) this.mListeners.clone()).iterator();
            while (it.hasNext()) {
                ((android.animation.LayoutTransition.TransitionListener) it.next()).startTransition(this, viewGroup, view, 2);
            }
        }
        if (z && (this.mTransitionTypes & 4) == 4) {
            runChangeTransition(viewGroup, view, 2);
        }
        if ((this.mTransitionTypes & 1) == 1) {
            runAppearingTransition(viewGroup, view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasListeners() {
        ArrayList<android.animation.LayoutTransition.TransitionListener> arrayList = this.mListeners;
        return arrayList != null && arrayList.size() > 0;
    }

    public void layoutChange(ViewGroup viewGroup) {
        if (viewGroup.getWindowVisibility() == 0 && (this.mTransitionTypes & 16) == 16 && !isRunning()) {
            runChangeTransition(viewGroup, null, 4);
        }
    }

    @Override // android.animation.LayoutTransition
    public void addChild(ViewGroup viewGroup, View view) {
        addChild(viewGroup, view, true);
    }

    @Override // android.animation.LayoutTransition
    @Deprecated
    public void showChild(ViewGroup viewGroup, View view) {
        addChild(viewGroup, view, true);
    }

    @Override // android.animation.LayoutTransition
    public void showChild(ViewGroup viewGroup, View view, int i) {
        addChild(viewGroup, view, i == 8);
    }

    private void removeChild(ViewGroup viewGroup, View view, boolean z) {
        if (viewGroup.getWindowVisibility() != 0) {
            return;
        }
        if ((this.mTransitionTypes & 2) == 2) {
            cancel(2);
        }
        if (z && (this.mTransitionTypes & 8) == 8) {
            cancel(1);
            cancel(4);
        }
        if (hasListeners() && (this.mTransitionTypes & 2) == 2) {
            Iterator it = ((ArrayList) this.mListeners.clone()).iterator();
            while (it.hasNext()) {
                ((android.animation.LayoutTransition.TransitionListener) it.next()).startTransition(this, viewGroup, view, 3);
            }
        }
        if (z && (this.mTransitionTypes & 8) == 8) {
            runChangeTransition(viewGroup, view, 3);
        }
        if ((this.mTransitionTypes & 2) == 2) {
            runDisappearingTransition(viewGroup, view);
        }
    }

    @Override // android.animation.LayoutTransition
    public void removeChild(ViewGroup viewGroup, View view) {
        removeChild(viewGroup, view, true);
    }

    @Override // android.animation.LayoutTransition
    public void hideChild(ViewGroup viewGroup, View view) {
        removeChild(viewGroup, view, true);
    }

    @Override // android.animation.LayoutTransition
    public void hideChild(ViewGroup viewGroup, View view, int i) {
        removeChild(viewGroup, view, i == 8);
    }

    @Override // android.animation.LayoutTransition
    public void addTransitionListener(android.animation.LayoutTransition.TransitionListener transitionListener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(transitionListener);
    }

    @Override // android.animation.LayoutTransition
    public void removeTransitionListener(android.animation.LayoutTransition.TransitionListener transitionListener) {
        ArrayList<android.animation.LayoutTransition.TransitionListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(transitionListener);
    }

    @Override // android.animation.LayoutTransition
    public List<android.animation.LayoutTransition.TransitionListener> getTransitionListeners() {
        return this.mListeners;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setLeftTopRightBottom(View view, int i, int i2, int i3, int i4) {
        if (Build.VERSION.SDK_INT >= 29) {
            view.setLeftTopRightBottom(i, i2, i3, i4);
            return;
        }
        view.setLeft(i);
        view.setTop(i2);
        view.setRight(i3);
        view.setBottom(i4);
    }

    private static class ChangingAnimator {
        AnimConfig mAnimConfig;
        private float mBottom;
        IFolme mFolmeImpl;
        private float mLeft;
        private float mRight;
        private float mScrollX;
        private float mScrollY;
        View mTarget;
        private float mTop;
        static final FloatProperty<ChangingAnimator> LEFT_PROPERTY = new FloatProperty<ChangingAnimator>("left") { // from class: miuix.transition.LayoutTransition.ChangingAnimator.1
            @Override // miuix.animation.property.FloatProperty
            public float getValue(ChangingAnimator changingAnimator) {
                return changingAnimator.mLeft;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(ChangingAnimator changingAnimator, float f) {
                changingAnimator.mLeft = f;
                if (changingAnimator.mTarget != null) {
                    changingAnimator.mTarget.setLeft((int) f);
                }
            }
        };
        static final FloatProperty<ChangingAnimator> TOP_PROPERTY = new FloatProperty<ChangingAnimator>("top") { // from class: miuix.transition.LayoutTransition.ChangingAnimator.2
            @Override // miuix.animation.property.FloatProperty
            public float getValue(ChangingAnimator changingAnimator) {
                return changingAnimator.mTop;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(ChangingAnimator changingAnimator, float f) {
                changingAnimator.mTop = f;
                if (changingAnimator.mTarget != null) {
                    changingAnimator.mTarget.setTop((int) f);
                }
            }
        };
        static final FloatProperty<ChangingAnimator> RIGHT_PROPERTY = new FloatProperty<ChangingAnimator>("right") { // from class: miuix.transition.LayoutTransition.ChangingAnimator.3
            @Override // miuix.animation.property.FloatProperty
            public float getValue(ChangingAnimator changingAnimator) {
                return changingAnimator.mRight;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(ChangingAnimator changingAnimator, float f) {
                changingAnimator.mRight = f;
                if (changingAnimator.mTarget != null) {
                    changingAnimator.mTarget.setRight((int) f);
                }
            }
        };
        static final FloatProperty<ChangingAnimator> BOTTOM_PROPERTY = new FloatProperty<ChangingAnimator>("bottom") { // from class: miuix.transition.LayoutTransition.ChangingAnimator.4
            @Override // miuix.animation.property.FloatProperty
            public float getValue(ChangingAnimator changingAnimator) {
                return changingAnimator.mBottom;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(ChangingAnimator changingAnimator, float f) {
                changingAnimator.mBottom = f;
                if (changingAnimator.mTarget != null) {
                    changingAnimator.mTarget.setBottom((int) f);
                }
            }
        };
        static final FloatProperty<ChangingAnimator> SCROLLX_PROPERTY = new FloatProperty<ChangingAnimator>("scrollX") { // from class: miuix.transition.LayoutTransition.ChangingAnimator.5
            @Override // miuix.animation.property.FloatProperty
            public float getValue(ChangingAnimator changingAnimator) {
                return changingAnimator.mScrollX;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(ChangingAnimator changingAnimator, float f) {
                changingAnimator.mScrollX = f;
                if (changingAnimator.mTarget != null) {
                    changingAnimator.mTarget.setScrollX((int) f);
                }
            }
        };
        static final FloatProperty<ChangingAnimator> SCROLLY_PROPERTY = new FloatProperty<ChangingAnimator>("scrollY") { // from class: miuix.transition.LayoutTransition.ChangingAnimator.6
            @Override // miuix.animation.property.FloatProperty
            public float getValue(ChangingAnimator changingAnimator) {
                return changingAnimator.mScrollY;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(ChangingAnimator changingAnimator, float f) {
                changingAnimator.mScrollY = f;
                if (changingAnimator.mTarget != null) {
                    changingAnimator.mTarget.setScrollY((int) f);
                }
            }
        };

        private ChangingAnimator() {
        }

        public void start() {
            IFolme iFolme = this.mFolmeImpl;
            if (iFolme != null) {
                iFolme.to(this.mAnimConfig);
            }
        }

        public void cancel() {
            IFolme iFolme = this.mFolmeImpl;
            if (iFolme != null) {
                iFolme.cancel();
            }
        }

        public void initFromView(View view) {
            this.mLeft = view.getLeft();
            this.mTop = view.getTop();
            this.mRight = view.getRight();
            this.mBottom = view.getBottom();
            this.mScrollX = view.getScrollX();
            this.mScrollY = view.getScrollY();
        }
    }

    private static final class CleanupCallback implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {
        final Map<View, View.OnLayoutChangeListener> layoutChangeListenerMap;
        final ViewGroup parent;

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
        }

        CleanupCallback(Map<View, View.OnLayoutChangeListener> map, ViewGroup viewGroup) {
            this.layoutChangeListenerMap = map;
            this.parent = viewGroup;
        }

        private void cleanup() {
            this.parent.getViewTreeObserver().removeOnPreDrawListener(this);
            this.parent.removeOnAttachStateChangeListener(this);
            if (this.layoutChangeListenerMap.size() > 0) {
                for (View view : this.layoutChangeListenerMap.keySet()) {
                    view.removeOnLayoutChangeListener(this.layoutChangeListenerMap.get(view));
                }
                this.layoutChangeListenerMap.clear();
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            cleanup();
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            cleanup();
            return true;
        }
    }
}
