package com.android.deskclock;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.os.Bundle;
import com.android.deskclock.util.Log;
import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: classes.dex */
public final class VoiceController {
    private static final VoiceController sVoiceController = new VoiceController();

    private VoiceController() {
    }

    public static VoiceController getController() {
        return sVoiceController;
    }

    public void notifyVoiceSuccess(Activity activity, String str) {
        try {
            Class<?> cls = Class.forName("android.app.VoiceInteractor$Prompt");
            Object objNewInstance = cls.getConstructor(CharSequence.class).newInstance(str);
            VoiceInteractor voiceInteractor = activity.getVoiceInteractor();
            if (voiceInteractor != null) {
                voiceInteractor.submitRequest((VoiceInteractor.CompleteVoiceRequest) Class.forName("android.app.VoiceInteractor$CompleteVoiceRequest").getConstructor(cls, Bundle.class).newInstance(objNewInstance, null));
            }
        } catch (ClassNotFoundException e) {
            Log.e("failed to notifyVoiceSuccess ", e);
        } catch (IllegalAccessException e2) {
            Log.e("failed to notifyVoiceSuccess ", e2);
        } catch (InstantiationException e3) {
            Log.e("failed to notifyVoiceSuccess ", e3);
        } catch (NoSuchMethodException e4) {
            Log.e("failed to notifyVoiceSuccess ", e4);
        } catch (InvocationTargetException e5) {
            Log.e("failed to notifyVoiceSuccess ", e5);
        }
    }

    public void notifyVoiceFailure(Activity activity, String str) {
        try {
            VoiceInteractor voiceInteractor = activity.getVoiceInteractor();
            if (voiceInteractor != null) {
                Class<?> cls = Class.forName("android.app.VoiceInteractor$Prompt");
                voiceInteractor.submitRequest((VoiceInteractor.AbortVoiceRequest) Class.forName("android.app.VoiceInteractor$AbortVoiceRequest").getConstructor(cls, Bundle.class).newInstance(cls.getConstructor(CharSequence.class).newInstance(str), null));
            }
        } catch (ClassNotFoundException e) {
            Log.e("failed to notifyVoiceFailure ", e);
        } catch (IllegalAccessException e2) {
            Log.e("failed to notifyVoiceFailure ", e2);
        } catch (InstantiationException e3) {
            Log.e("failed to notifyVoiceFailure ", e3);
        } catch (NoSuchMethodException e4) {
            Log.e("failed to notifyVoiceFailure ", e4);
        } catch (InvocationTargetException e5) {
            Log.e("failed to notifyVoiceFailure ", e5);
        }
    }
}
