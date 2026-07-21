package miuix.theme.token.hypermaterial;

import com.miui.miwallpaper.MiuiWallpaperManager;
import miuix.theme.token.ColorBlendToken;
import miuix.theme.token.MaterialDayNightToken;
import miuix.theme.token.MaterialToken;

/* JADX INFO: loaded from: classes3.dex */
@Deprecated
public class Blur {

    @Deprecated
    public static MaterialToken ExtraHeavy_Light = new MaterialToken.Builder(10, "blur-extraheavy", "light").setColorBlend(ColorBlendToken.ExtraHeavy_Light).setBlur(1, 1, 0, 66).build();

    @Deprecated
    public static MaterialToken ExtraHeavy_Dark = new MaterialToken.Builder(10, "blur-extraheavy", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.ExtraHeavy_Dark).setBlur(1, 1, 0, 66).build();

    @Deprecated
    public static MaterialDayNightToken ExtraHeavy = new MaterialDayNightToken(ExtraHeavy_Light, ExtraHeavy_Dark);

    @Deprecated
    public static MaterialToken Heavy_Light = new MaterialToken.Builder(10, "blur-heavy", "light").setColorBlend(ColorBlendToken.Heavy_Light).setBlur(1, 1, 0, 66).build();

    @Deprecated
    public static MaterialToken Heavy_Dark = new MaterialToken.Builder(10, "blur-heavy", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Heavy_Dark).setBlur(1, 1, 0, 66).build();

    @Deprecated
    public static MaterialDayNightToken Heavy = new MaterialDayNightToken(Heavy_Light, Heavy_Dark);

    @Deprecated
    public static MaterialToken ExtraHeavy(boolean z) {
        return z ? ExtraHeavy_Light : ExtraHeavy_Dark;
    }

    @Deprecated
    public static MaterialToken Heavy(boolean z) {
        return z ? Heavy_Light : Heavy_Dark;
    }
}
