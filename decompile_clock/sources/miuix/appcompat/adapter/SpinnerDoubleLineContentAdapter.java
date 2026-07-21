package miuix.appcompat.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class SpinnerDoubleLineContentAdapter extends ArrayAdapter {
    private static final int TAG_VIEW = R.id.tag_spinner_dropdown_view_double_line;
    protected CharSequence[] mEntries;
    protected boolean mIconOnlyEnabled;
    protected Drawable[] mIcons;
    private LayoutInflater mInflater;
    protected CharSequence[] mSummaries;

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return true;
    }

    protected SpinnerDoubleLineContentAdapter(Context context, int i) {
        super(context, i);
        this.mIconOnlyEnabled = false;
        this.mInflater = LayoutInflater.from(context);
    }

    public SpinnerDoubleLineContentAdapter(Context context, CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2, int[] iArr) {
        this(context, 0);
        this.mEntries = charSequenceArr;
        this.mSummaries = charSequenceArr2;
        setEntryIcons(iArr);
    }

    public SpinnerDoubleLineContentAdapter(Context context, CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2, int[] iArr, boolean z) {
        this(context, 0);
        this.mEntries = charSequenceArr;
        this.mSummaries = charSequenceArr2;
        setEntryIcons(iArr);
        this.mIconOnlyEnabled = z;
    }

    public void setEntryIcons(int[] iArr) {
        if (iArr == null) {
            setEntryIcons((Drawable[]) null);
            return;
        }
        Drawable[] drawableArr = new Drawable[iArr.length];
        Resources resources = getContext().getResources();
        for (int i = 0; i < iArr.length; i++) {
            int i2 = iArr[i];
            if (i2 > 0) {
                drawableArr[i] = resources.getDrawable(i2);
            } else {
                drawableArr[i] = null;
            }
        }
        setEntryIcons(drawableArr);
    }

    public void setEntryIcons(Drawable[] drawableArr) {
        this.mIcons = drawableArr;
    }

    public Drawable[] getEntryIcons() {
        return this.mIcons;
    }

    public void setEntries(CharSequence[] charSequenceArr) {
        this.mEntries = charSequenceArr;
    }

    public CharSequence[] getEntries() {
        return this.mEntries;
    }

    public void setSummaries(CharSequence[] charSequenceArr) {
        this.mSummaries = charSequenceArr;
    }

    public CharSequence[] getSummaries() {
        return this.mSummaries;
    }

    public void setIconOnlyEnabled(boolean z) {
        this.mIconOnlyEnabled = z;
    }

    public boolean isIconOnlyEnabled() {
        return this.mIconOnlyEnabled;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public Object getItem(int i) {
        CharSequence[] charSequenceArr = this.mEntries;
        if (charSequenceArr == null || i < 0 || i >= charSequenceArr.length) {
            return null;
        }
        return charSequenceArr[i];
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        CharSequence[] charSequenceArr = this.mEntries;
        if (charSequenceArr == null) {
            return 0;
        }
        return charSequenceArr.length;
    }

    @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (view == null || view.getTag(TAG_VIEW) == null) {
            view = this.mInflater.inflate(R.layout.miuix_appcompat_spiner_dropdown_view_double_line, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(android.R.id.icon);
            viewHolder.title = (TextView) view.findViewById(android.R.id.title);
            viewHolder.summary = (TextView) view.findViewById(android.R.id.summary);
            view.setTag(TAG_VIEW, viewHolder);
        }
        CharSequence entry = getEntry(i);
        CharSequence summary = getSummary(i);
        Drawable icon = getIcon(i);
        Object tag = view.getTag(TAG_VIEW);
        if (tag != null) {
            ViewHolder viewHolder2 = (ViewHolder) tag;
            if (!TextUtils.isEmpty(entry) && !this.mIconOnlyEnabled) {
                viewHolder2.title.setText(entry);
                viewHolder2.title.setVisibility(0);
            } else {
                viewHolder2.title.setText("");
                viewHolder2.title.setVisibility(8);
            }
            if (!TextUtils.isEmpty(summary) && !this.mIconOnlyEnabled) {
                viewHolder2.summary.setText(summary);
                viewHolder2.summary.setVisibility(0);
            } else {
                viewHolder2.summary.setText("");
                viewHolder2.summary.setVisibility(8);
            }
            if (icon != null) {
                viewHolder2.icon.setImageDrawable(icon);
                viewHolder2.icon.setVisibility(0);
                if (!TextUtils.isEmpty(entry) && this.mIconOnlyEnabled) {
                    viewHolder2.icon.setContentDescription(entry);
                }
            } else {
                viewHolder2.icon.setVisibility(8);
            }
        }
        return view;
    }

    private CharSequence getEntry(int i) {
        CharSequence[] charSequenceArr = this.mEntries;
        if (charSequenceArr == null || i >= charSequenceArr.length) {
            return null;
        }
        return charSequenceArr[i];
    }

    private CharSequence getSummary(int i) {
        CharSequence[] charSequenceArr = this.mSummaries;
        if (charSequenceArr == null || i >= charSequenceArr.length) {
            return null;
        }
        return charSequenceArr[i];
    }

    private Drawable getIcon(int i) {
        Drawable[] drawableArr = this.mIcons;
        if (drawableArr == null || i >= drawableArr.length) {
            return null;
        }
        return drawableArr[i];
    }

    private static class ViewHolder {
        ImageView icon;
        TextView summary;
        TextView title;

        private ViewHolder() {
        }
    }
}
