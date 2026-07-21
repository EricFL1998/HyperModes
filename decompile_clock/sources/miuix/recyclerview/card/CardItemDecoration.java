package miuix.recyclerview.card;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import java.util.ArrayList;
import miuix.recyclerview.card.base.BaseDecoration;

/* JADX INFO: loaded from: classes3.dex */
public class CardItemDecoration extends BaseDecoration {
    private static final int SAFE_LIMIT = 2;
    public int firstVisiblePosition;
    public int lastVisiblePosition;
    private CardTouchHelperCallback mDragCallback;
    private Drawable mGroupDrawable;
    private int mPageBackgroundColor;
    private int mCardPaddingTop = 0;
    private int mCardPaddingBottom = 0;
    private int mCardPaddingStart = 0;
    private int mCardPaddingEnd = 0;
    private int mCardMarginTop = 0;
    private int mCardMarginBottom = 0;
    private int mNoneCardMarginTop = 0;
    private int mNoneCardMarginBottom = 0;
    private final ArrayList<CardArea> mCardAreas = new ArrayList<>();

    public int findFirstVisibleItemPosition() {
        return 0;
    }

    public int findLastVisibleItemPosition() {
        return 0;
    }

    public boolean isLineLayout() {
        return false;
    }

    public void enableHyperMaterial(boolean z, Context context) {
        this.mEnableHyperMaterial = z;
        this.mGroupDrawable = getGroupDrawable(context);
    }

    private static class CardArea {
        public boolean drawBottomRoundCorner;
        public boolean drawTopRoundCorner;
        public RectF rect;

        private CardArea() {
            this.rect = new RectF();
            this.drawTopRoundCorner = true;
            this.drawBottomRoundCorner = true;
        }
    }

    public CardItemDecoration(Context context) {
        initCardPaddingAndMargin(context);
    }

    public CardItemDecoration(Context context, CardTouchHelperCallback cardTouchHelperCallback) {
        this.mDragCallback = cardTouchHelperCallback;
        initCardPaddingAndMargin(context);
    }

