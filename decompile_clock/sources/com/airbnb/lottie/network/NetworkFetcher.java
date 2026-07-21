package com.airbnb.lottie.network;

import android.util.Pair;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieResult;
import com.airbnb.lottie.utils.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/* JADX INFO: loaded from: classes.dex */
public class NetworkFetcher {
    private final LottieNetworkFetcher fetcher;
    private final NetworkCache networkCache;

    public NetworkFetcher(NetworkCache networkCache, LottieNetworkFetcher lottieNetworkFetcher) {
        this.networkCache = networkCache;
        this.fetcher = lottieNetworkFetcher;
    }

    public LottieResult<LottieComposition> fetchSync(String str, String str2) {
        LottieComposition lottieCompositionFetchFromCache = fetchFromCache(str, str2);
        if (lottieCompositionFetchFromCache != null) {
            return new LottieResult<>(lottieCompositionFetchFromCache);
        }
        Logger.debug("Animation for " + str + " not found in cache. Fetching from network.");
        return fetchFromNetwork(str, str2);
    }

    private LottieComposition fetchFromCache(String str, String str2) {
        Pair<FileExtension, InputStream> pairFetch;
        LottieResult<LottieComposition> lottieResultFromJsonInputStreamSync;
        if (str2 == null || (pairFetch = this.networkCache.fetch(str)) == null) {
            return null;
        }
        FileExtension fileExtension = (FileExtension) pairFetch.first;
        InputStream inputStream = (InputStream) pairFetch.second;
        if (fileExtension == FileExtension.ZIP) {
            lottieResultFromJsonInputStreamSync = LottieCompositionFactory.fromZipStreamSync(new ZipInputStream(inputStream), str);
        } else {
            lottieResultFromJsonInputStreamSync = LottieCompositionFactory.fromJsonInputStreamSync(inputStream, str);
        }
        if (lottieResultFromJsonInputStreamSync.getValue() != null) {
            return lottieResultFromJsonInputStreamSync.getValue();
        }
        return null;
    }

    private LottieResult<LottieComposition> fetchFromNetwork(String str, String str2) {
        Logger.debug("Fetching " + str);
        LottieFetchResult lottieFetchResult = null;
        try {
            try {
                LottieFetchResult lottieFetchResultFetchSync = this.fetcher.fetchSync(str);
                if (!lottieFetchResultFetchSync.isSuccessful()) {
                    LottieResult<LottieComposition> lottieResult = new LottieResult<>(new IllegalArgumentException(lottieFetchResultFetchSync.error()));
                    if (lottieFetchResultFetchSync != null) {
                        try {
                            lottieFetchResultFetchSync.close();
                        } catch (IOException e) {
                            Logger.warning("LottieFetchResult close failed ", e);
                        }
                    }
                    return lottieResult;
                }
                LottieResult<LottieComposition> lottieResultFromInputStream = fromInputStream(str, lottieFetchResultFetchSync.bodyByteStream(), lottieFetchResultFetchSync.contentType(), str2);
                Logger.debug("Completed fetch from network. Success: " + (lottieResultFromInputStream.getValue() != null));
                if (lottieFetchResultFetchSync != null) {
                    try {
                        lottieFetchResultFetchSync.close();
                    } catch (IOException e2) {
                        Logger.warning("LottieFetchResult close failed ", e2);
                    }
                }
                return lottieResultFromInputStream;
            } catch (Exception e3) {
                LottieResult<LottieComposition> lottieResult2 = new LottieResult<>(e3);
                if (0 != 0) {
                    try {
                        lottieFetchResult.close();
                    } catch (IOException e4) {
                        Logger.warning("LottieFetchResult close failed ", e4);
                    }
                }
                return lottieResult2;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    lottieFetchResult.close();
                } catch (IOException e5) {
                    Logger.warning("LottieFetchResult close failed ", e5);
                }
            }
            throw th;
        }
    }

    private LottieResult<LottieComposition> fromInputStream(String str, InputStream inputStream, String str2, String str3) throws IOException {
        FileExtension fileExtension;
        LottieResult<LottieComposition> lottieResultFromZipStream;
        if (str2 == null) {
            str2 = "application/json";
        }
        if (str2.contains("application/zip") || str.split("\\?")[0].endsWith(".lottie")) {
            Logger.debug("Handling zip response.");
            fileExtension = FileExtension.ZIP;
            lottieResultFromZipStream = fromZipStream(str, inputStream, str3);
        } else {
            Logger.debug("Received json response.");
            fileExtension = FileExtension.JSON;
            lottieResultFromZipStream = fromJsonStream(str, inputStream, str3);
        }
        if (str3 != null && lottieResultFromZipStream.getValue() != null) {
            this.networkCache.renameTempFile(str, fileExtension);
        }
        return lottieResultFromZipStream;
    }

    private LottieResult<LottieComposition> fromZipStream(String str, InputStream inputStream, String str2) throws IOException {
        if (str2 == null) {
            return LottieCompositionFactory.fromZipStreamSync(new ZipInputStream(inputStream), null);
        }
        return LottieCompositionFactory.fromZipStreamSync(new ZipInputStream(new FileInputStream(this.networkCache.writeTempCacheFile(str, inputStream, FileExtension.ZIP))), str);
    }

    private LottieResult<LottieComposition> fromJsonStream(String str, InputStream inputStream, String str2) throws IOException {
        if (str2 == null) {
            return LottieCompositionFactory.fromJsonInputStreamSync(inputStream, null);
        }
        return LottieCompositionFactory.fromJsonInputStreamSync(new FileInputStream(this.networkCache.writeTempCacheFile(str, inputStream, FileExtension.JSON).getAbsolutePath()), str);
    }
}
