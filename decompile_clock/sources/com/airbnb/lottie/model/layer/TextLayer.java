package com.airbnb.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import androidx.collection.LongSparseArray;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.TextDelegate;
import com.airbnb.lottie.animation.content.ContentGroup;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.TextKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.ValueCallbackKeyframeAnimation;
import com.airbnb.lottie.model.DocumentData;
import com.airbnb.lottie.model.Font;
import com.airbnb.lottie.model.FontCharacter;
import com.airbnb.lottie.model.animatable.AnimatableTextProperties;
import com.airbnb.lottie.model.content.ShapeGroup;
import com.airbnb.lottie.utils.Utils;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class TextLayer extends BaseLayer {
    private final LongSparseArray<String> codePointCache;
    private BaseKeyframeAnimation<Integer, Integer> colorAnimation;
    private BaseKeyframeAnimation<Integer, Integer> colorCallbackAnimation;
    private final LottieComposition composition;
    private final Map<FontCharacter, List<ContentGroup>> contentsForCharacter;
    private final Paint fillPaint;
    private final LottieDrawable lottieDrawable;
    private final Matrix matrix;
    private final RectF rectF;
    private final StringBuilder stringBuilder;
    private BaseKeyframeAnimation<Integer, Integer> strokeColorAnimation;
    private BaseKeyframeAnimation<Integer, Integer> strokeColorCallbackAnimation;
    private final Paint strokePaint;
    private BaseKeyframeAnimation<Float, Float> strokeWidthAnimation;
    private BaseKeyframeAnimation<Float, Float> strokeWidthCallbackAnimation;
    private final TextKeyframeAnimation textAnimation;
    private BaseKeyframeAnimation<Float, Float> textSizeAnimation;
    private BaseKeyframeAnimation<Float, Float> textSizeCallbackAnimation;
    private BaseKeyframeAnimation<Float, Float> trackingAnimation;
    private BaseKeyframeAnimation<Float, Float> trackingCallbackAnimation;
    private BaseKeyframeAnimation<Typeface, Typeface> typefaceCallbackAnimation;

    TextLayer(LottieDrawable lottieDrawable, Layer layer) {
        super(lottieDrawable, layer);
        this.stringBuilder = new StringBuilder(2);
        this.rectF = new RectF();
        this.matrix = new Matrix();
        int i = 1;
        this.fillPaint = new Paint(i) { // from class: com.airbnb.lottie.model.layer.TextLayer.1
            {
                setStyle(Paint.Style.FILL);
            }
        };
        this.strokePaint = new Paint(i) { // from class: com.airbnb.lottie.model.layer.TextLayer.2
            {
                setStyle(Paint.Style.STROKE);
            }
        };
        this.contentsForCharacter = new HashMap();
        this.codePointCache = new LongSparseArray<>();
        this.lottieDrawable = lottieDrawable;
        this.composition = layer.getComposition();
        TextKeyframeAnimation textKeyframeAnimationCreateAnimation = layer.getText().createAnimation();
        this.textAnimation = textKeyframeAnimationCreateAnimation;
        textKeyframeAnimationCreateAnimation.addUpdateListener(this);
        addAnimation(textKeyframeAnimationCreateAnimation);
        AnimatableTextProperties textProperties = layer.getTextProperties();
        if (textProperties != null && textProperties.color != null) {
            BaseKeyframeAnimation<Integer, Integer> baseKeyframeAnimationCreateAnimation = textProperties.color.createAnimation();
            this.colorAnimation = baseKeyframeAnimationCreateAnimation;
            baseKeyframeAnimationCreateAnimation.addUpdateListener(this);
            addAnimation(this.colorAnimation);
        }
        if (textProperties != null && textProperties.stroke != null) {
            BaseKeyframeAnimation<Integer, Integer> baseKeyframeAnimationCreateAnimation2 = textProperties.stroke.createAnimation();
            this.strokeColorAnimation = baseKeyframeAnimationCreateAnimation2;
            baseKeyframeAnimationCreateAnimation2.addUpdateListener(this);
            addAnimation(this.strokeColorAnimation);
        }
        if (textProperties != null && textProperties.strokeWidth != null) {
            BaseKeyframeAnimation<Float, Float> baseKeyframeAnimationCreateAnimation3 = textProperties.strokeWidth.createAnimation();
            this.strokeWidthAnimation = baseKeyframeAnimationCreateAnimation3;
            baseKeyframeAnimationCreateAnimation3.addUpdateListener(this);
            addAnimation(this.strokeWidthAnimation);
        }
        if (textProperties == null || textProperties.tracking == null) {
            return;
        }
        BaseKeyframeAnimation<Float, Float> baseKeyframeAnimationCreateAnimation4 = textProperties.tracking.createAnimation();
        this.trackingAnimation = baseKeyframeAnimationCreateAnimation4;
        baseKeyframeAnimationCreateAnimation4.addUpdateListener(this);
        addAnimation(this.trackingAnimation);
    }

    @Override // com.airbnb.lottie.model.layer.BaseLayer, com.airbnb.lottie.animation.content.DrawingContent
    public void getBounds(RectF rectF, Matrix matrix, boolean z) {
        super.getBounds(rectF, matrix, z);
        rectF.set(0.0f, 0.0f, this.composition.getBounds().width(), this.composition.getBounds().height());
    }

    @Override // com.airbnb.lottie.model.layer.BaseLayer
    void drawLayer(Canvas canvas, Matrix matrix, int i) {
        canvas.save();
        if (!this.lottieDrawable.useTextGlyphs()) {
            canvas.concat(matrix);
        }
        DocumentData value = this.textAnimation.getValue();
        Font font = this.composition.getFonts().get(value.fontName);
        if (font == null) {
            canvas.restore();
            return;
        }
        BaseKeyframeAnimation<Integer, Integer> baseKeyframeAnimation = this.colorCallbackAnimation;
        if (baseKeyframeAnimation != null) {
            this.fillPaint.setColor(baseKeyframeAnimation.getValue().intValue());
        } else {
            BaseKeyframeAnimation<Integer, Integer> baseKeyframeAnimation2 = this.colorAnimation;
            if (baseKeyframeAnimation2 != null) {
                this.fillPaint.setColor(baseKeyframeAnimation2.getValue().intValue());
            } else {
                this.fillPaint.setColor(value.color);
            }
        }
        BaseKeyframeAnimation<Integer, Integer> baseKeyframeAnimation3 = this.strokeColorCallbackAnimation;
        if (baseKeyframeAnimation3 != null) {
            this.strokePaint.setColor(baseKeyframeAnimation3.getValue().intValue());
        } else {
            BaseKeyframeAnimation<Integer, Integer> baseKeyframeAnimation4 = this.strokeColorAnimation;
            if (baseKeyframeAnimation4 != null) {
                this.strokePaint.setColor(baseKeyframeAnimation4.getValue().intValue());
            } else {
                this.strokePaint.setColor(value.strokeColor);
            }
        }
        int iIntValue = ((this.transform.getOpacity() == null ? 100 : this.transform.getOpacity().getValue().intValue()) * 255) / 100;
        this.fillPaint.setAlpha(iIntValue);
        this.strokePaint.setAlpha(iIntValue);
        BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation5 = this.strokeWidthCallbackAnimation;
        if (baseKeyframeAnimation5 != null) {
            this.strokePaint.setStrokeWidth(baseKeyframeAnimation5.getValue().floatValue());
        } else {
            BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation6 = this.strokeWidthAnimation;
            if (baseKeyframeAnimation6 != null) {
                this.strokePaint.setStrokeWidth(baseKeyframeAnimation6.getValue().floatValue());
            } else {
                this.strokePaint.setStrokeWidth(value.strokeWidth * Utils.dpScale() * Utils.getScale(matrix));
            }
        }
        if (this.lottieDrawable.useTextGlyphs()) {
            drawTextGlyphs(value, matrix, font, canvas);
        } else {
            drawTextWithFont(value, font, matrix, canvas);
        }
        canvas.restore();
    }

    private void drawTextGlyphs(DocumentData documentData, Matrix matrix, Font font, Canvas canvas) {
        float fFloatValue;
        BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation = this.textSizeCallbackAnimation;
        if (baseKeyframeAnimation != null) {
            fFloatValue = baseKeyframeAnimation.getValue().floatValue();
        } else {
            BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation2 = this.textSizeAnimation;
            if (baseKeyframeAnimation2 != null) {
                fFloatValue = baseKeyframeAnimation2.getValue().floatValue();
            } else {
                fFloatValue = documentData.size;
            }
        }
        float f = fFloatValue / 100.0f;
        float scale = Utils.getScale(matrix);
        String str = documentData.text;
        float fDpScale = documentData.lineHeight * Utils.dpScale();
        List<String> textLines = getTextLines(str);
        int size = textLines.size();
        for (int i = 0; i < size; i++) {
            String str2 = textLines.get(i);
            float textLineWidthForGlyphs = getTextLineWidthForGlyphs(str2, font, f, scale);
            canvas.save();
            applyJustification(documentData.justification, canvas, textLineWidthForGlyphs);
            canvas.translate(0.0f, (i * fDpScale) - (((size - 1) * fDpScale) / 2.0f));
            drawGlyphTextLine(str2, documentData, matrix, font, canvas, scale, f);
            canvas.restore();
        }
    }

    private void drawGlyphTextLine(String str, DocumentData documentData, Matrix matrix, Font font, Canvas canvas, float f, float f2) {
        float fFloatValue;
        for (int i = 0; i < str.length(); i++) {
            FontCharacter fontCharacter = this.composition.getCharacters().get(FontCharacter.hashFor(str.charAt(i), font.getFamily(), font.getStyle()));
            if (fontCharacter != null) {
                drawCharacterAsGlyph(fontCharacter, matrix, f2, documentData, canvas);
                float width = ((float) fontCharacter.getWidth()) * f2 * Utils.dpScale() * f;
                float f3 = documentData.tracking / 10.0f;
                BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation = this.trackingCallbackAnimation;
                if (baseKeyframeAnimation != null) {
                    fFloatValue = baseKeyframeAnimation.getValue().floatValue();
                } else {
                    BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation2 = this.trackingAnimation;
                    if (baseKeyframeAnimation2 != null) {
                        fFloatValue = baseKeyframeAnimation2.getValue().floatValue();
                    }
                    canvas.translate(width + (f3 * f), 0.0f);
                }
                f3 += fFloatValue;
                canvas.translate(width + (f3 * f), 0.0f);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:24:0x009a A[LOOP:0: B:23:0x0098->B:24:0x009a, LOOP_END] */
    private void drawTextWithFont(DocumentData documentData, Font font, Matrix matrix, Canvas canvas) {
        float fFloatValue;
        float fFloatValue2;
        float fDpScale;
        List<String> textLines;
        int size;
        int i;
        Typeface typeface = getTypeface(font);
        if (typeface == null) {
            return;
        }
        String textInternal = documentData.text;
        TextDelegate textDelegate = this.lottieDrawable.getTextDelegate();
        if (textDelegate != null) {
            textInternal = textDelegate.getTextInternal(textInternal);
        }
        this.fillPaint.setTypeface(typeface);
        BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation = this.textSizeCallbackAnimation;
        if (baseKeyframeAnimation != null) {
            fFloatValue = baseKeyframeAnimation.getValue().floatValue();
        } else {
            BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation2 = this.textSizeAnimation;
            if (baseKeyframeAnimation2 != null) {
                fFloatValue = baseKeyframeAnimation2.getValue().floatValue();
            } else {
                fFloatValue = documentData.size;
            }
        }
        this.fillPaint.setTextSize(Utils.dpScale() * fFloatValue);
        this.strokePaint.setTypeface(this.fillPaint.getTypeface());
        this.strokePaint.setTextSize(this.fillPaint.getTextSize());
        float fDpScale2 = documentData.lineHeight * Utils.dpScale();
        float f = documentData.tracking / 10.0f;
        BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation3 = this.trackingCallbackAnimation;
        if (baseKeyframeAnimation3 != null) {
            fFloatValue2 = baseKeyframeAnimation3.getValue().floatValue();
        } else {
            BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation4 = this.trackingAnimation;
            if (baseKeyframeAnimation4 != null) {
                fFloatValue2 = baseKeyframeAnimation4.getValue().floatValue();
            }
            fDpScale = ((f * Utils.dpScale()) * fFloatValue) / 100.0f;
            textLines = getTextLines(textInternal);
            size = textLines.size();
            for (i = 0; i < size; i++) {
                String str = textLines.get(i);
                float fMeasureText = this.strokePaint.measureText(str) + ((str.length() - 1) * fDpScale);
                canvas.save();
                applyJustification(documentData.justification, canvas, fMeasureText);
                canvas.translate(0.0f, (i * fDpScale2) - (((size - 1) * fDpScale2) / 2.0f));
                drawFontTextLine(str, documentData, canvas, fDpScale);
                canvas.restore();
            }
        }
        f += fFloatValue2;
        fDpScale = ((f * Utils.dpScale()) * fFloatValue) / 100.0f;
        textLines = getTextLines(textInternal);
        size = textLines.size();
        while (i < size) {
            String str2 = textLines.get(i);
            float fMeasureText2 = this.strokePaint.measureText(str2) + ((str2.length() - 1) * fDpScale);
            canvas.save();
            applyJustification(documentData.justification, canvas, fMeasureText2);
            canvas.translate(0.0f, (i * fDpScale2) - (((size - 1) * fDpScale2) / 2.0f));
            drawFontTextLine(str2, documentData, canvas, fDpScale);
            canvas.restore();
        }
    }

    private Typeface getTypeface(Font font) {
        Typeface value;
        BaseKeyframeAnimation<Typeface, Typeface> baseKeyframeAnimation = this.typefaceCallbackAnimation;
        if (baseKeyframeAnimation != null && (value = baseKeyframeAnimation.getValue()) != null) {
            return value;
        }
        Typeface typeface = this.lottieDrawable.getTypeface(font.getFamily(), font.getStyle());
        return typeface != null ? typeface : font.getTypeface();
    }

    private List<String> getTextLines(String str) {
        return Arrays.asList(str.replaceAll("\r\n", "\r").replaceAll("\n", "\r").split("\r"));
    }

    private void drawFontTextLine(String str, DocumentData documentData, Canvas canvas, float f) {
        int length = 0;
        while (length < str.length()) {
            String strCodePointToString = codePointToString(str, length);
            length += strCodePointToString.length();
            drawCharacterFromFont(strCodePointToString, documentData, canvas);
            canvas.translate(this.fillPaint.measureText(strCodePointToString) + f, 0.0f);
        }
    }

    private float getTextLineWidthForGlyphs(String str, Font font, float f, float f2) {
        float width = 0.0f;
        for (int i = 0; i < str.length(); i++) {
            FontCharacter fontCharacter = this.composition.getCharacters().get(FontCharacter.hashFor(str.charAt(i), font.getFamily(), font.getStyle()));
            if (fontCharacter != null) {
                width = (float) (((double) width) + (fontCharacter.getWidth() * ((double) f) * ((double) Utils.dpScale()) * ((double) f2)));
            }
        }
        return width;
    }

    /* JADX INFO: renamed from: com.airbnb.lottie.model.layer.TextLayer$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$airbnb$lottie$model$DocumentData$Justification;

        static {
            int[] iArr = new int[DocumentData.Justification.values().length];
            $SwitchMap$com$airbnb$lottie$model$DocumentData$Justification = iArr;
            try {
                iArr[DocumentData.Justification.LEFT_ALIGN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$airbnb$lottie$model$DocumentData$Justification[DocumentData.Justification.RIGHT_ALIGN.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$airbnb$lottie$model$DocumentData$Justification[DocumentData.Justification.CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    private void applyJustification(DocumentData.Justification justification, Canvas canvas, float f) {
        int i = AnonymousClass3.$SwitchMap$com$airbnb$lottie$model$DocumentData$Justification[justification.ordinal()];
        if (i == 2) {
            canvas.translate(-f, 0.0f);
        } else {
            if (i != 3) {
                return;
            }
            canvas.translate((-f) / 2.0f, 0.0f);
        }
    }

    private void drawCharacterAsGlyph(FontCharacter fontCharacter, Matrix matrix, float f, DocumentData documentData, Canvas canvas) {
        List<ContentGroup> contentsForCharacter = getContentsForCharacter(fontCharacter);
        for (int i = 0; i < contentsForCharacter.size(); i++) {
            Path path = contentsForCharacter.get(i).getPath();
            path.computeBounds(this.rectF, false);
            this.matrix.set(matrix);
            this.matrix.preTranslate(0.0f, (-documentData.baselineShift) * Utils.dpScale());
            this.matrix.preScale(f, f);
            path.transform(this.matrix);
            if (documentData.strokeOverFill) {
                drawGlyph(path, this.fillPaint, canvas);
                drawGlyph(path, this.strokePaint, canvas);
            } else {
                drawGlyph(path, this.strokePaint, canvas);
                drawGlyph(path, this.fillPaint, canvas);
            }
        }
    }

    private void drawGlyph(Path path, Paint paint, Canvas canvas) {
        if (paint.getColor() == 0) {
            return;
        }
        if (paint.getStyle() == Paint.Style.STROKE && paint.getStrokeWidth() == 0.0f) {
            return;
        }
        canvas.drawPath(path, paint);
    }

    private void drawCharacterFromFont(String str, DocumentData documentData, Canvas canvas) {
        if (documentData.strokeOverFill) {
            drawCharacter(str, this.fillPaint, canvas);
            drawCharacter(str, this.strokePaint, canvas);
        } else {
            drawCharacter(str, this.strokePaint, canvas);
            drawCharacter(str, this.fillPaint, canvas);
        }
    }

    private void drawCharacter(String str, Paint paint, Canvas canvas) {
        if (paint.getColor() == 0) {
            return;
        }
        if (paint.getStyle() == Paint.Style.STROKE && paint.getStrokeWidth() == 0.0f) {
            return;
        }
        canvas.drawText(str, 0, str.length(), 0.0f, 0.0f, paint);
    }

    private List<ContentGroup> getContentsForCharacter(FontCharacter fontCharacter) {
        if (this.contentsForCharacter.containsKey(fontCharacter)) {
            return this.contentsForCharacter.get(fontCharacter);
        }
        List<ShapeGroup> shapes = fontCharacter.getShapes();
        int size = shapes.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            arrayList.add(new ContentGroup(this.lottieDrawable, this, shapes.get(i)));
        }
        this.contentsForCharacter.put(fontCharacter, arrayList);
        return arrayList;
    }

    private String codePointToString(String str, int i) {
        int iCodePointAt = str.codePointAt(i);
        int iCharCount = Character.charCount(iCodePointAt) + i;
        while (iCharCount < str.length()) {
            int iCodePointAt2 = str.codePointAt(iCharCount);
            if (!isModifier(iCodePointAt2)) {
                break;
            }
            iCharCount += Character.charCount(iCodePointAt2);
            iCodePointAt = (iCodePointAt * 31) + iCodePointAt2;
        }
        long j = iCodePointAt;
        if (this.codePointCache.containsKey(j)) {
            return this.codePointCache.get(j);
        }
        this.stringBuilder.setLength(0);
        while (i < iCharCount) {
            int iCodePointAt3 = str.codePointAt(i);
            this.stringBuilder.appendCodePoint(iCodePointAt3);
            i += Character.charCount(iCodePointAt3);
        }
        String string = this.stringBuilder.toString();
        this.codePointCache.put(j, string);
        return string;
    }

    private boolean isModifier(int i) {
        return Character.getType(i) == 16 || Character.getType(i) == 27 || Character.getType(i) == 6 || Character.getType(i) == 28 || Character.getType(i) == 19;
    }

    @Override // com.airbnb.lottie.model.layer.BaseLayer, com.airbnb.lottie.model.KeyPathElement
    public <T> void addValueCallback(T t, LottieValueCallback<T> lottieValueCallback) {
        super.addValueCallback(t, lottieValueCallback);
        if (t == LottieProperty.COLOR) {
            BaseKeyframeAnimation<Integer, Integer> baseKeyframeAnimation = this.colorCallbackAnimation;
            if (baseKeyframeAnimation != null) {
                removeAnimation(baseKeyframeAnimation);
            }
            if (lottieValueCallback == null) {
                this.colorCallbackAnimation = null;
                return;
            }
            ValueCallbackKeyframeAnimation valueCallbackKeyframeAnimation = new ValueCallbackKeyframeAnimation(lottieValueCallback);
            this.colorCallbackAnimation = valueCallbackKeyframeAnimation;
            valueCallbackKeyframeAnimation.addUpdateListener(this);
            addAnimation(this.colorCallbackAnimation);
            return;
        }
        if (t == LottieProperty.STROKE_COLOR) {
            BaseKeyframeAnimation<Integer, Integer> baseKeyframeAnimation2 = this.strokeColorCallbackAnimation;
            if (baseKeyframeAnimation2 != null) {
                removeAnimation(baseKeyframeAnimation2);
            }
            if (lottieValueCallback == null) {
                this.strokeColorCallbackAnimation = null;
                return;
            }
            ValueCallbackKeyframeAnimation valueCallbackKeyframeAnimation2 = new ValueCallbackKeyframeAnimation(lottieValueCallback);
            this.strokeColorCallbackAnimation = valueCallbackKeyframeAnimation2;
            valueCallbackKeyframeAnimation2.addUpdateListener(this);
            addAnimation(this.strokeColorCallbackAnimation);
            return;
        }
        if (t == LottieProperty.STROKE_WIDTH) {
            BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation3 = this.strokeWidthCallbackAnimation;
            if (baseKeyframeAnimation3 != null) {
                removeAnimation(baseKeyframeAnimation3);
            }
            if (lottieValueCallback == null) {
                this.strokeWidthCallbackAnimation = null;
                return;
            }
            ValueCallbackKeyframeAnimation valueCallbackKeyframeAnimation3 = new ValueCallbackKeyframeAnimation(lottieValueCallback);
            this.strokeWidthCallbackAnimation = valueCallbackKeyframeAnimation3;
            valueCallbackKeyframeAnimation3.addUpdateListener(this);
            addAnimation(this.strokeWidthCallbackAnimation);
            return;
        }
        if (t == LottieProperty.TEXT_TRACKING) {
            BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation4 = this.trackingCallbackAnimation;
            if (baseKeyframeAnimation4 != null) {
                removeAnimation(baseKeyframeAnimation4);
            }
            if (lottieValueCallback == null) {
                this.trackingCallbackAnimation = null;
                return;
            }
            ValueCallbackKeyframeAnimation valueCallbackKeyframeAnimation4 = new ValueCallbackKeyframeAnimation(lottieValueCallback);
            this.trackingCallbackAnimation = valueCallbackKeyframeAnimation4;
            valueCallbackKeyframeAnimation4.addUpdateListener(this);
            addAnimation(this.trackingCallbackAnimation);
            return;
        }
        if (t == LottieProperty.TEXT_SIZE) {
            BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation5 = this.textSizeCallbackAnimation;
            if (baseKeyframeAnimation5 != null) {
                removeAnimation(baseKeyframeAnimation5);
            }
            if (lottieValueCallback == null) {
                this.textSizeCallbackAnimation = null;
                return;
            }
            ValueCallbackKeyframeAnimation valueCallbackKeyframeAnimation5 = new ValueCallbackKeyframeAnimation(lottieValueCallback);
            this.textSizeCallbackAnimation = valueCallbackKeyframeAnimation5;
            valueCallbackKeyframeAnimation5.addUpdateListener(this);
            addAnimation(this.textSizeCallbackAnimation);
            return;
        }
        if (t == LottieProperty.TYPEFACE) {
            BaseKeyframeAnimation<Typeface, Typeface> baseKeyframeAnimation6 = this.typefaceCallbackAnimation;
            if (baseKeyframeAnimation6 != null) {
                removeAnimation(baseKeyframeAnimation6);
            }
            if (lottieValueCallback == null) {
                this.typefaceCallbackAnimation = null;
                return;
            }
            ValueCallbackKeyframeAnimation valueCallbackKeyframeAnimation6 = new ValueCallbackKeyframeAnimation(lottieValueCallback);
            this.typefaceCallbackAnimation = valueCallbackKeyframeAnimation6;
            valueCallbackKeyframeAnimation6.addUpdateListener(this);
            addAnimation(this.typefaceCallbackAnimation);
        }
    }
}
