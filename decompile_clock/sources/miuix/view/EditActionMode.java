package miuix.view;

/* JADX INFO: loaded from: classes3.dex */
public interface EditActionMode {
    public static final int BUTTON1 = 16908313;
    public static final int BUTTON2 = 16908314;

    void addAnimationListener(ActionModeAnimationListener actionModeAnimationListener);

    void announceAccessibilityEvent(String str);

    void removeAnimationListener(ActionModeAnimationListener actionModeAnimationListener);

    void setAnnounceAccessibilityEnabled(boolean z);

    void setButton(int i, int i2);

    void setButton(int i, int i2, int i3);

    void setButton(int i, CharSequence charSequence);

    void setButton(int i, CharSequence charSequence, int i2);

    void setButton(int i, CharSequence charSequence, int i2, CharSequence charSequence2, int i3);

    void setButton(int i, CharSequence charSequence, CharSequence charSequence2, int i2);

    void setFinishEditActionModeDescription(int i);

    void setStartEditActionModeDescription(int i);
}
