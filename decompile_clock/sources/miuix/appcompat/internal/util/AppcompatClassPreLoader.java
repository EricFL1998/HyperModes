package miuix.appcompat.internal.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.appcompat.internal.app.widget.ActionBarContainer;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.appcompat.internal.app.widget.CollapseTabContainer;
import miuix.appcompat.internal.app.widget.ExpandTabContainer;
import miuix.appcompat.internal.app.widget.SecondaryCollapseTabContainer;
import miuix.appcompat.internal.app.widget.SecondaryExpandTabContainer;
import miuix.appcompat.internal.app.widget.actionbar.CollapseTitle;
import miuix.appcompat.internal.app.widget.actionbar.ExpandTitle;
import miuix.springback.view.SpringBackLayout;

/* JADX INFO: loaded from: classes2.dex */
public class AppcompatClassPreLoader {
    public static boolean sIsActionBarClassInit = false;

    public static void preloadClass(final Context context) {
        new Thread(new Runnable() { // from class: miuix.appcompat.internal.util.AppcompatClassPreLoader.1
            @Override // java.lang.Runnable
            public void run() {
                AppcompatClassPreLoader.preloadViewClasses(context);
                AppcompatClassPreLoader.preloadActionBarClasses(context);
            }
        }).start();
    }

    public static void preloadViewClasses(Context context) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class.forName(FrameLayout.class.getName(), true, classLoader);
            Class.forName(LinearLayout.class.getName(), true, classLoader);
            Class.forName(ImageView.class.getName(), true, classLoader);
            Class.forName(TextView.class.getName(), true, classLoader);
            Class.forName(Button.class.getName(), true, classLoader);
            Class.forName(SpringBackLayout.class.getName(), true, classLoader);
            Class.forName(TypedArray.class.getName(), true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void preloadActionBarClasses(Context context) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class.forName(ActionBarOverlayLayout.class.getName(), true, classLoader);
            Class.forName(ActionBarContainer.class.getName(), true, classLoader);
            Class.forName(ActionBar.Tab.class.getName(), true, classLoader);
            Class.forName(AnimState.class.getName(), true, classLoader);
            Class.forName(AnimConfig.class.getName(), true, classLoader);
            Class.forName(CollapseTitle.class.getName(), true, classLoader);
            Class.forName(ExpandTitle.class.getName(), true, classLoader);
            Class.forName(CollapseTabContainer.class.getName(), true, classLoader);
            Class.forName(ExpandTabContainer.class.getName(), true, classLoader);
            Class.forName(SecondaryCollapseTabContainer.class.getName(), true, classLoader);
            Class.forName(SecondaryExpandTabContainer.class.getName(), true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
