package miuix.preference.flexible;

import android.util.SparseArray;
import android.view.ViewGroup;
import miuix.flexible.R;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes3.dex */
public class RadioButtonPreferenceTemplate extends AbstractBaseTemplate {
    public static final int LEVEL_LARGE_RADIO_BUTTON_BASE = 60000;
    public static final int LEVEL_LARGE_RADIO_BUTTON_FULL_MULTI_TITLE = 60001;
    public static final int LEVEL_LARGE_RADIO_BUTTON_FULL_SINGLE_TITLE = 60002;
    public static final int LEVEL_LARGE_RADIO_BUTTON_MULTI_TITLE_ONLY = 60004;
    public static final int LEVEL_LARGE_RADIO_BUTTON_SINGLE_TITLE_ONLY = 60003;
    public static final int LEVEL_LARGE_RADIO_BUTTON_SUMMARY_ONLY = 60005;
    public static final int LEVEL_NORMAL_RADIO_BUTTON_BASE = 30000;
    public static final int LEVEL_NORMAL_RADIO_BUTTON_FULL = 30001;
    public static final int LEVEL_NORMAL_RADIO_BUTTON_ONLY_SUMMARY = 30003;
    public static final int LEVEL_NORMAL_RADIO_BUTTON_ONLY_TITLE = 30002;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_RADIO_BUTTON_FULL_MULTI_TITLE;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_RADIO_BUTTON_FULL_SINGLE_TITLE;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_RADIO_BUTTON_MULTI_TITLE_ONLY;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_RADIO_BUTTON_SINGLE_TITLE_ONLY;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_RADIO_BUTTON_SUMMARY_ONLY;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_NORMAL_RADIO_BUTTON_FULL;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_NORMAL_RADIO_BUTTON_ONLY_SUMMARY;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_NORMAL_RADIO_BUTTON_ONLY_TITLE;

    @Override // miuix.preference.flexible.AbstractBaseTemplate
    public void checkView(ViewGroup viewGroup) {
    }

