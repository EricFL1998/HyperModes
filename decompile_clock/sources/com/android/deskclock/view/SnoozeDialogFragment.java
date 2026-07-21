package com.android.deskclock.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.widget.FontLinearLayout;
import miuix.appcompat.app.AlertDialog;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes.dex */
public class SnoozeDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener, FontLinearLayout.OnFontLinearLayoutClickListener {
    private static final String REPEAT_COUNT = "repeat_count";
    private static final int REPEAT_COUNT_TYPE_FIVE = 3;
    private static final int REPEAT_COUNT_TYPE_ONE = 0;
    private static final int REPEAT_COUNT_TYPE_TEN = 4;
    private static final int REPEAT_COUNT_TYPE_THREE = 2;
    private static final int REPEAT_COUNT_TYPE_TWO = 1;
    public static final int SEEK_BAR_HALF_PROGRESS = 25;
    public static final int SEEK_BAR_ONE_PROGRESS = 50;
    private static final String SNOOZE_DURATION = "snooze_duration";
    private static final int SNOOZE_DURATION_TYPE_FIFTEEN = 3;
    private static final int SNOOZE_DURATION_TYPE_FIVE = 1;
    private static final int SNOOZE_DURATION_TYPE_ONE = 0;
    private static final int SNOOZE_DURATION_TYPE_TEN = 2;
    private static final int SNOOZE_DURATION_TYPE_THIRTY = 6;
    private static final int SNOOZE_DURATION_TYPE_TWENTY = 4;
    private static final int SNOOZE_DURATION_TYPE_TWENTY_FIVE = 5;
    private Activity mActivity;
    private AlertDialog mAlertDialog;
    private int mCurrentRepeatCountType = -1;
    private int mCurrentSnoozeDurationType = -1;
    private int mFontLevel;
    private miuix.androidbasewidget.widget.SeekBar mRepeatCountSeekBar;
    private TextView[] mRepeatCountTvs;
    private TextView mRepeatCountValueFive;
    private TextView mRepeatCountValueOne;
    private TextView mRepeatCountValueTen;
    private TextView mRepeatCountValueThree;
    private TextView mRepeatCountValueTwo;
    private LinearLayout mRootView;
    private TextView mSnoozeDurationFifteen;
    private TextView mSnoozeDurationFive;
    private TextView mSnoozeDurationOne;
    private SnoozeDurationOrRepeatCountChangedListener mSnoozeDurationOrRepeatCountChangedListener;
    private miuix.androidbasewidget.widget.SeekBar mSnoozeDurationSeekBar;
    private TextView mSnoozeDurationTen;
    private TextView mSnoozeDurationThirty;
    private TextView[] mSnoozeDurationTvs;
    private TextView mSnoozeDurationTwenty;
    private TextView mSnoozeDurationTwentyFive;
    private static final float[] SNOOZE_DURATION_CLICK_LEVEL = {0.0f, 0.167f, 0.333f, 0.5f, 0.667f, 0.833f, 1.0f};
    private static final float[] REPEAT_COUNT_CLICK_LEVEL = {0.0f, 0.25f, 0.5f, 0.75f, 1.0f};
    private static final int[] SNOOZE_DURATION_TEXT_IDS = {R.id.snooze_duration_one, R.id.snooze_duration_five, R.id.snooze_duration_ten, R.id.snooze_duration_fifteen, R.id.snooze_duration_twenty, R.id.snooze_duration_twenty_five, R.id.snooze_duration_thirty};
    private static final int[] REPEAT_COUNT_TEXT_IDS = {R.id.repeat_count_one, R.id.repeat_count_two, R.id.repeat_count_three, R.id.repeat_count_five, R.id.repeat_count_ten};
    private static final String[] REPEAT_COUNT_VALUES = {"1", "2", "3", "5", "10"};
    private static final String[] SNOOZE_DURATION_VALUES = {"1", "5", "10", "15", "20", "25", "30"};

