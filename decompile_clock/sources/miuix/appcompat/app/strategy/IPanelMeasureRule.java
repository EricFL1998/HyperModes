package miuix.appcompat.app.strategy;

import android.util.TypedValue;
import miuix.appcompat.app.DialogContract;

/* JADX INFO: loaded from: classes2.dex */
public interface IPanelMeasureRule {
    int measurePanelHeight(int i, int i2, int i3, int i4, boolean z);

    int measurePanelWidth(int i, int i2, int i3);

    TypedValue selectLimitValue(boolean z, boolean z2, int i, DialogContract.ValueList valueList);
}
