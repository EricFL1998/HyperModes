package miuix.appcompat.app;

import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.MotionEvent;
import androidx.fragment.app.FragmentManager;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public interface ShortcutsCallback {
    default boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return false;
    }

    default boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    default boolean onKeyEvent(KeyEvent keyEvent) {
        return false;
    }

    default boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        return false;
    }

    default boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return false;
    }

    default boolean onKeyShortcutEvent(KeyEvent keyEvent) {
        return false;
    }

    default boolean onKeyUp(int i, KeyEvent keyEvent) {
        return false;
    }

    default void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> list, Menu menu, int i) {
    }

    default boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    default boolean onTrackballEvent(MotionEvent motionEvent) {
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean dispatchKeyEvent(FragmentManager fragmentManager, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyEvent(keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean dispatchKeyShortcutEvent(FragmentManager fragmentManager, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyShortcutEvent(keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean dispatchTouchEvent(FragmentManager fragmentManager, MotionEvent motionEvent) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onTouchEvent(motionEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean dispatchTrackballEvent(FragmentManager fragmentManager, MotionEvent motionEvent) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onTrackballEvent(motionEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean dispatchGenericMotionEvent(FragmentManager fragmentManager, MotionEvent motionEvent) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onGenericMotionEvent(motionEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static void dispatchProvideKeyboardShortcuts(FragmentManager fragmentManager, List<KeyboardShortcutGroup> list, Menu menu, int i) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback)) {
                ((ShortcutsCallback) fragment).onProvideKeyboardShortcuts(list, menu, i);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean dispatchKeyDown(FragmentManager fragmentManager, int i, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyDown(i, keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean dispatchKeyLongPress(FragmentManager fragmentManager, int i, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyLongPress(i, keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean dispatchKeyUp(FragmentManager fragmentManager, int i, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyUp(i, keyEvent)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean dispatchKeyMultiple(FragmentManager fragmentManager, int i, int i2, KeyEvent keyEvent) {
        for (androidx.fragment.app.Fragment fragment : fragmentManager.getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden() && fragment.isResumed() && (fragment instanceof ShortcutsCallback) && ((ShortcutsCallback) fragment).onKeyMultiple(i, i2, keyEvent)) {
                return true;
            }
        }
        return false;
    }
}
