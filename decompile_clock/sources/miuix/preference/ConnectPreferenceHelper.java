package miuix.preference;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;

/* JADX INFO: loaded from: classes3.dex */
public class ConnectPreferenceHelper {
    public static final float BACKGROUND_ANIM_FACTOR = 1.5f;
    public static final int BACKGROUND_ANIM_TIME = 300;
    private static final int[] STATE_ATTR_CONNECTED = {R.attr.state_connected};
    private static final int[] STATE_ATTR_DISCONNECTED = {-R.attr.state_connected};
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_DISCONNECTED = 0;
    public static final String TAG = "ConnectPreferenceHelper";
    private Drawable bgDrawableConnected;
    private LayerDrawable bgDrawableParent;
    private AnimatedVectorDrawable connectingAnimDrawable;
    private ColorStateList iconColorList;
    private Context mContext;
    private ValueAnimator mDisConnectedToConnectedBgAnim;
    private ValueAnimator mDisConnectedToConnectedIconAnim;
    private ValueAnimator mDisConnectedToConnectedSummaryAnim;
    private ValueAnimator mDisConnectedToConnectedTitleAnim;
    private Preference mPreference;
    private TextView mSummaryView;
    private TextView mTitleView;
    private View mWidgetView;
    private ColorStateList summaryColorList;
    private ColorStateList titleColorList;
    private int mState = -1;
    private int mLastState = -1;
    private boolean mIconAnimEnabled = true;

    public ConnectPreferenceHelper(Context context, Preference preference) {
        this.mContext = context;
        this.mPreference = preference;
        this.titleColorList = ContextCompat.getColorStateList(context, R.color.miuix_preference_connect_title_color);
        this.summaryColorList = ContextCompat.getColorStateList(context, R.color.miuix_preference_connect_summary_color);
        this.iconColorList = ContextCompat.getColorStateList(context, R.color.miuix_preference_connect_icon_color);
        initAnim(context);
    }

    public void setIconAnimEnabled(boolean z) {
        this.mIconAnimEnabled = z;
    }

