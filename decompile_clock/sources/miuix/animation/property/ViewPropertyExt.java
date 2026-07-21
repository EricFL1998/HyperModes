package miuix.animation.property;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import miuix.animation.R;

/* JADX INFO: loaded from: classes2.dex */
public class ViewPropertyExt {
    public static final BackgroundProperty BACKGROUND;
    public static final ForegroundProperty FOREGROUND;

    private ViewPropertyExt() {
    }

    public static class ForegroundProperty extends ViewProperty implements IIntValueProperty<View> {
        @Override // miuix.animation.property.FloatProperty
        public float getValue(View view) {
            return 0.0f;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(View view, float f) {
        }

        private ForegroundProperty() {
            super("foreground", 0.00390625f);
        }

        @Override // miuix.animation.property.IIntValueProperty
        public int getIntValue(View view) {
            Object tag = view.getTag(R.id.miuix_animation_tag_foreground_color);
            if (tag instanceof Integer) {
                return ((Integer) tag).intValue();
            }
            return 0;
        }

        @Override // miuix.animation.property.IIntValueProperty
        public void setIntValue(View view, int i) {
            view.setTag(R.id.miuix_animation_tag_foreground_color, Integer.valueOf(i));
            Drawable foreground = view.getForeground();
            if (foreground != null) {
                foreground.invalidateSelf();
            }
        }
    }

    public static class BackgroundProperty extends ViewProperty implements IIntValueProperty<View> {
        @Override // miuix.animation.property.FloatProperty
        public float getValue(View view) {
            return 0.0f;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(View view, float f) {
        }

        private BackgroundProperty() {
            super("background", 0.00390625f);
        }

        @Override // miuix.animation.property.IIntValueProperty
        public void setIntValue(View view, int i) {
            view.setBackgroundColor(i);
        }

        @Override // miuix.animation.property.IIntValueProperty
        public int getIntValue(View view) {
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                return ((ColorDrawable) background).getColor();
            }
            return 0;
        }
    }

    static {
        FOREGROUND = new ForegroundProperty();
        BACKGROUND = new BackgroundProperty();
    }
}
