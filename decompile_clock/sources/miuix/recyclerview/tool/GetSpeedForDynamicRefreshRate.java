package miuix.recyclerview.tool;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import com.xiaomi.onetrack.util.z;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;
import miuix.mimotion.MiMotionHelper;
import miuix.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes3.dex */
public class GetSpeedForDynamicRefreshRate {
    private static final int COUNT = 3;
    private static final String TAG = "DynamicRefreshRate recy";
    private static int sControlViewHashCode = 0;
    private static boolean sHasGetProperty = false;
    private static int[] sRefreshRateList;
    private static int[] sRefreshRateSpeedLimits;
    private int mCurrentRefreshRate;
    private final Display mDisplay;
    private final boolean mIsEnable;
    private MiMotionRecyclerViewHelper mMiMotionRecyclerViewHelper;
    private RecyclerView mRecyclerView;
    private final Window mWindow;
    private volatile boolean mIsTouch = false;
    private boolean mHasFocus = false;
    private int mCountIndex = 0;
    private long mStartTime = -1;
    private long mTotalDistance = 0;
    private boolean mNeedAbandon = false;
    private int mOldScrollState = 0;
    private int mRefreshRate = -1;

    public GetSpeedForDynamicRefreshRate(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        Activity activity = getActivity(recyclerView.getContext());
        Display display = activity != null ? activity.getDisplay() : null;
        this.mDisplay = display;
        Window window = activity != null ? activity.getWindow() : null;
        this.mWindow = window;
        boolean z = (!getParam() || display == null || window == null) ? false : true;
        this.mIsEnable = z;
        if (!z) {
            Log.e(TAG, "dynamic is not enable");
        }
        if (display == null || window == null) {
            return;
        }
        if (MiMotionHelper.isSupportMiMotion()) {
            MiMotionRecyclerViewHelper miMotionRecyclerViewHelper = new MiMotionRecyclerViewHelper();
            this.mMiMotionRecyclerViewHelper = miMotionRecyclerViewHelper;
            if (!miMotionRecyclerViewHelper.initMiMotion(recyclerView)) {
                this.mMiMotionRecyclerViewHelper = null;
            }
        }
        int[] iArr = sRefreshRateList;
        if (iArr == null || iArr.length <= 0) {
            return;
        }
        this.mCurrentRefreshRate = iArr[0];
    }

    public void calculateSpeed(int i, int i2, int i3, int i4) {
        int iCalculateRefreshRate;
        MiMotionRecyclerViewHelper miMotionRecyclerViewHelper = this.mMiMotionRecyclerViewHelper;
        if (miMotionRecyclerViewHelper != null) {
            miMotionRecyclerViewHelper.calculateSpeed(i3, i4, i, i2);
            return;
        }
        if (this.mIsEnable) {
            if ((i == 0 && i2 == 0) || this.mIsTouch || (iCalculateRefreshRate = calculateRefreshRate(Math.max(Math.abs(i), Math.abs(i2)))) == -1) {
                return;
            }
            setRefreshRate(iCalculateRefreshRate, false);
        }
    }

    public void calculateTouchSpeed(int i, int i2, int i3, int i4) {
        MiMotionRecyclerViewHelper miMotionRecyclerViewHelper = this.mMiMotionRecyclerViewHelper;
        if (miMotionRecyclerViewHelper != null) {
            miMotionRecyclerViewHelper.calculateTouchSpeed(i3, i4);
        }
    }

    public void touchEvent(MotionEvent motionEvent) {
        checkMiMotionRecyclerViewHelper();
        MiMotionRecyclerViewHelper miMotionRecyclerViewHelper = this.mMiMotionRecyclerViewHelper;
        if (miMotionRecyclerViewHelper != null) {
            miMotionRecyclerViewHelper.touchEvent(motionEvent);
            return;
        }
        if (this.mIsEnable) {
            if (motionEvent.getActionMasked() == 0) {
                this.mIsTouch = true;
                int i = sRefreshRateList[0];
                this.mCurrentRefreshRate = i;
                this.mCountIndex = 0;
                setRefreshRate(i, true);
                this.mHasFocus = true;
                this.mNeedAbandon = false;
                return;
            }
            if (motionEvent.getActionMasked() == 1) {
                this.mIsTouch = false;
            }
        }
    }

    private void checkMiMotionRecyclerViewHelper() {
        if (MiMotionHelper.isSupportMiMotion() && MiMotionHelper.getInstance().isEnabled()) {
            if (this.mMiMotionRecyclerViewHelper == null) {
                MiMotionRecyclerViewHelper miMotionRecyclerViewHelper = new MiMotionRecyclerViewHelper();
                this.mMiMotionRecyclerViewHelper = miMotionRecyclerViewHelper;
                if (miMotionRecyclerViewHelper.initMiMotion(this.mRecyclerView)) {
                    return;
                }
                this.mMiMotionRecyclerViewHelper = null;
                return;
            }
            return;
        }
        this.mMiMotionRecyclerViewHelper = null;
    }

    public void scrollState(RecyclerView recyclerView, int i) {
        MiMotionRecyclerViewHelper miMotionRecyclerViewHelper = this.mMiMotionRecyclerViewHelper;
        if (miMotionRecyclerViewHelper != null) {
            miMotionRecyclerViewHelper.scrollState(recyclerView, i);
            return;
        }
        if (this.mIsEnable) {
            if (this.mNeedAbandon || this.mIsTouch || this.mOldScrollState != 2) {
                this.mOldScrollState = i;
                return;
            }
            this.mOldScrollState = i;
            if ((recyclerView.canScrollVertically(-1) && recyclerView.canScrollVertically(1)) || (recyclerView.canScrollHorizontally(-1) && recyclerView.canScrollVertically(1))) {
                int[] iArr = sRefreshRateList;
                setRefreshRate(iArr[iArr.length - 1], false);
            }
        }
    }

