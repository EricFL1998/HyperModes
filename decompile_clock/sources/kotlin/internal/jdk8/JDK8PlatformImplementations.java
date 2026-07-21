package kotlin.internal.jdk8;

import com.xiaomi.onetrack.api.as;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import kotlin.Metadata;
import kotlin.internal.jdk7.JDK7PlatformImplementations;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.random.jdk8.PlatformThreadLocalRandom;
import kotlin.ranges.IntRange;
import kotlin.text.MatchGroup;

/* JADX INFO: compiled from: JDK8PlatformImplementations.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0010\u0018\u00002\u00020\u0001:\u0001\u0010B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\u001a\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016¨\u0006\u0011"}, d2 = {"Lkotlin/internal/jdk8/JDK8PlatformImplementations;", "Lkotlin/internal/jdk7/JDK7PlatformImplementations;", "<init>", "()V", "sdkIsNullOrAtLeast", "", "version", "", "getMatchResultNamedGroup", "Lkotlin/text/MatchGroup;", "matchResult", "Ljava/util/regex/MatchResult;", as.a, "", "defaultPlatformRandom", "Lkotlin/random/Random;", "ReflectSdkVersion", "kotlin-stdlib-jdk8"}, k = 1, mv = {2, 1, 0}, xi = 48)
public class JDK8PlatformImplementations extends JDK7PlatformImplementations {

    /* JADX INFO: compiled from: JDK8PlatformImplementations.kt */
    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\bÂ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\u0004\n\u0002\u0010\u0006¨\u0006\u0007"}, d2 = {"Lkotlin/internal/jdk8/JDK8PlatformImplementations$ReflectSdkVersion;", "", "<init>", "()V", "sdkVersion", "", "Ljava/lang/Integer;", "kotlin-stdlib-jdk8"}, k = 1, mv = {2, 1, 0}, xi = 48)
    private static final class ReflectSdkVersion {
        public static final ReflectSdkVersion INSTANCE = new ReflectSdkVersion();
        public static final Integer sdkVersion;

        private ReflectSdkVersion() {
        }

        static {
            Integer num;
            Integer num2 = null;
            try {
                Object obj = Class.forName("android.os.Build$VERSION").getField("SDK_INT").get(null);
                num = obj instanceof Integer ? (Integer) obj : null;
            } catch (Throwable unused) {
            }
            if (num != null && num.intValue() > 0) {
                num2 = num;
            }
            sdkVersion = num2;
        }
    }

    private final boolean sdkIsNullOrAtLeast(int version) {
        return ReflectSdkVersion.sdkVersion == null || ReflectSdkVersion.sdkVersion.intValue() >= version;
    }

    @Override // kotlin.internal.PlatformImplementations
    public MatchGroup getMatchResultNamedGroup(MatchResult matchResult, String name) {
        Intrinsics.checkNotNullParameter(matchResult, "matchResult");
        Intrinsics.checkNotNullParameter(name, "name");
        Matcher matcher = matchResult instanceof Matcher ? (Matcher) matchResult : null;
        if (matcher == null) {
            throw new UnsupportedOperationException("Retrieving groups by name is not supported on this platform.");
        }
        IntRange intRange = new IntRange(matcher.start(name), matcher.end(name) - 1);
        if (intRange.getStart().intValue() < 0) {
            return null;
        }
        String strGroup = matcher.group(name);
        Intrinsics.checkNotNullExpressionValue(strGroup, "group(...)");
        return new MatchGroup(strGroup, intRange);
    }

    @Override // kotlin.internal.PlatformImplementations
    public Random defaultPlatformRandom() {
        return sdkIsNullOrAtLeast(34) ? new PlatformThreadLocalRandom() : super.defaultPlatformRandom();
    }
}
