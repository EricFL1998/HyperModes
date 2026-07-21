package miuix.navigator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes3.dex */
class NavigationDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final Rect mBounds = new Rect();
    private final Drawable mDivider;
    private final int mDividerHeight;

    NavigationDividerItemDecoration(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(R.styleable.NavigationList);
        this.mDivider = typedArrayObtainStyledAttributes.getDrawable(R.styleable.NavigationList_navigationListDivider);
        this.mDividerHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.NavigationList_navigationListDividerHeight, 0);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        if (drawDivider(view)) {
            rect.top = this.mDividerHeight;
        }
    }

    private boolean drawDivider(View view) {
        return view.getId() == R.id.navigation_item_category && !Boolean.TRUE.equals(view.getTag(R.id.miuix_navigator_category_hide_divider));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        int width;
        int paddingLeft;
        canvas.save();
        if (recyclerView.getClipToPadding()) {
            paddingLeft = recyclerView.getPaddingLeft();
            width = recyclerView.getWidth() - recyclerView.getPaddingRight();
            canvas.clipRect(paddingLeft, recyclerView.getPaddingTop(), width, recyclerView.getHeight() - recyclerView.getPaddingBottom());
        } else {
            width = recyclerView.getWidth();
            paddingLeft = 0;
        }
        int childCount = recyclerView.getChildCount();
        for (int i = 1; i < childCount; i++) {
            View childAt = recyclerView.getChildAt(i);
            if (drawDivider(childAt)) {
                recyclerView.getDecoratedBoundsWithMargins(childAt, this.mBounds);
                int iRound = this.mBounds.top + Math.round(childAt.getTranslationY());
                this.mDivider.setBounds(paddingLeft, iRound, width, this.mDividerHeight + iRound);
                this.mDivider.setAlpha((int) (childAt.getAlpha() * 255.0f));
                this.mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }
}
