package miuix.animation.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import miuix.animation.internal.AnimConfigUtils;
import miuix.animation.utils.EaseManager;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AnimConfigLink {
    private static final AtomicInteger sIdGenerator = new AtomicInteger();
    private final int id = sIdGenerator.getAndIncrement();
    private final List<AnimConfig> mConfigList = new ArrayList();
    private final AnimConfig mHeadConfig = new AnimConfig();

    public static AnimConfigLink linkConfig(AnimConfig... animConfigArr) {
        AnimConfigLink animConfigLink = new AnimConfigLink();
        for (AnimConfig animConfig : animConfigArr) {
            animConfigLink.add(animConfig, new boolean[0]);
        }
        return animConfigLink;
    }

    public void add(AnimConfig animConfig, boolean... zArr) {
        if (animConfig == null || this.mConfigList.contains(animConfig)) {
            return;
        }
        if (zArr.length > 0 && zArr[0]) {
            this.mConfigList.add(new AnimConfig(animConfig));
        } else {
            this.mConfigList.add(animConfig);
        }
    }

    public void add(AnimConfigLink animConfigLink, boolean... zArr) {
        if (animConfigLink == null) {
            return;
        }
        Iterator<AnimConfig> it = animConfigLink.mConfigList.iterator();
        while (it.hasNext()) {
            add(it.next(), zArr);
        }
    }

    public int size() {
        return this.mConfigList.size();
    }

    public void remove(AnimConfig animConfig) {
        if (animConfig != null) {
            this.mConfigList.remove(animConfig);
            if (this.mConfigList.isEmpty()) {
                this.mHeadConfig.clear();
                this.mConfigList.add(this.mHeadConfig);
            }
        }
    }

    public void copy(AnimConfigLink animConfigLink) {
        doClear();
        if (animConfigLink != null) {
            this.mConfigList.addAll(animConfigLink.mConfigList);
        }
    }

    public synchronized void addTo(AnimConfig animConfig) {
        EaseManager.EaseStyle easeStyle;
        int size = this.mConfigList.size();
        if (LogUtils.isLogMoreEnable()) {
            LogUtils.debug("AnimConfigLink addTo config listSize=" + this.mConfigList.size(), LogUtils.getStackTrace(10));
        }
        for (int i = size - 1; i >= 0; i--) {
            AnimConfig animConfig2 = this.mConfigList.get(i);
            EaseManager.EaseStyle easeStyle2 = animConfig.ease;
            if (animConfig2 != null) {
                animConfig.delay = Math.max(animConfig.delay, animConfig2.delay);
                easeStyle = animConfig2.ease;
                if (animConfig2.listeners != null && animConfig.listeners != null) {
                    animConfig.listeners.addAll(animConfig2.listeners);
                }
                if (animConfig2.getObserverLooper() != null) {
                    animConfig.setObserverLooper(animConfig2.getObserverLooper());
                }
                animConfig.flags |= animConfig2.flags;
                if (!animConfig2.startImmediately) {
                    animConfig.startImmediately = false;
                }
                animConfig.fromSpeed = AnimConfigUtils.chooseSpeed(animConfig.fromSpeed, animConfig2.fromSpeed);
                animConfig.minDuration = Math.max(animConfig.minDuration, animConfig2.minDuration);
                animConfig.tintMode = Math.max(animConfig.tintMode, animConfig2.tintMode);
                if (LogUtils.isLogMainEnabled()) {
                    LogUtils.debug("AnimConfigLink addTo c.focusCount=" + animConfig2.getFocusPropertyCount() + " c:" + animConfig2, new Object[0]);
                }
                animConfig.addFocusPropertyForComplete(animConfig2);
                animConfig.addSpecialConfigs(animConfig2);
            } else {
                if (LogUtils.isLogMoreEnable()) {
                    LogUtils.debug(String.format("AnimConfigLink addTo config warning!! mConfigList.get(%s) is null!", Integer.valueOf(i)), new Object[0]);
                }
                easeStyle = null;
            }
            if (easeStyle != null && easeStyle != AnimConfig.sDefEase) {
                easeStyle2 = easeStyle;
            }
            animConfig.setEase(easeStyle2);
        }
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("AnimConfigLink addTo finish " + this, new Object[0]);
        }
    }

    public synchronized void clear() {
        if (LogUtils.isLogMoreEnable()) {
            LogUtils.debug("AnimConfigLink clear" + LogUtils.getStackTrace(10), new Object[0]);
        }
        doClear();
        this.mConfigList.add(this.mHeadConfig);
    }

    private void doClear() {
        this.mConfigList.clear();
        this.mHeadConfig.clear();
    }

    public AnimConfig getHead() {
        if (this.mConfigList.isEmpty()) {
            this.mConfigList.add(this.mHeadConfig);
        }
        return this.mConfigList.get(0);
    }

    public String toString() {
        return "AnimConfigLink{id=" + this.id + ", configList=" + Arrays.toString(this.mConfigList.toArray()) + '}';
    }
}
