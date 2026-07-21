package miuix.preference.flexible;

import android.util.SparseArray;
import java.util.HashMap;
import java.util.Map;
import miuix.flexible.R;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes3.dex */
public class PreferenceMarkLevel {
    private static final int LEVEL_LARGE_FULL_MULTI = 22000;
    private static final int LEVEL_LARGE_FULL_SINGLE = 21000;
    public static final int LEVEL_LARGE_FULL_TITLE_MULTI = 22001;
    public static final int LEVEL_LARGE_FULL_TITLE_MULTI_TEXT = 22002;
    public static final int LEVEL_LARGE_FULL_TITLE_SINGLE = 21001;
    public static final int LEVEL_LARGE_FULL_TITLE_SINGLE_TEXT = 21002;
    private static final int LEVEL_LARGE_FULL_VISIBLE = 20000;
    public static final int LEVEL_LARGE_ONLY_SUMMARY = 20002;
    public static final int LEVEL_LARGE_ONLY_TITLE = 20001;
    public static final int LEVEL_LARGE_SUMMARY_WIDGET_TEXT = 21005;
    public static final int LEVEL_LARGE_TITLE_MULTI_WIDGET_TEXT = 21004;
    public static final int LEVEL_LARGE_TITLE_SINGLE_WIDGET_TEXT = 21003;
    public static final int LEVEL_NORMAL_FULL_VISIBLE = 10000;
    public static final int LEVEL_NORMAL_ONLY_SUMMARY = 10002;
    public static final int LEVEL_NORMAL_ONLY_TITLE = 10001;
    private static final Map<Integer, SparseArray<HyperCellLayout.LayoutParams>> MAP_LEVEL_PARAMS;
    protected static final int NOT_SET = Integer.MAX_VALUE;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_FULL_TITLE_MULTI;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_FULL_TITLE_MULTI_TEXT;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_FULL_TITLE_SINGLE;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_FULL_TITLE_SINGLE_TEXT;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_ONLY_SUMMARY;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_ONLY_TITLE;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_SUMMARY_WIDGET_TEXT;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_TITLE_MULTI_WIDGET_TEXT;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_LARGE_TITLE_SINGLE_WIDGET_TEXT;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_NORMAL_FULL_VISIBLE;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_NORMAL_ONLY_SUMMARY;
    public static final SparseArray<HyperCellLayout.LayoutParams> PARAMS_NORMAL_ONLY_TITLE;

