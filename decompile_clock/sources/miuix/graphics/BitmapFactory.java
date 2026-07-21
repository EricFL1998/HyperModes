package miuix.graphics;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import miuix.core.util.Utf8TextUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.io.ResettableInputStream;

/* JADX INFO: loaded from: classes2.dex */
public class BitmapFactory extends android.graphics.BitmapFactory {
    private static final String[] APPELLATION_SUFFIX;
    private static final Pattern ASIALANGPATTERN;
    public static final int BITMAP_COLOR_MODE_DARK = 0;
    public static final int BITMAP_COLOR_MODE_LIGHT = 2;
    public static final int BITMAP_COLOR_MODE_MEDIUM = 1;
    public static final int MODE_DARK = 1;
    public static final int MODE_DAYNIGHT = 2;
    public static final int MODE_LIGHT = 0;
    static RenderScript sRsContext;
    private static final Paint sSrcInPaint;
    static Object sLockForRsContext = new Object();
    private static byte[] PNG_HEAD_FORMAT = {-119, 80, 78, 71, 13, 10, 26, 10};
    private static final ThreadLocal<Canvas> sCanvasCache = new ThreadLocal<>();

    static {
        Paint paint = new Paint(1);
        sSrcInPaint = paint;
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        ASIALANGPATTERN = Pattern.compile("[\u3100-ㄭㆠ-ㆺ一-鿌㐀-䶵豈-龎⼀-⿕⺀-⻳㇀-㇣ᄀ-ᇿꥠ-ꥼힰ-ퟻㄱ-ㆎ가-힣\u3040-ゟ゠-ヿㇰ-ㇿ㆐-㆟ꀀ-ꒌ꒐-꓆]");
        APPELLATION_SUFFIX = new String[]{"老师", "先生", "老板", "仔", "手机", "叔", "阿姨", "宅", "伯", "伯母", "伯父", "哥", "姐", "弟", "妹", "舅", "姑", "父", "主任", "经理", "工作", "同事", "律师", "司机", "师傅", "师父", "爷", "奶", "中介", "董", "总", "太太", "保姆", "某", "秘书", "处长", "局长", "班长", "兄", "助理"};
    }

    protected BitmapFactory() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    private static int computeSampleSize(ResettableInputStream resettableInputStream, int i) {
        if (i <= 0) {
            return 1;
        }
        android.graphics.BitmapFactory.Options bitmapSize = getBitmapSize(resettableInputStream);
        return (int) Math.sqrt((((double) bitmapSize.outWidth) * ((double) bitmapSize.outHeight)) / ((double) i));
    }

    public static android.graphics.BitmapFactory.Options getBitmapSize(ResettableInputStream resettableInputStream) {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeStream(resettableInputStream, null, options);
        return options;
    }

