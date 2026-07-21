package com.android.deskclock.addition.resource;

import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class Metadata {
    private static final String KEY_FILE_HASH = "fileHash";
    private static final String KEY_HOST = "host";
    private static final String KEY_RESOURCE_ID = "resourceId";
    private static final String KEY_URL = "url";
    public String mFileHash;
    public String mHost;
    public String mResourceId;
    public String mUrl;

    static Metadata parse(JSONObject jSONObject) {
        Metadata metadata = new Metadata();
        try {
            metadata.mResourceId = jSONObject.getString(KEY_RESOURCE_ID);
            metadata.mHost = jSONObject.getString("host");
            metadata.mUrl = jSONObject.getString("url");
            metadata.mFileHash = jSONObject.getString(KEY_FILE_HASH);
            return metadata;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
