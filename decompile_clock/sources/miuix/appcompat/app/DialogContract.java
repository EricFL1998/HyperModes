package miuix.appcompat.app;

import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;

/* JADX INFO: loaded from: classes2.dex */
public class DialogContract {
    public static final float BUTTON_PANEL_THRESHOLD = 0.4f;
    public static final float BUTTON_PANEL_THRESHOLD_LARGE_FONT = 0.3f;
    public static final int BUTTON_SCROLL_WINDOW_HEIGHT_LIMIT_DP = 480;
    public static final int LIMIT_PANEL_WIDTH_THRESHOLD = 404;
    public static final float TOP_PANEL_THRESHOLD = 0.45f;
    public static final float TOP_PANEL_THRESHOLD_LARGE_FONT = 0.35f;
    public static final int WIDTH_MARGIN_THRESHOLD = 360;

    public static class DimensConfig {
        public int extraImeMargin = -1;
        public int fakeLandScreenMinorSize;
        public int freePhoneCompactHeight;
        public int freeTabletCompactHeight;
        public int listViewMarginBottom;
        public int panelMaxWidth;
        public int panelMaxWidthLand;
        public int smallIconHeight;
        public int smallIconWidth;
        public int widthMargin;
        public int widthSmallMargin;
    }

    private DialogContract() {
    }

    public static Rect mergeInsets(Rect rect, Rect rect2) {
        Rect rect3 = new Rect();
        rect3.left = Math.max(rect != null ? rect.left : 0, rect2 != null ? rect2.left : 0);
        rect3.top = Math.max(rect != null ? rect.top : 0, rect2 != null ? rect2.top : 0);
        rect3.right = Math.max(rect != null ? rect.right : 0, rect2 != null ? rect2.right : 0);
        rect3.bottom = Math.max(rect != null ? rect.bottom : 0, rect2 != null ? rect2.bottom : 0);
        return rect3;
    }

    public static Rect insetsToRect(Insets insets) {
        Rect rect = new Rect();
        if (insets == null) {
            return rect;
        }
        rect.left = insets.left;
        rect.top = insets.top;
        rect.right = insets.right;
        rect.bottom = insets.bottom;
        return rect;
    }

    public static class OrientationSpec {
        public boolean mInFreeFrom;
        public boolean mIsCarWithScreen;
        public boolean mIsSynergy;
        public boolean mMarkLandscape;
        public int mScreenOrientation;
        public Point mWindowSize = new Point();
        public Point mUsableWindowSizeDp = new Point();
        public Point mRealScreenSize = new Point();

        public void updateData(boolean z, boolean z2, int i, boolean z3, boolean z4) {
            this.mMarkLandscape = z;
            this.mInFreeFrom = z2;
            this.mScreenOrientation = i;
            this.mIsCarWithScreen = z3;
            this.mIsSynergy = z4;
        }
    }

    public static class PanelWidthSpec {
        public boolean mIsCarWithScreen;
        public boolean mIsDebugMode;
        public boolean mIsLandscapeWindow;
        public boolean mMarkLandscapeWindow;
        public int mScreenMinorSize;
        public int mUsableWindowWidthDp;
        public boolean mUseLandscapeLayout;

        public void updateData(boolean z, boolean z2, boolean z3, boolean z4, int i, int i2, boolean z5) {
            this.mUseLandscapeLayout = z;
            this.mIsLandscapeWindow = z2;
            this.mIsCarWithScreen = z3;
            this.mMarkLandscapeWindow = z4;
            this.mUsableWindowWidthDp = i;
            this.mScreenMinorSize = i2;
            this.mIsDebugMode = z5;
        }

        public String toString() {
            return "PanelWidthSpec{mUseLandscapeLayout=" + this.mUseLandscapeLayout + ", mIsLandscapeWindow=" + this.mIsLandscapeWindow + ", mIsCarWithScreen=" + this.mIsCarWithScreen + ", mMarkLandscapeWindow=" + this.mMarkLandscapeWindow + ", mUsableWindowWidthDp=" + this.mUsableWindowWidthDp + ", mScreenMinorSize=" + this.mScreenMinorSize + ", mIsDebugMode=" + this.mIsDebugMode + '}';
        }
    }

