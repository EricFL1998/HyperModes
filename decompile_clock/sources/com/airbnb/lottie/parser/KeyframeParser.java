package com.airbnb.lottie.parser;

import android.graphics.PointF;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import androidx.collection.SparseArrayCompat;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.core.view.animation.PathInterpolatorCompat;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.utils.Utils;
import com.airbnb.lottie.value.Keyframe;
import com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants;
import java.io.IOException;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
class KeyframeParser {
    private static final float MAX_CP_VALUE = 100.0f;
    private static SparseArrayCompat<WeakReference<Interpolator>> pathInterpolatorCache;
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    static JsonReader.Options NAMES = JsonReader.Options.of("t", MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.SUBSCRIPTION, "e", "o", "i", "h", TypedValues.TransitionType.S_TO, "ti");
    static JsonReader.Options INTERPOLATOR_NAMES = JsonReader.Options.of("x", "y");

    KeyframeParser() {
    }

    private static SparseArrayCompat<WeakReference<Interpolator>> pathInterpolatorCache() {
        if (pathInterpolatorCache == null) {
            pathInterpolatorCache = new SparseArrayCompat<>();
        }
        return pathInterpolatorCache;
    }

    private static WeakReference<Interpolator> getInterpolator(int i) {
        WeakReference<Interpolator> weakReference;
        synchronized (KeyframeParser.class) {
            weakReference = pathInterpolatorCache().get(i);
        }
        return weakReference;
    }

    private static void putInterpolator(int i, WeakReference<Interpolator> weakReference) {
        synchronized (KeyframeParser.class) {
            pathInterpolatorCache.put(i, weakReference);
        }
    }

    static <T> Keyframe<T> parse(JsonReader jsonReader, LottieComposition lottieComposition, float f, ValueParser<T> valueParser, boolean z, boolean z2) throws IOException {
        if (z && z2) {
            return parseMultiDimensionalKeyframe(lottieComposition, jsonReader, f, valueParser);
        }
        if (z) {
            return parseKeyframe(lottieComposition, jsonReader, f, valueParser);
        }
        return parseStaticValue(jsonReader, f, valueParser);
    }

