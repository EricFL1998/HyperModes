package miuix.springback.trigger;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.deskclock.alarm.lifepost.model.NewsTable;
import miuix.android.content.MiuiIntent;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;
import miuix.core.util.MiuixUIUtils;
import miuix.springback.R;
import miuix.springback.view.SpringBackLayout;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultTrigger extends CustomTrigger {
    private static final String TAG = "DefaultCustomTrigger";
    private static int mIndeterminateTop;
    private Context mContext;
    public Pair<Integer, Integer> mIndeterminateActionPoint;
    public Pair<Integer, Integer> mIndeterminateSimpleActionPoint;
    public Pair<Integer, Integer> mIndeterminateUpActionPoint;
    private ProgressBar mLoadingIndicator;
    private CustomTrigger.OnIndeterminateActionDataListener mOnActionDataListener;
    private BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener mOnIndeterminateActionViewListener;
    private BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener mOnIndeterminateUpActionViewListener;
    private BaseTrigger.SimpleAction.OnSimpleActionViewListener mOnSimpleActionViewListener;
    private CustomTrigger.OnIndeterminateUpActionDataListener mOnUpActionDataListener;
    private View mTrackingIndicator;
    private int mTrackingIndicatorBottom;
    private TextView mTrackingIndicatorLabel;
    private int mTrackingIndicatorLabelBottom;
    private int mTrackingIndicatorLabelTop;
    private ProgressBar mUpLoadingIndicator;
    private ViewGroup mUpTrackingContainer;
    private View mUpTrackingIndicator;
    private TextView mUpTrackingIndicatorLabel;

    private void initSimpleActionView() {
    }

    public DefaultTrigger(Context context) {
        int i;
        super(context);
        this.mTrackingIndicatorBottom = 0;
        this.mTrackingIndicatorLabelTop = 0;
        this.mTrackingIndicatorLabelBottom = 0;
        this.mOnActionDataListener = new CustomTrigger.OnIndeterminateActionDataListener() { // from class: miuix.springback.trigger.DefaultTrigger.1
            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateActionDataListener
            public void onActionLoadCancel(BaseTrigger.IndeterminateAction indeterminateAction) {
            }

            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateActionDataListener
            public void onActionLoadFail(BaseTrigger.IndeterminateAction indeterminateAction) {
            }

            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateActionDataListener
            public void onActionNoData(BaseTrigger.IndeterminateAction indeterminateAction, int i2) {
            }

            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateActionDataListener
            public void onActionStart(BaseTrigger.IndeterminateAction indeterminateAction) {
                DefaultTrigger.this.mLoadingIndicator.setVisibility(0);
                DefaultTrigger.this.mTrackingIndicator.setVisibility(0);
                DefaultTrigger.this.mTrackingIndicatorLabel.setVisibility(0);
                if (indeterminateAction != null) {
                    DefaultTrigger.this.mTrackingIndicatorLabel.setText(indeterminateAction.mTriggerTexts[2]);
                }
            }

            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateActionDataListener
            public void onActionComplete(BaseTrigger.IndeterminateAction indeterminateAction) {
                DefaultTrigger.this.mLoadingIndicator.setVisibility(8);
                if (indeterminateAction != null) {
                    DefaultTrigger.this.mTrackingIndicatorLabel.setText(indeterminateAction.mTriggerTexts[3]);
                }
                if (DefaultTrigger.this.mLayout.springBackEnable()) {
                    return;
                }
                DefaultTrigger defaultTrigger = DefaultTrigger.this;
                defaultTrigger.viewHide(defaultTrigger.getRootContainer());
            }
        };
        this.mOnUpActionDataListener = new CustomTrigger.OnIndeterminateUpActionDataListener() { // from class: miuix.springback.trigger.DefaultTrigger.2
            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateUpActionDataListener
            public void onActionComplete(BaseTrigger.IndeterminateUpAction indeterminateUpAction) {
            }

            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateUpActionDataListener
            public void onActionLoadCancel(BaseTrigger.IndeterminateUpAction indeterminateUpAction) {
            }

            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateUpActionDataListener
            public void onActionNoData(BaseTrigger.IndeterminateUpAction indeterminateUpAction, int i2) {
                if (indeterminateUpAction != null && i2 < 3) {
                    DefaultTrigger.this.mUpTrackingIndicatorLabel.setText(indeterminateUpAction.mTriggerTexts[2]);
                } else if (indeterminateUpAction != null) {
                    DefaultTrigger.this.mUpTrackingIndicatorLabel.setText(indeterminateUpAction.mTriggerTexts[3]);
                }
                DefaultTrigger.this.mUpLoadingIndicator.setVisibility(8);
                DefaultTrigger.this.mUpTrackingIndicator.setVisibility(8);
            }

            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateUpActionDataListener
            public void onActionStart(BaseTrigger.IndeterminateUpAction indeterminateUpAction) {
                DefaultTrigger.this.mUpLoadingIndicator.setVisibility(0);
                DefaultTrigger.this.mUpTrackingIndicatorLabel.setVisibility(0);
                if (indeterminateUpAction != null) {
                    DefaultTrigger.this.mUpTrackingIndicatorLabel.setText(indeterminateUpAction.mTriggerTexts[0]);
                }
            }

            @Override // miuix.springback.trigger.CustomTrigger.OnIndeterminateUpActionDataListener
            public void onActionLoadFail(BaseTrigger.IndeterminateUpAction indeterminateUpAction) {
                DefaultTrigger.this.mUpLoadingIndicator.setVisibility(8);
                DefaultTrigger.this.mUpTrackingIndicator.setVisibility(8);
                if (indeterminateUpAction != null) {
                    DefaultTrigger.this.mUpTrackingIndicatorLabel.setText(indeterminateUpAction.mTriggerTexts[1]);
                }
            }
        };
        this.mOnIndeterminateActionViewListener = new BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener() { // from class: miuix.springback.trigger.DefaultTrigger.3
            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public float getViewRestartOffsetPoint() {
                return -1.0f;
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public void onViewActivated(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public void onViewActivating(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public void onViewEntering(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public void onViewExit(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public void onViewFinished(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public void onViewStarting(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public void onViewStart(int i2) {
                DefaultTrigger.this.mLoadingIndicator.setVisibility(8);
                DefaultTrigger.this.mTrackingIndicator.setVisibility(0);
                DefaultTrigger.this.mTrackingIndicatorLabel.setVisibility(0);
                BaseTrigger.IndeterminateAction indeterminateAction = DefaultTrigger.this.getIndeterminateAction();
                if (indeterminateAction != null) {
                    DefaultTrigger.this.mTrackingIndicatorLabel.setText(indeterminateAction.mTriggerTexts[0]);
                }
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public void onViewEntered(int i2) {
                DefaultTrigger.this.mTrackingIndicator.setVisibility(0);
                DefaultTrigger.this.mTrackingIndicatorLabel.setVisibility(0);
                if (DefaultTrigger.this.isExitSimpleAction()) {
                    DefaultTrigger.this.getIndicatorContainer().setVisibility(8);
                }
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateAction.OnIndeterminateActionViewListener
            public void onViewTriggered(int i2) {
                DefaultTrigger.this.mLoadingIndicator.setVisibility(0);
                DefaultTrigger.this.mTrackingIndicator.setVisibility(0);
                DefaultTrigger.this.mTrackingIndicatorLabel.setVisibility(0);
                BaseTrigger.IndeterminateAction indeterminateAction = DefaultTrigger.this.getIndeterminateAction();
                if (indeterminateAction != null) {
                    DefaultTrigger.this.mTrackingIndicatorLabel.setText(indeterminateAction.mTriggerTexts[2]);
                }
                if (DefaultTrigger.this.mLoadingIndicator.getVisibility() == 0) {
                    DefaultTrigger.this.mLoadingIndicator.setAlpha(1.0f);
                    DefaultTrigger.this.mLoadingIndicator.setScaleX(1.0f);
                    DefaultTrigger.this.mLoadingIndicator.setScaleY(1.0f);
                }
            }
        };
        this.mOnSimpleActionViewListener = new BaseTrigger.SimpleAction.OnSimpleActionViewListener() { // from class: miuix.springback.trigger.DefaultTrigger.4
            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public float getViewRestartOffsetPoint() {
                return -1.0f;
            }

            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public void onViewActivated(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public void onViewActivating(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public void onViewEntering(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public void onViewExit(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public void onViewFinished(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public void onViewStarting(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public void onViewTriggered(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public void onViewStart(int i2) {
                DefaultTrigger.this.getIndicatorContainer().setVisibility(0);
            }

            @Override // miuix.springback.trigger.BaseTrigger.SimpleAction.OnSimpleActionViewListener
            public void onViewEntered(int i2) {
                DefaultTrigger defaultTrigger = DefaultTrigger.this;
                defaultTrigger.viewShow(defaultTrigger.getIndicatorContainer());
                if (DefaultTrigger.this.isExitIndeterminateAction()) {
                    DefaultTrigger.this.mTrackingIndicator.setVisibility(8);
                    DefaultTrigger.this.mTrackingIndicatorLabel.setVisibility(8);
                }
            }
        };
        this.mOnIndeterminateUpActionViewListener = new BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener() { // from class: miuix.springback.trigger.DefaultTrigger.5
            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public float getViewRestartOffsetPoint() {
                return -1.0f;
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public void onViewActivated(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public void onViewActivating(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public void onViewEntered(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public void onViewEntering(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public void onViewExit(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public void onViewFinished(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public void onViewStarting(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public void onViewTriggered(int i2) {
            }

            @Override // miuix.springback.trigger.BaseTrigger.IndeterminateUpAction.OnIndeterminateUpActionViewListener
            public void onViewStart(int i2) {
                DefaultTrigger.this.mUpTrackingContainer.setVisibility(0);
                BaseTrigger.IndeterminateUpAction indeterminateUpAction = DefaultTrigger.this.getIndeterminateUpAction();
                if (indeterminateUpAction == null || !indeterminateUpAction.isNoData()) {
                    if (indeterminateUpAction != null) {
                        DefaultTrigger.this.mUpTrackingIndicator.setVisibility(0);
                        DefaultTrigger.this.mUpLoadingIndicator.setVisibility(0);
                        DefaultTrigger.this.mUpTrackingIndicatorLabel.setVisibility(0);
                        DefaultTrigger.this.mUpTrackingIndicatorLabel.setText(indeterminateUpAction.mTriggerTexts[0]);
                        return;
                    }
                    return;
                }
                DefaultTrigger.this.mUpTrackingIndicator.setVisibility(8);
                DefaultTrigger.this.mUpLoadingIndicator.setVisibility(8);
                if (indeterminateUpAction.getCountNoData() < 3) {
                    DefaultTrigger.this.mUpTrackingIndicatorLabel.setText(indeterminateUpAction.mTriggerTexts[2]);
                } else {
                    DefaultTrigger.this.mUpTrackingIndicatorLabel.setText(indeterminateUpAction.mTriggerTexts[3]);
                }
            }
        };
        this.mContext = context;
        setOnActionDataListener(this.mOnActionDataListener);
        setOnUpActionDataListener(this.mOnUpActionDataListener);
        mIndeterminateTop = context.getResources().getDimensionPixelSize(R.dimen.miuix_sbl_tracking_progress_bg_margintop);
        if (MiuixUIUtils.getFontLevel(context) == 2) {
            i = R.dimen.miuix_sbl_action_indeterminate_distance_large_font;
        } else {
            i = R.dimen.miuix_sbl_action_indeterminate_distance;
        }
        this.mIndeterminateActionPoint = new Pair<>(0, Integer.valueOf(this.mContext.getResources().getDimensionPixelSize(i)));
        this.mIndeterminateUpActionPoint = new Pair<>(0, Integer.valueOf(this.mContext.getResources().getDimensionPixelSize(R.dimen.miuix_sbl_action_upindeterminate_distance)));
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.miuix_sbl_action_simple_enter);
        this.mIndeterminateSimpleActionPoint = new Pair<>(Integer.valueOf(dimensionPixelSize), Integer.valueOf(dimensionPixelSize));
    }

    @Override // miuix.springback.trigger.CustomTrigger, miuix.springback.trigger.BaseTrigger
    public void addAction(BaseTrigger.Action action) {
        super.addAction(action);
        if (action instanceof BaseTrigger.IndeterminateUpAction) {
            initIndeterminateUpActionView();
            BaseTrigger.IndeterminateUpAction indeterminateUpAction = (BaseTrigger.IndeterminateUpAction) action;
            setOnIndeterminateUpActionViewListener(this.mOnIndeterminateUpActionViewListener);
            updateTextIdToString(this.mContext, indeterminateUpAction.mTriggerTextIDs, indeterminateUpAction.mTriggerTexts);
            return;
        }
        if (action instanceof BaseTrigger.IndeterminateAction) {
            initIndeterminateActionView();
            BaseTrigger.IndeterminateAction indeterminateAction = (BaseTrigger.IndeterminateAction) action;
            setOnIndeterminateActionViewListener(this.mOnIndeterminateActionViewListener);
            updateTextIdToString(this.mContext, indeterminateAction.mTriggerTextIDs, indeterminateAction.mTriggerTexts);
            return;
        }
        if (action instanceof BaseTrigger.SimpleAction) {
            initSimpleActionView();
            setOnSimpleActionViewListener(this.mOnSimpleActionViewListener);
        }
    }

    private void initIndeterminateActionView() {
        this.mTrackingIndicator = getRootContainer().findViewById(R.id.tracking_progress);
        this.mTrackingIndicatorLabel = (TextView) getRootContainer().findViewById(R.id.tracking_progress_label);
        this.mLoadingIndicator = (ProgressBar) getRootContainer().findViewById(R.id.loading_progress);
    }

    private void initIndeterminateUpActionView() {
        this.mUpTrackingContainer = (ViewGroup) getIndeterminateUpView().findViewById(R.id.tracking_progress_up_container);
        this.mUpTrackingIndicator = getIndeterminateUpView().findViewById(R.id.tracking_progress_up);
        this.mUpTrackingIndicatorLabel = (TextView) getIndeterminateUpView().findViewById(R.id.tracking_progress_up_label);
        this.mUpLoadingIndicator = (ProgressBar) getIndeterminateUpView().findViewById(R.id.loading_progress_up);
    }

    @Override // miuix.springback.trigger.BaseTrigger
    public boolean containAction(BaseTrigger.Action action) {
        return super.containAction(action);
    }

    @Override // miuix.springback.trigger.CustomTrigger, miuix.springback.trigger.BaseTrigger
    public boolean isActionRunning() {
        return super.isActionRunning();
    }

    @Override // miuix.springback.trigger.CustomTrigger, miuix.springback.trigger.BaseTrigger
    public boolean isActionRunning(BaseTrigger.Action action) {
        return super.isActionRunning(action);
    }

    private void updateTextIdToString(Context context, int[] iArr, String[] strArr) {
        if (iArr != null) {
            for (int i = 0; i < iArr.length; i++) {
                strArr[i] = context.getResources().getString(iArr[i]);
            }
        }
    }

    @Override // miuix.springback.trigger.CustomTrigger
    public void onSpringBackLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (isExitIndeterminateAction()) {
            for (int i9 = 0; i9 < getActions().size(); i9++) {
                BaseTrigger.Action action = getActions().get(i9);
                if (action instanceof BaseTrigger.IndeterminateAction) {
                    BaseTrigger.IndeterminateAction indeterminateAction = (BaseTrigger.IndeterminateAction) action;
                    View view2 = this.mTrackingIndicator;
                    if (view2 != null && mIndeterminateTop >= view2.getTop()) {
                        int i10 = indeterminateAction.mEnterPoint;
                        this.mLoadingIndicator.offsetTopAndBottom(i10);
                        this.mTrackingIndicator.offsetTopAndBottom(i10);
                        this.mTrackingIndicatorLabel.offsetTopAndBottom(i10);
                    }
                }
            }
            View view3 = this.mTrackingIndicator;
            if (view3 != null && view3.getVisibility() == 0 && getCurrentAction() != null && (getCurrentAction() instanceof BaseTrigger.IndeterminateAction)) {
                if (this.mTrackingIndicatorBottom <= 0) {
                    this.mTrackingIndicatorBottom = this.mTrackingIndicator.getBottom();
                }
                TextView textView = this.mTrackingIndicatorLabel;
                if (textView != null && (this.mTrackingIndicatorLabelTop <= 0 || this.mTrackingIndicatorLabelBottom <= 0)) {
                    this.mTrackingIndicatorLabelTop = textView.getTop();
                    this.mTrackingIndicatorLabelBottom = this.mTrackingIndicatorLabel.getBottom();
                }
                if ((this.mLoadingIndicator.getVisibility() == 8 || this.mLoadingIndicator.getVisibility() == 4) && getCurrentState() != this.mActionComplete && getRootContainer().getHeight() > getCurrentAction().mTriggerPoint) {
                    this.mTrackingIndicator.setBottom(this.mTrackingIndicatorBottom + (getRootContainer().getHeight() - getCurrentAction().mTriggerPoint));
                }
            }
        }
        if (isExitSimpleAction() && getIndicatorContainer() != null && getIndicatorContainer().getVisibility() == 0 && getIndicatorContainer().getTop() == 0) {
            getIndicatorContainer().offsetTopAndBottom(this.mScrollDistance - getIndicatorContainer().getMeasuredHeight());
        }
    }

    @Override // miuix.springback.trigger.CustomTrigger
    public void onSpringBackScrolled(SpringBackLayout springBackLayout, int i, int i2, int i3) {
        ViewGroup rootContainer = getRootContainer();
        if (isShowContainer()) {
            if (rootContainer.getVisibility() != 0) {
                rootContainer.setVisibility(0);
            }
            rootContainer.setAlpha(1.0f);
        } else {
            rootContainer.setVisibility(4);
        }
        if (i3 < 0 && isExitIndeterminateUpAction() && getCurrentAction() != null && (getCurrentAction() instanceof BaseTrigger.IndeterminateUpAction)) {
            this.mUpTrackingContainer.setTranslationY(Math.max(getIndeterminateUpView().getHeight() - getIndeterminateUpAction().mTriggerPoint, 0));
        }
        if (isExitIndeterminateAction() && getCurrentAction() != null && (getCurrentAction() instanceof BaseTrigger.IndeterminateAction)) {
            BaseTrigger.IndeterminateAction indeterminateAction = (BaseTrigger.IndeterminateAction) getCurrentAction();
            if (this.mTrackingIndicator.getVisibility() == 0) {
                this.mTrackingIndicatorBottom = this.mTrackingIndicator.getTop() + this.mTrackingIndicator.getWidth();
                this.mTrackingIndicatorLabelTop = this.mTrackingIndicatorLabel.getTop();
                this.mTrackingIndicatorLabelBottom = this.mTrackingIndicatorLabel.getBottom();
                float f = indeterminateAction.mTriggerPoint;
                float fMax = Math.max(0.0f, Math.min(getRootContainer().getHeight() / f, 1.0f));
                float f2 = 0.5f * f;
                float fMax2 = Math.max(0.0f, ((float) getRootContainer().getHeight()) < f2 ? 0.0f : Math.min((getRootContainer().getHeight() - f2) / f2, 1.0f));
                float fMax3 = Math.max(0.0f, ((float) getRootContainer().getHeight()) < f2 ? 0.0f : Math.min((getRootContainer().getHeight() - (0.7f * f)) / (f * 0.3f), 1.0f));
                float f3 = (-this.mTrackingIndicator.getWidth()) * (1.0f - fMax);
                this.mTrackingIndicator.setAlpha(fMax2);
                this.mTrackingIndicator.setScaleX(fMax);
                this.mTrackingIndicator.setScaleY(fMax);
                this.mTrackingIndicatorLabel.setAlpha(fMax3);
                this.mTrackingIndicatorLabel.setTop(this.mTrackingIndicatorLabelTop);
                this.mTrackingIndicatorLabel.setBottom(this.mTrackingIndicatorLabelBottom);
                if (this.mLoadingIndicator.getVisibility() == 0) {
                    this.mLoadingIndicator.setAlpha(fMax2);
                    this.mLoadingIndicator.setScaleX(fMax);
                    this.mLoadingIndicator.setScaleY(fMax);
                }
                if (getRootContainer().getHeight() < indeterminateAction.mTriggerPoint) {
                    if (fMax3 > 0.0f) {
                        this.mTrackingIndicatorLabel.setTranslationY(f3);
                    }
                    if (getCurrentState() == this.mTracking) {
                        this.mTrackingIndicatorLabel.setText(indeterminateAction.mTriggerTexts[0]);
                    }
                    this.mTrackingIndicator.setBottom(this.mTrackingIndicatorBottom);
                } else if (getRootContainer().getHeight() >= indeterminateAction.mTriggerPoint) {
                    int height = this.mTrackingIndicatorBottom + (getRootContainer().getHeight() - indeterminateAction.mTriggerPoint);
                    if (this.mLoadingIndicator.getVisibility() != 0 && getCurrentState() != this.mActionComplete) {
                        this.mTrackingIndicator.setBottom(height);
                        this.mTrackingIndicatorLabel.setTranslationY(getRootContainer().getHeight() - indeterminateAction.mTriggerPoint);
                    } else {
                        this.mTrackingIndicatorLabel.setTranslationY(0.0f);
                    }
                    if (getCurrentState() == this.mTracking) {
                        this.mTrackingIndicatorLabel.setText(indeterminateAction.mTriggerTexts[1]);
                    }
                }
            }
        }
        if (isExitSimpleAction() && getCurrentAction() != null && (getCurrentAction() instanceof BaseTrigger.SimpleAction) && getRootContainer().getHeight() < getCurrentAction().mEnterPoint) {
            getIndicatorContainer().setVisibility(8);
        } else if (isExitSimpleAction() && getCurrentAction() != null && (getCurrentAction() instanceof BaseTrigger.SimpleAction) && getRootContainer().getHeight() >= getCurrentAction().mEnterPoint && getIndicatorContainer().getVisibility() == 8) {
            getIndicatorContainer().setVisibility(0);
            viewShow(getIndicatorContainer());
        }
        if (isExitSimpleAction() && getCurrentAction() != null && getIndicatorContainer().getVisibility() == 0) {
            getIndicatorContainer().offsetTopAndBottom(-i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void viewShow(View view) {
        if (view != null) {
            view.setVisibility(0);
            AnimState animStateAdd = new AnimState("start").add(ViewProperty.ALPHA, 0.0d).add(ViewProperty.TRANSLATION_Y, -180.0d);
            AnimState animStateAdd2 = new AnimState(NewsTable.Columns.SHOW).add(ViewProperty.ALPHA, 1.0d).add(ViewProperty.TRANSLATION_Y, 25.0d);
            Folme.useAt(view).state().setFlags(1L).fromTo(animStateAdd, animStateAdd2, new AnimConfig().setEase(EaseManager.getStyle(4, 120.0f))).then(new AnimState(MiuiIntent.COMMAND_ICON_PANEL_HIDE).add(ViewProperty.ALPHA, 1.0d).add(ViewProperty.TRANSLATION_Y, 0.0d), new AnimConfig().setEase(EaseManager.getStyle(4, 40.0f)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void viewHide(View view) {
        if (view != null) {
            Folme.useAt(view).state().setFlags(1L).to(new AnimState(MiuiIntent.COMMAND_ICON_PANEL_HIDE).add(ViewProperty.AUTO_ALPHA, 0.0d), new AnimConfig().setEase(EaseManager.getStyle(4, 240.0f)));
        }
    }
}
