package miuix.appcompat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import miuix.appcompat.R;
import miuix.appcompat.internal.view.menu.action.EndActionMenuItemView;
import miuix.appcompat.internal.view.menu.action.EndActionMenuView;
import miuix.core.util.MiuixUIUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class BadgeDrawable {
    private static final String CEILING = "99+";
    private static final int CORNER_RADIUS = Integer.MAX_VALUE;
    private static final int END_ACTION_MENU_OFFSET = 6;
    public static final int EXPAND_INSIDE = 4;
    private static final int EXPAND_INSIDE_BADGE_HEIGHT = 24;
    private static final int EXPAND_INSIDE_BADGE_TEXT_SIZE = 14;
    private static final int EXPAND_INSIDE_LARGE_BADGE_WIDTH = 38;
    private static final int EXPAND_INSIDE_MEDIUM_BADGE_WIDTH = 29;
    private static final int EXPAND_INSIDE_SMALL_BADGE_WIDTH = 24;
    public static final int EXPAND_OUTSIDE = 3;
    private static final int EXPAND_OUTSIDE_BADGE_HEIGHT = 16;
    private static final int EXPAND_OUTSIDE_BADGE_TEXT_SIZE = 12;
    private static final int EXPAND_OUTSIDE_LARGE_BADGE_WIDTH = 31;
    private static final int EXPAND_OUTSIDE_MEDIUM_BADGE_WIDTH = 21;
    private static final int EXPAND_OUTSIDE_SMALL_BADGE_WIDTH = 16;
    public static final int GRAVITY_END_BOTTOM = 3;
    public static final int GRAVITY_END_TOP = 2;
    public static final int GRAVITY_START_BOTTOM = 1;
    public static final int GRAVITY_START_TOP = 0;
    private static final int LARGE_BADGE_SIZE = 12;
    private static final int MAJOR_EXTRA_SPACE = 10;
    private static final int MEDIUM_BADGE_SIZE = 8;
    private static final int MINOR_EXTRA_SPACE = 2;
    public static final int SIZE_LARGE = 2;
    public static final int SIZE_MEDIUM = 1;
    public static final int SIZE_SMALL = 0;
    private static final String TAG = "BadgeDrawable";
    private View mAnchor;
    private Drawable mBadgeDrawable;
    private Canvas mCanvas;
    private int mColor;
    private BadgeConfig mConfig;
    private Context mContext;
    private int mGravity;
    private boolean mHandleExtraOffset;
    private int mNumber;
    private Paint mPaint;

    public enum BadgeConfig {
        SIZE_SMALL,
        SIZE_MEDIUM,
        SIZE_LARGE,
        EXPAND_INSIDE,
        EXPAND_OUTSIDE
    }

    public BadgeDrawable(Context context) {
        this(context, 2);
    }

    public BadgeDrawable(Context context, int i) {
        this(context, i, BadgeConfig.SIZE_SMALL, 0);
    }

    public BadgeDrawable(Context context, BadgeConfig badgeConfig) {
        this(context, 2, badgeConfig, 0);
    }

    public BadgeDrawable(Context context, int i, int i2) {
        this(context, i, BadgeConfig.EXPAND_OUTSIDE, i2);
    }

    public BadgeDrawable(Context context, int i, BadgeConfig badgeConfig, int i2) {
        this.mHandleExtraOffset = false;
        setGravity(i);
        this.mContext = context;
        this.mConfig = badgeConfig;
        this.mNumber = i2;
        this.mColor = context.getResources().getColor(R.color.miuix_appcompat_badge_drawable_color);
        this.mBadgeDrawable = getBadgeDrawable(badgeConfig);
    }

    public void setGravity(int i) {
        if (i >= 0 && i <= 3) {
            this.mGravity = i;
        } else {
            Log.d(TAG, "set invalid gravity value.");
            this.mGravity = 2;
        }
    }

    public void attachBadgeDrawable(View view) {
        attachBadgeDrawable(view, this.mGravity);
    }

    public void attachBadgeDrawable(View view, int i) {
        setGravity(i);
        Rect badgeRect = getBadgeRect(view);
        if (badgeRect == null) {
            Log.d(TAG, "attach failed.");
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.setClipChildren(false);
            viewGroup.setClipToPadding(false);
            ViewParent parent = viewGroup.getParent();
            if ((viewGroup instanceof EndActionMenuItemView) && (parent instanceof EndActionMenuView)) {
                EndActionMenuView endActionMenuView = (EndActionMenuView) parent;
                endActionMenuView.setClipChildren(false);
                endActionMenuView.setClipToPadding(false);
            }
        }
        this.mBadgeDrawable.setBounds(badgeRect);
        view.getOverlay().add(this.mBadgeDrawable);
        this.mAnchor = view;
    }

    public void detachBadgeDrawable() {
        View view = this.mAnchor;
        if (view != null) {
            view.getOverlay().clear();
        }
    }

    public void detachBadgeDrawable(View view) {
        if (view != null) {
            view.getOverlay().remove(this.mBadgeDrawable);
        }
    }

    public void setHandleExtraOffset(boolean z) {
        if (this.mHandleExtraOffset != z) {
            this.mHandleExtraOffset = z;
        }
    }

    public void updateNumberOnBadge(int i, View view) {
        if (view == null) {
            return;
        }
        detachBadgeDrawable(view);
        this.mNumber = i;
        Drawable badgeDrawable = getBadgeDrawable(this.mConfig);
        this.mBadgeDrawable = badgeDrawable;
        if (badgeDrawable == null) {
            return;
        }
        attachBadgeDrawable(view);
    }

    private Drawable getBadgeDrawable() {
        return AttributeResolver.resolveDrawable(this.mContext, R.attr.actionBarTabBadgeIcon);
    }

    private Drawable getBadgeDrawable(int i, int i2, float f, int i3) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setSize(i, i2);
        gradientDrawable.setCornerRadius(f);
        gradientDrawable.setColor(i3);
        return gradientDrawable;
    }

    /* JADX INFO: renamed from: miuix.appcompat.widget.BadgeDrawable$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$miuix$appcompat$widget$BadgeDrawable$BadgeConfig;

        static {
            int[] iArr = new int[BadgeConfig.values().length];
            $SwitchMap$miuix$appcompat$widget$BadgeDrawable$BadgeConfig = iArr;
            try {
                iArr[BadgeConfig.EXPAND_INSIDE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$appcompat$widget$BadgeDrawable$BadgeConfig[BadgeConfig.EXPAND_OUTSIDE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miuix$appcompat$widget$BadgeDrawable$BadgeConfig[BadgeConfig.SIZE_MEDIUM.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$miuix$appcompat$widget$BadgeDrawable$BadgeConfig[BadgeConfig.SIZE_LARGE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$miuix$appcompat$widget$BadgeDrawable$BadgeConfig[BadgeConfig.SIZE_SMALL.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    private Drawable getBadgeDrawable(BadgeConfig badgeConfig) {
        int i = AnonymousClass1.$SwitchMap$miuix$appcompat$widget$BadgeDrawable$BadgeConfig[badgeConfig.ordinal()];
        if (i == 1) {
            return drawTextOnBadge(4, getBadgeDrawable(normalizeWidth(4), MiuixUIUtils.dp2px(this.mContext, 24.0f), 2.1474836E9f, this.mColor));
        }
        if (i == 2) {
            return drawTextOnBadge(3, getBadgeDrawable(normalizeWidth(3), MiuixUIUtils.dp2px(this.mContext, 16.0f), 2.1474836E9f, this.mColor));
        }
        if (i == 3) {
            int iDp2px = MiuixUIUtils.dp2px(this.mContext, 8.0f);
            return getBadgeDrawable(iDp2px, iDp2px, 2.1474836E9f, this.mColor);
        }
        if (i == 4) {
            int iDp2px2 = MiuixUIUtils.dp2px(this.mContext, 12.0f);
            return getBadgeDrawable(iDp2px2, iDp2px2, 2.1474836E9f, this.mColor);
        }
        return getBadgeDrawable();
    }

    public Drawable getCurrentBadgeDrawable() {
        return this.mBadgeDrawable;
    }

    private Drawable drawTextOnBadge(int i, Drawable drawable) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        this.mCanvas = canvas;
        drawable.setBounds(0, 0, canvas.getWidth(), this.mCanvas.getHeight());
        drawable.draw(this.mCanvas);
        initPaint(i);
        this.mCanvas.drawText(normalizeText(), this.mCanvas.getWidth() / 2.0f, (this.mCanvas.getHeight() / 2.0f) - ((this.mPaint.descent() + this.mPaint.ascent()) / 2.0f), this.mPaint);
        return new BitmapDrawable(this.mContext.getResources(), bitmapCreateBitmap);
    }

    private void initPaint(int i) {
        if (this.mPaint == null) {
            Paint paint = new Paint();
            this.mPaint = paint;
            paint.setColor(-1);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setTextAlign(Paint.Align.CENTER);
        }
        this.mPaint.setTextSize(i == 3 ? MiuixUIUtils.dp2px(this.mContext, 12.0f) : MiuixUIUtils.dp2px(this.mContext, 14.0f));
    }

    private int normalizeWidth(int i) {
        int i2 = this.mNumber;
        if (i2 >= 0 && i2 < 10) {
            if (i == 4) {
                return MiuixUIUtils.dp2px(this.mContext, 24.0f);
            }
            return MiuixUIUtils.dp2px(this.mContext, 16.0f);
        }
        if (i2 >= 10 && i2 < 100) {
            if (i == 4) {
                return MiuixUIUtils.dp2px(this.mContext, 29.0f);
            }
            return MiuixUIUtils.dp2px(this.mContext, 21.0f);
        }
        if (i2 < 100) {
            return 0;
        }
        if (i == 4) {
            return MiuixUIUtils.dp2px(this.mContext, 38.0f);
        }
        return MiuixUIUtils.dp2px(this.mContext, 31.0f);
    }

    private String normalizeText() {
        int i = this.mNumber;
        if (i > 0 && i < 100) {
            return Integer.toString(i);
        }
        return CEILING;
    }

    private boolean handleExtraOffset(View view) {
        View view2;
        return this.mHandleExtraOffset && (view2 = (View) view.getParent()) != null && view.getTop() == 0 && view.getLeft() == 0 && view.getBottom() == view2.getMeasuredHeight() && view.getRight() == view2.getMeasuredWidth();
    }

    /* JADX WARN: Code duplicated, block: B:33:0x0094  */
    /* JADX WARN: Code duplicated, block: B:40:0x00a9 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:41:0x00ab A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:42:0x00ad  */
    /* JADX WARN: Code duplicated, block: B:44:0x00b0  */
    /* JADX WARN: Code duplicated, block: B:46:0x00b5  */
    /* JADX WARN: Code duplicated, block: B:47:0x00b9  */
    /* JADX WARN: Code duplicated, block: B:48:0x00be  */
    /* JADX WARN: Code duplicated, block: B:50:0x00c2  */
    /* JADX WARN: Code duplicated, block: B:51:0x00c5  */
    /* JADX WARN: Code duplicated, block: B:53:0x00ca  */
    /* JADX WARN: Code duplicated, block: B:55:0x00cd  */
    /* JADX WARN: Code duplicated, block: B:56:0x00cf  */
    /* JADX WARN: Code duplicated, block: B:58:0x00d9  */
    /* JADX WARN: Code duplicated, block: B:60:0x00e0  */
    /* JADX WARN: Code duplicated, block: B:61:0x00e3  */
    /* JADX WARN: Code duplicated, block: B:64:0x00ec  */
    /* JADX WARN: Code duplicated, block: B:66:0x00f1  */
    /* JADX WARN: Code duplicated, block: B:69:0x00f6  */
    /* JADX WARN: Code duplicated, block: B:71:0x00fb  */
    /* JADX WARN: Code duplicated, block: B:73:0x0101  */
    /* JADX WARN: Code duplicated, block: B:75:0x0106  */
    /* JADX WARN: Code duplicated, block: B:78:0x010e  */
    /* JADX WARN: Code duplicated, block: B:80:0x0115  */
    /* JADX WARN: Code duplicated, block: B:83:0x011e  */
    private Rect getBadgeRect(View view) {
        int iDp2px;
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        if (view == null) {
            Log.d(TAG, "can not attach badge on a null object.");
            return null;
        }
        if (this.mBadgeDrawable == null) {
            Log.d(TAG, "can not find badge drawable resource.");
            return null;
        }
        Rect rect = new Rect();
        int intrinsicWidth = this.mBadgeDrawable.getCurrent().getIntrinsicWidth();
        int intrinsicHeight = this.mBadgeDrawable.getCurrent().getIntrinsicHeight();
        view.getDrawingRect(rect);
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(view);
        boolean zHandleExtraOffset = handleExtraOffset(view);
        if (this.mConfig == BadgeConfig.EXPAND_INSIDE || this.mConfig == BadgeConfig.EXPAND_OUTSIDE) {
            iDp2px = MiuixUIUtils.dp2px(this.mContext, 10.0f);
        } else {
            iDp2px = MiuixUIUtils.dp2px(this.mContext, 2.0f);
        }
        int iDp2px2 = MiuixUIUtils.dp2px(this.mContext, 6.0f);
        int i14 = this.mGravity;
        int i15 = 0;
        if (i14 == 0) {
            if ((zIsLayoutRtl && i14 == 0) || (zIsLayoutRtl && i14 == 2)) {
            }
            i = AnonymousClass1.$SwitchMap$miuix$appcompat$widget$BadgeDrawable$BadgeConfig[this.mConfig.ordinal()];
            if (i != 1) {
                i2 = rect.top - iDp2px;
                i3 = i2 + intrinsicHeight;
                if (i15 != 0) {
                    i6 = rect.left - iDp2px;
                    i5 = intrinsicWidth + i6;
                    i7 = i6;
                    i15 = i2;
                    i4 = i7;
                } else {
                    int i16 = rect.right + iDp2px;
                    int i17 = i16 - intrinsicWidth;
                    i15 = i2;
                    i4 = i17;
                    i5 = i16;
                }
            } else if (i != 2) {
                if (i != 3) {
                    i2 = rect.top - (intrinsicHeight / 2);
                    if (i15 != 0) {
                        i10 = rect.left;
                    } else {
                        i10 = rect.right;
                    }
                    i11 = i10 - (intrinsicWidth / 2);
                } else if (i != 5) {
                    i12 = rect.top;
                    if (i15 != 0) {
                        i13 = rect.left;
                    } else {
                        i13 = rect.right - intrinsicWidth;
                    }
                    if (zHandleExtraOffset) {
                        i12 += iDp2px2;
                        if (i15 != 0) {
                            i13 += iDp2px2;
                        } else {
                            i13 -= iDp2px2;
                        }
                    }
                    i15 = i12;
                    i5 = i13 + intrinsicWidth;
                    i3 = i15 + intrinsicHeight;
                    i4 = i13;
                } else {
                    i2 = rect.top - iDp2px;
                    if (i15 != 0) {
                        i11 = rect.left - iDp2px;
                    } else {
                        i11 = (rect.right + iDp2px) - intrinsicWidth;
                    }
                }
                i6 = i11;
                i3 = i2 + intrinsicHeight;
                i5 = intrinsicWidth + i6;
                i7 = i6;
                i15 = i2;
                i4 = i7;
            } else {
                i3 = rect.top + iDp2px;
                if (zHandleExtraOffset) {
                    i3 += iDp2px2;
                }
                int i18 = i3 - intrinsicHeight;
                if (i15 != 0) {
                    i9 = rect.left + iDp2px;
                    if (zHandleExtraOffset) {
                        i9 += iDp2px2;
                    }
                    int i19 = i9;
                    i4 = i19 - intrinsicWidth;
                    i5 = i19;
                } else {
                    i8 = rect.right - iDp2px;
                    if (zHandleExtraOffset) {
                        i8 -= iDp2px2;
                    }
                    int i20 = i8;
                    i5 = i20 + intrinsicWidth;
                    i4 = i20;
                }
                i15 = i18;
            }
        } else {
            if (i14 != 1) {
                if (i14 == 2) {
                    i15 = zIsLayoutRtl ? 1 : 1;
                    i = AnonymousClass1.$SwitchMap$miuix$appcompat$widget$BadgeDrawable$BadgeConfig[this.mConfig.ordinal()];
                    if (i != 1) {
                        i2 = rect.top - iDp2px;
                        i3 = i2 + intrinsicHeight;
                        if (i15 != 0) {
                            i6 = rect.left - iDp2px;
                            i5 = intrinsicWidth + i6;
                            i7 = i6;
                            i15 = i2;
                            i4 = i7;
                        } else {
                            int i110 = rect.right + iDp2px;
                            int i111 = i110 - intrinsicWidth;
                            i15 = i2;
                            i4 = i111;
                            i5 = i110;
                        }
                    } else if (i != 2) {
                        if (i != 3) {
                            i2 = rect.top - (intrinsicHeight / 2);
                            if (i15 != 0) {
                                i10 = rect.left;
                            } else {
                                i10 = rect.right;
                            }
                            i11 = i10 - (intrinsicWidth / 2);
                        } else if (i != 5) {
                            i12 = rect.top;
                            if (i15 != 0) {
                                i13 = rect.left;
                            } else {
                                i13 = rect.right - intrinsicWidth;
                            }
                            if (zHandleExtraOffset) {
                                i12 += iDp2px2;
                                if (i15 != 0) {
                                    i13 += iDp2px2;
                                } else {
                                    i13 -= iDp2px2;
                                }
                            }
                            i15 = i12;
                            i5 = i13 + intrinsicWidth;
                            i3 = i15 + intrinsicHeight;
                            i4 = i13;
                        } else {
                            i2 = rect.top - iDp2px;
                            if (i15 != 0) {
                                i11 = rect.left - iDp2px;
                            } else {
                                i11 = (rect.right + iDp2px) - intrinsicWidth;
                            }
                        }
                        i6 = i11;
                        i3 = i2 + intrinsicHeight;
                        i5 = intrinsicWidth + i6;
                        i7 = i6;
                        i15 = i2;
                        i4 = i7;
                    } else {
                        i3 = rect.top + iDp2px;
                        if (zHandleExtraOffset) {
                            i3 += iDp2px2;
                        }
                        int i112 = i3 - intrinsicHeight;
                        if (i15 != 0) {
                            i9 = rect.left + iDp2px;
                            if (zHandleExtraOffset) {
                                i9 += iDp2px2;
                            }
                            int i113 = i9;
                            i4 = i113 - intrinsicWidth;
                            i5 = i113;
                        } else {
                            i8 = rect.right - iDp2px;
                            if (zHandleExtraOffset) {
                                i8 -= iDp2px2;
                            }
                            int i21 = i8;
                            i5 = i21 + intrinsicWidth;
                            i4 = i21;
                        }
                        i15 = i112;
                    }
                } else if (i14 != 3) {
                    Log.d(TAG, "invalid gravity value.");
                    i4 = 0;
                    i3 = 0;
                    i5 = 0;
                }
            }
            i15 = rect.bottom - intrinsicHeight;
            int i22 = i15 + intrinsicHeight;
            int i23 = ((zIsLayoutRtl || this.mGravity != 1) && !(zIsLayoutRtl && this.mGravity == 3)) ? rect.right - intrinsicWidth : rect.left;
            i5 = intrinsicWidth + i23;
            i7 = i23;
            i3 = i22;
            i4 = i7;
        }
        rect.top = i15;
        rect.left = i4;
        rect.bottom = i3;
        rect.right = i5;
        return rect;
    }
}
