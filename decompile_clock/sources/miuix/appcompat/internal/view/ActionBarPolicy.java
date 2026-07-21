package miuix.appcompat.internal.view;

import android.content.Context;
import android.content.res.TypedArray;
import miuix.appcompat.R;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarPolicy {
    private Context mContext;

    public boolean showsOverflowMenuButton() {
        return true;
    }

    public static ActionBarPolicy get(Context context) {
        return new ActionBarPolicy(context);
    }

    private ActionBarPolicy(Context context) {
        this.mContext = context;
    }

    public int getMaxActionButtons() {
        return this.mContext.getResources().getInteger(R.integer.abc_max_action_buttons);
    }

    public int getEmbeddedMenuWidthLimit() {
        return this.mContext.getResources().getDisplayMetrics().widthPixels / 2;
    }

    public boolean hasEmbeddedTabs() {
        return AttributeResolver.resolveBoolean(this.mContext, R.attr.actionBarEmbedTabs, false);
    }

    public boolean isTightTitle() {
        return AttributeResolver.resolveBoolean(this.mContext, R.attr.actionBarTightTitle, false);
    }

    public boolean isTitleEnableEllipsis() {
        return AttributeResolver.resolveBoolean(this.mContext, R.attr.actionBarTitleEnableEllipsis, false);
    }

    public int getTabContainerHeight() {
        TypedArray typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(null, R.styleable.ActionBar, android.R.attr.actionBarTabBarStyle, 0);
        int layoutDimension = typedArrayObtainStyledAttributes.getLayoutDimension(R.styleable.ActionBar_android_height, 0);
        typedArrayObtainStyledAttributes.recycle();
        if (layoutDimension > 0) {
            return layoutDimension;
        }
        TypedArray typedArrayObtainStyledAttributes2 = this.mContext.obtainStyledAttributes(null, R.styleable.ActionBar, android.R.attr.actionBarStyle, 0);
        int layoutDimension2 = typedArrayObtainStyledAttributes2.getLayoutDimension(R.styleable.ActionBar_android_height, 0);
        typedArrayObtainStyledAttributes2.recycle();
        return layoutDimension2;
    }

    public boolean enableHomeButtonByDefault() {
        return this.mContext.getApplicationInfo().targetSdkVersion < 14;
    }

    public int getStackedTabMaxWidth() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_bar_stacked_tab_max_width);
    }
}
