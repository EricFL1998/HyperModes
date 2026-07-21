package com.android.deskclock.addition.resource;

import com.android.deskclock.worldclock.WorldClockEditActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import kotlin.jvm.internal.ByteCompanionObject;
import miuix.security.DigestUtils;

/* JADX INFO: loaded from: classes.dex */
public class MD5Utils {
    public static String MD5Sum(File file) throws Throwable {
        StringBuilder sb = new StringBuilder();
        FileInputStream fileInputStream = null;
        try {
            try {
                try {
                    FileInputStream fileInputStream2 = new FileInputStream(file);
                    try {
                        for (byte b : DigestUtils.get(fileInputStream2, "MD5")) {
                            sb.append(byte2Hex(b));
                        }
                        fileInputStream2.close();
                    } catch (Exception e) {
                        e = e;
                        fileInputStream = fileInputStream2;
                        e.printStackTrace();
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        return sb.toString();
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream = fileInputStream2;
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        return sb.toString();
    }

    public static boolean checkMD5(File file, String str) {
        return file != null && file.exists() && str != null && MD5Sum(file).equals(str.toLowerCase());
    }

    private static String byte2Hex(byte b) {
        int i = (b & ByteCompanionObject.MAX_VALUE) + (b < 0 ? 128 : 0);
        return (i < 16 ? WorldClockEditActivity.LOCAL_CITY_ID : "") + Integer.toHexString(i).toLowerCase();
    }
}
