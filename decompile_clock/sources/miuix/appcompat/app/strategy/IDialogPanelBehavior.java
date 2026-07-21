package miuix.appcompat.app.strategy;

import android.graphics.Rect;
import miuix.appcompat.app.DialogContract;

/* JADX INFO: loaded from: classes2.dex */
public interface IDialogPanelBehavior {
    public static final String TAG = "IPanelBehavior";

    int calcDesignedPanelWidth(DialogContract.PanelWidthSpec panelWidthSpec, DialogContract.DimensConfig dimensConfig);

    int calcDesignedWidthMargin(DialogContract.DimensConfig dimensConfig, int i);

    int calcPanelPosition(DialogContract.PanelPosSpec panelPosSpec, DialogContract.DimensConfig dimensConfig, Rect rect);

    boolean isLandscapeWindow(DialogContract.OrientationSpec orientationSpec);

    boolean shouldLimitPanelWidth(int i);
}
