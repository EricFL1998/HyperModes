package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.content.ContentModel;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.utils.Logger;
import com.android.deskclock.R2;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class ContentModelParser {
    private static final JsonReader.Options NAMES = JsonReader.Options.of("ty", "d");

    private ContentModelParser() {
    }

    static ContentModel parse(JsonReader jsonReader, LottieComposition lottieComposition) throws IOException {
        ContentModel contentModel;
        String strNextString;
        jsonReader.beginObject();
        byte b = 2;
        int iNextInt = 2;
        while (true) {
            contentModel = null;
            if (!jsonReader.hasNext()) {
                strNextString = null;
                break;
            }
            int iSelectName = jsonReader.selectName(NAMES);
            if (iSelectName == 0) {
                strNextString = jsonReader.nextString();
                break;
            }
            if (iSelectName == 1) {
                iNextInt = jsonReader.nextInt();
            } else {
                jsonReader.skipName();
                jsonReader.skipValue();
            }
        }
        if (strNextString == null) {
            return null;
        }
        strNextString.hashCode();
        switch (strNextString.hashCode()) {
            case R2.color.material_cursor_color /* 3239 */:
                b = !strNextString.equals("el") ? (byte) -1 : (byte) 0;
                break;
            case R2.color.material_dynamic_neutral_variant50 /* 3270 */:
                b = !strNextString.equals("fl") ? (byte) -1 : (byte) 1;
                break;
            case R2.color.material_dynamic_secondary40 /* 3295 */:
                if (!strNextString.equals("gf")) {
                    b = -1;
                }
                break;
            case R2.color.material_dynamic_tertiary30 /* 3307 */:
                b = !strNextString.equals("gr") ? (byte) -1 : (byte) 3;
                break;
            case R2.color.material_dynamic_tertiary40 /* 3308 */:
                b = !strNextString.equals("gs") ? (byte) -1 : (byte) 4;
                break;
            case R2.color.miuix_appcompat_action_sheet_item_text_primary_disabled_color_light /* 3488 */:
                b = !strNextString.equals("mm") ? (byte) -1 : (byte) 5;
                break;
            case R2.color.miuix_appcompat_dialog_default_button_text_color_guide_normal_light /* 3633 */:
                b = !strNextString.equals("rc") ? (byte) -1 : (byte) 6;
                break;
            case R2.color.miuix_appcompat_dialog_default_edit_text_bg_solid_light /* 3646 */:
                b = !strNextString.equals("rp") ? (byte) -1 : (byte) 7;
                break;
            case R2.color.miuix_appcompat_dialog_default_singleChoice_checked_text_light /* 3669 */:
                b = !strNextString.equals("sh") ? (byte) -1 : (byte) 8;
                break;
            case R2.color.miuix_appcompat_dim_foreground_dark_disabled /* 3679 */:
                b = !strNextString.equals("sr") ? (byte) -1 : (byte) 9;
                break;
            case R2.color.miuix_appcompat_dim_foreground_light_disabled /* 3681 */:
                b = !strNextString.equals("st") ? (byte) -1 : (byte) 10;
                break;
            case R2.color.miuix_appcompat_dropdown_popup_list_text_color_disabled_dark /* 3705 */:
                b = !strNextString.equals("tm") ? (byte) -1 : (byte) 11;
                break;
            case R2.color.miuix_appcompat_dropdown_popup_list_text_color_pressed_light /* 3710 */:
                b = !strNextString.equals("tr") ? (byte) -1 : (byte) 12;
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
                contentModel = CircleShapeParser.parse(jsonReader, lottieComposition, iNextInt);
                break;
            case 1:
                contentModel = ShapeFillParser.parse(jsonReader, lottieComposition);
                break;
            case 2:
                contentModel = GradientFillParser.parse(jsonReader, lottieComposition);
                break;
            case 3:
                contentModel = ShapeGroupParser.parse(jsonReader, lottieComposition);
                break;
            case 4:
                contentModel = GradientStrokeParser.parse(jsonReader, lottieComposition);
                break;
            case 5:
                contentModel = MergePathsParser.parse(jsonReader);
                lottieComposition.addWarning("Animation contains merge paths. Merge paths are only supported on KitKat+ and must be manually enabled by calling enableMergePathsForKitKatAndAbove().");
                break;
            case 6:
                contentModel = RectangleShapeParser.parse(jsonReader, lottieComposition);
                break;
            case 7:
                contentModel = RepeaterParser.parse(jsonReader, lottieComposition);
                break;
            case 8:
                contentModel = ShapePathParser.parse(jsonReader, lottieComposition);
                break;
            case 9:
                contentModel = PolystarShapeParser.parse(jsonReader, lottieComposition);
                break;
            case 10:
                contentModel = ShapeStrokeParser.parse(jsonReader, lottieComposition);
                break;
            case 11:
                contentModel = ShapeTrimPathParser.parse(jsonReader, lottieComposition);
                break;
            case 12:
                contentModel = AnimatableTransformParser.parse(jsonReader, lottieComposition);
                break;
            default:
                Logger.warning("Unknown shape type " + strNextString);
                break;
        }
        while (jsonReader.hasNext()) {
            jsonReader.skipValue();
        }
        jsonReader.endObject();
        return contentModel;
    }
}
