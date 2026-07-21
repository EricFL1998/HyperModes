package com.android.deskclock.alarm.lifepost.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import com.android.deskclock.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Response;

/* JADX INFO: loaded from: classes.dex */
public abstract class CallBackUtil<T> {
    public static Handler mMainHandler = new Handler(Looper.getMainLooper());

    public static abstract class CallBackDefault extends CallBackUtil<Response> {
        @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
        public Response onParseResponse(Call call, Response response) {
            return response;
        }
    }

    public abstract void onFailure(Call call, Exception exc);

    public abstract T onParseResponse(Call call, Response response);

    public void onProgress(float f, long j) {
    }

    public abstract void onResponse(T t);

    public void onError(final Call call, final Exception exc) {
        mMainHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil.1
            @Override // java.lang.Runnable
            public void run() {
                CallBackUtil.this.onFailure(call, exc);
            }
        });
    }

    public void onSuccess(Call call, Response response) {
        final T tOnParseResponse = onParseResponse(call, response);
        mMainHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil.2
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.lang.Runnable
            public void run() {
                CallBackUtil.this.onResponse(tOnParseResponse);
            }
        });
    }

    public static abstract class CallBackString extends CallBackUtil<String> {
        @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
        public String onParseResponse(Call call, Response response) {
            try {
                return response.body().string();
            } catch (IOException unused) {
                new RuntimeException("failure");
                return "";
            }
        }
    }

    public static abstract class CallBackBitmap extends CallBackUtil<Bitmap> {
        private int mTargetHeight;
        private int mTargetWidth;

        public CallBackBitmap() {
        }

        public CallBackBitmap(int i, int i2) {
            this.mTargetWidth = i;
            this.mTargetHeight = i2;
        }

        public CallBackBitmap(ImageView imageView) {
            int width = imageView.getWidth();
            int height = imageView.getHeight();
            if (width <= 0 || height <= 0) {
                throw new RuntimeException("无法获取ImageView的width或height");
            }
            this.mTargetWidth = width;
            this.mTargetHeight = height;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
        public Bitmap onParseResponse(Call call, Response response) {
            if (this.mTargetWidth == 0 || this.mTargetHeight == 0) {
                try {
                    byte[] bArrBytes = response.body().bytes();
                    return BitmapFactory.decodeByteArray(bArrBytes, 0, bArrBytes.length);
                } catch (IOException unused) {
                    Log.e("CallBackBitmap(),  onParseResponse() error!");
                    return null;
                }
            }
            return getZoomBitmap(response);
        }

        private Bitmap getZoomBitmap(Response response) {
            byte[] bArrBytes;
            try {
                bArrBytes = response.body().bytes();
            } catch (IOException unused) {
                Log.e("getZoomBitmap(), error!");
                bArrBytes = null;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bArrBytes, 0, bArrBytes.length, options);
            int i = options.outWidth;
            int i2 = options.outHeight;
            int iFloor = (int) Math.floor(i / this.mTargetWidth);
            int iFloor2 = (int) Math.floor(i2 / this.mTargetHeight);
            options.inSampleSize = (iFloor > 1 || iFloor2 > 1) ? Math.max(iFloor, iFloor2) : 1;
            options.inJustDecodeBounds = false;
            Bitmap bitmapDecodeByteArray = BitmapFactory.decodeByteArray(bArrBytes, 0, bArrBytes.length, options);
            if (bitmapDecodeByteArray != null) {
                return bitmapDecodeByteArray;
            }
            throw new RuntimeException("Failed to decode stream.");
        }
    }

    public static abstract class CallBackFile extends CallBackUtil<File> {
        private final String mDestFileDir;
        private final String mdestFileName;

        public CallBackFile(String str, String str2) {
            this.mDestFileDir = str;
            this.mdestFileName = str2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        /* JADX WARN: Code duplicated, block: B:30:0x0089 A[Catch: IOException -> 0x008c, TRY_LEAVE, TryCatch #11 {IOException -> 0x008c, blocks: (B:28:0x0080, B:30:0x0089), top: B:65:0x0080 }] */
        /* JADX WARN: Code duplicated, block: B:41:0x00a2 A[Catch: IOException -> 0x00a5, TRY_LEAVE, TryCatch #2 {IOException -> 0x00a5, blocks: (B:39:0x0099, B:41:0x00a2), top: B:54:0x0099 }] */
        /* JADX WARN: Code duplicated, block: B:59:0x008e A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:63:0x00a7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:72:? A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:74:? A[RETURN, SYNTHETIC] */
        @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
        public File onParseResponse(Call call, Response response) throws Throwable {
            InputStream inputStream;
            InputStream inputStreamByteStream;
            FileOutputStream fileOutputStream;
            byte[] bArr = new byte[8192];
            try {
                inputStreamByteStream = response.body().byteStream();
                try {
                    final long jContentLength = response.body().contentLength();
                    File file = new File(this.mDestFileDir);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File file2 = new File(file, this.mdestFileName);
                    fileOutputStream = new FileOutputStream(file2);
                    long j = 0;
                    while (true) {
                        try {
                            try {
                                int i = inputStreamByteStream.read(bArr);
                                if (i == -1) {
                                    break;
                                }
                                final long j2 = j + ((long) i);
                                fileOutputStream.write(bArr, 0, i);
                                mMainHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil.CallBackFile.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        CallBackFile callBackFile = CallBackFile.this;
                                        float f = j2 * 100.0f;
                                        long j3 = jContentLength;
                                        callBackFile.onProgress(f / j3, j3);
                                    }
                                });
                                j = j2;
                                bArr = bArr;
                            } catch (Throwable th) {
                                th = th;
                                inputStream = inputStreamByteStream;
                            }
                        } catch (Exception unused) {
                            Log.e("CallBackFile(),  onParseResponse() error!");
                            try {
                                response.body().close();
                                if (inputStreamByteStream != null) {
                                    inputStreamByteStream.close();
                                }
                            } catch (IOException unused2) {
                            }
                            if (fileOutputStream != null) {
                                return null;
                            }
                            try {
                                fileOutputStream.close();
                                return null;
                            } catch (IOException unused3) {
                                Log.e("CallBackFile(),  onParseResponse() fos.close() error!");
                                return null;
                            }
                        }
                        th = th;
                        inputStream = inputStreamByteStream;
                        try {
                            response.body().close();
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException unused4) {
                        }
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                                throw th;
                            } catch (IOException unused5) {
                                Log.e("CallBackFile(),  onParseResponse() fos.close() error!");
                                throw th;
                            }
                        }
                        throw th;
                    }
                    fileOutputStream.flush();
                    try {
                        response.body().close();
                        if (inputStreamByteStream != null) {
                            inputStreamByteStream.close();
                        }
                    } catch (IOException unused6) {
                    }
                    try {
                        fileOutputStream.close();
                    } catch (IOException unused7) {
                        Log.e("CallBackFile(),  onParseResponse() fos.close() error!");
                    }
                    return file2;
                } catch (Exception unused8) {
                    fileOutputStream = null;
                    Log.e("CallBackFile(),  onParseResponse() error!");
                    response.body().close();
                    if (inputStreamByteStream != null) {
                        inputStreamByteStream.close();
                    }
                    if (fileOutputStream != null) {
                        return null;
                    }
                    fileOutputStream.close();
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    inputStream = inputStreamByteStream;
                    fileOutputStream = null;
                    response.body().close();
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                        throw th;
                    }
                    throw th;
                }
            } catch (Exception unused9) {
                inputStreamByteStream = null;
            } catch (Throwable th3) {
                th = th3;
                inputStream = null;
            }
        }
    }
}
