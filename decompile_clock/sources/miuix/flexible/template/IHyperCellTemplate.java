package miuix.flexible.template;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import miuix.flexible.template.level.LevelSupplier;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes2.dex */
public interface IHyperCellTemplate {
    void applyLevel();

    LevelSupplier createLevelSupplier();

    int getLevel();

    void init(ViewGroup viewGroup, Context context, AttributeSet attributeSet);

    void onAttachedToWindow(ViewGroup viewGroup);

    void onConfigurationChanged(ViewGroup viewGroup, Configuration configuration);

    void onDetachedFromWindow(ViewGroup viewGroup);

    void onFinishInflate(ViewGroup viewGroup);

    void onLayout(ViewGroup viewGroup, boolean z, int i, int i2, int i3, int i4);

    int[] onMeasure(ViewGroup viewGroup, int i, int i2);

    void onViewAdded(ViewGroup viewGroup, View view);

    void onViewRemoved(ViewGroup viewGroup, View view);

    void setLevelCallback(HyperCellLayout.LevelCallback levelCallback);
}
