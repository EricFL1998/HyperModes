package com.android.deskclock.timer;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class PopupMenu {
    private View mAnchor;
    private Context mContext;
    private boolean mIsPopupMenuVertical;
    private PopupMenuCallback mPopupMenuCallback;
    private View.OnClickListener mPopupMenuItemListener = new View.OnClickListener() { // from class: com.android.deskclock.timer.PopupMenu.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            PopupMenu.this.mPopupMenuCallback.onPopupMenuSelected(view.getId());
            PopupMenu.this.dismissPopupMenu();
        }
    };
    protected PopupWindow mPopupWindow;
    private LinearLayout mRootLayout;

    public interface PopupMenuCallback {
        void onCreatePopupMenu(HashMap<Integer, String> map);

        void onPopup(PopupWindow popupWindow);

        void onPopupMenuSelected(int i);
    }

    public PopupMenu(View view, PopupMenuCallback popupMenuCallback) {
        this.mContext = view.getContext();
        this.mAnchor = view;
        this.mPopupMenuCallback = popupMenuCallback;
        DefinedShadowLayout definedShadowLayout = new DefinedShadowLayout(this.mContext);
        this.mRootLayout = definedShadowLayout;
        definedShadowLayout.setPadding(24, 0, 24, 0);
    }

    public void dismiss() {
        PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private int makeDropDownMeasureSpec(int i) {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), i == -2 ? 0 : BasicMeasure.EXACTLY);
    }

    public void showPopupMenu(View view) {
        PopupWindow popupWindow = this.mPopupWindow;
        if (popupWindow != null && popupWindow.isShowing()) {
            this.mPopupWindow.dismiss();
        }
        PopupWindow popupWindow2 = new PopupWindow(this.mContext);
        this.mPopupWindow = popupWindow2;
        popupWindow2.setWidth(-2);
        this.mPopupWindow.setHeight(-2);
        this.mPopupWindow.setOutsideTouchable(true);
        this.mPopupWindow.setContentView(this.mRootLayout);
        this.mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        this.mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: com.android.deskclock.timer.PopupMenu.1
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                PopupMenu.this.dismissPopupMenu();
            }
        });
        HashMap<Integer, String> map = new HashMap<>();
        PopupMenuCallback popupMenuCallback = this.mPopupMenuCallback;
        if (popupMenuCallback != null) {
            popupMenuCallback.onCreatePopupMenu(map);
        }
        if (this.mIsPopupMenuVertical) {
            this.mRootLayout.setOrientation(1);
        } else {
            this.mRootLayout.setOrientation(0);
        }
        this.mRootLayout.removeAllViews();
        for (Integer num : map.keySet()) {
            map.get(num);
            TextView textViewNewTextView = newTextView();
            textViewNewTextView.setId(num.intValue());
            textViewNewTextView.setText(map.get(num));
            this.mRootLayout.addView(textViewNewTextView);
        }
        View contentView = this.mPopupWindow.getContentView();
        contentView.measure(makeDropDownMeasureSpec(this.mPopupWindow.getWidth()), makeDropDownMeasureSpec(this.mPopupWindow.getHeight()));
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        this.mPopupWindow.showAtLocation(this.mAnchor, 0, iArr[0] - 30, (iArr[1] - contentView.getMeasuredHeight()) - 32);
        PopupMenuCallback popupMenuCallback2 = this.mPopupMenuCallback;
        if (popupMenuCallback2 != null) {
            popupMenuCallback2.onPopup(this.mPopupWindow);
        }
    }

    public void dismissPopupMenu() {
        this.mPopupWindow.dismiss();
    }

    private TextView newTextView() {
        TextView textView = (TextView) ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(R.layout.timer_list_popup_text, (ViewGroup) null);
        MiuiFont.setFont(textView, MiuiFont.MI_PRO_MEDIUM);
        textView.setPadding(48, 48, 48, 48);
        textView.setOnClickListener(this.mPopupMenuItemListener);
        return textView;
    }

    public void setPopupMenuVertical(boolean z) {
        this.mIsPopupMenuVertical = z;
    }

    public void setPopupMenuCallback(PopupMenuCallback popupMenuCallback) {
        this.mPopupMenuCallback = popupMenuCallback;
    }
}