    public void initCardPaddingAndMargin(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.recyclerViewCardStyle, typedValue, true);
        int i = typedValue.resourceId;
        if (i == 0) {
            i = R.style.RecyclerViewCardStyle_DayNight;
        }
        theme.applyStyle(i, false);
        Resources resources = context.getResources();
        this.mCardPaddingTop = LiteUtils.getThemeDimens(theme, resources, R.attr.cardGroupPaddingTop);
        this.mCardPaddingBottom = LiteUtils.getThemeDimens(theme, resources, R.attr.cardGroupPaddingBottom);
        this.mCardPaddingStart = LiteUtils.getThemeDimens(theme, resources, R.attr.cardGroupPaddingStart);
        this.mCardPaddingEnd = LiteUtils.getThemeDimens(theme, resources, R.attr.cardGroupPaddingEnd);
        this.mCardMarginStart = LiteUtils.getThemeDimens(theme, resources, R.attr.cardGroupMarginStart);
        this.mCardMarginEnd = LiteUtils.getThemeDimens(theme, resources, R.attr.cardGroupMarginEnd);
        this.mCardMarginTop = LiteUtils.getThemeDimens(theme, resources, R.attr.cardGroupMarginTop);
        this.mCardMarginBottom = LiteUtils.getThemeDimens(theme, resources, R.attr.cardGroupMarginBottom);
        this.mCardRadius = LiteUtils.getThemeDimens(theme, resources, R.attr.cardGroupRadius);
        this.mAllRadii = new float[]{this.mCardRadius, this.mCardRadius, this.mCardRadius, this.mCardRadius, this.mCardRadius, this.mCardRadius, this.mCardRadius, this.mCardRadius};
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaint.setDither(true);
        this.mGroupDrawable = getGroupDrawable(context);
    }

    private Drawable getGroupDrawable(Context context) {
        Drawable themeDrawable;
        if (this.mEnableHyperMaterial) {
            themeDrawable = LiteUtils.getThemeDrawable(context, R.attr.cardHyperMaterialGroupBackground);
        } else {
            themeDrawable = LiteUtils.getThemeDrawable(context, R.attr.cardGroupBackground);
        }
        this.mGroupDrawable = themeDrawable;
        return themeDrawable;
    }

    private void calculateDeltaY(CardArea cardArea, RecyclerView recyclerView, int i, int i2, boolean z, CardGroupAdapter cardGroupAdapter) {
        int i3 = 0;
        boolean z2 = cardGroupAdapter.getItemViewGroup(i) == cardGroupAdapter.getRemovedGroupId();
        CardTouchHelperCallback cardTouchHelperCallback = this.mDragCallback;
        if ((cardTouchHelperCallback == null || !cardTouchHelperCallback.isInDragState()) && z2) {
            float fFindNearViewY = findNearViewY(recyclerView, i, i2, z);
            if (fFindNearViewY != -1.0f) {
                if (z) {
                    int i4 = i + 1;
                    if (i4 < i2) {
                        i3 = marginRect(cardGroupAdapter.getItemViewGroupType(i)).bottom + offsetsRect(cardGroupAdapter, i4).top;
                    }
                    cardArea.rect.bottom = fFindNearViewY - i3;
                    return;
                }
                int i5 = i - 1;
                if (i5 >= 0) {
                    i3 = marginRect(cardGroupAdapter.getItemViewGroupType(i)).top + offsetsRect(cardGroupAdapter, i5).bottom;
                }
                cardArea.rect.top = fFindNearViewY + i3;
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        super.onDrawOver(canvas, recyclerView, state);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        if (isSupportLayoutManager(recyclerView.getLayoutManager())) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter instanceof CardGroupAdapter) {
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                Rect rectOffsetsRect = offsetsRect((CardGroupAdapter) adapter, childAdapterPosition);
                if (childAdapterPosition == 0) {
                    rectOffsetsRect.top = 0;
                    rectOffsetsRect.bottom = 0;
                }
                if (isLayoutRtl(recyclerView)) {
                    rect.left = this.mCardMarginStart + this.mCardPaddingStart;
                    rect.right = this.mCardMarginEnd + this.mCardPaddingEnd;
                } else {
                    rect.right = this.mCardMarginStart + this.mCardPaddingStart;
                    rect.left = this.mCardMarginEnd + this.mCardPaddingEnd;
                }
                rect.top = rectOffsetsRect.top;
                rect.bottom = rectOffsetsRect.bottom;
            }
        }
    }

    @Override // miuix.recyclerview.card.base.BaseDecoration
    public void calculateGroupRectAndDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state, RecyclerView.Adapter<?> adapter) {
        int i;
        CardArea cardArea;
        CardArea cardArea2;
        int width;
        int i2;
        if (adapter instanceof CardGroupAdapter) {
            this.mCardAreas.clear();
            CardGroupAdapter cardGroupAdapter = (CardGroupAdapter) adapter;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager == null || !isSupportLayoutManager(layoutManager)) {
                return;
            }
            calculateVisiblePositionLimit(layoutManager);
            AnonymousClass1 anonymousClass1 = null;
            int i3 = this.firstVisiblePosition;
            CardArea cardArea3 = null;
            int i4 = 0;
            while (true) {
                int i5 = 4;
                boolean z = true;
                if (i3 > this.lastVisiblePosition) {
                    break;
                }
                RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(i3);
                if (viewHolderFindViewHolderForAdapterPosition != null) {
                    View view = viewHolderFindViewHolderForAdapterPosition.itemView;
                    int itemViewGroupType = cardGroupAdapter.getItemViewGroupType(recyclerView.getChildAdapterPosition(view));
                    if (itemViewGroupType != 0) {
                        CardTouchHelperCallback cardTouchHelperCallback = this.mDragCallback;
                        float y = (cardTouchHelperCallback == null || !cardTouchHelperCallback.isInDragState()) ? view.getY() : view.getTop();
                        if (cardArea3 == null) {
                            if (i4 < this.mCardAreas.size()) {
                                cardArea2 = this.mCardAreas.get(i4);
                            } else {
                                cardArea2 = new CardArea();
                                this.mCardAreas.add(cardArea2);
                            }
                            CardArea cardArea4 = cardArea2;
                            cardArea4.rect.top = y - this.mCardPaddingTop;
                            cardArea4.rect.bottom = y + view.getHeight() + this.mCardPaddingBottom;
                            cardArea4.rect.left = isLayoutRtl(recyclerView) ? this.mCardMarginEnd : this.mCardMarginStart;
                            RectF rectF = cardArea4.rect;
                            if (isLayoutRtl(recyclerView)) {
                                width = recyclerView.getWidth();
                                i2 = this.mCardMarginStart;
                            } else {
                                width = recyclerView.getWidth();
                                i2 = this.mCardMarginEnd;
                            }
                            rectF.right = width - i2;
                            cardArea4.drawTopRoundCorner = itemViewGroupType == 2;
                            cardArea4.drawBottomRoundCorner = itemViewGroupType == 4;
                            if (itemViewGroupType == 2 || itemViewGroupType == 1) {
                                calculateDeltaY(cardArea4, recyclerView, i3, 0, false, cardGroupAdapter);
                            }
                            cardArea = cardArea4;
                            i = itemViewGroupType;
                        } else {
                            view = view;
                            z = true;
                            i5 = 4;
                            cardArea3.rect.bottom = y + view.getHeight() + this.mCardPaddingBottom;
                            i = itemViewGroupType;
                            cardArea3.drawBottomRoundCorner = i == 4;
                            cardArea = cardArea3;
                        }
                        if (i == z) {
                            cardArea.drawTopRoundCorner = z;
                            cardArea.drawBottomRoundCorner = z;
                            cardArea.rect.bottom = y + view.getHeight() + this.mCardPaddingBottom;
                            calculateDeltaY(cardArea, recyclerView, i3, layoutManager.getItemCount(), true, cardGroupAdapter);
                            i4++;
                            cardArea = null;
                        }
                        if (i == i5) {
                            calculateDeltaY(cardArea, recyclerView, i3, layoutManager.getItemCount(), true, cardGroupAdapter);
                            i4++;
                            cardArea3 = null;
                        } else {
                            cardArea3 = cardArea;
                        }
                    }
                }
                i3++;
                anonymousClass1 = null;
            }
            for (int i6 = 0; i6 < this.mCardAreas.size(); i6++) {
                CardArea cardArea5 = this.mCardAreas.get(i6);
                if (cardArea5.rect.bottom - cardArea5.rect.top >= 0.0f && this.mGroupDrawable != null) {
                    float f = cardArea5.drawTopRoundCorner ? this.mCardRadius : 0.0f;
                    float f2 = cardArea5.drawBottomRoundCorner ? this.mCardRadius : 0.0f;
                    float[] fArr = {f, f, f, f, f2, f2, f2, f2};
                    if (this.mGroupDrawable instanceof ColorDrawable) {
                        this.mPaint.setColor(((ColorDrawable) this.mGroupDrawable).getColor());
                        drawCardRect(canvas, cardArea5.rect, fArr, Path.Direction.CW);
                    } else {
                        this.mCardPath.reset();
                        this.mCardPath.addRoundRect(cardArea5.rect, fArr, Path.Direction.CW);
                        clipDrawableRoundRect(canvas, cardArea5.rect, this.mCardPath, this.mGroupDrawable);
                    }
                }
            }
        }
    }

    public boolean isSupportLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount() == 1;
        }
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount() == 1;
        }
        return ((layoutManager instanceof RecyclerView.LayoutManager) && isLineLayout()) || (layoutManager instanceof LinearLayoutManager);
    }

    protected void calculateVisiblePositionLimit(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            if (gridLayoutManager.getSpanCount() == 1) {
                calculateVisiblePositionLimit(gridLayoutManager.findFirstVisibleItemPosition(), gridLayoutManager.findLastVisibleItemPosition());
                return;
            }
            return;
        }
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            calculateVisiblePositionLimit(linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition());
            return;
        }
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            if (staggeredGridLayoutManager.getSpanCount() == 1) {
                int[] iArrFindFirstVisibleItemPositions = staggeredGridLayoutManager.findFirstVisibleItemPositions(null);
                int[] iArrFindLastVisibleItemPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                if (iArrFindFirstVisibleItemPositions.length <= 0 || iArrFindLastVisibleItemPositions.length <= 0) {
                    return;
                }
                calculateVisiblePositionLimit(iArrFindFirstVisibleItemPositions[0], iArrFindLastVisibleItemPositions[0]);
                return;
            }
            return;
        }
        if ((layoutManager instanceof RecyclerView.LayoutManager) && isLineLayout()) {
            calculateVisiblePositionLimit(findFirstVisibleItemPosition(), findLastVisibleItemPosition());
        }
    }

    private void calculateVisiblePositionLimit(int i, int i2) {
        this.firstVisiblePosition = Math.max(i - 2, 0);
        this.lastVisiblePosition = i2 + 2;
    }

    private Rect marginRect(int i) {
        Rect rect = new Rect();
        if (i == 2) {
            rect.top = this.mCardMarginTop;
        } else if (i == 4) {
            rect.bottom = this.mCardMarginBottom;
        } else if (i == 1) {
            rect.top = this.mCardMarginTop;
            rect.bottom = this.mCardMarginBottom;
        } else if (i == 0) {
            rect.top = this.mNoneCardMarginTop;
            rect.bottom = this.mNoneCardMarginBottom;
        }
        return rect;
    }

    public Rect offsetsRect(CardGroupAdapter<?> cardGroupAdapter, int i) {
        Rect rect = new Rect();
        if (i >= 0) {
            int itemViewGroupType = cardGroupAdapter.getItemViewGroupType(i);
            if (itemViewGroupType == 2) {
                rect.top = this.mCardMarginTop + this.mCardPaddingTop;
            } else if (itemViewGroupType == 4) {
                rect.bottom = this.mCardMarginBottom + this.mCardPaddingBottom;
            } else if (itemViewGroupType == 1) {
                rect.top = this.mCardMarginTop + this.mCardPaddingTop;
                rect.bottom = this.mCardMarginBottom + this.mCardPaddingBottom;
            } else if (itemViewGroupType == 0) {
                rect.top = this.mNoneCardMarginTop;
                rect.bottom = this.mNoneCardMarginBottom;
            }
        }
        return rect;
    }

    public float getCardRadius() {
        return this.mCardRadius;
    }

    public void setCardRadius(int i) {
        this.mCardRadius = i;
        float f = i;
        this.mAllRadii = new float[]{f, f, f, f, f, f, f, f};
    }

    public int getCardPaddingTop() {
        return this.mCardPaddingTop;
    }

    public void setCardPaddingTop(int i) {
        this.mCardPaddingTop = i;
    }

    public int getCardPaddingBottom() {
        return this.mCardPaddingBottom;
    }

    public void setCardPaddingBottom(int i) {
        this.mCardPaddingBottom = i;
    }

    public int getCardPaddingStart() {
        return this.mCardPaddingStart;
    }

    public void setCardPaddingStart(int i) {
        this.mCardPaddingStart = i;
    }

    public int getCardPaddingEnd() {
        return this.mCardPaddingEnd;
    }

    public void setCardPaddingEnd(int i) {
        this.mCardPaddingEnd = i;
    }

    public int getCardMarginStart() {
        return this.mCardMarginStart;
    }

    public void setCardMarginStart(int i) {
        this.mCardMarginStart = i;
    }

    public int getCardMarginEnd() {
        return this.mCardMarginEnd;
    }

    public void setCardMarginEnd(int i) {
        this.mCardMarginEnd = i;
    }

    public int getCardMarginTop() {
        return this.mCardMarginTop;
    }

    public void setCardMarginTop(int i) {
        this.mCardMarginTop = i;
    }

    public int getCardMarginBottom() {
        return this.mCardMarginBottom;
    }

    public void setCardMarginBottom(int i) {
        this.mCardMarginBottom = i;
    }

    public int getNoneCardMarginTop() {
        return this.mNoneCardMarginTop;
    }

    public void setNoneCardMarginTop(int i) {
        this.mNoneCardMarginTop = i;
    }

    public int getNoneCardMarginBottom() {
        return this.mNoneCardMarginBottom;
    }

    public void setNoneCardMarginBottom(int i) {
        this.mNoneCardMarginBottom = i;
    }

    public void setPageBackgroundColor(int i) {
        this.mPageBackgroundColor = i;
    }
}
