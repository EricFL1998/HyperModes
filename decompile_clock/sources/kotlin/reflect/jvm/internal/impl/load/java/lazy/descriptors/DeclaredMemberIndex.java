package kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaField;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaMethod;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaRecordComponent;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: DeclaredMemberIndex.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface DeclaredMemberIndex {
    JavaField findFieldByName(Name name);

    Collection<JavaMethod> findMethodsByName(Name name);

    JavaRecordComponent findRecordComponentByName(Name name);

    Set<Name> getFieldNames();

    Set<Name> getMethodNames();

    Set<Name> getRecordComponentNames();

    /* JADX INFO: compiled from: DeclaredMemberIndex.kt */
    public static final class Empty implements DeclaredMemberIndex {
        public static final Empty INSTANCE = new Empty();

        @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex
        public JavaField findFieldByName(Name name) {
            Intrinsics.checkNotNullParameter(name, "name");
            return null;
        }

        @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex
        public JavaRecordComponent findRecordComponentByName(Name name) {
            Intrinsics.checkNotNullParameter(name, "name");
            return null;
        }

        private Empty() {
        }

        @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex
        public List<JavaMethod> findMethodsByName(Name name) {
            Intrinsics.checkNotNullParameter(name, "name");
            return CollectionsKt.emptyList();
        }

        @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex
        public Set<Name> getMethodNames() {
            return SetsKt.emptySet();
        }

        @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex
        public Set<Name> getFieldNames() {
            return SetsKt.emptySet();
        }

        @Override // kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex
        public Set<Name> getRecordComponentNames() {
            return SetsKt.emptySet();
        }
    }
}
