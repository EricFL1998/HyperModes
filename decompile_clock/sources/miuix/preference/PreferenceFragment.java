package miuix.preference;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.activity.result.ActivityResultCaller;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.GroupButtonsConfig;
import miuix.appcompat.app.IFragment;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.appcompat.internal.app.widget.ActionBarOverlayLayout;
import miuix.container.ExtraPaddingObserver;
import miuix.container.ExtraPaddingPolicy;
import miuix.core.util.EnvStateManager;
import miuix.core.util.IntentUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.RomUtils;
import miuix.core.util.WindowBaseInfo;
import miuix.flexible.template.TemplateFactory;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;
import miuix.os.DeviceHelper;
import miuix.preference.flexible.DropdownPreferenceTemplate;
import miuix.preference.flexible.MiuixPreferenceTemplate;
import miuix.preference.flexible.RadioButtonPreferenceTemplate;
import miuix.preference.flexible.TextPreferenceTemplate;
import miuix.preference.utils.PreferenceLayoutUtils;
import miuix.recyclerview.card.CardDefaultItemAnimator;
import miuix.recyclerview.card.base.BaseDecoration;
import miuix.smooth.SmoothCornerHelper;
import miuix.springback.view.SpringBackLayout;
import miuix.theme.token.ContainerToken;

/* JADX INFO: loaded from: classes3.dex */
public abstract class PreferenceFragment extends PreferenceFragmentCompat implements IFragment {
    public static final int CARD_STYLE = 1;
    private static final String DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG";
    public static final int DISABLE_ALL_CARD_STYLE = -1;
    public static final int FORCE_CARD_STYLE = 2;
    public static final int TRADITIONAL_STYLE = 0;
    public static final int VERTICAL_PADDING_NOT_CUSTOMIZED = -1;
    private Rect mCacheListContainerMargin;
    private int mCardStyle;
    private boolean mConfigChangeUpdateViewEnable;
    protected Rect mContentInset;
    private int mDeviceType;
    private ExtraPaddingPolicy mExtraPaddingPolicy;
    private FrameDecoration mFrameDecoration;
    private PreferenceGroupAdapter mGroupAdapter;
    private boolean mIsEnableCardStyle;
    private View mListContainer;
    private int mListViewPaddingBottom;
    private Insets mTempNavigationBarInsets;
    private boolean mUserExtraPaddingPolicy;
    private boolean mIsOverlayMode = false;
    private boolean mAdapterInvalid = true;
    private boolean mItemSelectable = false;
    private int mCurSelectedItem = -1;
    private boolean mExtraPaddingEnable = true;
    private boolean mExtraPaddingInitEnable = false;
    private List<ExtraPaddingObserver> mExtraPaddingObserver = null;
    private int mExtraHorizontalPadding = 0;
    boolean mEnableNavigationBarInsets = true;
    boolean mEnableWindowInsets = true;
    private boolean mEnableHyperMaterial = false;

    @Override // miuix.appcompat.app.IFragment
    public boolean acceptExtraPaddingFromParent() {
        return false;
    }

