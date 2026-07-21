package com.airbnb.lottie.parser;

import com.airbnb.lottie.parser.moshi.JsonReader;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
interface ValueParser<V> {
    V parse(JsonReader jsonReader, float f) throws IOException;
}
