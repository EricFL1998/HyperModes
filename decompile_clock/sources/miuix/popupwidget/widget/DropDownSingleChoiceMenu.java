package miuix.popupwidget.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.Arrays;
import java.util.List;
import miuix.androidbasewidget.widget.CheckedTextView;
import miuix.popupwidget.R;

/* JADX INFO: loaded from: classes3.dex */
public class DropDownSingleChoiceMenu implements DropDownPopupWindow.Controller {
    private View mAnchorView;
    private Context mContext;
    private List<String> mItems;
    private OnMenuListener mListener;
    private DropDownPopupWindow mPopupWindow;
    private int mSelectedItem;

    public interface OnMenuListener {
        void onDismiss();

        void onItemSelected(DropDownSingleChoiceMenu dropDownSingleChoiceMenu, int i);

        void onShow();
    }

    @Override // miuix.popupwidget.widget.DropDownPopupWindow.Controller
    public void onAnimationUpdate(View view, float f) {
    }

    @Override // miuix.popupwidget.widget.DropDownPopupWindow.Controller
    public void onShow() {
    }

    public DropDownSingleChoiceMenu(Context context) {
        this.mContext = context;
    }

    public void setItems(List<String> list) {
        this.mItems = list;
    }

    public void setItems(String[] strArr) {
        this.mItems = Arrays.asList(strArr);
    }

    public void setSelectedItem(int i) {
        this.mSelectedItem = i;
    }

    public int getSelectedItem() {
        return this.mSelectedItem;
    }

    public void setAnchorView(View view) {
        this.mAnchorView = view;
        setAccessibilityDelegate(view);
    }

    public List<String> getItems() {
        return this.mItems;
    }

    public void setOnMenuListener(OnMenuListener onMenuListener) {
        this.mListener = onMenuListener;
    }

    public void show() {
        if (this.mItems == null || this.mAnchorView == null) {
            return;
        }
        if (this.mPopupWindow == null) {
            DropDownPopupWindow dropDownPopupWindow = new DropDownPopupWindow(this.mContext, null, 0);
            this.mPopupWindow = dropDownPopupWindow;
            dropDownPopupWindow.setContainerController(new DropDownPopupWindow.DefaultContainerController() { // from class: miuix.popupwidget.widget.DropDownSingleChoiceMenu.1
                @Override // miuix.popupwidget.widget.DropDownPopupWindow.DefaultContainerController, miuix.popupwidget.widget.DropDownPopupWindow.Controller
                public void onShow() {
                    if (DropDownSingleChoiceMenu.this.mListener != null) {
                        DropDownSingleChoiceMenu.this.mListener.onShow();
                    }
                }

                @Override // miuix.popupwidget.widget.DropDownPopupWindow.DefaultContainerController, miuix.popupwidget.widget.DropDownPopupWindow.Controller
                public void onDismiss() {
                    DropDownSingleChoiceMenu.this.internalDismiss();
                }
            });
            this.mPopupWindow.setDropDownController(this);
            ListView listView = new DropDownPopupWindow.ListController(this.mPopupWindow).getListView();
            listView.setAdapter((ListAdapter) new AnonymousClass2(this.mContext, R.layout.miuix_appcompat_select_dropdown_popup_singlechoice, this.mItems));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.popupwidget.widget.DropDownSingleChoiceMenu.3
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    DropDownSingleChoiceMenu.this.mSelectedItem = i;
                    if (DropDownSingleChoiceMenu.this.mListener != null) {
                        DropDownSingleChoiceMenu.this.mListener.onItemSelected(DropDownSingleChoiceMenu.this, i);
                    }
                    DropDownSingleChoiceMenu.this.dismiss();
                }
            });
            listView.setChoiceMode(1);
            listView.setItemChecked(this.mSelectedItem, true);
            this.mPopupWindow.setAnchor(this.mAnchorView);
        }
        this.mPopupWindow.show();
    }

    /* JADX INFO: renamed from: miuix.popupwidget.widget.DropDownSingleChoiceMenu$2, reason: invalid class name */
    class AnonymousClass2 extends ArrayAdapter<String> {
        AnonymousClass2(Context context, int i, List list) {
            super(context, i, list);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View viewInner = getViewInner(getContext(), getCount(), i, super.getView(i, view, viewGroup));
            viewInner.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.miuix_popup_window_list_item_fg));
            if (!viewInner.isClickable()) {
                viewInner.setOnHoverListener(new View.OnHoverListener() { // from class: miuix.popupwidget.widget.DropDownSingleChoiceMenu$2$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnHoverListener
                    public final boolean onHover(View view2, MotionEvent motionEvent) {
                        return DropDownSingleChoiceMenu.AnonymousClass2.lambda$getView$0(view2, motionEvent);
                    }
                });
            }
            if (viewInner instanceof CheckedTextView) {
                setAccessibilityDelegate((CheckedTextView) viewInner);
            }
            return viewInner;
        }

        static /* synthetic */ boolean lambda$getView$0(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 9) {
                view.setHovered(true);
            } else if (motionEvent.getAction() == 10) {
                view.setHovered(false);
            }
            return false;
        }

        private View getViewInner(Context context, int i, int i2, View view) {
            int dimensionPixelSize;
            int dimensionPixelSize2;
            view.getLayoutParams();
            int paddingStart = view.getPaddingStart();
            view.getPaddingTop();
            int paddingEnd = view.getPaddingEnd();
            view.getPaddingBottom();
            if (i == 1) {
                dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_padding_small);
                dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_padding_small);
            } else if (i2 == 0) {
                dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_padding_large);
                dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_padding_small);
            } else if (i2 == i - 1) {
                dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_padding_small);
                dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_padding_large);
            } else {
                dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_padding_small);
                dimensionPixelSize2 = context.getResources().getDimensionPixelSize(R.dimen.miuix_appcompat_drop_down_menu_padding_small);
            }
            view.setPaddingRelative(paddingStart, dimensionPixelSize, paddingEnd, dimensionPixelSize2);
            return view;
        }

        private void setAccessibilityDelegate(final CheckedTextView checkedTextView) {
            ViewCompat.setAccessibilityDelegate(checkedTextView, new AccessibilityDelegateCompat() { // from class: miuix.popupwidget.widget.DropDownSingleChoiceMenu.2.1
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    accessibilityNodeInfoCompat.setClassName(RadioButton.class.getName());
                    if (!checkedTextView.isChecked()) {
                        accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                    } else {
                        accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
                    }
                }
            });
        }
    }

    public void dismiss() {
        DropDownPopupWindow dropDownPopupWindow = this.mPopupWindow;
        if (dropDownPopupWindow != null) {
            dropDownPopupWindow.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void internalDismiss() {
        this.mPopupWindow = null;
    }

    @Override // miuix.popupwidget.widget.DropDownPopupWindow.Controller
    public void onDismiss() {
        OnMenuListener onMenuListener = this.mListener;
        if (onMenuListener != null) {
            onMenuListener.onDismiss();
        }
    }

    private void setAccessibilityDelegate(View view) {
        view.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: miuix.popupwidget.widget.DropDownSingleChoiceMenu.4
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityEvent(View view2, AccessibilityEvent accessibilityEvent) {
                super.onInitializeAccessibilityEvent(view2, accessibilityEvent);
                accessibilityEvent.setClassName(Spinner.class.getName());
            }

            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfo);
                accessibilityNodeInfo.setClassName(Spinner.class.getName());
            }
        });
    }
}
