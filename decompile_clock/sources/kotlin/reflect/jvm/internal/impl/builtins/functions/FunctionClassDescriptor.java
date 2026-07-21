package kotlin.reflect.jvm.internal.impl.builtins.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassKind;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility;
import kotlin.reflect.jvm.internal.impl.descriptors.FindClassInModuleKt;
import kotlin.reflect.jvm.internal.impl.descriptors.Modality;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.SupertypeLoopChecker;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueClassRepresentation;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.AbstractClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.TypeParameterDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;
import kotlin.reflect.jvm.internal.impl.types.AbstractClassTypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeAttributes;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeProjectionImpl;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.utils.addToStdlib.AddToStdlibKt;

/* JADX INFO: compiled from: FunctionClassDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class FunctionClassDescriptor extends AbstractClassDescriptor {
    public static final Companion Companion = new Companion(null);
    private static final ClassId functionClassId;
    private static final ClassId kFunctionClassId;
    private final int arity;
    private final PackageFragmentDescriptor containingDeclaration;
    private final FunctionClassKind functionKind;
    private final FunctionTypeKind functionTypeKind;
    private final FunctionClassScope memberScope;
    private final List<TypeParameterDescriptor> parameters;
    private final StorageManager storageManager;
    private final FunctionTypeConstructor typeConstructor;

    public Void getCompanionObjectDescriptor() {
        return null;
    }

    public Void getUnsubstitutedPrimaryConstructor() {
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public ValueClassRepresentation<SimpleType> getValueClassRepresentation() {
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor
    public boolean isActual() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public boolean isCompanionObject() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public boolean isData() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor
    public boolean isExpect() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor
    public boolean isExternal() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public boolean isFun() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public boolean isInline() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptorWithTypeParameters
    public boolean isInner() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public boolean isValue() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    /* JADX INFO: renamed from: getCompanionObjectDescriptor, reason: collision with other method in class */
    public /* bridge */ /* synthetic */ ClassDescriptor mo1484getCompanionObjectDescriptor() {
        return (ClassDescriptor) getCompanionObjectDescriptor();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    /* JADX INFO: renamed from: getUnsubstitutedPrimaryConstructor, reason: collision with other method in class */
    public /* bridge */ /* synthetic */ ClassConstructorDescriptor mo1485getUnsubstitutedPrimaryConstructor() {
        return (ClassConstructorDescriptor) getUnsubstitutedPrimaryConstructor();
    }

    public final FunctionTypeKind getFunctionTypeKind() {
        return this.functionTypeKind;
    }

    public final int getArity() {
        return this.arity;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public FunctionClassDescriptor(StorageManager storageManager, PackageFragmentDescriptor containingDeclaration, FunctionTypeKind functionTypeKind, int i) {
        super(storageManager, functionTypeKind.numberedClassName(i));
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(containingDeclaration, "containingDeclaration");
        Intrinsics.checkNotNullParameter(functionTypeKind, "functionTypeKind");
        this.storageManager = storageManager;
        this.containingDeclaration = containingDeclaration;
        this.functionTypeKind = functionTypeKind;
        this.arity = i;
        this.typeConstructor = new FunctionTypeConstructor();
        this.memberScope = new FunctionClassScope(storageManager, this);
        ArrayList arrayList = new ArrayList();
        IntRange intRange = new IntRange(1, i);
        ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(intRange, 10));
        Iterator<Integer> it = intRange.iterator();
        while (it.hasNext()) {
            _init_$typeParameter(arrayList, this, Variance.IN_VARIANCE, "P" + ((IntIterator) it).nextInt());
            arrayList2.add(Unit.INSTANCE);
        }
        _init_$typeParameter(arrayList, this, Variance.OUT_VARIANCE, "R");
        this.parameters = CollectionsKt.toList(arrayList);
        this.functionKind = FunctionClassKind.Companion.getFunctionClassKind(this.functionTypeKind);
    }

    private static final void _init_$typeParameter(ArrayList<TypeParameterDescriptor> arrayList, FunctionClassDescriptor functionClassDescriptor, Variance variance, String str) {
        arrayList.add(TypeParameterDescriptorImpl.createWithDefaultBound(functionClassDescriptor, Annotations.Companion.getEMPTY(), false, variance, Name.identifier(str), arrayList.size(), functionClassDescriptor.storageManager));
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorNonRoot, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public PackageFragmentDescriptor getContainingDeclaration() {
        return this.containingDeclaration;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public MemberScope.Empty getStaticScope() {
        return MemberScope.Empty.INSTANCE;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor
    public TypeConstructor getTypeConstructor() {
        return this.typeConstructor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleAwareClassDescriptor
    public FunctionClassScope getUnsubstitutedMemberScope(KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        return this.memberScope;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public List<ClassConstructorDescriptor> getConstructors() {
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public ClassKind getKind() {
        return ClassKind.INTERFACE;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor
    public Modality getModality() {
        return Modality.ABSTRACT;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.MemberDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorWithVisibility
    public DescriptorVisibility getVisibility() {
        DescriptorVisibility PUBLIC = DescriptorVisibilities.PUBLIC;
        Intrinsics.checkNotNullExpressionValue(PUBLIC, "PUBLIC");
        return PUBLIC;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated
    public Annotations getAnnotations() {
        return Annotations.Companion.getEMPTY();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorWithSource
    public SourceElement getSource() {
        SourceElement NO_SOURCE = SourceElement.NO_SOURCE;
        Intrinsics.checkNotNullExpressionValue(NO_SOURCE, "NO_SOURCE");
        return NO_SOURCE;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
    public List<ClassDescriptor> getSealedSubclasses() {
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptorWithTypeParameters
    public List<TypeParameterDescriptor> getDeclaredTypeParameters() {
        return this.parameters;
    }

    /* JADX INFO: compiled from: FunctionClassDescriptor.kt */
    private final class FunctionTypeConstructor extends AbstractClassTypeConstructor {
        @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
        public boolean isDenotable() {
            return true;
        }

        public FunctionTypeConstructor() {
            super(FunctionClassDescriptor.this.storageManager);
        }

        @Override // kotlin.reflect.jvm.internal.impl.types.AbstractTypeConstructor
        protected Collection<KotlinType> computeSupertypes() {
            List listListOf;
            FunctionTypeKind functionTypeKind = FunctionClassDescriptor.this.getFunctionTypeKind();
            if (Intrinsics.areEqual(functionTypeKind, FunctionTypeKind.Function.INSTANCE)) {
                listListOf = CollectionsKt.listOf(FunctionClassDescriptor.functionClassId);
            } else if (Intrinsics.areEqual(functionTypeKind, FunctionTypeKind.KFunction.INSTANCE)) {
                listListOf = CollectionsKt.listOf((Object[]) new ClassId[]{FunctionClassDescriptor.kFunctionClassId, new ClassId(StandardNames.BUILT_INS_PACKAGE_FQ_NAME, FunctionTypeKind.Function.INSTANCE.numberedClassName(FunctionClassDescriptor.this.getArity()))});
            } else if (Intrinsics.areEqual(functionTypeKind, FunctionTypeKind.SuspendFunction.INSTANCE)) {
                listListOf = CollectionsKt.listOf(FunctionClassDescriptor.functionClassId);
            } else if (Intrinsics.areEqual(functionTypeKind, FunctionTypeKind.KSuspendFunction.INSTANCE)) {
                listListOf = CollectionsKt.listOf((Object[]) new ClassId[]{FunctionClassDescriptor.kFunctionClassId, new ClassId(StandardNames.COROUTINES_PACKAGE_FQ_NAME, FunctionTypeKind.SuspendFunction.INSTANCE.numberedClassName(FunctionClassDescriptor.this.getArity()))});
            } else {
                AddToStdlibKt.shouldNotBeCalled$default(null, 1, null);
                throw null;
            }
            ModuleDescriptor containingDeclaration = FunctionClassDescriptor.this.containingDeclaration.getContainingDeclaration();
            List<ClassId> list = listListOf;
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
            for (ClassId classId : list) {
                ClassDescriptor classDescriptorFindClassAcrossModuleDependencies = FindClassInModuleKt.findClassAcrossModuleDependencies(containingDeclaration, classId);
                if (classDescriptorFindClassAcrossModuleDependencies == null) {
                    throw new IllegalStateException(("Built-in class " + classId + " not found").toString());
                }
                List listTakeLast = CollectionsKt.takeLast(getParameters(), classDescriptorFindClassAcrossModuleDependencies.getTypeConstructor().getParameters().size());
                ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(listTakeLast, 10));
                Iterator it = listTakeLast.iterator();
                while (it.hasNext()) {
                    arrayList2.add(new TypeProjectionImpl(((TypeParameterDescriptor) it.next()).getDefaultType()));
                }
                arrayList.add(KotlinTypeFactory.simpleNotNullType(TypeAttributes.Companion.getEmpty(), classDescriptorFindClassAcrossModuleDependencies, arrayList2));
            }
            return CollectionsKt.toList(arrayList);
        }

        @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
        public List<TypeParameterDescriptor> getParameters() {
            return FunctionClassDescriptor.this.parameters;
        }

        @Override // kotlin.reflect.jvm.internal.impl.types.AbstractClassTypeConstructor, kotlin.reflect.jvm.internal.impl.types.ClassifierBasedTypeConstructor, kotlin.reflect.jvm.internal.impl.types.TypeConstructor
        /* JADX INFO: renamed from: getDeclarationDescriptor */
        public FunctionClassDescriptor mo1491getDeclarationDescriptor() {
            return FunctionClassDescriptor.this;
        }

        public String toString() {
            return mo1491getDeclarationDescriptor().toString();
        }

        @Override // kotlin.reflect.jvm.internal.impl.types.AbstractTypeConstructor
        protected SupertypeLoopChecker getSupertypeLoopChecker() {
            return SupertypeLoopChecker.EMPTY.INSTANCE;
        }
    }

    public String toString() {
        String strAsString = getName().asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        return strAsString;
    }

    /* JADX INFO: compiled from: FunctionClassDescriptor.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    static {
        FqName fqName = StandardNames.BUILT_INS_PACKAGE_FQ_NAME;
        Name nameIdentifier = Name.identifier("Function");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        functionClassId = new ClassId(fqName, nameIdentifier);
        FqName fqName2 = StandardNames.KOTLIN_REFLECT_FQ_NAME;
        Name nameIdentifier2 = Name.identifier("KFunction");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier2, "identifier(...)");
        kFunctionClassId = new ClassId(fqName2, nameIdentifier2);
    }
}
