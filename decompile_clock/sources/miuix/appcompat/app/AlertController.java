package miuix.appcompat.app;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.miui.miwallpaper.MiuiWallpaperManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.androidbasewidget.widget.CheckedTextView;
import miuix.animation.Folme;
import miuix.appcompat.R;
import miuix.appcompat.app.strategy.DialogButtonBehaviorImpl;
import miuix.appcompat.app.strategy.DialogPanelBehaviorImpl;
import miuix.appcompat.internal.widget.DialogButtonPanel;
import miuix.appcompat.internal.widget.DialogParentPanel2;
import miuix.appcompat.internal.widget.DialogRootView;
import miuix.appcompat.internal.widget.NestedScrollViewExpander;
import miuix.appcompat.widget.DialogAnimHelper;
import miuix.core.util.EnvStateManager;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MaterialDayNightConfig;
import miuix.core.util.MiuiBlurUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.RomUtils;
import miuix.core.util.ScreenModeHelper;
import miuix.core.util.SystemProperties;
import miuix.core.util.WindowBaseInfo;
import miuix.core.util.WindowUtils;
import miuix.device.DeviceUtils;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.AsyncInflateLayoutManager;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.LiteUtils;
import miuix.internal.util.ReflectUtil;
import miuix.internal.util.ViewUtils;
import miuix.internal.widget.GroupButton;
import miuix.os.Build;
import miuix.os.DeviceHelper;
import miuix.reflect.ReflectionHelper;
import miuix.theme.token.BloomStrokeToken;
import miuix.theme.token.ColorBlendToken;
import miuix.theme.token.DimToken;
import miuix.theme.token.MaterialDayNightToken;
import miuix.theme.token.MaterialToken;
import miuix.view.CompatViewMethod;
import miuix.view.DensityChangedHelper;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;
import miuix.view.animation.CubicEaseInOutInterpolator;

/* JADX INFO: loaded from: classes2.dex */
class AlertController {
    public static final MaterialToken Default_Dialog_Dark;
    public static final MaterialToken Default_Dialog_Light;
    public static final MaterialDayNightToken Default_Dialog_Material;
    private static final int FLAG_NO_EAR_AREA = 768;
    private static final String TAG = "AlertController";
    private boolean buildJustNow;
    private Configuration configurationAfterInstalled;
    ListAdapter mAdapter;
    private final int mAlertDialogLayout;
    private boolean mAsyncInflatePanelEnabled;
    private boolean mButtonForceVertical;
    private final View.OnClickListener mButtonHandler;
    Button mButtonNegative;
    Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    Button mButtonNeutral;
    Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    Button mButtonPositive;
    Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    private boolean mCancelable;
    private boolean mCanceledOnTouchOutside;
    private CharSequence mCheckBoxMessage;
    int mCheckedItem;
    private CharSequence mComment;
    private float mCommentTextSize;
    private TextView mCommentView;
    private AlertDialog.OnConfigurationChangedListener mConfigurationChangedListener;
    private boolean mContentForceFullScroll;
    private final Context mContext;
    private final Thread mCreateThread;
    private int mCurrentDensityDpi;
    private float mCustomTitleTextSize;
    private TextView mCustomTitleTextView;
    private View mCustomTitleView;
    private TextWatcher mDefaultButtonsTextWatcher;
    final AppCompatDialog mDialog;
    private final DialogAnimHelper mDialogAnimHelper;
    private int mDialogContentLayout;
    private DialogRootView mDialogRootView;
    private View mDimBg;
    private boolean mDiscardImeAnimEnabled;
    private boolean mDiscardNaviBarHeightEnabled;
    private Rect mDisplayCutoutSafeInsets;
    private final DialogDisplayStrategy mDisplayStrategy;
    boolean mEnableEnterAnim;
    private List<ButtonInfo> mExtraButtonList;
    private DisplayCutout mFlipCutout;
    Handler mHandler;
    boolean mHapticFeedbackEnabled;
    private boolean mHasPendingDismiss;
    private Drawable mIcon;
    private int mIconHeight;
    private int mIconId;
    private int mIconWidth;
    private View mInflatedView;
    private boolean mInsetsAnimationPlayed;
    private boolean mIsAssociatedActivityHideNavigationBar;
    private boolean mIsCarWithScreen;
    private boolean mIsChecked;
    private boolean mIsDialogAnimating;
    private boolean mIsEnableImmersive;
    private boolean mIsFlipTinyScreen;
    private boolean mIsFromRebuild;
    private boolean mIsSynergy;
    private boolean mIsWindowLandScape;
    private boolean mLandscapePanel;
    private final LayoutChangeListener mLayoutChangeListener;
    private AlertDialog.OnDialogLayoutReloadListener mLayoutReloadListener;
    int mListItemLayout;
    int mListLayout;
    ListView mListView;
    private int mLiteVersion;
    private boolean mMarkLandscape;
    private boolean mMaterialEnabled;
    private CharSequence mMessage;
    private float mMessageTextSize;
    private TextView mMessageView;
    private int mMinCustomVisibleHeight;
    int mMultiChoiceItemLayout;
    private boolean mNavigationBarHiddenEnabled;
    long mNonImmersiveDialogShowAnimDuration;
    private int mPanelAndImeMargin;
    private int mPanelFixedHeight;
    private boolean mPanelFixedSizeEnabled;
    private int mPanelFixedWidth;
    private int mPanelParamsHorizontalMargin;
    private int mPanelParamsWidth;
    private AlertDialog.OnPanelSizeChangedListener mPanelSizeChangedListener;
    private DialogParentPanel2 mParentPanel;
    private boolean mPreferLandscape;
    private boolean mPrimaryButtonFirstEnabled;
    private Point mRootViewSize;
    private Point mRootViewSizeDp;
    private int mScreenMinorSize;
    private int mScreenOrientation;
    private Point mScreenRealSize;
    private boolean mSetupWindowInsetsAnimation;
    private AlertDialog.OnDialogShowAnimListener mShowAnimListener;
    private AlertDialog.OnDialogShowAnimListener mShowAnimListenerWrapper;
    private final boolean mShowTitle;
    int mSingleChoiceItemLayout;
    private boolean mSmallIcon;
    private CharSequence mTitle;
    private float mTitleTextSize;
    private TextView mTitleView;
    private boolean mUseForceFlipCutout;
    private View mView;
    private int mViewLayoutResId;
    private final Window mWindow;
    private WindowManager mWindowManager;
    protected boolean mIsDebugEnabled = false;
    private int mExtraImeMargin = -1;
    private boolean mIsInFreeForm = false;
    private int mNonImmersiveDialogHeight = -2;
    long mShowUpTimeMillis = 0;
    private final DialogContract.DimensConfig mDimensConfig = new DialogContract.DimensConfig();

    private int getCutoutMode(int i, int i2) {
        if (i2 == 0) {
            return i == 2 ? 2 : 1;
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDialogPanelExtraBottomPadding() {
        return 0;
    }

    static {
        MaterialToken materialTokenBuild = new MaterialToken.Builder(30, "dialog-default", "light").setColorBlend(ColorBlendToken.Pured_Thin_Light).setMaskBlur(100).setBloomStroke(BloomStrokeToken.Glass_Stroke_Big_Light).build();
        Default_Dialog_Light = materialTokenBuild;
        MaterialToken materialTokenBuild2 = new MaterialToken.Builder(30, "dialog-default", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setColorBlend(ColorBlendToken.Pured_Regular_Dark).setMaskBlur(100).setBloomStroke(BloomStrokeToken.Glass_Stroke_Small_Light).build();
        Default_Dialog_Dark = materialTokenBuild2;
        Default_Dialog_Material = new MaterialDayNightToken(materialTokenBuild, materialTokenBuild2);
    }

    /* JADX INFO: renamed from: miuix.appcompat.app.AlertController$2, reason: invalid class name */
    class AnonymousClass2 implements AlertDialog.OnDialogShowAnimListener {
        AnonymousClass2() {
        }

        @Override // miuix.appcompat.app.AlertDialog.OnDialogShowAnimListener
        public void onShowAnimStart() {
            AlertController.this.mIsDialogAnimating = true;
            if (AlertController.this.mShowAnimListener != null) {
                AlertController.this.mShowAnimListener.onShowAnimStart();
            }
        }

        @Override // miuix.appcompat.app.AlertDialog.OnDialogShowAnimListener
        public void onShowAnimComplete() {
            AlertController.this.mIsDialogAnimating = false;
            if (AlertController.this.mShowAnimListener != null) {
                AlertController.this.mShowAnimListener.onShowAnimComplete();
            }
            if (!AlertController.this.mHasPendingDismiss || AlertController.this.mDialog == null || AlertController.this.mWindow == null) {
                return;
            }
            AlertController.this.mWindow.getDecorView().post(new Runnable() { // from class: miuix.appcompat.app.AlertController$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1799x2ad64ed6();
                }
            });
        }

        /* JADX INFO: renamed from: lambda$onShowAnimComplete$0$miuix-appcompat-app-AlertController$2, reason: not valid java name */
        /* synthetic */ void m1799x2ad64ed6() {
            AlertController.this.mDialog.dismiss();
        }
    }

    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = -1651327837;
        private static final int MSG_RUN_ON_CLICK = -1651327821;
        private final WeakReference<DialogInterface> mDialog;

        ButtonHandler(DialogInterface dialogInterface) {
            this.mDialog = new WeakReference<>(dialogInterface);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            DialogInterface dialogInterface = this.mDialog.get();
            if (message.what == MSG_DISMISS_DIALOG) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            } else if (message.obj instanceof DialogInterface.OnClickListener) {
                ((DialogInterface.OnClickListener) message.obj).onClick(dialogInterface, message.what);
            }
        }
    }

    public void setPanelFixedWidth(int i) {
        this.mPanelFixedWidth = i;
    }

    public void setPanelFixedHeight(int i) {
        this.mPanelFixedHeight = i;
    }

    public void setPanelFixedSizeEnabled(boolean z) {
        this.mPanelFixedSizeEnabled = z;
    }

    public void setNavigationHiddenEnabled(boolean z) {
        this.mNavigationBarHiddenEnabled = z;
    }

    public void setDiscardNaviBarHeightEnabled(boolean z) {
        this.mDiscardNaviBarHeightEnabled = z;
    }

