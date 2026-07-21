package com.android.deskclock.alarm.lifepost.okhttp;

import android.content.Context;
import com.android.deskclock.alarm.lifepost.model.Gallery;
import com.android.deskclock.alarm.lifepost.model.GalleryHelper;
import com.android.deskclock.util.FileUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Response;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class GalleryUtil {
    private static final String GALLERY_PIC = "gallery_";
    private static final String IMAGE_DIR_PATH = Util.getFilesDir() + File.separator + "recommend" + File.separator;
    private static final String JPG = ".jpg";
    private static final String TAG = "DC:GalleryUtil";

    public static void getGalleryFromNet(final Context context) {
        OkhttpUtil.okHttpPost(Net.Api.GALLERY, new CallBackUtil<ArrayList<Body<GalleryBean>.Data<GalleryBean>>>() { // from class: com.android.deskclock.alarm.lifepost.okhttp.GalleryUtil.1
            /* JADX WARN: Type inference failed for: r0v4, types: [com.android.deskclock.alarm.lifepost.okhttp.GalleryUtil$1$1] */
            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public ArrayList<Body<GalleryBean>.Data<GalleryBean>> onParseResponse(Call call, Response response) {
                try {
                    String strString = response.body().string();
                    Log.f(GalleryUtil.TAG, "getGalleryFromNet: " + strString);
                    Body body = (Body) new Gson().fromJson(strString, new TypeToken<Body<GalleryBean>>() { // from class: com.android.deskclock.alarm.lifepost.okhttp.GalleryUtil.1.1
                    }.getType());
                    if (body.getStatus() == 0) {
                        return body.getDatas();
                    }
                    return null;
                } catch (Exception e) {
                    Log.e(GalleryUtil.TAG, "getGalleryFromNet error: ", e);
                    return null;
                }
            }

            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public void onFailure(Call call, Exception exc) {
                Log.e(GalleryUtil.TAG, "getGalleryFromNet error: ", exc);
            }

            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public void onResponse(ArrayList<Body<GalleryBean>.Data<GalleryBean>> arrayList) {
                if (arrayList == null || arrayList.size() == 0) {
                    return;
                }
                PrefUtil.setGalleryUpdateTime(System.currentTimeMillis());
                FileUtil.deleteDirectory(GalleryUtil.IMAGE_DIR_PATH);
                GalleryHelper.deleteGallery(context);
                for (int i = 0; i < arrayList.size(); i++) {
                    Body<GalleryBean>.Data<GalleryBean> data = arrayList.get(i);
                    GalleryUtil.saveGallery(context, data.getItemId(), data.getData());
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void saveGallery(Context context, String str, GalleryBean galleryBean) {
        if (galleryBean == null) {
            return;
        }
        try {
            Gallery gallery = new Gallery();
            gallery.setType(!str.equals(Net.Param.GALLERY) ? 1 : 0);
            gallery.setText(galleryBean.getTextInfo().getText());
            gallery.setColor(galleryBean.getTextInfo().getColor());
            gallery.setExpire(galleryBean.getDate() * 1000);
            gallery.setUrlType(galleryBean.getTextInfo().getLink().getUrlType());
            gallery.setUrlApp(galleryBean.getTextInfo().getLink().getUrlApp());
            gallery.setPkgName(galleryBean.getTextInfo().getLink().getPkgName());
            gallery.setMarketUrl(galleryBean.getTextInfo().getLink().getMarketUrl());
            saveGalleryImage(context, GalleryHelper.insertGallery(context, gallery), galleryBean.getImage());
        } catch (Exception e) {
            Log.e(TAG, "saveGallery error, " + e);
        }
    }

    public static void saveGalleryImage(final Context context, final int i, String str) {
        String str2 = IMAGE_DIR_PATH;
        File file = new File(str2);
        if (!file.exists()) {
            file.mkdir();
        }
        final String str3 = str2 + GALLERY_PIC + String.valueOf(System.currentTimeMillis()) + JPG;
        OkhttpUtil.okHttpGet(str, new CallBackUtil<Boolean>() { // from class: com.android.deskclock.alarm.lifepost.okhttp.GalleryUtil.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public Boolean onParseResponse(Call call, Response response) {
                try {
                    return Boolean.valueOf(FileUtil.saveByteArray(response.body().bytes(), str3));
                } catch (IOException e) {
                    Log.e(GalleryUtil.TAG, "saveGalleryImage error: ", e);
                    return false;
                }
            }

            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public void onFailure(Call call, Exception exc) {
                Log.e(GalleryUtil.TAG, "saveGalleryImage error: ", exc);
            }

            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public void onResponse(Boolean bool) {
                if (bool.booleanValue()) {
                    GalleryHelper.updateGalleryPic(context, i, str3);
                }
            }
        });
    }
}
