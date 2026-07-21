package com.android.deskclock.util.themeringtone;

import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class ThemeProviderHelper {
    private static final String KEY_FLAG = "modeFlags";
    private static final String KEY_RESULT_STATUS = "resultStatus";
    private static final String KEY_RESULT_URI_LIST = "uriList";
    private static final int RESULT_STATUS_ARGS_ERROR = 1;
    private static final int RESULT_STATUS_DIR_EMPTY = 4;
    private static final int RESULT_STATUS_OK = 0;
    public static final int RESULT_STATUS_PATH_NOT_EXIST = 3;
    private static final int RESULT_STATUS_THEME_VERSION_LOW = 2;
    private static final String TAG = "DC:ThemeProviderHelper";
    private static final String THEME_METHOD_GRANT_FILES = "grantFilePermission";
    private static final String THEME_PROVIDER_URI = "content://com.android.thememanager.theme_provider";

    /* JADX WARN: Code duplicated, block: B:49:0x00a0 A[PHI: r6
  0x00a0: PHI (r6v3 android.content.ContentProviderClient) = (r6v2 android.content.ContentProviderClient), (r6v5 android.content.ContentProviderClient) binds: [B:48:0x009e, B:39:0x0090] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Multi-variable type inference failed */
    public static GrantThemeResult requestGrantThemeFiles(Context context, String str, Bundle bundle) throws Throwable {
        ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient;
        ContentProviderClient contentProviderClient = 0;
        if (context != null) {
            try {
                if (!TextUtils.isEmpty(str)) {
                    try {
                        contentProviderClientAcquireUnstableContentProviderClient = context.getContentResolver().acquireUnstableContentProviderClient(Uri.parse(THEME_PROVIDER_URI));
                        if (contentProviderClientAcquireUnstableContentProviderClient == null) {
                            if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                                contentProviderClientAcquireUnstableContentProviderClient.close();
                            }
                            return null;
                        }
                        try {
                            Bundle bundleCall = contentProviderClientAcquireUnstableContentProviderClient.call(THEME_METHOD_GRANT_FILES, str, bundle);
                            if (bundleCall == null) {
                                Log.d(TAG, "theme app is low version");
                                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                                    contentProviderClientAcquireUnstableContentProviderClient.close();
                                }
                                return null;
                            }
                            int i = bundleCall.containsKey(KEY_RESULT_STATUS) ? bundleCall.getInt(KEY_RESULT_STATUS) : 0;
                            if (i != 0) {
                                Log.d(TAG, "requestGrantThemeFiles fail, status = " + i);
                                GrantThemeResult grantThemeResult = new GrantThemeResult(null, i);
                                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                                    contentProviderClientAcquireUnstableContentProviderClient.close();
                                }
                                return grantThemeResult;
                            }
                            ArrayList parcelableArrayList = bundleCall.containsKey(KEY_RESULT_URI_LIST) ? bundleCall.getParcelableArrayList(KEY_RESULT_URI_LIST) : null;
                            if (parcelableArrayList == null || parcelableArrayList.size() <= 0) {
                                GrantThemeResult grantThemeResult2 = new GrantThemeResult(null, 0);
                                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                                    contentProviderClientAcquireUnstableContentProviderClient.close();
                                }
                                return grantThemeResult2;
                            }
                            GrantThemeResult grantThemeResult3 = new GrantThemeResult((Uri) parcelableArrayList.get(0), 0);
                            if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                                contentProviderClientAcquireUnstableContentProviderClient.close();
                            }
                            return grantThemeResult3;
                        } catch (Exception e) {
                            e = e;
                            Log.e(TAG, "Failed to grant theme files", e);
                            if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                                contentProviderClientAcquireUnstableContentProviderClient.close();
                            }
                        }
                    } catch (Exception e2) {
                        e = e2;
                        contentProviderClientAcquireUnstableContentProviderClient = null;
                    } catch (Throwable th) {
                        th = th;
                        if (contentProviderClient != 0) {
                            contentProviderClient.close();
                        }
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                contentProviderClient = context;
            }
        }
        return null;
    }

    public static class GrantThemeResult {
        public int resultCode;
        public Uri uri;

        public GrantThemeResult(Uri uri, int i) {
            this.uri = uri;
            this.resultCode = i;
        }

        public String toString() {
            return "resultCode:" + this.resultCode + ", " + this.uri;
        }
    }
}