    public static class PanelPosSpec {
        public Rect mBoundInsets = new Rect();
        public int mDesignedPanelWidth;
        public boolean mIsDebugMode;
        public boolean mIsFlipTiny;
        public int mRootViewPaddingLeft;
        public int mRootViewPaddingRight;
        public int mRootViewSizeX;
        public int mRootViewWidth;
        public int mUsableWindowWidth;
        public int mUsableWindowWidthDp;

        public void updateData(int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean z, boolean z2) {
            this.mRootViewPaddingLeft = i;
            this.mRootViewPaddingRight = i2;
            this.mRootViewWidth = i3;
            this.mDesignedPanelWidth = i4;
            this.mUsableWindowWidthDp = i5;
            this.mUsableWindowWidth = i6;
            this.mRootViewSizeX = i7;
            this.mIsFlipTiny = z;
            this.mIsDebugMode = z2;
        }

        public String toString() {
            return "PanelPosSpec{mRootViewPaddingLeft=" + this.mRootViewPaddingLeft + ", mRootViewPaddingRight=" + this.mRootViewPaddingRight + ", mRootViewWidth=" + this.mRootViewWidth + ", mDesignedPanelWidth=" + this.mDesignedPanelWidth + ", mUsableWindowWidthDp=" + this.mUsableWindowWidthDp + ", mUsableWindowWidth=" + this.mUsableWindowWidth + ", mRootViewSizeX=" + this.mRootViewSizeX + ", mIsFlipTiny=" + this.mIsFlipTiny + ", mIsDebugMode=" + this.mIsDebugMode + ", mBoundInsets=" + this.mBoundInsets + '}';
        }
    }

    public static class ButtonScrollSpec {
        public boolean mHasListView;
        public boolean mIsFlipTiny;
        public boolean mIsLargeFont;
        public int mRootViewSizeYDp;
        public int mVisibleButtonCount;
        public int mWindowOrientation;
        public int mButtonFVHeight = 0;
        public int mButtonPanelHeight = 0;
        public int mWindowHeight = 0;
        public int mTopPanelHeight = 0;

        public void updateData(int i, int i2, int i3, int i4, boolean z, int i5, int i6, int i7, boolean z2, boolean z3) {
            this.mButtonFVHeight = i;
            this.mButtonPanelHeight = i2;
            this.mWindowHeight = i3;
            this.mTopPanelHeight = i4;
            this.mIsFlipTiny = z;
            this.mWindowOrientation = i5;
            this.mVisibleButtonCount = i6;
            this.mRootViewSizeYDp = i7;
            this.mIsLargeFont = z2;
            this.mHasListView = z3;
        }

        public String toString() {
            return "ButtonScrollSpec{mButtonFVHeight=" + this.mButtonFVHeight + ", mButtonPanelHeight=" + this.mButtonPanelHeight + ", mWindowHeight=" + this.mWindowHeight + ", mTopPanelHeight=" + this.mTopPanelHeight + ", mIsFlipTiny=" + this.mIsFlipTiny + ", mWindowOrientation=" + this.mWindowOrientation + ", mVisibleButtonCount=" + this.mVisibleButtonCount + ", mRootViewSizeYDp=" + this.mRootViewSizeYDp + ", mIsLargeFont=" + this.mIsLargeFont + ", mHasListView = " + this.mHasListView + '}';
        }
    }

    public static class ValueList {
        private final TypedValue full;
        private final TypedValue major;
        private final TypedValue minor;

        public ValueList(TypedValue typedValue, TypedValue typedValue2, TypedValue typedValue3) {
            this.minor = typedValue;
            this.major = typedValue2;
            this.full = typedValue2;
        }

        public TypedValue getMinor() {
            return this.minor;
        }

        public TypedValue getMajor() {
            return this.major;
        }

        public TypedValue getFull() {
            return this.full;
        }
    }
}
