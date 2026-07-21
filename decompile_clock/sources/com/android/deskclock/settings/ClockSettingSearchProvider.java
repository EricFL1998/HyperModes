package com.android.deskclock.settings;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import com.android.deskclock.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ClockSettingSearchProvider extends ContentProvider {
    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return false;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    private static class RawData {
        String intentAction;
        String intentTargetClass;
        String intentTargetPackage;
        String title;

        public RawData(String str, String str2, String str3, String str4) {
            this.title = str;
            this.intentAction = str2;
            this.intentTargetPackage = str3;
            this.intentTargetClass = str4;
        }
    }

    public List<RawData> prepareData() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new RawData(getContext().getString(R.string.alarm_ringtone_picker), AlarmRingtonePickerActivity.ACTION_ALARM_RINGTONE_PICKER, getContext().getPackageName(), AlarmRingtonePickerActivity.class.getCanonicalName()));
        return arrayList;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchContract.SEARCH_RESULT_COLUMNS);
        for (RawData rawData : prepareData()) {
            matrixCursor.newRow().add("title", rawData.title).add(SearchContract.SearchResultColumn.COLUMN_INTENT_ACTION, rawData.intentAction).add(SearchContract.SearchResultColumn.COLUMN_INTENT_TARGET_PACKAGE, rawData.intentTargetPackage).add(SearchContract.SearchResultColumn.COLUMN_INTENT_TARGET_CLASS, rawData.intentTargetClass);
        }
        return matrixCursor;
    }
}
