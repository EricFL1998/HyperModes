package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import androidx.collection.ArrayMap;
import androidx.core.app.SharedElementCallback;
import androidx.core.os.CancellationSignal;
import androidx.core.util.Preconditions;
import androidx.core.view.OneShotPreDrawListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewGroupCompat;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miuix.reflect.Reflects;

/* JADX INFO: loaded from: classes.dex */
public class MiuixSpecialEffectsController extends SpecialEffectsController {
    @Override // androidx.fragment.app.SpecialEffectsController
    public /* bridge */ /* synthetic */ ViewGroup getContainer() {
        return super.getContainer();
    }

    MiuixSpecialEffectsController(ViewGroup viewGroup) {
        super(viewGroup);
    }

    public static SpecialEffectsControllerFactory createMiuixSpecialEffectsController() {
        return new SpecialEffectsControllerFactory() { // from class: androidx.fragment.app.MiuixSpecialEffectsController$$ExternalSyntheticLambda0
            @Override // androidx.fragment.app.SpecialEffectsControllerFactory
            public final SpecialEffectsController createController(ViewGroup viewGroup) {
                return MiuixSpecialEffectsController.lambda$createMiuixSpecialEffectsController$0(viewGroup);
            }
        };
    }

    static /* synthetic */ SpecialEffectsController lambda$createMiuixSpecialEffectsController$0(ViewGroup viewGroup) {
        return new MiuixSpecialEffectsController(viewGroup);
    }

    public static void setEnabled(FragmentManager fragmentManager, boolean z) {
        Field declaredField = Reflects.getDeclaredField((Class<?>) FragmentManager.class, "mSpecialEffectsControllerFactory");
        declaredField.setAccessible(true);
        if (z) {
            if (Reflects.get(fragmentManager, declaredField) instanceof MiuixSpecialEffectsController) {
                return;
            }
            Reflects.set(fragmentManager, declaredField, createMiuixSpecialEffectsController());
            return;
        }
        Reflects.set(fragmentManager, declaredField, null);
    }

    /* JADX WARN: Code duplicated, block: B:31:0x00af  */
    @Override // androidx.fragment.app.SpecialEffectsController
    void executeOperations(List<SpecialEffectsController.Operation> list, boolean z) {
        SpecialEffectsController.Operation operation = null;
        SpecialEffectsController.Operation operation2 = null;
        for (SpecialEffectsController.Operation operation3 : list) {
            SpecialEffectsController.Operation.State stateFrom = SpecialEffectsController.Operation.State.from(operation3.getFragment().mView);
            int i = AnonymousClass9.$SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State[operation3.getFinalState().ordinal()];
            if (i == 1 || i == 2 || i == 3) {
                if (stateFrom == SpecialEffectsController.Operation.State.VISIBLE && operation == null) {
                    operation = operation3;
                }
            } else if (i == 4 && stateFrom != SpecialEffectsController.Operation.State.VISIBLE) {
                operation2 = operation3;
            }
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            Log.v(FragmentManager.TAG, "Executing operations from " + operation + " to " + operation2);
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList(list);
        for (final SpecialEffectsController.Operation operation4 : list) {
            CancellationSignal cancellationSignal = new CancellationSignal();
            operation4.markStartedSpecialEffect(cancellationSignal);
            arrayList.add(new AnimationInfo(operation4, cancellationSignal, z));
            CancellationSignal cancellationSignal2 = new CancellationSignal();
            operation4.markStartedSpecialEffect(cancellationSignal2);
            boolean z2 = false;
            if (z) {
                if (operation4 == operation) {
                    z2 = true;
                }
            } else if (operation4 == operation2) {
                z2 = true;
            }
            arrayList2.add(new TransitionInfo(operation4, cancellationSignal2, z, z2));
            operation4.addCompletionListener(new Runnable() { // from class: androidx.fragment.app.MiuixSpecialEffectsController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m54xb31e7d55(arrayList3, operation4);
                }
            });
        }
        Map<SpecialEffectsController.Operation, Boolean> mapStartTransitions = startTransitions(arrayList2, arrayList3, z, operation, operation2);
        startAnimations(arrayList, arrayList3, mapStartTransitions.containsValue(true), mapStartTransitions);
        Iterator<SpecialEffectsController.Operation> it = arrayList3.iterator();
        while (it.hasNext()) {
            applyContainerChanges(it.next());
        }
        arrayList3.clear();
        if (FragmentManager.isLoggingEnabled(2)) {
            Log.v(FragmentManager.TAG, "Completed executing operations from " + operation + " to " + operation2);
        }
    }