    private static <T> Keyframe<T> parseKeyframe(LottieComposition lottieComposition, JsonReader jsonReader, float f, ValueParser<T> valueParser) throws IOException {
        Interpolator interpolatorInterpolatorFor;
        Interpolator interpolator;
        T t;
        jsonReader.beginObject();
        PointF pointFJsonToPoint = null;
        T t2 = null;
        T t3 = null;
        PointF pointFJsonToPoint2 = null;
        PointF pointFJsonToPoint3 = null;
        float fNextDouble = 0.0f;
        boolean z = false;
        PointF pointFJsonToPoint4 = null;
        while (jsonReader.hasNext()) {
            switch (jsonReader.selectName(NAMES)) {
                case 0:
                    fNextDouble = (float) jsonReader.nextDouble();
                    break;
                case 1:
                    t3 = valueParser.parse(jsonReader, f);
                    break;
                case 2:
                    t2 = valueParser.parse(jsonReader, f);
                    break;
                case 3:
                    pointFJsonToPoint = JsonUtils.jsonToPoint(jsonReader, 1.0f);
                    break;
                case 4:
                    pointFJsonToPoint4 = JsonUtils.jsonToPoint(jsonReader, 1.0f);
                    break;
                case 5:
                    z = jsonReader.nextInt() == 1;
                    break;
                case 6:
                    pointFJsonToPoint2 = JsonUtils.jsonToPoint(jsonReader, f);
                    break;
                case 7:
                    pointFJsonToPoint3 = JsonUtils.jsonToPoint(jsonReader, f);
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();
        if (z) {
            interpolator = LINEAR_INTERPOLATOR;
            t = t3;
        } else {
            if (pointFJsonToPoint != null && pointFJsonToPoint4 != null) {
                interpolatorInterpolatorFor = interpolatorFor(pointFJsonToPoint, pointFJsonToPoint4);
            } else {
                interpolatorInterpolatorFor = LINEAR_INTERPOLATOR;
            }
            interpolator = interpolatorInterpolatorFor;
            t = t2;
        }
        Keyframe<T> keyframe = new Keyframe<>(lottieComposition, t3, t, interpolator, fNextDouble, null);
        keyframe.pathCp1 = pointFJsonToPoint2;
        keyframe.pathCp2 = pointFJsonToPoint3;
        return keyframe;
    }

    /* JADX WARN: Code duplicated, block: B:94:0x01eb  */
    private static <T> Keyframe<T> parseMultiDimensionalKeyframe(LottieComposition lottieComposition, JsonReader jsonReader, float f, ValueParser<T> valueParser) throws IOException {
        Interpolator interpolatorInterpolatorFor;
        Interpolator interpolatorInterpolatorFor2;
        Interpolator interpolatorInterpolatorFor3;
        T t;
        Keyframe<T> keyframe;
        PointF pointF;
        float f2;
        PointF pointF2;
        float f3;
        jsonReader.beginObject();
        PointF pointFJsonToPoint = null;
        boolean z = false;
        PointF pointFJsonToPoint2 = null;
        PointF pointFJsonToPoint3 = null;
        PointF pointF3 = null;
        T t2 = null;
        PointF pointF4 = null;
        PointF pointF5 = null;
        PointF pointF6 = null;
        float fNextDouble = 0.0f;
        PointF pointFJsonToPoint4 = null;
        T t3 = null;
        while (jsonReader.hasNext()) {
            switch (jsonReader.selectName(NAMES)) {
                case 0:
                    pointF = pointFJsonToPoint;
                    fNextDouble = (float) jsonReader.nextDouble();
                    pointFJsonToPoint = pointF;
                    break;
                case 1:
                    pointF = pointFJsonToPoint;
                    t2 = valueParser.parse(jsonReader, f);
                    pointFJsonToPoint = pointF;
                    break;
                case 2:
                    pointF = pointFJsonToPoint;
                    t3 = valueParser.parse(jsonReader, f);
                    pointFJsonToPoint = pointF;
                    break;
                case 3:
                    pointF = pointFJsonToPoint;
                    f2 = fNextDouble;
                    PointF pointF7 = pointFJsonToPoint4;
                    if (jsonReader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                        jsonReader.beginObject();
                        float fNextDouble2 = 0.0f;
                        float fNextDouble3 = 0.0f;
                        float fNextDouble4 = 0.0f;
                        float fNextDouble5 = 0.0f;
                        while (jsonReader.hasNext()) {
                            int iSelectName = jsonReader.selectName(INTERPOLATOR_NAMES);
                            if (iSelectName != 0) {
                                if (iSelectName == 1) {
                                    if (jsonReader.peek() == JsonReader.Token.NUMBER) {
                                        fNextDouble5 = (float) jsonReader.nextDouble();
                                        fNextDouble3 = fNextDouble5;
                                    } else {
                                        jsonReader.beginArray();
                                        fNextDouble3 = (float) jsonReader.nextDouble();
                                        fNextDouble5 = jsonReader.peek() == JsonReader.Token.NUMBER ? (float) jsonReader.nextDouble() : fNextDouble3;
                                        jsonReader.endArray();
                                    }
                                } else {
                                    jsonReader.skipValue();
                                }
                            } else if (jsonReader.peek() == JsonReader.Token.NUMBER) {
                                fNextDouble4 = (float) jsonReader.nextDouble();
                                fNextDouble2 = fNextDouble4;
                            } else {
                                jsonReader.beginArray();
                                fNextDouble2 = (float) jsonReader.nextDouble();
                                fNextDouble4 = jsonReader.peek() == JsonReader.Token.NUMBER ? (float) jsonReader.nextDouble() : fNextDouble2;
                                jsonReader.endArray();
                            }
                        }
                        PointF pointF8 = new PointF(fNextDouble2, fNextDouble3);
                        PointF pointF9 = new PointF(fNextDouble4, fNextDouble5);
                        jsonReader.endObject();
                        pointF4 = pointF9;
                        pointF3 = pointF8;
                        pointFJsonToPoint4 = pointF7;
                        fNextDouble = f2;
                    } else {
                        pointFJsonToPoint2 = JsonUtils.jsonToPoint(jsonReader, f);
                        fNextDouble = f2;
                        pointFJsonToPoint4 = pointF7;
                    }
                    pointFJsonToPoint = pointF;
                    break;
                case 4:
                    if (jsonReader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                        jsonReader.beginObject();
                        float fNextDouble6 = 0.0f;
                        float fNextDouble7 = 0.0f;
                        float fNextDouble8 = 0.0f;
                        float fNextDouble9 = 0.0f;
                        while (jsonReader.hasNext()) {
                            PointF pointF10 = pointFJsonToPoint4;
                            int iSelectName2 = jsonReader.selectName(INTERPOLATOR_NAMES);
                            if (iSelectName2 != 0) {
                                pointF2 = pointFJsonToPoint;
                                if (iSelectName2 == 1) {
                                    if (jsonReader.peek() == JsonReader.Token.NUMBER) {
                                        fNextDouble9 = (float) jsonReader.nextDouble();
                                        fNextDouble = fNextDouble;
                                        fNextDouble7 = fNextDouble9;
                                    } else {
                                        f3 = fNextDouble;
                                        jsonReader.beginArray();
                                        fNextDouble7 = (float) jsonReader.nextDouble();
                                        fNextDouble9 = jsonReader.peek() == JsonReader.Token.NUMBER ? (float) jsonReader.nextDouble() : fNextDouble7;
                                        jsonReader.endArray();
                                        fNextDouble = f3;
                                    }
                                } else {
                                    jsonReader.skipValue();
                                }
                            } else {
                                pointF2 = pointFJsonToPoint;
                                f3 = fNextDouble;
                                if (jsonReader.peek() == JsonReader.Token.NUMBER) {
                                    fNextDouble8 = (float) jsonReader.nextDouble();
                                    fNextDouble = f3;
                                    fNextDouble6 = fNextDouble8;
                                } else {
                                    jsonReader.beginArray();
                                    fNextDouble6 = (float) jsonReader.nextDouble();
                                    fNextDouble8 = jsonReader.peek() == JsonReader.Token.NUMBER ? (float) jsonReader.nextDouble() : fNextDouble6;
                                    jsonReader.endArray();
                                    fNextDouble = f3;
                                }
                            }
                            pointFJsonToPoint4 = pointF10;
                            pointFJsonToPoint = pointF2;
                        }
                        pointF = pointFJsonToPoint;
                        f2 = fNextDouble;
                        PointF pointF11 = new PointF(fNextDouble6, fNextDouble7);
                        PointF pointF12 = new PointF(fNextDouble8, fNextDouble9);
                        jsonReader.endObject();
                        pointF6 = pointF12;
                        pointF5 = pointF11;
                        fNextDouble = f2;
                    } else {
                        pointF = pointFJsonToPoint;
                        pointFJsonToPoint3 = JsonUtils.jsonToPoint(jsonReader, f);
                    }
                    pointFJsonToPoint = pointF;
                    break;
                case 5:
                    z = jsonReader.nextInt() == 1;
                    break;
                case 6:
                    pointFJsonToPoint4 = JsonUtils.jsonToPoint(jsonReader, f);
                    break;
                case 7:
                    pointFJsonToPoint = JsonUtils.jsonToPoint(jsonReader, f);
                    break;
                default:
                    pointF = pointFJsonToPoint;
                    jsonReader.skipValue();
                    pointFJsonToPoint = pointF;
                    break;
            }
        }
        PointF pointF13 = pointFJsonToPoint;
        float f4 = fNextDouble;
        PointF pointF14 = pointFJsonToPoint4;
        jsonReader.endObject();
        if (z) {
            interpolatorInterpolatorFor = LINEAR_INTERPOLATOR;
            t = t2;
        } else {
            if (pointFJsonToPoint2 != null && pointFJsonToPoint3 != null) {
                interpolatorInterpolatorFor = interpolatorFor(pointFJsonToPoint2, pointFJsonToPoint3);
            } else {
                if (pointF3 != null && pointF4 != null && pointF5 != null && pointF6 != null) {
                    interpolatorInterpolatorFor2 = interpolatorFor(pointF3, pointF5);
                    interpolatorInterpolatorFor3 = interpolatorFor(pointF4, pointF6);
                    t = t3;
                    interpolatorInterpolatorFor = null;
                } else {
                    interpolatorInterpolatorFor = LINEAR_INTERPOLATOR;
                }
                if (interpolatorInterpolatorFor2 == null && interpolatorInterpolatorFor3 != null) {
                    keyframe = new Keyframe<>(lottieComposition, t2, t, interpolatorInterpolatorFor2, interpolatorInterpolatorFor3, f4, null);
                } else {
                    keyframe = new Keyframe<>(lottieComposition, t2, t, interpolatorInterpolatorFor, f4, null);
                }
                keyframe.pathCp1 = pointF14;
                keyframe.pathCp2 = pointF13;
                return keyframe;
            }
            t = t3;
        }
        interpolatorInterpolatorFor2 = null;
        interpolatorInterpolatorFor3 = null;
        if (interpolatorInterpolatorFor2 == null) {
            keyframe = new Keyframe<>(lottieComposition, t2, t, interpolatorInterpolatorFor, f4, null);
        } else {
            keyframe = new Keyframe<>(lottieComposition, t2, t, interpolatorInterpolatorFor, f4, null);
        }
        keyframe.pathCp1 = pointF14;
        keyframe.pathCp2 = pointF13;
        return keyframe;
    }

    private static Interpolator interpolatorFor(PointF pointF, PointF pointF2) {
        Interpolator linearInterpolator;
        pointF.x = MiscUtils.clamp(pointF.x, -1.0f, 1.0f);
        pointF.y = MiscUtils.clamp(pointF.y, -100.0f, MAX_CP_VALUE);
        pointF2.x = MiscUtils.clamp(pointF2.x, -1.0f, 1.0f);
        pointF2.y = MiscUtils.clamp(pointF2.y, -100.0f, MAX_CP_VALUE);
        int iHashFor = Utils.hashFor(pointF.x, pointF.y, pointF2.x, pointF2.y);
        WeakReference<Interpolator> interpolator = getInterpolator(iHashFor);
        Interpolator interpolator2 = interpolator != null ? interpolator.get() : null;
        if (interpolator == null || interpolator2 == null) {
            try {
                linearInterpolator = PathInterpolatorCompat.create(pointF.x, pointF.y, pointF2.x, pointF2.y);
            } catch (IllegalArgumentException e) {
                if ("The Path cannot loop back on itself.".equals(e.getMessage())) {
                    linearInterpolator = PathInterpolatorCompat.create(Math.min(pointF.x, 1.0f), pointF.y, Math.max(pointF2.x, 0.0f), pointF2.y);
                } else {
                    linearInterpolator = new LinearInterpolator();
                }
            }
            interpolator2 = linearInterpolator;
            try {
                putInterpolator(iHashFor, new WeakReference(interpolator2));
            } catch (ArrayIndexOutOfBoundsException unused) {
            }
        }
        return interpolator2;
    }

    private static <T> Keyframe<T> parseStaticValue(JsonReader jsonReader, float f, ValueParser<T> valueParser) throws IOException {
        return new Keyframe<>(valueParser.parse(jsonReader, f));
    }
}
