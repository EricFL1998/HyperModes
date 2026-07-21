package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableColorValue;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.parser.moshi.JsonReader;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class DropShadowEffectParser {
    private static final JsonReader.Options DROP_SHADOW_EFFECT_NAMES = JsonReader.Options.of("ef");
    private static final JsonReader.Options INNER_EFFECT_NAMES = JsonReader.Options.of("nm", "v");
    private AnimatableColorValue color;
    private AnimatableFloatValue direction;
    private AnimatableFloatValue distance;
    private AnimatableFloatValue opacity;
    private AnimatableFloatValue radius;

    DropShadowEffect parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        while (jsonReader.hasNext()) {
            if (jsonReader.selectName(DROP_SHADOW_EFFECT_NAMES) == 0) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    maybeParseInnerEffect(jsonReader, lottieComposition);
                }
                jsonReader.endArray();
            } else {
                jsonReader.skipName();
                jsonReader.skipValue();
            }
        }
        if (this.color == null || this.opacity == null || this.direction == null || this.distance == null || this.radius == null) {
            return null;
        }
        return new DropShadowEffect(this.color, this.opacity, this.direction, this.distance, this.radius);
    }

    private void maybeParseInnerEffect(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        jsonReader.beginObject();
        String strNextString = "";
        while (jsonReader.hasNext()) {
            int iSelectName = jsonReader.selectName(INNER_EFFECT_NAMES);
            if (iSelectName != 0) {
                if (iSelectName == 1) {
                    strNextString.hashCode();
                    switch (strNextString) {
                        case "Distance":
                            this.distance = AnimatableValueParser.parseFloat(jsonReader, lottieComposition);
                            break;
                        case "Opacity":
                            this.opacity = AnimatableValueParser.parseFloat(jsonReader, lottieComposition, false);
                            break;
                        case "Direction":
                            this.direction = AnimatableValueParser.parseFloat(jsonReader, lottieComposition, false);
                            break;
                        case "Shadow Color":
                            this.color = AnimatableValueParser.parseColor(jsonReader, lottieComposition);
                            break;
                        case "Softness":
                            this.radius = AnimatableValueParser.parseFloat(jsonReader, lottieComposition);
                            break;
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                } else {
                    jsonReader.skipName();
                    jsonReader.skipValue();
                }
            } else {
                strNextString = jsonReader.nextString();
            }
        }
        jsonReader.endObject();
    }
}
