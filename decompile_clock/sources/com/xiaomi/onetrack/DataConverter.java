package com.xiaomi.onetrack;

import android.os.Bundle;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.r;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class DataConverter {
    private static final String a = "DataConverter";

    public static Map fromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        HashMap map = new HashMap();
        try {
            for (String str : bundle.keySet()) {
                Object obj = bundle.get(str);
                if (r.b(obj)) {
                    map.put(str, obj);
                } else if (obj.getClass().isArray()) {
                    int length = Array.getLength(obj);
                    ArrayList arrayList = new ArrayList();
                    for (int i = 0; i < length; i++) {
                        Object obj2 = Array.get(obj, i);
                        if (r.b(obj2)) {
                            arrayList.add(obj2);
                        }
                    }
                    if (arrayList.size() > 0) {
                        map.put(str, arrayList);
                    }
                } else if (obj instanceof List) {
                    ArrayList arrayList2 = new ArrayList();
                    for (Object obj3 : (List) obj) {
                        if (r.b(obj3)) {
                            arrayList2.add(obj3);
                        }
                    }
                    if (arrayList2.size() > 0) {
                        map.put(str, arrayList2);
                    }
                } else if (p.a) {
                    r.a(a, str);
                }
            }
        } catch (Exception e) {
            p.b(a, "convert bundle error:" + e.toString());
        }
        return map;
    }

    public static Map fromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        HashMap map = new HashMap();
        try {
            Iterator<String> itKeys = jSONObject.keys();
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                Object obj = jSONObject.get(next);
                if (r.b(obj)) {
                    map.put(next, obj);
                } else if (obj instanceof JSONArray) {
                    map.put(next, a((JSONArray) obj));
                } else if (p.a) {
                    r.a(a, next);
                }
            }
        } catch (Exception e) {
            p.b(a, "convert json to map error:" + e.toString());
        }
        return map;
    }

    private static List a(JSONArray jSONArray) throws JSONException {
        if (jSONArray == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            Object obj = jSONArray.get(i);
            if (r.b(obj)) {
                arrayList.add(obj);
            } else if (obj instanceof JSONObject) {
                JSONObject jSONObject = (JSONObject) obj;
                Iterator<String> itKeys = jSONObject.keys();
                HashMap map = new HashMap();
                while (itKeys.hasNext()) {
                    String next = itKeys.next();
                    Object obj2 = jSONObject.get(next);
                    if (r.b(obj2)) {
                        map.put(next, obj2);
                    }
                }
                if (map.size() > 0) {
                    arrayList.add(map);
                }
            }
        }
        return arrayList;
    }
}
