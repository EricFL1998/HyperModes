package com.airbnb.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.animation.content.ContentGroup;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.model.content.BlurEffect;
import com.airbnb.lottie.model.content.ShapeGroup;
import com.airbnb.lottie.parser.DropShadowEffect;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShapeLayer extends BaseLayer {
    private final CompositionLayer compositionLayer;
    private final ContentGroup contentGroup;

    ShapeLayer(LottieDrawable lottieDrawable, Layer layer, CompositionLayer compositionLayer) {
        super(lottieDrawable, layer);
        this.compositionLayer = compositionLayer;
        ContentGroup contentGroup = new ContentGroup(lottieDrawable, this, new ShapeGroup("__container", layer.getShapes(), false));
        this.contentGroup = contentGroup;
        contentGroup.setContents(Collections.emptyList(), Collections.emptyList());
    }

    @Override // com.airbnb.lottie.model.layer.BaseLayer
    void drawLayer(Canvas canvas, Matrix matrix, int i) {
        this.contentGroup.draw(canvas, matrix, i);
    }

    @Override // com.airbnb.lottie.model.layer.BaseLayer, com.airbnb.lottie.animation.content.DrawingContent
    public void getBounds(RectF rectF, Matrix matrix, boolean z) {
        super.getBounds(rectF, matrix, z);
        this.contentGroup.getBounds(rectF, this.boundsMatrix, z);
    }

    @Override // com.airbnb.lottie.model.layer.BaseLayer
    public BlurEffect getBlurEffect() {
        BlurEffect blurEffect = super.getBlurEffect();
        return blurEffect != null ? blurEffect : this.compositionLayer.getBlurEffect();
    }

    @Override // com.airbnb.lottie.model.layer.BaseLayer
    public DropShadowEffect getDropShadowEffect() {
        DropShadowEffect dropShadowEffect = super.getDropShadowEffect();
        return dropShadowEffect != null ? dropShadowEffect : this.compositionLayer.getDropShadowEffect();
    }

    @Override // com.airbnb.lottie.model.layer.BaseLayer
    protected void resolveChildKeyPath(KeyPath keyPath, int i, List<KeyPath> list, KeyPath keyPath2) {
        this.contentGroup.resolveKeyPath(keyPath, i, list, keyPath2);
    }
}
