package miuix.appcompat.internal.app.widget.actionbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.widget.TextViewCompat;
import miuix.appcompat.R;
import miuix.appcompat.internal.view.ColorTransitionTextView;
import miuix.core.util.RomUtils;
import miuix.internal.util.AttributeResolver;
import miuix.theme.Typography;

/* JADX INFO: loaded from: classes2.dex */
public class ExpandTitle {
    private Context mContext;
    private ColorTransitionTextView mExpandSubtitleView;
    private LinearLayout mExpandTitleLayout;
    private ColorTransitionTextView mExpandTitleView;
    private boolean mVisible = true;
    private boolean mTextColorTransitEnable = false;
    View.OnTouchListener subtitleTouchListener = new View.OnTouchListener() { // from class: miuix.appcompat.internal.app.widget.actionbar.ExpandTitle.1
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return !view.isClickable();
        }
    };
    private int mTitleStyle = R.style.Miuix_AppCompat_TextAppearance_WindowTitle_Expand;
    private int mSubtitleStyle = R.style.Miuix_AppCompat_TextAppearance_WindowTitle_Subtitle_Expand;

    public ExpandTitle(Context context) {
        this.mContext = context;
    }

    public void init() {
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        this.mExpandTitleLayout = linearLayout;
        linearLayout.setImportantForAccessibility(2);
        this.mExpandTitleLayout.setOrientation(1);
        this.mExpandTitleLayout.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.actionbar.ExpandTitle$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1835x71c83647();
            }
        });
        ColorTransitionTextView colorTransitionTextView = new ColorTransitionTextView(this.mContext, null, R.attr.expandTitleTheme);
        this.mExpandTitleView = colorTransitionTextView;
        colorTransitionTextView.setId(R.id.action_bar_title_expand);
        this.mExpandTitleView.setVerticalScrollBarEnabled(false);
        this.mExpandTitleView.setHorizontalScrollBarEnabled(false);
        this.mExpandTitleView.setFocusableInTouchMode(false);
        if (RomUtils.getHyperOsVersion() <= 1) {
            Typography.applyMiSansLight(this.mExpandTitleView);
        }
        this.mExpandTitleLayout.addView(this.mExpandTitleView, getChildLayoutParams());
        ColorTransitionTextView colorTransitionTextView2 = new ColorTransitionTextView(this.mContext, null, R.attr.expandSubtitleTheme);
        this.mExpandSubtitleView = colorTransitionTextView2;
        colorTransitionTextView2.setId(R.id.action_bar_subtitle_expand);
        this.mExpandSubtitleView.setVisibility(8);
        this.mExpandSubtitleView.setVerticalScrollBarEnabled(false);
        this.mExpandSubtitleView.setHorizontalScrollBarEnabled(false);
        this.mExpandTitleLayout.addView(this.mExpandSubtitleView, getChildLayoutParams());
        Resources resources = this.mContext.getResources();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mExpandSubtitleView.getLayoutParams();
        layoutParams.topMargin = resources.getDimensionPixelOffset(R.dimen.action_bar_subtitle_top_margin);
        layoutParams.bottomMargin = resources.getDimensionPixelOffset(R.dimen.action_bar_subtitle_bottom_margin);
    }

    /* JADX INFO: renamed from: lambda$init$0$miuix-appcompat-internal-app-widget-actionbar-ExpandTitle, reason: not valid java name */
    /* synthetic */ void m1835x71c83647() {
        this.mExpandTitleLayout.setBackground(AttributeResolver.resolveDrawable(this.mContext, android.R.attr.actionBarItemBackground));
    }

    private LinearLayout.LayoutParams getChildLayoutParams() {
        return new LinearLayout.LayoutParams(-2, -2);
    }

    public void setOnClickListener(View.OnClickListener onClickListener, boolean z) {
        this.mExpandTitleView.setOnClickListener(onClickListener);
        this.mExpandTitleView.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.actionbar.ExpandTitle$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1836xfd32bab3();
            }
        });
        this.mExpandTitleView.setClickable(z);
    }

    /* JADX INFO: renamed from: lambda$setOnClickListener$1$miuix-appcompat-internal-app-widget-actionbar-ExpandTitle, reason: not valid java name */
    /* synthetic */ void m1836xfd32bab3() {
        this.mExpandTitleLayout.setTouchDelegate(new TouchDelegate(new Rect(0, 0, this.mExpandTitleLayout.getWidth(), this.mExpandTitleLayout.getHeight()), this.mExpandTitleView));
    }

    public void setSubTitleOnClickListener(View.OnClickListener onClickListener, boolean z) {
        ColorTransitionTextView colorTransitionTextView = this.mExpandSubtitleView;
        if (colorTransitionTextView != null) {
            colorTransitionTextView.setOnClickListener(onClickListener);
            this.mExpandSubtitleView.setClickable(z);
            this.mExpandSubtitleView.setOnTouchListener(this.subtitleTouchListener);
        }
    }

    public void setAllTitlesClickable(boolean z) {
        ColorTransitionTextView colorTransitionTextView = this.mExpandTitleView;
        if (colorTransitionTextView != null) {
            colorTransitionTextView.setClickable(z);
        }
        ColorTransitionTextView colorTransitionTextView2 = this.mExpandSubtitleView;
        if (colorTransitionTextView2 != null) {
            colorTransitionTextView2.setClickable(z);
        }
    }

    public void setTitleClickable(boolean z) {
        ColorTransitionTextView colorTransitionTextView = this.mExpandTitleView;
        if (colorTransitionTextView != null) {
            colorTransitionTextView.setClickable(z);
        }
    }

    public void setSubTitleClickable(boolean z) {
        ColorTransitionTextView colorTransitionTextView = this.mExpandSubtitleView;
        if (colorTransitionTextView != null) {
            colorTransitionTextView.setClickable(z);
        }
    }

    public void setTitle(CharSequence charSequence) {
        this.mExpandTitleView.setText(charSequence);
        setEnabled(!TextUtils.isEmpty(charSequence));
    }

    public void setSubTitle(CharSequence charSequence) {
        this.mExpandSubtitleView.setText(charSequence);
        setSubTitleVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
    }

    public void setEnabled(boolean z) {
        this.mExpandTitleLayout.setEnabled(z);
    }

    public void setSubTitleVisibility(int i) {
        this.mExpandSubtitleView.setVisibility(i);
    }

    public void setVisible(boolean z) {
        if (this.mVisible != z) {
            this.mVisible = z;
            this.mExpandTitleLayout.setVisibility(z ? 0 : 4);
        }
    }

    public void setTitleVisibility(int i) {
        this.mExpandTitleView.setVisibility(i);
    }

    public void setVisibility(int i) {
        if (!this.mVisible && i == 0) {
            this.mExpandTitleLayout.setVisibility(4);
        } else {
            this.mExpandTitleLayout.setVisibility(i);
        }
    }

    public int getVisibility() {
        return this.mExpandTitleLayout.getVisibility();
    }

    public View getLayout() {
        return this.mExpandTitleLayout;
    }

    public void onConfigurationChanged(Configuration configuration) {
        this.mExpandTitleView.setTextAppearance(this.mTitleStyle);
        this.mExpandSubtitleView.setTextAppearance(this.mSubtitleStyle);
        if (RomUtils.getHyperOsVersion() <= 1) {
            Typography.applyMiSansLight(this.mExpandTitleView);
        }
    }

    public void setTextColorTransitEnable(boolean z, int i) {
        if (this.mTextColorTransitEnable != z) {
            if (!z) {
                this.mExpandTitleView.startColorTransition(false, false);
            }
            this.mTextColorTransitEnable = z;
            if (z && i == 1) {
                this.mExpandTitleView.startColorTransition(true, false);
            }
        }
    }

    public void startColorTransition(boolean z, boolean z2) {
        if (this.mTextColorTransitEnable) {
            this.mExpandTitleView.startColorTransition(z, z2);
        }
    }

    public void setTitleStyle(int i) {
        this.mTitleStyle = i;
        TextViewCompat.setTextAppearance(this.mExpandTitleView, i);
        this.mExpandTitleView.invalidate();
    }

    public void setSubTitleStyle(int i) {
        this.mSubtitleStyle = i;
        TextViewCompat.setTextAppearance(this.mExpandSubtitleView, i);
        this.mExpandSubtitleView.invalidate();
    }
}
