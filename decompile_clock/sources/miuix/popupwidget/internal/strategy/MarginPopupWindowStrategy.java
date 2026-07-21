package miuix.popupwidget.internal.strategy;

import android.graphics.Rect;
import android.view.Gravity;

/* JADX INFO: loaded from: classes3.dex */
public class MarginPopupWindowStrategy implements IPopupWindowStrategy {
    private static final int ANIMATION_MARGIN = 70;
    private static final int HALF_ANIMATION_MARGIN = 35;

    @Override // miuix.popupwidget.internal.strategy.IPopupWindowStrategy
    public void measureContentSize(PopupWindowSpec popupWindowSpec) {
        if (popupWindowSpec.mItemViewBounds != null) {
            int[][] iArr = popupWindowSpec.mItemViewBounds;
            int i = popupWindowSpec.mMaxWidth;
            int i2 = popupWindowSpec.mMaxHeight;
            int i3 = 0;
            int iMax = 0;
            for (int[] iArr2 : iArr) {
                int i4 = iArr2[0];
                int i5 = iArr2[1];
                if (i4 > i) {
                    i4 = i;
                }
                iMax = Math.max(i4, iMax);
                i3 += i5;
            }
            popupWindowSpec.mContentHeight = i3;
            if (i3 <= i2) {
                i2 = i3;
            }
            popupWindowSpec.mFinalPopupHeight = i2 + 70;
            popupWindowSpec.mContentWidth = Math.max(iMax, popupWindowSpec.mMinWidth);
            popupWindowSpec.mFinalPopupWidth = popupWindowSpec.mContentWidth + 70;
            return;
        }
        Rect rect = popupWindowSpec.mContentViewBounds;
        popupWindowSpec.mContentHeight = rect.height();
        popupWindowSpec.mFinalPopupWidth = rect.width();
        popupWindowSpec.mFinalPopupHeight = rect.height();
    }

    @Override // miuix.popupwidget.internal.strategy.IPopupWindowStrategy
    public boolean isNeedScroll(int i, PopupWindowSpec popupWindowSpec) {
        int i2 = popupWindowSpec.mContentHeight;
        return i2 > i || i2 > popupWindowSpec.mMaxHeight;
    }

    @Override // miuix.popupwidget.internal.strategy.IPopupWindowStrategy
    public int getXInWindow(PopupWindowSpec popupWindowSpec) {
        int absoluteGravity = Gravity.getAbsoluteGravity(popupWindowSpec.mGravity, popupWindowSpec.layoutDirection) & 7;
        if (absoluteGravity == 1) {
            return getXInWindowAlignCenterHorizontal(popupWindowSpec);
        }
        if (absoluteGravity == 5) {
            return getXInWindowAlightRight(popupWindowSpec);
        }
        return getXInWindowAlignLeft(popupWindowSpec);
    }

    private int getXInWindowAlignCenterHorizontal(PopupWindowSpec popupWindowSpec) {
        Rect rect = popupWindowSpec.mAnchorViewBounds;
        Rect rect2 = popupWindowSpec.mDecorViewBounds;
        Rect rect3 = popupWindowSpec.mSafeInsets;
        int i = popupWindowSpec.mFinalPopupWidth;
        int iCenterX = rect.centerX() - (i / 2);
        if (iCenterX + i > (rect2.right - rect3.right) + 35) {
            iCenterX = ((rect2.right - rect3.right) + 35) - i;
        }
        if (iCenterX < (rect2.left + rect3.left) - 35) {
            iCenterX = (rect2.left + rect3.left) - 35;
        }
        if (iCenterX + i > (rect2.right - rect3.right) + 35) {
            i = ((rect2.right - rect3.right) + 35) - iCenterX;
        }
        popupWindowSpec.mFinalPopupWidth = i;
        return iCenterX;
    }

    private int getXInWindowAlignLeft(PopupWindowSpec popupWindowSpec) {
        Rect rect = popupWindowSpec.mAnchorViewBounds;
        Rect rect2 = popupWindowSpec.mDecorViewBounds;
        Rect rect3 = popupWindowSpec.mSafeInsets;
        int i = popupWindowSpec.mFinalPopupWidth;
        int i2 = rect.left - 35;
        if (i2 < (rect2.left + rect3.left) - 35) {
            i2 = (rect2.left + rect3.left) - 35;
        }
        int i3 = i2 + i;
        if (i3 > (rect2.right - rect3.right) + 35) {
            i3 = (rect2.right - rect3.right) + 35;
        }
        int i4 = i3 - i;
        if (i4 >= (rect2.left + rect3.left) - 35) {
            return i4;
        }
        int i5 = (rect2.left + rect3.left) - 35;
        popupWindowSpec.mFinalPopupWidth = i3 - i5;
        return i5;
    }

