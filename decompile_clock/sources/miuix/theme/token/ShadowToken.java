package miuix.theme.token;

/* JADX INFO: loaded from: classes3.dex */
public class ShadowToken {
    public int color;
    public float dispersion = 1.0f;
    public int offsetX;
    public int offsetY;
    public int radius;
    public static ShadowToken Low = new Builder().setConfig(1157627904, 26, 46).build();
    public static ShadowToken Regular = new Builder().setConfig(1375731712, 33, 52).build();
    public static ShadowToken High = new Builder().setConfig(1375731712, 77, 52).build();
    public static ShadowToken ExtraHigh = new Builder().setConfig(1711276032, 105, 92).build();
    public static ShadowToken Float = new Builder().setConfig(1291845632, 0, 79).build();

    public static class Builder {
        private final ShadowToken mToken = new ShadowToken();

        public Builder setConfig(int i, int i2, int i3) {
            this.mToken.color = i;
            this.mToken.offsetX = 0;
            this.mToken.offsetY = i2;
            this.mToken.radius = i3;
            return this;
        }

        public Builder setConfig(int i, int i2) {
            this.mToken.offsetX = 0;
            this.mToken.offsetY = i;
            this.mToken.radius = i2;
            return this;
        }

        public Builder setConfig(int i, int i2, int i3, int i4) {
            this.mToken.color = i;
            this.mToken.offsetX = i2;
            this.mToken.offsetY = i3;
            this.mToken.radius = i4;
            return this;
        }

        public ShadowToken build() {
            return this.mToken;
        }
    }
}
