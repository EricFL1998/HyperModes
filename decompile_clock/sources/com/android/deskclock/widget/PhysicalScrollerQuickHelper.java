package com.android.deskclock.widget;

import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes.dex */
public class PhysicalScrollerQuickHelper extends PhysicalScrollerHelper {
    private double mCurrentPosition;
    private double mEndPosition;
    private boolean mIsFinish = true;
    private String mTag;

    @Override // com.android.deskclock.widget.PhysicalScrollerHelper
    public void startAnim(double d, double d2, String str) {
        this.mTag = str;
        this.mIsFinish = false;
        this.mCurrentPosition = d;
        this.mEndPosition = d2;
        Folme.useValue(str).setTo("scrollerQuick", Float.valueOf((float) d)).to("scrollerQuick", Float.valueOf((float) d2), new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.8f)).addListeners(new TransitionListener() { // from class: com.android.deskclock.widget.PhysicalScrollerQuickHelper.1
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
                PhysicalScrollerQuickHelper.this.mCurrentPosition = UpdateInfo.findByName(collection, "scrollerQuick").getFloatValue();
            }
        }));
    }

    @Override // com.android.deskclock.widget.PhysicalScrollerHelper
    public boolean isFinish() {
        return this.mIsFinish;
    }

    @Override // com.android.deskclock.widget.PhysicalScrollerHelper
    public double getPosition() {
        return this.mCurrentPosition;
    }

    @Override // com.android.deskclock.widget.PhysicalScrollerHelper
    public void stopScroll() {
        Folme.useValue(this.mTag).cancel();
        this.mCurrentPosition = this.mEndPosition;
        this.mIsFinish = true;
    }

    @Override // com.android.deskclock.widget.PhysicalScrollerHelper
    public boolean computeScrollOffset() {
        if (isFinish() || Math.abs(this.mCurrentPosition - this.mEndPosition) >= 1.0d) {
            return false;
        }
        Folme.useValue(this.mTag).cancel();
        this.mCurrentPosition = this.mEndPosition;
        this.mIsFinish = true;
        return true;
    }
}