    public void onFocusChange(boolean z) {
        MiMotionRecyclerViewHelper miMotionRecyclerViewHelper = this.mMiMotionRecyclerViewHelper;
        if (miMotionRecyclerViewHelper != null) {
            miMotionRecyclerViewHelper.onFocusChange(z);
        } else if (this.mIsEnable) {
            this.mHasFocus = z;
            this.mNeedAbandon = true;
            setRefreshRate(sRefreshRateList[0], false);
        }
    }

    private void setRefreshRate(int i, boolean z) {
        Display.Mode[] supportedModes = this.mDisplay.getSupportedModes();
        WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
        if (i == this.mRefreshRate || supportedModes == null) {
            return;
        }
        this.mRefreshRate = i;
        for (Display.Mode mode : supportedModes) {
            if (Math.abs(mode.getRefreshRate() - i) <= 1.0f) {
                if (z || hashCode() == sControlViewHashCode || mode.getRefreshRate() > this.mRefreshRate) {
                    sControlViewHashCode = hashCode();
                    Log.i(TAG, sControlViewHashCode + " set Refresh rate to: " + i + ", mode is: " + mode.getModeId());
                    attributes.preferredDisplayModeId = mode.getModeId();
                    this.mWindow.setAttributes(attributes);
                    return;
                }
                return;
            }
        }
    }

    private int calculateRefreshRate(int i) {
        int[] iArr = sRefreshRateList;
        int i2 = iArr[iArr.length - 1];
        if (!this.mHasFocus || this.mNeedAbandon) {
            return -1;
        }
        if (i == 0) {
            return i2;
        }
        if (this.mCountIndex == 0) {
            this.mTotalDistance = 0L;
            this.mStartTime = System.currentTimeMillis();
        }
        int i3 = this.mCountIndex + 1;
        this.mCountIndex = i3;
        this.mTotalDistance += (long) i;
        if (i3 < 3) {
            return -1;
        }
        int iAbs = Math.abs(Math.round(this.mTotalDistance / ((System.currentTimeMillis() - this.mStartTime) / 1000.0f)));
        this.mCountIndex = 0;
        int i4 = 0;
        while (true) {
            int[] iArr2 = sRefreshRateSpeedLimits;
            if (i4 >= iArr2.length) {
                break;
            }
            if (iAbs > iArr2[i4]) {
                i2 = sRefreshRateList[i4];
                break;
            }
            i4++;
        }
        int i5 = this.mCurrentRefreshRate;
        if (i2 >= i5) {
            int[] iArr3 = sRefreshRateList;
            if (i5 != iArr3[iArr3.length - 1] || i2 != iArr3[0]) {
                return -1;
            }
        }
        this.mCurrentRefreshRate = i2;
        return i2;
    }

    private static boolean getParam() {
        boolean z = false;
        if (sHasGetProperty) {
            return (sRefreshRateList == null || sRefreshRateSpeedLimits == null) ? false : true;
        }
        try {
            try {
                String str = (String) Class.forName("android.os.SystemProperties").getDeclaredMethod("get", String.class).invoke(null, "ro.vendor.display.dynamic_refresh_rate");
                if (str == null) {
                    Log.i(TAG, "dynamic params is " + ((sRefreshRateList == null || sRefreshRateSpeedLimits == null) ? false : true));
                    sHasGetProperty = true;
                    return false;
                }
                String[] strArrSplit = str.split(MethodCodeHelper.IDENTITY_INFO_SEPARATOR);
                if (strArrSplit.length != 2) {
                    Log.i(TAG, "dynamic params is " + ((sRefreshRateList == null || sRefreshRateSpeedLimits == null) ? false : true));
                    sHasGetProperty = true;
                    return false;
                }
                String[] strArrSplit2 = strArrSplit[0].split(z.b);
                String[] strArrSplit3 = strArrSplit[1].split(z.b);
                if (strArrSplit3.length != strArrSplit2.length - 1) {
                    Log.i(TAG, "dynamic params is " + ((sRefreshRateList == null || sRefreshRateSpeedLimits == null) ? false : true));
                    sHasGetProperty = true;
                    return false;
                }
                sRefreshRateList = new int[strArrSplit2.length];
                for (int i = 0; i < strArrSplit2.length; i++) {
                    sRefreshRateList[i] = Integer.parseInt(strArrSplit2[i]);
                }
                sRefreshRateSpeedLimits = new int[strArrSplit3.length];
                for (int i2 = 0; i2 < strArrSplit3.length; i2++) {
                    sRefreshRateSpeedLimits[i2] = Integer.parseInt(strArrSplit3[i2]);
                }
                StringBuilder sb = new StringBuilder("dynamic params is ");
                if (sRefreshRateList != null && sRefreshRateSpeedLimits != null) {
                    z = true;
                }
                Log.i(TAG, sb.append(z).toString());
                sHasGetProperty = true;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "dynamic params is " + ((sRefreshRateList == null || sRefreshRateSpeedLimits == null) ? false : true));
                sHasGetProperty = true;
                sRefreshRateList = null;
                sRefreshRateSpeedLimits = null;
                return false;
            }
        } catch (Throwable th) {
            StringBuilder sb2 = new StringBuilder("dynamic params is ");
            if (sRefreshRateList != null && sRefreshRateSpeedLimits != null) {
                z = true;
            }
            Log.i(TAG, sb2.append(z).toString());
            sHasGetProperty = true;
            throw th;
        }
    }

    private static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
