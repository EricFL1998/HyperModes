package miuix.animation.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import miuix.animation.FolmeFactory;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.ListenerNotifier;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.utils.LogUtils;
import miuix.animation.utils.ObjectPool;

/* JADX INFO: loaded from: classes2.dex */
public class NotifyManager {
    private AnimConfig mConfig = new AnimConfig();
    ListenerNotifier mNotifier;
    ListenerNotifier mSetToNotifier;
    IAnimTarget mTarget;

    public NotifyManager(IAnimTarget iAnimTarget) {
        this.mTarget = iAnimTarget;
        this.mSetToNotifier = new ListenerNotifier(iAnimTarget);
        this.mNotifier = new ListenerNotifier(iAnimTarget);
    }

    public void setToNotify(AnimState animState, AnimConfigLink animConfigLink) {
        boolean zIsLogMainEnabled = LogUtils.isLogMainEnabled();
        if (zIsLogMainEnabled) {
            LogUtils.debug("setTo->setToNotify start", new Object[0]);
        }
        if (animConfigLink == null) {
            if (zIsLogMainEnabled) {
                LogUtils.debug("setTo->setToNotify warning!! configLink is null, return", new Object[0]);
                return;
            }
            return;
        }
        Object tag = animState.getTag();
        this.mConfig.copy(animState.getConfig());
        animConfigLink.addTo(this.mConfig);
        if (!this.mSetToNotifier.addListeners(tag, this.mConfig)) {
            this.mConfig.clear();
            return;
        }
        ObjectPool objPool = FolmeFactory.getEngine().getObjPool();
        Set<TransitionListener> set = (Set) ObjectPool.acquire(objPool, HashSet.class, new Object[0]);
        this.mSetToNotifier.notifyBegin(tag, tag, null, set);
        if (zIsLogMainEnabled) {
            LogUtils.debug("setTo->setToNotify >>> onStart", new Object[0]);
        }
        ArrayList arrayList = new ArrayList();
        for (UpdateInfo updateInfo : this.mTarget.animManager.mUpdateMap.values()) {
            if (animState.contains(updateInfo.property)) {
                arrayList.add(updateInfo);
            }
        }
        this.mSetToNotifier.notifyUpdate(tag, tag, arrayList, set);
        if (zIsLogMainEnabled) {
            LogUtils.debug("setTo->setToNotify >>> onUpdate updates.size=" + arrayList.size(), new Object[0]);
        }
        this.mSetToNotifier.notifyEndAll(tag, tag, set);
        if (zIsLogMainEnabled) {
            LogUtils.debug("setTo->setToNotify >>> onComplete", new Object[0]);
        }
        this.mSetToNotifier.removeListeners(tag);
        ObjectPool.release(objPool, set);
        this.mConfig.clear();
    }

    public ListenerNotifier getNotifier() {
        return this.mNotifier;
    }
}
