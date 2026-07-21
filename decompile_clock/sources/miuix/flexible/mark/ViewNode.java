package miuix.flexible.mark;

import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public class ViewNode {
    private int height;
    private int mark;
    private View view;
    private float weight = 0.0f;
    private int width;

    public int getMark() {
        return this.mark;
    }

    public void setMark(int i) {
        this.mark = i;
    }

    public View getView() {
        return this.view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float f) {
        this.weight = f;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int i) {
        this.width = i;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int i) {
        this.height = i;
    }
}
