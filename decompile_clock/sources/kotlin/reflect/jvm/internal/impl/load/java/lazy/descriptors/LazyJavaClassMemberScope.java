package kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KDeclarationContainer;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.Modality;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyGetterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ReceiverParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ClassConstructorDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.EnumEntrySyntheticClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.PropertyGetterDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.PropertySetterDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.SimpleFunctionDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ValueParameterDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupLocation;
import kotlin.reflect.jvm.internal.impl.incremental.components.NoLookupLocation;
import kotlin.reflect.jvm.internal.impl.load.java.BuiltinMethodsWithDifferentJvmName;
import kotlin.reflect.jvm.internal.impl.load.java.BuiltinMethodsWithSpecialGenericSignature;
import kotlin.reflect.jvm.internal.impl.load.java.ClassicBuiltinSpecialProperties;
import kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder;
import kotlin.reflect.jvm.internal.impl.load.java.JavaDescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.load.java.JavaIncompatibilityRulesOverridabilityCondition;
import kotlin.reflect.jvm.internal.impl.load.java.JvmAbi;
import kotlin.reflect.jvm.internal.impl.load.java.JvmAnnotationNames;
import kotlin.reflect.jvm.internal.impl.load.java.PropertiesConventionUtilKt;
import kotlin.reflect.jvm.internal.impl.load.java.SpecialBuiltinMembers;
import kotlin.reflect.jvm.internal.impl.load.java.SpecialGenericSignatures;
import kotlin.reflect.jvm.internal.impl.load.java.UtilsKt;
import kotlin.reflect.jvm.internal.impl.load.java.components.DescriptorResolverUtils;
import kotlin.reflect.jvm.internal.impl.load.java.components.SignaturePropagator;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaClassConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaForKotlinOverridePropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaMethodDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaPropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.UtilKt;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.ContextKt;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaAnnotationsKt;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaResolverContext;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.types.JavaTypeAttributes;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.types.JavaTypeAttributesKt;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaArrayType;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaConstructor;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaField;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaMember;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaMethod;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaRecordComponent;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaType;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaTypeParameter;
import kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.SignatureEnhancement;
import kotlin.reflect.jvm.internal.impl.load.kotlin.MethodSignatureMappingKt;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorFactory;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils;
import kotlin.reflect.jvm.internal.impl.resolve.OverridingUtil;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.DescriptorKindFilter;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ErrorReporter;
import kotlin.reflect.jvm.internal.impl.storage.MemoizedFunctionToNullable;
import kotlin.reflect.jvm.internal.impl.storage.NotNullLazyValue;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.TypeUsage;
import kotlin.reflect.jvm.internal.impl.types.TypeUtils;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeChecker;
import kotlin.reflect.jvm.internal.impl.utils.SmartSet;

