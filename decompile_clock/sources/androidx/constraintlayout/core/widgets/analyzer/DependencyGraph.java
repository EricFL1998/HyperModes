package androidx.constraintlayout.core.widgets.analyzer;

import androidx.constraintlayout.core.widgets.Barrier;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import androidx.constraintlayout.core.widgets.HelperWidget;
import com.xiaomi.onetrack.util.z;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class DependencyGraph {
    private static final boolean USE_GROUPS = true;
    private ConstraintWidgetContainer container;
    private ConstraintWidgetContainer mContainer;
    private boolean mNeedBuildGraph = true;
    private boolean mNeedRedoMeasures = true;
    private ArrayList<WidgetRun> mRuns = new ArrayList<>();
    private ArrayList<RunGroup> runGroups = new ArrayList<>();
    private BasicMeasure.Measurer mMeasurer = null;
    private BasicMeasure.Measure mMeasure = new BasicMeasure.Measure();
    ArrayList<RunGroup> mGroups = new ArrayList<>();

    public DependencyGraph(ConstraintWidgetContainer constraintWidgetContainer) {
        this.container = constraintWidgetContainer;
        this.mContainer = constraintWidgetContainer;
    }

    public void setMeasurer(BasicMeasure.Measurer measurer) {
        this.mMeasurer = measurer;
    }

    private int computeWrap(ConstraintWidgetContainer constraintWidgetContainer, int i) {
        int size = this.mGroups.size();
        long jMax = 0;
        for (int i2 = 0; i2 < size; i2++) {
            jMax = Math.max(jMax, this.mGroups.get(i2).computeWrapSize(constraintWidgetContainer, i));
        }
        return (int) jMax;
    }

    public void defineTerminalWidgets(ConstraintWidget.DimensionBehaviour dimensionBehaviour, ConstraintWidget.DimensionBehaviour dimensionBehaviour2) {
        if (this.mNeedBuildGraph) {
            buildGraph();
            boolean z = false;
            for (ConstraintWidget constraintWidget : this.container.mChildren) {
                constraintWidget.isTerminalWidget[0] = true;
                constraintWidget.isTerminalWidget[1] = true;
                if (constraintWidget instanceof Barrier) {
                    z = true;
                }
            }
            if (z) {
                return;
            }
            Iterator<RunGroup> it = this.mGroups.iterator();
            while (it.hasNext()) {
                it.next().defineTerminalWidgets(dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
            }
        }
    }

    public boolean directMeasure(boolean z) {
        boolean z2;
        boolean z3 = false;
        if (this.mNeedBuildGraph || this.mNeedRedoMeasures) {
            for (ConstraintWidget constraintWidget : this.container.mChildren) {
                constraintWidget.ensureWidgetRuns();
                constraintWidget.measured = false;
                constraintWidget.horizontalRun.reset();
                constraintWidget.verticalRun.reset();
            }
            this.container.ensureWidgetRuns();
            this.container.measured = false;
            this.container.horizontalRun.reset();
            this.container.verticalRun.reset();
            this.mNeedRedoMeasures = false;
        }
        if (basicMeasureWidgets(this.mContainer)) {
            return false;
        }
        this.container.setX(0);
        this.container.setY(0);
        ConstraintWidget.DimensionBehaviour dimensionBehaviour = this.container.getDimensionBehaviour(0);
        ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = this.container.getDimensionBehaviour(1);
        if (this.mNeedBuildGraph) {
            buildGraph();
        }
        int x = this.container.getX();
        int y = this.container.getY();
        this.container.horizontalRun.start.resolve(x);
        this.container.verticalRun.start.resolve(y);
        measureWidgets();
        if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            if (z) {
                Iterator<WidgetRun> it = this.mRuns.iterator();
                while (it.hasNext()) {
                    if (!it.next().supportsWrapComputation()) {
                        z = false;
                        break;
                    }
                }
            }
            if (z && dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                this.container.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                ConstraintWidgetContainer constraintWidgetContainer = this.container;
                constraintWidgetContainer.setWidth(computeWrap(constraintWidgetContainer, 0));
                this.container.horizontalRun.dimension.resolve(this.container.getWidth());
            }
            if (z && dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                this.container.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                ConstraintWidgetContainer constraintWidgetContainer2 = this.container;
                constraintWidgetContainer2.setHeight(computeWrap(constraintWidgetContainer2, 1));
                this.container.verticalRun.dimension.resolve(this.container.getHeight());
            }
        }
        if (this.container.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.FIXED || this.container.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            int width = this.container.getWidth() + x;
            this.container.horizontalRun.end.resolve(width);
            this.container.horizontalRun.dimension.resolve(width - x);
            measureWidgets();
            if (this.container.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.FIXED || this.container.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                int height = this.container.getHeight() + y;
                this.container.verticalRun.end.resolve(height);
                this.container.verticalRun.dimension.resolve(height - y);
            }
            measureWidgets();
            z2 = true;
        } else {
            z2 = false;
        }
        for (WidgetRun widgetRun : this.mRuns) {
            if (widgetRun.widget != this.container || widgetRun.resolved) {
                widgetRun.applyToWidget();
            }
        }
        for (WidgetRun widgetRun2 : this.mRuns) {
            if (z2 || widgetRun2.widget != this.container) {
                if (!widgetRun2.start.resolved || ((!widgetRun2.end.resolved && !(widgetRun2 instanceof GuidelineReference)) || (!widgetRun2.dimension.resolved && !(widgetRun2 instanceof ChainRun) && !(widgetRun2 instanceof GuidelineReference)))) {
                    this.container.setHorizontalDimensionBehaviour(dimensionBehaviour);
                    this.container.setVerticalDimensionBehaviour(dimensionBehaviour2);
                    return z3;
                }
            }
        }
        z3 = true;
        this.container.setHorizontalDimensionBehaviour(dimensionBehaviour);
        this.container.setVerticalDimensionBehaviour(dimensionBehaviour2);
        return z3;
    }

    public boolean directMeasureSetup(boolean z) {
        if (this.mNeedBuildGraph) {
            for (ConstraintWidget constraintWidget : this.container.mChildren) {
                constraintWidget.ensureWidgetRuns();
                constraintWidget.measured = false;
                constraintWidget.horizontalRun.dimension.resolved = false;
                constraintWidget.horizontalRun.resolved = false;
                constraintWidget.horizontalRun.reset();
                constraintWidget.verticalRun.dimension.resolved = false;
                constraintWidget.verticalRun.resolved = false;
                constraintWidget.verticalRun.reset();
            }
            this.container.ensureWidgetRuns();
            this.container.measured = false;
            this.container.horizontalRun.dimension.resolved = false;
            this.container.horizontalRun.resolved = false;
            this.container.horizontalRun.reset();
            this.container.verticalRun.dimension.resolved = false;
            this.container.verticalRun.resolved = false;
            this.container.verticalRun.reset();
            buildGraph();
        }
        if (basicMeasureWidgets(this.mContainer)) {
            return false;
        }
        this.container.setX(0);
        this.container.setY(0);
        this.container.horizontalRun.start.resolve(0);
        this.container.verticalRun.start.resolve(0);
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:36:0x00d4  */
    public boolean directMeasureWithOrientation(boolean z, int i) {
        boolean z2;
        boolean z3 = false;
        ConstraintWidget.DimensionBehaviour dimensionBehaviour = this.container.getDimensionBehaviour(0);
        ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = this.container.getDimensionBehaviour(1);
        int x = this.container.getX();
        int y = this.container.getY();
        if (z && (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT)) {
            for (WidgetRun widgetRun : this.mRuns) {
                if (widgetRun.orientation == i && !widgetRun.supportsWrapComputation()) {
                    z = false;
                    break;
                }
            }
            if (i == 0) {
                if (z && dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    this.container.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                    ConstraintWidgetContainer constraintWidgetContainer = this.container;
                    constraintWidgetContainer.setWidth(computeWrap(constraintWidgetContainer, 0));
                    this.container.horizontalRun.dimension.resolve(this.container.getWidth());
                }
            } else if (z && dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                this.container.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                ConstraintWidgetContainer constraintWidgetContainer2 = this.container;
                constraintWidgetContainer2.setHeight(computeWrap(constraintWidgetContainer2, 1));
                this.container.verticalRun.dimension.resolve(this.container.getHeight());
            }
        }
        if (i == 0) {
            if (this.container.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.FIXED || this.container.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                int width = this.container.getWidth() + x;
                this.container.horizontalRun.end.resolve(width);
                this.container.horizontalRun.dimension.resolve(width - x);
                z2 = true;
            } else {
                z2 = false;
            }
        } else if (this.container.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.FIXED || this.container.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            int height = this.container.getHeight() + y;
            this.container.verticalRun.end.resolve(height);
            this.container.verticalRun.dimension.resolve(height - y);
            z2 = true;
        } else {
            z2 = false;
        }
        measureWidgets();
        for (WidgetRun widgetRun2 : this.mRuns) {
            if (widgetRun2.orientation == i && (widgetRun2.widget != this.container || widgetRun2.resolved)) {
                widgetRun2.applyToWidget();
            }
        }
        for (WidgetRun widgetRun3 : this.mRuns) {
            if (widgetRun3.orientation == i && (z2 || widgetRun3.widget != this.container)) {
                if (!widgetRun3.start.resolved || !widgetRun3.end.resolved || (!(widgetRun3 instanceof ChainRun) && !widgetRun3.dimension.resolved)) {
                    this.container.setHorizontalDimensionBehaviour(dimensionBehaviour);
                    this.container.setVerticalDimensionBehaviour(dimensionBehaviour2);
                    return z3;
                }
            }
        }
        z3 = true;
        this.container.setHorizontalDimensionBehaviour(dimensionBehaviour);
        this.container.setVerticalDimensionBehaviour(dimensionBehaviour2);
        return z3;
    }

    private void measure(ConstraintWidget constraintWidget, ConstraintWidget.DimensionBehaviour dimensionBehaviour, int i, ConstraintWidget.DimensionBehaviour dimensionBehaviour2, int i2) {
        this.mMeasure.horizontalBehavior = dimensionBehaviour;
        this.mMeasure.verticalBehavior = dimensionBehaviour2;
        this.mMeasure.horizontalDimension = i;
        this.mMeasure.verticalDimension = i2;
        this.mMeasurer.measure(constraintWidget, this.mMeasure);
        constraintWidget.setWidth(this.mMeasure.measuredWidth);
        constraintWidget.setHeight(this.mMeasure.measuredHeight);
        constraintWidget.setHasBaseline(this.mMeasure.measuredHasBaseline);
        constraintWidget.setBaselineDistance(this.mMeasure.measuredBaseline);
    }

    private boolean basicMeasureWidgets(ConstraintWidgetContainer constraintWidgetContainer) {
        int width;
        ConstraintWidget.DimensionBehaviour dimensionBehaviour;
        int height;
        for (ConstraintWidget constraintWidget : constraintWidgetContainer.mChildren) {
            ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = constraintWidget.mListDimensionBehaviors[0];
            ConstraintWidget.DimensionBehaviour dimensionBehaviour3 = constraintWidget.mListDimensionBehaviors[1];
            if (constraintWidget.getVisibility() == 8) {
                constraintWidget.measured = true;
            } else {
                if (constraintWidget.mMatchConstraintPercentWidth < 1.0f && dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    constraintWidget.mMatchConstraintDefaultWidth = 2;
                }
                if (constraintWidget.mMatchConstraintPercentHeight < 1.0f && dimensionBehaviour3 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    constraintWidget.mMatchConstraintDefaultHeight = 2;
                }
                if (constraintWidget.getDimensionRatio() > 0.0f) {
                    if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && (dimensionBehaviour3 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || dimensionBehaviour3 == ConstraintWidget.DimensionBehaviour.FIXED)) {
                        constraintWidget.mMatchConstraintDefaultWidth = 3;
                    } else if (dimensionBehaviour3 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.FIXED)) {
                        constraintWidget.mMatchConstraintDefaultHeight = 3;
                    } else if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && dimensionBehaviour3 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                        if (constraintWidget.mMatchConstraintDefaultWidth == 0) {
                            constraintWidget.mMatchConstraintDefaultWidth = 3;
                        }
                        if (constraintWidget.mMatchConstraintDefaultHeight == 0) {
                            constraintWidget.mMatchConstraintDefaultHeight = 3;
                        }
                    }
                }
                if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mMatchConstraintDefaultWidth == 1 && (constraintWidget.mLeft.mTarget == null || constraintWidget.mRight.mTarget == null)) {
                    dimensionBehaviour2 = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                }
                ConstraintWidget.DimensionBehaviour dimensionBehaviour4 = dimensionBehaviour2;
                ConstraintWidget.DimensionBehaviour dimensionBehaviour5 = (dimensionBehaviour3 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mMatchConstraintDefaultHeight == 1 && (constraintWidget.mTop.mTarget == null || constraintWidget.mBottom.mTarget == null)) ? ConstraintWidget.DimensionBehaviour.WRAP_CONTENT : dimensionBehaviour3;
                constraintWidget.horizontalRun.dimensionBehavior = dimensionBehaviour4;
                constraintWidget.horizontalRun.matchConstraintsType = constraintWidget.mMatchConstraintDefaultWidth;
                constraintWidget.verticalRun.dimensionBehavior = dimensionBehaviour5;
                constraintWidget.verticalRun.matchConstraintsType = constraintWidget.mMatchConstraintDefaultHeight;
                if ((dimensionBehaviour4 == ConstraintWidget.DimensionBehaviour.MATCH_PARENT || dimensionBehaviour4 == ConstraintWidget.DimensionBehaviour.FIXED || dimensionBehaviour4 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) && (dimensionBehaviour5 == ConstraintWidget.DimensionBehaviour.MATCH_PARENT || dimensionBehaviour5 == ConstraintWidget.DimensionBehaviour.FIXED || dimensionBehaviour5 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT)) {
                    int width2 = constraintWidget.getWidth();
                    if (dimensionBehaviour4 == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                        width = (constraintWidgetContainer.getWidth() - constraintWidget.mLeft.mMargin) - constraintWidget.mRight.mMargin;
                        dimensionBehaviour4 = ConstraintWidget.DimensionBehaviour.FIXED;
                    } else {
                        width = width2;
                    }
                    int height2 = constraintWidget.getHeight();
                    if (dimensionBehaviour5 == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                        height = (constraintWidgetContainer.getHeight() - constraintWidget.mTop.mMargin) - constraintWidget.mBottom.mMargin;
                        dimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    } else {
                        dimensionBehaviour = dimensionBehaviour5;
                        height = height2;
                    }
                    measure(constraintWidget, dimensionBehaviour4, width, dimensionBehaviour, height);
                    constraintWidget.horizontalRun.dimension.resolve(constraintWidget.getWidth());
                    constraintWidget.verticalRun.dimension.resolve(constraintWidget.getHeight());
                    constraintWidget.measured = true;
                } else {
                    if (dimensionBehaviour4 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && (dimensionBehaviour5 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || dimensionBehaviour5 == ConstraintWidget.DimensionBehaviour.FIXED)) {
                        if (constraintWidget.mMatchConstraintDefaultWidth == 3) {
                            if (dimensionBehaviour5 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                measure(constraintWidget, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0);
                            }
                            int height3 = constraintWidget.getHeight();
                            measure(constraintWidget, ConstraintWidget.DimensionBehaviour.FIXED, (int) ((height3 * constraintWidget.mDimensionRatio) + 0.5f), ConstraintWidget.DimensionBehaviour.FIXED, height3);
                            constraintWidget.horizontalRun.dimension.resolve(constraintWidget.getWidth());
                            constraintWidget.verticalRun.dimension.resolve(constraintWidget.getHeight());
                            constraintWidget.measured = true;
                        } else if (constraintWidget.mMatchConstraintDefaultWidth == 1) {
                            measure(constraintWidget, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0, dimensionBehaviour5, 0);
                            constraintWidget.horizontalRun.dimension.wrapValue = constraintWidget.getWidth();
                        } else if (constraintWidget.mMatchConstraintDefaultWidth == 2) {
                            if (constraintWidgetContainer.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.FIXED || constraintWidgetContainer.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                                measure(constraintWidget, ConstraintWidget.DimensionBehaviour.FIXED, (int) ((constraintWidget.mMatchConstraintPercentWidth * constraintWidgetContainer.getWidth()) + 0.5f), dimensionBehaviour5, constraintWidget.getHeight());
                                constraintWidget.horizontalRun.dimension.resolve(constraintWidget.getWidth());
                                constraintWidget.verticalRun.dimension.resolve(constraintWidget.getHeight());
                                constraintWidget.measured = true;
                            }
                        } else if (constraintWidget.mListAnchors[0].mTarget == null || constraintWidget.mListAnchors[1].mTarget == null) {
                            measure(constraintWidget, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0, dimensionBehaviour5, 0);
                            constraintWidget.horizontalRun.dimension.resolve(constraintWidget.getWidth());
                            constraintWidget.verticalRun.dimension.resolve(constraintWidget.getHeight());
                            constraintWidget.measured = true;
                        }
                    }
                    if (dimensionBehaviour5 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && (dimensionBehaviour4 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || dimensionBehaviour4 == ConstraintWidget.DimensionBehaviour.FIXED)) {
                        if (constraintWidget.mMatchConstraintDefaultHeight == 3) {
                            if (dimensionBehaviour4 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                measure(constraintWidget, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0);
                            }
                            int width3 = constraintWidget.getWidth();
                            float f = constraintWidget.mDimensionRatio;
                            if (constraintWidget.getDimensionRatioSide() == -1) {
                                f = 1.0f / f;
                            }
                            measure(constraintWidget, ConstraintWidget.DimensionBehaviour.FIXED, width3, ConstraintWidget.DimensionBehaviour.FIXED, (int) ((width3 * f) + 0.5f));
                            constraintWidget.horizontalRun.dimension.resolve(constraintWidget.getWidth());
                            constraintWidget.verticalRun.dimension.resolve(constraintWidget.getHeight());
                            constraintWidget.measured = true;
                        } else if (constraintWidget.mMatchConstraintDefaultHeight == 1) {
                            measure(constraintWidget, dimensionBehaviour4, 0, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0);
                            constraintWidget.verticalRun.dimension.wrapValue = constraintWidget.getHeight();
                        } else if (constraintWidget.mMatchConstraintDefaultHeight == 2) {
                            if (constraintWidgetContainer.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.FIXED || constraintWidgetContainer.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                                measure(constraintWidget, dimensionBehaviour4, constraintWidget.getWidth(), ConstraintWidget.DimensionBehaviour.FIXED, (int) ((constraintWidget.mMatchConstraintPercentHeight * constraintWidgetContainer.getHeight()) + 0.5f));
                                constraintWidget.horizontalRun.dimension.resolve(constraintWidget.getWidth());
                                constraintWidget.verticalRun.dimension.resolve(constraintWidget.getHeight());
                                constraintWidget.measured = true;
                            }
                        } else if (constraintWidget.mListAnchors[2].mTarget == null || constraintWidget.mListAnchors[3].mTarget == null) {
                            measure(constraintWidget, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0, dimensionBehaviour5, 0);
                            constraintWidget.horizontalRun.dimension.resolve(constraintWidget.getWidth());
                            constraintWidget.verticalRun.dimension.resolve(constraintWidget.getHeight());
                            constraintWidget.measured = true;
                        }
                    }
                    if (dimensionBehaviour4 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && dimensionBehaviour5 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                        if (constraintWidget.mMatchConstraintDefaultWidth == 1 || constraintWidget.mMatchConstraintDefaultHeight == 1) {
                            measure(constraintWidget, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, 0);
                            constraintWidget.horizontalRun.dimension.wrapValue = constraintWidget.getWidth();
                            constraintWidget.verticalRun.dimension.wrapValue = constraintWidget.getHeight();
                        } else if (constraintWidget.mMatchConstraintDefaultHeight == 2 && constraintWidget.mMatchConstraintDefaultWidth == 2 && constraintWidgetContainer.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.FIXED && constraintWidgetContainer.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.FIXED) {
                            measure(constraintWidget, ConstraintWidget.DimensionBehaviour.FIXED, (int) ((constraintWidget.mMatchConstraintPercentWidth * constraintWidgetContainer.getWidth()) + 0.5f), ConstraintWidget.DimensionBehaviour.FIXED, (int) ((constraintWidget.mMatchConstraintPercentHeight * constraintWidgetContainer.getHeight()) + 0.5f));
                            constraintWidget.horizontalRun.dimension.resolve(constraintWidget.getWidth());
                            constraintWidget.verticalRun.dimension.resolve(constraintWidget.getHeight());
                            constraintWidget.measured = true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void measureWidgets() {
        for (ConstraintWidget constraintWidget : this.container.mChildren) {
            if (!constraintWidget.measured) {
                boolean z = false;
                ConstraintWidget.DimensionBehaviour dimensionBehaviour = constraintWidget.mListDimensionBehaviors[0];
                ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = constraintWidget.mListDimensionBehaviors[1];
                int i = constraintWidget.mMatchConstraintDefaultWidth;
                int i2 = constraintWidget.mMatchConstraintDefaultHeight;
                boolean z2 = dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && i == 1);
                if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && i2 == 1)) {
                    z = true;
                }
                boolean z3 = constraintWidget.horizontalRun.dimension.resolved;
                boolean z4 = constraintWidget.verticalRun.dimension.resolved;
                if (z3 && z4) {
                    measure(constraintWidget, ConstraintWidget.DimensionBehaviour.FIXED, constraintWidget.horizontalRun.dimension.value, ConstraintWidget.DimensionBehaviour.FIXED, constraintWidget.verticalRun.dimension.value);
                    constraintWidget.measured = true;
                } else if (z3 && z) {
                    measure(constraintWidget, ConstraintWidget.DimensionBehaviour.FIXED, constraintWidget.horizontalRun.dimension.value, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, constraintWidget.verticalRun.dimension.value);
                    if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                        constraintWidget.verticalRun.dimension.wrapValue = constraintWidget.getHeight();
                    } else {
                        constraintWidget.verticalRun.dimension.resolve(constraintWidget.getHeight());
                        constraintWidget.measured = true;
                    }
                } else if (z4 && z2) {
                    measure(constraintWidget, ConstraintWidget.DimensionBehaviour.WRAP_CONTENT, constraintWidget.horizontalRun.dimension.value, ConstraintWidget.DimensionBehaviour.FIXED, constraintWidget.verticalRun.dimension.value);
                    if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                        constraintWidget.horizontalRun.dimension.wrapValue = constraintWidget.getWidth();
                    } else {
                        constraintWidget.horizontalRun.dimension.resolve(constraintWidget.getWidth());
                        constraintWidget.measured = true;
                    }
                }
                if (constraintWidget.measured && constraintWidget.verticalRun.baselineDimension != null) {
                    constraintWidget.verticalRun.baselineDimension.resolve(constraintWidget.getBaselineDistance());
                }
            }
        }
    }

    public void invalidateGraph() {
        this.mNeedBuildGraph = true;
    }

    public void invalidateMeasures() {
        this.mNeedRedoMeasures = true;
    }

    public void buildGraph() {
        buildGraph(this.mRuns);
        this.mGroups.clear();
        RunGroup.index = 0;
        findGroup(this.container.horizontalRun, 0, this.mGroups);
        findGroup(this.container.verticalRun, 1, this.mGroups);
        this.mNeedBuildGraph = false;
    }

    public void buildGraph(ArrayList<WidgetRun> arrayList) {
        arrayList.clear();
        this.mContainer.horizontalRun.clear();
        this.mContainer.verticalRun.clear();
        arrayList.add(this.mContainer.horizontalRun);
        arrayList.add(this.mContainer.verticalRun);
        HashSet hashSet = null;
        for (ConstraintWidget constraintWidget : this.mContainer.mChildren) {
            if (constraintWidget instanceof Guideline) {
                arrayList.add(new GuidelineReference(constraintWidget));
            } else {
                if (constraintWidget.isInHorizontalChain()) {
                    if (constraintWidget.horizontalChainRun == null) {
                        constraintWidget.horizontalChainRun = new ChainRun(constraintWidget, 0);
                    }
                    if (hashSet == null) {
                        hashSet = new HashSet();
                    }
                    hashSet.add(constraintWidget.horizontalChainRun);
                } else {
                    arrayList.add(constraintWidget.horizontalRun);
                }
                if (constraintWidget.isInVerticalChain()) {
                    if (constraintWidget.verticalChainRun == null) {
                        constraintWidget.verticalChainRun = new ChainRun(constraintWidget, 1);
                    }
                    if (hashSet == null) {
                        hashSet = new HashSet();
                    }
                    hashSet.add(constraintWidget.verticalChainRun);
                } else {
                    arrayList.add(constraintWidget.verticalRun);
                }
                if (constraintWidget instanceof HelperWidget) {
                    arrayList.add(new HelperReferences(constraintWidget));
                }
            }
        }
        if (hashSet != null) {
            arrayList.addAll(hashSet);
        }
        Iterator<WidgetRun> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
        for (WidgetRun widgetRun : arrayList) {
            if (widgetRun.widget != this.mContainer) {
                widgetRun.apply();
            }
        }
    }

    private void displayGraph() {
        Iterator<WidgetRun> it = this.mRuns.iterator();
        String strGenerateDisplayGraph = "digraph {\n";
        while (it.hasNext()) {
            strGenerateDisplayGraph = generateDisplayGraph(it.next(), strGenerateDisplayGraph);
        }
        System.out.println("content:<<\n" + (strGenerateDisplayGraph + "\n}\n") + "\n>>");
    }

    private void applyGroup(DependencyNode dependencyNode, int i, int i2, DependencyNode dependencyNode2, ArrayList<RunGroup> arrayList, RunGroup runGroup) {
        WidgetRun widgetRun = dependencyNode.run;
        if (widgetRun.runGroup != null || widgetRun == this.container.horizontalRun || widgetRun == this.container.verticalRun) {
            return;
        }
        if (runGroup == null) {
            runGroup = new RunGroup(widgetRun, i2);
            arrayList.add(runGroup);
        }
        widgetRun.runGroup = runGroup;
        runGroup.add(widgetRun);
        for (Dependency dependency : widgetRun.start.dependencies) {
            if (dependency instanceof DependencyNode) {
                applyGroup((DependencyNode) dependency, i, 0, dependencyNode2, arrayList, runGroup);
            }
        }
        for (Dependency dependency2 : widgetRun.end.dependencies) {
            if (dependency2 instanceof DependencyNode) {
                applyGroup((DependencyNode) dependency2, i, 1, dependencyNode2, arrayList, runGroup);
            }
        }
        if (i == 1 && (widgetRun instanceof VerticalWidgetRun)) {
            for (Dependency dependency3 : ((VerticalWidgetRun) widgetRun).baseline.dependencies) {
                if (dependency3 instanceof DependencyNode) {
                    applyGroup((DependencyNode) dependency3, i, 2, dependencyNode2, arrayList, runGroup);
                }
            }
        }
        for (DependencyNode dependencyNode3 : widgetRun.start.targets) {
            if (dependencyNode3 == dependencyNode2) {
                runGroup.dual = true;
            }
            applyGroup(dependencyNode3, i, 0, dependencyNode2, arrayList, runGroup);
        }
        for (DependencyNode dependencyNode4 : widgetRun.end.targets) {
            if (dependencyNode4 == dependencyNode2) {
                runGroup.dual = true;
            }
            applyGroup(dependencyNode4, i, 1, dependencyNode2, arrayList, runGroup);
        }
        if (i == 1 && (widgetRun instanceof VerticalWidgetRun)) {
            Iterator<DependencyNode> it = ((VerticalWidgetRun) widgetRun).baseline.targets.iterator();
            while (it.hasNext()) {
                applyGroup(it.next(), i, 2, dependencyNode2, arrayList, runGroup);
            }
        }
    }

    private void findGroup(WidgetRun widgetRun, int i, ArrayList<RunGroup> arrayList) {
        for (Dependency dependency : widgetRun.start.dependencies) {
            if (dependency instanceof DependencyNode) {
                applyGroup((DependencyNode) dependency, i, 0, widgetRun.end, arrayList, null);
            } else if (dependency instanceof WidgetRun) {
                applyGroup(((WidgetRun) dependency).start, i, 0, widgetRun.end, arrayList, null);
            }
        }
        for (Dependency dependency2 : widgetRun.end.dependencies) {
            if (dependency2 instanceof DependencyNode) {
                applyGroup((DependencyNode) dependency2, i, 1, widgetRun.start, arrayList, null);
            } else if (dependency2 instanceof WidgetRun) {
                applyGroup(((WidgetRun) dependency2).end, i, 1, widgetRun.start, arrayList, null);
            }
        }
        if (i == 1) {
            for (Dependency dependency3 : ((VerticalWidgetRun) widgetRun).baseline.dependencies) {
                if (dependency3 instanceof DependencyNode) {
                    applyGroup((DependencyNode) dependency3, i, 2, null, arrayList, null);
                }
            }
        }
    }

    private String generateDisplayNode(DependencyNode dependencyNode, boolean z, String str) {
        StringBuilder sb = new StringBuilder(str);
        Iterator<DependencyNode> it = dependencyNode.targets.iterator();
        while (it.hasNext()) {
            String str2 = ("\n" + dependencyNode.name()) + " -> " + it.next().name();
            if (dependencyNode.margin > 0 || z || (dependencyNode.run instanceof HelperReferences)) {
                String str3 = str2 + "[";
                if (dependencyNode.margin > 0) {
                    str3 = str3 + "label=\"" + dependencyNode.margin + "\"";
                    if (z) {
                        str3 = str3 + z.b;
                    }
                }
                if (z) {
                    str3 = str3 + " style=dashed ";
                }
                if (dependencyNode.run instanceof HelperReferences) {
                    str3 = str3 + " style=bold,color=gray ";
                }
                str2 = str3 + "]";
            }
            sb.append(str2 + "\n");
        }
        return sb.toString();
    }

    private String nodeDefinition(WidgetRun widgetRun) {
        boolean z = widgetRun instanceof VerticalWidgetRun;
        String debugName = widgetRun.widget.getDebugName();
        StringBuilder sb = new StringBuilder(debugName);
        ConstraintWidget constraintWidget = widgetRun.widget;
        ConstraintWidget.DimensionBehaviour horizontalDimensionBehaviour = !z ? constraintWidget.getHorizontalDimensionBehaviour() : constraintWidget.getVerticalDimensionBehaviour();
        RunGroup runGroup = widgetRun.runGroup;
        if (!z) {
            sb.append("_HORIZONTAL");
        } else {
            sb.append("_VERTICAL");
        }
        sb.append(" [shape=none, label=<<TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"2\">  <TR>");
        if (!z) {
            sb.append("    <TD ");
            if (widgetRun.start.resolved) {
                sb.append(" BGCOLOR=\"green\"");
            }
            sb.append(" PORT=\"LEFT\" BORDER=\"1\">L</TD>");
        } else {
            sb.append("    <TD ");
            if (widgetRun.start.resolved) {
                sb.append(" BGCOLOR=\"green\"");
            }
            sb.append(" PORT=\"TOP\" BORDER=\"1\">T</TD>");
        }
        sb.append("    <TD BORDER=\"1\" ");
        if (widgetRun.dimension.resolved && !widgetRun.widget.measured) {
            sb.append(" BGCOLOR=\"green\" ");
        } else if (widgetRun.dimension.resolved) {
            sb.append(" BGCOLOR=\"lightgray\" ");
        } else if (widgetRun.widget.measured) {
            sb.append(" BGCOLOR=\"yellow\" ");
        }
        if (horizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
            sb.append("style=\"dashed\"");
        }
        sb.append(">");
        sb.append(debugName);
        if (runGroup != null) {
            sb.append(" [");
            sb.append(runGroup.groupIndex + 1);
            sb.append("/");
            sb.append(RunGroup.index);
            sb.append("]");
        }
        sb.append(" </TD>");
        if (!z) {
            sb.append("    <TD ");
            if (widgetRun.end.resolved) {
                sb.append(" BGCOLOR=\"green\"");
            }
            sb.append(" PORT=\"RIGHT\" BORDER=\"1\">R</TD>");
        } else {
            sb.append("    <TD ");
            if (((VerticalWidgetRun) widgetRun).baseline.resolved) {
                sb.append(" BGCOLOR=\"green\"");
            }
            sb.append(" PORT=\"BASELINE\" BORDER=\"1\">b</TD>    <TD ");
            if (widgetRun.end.resolved) {
                sb.append(" BGCOLOR=\"green\"");
            }
            sb.append(" PORT=\"BOTTOM\" BORDER=\"1\">B</TD>");
        }
        sb.append("  </TR></TABLE>>];\n");
        return sb.toString();
    }

    private String generateChainDisplayGraph(ChainRun chainRun, String str) {
        int i = chainRun.orientation;
        StringBuilder sb = new StringBuilder("subgraph cluster_");
        sb.append(chainRun.widget.getDebugName());
        if (i == 0) {
            sb.append("_h");
        } else {
            sb.append("_v");
        }
        sb.append(" {\n");
        String strGenerateDisplayGraph = "";
        for (WidgetRun widgetRun : chainRun.widgets) {
            sb.append(widgetRun.widget.getDebugName());
            if (i == 0) {
                sb.append("_HORIZONTAL");
            } else {
                sb.append("_VERTICAL");
            }
            sb.append(";\n");
            strGenerateDisplayGraph = generateDisplayGraph(widgetRun, strGenerateDisplayGraph);
        }
        sb.append("}\n");
        return str + strGenerateDisplayGraph + ((Object) sb);
    }

    private boolean isCenteredConnection(DependencyNode dependencyNode, DependencyNode dependencyNode2) {
        Iterator<DependencyNode> it = dependencyNode.targets.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next() != dependencyNode2) {
                i++;
            }
        }
        Iterator<DependencyNode> it2 = dependencyNode2.targets.iterator();
        int i2 = 0;
        while (it2.hasNext()) {
            if (it2.next() != dependencyNode) {
                i2++;
            }
        }
        return i > 0 && i2 > 0;
    }

    private String generateDisplayGraph(WidgetRun widgetRun, String str) {
        boolean z;
        DependencyNode dependencyNode = widgetRun.start;
        DependencyNode dependencyNode2 = widgetRun.end;
        StringBuilder sb = new StringBuilder(str);
        if (!(widgetRun instanceof HelperReferences) && dependencyNode.dependencies.isEmpty() && (dependencyNode2.dependencies.isEmpty() && dependencyNode.targets.isEmpty()) && dependencyNode2.targets.isEmpty()) {
            return str;
        }
        sb.append(nodeDefinition(widgetRun));
        boolean zIsCenteredConnection = isCenteredConnection(dependencyNode, dependencyNode2);
        String strGenerateDisplayNode = generateDisplayNode(dependencyNode2, zIsCenteredConnection, generateDisplayNode(dependencyNode, zIsCenteredConnection, str));
        boolean z2 = widgetRun instanceof VerticalWidgetRun;
        if (z2) {
            strGenerateDisplayNode = generateDisplayNode(((VerticalWidgetRun) widgetRun).baseline, zIsCenteredConnection, strGenerateDisplayNode);
        }
        if ((widgetRun instanceof HorizontalWidgetRun) || (((z = widgetRun instanceof ChainRun)) && ((ChainRun) widgetRun).orientation == 0)) {
            ConstraintWidget.DimensionBehaviour horizontalDimensionBehaviour = widgetRun.widget.getHorizontalDimensionBehaviour();
            if (horizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.FIXED || horizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                if (!dependencyNode.targets.isEmpty() && dependencyNode2.targets.isEmpty()) {
                    sb.append("\n");
                    sb.append(dependencyNode2.name());
                    sb.append(" -> ");
                    sb.append(dependencyNode.name());
                    sb.append("\n");
                } else if (dependencyNode.targets.isEmpty() && !dependencyNode2.targets.isEmpty()) {
                    sb.append("\n");
                    sb.append(dependencyNode.name());
                    sb.append(" -> ");
                    sb.append(dependencyNode2.name());
                    sb.append("\n");
                }
            } else if (horizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widgetRun.widget.getDimensionRatio() > 0.0f) {
                sb.append("\n");
                sb.append(widgetRun.widget.getDebugName());
                sb.append("_HORIZONTAL -> ");
                sb.append(widgetRun.widget.getDebugName());
                sb.append("_VERTICAL;\n");
            }
        } else if (z2 || (z && ((ChainRun) widgetRun).orientation == 1)) {
            ConstraintWidget.DimensionBehaviour verticalDimensionBehaviour = widgetRun.widget.getVerticalDimensionBehaviour();
            if (verticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.FIXED || verticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                if (!dependencyNode.targets.isEmpty() && dependencyNode2.targets.isEmpty()) {
                    sb.append("\n");
                    sb.append(dependencyNode2.name());
                    sb.append(" -> ");
                    sb.append(dependencyNode.name());
                    sb.append("\n");
                } else if (dependencyNode.targets.isEmpty() && !dependencyNode2.targets.isEmpty()) {
                    sb.append("\n");
                    sb.append(dependencyNode.name());
                    sb.append(" -> ");
                    sb.append(dependencyNode2.name());
                    sb.append("\n");
                }
            } else if (verticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widgetRun.widget.getDimensionRatio() > 0.0f) {
                sb.append("\n");
                sb.append(widgetRun.widget.getDebugName());
                sb.append("_VERTICAL -> ");
                sb.append(widgetRun.widget.getDebugName());
                sb.append("_HORIZONTAL;\n");
            }
        }
        if (widgetRun instanceof ChainRun) {
            return generateChainDisplayGraph((ChainRun) widgetRun, strGenerateDisplayNode);
        }
        return sb.toString();
    }
}
