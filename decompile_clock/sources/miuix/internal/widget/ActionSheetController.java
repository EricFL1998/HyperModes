package miuix.internal.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import miuix.animation.Folme;
import miuix.appcompat.R;
import miuix.appcompat.app.AccessibilityDelegateProvider;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.internal.widget.DialogParentPanel2;
import miuix.appcompat.widget.DialogAnimHelper;
import miuix.core.util.EnvStateManager;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowBaseInfo;
import miuix.core.widget.NestedScrollView;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.ViewUtils;
import miuix.os.Build;
import miuix.os.DeviceHelper;
import miuix.springback.view.SpringBackLayout;
import miuix.theme.token.DimToken;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
class ActionSheetController {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int SHOW_ARROW_SHAPE_WINDOW_WIDTH_THRESHOLD_DP = 747;
    private static final String TAG = "ActionSheetController";
    private ActionSheet.ActionSheetItemType[] mActionItemTypes;
    private CharSequence[] mActionItems;
    private int mActionSheetLayout;
    private NestedScrollView mActionSheetScrollView;
    private LinearLayout mAlertContentWrapper;
    protected boolean mCanceledOnTouchOutside;
    private LinearLayout mContentContainer;
    private ActionSheet.ContentController mContentController;
    private int mContentLayout;
    private ViewGroup mContentPanel;
    private int mContentPanelHeight;
    private int mContentPanelWidth;
    private final Context mContext;
    final AppCompatDialog mDialog;
    private final DialogAnimHelper mDialogAnimHelper;
    private View mDimBg;
    protected boolean mEnableEnterAnim;
    protected boolean mHapticFeedbackEnabled;
    protected boolean mHasPendingDismiss;
    protected boolean mIsAssociatedActivityNavigationBarHidden;
    private boolean mIsDialogAnimating;
    protected boolean mIsFlipTinyScreen;
    private boolean mIsFromRebuild;
    private boolean mIsInFreeForm;
    private DialogInterface.OnClickListener mItemClickListener;
    private AccessibilityDelegateProvider mItemProvider;
    private int mListItemLayout;
    private ListView mListView;
    ListAdapter mListViewAdapter;
    private CharSequence mMessage;
    private TextView mMessageView;
    private ActionSheet.ActionSheetMode mMode;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private AlertDialog.OnConfigurationChangedListener mOnConfigurationChangedListener;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private final DialogInterface.OnDismissListener mOnDismissListenerWrapper;
    private DialogInterface.OnKeyListener mOnKeyListener;
    private DialogInterface.OnShowListener mOnShowListener;
    private final DialogInterface.OnShowListener mOnShowListenerWrapper;
    private Rect mPanelMargins;
    private ActionSheetRootView mRootView;
    private Point mRootViewSize;
    private Point mRootViewSizeDp;
    private int mScreenOrientation;
    private Button mSeparateButton;
    private DialogInterface.OnClickListener mSeparateButtonClickListener;
    private CharSequence mSeparateButtonText;
    private boolean mSetupWindowInsetsAnimation;
    private AlertDialog.OnDialogShowAnimListener mShowAnimListener;
    private final AlertDialog.OnDialogShowAnimListener mShowAnimListenerWrapper;
    private SpringBackLayout mSpringBackLayout;
    private final Map<ActionSheet.ActionSheetItemType, Integer> mTypeColorMap;
    private final Window mWindow;
    private final WindowManager mWindowManager;

    private int getCutoutMode(int i, int i2) {
        if (i2 == 0) {
            return i == 2 ? 2 : 1;
        }
        return i2;
    }

    /* JADX INFO: renamed from: miuix.internal.widget.ActionSheetController$3, reason: invalid class name */
    class AnonymousClass3 implements AlertDialog.OnDialogShowAnimListener {
        AnonymousClass3() {
        }

        @Override // miuix.appcompat.app.AlertDialog.OnDialogShowAnimListener
        public void onShowAnimStart() {
            ActionSheetController.this.mIsDialogAnimating = true;
            if (ActionSheetController.this.mShowAnimListener != null) {
                ActionSheetController.this.mShowAnimListener.onShowAnimStart();
            }
        }

