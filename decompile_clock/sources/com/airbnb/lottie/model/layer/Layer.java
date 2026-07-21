package com.airbnb.lottie.model.layer;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableTextFrame;
import com.airbnb.lottie.model.animatable.AnimatableTextProperties;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.content.BlurEffect;
import com.airbnb.lottie.model.content.ContentModel;
import com.airbnb.lottie.model.content.Mask;
import com.airbnb.lottie.parser.DropShadowEffect;
import com.airbnb.lottie.value.Keyframe;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class Layer {
    private final BlurEffect blurEffect;
    private final LottieComposition composition;
    private final DropShadowEffect dropShadowEffect;
    private final boolean hidden;
    private final List<Keyframe<Float>> inOutKeyframes;
    private final long layerId;
    private final String layerName;
    private final LayerType layerType;
    private final List<Mask> masks;
    private final MatteType matteType;
    private final long parentId;
    private final int preCompHeight;
    private final int preCompWidth;
    private final String refId;
    private final List<ContentModel> shapes;
    private final int solidColor;
    private final int solidHeight;
    private final int solidWidth;
    private final float startFrame;
    private final AnimatableTextFrame text;
    private final AnimatableTextProperties textProperties;
    private final AnimatableFloatValue timeRemapping;
    private final float timeStretch;
    private final AnimatableTransform transform;

    public enum LayerType {
        PRE_COMP,
        SOLID,
        IMAGE,
        NULL,
        SHAPE,
        TEXT,
        UNKNOWN
    }

    public enum MatteType {
        NONE,
        ADD,
        INVERT,
        LUMA,
        LUMA_INVERTED,
        UNKNOWN
    }

    public Layer(List<ContentModel> list, LottieComposition lottieComposition, String str, long j, LayerType layerType, long j2, String str2, List<Mask> list2, AnimatableTransform animatableTransform, int i, int i2, int i3, float f, float f2, int i4, int i5, AnimatableTextFrame animatableTextFrame, AnimatableTextProperties animatableTextProperties, List<Keyframe<Float>> list3, MatteType matteType, AnimatableFloatValue animatableFloatValue, boolean z, BlurEffect blurEffect, DropShadowEffect dropShadowEffect) {
        this.shapes = list;
        this.composition = lottieComposition;
        this.layerName = str;
        this.layerId = j;
        this.layerType = layerType;
        this.parentId = j2;
        this.refId = str2;
        this.masks = list2;
        this.transform = animatableTransform;
        this.solidWidth = i;
        this.solidHeight = i2;
        this.solidColor = i3;
        this.timeStretch = f;
        this.startFrame = f2;
        this.preCompWidth = i4;
        this.preCompHeight = i5;
        this.text = animatableTextFrame;
        this.textProperties = animatableTextProperties;
        this.inOutKeyframes = list3;
        this.matteType = matteType;
        this.timeRemapping = animatableFloatValue;
        this.hidden = z;
        this.blurEffect = blurEffect;
        this.dropShadowEffect = dropShadowEffect;
    }

    LottieComposition getComposition() {
        return this.composition;
    }

    float getTimeStretch() {
        return this.timeStretch;
    }

    float getStartProgress() {
        return this.startFrame / this.composition.getDurationFrames();
    }

    List<Keyframe<Float>> getInOutKeyframes() {
        return this.inOutKeyframes;
    }

    public long getId() {
        return this.layerId;
    }

    String getName() {
        return this.layerName;
    }

    String getRefId() {
        return this.refId;
    }

    int getPreCompWidth() {
        return this.preCompWidth;
    }

    int getPreCompHeight() {
        return this.preCompHeight;
    }

    List<Mask> getMasks() {
        return this.masks;
    }

    public LayerType getLayerType() {
        return this.layerType;
    }

    MatteType getMatteType() {
        return this.matteType;
    }

    long getParentId() {
        return this.parentId;
    }

    List<ContentModel> getShapes() {
        return this.shapes;
    }

    AnimatableTransform getTransform() {
        return this.transform;
    }

    int getSolidColor() {
        return this.solidColor;
    }

    int getSolidHeight() {
        return this.solidHeight;
    }

    int getSolidWidth() {
        return this.solidWidth;
    }

    AnimatableTextFrame getText() {
        return this.text;
    }

    AnimatableTextProperties getTextProperties() {
        return this.textProperties;
    }

    AnimatableFloatValue getTimeRemapping() {
        return this.timeRemapping;
    }

    public String toString() {
        return toString("");
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public BlurEffect getBlurEffect() {
        return this.blurEffect;
    }

    public DropShadowEffect getDropShadowEffect() {
        return this.dropShadowEffect;
    }

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str).append(getName()).append("\n");
        Layer layerLayerModelForId = this.composition.layerModelForId(getParentId());
        if (layerLayerModelForId != null) {
            sb.append("\t\tParents: ").append(layerLayerModelForId.getName());
            Layer layerLayerModelForId2 = this.composition.layerModelForId(layerLayerModelForId.getParentId());
            while (layerLayerModelForId2 != null) {
                sb.append("->").append(layerLayerModelForId2.getName());
                layerLayerModelForId2 = this.composition.layerModelForId(layerLayerModelForId2.getParentId());
            }
            sb.append(str).append("\n");
        }
        if (!getMasks().isEmpty()) {
            sb.append(str).append("\tMasks: ").append(getMasks().size()).append("\n");
        }
        if (getSolidWidth() != 0 && getSolidHeight() != 0) {
            sb.append(str).append("\tBackground: ").append(String.format(Locale.US, "%dx%d %X\n", Integer.valueOf(getSolidWidth()), Integer.valueOf(getSolidHeight()), Integer.valueOf(getSolidColor())));
        }
        if (!this.shapes.isEmpty()) {
            sb.append(str).append("\tShapes:\n");
            Iterator<ContentModel> it = this.shapes.iterator();
            while (it.hasNext()) {
                sb.append(str).append("\t\t").append(it.next()).append("\n");
            }
        }
        return sb.toString();
    }
}
