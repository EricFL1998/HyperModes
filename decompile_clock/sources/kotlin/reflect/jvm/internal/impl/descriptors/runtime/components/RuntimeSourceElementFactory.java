package kotlin.reflect.jvm.internal.impl.descriptors.runtime.components;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceFile;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaElement;
import kotlin.reflect.jvm.internal.impl.load.java.sources.JavaSourceElement;
import kotlin.reflect.jvm.internal.impl.load.java.sources.JavaSourceElementFactory;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaElement;

/* JADX INFO: compiled from: RuntimeSourceElementFactory.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class RuntimeSourceElementFactory implements JavaSourceElementFactory {
    public static final RuntimeSourceElementFactory INSTANCE = new RuntimeSourceElementFactory();

    /* JADX INFO: compiled from: RuntimeSourceElementFactory.kt */
    public static final class RuntimeSourceElement implements JavaSourceElement {
        private final ReflectJavaElement javaElement;

        public RuntimeSourceElement(ReflectJavaElement javaElement) {
            Intrinsics.checkNotNullParameter(javaElement, "javaElement");
            this.javaElement = javaElement;
        }

        @Override // kotlin.reflect.jvm.internal.impl.load.java.sources.JavaSourceElement
        public ReflectJavaElement getJavaElement() {
            return this.javaElement;
        }

        public String toString() {
            return getClass().getName() + ": " + getJavaElement();
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.SourceElement
        public SourceFile getContainingFile() {
            SourceFile NO_SOURCE_FILE = SourceFile.NO_SOURCE_FILE;
            Intrinsics.checkNotNullExpressionValue(NO_SOURCE_FILE, "NO_SOURCE_FILE");
            return NO_SOURCE_FILE;
        }
    }

    private RuntimeSourceElementFactory() {
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.sources.JavaSourceElementFactory
    public JavaSourceElement source(JavaElement javaElement) {
        Intrinsics.checkNotNullParameter(javaElement, "javaElement");
        return new RuntimeSourceElement((ReflectJavaElement) javaElement);
    }
}
