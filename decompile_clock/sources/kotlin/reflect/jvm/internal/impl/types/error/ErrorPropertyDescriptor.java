package kotlin.reflect.jvm.internal.impl.types.error;

import java.util.Collection;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorVisitor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility;
import kotlin.reflect.jvm.internal.impl.descriptors.FieldDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.Modality;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyAccessorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyGetterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertySetterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ReceiverParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.PropertyDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutor;

/* JADX INFO: compiled from: ErrorPropertyDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ErrorPropertyDescriptor implements PropertyDescriptor {
    private final /* synthetic */ PropertyDescriptorImpl $$delegate_0;

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public <R, D> R accept(DeclarationDescriptorVisitor<R, D> declarationDescriptorVisitor, D d) {
        return (R) this.$$delegate_0.accept(declarationDescriptorVisitor, d);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor
    public CallableMemberDescriptor copy(DeclarationDescriptor declarationDescriptor, Modality modality, DescriptorVisibility descriptorVisibility, CallableMemberDescriptor.Kind kind, boolean z) {
        PropertyDescriptor propertyDescriptorCopy = this.$$delegate_0.copy(declarationDescriptor, modality, descriptorVisibility, kind, z);
        Intrinsics.checkNotNullExpressionValue(propertyDescriptorCopy, "copy(...)");
        return propertyDescriptorCopy;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor
    public List<PropertyAccessorDescriptor> getAccessors() {
        List<PropertyAccessorDescriptor> accessors = this.$$delegate_0.getAccessors();
        Intrinsics.checkNotNullExpressionValue(accessors, "getAccessors(...)");
        return accessors;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated
    public Annotations getAnnotations() {
        Annotations annotations = this.$$delegate_0.getAnnotations();
        Intrinsics.checkNotNullExpressionValue(annotations, "<get-annotations>(...)");
        return annotations;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor
    public FieldDescriptor getBackingField() {
        return this.$$delegate_0.getBackingField();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.VariableDescriptor
    /* JADX INFO: renamed from: getCompileTimeInitializer */
    public ConstantValue<?> mo1487getCompileTimeInitializer() {
        return this.$$delegate_0.mo1487getCompileTimeInitializer();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorNonRoot, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public DeclarationDescriptor getContainingDeclaration() {
        DeclarationDescriptor containingDeclaration = this.$$delegate_0.getContainingDeclaration();
        Intrinsics.checkNotNullExpressionValue(containingDeclaration, "getContainingDeclaration(...)");
        return containingDeclaration;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public List<ReceiverParameterDescriptor> getContextReceiverParameters() {
        List<ReceiverParameterDescriptor> contextReceiverParameters = this.$$delegate_0.getContextReceiverParameters();
        Intrinsics.checkNotNullExpressionValue(contextReceiverParameters, "getContextReceiverParameters(...)");
        return contextReceiverParameters;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor
    public FieldDescriptor getDelegateField() {
        return this.$$delegate_0.getDelegateField();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public ReceiverParameterDescriptor getDispatchReceiverParameter() {
        return this.$$delegate_0.getDispatchReceiverParameter();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public ReceiverParameterDescriptor getExtensionReceiverParameter() {
        return this.$$delegate_0.getExtensionReceiverParameter();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor
    public PropertyGetterDescriptor getGetter() {
        return this.$$delegate_0.getGetter();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor
    public CallableMemberDescriptor.Kind getKind() {
        CallableMemberDescriptor.Kind kind = this.$$delegate_0.getKind();
        Intrinsics.checkNotNullExpressionValue(kind, "getKind(...)");
        return kind;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor
    public Modality getModality() {
        Modality modality = this.$$delegate_0.getModality();
        Intrinsics.checkNotNullExpressionValue(modality, "getModality(...)");
        return modality;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.Named
    public Name getName() {
        Name name = this.$$delegate_0.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        return name;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public PropertyDescriptor getOriginal() {
        PropertyDescriptor original = this.$$delegate_0.getOriginal();
        Intrinsics.checkNotNullExpressionValue(original, "getOriginal(...)");
        return original;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public Collection<? extends PropertyDescriptor> getOverriddenDescriptors() {
        Collection<? extends PropertyDescriptor> overriddenDescriptors = this.$$delegate_0.getOverriddenDescriptors();
        Intrinsics.checkNotNullExpressionValue(overriddenDescriptors, "getOverriddenDescriptors(...)");
        return overriddenDescriptors;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public KotlinType getReturnType() {
        return this.$$delegate_0.getReturnType();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor
    public PropertySetterDescriptor getSetter() {
        return this.$$delegate_0.getSetter();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorWithSource
    public SourceElement getSource() {
        SourceElement source = this.$$delegate_0.getSource();
        Intrinsics.checkNotNullExpressionValue(source, "getSource(...)");
        return source;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ValueDescriptor
    public KotlinType getType() {
        KotlinType type = this.$$delegate_0.getType();
        Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
        return type;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public List<TypeParameterDescriptor> getTypeParameters() {
        List<TypeParameterDescriptor> typeParameters = this.$$delegate_0.getTypeParameters();
        Intrinsics.checkNotNullExpressionValue(typeParameters, "getTypeParameters(...)");
        return typeParameters;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public <V> V getUserData(CallableDescriptor.UserDataKey<V> userDataKey) {
        return (V) this.$$delegate_0.getUserData(userDataKey);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public List<ValueParameterDescriptor> getValueParameters() {
        List<ValueParameterDescriptor> valueParameters = this.$$delegate_0.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
        return valueParameters;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorWithVisibility
    public DescriptorVisibility getVisibility() {
        DescriptorVisibility visibility = this.$$delegate_0.getVisibility();
        Intrinsics.checkNotNullExpressionValue(visibility, "getVisibility(...)");
        return visibility;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
    public boolean hasSynthesizedParameterNames() {
        return this.$$delegate_0.hasSynthesizedParameterNames();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor
    public boolean isActual() {
        return this.$$delegate_0.isActual();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.VariableDescriptor
    public boolean isConst() {
        return this.$$delegate_0.isConst();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.VariableDescriptorWithAccessors
    public boolean isDelegated() {
        return this.$$delegate_0.isDelegated();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor
    public boolean isExpect() {
        return this.$$delegate_0.isExpect();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor
    public boolean isExternal() {
        return this.$$delegate_0.isExternal();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.VariableDescriptor
    public boolean isLateInit() {
        return this.$$delegate_0.isLateInit();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.VariableDescriptor
    public boolean isVar() {
        return this.$$delegate_0.isVar();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor
    public void setOverriddenDescriptors(Collection<? extends CallableMemberDescriptor> overriddenDescriptors) {
        Intrinsics.checkNotNullParameter(overriddenDescriptors, "overriddenDescriptors");
        this.$$delegate_0.setOverriddenDescriptors(overriddenDescriptors);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.Substitutable
    public PropertyDescriptor substitute(TypeSubstitutor substitutor) {
        Intrinsics.checkNotNullParameter(substitutor, "substitutor");
        return this.$$delegate_0.substitute(substitutor);
    }

    public ErrorPropertyDescriptor() {
        PropertyDescriptorImpl propertyDescriptorImplCreate = PropertyDescriptorImpl.create(ErrorUtils.INSTANCE.getErrorClass(), Annotations.Companion.getEMPTY(), Modality.OPEN, DescriptorVisibilities.PUBLIC, true, Name.special(ErrorEntity.ERROR_PROPERTY.getDebugText()), CallableMemberDescriptor.Kind.DECLARATION, SourceElement.NO_SOURCE, false, false, false, false, false, false);
        propertyDescriptorImplCreate.setType(ErrorUtils.INSTANCE.getErrorPropertyType(), CollectionsKt.emptyList(), null, null, CollectionsKt.emptyList());
        this.$$delegate_0 = propertyDescriptorImplCreate;
    }
}
