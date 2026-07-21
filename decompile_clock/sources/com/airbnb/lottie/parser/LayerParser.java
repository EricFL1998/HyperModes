package com.airbnb.lottie.parser;

import android.graphics.Color;
import android.graphics.Rect;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableTextFrame;
import com.airbnb.lottie.model.animatable.AnimatableTextProperties;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.content.BlurEffect;
import com.airbnb.lottie.model.content.ContentModel;
import com.airbnb.lottie.model.layer.Layer;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.utils.Utils;
import com.airbnb.lottie.value.Keyframe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
public class LayerParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("nm", "ind", "refId", "ty", "parent", "sw", "sh", "sc", "ks", "tt", "masksProperties", "shapes", "t", "ef", "sr", "st", "w", "h", "ip", "op", "tm", "cl", "hd");
    private static final JsonReader.Options TEXT_NAMES = JsonReader.Options.of("d", "a");
    private static final JsonReader.Options EFFECTS_NAMES = JsonReader.Options.of("ty", "nm");

    private LayerParser() {
    }

    public static Layer parse(LottieComposition lottieComposition) {
        Rect bounds = lottieComposition.getBounds();
        return new Layer(Collections.emptyList(), lottieComposition, "__container", -1L, Layer.LayerType.PRE_COMP, -1L, null, Collections.emptyList(), new AnimatableTransform(), 0, 0, 0, 0.0f, 0.0f, bounds.width(), bounds.height(), null, null, Collections.emptyList(), Layer.MatteType.NONE, null, false, null, null);
    }

    public static Layer parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        ArrayList arrayList;
        float f;
        Layer.MatteType matteType = Layer.MatteType.NONE;
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        jsonReader.beginObject();
        Float fValueOf = Float.valueOf(1.0f);
        Float fValueOf2 = Float.valueOf(0.0f);
        Layer.MatteType matteType2 = matteType;
        Layer.LayerType layerType = null;
        String strNextString = null;
        AnimatableTransform animatableTransform = null;
        AnimatableTextFrame documentData = null;
        AnimatableTextProperties animatableTextProperties = null;
        AnimatableFloatValue animatableFloatValue = null;
        BlurEffect blurEffect = null;
        DropShadowEffect dropShadowEffect = null;
        long jNextInt = 0;
        int iNextInt = 0;
        int iNextInt2 = 0;
        int color = 0;
        int iNextInt3 = 0;
        int iNextInt4 = 0;
        boolean zNextBoolean = false;
        float fNextDouble = 1.0f;
        long jNextInt2 = -1;
        float fNextDouble2 = 0.0f;
        float fNextDouble3 = 0.0f;
        float endFrame = 0.0f;
        String strNextString2 = "UNSET";
        String strNextString3 = null;
        while (jsonReader.hasNext()) {
            switch (jsonReader.selectName(NAMES)) {
                case 0:
                    strNextString2 = jsonReader.nextString();
                    break;
                case 1:
                    jNextInt = jsonReader.nextInt();
                    break;
                case 2:
                    strNextString = jsonReader.nextString();
                    break;
                case 3:
                    int iNextInt5 = jsonReader.nextInt();
                    if (iNextInt5 < Layer.LayerType.UNKNOWN.ordinal()) {
                        layerType = Layer.LayerType.values()[iNextInt5];
                    } else {
                        layerType = Layer.LayerType.UNKNOWN;
                    }
                    break;
                case 4:
                    jNextInt2 = jsonReader.nextInt();
                    break;
                case 5:
                    iNextInt = (int) (jsonReader.nextInt() * Utils.dpScale());
                    break;
                case 6:
                    iNextInt2 = (int) (jsonReader.nextInt() * Utils.dpScale());
                    break;
                case 7:
                    color = Color.parseColor(jsonReader.nextString());
                    break;
                case 8:
                    animatableTransform = AnimatableTransformParser.parse(jsonReader, lottieComposition);
                    break;
                case 9:
                    int iNextInt6 = jsonReader.nextInt();
                    if (iNextInt6 >= Layer.MatteType.values().length) {
                        lottieComposition.addWarning("Unsupported matte type: " + iNextInt6);
                    } else {
                        matteType2 = Layer.MatteType.values()[iNextInt6];
                        int i = AnonymousClass1.$SwitchMap$com$airbnb$lottie$model$layer$Layer$MatteType[matteType2.ordinal()];
                        if (i == 1) {
                            lottieComposition.addWarning("Unsupported matte type: Luma");
                        } else if (i == 2) {
                            lottieComposition.addWarning("Unsupported matte type: Luma Inverted");
                        }
                        lottieComposition.incrementMatteOrMaskCount(1);
                    }
                    break;
                case 10:
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        arrayList2.add(MaskParser.parse(jsonReader, lottieComposition));
                    }
                    lottieComposition.incrementMatteOrMaskCount(arrayList2.size());
                    jsonReader.endArray();
                    break;
                case 11:
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        ContentModel contentModel = ContentModelParser.parse(jsonReader, lottieComposition);
                        if (contentModel != null) {
                            arrayList3.add(contentModel);
                        }
                    }
                    jsonReader.endArray();
                    break;
                case 12:
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        int iSelectName = jsonReader.selectName(TEXT_NAMES);
                        if (iSelectName == 0) {
                            documentData = AnimatableValueParser.parseDocumentData(jsonReader, lottieComposition);
                        } else if (iSelectName == 1) {
                            jsonReader.beginArray();
                            if (jsonReader.hasNext()) {
                                animatableTextProperties = AnimatableTextPropertiesParser.parse(jsonReader, lottieComposition);
                            }
                            while (jsonReader.hasNext()) {
                                jsonReader.skipValue();
                            }
                            jsonReader.endArray();
                        } else {
                            jsonReader.skipName();
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                    break;
                case 13:
                    jsonReader.beginArray();
                    ArrayList arrayList4 = new ArrayList();
                    while (jsonReader.hasNext()) {
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            int iSelectName2 = jsonReader.selectName(EFFECTS_NAMES);
                            if (iSelectName2 == 0) {
                                int iNextInt7 = jsonReader.nextInt();
                                if (iNextInt7 == 29) {
                                    blurEffect = BlurEffectParser.parse(jsonReader, lottieComposition);
                                } else if (iNextInt7 == 25) {
                                    dropShadowEffect = new DropShadowEffectParser().parse(jsonReader, lottieComposition);
                                }
                            } else if (iSelectName2 == 1) {
                                arrayList4.add(jsonReader.nextString());
                            } else {
                                jsonReader.skipName();
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                    }
                    jsonReader.endArray();
                    lottieComposition.addWarning("Lottie doesn't support layer effects. If you are using them for  fills, strokes, trim paths etc. then try adding them directly as contents  in your shape. Found: " + arrayList4);
                    break;
                case 14:
                    fNextDouble = (float) jsonReader.nextDouble();
                    break;
                case 15:
                    fNextDouble3 = (float) jsonReader.nextDouble();
                    break;
                case 16:
                    iNextInt3 = (int) (jsonReader.nextInt() * Utils.dpScale());
                    break;
                case 17:
                    iNextInt4 = (int) (jsonReader.nextInt() * Utils.dpScale());
                    break;
                case 18:
                    fNextDouble2 = (float) jsonReader.nextDouble();
                    break;
                case 19:
                    endFrame = (float) jsonReader.nextDouble();
                    break;
                case 20:
                    animatableFloatValue = AnimatableValueParser.parseFloat(jsonReader, lottieComposition, false);
                    break;
                case 21:
                    strNextString3 = jsonReader.nextString();
                    break;
                case 22:
                    zNextBoolean = jsonReader.nextBoolean();
                    break;
                default:
                    jsonReader.skipName();
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();
        ArrayList arrayList5 = new ArrayList();
        if (fNextDouble2 > 0.0f) {
            arrayList = arrayList5;
            arrayList.add(new Keyframe(lottieComposition, fValueOf2, fValueOf2, null, 0.0f, Float.valueOf(fNextDouble2)));
            f = 0.0f;
        } else {
            arrayList = arrayList5;
            f = 0.0f;
        }
        if (endFrame <= f) {
            endFrame = lottieComposition.getEndFrame();
        }
        arrayList.add(new Keyframe(lottieComposition, fValueOf, fValueOf, null, fNextDouble2, Float.valueOf(endFrame)));
        arrayList.add(new Keyframe(lottieComposition, fValueOf2, fValueOf2, null, endFrame, Float.valueOf(Float.MAX_VALUE)));
        if (strNextString2.endsWith(".ai") || "ai".equals(strNextString3)) {
            lottieComposition.addWarning("Convert your Illustrator layers to shape layers.");
        }
        return new Layer(arrayList3, lottieComposition, strNextString2, jNextInt, layerType, jNextInt2, strNextString, arrayList2, animatableTransform, iNextInt, iNextInt2, color, fNextDouble, fNextDouble3, iNextInt3, iNextInt4, documentData, animatableTextProperties, arrayList, matteType2, animatableFloatValue, zNextBoolean, blurEffect, dropShadowEffect);
    }

    /* JADX INFO: renamed from: com.airbnb.lottie.parser.LayerParser$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$airbnb$lottie$model$layer$Layer$MatteType;

        static {
            int[] iArr = new int[Layer.MatteType.values().length];
            $SwitchMap$com$airbnb$lottie$model$layer$Layer$MatteType = iArr;
            try {
                iArr[Layer.MatteType.LUMA.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$airbnb$lottie$model$layer$Layer$MatteType[Layer.MatteType.LUMA_INVERTED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }
}
