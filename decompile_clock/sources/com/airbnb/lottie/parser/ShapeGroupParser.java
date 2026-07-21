package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.content.ContentModel;
import com.airbnb.lottie.model.content.ShapeGroup;
import com.airbnb.lottie.parser.moshi.JsonReader;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class ShapeGroupParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("nm", "hd", "it");

    private ShapeGroupParser() {
    }

    static ShapeGroup parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        ArrayList arrayList = new ArrayList();
        String strNextString = null;
        boolean zNextBoolean = false;
        while (jsonReader.hasNext()) {
            int iSelectName = jsonReader.selectName(NAMES);
            if (iSelectName == 0) {
                strNextString = jsonReader.nextString();
            } else if (iSelectName == 1) {
                zNextBoolean = jsonReader.nextBoolean();
            } else if (iSelectName == 2) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    ContentModel contentModel = ContentModelParser.parse(jsonReader, lottieComposition);
                    if (contentModel != null) {
                        arrayList.add(contentModel);
                    }
                }
                jsonReader.endArray();
            } else {
                jsonReader.skipValue();
            }
        }
        return new ShapeGroup(strNextString, arrayList, zNextBoolean);
    }
}
