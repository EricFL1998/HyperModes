package kotlin.reflect.jvm.internal.impl.metadata.builtins;

import java.io.IOException;
import java.io.InputStream;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.protobuf.ExtensionRegistryLite;

/* JADX INFO: compiled from: readPackageFragment.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ReadPackageFragmentKt {
    public static final Pair<ProtoBuf.PackageFragment, BuiltInsBinaryVersion> readBuiltinsPackageFragment(InputStream inputStream) throws IOException {
        ProtoBuf.PackageFragment from;
        Intrinsics.checkNotNullParameter(inputStream, "<this>");
        InputStream inputStream2 = inputStream;
        try {
            InputStream inputStream3 = inputStream2;
            BuiltInsBinaryVersion from2 = BuiltInsBinaryVersion.Companion.readFrom(inputStream3);
            if (from2.isCompatibleWithCurrentCompilerVersion()) {
                ExtensionRegistryLite extensionRegistryLiteNewInstance = ExtensionRegistryLite.newInstance();
                BuiltInsProtoBuf.registerAllExtensions(extensionRegistryLiteNewInstance);
                from = ProtoBuf.PackageFragment.parseFrom(inputStream3, extensionRegistryLiteNewInstance);
            } else {
                from = null;
            }
            Pair<ProtoBuf.PackageFragment, BuiltInsBinaryVersion> pair = TuplesKt.to(from, from2);
            CloseableKt.closeFinally(inputStream2, null);
            return pair;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                CloseableKt.closeFinally(inputStream2, th);
                throw th2;
            }
        }
    }
}
