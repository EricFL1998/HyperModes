package miuix.animation.internal;

import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import miuix.animation.FolmeEase;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;

/* JADX INFO: loaded from: classes2.dex */
public class AnimDebugger {
    private static final String CONFIG_NAME = "config";
    private static final String EASE_NAME = "ease";
    private static final String SPECIAL_NAME = "special";
    private static final String STATE_NAME = "name";
    private static final String TO_STATE_NAME = "toState";

    public static class StateDebugInfo {
        public AnimState state = new AnimState("debugState");
        public AnimConfig config = new AnimConfig();
    }

    public static StateDebugInfo parseDebugConfig() {
        try {
            String prop = CommonUtils.readProp("debug.design.folme");
            if (TextUtils.isEmpty(prop)) {
                return null;
            }
            Log.i(CommonUtils.D_TAG, "get debug.design.folme: " + prop);
            String[] strArrSplit = prop.split(MethodCodeHelper.IDENTITY_INFO_SEPARATOR);
            StateDebugInfo stateDebugInfo = new StateDebugInfo();
            stateDebugInfo.state.setAlias(strArrSplit[0]);
            if (strArrSplit[1].equals("sp")) {
                float[] fArr = new float[strArrSplit.length - 4];
                for (int i = 0; i < strArrSplit.length - 4; i++) {
                    fArr[i] = Float.parseFloat(strArrSplit[i + 4]);
                }
                stateDebugInfo.config.setSpecial(strArrSplit[2], EaseManager.getStyle(FolmeEase.getStyleDef(strArrSplit[3]), fArr), new float[0]);
            } else {
                float[] fArr2 = new float[strArrSplit.length - 3];
                for (int i2 = 0; i2 < strArrSplit.length - 3; i2++) {
                    fArr2[i2] = Float.parseFloat(strArrSplit[i2 + 3]);
                }
                stateDebugInfo.config.setEase(EaseManager.getStyle(FolmeEase.getStyleDef(strArrSplit[2]), fArr2));
            }
            return stateDebugInfo;
        } catch (Exception e) {
            Log.w(CommonUtils.D_TAG, "parseDebugConfig failed. " + e);
            return null;
        }
    }

    public static StateDebugInfo parseDebugConfig(String str) {
        JsonReader jsonReader = new JsonReader(new StringReader(str));
        jsonReader.setLenient(false);
        try {
            StateDebugInfo stateDebugInfo = new StateDebugInfo();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String strNextName = jsonReader.nextName();
                if (strNextName.equals(TO_STATE_NAME)) {
                    parseState(jsonReader, stateDebugInfo.state);
                } else if (strNextName.equals("config")) {
                    parseConfig(jsonReader, stateDebugInfo.config);
                }
            }
            jsonReader.endObject();
            return stateDebugInfo;
        } catch (IOException e) {
            Log.w(CommonUtils.D_TAG, "parseDebugConfig failed. " + e);
            return null;
        }
    }

    private static void parseState(JsonReader jsonReader, AnimState animState) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String strNextName = jsonReader.nextName();
            if (strNextName.equals("name")) {
                animState.setAlias(jsonReader.nextString());
            } else {
                animState.add(strNextName, (float) jsonReader.nextDouble());
            }
        }
        jsonReader.endObject();
    }

    private static void parseConfig(JsonReader jsonReader, AnimConfig animConfig) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String strNextName = jsonReader.nextName();
            if (strNextName.equals(EASE_NAME) && jsonReader.peek() != JsonToken.NULL) {
                animConfig.setEase(parseEase(jsonReader));
            } else if (strNextName.equals(SPECIAL_NAME)) {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String strNextName2 = jsonReader.nextName();
                    jsonReader.beginObject();
                    jsonReader.nextName();
                    animConfig.setSpecial(strNextName2, parseEase(jsonReader), new float[0]);
                    jsonReader.endObject();
                }
                jsonReader.endObject();
            }
        }
        jsonReader.endObject();
    }

    private static EaseManager.EaseStyle parseEase(JsonReader jsonReader) throws IOException {
        EaseManager.EaseStyle style;
        jsonReader.beginArray();
        ArrayList arrayList = new ArrayList();
        int styleDef = jsonReader.hasNext() ? FolmeEase.getStyleDef(jsonReader.nextString()) : 0;
        if (styleDef < -1) {
            while (jsonReader.hasNext()) {
                arrayList.add(Float.valueOf((float) jsonReader.nextDouble()));
            }
            float[] fArr = new float[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                fArr[i] = ((Float) arrayList.get(i)).floatValue();
            }
            style = EaseManager.getStyle(styleDef, fArr);
        } else {
            style = EaseManager.getStyle(styleDef, jsonReader.nextLong());
        }
        jsonReader.endArray();
        return style;
    }

    public static boolean updateTransitionInfo(StateDebugInfo stateDebugInfo, TransitionInfo transitionInfo) {
        String alias = stateDebugInfo.state.getAlias();
        if (alias == null || !alias.equals(transitionInfo.to.getAlias())) {
            return false;
        }
        AnimConfig animConfig = transitionInfo.config;
        if (!stateDebugInfo.config.getSpecialSet().isEmpty()) {
            for (String str : stateDebugInfo.config.getSpecialSet()) {
                animConfig.getSpecialConfig(str).setEase(stateDebugInfo.config.getSpecialConfig(str).ease);
            }
            return true;
        }
        animConfig.setEase(stateDebugInfo.config.ease);
        return true;
    }
}
