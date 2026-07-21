package com.android.deskclock.worldclock;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import com.android.deskclock.util.Util;
import miuix.view.SearchActionMode;

/* JADX INFO: loaded from: classes.dex */
public class SearchCallback implements SearchActionMode.Callback {
    private static final int MAX_SEARCH_LENGTH = 50;
    private ActionMode mActionMode;
    private Context mContext;
    private SearchView.OnQueryTextListener mOnQueryTextListener;
    private OnSearchListener mOnSearchListener;
    private EditText mSearchInput;
    private View nAnchorView;
    private View nAnimView;
    private TextWatcher mSearchTextWatcher = new TextWatcher() { // from class: com.android.deskclock.worldclock.SearchCallback.1
        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            SearchCallback.this.mSearchText = charSequence.toString();
            if (SearchCallback.this.mOnQueryTextListener != null) {
                SearchCallback.this.mOnQueryTextListener.onQueryTextChange(SearchCallback.this.mSearchText);
            }
        }
    };
    private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() { // from class: com.android.deskclock.worldclock.SearchCallback.2
        @Override // android.widget.TextView.OnEditorActionListener
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            Util.hideSoftInput(SearchCallback.this.mContext, textView);
            return false;
        }
    };
    private String mSearchText = "";

    public interface OnSearchListener {
        void onCreateSearchMode(ActionMode actionMode, Menu menu);

        void onDestroySearchMode(ActionMode actionMode);

        void onPrepareSearchMode(ActionMode actionMode, Menu menu);
    }

    @Override // android.view.ActionMode.Callback
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    public SearchCallback(Context context, SearchView.OnQueryTextListener onQueryTextListener, OnSearchListener onSearchListener) {
        this.mContext = context;
        this.mOnQueryTextListener = onQueryTextListener;
        this.mOnSearchListener = onSearchListener;
    }

    public void setup(View view, View view2) {
        this.nAnchorView = view;
        this.nAnimView = view2;
    }

    @Override // android.view.ActionMode.Callback
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        OnSearchListener onSearchListener = this.mOnSearchListener;
        if (onSearchListener == null) {
            return true;
        }
        onSearchListener.onPrepareSearchMode(actionMode, menu);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.view.ActionMode.Callback
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        this.mActionMode = actionMode;
        SearchActionMode searchActionMode = (SearchActionMode) actionMode;
        searchActionMode.setAnchorView(this.nAnchorView);
        searchActionMode.setAnimateView(this.nAnimView);
        searchActionMode.setResultView(this.nAnimView);
        EditText searchInput = searchActionMode.getSearchInput();
        this.mSearchInput = searchInput;
        searchInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        this.mSearchInput.setHint("");
        this.mSearchInput.addTextChangedListener(this.mSearchTextWatcher);
        this.mSearchInput.setOnEditorActionListener(this.mEditorActionListener);
        OnSearchListener onSearchListener = this.mOnSearchListener;
        if (onSearchListener != null) {
            onSearchListener.onCreateSearchMode(actionMode, menu);
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.view.ActionMode.Callback
    public void onDestroyActionMode(ActionMode actionMode) {
        this.mActionMode = null;
        ((SearchActionMode) actionMode).getSearchInput().removeTextChangedListener(this.mSearchTextWatcher);
        this.mSearchTextWatcher.afterTextChanged(Editable.Factory.getInstance().newEditable(""));
        OnSearchListener onSearchListener = this.mOnSearchListener;
        if (onSearchListener != null) {
            onSearchListener.onDestroySearchMode(actionMode);
        }
    }
}
