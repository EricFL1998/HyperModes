package miuix.animation.controller;

import miuix.animation.IAnimTarget;
import miuix.animation.IStateStyle;

/* JADX INFO: loaded from: classes2.dex */
public interface IFolmeStateStyle extends IStateStyle {
    void addState(AnimState animState);

    AnimState getState(Object obj);

    @Override // miuix.animation.FolmeStyle
    IAnimTarget getTarget();
}
