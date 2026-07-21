package com.airbnb.lottie.parser;

import android.graphics.Path;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableGradientColorValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.content.GradientFill;
import com.airbnb.lottie.model.content.GradientType;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.value.Keyframe;
import com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants;
import java.io.IOException;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
class GradientFillParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("nm", MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.GLOBAL, "o", "t", MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.SUBSCRIPTION, "e", "r", "hd");
    private static final JsonReader.Options GRADIENT_NAMES = JsonReader.Options.of(MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.PERSONAL, "k");

    private GradientFillParser() {
    }

    static GradientFill parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        AnimatableIntegerValue integer = null;
        Path.FillType fillType = Path.FillType.WINDING;
        String strNextString = null;
        GradientType gradientType = null;
        AnimatableGradientColorValue gradientColor = null;
        AnimatablePointValue point = null;
        AnimatablePointValue point2 = null;
        boolean zNextBoolean = false;
        while (jsonReader.hasNext()) {
            switch (jsonReader.selectName(NAMES)) {
                case 0:
                    strNextString = jsonReader.nextString();
                    break;
                case 1:
                    jsonReader.beginObject();
                    int iNextInt = -1;
                    while (jsonReader.hasNext()) {
                        int iSelectName = jsonReader.selectName(GRADIENT_NAMES);
                        if (iSelectName == 0) {
                            iNextInt = jsonReader.nextInt();
                        } else if (iSelectName == 1) {
                            gradientColor = AnimatableValueParser.parseGradientColor(jsonReader, lottieComposition, iNextInt);
                        } else {
                            jsonReader.skipName();
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                    break;
                case 2:
                    integer = AnimatableValueParser.parseInteger(jsonReader, lottieComposition);
                    break;
                case 3:
                    gradientType = jsonReader.nextInt() == 1 ? GradientType.LINEAR : GradientType.RADIAL;
                    break;
                case 4:
                    point = AnimatableValueParser.parsePoint(jsonReader, lottieComposition);
                    break;
                case 5:
                    point2 = AnimatableValueParser.parsePoint(jsonReader, lottieComposition);
                    break;
                case 6:
                    fillType = jsonReader.nextInt() == 1 ? Path.FillType.WINDING : Path.FillType.EVEN_ODD;
                    break;
                case 7:
                    zNextBoolean = jsonReader.nextBoolean();
                    break;
                default:
                    jsonReader.skipName();
                    jsonReader.skipValue();
                    break;
            }
        }
        return new GradientFill(strNextString, gradientType, fillType, gradientColor, integer == null ? new AnimatableIntegerValue(Collections.singletonList(new Keyframe(100))) : integer, point, point2, null, null, zNextBoolean);
    }
}
