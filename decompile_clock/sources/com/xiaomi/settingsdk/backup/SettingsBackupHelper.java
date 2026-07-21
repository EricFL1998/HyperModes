package com.xiaomi.settingsdk.backup;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.xiaomi.settingsdk.backup.data.DataPackage;
import com.xiaomi.settingsdk.backup.data.SettingItem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class SettingsBackupHelper {
    private static final String KEY_DATA = "data";
    private static final String KEY_VERSION = "version";
    private static final String TAG = "SettingsBackup";

    private SettingsBackupHelper() {
    }

    public static DataPackage backupSettings(Context context, ParcelFileDescriptor parcelFileDescriptor, ICloudBackup iCloudBackup) throws Throwable {
        DataPackage dataPackage = new DataPackage();
        iCloudBackup.onBackupSettings(context, dataPackage);
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        Collection<SettingItem<?>> collectionValues = dataPackage.getDataItems().values();
        FileOutputStream fileOutputStream = null;
        try {
            if (collectionValues != null) {
                try {
                    Iterator<SettingItem<?>> it = collectionValues.iterator();
                    while (it.hasNext()) {
                        jSONArray.put(it.next().toJson());
                    }
                    jSONObject.put(SettingsBackupConsts.EXTRA_PACKAGE_NAME, context.getPackageName());
                    jSONObject.put("version", iCloudBackup.getCurrentVersion(context));
                    jSONObject.put("data", jSONArray);
                } catch (IOException e) {
                    e = e;
                    Log.e("SettingsBackup", "IOException in backupSettings", e);
                    closeQuietly(fileOutputStream);
                    return dataPackage;
                } catch (JSONException e2) {
                    e = e2;
                    Log.e("SettingsBackup", "JSONException in backupSettings", e);
                    closeQuietly(fileOutputStream);
                    return dataPackage;
                }
            }
            FileOutputStream fileOutputStream2 = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
            try {
                fileOutputStream2.write(jSONObject.toString().getBytes("utf-8"));
                fileOutputStream2.flush();
                fileOutputStream2.close();
                closeQuietly(fileOutputStream2);
            } catch (IOException e3) {
                fileOutputStream = fileOutputStream2;
                e = e3;
                Log.e("SettingsBackup", "IOException in backupSettings", e);
                closeQuietly(fileOutputStream);
            } catch (JSONException e4) {
                fileOutputStream = fileOutputStream2;
                e = e4;
                Log.e("SettingsBackup", "JSONException in backupSettings", e);
                closeQuietly(fileOutputStream);
            } catch (Throwable th) {
                fileOutputStream = fileOutputStream2;
                th = th;
                closeQuietly(fileOutputStream);
                throw th;
            }
            return dataPackage;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void restoreSettings(Context context, ParcelFileDescriptor parcelFileDescriptor, ICloudBackup iCloudBackup) throws Throwable {
        BufferedReader bufferedReader = null;
        try {
            try {
                BufferedReader bufferedReader2 = new BufferedReader(new FileReader(parcelFileDescriptor.getFileDescriptor()));
                try {
                    StringBuilder sb = new StringBuilder();
                    String property = System.getProperty("line.separator");
                    while (true) {
                        String line = bufferedReader2.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                        sb.append(property);
                    }
                    JSONObject jSONObject = new JSONObject(sb.toString());
                    if (jSONObject.length() > 0) {
                        int iOptInt = jSONObject.optInt("version");
                        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("data");
                        DataPackage dataPackage = new DataPackage();
                        if (jSONArrayOptJSONArray != null) {
                            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i);
                                if (jSONObjectOptJSONObject != null) {
                                    SettingItem<?> settingItemFromJson = SettingItem.fromJson(jSONObjectOptJSONObject);
                                    dataPackage.addAbstractDataItem(settingItemFromJson.key, settingItemFromJson);
                                }
                            }
                        }
                        iCloudBackup.onRestoreSettings(context, dataPackage, iOptInt);
                    }
                    closeQuietly(bufferedReader2);
                } catch (IOException e) {
                    e = e;
                    bufferedReader = bufferedReader2;
                    Log.e("SettingsBackup", "IOException in restoreSettings", e);
                    closeQuietly(bufferedReader);
                } catch (JSONException e2) {
                    e = e2;
                    bufferedReader = bufferedReader2;
                    Log.e("SettingsBackup", "JSONException in restoreSettings", e);
                    closeQuietly(bufferedReader);
                } catch (Throwable th) {
                    th = th;
                    bufferedReader = bufferedReader2;
                    closeQuietly(bufferedReader);
                    throw th;
                }
            } catch (IOException e3) {
                e = e3;
            } catch (JSONException e4) {
                e = e4;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v21 */
    /* JADX WARN: Type inference failed for: r7v6, types: [java.io.OutputStream] */
    public static void restoreOneFile(String str, ParcelFileDescriptor parcelFileDescriptor) throws Throwable {
        ?? r7;
        FileInputStream fileInputStream;
        IOException e;
        FileOutputStream fileOutputStream;
        FileNotFoundException e2;
        FileInputStream fileInputStream2 = null;
        try {
            try {
                fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                try {
                    new File(str.substring(0, str.lastIndexOf(File.separator))).mkdirs();
                    fileOutputStream = new FileOutputStream(new File(str));
                    try {
                        byte[] bArr = new byte[1024];
                        while (true) {
                            int i = fileInputStream.read(bArr);
                            if (i <= 0) {
                                break;
                            } else {
                                fileOutputStream.write(bArr, 0, i);
                            }
                        }
                        fileOutputStream.flush();
                    } catch (FileNotFoundException e3) {
                        e2 = e3;
                        Log.e("SettingsBackup", "FileNotFoundException in restoreFiles: " + str, e2);
                    } catch (IOException e4) {
                        e = e4;
                        Log.e("SettingsBackup", "IOException in restoreFiles: " + str, e);
                    }
                } catch (FileNotFoundException e5) {
                    e2 = e5;
                    fileOutputStream = null;
                } catch (IOException e6) {
                    e = e6;
                    fileOutputStream = null;
                } catch (Throwable th) {
                    th = th;
                    parcelFileDescriptor = null;
                    fileInputStream2 = fileInputStream;
                    r7 = parcelFileDescriptor;
                    closeQuietly(fileInputStream2);
                    closeQuietly((OutputStream) r7);
                    throw th;
                }
            } catch (FileNotFoundException e7) {
                fileInputStream = null;
                e2 = e7;
                fileOutputStream = null;
            } catch (IOException e8) {
                fileInputStream = null;
                e = e8;
                fileOutputStream = null;
            } catch (Throwable th2) {
                th = th2;
                r7 = 0;
                closeQuietly(fileInputStream2);
                closeQuietly((OutputStream) r7);
                throw th;
            }
            closeQuietly(fileInputStream);
            closeQuietly(fileOutputStream);
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public static void restoreFiles(DataPackage dataPackage) throws Throwable {
        for (Map.Entry<String, ParcelFileDescriptor> entry : dataPackage.getFileItems().entrySet()) {
            restoreOneFile(entry.getKey(), entry.getValue());
        }
    }

    private static void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException unused) {
            }
        }
    }

    private static void closeQuietly(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.flush();
            } catch (IOException unused) {
            }
            try {
                outputStream.close();
            } catch (IOException unused2) {
            }
        }
    }

    private static void closeQuietly(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException unused) {
            }
        }
    }
}
