package miuix.appcompat.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import miuix.appcompat.R;
import miuix.appcompat.internal.widget.DialogButtonPanel;
import miuix.appcompat.internal.widget.DialogParentPanel2;
import miuix.appcompat.internal.widget.PairingParentPanel;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowUtils;
import miuix.core.widget.NestedScrollView;
import miuix.internal.util.AnimHelper;
import miuix.os.DeviceHelper;
import miuix.springback.view.SpringBackLayout;

/* JADX INFO: loaded from: classes2.dex */
public class PairingDialog extends AlertDialog {
    private LinearLayout mCheckboxContainer;
    private CharSequence mCheckboxMessage;
    private DialogInterface.OnClickListener mCloseClickListener;
    private Context mContext;
    private float mCornerRadiusThreshold;
    private int mCustomLayoutResId;
    private View mCustomView;
    private boolean mCustomViewVerticalCenterEnabled;
    private AppCompatCheckBox mDefaultCheckbox;
    private ViewGroup mDialogButtonPanel;
    private int mDialogButtonPanelHPadding;
    private ViewGroup mDialogContentPanel;
    private DialogParentPanel2 mDialogParentPanel;
    private int mDialogParentPanelFixedHeight;
    private int mDialogParentPanelFlipTinyFixedHeight;
    private int mDialogParentPanelLargeFontFixedHeight;
    private int mDialogParentPanelPaddingBottom;
    private boolean mDiscardNaviBarHeightEnabled;
    private TextView mFeedBackMessageView;
    private LinearLayout mFeedbackContainer;
    private View mInflatedCustomView;
    private boolean mIsChecked;
    private boolean mIsFullScreenGestureMode;
    private Drawable mLabelDrawable;
    private int mLabelDrawableResId;
    private AppCompatImageView mLabelImage;
    private int mLabelImageHeight;
    private int mLabelImageWidth;
    private CharSequence mMessage;
    private AppCompatTextView mMessageView;
    private boolean mNavigationBarHiddenEnabled;
    private float mNormalCornerRadius;
    private LinearLayout mPairingContentView;
    private ViewGroup mPairingCustom;
    private int mPairingPanelPaddingBottom;
    private PairingParentPanel mPairingParentPanel;
    private NestedScrollView mPairingScrollView;
    private SpringBackLayout mPairingSpringBack;
    private int mPanelBottomMargin;
    private CharSequence mTitle;
    private TextView mTitleView;
    private WindowManager mWindowManager;

    public PairingDialog(Context context) {
        super(context);
        this.mWindowManager = null;
        this.mLabelImageWidth = -2;
        this.mLabelImageHeight = -2;
        this.mCustomViewVerticalCenterEnabled = true;
        this.mNavigationBarHiddenEnabled = false;
        this.mDiscardNaviBarHeightEnabled = true;
        init(context);
    }

    public PairingDialog(Context context, int i) {
        super(context, i);
        this.mWindowManager = null;
        this.mLabelImageWidth = -2;
        this.mLabelImageHeight = -2;
        this.mCustomViewVerticalCenterEnabled = true;
        this.mNavigationBarHiddenEnabled = false;
        this.mDiscardNaviBarHeightEnabled = true;
        init(context);
    }

