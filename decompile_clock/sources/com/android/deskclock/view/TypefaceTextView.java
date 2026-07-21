package com.android.deskclock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.util.TypefaceFactory;
import com.android.deskclock.widget.LimitSizeTextView;

/* JADX INFO: loaded from: classes.dex */
public class TypefaceTextView extends LimitSizeTextView {
    public static final int MI_PRO_LIGHT = 8;
    public static final int MI_PRO_MEDIUM = 6;
    public static final int MI_PRO_NORMAL = 5;
    public static final int MI_PRO_REGULAR = 7;
    public static final int MI_TYPE_2019_40 = 1;
    public static final int MI_TYPE_2019_50 = 2;
    public static final int MI_TYPE_2019_60 = 3;
    public static final int MI_TYPE_2019_70 = 4;
    public static final int MI_TYPE_MI_SANS_RCFVF = 15;
    public static final int MI_TYPE_MONO_BOLD = 11;
    public static final int MI_TYPE_MONO_DEMIBOLD = 12;
    public static final int MI_TYPE_MONO_MEDIUM = 10;
    public static final int MI_TYPE_MONO_NORMAL = 9;
    public static final int MI_TYPE_MONO_REGULAR = 14;
    public static final int MI_TYPE_MONO_SEMIBOLD = 13;
    private static String TAG = "TypefaceTextView";

    public TypefaceTextView(Context context) {
        this(context, null);
    }

    public TypefaceTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (getTypeface(context, attributeSet) != null) {
            setTypeface(getTypeface(context, attributeSet));
        }
    }

    public void setTypefaceType(int i) {
        Typeface typeface;
        switch (i) {
            case 1:
                typeface = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_40);
                break;
            case 2:
                typeface = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_50);
                break;
            case 3:
                typeface = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_60);
                break;
            case 4:
                typeface = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_70);
                break;
            case 5:
                typeface = MiuiFont.MI_PRO_NORMAL;
                break;
            case 6:
                typeface = MiuiFont.MI_PRO_MEDIUM;
                break;
            case 7:
                typeface = MiuiFont.MI_PRO_REGULAR;
                break;
            case 8:
                typeface = MiuiFont.MI_PRO_LIGHT;
                break;
            case 9:
                typeface = MiuiFont.MI_TYPE_MONO_NORMAL;
                break;
            case 10:
                typeface = MiuiFont.MI_TYPE_MONO_MEDIUM;
                break;
            case 11:
                typeface = MiuiFont.MI_TYPE_MONO_BOLD;
                break;
            case 12:
                typeface = MiuiFont.MI_TYPE_MONO_DEMIBOLD;
                break;
            case 13:
                typeface = MiuiFont.MI_TYPE_MONO_SEMIBOLD;
                break;
            case 14:
                typeface = MiuiFont.MI_TYPE_MONO_REGULAR;
                break;
            default:
                typeface = null;
                break;
        }
        try {
            setTypeface(typeface);
            invalidate();
        } catch (Exception unused) {
        }
    }

    private Typeface getTypeface(Context context, AttributeSet attributeSet) {
        Typeface typefaceCreate;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TypefaceTextView);
        switch (typedArrayObtainStyledAttributes.getInt(0, 0)) {
            case 1:
                typefaceCreate = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_40);
                break;
            case 2:
                typefaceCreate = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_50);
                break;
            case 3:
                typefaceCreate = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_60);
                break;
            case 4:
                typefaceCreate = TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_70);
                break;
            case 5:
                typefaceCreate = MiuiFont.MI_PRO_NORMAL;
                break;
            case 6:
                typefaceCreate = MiuiFont.MI_PRO_MEDIUM;
                break;
            case 7:
                typefaceCreate = MiuiFont.MI_PRO_REGULAR;
                break;
            case 8:
                typefaceCreate = MiuiFont.MI_PRO_LIGHT;
                break;
            case 9:
                typefaceCreate = MiuiFont.MI_TYPE_MONO_NORMAL;
                break;
            case 10:
                typefaceCreate = MiuiFont.MI_TYPE_MONO_MEDIUM;
                break;
            case 11:
                typefaceCreate = MiuiFont.MI_TYPE_MONO_BOLD;
                break;
            case 12:
                typefaceCreate = MiuiFont.MI_TYPE_MONO_DEMIBOLD;
                break;
            case 13:
                typefaceCreate = MiuiFont.MI_TYPE_MONO_SEMIBOLD;
                break;
            case 14:
                typefaceCreate = MiuiFont.MI_TYPE_MONO_REGULAR;
                break;
            case 15:
                typefaceCreate = TypefaceFactory.get(TypefaceFactory.MI_TYPE_MiSANS_RCFVF);
                if (Build.VERSION.SDK_INT >= 28) {
                    typefaceCreate = Typeface.create(typefaceCreate, 600, false);
                }
                break;
            default:
                typefaceCreate = null;
                break;
        }
        typedArrayObtainStyledAttributes.recycle();
        return typefaceCreate;
    }
}
