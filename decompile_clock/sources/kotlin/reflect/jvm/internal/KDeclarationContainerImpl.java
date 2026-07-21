package kotlin.reflect.jvm.internal;

import com.android.deskclock.alarm.lifepost.model.NewsTable;
import com.xiaomi.onetrack.api.as;
import com.xiaomi.onetrack.api.g;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.ClassBasedDeclarationContainer;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.internal.calls.ValueClassAwareCallerKt;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptorWithTypeParameters;
import kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectJavaClassFinderKt;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.RuntimeModuleData;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectClassUtilKt;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer;
import kotlin.reflect.jvm.internal.impl.resolve.InlineClassesUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: KDeclarationContainerImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\b \u0018\u0000 B2\u00020\u0001:\u0004?@ABB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t2\u0006\u0010\u000f\u001a\u00020\u0010H&J\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\t2\u0006\u0010\u000f\u001a\u00020\u0010H&J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0014\u001a\u00020\u0015H&J\"\u0010\u0016\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00170\t2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0004J\u0016\u0010\u001c\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001dJ\u0016\u0010\u001f\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001dJE\u0010 \u001a\u0004\u0018\u00010!*\u0006\u0012\u0002\b\u00030\u00052\u0006\u0010\u000f\u001a\u00020\u001d2\u0010\u0010\"\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050#2\n\u0010$\u001a\u0006\u0012\u0002\b\u00030\u00052\u0006\u0010%\u001a\u00020&H\u0002¢\u0006\u0002\u0010'J=\u0010(\u001a\u0004\u0018\u00010!*\u0006\u0012\u0002\b\u00030\u00052\u0006\u0010\u000f\u001a\u00020\u001d2\u0010\u0010\"\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050#2\n\u0010$\u001a\u0006\u0012\u0002\b\u00030\u0005H\u0002¢\u0006\u0002\u0010)J(\u0010*\u001a\b\u0012\u0002\b\u0003\u0018\u00010+*\u0006\u0012\u0002\b\u00030\u00052\u0010\u0010\"\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050,H\u0002J\u0018\u0010-\u001a\u0004\u0018\u00010!2\u0006\u0010\u000f\u001a\u00020\u001d2\u0006\u0010.\u001a\u00020\u001dJ \u0010/\u001a\u0004\u0018\u00010!2\u0006\u0010\u000f\u001a\u00020\u001d2\u0006\u0010.\u001a\u00020\u001d2\u0006\u00100\u001a\u00020&J\u0014\u00101\u001a\b\u0012\u0002\b\u0003\u0018\u00010+2\u0006\u0010.\u001a\u00020\u001dJ\u0014\u00102\u001a\b\u0012\u0002\b\u0003\u0018\u00010+2\u0006\u0010.\u001a\u00020\u001dJ4\u00103\u001a\u0002042\u0010\u00105\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u0005062\u0010\u00107\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050,2\u0006\u00108\u001a\u00020&H\u0002J\u0018\u00109\u001a\u00020:2\u0006\u0010.\u001a\u00020\u001d2\u0006\u0010;\u001a\u00020&H\u0002J$\u0010<\u001a\u0006\u0012\u0002\b\u00030\u00052\u0006\u0010.\u001a\u00020\u001d2\u0006\u0010=\u001a\u00020\u00152\u0006\u0010>\u001a\u00020\u0015H\u0002R\u0018\u0010\u0004\u001a\u0006\u0012\u0002\b\u00030\u00058TX\u0094\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0018\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX¦\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\f¨\u0006C"}, d2 = {"Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;", "Lkotlin/jvm/internal/ClassBasedDeclarationContainer;", "<init>", "()V", "methodOwner", "Ljava/lang/Class;", "getMethodOwner", "()Ljava/lang/Class;", "constructorDescriptors", "", "Lkotlin/reflect/jvm/internal/impl/descriptors/ConstructorDescriptor;", "getConstructorDescriptors", "()Ljava/util/Collection;", "getProperties", "Lkotlin/reflect/jvm/internal/impl/descriptors/PropertyDescriptor;", as.a, "Lkotlin/reflect/jvm/internal/impl/name/Name;", "getFunctions", "Lkotlin/reflect/jvm/internal/impl/descriptors/FunctionDescriptor;", "getLocalProperty", "index", "", "getMembers", "Lkotlin/reflect/jvm/internal/KCallableImpl;", "scope", "Lkotlin/reflect/jvm/internal/impl/resolve/scopes/MemberScope;", "belonginess", "Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl$MemberBelonginess;", "findPropertyDescriptor", "", "signature", "findFunctionDescriptor", "lookupMethod", "Ljava/lang/reflect/Method;", "parameterTypes", "", "returnType", "isStaticDefault", "", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;Ljava/lang/Class;Z)Ljava/lang/reflect/Method;", "tryGetMethod", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;Ljava/lang/Class;)Ljava/lang/reflect/Method;", "tryGetConstructor", "Ljava/lang/reflect/Constructor;", "", "findMethodBySignature", NewsTable.Columns.DESC, "findDefaultMethod", "isMember", "findConstructorBySignature", "findDefaultConstructor", "addParametersAndMasks", "", g.L, "", "valueParameters", "isConstructor", "parseJvmDescriptor", "Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl$FunctionJvmDescriptor;", "parseReturnType", "parseType", "begin", "end", "Data", "MemberBelonginess", "FunctionJvmDescriptor", "Companion", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public abstract class KDeclarationContainerImpl implements ClassBasedDeclarationContainer {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final Class<?> DEFAULT_CONSTRUCTOR_MARKER = Class.forName("kotlin.jvm.internal.DefaultConstructorMarker");
    private static final Regex LOCAL_PROPERTY_SIGNATURE = new Regex("<v#(\\d+)>");

    public abstract Collection<ConstructorDescriptor> getConstructorDescriptors();

    public abstract Collection<FunctionDescriptor> getFunctions(Name name);

    public abstract PropertyDescriptor getLocalProperty(int index);

    public abstract Collection<PropertyDescriptor> getProperties(Name name);

    /* JADX INFO: compiled from: KDeclarationContainerImpl.kt */
    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b¦\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003R\u001b\u0010\u0004\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007¨\u0006\n"}, d2 = {"Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl$Data;", "", "<init>", "(Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;)V", "moduleData", "Lkotlin/reflect/jvm/internal/impl/descriptors/runtime/components/RuntimeModuleData;", "getModuleData", "()Lorg/jetbrains/kotlin/descriptors/runtime/components/RuntimeModuleData;", "moduleData$delegate", "Lkotlin/reflect/jvm/internal/ReflectProperties$LazySoftVal;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public abstract class Data {
        static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(Data.class), "moduleData", "getModuleData()Lorg/jetbrains/kotlin/descriptors/runtime/components/RuntimeModuleData;"))};

        /* JADX INFO: renamed from: moduleData$delegate, reason: from kotlin metadata */
        private final ReflectProperties.LazySoftVal moduleData;

        public Data() {
            this.moduleData = ReflectProperties.lazySoft(new Function0(KDeclarationContainerImpl.this) { // from class: kotlin.reflect.jvm.internal.KDeclarationContainerImpl$Data$$Lambda$0
                private final KDeclarationContainerImpl arg$0;

                {
                    this.arg$0 = kDeclarationContainerImpl;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return KDeclarationContainerImpl.Data.moduleData_delegate$lambda$0(this.arg$0);
                }
            });
        }

        /* JADX WARN: Multi-variable type inference failed */
        public final RuntimeModuleData getModuleData() {
            T value = this.moduleData.getValue(this, $$delegatedProperties[0]);
            Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
            return (RuntimeModuleData) value;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final RuntimeModuleData moduleData_delegate$lambda$0(KDeclarationContainerImpl this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            return ModuleByClassLoaderKt.getOrCreateModule(this$0.getJClass());
        }
    }

    protected Class<?> getMethodOwner() {
        Class<?> wrapperByPrimitive = ReflectClassUtilKt.getWrapperByPrimitive(getJClass());
        return wrapperByPrimitive == null ? getJClass() : wrapperByPrimitive;
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0055  */
    protected final Collection<KCallableImpl<?>> getMembers(MemberScope scope, MemberBelonginess belonginess) {
        KCallableImpl kCallableImpl;
        Intrinsics.checkNotNullParameter(scope, "scope");
        Intrinsics.checkNotNullParameter(belonginess, "belonginess");
        CreateKCallableVisitor createKCallableVisitor = new CreateKCallableVisitor(this) { // from class: kotlin.reflect.jvm.internal.KDeclarationContainerImpl$getMembers$visitor$1
            {
                super(this);
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.DeclarationDescriptorVisitorEmptyBodies, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorVisitor
            public KCallableImpl<?> visitConstructorDescriptor(ConstructorDescriptor descriptor, Unit data) {
                Intrinsics.checkNotNullParameter(descriptor, "descriptor");
                Intrinsics.checkNotNullParameter(data, "data");
                throw new IllegalStateException("No constructors should appear here: " + descriptor);
            }
        };
        Collection<DeclarationDescriptor> contributedDescriptors$default = ResolutionScope.DefaultImpls.getContributedDescriptors$default(scope, null, null, 3, null);
        ArrayList arrayList = new ArrayList();
        for (DeclarationDescriptor declarationDescriptor : contributedDescriptors$default) {
            if (declarationDescriptor instanceof CallableMemberDescriptor) {
                CallableMemberDescriptor callableMemberDescriptor = (CallableMemberDescriptor) declarationDescriptor;
                if (Intrinsics.areEqual(callableMemberDescriptor.getVisibility(), DescriptorVisibilities.INVISIBLE_FAKE) || !belonginess.accept(callableMemberDescriptor)) {
                    kCallableImpl = null;
                } else {
                    kCallableImpl = (KCallableImpl) declarationDescriptor.accept(createKCallableVisitor, Unit.INSTANCE);
                }
            } else {
                kCallableImpl = null;
            }
            if (kCallableImpl != null) {
                arrayList.add(kCallableImpl);
            }
        }
        return CollectionsKt.toList(arrayList);
    }

    /* JADX INFO: compiled from: KDeclarationContainerImpl.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0084\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tj\u0002\b\u0004j\u0002\b\u0005¨\u0006\n"}, d2 = {"Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl$MemberBelonginess;", "", "<init>", "(Ljava/lang/String;I)V", "DECLARED", "INHERITED", "accept", "", "member", "Lkotlin/reflect/jvm/internal/impl/descriptors/CallableMemberDescriptor;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
    protected enum MemberBelonginess {
        DECLARED,
        INHERITED;

        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());

        public final boolean accept(CallableMemberDescriptor member) {
            Intrinsics.checkNotNullParameter(member, "member");
            return member.getKind().isReal() == (this == DECLARED);
        }
    }

    public final PropertyDescriptor findPropertyDescriptor(String name, String signature) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(signature, "signature");
        MatchResult matchResultMatchEntire = LOCAL_PROPERTY_SIGNATURE.matchEntire(signature);
        if (matchResultMatchEntire != null) {
            String str = matchResultMatchEntire.getDestructured().getMatch().getGroupValues().get(1);
            PropertyDescriptor localProperty = getLocalProperty(Integer.parseInt(str));
            if (localProperty != null) {
                return localProperty;
            }
            throw new KotlinReflectionInternalError("Local property #" + str + " not found in " + getJClass());
        }
        Name nameIdentifier = Name.identifier(name);
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        Collection<PropertyDescriptor> properties = getProperties(nameIdentifier);
        ArrayList arrayList = new ArrayList();
        for (Object obj : properties) {
            if (Intrinsics.areEqual(RuntimeTypeMapper.INSTANCE.mapPropertySignature((PropertyDescriptor) obj).getString(), signature)) {
                arrayList.add(obj);
            }
        }
        ArrayList arrayList2 = arrayList;
        if (arrayList2.isEmpty()) {
            throw new KotlinReflectionInternalError("Property '" + name + "' (JVM signature: " + signature + ") not resolved in " + this);
        }
        if (arrayList2.size() == 1) {
            return (PropertyDescriptor) CollectionsKt.single((List) arrayList2);
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Object obj2 : arrayList2) {
            DescriptorVisibility visibility = ((PropertyDescriptor) obj2).getVisibility();
            Object obj3 = linkedHashMap.get(visibility);
            if (obj3 == null) {
                obj3 = (List) new ArrayList();
                linkedHashMap.put(visibility, obj3);
            }
            ((List) obj3).add(obj2);
        }
        final KDeclarationContainerImpl$$Lambda$0 kDeclarationContainerImpl$$Lambda$0 = new Function2() { // from class: kotlin.reflect.jvm.internal.KDeclarationContainerImpl$$Lambda$0
            @Override // kotlin.jvm.functions.Function2
            public Object invoke(Object obj4, Object obj5) {
                return Integer.valueOf(KDeclarationContainerImpl.findPropertyDescriptor$lambda$3((DescriptorVisibility) obj4, (DescriptorVisibility) obj5));
            }
        };
        Collection collectionValues = MapsKt.toSortedMap(linkedHashMap, new Comparator(kDeclarationContainerImpl$$Lambda$0) { // from class: kotlin.reflect.jvm.internal.KDeclarationContainerImpl$$Lambda$1
            private final Function2 arg$0;

            {
                this.arg$0 = kDeclarationContainerImpl$$Lambda$0;
            }

            @Override // java.util.Comparator
            public int compare(Object obj4, Object obj5) {
                return KDeclarationContainerImpl.findPropertyDescriptor$lambda$4(this.arg$0, obj4, obj5);
            }
        }).values();
        Intrinsics.checkNotNullExpressionValue(collectionValues, "<get-values>(...)");
        List list = (List) CollectionsKt.last(collectionValues);
        if (list.size() == 1) {
            Intrinsics.checkNotNull(list);
            return (PropertyDescriptor) CollectionsKt.first(list);
        }
        Name nameIdentifier2 = Name.identifier(name);
        Intrinsics.checkNotNullExpressionValue(nameIdentifier2, "identifier(...)");
        String strJoinToString$default = CollectionsKt.joinToString$default(getProperties(nameIdentifier2), "\n", null, null, 0, null, new Function1() { // from class: kotlin.reflect.jvm.internal.KDeclarationContainerImpl$$Lambda$2
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj4) {
                return KDeclarationContainerImpl.findPropertyDescriptor$lambda$5((PropertyDescriptor) obj4);
            }
        }, 30, null);
        throw new KotlinReflectionInternalError("Property '" + name + "' (JVM signature: " + signature + ") not resolved in " + this + ':' + (strJoinToString$default.length() == 0 ? " no members found" : "\n" + strJoinToString$default));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int findPropertyDescriptor$lambda$4(Function2 tmp0, Object obj, Object obj2) {
        Intrinsics.checkNotNullParameter(tmp0, "$tmp0");
        return ((Number) tmp0.invoke(obj, obj2)).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int findPropertyDescriptor$lambda$3(DescriptorVisibility descriptorVisibility, DescriptorVisibility descriptorVisibility2) {
        Integer numCompare = DescriptorVisibilities.compare(descriptorVisibility, descriptorVisibility2);
        if (numCompare != null) {
            return numCompare.intValue();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence findPropertyDescriptor$lambda$5(PropertyDescriptor descriptor) {
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        return DescriptorRenderer.DEBUG_TEXT.render(descriptor) + " | " + RuntimeTypeMapper.INSTANCE.mapPropertySignature(descriptor).getString();
    }

    /* JADX WARN: Code duplicated, block: B:18:0x00ba  */
    public final FunctionDescriptor findFunctionDescriptor(String name, String signature) {
        List functions;
        ArrayList arrayList;
        String strAsString;
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(signature, "signature");
        if (Intrinsics.areEqual(name, "<init>")) {
            functions = CollectionsKt.toList(getConstructorDescriptors());
            ArrayList arrayList2 = new ArrayList();
            for (Object obj : functions) {
                ConstructorDescriptor constructorDescriptor = (ConstructorDescriptor) obj;
                if (constructorDescriptor.isPrimary()) {
                    ClassifierDescriptorWithTypeParameters containingDeclaration = constructorDescriptor.getContainingDeclaration();
                    Intrinsics.checkNotNullExpressionValue(containingDeclaration, "getContainingDeclaration(...)");
                    if (InlineClassesUtilsKt.isMultiFieldValueClass(containingDeclaration)) {
                        String strAsString2 = RuntimeTypeMapper.INSTANCE.mapSignature(constructorDescriptor).get_signature();
                        if (!StringsKt.startsWith$default(strAsString2, "constructor-impl", false, 2, (Object) null) || !StringsKt.endsWith$default(strAsString2, ")V", false, 2, (Object) null)) {
                            throw new IllegalArgumentException(("Invalid signature of " + constructorDescriptor + ": " + strAsString2).toString());
                        }
                        StringBuilder sbAppend = new StringBuilder().append(StringsKt.removeSuffix(strAsString2, (CharSequence) "V"));
                        ClassifierDescriptorWithTypeParameters containingDeclaration2 = constructorDescriptor.getContainingDeclaration();
                        Intrinsics.checkNotNullExpressionValue(containingDeclaration2, "getContainingDeclaration(...)");
                        strAsString = sbAppend.append(ValueClassAwareCallerKt.toJvmDescriptor(containingDeclaration2)).toString();
                    } else {
                        strAsString = RuntimeTypeMapper.INSTANCE.mapSignature(constructorDescriptor).get_signature();
                    }
                } else {
                    strAsString = RuntimeTypeMapper.INSTANCE.mapSignature(constructorDescriptor).get_signature();
                }
                if (Intrinsics.areEqual(strAsString, signature)) {
                    arrayList2.add(obj);
                }
            }
            arrayList = arrayList2;
        } else {
            Name nameIdentifier = Name.identifier(name);
            Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
            functions = getFunctions(nameIdentifier);
            ArrayList arrayList3 = new ArrayList();
            for (Object obj2 : functions) {
                if (Intrinsics.areEqual(RuntimeTypeMapper.INSTANCE.mapSignature((FunctionDescriptor) obj2).get_signature(), signature)) {
                    arrayList3.add(obj2);
                }
            }
            arrayList = arrayList3;
        }
        if (arrayList.size() != 1) {
            String strJoinToString$default = CollectionsKt.joinToString$default(functions, "\n", null, null, 0, null, new Function1() { // from class: kotlin.reflect.jvm.internal.KDeclarationContainerImpl$$Lambda$3
                @Override // kotlin.jvm.functions.Function1
                public Object invoke(Object obj3) {
                    return KDeclarationContainerImpl.findFunctionDescriptor$lambda$9((FunctionDescriptor) obj3);
                }
            }, 30, null);
            throw new KotlinReflectionInternalError("Function '" + name + "' (JVM signature: " + signature + ") not resolved in " + this + ':' + (strJoinToString$default.length() == 0 ? " no members found" : "\n" + strJoinToString$default));
        }
        return (FunctionDescriptor) CollectionsKt.single((List) arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence findFunctionDescriptor$lambda$9(FunctionDescriptor descriptor) {
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        return DescriptorRenderer.DEBUG_TEXT.render(descriptor) + " | " + RuntimeTypeMapper.INSTANCE.mapSignature(descriptor).get_signature();
    }

    private final Method lookupMethod(Class<?> cls, String str, Class<?>[] clsArr, Class<?> cls2, boolean z) {
        Class<?> clsTryLoadClass;
        Method methodLookupMethod;
        if (z) {
            clsArr[0] = cls;
        }
        Method methodTryGetMethod = tryGetMethod(cls, str, clsArr, cls2);
        if (methodTryGetMethod != null) {
            return methodTryGetMethod;
        }
        Class<? super Object> superclass = cls.getSuperclass();
        if (superclass != null && (methodLookupMethod = lookupMethod(superclass, str, clsArr, cls2, z)) != null) {
            return methodLookupMethod;
        }
        Iterator it = ArrayIteratorKt.iterator(cls.getInterfaces());
        while (it.hasNext()) {
            Class<?> cls3 = (Class) it.next();
            Intrinsics.checkNotNull(cls3);
            Method methodLookupMethod2 = lookupMethod(cls3, str, clsArr, cls2, z);
            if (methodLookupMethod2 != null) {
                return methodLookupMethod2;
            }
            if (z && (clsTryLoadClass = ReflectJavaClassFinderKt.tryLoadClass(ReflectClassUtilKt.getSafeClassLoader(cls3), cls3.getName() + "$DefaultImpls")) != null) {
                clsArr[0] = cls3;
                Method methodTryGetMethod2 = tryGetMethod(clsTryLoadClass, str, clsArr, cls2);
                if (methodTryGetMethod2 != null) {
                    return methodTryGetMethod2;
                }
            }
        }
        return null;
    }

    private final Method tryGetMethod(Class<?> cls, String str, Class<?>[] clsArr, Class<?> cls2) {
        Method method;
        try {
            Method declaredMethod = cls.getDeclaredMethod(str, (Class[]) Arrays.copyOf(clsArr, clsArr.length));
            if (Intrinsics.areEqual(declaredMethod.getReturnType(), cls2)) {
                return declaredMethod;
            }
            Method[] declaredMethods = cls.getDeclaredMethods();
            Intrinsics.checkNotNullExpressionValue(declaredMethods, "getDeclaredMethods(...)");
            Method[] methodArr = declaredMethods;
            int length = methodArr.length;
            for (int i = 0; i < length; i++) {
                method = methodArr[i];
                Method method2 = method;
                if (Intrinsics.areEqual(method2.getName(), str) && Intrinsics.areEqual(method2.getReturnType(), cls2) && Arrays.equals(method2.getParameterTypes(), clsArr)) {
                    return method;
                }
            }
            method = null;
            return method;
        } catch (NoSuchMethodException unused) {
            return null;
        }
    }

    private final Constructor<?> tryGetConstructor(Class<?> cls, List<? extends Class<?>> list) {
        try {
            Class[] clsArr = (Class[]) list.toArray(new Class[0]);
            return cls.getDeclaredConstructor((Class[]) Arrays.copyOf(clsArr, clsArr.length));
        } catch (NoSuchMethodException unused) {
            return null;
        }
    }

    public final Method findMethodBySignature(String name, String desc) {
        Method methodLookupMethod;
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(desc, "desc");
        if (Intrinsics.areEqual(name, "<init>")) {
            return null;
        }
        FunctionJvmDescriptor jvmDescriptor = parseJvmDescriptor(desc, true);
        Class<?>[] clsArr = (Class[]) jvmDescriptor.getParameters().toArray(new Class[0]);
        Class<?> returnType = jvmDescriptor.getReturnType();
        Intrinsics.checkNotNull(returnType);
        Method methodLookupMethod2 = lookupMethod(getMethodOwner(), name, clsArr, returnType, false);
        if (methodLookupMethod2 != null) {
            return methodLookupMethod2;
        }
        if (!getMethodOwner().isInterface() || (methodLookupMethod = lookupMethod(Object.class, name, clsArr, returnType, false)) == null) {
            return null;
        }
        return methodLookupMethod;
    }

    public final Method findDefaultMethod(String name, String desc, boolean isMember) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(desc, "desc");
        if (Intrinsics.areEqual(name, "<init>")) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        if (isMember) {
            arrayList.add(getJClass());
        }
        FunctionJvmDescriptor jvmDescriptor = parseJvmDescriptor(desc, true);
        addParametersAndMasks(arrayList, jvmDescriptor.getParameters(), false);
        Class<?> methodOwner = getMethodOwner();
        String str = name + "$default";
        Class<?>[] clsArr = (Class[]) arrayList.toArray(new Class[0]);
        Class<?> returnType = jvmDescriptor.getReturnType();
        Intrinsics.checkNotNull(returnType);
        return lookupMethod(methodOwner, str, clsArr, returnType, isMember);
    }

    public final Constructor<?> findConstructorBySignature(String desc) {
        Intrinsics.checkNotNullParameter(desc, "desc");
        return tryGetConstructor(getJClass(), parseJvmDescriptor(desc, false).getParameters());
    }

    public final Constructor<?> findDefaultConstructor(String desc) {
        Intrinsics.checkNotNullParameter(desc, "desc");
        Class<?> jClass = getJClass();
        ArrayList arrayList = new ArrayList();
        addParametersAndMasks(arrayList, parseJvmDescriptor(desc, false).getParameters(), true);
        Unit unit = Unit.INSTANCE;
        return tryGetConstructor(jClass, arrayList);
    }

    private final void addParametersAndMasks(List<Class<?>> result, List<? extends Class<?>> valueParameters, boolean isConstructor) {
        if (Intrinsics.areEqual(CollectionsKt.lastOrNull((List) valueParameters), DEFAULT_CONSTRUCTOR_MARKER)) {
            valueParameters = valueParameters.subList(0, valueParameters.size() - 1);
        }
        result.addAll(valueParameters);
        int size = (valueParameters.size() + 31) / 32;
        for (int i = 0; i < size; i++) {
            Class<?> TYPE = Integer.TYPE;
            Intrinsics.checkNotNullExpressionValue(TYPE, "TYPE");
            result.add(TYPE);
        }
        Class<?> cls = isConstructor ? DEFAULT_CONSTRUCTOR_MARKER : Object.class;
        Intrinsics.checkNotNull(cls);
        result.add(cls);
    }

    /* JADX INFO: compiled from: KDeclarationContainerImpl.kt */
    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\b\b\u0002\u0018\u00002\u00020\u0001B'\u0012\u0010\u0010\u0002\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u0004¢\u0006\u0004\b\u0006\u0010\u0007R\u001b\u0010\u0002\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0005\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\f"}, d2 = {"Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl$FunctionJvmDescriptor;", "", "parameters", "", "Ljava/lang/Class;", "returnType", "<init>", "(Ljava/util/List;Ljava/lang/Class;)V", "getParameters", "()Ljava/util/List;", "getReturnType", "()Ljava/lang/Class;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
    private static final class FunctionJvmDescriptor {
        private final List<Class<?>> parameters;
        private final Class<?> returnType;

        /* JADX WARN: Multi-variable type inference failed */
        public FunctionJvmDescriptor(List<? extends Class<?>> parameters, Class<?> cls) {
            Intrinsics.checkNotNullParameter(parameters, "parameters");
            this.parameters = parameters;
            this.returnType = cls;
        }

        public final List<Class<?>> getParameters() {
            return this.parameters;
        }

        public final Class<?> getReturnType() {
            return this.returnType;
        }
    }

    private final FunctionJvmDescriptor parseJvmDescriptor(String desc, boolean parseReturnType) {
        int iIndexOf$default;
        ArrayList arrayList = new ArrayList();
        int i = 1;
        while (true) {
            if (desc.charAt(i) != ')') {
                int i2 = i;
                while (desc.charAt(i2) == '[') {
                    i2++;
                }
                char cCharAt = desc.charAt(i2);
                if (StringsKt.contains$default((CharSequence) "VZCBSIFJD", cCharAt, false, 2, (Object) null)) {
                    iIndexOf$default = i2 + 1;
                } else if (cCharAt == 'L') {
                    iIndexOf$default = StringsKt.indexOf$default((CharSequence) desc, ';', i, false, 4, (Object) null) + 1;
                } else {
                    throw new KotlinReflectionInternalError("Unknown type prefix in the method signature: " + desc);
                }
                arrayList.add(parseType(desc, i, iIndexOf$default));
                i = iIndexOf$default;
            } else {
                return new FunctionJvmDescriptor(arrayList, parseReturnType ? parseType(desc, i + 1, desc.length()) : null);
            }
        }
    }

    private final Class<?> parseType(String desc, int begin, int end) throws ClassNotFoundException {
        char cCharAt = desc.charAt(begin);
        if (cCharAt == 'L') {
            ClassLoader safeClassLoader = ReflectClassUtilKt.getSafeClassLoader(getJClass());
            String strSubstring = desc.substring(begin + 1, end - 1);
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            Class<?> clsLoadClass = safeClassLoader.loadClass(StringsKt.replace$default(strSubstring, '/', '.', false, 4, (Object) null));
            Intrinsics.checkNotNullExpressionValue(clsLoadClass, "loadClass(...)");
            return clsLoadClass;
        }
        if (cCharAt == '[') {
            return UtilKt.createArrayType(parseType(desc, begin + 1, end));
        }
        if (cCharAt == 'V') {
            Class<?> TYPE = Void.TYPE;
            Intrinsics.checkNotNullExpressionValue(TYPE, "TYPE");
            return TYPE;
        }
        if (cCharAt == 'Z') {
            return Boolean.TYPE;
        }
        if (cCharAt == 'C') {
            return Character.TYPE;
        }
        if (cCharAt == 'B') {
            return Byte.TYPE;
        }
        if (cCharAt == 'S') {
            return Short.TYPE;
        }
        if (cCharAt == 'I') {
            return Integer.TYPE;
        }
        if (cCharAt == 'F') {
            return Float.TYPE;
        }
        if (cCharAt == 'J') {
            return Long.TYPE;
        }
        if (cCharAt == 'D') {
            return Double.TYPE;
        }
        throw new KotlinReflectionInternalError("Unknown type prefix in the method signature: " + desc);
    }

    /* JADX INFO: compiled from: KDeclarationContainerImpl.kt */
    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R \u0010\u0004\u001a\u0012\u0012\u0002\b\u0003 \u0006*\b\u0012\u0002\b\u0003\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\f"}, d2 = {"Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl$Companion;", "", "<init>", "()V", "DEFAULT_CONSTRUCTOR_MARKER", "Ljava/lang/Class;", "kotlin.jvm.PlatformType", "Ljava/lang/Class;", "LOCAL_PROPERTY_SIGNATURE", "Lkotlin/text/Regex;", "getLOCAL_PROPERTY_SIGNATURE$kotlin_reflection", "()Lkotlin/text/Regex;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Regex getLOCAL_PROPERTY_SIGNATURE$kotlin_reflection() {
            return KDeclarationContainerImpl.LOCAL_PROPERTY_SIGNATURE;
        }
    }
}