/* JADX INFO: compiled from: LazyJavaClassMemberScope.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class LazyJavaClassMemberScope extends LazyJavaScope {
    private final NotNullLazyValue<List<ClassConstructorDescriptor>> constructors;
    private final NotNullLazyValue<Map<Name, JavaField>> enumEntryIndex;
    private final NotNullLazyValue<Set<Name>> generatedNestedClassNames;
    private final JavaClass jClass;
    private final NotNullLazyValue<Set<Name>> nestedClassIndex;
    private final MemoizedFunctionToNullable<Name, ClassDescriptor> nestedClasses;
    private final ClassDescriptor ownerDescriptor;
    private final boolean skipRefinement;

    public /* synthetic */ LazyJavaClassMemberScope(LazyJavaResolverContext lazyJavaResolverContext, ClassDescriptor classDescriptor, JavaClass javaClass, boolean z, LazyJavaClassMemberScope lazyJavaClassMemberScope, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(lazyJavaResolverContext, classDescriptor, javaClass, z, (i & 16) != 0 ? null : lazyJavaClassMemberScope);
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    public /* bridge */ /* synthetic */ Set computeFunctionNames(DescriptorKindFilter descriptorKindFilter, Function1 function1) {
        return computeFunctionNames(descriptorKindFilter, (Function1<? super Name, Boolean>) function1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    public ClassDescriptor getOwnerDescriptor() {
        return this.ownerDescriptor;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public LazyJavaClassMemberScope(final LazyJavaResolverContext c, ClassDescriptor ownerDescriptor, JavaClass jClass, boolean z, LazyJavaClassMemberScope lazyJavaClassMemberScope) {
        super(c, lazyJavaClassMemberScope);
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(ownerDescriptor, "ownerDescriptor");
        Intrinsics.checkNotNullParameter(jClass, "jClass");
        this.ownerDescriptor = ownerDescriptor;
        this.jClass = jClass;
        this.skipRefinement = z;
        this.constructors = c.getStorageManager().createLazyValue(new Function0(this, c) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$0
            private final LazyJavaClassMemberScope arg$0;
            private final LazyJavaResolverContext arg$1;

            {
                this.arg$0 = this;
                this.arg$1 = c;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return LazyJavaClassMemberScope.constructors$lambda$5(this.arg$0, this.arg$1);
            }
        });
        this.nestedClassIndex = c.getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$1
            private final LazyJavaClassMemberScope arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return LazyJavaClassMemberScope.nestedClassIndex$lambda$49(this.arg$0);
            }
        });
        this.generatedNestedClassNames = c.getStorageManager().createLazyValue(new Function0(c, this) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$2
            private final LazyJavaResolverContext arg$0;
            private final LazyJavaClassMemberScope arg$1;

            {
                this.arg$0 = c;
                this.arg$1 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return LazyJavaClassMemberScope.generatedNestedClassNames$lambda$50(this.arg$0, this.arg$1);
            }
        });
        this.enumEntryIndex = c.getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$3
            private final LazyJavaClassMemberScope arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return LazyJavaClassMemberScope.enumEntryIndex$lambda$53(this.arg$0);
            }
        });
        this.nestedClasses = c.getStorageManager().createMemoizedFunctionWithNullableValues(new Function1(this, c) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$4
            private final LazyJavaClassMemberScope arg$0;
            private final LazyJavaResolverContext arg$1;

            {
                this.arg$0 = this;
                this.arg$1 = c;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return LazyJavaClassMemberScope.nestedClasses$lambda$59(this.arg$0, this.arg$1, (Name) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean computeMemberIndex$lambda$0(JavaMember it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return !it.isStatic();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    public ClassDeclaredMemberIndex computeMemberIndex() {
        return new ClassDeclaredMemberIndex(this.jClass, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$5
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return Boolean.valueOf(LazyJavaClassMemberScope.computeMemberIndex$lambda$0((JavaMember) obj));
            }
        });
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    protected LinkedHashSet<Name> computeFunctionNames(DescriptorKindFilter kindFilter, Function1<? super Name, Boolean> function1) {
        Intrinsics.checkNotNullParameter(kindFilter, "kindFilter");
        Collection<KotlinType> collectionMo1492getSupertypes = getOwnerDescriptor().getTypeConstructor().mo1492getSupertypes();
        Intrinsics.checkNotNullExpressionValue(collectionMo1492getSupertypes, "getSupertypes(...)");
        LinkedHashSet<Name> linkedHashSet = new LinkedHashSet<>();
        Iterator<T> it = collectionMo1492getSupertypes.iterator();
        while (it.hasNext()) {
            CollectionsKt.addAll(linkedHashSet, ((KotlinType) it.next()).getMemberScope().getFunctionNames());
        }
        LinkedHashSet<Name> linkedHashSet2 = linkedHashSet;
        linkedHashSet2.addAll(getDeclaredMemberIndex().invoke().getMethodNames());
        linkedHashSet2.addAll(getDeclaredMemberIndex().invoke().getRecordComponentNames());
        linkedHashSet2.addAll(computeClassNames(kindFilter, function1));
        linkedHashSet2.addAll(getC().getComponents().getSyntheticPartsProvider().getMethodNames(getOwnerDescriptor(), getC()));
        return linkedHashSet2;
    }

    public final NotNullLazyValue<List<ClassConstructorDescriptor>> getConstructors$descriptors_jvm() {
        return this.constructors;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List constructors$lambda$5(LazyJavaClassMemberScope this$0, LazyJavaResolverContext c) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(c, "$c");
        Collection<JavaConstructor> constructors = this$0.jClass.getConstructors();
        ArrayList arrayList = new ArrayList(constructors.size());
        Iterator<JavaConstructor> it = constructors.iterator();
        while (it.hasNext()) {
            arrayList.add(this$0.resolveConstructor(it.next()));
        }
        if (this$0.jClass.isRecord()) {
            ClassConstructorDescriptor classConstructorDescriptorCreateDefaultRecordConstructor = this$0.createDefaultRecordConstructor();
            String strComputeJvmDescriptor$default = MethodSignatureMappingKt.computeJvmDescriptor$default(classConstructorDescriptorCreateDefaultRecordConstructor, false, false, 2, null);
            ArrayList arrayList2 = arrayList;
            if ((arrayList2 instanceof Collection) && arrayList2.isEmpty()) {
                arrayList.add(classConstructorDescriptorCreateDefaultRecordConstructor);
                c.getComponents().getJavaResolverCache().recordConstructor(this$0.jClass, classConstructorDescriptorCreateDefaultRecordConstructor);
                break;
            }
            Iterator it2 = arrayList2.iterator();
            do {
                if (!it2.hasNext()) {
                    arrayList.add(classConstructorDescriptorCreateDefaultRecordConstructor);
                    c.getComponents().getJavaResolverCache().recordConstructor(this$0.jClass, classConstructorDescriptorCreateDefaultRecordConstructor);
                    break;
                }
            } while (!Intrinsics.areEqual(MethodSignatureMappingKt.computeJvmDescriptor$default((ClassConstructorDescriptor) it2.next(), false, false, 2, null), strComputeJvmDescriptor$default));
        }
        c.getComponents().getSyntheticPartsProvider().generateConstructors(this$0.getOwnerDescriptor(), arrayList, c);
        SignatureEnhancement signatureEnhancement = c.getComponents().getSignatureEnhancement();
        List listListOfNotNull = arrayList;
        if (listListOfNotNull.isEmpty()) {
            listListOfNotNull = CollectionsKt.listOfNotNull(this$0.createDefaultConstructor());
        }
        return CollectionsKt.toList(signatureEnhancement.enhanceSignatures(c, listListOfNotNull));
    }

    private final ClassConstructorDescriptor createDefaultRecordConstructor() {
        ClassDescriptor ownerDescriptor = getOwnerDescriptor();
        JavaClassConstructorDescriptor javaClassConstructorDescriptorCreateJavaConstructor = JavaClassConstructorDescriptor.createJavaConstructor(ownerDescriptor, Annotations.Companion.getEMPTY(), true, getC().getComponents().getSourceElementFactory().source(this.jClass));
        Intrinsics.checkNotNullExpressionValue(javaClassConstructorDescriptorCreateJavaConstructor, "createJavaConstructor(...)");
        List<ValueParameterDescriptor> listCreateRecordConstructorParameters = createRecordConstructorParameters(javaClassConstructorDescriptorCreateJavaConstructor);
        javaClassConstructorDescriptorCreateJavaConstructor.setHasSynthesizedParameterNames(false);
        javaClassConstructorDescriptorCreateJavaConstructor.initialize(listCreateRecordConstructorParameters, getConstructorVisibility(ownerDescriptor));
        javaClassConstructorDescriptorCreateJavaConstructor.setHasStableParameterNames(false);
        javaClassConstructorDescriptorCreateJavaConstructor.setReturnType(ownerDescriptor.getDefaultType());
        return javaClassConstructorDescriptorCreateJavaConstructor;
    }

    private final List<ValueParameterDescriptor> createRecordConstructorParameters(ClassConstructorDescriptorImpl classConstructorDescriptorImpl) {
        Collection<JavaRecordComponent> recordComponents = this.jClass.getRecordComponents();
        ArrayList arrayList = new ArrayList(recordComponents.size());
        JavaTypeAttributes attributes$default = JavaTypeAttributesKt.toAttributes$default(TypeUsage.COMMON, false, false, null, 6, null);
        Iterator<T> it = recordComponents.iterator();
        int i = 0;
        while (true) {
            int i2 = i;
            if (it.hasNext()) {
                i = i2 + 1;
                JavaRecordComponent javaRecordComponent = (JavaRecordComponent) it.next();
                KotlinType kotlinTypeTransformJavaType = getC().getTypeResolver().transformJavaType(javaRecordComponent.getType(), attributes$default);
                arrayList.add(new ValueParameterDescriptorImpl(classConstructorDescriptorImpl, null, i2, Annotations.Companion.getEMPTY(), javaRecordComponent.getName(), kotlinTypeTransformJavaType, false, false, false, javaRecordComponent.isVararg() ? getC().getComponents().getModule().getBuiltIns().getArrayElementType(kotlinTypeTransformJavaType) : null, getC().getComponents().getSourceElementFactory().source(javaRecordComponent)));
            } else {
                return arrayList;
            }
        }
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    protected boolean isVisibleAsFunction(JavaMethodDescriptor javaMethodDescriptor) {
        Intrinsics.checkNotNullParameter(javaMethodDescriptor, "<this>");
        if (this.jClass.isAnnotationType()) {
            return false;
        }
        return isVisibleAsFunctionInCurrentClass(javaMethodDescriptor);
    }

    private final boolean isVisibleAsFunctionInCurrentClass(final SimpleFunctionDescriptor simpleFunctionDescriptor) {
        Name name = simpleFunctionDescriptor.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        List<Name> propertyNamesCandidatesByAccessorName = PropertiesConventionUtilKt.getPropertyNamesCandidatesByAccessorName(name);
        if (!(propertyNamesCandidatesByAccessorName instanceof Collection) || !propertyNamesCandidatesByAccessorName.isEmpty()) {
            Iterator<T> it = propertyNamesCandidatesByAccessorName.iterator();
            while (it.hasNext()) {
                Set<PropertyDescriptor> propertiesFromSupertypes = getPropertiesFromSupertypes((Name) it.next());
                if (!(propertiesFromSupertypes instanceof Collection) || !propertiesFromSupertypes.isEmpty()) {
                    for (PropertyDescriptor propertyDescriptor : propertiesFromSupertypes) {
                        if (doesClassOverridesProperty(propertyDescriptor, new Function1(simpleFunctionDescriptor, this) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$6
                            private final SimpleFunctionDescriptor arg$0;
                            private final LazyJavaClassMemberScope arg$1;

                            {
                                this.arg$0 = simpleFunctionDescriptor;
                                this.arg$1 = this;
                            }

                            @Override // kotlin.jvm.functions.Function1
                            public Object invoke(Object obj) {
                                return LazyJavaClassMemberScope.isVisibleAsFunctionInCurrentClass$lambda$8$lambda$7$lambda$6(this.arg$0, this.arg$1, (Name) obj);
                            }
                        })) {
                            if (!propertyDescriptor.isVar()) {
                                String strAsString = simpleFunctionDescriptor.getName().asString();
                                Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
                                if (!JvmAbi.isSetterName(strAsString)) {
                                }
                            }
                            return false;
                        }
                    }
                }
            }
        }
        return (doesOverrideRenamedBuiltins(simpleFunctionDescriptor) || shouldBeVisibleAsOverrideOfBuiltInWithErasedValueParameters(simpleFunctionDescriptor) || doesOverrideSuspendFunction(simpleFunctionDescriptor)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Collection isVisibleAsFunctionInCurrentClass$lambda$8$lambda$7$lambda$6(SimpleFunctionDescriptor function, LazyJavaClassMemberScope this$0, Name accessorName) {
        Intrinsics.checkNotNullParameter(function, "$function");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(accessorName, "accessorName");
        if (Intrinsics.areEqual(function.getName(), accessorName)) {
            return CollectionsKt.listOf(function);
        }
        return CollectionsKt.plus((Collection) this$0.searchMethodsByNameWithoutBuiltinMagic(accessorName), (Iterable) this$0.searchMethodsInSupertypesWithoutBuiltinMagic(accessorName));
    }

    private final boolean shouldBeVisibleAsOverrideOfBuiltInWithErasedValueParameters(SimpleFunctionDescriptor simpleFunctionDescriptor) {
        BuiltinMethodsWithSpecialGenericSignature builtinMethodsWithSpecialGenericSignature = BuiltinMethodsWithSpecialGenericSignature.INSTANCE;
        Name name = simpleFunctionDescriptor.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        if (!builtinMethodsWithSpecialGenericSignature.getSameAsBuiltinMethodWithErasedValueParameters(name)) {
            return false;
        }
        Name name2 = simpleFunctionDescriptor.getName();
        Intrinsics.checkNotNullExpressionValue(name2, "getName(...)");
        Set<SimpleFunctionDescriptor> functionsFromSupertypes = getFunctionsFromSupertypes(name2);
        ArrayList arrayList = new ArrayList();
        Iterator<T> it = functionsFromSupertypes.iterator();
        while (it.hasNext()) {
            FunctionDescriptor overriddenBuiltinFunctionWithErasedValueParametersInJava = BuiltinMethodsWithSpecialGenericSignature.getOverriddenBuiltinFunctionWithErasedValueParametersInJava((SimpleFunctionDescriptor) it.next());
            if (overriddenBuiltinFunctionWithErasedValueParametersInJava != null) {
                arrayList.add(overriddenBuiltinFunctionWithErasedValueParametersInJava);
            }
        }
        ArrayList arrayList2 = arrayList;
        if ((arrayList2 instanceof Collection) && arrayList2.isEmpty()) {
            return false;
        }
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            if (hasSameJvmDescriptorButDoesNotOverride(simpleFunctionDescriptor, (FunctionDescriptor) it2.next())) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Collection<SimpleFunctionDescriptor> searchMethodsByNameWithoutBuiltinMagic(Name name) {
        Collection<JavaMethod> collectionFindMethodsByName = getDeclaredMemberIndex().invoke().findMethodsByName(name);
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(collectionFindMethodsByName, 10));
        Iterator<T> it = collectionFindMethodsByName.iterator();
        while (it.hasNext()) {
            arrayList.add(resolveMethodToFunctionDescriptor((JavaMethod) it.next()));
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Collection<SimpleFunctionDescriptor> searchMethodsInSupertypesWithoutBuiltinMagic(Name name) {
        Set<SimpleFunctionDescriptor> functionsFromSupertypes = getFunctionsFromSupertypes(name);
        ArrayList arrayList = new ArrayList();
        for (Object obj : functionsFromSupertypes) {
            SimpleFunctionDescriptor simpleFunctionDescriptor = (SimpleFunctionDescriptor) obj;
            if (!SpecialBuiltinMembers.doesOverrideBuiltinWithDifferentJvmName(simpleFunctionDescriptor) && BuiltinMethodsWithSpecialGenericSignature.getOverriddenBuiltinFunctionWithErasedValueParametersInJava(simpleFunctionDescriptor) == null) {
                arrayList.add(obj);
            }
        }
        return arrayList;
    }

    private final boolean doesOverrideRenamedBuiltins(SimpleFunctionDescriptor simpleFunctionDescriptor) {
        SpecialGenericSignatures.Companion companion = SpecialGenericSignatures.Companion;
        Name name = simpleFunctionDescriptor.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        Name builtinFunctionNamesByJvmName = companion.getBuiltinFunctionNamesByJvmName(name);
        if (builtinFunctionNamesByJvmName == null) {
            return false;
        }
        Set<SimpleFunctionDescriptor> functionsFromSupertypes = getFunctionsFromSupertypes(builtinFunctionNamesByJvmName);
        ArrayList arrayList = new ArrayList();
        for (Object obj : functionsFromSupertypes) {
            if (SpecialBuiltinMembers.doesOverrideBuiltinWithDifferentJvmName((SimpleFunctionDescriptor) obj)) {
                arrayList.add(obj);
            }
        }
        ArrayList arrayList2 = arrayList;
        if (arrayList2.isEmpty()) {
            return false;
        }
        SimpleFunctionDescriptor simpleFunctionDescriptorCreateRenamedCopy = createRenamedCopy(simpleFunctionDescriptor, builtinFunctionNamesByJvmName);
        ArrayList arrayList3 = arrayList2;
        if ((arrayList3 instanceof Collection) && arrayList3.isEmpty()) {
            return false;
        }
        Iterator it = arrayList3.iterator();
        while (it.hasNext()) {
            if (doesOverrideRenamedDescriptor((SimpleFunctionDescriptor) it.next(), simpleFunctionDescriptorCreateRenamedCopy)) {
                return true;
            }
        }
        return false;
    }

    private final boolean doesOverrideSuspendFunction(SimpleFunctionDescriptor simpleFunctionDescriptor) {
        SimpleFunctionDescriptor simpleFunctionDescriptorCreateSuspendView = createSuspendView(simpleFunctionDescriptor);
        if (simpleFunctionDescriptorCreateSuspendView == null) {
            return false;
        }
        Name name = simpleFunctionDescriptor.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        Set<SimpleFunctionDescriptor> functionsFromSupertypes = getFunctionsFromSupertypes(name);
        if ((functionsFromSupertypes instanceof Collection) && functionsFromSupertypes.isEmpty()) {
            return false;
        }
        for (SimpleFunctionDescriptor simpleFunctionDescriptor2 : functionsFromSupertypes) {
            if (simpleFunctionDescriptor2.isSuspend() && doesOverride(simpleFunctionDescriptorCreateSuspendView, simpleFunctionDescriptor2)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0037  */
    private final SimpleFunctionDescriptor createSuspendView(SimpleFunctionDescriptor simpleFunctionDescriptor) {
        FqName safe;
        FqNameUnsafe fqNameUnsafe;
        List<ValueParameterDescriptor> valueParameters = simpleFunctionDescriptor.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
        ValueParameterDescriptor valueParameterDescriptor = (ValueParameterDescriptor) CollectionsKt.lastOrNull((List) valueParameters);
        if (valueParameterDescriptor != null) {
            ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = valueParameterDescriptor.getType().getConstructor().mo1491getDeclarationDescriptor();
            if (classifierDescriptorMo1491getDeclarationDescriptor == null || (fqNameUnsafe = DescriptorUtilsKt.getFqNameUnsafe(classifierDescriptorMo1491getDeclarationDescriptor)) == null) {
                safe = null;
            } else {
                if (!fqNameUnsafe.isSafe()) {
                    fqNameUnsafe = null;
                }
                if (fqNameUnsafe != null) {
                    safe = fqNameUnsafe.toSafe();
                } else {
                    safe = null;
                }
            }
            if (!Intrinsics.areEqual(safe, StandardNames.CONTINUATION_INTERFACE_FQ_NAME)) {
                valueParameterDescriptor = null;
            }
            if (valueParameterDescriptor != null) {
                FunctionDescriptor.CopyBuilder<? extends SimpleFunctionDescriptor> copyBuilderNewCopyBuilder = simpleFunctionDescriptor.newCopyBuilder();
                List<ValueParameterDescriptor> valueParameters2 = simpleFunctionDescriptor.getValueParameters();
                Intrinsics.checkNotNullExpressionValue(valueParameters2, "getValueParameters(...)");
                SimpleFunctionDescriptor simpleFunctionDescriptor2 = (SimpleFunctionDescriptor) copyBuilderNewCopyBuilder.setValueParameters(CollectionsKt.dropLast(valueParameters2, 1)).setReturnType(valueParameterDescriptor.getType().getArguments().get(0).getType()).build();
                SimpleFunctionDescriptorImpl simpleFunctionDescriptorImpl = (SimpleFunctionDescriptorImpl) simpleFunctionDescriptor2;
                if (simpleFunctionDescriptorImpl != null) {
                    simpleFunctionDescriptorImpl.setSuspend(true);
                }
                return simpleFunctionDescriptor2;
            }
        }
        return null;
    }

    private final SimpleFunctionDescriptor createRenamedCopy(SimpleFunctionDescriptor simpleFunctionDescriptor, Name name) {
        FunctionDescriptor.CopyBuilder<? extends SimpleFunctionDescriptor> copyBuilderNewCopyBuilder = simpleFunctionDescriptor.newCopyBuilder();
        copyBuilderNewCopyBuilder.setName(name);
        copyBuilderNewCopyBuilder.setSignatureChange();
        copyBuilderNewCopyBuilder.setPreserveSourceElement();
        FunctionDescriptor functionDescriptorBuild = copyBuilderNewCopyBuilder.build();
        Intrinsics.checkNotNull(functionDescriptorBuild);
        return (SimpleFunctionDescriptor) functionDescriptorBuild;
    }

    private final boolean doesOverrideRenamedDescriptor(SimpleFunctionDescriptor simpleFunctionDescriptor, FunctionDescriptor functionDescriptor) {
        if (BuiltinMethodsWithDifferentJvmName.INSTANCE.isRemoveAtByIndex(simpleFunctionDescriptor)) {
            functionDescriptor = functionDescriptor.getOriginal();
        }
        Intrinsics.checkNotNull(functionDescriptor);
        return doesOverride(functionDescriptor, simpleFunctionDescriptor);
    }

    private final boolean doesOverride(CallableDescriptor callableDescriptor, CallableDescriptor callableDescriptor2) {
        OverridingUtil.OverrideCompatibilityInfo.Result result = OverridingUtil.DEFAULT.isOverridableByWithoutExternalConditions(callableDescriptor2, callableDescriptor, true).getResult();
        Intrinsics.checkNotNullExpressionValue(result, "getResult(...)");
        return result == OverridingUtil.OverrideCompatibilityInfo.Result.OVERRIDABLE && !JavaIncompatibilityRulesOverridabilityCondition.Companion.doesJavaOverrideHaveIncompatibleValueParameterKinds(callableDescriptor2, callableDescriptor);
    }

    private final SimpleFunctionDescriptor findGetterOverride(PropertyDescriptor propertyDescriptor, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1) {
        PropertyGetterDescriptor getter = propertyDescriptor.getGetter();
        PropertyGetterDescriptor propertyGetterDescriptor = getter != null ? (PropertyGetterDescriptor) SpecialBuiltinMembers.getOverriddenBuiltinWithDifferentJvmName(getter) : null;
        String builtinSpecialPropertyGetterName = propertyGetterDescriptor != null ? ClassicBuiltinSpecialProperties.INSTANCE.getBuiltinSpecialPropertyGetterName(propertyGetterDescriptor) : null;
        if (builtinSpecialPropertyGetterName != null && !SpecialBuiltinMembers.hasRealKotlinSuperClassWithOverrideOf(getOwnerDescriptor(), propertyGetterDescriptor)) {
            return findGetterByName(propertyDescriptor, builtinSpecialPropertyGetterName, function1);
        }
        String strAsString = propertyDescriptor.getName().asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        return findGetterByName(propertyDescriptor, JvmAbi.getterName(strAsString), function1);
    }

    private final SimpleFunctionDescriptor findGetterByName(PropertyDescriptor propertyDescriptor, String str, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1) {
        SimpleFunctionDescriptor simpleFunctionDescriptor;
        Name nameIdentifier = Name.identifier(str);
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        Iterator<T> it = function1.invoke(nameIdentifier).iterator();
        do {
            simpleFunctionDescriptor = null;
            if (!it.hasNext()) {
                break;
            }
            SimpleFunctionDescriptor simpleFunctionDescriptor2 = (SimpleFunctionDescriptor) it.next();
            if (simpleFunctionDescriptor2.getValueParameters().size() == 0) {
                KotlinTypeChecker kotlinTypeChecker = KotlinTypeChecker.DEFAULT;
                KotlinType returnType = simpleFunctionDescriptor2.getReturnType();
                if (returnType == null ? false : kotlinTypeChecker.isSubtypeOf(returnType, propertyDescriptor.getType())) {
                    simpleFunctionDescriptor = simpleFunctionDescriptor2;
                }
            }
        } while (simpleFunctionDescriptor == null);
        return simpleFunctionDescriptor;
    }

    private final SimpleFunctionDescriptor findSetterOverride(PropertyDescriptor propertyDescriptor, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1) {
        SimpleFunctionDescriptor simpleFunctionDescriptor;
        KotlinType returnType;
        String strAsString = propertyDescriptor.getName().asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        Name nameIdentifier = Name.identifier(JvmAbi.setterName(strAsString));
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        Iterator<T> it = function1.invoke(nameIdentifier).iterator();
        do {
            simpleFunctionDescriptor = null;
            if (!it.hasNext()) {
                break;
            }
            SimpleFunctionDescriptor simpleFunctionDescriptor2 = (SimpleFunctionDescriptor) it.next();
            if (simpleFunctionDescriptor2.getValueParameters().size() == 1 && (returnType = simpleFunctionDescriptor2.getReturnType()) != null && KotlinBuiltIns.isUnit(returnType)) {
                KotlinTypeChecker kotlinTypeChecker = KotlinTypeChecker.DEFAULT;
                List<ValueParameterDescriptor> valueParameters = simpleFunctionDescriptor2.getValueParameters();
                Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
                if (kotlinTypeChecker.equalTypes(((ValueParameterDescriptor) CollectionsKt.single((List) valueParameters)).getType(), propertyDescriptor.getType())) {
                    simpleFunctionDescriptor = simpleFunctionDescriptor2;
                }
            }
        } while (simpleFunctionDescriptor == null);
        return simpleFunctionDescriptor;
    }

    private final boolean doesClassOverridesProperty(PropertyDescriptor propertyDescriptor, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1) {
        if (JavaDescriptorUtilKt.isJavaField(propertyDescriptor)) {
            return false;
        }
        SimpleFunctionDescriptor simpleFunctionDescriptorFindGetterOverride = findGetterOverride(propertyDescriptor, function1);
        SimpleFunctionDescriptor simpleFunctionDescriptorFindSetterOverride = findSetterOverride(propertyDescriptor, function1);
        if (simpleFunctionDescriptorFindGetterOverride == null) {
            return false;
        }
        if (propertyDescriptor.isVar()) {
            return simpleFunctionDescriptorFindSetterOverride != null && simpleFunctionDescriptorFindSetterOverride.getModality() == simpleFunctionDescriptorFindGetterOverride.getModality();
        }
        return true;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    protected void computeNonDeclaredFunctions(Collection<SimpleFunctionDescriptor> result, Name name) {
        Intrinsics.checkNotNullParameter(result, "result");
        Intrinsics.checkNotNullParameter(name, "name");
        Set<SimpleFunctionDescriptor> functionsFromSupertypes = getFunctionsFromSupertypes(name);
        if (!SpecialGenericSignatures.Companion.getSameAsRenamedInJvmBuiltin(name) && !BuiltinMethodsWithSpecialGenericSignature.INSTANCE.getSameAsBuiltinMethodWithErasedValueParameters(name)) {
            Set<SimpleFunctionDescriptor> set = functionsFromSupertypes;
            if (!(set instanceof Collection) || !set.isEmpty()) {
                Iterator<T> it = set.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((FunctionDescriptor) it.next()).isSuspend()) {
                        }
                    }
                }
            }
            ArrayList arrayList = new ArrayList();
            for (Object obj : set) {
                if (isVisibleAsFunctionInCurrentClass((SimpleFunctionDescriptor) obj)) {
                    arrayList.add(obj);
                }
            }
            addFunctionFromSupertypes(result, name, arrayList, false);
            return;
        }
        SmartSet smartSetCreate = SmartSet.Companion.create();
        Collection<? extends SimpleFunctionDescriptor> collectionResolveOverridesForNonStaticMembers = DescriptorResolverUtils.resolveOverridesForNonStaticMembers(name, functionsFromSupertypes, CollectionsKt.emptyList(), getOwnerDescriptor(), ErrorReporter.DO_NOTHING, getC().getComponents().getKotlinTypeChecker().getOverridingUtil());
        Intrinsics.checkNotNullExpressionValue(collectionResolveOverridesForNonStaticMembers, "resolveOverridesForNonStaticMembers(...)");
        addOverriddenSpecialMethods(name, result, collectionResolveOverridesForNonStaticMembers, result, new AnonymousClass3(this));
        addOverriddenSpecialMethods(name, result, collectionResolveOverridesForNonStaticMembers, smartSetCreate, new AnonymousClass4(this));
        ArrayList arrayList2 = new ArrayList();
        for (Object obj2 : functionsFromSupertypes) {
            if (isVisibleAsFunctionInCurrentClass((SimpleFunctionDescriptor) obj2)) {
                arrayList2.add(obj2);
            }
        }
        addFunctionFromSupertypes(result, name, CollectionsKt.plus((Collection) arrayList2, (Iterable) smartSetCreate), true);
    }

    /* JADX INFO: renamed from: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$computeNonDeclaredFunctions$3, reason: invalid class name */
    /* JADX INFO: compiled from: LazyJavaClassMemberScope.kt */
    /* synthetic */ class AnonymousClass3 extends FunctionReference implements Function1<Name, Collection<? extends SimpleFunctionDescriptor>> {
        AnonymousClass3(Object obj) {
            super(1, obj);
        }

        @Override // kotlin.jvm.internal.CallableReference, kotlin.reflect.KCallable
        public final String getName() {
            return "searchMethodsByNameWithoutBuiltinMagic";
        }

        @Override // kotlin.jvm.internal.CallableReference
        public final KDeclarationContainer getOwner() {
            return Reflection.getOrCreateKotlinClass(LazyJavaClassMemberScope.class);
        }

        @Override // kotlin.jvm.internal.CallableReference
        public final String getSignature() {
            return "searchMethodsByNameWithoutBuiltinMagic(Lorg/jetbrains/kotlin/name/Name;)Ljava/util/Collection;";
        }

        @Override // kotlin.jvm.functions.Function1
        public final Collection<SimpleFunctionDescriptor> invoke(Name p0) {
            Intrinsics.checkNotNullParameter(p0, "p0");
            return ((LazyJavaClassMemberScope) this.receiver).searchMethodsByNameWithoutBuiltinMagic(p0);
        }
    }

    /* JADX INFO: renamed from: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$computeNonDeclaredFunctions$4, reason: invalid class name */
    /* JADX INFO: compiled from: LazyJavaClassMemberScope.kt */
    /* synthetic */ class AnonymousClass4 extends FunctionReference implements Function1<Name, Collection<? extends SimpleFunctionDescriptor>> {
        AnonymousClass4(Object obj) {
            super(1, obj);
        }

        @Override // kotlin.jvm.internal.CallableReference, kotlin.reflect.KCallable
        public final String getName() {
            return "searchMethodsInSupertypesWithoutBuiltinMagic";
        }

        @Override // kotlin.jvm.internal.CallableReference
        public final KDeclarationContainer getOwner() {
            return Reflection.getOrCreateKotlinClass(LazyJavaClassMemberScope.class);
        }

        @Override // kotlin.jvm.internal.CallableReference
        public final String getSignature() {
            return "searchMethodsInSupertypesWithoutBuiltinMagic(Lorg/jetbrains/kotlin/name/Name;)Ljava/util/Collection;";
        }

        @Override // kotlin.jvm.functions.Function1
        public final Collection<SimpleFunctionDescriptor> invoke(Name p0) {
            Intrinsics.checkNotNullParameter(p0, "p0");
            return ((LazyJavaClassMemberScope) this.receiver).searchMethodsInSupertypesWithoutBuiltinMagic(p0);
        }
    }

    private final void addFunctionFromSupertypes(Collection<SimpleFunctionDescriptor> collection, Name name, Collection<? extends SimpleFunctionDescriptor> collection2, boolean z) {
        Collection<? extends SimpleFunctionDescriptor> collectionResolveOverridesForNonStaticMembers = DescriptorResolverUtils.resolveOverridesForNonStaticMembers(name, collection2, collection, getOwnerDescriptor(), getC().getComponents().getErrorReporter(), getC().getComponents().getKotlinTypeChecker().getOverridingUtil());
        Intrinsics.checkNotNullExpressionValue(collectionResolveOverridesForNonStaticMembers, "resolveOverridesForNonStaticMembers(...)");
        if (!z) {
            collection.addAll(collectionResolveOverridesForNonStaticMembers);
            return;
        }
        Collection<? extends SimpleFunctionDescriptor> collection3 = collectionResolveOverridesForNonStaticMembers;
        List listPlus = CollectionsKt.plus((Collection) collection, (Iterable) collection3);
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(collection3, 10));
        for (SimpleFunctionDescriptor simpleFunctionDescriptorCreateHiddenCopyIfBuiltinAlreadyAccidentallyOverridden : collection3) {
            SimpleFunctionDescriptor simpleFunctionDescriptor = (SimpleFunctionDescriptor) SpecialBuiltinMembers.getOverriddenSpecialBuiltin(simpleFunctionDescriptorCreateHiddenCopyIfBuiltinAlreadyAccidentallyOverridden);
            if (simpleFunctionDescriptor == null) {
                Intrinsics.checkNotNull(simpleFunctionDescriptorCreateHiddenCopyIfBuiltinAlreadyAccidentallyOverridden);
            } else {
                Intrinsics.checkNotNull(simpleFunctionDescriptorCreateHiddenCopyIfBuiltinAlreadyAccidentallyOverridden);
                simpleFunctionDescriptorCreateHiddenCopyIfBuiltinAlreadyAccidentallyOverridden = createHiddenCopyIfBuiltinAlreadyAccidentallyOverridden(simpleFunctionDescriptorCreateHiddenCopyIfBuiltinAlreadyAccidentallyOverridden, simpleFunctionDescriptor, listPlus);
            }
            arrayList.add(simpleFunctionDescriptorCreateHiddenCopyIfBuiltinAlreadyAccidentallyOverridden);
        }
        collection.addAll(arrayList);
    }

    private final void addOverriddenSpecialMethods(Name name, Collection<? extends SimpleFunctionDescriptor> collection, Collection<? extends SimpleFunctionDescriptor> collection2, Collection<SimpleFunctionDescriptor> collection3, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1) {
        for (SimpleFunctionDescriptor simpleFunctionDescriptor : collection2) {
            kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.addIfNotNull(collection3, obtainOverrideForBuiltinWithDifferentJvmName(simpleFunctionDescriptor, function1, name, collection));
            kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.addIfNotNull(collection3, obtainOverrideForBuiltInWithErasedValueParametersInJava(simpleFunctionDescriptor, function1, collection));
            kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.addIfNotNull(collection3, obtainOverrideForSuspend(simpleFunctionDescriptor, function1));
        }
    }

    private final SimpleFunctionDescriptor obtainOverrideForBuiltInWithErasedValueParametersInJava(SimpleFunctionDescriptor simpleFunctionDescriptor, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1, Collection<? extends SimpleFunctionDescriptor> collection) {
        SimpleFunctionDescriptor simpleFunctionDescriptorCreateOverrideForBuiltinFunctionWithErasedParameterIfNeeded;
        FunctionDescriptor overriddenBuiltinFunctionWithErasedValueParametersInJava = BuiltinMethodsWithSpecialGenericSignature.getOverriddenBuiltinFunctionWithErasedValueParametersInJava(simpleFunctionDescriptor);
        if (overriddenBuiltinFunctionWithErasedValueParametersInJava == null || (simpleFunctionDescriptorCreateOverrideForBuiltinFunctionWithErasedParameterIfNeeded = createOverrideForBuiltinFunctionWithErasedParameterIfNeeded(overriddenBuiltinFunctionWithErasedValueParametersInJava, function1)) == null) {
            return null;
        }
        if (!isVisibleAsFunctionInCurrentClass(simpleFunctionDescriptorCreateOverrideForBuiltinFunctionWithErasedParameterIfNeeded)) {
            simpleFunctionDescriptorCreateOverrideForBuiltinFunctionWithErasedParameterIfNeeded = null;
        }
        if (simpleFunctionDescriptorCreateOverrideForBuiltinFunctionWithErasedParameterIfNeeded != null) {
            return createHiddenCopyIfBuiltinAlreadyAccidentallyOverridden(simpleFunctionDescriptorCreateOverrideForBuiltinFunctionWithErasedParameterIfNeeded, overriddenBuiltinFunctionWithErasedValueParametersInJava, collection);
        }
        return null;
    }

    private final SimpleFunctionDescriptor obtainOverrideForBuiltinWithDifferentJvmName(SimpleFunctionDescriptor simpleFunctionDescriptor, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1, Name name, Collection<? extends SimpleFunctionDescriptor> collection) {
        SimpleFunctionDescriptor simpleFunctionDescriptor2 = (SimpleFunctionDescriptor) SpecialBuiltinMembers.getOverriddenBuiltinWithDifferentJvmName(simpleFunctionDescriptor);
        if (simpleFunctionDescriptor2 == null) {
            return null;
        }
        String jvmMethodNameIfSpecial = SpecialBuiltinMembers.getJvmMethodNameIfSpecial(simpleFunctionDescriptor2);
        Intrinsics.checkNotNull(jvmMethodNameIfSpecial);
        Name nameIdentifier = Name.identifier(jvmMethodNameIfSpecial);
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        Iterator<? extends SimpleFunctionDescriptor> it = function1.invoke(nameIdentifier).iterator();
        while (it.hasNext()) {
            SimpleFunctionDescriptor simpleFunctionDescriptorCreateRenamedCopy = createRenamedCopy(it.next(), name);
            if (doesOverrideRenamedDescriptor(simpleFunctionDescriptor2, simpleFunctionDescriptorCreateRenamedCopy)) {
                return createHiddenCopyIfBuiltinAlreadyAccidentallyOverridden(simpleFunctionDescriptorCreateRenamedCopy, simpleFunctionDescriptor2, collection);
            }
        }
        return null;
    }

    private final SimpleFunctionDescriptor obtainOverrideForSuspend(SimpleFunctionDescriptor simpleFunctionDescriptor, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1) {
        if (!simpleFunctionDescriptor.isSuspend()) {
            return null;
        }
        Name name = simpleFunctionDescriptor.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        Iterator<T> it = function1.invoke(name).iterator();
        while (it.hasNext()) {
            SimpleFunctionDescriptor simpleFunctionDescriptorCreateSuspendView = createSuspendView((SimpleFunctionDescriptor) it.next());
            if (simpleFunctionDescriptorCreateSuspendView == null || !doesOverride(simpleFunctionDescriptorCreateSuspendView, simpleFunctionDescriptor)) {
                simpleFunctionDescriptorCreateSuspendView = null;
            }
            if (simpleFunctionDescriptorCreateSuspendView != null) {
                return simpleFunctionDescriptorCreateSuspendView;
            }
        }
        return null;
    }

    private final SimpleFunctionDescriptor createHiddenCopyIfBuiltinAlreadyAccidentallyOverridden(SimpleFunctionDescriptor simpleFunctionDescriptor, CallableDescriptor callableDescriptor, Collection<? extends SimpleFunctionDescriptor> collection) {
        Collection<? extends SimpleFunctionDescriptor> collection2 = collection;
        if ((collection2 instanceof Collection) && collection2.isEmpty()) {
            return simpleFunctionDescriptor;
        }
        for (SimpleFunctionDescriptor simpleFunctionDescriptor2 : collection2) {
            if (!Intrinsics.areEqual(simpleFunctionDescriptor, simpleFunctionDescriptor2) && simpleFunctionDescriptor2.getInitialSignatureDescriptor() == null && doesOverride(simpleFunctionDescriptor2, callableDescriptor)) {
                FunctionDescriptor functionDescriptorBuild = simpleFunctionDescriptor.newCopyBuilder().setHiddenToOvercomeSignatureClash().build();
                Intrinsics.checkNotNull(functionDescriptorBuild);
                return (SimpleFunctionDescriptor) functionDescriptorBuild;
            }
        }
        return simpleFunctionDescriptor;
    }

    private final SimpleFunctionDescriptor createOverrideForBuiltinFunctionWithErasedParameterIfNeeded(FunctionDescriptor functionDescriptor, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1) {
        Object next;
        Name name = functionDescriptor.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        Iterator<T> it = function1.invoke(name).iterator();
        do {
            if (!it.hasNext()) {
                next = null;
                break;
            }
            next = it.next();
        } while (!hasSameJvmDescriptorButDoesNotOverride((SimpleFunctionDescriptor) next, functionDescriptor));
        SimpleFunctionDescriptor simpleFunctionDescriptor = (SimpleFunctionDescriptor) next;
        if (simpleFunctionDescriptor == null) {
            return null;
        }
        FunctionDescriptor.CopyBuilder<? extends SimpleFunctionDescriptor> copyBuilderNewCopyBuilder = simpleFunctionDescriptor.newCopyBuilder();
        List<ValueParameterDescriptor> valueParameters = functionDescriptor.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
        List<ValueParameterDescriptor> list = valueParameters;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it2 = list.iterator();
        while (it2.hasNext()) {
            arrayList.add(((ValueParameterDescriptor) it2.next()).getType());
        }
        List<ValueParameterDescriptor> valueParameters2 = simpleFunctionDescriptor.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters2, "getValueParameters(...)");
        copyBuilderNewCopyBuilder.setValueParameters(UtilKt.copyValueParameters(arrayList, valueParameters2, functionDescriptor));
        copyBuilderNewCopyBuilder.setSignatureChange();
        copyBuilderNewCopyBuilder.setPreserveSourceElement();
        copyBuilderNewCopyBuilder.putUserData(JavaMethodDescriptor.HAS_ERASED_VALUE_PARAMETERS, true);
        return (SimpleFunctionDescriptor) copyBuilderNewCopyBuilder.build();
    }

    private final Set<SimpleFunctionDescriptor> getFunctionsFromSupertypes(Name name) {
        Collection<KotlinType> collectionComputeSupertypes = computeSupertypes();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Iterator<T> it = collectionComputeSupertypes.iterator();
        while (it.hasNext()) {
            CollectionsKt.addAll(linkedHashSet, ((KotlinType) it.next()).getMemberScope().getContributedFunctions(name, NoLookupLocation.WHEN_GET_SUPER_MEMBERS));
        }
        return linkedHashSet;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    protected void computeImplicitlyDeclaredFunctions(Collection<SimpleFunctionDescriptor> result, Name name) {
        Intrinsics.checkNotNullParameter(result, "result");
        Intrinsics.checkNotNullParameter(name, "name");
        if (this.jClass.isRecord() && getDeclaredMemberIndex().invoke().findRecordComponentByName(name) != null) {
            Collection<SimpleFunctionDescriptor> collection = result;
            if (collection.isEmpty()) {
                JavaRecordComponent javaRecordComponentFindRecordComponentByName = getDeclaredMemberIndex().invoke().findRecordComponentByName(name);
                Intrinsics.checkNotNull(javaRecordComponentFindRecordComponentByName);
                result.add(resolveRecordComponentToFunctionDescriptor(javaRecordComponentFindRecordComponentByName));
            } else {
                Iterator<T> it = collection.iterator();
                while (it.hasNext()) {
                    if (((SimpleFunctionDescriptor) it.next()).getValueParameters().isEmpty()) {
                    }
                }
                JavaRecordComponent javaRecordComponentFindRecordComponentByName2 = getDeclaredMemberIndex().invoke().findRecordComponentByName(name);
                Intrinsics.checkNotNull(javaRecordComponentFindRecordComponentByName2);
                result.add(resolveRecordComponentToFunctionDescriptor(javaRecordComponentFindRecordComponentByName2));
            }
        }
        getC().getComponents().getSyntheticPartsProvider().generateMethods(getOwnerDescriptor(), name, result, getC());
    }

    private final JavaMethodDescriptor resolveRecordComponentToFunctionDescriptor(JavaRecordComponent javaRecordComponent) {
        JavaMethodDescriptor javaMethodDescriptorCreateJavaMethod = JavaMethodDescriptor.createJavaMethod(getOwnerDescriptor(), LazyJavaAnnotationsKt.resolveAnnotations(getC(), javaRecordComponent), javaRecordComponent.getName(), getC().getComponents().getSourceElementFactory().source(javaRecordComponent), true);
        Intrinsics.checkNotNullExpressionValue(javaMethodDescriptorCreateJavaMethod, "createJavaMethod(...)");
        javaMethodDescriptorCreateJavaMethod.initialize(null, getDispatchReceiverParameter(), CollectionsKt.emptyList(), CollectionsKt.emptyList(), CollectionsKt.emptyList(), getC().getTypeResolver().transformJavaType(javaRecordComponent.getType(), JavaTypeAttributesKt.toAttributes$default(TypeUsage.COMMON, false, false, null, 6, null)), Modality.Companion.convertFromFlags(false, false, true), DescriptorVisibilities.PUBLIC, null);
        javaMethodDescriptorCreateJavaMethod.setParameterNamesStatus(false, false);
        getC().getComponents().getJavaResolverCache().recordMethod(javaRecordComponent, javaMethodDescriptorCreateJavaMethod);
        return javaMethodDescriptorCreateJavaMethod;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    protected void computeNonDeclaredProperties(Name name, Collection<PropertyDescriptor> result) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(result, "result");
        if (this.jClass.isAnnotationType()) {
            computeAnnotationProperties(name, result);
        }
        Set<PropertyDescriptor> propertiesFromSupertypes = getPropertiesFromSupertypes(name);
        if (propertiesFromSupertypes.isEmpty()) {
            return;
        }
        SmartSet smartSetCreate = SmartSet.Companion.create();
        SmartSet smartSetCreate2 = SmartSet.Companion.create();
        addPropertyOverrideByMethod(propertiesFromSupertypes, result, smartSetCreate, new Function1(this) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$7
            private final LazyJavaClassMemberScope arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return LazyJavaClassMemberScope.computeNonDeclaredProperties$lambda$36(this.arg$0, (Name) obj);
            }
        });
        addPropertyOverrideByMethod(SetsKt.minus((Set) propertiesFromSupertypes, (Iterable) smartSetCreate), smartSetCreate2, null, new Function1(this) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$8
            private final LazyJavaClassMemberScope arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return LazyJavaClassMemberScope.computeNonDeclaredProperties$lambda$37(this.arg$0, (Name) obj);
            }
        });
        Collection<? extends PropertyDescriptor> collectionResolveOverridesForNonStaticMembers = DescriptorResolverUtils.resolveOverridesForNonStaticMembers(name, SetsKt.plus((Set) propertiesFromSupertypes, (Iterable) smartSetCreate2), result, getOwnerDescriptor(), getC().getComponents().getErrorReporter(), getC().getComponents().getKotlinTypeChecker().getOverridingUtil());
        Intrinsics.checkNotNullExpressionValue(collectionResolveOverridesForNonStaticMembers, "resolveOverridesForNonStaticMembers(...)");
        result.addAll(collectionResolveOverridesForNonStaticMembers);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Collection computeNonDeclaredProperties$lambda$36(LazyJavaClassMemberScope this$0, Name it) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(it, "it");
        return this$0.searchMethodsByNameWithoutBuiltinMagic(it);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Collection computeNonDeclaredProperties$lambda$37(LazyJavaClassMemberScope this$0, Name it) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(it, "it");
        return this$0.searchMethodsInSupertypesWithoutBuiltinMagic(it);
    }

    private final void addPropertyOverrideByMethod(Set<? extends PropertyDescriptor> set, Collection<PropertyDescriptor> collection, Set<PropertyDescriptor> set2, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1) {
        for (PropertyDescriptor propertyDescriptor : set) {
            JavaPropertyDescriptor javaPropertyDescriptorCreatePropertyDescriptorByMethods = createPropertyDescriptorByMethods(propertyDescriptor, function1);
            if (javaPropertyDescriptorCreatePropertyDescriptorByMethods != null) {
                collection.add(javaPropertyDescriptorCreatePropertyDescriptorByMethods);
                if (set2 != null) {
                    set2.add(propertyDescriptor);
                    return;
                }
                return;
            }
        }
    }

    private final void computeAnnotationProperties(Name name, Collection<PropertyDescriptor> collection) {
        JavaMethod javaMethod = (JavaMethod) CollectionsKt.singleOrNull(getDeclaredMemberIndex().invoke().findMethodsByName(name));
        if (javaMethod == null) {
            return;
        }
        collection.add(createPropertyDescriptorWithDefaultGetter$default(this, javaMethod, null, Modality.FINAL, 2, null));
    }

    static /* synthetic */ JavaPropertyDescriptor createPropertyDescriptorWithDefaultGetter$default(LazyJavaClassMemberScope lazyJavaClassMemberScope, JavaMethod javaMethod, KotlinType kotlinType, Modality modality, int i, Object obj) {
        if ((i & 2) != 0) {
            kotlinType = null;
        }
        return lazyJavaClassMemberScope.createPropertyDescriptorWithDefaultGetter(javaMethod, kotlinType, modality);
    }

    private final JavaPropertyDescriptor createPropertyDescriptorWithDefaultGetter(JavaMethod javaMethod, KotlinType kotlinType, Modality modality) {
        JavaPropertyDescriptor javaPropertyDescriptorCreate = JavaPropertyDescriptor.create(getOwnerDescriptor(), LazyJavaAnnotationsKt.resolveAnnotations(getC(), javaMethod), modality, UtilsKt.toDescriptorVisibility(javaMethod.getVisibility()), false, javaMethod.getName(), getC().getComponents().getSourceElementFactory().source(javaMethod), false);
        Intrinsics.checkNotNullExpressionValue(javaPropertyDescriptorCreate, "create(...)");
        PropertyGetterDescriptorImpl propertyGetterDescriptorImplCreateDefaultGetter = DescriptorFactory.createDefaultGetter(javaPropertyDescriptorCreate, Annotations.Companion.getEMPTY());
        Intrinsics.checkNotNullExpressionValue(propertyGetterDescriptorImplCreateDefaultGetter, "createDefaultGetter(...)");
        javaPropertyDescriptorCreate.initialize(propertyGetterDescriptorImplCreateDefaultGetter, null);
        KotlinType kotlinTypeComputeMethodReturnType = kotlinType == null ? computeMethodReturnType(javaMethod, ContextKt.childForMethod$default(getC(), javaPropertyDescriptorCreate, javaMethod, 0, 4, null)) : kotlinType;
        javaPropertyDescriptorCreate.setType(kotlinTypeComputeMethodReturnType, CollectionsKt.emptyList(), getDispatchReceiverParameter(), null, CollectionsKt.emptyList());
        propertyGetterDescriptorImplCreateDefaultGetter.initialize(kotlinTypeComputeMethodReturnType);
        return javaPropertyDescriptorCreate;
    }

    private final JavaPropertyDescriptor createPropertyDescriptorByMethods(PropertyDescriptor propertyDescriptor, Function1<? super Name, ? extends Collection<? extends SimpleFunctionDescriptor>> function1) {
        SimpleFunctionDescriptor simpleFunctionDescriptorFindSetterOverride;
        PropertySetterDescriptorImpl propertySetterDescriptorImplCreateSetter = null;
        if (!doesClassOverridesProperty(propertyDescriptor, function1)) {
            return null;
        }
        SimpleFunctionDescriptor simpleFunctionDescriptorFindGetterOverride = findGetterOverride(propertyDescriptor, function1);
        Intrinsics.checkNotNull(simpleFunctionDescriptorFindGetterOverride);
        if (propertyDescriptor.isVar()) {
            simpleFunctionDescriptorFindSetterOverride = findSetterOverride(propertyDescriptor, function1);
            Intrinsics.checkNotNull(simpleFunctionDescriptorFindSetterOverride);
        } else {
            simpleFunctionDescriptorFindSetterOverride = null;
        }
        if (simpleFunctionDescriptorFindSetterOverride != null) {
            simpleFunctionDescriptorFindSetterOverride.getModality();
            simpleFunctionDescriptorFindGetterOverride.getModality();
        }
        JavaForKotlinOverridePropertyDescriptor javaForKotlinOverridePropertyDescriptor = new JavaForKotlinOverridePropertyDescriptor(getOwnerDescriptor(), simpleFunctionDescriptorFindGetterOverride, simpleFunctionDescriptorFindSetterOverride, propertyDescriptor);
        KotlinType returnType = simpleFunctionDescriptorFindGetterOverride.getReturnType();
        Intrinsics.checkNotNull(returnType);
        javaForKotlinOverridePropertyDescriptor.setType(returnType, CollectionsKt.emptyList(), getDispatchReceiverParameter(), null, CollectionsKt.emptyList());
        JavaForKotlinOverridePropertyDescriptor javaForKotlinOverridePropertyDescriptor2 = javaForKotlinOverridePropertyDescriptor;
        PropertyGetterDescriptorImpl propertyGetterDescriptorImplCreateGetter = DescriptorFactory.createGetter(javaForKotlinOverridePropertyDescriptor2, simpleFunctionDescriptorFindGetterOverride.getAnnotations(), false, false, false, simpleFunctionDescriptorFindGetterOverride.getSource());
        propertyGetterDescriptorImplCreateGetter.setInitialSignatureDescriptor(simpleFunctionDescriptorFindGetterOverride);
        propertyGetterDescriptorImplCreateGetter.initialize(javaForKotlinOverridePropertyDescriptor.getType());
        Intrinsics.checkNotNullExpressionValue(propertyGetterDescriptorImplCreateGetter, "apply(...)");
        if (simpleFunctionDescriptorFindSetterOverride != null) {
            List<ValueParameterDescriptor> valueParameters = simpleFunctionDescriptorFindSetterOverride.getValueParameters();
            Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
            ValueParameterDescriptor valueParameterDescriptor = (ValueParameterDescriptor) CollectionsKt.firstOrNull((List) valueParameters);
            if (valueParameterDescriptor == null) {
                throw new AssertionError("No parameter found for " + simpleFunctionDescriptorFindSetterOverride);
            }
            propertySetterDescriptorImplCreateSetter = DescriptorFactory.createSetter(javaForKotlinOverridePropertyDescriptor2, simpleFunctionDescriptorFindSetterOverride.getAnnotations(), valueParameterDescriptor.getAnnotations(), false, false, false, simpleFunctionDescriptorFindSetterOverride.getVisibility(), simpleFunctionDescriptorFindSetterOverride.getSource());
            propertySetterDescriptorImplCreateSetter.setInitialSignatureDescriptor(simpleFunctionDescriptorFindSetterOverride);
        }
        javaForKotlinOverridePropertyDescriptor.initialize(propertyGetterDescriptorImplCreateGetter, propertySetterDescriptorImplCreateSetter);
        return javaForKotlinOverridePropertyDescriptor;
    }

    private final Set<PropertyDescriptor> getPropertiesFromSupertypes(Name name) {
        Collection<KotlinType> collectionComputeSupertypes = computeSupertypes();
        ArrayList arrayList = new ArrayList();
        Iterator<T> it = collectionComputeSupertypes.iterator();
        while (it.hasNext()) {
            Collection<? extends PropertyDescriptor> contributedVariables = ((KotlinType) it.next()).getMemberScope().getContributedVariables(name, NoLookupLocation.WHEN_GET_SUPER_MEMBERS);
            ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(contributedVariables, 10));
            Iterator<T> it2 = contributedVariables.iterator();
            while (it2.hasNext()) {
                arrayList2.add((PropertyDescriptor) it2.next());
            }
            CollectionsKt.addAll(arrayList, arrayList2);
        }
        return CollectionsKt.toSet(arrayList);
    }

    private final Collection<KotlinType> computeSupertypes() {
        if (!this.skipRefinement) {
            return getC().getComponents().getKotlinTypeChecker().getKotlinTypeRefiner().refineSupertypes(getOwnerDescriptor());
        }
        Collection<KotlinType> collectionMo1492getSupertypes = getOwnerDescriptor().getTypeConstructor().mo1492getSupertypes();
        Intrinsics.checkNotNullExpressionValue(collectionMo1492getSupertypes, "getSupertypes(...)");
        return collectionMo1492getSupertypes;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    protected LazyJavaScope.MethodSignatureData resolveMethodSignature(JavaMethod method, List<? extends TypeParameterDescriptor> methodTypeParameters, KotlinType returnType, List<? extends ValueParameterDescriptor> valueParameters) {
        Intrinsics.checkNotNullParameter(method, "method");
        Intrinsics.checkNotNullParameter(methodTypeParameters, "methodTypeParameters");
        Intrinsics.checkNotNullParameter(returnType, "returnType");
        Intrinsics.checkNotNullParameter(valueParameters, "valueParameters");
        SignaturePropagator.PropagatedSignature propagatedSignatureResolvePropagatedSignature = getC().getComponents().getSignaturePropagator().resolvePropagatedSignature(method, getOwnerDescriptor(), returnType, null, valueParameters, methodTypeParameters);
        Intrinsics.checkNotNullExpressionValue(propagatedSignatureResolvePropagatedSignature, "resolvePropagatedSignature(...)");
        KotlinType returnType2 = propagatedSignatureResolvePropagatedSignature.getReturnType();
        Intrinsics.checkNotNullExpressionValue(returnType2, "getReturnType(...)");
        KotlinType receiverType = propagatedSignatureResolvePropagatedSignature.getReceiverType();
        List<ValueParameterDescriptor> valueParameters2 = propagatedSignatureResolvePropagatedSignature.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters2, "getValueParameters(...)");
        List<TypeParameterDescriptor> typeParameters = propagatedSignatureResolvePropagatedSignature.getTypeParameters();
        Intrinsics.checkNotNullExpressionValue(typeParameters, "getTypeParameters(...)");
        boolean zHasStableParameterNames = propagatedSignatureResolvePropagatedSignature.hasStableParameterNames();
        List<String> errors = propagatedSignatureResolvePropagatedSignature.getErrors();
        Intrinsics.checkNotNullExpressionValue(errors, "getErrors(...)");
        return new LazyJavaScope.MethodSignatureData(returnType2, receiverType, valueParameters2, typeParameters, zHasStableParameterNames, errors);
    }

    private final boolean hasSameJvmDescriptorButDoesNotOverride(SimpleFunctionDescriptor simpleFunctionDescriptor, FunctionDescriptor functionDescriptor) {
        String strComputeJvmDescriptor$default = MethodSignatureMappingKt.computeJvmDescriptor$default(simpleFunctionDescriptor, false, false, 2, null);
        FunctionDescriptor original = functionDescriptor.getOriginal();
        Intrinsics.checkNotNullExpressionValue(original, "getOriginal(...)");
        return Intrinsics.areEqual(strComputeJvmDescriptor$default, MethodSignatureMappingKt.computeJvmDescriptor$default(original, false, false, 2, null)) && !doesOverride(simpleFunctionDescriptor, functionDescriptor);
    }

    private final JavaClassConstructorDescriptor resolveConstructor(JavaConstructor javaConstructor) {
        ClassDescriptor ownerDescriptor = getOwnerDescriptor();
        JavaConstructor javaConstructor2 = javaConstructor;
        JavaClassConstructorDescriptor javaClassConstructorDescriptorCreateJavaConstructor = JavaClassConstructorDescriptor.createJavaConstructor(ownerDescriptor, LazyJavaAnnotationsKt.resolveAnnotations(getC(), javaConstructor), false, getC().getComponents().getSourceElementFactory().source(javaConstructor2));
        Intrinsics.checkNotNullExpressionValue(javaClassConstructorDescriptorCreateJavaConstructor, "createJavaConstructor(...)");
        LazyJavaResolverContext lazyJavaResolverContextChildForMethod = ContextKt.childForMethod(getC(), javaClassConstructorDescriptorCreateJavaConstructor, javaConstructor, ownerDescriptor.getDeclaredTypeParameters().size());
        LazyJavaScope.ResolvedValueParameters resolvedValueParametersResolveValueParameters = resolveValueParameters(lazyJavaResolverContextChildForMethod, javaClassConstructorDescriptorCreateJavaConstructor, javaConstructor.getValueParameters());
        List<TypeParameterDescriptor> declaredTypeParameters = ownerDescriptor.getDeclaredTypeParameters();
        Intrinsics.checkNotNullExpressionValue(declaredTypeParameters, "getDeclaredTypeParameters(...)");
        List<TypeParameterDescriptor> list = declaredTypeParameters;
        List<JavaTypeParameter> typeParameters = javaConstructor.getTypeParameters();
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(typeParameters, 10));
        Iterator<T> it = typeParameters.iterator();
        while (it.hasNext()) {
            TypeParameterDescriptor typeParameterDescriptorResolveTypeParameter = lazyJavaResolverContextChildForMethod.getTypeParameterResolver().resolveTypeParameter((JavaTypeParameter) it.next());
            Intrinsics.checkNotNull(typeParameterDescriptorResolveTypeParameter);
            arrayList.add(typeParameterDescriptorResolveTypeParameter);
        }
        javaClassConstructorDescriptorCreateJavaConstructor.initialize(resolvedValueParametersResolveValueParameters.getDescriptors(), UtilsKt.toDescriptorVisibility(javaConstructor.getVisibility()), CollectionsKt.plus((Collection) list, (Iterable) arrayList));
        javaClassConstructorDescriptorCreateJavaConstructor.setHasStableParameterNames(false);
        javaClassConstructorDescriptorCreateJavaConstructor.setHasSynthesizedParameterNames(resolvedValueParametersResolveValueParameters.getHasSynthesizedNames());
        javaClassConstructorDescriptorCreateJavaConstructor.setReturnType(ownerDescriptor.getDefaultType());
        lazyJavaResolverContextChildForMethod.getComponents().getJavaResolverCache().recordConstructor(javaConstructor2, javaClassConstructorDescriptorCreateJavaConstructor);
        return javaClassConstructorDescriptorCreateJavaConstructor;
    }

    private final ClassConstructorDescriptor createDefaultConstructor() {
        List<ValueParameterDescriptor> listEmptyList;
        boolean zIsAnnotationType = this.jClass.isAnnotationType();
        if ((this.jClass.isInterface() || !this.jClass.hasDefaultConstructor()) && !zIsAnnotationType) {
            return null;
        }
        ClassDescriptor ownerDescriptor = getOwnerDescriptor();
        JavaClassConstructorDescriptor javaClassConstructorDescriptorCreateJavaConstructor = JavaClassConstructorDescriptor.createJavaConstructor(ownerDescriptor, Annotations.Companion.getEMPTY(), true, getC().getComponents().getSourceElementFactory().source(this.jClass));
        Intrinsics.checkNotNullExpressionValue(javaClassConstructorDescriptorCreateJavaConstructor, "createJavaConstructor(...)");
        if (zIsAnnotationType) {
            listEmptyList = createAnnotationConstructorParameters(javaClassConstructorDescriptorCreateJavaConstructor);
        } else {
            listEmptyList = Collections.emptyList();
        }
        javaClassConstructorDescriptorCreateJavaConstructor.setHasSynthesizedParameterNames(false);
        javaClassConstructorDescriptorCreateJavaConstructor.initialize(listEmptyList, getConstructorVisibility(ownerDescriptor));
        javaClassConstructorDescriptorCreateJavaConstructor.setHasStableParameterNames(true);
        javaClassConstructorDescriptorCreateJavaConstructor.setReturnType(ownerDescriptor.getDefaultType());
        getC().getComponents().getJavaResolverCache().recordConstructor(this.jClass, javaClassConstructorDescriptorCreateJavaConstructor);
        return javaClassConstructorDescriptorCreateJavaConstructor;
    }

    private final DescriptorVisibility getConstructorVisibility(ClassDescriptor classDescriptor) {
        DescriptorVisibility visibility = classDescriptor.getVisibility();
        Intrinsics.checkNotNullExpressionValue(visibility, "getVisibility(...)");
        if (!Intrinsics.areEqual(visibility, JavaDescriptorVisibilities.PROTECTED_STATIC_VISIBILITY)) {
            return visibility;
        }
        DescriptorVisibility PROTECTED_AND_PACKAGE = JavaDescriptorVisibilities.PROTECTED_AND_PACKAGE;
        Intrinsics.checkNotNullExpressionValue(PROTECTED_AND_PACKAGE, "PROTECTED_AND_PACKAGE");
        return PROTECTED_AND_PACKAGE;
    }

    private final List<ValueParameterDescriptor> createAnnotationConstructorParameters(ClassConstructorDescriptorImpl classConstructorDescriptorImpl) {
        Pair pair;
        Collection<JavaMethod> methods = this.jClass.getMethods();
        ArrayList arrayList = new ArrayList(methods.size());
        JavaTypeAttributes attributes$default = JavaTypeAttributesKt.toAttributes$default(TypeUsage.COMMON, true, false, null, 6, null);
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (Object obj : methods) {
            if (Intrinsics.areEqual(((JavaMethod) obj).getName(), JvmAnnotationNames.DEFAULT_ANNOTATION_MEMBER_NAME)) {
                arrayList2.add(obj);
            } else {
                arrayList3.add(obj);
            }
        }
        Pair pair2 = new Pair(arrayList2, arrayList3);
        List list = (List) pair2.component1();
        List<JavaMethod> list2 = (List) pair2.component2();
        list.size();
        JavaMethod javaMethod = (JavaMethod) CollectionsKt.firstOrNull(list);
        if (javaMethod != null) {
            JavaType returnType = javaMethod.getReturnType();
            if (returnType instanceof JavaArrayType) {
                JavaArrayType javaArrayType = (JavaArrayType) returnType;
                pair = new Pair(getC().getTypeResolver().transformArrayType(javaArrayType, attributes$default, true), getC().getTypeResolver().transformJavaType(javaArrayType.getComponentType(), attributes$default));
            } else {
                pair = new Pair(getC().getTypeResolver().transformJavaType(returnType, attributes$default), null);
            }
            addAnnotationValueParameter(arrayList, classConstructorDescriptorImpl, 0, javaMethod, (KotlinType) pair.component1(), (KotlinType) pair.component2());
        }
        int i = 0;
        int i2 = javaMethod == null ? 0 : 1;
        for (JavaMethod javaMethod2 : list2) {
            addAnnotationValueParameter(arrayList, classConstructorDescriptorImpl, i + i2, javaMethod2, getC().getTypeResolver().transformJavaType(javaMethod2.getReturnType(), attributes$default), null);
            i++;
        }
        return arrayList;
    }

    private final void addAnnotationValueParameter(List<ValueParameterDescriptor> list, ConstructorDescriptor constructorDescriptor, int i, JavaMethod javaMethod, KotlinType kotlinType, KotlinType kotlinType2) {
        ConstructorDescriptor constructorDescriptor2 = constructorDescriptor;
        Annotations empty = Annotations.Companion.getEMPTY();
        Name name = javaMethod.getName();
        KotlinType kotlinTypeMakeNotNullable = TypeUtils.makeNotNullable(kotlinType);
        Intrinsics.checkNotNullExpressionValue(kotlinTypeMakeNotNullable, "makeNotNullable(...)");
        list.add(new ValueParameterDescriptorImpl(constructorDescriptor2, null, i, empty, name, kotlinTypeMakeNotNullable, javaMethod.getHasAnnotationParameterDefaultValue(), false, false, kotlinType2 != null ? TypeUtils.makeNotNullable(kotlinType2) : null, getC().getComponents().getSourceElementFactory().source(javaMethod)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Set nestedClassIndex$lambda$49(LazyJavaClassMemberScope this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return CollectionsKt.toSet(this$0.jClass.getInnerClassNames());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Set generatedNestedClassNames$lambda$50(LazyJavaResolverContext c, LazyJavaClassMemberScope this$0) {
        Intrinsics.checkNotNullParameter(c, "$c");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return CollectionsKt.toSet(c.getComponents().getSyntheticPartsProvider().getNestedClassNames(this$0.getOwnerDescriptor(), c));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Map enumEntryIndex$lambda$53(LazyJavaClassMemberScope this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Collection<JavaField> fields = this$0.jClass.getFields();
        ArrayList arrayList = new ArrayList();
        for (Object obj : fields) {
            if (((JavaField) obj).isEnumEntry()) {
                arrayList.add(obj);
            }
        }
        ArrayList arrayList2 = arrayList;
        LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(arrayList2, 10)), 16));
        for (Object obj2 : arrayList2) {
            linkedHashMap.put(((JavaField) obj2).getName(), obj2);
        }
        return linkedHashMap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ClassDescriptor nestedClasses$lambda$59(final LazyJavaClassMemberScope this$0, LazyJavaResolverContext c, Name name) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(c, "$c");
        Intrinsics.checkNotNullParameter(name, "name");
        ClassDescriptor classDescriptorCreate = null;
        if (this$0.nestedClassIndex.invoke().contains(name)) {
            JavaClassFinder finder = c.getComponents().getFinder();
            ClassId classId = DescriptorUtilsKt.getClassId(this$0.getOwnerDescriptor());
            Intrinsics.checkNotNull(classId);
            JavaClass javaClassFindClass = finder.findClass(new JavaClassFinder.Request(classId.createNestedClassId(name), null, this$0.jClass, 2, null));
            if (javaClassFindClass != null) {
                LazyJavaClassDescriptor lazyJavaClassDescriptor = new LazyJavaClassDescriptor(c, this$0.getOwnerDescriptor(), javaClassFindClass, null, 8, null);
                c.getComponents().getJavaClassesTracker().reportClass(lazyJavaClassDescriptor);
                classDescriptorCreate = lazyJavaClassDescriptor;
            }
            return classDescriptorCreate;
        }
        if (this$0.generatedNestedClassNames.invoke().contains(name)) {
            List<ClassDescriptor> listCreateListBuilder = CollectionsKt.createListBuilder();
            c.getComponents().getSyntheticPartsProvider().generateNestedClass(this$0.getOwnerDescriptor(), name, listCreateListBuilder, c);
            List listBuild = CollectionsKt.build(listCreateListBuilder);
            int size = listBuild.size();
            if (size == 0) {
                return null;
            }
            if (size == 1) {
                return (ClassDescriptor) CollectionsKt.single(listBuild);
            }
            throw new IllegalStateException(("Multiple classes with same name are generated: " + listBuild).toString());
        }
        JavaField javaField = this$0.enumEntryIndex.invoke().get(name);
        if (javaField != null) {
            classDescriptorCreate = EnumEntrySyntheticClassDescriptor.create(c.getStorageManager(), this$0.getOwnerDescriptor(), name, c.getStorageManager().createLazyValue(new Function0(this$0) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaClassMemberScope$$Lambda$9
                private final LazyJavaClassMemberScope arg$0;

                {
                    this.arg$0 = this$0;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return LazyJavaClassMemberScope.nestedClasses$lambda$59$lambda$58(this.arg$0);
                }
            }), LazyJavaAnnotationsKt.resolveAnnotations(c, javaField), c.getComponents().getSourceElementFactory().source(javaField));
        }
        return classDescriptorCreate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Set nestedClasses$lambda$59$lambda$58(LazyJavaClassMemberScope this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return SetsKt.plus((Set) this$0.getFunctionNames(), (Iterable) this$0.getVariableNames());
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    protected ReceiverParameterDescriptor getDispatchReceiverParameter() {
        return DescriptorUtils.getDispatchReceiverParameterIfNeeded(getOwnerDescriptor());
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    /* JADX INFO: renamed from: getContributedClassifier */
    public ClassifierDescriptor mo1493getContributedClassifier(Name name, LookupLocation location) {
        MemoizedFunctionToNullable<Name, ClassDescriptor> memoizedFunctionToNullable;
        ClassDescriptor classDescriptorInvoke;
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        mo1497recordLookup(name, location);
        LazyJavaClassMemberScope lazyJavaClassMemberScope = (LazyJavaClassMemberScope) getMainScope();
        return (lazyJavaClassMemberScope == null || (memoizedFunctionToNullable = lazyJavaClassMemberScope.nestedClasses) == null || (classDescriptorInvoke = memoizedFunctionToNullable.invoke(name)) == null) ? this.nestedClasses.invoke(name) : classDescriptorInvoke;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    public Collection<SimpleFunctionDescriptor> getContributedFunctions(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        mo1497recordLookup(name, location);
        return super.getContributedFunctions(name, location);
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
    public Collection<PropertyDescriptor> getContributedVariables(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        mo1497recordLookup(name, location);
        return super.getContributedVariables(name, location);
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    protected Set<Name> computeClassNames(DescriptorKindFilter kindFilter, Function1<? super Name, Boolean> function1) {
        Intrinsics.checkNotNullParameter(kindFilter, "kindFilter");
        return SetsKt.plus((Set) this.nestedClassIndex.invoke(), (Iterable) this.enumEntryIndex.invoke().keySet());
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    protected Set<Name> computePropertyNames(DescriptorKindFilter kindFilter, Function1<? super Name, Boolean> function1) {
        Intrinsics.checkNotNullParameter(kindFilter, "kindFilter");
        if (this.jClass.isAnnotationType()) {
            return getFunctionNames();
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet(getDeclaredMemberIndex().invoke().getFieldNames());
        Collection<KotlinType> collectionMo1492getSupertypes = getOwnerDescriptor().getTypeConstructor().mo1492getSupertypes();
        Intrinsics.checkNotNullExpressionValue(collectionMo1492getSupertypes, "getSupertypes(...)");
        Iterator<T> it = collectionMo1492getSupertypes.iterator();
        while (it.hasNext()) {
            CollectionsKt.addAll(linkedHashSet, ((KotlinType) it.next()).getMemberScope().getVariableNames());
        }
        return linkedHashSet;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    /* JADX INFO: renamed from: recordLookup */
    public void mo1497recordLookup(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        kotlin.reflect.jvm.internal.impl.incremental.UtilsKt.record(getC().getComponents().getLookupTracker(), location, getOwnerDescriptor(), name);
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaScope
    public String toString() {
        return "Lazy Java member scope for " + this.jClass.getFqName();
    }
}
