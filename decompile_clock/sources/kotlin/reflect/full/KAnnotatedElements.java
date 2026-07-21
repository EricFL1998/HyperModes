package kotlin.reflect.full;

import com.xiaomi.onetrack.api.g;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KAnnotatedElement;
import kotlin.reflect.KClass;

/* JADX INFO: compiled from: KAnnotatedElements.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\b\u0002\n\u0002\u0010\u001b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\u001a \u0010\u0000\u001a\u0004\u0018\u0001H\u0001\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0087\b¢\u0006\u0002\u0010\u0004\u001a\u0019\u0010\u0005\u001a\u00020\u0006\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0087\b\u001a\u001f\u0010\u0007\u001a\b\u0012\u0004\u0012\u0002H\u00010\b\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0087\b\u001a*\u0010\u0007\u001a\b\u0012\u0004\u0012\u0002H\u00010\b\"\b\b\u0000\u0010\u0001*\u00020\u0002*\u00020\u00032\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\u00010\nH\u0007¨\u0006\u000b"}, d2 = {"findAnnotation", "T", "", "Lkotlin/reflect/KAnnotatedElement;", "(Lkotlin/reflect/KAnnotatedElement;)Ljava/lang/annotation/Annotation;", "hasAnnotation", "", "findAnnotations", "", "klass", "Lkotlin/reflect/KClass;", "kotlin-reflection"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class KAnnotatedElements {
    public static final /* synthetic */ <T extends Annotation> T findAnnotation(KAnnotatedElement kAnnotatedElement) {
        Intrinsics.checkNotNullParameter(kAnnotatedElement, "<this>");
        for (Object obj : kAnnotatedElement.getAnnotations()) {
            Intrinsics.reifiedOperationMarker(3, "T");
            if (((Annotation) obj) instanceof Annotation) {
                Intrinsics.reifiedOperationMarker(1, "T?");
                return (T) obj;
            }
        }
        obj = null;
        Intrinsics.reifiedOperationMarker(1, "T?");
        return (T) obj;
    }

    public static final /* synthetic */ <T extends Annotation> List<T> findAnnotations(KAnnotatedElement kAnnotatedElement) {
        Intrinsics.checkNotNullParameter(kAnnotatedElement, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        return findAnnotations(kAnnotatedElement, Reflection.getOrCreateKotlinClass(Annotation.class));
    }

    public static final <T extends Annotation> List<T> findAnnotations(KAnnotatedElement kAnnotatedElement, KClass<T> klass) throws IllegalAccessException, InvocationTargetException {
        Object next;
        Intrinsics.checkNotNullParameter(kAnnotatedElement, "<this>");
        Intrinsics.checkNotNullParameter(klass, "klass");
        List<T> listFilterIsInstance = CollectionsKt.filterIsInstance(kAnnotatedElement.getAnnotations(), JvmClassMappingKt.getJavaClass((KClass) klass));
        if (!listFilterIsInstance.isEmpty()) {
            return listFilterIsInstance;
        }
        Class<? extends Annotation> clsLoadRepeatableContainer = Java8RepeatableContainerLoader.INSTANCE.loadRepeatableContainer(JvmClassMappingKt.getJavaClass((KClass) klass));
        if (clsLoadRepeatableContainer != null) {
            Iterator<T> it = kAnnotatedElement.getAnnotations().iterator();
            do {
                if (!it.hasNext()) {
                    next = null;
                    break;
                }
                next = it.next();
            } while (!Intrinsics.areEqual(JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass((Annotation) next)), clsLoadRepeatableContainer));
            Annotation annotation = (Annotation) next;
            if (annotation != null) {
                Object objInvoke = annotation.getClass().getMethod(g.p, new Class[0]).invoke(annotation, new Object[0]);
                Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.Array<T of kotlin.reflect.full.KAnnotatedElements.findAnnotations>");
                return ArraysKt.asList((Annotation[]) objInvoke);
            }
        }
        return CollectionsKt.emptyList();
    }

    public static final /* synthetic */ <T extends Annotation> boolean hasAnnotation(KAnnotatedElement kAnnotatedElement) {
        Object next;
        Intrinsics.checkNotNullParameter(kAnnotatedElement, "<this>");
        Iterator<T> it = kAnnotatedElement.getAnnotations().iterator();
        do {
            if (!it.hasNext()) {
                next = null;
                break;
            }
            next = it.next();
            Intrinsics.reifiedOperationMarker(3, "T");
        } while (!(((Annotation) next) instanceof Annotation));
        Intrinsics.reifiedOperationMarker(1, "T?");
        return ((Annotation) next) != null;
    }
}
