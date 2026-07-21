package miuix.internal.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import miuix.appcompat.R;
import miuix.appcompat.app.AccessibilityDelegateProvider;
import miuix.appcompat.app.AlertDialog;
import miuix.autodensity.DensityUtil;
import miuix.core.util.EnvStateManager;
import miuix.internal.util.ViewUtils;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes2.dex */
public class ArrowActionSheet extends AlertDialog implements ActionSheet.IActionSheet {
    final ActionSheetController mActionController;
    private final View mAnchorView;
    private int mArrowIconLongSide;
    private int mArrowIconShortSide;
    private int mArrowLinkOffset;
    private ActionSheet.ArrowMode mArrowMode;
    private ActionSheet.ContentController mContentController;
    private Context mContext;
    private int mDefaultMargin;
    private int mFixedWidth;
    private boolean mIsDismissForShift;
    private boolean mIsFromAlertShape;
    private Point mOffset;
    private int mOffsetToPoint;

    protected ArrowActionSheet(Context context, View view) {
        this(context, 0, view);
    }

    protected ArrowActionSheet(Context context, int i, View view) {
        super(context, i);
        this.mOffset = new Point();
        this.mActionController = new ActionSheetController(context, this, getWindow(), ActionSheet.ActionSheetMode.ARROW_MODE);
        this.mAnchorView = view;
        this.mContext = context;
        init(context);
    }

    protected ArrowActionSheet(Context context, View view, boolean z, DialogInterface.OnCancelListener onCancelListener) {
        this(context, 0, view);
        setCancelable(z);
        setOnCancelListener(onCancelListener);
    }

    protected AlertActionSheet createAlertActionSheet(View view) {
        AlertActionSheet alertActionSheet = new AlertActionSheet(this.mContext);
        alertActionSheet.setArrowActionAnchor(view);
        alertActionSheet.setArrowMode(this.mArrowMode);
        alertActionSheet.setCanceledOnTouchOutside(isCanceledOnTouchOutside());
        alertActionSheet.setHapticFeedbackEnabled(isHapticFeedbackEnabled());
        if (this.mActionController.getMessage() != null) {
            alertActionSheet.setMessage(this.mActionController.getMessage());
        }
        if (this.mActionController.getActionItems() != null && this.mActionController.getItemClickListener() != null) {
            alertActionSheet.setActionItems(this.mActionController.getActionItems(), this.mActionController.getItemClickListener());
        }
        if (this.mActionController.getActionItems() != null && this.mActionController.getItemClickListener() != null && this.mActionController.getItemTypes() != null) {
            alertActionSheet.setActionItems(this.mActionController.getActionItems(), this.mActionController.getItemTypes(), this.mActionController.getItemClickListener());
        }
        if (this.mActionController.getShowAnimListener() != null) {
            alertActionSheet.setOnShowAnimListener(this.mActionController.getShowAnimListener());
        }
        if (this.mActionController.getOnShowListener() != null) {
            alertActionSheet.setActionSheetOnShowListener(this.mActionController.getOnShowListener());
        }
        if (this.mActionController.getOnDismissListener() != null) {
            alertActionSheet.setActionSheetOnDismissListener(this.mActionController.getOnDismissListener());
        }
        if (this.mActionController.getOnKeyListener() != null) {
            alertActionSheet.setOnKeyListener(this.mActionController.getOnKeyListener());
            alertActionSheet.setActionSheetOnKeyListener(this.mActionController.getOnKeyListener());
        }
        if (this.mActionController.getListViewAdapter() != null) {
            alertActionSheet.setListViewAdapter(this.mActionController.getListViewAdapter());
        }
        if (this.mActionController.getOnCancelListener() != null) {
            alertActionSheet.setOnCancelListener(this.mActionController.getOnCancelListener());
            alertActionSheet.setActionSheetOnCancelListener(this.mActionController.getOnCancelListener());
        }
        if (this.mActionController.getConfigurationChangedListener() != null) {
            alertActionSheet.setConfigurationChangedListener(this.mActionController.getConfigurationChangedListener());
        }
        if (this.mActionController.getItemProvider() != null) {
            alertActionSheet.setItemAccessibilityProvider(this.mActionController.getItemProvider());
        }
        return alertActionSheet;
    }

