package miuix.flexible.template;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import miuix.flexible.R;
import miuix.flexible.template.level.LevelSupplier;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes2.dex */
public class TernaryLayoutTemplate extends AbstractMarkTemplate {
    public static final int LEVEL_NARROW = 1;
    public static final int LEVEL_REGULAR = 2;
    public static final int LEVEL_WIDE = 3;
    private static final SparseArray<HyperCellLayout.LayoutParams> NARROW_PARAMS;
    private static final SparseArray<HyperCellLayout.LayoutParams> REGULAR_PARAMS;
    public static final int UNDEFINED = -1;
    private static final SparseArray<HyperCellLayout.LayoutParams> WIDE_PARAMS;
    private ViewGroup mContainer;
    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    private int regularWidthThreshold = -1;
    private int wideWidthThreshold = -1;
    private int mCurrentLevel = -1;

    static {
        SparseArray<HyperCellLayout.LayoutParams> sparseArray = new SparseArray<>();
        NARROW_PARAMS = sparseArray;
        sparseArray.put(R.id.area_item1, generateLayoutParams(2, 0.0f, 0.0f, 0, 0));
        sparseArray.put(R.id.area_item2, generateLayoutParams(2, 0.0f, 0.0f, 0, 1));
        sparseArray.put(R.id.area_item3, generateLayoutParams(2, 0.0f, 0.0f, 0, 2));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray2 = new SparseArray<>();
        REGULAR_PARAMS = sparseArray2;
        sparseArray2.put(R.id.area_item1, generateLayoutParams(1, 1.0f, 0.0f, 0, 0));
        sparseArray2.put(R.id.area_item2, generateLayoutParams(2, 1.0f, 1.0f, 0, 1));
        sparseArray2.put(R.id.area_item3, generateLayoutParams(2, 1.0f, 0.0f, 0, 2));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray3 = new SparseArray<>();
        WIDE_PARAMS = sparseArray3;
        sparseArray3.put(R.id.area_item1, generateLayoutParams(1, 1.0f, 0.0f, 0, 0));
        sparseArray3.put(R.id.area_item2, generateLayoutParams(1, 1.0f, 0.0f, 0, 1));
        sparseArray3.put(R.id.area_item3, generateLayoutParams(1, 1.0f, 0.0f, 0, 2));
    }

    public void setThreshold(int i, int i2) {
        this.regularWidthThreshold = i;
        this.wideWidthThreshold = i2;
        ViewGroup viewGroup = this.mContainer;
        if (viewGroup != null) {
            viewGroup.requestLayout();
        }
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public void onFinishInflate(ViewGroup viewGroup) {
        this.mContainer = viewGroup;
        super.onFinishInflate(viewGroup);
        if (this.regularWidthThreshold == -1) {
            this.regularWidthThreshold = dp2px(640.0f);
        }
        if (this.wideWidthThreshold == -1) {
            this.wideWidthThreshold = dp2px(960.0f);
        }
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public void onAttachedToWindow(final ViewGroup viewGroup) {
        super.onAttachedToWindow(viewGroup);
        this.mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: miuix.flexible.template.TernaryLayoutTemplate$$ExternalSyntheticLambda0
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public final boolean onPreDraw() {
                return this.f$0.m1854x1accc6fc(viewGroup);
            }
        };
        viewGroup.getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    }

    /* JADX INFO: renamed from: lambda$onAttachedToWindow$0$miuix-flexible-template-TernaryLayoutTemplate, reason: not valid java name */
    /* synthetic */ boolean m1854x1accc6fc(ViewGroup viewGroup) {
        int level = getLevel();
        if (this.mCurrentLevel == level) {
            return true;
        }
        this.mCurrentLevel = level;
        onPreBuildViewTree(viewGroup);
        buildViewTree(viewGroup);
        viewGroup.requestLayout();
        applyLevel();
        return false;
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public void onDetachedFromWindow(ViewGroup viewGroup) {
        if (this.mOnPreDrawListener != null) {
            viewGroup.getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
            this.mOnPreDrawListener = null;
        }
        super.onDetachedFromWindow(viewGroup);
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate
    public HyperCellLayout.LayoutParams getLayoutParams(View view) {
        HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(view);
        int areaId = childViewLayoutParamsSafe.getAreaId();
        int level = getLevel();
        if (level == 1) {
            return NARROW_PARAMS.get(areaId);
        }
        if (level != 2) {
            return level != 3 ? childViewLayoutParamsSafe : WIDE_PARAMS.get(areaId);
        }
        return REGULAR_PARAMS.get(areaId);
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public LevelSupplier createLevelSupplier() {
        return new LevelSupplier() { // from class: miuix.flexible.template.TernaryLayoutTemplate$$ExternalSyntheticLambda1
            @Override // miuix.flexible.template.level.LevelSupplier
            public final int getLevel() {
                return this.f$0.m1853x4d4342a9();
            }
        };
    }

    /* JADX INFO: renamed from: lambda$createLevelSupplier$1$miuix-flexible-template-TernaryLayoutTemplate, reason: not valid java name */
    /* synthetic */ int m1853x4d4342a9() {
        ViewGroup viewGroup = this.mContainer;
        int measuredWidth = viewGroup != null ? viewGroup.getMeasuredWidth() : 0;
        if (measuredWidth >= this.wideWidthThreshold) {
            return 3;
        }
        return measuredWidth >= this.regularWidthThreshold ? 2 : 1;
    }
}
