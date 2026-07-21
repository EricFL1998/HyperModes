package miuix.springback.trigger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import miuix.core.util.MiuixUIUtils;
import miuix.springback.R;

/* JADX INFO: loaded from: classes3.dex */
public abstract class BaseTrigger {
    private static int mDefaultIndeterminateDistance;
    private static int mDefaultSimpleEnter;
    private static int mDefaultSimpleTrigger;
    private static int mDefaultUpIndeterminateDistance;
    private List<Action> mActions = new ArrayList();
    private Action mUpAction;

    public abstract boolean isActionRunning();

    public abstract boolean isActionRunning(Action action);

    public BaseTrigger(Context context) {
        int i;
        boolean z = MiuixUIUtils.getFontLevel(context) == 2;
        if (z) {
            i = R.dimen.miuix_sbl_action_indeterminate_distance_large_font;
        } else {
            i = R.dimen.miuix_sbl_action_indeterminate_distance;
        }
        mDefaultIndeterminateDistance = context.getResources().getDimensionPixelSize(i);
        boolean zIsTallFontLang = MiuixUIUtils.isTallFontLang(context);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.miuix_sbl_action_upindeterminate_distance);
        mDefaultUpIndeterminateDistance = dimensionPixelSize;
        if (zIsTallFontLang && z) {
            dimensionPixelSize = (int) (dimensionPixelSize * 1.8f);
        }
        mDefaultUpIndeterminateDistance = dimensionPixelSize;
        int dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R.dimen.miuix_sbl_action_simple_enter);
        mDefaultSimpleEnter = dimensionPixelSize2;
        mDefaultSimpleTrigger = dimensionPixelSize2;
    }

    public List<Action> getActions() {
        return this.mActions;
    }

    public IndeterminateAction getIndeterminateAction() {
        for (int i = 0; i < this.mActions.size(); i++) {
            Action action = this.mActions.get(i);
            if (action != null && (action instanceof IndeterminateAction)) {
                return (IndeterminateAction) action;
            }
        }
        return null;
    }

    public IndeterminateUpAction getIndeterminateUpAction() {
        return (IndeterminateUpAction) this.mUpAction;
    }

    public SimpleAction getSimpleAction() {
        for (int i = 0; i < this.mActions.size(); i++) {
            Action action = this.mActions.get(i);
            if (action != null && (action instanceof SimpleAction)) {
                return (SimpleAction) action;
            }
        }
        return null;
    }

    public void addAction(Action action) {
        if (action instanceof IndeterminateUpAction) {
            this.mUpAction = action;
            return;
        }
        int iBinarySearch = Collections.binarySearch(this.mActions, action, Action.DISTANCE_COMPARATOR);
        if (iBinarySearch < 0) {
            this.mActions.add((-iBinarySearch) - 1, action);
            return;
        }
        throw new IllegalArgumentException("action conflict.");
    }

    public boolean removeAction(Action action) {
        if (isActionRunning()) {
            return false;
        }
        if (action instanceof IndeterminateUpAction) {
            ((IndeterminateUpAction) action).mUpDataListener = null;
            this.mUpAction = null;
            return true;
        }
        if (action != null && this.mActions.contains(action)) {
            if (action instanceof IndeterminateAction) {
                ((IndeterminateAction) action).mCompleteListener = null;
            }
            this.mActions.remove(action);
        }
        return true;
    }

    public boolean containAction(Action action) {
        if (action instanceof IndeterminateUpAction) {
            return this.mUpAction != null;
        }
        return action != null && this.mActions.contains(action);
    }

    public static abstract class Action {
        static final Comparator<Action> DISTANCE_COMPARATOR = new Comparator<Action>() { // from class: miuix.springback.trigger.BaseTrigger.Action.1
            @Override // java.util.Comparator
            public int compare(Action action, Action action2) {
                return Integer.compare(action.mEnterPoint, action2.mEnterPoint);
            }
        };
        public int mEnterPoint;
        public int mTriggerPoint;

        protected abstract void onActivated();

        protected View onCreateIndicator(LayoutInflater layoutInflater, ViewGroup viewGroup) {
            return null;
        }

        protected abstract void onEntered();

        protected abstract void onExit();

        protected abstract void onFinished();

        protected abstract void onTriggered();

        Action(int i, int i2) {
            if (i < 0 || i2 < 0 || i2 < i) {
                throw new IllegalArgumentException("not allow enterPoint < 0 or triggerPoint < 0 or triggerPoint < enterPoint!");
            }
            this.mEnterPoint = i;
            this.mTriggerPoint = i2;
        }

        void notifyTriggered() {
            onTriggered();
        }

        void notifyEntered() {
            onEntered();
        }

        void notifyExit() {
            onExit();
        }

        void notifyActivated() {
            onActivated();
        }

        void notifyFinished() {
            onFinished();
        }
    }

    public static abstract class SimpleAction extends Action {

        public interface OnSimpleActionViewListener {
            float getViewRestartOffsetPoint();

            void onViewActivated(int i);

            void onViewActivating(int i);

            void onViewEntered(int i);

            void onViewEntering(int i);

            void onViewExit(int i);

            void onViewFinished(int i);

            void onViewStart(int i);

            void onViewStarting(int i);

            void onViewTriggered(int i);
        }

        public SimpleAction() {
            super(BaseTrigger.mDefaultSimpleEnter, BaseTrigger.mDefaultSimpleTrigger);
        }

        public SimpleAction(int i, int i2) {
            super(i, i2);
        }
    }

    public static abstract class IndeterminateAction extends Action {
        public static final int DEFAULT_OFFSET_POINT = 0;
        static final int[] DEFAULT_TRIGGER_TEXTIDS = {R.string.miuix_sbl_tracking_progress_labe_pull_to_refresh, R.string.miuix_sbl_tracking_progress_labe_release_to_refresh, R.string.miuix_sbl_tracking_progress_labe_refreshing, R.string.miuix_sbl_tracking_progress_labe_refreshed};
        protected OnActionCompleteListener mCompleteListener;
        private int mCountNoData;
        public int[] mTriggerTextIDs;
        public String[] mTriggerTexts;

        interface OnActionCompleteListener {
            void onActionComplete(IndeterminateAction indeterminateAction);

            void onActionLoadCancel(IndeterminateAction indeterminateAction);

            void onActionLoadFail(IndeterminateAction indeterminateAction);

            void onActionNoData(IndeterminateAction indeterminateAction, int i);

            void onActionStart(IndeterminateAction indeterminateAction);

            void onUpdateTriggerTextIndex(IndeterminateAction indeterminateAction, int i, String str);
        }

        public interface OnIndeterminateActionViewListener {
            float getViewRestartOffsetPoint();

            void onViewActivated(int i);

            void onViewActivating(int i);

            void onViewEntered(int i);

            void onViewEntering(int i);

            void onViewExit(int i);

            void onViewFinished(int i);

            void onViewStart(int i);

            void onViewStarting(int i);

            void onViewTriggered(int i);
        }

        public boolean isNoData() {
            return this.mCountNoData > 0;
        }

        public int getCountNoData() {
            return this.mCountNoData;
        }

        public IndeterminateAction(int i) {
            super(i, BaseTrigger.mDefaultIndeterminateDistance + i);
            int[] iArr = DEFAULT_TRIGGER_TEXTIDS;
            this.mTriggerTexts = new String[iArr.length];
            this.mCountNoData = 0;
            if (i < 0) {
                throw new IllegalArgumentException("invalid offsetPoint");
            }
            this.mTriggerTextIDs = iArr;
        }

        public IndeterminateAction(int i, int[] iArr) {
            super(i, BaseTrigger.mDefaultIndeterminateDistance + i);
            int[] iArr2 = DEFAULT_TRIGGER_TEXTIDS;
            this.mTriggerTexts = new String[iArr2.length];
            this.mCountNoData = 0;
            if (i < 0) {
                throw new IllegalArgumentException("invalid offsetPoint");
            }
            if (iArr == null || iArr.length != iArr2.length) {
                throw new IllegalArgumentException("invalid text Ids");
            }
            this.mTriggerTextIDs = iArr;
        }

        public IndeterminateAction(int i, int i2) {
            super(i, i2);
            int[] iArr = DEFAULT_TRIGGER_TEXTIDS;
            this.mTriggerTexts = new String[iArr.length];
            this.mCountNoData = 0;
            this.mTriggerTextIDs = iArr;
        }

        public IndeterminateAction(int i, int i2, int[] iArr) {
            super(i, i2);
            int[] iArr2 = DEFAULT_TRIGGER_TEXTIDS;
            this.mTriggerTexts = new String[iArr2.length];
            this.mCountNoData = 0;
            if (iArr == null || iArr.length != iArr2.length) {
                throw new IllegalArgumentException("invalid text Ids");
            }
            this.mTriggerTextIDs = iArr;
        }

        public void notifyActionNoData() {
            OnActionCompleteListener onActionCompleteListener = this.mCompleteListener;
            if (onActionCompleteListener != null) {
                int i = this.mCountNoData + 1;
                this.mCountNoData = i;
                onActionCompleteListener.onActionNoData(this, i);
            }
        }

        public void notifyLoadComplete() {
            OnActionCompleteListener onActionCompleteListener = this.mCompleteListener;
            if (onActionCompleteListener != null) {
                onActionCompleteListener.onActionComplete(this);
            }
        }

        public void notifyLoadFail() {
            OnActionCompleteListener onActionCompleteListener = this.mCompleteListener;
            if (onActionCompleteListener != null) {
                onActionCompleteListener.onActionLoadFail(this);
            }
        }

        public void startIndeterminateAction() {
            OnActionCompleteListener onActionCompleteListener = this.mCompleteListener;
            if (onActionCompleteListener != null) {
                onActionCompleteListener.onActionStart(this);
            }
        }

        public void notifyTriggerTextIndex(int i, String str) {
            if (i >= DEFAULT_TRIGGER_TEXTIDS.length) {
                throw new IllegalArgumentException("invalid index");
            }
            OnActionCompleteListener onActionCompleteListener = this.mCompleteListener;
            if (onActionCompleteListener != null) {
                onActionCompleteListener.onUpdateTriggerTextIndex(this, i, str);
            }
        }

        public void notifyLoadCancel() {
            OnActionCompleteListener onActionCompleteListener = this.mCompleteListener;
            if (onActionCompleteListener != null) {
                onActionCompleteListener.onActionLoadCancel(this);
            }
        }
    }

    public static abstract class IndeterminateUpAction extends Action {
        public static final int DEFAULT_OFFSET_POINT = 0;
        static final int[] DEFAULT_TRIGGER_TEXTIDS = {R.string.miuix_sbl_tracking_progress_labe_up_refresh, R.string.miuix_sbl_tracking_progress_labe_up_refresh_fail, R.string.miuix_sbl_tracking_progress_labe_up_nodata, R.string.miuix_sbl_tracking_progress_labe_up_none};
        private int mCountNoData;
        public int[] mTriggerTextIDs;
        public String[] mTriggerTexts;
        protected OnUpActionDataListener mUpDataListener;

        public interface OnIndeterminateUpActionViewListener {
            float getViewRestartOffsetPoint();

            void onViewActivated(int i);

            void onViewActivating(int i);

            void onViewEntered(int i);

            void onViewEntering(int i);

            void onViewExit(int i);

            void onViewFinished(int i);

            void onViewStart(int i);

            void onViewStarting(int i);

            void onViewTriggered(int i);
        }

        interface OnUpActionDataListener {
            void onActionComplete(IndeterminateUpAction indeterminateUpAction);

            void onActionLoadCancel(IndeterminateUpAction indeterminateUpAction);

            void onActionLoadFail(IndeterminateUpAction indeterminateUpAction);

            void onActionNoData(IndeterminateUpAction indeterminateUpAction, int i);

            void onActionStart(IndeterminateUpAction indeterminateUpAction);

            void onUpdateTriggerTextIndex(IndeterminateUpAction indeterminateUpAction, int i, String str);
        }

        public boolean isNoData() {
            return this.mCountNoData > 0;
        }

        public int getCountNoData() {
            return this.mCountNoData;
        }

        public IndeterminateUpAction(int i) {
            super(i, BaseTrigger.mDefaultUpIndeterminateDistance + i);
            int[] iArr = DEFAULT_TRIGGER_TEXTIDS;
            this.mTriggerTexts = new String[iArr.length];
            this.mCountNoData = 0;
            if (i < 0) {
                throw new IllegalArgumentException("invalid offsetPoint");
            }
            this.mTriggerTextIDs = iArr;
        }

        public IndeterminateUpAction(int i, int[] iArr) {
            super(i, BaseTrigger.mDefaultUpIndeterminateDistance + i);
            int[] iArr2 = DEFAULT_TRIGGER_TEXTIDS;
            this.mTriggerTexts = new String[iArr2.length];
            this.mCountNoData = 0;
            if (i < 0) {
                throw new IllegalArgumentException("invalid offsetPoint");
            }
            if (iArr == null || iArr.length != iArr2.length) {
                throw new IllegalArgumentException("invalid text Ids");
            }
            this.mTriggerTextIDs = iArr;
        }

        public IndeterminateUpAction(int i, int i2) {
            super(i, i2);
            int[] iArr = DEFAULT_TRIGGER_TEXTIDS;
            this.mTriggerTexts = new String[iArr.length];
            this.mCountNoData = 0;
            this.mTriggerTextIDs = iArr;
        }

        public IndeterminateUpAction(int i, int i2, int[] iArr) {
            super(i, i2);
            int[] iArr2 = DEFAULT_TRIGGER_TEXTIDS;
            this.mTriggerTexts = new String[iArr2.length];
            this.mCountNoData = 0;
            if (iArr == null || iArr.length != iArr2.length) {
                throw new IllegalArgumentException("invalid text Ids");
            }
            this.mTriggerTextIDs = iArr;
        }

        public void notifyActionNoData() {
            OnUpActionDataListener onUpActionDataListener = this.mUpDataListener;
            if (onUpActionDataListener != null) {
                int i = this.mCountNoData + 1;
                this.mCountNoData = i;
                onUpActionDataListener.onActionNoData(this, i);
            }
        }

        public void notifyLoadComplete() {
            OnUpActionDataListener onUpActionDataListener = this.mUpDataListener;
            if (onUpActionDataListener != null) {
                this.mCountNoData = 0;
                onUpActionDataListener.onActionComplete(this);
            }
        }

        public void notifyTriggerTextIndex(int i, String str) {
            if (i >= DEFAULT_TRIGGER_TEXTIDS.length) {
                throw new IllegalArgumentException("invalid index");
            }
            OnUpActionDataListener onUpActionDataListener = this.mUpDataListener;
            if (onUpActionDataListener != null) {
                onUpActionDataListener.onUpdateTriggerTextIndex(this, i, str);
            }
        }

        public void notifyLoadFail() {
            OnUpActionDataListener onUpActionDataListener = this.mUpDataListener;
            if (onUpActionDataListener != null) {
                onUpActionDataListener.onActionLoadFail(this);
            }
        }

        public void startIndeterminateUpAction() {
            OnUpActionDataListener onUpActionDataListener = this.mUpDataListener;
            if (onUpActionDataListener != null) {
                this.mCountNoData = 0;
                onUpActionDataListener.onActionStart(this);
            }
        }

        public void notifyLoadCancel() {
            OnUpActionDataListener onUpActionDataListener = this.mUpDataListener;
            if (onUpActionDataListener != null) {
                onUpActionDataListener.onActionLoadCancel(this);
            }
        }
    }
}
