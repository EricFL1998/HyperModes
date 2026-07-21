package miuix.animation.controller;

import android.widget.TextView;
import miuix.animation.Folme;
import miuix.animation.IAnimTarget;
import miuix.animation.IVarFontStyle;
import miuix.animation.ViewTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.font.FontWeightProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes2.dex */
public class FolmeFont extends FolmeBase implements IVarFontStyle {
    private static final String ALIAS_FONT_INIT = "fontInit";
    private static final String ALIAS_FONT_TARGET = "fontTarget";
    private AnimConfig mDefaultTo;
    private int mInitValue;
    private boolean mIsInitSet;
    private FontWeightProperty mProperty;

    public enum FontType {
        INIT,
        TARGET
    }

    public FolmeFont() {
        super(new IAnimTarget[0]);
        AnimConfig animConfig = new AnimConfig();
        this.mDefaultTo = animConfig;
        animConfig.setEase(EaseManager.getStyle(0, 350.0f, 0.9f, 0.86f));
    }

    @Override // miuix.animation.IVarFontStyle
    public IVarFontStyle useAt(TextView textView, int i, int i2) {
        this.mState = new FolmeState(Folme.getTarget(textView, ViewTarget.sCreator));
        this.mState.addState(new AnimState(FontType.INIT, ALIAS_FONT_INIT));
        this.mState.addState(new AnimState(FontType.TARGET, ALIAS_FONT_TARGET));
        this.mProperty = new FontWeightProperty(textView, i);
        this.mInitValue = i2;
        this.mState.getState(FontType.INIT).add(this.mProperty, i2);
        this.mIsInitSet = false;
        return this;
    }

    @Override // miuix.animation.controller.FolmeBase, miuix.animation.IStateContainer
    public void clean() {
        super.clean();
        this.mState = null;
        this.mProperty = null;
        this.mInitValue = 0;
    }

    @Override // miuix.animation.IVarFontStyle
    public IVarFontStyle to(int i, AnimConfig... animConfigArr) {
        if (this.mState != null) {
            if (!this.mIsInitSet) {
                this.mIsInitSet = true;
                this.mState.setTo(FontType.INIT);
            }
            AnimConfig[] animConfigArr2 = (AnimConfig[]) CommonUtils.mergeArray(animConfigArr, this.mDefaultTo);
            if (this.mInitValue == i) {
                this.mState.to(FontType.INIT, animConfigArr2);
            } else {
                this.mState.getState(FontType.TARGET).add(this.mProperty, i);
                this.mState.to(FontType.TARGET, animConfigArr2);
            }
        }
        return this;
    }

    @Override // miuix.animation.IVarFontStyle
    public IVarFontStyle setTo(int i) {
        if (this.mState != null) {
            this.mState.getState(FontType.TARGET).add(this.mProperty, i);
            this.mState.setTo(FontType.TARGET);
        }
        return this;
    }

    @Override // miuix.animation.IVarFontStyle
    public IVarFontStyle fromTo(int i, int i2, AnimConfig... animConfigArr) {
        if (this.mState != null) {
            this.mState.getState(FontType.INIT).add(this.mProperty, i);
            this.mState.getState(FontType.TARGET).add(this.mProperty, i2);
            this.mState.fromTo(FontType.INIT, FontType.TARGET, animConfigArr);
        }
        return this;
    }
}