    static {
        SparseArray<HyperCellLayout.LayoutParams> sparseArray = new SparseArray<>();
        PARAMS_NORMAL_FULL_VISIBLE = sparseArray;
        sparseArray.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 0));
        sparseArray.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 1, 0, 0, 16, 0));
        sparseArray.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2, 0, 14, 0, 0));
        sparseArray.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 0, 0, 14));
        sparseArray.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 4, 8, 0, 0, 0));
        sparseArray.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray2 = new SparseArray<>();
        PARAMS_NORMAL_ONLY_TITLE = sparseArray2;
        sparseArray2.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 0));
        sparseArray2.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 1, 0, 0, 16, 0));
        sparseArray2.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2, 0, 14, 0, 14));
        sparseArray2.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 0, 0, 0));
        sparseArray2.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 4, 8, 0, 0, 0));
        sparseArray2.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray3 = new SparseArray<>();
        PARAMS_NORMAL_ONLY_SUMMARY = sparseArray3;
        sparseArray3.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 0.0f, 0, 0));
        sparseArray3.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 17, 1, 0, 0, 16, 0));
        sparseArray3.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2, 0, 0, 0, 0));
        sparseArray3.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 14, 0, 14));
        sparseArray3.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 17, 4, 8, 0, 0, 0));
        sparseArray3.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray4 = new SparseArray<>();
        PARAMS_LARGE_FULL_TITLE_SINGLE = sparseArray4;
        sparseArray4.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 0));
        sparseArray4.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 1, 0, 0, 16, 0));
        sparseArray4.put(R.id.area_title, generateLayoutParams(3, 0.0f, 0.0f, 16, 2, 0, 14, 0, 10));
        sparseArray4.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 0, 0, 14));
        sparseArray4.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 4, 10, 0, 0, 0));
        sparseArray4.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray5 = new SparseArray<>();
        PARAMS_LARGE_FULL_TITLE_MULTI = sparseArray5;
        sparseArray5.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray5.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 16, 0));
        sparseArray5.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2, 0, 14, 0, 10));
        sparseArray5.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 0, 0, 14));
        sparseArray5.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 4, 10, 0, 0, 0));
        sparseArray5.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 16, 5, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray6 = new SparseArray<>();
        PARAMS_LARGE_ONLY_TITLE = sparseArray6;
        sparseArray6.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray6.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 16, 0));
        sparseArray6.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2, 0, 14, 0, 14));
        sparseArray6.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 0, 0, 0));
        sparseArray6.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 4, 10, 0, 0, 0));
        sparseArray6.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray7 = new SparseArray<>();
        PARAMS_LARGE_ONLY_SUMMARY = sparseArray7;
        sparseArray7.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray7.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 16, 0));
        sparseArray7.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2, 0, 0, 0, 0));
        sparseArray7.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 14, 0, 14));
        sparseArray7.put(R.id.area_end, generateLayoutParams(1, 0.0f, 0.0f, 16, 4, 10, 0, 0, 0));
        sparseArray7.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray8 = new SparseArray<>();
        PARAMS_LARGE_FULL_TITLE_SINGLE_TEXT = sparseArray8;
        sparseArray8.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 0));
        sparseArray8.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 1, 0, 0, 16, 0));
        sparseArray8.put(R.id.area_title, generateLayoutParams(3, 0.0f, 0.0f, 16, 2, 0, 14, 0, 4));
        sparseArray8.put(R.id.area_end, generateLayoutParams(2, 0.0f, 1.0f, 0, 3, 0, 0, 0, 0));
        sparseArray8.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 4, 0, 14));
        sparseArray8.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 10, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray9 = new SparseArray<>();
        PARAMS_LARGE_FULL_TITLE_MULTI_TEXT = sparseArray9;
        sparseArray9.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray9.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 16, 0));
        sparseArray9.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2, 0, 14, 0, 4));
        sparseArray9.put(R.id.area_end, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 0, 0, 0));
        sparseArray9.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 4, 0, 14));
        sparseArray9.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 10, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray10 = new SparseArray<>();
        PARAMS_LARGE_TITLE_SINGLE_WIDGET_TEXT = sparseArray10;
        sparseArray10.put(R.id.view_auxiliary, generateLayoutParams(2, 0.0f, 1.0f, 0, 0));
        sparseArray10.put(R.id.area_head, generateLayoutParams(3, 0.0f, 0.0f, 16, 1, 0, 0, 16, 0));
        sparseArray10.put(R.id.area_title, generateLayoutParams(3, 0.0f, 0.0f, 16, 2, 0, 14, 0, 4));
        sparseArray10.put(R.id.area_end, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 0, 0, 14));
        sparseArray10.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 0));
        sparseArray10.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray11 = new SparseArray<>();
        PARAMS_LARGE_TITLE_MULTI_WIDGET_TEXT = sparseArray11;
        sparseArray11.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray11.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 16, 0));
        sparseArray11.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2, 0, 14, 0, 4));
        sparseArray11.put(R.id.area_end, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 0, 0, 14));
        sparseArray11.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 0));
        sparseArray11.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        SparseArray<HyperCellLayout.LayoutParams> sparseArray12 = new SparseArray<>();
        PARAMS_LARGE_SUMMARY_WIDGET_TEXT = sparseArray12;
        sparseArray12.put(R.id.view_auxiliary, generateLayoutParams(1, 0.0f, 1.0f, 0, 0));
        sparseArray12.put(R.id.area_head, generateLayoutParams(1, 0.0f, 0.0f, 16, 1, 0, 0, 16, 0));
        sparseArray12.put(R.id.area_title, generateLayoutParams(2, 0.0f, 1.0f, 16, 2, 0, 0, 0, 0));
        sparseArray12.put(R.id.area_end, generateLayoutParams(2, 0.0f, 0.0f, 0, 3, 0, 14, 0, 4));
        sparseArray12.put(R.id.area_content, generateLayoutParams(2, 0.0f, 0.0f, 0, 4, 0, 0, 0, 14));
        sparseArray12.put(R.id.area_end2, generateLayoutParams(1, 0.0f, 0.0f, 17, 5, 8, 0, 0, 0));
        HashMap map = new HashMap();
        MAP_LEVEL_PARAMS = map;
        map.put(10000, sparseArray);
        map.put(10001, sparseArray2);
        map.put(10002, sparseArray3);
        map.put(Integer.valueOf(LEVEL_LARGE_FULL_TITLE_SINGLE), sparseArray4);
        map.put(Integer.valueOf(LEVEL_LARGE_FULL_TITLE_MULTI), sparseArray5);
        map.put(Integer.valueOf(LEVEL_LARGE_ONLY_TITLE), sparseArray6);
        map.put(Integer.valueOf(LEVEL_LARGE_ONLY_SUMMARY), sparseArray7);
        map.put(Integer.valueOf(LEVEL_LARGE_FULL_TITLE_SINGLE_TEXT), sparseArray8);
        map.put(Integer.valueOf(LEVEL_LARGE_FULL_TITLE_MULTI_TEXT), sparseArray9);
        map.put(Integer.valueOf(LEVEL_LARGE_TITLE_SINGLE_WIDGET_TEXT), sparseArray10);
        map.put(Integer.valueOf(LEVEL_LARGE_TITLE_MULTI_WIDGET_TEXT), sparseArray11);
        map.put(Integer.valueOf(LEVEL_LARGE_SUMMARY_WIDGET_TEXT), sparseArray12);
    }

    public static SparseArray<HyperCellLayout.LayoutParams> getLevelParams(int i) {
        Map<Integer, SparseArray<HyperCellLayout.LayoutParams>> map = MAP_LEVEL_PARAMS;
        if (!map.containsKey(Integer.valueOf(i))) {
            throw new IllegalArgumentException("The current level = " + i + " does not exist, please check whether it has been registered");
        }
        return map.get(Integer.valueOf(i));
    }

    public static void registerLevelParams(int i, SparseArray<HyperCellLayout.LayoutParams> sparseArray) {
        Map<Integer, SparseArray<HyperCellLayout.LayoutParams>> map = MAP_LEVEL_PARAMS;
        if (map.containsKey(Integer.valueOf(i))) {
            throw new IllegalArgumentException("Template level '" + i + "' has been registered! Please do not register repeatedly.");
        }
        map.put(Integer.valueOf(i), sparseArray);
    }

    public static HyperCellLayout.LayoutParams generateLayoutParams(int i, float f, float f2, int i2, int i3, int i4, int i5, int i6, int i7) {
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

    protected static HyperCellLayout.LayoutParams generateLayoutParams(int i, float f, float f2, int i2, int i3) {
        return generateLayoutParams(i, f, f2, i2, i3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
}
