package miuix.popupwidget.widget;

import android.R;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.view.WindowInsetsCompat;
import miuix.core.util.WindowUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ListPopupWindow {
    private static final boolean DEBUG = false;
    private static final int EXPAND_LIST_TIMEOUT = 250;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    public static final int MATCH_PARENT = -1;
    public static final int POSITION_PROMPT_ABOVE = 0;
    public static final int POSITION_PROMPT_BELOW = 1;
    private static final String TAG = "ListPopupWindow";
    public static final int WRAP_CONTENT = -2;
    private ListAdapter mAdapter;
    private Context mContext;
    private boolean mDropDownAlwaysVisible;
    private View mDropDownAnchorView;
    private int mDropDownHeight;
    private int mDropDownHorizontalOffset;
    private DropDownListView mDropDownList;
    private Drawable mDropDownListHighlight;
    private int mDropDownVerticalOffset;
    private boolean mDropDownVerticalOffsetSet;
    private int mDropDownWidth;
    private boolean mForceIgnoreOutsideTouch;
    private Handler mHandler;
    private final ListSelectorHider mHideSelector;
    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemSelectedListener mItemSelectedListener;
    int mListItemExpandMaximum;
    private boolean mModal;
    private DataSetObserver mObserver;
    private ArrowPopupWindow mPopup;
    private int mPromptPosition;
    private View mPromptView;
    private final ResizePopupRunnable mResizePopupRunnable;
    private final PopupScrollListener mScrollListener;
    private Runnable mShowDropDownRunnable;
    private Rect mTempRect;
    private final PopupTouchInterceptor mTouchInterceptor;

    public ListPopupWindow(Context context) {
        this(context, null, R.attr.listPopupWindowStyle);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.listPopupWindowStyle);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet, int i) {
        this.mResizePopupRunnable = new ResizePopupRunnable();
        this.mTouchInterceptor = new PopupTouchInterceptor();
        this.mScrollListener = new PopupScrollListener();
        this.mHideSelector = new ListSelectorHider();
        this.mListItemExpandMaximum = Integer.MAX_VALUE;
        this.mDropDownHeight = -2;
        this.mDropDownWidth = -2;
        this.mDropDownAlwaysVisible = false;
        this.mForceIgnoreOutsideTouch = false;
        this.mPromptPosition = 0;
        this.mHandler = new Handler();
        this.mTempRect = new Rect();
        this.mContext = context;
        this.mPopup = new ArrowPopupWindow(context, attributeSet, i);
    }

    public ArrowPopupWindow getPopupWindow() {
        return this.mPopup;
    }

    public void setAdapter(ListAdapter listAdapter) {
        DataSetObserver dataSetObserver = this.mObserver;
        if (dataSetObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        } else {
            ListAdapter listAdapter2 = this.mAdapter;
            if (listAdapter2 != null) {
                listAdapter2.unregisterDataSetObserver(dataSetObserver);
            }
        }
        this.mAdapter = listAdapter;
        if (listAdapter != null) {
            listAdapter.registerDataSetObserver(this.mObserver);
        }
        DropDownListView dropDownListView = this.mDropDownList;
        if (dropDownListView != null) {
            dropDownListView.setAdapter(this.mAdapter);
        }
    }

    public int getPromptPosition() {
        return this.mPromptPosition;
    }

    public void setPromptPosition(int i) {
        this.mPromptPosition = i;
    }

    public boolean isModal() {
        return this.mModal;
    }

    public void setModal(boolean z) {
        this.mModal = true;
        this.mPopup.setFocusable(z);
    }

    public void setForceIgnoreOutsideTouch(boolean z) {
        this.mForceIgnoreOutsideTouch = z;
    }

    public boolean isDropDownAlwaysVisible() {
        return this.mDropDownAlwaysVisible;
    }

    public void setDropDownAlwaysVisible(boolean z) {
        this.mDropDownAlwaysVisible = z;
    }

    public int getSoftInputMode() {
        return this.mPopup.getSoftInputMode();
    }

    public void setSoftInputMode(int i) {
        this.mPopup.setSoftInputMode(i);
    }

    public void setListSelector(Drawable drawable) {
        this.mDropDownListHighlight = drawable;
    }

    public Drawable getBackground() {
        return this.mPopup.getBackground();
    }

    public void setBackgroundDrawable(Drawable drawable) {
        this.mPopup.setBackgroundDrawable(drawable);
    }

    public int getAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }

    public void setAnimationStyle(int i) {
        this.mPopup.setAnimationStyle(i);
    }

    public View getAnchorView() {
        return this.mDropDownAnchorView;
    }

    public void setAnchorView(View view) {
        this.mDropDownAnchorView = view;
    }

    public int getHorizontalOffset() {
        return this.mDropDownHorizontalOffset;
    }

    public void setHorizontalOffset(int i) {
        this.mDropDownHorizontalOffset = i;
    }

    public int getVerticalOffset() {
        if (this.mDropDownVerticalOffsetSet) {
            return this.mDropDownVerticalOffset;
        }
        return 0;
    }

    public void setVerticalOffset(int i) {
        this.mDropDownVerticalOffset = i;
        this.mDropDownVerticalOffsetSet = true;
    }

    public int getWidth() {
        return this.mDropDownWidth;
    }

    public void setWidth(int i) {
        this.mDropDownWidth = i;
    }

    public void setContentWidth(int i) {
        Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            this.mDropDownWidth = this.mTempRect.left + this.mTempRect.right + i;
        } else {
            setWidth(i);
        }
    }

    public int getHeight() {
        return this.mDropDownHeight;
    }

    public void setHeight(int i) {
        this.mDropDownHeight = i;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.mItemSelectedListener = onItemSelectedListener;
    }

    public void setPromptView(View view) {
        boolean zIsShowing = isShowing();
        if (zIsShowing) {
            removePromptView();
        }
        this.mPromptView = view;
        if (zIsShowing) {
            show();
        }
    }

    public void postShow() {
        this.mHandler.post(this.mShowDropDownRunnable);
    }

    public void show() {
        int iBuildDropDown = buildDropDown();
        int i = this.mDropDownWidth;
        if (i != -1) {
            if (i == -2) {
                this.mPopup.setContentWidth(getAnchorView().getWidth());
            } else {
                this.mPopup.setContentWidth(i);
            }
        }
        int i2 = this.mDropDownHeight;
        if (i2 != -1) {
            if (i2 == -2) {
                this.mPopup.setContentHeight(iBuildDropDown);
            } else {
                this.mPopup.setContentHeight(i2);
            }
        }
        this.mPopup.setFocusable(true);
        if (this.mPopup.isShowing()) {
            this.mPopup.setOutsideTouchable((this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) ? false : true);
            this.mPopup.update(getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownWidth, iBuildDropDown);
            return;
        }
        this.mPopup.setWindowLayoutMode(-1, -1);
        this.mPopup.setOutsideTouchable((this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) ? false : true);
        this.mPopup.setTouchInterceptor(this.mTouchInterceptor);
        this.mPopup.show(getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset);
        this.mDropDownList.setSelection(-1);
        if (!this.mModal || this.mDropDownList.isInTouchMode()) {
            clearListSelection();
        }
        if (this.mModal) {
            return;
        }
        this.mHandler.post(this.mHideSelector);
    }

    public void dismiss() {
        dismiss(true);
    }

    public void dismiss(boolean z) {
        this.mPopup.dismiss(z);
        removePromptView();
        this.mDropDownList = null;
        this.mHandler.removeCallbacks(this.mResizePopupRunnable);
    }

    public void setOnDismissListener(android.widget.PopupWindow.OnDismissListener onDismissListener) {
        this.mPopup.setOnDismissListener(onDismissListener);
    }

    private void removePromptView() {
        View view = this.mPromptView;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.mPromptView);
            }
        }
    }

    public int getInputMethodMode() {
        return this.mPopup.getInputMethodMode();
    }

    public void setInputMethodMode(int i) {
        this.mPopup.setInputMethodMode(i);
    }

    public void setSelection(int i) {
        DropDownListView dropDownListView = this.mDropDownList;
        if (!isShowing() || dropDownListView == null) {
            return;
        }
        dropDownListView.mListSelectionHidden = false;
        dropDownListView.setSelection(i);
        if (dropDownListView.getChoiceMode() != 0) {
            dropDownListView.setItemChecked(i, true);
        }
    }

    public void clearListSelection() {
        DropDownListView dropDownListView = this.mDropDownList;
        if (dropDownListView != null) {
            dropDownListView.mListSelectionHidden = true;
            dropDownListView.requestLayout();
        }
    }

    public boolean isShowing() {
        return this.mPopup.isShowing();
    }

    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }

    public boolean performItemClick(int i) {
        if (!isShowing()) {
            return false;
        }
        if (this.mItemClickListener == null) {
            return true;
        }
        DropDownListView dropDownListView = this.mDropDownList;
        this.mItemClickListener.onItemClick(dropDownListView, dropDownListView.getChildAt(i - dropDownListView.getFirstVisiblePosition()), i, dropDownListView.getAdapter().getItemId(i));
        return true;
    }

    public Object getSelectedItem() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItem();
        }
        return null;
    }

    public int getSelectedItemPosition() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItemPosition();
        }
        return -1;
    }

    public long getSelectedItemId() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItemId();
        }
        return Long.MIN_VALUE;
    }

    public View getSelectedView() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedView();
        }
        return null;
    }

    public ListView getListView() {
        return this.mDropDownList;
    }

    void setListItemExpandMax(int i) {
        this.mListItemExpandMaximum = i;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int iLookForSelectablePosition;
        int iLookForSelectablePosition2;
        if (isShowing() && i != 62 && (this.mDropDownList.getSelectedItemPosition() >= 0 || (i != 66 && i != 23))) {
            int selectedItemPosition = this.mDropDownList.getSelectedItemPosition();
            boolean z = !this.mPopup.isAboveAnchor();
            ListAdapter listAdapter = this.mAdapter;
            if (listAdapter != null) {
                boolean zAreAllItemsEnabled = listAdapter.areAllItemsEnabled();
                iLookForSelectablePosition = zAreAllItemsEnabled ? 0 : this.mDropDownList.lookForSelectablePosition(0, true);
                if (zAreAllItemsEnabled) {
                    iLookForSelectablePosition2 = listAdapter.getCount() - 1;
                } else {
                    iLookForSelectablePosition2 = this.mDropDownList.lookForSelectablePosition(listAdapter.getCount() - 1, false);
                }
            } else {
                iLookForSelectablePosition = Integer.MAX_VALUE;
                iLookForSelectablePosition2 = Integer.MIN_VALUE;
            }
            if ((z && i == 19 && selectedItemPosition <= iLookForSelectablePosition) || (!z && i == 20 && selectedItemPosition >= iLookForSelectablePosition2)) {
                clearListSelection();
                this.mPopup.setInputMethodMode(1);
                show();
                return true;
            }
            this.mDropDownList.mListSelectionHidden = false;
            if (this.mDropDownList.onKeyDown(i, keyEvent)) {
                this.mPopup.setInputMethodMode(2);
                this.mDropDownList.requestFocusFromTouch();
                show();
                if (i == 19 || i == 20 || i == 23 || i == 66) {
                    return true;
                }
            } else if (z && i == 20) {
                if (selectedItemPosition == iLookForSelectablePosition2) {
                    return true;
                }
            } else if (!z && i == 19 && selectedItemPosition == iLookForSelectablePosition) {
                return true;
            }
        }
        return false;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!isShowing() || this.mDropDownList.getSelectedItemPosition() < 0) {
            return false;
        }
        boolean zOnKeyUp = this.mDropDownList.onKeyUp(i, keyEvent);
        if (zOnKeyUp && (i == 23 || i == 66)) {
            dismiss(true);
        }
        return zOnKeyUp;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private int buildDropDown() {
        int measuredHeight;
        int iMakeMeasureSpec;
        View view;
        int i = 0;
        if (this.mDropDownList == null) {
            Context context = this.mContext;
            this.mShowDropDownRunnable = new Runnable() { // from class: miuix.popupwidget.widget.ListPopupWindow.1
                @Override // java.lang.Runnable
                public void run() {
                    View anchorView = ListPopupWindow.this.getAnchorView();
                    if (anchorView == null || anchorView.getWindowToken() == null) {
                        return;
                    }
                    ListPopupWindow.this.show();
                }
            };
            DropDownListView dropDownListView = new DropDownListView(context, !this.mModal);
            this.mDropDownList = dropDownListView;
            Drawable drawable = this.mDropDownListHighlight;
            if (drawable != null) {
                dropDownListView.setSelector(drawable);
            }
            this.mDropDownList.setAdapter(this.mAdapter);
            this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
            this.mDropDownList.setFocusable(true);
            this.mDropDownList.setFocusableInTouchMode(true);
            this.mDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: miuix.popupwidget.widget.ListPopupWindow.2
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view2, int i2, long j) {
                    DropDownListView dropDownListView2;
                    if (i2 == -1 || (dropDownListView2 = ListPopupWindow.this.mDropDownList) == null) {
                        return;
                    }
                    dropDownListView2.mListSelectionHidden = false;
                }
            });
            this.mDropDownList.setOnScrollListener(this.mScrollListener);
            AdapterView.OnItemSelectedListener onItemSelectedListener = this.mItemSelectedListener;
            if (onItemSelectedListener != null) {
                this.mDropDownList.setOnItemSelectedListener(onItemSelectedListener);
            }
            DropDownListView dropDownListView2 = this.mDropDownList;
            View view2 = this.mPromptView;
            if (view2 != null) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
                int i2 = this.mPromptPosition;
                if (i2 == 0) {
                    linearLayout.addView(view2);
                    linearLayout.addView(dropDownListView2, layoutParams);
                } else if (i2 == 1) {
                    linearLayout.addView(dropDownListView2, layoutParams);
                    linearLayout.addView(view2);
                } else {
                    Log.e(TAG, "Invalid hint position " + this.mPromptPosition);
                }
                view2.measure(View.MeasureSpec.makeMeasureSpec(this.mDropDownWidth, Integer.MIN_VALUE), 0);
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) view2.getLayoutParams();
                measuredHeight = view2.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin;
                view = linearLayout;
            } else {
                measuredHeight = 0;
                view = dropDownListView2;
            }
            this.mPopup.setContentView(view);
        } else {
            View view3 = this.mPromptView;
            if (view3 != null) {
                LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) view3.getLayoutParams();
                measuredHeight = view3.getMeasuredHeight() + layoutParams3.topMargin + layoutParams3.bottomMargin;
            } else {
                measuredHeight = 0;
            }
        }
        Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            i = this.mTempRect.top + this.mTempRect.bottom;
            if (!this.mDropDownVerticalOffsetSet) {
                this.mDropDownVerticalOffset = -this.mTempRect.top;
            }
        } else {
            this.mTempRect.setEmpty();
        }
        int maxAvailableHeight = getMaxAvailableHeight(getAnchorView(), this.mDropDownVerticalOffset);
        if (this.mDropDownAlwaysVisible || this.mDropDownHeight == -1) {
            return maxAvailableHeight + i;
        }
        int i3 = this.mDropDownWidth;
        if (i3 == -2) {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
        } else if (i3 == -1) {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), BasicMeasure.EXACTLY);
        } else {
            iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, BasicMeasure.EXACTLY);
        }
        int iMeasureHeightOfChildrenCompact = this.mDropDownList.measureHeightOfChildrenCompact(iMakeMeasureSpec, 0, -1, maxAvailableHeight - measuredHeight, -1);
        if (iMeasureHeightOfChildrenCompact > 0) {
            measuredHeight += i;
        }
        return iMeasureHeightOfChildrenCompact + measuredHeight;
    }

    public int getMaxAvailableHeight(View view, int i) {
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int i2 = WindowUtils.getWindowSize(this.mContext).y;
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        WindowInsets rootWindowInsets = view.getRootWindowInsets();
        int maxAvailableHeight = this.mPopup.getMaxAvailableHeight(((i2 - (rootWindowInsets != null ? WindowInsetsCompat.toWindowInsetsCompat(rootWindowInsets).getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()).bottom : 0)) - (iArr[1] + view.getHeight())) - i, (iArr[1] - rect.top) + i);
        if (this.mPopup.getBackground() == null) {
            return maxAvailableHeight;
        }
        this.mPopup.getBackground().getPadding(this.mTempRect);
        return maxAvailableHeight - (this.mTempRect.top + this.mTempRect.bottom);
    }

    private static class DropDownListView extends ListView {
        public static final int INVALID_POSITION = -1;
        static final int NO_POSITION = -1;
        private boolean mHijackFocus;
        private boolean mListSelectionHidden;

        public DropDownListView(Context context, boolean z) {
            super(context, null, R.attr.dropDownListViewStyle);
            this.mHijackFocus = z;
            setCacheColorHint(0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int lookForSelectablePosition(int i, boolean z) {
            int iMin;
            ListAdapter adapter = getAdapter();
            if (adapter != null && !isInTouchMode()) {
                int count = adapter.getCount();
                if (!getAdapter().areAllItemsEnabled()) {
                    if (z) {
                        iMin = Math.max(0, i);
                        while (iMin < count && !adapter.isEnabled(iMin)) {
                            iMin++;
                        }
                    } else {
                        iMin = Math.min(i, count - 1);
                        while (iMin >= 0 && !adapter.isEnabled(iMin)) {
                            iMin--;
                        }
                    }
                    if (iMin < 0 || iMin >= count) {
                        return -1;
                    }
                    return iMin;
                }
                if (i >= 0 && i < count) {
                    return i;
                }
            }
            return -1;
        }

        @Override // android.view.View
        public boolean isInTouchMode() {
            return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
        }

        @Override // android.view.View
        public boolean hasWindowFocus() {
            return this.mHijackFocus || super.hasWindowFocus();
        }

        @Override // android.view.View
        public boolean isFocused() {
            return this.mHijackFocus || super.isFocused();
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean hasFocus() {
            return this.mHijackFocus || super.hasFocus();
        }

        final int measureHeightOfChildrenCompact(int i, int i2, int i3, int i4, int i5) {
            int iMakeMeasureSpec;
            int listPaddingTop = getListPaddingTop();
            int listPaddingBottom = getListPaddingBottom();
            int dividerHeight = getDividerHeight();
            Drawable divider = getDivider();
            ListAdapter adapter = getAdapter();
            if (adapter == null) {
                return listPaddingTop + listPaddingBottom;
            }
            int measuredHeight = listPaddingTop + listPaddingBottom;
            if (dividerHeight <= 0 || divider == null) {
                dividerHeight = 0;
            }
            int count = adapter.getCount();
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            View view = null;
            while (i6 < count) {
                int itemViewType = adapter.getItemViewType(i6);
                if (itemViewType != i7) {
                    view = null;
                    i7 = itemViewType;
                }
                view = adapter.getView(i6, view, this);
                int i9 = view.getLayoutParams().height;
                if (i9 > 0) {
                    iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i9, BasicMeasure.EXACTLY);
                } else {
                    iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                }
                view.measure(i, iMakeMeasureSpec);
                if (i6 > 0) {
                    measuredHeight += dividerHeight;
                }
                measuredHeight += view.getMeasuredHeight();
                if (measuredHeight >= i4) {
                    return (i5 < 0 || i6 <= i5 || i8 <= 0 || measuredHeight == i4) ? i4 : i8;
                }
                if (i5 >= 0 && i6 >= i5) {
                    i8 = measuredHeight;
                }
                i6++;
            }
            return measuredHeight;
        }
    }

    private class PopupDataSetObserver extends DataSetObserver {
        private PopupDataSetObserver() {
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            if (ListPopupWindow.this.isShowing()) {
                ListPopupWindow.this.show();
            }
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            ListPopupWindow.this.dismiss(true);
        }
    }

    private class ListSelectorHider implements Runnable {
        private ListSelectorHider() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ListPopupWindow.this.clearListSelection();
        }
    }

    private class ResizePopupRunnable implements Runnable {
        private ResizePopupRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ListPopupWindow.this.mDropDownList == null || ListPopupWindow.this.mDropDownList.getCount() <= ListPopupWindow.this.mDropDownList.getChildCount() || ListPopupWindow.this.mDropDownList.getChildCount() > ListPopupWindow.this.mListItemExpandMaximum) {
                return;
            }
            ListPopupWindow.this.mPopup.setInputMethodMode(2);
            ListPopupWindow.this.show();
        }
    }

    private class PopupTouchInterceptor implements View.OnTouchListener {
        private PopupTouchInterceptor() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (action == 0 && ListPopupWindow.this.mPopup != null && ListPopupWindow.this.mPopup.isShowing() && x >= 0 && x < ListPopupWindow.this.mPopup.getContentWidth() && y >= 0 && y < ListPopupWindow.this.mPopup.getContentHeight()) {
                ListPopupWindow.this.mHandler.postDelayed(ListPopupWindow.this.mResizePopupRunnable, 250L);
                return false;
            }
            if (action != 1) {
                return false;
            }
            ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
            return false;
        }
    }

    private class PopupScrollListener implements AbsListView.OnScrollListener {
        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }

        private PopupScrollListener() {
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i != 1 || ListPopupWindow.this.isInputMethodNotNeeded() || ListPopupWindow.this.mPopup.getContentView() == null) {
                return;
            }
            ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
            ListPopupWindow.this.mResizePopupRunnable.run();
        }
    }
}
