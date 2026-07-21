package com.android.deskclock.alarm.lifepost.okhttp;

import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/* JADX INFO: loaded from: classes.dex */
class RequestUtil {
    private CallBackUtil mCallBack;
    private File mFile;
    private String mFileType;
    private Map<String, String> mHeaderMap;
    private String mJsonStr;
    private String mMetyodType;
    private OkHttpClient mOkHttpClient;
    private Request mOkHttpRequest;
    private Map<String, String> mParamsMap;
    private Request.Builder mRequestBuilder;
    private String mUrl;
    private String mfileKey;
    private List<File> mfileList;
    private Map<String, File> mfileMap;

    RequestUtil(String str, String str2, Map<String, String> map, Map<String, String> map2, CallBackUtil callBackUtil) {
        this(str, str2, null, null, null, null, null, null, map, map2, callBackUtil);
    }

    RequestUtil(String str, String str2, String str3, Map<String, String> map, CallBackUtil callBackUtil) {
        this(str, str2, str3, null, null, null, null, null, null, map, callBackUtil);
    }

    RequestUtil(String str, String str2, Map<String, String> map, File file, String str3, String str4, Map<String, String> map2, CallBackUtil callBackUtil) {
        this(str, str2, null, file, null, str3, null, str4, map, map2, callBackUtil);
    }

    RequestUtil(String str, String str2, Map<String, String> map, List<File> list, String str3, String str4, Map<String, String> map2, CallBackUtil callBackUtil) {
        this(str, str2, null, null, list, str3, null, str4, map, map2, callBackUtil);
    }

    RequestUtil(String str, String str2, Map<String, String> map, Map<String, File> map2, String str3, Map<String, String> map3, CallBackUtil callBackUtil) {
        this(str, str2, null, null, null, null, map2, str3, map, map3, callBackUtil);
    }

    private RequestUtil(String str, String str2, String str3, File file, List<File> list, String str4, Map<String, File> map, String str5, Map<String, String> map2, Map<String, String> map3, CallBackUtil callBackUtil) {
        this.mMetyodType = str;
        this.mUrl = str2;
        this.mJsonStr = str3;
        this.mFile = file;
        this.mfileList = list;
        this.mfileKey = str4;
        this.mfileMap = map;
        this.mFileType = str5;
        this.mParamsMap = map2;
        this.mHeaderMap = map3;
        this.mCallBack = callBackUtil;
        getInstance();
    }

    private void getInstance() {
        this.mOkHttpClient = new OkHttpClient();
        this.mRequestBuilder = new Request.Builder();
        if (this.mFile != null || this.mfileList != null || this.mfileMap != null) {
            setFile();
        } else {
            String str = this.mMetyodType;
            str.hashCode();
            switch (str) {
                case "GET":
                    setGetParams();
                    break;
                case "PUT":
                    this.mRequestBuilder.put(getRequestBody());
                    break;
                case "POST":
                    this.mRequestBuilder.post(getRequestBody());
                    break;
                case "DELETE":
                    this.mRequestBuilder.delete(getRequestBody());
                    break;
            }
        }
        this.mRequestBuilder.url(this.mUrl);
        if (this.mHeaderMap != null) {
            setHeader();
        }
        this.mOkHttpRequest = this.mRequestBuilder.build();
    }

