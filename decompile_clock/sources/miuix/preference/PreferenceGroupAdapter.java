package miuix.preference;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.widget.ViewUtils;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.support.drawable.CardStateDrawable;
import com.xiaomi.onetrack.util.z;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.FolmeBlink;
import miuix.animation.internal.BlinkStateObserver;
import miuix.container.ExtraPaddingObserver;
import miuix.core.util.RomUtils;
import miuix.flexible.template.IHyperCellTemplate;
import miuix.flexible.view.HyperCellLayout;
import miuix.internal.graphics.drawable.TaggingDrawable;
import miuix.internal.util.AttributeResolver;
import miuix.preference.drawable.MaskTaggingDrawable;
import miuix.preference.flexible.AbstractBaseTemplate;
import miuix.preference.utils.PreferenceLayoutUtils;
import miuix.recyclerview.card.base.ViewOutlineHelper;
import miuix.view.CompatViewMethod;

/* JADX INFO: loaded from: classes3.dex */
class PreferenceGroupAdapter extends androidx.preference.PreferenceGroupAdapter implements BlinkStateObserver, ExtraPaddingObserver {
    private static final int[] STATES_TAGS;
    private static final int[] STATE_SET_FIRST;
    private static final int[] STATE_SET_LAST;
    private static final int[] STATE_SET_MIDDLE;
    private static final int[] STATE_SET_NO_LINE;
    private static final int[] STATE_SET_NO_TITLE;
    private static final int[] STATE_SET_SINGLE;
    static final String TAG_CARD_VIEW = "CardView";
    static final int TYPE_FIRST = 2;
    static final int TYPE_LAST = 4;
    static final int TYPE_MIDDLE = 3;
    static final int TYPE_SINGLE = 1;
    static final int TYPE_UNDEFINED = -1;
    private final List<Preference> mAnimatorPreferenceGroups;
    public int mCardMarginEnd;
    public int mCardMarginStart;
    private int mCheckableFilterColorChecked;
    private int mCheckableFilterColorNormal;
    private Paint mClipPaint;
    private RecyclerView.ItemAnimator mCurrentItemAnimator;
    private PositionDescriptor[] mDescriptors;
    private int mExtraHorizontalPadding;
    private FolmeBlink mFolmeBlink;
    private View mHighlightItemView;
    private int mHighlightPosition;
    private boolean mIsDisableAllCard;
    private boolean mIsEnableCardStyle;
    private boolean mItemSelectable;
    private View.OnTouchListener mItemTouchOnDownListener;
    private RecyclerView.OnItemTouchListener mItemTouchOnHighlightListener;
    private int mMaskPaddingBottom;
    private int mMaskPaddingEnd;
    private int mMaskPaddingStart;
    private int mMaskPaddingTop;
    private int mMaskRadius;
    private boolean mNeedCancelHighlightRequest;
    private final RecyclerView.AdapterDataObserver mObserver;
    private View.OnTouchListener mParentTouchOnHighlightListener;
    private int mPreferenceHighLightChildRadius;
    private int mRadioSetItemPaddingEndExtra;
    private int mRadioSetItemPaddingStartExtra;
    private RecyclerView mRecyclerView;
    private Preference mSelectedPreference;
    private Rect mTempBgPadding;

    static {
        int[] iArr = {android.R.attr.state_single, android.R.attr.state_first, android.R.attr.state_middle, android.R.attr.state_last, R.attr.state_no_title, R.attr.state_no_line};
        STATES_TAGS = iArr;
        Arrays.sort(iArr);
        STATE_SET_SINGLE = new int[]{android.R.attr.state_single};
        STATE_SET_FIRST = new int[]{android.R.attr.state_first};
        STATE_SET_MIDDLE = new int[]{android.R.attr.state_middle};
        STATE_SET_LAST = new int[]{android.R.attr.state_last};
        STATE_SET_NO_TITLE = new int[]{R.attr.state_no_title};
        STATE_SET_NO_LINE = new int[]{R.attr.state_no_line};
    }

