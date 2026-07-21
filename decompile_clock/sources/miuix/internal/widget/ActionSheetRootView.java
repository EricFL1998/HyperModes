package miuix.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class ActionSheetRootView extends FrameLayout {
    private static final String TAG = "ActionSheetRootView";
    private final Point mAnchorLocation;
    private ConfigurationChangedCallback mCallback;
    private ActionSheet.ContentController mContentController;
    private ViewGroup mContentPanelChild;
    private Rect mContentPanelExtraBounds;
    private boolean mDebugEnabled;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    public interface ConfigurationChangedCallback {
        void onConfigurationChanged(Configuration configuration);
    }

    public ActionSheetRootView(Context context) {
        this(context, null, 0);
    }

    public ActionSheetRootView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ActionSheetRootView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAnchorLocation = new Point();
    }

    public void setContentController(ActionSheet.ContentController contentController) {
        this.mContentController = contentController;
    }

    public void setContentPanelExtraBounds(Rect rect) {
        this.mContentPanelExtraBounds = rect;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        beforeOnMeasure();
        super.onMeasure(i, i2);
    }

    private void beforeOnMeasure() {
        this.mContentPanelChild = (ViewGroup) findViewById(R.id.action_sheet_content_panel);
    }

    public void setDebugEnabled(boolean z) {
        this.mDebugEnabled = z;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        layoutContentPanel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void layoutContentPanel() {
        Point pointComputeContentPosition;
        if (this.mContentPanelExtraBounds == null || this.mContentController == null || this.mContentPanelChild == null) {
            return;
        }
        Point point = new Point(getMeasuredWidth(), getMeasuredHeight());
        int measuredWidth = this.mContentPanelChild.getMeasuredWidth();
        int measuredHeight = this.mContentPanelChild.getMeasuredHeight();
        Point point2 = new Point(measuredWidth, measuredHeight);
        View arrowAnchor = this.mContentController.getArrowAnchor();
        if (arrowAnchor != null) {
            pointComputeContentPosition = this.mContentController.computeContentPosition(computeExtraBoundsUseArrowAnchor(arrowAnchor), point, point2, this, this.mContentPanelChild);
        } else {
            pointComputeContentPosition = this.mContentController.computeContentPosition(this.mContentPanelExtraBounds, point, point2, this, this.mContentPanelChild);
        }
        if (this.mDebugEnabled) {
            Log.d(TAG, "layoutContentPanel: panelPosition = " + pointComputeContentPosition);
        }
        this.mContentPanelChild.layout(pointComputeContentPosition.x, pointComputeContentPosition.y, pointComputeContentPosition.x + measuredWidth, pointComputeContentPosition.y + measuredHeight);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getAnchorLocation(View view, Point point) {
        if (view == null || point == null) {
            return;
        }
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        point.x = iArr[0];
        point.y = iArr[1];
    }

    private Rect computeExtraBoundsUseArrowAnchor(View view) {
        Rect rect = new Rect();
        getAnchorLocation(view, this.mAnchorLocation);
        rect.left = this.mAnchorLocation.x;
        rect.top = this.mAnchorLocation.y;
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int measuredWidth2 = getMeasuredWidth();
        int measuredHeight2 = getMeasuredHeight();
        rect.right = measuredWidth2 - (rect.left + measuredWidth);
        rect.bottom = measuredHeight2 - (rect.top + measuredHeight);
        return rect;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchConfigurationChanged(Configuration configuration) {
        super.dispatchConfigurationChanged(configuration);
        ConfigurationChangedCallback configurationChangedCallback = this.mCallback;
        if (configurationChangedCallback != null) {
            configurationChangedCallback.onConfigurationChanged(configuration);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        View arrowAnchor;
        super.onAttachedToWindow();
        this.mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: miuix.internal.widget.ActionSheetRootView.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                View arrowAnchor2;
                if (ActionSheetRootView.this.mContentController == null || (arrowAnchor2 = ActionSheetRootView.this.mContentController.getArrowAnchor()) == null) {
                    return;
                }
                Point point = new Point();
                ActionSheetRootView.this.getAnchorLocation(arrowAnchor2, point);
                if (ActionSheetRootView.this.mDebugEnabled) {
                    Log.d(ActionSheetRootView.TAG, "onGlobalLayout: mAnchorLocation = " + ActionSheetRootView.this.mAnchorLocation + ", currentLocation = " + point);
                }
                if (point.x == ActionSheetRootView.this.mAnchorLocation.x && point.y == ActionSheetRootView.this.mAnchorLocation.y) {
                    return;
                }
                ActionSheetRootView.this.layoutContentPanel();
            }
        };
        ActionSheet.ContentController contentController = this.mContentController;
        if (contentController == null || (arrowAnchor = contentController.getArrowAnchor()) == null) {
            return;
        }
        arrowAnchor.getViewTreeObserver().addOnGlobalLayoutListener(this.mGlobalLayoutListener);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        View arrowAnchor;
        super.onDetachedFromWindow();
        ActionSheet.ContentController contentController = this.mContentController;
        if (contentController == null || (arrowAnchor = contentController.getArrowAnchor()) == null) {
            return;
        }
        arrowAnchor.getViewTreeObserver().removeOnGlobalLayoutListener(this.mGlobalLayoutListener);
    }

    public void setConfigurationChangedCallback(ConfigurationChangedCallback configurationChangedCallback) {
        this.mCallback = configurationChangedCallback;
    }
}
