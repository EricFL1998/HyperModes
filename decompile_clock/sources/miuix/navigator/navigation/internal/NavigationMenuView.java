package miuix.navigator.navigation.internal;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import miuix.appcompat.view.menu.MenuBuilder;
import miuix.appcompat.view.menu.MenuView;
import miuix.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes3.dex */
public class NavigationMenuView extends RecyclerView implements MenuView {
    public boolean filterLeftoverView(int i) {
        return false;
    }

    @Override // miuix.appcompat.view.menu.MenuView
    public int getWindowAnimations() {
        return 0;
    }

    public boolean hasBackgroundView() {
        return false;
    }

    public boolean hasBlurBackgroundView() {
        return false;
    }

    @Override // miuix.appcompat.view.menu.MenuView
    public void initialize(MenuBuilder menuBuilder) {
    }

    public NavigationMenuView(Context context) {
        this(context, null);
    }

    public NavigationMenuView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NavigationMenuView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutManager(new LinearLayoutManager(context, 1, false));
    }
}
