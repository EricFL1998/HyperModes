package androidx.constraintlayout.core.widgets;

import androidx.constraintlayout.core.LinearSystem;

/* JADX INFO: loaded from: classes.dex */
public class Placeholder extends VirtualLayout {
    @Override // androidx.constraintlayout.core.widgets.VirtualLayout
    public void measure(int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        if (this.mWidgetsCount > 0) {
            paddingLeft += this.mWidgets[0].getWidth();
            paddingTop += this.mWidgets[0].getHeight();
        }
        int iMax = Math.max(getMinWidth(), paddingLeft);
        int iMax2 = Math.max(getMinHeight(), paddingTop);
        if (i != 1073741824) {
            if (i == Integer.MIN_VALUE) {
                i2 = Math.min(iMax, i2);
            } else {
                i2 = i == 0 ? iMax : 0;
            }
        }
        if (i3 != 1073741824) {
            if (i3 == Integer.MIN_VALUE) {
                i4 = Math.min(iMax2, i4);
            } else {
                i4 = i3 == 0 ? iMax2 : 0;
            }
        }
        setMeasure(i2, i4);
        setWidth(i2);
        setHeight(i4);
        needsCallbackFromSolver(this.mWidgetsCount > 0);
    }

    @Override // androidx.constraintlayout.core.widgets.ConstraintWidget
    public void addToSolver(LinearSystem linearSystem, boolean z) {
        super.addToSolver(linearSystem, z);
        if (this.mWidgetsCount > 0) {
            ConstraintWidget constraintWidget = this.mWidgets[0];
            constraintWidget.resetAllConstraints();
            constraintWidget.connect(ConstraintAnchor.Type.LEFT, this, ConstraintAnchor.Type.LEFT);
            constraintWidget.connect(ConstraintAnchor.Type.RIGHT, this, ConstraintAnchor.Type.RIGHT);
            constraintWidget.connect(ConstraintAnchor.Type.TOP, this, ConstraintAnchor.Type.TOP);
            constraintWidget.connect(ConstraintAnchor.Type.BOTTOM, this, ConstraintAnchor.Type.BOTTOM);
        }
    }
}
