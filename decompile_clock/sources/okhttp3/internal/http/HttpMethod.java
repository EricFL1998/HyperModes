package okhttp3.internal.http;

import com.android.deskclock.alarm.lifepost.okhttp.OkhttpUtil;

/* JADX INFO: loaded from: classes3.dex */
public final class HttpMethod {
    public static boolean invalidatesCache(String str) {
        return str.equals(OkhttpUtil.METHOD_POST) || str.equals("PATCH") || str.equals(OkhttpUtil.METHOD_PUT) || str.equals(OkhttpUtil.METHOD_DELETE) || str.equals("MOVE");
    }

    public static boolean requiresRequestBody(String str) {
        return str.equals(OkhttpUtil.METHOD_POST) || str.equals(OkhttpUtil.METHOD_PUT) || str.equals("PATCH") || str.equals("PROPPATCH") || str.equals("REPORT");
    }

    public static boolean permitsRequestBody(String str) {
        return (str.equals(OkhttpUtil.METHOD_GET) || str.equals("HEAD")) ? false : true;
    }

    public static boolean redirectsWithBody(String str) {
        return str.equals("PROPFIND");
    }

    public static boolean redirectsToGet(String str) {
        return !str.equals("PROPFIND");
    }

    private HttpMethod() {
    }
}