    private void initAnim(Context context) {
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.miuix_preference_ic_bg_connect);
        this.bgDrawableParent = layerDrawable;
        if (layerDrawable == null) {
            return;
        }
        this.connectingAnimDrawable = (AnimatedVectorDrawable) this.bgDrawableParent.findDrawableByLayerId(R.id.anim_preference_connecting);
        this.bgDrawableConnected = this.bgDrawableParent.findDrawableByLayerId(R.id.shape_preference_connected);
        ColorStateList colorStateList = this.titleColorList;
        int[] iArr = STATE_ATTR_DISCONNECTED;
        final int colorForState = colorStateList.getColorForState(iArr, R.color.miuix_preference_connect_title_disconnected_color);
        ColorStateList colorStateList2 = this.titleColorList;
        int[] iArr2 = STATE_ATTR_CONNECTED;
        final int colorForState2 = colorStateList2.getColorForState(iArr2, R.color.miuix_preference_connect_title_connected_color);
        final int colorForState3 = this.summaryColorList.getColorForState(iArr, R.color.miuix_preference_connect_summary_disconnected_color);
        final int colorForState4 = this.summaryColorList.getColorForState(iArr2, R.color.miuix_preference_connect_summary_connected_color);
        final int colorForState5 = this.iconColorList.getColorForState(iArr, R.color.miuix_preference_connect_icon_disconnected_color);
        final int colorForState6 = this.iconColorList.getColorForState(iArr2, R.color.miuix_preference_connect_icon_connected_color);
        ValueAnimator valueAnimatorOfArgb = ValueAnimator.ofArgb(colorForState5, colorForState6);
        this.mDisConnectedToConnectedIconAnim = valueAnimatorOfArgb;
        valueAnimatorOfArgb.setDuration(300L);
        this.mDisConnectedToConnectedIconAnim.addListener(new AnimatorListenerAdapter() { // from class: miuix.preference.ConnectPreferenceHelper.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                Drawable icon = ConnectPreferenceHelper.this.mPreference.getIcon();
                if (icon == null || !ConnectPreferenceHelper.this.mIconAnimEnabled) {
                    return;
                }
                if (ConnectPreferenceHelper.this.mState == 1) {
                    DrawableCompat.setTint(icon, colorForState6);
                } else {
                    DrawableCompat.setTint(icon, colorForState5);
                }
            }
        });
        this.mDisConnectedToConnectedIconAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.preference.ConnectPreferenceHelper.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Drawable icon = ConnectPreferenceHelper.this.mPreference.getIcon();
                if (icon == null || !ConnectPreferenceHelper.this.mIconAnimEnabled) {
                    return;
                }
                DrawableCompat.setTint(icon, ((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        });
        ValueAnimator valueAnimatorOfArgb2 = ValueAnimator.ofArgb(colorForState, colorForState2);
        this.mDisConnectedToConnectedTitleAnim = valueAnimatorOfArgb2;
        valueAnimatorOfArgb2.setDuration(300L);
        this.mDisConnectedToConnectedTitleAnim.addListener(new AnimatorListenerAdapter() { // from class: miuix.preference.ConnectPreferenceHelper.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (ConnectPreferenceHelper.this.mTitleView != null) {
                    if (ConnectPreferenceHelper.this.mState == 1) {
                        ConnectPreferenceHelper.this.mTitleView.setTextColor(colorForState2);
                    } else {
                        ConnectPreferenceHelper.this.mTitleView.setTextColor(colorForState);
                    }
                }
            }
        });
        this.mDisConnectedToConnectedTitleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.preference.ConnectPreferenceHelper.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (ConnectPreferenceHelper.this.mTitleView != null) {
                    ConnectPreferenceHelper.this.mTitleView.setTextColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
                }
            }
        });
        ValueAnimator valueAnimatorOfArgb3 = ValueAnimator.ofArgb(colorForState3, colorForState4);
        this.mDisConnectedToConnectedSummaryAnim = valueAnimatorOfArgb3;
        valueAnimatorOfArgb3.setDuration(300L);
        this.mDisConnectedToConnectedSummaryAnim.addListener(new AnimatorListenerAdapter() { // from class: miuix.preference.ConnectPreferenceHelper.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (ConnectPreferenceHelper.this.mSummaryView != null) {
                    if (ConnectPreferenceHelper.this.mState == 1) {
                        ConnectPreferenceHelper.this.mSummaryView.setTextColor(colorForState4);
                    } else {
                        ConnectPreferenceHelper.this.mSummaryView.setTextColor(colorForState3);
                    }
                }
            }
        });
        this.mDisConnectedToConnectedSummaryAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.preference.ConnectPreferenceHelper.6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (ConnectPreferenceHelper.this.mSummaryView != null) {
                    ConnectPreferenceHelper.this.mSummaryView.setTextColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
                }
            }
        });
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(0, 255);
        this.mDisConnectedToConnectedBgAnim = valueAnimatorOfInt;
        valueAnimatorOfInt.setDuration(300L);
        this.mDisConnectedToConnectedBgAnim.addListener(new AnimatorListenerAdapter() { // from class: miuix.preference.ConnectPreferenceHelper.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (ConnectPreferenceHelper.this.mState != 2 && ConnectPreferenceHelper.this.connectingAnimDrawable != null && ConnectPreferenceHelper.this.connectingAnimDrawable.isRunning()) {
                    ConnectPreferenceHelper.this.connectingAnimDrawable.stop();
                }
                if (ConnectPreferenceHelper.this.mState == 1) {
                    ConnectPreferenceHelper.this.bgDrawableConnected.setAlpha(255);
                } else {
                    ConnectPreferenceHelper.this.bgDrawableConnected.setAlpha(0);
                }
            }
        });
        this.mDisConnectedToConnectedBgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.preference.ConnectPreferenceHelper.8
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ConnectPreferenceHelper.this.bgDrawableConnected.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        });
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, View view) {
        if (view == null || preferenceViewHolder == null) {
            return;
        }
        view.setBackground(this.bgDrawableParent);
        preferenceViewHolder.itemView.setBackground(null);
        TextView textView = (TextView) preferenceViewHolder.findViewById(android.R.id.title);
        this.mTitleView = textView;
        if (textView == null) {
            this.mTitleView = (TextView) preferenceViewHolder.findViewById(android.R.id.text1);
        }
        this.mSummaryView = (TextView) preferenceViewHolder.findViewById(android.R.id.summary);
        View viewFindViewById = preferenceViewHolder.findViewById(R.id.preference_detail);
        this.mWidgetView = viewFindViewById;
        setAlphaFolme(viewFindViewById);
        if (this.mLastState == -1) {
            int i = this.mState;
            if (i == -1) {
                initConnectState(0);
                updateState(false);
                return;
            } else {
                updateState(i == 2);
                return;
            }
        }
        updateState(false);
    }

    private void updateWidgetDrawable(int[] iArr) {
        View view = this.mWidgetView;
        if (view instanceof ImageView) {
            if (iArr == STATE_ATTR_CONNECTED) {
                ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.miuix_preference_ic_detail_connected));
                return;
            }
            TypedValue typedValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(R.attr.connectDetailDisconnectedDrawable, typedValue, true);
            ((ImageView) this.mWidgetView).setImageDrawable(ContextCompat.getDrawable(this.mContext, typedValue.resourceId));
        }
    }

    private void updateStateConnecting() {
        this.bgDrawableConnected.setAlpha(0);
        checkAndUpdateStateToDisconnected();
        AnimatedVectorDrawable animatedVectorDrawable = this.connectingAnimDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.setAlpha(255);
            if (!this.connectingAnimDrawable.isRunning()) {
                this.connectingAnimDrawable.start();
            }
        }
        int[] iArr = STATE_ATTR_DISCONNECTED;
        updateViewColorList(iArr);
        updateWidgetDrawable(iArr);
    }

    private void checkAndUpdateStateToDisconnected() {
        if (this.mLastState == 1) {
            updateViewColorList(STATE_ATTR_DISCONNECTED);
            this.bgDrawableConnected.setAlpha(0);
            cancelDisConnectedToConnectedAnim();
        }
    }

    private void cancelDisConnectedToConnectedAnim() {
        if (this.mDisConnectedToConnectedBgAnim.isRunning()) {
            this.mDisConnectedToConnectedBgAnim.cancel();
        }
        if (this.mDisConnectedToConnectedTitleAnim.isRunning()) {
            this.mDisConnectedToConnectedTitleAnim.cancel();
        }
        if (this.mDisConnectedToConnectedSummaryAnim.isRunning()) {
            this.mDisConnectedToConnectedSummaryAnim.cancel();
        }
        if (this.mDisConnectedToConnectedIconAnim.isRunning()) {
            this.mDisConnectedToConnectedIconAnim.cancel();
        }
    }

    private void updateStateDisconnected(boolean z) {
        if (z) {
            if (this.mLastState == 1) {
                updateViewColorList(STATE_ATTR_CONNECTED);
                this.bgDrawableConnected.setAlpha(255);
                startConnectedToDisConnectedAnim();
            } else {
                this.bgDrawableConnected.setAlpha(0);
                updateViewColorList(STATE_ATTR_DISCONNECTED);
            }
        } else {
            if (this.mState == 0 && !this.mDisConnectedToConnectedBgAnim.isRunning()) {
                this.bgDrawableConnected.setAlpha(0);
            }
            if (this.mState == 0 && !this.mDisConnectedToConnectedTitleAnim.isRunning()) {
                updateViewColorList(STATE_ATTR_DISCONNECTED);
            }
        }
        AnimatedVectorDrawable animatedVectorDrawable = this.connectingAnimDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.stop();
            this.connectingAnimDrawable.setAlpha(0);
        }
        updateWidgetDrawable(STATE_ATTR_DISCONNECTED);
    }

    private void updateStateConnected(boolean z) {
        if (z) {
            updateViewColorList(STATE_ATTR_DISCONNECTED);
            startDisConnectedToConnectedAnim();
        } else {
            if (this.mState == 1 && !this.mDisConnectedToConnectedBgAnim.isRunning()) {
                this.bgDrawableConnected.setAlpha(255);
            }
            if (this.mState == 1 && !this.mDisConnectedToConnectedTitleAnim.isRunning()) {
                updateViewColorList(STATE_ATTR_CONNECTED);
            }
        }
        updateWidgetDrawable(STATE_ATTR_CONNECTED);
    }

    private void updateState(boolean z) {
        int i = this.mState;
        if (i == 0) {
            updateStateDisconnected(z);
        } else if (i == 1) {
            updateStateConnected(z);
        } else {
            if (i != 2) {
                return;
            }
            updateStateConnecting();
        }
    }

    private void startConnectedToDisConnectedAnim() {
        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator(1.5f);
        if (this.mDisConnectedToConnectedBgAnim.isRunning()) {
            this.mDisConnectedToConnectedBgAnim.cancel();
        }
        this.mDisConnectedToConnectedBgAnim.setInterpolator(accelerateInterpolator);
        this.mDisConnectedToConnectedBgAnim.reverse();
        if (this.mDisConnectedToConnectedTitleAnim.isRunning()) {
            this.mDisConnectedToConnectedTitleAnim.cancel();
        }
        this.mDisConnectedToConnectedTitleAnim.setInterpolator(accelerateInterpolator);
        this.mDisConnectedToConnectedTitleAnim.reverse();
        if (this.mDisConnectedToConnectedSummaryAnim.isRunning()) {
            this.mDisConnectedToConnectedSummaryAnim.cancel();
        }
        this.mDisConnectedToConnectedSummaryAnim.setInterpolator(accelerateInterpolator);
        this.mDisConnectedToConnectedSummaryAnim.reverse();
        if (this.mDisConnectedToConnectedIconAnim.isRunning()) {
            this.mDisConnectedToConnectedIconAnim.cancel();
        }
        this.mDisConnectedToConnectedIconAnim.setInterpolator(accelerateInterpolator);
        this.mDisConnectedToConnectedIconAnim.reverse();
    }

    private void startDisConnectedToConnectedAnim() {
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);
        if (this.mDisConnectedToConnectedBgAnim.isRunning()) {
            this.mDisConnectedToConnectedBgAnim.cancel();
        }
        this.mDisConnectedToConnectedBgAnim.setInterpolator(decelerateInterpolator);
        this.mDisConnectedToConnectedBgAnim.start();
        if (this.mDisConnectedToConnectedTitleAnim.isRunning()) {
            this.mDisConnectedToConnectedTitleAnim.cancel();
        }
        this.mDisConnectedToConnectedTitleAnim.setInterpolator(decelerateInterpolator);
        this.mDisConnectedToConnectedTitleAnim.start();
        if (this.mDisConnectedToConnectedSummaryAnim.isRunning()) {
            this.mDisConnectedToConnectedSummaryAnim.cancel();
        }
        this.mDisConnectedToConnectedSummaryAnim.setInterpolator(decelerateInterpolator);
        this.mDisConnectedToConnectedSummaryAnim.start();
        if (this.mDisConnectedToConnectedIconAnim.isRunning()) {
            this.mDisConnectedToConnectedIconAnim.cancel();
        }
        this.mDisConnectedToConnectedIconAnim.setInterpolator(decelerateInterpolator);
        this.mDisConnectedToConnectedIconAnim.start();
    }

    private void updateViewColorList(int[] iArr) {
        Drawable icon = this.mPreference.getIcon();
        if (icon != null && this.mIconAnimEnabled) {
            DrawableCompat.setTint(icon, this.iconColorList.getColorForState(iArr, R.color.miuix_preference_connect_icon_disconnected_color));
        }
        TextView textView = this.mTitleView;
        if (textView != null) {
            if (iArr == STATE_ATTR_CONNECTED) {
                textView.setTextColor(this.titleColorList.getColorForState(iArr, R.color.miuix_preference_connect_title_connected_color));
            } else {
                textView.setTextColor(this.titleColorList.getColorForState(iArr, R.color.miuix_preference_connect_title_disconnected_color));
            }
        }
        TextView textView2 = this.mSummaryView;
        if (textView2 != null) {
            if (iArr == STATE_ATTR_CONNECTED) {
                textView2.setTextColor(this.summaryColorList.getColorForState(iArr, R.color.miuix_preference_connect_summary_connected_color));
            } else {
                textView2.setTextColor(this.summaryColorList.getColorForState(iArr, R.color.miuix_preference_connect_summary_disconnected_color));
            }
        }
    }

    public void initConnectState(int i) {
        this.mLastState = this.mState;
        this.mState = i;
    }

    public void setConnectState(int i) {
        this.mLastState = this.mState;
        this.mState = i;
        updateState(true);
    }

    public int getConnectState() {
        return this.mState;
    }

    private static void setAlphaFolme(View view) {
        if (view == null) {
            return;
        }
        Folme.useAt(view).touch().setAlpha(0.6f, ITouchStyle.TouchType.DOWN).handleTouchOf(view, new AnimConfig[0]);
    }
}
