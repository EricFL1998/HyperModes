package androidx.preference;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes.dex */
public class PreferenceViewHolder extends RecyclerView.ViewHolder {
    private final Drawable mBackground;
    private final SparseArray<View> mCachedViews;
    private boolean mDividerAllowedAbove;
    private boolean mDividerAllowedBelow;
    private ColorStateList mTitleTextColors;

    PreferenceViewHolder(View view) {
        super(view);
        SparseArray<View> sparseArray = new SparseArray<>(4);
        this.mCachedViews = sparseArray;
        TextView textView = (TextView) view.findViewById(android.R.id.title);
        sparseArray.put(android.R.id.title, textView);
        sparseArray.put(android.R.id.summary, view.findViewById(android.R.id.summary));
        sparseArray.put(android.R.id.icon, view.findViewById(android.R.id.icon));
        sparseArray.put(R.id.icon_frame, view.findViewById(R.id.icon_frame));
        sparseArray.put(16908350, view.findViewById(16908350));
        this.mBackground = view.getBackground();
        if (textView != null) {
            this.mTitleTextColors = textView.getTextColors();
        }
    }

    public static PreferenceViewHolder createInstanceForTests(View view) {
        return new PreferenceViewHolder(view);
    }

    public View findViewById(int i) {
        View view = this.mCachedViews.get(i);
        if (view != null) {
            return view;
        }
        View viewFindViewById = this.itemView.findViewById(i);
        if (viewFindViewById != null) {
            this.mCachedViews.put(i, viewFindViewById);
        }
        return viewFindViewById;
    }

    public boolean isDividerAllowedAbove() {
        return this.mDividerAllowedAbove;
    }

    public void setDividerAllowedAbove(boolean z) {
        this.mDividerAllowedAbove = z;
    }

    public boolean isDividerAllowedBelow() {
        return this.mDividerAllowedBelow;
    }

    public void setDividerAllowedBelow(boolean z) {
        this.mDividerAllowedBelow = z;
    }

    void resetState() {
        if (this.itemView.getBackground() != this.mBackground) {
            ViewCompat.setBackground(this.itemView, this.mBackground);
        }
        TextView textView = (TextView) findViewById(android.R.id.title);
        if (textView == null || this.mTitleTextColors == null || textView.getTextColors().equals(this.mTitleTextColors)) {
            return;
        }
        textView.setTextColor(this.mTitleTextColors);
    }
}
