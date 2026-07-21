package miuix.flexible.template;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import miuix.flexible.R;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes2.dex */
public class StandardButtonTemplate extends AbstractMarkTemplate {
    private static final SparseArray<HyperCellLayout.LayoutParams> LARGE_PARAMS;
    private static final SparseArray<HyperCellLayout.LayoutParams> NORMAL_PARAMS;

    static {
        SparseArray<HyperCellLayout.LayoutParams> sparseArray = new SparseArray<>();
        NORMAL_PARAMS = sparseArray;
        sparseArray.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 0).setMargin(0, 0, 16, 0));
        sparseArray.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 16, 1));
        sparseArray.put(R.id.area_title, generateLayoutParams(3, 0.0f, 0.0f, 16, 2).setMargin(0, 0, 0, 0));
        sparseArray.put(R.id.area_title_comment, generateLayoutParams(3, 0.0f, 0.0f, 16, 3).setMargin(8, 0, 0, 0).setPriority(1));
        sparseArray.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4).setMargin(0, 10, 0, 0));
        sparseArray.put(R.id.area_text_button, generateLayoutParams(1, 0.0f, 0.0f, 8388629, 5).setMargin(10, 0, 0, 0));
        sparseArray.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 8388629, 6).setMargin(10, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray2 = new SparseArray<>();
        LARGE_PARAMS = sparseArray2;
        sparseArray2.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 0));
        sparseArray2.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 1).setMargin(0, 0, 16, 0));
        sparseArray2.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2).setMargin(0, 0, 0, 0));
        sparseArray2.put(R.id.area_title_comment, generateLayoutParams(2, 0.0f, 0.0f, 0, 3).setMargin(0, 4, 0, 0).setPriority(1));
        sparseArray2.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4).setMargin(0, 10, 0, 0));
        sparseArray2.put(R.id.area_text_button, generateLayoutParams(2, 0.0f, 0.0f, 0, 5).setMargin(0, 10, 0, 0));
        sparseArray2.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 8388629, 6).setMargin(10, 0, 0, 0));
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate
    public void onAddAuxiliaryViews(ViewGroup viewGroup) {
        super.onAddAuxiliaryViews(viewGroup);
        addAuxiliaryView(viewGroup, this.mContext, R.id.view_auxiliary);
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate
    public void onPreBuildViewTree(ViewGroup viewGroup) {
        super.onPreBuildViewTree(viewGroup);
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(childAt);
            HyperCellLayout.LayoutParams layoutParams = getLayoutParams(childAt);
            setGravity(childViewLayoutParamsSafe, layoutParams);
            setMargin(childViewLayoutParamsSafe, layoutParams);
            setPriority(childViewLayoutParamsSafe, layoutParams);
        }
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate
    public HyperCellLayout.LayoutParams getLayoutParams(View view) {
        HyperCellLayout.LayoutParams layoutParams;
        HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(view);
        int areaId = childViewLayoutParamsSafe.getAreaId();
        if (getLevel() == 1) {
            layoutParams = NORMAL_PARAMS.get(areaId);
        } else {
            layoutParams = LARGE_PARAMS.get(areaId);
        }
        return layoutParams == null ? childViewLayoutParamsSafe : layoutParams;
    }
}
