package com.airbnb.lottie.parser;

import android.graphics.PointF;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.content.RectangleShape;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class RectangleShapeParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("nm", MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.PERSONAL, MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.SUBSCRIPTION, "r", "hd");

    private RectangleShapeParser() {
    }

    static RectangleShape parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        String strNextString = null;
        AnimatableValue<PointF, PointF> splitPath = null;
        AnimatablePointValue point = null;
        AnimatableFloatValue animatableFloatValue = null;
        boolean zNextBoolean = false;
        while (jsonReader.hasNext()) {
            int iSelectName = jsonReader.selectName(NAMES);
            if (iSelectName == 0) {
                strNextString = jsonReader.nextString();
            } else if (iSelectName == 1) {
                splitPath = AnimatablePathValueParser.parseSplitPath(jsonReader, lottieComposition);
            } else if (iSelectName == 2) {
                point = AnimatableValueParser.parsePoint(jsonReader, lottieComposition);
            } else if (iSelectName == 3) {
                animatableFloatValue = AnimatableValueParser.parseFloat(jsonReader, lottieComposition);
            } else if (iSelectName == 4) {
                zNextBoolean = jsonReader.nextBoolean();
            } else {
                jsonReader.skipValue();
            }
        }
        return new RectangleShape(strNextString, splitPath, point, animatableFloatValue, zNextBoolean);
    }
}
