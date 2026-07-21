package miuix.popupwidget.internal.strategy;

import android.graphics.Rect;

/* JADX INFO: loaded from: classes3.dex */
public class PopupWindowSpec implements Cloneable {
    public int layoutDirection;
    public int mContentHeight;
    public int mContentWidth;
    public int mFinalPopupHeight;
    public int mFinalPopupWidth;
    public int[][] mItemViewBounds;
    public int mMaxHeight;
    public int mMaxWidth;
    public int mMinHeight;
    public int mMinWidth;
    public boolean mOffsetXSet;
    public boolean mOffsetYSet;
    public Rect mSafeInsets;
    public int mUserOffsetX;
    public int mUserOffsetY;
    public int mGravity = 8388693;
    public Rect mContentViewBounds = new Rect();
    public Rect mDecorViewBounds = new Rect();
    public Rect mAnchorViewBounds = new Rect();

    public String toString() {
        return "PopupWindowSpec{mMaxWidth=" + this.mMaxWidth + ", mMinWidth=" + this.mMinWidth + ", mMaxHeight=" + this.mMaxHeight + ", mMinHeight=" + this.mMinHeight + ", mContentWidth=" + this.mContentWidth + ", mContentHeight=" + this.mContentHeight + ", mFinalPopupWidth=" + this.mFinalPopupWidth + ", mFinalPopupHeight=" + this.mFinalPopupHeight + ", mGravity=" + this.mGravity + ", mUserOffsetX=" + this.mUserOffsetX + ", mUserOffsetY=" + this.mUserOffsetY + ", mOffsetXSet=" + this.mOffsetXSet + ", mOffsetYSet=" + this.mOffsetYSet + ", mItemViewBounds=" + convertItemViewBounds(this.mItemViewBounds) + ", mDecorViewBounds=" + this.mDecorViewBounds.flattenToString() + ", mAnchorViewBounds=" + this.mAnchorViewBounds.flattenToString() + ", mSafeInsets=" + this.mSafeInsets.flattenToString() + ", layoutDirection=" + this.layoutDirection + '}';
    }

    public PopupWindowSpec clone() {
        try {
            PopupWindowSpec popupWindowSpec = (PopupWindowSpec) super.clone();
            popupWindowSpec.mMaxWidth = this.mMaxWidth;
            popupWindowSpec.mMinWidth = this.mMinWidth;
            popupWindowSpec.mMaxHeight = this.mMaxHeight;
            popupWindowSpec.mMinHeight = this.mMinHeight;
            popupWindowSpec.mContentWidth = this.mContentWidth;
            popupWindowSpec.mContentHeight = this.mContentHeight;
            popupWindowSpec.mFinalPopupWidth = this.mFinalPopupWidth;
            popupWindowSpec.mFinalPopupHeight = this.mFinalPopupHeight;
            popupWindowSpec.mGravity = this.mGravity;
            popupWindowSpec.mUserOffsetX = this.mUserOffsetX;
            popupWindowSpec.mUserOffsetY = this.mUserOffsetY;
            popupWindowSpec.mOffsetXSet = this.mOffsetXSet;
            popupWindowSpec.mOffsetYSet = this.mOffsetYSet;
            popupWindowSpec.mItemViewBounds = this.mItemViewBounds;
            popupWindowSpec.mContentViewBounds = new Rect(this.mContentViewBounds.left, this.mContentViewBounds.top, this.mContentViewBounds.right, this.mContentViewBounds.bottom);
            popupWindowSpec.mDecorViewBounds = new Rect(this.mDecorViewBounds.left, this.mDecorViewBounds.top, this.mDecorViewBounds.right, this.mDecorViewBounds.bottom);
            popupWindowSpec.mAnchorViewBounds = new Rect(this.mAnchorViewBounds.left, this.mAnchorViewBounds.top, this.mAnchorViewBounds.right, this.mAnchorViewBounds.bottom);
            popupWindowSpec.mSafeInsets = this.mSafeInsets;
            popupWindowSpec.layoutDirection = this.layoutDirection;
            return popupWindowSpec;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertItemViewBounds(int[][] iArr) {
        if (iArr != null) {
            StringBuilder sb = new StringBuilder();
            for (int[] iArr2 : iArr) {
                sb.append(String.format("{%d, %d},", Integer.valueOf(iArr2[0]), Integer.valueOf(iArr2[1])));
            }
            return sb.toString();
        }
        return "null";
    }
}
