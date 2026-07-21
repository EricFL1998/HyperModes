package kotlin.reflect.jvm.internal;

import java.util.Collections;
import java.util.List;
import kotlin.jvm.internal.CallableReference;
import kotlin.jvm.internal.ClassBasedDeclarationContainer;
import kotlin.jvm.internal.FunctionBase;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.MutablePropertyReference0;
import kotlin.jvm.internal.MutablePropertyReference1;
import kotlin.jvm.internal.MutablePropertyReference2;
import kotlin.jvm.internal.PropertyReference0;
import kotlin.jvm.internal.PropertyReference1;
import kotlin.jvm.internal.PropertyReference2;
import kotlin.jvm.internal.ReflectionFactory;
import kotlin.reflect.KCallable;
import kotlin.reflect.KClass;
import kotlin.reflect.KClassifier;
import kotlin.reflect.KDeclarationContainer;
import kotlin.reflect.KFunction;
import kotlin.reflect.KMutableProperty0;
import kotlin.reflect.KMutableProperty1;
import kotlin.reflect.KMutableProperty2;
import kotlin.reflect.KProperty0;
import kotlin.reflect.KProperty1;
import kotlin.reflect.KProperty2;
import kotlin.reflect.KType;
import kotlin.reflect.KTypeParameter;
import kotlin.reflect.KTypeProjection;
import kotlin.reflect.KVariance;
import kotlin.reflect.full.KClassifiers;
import kotlin.reflect.jvm.ReflectLambdaKt;

/* JADX INFO: loaded from: classes2.dex */
public class ReflectionFactoryImpl extends ReflectionFactory {
    @Override // kotlin.jvm.internal.ReflectionFactory
    public void setUpperBounds(KTypeParameter kTypeParameter, List<KType> list) {
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KClass createKotlinClass(Class cls) {
        return new KClassImpl(cls);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KClass createKotlinClass(Class cls, String str) {
        return new KClassImpl(cls);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KDeclarationContainer getOrCreateKotlinPackage(Class cls, String str) {
        return CachesKt.getOrCreateKotlinPackage(cls);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KClass getOrCreateKotlinClass(Class cls) {
        return CachesKt.getOrCreateKotlinClass(cls);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KClass getOrCreateKotlinClass(Class cls, String str) {
        return CachesKt.getOrCreateKotlinClass(cls);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public String renderLambdaToString(Lambda lambda) {
        return renderLambdaToString((FunctionBase) lambda);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public String renderLambdaToString(FunctionBase functionBase) {
        KFunctionImpl kFunctionImplAsKFunctionImpl;
        KFunction kFunctionReflect = ReflectLambdaKt.reflect(functionBase);
        if (kFunctionReflect != null && (kFunctionImplAsKFunctionImpl = UtilKt.asKFunctionImpl(kFunctionReflect)) != null) {
            return ReflectionObjectRenderer.INSTANCE.renderLambda(kFunctionImplAsKFunctionImpl.getDescriptor());
        }
        return super.renderLambdaToString(functionBase);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KFunction function(FunctionReference functionReference) {
        return new KFunctionImpl(getOwner(functionReference), functionReference.getName(), functionReference.getSignature(), functionReference.getBoundReceiver());
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KProperty0 property0(PropertyReference0 propertyReference0) {
        return new KProperty0Impl(getOwner(propertyReference0), propertyReference0.getName(), propertyReference0.getSignature(), propertyReference0.getBoundReceiver());
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KMutableProperty0 mutableProperty0(MutablePropertyReference0 mutablePropertyReference0) {
        return new KMutableProperty0Impl(getOwner(mutablePropertyReference0), mutablePropertyReference0.getName(), mutablePropertyReference0.getSignature(), mutablePropertyReference0.getBoundReceiver());
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KProperty1 property1(PropertyReference1 propertyReference1) {
        return new KProperty1Impl(getOwner(propertyReference1), propertyReference1.getName(), propertyReference1.getSignature(), propertyReference1.getBoundReceiver());
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KMutableProperty1 mutableProperty1(MutablePropertyReference1 mutablePropertyReference1) {
        return new KMutableProperty1Impl(getOwner(mutablePropertyReference1), mutablePropertyReference1.getName(), mutablePropertyReference1.getSignature(), mutablePropertyReference1.getBoundReceiver());
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KProperty2 property2(PropertyReference2 propertyReference2) {
        return new KProperty2Impl(getOwner(propertyReference2), propertyReference2.getName(), propertyReference2.getSignature());
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KMutableProperty2 mutableProperty2(MutablePropertyReference2 mutablePropertyReference2) {
        return new KMutableProperty2Impl(getOwner(mutablePropertyReference2), mutablePropertyReference2.getName(), mutablePropertyReference2.getSignature());
    }

    private static KDeclarationContainerImpl getOwner(CallableReference callableReference) {
        KDeclarationContainer owner = callableReference.getOwner();
        return owner instanceof KDeclarationContainerImpl ? (KDeclarationContainerImpl) owner : EmptyContainerForLocal.INSTANCE;
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KType typeOf(KClassifier kClassifier, List<KTypeProjection> list, boolean z) {
        if (kClassifier instanceof ClassBasedDeclarationContainer) {
            return CachesKt.getOrCreateKType(((ClassBasedDeclarationContainer) kClassifier).getJClass(), list, z);
        }
        return KClassifiers.createType(kClassifier, list, z, Collections.emptyList());
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KTypeParameter typeParameter(Object obj, String str, KVariance kVariance, boolean z) {
        List<KTypeParameter> typeParameters;
        if (obj instanceof KClass) {
            typeParameters = ((KClass) obj).getTypeParameters();
        } else if (obj instanceof KCallable) {
            typeParameters = ((KCallable) obj).getTypeParameters();
        } else {
            throw new IllegalArgumentException("Type parameter container must be a class or a callable: " + obj);
        }
        for (KTypeParameter kTypeParameter : typeParameters) {
            if (kTypeParameter.getName().equals(str)) {
                return kTypeParameter;
            }
        }
        throw new IllegalArgumentException("Type parameter " + str + " is not found in container: " + obj);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KType platformType(KType kType, KType kType2) {
        return TypeOfImplKt.createPlatformKType(kType, kType2);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KType mutableCollectionType(KType kType) {
        return TypeOfImplKt.createMutableCollectionKType(kType);
    }

    @Override // kotlin.jvm.internal.ReflectionFactory
    public KType nothingType(KType kType) {
        return TypeOfImplKt.createNothingType(kType);
    }

    public static void clearCaches() {
        CachesKt.clearCaches();
        ModuleByClassLoaderKt.clearModuleByClassLoaderCache();
    }
}
