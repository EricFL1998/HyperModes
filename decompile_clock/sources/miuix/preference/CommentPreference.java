package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import miuix.core.util.RomUtils;

/* JADX INFO: loaded from: classes3.dex */
public class CommentPreference extends BasePreference {
    public static final int VERTICAL_MARGIN_BOTTOM_LARGE = 1;
    private static final int VERTICAL_MARGIN_DEFAULT = -1;
    public static final int VERTICAL_MARGIN_LARGE = 0;
    private int mLargeVerticalMargin;
    private int mSmallVerticalMargin;
    private CharSequence mText;
    private int mVerticalMarginChoice;

    public CommentPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.Miuix_Preference_CommentPreference);
    }

    public CommentPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.commentPreferenceStyle);
    }

    public CommentPreference(Context context) {
        this(context, null);
    }

    public CommentPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mText = "";
        this.mVerticalMarginChoice = -1;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CommentPreference, i, i2);
        this.mVerticalMarginChoice = typedArrayObtainStyledAttributes.getInt(R.styleable.CommentPreference_verticalMarginChoice, -1);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.CommentPreference_android_text, 0);
        if (resourceId != 0) {
            this.mText = context.getString(resourceId);
        } else {
            this.mText = typedArrayObtainStyledAttributes.getText(R.styleable.CommentPreference_android_text);
        }
        setClickable(typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_clickable, false));
        setCardStyleEnable(typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_cardEnable, false));
        setTouchAnimationEnable(typedArrayObtainStyledAttributes.getBoolean(R.styleable.BasePreference_touchAnimationEnable, false));
        this.mLargeVerticalMargin = context.getResources().getDimensionPixelSize(R.dimen.miuix_preference_comment_margin_vertical_traditional);
        this.mSmallVerticalMargin = context.getResources().getDimensionPixelSize(R.dimen.miuix_preference_comment_margin_vertical_card);
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setText(int i) {
        setText(getContext().getString(i));
    }

    public void setText(String str) {
        if (TextUtils.equals(str, this.mText)) {
            return;
        }
        this.mText = str;
        notifyChanged();
    }

    public CharSequence getText() {
        return this.mText;
    }

    public void setVerticalMarginChoice(int i) {
        if (this.mVerticalMarginChoice != i) {
            this.mVerticalMarginChoice = i;
            notifyChanged();
        }
    }

    public int getVerticalMarginChoice() {
        return this.mVerticalMarginChoice;
    }

    @Override // miuix.preference.BasePreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        TextView textView = (TextView) view.findViewById(R.id.content);
        LinearLayout linearLayout = view instanceof LinearLayout ? (LinearLayout) view : null;
        if (textView != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(new int[]{R.attr.preferenceCardStyleEnable});
            try {
                int i = typedArrayObtainStyledAttributes.getInt(0, 1);
                if (typedArrayObtainStyledAttributes != null) {
                    typedArrayObtainStyledAttributes.recycle();
                }
                int[] verticalMargin = getVerticalMargin(i == 2 || (RomUtils.getHyperOsVersion() > 1 && i == 1));
                if (linearLayout != null && verticalMargin[0] != verticalMargin[1]) {
                    linearLayout.setGravity(48);
                } else if (linearLayout != null) {
                    linearLayout.setGravity(16);
                }
                layoutParams.setMargins(0, verticalMargin[0], 0, verticalMargin[1]);
                textView.setText(this.mText);
            } catch (Throwable th) {
                if (typedArrayObtainStyledAttributes != null) {
                    try {
                        typedArrayObtainStyledAttributes.recycle();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    private int[] getVerticalMargin(boolean z) {
        int i;
        int i2;
        int i3 = this.mVerticalMarginChoice;
        if (i3 != -1) {
            if (i3 == 1) {
                i = this.mSmallVerticalMargin;
                i2 = this.mLargeVerticalMargin;
            } else {
                i = this.mLargeVerticalMargin;
            }
            return new int[]{i, i2};
        }
        if (z) {
            i = this.mSmallVerticalMargin;
        } else {
            i = this.mLargeVerticalMargin;
        }
        i2 = i;
        return new int[]{i, i2};
    }
}