    public static android.graphics.BitmapFactory.Options getBitmapSize(String str) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                android.graphics.BitmapFactory.Options bitmapSize = getBitmapSize(resettableInputStream2);
                resettableInputStream2.close();
                return bitmapSize;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static android.graphics.BitmapFactory.Options getBitmapSize(Context context, Uri uri) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                android.graphics.BitmapFactory.Options bitmapSize = getBitmapSize(resettableInputStream2);
                resettableInputStream2.close();
                return bitmapSize;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(String str, boolean z) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, -1, z);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(String str, int i, int i2, boolean z) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, i, i2, z);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(Context context, Uri uri, boolean z) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, -1, z);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(Context context, Uri uri, int i, int i2, boolean z) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, i, i2, z);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(String str, int i, boolean z) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, i, z);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(Context context, Uri uri, int i, boolean z) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream2, i, z);
                resettableInputStream2.close();
                return bitmapDecodeBitmap;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Bitmap decodeBitmap(ResettableInputStream resettableInputStream, int i, boolean z) throws IOException {
        Bitmap bitmapDecodeStream;
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inSampleSize = 1;
        int i2 = 0;
        options.inScaled = false;
        options.inSampleSize = computeSampleSize(resettableInputStream, i);
        while (true) {
            int i3 = i2 + 1;
            bitmapDecodeStream = null;
            if (i2 >= 3) {
                break;
            }
            try {
                resettableInputStream.reset();
                bitmapDecodeStream = decodeStream(resettableInputStream, null, options);
                break;
            } catch (OutOfMemoryError e) {
                if (z) {
                    options.inSampleSize *= 2;
                    i2 = i3;
                } else {
                    throw e;
                }
            }
        }
        return bitmapDecodeStream;
    }

    public static Bitmap decodeBitmap(ResettableInputStream resettableInputStream, int i, int i2, boolean z) throws IOException {
        int i3 = i * i2;
        if (i <= 0 || i2 <= 0) {
            i3 = -1;
        }
        Bitmap bitmapDecodeBitmap = decodeBitmap(resettableInputStream, i3, z);
        if (bitmapDecodeBitmap == null) {
            return null;
        }
        if (i3 <= 0) {
            return bitmapDecodeBitmap;
        }
        Bitmap bitmapScaleBitmap = scaleBitmap(bitmapDecodeBitmap, i, i2);
        if (bitmapDecodeBitmap != bitmapScaleBitmap) {
            bitmapDecodeBitmap.recycle();
        }
        return bitmapScaleBitmap;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return null;
        }
        if (bitmap.getWidth() == i && bitmap.getHeight() == i2) {
            return bitmap;
        }
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        if (bitmap.getConfig() != null) {
            config = bitmap.getConfig();
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i2, config);
        scaleBitmap(bitmap, bitmapCreateBitmap);
        return bitmapCreateBitmap;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, Bitmap bitmap2) {
        if (bitmap == null || bitmap2 == null) {
            return null;
        }
        if (bitmap.getWidth() == bitmap2.getWidth() && bitmap.getHeight() == bitmap2.getHeight()) {
            return bitmap;
        }
        Canvas canvas = new Canvas(bitmap2);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight()), paint);
        return bitmap2;
    }

    public static Bitmap cropBitmap(Bitmap bitmap, CropOption cropOption) {
        if (bitmap != null) {
            return cropBitmap(bitmap, copyToEmpty(bitmap), cropOption);
        }
        return null;
    }

    public static Bitmap cropBitmap(Bitmap bitmap, Bitmap bitmap2, CropOption cropOption) {
        if (bitmap == null || bitmap2 == null) {
            return null;
        }
        CropOption cropOption2 = cropOption == null ? new CropOption() : cropOption;
        Rect rect = cropOption2.srcBmpDrawingArea;
        if (rect == null) {
            rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
        int iBetween = between(0, bitmap.getWidth() - 1, rect.left);
        int iBetween2 = between(iBetween, bitmap.getWidth(), rect.right);
        int iBetween3 = between(0, bitmap.getHeight() - 1, rect.top);
        int iBetween4 = between(iBetween3, bitmap.getHeight(), rect.bottom);
        int i = iBetween2 - iBetween;
        int i2 = iBetween4 - iBetween3;
        int width = bitmap2.getWidth();
        int height = bitmap2.getHeight();
        cropOption2.borderWidth = between(0, Math.min(width, height) / 2, cropOption2.borderWidth);
        cropOption2.rx = between(0, width / 2, cropOption2.rx);
        cropOption2.ry = between(0, height / 2, cropOption2.ry);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint.setDither(true);
        Canvas canvas = new Canvas(bitmap2);
        canvas.drawARGB(0, 0, 0, 0);
        if (cropOption2.rx - cropOption2.borderWidth > 0 && cropOption2.ry - cropOption2.borderWidth > 0) {
            canvas.drawRoundRect(new RectF(cropOption2.borderWidth, cropOption2.borderWidth, width - cropOption2.borderWidth, height - cropOption2.borderWidth), cropOption2.rx - cropOption2.borderWidth, cropOption2.ry - cropOption2.borderWidth, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }
        float f = i;
        float f2 = width - (cropOption2.borderWidth * 2);
        float f3 = i2;
        float f4 = height - (cropOption2.borderWidth * 2);
        float fMin = Math.min((f * 1.0f) / f2, (1.0f * f3) / f4);
        int i3 = (int) ((f - (f2 * fMin)) / 2.0f);
        int i4 = (int) ((f3 - (f4 * fMin)) / 2.0f);
        canvas.drawBitmap(bitmap, new Rect(iBetween + i3, iBetween3 + i4, iBetween2 - i3, iBetween4 - i4), new Rect(cropOption2.borderWidth, cropOption2.borderWidth, width - cropOption2.borderWidth, height - cropOption2.borderWidth), paint);
        if (cropOption2.borderWidth > 0 && (cropOption2.borderColor >>> 24) != 0) {
            paint.setColor(cropOption2.borderColor);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            canvas.drawRoundRect(new RectF(0.0f, 0.0f, width, height), cropOption2.rx, cropOption2.ry, paint);
        }
        return bitmap2;
    }

    private static int between(int i, int i2, int i3) {
        return Math.min(i2, Math.max(i, i3));
    }

    public static boolean saveToFile(Bitmap bitmap, String str) throws IOException {
        return saveToFile(bitmap, str, false);
    }

    public static boolean saveToFile(Bitmap bitmap, String str, boolean z) throws Throwable {
        if (bitmap == null) {
            return false;
        }
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream fileOutputStream2 = new FileOutputStream(str);
            try {
                bitmap.compress(z ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 100, fileOutputStream2);
                fileOutputStream2.close();
                return true;
            } catch (Throwable th) {
                th = th;
                fileOutputStream = fileOutputStream2;
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean isPngFormat(Context context, Uri uri) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(context, uri);
            try {
                boolean zIsPngFormat = isPngFormat(resettableInputStream2);
                resettableInputStream2.close();
                return zIsPngFormat;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean isPngFormat(String str) throws Throwable {
        ResettableInputStream resettableInputStream = null;
        try {
            ResettableInputStream resettableInputStream2 = new ResettableInputStream(str);
            try {
                boolean zIsPngFormat = isPngFormat(resettableInputStream2);
                resettableInputStream2.close();
                return zIsPngFormat;
            } catch (Throwable th) {
                th = th;
                resettableInputStream = resettableInputStream2;
                if (resettableInputStream != null) {
                    resettableInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static boolean isPngFormat(ResettableInputStream resettableInputStream) throws IOException {
        int length = PNG_HEAD_FORMAT.length;
        byte[] bArr = new byte[length];
        if (resettableInputStream.read(bArr) >= length) {
            return isPngFormat(bArr);
        }
        return false;
    }

    private static boolean isPngFormat(byte[] bArr) {
        if (bArr == null || bArr.length < PNG_HEAD_FORMAT.length) {
            return false;
        }
        int i = 0;
        while (true) {
            byte[] bArr2 = PNG_HEAD_FORMAT;
            if (i >= bArr2.length) {
                return true;
            }
            if (bArr[i] != bArr2[i]) {
                return false;
            }
            i++;
        }
    }

    private static Bitmap copyToEmpty(Bitmap bitmap) {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        if (bitmap.getConfig() != null) {
            config = bitmap.getConfig();
        }
        return Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
    }

    public static Bitmap fastBlur(Context context, Bitmap bitmap, int i) {
        Bitmap bitmapCopyToEmpty = copyToEmpty(bitmap);
        fastBlur(context, bitmap, bitmapCopyToEmpty, i);
        return bitmapCopyToEmpty;
    }

    public static Bitmap fastBlur(Context context, Bitmap bitmap, Bitmap bitmap2, int i) {
        if (bitmap == null) {
            return null;
        }
        if (bitmap2 == null || bitmap.getWidth() != bitmap2.getWidth() || bitmap.getHeight() != bitmap2.getHeight()) {
            bitmap2 = copyToEmpty(bitmap);
        }
        fastblur_v17(context, bitmap, bitmap2, i);
        return bitmap2;
    }

    private static Bitmap fastblur_v17(Context context, Bitmap bitmap, Bitmap bitmap2, int i) {
        int i2 = 1;
        while (i > 25) {
            i2 *= 2;
            i /= 2;
        }
        Bitmap bitmapScaleBitmap = i2 == 1 ? bitmap : scaleBitmap(bitmap, Math.max(bitmap.getWidth() / i2, 1), Math.max(bitmap.getHeight() / i2, 1));
        if (context.getApplicationContext() == null) {
            context = new ContextWrapper(context) { // from class: miuix.graphics.BitmapFactory.1
                @Override // android.content.ContextWrapper, android.content.Context
                public Context getApplicationContext() {
                    return this;
                }
            };
        }
        try {
            Object[] enumConstants = context.getClassLoader().loadClass("android.graphics.Bitmap$Config").getEnumConstants();
            if (enumConstants != null) {
                for (Object obj : enumConstants) {
                    Enum r6 = (Enum) obj;
                    if ("RGBA_F16".equals(r6.name()) && bitmapScaleBitmap.getConfig() == r6) {
                        bitmapScaleBitmap = transferF16ToARGB(bitmapScaleBitmap);
                        break;
                    }
                }
            }
        } catch (Exception unused) {
        }
        synchronized (sLockForRsContext) {
            if (sRsContext == null) {
                sRsContext = RenderScript.create(context);
            }
            Bitmap bitmap3 = i2 == 1 ? bitmap2 : bitmapScaleBitmap;
            if (bitmapScaleBitmap.getRowBytes() != bitmap3.getRowBytes()) {
                bitmapScaleBitmap = bitmapScaleBitmap.copy(Bitmap.Config.ARGB_8888, true);
            }
            Allocation allocationCreateFromBitmap = Allocation.createFromBitmap(sRsContext, bitmapScaleBitmap);
            Allocation allocationCreateTyped = Allocation.createTyped(sRsContext, allocationCreateFromBitmap.getType());
            RenderScript renderScript = sRsContext;
            ScriptIntrinsicBlur scriptIntrinsicBlurCreate = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            scriptIntrinsicBlurCreate.setRadius(i);
            scriptIntrinsicBlurCreate.setInput(allocationCreateFromBitmap);
            scriptIntrinsicBlurCreate.forEach(allocationCreateTyped);
            allocationCreateTyped.copyTo(bitmap3);
            if (bitmap3 != bitmap2) {
                scaleBitmap(bitmap3, bitmap2);
            }
            if (bitmapScaleBitmap != bitmap) {
                bitmapScaleBitmap.recycle();
            }
            if (bitmap3 != bitmap2) {
                bitmap3.recycle();
            }
            allocationCreateFromBitmap.destroy();
            allocationCreateTyped.destroy();
            scriptIntrinsicBlurCreate.destroy();
        }
        return bitmap2;
    }

    private static Bitmap transferF16ToARGB(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == 0 || height == 0) {
            return bitmap;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        Paint paint = new Paint();
        paint.setFlags(3);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bitmapCreateBitmap;
    }

    public static class CropOption {
        public int borderColor;
        public int borderWidth;
        public int rx;
        public int ry;
        public Rect srcBmpDrawingArea;

        public CropOption() {
        }

        public CropOption(int i, int i2, int i3, int i4) {
            this.rx = i;
            this.ry = i2;
            this.borderWidth = i3;
            this.borderColor = i4;
        }

        public CropOption(CropOption cropOption) {
            this.rx = cropOption.rx;
            this.ry = cropOption.ry;
            this.borderWidth = cropOption.borderWidth;
            this.borderColor = cropOption.borderColor;
            this.srcBmpDrawingArea = cropOption.srcBmpDrawingArea;
        }
    }

    private static Canvas getCachedCanvas() {
        ThreadLocal<Canvas> threadLocal = sCanvasCache;
        Canvas canvas = threadLocal.get();
        if (canvas != null) {
            return canvas;
        }
        Canvas canvas2 = new Canvas();
        threadLocal.set(canvas2);
        return canvas2;
    }

    public static Bitmap createPhoto(Context context, Bitmap bitmap) {
        return createPhoto(context, bitmap, context.getResources().getDimensionPixelSize(R.dimen.contact_photo_width));
    }

    public static Bitmap createPhoto(Context context, Bitmap bitmap, int i) {
        Resources resources = context.getResources();
        return composeBitmap(bitmap, null, resources.getDrawable(R.drawable.ic_contact_photo_mask), resources.getDrawable(R.drawable.ic_contact_photo_fg), resources.getDrawable(R.drawable.ic_contact_photo_bg), i);
    }

    public static Bitmap composeBitmap(Bitmap bitmap, Bitmap bitmap2, Drawable drawable, Drawable drawable2, Drawable drawable3) {
        Rect rect;
        Rect rect2 = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        if (bitmap2 != null) {
            rect = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        } else {
            rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
        return composeBitmap(bitmap, bitmap2, drawable, drawable2, drawable3, rect2, rect);
    }

    public static Bitmap composeBitmap(Bitmap bitmap, Bitmap bitmap2, Drawable drawable, Drawable drawable2, Drawable drawable3, int i) {
        return composeBitmap(bitmap, bitmap2, drawable, drawable2, drawable3, null, new Rect(0, 0, i, i));
    }

    public static Bitmap composeBitmap(Bitmap bitmap, Bitmap bitmap2, Drawable drawable, Drawable drawable2, Drawable drawable3, Rect rect, Rect rect2) {
        if (bitmap2 == null && rect2 == null) {
            return null;
        }
        if (bitmap2 == null) {
            if (rect2.height() <= 0 || rect2.width() <= 0) {
                return null;
            }
            bitmap2 = Bitmap.createBitmap(rect2.width(), rect2.height(), Bitmap.Config.ARGB_8888);
        } else if (rect2 == null) {
            rect2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        }
        Bitmap bitmapMaskOutBitmap = maskOutBitmap(bitmap, drawable, null, rect, rect2);
        if (bitmapMaskOutBitmap != null) {
            Canvas cachedCanvas = getCachedCanvas();
            cachedCanvas.setBitmap(bitmap2);
            if (drawable3 != null) {
                drawable3.setBounds(rect2);
                drawable3.draw(cachedCanvas);
            }
            cachedCanvas.drawBitmap(bitmapMaskOutBitmap, rect2, rect2, (Paint) null);
            bitmapMaskOutBitmap.recycle();
            if (drawable2 != null) {
                drawable2.setBounds(rect2);
                drawable2.draw(cachedCanvas);
            }
        } else {
            Log.e("BitmapFactory", "Get mask bitmap failed");
        }
        return bitmap2;
    }

    public static Bitmap maskOutBitmap(Bitmap bitmap, Drawable drawable, Bitmap bitmap2, Rect rect, Rect rect2) {
        int i;
        if (bitmap2 == null && rect2 == null) {
            return null;
        }
        int i2 = 0;
        if (bitmap2 == null) {
            if (rect2.height() <= 0 || rect2.width() <= 0) {
                return null;
            }
            bitmap2 = Bitmap.createBitmap(rect2.width(), rect2.height(), Bitmap.Config.ARGB_8888);
        } else if (rect2 == null) {
            rect2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        }
        Canvas cachedCanvas = getCachedCanvas();
        cachedCanvas.setBitmap(bitmap2);
        cachedCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (drawable != null) {
            drawable.setBounds(rect2);
            drawable.draw(cachedCanvas);
        }
        if (rect == null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int iWidth = rect2.width();
            float fHeight = rect2.height();
            float f = iWidth;
            float f2 = fHeight / f;
            float f3 = width;
            float f4 = f3 / f;
            float f5 = height;
            float f6 = f5 / fHeight;
            if (f4 > f6) {
                int i3 = (int) (f5 / f2);
                i2 = (width - i3) / 2;
                width = i3;
                i = 0;
            } else if (f4 < f6) {
                int i4 = (int) (f2 * f3);
                i = (height - i4) / 2;
                height = i4;
            } else {
                i = 0;
            }
            rect = new Rect(i2, i, width + i2, height + i);
        }
        cachedCanvas.drawBitmap(bitmap, rect, rect2, sSrcInPaint);
        return bitmap2;
    }

    @Deprecated
    public static Bitmap createNameBitmap(Context context, String str, int i) {
        return createNameBitmap(context, str, i, 0, 0);
    }

    @Deprecated
    public static Bitmap createNameBitmap(Context context, String str, int i, int i2, int i3) {
        return createNameBitmap(context, str, i, i2, i3, 0);
    }

    public static Bitmap createNameBitmap(Context context, String str, int i, int i2) {
        return createNameBitmap(context, str, i, 0, 0, i2);
    }

    public static Bitmap createNameBitmap(Context context, String str, int i, int i2, int i3, int i4) {
        Drawable drawable;
        int color;
        int iResolveColor;
        if (str == null) {
            return null;
        }
        String wordFromName = getWordFromName(str.trim());
        if (TextUtils.isEmpty(wordFromName)) {
            return null;
        }
        if (i2 != 0) {
            drawable = context.getResources().getDrawable(i2);
        } else if (i4 == 0) {
            drawable = context.getResources().getDrawable(R.drawable.word_photo_bg_light);
        } else if (1 == i4) {
            drawable = context.getResources().getDrawable(R.drawable.word_photo_bg_dark);
        } else if (2 == i4) {
            drawable = AttributeResolver.resolveDrawable(context, R.attr.wordPhotoBackground);
            if (drawable == null) {
                if (ViewUtils.isNightMode(context)) {
                    drawable = context.getResources().getDrawable(R.drawable.word_photo_bg_dark);
                } else {
                    drawable = context.getResources().getDrawable(R.drawable.word_photo_bg_light);
                }
            }
        } else {
            throw new IllegalArgumentException("unknown mode when get drawable: " + i4);
        }
        drawable.setBounds(new Rect(0, 0, i, i));
        if (i3 != 0) {
            color = context.getResources().getColor(i3);
        } else if (i4 == 0) {
            color = context.getResources().getColor(R.color.word_photo_color);
        } else if (1 == i4) {
            color = context.getResources().getColor(R.color.word_photo_color_dark);
        } else if (2 == i4) {
            try {
                iResolveColor = AttributeResolver.resolveColor(context, R.attr.wordPhotoTextColor);
            } catch (Exception unused) {
                iResolveColor = -1;
            }
            if (iResolveColor != -1) {
                color = iResolveColor;
            } else if (ViewUtils.isNightMode(context)) {
                color = context.getResources().getColor(R.color.word_photo_color_dark);
            } else {
                color = context.getResources().getColor(R.color.word_photo_color);
            }
        } else {
            throw new IllegalArgumentException("unknown mode when get photo color: " + i4);
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.draw(canvas);
        Paint paint = new Paint(1);
        paint.setFilterBitmap(true);
        paint.setColor(color);
        paint.setTextSize(i * 0.6f);
        Rect rect = new Rect();
        paint.getTextBounds(wordFromName, 0, wordFromName.length(), rect);
        canvas.drawText(wordFromName, (int) (((double) (i - (rect.right + rect.left))) * 0.5d), (int) (((double) (i - (rect.top + rect.bottom))) * 0.5d), paint);
        return bitmapCreateBitmap;
    }

    private static String getWordFromName(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (containsEastAsianCharacter(str)) {
            String strRemoveSuffix = removeSuffix(str);
            if (TextUtils.isEmpty(strRemoveSuffix)) {
                return null;
            }
            int length = strRemoveSuffix.length();
            return strRemoveSuffix.substring(length - 1, length).trim();
        }
        return Utf8TextUtils.subString(str, 0, 1).toUpperCase();
    }

    private static boolean containsEastAsianCharacter(String str) {
        return ASIALANGPATTERN.matcher(str).find();
    }

    /* JADX WARN: Code duplicated, block: B:20:0x004f A[LOOP:1: B:8:0x000c->B:20:0x004f, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:33:0x0052 A[EDGE_INSN: B:33:0x0052->B:21:0x0052 BREAK  A[LOOP:1: B:8:0x000c->B:20:0x004f], SYNTHETIC] */
    private static String removeSuffix(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String strTrim = str;
        do {
            int i = 0;
            boolean z = false;
            while (true) {
                String[] strArr = APPELLATION_SUFFIX;
                if (i >= strArr.length) {
                    break;
                }
                if (strTrim.endsWith(strArr[i])) {
                    strTrim = strTrim.substring(0, strTrim.length() - strArr[i].length());
                } else {
                    if (!containsEastAsianCharacter(String.valueOf(strTrim.charAt(strTrim.length() - 1)))) {
                        strTrim = strTrim.substring(0, strTrim.length() - 1);
                    }
                    if (TextUtils.isEmpty(strTrim)) {
                        break;
                    }
                    i++;
                }
                z = true;
                if (TextUtils.isEmpty(strTrim)) {
                    break;
                    break;
                }
                i++;
            }
            if (!z) {
                break;
            }
        } while (!TextUtils.isEmpty(strTrim));
        if (strTrim != null) {
            strTrim = strTrim.trim();
        }
        return TextUtils.isEmpty(strTrim) ? str.substring(str.length() - 1) : strTrim;
    }

    public static int getBitmapColorMode(Bitmap bitmap, int i) {
        int height = bitmap.getHeight() / i;
        int width = bitmap.getWidth() / i;
        int i2 = (width * height) / 5;
        Bitmap bitmapScaleBitmap = scaleBitmap(bitmap, width, height);
        int i3 = 2;
        int i4 = 0;
        for (int i5 = 0; i5 < width; i5++) {
            for (int i6 = 0; i6 < height; i6++) {
                int pixel = bitmapScaleBitmap.getPixel(i5, i6);
                if (((int) ((((double) ((16711680 & pixel) >> 16)) * 0.3d) + (((double) ((65280 & pixel) >> 8)) * 0.59d) + (((double) (pixel & 255)) * 0.11d))) < 180) {
                    i4++;
                    if (i4 > i2) {
                        i3 = 1;
                    }
                    if (i4 > i2 * 2) {
                        i3 = 0;
                        break;
                    }
                }
            }
        }
        if (bitmapScaleBitmap != bitmap) {
            bitmapScaleBitmap.recycle();
        }
        return i3;
    }

    public static Bitmap getRoundBitmap(Bitmap bitmap, float f) {
        return getRoundBitmap(bitmap, f, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap getRoundBitmap(Bitmap bitmap, float f, Bitmap.Config config) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, f, f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return bitmapCreateBitmap;
    }
}
