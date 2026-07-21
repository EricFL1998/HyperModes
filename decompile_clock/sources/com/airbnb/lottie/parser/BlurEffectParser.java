package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.content.BlurEffect;
import com.airbnb.lottie.parser.moshi.JsonReader;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class BlurEffectParser {
    private static final JsonReader.Options BLUR_EFFECT_NAMES = JsonReader.Options.of("ef");
    private static final JsonReader.Options INNER_BLUR_EFFECT_NAMES = JsonReader.Options.of("ty", "v");

    BlurEffectParser() {
    }

    static BlurEffect parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        BlurEffect blurEffect = null;
        while (jsonReader.hasNext()) {
            if (jsonReader.selectName(BLUR_EFFECT_NAMES) == 0) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    BlurEffect blurEffectMaybeParseInnerEffect = maybeParseInnerEffect(jsonReader, lottieComposition);
                    if (blurEffectMaybeParseInnerEffect != null) {
                        blurEffect = blurEffectMaybeParseInnerEffect;
                    }
                }
                jsonReader.endArray();
            } else {
                jsonReader.skipName();
                jsonReader.skipValue();
            }
        }
        return blurEffect;
    }

    private static BlurEffect maybeParseInnerEffect(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        jsonReader.beginObject();
        BlurEffect blurEffect = null;
        while (true) {
            boolean z = false;
            while (true) {
                if (jsonReader.hasNext()) {
                    int iSelectName = jsonReader.selectName(INNER_BLUR_EFFECT_NAMES);
                    if (iSelectName != 0) {
                        if (iSelectName != 1) {
                            jsonReader.skipName();
                            jsonReader.skipValue();
                        } else if (z) {
                            blurEffect = new BlurEffect(AnimatableValueParser.parseFloat(jsonReader, lottieComposition));
                        } else {
                            jsonReader.skipValue();
                        }
                    } else if (jsonReader.nextInt() == 0) {
                        z = true;
                    }
                } else {
                    jsonReader.endObject();
                    return blurEffect;
                }
            }
        }
    }
}
