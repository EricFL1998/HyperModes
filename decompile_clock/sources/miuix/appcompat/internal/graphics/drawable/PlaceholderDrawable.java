package miuix.appcompat.internal.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import java.io.IOException;
import miuix.appcompat.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public class PlaceholderDrawable extends Drawable {
    private Rect mPadding;
    public int mWidth = -1;
    public int mHeight = -1;

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth() + 1;
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            int depth2 = xmlPullParser.getDepth();
            if (depth2 < depth && next == 3) {
                return;
            }
            if (next == 2 && depth2 <= depth) {
                String name = xmlPullParser.getName();
                if (name.equals("size")) {
                    TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.PlaceholderDrawableSize);
                    this.mWidth = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.PlaceholderDrawableSize_android_width, -1);
                    this.mHeight = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.PlaceholderDrawableSize_android_height, -1);
                    typedArrayObtainAttributes.recycle();
                } else if (name.equals("padding")) {
                    TypedArray typedArrayObtainAttributes2 = resources.obtainAttributes(attributeSet, R.styleable.PlaceholderDrawablePadding);
                    this.mPadding = new Rect(typedArrayObtainAttributes2.getDimensionPixelOffset(R.styleable.PlaceholderDrawablePadding_android_left, 0), typedArrayObtainAttributes2.getDimensionPixelOffset(R.styleable.PlaceholderDrawablePadding_android_top, 0), typedArrayObtainAttributes2.getDimensionPixelOffset(R.styleable.PlaceholderDrawablePadding_android_right, 0), typedArrayObtainAttributes2.getDimensionPixelOffset(R.styleable.PlaceholderDrawablePadding_android_bottom, 0));
                    typedArrayObtainAttributes2.recycle();
                } else {
                    Log.w("drawable", "Bad element under <placeholder>: " + name);
                }
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        Rect rect2 = this.mPadding;
        if (rect2 != null) {
            rect.set(rect2);
            return true;
        }
        return super.getPadding(rect);
    }
}
