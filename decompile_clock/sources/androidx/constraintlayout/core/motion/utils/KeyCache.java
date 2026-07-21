package androidx.constraintlayout.core.motion.utils;

import java.util.Arrays;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class KeyCache {
    HashMap<Object, HashMap<String, float[]>> map = new HashMap<>();

    public void setFloatValue(Object obj, String str, int i, float f) {
        if (!this.map.containsKey(obj)) {
            HashMap<String, float[]> map = new HashMap<>();
            float[] fArr = new float[i + 1];
            fArr[i] = f;
            map.put(str, fArr);
            this.map.put(obj, map);
            return;
        }
        HashMap<String, float[]> map2 = this.map.get(obj);
        if (map2 == null) {
            map2 = new HashMap<>();
        }
        if (!map2.containsKey(str)) {
            float[] fArr2 = new float[i + 1];
            fArr2[i] = f;
            map2.put(str, fArr2);
            this.map.put(obj, map2);
            return;
        }
        float[] fArrCopyOf = map2.get(str);
        if (fArrCopyOf == null) {
            fArrCopyOf = new float[0];
        }
        if (fArrCopyOf.length <= i) {
            fArrCopyOf = Arrays.copyOf(fArrCopyOf, i + 1);
        }
        fArrCopyOf[i] = f;
        map2.put(str, fArrCopyOf);
    }

    public float getFloatValue(Object obj, String str, int i) {
        HashMap<String, float[]> map;
        float[] fArr;
        if (this.map.containsKey(obj) && (map = this.map.get(obj)) != null && map.containsKey(str) && (fArr = map.get(str)) != null && fArr.length > i) {
            return fArr[i];
        }
        return Float.NaN;
    }
}
