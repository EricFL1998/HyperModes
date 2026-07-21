package miuix.appcompat.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import miui.securityspace.CrossUserUtils;
import miuix.core.compat.ContextCompat;
import miuix.core.compat.UserHandleCompat;
import miuix.core.util.PackageHelper;

/* JADX INFO: loaded from: classes2.dex */
public class CrossUserPickerActivity extends AppCompatActivity {
    private static final String EXTRA_PICKED_USER_ID = "android.intent.extra.picked_user_id";
    private static final String TAG = "CrossUserPickerActivity";
    public static final int USER_ID_INVALID = -1;
    private volatile ContentResolver mCrossUserContentResolver;
    private volatile ContextWrapper mCrossUserContextWrapper;
    private final Object mLockObject = new Object();

    @Override // android.content.ContextWrapper, android.content.Context
    public ContentResolver getContentResolver() {
        if (isCrossUserPick() && PackageHelper.isMiuiSystem()) {
            if (this.mCrossUserContentResolver == null) {
                synchronized (this.mLockObject) {
                    if (this.mCrossUserContentResolver == null) {
                        this.mCrossUserContentResolver = ContextCompat.getContentResolverForUser(this, UserHandleCompat.createNew(validateCrossUser()));
                    }
                }
            }
            Log.d(TAG, "getContentResolver: CrossUserContentResolver");
            return this.mCrossUserContentResolver;
        }
        Log.d(TAG, "getContentResolver: NormalContentResolver");
        return super.getContentResolver();
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context getApplicationContext() {
        if (isCrossUserPick() && PackageHelper.isMiuiSystem()) {
            if (this.mCrossUserContextWrapper == null) {
                synchronized (this.mLockObject) {
                    if (this.mCrossUserContextWrapper == null) {
                        this.mCrossUserContextWrapper = new CrossUserContextWrapper(super.getApplicationContext(), UserHandleCompat.createNew(validateCrossUser()));
                    }
                }
            }
            Log.d(TAG, "getApplicationContext: WrapperedApplication");
            return this.mCrossUserContextWrapper;
        }
        Log.d(TAG, "getApplicationContext: NormalApplication");
        return super.getApplicationContext();
    }

    @Override // android.app.Activity, android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent) {
        if (isCrossUserPick()) {
            intent.putExtra(EXTRA_PICKED_USER_ID, validateCrossUser());
        }
        super.startActivity(intent);
    }

    @Override // android.app.Activity, android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent, Bundle bundle) {
        if (isCrossUserPick()) {
            intent.putExtra(EXTRA_PICKED_USER_ID, validateCrossUser());
        }
        super.startActivity(intent, bundle);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void startActivityForResult(Intent intent, int i) {
        if (isCrossUserPick()) {
            intent.putExtra(EXTRA_PICKED_USER_ID, validateCrossUser());
        }
        super.startActivityForResult(intent, i);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void startActivityForResult(Intent intent, int i, Bundle bundle) {
        if (isCrossUserPick()) {
            intent.putExtra(EXTRA_PICKED_USER_ID, validateCrossUser());
        }
        super.startActivityForResult(intent, i, bundle);
    }

    @Override // android.app.Activity
    public void startActivityFromFragment(android.app.Fragment fragment, Intent intent, int i, Bundle bundle) {
        if (isCrossUserPick()) {
            intent.putExtra(EXTRA_PICKED_USER_ID, validateCrossUser());
        }
        super.startActivityFromFragment(fragment, intent, i, bundle);
    }

    private int validateCrossUser() {
        if (getIntent() == null) {
            return -1;
        }
        int intExtra = getIntent().getIntExtra(EXTRA_PICKED_USER_ID, -1);
        if (validateCallingPackage()) {
            return intExtra;
        }
        return -1;
    }

    private boolean validateCallingPackage() {
        return getPackageName().equals(getCallingPackage()) || CrossUserUtils.checkUidPermission(this, getCallingPackage());
    }

    public boolean isCrossUserPick() {
        return validateCrossUser() != -1;
    }

    class CrossUserContextWrapper extends ContextWrapper {
        Context mBase;
        UserHandle mCrossUser;

        public CrossUserContextWrapper(Context context, UserHandle userHandle) {
            super(context);
            this.mBase = context;
            this.mCrossUser = userHandle;
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public ContentResolver getContentResolver() {
            return ContextCompat.getContentResolverForUser(this.mBase, this.mCrossUser);
        }
    }
}
