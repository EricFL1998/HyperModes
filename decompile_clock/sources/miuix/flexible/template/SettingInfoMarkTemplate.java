package miuix.flexible.template;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import miuix.flexible.R;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes2.dex */
public class SettingInfoMarkTemplate extends AbstractMarkTemplate {
    private static final SparseArray<HyperCellLayout.LayoutParams> LARGE_PARAMS;
    private static final SparseArray<HyperCellLayout.LayoutParams> NORMAL_PARAMS;

    static {
        SparseArray<HyperCellLayout.LayoutParams> sparseArray = new SparseArray<>();
        NORMAL_PARAMS = sparseArray;
        sparseArray.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 0, 0, 0, 10, 0));
        sparseArray.put(R.id.area_content, generateLayoutParams(3, 0.0f, 1.0f, 16, 1));
        sparseArray.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 2));
        sparseArray.put(R.id.area_comment, generateLayoutParams(1, 0.0f, 0.0f, 16, 3, 10, 0, 0, 0));
        sparseArray.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 4, 10, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray2 = new SparseArray<>();
        LARGE_PARAMS = sparseArray2;
        sparseArray2.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 0));
        sparseArray2.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 1, 0, 0, 10, 0));
        sparseArray2.put(R.id.area_content, generateLayoutParams(3, 0.0f, 0.0f, 16, 2));
        sparseArray2.put(R.id.area_comment, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 10, 0, 0));
        sparseArray2.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 4, 10, 0, 0, 0));
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
