package kotlin.reflect.jvm.internal;

import com.xiaomi.onetrack.api.as;
import java.lang.reflect.Member;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KProperty1;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;

/* JADX INFO: compiled from: KProperty1Impl.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0010\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0006\b\u0001\u0010\u0002 \u00012\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u00032\b\u0012\u0004\u0012\u0002H\u00020\u0004:\u0001\u001eB+\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\u0004\b\f\u0010\rB\u0019\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\f\u0010\u0010J\u0015\u0010\u0017\u001a\u00028\u00012\u0006\u0010\u0018\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0019J\u0017\u0010\u001c\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0018\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0019J\u0016\u0010\u001d\u001a\u00028\u00012\u0006\u0010\u0018\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\u0019R \u0010\u0011\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u00130\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u00138VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R\u0016\u0010\u001a\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001b0\u0012X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001f"}, d2 = {"Lkotlin/reflect/jvm/internal/KProperty1Impl;", "T", "V", "Lkotlin/reflect/KProperty1;", "Lkotlin/reflect/jvm/internal/KPropertyImpl;", "container", "Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;", as.a, "", "signature", "boundReceiver", "", "<init>", "(Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", "descriptor", "Lkotlin/reflect/jvm/internal/impl/descriptors/PropertyDescriptor;", "(Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;Lorg/jetbrains/kotlin/descriptors/PropertyDescriptor;)V", "_getter", "Lkotlin/Lazy;", "Lkotlin/reflect/jvm/internal/KProperty1Impl$Getter;", "getter", "getGetter", "()Lkotlin/reflect/jvm/internal/KProperty1Impl$Getter;", "get", "receiver", "(Ljava/lang/Object;)Ljava/lang/Object;", "delegateSource", "Ljava/lang/reflect/Member;", "getDelegate", "invoke", "Getter", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public class KProperty1Impl<T, V> extends KPropertyImpl<V> implements KProperty1<T, V> {
    private final Lazy<Getter<T, V>> _getter;
    private final Lazy<Member> delegateSource;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public KProperty1Impl(KDeclarationContainerImpl container, String name, String signature, Object obj) {
        super(container, name, signature, obj);
        Intrinsics.checkNotNullParameter(container, "container");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(signature, "signature");
        this._getter = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.KProperty1Impl$$Lambda$0
            private final KProperty1Impl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KProperty1Impl._getter$lambda$0(this.arg$0);
            }
        });
        this.delegateSource = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.KProperty1Impl$$Lambda$1
            private final KProperty1Impl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KProperty1Impl.delegateSource$lambda$1(this.arg$0);
            }
        });
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public KProperty1Impl(KDeclarationContainerImpl container, PropertyDescriptor descriptor) {
        super(container, descriptor);
        Intrinsics.checkNotNullParameter(container, "container");
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        this._getter = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.KProperty1Impl$$Lambda$0
            private final KProperty1Impl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KProperty1Impl._getter$lambda$0(this.arg$0);
            }
        });
        this.delegateSource = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.KProperty1Impl$$Lambda$1
            private final KProperty1Impl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KProperty1Impl.delegateSource$lambda$1(this.arg$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Getter _getter$lambda$0(KProperty1Impl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return new Getter(this$0);
    }

    @Override // kotlin.reflect.jvm.internal.KPropertyImpl, kotlin.reflect.KProperty
    public Getter<T, V> getGetter() {
        return this._getter.getValue();
    }

    @Override // kotlin.reflect.KProperty1
    public V get(T receiver) {
        return getGetter().call(receiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Member delegateSource$lambda$1(KProperty1Impl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return this$0.computeDelegateSource();
    }

    @Override // kotlin.reflect.KProperty1
    public Object getDelegate(T receiver) {
        return getDelegateImpl(this.delegateSource.getValue(), receiver, null);
    }

    @Override // kotlin.jvm.functions.Function1
    public V invoke(T receiver) {
        return get(receiver);
    }

    /* JADX INFO: compiled from: KProperty1Impl.kt */
    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000*\u0004\b\u0002\u0010\u0001*\u0006\b\u0003\u0010\u0002 \u00012\b\u0012\u0004\u0012\u0002H\u00020\u00032\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u0004B\u001b\u0012\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0016\u0010\u000b\u001a\u00028\u00032\u0006\u0010\f\u001a\u00028\u0002H\u0096\u0002¢\u0006\u0002\u0010\rR \u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u0006X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u000e"}, d2 = {"Lkotlin/reflect/jvm/internal/KProperty1Impl$Getter;", "T", "V", "Lkotlin/reflect/jvm/internal/KPropertyImpl$Getter;", "Lkotlin/reflect/KProperty1$Getter;", "property", "Lkotlin/reflect/jvm/internal/KProperty1Impl;", "<init>", "(Lkotlin/reflect/jvm/internal/KProperty1Impl;)V", "getProperty", "()Lkotlin/reflect/jvm/internal/KProperty1Impl;", "invoke", "receiver", "(Ljava/lang/Object;)Ljava/lang/Object;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class Getter<T, V> extends KPropertyImpl.Getter<V> implements KProperty1.Getter<T, V> {
        private final KProperty1Impl<T, V> property;

        /* JADX WARN: Multi-variable type inference failed */
        public Getter(KProperty1Impl<T, ? extends V> property) {
            Intrinsics.checkNotNullParameter(property, "property");
            this.property = property;
        }

        @Override // kotlin.reflect.jvm.internal.KPropertyImpl.Accessor, kotlin.reflect.KProperty.Accessor
        public KProperty1Impl<T, V> getProperty() {
            return this.property;
        }

        @Override // kotlin.jvm.functions.Function1
        public V invoke(T receiver) {
            return getProperty().get(receiver);
        }
    }
}
