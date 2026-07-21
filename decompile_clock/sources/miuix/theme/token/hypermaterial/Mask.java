package miuix.theme.token.hypermaterial;

import com.miui.miwallpaper.MiuiWallpaperManager;
import miuix.theme.token.ColorBlendToken;
import miuix.theme.token.MaterialDayNightToken;
import miuix.theme.token.MaterialToken;

/* JADX INFO: loaded from: classes3.dex */
public class Mask {
    public static MaterialToken Pured_Extra_Thin_Light = new MaterialToken.Builder(30, "mask-pured-extra-thin", "light").setColorBlend(ColorBlendToken.Pured_Extra_Thin_Light).setMaskBlur(100).build();
    public static MaterialToken Pured_Extra_Thin_Dark = new MaterialToken.Builder(10, "mask-pured-extra-thin", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Pured_Extra_Thin_Dark).setMaskBlur(100).build();
    public static MaterialDayNightToken Pured_Extra_Thin = new MaterialDayNightToken(Pured_Extra_Thin_Light, Pured_Extra_Thin_Dark);
    public static MaterialToken Pured_Thin_Light = new MaterialToken.Builder(30, "mask-pured-thin", "light").setColorBlend(ColorBlendToken.Pured_Thin_Light).setMaskBlur(100).build();
    public static MaterialToken Pured_Thin_Dark = new MaterialToken.Builder(30, "mask-pured-thin", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Pured_Thin_Dark).setMaskBlur(100).build();
    public static MaterialDayNightToken Pured_Thin = new MaterialDayNightToken(Pured_Thin_Light, Pured_Thin_Dark);
    public static MaterialToken Pured_Regular_Light = new MaterialToken.Builder(10, "mask-pured-regular", "light").setColorBlend(ColorBlendToken.Pured_Regular_Light).setMaskBlur(40).build();
    public static MaterialToken Pured_Regular_Dark = new MaterialToken.Builder(10, "mask-pured-regular", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Pured_Regular_Dark).setMaskBlur(70).build();
    public static MaterialDayNightToken Pured_Regular = new MaterialDayNightToken(Pured_Regular_Light, Pured_Regular_Dark);
    public static MaterialToken Pured_Thick_Light = new MaterialToken.Builder(10, "mask-pured-thick", "light").setColorBlend(ColorBlendToken.Pured_Thick_Light).setMaskBlur(70).build();
    public static MaterialToken Pured_Thick_Dark = new MaterialToken.Builder(10, "mask-pured-thick", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Pured_Thick_Dark).setMaskBlur(70).build();
    public static MaterialDayNightToken Pured_Thick = new MaterialDayNightToken(Pured_Thick_Light, Pured_Thick_Dark);
    public static MaterialToken Pured_Extra_Thick_Light = new MaterialToken.Builder(30, "mask-pured-extra-thick", "light").setColorBlend(ColorBlendToken.Pured_Extra_Thick_Light).setMaskBlur(100).build();
    public static MaterialToken Pured_Extra_Thick_Dark = new MaterialToken.Builder(30, "mask-pured-extra-thick", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Pured_Extra_Thick_Dark).setMaskBlur(100).build();
    public static MaterialDayNightToken Pured_Extra_Thick = new MaterialDayNightToken(Pured_Extra_Thick_Light, Pured_Extra_Thick_Dark);

    public static MaterialToken Pured_Extra_Thin(boolean z) {
        return z ? Pured_Extra_Thin_Light : Pured_Extra_Thin_Dark;
    }

    public static MaterialToken Pured_Thin(boolean z) {
        return z ? Pured_Thin_Light : Pured_Thin_Dark;
    }

    public static MaterialToken Pured_Regular(boolean z) {
        return z ? Pured_Regular_Light : Pured_Regular_Dark;
    }

    public static MaterialToken Pured_Thick(boolean z) {
        return z ? Pured_Thick_Light : Pured_Thick_Dark;
    }

    public static MaterialToken Pured_Extra_Thick(boolean z) {
        return z ? Pured_Extra_Thick_Light : Pured_Extra_Thick_Dark;
    }
}
