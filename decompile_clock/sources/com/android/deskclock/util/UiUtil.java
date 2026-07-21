package com.android.deskclock.util;

import android.app.Activity;
import android.view.ActionMode;
import android.widget.ImageView;
import com.android.deskclock.R;
import miuix.view.EditActionMode;

/* JADX INFO: loaded from: classes.dex */
public class UiUtil {
    public static boolean isMiuiXSdkDarkSupported() {
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void updateActionModeButton2(ActionMode actionMode, boolean z) {
        if (isMiuiXSdkDarkSupported()) {
            try {
                ((EditActionMode) actionMode).setButton(16908314, (CharSequence) null, z ? R.drawable.action_mode_title_button_deselect_all_new : R.drawable.action_mode_title_button_select_all_new);
            } catch (Exception e) {
                Log.e("updateActionModeButton2 error is " + e);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void updateActionModeButton1(ActionMode actionMode) {
        if (isMiuiXSdkDarkSupported()) {
            try {
                ((EditActionMode) actionMode).setButton(16908313, (CharSequence) null, R.drawable.action_mode_title_button_cancel);
            } catch (Exception e) {
                Log.e("updateActionModeButton1 error is " + e);
            }
        }
    }

    public static void updateActionModeDeleteBtn(ActionMode actionMode, boolean z) {
        actionMode.getMenu().findItem(R.id.delete).setEnabled(!z);
    }

    public static ImageView getFloatPageBackIcon(Activity activity) {
        ImageView imageView = new ImageView(activity);
        if (Util.isWideMode(activity)) {
            imageView.setBackgroundResource(R.drawable.action_icon_cancel);
        } else {
            imageView.setBackgroundResource(R.drawable.action_icon_back);
        }
        return imageView;
    }

    public static ImageView getTimezoneSearchBSBackIcon(Activity activity) {
        ImageView imageView = new ImageView(activity);
        imageView.setBackgroundResource(R.drawable.action_icon_cancel);
        return imageView;
    }
}