    /* JADX INFO: renamed from: androidx.fragment.app.MiuixSpecialEffectsController$9, reason: invalid class name */
    static /* synthetic */ class AnonymousClass9 {
        static final /* synthetic */ int[] $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State;

        static {
            int[] iArr = new int[SpecialEffectsController.Operation.State.values().length];
            $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State = iArr;
            try {
                iArr[SpecialEffectsController.Operation.State.GONE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State[SpecialEffectsController.Operation.State.INVISIBLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State[SpecialEffectsController.Operation.State.REMOVED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$androidx$fragment$app$SpecialEffectsController$Operation$State[SpecialEffectsController.Operation.State.VISIBLE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* JADX INFO: renamed from: lambda$executeOperations$1$androidx-fragment-app-MiuixSpecialEffectsController, reason: not valid java name */
    /* synthetic */ void m54xb31e7d55(List list, SpecialEffectsController.Operation operation) {
        if (list.contains(operation)) {
            list.remove(operation);
            applyContainerChanges(operation);
        }
    }

    private void startAnimations(List<AnimationInfo> list, List<SpecialEffectsController.Operation> list2, boolean z, Map<SpecialEffectsController.Operation, Boolean> map) {
        int i;
        boolean z2;
        Context context;
        View view;
        int i2;
        final SpecialEffectsController.Operation operation;
        final ViewGroup container = getContainer();
        Context context2 = container.getContext();
        ArrayList<AnimationInfo> arrayList = new ArrayList();
        Iterator<AnimationInfo> it = list.iterator();
        boolean z3 = false;
        while (true) {
            i = 2;
            if (!it.hasNext()) {
                break;
            }
            final AnimationInfo next = it.next();
            if (next.isVisibilityUnchanged()) {
                next.completeSpecialEffect();
            } else {
                FragmentAnim.AnimationOrAnimator animation = next.getAnimation(context2);
                if (animation == null) {
                    next.completeSpecialEffect();
                } else {
                    final Animator animator = animation.animator;
                    if (animator == null) {
                        arrayList.add(next);
                    } else {
                        final SpecialEffectsController.Operation operation2 = next.getOperation();
                        Fragment fragment = operation2.getFragment();
                        if (Boolean.TRUE.equals(map.get(operation2))) {
                            if (FragmentManager.isLoggingEnabled(2)) {
                                Log.v(FragmentManager.TAG, "Ignoring Animator set on " + fragment + " as this Fragment was involved in a Transition.");
                            }
                            next.completeSpecialEffect();
                        } else {
                            boolean z4 = operation2.getFinalState() == SpecialEffectsController.Operation.State.GONE;
                            if (z4) {
                                list2.remove(operation2);
                            }
                            final View view2 = fragment.mView;
                            container.startViewTransition(view2);
                            final boolean z5 = z4;
                            animator.addListener(new AnimatorListenerAdapter() { // from class: androidx.fragment.app.MiuixSpecialEffectsController.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animator2) {
                                    container.endViewTransition(view2);
                                    if (z5) {
                                        operation2.getFinalState().applyState(view2);
                                    }
                                    next.completeSpecialEffect();
                                    if (FragmentManager.isLoggingEnabled(2)) {
                                        Log.v(FragmentManager.TAG, "Animator from operation " + operation2 + " has ended.");
                                    }
                                }
                            });
                            animator.setTarget(view2);
                            animator.start();
                            if (FragmentManager.isLoggingEnabled(2)) {
                                operation = operation2;
                                Log.v(FragmentManager.TAG, "Animator from operation " + operation + " has started.");
                            } else {
                                operation = operation2;
                            }
                            next.getSignal().setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: androidx.fragment.app.MiuixSpecialEffectsController.2
                                @Override // androidx.core.os.CancellationSignal.OnCancelListener
                                public void onCancel() {
                                    animator.end();
                                    if (FragmentManager.isLoggingEnabled(2)) {
                                        Log.v(FragmentManager.TAG, "Animator from operation " + operation + " has been canceled.");
                                    }
                                }
                            });
                            z3 = true;
                        }
                    }
                }
            }
        }
        for (final AnimationInfo animationInfo : arrayList) {
            final SpecialEffectsController.Operation operation3 = animationInfo.getOperation();
            Fragment fragment2 = operation3.getFragment();
            if (z) {
                if (FragmentManager.isLoggingEnabled(i)) {
                    Log.v(FragmentManager.TAG, "Ignoring Animation set on " + fragment2 + " as Animations cannot run alongside Transitions.");
                }
                animationInfo.completeSpecialEffect();
            } else if (z3) {
                if (FragmentManager.isLoggingEnabled(i)) {
                    Log.v(FragmentManager.TAG, "Ignoring Animation set on " + fragment2 + " as Animations cannot run alongside Animators.");
                }
                animationInfo.completeSpecialEffect();
            } else {
                final View view3 = fragment2.mView;
                Animation animation2 = (Animation) Preconditions.checkNotNull(((FragmentAnim.AnimationOrAnimator) Preconditions.checkNotNull(animationInfo.getAnimation(context2))).animation);
                if (operation3.getFinalState() != SpecialEffectsController.Operation.State.REMOVED) {
                    view3.startAnimation(animation2);
                    animationInfo.completeSpecialEffect();
                    z2 = z3;
                    context = context2;
                    i2 = i;
                    view = view3;
                } else {
                    container.startViewTransition(view3);
                    FragmentAnim.EndViewTransitionAnimation endViewTransitionAnimation = new FragmentAnim.EndViewTransitionAnimation(animation2, container, view3);
                    z2 = z3;
                    context = context2;
                    view = view3;
                    endViewTransitionAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: androidx.fragment.app.MiuixSpecialEffectsController.3
                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationRepeat(Animation animation3) {
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationStart(Animation animation3) {
                            if (FragmentManager.isLoggingEnabled(2)) {
                                Log.v(FragmentManager.TAG, "Animation from operation " + operation3 + " has reached onAnimationStart.");
                            }
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationEnd(Animation animation3) {
                            container.post(new Runnable() { // from class: androidx.fragment.app.MiuixSpecialEffectsController.3.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    container.endViewTransition(view3);
                                    animationInfo.completeSpecialEffect();
                                }
                            });
                            if (FragmentManager.isLoggingEnabled(2)) {
                                Log.v(FragmentManager.TAG, "Animation from operation " + operation3 + " has ended.");
                            }
                        }
                    });
                    view.startAnimation(endViewTransitionAnimation);
                    i2 = 2;
                    if (FragmentManager.isLoggingEnabled(2)) {
                        Log.v(FragmentManager.TAG, "Animation from operation " + operation3 + " has started.");
                    }
                }
                final View view4 = view;
                animationInfo.getSignal().setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: androidx.fragment.app.MiuixSpecialEffectsController.4
                    @Override // androidx.core.os.CancellationSignal.OnCancelListener
                    public void onCancel() {
                        view4.clearAnimation();
                        container.endViewTransition(view4);
                        animationInfo.completeSpecialEffect();
                        if (FragmentManager.isLoggingEnabled(2)) {
                            Log.v(FragmentManager.TAG, "Animation from operation " + operation3 + " has been cancelled.");
                        }
                    }
                });
                i = i2;
                z3 = z2;
                context2 = context;
            }
        }
    }

    private Map<SpecialEffectsController.Operation, Boolean> startTransitions(List<TransitionInfo> list, List<SpecialEffectsController.Operation> list2, final boolean z, final SpecialEffectsController.Operation operation, final SpecialEffectsController.Operation operation2) {
        String str;
        String str2;
        View view;
        ArrayList<View> arrayList;
        Object obj;
        Object objMergeTransitionsTogether;
        String str3;
        ArrayList<View> arrayList2;
        SpecialEffectsController.Operation operation3;
        View view2;
        SpecialEffectsController.Operation operation4;
        int i;
        ArrayList<View> arrayList3;
        ArrayList<View> arrayList4;
        Rect rect;
        View view3;
        SharedElementCallback enterTransitionCallback;
        SharedElementCallback exitTransitionCallback;
        ArrayList<String> arrayList5;
        final Rect rect2;
        final View view4;
        String strFindKeyForValue;
        ArrayList<String> arrayList6;
        SpecialEffectsController.Operation operation5 = operation;
        SpecialEffectsController.Operation operation6 = operation2;
        HashMap map = new HashMap();
        final FragmentTransitionImpl fragmentTransitionImpl = null;
        for (TransitionInfo transitionInfo : list) {
            if (!transitionInfo.isVisibilityUnchanged()) {
                FragmentTransitionImpl handlingImpl = transitionInfo.getHandlingImpl();
                if (fragmentTransitionImpl == null) {
                    fragmentTransitionImpl = handlingImpl;
                } else if (handlingImpl != null && fragmentTransitionImpl != handlingImpl) {
                    throw new IllegalArgumentException("Mixing framework transitions and AndroidX transitions is not allowed. Fragment " + transitionInfo.getOperation().getFragment() + " returned Transition " + transitionInfo.getTransition() + " which uses a different Transition  type than other Fragments.");
                }
            }
        }
        if (fragmentTransitionImpl == null) {
            for (TransitionInfo transitionInfo2 : list) {
                map.put(transitionInfo2.getOperation(), false);
                transitionInfo2.completeSpecialEffect();
            }
            return map;
        }
        View view5 = new View(getContainer().getContext());
        Rect rect3 = new Rect();
        ArrayList<View> arrayList7 = new ArrayList<>();
        ArrayList<View> arrayList8 = new ArrayList<>();
        ArrayMap arrayMap = new ArrayMap();
        Iterator<TransitionInfo> it = list.iterator();
        boolean z2 = false;
        Object obj2 = null;
        View view6 = null;
        while (true) {
            boolean zHasNext = it.hasNext();
            str = FragmentManager.TAG;
            if (!zHasNext) {
                break;
            }
            TransitionInfo next = it.next();
            if (!next.hasSharedElementTransition() || operation5 == null || operation6 == null) {
                operation4 = operation6;
                i = 0;
                arrayList3 = arrayList8;
                arrayList4 = arrayList7;
                rect = rect3;
                view3 = view5;
                view6 = view6;
            } else {
                Object objWrapTransitionInSet = fragmentTransitionImpl.wrapTransitionInSet(fragmentTransitionImpl.cloneTransition(next.getSharedElementTransition()));
                ArrayList<String> sharedElementSourceNames = operation2.getFragment().getSharedElementSourceNames();
                ArrayList<String> sharedElementSourceNames2 = operation.getFragment().getSharedElementSourceNames();
                ArrayList<String> sharedElementTargetNames = operation.getFragment().getSharedElementTargetNames();
                View view7 = view6;
                HashMap map2 = map;
                int i2 = 0;
                while (i2 < sharedElementTargetNames.size()) {
                    int iIndexOf = sharedElementSourceNames.indexOf(sharedElementTargetNames.get(i2));
                    ArrayList<String> arrayList9 = sharedElementTargetNames;
                    if (iIndexOf != -1) {
                        sharedElementSourceNames.set(iIndexOf, sharedElementSourceNames2.get(i2));
                    }
                    i2++;
                    sharedElementTargetNames = arrayList9;
                }
                ArrayList<String> sharedElementTargetNames2 = operation2.getFragment().getSharedElementTargetNames();
                if (z == 0) {
                    enterTransitionCallback = operation.getFragment().getExitTransitionCallback();
                    exitTransitionCallback = operation2.getFragment().getEnterTransitionCallback();
                } else {
                    enterTransitionCallback = operation.getFragment().getEnterTransitionCallback();
                    exitTransitionCallback = operation2.getFragment().getExitTransitionCallback();
                }
                int size = sharedElementSourceNames.size();
                View view8 = view5;
                int i3 = 0;
                while (i3 < size) {
                    arrayMap.put(sharedElementSourceNames.get(i3), sharedElementTargetNames2.get(i3));
                    i3++;
                    size = size;
                    rect3 = rect3;
                }
                Rect rect4 = rect3;
                if (FragmentManager.isLoggingEnabled(2)) {
                    Log.v(FragmentManager.TAG, ">>> entering view names <<<");
                    for (Iterator<String> it2 = sharedElementTargetNames2.iterator(); it2.hasNext(); it2 = it2) {
                        Log.v(FragmentManager.TAG, "Name: " + it2.next());
                    }
                    Log.v(FragmentManager.TAG, ">>> exiting view names <<<");
                    for (Iterator<String> it3 = sharedElementSourceNames.iterator(); it3.hasNext(); it3 = it3) {
                        Log.v(FragmentManager.TAG, "Name: " + it3.next());
                    }
                }
                ArrayMap<String, View> arrayMap2 = new ArrayMap<>();
                findNamedViews(arrayMap2, operation.getFragment().mView);
                arrayMap2.retainAll(sharedElementSourceNames);
                if (enterTransitionCallback != null) {
                    if (FragmentManager.isLoggingEnabled(2)) {
                        Log.v(FragmentManager.TAG, "Executing exit callback for operation " + operation5);
                    }
                    enterTransitionCallback.onMapSharedElements(sharedElementSourceNames, arrayMap2);
                    int size2 = sharedElementSourceNames.size() - 1;
                    while (size2 >= 0) {
                        String str4 = sharedElementSourceNames.get(size2);
                        View view9 = arrayMap2.get(str4);
                        if (view9 == null) {
                            arrayMap.remove(str4);
                            arrayList6 = sharedElementSourceNames;
                        } else {
                            arrayList6 = sharedElementSourceNames;
                            if (!str4.equals(ViewCompat.getTransitionName(view9))) {
                                arrayMap.put(ViewCompat.getTransitionName(view9), (String) arrayMap.remove(str4));
                            }
                        }
                        size2--;
                        sharedElementSourceNames = arrayList6;
                    }
                    arrayList5 = sharedElementSourceNames;
                } else {
                    arrayList5 = sharedElementSourceNames;
                    arrayMap.retainAll(arrayMap2.keySet());
                }
                final ArrayMap<String, View> arrayMap3 = new ArrayMap<>();
                findNamedViews(arrayMap3, operation2.getFragment().mView);
                arrayMap3.retainAll(sharedElementTargetNames2);
                arrayMap3.retainAll(arrayMap.values());
                if (exitTransitionCallback != null) {
                    if (FragmentManager.isLoggingEnabled(2)) {
                        Log.v(FragmentManager.TAG, "Executing enter callback for operation " + operation6);
                    }
                    exitTransitionCallback.onMapSharedElements(sharedElementTargetNames2, arrayMap3);
                    for (int size3 = sharedElementTargetNames2.size() - 1; size3 >= 0; size3--) {
                        String str5 = sharedElementTargetNames2.get(size3);
                        View view10 = arrayMap3.get(str5);
                        if (view10 == null) {
                            String strFindKeyForValue2 = MiuixFragmentTransition.findKeyForValue(arrayMap, str5);
                            if (strFindKeyForValue2 != null) {
                                arrayMap.remove(strFindKeyForValue2);
                            }
                        } else if (!str5.equals(ViewCompat.getTransitionName(view10)) && (strFindKeyForValue = MiuixFragmentTransition.findKeyForValue(arrayMap, str5)) != null) {
                            arrayMap.put(strFindKeyForValue, ViewCompat.getTransitionName(view10));
                        }
                    }
                } else {
                    MiuixFragmentTransition.retainValues(arrayMap, arrayMap3);
                }
                retainMatchingViews(arrayMap2, arrayMap.keySet());
                retainMatchingViews(arrayMap3, arrayMap.values());
                if (arrayMap.isEmpty()) {
                    arrayList7.clear();
                    arrayList8.clear();
                    operation4 = operation6;
                    view6 = view7;
                    map = map2;
                    obj2 = null;
                    i = 0;
                    arrayList3 = arrayList8;
                    arrayList4 = arrayList7;
                    view3 = view8;
                    rect = rect4;
                } else {
                    MiuixFragmentTransition.callSharedElementStartEnd(operation2.getFragment(), operation.getFragment(), z, arrayMap2, true);
                    arrayList3 = arrayList8;
                    ArrayList<View> arrayList10 = arrayList7;
                    OneShotPreDrawListener.add(getContainer(), new Runnable() { // from class: androidx.fragment.app.MiuixSpecialEffectsController.5
                        @Override // java.lang.Runnable
                        public void run() {
                            MiuixFragmentTransition.callSharedElementStartEnd(operation2.getFragment(), operation.getFragment(), z, arrayMap3, false);
                        }
                    });
                    arrayList10.addAll(arrayMap2.values());
                    if (arrayList5.isEmpty()) {
                        i = 0;
                        view6 = view7;
                    } else {
                        i = 0;
                        View view11 = arrayMap2.get(arrayList5.get(0));
                        fragmentTransitionImpl.setEpicenter(objWrapTransitionInSet, view11);
                        view6 = view11;
                    }
                    arrayList3.addAll(arrayMap3.values());
                    if (sharedElementTargetNames2.isEmpty() || (view4 = arrayMap3.get(sharedElementTargetNames2.get(i))) == null) {
                        rect2 = rect4;
                    } else {
                        rect2 = rect4;
                        OneShotPreDrawListener.add(getContainer(), new Runnable() { // from class: androidx.fragment.app.MiuixSpecialEffectsController.6
                            @Override // java.lang.Runnable
                            public void run() {
                                fragmentTransitionImpl.getBoundsOnScreen(view4, rect2);
                            }
                        });
                        z2 = true;
                    }
                    fragmentTransitionImpl.setSharedElementTargets(objWrapTransitionInSet, view8, arrayList10);
                    rect = rect2;
                    view3 = view8;
                    fragmentTransitionImpl.scheduleRemoveTargets(objWrapTransitionInSet, null, null, null, null, objWrapTransitionInSet, arrayList3);
                    arrayList4 = arrayList10;
                    map = map2;
                    operation5 = operation;
                    map.put(operation5, true);
                    operation4 = operation2;
                    map.put(operation4, true);
                    obj2 = objWrapTransitionInSet;
                }
            }
            rect3 = rect;
            view5 = view3;
            arrayList8 = arrayList3;
            arrayList7 = arrayList4;
            operation6 = operation4;
            fragmentTransitionImpl = fragmentTransitionImpl;
            arrayMap = arrayMap;
        }
        View view12 = view6;
        ArrayMap arrayMap4 = arrayMap;
        SpecialEffectsController.Operation operation7 = operation6;
        FragmentTransitionImpl fragmentTransitionImpl2 = fragmentTransitionImpl;
        boolean z3 = false;
        ArrayList<View> arrayList11 = arrayList8;
        ArrayList<View> arrayList12 = arrayList7;
        Rect rect5 = rect3;
        View view13 = view5;
        ArrayList arrayList13 = new ArrayList();
        Iterator<TransitionInfo> it4 = list.iterator();
        Object obj3 = null;
        Object obj4 = null;
        while (it4.hasNext()) {
            TransitionInfo next2 = it4.next();
            if (next2.isVisibilityUnchanged()) {
                map.put(next2.getOperation(), Boolean.valueOf(z3));
                next2.completeSpecialEffect();
                it4 = it4;
                obj3 = obj3;
            } else {
                Iterator<TransitionInfo> it5 = it4;
                Object objMergeTransitionsTogether2 = obj3;
                Object objCloneTransition = fragmentTransitionImpl2.cloneTransition(next2.getTransition());
                SpecialEffectsController.Operation operation8 = next2.getOperation();
                boolean z4 = (obj2 == null || !(operation8 == operation5 || operation8 == operation7)) ? z3 : true;
                if (objCloneTransition == null) {
                    if (!z4) {
                        map.put(operation8, Boolean.valueOf(z3));
                        next2.completeSpecialEffect();
                    }
                    view = view13;
                    arrayList = arrayList11;
                    objMergeTransitionsTogether = obj4;
                    str3 = str;
                    arrayList2 = arrayList12;
                    view2 = view12;
                } else {
                    final ArrayList<View> arrayList14 = new ArrayList<>();
                    Object obj5 = obj4;
                    captureTransitioningViews(arrayList14, operation8.getFragment().mView);
                    if (z4) {
                        if (operation8 == operation5) {
                            arrayList14.removeAll(arrayList12);
                        } else {
                            arrayList14.removeAll(arrayList11);
                        }
                    }
                    if (arrayList14.isEmpty()) {
                        fragmentTransitionImpl2.addTarget(objCloneTransition, view13);
                        view = view13;
                        arrayList = arrayList11;
                        operation3 = operation8;
                        str3 = str;
                        arrayList2 = arrayList12;
                        obj = objMergeTransitionsTogether2;
                        objMergeTransitionsTogether = obj5;
                    } else {
                        fragmentTransitionImpl2.addTargets(objCloneTransition, arrayList14);
                        view = view13;
                        arrayList = arrayList11;
                        obj = objMergeTransitionsTogether2;
                        objMergeTransitionsTogether = obj5;
                        str3 = str;
                        arrayList2 = arrayList12;
                        fragmentTransitionImpl2.scheduleRemoveTargets(objCloneTransition, objCloneTransition, arrayList14, null, null, null, null);
                        if (operation8.getFinalState() == SpecialEffectsController.Operation.State.GONE) {
                            operation3 = operation8;
                            list2.remove(operation3);
                            ArrayList<View> arrayList15 = new ArrayList<>(arrayList14);
                            arrayList15.remove(operation3.getFragment().mView);
                            fragmentTransitionImpl2.scheduleHideFragmentView(objCloneTransition, operation3.getFragment().mView, arrayList15);
                            OneShotPreDrawListener.add(getContainer(), new Runnable() { // from class: androidx.fragment.app.MiuixSpecialEffectsController.7
                                @Override // java.lang.Runnable
                                public void run() {
                                    MiuixFragmentTransition.setViewVisibility(arrayList14, 4);
                                }
                            });
                        } else {
                            operation3 = operation8;
                        }
                    }
                    if (operation3.getFinalState() == SpecialEffectsController.Operation.State.VISIBLE) {
                        arrayList13.addAll(arrayList14);
                        if (z2) {
                            fragmentTransitionImpl2.setEpicenter(objCloneTransition, rect5);
                        }
                        view2 = view12;
                    } else {
                        view2 = view12;
                        fragmentTransitionImpl2.setEpicenter(objCloneTransition, view2);
                    }
                    map.put(operation3, true);
                    if (next2.isOverlapAllowed()) {
                        objMergeTransitionsTogether = fragmentTransitionImpl2.mergeTransitionsTogether(objMergeTransitionsTogether, objCloneTransition, null);
                        objMergeTransitionsTogether2 = obj;
                    } else {
                        objMergeTransitionsTogether2 = fragmentTransitionImpl2.mergeTransitionsTogether(obj, objCloneTransition, null);
                    }
                }
                it4 = it5;
                view12 = view2;
                obj4 = objMergeTransitionsTogether;
                obj3 = objMergeTransitionsTogether2;
                view13 = view;
                arrayList11 = arrayList;
                str = str3;
                arrayList12 = arrayList2;
                z3 = false;
            }
        }
        ArrayList<View> arrayList16 = arrayList11;
        String str6 = str;
        ArrayList<View> arrayList17 = arrayList12;
        Object objMergeTransitionsInSequence = fragmentTransitionImpl2.mergeTransitionsInSequence(obj4, obj3, obj2);
        if (objMergeTransitionsInSequence == null) {
            return map;
        }
        for (final TransitionInfo transitionInfo3 : list) {
            if (!transitionInfo3.isVisibilityUnchanged()) {
                Object transition = transitionInfo3.getTransition();
                final SpecialEffectsController.Operation operation9 = transitionInfo3.getOperation();
                boolean z5 = obj2 != null && (operation9 == operation5 || operation9 == operation7);
                if (transition == null && !z5) {
                    str2 = str6;
                } else if (!ViewCompat.isLaidOut(getContainer())) {
                    if (FragmentManager.isLoggingEnabled(2)) {
                        str2 = str6;
                        Log.v(str2, "SpecialEffectsController: Container " + getContainer() + " has not been laid out. Completing operation " + operation9);
                    } else {
                        str2 = str6;
                    }
                    transitionInfo3.completeSpecialEffect();
                } else {
                    str2 = str6;
                    fragmentTransitionImpl2.setListenerForTransitionEnd(transitionInfo3.getOperation().getFragment(), objMergeTransitionsInSequence, transitionInfo3.getSignal(), new Runnable() { // from class: androidx.fragment.app.MiuixSpecialEffectsController.8
                        @Override // java.lang.Runnable
                        public void run() {
                            transitionInfo3.completeSpecialEffect();
                            if (FragmentManager.isLoggingEnabled(2)) {
                                Log.v(FragmentManager.TAG, "Transition for operation " + operation9 + "has completed");
                            }
                        }
                    });
                }
                str6 = str2;
            }
        }
        String str7 = str6;
        if (!ViewCompat.isLaidOut(getContainer())) {
            return map;
        }
        MiuixFragmentTransition.setViewVisibility(arrayList13, 4);
        ArrayList<String> arrayListPrepareSetNameOverridesReordered = fragmentTransitionImpl2.prepareSetNameOverridesReordered(arrayList16);
        if (FragmentManager.isLoggingEnabled(2)) {
            Log.v(str7, ">>>>> Beginning transition <<<<<");
            Log.v(str7, ">>>>> SharedElementFirstOutViews <<<<<");
            for (View view14 : arrayList17) {
                Log.v(str7, "View: " + view14 + " Name: " + ViewCompat.getTransitionName(view14));
            }
            Log.v(str7, ">>>>> SharedElementLastInViews <<<<<");
            for (View view15 : arrayList16) {
                Log.v(str7, "View: " + view15 + " Name: " + ViewCompat.getTransitionName(view15));
            }
        }
        fragmentTransitionImpl2.beginDelayedTransition(getContainer(), objMergeTransitionsInSequence);
        fragmentTransitionImpl2.setNameOverridesReordered(getContainer(), arrayList17, arrayList16, arrayListPrepareSetNameOverridesReordered, arrayMap4);
        MiuixFragmentTransition.setViewVisibility(arrayList13, 0);
        fragmentTransitionImpl2.swapSharedElementTargets(obj2, arrayList17, arrayList16);
        return map;
    }

    void retainMatchingViews(ArrayMap<String, View> arrayMap, Collection<String> collection) {
        Iterator<Map.Entry<String, View>> it = arrayMap.entrySet().iterator();
        while (it.hasNext()) {
            if (!collection.contains(ViewCompat.getTransitionName(it.next().getValue()))) {
                it.remove();
            }
        }
    }

    void captureTransitioningViews(ArrayList<View> arrayList, View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (ViewGroupCompat.isTransitionGroup(viewGroup)) {
                if (arrayList.contains(view)) {
                    return;
                }
                arrayList.add(viewGroup);
                return;
            }
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    captureTransitioningViews(arrayList, childAt);
                }
            }
            return;
        }
        if (arrayList.contains(view)) {
            return;
        }
        arrayList.add(view);
    }

    void findNamedViews(Map<String, View> map, View view) {
        String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            map.put(transitionName, view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    findNamedViews(map, childAt);
                }
            }
        }
    }

    void applyContainerChanges(SpecialEffectsController.Operation operation) {
        operation.getFinalState().applyState(operation.getFragment().mView);
    }

    private static class SpecialEffectsInfo {
        private final SpecialEffectsController.Operation mOperation;
        private final CancellationSignal mSignal;

        SpecialEffectsInfo(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal) {
            this.mOperation = operation;
            this.mSignal = cancellationSignal;
        }

        SpecialEffectsController.Operation getOperation() {
            return this.mOperation;
        }

        CancellationSignal getSignal() {
            return this.mSignal;
        }

        boolean isVisibilityUnchanged() {
            SpecialEffectsController.Operation.State stateFrom = SpecialEffectsController.Operation.State.from(this.mOperation.getFragment().mView);
            SpecialEffectsController.Operation.State finalState = this.mOperation.getFinalState();
            return stateFrom == finalState || !(stateFrom == SpecialEffectsController.Operation.State.VISIBLE || finalState == SpecialEffectsController.Operation.State.VISIBLE);
        }

        void completeSpecialEffect() {
            this.mOperation.completeSpecialEffect(this.mSignal);
        }
    }

    private static class AnimationInfo extends SpecialEffectsInfo {
        private FragmentAnim.AnimationOrAnimator mAnimation;
        private boolean mIsPop;
        private boolean mLoadedAnim;

        AnimationInfo(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal, boolean z) {
            super(operation, cancellationSignal);
            this.mLoadedAnim = false;
            this.mIsPop = z;
        }

        FragmentAnim.AnimationOrAnimator getAnimation(Context context) {
            if (this.mLoadedAnim) {
                return this.mAnimation;
            }
            FragmentAnim.AnimationOrAnimator animationOrAnimatorLoadAnimation = FragmentAnim.loadAnimation(context, getOperation().getFragment(), getOperation().getFinalState() == SpecialEffectsController.Operation.State.VISIBLE, this.mIsPop);
            this.mAnimation = animationOrAnimatorLoadAnimation;
            this.mLoadedAnim = true;
            return animationOrAnimatorLoadAnimation;
        }
    }

    private static class TransitionInfo extends SpecialEffectsInfo {
        private final boolean mOverlapAllowed;
        private final Object mSharedElementTransition;
        private final Object mTransition;

        TransitionInfo(SpecialEffectsController.Operation operation, CancellationSignal cancellationSignal, boolean z, boolean z2) {
            Object exitTransition;
            Object enterTransition;
            boolean allowEnterTransitionOverlap;
            super(operation, cancellationSignal);
            if (operation.getFinalState() == SpecialEffectsController.Operation.State.VISIBLE) {
                if (z) {
                    enterTransition = operation.getFragment().getReenterTransition();
                } else {
                    enterTransition = operation.getFragment().getEnterTransition();
                }
                this.mTransition = enterTransition;
                if (z) {
                    allowEnterTransitionOverlap = operation.getFragment().getAllowReturnTransitionOverlap();
                } else {
                    allowEnterTransitionOverlap = operation.getFragment().getAllowEnterTransitionOverlap();
                }
                this.mOverlapAllowed = allowEnterTransitionOverlap;
            } else {
                if (z) {
                    exitTransition = operation.getFragment().getReturnTransition();
                } else {
                    exitTransition = operation.getFragment().getExitTransition();
                }
                this.mTransition = exitTransition;
                this.mOverlapAllowed = true;
            }
            if (!z2) {
                this.mSharedElementTransition = null;
            } else if (z) {
                this.mSharedElementTransition = operation.getFragment().getSharedElementReturnTransition();
            } else {
                this.mSharedElementTransition = operation.getFragment().getSharedElementEnterTransition();
            }
        }

        Object getTransition() {
            return this.mTransition;
        }

        boolean isOverlapAllowed() {
            return this.mOverlapAllowed;
        }

        public boolean hasSharedElementTransition() {
            return this.mSharedElementTransition != null;
        }

        public Object getSharedElementTransition() {
            return this.mSharedElementTransition;
        }

        FragmentTransitionImpl getHandlingImpl() {
            FragmentTransitionImpl handlingImpl = getHandlingImpl(this.mTransition);
            FragmentTransitionImpl handlingImpl2 = getHandlingImpl(this.mSharedElementTransition);
            if (handlingImpl == null || handlingImpl2 == null || handlingImpl == handlingImpl2) {
                return handlingImpl != null ? handlingImpl : handlingImpl2;
            }
            throw new IllegalArgumentException("Mixing framework transitions and AndroidX transitions is not allowed. Fragment " + getOperation().getFragment() + " returned Transition " + this.mTransition + " which uses a different Transition  type than its shared element transition " + this.mSharedElementTransition);
        }

        private FragmentTransitionImpl getHandlingImpl(Object obj) {
            if (obj == null) {
                return null;
            }
            if (MiuixFragmentTransition.PLATFORM_IMPL != null && MiuixFragmentTransition.PLATFORM_IMPL.canHandle(obj)) {
                return MiuixFragmentTransition.PLATFORM_IMPL;
            }
            if (MiuixFragmentTransition.SUPPORT_IMPL != null && MiuixFragmentTransition.SUPPORT_IMPL.canHandle(obj)) {
                return MiuixFragmentTransition.SUPPORT_IMPL;
            }
            if (MiuixFragmentTransition.MIUIX_IMPL != null && MiuixFragmentTransition.MIUIX_IMPL.canHandle(obj)) {
                return MiuixFragmentTransition.MIUIX_IMPL;
            }
            throw new IllegalArgumentException("Transition " + obj + " for fragment " + getOperation().getFragment() + " is not a valid framework Transition or AndroidX Transition");
        }
    }
}