    private RequestBody getRequestBody() {
        if (!TextUtils.isEmpty(this.mJsonStr)) {
            return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), this.mJsonStr);
        }
        FormBody.Builder builder = new FormBody.Builder();
        Map<String, String> map = this.mParamsMap;
        if (map != null) {
            for (String str : map.keySet()) {
                builder.add(str, this.mParamsMap.get(str));
            }
        }
        return builder.build();
    }

    private void setGetParams() {
        if (this.mParamsMap != null) {
            this.mUrl += "?";
            for (String str : this.mParamsMap.keySet()) {
                this.mUrl += str + "=" + this.mParamsMap.get(str) + "&";
            }
            String str2 = this.mUrl;
            this.mUrl = str2.substring(0, str2.length() - 1);
        }
    }

    private void setFile() {
        if (this.mFile != null) {
            if (this.mParamsMap == null) {
                setPostFile();
                return;
            } else {
                setPostParameAndFile();
                return;
            }
        }
        if (this.mfileList != null) {
            setPostParameAndListFile();
        } else if (this.mfileMap != null) {
            setPostParameAndMapFile();
        }
    }

    private void setPostFile() {
        File file = this.mFile;
        if (file == null || !file.exists()) {
            return;
        }
        this.mRequestBuilder.post(new ProgressRequestBody(RequestBody.create(MediaType.parse(this.mFileType), this.mFile), this.mCallBack));
    }

    private void setPostParameAndFile() {
        if (this.mParamsMap == null || this.mFile == null) {
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String str : this.mParamsMap.keySet()) {
            builder.addFormDataPart(str, this.mParamsMap.get(str));
        }
        builder.addFormDataPart(this.mfileKey, this.mFile.getName(), RequestBody.create(MediaType.parse(this.mFileType), this.mFile));
        this.mRequestBuilder.post(new ProgressRequestBody(builder.build(), this.mCallBack));
    }

    private void setPostParameAndListFile() {
        if (this.mfileList != null) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            Map<String, String> map = this.mParamsMap;
            if (map != null) {
                for (String str : map.keySet()) {
                    builder.addFormDataPart(str, this.mParamsMap.get(str));
                }
            }
            for (File file : this.mfileList) {
                builder.addFormDataPart(this.mfileKey, file.getName(), RequestBody.create(MediaType.parse(this.mFileType), file));
            }
            this.mRequestBuilder.post(builder.build());
        }
    }

    private void setPostParameAndMapFile() {
        if (this.mfileMap != null) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            Map<String, String> map = this.mParamsMap;
            if (map != null) {
                for (String str : map.keySet()) {
                    builder.addFormDataPart(str, this.mParamsMap.get(str));
                }
            }
            for (String str2 : this.mfileMap.keySet()) {
                builder.addFormDataPart(str2, this.mfileMap.get(str2).getName(), RequestBody.create(MediaType.parse(this.mFileType), this.mfileMap.get(str2)));
            }
            this.mRequestBuilder.post(builder.build());
        }
    }

    private void setHeader() {
        Map<String, String> map = this.mHeaderMap;
        if (map != null) {
            for (String str : map.keySet()) {
                this.mRequestBuilder.addHeader(str, this.mHeaderMap.get(str));
            }
        }
    }

    void execute() {
        this.mOkHttpClient.newCall(this.mOkHttpRequest).enqueue(new Callback() { // from class: com.android.deskclock.alarm.lifepost.okhttp.RequestUtil.1
            @Override // okhttp3.Callback
            public void onFailure(Call call, IOException iOException) {
                if (RequestUtil.this.mCallBack != null) {
                    RequestUtil.this.mCallBack.onError(call, iOException);
                }
            }

            @Override // okhttp3.Callback
            public void onResponse(Call call, Response response) throws IOException {
                if (RequestUtil.this.mCallBack != null) {
                    RequestUtil.this.mCallBack.onSuccess(call, response);
                }
            }
        });
    }

    private static class ProgressRequestBody extends RequestBody {
        private BufferedSink bufferedSink;
        private CallBackUtil callBack;
        private final RequestBody requestBody;

        ProgressRequestBody(RequestBody requestBody, CallBackUtil callBackUtil) {
            this.requestBody = requestBody;
            this.callBack = callBackUtil;
        }

        @Override // okhttp3.RequestBody
        public MediaType contentType() {
            return this.requestBody.contentType();
        }

        @Override // okhttp3.RequestBody
        public long contentLength() throws IOException {
            return this.requestBody.contentLength();
        }

        @Override // okhttp3.RequestBody
        public void writeTo(BufferedSink bufferedSink) throws IOException {
            if (this.bufferedSink == null) {
                this.bufferedSink = Okio.buffer(sink(bufferedSink));
            }
            this.requestBody.writeTo(this.bufferedSink);
            this.bufferedSink.flush();
        }

        /* JADX INFO: renamed from: com.android.deskclock.alarm.lifepost.okhttp.RequestUtil$ProgressRequestBody$1, reason: invalid class name */
        class AnonymousClass1 extends ForwardingSink {
            long bytesWritten;
            long contentLength;

            AnonymousClass1(Sink sink) {
                super(sink);
                this.bytesWritten = 0L;
                this.contentLength = 0L;
            }

            @Override // okio.ForwardingSink, okio.Sink
            public void write(Buffer buffer, long j) throws IOException {
                super.write(buffer, j);
                if (this.contentLength == 0) {
                    this.contentLength = ProgressRequestBody.this.contentLength();
                }
                long j2 = this.bytesWritten + j;
                this.bytesWritten = j2;
                final float f = (j2 * 1.0f) / this.contentLength;
                CallBackUtil.mMainHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.okhttp.RequestUtil.ProgressRequestBody.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ProgressRequestBody.this.callBack.onProgress(f, AnonymousClass1.this.contentLength);
                    }
                });
            }
        }

        private Sink sink(BufferedSink bufferedSink) {
            return new AnonymousClass1(bufferedSink);
        }
    }
}
