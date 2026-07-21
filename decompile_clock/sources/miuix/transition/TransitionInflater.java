package miuix.transition;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.ViewGroup;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.transition.MiuixTransitionManager;
import androidx.transition.Scene;
import com.xiaomi.onetrack.api.g;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes3.dex */
public class TransitionInflater {
    private static final Class<?>[] sConstructorSignature = {Context.class, AttributeSet.class};
    private static final ArrayMap<String, Constructor> sConstructors = new ArrayMap<>();
    private Context mContext;

    private TransitionInflater(Context context) {
        this.mContext = context;
    }

    public static TransitionInflater from(Context context) {
        return new TransitionInflater(context);
    }

    public MiuixTransition inflateTransition(int i) {
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            try {
                MiuixTransition miuixTransitionCreateTransitionFromXml = createTransitionFromXml(xml, Xml.asAttributeSet(xml), null);
                xml.close();
                return miuixTransitionCreateTransitionFromXml;
            } catch (IOException e) {
                InflateException inflateException = new InflateException(xml.getPositionDescription() + ": " + e.getMessage());
                inflateException.initCause(e);
                throw inflateException;
            } catch (XmlPullParserException e2) {
                InflateException inflateException2 = new InflateException(e2.getMessage());
                inflateException2.initCause(e2);
                throw inflateException2;
            }
        } catch (Throwable th) {
            xml.close();
            throw th;
        }
    }

    public MiuixTransitionManager inflateTransitionManager(int i, ViewGroup viewGroup) {
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            try {
                MiuixTransitionManager miuixTransitionManagerCreateTransitionManagerFromXml = createTransitionManagerFromXml(xml, Xml.asAttributeSet(xml), viewGroup);
                xml.close();
                return miuixTransitionManagerCreateTransitionManagerFromXml;
            } catch (IOException e) {
                InflateException inflateException = new InflateException(xml.getPositionDescription() + ": " + e.getMessage());
                inflateException.initCause(e);
                throw inflateException;
            } catch (XmlPullParserException e2) {
                InflateException inflateException2 = new InflateException(e2.getMessage());
                inflateException2.initCause(e2);
                throw inflateException2;
            }
        } catch (Throwable th) {
            xml.close();
            throw th;
        }
    }

    private MiuixTransition createTransitionFromXml(XmlPullParser xmlPullParser, AttributeSet attributeSet, MiuixTransition miuixTransition) throws XmlPullParserException, IOException {
        MiuixTransition autoTransition;
        int depth = xmlPullParser.getDepth();
        MiuixTransitionSet miuixTransitionSet = miuixTransition instanceof MiuixTransitionSet ? (MiuixTransitionSet) miuixTransition : null;
        loop0: while (true) {
            autoTransition = null;
            while (true) {
                int next = xmlPullParser.next();
                if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                    break loop0;
                }
                if (next == 2) {
                    String name = xmlPullParser.getName();
                    if ("fade".equals(name)) {
                        autoTransition = new Fade(this.mContext, attributeSet);
                    } else if ("changeBounds".equals(name)) {
                        autoTransition = new ChangeBounds(this.mContext, attributeSet);
                    } else if ("sharedElement".equals(name)) {
                        autoTransition = new SharedElementTransition(this.mContext, attributeSet);
                    } else if ("trendTransition".equals(name)) {
                        autoTransition = new TrendTransition(this.mContext, attributeSet);
                    } else if ("transition".equals(name)) {
                        autoTransition = (MiuixTransition) createCustom(attributeSet, MiuixTransition.class, "transition");
                    } else if ("transitionSet".equals(name)) {
                        autoTransition = new MiuixTransitionSet(this.mContext, attributeSet);
                    } else if (TypedValues.TransitionType.S_AUTO_TRANSITION.equals(name)) {
                        autoTransition = new AutoTransition(this.mContext, attributeSet);
                    } else if ("targets".equals(name)) {
                        getTargetIds(xmlPullParser, attributeSet, miuixTransition);
                    } else {
                        throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
                    }
                    if (autoTransition == null) {
                        continue;
                    } else {
                        if (!xmlPullParser.isEmptyElementTag()) {
                            createTransitionFromXml(xmlPullParser, attributeSet, autoTransition);
                        }
                        if (miuixTransitionSet != null) {
                            break;
                        }
                        if (miuixTransition != null) {
                            throw new InflateException("Could not add transition to another transition.");
                        }
                    }
                }
            }
            miuixTransitionSet.addTransition(autoTransition);
        }
        return autoTransition;
    }

    private Object createCustom(AttributeSet attributeSet, Class cls, String str) {
        Object objNewInstance;
        Class<? extends U> clsAsSubclass;
        String attributeValue = attributeSet.getAttributeValue(null, g.r);
        if (attributeValue == null) {
            throw new InflateException(str + " tag must have a 'class' attribute");
        }
        try {
            ArrayMap<String, Constructor> arrayMap = sConstructors;
            synchronized (arrayMap) {
                Constructor constructor = arrayMap.get(attributeValue);
                if (constructor == null && (clsAsSubclass = this.mContext.getClassLoader().loadClass(attributeValue).asSubclass(cls)) != 0) {
                    constructor = clsAsSubclass.getConstructor(sConstructorSignature);
                    constructor.setAccessible(true);
                    arrayMap.put(attributeValue, constructor);
                }
                objNewInstance = constructor != null ? constructor.newInstance(this.mContext, attributeSet) : null;
            }
            return objNewInstance;
        } catch (ClassNotFoundException e) {
            throw new InflateException("Could not instantiate " + cls + " class " + attributeValue, e);
        } catch (IllegalAccessException e2) {
            throw new InflateException("Could not instantiate " + cls + " class " + attributeValue, e2);
        } catch (InstantiationException e3) {
            throw new InflateException("Could not instantiate " + cls + " class " + attributeValue, e3);
        } catch (NoSuchMethodException e4) {
            throw new InflateException("Could not instantiate " + cls + " class " + attributeValue, e4);
        } catch (InvocationTargetException e5) {
            throw new InflateException("Could not instantiate " + cls + " class " + attributeValue, e5);
        }
    }

    private void getTargetIds(XmlPullParser xmlPullParser, AttributeSet attributeSet, MiuixTransition miuixTransition) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return;
            }
            if (next == 2) {
                if (xmlPullParser.getName().equals(TypedValues.AttributesType.S_TARGET)) {
                    TypedArray typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R.styleable.TransitionTarget);
                    int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.TransitionTarget_targetId, 0);
                    if (resourceId != 0) {
                        miuixTransition.addTarget(resourceId);
                    } else {
                        int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.TransitionTarget_excludeId, 0);
                        if (resourceId2 != 0) {
                            miuixTransition.excludeTarget(resourceId2, true);
                        } else {
                            String string = typedArrayObtainStyledAttributes.getString(R.styleable.TransitionTarget_targetName);
                            if (string != null) {
                                miuixTransition.addTarget(string);
                            } else {
                                String string2 = typedArrayObtainStyledAttributes.getString(R.styleable.TransitionTarget_excludeName);
                                if (string2 != null) {
                                    miuixTransition.excludeTarget(string2, true);
                                } else {
                                    String string3 = typedArrayObtainStyledAttributes.getString(R.styleable.TransitionTarget_excludeClass);
                                    if (string3 != null) {
                                        try {
                                            miuixTransition.excludeTarget(Class.forName(string3), true);
                                        } catch (ClassNotFoundException e) {
                                            typedArrayObtainStyledAttributes.recycle();
                                            throw new RuntimeException("Could not create " + string3, e);
                                        }
                                    } else {
                                        String string4 = typedArrayObtainStyledAttributes.getString(R.styleable.TransitionTarget_targetClass);
                                        if (string4 != null) {
                                            miuixTransition.addTarget(Class.forName(string4));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    typedArrayObtainStyledAttributes.recycle();
                } else {
                    throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
                }
            }
        }
    }

    private MiuixTransitionManager createTransitionManagerFromXml(XmlPullParser xmlPullParser, AttributeSet attributeSet, ViewGroup viewGroup) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        MiuixTransitionManager miuixTransitionManager = null;
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                break;
            }
            if (next == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("transitionManager")) {
                    miuixTransitionManager = new MiuixTransitionManager();
                } else if (name.equals("transition") && miuixTransitionManager != null) {
                    loadTransition(attributeSet, viewGroup, miuixTransitionManager);
                } else {
                    throw new RuntimeException("Unknown scene name: " + xmlPullParser.getName());
                }
            }
        }
        return miuixTransitionManager;
    }

    private void loadTransition(AttributeSet attributeSet, ViewGroup viewGroup, MiuixTransitionManager miuixTransitionManager) throws Resources.NotFoundException {
        MiuixTransition miuixTransitionInflateTransition;
        TypedArray typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R.styleable.TransitionManager);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.TransitionManager_transition, -1);
        int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.TransitionManager_fromScene, -1);
        Scene sceneForLayout = resourceId2 < 0 ? null : Scene.getSceneForLayout(viewGroup, resourceId2, this.mContext);
        int resourceId3 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.TransitionManager_toScene, -1);
        Scene sceneForLayout2 = resourceId3 >= 0 ? Scene.getSceneForLayout(viewGroup, resourceId3, this.mContext) : null;
        if (resourceId >= 0 && (miuixTransitionInflateTransition = inflateTransition(resourceId)) != null) {
            if (sceneForLayout2 == null) {
                throw new RuntimeException("No toScene for transition ID " + resourceId);
            }
            if (sceneForLayout == null) {
                miuixTransitionManager.setTransition(sceneForLayout2, miuixTransitionInflateTransition);
            } else {
                miuixTransitionManager.setTransition(sceneForLayout, sceneForLayout2, miuixTransitionInflateTransition);
            }
        }
        typedArrayObtainStyledAttributes.recycle();
    }
}
