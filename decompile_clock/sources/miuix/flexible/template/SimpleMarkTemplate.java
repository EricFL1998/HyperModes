package miuix.flexible.template;

import android.view.View;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes2.dex */
public class SimpleMarkTemplate extends AbstractMarkTemplate {
    @Override // miuix.flexible.template.AbstractMarkTemplate
    public HyperCellLayout.LayoutParams getLayoutParams(View view) {
        return getChildViewLayoutParamsSafe(view);
    }
}
