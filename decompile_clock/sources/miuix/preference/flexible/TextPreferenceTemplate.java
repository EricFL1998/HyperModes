package miuix.preference.flexible;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import miuix.preference.R;

/* JADX INFO: loaded from: classes3.dex */
public class TextPreferenceTemplate extends AbstractBaseTemplate {
    private boolean mHasTextView = false;

    @Override // miuix.preference.flexible.AbstractBaseTemplate
    public int onLargeLayoutSelected() {
        if (this.mHasTitle && this.mHasSummary && this.mHasTextView) {
            return PreferenceMarkLevel.LEVEL_LARGE_FULL_TITLE_MULTI_TEXT;
        }
        if (this.mHasTitle && this.mHasTextView && !this.mHasSummary) {
            return PreferenceMarkLevel.LEVEL_LARGE_TITLE_MULTI_WIDGET_TEXT;
        }
        if (this.mHasTitle && this.mHasSummary && !this.mHasTextView) {
            return PreferenceMarkLevel.LEVEL_LARGE_FULL_TITLE_MULTI;
        }
        return (!this.mHasTitle || this.mHasSummary || this.mHasTextView) ? PreferenceMarkLevel.LEVEL_LARGE_TITLE_MULTI_WIDGET_TEXT : PreferenceMarkLevel.LEVEL_LARGE_ONLY_TITLE;
    }

    @Override // miuix.preference.flexible.AbstractBaseTemplate
    public void checkView(ViewGroup viewGroup) {
        View viewFindViewByAreaId = findViewByAreaId(viewGroup, R.id.area_end);
        if (this.mHasWidget && (viewFindViewByAreaId instanceof LinearLayout)) {
            View childAt = ((LinearLayout) viewFindViewByAreaId).getChildAt(0);
            if (childAt instanceof TextView) {
                this.mHasTextView = childAt.getVisibility() == 0;
            }
        }
    }
}
