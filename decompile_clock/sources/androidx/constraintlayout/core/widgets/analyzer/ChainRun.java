package androidx.constraintlayout.core.widgets.analyzer;

import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class ChainRun extends WidgetRun {
    private int chainStyle;
    ArrayList<WidgetRun> widgets;

    public ChainRun(ConstraintWidget constraintWidget, int i) {
        super(constraintWidget);
        this.widgets = new ArrayList<>();
        this.orientation = i;
        build();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ChainRun ");
        sb.append(this.orientation == 0 ? "horizontal : " : "vertical : ");
        for (WidgetRun widgetRun : this.widgets) {
            sb.append("<");
            sb.append(widgetRun);
            sb.append("> ");
        }
        return sb.toString();
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    boolean supportsWrapComputation() {
        int size = this.widgets.size();
        for (int i = 0; i < size; i++) {
            if (!this.widgets.get(i).supportsWrapComputation()) {
                return false;
            }
        }
        return true;
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    public long getWrapDimension() {
        int size = this.widgets.size();
        long wrapDimension = 0;
        for (int i = 0; i < size; i++) {
            WidgetRun widgetRun = this.widgets.get(i);
            wrapDimension = wrapDimension + ((long) widgetRun.start.margin) + widgetRun.getWrapDimension() + ((long) widgetRun.end.margin);
        }
        return wrapDimension;
    }

    private void build() {
        ConstraintWidget constraintWidget;
        ConstraintWidget constraintWidget2 = this.widget;
        ConstraintWidget previousChainMember = constraintWidget2.getPreviousChainMember(this.orientation);
        while (true) {
            ConstraintWidget constraintWidget3 = previousChainMember;
            constraintWidget = constraintWidget2;
            constraintWidget2 = constraintWidget3;
            if (constraintWidget2 == null) {
                break;
            } else {
                previousChainMember = constraintWidget2.getPreviousChainMember(this.orientation);
            }
        }
        this.widget = constraintWidget;
        this.widgets.add(constraintWidget.getRun(this.orientation));
        ConstraintWidget nextChainMember = constraintWidget.getNextChainMember(this.orientation);
        while (nextChainMember != null) {
            this.widgets.add(nextChainMember.getRun(this.orientation));
            nextChainMember = nextChainMember.getNextChainMember(this.orientation);
        }
        for (WidgetRun widgetRun : this.widgets) {
            if (this.orientation == 0) {
                widgetRun.widget.horizontalChainRun = this;
            } else if (this.orientation == 1) {
                widgetRun.widget.verticalChainRun = this;
            }
        }
        if (this.orientation == 0 && ((ConstraintWidgetContainer) this.widget.getParent()).isRtl() && this.widgets.size() > 1) {
            ArrayList<WidgetRun> arrayList = this.widgets;
            this.widget = arrayList.get(arrayList.size() - 1).widget;
        }
        this.chainStyle = this.orientation == 0 ? this.widget.getHorizontalChainStyle() : this.widget.getVerticalChainStyle();
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    void clear() {
        this.runGroup = null;
        Iterator<WidgetRun> it = this.widgets.iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    void reset() {
        this.start.resolved = false;
        this.end.resolved = false;
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun, androidx.constraintlayout.core.widgets.analyzer.Dependency
    public void update(Dependency dependency) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        float f;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        if (this.start.resolved && this.end.resolved) {
            ConstraintWidget parent = this.widget.getParent();
            boolean zIsRtl = parent instanceof ConstraintWidgetContainer ? ((ConstraintWidgetContainer) parent).isRtl() : false;
            int i12 = this.end.value - this.start.value;
            int size = this.widgets.size();
            int i13 = 0;
            while (true) {
                i = -1;
                i2 = 8;
                if (i13 >= size) {
                    i13 = -1;
                    break;
                } else if (this.widgets.get(i13).widget.getVisibility() != 8) {
                    break;
                } else {
                    i13++;
                }
            }
            int i14 = size - 1;
            for (int i15 = i14; i15 >= 0; i15--) {
                if (this.widgets.get(i15).widget.getVisibility() != 8) {
                    i = i15;
                    break;
                }
            }
            int i16 = 0;
            while (true) {
                if (i16 >= 2) {
                    i3 = 0;
                    i4 = 0;
                    i5 = 0;
                    f = 0.0f;
                    break;
                }
                int i17 = 0;
                i4 = 0;
                i5 = 0;
                int i18 = 0;
                f = 0.0f;
                while (i17 < size) {
                    WidgetRun widgetRun = this.widgets.get(i17);
                    if (widgetRun.widget.getVisibility() != i2) {
                        i18++;
                        if (i17 > 0 && i17 >= i13) {
                            i4 += widgetRun.start.margin;
                        }
                        int i19 = widgetRun.dimension.value;
                        boolean z = widgetRun.dimensionBehavior != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
                        if (z) {
                            if (this.orientation == 0 && !widgetRun.widget.horizontalRun.dimension.resolved) {
                                return;
                            }
                            if (this.orientation == 1 && !widgetRun.widget.verticalRun.dimension.resolved) {
                                return;
                            }
                        } else {
                            if (widgetRun.matchConstraintsType == 1 && i16 == 0) {
                                i19 = widgetRun.dimension.wrapValue;
                                i5++;
                            } else if (widgetRun.dimension.resolved) {
                            }
                            z = true;
                        }
                        if (z) {
                            i4 += i19;
                        } else {
                            i5++;
                            float f2 = widgetRun.widget.mWeight[this.orientation];
                            if (f2 >= 0.0f) {
                                f += f2;
                            }
                        }
                        if (i17 < i14 && i17 < i) {
                            i4 += -widgetRun.end.margin;
                        }
                    }
                    i17++;
                    i2 = 8;
                }
                if (i4 < i12 || i5 == 0) {
                    i3 = i18;
                    break;
                } else {
                    i16++;
                    i2 = 8;
                }
            }
            int i20 = this.start.value;
            if (zIsRtl) {
                i20 = this.end.value;
            }
            if (i4 > i12) {
                i20 = zIsRtl ? i20 + ((int) (((i4 - i12) / 2.0f) + 0.5f)) : i20 - ((int) (((i4 - i12) / 2.0f) + 0.5f));
            }
            if (i5 > 0) {
                float f3 = i12 - i4;
                int i21 = (int) ((f3 / i5) + 0.5f);
                int i22 = 0;
                int i23 = 0;
                while (i22 < size) {
                    WidgetRun widgetRun2 = this.widgets.get(i22);
                    int i24 = i21;
                    if (widgetRun2.widget.getVisibility() == 8 || widgetRun2.dimensionBehavior != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widgetRun2.dimension.resolved) {
                        f3 = f3;
                    } else {
                        int i25 = f > 0.0f ? (int) (((widgetRun2.widget.mWeight[this.orientation] * f3) / f) + 0.5f) : i24;
                        if (this.orientation == 0) {
                            i10 = widgetRun2.widget.mMatchConstraintMaxWidth;
                            i11 = widgetRun2.widget.mMatchConstraintMinWidth;
                        } else {
                            i10 = widgetRun2.widget.mMatchConstraintMaxHeight;
                            i11 = widgetRun2.widget.mMatchConstraintMinHeight;
                        }
                        int iMax = Math.max(i11, widgetRun2.matchConstraintsType == 1 ? Math.min(i25, widgetRun2.dimension.wrapValue) : i25);
                        if (i10 > 0) {
                            iMax = Math.min(i10, iMax);
                        }
                        if (iMax != i25) {
                            i23++;
                            i25 = iMax;
                        }
                        widgetRun2.dimension.resolve(i25);
                    }
                    i22++;
                    i21 = i24;
                    f3 = f3;
                    i4 = i4;
                    i20 = i20;
                }
                i6 = i20;
                int i26 = i4;
                if (i23 > 0) {
                    i5 -= i23;
                    int i27 = 0;
                    for (int i28 = 0; i28 < size; i28++) {
                        WidgetRun widgetRun3 = this.widgets.get(i28);
                        if (widgetRun3.widget.getVisibility() != 8) {
                            if (i28 > 0 && i28 >= i13) {
                                i27 += widgetRun3.start.margin;
                            }
                            i27 += widgetRun3.dimension.value;
                            if (i28 < i14 && i28 < i) {
                                i27 += -widgetRun3.end.margin;
                            }
                        }
                    }
                    i4 = i27;
                } else {
                    i4 = i26;
                }
                i8 = 2;
                if (this.chainStyle == 2 && i23 == 0) {
                    i7 = 0;
                    this.chainStyle = 0;
                } else {
                    i7 = 0;
                }
            } else {
                i6 = i20;
                i7 = 0;
                i8 = 2;
            }
            if (i4 > i12) {
                this.chainStyle = i8;
            }
            if (i3 > 0 && i5 == 0 && i13 == i) {
                this.chainStyle = i8;
            }
            int i29 = this.chainStyle;
            if (i29 == 1) {
                if (i3 > 1) {
                    i9 = (i12 - i4) / (i3 - 1);
                } else {
                    i9 = i3 == 1 ? (i12 - i4) / 2 : i7;
                }
                if (i5 > 0) {
                    i9 = i7;
                }
                int i30 = i6;
                for (int i31 = i7; i31 < size; i31++) {
                    WidgetRun widgetRun4 = this.widgets.get(zIsRtl ? size - (i31 + 1) : i31);
                    if (widgetRun4.widget.getVisibility() == 8) {
                        widgetRun4.start.resolve(i30);
                        widgetRun4.end.resolve(i30);
                    } else {
                        if (i31 > 0) {
                            i30 = zIsRtl ? i30 - i9 : i30 + i9;
                        }
                        if (i31 > 0 && i31 >= i13) {
                            if (zIsRtl) {
                                i30 -= widgetRun4.start.margin;
                            } else {
                                i30 += widgetRun4.start.margin;
                            }
                        }
                        if (zIsRtl) {
                            widgetRun4.end.resolve(i30);
                        } else {
                            widgetRun4.start.resolve(i30);
                        }
                        int i32 = widgetRun4.dimension.value;
                        if (widgetRun4.dimensionBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widgetRun4.matchConstraintsType == 1) {
                            i32 = widgetRun4.dimension.wrapValue;
                        }
                        i30 = zIsRtl ? i30 - i32 : i30 + i32;
                        if (zIsRtl) {
                            widgetRun4.start.resolve(i30);
                        } else {
                            widgetRun4.end.resolve(i30);
                        }
                        widgetRun4.resolved = true;
                        if (i31 < i14 && i31 < i) {
                            if (zIsRtl) {
                                i30 -= -widgetRun4.end.margin;
                            } else {
                                i30 += -widgetRun4.end.margin;
                            }
                        }
                    }
                }
                return;
            }
            if (i29 == 0) {
                int i33 = (i12 - i4) / (i3 + 1);
                if (i5 > 0) {
                    i33 = i7;
                }
                int i34 = i6;
                for (int i35 = i7; i35 < size; i35++) {
                    WidgetRun widgetRun5 = this.widgets.get(zIsRtl ? size - (i35 + 1) : i35);
                    if (widgetRun5.widget.getVisibility() == 8) {
                        widgetRun5.start.resolve(i34);
                        widgetRun5.end.resolve(i34);
                    } else {
                        int i36 = zIsRtl ? i34 - i33 : i34 + i33;
                        if (i35 > 0 && i35 >= i13) {
                            if (zIsRtl) {
                                i36 -= widgetRun5.start.margin;
                            } else {
                                i36 += widgetRun5.start.margin;
                            }
                        }
                        if (zIsRtl) {
                            widgetRun5.end.resolve(i36);
                        } else {
                            widgetRun5.start.resolve(i36);
                        }
                        int iMin = widgetRun5.dimension.value;
                        if (widgetRun5.dimensionBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widgetRun5.matchConstraintsType == 1) {
                            iMin = Math.min(iMin, widgetRun5.dimension.wrapValue);
                        }
                        i34 = zIsRtl ? i36 - iMin : i36 + iMin;
                        if (zIsRtl) {
                            widgetRun5.start.resolve(i34);
                        } else {
                            widgetRun5.end.resolve(i34);
                        }
                        if (i35 < i14 && i35 < i) {
                            if (zIsRtl) {
                                i34 -= -widgetRun5.end.margin;
                            } else {
                                i34 += -widgetRun5.end.margin;
                            }
                        }
                    }
                }
                return;
            }
            if (i29 == 2) {
                float horizontalBiasPercent = this.orientation == 0 ? this.widget.getHorizontalBiasPercent() : this.widget.getVerticalBiasPercent();
                if (zIsRtl) {
                    horizontalBiasPercent = 1.0f - horizontalBiasPercent;
                }
                int i37 = (int) (((i12 - i4) * horizontalBiasPercent) + 0.5f);
                if (i37 < 0 || i5 > 0) {
                    i37 = i7;
                }
                int i38 = zIsRtl ? i6 - i37 : i6 + i37;
                for (int i39 = i7; i39 < size; i39++) {
                    WidgetRun widgetRun6 = this.widgets.get(zIsRtl ? size - (i39 + 1) : i39);
                    if (widgetRun6.widget.getVisibility() == 8) {
                        widgetRun6.start.resolve(i38);
                        widgetRun6.end.resolve(i38);
                    } else {
                        if (i39 > 0 && i39 >= i13) {
                            if (zIsRtl) {
                                i38 -= widgetRun6.start.margin;
                            } else {
                                i38 += widgetRun6.start.margin;
                            }
                        }
                        if (zIsRtl) {
                            widgetRun6.end.resolve(i38);
                        } else {
                            widgetRun6.start.resolve(i38);
                        }
                        int i40 = widgetRun6.dimension.value;
                        if (widgetRun6.dimensionBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widgetRun6.matchConstraintsType == 1) {
                            i40 = widgetRun6.dimension.wrapValue;
                        }
                        i38 = zIsRtl ? i38 - i40 : i38 + i40;
                        if (zIsRtl) {
                            widgetRun6.start.resolve(i38);
                        } else {
                            widgetRun6.end.resolve(i38);
                        }
                        if (i39 < i14 && i39 < i) {
                            if (zIsRtl) {
                                i38 -= -widgetRun6.end.margin;
                            } else {
                                i38 += -widgetRun6.end.margin;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    public void applyToWidget() {
        for (int i = 0; i < this.widgets.size(); i++) {
            this.widgets.get(i).applyToWidget();
        }
    }

    private ConstraintWidget getFirstVisibleWidget() {
        for (int i = 0; i < this.widgets.size(); i++) {
            WidgetRun widgetRun = this.widgets.get(i);
            if (widgetRun.widget.getVisibility() != 8) {
                return widgetRun.widget;
            }
        }
        return null;
    }

    private ConstraintWidget getLastVisibleWidget() {
        for (int size = this.widgets.size() - 1; size >= 0; size--) {
            WidgetRun widgetRun = this.widgets.get(size);
            if (widgetRun.widget.getVisibility() != 8) {
                return widgetRun.widget;
            }
        }
        return null;
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    void apply() {
        Iterator<WidgetRun> it = this.widgets.iterator();
        while (it.hasNext()) {
            it.next().apply();
        }
        int size = this.widgets.size();
        if (size < 1) {
            return;
        }
        ConstraintWidget constraintWidget = this.widgets.get(0).widget;
        ConstraintWidget constraintWidget2 = this.widgets.get(size - 1).widget;
        if (this.orientation == 0) {
            ConstraintAnchor constraintAnchor = constraintWidget.mLeft;
            ConstraintAnchor constraintAnchor2 = constraintWidget2.mRight;
            DependencyNode target = getTarget(constraintAnchor, 0);
            int margin = constraintAnchor.getMargin();
            ConstraintWidget firstVisibleWidget = getFirstVisibleWidget();
            if (firstVisibleWidget != null) {
                margin = firstVisibleWidget.mLeft.getMargin();
            }
            if (target != null) {
                addTarget(this.start, target, margin);
            }
            DependencyNode target2 = getTarget(constraintAnchor2, 0);
            int margin2 = constraintAnchor2.getMargin();
            ConstraintWidget lastVisibleWidget = getLastVisibleWidget();
            if (lastVisibleWidget != null) {
                margin2 = lastVisibleWidget.mRight.getMargin();
            }
            if (target2 != null) {
                addTarget(this.end, target2, -margin2);
            }
        } else {
            ConstraintAnchor constraintAnchor3 = constraintWidget.mTop;
            ConstraintAnchor constraintAnchor4 = constraintWidget2.mBottom;
            DependencyNode target3 = getTarget(constraintAnchor3, 1);
            int margin3 = constraintAnchor3.getMargin();
            ConstraintWidget firstVisibleWidget2 = getFirstVisibleWidget();
            if (firstVisibleWidget2 != null) {
                margin3 = firstVisibleWidget2.mTop.getMargin();
            }
            if (target3 != null) {
                addTarget(this.start, target3, margin3);
            }
            DependencyNode target4 = getTarget(constraintAnchor4, 1);
            int margin4 = constraintAnchor4.getMargin();
            ConstraintWidget lastVisibleWidget2 = getLastVisibleWidget();
            if (lastVisibleWidget2 != null) {
                margin4 = lastVisibleWidget2.mBottom.getMargin();
            }
            if (target4 != null) {
                addTarget(this.end, target4, -margin4);
            }
        }
        this.start.updateDelegate = this;
        this.end.updateDelegate = this;
    }
}
