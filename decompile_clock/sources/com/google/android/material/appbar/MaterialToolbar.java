package com.google.android.material.appbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import com.google.android.material.drawable.DrawableUtils;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.internal.ToolbarUtils;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.MaterialShapeUtils;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;

/* JADX INFO: loaded from: classes2.dex */
public class MaterialToolbar extends Toolbar {
    private static final int DEF_STYLE_RES = R.style.Widget_MaterialComponents_Toolbar;
    private static final ImageView.ScaleType[] LOGO_SCALE_TYPE_ARRAY = {ImageView.ScaleType.MATRIX, ImageView.ScaleType.FIT_XY, ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.FIT_END, ImageView.ScaleType.CENTER, ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE};
    private Boolean logoAdjustViewBounds;
    private ImageView.ScaleType logoScaleType;
    private Integer navigationIconTint;
    private boolean subtitleCentered;
    private boolean titleCentered;

    public MaterialToolbar(Context context) {
        this(context, null);
    }

    public MaterialToolbar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.toolbarStyle);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public MaterialToolbar(Context context, AttributeSet attributeSet, int i) {
        int i2 = DEF_STYLE_RES;
        super(MaterialThemeOverlay.wrap(context, attributeSet, i, i2), attributeSet, i);
        Context context2 = getContext();
        TypedArray typedArrayObtainStyledAttributes = ThemeEnforcement.obtainStyledAttributes(context2, attributeSet, R.styleable.MaterialToolbar, i, i2, new int[0]);
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MaterialToolbar_navigationIconTint)) {
            setNavigationIconTint(typedArrayObtainStyledAttributes.getColor(R.styleable.MaterialToolbar_navigationIconTint, -1));
        }
        this.titleCentered = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialToolbar_titleCentered, false);
        this.subtitleCentered = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialToolbar_subtitleCentered, false);
        int i3 = typedArrayObtainStyledAttributes.getInt(R.styleable.MaterialToolbar_logoScaleType, -1);
        if (i3 >= 0) {
            ImageView.ScaleType[] scaleTypeArr = LOGO_SCALE_TYPE_ARRAY;
            if (i3 < scaleTypeArr.length) {
                this.logoScaleType = scaleTypeArr[i3];
            }
        }
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MaterialToolbar_logoAdjustViewBounds)) {
            this.logoAdjustViewBounds = Boolean.valueOf(typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialToolbar_logoAdjustViewBounds, false));
        }
        typedArrayObtainStyledAttributes.recycle();
        initBackground(context2);
    }

    @Override // androidx.appcompat.widget.Toolbar
    public void inflateMenu(int i) {
        Menu menu = getMenu();
        boolean z = menu instanceof MenuBuilder;
        if (z) {
            ((MenuBuilder) menu).stopDispatchingItemsChanged();
        }
        super.inflateMenu(i);
        if (z) {
            ((MenuBuilder) menu).startDispatchingItemsChanged();
        }
    }

    @Override // androidx.appcompat.widget.Toolbar, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        maybeCenterTitleViews();
        updateLogoImageView();
    }

    private void maybeCenterTitleViews() {
        if (this.titleCentered || this.subtitleCentered) {
            TextView titleTextView = ToolbarUtils.getTitleTextView(this);
            TextView subtitleTextView = ToolbarUtils.getSubtitleTextView(this);
            if (titleTextView == null && subtitleTextView == null) {
                return;
            }
            Pair<Integer, Integer> pairCalculateTitleBoundLimits = calculateTitleBoundLimits(titleTextView, subtitleTextView);
            if (this.titleCentered && titleTextView != null) {
                layoutTitleCenteredHorizontally(titleTextView, pairCalculateTitleBoundLimits);
            }
            if (!this.subtitleCentered || subtitleTextView == null) {
                return;
            }
            layoutTitleCenteredHorizontally(subtitleTextView, pairCalculateTitleBoundLimits);
        }
    }

    private Pair<Integer, Integer> calculateTitleBoundLimits(TextView textView, TextView textView2) {
        int measuredWidth = getMeasuredWidth();
        int i = measuredWidth / 2;
        int paddingLeft = getPaddingLeft();
        int paddingRight = measuredWidth - getPaddingRight();
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() != 8 && childAt != textView && childAt != textView2) {
                if (childAt.getRight() < i && childAt.getRight() > paddingLeft) {
                    paddingLeft = childAt.getRight();
                }
                if (childAt.getLeft() > i && childAt.getLeft() < paddingRight) {
                    paddingRight = childAt.getLeft();
                }
            }
        }
        return new Pair<>(Integer.valueOf(paddingLeft), Integer.valueOf(paddingRight));
    }

    private void layoutTitleCenteredHorizontally(View view, Pair<Integer, Integer> pair) {
        int measuredWidth = getMeasuredWidth();
        int measuredWidth2 = view.getMeasuredWidth();
        int i = (measuredWidth / 2) - (measuredWidth2 / 2);
        int i2 = measuredWidth2 + i;
        int iMax = Math.max(Math.max(((Integer) pair.first).intValue() - i, 0), Math.max(i2 - ((Integer) pair.second).intValue(), 0));
        if (iMax > 0) {
            i += iMax;
            i2 -= iMax;
            view.measure(View.MeasureSpec.makeMeasureSpec(i2 - i, BasicMeasure.EXACTLY), view.getMeasuredHeightAndState());
        }
        view.layout(i, view.getTop(), i2, view.getBottom());
    }

    private void updateLogoImageView() {
        ImageView logoImageView = ToolbarUtils.getLogoImageView(this);
        if (logoImageView != null) {
            Boolean bool = this.logoAdjustViewBounds;
            if (bool != null) {
                logoImageView.setAdjustViewBounds(bool.booleanValue());
            }
            ImageView.ScaleType scaleType = this.logoScaleType;
            if (scaleType != null) {
                logoImageView.setScaleType(scaleType);
            }
        }
    }

    public ImageView.ScaleType getLogoScaleType() {
        return this.logoScaleType;
    }

    public void setLogoScaleType(ImageView.ScaleType scaleType) {
        if (this.logoScaleType != scaleType) {
            this.logoScaleType = scaleType;
            requestLayout();
        }
    }

    public boolean isLogoAdjustViewBounds() {
        Boolean bool = this.logoAdjustViewBounds;
        return bool != null && bool.booleanValue();
    }

    public void setLogoAdjustViewBounds(boolean z) {
        Boolean bool = this.logoAdjustViewBounds;
        if (bool == null || bool.booleanValue() != z) {
            this.logoAdjustViewBounds = Boolean.valueOf(z);
            requestLayout();
        }
    }

    @Override // androidx.appcompat.widget.Toolbar, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MaterialShapeUtils.setParentAbsoluteElevation(this);
    }

    @Override // android.view.View
    public void setElevation(float f) {
        super.setElevation(f);
        MaterialShapeUtils.setElevation(this, f);
    }

    @Override // androidx.appcompat.widget.Toolbar
    public void setNavigationIcon(Drawable drawable) {
        super.setNavigationIcon(maybeTintNavigationIcon(drawable));
    }

    public void setNavigationIconTint(int i) {
        this.navigationIconTint = Integer.valueOf(i);
        Drawable navigationIcon = getNavigationIcon();
        if (navigationIcon != null) {
            setNavigationIcon(navigationIcon);
        }
    }

    public void clearNavigationIconTint() {
        this.navigationIconTint = null;
        Drawable navigationIcon = getNavigationIcon();
        if (navigationIcon != null) {
            DrawableCompat.setTintList(DrawableCompat.wrap(navigationIcon.mutate()), null);
            setNavigationIcon(navigationIcon);
        }
    }

    public Integer getNavigationIconTint() {
        return this.navigationIconTint;
    }

    public void setTitleCentered(boolean z) {
        if (this.titleCentered != z) {
            this.titleCentered = z;
            requestLayout();
        }
    }

    public boolean isTitleCentered() {
        return this.titleCentered;
    }

    public void setSubtitleCentered(boolean z) {
        if (this.subtitleCentered != z) {
            this.subtitleCentered = z;
            requestLayout();
        }
    }

    public boolean isSubtitleCentered() {
        return this.subtitleCentered;
    }

    private void initBackground(Context context) {
        ColorStateList colorStateListOrNull;
        Drawable background = getBackground();
        if (background == null) {
            colorStateListOrNull = ColorStateList.valueOf(0);
        } else {
            colorStateListOrNull = DrawableUtils.getColorStateListOrNull(background);
        }
        if (colorStateListOrNull != null) {
            MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
            materialShapeDrawable.setFillColor(colorStateListOrNull);
            materialShapeDrawable.initializeElevationOverlay(context);
            materialShapeDrawable.setElevation(ViewCompat.getElevation(this));
            ViewCompat.setBackground(this, materialShapeDrawable);
        }
    }

    private Drawable maybeTintNavigationIcon(Drawable drawable) {
        if (drawable == null || this.navigationIconTint == null) {
            return drawable;
        }
        Drawable drawableWrap = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(drawableWrap, this.navigationIconTint.intValue());
        return drawableWrap;
    }
}
