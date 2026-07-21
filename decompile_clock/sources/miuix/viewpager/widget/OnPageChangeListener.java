package miuix.viewpager.widget;

/* JADX INFO: loaded from: classes3.dex */
public interface OnPageChangeListener {
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;

    void onPageScrollStateChanged(int i);

    void onPageScrolled(int i, float f, int i2);

    void onPageSelected(int i);
}
