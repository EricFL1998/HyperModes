package miuix.internal.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import miuix.appcompat.R;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ArrowActionSheetPanel extends FrameLayout {
    private int mArrowIconLongSide;
    private int mArrowIconShortSide;
    private int mArrowLinkOffset;
    private ActionSheet.ArrowMode mArrowMode;
    private AppCompatImageView mArrowView;
    private ViewGroup mContent;
    private int mContentMaxHeight;
    private int mContentRadius;
    private Context mContext;

    public ArrowActionSheetPanel(Context context) {
        this(context, null, 0);
    }

    public ArrowActionSheetPanel(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ArrowActionSheetPanel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
        this.mContentRadius = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_content_panel_radius);
        this.mArrowLinkOffset = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_arrow_icon_link_offset);
        this.mArrowIconLongSide = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_arrow_icon_long_side);
        this.mArrowIconShortSide = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_arrow_icon_short_side);
        this.mContentMaxHeight = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_action_sheet_arrow_content_panel_max_height);
    }

    public void setArrowMode(ActionSheet.ArrowMode arrowMode) {
        this.mArrowMode = arrowMode;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mArrowView = (AppCompatImageView) findViewById(R.id.action_sheet_arrow_view);
        this.mContent = (ViewGroup) findViewById(R.id.action_sheet_content);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.mArrowView.getDrawable() == null) {
            updateArrowViewDrawable(this.mArrowMode);
        }
        super.onMeasure(i, i2);
        updateMeasuredSizeAfterSuperMeasured();
    }

    private void updateMeasuredSizeAfterSuperMeasured() {
        int measuredWidth = this.mArrowView.getMeasuredWidth();
        int measuredHeight = this.mArrowView.getMeasuredHeight();
        int measuredWidth2 = this.mContent.getMeasuredWidth();
        int measuredHeight2 = this.mContent.getMeasuredHeight();
        if (this.mArrowMode == ActionSheet.ArrowMode.ARROW_TOP_LEFT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_TOP_RIGHT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_LEFT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_RIGHT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_TOP_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_MODE) {
            if (measuredHeight2 > this.mContentMaxHeight - measuredHeight) {
                measureChild(this.mContent, View.MeasureSpec.makeMeasureSpec(measuredWidth2, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(this.mContentMaxHeight - measuredHeight, Integer.MIN_VALUE));
                measuredHeight2 = this.mContent.getMeasuredHeight();
            }
        } else if ((this.mArrowMode == ActionSheet.ArrowMode.ARROW_LEFT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_RIGHT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_LEFT_TOP_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_LEFT_BOTTOM_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_RIGHT_TOP_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_RIGHT_BOTTOM_MODE) && measuredHeight2 > this.mContentMaxHeight) {
            measureChild(this.mContent, View.MeasureSpec.makeMeasureSpec(measuredWidth2, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(this.mContentMaxHeight, Integer.MIN_VALUE));
            measuredHeight2 = this.mContent.getMeasuredHeight();
        }
        if (this.mArrowMode == ActionSheet.ArrowMode.ARROW_TOP_LEFT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_TOP_RIGHT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_LEFT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_RIGHT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_TOP_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_BOTTOM_MODE) {
            setMeasuredDimension(measuredWidth2, measuredHeight + measuredHeight2);
            return;
        }
        if (this.mArrowMode == ActionSheet.ArrowMode.ARROW_LEFT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_RIGHT_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_LEFT_TOP_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_LEFT_BOTTOM_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_RIGHT_TOP_MODE || this.mArrowMode == ActionSheet.ArrowMode.ARROW_RIGHT_BOTTOM_MODE) {
            setMeasuredDimension(measuredWidth + measuredWidth2, measuredHeight2);
        }
    }

    private void updateArrowViewDrawable(ActionSheet.ArrowMode arrowMode) {
        Drawable drawable;
        boolean zIsNightMode = ViewUtils.isNightMode(this.mContext);
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
        switch (AnonymousClass1.$SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[arrowMode.ordinal()]) {
            case 1:
            case 2:
            case 3:
                drawable = AppCompatResources.getDrawable(this.mContext, zIsNightMode ? R.drawable.miuix_appcompat_arrow_action_sheet_top_dark : R.drawable.miuix_appcompat_arrow_action_sheet_top_light);
                break;
            case 4:
            case 5:
            case 6:
                drawable = AppCompatResources.getDrawable(this.mContext, zIsNightMode ? R.drawable.miuix_appcompat_arrow_action_sheet_bottom_dark : R.drawable.miuix_appcompat_arrow_action_sheet_bottom_light);
                break;
            case 7:
            case 8:
            case 9:
                if (zIsLayoutRtl) {
                    drawable = AppCompatResources.getDrawable(this.mContext, zIsNightMode ? R.drawable.miuix_appcompat_arrow_action_sheet_right_dark : R.drawable.miuix_appcompat_arrow_action_sheet_right_light);
                } else {
                    drawable = AppCompatResources.getDrawable(this.mContext, zIsNightMode ? R.drawable.miuix_appcompat_arrow_action_sheet_left_dark : R.drawable.miuix_appcompat_arrow_action_sheet_left_light);
                }
                break;
            case 10:
            case 11:
            case 12:
                if (zIsLayoutRtl) {
                    drawable = AppCompatResources.getDrawable(this.mContext, zIsNightMode ? R.drawable.miuix_appcompat_arrow_action_sheet_left_dark : R.drawable.miuix_appcompat_arrow_action_sheet_left_light);
                } else {
                    drawable = AppCompatResources.getDrawable(this.mContext, zIsNightMode ? R.drawable.miuix_appcompat_arrow_action_sheet_right_dark : R.drawable.miuix_appcompat_arrow_action_sheet_right_light);
                }
                break;
            default:
                drawable = null;
                break;
        }
        this.mArrowView.setImageDrawable(drawable);
    }

    /* JADX INFO: renamed from: miuix.internal.widget.ArrowActionSheetPanel$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode;

        static {
            int[] iArr = new int[ActionSheet.ArrowMode.values().length];
            $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode = iArr;
            try {
                iArr[ActionSheet.ArrowMode.ARROW_TOP_MODE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_TOP_LEFT_MODE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_TOP_RIGHT_MODE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_BOTTOM_MODE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_BOTTOM_LEFT_MODE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_BOTTOM_RIGHT_MODE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_LEFT_MODE.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_LEFT_TOP_MODE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_LEFT_BOTTOM_MODE.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_RIGHT_MODE.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_RIGHT_TOP_MODE.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[ActionSheet.ArrowMode.ARROW_RIGHT_BOTTOM_MODE.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        layoutContentView(layoutArrowView());
    }

    private Point layoutArrowView() {
        Point arrowPosition = getArrowPosition(this.mArrowMode);
        this.mArrowView.layout(arrowPosition.x, arrowPosition.y, arrowPosition.x + this.mArrowView.getMeasuredWidth(), arrowPosition.y + this.mArrowView.getMeasuredHeight());
        return arrowPosition;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private Point getArrowPosition(ActionSheet.ArrowMode arrowMode) {
        Point point = new Point();
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
        switch (AnonymousClass1.$SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[arrowMode.ordinal()]) {
            case 1:
                point.x = (this.mContent.getMeasuredWidth() / 2) - (this.mArrowView.getWidth() / 2);
                point.y = 0;
                return point;
            case 2:
                if (zIsLayoutRtl) {
                    point.x = (this.mContent.getMeasuredWidth() - this.mArrowLinkOffset) - this.mArrowView.getWidth();
                    point.y = 0;
                } else {
                    point.x = this.mArrowLinkOffset;
                    point.y = 0;
                }
                return point;
            case 3:
                if (zIsLayoutRtl) {
                    point.x = this.mArrowLinkOffset;
                    point.y = 0;
                } else {
                    point.x = (this.mContent.getMeasuredWidth() - this.mArrowLinkOffset) - this.mArrowView.getWidth();
                    point.y = 0;
                }
                return point;
            case 4:
                point.x = (this.mContent.getMeasuredWidth() / 2) - (this.mArrowView.getWidth() / 2);
                point.y = this.mContent.getMeasuredHeight();
                return point;
            case 5:
                if (zIsLayoutRtl) {
                    point.x = (this.mContent.getMeasuredWidth() - this.mArrowLinkOffset) - this.mArrowView.getWidth();
                    point.y = this.mContent.getMeasuredHeight();
                } else {
                    point.x = this.mArrowLinkOffset;
                    point.y = this.mContent.getMeasuredHeight();
                }
                return point;
            case 6:
                if (zIsLayoutRtl) {
                    point.x = this.mArrowLinkOffset;
                    point.y = this.mContent.getMeasuredHeight();
                } else {
                    point.x = (this.mContent.getMeasuredWidth() - this.mArrowLinkOffset) - this.mArrowView.getWidth();
                    point.y = this.mContent.getMeasuredHeight();
                }
                return point;
            case 7:
                if (zIsLayoutRtl) {
                    point.x = this.mContent.getMeasuredWidth();
                    point.y = (this.mContent.getMeasuredHeight() / 2) - (this.mArrowView.getHeight() / 2);
                } else {
                    point.x = 0;
                    point.y = (this.mContent.getMeasuredHeight() / 2) - (this.mArrowView.getHeight() / 2);
                }
                return point;
            case 8:
                if (zIsLayoutRtl) {
                    point.x = this.mContent.getMeasuredWidth();
                    point.y = this.mArrowLinkOffset;
                } else {
                    point.x = 0;
                    point.y = this.mArrowLinkOffset;
                }
                return point;
            case 9:
                if (zIsLayoutRtl) {
                    point.x = this.mContent.getMeasuredWidth();
                    point.y = (this.mContent.getMeasuredHeight() - this.mArrowLinkOffset) - this.mArrowView.getMeasuredHeight();
                } else {
                    point.x = 0;
                    point.y = (this.mContent.getMeasuredHeight() - this.mArrowLinkOffset) - this.mArrowView.getHeight();
                }
                return point;
            case 10:
                if (zIsLayoutRtl) {
                    point.x = 0;
                    point.y = (this.mContent.getMeasuredHeight() / 2) - (this.mArrowView.getHeight() / 2);
                } else {
                    point.x = this.mContent.getMeasuredWidth();
                    point.y = (this.mContent.getMeasuredHeight() / 2) - (this.mArrowView.getHeight() / 2);
                }
                return point;
            case 11:
                if (zIsLayoutRtl) {
                    point.x = 0;
                    point.y = this.mArrowLinkOffset;
                } else {
                    point.x = this.mContent.getMeasuredWidth();
                    point.y = this.mArrowLinkOffset;
                }
                return point;
            case 12:
                if (zIsLayoutRtl) {
                    point.x = 0;
                    point.y = (this.mContent.getMeasuredHeight() - this.mArrowLinkOffset) - this.mArrowView.getHeight();
                } else {
                    point.x = this.mContent.getMeasuredWidth();
                    point.y = (this.mContent.getMeasuredHeight() - this.mArrowLinkOffset) - this.mArrowView.getMeasuredHeight();
                }
                return point;
            default:
                return point;
        }
    }

    private void layoutContentView(Point point) {
        Point contentPanelPosition = getContentPanelPosition(this.mArrowMode, point);
        this.mContent.layout(contentPanelPosition.x, contentPanelPosition.y, contentPanelPosition.x + this.mContent.getMeasuredWidth(), contentPanelPosition.y + this.mContent.getMeasuredHeight());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private Point getContentPanelPosition(ActionSheet.ArrowMode arrowMode, Point point) {
        Point point2 = new Point();
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
        switch (AnonymousClass1.$SwitchMap$miuix$internal$widget$ActionSheet$ArrowMode[arrowMode.ordinal()]) {
            case 1:
                point2.x = point.x - ((this.mContent.getMeasuredWidth() - this.mArrowView.getMeasuredWidth()) / 2);
                point2.y = point.y + this.mArrowView.getMeasuredHeight();
                return point2;
            case 2:
                if (zIsLayoutRtl) {
                    point2.x = point.x - ((this.mContent.getMeasuredWidth() - this.mArrowView.getMeasuredWidth()) - this.mArrowLinkOffset);
                    point2.y = point.y + this.mArrowView.getMeasuredHeight();
                } else {
                    point2.x = point.x - this.mArrowLinkOffset;
                    point2.y = point.y + this.mArrowView.getMeasuredHeight();
                }
                return point2;
            case 3:
                if (zIsLayoutRtl) {
                    point2.x = point.x - this.mArrowLinkOffset;
                    point2.y = point.y + this.mArrowView.getMeasuredHeight();
                } else {
                    point2.x = point.x - ((this.mContent.getMeasuredWidth() - this.mArrowView.getMeasuredWidth()) - this.mArrowLinkOffset);
                    point2.y = point.y + this.mArrowView.getMeasuredHeight();
                }
                return point2;
            case 4:
                point2.x = point.x - ((this.mContent.getMeasuredWidth() - this.mArrowView.getMeasuredWidth()) / 2);
                point2.y = point.y - this.mContent.getMeasuredHeight();
                return point2;
            case 5:
                if (zIsLayoutRtl) {
                    point2.x = point.x - ((this.mContent.getMeasuredWidth() - this.mArrowView.getMeasuredWidth()) - this.mArrowLinkOffset);
                    point2.y = point.y - this.mContent.getMeasuredHeight();
                } else {
                    point2.x = point.x - this.mArrowLinkOffset;
                    point2.y = point.y - this.mContent.getMeasuredHeight();
                }
                return point2;
            case 6:
                if (zIsLayoutRtl) {
                    point2.x = point.x - this.mArrowLinkOffset;
                    point2.y = point.y - this.mContent.getMeasuredHeight();
                } else {
                    point2.x = point.x - ((this.mContent.getMeasuredWidth() - this.mArrowView.getMeasuredWidth()) - this.mArrowLinkOffset);
                    point2.y = point.y - this.mContent.getMeasuredHeight();
                }
                return point2;
            case 7:
                if (zIsLayoutRtl) {
                    point2.x = point.x - this.mContent.getMeasuredWidth();
                    point2.y = point.y - ((this.mContent.getMeasuredHeight() - this.mArrowView.getMeasuredHeight()) / 2);
                } else {
                    point2.x = point.x + this.mArrowView.getMeasuredWidth();
                    point2.y = point.y - ((this.mContent.getMeasuredHeight() - this.mArrowView.getMeasuredHeight()) / 2);
                }
                return point2;
            case 8:
                if (zIsLayoutRtl) {
                    point2.x = point.x - this.mContent.getMeasuredWidth();
                    point2.y = point.y - this.mArrowLinkOffset;
                } else {
                    point2.x = point.x + this.mArrowView.getMeasuredWidth();
                    point2.y = point.y - this.mArrowLinkOffset;
                }
                return point2;
            case 9:
                if (zIsLayoutRtl) {
                    point2.x = point.x - this.mContent.getMeasuredWidth();
                    point2.y = point.y - ((this.mContent.getMeasuredHeight() - this.mArrowView.getMeasuredHeight()) - this.mArrowLinkOffset);
                } else {
                    point2.x = point.x + this.mArrowView.getMeasuredWidth();
                    point2.y = point.y - ((this.mContent.getMeasuredHeight() - this.mArrowView.getMeasuredHeight()) - this.mArrowLinkOffset);
                }
                return point2;
            case 10:
                if (zIsLayoutRtl) {
                    point2.x = point.x + this.mArrowView.getMeasuredWidth();
                    point2.y = point.y - ((this.mContent.getMeasuredHeight() - this.mArrowView.getMeasuredHeight()) / 2);
                } else {
                    point2.x = point.x - this.mContent.getMeasuredWidth();
                    point2.y = point.y - ((this.mContent.getMeasuredHeight() - this.mArrowView.getMeasuredHeight()) / 2);
                }
                return point2;
            case 11:
                if (zIsLayoutRtl) {
                    point2.x = point.x + this.mArrowView.getMeasuredWidth();
                    point2.y = point.y - this.mArrowLinkOffset;
                } else {
                    point2.x = point.x - this.mContent.getMeasuredWidth();
                    point2.y = point.y - this.mArrowLinkOffset;
                }
                return point2;
            case 12:
                if (zIsLayoutRtl) {
                    point2.x = point.x + this.mArrowView.getMeasuredWidth();
                    point2.y = point.y - ((this.mContent.getMeasuredHeight() - this.mArrowView.getMeasuredHeight()) - this.mArrowLinkOffset);
                } else {
                    point2.x = point.x - this.mContent.getMeasuredWidth();
                    point2.y = point.y - ((this.mContent.getMeasuredHeight() - this.mArrowView.getMeasuredHeight()) - this.mArrowLinkOffset);
                }
                return point2;
            default:
                return point2;
        }
    }
}