    public AlertController(Context context, AppCompatDialog appCompatDialog, Window window) {
        DialogDisplayStrategy dialogDisplayStrategy = new DialogDisplayStrategy();
        this.mDisplayStrategy = dialogDisplayStrategy;
        this.mPanelFixedWidth = -1;
        this.mPanelFixedHeight = -1;
        this.mPanelFixedSizeEnabled = false;
        this.mDefaultButtonsTextWatcher = new TextWatcher() { // from class: miuix.appcompat.app.AlertController.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (AlertController.this.mParentPanel == null || AlertController.this.mParentPanel.findViewById(R.id.buttonPanel) == null) {
                    return;
                }
                AlertController.this.mParentPanel.findViewById(R.id.buttonPanel).requestLayout();
            }
        };
        this.mIconId = 0;
        this.mCustomTitleTextView = null;
        this.mCheckedItem = -1;
        this.mDialogAnimHelper = new DialogAnimHelper();
        this.mCancelable = true;
        this.mCanceledOnTouchOutside = true;
        this.mScreenOrientation = 0;
        this.mTitleTextSize = 18.0f;
        this.mMessageTextSize = 16.0f;
        this.mCommentTextSize = 13.0f;
        this.mCustomTitleTextSize = 18.0f;
        this.mRootViewSize = new Point();
        this.mRootViewSizeDp = new Point();
        this.mScreenRealSize = new Point();
        this.mDisplayCutoutSafeInsets = new Rect();
        this.mHasPendingDismiss = false;
        this.mUseForceFlipCutout = false;
        this.mNavigationBarHiddenEnabled = false;
        this.mIsAssociatedActivityHideNavigationBar = false;
        this.mDiscardNaviBarHeightEnabled = false;
        this.mShowAnimListenerWrapper = new AnonymousClass2();
        this.mMinCustomVisibleHeight = 0;
        this.mPrimaryButtonFirstEnabled = false;
        this.mMaterialEnabled = false;
        this.mDiscardImeAnimEnabled = false;
        this.mButtonHandler = new View.OnClickListener() { // from class: miuix.appcompat.app.AlertController.3
            /* JADX WARN: Code duplicated, block: B:43:0x00ac  */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                boolean z;
                int i = HapticFeedbackConstants.MIUI_TAP_LIGHT;
                Message messageObtain = null;
                if (view == AlertController.this.mButtonPositive) {
                    messageObtain = AlertController.this.mButtonPositiveMessage != null ? Message.obtain(AlertController.this.mButtonPositiveMessage) : null;
                    i = HapticFeedbackConstants.MIUI_TAP_NORMAL;
                } else if (view == AlertController.this.mButtonNegative) {
                    if (AlertController.this.mButtonNegativeMessage != null) {
                        messageObtain = Message.obtain(AlertController.this.mButtonNegativeMessage);
                    }
                } else if (view != AlertController.this.mButtonNeutral) {
                    if (AlertController.this.mExtraButtonList != null && !AlertController.this.mExtraButtonList.isEmpty()) {
                        for (ButtonInfo buttonInfo : AlertController.this.mExtraButtonList) {
                            if (view == buttonInfo.mButton) {
                                Message messageObtain2 = buttonInfo.mMsg;
                                if (messageObtain2 != null) {
                                    messageObtain2 = Message.obtain(messageObtain2);
                                }
                                messageObtain = messageObtain2;
                                break;
                            }
                        }
                    }
                    if ((view instanceof GroupButton) && ((GroupButton) view).isPrimary()) {
                        i = HapticFeedbackConstants.MIUI_TAP_NORMAL;
                    }
                } else if (AlertController.this.mButtonNeutralMessage != null) {
                    messageObtain = Message.obtain(AlertController.this.mButtonNeutralMessage);
                }
                HapticCompat.performHapticFeedbackAsync(view, HapticFeedbackConstants.MIUI_BUTTON_MIDDLE, i);
                if (messageObtain != null) {
                    messageObtain.sendToTarget();
                    Bundle data = messageObtain.getData();
                    if (data == null || !data.containsKey(AlertDialog.KEY_BUTTON_CLICK_AUTO_DISMISSIBLE)) {
                        z = true;
                    } else {
                        z = data.getBoolean(AlertDialog.KEY_BUTTON_CLICK_AUTO_DISMISSIBLE);
                    }
                } else {
                    z = true;
                }
                AlertController.this.mHandler.sendEmptyMessage(z ? -1651327837 : -1651327821);
            }
        };
        this.mInsetsAnimationPlayed = false;
        dialogDisplayStrategy.setPanelBehavior(new DialogPanelBehaviorImpl()).setButtonBehavior(new DialogButtonBehaviorImpl());
        this.mContext = context;
        this.mCurrentDensityDpi = context.getResources().getConfiguration().densityDpi;
        this.mDialog = appCompatDialog;
        this.mWindow = window;
        this.mEnableEnterAnim = true;
        this.mNonImmersiveDialogShowAnimDuration = context.getResources().getInteger(R.integer.dialog_enter_duration);
        this.mHandler = new ButtonHandler(appCompatDialog);
        this.mLayoutChangeListener = new LayoutChangeListener(this);
        this.mIsFlipTinyScreen = Build.IS_FLIP && DeviceHelper.isTinyScreen(context);
        this.mIsEnableImmersive = (LiteUtils.isCommonLiteStrategy() || this.mIsFlipTinyScreen) ? false : true;
        updateDisplayInfo(context);
        initScreenMinorSize(context);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(null, R.styleable.AlertDialog, android.R.attr.alertDialogStyle, 0);
        this.mAlertDialogLayout = typedArrayObtainStyledAttributes.getResourceId(R.styleable.AlertDialog_layout, 0);
        this.mListLayout = typedArrayObtainStyledAttributes.getResourceId(R.styleable.AlertDialog_listLayout, 0);
        this.mMultiChoiceItemLayout = typedArrayObtainStyledAttributes.getResourceId(R.styleable.AlertDialog_multiChoiceItemLayout, 0);
        this.mSingleChoiceItemLayout = typedArrayObtainStyledAttributes.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 0);
        this.mListItemLayout = typedArrayObtainStyledAttributes.getResourceId(R.styleable.AlertDialog_listItemLayout, 0);
        this.mShowTitle = typedArrayObtainStyledAttributes.getBoolean(R.styleable.AlertDialog_showTitle, true);
        typedArrayObtainStyledAttributes.recycle();
        appCompatDialog.supportRequestWindowFeature(1);
        if (android.os.Build.VERSION.SDK_INT < 28 && isMiuiLegacyNotch()) {
            ReflectUtil.callObjectMethod(window, "addExtraFlags", new Class[]{Integer.TYPE}, 768);
        }
        updateDimensConfig(context.getResources());
        this.mMarkLandscape = context.getResources().getBoolean(R.bool.treat_as_land);
        this.mCreateThread = Thread.currentThread();
        isDialogImeDebugEnabled();
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "create Dialog mCurrentDensityDpi " + this.mCurrentDensityDpi);
        }
    }

    private void updateDimensConfig(Resources resources) {
        this.mDimensConfig.panelMaxWidth = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_max_width);
        this.mDimensConfig.panelMaxWidthLand = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_max_width_land);
        this.mDimensConfig.listViewMarginBottom = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_list_view_margin_bottom);
        this.mDimensConfig.extraImeMargin = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_ime_margin);
        this.mDimensConfig.fakeLandScreenMinorSize = resources.getDimensionPixelSize(R.dimen.fake_landscape_screen_minor_size);
        this.mDimensConfig.widthSmallMargin = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_width_small_margin);
        this.mDimensConfig.widthMargin = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_width_margin);
        this.mDimensConfig.freeTabletCompactHeight = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_freeform_bottom_height_tablet_t);
        this.mDimensConfig.freePhoneCompactHeight = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_freeform_bottom_height_phone_t);
        this.mDimensConfig.smallIconWidth = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_icon_drawable_width_small);
        this.mDimensConfig.smallIconHeight = resources.getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_icon_drawable_height_small);
    }

    private void updateDisplayInfo(Context context) {
        boolean zIsCarWithScreen = DeviceHelper.isCarWithScreen(context, null);
        this.mIsCarWithScreen = zIsCarWithScreen;
        if (!zIsCarWithScreen) {
            this.mIsSynergy = DeviceHelper.isXiaomiSynergy(context);
        } else {
            this.mIsSynergy = true;
        }
    }

    private boolean isNotch(WindowInsets windowInsets) {
        DisplayCutout displayCutout;
        List<Rect> boundingRects;
        return (windowInsets == null || (displayCutout = windowInsets.getDisplayCutout()) == null || (boundingRects = displayCutout.getBoundingRects()) == null || boundingRects.size() <= 0) ? false : true;
    }

    @Deprecated
    private boolean isMiuiLegacyNotch() {
        return ((Integer) ReflectUtil.callStaticObjectMethod(ReflectUtil.getClass("android.os.SystemProperties"), Integer.TYPE, "getInt", new Class[]{String.class, Integer.TYPE}, "ro.miui.notch", 0)).intValue() == 1;
    }

    static boolean canTextInput(View view) {
        if (view.onCheckIsTextEditor()) {
            return true;
        }
        if (!(view instanceof ViewGroup)) {
            return false;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        while (childCount > 0) {
            childCount--;
            if (canTextInput(viewGroup.getChildAt(childCount))) {
                return true;
            }
        }
        return false;
    }

    public void installContent(Bundle bundle) {
        this.mIsFromRebuild = bundle != null;
        this.mIsInFreeForm = MiuixUIUtils.isFreeformMode(this.mContext);
        getFlipCutout();
        this.mDialog.setContentView(this.mAlertDialogLayout);
        this.mDialogRootView = (DialogRootView) this.mWindow.findViewById(R.id.dialog_root_view);
        this.mDimBg = this.mWindow.findViewById(R.id.dialog_dim_bg);
        this.mDialogRootView.setConfigurationChangedCallback(new DialogRootView.ConfigurationChangedCallback() { // from class: miuix.appcompat.app.AlertController.4
            @Override // miuix.appcompat.internal.widget.DialogRootView.ConfigurationChangedCallback
            public void onConfigurationChanged(Configuration configuration, int i, int i2, int i3, int i4) {
                AlertController.this.onConfigurationChanged(configuration, false, false);
            }
        });
        Configuration configuration = this.mContext.getResources().getConfiguration();
        updateRootViewSize((Configuration) null);
        setupWindow();
        setupView();
        this.configurationAfterInstalled = configuration;
        this.buildJustNow = true;
        this.mDialogRootView.post(new Runnable() { // from class: miuix.appcompat.app.AlertController.5
            @Override // java.lang.Runnable
            public void run() {
                if (AlertController.this.isDialogImmersive()) {
                    AlertController alertController = AlertController.this;
                    alertController.updateRootViewSize(alertController.mDialogRootView);
                }
                ViewGroup viewGroup = (ViewGroup) AlertController.this.mParentPanel.findViewById(R.id.contentPanel);
                ViewGroup viewGroup2 = (ViewGroup) AlertController.this.mParentPanel.findViewById(R.id.buttonPanel);
                if (viewGroup2 == null || viewGroup == null || AlertController.this.shouldUseLandscapePanel()) {
                    return;
                }
                AlertController.this.updateButtons(viewGroup2, viewGroup);
            }
        });
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    void setPreferLandscape(boolean z) {
        this.mPreferLandscape = z;
    }

    void setUseForceFlipCutout(boolean z) {
        this.mUseForceFlipCutout = z;
    }

    void setButtonForceVertical(boolean z) {
        this.mButtonForceVertical = z;
    }

    void setContentForceFullScroll(boolean z) {
        this.mContentForceFullScroll = z;
    }

    void setMinCustomVisibleHeight(int i) {
        this.mMinCustomVisibleHeight = i;
    }

    void setPrimaryButtonFirstEnabled(boolean z) {
        this.mPrimaryButtonFirstEnabled = z;
    }

    void setMaterialEnabled(boolean z) {
        this.mMaterialEnabled = z;
    }

    void setAsyncInflatePanelEnable(boolean z) {
        this.mAsyncInflatePanelEnabled = z;
    }

    void setDiscardImeAnimEnabled(boolean z) {
        this.mDiscardImeAnimEnabled = z;
    }

    void setEnableEnterAnim(boolean z) {
        this.mEnableEnterAnim = z;
    }

    public void setCustomTitle(View view) {
        this.mCustomTitleView = view;
    }

    public void setMessage(CharSequence charSequence) {
        this.mMessage = charSequence;
        TextView textView = this.mMessageView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public void setComment(CharSequence charSequence) {
        this.mComment = charSequence;
        TextView textView = this.mCommentView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public TextView getMessageView() {
        return this.mMessageView;
    }

    public void setView(int i) {
        this.mView = null;
        this.mViewLayoutResId = i;
    }

    public void setView(View view) {
        this.mView = view;
        this.mViewLayoutResId = 0;
    }

    public void replaceView(int i, boolean z) {
        clearCustomContent();
        this.mView = null;
        this.mViewLayoutResId = i;
        onConfigurationChanged(this.mContext.getResources().getConfiguration(), true, z);
    }

    public void replaceView(View view, boolean z) {
        clearCustomContent();
        this.mView = view;
        this.mViewLayoutResId = 0;
        onConfigurationChanged(this.mContext.getResources().getConfiguration(), true, z);
    }

    public void setButton(int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener, Message message) {
        if (message == null && onClickListener != null) {
            message = this.mHandler.obtainMessage(i, onClickListener);
        }
        if (message != null && message.getTarget() == null) {
            message.setTarget(this.mHandler);
        }
        if (i == -3) {
            this.mButtonNeutralText = charSequence;
            this.mButtonNeutralMessage = message;
        } else if (i == -2) {
            this.mButtonNegativeText = charSequence;
            this.mButtonNegativeMessage = message;
        } else {
            if (i == -1) {
                this.mButtonPositiveText = charSequence;
                this.mButtonPositiveMessage = message;
                return;
            }
            throw new IllegalArgumentException("Button does not exist");
        }
    }

    void addExtraButton(ButtonInfo buttonInfo) {
        if (TextUtils.isEmpty(buttonInfo.mText)) {
            return;
        }
        if (this.mExtraButtonList == null) {
            this.mExtraButtonList = new ArrayList();
        }
        this.mExtraButtonList.add(buttonInfo);
    }

    void clearExtraButton() {
        List<ButtonInfo> list = this.mExtraButtonList;
        if (list != null) {
            list.clear();
        }
    }

    public void setIcon(int i) {
        this.mIcon = null;
        this.mIconId = i;
    }

    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        this.mIconId = 0;
    }

    public void setUseSmallIcon(boolean z) {
        this.mSmallIcon = z;
    }

    public void setIconSize(int i, int i2) {
        this.mIconWidth = i;
        this.mIconHeight = i2;
    }

    public int getIconAttributeResId(int i) {
        TypedValue typedValue = new TypedValue();
        this.mContext.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.resourceId;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public Button getButton(int i) {
        if (i == -3) {
            return this.mButtonNeutral;
        }
        if (i == -2) {
            return this.mButtonNegative;
        }
        if (i == -1) {
            return this.mButtonPositive;
        }
        List<ButtonInfo> list = this.mExtraButtonList;
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (ButtonInfo buttonInfo : this.mExtraButtonList) {
            if (buttonInfo.mWhich == i) {
                return buttonInfo.mButton;
            }
        }
        return null;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return keyEvent.getKeyCode() == 82;
    }

    public void setCancelable(boolean z) {
        this.mCancelable = z;
    }

    public void setCanceledOnTouchOutside(boolean z) {
        this.mCanceledOnTouchOutside = z;
    }

    private boolean isCancelable() {
        return this.mCancelable;
    }

    public boolean isCanceledOnTouchOutside() {
        return this.mCanceledOnTouchOutside;
    }

    private void hideSoftIME() {
        InputMethodManager inputMethodManager = (InputMethodManager) ContextCompat.getSystemService(this.mContext, InputMethodManager.class);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(this.mParentPanel.getWindowToken(), 0);
        }
    }

    private void setupView() {
        setupView(true, false, false, 1.0f);
        storeCustomViewInitialTextSize();
    }

    private void storeCustomViewInitialTextSize() {
        DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
        float f = displayMetrics.scaledDensity;
        float f2 = displayMetrics.density;
        View view = this.mCustomTitleView;
        if (view != null) {
            this.mCustomTitleTextView = (TextView) view.findViewById(android.R.id.title);
        }
        TextView textView = this.mCustomTitleTextView;
        if (textView != null) {
            this.mCustomTitleTextSize = textView.getTextSize();
            int textSizeUnit = android.os.Build.VERSION.SDK_INT >= 30 ? this.mCustomTitleTextView.getTextSizeUnit() : 2;
            if (textSizeUnit == 1) {
                this.mCustomTitleTextSize /= f2;
            } else if (textSizeUnit == 2) {
                this.mCustomTitleTextSize /= f;
            }
        }
    }

    private void setupView(boolean z, boolean z2, boolean z3, final float f) {
        ListAdapter listAdapter;
        if (isDialogImmersive()) {
            this.mDimBg.setOnClickListener(new View.OnClickListener() { // from class: miuix.appcompat.app.AlertController$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.m1796lambda$setupView$0$miuixappcompatappAlertController(view);
                }
            });
            updateImmersiveDialogPanel();
        } else {
            if (isSpecifiedDialogHeight()) {
                this.mDialogRootView.setOnClickListener(new View.OnClickListener() { // from class: miuix.appcompat.app.AlertController$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.m1797lambda$setupView$1$miuixappcompatappAlertController(view);
                    }
                });
            }
            this.mDimBg.setVisibility(8);
        }
        if (z || z2 || this.mPreferLandscape) {
            ViewGroup viewGroup = (ViewGroup) this.mParentPanel.findViewById(R.id.topPanel);
            ViewGroup viewGroup2 = (ViewGroup) this.mParentPanel.findViewById(R.id.contentPanel);
            ViewGroup viewGroup3 = (ViewGroup) this.mParentPanel.findViewById(R.id.buttonPanel);
            if (viewGroup2 instanceof NestedScrollViewExpander) {
                ((NestedScrollViewExpander) viewGroup2).setMinCustomVisibleHeight(this.mMinCustomVisibleHeight);
            }
            if (viewGroup2 != null) {
                setupContent(viewGroup2, z3);
            }
            if (viewGroup3 instanceof DialogButtonPanel) {
                DialogButtonPanel dialogButtonPanel = (DialogButtonPanel) viewGroup3;
                dialogButtonPanel.isContentLandscape(shouldUseLandscapePanel());
                dialogButtonPanel.setPrimaryButtonFirstEnabled(this.mPrimaryButtonFirstEnabled);
                setupButtons(viewGroup3);
            }
            if (viewGroup != null) {
                setupTitle(viewGroup);
            }
            if (viewGroup != null && viewGroup.getVisibility() != 8) {
                View viewFindViewById = (this.mMessage == null && this.mListView == null) ? null : viewGroup.findViewById(R.id.titleDividerNoCustom);
                if (viewFindViewById != null) {
                    viewFindViewById.setVisibility(0);
                }
            }
            ListView listView = this.mListView;
            if (listView != null && (listAdapter = this.mAdapter) != null) {
                listView.setAdapter(listAdapter);
                int i = this.mCheckedItem;
                if (i > -1) {
                    listView.setItemChecked(i, true);
                    listView.setSelection(i);
                }
            }
            ViewStub viewStub = (ViewStub) this.mParentPanel.findViewById(R.id.checkbox_stub);
            if (viewStub != null) {
                setupCheckbox(this.mParentPanel, viewStub);
            }
            setupMaterial();
            if (!z) {
                onLayoutReload();
            }
        } else {
            this.mParentPanel.post(new Runnable() { // from class: miuix.appcompat.app.AlertController.6
                @Override // java.lang.Runnable
                public void run() {
                    ViewGroup viewGroup4 = (ViewGroup) AlertController.this.mParentPanel.findViewById(R.id.contentPanel);
                    ViewGroup viewGroup5 = (ViewGroup) AlertController.this.mParentPanel.findViewById(R.id.buttonPanel);
                    if (viewGroup4 != null) {
                        AlertController.this.updateContent(viewGroup4);
                        if (viewGroup5 != null && !AlertController.this.mPreferLandscape) {
                            AlertController.this.updateButtons(viewGroup5, viewGroup4);
                        }
                    }
                    float f2 = f;
                    if (f2 != 1.0f) {
                        AlertController.this.updateViewOnDensityChanged(f2);
                    }
                }
            });
        }
        this.mParentPanel.post(new Runnable() { // from class: miuix.appcompat.app.AlertController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1798lambda$setupView$2$miuixappcompatappAlertController();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setupView$0$miuix-appcompat-app-AlertController, reason: not valid java name */
    /* synthetic */ void m1796lambda$setupView$0$miuixappcompatappAlertController(View view) {
        if (isCancelable() && isCanceledOnTouchOutside()) {
            this.mDialog.cancel();
        }
    }

    /* JADX INFO: renamed from: lambda$setupView$1$miuix-appcompat-app-AlertController, reason: not valid java name */
    /* synthetic */ void m1797lambda$setupView$1$miuixappcompatappAlertController(View view) {
        if (isCancelable() && isCanceledOnTouchOutside()) {
            hideSoftIME();
            this.mDialog.cancel();
        }
    }

    /* JADX INFO: renamed from: lambda$setupView$2$miuix-appcompat-app-AlertController, reason: not valid java name */
    /* synthetic */ void m1798lambda$setupView$2$miuixappcompatappAlertController() {
        AlertDialog.OnPanelSizeChangedListener onPanelSizeChangedListener = this.mPanelSizeChangedListener;
        if (onPanelSizeChangedListener != null) {
            onPanelSizeChangedListener.onPanelSizeChanged((AlertDialog) this.mDialog, this.mParentPanel);
        }
    }

    private void setupMaterial() {
        if (this.mMaterialEnabled && HyperMaterialUtils.isFeatureEnable(this.mContext)) {
            if (MiuiBlurUtils.setPassWindowBlurEnabled(this.mParentPanel, true)) {
                this.mParentPanel.post(new Runnable() { // from class: miuix.appcompat.app.AlertController.7
                    @Override // java.lang.Runnable
                    public void run() {
                        MaterialDayNightConfig materialDayNightConfigCreate;
                        MaterialConfig materialConfig;
                        if (AlertController.this.mParentPanel == null || ((ViewGroup) AlertController.this.mParentPanel.getParent()) == null || !MiuiBlurUtils.getPassWindowBlurEnabled(AlertController.this.mParentPanel) || (materialDayNightConfigCreate = MaterialDayNightConfig.create(AlertController.Default_Dialog_Material)) == null || (materialConfig = materialDayNightConfigCreate.get(AlertController.this.isLightTheme())) == null) {
                            return;
                        }
                        HyperMaterialUtils.applyContainer(AlertController.this.mParentPanel, materialConfig.getBlurConfig());
                        HyperMaterialUtils.applyElement(AlertController.this.mParentPanel, materialConfig);
                        Drawable background = AlertController.this.mParentPanel.getBackground();
                        if (background != null) {
                            background.setAlpha(0);
                        }
                    }
                });
            }
        } else if (MiuiBlurUtils.getPassWindowBlurEnabled(this.mParentPanel)) {
            MiuiBlurUtils.setPassWindowBlurEnabled(this.mParentPanel, false);
            Drawable background = this.mParentPanel.getBackground();
            if (background != null) {
                background.setAlpha(255);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateViewOnDensityChanged(float f) {
        TextView textView;
        DialogParentPanel2 dialogParentPanel2 = this.mParentPanel;
        if (dialogParentPanel2 != null) {
            DensityChangedHelper.updateViewPadding(dialogParentPanel2, f);
        }
        TextView textView2 = this.mTitleView;
        if (textView2 != null) {
            DensityChangedHelper.updateTextSizeSpUnit(textView2, this.mTitleTextSize);
        }
        TextView textView3 = this.mMessageView;
        if (textView3 != null) {
            DensityChangedHelper.updateTextSizeSpUnit(textView3, this.mMessageTextSize);
        }
        TextView textView4 = this.mCommentView;
        if (textView4 != null) {
            DensityChangedHelper.updateTextSizeSpUnit(textView4, this.mCommentTextSize);
            DensityChangedHelper.updateViewPadding(this.mCommentView, f);
        }
        if (this.mCustomTitleView != null && (textView = this.mCustomTitleTextView) != null) {
            DensityChangedHelper.updateTextSizeDefaultUnit(textView, this.mCustomTitleTextSize);
        }
        View viewFindViewById = this.mWindow.findViewById(R.id.buttonPanel);
        if (viewFindViewById != null) {
            DensityChangedHelper.updateViewMargin(viewFindViewById, f);
        }
        ViewGroup viewGroup = (ViewGroup) this.mWindow.findViewById(R.id.topPanel);
        if (viewGroup != null) {
            DensityChangedHelper.updateViewPadding(viewGroup, f);
        }
        View viewFindViewById2 = this.mWindow.findViewById(R.id.contentView);
        if (viewFindViewById2 != null) {
            DensityChangedHelper.updateViewMargin(viewFindViewById2, f);
        }
        CheckBox checkBox = (CheckBox) this.mWindow.findViewById(android.R.id.checkbox);
        if (checkBox != null) {
            DensityChangedHelper.updateViewMargin(checkBox, f);
        }
        ImageView imageView = (ImageView) this.mWindow.findViewById(android.R.id.icon);
        if (imageView != null) {
            DensityChangedHelper.updateViewSize(imageView, f);
            DensityChangedHelper.updateViewMargin(imageView, f);
        }
    }

    private boolean setupCustomContent(ViewGroup viewGroup) {
        View view = this.mInflatedView;
        View viewInflate = null;
        if (view != null && view.getParent() != null) {
            safeRemoveFromParent(this.mInflatedView);
            this.mInflatedView = null;
        }
        View view2 = this.mView;
        if (view2 != null) {
            viewInflate = view2;
        } else if (this.mViewLayoutResId != 0) {
            viewInflate = LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, viewGroup, false);
            this.mInflatedView = viewInflate;
        }
        boolean z = viewInflate != null;
        if (!z || !canTextInput(viewInflate)) {
            this.mWindow.setFlags(131072, 131072);
        } else {
            this.mWindow.clearFlags(131072);
        }
        setAnimIfEditExistForNonImmersive(viewInflate);
        if (z) {
            safeMoveView(viewInflate, viewGroup);
        } else {
            safeRemoveFromParent(viewGroup);
        }
        return z;
    }

    private void setAnimIfEditExistForNonImmersive(View view) {
        if (!this.mEnableEnterAnim || view == null || isTablet() || isDialogImmersive() || !canTextInput(view)) {
            return;
        }
        this.mWindow.setWindowAnimations(R.style.Animation_Dialog_ExistIme);
    }

    private void clearCustomContent() {
        View view = this.mInflatedView;
        if (view != null && view.getParent() != null) {
            safeRemoveFromParent(this.mInflatedView);
            this.mInflatedView = null;
        }
        View view2 = this.mView;
        if (view2 == null || view2.getParent() == null) {
            return;
        }
        safeRemoveFromParent(this.mView);
        this.mView = null;
    }

    private void setupTitle(ViewGroup viewGroup) {
        ImageView imageView = (ImageView) this.mWindow.findViewById(android.R.id.icon);
        View view = this.mCustomTitleView;
        if (view != null) {
            safeRemoveFromParent(view);
            viewGroup.addView(this.mCustomTitleView, 0, new ViewGroup.LayoutParams(-1, -2));
            this.mWindow.findViewById(R.id.alertTitle).setVisibility(8);
            imageView.setVisibility(8);
            return;
        }
        if ((!TextUtils.isEmpty(this.mTitle)) && this.mShowTitle) {
            TextView textView = (TextView) this.mWindow.findViewById(R.id.alertTitle);
            this.mTitleView = textView;
            textView.setText(this.mTitle);
            int i = this.mIconId;
            if (i != 0) {
                imageView.setImageResource(i);
            } else {
                Drawable drawable = this.mIcon;
                if (drawable != null) {
                    imageView.setImageDrawable(drawable);
                } else {
                    this.mTitleView.setPadding(imageView.getPaddingLeft(), imageView.getPaddingTop(), imageView.getPaddingRight(), imageView.getPaddingBottom());
                    imageView.setVisibility(8);
                }
            }
            if (this.mSmallIcon) {
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = this.mDimensConfig.smallIconWidth;
                layoutParams.height = this.mDimensConfig.smallIconHeight;
            }
            if (this.mIconWidth != 0 && this.mIconHeight != 0) {
                ViewGroup.LayoutParams layoutParams2 = imageView.getLayoutParams();
                layoutParams2.width = this.mIconWidth;
                layoutParams2.height = this.mIconHeight;
            }
            if (this.mMessage == null || viewGroup.getVisibility() == 8) {
                return;
            }
            changeTitlePadding(this.mTitleView);
            return;
        }
        this.mWindow.findViewById(R.id.alertTitle).setVisibility(8);
        imageView.setVisibility(8);
        viewGroup.setVisibility(8);
    }

    public int getSingleItemMinHeight() {
        return AttributeResolver.resolveDimensionPixelSize(this.mContext, R.attr.dialogListPreferredItemHeight);
    }

    private int computeParentPanelMaxHeight() {
        float f;
        boolean zIsPortrait = WindowUtils.isPortrait(this.mContext);
        boolean z = this.mIsFlipTinyScreen || this.mIsInFreeForm;
        Point screenSize = EnvStateManager.getScreenSize(this.mContext);
        int iPx2dp = MiuixUIUtils.px2dp(this.mContext, screenSize.y);
        if (z) {
            return screenSize.y;
        }
        float f2 = 0.7f;
        if (zIsPortrait) {
            return (int) (screenSize.y * 0.7f);
        }
        if (iPx2dp >= 500) {
            f = screenSize.y;
        } else {
            f = screenSize.y;
            f2 = 0.9f;
        }
        return (int) (f * f2);
    }

    private boolean listViewIsNeedFullScroll() {
        int singleItemMinHeight = getSingleItemMinHeight();
        int count = this.mAdapter.getCount() * singleItemMinHeight;
        boolean z = MiuixUIUtils.getFontLevel(this.mContext) == 2;
        int iComputeParentPanelMaxHeight = computeParentPanelMaxHeight();
        if (this.mIsDebugEnabled) {
            Log.i(TAG, "listViewIsNeedFullScroll: itemsMinHeight = " + count + ", singleItemMinHeight = " + singleItemMinHeight + ", panelMaxHeight = " + iComputeParentPanelMaxHeight);
        }
        if (!z || iComputeParentPanelMaxHeight <= 0) {
            return count > ((int) (((float) this.mRootViewSize.y) * 0.35f));
        }
        float f = (count * 1.0f) / iComputeParentPanelMaxHeight;
        if (this.mIsDebugEnabled) {
            Log.i(TAG, "listViewIsNeedFullScroll: radioInMaxHeight = " + f);
        }
        return f >= 0.3f;
    }

    private void resetListMaxHeight() {
        int i = (int) (this.mRootViewSize.y * 0.35f);
        int singleItemMinHeight = getSingleItemMinHeight();
        int i2 = singleItemMinHeight * (i / singleItemMinHeight);
        this.mListView.setMinimumHeight(i2);
        ViewGroup.LayoutParams layoutParams = this.mListView.getLayoutParams();
        layoutParams.height = i2;
        this.mListView.setLayoutParams(layoutParams);
    }

    private void adjustHeight2WrapContent() {
        ViewGroup.LayoutParams layoutParams = this.mListView.getLayoutParams();
        layoutParams.height = -2;
        this.mListView.setLayoutParams(layoutParams);
    }

    private void setupContent(ViewGroup viewGroup, boolean z) {
        View childAt;
        FrameLayout frameLayout = (FrameLayout) viewGroup.findViewById(android.R.id.custom);
        boolean z2 = false;
        if (frameLayout != null) {
            if (z) {
                LayoutTransition layoutTransition = new LayoutTransition();
                layoutTransition.setDuration(0, 200L);
                layoutTransition.setInterpolator(0, new CubicEaseInOutInterpolator());
                frameLayout.setLayoutTransition(layoutTransition);
            } else {
                frameLayout.setLayoutTransition(null);
            }
        }
        if (this.mListView != null) {
            if (frameLayout != null ? setupCustomContent(frameLayout) : false) {
                viewGroup.removeView(viewGroup.findViewById(R.id.contentView));
                safeRemoveFromParent(frameLayout);
                LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
                linearLayout.setOrientation(1);
                safeRemoveFromParent(this.mListView);
                ViewCompat.setNestedScrollingEnabled(this.mListView, true);
                linearLayout.addView(this.mListView, 0, new ViewGroup.MarginLayoutParams(-1, -2));
                boolean zListViewIsNeedFullScroll = listViewIsNeedFullScroll();
                if (!zListViewIsNeedFullScroll) {
                    adjustHeight2WrapContent();
                    linearLayout.addView(frameLayout, new LinearLayout.LayoutParams(-1, 0, 1.0f));
                } else {
                    resetListMaxHeight();
                    linearLayout.addView(frameLayout, new LinearLayout.LayoutParams(-1, -2, 0.0f));
                }
                viewGroup.addView(linearLayout, 0, new ViewGroup.MarginLayoutParams(-1, -2));
                ViewGroup viewGroup2 = (ViewGroup) viewGroup.findViewById(R.id.contentView);
                if (viewGroup2 != null) {
                    setupContentView(viewGroup2);
                }
                ((NestedScrollViewExpander) viewGroup).setExpandView(zListViewIsNeedFullScroll ? null : linearLayout);
                return;
            }
            viewGroup.removeView(viewGroup.findViewById(R.id.contentView));
            if (frameLayout != null) {
                safeRemoveFromParent(frameLayout);
            }
            safeRemoveFromParent(this.mListView);
            this.mListView.setMinimumHeight(getSingleItemMinHeight());
            ViewCompat.setNestedScrollingEnabled(this.mListView, true);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-1, -2);
            if (getVisibleButtonCount() > 0 && !shouldUseLandscapePanel()) {
                marginLayoutParams.bottomMargin = this.mDimensConfig.listViewMarginBottom;
            }
            viewGroup.addView(this.mListView, 0, marginLayoutParams);
            ((NestedScrollViewExpander) viewGroup).setExpandView(this.mListView);
            return;
        }
        ViewGroup viewGroup3 = (ViewGroup) viewGroup.findViewById(R.id.contentView);
        if (viewGroup3 != null) {
            setupContentView(viewGroup3);
        }
        if (frameLayout != null) {
            boolean z3 = setupCustomContent(frameLayout);
            if (z3 && (childAt = frameLayout.getChildAt(0)) != null) {
                ViewCompat.setNestedScrollingEnabled(childAt, true);
            }
            z2 = z3;
        }
        NestedScrollViewExpander nestedScrollViewExpander = (NestedScrollViewExpander) viewGroup;
        if (!z2) {
            frameLayout = null;
        }
        nestedScrollViewExpander.setExpandView(frameLayout);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContent(ViewGroup viewGroup) {
        FrameLayout frameLayout = (FrameLayout) viewGroup.findViewById(android.R.id.custom);
        boolean z = frameLayout != null && frameLayout.getChildCount() > 0;
        ListView listView = this.mListView;
        if (listView == null) {
            if (z) {
                ViewCompat.setNestedScrollingEnabled(frameLayout.getChildAt(0), true);
            }
            NestedScrollViewExpander nestedScrollViewExpander = (NestedScrollViewExpander) viewGroup;
            if (!z) {
                frameLayout = null;
            }
            nestedScrollViewExpander.setExpandView(frameLayout);
            return;
        }
        if (z) {
            if (!listViewIsNeedFullScroll()) {
                adjustHeight2WrapContent();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                layoutParams.height = 0;
                layoutParams.weight = 1.0f;
                frameLayout.setLayoutParams(layoutParams);
                ((NestedScrollViewExpander) viewGroup).setExpandView((View) frameLayout.getParent());
                viewGroup.requestLayout();
                return;
            }
            resetListMaxHeight();
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
            layoutParams2.height = -2;
            layoutParams2.weight = 0.0f;
            frameLayout.setLayoutParams(layoutParams2);
            ((NestedScrollViewExpander) viewGroup).setExpandView(null);
            viewGroup.requestLayout();
            return;
        }
        ((NestedScrollViewExpander) viewGroup).setExpandView(listView);
    }

    private void setupContentView(ViewGroup viewGroup) {
        CharSequence charSequence;
        this.mMessageView = (TextView) viewGroup.findViewById(R.id.message);
        this.mCommentView = (TextView) viewGroup.findViewById(R.id.comment);
        TextView textView = this.mMessageView;
        if (textView != null && (charSequence = this.mMessage) != null) {
            textView.setText(charSequence);
            TextView textView2 = this.mCommentView;
            if (textView2 != null) {
                CharSequence charSequence2 = this.mComment;
                if (charSequence2 != null) {
                    textView2.setText(charSequence2);
                    return;
                } else {
                    textView2.setVisibility(8);
                    return;
                }
            }
            return;
        }
        safeRemoveFromParent(viewGroup);
    }

    private void disableForceDark(View view) {
        CompatViewMethod.setForceDarkAllowed(view, false);
    }

    private void setupButtons(ViewGroup viewGroup) {
        int i;
        Button button = (Button) viewGroup.findViewById(16908313);
        this.mButtonPositive = button;
        button.setOnClickListener(this.mButtonHandler);
        this.mButtonPositive.removeTextChangedListener(this.mDefaultButtonsTextWatcher);
        this.mButtonPositive.addTextChangedListener(this.mDefaultButtonsTextWatcher);
        boolean z = true;
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(8);
            i = 0;
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            this.mButtonPositive.setVisibility(0);
            disableForceDark(this.mButtonPositive);
            i = 1;
        }
        Button button2 = (Button) viewGroup.findViewById(16908314);
        this.mButtonNegative = button2;
        button2.setOnClickListener(this.mButtonHandler);
        this.mButtonNegative.removeTextChangedListener(this.mDefaultButtonsTextWatcher);
        this.mButtonNegative.addTextChangedListener(this.mDefaultButtonsTextWatcher);
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(8);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            this.mButtonNegative.setVisibility(0);
            i++;
            disableForceDark(this.mButtonNegative);
        }
        Button button3 = (Button) viewGroup.findViewById(android.R.id.button3);
        this.mButtonNeutral = button3;
        button3.setOnClickListener(this.mButtonHandler);
        this.mButtonNeutral.removeTextChangedListener(this.mDefaultButtonsTextWatcher);
        this.mButtonNeutral.addTextChangedListener(this.mDefaultButtonsTextWatcher);
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(8);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            this.mButtonNeutral.setVisibility(0);
            i++;
            disableForceDark(this.mButtonNeutral);
        }
        List<ButtonInfo> list = this.mExtraButtonList;
        if (list != null && !list.isEmpty()) {
            for (ButtonInfo buttonInfo : this.mExtraButtonList) {
                if (buttonInfo.mButton != null) {
                    safeRemoveFromParent(buttonInfo.mButton);
                }
            }
            for (ButtonInfo buttonInfo2 : this.mExtraButtonList) {
                if (buttonInfo2.mButton == null) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
                    buttonInfo2.mButton = new GroupButton(this.mContext, null, buttonInfo2.mStyle);
                    buttonInfo2.mButton.setText(buttonInfo2.mText);
                    buttonInfo2.mButton.setOnClickListener(this.mButtonHandler);
                    buttonInfo2.mButton.setLayoutParams(layoutParams);
                    buttonInfo2.mButton.setMaxLines(1);
                    buttonInfo2.mButton.setGravity(17);
                }
                if (buttonInfo2.mMsg == null) {
                    buttonInfo2.mMsg = this.mHandler.obtainMessage(buttonInfo2.mWhich, buttonInfo2.mOnClickListener);
                }
                if (buttonInfo2.mButton.getVisibility() != 8) {
                    i++;
                    disableForceDark(buttonInfo2.mButton);
                }
                viewGroup.addView(buttonInfo2.mButton);
            }
        }
        if (viewGroup instanceof DialogButtonPanel) {
            DialogButtonPanel dialogButtonPanel = (DialogButtonPanel) viewGroup;
            dialogButtonPanel.setHorizontalPositionConfirmed(false);
            dialogButtonPanel.setVerticalPositionConfirmed(false);
        }
        recordButtonTypeForOS3Display(viewGroup);
        if (i == 0) {
            viewGroup.setVisibility(8);
        } else {
            ((DialogButtonPanel) viewGroup).setForceVertical(this.mButtonForceVertical || this.mLandscapePanel || (this.mIsFlipTinyScreen && WindowUtils.isPortrait(this.mContext)) || (MiuixUIUtils.getFontLevel(this.mContext) == 2));
        }
        Point point = new Point();
        WindowUtils.getScreenSize(this.mContext, point);
        int iMax = Math.max(point.x, point.y);
        ViewGroup viewGroup2 = (ViewGroup) this.mParentPanel.findViewById(R.id.contentPanel);
        boolean zButtonNeedScrollable = buttonNeedScrollable((DialogButtonPanel) viewGroup, i);
        if (this.mRootViewSize.y > iMax * 0.33f && !zButtonNeedScrollable) {
            z = false;
        }
        if (this.mLandscapePanel) {
            return;
        }
        if (!z) {
            safeMoveView(viewGroup, this.mParentPanel);
        } else {
            safeMoveView(viewGroup, viewGroup2);
            ((NestedScrollViewExpander) viewGroup2).setExpandView(null);
        }
    }

    private void recordButtonTypeForOS3Display(ViewGroup viewGroup) {
        if (RomUtils.getHyperOsVersion() > 2 || this.mPrimaryButtonFirstEnabled) {
            boolean z = viewGroup instanceof DialogButtonPanel;
            if (z && (this.mButtonPositive instanceof GroupButton)) {
                DialogButtonPanel dialogButtonPanel = (DialogButtonPanel) viewGroup;
                dialogButtonPanel.clearPrimaryStyleButtonList();
                dialogButtonPanel.addPrimaryStyleButtons((GroupButton) this.mButtonPositive);
            }
            if (z && (this.mButtonNegative instanceof GroupButton)) {
                DialogButtonPanel dialogButtonPanel2 = (DialogButtonPanel) viewGroup;
                dialogButtonPanel2.clearNegativeStyleButtonList();
                dialogButtonPanel2.addNegativeStyleButtons((GroupButton) this.mButtonNegative);
            }
            if (z && (this.mButtonNeutral instanceof GroupButton)) {
                DialogButtonPanel dialogButtonPanel3 = (DialogButtonPanel) viewGroup;
                dialogButtonPanel3.clearNeutralStyleButtonList();
                dialogButtonPanel3.addNeutralStyleButtons((GroupButton) this.mButtonNeutral);
            }
            List<ButtonInfo> list = this.mExtraButtonList;
            if (list == null || list.isEmpty()) {
                return;
            }
            for (ButtonInfo buttonInfo : this.mExtraButtonList) {
                if (buttonInfo != null && buttonInfo.mButton != null && z) {
                    if (buttonInfo.mStyle == 16843913 || buttonInfo.mStyle == R.attr.buttonBarPrimaryButtonStyle) {
                        ((DialogButtonPanel) viewGroup).addPrimaryStyleButtons(buttonInfo.mButton);
                    } else if (buttonInfo.mStyle == 16843915 || buttonInfo.mStyle == R.attr.buttonBarButtonStyle || buttonInfo.mStyle == 16843567) {
                        ((DialogButtonPanel) viewGroup).addNegativeStyleButtons(buttonInfo.mButton);
                    } else if (buttonInfo.mStyle == 16843914 || buttonInfo.mStyle == R.attr.buttonBarButtonStyle) {
                        ((DialogButtonPanel) viewGroup).addNeutralStyleButtons(buttonInfo.mButton);
                    }
                }
            }
        }
    }

    private int getVisibleButtonCount() {
        Button button = this.mButtonPositive;
        int i = 1;
        if (button == null) {
            i = 1 ^ (TextUtils.isEmpty(this.mButtonPositiveText) ? 1 : 0);
        } else if (button.getVisibility() != 0) {
            i = 0;
        }
        Button button2 = this.mButtonNegative;
        if (button2 == null ? !TextUtils.isEmpty(this.mButtonNegativeText) : button2.getVisibility() == 0) {
            i++;
        }
        Button button3 = this.mButtonNeutral;
        if (button3 == null ? !TextUtils.isEmpty(this.mButtonNeutralText) : button3.getVisibility() == 0) {
            i++;
        }
        List<ButtonInfo> list = this.mExtraButtonList;
        if (list != null && !list.isEmpty()) {
            Iterator<ButtonInfo> it = this.mExtraButtonList.iterator();
            while (it.hasNext()) {
                GroupButton groupButton = it.next().mButton;
                if (groupButton == null || groupButton.getVisibility() == 0) {
                    i++;
                }
            }
        }
        return i;
    }

    private boolean buttonNeedScrollable(DialogButtonPanel dialogButtonPanel, int i) {
        int buttonFullyVisibleHeight = dialogButtonPanel.getButtonFullyVisibleHeight();
        ViewGroup viewGroup = (ViewGroup) this.mParentPanel.findViewById(R.id.topPanel);
        int height = viewGroup != null ? viewGroup.getHeight() : 0;
        int i2 = EnvStateManager.getWindowSize(this.mContext).y;
        boolean z = MiuixUIUtils.getFontLevel(this.mContext) == 2;
        DialogContract.ButtonScrollSpec buttonScrollSpec = new DialogContract.ButtonScrollSpec();
        buttonScrollSpec.updateData(buttonFullyVisibleHeight, dialogButtonPanel.getHeight(), i2, height, this.mIsFlipTinyScreen, getScreenOrientation(), i, this.mRootViewSizeDp.y, z, this.mListView != null);
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "buttonNeedScrollable: buttonScrollSpec = " + buttonScrollSpec);
        }
        return ifNeedMoveButtonToContentPanel(dialogButtonPanel, viewGroup) || this.mDisplayStrategy.isButtonScrollable(buttonScrollSpec);
    }

    private boolean ifNeedMoveButtonToContentPanel(DialogButtonPanel dialogButtonPanel, ViewGroup viewGroup) {
        int height;
        DialogParentPanel2 dialogParentPanel2 = this.mParentPanel;
        if (dialogParentPanel2 == null || dialogButtonPanel == null || viewGroup == null || this.mLandscapePanel || (height = dialogParentPanel2.getHeight()) == 0) {
            return false;
        }
        int paddingTop = this.mParentPanel.getPaddingTop();
        int paddingBottom = this.mParentPanel.getPaddingBottom();
        int height2 = viewGroup.getHeight();
        int height3 = dialogButtonPanel.getHeight();
        ViewGroup.LayoutParams layoutParams = dialogButtonPanel.getLayoutParams();
        int i = layoutParams instanceof ViewGroup.MarginLayoutParams ? ((ViewGroup.MarginLayoutParams) layoutParams).topMargin : 0;
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "ifNeedMoveButtonToContentPanel: topPanelHeight = " + height2 + ", buttonPanelHeight = " + height3 + ", buttonPanelMarginTop = " + i + ", parentPanelPaddingTop = " + paddingTop + ", parentPanelPaddingBottom = " + paddingBottom + ", parentPanelHeight = " + height);
        }
        return (((height2 + height3) + i) + paddingTop) + paddingBottom > height;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateButtons(ViewGroup viewGroup, ViewGroup viewGroup2) {
        int visibleButtonCount = getVisibleButtonCount();
        Point point = new Point();
        WindowUtils.getScreenSize(this.mContext, point);
        DialogButtonPanel dialogButtonPanel = (DialogButtonPanel) viewGroup;
        boolean z = ((float) this.mRootViewSize.y) <= ((float) Math.max(point.x, point.y)) * 0.33f || buttonNeedScrollable(dialogButtonPanel, visibleButtonCount) || this.mContentForceFullScroll;
        dialogButtonPanel.setForceVertical(this.mButtonForceVertical || this.mLandscapePanel || (this.mIsFlipTinyScreen && (this.mContext.getResources().getConfiguration().orientation == 1)) || (MiuixUIUtils.getFontLevel(this.mContext) == 2));
        if (!z) {
            safeMoveView(viewGroup, this.mParentPanel);
        } else {
            safeMoveView(viewGroup, viewGroup2);
            ((NestedScrollViewExpander) viewGroup2).setExpandView(null);
        }
    }

    protected void safeRemoveFromParent(View view) {
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
    }

    protected void safeMoveView(View view, ViewGroup viewGroup) {
        ViewGroup viewGroup2 = (ViewGroup) view.getParent();
        if (viewGroup2 == viewGroup) {
            return;
        }
        if (viewGroup2 != null) {
            viewGroup2.removeView(view);
        }
        viewGroup.addView(view);
    }

    private void setupWindowInsetsAnimation() {
        if (isDialogImmersive()) {
            this.mWindow.setSoftInputMode((this.mWindow.getAttributes().softInputMode & 15) | 48);
            WindowInsetsAnimation.Callback callback = new WindowInsetsAnimation.Callback(1) { // from class: miuix.appcompat.app.AlertController.8
                boolean isTablet = false;

                @Override // android.view.WindowInsetsAnimation.Callback
                public void onPrepare(WindowInsetsAnimation windowInsetsAnimation) {
                    super.onPrepare(windowInsetsAnimation);
                    if (windowInsetsAnimation != null && (windowInsetsAnimation.getTypeMask() & WindowInsets.Type.ime()) > 0) {
                        AlertController.this.mDialogAnimHelper.cancelAnimator();
                    }
                    AlertController.this.mInsetsAnimationPlayed = false;
                    this.isTablet = AlertController.this.isTablet();
                }

                @Override // android.view.WindowInsetsAnimation.Callback
                public WindowInsetsAnimation.Bounds onStart(WindowInsetsAnimation windowInsetsAnimation, WindowInsetsAnimation.Bounds bounds) {
                    AlertController alertController = AlertController.this;
                    alertController.mPanelAndImeMargin = (int) (alertController.getDialogPanelMargin() + AlertController.this.mParentPanel.getTranslationY());
                    if (AlertController.this.mIsDebugEnabled) {
                        Log.d(AlertController.TAG, "WindowInsetsAnimation onStart mPanelAndImeMargin : " + AlertController.this.mPanelAndImeMargin);
                    }
                    if (AlertController.this.mPanelAndImeMargin <= 0) {
                        AlertController.this.mPanelAndImeMargin = 0;
                    }
                    return super.onStart(windowInsetsAnimation, bounds);
                }

                @Override // android.view.WindowInsetsAnimation.Callback
                public WindowInsets onProgress(WindowInsets windowInsets, List<WindowInsetsAnimation> list) {
                    Insets insets = windowInsets.getInsets(WindowInsets.Type.ime());
                    Insets insets2 = windowInsets.getInsets(WindowInsets.Type.navigationBars());
                    int iMax = insets.bottom - Math.max(AlertController.this.mPanelAndImeMargin, insets2.bottom);
                    if (windowInsets.isVisible(WindowInsets.Type.ime())) {
                        if (AlertController.this.mIsDebugEnabled) {
                            Log.d(AlertController.TAG, "WindowInsetsAnimation onProgress mPanelAndImeMargin : " + AlertController.this.mPanelAndImeMargin);
                            Log.d(AlertController.TAG, "WindowInsetsAnimation onProgress ime : " + insets.bottom);
                            Log.d(AlertController.TAG, "WindowInsetsAnimation onProgress navigationBar : " + insets2.bottom);
                        }
                        AlertController.this.translateDialogPanel(-(iMax < 0 ? 0 : iMax));
                    }
                    if (!this.isTablet) {
                        AlertController.this.updateDimBgBottomMargin(iMax);
                    }
                    return windowInsets;
                }

                @Override // android.view.WindowInsetsAnimation.Callback
                public void onEnd(WindowInsetsAnimation windowInsetsAnimation) {
                    super.onEnd(windowInsetsAnimation);
                    AlertController.this.mInsetsAnimationPlayed = true;
                    WindowInsets rootWindowInsets = AlertController.this.mWindow.getDecorView().getRootWindowInsets();
                    if (rootWindowInsets != null) {
                        Insets insets = rootWindowInsets.getInsets(WindowInsets.Type.ime());
                        if (insets.bottom <= 0 && AlertController.this.mParentPanel.getTranslationY() < 0.0f) {
                            AlertController.this.translateDialogPanel(0);
                        }
                        AlertController.this.updateParentPanelMarginByWindowInsets(rootWindowInsets);
                        if (this.isTablet) {
                            return;
                        }
                        AlertController.this.updateDimBgBottomMargin(insets.bottom);
                    }
                }
            };
            if (!(this.mDialog instanceof PairingDialog)) {
                this.mWindow.getDecorView().setWindowInsetsAnimationCallback(callback);
            }
            this.mWindow.getDecorView().setOnApplyWindowInsetsListener(new AnonymousClass9());
            this.mSetupWindowInsetsAnimation = true;
        }
    }

    /* JADX INFO: renamed from: miuix.appcompat.app.AlertController$9, reason: invalid class name */
    class AnonymousClass9 implements View.OnApplyWindowInsetsListener {
        AnonymousClass9() {
        }

        @Override // android.view.View.OnApplyWindowInsetsListener
        public WindowInsets onApplyWindowInsets(final View view, WindowInsets windowInsets) {
            if (AlertController.this.mIsDebugEnabled) {
                Log.d(AlertController.TAG, "onApplyWindowInsets insets " + windowInsets);
            }
            AlertController alertController = AlertController.this;
            alertController.mPanelAndImeMargin = (int) (alertController.getDialogPanelMargin() + AlertController.this.mParentPanel.getTranslationY());
            if (view != null && windowInsets != null) {
                if (AlertController.this.mLayoutChangeListener != null) {
                    AlertController.this.mLayoutChangeListener.updateLayout(view);
                }
                AlertController.this.updateDialogPanelByWindowInsets(windowInsets);
                view.post(new Runnable() { // from class: miuix.appcompat.app.AlertController$9$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1800x47a934f2(view);
                    }
                });
            }
            return WindowInsets.CONSUMED;
        }

        /* JADX INFO: renamed from: lambda$onApplyWindowInsets$0$miuix-appcompat-app-AlertController$9, reason: not valid java name */
        /* synthetic */ void m1800x47a934f2(View view) {
            WindowInsets rootWindowInsets = view.getRootWindowInsets();
            if (rootWindowInsets != null) {
                AlertController.this.updateDialogPanelByWindowInsets(rootWindowInsets);
            }
        }
    }

    private void cleanWindowInsetsAnimation() {
        if (this.mSetupWindowInsetsAnimation) {
            this.mWindow.getDecorView().setWindowInsetsAnimationCallback(null);
            this.mWindow.getDecorView().setOnApplyWindowInsetsListener(null);
            this.mSetupWindowInsetsAnimation = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDialogPanelMargin() {
        int[] iArr = new int[2];
        this.mParentPanel.getLocationInWindow(iArr);
        if (this.mExtraImeMargin == -1) {
            this.mExtraImeMargin = this.mDimensConfig.extraImeMargin;
        }
        int height = this.mWindow.getDecorView().getHeight();
        int height2 = this.mParentPanel.getHeight();
        int iMax = iArr[1];
        WindowInsets rootWindowInsets = this.mWindow.getDecorView().getRootWindowInsets();
        boolean zIsVisible = (rootWindowInsets == null || android.os.Build.VERSION.SDK_INT < 30) ? false : rootWindowInsets.isVisible(WindowInsets.Type.ime());
        if (isTablet() && isDialogImmersive() && zIsVisible && (iMax = Math.max(0, (height - height2) / 2)) == 0) {
            iMax = iArr[1];
        }
        return (height - (iMax + height2)) - this.mExtraImeMargin;
    }

    public boolean isChecked() {
        CheckBox checkBox = (CheckBox) this.mWindow.findViewById(android.R.id.checkbox);
        if (checkBox == null) {
            return false;
        }
        boolean zIsChecked = checkBox.isChecked();
        this.mIsChecked = zIsChecked;
        return zIsChecked;
    }

    public void setCheckBox(boolean z, CharSequence charSequence) {
        this.mIsChecked = z;
        this.mCheckBoxMessage = charSequence;
    }

    private void initScreenMinorSize(Context context) {
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        updateMinorScreenSize();
    }

    private void updateMinorScreenSize() {
        Configuration configuration = this.mContext.getResources().getConfiguration();
        int iMin = (int) (Math.min(configuration.screenWidthDp, configuration.screenHeightDp) * (configuration.densityDpi / 160.0f));
        if (iMin > 0) {
            this.mScreenMinorSize = iMin;
            return;
        }
        Point point = new Point();
        this.mWindowManager.getDefaultDisplay().getSize(point);
        this.mScreenMinorSize = Math.min(point.x, point.y);
    }

    private void setupCheckbox(ViewGroup viewGroup, ViewStub viewStub) {
        if (this.mCheckBoxMessage != null) {
            viewStub.inflate();
            CheckBox checkBox = (CheckBox) viewGroup.findViewById(android.R.id.checkbox);
            checkBox.setVisibility(0);
            checkBox.setChecked(this.mIsChecked);
            checkBox.setText(this.mCheckBoxMessage);
        }
        TextAlignLayout textAlignLayout = (TextAlignLayout) viewGroup.findViewById(R.id.textAlign);
        if (textAlignLayout != null) {
            textAlignLayout.setDialogPanelHasCheckbox(this.mCheckBoxMessage != null);
        }
    }

    @Deprecated
    private void setupCheckbox(CheckBox checkBox) {
        if (this.mCheckBoxMessage != null) {
            checkBox.setVisibility(0);
            checkBox.setChecked(this.mIsChecked);
            checkBox.setText(this.mCheckBoxMessage);
            return;
        }
        checkBox.setVisibility(8);
    }

    void setEnableImmersive(boolean z) {
        this.mIsEnableImmersive = z;
    }

    public void setCustomPanelSize(final ViewGroup.LayoutParams layoutParams) throws IllegalArgumentException {
        DialogParentPanel2 dialogParentPanel2 = this.mParentPanel;
        if (dialogParentPanel2 == null || layoutParams == null) {
            throw new IllegalArgumentException("mParentPanel or layoutParams is null");
        }
        dialogParentPanel2.post(new Runnable() { // from class: miuix.appcompat.app.AlertController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1795lambda$setCustomPanelSize$3$miuixappcompatappAlertController(layoutParams);
            }
        });
        if (isDialogImmersive()) {
            return;
        }
        WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
        attributes.width = layoutParams.width;
        attributes.height = layoutParams.height;
        this.mWindow.setAttributes(attributes);
    }

    /* JADX INFO: renamed from: lambda$setCustomPanelSize$3$miuix-appcompat-app-AlertController, reason: not valid java name */
    /* synthetic */ void m1795lambda$setCustomPanelSize$3$miuixappcompatappAlertController(ViewGroup.LayoutParams layoutParams) {
        this.mParentPanel.setLayoutParams(layoutParams);
    }

    void setLiteVersion(int i) {
        this.mLiteVersion = i;
    }

    boolean isDialogImmersive() {
        return this.mIsEnableImmersive && android.os.Build.VERSION.SDK_INT >= 30;
    }

    private Point getAvailableWindowSizeDp(WindowInsets windowInsets) {
        Point point = new Point();
        int i = this.mRootViewSizeDp.x;
        int i2 = this.mRootViewSizeDp.y;
        Rect rect = new Rect();
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            rect = getNaviBarInsets(windowInsets, true);
        }
        if (this.mIsFlipTinyScreen) {
            Rect guaranteedCutout = getGuaranteedCutout(windowInsets, true);
            i -= guaranteedCutout.left + guaranteedCutout.right;
            i2 -= guaranteedCutout.top + guaranteedCutout.bottom;
        } else if (android.os.Build.VERSION.SDK_INT >= 30 && !isDialogImmersive()) {
            Insets associatedActivityInsets = getAssociatedActivityInsets(WindowInsets.Type.displayCutout());
            if (associatedActivityInsets != null) {
                Rect rectPx2dp = px2dp(new Rect(associatedActivityInsets.left, associatedActivityInsets.top, associatedActivityInsets.right, associatedActivityInsets.bottom));
                i -= rectPx2dp.left + rectPx2dp.right;
                i2 -= rectPx2dp.top + rectPx2dp.bottom;
            }
            if (this.mIsDebugEnabled) {
                Log.d(TAG, "getAvailableWindowSizeDp: cutoutInsets = " + associatedActivityInsets);
            }
        }
        int i3 = i - (rect.left + rect.right);
        int i4 = i2 - (rect.top + rect.bottom);
        point.x = i3;
        point.y = i4;
        return point;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldUseLandscapePanel() {
        if (getVisibleButtonCount() == 0) {
            return false;
        }
        int i = this.mRootViewSize.x;
        return i >= this.mDimensConfig.panelMaxWidthLand && i * 2 > this.mRootViewSize.y && this.mPreferLandscape;
    }

    private int getVerticalAvoidSpace() {
        int i;
        int i2 = 0;
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            Insets associatedActivityInsets = getAssociatedActivityInsets(WindowInsets.Type.captionBar());
            int i3 = associatedActivityInsets != null ? associatedActivityInsets.top : 0;
            i = associatedActivityInsets != null ? associatedActivityInsets.bottom : 0;
            i2 = i3;
        } else {
            i = 0;
        }
        int i4 = this.mDimensConfig.freeTabletCompactHeight;
        int i5 = this.mDimensConfig.freePhoneCompactHeight;
        int i6 = this.mDimensConfig.extraImeMargin;
        if (i2 == 0) {
            i2 = isTablet() ? i4 : i5 + i6;
        }
        if (i == 0) {
            i = isTablet() ? i4 : i5 + i6;
        }
        return i2 + i;
    }

    private int getGravity() {
        return isTablet() ? 17 : 81;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isTablet() {
        return Build.IS_TABLET || this.mIsCarWithScreen;
    }

    private void updateImmersiveDialogPanel() {
        Point availableWindowSizeDp = getAvailableWindowSizeDp(null);
        updateDialogPanelLayoutParams(availableWindowSizeDp);
        int iDp2px = this.mPanelParamsWidth;
        if (iDp2px == -1) {
            iDp2px = MiuixUIUtils.dp2px(this.mContext, availableWindowSizeDp.x) - (this.mPanelParamsHorizontalMargin * 2);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(iDp2px, -2);
        layoutParams.gravity = getGravity();
        if (this.mPanelParamsWidth == -1) {
            layoutParams.leftMargin = this.mPanelParamsHorizontalMargin;
            layoutParams.rightMargin = this.mPanelParamsHorizontalMargin;
        }
        this.mParentPanel.setLayoutParams(layoutParams);
    }

    private void updateDialogPanelLayoutParams(Point point) {
        Point availableWindowSizeDp = point == null ? getAvailableWindowSizeDp(null) : point;
        DialogContract.OrientationSpec orientationSpec = new DialogContract.OrientationSpec();
        orientationSpec.mUsableWindowSizeDp.set(availableWindowSizeDp.x, availableWindowSizeDp.y);
        orientationSpec.mWindowSize.set(this.mRootViewSize.x, this.mRootViewSize.y);
        WindowUtils.getScreenSize(this.mContext, orientationSpec.mRealScreenSize);
        orientationSpec.updateData(this.mMarkLandscape, this.mIsInFreeForm, getScreenOrientation(), this.mIsCarWithScreen, this.mIsSynergy);
        boolean zIsLandscapeWindow = this.mDisplayStrategy.isLandscapeWindow(orientationSpec);
        int i = availableWindowSizeDp.x;
        boolean zShouldLimitPanelWidth = this.mDisplayStrategy.shouldLimitPanelWidth(i);
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "updateDialogPanelLayoutParams isLandScape " + zIsLandscapeWindow);
            Log.d(TAG, "updateDialogPanelLayoutParams shouldLimitWidth " + zShouldLimitPanelWidth);
        }
        boolean z = false;
        int widthMargin = zShouldLimitPanelWidth ? 0 : this.mDisplayStrategy.getWidthMargin(this.mDimensConfig, i);
        this.mIsWindowLandScape = zIsLandscapeWindow;
        DialogContract.PanelWidthSpec panelWidthSpec = new DialogContract.PanelWidthSpec();
        if (this.mPreferLandscape && shouldUseLandscapePanel()) {
            z = true;
        }
        panelWidthSpec.updateData(z, zIsLandscapeWindow, this.mIsCarWithScreen, this.mMarkLandscape, i, this.mScreenMinorSize, this.mIsDebugEnabled);
        this.mPanelParamsWidth = this.mDisplayStrategy.getPanelWidth(panelWidthSpec, this.mDimensConfig);
        inflateDialogLayout();
        this.mPanelParamsHorizontalMargin = widthMargin;
    }

    private void inflateDialogLayout() {
        this.mLandscapePanel = false;
        int i = R.layout.miuix_appcompat_alert_dialog_content;
        if (this.mPreferLandscape && shouldUseLandscapePanel()) {
            i = R.layout.miuix_appcompat_alert_dialog_content_land;
            this.mLandscapePanel = true;
        }
        if (this.mDialogContentLayout != i) {
            this.mDialogContentLayout = i;
            DialogParentPanel2 dialogParentPanel2 = this.mParentPanel;
            if (dialogParentPanel2 != null) {
                this.mDialogRootView.removeView(dialogParentPanel2);
            }
            if (this.mAsyncInflatePanelEnabled) {
                this.mParentPanel = (DialogParentPanel2) AsyncInflateLayoutManager.getInstance().inflateViewById(Integer.valueOf(this.mDialogContentLayout), this.mContext, this.mDialogRootView, false);
                if (this.mIsDebugEnabled) {
                    Log.w(TAG, "inflateDialogLayout: parentPanel.getParent = " + this.mParentPanel.getParent());
                    Log.w(TAG, "inflateDialogLayout: mParentPanel.getTag = " + this.mParentPanel.getTag());
                }
                DialogParentPanel2 dialogParentPanel3 = this.mParentPanel;
                if (dialogParentPanel3 != null && dialogParentPanel3.getParent() != null) {
                    this.mParentPanel = (DialogParentPanel2) LayoutInflater.from(this.mContext).inflate(this.mDialogContentLayout, (ViewGroup) this.mDialogRootView, false);
                }
            } else {
                this.mParentPanel = (DialogParentPanel2) LayoutInflater.from(this.mContext).inflate(this.mDialogContentLayout, (ViewGroup) this.mDialogRootView, false);
            }
            if (this.mPanelFixedSizeEnabled) {
                this.mParentPanel.setPanelFixedWidth(this.mPanelFixedWidth);
                this.mParentPanel.setPanelFixedHeight(this.mPanelFixedHeight);
            }
            this.mParentPanel.setIsInTinyScreen(this.mIsFlipTinyScreen);
            this.mParentPanel.setIsDebugEnabled(this.mIsDebugEnabled);
            this.mParentPanel.setPanelMaxLimitHeight(getPanelMaxLimitHeight(null));
            this.mDialogRootView.addView(this.mParentPanel);
        }
    }

    public void safeRemovePanelFromParent() {
        if (this.mParentPanel == null) {
            return;
        }
        if (isAsyncInflatePanelEnabled()) {
            this.mParentPanel.setTag(null);
        }
        ViewGroup viewGroup = (ViewGroup) this.mParentPanel.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.mParentPanel);
        }
    }

    public boolean isAsyncInflatePanelEnabled() {
        return this.mAsyncInflatePanelEnabled;
    }

    private int getPanelMaxLimitHeight(Rect rect) {
        int i;
        int i2;
        int i3 = EnvStateManager.getWindowSize(this.mContext).y;
        int i4 = this.mIsFlipTinyScreen ? this.mDimensConfig.widthSmallMargin : this.mDimensConfig.extraImeMargin;
        if (rect != null) {
            i = rect.top;
            i2 = rect.bottom;
        } else if (android.os.Build.VERSION.SDK_INT >= 30) {
            Insets associatedActivityInsets = getAssociatedActivityInsets(WindowInsets.Type.systemBars());
            int i5 = associatedActivityInsets != null ? associatedActivityInsets.top : 0;
            i2 = associatedActivityInsets != null ? associatedActivityInsets.bottom : 0;
            i = i5;
        } else {
            i = 0;
            i2 = 0;
        }
        int iMax = (i3 - Math.max(i, i4)) - (i2 + i4);
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "getPanelMaxLimitHeight: boundInset = " + rect + ", topInset = " + i + ", bottomInset = " + i2 + ", windowHeight = " + i3 + ", verticalMargin = " + i4 + ", panelMaxLimitHeight = " + iMax);
        }
        if (this.mIsInFreeForm) {
            iMax = i3 - getFreeFormAvoidSpace(rect);
        }
        return this.mIsFlipTinyScreen ? i3 - (Math.max(i, EnvStateManager.getStatusBarHeight(this.mContext, false)) + i4) : iMax;
    }

    private int getFreeFormAvoidSpace(Rect rect) {
        int i;
        int i2;
        if (rect != null) {
            i2 = rect.top;
            i = rect.bottom;
        } else {
            i = 0;
            i2 = 0;
        }
        boolean z = i2 == 0 || i == 0;
        if (android.os.Build.VERSION.SDK_INT >= 30 && z) {
            Insets associatedActivityInsets = getAssociatedActivityInsets(WindowInsets.Type.captionBar());
            i2 = associatedActivityInsets != null ? associatedActivityInsets.top : 0;
            i = associatedActivityInsets != null ? associatedActivityInsets.bottom : 0;
        }
        if (i2 == 0) {
            i2 = isTablet() ? this.mDimensConfig.freeTabletCompactHeight : this.mDimensConfig.freePhoneCompactHeight + this.mDimensConfig.extraImeMargin;
        }
        if (i == 0) {
            i = isTablet() ? this.mDimensConfig.freeTabletCompactHeight : this.mDimensConfig.freePhoneCompactHeight + this.mDimensConfig.extraImeMargin;
        }
        return i2 + i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateParentPanelMarginByWindowInsets(WindowInsets windowInsets) {
        Insets insets;
        int i;
        if (isTablet() || windowInsets == null) {
            return;
        }
        if (this.mNavigationBarHiddenEnabled || this.mIsAssociatedActivityHideNavigationBar) {
            insets = windowInsets.getInsets(WindowInsets.Type.systemBars());
        } else {
            insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
        }
        Rect rectInsetsToRect = DialogContract.insetsToRect(insets);
        boolean z = false;
        if (this.mDiscardNaviBarHeightEnabled) {
            rectInsetsToRect.bottom = 0;
        }
        Insets insets2 = windowInsets.getInsets(WindowInsets.Type.displayCutout());
        this.mDisplayCutoutSafeInsets.setEmpty();
        int width = this.mDialogRootView.getWidth();
        int height = this.mDialogRootView.getHeight();
        if (this.mRootViewSize.x == 0 || this.mRootViewSize.y == 0) {
            updateRootViewSize((Configuration) null);
            width = this.mRootViewSize.x;
            height = this.mRootViewSize.y;
        }
        int i2 = width;
        if (insets2 != null && !this.mIsInFreeForm) {
            this.mDisplayCutoutSafeInsets.set(insets2.left, insets2.top, insets2.right, insets2.bottom);
        }
        if (this.mIsFlipTinyScreen) {
            Rect guaranteedCutout = getGuaranteedCutout(windowInsets, false);
            this.mDisplayCutoutSafeInsets.set(guaranteedCutout.left, guaranteedCutout.top, guaranteedCutout.right, guaranteedCutout.bottom);
        }
        Rect rectMergeInsets = DialogContract.mergeInsets(rectInsetsToRect, this.mDisplayCutoutSafeInsets);
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "updateParentPanelMargin systemBarInsets: " + insets);
            Log.d(TAG, "updateParentPanelMargin mDisplayCutoutSafeInsets: " + this.mDisplayCutoutSafeInsets);
            Log.d(TAG, "updateParentPanelMargin boundInsets = " + rectMergeInsets);
        }
        Point point = new Point(this.mRootViewSize.x, this.mRootViewSize.y);
        if (i2 != 0 && i2 != point.x) {
            point.x = i2;
            point.y = height;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mParentPanel.getLayoutParams();
        DialogContract.OrientationSpec orientationSpec = new DialogContract.OrientationSpec();
        Point windowSize = EnvStateManager.getWindowSize(this.mContext);
        orientationSpec.updateData(this.mMarkLandscape, this.mIsInFreeForm, getScreenOrientation(), this.mIsCarWithScreen, this.mIsSynergy);
        orientationSpec.mWindowSize.set(windowSize.x, windowSize.y);
        WindowUtils.getScreenSize(this.mContext, orientationSpec.mRealScreenSize);
        float f = this.mContext.getResources().getDisplayMetrics().densityDpi / 160.0f;
        int i3 = (point.x - rectMergeInsets.left) - rectMergeInsets.right;
        int i4 = (point.y - rectMergeInsets.top) - rectMergeInsets.bottom;
        int iPx2dp = MiuixUIUtils.px2dp(f, i3);
        orientationSpec.mUsableWindowSizeDp.set(iPx2dp, MiuixUIUtils.px2dp(f, i4));
        boolean zIsLandscapeWindow = this.mDisplayStrategy.isLandscapeWindow(orientationSpec);
        DialogContract.PanelWidthSpec panelWidthSpec = new DialogContract.PanelWidthSpec();
        boolean z2 = true;
        panelWidthSpec.updateData(this.mPreferLandscape && shouldUseLandscapePanel(), zIsLandscapeWindow, this.mIsCarWithScreen, this.mMarkLandscape, iPx2dp, this.mScreenMinorSize, this.mIsDebugEnabled);
        int panelWidth = this.mDisplayStrategy.getPanelWidth(panelWidthSpec, this.mDimensConfig);
        DialogContract.PanelPosSpec panelPosSpec = new DialogContract.PanelPosSpec();
        panelPosSpec.mBoundInsets.set(rectMergeInsets.left, rectMergeInsets.top, rectMergeInsets.right, rectMergeInsets.bottom);
        panelPosSpec.updateData(this.mDialogRootView.getPaddingLeft(), this.mDialogRootView.getPaddingRight(), i2, panelWidth, iPx2dp, i3, this.mRootViewSize.x, this.mIsFlipTinyScreen, this.mIsDebugEnabled);
        Rect rect = new Rect();
        int iUpdatePanelPosMargins = this.mDisplayStrategy.updatePanelPosMargins(panelPosSpec, this.mDimensConfig, rect);
        layoutParams.width = iUpdatePanelPosMargins;
        int i5 = rect.bottom;
        boolean z3 = MiuixUIUtils.isInMultiWindowMode(this.mContext) && !this.mIsInFreeForm && DeviceHelper.isWideScreen(this.mContext);
        if ((this.mIsInFreeForm || z3) && insets.bottom == 0) {
            Insets associatedActivityInsets = getAssociatedActivityInsets(WindowInsets.Type.captionBar());
            int i6 = this.mDimensConfig.freePhoneCompactHeight;
            int i7 = associatedActivityInsets != null ? associatedActivityInsets.bottom : 0;
            int i8 = i7 == 0 ? i6 + i5 : i5 + i7;
            if (getImeBottomByWindowInsets(windowInsets) <= 0) {
                i5 = i8;
            }
            if (this.mIsInFreeForm && this.mDiscardNaviBarHeightEnabled) {
                i5 = rect.bottom;
            }
        } else {
            if (!this.mIsFlipTinyScreen || this.mDisplayCutoutSafeInsets.bottom <= 0) {
                i = this.mIsFlipTinyScreen ? 0 : rectMergeInsets.bottom;
            } else {
                i = this.mDisplayCutoutSafeInsets.bottom;
            }
            i5 += i;
        }
        if (layoutParams.topMargin != rect.top) {
            layoutParams.topMargin = rect.top;
            z = true;
        }
        if (layoutParams.bottomMargin != i5) {
            layoutParams.bottomMargin = i5;
            z = true;
        }
        if (layoutParams.leftMargin != rect.left) {
            layoutParams.leftMargin = rect.left;
            z = true;
        }
        if (layoutParams.rightMargin != rect.right) {
            layoutParams.rightMargin = rect.right;
            z = true;
        }
        if (panelWidth != iUpdatePanelPosMargins) {
            z = true;
        }
        int panelMaxLimitHeight = getPanelMaxLimitHeight(rectMergeInsets);
        if (panelMaxLimitHeight != this.mParentPanel.getPanelMaxLimitHeight()) {
            this.mParentPanel.setPanelMaxLimitHeight(panelMaxLimitHeight);
        } else {
            z2 = z;
        }
        if (z2) {
            this.mParentPanel.requestLayout();
        }
    }

    private Rect getGuaranteedCutout(WindowInsets windowInsets, boolean z) {
        Rect rect = new Rect();
        if (android.os.Build.VERSION.SDK_INT < 30) {
            return rect;
        }
        if (windowInsets != null) {
            Insets insets = windowInsets.getInsets(WindowInsets.Type.displayCutout());
            rect.set(insets.left, insets.top, insets.right, insets.bottom);
            if (!z) {
                return rect;
            }
            px2dp(rect);
            return rect;
        }
        return getDisplayCutout(z);
    }

    private void printDebugMsg(String str, String str2, boolean z) {
        if (this.mIsDebugEnabled || z) {
            Log.d(TAG, str + ": " + str2);
        }
    }

    private Rect getDisplayCutout(boolean z) {
        Rect rect = new Rect();
        Insets associatedActivityInsets = getAssociatedActivityInsets(WindowInsets.Type.displayCutout());
        if (associatedActivityInsets != null) {
            rect.set(associatedActivityInsets.left, associatedActivityInsets.top, associatedActivityInsets.right, associatedActivityInsets.bottom);
            printDebugMsg("getDisplayCutout", "get cutout from host, cutout = " + rect.flattenToString(), false);
        } else {
            DisplayCutout cutoutSafely = getCutoutSafely();
            rect.left = cutoutSafely != null ? cutoutSafely.getSafeInsetLeft() : 0;
            rect.top = cutoutSafely != null ? cutoutSafely.getSafeInsetTop() : 0;
            rect.right = cutoutSafely != null ? cutoutSafely.getSafeInsetRight() : 0;
            rect.bottom = cutoutSafely != null ? cutoutSafely.getSafeInsetBottom() : 0;
        }
        return z ? px2dp(rect) : rect;
    }

    private Rect getNaviBarInsets(WindowInsets windowInsets, boolean z) {
        Rect rect = new Rect();
        if (windowInsets == null) {
            windowInsets = this.mDialogRootView.getRootWindowInsets();
        }
        if (windowInsets != null) {
            Insets insetsIgnoringVisibility = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars());
            rect.set(insetsIgnoringVisibility.left, insetsIgnoringVisibility.top, insetsIgnoringVisibility.right, insetsIgnoringVisibility.bottom);
            return z ? px2dp(rect) : rect;
        }
        Insets associatedActivityInsets = getAssociatedActivityInsets(WindowInsets.Type.navigationBars());
        if (associatedActivityInsets != null) {
            rect.set(associatedActivityInsets.left, associatedActivityInsets.top, associatedActivityInsets.right, associatedActivityInsets.bottom);
            return z ? px2dp(rect) : rect;
        }
        int navigationBarHeight = EnvStateManager.getNavigationBarHeight(this.mContext, z);
        int rotationSafely = getRotationSafely();
        if (rotationSafely == 1) {
            rect.right = navigationBarHeight;
        } else if (rotationSafely == 2) {
            rect.top = navigationBarHeight;
        } else if (rotationSafely == 3) {
            rect.left = navigationBarHeight;
        } else {
            rect.bottom = navigationBarHeight;
        }
        return rect;
    }

    private int getRotationSafely() {
        try {
            return this.mContext.getDisplay().getRotation();
        } catch (Exception e) {
            Log.e(TAG, "context is not associated display info, please check the type of context, the exception info = " + Log.getStackTraceString(e));
            WindowManager windowManager = this.mWindowManager;
            Display defaultDisplay = windowManager != null ? windowManager.getDefaultDisplay() : null;
            if (defaultDisplay != null) {
                return defaultDisplay.getRotation();
            }
            return 0;
        }
    }

    private void getFlipCutout() {
        Display defaultDisplay;
        if (this.mIsFlipTinyScreen) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= 30) {
                    defaultDisplay = this.mContext.getDisplay();
                } else {
                    WindowManager windowManager = this.mWindowManager;
                    defaultDisplay = windowManager != null ? windowManager.getDefaultDisplay() : null;
                }
                if (defaultDisplay != null && android.os.Build.VERSION.SDK_INT >= 28) {
                    this.mFlipCutout = (DisplayCutout) ReflectionHelper.getMethod(defaultDisplay.getClass(), "getFlipFoldedCutout", new Class[0]).invoke(defaultDisplay, new Object[0]);
                } else {
                    this.mFlipCutout = null;
                }
            } catch (Exception unused) {
                printDebugMsg("getFlipCutout", "can't reflect from display to query cutout", false);
                this.mFlipCutout = null;
            }
        }
    }

    private boolean showSystemAlertInFlip() {
        int i = this.mWindow.getAttributes().type;
        boolean z = i == 2038 || i == 2003;
        if (this.mIsFlipTinyScreen) {
            return z || this.mUseForceFlipCutout;
        }
        return false;
    }

    private DisplayCutout getCutoutSafely() {
        if (showSystemAlertInFlip() && this.mFlipCutout != null) {
            printDebugMsg("getCutoutSafely", "show system alert in flip, use displayCutout by reflect, displayCutout = " + this.mFlipCutout, false);
            return this.mFlipCutout;
        }
        try {
            DisplayCutout cutout = this.mContext.getDisplay().getCutout();
            printDebugMsg("getCutoutSafely", "get displayCutout from context = " + cutout, false);
            return cutout;
        } catch (Exception e) {
            Log.e(TAG, "context is not associated display info, please check the type of context, the exception info = " + Log.getStackTraceString(e));
            WindowManager windowManager = this.mWindowManager;
            Display defaultDisplay = windowManager != null ? windowManager.getDefaultDisplay() : null;
            if (defaultDisplay != null) {
                return defaultDisplay.getCutout();
            }
            return null;
        }
    }

    private Rect px2dp(Rect rect) {
        float f = this.mContext.getResources().getDisplayMetrics().densityDpi / 160.0f;
        rect.left = MiuixUIUtils.px2dp(f, rect.left);
        rect.top = MiuixUIUtils.px2dp(f, rect.top);
        rect.right = MiuixUIUtils.px2dp(f, rect.right);
        rect.bottom = MiuixUIUtils.px2dp(f, rect.bottom);
        return rect;
    }

    private int getImeBottomByWindowInsets(WindowInsets windowInsets) {
        if (windowInsets == null) {
            windowInsets = this.mWindow.getDecorView().getRootWindowInsets();
        }
        if (windowInsets != null) {
            Insets insets = windowInsets.getInsets(WindowInsets.Type.ime());
            if (insets != null) {
                return insets.bottom;
            }
            return 0;
        }
        Insets associatedActivityInsets = getAssociatedActivityInsets(WindowInsets.Type.ime());
        if (associatedActivityInsets != null) {
            return associatedActivityInsets.bottom;
        }
        return 0;
    }

    private Insets getAssociatedActivityInsets(int i) {
        WindowInsets rootWindowInsets;
        Activity associatedActivity = ((AlertDialog) this.mDialog).getAssociatedActivity();
        if (associatedActivity == null || android.os.Build.VERSION.SDK_INT < 30 || (rootWindowInsets = associatedActivity.getWindow().getDecorView().getRootWindowInsets()) == null) {
            return null;
        }
        return rootWindowInsets.getInsets(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDimBgBottomMargin(int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mDimBg.getLayoutParams();
        if (marginLayoutParams.bottomMargin != i) {
            marginLayoutParams.bottomMargin = i;
            this.mDimBg.requestLayout();
        }
    }

    private void setupWindow() {
        if (isDialogImmersive()) {
            setupImmersiveWindow();
        } else {
            setupNonImmersiveWindow();
        }
    }

    private void setupImmersiveWindow() {
        this.mWindow.setLayout(-1, -1);
        this.mWindow.setBackgroundDrawableResource(R.color.miuix_appcompat_transparent);
        this.mWindow.setDimAmount(0.0f);
        this.mWindow.setWindowAnimations(R.style.Animation_Dialog_NoAnimation);
        this.mWindow.addFlags(-2147481344);
        if (android.os.Build.VERSION.SDK_INT > 28) {
            Activity associatedActivity = ((AlertDialog) this.mDialog).getAssociatedActivity();
            if (associatedActivity != null) {
                this.mWindow.getAttributes().layoutInDisplayCutoutMode = getCutoutMode(getScreenOrientation(), associatedActivity.getWindow().getAttributes().layoutInDisplayCutoutMode);
            } else {
                this.mWindow.getAttributes().layoutInDisplayCutoutMode = getScreenOrientation() != 2 ? 1 : 2;
            }
        }
        clearFitSystemWindow(this.mWindow.getDecorView());
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            this.mWindow.getAttributes().setFitInsetsSides(0);
            Activity associatedActivity2 = ((AlertDialog) this.mDialog).getAssociatedActivity();
            boolean associatedActivitySystemBarVisibility = getAssociatedActivitySystemBarVisibility(associatedActivity2, WindowInsets.Type.statusBars());
            boolean z = (associatedActivity2 == null || (associatedActivity2.getWindow().getAttributes().flags & 1024) == 1024) ? false : true;
            if (z && (associatedActivitySystemBarVisibility || this.mIsInFreeForm)) {
                this.mWindow.clearFlags(1024);
            }
            boolean associatedActivitySystemBarVisibility2 = getAssociatedActivitySystemBarVisibility(associatedActivity2, WindowInsets.Type.navigationBars());
            if (this.mIsDebugEnabled) {
                Log.i(TAG, "setupImmersiveWindow: statusBarIsVisible = " + associatedActivitySystemBarVisibility + ", windowExcludeFullScreenFlag = " + z + ", navigationBarIsVisible = " + associatedActivitySystemBarVisibility2);
            }
            if (associatedActivitySystemBarVisibility2 || this.mIsInFreeForm) {
                return;
            }
            setWindowNavigationBarHidden();
        }
    }

    private void setWindowNavigationBarHidden() {
        View decorView = this.mWindow.getDecorView();
        if (decorView != null) {
            decorView.setSystemUiVisibility(4098);
            this.mIsAssociatedActivityHideNavigationBar = true;
        }
    }

    private boolean getAssociatedActivitySystemBarVisibility(Activity activity, int i) {
        if (activity != null && activity.getWindow() != null && android.os.Build.VERSION.SDK_INT >= 30) {
            View decorView = activity.getWindow().getDecorView();
            WindowInsets rootWindowInsets = decorView != null ? decorView.getRootWindowInsets() : null;
            if (rootWindowInsets != null) {
                return rootWindowInsets.isVisible(i);
            }
        }
        return true;
    }

    private void setupNonImmersiveWindow() {
        Point availableWindowSizeDp = getAvailableWindowSizeDp(null);
        updateDialogPanelLayoutParams(availableWindowSizeDp);
        int iDp2px = this.mPanelParamsWidth;
        if (iDp2px == -1) {
            iDp2px = MiuixUIUtils.dp2px(this.mContext, availableWindowSizeDp.x) - (this.mPanelParamsHorizontalMargin * 2);
        }
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "setupNonImmersiveWindow: windowWidth = " + iDp2px);
            Log.d(TAG, "setupNonImmersiveWindow: availableWindowSizeDp = " + availableWindowSizeDp);
            Log.d(TAG, "setupNonImmersiveWindow: horizontalMargin = " + this.mPanelParamsHorizontalMargin);
        }
        int i = this.mNonImmersiveDialogHeight;
        int i2 = (i <= 0 || i < this.mRootViewSize.y) ? i : -1;
        int gravity = getGravity();
        this.mWindow.setGravity(gravity);
        WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
        if ((gravity & 80) == 80) {
            int i3 = this.mIsFlipTinyScreen ? this.mDimensConfig.widthSmallMargin : this.mDimensConfig.extraImeMargin;
            boolean zIsShowNavigationHandle = MiuixUIUtils.isShowNavigationHandle(this.mContext);
            boolean z = MiuixUIUtils.isInMultiWindowMode(this.mContext) && !this.mIsInFreeForm && DeviceHelper.isWideScreen(this.mContext);
            if ((this.mIsInFreeForm || (z && zIsShowNavigationHandle)) && android.os.Build.VERSION.SDK_INT >= 30) {
                Insets associatedActivityInsets = getAssociatedActivityInsets(WindowInsets.Type.captionBar());
                int i4 = this.mDimensConfig.freePhoneCompactHeight;
                int i5 = associatedActivityInsets != null ? associatedActivityInsets.bottom : 0;
                i3 = i5 == 0 ? i3 + i4 : i3 + i5;
            }
            int i6 = attributes.flags;
            if ((i6 & 134217728) != 0) {
                this.mWindow.clearFlags(134217728);
            }
            if ((i6 & AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL) != 0) {
                this.mWindow.clearFlags(AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL);
            }
            attributes.verticalMargin = (i3 * 1.0f) / this.mRootViewSize.y;
        }
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            attributes.layoutInDisplayCutoutMode = 2;
        }
        this.mWindow.setAttributes(attributes);
        this.mWindow.addFlags(2);
        this.mWindow.addFlags(262144);
        this.mWindow.setDimAmount(ViewUtils.isNightMode(this.mContext) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT);
        this.mWindow.setLayout(iDp2px, i2);
        this.mWindow.setBackgroundDrawableResource(R.color.miuix_appcompat_transparent);
        DialogParentPanel2 dialogParentPanel2 = this.mParentPanel;
        if (dialogParentPanel2 != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) dialogParentPanel2.getLayoutParams();
            layoutParams.width = iDp2px;
            layoutParams.height = -2;
            if (isSpecifiedDialogHeight()) {
                layoutParams.gravity = getGravity();
            }
            this.mParentPanel.setLayoutParams(layoutParams);
            this.mParentPanel.setTag(null);
        }
        if (this.mEnableEnterAnim) {
            if (isTablet()) {
                this.mWindow.setWindowAnimations(R.style.Animation_Dialog_Center);
                return;
            }
            return;
        }
        this.mWindow.setWindowAnimations(0);
    }

    private void clearFitSystemWindow(View view) {
        if ((view instanceof DialogParentPanel2) || view == null) {
            return;
        }
        int i = 0;
        view.setFitsSystemWindows(false);
        if (!(view instanceof ViewGroup)) {
            return;
        }
        while (true) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (i >= viewGroup.getChildCount()) {
                return;
            }
            clearFitSystemWindow(viewGroup.getChildAt(i));
            i++;
        }
    }

    private void reInitLandConfig() {
        this.mMarkLandscape = this.mContext.getResources().getBoolean(R.bool.treat_as_land);
        updateMinorScreenSize();
    }

    private void updateRootViewSize(Configuration configuration) {
        WindowBaseInfo windowInfo;
        if (this.mIsFlipTinyScreen) {
            windowInfo = EnvStateManager.getWindowInfo(this.mContext);
        } else {
            windowInfo = EnvStateManager.getWindowInfo(this.mContext, configuration);
        }
        this.mRootViewSizeDp.x = windowInfo.windowSizeDp.x;
        this.mRootViewSizeDp.y = windowInfo.windowSizeDp.y;
        this.mRootViewSize.x = windowInfo.windowSize.x;
        this.mRootViewSize.y = windowInfo.windowSize.y;
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "updateRootViewSize mRootViewSizeDp " + this.mRootViewSizeDp + " mRootViewSize " + this.mRootViewSize);
            if (configuration != null) {
                Log.d(TAG, "configuration.density " + (configuration.densityDpi / 160.0f));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRootViewSize(View view) {
        this.mRootViewSize.x = view.getWidth();
        this.mRootViewSize.y = view.getHeight();
        float f = this.mContext.getResources().getDisplayMetrics().density;
        this.mRootViewSizeDp.x = (int) (this.mRootViewSize.x / f);
        this.mRootViewSizeDp.y = (int) (this.mRootViewSize.y / f);
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "updateRootViewSize by view mRootViewSizeDp " + this.mRootViewSizeDp + " mRootViewSize " + this.mRootViewSize + " configuration.density " + f);
        }
    }

    private boolean isConfigurationChanged(Configuration configuration) {
        return (this.configurationAfterInstalled.uiMode != configuration.uiMode) || (this.configurationAfterInstalled.screenLayout != configuration.screenLayout) || (this.configurationAfterInstalled.orientation != configuration.orientation) || (this.configurationAfterInstalled.screenWidthDp != configuration.screenWidthDp) || (this.configurationAfterInstalled.screenHeightDp != configuration.screenHeightDp) || ((this.configurationAfterInstalled.fontScale > configuration.fontScale ? 1 : (this.configurationAfterInstalled.fontScale == configuration.fontScale ? 0 : -1)) != 0) || (this.configurationAfterInstalled.smallestScreenWidthDp != configuration.smallestScreenWidthDp) || (this.configurationAfterInstalled.keyboard != configuration.keyboard);
    }

    public void onConfigurationChanged(Configuration configuration, boolean z, boolean z2) {
        this.mIsFlipTinyScreen = Build.IS_FLIP && DeviceHelper.isTinyScreen(this.mContext);
        this.mIsInFreeForm = MiuixUIUtils.isFreeformMode(this.mContext);
        updateDimensConfig(this.mContext.getResources());
        getFlipCutout();
        updateDisplayInfo(this.mContext);
        float f = (configuration.densityDpi * 1.0f) / this.mCurrentDensityDpi;
        if (f != 1.0f) {
            this.mCurrentDensityDpi = configuration.densityDpi;
        }
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "onConfigurationChangednewDensityDpi " + this.mCurrentDensityDpi + " densityScale " + f);
        }
        if (!this.buildJustNow || isConfigurationChanged(configuration) || this.mIsFlipTinyScreen || z) {
            this.buildJustNow = false;
            this.mExtraImeMargin = -1;
            updateRootViewSize((Configuration) null);
            if (this.mIsDebugEnabled) {
                Log.d(TAG, "onConfigurationChanged mRootViewSize " + this.mRootViewSize);
            }
            if (!checkThread()) {
                Log.w(TAG, "dialog is created in thread:" + this.mCreateThread + ", but onConfigurationChanged is called from different thread:" + Thread.currentThread() + ", so this onConfigurationChanged call should be ignore");
                return;
            }
            if (isDialogImmersive()) {
                this.mWindow.getDecorView().removeOnLayoutChangeListener(this.mLayoutChangeListener);
            }
            if (this.mWindow.getDecorView().isAttachedToWindow()) {
                if (f != 1.0f) {
                    this.mDimensConfig.panelMaxWidth = this.mContext.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_max_width);
                    this.mDimensConfig.panelMaxWidthLand = this.mContext.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_max_width_land);
                }
                reInitLandConfig();
                if (isDialogImmersive()) {
                    updateWindowCutoutMode();
                } else {
                    setupNonImmersiveWindow();
                }
                this.mParentPanel.setIsInTinyScreen(this.mIsFlipTinyScreen);
                this.mParentPanel.setIsDebugEnabled(this.mIsDebugEnabled);
                setupView(false, z, z2, f);
                this.mParentPanel.notifyConfigurationChanged();
            }
            if (isDialogImmersive()) {
                this.mLayoutChangeListener.updateLayout(this.mWindow.getDecorView());
                this.mWindow.getDecorView().addOnLayoutChangeListener(this.mLayoutChangeListener);
                WindowInsets rootWindowInsets = this.mWindow.getDecorView().getRootWindowInsets();
                if (rootWindowInsets != null) {
                    updateDialogPanelByWindowInsets(rootWindowInsets);
                }
                this.mDialogRootView.post(new Runnable() { // from class: miuix.appcompat.app.AlertController.10
                    @Override // java.lang.Runnable
                    public void run() {
                        WindowInsets rootWindowInsets2 = AlertController.this.mWindow.getDecorView().getRootWindowInsets();
                        if (rootWindowInsets2 != null) {
                            AlertController.this.updateDialogPanelByWindowInsets(rootWindowInsets2);
                        }
                    }
                });
            }
            this.mParentPanel.setPanelMaxLimitHeight(getPanelMaxLimitHeight(null));
            setupMaterial();
            AlertDialog.OnConfigurationChangedListener onConfigurationChangedListener = this.mConfigurationChangedListener;
            if (onConfigurationChangedListener != null) {
                onConfigurationChangedListener.onConfigurationChanged(this.mDialog, this.mParentPanel, configuration);
            }
        }
    }

    private void onLayoutReload() {
        ((AlertDialog) this.mDialog).onLayoutReload();
        AlertDialog.OnDialogLayoutReloadListener onDialogLayoutReloadListener = this.mLayoutReloadListener;
        if (onDialogLayoutReloadListener != null) {
            onDialogLayoutReloadListener.onLayoutReload();
        }
    }

    private void updateWindowCutoutMode() {
        int screenOrientation = getScreenOrientation();
        if (android.os.Build.VERSION.SDK_INT <= 28 || this.mScreenOrientation == screenOrientation) {
            return;
        }
        this.mScreenOrientation = screenOrientation;
        Activity associatedActivity = ((AlertDialog) this.mDialog).getAssociatedActivity();
        if (associatedActivity != null) {
            int cutoutMode = getCutoutMode(screenOrientation, associatedActivity.getWindow().getAttributes().layoutInDisplayCutoutMode);
            if (this.mWindow.getAttributes().layoutInDisplayCutoutMode != cutoutMode) {
                this.mWindow.getAttributes().layoutInDisplayCutoutMode = cutoutMode;
                View decorView = this.mWindow.getDecorView();
                if (this.mDialog.isShowing() && decorView.isAttachedToWindow()) {
                    this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), this.mWindow.getAttributes());
                    return;
                }
                return;
            }
            return;
        }
        int i = getScreenOrientation() != 2 ? 1 : 2;
        if (this.mWindow.getAttributes().layoutInDisplayCutoutMode != i) {
            this.mWindow.getAttributes().layoutInDisplayCutoutMode = i;
            View decorView2 = this.mWindow.getDecorView();
            if (this.mDialog.isShowing() && decorView2.isAttachedToWindow()) {
                this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), this.mWindow.getAttributes());
            }
        }
    }

    private int getScreenOrientation() {
        WindowManager windowManager = this.mWindowManager;
        if (windowManager == null) {
            return 0;
        }
        int rotation = windowManager.getDefaultDisplay().getRotation();
        return (rotation == 1 || rotation == 3) ? 2 : 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLightTheme() {
        Context context = this.mContext;
        if (context == null) {
            return true;
        }
        boolean zIsNightMode = ViewUtils.isNightMode(context);
        TextView textView = this.mTitleView;
        if (textView != null) {
            return !MiuixUIUtils.isLightColor(textView.getCurrentTextColor());
        }
        TextView textView2 = this.mMessageView;
        if (textView2 != null) {
            return !MiuixUIUtils.isLightColor(textView2.getCurrentTextColor());
        }
        TextView textView3 = this.mCommentView;
        if (textView3 != null) {
            return !MiuixUIUtils.isLightColor(textView3.getCurrentTextColor());
        }
        TextView textView4 = this.mCustomTitleTextView;
        return textView4 != null ? !MiuixUIUtils.isLightColor(textView4.getCurrentTextColor()) : zIsNightMode;
    }

    public void setShowAnimListener(AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
        this.mShowAnimListener = onDialogShowAnimListener;
    }

    public void setLayoutReloadListener(AlertDialog.OnDialogLayoutReloadListener onDialogLayoutReloadListener) {
        this.mLayoutReloadListener = onDialogLayoutReloadListener;
    }

    public void setPanelSizeChangedListener(AlertDialog.OnPanelSizeChangedListener onPanelSizeChangedListener) {
        this.mPanelSizeChangedListener = onPanelSizeChangedListener;
    }

    public void setConfigurationChangedListener(AlertDialog.OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mConfigurationChangedListener = onConfigurationChangedListener;
    }

    public void onStart() {
        if (isDialogImmersive()) {
            if (this.mDimBg != null) {
                updateDimBgBottomMargin(0);
            }
            reInitLandConfig();
            updateWindowCutoutMode();
            if (!this.mIsFromRebuild && this.mEnableEnterAnim && this.mDimBg != null) {
                this.mDialogAnimHelper.setDiscardImeAnimEnabled(this.mDiscardImeAnimEnabled);
                this.mDialogAnimHelper.executeShowAnim(this.mParentPanel, this.mDimBg, isTablet(), this.mIsWindowLandScape, this.mShowAnimListenerWrapper);
            } else {
                this.mParentPanel.setTag(null);
                View view = this.mDimBg;
                if (view != null) {
                    view.setAlpha(ViewUtils.isNightMode(this.mContext) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT);
                }
            }
            this.mLayoutChangeListener.updateLayout(this.mWindow.getDecorView());
            this.mWindow.getDecorView().addOnLayoutChangeListener(this.mLayoutChangeListener);
        }
    }

    public void onStop() {
        if (isDialogImmersive()) {
            this.mWindow.getDecorView().removeOnLayoutChangeListener(this.mLayoutChangeListener);
        }
    }

    private void checkAndClearFocus() {
        View currentFocus = this.mWindow.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    private boolean checkThread() {
        return this.mCreateThread == Thread.currentThread();
    }

    public void onAttachedToWindow() {
        reInitLandConfig();
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            setupWindowInsetsAnimation();
        }
    }

    public void onDetachedFromWindow() {
        if (!AnimHelper.isDialogDebugInAndroidUIThreadEnabled()) {
            Folme.clean(this.mParentPanel, this.mDimBg);
            translateDialogPanel(0);
        }
        if (isDialogImmersive() && isAsyncInflatePanelEnabled()) {
            safeRemovePanelFromParent();
        }
    }

    public void dismiss(DialogAnimHelper.OnDismiss onDismiss) {
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            cleanWindowInsetsAnimation();
        }
        if (this.mParentPanel == null) {
            if (onDismiss != null) {
                onDismiss.end();
                return;
            }
            return;
        }
        if (this.mDimBg != null) {
            updateDimBgBottomMargin(0);
        }
        if (this.mParentPanel.isAttachedToWindow()) {
            checkAndClearFocus();
            if (!isTablet()) {
                WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(this.mContext);
                if (ScreenModeHelper.isInFreeFormMode(windowInfo.windowMode) || ScreenModeHelper.isInSplitScreenMode(windowInfo.windowMode)) {
                    hideSoftIME();
                }
            } else {
                hideSoftIME();
            }
            this.mDialogAnimHelper.executeDismissAnim(this.mParentPanel, isTablet(), this.mDimBg, onDismiss);
            return;
        }
        Log.d(TAG, "dialog is not attached to window when dismiss is invoked");
        try {
            ((AlertDialog) this.mDialog).realDismiss();
        } catch (IllegalArgumentException e) {
            Log.wtf(TAG, "Not catch the dialog will throw the illegalArgumentException (In Case cause the crash , we expect it should be caught)", e);
        }
    }

    private void changeTitlePadding(TextView textView) {
        textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void translateDialogPanel(int i) {
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "The DialogPanel transitionY for : " + i);
        }
        this.mParentPanel.animate().cancel();
        this.mParentPanel.setTranslationY(i);
    }

    private boolean isDialogImeDebugEnabled() {
        String str = "";
        try {
            String str2 = SystemProperties.get("log.tag.alertdialog.ime.debug.enable");
            if (str2 != null) {
                str = str2;
            }
        } catch (Exception e) {
            Log.i(TAG, "can not access property log.tag.alertdialog.ime.enable, undebugable", e);
        }
        Log.d(TAG, "Alert dialog ime debugEnable = " + str);
        boolean zEquals = TextUtils.equals("true", str);
        this.mIsDebugEnabled = zEquals;
        return zEquals;
    }

    boolean isShowingAnimation() {
        return this.mEnableEnterAnim && (this.mIsDialogAnimating || (!isDialogImmersive() && (Math.abs(this.mShowUpTimeMillis - SystemClock.uptimeMillis()) > this.mNonImmersiveDialogShowAnimDuration ? 1 : (Math.abs(this.mShowUpTimeMillis - SystemClock.uptimeMillis()) == this.mNonImmersiveDialogShowAnimDuration ? 0 : -1)) < 0));
    }

    void setPendingDismiss(boolean z) {
        this.mHasPendingDismiss = z;
    }

    boolean hasPendingDismiss() {
        return this.mHasPendingDismiss;
    }

    static class AlertParams {
        int iconHeight;
        int iconWidth;
        ListAdapter mAdapter;
        boolean mAsyncInflatePanelEnabled;
        boolean mButtonForceVertical;
        CharSequence mCheckBoxMessage;
        boolean[] mCheckedItems;
        CharSequence mComment;
        AlertDialog.OnConfigurationChangedListener mConfigurationChangedListener;
        final Context mContext;
        Cursor mCursor;
        View mCustomTitleView;
        boolean mDiscardImeAnimEnabled;
        boolean mEnableDialogImmersive;
        boolean mEnableEnterAnim;
        List<ButtonInfo> mExtraButtonList;
        boolean mHapticFeedbackEnabled;
        Drawable mIcon;
        final LayoutInflater mInflater;
        boolean mIsChecked;
        String mIsCheckedColumn;
        boolean mIsMultiChoice;
        boolean mIsSingleChoice;
        CharSequence[] mItems;
        AccessibilityDelegateProvider mItemsProvider;
        String mLabelColumn;
        int mLiteVersion;
        boolean mMaterialEnabled;
        CharSequence mMessage;
        DialogInterface.OnClickListener mNegativeButtonListener;
        CharSequence mNegativeButtonText;
        DialogInterface.OnClickListener mNeutralButtonListener;
        CharSequence mNeutralButtonText;
        int mNonImmersiveDialogHeight;
        DialogInterface.OnCancelListener mOnCancelListener;
        DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
        DialogInterface.OnClickListener mOnClickListener;
        AlertDialog.OnDialogShowAnimListener mOnDialogShowAnimListener;
        DialogInterface.OnDismissListener mOnDismissListener;
        AdapterView.OnItemSelectedListener mOnItemSelectedListener;
        DialogInterface.OnKeyListener mOnKeyListener;
        OnPrepareListViewListener mOnPrepareListViewListener;
        DialogInterface.OnShowListener mOnShowListener;
        AlertDialog.OnPanelSizeChangedListener mPanelSizeChangedListener;
        DialogInterface.OnClickListener mPositiveButtonListener;
        CharSequence mPositiveButtonText;
        boolean mPreferLandscape;
        boolean mSmallIcon;
        CharSequence mTitle;
        boolean mUseForceFlipCutout;
        View mView;
        int mViewLayoutResId;
        int mIconId = 0;
        int mIconAttrId = 0;
        int mCheckedItem = -1;
        int mMinCustomVisibleHeight = 0;
        boolean mPrimaryButtonFirstEnabled = false;
        boolean mCancelable = true;

        private enum ItemType {
            DEFAULT,
            CHOICE_SINGLE,
            CHOICE_MULTI
        }

        interface OnPrepareListViewListener {
            void onPrepareListView(ListView listView);
        }

        AlertParams(Context context) {
            this.mContext = context;
            this.mEnableDialogImmersive = (LiteUtils.isCommonLiteStrategy() || (Build.IS_FLIP && DeviceHelper.isTinyScreen(context))) ? false : true;
            this.mNonImmersiveDialogHeight = -2;
            int miuiLiteVersion = DeviceUtils.getMiuiLiteVersion();
            this.mLiteVersion = miuiLiteVersion;
            if (miuiLiteVersion < 0) {
                this.mLiteVersion = 0;
            }
            this.mEnableEnterAnim = true;
            this.mExtraButtonList = new ArrayList();
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        void apply(AlertController alertController) {
            int i;
            View view = this.mCustomTitleView;
            if (view != null) {
                alertController.setCustomTitle(view);
            } else {
                CharSequence charSequence = this.mTitle;
                if (charSequence != null) {
                    alertController.setTitle(charSequence);
                }
                Drawable drawable = this.mIcon;
                if (drawable != null) {
                    alertController.setIcon(drawable);
                }
                int i2 = this.mIconId;
                if (i2 != 0) {
                    alertController.setIcon(i2);
                }
                int i3 = this.mIconAttrId;
                if (i3 != 0) {
                    alertController.setIcon(alertController.getIconAttributeResId(i3));
                }
                if (this.mSmallIcon) {
                    alertController.setUseSmallIcon(true);
                }
                int i4 = this.iconWidth;
                if (i4 != 0 && (i = this.iconHeight) != 0) {
                    alertController.setIconSize(i4, i);
                }
            }
            CharSequence charSequence2 = this.mMessage;
            if (charSequence2 != null) {
                alertController.setMessage(charSequence2);
            }
            CharSequence charSequence3 = this.mComment;
            if (charSequence3 != null) {
                alertController.setComment(charSequence3);
            }
            CharSequence charSequence4 = this.mPositiveButtonText;
            if (charSequence4 != null) {
                alertController.setButton(-1, charSequence4, this.mPositiveButtonListener, null);
            }
            CharSequence charSequence5 = this.mNegativeButtonText;
            if (charSequence5 != null) {
                alertController.setButton(-2, charSequence5, this.mNegativeButtonListener, null);
            }
            CharSequence charSequence6 = this.mNeutralButtonText;
            if (charSequence6 != null) {
                alertController.setButton(-3, charSequence6, this.mNeutralButtonListener, null);
            }
            if (this.mExtraButtonList != null) {
                alertController.mExtraButtonList = new ArrayList(this.mExtraButtonList);
            }
            if (this.mItems != null || this.mCursor != null || this.mAdapter != null) {
                createListView(alertController);
            }
            View view2 = this.mView;
            if (view2 != null) {
                alertController.setView(view2);
            } else {
                int i5 = this.mViewLayoutResId;
                if (i5 != 0) {
                    alertController.setView(i5);
                }
            }
            CharSequence charSequence7 = this.mCheckBoxMessage;
            if (charSequence7 != null) {
                alertController.setCheckBox(this.mIsChecked, charSequence7);
            }
            alertController.mHapticFeedbackEnabled = this.mHapticFeedbackEnabled;
            alertController.setEnableImmersive(this.mEnableDialogImmersive);
            alertController.setNonImmersiveDialogHeight(this.mNonImmersiveDialogHeight);
            alertController.setLiteVersion(this.mLiteVersion);
            alertController.setPreferLandscape(this.mPreferLandscape);
            alertController.setButtonForceVertical(this.mButtonForceVertical);
            alertController.setMinCustomVisibleHeight(this.mMinCustomVisibleHeight);
            alertController.setPrimaryButtonFirstEnabled(this.mPrimaryButtonFirstEnabled);
            alertController.setAsyncInflatePanelEnable(this.mAsyncInflatePanelEnabled);
            alertController.setDiscardImeAnimEnabled(this.mDiscardImeAnimEnabled);
            alertController.setPanelSizeChangedListener(this.mPanelSizeChangedListener);
            alertController.setConfigurationChangedListener(this.mConfigurationChangedListener);
            alertController.setEnableEnterAnim(this.mEnableEnterAnim);
            alertController.setUseForceFlipCutout(this.mUseForceFlipCutout);
            alertController.setMaterialEnabled(this.mMaterialEnabled);
        }

        static void setAccessibilityDelegate(View view, ItemType itemType, AccessibilityDelegateProvider accessibilityDelegateProvider) {
            AccessibilityDelegateCompat defaultAccessibilityDelegateCompat;
            if (accessibilityDelegateProvider != null) {
                defaultAccessibilityDelegateCompat = accessibilityDelegateProvider.getAccessibilityDelegate();
            } else {
                Log.i(AlertController.TAG, "type=" + itemType);
                defaultAccessibilityDelegateCompat = getDefaultAccessibilityDelegateCompat(itemType);
            }
            if (defaultAccessibilityDelegateCompat != null) {
                ViewCompat.setAccessibilityDelegate(view, defaultAccessibilityDelegateCompat);
            }
        }

        private static AccessibilityDelegateCompat getDefaultAccessibilityDelegateCompat(final ItemType itemType) {
            return new AccessibilityDelegateCompat() { // from class: miuix.appcompat.app.AlertController.AlertParams.1
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    int i = AnonymousClass11.$SwitchMap$miuix$appcompat$app$AlertController$AlertParams$ItemType[itemType.ordinal()];
                    if (i == 1) {
                        accessibilityNodeInfoCompat.addAction(16);
                        return;
                    }
                    if (i != 2) {
                        if (i != 3) {
                            return;
                        }
                        accessibilityNodeInfoCompat.setClassName(CheckBox.class.getName());
                        accessibilityNodeInfoCompat.addAction(16);
                        return;
                    }
                    accessibilityNodeInfoCompat.setCheckable(true);
                    accessibilityNodeInfoCompat.setClassName(RadioButton.class.getName());
                    if (view instanceof CheckedTextView) {
                        boolean zIsChecked = ((CheckedTextView) view).isChecked();
                        accessibilityNodeInfoCompat.setChecked(zIsChecked);
                        accessibilityNodeInfoCompat.setClickable(!zIsChecked);
                        if (zIsChecked) {
                            accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                        }
                    }
                }
            };
        }

        private void createListView(final AlertController alertController) {
            int i;
            ItemType itemType;
            ListAdapter listAdapter;
            ListAdapter listAdapter2;
            final ListView listView = (ListView) this.mInflater.inflate(alertController.mListLayout, (ViewGroup) null);
            if (this.mIsMultiChoice) {
                if (this.mCursor == null) {
                    listAdapter2 = new ArrayAdapter<CharSequence>(this.mContext, alertController.mMultiChoiceItemLayout, android.R.id.text1, this.mItems) { // from class: miuix.appcompat.app.AlertController.AlertParams.2
                        @Override // android.widget.ArrayAdapter, android.widget.Adapter
                        public View getView(int i2, View view, ViewGroup viewGroup) {
                            View view2 = super.getView(i2, view, viewGroup);
                            if (AlertParams.this.mCheckedItems != null && AlertParams.this.mCheckedItems[i2]) {
                                listView.setItemChecked(i2, true);
                            }
                            CompatViewMethod.setForceDarkAllowed(view2, false);
                            AlertParams.setAccessibilityDelegate(view2, ItemType.CHOICE_MULTI, AlertParams.this.mItemsProvider);
                            return view2;
                        }
                    };
                } else {
                    listAdapter2 = new CursorAdapter(this.mContext, this.mCursor, false) { // from class: miuix.appcompat.app.AlertController.AlertParams.3
                        private final int mIsCheckedIndex;
                        private final int mLabelIndex;

                        {
                            Cursor cursor = getCursor();
                            this.mLabelIndex = cursor.getColumnIndexOrThrow(AlertParams.this.mLabelColumn);
                            this.mIsCheckedIndex = cursor.getColumnIndexOrThrow(AlertParams.this.mIsCheckedColumn);
                        }

                        @Override // android.widget.CursorAdapter
                        public void bindView(View view, Context context, Cursor cursor) {
                            ((android.widget.CheckedTextView) view.findViewById(android.R.id.text1)).setText(cursor.getString(this.mLabelIndex));
                            listView.setItemChecked(cursor.getPosition(), cursor.getInt(this.mIsCheckedIndex) == 1);
                            AlertParams.setAccessibilityDelegate(view, ItemType.CHOICE_MULTI, AlertParams.this.mItemsProvider);
                        }

                        @Override // android.widget.CursorAdapter
                        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                            View viewInflate = AlertParams.this.mInflater.inflate(alertController.mMultiChoiceItemLayout, viewGroup, false);
                            CompatViewMethod.setForceDarkAllowed(viewInflate, false);
                            return viewInflate;
                        }
                    };
                }
            } else {
                if (this.mIsSingleChoice) {
                    i = alertController.mSingleChoiceItemLayout;
                    itemType = ItemType.CHOICE_SINGLE;
                } else {
                    i = alertController.mListItemLayout;
                    itemType = ItemType.DEFAULT;
                }
                int i2 = i;
                final ItemType itemType2 = itemType;
                if (this.mCursor != null) {
                    listAdapter2 = new SimpleCursorAdapter(this.mContext, i2, this.mCursor, new String[]{this.mLabelColumn}, new int[]{android.R.id.text1}) { // from class: miuix.appcompat.app.AlertController.AlertParams.4
                        @Override // android.widget.CursorAdapter, android.widget.Adapter
                        public View getView(int i3, View view, ViewGroup viewGroup) {
                            View view2 = super.getView(i3, view, viewGroup);
                            if (view == null) {
                                AnimHelper.addPressAnim(view2);
                            }
                            AlertParams.setAccessibilityDelegate(view2, itemType2, AlertParams.this.mItemsProvider);
                            return view2;
                        }
                    };
                } else {
                    ListAdapter listAdapter3 = this.mAdapter;
                    if (listAdapter3 == null) {
                        listAdapter = listAdapter3;
                        CheckedItemAdapter checkedItemAdapter = new CheckedItemAdapter(this.mContext, i2, android.R.id.text1, this.mItems);
                        checkedItemAdapter.setItemsProvider(this.mItemsProvider);
                        checkedItemAdapter.setItemType(itemType2);
                        listAdapter = checkedItemAdapter;
                    }
                    listAdapter = listAdapter3;
                    listAdapter2 = listAdapter;
                }
            }
            OnPrepareListViewListener onPrepareListViewListener = this.mOnPrepareListViewListener;
            if (onPrepareListViewListener != null) {
                onPrepareListViewListener.onPrepareListView(listView);
            }
            alertController.mAdapter = listAdapter2;
            alertController.mCheckedItem = this.mCheckedItem;
            if (this.mOnClickListener != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.app.AlertController.AlertParams.5
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i3, long j) {
                        AlertParams.this.mOnClickListener.onClick(alertController.mDialog, i3);
                        if (AlertParams.this.mIsSingleChoice) {
                            return;
                        }
                        alertController.mDialog.dismiss();
                    }
                });
            } else if (this.mOnCheckboxClickListener != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.app.AlertController.AlertParams.6
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i3, long j) {
                        if (AlertParams.this.mCheckedItems != null) {
                            AlertParams.this.mCheckedItems[i3] = listView.isItemChecked(i3);
                        }
                        AlertParams.this.mOnCheckboxClickListener.onClick(alertController.mDialog, i3, listView.isItemChecked(i3));
                    }
                });
            }
            AdapterView.OnItemSelectedListener onItemSelectedListener = this.mOnItemSelectedListener;
            if (onItemSelectedListener != null) {
                listView.setOnItemSelectedListener(onItemSelectedListener);
            }
            if (this.mIsSingleChoice) {
                listView.setChoiceMode(1);
            } else if (this.mIsMultiChoice) {
                listView.setChoiceMode(2);
            }
            alertController.mListView = listView;
        }
    }

    /* JADX INFO: renamed from: miuix.appcompat.app.AlertController$11, reason: invalid class name */
    static /* synthetic */ class AnonymousClass11 {
        static final /* synthetic */ int[] $SwitchMap$miuix$appcompat$app$AlertController$AlertParams$ItemType;

        static {
            int[] iArr = new int[AlertParams.ItemType.values().length];
            $SwitchMap$miuix$appcompat$app$AlertController$AlertParams$ItemType = iArr;
            try {
                iArr[AlertParams.ItemType.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$appcompat$app$AlertController$AlertParams$ItemType[AlertParams.ItemType.CHOICE_SINGLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miuix$appcompat$app$AlertController$AlertParams$ItemType[AlertParams.ItemType.CHOICE_MULTI.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    static class ButtonInfo {
        private GroupButton mButton;
        private Message mMsg;
        private final DialogInterface.OnClickListener mOnClickListener;
        private final int mStyle;
        private final CharSequence mText;
        private final int mWhich;

        ButtonInfo(CharSequence charSequence, int i, Message message) {
            this.mText = charSequence;
            this.mStyle = i;
            this.mMsg = message;
            this.mOnClickListener = null;
            this.mWhich = 0;
        }

        ButtonInfo(CharSequence charSequence, int i, DialogInterface.OnClickListener onClickListener, int i2) {
            this.mText = charSequence;
            this.mStyle = i;
            this.mMsg = null;
            this.mOnClickListener = onClickListener;
            this.mWhich = i2;
        }
    }

    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        private AccessibilityDelegateProvider mItemsProvider;
        private AlertParams.ItemType mType;

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        public CheckedItemAdapter(Context context, int i, int i2, CharSequence[] charSequenceArr) {
            super(context, i, i2, charSequenceArr);
            this.mType = AlertParams.ItemType.DEFAULT;
        }

        public void setItemsProvider(AccessibilityDelegateProvider accessibilityDelegateProvider) {
            this.mItemsProvider = accessibilityDelegateProvider;
        }

        public void setItemType(AlertParams.ItemType itemType) {
            this.mType = itemType;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            if (view == null) {
                AnimHelper.addPressAnim(view2);
            }
            AlertParams.setAccessibilityDelegate(view2, this.mType, this.mItemsProvider);
            return view2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFreeFormMode() {
        return EnvStateManager.isFreeFormMode(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDialogPanelByWindowInsets(WindowInsets windowInsets) {
        updateParentPanelMarginByWindowInsets(windowInsets);
        if (isNeedUpdateDialogPanelTranslationY()) {
            boolean zIsInMultiWindowMode = MiuixUIUtils.isInMultiWindowMode(this.mContext);
            Insets insets = windowInsets.getInsets(WindowInsets.Type.ime());
            Insets insets2 = windowInsets.getInsets(WindowInsets.Type.navigationBars());
            if (this.mIsDebugEnabled) {
                Log.d(TAG, "======================Debug for checkTranslateDialogPanel======================");
                Log.d(TAG, "The mPanelAndImeMargin: " + this.mPanelAndImeMargin);
                Log.d(TAG, "The imeInsets info: " + insets.toString());
                Log.d(TAG, "The navigationBarInsets info: " + insets2.toString());
                Log.d(TAG, "The insets info: " + windowInsets);
            }
            boolean zIsTablet = isTablet();
            if (!zIsTablet) {
                updateDimBgBottomMargin(insets.bottom);
            }
            int i = insets.bottom;
            if (zIsInMultiWindowMode && !zIsTablet) {
                i -= insets2.bottom;
            }
            updateDialogPanelTranslationYByIme(i, zIsInMultiWindowMode, zIsTablet);
            if (this.mIsDebugEnabled) {
                Log.d(TAG, "===================End of Debug for checkTranslateDialogPanel===================");
            }
        }
    }

    private boolean isNeedUpdateDialogPanelTranslationY() {
        byte b;
        boolean zIsInMultiWindowMode = MiuixUIUtils.isInMultiWindowMode(this.mContext);
        int i = this.mContext.getResources().getConfiguration().keyboard;
        boolean z = i == 2 || i == 3;
        boolean z2 = Build.IS_TABLET;
        if (!zIsInMultiWindowMode || isFreeFormMode()) {
            b = -1;
        } else {
            b = DeviceHelper.isWideScreen(this.mContext) ? (byte) 0 : (byte) 1;
        }
        if (this.mIsDialogAnimating) {
            if ((z2 && z) || b != 0) {
                return false;
            }
        } else {
            if ((z2 && z) || !this.mSetupWindowInsetsAnimation) {
                return false;
            }
            if (!this.mInsetsAnimationPlayed && !zIsInMultiWindowMode) {
                return false;
            }
        }
        return true;
    }

    int getNonImmersiveDialogHeight() {
        return this.mNonImmersiveDialogHeight;
    }

    void setNonImmersiveDialogHeight(int i) {
        this.mNonImmersiveDialogHeight = i;
    }

    private boolean isSpecifiedDialogHeight() {
        return (isDialogImmersive() || this.mNonImmersiveDialogHeight == -2) ? false : true;
    }

    private void updateDialogPanelTranslationYByIme(int i, boolean z, boolean z2) {
        boolean z3 = false;
        if (i > 0) {
            int dialogPanelMargin = getDialogPanelMargin();
            int translationY = (int) (dialogPanelMargin + this.mParentPanel.getTranslationY());
            this.mPanelAndImeMargin = translationY;
            if (translationY <= 0) {
                this.mPanelAndImeMargin = 0;
            }
            if (this.mIsDebugEnabled) {
                Log.d(TAG, "updateDialogPanelTranslationYByIme mPanelAndImeMargin " + this.mPanelAndImeMargin + " isMultiWindowMode " + z + " imeBottom " + i);
            }
            int i2 = (!z || z2) ? (-i) + this.mPanelAndImeMargin : -i;
            if (z2 && i < this.mPanelAndImeMargin) {
                i2 = 0;
            }
            if (this.mIsDialogAnimating) {
                if (this.mIsDebugEnabled) {
                    Log.d(TAG, "updateDialogPanelTranslationYByIme anim translateDialogPanel Y=" + i2);
                }
                this.mParentPanel.animate().cancel();
                this.mParentPanel.animate().setDuration(200L).translationY(i2).start();
                return;
            }
            if (this.mIsDebugEnabled) {
                Log.d(TAG, "updateDialogPanelTranslationYByIme translateDialogPanel Y=" + i2);
            }
            int i3 = -(i - dialogPanelMargin);
            if (i2 < 0 && i3 < 0 && i2 < i3) {
                z3 = true;
            }
            if (z2 && !this.mIsInFreeForm && isDialogImmersive() && z3) {
                i2 = i3;
            }
            if (this.mIsDebugEnabled) {
                Log.d(TAG, "updateDialogPanelTranslationYByIme: expectedTabletTranslationY = " + i3 + ", translationYUnexpected = " + z3 + ", bottom = " + i2 + ", mIsInFreeForm = " + this.mIsInFreeForm);
            }
            translateDialogPanel(i2);
            return;
        }
        if (this.mIsDebugEnabled) {
            Log.d(TAG, "updateDialogPanelTranslationYByIme imeBottom <= 0");
        }
        if (this.mParentPanel.getTranslationY() < 0.0f) {
            translateDialogPanel(0);
        }
    }

    private static class LayoutChangeListener implements View.OnLayoutChangeListener {
        private final WeakReference<AlertController> mHost;
        private final Rect mCutoutInsets = new Rect();
        private final Rect mWindowVisibleFrame = new Rect();

        LayoutChangeListener(AlertController alertController) {
            this.mHost = new WeakReference<>(alertController);
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            AlertController alertController = this.mHost.get();
            if (alertController != null) {
                view.getWindowVisibleDisplayFrame(this.mWindowVisibleFrame);
                updateCutoutInsets(view, this.mCutoutInsets);
                handleMultiWindowLandscapeChange(alertController, i3);
                if (android.os.Build.VERSION.SDK_INT < 30) {
                    if (view.findFocus() != null) {
                        if (alertController.isFreeFormMode()) {
                            return;
                        }
                        handleImeChange(view, this.mWindowVisibleFrame, alertController);
                    } else if (alertController.mParentPanel.getTranslationY() < 0.0f) {
                        alertController.translateDialogPanel(0);
                    }
                }
            }
        }

        public void updateLayout(View view) {
            AlertController alertController = this.mHost.get();
            if (alertController != null) {
                view.getWindowVisibleDisplayFrame(this.mWindowVisibleFrame);
                updateCutoutInsets(view, this.mCutoutInsets);
                handleMultiWindowLandscapeChange(alertController, view.getWidth());
            }
        }

        private void updateCutoutInsets(View view, Rect rect) {
            WindowInsets rootWindowInsets = view != null ? view.getRootWindowInsets() : null;
            if (rootWindowInsets == null) {
                return;
            }
            Insets insets = android.os.Build.VERSION.SDK_INT >= 30 ? rootWindowInsets.getInsets(WindowInsets.Type.displayCutout()) : null;
            if (insets == null || android.os.Build.VERSION.SDK_INT < 29) {
                return;
            }
            rect.left = insets.left;
            rect.top = insets.top;
            rect.right = insets.right;
            rect.bottom = insets.bottom;
        }

        private void handleImeChange(View view, Rect rect, AlertController alertController) {
            int height = (view.getHeight() - alertController.getDialogPanelExtraBottomPadding()) - rect.bottom;
            int systemWindowInsetBottom = 0;
            if (height > 0) {
                int i = -height;
                WindowInsets rootWindowInsets = view.getRootWindowInsets();
                if (rootWindowInsets != null) {
                    if (android.os.Build.VERSION.SDK_INT >= 30) {
                        systemWindowInsetBottom = rootWindowInsets.getInsets(WindowInsets.Type.systemBars()).bottom;
                    } else {
                        systemWindowInsetBottom = rootWindowInsets.getSystemWindowInsetBottom();
                    }
                }
                systemWindowInsetBottom += i;
                alertController.mDialogAnimHelper.cancelAnimator();
            }
            alertController.translateDialogPanel(systemWindowInsetBottom);
        }

        private void changeViewPadding(View view, int i, int i2) {
            view.setPadding(i, 0, i2, 0);
        }

        private void handleMultiWindowLandscapeChange(AlertController alertController, int i) {
            if (!MiuixUIUtils.isInMultiWindowMode(alertController.mContext)) {
                DialogRootView dialogRootView = alertController.mDialogRootView;
                if (dialogRootView.getPaddingLeft() > 0 || dialogRootView.getPaddingRight() > 0) {
                    changeViewPadding(dialogRootView, 0, 0);
                    return;
                }
                return;
            }
            boolean z = (this.mCutoutInsets.left > 0 || this.mCutoutInsets.right > 0) && (android.os.Build.VERSION.SDK_INT >= 36);
            if (this.mWindowVisibleFrame.left <= 0 || z) {
                changeViewPadding(alertController.mDialogRootView, 0, 0);
                return;
            }
            int iWidth = i - this.mWindowVisibleFrame.width();
            if (this.mWindowVisibleFrame.right == i) {
                changeViewPadding(alertController.mDialogRootView, iWidth, 0);
            } else {
                changeViewPadding(alertController.mDialogRootView, 0, iWidth);
            }
        }

        public boolean hasNavigationBarHeightInMultiWindowMode() {
            WindowUtils.getScreenSize(this.mHost.get().mContext, this.mHost.get().mScreenRealSize);
            return (this.mWindowVisibleFrame.left == 0 && this.mWindowVisibleFrame.right == this.mHost.get().mScreenRealSize.x && this.mWindowVisibleFrame.top <= EnvStateManager.getStatusBarHeight(this.mHost.get().mContext, false)) ? false : true;
        }

        public boolean isInMultiScreenTop() {
            AlertController alertController = this.mHost.get();
            if (alertController == null) {
                return false;
            }
            WindowUtils.getScreenSize(alertController.mContext, alertController.mScreenRealSize);
            if (this.mWindowVisibleFrame.left == 0 && this.mWindowVisibleFrame.right == alertController.mScreenRealSize.x) {
                return this.mWindowVisibleFrame.top >= 0 && this.mWindowVisibleFrame.bottom <= ((int) (((float) alertController.mScreenRealSize.y) * 0.75f));
            }
            return false;
        }
    }
}
