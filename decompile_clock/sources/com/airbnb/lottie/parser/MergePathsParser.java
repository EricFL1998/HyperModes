package com.airbnb.lottie.parser;

import com.airbnb.lottie.model.content.MergePaths;
import com.airbnb.lottie.parser.moshi.JsonReader;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class MergePathsParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("nm", "mm", "hd");

    private MergePathsParser() {
    }

    static MergePaths parse(JsonReader jsonReader) throws IOException {
        String strNextString = null;
        boolean zNextBoolean = false;
        MergePaths.MergePathsMode mergePathsModeForId = null;
        while (jsonReader.hasNext()) {
            int iSelectName = jsonReader.selectName(NAMES);
            if (iSelectName == 0) {
                strNextString = jsonReader.nextString();
            } else if (iSelectName == 1) {
                mergePathsModeForId = MergePaths.MergePathsMode.forId(jsonReader.nextInt());
            } else if (iSelectName == 2) {
                zNextBoolean = jsonReader.nextBoolean();
            } else {
                jsonReader.skipName();
                jsonReader.skipValue();
            }
        }
        return new MergePaths(strNextString, mergePathsModeForId, zNextBoolean);
    }
}
