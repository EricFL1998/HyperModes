package miuix.animation.controller;

import java.lang.reflect.Method;
import miuix.animation.IAnimTarget;
import miuix.animation.utils.StyleComposer;

/* JADX INFO: loaded from: classes2.dex */
public class StateComposer {
    private static final String METHOD_GET_STATE = "getState";
    private static final StyleComposer.IInterceptor<IFolmeStateStyle> sInterceptor = new StyleComposer.IInterceptor<IFolmeStateStyle>() { // from class: miuix.animation.controller.StateComposer.1
        @Override // miuix.animation.utils.StyleComposer.IInterceptor
        public boolean shouldIntercept(Method method, Object[] objArr) {
            return method.getName().equals(StateComposer.METHOD_GET_STATE);
        }

        @Override // miuix.animation.utils.StyleComposer.IInterceptor
        public Object onMethod(Method method, Object[] objArr, IFolmeStateStyle[] iFolmeStateStyleArr) {
            if (iFolmeStateStyleArr.length <= 0 || objArr.length <= 0) {
                return null;
            }
            AnimState state = iFolmeStateStyleArr[0].getState(objArr[0]);
            for (int i = 1; i < iFolmeStateStyleArr.length; i++) {
                iFolmeStateStyleArr[i].addState(state);
            }
            return state;
        }
    };

    private StateComposer() {
    }

    public static IFolmeStateStyle composeStyle(IAnimTarget... iAnimTargetArr) {
        if (iAnimTargetArr == null || iAnimTargetArr.length == 0) {
            return null;
        }
        if (iAnimTargetArr.length == 1) {
            return new FolmeState(iAnimTargetArr[0]);
        }
        FolmeState[] folmeStateArr = new FolmeState[iAnimTargetArr.length];
        for (int i = 0; i < iAnimTargetArr.length; i++) {
            folmeStateArr[i] = new FolmeState(iAnimTargetArr[i]);
        }
        return (IFolmeStateStyle) StyleComposer.compose(IFolmeStateStyle.class, sInterceptor, folmeStateArr);
    }
}
