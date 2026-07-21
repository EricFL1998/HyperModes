package kotlin.reflect.jvm.internal.impl.descriptors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.types.model.SimpleTypeMarker;

/* JADX INFO: compiled from: ValueClassRepresentation.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class ValueClassRepresentation<Type extends SimpleTypeMarker> {
    public /* synthetic */ ValueClassRepresentation(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    public abstract boolean containsPropertyWithName(Name name);

    private ValueClassRepresentation() {
    }

    public final <Other extends SimpleTypeMarker> ValueClassRepresentation<Other> mapUnderlyingType(Function1<? super Type, ? extends Other> transform) {
        Intrinsics.checkNotNullParameter(transform, "transform");
        if (this instanceof InlineClassRepresentation) {
            InlineClassRepresentation inlineClassRepresentation = (InlineClassRepresentation) this;
            return new InlineClassRepresentation(inlineClassRepresentation.getUnderlyingPropertyName(), transform.invoke(inlineClassRepresentation.getUnderlyingType()));
        }
        if (!(this instanceof MultiFieldValueClassRepresentation)) {
            throw new NoWhenBranchMatchedException();
        }
        List<Pair<Name, Type>> underlyingPropertyNamesToTypes = ((MultiFieldValueClassRepresentation) this).getUnderlyingPropertyNamesToTypes();
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(underlyingPropertyNamesToTypes, 10));
        Iterator<T> it = underlyingPropertyNamesToTypes.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            arrayList.add(TuplesKt.to((Name) pair.component1(), transform.invoke((SimpleTypeMarker) pair.component2())));
        }
        return new MultiFieldValueClassRepresentation(arrayList);
    }
}
