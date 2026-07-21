package com.android.deskclock.alarm.lifepost.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class GalleryHelper {
    private static final String TAG = "DC:GalleryHelper";

    public static boolean isComplete(Gallery gallery) {
        if (gallery == null || TextUtils.isEmpty(gallery.getPic()) || TextUtils.isEmpty(gallery.getColor())) {
            return false;
        }
        try {
            Color.parseColor(gallery.getColor());
            return (TextUtils.isEmpty(gallery.getText()) || !TextUtils.isEmpty(gallery.getUrlApp())) && gallery.getExpire() > 0;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0047  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r1v2 */
    public static Gallery getGalleryByType(Context context, int i) throws Throwable {
        Cursor cursorQuery;
        ?? r1 = 0;
        galleryCreateFromCursor = null;
        galleryCreateFromCursor = null;
        galleryCreateFromCursor = null;
        Gallery galleryCreateFromCursor = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(GalleryTable.CONTENT_URI, GalleryTable.PROJECTION, "type=" + i, null, null);
                if (cursorQuery != null) {
                    try {
                        if (cursorQuery.moveToFirst()) {
                            galleryCreateFromCursor = createFromCursor(cursorQuery);
                        }
                    } catch (Exception e) {
                        e = e;
                        Log.e(TAG, "getGalleryById error: ", e);
                        if (cursorQuery != null) {
                        }
                        return galleryCreateFromCursor;
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Exception e2) {
                e = e2;
                cursorQuery = null;
            } catch (Throwable th) {
                th = th;
                if (r1 != 0) {
                    r1.close();
                }
                throw th;
            }
            return galleryCreateFromCursor;
        } catch (Throwable th2) {
            th = th2;
            r1 = context;
            if (r1 != 0) {
                r1.close();
            }
            throw th;
        }
    }

    public static int insertGallery(Context context, Gallery gallery) {
        if (gallery == null) {
            return -1;
        }
        try {
            Uri uriInsert = context.getContentResolver().insert(GalleryTable.CONTENT_URI, transferToContentValues(gallery));
            if (uriInsert != null) {
                return (int) ContentUris.parseId(uriInsert);
            }
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "insertGallery error: ", e);
            return -1;
        }
    }

    public static int updateGalleryPic(Context context, int i, String str) {
        try {
            return context.getContentResolver().update(ContentUris.withAppendedId(GalleryTable.CONTENT_URI, i), transferToContentValues(str), null, null);
        } catch (Exception e) {
            Log.e(TAG, "updateGalleryPic error: ", e);
            return -1;
        }
    }

    public static void deleteGallery(Context context) {
        try {
            context.getContentResolver().delete(GalleryTable.CONTENT_URI, null, null);
        } catch (Exception e) {
            Log.e(TAG, "deleteGallery error: ", e);
        }
    }

    private static Gallery createFromCursor(Cursor cursor) {
        Gallery gallery = new Gallery();
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex("_id");
            if (columnIndex != -1) {
                gallery.setId(cursor.getInt(columnIndex));
            }
            int columnIndex2 = cursor.getColumnIndex("image");
            if (columnIndex2 != -1) {
                gallery.setPic(cursor.getString(columnIndex2));
            }
            int columnIndex3 = cursor.getColumnIndex(GalleryTable.Columns.TEXT);
            if (columnIndex3 != -1) {
                gallery.setText(cursor.getString(columnIndex3));
            }
            int columnIndex4 = cursor.getColumnIndex("color");
            if (columnIndex4 != -1) {
                gallery.setColor(cursor.getString(columnIndex4));
            }
            int columnIndex5 = cursor.getColumnIndex("expire");
            if (columnIndex5 != -1) {
                gallery.setExpire(cursor.getLong(columnIndex5));
            }
            int columnIndex6 = cursor.getColumnIndex("type");
            if (columnIndex6 != -1) {
                gallery.setType(cursor.getInt(columnIndex6));
            }
            int columnIndex7 = cursor.getColumnIndex(GalleryTable.Columns.ACTION_TYPE);
            if (columnIndex7 != -1) {
                gallery.setUrlType(cursor.getString(columnIndex7));
            }
            int columnIndex8 = cursor.getColumnIndex(GalleryTable.Columns.ACTION_URL);
            if (columnIndex8 != -1) {
                gallery.setUrlApp(cursor.getString(columnIndex8));
            }
            int columnIndex9 = cursor.getColumnIndex(GalleryTable.Columns.MARKET_URL);
            if (columnIndex9 != -1) {
                gallery.setMarketUrl(cursor.getString(columnIndex9));
            }
            int columnIndex10 = cursor.getColumnIndex(GalleryTable.Columns.PKG_NAME);
            if (columnIndex10 != -1) {
                gallery.setPkgName(cursor.getString(columnIndex10));
            }
        }
        return gallery;
    }

    private static ContentValues transferToContentValues(Gallery gallery) {
        ContentValues contentValues = new ContentValues();
        if (gallery != null) {
            contentValues.put("image", gallery.getPic());
            contentValues.put(GalleryTable.Columns.TEXT, gallery.getText());
            contentValues.put("color", gallery.getColor());
            contentValues.put("expire", Long.valueOf(gallery.getExpire()));
            contentValues.put("type", Integer.valueOf(gallery.getType()));
            contentValues.put(GalleryTable.Columns.ACTION_TYPE, gallery.getUrlType());
            contentValues.put(GalleryTable.Columns.ACTION_URL, gallery.getUrlApp());
            contentValues.put(GalleryTable.Columns.MARKET_URL, gallery.getMarketUrl());
            contentValues.put(GalleryTable.Columns.PKG_NAME, gallery.getPkgName());
        }
        return contentValues;
    }

    private static ContentValues transferToContentValues(String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("image", str);
        return contentValues;
    }
}
