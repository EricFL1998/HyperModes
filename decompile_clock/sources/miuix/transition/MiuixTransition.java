package miuix.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.collection.ArrayMap;
import androidx.collection.LongSparseArray;
import androidx.core.view.ViewCompat;
import com.xiaomi.onetrack.util.z;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes3.dex */
public abstract class MiuixTransition implements Cloneable {
    static final boolean DBG = false;
    private static final int[] DEFAULT_MATCH_ORDER = {2, 1, 3, 4};
    private static final String LOG_TAG = "Transition";
    public static final int MATCH_ID = 3;
    public static final int MATCH_INSTANCE = 1;
    public static final int MATCH_ITEM_ID = 4;
    public static final int MATCH_NAME = 2;
    private ArrayList<TransitionValues> mEndValuesList;
    ArrayMap<String, String> mNameOverrides;
    private ArrayList<TransitionValues> mStartValuesList;
    private final String mName = getClass().getName();
    protected AnimConfig mAnimConfig = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.95f, 0.35f));
    ArrayList<Integer> mTargetIds = new ArrayList<>();
    ArrayList<View> mTargets = new ArrayList<>();
    private ArrayList<String> mTargetNames = null;
    private ArrayList<Class<?>> mTargetTypes = null;
    private ArrayList<Integer> mTargetIdExcludes = null;
    private ArrayList<View> mTargetExcludes = null;
    private ArrayList<Class<?>> mTargetTypeExcludes = null;
    private ArrayList<String> mTargetNameExcludes = null;
    private ArrayList<Integer> mTargetIdChildExcludes = null;
    private ArrayList<View> mTargetChildExcludes = null;
    private ArrayList<Class<?>> mTargetTypeChildExcludes = null;
    MiuixTransitionSet mParent = null;
    private int[] mMatchOrder = DEFAULT_MATCH_ORDER;
    private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
    private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
    ViewGroup mSceneRoot = null;
    boolean mCanRemoveViews = false;
    int mNumInstances = 0;
    CopyOnWriteArrayList<MiuixTransitionListener> mListeners = null;
    ArrayList<TransitionRunner> mTransitionRunners = new ArrayList<>();
    private int mTransitionMode = 0;

    public interface MiuixTransitionListener {
        void onTransitionEnd(MiuixTransition miuixTransition);

        void onTransitionStart(MiuixTransition miuixTransition);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TRANSITION_MODE {
        public static final int MULTI_LAYOUT = 1;
        public static final int SINGLE_LAYOUT = 0;
    }

    public abstract void captureEndValues(TransitionValues transitionValues);

    public abstract void captureStartValues(TransitionValues transitionValues);

    public void createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
    }

    public String[] getTransitionProperties() {
        return null;
    }

    protected MiuixTransition() {
    }

    protected MiuixTransition(Context context, AttributeSet attributeSet) {
        float[] fArr = null;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Transition);
        int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
        boolean z = false;
        int i = 1;
        for (int i2 = 0; i2 < indexCount; i2++) {
            int index = typedArrayObtainStyledAttributes.getIndex(i2);
            if (index == R.styleable.Transition_delay) {
                this.mAnimConfig.setDelay(typedArrayObtainStyledAttributes.getInt(index, 0));
            } else if (index == R.styleable.Transition_fromSpeed) {
                this.mAnimConfig.setFromSpeed(typedArrayObtainStyledAttributes.getFloat(index, 0.0f));
            } else {
                if (index == R.styleable.Transition_ease) {
                    i = typedArrayObtainStyledAttributes.getInt(index, 1);
                } else if (index == R.styleable.Transition_params) {
                    String[] strArrSplit = typedArrayObtainStyledAttributes.getString(index).split(z.b);
                    float[] fArr2 = new float[strArrSplit.length];
                    for (int i3 = 0; i3 < strArrSplit.length; i3++) {
                        fArr2[i3] = Float.valueOf(strArrSplit[i3]).floatValue();
                    }
                    fArr = fArr2;
                }
                z = true;
            }
        }
        if (z && fArr != null) {
            this.mAnimConfig.setEase(i, fArr);
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    public void captureValues(ViewGroup viewGroup, boolean z) {
        ArrayList<String> arrayList;
        ArrayList<Class<?>> arrayList2;
        ArrayMap<String, String> arrayMap;
        clearValues(z);
        if ((!this.mTargetIds.isEmpty() || !this.mTargets.isEmpty()) && (((arrayList = this.mTargetNames) == null || arrayList.isEmpty()) && ((arrayList2 = this.mTargetTypes) == null || arrayList2.isEmpty()))) {
            for (int i = 0; i < this.mTargetIds.size(); i++) {
                View viewFindViewById = viewGroup.findViewById(this.mTargetIds.get(i).intValue());
                if (viewFindViewById != null && (this.mTransitionMode != 1 || viewFindViewById.getVisibility() != 8)) {
                    TransitionValues transitionValues = new TransitionValues(viewFindViewById);
                    if (z) {
                        captureStartValues(transitionValues);
                    } else {
                        captureEndValues(transitionValues);
                    }
                    transitionValues.mTargetedTransitions.add(this);
                    if (z) {
                        addViewValues(this.mStartValues, viewFindViewById, transitionValues);
                    } else {
                        addViewValues(this.mEndValues, viewFindViewById, transitionValues);
                    }
                }
            }
            for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                View view = this.mTargets.get(i2);
                if (this.mTransitionMode != 1 || view.getVisibility() != 8) {
                    TransitionValues transitionValues2 = new TransitionValues(view);
                    if (z) {
                        captureStartValues(transitionValues2);
                    } else {
                        captureEndValues(transitionValues2);
                    }
                    transitionValues2.mTargetedTransitions.add(this);
                    if (z) {
                        addViewValues(this.mStartValues, view, transitionValues2);
                    } else {
                        addViewValues(this.mEndValues, view, transitionValues2);
                    }
                }
            }
        } else {
            captureHierarchy(viewGroup, z);
        }
        if (z || (arrayMap = this.mNameOverrides) == null) {
            return;
        }
        int size = arrayMap.size();
        ArrayList arrayList3 = new ArrayList(size);
        for (int i3 = 0; i3 < size; i3++) {
            arrayList3.add(this.mStartValues.mNameValues.remove(this.mNameOverrides.keyAt(i3)));
        }
        for (int i4 = 0; i4 < size; i4++) {
            View view2 = (View) arrayList3.get(i4);
            if (view2 != null) {
                this.mStartValues.mNameValues.put(this.mNameOverrides.valueAt(i4), view2);
            }
        }
    }

    private static void addViewValues(TransitionValuesMaps transitionValuesMaps, View view, TransitionValues transitionValues) {
        transitionValuesMaps.mViewValues.put(view, transitionValues);
        int id = view.getId();
        if (id >= 0) {
            if (transitionValuesMaps.mIdValues.indexOfKey(id) >= 0) {
                transitionValuesMaps.mIdValues.put(id, null);
            } else {
                transitionValuesMaps.mIdValues.put(id, view);
            }
        }
        String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            if (transitionValuesMaps.mNameValues.containsKey(transitionName)) {
                transitionValuesMaps.mNameValues.put(transitionName, null);
            } else {
                transitionValuesMaps.mNameValues.put(transitionName, view);
            }
        }
        if (view.getParent() instanceof ListView) {
            ListView listView = (ListView) view.getParent();
            if (listView.getAdapter().hasStableIds()) {
                long itemIdAtPosition = listView.getItemIdAtPosition(listView.getPositionForView(view));
                if (transitionValuesMaps.mItemIdValues.indexOfKey(itemIdAtPosition) >= 0) {
                    View view2 = transitionValuesMaps.mItemIdValues.get(itemIdAtPosition);
                    if (view2 != null) {
                        ViewCompat.setHasTransientState(view2, false);
                        transitionValuesMaps.mItemIdValues.put(itemIdAtPosition, null);
                        return;
                    }
                    return;
                }
                ViewCompat.setHasTransientState(view, true);
                transitionValuesMaps.mItemIdValues.put(itemIdAtPosition, view);
            }
        }
    }

    private void captureHierarchy(View view, boolean z) {
        if (view == null) {
            return;
        }
        if (this.mTransitionMode == 1 && view.getVisibility() == 8) {
            return;
        }
        int id = view.getId();
        ArrayList<Integer> arrayList = this.mTargetIdExcludes;
        if (arrayList == null || !arrayList.contains(Integer.valueOf(id))) {
            ArrayList<View> arrayList2 = this.mTargetExcludes;
            if (arrayList2 == null || !arrayList2.contains(view)) {
                ArrayList<Class<?>> arrayList3 = this.mTargetTypeExcludes;
                if (arrayList3 != null) {
                    int size = arrayList3.size();
                    for (int i = 0; i < size; i++) {
                        if (this.mTargetTypeExcludes.get(i).isInstance(view)) {
                            return;
                        }
                    }
                }
                if (view.getParent() instanceof ViewGroup) {
                    TransitionValues transitionValues = new TransitionValues(view);
                    if (z) {
                        captureStartValues(transitionValues);
                    } else {
                        captureEndValues(transitionValues);
                    }
                    transitionValues.mTargetedTransitions.add(this);
                    if (z) {
                        addViewValues(this.mStartValues, view, transitionValues);
                    } else {
                        addViewValues(this.mEndValues, view, transitionValues);
                    }
                }
                if (view instanceof ViewGroup) {
                    ArrayList<Integer> arrayList4 = this.mTargetIdChildExcludes;
                    if (arrayList4 == null || !arrayList4.contains(Integer.valueOf(id))) {
                        ArrayList<View> arrayList5 = this.mTargetChildExcludes;
                        if (arrayList5 == null || !arrayList5.contains(view)) {
                            ArrayList<Class<?>> arrayList6 = this.mTargetTypeChildExcludes;
                            if (arrayList6 != null) {
                                int size2 = arrayList6.size();
                                for (int i2 = 0; i2 < size2; i2++) {
                                    if (this.mTargetTypeChildExcludes.get(i2).isInstance(view)) {
                                        return;
                                    }
                                }
                            }
                            ViewGroup viewGroup = (ViewGroup) view;
                            for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                                captureHierarchy(viewGroup.getChildAt(i3), z);
                            }
                        }
                    }
                }
            }
        }
    }

    public void clearValues(boolean z) {
        if (z) {
            this.mStartValues.mViewValues.clear();
            this.mStartValues.mIdValues.clear();
            this.mStartValues.mItemIdValues.clear();
        } else {
            this.mEndValues.mViewValues.clear();
            this.mEndValues.mIdValues.clear();
            this.mEndValues.mItemIdValues.clear();
        }
    }

    public void clear() {
        clearValues(true);
        clearValues(false);
        clearListener();
        this.mAnimConfig.clear();
        this.mTransitionRunners.clear();
    }

    public void playTransition(ViewGroup viewGroup) {
        this.mStartValuesList = new ArrayList<>();
        this.mEndValuesList = new ArrayList<>();
        matchStartAndEnd(this.mStartValues, this.mEndValues);
        createAnimators(viewGroup, this.mStartValues, this.mEndValues, this.mStartValuesList, this.mEndValuesList);
        runAnimators();
    }

    public TransitionValues getTransitionValues(View view, boolean z) {
        MiuixTransitionSet miuixTransitionSet = this.mParent;
        if (miuixTransitionSet != null) {
            return miuixTransitionSet.getTransitionValues(view, z);
        }
        return (z ? this.mStartValues : this.mEndValues).mViewValues.get(view);
    }

    TransitionValues getMatchedTransitionValues(View view, boolean z) {
        MiuixTransitionSet miuixTransitionSet = this.mParent;
        if (miuixTransitionSet != null) {
            return miuixTransitionSet.getMatchedTransitionValues(view, z);
        }
        ArrayList<TransitionValues> arrayList = z ? this.mStartValuesList : this.mEndValuesList;
        if (arrayList == null) {
            return null;
        }
        int size = arrayList.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                i = -1;
                break;
            }
            TransitionValues transitionValues = arrayList.get(i);
            if (transitionValues == null) {
                return null;
            }
            if (transitionValues.view == view) {
                break;
            }
            i++;
        }
        if (i >= 0) {
            return (z ? this.mEndValuesList : this.mStartValuesList).get(i);
        }
        return null;
    }

    private void addUnmatched(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2) {
        for (int i = 0; i < arrayMap.size(); i++) {
            TransitionValues transitionValuesValueAt = arrayMap.valueAt(i);
            if (isValidTarget(transitionValuesValueAt.view)) {
                this.mStartValuesList.add(transitionValuesValueAt);
                this.mEndValuesList.add(null);
            }
        }
        for (int i2 = 0; i2 < arrayMap2.size(); i2++) {
            TransitionValues transitionValuesValueAt2 = arrayMap2.valueAt(i2);
            if (isValidTarget(transitionValuesValueAt2.view)) {
                this.mEndValuesList.add(transitionValuesValueAt2);
                this.mStartValuesList.add(null);
            }
        }
    }

    private void matchStartAndEnd(TransitionValuesMaps transitionValuesMaps, TransitionValuesMaps transitionValuesMaps2) {
        ArrayMap<View, TransitionValues> arrayMap = new ArrayMap<>(transitionValuesMaps.mViewValues);
        ArrayMap<View, TransitionValues> arrayMap2 = new ArrayMap<>(transitionValuesMaps2.mViewValues);
        int i = 0;
        while (true) {
            int[] iArr = this.mMatchOrder;
            if (i < iArr.length) {
                int i2 = iArr[i];
                if (i2 == 1) {
                    matchInstances(arrayMap, arrayMap2);
                } else if (i2 == 2) {
                    matchNames(arrayMap, arrayMap2, transitionValuesMaps.mNameValues, transitionValuesMaps2.mNameValues);
                } else if (i2 == 3) {
                    matchIds(arrayMap, arrayMap2, transitionValuesMaps.mIdValues, transitionValuesMaps2.mIdValues);
                } else if (i2 == 4) {
                    matchItemIds(arrayMap, arrayMap2, transitionValuesMaps.mItemIdValues, transitionValuesMaps2.mItemIdValues);
                }
                i++;
            } else {
                addUnmatched(arrayMap, arrayMap2);
                return;
            }
        }
    }

    private void matchInstances(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2) {
        TransitionValues transitionValuesRemove;
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            View viewKeyAt = arrayMap.keyAt(size);
            if (viewKeyAt != null && isValidTarget(viewKeyAt) && (transitionValuesRemove = arrayMap2.remove(viewKeyAt)) != null && isValidTarget(transitionValuesRemove.view)) {
                this.mStartValuesList.add(arrayMap.removeAt(size));
                this.mEndValuesList.add(transitionValuesRemove);
            }
        }
    }

    protected void createAnimators(ViewGroup viewGroup, TransitionValuesMaps transitionValuesMaps, TransitionValuesMaps transitionValuesMaps2, ArrayList<TransitionValues> arrayList, ArrayList<TransitionValues> arrayList2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TransitionValues transitionValues = arrayList.get(i);
            TransitionValues transitionValues2 = arrayList2.get(i);
            if (transitionValues != null && !transitionValues.mTargetedTransitions.contains(this)) {
                transitionValues = null;
            }
            if (transitionValues2 != null && !transitionValues2.mTargetedTransitions.contains(this)) {
                transitionValues2 = null;
            }
            if ((transitionValues != null || transitionValues2 != null) && (transitionValues == null || transitionValues2 == null || isTransitionRequired(transitionValues, transitionValues2))) {
                createAnimator(viewGroup, transitionValues, transitionValues2);
            }
        }
    }

    protected static boolean diff(float f, float f2) {
        if (Float.isNaN(f) || Float.isNaN(f2)) {
            return Float.isNaN(f) != Float.isNaN(f2);
        }
        return Math.abs(f - f2) > 1.0E-6f;
    }

    public boolean isTransitionRequired(TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues == null || transitionValues2 == null) {
            return false;
        }
        String[] transitionProperties = getTransitionProperties();
        if (transitionProperties != null) {
            for (String str : transitionProperties) {
                if (!isValueChanged(transitionValues, transitionValues2, str)) {
                }
            }
            return false;
        }
        Iterator<String> it = transitionValues.values.keySet().iterator();
        while (it.hasNext()) {
            if (isValueChanged(transitionValues, transitionValues2, it.next())) {
            }
        }
        return false;
        return true;
    }

    private static boolean isValueChanged(TransitionValues transitionValues, TransitionValues transitionValues2, String str) {
        Object obj = transitionValues.values.get(str);
        Object obj2 = transitionValues2.values.get(str);
        if (obj == null && obj2 == null) {
            return false;
        }
        if (obj == null || obj2 == null) {
            return true;
        }
        return !obj.equals(obj2);
    }

    boolean isValidTarget(View view) {
        ArrayList<Class<?>> arrayList;
        ArrayList<String> arrayList2;
        int id = view.getId();
        ArrayList<Integer> arrayList3 = this.mTargetIdExcludes;
        if (arrayList3 != null && arrayList3.contains(Integer.valueOf(id))) {
            return false;
        }
        ArrayList<View> arrayList4 = this.mTargetExcludes;
        if (arrayList4 != null && arrayList4.contains(view)) {
            return false;
        }
        ArrayList<Class<?>> arrayList5 = this.mTargetTypeExcludes;
        if (arrayList5 != null) {
            int size = arrayList5.size();
            for (int i = 0; i < size; i++) {
                if (this.mTargetTypeExcludes.get(i).isInstance(view)) {
                    return false;
                }
            }
        }
        if (this.mTargetNameExcludes != null && ViewCompat.getTransitionName(view) != null && this.mTargetNameExcludes.contains(ViewCompat.getTransitionName(view))) {
            return false;
        }
        if ((this.mTargetIds.isEmpty() && this.mTargets.isEmpty() && (((arrayList = this.mTargetTypes) == null || arrayList.isEmpty()) && ((arrayList2 = this.mTargetNames) == null || arrayList2.isEmpty()))) || this.mTargetIds.contains(Integer.valueOf(id)) || this.mTargets.contains(view)) {
            return true;
        }
        ArrayList<String> arrayList6 = this.mTargetNames;
        if (arrayList6 != null && arrayList6.contains(ViewCompat.getTransitionName(view))) {
            return true;
        }
        if (this.mTargetTypes != null) {
            for (int i2 = 0; i2 < this.mTargetTypes.size(); i2++) {
                if (this.mTargetTypes.get(i2).isInstance(view)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void matchItemIds(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2, LongSparseArray<View> longSparseArray, LongSparseArray<View> longSparseArray2) {
        View view;
        int size = longSparseArray.size();
        for (int i = 0; i < size; i++) {
            View viewValueAt = longSparseArray.valueAt(i);
            if (viewValueAt != null && isValidTarget(viewValueAt) && (view = longSparseArray2.get(longSparseArray.keyAt(i))) != null && isValidTarget(view)) {
                TransitionValues transitionValues = arrayMap.get(viewValueAt);
                TransitionValues transitionValues2 = arrayMap2.get(view);
                if (transitionValues != null && transitionValues2 != null) {
                    this.mStartValuesList.add(transitionValues);
                    this.mEndValuesList.add(transitionValues2);
                    arrayMap.remove(viewValueAt);
                    arrayMap2.remove(view);
                }
            }
        }
    }

    private void matchIds(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2, SparseArray<View> sparseArray, SparseArray<View> sparseArray2) {
        View view;
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            View viewValueAt = sparseArray.valueAt(i);
            if (viewValueAt != null && isValidTarget(viewValueAt) && (view = sparseArray2.get(sparseArray.keyAt(i))) != null && isValidTarget(view)) {
                TransitionValues transitionValues = arrayMap.get(viewValueAt);
                TransitionValues transitionValues2 = arrayMap2.get(view);
                if (transitionValues != null && transitionValues2 != null) {
                    this.mStartValuesList.add(transitionValues);
                    this.mEndValuesList.add(transitionValues2);
                    arrayMap.remove(viewValueAt);
                    arrayMap2.remove(view);
                }
            }
        }
    }

    private void matchNames(ArrayMap<View, TransitionValues> arrayMap, ArrayMap<View, TransitionValues> arrayMap2, ArrayMap<String, View> arrayMap3, ArrayMap<String, View> arrayMap4) {
        View view;
        int size = arrayMap3.size();
        for (int i = 0; i < size; i++) {
            View viewValueAt = arrayMap3.valueAt(i);
            if (viewValueAt != null && isValidTarget(viewValueAt) && (view = arrayMap4.get(arrayMap3.keyAt(i))) != null && isValidTarget(view)) {
                TransitionValues transitionValues = arrayMap.get(viewValueAt);
                TransitionValues transitionValues2 = arrayMap2.get(view);
                if (transitionValues != null && transitionValues2 != null) {
                    this.mStartValuesList.add(transitionValues);
                    this.mEndValuesList.add(transitionValues2);
                    arrayMap.remove(viewValueAt);
                    arrayMap2.remove(view);
                }
            }
        }
    }

    public MiuixTransition excludeTarget(int i, boolean z) {
        if (i >= 0) {
            this.mTargetIdExcludes = excludeObject(this.mTargetIdExcludes, Integer.valueOf(i), z);
        }
        return this;
    }

    public MiuixTransition excludeTarget(String str, boolean z) {
        this.mTargetNameExcludes = excludeObject(this.mTargetNameExcludes, str, z);
        return this;
    }

    public MiuixTransition excludeChildren(int i, boolean z) {
        if (i >= 0) {
            this.mTargetIdChildExcludes = excludeObject(this.mTargetIdChildExcludes, Integer.valueOf(i), z);
        }
        return this;
    }

    public MiuixTransition excludeTarget(View view, boolean z) {
        this.mTargetExcludes = excludeObject(this.mTargetExcludes, view, z);
        return this;
    }

    public MiuixTransition excludeChildren(View view, boolean z) {
        this.mTargetChildExcludes = excludeObject(this.mTargetChildExcludes, view, z);
        return this;
    }

    private static <T> ArrayList<T> excludeObject(ArrayList<T> arrayList, T t, boolean z) {
        if (t == null) {
            return arrayList;
        }
        if (z) {
            return ArrayListManager.add(arrayList, t);
        }
        return ArrayListManager.remove(arrayList, t);
    }

    public MiuixTransition excludeTarget(Class<?> cls, boolean z) {
        this.mTargetTypeExcludes = excludeObject(this.mTargetTypeExcludes, cls, z);
        return this;
    }

    public MiuixTransition excludeChildren(Class<?> cls, boolean z) {
        this.mTargetTypeChildExcludes = excludeObject(this.mTargetTypeChildExcludes, cls, z);
        return this;
    }

    public MiuixTransition addTarget(int i) {
        if (i > 0) {
            this.mTargetIds.add(Integer.valueOf(i));
        }
        return this;
    }

    public MiuixTransition addTarget(View view) {
        this.mTargets.add(view);
        return this;
    }

    public MiuixTransition addTarget(String str) {
        if (this.mTargetNames == null) {
            this.mTargetNames = new ArrayList<>();
        }
        this.mTargetNames.add(str);
        return this;
    }

    public MiuixTransition addTarget(Class<?> cls) {
        if (this.mTargetTypes == null) {
            this.mTargetTypes = new ArrayList<>();
        }
        this.mTargetTypes.add(cls);
        return this;
    }

    public MiuixTransition removeTarget(View view) {
        this.mTargets.remove(view);
        return this;
    }

    public MiuixTransition removeTarget(int i) {
        if (i != 0) {
            this.mTargetIds.remove(Integer.valueOf(i));
        }
        return this;
    }

    public MiuixTransition removeTarget(String str) {
        ArrayList<String> arrayList = this.mTargetNames;
        if (arrayList != null) {
            arrayList.remove(str);
        }
        return this;
    }

    public MiuixTransition removeTarget(Class<?> cls) {
        ArrayList<Class<?>> arrayList = this.mTargetTypes;
        if (arrayList != null) {
            arrayList.remove(cls);
        }
        return this;
    }

    public List<Integer> getTargetIds() {
        return this.mTargetIds;
    }

    public List<View> getTargets() {
        return this.mTargets;
    }

    public List<String> getTargetNames() {
        return this.mTargetNames;
    }

    public List<String> getTargetViewNames() {
        return this.mTargetNames;
    }

    public List<Class<?>> getTargetTypes() {
        return this.mTargetTypes;
    }

    public MiuixTransition setSceneRoot(ViewGroup viewGroup) {
        this.mSceneRoot = viewGroup;
        return this;
    }

    public MiuixTransition setTransitionMode(int i) {
        this.mTransitionMode = i;
        return this;
    }

    public void setCanRemoveViews(boolean z) {
        this.mCanRemoveViews = z;
    }

    public void setNameOverrides(ArrayMap<String, String> arrayMap) {
        this.mNameOverrides = arrayMap;
    }

    public ArrayMap<String, String> getNameOverrides() {
        return this.mNameOverrides;
    }

    public MiuixTransition setAnimConfig(AnimConfig animConfig) {
        this.mAnimConfig = animConfig;
        return this;
    }

    public AnimConfig getAnimConfig() {
        return this.mAnimConfig;
    }

    protected void onTransitionStart() {
        CopyOnWriteArrayList<MiuixTransitionListener> copyOnWriteArrayList = this.mListeners;
        if (copyOnWriteArrayList == null || copyOnWriteArrayList.isEmpty()) {
            return;
        }
        Iterator<MiuixTransitionListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onTransitionStart(this);
        }
    }

    protected void onTransitionEnd() {
        CopyOnWriteArrayList<MiuixTransitionListener> copyOnWriteArrayList = this.mListeners;
        if (copyOnWriteArrayList != null && !copyOnWriteArrayList.isEmpty()) {
            Iterator<MiuixTransitionListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onTransitionEnd(this);
            }
        }
        this.mTransitionRunners.clear();
        this.mAnimConfig.clear();
    }

    public MiuixTransition addListener(MiuixTransitionListener miuixTransitionListener) {
        if (this.mListeners == null) {
            this.mListeners = new CopyOnWriteArrayList<>();
        }
        this.mListeners.add(miuixTransitionListener);
        return this;
    }

    public MiuixTransition removeListener(MiuixTransitionListener miuixTransitionListener) {
        CopyOnWriteArrayList<MiuixTransitionListener> copyOnWriteArrayList = this.mListeners;
        if (copyOnWriteArrayList == null) {
            return this;
        }
        copyOnWriteArrayList.remove(miuixTransitionListener);
        if (this.mListeners.isEmpty()) {
            this.mListeners = null;
        }
        return this;
    }

    public void clearListener() {
        this.mListeners = null;
    }

    public String getName() {
        return this.mName;
    }

    public String toString() {
        return toString("");
    }

    @Override // 
    public MiuixTransition clone() {
        MiuixTransition miuixTransition = null;
        try {
            MiuixTransition miuixTransition2 = (MiuixTransition) super.clone();
            try {
                miuixTransition2.mStartValues = new TransitionValuesMaps();
                miuixTransition2.mEndValues = new TransitionValuesMaps();
                miuixTransition2.mStartValuesList = null;
                miuixTransition2.mEndValuesList = null;
                AnimConfig animConfig = new AnimConfig();
                this.mAnimConfig = animConfig;
                animConfig.copy(miuixTransition2.mAnimConfig);
                return miuixTransition2;
            } catch (CloneNotSupportedException unused) {
                miuixTransition = miuixTransition2;
                return miuixTransition;
            }
        } catch (CloneNotSupportedException unused2) {
        }
    }

    String toString(String str) {
        String str2 = str + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": ";
        if (this.mTargetIds.size() <= 0 && this.mTargets.size() <= 0) {
            return str2;
        }
        String str3 = str2 + "tgts(";
        if (this.mTargetIds.size() > 0) {
            for (int i = 0; i < this.mTargetIds.size(); i++) {
                if (i > 0) {
                    str3 = str3 + ", ";
                }
                str3 = str3 + this.mTargetIds.get(i);
            }
        }
        if (this.mTargets.size() > 0) {
            for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                if (i2 > 0) {
                    str3 = str3 + ", ";
                }
                str3 = str3 + this.mTargets.get(i2);
            }
        }
        return str3 + ")";
    }

    private static class ArrayListManager {
        private ArrayListManager() {
        }

        static <T> ArrayList<T> add(ArrayList<T> arrayList, T t) {
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            if (!arrayList.contains(t)) {
                arrayList.add(t);
            }
            return arrayList;
        }

        static <T> ArrayList<T> remove(ArrayList<T> arrayList, T t) {
            if (arrayList == null) {
                return arrayList;
            }
            arrayList.remove(t);
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList;
        }
    }

    protected void addTransitionRunner(TransitionRunner transitionRunner) {
        if (this.mTransitionRunners == null) {
            this.mTransitionRunners = new ArrayList<>();
        }
        this.mTransitionRunners.add(transitionRunner);
    }

    protected void runAnimators() {
        ArrayList<TransitionRunner> arrayList = this.mTransitionRunners;
        if (arrayList == null || arrayList.isEmpty()) {
            onTransitionEnd();
            return;
        }
        int size = this.mTransitionRunners.size();
        for (int i = 0; i < size; i++) {
            this.mTransitionRunners.get(i).run();
        }
    }

    public void forceToEnd() {
        Iterator it = new ArrayList(this.mTransitionRunners).iterator();
        while (it.hasNext()) {
            ((TransitionRunner) it.next()).setTo();
        }
    }

    protected static class TransitionRunner {
        AnimConfig[] configs;
        Object fromTag;
        IStateStyle stateStyle;
        Object target;
        Object toTag;

        public TransitionRunner(Object obj, Object obj2, Object obj3, AnimConfig... animConfigArr) {
            this.target = obj;
            this.fromTag = obj2;
            this.toTag = obj3;
            this.configs = animConfigArr;
        }

        public TransitionRunner(IStateStyle iStateStyle, Object obj, Object obj2, AnimConfig... animConfigArr) {
            this.stateStyle = iStateStyle;
            this.fromTag = obj;
            this.toTag = obj2;
            this.configs = animConfigArr;
        }

        void run() {
            Log.d(MiuixTransition.LOG_TAG, "fromTag:" + this.fromTag + "\ntoTag:" + this.toTag);
            IStateStyle iStateStyle = this.stateStyle;
            if (iStateStyle != null) {
                iStateStyle.fromTo(this.fromTag, this.toTag, this.configs);
                return;
            }
            Object obj = this.target;
            if (obj instanceof View) {
                Folme.useAt((View) obj).state().fromTo(this.fromTag, this.toTag, this.configs);
            } else {
                Folme.useValue(obj).fromTo(this.fromTag, this.toTag, this.configs);
            }
        }

        void setTo() {
            Log.d(MiuixTransition.LOG_TAG, "setTo:" + this.toTag);
            IStateStyle iStateStyle = this.stateStyle;
            if (iStateStyle != null) {
                iStateStyle.setTo(this.toTag, this.configs);
                return;
            }
            Object obj = this.target;
            if (obj instanceof View) {
                Folme.useAt((View) obj).state().setTo(this.toTag, this.configs);
            } else {
                Folme.useValue(obj).setTo(this.toTag, this.configs);
            }
        }
    }
}
