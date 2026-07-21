package miuix.theme.symbol;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public class SymbolPaint<T extends Paint> {
    private final T paint;
    private int[] state = null;
    private ColorStateList colorsList = null;

    public SymbolPaint(T t) {
        this.paint = t;
        t.setAlpha(255);
    }

    public int getAlpha() {
        return this.paint.getAlpha();
    }

    public void setAlpha(int i) {
        if (this.paint.getAlpha() != i) {
            this.paint.setAlpha(i);
        }
    }

    public boolean isStateful() {
        ColorStateList colorStateList = this.colorsList;
        return colorStateList != null && colorStateList.isStateful();
    }

    public int getColorForCurrentState() {
        ColorStateList colorStateList = this.colorsList;
        return getColorForCurrentState(colorStateList != null ? colorStateList.getDefaultColor() : 0);
    }

    private int getColorForCurrentState(int i) {
        ColorStateList colorStateList = this.colorsList;
        return colorStateList != null ? colorStateList.getColorForState(this.state, i) : i;
    }

    public boolean applyState(int[] iArr) {
        this.state = iArr;
        int colorForCurrentState = getColorForCurrentState();
        int color = this.paint.getColor();
        this.paint.setColor(colorForCurrentState);
        return colorForCurrentState != color;
    }

    public ColorStateList getColorsList() {
        return this.colorsList;
    }

    public void setColorsList(ColorStateList colorStateList) {
        this.colorsList = colorStateList;
    }

    public T getPaint() {
        return this.paint;
    }

    public String toString() {
        StringBuilder sbAppend = new StringBuilder("color=#").append(Integer.toHexString(this.paint.getColor())).append(", state=");
        int[] iArr = this.state;
        return sbAppend.append(iArr != null ? Arrays.toString(iArr) : "null").append(", colorList=").append(this.colorsList).toString();
    }
}
