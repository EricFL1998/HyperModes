package miuix.appcompat.app.strategy;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import miuix.appcompat.app.DialogContract;

/* JADX INFO: loaded from: classes2.dex */
public class DialogPanelBehaviorImpl implements IDialogPanelBehavior {
    @Override // miuix.appcompat.app.strategy.IDialogPanelBehavior
    public boolean shouldLimitPanelWidth(int i) {
        return i >= 404;
    }

    @Override // miuix.appcompat.app.strategy.IDialogPanelBehavior
    public boolean isLandscapeWindow(DialogContract.OrientationSpec orientationSpec) {
        if (orientationSpec.mMarkLandscape) {
            return true;
        }
        if (orientationSpec.mInFreeFrom) {
            return judgeLandscape(orientationSpec.mWindowSize, orientationSpec.mScreenOrientation);
        }
        if (orientationSpec.mScreenOrientation != 2) {
            return false;
        }
        if (orientationSpec.mIsCarWithScreen || orientationSpec.mIsSynergy) {
            return orientationSpec.mRealScreenSize.x > orientationSpec.mRealScreenSize.y;
        }
        return orientationSpec.mUsableWindowSizeDp.x >= 404 && orientationSpec.mUsableWindowSizeDp.x > orientationSpec.mUsableWindowSizeDp.y;
    }

    private boolean judgeLandscape(Point point, int i) {
        if (point.x > point.y) {
            return true;
        }
        return point.x >= point.y && i == 2;
    }

    @Override // miuix.appcompat.app.strategy.IDialogPanelBehavior
    public int calcPanelPosition(DialogContract.PanelPosSpec panelPosSpec, DialogContract.DimensConfig dimensConfig, Rect rect) {
        int i;
        int i2;
        Rect rect2 = rect == null ? new Rect() : rect;
        int iMax = Math.max(panelPosSpec.mRootViewSizeX, panelPosSpec.mRootViewWidth);
        boolean z = panelPosSpec.mRootViewPaddingLeft + panelPosSpec.mRootViewPaddingRight > 0;
        int i3 = panelPosSpec.mDesignedPanelWidth;
        int iCalcDesignedWidthMargin = calcDesignedWidthMargin(dimensConfig, panelPosSpec.mUsableWindowWidthDp);
        if (i3 == -1) {
            i3 = panelPosSpec.mUsableWindowWidth - (iCalcDesignedWidthMargin * 2);
        }
        int i4 = panelPosSpec.mIsFlipTiny ? dimensConfig.widthSmallMargin : dimensConfig.extraImeMargin;
        int iMax2 = Math.max(panelPosSpec.mBoundInsets.top, i4);
        int i5 = (panelPosSpec.mBoundInsets.left + panelPosSpec.mBoundInsets.right) / 2;
        int i6 = (iMax - i3) / 2;
        boolean z2 = i6 < panelPosSpec.mBoundInsets.left || i6 < panelPosSpec.mBoundInsets.right;
        if (panelPosSpec.mIsDebugMode) {
            Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: panelPosSpec = " + panelPosSpec);
            Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: avoidMoved = " + i5);
            Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: horizontalMargin = " + iCalcDesignedWidthMargin);
            Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: centerBlankSpace = " + i6);
            Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: widthSmallMargin = " + dimensConfig.widthSmallMargin);
        }
        if (i5 == 0 || !z2 || z) {
            i = iCalcDesignedWidthMargin;
            i2 = i;
        } else {
            if (panelPosSpec.mBoundInsets.left > panelPosSpec.mBoundInsets.right) {
                i = i5 + iCalcDesignedWidthMargin;
                i2 = iCalcDesignedWidthMargin;
            } else if (panelPosSpec.mBoundInsets.left < panelPosSpec.mBoundInsets.right) {
                i2 = i5 + iCalcDesignedWidthMargin;
                i = iCalcDesignedWidthMargin;
            } else {
                i = iCalcDesignedWidthMargin;
                i2 = i;
            }
            if (panelPosSpec.mIsDebugMode) {
                Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: leftMargin = " + i);
                Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: rightMargin = " + i2);
                Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: realRootViewWidth = " + iMax);
            }
        }
        boolean z3 = i6 < i5;
        if (z3) {
            i3 = panelPosSpec.mUsableWindowWidth - (iCalcDesignedWidthMargin * 2);
        }
        if (panelPosSpec.mIsDebugMode) {
            Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: isOverflow = " + z3);
            Log.d(IDialogPanelBehavior.TAG, "calcPanelPosition: panelWidth = " + i3);
        }
        rect2.left = i;
        rect2.top = iMax2;
        rect2.right = i2;
        rect2.bottom = i4;
        return i3;
    }

    @Override // miuix.appcompat.app.strategy.IDialogPanelBehavior
    public int calcDesignedPanelWidth(DialogContract.PanelWidthSpec panelWidthSpec, DialogContract.DimensConfig dimensConfig) {
        int i;
        boolean zShouldLimitPanelWidth = shouldLimitPanelWidth(panelWidthSpec.mUsableWindowWidthDp);
        if (panelWidthSpec.mUseLandscapeLayout) {
            i = dimensConfig.panelMaxWidthLand;
        } else if (zShouldLimitPanelWidth) {
            i = dimensConfig.panelMaxWidth;
        } else if (panelWidthSpec.mIsLandscapeWindow) {
            i = panelWidthSpec.mMarkLandscapeWindow ? dimensConfig.fakeLandScreenMinorSize : panelWidthSpec.mScreenMinorSize;
        } else {
            i = -1;
        }
        if (i != -1 && panelWidthSpec.mIsCarWithScreen) {
            i = (int) (i * 0.8f);
        }
        if (panelWidthSpec.mIsDebugMode) {
            Log.d(IDialogPanelBehavior.TAG, "calcDesignedPanelWidth: panelWidthSpec = " + panelWidthSpec);
            Log.d(IDialogPanelBehavior.TAG, "calcDesignedPanelWidth: shouldLimitPanelLimit = " + zShouldLimitPanelWidth);
            Log.d(IDialogPanelBehavior.TAG, "calcDesignedPanelWidth: panelWidth = " + i);
        }
        return i;
    }

    @Override // miuix.appcompat.app.strategy.IDialogPanelBehavior
    public int calcDesignedWidthMargin(DialogContract.DimensConfig dimensConfig, int i) {
        if (i < 360) {
            return dimensConfig.widthSmallMargin;
        }
        if (i <= 404) {
            return dimensConfig.widthMargin;
        }
        return 0;
    }
}
