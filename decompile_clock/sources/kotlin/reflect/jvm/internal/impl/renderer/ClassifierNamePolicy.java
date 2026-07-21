package kotlin.reflect.jvm.internal.impl.renderer;

import java.util.ArrayList;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils;

/* JADX INFO: compiled from: ClassifierNamePolicy.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface ClassifierNamePolicy {
    String renderClassifier(ClassifierDescriptor classifierDescriptor, DescriptorRenderer descriptorRenderer);

    /* JADX INFO: compiled from: ClassifierNamePolicy.kt */
    public static final class SHORT implements ClassifierNamePolicy {
        public static final SHORT INSTANCE = new SHORT();

        private SHORT() {
        }

        @Override // kotlin.reflect.jvm.internal.impl.renderer.ClassifierNamePolicy
        public String renderClassifier(ClassifierDescriptor classifier, DescriptorRenderer renderer) {
            Intrinsics.checkNotNullParameter(classifier, "classifier");
            Intrinsics.checkNotNullParameter(renderer, "renderer");
            if (classifier instanceof TypeParameterDescriptor) {
                Name name = ((TypeParameterDescriptor) classifier).getName();
                Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                return renderer.renderName(name, false);
            }
            ArrayList arrayList = new ArrayList();
            ClassifierDescriptor containingDeclaration = classifier;
            do {
                arrayList.add(containingDeclaration.getName());
                containingDeclaration = containingDeclaration.getContainingDeclaration();
            } while (containingDeclaration instanceof ClassDescriptor);
            return RenderingUtilsKt.renderFqName(CollectionsKt.asReversedMutable(arrayList));
        }
    }

    /* JADX INFO: compiled from: ClassifierNamePolicy.kt */
    public static final class FULLY_QUALIFIED implements ClassifierNamePolicy {
        public static final FULLY_QUALIFIED INSTANCE = new FULLY_QUALIFIED();

        private FULLY_QUALIFIED() {
        }

        @Override // kotlin.reflect.jvm.internal.impl.renderer.ClassifierNamePolicy
        public String renderClassifier(ClassifierDescriptor classifier, DescriptorRenderer renderer) {
            Intrinsics.checkNotNullParameter(classifier, "classifier");
            Intrinsics.checkNotNullParameter(renderer, "renderer");
            if (classifier instanceof TypeParameterDescriptor) {
                Name name = ((TypeParameterDescriptor) classifier).getName();
                Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                return renderer.renderName(name, false);
            }
            FqNameUnsafe fqName = DescriptorUtils.getFqName(classifier);
            Intrinsics.checkNotNullExpressionValue(fqName, "getFqName(...)");
            return renderer.renderFqName(fqName);
        }
    }

    /* JADX INFO: compiled from: ClassifierNamePolicy.kt */
    public static final class SOURCE_CODE_QUALIFIED implements ClassifierNamePolicy {
        public static final SOURCE_CODE_QUALIFIED INSTANCE = new SOURCE_CODE_QUALIFIED();

        private SOURCE_CODE_QUALIFIED() {
        }

        @Override // kotlin.reflect.jvm.internal.impl.renderer.ClassifierNamePolicy
        public String renderClassifier(ClassifierDescriptor classifier, DescriptorRenderer renderer) {
            Intrinsics.checkNotNullParameter(classifier, "classifier");
            Intrinsics.checkNotNullParameter(renderer, "renderer");
            return qualifiedNameForSourceCode(classifier);
        }

        private final String qualifiedNameForSourceCode(ClassifierDescriptor classifierDescriptor) {
            Name name = classifierDescriptor.getName();
            Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
            String strRender = RenderingUtilsKt.render(name);
            if (classifierDescriptor instanceof TypeParameterDescriptor) {
                return strRender;
            }
            DeclarationDescriptor containingDeclaration = classifierDescriptor.getContainingDeclaration();
            Intrinsics.checkNotNullExpressionValue(containingDeclaration, "getContainingDeclaration(...)");
            String strQualifierName = qualifierName(containingDeclaration);
            return (strQualifierName == null || Intrinsics.areEqual(strQualifierName, "")) ? strRender : strQualifierName + '.' + strRender;
        }

        private final String qualifierName(DeclarationDescriptor declarationDescriptor) {
            if (declarationDescriptor instanceof ClassDescriptor) {
                return qualifiedNameForSourceCode((ClassifierDescriptor) declarationDescriptor);
            }
            if (!(declarationDescriptor instanceof PackageFragmentDescriptor)) {
                return null;
            }
            FqNameUnsafe unsafe = ((PackageFragmentDescriptor) declarationDescriptor).getFqName().toUnsafe();
            Intrinsics.checkNotNullExpressionValue(unsafe, "toUnsafe(...)");
            return RenderingUtilsKt.render(unsafe);
        }
    }
}
