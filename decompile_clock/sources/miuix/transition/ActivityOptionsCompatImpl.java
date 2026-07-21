package miuix.transition;

import android.app.ActivityOptions;
import android.os.Bundle;

/* JADX INFO: loaded from: classes3.dex */
public class ActivityOptionsCompatImpl extends androidx.core.app.ActivityOptionsCompat {
    private final ActivityOptions mActivityOptions;

    public ActivityOptionsCompatImpl(ActivityOptions activityOptions) {
        this.mActivityOptions = activityOptions;
    }

    @Override // androidx.core.app.ActivityOptionsCompat
    public Bundle toBundle() {
        return this.mActivityOptions.toBundle();
    }
}
