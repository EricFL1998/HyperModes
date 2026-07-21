package miuix.view.inputmethod;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import miuix.core.util.SoftReferenceSingleton;
import miuix.reflect.ReflectionHelper;

/* JADX INFO: loaded from: classes3.dex */
public class InputMethodHelper {
    private static final SoftReferenceSingleton<InputMethodHelper> INSTANCE = new SoftReferenceSingleton<InputMethodHelper>() { // from class: miuix.view.inputmethod.InputMethodHelper.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // miuix.core.util.SoftReferenceSingleton
        public InputMethodHelper createInstance(Object obj) {
            return new InputMethodHelper((Context) obj);
        }
    };
    private static Boolean sSupportViewServedCallback;
    private InputMethodManager mManager;

    private InputMethodHelper(Context context) {
        this.mManager = (InputMethodManager) context.getApplicationContext().getSystemService("input_method");
    }

    public static InputMethodHelper getInstance(Context context) {
        return INSTANCE.get(context);
    }

    public InputMethodManager getManager() {
        return this.mManager;
    }

    public void showKeyBoard(EditText editText) {
        editText.requestFocus();
        this.mManager.viewClicked(editText);
        this.mManager.showSoftInput(editText, 0);
    }

    public void hideKeyBoard(EditText editText) {
        this.mManager.hideSoftInputFromInputMethod(editText.getWindowToken(), 0);
    }

    public static boolean setViewServedCallback(Context context, View view, Runnable runnable) {
        Boolean bool = sSupportViewServedCallback;
        if (bool != null && !bool.booleanValue()) {
            return false;
        }
        try {
            ReflectionHelper.invoke(InputMethodManager.class, getInstance(context).getManager(), "setViewServedCallback", new Class[]{View.class, Runnable.class}, view, runnable);
            sSupportViewServedCallback = true;
            return true;
        } catch (Exception unused) {
            sSupportViewServedCallback = false;
            return false;
        }
    }

    public static boolean removeViewServedCallback(Context context, View view) {
        Boolean bool = sSupportViewServedCallback;
        if (bool != null && bool.booleanValue()) {
            try {
                ReflectionHelper.invoke(InputMethodManager.class, getInstance(context).getManager(), "removeViewServedCallback", new Class[]{View.class}, view);
                return true;
            } catch (Exception unused) {
            }
        }
        return false;
    }
}
