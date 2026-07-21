package com.android.deskclock.worldclock;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.android.deskclock.R;
import miuix.flexible.template.AbstractMarkTemplate;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes.dex */
public class TimezoneTemplate extends AbstractMarkTemplate {
    private static final SparseArray<HyperCellLayout.LayoutParams> LARGE_PARAMS;
    private static final SparseArray<HyperCellLayout.LayoutParams> NORMAL_PARAMS;

    static {
        SparseArray<HyperCellLayout.LayoutParams> sparseArray = new SparseArray<>();
        NORMAL_PARAMS = sparseArray;
        sparseArray.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 0).setMargin(0, 0, 0, 0));
        sparseArray.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 1).setMargin(0, 4, 0, 0));
        sparseArray.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 2).setMargin(10, 0, 0, 0));
        sparseArray.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 16, 3).setMargin(10, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray2 = new SparseArray<>();
        LARGE_PARAMS = sparseArray2;
        sparseArray2.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 0).setMargin(0, 0, 0, 0));
        sparseArray2.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 2).setMargin(0, 4, 0, 0));
        sparseArray2.put(R.id.area_end, generateLayoutParams(2, 0.0f, 0.0f, 0, 1).setMargin(0, 4, 0, 0));
        sparseArray2.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 16, 3).setMargin(10, 0, 0, 0));
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
            HyperCellLayout.LayoutParams layoutParams = (HyperCellLayout.LayoutParams) childAt.getLayoutParams();
            HyperCellLayout.LayoutParams layoutParams2 = getLayoutParams(childAt);
            setGravity(layoutParams, layoutParams2);
            setMargin(layoutParams, layoutParams2);
            setPriority(layoutParams, layoutParams2);
        }
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate
    public HyperCellLayout.LayoutParams getLayoutParams(View view) {
        HyperCellLayout.LayoutParams layoutParams;
        HyperCellLayout.LayoutParams layoutParams2 = (HyperCellLayout.LayoutParams) view.getLayoutParams();
        int areaId = layoutParams2.getAreaId();
        if (getLevel() == 1) {
            layoutParams = NORMAL_PARAMS.get(areaId);
        } else {
            layoutParams = LARGE_PARAMS.get(areaId);
        }
        return layoutParams == null ? layoutParams2 : layoutParams;
    }
}
