package miuix.appcompat.app.floatingactivity;

/* JADX INFO: loaded from: classes2.dex */
public interface OnFloatingActivityCallback {
    public static final int TYPE_EVENT_CODE_BASE = 0;
    public static final int TYPE_FROM_ACTIONBAR_BUTTON_RETURN = 3;
    public static final int TYPE_FROM_BACKGROUND = 2;
    public static final int TYPE_FROM_PHYSICAL_RETURN = 4;
    public static final int TYPE_FROM_TOP = 1;

    boolean onFinish(int i);
}
