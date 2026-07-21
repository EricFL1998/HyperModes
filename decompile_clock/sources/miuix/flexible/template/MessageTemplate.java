package miuix.flexible.template;

import android.view.ViewGroup;
import androidx.constraintlayout.widget.ConstraintLayout;
import miuix.flexible.R;

/* JADX INFO: loaded from: classes2.dex */
public class MessageTemplate extends AbstractAreaMappingTemplate {
    @Override // miuix.flexible.template.AbstractAreaMappingTemplate
    public int getTemplateResId() {
        if (getLevel() == 1) {
            return R.layout.template_message_normal;
        }
        return R.layout.template_message_large;
    }

    @Override // miuix.flexible.template.AbstractAreaMappingTemplate, miuix.flexible.template.IHyperCellTemplate
    public void onFinishInflate(ViewGroup viewGroup) {
        super.onFinishInflate(viewGroup);
    }

    @Override // miuix.flexible.template.AbstractAreaMappingTemplate
    public void onFinishLayoutMapping(ViewGroup viewGroup) {
        super.onFinishLayoutMapping(viewGroup);
        ((ConstraintLayout.LayoutParams) viewGroup.findViewById(R.id.area_title).getLayoutParams()).setMarginStart(20);
    }
}
