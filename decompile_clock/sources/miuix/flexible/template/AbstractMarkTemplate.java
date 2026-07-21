package miuix.flexible.template;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import miuix.core.util.MiuixUIUtils;
import miuix.flexible.R;
import miuix.flexible.mark.MarkHelper;
import miuix.flexible.mark.ViewList;
import miuix.flexible.mark.ViewNode;
import miuix.flexible.template.level.FontLevelSupplier;
import miuix.flexible.template.level.LevelSupplier;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes2.dex */
public abstract class AbstractMarkTemplate implements IHyperCellTemplate, MarkHelper.IParamsGetter {
    protected static final int NOT_SET = Integer.MAX_VALUE;
    protected Context mContext;
    protected float mDensity;
    protected int mLevel;
    private HyperCellLayout.LevelCallback mLevelCallback;
    private LevelSupplier mLevelSupplier;
    private ViewList mViewList;
    private int mGravity = 0;
    private boolean mFinishInflate = false;
    private int mColumnSpacing = 0;
    private int mRowSpacing = 0;

    public abstract HyperCellLayout.LayoutParams getLayoutParams(View view);

    public void onAddAuxiliaryViews(ViewGroup viewGroup) {
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onAttachedToWindow(ViewGroup viewGroup) {
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onDetachedFromWindow(ViewGroup viewGroup) {
    }

    public void onPreBuildViewTree(ViewGroup viewGroup) {
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void init(ViewGroup viewGroup, Context context, AttributeSet attributeSet) {
        this.mContext = context;
        this.mDensity = context.getResources().getDisplayMetrics().density;
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HyperCellLayout);
            int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArrayObtainStyledAttributes.getIndex(i);
                if (index == R.styleable.HyperCellLayout_android_gravity) {
                    this.mGravity = typedArrayObtainStyledAttributes.getInt(index, 0);
                } else if (index == R.styleable.HyperCellLayout_column_spacing) {
                    this.mColumnSpacing = (int) (typedArrayObtainStyledAttributes.getDimension(index, 0.0f) + 0.5f);
                } else if (index == R.styleable.HyperCellLayout_row_spacing) {
                    this.mRowSpacing = (int) (typedArrayObtainStyledAttributes.getDimension(index, 0.0f) + 0.5f);
                }
            }
            typedArrayObtainStyledAttributes.recycle();
        }
        this.mLevelSupplier = createLevelSupplier();
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onFinishInflate(ViewGroup viewGroup) {
        onAddAuxiliaryViews(viewGroup);
        onPreBuildViewTree(viewGroup);
        buildViewTree(viewGroup);
        this.mFinishInflate = true;
        applyLevel();
    }

    @Override // miuix.flexible.mark.MarkHelper.IParamsGetter
    public int getMark(View view) {
        return getLayoutParams(view).getMark();
    }

    @Override // miuix.flexible.mark.MarkHelper.IParamsGetter
    public float getWeight(View view) {
        HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(view);
        if ((childViewLayoutParamsSafe.getCustomParams() & 512) != 0) {
            return childViewLayoutParamsSafe.getWeight();
        }
        return getLayoutParams(view).getWeight();
    }

    @Override // miuix.flexible.mark.MarkHelper.IParamsGetter
    public float getGroupWeight(View view) {
        HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(view);
        if ((childViewLayoutParamsSafe.getCustomParams() & 1024) != 0) {
            return childViewLayoutParamsSafe.getGroupWeight();
        }
        return getLayoutParams(view).getGroupWeight();
    }

    @Override // miuix.flexible.mark.MarkHelper.IParamsGetter
    public int getOrder(View view) {
        return getLayoutParams(view).getOrder();
    }

    public void buildViewTree(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        View[] viewArr = new View[childCount];
        for (int i = 0; i < childCount; i++) {
            viewArr[i] = viewGroup.getChildAt(i);
        }
        this.mViewList = MarkHelper.buildViewNodeTree(viewArr, this);
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onViewAdded(ViewGroup viewGroup, View view) {
        if (this.mFinishInflate) {
            onPreBuildViewTree(viewGroup);
            buildViewTree(viewGroup);
        }
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onViewRemoved(ViewGroup viewGroup, View view) {
        if (this.mFinishInflate) {
            onPreBuildViewTree(viewGroup);
            buildViewTree(viewGroup);
        }
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onConfigurationChanged(ViewGroup viewGroup, Configuration configuration) {
        float f = this.mContext.getResources().getDisplayMetrics().density;
        int i = this.mLevel;
        getLevel();
        if (this.mFinishInflate) {
            if (Math.abs(f - this.mDensity) > 0.001f || i != this.mLevel) {
                onPreBuildViewTree(viewGroup);
                buildViewTree(viewGroup);
                viewGroup.requestLayout();
                if (i != this.mLevel) {
                    applyLevel();
                }
            }
        }
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public int[] onMeasure(ViewGroup viewGroup, int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        ViewList viewList = this.mViewList;
        if (viewList != null) {
            measureViewList(null, viewList, (size - viewGroup.getPaddingStart()) - viewGroup.getPaddingEnd(), (size2 - viewGroup.getPaddingTop()) - viewGroup.getPaddingBottom(), mode, mode2);
            if (mode != 1073741824) {
                size = viewGroup.getPaddingEnd() + this.mViewList.getWidth() + viewGroup.getPaddingStart();
            }
            if (mode2 != 1073741824) {
                size2 = this.mViewList.getHeight() + viewGroup.getPaddingTop() + viewGroup.getPaddingBottom();
            }
            return new int[]{Math.max(size, viewGroup.getMinimumWidth()), Math.max(size2, viewGroup.getMinimumHeight())};
        }
        return new int[]{viewGroup.getMinimumWidth(), viewGroup.getMinimumHeight()};
    }

    private void measureViewList(ViewList viewList, ViewList viewList2, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int height;
        ArrayList<ViewNode> arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(viewList2.getList());
        Collections.sort(arrayList2, new Comparator() { // from class: miuix.flexible.template.AbstractMarkTemplate$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return this.f$0.m1852xc7544ff3((ViewNode) obj, (ViewNode) obj2);
            }
        });
        Iterator it = arrayList2.iterator();
        float weight = 0.0f;
        int iMax = 0;
        int height2 = 0;
        while (true) {
            i5 = 8;
            if (!it.hasNext()) {
                break;
            }
            ViewNode viewNode = (ViewNode) it.next();
            if (viewNode.getWeight() > 0.0f) {
                weight += viewNode.getWeight();
                arrayList.add(viewNode);
            } else {
                int i8 = viewList2.getOrientation() == 1 ? i - iMax : i;
                int i9 = viewList2.getOrientation() == 1 ? i2 : i2 - height2;
                if (viewNode instanceof ViewList) {
                    ViewList viewList3 = (ViewList) viewNode;
                    measureViewList(viewList2, viewList3, i8, i9, i3, i4);
                    if (viewList2.getOrientation() == 1) {
                        iMax += viewList3.getWidth() + this.mColumnSpacing;
                        height = Math.max(height2, viewList3.getHeight());
                    } else {
                        iMax = Math.max(iMax, viewList3.getWidth());
                        height = viewList3.getHeight() + this.mRowSpacing + height2;
                    }
                } else if (viewNode.getView() != null && viewNode.getView().getVisibility() != 8) {
                    measureChildView(viewNode, i8, i9, i3, i4);
                    if (viewList2.getOrientation() == 1) {
                        iMax += viewNode.getWidth() + this.mColumnSpacing;
                        height = Math.max(height2, viewNode.getHeight());
                    } else {
                        int iMax2 = Math.max(iMax, viewNode.getWidth());
                        height2 += viewNode.getHeight() + this.mRowSpacing;
                        iMax = iMax2;
                    }
                }
                height2 = height;
            }
        }
        if (!arrayList.isEmpty()) {
            int size = (i - iMax) - ((arrayList.size() - 1) * this.mColumnSpacing);
            int size2 = (i2 - height2) - ((arrayList.size() - 1) * this.mRowSpacing);
            for (ViewNode viewNode2 : arrayList) {
                int weight2 = viewList2.getOrientation() == 1 ? (int) ((size * viewNode2.getWeight()) / weight) : i;
                int weight3 = viewList2.getOrientation() == 1 ? i2 : (int) ((size2 * viewNode2.getWeight()) / weight);
                if (viewNode2 instanceof ViewList) {
                    ViewList viewList4 = (ViewList) viewNode2;
                    i6 = size2;
                    i7 = i5;
                    measureViewList(viewList2, viewList4, weight2, weight3, i3, i4);
                    if (viewList2.getOrientation() == 1) {
                        iMax += viewList4.getWidth() + this.mColumnSpacing;
                        height2 = Math.max(height2, viewList4.getHeight());
                    } else {
                        int iMax3 = Math.max(iMax, viewList4.getWidth());
                        height2 += viewList4.getHeight() + this.mRowSpacing;
                        iMax = iMax3;
                    }
                } else {
                    i6 = size2;
                    i7 = i5;
                    if (viewNode2.getView() != null && viewNode2.getView().getVisibility() != i7) {
                        measureChildView(viewNode2, weight2, weight3, i3, i4);
                        if (viewList2.getOrientation() == 1) {
                            iMax += viewNode2.getWidth() + this.mColumnSpacing;
                            height2 = Math.max(height2, viewNode2.getHeight());
                        } else {
                            iMax = Math.max(iMax, viewNode2.getWidth());
                            height2 += viewNode2.getHeight() + this.mRowSpacing;
                        }
                    }
                }
                i5 = i7;
                size2 = i6;
            }
        }
        int i10 = i5;
        boolean z = viewList2.getWeight() > 0.0f;
        if (viewList2.getOrientation() == 1) {
            iMax -= this.mColumnSpacing;
        } else {
            height2 -= this.mRowSpacing;
        }
        if (z && i3 == 1073741824 && (viewList == null || viewList.getOrientation() == 1)) {
            iMax = i;
        }
        viewList2.setWidth(iMax);
        if (z && i4 == 1073741824 && (viewList == null || viewList.getOrientation() == 0)) {
            height2 = i2;
        }
        viewList2.setHeight(height2);
        for (ViewNode viewNode3 : viewList2.getList()) {
            if (viewNode3 instanceof ViewList) {
                ViewList viewList5 = (ViewList) viewNode3;
                if (viewList2.getOrientation() == 1 && viewList5.getHeight() < viewList2.getHeight()) {
                    if (hasMatchParentChild(viewList5, 0)) {
                        measureViewList(viewList2, viewList5, viewList5.getWidth(), viewList2.getHeight(), BasicMeasure.EXACTLY, BasicMeasure.EXACTLY);
                    } else if (viewList2.getOrientation() != 0) {
                    }
                } else if (viewList2.getOrientation() != 0 && viewList5.getWidth() < viewList2.getWidth() && hasMatchParentChild(viewList5, 1)) {
                    measureViewList(viewList2, viewList5, viewList2.getWidth(), viewList5.getHeight(), BasicMeasure.EXACTLY, BasicMeasure.EXACTLY);
                }
            } else if (viewNode3.getView() != null && viewNode3.getView().getVisibility() != i10) {
                HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(viewNode3.getView());
                if (childViewLayoutParamsSafe.height == -1 && viewList2.getOrientation() == 1 && viewNode3.getHeight() < viewList2.getHeight()) {
                    measureChildView(viewNode3, viewNode3.getWidth(), viewList2.getHeight(), BasicMeasure.EXACTLY, BasicMeasure.EXACTLY);
                } else if (childViewLayoutParamsSafe.width == -1 && viewList2.getOrientation() == 0 && viewNode3.getWidth() < viewList2.getWidth()) {
                    measureChildView(viewNode3, viewList2.getWidth(), viewNode3.getHeight(), BasicMeasure.EXACTLY, BasicMeasure.EXACTLY);
                }
            }
        }
    }

    /* JADX INFO: renamed from: lambda$measureViewList$0$miuix-flexible-template-AbstractMarkTemplate, reason: not valid java name */
    /* synthetic */ int m1852xc7544ff3(ViewNode viewNode, ViewNode viewNode2) {
        return getViewNodePriority(viewNode2) - getViewNodePriority(viewNode);
    }

    /* JADX WARN: Code duplicated, block: B:19:0x003d  */
    /* JADX WARN: Code duplicated, block: B:21:0x0044  */
    /* JADX WARN: Code duplicated, block: B:23:0x0048  */
    /* JADX WARN: Code duplicated, block: B:27:0x004f  */
    /* JADX WARN: Code duplicated, block: B:29:0x0056  */
    /* JADX WARN: Code duplicated, block: B:32:0x0082  */
    /* JADX WARN: Code duplicated, block: B:34:0x008a  */
    /* JADX WARN: Code duplicated, block: B:37:0x0099  */
    private void measureChildView(ViewNode viewNode, int i, int i2, int i3, int i4) {
        int i5;
        int marginStart;
        int marginEnd;
        int i6;
        int i7;
        int i8;
        int measuredWidth;
        int measuredHeight;
        View view = viewNode.getView();
        HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(view);
        if (childViewLayoutParamsSafe != null) {
            if (childViewLayoutParamsSafe.width == -1) {
                marginStart = i - childViewLayoutParamsSafe.getMarginStart();
                marginEnd = childViewLayoutParamsSafe.getMarginEnd();
            } else {
                if (childViewLayoutParamsSafe.width == -2) {
                    i3 = (i3 == 1073741824 || i3 == Integer.MIN_VALUE) ? Integer.MIN_VALUE : 0;
                    marginStart = i - childViewLayoutParamsSafe.getMarginStart();
                    marginEnd = childViewLayoutParamsSafe.getMarginEnd();
                } else {
                    i5 = childViewLayoutParamsSafe.width;
                    i3 = 1073741824;
                }
                if (childViewLayoutParamsSafe.height == -1) {
                    i7 = i2 - childViewLayoutParamsSafe.topMargin;
                    i8 = childViewLayoutParamsSafe.bottomMargin;
                } else {
                    if (childViewLayoutParamsSafe.height == -2) {
                        if (i4 != 1073741824 || i4 == Integer.MIN_VALUE) {
                            i4 = Integer.MIN_VALUE;
                        } else {
                            i4 = 0;
                        }
                        i7 = i2 - childViewLayoutParamsSafe.topMargin;
                        i8 = childViewLayoutParamsSafe.bottomMargin;
                    } else {
                        i6 = childViewLayoutParamsSafe.height;
                        i4 = 1073741824;
                    }
                    view.measure(View.MeasureSpec.makeMeasureSpec(i5, i3), View.MeasureSpec.makeMeasureSpec(i6, i4));
                    measuredWidth = view.getMeasuredWidth() + childViewLayoutParamsSafe.getMarginStart() + childViewLayoutParamsSafe.getMarginEnd();
                    measuredHeight = view.getMeasuredHeight() + childViewLayoutParamsSafe.topMargin + childViewLayoutParamsSafe.bottomMargin;
                    if (childViewLayoutParamsSafe.isAnimating()) {
                        if ((childViewLayoutParamsSafe.getAnimSpec() & 1) > 0) {
                            measuredWidth = (int) (measuredWidth * childViewLayoutParamsSafe.getAnimationProgress());
                        }
                        if ((childViewLayoutParamsSafe.getAnimSpec() & 2) > 0) {
                            measuredHeight = (int) (measuredHeight * childViewLayoutParamsSafe.getAnimationProgress());
                        }
                    }
                    viewNode.setWidth(measuredWidth);
                    viewNode.setHeight(measuredHeight);
                }
                i6 = i7 - i8;
                view.measure(View.MeasureSpec.makeMeasureSpec(i5, i3), View.MeasureSpec.makeMeasureSpec(i6, i4));
                measuredWidth = view.getMeasuredWidth() + childViewLayoutParamsSafe.getMarginStart() + childViewLayoutParamsSafe.getMarginEnd();
                measuredHeight = view.getMeasuredHeight() + childViewLayoutParamsSafe.topMargin + childViewLayoutParamsSafe.bottomMargin;
                if (childViewLayoutParamsSafe.isAnimating()) {
                    if ((childViewLayoutParamsSafe.getAnimSpec() & 1) > 0) {
                        measuredWidth = (int) (measuredWidth * childViewLayoutParamsSafe.getAnimationProgress());
                    }
                    if ((childViewLayoutParamsSafe.getAnimSpec() & 2) > 0) {
                        measuredHeight = (int) (measuredHeight * childViewLayoutParamsSafe.getAnimationProgress());
                    }
                }
                viewNode.setWidth(measuredWidth);
                viewNode.setHeight(measuredHeight);
            }
            i5 = marginStart - marginEnd;
            if (childViewLayoutParamsSafe.height == -1) {
                i7 = i2 - childViewLayoutParamsSafe.topMargin;
                i8 = childViewLayoutParamsSafe.bottomMargin;
            } else {
                if (childViewLayoutParamsSafe.height == -2) {
                    if (i4 != 1073741824) {
                        i4 = Integer.MIN_VALUE;
                    } else {
                        i4 = Integer.MIN_VALUE;
                    }
                    i7 = i2 - childViewLayoutParamsSafe.topMargin;
                    i8 = childViewLayoutParamsSafe.bottomMargin;
                } else {
                    i6 = childViewLayoutParamsSafe.height;
                    i4 = 1073741824;
                }
                view.measure(View.MeasureSpec.makeMeasureSpec(i5, i3), View.MeasureSpec.makeMeasureSpec(i6, i4));
                measuredWidth = view.getMeasuredWidth() + childViewLayoutParamsSafe.getMarginStart() + childViewLayoutParamsSafe.getMarginEnd();
                measuredHeight = view.getMeasuredHeight() + childViewLayoutParamsSafe.topMargin + childViewLayoutParamsSafe.bottomMargin;
                if (childViewLayoutParamsSafe.isAnimating()) {
                    if ((childViewLayoutParamsSafe.getAnimSpec() & 1) > 0) {
                        measuredWidth = (int) (measuredWidth * childViewLayoutParamsSafe.getAnimationProgress());
                    }
                    if ((childViewLayoutParamsSafe.getAnimSpec() & 2) > 0) {
                        measuredHeight = (int) (measuredHeight * childViewLayoutParamsSafe.getAnimationProgress());
                    }
                }
                viewNode.setWidth(measuredWidth);
                viewNode.setHeight(measuredHeight);
            }
            i6 = i7 - i8;
            view.measure(View.MeasureSpec.makeMeasureSpec(i5, i3), View.MeasureSpec.makeMeasureSpec(i6, i4));
            measuredWidth = view.getMeasuredWidth() + childViewLayoutParamsSafe.getMarginStart() + childViewLayoutParamsSafe.getMarginEnd();
            measuredHeight = view.getMeasuredHeight() + childViewLayoutParamsSafe.topMargin + childViewLayoutParamsSafe.bottomMargin;
            if (childViewLayoutParamsSafe.isAnimating()) {
                if ((childViewLayoutParamsSafe.getAnimSpec() & 1) > 0) {
                    measuredWidth = (int) (measuredWidth * childViewLayoutParamsSafe.getAnimationProgress());
                }
                if ((childViewLayoutParamsSafe.getAnimSpec() & 2) > 0) {
                    measuredHeight = (int) (measuredHeight * childViewLayoutParamsSafe.getAnimationProgress());
                }
            }
            viewNode.setWidth(measuredWidth);
            viewNode.setHeight(measuredHeight);
        }
    }

    protected int getViewNodePriority(ViewNode viewNode) {
        if (viewNode.getView() != null) {
            return getChildViewLayoutParamsSafe(viewNode.getView()).getPriority();
        }
        if (viewNode instanceof ViewList) {
            return getViewNodePriority(((ViewList) viewNode).getList().get(0));
        }
        return 0;
    }

    protected boolean hasMatchParentChild(ViewList viewList, int i) {
        for (ViewNode viewNode : viewList.getList()) {
            if (viewNode instanceof ViewList) {
                if (hasMatchParentChild((ViewList) viewNode, i)) {
                    return true;
                }
            } else if (viewNode.getView() != null) {
                HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(viewNode.getView());
                if (i == 1) {
                    if (childViewLayoutParamsSafe.width == -1) {
                        return true;
                    }
                } else if (childViewLayoutParamsSafe.height == -1) {
                    return true;
                }
            } else {
                continue;
            }
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:16:0x0052  */
    /* JADX WARN: Code duplicated, block: B:17:0x006c  */
    /* JADX WARN: Code duplicated, block: B:19:0x006f  */
    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onLayout(ViewGroup viewGroup, boolean z, int i, int i2, int i3, int i4) {
        int height;
        if (this.mViewList != null) {
            boolean z2 = viewGroup.getLayoutDirection() == 1;
            int i5 = i3 - i;
            int i6 = i4 - i2;
            int paddingStart = viewGroup.getPaddingStart();
            int paddingTop = viewGroup.getPaddingTop();
            int i7 = this.mGravity;
            int i8 = i7 & 112;
            int i9 = i7 & 7;
            if (i8 == 16) {
                paddingTop = viewGroup.getPaddingTop() + ((((i6 - viewGroup.getPaddingTop()) - viewGroup.getPaddingBottom()) - this.mViewList.getHeight()) / 2);
            } else {
                if (i8 == 80) {
                    height = (i6 - this.mViewList.getHeight()) - viewGroup.getPaddingBottom();
                }
                if (i9 == 1) {
                    paddingStart = viewGroup.getPaddingStart() + ((((i5 - viewGroup.getPaddingStart()) - viewGroup.getPaddingEnd()) - this.mViewList.getWidth()) / 2);
                } else if (i9 == 5) {
                    paddingStart = (i5 - this.mViewList.getWidth()) - viewGroup.getPaddingEnd();
                }
                layoutViewList(this.mViewList, z2, i5, paddingStart, height);
            }
            height = paddingTop;
            if (i9 == 1) {
                paddingStart = viewGroup.getPaddingStart() + ((((i5 - viewGroup.getPaddingStart()) - viewGroup.getPaddingEnd()) - this.mViewList.getWidth()) / 2);
            } else if (i9 == 5) {
                paddingStart = (i5 - this.mViewList.getWidth()) - viewGroup.getPaddingEnd();
            }
            layoutViewList(this.mViewList, z2, i5, paddingStart, height);
        }
    }

    private void layoutViewList(ViewList viewList, boolean z, int i, int i2, int i3) {
        int width;
        int height;
        int i4;
        int i5;
        int width2 = i2 + viewList.getWidth();
        int height2 = i3 + viewList.getHeight();
        ArrayList<ViewNode> arrayList = new ArrayList(viewList.getList());
        int i6 = 1;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            ViewNode viewNode = (ViewNode) arrayList.get(size);
            View view = viewNode.getView();
            if (view != null) {
                int gravity = getChildViewLayoutParamsSafe(view).getGravity();
                if ((viewList.getOrientation() == 1 && (gravity & 7) == 5) || (viewList.getOrientation() == 0 && (gravity & 112) == 80)) {
                    arrayList.remove(size);
                    arrayList.add(viewNode);
                }
            }
        }
        int width3 = i2;
        int height3 = i3;
        int width4 = width2;
        int height4 = height2;
        for (ViewNode viewNode2 : arrayList) {
            if (viewNode2 instanceof ViewList) {
                ViewList viewList2 = (ViewList) viewNode2;
                HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(viewList2.getList().get(0).getView());
                if (viewList.getOrientation() == i6 && (childViewLayoutParamsSafe.getGravity() & 112) == 16) {
                    height = ((viewList.getHeight() - viewList2.getHeight()) / 2) + height3;
                    width = width3;
                } else {
                    width = (viewList.getOrientation() == 0 && (childViewLayoutParamsSafe.getGravity() & 7) == i6) ? ((viewList.getWidth() - viewList2.getWidth()) / 2) + width3 : width3;
                    height = height3;
                }
                layoutViewList(viewList2, z, i, width, height);
                if (viewList.getOrientation() == i6) {
                    width3 += viewList2.getWidth() + this.mColumnSpacing;
                } else {
                    height3 += viewList2.getHeight() + this.mRowSpacing;
                }
            } else if (viewNode2.getView() != null && viewNode2.getView().getVisibility() != 8) {
                View view2 = viewNode2.getView();
                HyperCellLayout.LayoutParams childViewLayoutParamsSafe2 = getChildViewLayoutParamsSafe(view2);
                int marginStart = childViewLayoutParamsSafe2.getMarginStart() + width3;
                int measuredHeight = childViewLayoutParamsSafe2.topMargin + height3;
                if (viewList.getOrientation() == i6) {
                    if ((childViewLayoutParamsSafe2.getGravity() & 7) == 5) {
                        marginStart = (width4 - view2.getMeasuredWidth()) - childViewLayoutParamsSafe2.getMarginEnd();
                        width4 -= viewNode2.getWidth() + this.mColumnSpacing;
                    } else {
                        width3 += viewNode2.getWidth() + this.mColumnSpacing;
                    }
                    if ((childViewLayoutParamsSafe2.getGravity() & 112) == 80) {
                        measuredHeight = ((viewList.getHeight() + height3) - view2.getMeasuredHeight()) - childViewLayoutParamsSafe2.bottomMargin;
                    } else if ((childViewLayoutParamsSafe2.getGravity() & 112) == 16) {
                        measuredHeight = ((viewList.getHeight() - viewNode2.getHeight()) / 2) + height3 + childViewLayoutParamsSafe2.topMargin;
                    }
                } else {
                    if ((childViewLayoutParamsSafe2.getGravity() & 112) == 80) {
                        measuredHeight = (height4 - view2.getMeasuredHeight()) - childViewLayoutParamsSafe2.bottomMargin;
                        height4 -= viewNode2.getHeight() + this.mRowSpacing;
                    } else {
                        height3 += viewNode2.getHeight() + this.mRowSpacing;
                    }
                    if ((childViewLayoutParamsSafe2.getGravity() & 7) == 5) {
                        marginStart = ((viewList.getWidth() + width3) - view2.getMeasuredWidth()) - childViewLayoutParamsSafe2.getMarginEnd();
                    } else {
                        i6 = 1;
                        if ((childViewLayoutParamsSafe2.getGravity() & 7) == 1) {
                            marginStart = ((viewList.getWidth() - viewNode2.getWidth()) / 2) + width3 + childViewLayoutParamsSafe2.getMarginStart();
                        }
                        i4 = width4;
                        i5 = height4;
                    }
                    layoutChildView(view2, z, i, marginStart, measuredHeight, marginStart + view2.getMeasuredWidth(), measuredHeight + view2.getMeasuredHeight());
                    width3 = width3;
                    height3 = height3;
                    width4 = i4;
                    height4 = i5;
                }
                i4 = width4;
                i5 = height4;
                i6 = 1;
                layoutChildView(view2, z, i, marginStart, measuredHeight, marginStart + view2.getMeasuredWidth(), measuredHeight + view2.getMeasuredHeight());
                width3 = width3;
                height3 = height3;
                width4 = i4;
                height4 = i5;
            }
        }
    }

    public void layoutChildView(View view, boolean z, int i, int i2, int i3, int i4, int i5) {
        int i6 = z ? i - i4 : i2;
        if (z) {
            i4 = i - i2;
        }
        view.layout(i6, i3, i4, i5);
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public final int getLevel() {
        int level = this.mLevelSupplier.getLevel();
        this.mLevel = level;
        return level;
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public LevelSupplier createLevelSupplier() {
        return new FontLevelSupplier(this.mContext);
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void applyLevel() {
        HyperCellLayout.LevelCallback levelCallback = this.mLevelCallback;
        if (levelCallback != null) {
            levelCallback.onLevelApply(getLevel(), new Object[0]);
        }
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void setLevelCallback(HyperCellLayout.LevelCallback levelCallback) {
        this.mLevelCallback = levelCallback;
        applyLevel();
    }

    protected static HyperCellLayout.LayoutParams generateAuxiliaryLayoutParams(int i) {
        return generateAuxiliaryLayoutParams(i, 0, 0);
    }

    protected static HyperCellLayout.LayoutParams generateAuxiliaryLayoutParams(int i, int i2, int i3) {
        HyperCellLayout.LayoutParams layoutParams = new HyperCellLayout.LayoutParams(i2, i3);
        layoutParams.setAreaId(i);
        return layoutParams;
    }

    protected static void addAuxiliaryView(ViewGroup viewGroup, Context context, int i) {
        addAuxiliaryView(viewGroup, context, i, 0, 0);
    }

    protected static void addAuxiliaryView(ViewGroup viewGroup, Context context, int i, int i2, int i3) {
        View view = new View(context);
        view.setWillNotDraw(true);
        HyperCellLayout.LayoutParams layoutParamsGenerateAuxiliaryLayoutParams = generateAuxiliaryLayoutParams(i, i2, i3);
        layoutParamsGenerateAuxiliaryLayoutParams.setAreaId(i);
        viewGroup.addView(view, layoutParamsGenerateAuxiliaryLayoutParams);
    }

    protected HyperCellLayout.LayoutParams getChildViewLayoutParamsSafe(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof HyperCellLayout.LayoutParams) {
            return (HyperCellLayout.LayoutParams) layoutParams;
        }
        throw new IllegalArgumentException("LayoutParams " + layoutParams + " of child view " + view + " is not instance of HyperCellLayout.LayoutParams! Context is " + view.getContext());
    }

    protected static HyperCellLayout.LayoutParams generateLayoutParams(int i, float f, float f2, int i2, int i3) {
        return generateLayoutParams(i, f, f2, i2, i3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    protected static HyperCellLayout.LayoutParams generateLayoutParams(int i, float f, float f2, int i2, int i3, int i4, int i5, int i6, int i7) {
        HyperCellLayout.LayoutParams layoutParams = new HyperCellLayout.LayoutParams(0, 0);
        layoutParams.setMark(i);
        layoutParams.setWeight(f);
        layoutParams.setGroupWeight(f2);
        layoutParams.setGravity(i2);
        layoutParams.setOrder(i3);
        layoutParams.setMarginStart(i4);
        layoutParams.setMarginEnd(i6);
        layoutParams.topMargin = i5;
        layoutParams.bottomMargin = i7;
        return layoutParams;
    }

    protected void setMargin(HyperCellLayout.LayoutParams layoutParams, HyperCellLayout.LayoutParams layoutParams2) {
        if ((layoutParams.getCustomParams() & 2) == 0 && layoutParams2.getMarginStart() != Integer.MAX_VALUE) {
            layoutParams.setMarginStart(dp2px(layoutParams2.getMarginStart()));
        }
        if ((layoutParams.getCustomParams() & 4) == 0 && layoutParams2.getMarginEnd() != Integer.MAX_VALUE) {
            layoutParams.setMarginEnd(dp2px(layoutParams2.getMarginEnd()));
        }
        if ((layoutParams.getCustomParams() & 8) == 0 && layoutParams2.topMargin != Integer.MAX_VALUE) {
            layoutParams.topMargin = dp2px(layoutParams2.topMargin);
        }
        if ((layoutParams.getCustomParams() & 16) != 0 || layoutParams2.bottomMargin == Integer.MAX_VALUE) {
            return;
        }
        layoutParams.bottomMargin = dp2px(layoutParams2.bottomMargin);
    }

    protected void setGravity(HyperCellLayout.LayoutParams layoutParams, HyperCellLayout.LayoutParams layoutParams2) {
        if ((layoutParams.getCustomParams() & 1) != 0 || layoutParams2.getGravity() == Integer.MAX_VALUE) {
            return;
        }
        layoutParams.setGravity(layoutParams2.getGravity());
    }

    protected void setPriority(HyperCellLayout.LayoutParams layoutParams, HyperCellLayout.LayoutParams layoutParams2) {
        if ((layoutParams.getCustomParams() & 128) == 0 && layoutParams2.getPriority() != Integer.MAX_VALUE) {
            layoutParams.setPriority(layoutParams2.getPriority());
        }
        if ((layoutParams.getCustomParams() & 256) != 0 || layoutParams2.getGroupPriority() == Integer.MAX_VALUE) {
            return;
        }
        layoutParams.setGroupPriority(layoutParams2.getGroupPriority());
    }

    protected void setWidthHeight(HyperCellLayout.LayoutParams layoutParams, HyperCellLayout.LayoutParams layoutParams2) {
        if ((layoutParams.getCustomParams() & 32) == 0 && layoutParams2.width != Integer.MAX_VALUE) {
            layoutParams.width = layoutParams2.width;
        }
        if ((layoutParams.getCustomParams() & 64) != 0 || layoutParams2.height == Integer.MAX_VALUE) {
            return;
        }
        layoutParams.height = layoutParams2.height;
    }

    protected int dp2px(float f) {
        return MiuixUIUtils.dp2px(this.mDensity, f);
    }

    protected View findViewByAreaId(ViewGroup viewGroup, int i) {
        if (viewGroup instanceof HyperCellLayout) {
            return ((HyperCellLayout) viewGroup).findViewByAreaId(i);
        }
        return null;
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setGravity(int i) {
        this.mGravity = i;
    }

    public int getColumnSpacing() {
        return this.mColumnSpacing;
    }

    public void setColumnSpacing(int i) {
        this.mColumnSpacing = i;
    }

    public int getRowSpacing() {
        return this.mRowSpacing;
    }

    public void setRowSpacing(int i) {
        this.mRowSpacing = i;
    }
}
