package androidx.window.embedding;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import com.xiaomi.onetrack.api.as;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: MatcherUtils.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\bÁ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001d\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0000¢\u0006\u0002\b\fJ\u001f\u0010\r\u001a\u00020\u00042\b\u0010\u000e\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\n\u001a\u00020\u000bH\u0000¢\u0006\u0002\b\u000fJ\u0018\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0080T¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Landroidx/window/embedding/MatcherUtils;", "", "()V", "sDebugMatchers", "", "sMatchersTag", "", "areActivityOrIntentComponentsMatching", "activity", "Landroid/app/Activity;", "ruleComponent", "Landroid/content/ComponentName;", "areActivityOrIntentComponentsMatching$window_release", "areComponentsMatching", "activityComponent", "areComponentsMatching$window_release", "wildcardMatch", as.a, "pattern", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class MatcherUtils {
    public static final MatcherUtils INSTANCE = new MatcherUtils();
    public static final boolean sDebugMatchers = false;
    public static final String sMatchersTag = "SplitRuleResolution";

    private MatcherUtils() {
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0061  */
    /* JADX WARN: Code duplicated, block: B:26:0x008b  */
    public final boolean areComponentsMatching$window_release(ComponentName activityComponent, ComponentName ruleComponent) {
        boolean z;
        boolean z2;
        Intrinsics.checkNotNullParameter(ruleComponent, "ruleComponent");
        if (activityComponent == null) {
            return Intrinsics.areEqual(ruleComponent.getPackageName(), "*") && Intrinsics.areEqual(ruleComponent.getClassName(), "*");
        }
        String string = activityComponent.toString();
        Intrinsics.checkNotNullExpressionValue(string, "activityComponent.toString()");
        if (!(!StringsKt.contains$default((CharSequence) string, (CharSequence) "*", false, 2, (Object) null))) {
            throw new IllegalArgumentException("Wildcard can only be part of the rule.".toString());
        }
        if (Intrinsics.areEqual(activityComponent.getPackageName(), ruleComponent.getPackageName())) {
            z = true;
        } else {
            String packageName = activityComponent.getPackageName();
            Intrinsics.checkNotNullExpressionValue(packageName, "activityComponent.packageName");
            String packageName2 = ruleComponent.getPackageName();
            Intrinsics.checkNotNullExpressionValue(packageName2, "ruleComponent.packageName");
            if (wildcardMatch(packageName, packageName2)) {
                z = true;
            } else {
                z = false;
            }
        }
        if (Intrinsics.areEqual(activityComponent.getClassName(), ruleComponent.getClassName())) {
            z2 = true;
        } else {
            String className = activityComponent.getClassName();
            Intrinsics.checkNotNullExpressionValue(className, "activityComponent.className");
            String className2 = ruleComponent.getClassName();
            Intrinsics.checkNotNullExpressionValue(className2, "ruleComponent.className");
            if (wildcardMatch(className, className2)) {
                z2 = true;
            } else {
                z2 = false;
            }
        }
        return z && z2;
    }

    public final boolean areActivityOrIntentComponentsMatching$window_release(Activity activity, ComponentName ruleComponent) {
        ComponentName component;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(ruleComponent, "ruleComponent");
        if (areComponentsMatching$window_release(activity.getComponentName(), ruleComponent)) {
            return true;
        }
        Intent intent = activity.getIntent();
        if (intent == null || (component = intent.getComponent()) == null) {
            return false;
        }
        return INSTANCE.areComponentsMatching$window_release(component, ruleComponent);
    }

    private final boolean wildcardMatch(String name, String pattern) {
        String str = pattern;
        if (!StringsKt.contains$default((CharSequence) str, (CharSequence) "*", false, 2, (Object) null)) {
            return false;
        }
        if (Intrinsics.areEqual(pattern, "*")) {
            return true;
        }
        if (StringsKt.indexOf$default((CharSequence) str, "*", 0, false, 6, (Object) null) != StringsKt.lastIndexOf$default((CharSequence) str, "*", 0, false, 6, (Object) null) || !StringsKt.endsWith$default(pattern, "*", false, 2, (Object) null)) {
            throw new IllegalArgumentException("Name pattern with a wildcard must only contain a single wildcard in the end".toString());
        }
        String strSubstring = pattern.substring(0, pattern.length() - 1);
        Intrinsics.checkNotNullExpressionValue(strSubstring, "this as java.lang.String…ing(startIndex, endIndex)");
        return StringsKt.startsWith$default(name, strSubstring, false, 2, (Object) null);
    }
}
