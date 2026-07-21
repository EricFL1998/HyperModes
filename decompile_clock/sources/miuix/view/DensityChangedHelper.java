package miuix.view;

import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/* JADX INFO: loaded from: classes3.dex */
public class DensityChangedHelper {
    public static void updateView(View view, int i) {
        float f = i;
        updateView(view, f / 160.0f, (view.getResources().getConfiguration().densityDpi * 1.0f) / f);
    }

    public static void updateView(View view, int i, float f) {
        updateView(view, i / 160.0f, f);
    }

    public static void updateView(View view, float f, float f2) {
        updateViewSize(view, f2);
        updateViewPadding(view, f2);
        updateViewMargin(view, f2);
        if (view instanceof TextView) {
            updateTextSize((TextView) view, f);
        }
    }

    public static void updateTextSize(TextView textView, int i) {
        updateTextSize(textView, i, 2);
    }

    public static void updateTextSize(TextView textView, float f) {
        updateTextSize(textView, f, 2);
    }

    public static void updateTextSizeDefaultUnit(TextView textView, float f) {
        if (Build.VERSION.SDK_INT >= 30) {
            textView.setTextSize(textView.getTextSizeUnit(), f);
        } else {
            textView.setTextSize(2, f);
        }
    }

    public static void updateTextSizeSpUnit(TextView textView, float f) {
        textView.setTextSize(2, f);
    }

    public static void updateTextSizeDpUnit(TextView textView, float f) {
        textView.setTextSize(1, f);
    }

    public static void updateTextSize(TextView textView, int i, int i2) {
        updateTextSize(textView, i / 160.0f, i2);
    }

    public static void updateTextSize(TextView textView, float f, int i) {
        if (Build.VERSION.SDK_INT >= 30) {
            textView.setTextSize(textView.getTextSizeUnit(), textView.getTextSize() / f);
        } else {
            textView.setTextSize(i, textView.getTextSize() / f);
        }
    }

    public static void updateViewPadding(View view, int i) {
        updateViewPadding(view, (view.getResources().getConfiguration().densityDpi * 1.0f) / i);
    }

    public static void updateViewSize(View view, int i) {
        updateViewSize(view, (view.getResources().getConfiguration().densityDpi * 1.0f) / i);
    }

    public static void updateViewMargin(View view, int i) {
        updateViewMargin(view, (view.getResources().getConfiguration().densityDpi * 1.0f) / i);
    }

    public static void updateViewPadding(View view, float f) {
        view.setPadding((int) (view.getPaddingLeft() * f), (int) (view.getPaddingTop() * f), (int) (view.getPaddingRight() * f), (int) (view.getPaddingBottom() * f));
    }

    public static void updateViewSize(View view, float f) {
        boolean z;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        boolean z2 = true;
        if (layoutParams.width > 0) {
            layoutParams.width = (int) (layoutParams.width * f);
            z = true;
        } else {
            z = false;
        }
        if (layoutParams.height > 0) {
            layoutParams.height = (int) (layoutParams.height * f);
        } else {
            z2 = z;
        }
        if (z2) {
            view.setLayoutParams(layoutParams);
        }
    }

    public static void updateViewMargin(View view, float f) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.leftMargin = (int) (marginLayoutParams.leftMargin * f);
            marginLayoutParams.topMargin = (int) (marginLayoutParams.topMargin * f);
            marginLayoutParams.rightMargin = (int) (marginLayoutParams.rightMargin * f);
            marginLayoutParams.bottomMargin = (int) (marginLayoutParams.bottomMargin * f);
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void updateViewPaddingByResource(Resources resources, View view, int i) {
        if (i == -1) {
            return;
        }
        int dimensionPixelSize = resources.getDimensionPixelSize(i);
        view.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
    }

