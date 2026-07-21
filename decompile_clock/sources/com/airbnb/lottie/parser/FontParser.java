package com.airbnb.lottie.parser;

import com.airbnb.lottie.model.Font;
import com.airbnb.lottie.parser.moshi.JsonReader;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class FontParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("fFamily", "fName", "fStyle", "ascent");

    private FontParser() {
    }

    static Font parse(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String strNextString = null;
        String strNextString2 = null;
        float fNextDouble = 0.0f;
        String strNextString3 = null;
        while (jsonReader.hasNext()) {
            int iSelectName = jsonReader.selectName(NAMES);
            if (iSelectName == 0) {
                strNextString = jsonReader.nextString();
            } else if (iSelectName == 1) {
                strNextString3 = jsonReader.nextString();
            } else if (iSelectName == 2) {
                strNextString2 = jsonReader.nextString();
            } else if (iSelectName == 3) {
                fNextDouble = (float) jsonReader.nextDouble();
            } else {
                jsonReader.skipName();
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return new Font(strNextString, strNextString3, strNextString2, fNextDouble);
    }
}
