package androidx.constraintlayout.core.widgets;

import androidx.constraintlayout.core.ArrayRow;
import androidx.constraintlayout.core.LinearSystem;
import androidx.constraintlayout.core.SolverVariable;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Chain {
    private static final boolean DEBUG = false;
    public static final boolean USE_CHAIN_OPTIMIZATION = false;

    public static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ArrayList<ConstraintWidget> arrayList, int i) {
        int i2;
        ChainHead[] chainHeadArr;
        int i3;
        if (i == 0) {
            i2 = constraintWidgetContainer.mHorizontalChainsSize;
            chainHeadArr = constraintWidgetContainer.mHorizontalChainsArray;
            i3 = 0;
        } else {
            i2 = constraintWidgetContainer.mVerticalChainsSize;
            chainHeadArr = constraintWidgetContainer.mVerticalChainsArray;
            i3 = 2;
        }
        for (int i4 = 0; i4 < i2; i4++) {
            ChainHead chainHead = chainHeadArr[i4];
            chainHead.define();
            if (arrayList == null || (arrayList != null && arrayList.contains(chainHead.mFirst))) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i3, chainHead);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:108:0x01b4  */
    /* JADX WARN: Code duplicated, block: B:203:0x0383  */
    /* JADX WARN: Code duplicated, block: B:223:0x03dc  */
    /* JADX WARN: Code duplicated, block: B:29:0x004c A[PHI: r8 r15
  0x004c: PHI (r8v3 boolean) = (r8v1 boolean), (r8v43 boolean) binds: [B:28:0x004a, B:17:0x0037] A[DONT_GENERATE, DONT_INLINE]
  0x004c: PHI (r15v3 boolean) = (r15v1 boolean), (r15v32 boolean) binds: [B:28:0x004a, B:17:0x0037] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:30:0x004e A[PHI: r8 r15
  0x004e: PHI (r8v40 boolean) = (r8v1 boolean), (r8v43 boolean) binds: [B:28:0x004a, B:17:0x0037] A[DONT_GENERATE, DONT_INLINE]
  0x004e: PHI (r15v29 boolean) = (r15v1 boolean), (r15v32 boolean) binds: [B:28:0x004a, B:17:0x0037] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:325:0x03de A[SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r39v0, types: [androidx.constraintlayout.core.LinearSystem] */
    /* JADX WARN: Type inference failed for: r5v25 */
    /* JADX WARN: Type inference failed for: r5v26, types: [androidx.constraintlayout.core.SolverVariable] */
    /* JADX WARN: Type inference failed for: r5v28 */
    /* JADX WARN: Type inference failed for: r8v38 */
    /* JADX WARN: Type inference failed for: r8v39 */
    /* JADX WARN: Type inference failed for: r8v45 */
    /* JADX WARN: Type inference failed for: r8v5 */
    /* JADX WARN: Type inference failed for: r8v6, types: [androidx.constraintlayout.core.widgets.ConstraintWidget] */
    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, int i2, ChainHead chainHead) {
        boolean z;
        boolean z2;
        boolean z3;
        int i3;
        ConstraintAnchor constraintAnchor;
        SolverVariable solverVariable;
        SolverVariable solverVariable2;
        ConstraintAnchor constraintAnchor2;
        SolverVariable solverVariable3;
        ?? r5;
        float f;
        int size;
        int i4;
        int i5 = i;
        ConstraintWidget constraintWidget = chainHead.mFirst;
        ConstraintWidget constraintWidget2 = chainHead.mLast;
        ConstraintWidget constraintWidget3 = chainHead.mFirstVisibleWidget;
        ConstraintWidget constraintWidget4 = chainHead.mLastVisibleWidget;
        ConstraintWidget constraintWidget5 = chainHead.mHead;
        float f2 = chainHead.mTotalWeight;
        ConstraintWidget constraintWidget6 = chainHead.mFirstMatchConstraintWidget;
        ConstraintWidget constraintWidget7 = chainHead.mLastMatchConstraintWidget;
        boolean z4 = constraintWidgetContainer.mListDimensionBehaviors[i5] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (i5 == 0) {
            z = constraintWidget5.mHorizontalChainStyle == 0;
            z2 = constraintWidget5.mHorizontalChainStyle == 1;
            if (constraintWidget5.mHorizontalChainStyle == 2) {
                z3 = true;
            } else {
                z3 = false;
            }
        } else {
            z = constraintWidget5.mVerticalChainStyle == 0;
            z2 = constraintWidget5.mVerticalChainStyle == 1;
            if (constraintWidget5.mVerticalChainStyle == 2) {
                z3 = true;
            } else {
                z3 = false;
            }
        }
        boolean z5 = z2;
        boolean z6 = false;
        boolean z7 = z;
        ?? r8 = constraintWidget;
        while (true) {
            Object obj = null;
            if (z6) {
                break;
            }
            ConstraintAnchor constraintAnchor3 = r8.mListAnchors[i2];
            int i6 = z3 ? 1 : 4;
            int margin = constraintAnchor3.getMargin();
            float f3 = f2;
            boolean z8 = z6;
            boolean z9 = r8.mListDimensionBehaviors[i5] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && r8.mResolvedMatchConstraintDefault[i5] == 0;
            if (constraintAnchor3.mTarget != null && r8 != constraintWidget) {
                margin += constraintAnchor3.mTarget.getMargin();
            }
            int i7 = margin;
            if (z3 && r8 != constraintWidget && r8 != constraintWidget3) {
                i6 = 8;
            }
            if (constraintAnchor3.mTarget != null) {
                if (r8 == constraintWidget3) {
                    linearSystem.addGreaterThan(constraintAnchor3.mSolverVariable, constraintAnchor3.mTarget.mSolverVariable, i7, 6);
                } else {
                    linearSystem.addGreaterThan(constraintAnchor3.mSolverVariable, constraintAnchor3.mTarget.mSolverVariable, i7, 8);
                }
                if (z9 && !z3) {
                    i6 = 5;
                }
                linearSystem.addEquality(constraintAnchor3.mSolverVariable, constraintAnchor3.mTarget.mSolverVariable, i7, (r8 == constraintWidget3 && z3 && r8.isInBarrier(i5)) ? 5 : i6);
            } else {
                constraintWidget5 = constraintWidget5;
                constraintWidget = constraintWidget;
            }
            if (z4) {
                if (r8.getVisibility() == 8 || r8.mListDimensionBehaviors[i5] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    i4 = 0;
                } else {
                    i4 = 0;
                    linearSystem.addGreaterThan(r8.mListAnchors[i2 + 1].mSolverVariable, r8.mListAnchors[i2].mSolverVariable, 0, 5);
                }
                linearSystem.addGreaterThan(r8.mListAnchors[i2].mSolverVariable, constraintWidgetContainer.mListAnchors[i2].mSolverVariable, i4, 8);
            }
            ConstraintAnchor constraintAnchor4 = r8.mListAnchors[i2 + 1].mTarget;
            if (constraintAnchor4 != null) {
                ConstraintWidget constraintWidget8 = constraintAnchor4.mOwner;
                if (constraintWidget8.mListAnchors[i2].mTarget != null && constraintWidget8.mListAnchors[i2].mTarget.mOwner == r8) {
                    obj = constraintWidget8;
                }
            }
            if (obj != null) {
                r8 = obj;
                z6 = z8;
            } else {
                z6 = true;
            }
            z7 = z7;
            f2 = f3;
            constraintWidget5 = constraintWidget5;
            constraintWidget = constraintWidget;
            r8 = r8;
        }
        ConstraintWidget constraintWidget9 = constraintWidget5;
        float f4 = f2;
        ConstraintWidget constraintWidget10 = constraintWidget;
        boolean z10 = z7;
        if (constraintWidget4 != null) {
            int i8 = i2 + 1;
            if (constraintWidget2.mListAnchors[i8].mTarget != null) {
                ConstraintAnchor constraintAnchor5 = constraintWidget4.mListAnchors[i8];
                if (constraintWidget4.mListDimensionBehaviors[i5] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget4.mResolvedMatchConstraintDefault[i5] == 0 && !z3 && constraintAnchor5.mTarget.mOwner == constraintWidgetContainer) {
                    linearSystem.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor5.mTarget.mSolverVariable, -constraintAnchor5.getMargin(), 5);
                } else if (z3 && constraintAnchor5.mTarget.mOwner == constraintWidgetContainer) {
                    linearSystem.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor5.mTarget.mSolverVariable, -constraintAnchor5.getMargin(), 4);
                }
                linearSystem.addLowerThan(constraintAnchor5.mSolverVariable, constraintWidget2.mListAnchors[i8].mTarget.mSolverVariable, -constraintAnchor5.getMargin(), 6);
            }
        }
        if (z4) {
            int i9 = i2 + 1;
            linearSystem.addGreaterThan(constraintWidgetContainer.mListAnchors[i9].mSolverVariable, constraintWidget2.mListAnchors[i9].mSolverVariable, constraintWidget2.mListAnchors[i9].getMargin(), 8);
        }
        ArrayList<ConstraintWidget> arrayList = chainHead.mWeightedMatchConstraintsWidgets;
        if (arrayList != null && (size = arrayList.size()) > 1) {
            float f5 = (!chainHead.mHasUndefinedWeights || chainHead.mHasComplexMatchWeights) ? f4 : chainHead.mWidgetsMatchCount;
            float f6 = 0.0f;
            float f7 = 0.0f;
            ConstraintWidget constraintWidget11 = null;
            int i10 = 0;
            while (i10 < size) {
                ConstraintWidget constraintWidget12 = arrayList.get(i10);
                float f8 = constraintWidget12.mWeight[i5];
                if (f8 < f6) {
                    if (chainHead.mHasComplexMatchWeights) {
                        linearSystem.addEquality(constraintWidget12.mListAnchors[i2 + 1].mSolverVariable, constraintWidget12.mListAnchors[i2].mSolverVariable, 0, 4);
                    } else {
                        f8 = 1.0f;
                        f6 = 0.0f;
                    }
                    arrayList = arrayList;
                    size = size;
                    i10++;
                    size = size;
                    arrayList = arrayList;
                    f6 = 0.0f;
                }
                if (f8 == f6) {
                    linearSystem.addEquality(constraintWidget12.mListAnchors[i2 + 1].mSolverVariable, constraintWidget12.mListAnchors[i2].mSolverVariable, 0, 8);
                    arrayList = arrayList;
                    size = size;
                } else {
                    if (constraintWidget11 != null) {
                        SolverVariable solverVariable4 = constraintWidget11.mListAnchors[i2].mSolverVariable;
                        int i11 = i2 + 1;
                        SolverVariable solverVariable5 = constraintWidget11.mListAnchors[i11].mSolverVariable;
                        SolverVariable solverVariable6 = constraintWidget12.mListAnchors[i2].mSolverVariable;
                        SolverVariable solverVariable7 = constraintWidget12.mListAnchors[i11].mSolverVariable;
                        ArrayRow arrayRowCreateRow = linearSystem.createRow();
                        arrayRowCreateRow.createRowEqualMatchDimensions(f7, f5, f8, solverVariable4, solverVariable5, solverVariable6, solverVariable7);
                        linearSystem.addConstraint(arrayRowCreateRow);
                    }
                    f7 = f8;
                    constraintWidget11 = constraintWidget12;
                }
                i10++;
                size = size;
                arrayList = arrayList;
                f6 = 0.0f;
            }
        }
        if (constraintWidget3 != null && (constraintWidget3 == constraintWidget4 || z3)) {
            ConstraintAnchor constraintAnchor6 = constraintWidget10.mListAnchors[i2];
            int i12 = i2 + 1;
            ConstraintAnchor constraintAnchor7 = constraintWidget2.mListAnchors[i12];
            SolverVariable solverVariable8 = constraintAnchor6.mTarget != null ? constraintAnchor6.mTarget.mSolverVariable : null;
            SolverVariable solverVariable9 = constraintAnchor7.mTarget != null ? constraintAnchor7.mTarget.mSolverVariable : null;
            ConstraintAnchor constraintAnchor8 = constraintWidget3.mListAnchors[i2];
            if (constraintWidget4 != null) {
                constraintAnchor7 = constraintWidget4.mListAnchors[i12];
            }
            if (solverVariable8 != null && solverVariable9 != null) {
                if (i5 == 0) {
                    f = constraintWidget9.mHorizontalBiasPercent;
                } else {
                    f = constraintWidget9.mVerticalBiasPercent;
                }
                linearSystem.addCentering(constraintAnchor8.mSolverVariable, solverVariable8, constraintAnchor8.getMargin(), f, solverVariable9, constraintAnchor7.mSolverVariable, constraintAnchor7.getMargin(), 7);
            }
        } else if (!z10 || constraintWidget3 == null) {
            int i13 = 8;
            if (z5 && constraintWidget3 != null) {
                boolean z11 = chainHead.mWidgetsMatchCount > 0 && chainHead.mWidgetsCount == chainHead.mWidgetsMatchCount;
                ConstraintWidget constraintWidget13 = constraintWidget3;
                ConstraintWidget constraintWidget14 = constraintWidget13;
                while (constraintWidget14 != null) {
                    ConstraintWidget constraintWidget15 = constraintWidget14.mNextChainWidget[i5];
                    while (constraintWidget15 != null && constraintWidget15.getVisibility() == i13) {
                        constraintWidget15 = constraintWidget15.mNextChainWidget[i5];
                    }
                    if (constraintWidget14 == constraintWidget3 || constraintWidget14 == constraintWidget4 || constraintWidget15 == null) {
                        constraintWidget13 = constraintWidget13;
                        i3 = i13;
                    } else {
                        ConstraintWidget constraintWidget16 = constraintWidget15 == constraintWidget4 ? null : constraintWidget15;
                        ConstraintAnchor constraintAnchor9 = constraintWidget14.mListAnchors[i2];
                        SolverVariable solverVariable10 = constraintAnchor9.mSolverVariable;
                        if (constraintAnchor9.mTarget != null) {
                            SolverVariable solverVariable11 = constraintAnchor9.mTarget.mSolverVariable;
                        }
                        int i14 = i2 + 1;
                        SolverVariable solverVariable12 = constraintWidget13.mListAnchors[i14].mSolverVariable;
                        int margin2 = constraintAnchor9.getMargin();
                        int margin3 = constraintWidget14.mListAnchors[i14].getMargin();
                        if (constraintWidget16 != null) {
                            constraintAnchor = constraintWidget16.mListAnchors[i2];
                            SolverVariable solverVariable13 = constraintAnchor.mSolverVariable;
                            solverVariable2 = constraintAnchor.mTarget != null ? constraintAnchor.mTarget.mSolverVariable : null;
                            solverVariable = solverVariable13;
                        } else {
                            constraintAnchor = constraintWidget4.mListAnchors[i2];
                            solverVariable = constraintAnchor != null ? constraintAnchor.mSolverVariable : null;
                            solverVariable2 = constraintWidget14.mListAnchors[i14].mSolverVariable;
                        }
                        if (constraintAnchor != null) {
                            margin3 += constraintAnchor.getMargin();
                        }
                        int i15 = margin3;
                        int margin4 = constraintWidget13.mListAnchors[i14].getMargin() + margin2;
                        int i16 = z11 ? 8 : 4;
                        if (solverVariable10 == null || solverVariable12 == null || solverVariable == null || solverVariable2 == null) {
                            i3 = 8;
                        } else {
                            i3 = 8;
                            linearSystem.addCentering(solverVariable10, solverVariable12, margin4, 0.5f, solverVariable, solverVariable2, i15, i16);
                        }
                        constraintWidget15 = constraintWidget16;
                    }
                    constraintWidget13 = constraintWidget14.getVisibility() != i3 ? constraintWidget14 : constraintWidget13;
                    constraintWidget14 = constraintWidget15;
                    i13 = i3;
                    i5 = i;
                }
                ConstraintAnchor constraintAnchor10 = constraintWidget3.mListAnchors[i2];
                ConstraintAnchor constraintAnchor11 = constraintWidget10.mListAnchors[i2].mTarget;
                int i17 = i2 + 1;
                ConstraintAnchor constraintAnchor12 = constraintWidget4.mListAnchors[i17];
                ConstraintAnchor constraintAnchor13 = constraintWidget2.mListAnchors[i17].mTarget;
                if (constraintAnchor11 != null) {
                    if (constraintWidget3 != constraintWidget4) {
                        linearSystem.addEquality(constraintAnchor10.mSolverVariable, constraintAnchor11.mSolverVariable, constraintAnchor10.getMargin(), 5);
                    } else if (constraintAnchor13 != null) {
                        linearSystem.addCentering(constraintAnchor10.mSolverVariable, constraintAnchor11.mSolverVariable, constraintAnchor10.getMargin(), 0.5f, constraintAnchor12.mSolverVariable, constraintAnchor13.mSolverVariable, constraintAnchor12.getMargin(), 5);
                    }
                }
                if (constraintAnchor13 != null && constraintWidget3 != constraintWidget4) {
                    linearSystem.addEquality(constraintAnchor12.mSolverVariable, constraintAnchor13.mSolverVariable, -constraintAnchor12.getMargin(), 5);
                }
            }
        } else {
            boolean z12 = chainHead.mWidgetsMatchCount > 0 && chainHead.mWidgetsCount == chainHead.mWidgetsMatchCount;
            ConstraintWidget constraintWidget17 = constraintWidget3;
            ConstraintWidget constraintWidget18 = constraintWidget17;
            while (constraintWidget18 != null) {
                ConstraintWidget constraintWidget19 = constraintWidget18.mNextChainWidget[i5];
                while (constraintWidget19 != null && constraintWidget19.getVisibility() == 8) {
                    constraintWidget19 = constraintWidget19.mNextChainWidget[i5];
                }
                if (constraintWidget19 != null || constraintWidget18 == constraintWidget4) {
                    ConstraintAnchor constraintAnchor14 = constraintWidget18.mListAnchors[i2];
                    SolverVariable solverVariable14 = constraintAnchor14.mSolverVariable;
                    SolverVariable solverVariable15 = constraintAnchor14.mTarget != null ? constraintAnchor14.mTarget.mSolverVariable : null;
                    if (constraintWidget17 != constraintWidget18) {
                        solverVariable15 = constraintWidget17.mListAnchors[i2 + 1].mSolverVariable;
                    } else if (constraintWidget18 == constraintWidget3) {
                        solverVariable15 = constraintWidget10.mListAnchors[i2].mTarget != null ? constraintWidget10.mListAnchors[i2].mTarget.mSolverVariable : null;
                    }
                    int margin5 = constraintAnchor14.getMargin();
                    int i18 = i2 + 1;
                    int margin6 = constraintWidget18.mListAnchors[i18].getMargin();
                    if (constraintWidget19 != null) {
                        constraintAnchor2 = constraintWidget19.mListAnchors[i2];
                        solverVariable3 = constraintAnchor2.mSolverVariable;
                    } else {
                        constraintAnchor2 = constraintWidget2.mListAnchors[i18].mTarget;
                        if (constraintAnchor2 != null) {
                            solverVariable3 = constraintAnchor2.mSolverVariable;
                        } else {
                            solverVariable3 = null;
                        }
                        SolverVariable solverVariable16 = constraintWidget18.mListAnchors[i18].mSolverVariable;
                        if (constraintAnchor2 != null) {
                            margin6 += constraintAnchor2.getMargin();
                        }
                        int margin7 = margin5 + constraintWidget17.mListAnchors[i18].getMargin();
                        if (solverVariable14 == null && solverVariable15 != null && solverVariable3 != null && solverVariable16 != null) {
                            if (constraintWidget18 == constraintWidget3) {
                                margin7 = constraintWidget3.mListAnchors[i2].getMargin();
                            }
                            constraintWidget19 = constraintWidget19;
                            linearSystem.addCentering(solverVariable14, solverVariable15, margin7, 0.5f, solverVariable3, solverVariable16, constraintWidget18 == constraintWidget4 ? constraintWidget4.mListAnchors[i18].getMargin() : margin6, z12 ? 8 : 5);
                        }
                        if (constraintWidget18.getVisibility() != 8) {
                            constraintWidget18 = constraintWidget17;
                        }
                        constraintWidget17 = constraintWidget18;
                        constraintWidget18 = constraintWidget19;
                    }
                    SolverVariable solverVariable17 = constraintWidget18.mListAnchors[i18].mSolverVariable;
                    if (constraintAnchor2 != null) {
                        margin6 += constraintAnchor2.getMargin();
                    }
                    int margin8 = margin5 + constraintWidget17.mListAnchors[i18].getMargin();
                    if (solverVariable14 == null) {
                    }
                }
                if (constraintWidget18.getVisibility() != 8) {
                    constraintWidget18 = constraintWidget17;
                }
                constraintWidget17 = constraintWidget18;
                constraintWidget18 = constraintWidget19;
            }
        }
        if ((!z10 && !z5) || constraintWidget3 == null || constraintWidget3 == constraintWidget4) {
            return;
        }
        ConstraintAnchor constraintAnchor15 = constraintWidget3.mListAnchors[i2];
        if (constraintWidget4 == null) {
            constraintWidget4 = constraintWidget3;
        }
        int i19 = i2 + 1;
        ConstraintAnchor constraintAnchor16 = constraintWidget4.mListAnchors[i19];
        SolverVariable solverVariable18 = constraintAnchor15.mTarget != null ? constraintAnchor15.mTarget.mSolverVariable : null;
        SolverVariable solverVariable19 = constraintAnchor16.mTarget != null ? constraintAnchor16.mTarget.mSolverVariable : null;
        if (constraintWidget2 != constraintWidget4) {
            ConstraintAnchor constraintAnchor17 = constraintWidget2.mListAnchors[i19];
            r5 = constraintAnchor17.mTarget != null ? constraintAnchor17.mTarget.mSolverVariable : null;
        } else {
            r5 = solverVariable19;
        }
        if (constraintWidget3 == constraintWidget4) {
            constraintAnchor15 = constraintWidget3.mListAnchors[i2];
            constraintAnchor16 = constraintWidget3.mListAnchors[i19];
        }
        if (solverVariable18 == null || r5 == 0) {
            return;
        }
        linearSystem.addCentering(constraintAnchor15.mSolverVariable, solverVariable18, constraintAnchor15.getMargin(), 0.5f, r5, constraintAnchor16.mSolverVariable, constraintWidget4.mListAnchors[i19].getMargin(), 5);
    }
}