    @Override // miuix.appcompat.app.IFragment
    public void checkThemeLegality() {
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void dismissImmersionMenu(boolean z) {
    }

    protected int getListViewPaddingBottom() {
        return -1;
    }

    protected int getListViewPaddingTop() {
        return -1;
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean hasActionBar() {
        return false;
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void hideEndOverflowMenu() {
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void hideOverflowMenu() {
    }

    public boolean isConfigChangeUpdateViewEnable() {
        return true;
    }

    protected boolean isEmbeddedFragment() {
        return false;
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean isInEditActionMode() {
        return false;
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean isIsInSearchActionMode() {
        return false;
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean isRegisterResponsive() {
        return false;
    }

    @Override // miuix.appcompat.app.IFragment
    public void onActionModeFinished(ActionMode actionMode) {
    }

    @Override // miuix.appcompat.app.IFragment
    public void onActionModeStarted(ActionMode actionMode) {
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override // miuix.appcompat.app.IFragment
    public boolean onCreatePanelMenu(int i, Menu menu) {
        return false;
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void onDispatchNestedScrollOffset(int[] iArr) {
    }

    @Override // miuix.container.ExtraPaddingObserver
    public void onExtraPaddingChanged(int i) {
    }

    @Override // miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return null;
    }

    @Override // miuix.appcompat.app.IFragment
    public void onOptionsMenuViewAdded(Menu menu, Menu menu2) {
    }

    @Override // miuix.appcompat.app.IFragment
    public void onPanelClosed(int i, Menu menu) {
    }

    @Override // miuix.appcompat.app.IFragment
    public void onPreparePanel(int i, View view, Menu menu) {
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void onProcessBindViewWithContentInset(Rect rect) {
    }

    @Override // miuix.appcompat.app.IFragment
    public void onViewInflated(View view, Bundle bundle) {
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void setCorrectNestedScrollMotionEventEnabled(boolean z) {
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void setImmersionMenuEnabled(boolean z) {
    }

    @Override // miuix.appcompat.app.IFragment
    public void setNestedScrollingParentEnabled(boolean z) {
    }

    @Override // miuix.appcompat.app.IFragment
    public void setThemeRes(int i) {
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void showEndOverflowMenu() {
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void showImmersionMenu() {
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void showImmersionMenu(View view, ViewGroup viewGroup) {
    }

    @Override // miuix.appcompat.app.IImmersionMenu
    public void showOverflowMenu() {
    }

    @Override // miuix.appcompat.app.IFragment
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return null;
    }

    static {
        TemplateFactory.registerTemplate("dropdownPreference", DropdownPreferenceTemplate.class);
        TemplateFactory.registerTemplate("textPreference", TextPreferenceTemplate.class);
        TemplateFactory.registerTemplate("radioButtonPreference", RadioButtonPreferenceTemplate.class);
        TemplateFactory.registerTemplate("preference", MiuixPreferenceTemplate.class);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mConfigChangeUpdateViewEnable = isConfigChangeUpdateViewEnable();
        Context themedContext = getThemedContext();
        if (themedContext != null) {
            TypedArray typedArrayObtainStyledAttributes = themedContext.obtainStyledAttributes(miuix.appcompat.R.styleable.Window);
            setExtraHorizontalPaddingEnable(typedArrayObtainStyledAttributes.getBoolean(miuix.appcompat.R.styleable.Window_windowExtraPaddingHorizontalEnable, this.mExtraPaddingEnable));
            setExtraHorizontalPaddingInitEnable(typedArrayObtainStyledAttributes.getBoolean(miuix.appcompat.R.styleable.Window_windowExtraPaddingHorizontalInitEnable, this.mExtraPaddingInitEnable));
            typedArrayObtainStyledAttributes.recycle();
            boolean z = true;
            int iResolveInt = AttributeResolver.resolveInt(themedContext, R.attr.preferenceCardStyleEnable, 1);
            this.mCardStyle = iResolveInt;
            if (iResolveInt != 2 && (RomUtils.getHyperOsVersion() <= 1 || this.mCardStyle != 1)) {
                z = false;
            }
            this.mIsEnableCardStyle = z;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context themedContext;
        updateActionBarOverlay();
        setActionBarOverLayoutBg();
        this.mDeviceType = DeviceHelper.detectType(getActivity());
        if (!this.mUserExtraPaddingPolicy) {
            initExtraPaddingPolicy();
        }
        if (this.mExtraPaddingInitEnable && this.mExtraPaddingPolicy != null && (themedContext = getThemedContext()) != null) {
            updateExtraPaddingHorizontal(themedContext, this.mExtraPaddingPolicy, viewGroup != null ? viewGroup.getMeasuredWidth() : 0, viewGroup != null ? viewGroup.getMeasuredHeight() : 0);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        stopHighlight();
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraPaddingPolicy(ExtraPaddingPolicy extraPaddingPolicy) {
        if (extraPaddingPolicy != null) {
            this.mExtraPaddingPolicy = extraPaddingPolicy;
            this.mUserExtraPaddingPolicy = true;
        } else if (this.mUserExtraPaddingPolicy && this.mExtraPaddingPolicy != null) {
            this.mUserExtraPaddingPolicy = false;
            initExtraPaddingPolicy();
        }
        View view = this.mListContainer;
        if (view != null) {
            view.requestLayout();
        }
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public ExtraPaddingPolicy getExtraPaddingPolicy() {
        return this.mExtraPaddingPolicy;
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void addExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        if (this.mExtraPaddingObserver == null) {
            this.mExtraPaddingObserver = new CopyOnWriteArrayList();
        }
        if (this.mExtraPaddingObserver.contains(extraPaddingObserver)) {
            return;
        }
        this.mExtraPaddingObserver.add(extraPaddingObserver);
        extraPaddingObserver.setExtraHorizontalPadding(this.mExtraHorizontalPadding);
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void removeExtraPaddingObserver(ExtraPaddingObserver extraPaddingObserver) {
        List<ExtraPaddingObserver> list = this.mExtraPaddingObserver;
        if (list != null) {
            list.remove(extraPaddingObserver);
        }
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingEnable(boolean z) {
        this.mExtraPaddingEnable = z;
        ExtraPaddingPolicy extraPaddingPolicy = this.mExtraPaddingPolicy;
        if (extraPaddingPolicy != null) {
            extraPaddingPolicy.setEnable(z);
        }
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public void setExtraHorizontalPaddingInitEnable(boolean z) {
        this.mExtraPaddingInitEnable = z;
    }

    @Override // miuix.container.ExtraPaddingObserver
    public boolean setExtraHorizontalPadding(int i) {
        if (this.mExtraHorizontalPadding == i) {
            return false;
        }
        this.mExtraHorizontalPadding = i;
        return true;
    }

    @Override // miuix.container.ExtraPaddingObserver
    public int getExtraHorizontalPadding() {
        return this.mExtraHorizontalPadding;
    }

    @Override // miuix.container.ExtraPaddingProcessor
    public boolean isExtraHorizontalPaddingEnable() {
        return this.mExtraPaddingEnable;
    }

    private void updateActionBarOverlay() {
        IFragment iFragment;
        Context activity;
        Fragment parentFragment = getParentFragment();
        while (true) {
            if (parentFragment == null) {
                iFragment = null;
                break;
            }
            if (parentFragment instanceof IFragment) {
                iFragment = (IFragment) parentFragment;
                if (iFragment.hasActionBar()) {
                    break;
                }
            }
            parentFragment = parentFragment.getParentFragment();
        }
        if (iFragment != null) {
            activity = iFragment.getThemedContext();
        } else {
            activity = getActivity();
        }
        if (activity != null) {
            this.mIsOverlayMode = AttributeResolver.resolveBoolean(activity, R.attr.windowActionBarOverlay, false);
        }
    }

    private void setActionBarOverLayoutBg() {
        FragmentActivity activity;
        Drawable drawableResolveDrawable;
        if (!this.mIsEnableCardStyle || (activity = getActivity()) == null) {
            return;
        }
        Window window = activity.getWindow();
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) activity.findViewById(miuix.appcompat.R.id.action_bar_overlay_layout);
        Drawable drawableResolveDrawable2 = AttributeResolver.resolveDrawable(getContext(), R.attr.preferenceCardPageBackground);
        if (!isInFloatingWindowMode() && (drawableResolveDrawable = AttributeResolver.resolveDrawable(getContext(), R.attr.preferenceCardPageNoFloatingBackground)) != null) {
            drawableResolveDrawable2 = drawableResolveDrawable;
        }
        if (actionBarOverlayLayout != null) {
            actionBarOverlayLayout.setBackground(drawableResolveDrawable2);
        } else {
            View viewFindViewById = window.getDecorView().findViewById(android.R.id.content);
            if (viewFindViewById != null && viewFindViewById.getParent() != null && (viewFindViewById.getParent() instanceof View)) {
                ((View) viewFindViewById.getParent()).setBackground(drawableResolveDrawable2);
            } else {
                window.setBackgroundDrawable(drawableResolveDrawable2);
            }
        }
        if (EnvStateManager.isFullScreenGestureMode(getContext())) {
            return;
        }
        int i = window.getAttributes().flags;
        boolean z = (Integer.MIN_VALUE & i) != 0;
        boolean z2 = (i & 134217728) != 0;
        if (z && !z2 && (drawableResolveDrawable2 instanceof ColorDrawable)) {
            window.setNavigationBarColor(((ColorDrawable) drawableResolveDrawable2).getColor());
        }
    }

    protected boolean isInFloatingWindowMode() {
        FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            return ((AppCompatActivity) activity).isInFloatingWindowMode();
        }
        return false;
    }

    protected boolean isActionBarOverlay() {
        return this.mIsOverlayMode;
    }

    protected boolean isInMiuiSettingMultiWindowMode() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return IntentUtils.isIntentFromSettingsSplit(activity.getIntent());
        }
        return false;
    }

    private void initExtraPaddingPolicy() {
        ExtraPaddingPolicy extraPaddingPolicyCreateDefault = ExtraPaddingPolicy.Builder.createDefault(this.mDeviceType, ContainerToken.PADDING_BASE_DP, ContainerToken.PADDING_HORIZONTAL_COMMON);
        this.mExtraPaddingPolicy = extraPaddingPolicyCreateDefault;
        if (extraPaddingPolicyCreateDefault != null) {
            extraPaddingPolicyCreateDefault.setEnable(this.mExtraPaddingEnable);
            float f = getResources().getDisplayMetrics().density;
            if (this.mExtraPaddingPolicy.isEnable()) {
                this.mExtraHorizontalPadding = (int) ((this.mExtraPaddingPolicy.getExtraPaddingDp() * f) + 0.5f);
            } else {
                this.mExtraHorizontalPadding = 0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateExtraPaddingHorizontal(Context context, ExtraPaddingPolicy extraPaddingPolicy, int i, int i2) {
        Resources resources = context.getResources();
        WindowBaseInfo windowInfo = EnvStateManager.getWindowInfo(context, resources.getConfiguration());
        if (i == 0) {
            i = windowInfo.windowSize.x;
        }
        int i3 = i;
        if (i2 == 0) {
            i2 = windowInfo.windowSize.y;
        }
        float f = resources.getDisplayMetrics().density;
        extraPaddingPolicy.onContainerSizeChanged(windowInfo.windowSizeDp.x, windowInfo.windowSizeDp.y, i3, i2, f, isInFloatingWindowMode());
        return setExtraHorizontalPadding(extraPaddingPolicy.isEnable() ? (int) ((extraPaddingPolicy.getExtraPaddingDp() * f) + 0.5f) : 0);
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ActionBarOverlayLayout actionBarOverlayLayout;
        RecyclerView recyclerView = (RecyclerView) layoutInflater.inflate(R.layout.miuix_preference_recyclerview, viewGroup, false);
        if (recyclerView instanceof miuix.recyclerview.widget.RecyclerView) {
            ((miuix.recyclerview.widget.RecyclerView) recyclerView).setSpringEnabled(false);
        }
        recyclerView.setLayoutManager(onCreateLayoutManager());
        Context context = recyclerView.getContext();
        int listViewPaddingTop = getListViewPaddingTop();
        int listViewPaddingBottom = getListViewPaddingBottom();
        if (listViewPaddingTop == -1) {
            listViewPaddingTop = recyclerView.getPaddingTop();
        }
        if (listViewPaddingBottom == -1) {
            listViewPaddingBottom = recyclerView.getPaddingBottom();
        }
        this.mListViewPaddingBottom = listViewPaddingBottom;
        recyclerView.setPadding(recyclerView.getPaddingLeft(), listViewPaddingTop, recyclerView.getPaddingRight(), this.mListViewPaddingBottom);
        SmoothCornerHelper.setViewSmoothCornerEnable(recyclerView, true);
        FrameDecoration frameDecoration = new FrameDecoration(this, context, null);
        this.mFrameDecoration = frameDecoration;
        frameDecoration.enableHyperMaterial(this.mEnableHyperMaterial);
        recyclerView.addItemDecoration(this.mFrameDecoration);
        recyclerView.setItemAnimator(new CardDefaultItemAnimator());
        this.mListContainer = viewGroup;
        this.mCacheListContainerMargin = getContentViewMargin(viewGroup);
        this.mListContainer.addOnLayoutChangeListener(new AnonymousClass1());
        if (viewGroup instanceof SpringBackLayout) {
            ((SpringBackLayout) viewGroup).setTarget(recyclerView);
        }
        FragmentActivity activity = getActivity();
        if (activity != null && (actionBarOverlayLayout = (ActionBarOverlayLayout) activity.findViewById(miuix.appcompat.R.id.action_bar_overlay_layout)) != null) {
            actionBarOverlayLayout.setRootSubDecor(false);
        }
        addWindowInsetsListener();
        return recyclerView;
    }

    /* JADX INFO: renamed from: miuix.preference.PreferenceFragment$1, reason: invalid class name */
    class AnonymousClass1 implements View.OnLayoutChangeListener {
        AnonymousClass1() {
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            Context context = PreferenceFragment.this.getContext();
            if (context != null) {
                int i9 = i8 - i6;
                int i10 = i3 - i;
                int i11 = i4 - i2;
                if (i10 == i7 - i5 && i11 == i9) {
                    return;
                }
                if (PreferenceFragment.this.mFrameDecoration != null) {
                    PreferenceFragment.this.mFrameDecoration.updateContainerHeight(i11);
                }
                if (PreferenceFragment.this.mExtraPaddingPolicy != null) {
                    PreferenceFragment preferenceFragment = PreferenceFragment.this;
                    if (preferenceFragment.updateExtraPaddingHorizontal(context, preferenceFragment.mExtraPaddingPolicy, i10, i11)) {
                        int extraHorizontalPadding = PreferenceFragment.this.getExtraHorizontalPadding();
                        if (PreferenceFragment.this.mExtraPaddingObserver != null) {
                            for (int i12 = 0; i12 < PreferenceFragment.this.mExtraPaddingObserver.size(); i12++) {
                                ((ExtraPaddingObserver) PreferenceFragment.this.mExtraPaddingObserver.get(i12)).onExtraPaddingChanged(extraHorizontalPadding);
                            }
                        }
                        PreferenceFragment.this.onExtraPaddingChanged(extraHorizontalPadding);
                        final RecyclerView listView = PreferenceFragment.this.getListView();
                        if (listView != null) {
                            if (PreferenceFragment.this.mGroupAdapter != null) {
                                PreferenceFragment.this.mGroupAdapter.onExtraPaddingChanged(extraHorizontalPadding);
                            }
                            listView.post(new Runnable() { // from class: miuix.preference.PreferenceFragment$1$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    PreferenceFragment.AnonymousClass1.lambda$onLayoutChange$0(listView);
                                }
                            });
                        }
                    }
                }
            }
        }

        static /* synthetic */ void lambda$onLayoutChange$0(RecyclerView recyclerView) {
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator != null) {
                itemAnimator.endAnimations();
            }
        }
    }

    public void handleNavigationBarInsetsEnabled(boolean z) {
        this.mEnableNavigationBarInsets = z;
    }

    public void handleWindowInsetsEnabled(boolean z) {
        this.mEnableWindowInsets = z;
    }

    private void addWindowInsetsListener() {
        View view;
        if (!this.mEnableWindowInsets || isActionBarOverlay() || (view = this.mListContainer) == null) {
            return;
        }
        ViewUtils.doOnApplyWindowInsets(view, new ViewUtils.OnApplyWindowInsetsListener() { // from class: miuix.preference.PreferenceFragment.2
            @Override // miuix.internal.util.ViewUtils.OnApplyWindowInsetsListener
            public WindowInsetsCompat onApplyWindowInsets(View view2, WindowInsetsCompat windowInsetsCompat, ViewUtils.RelativePadding relativePadding) {
                WindowInsetsCompat rootWindowInsets;
                if (!PreferenceFragment.this.isLayoutHideNavigation(view2) || (rootWindowInsets = ViewCompat.getRootWindowInsets(view2)) == null) {
                    return windowInsetsCompat;
                }
                Insets insets = rootWindowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
                if (insets != null && PreferenceFragment.this.mTempNavigationBarInsets != null && PreferenceFragment.this.mTempNavigationBarInsets.equals(insets)) {
                    return windowInsetsCompat;
                }
                PreferenceFragment.this.mTempNavigationBarInsets = insets;
                if (PreferenceFragment.this.mEnableNavigationBarInsets) {
                    PreferenceFragment preferenceFragment = PreferenceFragment.this;
                    preferenceFragment.applyWindowInsets(preferenceFragment.mListContainer, PreferenceFragment.this.mTempNavigationBarInsets);
                }
                PreferenceFragment preferenceFragment2 = PreferenceFragment.this;
                preferenceFragment2.setRecyclerViewPadding(preferenceFragment2.mTempNavigationBarInsets);
                return windowInsetsCompat;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLayoutHideNavigation(View view) {
        return MiuixUIUtils.isTargetSdkVersionAboveV(view.getContext()) || (view.getWindowSystemUiVisibility() & 512) != 0;
    }

    private Rect getContentViewMargin(View view) {
        if (view == null) {
            return new Rect();
        }
        ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(view);
        return new Rect(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyWindowInsets(View view, Insets insets) {
        int i;
        int i2;
        if (view == null) {
            return;
        }
        ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(view);
        Rect rect = this.mCacheListContainerMargin;
        if (rect != null) {
            i = rect.left;
            i2 = this.mCacheListContainerMargin.right;
        } else {
            i = 0;
            i2 = 0;
        }
        layoutParams.setMargins(i + insets.left, layoutParams.topMargin, i2 + insets.right, layoutParams.bottomMargin);
        view.setLayoutParams(layoutParams);
    }

    private ViewGroup.MarginLayoutParams getLayoutParams(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return (ViewGroup.MarginLayoutParams) layoutParams;
        }
        if (layoutParams != null) {
            return new ViewGroup.MarginLayoutParams(layoutParams);
        }
        return new ViewGroup.MarginLayoutParams(new ViewGroup.LayoutParams(-2, -2));
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    protected final RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        PreferenceGroupAdapter preferenceGroupAdapter = new PreferenceGroupAdapter(preferenceScreen, this.mIsEnableCardStyle, this.mCardStyle);
        this.mGroupAdapter = preferenceGroupAdapter;
        preferenceGroupAdapter.setItemSelectable(this.mItemSelectable);
        this.mGroupAdapter.setExtraHorizontalPadding(this.mExtraHorizontalPadding);
        this.mAdapterInvalid = this.mGroupAdapter.getItemCount() < 1;
        FrameDecoration frameDecoration = this.mFrameDecoration;
        if (frameDecoration != null) {
            this.mGroupAdapter.setClipPaint(frameDecoration.mPaint, this.mFrameDecoration.mMaskPaddingTop, this.mFrameDecoration.mMaskPaddingBottom, this.mFrameDecoration.mMaskPaddingStart, this.mFrameDecoration.mMaskPaddingEnd, this.mFrameDecoration.mCardRadius);
        }
        return this.mGroupAdapter;
    }

    public List<Preference> getPreferenceAmimationList() {
        PreferenceGroupAdapter preferenceGroupAdapter = this.mGroupAdapter;
        if (preferenceGroupAdapter != null) {
            return preferenceGroupAdapter.getAnimatorPreferenceGroups();
        }
        return null;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (!this.mIsOverlayMode || isEmbeddedFragment()) {
            return;
        }
        registerCoordinateScrollView(this.mListContainer);
        getListView().setClipToPadding(false);
        Rect contentInset = getContentInset();
        if (contentInset == null || contentInset.isEmpty()) {
            return;
        }
        onContentInsetChanged(contentInset);
    }

    @Override // miuix.appcompat.app.IFragment
    public ActionBar getActionBar() {
        ActivityResultCaller parentFragment = getParentFragment();
        FragmentActivity activity = getActivity();
        if (parentFragment == null && (activity instanceof AppCompatActivity)) {
            return ((AppCompatActivity) activity).getAppCompatActionBar();
        }
        if (parentFragment instanceof IFragment) {
            return ((IFragment) parentFragment).getActionBar();
        }
        return null;
    }

    public void registerCoordinateScrollView(View view) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.registerCoordinateScrollView(view);
        }
    }

    public void unregisterCoordinateScrollView(View view) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.unregisterCoordinateScrollView(view);
        }
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public Rect getContentInset() {
        if (this.mIsOverlayMode && this.mContentInset == null) {
            ActivityResultCaller parentFragment = getParentFragment();
            if (parentFragment == null && (getActivity() instanceof AppCompatActivity)) {
                this.mContentInset = ((AppCompatActivity) getActivity()).getContentInset();
            } else if (parentFragment instanceof IFragment) {
                this.mContentInset = ((IFragment) parentFragment).getContentInset();
            }
        }
        return this.mContentInset;
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public boolean requestDispatchContentInset() {
        ActivityResultCaller parentFragment = getParentFragment();
        if (parentFragment instanceof IFragment) {
            return ((IFragment) parentFragment).requestDispatchContentInset();
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            return ((AppCompatActivity) activity).requestDispatchContentInset();
        }
        return false;
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void bindViewWithContentInset(View view) {
        ActivityResultCaller parentFragment = getParentFragment();
        if (parentFragment instanceof IFragment) {
            ((IFragment) parentFragment).bindViewWithContentInset(view);
            return;
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).bindViewWithContentInset(view);
        }
    }

    @Override // miuix.appcompat.app.IContentInsetState
    public void onContentInsetChanged(Rect rect) {
        if (rect == null) {
            return;
        }
        setRecyclerViewPadding(Insets.of(rect));
        applyWindowInsets(this.mListContainer, Insets.of(rect));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRecyclerViewPadding(Insets insets) {
        View view = getView();
        RecyclerView listView = getListView();
        if (view == null || listView == null) {
            return;
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            ActionBarImpl actionBarImpl = (ActionBarImpl) actionBar;
            if (actionBarImpl.getContentView() != null) {
                Rect rect = new Rect();
                Rect rect2 = new Rect();
                actionBarImpl.getContentView().getGlobalVisibleRect(rect);
                view.getGlobalVisibleRect(rect2);
                listView.setPadding(listView.getPaddingLeft(), listView.getPaddingTop(), listView.getPaddingRight(), Math.max(0, insets.bottom - Math.max(0, rect.bottom - rect2.bottom)) + this.mListViewPaddingBottom);
                return;
            }
        }
        listView.setPadding(listView.getPaddingLeft(), listView.getPaddingTop(), listView.getPaddingRight(), insets.bottom + this.mListViewPaddingBottom);
    }

    public void requestApplyInsets() {
        View view = this.mListContainer;
        if (view != null) {
            this.mTempNavigationBarInsets = null;
            ViewCompat.requestApplyInsets(view);
        }
    }

    @Override // miuix.appcompat.app.IFragment
    public Context getThemedContext() {
        return getContext();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        List<ExtraPaddingObserver> list = this.mExtraPaddingObserver;
        if (list != null) {
            list.clear();
        }
        unregisterCoordinateScrollView(this.mListContainer);
    }

    public void requestHighlight(final String str) {
        getListView().post(new Runnable() { // from class: miuix.preference.PreferenceFragment.3
            @Override // java.lang.Runnable
            public void run() {
                if (PreferenceFragment.this.mGroupAdapter != null) {
                    PreferenceFragment.this.mGroupAdapter.requestHighlight(PreferenceFragment.this.getListView(), str);
                }
            }
        });
    }

    public void stopHighlight() {
        PreferenceGroupAdapter preferenceGroupAdapter = this.mGroupAdapter;
        if (preferenceGroupAdapter != null) {
            preferenceGroupAdapter.stopHighlight();
        }
    }

    public boolean isHighlightRequested() {
        PreferenceGroupAdapter preferenceGroupAdapter = this.mGroupAdapter;
        if (preferenceGroupAdapter != null) {
            return preferenceGroupAdapter.isHighlightRequested();
        }
        return false;
    }

    @Override // androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        PreferenceScreen preferenceScreen;
        FrameDecoration frameDecoration;
        boolean extraHorizontalPadding;
        super.onConfigurationChanged(configuration);
        if (getActivity() == null) {
            return;
        }
        Context context = getContext();
        if (context != null) {
            setActionBarOverLayoutBg();
            int iDetectType = DeviceHelper.detectType(context);
            if (this.mDeviceType != iDetectType) {
                this.mDeviceType = iDetectType;
                if (!this.mUserExtraPaddingPolicy) {
                    this.mExtraPaddingPolicy = ExtraPaddingPolicy.Builder.createDefault(iDetectType, ContainerToken.PADDING_BASE_DP, ContainerToken.PADDING_HORIZONTAL_COMMON);
                }
                ExtraPaddingPolicy extraPaddingPolicy = this.mExtraPaddingPolicy;
                if (extraPaddingPolicy != null) {
                    extraPaddingPolicy.setEnable(this.mExtraPaddingEnable);
                    if (this.mExtraPaddingInitEnable) {
                        extraHorizontalPadding = updateExtraPaddingHorizontal(context, this.mExtraPaddingPolicy, -1, -1);
                    } else {
                        extraHorizontalPadding = setExtraHorizontalPadding(this.mExtraPaddingPolicy.isEnable() ? (int) (this.mExtraPaddingPolicy.getExtraPaddingDp() * getResources().getDisplayMetrics().density) : 0);
                    }
                    if (extraHorizontalPadding) {
                        int extraHorizontalPadding2 = getExtraHorizontalPadding();
                        PreferenceGroupAdapter preferenceGroupAdapter = this.mGroupAdapter;
                        if (preferenceGroupAdapter != null) {
                            preferenceGroupAdapter.setExtraHorizontalPadding(extraHorizontalPadding2);
                        }
                        if (this.mExtraPaddingObserver != null) {
                            for (int i = 0; i < this.mExtraPaddingObserver.size(); i++) {
                                this.mExtraPaddingObserver.get(i).onExtraPaddingChanged(extraHorizontalPadding2);
                            }
                        }
                        onExtraPaddingChanged(extraHorizontalPadding2);
                    }
                }
            }
        }
        if (!isTabletOrFold() || !this.mConfigChangeUpdateViewEnable || (preferenceScreen = getPreferenceScreen()) == null || (frameDecoration = this.mFrameDecoration) == null) {
            return;
        }
        frameDecoration.initMaskPadding(preferenceScreen.getContext());
        this.mFrameDecoration.updateClipPaintColor();
        PreferenceGroupAdapter preferenceGroupAdapter2 = this.mGroupAdapter;
        if (preferenceGroupAdapter2 != null) {
            preferenceGroupAdapter2.initAttr(preferenceScreen.getContext());
            this.mGroupAdapter.setClipPaint(this.mFrameDecoration.mPaint, this.mFrameDecoration.mMaskPaddingTop, this.mFrameDecoration.mMaskPaddingBottom, this.mFrameDecoration.mMaskPaddingStart, this.mFrameDecoration.mMaskPaddingEnd, this.mFrameDecoration.mCardRadius);
        }
    }

    private boolean isTabletOrFold() {
        int i = this.mDeviceType;
        return i == 2 || i == 3 || i == 5;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragmentNewInstance;
        boolean zOnPreferenceDisplayDialog = getCallbackFragment() instanceof PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback ? ((PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) getCallbackFragment()).onPreferenceDisplayDialog(this, preference) : false;
        if (!zOnPreferenceDisplayDialog && (getActivity() instanceof PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback)) {
            zOnPreferenceDisplayDialog = ((PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) getActivity()).onPreferenceDisplayDialog(this, preference);
        }
        if (!zOnPreferenceDisplayDialog && getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) == null) {
            if (preference instanceof EditTextPreference) {
                dialogFragmentNewInstance = EditTextPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            } else if (preference instanceof ListPreference) {
                dialogFragmentNewInstance = ListPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            } else if (preference instanceof MultiSelectListPreference) {
                dialogFragmentNewInstance = MultiSelectListPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            } else {
                throw new IllegalArgumentException("Cannot display dialog for an unknown Preference type: " + preference.getClass().getSimpleName() + ". Make sure to implement onPreferenceDisplayDialog() to handle displaying a custom dialog for this Preference.");
            }
            dialogFragmentNewInstance.setTargetFragment(this, 0);
            dialogFragmentNewInstance.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        }
    }

    public void setItemSelectable(boolean z) {
        this.mItemSelectable = z;
        PreferenceGroupAdapter preferenceGroupAdapter = this.mGroupAdapter;
        if (preferenceGroupAdapter != null) {
            preferenceGroupAdapter.setItemSelectable(z);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        PreferenceGroupAdapter preferenceGroupAdapter;
        if (this.mItemSelectable && (preferenceGroupAdapter = this.mGroupAdapter) != null) {
            preferenceGroupAdapter.setSelectedPreference(preference);
        }
        return super.onPreferenceTreeClick(preference);
    }

    private class PreferenceGroupRect {
        public boolean endRadioButtonCategory;
        private boolean isRadioButton;
        private boolean isRadioButtonChecked;
        public int primeIndex;
        public RectF rectF;
        public boolean startRadioButtonCategory;
        public int type;

        private PreferenceGroupRect() {
            this.rectF = new RectF();
            this.primeIndex = -1;
            this.endRadioButtonCategory = false;
            this.startRadioButtonCategory = false;
            this.type = 0;
            this.isRadioButton = false;
            this.isRadioButtonChecked = false;
        }

        /* synthetic */ PreferenceGroupRect(PreferenceFragment preferenceFragment, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private class FrameDecoration extends BaseDecoration {
        private static final int INNER_TAG_POS_FRIST = 1;
        private static final int INNER_TAG_POS_LAST = 4;
        private static final int INNER_TAG_POS_MIDDLE = 2;
        private static final String TAG = "FrameDecoration";
        private boolean isAnimatorRunning;
        private Drawable mCardGroupBackground;
        private int mCardGroupMarginBottom;
        private final ArrayList<PreferenceGroupRect> mCardGroups;
        private int mCheckableFilterColorChecked;
        private int mCheckableFilterColorNormal;
        private Paint mGroupBgPaint;
        private int mGroupUnCheckedBgColor;
        private int mHeightPixels;
        private boolean mIsLayoutRtl;
        private int mMaskPaddingBottom;
        private int mMaskPaddingEnd;
        private int mMaskPaddingStart;
        private int mMaskPaddingTop;
        private PreferenceGroupRect mPreferenceGroupRect;

        /* synthetic */ FrameDecoration(PreferenceFragment preferenceFragment, Context context, AnonymousClass1 anonymousClass1) {
            this(context);
        }

        private FrameDecoration(Context context) {
            this.mIsLayoutRtl = false;
            this.mCardGroups = new ArrayList<>();
            this.mPaint.setAntiAlias(true);
            updateClipPaintColor();
            initMaskPadding(context);
            Paint paint = new Paint();
            this.mGroupBgPaint = paint;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            int iResolveColor = AttributeResolver.resolveColor(context, R.attr.checkablePreferenceItemColorFilterNormal);
            this.mGroupUnCheckedBgColor = iResolveColor;
            this.mGroupBgPaint.setColor(iResolveColor);
            this.mGroupBgPaint.setAntiAlias(true);
        }

        public void initMaskPadding(Context context) {
            this.mMaskPaddingTop = context.getResources().getDimensionPixelSize(R.dimen.miuix_preference_checkable_item_mask_padding_top);
            this.mMaskPaddingBottom = context.getResources().getDimensionPixelSize(R.dimen.miuix_preference_checkable_item_mask_padding_bottom);
            this.mMaskPaddingStart = AttributeResolver.resolveDimensionPixelSize(context, R.attr.preferenceCheckableItemMaskPaddingStart);
            this.mMaskPaddingEnd = AttributeResolver.resolveDimensionPixelSize(context, R.attr.preferenceCheckableItemSetMaskPaddingEnd);
            this.mCardRadius = context.getResources().getDimensionPixelSize(R.dimen.miuix_theme_radius_common);
            this.mCardMarginStart = AttributeResolver.resolveDimensionPixelSize(context, R.attr.preferenceCardGroupMarginStart);
            this.mCardMarginEnd = AttributeResolver.resolveDimensionPixelSize(context, R.attr.preferenceCardGroupMarginEnd);
            this.mCheckableFilterColorChecked = AttributeResolver.resolveColor(context, R.attr.checkablePreferenceItemColorFilterChecked);
            this.mCheckableFilterColorNormal = AttributeResolver.resolveColor(context, R.attr.checkablePreferenceItemColorFilterNormal);
            this.mCardGroupMarginBottom = context.getResources().getDimensionPixelSize(R.dimen.miuix_preference_card_group_margin_bottom);
            if (PreferenceFragment.this.mIsEnableCardStyle) {
                setCardDrawable();
            }
        }

        public void enableHyperMaterial(boolean z) {
            this.mEnableHyperMaterial = z;
            setCardDrawable();
        }

        private void setCardDrawable() {
            Drawable drawableResolveDrawable;
            Context context = PreferenceFragment.this.getContext();
            if (context != null) {
                if (this.mEnableHyperMaterial) {
                    drawableResolveDrawable = AttributeResolver.resolveDrawable(context, R.attr.preferenceHyperMaterialCardGroupBackground);
                } else {
                    drawableResolveDrawable = AttributeResolver.resolveDrawable(context, R.attr.preferenceCardGroupBackground);
                }
                this.mCardGroupBackground = drawableResolveDrawable;
                if (drawableResolveDrawable instanceof ColorDrawable) {
                    this.mPaint.setColor(((ColorDrawable) this.mCardGroupBackground).getColor());
                }
            }
        }

        public void updateContainerHeight(int i) {
            this.mHeightPixels = i;
        }

        private int findNearViewY(RecyclerView recyclerView, View view, int i, int i2, boolean z) {
            View childAt;
            if (z) {
                if (view == null || view.getTop() >= this.mHeightPixels) {
                    return -1;
                }
                do {
                    i++;
                    if (i < i2) {
                        childAt = recyclerView.getChildAt(i);
                    }
                } while (childAt == null);
                return (int) childAt.getY();
            }
            for (int i3 = i - 1; i3 >= i2; i3--) {
                View childAt2 = recyclerView.getChildAt(i3);
                if (childAt2 != null) {
                    return ((int) childAt2.getY()) + childAt2.getHeight();
                }
            }
            return -1;
        }

        private boolean checkEndRadioButtonPreferenceCategory(RecyclerView recyclerView, int i, int i2) {
            int i3 = i + 1;
            if (i3 < i2) {
                return !(PreferenceFragment.this.mGroupAdapter.getItem(recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i3))) instanceof RadioSetPreferenceCategory);
            }
            return false;
        }

        /* JADX WARN: Code duplicated, block: B:16:0x0072  */
        /* JADX WARN: Code duplicated, block: B:44:0x0121  */
        @Override // miuix.recyclerview.card.base.BaseDecoration
        public void calculateGroupRectAndDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state, RecyclerView.Adapter<?> adapter) {
            int i;
            int width;
            int i2;
            int i3;
            int i4;
            int i5;
            Preference preference;
            PreferenceGroupRect preferenceGroupRect;
            if (PreferenceFragment.this.mAdapterInvalid || PreferenceFragment.this.isDisableAllCardStyle()) {
                return;
            }
            this.mCardGroups.clear();
            int childCount = recyclerView.getChildCount();
            boolean zIsLayoutRtl = androidx.appcompat.widget.ViewUtils.isLayoutRtl(recyclerView);
            this.mIsLayoutRtl = zIsLayoutRtl;
            if (zIsLayoutRtl) {
                i = this.mCardMarginEnd + PreferenceFragment.this.mExtraHorizontalPadding;
                width = recyclerView.getWidth() - this.mCardMarginStart;
                i2 = PreferenceFragment.this.mExtraHorizontalPadding;
            } else {
                i = this.mCardMarginStart + PreferenceFragment.this.mExtraHorizontalPadding;
                width = recyclerView.getWidth() - this.mCardMarginEnd;
                i2 = PreferenceFragment.this.mExtraHorizontalPadding;
            }
            int i6 = width - i2;
            int i7 = i;
            int i8 = 0;
            for (int i9 = 0; i9 < childCount; i9++) {
                View childAt = recyclerView.getChildAt(i9);
                int childAdapterPosition = recyclerView.getChildAdapterPosition(childAt);
                Preference item = PreferenceFragment.this.mGroupAdapter.getItem(childAdapterPosition);
                if (item == null) {
                    i4 = i8;
                    i8 = i4;
                } else {
                    int positionType = PreferenceFragment.this.mGroupAdapter.getPositionType(childAdapterPosition);
                    AnonymousClass1 anonymousClass1 = null;
                    if (i8 < this.mCardGroups.size()) {
                        if (this.mPreferenceGroupRect == null) {
                            this.mPreferenceGroupRect = this.mCardGroups.get(i8);
                        }
                    } else {
                        PreferenceGroupRect preferenceGroupRect2 = new PreferenceGroupRect(PreferenceFragment.this, anonymousClass1);
                        this.mPreferenceGroupRect = preferenceGroupRect2;
                        this.mCardGroups.add(preferenceGroupRect2);
                    }
                    boolean z = item instanceof RadioButtonPreference;
                    if (z || (item.getParent() instanceof RadioSetPreferenceCategory)) {
                        i4 = i8;
                        RadioSetPreferenceCategory radioSetPreferenceCategory = item.getParent() instanceof RadioSetPreferenceCategory ? (RadioSetPreferenceCategory) item.getParent() : null;
                        if (positionType == 1 || positionType == 2) {
                            this.mPreferenceGroupRect.type |= 1;
                            this.mPreferenceGroupRect.startRadioButtonCategory = true;
                            this.mPreferenceGroupRect.isRadioButton = true;
                            if (item.getParent() != null) {
                                i5 = 4;
                                calculateGroupTop(recyclerView, item, childAt, childAdapterPosition, i9);
                            } else {
                                i5 = 4;
                            }
                        } else {
                            i5 = 4;
                        }
                        if (positionType == i5 || positionType == 3) {
                            this.mPreferenceGroupRect.isRadioButton = true;
                            this.mPreferenceGroupRect.type |= 2;
                            if (this.mPreferenceGroupRect.rectF.bottom < childAt.getY() + childAt.getHeight()) {
                                this.mPreferenceGroupRect.rectF.bottom = childAt.getY() + childAt.getHeight();
                            }
                        }
                        if (radioSetPreferenceCategory != null) {
                            preference = item;
                            if (radioSetPreferenceCategory.getPrimaryPreference() == preference && (preferenceGroupRect = this.mPreferenceGroupRect) != null) {
                                preferenceGroupRect.primeIndex = i9;
                            }
                        } else {
                            preference = 
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x016f: MOVE (r2v29 'preference' androidx.preference.Preference) = (r5v15 androidx.preference.Preference) in method: miuix.preference.PreferenceFragment.FrameDecoration.calculateGroupRectAndDraw(android.graphics.Canvas, androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$State, androidx.recyclerview.widget.RecyclerView$Adapter<?>):void, file: classes3.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:310)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:273)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:94)
                                	at jadx.core.dex.nodes.IBlock.generate(IBlock.java:15)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.dex.regions.Region.generate(Region.java:35)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:83)
                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:140)
                                	at jadx.core.dex.regions.conditions.IfRegion.generate(IfRegion.java:90)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.dex.regions.Region.generate(Region.java:35)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:83)
                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:126)
                                	at jadx.core.dex.regions.conditions.IfRegion.generate(IfRegion.java:90)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.dex.regions.Region.generate(Region.java:35)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:83)
                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:140)
                                	at jadx.core.dex.regions.conditions.IfRegion.generate(IfRegion.java:90)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.dex.regions.Region.generate(Region.java:35)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:83)
                                	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:195)
                                	at jadx.core.dex.regions.loops.LoopRegion.generate(LoopRegion.java:173)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.dex.regions.Region.generate(Region.java:35)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.dex.regions.Region.generate(Region.java:35)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.dex.regions.Region.generate(Region.java:35)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:291)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:270)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:420)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:345)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:299)
                                	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(Unknown Source)
                                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                                	at java.base/java.util.stream.SortedOps$RefSortingSink.end(Unknown Source)
                                	at java.base/java.util.stream.Sink$ChainedReference.end(Unknown Source)
                                	at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.end(Unknown Source)
                                	at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
                                	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
                                	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(Unknown Source)
                                	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(Unknown Source)
                                	at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
                                	at java.base/java.util.stream.ReferencePipeline.forEach(Unknown Source)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:295)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:284)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:268)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:160)
                                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:320)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:297)
                                	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.accept(Unknown Source)
                                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                                	at java.base/java.util.stream.SortedOps$RefSortingSink.end(Unknown Source)
                                	at java.base/java.util.stream.Sink$ChainedReference.end(Unknown Source)
                                	at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.end(Unknown Source)
                                	at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
                                	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
                                	at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(Unknown Source)
                                	at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(Unknown Source)
                                	at java.base/java.util.stream.AbstractPipeline.evaluate(Unknown Source)
                                	at java.base/java.util.stream.ReferencePipeline.forEach(Unknown Source)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:295)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:284)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:268)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:160)
                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:104)
                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:45)
                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:34)
                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:22)
                                	at jadx.core.ProcessClass.process(ProcessClass.java:89)
                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:127)
                                	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:405)
                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:393)
                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:311)
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Code variable not set in r5v15 androidx.preference.Preference
                                	at jadx.core.dex.instructions.args.SSAVar.getCodeVar(SSAVar.java:236)
                                */
                            /*
                                Method dump skipped, instruction units count: 718
                                To view this dump change 'Code comments level' option to 'DEBUG'
                            */
                            throw new UnsupportedOperationException("Method not decompiled: miuix.preference.PreferenceFragment.FrameDecoration.calculateGroupRectAndDraw(android.graphics.Canvas, androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$State, androidx.recyclerview.widget.RecyclerView$Adapter):void");
                        }

                        private void calculateGroupBottom(RecyclerView recyclerView, Preference preference, View view, int i, int i2) {
                            if (preference.getParent() == null || view == null) {
                                return;
                            }
                            float fFindNearViewY = findNearViewY(recyclerView, view, i, i2, true);
                            if (PreferenceFragment.this.mGroupAdapter.getAnimatorPreferenceGroups().contains(preference.getParent())) {
                                if (fFindNearViewY == -1.0f || getNextPreference(recyclerView, i, i2) == null) {
                                    this.mPreferenceGroupRect.rectF.bottom = view.getY() + view.getHeight();
                                    return;
                                } else {
                                    this.mPreferenceGroupRect.rectF.bottom = fFindNearViewY - this.mCardGroupMarginBottom;
                                    return;
                                }
                            }
                            this.mPreferenceGroupRect.rectF.bottom = view.getY() + view.getHeight();
                        }

                        private void calculateGroupTop(RecyclerView recyclerView, Preference preference, View view, int i, int i2) {
                            if (preference.getParent() != null) {
                                if (PreferenceFragment.this.mGroupAdapter.getAnimatorPreferenceGroups().contains(preference.getParent())) {
                                    boolean zCheckPreIsAtomicPreference = checkPreIsAtomicPreference(i);
                                    float fFindNearViewY = findNearViewY(recyclerView, view, i2, 0, false);
                                    if (getPrePreference(recyclerView, i2) == null) {
                                        this.mPreferenceGroupRect.rectF.top = view.getY();
                                    } else if (zCheckPreIsAtomicPreference) {
                                        if (fFindNearViewY == -1.0f) {
                                            this.mPreferenceGroupRect.rectF.top = view.getY();
                                        } else {
                                            this.mPreferenceGroupRect.rectF.top = fFindNearViewY + this.mCardGroupMarginBottom;
                                        }
                                    } else if (fFindNearViewY == -1.0f) {
                                        this.mPreferenceGroupRect.rectF.top = view.getY();
                                    } else {
                                        this.mPreferenceGroupRect.rectF.top = fFindNearViewY;
                                    }
                                } else {
                                    this.mPreferenceGroupRect.rectF.top = view.getY();
                                }
                                if (this.mPreferenceGroupRect.rectF.bottom < view.getY() + view.getHeight()) {
                                    this.mPreferenceGroupRect.rectF.bottom = view.getY() + view.getHeight();
                                    return;
                                }
                                return;
                            }
                            this.mPreferenceGroupRect.rectF.top = view.getY();
                        }

                        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                            int childAdapterPosition;
                            Preference item;
                            if (PreferenceFragment.this.mAdapterInvalid || PreferenceFragment.this.isDisableAllCardStyle() || (item = PreferenceFragment.this.mGroupAdapter.getItem((childAdapterPosition = recyclerView.getChildAdapterPosition(view)))) == null) {
                                return;
                            }
                            if ((item.getParent() instanceof RadioSetPreferenceCategory) || ((!(item instanceof PreferenceGroup) && (item.getParent() instanceof RadioButtonPreferenceCategory)) || (item instanceof RadioButtonPreference))) {
                                setItemOffsets(rect, item, childAdapterPosition, recyclerView);
                                return;
                            }
                            if (isPreferenceCardStyleEnabled(item)) {
                                setItemOffsets(rect, item, childAdapterPosition, recyclerView);
                            }
                            if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() != childAdapterPosition + 1) {
                                return;
                            }
                            rect.bottom = 0;
                        }

                        private void setItemOffsets(Rect rect, Preference preference, int i, RecyclerView recyclerView) {
                            boolean zIsLayoutRtl = androidx.appcompat.widget.ViewUtils.isLayoutRtl(recyclerView);
                            int i2 = zIsLayoutRtl ? this.mCardMarginEnd : this.mCardMarginStart;
                            int i3 = zIsLayoutRtl ? this.mCardMarginStart : this.mCardMarginEnd;
                            rect.left = i2 + PreferenceFragment.this.mExtraHorizontalPadding;
                            rect.right = i3 + PreferenceFragment.this.mExtraHorizontalPadding;
                            calculateItemOffsets(rect, i, preference);
                        }

                        public void updateClipPaintColor() {
                            if ((PreferenceFragment.this.getActivity() instanceof AppCompatActivity) && !((AppCompatActivity) PreferenceFragment.this.getActivity()).isInFloatingWindowMode()) {
                                this.mPaint.setColor(AttributeResolver.resolveColor(PreferenceFragment.this.getContext(), R.attr.preferenceNormalCheckableMaskColor));
                            } else {
                                this.mPaint.setColor(AttributeResolver.resolveColor(PreferenceFragment.this.getContext(), R.attr.preferenceCheckableMaskColor));
                            }
                        }

                        /* JADX WARN: Multi-variable type inference failed */
                        private boolean calculateGroupRect(Preference preference, int i, int i2, RecyclerView recyclerView, int i3, int i4, View view) {
                            int groupItemType;
                            if ((preference.getParent() instanceof PreferenceScreen) && PreferenceLayoutUtils.isDynamicGroupItem(preference)) {
                                groupItemType = ((PreferencedynamicGroupController) preference).getGroupItemType();
                            } else {
                                groupItemType = preference.getParent() instanceof PreferenceScreen ? 1 : i;
                            }
                            if (groupItemType != 1 && (groupItemType != 2 || checkNextIsAtomicPreference(recyclerView, i2, i3))) {
                                if (groupItemType == 2) {
                                    this.mPreferenceGroupRect.type |= 1;
                                    calculateGroupTop(recyclerView, preference, view, i4, i2);
                                }
                                if (groupItemType == 4 || groupItemType == 3) {
                                    this.mPreferenceGroupRect.type |= 2;
                                    if (this.mPreferenceGroupRect.rectF.bottom < view.getY() + view.getHeight()) {
                                        this.mPreferenceGroupRect.rectF.bottom = view.getY() + view.getHeight();
                                    }
                                }
                                PreferenceGroupRect preferenceGroupRect = this.mPreferenceGroupRect;
                                if (preferenceGroupRect == null || groupItemType != 4) {
                                    return false;
                                }
                                preferenceGroupRect.type |= 4;
                                calculateGroupBottom(recyclerView, preference, view, i2, i3);
                                if (this.mPreferenceGroupRect.rectF.bottom <= this.mPreferenceGroupRect.rectF.top) {
                                    this.mPreferenceGroupRect.rectF.top = view.getY();
                                }
                                this.mPreferenceGroupRect = null;
                                return true;
                            }
                            this.mPreferenceGroupRect.type |= 1;
                            calculateGroupTop(recyclerView, preference, view, i4, i2);
                            if (groupItemType == 1) {
                                this.mPreferenceGroupRect.type |= 4;
                            }
                            calculateGroupBottom(recyclerView, preference, view, i2, i3);
                            this.mPreferenceGroupRect = null;
                            return true;
                        }

                        private boolean checkNextIsAtomicPreference(RecyclerView recyclerView, int i, int i2) {
                            return !(getNextPreference(recyclerView, i, i2) instanceof PreferenceGroup);
                        }

                        private Preference getNextPreference(RecyclerView recyclerView, int i, int i2) {
                            int i3 = i + 1;
                            if (i3 >= i2) {
                                return null;
                            }
                            int childAdapterPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i3));
                            if (PreferenceFragment.this.mGroupAdapter != null) {
                                return PreferenceFragment.this.mGroupAdapter.getItem(childAdapterPosition);
                            }
                            return null;
                        }

                        private boolean checkPreIsAtomicPreference(int i) {
                            int i2 = i - 1;
                            if (i2 >= 0) {
                                return !((PreferenceFragment.this.mGroupAdapter != null ? PreferenceFragment.this.mGroupAdapter.getItem(i2) : null) instanceof PreferenceGroup);
                            }
                            return false;
                        }

                        private Preference getPrePreference(RecyclerView recyclerView, int i) {
                            int i2 = i - 1;
                            if (i2 < 0) {
                                return null;
                            }
                            int childAdapterPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i2));
                            if (PreferenceFragment.this.mGroupAdapter != null) {
                                return PreferenceFragment.this.mGroupAdapter.getItem(childAdapterPosition);
                            }
                            return null;
                        }

                        private void calculateItemOffsets(Rect rect, int i, Preference preference) {
                            int positionType = PreferenceFragment.this.mGroupAdapter.getPositionType(i);
                            if ((preference.getParent() instanceof PreferenceScreen) && !PreferenceLayoutUtils.isDynamicGroupItem(preference)) {
                                positionType = 1;
                            }
                            if (positionType == 1 || positionType == 4) {
                                rect.bottom += this.mCardGroupMarginBottom;
                            }
                        }

                        /* JADX WARN: Multi-variable type inference failed */
                        private boolean isPreferenceCardStyleEnabled(Preference preference) {
                            if (!PreferenceFragment.this.mIsEnableCardStyle || (preference instanceof PreferenceGroup)) {
                                return false;
                            }
                            if (preference instanceof PreferenceStyle) {
                                return ((PreferenceStyle) preference).enabledCardStyle();
                            }
                            return true;
                        }
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public boolean isDisableAllCardStyle() {
                        return -1 == this.mCardStyle;
                    }

                    public void addGroupButtons(GroupButtonsConfig groupButtonsConfig) {
                        addGroupButtons(groupButtonsConfig, true);
                    }

                    public void addGroupButtons(GroupButtonsConfig groupButtonsConfig, boolean z) {
                        ActionBarOverlayLayout actionBarOverlayLayout;
                        FragmentActivity activity = getActivity();
                        if (activity == null || (actionBarOverlayLayout = (ActionBarOverlayLayout) activity.findViewById(miuix.appcompat.R.id.action_bar_overlay_layout)) == null || groupButtonsConfig == null) {
                            return;
                        }
                        actionBarOverlayLayout.addGroupButtons(groupButtonsConfig, z);
                    }

                    public void setGroupButtonsPanelBackground(Drawable drawable) {
                        ActionBarOverlayLayout actionBarOverlayLayout;
                        FragmentActivity activity = getActivity();
                        if (activity == null || (actionBarOverlayLayout = (ActionBarOverlayLayout) activity.findViewById(miuix.appcompat.R.id.action_bar_overlay_layout)) == null) {
                            return;
                        }
                        actionBarOverlayLayout.setGroupButtonsPanelBackground(drawable);
                    }

                    public void setGroupButtonsPanelBackgroundColor(int i) {
                        ActionBarOverlayLayout actionBarOverlayLayout;
                        FragmentActivity activity = getActivity();
                        if (activity == null || (actionBarOverlayLayout = (ActionBarOverlayLayout) activity.findViewById(miuix.appcompat.R.id.action_bar_overlay_layout)) == null) {
                            return;
                        }
                        actionBarOverlayLayout.setGroupButtonsPanelBackgroundColor(i);
                    }

                    public void setGroupButtonsPanelBackgroundResource(int i) {
                        ActionBarOverlayLayout actionBarOverlayLayout;
                        FragmentActivity activity = getActivity();
                        if (activity == null || (actionBarOverlayLayout = (ActionBarOverlayLayout) activity.findViewById(miuix.appcompat.R.id.action_bar_overlay_layout)) == null) {
                            return;
                        }
                        actionBarOverlayLayout.setGroupButtonsPanelBackgroundResource(i);
                    }

                    public void enablePreferenceHyperMaterial(boolean z) {
                        this.mEnableHyperMaterial = z;
                        FrameDecoration frameDecoration = this.mFrameDecoration;
                        if (frameDecoration != null) {
                            frameDecoration.enableHyperMaterial(z);
                            PreferenceGroupAdapter preferenceGroupAdapter = this.mGroupAdapter;
                            if (preferenceGroupAdapter != null) {
                                preferenceGroupAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
