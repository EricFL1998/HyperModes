package miuix.core.util;

import android.text.TextUtils;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class Utf8TextUtils {
    private static final int MAX_LENGTH = 6;
    private static final int MIN_LENGTH = 1;
    private static final String TAG = "Utf8TextUtils";
    private static final String UTF8 = "UTF-8";

    private static int getByteCount(byte b) {
        int i = 0;
        for (int i2 = 7; i2 >= 1 && (1 & ((byte) (b >> i2))) != 0; i2--) {
            i++;
        }
        return i;
    }

    private static class CharRange {
        int length;
        int start;

        CharRange() {
            this.start = -1;
            this.length = -1;
        }

        CharRange(int i, int i2) {
            this.start = i;
            this.length = i2;
        }

        boolean isValid() {
            return this.start >= 0 && this.length > 0;
        }

        int getEndIndex() {
            return this.start + this.length;
        }
    }

    private Utf8TextUtils() {
    }

    public static String subString(String str, int i, int i2) {
        if (!TextUtils.isEmpty(str) && i2 > i) {
            byte[] bytes = str.getBytes();
            List<CharRange> utf8CharList = getUtf8CharList(bytes);
            if (utf8CharList.isEmpty()) {
                return str.substring(i, i2);
            }
            if (i >= 0 && i < utf8CharList.size()) {
                int size = utf8CharList.size();
                int i3 = utf8CharList.get(i).start;
                int length = (i2 >= size ? bytes.length : utf8CharList.get(i2).start) - i3;
                byte[] bArr = new byte[length];
                System.arraycopy(bytes, i3, bArr, 0, length);
                try {
                    return new String(bArr, UTF8);
                } catch (UnsupportedEncodingException unused) {
                }
            }
        }
        return "";
    }

    public static String truncateByte(String str, int i) {
        try {
            byte[] bytes = str.getBytes();
            List<CharRange> utf8CharList = getUtf8CharList(bytes);
            if (utf8CharList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                while (sb.toString().getBytes().length < i) {
                    sb.append(str.charAt(sb.length()));
                }
                if (sb.toString().getBytes().length > i) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                return sb.toString();
            }
            int length = bytes.length;
            for (int size = utf8CharList.size() - 1; size >= 0; size--) {
                CharRange charRange = utf8CharList.get(size);
                if (charRange.start < i) {
                    length = charRange.getEndIndex();
                    if (length > i) {
                        length = charRange.start;
                        break;
                    }
                    break;
                }
            }
            if (length >= bytes.length) {
                return str;
            }
            byte[] bArr = new byte[length];
            System.arraycopy(bytes, 0, bArr, 0, length);
            return new String(bArr, UTF8);
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "failed to get bytes of UTF-8 from " + str + ", " + e);
            return null;
        }
    }

    private static List<CharRange> getUtf8CharList(byte[] bArr) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < bArr.length) {
            CharRange charRangeAt = getCharRangeAt(bArr, i);
            if (!charRangeAt.isValid()) {
                arrayList.clear();
                break;
            }
            arrayList.add(charRangeAt);
            i += charRangeAt.length;
        }
        return arrayList;
    }

    private static CharRange getCharRangeAt(byte[] bArr, int i) {
        int byteCount = getByteCount(bArr[i]);
        if (byteCount == 0) {
            return new CharRange(i, 1);
        }
        return findRange(bArr, i, byteCount);
    }

    private static CharRange findRange(byte[] bArr, int i, int i2) {
        CharRange charRange = new CharRange();
        if (isValidCharacter(bArr, i, i2)) {
            charRange.start = i;
            charRange.length = i2;
        }
        return charRange;
    }

    private static boolean isValidCharacter(byte[] bArr, int i, int i2) {
        if (i2 <= 1 || i2 > 6) {
            return false;
        }
        for (int i3 = 1; i3 < i2; i3++) {
            if (getByteCount(bArr[i + i3]) != 1) {
                return false;
            }
        }
        return true;
    }
}
