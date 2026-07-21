package kotlin.reflect.jvm.internal.impl.load.java.lazy.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRendererOptions;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.types.FlexibleType;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.RawType;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeAttributes;
import kotlin.reflect.jvm.internal.impl.types.TypeParameterUpperBoundEraser;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeChecker;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;
import kotlin.text.StringsKt;
import kotlin.text.Typography;

/* JADX INFO: compiled from: RawType.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class RawTypeImpl extends FlexibleType implements RawType {
    private RawTypeImpl(SimpleType simpleType, SimpleType simpleType2, boolean z) {
        super(simpleType, simpleType2);
        if (z) {
            return;
        }
        KotlinTypeChecker.DEFAULT.isSubtypeOf(simpleType, simpleType2);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public RawTypeImpl(SimpleType lowerBound, SimpleType upperBound) {
        this(lowerBound, upperBound, false);
        Intrinsics.checkNotNullParameter(lowerBound, "lowerBound");
        Intrinsics.checkNotNullParameter(upperBound, "upperBound");
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public SimpleType getDelegate() {
        return getLowerBound();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public MemberScope getMemberScope() {
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = getConstructor().mo1491getDeclarationDescriptor();
        TypeParameterUpperBoundEraser typeParameterUpperBoundEraser = null;
        byte b = 0;
        ClassDescriptor classDescriptor = classifierDescriptorMo1491getDeclarationDescriptor instanceof ClassDescriptor ? (ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor : null;
        if (classDescriptor == null) {
            throw new IllegalStateException(("Incorrect classifier: " + getConstructor().mo1491getDeclarationDescriptor()).toString());
        }
        MemberScope memberScope = classDescriptor.getMemberScope(new RawSubstitution(typeParameterUpperBoundEraser, 1, b == true ? 1 : 0));
        Intrinsics.checkNotNullExpressionValue(memberScope, "getMemberScope(...)");
        return memberScope;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public RawTypeImpl replaceAttributes(TypeAttributes newAttributes) {
        Intrinsics.checkNotNullParameter(newAttributes, "newAttributes");
        return new RawTypeImpl(getLowerBound().replaceAttributes(newAttributes), getUpperBound().replaceAttributes(newAttributes));
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public RawTypeImpl makeNullableAsSpecified(boolean z) {
        return new RawTypeImpl(getLowerBound().makeNullableAsSpecified(z), getUpperBound().makeNullableAsSpecified(z));
    }

    private static final boolean render$onlyOutDiffers(String str, String str2) {
        return Intrinsics.areEqual(str, StringsKt.removePrefix(str2, (CharSequence) "out ")) || Intrinsics.areEqual(str2, "*");
    }

    private static final List<String> render$renderArguments(DescriptorRenderer descriptorRenderer, KotlinType kotlinType) {
        List<TypeProjection> arguments = kotlinType.getArguments();
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(arguments, 10));
        Iterator<T> it = arguments.iterator();
        while (it.hasNext()) {
            arrayList.add(descriptorRenderer.renderTypeProjection((TypeProjection) it.next()));
        }
        return arrayList;
    }

    private static final String render$replaceArgs(String str, String str2) {
        return !StringsKt.contains$default((CharSequence) str, Typography.less, false, 2, (Object) null) ? str : StringsKt.substringBefore$default(str, Typography.less, (String) null, 2, (Object) null) + Typography.less + str2 + Typography.greater + StringsKt.substringAfterLast$default(str, Typography.greater, (String) null, 2, (Object) null);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public String render(DescriptorRenderer renderer, DescriptorRendererOptions options) {
        Pair pair;
        Intrinsics.checkNotNullParameter(renderer, "renderer");
        Intrinsics.checkNotNullParameter(options, "options");
        String strRenderType = renderer.renderType(getLowerBound());
        String strRenderType2 = renderer.renderType(getUpperBound());
        if (options.getDebugMode()) {
            return "raw (" + strRenderType + ".." + strRenderType2 + ')';
        }
        if (getUpperBound().getArguments().isEmpty()) {
            return renderer.renderFlexibleType(strRenderType, strRenderType2, TypeUtilsKt.getBuiltIns(this));
        }
        List<String> listRender$renderArguments = render$renderArguments(renderer, getLowerBound());
        List<String> listRender$renderArguments2 = render$renderArguments(renderer, getUpperBound());
        List<String> list = listRender$renderArguments;
        String strJoinToString$default = CollectionsKt.joinToString$default(list, ", ", null, null, 0, null, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.types.RawTypeImpl$$Lambda$0
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return RawTypeImpl.render$lambda$2((String) obj);
            }
        }, 30, null);
        List listZip = CollectionsKt.zip(list, listRender$renderArguments2);
        if ((listZip instanceof Collection) && listZip.isEmpty()) {
            strRenderType2 = render$replaceArgs(strRenderType2, strJoinToString$default);
            break;
        }
        Iterator it = listZip.iterator();
        do {
            if (!it.hasNext()) {
                strRenderType2 = render$replaceArgs(strRenderType2, strJoinToString$default);
                break;
            }
            pair = (Pair) it.next();
        } while (render$onlyOutDiffers((String) pair.getFirst(), (String) pair.getSecond()));
        String strRender$replaceArgs = render$replaceArgs(strRenderType, strJoinToString$default);
        return Intrinsics.areEqual(strRender$replaceArgs, strRenderType2) ? strRender$replaceArgs : renderer.renderFlexibleType(strRender$replaceArgs, strRenderType2, TypeUtilsKt.getBuiltIns(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence render$lambda$2(String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return "(raw) " + it;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public FlexibleType refine(KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        KotlinType kotlinTypeRefineType = kotlinTypeRefiner.refineType((KotlinTypeMarker) getLowerBound());
        Intrinsics.checkNotNull(kotlinTypeRefineType, "null cannot be cast to non-null type org.jetbrains.kotlin.types.SimpleType");
        KotlinType kotlinTypeRefineType2 = kotlinTypeRefiner.refineType((KotlinTypeMarker) getUpperBound());
        Intrinsics.checkNotNull(kotlinTypeRefineType2, "null cannot be cast to non-null type org.jetbrains.kotlin.types.SimpleType");
        return new RawTypeImpl((SimpleType) kotlinTypeRefineType, (SimpleType) kotlinTypeRefineType2, true);
    }
}
