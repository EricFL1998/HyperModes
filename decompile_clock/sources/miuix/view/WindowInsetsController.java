package miuix.view;

/* JADX INFO: loaded from: classes3.dex */
public interface WindowInsetsController {
    void applyWindowInsets(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6);

    public static class InsetsConfig {
        public boolean ignoreBottomInset;
        public boolean ignoreLeftInset;
        public boolean ignoreRightInset;
        public boolean ignoreTopInset;
        public boolean isFloatingMode;
        public boolean renderUnderBottomDecorations;

        public boolean update(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
            boolean z7;
            if (this.isFloatingMode != z) {
                this.isFloatingMode = z;
                z7 = true;
            } else {
                z7 = false;
            }
            if (this.renderUnderBottomDecorations != z2) {
                this.renderUnderBottomDecorations = z2;
                z7 = true;
            }
            if (this.ignoreLeftInset != z3) {
                this.ignoreLeftInset = z3;
                z7 = true;
            }
            if (this.ignoreTopInset != z4) {
                this.ignoreTopInset = z4;
                z7 = true;
            }
            if (this.ignoreRightInset != z5) {
                this.ignoreRightInset = z5;
                z7 = true;
            }
            if (this.ignoreBottomInset == z6) {
                return z7;
            }
            this.ignoreBottomInset = z6;
            return true;
        }

        public boolean update(InsetsConfig insetsConfig) {
            boolean z;
            boolean z2 = this.isFloatingMode;
            boolean z3 = insetsConfig.isFloatingMode;
            if (z2 != z3) {
                this.isFloatingMode = z3;
                z = true;
            } else {
                z = false;
            }
            boolean z4 = this.renderUnderBottomDecorations;
            boolean z5 = insetsConfig.renderUnderBottomDecorations;
            if (z4 != z5) {
                this.renderUnderBottomDecorations = z5;
                z = true;
            }
            boolean z6 = this.ignoreLeftInset;
            boolean z7 = insetsConfig.ignoreLeftInset;
            if (z6 != z7) {
                this.ignoreLeftInset = z7;
                z = true;
            }
            boolean z8 = this.ignoreTopInset;
            boolean z9 = insetsConfig.ignoreTopInset;
            if (z8 != z9) {
                this.ignoreTopInset = z9;
                z = true;
            }
            boolean z10 = this.ignoreRightInset;
            boolean z11 = insetsConfig.ignoreRightInset;
            if (z10 != z11) {
                this.ignoreRightInset = z11;
                z = true;
            }
            boolean z12 = this.ignoreBottomInset;
            boolean z13 = insetsConfig.ignoreBottomInset;
            if (z12 == z13) {
                return z;
            }
            this.ignoreBottomInset = z13;
            return true;
        }

        public String toString() {
            return "isFloatingMode: " + this.isFloatingMode + ", renderUnderBottomDecorations: " + this.renderUnderBottomDecorations + ", ignoreLeftInset: " + this.ignoreLeftInset + ", ignoreTopInset: " + this.ignoreTopInset + ", ignoreRightInset: " + this.ignoreRightInset + " ,ignoreBottomInset: " + this.ignoreBottomInset;
        }
    }
}
