package miuix.theme.token;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public class MaterialToken implements Parcelable {
    public static final Parcelable.Creator<MaterialToken> CREATOR = new Parcelable.Creator<MaterialToken>() { // from class: miuix.theme.token.MaterialToken.1
        @Override // android.os.Parcelable.Creator
        public MaterialToken createFromParcel(Parcel parcel) {
            return new MaterialToken(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public MaterialToken[] newArray(int i) {
            return new MaterialToken[i];
        }
    };
    public int[] blendColors;
    public float[] blendExtraParams;
    public int[] blendModes;
    public float bloomStrokeColorA;
    public float bloomStrokeColorB;
    public float bloomStrokeColorG;
    public float bloomStrokeColorR;
    public float bloomStrokeGradientDegree;
    public float bloomStrokeWidth;
    public int blurContainerMode;
    public int blurElementMode;
    public float[] blurExtraParams;
    public int blurRadius;
    public int blurSubType;
    public int blurType;
    public int enableBloomStroke;
    public int enableBlur;
    public int enableColorBlend;
    public int enableShadow;
    public int[] fallbackBlendColors;
    public float[] fallbackBlendExtraParams;
    public int[] fallbackBlendModes;
    public int hasFallbackColorBlend;
    public float normalWidth;
    public int shadowColor;
    public float shadowDispersion;
    public float shadowOffsetX;
    public float shadowOffsetY;
    public float shadowRadius;
    public float source1A;
    public float source1B;
    public float source1G;
    public float source1R;
    public float source1X;
    public float source1Y;
    public float source1Z;
    public float source2A;
    public float source2B;
    public float source2G;
    public float source2R;
    public float source2X;
    public float source2Y;
    public float source2Z;
    public String token;
    public String tokenVariant;
    public final int version;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MaterialToken(Parcel parcel) {
        this.enableColorBlend = 0;
        this.hasFallbackColorBlend = 0;
        this.enableBlur = 0;
        this.enableShadow = 0;
        this.shadowOffsetX = 0.0f;
        this.shadowOffsetY = 0.0f;
        this.shadowRadius = 0.0f;
        this.shadowDispersion = 1.0f;
        this.enableBloomStroke = 0;
        this.bloomStrokeWidth = 0.0f;
        this.bloomStrokeGradientDegree = 0.0f;
        this.bloomStrokeColorR = 0.0f;
        this.bloomStrokeColorG = 0.0f;
        this.bloomStrokeColorB = 0.0f;
        this.bloomStrokeColorA = 0.0f;
        this.normalWidth = 0.0f;
        this.source1X = 0.0f;
        this.source1Y = 0.0f;
        this.source1Z = 0.0f;
        this.source1R = 0.0f;
        this.source1G = 0.0f;
        this.source1B = 0.0f;
        this.source1A = 0.0f;
        this.source2X = 0.0f;
        this.source2Y = 0.0f;
        this.source2Z = 0.0f;
        this.source2R = 0.0f;
        this.source2G = 0.0f;
        this.source2B = 0.0f;
        this.source2A = 0.0f;
        this.version = parcel.readInt();
        this.token = parcel.readString();
        this.tokenVariant = parcel.readString();
        int i = parcel.readInt();
        this.enableColorBlend = i;
        if (i > 0) {
            int i2 = parcel.readInt();
            if (i2 > 0) {
                int[] iArr = new int[i2];
                this.blendColors = iArr;
                parcel.readIntArray(iArr);
            }
            int i3 = parcel.readInt();
            if (i3 > 0) {
                int[] iArr2 = new int[i3];
                this.blendModes = iArr2;
                parcel.readIntArray(iArr2);
            }
            int i4 = parcel.readInt();
            if (i4 > 0) {
                float[] fArr = new float[i4];
                this.blendExtraParams = fArr;
                parcel.readFloatArray(fArr);
            }
            int i5 = parcel.readInt();
            this.hasFallbackColorBlend = i5;
            if (i5 > 0) {
                int i6 = parcel.readInt();
                if (i6 > 0) {
                    int[] iArr3 = new int[i6];
                    this.fallbackBlendColors = iArr3;
                    parcel.readIntArray(iArr3);
                }
                int i7 = parcel.readInt();
                if (i7 > 0) {
                    int[] iArr4 = new int[i7];
                    this.fallbackBlendModes = iArr4;
                    parcel.readIntArray(iArr4);
                }
                int i8 = parcel.readInt();
                if (i8 > 0) {
                    float[] fArr2 = new float[i8];
                    this.fallbackBlendExtraParams = fArr2;
                    parcel.readFloatArray(fArr2);
                }
            }
        }
        int i9 = parcel.readInt();
        this.enableBlur = i9;
        if (i9 > 0) {
            this.blurContainerMode = parcel.readInt();
            this.blurElementMode = parcel.readInt();
            this.blurType = parcel.readInt();
            this.blurRadius = parcel.readInt();
            this.blurSubType = parcel.readInt();
            int i10 = parcel.readInt();
            if (i10 > 0) {
                float[] fArr3 = new float[i10];
                this.blurExtraParams = fArr3;
                parcel.readFloatArray(fArr3);
            } else {
                this.blurExtraParams = null;
            }
        }
        int i11 = parcel.readInt();
        this.enableShadow = i11;
        if (i11 > 0) {
            this.shadowColor = parcel.readInt();
            this.shadowOffsetX = parcel.readFloat();
            this.shadowOffsetY = parcel.readFloat();
            this.shadowRadius = parcel.readFloat();
            this.shadowDispersion = parcel.readFloat();
        }
        int i12 = parcel.readInt();
        this.enableBloomStroke = i12;
        if (i12 > 0) {
            this.bloomStrokeWidth = parcel.readFloat();
            this.bloomStrokeGradientDegree = parcel.readFloat();
            this.bloomStrokeColorR = parcel.readFloat();
            this.bloomStrokeColorG = parcel.readFloat();
            this.bloomStrokeColorB = parcel.readFloat();
            this.bloomStrokeColorA = parcel.readFloat();
            this.normalWidth = parcel.readFloat();
            this.source1X = parcel.readFloat();
            this.source1Y = parcel.readFloat();
            this.source1Z = parcel.readFloat();
            this.source1R = parcel.readFloat();
            this.source1G = parcel.readFloat();
            this.source1B = parcel.readFloat();
            this.source1A = parcel.readFloat();
            this.source2X = parcel.readFloat();
            this.source2Y = parcel.readFloat();
            this.source2Z = parcel.readFloat();
            this.source2R = parcel.readFloat();
            this.source2G = parcel.readFloat();
            this.source2B = parcel.readFloat();
            this.source2A = parcel.readFloat();
        }
    }

    private MaterialToken(int i) {
        this.enableColorBlend = 0;
        this.hasFallbackColorBlend = 0;
        this.enableBlur = 0;
        this.enableShadow = 0;
        this.shadowOffsetX = 0.0f;
        this.shadowOffsetY = 0.0f;
        this.shadowRadius = 0.0f;
        this.shadowDispersion = 1.0f;
        this.enableBloomStroke = 0;
        this.bloomStrokeWidth = 0.0f;
        this.bloomStrokeGradientDegree = 0.0f;
        this.bloomStrokeColorR = 0.0f;
        this.bloomStrokeColorG = 0.0f;
        this.bloomStrokeColorB = 0.0f;
        this.bloomStrokeColorA = 0.0f;
        this.normalWidth = 0.0f;
        this.source1X = 0.0f;
        this.source1Y = 0.0f;
        this.source1Z = 0.0f;
        this.source1R = 0.0f;
        this.source1G = 0.0f;
        this.source1B = 0.0f;
        this.source1A = 0.0f;
        this.source2X = 0.0f;
        this.source2Y = 0.0f;
        this.source2Z = 0.0f;
        this.source2R = 0.0f;
        this.source2G = 0.0f;
        this.source2B = 0.0f;
        this.source2A = 0.0f;
        this.version = i;
    }

    public String toString() {
        if (TextUtils.isEmpty(this.token)) {
            return "{no token name}@" + Integer.toHexString(hashCode());
        }
        if (TextUtils.isEmpty(this.tokenVariant)) {
            return this.token;
        }
        return this.token + "_" + this.tokenVariant;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        int[] iArr;
        int[] iArr2;
        parcel.writeInt(this.version);
        parcel.writeString(TextUtils.isEmpty(this.token) ? "" : this.token);
        parcel.writeString(TextUtils.isEmpty(this.tokenVariant) ? "" : this.tokenVariant);
        parcel.writeInt(this.enableColorBlend);
        if (this.enableColorBlend > 0) {
            int[] iArr3 = this.blendColors;
            if (iArr3 != null && iArr3.length > 0 && (iArr2 = this.blendModes) != null && iArr2.length > 0) {
                parcel.writeInt(iArr3.length);
                parcel.writeIntArray(this.blendColors);
                parcel.writeInt(this.blendModes.length);
                parcel.writeIntArray(this.blendModes);
                float[] fArr = this.blendExtraParams;
                if (fArr != null) {
                    parcel.writeInt(fArr.length);
                    float[] fArr2 = this.blendExtraParams;
                    if (fArr2.length > 0) {
                        parcel.writeFloatArray(fArr2);
                    }
                } else {
                    parcel.writeInt(0);
                }
            } else {
                parcel.writeInt(0);
                parcel.writeInt(0);
                parcel.writeInt(0);
            }
            parcel.writeInt(this.hasFallbackColorBlend);
            if (this.hasFallbackColorBlend > 0) {
                int[] iArr4 = this.fallbackBlendColors;
                if (iArr4 != null && iArr4.length > 0 && (iArr = this.fallbackBlendModes) != null && iArr.length > 0) {
                    parcel.writeInt(iArr4.length);
                    parcel.writeIntArray(this.fallbackBlendColors);
                    parcel.writeInt(this.fallbackBlendModes.length);
                    parcel.writeIntArray(this.fallbackBlendModes);
                    float[] fArr3 = this.fallbackBlendExtraParams;
                    if (fArr3 != null) {
                        parcel.writeInt(fArr3.length);
                        float[] fArr4 = this.fallbackBlendExtraParams;
                        if (fArr4.length > 0) {
                            parcel.writeFloatArray(fArr4);
                        }
                    } else {
                        parcel.writeInt(0);
                    }
                } else {
                    parcel.writeInt(0);
                    parcel.writeInt(0);
                    parcel.writeInt(0);
                }
            }
        }
        parcel.writeInt(this.enableBlur);
        if (this.enableBlur > 0) {
            parcel.writeInt(this.blurContainerMode);
            parcel.writeInt(this.blurElementMode);
            parcel.writeInt(this.blurType);
            parcel.writeInt(this.blurRadius);
            parcel.writeInt(this.blurSubType);
            float[] fArr5 = this.blurExtraParams;
            if (fArr5 != null) {
                parcel.writeInt(fArr5.length);
                float[] fArr6 = this.blurExtraParams;
                if (fArr6.length > 0) {
                    parcel.writeFloatArray(fArr6);
                }
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.enableShadow);
        if (this.enableShadow > 0) {
            parcel.writeInt(this.shadowColor);
            parcel.writeFloat(this.shadowOffsetX);
            parcel.writeFloat(this.shadowOffsetY);
            parcel.writeFloat(this.shadowRadius);
            parcel.writeFloat(this.shadowDispersion);
        }
        parcel.writeInt(this.enableBloomStroke);
        if (this.enableBloomStroke > 0) {
            parcel.writeFloat(this.bloomStrokeWidth);
            parcel.writeFloat(this.bloomStrokeGradientDegree);
            parcel.writeFloat(this.bloomStrokeColorR);
            parcel.writeFloat(this.bloomStrokeColorG);
            parcel.writeFloat(this.bloomStrokeColorB);
            parcel.writeFloat(this.bloomStrokeColorA);
            parcel.writeFloat(this.normalWidth);
            parcel.writeFloat(this.source1X);
            parcel.writeFloat(this.source1Y);
            parcel.writeFloat(this.source1Z);
            parcel.writeFloat(this.source1R);
            parcel.writeFloat(this.source1G);
            parcel.writeFloat(this.source1B);
            parcel.writeFloat(this.source1A);
            parcel.writeFloat(this.source2X);
            parcel.writeFloat(this.source2Y);
            parcel.writeFloat(this.source2Z);
            parcel.writeFloat(this.source2R);
            parcel.writeFloat(this.source2G);
            parcel.writeFloat(this.source2B);
            parcel.writeFloat(this.source2A);
        }
    }

    public static class Builder {
        private MaterialToken mToken;

        public Builder(int i) {
            this.mToken = new MaterialToken(i);
        }

        public Builder(int i, String str, String str2) {
            this.mToken = new MaterialToken(i);
            setTokenInfo(str, str2);
        }

        public Builder setTokenInfo(String str, String str2) {
            MaterialToken materialToken = this.mToken;
            if (str == null) {
                str = "";
            }
            materialToken.token = str;
            MaterialToken materialToken2 = this.mToken;
            if (str2 == null) {
                str2 = "";
            }
            materialToken2.tokenVariant = str2;
            return this;
        }

        public Builder setColorBlend(int[] iArr) {
            if (iArr != null && iArr.length >= 2) {
                this.mToken.enableColorBlend = 1;
                this.mToken.blendColors = new int[iArr.length / 2];
                this.mToken.blendModes = new int[iArr.length / 2];
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < iArr.length; i3++) {
                    if (i3 % 2 == 0) {
                        this.mToken.blendColors[i2] = iArr[i3];
                        i2++;
                    } else {
                        this.mToken.blendModes[i] = iArr[i3];
                        i++;
                    }
                }
            }
            return this;
        }

        public Builder setColorBlend(int[] iArr, int[] iArr2) {
            if (iArr != null && iArr.length >= 2) {
                this.mToken.enableColorBlend = 1;
                this.mToken.blendColors = new int[iArr.length / 2];
                this.mToken.blendModes = new int[iArr.length / 2];
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < iArr.length; i3++) {
                    if (i3 % 2 == 0) {
                        this.mToken.blendColors[i2] = iArr[i3];
                        i2++;
                    } else {
                        this.mToken.blendModes[i] = iArr[i3];
                        i++;
                    }
                }
                if (iArr2 != null && iArr2.length >= 2) {
                    this.mToken.hasFallbackColorBlend = 1;
                    this.mToken.fallbackBlendColors = new int[iArr2.length / 2];
                    this.mToken.fallbackBlendModes = new int[iArr2.length / 2];
                    int i4 = 0;
                    int i5 = 0;
                    for (int i6 = 0; i6 < iArr2.length; i6++) {
                        if (i6 % 2 == 0) {
                            this.mToken.fallbackBlendColors[i5] = iArr2[i6];
                            i5++;
                        } else {
                            this.mToken.fallbackBlendModes[i4] = iArr2[i6];
                            i4++;
                        }
                    }
                }
            }
            return this;
        }

        public Builder setColorBlend(ArrayList<Point> arrayList) {
            return setColorBlend(arrayList, (ArrayList<Point>) null);
        }

        public Builder setColorBlend(ArrayList<Point> arrayList, ArrayList<Point> arrayList2) {
            if (arrayList == null) {
                return this;
            }
            this.mToken.enableColorBlend = 1;
            this.mToken.blendColors = new int[arrayList.size()];
            this.mToken.blendModes = new int[arrayList.size()];
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                Point point = arrayList.get(i3);
                this.mToken.blendColors[i] = point.x;
                this.mToken.blendModes[i2] = point.y;
                i++;
                i2++;
            }
            if (arrayList2 != null) {
                this.mToken.hasFallbackColorBlend = 1;
                this.mToken.fallbackBlendColors = new int[arrayList2.size()];
                this.mToken.fallbackBlendModes = new int[arrayList2.size()];
                int i4 = 0;
                int i5 = 0;
                for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                    Point point2 = arrayList2.get(i6);
                    this.mToken.fallbackBlendColors[i4] = point2.x;
                    this.mToken.fallbackBlendModes[i5] = point2.y;
                    i4++;
                    i5++;
                }
            }
            return this;
        }

        public Builder setColorBlend(ColorBlendToken colorBlendToken) {
            if (colorBlendToken == null) {
                return this;
            }
            this.mToken.enableColorBlend = 1;
            this.mToken.blendColors = colorBlendToken.colors;
            this.mToken.blendModes = colorBlendToken.blendModes;
            this.mToken.blendExtraParams = colorBlendToken.extraBlendParams;
            if (colorBlendToken.fallbackColors != null) {
                this.mToken.hasFallbackColorBlend = 1;
                this.mToken.fallbackBlendColors = colorBlendToken.fallbackColors;
                this.mToken.fallbackBlendModes = colorBlendToken.fallbackBlendModes;
                this.mToken.fallbackBlendExtraParams = colorBlendToken.fallbackExtraBlendParams;
            } else {
                this.mToken.hasFallbackColorBlend = 0;
            }
            return this;
        }

        public Builder setElementBlur() {
            this.mToken.enableBlur = 1;
            this.mToken.blurContainerMode = 0;
            this.mToken.blurElementMode = 1;
            this.mToken.blurType = 0;
            this.mToken.blurRadius = -1;
            return this;
        }

        public Builder setElementBlur(int i) {
            this.mToken.enableBlur = 1;
            this.mToken.blurContainerMode = 0;
            this.mToken.blurElementMode = 1;
            this.mToken.blurType = 0;
            this.mToken.blurRadius = i;
            return this;
        }

        public Builder setContainerBlur(int i, int i2) {
            this.mToken.enableBlur = 1;
            this.mToken.blurContainerMode = i2;
            this.mToken.blurElementMode = 0;
            this.mToken.blurType = 0;
            this.mToken.blurRadius = i;
            return this;
        }

        public Builder setMaskBlur(int i) {
            this.mToken.enableBlur = 1;
            this.mToken.blurContainerMode = 1;
            this.mToken.blurElementMode = 1;
            this.mToken.blurType = 0;
            this.mToken.blurRadius = i;
            return this;
        }

        public Builder setBlur(int i, int i2, int i3, int i4) {
            this.mToken.enableBlur = 1;
            this.mToken.blurContainerMode = i;
            this.mToken.blurElementMode = i2;
            this.mToken.blurType = i3;
            this.mToken.blurRadius = i4;
            return this;
        }

        public Builder setShadow(ShadowToken shadowToken) {
            if (shadowToken == null) {
                return this;
            }
            this.mToken.enableShadow = 1;
            this.mToken.shadowColor = shadowToken.color;
            this.mToken.shadowOffsetX = shadowToken.offsetX;
            this.mToken.shadowOffsetY = shadowToken.offsetY;
            this.mToken.shadowRadius = shadowToken.radius;
            this.mToken.shadowDispersion = shadowToken.dispersion;
            return this;
        }

        public Builder setShadow(int i, float f, float f2, float f3, float f4) {
            this.mToken.enableShadow = 1;
            this.mToken.shadowColor = i;
            this.mToken.shadowOffsetX = f;
            this.mToken.shadowOffsetY = f2;
            this.mToken.shadowRadius = f3;
            this.mToken.shadowDispersion = f4;
            return this;
        }

        public Builder setBloomStroke(float[] fArr) {
            this.mToken.enableBloomStroke = 1;
            this.mToken.bloomStrokeWidth = fArr[0];
            this.mToken.bloomStrokeGradientDegree = fArr[1];
            this.mToken.bloomStrokeColorR = fArr[2];
            this.mToken.bloomStrokeColorG = fArr[3];
            this.mToken.bloomStrokeColorB = fArr[4];
            this.mToken.bloomStrokeColorA = fArr[5];
            this.mToken.normalWidth = fArr[6];
            this.mToken.source1X = fArr[7];
            this.mToken.source1Y = fArr[8];
            this.mToken.source1Z = fArr[9];
            this.mToken.source1R = fArr[10];
            this.mToken.source1G = fArr[11];
            this.mToken.source1B = fArr[12];
            this.mToken.source1A = fArr[13];
            this.mToken.source2X = fArr[14];
            this.mToken.source2Y = fArr[15];
            this.mToken.source2Z = fArr[16];
            this.mToken.source2R = fArr[17];
            this.mToken.source2G = fArr[18];
            this.mToken.source2B = fArr[19];
            this.mToken.source2A = fArr[20];
            return this;
        }

        public Builder setBloomStroke(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16, float f17, float f18, float f19, float f20, float f21) {
            this.mToken.enableBloomStroke = 1;
            this.mToken.bloomStrokeWidth = f;
            this.mToken.bloomStrokeColorR = f3;
            this.mToken.bloomStrokeColorG = f4;
            this.mToken.bloomStrokeColorB = f5;
            this.mToken.bloomStrokeColorA = f6;
            this.mToken.bloomStrokeGradientDegree = f2;
            this.mToken.normalWidth = f7;
            this.mToken.source1X = f8;
            this.mToken.source1Y = f9;
            this.mToken.source1Z = f10;
            this.mToken.source1R = f11;
            this.mToken.source1G = f12;
            this.mToken.source1B = f13;
            this.mToken.source1A = f14;
            this.mToken.source2X = f15;
            this.mToken.source2Y = f16;
            this.mToken.source2Z = f17;
            this.mToken.source2R = f18;
            this.mToken.source2G = f19;
            this.mToken.source2B = f20;
            this.mToken.source2A = f21;
            return this;
        }

        public MaterialToken build() {
            return this.mToken;
        }
    }
}
