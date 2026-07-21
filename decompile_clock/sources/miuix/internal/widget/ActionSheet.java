package miuix.internal.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import miuix.appcompat.app.AccessibilityDelegateProvider;
import miuix.appcompat.app.AlertDialog;
import miuix.core.util.EnvStateManager;
import miuix.os.Build;

/* JADX INFO: loaded from: classes2.dex */
public class ActionSheet {
    private static final int SHOW_ARROW_ACTION_WIDTH_DP = 747;

    public enum ActionSheetItemType {
        PRIMARY_ITEM,
        ERROR_ITEM
    }

    public enum ActionSheetMode {
        ALERT_MODE,
        ARROW_MODE
    }

    public enum ArrowMode {
        ARROW_TOP_MODE,
        ARROW_TOP_LEFT_MODE,
        ARROW_TOP_RIGHT_MODE,
        ARROW_LEFT_TOP_MODE,
        ARROW_LEFT_BOTTOM_MODE,
        ARROW_BOTTOM_MODE,
        ARROW_BOTTOM_LEFT_MODE,
        ARROW_BOTTOM_RIGHT_MODE,
        ARROW_RIGHT_TOP_MODE,
        ARROW_RIGHT_BOTTOM_MODE,
        ARROW_LEFT_MODE,
        ARROW_RIGHT_MODE,
        ARROW_MODE_NONE
    }

    public interface ContentController {
        int calcContentPanelHeight(Context context, ViewGroup viewGroup, ViewGroup viewGroup2, int i, WindowInsets windowInsets);

        int calcContentPanelWidth(Context context, ViewGroup viewGroup, ViewGroup viewGroup2, int i, WindowInsets windowInsets);

        int[] calcHorizontalMargin(Context context, int i, WindowInsets windowInsets);

        int[] calcVerticalMargin(Context context, int i, WindowInsets windowInsets);

        Point computeContentPosition(Rect rect, Point point, Point point2, ViewGroup viewGroup, ViewGroup viewGroup2);

        View getArrowAnchor();

        ArrowMode getArrowMode();
    }

    public interface IActionSheet {
        void dismiss();

        void dismissWithoutAnimation();

        ListView getListView();

        TextView getMessageView();

        TextView getSeparateView();

        void show();
    }

    public static class Builder {
        private ActionSheetItemType[] mActionItemTypes;
        private CharSequence[] mActionItems;
        private View mAnchor;
        private Point mArrowActionOffset;
        private ArrowMode mArrowMode;
        private boolean mCanceledOnTouchOutside;
        private AlertDialog.OnConfigurationChangedListener mConfigurationChangedListener;
        private Context mContext;
        private boolean mEnterAnimEnabled;
        protected boolean mHapticFeedbackEnabled;
        private DialogInterface.OnClickListener mItemClickListener;
        private AccessibilityDelegateProvider mItemsProvider;
        private ListAdapter mListAdapter;
        private CharSequence mMessage;
        private DialogInterface.OnCancelListener mOnCancelListener;
        private DialogInterface.OnDismissListener mOnDismissListener;
        private DialogInterface.OnKeyListener mOnKeyListener;
        private DialogInterface.OnShowListener mOnShowListener;
        private DialogInterface.OnClickListener mSeparateClickListener;
        private CharSequence mSeparateText;
        private AlertDialog.OnDialogShowAnimListener mShowAnimListener;
        private int mThemeResId;

        public Builder(Context context) {
            this(context, 0);
        }

        public Builder(Context context, int i) {
            this.mArrowMode = ArrowMode.ARROW_MODE_NONE;
            this.mEnterAnimEnabled = true;
            this.mCanceledOnTouchOutside = true;
            this.mHapticFeedbackEnabled = true;
            this.mContext = context;
            this.mThemeResId = i;
        }

        public Builder setMessage(CharSequence charSequence) {
            this.mMessage = charSequence;
            return this;
        }

        public Builder setSeparateText(CharSequence charSequence) {
            this.mSeparateText = charSequence;
            return this;
        }

        public Builder setActionItems(int i, DialogInterface.OnClickListener onClickListener) {
            this.mActionItems = this.mContext.getResources().getTextArray(i);
            this.mItemClickListener = onClickListener;
            return this;
        }

