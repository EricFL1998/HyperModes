package kotlin.reflect.jvm.internal.impl.load.java.structure;

import java.util.List;

/* JADX INFO: compiled from: javaElements.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface JavaConstructor extends JavaMember, JavaTypeParameterListOwner {
    List<JavaValueParameter> getValueParameters();
}
