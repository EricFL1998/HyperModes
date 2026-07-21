package androidx.slidingpanelayout.widget;

import android.app.Activity;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;
import com.android.deskclock.R2;
import com.xiaomi.onetrack.api.as;
import com.xiaomi.onetrack.api.g;
import java.util.Iterator;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.ExecutorsKt;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.FlowKt;

/* JADX INFO: compiled from: FoldingFeatureObserver.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u00002\u00020\u0001:\u0001\u0015B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0012\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u00102\u0006\u0010\t\u001a\u00020\nJ\u0006\u0010\u0014\u001a\u00020\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Landroidx/slidingpanelayout/widget/FoldingFeatureObserver;", "", "windowInfoTracker", "Landroidx/window/layout/WindowInfoTracker;", "executor", "Ljava/util/concurrent/Executor;", "(Landroidx/window/layout/WindowInfoTracker;Ljava/util/concurrent/Executor;)V", as.e, "Lkotlinx/coroutines/Job;", "onFoldingFeatureChangeListener", "Landroidx/slidingpanelayout/widget/FoldingFeatureObserver$OnFoldingFeatureChangeListener;", "getFoldingFeature", "Landroidx/window/layout/FoldingFeature;", "windowLayoutInfo", "Landroidx/window/layout/WindowLayoutInfo;", "registerLayoutStateChangeCallback", "", "activity", "Landroid/app/Activity;", "setOnFoldingFeatureChangeListener", "unregisterLayoutStateChangeCallback", "OnFoldingFeatureChangeListener", "slidingpanelayout_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class FoldingFeatureObserver {
    private final Executor executor;
    private Job job;
    private OnFoldingFeatureChangeListener onFoldingFeatureChangeListener;
    private final WindowInfoTracker windowInfoTracker;

    /* JADX INFO: compiled from: FoldingFeatureObserver.kt */
    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b`\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006"}, d2 = {"Landroidx/slidingpanelayout/widget/FoldingFeatureObserver$OnFoldingFeatureChangeListener;", "", "onFoldingFeatureChange", "", "foldingFeature", "Landroidx/window/layout/FoldingFeature;", "slidingpanelayout_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public interface OnFoldingFeatureChangeListener {
        void onFoldingFeatureChange(FoldingFeature foldingFeature);
    }

    public FoldingFeatureObserver(WindowInfoTracker windowInfoTracker, Executor executor) {
        Intrinsics.checkNotNullParameter(windowInfoTracker, "windowInfoTracker");
        Intrinsics.checkNotNullParameter(executor, "executor");
        this.windowInfoTracker = windowInfoTracker;
        this.executor = executor;
    }

    public final void setOnFoldingFeatureChangeListener(OnFoldingFeatureChangeListener onFoldingFeatureChangeListener) {
        Intrinsics.checkNotNullParameter(onFoldingFeatureChangeListener, "onFoldingFeatureChangeListener");
        this.onFoldingFeatureChangeListener = onFoldingFeatureChangeListener;
    }

    /* JADX INFO: renamed from: androidx.slidingpanelayout.widget.FoldingFeatureObserver$registerLayoutStateChangeCallback$1, reason: invalid class name */
    /* JADX INFO: compiled from: FoldingFeatureObserver.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 6, 0}, xi = 48)
    @DebugMetadata(c = "androidx.slidingpanelayout.widget.FoldingFeatureObserver$registerLayoutStateChangeCallback$1", f = "FoldingFeatureObserver.kt", i = {}, l = {97}, m = "invokeSuspend", n = {}, s = {})
    static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Activity $activity;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Activity activity, Continuation<? super AnonymousClass1> continuation) {
            super(2, continuation);
            this.$activity = activity;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return FoldingFeatureObserver.this.new AnonymousClass1(this.$activity, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) throws Throwable {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                final Flow<WindowLayoutInfo> flowWindowLayoutInfo = FoldingFeatureObserver.this.windowInfoTracker.windowLayoutInfo(this.$activity);
                final FoldingFeatureObserver foldingFeatureObserver = FoldingFeatureObserver.this;
                Flow flowDistinctUntilChanged = FlowKt.distinctUntilChanged(new Flow<FoldingFeature>() { // from class: androidx.slidingpanelayout.widget.FoldingFeatureObserver$registerLayoutStateChangeCallback$1$invokeSuspend$$inlined$mapNotNull$1

                    /* JADX INFO: renamed from: androidx.slidingpanelayout.widget.FoldingFeatureObserver$registerLayoutStateChangeCallback$1$invokeSuspend$$inlined$mapNotNull$1$2, reason: invalid class name */
                    /* JADX INFO: compiled from: Collect.kt */
                    @Metadata(d1 = {"\u0000\u0013\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00028\u00000\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00028\u0000H\u0096@ø\u0001\u0000¢\u0006\u0002\u0010\u0005\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0006¸\u0006\b"}, d2 = {"kotlinx/coroutines/flow/FlowKt__CollectKt$collect$3", "Lkotlinx/coroutines/flow/FlowCollector;", "emit", "", g.p, "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kotlinx-coroutines-core", "kotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$lambda-1$$inlined$collect$1", "kotlinx/coroutines/flow/FlowKt__TransformKt$mapNotNull$$inlined$unsafeTransform$1$2"}, k = 1, mv = {1, 6, 0}, xi = 48)
                    public static final class AnonymousClass2 implements FlowCollector<WindowLayoutInfo> {
                        final /* synthetic */ FlowCollector $this_unsafeFlow$inlined;
                        final /* synthetic */ FoldingFeatureObserver this$0;

                        /* JADX INFO: renamed from: androidx.slidingpanelayout.widget.FoldingFeatureObserver$registerLayoutStateChangeCallback$1$invokeSuspend$$inlined$mapNotNull$1$2$1, reason: invalid class name */
                        @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
                        @DebugMetadata(c = "androidx.slidingpanelayout.widget.FoldingFeatureObserver$registerLayoutStateChangeCallback$1$invokeSuspend$$inlined$mapNotNull$1$2", f = "FoldingFeatureObserver.kt", i = {}, l = {R2.array.chinese_leap_months}, m = "emit", n = {}, s = {})
                        public static final class AnonymousClass1 extends ContinuationImpl {
                            Object L$0;
                            int label;
                            /* synthetic */ Object result;

                            public AnonymousClass1(Continuation continuation) {
                                super(continuation);
                            }

                            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
                            public final Object invokeSuspend(Object obj) {
                                this.result = obj;
                                this.label |= Integer.MIN_VALUE;
                                return AnonymousClass2.this.emit(null, this);
                            }
                        }

                        public AnonymousClass2(FlowCollector flowCollector, FoldingFeatureObserver foldingFeatureObserver) {
                            this.$this_unsafeFlow$inlined = flowCollector;
                            this.this$0 = foldingFeatureObserver;
                        }

                        /* JADX WARN: Code duplicated, block: B:7:0x0014  */
                        @Override // kotlinx.coroutines.flow.FlowCollector
                        public Object emit(WindowLayoutInfo windowLayoutInfo, Continuation continuation) throws Throwable {
                            AnonymousClass1 anonymousClass1;
                            if (continuation instanceof AnonymousClass1) {
                                anonymousClass1 = (AnonymousClass1) continuation;
                                if ((anonymousClass1.label & Integer.MIN_VALUE) != 0) {
                                    anonymousClass1.label -= Integer.MIN_VALUE;
                                } else {
                                    anonymousClass1 = new AnonymousClass1(continuation);
                                }
                            } else {
                                anonymousClass1 = new AnonymousClass1(continuation);
                            }
                            Object obj = anonymousClass1.result;
                            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            int i = anonymousClass1.label;
                            if (i == 0) {
                                ResultKt.throwOnFailure(obj);
                                FlowCollector flowCollector = this.$this_unsafeFlow$inlined;
                                FoldingFeature foldingFeature = this.this$0.getFoldingFeature(windowLayoutInfo);
                                if (foldingFeature != null) {
                                    anonymousClass1.label = 1;
                                    if (flowCollector.emit(foldingFeature, anonymousClass1) == coroutine_suspended) {
                                        return coroutine_suspended;
                                    }
                                }
                            } else {
                                if (i != 1) {
                                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                }
                                ResultKt.throwOnFailure(obj);
                            }
                            return Unit.INSTANCE;
                        }
                    }

                    @Override // kotlinx.coroutines.flow.Flow
                    public Object collect(FlowCollector<? super FoldingFeature> flowCollector, Continuation continuation) {
                        Object objCollect = flowWindowLayoutInfo.collect(new AnonymousClass2(flowCollector, foldingFeatureObserver), continuation);
                        return objCollect == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? objCollect : Unit.INSTANCE;
                    }
                });
                final FoldingFeatureObserver foldingFeatureObserver2 = FoldingFeatureObserver.this;
                this.label = 1;
                if (flowDistinctUntilChanged.collect(new FlowCollector<FoldingFeature>() { // from class: androidx.slidingpanelayout.widget.FoldingFeatureObserver$registerLayoutStateChangeCallback$1$invokeSuspend$$inlined$collect$1
                    @Override // kotlinx.coroutines.flow.FlowCollector
                    public Object emit(FoldingFeature foldingFeature, Continuation<? super Unit> continuation) {
                        Unit unit;
                        FoldingFeature foldingFeature2 = foldingFeature;
                        FoldingFeatureObserver.OnFoldingFeatureChangeListener onFoldingFeatureChangeListener = foldingFeatureObserver2.onFoldingFeatureChangeListener;
                        if (onFoldingFeatureChangeListener == null) {
                            unit = null;
                        } else {
                            onFoldingFeatureChangeListener.onFoldingFeatureChange(foldingFeature2);
                            unit = Unit.INSTANCE;
                        }
                        return unit == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? unit : Unit.INSTANCE;
                    }
                }, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            return Unit.INSTANCE;
        }
    }

    public final void registerLayoutStateChangeCallback(Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Job job = this.job;
        if (job != null) {
            Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
        }
        this.job = BuildersKt__Builders_commonKt.launch$default(CoroutineScopeKt.CoroutineScope(ExecutorsKt.from(this.executor)), null, null, new AnonymousClass1(activity, null), 3, null);
    }

    public final void unregisterLayoutStateChangeCallback() {
        Job job = this.job;
        if (job == null) {
            return;
        }
        Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final FoldingFeature getFoldingFeature(WindowLayoutInfo windowLayoutInfo) {
        Object next;
        Iterator<T> it = windowLayoutInfo.getDisplayFeatures().iterator();
        do {
            if (!it.hasNext()) {
                next = null;
                break;
            }
            next = it.next();
        } while (!(((DisplayFeature) next) instanceof FoldingFeature));
        if (next instanceof FoldingFeature) {
            return (FoldingFeature) next;
        }
        return null;
    }
}
