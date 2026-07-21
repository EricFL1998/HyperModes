package miuix.navigator;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;

/* JADX INFO: loaded from: classes3.dex */
public class ViewAfterNavigatorSwitchPresenter {
    private List<View> mViews = new ArrayList();
    private boolean mIsIgnoreHover = false;

    public void attachViews(View view) {
        if (view == null || this.mViews.contains(view)) {
            return;
        }
        this.mViews.add(view);
    }

    public void detachView(View view) {
        if (view == null || !this.mViews.contains(view)) {
            return;
        }
        this.mViews.remove(view);
    }

    public void setTranslationX(float f) {
        Iterator<View> it = this.mViews.iterator();
        while (it.hasNext()) {
            it.next().setTranslationX(f);
        }
    }

    public void setTranslationXTagForHover(float f) {
        for (View view : this.mViews) {
            if (view.getId() != R.id.up || !(view instanceof ViewGroup)) {
                view.setTag(miuix.folme.R.id.miuix_animation_tag_hover_set_translation_x, Float.valueOf(f));
                if (this.mIsIgnoreHover) {
                    Folme.useAt(view).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(view, new AnimConfig[0]);
                }
            }
        }
        this.mIsIgnoreHover = false;
    }

    public void ignoreHover() {
        if (this.mIsIgnoreHover) {
            return;
        }
        for (View view : this.mViews) {
            Folme.useAt(view).hover().ignoreHoverOf(view);
        }
        this.mIsIgnoreHover = true;
    }
}
