package com.airbnb.lottie;

import com.airbnb.lottie.network.LottieNetworkCacheProvider;
import com.airbnb.lottie.network.LottieNetworkFetcher;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class LottieConfig {
    final LottieNetworkCacheProvider cacheProvider;
    final boolean enableSystraceMarkers;
    final LottieNetworkFetcher networkFetcher;

    private LottieConfig(LottieNetworkFetcher lottieNetworkFetcher, LottieNetworkCacheProvider lottieNetworkCacheProvider, boolean z) {
        this.networkFetcher = lottieNetworkFetcher;
        this.cacheProvider = lottieNetworkCacheProvider;
        this.enableSystraceMarkers = z;
    }

    public static final class Builder {
        private LottieNetworkCacheProvider cacheProvider;
        private boolean enableSystraceMarkers = false;
        private LottieNetworkFetcher networkFetcher;

        public Builder setNetworkFetcher(LottieNetworkFetcher lottieNetworkFetcher) {
            this.networkFetcher = lottieNetworkFetcher;
            return this;
        }

        public Builder setNetworkCacheDir(final File file) {
            if (this.cacheProvider != null) {
                throw new IllegalStateException("There is already a cache provider!");
            }
            this.cacheProvider = new LottieNetworkCacheProvider() { // from class: com.airbnb.lottie.LottieConfig.Builder.1
                @Override // com.airbnb.lottie.network.LottieNetworkCacheProvider
                public File getCacheDir() {
                    if (!file.isDirectory()) {
                        throw new IllegalArgumentException("cache file must be a directory");
                    }
                    return file;
                }
            };
            return this;
        }

        public Builder setNetworkCacheProvider(final LottieNetworkCacheProvider lottieNetworkCacheProvider) {
            if (this.cacheProvider != null) {
                throw new IllegalStateException("There is already a cache provider!");
            }
            this.cacheProvider = new LottieNetworkCacheProvider() { // from class: com.airbnb.lottie.LottieConfig.Builder.2
                @Override // com.airbnb.lottie.network.LottieNetworkCacheProvider
                public File getCacheDir() {
                    File cacheDir = lottieNetworkCacheProvider.getCacheDir();
                    if (cacheDir.isDirectory()) {
                        return cacheDir;
                    }
                    throw new IllegalArgumentException("cache file must be a directory");
                }
            };
            return this;
        }

        public Builder setEnableSystraceMarkers(boolean z) {
            this.enableSystraceMarkers = z;
            return this;
        }

        public LottieConfig build() {
            return new LottieConfig(this.networkFetcher, this.cacheProvider, this.enableSystraceMarkers);
        }
    }
}