    public PreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);
        this.mObserver = new RecyclerView.AdapterDataObserver() { // from class: miuix.preference.PreferenceGroupAdapter.1
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                super.onChanged();
                PreferenceGroupAdapter preferenceGroupAdapter = PreferenceGroupAdapter.this;
                preferenceGroupAdapter.mDescriptors = new PositionDescriptor[preferenceGroupAdapter.getItemCount()];
            }
        };
        boolean z = false;
        this.mRadioSetItemPaddingEndExtra = 0;
        this.mExtraHorizontalPadding = 0;
        this.mHighlightPosition = -1;
        this.mHighlightItemView = null;
        this.mNeedCancelHighlightRequest = false;
        this.mParentTouchOnHighlightListener = null;
        this.mItemTouchOnHighlightListener = null;
        this.mItemTouchOnDownListener = new View.OnTouchListener() { // from class: miuix.preference.PreferenceGroupAdapter.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0) {
                    return false;
                }
                view.setPressed(true);
                return false;
            }
        };
        this.mItemSelectable = false;
        this.mTempBgPadding = new Rect();
        this.mCardMarginStart = 0;
        this.mCardMarginEnd = 0;
        this.mAnimatorPreferenceGroups = new ArrayList();
        int iResolveInt = AttributeResolver.resolveInt(preferenceGroup.getContext(), R.attr.preferenceCardStyleEnable, 1);
        if (iResolveInt == 2 || (RomUtils.getHyperOsVersion() > 1 && iResolveInt == 1)) {
            z = true;
        }
        init(preferenceGroup, z, iResolveInt);
    }

    public PreferenceGroupAdapter(PreferenceGroup preferenceGroup, boolean z, int i) {
        super(preferenceGroup);
        this.mObserver = new RecyclerView.AdapterDataObserver() { // from class: miuix.preference.PreferenceGroupAdapter.1
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                super.onChanged();
                PreferenceGroupAdapter preferenceGroupAdapter = PreferenceGroupAdapter.this;
                preferenceGroupAdapter.mDescriptors = new PositionDescriptor[preferenceGroupAdapter.getItemCount()];
            }
        };
        this.mRadioSetItemPaddingEndExtra = 0;
        this.mExtraHorizontalPadding = 0;
        this.mHighlightPosition = -1;
        this.mHighlightItemView = null;
        this.mNeedCancelHighlightRequest = false;
        this.mParentTouchOnHighlightListener = null;
        this.mItemTouchOnHighlightListener = null;
        this.mItemTouchOnDownListener = new View.OnTouchListener() { // from class: miuix.preference.PreferenceGroupAdapter.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0) {
                    return false;
                }
                view.setPressed(true);
                return false;
            }
        };
        this.mItemSelectable = false;
        this.mTempBgPadding = new Rect();
        this.mCardMarginStart = 0;
        this.mCardMarginEnd = 0;
        this.mAnimatorPreferenceGroups = new ArrayList();
        init(preferenceGroup, z, i);
    }

    private void init(PreferenceGroup preferenceGroup, boolean z, int i) {
        this.mIsEnableCardStyle = z;
        this.mIsDisableAllCard = -1 == i;
        this.mDescriptors = new PositionDescriptor[getItemCount()];
        initAttr(preferenceGroup.getContext());
    }

    public void initAttr(Context context) {
        this.mRadioSetItemPaddingStartExtra = AttributeResolver.resolveDimensionPixelSize(context, R.attr.preferenceRadioSetChildExtraPaddingStart);
        this.mCheckableFilterColorChecked = AttributeResolver.resolveColor(context, R.attr.checkablePreferenceItemColorFilterChecked);
        this.mCheckableFilterColorNormal = AttributeResolver.resolveColor(context, R.attr.checkablePreferenceItemColorFilterNormal);
        this.mPreferenceHighLightChildRadius = context.getResources().getDimensionPixelSize(R.dimen.miuix_preference_high_light_radius);
        this.mCardMarginStart = AttributeResolver.resolveDimensionPixelSize(context, R.attr.preferenceCardGroupMarginStart);
        this.mCardMarginEnd = AttributeResolver.resolveDimensionPixelSize(context, R.attr.preferenceCardGroupMarginEnd);
    }

    public void setClipPaint(Paint paint, int i, int i2, int i3, int i4, int i5) {
        this.mClipPaint = paint;
        this.mMaskPaddingTop = i;
        this.mMaskPaddingBottom = i2;
        this.mMaskPaddingStart = i3;
        this.mMaskPaddingEnd = i4;
        this.mMaskRadius = i5;
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

    @Override // miuix.container.ExtraPaddingObserver
    public void onExtraPaddingChanged(int i) {
        this.mExtraHorizontalPadding = i;
        notifyDataSetChanged();
    }

    public void setItemSelectable(boolean z) {
        this.mItemSelectable = z;
    }

    public void setSelectedPreference(Preference preference) {
        this.mSelectedPreference = preference;
        notifyDataSetChanged();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean isNeedSetOutlineForItem(Preference preference) {
        if (preference instanceof PreferenceStyle) {
            return ((PreferenceStyle) preference).enabledCardStyle();
        }
        return this.mIsEnableCardStyle;
    }

    private boolean isNeedSetOutline(int i, Preference preference) {
        return (i != -1 && this.mIsEnableCardStyle && !(preference instanceof PreferenceScreen) && isNeedSetOutlineForItem(preference)) || (preference instanceof RadioButtonPreference) || (preference != null && (preference.getParent() instanceof RadioSetPreferenceCategory));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.preference.PreferenceGroupAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder, int i) {
        Drawable background;
        boolean z = preferenceViewHolder.itemView instanceof HyperCellLayout;
        if (z) {
            IHyperCellTemplate template = ((HyperCellLayout) preferenceViewHolder.itemView).getTemplate();
            if (template instanceof AbstractBaseTemplate) {
                ((AbstractBaseTemplate) template).storeVisibilityBeforeUpdate(preferenceViewHolder);
            }
        }
        super.onBindViewHolder(preferenceViewHolder, i);
        CompatViewMethod.setForceDarkAllowed(preferenceViewHolder.itemView, false);
        Preference item = getItem(i);
        if (!(item instanceof PreferenceAccessibility) || ((PreferenceAccessibility) item).isAccessibilityEnabled()) {
            handleAccessibility(item, preferenceViewHolder);
        }
        boolean z2 = !(item instanceof DropDownPreference);
        if (z2) {
            preferenceViewHolder.itemView.setOnTouchListener(null);
        }
        if (this.mItemSelectable) {
            preferenceViewHolder.itemView.setActivated(item == this.mSelectedPreference);
        } else {
            preferenceViewHolder.itemView.setActivated(false);
        }
        PositionDescriptor positionDescriptor = this.mDescriptors[i];
        int i2 = positionDescriptor != null ? positionDescriptor.type : -1;
        final int preferenceDescriptor = getPreferenceDescriptor(item, i);
        if (!this.mIsDisableAllCard && isNeedSetOutline(preferenceDescriptor, item) && Build.VERSION.SDK_INT > 31) {
            ViewOutlineHelper.setItemCardOutline(preferenceViewHolder, preferenceDescriptor, this.mMaskRadius, i2 != preferenceDescriptor, this.mRecyclerView.getItemAnimator() != null ? this.mRecyclerView.getItemAnimator().getAddDuration() : 0L);
        }
        if (item == 0) {
            return;
        }
        int i3 = this.mExtraHorizontalPadding;
        if (!this.mIsEnableCardStyle) {
            Drawable background2 = preferenceViewHolder.itemView.getBackground();
            if (((item instanceof PreferenceGroup) || (item.getParent() instanceof RadioSetPreferenceCategory) || (item.getParent() instanceof RadioButtonPreferenceCategory) || (item instanceof RadioButtonPreference)) && !(item instanceof PreferenceScreen)) {
                if (item instanceof androidx.preference.PreferenceCategory) {
                    if (background2 != null) {
                        if (background2 instanceof LayerDrawable) {
                            ((LayerDrawable) background2).setLayerInset(0, i3, 0, i3, 0);
                            TaggingDrawable taggingDrawable = new TaggingDrawable(background2);
                            preferenceViewHolder.itemView.setBackground(taggingDrawable);
                            int[] iArr = this.mDescriptors[i].status;
                            if (iArr != null) {
                                taggingDrawable.setTaggingState(iArr);
                            }
                        }
                        background2.getPadding(this.mTempBgPadding);
                        preferenceViewHolder.itemView.setPadding(this.mTempBgPadding.left + i3, this.mTempBgPadding.top, this.mTempBgPadding.right + i3, this.mTempBgPadding.bottom);
                    }
                } else if (background2 != null) {
                    background2.getPadding(this.mTempBgPadding);
                    preferenceViewHolder.itemView.setPadding(this.mTempBgPadding.left, this.mTempBgPadding.top, this.mTempBgPadding.right, this.mTempBgPadding.bottom);
                }
            } else if (background2 != null) {
                background2.getPadding(this.mTempBgPadding);
                boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this.mRecyclerView);
                preferenceViewHolder.itemView.setPadding(this.mTempBgPadding.left + (zIsLayoutRtl ? this.mCardMarginEnd : this.mCardMarginStart) + i3, this.mTempBgPadding.top, this.mTempBgPadding.right + (zIsLayoutRtl ? this.mCardMarginStart : this.mCardMarginEnd) + i3, this.mTempBgPadding.bottom);
            }
        } else if (item instanceof PreferenceScreen) {
            Drawable background3 = preferenceViewHolder.itemView.getBackground();
            if (background3 != null) {
                background3.getPadding(this.mTempBgPadding);
                boolean zIsLayoutRtl2 = ViewUtils.isLayoutRtl(this.mRecyclerView);
                preferenceViewHolder.itemView.setPadding(this.mTempBgPadding.left + (zIsLayoutRtl2 ? this.mCardMarginEnd : this.mCardMarginStart) + i3, this.mTempBgPadding.top, this.mTempBgPadding.right + (zIsLayoutRtl2 ? this.mCardMarginStart : this.mCardMarginEnd) + i3, this.mTempBgPadding.bottom);
            }
        } else if (item instanceof androidx.preference.PreferenceCategory) {
            Drawable background4 = preferenceViewHolder.itemView.getBackground();
            if (background4 != null) {
                background4.getPadding(this.mTempBgPadding);
                preferenceViewHolder.itemView.setPadding(this.mTempBgPadding.left + i3, this.mTempBgPadding.top, this.mTempBgPadding.right + i3, this.mTempBgPadding.bottom);
            }
        } else if ((item instanceof PreferenceStyle) && !((PreferenceStyle) item).enabledCardStyle()) {
            Drawable background5 = preferenceViewHolder.itemView.getBackground();
            if (background5 != null) {
                background5.getPadding(this.mTempBgPadding);
                boolean zIsLayoutRtl3 = ViewUtils.isLayoutRtl(this.mRecyclerView);
                preferenceViewHolder.itemView.setPadding(this.mTempBgPadding.left + (zIsLayoutRtl3 ? this.mCardMarginEnd : this.mCardMarginStart) + i3, this.mTempBgPadding.top, this.mTempBgPadding.right + (zIsLayoutRtl3 ? this.mCardMarginStart : this.mCardMarginEnd) + i3, this.mTempBgPadding.bottom);
            }
        } else {
            Drawable background6 = preferenceViewHolder.itemView.getBackground();
            if (background6 != null) {
                background6.getPadding(this.mTempBgPadding);
                if (this.mTempBgPadding.left != 0 || this.mTempBgPadding.right != 0) {
                    preferenceViewHolder.itemView.setPadding(this.mTempBgPadding.left, this.mTempBgPadding.top, this.mTempBgPadding.right, this.mTempBgPadding.bottom);
                }
            }
        }
        if ((item.getParent() instanceof RadioSetPreferenceCategory) && !(item instanceof RadioButtonPreference) && (background = preferenceViewHolder.itemView.getBackground()) != null) {
            background.getPadding(this.mTempBgPadding);
            if (ViewUtils.isLayoutRtl(this.mRecyclerView)) {
                this.mTempBgPadding.right += this.mRadioSetItemPaddingStartExtra;
            } else {
                this.mTempBgPadding.left += this.mRadioSetItemPaddingStartExtra;
            }
            preferenceViewHolder.itemView.setPadding(this.mTempBgPadding.left, this.mTempBgPadding.top, this.mTempBgPadding.right, this.mTempBgPadding.bottom);
        }
        View viewFindViewById = preferenceViewHolder.itemView.findViewById(R.id.arrow_right);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(isArrowRightVisible(item) ? 0 : 8);
        }
        if (ableToUseFolmeAnim(item)) {
            if (preferenceViewHolder.itemView.findViewById(R.id.miuix_preference_navigation) == null) {
                if (preferenceViewHolder.itemView.getForeground() == null) {
                    Drawable drawableResolveDrawable = AttributeResolver.resolveDrawable(item.getContext(), R.attr.preferenceItemForeground);
                    if (drawableResolveDrawable instanceof CardStateDrawable) {
                        if (isNeedSetOutline(preferenceDescriptor, item) && Build.VERSION.SDK_INT <= 31) {
                            drawableResolveDrawable = drawableResolveDrawable.mutate();
                            ((CardStateDrawable) drawableResolveDrawable).setRadiusMode(this.mMaskRadius, preferenceDescriptor);
                        } else {
                            ((CardStateDrawable) drawableResolveDrawable).setRadius(0);
                        }
                        CardStateDrawable cardStateDrawable = (CardStateDrawable) drawableResolveDrawable;
                        cardStateDrawable.setInset(0, 0, 0, 0);
                        setPreferenceItemForegroundForHighLightChild(preferenceViewHolder.itemView, cardStateDrawable, item);
                    }
                    preferenceViewHolder.itemView.setForeground(drawableResolveDrawable);
                    if (z2) {
                        preferenceViewHolder.itemView.setOnTouchListener(this.mItemTouchOnDownListener);
                    }
                } else {
                    Drawable foreground = preferenceViewHolder.itemView.getForeground();
                    if (foreground instanceof CardStateDrawable) {
                        CardStateDrawable cardStateDrawable2 = (CardStateDrawable) foreground;
                        cardStateDrawable2.setInset(0, 0, 0, 0);
                        if (setPreferenceItemForegroundForHighLightChild(preferenceViewHolder.itemView, cardStateDrawable2, item)) {
                            preferenceViewHolder.itemView.setForeground(foreground);
                        }
                    }
                    if (Build.VERSION.SDK_INT <= 31) {
                        Drawable foreground2 = preferenceViewHolder.itemView.getForeground();
                        if ((foreground2 instanceof CardStateDrawable) && isNeedSetOutline(preferenceDescriptor, item)) {
                            if (i2 != preferenceDescriptor) {
                                preferenceViewHolder.itemView.postDelayed(new Runnable() { // from class: miuix.preference.PreferenceGroupAdapter$$ExternalSyntheticLambda0
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        this.f$0.m1928x4a27a58a(preferenceViewHolder, preferenceDescriptor);
                                    }
                                }, this.mRecyclerView.getItemAnimator() != null ? this.mRecyclerView.getItemAnimator().getAddDuration() : 100L);
                            } else {
                                ((CardStateDrawable) foreground2.mutate()).setRadiusMode(this.mMaskRadius, preferenceDescriptor);
                                preferenceViewHolder.itemView.setForeground(foreground2);
                                if (z2) {
                                    preferenceViewHolder.itemView.setOnTouchListener(this.mItemTouchOnDownListener);
                                }
                            }
                        }
                    }
                }
            } else {
                Drawable foreground3 = preferenceViewHolder.itemView.getForeground();
                if (foreground3 == null) {
                    Drawable drawableResolveDrawable2 = AttributeResolver.resolveDrawable(item.getContext(), R.attr.navigationPreferenceItemForeground);
                    if (drawableResolveDrawable2 instanceof LayerDrawable) {
                        int i4 = this.mIsEnableCardStyle ? 0 : i3;
                        ((LayerDrawable) drawableResolveDrawable2).setLayerInset(0, i4, 0, i4, 0);
                    }
                    preferenceViewHolder.itemView.setForeground(drawableResolveDrawable2);
                    if (z2) {
                        preferenceViewHolder.itemView.setOnTouchListener(this.mItemTouchOnDownListener);
                    }
                } else if (foreground3 instanceof LayerDrawable) {
                    LayerDrawable layerDrawable = (LayerDrawable) foreground3;
                    int i5 = this.mIsEnableCardStyle ? 0 : i3;
                    layerDrawable.setLayerInset(0, i5, 0, i5, 0);
                    layerDrawable.invalidateSelf();
                }
            }
        }
        checkHighlight(preferenceViewHolder, i, preferenceDescriptor, item);
        if (item instanceof PreferenceExtraPadding) {
            ((PreferenceExtraPadding) item).onPreferenceExtraPadding(preferenceViewHolder, i3);
        }
        if (z) {
            IHyperCellTemplate template2 = ((HyperCellLayout) preferenceViewHolder.itemView).getTemplate();
            if (template2 instanceof AbstractBaseTemplate) {
                ((AbstractBaseTemplate) template2).refreshLayoutIfVisibleChanged(preferenceViewHolder);
            }
        }
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$miuix-preference-PreferenceGroupAdapter, reason: not valid java name */
    /* synthetic */ void m1928x4a27a58a(PreferenceViewHolder preferenceViewHolder, int i) {
        Drawable foreground = preferenceViewHolder.itemView.getForeground();
        if (foreground instanceof CardStateDrawable) {
            ((CardStateDrawable) foreground.mutate()).setRadiusMode(this.mMaskRadius, i);
            preferenceViewHolder.itemView.setForeground(foreground);
        }
    }

    private void handleAccessibility(final Preference preference, PreferenceViewHolder preferenceViewHolder) {
        if (preference instanceof androidx.preference.PreferenceCategory) {
            TextView textView = (TextView) preferenceViewHolder.findViewById(android.R.id.title);
            if (textView == null || TextUtils.isEmpty(preference.getTitle())) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 28) {
                textView.setAccessibilityHeading(true);
                return;
            } else {
                textView.setContentDescription(((Object) preference.getTitle()) + z.b + preference.getContext().getString(R.string.miuix_accessibility_title));
                return;
            }
        }
        if (preference instanceof androidx.preference.CheckBoxPreference) {
            View viewFindViewById = preferenceViewHolder.findViewById(android.R.id.checkbox);
            if (viewFindViewById != null) {
                viewFindViewById.setImportantForAccessibility(2);
            }
            if (isCheckBoxPreferenceExcluded(preference)) {
                return;
            }
            ViewCompat.setAccessibilityDelegate(preferenceViewHolder.itemView, new AccessibilityDelegateCompat() { // from class: miuix.preference.PreferenceGroupAdapter.3
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    accessibilityNodeInfoCompat.setCheckable(preference.isEnabled());
                    accessibilityNodeInfoCompat.setClassName(Switch.class.getName());
                    accessibilityNodeInfoCompat.setChecked(((androidx.preference.CheckBoxPreference) preference).isChecked());
                    accessibilityNodeInfoCompat.setContentDescription(preference.toString());
                }
            });
            return;
        }
        if (preference instanceof SwitchPreference) {
            View viewFindViewById2 = preferenceViewHolder.findViewById(android.R.id.switch_widget);
            if (viewFindViewById2 != null) {
                viewFindViewById2.setImportantForAccessibility(2);
            }
            if (isCheckBoxPreferenceExcluded(preference)) {
                return;
            }
            ViewCompat.setAccessibilityDelegate(preferenceViewHolder.itemView, new AccessibilityDelegateCompat() { // from class: miuix.preference.PreferenceGroupAdapter.4
                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    accessibilityNodeInfoCompat.setCheckable(preference.isEnabled());
                    accessibilityNodeInfoCompat.setClassName(Switch.class.getName());
                    accessibilityNodeInfoCompat.setChecked(((SwitchPreference) preference).isChecked());
                    accessibilityNodeInfoCompat.setContentDescription(preference.toString());
                }
            });
        }
    }

    private boolean isCheckBoxPreferenceExcluded(Preference preference) {
        return (preference instanceof RadioButtonPreference) || (preference instanceof SingleChoicePreference) || (preference instanceof MultiChoicePreference);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean setPreferenceItemForegroundForHighLightChild(View view, CardStateDrawable cardStateDrawable, Preference preference) {
        View childAt;
        if (!(view instanceof ViewGroup) || (childAt = ((ViewGroup) view).getChildAt(0)) == null || !childAt.getClass().getSimpleName().contains(TAG_CARD_VIEW)) {
            return false;
        }
        int paddingLeft = view.getPaddingLeft();
        int paddingTop = view.getPaddingTop();
        int paddingRight = view.getPaddingRight();
        int paddingBottom = view.getPaddingBottom();
        if (childAt.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
            paddingLeft += marginLayoutParams.leftMargin;
            paddingTop += marginLayoutParams.topMargin;
            paddingRight += marginLayoutParams.rightMargin;
            paddingBottom += marginLayoutParams.bottomMargin;
        }
        if ((preference instanceof PreferenceStyle) && ((PreferenceStyle) preference).enabledCardStyle()) {
            cardStateDrawable.setRadius(0);
        } else {
            cardStateDrawable.setRadius(this.mPreferenceHighLightChildRadius);
        }
        cardStateDrawable.setInset(paddingLeft, paddingTop, paddingRight, paddingBottom);
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewRecycled(PreferenceViewHolder preferenceViewHolder) {
        super.onViewRecycled(preferenceViewHolder);
        stopHighlight(preferenceViewHolder.itemView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewDetachedFromWindow(PreferenceViewHolder preferenceViewHolder) {
        super.onViewDetachedFromWindow(preferenceViewHolder);
        stopHighlight(preferenceViewHolder.itemView);
    }

    public void stopHighlight() {
        View view = this.mHighlightItemView;
        if (view != null) {
            stopHighlight(view);
            FolmeBlink folmeBlink = this.mFolmeBlink;
            if (folmeBlink != null) {
                folmeBlink.detach(this);
            }
            this.mFolmeBlink = null;
            this.mNeedCancelHighlightRequest = false;
        }
    }

    public void checkHighlight(PreferenceViewHolder preferenceViewHolder, int i, int i2, Preference preference) {
        View view = preferenceViewHolder.itemView;
        if (i == this.mHighlightPosition) {
            if (!this.mNeedCancelHighlightRequest) {
                if (Boolean.TRUE.equals(view.getTag(R.id.preference_highlighted))) {
                    return;
                }
                startHighlight(view, i2, preference);
                return;
            }
            this.mNeedCancelHighlightRequest = false;
            return;
        }
        if (Boolean.TRUE.equals(view.getTag(R.id.preference_highlighted))) {
            stopHighlight(view);
        }
    }

    public boolean isHighlightRequested() {
        return this.mHighlightPosition != -1;
    }

    private void startHighlight(View view, int i, Preference preference) {
        view.setTag(R.id.preference_highlighted, true);
        if (this.mFolmeBlink == null) {
            FolmeBlink folmeBlink = (FolmeBlink) Folme.useAt(view).blink();
            this.mFolmeBlink = folmeBlink;
            folmeBlink.setTintMode(3);
            setHighlightBlinkRadius(i, preference);
            this.mFolmeBlink.attach(this);
            this.mFolmeBlink.startBlink(3, new AnimConfig[0]);
            this.mHighlightItemView = view;
        }
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            recyclerView.setItemAnimator(this.mCurrentItemAnimator);
        }
    }

    private void setHighlightBlinkRadius(int i, Preference preference) {
        float f;
        float f2;
        float f3;
        float f4 = 0.0f;
        if (Build.VERSION.SDK_INT > 31) {
            this.mFolmeBlink.setBlinkRadius(0.0f);
            return;
        }
        if (isNeedSetOutline(i, preference)) {
            if (i == 1) {
                f4 = this.mMaskRadius;
            } else {
                if (i == 2) {
                    f3 = this.mMaskRadius;
                    f = 0.0f;
                    f2 = 0.0f;
                    f4 = f3;
                } else if (i == 4) {
                    f = this.mMaskRadius;
                    f2 = f;
                    f3 = 0.0f;
                }
                this.mFolmeBlink.setBlinkRadius(f4, f3, f, f2);
                return;
            }
            f3 = f4;
            f = f3;
            f2 = f;
            this.mFolmeBlink.setBlinkRadius(f4, f3, f, f2);
            return;
        }
        this.mFolmeBlink.setBlinkRadius(0.0f);
    }

    public void stopHighlight(View view) {
        if (isHighlightRequested() && view != null && Boolean.TRUE.equals(view.getTag(R.id.preference_highlighted))) {
            Folme.useAt(view).blink().stopBlink();
            view.setTag(R.id.preference_highlighted, false);
            if (this.mHighlightItemView == view) {
                this.mHighlightItemView = null;
            }
            this.mHighlightPosition = -1;
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.removeOnItemTouchListener(this.mItemTouchOnHighlightListener);
                this.mRecyclerView.setOnTouchListener(null);
                this.mItemTouchOnHighlightListener = null;
                this.mParentTouchOnHighlightListener = null;
            }
        }
    }

    public void requestHighlight(RecyclerView recyclerView, String str) {
        final int preferenceAdapterPosition;
        if (isHighlightRequested() || recyclerView == null || TextUtils.isEmpty(str) || (preferenceAdapterPosition = getPreferenceAdapterPosition(str)) < 0) {
            return;
        }
        if (this.mParentTouchOnHighlightListener == null) {
            this.mParentTouchOnHighlightListener = new View.OnTouchListener() { // from class: miuix.preference.PreferenceGroupAdapter.5
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if ((action != 0 && action != 2 && action != 3) || PreferenceGroupAdapter.this.mHighlightItemView == null || PreferenceGroupAdapter.this.mNeedCancelHighlightRequest) {
                        return false;
                    }
                    PreferenceGroupAdapter.this.mNeedCancelHighlightRequest = true;
                    view.post(new Runnable() { // from class: miuix.preference.PreferenceGroupAdapter.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            PreferenceGroupAdapter.this.stopHighlight();
                        }
                    });
                    return true;
                }
            };
        }
        if (this.mItemTouchOnHighlightListener == null) {
            this.mItemTouchOnHighlightListener = new RecyclerView.OnItemTouchListener() { // from class: miuix.preference.PreferenceGroupAdapter.6
                @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
                public void onRequestDisallowInterceptTouchEvent(boolean z) {
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
                public boolean onInterceptTouchEvent(RecyclerView recyclerView2, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if ((action != 0 && action != 2 && action != 3) || PreferenceGroupAdapter.this.mHighlightItemView == null || PreferenceGroupAdapter.this.mNeedCancelHighlightRequest) {
                        return false;
                    }
                    PreferenceGroupAdapter.this.mNeedCancelHighlightRequest = true;
                    recyclerView2.post(new Runnable() { // from class: miuix.preference.PreferenceGroupAdapter.6.1
                        @Override // java.lang.Runnable
                        public void run() {
                            PreferenceGroupAdapter.this.stopHighlight();
                        }
                    });
                    return true;
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
                public void onTouchEvent(RecyclerView recyclerView2, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if ((action != 0 && action != 2 && action != 3) || PreferenceGroupAdapter.this.mHighlightItemView == null || PreferenceGroupAdapter.this.mNeedCancelHighlightRequest) {
                        return;
                    }
                    PreferenceGroupAdapter.this.mNeedCancelHighlightRequest = true;
                    recyclerView2.post(new Runnable() { // from class: miuix.preference.PreferenceGroupAdapter.6.2
                        @Override // java.lang.Runnable
                        public void run() {
                            PreferenceGroupAdapter.this.stopHighlight();
                        }
                    });
                }
            };
        }
        recyclerView.setOnTouchListener(this.mParentTouchOnHighlightListener);
        recyclerView.addOnItemTouchListener(this.mItemTouchOnHighlightListener);
        View childAt = recyclerView.getLayoutManager().getChildAt(preferenceAdapterPosition);
        if (childAt != null) {
            Rect rect = new Rect();
            childAt.getGlobalVisibleRect(rect);
            if (rect.height() >= childAt.getHeight()) {
                this.mHighlightPosition = preferenceAdapterPosition;
                RecyclerView recyclerView2 = this.mRecyclerView;
                if (recyclerView2 != null) {
                    this.mCurrentItemAnimator = recyclerView2.getItemAnimator();
                    this.mRecyclerView.setItemAnimator(null);
                }
                notifyItemChanged(this.mHighlightPosition);
                return;
            }
        }
        recyclerView.smoothScrollToPosition(preferenceAdapterPosition);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: miuix.preference.PreferenceGroupAdapter.7
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView3, int i) {
                super.onScrollStateChanged(recyclerView3, i);
                if (i == 0) {
                    PreferenceGroupAdapter.this.mHighlightPosition = preferenceAdapterPosition;
                    if (PreferenceGroupAdapter.this.mRecyclerView != null) {
                        PreferenceGroupAdapter preferenceGroupAdapter = PreferenceGroupAdapter.this;
                        preferenceGroupAdapter.mCurrentItemAnimator = preferenceGroupAdapter.mRecyclerView.getItemAnimator();
                        PreferenceGroupAdapter.this.mRecyclerView.setItemAnimator(null);
                    }
                    PreferenceGroupAdapter preferenceGroupAdapter2 = PreferenceGroupAdapter.this;
                    preferenceGroupAdapter2.notifyItemChanged(preferenceGroupAdapter2.mHighlightPosition);
                    recyclerView3.removeOnScrollListener(this);
                }
            }
        });
    }

    public Pair getLeftAndRightWithRTL(RecyclerView recyclerView, boolean z) {
        int width;
        int i;
        int scrollBarSize = recyclerView.getScrollBarSize();
        if (z) {
            i = scrollBarSize * 3;
            width = recyclerView.getWidth();
        } else {
            width = recyclerView.getWidth() - (scrollBarSize * 3);
            i = 0;
        }
        return new Pair(Integer.valueOf(i), Integer.valueOf(width));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean ableToUseFolmeAnim(Preference preference) {
        if (preference instanceof androidx.preference.PreferenceCategory) {
            return false;
        }
        if (preference instanceof FolmeAnimationController) {
            return ((FolmeAnimationController) preference).isTouchAnimationEnable();
        }
        return true;
    }

    private boolean isArrowRightVisible(Preference preference) {
        return (preference.getIntent() == null && preference.getFragment() == null && (preference.getOnPreferenceClickListener() == null || (preference instanceof TwoStatePreference)) && !(preference instanceof DialogPreference)) ? false : true;
    }

    /* JADX WARN: Code duplicated, block: B:9:0x0019  */
    /* JADX WARN: Multi-variable type inference failed */
    private int getPreferenceDescriptor(Preference preference, int i) {
        int[] iArr;
        PreferenceGroup parent;
        int[] iArr2;
        boolean z;
        int[] iArr3;
        int[] iArr4;
        if (i >= 0) {
            PositionDescriptor[] positionDescriptorArr = this.mDescriptors;
            if (i < positionDescriptorArr.length) {
                if (positionDescriptorArr[i] == null) {
                    positionDescriptorArr[i] = new PositionDescriptor();
                }
                iArr = this.mDescriptors[i].status;
            } else {
                iArr = null;
            }
        } else {
            iArr = null;
        }
        if (iArr == null && (parent = preference.getParent()) != null) {
            List<Preference> allVisiblePreferences = getAllVisiblePreferences(parent);
            if (allVisiblePreferences.isEmpty()) {
                return -1;
            }
            boolean z2 = parent instanceof PreferenceScreen;
            int i2 = 4;
            boolean zIsEmpty = true;
            if (z2 && PreferenceLayoutUtils.isDynamicGroupItem(preference)) {
                int groupItemType = ((PreferencedynamicGroupController) preference).getGroupItemType();
                if (groupItemType == 1) {
                    this.mDescriptors[i].status = STATE_SET_SINGLE;
                    this.mDescriptors[i].type = 1;
                } else if (groupItemType == 2) {
                    this.mDescriptors[i].status = STATE_SET_FIRST;
                    this.mDescriptors[i].type = 2;
                } else if (groupItemType == 3) {
                    this.mDescriptors[i].status = STATE_SET_MIDDLE;
                    this.mDescriptors[i].type = 3;
                } else if (groupItemType == 4) {
                    this.mDescriptors[i].status = STATE_SET_LAST;
                    this.mDescriptors[i].type = 4;
                }
            } else {
                if (!(preference instanceof PreferenceGroup) && (z2 || (((parent instanceof RadioButtonPreferenceCategory) || (parent instanceof SingleChoicePreferenceCategory) || (parent instanceof MultiChoicePreferenceCategory)) && !isNeedCardGroup(preference)))) {
                    this.mDescriptors[i].status = STATE_SET_SINGLE;
                    this.mDescriptors[i].type = 1;
                    return 1;
                }
                if (allVisiblePreferences.size() == 1) {
                    iArr2 = STATE_SET_SINGLE;
                    i2 = 1;
                } else if (preference.compareTo(allVisiblePreferences.get(0)) == 0) {
                    iArr2 = STATE_SET_FIRST;
                    i2 = 2;
                } else if (preference.compareTo(allVisiblePreferences.get(allVisiblePreferences.size() - 1)) == 0) {
                    iArr2 = STATE_SET_LAST;
                } else {
                    iArr2 = STATE_SET_MIDDLE;
                    i2 = 3;
                }
                if (preference instanceof androidx.preference.PreferenceCategory) {
                    androidx.preference.PreferenceCategory preferenceCategory = (androidx.preference.PreferenceCategory) preference;
                    if (preferenceCategory instanceof PreferenceCategory) {
                        PreferenceCategory preferenceCategory2 = (PreferenceCategory) preferenceCategory;
                        z = !preferenceCategory2.isDividerLineNeeded();
                        if (preferenceCategory2.hasTitle()) {
                            zIsEmpty = false;
                        }
                    } else {
                        zIsEmpty = TextUtils.isEmpty(preferenceCategory.getTitle());
                        z = false;
                    }
                    if (z || zIsEmpty) {
                        if (z) {
                            int[] iArr5 = STATE_SET_NO_LINE;
                            iArr3 = new int[iArr5.length];
                            System.arraycopy(iArr5, 0, iArr3, 0, iArr5.length);
                        } else {
                            iArr3 = new int[0];
                        }
                        if (zIsEmpty) {
                            int[] iArr6 = STATE_SET_NO_TITLE;
                            iArr4 = new int[iArr6.length];
                            System.arraycopy(iArr6, 0, iArr4, 0, iArr6.length);
                        } else {
                            iArr4 = new int[0];
                        }
                        int[] iArr7 = new int[iArr3.length + iArr4.length + iArr2.length];
                        System.arraycopy(iArr3, 0, iArr7, 0, iArr3.length);
                        System.arraycopy(iArr4, 0, iArr7, iArr3.length, iArr4.length);
                        System.arraycopy(iArr2, 0, iArr7, iArr3.length + iArr4.length, iArr2.length);
                        iArr2 = iArr7;
                    }
                }
                this.mDescriptors[i].status = iArr2;
                this.mDescriptors[i].type = i2;
            }
        }
        return this.mDescriptors[i].type;
    }

    private boolean isNeedCardGroup(Preference preference) {
        if (!this.mIsEnableCardStyle) {
            return false;
        }
        PreferenceGroup parent = preference.getParent();
        if ((parent instanceof RadioButtonPreferenceCategory) && (preference instanceof RadioButtonPreference)) {
            return ((RadioButtonPreferenceCategory) parent).isNeedCardGroup();
        }
        if ((parent instanceof SingleChoicePreferenceCategory) && (preference instanceof SingleChoicePreference)) {
            return ((SingleChoicePreferenceCategory) parent).getCardGroupEnabled();
        }
        if ((parent instanceof MultiChoicePreferenceCategory) && (preference instanceof MultiChoicePreference)) {
            return ((MultiChoicePreferenceCategory) parent).getCardGroupEnabled();
        }
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        registerAdapterDataObserver(this.mObserver);
        this.mRecyclerView = recyclerView;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(this.mObserver);
        this.mRecyclerView = null;
    }

    int getPositionType(int i) {
        return this.mDescriptors[i].type;
    }

    @Override // miuix.animation.internal.BlinkStateObserver
    public void updateBlinkState(boolean z) {
        RecyclerView recyclerView;
        if (!z || (recyclerView = this.mRecyclerView) == null) {
            return;
        }
        recyclerView.removeOnItemTouchListener(this.mItemTouchOnHighlightListener);
        this.mRecyclerView.setOnTouchListener(null);
        this.mItemTouchOnHighlightListener = null;
        this.mParentTouchOnHighlightListener = null;
        FolmeBlink folmeBlink = this.mFolmeBlink;
        if (folmeBlink != null) {
            folmeBlink.detach(this);
        }
    }

    class PositionDescriptor {
        int[] status;
        int type;

        PositionDescriptor() {
        }
    }

    public List<Preference> getAnimatorPreferenceGroups() {
        return this.mAnimatorPreferenceGroups;
    }

    public void clearPreferenceGroups() {
        if (this.mAnimatorPreferenceGroups.isEmpty()) {
            return;
        }
        this.mAnimatorPreferenceGroups.clear();
    }

    @Override // androidx.preference.PreferenceGroupAdapter, androidx.preference.Preference.OnPreferenceChangeInternalListener
    public void onPreferenceVisibilityChange(Preference preference) {
        PreferenceGroup parent;
        super.onPreferenceVisibilityChange(preference);
        if ((preference instanceof PreferenceGroup) || (preference.getParent() instanceof PreferenceScreen) || (parent = preference.getParent()) == null || this.mAnimatorPreferenceGroups.contains(parent)) {
            return;
        }
        this.mAnimatorPreferenceGroups.add(parent);
    }

    @Override // androidx.preference.PreferenceGroupAdapter, androidx.preference.Preference.OnPreferenceChangeInternalListener
    public void onPreferenceChange(Preference preference) {
        Preference preferenceFindPreference;
        super.onPreferenceChange(preference);
        String dependency = preference.getDependency();
        if (TextUtils.isEmpty(dependency) || (preferenceFindPreference = preference.getPreferenceManager().findPreference(dependency)) == null) {
            return;
        }
        if (preference instanceof androidx.preference.PreferenceCategory) {
            if (preferenceFindPreference instanceof TwoStatePreference) {
                preference.setVisible(((TwoStatePreference) preferenceFindPreference).isChecked());
                return;
            } else {
                preference.setVisible(preferenceFindPreference.isEnabled());
                return;
            }
        }
        preference.setVisible(preference.isEnabled());
    }

    private List<Preference> getAllVisiblePreferences(PreferenceGroup preferenceGroup) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (preference.isVisible()) {
                arrayList.add(preference);
            }
        }
        return arrayList;
    }

    private void updateViewBackgroundMask(Preference preference) {
        if (preference == null || this.mRecyclerView == null) {
            return;
        }
        if (preference instanceof RadioButtonPreferenceCategory) {
            drawRadioSetPreferenceCategory((RadioButtonPreferenceCategory) preference);
        } else if (preference instanceof RadioSetPreferenceCategory) {
            drawRadioSetPreferenceCategory((RadioSetPreferenceCategory) preference);
        } else {
            boolean z = preference instanceof RadioButtonPreference;
        }
    }

    private void drawRadioSetPreferenceCategory(RadioButtonPreferenceCategory radioButtonPreferenceCategory) {
        int preferenceCount = radioButtonPreferenceCategory.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = radioButtonPreferenceCategory.getPreference(i);
            if (preference instanceof RadioSetPreferenceCategory) {
                drawRadioSetPreferenceCategory((RadioSetPreferenceCategory) preference);
            }
        }
    }

    private void drawRadioSetPreferenceCategory(RadioSetPreferenceCategory radioSetPreferenceCategory) {
        int preferenceAdapterPosition;
        View childAt;
        int preferenceCount = radioSetPreferenceCategory.getPreferenceCount();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = radioSetPreferenceCategory.getPreference(i);
            if (preference != null && (preferenceAdapterPosition = getPreferenceAdapterPosition(preference)) != -1 && (childAt = this.mRecyclerView.getChildAt(preferenceAdapterPosition)) != null) {
                arrayList.add(childAt);
            }
        }
        drawViews(arrayList);
    }

    private void drawViews(List<View> list) {
        int i = 0;
        while (i < list.size()) {
            boolean z = true;
            boolean z2 = i == 0;
            if (i != list.size() - 1) {
                z = false;
            }
            drawView(list.get(i), z2, z);
            i++;
        }
    }

    private void drawView(View view, boolean z, boolean z2) {
        if (view != null) {
            drawDrawable(view.getBackground(), z, z2);
        }
    }

    private void drawDrawable(Drawable drawable, boolean z, boolean z2) {
        if (drawable instanceof MaskTaggingDrawable) {
            MaskTaggingDrawable maskTaggingDrawable = (MaskTaggingDrawable) drawable;
            maskTaggingDrawable.setMaskEnabled(true);
            Paint paint = this.mClipPaint;
            int i = this.mMaskPaddingTop;
            int i2 = this.mMaskPaddingBottom;
            int i3 = this.mMaskPaddingStart;
            int i4 = this.mExtraHorizontalPadding;
            maskTaggingDrawable.setClipPaint(paint, i, i2, i3 + i4, this.mMaskPaddingEnd + i4, this.mMaskRadius);
            boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this.mRecyclerView);
            Pair leftAndRightWithRTL = getLeftAndRightWithRTL(this.mRecyclerView, zIsLayoutRtl);
            maskTaggingDrawable.setLeftRight(((Integer) leftAndRightWithRTL.first).intValue(), ((Integer) leftAndRightWithRTL.second).intValue(), zIsLayoutRtl);
            maskTaggingDrawable.updateDrawCorner(z, z2);
        }
    }
}
