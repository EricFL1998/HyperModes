package miuix.appcompat.internal.util;

import android.animation.Animator;
import androidx.fragment.app.Fragment;
import miuix.appcompat.R;
import miuix.appcompat.internal.app.FragmentAnimator;

/* JADX INFO: loaded from: classes2.dex */
public class AnimationUtils {
    public static Animator createAnimator(Fragment fragment, int i) {
        if (i == R.anim.miuix_appcompat_fragment_transition_activity_open_enter) {
            return new FragmentAnimator(fragment, true, true);
        }
        if (i == R.anim.miuix_appcompat_fragment_transition_activity_open_exit) {
            return new FragmentAnimator(fragment, true, false);
        }
        if (i == R.anim.miuix_appcompat_fragment_transition_activity_close_enter) {
            return new FragmentAnimator(fragment, false, true);
        }
        if (i == R.anim.miuix_appcompat_fragment_transition_activity_close_exit) {
            return new FragmentAnimator(fragment, false, false);
        }
        return null;
    }
}
