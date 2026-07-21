package miuix.flexible.template;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import miuix.flexible.R;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes2.dex */
public class SimpleItemMarkTemplate extends AbstractMarkTemplate {
    private static final SparseArray<HyperCellLayout.LayoutParams> LARGE_PARAMS;
    private static final SparseArray<HyperCellLayout.LayoutParams> NORMAL_PARAMS;

    static {
        SparseArray<HyperCellLayout.LayoutParams> sparseArray = new SparseArray<>();
        NORMAL_PARAMS = sparseArray;
        sparseArray.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 0, 0, 0, 10, 0));
        sparseArray.put(R.id.area_content, generateLayoutParams(1, 0.0f, 0.0f, 0, 1));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray2 = new SparseArray<>();
        LARGE_PARAMS = sparseArray2;
        sparseArray2.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 0, 0, 0, 10, 0));
        sparseArray2.put(R.id.area_content, generateLayoutParams(1, 0.0f, 0.0f, 0, 1));
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

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public void onFinishInflate(ViewGroup viewGroup) {
        super.onFinishInflate(viewGroup);
        final View viewFindViewByAreaId = findViewByAreaId(viewGroup, R.id.area_head);
        View viewFindViewByAreaId2 = findViewByAreaId(viewGroup, R.id.area_content);
        if (viewFindViewByAreaId2 == null || viewFindViewByAreaId2.hasOnClickListeners()) {
            return;
        }
        viewFindViewByAreaId2.setOnClickListener(new View.OnClickListener() { // from class: miuix.flexible.template.SimpleItemMarkTemplate.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                View view2 = viewFindViewByAreaId;
                if (view2 != null) {
                    view2.performClick();
                }
            }
        });
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
