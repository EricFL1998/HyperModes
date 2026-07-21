package kotlin.reflect.jvm.internal.impl.types.error;

import java.util.Collection;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.Modality;
import kotlin.reflect.jvm.internal.impl.descriptors.ReceiverParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.SimpleFunctionDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitution;

/* JADX INFO: compiled from: ErrorFunctionDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ErrorFunctionDescriptor extends SimpleFunctionDescriptorImpl {
    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public <V> V getUserData(CallableDescriptor.UserDataKey<V> key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor
    public boolean isSuspend() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor
    public void setOverriddenDescriptors(Collection<? extends CallableMemberDescriptor> overriddenDescriptors) {
        Intrinsics.checkNotNullParameter(overriddenDescriptors, "overriddenDescriptors");
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ErrorFunctionDescriptor(ClassDescriptor containingDeclaration) {
        super(containingDeclaration, null, Annotations.Companion.getEMPTY(), Name.special(ErrorEntity.ERROR_FUNCTION.getDebugText()), CallableMemberDescriptor.Kind.DECLARATION, SourceElement.NO_SOURCE);
        Intrinsics.checkNotNullParameter(containingDeclaration, "containingDeclaration");
        initialize((ReceiverParameterDescriptor) null, (ReceiverParameterDescriptor) null, CollectionsKt.emptyList(), CollectionsKt.emptyList(), CollectionsKt.emptyList(), (KotlinType) ErrorUtils.createErrorType(ErrorTypeKind.RETURN_TYPE_FOR_FUNCTION, new String[0]), Modality.OPEN, DescriptorVisibilities.PUBLIC);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.SimpleFunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl
    protected FunctionDescriptorImpl createSubstitutedCopy(DeclarationDescriptor newOwner, FunctionDescriptor functionDescriptor, CallableMemberDescriptor.Kind kind, Name name, Annotations annotations, SourceElement source) {
        Intrinsics.checkNotNullParameter(newOwner, "newOwner");
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        Intrinsics.checkNotNullParameter(source, "source");
        return this;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.SimpleFunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor
    public SimpleFunctionDescriptor copy(DeclarationDescriptor newOwner, Modality modality, DescriptorVisibility visibility, CallableMemberDescriptor.Kind kind, boolean z) {
        Intrinsics.checkNotNullParameter(newOwner, "newOwner");
        Intrinsics.checkNotNullParameter(modality, "modality");
        Intrinsics.checkNotNullParameter(visibility, "visibility");
        Intrinsics.checkNotNullParameter(kind, "kind");
        return this;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.SimpleFunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor
    public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> newCopyBuilder() {
        return new FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor>() { // from class: kotlin.reflect.jvm.internal.impl.types.error.ErrorFunctionDescriptor.newCopyBuilder.1
            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setOwner(DeclarationDescriptor owner) {
                Intrinsics.checkNotNullParameter(owner, "owner");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setModality(Modality modality) {
                Intrinsics.checkNotNullParameter(modality, "modality");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setVisibility(DescriptorVisibility visibility) {
                Intrinsics.checkNotNullParameter(visibility, "visibility");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setKind(CallableMemberDescriptor.Kind kind) {
                Intrinsics.checkNotNullParameter(kind, "kind");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setCopyOverrides(boolean z) {
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setName(Name name) {
                Intrinsics.checkNotNullParameter(name, "name");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setSubstitution(TypeSubstitution substitution) {
                Intrinsics.checkNotNullParameter(substitution, "substitution");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setValueParameters(List<? extends ValueParameterDescriptor> parameters) {
                Intrinsics.checkNotNullParameter(parameters, "parameters");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public <V> FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> putUserData(CallableDescriptor.UserDataKey<V> userDataKey, V v) {
                Intrinsics.checkNotNullParameter(userDataKey, "userDataKey");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setTypeParameters(List<? extends TypeParameterDescriptor> parameters) {
                Intrinsics.checkNotNullParameter(parameters, "parameters");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setReturnType(KotlinType type) {
                Intrinsics.checkNotNullParameter(type, "type");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setExtensionReceiverParameter(ReceiverParameterDescriptor receiverParameterDescriptor) {
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setDispatchReceiverParameter(ReceiverParameterDescriptor receiverParameterDescriptor) {
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setOriginal(CallableMemberDescriptor callableMemberDescriptor) {
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setSignatureChange() {
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setPreserveSourceElement() {
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setDropOriginalInContainingParts() {
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setHiddenToOvercomeSignatureClash() {
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setHiddenForResolutionEverywhereBesideSupercalls() {
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public FunctionDescriptor.CopyBuilder<SimpleFunctionDescriptor> setAdditionalAnnotations(Annotations additionalAnnotations) {
                Intrinsics.checkNotNullParameter(additionalAnnotations, "additionalAnnotations");
                return this;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor.CopyBuilder
            public SimpleFunctionDescriptor build() {
                return ErrorFunctionDescriptor.this;
            }
        };
    }
}
