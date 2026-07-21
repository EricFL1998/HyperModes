package miuix.appcompat.internal.view.menu.context;

import android.content.Context;
import android.graphics.Rect;
import miuix.appcompat.R;
import miuix.popupwidget.internal.strategy.IPopupWindowStrategy;
import miuix.popupwidget.internal.strategy.PopupWindowSpec;

/* JADX INFO: loaded from: classes2.dex */
public class ContextMenuStrategy implements IPopupWindowStrategy {
    private static final float SCREEN_MARGIN_BOTTOM_PROPORTION = 0.1f;
    private static final float SCREEN_MARGIN_TOP_PROPORTION = 0.1f;
    private int marginScreen;
    private float x;
    private float y;

    public ContextMenuStrategy(Context context, float f, float f2) {
        this.x = f;
        this.y = f2;
        this.marginScreen = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_context_menu_window_margin_screen);
    }

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
            popupWindowSpec.mFinalPopupHeight = i2;
            int iMax2 = Math.max(iMax, popupWindowSpec.mMinWidth);
            popupWindowSpec.mContentWidth = iMax2;
            popupWindowSpec.mFinalPopupWidth = iMax2;
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
        Rect rect = popupWindowSpec.mAnchorViewBounds;
        Rect rect2 = popupWindowSpec.mDecorViewBounds;
        int i = rect.left + ((int) this.x);
        boolean z = i <= popupWindowSpec.mContentWidth;
        boolean z2 = i >= rect2.width() - popupWindowSpec.mContentWidth;
        if (z) {
            return this.marginScreen;
        }
        return z2 ? (rect2.width() - this.marginScreen) - popupWindowSpec.mContentWidth : i;
    }

    @Override // miuix.popupwidget.internal.strategy.IPopupWindowStrategy
    public int getYInWindow(PopupWindowSpec popupWindowSpec) {
        Rect rect = popupWindowSpec.mAnchorViewBounds;
        Rect rect2 = popupWindowSpec.mDecorViewBounds;
        float fHeight = (rect.top + ((int) this.y)) - (popupWindowSpec.mContentHeight / 2);
        if (fHeight < rect2.height() * 0.1f) {
            fHeight = rect2.height() * 0.1f;
        }
        float f = popupWindowSpec.mContentHeight;
        if (fHeight + f > rect2.height() * 0.9f) {
            fHeight = (rect2.height() * 0.9f) - f;
        }
        if (fHeight < rect2.height() * 0.1f) {
            fHeight = rect2.height() * 0.1f;
            popupWindowSpec.mFinalPopupHeight = (int) (rect2.height() * 0.79999995f);
        }
        return (int) fHeight;
    }
}
