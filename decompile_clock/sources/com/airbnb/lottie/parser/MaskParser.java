package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatableShapeValue;
import com.airbnb.lottie.model.content.Mask;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.utils.Logger;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class MaskParser {
    private MaskParser() {
    }

    static Mask parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        jsonReader.beginObject();
        Mask.MaskMode maskMode = null;
        AnimatableShapeValue shapeData = null;
        AnimatableIntegerValue integer = null;
        boolean zNextBoolean = false;
        while (jsonReader.hasNext()) {
            String strNextName = jsonReader.nextName();
            strNextName.hashCode();
            switch (strNextName) {
                case "o":
                    integer = AnimatableValueParser.parseInteger(jsonReader, lottieComposition);
                    break;
                case "pt":
                    shapeData = AnimatableValueParser.parseShapeData(jsonReader, lottieComposition);
                    break;
                case "inv":
                    zNextBoolean = jsonReader.nextBoolean();
                    break;
                case "mode":
                    String strNextString = jsonReader.nextString();
                    strNextString.hashCode();
                    switch (strNextString) {
                        case "a":
                            maskMode = Mask.MaskMode.MASK_MODE_ADD;
                            break;
                        case "i":
                            lottieComposition.addWarning("Animation contains intersect masks. They are not supported but will be treated like add masks.");
                            maskMode = Mask.MaskMode.MASK_MODE_INTERSECT;
                            break;
                        case "n":
                            maskMode = Mask.MaskMode.MASK_MODE_NONE;
                            break;
                        case "s":
                            maskMode = Mask.MaskMode.MASK_MODE_SUBTRACT;
                            break;
                        default:
                            Logger.warning("Unknown mask mode " + strNextName + ". Defaulting to Add.");
                            maskMode = Mask.MaskMode.MASK_MODE_ADD;
                            break;
                    }
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();
        return new Mask(maskMode, shapeData, integer, zNextBoolean);
    }
}
