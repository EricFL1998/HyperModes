package miuix.navigator.draganddrop;

/* JADX INFO: loaded from: classes3.dex */
public class DragStartFeedback {
    private boolean mCanAccept;
    private boolean mCanInsert;

    public void setCanInsert(boolean z) {
        this.mCanInsert = z;
    }

    public boolean canInsert() {
        return this.mCanInsert;
    }

    public void setCanAccept(boolean z) {
        this.mCanAccept = z;
    }

    public boolean canAccept() {
        return this.mCanAccept;
    }
}
