package com.airbnb.lottie;

/* JADX INFO: loaded from: classes.dex */
public class Lottie {
    private Lottie() {
    }

    public static void initialize(LottieConfig lottieConfig) {
        L.setFetcher(lottieConfig.networkFetcher);
        L.setCacheProvider(lottieConfig.cacheProvider);
        L.setTraceEnabled(lottieConfig.enableSystraceMarkers);
    }
}
