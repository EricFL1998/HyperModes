package miuix.view;

/* JADX INFO: loaded from: classes3.dex */
public interface DynamicThemeWidget {
    public static final int DARK_THEME = 2;
    public static final int DEFAULT_THEME = 0;
    public static final int LIGHT_THEME = 1;

    default int getThemeType() {
        return 0;
    }

    default boolean hasThemeType() {
        return false;
    }

    default void setThemeType(int i) {
    }
}
