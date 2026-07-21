package miuix.appcompat.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.NumberFormat;
import miuix.androidbasewidget.widget.ProgressBar;
import miuix.appcompat.R;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ProgressDialog extends AlertDialog {
    public static final int STYLE_HORIZONTAL = 1;
    public static final int STYLE_SPINNER = 0;
    private boolean mHasStarted;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private boolean mIndeterminate;
    private Drawable mIndeterminateDrawable;
    private int mMax;
    private CharSequence mMessage;
    private TextView mMessageView;
    private ProgressBar mProgress;
    private Drawable mProgressDrawable;
    private String mProgressNumberFormat;
    private NumberFormat mProgressPercentFormat;
    private TextView mProgressPercentView;
    private int mProgressStyle;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private Handler mViewUpdateHandler;

    public ProgressDialog(Context context) {
        super(context);
        this.mProgressStyle = 0;
        initFormats();
    }

    public ProgressDialog(Context context, int i) {
        super(context, i);
        this.mProgressStyle = 0;
        initFormats();
    }

    private void initFormats() {
        this.mProgressNumberFormat = "%1d/%2d";
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        this.mProgressPercentFormat = percentInstance;
        percentInstance.setMaximumFractionDigits(0);
    }

    public static ProgressDialog show(Context context, CharSequence charSequence, CharSequence charSequence2) {
        return show(context, charSequence, charSequence2, false);
    }

    public static ProgressDialog show(Context context, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        return show(context, charSequence, charSequence2, z, false, null);
    }

    public static ProgressDialog show(Context context, CharSequence charSequence, CharSequence charSequence2, boolean z, boolean z2) {
        return show(context, charSequence, charSequence2, z, z2, null);
    }

    public static ProgressDialog show(Context context, CharSequence charSequence, CharSequence charSequence2, boolean z, boolean z2, DialogInterface.OnCancelListener onCancelListener) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(charSequence);
        progressDialog.setMessage(charSequence2);
        progressDialog.setIndeterminate(z);
        progressDialog.setCancelable(z2);
        progressDialog.setOnCancelListener(onCancelListener);
        progressDialog.show();
        return progressDialog;
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    protected void onCreate(Bundle bundle) {
        View viewInflate;
        int i;
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(getContext());
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(null, R.styleable.AlertDialog, android.R.attr.alertDialogStyle, 0);
        TypedArray typedArrayObtainStyledAttributes2 = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.dialogProgressPercentColor});
        final int color = typedArrayObtainStyledAttributes2.getColor(0, getContext().getResources().getColor(R.color.miuix_appcompat_dialog_default_progress_percent_color));
        typedArrayObtainStyledAttributes2.recycle();
        boolean z = MiuixUIUtils.getFontLevel(getContext()) == 2;
        if (this.mProgressStyle == 1) {
            this.mViewUpdateHandler = new Handler() { // from class: miuix.appcompat.app.ProgressDialog.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    super.handleMessage(message);
                    ProgressDialog.this.mMessageView.setText(ProgressDialog.this.mMessage);
                    if (ProgressDialog.this.mProgressPercentFormat == null || ProgressDialog.this.mProgressPercentView == null) {
                        return;
                    }
                    double max = ((double) ProgressDialog.this.mProgressVal) / ((double) ProgressDialog.this.mProgress.getMax());
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    String str = ProgressDialog.this.mProgressPercentFormat.format(max);
                    spannableStringBuilder.append((CharSequence) str);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(color), 0, str.length(), 34);
                    ProgressDialog.this.mProgress.setProgress(ProgressDialog.this.mProgressVal);
                    ProgressDialog.this.mProgressPercentView.setText(spannableStringBuilder);
                }
            };
            if (z) {
                i = R.layout.miuix_appcompat_alert_dialog_progress_xl_font;
            } else {
                i = R.layout.miuix_appcompat_alert_dialog_progress;
            }
            viewInflate = layoutInflaterFrom.inflate(typedArrayObtainStyledAttributes.getResourceId(R.styleable.AlertDialog_horizontalProgressLayout, i), (ViewGroup) null);
            this.mProgressPercentView = (TextView) viewInflate.findViewById(R.id.progress_percent);
        } else {
            viewInflate = layoutInflaterFrom.inflate(typedArrayObtainStyledAttributes.getResourceId(R.styleable.AlertDialog_progressLayout, R.layout.miuix_appcompat_progress_dialog), (ViewGroup) null);
        }
        this.mProgress = (ProgressBar) viewInflate.findViewById(android.R.id.progress);
        this.mMessageView = (TextView) viewInflate.findViewById(R.id.message);
        setView(viewInflate);
        typedArrayObtainStyledAttributes.recycle();
        int i2 = this.mMax;
        if (i2 > 0) {
            setMax(i2);
        }
        int i3 = this.mProgressVal;
        if (i3 > 0) {
            setProgress(i3);
        }
        int i4 = this.mSecondaryProgressVal;
        if (i4 > 0) {
            setSecondaryProgress(i4);
        }
        int i5 = this.mIncrementBy;
        if (i5 > 0) {
            incrementProgressBy(i5);
        }
        int i6 = this.mIncrementSecondaryBy;
        if (i6 > 0) {
            incrementSecondaryProgressBy(i6);
        }
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            setProgressDrawable(drawable);
        }
        Drawable drawable2 = this.mIndeterminateDrawable;
        if (drawable2 != null) {
            setIndeterminateDrawable(drawable2);
        }
        CharSequence charSequence = this.mMessage;
        if (charSequence != null) {
            setMessage(charSequence);
        }
        setIndeterminate(this.mIndeterminate);
        onProgressChanged();
        super.onCreate(bundle);
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.activity.ComponentDialog, android.app.Dialog
    public void onStart() {
        super.onStart();
        this.mHasStarted = true;
    }

    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, androidx.activity.ComponentDialog, android.app.Dialog
    protected void onStop() {
        super.onStop();
        this.mHasStarted = false;
    }

    public void setProgress(int i) {
        this.mProgressVal = i;
        if (this.mHasStarted) {
            onProgressChanged();
        }
    }

    public void setSecondaryProgress(int i) {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            progressBar.setSecondaryProgress(i);
            onProgressChanged();
        } else {
            this.mSecondaryProgressVal = i;
        }
    }

    public int getProgress() {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            return progressBar.getProgress();
        }
        return this.mProgressVal;
    }

    public int getSecondaryProgress() {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            return progressBar.getSecondaryProgress();
        }
        return this.mSecondaryProgressVal;
    }

    public int getMax() {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            return progressBar.getMax();
        }
        return this.mMax;
    }

    public void setMax(int i) {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            progressBar.setMax(i);
            onProgressChanged();
        } else {
            this.mMax = i;
        }
    }

    public void incrementProgressBy(int i) {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            progressBar.incrementProgressBy(i);
            onProgressChanged();
        } else {
            this.mIncrementBy += i;
        }
    }

    public void incrementSecondaryProgressBy(int i) {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            progressBar.incrementSecondaryProgressBy(i);
            onProgressChanged();
        } else {
            this.mIncrementSecondaryBy += i;
        }
    }

    public void setProgressDrawable(Drawable drawable) {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            progressBar.setProgressDrawable(drawable);
        } else {
            this.mProgressDrawable = drawable;
        }
    }

    public void setIndeterminateDrawable(Drawable drawable) {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            progressBar.setIndeterminateDrawable(drawable);
        } else {
            this.mIndeterminateDrawable = drawable;
        }
    }

    public void setIndeterminate(boolean z) {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            progressBar.setIndeterminate(z);
        } else {
            this.mIndeterminate = z;
        }
    }

    public boolean isIndeterminate() {
        ProgressBar progressBar = this.mProgress;
        if (progressBar != null) {
            return progressBar.isIndeterminate();
        }
        return this.mIndeterminate;
    }

    @Override // miuix.appcompat.app.AlertDialog
    public void setMessage(CharSequence charSequence) {
        if (this.mProgress != null) {
            if (this.mProgressStyle == 1) {
                this.mMessage = charSequence;
            }
            this.mMessageView.setText(charSequence);
            return;
        }
        this.mMessage = charSequence;
    }

    public void setProgressStyle(int i) {
        this.mProgressStyle = i;
    }

    public void setProgressNumberFormat(String str) {
        this.mProgressNumberFormat = str;
        onProgressChanged();
    }

    public void setProgressPercentFormat(NumberFormat numberFormat) {
        this.mProgressPercentFormat = numberFormat;
        onProgressChanged();
    }

    private void onProgressChanged() {
        Handler handler;
        if (this.mProgressStyle != 1 || (handler = this.mViewUpdateHandler) == null || handler.hasMessages(0)) {
            return;
        }
        this.mViewUpdateHandler.sendEmptyMessage(0);
    }
}
