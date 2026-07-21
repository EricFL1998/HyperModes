package com.android.deskclock.alarm.lifepost.okhttp;

import android.content.Context;
import android.util.Base64;
import com.android.deskclock.alarm.lifepost.LifePostController;
import com.android.deskclock.alarm.lifepost.model.News;
import com.android.deskclock.alarm.lifepost.model.NewsHelper;
import com.android.deskclock.util.FileUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.Response;

/* JADX INFO: loaded from: classes.dex */
public class ListenNewsUtil {
    private static final String IMAGE_DIR_PATH = Util.getFilesDir() + File.separator + "NewsImages" + File.separator;
    private static final String JPG = ".jpg";
    private static final String NEWS_PIC = "news_";
    private static final String TAG = "DC:ListenNewsUtil";

    public static void getListenNewsFromUrl(final Context context) {
        HashMap map = new HashMap();
        map.put("itemIds", Net.Param.NEWS_TYPE);
        map.put("lang", Net.Param.NEWS_LANG);
        map.put("country", "cn");
        String strValueOf = String.valueOf(Util.getPackageCode());
        map.put("clockcode", strValueOf);
        String strValueOf2 = String.valueOf(Util.getPackageCode(LifePostController.MI_PLAYER_PKG_NAME));
        map.put("playercode", strValueOf2);
        Log.f(TAG, "clockcode: " + strValueOf + " playercode: " + strValueOf2);
        OkhttpUtil.okHttpGet(new String(Base64.decode(Net.Api.NEWS, 0)), map, new CallBackUtil<ArrayList<Body<NewsBean>.Data<NewsBean>>>() { // from class: com.android.deskclock.alarm.lifepost.okhttp.ListenNewsUtil.1
            /* JADX WARN: Type inference failed for: r0v4, types: [com.android.deskclock.alarm.lifepost.okhttp.ListenNewsUtil$1$1] */
            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public ArrayList<Body<NewsBean>.Data<NewsBean>> onParseResponse(Call call, Response response) {
                try {
                    String strString = response.body().string();
                    Log.i(ListenNewsUtil.TAG, "getNewsFromNet: " + strString);
                    Body body = (Body) new Gson().fromJson(strString, new TypeToken<Body<NewsBean>>() { // from class: com.android.deskclock.alarm.lifepost.okhttp.ListenNewsUtil.1.1
                    }.getType());
                    if (body.getStatus() == 0) {
                        return body.getDatas();
                    }
                    return null;
                } catch (Exception e) {
                    Log.e(ListenNewsUtil.TAG, "getListenNewsFromUrl onParseResponse error, " + e);
                    return null;
                }
            }

            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public void onFailure(Call call, Exception exc) {
                Log.e("getListenNewsFromUrl onFailure, " + exc);
                StatHelper.recordCountEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_POST_NEWS_DATA_FAIL_COUNT);
                OneTrackStatHelper.trackBoolEvent(false, OneTrackStatHelper.LIFE_POST_NEWS_DOWNLOAD_RESULT);
            }

            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public void onResponse(ArrayList<Body<NewsBean>.Data<NewsBean>> arrayList) {
                if (arrayList == null || arrayList.size() == 0) {
                    return;
                }
                OneTrackStatHelper.trackBoolEvent(true, OneTrackStatHelper.LIFE_POST_NEWS_DOWNLOAD_RESULT);
                PrefUtil.setNewsUpdateTime(System.currentTimeMillis());
                FileUtil.deleteDirectory(ListenNewsUtil.IMAGE_DIR_PATH);
                NewsHelper.deleteNews(context);
                for (int i = 0; i < arrayList.size(); i++) {
                    Body<NewsBean>.Data<NewsBean> data = arrayList.get(i);
                    ListenNewsUtil.saveNews(context, data.getItemId(), data.getData());
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void saveNews(Context context, String str, NewsBean newsBean) {
        ArrayList<NewsBean.Candidate> candidates;
        if (newsBean == null || (candidates = newsBean.getCandidates()) == null) {
            return;
        }
        for (int i = 0; i < candidates.size(); i++) {
            try {
                NewsBean.Candidate candidate = candidates.get(i);
                News news = new News();
                news.setType(str);
                news.setShow(newsBean.isShow());
                news.setTitle(candidate.getTitle());
                news.setDesc(candidate.getDesc());
                news.setExpire(getExpireTime(newsBean.getCreateTime(), newsBean.getExpireMinutes()));
                news.setUrlType(candidate.getLink().getUrlType());
                news.setUrlApp(candidate.getLink().getUrlApp());
                news.setUrl(candidate.getLink().getUrl());
                news.setPkgName(candidate.getLink().getPkgName());
                saveNewsImage(context, NewsHelper.insertNews(context, news), candidate.getImgs().get(0));
            } catch (Exception e) {
                Log.e(TAG, "saveNews error, " + e);
            }
        }
    }

    public static void saveNewsImage(final Context context, final int i, String str) {
        String str2 = IMAGE_DIR_PATH;
        File file = new File(str2);
        if (!file.exists()) {
            file.mkdir();
        }
        final String str3 = str2 + NEWS_PIC + String.valueOf(System.currentTimeMillis()) + JPG;
        OkhttpUtil.okHttpGet(str, new CallBackUtil<Boolean>() { // from class: com.android.deskclock.alarm.lifepost.okhttp.ListenNewsUtil.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public Boolean onParseResponse(Call call, Response response) {
                try {
                    return Boolean.valueOf(FileUtil.saveByteArray(response.body().bytes(), str3));
                } catch (IOException e) {
                    Log.e(ListenNewsUtil.TAG, "saveNewsImage error: ", e);
                    return false;
                }
            }

            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public void onFailure(Call call, Exception exc) {
                Log.e(ListenNewsUtil.TAG, "saveNewsImage error: ", exc);
            }

            @Override // com.android.deskclock.alarm.lifepost.okhttp.CallBackUtil
            public void onResponse(Boolean bool) {
                if (bool.booleanValue()) {
                    NewsHelper.updateNewsPic(context, i, str3);
                }
            }
        });
    }

    private static long getExpireTime(long j, String str) {
        int i;
        try {
            i = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            Log.e("getExpireTime(), error is " + e);
            i = 0;
        }
        return (j * 1000) + (((long) i) * 60000);
    }
}
