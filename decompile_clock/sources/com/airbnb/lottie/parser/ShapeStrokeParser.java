package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableColorValue;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.content.ShapeStroke;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.value.Keyframe;
import com.xiaomi.onetrack.b.m;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
class ShapeStrokeParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("nm", "c", "w", "o", "lc", "lj", m.g, "hd", "d");
    private static final JsonReader.Options DASH_PATTERN_NAMES = JsonReader.Options.of("n", "v");

    private ShapeStrokeParser() {
    }

    static ShapeStroke parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        ArrayList arrayList = new ArrayList();
        float fNextDouble = 0.0f;
        boolean zNextBoolean = false;
        String strNextString = null;
        AnimatableFloatValue animatableFloatValue = null;
        AnimatableColorValue color = null;
        AnimatableFloatValue animatableFloatValue2 = null;
        ShapeStroke.LineCapType lineCapType = null;
        ShapeStroke.LineJoinType lineJoinType = null;
        AnimatableIntegerValue animatableIntegerValue = null;
        while (jsonReader.hasNext()) {
            switch (jsonReader.selectName(NAMES)) {
                case 0:
                    strNextString = jsonReader.nextString();
                    break;
                case 1:
                    color = AnimatableValueParser.parseColor(jsonReader, lottieComposition);
                    break;
                case 2:
                    animatableFloatValue2 = AnimatableValueParser.parseFloat(jsonReader, lottieComposition);
                    break;
                case 3:
                    animatableIntegerValue = AnimatableValueParser.parseInteger(jsonReader, lottieComposition);
                    break;
                case 4:
                    lineCapType = ShapeStroke.LineCapType.values()[jsonReader.nextInt() - 1];
                    break;
                case 5:
                    lineJoinType = ShapeStroke.LineJoinType.values()[jsonReader.nextInt() - 1];
                    break;
                case 6:
                    fNextDouble = (float) jsonReader.nextDouble();
                    break;
                case 7:
                    zNextBoolean = jsonReader.nextBoolean();
                    break;
                case 8:
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        jsonReader.beginObject();
                        String strNextString2 = null;
                        AnimatableFloatValue animatableFloatValue3 = null;
                        while (jsonReader.hasNext()) {
                            int iSelectName = jsonReader.selectName(DASH_PATTERN_NAMES);
                            if (iSelectName == 0) {
                                strNextString2 = jsonReader.nextString();
                            } else if (iSelectName == 1) {
                                animatableFloatValue3 = AnimatableValueParser.parseFloat(jsonReader, lottieComposition);
                            } else {
                                jsonReader.skipName();
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                        strNextString2.hashCode();
                        switch (strNextString2) {
                            case "d":
                            case "g":
                                lottieComposition.setHasDashPattern(true);
                                arrayList.add(animatableFloatValue3);
                                break;
                            case "o":
                                animatableFloatValue = animatableFloatValue3;
                                break;
                        }
                    }
                    jsonReader.endArray();
                    if (arrayList.size() == 1) {
                        arrayList.add((AnimatableFloatValue) arrayList.get(0));
                    }
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        if (animatableIntegerValue == null) {
            animatableIntegerValue = new AnimatableIntegerValue(Collections.singletonList(new Keyframe(100)));
        }
        return new ShapeStroke(strNextString, animatableFloatValue, arrayList, color, animatableIntegerValue, animatableFloatValue2, lineCapType, lineJoinType, fNextDouble, zNextBoolean);
    }
}
