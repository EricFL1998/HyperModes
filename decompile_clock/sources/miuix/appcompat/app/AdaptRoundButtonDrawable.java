package miuix.appcompat.app;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import java.io.IOException;
import miuix.appcompat.R;
import miuix.core.util.RomUtils;
import miuix.smooth.SmoothContainerDrawable2;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public class AdaptRoundButtonDrawable extends SmoothContainerDrawable2 {
    private float mCapsuleRadius;
    private float mRadius;

    @Override // miuix.smooth.SmoothContainerDrawable2, android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        init(resources, attributeSet, theme);
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        if (RomUtils.isMiuiXVSdkSupported()) {
            setCornerRadius(this.mRadius);
        } else {
            setCornerRadius(this.mCapsuleRadius);
        }
    }

    private void init(Resources resources, AttributeSet attributeSet, Resources.Theme theme) {
        TypedArray typedArrayObtainAttributes = obtainAttributes(resources, theme, attributeSet, R.styleable.AdaptRoundButtonDrawable);
        float f = resources.getDisplayMetrics().density;
        this.mRadius = typedArrayObtainAttributes.getDimension(R.styleable.AdaptRoundButtonDrawable_buttonRadius, 16.0f * f);
        this.mCapsuleRadius = typedArrayObtainAttributes.getDimension(R.styleable.AdaptRoundButtonDrawable_buttonCapsuleRadius, f * 36.0f);
        typedArrayObtainAttributes.recycle();
    }
}
