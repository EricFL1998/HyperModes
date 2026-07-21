package androidx.constraintlayout.core.state.helpers;

import androidx.constraintlayout.core.state.ConstraintReference;
import androidx.constraintlayout.core.state.HelperReference;
import androidx.constraintlayout.core.state.State;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class AlignVerticallyReference extends HelperReference {
    private float mBias;

    public AlignVerticallyReference(State state) {
        super(state, State.Helper.ALIGN_VERTICALLY);
        this.mBias = 0.5f;
    }

    @Override // androidx.constraintlayout.core.state.HelperReference, androidx.constraintlayout.core.state.ConstraintReference, androidx.constraintlayout.core.state.Reference
    public void apply() {
        Iterator<Object> it = this.mReferences.iterator();
        while (it.hasNext()) {
            ConstraintReference constraintReferenceConstraints = this.mState.constraints(it.next());
            constraintReferenceConstraints.clearVertical();
            if (this.mTopToTop != null) {
                constraintReferenceConstraints.topToTop(this.mTopToTop);
            } else if (this.mTopToBottom != null) {
                constraintReferenceConstraints.topToBottom(this.mTopToBottom);
            } else {
                constraintReferenceConstraints.topToTop(State.PARENT);
            }
            if (this.mBottomToTop != null) {
                constraintReferenceConstraints.bottomToTop(this.mBottomToTop);
            } else if (this.mBottomToBottom != null) {
                constraintReferenceConstraints.bottomToBottom(this.mBottomToBottom);
            } else {
                constraintReferenceConstraints.bottomToBottom(State.PARENT);
            }
            float f = this.mBias;
            if (f != 0.5f) {
                constraintReferenceConstraints.verticalBias(f);
            }
        }
    }
}
