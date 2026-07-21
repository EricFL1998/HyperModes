package miuix.core.view;

/* JADX INFO: loaded from: classes2.dex */
public interface ScrollStateDispatcher {
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;

    void addOnScrollChangeListener(ViewCompatOnScrollChangeListener viewCompatOnScrollChangeListener);

    void removeOnScrollChangeListener(ViewCompatOnScrollChangeListener viewCompatOnScrollChangeListener);
}
