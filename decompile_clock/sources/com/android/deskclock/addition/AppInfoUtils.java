package com.android.deskclock.addition;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: classes.dex */
public class AppInfoUtils {
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";
    public static final String SHA256 = "SHA256";

    public static String getSingInfo(Context context, String str, String str2) {
        for (Signature signature : getSignatures(context, str)) {
            if (SHA1.equals(str2)) {
                return getSignatureString(signature, SHA1);
            }
            if ("MD5".equals(str2)) {
                return getSignatureString(signature, "MD5");
            }
            if (SHA256.equals(str2)) {
                return getSignatureString(signature, SHA256);
            }
        }
        return null;
    }

    public static Signature[] getSignatures(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 64).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSignatureString(Signature signature, String str) {
        byte[] byteArray = signature.toByteArray();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(str);
            if (messageDigest == null) {
                return "error!";
            }
            byte[] bArrDigest = messageDigest.digest(byteArray);
            StringBuilder sb = new StringBuilder();
            for (byte b : bArrDigest) {
                sb.append(Integer.toHexString((b & 255) | 256).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "error!";
        }
    }
}