    public PairingDialog(Context context, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        this.mWindowManager = null;
        this.mLabelImageWidth = -2;
        this.mLabelImageHeight = -2;
        this.mCustomViewVerticalCenterEnabled = true;
        this.mNavigationBarHiddenEnabled = false;
        this.mDiscardNaviBarHeightEnabled = true;
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mDialogParentPanelPaddingBottom = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_pairing_dialog_parent_panel_padding_bottom);
        this.mNormalCornerRadius = context.getResources().getDimensionPixelSize(R.dimen.miuix_theme_radius_big);
        this.mCornerRadiusThreshold = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_pairing_dialog_corner_radius_threshold);
        this.mDialogButtonPanelHPadding = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_pairing_dialog_content_padding_horizontal);
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mDialogParentPanelFixedHeight = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_pairing_dialog_parent_panel_fixed_height);
        this.mDialogParentPanelLargeFontFixedHeight = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_pairing_dialog_parent_panel_fixed_height_large_font);
        this.mDialogParentPanelFlipTinyFixedHeight = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_pairing_dialog_parent_panel_fixed_height_flip_tiny);
        this.mPairingPanelPaddingBottom = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_pairing_dialog_panel_padding_bottom);
        this.mIsFullScreenGestureMode = MiuixUIUtils.isFullScreenGestureMode(this.mContext);
        prepareDefaultCheckbox(this.mContext);
        prepareFeedbackMessageView(this.mContext);
        setMaterialEnabled(true);
    }

    private void prepareFeedbackMessageView(Context context) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.miuix_appcompat_pairing_dialog_feedback_message, (ViewGroup) null);
        this.mFeedbackContainer = linearLayout;
        this.mFeedBackMessageView = (TextView) linearLayout.findViewById(R.id.pairingDialogFeedback);
    }

    private void prepareDefaultCheckbox(Context context) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.miuix_appcompat_pairing_dialog_checkbox, (ViewGroup) null);
        this.mCheckboxContainer = linearLayout;
        this.mDefaultCheckbox = (AppCompatCheckBox) linearLayout.findViewById(R.id.pairing_checkbox_stub);
    }

    public void setCustomViewVerticalCenterEnabled(boolean z) {
        this.mCustomViewVerticalCenterEnabled = z;
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
    }

    @Override // androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void setTitle(int i) {
        this.mTitle = this.mContext.getResources().getString(i);
    }

    public void setNavigationBarHiddenEnabled(boolean z) {
        this.mNavigationBarHiddenEnabled = z;
    }

    public void setDiscardNaviBarHeightEnabled(boolean z) {
        this.mDiscardNaviBarHeightEnabled = z;
    }

    public TextView getTitleView() {
        return this.mTitleView;
    }

    @Override // miuix.appcompat.app.AlertDialog
    public void setMessage(CharSequence charSequence) {
        this.mMessage = charSequence;
    }

    public void setMessage(int i) {
        this.mMessage = this.mContext.getResources().getString(i);
    }

    @Override // miuix.appcompat.app.AlertDialog
    public AppCompatTextView getMessageView() {
        return this.mMessageView;
    }

    public void setCheckbox(boolean z, CharSequence charSequence) {
        this.mIsChecked = z;
        this.mCheckboxMessage = charSequence;
    }

    @Override // miuix.appcompat.app.AlertDialog
    public boolean isChecked() {
        LinearLayout linearLayout = this.mCheckboxContainer;
        if (linearLayout == null || linearLayout.getVisibility() == 8) {
            return false;
        }
        return this.mDefaultCheckbox.isChecked();
    }

    public void setLabelImage(Drawable drawable) {
        this.mLabelDrawable = drawable;
        this.mLabelDrawableResId = 0;
    }

    public void setLabelImage(Drawable drawable, int i, int i2) {
        setLabelImage(drawable);
        this.mLabelImageWidth = i;
        this.mLabelImageHeight = i2;
    }

    public void setLabelImage(int i) {
        this.mLabelDrawable = null;
        this.mLabelDrawableResId = i;
    }

    public void setLabelImage(int i, int i2, int i3) {
        setLabelImage(i);
        this.mLabelImageWidth = i2;
        this.mLabelImageHeight = i3;
    }

    public AppCompatImageView getLabelImageView() {
        return this.mLabelImage;
    }

    public void setButton(int i, CharSequence charSequence, boolean z, DialogInterface.OnClickListener onClickListener) {
        Message messageObtain = Message.obtain();
        messageObtain.what = i;
        messageObtain.obj = onClickListener;
        Bundle bundle = new Bundle();
        bundle.putBoolean(AlertDialog.KEY_BUTTON_CLICK_AUTO_DISMISSIBLE, z);
        messageObtain.setData(bundle);
        this.mAlert.setButton(i, charSequence, null, messageObtain);
    }

    public void setButton(int i, int i2, boolean z, DialogInterface.OnClickListener onClickListener) {
        setButton(i, this.mContext.getResources().getString(i2), z, onClickListener);
    }

    public void setCloseClickListener(DialogInterface.OnClickListener onClickListener) {
        this.mCloseClickListener = onClickListener;
    }

    public void setCustomView(View view) {
        this.mCustomView = view;
        this.mCustomLayoutResId = 0;
    }

    public void setCustomView(int i) {
        this.mCustomView = null;
        this.mCustomLayoutResId = i;
    }

    private void beforeInstallDialogContent(View view) {
        if (view instanceof PairingParentPanel) {
            PairingParentPanel pairingParentPanel = (PairingParentPanel) view;
            pairingParentPanel.setCustomViewVerticalCenterEnabled(this.mCustomViewVerticalCenterEnabled);
            this.mPairingParentPanel = pairingParentPanel;
        }
        this.mPairingSpringBack = (SpringBackLayout) view.findViewById(R.id.pairingSpringBack);
        this.mPairingScrollView = (NestedScrollView) view.findViewById(R.id.pairingScrollView);
        this.mPairingContentView = (LinearLayout) view.findViewById(R.id.pairingContentView);
        this.mTitleView = (TextView) view.findViewById(R.id.pairingTitle);
        this.mPairingCustom = (ViewGroup) view.findViewById(R.id.pairingCustom);
        this.mMessageView = (AppCompatTextView) view.findViewById(R.id.pairingMessage);
        this.mLabelImage = (AppCompatImageView) view.findViewById(R.id.pairingLabelImage);
    }

    private void afterInstallDialogContent() {
        this.mDialogContentPanel = (ViewGroup) findViewById(R.id.contentPanel);
        this.mDialogParentPanel = (DialogParentPanel2) findViewById(R.id.parentPanel);
        this.mDialogButtonPanel = (ViewGroup) findViewById(R.id.buttonPanel);
        installDefaultCheckbox();
        installFeedbackMessageView();
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    protected void onCreate(Bundle bundle) {
        this.mAlert.setNavigationHiddenEnabled(this.mNavigationBarHiddenEnabled && this.mIsFullScreenGestureMode);
        this.mAlert.setDiscardNaviBarHeightEnabled(this.mDiscardNaviBarHeightEnabled && this.mIsFullScreenGestureMode);
        updateParentPanelFixedHeight(this.mContext.getResources().getConfiguration());
        View viewInflate = LayoutInflater.from(getContext()).inflate(R.layout.miuix_appcompat_pairing_dialog_content, (ViewGroup) null);
        beforeInstallDialogContent(viewInflate);
        setupTitle();
        setupMessageView();
        setupCustomContent();
        setupLabelImage();
        setView(viewInflate);
        super.onCreate(bundle);
        afterInstallDialogContent();
        setParentPanelConfigChangedCallback(this.mDialogParentPanel);
        fixedButtonPanelToBottom(this.mDialogParentPanel, this.mDialogContentPanel, this.mDialogButtonPanel);
        DialogParentPanel2 dialogParentPanel2 = this.mDialogParentPanel;
        if (dialogParentPanel2 != null) {
            dialogParentPanel2.setCornerRadius(getPanelCornerRadius(this.mContext));
            DialogParentPanel2 dialogParentPanel3 = this.mDialogParentPanel;
            dialogParentPanel3.setPadding(dialogParentPanel3.getPaddingStart(), 0, this.mDialogParentPanel.getPaddingEnd(), this.mDialogParentPanelPaddingBottom);
        }
        ViewGroup viewGroup = this.mDialogButtonPanel;
        if (viewGroup != null) {
            if (viewGroup instanceof DialogButtonPanel) {
                ((DialogButtonPanel) viewGroup).setCustomPaddingEnabled(true);
                ((DialogButtonPanel) this.mDialogButtonPanel).setCustomPaddingHorizontal(this.mDialogButtonPanelHPadding);
            }
            ViewGroup viewGroup2 = this.mDialogButtonPanel;
            viewGroup2.setPadding(this.mDialogButtonPanelHPadding, viewGroup2.getPaddingTop(), this.mDialogButtonPanelHPadding, this.mDialogButtonPanel.getPaddingBottom());
            ViewGroup.LayoutParams layoutParams = this.mDialogButtonPanel.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = 0;
            }
        }
        ImageView imageView = (ImageView) findViewById(R.id.pairingClosable);
        if (imageView != null) {
            imageView.setOnClickListener(new View.OnClickListener() { // from class: miuix.appcompat.app.PairingDialog.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (PairingDialog.this.mCloseClickListener != null) {
                        PairingDialog.this.mCloseClickListener.onClick(PairingDialog.this.mAlert.mDialog, -2);
                    }
                    PairingDialog.this.dismiss();
                }
            });
            AnimHelper.addPressAnim(imageView);
        }
        adjustSpringBackEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustSpringBackEnabled() {
        PairingParentPanel pairingParentPanel = this.mPairingParentPanel;
        if (pairingParentPanel == null || this.mPairingSpringBack == null || this.mPairingScrollView == null || this.mPairingContentView == null) {
            return;
        }
        pairingParentPanel.post(new Runnable() { // from class: miuix.appcompat.app.PairingDialog$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1815x435fddbe();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$adjustSpringBackEnabled$0$miuix-appcompat-app-PairingDialog, reason: not valid java name */
    /* synthetic */ void m1815x435fddbe() {
        setScrollViewToExpectedHeight();
        ViewGroup.LayoutParams layoutParams = this.mPairingContentView.getLayoutParams();
        this.mPairingSpringBack.setSpringBackEnable((layoutParams instanceof ViewGroup.MarginLayoutParams ? ((ViewGroup.MarginLayoutParams) layoutParams).topMargin : 0) + this.mPairingContentView.getHeight() > this.mPairingScrollView.getHeight());
    }

    private void setScrollViewToExpectedHeight() {
        PairingParentPanel pairingParentPanel;
        ViewGroup.LayoutParams layoutParams;
        if (this.mPairingScrollView == null || (pairingParentPanel = this.mPairingParentPanel) == null || pairingParentPanel.getScrollExpectedHeight() == 0 || (layoutParams = this.mPairingScrollView.getLayoutParams()) == null) {
            return;
        }
        layoutParams.height = this.mPairingParentPanel.getScrollExpectedHeight();
        this.mPairingScrollView.setLayoutParams(layoutParams);
    }

    private float getPanelCornerRadius(Context context) {
        if (context == null) {
            return this.mNormalCornerRadius;
        }
        Display display = Build.VERSION.SDK_INT >= 30 ? context.getDisplay() : null;
        if (display == null) {
            display = this.mWindowManager.getDefaultDisplay();
        }
        float fMax = this.mNormalCornerRadius;
        if (Build.VERSION.SDK_INT >= 31) {
            RoundedCorner roundedCorner = display.getRoundedCorner(3);
            if (roundedCorner != null) {
                fMax = Math.max(0.0f, roundedCorner.getRadius() - this.mPanelBottomMargin);
            }
            return fMax < this.mCornerRadiusThreshold ? this.mNormalCornerRadius : fMax;
        }
        return this.mNormalCornerRadius;
    }

    private void setupTitle() {
        if (this.mTitleView == null) {
            return;
        }
        if (!TextUtils.isEmpty(this.mTitle)) {
            this.mTitleView.setVisibility(0);
            this.mTitleView.setText(this.mTitle);
        } else {
            this.mTitleView.setVisibility(8);
        }
    }

    private void setupCustomContent() {
        if (this.mPairingCustom == null) {
            this.mPairingCustom = (ViewGroup) findViewById(R.id.pairingCustom);
        }
        View view = this.mInflatedCustomView;
        View viewInflate = null;
        if (view != null && view.getParent() != null) {
            this.mAlert.safeRemoveFromParent(this.mInflatedCustomView);
            this.mInflatedCustomView = null;
        }
        View view2 = this.mCustomView;
        if (view2 != null) {
            viewInflate = view2;
        } else if (this.mCustomLayoutResId != 0) {
            viewInflate = LayoutInflater.from(this.mContext).inflate(this.mCustomLayoutResId, this.mPairingCustom, false);
            this.mInflatedCustomView = viewInflate;
        }
        if (viewInflate != null) {
            this.mAlert.safeMoveView(viewInflate, this.mPairingCustom);
        } else {
            this.mAlert.safeRemoveFromParent(this.mPairingCustom);
        }
    }

    private void setupMessageView() {
        if (this.mMessageView == null) {
            return;
        }
        if (!TextUtils.isEmpty(this.mMessage)) {
            this.mMessageView.setText(this.mMessage);
            this.mMessageView.setVisibility(0);
        } else {
            this.mMessageView.setVisibility(8);
        }
    }

    private void setupLabelImage() {
        AppCompatImageView appCompatImageView = this.mLabelImage;
        if (appCompatImageView == null) {
            return;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) appCompatImageView.getLayoutParams();
        if (marginLayoutParams != null) {
            marginLayoutParams.width = this.mLabelImageWidth;
            marginLayoutParams.height = this.mLabelImageHeight;
            this.mLabelImage.setLayoutParams(marginLayoutParams);
        }
        Drawable drawable = this.mLabelDrawable;
        if (drawable != null) {
            this.mLabelImage.setImageDrawable(drawable);
            this.mLabelImage.setVisibility(0);
            return;
        }
        int i = this.mLabelDrawableResId;
        if (i != 0) {
            this.mLabelImage.setImageResource(i);
            this.mLabelImage.setVisibility(0);
        } else {
            this.mLabelImage.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:25:0x0065  */
    /* JADX WARN: Code duplicated, block: B:26:0x0068  */
    /* JADX WARN: Code duplicated, block: B:28:0x0070  */
    /* JADX WARN: Code duplicated, block: B:29:0x0073  */
    /* JADX WARN: Code duplicated, block: B:32:0x0079  */
    public int updateParentPanelFixedHeight(Configuration configuration) {
        int dimensionPixelSize;
        int i;
        int i2;
        int i3;
        Point screenSize = WindowUtils.getScreenSize(this.mContext);
        int statusBarHeight = MiuixUIUtils.getStatusBarHeight(this.mContext);
        int navigationBarHeight = MiuixUIUtils.getNavigationBarHeight(this.mContext);
        boolean zIsFullScreenGestureMode = MiuixUIUtils.isFullScreenGestureMode(this.mContext);
        boolean z = configuration.orientation == 2;
        boolean z2 = miuix.os.Build.IS_FLIP && DeviceHelper.isTinyScreen(this.mContext);
        if (z2) {
            dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_width_small_margin);
        } else {
            dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_ime_margin);
        }
        this.mPanelBottomMargin = dimensionPixelSize;
        if (zIsFullScreenGestureMode) {
            i = screenSize.y - statusBarHeight;
            if (this.mNavigationBarHiddenEnabled) {
                navigationBarHeight = 0;
            }
        } else {
            if (z) {
                i2 = screenSize.y - statusBarHeight;
            } else {
                i = screenSize.y - statusBarHeight;
            }
            if (z2) {
                i3 = this.mDialogParentPanelFlipTinyFixedHeight;
            } else if (MiuixUIUtils.getFontLevel(this.mContext) == 2) {
                i3 = this.mDialogParentPanelLargeFontFixedHeight;
            } else {
                i3 = this.mDialogParentPanelFixedHeight;
            }
            if (i2 < i3 + dimensionPixelSize) {
                i3 = i2 - dimensionPixelSize;
            }
            this.mAlert.setPanelFixedSizeEnabled(true);
            this.mAlert.setPanelFixedHeight(i3);
            return i3;
        }
        i2 = i - navigationBarHeight;
        if (z2) {
            i3 = this.mDialogParentPanelFlipTinyFixedHeight;
        } else if (MiuixUIUtils.getFontLevel(this.mContext) == 2) {
            i3 = this.mDialogParentPanelLargeFontFixedHeight;
        } else {
            i3 = this.mDialogParentPanelFixedHeight;
        }
        if (i2 < i3 + dimensionPixelSize) {
            i3 = i2 - dimensionPixelSize;
        }
        this.mAlert.setPanelFixedSizeEnabled(true);
        this.mAlert.setPanelFixedHeight(i3);
        return i3;
    }

    private void setParentPanelConfigChangedCallback(DialogParentPanel2 dialogParentPanel2) {
        if (dialogParentPanel2 == null) {
            return;
        }
        dialogParentPanel2.setConfigurationChangedCallback(new DialogParentPanel2.ConfigurationChangedCallback() { // from class: miuix.appcompat.app.PairingDialog.2
            @Override // miuix.appcompat.internal.widget.DialogParentPanel2.ConfigurationChangedCallback
            public void onConfigurationChanged(Configuration configuration) {
                int iUpdateParentPanelFixedHeight = PairingDialog.this.updateParentPanelFixedHeight(configuration);
                if (PairingDialog.this.mDialogParentPanel != null) {
                    PairingDialog.this.mDialogParentPanel.setPanelFixedHeight(iUpdateParentPanelFixedHeight);
                    PairingDialog.this.mDialogParentPanel.requestLayout();
                }
                PairingDialog.this.adjustSpringBackEnabled();
            }
        });
    }

    private void fixedButtonPanelToBottom(ViewGroup viewGroup, ViewGroup viewGroup2, ViewGroup viewGroup3) {
        if (viewGroup == null || viewGroup2 == null || viewGroup3 == null) {
            return;
        }
        ViewGroup viewGroup4 = (ViewGroup) viewGroup.findViewById(R.id.buttonPanel);
        ViewGroup viewGroup5 = (ViewGroup) viewGroup2.findViewById(R.id.buttonPanel);
        if (viewGroup4 != null || viewGroup5 == null) {
            return;
        }
        this.mAlert.safeMoveView(viewGroup3, viewGroup);
    }

    private void installFeedbackMessageView() {
        LinearLayout linearLayout;
        if (this.mDialogParentPanel == null || (linearLayout = this.mFeedbackContainer) == null) {
            return;
        }
        ViewGroup viewGroup = linearLayout.getParent() instanceof ViewGroup ? (ViewGroup) this.mFeedbackContainer.getParent() : null;
        if (viewGroup != null) {
            viewGroup.removeView(this.mFeedbackContainer);
        } else {
            this.mFeedbackContainer.setVisibility(8);
            this.mDialogParentPanel.addView(this.mFeedbackContainer, this.mDialogParentPanel.indexOfChild(this.mDialogButtonPanel) + 1);
        }
    }

    private void installDefaultCheckbox() {
        LinearLayout linearLayout;
        if (this.mDialogParentPanel == null || (linearLayout = this.mCheckboxContainer) == null) {
            return;
        }
        ViewGroup viewGroup = linearLayout.getParent() instanceof ViewGroup ? (ViewGroup) this.mCheckboxContainer.getParent() : null;
        if (viewGroup != null) {
            viewGroup.removeView(this.mCheckboxContainer);
        } else {
            this.mDialogParentPanel.addView(this.mCheckboxContainer, this.mDialogParentPanel.indexOfChild(this.mDialogButtonPanel));
        }
        if (!TextUtils.isEmpty(this.mCheckboxMessage)) {
            this.mDefaultCheckbox.setText(this.mCheckboxMessage);
            this.mDefaultCheckbox.setChecked(this.mIsChecked);
            this.mCheckboxContainer.setVisibility(0);
            return;
        }
        this.mCheckboxContainer.setVisibility(8);
    }

    private void adjustPairingParentPanelPaddingBottom() {
        ViewGroup viewGroup = this.mDialogButtonPanel;
        if (viewGroup == null || this.mFeedbackContainer == null || this.mPairingParentPanel == null) {
            return;
        }
        if (viewGroup.getVisibility() == 8 && this.mFeedbackContainer.getVisibility() == 8) {
            PairingParentPanel pairingParentPanel = this.mPairingParentPanel;
            pairingParentPanel.setPadding(pairingParentPanel.getPaddingStart(), this.mPairingParentPanel.getPaddingTop(), this.mPairingParentPanel.getPaddingEnd(), 0);
        } else {
            PairingParentPanel pairingParentPanel2 = this.mPairingParentPanel;
            pairingParentPanel2.setPadding(pairingParentPanel2.getPaddingStart(), this.mPairingParentPanel.getPaddingTop(), this.mPairingParentPanel.getPaddingEnd(), this.mPairingPanelPaddingBottom);
        }
    }

    public void showOrHideDialogButtonPanel(boolean z) {
        ViewGroup viewGroup = this.mDialogButtonPanel;
        if (viewGroup == null) {
            return;
        }
        if (z && viewGroup.getVisibility() == 8) {
            this.mDialogButtonPanel.setVisibility(0);
        } else if (!z && this.mDialogButtonPanel.getVisibility() == 0) {
            this.mDialogButtonPanel.setVisibility(8);
        }
        adjustPairingParentPanelPaddingBottom();
    }

    public void showOrHideFeedbackMessage(boolean z) {
        if (this.mDialogParentPanel == null) {
            return;
        }
        if (z && this.mFeedbackContainer.getVisibility() == 8) {
            this.mFeedbackContainer.setVisibility(0);
        } else if (!z && this.mFeedbackContainer.getVisibility() == 0) {
            this.mFeedbackContainer.setVisibility(8);
        }
        adjustPairingParentPanelPaddingBottom();
    }

    public void setFeedbackMessage(CharSequence charSequence) {
        TextView textView = this.mFeedBackMessageView;
        if (textView == null) {
            return;
        }
        textView.setText(charSequence);
    }

    public boolean isButtonPanelVisible() {
        ViewGroup viewGroup = this.mDialogButtonPanel;
        return viewGroup != null && viewGroup.getVisibility() == 0;
    }

    public boolean isFeedbackMessageVisible() {
        LinearLayout linearLayout = this.mFeedbackContainer;
        return linearLayout != null && linearLayout.getVisibility() == 0;
    }

    public void showFeedbackMessage() {
        showOrHideDialogButtonPanel(false);
        showOrHideFeedbackMessage(true);
    }

    public void hideFeedbackMessage() {
        showOrHideDialogButtonPanel(true);
        showOrHideFeedbackMessage(false);
    }

    public TextView getFeedbackMessageView() {
        return this.mFeedBackMessageView;
    }

    public LinearLayout getFeedbackContainer() {
        return this.mFeedbackContainer;
    }

    public ViewGroup getButtonPanel() {
        return this.mDialogButtonPanel;
    }
}
