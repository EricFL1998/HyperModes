package miuix.theme.token;

/* JADX INFO: loaded from: classes3.dex */
public class ColorBlendToken {
    public int[] blendModes;
    public int[] colors;
    public float[] extraBlendParams;
    public int[] fallbackBlendModes;
    public int[] fallbackColors;
    public float[] fallbackExtraBlendParams;
    public static ColorBlendToken Info_Extra_Thin_Light = new Builder().setConfig(new int[]{1001501105}, new int[]{BlendModeToken.PLUS_DARKER.value}).build();
    public static ColorBlendToken Info_Extra_Thin_Dark = new Builder().setConfig(new int[]{1001501105}, new int[]{BlendModeToken.PLUS_LIGHTER.value}).build();
    public static ColorBlendToken Info_Thin_Light = new Builder().setConfig(new int[]{-2137483112}, new int[]{BlendModeToken.PLUS_LIGHTER.value}).build();
    public static ColorBlendToken Info_Thin_Dark = new Builder().setConfig(new int[]{-2137483112}, new int[]{BlendModeToken.PLUS_DARKER.value}).build();
    public static ColorBlendToken Info_Regular_Light = new Builder().setConfig(new int[]{-1281845096}, new int[]{BlendModeToken.PLUS_LIGHTER.value}).build();
    public static ColorBlendToken Info_Regular_Dark = new Builder().setConfig(new int[]{-1281845096}, new int[]{BlendModeToken.PLUS_DARKER.value}).build();
    public static ColorBlendToken Info_Thick_Light = new Builder().setConfig(new int[]{-6776680}, new int[]{BlendModeToken.PLUS_LIGHTER.value}).build();
    public static ColorBlendToken Info_Thick_Dark = new Builder().setConfig(new int[]{-6776680}, new int[]{BlendModeToken.PLUS_DARKER.value}).build();
    public static ColorBlendToken Info_Colored_Regular = new Builder().setConfig(new int[]{-6579301, 268435455}, new int[]{BlendModeToken.COLOR_DODGE.value, BlendModeToken.PLUS_LIGHTER.value}).build();
    public static ColorBlendToken Colored_Extra_Thin_Light = new Builder().setConfig(new int[]{-2140772762, 653390321, 449958353}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.COLOR_DODGE.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Colored_Extra_Thin_Dark = new Builder().setConfig(new int[]{-942879540, 777146962}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Colored_Thin_Light = new Builder().setConfig(new int[]{-1719697537, -2130772226}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.SOFT_LIGHT.value}).build();
    public static ColorBlendToken Colored_Thin_Dark = new Builder().setConfig(new int[]{442918502, 861427800}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.PLUS_DARKER.value}).setFallbackConfig(new int[]{694576742, 1293687836, 436207616}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.OVERLAY.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Colored_Regular_Light = new Builder().setConfig(new int[]{-2052675930, 486539263}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.PLUS_LIGHTER.value}).setFallbackConfig(new int[]{-1720355467, 1640022208, 1493172224}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.COLOR_BURN.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Colored_Regular_Dark = new Builder().setConfig(new int[]{1879048192, 335544320}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Colored_Thick_Light = new Builder().setConfig(new int[]{-428575628, -1722658222, 869388753}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.COLOR_DODGE.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Colored_Thick_Dark = new Builder().setConfig(new int[]{1719105399, 863270004, 855638016}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.OVERLAY.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Colored_Extra_Thick_Light = new Builder().setConfig(new int[]{1301254031, 1807794368}, new int[]{BlendModeToken.PLUS_LIGHTER.value, BlendModeToken.COLOR_DODGE.value}).setFallbackConfig(new int[]{1305464783, 1388166589, 1895167477}, new int[]{BlendModeToken.COLOR_DODGE.value, BlendModeToken.OVERLAY.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Colored_Extra_Thick_Dark = new Builder().setConfig(new int[]{1718973813, 1640022208}, new int[]{BlendModeToken.PLUS_DARKER.value, BlendModeToken.COLOR_BURN.value}).setFallbackConfig(new int[]{1299543413, 1640022208, 1493172224}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.COLOR_BURN.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Pured_Extra_Thin_Light = new Builder().setConfig(new int[]{-855638017, 1593835519, 620756991}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.PLUS_LIGHTER.value, BlendModeToken.SRC_OVER.value}).setFallbackConfig(new int[]{-855638017, -855638017, 1811939327}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.OVERLAY.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Pured_Extra_Thin_Dark = new Builder().setConfig(new int[]{-428838800, -871099372}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Pured_Thin_Light = new Builder().setConfig(new int[]{812477805, 1593835519, 1728053247}, new int[]{BlendModeToken.PLUS_LIGHTER.value, BlendModeToken.PLUS_LIGHTER.value, BlendModeToken.SRC_OVER.value}).setFallbackConfig(new int[]{-1284674195, 1728053247, -1459617793}, new int[]{BlendModeToken.COLOR_DODGE.value, BlendModeToken.OVERLAY.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Pured_Thin_Dark = new Builder().setConfig(new int[]{-1803715203, 1711276032}, new int[]{BlendModeToken.PLUS_DARKER.value, BlendModeToken.SRC_OVER.value}).setFallbackConfig(new int[]{-1552056963, 864520071, 1761607680}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.OVERLAY.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Pured_Regular_Light = new Builder().setConfig(new int[]{872020473, -1275068417}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.HARD_LIGHT.value}).build();
    public static ColorBlendToken Pured_Regular_Dark = new Builder().setConfig(new int[]{1962934272, 1375731712}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Pured_Thick_Light = new Builder().setConfig(new int[]{-1711276033, -1073741825}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Pured_Thick_Dark = new Builder().setConfig(new int[]{1291845632, -2146299374}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Pured_Extra_Thick_Light = new Builder().setConfig(new int[]{1728053247, -1711933963}, new int[]{BlendModeToken.PLUS_LIGHTER.value, BlendModeToken.SRC_OVER.value}).setFallbackConfig(new int[]{-2130706433, 1728053247, -1711933963}, new int[]{BlendModeToken.OVERLAY.value, BlendModeToken.COLOR_DODGE.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Pured_Extra_Thick_Dark = new Builder().setConfig(new int[]{-870770407, 1412510001}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.PLUS_LIGHTER.value}).setFallbackConfig(new int[]{-1037687258, 1415733858}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.COLOR_DODGE.value}).build();
    public static ColorBlendToken Overlay_Extra_Thin_Light = new Builder().setConfig(new int[]{261724569}, new int[]{BlendModeToken.LUMINOSITY.value}).build();
    public static ColorBlendToken Overlay_Extra_Thin_Dark = new Builder().setConfig(new int[]{1965500199}, new int[]{BlendModeToken.LUMINOSITY.value}).build();
    public static ColorBlendToken Overlay_Thin_Light = new Builder().setConfig(new int[]{1301714582, 442918502}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.PLUS_DARKER.value}).setFallbackConfig(new int[]{1301714582, 862348902}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.COLOR_BURN.value}).build();
    public static ColorBlendToken Overlay_Regular_Light = new Builder().setConfig(new int[]{1301714582, 436207616}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.PLUS_DARKER.value}).setFallbackConfig(new int[]{864191106, 721420288}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.COLOR_BURN.value}).build();
    public static ColorBlendToken Overlay_Thick_Light = new Builder().setConfig(new int[]{-1474355425, -6645094}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.OVERLAY.value}).build();
    public static ColorBlendToken Overlay_Thick_Dark = new Builder().setConfig(new int[]{1723052979, -1637456282}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.PLUS_DARKER.value}).setFallbackConfig(new int[]{1723052979, -1634824562, 1577058304}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.COLOR_BURN.value, BlendModeToken.SRC_OVER.value}).build();
    public static ColorBlendToken Overlay_Extra_Thick_Light = new Builder().setConfig(new int[]{-866033311, 1291845632}, new int[]{BlendModeToken.LUMINOSITY.value, BlendModeToken.COLOR_BURN.value}).build();

    @Deprecated
    public static ColorBlendToken ExtraHeavy_Light = new Builder().setConfig(new int[]{-1889443744, -1543503873}, new int[]{BlendModeToken.COLOR_DODGE.value, BlendModeToken.SRC_OVER.value}).build();

    @Deprecated
    public static ColorBlendToken ExtraHeavy_Dark = new Builder().setConfig(new int[]{1970500467, -1979711488, 184549375}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.SRC_OVER.value, BlendModeToken.SRC_OVER.value}).build();

    @Deprecated
    public static ColorBlendToken Heavy_Light = new Builder().setConfig(new int[]{-1502909589, -856295947}, new int[]{BlendModeToken.COLOR_DODGE.value, BlendModeToken.SRC_OVER.value}).build();

    @Deprecated
    public static ColorBlendToken Heavy_Dark = new Builder().setConfig(new int[]{-2141430692, -1088479457}, new int[]{BlendModeToken.COLOR_BURN.value, BlendModeToken.SRC_OVER.value}).build();

    public static class Builder {
        private final ColorBlendToken mToken = new ColorBlendToken();

        public Builder setConfig(int[] iArr, int[] iArr2) {
            this.mToken.colors = iArr;
            this.mToken.blendModes = iArr2;
            this.mToken.extraBlendParams = null;
            return this;
        }

        public Builder setConfig(int[] iArr, int[] iArr2, float[] fArr) {
            this.mToken.colors = iArr;
            this.mToken.blendModes = iArr2;
            this.mToken.extraBlendParams = fArr;
            return this;
        }

        public Builder setFallbackConfig(int[] iArr, int[] iArr2) {
            this.mToken.fallbackColors = iArr;
            this.mToken.fallbackBlendModes = iArr2;
            this.mToken.fallbackExtraBlendParams = null;
            return this;
        }

        public Builder setFallbackConfig(int[] iArr, int[] iArr2, float[] fArr) {
            this.mToken.fallbackColors = iArr;
            this.mToken.fallbackBlendModes = iArr2;
            this.mToken.fallbackExtraBlendParams = fArr;
            return this;
        }

        public ColorBlendToken build() {
            return this.mToken;
        }
    }
}