    public static void updateViewPaddingByResource(Resources resources, View view, int i, int i2, int i3, int i4) {
        int dimensionPixelSize;
        int dimensionPixelSize2;
        int dimensionPixelSize3;
        int paddingTop;
        if (i == i3 && i != -1) {
            dimensionPixelSize = resources.getDimensionPixelSize(i);
            dimensionPixelSize2 = dimensionPixelSize;
        } else {
            dimensionPixelSize = i != -1 ? resources.getDimensionPixelSize(i) : 0;
            dimensionPixelSize2 = i3 != -1 ? resources.getDimensionPixelSize(i3) : 0;
        }
        if (i2 == i4 && i2 != -1) {
            paddingTop = resources.getDimensionPixelSize(i2);
            dimensionPixelSize3 = paddingTop;
        } else {
            int dimensionPixelSize4 = i2 != -1 ? resources.getDimensionPixelSize(i2) : 0;
            dimensionPixelSize3 = i4 != -1 ? resources.getDimensionPixelSize(i4) : 0;
            paddingTop = dimensionPixelSize4;
        }
        if (i == -1) {
            dimensionPixelSize = view.getPaddingLeft();
        }
        if (i2 == -1) {
            paddingTop = view.getPaddingTop();
        }
        if (i3 == -1) {
            dimensionPixelSize2 = view.getPaddingLeft();
        }
        if (i4 == -1) {
            dimensionPixelSize3 = view.getPaddingBottom();
        }
        view.setPadding(dimensionPixelSize, paddingTop, dimensionPixelSize2, dimensionPixelSize3);
    }

    public static void updateViewMarginByResource(Resources resources, View view, int i) {
        if (i == -1) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            int dimensionPixelSize = resources.getDimensionPixelSize(i);
            marginLayoutParams.leftMargin = dimensionPixelSize;
            marginLayoutParams.topMargin = dimensionPixelSize;
            marginLayoutParams.rightMargin = dimensionPixelSize;
            marginLayoutParams.bottomMargin = dimensionPixelSize;
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void updateViewMarginByResource(Resources resources, View view, int i, int i2, int i3, int i4) {
        int dimensionPixelSize;
        int dimensionPixelSize2;
        int dimensionPixelSize3;
        int dimensionPixelSize4;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            if (i == i3 && i != -1) {
                dimensionPixelSize = resources.getDimensionPixelSize(i);
                dimensionPixelSize2 = dimensionPixelSize;
            } else {
                dimensionPixelSize = i != -1 ? resources.getDimensionPixelSize(i) : 0;
                dimensionPixelSize2 = i3 != -1 ? resources.getDimensionPixelSize(i3) : 0;
            }
            if (i2 == i4 && i2 != -1) {
                dimensionPixelSize4 = resources.getDimensionPixelSize(i2);
                dimensionPixelSize3 = dimensionPixelSize4;
            } else {
                int dimensionPixelSize5 = i2 != -1 ? resources.getDimensionPixelSize(i2) : 0;
                dimensionPixelSize3 = i4 != -1 ? resources.getDimensionPixelSize(i4) : 0;
                dimensionPixelSize4 = dimensionPixelSize5;
            }
            if (i != -1) {
                marginLayoutParams.leftMargin = dimensionPixelSize;
            }
            if (i2 != -1) {
                marginLayoutParams.topMargin = dimensionPixelSize4;
            }
            if (i3 != -1) {
                marginLayoutParams.rightMargin = dimensionPixelSize2;
            }
            if (i4 != -1) {
                marginLayoutParams.bottomMargin = dimensionPixelSize3;
            }
            view.setLayoutParams(marginLayoutParams);
        }
    }

    public static void updateViewSizeByResource(Resources resources, View view, int i) {
        if (i == -1) {
            return;
        }
        updateViewSizeByResource(resources, view, i, i);
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0037 A[PHI: r3
  0x0037: PHI (r3v3 boolean) = (r3v1 boolean), (r3v1 boolean), (r3v4 boolean) binds: [B:15:0x002a, B:17:0x002e, B:9:0x0018] A[DONT_GENERATE, DONT_INLINE]] */
    public static void updateViewSizeByResource(Resources resources, View view, int i, int i2) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        boolean z = true;
        boolean z2 = false;
        if (i == i2 && i != -1) {
            int dimensionPixelSize = resources.getDimensionPixelSize(i);
            if (layoutParams.width > 0) {
                layoutParams.width = dimensionPixelSize;
                z2 = true;
            }
            if (layoutParams.height > 0) {
                layoutParams.height = dimensionPixelSize;
            } else {
                z = z2;
            }
        } else {
            if (i != -1 && layoutParams.width > 0) {
                layoutParams.width = resources.getDimensionPixelSize(i);
                z2 = true;
            }
            if (i2 == -1 || layoutParams.height <= 0) {
                z = z2;
            } else {
                layoutParams.height = resources.getDimensionPixelSize(i2);
            }
        }
        if (z) {
            view.setLayoutParams(layoutParams);
        }
    }
}