        public Builder setActionItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
            this.mActionItems = charSequenceArr;
            this.mItemClickListener = onClickListener;
            return this;
        }

        public Builder setActionItems(CharSequence[] charSequenceArr, ActionSheetItemType[] actionSheetItemTypeArr, DialogInterface.OnClickListener onClickListener) {
            this.mActionItems = charSequenceArr;
            this.mItemClickListener = onClickListener;
            this.mActionItemTypes = actionSheetItemTypeArr;
            return this;
        }

        public Builder setItemAccessibilityProvider(AccessibilityDelegateProvider accessibilityDelegateProvider) {
            this.mItemsProvider = accessibilityDelegateProvider;
            return this;
        }

        public Builder setShowAnimListener(AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
            this.mShowAnimListener = onDialogShowAnimListener;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            this.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnShowListener(DialogInterface.OnShowListener onShowListener) {
            this.mOnShowListener = onShowListener;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            this.mOnKeyListener = onKeyListener;
            return this;
        }

        public Builder setSeparateClickListener(DialogInterface.OnClickListener onClickListener) {
            this.mSeparateClickListener = onClickListener;
            return this;
        }

        public Builder setAnchorView(View view) {
            this.mAnchor = view;
            return this;
        }

        public Builder setArrowMode(ArrowMode arrowMode) {
            this.mArrowMode = arrowMode;
            return this;
        }

        public Builder setEnableEnterAnim(boolean z) {
            this.mEnterAnimEnabled = z;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean z) {
            this.mCanceledOnTouchOutside = z;
            return this;
        }

        public Builder setHapticFeedbackEnabled(boolean z) {
            this.mHapticFeedbackEnabled = z;
            return this;
        }

        public Builder setArrowActionOffset(Point point) {
            this.mArrowActionOffset = point;
            return this;
        }

        public Builder setListViewAdapter(ListAdapter listAdapter) {
            this.mListAdapter = listAdapter;
            return this;
        }

        public Builder setOnConfigurationChangedListener(AlertDialog.OnConfigurationChangedListener onConfigurationChangedListener) {
            this.mConfigurationChangedListener = onConfigurationChangedListener;
            return this;
        }

        public IActionSheet create() {
            if (ActionSheet.showArrowActionSheet(this.mContext) && this.mAnchor != null && this.mArrowMode != ArrowMode.ARROW_MODE_NONE) {
                return createArrowActionSheet();
            }
            return createAlertActionSheet();
        }

        private IActionSheet createAlertActionSheet() {
            AlertActionSheet alertActionSheet = new AlertActionSheet(this.mContext, this.mThemeResId);
            alertActionSheet.setMessage(this.mMessage);
            alertActionSheet.setActionItems(this.mActionItems, this.mActionItemTypes, this.mItemClickListener);
            alertActionSheet.setEnableEnterAnim(this.mEnterAnimEnabled);
            alertActionSheet.setCanceledOnTouchOutside(this.mCanceledOnTouchOutside);
            alertActionSheet.setHapticFeedbackEnabled(this.mHapticFeedbackEnabled);
            alertActionSheet.setSeparateButtonText(this.mSeparateText);
            alertActionSheet.setArrowActionAnchor(this.mAnchor);
            alertActionSheet.setArrowMode(this.mArrowMode);
            AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener = this.mShowAnimListener;
            if (onDialogShowAnimListener != null) {
                alertActionSheet.setOnShowAnimListener(onDialogShowAnimListener);
            }
            DialogInterface.OnShowListener onShowListener = this.mOnShowListener;
            if (onShowListener != null) {
                alertActionSheet.setActionSheetOnShowListener(onShowListener);
            }
            DialogInterface.OnDismissListener onDismissListener = this.mOnDismissListener;
            if (onDismissListener != null) {
                alertActionSheet.setActionSheetOnDismissListener(onDismissListener);
            }
            DialogInterface.OnKeyListener onKeyListener = this.mOnKeyListener;
            if (onKeyListener != null) {
                alertActionSheet.setOnKeyListener(onKeyListener);
                alertActionSheet.setActionSheetOnKeyListener(this.mOnKeyListener);
            }
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                alertActionSheet.setListViewAdapter(listAdapter);
            }
            DialogInterface.OnClickListener onClickListener = this.mSeparateClickListener;
            if (onClickListener != null) {
                alertActionSheet.setSeparateClickListener(onClickListener);
            }
            DialogInterface.OnCancelListener onCancelListener = this.mOnCancelListener;
            if (onCancelListener != null) {
                alertActionSheet.setOnCancelListener(onCancelListener);
                alertActionSheet.setActionSheetOnCancelListener(this.mOnCancelListener);
            }
            AlertDialog.OnConfigurationChangedListener onConfigurationChangedListener = this.mConfigurationChangedListener;
            if (onConfigurationChangedListener != null) {
                alertActionSheet.setConfigurationChangedListener(onConfigurationChangedListener);
            }
            AccessibilityDelegateProvider accessibilityDelegateProvider = this.mItemsProvider;
            if (accessibilityDelegateProvider != null) {
                alertActionSheet.setItemAccessibilityProvider(accessibilityDelegateProvider);
            }
            return alertActionSheet;
        }

