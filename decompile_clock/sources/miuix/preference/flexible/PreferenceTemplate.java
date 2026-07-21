package miuix.preference.flexible;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import miuix.core.util.MiuixUIUtils;
import miuix.flexible.R;
import miuix.flexible.template.AbstractMarkTemplate;
import miuix.flexible.template.level.FontLevelSupplier;
import miuix.flexible.template.level.LevelSupplier;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes3.dex */
public class PreferenceTemplate extends AbstractMarkTemplate {
    private static final int BIG_ICON_WIDTH_THRESHOLD_DP = 45;
    private static final int LEVEL_LARGE_ALL = 20000;
    private static final int LEVEL_LARGE_ALL_TITLE_MULTI = 21002;
    private static final int LEVEL_LARGE_ALL_TITLE_SINGLE = 21001;
    private static final int LEVEL_LARGE_ALL_WIDGET = 21000;
    private static final int LEVEL_LARGE_ALL_WIDGET_TEXT_MULTI = 21004;
    private static final int LEVEL_LARGE_ALL_WIDGET_TEXT_SINGLE = 21003;
    private static final int LEVEL_LARGE_MULTI_TITLE_WIDGET_TEXT = 22002;
    private static final int LEVEL_LARGE_ONLY_ONE_TEXT = 22000;
    private static final int LEVEL_LARGE_RADIO_SINGLE_TITLE = 23000;
    private static final int LEVEL_LARGE_SINGLE_TITLE_WIDGET_TEXT = 22001;
    private static final int LEVEL_NORMAL_ALL = 10000;
    private static final int LEVEL_NORMAL_CONTENT = 10002;
    private static final int LEVEL_NORMAL_TITLE = 10001;
    private static final SparseArray<HyperCellLayout.LayoutParams> NORMAL_ALL_PARAMS;
    private static final SparseArray<HyperCellLayout.LayoutParams> NORMAL_CONTENT_PARAMS;
    private static final SparseArray<HyperCellLayout.LayoutParams> NORMAL_TITLE_PARAMS;
    private static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_ALL_TITLE_MULTI;
    private static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_ALL_TITLE_SINGLE;
    private static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_ALL_WIDGET_TEXT_MULTI;
    private static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_ALL_WIDGET_TEXT_SINGLE;
    private static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_MULTI_TITLE_WIDGET_TEXT;
    private static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_ONLY_ONE_TEXT_PARAMS;
    private static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_RADIO_SINGLE_TITLE;
    private static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_SINGLE_TITLE_WIDGET_TEXT;
    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    private boolean mBigIcon = false;
    private boolean mHasTitle = false;
    private boolean mHasContent = false;
    private boolean mHasWidget = false;
    private boolean mHead2Radio = false;
    private boolean mWidgetIsText = false;
    private boolean mHasSpinner = false;
    private int mCurrentLevel = -1;
    private int mTitleLineCount = 0;