    private int getXInWindowAlightRight(PopupWindowSpec popupWindowSpec) {
        Rect rect = popupWindowSpec.mAnchorViewBounds;
        Rect rect2 = popupWindowSpec.mDecorViewBounds;
        Rect rect3 = popupWindowSpec.mSafeInsets;
        int i = popupWindowSpec.mFinalPopupWidth;
        int i2 = rect.right + 35;
        if (i2 > (rect2.right - rect3.right) + 35) {
            i2 = (rect2.right - rect3.right) + 35;
        }
        int i3 = i2 - i;
        if (i3 < (rect2.left + rect3.left) - 35) {
            i3 = (rect2.left + rect3.left) - 35;
        }
        if (i3 + i > (rect2.right - rect3.right) + 35) {
            i = (rect2.right - rect3.right) - i3;
        }
        popupWindowSpec.mFinalPopupWidth = i;
        return i3;
    }

    @Override // miuix.popupwidget.internal.strategy.IPopupWindowStrategy
    public int getYInWindow(PopupWindowSpec popupWindowSpec) {
        if ((popupWindowSpec.mGravity & 112) == 48) {
            return adjustDecorHeight(getYInWindowAlignTop(popupWindowSpec), popupWindowSpec);
        }
        return adjustDecorHeight(getYInWindowAlignBottom(popupWindowSpec), popupWindowSpec);
    }

    private int adjustDecorHeight(int i, PopupWindowSpec popupWindowSpec) {
        if (i < popupWindowSpec.mDecorViewBounds.top + popupWindowSpec.mSafeInsets.top) {
            return popupWindowSpec.mDecorViewBounds.top + popupWindowSpec.mSafeInsets.top;
        }
        int i2 = popupWindowSpec.mFinalPopupHeight;
        int i3 = i + i2;
        if (i3 >= popupWindowSpec.mDecorViewBounds.bottom - popupWindowSpec.mSafeInsets.bottom) {
            i3 = popupWindowSpec.mDecorViewBounds.bottom - popupWindowSpec.mSafeInsets.bottom;
        }
        return i3 - i2;
    }

    private int getYInWindowAlignTop(PopupWindowSpec popupWindowSpec) {
        Rect rect = popupWindowSpec.mAnchorViewBounds;
        Rect rect2 = popupWindowSpec.mDecorViewBounds;
        Rect rect3 = popupWindowSpec.mSafeInsets;
        int i = popupWindowSpec.mFinalPopupHeight - 70;
        int i2 = rect.top;
        if (i2 < rect2.top + rect3.top) {
            i2 = rect2.top + rect3.top;
        }
        if (i2 + i < rect2.bottom - rect3.bottom) {
            return i2 - 35;
        }
        if (rect2.bottom - rect.top >= rect.top - rect2.top) {
            int iMin = (rect2.bottom - rect3.bottom) - i2;
            if (iMin < popupWindowSpec.mMinHeight) {
                iMin = Math.min(i, (rect2.height() - rect3.top) - rect3.bottom);
                i2 = (rect2.bottom - rect3.bottom) - iMin;
            }
            popupWindowSpec.mFinalPopupHeight = iMin + 70;
            return i2;
        }
        int iMin2 = Math.min(i, (rect.top - rect2.top) - rect3.top);
        if (iMin2 < popupWindowSpec.mMinHeight) {
            iMin2 = Math.min(i, (rect2.height() - rect3.top) - rect3.bottom);
        }
        int i3 = rect.top - iMin2;
        popupWindowSpec.mFinalPopupHeight = iMin2 + 70;
        return i3;
    }

    private int getYInWindowAlignBottom(PopupWindowSpec popupWindowSpec) {
        Rect rect = popupWindowSpec.mAnchorViewBounds;
        Rect rect2 = popupWindowSpec.mDecorViewBounds;
        Rect rect3 = popupWindowSpec.mSafeInsets;
        int i = popupWindowSpec.mFinalPopupHeight - 70;
        int i2 = rect.bottom;
        if (i2 < rect2.top + rect3.top) {
            i2 = rect2.top + rect3.top;
        }
        if (i2 + i < rect2.bottom - rect3.bottom) {
            return i2 - 35;
        }
        if (rect2.bottom - rect.top >= rect.top - rect2.top) {
            int iMin = (rect2.bottom - rect3.bottom) - i2;
            if (iMin < popupWindowSpec.mMinHeight) {
                iMin = Math.min(i, (rect2.height() - rect3.top) - rect3.bottom);
                i2 = (rect2.bottom - rect3.bottom) - iMin;
            }
            popupWindowSpec.mFinalPopupHeight = iMin + 70;
        } else {
            int iMin2 = Math.min(i, (rect.top - rect2.top) - rect3.top);
            if (iMin2 < popupWindowSpec.mMinHeight) {
                iMin2 = Math.min(i, (rect2.height() - rect3.top) - rect3.bottom);
            }
            popupWindowSpec.mFinalPopupHeight = iMin2 + 70;
            i2 = rect.top - iMin2;
        }
        return i2 - 35;
    }
}