    private void init(Context context) {
        this.mArrowLinkOffset = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_arrow_icon_link_offset);
        this.mArrowIconLongSide = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_arrow_icon_long_side);
        this.mArrowIconShortSide = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_arrow_icon_short_side);
        this.mFixedWidth = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_content_arrow_fixed_width);
        this.mDefaultMargin = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_dialog_ime_margin);
        this.mOffsetToPoint = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_arrow_offset_to_point);
        setContentController();
    }

    private void setContentController() {
        ActionSheet.ContentController contentController = new ActionSheet.ContentController() { // from class: miuix.internal.widget.ArrowActionSheet.1
            @Override // miuix.internal.widget.ActionSheet.ContentController
            public int calcContentPanelHeight(Context context, ViewGroup viewGroup, ViewGroup viewGroup2, int i, WindowInsets windowInsets) {
                return -2;
            }

            @Override // miuix.internal.widget.ActionSheet.ContentController
            public ActionSheet.ArrowMode getArrowMode() {
                return ArrowActionSheet.this.mArrowMode;
            }

            @Override // miuix.internal.widget.ActionSheet.ContentController
            public View getArrowAnchor() {
                return ArrowActionSheet.this.mAnchorView;
            }

            @Override // miuix.internal.widget.ActionSheet.ContentController
            public Point computeContentPosition(Rect rect, Point point, Point point2, ViewGroup viewGroup, ViewGroup viewGroup2) {
                Point showPosition = getShowPosition(rect, point2, ArrowActionSheet.this.mArrowMode);
                Point pointReComputePosition = reComputePosition(rect, point2, viewGroup2, detectOverflow(rect, point, point2, viewGroup != null ? viewGroup.getRootWindowInsets() : null, showPosition));
                if (pointReComputePosition != null) {
                    showPosition.x = pointReComputePosition.x;
                    showPosition.y = pointReComputePosition.y;
                }
                return showPosition;
            }

            private Point reComputePosition(Rect rect, Point point, ViewGroup viewGroup, boolean[] zArr) {
                ActionSheet.ArrowMode arrowModeOverflowCompactStrategy = overflowCompactStrategy(zArr);
                if (arrowModeOverflowCompactStrategy != ArrowActionSheet.this.mArrowMode) {
                    return getShowPosition(rect, point, arrowModeOverflowCompactStrategy);
                }
                return null;
            }

            private ActionSheet.ArrowMode overflowCompactStrategy(boolean[] zArr) {
                ActionSheet.ArrowMode arrowMode = ArrowActionSheet.this.mArrowMode;
                boolean z = zArr[0];
                boolean z2 = zArr[1];
                boolean z3 = zArr[2];
                boolean z4 = zArr[3];
                switch (AnonymousClass2.$SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ArrowActionSheet.this.mArrowMode.ordinal()]) {
                    case 1:
                        if (!z || z3) {
                            return z4 ? ActionSheet.ArrowMode.ARROW_BOTTOM_LEFT_MODE : arrowMode;
                        }
                        return ActionSheet.ArrowMode.ARROW_LEFT_TOP_MODE;
                    case 2:
                        return z4 ? ActionSheet.ArrowMode.ARROW_BOTTOM_MODE : arrowMode;
                    case 3:
                        if (!z3 || z) {
                            return z4 ? ActionSheet.ArrowMode.ARROW_BOTTOM_RIGHT_MODE : arrowMode;
                        }
                        return ActionSheet.ArrowMode.ARROW_RIGHT_TOP_MODE;
                    case 4:
                    case 5:
                        if (!z3 || z4) {
                            return z3 ? ActionSheet.ArrowMode.ARROW_BOTTOM_LEFT_MODE : arrowMode;
                        }
                        return ActionSheet.ArrowMode.ARROW_TOP_LEFT_MODE;
                    case 6:
                    case 7:
                        if (!z || z4) {
                            return z ? ActionSheet.ArrowMode.ARROW_BOTTOM_RIGHT_MODE : arrowMode;
                        }
                        return ActionSheet.ArrowMode.ARROW_TOP_RIGHT_MODE;
                    case 8:
                        if (!z3 || z2) {
                            return (z3 || !z2) ? arrowMode : ActionSheet.ArrowMode.ARROW_LEFT_TOP_MODE;
                        }
                        return ActionSheet.ArrowMode.ARROW_BOTTOM_LEFT_MODE;
                    case 9:
                        if (!z || z2) {
                            return (z || !z2) ? arrowMode : ActionSheet.ArrowMode.ARROW_RIGHT_TOP_MODE;
                        }
                        return ActionSheet.ArrowMode.ARROW_BOTTOM_RIGHT_MODE;
                    case 10:
                        if (!z || z2) {
                            return z2 ? ActionSheet.ArrowMode.ARROW_LEFT_TOP_MODE : arrowMode;
                        }
                        return ActionSheet.ArrowMode.ARROW_LEFT_BOTTOM_MODE;
                    case 11:
                        return z2 ? ActionSheet.ArrowMode.ARROW_TOP_MODE : arrowMode;
                    case 12:
                        if (!z3 || z2) {
                            return z2 ? ActionSheet.ArrowMode.ARROW_RIGHT_TOP_MODE : arrowMode;
                        }
                        return ActionSheet.ArrowMode.ARROW_RIGHT_BOTTOM_MODE;
                    default:
                        return arrowMode;
                }
            }

            private Point getShowPosition(Rect rect, Point point, ActionSheet.ArrowMode arrowMode) {
                Point point2 = new Point();
                Point referencePoint = getReferencePoint(rect, arrowMode);
                Point showOffset = getShowOffset(arrowMode, point);
                point2.x = referencePoint.x + showOffset.x + ArrowActionSheet.this.mOffset.x;
                point2.y = referencePoint.y + showOffset.y + ArrowActionSheet.this.mOffset.y;
                handleReservedSpace(point2, arrowMode);
                return point2;
            }

            private void handleReservedSpace(Point point, ActionSheet.ArrowMode arrowMode) {
                boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(ArrowActionSheet.this.mAnchorView);
                switch (AnonymousClass2.$SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[arrowMode.ordinal()]) {
                    case 1:
                    case 2:
                    case 3:
                        point.y += ArrowActionSheet.this.mOffsetToPoint;
                        break;
                    case 4:
                    case 5:
                    case 8:
                        if (zIsLayoutRtl) {
                            point.x -= ArrowActionSheet.this.mOffsetToPoint;
                        } else {
                            point.x += ArrowActionSheet.this.mOffsetToPoint;
                        }
                        break;
                    case 6:
                    case 7:
                    case 9:
                        if (zIsLayoutRtl) {
                            point.x += ArrowActionSheet.this.mOffsetToPoint;
                        } else {
                            point.x -= ArrowActionSheet.this.mOffsetToPoint;
                        }
                        break;
                    case 10:
                    case 11:
                    case 12:
                        point.y -= ArrowActionSheet.this.mOffsetToPoint;
                        break;
                }
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            private Point getReferencePoint(Rect rect, ActionSheet.ArrowMode arrowMode) {
                Point point = new Point();
                boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(ArrowActionSheet.this.mAnchorView);
                switch (AnonymousClass2.$SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[arrowMode.ordinal()]) {
                    case 1:
                    case 2:
                    case 3:
                        point.x = rect.left + (ArrowActionSheet.this.mAnchorView.getWidth() / 2);
                        point.y = rect.top + ArrowActionSheet.this.mAnchorView.getHeight();
                        return point;
                    case 4:
                    case 5:
                    case 8:
                        if (!zIsLayoutRtl) {
                            point.x = rect.left + ArrowActionSheet.this.mAnchorView.getWidth();
                            point.y = rect.top + (ArrowActionSheet.this.mAnchorView.getHeight() / 2);
                        } else {
                            point.x = rect.left;
                            point.y = rect.top + (ArrowActionSheet.this.mAnchorView.getHeight() / 2);
                        }
                        return point;
                    case 6:
                    case 7:
                    case 9:
                        if (zIsLayoutRtl) {
                            point.x = rect.left + ArrowActionSheet.this.mAnchorView.getWidth();
                            point.y = rect.top + (ArrowActionSheet.this.mAnchorView.getHeight() / 2);
                        } else {
                            point.x = rect.left;
                            point.y = rect.top + (ArrowActionSheet.this.mAnchorView.getHeight() / 2);
                        }
                        return point;
                    case 10:
                    case 11:
                    case 12:
                        point.x = rect.left + (ArrowActionSheet.this.mAnchorView.getWidth() / 2);
                        point.y = rect.top;
                        return point;
                    default:
                        return point;
                }
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            private Point getShowOffset(ActionSheet.ArrowMode arrowMode, Point point) {
                Point point2 = new Point();
                boolean z = ArrowActionSheet.this.mAnchorView != null && ViewUtils.isLayoutRtl(ArrowActionSheet.this.mAnchorView);
                switch (AnonymousClass2.$SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[arrowMode.ordinal()]) {
                    case 1:
                        if (z) {
                            point2.x = (point.x * (-1)) + ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2);
                            point2.y = 0;
                        } else {
                            point2.x = (ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2)) * (-1);
                            point2.y = 0;
                        }
                        return point2;
                    case 2:
                        point2.x = (point.x / 2) * (-1);
                        point2.y = 0;
                        return point2;
                    case 3:
                        if (z) {
                            point2.x = (ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2)) * (-1);
                            point2.y = 0;
                        } else {
                            point2.x = (point.x * (-1)) + ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2);
                            point2.y = 0;
                        }
                        return point2;
                    case 4:
                        if (z) {
                            point2.x = point.x * (-1);
                            point2.y = (point.y / 2) * (-1);
                        } else {
                            point2.x = 0;
                            point2.y = (point.y / 2) * (-1);
                        }
                        return point2;
                    case 5:
                        if (z) {
                            point2.x = point.x * (-1);
                            point2.y = (ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2)) * (-1);
                        } else {
                            point2.x = 0;
                            point2.y = (ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2)) * (-1);
                        }
                        return point2;
                    case 6:
                        if (z) {
                            point2.x = 0;
                            point2.y = (point.y / 2) * (-1);
                        } else {
                            point2.x = point.x * (-1);
                            point2.y = (point.y / 2) * (-1);
                        }
                        return point2;
                    case 7:
                        if (z) {
                            point2.x = 0;
                            point2.y = (ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2)) * (-1);
                        } else {
                            point2.x = point.x * (-1);
                            point2.y = (ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2)) * (-1);
                        }
                        return point2;
                    case 8:
                        if (z) {
                            point2.x = point.x * (-1);
                            point2.y = (point.y * (-1)) + ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2);
                        } else {
                            point2.x = 0;
                            point2.y = (point.y * (-1)) + ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2);
                        }
                        return point2;
                    case 9:
                        if (z) {
                            point2.x = 0;
                            point2.y = (point.y * (-1)) + ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2);
                        } else {
                            point2.x = point.x * (-1);
                            point2.y = (point.y * (-1)) + ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2);
                        }
                        return point2;
                    case 10:
                        if (z) {
                            point2.x = (point.x * (-1)) + ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2);
                            point2.y = point.y * (-1);
                        } else {
                            point2.x = (ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2)) * (-1);
                            point2.y = point.y * (-1);
                        }
                        return point2;
                    case 11:
                        point2.x = (point.x / 2) * (-1);
                        point2.y = point.y * (-1);
                        return point2;
                    case 12:
                        if (z) {
                            point2.x = (ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2)) * (-1);
                            point2.y = point.y * (-1);
                        } else {
                            point2.x = (point.x * (-1)) + ArrowActionSheet.this.mArrowLinkOffset + (ArrowActionSheet.this.mArrowIconLongSide / 2);
                            point2.y = point.y * (-1);
                        }
                        return point2;
                    default:
                        return point2;
                }
            }

            @Override // miuix.internal.widget.ActionSheet.ContentController
            public int calcContentPanelWidth(Context context, ViewGroup viewGroup, ViewGroup viewGroup2, int i, WindowInsets windowInsets) {
                return ArrowActionSheet.this.mFixedWidth;
            }

            /* JADX WARN: Code duplicated, block: B:30:0x007f A[PHI: r4
  0x007f: PHI (r4v10 boolean) = (r4v7 boolean), (r4v17 boolean), (r4v29 boolean) binds: [B:44:0x00ac, B:37:0x0096, B:29:0x007d] A[DONT_GENERATE, DONT_INLINE]] */
            /* JADX WARN: Code duplicated, block: B:31:0x0081 A[PHI: r4
  0x0081: PHI (r4v9 boolean) = (r4v7 boolean), (r4v17 boolean), (r4v29 boolean) binds: [B:44:0x00ac, B:37:0x0096, B:29:0x007d] A[DONT_GENERATE, DONT_INLINE]] */
            /* JADX WARN: Code duplicated, block: B:75:0x012b  */
            private boolean[] detectOverflow(Rect rect, Point point, Point point2, WindowInsets windowInsets, Point point3) {
                boolean z;
                boolean z2;
                boolean z3;
                boolean z4;
                if (ArrowActionSheet.this.mAnchorView == null) {
                    return new boolean[]{false, false, false, false};
                }
                if (point.x > 0) {
                    int[] iArrCalcHorizontalMargin = calcHorizontalMargin(ArrowActionSheet.this.mContext, point.x, windowInsets);
                    if (ArrowActionSheet.this.mArrowMode != ActionSheet.ArrowMode.ARROW_TOP_MODE && ArrowActionSheet.this.mArrowMode != ActionSheet.ArrowMode.ARROW_BOTTOM_MODE) {
                        if (ArrowActionSheet.this.mArrowMode == ActionSheet.ArrowMode.ARROW_TOP_RIGHT_MODE || ArrowActionSheet.this.mArrowMode == ActionSheet.ArrowMode.ARROW_RIGHT_TOP_MODE || ArrowActionSheet.this.mArrowMode == ActionSheet.ArrowMode.ARROW_RIGHT_MODE || ArrowActionSheet.this.mArrowMode == ActionSheet.ArrowMode.ARROW_RIGHT_BOTTOM_MODE || ArrowActionSheet.this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_RIGHT_MODE) {
                            z2 = point3.x < iArrCalcHorizontalMargin[0];
                            if (point3.x + point2.x + iArrCalcHorizontalMargin[1] > point.x) {
                                z = true;
                            } else {
                                z = false;
                            }
                        } else {
                            z2 = point3.x < iArrCalcHorizontalMargin[0];
                            if (point3.x + point2.x + iArrCalcHorizontalMargin[1] > point.x) {
                                z = true;
                            } else {
                                z = false;
                            }
                        }
                    } else {
                        z2 = point3.x < iArrCalcHorizontalMargin[0];
                        if (point3.x + point2.x + iArrCalcHorizontalMargin[1] > point.x) {
                            z = true;
                        } else {
                            z = false;
                        }
                    }
                } else {
                    z = false;
                    z2 = false;
                }
                if (point.y > 0) {
                    int[] iArrCalcVerticalMargin = calcVerticalMargin(ArrowActionSheet.this.mContext, point.y, windowInsets);
                    if (ArrowActionSheet.this.mArrowMode != ActionSheet.ArrowMode.ARROW_TOP_LEFT_MODE && ArrowActionSheet.this.mArrowMode != ActionSheet.ArrowMode.ARROW_TOP_MODE && ArrowActionSheet.this.mArrowMode != ActionSheet.ArrowMode.ARROW_TOP_RIGHT_MODE) {
                        if (ArrowActionSheet.this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_LEFT_MODE || ArrowActionSheet.this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_MODE || ArrowActionSheet.this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_RIGHT_MODE) {
                            int i = rect.top;
                            int i2 = point2.y;
                            int i3 = iArrCalcVerticalMargin[0];
                            z3 = point3.y < iArrCalcVerticalMargin[0];
                            z4 = (point3.y + point2.y) + iArrCalcVerticalMargin[1] > point.y;
                        } else {
                            z3 = false;
                            z4 = false;
                        }
                    } else {
                        z4 = rect.bottom < point2.y + iArrCalcVerticalMargin[1];
                        z3 = false;
                    }
                } else {
                    z3 = false;
                    z4 = false;
                }
                return new boolean[]{z2, z3, z, z4};
            }

            @Override // miuix.internal.widget.ActionSheet.ContentController
            public int[] calcHorizontalMargin(Context context, int i, WindowInsets windowInsets) {
                return new int[]{ArrowActionSheet.this.mDefaultMargin, ArrowActionSheet.this.mDefaultMargin};
            }

            @Override // miuix.internal.widget.ActionSheet.ContentController
            public int[] calcVerticalMargin(Context context, int i, WindowInsets windowInsets) {
                if (windowInsets != null && Build.VERSION.SDK_INT >= 30) {
                    Insets insetsIgnoringVisibility = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                    return new int[]{insetsIgnoringVisibility.top + ArrowActionSheet.this.mDefaultMargin, insetsIgnoringVisibility.bottom + ArrowActionSheet.this.mDefaultMargin};
                }
                return new int[]{ArrowActionSheet.this.mDefaultMargin, ArrowActionSheet.this.mDefaultMargin};
            }
        };
        this.mContentController = contentController;
        ActionSheetController actionSheetController = this.mActionController;
        if (actionSheetController != null) {
            actionSheetController.setContentController(contentController);
        }
    }

    /* JADX INFO: renamed from: miuix.internal.widget.ArrowActionSheet$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode;

        static {
            int[] iArr = new int[ActionSheet.ArrowMode.values().length];
            $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode = iArr;
            try {
                iArr[ActionSheet.ArrowMode.ARROW_TOP_LEFT_MODE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_TOP_MODE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_TOP_RIGHT_MODE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_LEFT_MODE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_LEFT_TOP_MODE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_RIGHT_MODE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_RIGHT_TOP_MODE.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_LEFT_BOTTOM_MODE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_RIGHT_BOTTOM_MODE.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_BOTTOM_LEFT_MODE.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_BOTTOM_MODE.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_BOTTOM_RIGHT_MODE.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
        }
    }

    public void setOffset(int i, int i2) {
        this.mOffset.x = i;
        this.mOffset.y = i2;
    }

    public void setArrowMode(ActionSheet.ArrowMode arrowMode) {
        this.mArrowMode = arrowMode;
    }

    public ActionSheet.ArrowMode getArrowMode() {
        return this.mArrowMode;
    }

    public View getArrowAnchor() {
        return this.mAnchorView;
    }

    protected void setIsFromAlertShape(boolean z) {
        this.mIsFromAlertShape = z;
    }

    protected boolean isFromAlertShape() {
        return this.mIsFromAlertShape;
    }

    protected void setIsDismissForShift(boolean z) {
        this.mIsDismissForShift = z;
    }

    protected boolean isDismissForShift() {
        return this.mIsDismissForShift;
    }

    @Override // miuix.appcompat.app.AlertDialog, miuix.internal.widget.ActionSheet.IActionSheet
    public ListView getListView() {
        return this.mActionController.getListView();
    }

    @Override // miuix.appcompat.app.AlertDialog
    public TextView getMessageView() {
        return this.mActionController.getMessageView();
    }

    @Override // miuix.internal.widget.ActionSheet.IActionSheet
    public TextView getSeparateView() {
        return this.mActionController.getSeparateView();
    }

    public void setListViewAdapter(ListAdapter listAdapter) {
        this.mActionController.setListViewAdapter(listAdapter);
    }

    @Override // miuix.appcompat.app.AlertDialog
    public void setMessage(CharSequence charSequence) {
        this.mActionController.setMessage(charSequence);
    }

    public void setActionItems(int i, DialogInterface.OnClickListener onClickListener) {
        this.mActionController.setActionItems(i, onClickListener);
    }

    public void setActionItems(int i, ActionSheet.ActionSheetItemType[] actionSheetItemTypeArr, DialogInterface.OnClickListener onClickListener) {
        this.mActionController.setActionItems(i, actionSheetItemTypeArr, onClickListener);
    }

    public void setActionItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
        this.mActionController.setActionItems(charSequenceArr, onClickListener);
    }

    public void setActionItems(CharSequence[] charSequenceArr, ActionSheet.ActionSheetItemType[] actionSheetItemTypeArr, DialogInterface.OnClickListener onClickListener) {
        this.mActionController.setActionItems(charSequenceArr, actionSheetItemTypeArr, onClickListener);
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    protected void onCreate(Bundle bundle) {
        if (isSystemSpecialUiThread() && this.mLifecycleOwnerCompat != null) {
            this.mLifecycleOwnerCompat.onCreate();
        }
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(0);
        }
        superOnCreate(bundle);
        this.mActionController.installContent(bundle);
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.activity.ComponentDialog, android.app.Dialog
    protected void onStart() {
        superOnStart();
        this.mActionController.onStart();
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    protected void onStop() {
        if (isSystemSpecialUiThread() && this.mLifecycleOwnerCompat != null) {
            this.mLifecycleOwnerCompat.onStopBefore();
        }
        superOnStop();
        if (!isSystemSpecialUiThread() || this.mLifecycleOwnerCompat == null) {
            return;
        }
        this.mLifecycleOwnerCompat.onStopAfter();
    }

    @Override // miuix.appcompat.app.AlertDialog, android.app.Dialog
    public void show() {
        superShow();
    }

    @Override // miuix.appcompat.app.AlertDialog
    public void setEnableEnterAnim(boolean z) {
        this.mActionController.setEnableEnterAnim(z);
    }

    @Override // miuix.appcompat.app.AlertDialog
    public void setOnShowAnimListener(AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
        this.mActionController.setShowAnimListener(onDialogShowAnimListener);
    }

    @Override // miuix.appcompat.app.AlertDialog
    public void setConfigurationChangedListener(AlertDialog.OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mActionController.setConfigurationChangedListener(onConfigurationChangedListener);
    }

    public void setActionSheetOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.mActionController.setOnShowListener(onShowListener);
    }

    public void setActionSheetOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mActionController.setOnDismissListener(onDismissListener);
    }

    public void setActionSheetOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mActionController.setOnCancelListener(onCancelListener);
    }

    public void setActionSheetOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.mActionController.setOnKeyListener(onKeyListener);
    }

    public void setItemAccessibilityProvider(AccessibilityDelegateProvider accessibilityDelegateProvider) {
        this.mActionController.setItemProvider(accessibilityDelegateProvider);
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (this.mActionController.dispatchKeyEvent(keyEvent)) {
            return true;
        }
        return miuixSuperDispatchKeyEvent(keyEvent);
    }

    @Override // miuix.appcompat.app.AlertDialog, android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        View decorView = window != null ? window.getDecorView() : null;
        if (this.mActionController.mHapticFeedbackEnabled && decorView != null) {
            HapticCompat.performHapticFeedbackAsync(decorView, HapticFeedbackConstants.MIUI_ALERT, HapticFeedbackConstants.MIUI_POPUP_NORMAL);
        }
        this.mActionController.onAttachedToWindow();
        setAccessibilityDelegate(decorView);
    }

    @Override // miuix.appcompat.app.AlertDialog
    public void setHapticFeedbackEnabled(boolean z) {
        this.mActionController.mHapticFeedbackEnabled = z;
    }

    public boolean isHapticFeedbackEnabled() {
        return this.mActionController.mHapticFeedbackEnabled;
    }

    @Override // miuix.appcompat.app.AlertDialog, android.app.Dialog
    public void setCanceledOnTouchOutside(boolean z) {
        this.mActionController.setCanceledOnTouchOutside(z);
    }

    public boolean isCanceledOnTouchOutside() {
        return this.mActionController.isCanceledOnTouchOutside();
    }

    @Override // miuix.appcompat.app.AlertDialog, android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        superOnDetachedFromWindow();
        this.mActionController.onDetachedFromWindow();
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        Window window = getWindow();
        View decorView = window != null ? window.getDecorView() : null;
        if (this.mActionController.isShowingAnimation()) {
            this.mActionController.setPendingDismiss(true);
            return;
        }
        this.mActionController.setPendingDismiss(false);
        if ((decorView != null ? DensityUtil.findAutoDensityContextWrapper(decorView.getContext()) : null) != null) {
            EnvStateManager.removeInfoOfContext(decorView.getContext());
        }
        Activity associatedActivity = getAssociatedActivity();
        if (associatedActivity != null && associatedActivity.isFinishing()) {
            dismissIfAttachedToWindow(decorView);
        } else {
            dismissWithAnimationOrNot(decorView);
        }
    }

    @Override // miuix.appcompat.app.AlertDialog
    protected void dismissWithAnimationExistDecorView(View view) {
        if (Thread.currentThread() == view.getHandler().getLooper().getThread()) {
            this.mActionController.dismiss(this.mOnDismiss);
        } else {
            view.post(new Runnable() { // from class: miuix.internal.widget.ArrowActionSheet$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1859xd1c80f72();
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$dismissWithAnimationExistDecorView$0$miuix-internal-widget-ArrowActionSheet, reason: not valid java name */
    /* synthetic */ void m1859xd1c80f72() {
        this.mActionController.dismiss(this.mOnDismiss);
    }

    protected void dismissForShiftWithoutAnimation() {
        setIsDismissForShift(true);
        dismissWithoutAnimation();
    }

    @Override // miuix.appcompat.app.AlertDialog, miuix.internal.widget.ActionSheet.IActionSheet
    public void dismissWithoutAnimation() {
        Window window = getWindow();
        View decorView = window != null ? window.getDecorView() : null;
        if (decorView == null || decorView.isAttachedToWindow()) {
            if (this.mActionController.isShowingAnimation()) {
                this.mActionController.setPendingDismiss(true);
                return;
            }
            this.mActionController.setPendingDismiss(false);
            if ((decorView != null ? DensityUtil.findAutoDensityContextWrapper(decorView.getContext()) : null) != null) {
                EnvStateManager.removeInfoOfContext(decorView.getContext());
            }
            realDismiss();
        }
    }
}
