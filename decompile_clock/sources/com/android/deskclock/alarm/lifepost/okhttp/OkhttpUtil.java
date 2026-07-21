package com.android.deskclock.alarm.lifepost.okhttp;

import java.io.File;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class OkhttpUtil {
    public static final String FILE_TYPE_AUDIO = "audio/*";
    public static final String FILE_TYPE_FILE = "file/*";
    public static final String FILE_TYPE_IMAGE = "image/*";
    public static final String FILE_TYPE_VIDEO = "video/*";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";

    public static void okHttpGet(String str, CallBackUtil callBackUtil) {
        okHttpGet(str, null, null, callBackUtil);
    }

    public static void okHttpGet(String str, Map<String, String> map, CallBackUtil callBackUtil) {
        okHttpGet(str, map, null, callBackUtil);
    }

    public static void okHttpGet(String str, Map<String, String> map, Map<String, String> map2, CallBackUtil callBackUtil) {
        new RequestUtil(METHOD_GET, str, map, map2, callBackUtil).execute();
    }

    public static void okHttpPost(String str, CallBackUtil callBackUtil) {
        okHttpPost(str, null, callBackUtil);
    }

    public static void okHttpPost(String str, Map<String, String> map, CallBackUtil callBackUtil) {
        okHttpPost(str, map, null, callBackUtil);
    }

    public static void okHttpPost(String str, Map<String, String> map, Map<String, String> map2, CallBackUtil callBackUtil) {
        new RequestUtil(METHOD_POST, str, map, map2, callBackUtil).execute();
    }

    public static void okHttpPut(String str, CallBackUtil callBackUtil) {
        okHttpPut(str, null, callBackUtil);
    }

    public static void okHttpPut(String str, Map<String, String> map, CallBackUtil callBackUtil) {
        okHttpPut(str, map, null, callBackUtil);
    }

    public static void okHttpPut(String str, Map<String, String> map, Map<String, String> map2, CallBackUtil callBackUtil) {
        new RequestUtil(METHOD_PUT, str, map, map2, callBackUtil).execute();
    }

    public static void okHttpDelete(String str, CallBackUtil callBackUtil) {
        okHttpDelete(str, null, callBackUtil);
    }

    public static void okHttpDelete(String str, Map<String, String> map, CallBackUtil callBackUtil) {
        okHttpDelete(str, map, null, callBackUtil);
    }

    public static void okHttpDelete(String str, Map<String, String> map, Map<String, String> map2, CallBackUtil callBackUtil) {
        new RequestUtil(METHOD_DELETE, str, map, map2, callBackUtil).execute();
    }

    public static void okHttpPostJson(String str, String str2, CallBackUtil callBackUtil) {
        okHttpPostJson(str, str2, null, callBackUtil);
    }

    public static void okHttpPostJson(String str, String str2, Map<String, String> map, CallBackUtil callBackUtil) {
        new RequestUtil(METHOD_POST, str, str2, map, callBackUtil).execute();
    }

    public static void okHttpUploadFile(String str, File file, String str2, String str3, CallBackUtil callBackUtil) {
        okHttpUploadFile(str, file, str2, str3, null, callBackUtil);
    }

    public static void okHttpUploadFile(String str, File file, String str2, String str3, Map<String, String> map, CallBackUtil callBackUtil) {
        okHttpUploadFile(str, file, str2, str3, map, null, callBackUtil);
    }

    public static void okHttpUploadFile(String str, File file, String str2, String str3, Map<String, String> map, Map<String, String> map2, CallBackUtil callBackUtil) {
        new RequestUtil(METHOD_POST, str, map, file, str2, str3, map2, callBackUtil).execute();
    }

    public static void okHttpUploadListFile(String str, List<File> list, String str2, String str3, CallBackUtil callBackUtil) {
        okHttpUploadListFile(str, null, list, str2, str3, callBackUtil);
    }

    public static void okHttpUploadListFile(String str, Map<String, String> map, List<File> list, String str2, String str3, CallBackUtil callBackUtil) {
        okHttpUploadListFile(str, map, list, str2, str3, null, callBackUtil);
    }

    public static void okHttpUploadListFile(String str, Map<String, String> map, List<File> list, String str2, String str3, Map<String, String> map2, CallBackUtil callBackUtil) {
        new RequestUtil(METHOD_POST, str, map, list, str2, str3, map2, callBackUtil).execute();
    }

    public static void okHttpUploadMapFile(String str, Map<String, File> map, String str2, CallBackUtil callBackUtil) {
        okHttpUploadMapFile(str, map, str2, null, callBackUtil);
    }

    public static void okHttpUploadMapFile(String str, Map<String, File> map, String str2, Map<String, String> map2, CallBackUtil callBackUtil) {
        okHttpUploadMapFile(str, map, str2, map2, null, callBackUtil);
    }

    public static void okHttpUploadMapFile(String str, Map<String, File> map, String str2, Map<String, String> map2, Map<String, String> map3, CallBackUtil callBackUtil) {
        new RequestUtil(METHOD_POST, str, map2, map, str2, map3, callBackUtil).execute();
    }

    public static void okHttpDownloadFile(String str, CallBackUtil.CallBackFile callBackFile) {
        okHttpDownloadFile(str, null, callBackFile);
    }

    public static void okHttpDownloadFile(String str, Map<String, String> map, CallBackUtil.CallBackFile callBackFile) {
        okHttpGet(str, map, null, callBackFile);
    }

    public static void okHttpGetBitmap(String str, CallBackUtil.CallBackBitmap callBackBitmap) {
        okHttpGetBitmap(str, null, callBackBitmap);
    }

    public static void okHttpGetBitmap(String str, Map<String, String> map, CallBackUtil.CallBackBitmap callBackBitmap) {
        okHttpGet(str, map, null, callBackBitmap);
    }
}
