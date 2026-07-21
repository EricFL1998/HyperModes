package androidx.core.app;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: loaded from: classes.dex */
public class AppLocalesStorageHelper {
    static final String APPLICATION_LOCALES_RECORD_FILE = "androidx.appcompat.app.AppCompatDelegate.application_locales_record_file";
    static final boolean DEBUG = false;
    static final String LOCALE_RECORD_ATTRIBUTE_TAG = "application_locales";
    static final String LOCALE_RECORD_FILE_TAG = "locales";
    static final String TAG = "AppLocalesStorageHelper";
    private static final Object sAppLocaleStorageSync = new Object();

    private AppLocalesStorageHelper() {
    }

    /* JADX WARN: Code duplicated, block: B:46:0x0043 A[EXC_TOP_SPLITTER, PHI: r1
  0x0043: PHI (r1v2 java.lang.String) = (r1v0 java.lang.String), (r1v4 java.lang.String) binds: [B:26:0x0050, B:20:0x0041] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    public static String readLocales(Context context) {
        String attributeValue;
        synchronized (sAppLocaleStorageSync) {
            attributeValue = "";
            try {
                FileInputStream fileInputStreamOpenFileInput = context.openFileInput(APPLICATION_LOCALES_RECORD_FILE);
                try {
                    try {
                        XmlPullParser xmlPullParserNewPullParser = Xml.newPullParser();
                        xmlPullParserNewPullParser.setInput(fileInputStreamOpenFileInput, "UTF-8");
                        int depth = xmlPullParserNewPullParser.getDepth();
                        while (true) {
                            int next = xmlPullParserNewPullParser.next();
                            if (next != 1 && (next != 3 || xmlPullParserNewPullParser.getDepth() > depth)) {
                                if (next != 3 && next != 4 && xmlPullParserNewPullParser.getName().equals(LOCALE_RECORD_FILE_TAG)) {
                                    attributeValue = xmlPullParserNewPullParser.getAttributeValue(null, LOCALE_RECORD_ATTRIBUTE_TAG);
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (fileInputStreamOpenFileInput != null) {
                            try {
                                fileInputStreamOpenFileInput.close();
                            } catch (IOException unused) {
                            }
                        }
                    } catch (IOException | XmlPullParserException unused2) {
                        Log.w(TAG, "Reading app Locales : Unable to parse through file :androidx.appcompat.app.AppCompatDelegate.application_locales_record_file");
                        if (fileInputStreamOpenFileInput != null) {
                            fileInputStreamOpenFileInput.close();
                        }
                    }
                    if (attributeValue.isEmpty()) {
                        context.deleteFile(APPLICATION_LOCALES_RECORD_FILE);
                    }
                } catch (Throwable th) {
                    if (fileInputStreamOpenFileInput != null) {
                        try {
                            fileInputStreamOpenFileInput.close();
                        } catch (IOException unused3) {
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException unused4) {
                return "";
            }
        }
        return attributeValue;
    }

    /* JADX WARN: Code duplicated, block: B:42:0x003f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static void persistLocales(Context context, String str) {
        synchronized (sAppLocaleStorageSync) {
            if (str.equals("")) {
                context.deleteFile(APPLICATION_LOCALES_RECORD_FILE);
                return;
            }
            try {
                FileOutputStream fileOutputStreamOpenFileOutput = context.openFileOutput(APPLICATION_LOCALES_RECORD_FILE, 0);
                XmlSerializer xmlSerializerNewSerializer = Xml.newSerializer();
                try {
                    try {
                        xmlSerializerNewSerializer.setOutput(fileOutputStreamOpenFileOutput, null);
                        xmlSerializerNewSerializer.startDocument("UTF-8", true);
                        xmlSerializerNewSerializer.startTag(null, LOCALE_RECORD_FILE_TAG);
                        xmlSerializerNewSerializer.attribute(null, LOCALE_RECORD_ATTRIBUTE_TAG, str);
                        xmlSerializerNewSerializer.endTag(null, LOCALE_RECORD_FILE_TAG);
                        xmlSerializerNewSerializer.endDocument();
                        if (fileOutputStreamOpenFileOutput != null) {
                            try {
                                fileOutputStreamOpenFileOutput.close();
                            } catch (IOException unused) {
                            }
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Storing App Locales : Failed to persist app-locales in storage ", e);
                        if (fileOutputStreamOpenFileOutput != null) {
                            fileOutputStreamOpenFileOutput.close();
                        }
                    }
                } catch (Throwable th) {
                    if (fileOutputStreamOpenFileOutput != null) {
                        try {
                            fileOutputStreamOpenFileOutput.close();
                        } catch (IOException unused2) {
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException unused3) {
                Log.w(TAG, String.format("Storing App Locales : FileNotFoundException: Cannot open file %s for writing ", APPLICATION_LOCALES_RECORD_FILE));
            }
        }
    }
}
