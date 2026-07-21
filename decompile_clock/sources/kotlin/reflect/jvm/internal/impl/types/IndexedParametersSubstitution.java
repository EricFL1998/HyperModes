package kotlin.reflect.jvm.internal.impl.types;

import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;

/* JADX INFO: compiled from: TypeSubstitution.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class IndexedParametersSubstitution extends TypeSubstitution {
    private final boolean approximateContravariantCapturedTypes;
    private final TypeProjection[] arguments;
    private final TypeParameterDescriptor[] parameters;

    public /* synthetic */ IndexedParametersSubstitution(TypeParameterDescriptor[] typeParameterDescriptorArr, TypeProjection[] typeProjectionArr, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(typeParameterDescriptorArr, typeProjectionArr, (i & 4) != 0 ? false : z);
    }

    public final TypeParameterDescriptor[] getParameters() {
        return this.parameters;
    }

    public final TypeProjection[] getArguments() {
        return this.arguments;
    }

    public IndexedParametersSubstitution(TypeParameterDescriptor[] parameters, TypeProjection[] arguments, boolean z) {
        Intrinsics.checkNotNullParameter(parameters, "parameters");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        this.parameters = parameters;
        this.arguments = arguments;
        this.approximateContravariantCapturedTypes = z;
        int length = parameters.length;
        int length2 = arguments.length;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public IndexedParametersSubstitution(List<? extends TypeParameterDescriptor> parameters, List<? extends TypeProjection> argumentsList) {
        this((TypeParameterDescriptor[]) parameters.toArray(new TypeParameterDescriptor[0]), (TypeProjection[]) argumentsList.toArray(new TypeProjection[0]), false, 4, null);
        Intrinsics.checkNotNullParameter(parameters, "parameters");
        Intrinsics.checkNotNullParameter(argumentsList, "argumentsList");
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    public boolean isEmpty() {
        return this.arguments.length == 0;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    public boolean approximateContravariantCapturedTypes() {
        return this.approximateContravariantCapturedTypes;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    /* JADX INFO: renamed from: get */
    public TypeProjection mo1496get(KotlinType key) {
        Intrinsics.checkNotNullParameter(key, "key");
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = key.getConstructor().mo1491getDeclarationDescriptor();
        TypeParameterDescriptor typeParameterDescriptor = classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeParameterDescriptor ? (TypeParameterDescriptor) classifierDescriptorMo1491getDeclarationDescriptor : null;
        if (typeParameterDescriptor == null) {
            return null;
        }
        int index = typeParameterDescriptor.getIndex();
        TypeParameterDescriptor[] typeParameterDescriptorArr = this.parameters;
        if (index >= typeParameterDescriptorArr.length || !Intrinsics.areEqual(typeParameterDescriptorArr[index].getTypeConstructor(), typeParameterDescriptor.getTypeConstructor())) {
            return null;
        }
        return this.arguments[index];
    }
}
