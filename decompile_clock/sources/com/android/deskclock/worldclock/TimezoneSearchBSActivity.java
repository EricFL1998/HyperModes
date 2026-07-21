package com.android.deskclock.worldclock;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import com.android.deskclock.R;
import com.android.deskclock.util.Util;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.Fragment;
import miuix.appcompat.widget.Spinner;
import miuix.bottomsheet.BottomSheetModal;

/* JADX INFO: loaded from: classes.dex */
public class TimezoneSearchBSActivity extends AppCompatActivity implements TimezoneSearchAdapter.AddDataToListener {
    private int mModeConfig = 0;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_timezone_search_bs);
        if (getAppCompatActionBar() != null) {
            getAppCompatActionBar().hide();
        }
        Spinner spinner = (Spinner) findViewById(R.id.mode_config_spinner);
        spinner.setSelection(this.mModeConfig);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSActivity.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                TimezoneSearchBSActivity timezoneSearchBSActivity = TimezoneSearchBSActivity.this;
                timezoneSearchBSActivity.mModeConfig = timezoneSearchBSActivity.getModeConfig(i);
            }
        });
        BottomSheetModal bottomSheetModal = new BottomSheetModal(this);
        View viewInflate = LayoutInflater.from(this).inflate(R.layout.timezone_search_content_fragment, (ViewGroup) null, false);
        viewInflate.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSActivity.2
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view) {
                TimezoneSearchBSActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.timezone_search_fragment_content, new TimezoneSearchContentFragment()).commit();
            }
        });
        bottomSheetModal.setOnDismissListener(new BottomSheetModal.OnDismissListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchBSActivity.3
            @Override // miuix.bottomsheet.BottomSheetModal.OnDismissListener
            public void onDismiss() {
                TimezoneSearchBSActivity.this.finish();
            }
        });
        bottomSheetModal.setContentView(viewInflate);
        bottomSheetModal.getBehavior().setModeConfig(this.mModeConfig);
        bottomSheetModal.getBehavior().setState(3);
        bottomSheetModal.show();
        Util.cutOut(this);
    }

    public static class TimezoneSearchContentFragment extends Fragment {
        @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
        public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            FrameLayout frameLayout = new FrameLayout(requireContext());
            frameLayout.setId(View.generateViewId());
            if (bundle == null) {
                getChildFragmentManager().beginTransaction().replace(frameLayout.getId(), new TimezoneSearchBSFragment()).commit();
            }
            if (getActionBar() != null) {
                getActionBar().hide();
            }
            return frameLayout;
        }
    }

    @Override // com.android.deskclock.worldclock.TimezoneSearchAdapter.AddDataToListener
    public void addData(String str) {
        setResult(-1, new Intent().putExtra("android.intent.extra.TEXT", str));
        finish();
    }

    public int getModeConfig(int i) {
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        if (i == 2) {
            return 2;
        }
        throw new IllegalArgumentException("Invalid position: " + i);
    }
}
