package miuix.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.animation.base.AnimConfig;

/* JADX INFO: loaded from: classes3.dex */
public class MiuixTransitionSet extends MiuixTransition {
    public static final int ORDERING_SEQUENTIAL = 1;
    public static final int ORDERING_TOGETHER = 0;
    int mCurrentListeners;
    private boolean mPlayTogether;
    boolean mStarted;
    ArrayList<MiuixTransition> mTransitions;

    public MiuixTransitionSet() {
        this.mTransitions = new ArrayList<>();
        this.mPlayTogether = true;
        this.mStarted = false;
    }

    public MiuixTransitionSet(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTransitions = new ArrayList<>();
        this.mPlayTogether = true;
        this.mStarted = false;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TransitionSet);
        setOrdering(typedArrayObtainStyledAttributes.getInt(R.styleable.TransitionSet_transitionOrdering, 0));
        typedArrayObtainStyledAttributes.recycle();
    }

    public MiuixTransitionSet setOrdering(int i) {
        if (i == 0) {
            this.mPlayTogether = true;
        } else if (i == 1) {
            this.mPlayTogether = false;
        } else {
            throw new AndroidRuntimeException("Invalid parameter for TransitionSet ordering: " + i);
        }
        return this;
    }

    public int getOrdering() {
        return !this.mPlayTogether ? 1 : 0;
    }

    public MiuixTransitionSet addTransition(MiuixTransition miuixTransition) {
        if (miuixTransition != null) {
            addTransitionInternal(miuixTransition);
        }
        return this;
    }

    private void addTransitionInternal(MiuixTransition miuixTransition) {
        this.mTransitions.add(miuixTransition);
        miuixTransition.mParent = this;
    }

    public int getTransitionCount() {
        return this.mTransitions.size();
    }

    public MiuixTransition getTransitionAt(int i) {
        if (i < 0 || i >= this.mTransitions.size()) {
            return null;
        }
        return this.mTransitions.get(i);
    }

    private void setupStartEndListeners() {
        TransitionSetListener transitionSetListener = new TransitionSetListener(this);
        Iterator<MiuixTransition> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            it.next().addListener(transitionSetListener);
        }
        this.mCurrentListeners = this.mTransitions.size();
    }

    static class TransitionSetListener extends TransitionListenerAdapter {
        MiuixTransitionSet mTransitionSet;

        TransitionSetListener(MiuixTransitionSet miuixTransitionSet) {
            this.mTransitionSet = miuixTransitionSet;
        }

        @Override // miuix.transition.TransitionListenerAdapter, miuix.transition.MiuixTransition.MiuixTransitionListener
        public void onTransitionStart(MiuixTransition miuixTransition) {
            if (this.mTransitionSet.mStarted) {
                return;
            }
            this.mTransitionSet.onTransitionStart();
            this.mTransitionSet.mStarted = true;
        }

        @Override // miuix.transition.TransitionListenerAdapter, miuix.transition.MiuixTransition.MiuixTransitionListener
        public void onTransitionEnd(MiuixTransition miuixTransition) {
            this.mTransitionSet.mCurrentListeners--;
            if (this.mTransitionSet.mCurrentListeners == 0) {
                this.mTransitionSet.mStarted = false;
                this.mTransitionSet.onTransitionEnd();
            }
            miuixTransition.removeListener(this);
        }
    }

    @Override // miuix.transition.MiuixTransition
    protected void createAnimators(ViewGroup viewGroup, TransitionValuesMaps transitionValuesMaps, TransitionValuesMaps transitionValuesMaps2, ArrayList<TransitionValues> arrayList, ArrayList<TransitionValues> arrayList2) {
        int size = this.mTransitions.size();
        for (int i = 0; i < size; i++) {
            this.mTransitions.get(i).createAnimators(viewGroup, transitionValuesMaps, transitionValuesMaps2, arrayList, arrayList2);
        }
    }

    @Override // miuix.transition.MiuixTransition
    protected void runAnimators() {
        if (this.mTransitions.isEmpty()) {
            onTransitionStart();
            onTransitionEnd();
            return;
        }
        setupStartEndListeners();
        int size = this.mTransitions.size();
        if (this.mPlayTogether) {
            for (int i = 0; i < size; i++) {
                this.mTransitions.get(i).runAnimators();
            }
            return;
        }
        for (int i2 = 1; i2 < size; i2++) {
            final MiuixTransition miuixTransition = this.mTransitions.get(i2 - 1);
            final MiuixTransition miuixTransition2 = this.mTransitions.get(i2);
            miuixTransition.addListener(new TransitionListenerAdapter() { // from class: miuix.transition.MiuixTransitionSet.1
                @Override // miuix.transition.TransitionListenerAdapter, miuix.transition.MiuixTransition.MiuixTransitionListener
                public void onTransitionEnd(MiuixTransition miuixTransition3) {
                    miuixTransition.removeListener(this);
                    miuixTransition2.runAnimators();
                }
            });
        }
        MiuixTransition miuixTransition3 = this.mTransitions.get(0);
        if (miuixTransition3 != null) {
            miuixTransition3.runAnimators();
        }
    }

    @Override // miuix.transition.MiuixTransition
    public void forceToEnd() {
        int size = this.mTransitions.size();
        for (int i = 0; i < size; i++) {
            this.mTransitions.get(i).forceToEnd();
        }
    }

    @Override // miuix.transition.MiuixTransition
    public void clear() {
        int size = this.mTransitions.size();
        for (int i = 0; i < size; i++) {
            this.mTransitions.get(i).clear();
        }
    }

    @Override // miuix.transition.MiuixTransition
    public void captureStartValues(TransitionValues transitionValues) {
        if (isValidTarget(transitionValues.view)) {
            for (MiuixTransition miuixTransition : this.mTransitions) {
                if (miuixTransition.isValidTarget(transitionValues.view)) {
                    miuixTransition.captureStartValues(transitionValues);
                    transitionValues.mTargetedTransitions.add(miuixTransition);
                }
            }
        }
    }

    @Override // miuix.transition.MiuixTransition
    public void captureEndValues(TransitionValues transitionValues) {
        if (isValidTarget(transitionValues.view)) {
            for (MiuixTransition miuixTransition : this.mTransitions) {
                if (miuixTransition.isValidTarget(transitionValues.view)) {
                    miuixTransition.captureEndValues(transitionValues);
                    transitionValues.mTargetedTransitions.add(miuixTransition);
                }
            }
        }
    }

    @Override // miuix.transition.MiuixTransition
    public MiuixTransitionSet setSceneRoot(ViewGroup viewGroup) {
        super.setSceneRoot(viewGroup);
        int size = this.mTransitions.size();
        for (int i = 0; i < size; i++) {
            this.mTransitions.get(i).setSceneRoot(viewGroup);
        }
        return this;
    }

    @Override // miuix.transition.MiuixTransition
    public void setCanRemoveViews(boolean z) {
        super.setCanRemoveViews(z);
        int size = this.mTransitions.size();
        for (int i = 0; i < size; i++) {
            this.mTransitions.get(i).setCanRemoveViews(z);
        }
    }

    @Override // miuix.transition.MiuixTransition
    public MiuixTransition setAnimConfig(AnimConfig animConfig) {
        super.setAnimConfig(animConfig);
        int size = this.mTransitions.size();
        for (int i = 0; i < size; i++) {
            this.mTransitions.get(i).setAnimConfig(animConfig);
        }
        return this;
    }

    @Override // miuix.transition.MiuixTransition
    public MiuixTransition setTransitionMode(int i) {
        super.setTransitionMode(i);
        int size = this.mTransitions.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mTransitions.get(i2).setTransitionMode(i);
        }
        return this;
    }

    @Override // miuix.transition.MiuixTransition
    String toString(String str) {
        String string = super.toString(str);
        for (int i = 0; i < this.mTransitions.size(); i++) {
            string = string + "\n" + this.mTransitions.get(i).toString(str + "  ");
        }
        return string;
    }

    @Override // miuix.transition.MiuixTransition
    public MiuixTransitionSet clone() {
        MiuixTransitionSet miuixTransitionSet = (MiuixTransitionSet) super.clone();
        miuixTransitionSet.mTransitions = new ArrayList<>();
        int size = this.mTransitions.size();
        for (int i = 0; i < size; i++) {
            miuixTransitionSet.addTransitionInternal(this.mTransitions.get(i).clone());
        }
        return miuixTransitionSet;
    }
}
