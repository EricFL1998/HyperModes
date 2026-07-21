package miuix.animation.internal;

import android.os.Trace;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import miuix.animation.IAnimTarget;
import miuix.animation.ValueTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ColorProperty;
import miuix.animation.property.ViewPropertyExt;
import miuix.animation.styles.ForegroundColorStyle;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
class AnimTaskStackRunner {
    static int INIT_RESULT_CODE_FAILED = 1;
    static int INIT_RESULT_CODE_SUCCESS = 0;
    static int INIT_RESULT_CODE_VALUE_INVALID = 2;
    private static final String SECTION_TAG = "Folme.TaskRunner_doFrame";
    static final ThreadLocal<AnimData> animDataLocal = new ThreadLocal<>();
    static final ThreadLocal<List<UpdateInfo>> tempTaskUpdateList = new ThreadLocal<List<UpdateInfo>>() { // from class: miuix.animation.internal.AnimTaskStackRunner.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public List<UpdateInfo> initialValue() {
            return new ArrayList();
        }
    };

    AnimTaskStackRunner() {
    }

    /* JADX WARN: Code duplicated, block: B:108:0x025c  */
    /* JADX WARN: Code duplicated, block: B:114:0x0269  */
    /* JADX WARN: Code duplicated, block: B:117:0x026e A[Catch: Exception -> 0x0287, TRY_ENTER, TRY_LEAVE, TryCatch #14 {Exception -> 0x0287, blocks: (B:109:0x025d, B:117:0x026e), top: B:211:0x025d }] */
    /* JADX WARN: Code duplicated, block: B:124:0x02a7 A[Catch: Exception -> 0x0393, TryCatch #8 {Exception -> 0x0393, blocks: (B:122:0x02a0, B:124:0x02a7, B:126:0x02b3, B:127:0x02b9, B:129:0x02bf, B:131:0x02c5, B:133:0x02cb, B:139:0x030f, B:141:0x0316), top: B:199:0x02a0 }] */
    /* JADX WARN: Code duplicated, block: B:126:0x02b3 A[Catch: Exception -> 0x0393, TryCatch #8 {Exception -> 0x0393, blocks: (B:122:0x02a0, B:124:0x02a7, B:126:0x02b3, B:127:0x02b9, B:129:0x02bf, B:131:0x02c5, B:133:0x02cb, B:139:0x030f, B:141:0x0316), top: B:199:0x02a0 }] */
    /* JADX WARN: Code duplicated, block: B:129:0x02bf A[Catch: Exception -> 0x0393, TryCatch #8 {Exception -> 0x0393, blocks: (B:122:0x02a0, B:124:0x02a7, B:126:0x02b3, B:127:0x02b9, B:129:0x02bf, B:131:0x02c5, B:133:0x02cb, B:139:0x030f, B:141:0x0316), top: B:199:0x02a0 }] */
    /* JADX WARN: Code duplicated, block: B:130:0x02c3  */
    /* JADX WARN: Code duplicated, block: B:133:0x02cb A[Catch: Exception -> 0x0393, TryCatch #8 {Exception -> 0x0393, blocks: (B:122:0x02a0, B:124:0x02a7, B:126:0x02b3, B:127:0x02b9, B:129:0x02bf, B:131:0x02c5, B:133:0x02cb, B:139:0x030f, B:141:0x0316), top: B:199:0x02a0 }] */
    /* JADX WARN: Code duplicated, block: B:138:0x0307  */
    /* JADX WARN: Code duplicated, block: B:141:0x0316 A[Catch: Exception -> 0x0393, TRY_LEAVE, TryCatch #8 {Exception -> 0x0393, blocks: (B:122:0x02a0, B:124:0x02a7, B:126:0x02b3, B:127:0x02b9, B:129:0x02bf, B:131:0x02c5, B:133:0x02cb, B:139:0x030f, B:141:0x0316), top: B:199:0x02a0 }] */
    /* JADX WARN: Code duplicated, block: B:144:0x035b  */
    /* JADX WARN: Code duplicated, block: B:159:0x0380  */
    /* JADX WARN: Code duplicated, block: B:203:0x0256 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:207:0x0245 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:67:0x01e0  */
    /* JADX WARN: Code duplicated, block: B:70:0x01f7 A[Catch: Exception -> 0x039e, TRY_ENTER, TRY_LEAVE, TryCatch #13 {Exception -> 0x039e, blocks: (B:64:0x01db, B:70:0x01f7, B:60:0x01d0), top: B:209:0x01db }] */
    /* JADX WARN: Code duplicated, block: B:73:0x0201 A[Catch: Exception -> 0x0235, TRY_LEAVE, TryCatch #5 {Exception -> 0x0235, blocks: (B:68:0x01e8, B:71:0x01fc, B:73:0x0201), top: B:193:0x01e8 }] */
    /* JADX WARN: Code duplicated, block: B:81:0x0212  */
    /* JADX WARN: Code duplicated, block: B:91:0x0230  */
    /* JADX WARN: Code duplicated, block: B:95:0x023c  */
    static void doAnimationFrame(AnimTask animTask, long j, long j2, int i, double d, boolean z) {
        AnimData animData;
        AnimScheduler animScheduler;
        List<UpdateInfo> list;
        AnimTask animTask2;
        String str;
        AnimTask animTask3;
        String str2;
        AnimTask animTask4;
        String str3;
        UpdateInfo updateInfo;
        int i2;
        int i3;
        String str4;
        IAnimTarget iAnimTarget;
        AnimStats animStats;
        TransitionInfo transitionInfo;
        String str5;
        boolean z2;
        AnimScheduler animScheduler2;
        String str6;
        boolean z3;
        TransitionInfo transitionInfo2;
        String str7;
        TransitionInfo transitionInfo3;
        AnimTask animTask5;
        boolean z4;
        AnimTask animTask6 = animTask;
        AnimScheduler animScheduler3 = animTask6.scheduler;
        if (animScheduler3 != null) {
            animData = (AnimData) CommonUtils.getLocal(animScheduler3.mEngine.getObjPool(), animDataLocal, AnimData.class);
        } else {
            ThreadLocal<AnimData> threadLocal = animDataLocal;
            AnimData animData2 = threadLocal.get();
            if (animData2 == null) {
                animData2 = new AnimData();
                threadLocal.set(animData2);
            }
            animData = animData2;
        }
        animData.logEnabled = LogUtils.isLogDetailEnable() || LogUtils.isLogFrameEnable();
        if (animData.logEnabled) {
            Trace.beginSection("Folme.TaskRunner_doFrame " + Thread.currentThread().getId());
        }
        List<UpdateInfo> list2 = tempTaskUpdateList.get();
        boolean z5 = animData.logEnabled;
        String str8 = CommonUtils.TAG;
        if (z5) {
            LogUtils.logThread(CommonUtils.TAG, "↓---- TaskRunner.run start");
        }
        int i4 = 0;
        while (animTask6 != null) {
            AnimTask animTaskRemove = animTask6.remove();
            int i5 = i4 + 1;
            try {
                AnimStats animStats2 = animTask6.animStats;
                TransitionInfo transitionInfo4 = animTask6.info;
                IAnimTarget iAnimTarget2 = transitionInfo4.target;
                animStats2.prepareOnFrameStart();
                boolean zIsNeedSetup = animStats2.isNeedSetup();
                int animCount = animTask6.getAnimCount();
                list2.clear();
                list2.addAll(transitionInfo4.updateList);
                int i6 = animTask6.startPos;
                int i7 = i6 + animCount;
                int i8 = i6;
                boolean z6 = false;
                while (i8 < i7) {
                    UpdateInfo updateInfo2 = list2.get(i8);
                    if (updateInfo2 == null) {
                        animScheduler = animScheduler3;
                        list = list2;
                        i2 = i8;
                        i3 = i7;
                        transitionInfo3 = transitionInfo4;
                        iAnimTarget = iAnimTarget2;
                        animTask2 = animTaskRemove;
                        animStats = animStats2;
                        str = str8;
                        animTask5 = animTask6;
                    } else {
                        if (animData.logEnabled) {
                            try {
                                list = list2;
                                try {
                                    LogUtils.logThread(str8, "------ data-start: info.id=" + transitionInfo4.id + " startImmediately=" + transitionInfo4.config.startImmediately + String.format(", p='%s'", updateInfo2.property.getName()) + " update.op=" + ((int) updateInfo2.animInfo.op) + ", " + animStats2);
                                } catch (Exception e) {
                                    e = e;
                                    animScheduler = animScheduler3;
                                    animTask2 = animTaskRemove;
                                    str = str8;
                                    animTask3 = animTask2;
                                    LogUtils.logThread(str, "---- AnimTaskStackRunner.doAnimationFrame task:" + animTask3 + " failed: " + Log.getStackTraceString(e));
                                    animTask6 = animTask3;
                                    str8 = str;
                                    i4 = i5;
                                    list2 = list;
                                    animScheduler3 = animScheduler;
                                }
                            } catch (Exception e2) {
                                e = e2;
                                list = list2;
                            }
                        } else {
                            list = list2;
                        }
                        AnimSpecialConfig specialConfig = transitionInfo4.config.getSpecialConfig(updateInfo2.property.getName());
                        animData.from(updateInfo2, transitionInfo4.config, specialConfig);
                        try {
                            if (zIsNeedSetup) {
                                try {
                                    String str9 = str8;
                                    try {
                                        animTask4 = animTask6;
                                        str3 = ", ";
                                        updateInfo = updateInfo2;
                                        i2 = i8;
                                        i3 = i7;
                                        TransitionInfo transitionInfo5 = transitionInfo4;
                                        str4 = " update.op=";
                                        iAnimTarget = iAnimTarget2;
                                        animScheduler = animScheduler3;
                                        animTask2 = animTaskRemove;
                                        animStats = animStats2;
                                        str2 = str9;
                                        try {
                                            setup(animStats2, animData, iAnimTarget2, transitionInfo4.config, specialConfig, j, j2, transitionInfo4.key);
                                            if (animData.logEnabled) {
                                                printSetupLog(animData, iAnimTarget, transitionInfo5, animStats);
                                            }
                                            transitionInfo = transitionInfo5;
                                        } catch (Exception e3) {
                                            e = e3;
                                            str = str2;
                                            animTask3 = animTask2;
                                            LogUtils.logThread(str, "---- AnimTaskStackRunner.doAnimationFrame task:" + animTask3 + " failed: " + Log.getStackTraceString(e));
                                            animTask6 = animTask3;
                                            str8 = str;
                                            i4 = i5;
                                            list2 = list;
                                            animScheduler3 = animScheduler;
                                        }
                                    } catch (Exception e4) {
                                        e = e4;
                                        animScheduler = animScheduler3;
                                        animTask2 = animTaskRemove;
                                        str2 = str9;
                                    }
                                } catch (Exception e5) {
                                    e = e5;
                                    animScheduler = animScheduler3;
                                    animTask2 = animTaskRemove;
                                    str2 = str8;
                                }
                            } else {
                                animTask4 = animTask6;
                                animScheduler = animScheduler3;
                                str3 = ", ";
                                updateInfo = updateInfo2;
                                i2 = i8;
                                i3 = i7;
                                animTask2 = animTaskRemove;
                                animStats = animStats2;
                                str2 = str8;
                                str4 = " update.op=";
                                transitionInfo = transitionInfo4;
                                iAnimTarget = iAnimTarget2;
                                if (animData.op == 6) {
                                    try {
                                        reuse(animStats, animData, iAnimTarget, transitionInfo.config, specialConfig, j, j2);
                                        if (animData.logEnabled) {
                                            str5 = str2;
                                            LogUtils.logThread(str5, "++++++ data.reuse info.id=" + transitionInfo.id + String.format(", p='%s'", updateInfo.property.getName()) + " stats=" + animStats);
                                        }
                                        if (animData.op == 1) {
                                            try {
                                                start(animStats, animData, iAnimTarget, j, j2, transitionInfo);
                                                updateInfo.animInfo.tintMode = animData.tintMode;
                                                if (updateInfo.property == ViewPropertyExt.FOREGROUND) {
                                                    ForegroundColorStyle.start(iAnimTarget, animData.tintMode);
                                                }
                                                if (animData.op == 5) {
                                                    if (!transitionInfo.hasSendNotifyStart || animScheduler == null) {
                                                        animScheduler2 = animScheduler;
                                                    } else {
                                                        animScheduler2 = animScheduler;
                                                        try {
                                                            animScheduler2.executeNotifyTransitionBegin(transitionInfo);
                                                        } catch (Exception e6) {
                                                            e = e6;
                                                            animScheduler = animScheduler2;
                                                            str = str5;
                                                            animTask3 = animTask2;
                                                            LogUtils.logThread(str, "---- AnimTaskStackRunner.doAnimationFrame task:" + animTask3 + " failed: " + Log.getStackTraceString(e));
                                                            animTask6 = animTask3;
                                                            str8 = str;
                                                            i4 = i5;
                                                            list2 = list;
                                                            animScheduler3 = animScheduler;
                                                        }
                                                    }
                                                    try {
                                                        if (animStats.focusCount <= 0 && transitionInfo.config.isFocusPropertyForComplete(updateInfo.property)) {
                                                            z2 = true;
                                                            try {
                                                                animStats.focusCount--;
                                                            } catch (Exception e7) {
                                                                e = e7;
                                                                animScheduler = animScheduler2;
                                                                str = str5;
                                                                animTask3 = animTask2;
                                                                LogUtils.logThread(str, "---- AnimTaskStackRunner.doAnimationFrame task:" + animTask3 + " failed: " + Log.getStackTraceString(e));
                                                                animTask6 = animTask3;
                                                                str8 = str;
                                                                i4 = i5;
                                                                list2 = list;
                                                                animScheduler3 = animScheduler;
                                                            }
                                                        }
                                                    } catch (Exception e8) {
                                                        e = e8;
                                                    }
                                                } else {
                                                    animScheduler2 = animScheduler;
                                                }
                                                z2 = true;
                                            } catch (Exception e9) {
                                                e = e9;
                                            }
                                        } else {
                                            z2 = true;
                                            animScheduler2 = animScheduler;
                                        }
                                        if (animData.op == 2) {
                                            try {
                                                if (!transitionInfo.hasSendNotifyStart && animScheduler2 != null) {
                                                    animScheduler2.executeNotifyTransitionBegin(transitionInfo);
                                                }
                                                if (animData.velocity != 0.0d) {
                                                    try {
                                                        if (transitionInfo.config.startImmediately) {
                                                            z3 = false;
                                                            try {
                                                                animData.justStart = false;
                                                            } catch (Exception e10) {
                                                                e = e10;
                                                                animScheduler = animScheduler2;
                                                                str = str5;
                                                                animTask3 = animTask2;
                                                                LogUtils.logThread(str, "---- AnimTaskStackRunner.doAnimationFrame task:" + animTask3 + " failed: " + Log.getStackTraceString(e));
                                                                animTask6 = animTask3;
                                                                str8 = str;
                                                                i4 = i5;
                                                                list2 = list;
                                                                animScheduler3 = animScheduler;
                                                            }
                                                        } else {
                                                            z3 = false;
                                                        }
                                                    } catch (Exception e11) {
                                                        e = e11;
                                                        z3 = false;
                                                    }
                                                } else {
                                                    z3 = false;
                                                }
                                                try {
                                                    if (animData.logEnabled) {
                                                        LogUtils.logThread(str5, "++++++ data.update start data.justStart=" + animData.justStart);
                                                    }
                                                    animScheduler = animScheduler2;
                                                    transitionInfo2 = transitionInfo;
                                                    str6 = str5;
                                                    str7 = ", p='%s'";
                                                    try {
                                                        update(animStats, animData, iAnimTarget, j, j2, d, i, transitionInfo2);
                                                        if (animData.justEnd) {
                                                            transitionInfo3 = transitionInfo2;
                                                            if (transitionInfo3.config.isFocusPropertyForComplete(updateInfo.property)) {
                                                                animStats.focusEndCount++;
                                                            }
                                                            if (animData.property == ViewPropertyExt.FOREGROUND) {
                                                                ForegroundColorStyle.end(iAnimTarget, updateInfo);
                                                            }
                                                        } else {
                                                            transitionInfo3 = transitionInfo2;
                                                        }
                                                        if (LogUtils.isLogDesignEnable()) {
                                                            Log.i(CommonUtils.D_TAG, String.format("update anim:{name:\"%s\", %s:%.10f, %s }", transitionInfo3.to.getAlias(), updateInfo.property.getName(), Double.valueOf(animData.value), Integer.toHexString((int) animData.value)));
                                                        }
                                                    } catch (Exception e12) {
                                                        e = e12;
                                                        str = str6;
                                                        animTask3 = animTask2;
                                                        LogUtils.logThread(str, "---- AnimTaskStackRunner.doAnimationFrame task:" + animTask3 + " failed: " + Log.getStackTraceString(e));
                                                        animTask6 = animTask3;
                                                        str8 = str;
                                                        i4 = i5;
                                                        list2 = list;
                                                        animScheduler3 = animScheduler;
                                                    }
                                                } catch (Exception e13) {
                                                    e = e13;
                                                    animScheduler = animScheduler2;
                                                    str6 = str5;
                                                }
                                            } catch (Exception e14) {
                                                e = e14;
                                                animScheduler = animScheduler2;
                                                str6 = str5;
                                            }
                                        } else {
                                            animScheduler = animScheduler2;
                                            str6 = str5;
                                            transitionInfo3 = transitionInfo;
                                            str7 = ", p='%s'";
                                        }
                                        animData.to(updateInfo);
                                        if (animData.logEnabled) {
                                            str = str6;
                                            try {
                                                LogUtils.logThread(str, "------ data-end: info.id=" + transitionInfo3.id + String.format(str7, updateInfo.property.getName()) + str4 + ((int) updateInfo.animInfo.op) + str3 + animStats);
                                            } catch (Exception e15) {
                                                e = e15;
                                                animTask3 = animTask2;
                                                LogUtils.logThread(str, "---- AnimTaskStackRunner.doAnimationFrame task:" + animTask3 + " failed: " + Log.getStackTraceString(e));
                                                animTask6 = animTask3;
                                                str8 = str;
                                                i4 = i5;
                                                list2 = list;
                                                animScheduler3 = animScheduler;
                                            }
                                        } else {
                                            str = str6;
                                        }
                                        if (z || !AnimValueUtils.isValid(animData.value)) {
                                            animTask5 = animTask4;
                                        } else {
                                            if (iAnimTarget instanceof ValueTarget) {
                                                animTask5 = animTask4;
                                                if (!animTask5.runInMainThread) {
                                                    z4 = z2;
                                                }
                                                updateInfo.setTargetValue(iAnimTarget, z4);
                                                z6 = z4;
                                            } else {
                                                animTask5 = animTask4;
                                            }
                                            z4 = z6;
                                            updateInfo.setTargetValue(iAnimTarget, z4);
                                            z6 = z4;
                                        }
                                    } catch (Exception e16) {
                                        e = e16;
                                        str5 = str2;
                                        str = str5;
                                        animTask3 = animTask2;
                                        LogUtils.logThread(str, "---- AnimTaskStackRunner.doAnimationFrame task:" + animTask3 + " failed: " + Log.getStackTraceString(e));
                                        animTask6 = animTask3;
                                        str8 = str;
                                        i4 = i5;
                                        list2 = list;
                                        animScheduler3 = animScheduler;
                                    }
                                }
                                animTask6 = animTask3;
                                str8 = str;
                                i4 = i5;
                                list2 = list;
                                animScheduler3 = animScheduler;
                            }
                            if (animData.op == 1) {
                                start(animStats, animData, iAnimTarget, j, j2, transitionInfo);
                                updateInfo.animInfo.tintMode = animData.tintMode;
                                if (updateInfo.property == ViewPropertyExt.FOREGROUND) {
                                    ForegroundColorStyle.start(iAnimTarget, animData.tintMode);
                                }
                                if (animData.op == 5) {
                                    if (transitionInfo.hasSendNotifyStart) {
                                        animScheduler2 = animScheduler;
                                    } else {
                                        animScheduler2 = animScheduler;
                                    }
                                    if (animStats.focusCount <= 0) {
                                    }
                                } else {
                                    animScheduler2 = animScheduler;
                                }
                                z2 = true;
                            } else {
                                z2 = true;
                                animScheduler2 = animScheduler;
                            }
                            if (animData.op == 2) {
                                if (!transitionInfo.hasSendNotifyStart) {
                                    animScheduler2.executeNotifyTransitionBegin(transitionInfo);
                                }
                                if (animData.velocity != 0.0d) {
                                    z3 = false;
                                } else if (transitionInfo.config.startImmediately) {
                                    z3 = false;
                                    animData.justStart = false;
                                } else {
                                    z3 = false;
                                }
                                if (animData.logEnabled) {
                                    LogUtils.logThread(str5, "++++++ data.update start data.justStart=" + animData.justStart);
                                }
                                animScheduler = animScheduler2;
                                transitionInfo2 = transitionInfo;
                                str6 = str5;
                                str7 = ", p='%s'";
                                update(animStats, animData, iAnimTarget, j, j2, d, i, transitionInfo2);
                                if (animData.justEnd) {
                                    transitionInfo3 = transitionInfo2;
                                    if (transitionInfo3.config.isFocusPropertyForComplete(updateInfo.property)) {
                                        animStats.focusEndCount++;
                                    }
                                    if (animData.property == ViewPropertyExt.FOREGROUND) {
                                        ForegroundColorStyle.end(iAnimTarget, updateInfo);
                                    }
                                } else {
                                    transitionInfo3 = transitionInfo2;
                                }
                                if (LogUtils.isLogDesignEnable()) {
                                    Log.i(CommonUtils.D_TAG, String.format("update anim:{name:\"%s\", %s:%.10f, %s }", transitionInfo3.to.getAlias(), updateInfo.property.getName(), Double.valueOf(animData.value), Integer.toHexString((int) animData.value)));
                                }
                            } else {
                                animScheduler = animScheduler2;
                                str6 = str5;
                                transitionInfo3 = transitionInfo;
                                str7 = ", p='%s'";
                            }
                            animData.to(updateInfo);
                            if (animData.logEnabled) {
                                str = str6;
                                LogUtils.logThread(str, "------ data-end: info.id=" + transitionInfo3.id + String.format(str7, updateInfo.property.getName()) + str4 + ((int) updateInfo.animInfo.op) + str3 + animStats);
                            } else {
                                str = str6;
                            }
                            if (z) {
                                animTask5 = animTask4;
                            } else {
                                animTask5 = animTask4;
                            }
                        } catch (Exception e17) {
                            e = e17;
                        }
                        str5 = str2;
                    }
                    i8 = i2 + 1;
                    animStats2 = animStats;
                    animTask6 = animTask5;
                    transitionInfo4 = transitionInfo3;
                    str8 = str;
                    iAnimTarget2 = iAnimTarget;
                    list2 = list;
                    i7 = i3;
                    animTaskRemove = animTask2;
                    animScheduler3 = animScheduler;
                }
                animScheduler = animScheduler3;
                list = list2;
                str = str8;
                animTask3 = animTaskRemove;
            } catch (Exception e18) {
                e = e18;
                animScheduler = animScheduler3;
                list = list2;
            }
            animTask6 = animTask3;
            str8 = str;
            i4 = i5;
            list2 = list;
            animScheduler3 = animScheduler;
        }
        String str10 = str8;
        if (animData.logEnabled) {
            LogUtils.logThread(str10, "↑---- TaskRunner.run finish taskCount=" + i4);
            Trace.endSection();
        }
    }

    static void setup(AnimStats animStats, AnimData animData, IAnimTarget iAnimTarget, AnimConfig animConfig, AnimSpecialConfig animSpecialConfig, long j, long j2, Object obj) {
        double d = animData.startValue;
        if (d == Double.MAX_VALUE || d == 3.4028234663852886E38d || d == 2.147483647E9d) {
            animData.startValue = AnimValueUtils.getValue(iAnimTarget, animData.property, animData.startValue);
        }
        long j3 = j - j2;
        animData.initTime = j3;
        animStats.startedCount++;
        if (animData.op != 2 || animData.delay > 0) {
            animData.setOp((byte) 1);
            float fromSpeed = AnimConfigUtils.getFromSpeed(animConfig, animSpecialConfig);
            if (fromSpeed != Float.MAX_VALUE) {
                animData.velocity = fromSpeed;
            }
            if (animData.logEnabled) {
                LogUtils.logThread(CommonUtils.TAG, "++++++ data.setup path0");
                return;
            }
            return;
        }
        animData.startTime = j3;
        animData.delay = 0L;
        float fromSpeed2 = AnimConfigUtils.getFromSpeed(animConfig, animSpecialConfig);
        if (fromSpeed2 != Float.MAX_VALUE) {
            animData.velocity = fromSpeed2;
        }
        animStats.prepareCount--;
        setStartData(animData);
        if (animData.logEnabled) {
            LogUtils.logThread(CommonUtils.TAG, "++++++ data.setup path1");
            printSetupInUpdateLog(animData, iAnimTarget, obj);
        }
    }

    static void reuse(AnimStats animStats, AnimData animData, IAnimTarget iAnimTarget, AnimConfig animConfig, AnimSpecialConfig animSpecialConfig, long j, long j2) {
        if (AnimValueUtils.isInvalid(animData.startValue)) {
            animData.startValue = AnimValueUtils.getValue(iAnimTarget, animData.property, animData.startValue);
        }
        animData.initTime = j - j2;
        animData.setOp((byte) 1);
        if (animStats.failCount > 0) {
            animStats.failCount--;
        }
        if (animStats.focusEndCount > 0 && animConfig.isFocusPropertyForComplete(animData.property)) {
            animStats.focusEndCount--;
        }
        float fromSpeed = AnimConfigUtils.getFromSpeed(animConfig, animSpecialConfig);
        if (fromSpeed != Float.MAX_VALUE) {
            animData.velocity = fromSpeed;
        }
    }

    static void start(AnimStats animStats, AnimData animData, IAnimTarget iAnimTarget, long j, long j2, TransitionInfo transitionInfo) {
        if (animData.delay > 0) {
            if (animData.logEnabled) {
                printDelayTaskLog(animData, transitionInfo.key, j);
            }
            if (transitionInfo.currentTime < transitionInfo.startTime + (animData.delay * FolmeCore.NANOS_TO_MS)) {
                return;
            }
            double value = AnimValueUtils.getValue(iAnimTarget, animData.property, Double.MAX_VALUE);
            if (value != Double.MAX_VALUE) {
                animData.startValue = value;
            }
            if (animData.logEnabled) {
                LogUtils.logThread(CommonUtils.TAG, "+++++ data.delay-start: time's up", "info.id=" + transitionInfo.id, String.format("p='%s'", animData.property.getName()));
            }
        }
        animStats.prepareCount--;
        int iInitTask = initTask(iAnimTarget, animData, j, j2);
        if (iInitTask != INIT_RESULT_CODE_SUCCESS) {
            finishProperty(animStats, animData);
            if (animData.logEnabled) {
                if (iInitTask == INIT_RESULT_CODE_FAILED) {
                    printSetValueFailedLog(animData, transitionInfo.key);
                    return;
                } else {
                    if (iInitTask == INIT_RESULT_CODE_VALUE_INVALID) {
                        printValueInvalidFailedLog(animData, transitionInfo.key);
                        return;
                    }
                    return;
                }
            }
            return;
        }
        setStartData(animData);
        if (animData.logEnabled) {
            printStartFinishLog(animData, iAnimTarget, transitionInfo.key);
        }
    }

    private static void update(AnimStats animStats, AnimData animData, IAnimTarget iAnimTarget, long j, long j2, double d, int i, TransitionInfo transitionInfo) {
        double d2;
        if (animData.velocity == 0.0d && animData.justStart) {
            d2 = 0.0d;
        } else {
            animData.frameCount++;
            d2 = d;
        }
        animStats.updateCount++;
        animData.frameInterval = d2;
        animData.duration += d2;
        if (animData.property == ViewPropertyExt.FOREGROUND || animData.property == ViewPropertyExt.BACKGROUND || (animData.property instanceof ColorProperty)) {
            FolmeCore.doAnimationFrame(iAnimTarget, true, animData, j, d2, i);
        } else {
            FolmeCore.doAnimationFrame(iAnimTarget, false, animData, j, d2, i);
            if (animData.logEnabled) {
                LogUtils.logThread(CommonUtils.TAG, "------ data.update doAnimationFrame: info.id=" + transitionInfo.id, String.format("p='%s'", animData.property.getName()), "value=" + animData.value, "velocity=" + animData.velocity);
            }
        }
        if (animData.justStart) {
            animData.justStart = false;
        }
        if (animData.op == 3) {
            animData.justEnd = true;
            animStats.endCount++;
        }
        if (animData.logEnabled) {
            printUpdateAnimLog(animData, iAnimTarget, transitionInfo, d2);
        }
    }

    private static void setStartData(AnimData animData) {
        animData.progress = 0.0d;
        animData.reset();
    }

    private static int initTask(IAnimTarget iAnimTarget, AnimData animData, long j, long j2) {
        if ((animData.property instanceof ViewPropertyExt.ForegroundProperty) && !ForegroundColorStyle.isValid(iAnimTarget, animData)) {
            animData.value = animData.targetValue;
            animData.progress = 1.0d;
            return INIT_RESULT_CODE_FAILED;
        }
        if (!setValues(animData)) {
            return INIT_RESULT_CODE_FAILED;
        }
        if (isValueInvalid(animData)) {
            animData.reset();
            animData.value = animData.startValue;
            return INIT_RESULT_CODE_VALUE_INVALID;
        }
        animData.startTime = j - j2;
        animData.frameCount = 0;
        animData.setOp((byte) 2);
        return INIT_RESULT_CODE_SUCCESS;
    }

    private static boolean setValues(AnimData animData) {
        if (AnimValueUtils.isValid(animData.value)) {
            if (AnimValueUtils.isInvalid(animData.startValue)) {
                animData.startValue = animData.value;
            }
            return true;
        }
        if (!AnimValueUtils.isValid(animData.startValue)) {
            return false;
        }
        animData.value = animData.startValue;
        return true;
    }

    private static void finishProperty(AnimStats animStats, AnimData animData) {
        animData.setOp((byte) 5);
        animStats.failCount++;
    }

    private static boolean isValueInvalid(AnimData animData) {
        return animData.startValue == animData.targetValue && Math.abs(animData.velocity) < 16.66666603088379d;
    }

    private static void printDelayTaskLog(AnimData animData, Object obj, long j) {
        LogUtils.logThread(CommonUtils.TAG, "++++++ data.start:check delay", "tag=" + obj + "@" + obj.hashCode(), String.format("p='%s'", animData.property.getName()), "delay=" + animData.delay, "op=" + ((int) animData.op), "initTime=" + animData.initTime, "totalT_ms=" + ((j * 1.0d) / 1000000.0d));
    }

    private static void printSetValueFailedLog(AnimData animData, Object obj) {
        LogUtils.logThread(CommonUtils.TAG, "++++++ data.start:setValueFailed, break", String.format("p='%s'", animData.property.getName()), "tag=" + obj + "@" + obj.hashCode(), "op=" + ((int) animData.op), "value=" + animData.value, "start-v=" + animData.startValue, "target-v= " + animData.targetValue);
    }

    private static void printValueInvalidFailedLog(AnimData animData, Object obj) {
        LogUtils.logThread(CommonUtils.TAG, "++++++ data.start:valueInvalidFailedLog, start-v equal target-v, so break", String.format("p='%s'", animData.property.getName()), "tag=" + obj + "@" + obj.hashCode(), "op=" + ((int) animData.op), "value=" + animData.value, "start-v=" + animData.startValue, "target-v=" + animData.targetValue, "velocity=" + animData.velocity);
    }

    private static void printStartFinishLog(AnimData animData, IAnimTarget iAnimTarget, Object obj) {
        LogUtils.logThread(CommonUtils.TAG, "++++++ data.start:finish", String.format("p='%s'", animData.property.getName()), "tag=" + obj + "@" + obj.hashCode(), "op=" + ((int) animData.op), "value=" + animData.value, "start-v=" + animData.startValue, "target-v=" + animData.targetValue, "progress=" + animData.progress, "ease=" + animData.ease, "delay=" + animData.delay, "velocity=" + animData.velocity, "target=" + iAnimTarget);
    }

    private static void printSetupLog(AnimData animData, IAnimTarget iAnimTarget, TransitionInfo transitionInfo, AnimStats animStats) {
        LogUtils.logThread(CommonUtils.TAG, "++++++ data.setup: info.id=" + transitionInfo.id, String.format("p='%s'", animData.property.getName()), "tag=" + transitionInfo.key + "@" + transitionInfo.key.hashCode(), "value=" + animData.value, "start-v=" + animData.startValue, "target-v=" + animData.targetValue, "progress=" + animData.progress, "animStats=" + animStats, "ease=" + animData.ease, "velocity=" + animData.velocity, "delay=" + animData.delay, "op=" + ((int) animData.op), "target=" + iAnimTarget);
    }

    private static void printSetupInUpdateLog(AnimData animData, IAnimTarget iAnimTarget, Object obj) {
        LogUtils.logThread(CommonUtils.TAG, "++++++ data.setup when op is update and no delay: " + String.format("p='%s'", animData.property.getName()), "tag=" + obj + "@" + obj.hashCode(), "value=" + animData.value, "start-v=" + animData.startValue, "target-v=" + animData.targetValue, "target=" + iAnimTarget, "ease=" + animData.ease, "progress=" + animData.progress, "velocity=" + animData.velocity, "delay=" + animData.delay, "op=" + ((int) animData.op));
    }

    private static void printUpdateAnimLog(AnimData animData, IAnimTarget iAnimTarget, TransitionInfo transitionInfo, double d) {
        LogUtils.logThread(CommonUtils.TAG, "------ data.update: info.id=" + transitionInfo.id, String.format("p='%s'", animData.property.getName()), "tag=" + transitionInfo.key + "@" + transitionInfo.key.hashCode(), "op=" + ((int) animData.op), "frame=" + animData.frameCount, "value=" + animData.value, "start-v=" + animData.startValue, "target-v=" + animData.targetValue, "value_hex=" + Integer.toHexString((int) animData.value), "delta_s=" + d, "interval=" + animData.frameInterval, "progress=" + animData.progress, "target=" + iAnimTarget, "justEnd=" + animData.justEnd, "init-t=" + animData.initTime, "start-t=" + animData.startTime, "velocity=" + animData.velocity);
    }
}
