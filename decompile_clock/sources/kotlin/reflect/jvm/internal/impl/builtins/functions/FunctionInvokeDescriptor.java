package kotlin.reflect.jvm.internal.impl.builtins.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IndexedValue;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.FunctionTypesKt;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.Modality;
import kotlin.reflect.jvm.internal.impl.descriptors.ReceiverParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.SimpleFunctionDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ValueParameterDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutor;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.util.OperatorNameConventions;

/* JADX INFO: compiled from: FunctionInvokeDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class FunctionInvokeDescriptor extends SimpleFunctionDescriptorImpl {
    public static final Factory Factory = new Factory(null);

    public /* synthetic */ FunctionInvokeDescriptor(DeclarationDescriptor declarationDescriptor, FunctionInvokeDescriptor functionInvokeDescriptor, CallableMemberDescriptor.Kind kind, boolean z, DefaultConstructorMarker defaultConstructorMarker) {
        this(declarationDescriptor, functionInvokeDescriptor, kind, z);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor
    public boolean isExternal() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor
    public boolean isInline() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor
    public boolean isTailrec() {
        return false;
    }

    private FunctionInvokeDescriptor(DeclarationDescriptor declarationDescriptor, FunctionInvokeDescriptor functionInvokeDescriptor, CallableMemberDescriptor.Kind kind, boolean z) {
        super(declarationDescriptor, functionInvokeDescriptor, Annotations.Companion.getEMPTY(), OperatorNameConventions.INVOKE, kind, SourceElement.NO_SOURCE);
        setOperator(true);
        setSuspend(z);
        setHasStableParameterNames(false);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl
    protected FunctionDescriptor doSubstitute(FunctionDescriptorImpl.CopyConfiguration configuration) {
        Intrinsics.checkNotNullParameter(configuration, "configuration");
        FunctionInvokeDescriptor functionInvokeDescriptor = (FunctionInvokeDescriptor) super.doSubstitute(configuration);
        if (functionInvokeDescriptor == null) {
            return null;
        }
        List<ValueParameterDescriptor> valueParameters = functionInvokeDescriptor.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
        List<ValueParameterDescriptor> list = valueParameters;
        if (!(list instanceof Collection) || !list.isEmpty()) {
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                KotlinType type = ((ValueParameterDescriptor) it.next()).getType();
                Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
                if (FunctionTypesKt.extractParameterNameFromFunctionTypeArgument(type) != null) {
                    List<ValueParameterDescriptor> valueParameters2 = functionInvokeDescriptor.getValueParameters();
                    Intrinsics.checkNotNullExpressionValue(valueParameters2, "getValueParameters(...)");
                    List<ValueParameterDescriptor> list2 = valueParameters2;
                    ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
                    Iterator<T> it2 = list2.iterator();
                    while (it2.hasNext()) {
                        KotlinType type2 = ((ValueParameterDescriptor) it2.next()).getType();
                        Intrinsics.checkNotNullExpressionValue(type2, "getType(...)");
                        arrayList.add(FunctionTypesKt.extractParameterNameFromFunctionTypeArgument(type2));
                    }
                    return functionInvokeDescriptor.replaceParameterNames(arrayList);
                }
            }
        }
        return functionInvokeDescriptor;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.SimpleFunctionDescriptorImpl, kotlin.reflect.jvm.internal.impl.descriptors.impl.FunctionDescriptorImpl
    protected FunctionDescriptorImpl createSubstitutedCopy(DeclarationDescriptor newOwner, FunctionDescriptor functionDescriptor, CallableMemberDescriptor.Kind kind, Name name, Annotations annotations, SourceElement source) {
        Intrinsics.checkNotNullParameter(newOwner, "newOwner");
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        Intrinsics.checkNotNullParameter(source, "source");
        return new FunctionInvokeDescriptor(newOwner, (FunctionInvokeDescriptor) functionDescriptor, kind, isSuspend());
    }

    private final FunctionDescriptor replaceParameterNames(List<Name> list) {
        Name name;
        Pair pair;
        int size = getValueParameters().size() - list.size();
        boolean z = true;
        if (size == 0) {
            List<ValueParameterDescriptor> valueParameters = getValueParameters();
            Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
            List listZip = CollectionsKt.zip(list, valueParameters);
            if (!(listZip instanceof Collection) || !listZip.isEmpty()) {
                Iterator it = listZip.iterator();
                do {
                    if (it.hasNext()) {
                        pair = (Pair) it.next();
                    }
                } while (Intrinsics.areEqual((Name) pair.component1(), ((ValueParameterDescriptor) pair.component2()).getName()));
            }
            return this;
        }
        List<ValueParameterDescriptor> valueParameters2 = getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters2, "getValueParameters(...)");
        List<ValueParameterDescriptor> list2 = valueParameters2;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
        for (ValueParameterDescriptor valueParameterDescriptor : list2) {
            Name name2 = valueParameterDescriptor.getName();
            Intrinsics.checkNotNullExpressionValue(name2, "getName(...)");
            int index = valueParameterDescriptor.getIndex();
            int i = index - size;
            if (i >= 0 && (name = list.get(i)) != null) {
                name2 = name;
            }
            arrayList.add(valueParameterDescriptor.copy(this, name2, index));
        }
        ArrayList arrayList2 = arrayList;
        FunctionDescriptorImpl.CopyConfiguration copyConfigurationNewCopyBuilder = newCopyBuilder(TypeSubstitutor.EMPTY);
        List<Name> list3 = list;
        if ((list3 instanceof Collection) && list3.isEmpty()) {
            z = false;
        } else {
            Iterator<T> it2 = list3.iterator();
            while (it2.hasNext()) {
                if (((Name) it2.next()) == null) {
                }
            }
            z = false;
        }
        FunctionDescriptorImpl.CopyConfiguration original = copyConfigurationNewCopyBuilder.setHasSynthesizedParameterNames(z).setValueParameters((List<ValueParameterDescriptor>) arrayList2).setOriginal((CallableMemberDescriptor) getOriginal());
        Intrinsics.checkNotNullExpressionValue(original, "setOriginal(...)");
        FunctionDescriptor functionDescriptorDoSubstitute = super.doSubstitute(original);
        Intrinsics.checkNotNull(functionDescriptorDoSubstitute);
        return functionDescriptorDoSubstitute;
    }

    /* JADX INFO: compiled from: FunctionInvokeDescriptor.kt */
    public static final class Factory {
        public /* synthetic */ Factory(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Factory() {
        }

        public final FunctionInvokeDescriptor create(FunctionClassDescriptor functionClass, boolean z) {
            Intrinsics.checkNotNullParameter(functionClass, "functionClass");
            List<TypeParameterDescriptor> declaredTypeParameters = functionClass.getDeclaredTypeParameters();
            FunctionInvokeDescriptor functionInvokeDescriptor = new FunctionInvokeDescriptor(functionClass, null, CallableMemberDescriptor.Kind.DECLARATION, z, null);
            ReceiverParameterDescriptor thisAsReceiverParameter = functionClass.getThisAsReceiverParameter();
            List listEmptyList = CollectionsKt.emptyList();
            List listEmptyList2 = CollectionsKt.emptyList();
            ArrayList arrayList = new ArrayList();
            for (Object obj : declaredTypeParameters) {
                if (((TypeParameterDescriptor) obj).getVariance() != Variance.IN_VARIANCE) {
                    break;
                }
                arrayList.add(obj);
            }
            Iterable<IndexedValue> iterableWithIndex = CollectionsKt.withIndex(arrayList);
            ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterableWithIndex, 10));
            for (IndexedValue indexedValue : iterableWithIndex) {
                arrayList2.add(FunctionInvokeDescriptor.Factory.createValueParameter(functionInvokeDescriptor, indexedValue.getIndex(), (TypeParameterDescriptor) indexedValue.getValue()));
            }
            functionInvokeDescriptor.initialize((ReceiverParameterDescriptor) null, thisAsReceiverParameter, listEmptyList, listEmptyList2, (List) arrayList2, (KotlinType) ((TypeParameterDescriptor) CollectionsKt.last((List) declaredTypeParameters)).getDefaultType(), Modality.ABSTRACT, DescriptorVisibilities.PUBLIC);
            functionInvokeDescriptor.setHasSynthesizedParameterNames(true);
            return functionInvokeDescriptor;
        }

        private final ValueParameterDescriptor createValueParameter(FunctionInvokeDescriptor functionInvokeDescriptor, int i, TypeParameterDescriptor typeParameterDescriptor) {
            String lowerCase;
            String strAsString = typeParameterDescriptor.getName().asString();
            Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
            if (Intrinsics.areEqual(strAsString, "T")) {
                lowerCase = "instance";
            } else if (Intrinsics.areEqual(strAsString, "E")) {
                lowerCase = "receiver";
            } else {
                lowerCase = strAsString.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
            }
            Annotations empty = Annotations.Companion.getEMPTY();
            Name nameIdentifier = Name.identifier(lowerCase);
            Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
            SimpleType defaultType = typeParameterDescriptor.getDefaultType();
            Intrinsics.checkNotNullExpressionValue(defaultType, "getDefaultType(...)");
            SourceElement NO_SOURCE = SourceElement.NO_SOURCE;
            Intrinsics.checkNotNullExpressionValue(NO_SOURCE, "NO_SOURCE");
            return new ValueParameterDescriptorImpl(functionInvokeDescriptor, null, i, empty, nameIdentifier, defaultType, false, false, false, null, NO_SOURCE);
        }
    }
}
