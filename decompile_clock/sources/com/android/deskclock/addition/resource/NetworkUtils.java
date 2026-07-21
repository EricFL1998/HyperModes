package com.android.deskclock.addition.resource;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class NetworkUtils {
    private static final int BUFFER_SIZE = 4096;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 15000;
    private static final String TAG = "DC:NetworkUtils";

    static JSONObject downloadMetadata(String str) throws Throwable {
        HttpURLConnection httpURLConnection;
        StringBuilder sb = new StringBuilder();
        HttpURLConnection httpURLConnection2 = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            try {
                try {
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(15000);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(httpURLConnection.getInputStream())));
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                        th = th;
                        httpURLConnection2 = httpURLConnection;
                        if (httpURLConnection2 != null) {
                            httpURLConnection2.disconnect();
                        }
                        throw th;
                    }
                    JSONObject jSONObject = new JSONObject(sb.toString());
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    return jSONObject;
                } catch (Throwable th) {
                    th = th;
                    httpURLConnection2 = httpURLConnection;
                }
            } catch (Exception e) {
                e = e;
                Log.e(TAG, "error while downloading metadata " + e);
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                return null;
            }
        } catch (Exception e2) {
            e = e2;
            httpURLConnection = null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    static boolean downloadFile(String str, File file) throws Throwable {
        HttpURLConnection httpURLConnection;
        BufferedInputStream bufferedInputStream;
        FileOutputStream fileOutputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            try {
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(15000);
                bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                try {
                    try {
                        FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                        try {
                            byte[] bArr = new byte[4096];
                            while (true) {
                                int i = bufferedInputStream.read(bArr);
                                if (i != -1) {
                                    fileOutputStream2.write(bArr, 0, i);
                                } else {
                                    try {
                                        break;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            fileOutputStream2.close();
                            try {
                                bufferedInputStream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                            if (httpURLConnection == null) {
                                return true;
                            }
                            httpURLConnection.disconnect();
                            return true;
                        } catch (IOException e3) {
                            e = e3;
                            fileOutputStream = fileOutputStream2;
                            Log.e(TAG, "error while downloading file " + e);
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e4) {
                                    e4.printStackTrace();
                                }
                            }
                            if (bufferedInputStream != null) {
                                try {
                                    bufferedInputStream.close();
                                } catch (IOException e5) {
                                    e5.printStackTrace();
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                            return false;
                        } catch (Throwable th) {
                            th = th;
                            fileOutputStream = fileOutputStream2;
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e6) {
                                    e6.printStackTrace();
                                }
                            }
                            if (bufferedInputStream != null) {
                                try {
                                    bufferedInputStream.close();
                                } catch (IOException e7) {
                                    e7.printStackTrace();
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                                throw th;
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (IOException e8) {
                    e = e8;
                }
            } catch (IOException e9) {
                e = e9;
                bufferedInputStream = null;
            } catch (Throwable th3) {
                th = th3;
                bufferedInputStream = null;
            }
        } catch (IOException e10) {
            e = e10;
            httpURLConnection = null;
            bufferedInputStream = null;
        } catch (Throwable th4) {
            th = th4;
            httpURLConnection = null;
            bufferedInputStream = null;
        }
    }
}
