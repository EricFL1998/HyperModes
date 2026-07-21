package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.content.ShapeTrimPath;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class ShapeTrimPathParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of(MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.SUBSCRIPTION, "e", "o", "nm", "m", "hd");

    private ShapeTrimPathParser() {
    }

    static ShapeTrimPath parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        String strNextString = null;
        ShapeTrimPath.Type typeForId = null;
        AnimatableFloatValue animatableFloatValue = null;
        AnimatableFloatValue animatableFloatValue2 = null;
        AnimatableFloatValue animatableFloatValue3 = null;
        boolean zNextBoolean = false;
        while (jsonReader.hasNext()) {
            int iSelectName = jsonReader.selectName(NAMES);
            if (iSelectName == 0) {
                animatableFloatValue = AnimatableValueParser.parseFloat(jsonReader, lottieComposition, false);
            } else if (iSelectName == 1) {
                animatableFloatValue2 = AnimatableValueParser.parseFloat(jsonReader, lottieComposition, false);
            } else if (iSelectName == 2) {
                animatableFloatValue3 = AnimatableValueParser.parseFloat(jsonReader, lottieComposition, false);
            } else if (iSelectName == 3) {
                strNextString = jsonReader.nextString();
            } else if (iSelectName == 4) {
                typeForId = ShapeTrimPath.Type.forId(jsonReader.nextInt());
            } else if (iSelectName == 5) {
                zNextBoolean = jsonReader.nextBoolean();
            } else {
                jsonReader.skipValue();
            }
        }
        return new ShapeTrimPath(strNextString, typeForId, animatableFloatValue, animatableFloatValue2, animatableFloatValue3, zNextBoolean);
    }
}
