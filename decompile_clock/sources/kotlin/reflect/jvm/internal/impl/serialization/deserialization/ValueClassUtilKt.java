package kotlin.reflect.jvm.internal.impl.serialization.deserialization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.InlineClassRepresentation;
import kotlin.reflect.jvm.internal.impl.descriptors.MultiFieldValueClassRepresentation;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueClassRepresentation;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.NameResolver;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.ProtoTypeTableUtilKt;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.TypeTable;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.types.model.SimpleTypeMarker;

/* JADX INFO: compiled from: ValueClassUtil.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ValueClassUtilKt {
    /* JADX WARN: Multi-variable type inference failed */
    public static final <T extends SimpleTypeMarker> ValueClassRepresentation<T> loadValueClassRepresentation(ProtoBuf.Class r5, NameResolver nameResolver, TypeTable typeTable, Function1<? super ProtoBuf.Type, ? extends T> typeDeserializer, Function1<? super Name, ? extends T> typeOfPublicProperty) {
        T tInvoke;
        ArrayList multiFieldValueClassUnderlyingTypeList;
        Intrinsics.checkNotNullParameter(r5, "<this>");
        Intrinsics.checkNotNullParameter(nameResolver, "nameResolver");
        Intrinsics.checkNotNullParameter(typeTable, "typeTable");
        Intrinsics.checkNotNullParameter(typeDeserializer, "typeDeserializer");
        Intrinsics.checkNotNullParameter(typeOfPublicProperty, "typeOfPublicProperty");
        if (r5.getMultiFieldValueClassUnderlyingNameCount() > 0) {
            List<Integer> multiFieldValueClassUnderlyingNameList = r5.getMultiFieldValueClassUnderlyingNameList();
            Intrinsics.checkNotNullExpressionValue(multiFieldValueClassUnderlyingNameList, "getMultiFieldValueClassUnderlyingNameList(...)");
            List<Integer> list = multiFieldValueClassUnderlyingNameList;
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
            for (Integer num : list) {
                Intrinsics.checkNotNull(num);
                arrayList.add(NameResolverUtilKt.getName(nameResolver, num.intValue()));
            }
            ArrayList arrayList2 = arrayList;
            Pair pair = TuplesKt.to(Integer.valueOf(r5.getMultiFieldValueClassUnderlyingTypeIdCount()), Integer.valueOf(r5.getMultiFieldValueClassUnderlyingTypeCount()));
            if (!Intrinsics.areEqual(pair, TuplesKt.to(Integer.valueOf(arrayList2.size()), 0))) {
                if (!Intrinsics.areEqual(pair, TuplesKt.to(0, Integer.valueOf(arrayList2.size())))) {
                    throw new IllegalStateException(("class " + NameResolverUtilKt.getName(nameResolver, r5.getFqName()) + " has illegal multi-field value class representation").toString());
                }
                multiFieldValueClassUnderlyingTypeList = r5.getMultiFieldValueClassUnderlyingTypeList();
            } else {
                List<Integer> multiFieldValueClassUnderlyingTypeIdList = r5.getMultiFieldValueClassUnderlyingTypeIdList();
                Intrinsics.checkNotNullExpressionValue(multiFieldValueClassUnderlyingTypeIdList, "getMultiFieldValueClassUnderlyingTypeIdList(...)");
                List<Integer> list2 = multiFieldValueClassUnderlyingTypeIdList;
                ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
                for (Integer num2 : list2) {
                    Intrinsics.checkNotNull(num2);
                    arrayList3.add(typeTable.get(num2.intValue()));
                }
                multiFieldValueClassUnderlyingTypeList = arrayList3;
            }
            Intrinsics.checkNotNull(multiFieldValueClassUnderlyingTypeList);
            List<ProtoBuf.Type> list3 = multiFieldValueClassUnderlyingTypeList;
            ArrayList arrayList4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list3, 10));
            Iterator<T> it = list3.iterator();
            while (it.hasNext()) {
                arrayList4.add(typeDeserializer.invoke(it.next()));
            }
            return new MultiFieldValueClassRepresentation(CollectionsKt.zip(arrayList2, arrayList4));
        }
        if (!r5.hasInlineClassUnderlyingPropertyName()) {
            return null;
        }
        Name name = NameResolverUtilKt.getName(nameResolver, r5.getInlineClassUnderlyingPropertyName());
        ProtoBuf.Type typeInlineClassUnderlyingType = ProtoTypeTableUtilKt.inlineClassUnderlyingType(r5, typeTable);
        if ((typeInlineClassUnderlyingType == null || (tInvoke = typeDeserializer.invoke(typeInlineClassUnderlyingType)) == null) && (tInvoke = typeOfPublicProperty.invoke(name)) == null) {
            throw new IllegalStateException(("cannot determine underlying type for value class " + NameResolverUtilKt.getName(nameResolver, r5.getFqName()) + " with property " + name).toString());
        }
        return new InlineClassRepresentation(name, tInvoke);
    }
}
