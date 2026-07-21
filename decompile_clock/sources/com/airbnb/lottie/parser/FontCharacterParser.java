package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.FontCharacter;
import com.airbnb.lottie.model.content.ShapeGroup;
import com.airbnb.lottie.parser.moshi.JsonReader;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class FontCharacterParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("ch", "size", "w", "style", "fFamily", "data");
    private static final JsonReader.Options DATA_NAMES = JsonReader.Options.of("shapes");

    private FontCharacterParser() {
    }

    static FontCharacter parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        ArrayList arrayList = new ArrayList();
        jsonReader.beginObject();
        double dNextDouble = 0.0d;
        String strNextString = null;
        String strNextString2 = null;
        char cCharAt = 0;
        double dNextDouble2 = 0.0d;
        while (jsonReader.hasNext()) {
            int iSelectName = jsonReader.selectName(NAMES);
            if (iSelectName == 0) {
                cCharAt = jsonReader.nextString().charAt(0);
            } else if (iSelectName == 1) {
                dNextDouble2 = jsonReader.nextDouble();
            } else if (iSelectName == 2) {
                dNextDouble = jsonReader.nextDouble();
            } else if (iSelectName == 3) {
                strNextString = jsonReader.nextString();
            } else if (iSelectName == 4) {
                strNextString2 = jsonReader.nextString();
            } else if (iSelectName == 5) {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.selectName(DATA_NAMES) == 0) {
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            arrayList.add((ShapeGroup) ContentModelParser.parse(jsonReader, lottieComposition));
                        }
                        jsonReader.endArray();
                    } else {
                        jsonReader.skipName();
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
            } else {
                jsonReader.skipName();
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return new FontCharacter(arrayList, cCharAt, dNextDouble2, dNextDouble, strNextString, strNextString2);
    }
}
