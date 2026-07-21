package miuix.appcompat.internal.app.widget.actionbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.core.widget.TextViewCompat;
import miuix.appcompat.R;
import miuix.appcompat.app.TextViewDrawableConfig;
import miuix.appcompat.internal.view.CollapseTitleColorTransitionTextView;
import miuix.appcompat.internal.view.ColorTransitionTextView;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.AttributeResolver;

/* JADX INFO: loaded from: classes2.dex */
public class CollapseTitle {
    private ColorTransitionTextView mCollapseSubtitleView;
    private LinearLayout mCollapseTitleLayout;
    private ColorTransitionTextView mCollapseTitleView;
    private Context mContext;
    private int mSubtitleStyle;
    private int mTitleStyle;
    private boolean mVisible = true;
    private float mDefaultSubtitleSize = 0.0f;
    private boolean mIsTitleDirty = false;
    private float mCollapseTitlePaintTextSize = -1.0f;
    private float mTitleLength = 0.0f;
    private boolean mSubtitleSizeable = true;
    private boolean mTextColorTransitEnable = false;
    private boolean mLargeFontAdaptEnable = false;
    private int mLargeFontTitleMaxLine = 2;
    View.OnTouchListener subtitleTouchListener = new View.OnTouchListener() { // from class: miuix.appcompat.internal.app.widget.actionbar.CollapseTitle.1
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return !view.isClickable();
        }
    };

    public void onConfigurationChanged(Configuration configuration) {
    }

    public CollapseTitle(Context context, int i, int i2) {
        this.mContext = context;
        this.mTitleStyle = i;
        this.mSubtitleStyle = i2;
    }

    public void init() {
        Resources resources = this.mContext.getResources();
        EnvStateManager.getWindowInfo(this.mContext);
        this.mDefaultSubtitleSize = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_subtitle_text_size);
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        this.mCollapseTitleLayout = linearLayout;
        linearLayout.setImportantForAccessibility(2);
        CollapseTitleColorTransitionTextView collapseTitleColorTransitionTextView = new CollapseTitleColorTransitionTextView(this.mContext, null, R.attr.collapseTitleTheme);
        this.mCollapseTitleView = collapseTitleColorTransitionTextView;
        collapseTitleColorTransitionTextView.setVerticalScrollBarEnabled(false);
        this.mCollapseTitleView.setHorizontalScrollBarEnabled(false);
        this.mCollapseTitleView.setFocusableInTouchMode(false);
        boolean z = AttributeResolver.resolveBoolean(this.mContext, R.attr.actionBarTitleAdaptLargeFont, true) && (MiuixUIUtils.getFontLevel(this.mContext) == 2);
        this.mLargeFontAdaptEnable = z;
        if (z) {
            this.mLargeFontTitleMaxLine = AttributeResolver.resolveInt(this.mContext, R.attr.collapseTitleLargeFontMaxLine, 2);
            this.mCollapseTitleView.setSingleLine(false);
            this.mCollapseTitleView.setMaxLines(this.mLargeFontTitleMaxLine);
        }
        ColorTransitionTextView colorTransitionTextView = new ColorTransitionTextView(this.mContext, null, R.attr.collapseSubtitleTheme);
        this.mCollapseSubtitleView = colorTransitionTextView;
        colorTransitionTextView.setVerticalScrollBarEnabled(false);
        this.mCollapseSubtitleView.setHorizontalScrollBarEnabled(false);
        this.mCollapseTitleLayout.setOrientation(1);
        this.mCollapseTitleLayout.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.actionbar.CollapseTitle$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1833xe0422ed4();
            }
        });
        this.mCollapseTitleView.setId(R.id.action_bar_title);
        this.mCollapseTitleLayout.addView(this.mCollapseTitleView, getChildLayoutParams());
        this.mCollapseSubtitleView.setId(R.id.action_bar_subtitle);
        this.mCollapseSubtitleView.setVisibility(8);
        this.mCollapseTitleLayout.addView(this.mCollapseSubtitleView, getChildLayoutParams());
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mCollapseSubtitleView.getLayoutParams();
        layoutParams.topMargin = resources.getDimensionPixelOffset(R.dimen.action_bar_subtitle_top_margin);
        layoutParams.bottomMargin = resources.getDimensionPixelOffset(R.dimen.action_bar_subtitle_bottom_margin);
    }

    /* JADX INFO: renamed from: lambda$init$0$miuix-appcompat-internal-app-widget-actionbar-CollapseTitle, reason: not valid java name */
    /* synthetic */ void m1833xe0422ed4() {
        this.mCollapseTitleLayout.setBackground(AttributeResolver.resolveDrawable(this.mContext, android.R.attr.actionBarItemBackground));
    }

    private LinearLayout.LayoutParams getChildLayoutParams() {
        return new LinearLayout.LayoutParams(-2, -2);
    }

    public void setOnClickListener(View.OnClickListener onClickListener, boolean z) {
        this.mCollapseTitleView.setOnClickListener(onClickListener);
        this.mCollapseTitleView.post(new Runnable() { // from class: miuix.appcompat.internal.app.widget.actionbar.CollapseTitle$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1834x3b1d4840();
            }
        });
        this.mCollapseTitleView.setClickable(z);
    }

    /* JADX INFO: renamed from: lambda$setOnClickListener$1$miuix-appcompat-internal-app-widget-actionbar-CollapseTitle, reason: not valid java name */
    /* synthetic */ void m1834x3b1d4840() {
        this.mCollapseTitleLayout.setTouchDelegate(new TouchDelegate(new Rect(0, 0, this.mCollapseTitleLayout.getWidth(), this.mCollapseTitleLayout.getHeight()), this.mCollapseTitleView));
    }

    public void setSubTitleOnClickListener(View.OnClickListener onClickListener, boolean z) {
        ColorTransitionTextView colorTransitionTextView = this.mCollapseSubtitleView;
        if (colorTransitionTextView != null) {
            colorTransitionTextView.setOnClickListener(onClickListener);
            this.mCollapseSubtitleView.setClickable(z);
            this.mCollapseSubtitleView.setOnTouchListener(this.subtitleTouchListener);
        }
    }

    public void setAllTitlesClickable(boolean z) {
        ColorTransitionTextView colorTransitionTextView = this.mCollapseTitleView;
        if (colorTransitionTextView != null) {
            colorTransitionTextView.setClickable(z);
        }
        ColorTransitionTextView colorTransitionTextView2 = this.mCollapseSubtitleView;
        if (colorTransitionTextView2 != null) {
            colorTransitionTextView2.setClickable(z);
        }
    }

    public void setTitleClickable(boolean z) {
        ColorTransitionTextView colorTransitionTextView = this.mCollapseTitleView;
        if (colorTransitionTextView != null) {
            colorTransitionTextView.setClickable(z);
        }
    }

    public void setSubTitleClickable(boolean z) {
        ColorTransitionTextView colorTransitionTextView = this.mCollapseSubtitleView;
        if (colorTransitionTextView != null) {
            colorTransitionTextView.setClickable(z);
        }
    }

    public void setTitle(CharSequence charSequence) {
        if (TextUtils.equals(charSequence, this.mCollapseTitleView.getText())) {
            return;
        }
        this.mCollapseTitleView.setText(charSequence);
        setEnabled(!TextUtils.isEmpty(charSequence));
        this.mIsTitleDirty = true;
    }

    public void setSubTitle(CharSequence charSequence) {
        this.mCollapseSubtitleView.setText(charSequence);
        int i = TextUtils.isEmpty(charSequence) ? 8 : 0;
        setSubTitleVisibility(i);
        resetTitleMaxLine(i == 0);
    }

    public void setSubTitleDrawable(TextViewDrawableConfig textViewDrawableConfig) {
        textViewDrawableConfig.setTextViewDrawable(this.mCollapseSubtitleView);
    }

    private void resetTitleMaxLine(boolean z) {
        ColorTransitionTextView colorTransitionTextView = this.mCollapseTitleView;
        if (colorTransitionTextView == null || !this.mLargeFontAdaptEnable) {
            return;
        }
        if (z && colorTransitionTextView.getMaxLines() > 1) {
            this.mCollapseTitleView.setSingleLine(true);
            this.mCollapseTitleView.setMaxLines(1);
        } else {
            if (z || this.mCollapseTitleView.getMaxLines() != 1) {
                return;
            }
            this.mCollapseTitleView.setSingleLine(false);
            this.mCollapseTitleView.setMaxLines(this.mLargeFontTitleMaxLine);
        }
    }

    public void setEnabled(boolean z) {
        this.mCollapseTitleLayout.setEnabled(z);
    }

    public void setVisible(boolean z) {
        if (this.mVisible != z) {
            this.mVisible = z;
            this.mCollapseTitleLayout.setVisibility(z ? 0 : 4);
        }
    }

    public void setSubTitleVisibility(int i) {
        this.mCollapseSubtitleView.setVisibility(i);
    }

    public void setSubTitleTextSize(float f) {
        if (this.mSubtitleSizeable) {
            this.mCollapseSubtitleView.setTextSize(0, f);
        }
    }

    public void setTitleVisibility(int i) {
        this.mCollapseTitleView.setVisibility(i);
    }

    public int getTitleVisibility() {
        return this.mCollapseTitleView.getVisibility();
    }

    public ViewGroup getTitleParent() {
        return (ViewGroup) this.mCollapseTitleView.getParent();
    }

    public void setVisibility(int i) {
        if (!this.mVisible && i == 0) {
            this.mCollapseTitleLayout.setVisibility(4);
        } else {
            this.mCollapseTitleLayout.setVisibility(i);
        }
    }

    public int getVisibility() {
        return this.mCollapseTitleLayout.getVisibility();
    }

    public View getLayout() {
        return this.mCollapseTitleLayout;
    }

    public Rect getHitRect() {
        Rect rect = new Rect();
        this.mCollapseTitleLayout.getHitRect(rect);
        return rect;
    }

    public void updateTitleCenter(boolean z) {
        ViewGroup titleParent = getTitleParent();
        if (titleParent instanceof LinearLayout) {
            ((LinearLayout) titleParent).setGravity((z ? 1 : 8388611) | 16);
        }
        this.mCollapseTitleView.setGravity((z ? 1 : 8388611) | 16);
        this.mCollapseTitleView.setEllipsize(TextUtils.TruncateAt.END);
        this.mCollapseSubtitleView.setGravity((z ? 1 : 8388611) | 16);
        this.mCollapseSubtitleView.setEllipsize(TextUtils.TruncateAt.END);
    }

    public boolean canTitleBeShown(String str) {
        TextPaint paint = this.mCollapseTitleView.getPaint();
        float f = this.mCollapseTitlePaintTextSize;
        if (f == -1.0f || f != paint.getTextSize()) {
            this.mCollapseTitlePaintTextSize = paint.getTextSize();
            this.mIsTitleDirty = true;
        }
        if (this.mIsTitleDirty) {
            this.mTitleLength = this.mCollapseTitleView.getPaint().measureText(str);
            this.mIsTitleDirty = false;
        }
        return this.mCollapseTitleView.getMeasuredWidth() == 0 || this.mTitleLength <= ((float) this.mCollapseTitleView.getMeasuredWidth());
    }

    public float getSubtitleAdjustSize() {
        float f = this.mDefaultSubtitleSize;
        Resources resources = this.mContext.getResources();
        int measuredHeight = ((this.mCollapseTitleLayout.getMeasuredHeight() - this.mCollapseTitleView.getMeasuredHeight()) - this.mCollapseSubtitleView.getPaddingTop()) - this.mCollapseSubtitleView.getPaddingBottom();
        if (measuredHeight <= 0) {
            return f;
        }
        TextPaint textPaint = new TextPaint(this.mCollapseSubtitleView.getPaint());
        textPaint.setTextSize(f);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        int iCeil = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
        float f2 = f / 2.0f;
        float f3 = resources.getDisplayMetrics().scaledDensity;
        while (iCeil > measuredHeight && f >= f2) {
            f -= f3;
            textPaint.setTextSize(f);
            Paint.FontMetrics fontMetrics2 = textPaint.getFontMetrics();
            iCeil = (int) Math.ceil(fontMetrics2.descent - fontMetrics2.ascent);
        }
        return f;
    }

    public void setTextColorTransitEnable(boolean z, int i) {
        if (this.mTextColorTransitEnable != z) {
            if (!z) {
                this.mCollapseTitleView.startColorTransition(false, false);
            }
            this.mTextColorTransitEnable = z;
            if (z && i == 0) {
                this.mCollapseTitleView.startColorTransition(true, false);
            }
        }
    }

    public void startColorTransition(boolean z, boolean z2) {
        if (this.mTextColorTransitEnable) {
            this.mCollapseTitleView.startColorTransition(z, z2);
        }
    }

    public void setTitleStyle(int i) {
        this.mTitleStyle = i;
        TextViewCompat.setTextAppearance(this.mCollapseTitleView, i);
        this.mCollapseTitleView.invalidate();
    }

    public void setSubTitleStyle(int i) {
        this.mSubtitleStyle = i;
        TextViewCompat.setTextAppearance(this.mCollapseSubtitleView, i);
        this.mCollapseTitleView.invalidate();
    }
}