    static {
        SparseArray<HyperCellLayout.LayoutParams> sparseArray = new SparseArray<>();
        NORMAL_ALL_PARAMS = sparseArray;
        sparseArray.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 0));
        sparseArray.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 17, 1, 0, 0, 8, 0));
        sparseArray.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 2, 0, 0, 16, 0));
        sparseArray.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 3, 0, 14, 0, 0));
        sparseArray.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 14));
        sparseArray.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        sparseArray.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray2 = new SparseArray<>();
        NORMAL_TITLE_PARAMS = sparseArray2;
        sparseArray2.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 0));
        sparseArray2.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 17, 1, 0, 0, 8, 0));
        sparseArray2.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 2, 0, 0, 16, 0));
        sparseArray2.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 3, 0, 14, 0, 14));
        sparseArray2.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 0));
        sparseArray2.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        sparseArray2.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray3 = new SparseArray<>();
        NORMAL_CONTENT_PARAMS = sparseArray3;
        sparseArray3.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 0));
        sparseArray3.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 17, 1, 0, 0, 8, 0));
        sparseArray3.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 2, 0, 0, 16, 0));
        sparseArray3.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 3, 0, 0, 0, 0));
        sparseArray3.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 14, 0, 14));
        sparseArray3.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        sparseArray3.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray4 = new SparseArray<>();
        PARAMS_LARGE_ALL_TITLE_SINGLE = sparseArray4;
        sparseArray4.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 0));
        sparseArray4.put(R.id.area_head2, generateLayoutParams(3, 0.0f, 0.0f, 16, 1, 0, 0, 8, 0));
        sparseArray4.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 2, 0, 0, 16, 0));
        sparseArray4.put(R.id.area_title, generateLayoutParams(3, 0.0f, 0.0f, 16, 3, 0, 14, 0, 10));
        sparseArray4.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 14));
        sparseArray4.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 5, 10, 0, 0, 0));
        sparseArray4.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray5 = new SparseArray<>();
        PARAMS_LARGE_ALL_TITLE_MULTI = sparseArray5;
        sparseArray5.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray5.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 8, 0));
        sparseArray5.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 2, 0, 0, 16, 0));
        sparseArray5.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 3, 0, 14, 0, 10));
        sparseArray5.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 14));
        sparseArray5.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 5, 10, 0, 0, 0));
        sparseArray5.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 16, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray6 = new SparseArray<>();
        PARAMS_LARGE_ALL_WIDGET_TEXT_SINGLE = sparseArray6;
        sparseArray6.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 0));
        sparseArray6.put(R.id.area_head2, generateLayoutParams(3, 0.0f, 0.0f, 16, 1, 0, 0, 8, 0));
        sparseArray6.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 2, 0, 0, 16, 0));
        sparseArray6.put(R.id.area_title, generateLayoutParams(3, 0.0f, 0.0f, 16, 3, 0, 14, 0, 4));
        sparseArray6.put(R.id.area_end, generateLayoutParams(2, 0.0f, 1.0f, 0, 4, 0, 0, 0, 0));
        sparseArray6.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 4, 0, 14));
        sparseArray6.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 10, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray7 = new SparseArray<>();
        PARAMS_LARGE_ALL_WIDGET_TEXT_MULTI = sparseArray7;
        sparseArray7.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray7.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 8, 0));
        sparseArray7.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 2, 0, 0, 16, 0));
        sparseArray7.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 3, 0, 14, 0, 4));
        sparseArray7.put(R.id.area_end, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 0));
        sparseArray7.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 4, 0, 14));
        sparseArray7.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 10, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray8 = new SparseArray<>();
        PARAMS_LARGE_ONLY_ONE_TEXT_PARAMS = sparseArray8;
        sparseArray8.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray8.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 8, 0));
        sparseArray8.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 2, 0, 0, 16, 0));
        sparseArray8.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 3, 0, 14, 0, 14));
        sparseArray8.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 14, 0, 14));
        sparseArray8.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 5, 10, 0, 0, 0));
        sparseArray8.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray9 = new SparseArray<>();
        PARAMS_LARGE_SINGLE_TITLE_WIDGET_TEXT = sparseArray9;
        sparseArray9.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 0));
        sparseArray9.put(R.id.area_head2, generateLayoutParams(3, 0.0f, 0.0f, 16, 1, 0, 0, 8, 0));
        sparseArray9.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 2, 0, 0, 16, 0));
        sparseArray9.put(R.id.area_title, generateLayoutParams(3, 0.0f, 0.0f, 16, 3, 0, 14, 0, 4));
        sparseArray9.put(R.id.area_end, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 14));
        sparseArray9.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 4, 0, 14));
        sparseArray9.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray10 = new SparseArray<>();
        PARAMS_LARGE_MULTI_TITLE_WIDGET_TEXT = sparseArray10;
        sparseArray10.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray10.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 8, 0));
        sparseArray10.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 2, 0, 0, 16, 0));
        sparseArray10.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 3, 0, 14, 0, 4));
        sparseArray10.put(R.id.area_end, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 14));
        sparseArray10.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 4, 0, 14));
        sparseArray10.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray11 = new SparseArray<>();
        PARAMS_LARGE_RADIO_SINGLE_TITLE = sparseArray11;
        sparseArray11.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 16, 0, 0, 0, 8, 0));
        sparseArray11.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 1));
        sparseArray11.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 2, 0, 0, 16, 0));
        sparseArray11.put(R.id.area_title, generateLayoutParams(3, 0.0f, 1.0f, 16, 3, 0, 14, 0, 0));
        sparseArray11.put(R.id.area_content, generateLayoutParams(2, 0.0f, 1.0f, 0, 4, 0, 10, 0, 14));
        sparseArray11.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 5, 10, 0, 0, 0));
        sparseArray11.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public void onAttachedToWindow(final ViewGroup viewGroup) {
        super.onAttachedToWindow(viewGroup);
        final View viewFindViewByAreaId = findViewByAreaId(viewGroup, R.id.area_head);
        final View viewFindViewByAreaId2 = findViewByAreaId(viewGroup, R.id.area_title);
        final View viewFindViewByAreaId3 = findViewByAreaId(viewGroup, R.id.area_content);
        final View viewFindViewByAreaId4 = findViewByAreaId(viewGroup, R.id.area_end);
        final View viewFindViewByAreaId5 = findViewByAreaId(viewGroup, R.id.area_head2);
        final View viewFindViewByAreaId6 = findViewByAreaId(viewGroup, R.id.area_end2);
        this.mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: miuix.preference.flexible.PreferenceTemplate$$ExternalSyntheticLambda0
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public final boolean onPreDraw() {
                return this.f$0.m1930x94c1fbb7(viewFindViewByAreaId, viewFindViewByAreaId2, viewFindViewByAreaId3, viewFindViewByAreaId4, viewFindViewByAreaId5, viewFindViewByAreaId6, viewGroup);
            }
        };
        viewGroup.getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    }

    /* JADX INFO: renamed from: lambda$onAttachedToWindow$0$miuix-preference-flexible-PreferenceTemplate, reason: not valid java name */
    /* synthetic */ boolean m1930x94c1fbb7(View view, View view2, View view3, View view4, View view5, View view6, ViewGroup viewGroup) {
        detectView(view, view2, view3, view4, view5, view6);
        return changeLayout(viewGroup);
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public void onDetachedFromWindow(ViewGroup viewGroup) {
        if (this.mOnPreDrawListener != null) {
            viewGroup.getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
            this.mOnPreDrawListener = null;
        }
        super.onDetachedFromWindow(viewGroup);
    }

    private void detectView(View view, View view2, View view3, View view4, View view5, View view6) {
        boolean z = view != null && view.getVisibility() == 0;
        boolean z2 = view5 != null && view5.getVisibility() == 0;
        boolean z3 = view6 != null && view6.getVisibility() == 0;
        if (view2 != null) {
            this.mHasTitle = view2.getVisibility() == 0;
        }
        if (view3 != null) {
            this.mHasContent = view3.getVisibility() == 0;
        }
        if (view4 != null) {
            this.mHasWidget = view4.getVisibility() == 0;
        }
        if (z && (view instanceof ImageView)) {
            this.mBigIcon = MiuixUIUtils.px2dp(this.mContext, (float) view.getWidth()) > 45;
        }
        if (this.mHasTitle && (view2 instanceof TextView)) {
            this.mTitleLineCount = ((TextView) view2).getLineCount();
        }
        if (z2) {
            this.mHead2Radio = view5 instanceof RadioButton;
        }
        if (this.mHasWidget && (view4 instanceof LinearLayout)) {
            View childAt = ((LinearLayout) view4).getChildAt(0);
            this.mWidgetIsText = childAt != null && childAt.getClass() == TextView.class;
        }
        if (z3 && (view6 instanceof LinearLayout)) {
            this.mHasSpinner = ((LinearLayout) view6).getChildAt(0) instanceof Spinner;
        }
    }

    private boolean changeLayout(ViewGroup viewGroup) {
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

    @Override // miuix.flexible.template.AbstractMarkTemplate
    public void onAddAuxiliaryViews(ViewGroup viewGroup) {
        super.onAddAuxiliaryViews(viewGroup);
        View view = new View(this.mContext);
        view.setWillNotDraw(true);
        viewGroup.addView(view, generateAuxiliaryLayoutParams(R.id.view_auxiliary));
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
        HyperCellLayout.LayoutParams layoutParams;
        HyperCellLayout.LayoutParams childViewLayoutParamsSafe = getChildViewLayoutParamsSafe(view);
        int areaId = childViewLayoutParamsSafe.getAreaId();
        int level = getLevel();
        if (level == 10001) {
            layoutParams = NORMAL_TITLE_PARAMS.get(areaId);
        } else if (level == 10002) {
            layoutParams = NORMAL_CONTENT_PARAMS.get(areaId);
        } else if (level == 10000) {
            layoutParams = NORMAL_ALL_PARAMS.get(areaId);
        } else if (level == 22001) {
            layoutParams = PARAMS_LARGE_SINGLE_TITLE_WIDGET_TEXT.get(areaId);
        } else if (level == 22002) {
            layoutParams = PARAMS_LARGE_MULTI_TITLE_WIDGET_TEXT.get(areaId);
        } else if (level == LEVEL_LARGE_ONLY_ONE_TEXT) {
            layoutParams = PARAMS_LARGE_ONLY_ONE_TEXT_PARAMS.get(areaId);
        } else if (level == 21003) {
            layoutParams = PARAMS_LARGE_ALL_WIDGET_TEXT_SINGLE.get(areaId);
        } else if (level == 21004) {
            layoutParams = PARAMS_LARGE_ALL_WIDGET_TEXT_MULTI.get(areaId);
        } else if (level == 21001) {
            layoutParams = PARAMS_LARGE_ALL_TITLE_SINGLE.get(areaId);
        } else if (level == 21002) {
            layoutParams = PARAMS_LARGE_ALL_TITLE_MULTI.get(areaId);
        } else {
            layoutParams = level == LEVEL_LARGE_RADIO_SINGLE_TITLE ? PARAMS_LARGE_RADIO_SINGLE_TITLE.get(areaId) : null;
        }
        return layoutParams == null ? childViewLayoutParamsSafe : layoutParams;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int selectNormalLayoutLevel() {
        boolean z = this.mHasTitle;
        if (!z || this.mHasContent) {
            return (z || !this.mHasContent) ? 10000 : 10002;
        }
        return 10001;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int selectLargeLayoutLevel() {
        boolean z = this.mHasTitle;
        boolean z2 = false;
        boolean z3 = z && this.mHasContent;
        boolean z4 = this.mWidgetIsText;
        boolean z5 = z4 && this.mTitleLineCount <= 1;
        boolean z6 = z4 && this.mTitleLineCount > 1;
        boolean z7 = this.mHasSpinner;
        boolean z8 = z7 && this.mTitleLineCount <= 1;
        if (z7 && this.mTitleLineCount > 1) {
            z2 = true;
        }
        if (!z3) {
            if (z && z5) {
                return 22001;
            }
            if (z && z6) {
                return 22002;
            }
            if (z8) {
                return 22001;
            }
            if (z2) {
                return 22002;
            }
            return LEVEL_LARGE_ONLY_ONE_TEXT;
        }
        boolean z9 = this.mHasWidget;
        if (z9 && z5) {
            return 21003;
        }
        if (z9 && z6) {
            return 21004;
        }
        if (this.mHead2Radio && this.mTitleLineCount <= 1) {
            return LEVEL_LARGE_RADIO_SINGLE_TITLE;
        }
        int i = this.mTitleLineCount;
        if (i <= 1 && this.mBigIcon) {
            return 21002;
        }
        if (z8) {
            return 21003;
        }
        if (z2) {
            return 21004;
        }
        return i <= 1 ? 21001 : 21002;
    }

    @Override // miuix.flexible.template.AbstractMarkTemplate, miuix.flexible.template.IHyperCellTemplate
    public LevelSupplier createLevelSupplier() {
        return new FontLevelSupplier(this.mContext) { // from class: miuix.preference.flexible.PreferenceTemplate.1
            @Override // miuix.flexible.template.level.FontLevelSupplier, miuix.flexible.template.level.LevelSupplier
            public int getLevel() {
                return super.getLevel() == 1 ? PreferenceTemplate.this.selectNormalLayoutLevel() : PreferenceTemplate.this.selectLargeLayoutLevel();
            }
        };
    }
}
