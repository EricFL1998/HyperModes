package miuix.appcompat.internal.view.menu.context;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import miuix.appcompat.R;
import miuix.appcompat.internal.view.menu.MenuBuilder;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.TaggingDrawableUtil;
import miuix.internal.util.ViewUtils;
import miuix.internal.widget.ListPopup;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public class ContextMenuPopupWindowImpl extends ListPopup implements ContextMenuPopupWindow {
    private static final float SCREEN_MARGIN_BOTTOM_PROPORTION = 0.1f;
    private static final float SCREEN_MARGIN_TOP_PROPORTION = 0.1f;
    private ContextMenuAdapter mAdapter;
    private View mLastAnchor;
    private ViewGroup mLastParent;
    private int mMarginScreen;
    private int mMaxWidth;
    private MenuBuilder mMenu;
    private LinearLayout mPopupContentView;
    private View mSeparateItemMenuView;
    private MenuItem mSeparateMenuItem;
    private View mSeparateMenuView;
    private float mX;
    private float mY;

    public ContextMenuPopupWindowImpl(Context context, MenuBuilder menuBuilder, PopupWindow.OnDismissListener onDismissListener) {
        this(context, menuBuilder, onDismissListener, null);
    }

    public ContextMenuPopupWindowImpl(Context context, MenuBuilder menuBuilder, PopupWindow.OnDismissListener onDismissListener, View view) {
        super(context, view);
        this.mMenu = menuBuilder;
        ContextMenuAdapter contextMenuAdapter = new ContextMenuAdapter(context, this.mMenu);
        this.mAdapter = contextMenuAdapter;
        this.mSeparateMenuItem = contextMenuAdapter.getLastCategorySystemOrderMenuItem();
        prepareMultipleChoiceMenu(context);
        setAdapter(this.mAdapter);
        setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindowImpl.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                MenuItem item = ContextMenuPopupWindowImpl.this.mAdapter.getItem(i);
                ContextMenuPopupWindowImpl.this.mMenu.performItemAction(item, 0);
                if (item.hasSubMenu()) {
                    final SubMenu subMenu = item.getSubMenu();
                    ContextMenuPopupWindowImpl.this.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindowImpl.1.1
                        @Override // android.widget.PopupWindow.OnDismissListener
                        public void onDismiss() {
                            ContextMenuPopupWindowImpl.this.setOnDismissListener(null);
                            ContextMenuPopupWindowImpl.this.update(subMenu);
                            ContextMenuPopupWindowImpl.this.fastShowAsContextMenu(ContextMenuPopupWindowImpl.this.mLastAnchor, ContextMenuPopupWindowImpl.this.mX, ContextMenuPopupWindowImpl.this.mY);
                        }
                    });
                }
                ContextMenuPopupWindowImpl.this.dismiss();
            }
        });
        if (onDismissListener != null) {
            setOnDismissListener(onDismissListener);
        }
        this.mMarginScreen = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_context_menu_window_margin_screen);
    }

    private void prepareMultipleChoiceMenu(Context context) {
        if (this.mSeparateMenuItem == null) {
            this.mSeparateMenuView.setVisibility(8);
            return;
        }
        ((TextView) this.mSeparateMenuView.findViewById(android.R.id.text1)).setText(this.mSeparateMenuItem.getTitle());
        this.mSeparateMenuView.setOnClickListener(new View.OnClickListener() { // from class: miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindowImpl.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ContextMenuPopupWindowImpl.this.mMenu.performItemAction(ContextMenuPopupWindowImpl.this.mSeparateMenuItem, 0);
                ContextMenuPopupWindowImpl.this.dismiss();
            }
        });
        AnimHelper.addItemPressEffect(this.mSeparateMenuView);
    }

    @Override // miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindow
    public void update(Menu menu) {
        this.mAdapter.update(menu);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fastShowAsContextMenu(View view, float f, float f2) {
        View rootView = view.getRootView();
        Rect rect = new Rect();
        ViewUtils.getBoundsInWindow(rootView, rect);
        this.mMaxWidth = checkMaxWidth(rect);
        setWidth(computePopupContentWidth(rect));
        setHeight(-2);
        this.mSeparateMenuView.setVisibility(8);
        showWithAnchor(view, f, f2, rect);
        this.mContentView.forceLayout();
    }

    @Override // miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindow
    public void show(View view, ViewGroup viewGroup, float f, float f2) {
        this.mLastAnchor = view;
        this.mLastParent = viewGroup;
        this.mX = f;
        this.mY = f2;
        View rootView = view.getRootView();
        Rect rect = new Rect();
        ViewUtils.getBoundsInWindow(rootView, rect);
        this.mMaxWidth = checkMaxWidth(rect);
        if (prepareShow(view, viewGroup, rect)) {
            showWithAnchor(view, f, f2, rect);
        }
    }

    @Override // miuix.appcompat.internal.view.menu.context.ContextMenuPopupWindow
    public void updatePopupWindow(View view, ViewGroup viewGroup, float f, float f2) {
        if (view == null && (view = this.mLastAnchor) == null) {
            view = null;
        }
        if (viewGroup == null && (viewGroup = this.mLastParent) == null) {
            viewGroup = null;
        }
        show(view, viewGroup, f, f2);
    }

    private void showWithAnchor(View view, float f, float f2, Rect rect) {
        Rect rect2 = new Rect();
        ViewUtils.getBoundsInWindow(view, rect2);
        int iWidth = rect2.left + ((int) f);
        int i = rect2.top + ((int) f2);
        boolean z = iWidth <= getWidth();
        boolean z2 = iWidth >= rect.width() - getWidth();
        int listViewHeight = getListViewHeight();
        float listViewHeight2 = i - (getListViewHeight() / 2);
        if (listViewHeight2 < rect.height() * 0.1f) {
            listViewHeight2 = rect.height() * 0.1f;
        }
        int multipleChoiceViewHeight = listViewHeight + getMultipleChoiceViewHeight();
        setHeight(multipleChoiceViewHeight);
        setContentHeight(multipleChoiceViewHeight);
        float f3 = multipleChoiceViewHeight;
        if (listViewHeight2 + f3 > rect.height() * 0.9f) {
            listViewHeight2 = (rect.height() * 0.9f) - f3;
        }
        if (listViewHeight2 < rect.height() * 0.1f) {
            listViewHeight2 = rect.height() * 0.1f;
            setHeight((int) (rect.height() * 0.79999995f));
        }
        if (z) {
            iWidth = this.mMarginScreen;
        } else if (z2) {
            iWidth = (rect.width() - this.mMarginScreen) - getWidth();
        }
        showAtLocation(view, 0, iWidth, (int) listViewHeight2);
        changeWindowBackground(this.mRootView.getRootView());
    }

    private int getListViewHeight() {
        ListView listView = (ListView) this.mContentView.findViewById(android.R.id.list);
        if (listView != null) {
            ListAdapter adapter = listView.getAdapter();
            int measuredHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, listView);
                view.measure(View.MeasureSpec.makeMeasureSpec(this.mMaxWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
                measuredHeight += view.getMeasuredHeight();
            }
            return measuredHeight;
        }
        this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        return this.mContentView.getMeasuredHeight();
    }

    private int getMultipleChoiceViewHeight() {
        if (this.mSeparateMenuView.getVisibility() != 0) {
            return 0;
        }
        ViewGroup.LayoutParams layoutParams = this.mSeparateMenuView.getLayoutParams();
        int i = (layoutParams == null || !(layoutParams instanceof ViewGroup.MarginLayoutParams)) ? 0 : ((ViewGroup.MarginLayoutParams) this.mSeparateMenuView.getLayoutParams()).topMargin;
        View view = this.mSeparateItemMenuView;
        if (view != null) {
            view.setPadding(view.getPaddingLeft(), 0, this.mSeparateItemMenuView.getPaddingRight(), 0);
        }
        this.mSeparateMenuView.measure(View.MeasureSpec.makeMeasureSpec(this.mMaxWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
        View view2 = this.mSeparateItemMenuView;
        if (view2 != null) {
            TaggingDrawableUtil.updateItemBackground(view2, 0, 1);
            this.mSeparateMenuView.measure(View.MeasureSpec.makeMeasureSpec(this.mMaxWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
        }
        return this.mSeparateMenuView.getMeasuredHeight() + i;
    }

    @Override // miuix.internal.widget.ListPopup
    protected void prepareContentView(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        this.mPopupContentView = linearLayout;
        linearLayout.setOrientation(1);
        this.mPopupContentView.setClipChildren(false);
        this.mPopupContentView.setClipToPadding(false);
        View viewInflate = LayoutInflater.from(context).inflate(R.layout.miuix_appcompat_popup_menu_item_context_separate, (ViewGroup) null, false);
        this.mSeparateMenuView = viewInflate;
        if (viewInflate instanceof ViewGroup) {
            this.mSeparateItemMenuView = viewInflate.findViewById(R.id.separate_item_menu);
        }
        Drawable drawableResolveDrawable = AttributeResolver.resolveDrawable(context, R.attr.immersionWindowBackground);
        if (drawableResolveDrawable != null) {
            drawableResolveDrawable.getPadding(this.mBackgroundPadding);
            this.mSeparateMenuView.setBackground(drawableResolveDrawable.getConstantState().newDrawable());
            prepareWindowElevation(this.mSeparateMenuView, this.mElevation + this.mElevationExtra);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(0, context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_context_menu_separate_item_margin_top), 0, 0);
        this.mPopupContentView.addView(this.mRootView, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        this.mPopupContentView.addView(this.mSeparateMenuView, layoutParams);
        setBackgroundDrawable(null);
        setPopupWindowContentView(this.mPopupContentView);
    }
}