    public interface SnoozeDurationOrRepeatCountChangedListener {
        void onChangedSnoozeValues(String str, String str2);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new IllegalStateException("no argument");
        }
        String string = arguments.getString("snooze_duration");
        String string2 = arguments.getString(REPEAT_COUNT);
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.snooze_setting_dialog_fragment, (ViewGroup) null);
        this.mRootView = linearLayout;
        TextView textView = (TextView) linearLayout.findViewById(R.id.snooze_duration_one);
        this.mSnoozeDurationOne = textView;
        textView.setText(String.format(getResources().getString(R.string.snooze_duration_value_one_new), 1));
        TextView textView2 = (TextView) this.mRootView.findViewById(R.id.snooze_duration_five);
        this.mSnoozeDurationFive = textView2;
        textView2.setText(String.format(getResources().getString(R.string.snooze_duration_value_five_new), 5));
        TextView textView3 = (TextView) this.mRootView.findViewById(R.id.snooze_duration_ten);
        this.mSnoozeDurationTen = textView3;
        textView3.setText(String.format(getResources().getString(R.string.snooze_duration_value_ten_new), 10));
        TextView textView4 = (TextView) this.mRootView.findViewById(R.id.snooze_duration_fifteen);
        this.mSnoozeDurationFifteen = textView4;
        textView4.setText(String.format(getResources().getString(R.string.snooze_duration_value_fifteen_new), 15));
        TextView textView5 = (TextView) this.mRootView.findViewById(R.id.snooze_duration_twenty);
        this.mSnoozeDurationTwenty = textView5;
        textView5.setText(String.format(getResources().getString(R.string.snooze_duration_value_twenty_new), 20));
        TextView textView6 = (TextView) this.mRootView.findViewById(R.id.snooze_duration_twenty_five);
        this.mSnoozeDurationTwentyFive = textView6;
        textView6.setText(String.format(getResources().getString(R.string.snooze_duration_value_twenty_five_new), 25));
        TextView textView7 = (TextView) this.mRootView.findViewById(R.id.snooze_duration_thirty);
        this.mSnoozeDurationThirty = textView7;
        textView7.setText(String.format(getResources().getString(R.string.snooze_duration_value_thirty_new), 30));
        TextView textView8 = (TextView) this.mRootView.findViewById(R.id.repeat_count_one);
        this.mRepeatCountValueOne = textView8;
        textView8.setText(String.format(getResources().getString(R.string.repeat_count_value_one_new), 1));
        TextView textView9 = (TextView) this.mRootView.findViewById(R.id.repeat_count_two);
        this.mRepeatCountValueTwo = textView9;
        textView9.setText(String.format(getResources().getString(R.string.repeat_count_value_two_new), 2));
        TextView textView10 = (TextView) this.mRootView.findViewById(R.id.repeat_count_three);
        this.mRepeatCountValueThree = textView10;
        textView10.setText(String.format(getResources().getString(R.string.repeat_count_value_three_new), 3));
        TextView textView11 = (TextView) this.mRootView.findViewById(R.id.repeat_count_five);
        this.mRepeatCountValueFive = textView11;
        textView11.setText(String.format(getResources().getString(R.string.repeat_count_value_five_new), 5));
        TextView textView12 = (TextView) this.mRootView.findViewById(R.id.repeat_count_ten);
        this.mRepeatCountValueTen = textView12;
        textView12.setText(String.format(getResources().getString(R.string.repeat_count_value_ten_new), 10));
        this.mActivity = getActivity();
        init();
        setData(string, string2);
        this.mFontLevel = MiuixUIUtils.getFontLevel(DeskClockApp.getAppDEContext());
    }

    private void init() {
        int length = REPEAT_COUNT_TEXT_IDS.length;
        this.mRepeatCountTvs = new TextView[length];
        for (int i = 0; i < length; i++) {
            this.mRepeatCountTvs[i] = (TextView) this.mRootView.findViewById(REPEAT_COUNT_TEXT_IDS[i]);
        }
        int length2 = SNOOZE_DURATION_TEXT_IDS.length;
        this.mSnoozeDurationTvs = new TextView[length2];
        for (int i2 = 0; i2 < length2; i2++) {
            this.mSnoozeDurationTvs[i2] = (TextView) this.mRootView.findViewById(SNOOZE_DURATION_TEXT_IDS[i2]);
        }
        initSnoozeDurationLayout();
        initRepeatCountLayout();
    }

    private void initSnoozeDurationLayout() {
        miuix.androidbasewidget.widget.SeekBar seekBar = (miuix.androidbasewidget.widget.SeekBar) this.mRootView.findViewById(R.id.snooze_duration_value_seek_bar);
        this.mSnoozeDurationSeekBar = seekBar;
        seekBar.setOnSeekBarChangeListener(this);
        ((FontLinearLayout) this.mRootView.findViewById(R.id.font_snooze_duration_value)).setOnFontLinearLayoutClickListener(this);
    }

    private void initRepeatCountLayout() {
        this.mRepeatCountSeekBar = (miuix.androidbasewidget.widget.SeekBar) this.mRootView.findViewById(R.id.repeat_count_seek_bar);
        if (PadAdapterUtil.IS_PAD) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mRepeatCountSeekBar.getLayoutParams();
            layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.dialog_seek_bar_margin_bottom);
            this.mRepeatCountSeekBar.setLayoutParams(layoutParams);
        }
        this.mRepeatCountSeekBar.setOnSeekBarChangeListener(this);
        ((FontLinearLayout) this.mRootView.findViewById(R.id.font_repeat_count)).setOnFontLinearLayoutClickListener(this);
    }

    public void setData(String str, String str2) {
        wipeCheckedState();
        this.mCurrentSnoozeDurationType = getIndex(SNOOZE_DURATION_VALUES, str);
        this.mCurrentRepeatCountType = getIndex(REPEAT_COUNT_VALUES, str2);
        setSnoozeDuration(this.mCurrentSnoozeDurationType);
        setRepeatCount(this.mCurrentRepeatCountType);
    }

    private void wipeCheckedState() {
        int length = this.mRepeatCountTvs.length;
        for (int i = 0; i < length; i++) {
            setChecked(this.mRepeatCountTvs[i], false);
        }
        int length2 = this.mSnoozeDurationTvs.length;
        for (int i2 = 0; i2 < length2; i2++) {
            setChecked(this.mSnoozeDurationTvs[i2], false);
        }
    }

    private int getIndex(String[] strArr, String str) {
        for (int i = 0; i < strArr.length; i++) {
            if (str.equals(strArr[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder view = new AlertDialog.Builder(getActivity()).setTitle(R.string.snooze_duration).setView(this.mRootView);
        view.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.view.SnoozeDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SnoozeDialogFragment.this.mSnoozeDurationOrRepeatCountChangedListener.onChangedSnoozeValues(SnoozeDialogFragment.SNOOZE_DURATION_VALUES[SnoozeDialogFragment.this.mCurrentSnoozeDurationType], SnoozeDialogFragment.REPEAT_COUNT_VALUES[SnoozeDialogFragment.this.mCurrentRepeatCountType]);
                SnoozeDialogFragment.this.dismiss();
            }
        });
        view.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.deskclock.view.SnoozeDialogFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SnoozeDialogFragment.this.dismiss();
            }
        });
        AlertDialog alertDialogCreate = view.create();
        this.mAlertDialog = alertDialogCreate;
        alertDialogCreate.setCanceledOnTouchOutside(true);
        return this.mAlertDialog;
    }

    public static SnoozeDialogFragment newInstance(String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString("snooze_duration", str);
        bundle.putString(REPEAT_COUNT, str2);
        SnoozeDialogFragment snoozeDialogFragment = new SnoozeDialogFragment();
        snoozeDialogFragment.setArguments(bundle);
        return snoozeDialogFragment;
    }

    private void changedProgress(SeekBar seekBar, int i) {
        if (seekBar.getId() == R.id.snooze_duration_value_seek_bar) {
            setSnoozeDuration(i);
        } else if (seekBar.getId() == R.id.repeat_count_seek_bar) {
            setRepeatCount(i);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void setSnoozeDuration(int i) {
        if (i == -1) {
            return;
        }
        updateSnoozeDurationState(i);
        int i2 = -3;
        switch (i) {
            case 0:
                this.mCurrentSnoozeDurationType = 0;
                i2 = 0;
                break;
            case 1:
                this.mCurrentSnoozeDurationType = 1;
                break;
            case 2:
                this.mCurrentSnoozeDurationType = 2;
                break;
            case 3:
                this.mCurrentSnoozeDurationType = 3;
                i2 = 0;
                break;
            case 4:
                this.mCurrentSnoozeDurationType = 4;
                i2 = 3;
                break;
            case 5:
                this.mCurrentSnoozeDurationType = 5;
                i2 = 3;
                break;
            case 6:
                this.mCurrentSnoozeDurationType = 6;
                i2 = 0;
                break;
            default:
                i2 = 0;
                break;
        }
        this.mSnoozeDurationSeekBar.setProgress((i * 50) + i2);
    }

    private void setRepeatCount(int i) {
        int i2 = -1;
        if (i == -1) {
            return;
        }
        updateRepeatCountCheckedState(i);
        if (i == 0) {
            this.mCurrentRepeatCountType = 0;
        } else {
            if (i == 1) {
                this.mCurrentRepeatCountType = 1;
            } else if (i == 2) {
                this.mCurrentRepeatCountType = 2;
            } else if (i == 3) {
                this.mCurrentRepeatCountType = 3;
                i2 = 1;
            } else if (i == 4) {
                this.mCurrentRepeatCountType = 4;
            }
            this.mRepeatCountSeekBar.setProgress((i * 50) + i2);
        }
        i2 = 0;
        this.mRepeatCountSeekBar.setProgress((i * 50) + i2);
    }

    private void updateSnoozeDurationState(int i) {
        TextView[] textViewArr = this.mSnoozeDurationTvs;
        int length = textViewArr.length;
        int i2 = this.mCurrentSnoozeDurationType;
        if (i2 >= 0 && i2 < length) {
            setChecked(textViewArr[i2], false);
        }
        if (i < 0 || i >= length) {
            return;
        }
        setChecked(this.mSnoozeDurationTvs[i], true);
    }

    private void updateRepeatCountCheckedState(int i) {
        TextView[] textViewArr = this.mRepeatCountTvs;
        int length = textViewArr.length;
        int i2 = this.mCurrentRepeatCountType;
        if (i2 >= 0 && i2 < length) {
            setChecked(textViewArr[i2], false);
        }
        if (i < 0 || i >= length) {
            return;
        }
        setChecked(this.mRepeatCountTvs[i], true);
    }

    private void setChecked(TextView textView, boolean z) {
        if (z) {
            textView.setTextColor(this.mActivity.getResources().getColor(R.color.snooze_dialog_num_text_color_selected));
            if (this.mFontLevel == 2) {
                textView.setTextSize(0, this.mActivity.getResources().getDimension(R.dimen.snooze_dialog_num_value_size_normal));
                return;
            } else {
                textView.setTextSize(0, this.mActivity.getResources().getDimension(R.dimen.snooze_dialog_num_value_size_selected));
                return;
            }
        }
        textView.setTextColor(this.mActivity.getResources().getColor(R.color.alert_later_popup_window_second_title_color));
        textView.setTextSize(0, this.mActivity.getResources().getDimension(R.dimen.snooze_dialog_num_value_size_normal));
    }

    @Override // com.android.deskclock.widget.FontLinearLayout.OnFontLinearLayoutClickListener
    public void onStopTrackingTouch(FontLinearLayout fontLinearLayout, float f) {
        switch (fontLinearLayout.getId()) {
            case R.id.font_repeat_count /* 2131362235 */:
                onStopTrackingTouch(this.mRepeatCountSeekBar, REPEAT_COUNT_CLICK_LEVEL, f);
                break;
            case R.id.font_snooze_duration_value /* 2131362236 */:
                onStopTrackingTouch(this.mSnoozeDurationSeekBar, SNOOZE_DURATION_CLICK_LEVEL, f);
                break;
        }
    }

    private void onStopTrackingTouch(miuix.androidbasewidget.widget.SeekBar seekBar, float[] fArr, float f) {
        for (int i = 0; i < fArr.length; i++) {
            if (Math.abs(fArr[i] - f) < 0.1d) {
                seekBar.setProgress(this.mRootView.getLayoutDirection() == 1 ? seekBar.getMax() - (i * 50) : i * 50);
                onStopTrackingTouch(seekBar);
            }
        }
    }

    public void setSnoozeDurationOrRepeatCountChangedListener(SnoozeDurationOrRepeatCountChangedListener snoozeDurationOrRepeatCountChangedListener) {
        this.mSnoozeDurationOrRepeatCountChangedListener = snoozeDurationOrRepeatCountChangedListener;
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress % 50 >= 25) {
            progress += 25;
        }
        changedProgress(seekBar, progress / 50);
    }

    @Override // androidx.fragment.app.DialogFragment
    public void show(FragmentManager fragmentManager, String str) {
        SnoozeDialogFragment snoozeDialogFragment = (SnoozeDialogFragment) fragmentManager.findFragmentByTag(str);
        FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
        if (snoozeDialogFragment != null) {
            snoozeDialogFragment.dismissAllowingStateLoss();
        }
        fragmentTransactionBeginTransaction.add(this, str);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
    }
}
