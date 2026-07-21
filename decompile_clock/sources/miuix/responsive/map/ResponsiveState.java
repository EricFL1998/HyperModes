package miuix.responsive.map;

/* JADX INFO: loaded from: classes3.dex */
public class ResponsiveState {

    @Deprecated
    public static final int ESTIMATE_CATEGORY_BIG = 2;

    @Deprecated
    public static final int ESTIMATE_CATEGORY_SMALL = 1;

    @Deprecated
    public static final int ESTIMATE_CATEGORY_UNKNOWN = 0;
    public static final int RESPONSIVE_LAYOUT_BASE = 4096;
    public static final int RESPONSIVE_LAYOUT_FREEMODE_16_9 = 8193;
    public static final int RESPONSIVE_LAYOUT_FREEMODE_3_4 = 8194;
    public static final int RESPONSIVE_LAYOUT_FREEMODE_4_3 = 8195;
    public static final int RESPONSIVE_LAYOUT_FREEMODE_BASE = 8192;
    public static final int RESPONSIVE_LAYOUT_FREEMODE_NONE = 8192;
    public static final int RESPONSIVE_LAYOUT_FREEMODE_OTHER = 8196;
    public static final int RESPONSIVE_LAYOUT_FULL = 4103;
    public static final int RESPONSIVE_LAYOUT_HALF = 4098;
    public static final int RESPONSIVE_LAYOUT_ONE_THIRD = 4097;
    public static final int RESPONSIVE_LAYOUT_TWO_THIRD = 4100;
    public static final int RESPONSIVE_WINDOW_TYPE_COMPACT = 1;
    public static final int RESPONSIVE_WINDOW_TYPE_LARGE = 3;
    public static final int RESPONSIVE_WINDOW_TYPE_REGULAR = 2;
    public static final int RESPONSIVE_WINDOW_TYPE_TINY = 0;
    public static final int RESPONSIVE_WINDOW_TYPE_UNKNOWN = -1;
    private static final int TOLERANCE_PX = 1;
    private int mActualWindowHeight;
    private int mActualWindowWidth;

    @Deprecated
    private volatile int mEstimateCategory;
    private volatile int mResponsiveWindowType;
    private volatile int mScreenMode;
    private float mWindowDensity;
    private int mWindowHeightDp;
    private int mWindowWidthDp;

    public static class WindowInfoWrapper {

        @Deprecated
        public int windowCategory;
        public float windowDensity;
        public int windowHeight;
        public int windowHeightDp;
        public int windowMode;
        public int windowType;
        public int windowWidth;
        public int windowWidthDp;
    }

    public ResponsiveState() {
        setType(-1);
        setScreenMode(4103);
        setCategory(0);
        setWindowHeightDp(0);
        setWindowWidthDp(0);
        setActualWindowWidth(0);
        setActualWindowHeight(0);
    }

    public void setTo(ResponsiveState responsiveState) {
        if (responsiveState != null) {
            this.mResponsiveWindowType = responsiveState.mResponsiveWindowType;
            this.mScreenMode = responsiveState.mScreenMode;
            this.mWindowWidthDp = responsiveState.mWindowWidthDp;
            this.mWindowHeightDp = responsiveState.mWindowHeightDp;
            this.mActualWindowWidth = responsiveState.mActualWindowWidth;
            this.mActualWindowHeight = responsiveState.mActualWindowHeight;
            this.mEstimateCategory = responsiveState.mEstimateCategory;
        }
    }

    public void updateFromWindowInfoWrapper(WindowInfoWrapper windowInfoWrapper) {
        setType(windowInfoWrapper.windowType);
        setScreenMode(windowInfoWrapper.windowMode);
        setWindowWidthDp(windowInfoWrapper.windowWidthDp);
        setWindowHeightDp(windowInfoWrapper.windowHeightDp);
        setActualWindowWidth(windowInfoWrapper.windowWidth);
        setActualWindowHeight(windowInfoWrapper.windowHeight);
        setWindowDensity(windowInfoWrapper.windowDensity);
        setCategory(windowInfoWrapper.windowCategory);
    }

    public void setType(int i) {
        this.mResponsiveWindowType = i;
    }

    public int getType() {
        return this.mResponsiveWindowType;
    }

    public void setScreenMode(int i) {
        this.mScreenMode = i;
    }

    public int getScreenMode() {
        return this.mScreenMode;
    }

    public void setWindowWidthDp(int i) {
        this.mWindowWidthDp = i;
    }

    public int getWindowWidthDp() {
        return this.mWindowWidthDp;
    }

    public void setWindowHeightDp(int i) {
        this.mWindowHeightDp = i;
    }

    public int getWindowHeightDp() {
        return this.mWindowHeightDp;
    }

    @Deprecated
    public void setCategory(int i) {
        this.mEstimateCategory = i;
    }

    @Deprecated
    public int getCategory() {
        return this.mEstimateCategory;
    }

    public void setActualWindowWidth(int i) {
        this.mActualWindowWidth = i;
    }

    public int getActualWindowWidth() {
        return this.mActualWindowWidth;
    }

    public void setActualWindowHeight(int i) {
        this.mActualWindowHeight = i;
    }

    public int getActualWindowHeight() {
        return this.mActualWindowHeight;
    }

    public void setWindowDensity(float f) {
        this.mWindowDensity = f;
    }

    public float getWindowDensity() {
        return this.mWindowDensity;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ResponsiveState)) {
            return false;
        }
        ResponsiveState responsiveState = (ResponsiveState) obj;
        int i = this.mActualWindowWidth;
        int i2 = responsiveState.mActualWindowWidth;
        boolean z = i == i2 || Math.abs(i - i2) == 1;
        int i3 = this.mActualWindowHeight;
        int i4 = responsiveState.mActualWindowHeight;
        return this.mEstimateCategory == responsiveState.mEstimateCategory && this.mScreenMode == responsiveState.mScreenMode && z && (i3 == i4 || Math.abs(i3 - i4) == 1);
    }

    public void toScreenSpec(ScreenSpec screenSpec) {
        screenSpec.type = getType();
        screenSpec.category = getCategory();
        screenSpec.screenMode = getScreenMode();
        screenSpec.widthDp = getWindowWidthDp();
        screenSpec.heightDp = getWindowHeightDp();
        screenSpec.width = getActualWindowWidth();
        screenSpec.height = getActualWindowHeight();
    }

    public String toString() {
        return "ResponsiveState@" + hashCode() + "( type = " + this.mResponsiveWindowType + ", mode = " + this.mScreenMode + ", windowDensity " + this.mWindowDensity + ", wWidthDp " + this.mWindowWidthDp + ", wHeightDp " + this.mWindowHeightDp + ", wWidth " + this.mActualWindowWidth + ", wHeight " + this.mActualWindowHeight + " )";
    }
}
