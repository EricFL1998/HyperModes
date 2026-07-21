package com.airbnb.lottie.parser;

import android.graphics.PointF;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.model.content.CircleShape;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class CircleShapeParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("nm", MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.PERSONAL, MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.SUBSCRIPTION, "hd", "d");

    private CircleShapeParser() {
    }

    static CircleShape parse(JsonReader jsonReader, LottieComposition lottieComposition, int i) throws IOException {
        boolean z = i == 3;
        boolean zNextBoolean = false;
        String strNextString = null;
        AnimatableValue<PointF, PointF> splitPath = null;
        AnimatablePointValue point = null;
        while (jsonReader.hasNext()) {
            int iSelectName = jsonReader.selectName(NAMES);
            if (iSelectName == 0) {
                strNextString = jsonReader.nextString();
            } else if (iSelectName == 1) {
                splitPath = AnimatablePathValueParser.parseSplitPath(jsonReader, lottieComposition);
            } else if (iSelectName == 2) {
                point = AnimatableValueParser.parsePoint(jsonReader, lottieComposition);
            } else if (iSelectName == 3) {
                zNextBoolean = jsonReader.nextBoolean();
            } else if (iSelectName == 4) {
                z = jsonReader.nextInt() == 3;
            } else {
                jsonReader.skipName();
                jsonReader.skipValue();
            }
        }
        return new CircleShape(strNextString, splitPath, point, z, zNextBoolean);
    }
}
