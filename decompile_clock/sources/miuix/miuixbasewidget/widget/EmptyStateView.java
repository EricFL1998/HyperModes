package miuix.miuixbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import miuix.miuixbasewidget.R;

/* JADX INFO: loaded from: classes2.dex */
public class EmptyStateView extends LinearLayout {
    private Button mButton;
    private ImageView mIconView;
    private TextView mSummaryView;
    private int mTitlePadding;
    private TextView mTitleView;

    public EmptyStateView(Context context) {
        this(context, null);
    }

    public EmptyStateView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public EmptyStateView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.Widget_EmptyStateView);
    }

    public EmptyStateView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context, attributeSet, i, i2);
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2) {
        inflate(context, R.layout.miuix_appcompat_empty_state_layout, this);
        this.mIconView = (ImageView) findViewById(R.id.empty_state_view_icon);
        this.mTitleView = (TextView) findViewById(R.id.empty_state_view_title);
        this.mSummaryView = (TextView) findViewById(R.id.empty_state_view_summary);
        this.mButton = (Button) findViewById(R.id.empty_state_view_button);
        this.mTitlePadding = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_empty_state_view_title_padding);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.EmptyStateView, i, i2);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.EmptyStateView_android_icon, 0);
        String string = typedArrayObtainStyledAttributes.getString(R.styleable.EmptyStateView_android_title);
        String string2 = typedArrayObtainStyledAttributes.getString(R.styleable.EmptyStateView_android_summary);
        String string3 = typedArrayObtainStyledAttributes.getString(R.styleable.EmptyStateView_android_text);
        typedArrayObtainStyledAttributes.recycle();
        setIconInternal(resourceId, false);
        setTitleInternal(string, false);
        setSummaryInternal(string2, false);
        setButtonTextInternal(string3, true);
    }

    public ImageView getIconView() {
        return this.mIconView;
    }

    public TextView getTitleView() {
        return this.mTitleView;
    }

    public TextView getSummaryView() {
        return this.mSummaryView;
    }

    public Button getButton() {
        return this.mButton;
    }

    public void setIcon(int i) {
        setIconInternal(i, true);
    }

    public void setTitle(String str) {
        setTitleInternal(str, true);
    }

    public void setSummary(String str) {
        setSummaryInternal(str, true);
    }

    public void setButtonText(String str) {
        setButtonTextInternal(str, true);
    }

    private void setIconInternal(int i, boolean z) {
        this.mIconView.setVisibility(i != 0 ? 0 : 8);
        this.mIconView.setImageResource(i);
        if (z) {
            updateTitleViewPaddingIfNeed();
        }
    }

    private void setTitleInternal(String str, boolean z) {
        this.mTitleView.setVisibility(TextUtils.isEmpty(str) ^ true ? 0 : 8);
        this.mTitleView.setText(str);
        if (z) {
            updateTitleViewPaddingIfNeed();
        }
    }

    private void setSummaryInternal(String str, boolean z) {
        this.mSummaryView.setVisibility(TextUtils.isEmpty(str) ^ true ? 0 : 8);
        this.mSummaryView.setText(str);
        if (z) {
            updateTitleViewPaddingIfNeed();
        }
    }

    private void setButtonTextInternal(String str, boolean z) {
        this.mButton.setVisibility(TextUtils.isEmpty(str) ^ true ? 0 : 8);
        this.mButton.setText(str);
        if (z) {
            updateTitleViewPaddingIfNeed();
        }
    }

    private void updateTitleViewPaddingIfNeed() {
        int i = (this.mIconView.getVisibility() == 8 && this.mTitleView.getVisibility() == 0 && this.mSummaryView.getVisibility() == 8 && this.mButton.getVisibility() == 8) ? this.mTitlePadding : 0;
        this.mTitleView.setPadding(i, i, i, i);
    }
}