        @Override // miuix.appcompat.app.AlertDialog.OnDialogShowAnimListener
        public void onShowAnimComplete() {
            ActionSheetController.this.mIsDialogAnimating = false;
            if (ActionSheetController.this.mShowAnimListener != null) {
                ActionSheetController.this.mShowAnimListener.onShowAnimComplete();
            }
            if (!ActionSheetController.this.mHasPendingDismiss || ActionSheetController.this.mDialog == null || ActionSheetController.this.mWindow == null) {
                return;
            }
            View decorView = ActionSheetController.this.mWindow.getDecorView();
            final AppCompatDialog appCompatDialog = ActionSheetController.this.mDialog;
            Objects.requireNonNull(appCompatDialog);
            decorView.post(new Runnable() { // from class: miuix.internal.widget.ActionSheetController$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    appCompatDialog.dismiss();
                }
            });
        }
    }

    public ActionSheetController(Context context, AppCompatDialog appCompatDialog, Window window) {
        this(context, appCompatDialog, window, Build.IS_TABLET ? ActionSheet.ActionSheetMode.ARROW_MODE : ActionSheet.ActionSheetMode.ALERT_MODE);
    }

    public ActionSheetController(Context context, AppCompatDialog appCompatDialog, Window window, ActionSheet.ActionSheetMode actionSheetMode) {
        this.mPanelMargins = new Rect();
        this.mRootViewSize = new Point();
        this.mRootViewSizeDp = new Point();
        this.mTypeColorMap = new HashMap();
        boolean z = false;
        this.mScreenOrientation = 0;
        this.mDialogAnimHelper = new DialogAnimHelper();
        this.mHasPendingDismiss = false;
        this.mCanceledOnTouchOutside = true;
        this.mIsAssociatedActivityNavigationBarHidden = false;
        this.mIsInFreeForm = false;
        this.mOnShowListenerWrapper = new DialogInterface.OnShowListener() { // from class: miuix.internal.widget.ActionSheetController.1
            @Override // android.content.DialogInterface.OnShowListener
            public void onShow(DialogInterface dialogInterface) {
                boolean zIsFromArrowShape;
                if (dialogInterface instanceof ArrowActionSheet) {
                    zIsFromArrowShape = ((ArrowActionSheet) dialogInterface).isFromAlertShape();
                } else {
                    zIsFromArrowShape = dialogInterface instanceof AlertActionSheet ? ((AlertActionSheet) dialogInterface).isFromArrowShape() : false;
                }
                if (ActionSheetController.this.mOnShowListener == null || zIsFromArrowShape) {
                    return;
                }
                ActionSheetController.this.mOnShowListener.onShow(dialogInterface);
            }
        };
        this.mOnDismissListenerWrapper = new DialogInterface.OnDismissListener() { // from class: miuix.internal.widget.ActionSheetController.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                boolean zIsDismissForShift;
                boolean z2 = dialogInterface instanceof ArrowActionSheet;
                if (z2) {
                    zIsDismissForShift = ((ArrowActionSheet) dialogInterface).isDismissForShift();
                } else {
                    zIsDismissForShift = dialogInterface instanceof AlertActionSheet ? ((AlertActionSheet) dialogInterface).isDismissForShift() : false;
                }
                if (ActionSheetController.this.mOnDismissListener != null && !zIsDismissForShift) {
                    ActionSheetController.this.mOnDismissListener.onDismiss(dialogInterface);
                    if (z2) {
                        ((ArrowActionSheet) dialogInterface).setIsFromAlertShape(false);
                    } else if (dialogInterface instanceof AlertActionSheet) {
                        ((AlertActionSheet) dialogInterface).setIsFromArrowShape(false);
                    }
                }
                if (z2) {
                    ((ArrowActionSheet) dialogInterface).setIsDismissForShift(false);
                } else if (dialogInterface instanceof AlertActionSheet) {
                    ((AlertActionSheet) dialogInterface).setIsDismissForShift(false);
                }
            }
        };
        this.mShowAnimListenerWrapper = new AnonymousClass3();
        this.mMode = actionSheetMode;
        this.mContext = context;
        this.mDialog = appCompatDialog;
        this.mWindow = window;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mEnableEnterAnim = true;
        if (Build.IS_FLIP && DeviceHelper.isTinyScreen(context)) {
            z = true;
        }
        this.mIsFlipTinyScreen = z;
        initViewAndLayout(context);
        initListener();
    }

    private void initViewAndLayout(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(null, R.styleable.ActionSheet, this.mMode == ActionSheet.ActionSheetMode.ALERT_MODE ? R.attr.actionSheetAlertStyle : R.attr.actionSheetArrowStyle, 0);
        this.mActionSheetLayout = typedArrayObtainStyledAttributes.getResourceId(R.styleable.ActionSheet_actionSheetLayout, R.layout.miuix_appcompat_action_sheet_layout);
        this.mContentLayout = typedArrayObtainStyledAttributes.getResourceId(R.styleable.ActionSheet_actionSheetContentLayout, R.layout.miuix_appcompat_action_sheet_alert_content);
        this.mListItemLayout = typedArrayObtainStyledAttributes.getResourceId(R.styleable.ActionSheet_actionSheetListItem, R.layout.miuix_appcompat_action_sheet_list_item);
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setShowAnimListener(AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
        this.mShowAnimListener = onDialogShowAnimListener;
    }

    public AlertDialog.OnDialogShowAnimListener getShowAnimListener() {
        return this.mShowAnimListener;
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.mOnShowListener = onShowListener;
    }

    public DialogInterface.OnShowListener getOnShowListener() {
        return this.mOnShowListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
    }

    public void setConfigurationChangedListener(AlertDialog.OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mOnConfigurationChangedListener = onConfigurationChangedListener;
    }

    public AlertDialog.OnConfigurationChangedListener getConfigurationChangedListener() {
        return this.mOnConfigurationChangedListener;
    }

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return this.mOnDismissListener;
    }

    public DialogInterface.OnCancelListener getOnCancelListener() {
        return this.mOnCancelListener;
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.mOnKeyListener = onKeyListener;
    }

    public DialogInterface.OnKeyListener getOnKeyListener() {
        return this.mOnKeyListener;
    }

    public void setEnableEnterAnim(boolean z) {
        this.mEnableEnterAnim = z;
    }

    public void setCanceledOnTouchOutside(boolean z) {
        this.mCanceledOnTouchOutside = z;
    }

    public boolean isCanceledOnTouchOutside() {
        return this.mCanceledOnTouchOutside;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public TextView getMessageView() {
        return this.mMessageView;
    }

    public TextView getSeparateView() {
        return this.mSeparateButton;
    }

    public void setMessage(CharSequence charSequence) {
        this.mMessage = charSequence;
        TextView textView = this.mMessageView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public CharSequence getMessage() {
        return this.mMessage;
    }

    public void setSeparateButtonText(CharSequence charSequence) {
        this.mSeparateButtonText = charSequence;
        Button button = this.mSeparateButton;
        if (button != null) {
            button.setText(charSequence);
        }
    }

    public void setSeparateButtonClickListener(DialogInterface.OnClickListener onClickListener) {
        this.mSeparateButtonClickListener = onClickListener;
    }

    public void setActionItems(int i, DialogInterface.OnClickListener onClickListener) {
        this.mActionItems = this.mContext.getResources().getTextArray(i);
        this.mItemClickListener = onClickListener;
    }

    public void setActionItems(int i, ActionSheet.ActionSheetItemType[] actionSheetItemTypeArr, DialogInterface.OnClickListener onClickListener) {
        this.mActionItems = this.mContext.getResources().getTextArray(i);
        this.mActionItemTypes = actionSheetItemTypeArr;
        this.mItemClickListener = onClickListener;
    }

    public void setActionItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
        this.mActionItems = charSequenceArr;
        this.mItemClickListener = onClickListener;
    }

    public void setActionItems(CharSequence[] charSequenceArr, ActionSheet.ActionSheetItemType[] actionSheetItemTypeArr, DialogInterface.OnClickListener onClickListener) {
        this.mActionItems = charSequenceArr;
        this.mActionItemTypes = actionSheetItemTypeArr;
        this.mItemClickListener = onClickListener;
    }

    public CharSequence[] getActionItems() {
        return this.mActionItems;
    }

    public DialogInterface.OnClickListener getItemClickListener() {
        return this.mItemClickListener;
    }

    public ActionSheet.ActionSheetItemType[] getItemTypes() {
        return this.mActionItemTypes;
    }

    public void setContentController(ActionSheet.ContentController contentController) {
        this.mContentController = contentController;
    }

    public void setListViewAdapter(ListAdapter listAdapter) {
        this.mListViewAdapter = listAdapter;
    }

    public void setItemProvider(AccessibilityDelegateProvider accessibilityDelegateProvider) {
        this.mItemProvider = accessibilityDelegateProvider;
    }

    public AccessibilityDelegateProvider getItemProvider() {
        return this.mItemProvider;
    }

    public ListAdapter getListViewAdapter() {
        return this.mListViewAdapter;
    }

    public void installContent(Bundle bundle) {
        this.mIsFromRebuild = bundle != null;
        this.mIsInFreeForm = EnvStateManager.isFreeFormMode(this.mContext);
        this.mDialog.setContentView(this.mActionSheetLayout);
        ActionSheetRootView actionSheetRootView = (ActionSheetRootView) this.mWindow.findViewById(R.id.action_sheet_root_view);
        this.mRootView = actionSheetRootView;
        actionSheetRootView.setConfigurationChangedCallback(new ActionSheetRootView.ConfigurationChangedCallback() { // from class: miuix.internal.widget.ActionSheetController.4
            @Override // miuix.internal.widget.ActionSheetRootView.ConfigurationChangedCallback
            public void onConfigurationChanged(Configuration configuration) {
                ActionSheetController.this.runConfigurationChanged(configuration);
            }
        });
        this.mRootView.setContentController(this.mContentController);
        View viewFindViewById = this.mWindow.findViewById(R.id.action_sheet_dim_bg);
        this.mDimBg = viewFindViewById;
        viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: miuix.internal.widget.ActionSheetController.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ActionSheetController.this.mCanceledOnTouchOutside) {
                    ActionSheetController.this.mDialog.cancel();
                }
            }
        });
        this.mDimBg.setAlpha(ViewUtils.isNightMode(this.mContext) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT);
        updateRootViewSizeByWindow();
        setupWindow();
        setupContentView(false);
        prepareTypeColorMap(this.mContext);
    }

    private void initListener() {
        AppCompatDialog appCompatDialog = this.mDialog;
        if (appCompatDialog == null) {
            return;
        }
        appCompatDialog.setOnShowListener(this.mOnShowListenerWrapper);
        this.mDialog.setOnDismissListener(this.mOnDismissListenerWrapper);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runConfigurationChanged(Configuration configuration) {
        updateRootViewSizeByWindow();
        Log.d(TAG, "onConfigurationChanged: mRootViewSizeDp = " + this.mRootViewSizeDp);
        boolean z = Build.IS_TABLET && this.mRootViewSizeDp.x >= 747;
        boolean z2 = Build.IS_TABLET && this.mRootViewSizeDp.x < 747;
        AppCompatDialog appCompatDialog = this.mDialog;
        if ((appCompatDialog instanceof ArrowActionSheet) && z2) {
            ((ArrowActionSheet) appCompatDialog).dismissForShiftWithoutAnimation();
            View arrowAnchor = ((ArrowActionSheet) this.mDialog).getArrowAnchor();
            ActionSheet.ArrowMode arrowMode = ((ArrowActionSheet) this.mDialog).getArrowMode();
            AlertActionSheet alertActionSheetCreateAlertActionSheet = ((ArrowActionSheet) this.mDialog).createAlertActionSheet(arrowAnchor);
            alertActionSheetCreateAlertActionSheet.setArrowMode(arrowMode);
            alertActionSheetCreateAlertActionSheet.setEnableEnterAnim(false);
            alertActionSheetCreateAlertActionSheet.setIsFromArrowShape(true);
            alertActionSheetCreateAlertActionSheet.show();
            Log.d(TAG, "onConfigurationChanged first branch: ArrowActionSheet -> AlertActionSheet shift");
        } else if ((appCompatDialog instanceof AlertActionSheet) && z) {
            View arrowActionAnchor = ((AlertActionSheet) appCompatDialog).getArrowActionAnchor();
            ActionSheet.ArrowMode arrowMode2 = ((AlertActionSheet) this.mDialog).getArrowMode();
            if (arrowActionAnchor != null && arrowMode2 != null && arrowMode2 != ActionSheet.ArrowMode.ARROW_MODE_NONE) {
                ((AlertActionSheet) this.mDialog).dismissForShiftWithoutAnimation();
                ArrowActionSheet arrowActionSheetCreateArrowActionSheet = ((AlertActionSheet) this.mDialog).createArrowActionSheet(arrowActionAnchor);
                arrowActionSheetCreateArrowActionSheet.setArrowMode(arrowMode2);
                arrowActionSheetCreateArrowActionSheet.setEnableEnterAnim(false);
                arrowActionSheetCreateArrowActionSheet.setIsFromAlertShape(true);
                arrowActionSheetCreateArrowActionSheet.show();
                Log.d(TAG, "onConfigurationChanged second branch: AlertActionSheet -> ArrowActionSheet shift");
            }
        } else {
            onConfigurationChanged(configuration);
            Log.d(TAG, "onConfigurationChanged third branch: run config changed");
        }
        AlertDialog.OnConfigurationChangedListener onConfigurationChangedListener = this.mOnConfigurationChangedListener;
        if (onConfigurationChangedListener != null) {
            onConfigurationChangedListener.onConfigurationChanged(this.mDialog, null, configuration);
        }
    }

    private void setupWindow() {
        Activity associatedActivity;
        Activity associatedActivity2;
        this.mWindow.setLayout(-1, -1);
        this.mWindow.setBackgroundDrawableResource(R.color.miuix_color_transparent);
        this.mWindow.setDimAmount(0.0f);
        this.mWindow.setWindowAnimations(R.style.Animation_Dialog_NoAnimation);
        this.mWindow.addFlags(-2147481344);
        this.mWindow.setFlags(131072, 131072);
        if (android.os.Build.VERSION.SDK_INT > 30) {
            if (this.mMode == ActionSheet.ActionSheetMode.ALERT_MODE) {
                associatedActivity2 = ((AlertDialog) this.mDialog).getAssociatedActivity();
            } else {
                associatedActivity2 = ((AlertDialog) this.mDialog).getAssociatedActivity();
            }
            if (associatedActivity2 != null) {
                this.mWindow.getAttributes().layoutInDisplayCutoutMode = getCutoutMode(getScreenOrientation(), associatedActivity2.getWindow().getAttributes().layoutInDisplayCutoutMode);
            } else {
                this.mWindow.getAttributes().layoutInDisplayCutoutMode = getScreenOrientation() != 2 ? 3 : 2;
            }
        }
        clearFitSystemWindow(this.mWindow.getDecorView());
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            this.mWindow.getAttributes().setFitInsetsSides(0);
            if (this.mMode == ActionSheet.ActionSheetMode.ALERT_MODE) {
                associatedActivity = ((AlertDialog) this.mDialog).getAssociatedActivity();
            } else {
                associatedActivity = ((AlertDialog) this.mDialog).getAssociatedActivity();
            }
            boolean associatedActivitySystemBarVisibility = getAssociatedActivitySystemBarVisibility(associatedActivity, WindowInsets.Type.statusBars());
            if (associatedActivity != null && (associatedActivity.getWindow().getAttributes().flags & 1024) != 1024 && (associatedActivitySystemBarVisibility || this.mIsInFreeForm)) {
                this.mWindow.clearFlags(1024);
            }
            if (getAssociatedActivitySystemBarVisibility(associatedActivity, WindowInsets.Type.navigationBars()) || this.mIsInFreeForm) {
                return;
            }
            setWindowNavigationBarHidden();
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

    private void setWindowNavigationBarHidden() {
        View decorView = this.mWindow.getDecorView();
        if (decorView != null) {
            decorView.setSystemUiVisibility(4098);
            this.mIsAssociatedActivityNavigationBarHidden = true;
        }
    }

    private void setupContentView(boolean z) {
        setupContentPanel();
        if (!z) {
            setupContent();
        }
        adjustSpringEnabled();
        if (this.mMode == ActionSheet.ActionSheetMode.ALERT_MODE) {
            adjustAlertMessageMaxLine();
            adjustContentWrapperScrollable();
        }
    }

    private void setupContentPanel() {
        FrameLayout.LayoutParams layoutParams;
        if (this.mContentController == null) {
            throw new RuntimeException("action sheet require set contentController");
        }
        WindowInsets rootWindowInsets = this.mWindow.getDecorView().getRootWindowInsets();
        if (this.mContentPanel == null) {
            this.mContentPanel = (ViewGroup) LayoutInflater.from(this.mContext).inflate(this.mContentLayout, (ViewGroup) this.mRootView, false);
        }
        ViewGroup viewGroup = this.mContentPanel;
        if (viewGroup instanceof ArrowActionSheetPanel) {
            ((ArrowActionSheetPanel) viewGroup).setArrowMode(this.mContentController.getArrowMode());
        }
        this.mContentPanelWidth = this.mContentController.calcContentPanelWidth(this.mContext, this.mRootView, this.mContentPanel, this.mRootViewSize.x, rootWindowInsets);
        this.mContentPanelHeight = this.mContentController.calcContentPanelHeight(this.mContext, this.mRootView, this.mContentPanel, this.mRootViewSize.y, rootWindowInsets);
        int[] iArrCalcHorizontalMargin = this.mContentController.calcHorizontalMargin(this.mContext, this.mRootViewSize.x, rootWindowInsets);
        this.mPanelMargins.left = iArrCalcHorizontalMargin[0];
        this.mPanelMargins.right = iArrCalcHorizontalMargin[1];
        if (this.mContentPanelWidth == -1) {
            this.mContentPanelWidth = this.mRootViewSize.x - (this.mPanelMargins.left + this.mPanelMargins.right);
        }
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            int[] iArrCalcVerticalMargin = this.mContentController.calcVerticalMargin(this.mContext, this.mRootViewSize.y, rootWindowInsets);
            this.mPanelMargins.top = iArrCalcVerticalMargin[0];
            this.mPanelMargins.bottom = iArrCalcVerticalMargin[1];
        }
        this.mRootView.setContentPanelExtraBounds(this.mPanelMargins);
        ViewGroup viewGroup2 = this.mContentPanel;
        if (viewGroup2 != null && viewGroup2.getParent() == null) {
            layoutParams = new FrameLayout.LayoutParams(this.mContentPanelWidth, this.mContentPanelHeight);
        } else {
            layoutParams = (FrameLayout.LayoutParams) this.mContentPanel.getLayoutParams();
            layoutParams.width = this.mContentPanelWidth;
            layoutParams.height = this.mContentPanelHeight;
        }
        layoutParams.topMargin = this.mPanelMargins.top;
        layoutParams.bottomMargin = this.mPanelMargins.bottom;
        layoutParams.leftMargin = this.mPanelMargins.left;
        layoutParams.rightMargin = this.mPanelMargins.right;
        this.mContentPanel.setLayoutParams(layoutParams);
        ViewGroup viewGroup3 = this.mContentPanel;
        if (viewGroup3 == null || viewGroup3.getParent() != null) {
            return;
        }
        this.mRootView.addView(this.mContentPanel);
    }

    private void adjustContentWrapperScrollable() {
        if (this.mActionSheetScrollView == null || this.mAlertContentWrapper == null || this.mListView == null || this.mContentContainer == null) {
            return;
        }
        Point screenSize = EnvStateManager.getScreenSize(this.mContext);
        float fMax = (this.mRootViewSize.y * 1.0f) / Math.max(screenSize.y, 1);
        if (!Build.IS_TABLET && screenSize.y > screenSize.x && fMax < 0.35f) {
            this.mActionSheetScrollView.setVisibility(0);
            ViewCompat.setNestedScrollingEnabled(this.mListView, true);
            safeMoveView(this.mAlertContentWrapper, this.mActionSheetScrollView);
        } else {
            ViewCompat.setNestedScrollingEnabled(this.mListView, false);
            safeMoveView(this.mAlertContentWrapper, this.mContentContainer);
            this.mActionSheetScrollView.setVisibility(8);
        }
    }

    protected void safeMoveView(View view, ViewGroup viewGroup) {
        ViewGroup viewGroup2;
        if (view == null || (viewGroup2 = (ViewGroup) view.getParent()) == viewGroup) {
            return;
        }
        if (viewGroup2 != null) {
            viewGroup2.removeView(view);
        }
        viewGroup.addView(view);
    }

    private void adjustAlertMessageMaxLine() {
        if (this.mMessageView == null) {
            return;
        }
        Point screenSize = EnvStateManager.getScreenSize(this.mContext);
        boolean z = !Build.IS_TABLET && screenSize.y > screenSize.x;
        float fMax = (this.mRootViewSize.y * 1.0f) / Math.max(screenSize.y, 1);
        int i = MiuixUIUtils.getFontLevel(this.mContext) != 2 ? 2 : 1;
        if (z && fMax < 0.33f) {
            this.mMessageView.setMaxLines(i);
        } else {
            this.mMessageView.setMaxLines(3);
        }
    }

    private void prepareTypeColorMap(Context context) {
        if (this.mActionItemTypes == null || context == null) {
            return;
        }
        int[] iArr = {R.color.miuix_appcompat_dialog_list_text_light, R.color.miuix_appcompat_action_sheet_item_text_error_color_light};
        int[] iArr2 = {R.color.miuix_appcompat_dialog_list_text_dark, R.color.miuix_appcompat_action_sheet_item_text_error_color_dark};
        this.mTypeColorMap.clear();
        for (ActionSheet.ActionSheetItemType actionSheetItemType : this.mActionItemTypes) {
            int iOrdinal = actionSheetItemType.ordinal();
            this.mTypeColorMap.put(actionSheetItemType, Integer.valueOf(ViewUtils.isNightMode(context) ? iArr2[iOrdinal] : iArr[iOrdinal]));
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        this.mIsFlipTinyScreen = Build.IS_FLIP && DeviceHelper.isTinyScreen(this.mContext);
        this.mIsInFreeForm = EnvStateManager.isFreeFormMode(this.mContext);
        updateRootViewSizeByWindow();
        if (this.mWindow.getDecorView().isAttachedToWindow()) {
            updateWindowCutoutMode();
            setupContentView(true);
        }
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            WindowInsets rootWindowInsets = this.mWindow.getDecorView().getRootWindowInsets();
            if (rootWindowInsets != null) {
                updateContentPanelMarginByWindowInsetsListener(rootWindowInsets);
            }
            this.mRootView.post(new Runnable() { // from class: miuix.internal.widget.ActionSheetController.6
                @Override // java.lang.Runnable
                public void run() {
                    WindowInsets rootWindowInsets2 = ActionSheetController.this.mWindow.getDecorView().getRootWindowInsets();
                    if (rootWindowInsets2 != null) {
                        ActionSheetController.this.updateContentPanelMarginByWindowInsetsListener(rootWindowInsets2);
                    }
                }
            });
        }
    }

    public void onAttachedToWindow() {
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            configWindow();
        }
    }

    private void configWindow() {
        this.mWindow.setSoftInputMode((this.mWindow.getAttributes().softInputMode & 15) | 48);
        this.mWindow.getDecorView().setWindowInsetsAnimationCallback(new WindowInsetsAnimation.Callback(1) { // from class: miuix.internal.widget.ActionSheetController.7
            @Override // android.view.WindowInsetsAnimation.Callback
            public WindowInsets onProgress(WindowInsets windowInsets, List<WindowInsetsAnimation> list) {
                return windowInsets;
            }

            @Override // android.view.WindowInsetsAnimation.Callback
            public void onPrepare(WindowInsetsAnimation windowInsetsAnimation) {
                super.onPrepare(windowInsetsAnimation);
                if (windowInsetsAnimation == null || (windowInsetsAnimation.getTypeMask() & WindowInsets.Type.ime()) <= 0) {
                    return;
                }
                ActionSheetController.this.mDialogAnimHelper.cancelAnimator();
            }

            @Override // android.view.WindowInsetsAnimation.Callback
            public WindowInsetsAnimation.Bounds onStart(WindowInsetsAnimation windowInsetsAnimation, WindowInsetsAnimation.Bounds bounds) {
                return super.onStart(windowInsetsAnimation, bounds);
            }

            @Override // android.view.WindowInsetsAnimation.Callback
            public void onEnd(WindowInsetsAnimation windowInsetsAnimation) {
                super.onEnd(windowInsetsAnimation);
                WindowInsets rootWindowInsets = ActionSheetController.this.mWindow.getDecorView().getRootWindowInsets();
                if (rootWindowInsets != null) {
                    ActionSheetController.this.updateContentPanelMarginByWindowInsetsListener(rootWindowInsets);
                }
            }
        });
        this.mWindow.getDecorView().setOnApplyWindowInsetsListener(new AnonymousClass8());
        this.mSetupWindowInsetsAnimation = true;
    }

    /* JADX INFO: renamed from: miuix.internal.widget.ActionSheetController$8, reason: invalid class name */
    class AnonymousClass8 implements View.OnApplyWindowInsetsListener {
        AnonymousClass8() {
        }

        @Override // android.view.View.OnApplyWindowInsetsListener
        public WindowInsets onApplyWindowInsets(final View view, WindowInsets windowInsets) {
            ActionSheetController.this.updateContentPanelMarginByWindowInsetsListener(windowInsets);
            view.post(new Runnable() { // from class: miuix.internal.widget.ActionSheetController$8$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1857xf6ce287b(view);
                }
            });
            return WindowInsets.CONSUMED;
        }

        /* JADX INFO: renamed from: lambda$onApplyWindowInsets$0$miuix-internal-widget-ActionSheetController$8, reason: not valid java name */
        /* synthetic */ void m1857xf6ce287b(View view) {
            WindowInsets rootWindowInsets = view.getRootWindowInsets();
            if (rootWindowInsets != null) {
                ActionSheetController.this.updateContentPanelMarginByWindowInsetsListener(rootWindowInsets);
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

    public void dismiss(DialogAnimHelper.OnDismiss onDismiss) {
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            cleanWindowInsetsAnimation();
        }
        ViewGroup viewGroup = this.mContentPanel;
        if (viewGroup == null) {
            if (onDismiss != null) {
                onDismiss.end();
            }
        } else {
            if (viewGroup.isAttachedToWindow()) {
                checkAndClearFocus();
                this.mDialogAnimHelper.executeDismissAnim(this.mContentPanel, useTabletAnim(), this.mDimBg, onDismiss);
                return;
            }
            Log.d(TAG, "dialog is not attached to window when dismiss is invoked");
            try {
                ((AlertDialog) this.mDialog).realDismiss();
            } catch (IllegalArgumentException e) {
                Log.wtf(TAG, "Not catch the dialog will throw the illegalArgumentException (In Case cause the crash , we expect it should be caught)", e);
            }
        }
    }

    protected void checkAndClearFocus() {
        View currentFocus = this.mWindow.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    public void onStart() {
        updateDimBgBottomMargin(0);
        updateWindowCutoutMode();
        if (!this.mIsFromRebuild && this.mEnableEnterAnim) {
            this.mDialogAnimHelper.executeShowAnim(this.mContentPanel, this.mDimBg, useTabletAnim(), false, this.mShowAnimListenerWrapper);
            return;
        }
        View view = this.mDimBg;
        if (view != null) {
            view.setAlpha(ViewUtils.isNightMode(this.mContext) ? DimToken.DIM_DARK : DimToken.DIM_LIGHT);
        }
    }

    private void updateWindowCutoutMode() {
        int screenOrientation = getScreenOrientation();
        if (android.os.Build.VERSION.SDK_INT <= 30 || this.mScreenOrientation == screenOrientation) {
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
        int i = getScreenOrientation() != 2 ? 3 : 2;
        if (this.mWindow.getAttributes().layoutInDisplayCutoutMode != i) {
            this.mWindow.getAttributes().layoutInDisplayCutoutMode = i;
            View decorView2 = this.mWindow.getDecorView();
            if (this.mDialog.isShowing() && decorView2.isAttachedToWindow()) {
                this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), this.mWindow.getAttributes());
            }
        }
    }

    private boolean useTabletAnim() {
        return Build.IS_TABLET && this.mMode == ActionSheet.ActionSheetMode.ARROW_MODE;
    }

    protected boolean isShowingAnimation() {
        return this.mEnableEnterAnim && this.mIsDialogAnimating;
    }

    protected void setPendingDismiss(boolean z) {
        this.mHasPendingDismiss = z;
    }

    private void updateDimBgBottomMargin(int i) {
        View view = this.mDimBg;
        if (view == null) {
            return;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (marginLayoutParams.bottomMargin != i) {
            marginLayoutParams.bottomMargin = i;
            this.mDimBg.requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContentPanelMarginByWindowInsetsListener(WindowInsets windowInsets) {
        boolean z;
        int width = this.mRootView.getWidth() == 0 ? this.mRootViewSize.x : this.mRootView.getWidth();
        int height = this.mRootView.getHeight() == 0 ? this.mRootViewSize.y : this.mRootView.getHeight();
        int[] iArrCalcVerticalMargin = this.mContentController.calcVerticalMargin(this.mContext, height, windowInsets);
        int[] iArrCalcHorizontalMargin = this.mContentController.calcHorizontalMargin(this.mContext, width, windowInsets);
        int iCalcContentPanelWidth = this.mContentController.calcContentPanelWidth(this.mContext, this.mRootView, this.mContentPanel, width, windowInsets);
        int iCalcContentPanelHeight = this.mContentController.calcContentPanelHeight(this.mContext, this.mRootView, this.mContentPanel, height, windowInsets);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mContentPanel.getLayoutParams();
        boolean z2 = true;
        if (layoutParams.width != iCalcContentPanelWidth) {
            layoutParams.width = iCalcContentPanelWidth;
            z = true;
        } else {
            z = false;
        }
        if (layoutParams.height != iCalcContentPanelHeight) {
            layoutParams.height = iCalcContentPanelHeight;
            z = true;
        }
        int i = layoutParams.topMargin;
        int i2 = iArrCalcVerticalMargin[0];
        if (i != i2) {
            layoutParams.topMargin = i2;
            this.mPanelMargins.top = iArrCalcVerticalMargin[0];
            z = true;
        }
        int i3 = layoutParams.bottomMargin;
        int i4 = iArrCalcVerticalMargin[1];
        if (i3 != i4) {
            layoutParams.bottomMargin = i4;
            this.mPanelMargins.bottom = iArrCalcVerticalMargin[1];
            z = true;
        }
        int i5 = layoutParams.leftMargin;
        int i6 = iArrCalcHorizontalMargin[0];
        if (i5 != i6) {
            layoutParams.leftMargin = i6;
            this.mPanelMargins.left = iArrCalcHorizontalMargin[0];
            z = true;
        }
        int i7 = layoutParams.rightMargin;
        int i8 = iArrCalcHorizontalMargin[1];
        if (i7 != i8) {
            layoutParams.rightMargin = i8;
            this.mPanelMargins.right = iArrCalcHorizontalMargin[1];
        } else {
            z2 = z;
        }
        this.mRootView.setContentPanelExtraBounds(this.mPanelMargins);
        if (z2) {
            this.mContentPanel.requestLayout();
        }
    }

    private void setupContent() {
        TextView textView;
        if (this.mContentPanel == null) {
            return;
        }
        if (this.mMode == ActionSheet.ActionSheetMode.ALERT_MODE) {
            this.mActionSheetScrollView = (NestedScrollView) this.mContentPanel.findViewById(R.id.actionSheetScrollView);
            this.mAlertContentWrapper = (LinearLayout) this.mContentPanel.findViewById(R.id.alertContentWrapper);
        }
        this.mContentContainer = (LinearLayout) this.mContentPanel.findViewById(R.id.action_sheet_content_container);
        this.mMessageView = (TextView) this.mContentPanel.findViewById(R.id.action_sheet_message);
        if (TextUtils.isEmpty(this.mMessage)) {
            this.mMessage = this.mContext.getString(R.string.miuix_appcompat_action_sheet_default_message);
        }
        if (!TextUtils.isEmpty(this.mMessage) && (textView = this.mMessageView) != null) {
            textView.setText(this.mMessage);
            if (this.mMessageView.getVisibility() == 8) {
                this.mMessageView.setVisibility(0);
            }
        } else {
            TextView textView2 = this.mMessageView;
            if (textView2 != null && textView2.getVisibility() == 0) {
                this.mMessageView.setVisibility(8);
            }
        }
        setupListView();
        if (this.mMode == ActionSheet.ActionSheetMode.ALERT_MODE) {
            this.mSeparateButton = (Button) this.mContentPanel.findViewById(R.id.action_sheet_cancel_button);
        }
        if (TextUtils.isEmpty(this.mSeparateButtonText)) {
            this.mSeparateButtonText = this.mContext.getString(R.string.miuix_appcompat_cancel_description);
        }
        Button button = this.mSeparateButton;
        if (button != null) {
            button.setText(this.mSeparateButtonText);
            this.mSeparateButton.setOnClickListener(new View.OnClickListener() { // from class: miuix.internal.widget.ActionSheetController.9
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (view != null) {
                        HapticCompat.performHapticFeedbackAsync(view, HapticFeedbackConstants.MIUI_BUTTON_MIDDLE, HapticFeedbackConstants.MIUI_TAP_LIGHT);
                    }
                    if (ActionSheetController.this.mSeparateButtonClickListener != null) {
                        ActionSheetController.this.mSeparateButtonClickListener.onClick(ActionSheetController.this.mDialog, -2);
                    }
                    ActionSheetController.this.handleDismiss();
                }
            });
        }
    }

    private void setupListView() {
        ViewGroup viewGroup = this.mContentPanel;
        if (viewGroup == null) {
            return;
        }
        this.mSpringBackLayout = (SpringBackLayout) viewGroup.findViewById(R.id.actionSheetSpringBack);
        ListView listView = (ListView) this.mContentPanel.findViewById(R.id.action_sheet_list_view);
        this.mListView = listView;
        if (this.mItemClickListener != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.internal.widget.ActionSheetController.10
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    if (view.isEnabled()) {
                        ActionSheetController.this.mItemClickListener.onClick(ActionSheetController.this.mDialog, i);
                        ActionSheetController.this.handleDismiss();
                    }
                }
            });
        }
        ListAdapter listAdapter = this.mListViewAdapter;
        ListAdapter listAdapter2 = listAdapter;
        if (listAdapter == null) {
            ActionSheetListViewAdapter actionSheetListViewAdapter = new ActionSheetListViewAdapter(this.mContext, this.mListItemLayout, android.R.id.text1, this.mActionItems, this.mActionItemTypes, this.mTypeColorMap);
            actionSheetListViewAdapter.setProvider(this.mItemProvider);
            listAdapter2 = actionSheetListViewAdapter;
        }
        this.mListView.setAdapter(listAdapter2);
    }

    private void adjustSpringEnabled() {
        ListView listView;
        if (this.mSpringBackLayout == null || (listView = this.mListView) == null) {
            return;
        }
        listView.post(new Runnable() { // from class: miuix.internal.widget.ActionSheetController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1856xa7725957();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$adjustSpringEnabled$0$miuix-internal-widget-ActionSheetController, reason: not valid java name */
    /* synthetic */ void m1856xa7725957() {
        this.mSpringBackLayout.setSpringBackEnable(true ^ (getVisibleItemTotalHeight(this.mListView, (this.mListView.getLastVisiblePosition() - this.mListView.getFirstVisiblePosition()) + 1) == this.mListView.getHeight()));
    }

    private int getVisibleItemTotalHeight(ListView listView, int i) {
        if (listView == null || i <= 0) {
            return 0;
        }
        int height = 0;
        for (int i2 = 0; i2 < Math.min(listView.getChildCount(), i); i2++) {
            View childAt = listView.getChildAt(i2);
            if (childAt != null) {
                height += childAt.getHeight();
            }
        }
        return height;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDismiss() {
        this.mDialog.dismiss();
    }

    private void postUpdateRootViewSize(View view) {
        if (view == null) {
            return;
        }
        this.mRootViewSize.x = view.getWidth();
        this.mRootViewSize.y = view.getHeight();
        float f = this.mContext.getResources().getDisplayMetrics().density;
        this.mRootViewSizeDp.x = (int) (this.mRootViewSize.x / f);
        this.mRootViewSizeDp.y = (int) (this.mRootViewSize.y / f);
    }

    private void updateRootViewSizeByWindow() {
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(this.mContext);
        this.mRootViewSize.x = windowInfo.windowSize.x;
        this.mRootViewSize.y = windowInfo.windowSize.y;
        this.mRootViewSizeDp.x = windowInfo.windowSizeDp.x;
        this.mRootViewSizeDp.y = windowInfo.windowSizeDp.y;
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

    private int getScreenOrientation() {
        WindowManager windowManager = this.mWindowManager;
        if (windowManager == null) {
            return 0;
        }
        int rotation = windowManager.getDefaultDisplay().getRotation();
        return (rotation == 1 || rotation == 3) ? 2 : 1;
    }

    private static class ActionSheetListViewAdapter extends ArrayAdapter<CharSequence> {
        private final Context mContext;
        private final ActionSheet.ActionSheetItemType[] mItemTypes;
        private AccessibilityDelegateProvider mProvider;
        private final Map<ActionSheet.ActionSheetItemType, Integer> mTypeColorMap;

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        public ActionSheetListViewAdapter(Context context, int i, int i2, CharSequence[] charSequenceArr, ActionSheet.ActionSheetItemType[] actionSheetItemTypeArr, Map<ActionSheet.ActionSheetItemType, Integer> map) {
            super(context, i, i2, charSequenceArr);
            this.mContext = context;
            this.mItemTypes = actionSheetItemTypeArr;
            this.mTypeColorMap = map;
        }

        public void setProvider(AccessibilityDelegateProvider accessibilityDelegateProvider) {
            this.mProvider = accessibilityDelegateProvider;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            adjustTypedItemColor((TextView) view2.findViewById(android.R.id.text1), i);
            if (view == null) {
                AnimHelper.addPressAnim(view2);
            }
            AccessibilityDelegateProvider accessibilityDelegateProvider = this.mProvider;
            if (accessibilityDelegateProvider != null) {
                ActionSheetController.setAccessibilityDelegate(view2, accessibilityDelegateProvider);
            }
            return view2;
        }

        private void adjustTypedItemColor(TextView textView, int i) {
            Map<ActionSheet.ActionSheetItemType, Integer> map;
            Integer num;
            ActionSheet.ActionSheetItemType[] actionSheetItemTypeArr = this.mItemTypes;
            if (actionSheetItemTypeArr == null || textView == null || (map = this.mTypeColorMap) == null || (num = map.get(actionSheetItemTypeArr[i])) == null) {
                return;
            }
            textView.setTextColor(this.mContext.getResources().getColor(num.intValue()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setAccessibilityDelegate(View view, AccessibilityDelegateProvider accessibilityDelegateProvider) {
        AccessibilityDelegateCompat defaultAccessibilityDelegateCompat;
        if (accessibilityDelegateProvider != null) {
            defaultAccessibilityDelegateCompat = accessibilityDelegateProvider.getAccessibilityDelegate();
        } else {
            defaultAccessibilityDelegateCompat = getDefaultAccessibilityDelegateCompat();
        }
        if (defaultAccessibilityDelegateCompat != null) {
            ViewCompat.setAccessibilityDelegate(view, defaultAccessibilityDelegateCompat);
        }
    }

    private static AccessibilityDelegateCompat getDefaultAccessibilityDelegateCompat() {
        return new AccessibilityDelegateCompat() { // from class: miuix.internal.widget.ActionSheetController.11
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.addAction(16);
            }
        };
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return keyEvent.getKeyCode() == 82;
    }

    public void onDetachedFromWindow() {
        if (AnimHelper.isDialogDebugInAndroidUIThreadEnabled()) {
            return;
        }
        Folme.clean(this.mDimBg);
        Folme.clean(this.mContentPanel);
        List<View> listItemViews = getListItemViews(this.mListView);
        int size = (listItemViews == null || listItemViews.isEmpty()) ? 0 : listItemViews.size();
        for (int i = 0; i < size; i++) {
            Folme.clean(listItemViews.get(i));
        }
        translateContentPanel(0);
    }

    private List<View> getListItemViews(ListView listView) {
        if (listView == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < listView.getChildCount(); i++) {
            View childAt = listView.getChildAt(i);
            if (childAt != null) {
                arrayList.add(childAt);
            }
        }
        return arrayList;
    }

    protected void translateContentPanel(int i) {
        ViewGroup viewGroup = this.mContentPanel;
        if (viewGroup == null) {
            return;
        }
        viewGroup.animate().cancel();
        this.mContentPanel.setTranslationY(i);
    }
}
