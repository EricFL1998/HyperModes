package kotlin.reflect.jvm.internal.impl.serialization.deserialization.builtins;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: BuiltInsResourceLoader.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class BuiltInsResourceLoader {
    public final InputStream loadResource(String path) throws IOException {
        Intrinsics.checkNotNullParameter(path, "path");
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader == null) {
            return ClassLoader.getSystemResourceAsStream(path);
        }
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            return null;
        }
        URLConnection uRLConnectionOpenConnection = resource.openConnection();
        uRLConnectionOpenConnection.setUseCaches(false);
        return uRLConnectionOpenConnection.getInputStream();
    }
}
