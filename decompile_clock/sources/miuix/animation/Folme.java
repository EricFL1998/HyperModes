package miuix.animation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.FolmeBlink;
import miuix.animation.controller.FolmeFont;
import miuix.animation.controller.FolmeHover;
import miuix.animation.controller.FolmeTouch;
import miuix.animation.controller.FolmeVisible;
import miuix.animation.controller.ListViewTouchListener;
import miuix.animation.controller.StateComposer;
import miuix.animation.internal.TargetHandler;
import miuix.animation.internal.ThreadPoolUtil;
import miuix.animation.listener.EngineListener;
import miuix.animation.physics.AnimationHandler;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class Folme {
    private static final float DEFAULT_FRICTION = 0.4761905f;
    private static float DEFAULT_THRESHOLD_VELOCITY = 0.0f;
    private static final long DELAY_TIME = 20000;
    private static final long DELAY_TIME_MSG_TARGET_CLEAN_DIE_MUCH = 1000;
    private static final long DELAY_TIME_MSG_TARGET_CLEAN_UI_FREE = 20000;
    public static final int LOG_DESIGN = 16;
    public static final int LOG_DETAIL = 4;
    public static final int LOG_FRAME = 8;
    public static final int LOG_MAIN = 1;
    public static final int LOG_MORE = 2;
    public static final int LOG_OFF = 0;
    private static final int MSG_TARGET = 1;
    private static final int MSG_TARGET_CLEAN_DIE_MUCH = 2;
    private static final int MSG_TARGET_CLEAN_UI_FREE = 1;
    private static final long THRESHOLD_LIMIT = 1024;
    private static final double USE_PHY_MIN_VELOCITY = 1000.0d;
    public static Context appContext;
    private static boolean sEnableSleep;
    private static final ConcurrentHashMap<IAnimTarget, FolmeImpl> sImplMap;
    private static volatile Handler sMainHandler;
    private static volatile Looper sMainUiLooper;
    private static final ConcurrentHashMap<IAnimTarget, FolmeImpl> sSleepImplMap;
    private static AtomicReference<Float> sTimeRatio;
    private static volatile Map<Long, Looper> sUiLooperMap = new ConcurrentHashMap();
    private static volatile Map<Long, Handler> sUiHandlerMap = new ConcurrentHashMap();

    public interface FontType {
        public static final int MITYPE = 1;
        public static final int MITYPE_MONO = 2;
        public static final int MIUI = 0;
    }

    public interface FontWeight {
        public static final int BOLD = 8;
        public static final int DEMI_BOLD = 6;
        public static final int EXTRA_LIGHT = 1;
        public static final int HEAVY = 9;
        public static final int LIGHT = 2;
        public static final int MEDIUM = 5;
        public static final int NORMAL = 3;
        public static final int REGULAR = 4;
        public static final int SEMI_BOLD = 7;
        public static final int THIN = 0;
    }

    private static float getPredict(float f, float f2) {
        return (-f) / (f2 * (-4.2f));
    }

    public static float perFromValue(float f, float f2, float f3) {
        if (f3 == f2) {
            return 0.0f;
        }
        return (f - f2) / (f3 - f2);
    }

    public static float valueFromPer(float f, float f2, float f3) {
        return f2 + ((f3 - f2) * f);
    }

    static {
        ThreadPoolUtil.post(new Runnable() { // from class: miuix.animation.Folme.1
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.getLogEnableInfo();
            }
        });
        sMainUiLooper = Looper.getMainLooper();
        registerUiLooper(sMainUiLooper);
        sTimeRatio = new AtomicReference<>(Float.valueOf(1.0f));
        appContext = null;
        sImplMap = new ConcurrentHashMap<>();
        sEnableSleep = true;
        sSleepImplMap = new ConcurrentHashMap<>();
        DEFAULT_THRESHOLD_VELOCITY = 12.5f;
    }

    public static void useSystemAnimatorDurationScale(Context context) {
        sTimeRatio.set(Float.valueOf(Settings.Global.getFloat(context.getContentResolver(), "animator_duration_scale", 1.0f)));
    }

    public static void setAnimPlayRatio(float f) {
        sTimeRatio.set(Float.valueOf(f));
    }

    public static float getTimeRatio() {
        return sTimeRatio.get().floatValue();
    }

    public static <T> void post(T t, Runnable runnable) {
        IAnimTarget target = getTarget(t, null);
        if (target != null) {
            target.post(runnable);
        }
    }

    public static Collection<IAnimTarget> getTargets() {
        if (LogUtils.isLogMainEnabled()) {
            Iterator<IAnimTarget> it = sImplMap.keySet().iterator();
            int i = 0;
            while (it.hasNext()) {
                if (!it.next().isValid()) {
                    i++;
                }
            }
            LogUtils.debug("current sImplMap total:" + sImplMap.size(), "invalid count:" + i);
            if (LogUtils.isLogMoreEnable()) {
                printAllTargets();
            }
        }
        return sImplMap.keySet();
    }

    public static synchronized void printAllTargets() {
        StringBuilder sb = new StringBuilder();
        ConcurrentHashMap<IAnimTarget, FolmeImpl> concurrentHashMap = sImplMap;
        if (!concurrentHashMap.isEmpty()) {
            sb.append("Folme targets:\n");
            Iterator<IAnimTarget> it = concurrentHashMap.keySet().iterator();
            while (it.hasNext()) {
                sb.append(String.format("|-- %s\n", it.next()));
            }
        } else {
            sb.append("Folme has no target now.");
        }
        Log.d(CommonUtils.TAG, sb.toString());
    }

    public static boolean enableSleep() {
        return sEnableSleep;
    }

    public static IVarFontStyle useVarFontAt(TextView textView, int i, int i2) {
        return new FolmeFont().useAt(textView, i, i2);
    }

    public static void addEngineListener(EngineListener engineListener) {
        FolmeFactory.getEngine().addEngineListener(engineListener);
    }

    public static void removeEngineListener(EngineListener engineListener) {
        FolmeFactory.getEngine().removeEngineListener(engineListener);
    }

    public static class FolmeImpl implements IFolme {
        private IBlinkStyle mBlink;
        private IHoverStyle mHover;
        private IStateStyle mState;
        private IAnimTarget[] mTargets;
        private ITouchStyle mTouch;
        private IVisibleStyle mVisible;

        private FolmeImpl(IAnimTarget... iAnimTargetArr) {
            this.mTargets = iAnimTargetArr;
            Folme.sendToTargetMessage(false);
            Folme.performTargetCleanForTooMuchInvalid();
        }

        @Override // miuix.animation.FolmeStyle
        public void clean() {
            ITouchStyle iTouchStyle = this.mTouch;
            if (iTouchStyle != null) {
                iTouchStyle.clean();
            }
            IVisibleStyle iVisibleStyle = this.mVisible;
            if (iVisibleStyle != null) {
                iVisibleStyle.clean();
            }
            IStateStyle iStateStyle = this.mState;
            if (iStateStyle != null) {
                iStateStyle.clean();
            }
            IHoverStyle iHoverStyle = this.mHover;
            if (iHoverStyle != null) {
                iHoverStyle.clean();
            }
            IBlinkStyle iBlinkStyle = this.mBlink;
            if (iBlinkStyle != null) {
                iBlinkStyle.clean();
            }
            for (IAnimTarget iAnimTarget : this.mTargets) {
                FolmeFactory.clean(iAnimTarget);
            }
        }

        @Override // miuix.animation.FolmeStyle
        public void end() {
            ITouchStyle iTouchStyle = this.mTouch;
            if (iTouchStyle != null) {
                iTouchStyle.end(new Object[0]);
            }
            IVisibleStyle iVisibleStyle = this.mVisible;
            if (iVisibleStyle != null) {
                iVisibleStyle.end(new Object[0]);
            }
            IStateStyle iStateStyle = this.mState;
            if (iStateStyle != null) {
                iStateStyle.end();
            }
            IHoverStyle iHoverStyle = this.mHover;
            if (iHoverStyle != null) {
                iHoverStyle.end(new Object[0]);
            }
            IBlinkStyle iBlinkStyle = this.mBlink;
            if (iBlinkStyle != null) {
                iBlinkStyle.end(new Object[0]);
            }
        }

        @Override // miuix.animation.FolmeStyle
        public IAnimTarget getTarget() {
            return this.mTargets[0];
        }

        @Override // miuix.animation.IFolme
        public IHoverStyle hover() {
            if (this.mHover == null) {
                this.mHover = new FolmeHover(this.mTargets);
            }
            return this.mHover;
        }

        @Override // miuix.animation.IFolme
        public ITouchStyle touch() {
            if (this.mTouch == null) {
                FolmeTouch folmeTouch = new FolmeTouch(this.mTargets);
                folmeTouch.setFontStyle(new FolmeFont());
                this.mTouch = folmeTouch;
            }
            return this.mTouch;
        }

        @Override // miuix.animation.IFolme
        public IVisibleStyle visible() {
            if (this.mVisible == null) {
                this.mVisible = new FolmeVisible(this.mTargets);
            }
            return this.mVisible;
        }

        @Override // miuix.animation.IFolme
        public IStateStyle state() {
            if (this.mState == null) {
                this.mState = StateComposer.composeStyle(this.mTargets);
            }
            return this.mState;
        }

        @Override // miuix.animation.IFolme
        public IBlinkStyle blink() {
            if (this.mBlink == null) {
                this.mBlink = new FolmeBlink(this.mTargets);
            }
            return this.mBlink;
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setTo(Object obj) {
            return state().setTo(obj);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setTo(Object obj, AnimConfig... animConfigArr) {
            return state().setTo(obj, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle resetTo(Object obj) {
            return state().resetTo(obj);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle resetTo(Object obj, AnimConfig... animConfigArr) {
            return state().resetTo(obj, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle to(AnimConfig... animConfigArr) {
            return state().to(animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle to(Object obj, AnimConfig... animConfigArr) {
            return state().to(obj, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle then(Object obj, AnimConfig... animConfigArr) {
            return state().then(obj, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle toWithInit(Object... objArr) {
            return state().toWithInit(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle fromTo(Object obj, Object obj2, AnimConfig... animConfigArr) {
            return state().fromTo(obj, obj2, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setTo(Object... objArr) {
            return state().setTo(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle resetTo(Object... objArr) {
            return state().resetTo(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle to(Object... objArr) {
            return state().to(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public long predictDuration(Object... objArr) {
            return state().predictDuration(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle then(Object... objArr) {
            return state().then(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle autoSetTo(Object... objArr) {
            return state().autoSetTo(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setFlags(long j) {
            return state().setFlags(j);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setup(Object obj) {
            return state().setup(obj);
        }

        @Override // miuix.animation.ICancelableStyle
        public void cancel() {
            state().cancel();
        }

        @Override // miuix.animation.ICancelableStyle
        public void cancel(FloatProperty... floatPropertyArr) {
            state().cancel(floatPropertyArr);
        }

        @Override // miuix.animation.ICancelableStyle
        public void cancel(String... strArr) {
            state().cancel(strArr);
        }

        @Override // miuix.animation.ICancelableStyle
        public void end(Object... objArr) {
            state().end(objArr);
        }
    }

    public static class SimpleFolmeImpl implements IFolme {
        private IBlinkStyle mBlink;
        private IHoverStyle mHover;
        private IStateStyle mState;
        private IAnimTarget mTarget;
        private ITouchStyle mTouch;
        private IVisibleStyle mVisible;

        private SimpleFolmeImpl(IAnimTarget iAnimTarget) {
            this.mTarget = iAnimTarget;
        }

        @Override // miuix.animation.FolmeStyle
        public void clean() {
            ITouchStyle iTouchStyle = this.mTouch;
            if (iTouchStyle != null) {
                iTouchStyle.clean();
            }
            IVisibleStyle iVisibleStyle = this.mVisible;
            if (iVisibleStyle != null) {
                iVisibleStyle.clean();
            }
            IStateStyle iStateStyle = this.mState;
            if (iStateStyle != null) {
                iStateStyle.clean();
            }
            IHoverStyle iHoverStyle = this.mHover;
            if (iHoverStyle != null) {
                iHoverStyle.clean();
            }
            IBlinkStyle iBlinkStyle = this.mBlink;
            if (iBlinkStyle != null) {
                iBlinkStyle.clean();
            }
            IAnimTarget iAnimTarget = this.mTarget;
            if (iAnimTarget != null) {
                iAnimTarget.clean();
            }
        }

        @Override // miuix.animation.FolmeStyle
        public void end() {
            ITouchStyle iTouchStyle = this.mTouch;
            if (iTouchStyle != null) {
                iTouchStyle.end(new Object[0]);
            }
            IVisibleStyle iVisibleStyle = this.mVisible;
            if (iVisibleStyle != null) {
                iVisibleStyle.end(new Object[0]);
            }
            IStateStyle iStateStyle = this.mState;
            if (iStateStyle != null) {
                iStateStyle.end();
            }
            IHoverStyle iHoverStyle = this.mHover;
            if (iHoverStyle != null) {
                iHoverStyle.end(new Object[0]);
            }
            IBlinkStyle iBlinkStyle = this.mBlink;
            if (iBlinkStyle != null) {
                iBlinkStyle.end(new Object[0]);
            }
        }

        @Override // miuix.animation.FolmeStyle
        public IAnimTarget getTarget() {
            return this.mTarget;
        }

        @Override // miuix.animation.IFolme
        public IHoverStyle hover() {
            if (this.mHover == null) {
                this.mHover = new FolmeHover(this.mTarget);
            }
            return this.mHover;
        }

        @Override // miuix.animation.IFolme
        public ITouchStyle touch() {
            if ((this.mTarget instanceof ViewTarget) && this.mTouch == null) {
                FolmeTouch folmeTouch = new FolmeTouch(this.mTarget);
                folmeTouch.setFontStyle(new FolmeFont());
                this.mTouch = folmeTouch;
            }
            return this.mTouch;
        }

        @Override // miuix.animation.IFolme
        public IVisibleStyle visible() {
            if ((this.mTarget instanceof ViewTarget) && this.mVisible == null) {
                this.mVisible = new FolmeVisible(this.mTarget);
            }
            return this.mVisible;
        }

        @Override // miuix.animation.IFolme
        public IStateStyle state() {
            if (this.mState == null) {
                this.mState = StateComposer.composeStyle(this.mTarget);
            }
            return this.mState;
        }

        @Override // miuix.animation.IFolme
        public IBlinkStyle blink() {
            if ((this.mTarget instanceof ViewTarget) && this.mBlink == null) {
                this.mBlink = new FolmeBlink(this.mTarget);
            }
            return this.mBlink;
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setTo(Object obj) {
            return state().setTo(obj);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setTo(Object obj, AnimConfig... animConfigArr) {
            return state().setTo(obj, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle resetTo(Object obj) {
            return state().resetTo(obj);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle resetTo(Object obj, AnimConfig... animConfigArr) {
            return state().resetTo(obj, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle to(AnimConfig... animConfigArr) {
            return state().to(animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle to(Object obj, AnimConfig... animConfigArr) {
            return state().to(obj, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle then(Object obj, AnimConfig... animConfigArr) {
            return state().then(obj, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle toWithInit(Object... objArr) {
            return state().toWithInit(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle fromTo(Object obj, Object obj2, AnimConfig... animConfigArr) {
            return state().fromTo(obj, obj2, animConfigArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setTo(Object... objArr) {
            return state().setTo(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle resetTo(Object... objArr) {
            return state().resetTo(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle to(Object... objArr) {
            return state().to(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public long predictDuration(Object... objArr) {
            return state().predictDuration(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle then(Object... objArr) {
            return state().then(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle autoSetTo(Object... objArr) {
            return state().autoSetTo(objArr);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setFlags(long j) {
            return state().setFlags(j);
        }

        @Override // miuix.animation.FolmeStyle
        public IStateStyle setup(Object obj) {
            return state().setup(obj);
        }

        @Override // miuix.animation.ICancelableStyle
        public void cancel() {
            state().cancel();
        }

        @Override // miuix.animation.ICancelableStyle
        public void cancel(FloatProperty... floatPropertyArr) {
            state().cancel(floatPropertyArr);
        }

        @Override // miuix.animation.ICancelableStyle
        public void cancel(String... strArr) {
            state().cancel(strArr);
        }

        @Override // miuix.animation.ICancelableStyle
        public void end(Object... objArr) {
            state().end(objArr);
        }
    }

    public static class ObjectFolmeImpl extends SimpleFolmeImpl {
        @Override // miuix.animation.Folme.SimpleFolmeImpl, miuix.animation.IFolme
        @Deprecated
        public IBlinkStyle blink() {
            return null;
        }

        @Override // miuix.animation.Folme.SimpleFolmeImpl, miuix.animation.IFolme
        @Deprecated
        public IHoverStyle hover() {
            return null;
        }

        @Override // miuix.animation.Folme.SimpleFolmeImpl, miuix.animation.IFolme
        @Deprecated
        public ITouchStyle touch() {
            return null;
        }

        @Override // miuix.animation.Folme.SimpleFolmeImpl, miuix.animation.IFolme
        @Deprecated
        public IVisibleStyle visible() {
            return null;
        }

        private ObjectFolmeImpl(IAnimTarget iAnimTarget) {
            super(iAnimTarget);
        }
    }

    public static void setLooper(Looper looper) {
        if (sMainUiLooper != null) {
            unregisterUiLooper(sMainUiLooper);
        }
        sMainUiLooper = looper;
        registerUiLooper(sMainUiLooper);
        AnimationHandler.getInstance().recreateProvider();
    }

    public static Looper getLooper() {
        if (sMainUiLooper == null) {
            sMainUiLooper = Looper.getMainLooper();
            registerUiLooper(sMainUiLooper);
        }
        return sMainUiLooper;
    }

    public static synchronized void registerUiLooper(Looper looper) {
        sUiLooperMap.put(Long.valueOf(looper.getThread().getId()), looper);
        Handler handlerCreateUiHandler = createUiHandler(looper);
        sUiHandlerMap.put(Long.valueOf(looper.getThread().getId()), handlerCreateUiHandler);
        if (looper == sMainUiLooper) {
            sMainHandler = handlerCreateUiHandler;
        }
    }

    public static synchronized void unregisterUiLooper(Looper looper) {
        sUiLooperMap.remove(Long.valueOf(looper.getThread().getId()));
        sUiHandlerMap.remove(Long.valueOf(looper.getThread().getId()));
    }

    public static synchronized Looper getUiLooperByTid(long j) {
        return sUiLooperMap.get(Long.valueOf(j));
    }

    public static void setEnableSleep(boolean z) {
        sEnableSleep = z;
    }

    public static IStateStyle useValue(Object... objArr) {
        IFolme iFolmeUseAt;
        if (objArr == null) {
            Log.e(CommonUtils.TAG, "error!! You can't useValue for a null targetObj!!" + Log.getStackTraceString(new Throwable()));
            return null;
        }
        if (objArr.length > 0) {
            if (LogUtils.isLogMoreEnable()) {
                LogUtils.debug("Folme.useValue", "targetObj.length=" + objArr.length, "targetObj[0]=" + objArr[0], LogUtils.getStackTrace(5));
            }
            Object obj = objArr[0];
            if (obj == null) {
                Log.e(CommonUtils.TAG, "error!! targetObj[0] is null, You can't useValue for a null targetObj!! the stack trace:" + Log.getStackTraceString(new Throwable()));
                return null;
            }
            if (objArr.length == 1 && (obj instanceof FolmeObject)) {
                return use((FolmeObject) obj).state();
            }
            iFolmeUseAt = useAt(getTarget(obj, ValueTarget.sCreator));
        } else {
            if (LogUtils.isLogMainEnabled()) {
                LogUtils.debug("Folme.useValue targetObj.length is 0", new Object[0]);
            }
            ValueTarget valueTarget = new ValueTarget();
            valueTarget.setFlags(1L);
            iFolmeUseAt = useAt(valueTarget);
        }
        return iFolmeUseAt.state();
    }

    @Deprecated
    public static IFolme useAt(IAnimTarget iAnimTarget) {
        if (LogUtils.isLogMoreEnable()) {
            LogUtils.debug("Folme.useAt", "target=" + iAnimTarget, "\ntrace:" + LogUtils.getStackTrace(6));
        }
        ConcurrentHashMap<IAnimTarget, FolmeImpl> concurrentHashMap = sImplMap;
        FolmeImpl folmeImpl = concurrentHashMap.get(iAnimTarget);
        if (folmeImpl != null) {
            return folmeImpl;
        }
        FolmeImpl folmeImpl2 = new FolmeImpl(new IAnimTarget[]{iAnimTarget});
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("useAt target: sImplMap.put " + iAnimTarget + LogUtils.getStackTrace(4), new Object[0]);
        }
        FolmeImpl folmeImplPutIfAbsent = concurrentHashMap.putIfAbsent(iAnimTarget, folmeImpl2);
        return folmeImplPutIfAbsent != null ? folmeImplPutIfAbsent : folmeImpl2;
    }

    @Deprecated
    public static IFolme useAt(View... viewArr) {
        if (viewArr.length == 0) {
            throw new IllegalArgumentException("useAt can not be applied to empty views array");
        }
        if (viewArr.length == 1) {
            return use(viewArr[0]);
        }
        int length = viewArr.length;
        IAnimTarget[] iAnimTargetArr = new IAnimTarget[length];
        FolmeImpl folmeImplFillTargetArrayAndGetImpl = fillTargetArrayAndGetImpl(viewArr, iAnimTargetArr);
        if (folmeImplFillTargetArrayAndGetImpl == null) {
            folmeImplFillTargetArrayAndGetImpl = new FolmeImpl(iAnimTargetArr);
            for (int i = 0; i < length; i++) {
                IAnimTarget iAnimTarget = iAnimTargetArr[i];
                if (LogUtils.isLogMainEnabled()) {
                    LogUtils.debug("useAt views: sImplMap.put " + iAnimTarget + LogUtils.getStackTrace(4), new Object[0]);
                }
                FolmeImpl folmeImplPut = sImplMap.put(iAnimTarget, folmeImplFillTargetArrayAndGetImpl);
                if (folmeImplPut != null) {
                    folmeImplPut.clean();
                }
            }
        }
        return folmeImplFillTargetArrayAndGetImpl;
    }

    public static IFolme use(Object obj) {
        if (obj instanceof View) {
            return use((View) obj);
        }
        if (obj instanceof FolmeObject) {
            return use((FolmeObject) obj);
        }
        return useAt(getTarget(obj, ValueTarget.sCreator));
    }

    public static IFolme use(View view) {
        Object tag = view.getTag(miuix.folme.R.id.folme_tag_animator);
        if (!(tag instanceof IFolme)) {
            SimpleFolmeImpl simpleFolmeImpl = new SimpleFolmeImpl(createTarget(view, ViewTarget.sSimpleCreator));
            view.setTag(miuix.folme.R.id.folme_tag_animator, simpleFolmeImpl);
            tag = simpleFolmeImpl;
        }
        return (IFolme) tag;
    }

    public static ObjectFolmeImpl use(FolmeObject folmeObject) {
        ObjectFolmeImpl objectFolmeImplFolme = folmeObject.folme();
        if (objectFolmeImplFolme != null) {
            return objectFolmeImplFolme;
        }
        ObjectFolmeImpl objectFolmeImpl = new ObjectFolmeImpl(createTarget(folmeObject, ValueTarget.sCreator));
        folmeObject.setFolmeImpl(objectFolmeImpl);
        return objectFolmeImpl;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static IFolme useTarget(IAnimTarget iAnimTarget) {
        View targetObject;
        if (iAnimTarget instanceof FolmeObject) {
            return use((FolmeObject) iAnimTarget);
        }
        if (!(iAnimTarget instanceof ViewTarget) || (targetObject = ((ViewTarget) iAnimTarget).getTargetObject()) == null) {
            return null;
        }
        return use(targetObject);
    }

    public static IFolme get(View view) {
        if (view == null) {
            return null;
        }
        Object tag = view.getTag(miuix.folme.R.id.folme_tag_animator);
        if (tag instanceof IFolme) {
            return (IFolme) tag;
        }
        return null;
    }

    public static IFolme get(FolmeObject folmeObject) {
        return folmeObject.folme();
    }

    public static void remove(View view) {
        if (view == null) {
            return;
        }
        Object tag = view.getTag(miuix.folme.R.id.folme_tag_animator);
        if (tag instanceof IFolme) {
            ((IFolme) tag).clean();
        }
        view.setTag(miuix.folme.R.id.folme_tag_animator, null);
    }

    public static void remove(FolmeObject folmeObject) {
        if (folmeObject == null) {
            return;
        }
        ObjectFolmeImpl objectFolmeImplFolme = folmeObject.folme();
        if (objectFolmeImplFolme != null) {
            objectFolmeImplFolme.clean();
        }
        folmeObject.setFolmeImpl(null);
    }

    private static FolmeImpl fillTargetArrayAndGetImpl(View[] viewArr, IAnimTarget[] iAnimTargetArr) {
        boolean z = false;
        FolmeImpl folmeImpl = null;
        for (int i = 0; i < viewArr.length; i++) {
            IAnimTarget target = getTarget(viewArr[i], ViewTarget.sCreator);
            iAnimTargetArr[i] = target;
            FolmeImpl folmeImpl2 = sImplMap.get(target);
            if (folmeImpl == null) {
                folmeImpl = folmeImpl2;
            } else if (folmeImpl != folmeImpl2) {
                z = true;
            }
        }
        if (z) {
            return null;
        }
        return folmeImpl;
    }

    @SafeVarargs
    public static <T> void awake(T... tArr) {
        if (CommonUtils.isArrayEmpty(tArr)) {
            return;
        }
        for (T t : tArr) {
            doAwake(t);
        }
    }

    @SafeVarargs
    public static <T> void sleep(T... tArr) {
        if (CommonUtils.isArrayEmpty(tArr)) {
            return;
        }
        for (T t : tArr) {
            doSleep(t);
        }
    }

    public static void clean(View view) {
        if (view == null) {
            Log.w(CommonUtils.TAG, "Folme.clean(View) view is null！！\ntrace:" + Log.getStackTraceString(new Throwable()));
            return;
        }
        IFolme iFolme = get(view);
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("Folme.clean view=" + view + "\nfolmeImpl=" + iFolme + "\ntrace:" + Log.getStackTraceString(new Throwable()), new Object[0]);
        }
        if (iFolme instanceof SimpleFolmeImpl) {
            remove(view);
        } else {
            doClean(view);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @SafeVarargs
    public static <T> void clean(T... tArr) {
        if (LogUtils.isLogMainEnabled()) {
            LogUtils.debug("Folme.clean targetObjects " + tArr + "\ntrace:" + Log.getStackTraceString(new Throwable()), new Object[0]);
        }
        if (CommonUtils.isArrayEmpty(tArr)) {
            Iterator<IAnimTarget> it = sImplMap.keySet().iterator();
            while (it.hasNext()) {
                cleanAnimTarget(it.next());
            }
            return;
        }
        for (Object[] objArr : tArr) {
            if (objArr instanceof View) {
                clean((View) objArr);
            } else {
                doClean(objArr);
            }
        }
    }

    public static <T> void end(T... tArr) {
        FolmeImpl folmeImpl;
        for (T t : tArr) {
            IAnimTarget target = getTarget(t, null);
            if (target != null && (folmeImpl = sImplMap.get(target)) != null) {
                folmeImpl.end();
            }
        }
    }

    public static void onListViewTouchEvent(AbsListView absListView, MotionEvent motionEvent) {
        ListViewTouchListener listViewTouchListener = FolmeTouch.getListViewTouchListener(absListView);
        if (listViewTouchListener != null) {
            listViewTouchListener.onTouch(absListView, motionEvent);
        }
    }

    private static <T> void doSleep(T t) {
        sleepAnimTarget(getTarget(t, null));
    }

    private static void sleepAnimTarget(final IAnimTarget iAnimTarget) {
        final FolmeImpl folmeImplRemove;
        if (iAnimTarget == null || iAnimTarget.isSleep() || (folmeImplRemove = sImplMap.remove(iAnimTarget)) == null) {
            return;
        }
        iAnimTarget.post(new Runnable() { // from class: miuix.animation.Folme.2
            @Override // java.lang.Runnable
            public void run() {
                iAnimTarget.sleep();
                Folme.sSleepImplMap.put(iAnimTarget, folmeImplRemove);
            }
        });
    }

    private static <T> void doAwake(T t) {
        awakeAnimTarget(getTarget(t, null));
    }

    private static void awakeAnimTarget(final IAnimTarget iAnimTarget) {
        final FolmeImpl folmeImplRemove;
        if (iAnimTarget == null || !iAnimTarget.isSleep() || (folmeImplRemove = sSleepImplMap.remove(iAnimTarget)) == null) {
            return;
        }
        iAnimTarget.post(new Runnable() { // from class: miuix.animation.Folme.3
            @Override // java.lang.Runnable
            public void run() {
                iAnimTarget.awake();
                if (LogUtils.isLogMainEnabled()) {
                    LogUtils.debug("awakeAnimTarget: sImplMap.put " + iAnimTarget + LogUtils.getStackTrace(4), new Object[0]);
                }
                Folme.sImplMap.put(iAnimTarget, folmeImplRemove);
            }
        });
    }

    private static <T> void doClean(T t) {
        cleanAnimTarget(getTarget(t, null));
    }

    private static void cleanAnimTarget(IAnimTarget iAnimTarget) {
        if (iAnimTarget != null) {
            final FolmeImpl folmeImplRemove = sImplMap.remove(iAnimTarget);
            Runnable runnable = new Runnable() { // from class: miuix.animation.Folme.4
                @Override // java.lang.Runnable
                public void run() {
                    FolmeImpl folmeImpl = folmeImplRemove;
                    if (folmeImpl != null) {
                        folmeImpl.clean();
                    }
                }
            };
            TargetHandler handler = iAnimTarget.getHandler();
            if (handler == null || handler.isInTargetThread()) {
                runnable.run();
            } else {
                handler.post(runnable);
            }
        }
    }

    public static <T> ValueTarget getValueTarget(T t) {
        return (ValueTarget) getTarget(t, ValueTarget.sCreator);
    }

    public static ViewTarget getTarget(View view) {
        return (ViewTarget) use(view).getTarget();
    }

    public static IAnimTarget getTarget(FolmeObject folmeObject) {
        return use(folmeObject).getTarget();
    }

    public static <T> IAnimTarget getTarget(T t) {
        return getTarget(t, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> IAnimTarget getTarget(T t, ITargetCreator<T> iTargetCreator) {
        IAnimTarget iAnimTargetCreateTarget;
        IFolme iFolme;
        IFolme iFolme2;
        if (t == 0) {
            return null;
        }
        if ((t instanceof View) && (iFolme2 = get((View) t)) != null) {
            return iFolme2.getTarget();
        }
        if ((t instanceof FolmeObject) && (iFolme = get((FolmeObject) t)) != null) {
            return iFolme.getTarget();
        }
        if (t instanceof IAnimTarget) {
            return (IAnimTarget) t;
        }
        for (IAnimTarget iAnimTarget : sImplMap.keySet()) {
            Object targetObject = iAnimTarget.getTargetObject();
            if (targetObject != null && targetObject.equals(t)) {
                return iAnimTarget;
            }
        }
        if (iTargetCreator == null || (iAnimTargetCreateTarget = iTargetCreator.createTarget(t)) == null) {
            return null;
        }
        useAt(iAnimTargetCreateTarget);
        return iAnimTargetCreateTarget;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <T> IAnimTarget createTarget(T t, ITargetCreator<T> iTargetCreator) {
        if (t == 0) {
            return null;
        }
        if (t instanceof IAnimTarget) {
            return (IAnimTarget) t;
        }
        if (iTargetCreator != null) {
            return iTargetCreator.createTarget(t);
        }
        return null;
    }

    public static void setDraggingState(View view, boolean z) {
        if (z) {
            view.setTag(miuix.folme.R.id.miuix_animation_tag_is_dragging, true);
        } else {
            view.setTag(miuix.folme.R.id.miuix_animation_tag_is_dragging, null);
        }
    }

    public static boolean isInDraggingState(View view) {
        return view.getTag(miuix.folme.R.id.miuix_animation_tag_is_dragging) != null;
    }

    @Deprecated
    public static void getTargets(Collection<IAnimTarget> collection) {
        for (IAnimTarget iAnimTarget : sImplMap.keySet()) {
            if (!iAnimTarget.isValid() || (iAnimTarget.hasFlags(1L) && iAnimTarget.isIdle())) {
                clean(iAnimTarget);
            } else {
                collection.add(iAnimTarget);
            }
        }
    }

    public static IAnimTarget getTargetById(int i) {
        for (IAnimTarget iAnimTarget : sImplMap.keySet()) {
            if (iAnimTarget.id == i) {
                return iAnimTarget;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void clearTargets() {
        for (IAnimTarget iAnimTarget : sImplMap.keySet()) {
            if (iAnimTarget.canClear()) {
                clean(iAnimTarget);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void clearInvalidTargets(List<IAnimTarget> list) {
        for (IAnimTarget iAnimTarget : list) {
            if (iAnimTarget.canClearInvalid()) {
                clean(iAnimTarget);
            }
        }
    }

    public static Handler getMainHandler() {
        return sMainHandler;
    }

    public static Handler getHandler() {
        Handler handler = sUiHandlerMap.get(Long.valueOf(Thread.currentThread().getId()));
        return handler == null ? getMainHandler() : handler;
    }

    private static Handler createUiHandler(Looper looper) {
        return new Handler(looper) { // from class: miuix.animation.Folme.5
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    Folme.clearTargets();
                    Folme.sendToTargetMessage(true);
                } else {
                    if (i != 2) {
                        return;
                    }
                    Folme.clearInvalidTargets((List) message.obj);
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendToTargetMessage(boolean z) {
        clearTargetMessage(1);
        if (z && LogUtils.isLogMainEnabled()) {
            StringBuilder sb = new StringBuilder();
            ConcurrentHashMap<IAnimTarget, FolmeImpl> concurrentHashMap = sImplMap;
            if (!concurrentHashMap.isEmpty()) {
                sb.append(String.format("Folme.sendToTargetMessage fromAuto=%s count=%d\n", Boolean.valueOf(z), Integer.valueOf(concurrentHashMap.size())));
                Iterator<IAnimTarget> it = concurrentHashMap.keySet().iterator();
                while (it.hasNext()) {
                    sb.append(String.format(" |-exist target=%s\n", it.next()));
                }
            } else {
                sb.append("Folme.sendToTargetMessage has no target.");
            }
            LogUtils.debug(sb.toString(), new Object[0]);
        }
        if (sImplMap.size() > 0) {
            Handler mainHandler = getMainHandler();
            if (mainHandler != null) {
                mainHandler.sendEmptyMessageDelayed(1, 20000L);
                return;
            }
            return;
        }
        clearTargetMessage(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void performTargetCleanForTooMuchInvalid() {
        ConcurrentHashMap<IAnimTarget, FolmeImpl> concurrentHashMap = sImplMap;
        if (concurrentHashMap.size() <= 0 || ((long) concurrentHashMap.size()) % THRESHOLD_LIMIT != 0) {
            return;
        }
        ThreadPoolUtil.post(new Runnable() { // from class: miuix.animation.Folme.6
            @Override // java.lang.Runnable
            public void run() {
                ArrayList arrayList = new ArrayList();
                for (IAnimTarget iAnimTarget : Folme.sImplMap.keySet()) {
                    if (!iAnimTarget.isValid()) {
                        arrayList.add(iAnimTarget);
                    }
                }
                Handler mainHandler = Folme.getMainHandler();
                if (mainHandler == null || arrayList.size() <= 0) {
                    return;
                }
                Message messageObtain = Message.obtain();
                messageObtain.obj = arrayList;
                messageObtain.what = 2;
                mainHandler.sendMessageDelayed(messageObtain, 1000L);
            }
        });
    }

    private static void clearTargetMessage(int i) {
        Handler mainHandler = getMainHandler();
        if (mainHandler != null) {
            mainHandler.removeMessages(i);
        }
    }

    public static float afterFrictionValue(float f, float f2) {
        if (f2 == 0.0f) {
            return 0.0f;
        }
        float f3 = f >= 0.0f ? 1.0f : -1.0f;
        float fMin = Math.min(Math.abs(f) / f2, 1.0f);
        float f4 = fMin * fMin;
        return f3 * ((((f4 * fMin) / 3.0f) - f4) + fMin) * f2;
    }

    public static float getDefaultThresholdVelocity() {
        return DEFAULT_THRESHOLD_VELOCITY;
    }

    public static float getPredictDistance(float f) {
        return getPredict(f, DEFAULT_FRICTION);
    }

    public static float getPredictDistanceWithFriction(float f, float f2, float... fArr) {
        if (fArr != null && fArr.length > 0) {
            return getPredict(f, f2, fArr[0]);
        }
        return getPredict(f, f2);
    }

    public static float getPredictDistance(float f, float... fArr) {
        if (fArr != null && fArr.length > 0) {
            return getPredict(f, DEFAULT_FRICTION, fArr[0]);
        }
        return getPredict(f, DEFAULT_FRICTION);
    }

    public static float getPredictFriction(float f, float f2, float f3, float... fArr) {
        float f4 = f2 - f;
        if (f3 * f4 <= 0.0f) {
            return -1.0f;
        }
        float fSignum = Math.signum(f3) * Math.abs(getDefaultThresholdVelocity());
        if (fArr != null && fArr.length > 0) {
            fSignum = Math.signum(f3) * Math.abs(fArr[0]);
        }
        return (f3 - fSignum) / (f4 * 4.2f);
    }

    private static float getPredict(float f, float f2, float f3) {
        return getPredict(f, f2) - getPredict(f3, f2);
    }

    public static void setThreadPriority(int i) {
        try {
            Iterator<Looper> it = sUiLooperMap.values().iterator();
            while (it.hasNext()) {
                Process.setThreadPriority((int) it.next().getThread().getId(), i);
            }
            ThreadPoolUtil.setThreadPriority(i);
            Log.w(CommonUtils.TAG, "setThreadPriority " + i + " success");
        } catch (IllegalArgumentException e) {
            Log.w(CommonUtils.TAG, "setThreadPriority " + i + " failed: " + e);
        } catch (SecurityException e2) {
            Log.w(CommonUtils.TAG, "setThreadPriority " + i + " failed: " + e2);
        }
    }

    public static void setLogLevel(int i) {
        LogUtils.setLogLevel(i);
    }
}
