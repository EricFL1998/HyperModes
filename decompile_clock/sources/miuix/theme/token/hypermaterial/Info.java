package miuix.theme.token.hypermaterial;

import com.miui.miwallpaper.MiuiWallpaperManager;
import miuix.theme.token.ColorBlendToken;
import miuix.theme.token.MaterialDayNightToken;
import miuix.theme.token.MaterialToken;

/* JADX INFO: loaded from: classes3.dex */
public class Info {
    public static MaterialToken Extra_Thin_Light = new MaterialToken.Builder(30, "info-extra-thin", "light").setColorBlend(ColorBlendToken.Info_Extra_Thin_Light).setBlur(0, 3, 0, 70).build();
    public static MaterialToken Extra_Thin_Dark = new MaterialToken.Builder(30, "info-extra-thin", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Info_Extra_Thin_Dark).setBlur(0, 4, 0, 70).build();
    public static MaterialDayNightToken Extra_Thin = new MaterialDayNightToken(Extra_Thin_Light, Extra_Thin_Dark);
    public static MaterialToken Thin_Light = new MaterialToken.Builder(30, "info-thin", "light").setColorBlend(ColorBlendToken.Info_Thin_Light).setBlur(0, 4, 0, 70).build();
    public static MaterialToken Thin_Dark = new MaterialToken.Builder(30, "info-thin", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Info_Thin_Dark).setBlur(0, 4, 0, 70).build();
    public static MaterialDayNightToken Thin = new MaterialDayNightToken(Thin_Light, Thin_Dark);
    public static MaterialToken Regular_Light = new MaterialToken.Builder(30, "info-regular", "light").setColorBlend(ColorBlendToken.Info_Regular_Light).setBlur(0, 4, 0, 70).build();
    public static MaterialToken Regular_Dark = new MaterialToken.Builder(30, "info-regular", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Info_Regular_Dark).setBlur(0, 4, 0, 70).build();
    public static MaterialDayNightToken Regular = new MaterialDayNightToken(Regular_Light, Regular_Dark);
    public static MaterialToken Thick_Light = new MaterialToken.Builder(30, "info-thick", "light").setColorBlend(ColorBlendToken.Info_Thick_Light).setBlur(0, 4, 0, 70).build();
    public static MaterialToken Thick_Dark = new MaterialToken.Builder(30, "info-thick", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Info_Thick_Dark).setBlur(0, 4, 0, 70).build();
    public static MaterialDayNightToken Thick = new MaterialDayNightToken(Thick_Light, Thick_Dark);
    public static MaterialToken Colored_Regular = new MaterialToken.Builder(30, "info-thick", "").setColorBlend(ColorBlendToken.Info_Colored_Regular).setBlur(0, 4, 0, 70).build();

    public static MaterialToken Extra_Thin(boolean z) {
        return z ? Extra_Thin_Light : Extra_Thin_Dark;
    }

    public static MaterialToken Thin(boolean z) {
        return z ? Thin_Light : Thin_Dark;
    }

    public static MaterialToken Regular(boolean z) {
        return z ? Regular_Light : Regular_Dark;
    }

    public static MaterialToken Thick(boolean z) {
        return z ? Thick_Light : Thick_Dark;
    }
}
