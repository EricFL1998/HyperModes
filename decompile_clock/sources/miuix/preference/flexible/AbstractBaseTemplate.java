package miuix.preference.flexible;

import android.R;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import miuix.core.util.MiuixUIUtils;
import miuix.flexible.template.AbstractMarkTemplate;
import miuix.flexible.template.level.FontLevelSupplier;
import miuix.flexible.template.level.LevelSupplier;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractBaseTemplate extends AbstractMarkTemplate {
    public static final int BIG_ICON_WIDTH_THRESHOLD_DP = 45;
    private int mSummaryPreVisibility;
    private int mTitlePreVisibility;
    public int mCurrentLevel = -1;
    public boolean mHasTitle = false;
    public boolean mHasSummary = false;
    public boolean mHasWidget = false;
    public boolean mIsBigIcon = false;

    public abstract void checkView(ViewGroup viewGroup);

    public abstract int onLargeLayoutSelected();

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public void onAttachedToWindow(ViewGroup viewGroup) {
        super.onAttachedToWindow(viewGroup);
        refreshLayout(viewGroup);
    }

    public void refreshLayout(ViewGroup viewGroup) {
        checkMainViewVisibility(viewGroup);
        checkView(viewGroup);
        checkAndReLayout(viewGroup);
    }

    public void storeVisibilityBeforeUpdate(PreferenceViewHolder preferenceViewHolder) {
        TextView textView = (TextView) preferenceViewHolder.findViewById(R.id.title);
        this.mTitlePreVisibility = textView != null ? textView.getVisibility() : 8;
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(R.id.summary);
        this.mSummaryPreVisibility = textView2 != null ? textView2.getVisibility() : 8;
    }

    public void refreshLayoutIfVisibleChanged(PreferenceViewHolder preferenceViewHolder) {
        TextView textView = (TextView) preferenceViewHolder.findViewById(R.id.title);
        int visibility = textView != null ? textView.getVisibility() : 8;
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(R.id.summary);
        int visibility2 = textView2 != null ? textView2.getVisibility() : 8;
        if (this.mTitlePreVisibility == visibility && this.mSummaryPreVisibility == visibility2) {
            return;
        }
        refreshLayout((ViewGroup) preferenceViewHolder.itemView);
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public void onDetachedFromWindow(ViewGroup viewGroup) {
        super.onDetachedFromWindow(viewGroup);
    }

    public int onNormalLayoutSelected() {
        boolean z = this.mHasTitle;
        if (!z || this.mHasSummary) {
            return (z || !this.mHasSummary) ? 10000 : 10002;
        }
        return 10001;
    }

    private void checkMainViewVisibility(ViewGroup viewGroup) {
        View viewFindViewByAreaId = findViewByAreaId(viewGroup, miuix.flexible.R.id.area_head);
        View viewFindViewByAreaId2 = findViewByAreaId(viewGroup, miuix.flexible.R.id.area_title);
        View viewFindViewByAreaId3 = findViewByAreaId(viewGroup, miuix.flexible.R.id.area_content);
        View viewFindViewByAreaId4 = findViewByAreaId(viewGroup, miuix.flexible.R.id.area_end);
        if (viewFindViewByAreaId != null && viewFindViewByAreaId.getVisibility() == 0 && (viewFindViewByAreaId instanceof ImageView)) {
            this.mIsBigIcon = MiuixUIUtils.px2dp(this.mContext, (float) viewFindViewByAreaId.getWidth()) > 45;
        }
        if (viewFindViewByAreaId2 != null) {
            this.mHasTitle = viewFindViewByAreaId2.getVisibility() == 0;
        }
        if (viewFindViewByAreaId3 != null) {
            this.mHasSummary = viewFindViewByAreaId3.getVisibility() == 0;
        }
        if (viewFindViewByAreaId4 != null) {
            this.mHasWidget = viewFindViewByAreaId4.getVisibility() == 0;
        }
    }

    private void checkAndReLayout(ViewGroup viewGroup) {
        int level = getLevel();
        if (this.mCurrentLevel != level) {
            this.mCurrentLevel = level;
            onPreBuildViewTree(viewGroup);
            buildViewTree(viewGroup);
            viewGroup.requestLayout();
            applyLevel();
        }
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate
    public void onAddAuxiliaryViews(ViewGroup viewGroup) {
        super.onAddAuxiliaryViews(viewGroup);
        View view = new View(this.mContext);
        view.setWillNotDraw(true);
        viewGroup.addView(view, generateAuxiliaryLayoutParams(miuix.flexible.R.id.view_auxiliary));
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
        }
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate
    public HyperCellLayout.LayoutParams getLayoutParams(View view) {
        HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(view);
        int areaId = childViewLayoutParamsSafe.getAreaId();
        SparseArray<HyperCellLayout.LayoutParams> levelParams = PreferenceMarkLevel.getLevelParams(getLevel());
        HyperCellLayout.LayoutParams layoutParams = levelParams != null ? levelParams.get(areaId) : null;
        return layoutParams == null ? childViewLayoutParamsSafe : layoutParams;
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public LevelSupplier createLevelSupplier() {
        return new FontLevelSupplier(this.mContext) { // from class: miuix.preference.flexible.AbstractBaseTemplate.1
            @Override // miuix.flexible.template.level.FontLevelSupplier, miuix.flexible.template.level.LevelSupplier
            public int getLevel() {
                if (super.getLevel() == 1) {
                    return AbstractBaseTemplate.this.onNormalLayoutSelected();
                }
                return AbstractBaseTemplate.this.onLargeLayoutSelected();
            }
        };
    }
}
