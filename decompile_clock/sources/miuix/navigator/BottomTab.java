package miuix.navigator;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import miuix.navigator.navigatorinfo.NavigatorInfo;

/* JADX INFO: loaded from: classes3.dex */
public class BottomTab implements NavigationItem {
    static final int NO_ICON = 0;
    private NavigatorInfo mNavigatorInfo;
    public int groupId = 0;
    public int id = 0;
    public int categoryOrder = 0;
    public int order = 0;
    private int mTitleResId = 0;
    private CharSequence mTitle = null;
    private int mIconResId = 0;
    private Drawable mIconDrawable = null;
    private ColorStateList mIconTintList = null;
    private PorterDuff.Mode mIconTintMode = null;
    private CharSequence mContentDescription = null;

    public void setTitle(int i) {
        this.mTitleResId = i;
        this.mTitle = null;
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitleResId = 0;
        this.mTitle = charSequence;
    }

    public int getTitleResId() {
        return this.mTitleResId;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public BottomTab setIcon(int i) {
        this.mIconResId = i;
        this.mIconDrawable = null;
        return this;
    }

    public BottomTab setIcon(Drawable drawable) {
        this.mIconResId = 0;
        this.mIconDrawable = drawable;
        return this;
    }

    public int getIconResId() {
        return this.mIconResId;
    }

    public Drawable getIconDrawable() {
        return this.mIconDrawable;
    }

    public BottomTab setIconTintList(ColorStateList colorStateList) {
        this.mIconTintList = colorStateList;
        return this;
    }

    public ColorStateList getIconTintList() {
        return this.mIconTintList;
    }

    public BottomTab setIconTintMode(PorterDuff.Mode mode) {
        this.mIconTintMode = mode;
        return this;
    }

    public PorterDuff.Mode getIconTintMode() {
        return this.mIconTintMode;
    }

    public BottomTab setContentDescription(CharSequence charSequence) {
        this.mContentDescription = charSequence;
        return this;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    @Override // miuix.navigator.NavigationItem
    public void setNavigatorInfo(NavigatorInfo navigatorInfo) {
        this.mNavigatorInfo = navigatorInfo;
    }

    @Override // miuix.navigator.NavigationItem
    public NavigatorInfo getNavigatorInfo() {
        return this.mNavigatorInfo;
    }
}
