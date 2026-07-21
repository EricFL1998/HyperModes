package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.content.Repeater;
import com.airbnb.lottie.parser.moshi.JsonReader;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class RepeaterParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("nm", "c", "o", "tr", "hd");

    private RepeaterParser() {
    }

    static Repeater parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        String strNextString = null;
        AnimatableFloatValue animatableFloatValue = null;
        AnimatableFloatValue animatableFloatValue2 = null;
        AnimatableTransform animatableTransform = null;
        boolean zNextBoolean = false;
        while (jsonReader.hasNext()) {
            int iSelectName = jsonReader.selectName(NAMES);
            if (iSelectName == 0) {
                strNextString = jsonReader.nextString();
            } else if (iSelectName == 1) {
                animatableFloatValue = AnimatableValueParser.parseFloat(jsonReader, lottieComposition, false);
            } else if (iSelectName == 2) {
                animatableFloatValue2 = AnimatableValueParser.parseFloat(jsonReader, lottieComposition, false);
            } else if (iSelectName == 3) {
                animatableTransform = AnimatableTransformParser.parse(jsonReader, lottieComposition);
            } else if (iSelectName == 4) {
                zNextBoolean = jsonReader.nextBoolean();
            } else {
                jsonReader.skipValue();
            }
        }
        return new Repeater(strNextString, animatableFloatValue, animatableFloatValue2, animatableTransform, zNextBoolean);
    }
}
