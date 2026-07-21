package androidx.core.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;

/* JADX INFO: loaded from: classes.dex */
public interface TintableCheckedTextView {
    ColorStateList getSupportCheckMarkTintList();

    PorterDuff.Mode getSupportCheckMarkTintMode();

    void setSupportCheckMarkTintList(ColorStateList colorStateList);

    void setSupportCheckMarkTintMode(PorterDuff.Mode mode);
}
