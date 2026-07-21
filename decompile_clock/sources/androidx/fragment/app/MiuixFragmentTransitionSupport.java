package androidx.fragment.app;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import androidx.transition.MiuixTransitionManager;
import java.util.ArrayList;
import java.util.List;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.transition.MiuixTransition;
import miuix.transition.MiuixTransitionSet;
import miuix.transition.TransitionListenerAdapter;

/* JADX INFO: loaded from: classes.dex */
public class MiuixFragmentTransitionSupport extends FragmentTransitionImpl {
    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void setEpicenter(Object obj, Rect rect) {
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void setEpicenter(Object obj, View view) {
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public boolean canHandle(Object obj) {
        return obj instanceof MiuixTransition;
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public Object cloneTransition(Object obj) {
        if (obj != null) {
            return ((MiuixTransition) obj).clone();
        }
        return null;
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public Object wrapTransitionInSet(Object obj) {
        if (obj == null) {
            return null;
        }
        MiuixTransitionSet miuixTransitionSet = new MiuixTransitionSet();
        miuixTransitionSet.addTransition((MiuixTransition) obj);
        return miuixTransitionSet;
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void setSharedElementTargets(Object obj, View view, ArrayList<View> arrayList) {
        MiuixTransitionSet miuixTransitionSet = (MiuixTransitionSet) obj;
        List<View> targets = miuixTransitionSet.getTargets();
        targets.clear();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            bfsAddViewChildren(targets, arrayList.get(i));
        }
        targets.add(view);
        arrayList.add(view);
        addTargets(miuixTransitionSet, arrayList);
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void addTargets(Object obj, ArrayList<View> arrayList) {
        if (obj == null) {
            return;
        }
        MiuixTransition miuixTransition = (MiuixTransition) obj;
        int i = 0;
        if (miuixTransition instanceof MiuixTransitionSet) {
            MiuixTransitionSet miuixTransitionSet = (MiuixTransitionSet) miuixTransition;
            int transitionCount = miuixTransitionSet.getTransitionCount();
            while (i < transitionCount) {
                addTargets(miuixTransitionSet.getTransitionAt(i), arrayList);
                i++;
            }
            return;
        }
        if (hasSimpleTarget(miuixTransition) || !isNullOrEmpty(miuixTransition.getTargets())) {
            return;
        }
        int size = arrayList.size();
        while (i < size) {
            miuixTransition.addTarget(arrayList.get(i));
            i++;
        }
    }

    private static boolean hasSimpleTarget(MiuixTransition miuixTransition) {
        return (isNullOrEmpty(miuixTransition.getTargetIds()) && isNullOrEmpty(miuixTransition.getTargetNames()) && isNullOrEmpty(miuixTransition.getTargetTypes())) ? false : true;
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public Object mergeTransitionsTogether(Object obj, Object obj2, Object obj3) {
        MiuixTransitionSet miuixTransitionSet = new MiuixTransitionSet();
        if (obj != null) {
            miuixTransitionSet.addTransition((MiuixTransition) obj);
        }
        if (obj2 != null) {
            miuixTransitionSet.addTransition((MiuixTransition) obj2);
        }
        if (obj3 != null) {
            miuixTransitionSet.addTransition((MiuixTransition) obj3);
        }
        return miuixTransitionSet;
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void scheduleHideFragmentView(Object obj, final View view, final ArrayList<View> arrayList) {
        final AnimConfig animConfig = ((MiuixTransition) obj).getAnimConfig();
        animConfig.addListeners(new TransitionListener() { // from class: androidx.fragment.app.MiuixFragmentTransitionSupport.1
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj2) {
                animConfig.removeListeners(this);
                view.setVisibility(8);
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ((View) arrayList.get(i)).setVisibility(0);
                }
            }
        });
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public Object mergeTransitionsInSequence(Object obj, Object obj2, Object obj3) {
        MiuixTransition ordering = (MiuixTransition) obj;
        MiuixTransition miuixTransition = (MiuixTransition) obj2;
        MiuixTransition miuixTransition2 = (MiuixTransition) obj3;
        if (ordering != null && miuixTransition != null) {
            ordering = new MiuixTransitionSet().addTransition(ordering).addTransition(miuixTransition).setOrdering(1);
        } else if (ordering == null) {
            ordering = miuixTransition != null ? miuixTransition : null;
        }
        if (miuixTransition2 == null) {
            return ordering;
        }
        MiuixTransitionSet miuixTransitionSet = new MiuixTransitionSet();
        if (ordering != null) {
            miuixTransitionSet.addTransition(ordering);
        }
        miuixTransitionSet.addTransition(miuixTransition2);
        return miuixTransitionSet;
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void beginDelayedTransition(ViewGroup viewGroup, Object obj) {
        MiuixTransitionManager.beginDelayedTransition(viewGroup, (MiuixTransition) obj);
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void scheduleRemoveTargets(Object obj, final Object obj2, final ArrayList<View> arrayList, final Object obj3, final ArrayList<View> arrayList2, final Object obj4, final ArrayList<View> arrayList3) {
        ((MiuixTransition) obj).addListener(new TransitionListenerAdapter() { // from class: androidx.fragment.app.MiuixFragmentTransitionSupport.2
            @Override // miuix.transition.TransitionListenerAdapter, miuix.transition.MiuixTransition.MiuixTransitionListener
            public void onTransitionStart(MiuixTransition miuixTransition) {
                Object obj5 = obj2;
                if (obj5 != null) {
                    MiuixFragmentTransitionSupport.this.replaceTargets(obj5, arrayList, null);
                }
                Object obj6 = obj3;
                if (obj6 != null) {
                    MiuixFragmentTransitionSupport.this.replaceTargets(obj6, arrayList2, null);
                }
                Object obj7 = obj4;
                if (obj7 != null) {
                    MiuixFragmentTransitionSupport.this.replaceTargets(obj7, arrayList3, null);
                }
            }

            @Override // miuix.transition.TransitionListenerAdapter, miuix.transition.MiuixTransition.MiuixTransitionListener
            public void onTransitionEnd(MiuixTransition miuixTransition) {
                miuixTransition.removeListener(this);
            }
        });
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void swapSharedElementTargets(Object obj, ArrayList<View> arrayList, ArrayList<View> arrayList2) {
        MiuixTransitionSet miuixTransitionSet = (MiuixTransitionSet) obj;
        if (miuixTransitionSet != null) {
            miuixTransitionSet.getTargets().clear();
            miuixTransitionSet.getTargets().addAll(arrayList2);
            replaceTargets(miuixTransitionSet, arrayList, arrayList2);
        }
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void replaceTargets(Object obj, ArrayList<View> arrayList, ArrayList<View> arrayList2) {
        List<View> targets;
        MiuixTransition miuixTransition = (MiuixTransition) obj;
        int i = 0;
        if (miuixTransition instanceof MiuixTransitionSet) {
            MiuixTransitionSet miuixTransitionSet = (MiuixTransitionSet) miuixTransition;
            int transitionCount = miuixTransitionSet.getTransitionCount();
            while (i < transitionCount) {
                replaceTargets(miuixTransitionSet.getTransitionAt(i), arrayList, arrayList2);
                i++;
            }
            return;
        }
        if (hasSimpleTarget(miuixTransition) || (targets = miuixTransition.getTargets()) == null || targets.size() != arrayList.size() || !targets.containsAll(arrayList)) {
            return;
        }
        int size = arrayList2 == null ? 0 : arrayList2.size();
        while (i < size) {
            miuixTransition.addTarget(arrayList2.get(i));
            i++;
        }
        for (int size2 = arrayList.size() - 1; size2 >= 0; size2--) {
            miuixTransition.removeTarget(arrayList.get(size2));
        }
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void addTarget(Object obj, View view) {
        if (obj != null) {
            ((MiuixTransition) obj).addTarget(view);
        }
    }

    @Override // androidx.fragment.app.FragmentTransitionImpl
    public void removeTarget(Object obj, View view) {
        if (obj != null) {
            ((MiuixTransition) obj).removeTarget(view);
        }
    }
}
