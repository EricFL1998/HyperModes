package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class ConstraintAttribute {
    private static final String TAG = "TransitionLayout";
    boolean mBooleanValue;
    private int mColorValue;
    private float mFloatValue;
    private int mIntegerValue;
    private boolean mMethod;
    String mName;
    private String mStringValue;
    private AttributeType mType;

    public enum AttributeType {
        INT_TYPE,
        FLOAT_TYPE,
        COLOR_TYPE,
        COLOR_DRAWABLE_TYPE,
        STRING_TYPE,
        BOOLEAN_TYPE,
        DIMENSION_TYPE,
        REFERENCE_TYPE
    }

    private static int clamp(int c) {
        int i = (c & (~(c >> 31))) - 255;
        return (i & (i >> 31)) + 255;
    }

    public String getName() {
        return this.mName;
    }

    public boolean isMethod() {
        return this.mMethod;
    }

    public int getIntegerValue() {
        return this.mIntegerValue;
    }

    public float getFloatValue() {
        return this.mFloatValue;
    }

    public String getStringValue() {
        return this.mStringValue;
    }

    public boolean isBooleanValue() {
        return this.mBooleanValue;
    }

    public int getColorValue() {
        return this.mColorValue;
    }

    public AttributeType getType() {
        return this.mType;
    }

    public boolean isContinuous() {
        int i = AnonymousClass1.$SwitchMap$androidx$constraintlayout$widget$ConstraintAttribute$AttributeType[this.mType.ordinal()];
        return (i == 1 || i == 2 || i == 3) ? false : true;
    }

    public void setFloatValue(float value) {
        this.mFloatValue = value;
    }

    public void setColorValue(int value) {
        this.mColorValue = value;
    }

    public void setIntValue(int value) {
        this.mIntegerValue = value;
    }

    public void setStringValue(String value) {
        this.mStringValue = value;
    }

    public int numberOfInterpolatedValues() {
        int i = AnonymousClass1.$SwitchMap$androidx$constraintlayout$widget$ConstraintAttribute$AttributeType[this.mType.ordinal()];
        return (i == 4 || i == 5) ? 4 : 1;
    }

    public float getValueToInterpolate() {
        switch (this.mType) {
            case BOOLEAN_TYPE:
                return this.mBooleanValue ? 1.0f : 0.0f;
            case STRING_TYPE:
                throw new RuntimeException("Cannot interpolate String");
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case INT_TYPE:
                return this.mIntegerValue;
            case FLOAT_TYPE:
                return this.mFloatValue;
            case DIMENSION_TYPE:
                return this.mFloatValue;
            default:
                return Float.NaN;
        }
    }

    public void getValuesToInterpolate(float[] ret) {
        switch (this.mType) {
            case BOOLEAN_TYPE:
                ret[0] = this.mBooleanValue ? 1.0f : 0.0f;
                return;
            case STRING_TYPE:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                int i = this.mColorValue;
                int i2 = (i >> 24) & 255;
                float fPow = (float) Math.pow(((i >> 16) & 255) / 255.0f, 2.2d);
                float fPow2 = (float) Math.pow(((i >> 8) & 255) / 255.0f, 2.2d);
                float fPow3 = (float) Math.pow((i & 255) / 255.0f, 2.2d);
                ret[0] = fPow;
                ret[1] = fPow2;
                ret[2] = fPow3;
                ret[3] = i2 / 255.0f;
                return;
            case INT_TYPE:
                ret[0] = this.mIntegerValue;
                return;
            case FLOAT_TYPE:
                ret[0] = this.mFloatValue;
                return;
            case DIMENSION_TYPE:
                ret[0] = this.mFloatValue;
                return;
            default:
                return;
        }
    }

    public void setValue(float[] value) {
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$widget$ConstraintAttribute$AttributeType[this.mType.ordinal()]) {
            case 1:
            case 6:
                this.mIntegerValue = (int) value[0];
                return;
            case 2:
                this.mBooleanValue = ((double) value[0]) > 0.5d;
                return;
            case 3:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case 4:
            case 5:
                int iHSVToColor = Color.HSVToColor(value);
                this.mColorValue = iHSVToColor;
                this.mColorValue = (clamp((int) (value[3] * 255.0f)) << 24) | (iHSVToColor & 16777215);
                return;
            case 7:
                this.mFloatValue = value[0];
                return;
            case 8:
                this.mFloatValue = value[0];
                return;
            default:
                return;
        }
    }

    public boolean diff(ConstraintAttribute constraintAttribute) {
        if (constraintAttribute == null || this.mType != constraintAttribute.mType) {
            return false;
        }
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$widget$ConstraintAttribute$AttributeType[this.mType.ordinal()]) {
            case 1:
            case 6:
                return this.mIntegerValue == constraintAttribute.mIntegerValue;
            case 2:
                return this.mBooleanValue == constraintAttribute.mBooleanValue;
            case 3:
                return this.mIntegerValue == constraintAttribute.mIntegerValue;
            case 4:
            case 5:
                return this.mColorValue == constraintAttribute.mColorValue;
            case 7:
                return this.mFloatValue == constraintAttribute.mFloatValue;
            case 8:
                return this.mFloatValue == constraintAttribute.mFloatValue;
            default:
                return false;
        }
    }

    public ConstraintAttribute(String name, AttributeType attributeType) {
        this.mMethod = false;
        this.mName = name;
        this.mType = attributeType;
    }

    public ConstraintAttribute(String name, AttributeType attributeType, Object value, boolean method) {
        this.mName = name;
        this.mType = attributeType;
        this.mMethod = method;
        setValue(value);
    }

    public ConstraintAttribute(ConstraintAttribute source, Object value) {
        this.mMethod = false;
        this.mName = source.mName;
        this.mType = source.mType;
        setValue(value);
    }

    public void setValue(Object value) {
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$widget$ConstraintAttribute$AttributeType[this.mType.ordinal()]) {
            case 1:
            case 6:
                this.mIntegerValue = ((Integer) value).intValue();
                break;
            case 2:
                this.mBooleanValue = ((Boolean) value).booleanValue();
                break;
            case 3:
                this.mStringValue = (String) value;
                break;
            case 4:
            case 5:
                this.mColorValue = ((Integer) value).intValue();
                break;
            case 7:
                this.mFloatValue = ((Float) value).floatValue();
                break;
            case 8:
                this.mFloatValue = ((Float) value).floatValue();
                break;
        }
    }

    public static HashMap<String, ConstraintAttribute> extractAttributes(HashMap<String, ConstraintAttribute> base, View view) {
        HashMap<String, ConstraintAttribute> map = new HashMap<>();
        Class<?> cls = view.getClass();
        for (String str : base.keySet()) {
            ConstraintAttribute constraintAttribute = base.get(str);
            try {
                if (str.equals("BackgroundColor")) {
                    map.put(str, new ConstraintAttribute(constraintAttribute, Integer.valueOf(((ColorDrawable) view.getBackground()).getColor())));
                } else {
                    map.put(str, new ConstraintAttribute(constraintAttribute, cls.getMethod("getMap" + str, new Class[0]).invoke(view, new Object[0])));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            }
        }
        return map;
    }

    public static void setAttributes(View view, HashMap<String, ConstraintAttribute> map) {
        Class<?> cls = view.getClass();
        for (String str : map.keySet()) {
            ConstraintAttribute constraintAttribute = map.get(str);
            String str2 = !constraintAttribute.mMethod ? "set" + str : str;
            try {
                switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$widget$ConstraintAttribute$AttributeType[constraintAttribute.mType.ordinal()]) {
                    case 1:
                        cls.getMethod(str2, Integer.TYPE).invoke(view, Integer.valueOf(constraintAttribute.mIntegerValue));
                        break;
                    case 2:
                        cls.getMethod(str2, Boolean.TYPE).invoke(view, Boolean.valueOf(constraintAttribute.mBooleanValue));
                        break;
                    case 3:
                        cls.getMethod(str2, CharSequence.class).invoke(view, constraintAttribute.mStringValue);
                        break;
                    case 4:
                        cls.getMethod(str2, Integer.TYPE).invoke(view, Integer.valueOf(constraintAttribute.mColorValue));
                        break;
                    case 5:
                        Method method = cls.getMethod(str2, Drawable.class);
                        ColorDrawable colorDrawable = new ColorDrawable();
                        colorDrawable.setColor(constraintAttribute.mColorValue);
                        method.invoke(view, colorDrawable);
                        break;
                    case 6:
                        cls.getMethod(str2, Integer.TYPE).invoke(view, Integer.valueOf(constraintAttribute.mIntegerValue));
                        break;
                    case 7:
                        cls.getMethod(str2, Float.TYPE).invoke(view, Float.valueOf(constraintAttribute.mFloatValue));
                        break;
                    case 8:
                        cls.getMethod(str2, Float.TYPE).invoke(view, Float.valueOf(constraintAttribute.mFloatValue));
                        break;
                }
            } catch (IllegalAccessException e) {
                Log.e(TAG, " Custom Attribute \"" + str + "\" not found on " + cls.getName());
                e.printStackTrace();
            } catch (NoSuchMethodException e2) {
                Log.e(TAG, e2.getMessage());
                Log.e(TAG, " Custom Attribute \"" + str + "\" not found on " + cls.getName());
                Log.e(TAG, cls.getName() + " must have a method " + str2);
            } catch (InvocationTargetException e3) {
                Log.e(TAG, " Custom Attribute \"" + str + "\" not found on " + cls.getName());
                e3.printStackTrace();
            }
        }
    }

    public void applyCustom(View view) {
        Class<?> cls = view.getClass();
        String str = this.mName;
        String str2 = !this.mMethod ? "set" + str : str;
        try {
            switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$widget$ConstraintAttribute$AttributeType[this.mType.ordinal()]) {
                case 1:
                case 6:
                    cls.getMethod(str2, Integer.TYPE).invoke(view, Integer.valueOf(this.mIntegerValue));
                    break;
                case 2:
                    cls.getMethod(str2, Boolean.TYPE).invoke(view, Boolean.valueOf(this.mBooleanValue));
                    break;
                case 3:
                    cls.getMethod(str2, CharSequence.class).invoke(view, this.mStringValue);
                    break;
                case 4:
                    cls.getMethod(str2, Integer.TYPE).invoke(view, Integer.valueOf(this.mColorValue));
                    break;
                case 5:
                    Method method = cls.getMethod(str2, Drawable.class);
                    ColorDrawable colorDrawable = new ColorDrawable();
                    colorDrawable.setColor(this.mColorValue);
                    method.invoke(view, colorDrawable);
                    break;
                case 7:
                    cls.getMethod(str2, Float.TYPE).invoke(view, Float.valueOf(this.mFloatValue));
                    break;
                case 8:
                    cls.getMethod(str2, Float.TYPE).invoke(view, Float.valueOf(this.mFloatValue));
                    break;
                default:
                    break;
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, " Custom Attribute \"" + str + "\" not found on " + cls.getName());
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            Log.e(TAG, e2.getMessage());
            Log.e(TAG, " Custom Attribute \"" + str + "\" not found on " + cls.getName());
            Log.e(TAG, cls.getName() + " must have a method " + str2);
        } catch (InvocationTargetException e3) {
            Log.e(TAG, " Custom Attribute \"" + str + "\" not found on " + cls.getName());
            e3.printStackTrace();
        }
    }

    public static void parse(Context context, XmlPullParser parser, HashMap<String, ConstraintAttribute> custom) {
        AttributeType attributeType;
        Object objValueOf;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.CustomAttribute);
        int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
        String string = null;
        Object objValueOf2 = null;
        AttributeType attributeType2 = null;
        boolean z = false;
        for (int i = 0; i < indexCount; i++) {
            int index = typedArrayObtainStyledAttributes.getIndex(i);
            if (index == R.styleable.CustomAttribute_attributeName) {
                string = typedArrayObtainStyledAttributes.getString(index);
                if (string != null && string.length() > 0) {
                    string = Character.toUpperCase(string.charAt(0)) + string.substring(1);
                }
            } else if (index == R.styleable.CustomAttribute_methodName) {
                string = typedArrayObtainStyledAttributes.getString(index);
                z = true;
            } else if (index == R.styleable.CustomAttribute_customBoolean) {
                objValueOf2 = Boolean.valueOf(typedArrayObtainStyledAttributes.getBoolean(index, false));
                attributeType2 = AttributeType.BOOLEAN_TYPE;
            } else {
                if (index == R.styleable.CustomAttribute_customColorValue) {
                    attributeType = AttributeType.COLOR_TYPE;
                    objValueOf = Integer.valueOf(typedArrayObtainStyledAttributes.getColor(index, 0));
                } else if (index == R.styleable.CustomAttribute_customColorDrawableValue) {
                    attributeType = AttributeType.COLOR_DRAWABLE_TYPE;
                    objValueOf = Integer.valueOf(typedArrayObtainStyledAttributes.getColor(index, 0));
                } else if (index == R.styleable.CustomAttribute_customPixelDimension) {
                    attributeType = AttributeType.DIMENSION_TYPE;
                    objValueOf = Float.valueOf(TypedValue.applyDimension(1, typedArrayObtainStyledAttributes.getDimension(index, 0.0f), context.getResources().getDisplayMetrics()));
                } else if (index == R.styleable.CustomAttribute_customDimension) {
                    attributeType = AttributeType.DIMENSION_TYPE;
                    objValueOf = Float.valueOf(typedArrayObtainStyledAttributes.getDimension(index, 0.0f));
                } else if (index == R.styleable.CustomAttribute_customFloatValue) {
                    attributeType = AttributeType.FLOAT_TYPE;
                    objValueOf = Float.valueOf(typedArrayObtainStyledAttributes.getFloat(index, Float.NaN));
                } else if (index == R.styleable.CustomAttribute_customIntegerValue) {
                    attributeType = AttributeType.INT_TYPE;
                    objValueOf = Integer.valueOf(typedArrayObtainStyledAttributes.getInteger(index, -1));
                } else if (index == R.styleable.CustomAttribute_customStringValue) {
                    attributeType = AttributeType.STRING_TYPE;
                    objValueOf = typedArrayObtainStyledAttributes.getString(index);
                } else if (index == R.styleable.CustomAttribute_customReference) {
                    attributeType = AttributeType.REFERENCE_TYPE;
                    int resourceId = typedArrayObtainStyledAttributes.getResourceId(index, -1);
                    if (resourceId == -1) {
                        resourceId = typedArrayObtainStyledAttributes.getInt(index, -1);
                    }
                    objValueOf = Integer.valueOf(resourceId);
                }
                Object obj = objValueOf;
                attributeType2 = attributeType;
                objValueOf2 = obj;
            }
        }
        if (string != null && objValueOf2 != null) {
            custom.put(string, new ConstraintAttribute(string, attributeType2, objValueOf2, z));
        }
        typedArrayObtainStyledAttributes.recycle();
    }
}
