package miuix.flexible.mark;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class ViewList extends ViewNode {
    public static final int ORIENTATION_X = 1;
    public static final int ORIENTATION_Y = 0;
    private int orientation = 1;
    private final List<ViewNode> list = new ArrayList();

    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }

    public List<ViewNode> getList() {
        return this.list;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setOrientation(int i) {
        this.orientation = i;
    }
}
