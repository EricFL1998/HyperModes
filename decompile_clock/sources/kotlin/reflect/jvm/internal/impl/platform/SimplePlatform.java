package kotlin.reflect.jvm.internal.impl.platform;

/* JADX INFO: compiled from: TargetPlatform.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class SimplePlatform {
    private final String platformName;
    private final TargetPlatformVersion targetPlatformVersion;

    public String toString() {
        String targetName = getTargetName();
        return targetName.length() > 0 ? this.platformName + " (" + targetName + ')' : this.platformName;
    }

    public String getTargetName() {
        return getTargetPlatformVersion().getDescription();
    }

    public TargetPlatformVersion getTargetPlatformVersion() {
        return this.targetPlatformVersion;
    }
}
