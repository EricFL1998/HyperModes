package miuix.appcompat.app;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

/* JADX INFO: loaded from: classes2.dex */
public class TextViewDrawableConfig {
    private Drawable mDrawableStart = null;
    private Drawable mDrawableTop = null;
    private Drawable mDrawableEnd = null;
    private Drawable mDrawableBottom = null;
    private int mDrawableStartRes = 0;
    private int mDrawableTopRes = 0;
    private int mDrawableEndRes = 0;
    private int mDrawableBottomRes = 0;

    public static Builder createBuilder() {
        return new Builder();
    }

    public boolean isDrawableResAlreadySet() {
        return this.mDrawableStartRes > 0 || this.mDrawableTopRes > 0 || this.mDrawableEndRes > 0 || this.mDrawableBottomRes > 0;
    }

    public void setTextViewDrawable(TextView textView) {
        if (isDrawableResAlreadySet()) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(this.mDrawableStartRes, this.mDrawableTopRes, this.mDrawableEndRes, this.mDrawableBottomRes);
        } else {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(this.mDrawableStart, this.mDrawableTop, this.mDrawableEnd, this.mDrawableBottom);
        }
    }

    public static class Builder {
        private TextViewDrawableConfig mConfig = new TextViewDrawableConfig();

        public Builder setDrawableStart(Drawable drawable) {
            this.mConfig.mDrawableStart = drawable;
            return this;
        }

        public Builder setDrawableTop(Drawable drawable) {
            this.mConfig.mDrawableTop = drawable;
            return this;
        }

        public Builder setDrawableEnd(Drawable drawable) {
            this.mConfig.mDrawableEnd = drawable;
            return this;
        }

        public Builder setDrawableBottom(Drawable drawable) {
            this.mConfig.mDrawableBottom = drawable;
            return this;
        }

        public Builder setDrawableStartRes(int i) {
            this.mConfig.mDrawableStartRes = i;
            return this;
        }

        public Builder setDrawableTopRes(int i) {
            this.mConfig.mDrawableTopRes = i;
            return this;
        }

        public Builder setDrawableEndRes(int i) {
            this.mConfig.mDrawableEndRes = i;
            return this;
        }

        public Builder setDrawableBottomRes(int i) {
            this.mConfig.mDrawableBottomRes = i;
            return this;
        }

        public TextViewDrawableConfig build() {
            return this.mConfig;
        }
    }
}
