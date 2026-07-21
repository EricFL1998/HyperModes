package miuix.recyclerview.tool;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class RecyclerViewHelper {
    private RecyclerViewHelper() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> void handleListMove(List<T> list, int i, int i2, int i3) {
        int i4;
        boolean z = i < i2;
        if (i3 == 1) {
            Object obj = list.get(i);
            if (z) {
                while (i < i2) {
                    int i5 = i + 1;
                    list.set(i, list.get(i5));
                    i = i5;
                }
            } else {
                while (i > i2) {
                    list.set(i, list.get(i - 1));
                    i--;
                }
            }
            list.set(i2, obj);
            return;
        }
        ArrayList arrayList = new ArrayList(i3);
        int i6 = i;
        while (true) {
            i4 = i + i3;
            if (i6 >= i4) {
                break;
            }
            arrayList.add(list.get(i6));
            i6++;
        }
        if (z) {
            while (i < i2) {
                list.set(i, list.get(i + i3));
                i++;
            }
        } else {
            for (int i7 = i4 - 1; i7 >= i2 + i3; i7--) {
                list.set(i7, list.get(i7 - i3));
            }
        }
        for (int i8 = 0; i8 < i3; i8++) {
            list.set(i2 + i8, arrayList.get(i8));
        }
    }
}
