package kotlin.reflect.jvm.internal.impl.descriptors.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.Modality;
import kotlin.reflect.jvm.internal.impl.descriptors.ReceiverParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeAliasDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.name.SpecialNames;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorFactory;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers.ImplicitContextReceiver;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers.ReceiverValue;
import kotlin.reflect.jvm.internal.impl.storage.NullableLazyValue;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;
import kotlin.reflect.jvm.internal.impl.types.FlexibleTypesKt;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.SpecialTypesKt;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutor;
import kotlin.reflect.jvm.internal.impl.types.Variance;

/* JADX INFO: compiled from: TypeAliasConstructorDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeAliasConstructorDescriptorImpl extends FunctionDescriptorImpl implements TypeAliasConstructorDescriptor {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(TypeAliasConstructorDescriptorImpl.class), "withDispatchReceiver", "getWithDispatchReceiver()Lorg/jetbrains/kotlin/descriptors/impl/TypeAliasConstructorDescriptor;"))};
    public static final Companion Companion = new Companion(null);
    private final StorageManager storageManager;
    private final TypeAliasDescriptor typeAliasDescriptor;
    private ClassConstructorDescriptor underlyingConstructorDescriptor;
    private final NullableLazyValue withDispatchReceiver$delegate;

    public /* synthetic */ TypeAliasConstructorDescriptorImpl(StorageManager storageManager, TypeAliasDescriptor typeAliasDescriptor, ClassConstructorDescriptor classConstructorDescriptor, TypeAliasConstructorDescriptor typeAliasConstructorDescriptor, Annotations annotations, CallableMemberDescriptor.Kind kind, SourceElement sourceElement, DefaultConstructorMarker defaultConstructorMarker) {
        this(storageManager, typeAliasDescriptor, classConstructorDescriptor, typeAliasConstructorDescriptor, annotations, kind, sourceElement);
    }

    public TypeAliasDescriptor getTypeAliasDescriptor() {
        return this.typeAliasDescriptor;
    }

    private TypeAliasConstructorDescriptorImpl(StorageManager storageManager, TypeAliasDescriptor typeAliasDescriptor, final ClassConstructorDescriptor classConstructorDescriptor, TypeAliasConstructorDescriptor typeAliasConstructorDescriptor, Annotations annotations, CallableMemberDescriptor.Kind kind, SourceElement sourceElement) {
        super(typeAliasDescriptor, typeAliasConstructorDescriptor, annotations, SpecialNames.INIT, kind, sourceElement);
        this.storageManager = storageManager;
        this.typeAliasDescriptor = typeAliasDescriptor;
        setActual(getTypeAliasDescriptor().isActual());
        this.withDispatchReceiver$delegate = storageManager.createNullableLazyValue(new Function0(this, classConstructorDescriptor) { // from class: kotlin.reflect.jvm.internal.impl.descriptors.impl.TypeAliasConstructorDescriptorImpl$$Lambda$0
            private final TypeAliasConstructorDescriptorImpl arg$0;
            private final ClassConstructorDescriptor arg$1;

            {
                this.arg$0 = this;
                this.arg$1 = classConstructorDescriptor;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return TypeAliasConstructorDescriptorImpl.withDispatchReceiver_delegate$lambda$2(this.arg$0, this.arg$1);
            }
        });
        this.underlyingConstructorDescriptor = classConstructorDescriptor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final TypeAliasConstructorDescriptorImpl withDispatchReceiver_delegate$lambda$2(TypeAliasConstructorDescriptorImpl this$0, ClassConstructorDescriptor underlyingConstructorDescriptor) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(underlyingConstructorDescriptor, "$underlyingConstructorDescriptor");
        Annotations annotations = underlyingConstructorDescriptor.getAnnotations();
        CallableMemberDescriptor.Kind kind = underlyingConstructorDescriptor.getKind();
        Intrinsics.checkNotNullExpressionValue(kind, "getKind(...)");
        SourceElement source = this$0.getTypeAliasDescriptor().getSource();
        Intrinsics.checkNotNullExpressionValue(source, "getSource(...)");
        TypeAliasConstructorDescriptorImpl typeAliasConstructorDescriptorImpl = new TypeAliasConstructorDescriptorImpl(this$0.storageManager, this$0.getTypeAliasDescriptor(), underlyingConstructorDescriptor, this$0, annotations, kind, source);
        TypeSubstitutor typeSubstitutorForUnderlyingClass = Companion.getTypeSubstitutorForUnderlyingClass(this$0.getTypeAliasDescriptor());
        if (typeSubstitutorForUnderlyingClass == null) {
            return null;
        }
        ReceiverParameterDescriptor dispatchReceiverParameter = underlyingConstructorDescriptor.getDispatchReceiverParameter();
        ReceiverParameterDescriptor receiverParameterDescriptorSubstitute = dispatchReceiverParameter != null ? dispatchReceiverParameter.substitute(typeSubstitutorForUnderlyingClass) : null;
        List<ReceiverParameterDescriptor> contextReceiverParameters = underlyingConstructorDescriptor.getContextReceiverParameters();
        Intrinsics.checkNotNullExpressionValue(contextReceiverParameters, "getContextReceiverParameters(...)");
        List<ReceiverParameterDescriptor> list = contextReceiverParameters;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(((ReceiverParameterDescriptor) it.next()).substitute(typeSubstitutorForUnderlyingClass));
        }
        typeAliasConstructorDescriptorImpl.initialize(null, receiverParameterDescriptorSubstitute, arrayList, this$0.getTypeAliasDescriptor().getDeclaredTypeParameters(), this$0.getValueParameters(), this$0.getReturnType(), Modality.FINAL, this$0.getTypeAliasDescriptor().getVisibility());
        return typeAliasConstructorDescriptorImpl;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.TypeAliasConstructorDescriptor
    public ClassConstructorDescriptor getUnderlyingConstructorDescriptor() {
        return this.underlyingConstructorDescriptor;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor
    public boolean isPrimary() {
        return getUnderlyingConstructorDescriptor().isPrimary();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.DeclarationDescriptorNonRootImpl, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public TypeAliasDescriptor getContainingDeclaration() {
        return getTypeAliasDescriptor();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor
    public ClassDescriptor getConstructedClass() {
        ClassDescriptor constructedClass = getUnderlyingConstructorDescriptor().getConstructedClass();
        Intrinsics.checkNotNullExpressionValue(constructedClass, "getConstructedClass(...)");
        return constructedClass;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public KotlinType getReturnType() {
        KotlinType returnType = super.getReturnType();
        Intrinsics.checkNotNull(returnType);
        return returnType;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.impl.DeclarationDescriptorNonRootImpl, kotlin.reflect.jvm.internal.impl.descriptors.impl.DeclarationDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public TypeAliasConstructorDescriptor getOriginal() {
        FunctionDescriptor original = super.getOriginal();
        Intrinsics.checkNotNull(original, "null cannot be cast to non-null type org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor");
        return (TypeAliasConstructorDescriptor) original;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.Substitutable
    public TypeAliasConstructorDescriptor substitute(TypeSubstitutor substitutor) {
        Intrinsics.checkNotNullParameter(substitutor, "substitutor");
        FunctionDescriptor functionDescriptorSubstitute = super.substitute(substitutor);
        Intrinsics.checkNotNull(functionDescriptorSubstitute, "null cannot be cast to non-null type org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptorImpl");
        TypeAliasConstructorDescriptorImpl typeAliasConstructorDescriptorImpl = (TypeAliasConstructorDescriptorImpl) functionDescriptorSubstitute;
        TypeSubstitutor typeSubstitutorCreate = TypeSubstitutor.create(typeAliasConstructorDescriptorImpl.getReturnType());
        Intrinsics.checkNotNullExpressionValue(typeSubstitutorCreate, "create(...)");
        ClassConstructorDescriptor classConstructorDescriptorSubstitute = getUnderlyingConstructorDescriptor().getOriginal().substitute(typeSubstitutorCreate);
        if (classConstructorDescriptorSubstitute == null) {
            return null;
        }
        typeAliasConstructorDescriptorImpl.underlyingConstructorDescriptor = classConstructorDescriptorSubstitute;
        return typeAliasConstructorDescriptorImpl;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor
    public TypeAliasConstructorDescriptor copy(DeclarationDescriptor newOwner, Modality modality, DescriptorVisibility visibility, CallableMemberDescriptor.Kind kind, boolean z) {
        Intrinsics.checkNotNullParameter(newOwner, "newOwner");
        Intrinsics.checkNotNullParameter(modality, "modality");
        Intrinsics.checkNotNullParameter(visibility, "visibility");
        Intrinsics.checkNotNullParameter(kind, "kind");
        FunctionDescriptor functionDescriptorBuild = newCopyBuilder().setOwner(newOwner).setModality(modality).setVisibility(visibility).setKind(kind).setCopyOverrides(z).build();
        Intrinsics.checkNotNull(functionDescriptorBuild, "null cannot be cast to non-null type org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor");
        return (TypeAliasConstructorDescriptor) functionDescriptorBuild;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl
    public TypeAliasConstructorDescriptorImpl createSubstitutedCopy(DeclarationDescriptor newOwner, FunctionDescriptor functionDescriptor, CallableMemberDescriptor.Kind kind, Name name, Annotations annotations, SourceElement source) {
        Intrinsics.checkNotNullParameter(newOwner, "newOwner");
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        Intrinsics.checkNotNullParameter(source, "source");
        if (kind != CallableMemberDescriptor.Kind.DECLARATION) {
            CallableMemberDescriptor.Kind kind2 = CallableMemberDescriptor.Kind.SYNTHESIZED;
        }
        return new TypeAliasConstructorDescriptorImpl(this.storageManager, getTypeAliasDescriptor(), getUnderlyingConstructorDescriptor(), this, annotations, CallableMemberDescriptor.Kind.DECLARATION, source);
    }

    /* JADX INFO: compiled from: TypeAliasConstructorDescriptor.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final TypeSubstitutor getTypeSubstitutorForUnderlyingClass(TypeAliasDescriptor typeAliasDescriptor) {
            if (typeAliasDescriptor.getClassDescriptor() == null) {
                return null;
            }
            return TypeSubstitutor.create(typeAliasDescriptor.getExpandedType());
        }

        public final TypeAliasConstructorDescriptor createIfAvailable(StorageManager storageManager, TypeAliasDescriptor typeAliasDescriptor, ClassConstructorDescriptor constructor) {
            ClassConstructorDescriptor classConstructorDescriptorSubstitute;
            List<ReceiverParameterDescriptor> listEmptyList;
            Intrinsics.checkNotNullParameter(storageManager, "storageManager");
            Intrinsics.checkNotNullParameter(typeAliasDescriptor, "typeAliasDescriptor");
            Intrinsics.checkNotNullParameter(constructor, "constructor");
            TypeSubstitutor typeSubstitutorForUnderlyingClass = getTypeSubstitutorForUnderlyingClass(typeAliasDescriptor);
            if (typeSubstitutorForUnderlyingClass == null || (classConstructorDescriptorSubstitute = constructor.substitute(typeSubstitutorForUnderlyingClass)) == null) {
                return null;
            }
            Annotations annotations = constructor.getAnnotations();
            CallableMemberDescriptor.Kind kind = constructor.getKind();
            Intrinsics.checkNotNullExpressionValue(kind, "getKind(...)");
            SourceElement source = typeAliasDescriptor.getSource();
            Intrinsics.checkNotNullExpressionValue(source, "getSource(...)");
            TypeAliasConstructorDescriptorImpl typeAliasConstructorDescriptorImpl = new TypeAliasConstructorDescriptorImpl(storageManager, typeAliasDescriptor, classConstructorDescriptorSubstitute, null, annotations, kind, source, null);
            List<ValueParameterDescriptor> substitutedValueParameters = FunctionDescriptorImpl.getSubstitutedValueParameters(typeAliasConstructorDescriptorImpl, constructor.getValueParameters(), typeSubstitutorForUnderlyingClass);
            if (substitutedValueParameters == null) {
                return null;
            }
            SimpleType simpleTypeLowerIfFlexible = FlexibleTypesKt.lowerIfFlexible(classConstructorDescriptorSubstitute.getReturnType().unwrap());
            SimpleType defaultType = typeAliasDescriptor.getDefaultType();
            Intrinsics.checkNotNullExpressionValue(defaultType, "getDefaultType(...)");
            SimpleType simpleTypeWithAbbreviation = SpecialTypesKt.withAbbreviation(simpleTypeLowerIfFlexible, defaultType);
            ReceiverParameterDescriptor dispatchReceiverParameter = constructor.getDispatchReceiverParameter();
            ReceiverParameterDescriptor receiverParameterDescriptorCreateExtensionReceiverParameterForCallable = dispatchReceiverParameter != null ? DescriptorFactory.createExtensionReceiverParameterForCallable(typeAliasConstructorDescriptorImpl, typeSubstitutorForUnderlyingClass.safeSubstitute(dispatchReceiverParameter.getType(), Variance.INVARIANT), Annotations.Companion.getEMPTY()) : null;
            ClassDescriptor classDescriptor = typeAliasDescriptor.getClassDescriptor();
            if (classDescriptor != null) {
                List<ReceiverParameterDescriptor> contextReceiverParameters = constructor.getContextReceiverParameters();
                Intrinsics.checkNotNullExpressionValue(contextReceiverParameters, "getContextReceiverParameters(...)");
                List<ReceiverParameterDescriptor> list = contextReceiverParameters;
                ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
                int i = 0;
                for (Object obj : list) {
                    int i2 = i + 1;
                    if (i < 0) {
                        CollectionsKt.throwIndexOverflow();
                    }
                    ReceiverParameterDescriptor receiverParameterDescriptor = (ReceiverParameterDescriptor) obj;
                    KotlinType kotlinTypeSafeSubstitute = typeSubstitutorForUnderlyingClass.safeSubstitute(receiverParameterDescriptor.getType(), Variance.INVARIANT);
                    ReceiverValue value = receiverParameterDescriptor.getValue();
                    Intrinsics.checkNotNull(value, "null cannot be cast to non-null type org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitContextReceiver");
                    arrayList.add(DescriptorFactory.createContextReceiverParameterForClass(classDescriptor, kotlinTypeSafeSubstitute, ((ImplicitContextReceiver) value).getCustomLabelName(), Annotations.Companion.getEMPTY(), i));
                    i = i2;
                }
                listEmptyList = arrayList;
            } else {
                listEmptyList = CollectionsKt.emptyList();
            }
            typeAliasConstructorDescriptorImpl.initialize(receiverParameterDescriptorCreateExtensionReceiverParameterForCallable, null, listEmptyList, typeAliasDescriptor.getDeclaredTypeParameters(), substitutedValueParameters, simpleTypeWithAbbreviation, Modality.FINAL, typeAliasDescriptor.getVisibility());
            return typeAliasConstructorDescriptorImpl;
        }
    }
}