    static {
        SparseArray<HyperCellLayout.LayoutParams> sparseArray = new SparseArray<>();
        PARAMS_NORMAL_RADIO_BUTTON_FULL = sparseArray;
        sparseArray.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 1));
        sparseArray.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 17, 2, 0, 0, 8, 0));
        sparseArray.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 3, 0, 0, 16, 0));
        sparseArray.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 4, 0, 14, 0, 0));
        sparseArray.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 0, 0, 14));
        sparseArray.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray2 = new SparseArray<>();
        PARAMS_NORMAL_RADIO_BUTTON_ONLY_TITLE = sparseArray2;
        sparseArray2.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 1));
        sparseArray2.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 17, 2, 0, 0, 8, 0));
        sparseArray2.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 3, 0, 0, 16, 0));
        sparseArray2.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 4, 0, 14, 0, 14));
        sparseArray2.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 0, 0, 0));
        sparseArray2.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray3 = new SparseArray<>();
        PARAMS_NORMAL_RADIO_BUTTON_ONLY_SUMMARY = sparseArray3;
        sparseArray3.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 1));
        sparseArray3.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 17, 2, 0, 0, 8, 0));
        sparseArray3.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 3, 0, 0, 16, 0));
        sparseArray3.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 4, 0, 0, 0, 0));
        sparseArray3.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 14, 0, 14));
        sparseArray3.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray4 = new SparseArray<>();
        PARAMS_LARGE_RADIO_BUTTON_FULL_MULTI_TITLE = sparseArray4;
        sparseArray4.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 1));
        sparseArray4.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 17, 2, 0, 0, 8, 0));
        sparseArray4.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 3, 0, 0, 16, 0));
        sparseArray4.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 4, 0, 14, 0, 10));
        sparseArray4.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 0, 0, 14));
        sparseArray4.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray5 = new SparseArray<>();
        PARAMS_LARGE_RADIO_BUTTON_FULL_SINGLE_TITLE = sparseArray5;
        sparseArray5.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 8, 0));
        sparseArray5.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 2));
        sparseArray5.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 3, 0, 0, 16, 0));
        sparseArray5.put(R.id.area_title, generateLayoutParams(3, 0.0f, 1.0f, 16, 4, 0, 14, 0, 10));
        sparseArray5.put(R.id.area_content, generateLayoutParams(2, 0.0f, 1.0f, 0, 5, 0, 0, 0, 14));
        sparseArray5.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 6, 10, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray6 = new SparseArray<>();
        PARAMS_LARGE_RADIO_BUTTON_SINGLE_TITLE_ONLY = sparseArray6;
        sparseArray6.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 8, 0));
        sparseArray6.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 2));
        sparseArray6.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 3, 0, 0, 16, 0));
        sparseArray6.put(R.id.area_title, generateLayoutParams(3, 0.0f, 1.0f, 16, 4, 0, 14, 0, 14));
        sparseArray6.put(R.id.area_content, generateLayoutParams(2, 0.0f, 1.0f, 0, 5, 0, 0, 0, 0));
        sparseArray6.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 6, 10, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray7 = new SparseArray<>();
        PARAMS_LARGE_RADIO_BUTTON_MULTI_TITLE_ONLY = sparseArray7;
        sparseArray7.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 1));
        sparseArray7.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 17, 2, 0, 0, 8, 0));
        sparseArray7.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 3, 0, 0, 16, 0));
        sparseArray7.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 4, 0, 14, 0, 14));
        sparseArray7.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 0, 0, 0));
        sparseArray7.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray8 = new SparseArray<>();
        PARAMS_LARGE_RADIO_BUTTON_SUMMARY_ONLY = sparseArray8;
        sparseArray8.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 1));
        sparseArray8.put(R.id.area_head2, generateLayoutParams(1, 0.0f, 0.0f, 17, 2, 0, 0, 8, 0));
        sparseArray8.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 3, 0, 0, 16, 0));
        sparseArray8.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 4, 0, 0, 0, 0));
        sparseArray8.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 5, 0, 14, 0, 14));
        sparseArray8.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 6, 8, 0, 0, 0));
        PreferenceMarkLevel.registerLevelParams(LEVEL_NORMAL_RADIO_BUTTON_FULL, sparseArray);
        PreferenceMarkLevel.registerLevelParams(LEVEL_NORMAL_RADIO_BUTTON_ONLY_TITLE, sparseArray2);
        PreferenceMarkLevel.registerLevelParams(LEVEL_NORMAL_RADIO_BUTTON_ONLY_SUMMARY, sparseArray3);
        PreferenceMarkLevel.registerLevelParams(LEVEL_LARGE_RADIO_BUTTON_FULL_MULTI_TITLE, sparseArray4);
        PreferenceMarkLevel.registerLevelParams(LEVEL_LARGE_RADIO_BUTTON_FULL_SINGLE_TITLE, sparseArray5);
        PreferenceMarkLevel.registerLevelParams(LEVEL_LARGE_RADIO_BUTTON_SINGLE_TITLE_ONLY, sparseArray6);
        PreferenceMarkLevel.registerLevelParams(LEVEL_LARGE_RADIO_BUTTON_MULTI_TITLE_ONLY, sparseArray7);
        PreferenceMarkLevel.registerLevelParams(LEVEL_LARGE_RADIO_BUTTON_SUMMARY_ONLY, sparseArray8);
    }

    @Override // miuix.preference.flexible.AbstractBaseTemplate
    public int onNormalLayoutSelected() {
        if (!this.mHasTitle || this.mHasSummary) {
            return (this.mHasTitle || !this.mHasSummary) ? LEVEL_NORMAL_RADIO_BUTTON_FULL : LEVEL_NORMAL_RADIO_BUTTON_ONLY_SUMMARY;
        }
        return LEVEL_NORMAL_RADIO_BUTTON_ONLY_TITLE;
    }

    @Override // miuix.preference.flexible.AbstractBaseTemplate
    public int onLargeLayoutSelected() {
        if (this.mHasTitle && this.mHasSummary) {
            return LEVEL_LARGE_RADIO_BUTTON_FULL_MULTI_TITLE;
        }
        if (!this.mHasTitle || this.mHasSummary) {
            return (this.mHasTitle || !this.mHasSummary) ? LEVEL_LARGE_RADIO_BUTTON_FULL_MULTI_TITLE : LEVEL_LARGE_RADIO_BUTTON_SUMMARY_ONLY;
        }
        return LEVEL_LARGE_RADIO_BUTTON_MULTI_TITLE_ONLY;
    }
}
