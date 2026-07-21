package miuix.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.AppCompatTextView;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.IFolme;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ColorProperty;
import miuix.internal.util.LiteUtils;

/* JADX INFO: loaded from: classes2.dex */
public class TextView extends AppCompatTextView implements AnimatedTextView {
    private static final String TAG = "MiuixTextView";
    private static final ColorProperty TEXT_COLOR_PROPERTY = new ColorProperty<TextView>("textColorInAnim") { // from class: miuix.appcompat.widget.TextView.1
        @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
        public void setIntValue(TextView textView, int i) {
            super.setIntValue(textView, i);
            textView.setCurrentTextColorInAnim(i);
        }

        @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
        public int getIntValue(TextView textView) {
            return textView.getCurrentTextColorInAnim();
        }
    };
    private int mCurrentTextColorInAnim;
    private ColorStateList mCurrentTextColorStateList;
    private IFolme mFolmeAnimator;
    private final Runnable mInitAnimatorTask;
    private AnimConfig mTextColorConfig;

    public TextView(Context context) {
        super(context);
        this.mInitAnimatorTask = new Runnable() { // from class: miuix.appcompat.widget.TextView.2
            @Override // java.lang.Runnable
            public void run() {
                TextView.this.mFolmeAnimator = LiteUtils.isCommonLiteStrategy() ? null : Folme.use((View) TextView.this);
            }
        };
        this.mTextColorConfig = new AnimConfig().setEase(FolmeEase.spring(1.0f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.appcompat.widget.TextView.3
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                super.onComplete(obj);
                TextView.this.restoreTextColor();
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                super.onCancel(obj);
            }
        });
        init();
    }

    public TextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mInitAnimatorTask = new Runnable() { // from class: miuix.appcompat.widget.TextView.2
            @Override // java.lang.Runnable
            public void run() {
                TextView.this.mFolmeAnimator = LiteUtils.isCommonLiteStrategy() ? null : Folme.use((View) TextView.this);
            }
        };
        this.mTextColorConfig = new AnimConfig().setEase(FolmeEase.spring(1.0f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.appcompat.widget.TextView.3
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                super.onComplete(obj);
                TextView.this.restoreTextColor();
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                super.onCancel(obj);
            }
        });
        init();
    }

    public TextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mInitAnimatorTask = new Runnable() { // from class: miuix.appcompat.widget.TextView.2
            @Override // java.lang.Runnable
            public void run() {
                TextView.this.mFolmeAnimator = LiteUtils.isCommonLiteStrategy() ? null : Folme.use((View) TextView.this);
            }
        };
        this.mTextColorConfig = new AnimConfig().setEase(FolmeEase.spring(1.0f, 0.35f)).addListeners(new TransitionListener() { // from class: miuix.appcompat.widget.TextView.3
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                super.onComplete(obj);
                TextView.this.restoreTextColor();
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onCancel(Object obj) {
                super.onCancel(obj);
            }
        });
        init();
    }

    private void init() {
        post(this.mInitAnimatorTask);
    }

    @Override // androidx.appcompat.widget.AppCompatTextView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        IFolme iFolme = this.mFolmeAnimator;
        if (iFolme != null) {
            iFolme.state().cancel();
        }
        removeCallbacks(this.mInitAnimatorTask);
    }

    @Override // androidx.appcompat.widget.AppCompatTextView, android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        if (this.mFolmeAnimator == null) {
            super.drawableStateChanged();
            return;
        }
        int currentTextColor = getCurrentTextColor();
        super.drawableStateChanged();
        int currentTextColor2 = getCurrentTextColor();
        ColorStateList colorStateList = this.mCurrentTextColorStateList;
        if (colorStateList != null) {
            currentTextColor2 = colorStateList.getColorForState(getDrawableState(), this.mCurrentTextColorStateList.getDefaultColor());
        }
        if (currentTextColor != currentTextColor2) {
            this.mCurrentTextColorInAnim = currentTextColor;
            startTextColorTransition(currentTextColor2);
        }
    }

    @Override // android.widget.TextView
    public void setTextColor(ColorStateList colorStateList) {
        IFolme iFolme = this.mFolmeAnimator;
        if (iFolme != null) {
            iFolme.state().cancel();
            restoreTextColor();
        }
        super.setTextColor(colorStateList);
    }

    @Override // miuix.appcompat.widget.AnimatedTextView
    public void restoreTextColor() {
        ColorStateList colorStateList = this.mCurrentTextColorStateList;
        if (colorStateList != null) {
            super.setTextColor(colorStateList);
            this.mCurrentTextColorStateList = null;
        }
    }

    @Override // miuix.appcompat.widget.AnimatedTextView
    public void startTextColorTransition(int i) {
        if (this.mFolmeAnimator == null) {
            return;
        }
        if (this.mCurrentTextColorStateList == null) {
            this.mCurrentTextColorStateList = getTextColors();
        }
        this.mFolmeAnimator.state().to(TEXT_COLOR_PROPERTY, Integer.valueOf(i), this.mTextColorConfig);
    }

    @Override // miuix.appcompat.widget.AnimatedTextView
    public void setCurrentTextColorInAnim(int i) {
        if (this.mCurrentTextColorInAnim != i) {
            this.mCurrentTextColorInAnim = i;
            super.setTextColor(i);
        }
    }

    @Override // miuix.appcompat.widget.AnimatedTextView
    public int getCurrentTextColorInAnim() {
        return this.mCurrentTextColorInAnim;
    }
}
