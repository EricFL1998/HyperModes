package miuix.preference.flexible;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import miuix.preference.R;

/* JADX INFO: loaded from: classes3.dex */
public class DropdownPreferenceTemplate extends AbstractBaseTemplate {
    @Override // miuix.preference.flexible.AbstractBaseTemplate
    public int onLargeLayoutSelected() {
        if (this.mHasTitle && this.mHasSummary) {
            return PreferenceMarkLevel.LEVEL_LARGE_FULL_TITLE_MULTI_TEXT;
        }
        return ((!this.mHasTitle || this.mHasSummary) && !this.mHasTitle && this.mHasSummary) ? PreferenceMarkLevel.LEVEL_LARGE_SUMMARY_WIDGET_TEXT : PreferenceMarkLevel.LEVEL_LARGE_TITLE_MULTI_WIDGET_TEXT;
    }

    @Override // miuix.preference.flexible.AbstractBaseTemplate
    public void checkView(ViewGroup viewGroup) {
        View viewFindViewByAreaId = findViewByAreaId(viewGroup, R.id.area_end2);
        if (viewFindViewByAreaId instanceof LinearLayout) {
            boolean z = false;
            View childAt = ((LinearLayout) viewFindViewByAreaId).getChildAt(0);
            if (childAt != null && childAt.getVisibility() == 0) {
                z = true;
            }
            this.mHasWidget = z;
        }
    }
}