        private IActionSheet createArrowActionSheet() {
            ArrowActionSheet arrowActionSheet = new ArrowActionSheet(this.mContext, this.mThemeResId, this.mAnchor);
            arrowActionSheet.setMessage(this.mMessage);
            arrowActionSheet.setActionItems(this.mActionItems, this.mActionItemTypes, this.mItemClickListener);
            arrowActionSheet.setArrowMode(this.mArrowMode);
            arrowActionSheet.setEnableEnterAnim(this.mEnterAnimEnabled);
            arrowActionSheet.setCanceledOnTouchOutside(this.mCanceledOnTouchOutside);
            arrowActionSheet.setHapticFeedbackEnabled(this.mHapticFeedbackEnabled);
            AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener = this.mShowAnimListener;
            if (onDialogShowAnimListener != null) {
                arrowActionSheet.setOnShowAnimListener(onDialogShowAnimListener);
            }
            DialogInterface.OnShowListener onShowListener = this.mOnShowListener;
            if (onShowListener != null) {
                arrowActionSheet.setActionSheetOnShowListener(onShowListener);
            }
            DialogInterface.OnDismissListener onDismissListener = this.mOnDismissListener;
            if (onDismissListener != null) {
                arrowActionSheet.setActionSheetOnDismissListener(onDismissListener);
            }
            DialogInterface.OnKeyListener onKeyListener = this.mOnKeyListener;
            if (onKeyListener != null) {
                arrowActionSheet.setOnKeyListener(onKeyListener);
                arrowActionSheet.setActionSheetOnKeyListener(this.mOnKeyListener);
            }
            Point point = this.mArrowActionOffset;
            if (point != null) {
                arrowActionSheet.setOffset(point.x, this.mArrowActionOffset.y);
            }
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                arrowActionSheet.setListViewAdapter(listAdapter);
            }
            DialogInterface.OnCancelListener onCancelListener = this.mOnCancelListener;
            if (onCancelListener != null) {
                arrowActionSheet.setOnCancelListener(onCancelListener);
                arrowActionSheet.setActionSheetOnCancelListener(this.mOnCancelListener);
            }
            AlertDialog.OnConfigurationChangedListener onConfigurationChangedListener = this.mConfigurationChangedListener;
            if (onConfigurationChangedListener != null) {
                arrowActionSheet.setConfigurationChangedListener(onConfigurationChangedListener);
            }
            AccessibilityDelegateProvider accessibilityDelegateProvider = this.mItemsProvider;
            if (accessibilityDelegateProvider != null) {
                arrowActionSheet.setItemAccessibilityProvider(accessibilityDelegateProvider);
            }
            return arrowActionSheet;
        }

        public IActionSheet show() {
            IActionSheet iActionSheetCreate = create();
            iActionSheetCreate.show();
            return iActionSheetCreate;
        }
    }

    public static boolean showArrowActionSheet(Context context) {
        return Build.IS_TABLET && EnvStateManager.getWindowInfo(context).windowSizeDp.x >= 747;
    }
}
