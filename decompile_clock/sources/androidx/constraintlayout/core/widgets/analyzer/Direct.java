package androidx.constraintlayout.core.widgets.analyzer;

import androidx.constraintlayout.core.LinearSystem;
import androidx.constraintlayout.core.widgets.Barrier;
import androidx.constraintlayout.core.widgets.ChainHead;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class Direct {
    private static final boolean APPLY_MATCH_PARENT = false;
    private static final boolean DEBUG = false;
    private static final boolean EARLY_TERMINATION = true;
    private static BasicMeasure.Measure measure = new BasicMeasure.Measure();
    private static int hcount = 0;
    private static int vcount = 0;

    public static void solvingPass(ConstraintWidgetContainer constraintWidgetContainer, BasicMeasure.Measurer measurer) {
        ConstraintWidget.DimensionBehaviour horizontalDimensionBehaviour = constraintWidgetContainer.getHorizontalDimensionBehaviour();
        ConstraintWidget.DimensionBehaviour verticalDimensionBehaviour = constraintWidgetContainer.getVerticalDimensionBehaviour();
        hcount = 0;
        vcount = 0;
        constraintWidgetContainer.resetFinalResolution();
        ArrayList<ConstraintWidget> children = constraintWidgetContainer.getChildren();
        int size = children.size();
        for (int i = 0; i < size; i++) {
            children.get(i).resetFinalResolution();
        }
        boolean zIsRtl = constraintWidgetContainer.isRtl();
        if (horizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.FIXED) {
            constraintWidgetContainer.setFinalHorizontal(0, constraintWidgetContainer.getWidth());
        } else {
            constraintWidgetContainer.setFinalLeft(0);
        }
        boolean z = false;
        boolean z2 = false;
        for (int i2 = 0; i2 < size; i2++) {
            ConstraintWidget constraintWidget = children.get(i2);
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 1) {
                    if (guideline.getRelativeBegin() != -1) {
                        guideline.setFinalValue(guideline.getRelativeBegin());
                    } else if (guideline.getRelativeEnd() != -1 && constraintWidgetContainer.isResolvedHorizontally()) {
                        guideline.setFinalValue(constraintWidgetContainer.getWidth() - guideline.getRelativeEnd());
                    } else if (constraintWidgetContainer.isResolvedHorizontally()) {
                        guideline.setFinalValue((int) ((guideline.getRelativePercent() * constraintWidgetContainer.getWidth()) + 0.5f));
                    }
                    z = true;
                }
            } else if ((constraintWidget instanceof Barrier) && ((Barrier) constraintWidget).getOrientation() == 0) {
                z2 = true;
            }
        }
        if (z) {
            for (int i3 = 0; i3 < size; i3++) {
                ConstraintWidget constraintWidget2 = children.get(i3);
                if (constraintWidget2 instanceof Guideline) {
                    Guideline guideline2 = (Guideline) constraintWidget2;
                    if (guideline2.getOrientation() == 1) {
                        horizontalSolvingPass(0, guideline2, measurer, zIsRtl);
                    }
                }
            }
        }
        horizontalSolvingPass(0, constraintWidgetContainer, measurer, zIsRtl);
        if (z2) {
            for (int i4 = 0; i4 < size; i4++) {
                ConstraintWidget constraintWidget3 = children.get(i4);
                if (constraintWidget3 instanceof Barrier) {
                    Barrier barrier = (Barrier) constraintWidget3;
                    if (barrier.getOrientation() == 0) {
                        solveBarrier(0, barrier, measurer, 0, zIsRtl);
                    }
                }
            }
        }
        if (verticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.FIXED) {
            constraintWidgetContainer.setFinalVertical(0, constraintWidgetContainer.getHeight());
        } else {
            constraintWidgetContainer.setFinalTop(0);
        }
        boolean z3 = false;
        boolean z4 = false;
        for (int i5 = 0; i5 < size; i5++) {
            ConstraintWidget constraintWidget4 = children.get(i5);
            if (constraintWidget4 instanceof Guideline) {
                Guideline guideline3 = (Guideline) constraintWidget4;
                if (guideline3.getOrientation() == 0) {
                    if (guideline3.getRelativeBegin() != -1) {
                        guideline3.setFinalValue(guideline3.getRelativeBegin());
                    } else if (guideline3.getRelativeEnd() != -1 && constraintWidgetContainer.isResolvedVertically()) {
                        guideline3.setFinalValue(constraintWidgetContainer.getHeight() - guideline3.getRelativeEnd());
                    } else if (constraintWidgetContainer.isResolvedVertically()) {
                        guideline3.setFinalValue((int) ((guideline3.getRelativePercent() * constraintWidgetContainer.getHeight()) + 0.5f));
                    }
                    z3 = true;
                }
            } else if ((constraintWidget4 instanceof Barrier) && ((Barrier) constraintWidget4).getOrientation() == 1) {
                z4 = true;
            }
        }
        if (z3) {
            for (int i6 = 0; i6 < size; i6++) {
                ConstraintWidget constraintWidget5 = children.get(i6);
                if (constraintWidget5 instanceof Guideline) {
                    Guideline guideline4 = (Guideline) constraintWidget5;
                    if (guideline4.getOrientation() == 0) {
                        verticalSolvingPass(1, guideline4, measurer);
                    }
                }
            }
        }
        verticalSolvingPass(0, constraintWidgetContainer, measurer);
        if (z4) {
            for (int i7 = 0; i7 < size; i7++) {
                ConstraintWidget constraintWidget6 = children.get(i7);
                if (constraintWidget6 instanceof Barrier) {
                    Barrier barrier2 = (Barrier) constraintWidget6;
                    if (barrier2.getOrientation() == 1) {
                        solveBarrier(0, barrier2, measurer, 1, zIsRtl);
                    }
                }
            }
        }
        for (int i8 = 0; i8 < size; i8++) {
            ConstraintWidget constraintWidget7 = children.get(i8);
            if (constraintWidget7.isMeasureRequested() && canMeasure(0, constraintWidget7)) {
                ConstraintWidgetContainer.measure(0, constraintWidget7, measurer, measure, BasicMeasure.Measure.SELF_DIMENSIONS);
                if (constraintWidget7 instanceof Guideline) {
                    if (((Guideline) constraintWidget7).getOrientation() == 0) {
                        verticalSolvingPass(0, constraintWidget7, measurer);
                    } else {
                        horizontalSolvingPass(0, constraintWidget7, measurer, zIsRtl);
                    }
                } else {
                    horizontalSolvingPass(0, constraintWidget7, measurer, zIsRtl);
                    verticalSolvingPass(0, constraintWidget7, measurer);
                }
            }
        }
    }

    private static void solveBarrier(int i, Barrier barrier, BasicMeasure.Measurer measurer, int i2, boolean z) {
        if (barrier.allSolved()) {
            if (i2 == 0) {
                horizontalSolvingPass(i + 1, barrier, measurer, z);
            } else {
                verticalSolvingPass(i + 1, barrier, measurer);
            }
        }
    }

    public static String ls(int i) {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++) {
            sb.append("  ");
        }
        sb.append("+-(" + i + ") ");
        return sb.toString();
    }

    private static void horizontalSolvingPass(int i, ConstraintWidget constraintWidget, BasicMeasure.Measurer measurer, boolean z) {
        if (constraintWidget.isHorizontalSolvingPassDone()) {
            return;
        }
        boolean z2 = true;
        hcount++;
        if (!(constraintWidget instanceof ConstraintWidgetContainer) && constraintWidget.isMeasureRequested()) {
            int i2 = i + 1;
            if (canMeasure(i2, constraintWidget)) {
                ConstraintWidgetContainer.measure(i2, constraintWidget, measurer, new BasicMeasure.Measure(), BasicMeasure.Measure.SELF_DIMENSIONS);
            }
        }
        ConstraintAnchor anchor = constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT);
        ConstraintAnchor anchor2 = constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT);
        int finalValue = anchor.getFinalValue();
        int finalValue2 = anchor2.getFinalValue();
        if (anchor.getDependents() != null && anchor.hasFinalValue()) {
            Iterator<ConstraintAnchor> it = anchor.getDependents().iterator();
            while (it.hasNext()) {
                ConstraintAnchor next = it.next();
                ConstraintWidget constraintWidget2 = next.mOwner;
                int i3 = i + 1;
                boolean zCanMeasure = canMeasure(i3, constraintWidget2);
                if (constraintWidget2.isMeasureRequested() && zCanMeasure) {
                    ConstraintWidgetContainer.measure(i3, constraintWidget2, measurer, new BasicMeasure.Measure(), BasicMeasure.Measure.SELF_DIMENSIONS);
                }
                boolean z3 = ((next == constraintWidget2.mLeft && constraintWidget2.mRight.mTarget != null && constraintWidget2.mRight.mTarget.hasFinalValue()) || (next == constraintWidget2.mRight && constraintWidget2.mLeft.mTarget != null && constraintWidget2.mLeft.mTarget.hasFinalValue())) ? z2 : false;
                if (constraintWidget2.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || zCanMeasure) {
                    if (!constraintWidget2.isMeasureRequested()) {
                        if (next == constraintWidget2.mLeft && constraintWidget2.mRight.mTarget == null) {
                            int margin = constraintWidget2.mLeft.getMargin() + finalValue;
                            constraintWidget2.setFinalHorizontal(margin, constraintWidget2.getWidth() + margin);
                            horizontalSolvingPass(i3, constraintWidget2, measurer, z);
                        } else if (next == constraintWidget2.mRight && constraintWidget2.mLeft.mTarget == null) {
                            int margin2 = finalValue - constraintWidget2.mRight.getMargin();
                            constraintWidget2.setFinalHorizontal(margin2 - constraintWidget2.getWidth(), margin2);
                            horizontalSolvingPass(i3, constraintWidget2, measurer, z);
                        } else if (z3 && !constraintWidget2.isInHorizontalChain()) {
                            solveHorizontalCenterConstraints(i3, measurer, constraintWidget2, z);
                        }
                    }
                } else if (constraintWidget2.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.mMatchConstraintMaxWidth >= 0 && constraintWidget2.mMatchConstraintMinWidth >= 0 && ((constraintWidget2.getVisibility() == 8 || (constraintWidget2.mMatchConstraintDefaultWidth == 0 && constraintWidget2.getDimensionRatio() == 0.0f)) && !constraintWidget2.isInHorizontalChain() && !constraintWidget2.isInVirtualLayout() && z3 && !constraintWidget2.isInHorizontalChain())) {
                    solveHorizontalMatchConstraint(i3, constraintWidget, measurer, constraintWidget2, z);
                }
                z2 = true;
            }
        }
        if (constraintWidget instanceof Guideline) {
            return;
        }
        if (anchor2.getDependents() != null && anchor2.hasFinalValue()) {
            Iterator<ConstraintAnchor> it2 = anchor2.getDependents().iterator();
            while (it2.hasNext()) {
                ConstraintAnchor next2 = it2.next();
                ConstraintWidget constraintWidget3 = next2.mOwner;
                int i4 = i + 1;
                boolean zCanMeasure2 = canMeasure(i4, constraintWidget3);
                if (constraintWidget3.isMeasureRequested() && zCanMeasure2) {
                    ConstraintWidgetContainer.measure(i4, constraintWidget3, measurer, new BasicMeasure.Measure(), BasicMeasure.Measure.SELF_DIMENSIONS);
                }
                boolean z4 = (next2 == constraintWidget3.mLeft && constraintWidget3.mRight.mTarget != null && constraintWidget3.mRight.mTarget.hasFinalValue()) || (next2 == constraintWidget3.mRight && constraintWidget3.mLeft.mTarget != null && constraintWidget3.mLeft.mTarget.hasFinalValue());
                if (constraintWidget3.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || zCanMeasure2) {
                    if (!constraintWidget3.isMeasureRequested()) {
                        if (next2 == constraintWidget3.mLeft && constraintWidget3.mRight.mTarget == null) {
                            int margin3 = constraintWidget3.mLeft.getMargin() + finalValue2;
                            constraintWidget3.setFinalHorizontal(margin3, constraintWidget3.getWidth() + margin3);
                            horizontalSolvingPass(i4, constraintWidget3, measurer, z);
                        } else if (next2 == constraintWidget3.mRight && constraintWidget3.mLeft.mTarget == null) {
                            int margin4 = finalValue2 - constraintWidget3.mRight.getMargin();
                            constraintWidget3.setFinalHorizontal(margin4 - constraintWidget3.getWidth(), margin4);
                            horizontalSolvingPass(i4, constraintWidget3, measurer, z);
                        } else if (z4 && !constraintWidget3.isInHorizontalChain()) {
                            solveHorizontalCenterConstraints(i4, measurer, constraintWidget3, z);
                        }
                    }
                } else if (constraintWidget3.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget3.mMatchConstraintMaxWidth >= 0 && constraintWidget3.mMatchConstraintMinWidth >= 0 && (constraintWidget3.getVisibility() == 8 || (constraintWidget3.mMatchConstraintDefaultWidth == 0 && constraintWidget3.getDimensionRatio() == 0.0f))) {
                    if (!constraintWidget3.isInHorizontalChain() && !constraintWidget3.isInVirtualLayout() && z4 && !constraintWidget3.isInHorizontalChain()) {
                        solveHorizontalMatchConstraint(i4, constraintWidget, measurer, constraintWidget3, z);
                    }
                }
            }
        }
        constraintWidget.markHorizontalSolvingPassDone();
    }

    private static void verticalSolvingPass(int i, ConstraintWidget constraintWidget, BasicMeasure.Measurer measurer) {
        if (constraintWidget.isVerticalSolvingPassDone()) {
            return;
        }
        vcount++;
        if (!(constraintWidget instanceof ConstraintWidgetContainer) && constraintWidget.isMeasureRequested()) {
            int i2 = i + 1;
            if (canMeasure(i2, constraintWidget)) {
                ConstraintWidgetContainer.measure(i2, constraintWidget, measurer, new BasicMeasure.Measure(), BasicMeasure.Measure.SELF_DIMENSIONS);
            }
        }
        ConstraintAnchor anchor = constraintWidget.getAnchor(ConstraintAnchor.Type.TOP);
        ConstraintAnchor anchor2 = constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM);
        int finalValue = anchor.getFinalValue();
        int finalValue2 = anchor2.getFinalValue();
        if (anchor.getDependents() != null && anchor.hasFinalValue()) {
            Iterator<ConstraintAnchor> it = anchor.getDependents().iterator();
            while (it.hasNext()) {
                ConstraintAnchor next = it.next();
                ConstraintWidget constraintWidget2 = next.mOwner;
                int i3 = i + 1;
                boolean zCanMeasure = canMeasure(i3, constraintWidget2);
                if (constraintWidget2.isMeasureRequested() && zCanMeasure) {
                    ConstraintWidgetContainer.measure(i3, constraintWidget2, measurer, new BasicMeasure.Measure(), BasicMeasure.Measure.SELF_DIMENSIONS);
                }
                boolean z = (next == constraintWidget2.mTop && constraintWidget2.mBottom.mTarget != null && constraintWidget2.mBottom.mTarget.hasFinalValue()) || (next == constraintWidget2.mBottom && constraintWidget2.mTop.mTarget != null && constraintWidget2.mTop.mTarget.hasFinalValue());
                if (constraintWidget2.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || zCanMeasure) {
                    if (!constraintWidget2.isMeasureRequested()) {
                        if (next == constraintWidget2.mTop && constraintWidget2.mBottom.mTarget == null) {
                            int margin = constraintWidget2.mTop.getMargin() + finalValue;
                            constraintWidget2.setFinalVertical(margin, constraintWidget2.getHeight() + margin);
                            verticalSolvingPass(i3, constraintWidget2, measurer);
                        } else if (next == constraintWidget2.mBottom && constraintWidget2.mTop.mTarget == null) {
                            int margin2 = finalValue - constraintWidget2.mBottom.getMargin();
                            constraintWidget2.setFinalVertical(margin2 - constraintWidget2.getHeight(), margin2);
                            verticalSolvingPass(i3, constraintWidget2, measurer);
                        } else if (z && !constraintWidget2.isInVerticalChain()) {
                            solveVerticalCenterConstraints(i3, measurer, constraintWidget2);
                        }
                    }
                } else if (constraintWidget2.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.mMatchConstraintMaxHeight >= 0 && constraintWidget2.mMatchConstraintMinHeight >= 0 && (constraintWidget2.getVisibility() == 8 || (constraintWidget2.mMatchConstraintDefaultHeight == 0 && constraintWidget2.getDimensionRatio() == 0.0f))) {
                    if (!constraintWidget2.isInVerticalChain() && !constraintWidget2.isInVirtualLayout() && z && !constraintWidget2.isInVerticalChain()) {
                        solveVerticalMatchConstraint(i3, constraintWidget, measurer, constraintWidget2);
                    }
                }
            }
        }
        if (constraintWidget instanceof Guideline) {
            return;
        }
        if (anchor2.getDependents() != null && anchor2.hasFinalValue()) {
            Iterator<ConstraintAnchor> it2 = anchor2.getDependents().iterator();
            while (it2.hasNext()) {
                ConstraintAnchor next2 = it2.next();
                ConstraintWidget constraintWidget3 = next2.mOwner;
                int i4 = i + 1;
                boolean zCanMeasure2 = canMeasure(i4, constraintWidget3);
                if (constraintWidget3.isMeasureRequested() && zCanMeasure2) {
                    ConstraintWidgetContainer.measure(i4, constraintWidget3, measurer, new BasicMeasure.Measure(), BasicMeasure.Measure.SELF_DIMENSIONS);
                }
                boolean z2 = (next2 == constraintWidget3.mTop && constraintWidget3.mBottom.mTarget != null && constraintWidget3.mBottom.mTarget.hasFinalValue()) || (next2 == constraintWidget3.mBottom && constraintWidget3.mTop.mTarget != null && constraintWidget3.mTop.mTarget.hasFinalValue());
                if (constraintWidget3.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || zCanMeasure2) {
                    if (!constraintWidget3.isMeasureRequested()) {
                        if (next2 == constraintWidget3.mTop && constraintWidget3.mBottom.mTarget == null) {
                            int margin3 = constraintWidget3.mTop.getMargin() + finalValue2;
                            constraintWidget3.setFinalVertical(margin3, constraintWidget3.getHeight() + margin3);
                            verticalSolvingPass(i4, constraintWidget3, measurer);
                        } else if (next2 == constraintWidget3.mBottom && constraintWidget3.mTop.mTarget == null) {
                            int margin4 = finalValue2 - constraintWidget3.mBottom.getMargin();
                            constraintWidget3.setFinalVertical(margin4 - constraintWidget3.getHeight(), margin4);
                            verticalSolvingPass(i4, constraintWidget3, measurer);
                        } else if (z2 && !constraintWidget3.isInVerticalChain()) {
                            solveVerticalCenterConstraints(i4, measurer, constraintWidget3);
                        }
                    }
                } else if (constraintWidget3.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget3.mMatchConstraintMaxHeight >= 0 && constraintWidget3.mMatchConstraintMinHeight >= 0 && (constraintWidget3.getVisibility() == 8 || (constraintWidget3.mMatchConstraintDefaultHeight == 0 && constraintWidget3.getDimensionRatio() == 0.0f))) {
                    if (!constraintWidget3.isInVerticalChain() && !constraintWidget3.isInVirtualLayout() && z2 && !constraintWidget3.isInVerticalChain()) {
                        solveVerticalMatchConstraint(i4, constraintWidget, measurer, constraintWidget3);
                    }
                }
            }
        }
        ConstraintAnchor anchor3 = constraintWidget.getAnchor(ConstraintAnchor.Type.BASELINE);
        if (anchor3.getDependents() != null && anchor3.hasFinalValue()) {
            int finalValue3 = anchor3.getFinalValue();
            for (ConstraintAnchor constraintAnchor : anchor3.getDependents()) {
                ConstraintWidget constraintWidget4 = constraintAnchor.mOwner;
                int i5 = i + 1;
                boolean zCanMeasure3 = canMeasure(i5, constraintWidget4);
                if (constraintWidget4.isMeasureRequested() && zCanMeasure3) {
                    ConstraintWidgetContainer.measure(i5, constraintWidget4, measurer, new BasicMeasure.Measure(), BasicMeasure.Measure.SELF_DIMENSIONS);
                }
                if (constraintWidget4.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || zCanMeasure3) {
                    if (!constraintWidget4.isMeasureRequested() && constraintAnchor == constraintWidget4.mBaseline) {
                        constraintWidget4.setFinalBaseline(constraintAnchor.getMargin() + finalValue3);
                        verticalSolvingPass(i5, constraintWidget4, measurer);
                    }
                }
            }
        }
        constraintWidget.markVerticalSolvingPassDone();
    }

    private static void solveHorizontalCenterConstraints(int i, BasicMeasure.Measurer measurer, ConstraintWidget constraintWidget, boolean z) {
        float horizontalBiasPercent = constraintWidget.getHorizontalBiasPercent();
        int finalValue = constraintWidget.mLeft.mTarget.getFinalValue();
        int finalValue2 = constraintWidget.mRight.mTarget.getFinalValue();
        int margin = constraintWidget.mLeft.getMargin() + finalValue;
        int margin2 = finalValue2 - constraintWidget.mRight.getMargin();
        if (finalValue == finalValue2) {
            horizontalBiasPercent = 0.5f;
        } else {
            finalValue = margin;
            finalValue2 = margin2;
        }
        int width = constraintWidget.getWidth();
        int i2 = (finalValue2 - finalValue) - width;
        if (finalValue > finalValue2) {
            i2 = (finalValue - finalValue2) - width;
        }
        int i3 = ((int) (i2 > 0 ? (horizontalBiasPercent * i2) + 0.5f : horizontalBiasPercent * i2)) + finalValue;
        int i4 = i3 + width;
        if (finalValue > finalValue2) {
            i4 = i3 - width;
        }
        constraintWidget.setFinalHorizontal(i3, i4);
        horizontalSolvingPass(i + 1, constraintWidget, measurer, z);
    }

    private static void solveVerticalCenterConstraints(int i, BasicMeasure.Measurer measurer, ConstraintWidget constraintWidget) {
        float verticalBiasPercent = constraintWidget.getVerticalBiasPercent();
        int finalValue = constraintWidget.mTop.mTarget.getFinalValue();
        int finalValue2 = constraintWidget.mBottom.mTarget.getFinalValue();
        int margin = constraintWidget.mTop.getMargin() + finalValue;
        int margin2 = finalValue2 - constraintWidget.mBottom.getMargin();
        if (finalValue == finalValue2) {
            verticalBiasPercent = 0.5f;
        } else {
            finalValue = margin;
            finalValue2 = margin2;
        }
        int height = constraintWidget.getHeight();
        int i2 = (finalValue2 - finalValue) - height;
        if (finalValue > finalValue2) {
            i2 = (finalValue - finalValue2) - height;
        }
        int i3 = (int) (i2 > 0 ? (verticalBiasPercent * i2) + 0.5f : verticalBiasPercent * i2);
        int i4 = finalValue + i3;
        int i5 = i4 + height;
        if (finalValue > finalValue2) {
            i4 = finalValue - i3;
            i5 = i4 - height;
        }
        constraintWidget.setFinalVertical(i4, i5);
        verticalSolvingPass(i + 1, constraintWidget, measurer);
    }

    private static void solveHorizontalMatchConstraint(int i, ConstraintWidget constraintWidget, BasicMeasure.Measurer measurer, ConstraintWidget constraintWidget2, boolean z) {
        int width;
        float horizontalBiasPercent = constraintWidget2.getHorizontalBiasPercent();
        int finalValue = constraintWidget2.mLeft.mTarget.getFinalValue() + constraintWidget2.mLeft.getMargin();
        int finalValue2 = constraintWidget2.mRight.mTarget.getFinalValue() - constraintWidget2.mRight.getMargin();
        if (finalValue2 >= finalValue) {
            int width2 = constraintWidget2.getWidth();
            if (constraintWidget2.getVisibility() != 8) {
                if (constraintWidget2.mMatchConstraintDefaultWidth == 2) {
                    if (constraintWidget instanceof ConstraintWidgetContainer) {
                        width = constraintWidget.getWidth();
                    } else {
                        width = constraintWidget.getParent().getWidth();
                    }
                    width2 = (int) (constraintWidget2.getHorizontalBiasPercent() * 0.5f * width);
                } else if (constraintWidget2.mMatchConstraintDefaultWidth == 0) {
                    width2 = finalValue2 - finalValue;
                }
                width2 = Math.max(constraintWidget2.mMatchConstraintMinWidth, width2);
                if (constraintWidget2.mMatchConstraintMaxWidth > 0) {
                    width2 = Math.min(constraintWidget2.mMatchConstraintMaxWidth, width2);
                }
            }
            int i2 = finalValue + ((int) ((horizontalBiasPercent * ((finalValue2 - finalValue) - width2)) + 0.5f));
            constraintWidget2.setFinalHorizontal(i2, width2 + i2);
            horizontalSolvingPass(i + 1, constraintWidget2, measurer, z);
        }
    }

    private static void solveVerticalMatchConstraint(int i, ConstraintWidget constraintWidget, BasicMeasure.Measurer measurer, ConstraintWidget constraintWidget2) {
        int height;
        float verticalBiasPercent = constraintWidget2.getVerticalBiasPercent();
        int finalValue = constraintWidget2.mTop.mTarget.getFinalValue() + constraintWidget2.mTop.getMargin();
        int finalValue2 = constraintWidget2.mBottom.mTarget.getFinalValue() - constraintWidget2.mBottom.getMargin();
        if (finalValue2 >= finalValue) {
            int height2 = constraintWidget2.getHeight();
            if (constraintWidget2.getVisibility() != 8) {
                if (constraintWidget2.mMatchConstraintDefaultHeight == 2) {
                    if (constraintWidget instanceof ConstraintWidgetContainer) {
                        height = constraintWidget.getHeight();
                    } else {
                        height = constraintWidget.getParent().getHeight();
                    }
                    height2 = (int) (verticalBiasPercent * 0.5f * height);
                } else if (constraintWidget2.mMatchConstraintDefaultHeight == 0) {
                    height2 = finalValue2 - finalValue;
                }
                height2 = Math.max(constraintWidget2.mMatchConstraintMinHeight, height2);
                if (constraintWidget2.mMatchConstraintMaxHeight > 0) {
                    height2 = Math.min(constraintWidget2.mMatchConstraintMaxHeight, height2);
                }
            }
            int i2 = finalValue + ((int) ((verticalBiasPercent * ((finalValue2 - finalValue) - height2)) + 0.5f));
            constraintWidget2.setFinalVertical(i2, height2 + i2);
            verticalSolvingPass(i + 1, constraintWidget2, measurer);
        }
    }

    private static boolean canMeasure(int i, ConstraintWidget constraintWidget) {
        ConstraintWidget.DimensionBehaviour horizontalDimensionBehaviour = constraintWidget.getHorizontalDimensionBehaviour();
        ConstraintWidget.DimensionBehaviour verticalDimensionBehaviour = constraintWidget.getVerticalDimensionBehaviour();
        ConstraintWidgetContainer constraintWidgetContainer = constraintWidget.getParent() != null ? (ConstraintWidgetContainer) constraintWidget.getParent() : null;
        if (constraintWidgetContainer != null) {
            constraintWidgetContainer.getHorizontalDimensionBehaviour();
            ConstraintWidget.DimensionBehaviour dimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        }
        if (constraintWidgetContainer != null) {
            constraintWidgetContainer.getVerticalDimensionBehaviour();
            ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = ConstraintWidget.DimensionBehaviour.FIXED;
        }
        boolean z = horizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.FIXED || constraintWidget.isResolvedHorizontally() || horizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || (horizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mMatchConstraintDefaultWidth == 0 && constraintWidget.mDimensionRatio == 0.0f && constraintWidget.hasDanglingDimension(0)) || (horizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mMatchConstraintDefaultWidth == 1 && constraintWidget.hasResolvedTargets(0, constraintWidget.getWidth()));
        boolean z2 = verticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.FIXED || constraintWidget.isResolvedVertically() || verticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || (verticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mMatchConstraintDefaultHeight == 0 && constraintWidget.mDimensionRatio == 0.0f && constraintWidget.hasDanglingDimension(1)) || (verticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mMatchConstraintDefaultHeight == 1 && constraintWidget.hasResolvedTargets(1, constraintWidget.getHeight()));
        if (constraintWidget.mDimensionRatio <= 0.0f || !(z || z2)) {
            return z && z2;
        }
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:103:0x01ce  */
    /* JADX WARN: Code duplicated, block: B:107:0x01e2  */
    /* JADX WARN: Code duplicated, block: B:109:0x01e6  */
    /* JADX WARN: Code duplicated, block: B:110:0x01e8  */
    /* JADX WARN: Code duplicated, block: B:112:0x01ec A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:113:0x01ee A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:114:0x01f0 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:115:0x01f2  */
    /* JADX WARN: Code duplicated, block: B:116:0x0213  */
    /* JADX WARN: Code duplicated, block: B:118:0x0234 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:120:0x0236 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Code duplicated, block: B:78:0x0129 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:79:0x012b  */
    /* JADX WARN: Code duplicated, block: B:80:0x0130  */
    /* JADX WARN: Code duplicated, block: B:83:0x013e  */
    /* JADX WARN: Code duplicated, block: B:84:0x0147  */
    /* JADX WARN: Code duplicated, block: B:87:0x0158 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:88:0x015a  */
    /* JADX WARN: Code duplicated, block: B:90:0x0160  */
    /* JADX WARN: Code duplicated, block: B:92:0x0168 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:93:0x016a  */
    /* JADX WARN: Code duplicated, block: B:94:0x0175  */
    /* JADX WARN: Code duplicated, block: B:96:0x0183  */
    /* JADX WARN: Code duplicated, block: B:98:0x018e  */
    /* JADX WARN: Code duplicated, block: B:99:0x01a3  */
    /* JADX WARN: Type inference failed for: r2v24 */
    /* JADX WARN: Type inference failed for: r2v46 */
    /* JADX WARN: Type inference failed for: r2v7, types: [boolean, int] */
    public static boolean solveChain(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, int i2, ChainHead chainHead, boolean z, boolean z2, boolean z3) {
        int finalValue;
        int finalValue2;
        int finalValue3;
        int i3;
        int margin;
        ConstraintWidget constraintWidget;
        boolean z4;
        ?? r2;
        int margin2;
        int height;
        ConstraintAnchor constraintAnchor;
        ConstraintWidget constraintWidget2;
        float verticalBiasPercent;
        int i4;
        int height2;
        if (z3) {
            return false;
        }
        if (i == 0) {
            if (!constraintWidgetContainer.isResolvedHorizontally()) {
                return false;
            }
        } else if (!constraintWidgetContainer.isResolvedVertically()) {
            return false;
        }
        boolean zIsRtl = constraintWidgetContainer.isRtl();
        ConstraintWidget first = chainHead.getFirst();
        ConstraintWidget last = chainHead.getLast();
        ConstraintWidget firstVisibleWidget = chainHead.getFirstVisibleWidget();
        ConstraintWidget lastVisibleWidget = chainHead.getLastVisibleWidget();
        ConstraintWidget head = chainHead.getHead();
        ConstraintAnchor constraintAnchor2 = first.mListAnchors[i2];
        int i5 = i2 + 1;
        ConstraintAnchor constraintAnchor3 = last.mListAnchors[i5];
        if (constraintAnchor2.mTarget == null || constraintAnchor3.mTarget == null || !constraintAnchor2.mTarget.hasFinalValue() || !constraintAnchor3.mTarget.hasFinalValue() || firstVisibleWidget == null || lastVisibleWidget == null || (finalValue3 = (finalValue2 = constraintAnchor3.mTarget.getFinalValue() - lastVisibleWidget.mListAnchors[i5].getMargin()) - (finalValue = constraintAnchor2.mTarget.getFinalValue() + firstVisibleWidget.mListAnchors[i2].getMargin())) <= 0) {
            return false;
        }
        BasicMeasure.Measure measure2 = new BasicMeasure.Measure();
        boolean z5 = false;
        int i6 = 0;
        int i7 = 0;
        int margin3 = 0;
        ConstraintWidget constraintWidget3 = first;
        while (true) {
            ConstraintWidget constraintWidget4 = null;
            if (z5) {
                ConstraintWidget constraintWidget5 = first;
                if (i6 == 0 || i6 != i7 || finalValue3 < margin3) {
                    return false;
                }
                int i8 = finalValue3 - margin3;
                if (!z) {
                    if (z2 && i6 > 2) {
                        i3 = 1;
                        i8 = (i8 / i6) - 1;
                    }
                    if (i6 == i3) {
                        if (i == 0) {
                            verticalBiasPercent = head.getHorizontalBiasPercent();
                        } else {
                            verticalBiasPercent = head.getVerticalBiasPercent();
                        }
                        i4 = (int) (finalValue + 0.5f + (i8 * verticalBiasPercent));
                        if (i == 0) {
                            firstVisibleWidget.setFinalHorizontal(i4, firstVisibleWidget.getWidth() + i4);
                        } else {
                            firstVisibleWidget.setFinalVertical(i4, firstVisibleWidget.getHeight() + i4);
                        }
                        horizontalSolvingPass(1, firstVisibleWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                        return true;
                    }
                    if (z) {
                        if (z2) {
                            return true;
                        }
                        if (i6 == 2) {
                            return false;
                        }
                        if (i == 0) {
                            firstVisibleWidget.setFinalHorizontal(finalValue, firstVisibleWidget.getWidth() + finalValue);
                            lastVisibleWidget.setFinalHorizontal(finalValue2 - lastVisibleWidget.getWidth(), finalValue2);
                            horizontalSolvingPass(1, firstVisibleWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                            horizontalSolvingPass(1, lastVisibleWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                            return true;
                        }
                        firstVisibleWidget.setFinalVertical(finalValue, firstVisibleWidget.getHeight() + finalValue);
                        lastVisibleWidget.setFinalVertical(finalValue2 - lastVisibleWidget.getHeight(), finalValue2);
                        verticalSolvingPass(1, firstVisibleWidget, constraintWidgetContainer.getMeasurer());
                        verticalSolvingPass(1, lastVisibleWidget, constraintWidgetContainer.getMeasurer());
                        return true;
                    }
                    margin = finalValue + i8;
                    constraintWidget = constraintWidget5;
                    z4 = false;
                    while (!z4) {
                        if (constraintWidget.getVisibility() == 8) {
                            margin2 = margin + constraintWidget.mListAnchors[i2].getMargin();
                            if (i == 0) {
                                r2 = i3;
                                constraintWidget.setFinalHorizontal(margin2, constraintWidget.getWidth() + margin2);
                                horizontalSolvingPass(1, constraintWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                                height = constraintWidget.getWidth();
                            } else {
                                r2 = i3;
                                constraintWidget.setFinalVertical(margin2, constraintWidget.getHeight() + margin2);
                                verticalSolvingPass(1, constraintWidget, constraintWidgetContainer.getMeasurer());
                                height = constraintWidget.getHeight();
                            }
                            margin = margin2 + height + constraintWidget.mListAnchors[i5].getMargin() + i8;
                        } else if (i == 0) {
                            constraintWidget.setFinalHorizontal(margin, margin);
                            horizontalSolvingPass(r2, constraintWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                        } else {
                            constraintWidget.setFinalVertical(margin, margin);
                            verticalSolvingPass(r2, constraintWidget, constraintWidgetContainer.getMeasurer());
                        }
                        constraintWidget.addToSolver(linearSystem, false);
                        constraintAnchor = constraintWidget.mListAnchors[i5].mTarget;
                        if (constraintAnchor != null) {
                            constraintWidget2 = constraintAnchor.mOwner;
                            if (constraintWidget2.mListAnchors[i2].mTarget != null || constraintWidget2.mListAnchors[i2].mTarget.mOwner != constraintWidget) {
                                constraintWidget2 = null;
                            }
                        } else {
                            constraintWidget2 = null;
                        }
                        if (constraintWidget2 != null) {
                            constraintWidget = constraintWidget2;
                        } else {
                            z4 = true;
                        }
                        r2 = 1;
                    }
                    r2 = i3;
                    return r2;
                }
                i8 /= i6 + 1;
                i3 = 1;
                if (i6 == i3) {
                    if (i == 0) {
                        verticalBiasPercent = head.getHorizontalBiasPercent();
                    } else {
                        verticalBiasPercent = head.getVerticalBiasPercent();
                    }
                    i4 = (int) (finalValue + 0.5f + (i8 * verticalBiasPercent));
                    if (i == 0) {
                        firstVisibleWidget.setFinalHorizontal(i4, firstVisibleWidget.getWidth() + i4);
                    } else {
                        firstVisibleWidget.setFinalVertical(i4, firstVisibleWidget.getHeight() + i4);
                    }
                    horizontalSolvingPass(1, firstVisibleWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                    return true;
                }
                if (z) {
                    if (z2) {
                        return true;
                    }
                    if (i6 == 2) {
                        return false;
                    }
                    if (i == 0) {
                        firstVisibleWidget.setFinalHorizontal(finalValue, firstVisibleWidget.getWidth() + finalValue);
                        lastVisibleWidget.setFinalHorizontal(finalValue2 - lastVisibleWidget.getWidth(), finalValue2);
                        horizontalSolvingPass(1, firstVisibleWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                        horizontalSolvingPass(1, lastVisibleWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                        return true;
                    }
                    firstVisibleWidget.setFinalVertical(finalValue, firstVisibleWidget.getHeight() + finalValue);
                    lastVisibleWidget.setFinalVertical(finalValue2 - lastVisibleWidget.getHeight(), finalValue2);
                    verticalSolvingPass(1, firstVisibleWidget, constraintWidgetContainer.getMeasurer());
                    verticalSolvingPass(1, lastVisibleWidget, constraintWidgetContainer.getMeasurer());
                    return true;
                }
                margin = finalValue + i8;
                constraintWidget = constraintWidget5;
                z4 = false;
                while (!z4) {
                    if (constraintWidget.getVisibility() == 8) {
                        margin2 = margin + constraintWidget.mListAnchors[i2].getMargin();
                        if (i == 0) {
                            r2 = i3;
                            constraintWidget.setFinalHorizontal(margin2, constraintWidget.getWidth() + margin2);
                            horizontalSolvingPass(1, constraintWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                            height = constraintWidget.getWidth();
                        } else {
                            r2 = i3;
                            constraintWidget.setFinalVertical(margin2, constraintWidget.getHeight() + margin2);
                            verticalSolvingPass(1, constraintWidget, constraintWidgetContainer.getMeasurer());
                            height = constraintWidget.getHeight();
                        }
                        margin = margin2 + height + constraintWidget.mListAnchors[i5].getMargin() + i8;
                    } else if (i == 0) {
                        constraintWidget.setFinalHorizontal(margin, margin);
                        horizontalSolvingPass(r2, constraintWidget, constraintWidgetContainer.getMeasurer(), zIsRtl);
                    } else {
                        constraintWidget.setFinalVertical(margin, margin);
                        verticalSolvingPass(r2, constraintWidget, constraintWidgetContainer.getMeasurer());
                    }
                    constraintWidget.addToSolver(linearSystem, false);
                    constraintAnchor = constraintWidget.mListAnchors[i5].mTarget;
                    if (constraintAnchor != null) {
                        constraintWidget2 = constraintAnchor.mOwner;
                        if (constraintWidget2.mListAnchors[i2].mTarget != null) {
                            constraintWidget2 = null;
                        } else {
                            constraintWidget2 = null;
                        }
                    } else {
                        constraintWidget2 = null;
                    }
                    if (constraintWidget2 != null) {
                        constraintWidget = constraintWidget2;
                    } else {
                        z4 = true;
                    }
                    r2 = 1;
                }
                r2 = i3;
                return r2;
            }
            if (!canMeasure(1, constraintWidget3)) {
                return false;
            }
            ConstraintWidget constraintWidget6 = first;
            if (constraintWidget3.mListDimensionBehaviors[i] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                return false;
            }
            if (constraintWidget3.isMeasureRequested()) {
                ConstraintWidgetContainer.measure(1, constraintWidget3, constraintWidgetContainer.getMeasurer(), measure2, BasicMeasure.Measure.SELF_DIMENSIONS);
            }
            int margin4 = margin3 + constraintWidget3.mListAnchors[i2].getMargin();
            if (i == 0) {
                height2 = constraintWidget3.getWidth();
            } else {
                height2 = constraintWidget3.getHeight();
            }
            margin3 = margin4 + height2 + constraintWidget3.mListAnchors[i5].getMargin();
            i7++;
            if (constraintWidget3.getVisibility() != 8) {
                i6++;
            }
            ConstraintAnchor constraintAnchor4 = constraintWidget3.mListAnchors[i5].mTarget;
            if (constraintAnchor4 != null) {
                ConstraintWidget constraintWidget7 = constraintAnchor4.mOwner;
                if (constraintWidget7.mListAnchors[i2].mTarget != null && constraintWidget7.mListAnchors[i2].mTarget.mOwner == constraintWidget3) {
                    constraintWidget4 = constraintWidget7;
                }
            }
            if (constraintWidget4 != null) {
                constraintWidget3 = constraintWidget4;
                z5 = z5;
            } else {
                z5 = true;
            }
            first = constraintWidget6;
        }
    }
}
