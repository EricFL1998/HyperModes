package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import com.android.deskclock.R2;

/* JADX INFO: loaded from: classes.dex */
public class Placeholder extends View {
    private View mContent;
    private int mContentId;
    private int mEmptyVisibility;

    public Placeholder(Context context) {
        super(context);
        this.mContentId = -1;
        this.mContent = null;
        this.mEmptyVisibility = 4;
        init(null);
    }

    public Placeholder(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContentId = -1;
        this.mContent = null;
        this.mEmptyVisibility = 4;
        init(attrs);
    }

    public Placeholder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContentId = -1;
        this.mContent = null;
        this.mEmptyVisibility = 4;
        init(attrs);
    }

    public Placeholder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        this.mContentId = -1;
        this.mContent = null;
        this.mEmptyVisibility = 4;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        super.setVisibility(this.mEmptyVisibility);
        this.mContentId = -1;
        if (attrs != null) {
            TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_placeholder);
            int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArrayObtainStyledAttributes.getIndex(i);
                if (index == R.styleable.ConstraintLayout_placeholder_content) {
                    this.mContentId = typedArrayObtainStyledAttributes.getResourceId(index, this.mContentId);
                } else if (index == R.styleable.ConstraintLayout_placeholder_placeholder_emptyVisibility) {
                    this.mEmptyVisibility = typedArrayObtainStyledAttributes.getInt(index, this.mEmptyVisibility);
                }
            }
            typedArrayObtainStyledAttributes.recycle();
        }
    }

    public void setEmptyVisibility(int visibility) {
        this.mEmptyVisibility = visibility;
    }

    public int getEmptyVisibility() {
        return this.mEmptyVisibility;
    }

    public View getContent() {
        return this.mContent;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            canvas.drawRGB(R2.attr.actionBarStrategy, R2.attr.actionBarStrategy, R2.attr.actionBarStrategy);
            Paint paint = new Paint();
            paint.setARGB(255, R2.attr.actionBarSecondaryTabBarStyle, R2.attr.actionBarSecondaryTabBarStyle, R2.attr.actionBarSecondaryTabBarStyle);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, 0));
            Rect rect = new Rect();
            canvas.getClipBounds(rect);
            paint.setTextSize(rect.height());
            int iHeight = rect.height();
            int iWidth = rect.width();
            paint.setTextAlign(Paint.Align.LEFT);
            paint.getTextBounds("?", 0, 1, rect);
            canvas.drawText("?", ((iWidth / 2.0f) - (rect.width() / 2.0f)) - rect.left, ((iHeight / 2.0f) + (rect.height() / 2.0f)) - rect.bottom, paint);
        }
    }

    public void updatePreLayout(ConstraintLayout container) {
        if (this.mContentId == -1 && !isInEditMode()) {
            setVisibility(this.mEmptyVisibility);
        }
        View viewFindViewById = container.findViewById(this.mContentId);
        this.mContent = viewFindViewById;
        if (viewFindViewById != null) {
            ((ConstraintLayout.LayoutParams) viewFindViewById.getLayoutParams()).isInPlaceholder = true;
            this.mContent.setVisibility(0);
            setVisibility(0);
        }
    }

    public void setContentId(int id) {
        View viewFindViewById;
        if (this.mContentId == id) {
            return;
        }
        View view = this.mContent;
        if (view != null) {
            view.setVisibility(0);
            ((ConstraintLayout.LayoutParams) this.mContent.getLayoutParams()).isInPlaceholder = false;
            this.mContent = null;
        }
        this.mContentId = id;
        if (id == -1 || (viewFindViewById = ((View) getParent()).findViewById(id)) == null) {
            return;
        }
        viewFindViewById.setVisibility(8);
    }

    public void updatePostMeasure(ConstraintLayout container) {
        if (this.mContent == null) {
            return;
        }
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) getLayoutParams();
        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) this.mContent.getLayoutParams();
        layoutParams2.widget.setVisibility(0);
        if (layoutParams.widget.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.FIXED) {
            layoutParams.widget.setWidth(layoutParams2.widget.getWidth());
        }
        if (layoutParams.widget.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.FIXED) {
            layoutParams.widget.setHeight(layoutParams2.widget.getHeight());
        }
        layoutParams2.widget.setVisibility(8);
    }
}
