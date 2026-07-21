package miuix.stretchablewidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.ViewProperty;

/* JADX INFO: loaded from: classes3.dex */
public class StretchableWidget extends LinearLayout {
    private static final String STATE_COLLAPSE = "end";
    private static final String STATE_EXPAND = "start";
    private View mButtonLine;
    private WidgetContainer mContainer;
    private Context mContext;
    private String mDetailMsgResId;
    protected TextView mDetailMsgText;
    protected int mHeight;
    private ImageView mIcon;
    private int mIconResId;
    private boolean mIsExpand;
    private View mLayout;
    private int mLayoutResId;
    private ImageView mStateImage;
    private StretchableWidgetStateChangedListener mStretchableWidgetStateChangedListener;
    private TextView mTitle;
    private String mTitleResId;
    private View mTopLine;
    private RelativeLayout mTopView;

    public interface StretchableWidgetStateChangedListener {
        void stateChanged(boolean z);
    }

    protected void afterSetView() {
    }

    protected void preSetView(Context context, AttributeSet attributeSet, int i) {
    }

    public StretchableWidget(Context context) {
        this(context, null);
    }

    public StretchableWidget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.stretchableWidgetStyle);
    }

    public StretchableWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHeight = 0;
        setOrientation(1);
        this.mContext = context;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.StretchableWidget, i, 0);
        this.mTitleResId = typedArrayObtainStyledAttributes.getString(R.styleable.StretchableWidget_title);
        this.mIconResId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.StretchableWidget_icon, 0);
        this.mLayoutResId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.StretchableWidget_layout, 0);
        this.mDetailMsgResId = typedArrayObtainStyledAttributes.getString(R.styleable.StretchableWidget_detail_message);
        this.mIsExpand = typedArrayObtainStyledAttributes.getBoolean(R.styleable.StretchableWidget_expand_state, false);
        init(context, attributeSet, i);
        typedArrayObtainStyledAttributes.recycle();
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        View viewInflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.miuix_stretchable_widget_layout, (ViewGroup) this, true);
        this.mTopView = (RelativeLayout) viewInflate.findViewById(R.id.top_view);
        this.mIcon = (ImageView) viewInflate.findViewById(R.id.icon);
        this.mTitle = (TextView) viewInflate.findViewById(R.id.start_text);
        this.mStateImage = (ImageView) viewInflate.findViewById(R.id.state_image);
        this.mDetailMsgText = (TextView) viewInflate.findViewById(R.id.detail_msg_text);
        this.mContainer = (WidgetContainer) viewInflate.findViewById(R.id.customize_container);
        this.mButtonLine = viewInflate.findViewById(R.id.button_line);
        this.mTopLine = viewInflate.findViewById(R.id.top_line);
        setTitle(this.mTitleResId);
        preSetView(this.mContext, attributeSet, i);
        setLayout(this.mLayoutResId);
        setIcon(this.mIconResId);
        setDetailMessage(this.mDetailMsgResId);
        setState(this.mIsExpand);
        this.mTopView.setOnClickListener(new View.OnClickListener() { // from class: miuix.stretchablewidget.StretchableWidget.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                StretchableWidget.this.stateChangeAnim();
                view.announceForAccessibility(StretchableWidget.this.getStateInfo());
            }
        });
        ViewCompat.setAccessibilityDelegate(this.mTopView, new AccessibilityDelegateCompat() { // from class: miuix.stretchablewidget.StretchableWidget.2
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setStateDescription(StretchableWidget.this.getStateInfo());
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setChecked(StretchableWidget.this.mIsExpand);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getStateInfo() {
        if (this.mIsExpand) {
            return getContext().getString(R.string.miuix_stretchablewidget_expanded);
        }
        return getContext().getString(R.string.miuix_stretchablewidget_collapsed);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stateChangeAnim() {
        this.mIsExpand = !this.mIsExpand;
        AnimSpecialConfig animSpecialConfig = (AnimSpecialConfig) new AnimSpecialConfig().setEase(-2, 1.0f, 0.2f);
        if (this.mIsExpand) {
            Folme.useValue(this.mContainer).to(STATE_EXPAND, new AnimConfig().setFromSpeed(0.0f).setSpecial(ViewProperty.ALPHA, animSpecialConfig));
            this.mStateImage.setBackgroundResource(R.drawable.miuix_stretchable_widget_state_expand);
            this.mTopLine.setVisibility(0);
            this.mButtonLine.setVisibility(0);
        } else {
            Folme.useValue(this.mContainer).to(STATE_COLLAPSE, new AnimConfig().setFromSpeed(0.0f).setSpecial(ViewProperty.ALPHA, animSpecialConfig));
            this.mStateImage.setBackgroundResource(R.drawable.miuix_stretchable_widget_state_collapse);
            this.mTopLine.setVisibility(8);
            this.mButtonLine.setVisibility(8);
        }
        StretchableWidgetStateChangedListener stretchableWidgetStateChangedListener = this.mStretchableWidgetStateChangedListener;
        if (stretchableWidgetStateChangedListener != null) {
            stretchableWidgetStateChangedListener.stateChanged(this.mIsExpand);
        }
    }

    public void setState(boolean z) {
        if (z) {
            this.mStateImage.setBackgroundResource(R.drawable.miuix_stretchable_widget_state_expand);
            this.mTopLine.setVisibility(0);
            this.mButtonLine.setVisibility(0);
        } else {
            this.mStateImage.setBackgroundResource(R.drawable.miuix_stretchable_widget_state_collapse);
            this.mTopLine.setVisibility(8);
            this.mButtonLine.setVisibility(8);
        }
        setContainerAmin(z);
    }

    private void setContainerAmin(boolean z) {
        IStateStyle iStateStyleUseValue = Folme.useValue(this.mContainer);
        String str = STATE_EXPAND;
        iStateStyleUseValue.setup(STATE_EXPAND).add("widgetHeight", this.mHeight).add((FloatProperty) ViewProperty.ALPHA, 1.0f).setup(STATE_COLLAPSE).add("widgetHeight", 0).add((FloatProperty) ViewProperty.ALPHA, 0.0f);
        IStateStyle iStateStyleUseValue2 = Folme.useValue(this.mContainer);
        if (!z) {
            str = STATE_COLLAPSE;
        }
        iStateStyleUseValue2.setTo(str);
    }

    public void setDetailMessage(CharSequence charSequence) {
        if (charSequence != null) {
            this.mDetailMsgText.setText(charSequence);
        }
    }

    public void setTitle(CharSequence charSequence) {
        if (charSequence != null) {
            this.mTitle.setText(charSequence);
        }
    }

    public void setIcon(int i) {
        if (i == 0) {
            return;
        }
        this.mIcon.setBackgroundResource(i);
    }

    public View setLayout(int i) {
        if (i == 0) {
            return null;
        }
        View viewInflaterView = inflaterView(i);
        setView(viewInflaterView);
        return viewInflaterView;
    }

    public View getLayout() {
        return this.mLayout;
    }

    public void setLayout(View view) {
        setView(view);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setView(View view) {
        if (view == 0) {
            return;
        }
        this.mLayout = view;
        if (view instanceof TextProvider) {
            ((TextProvider) view).setListener(new SyncDetailMessageListener() { // from class: miuix.stretchablewidget.StretchableWidget.3
                @Override // miuix.stretchablewidget.SyncDetailMessageListener
                public void syncMessage(String str) {
                    StretchableWidget.this.setDetailMessage(str);
                }
            });
        }
        if (this.mContainer.getChildCount() == 0) {
            this.mContainer.addView(view);
        } else {
            this.mContainer.removeAllViews();
            this.mContainer.addView(view);
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        this.mHeight = view.getMeasuredHeight();
        afterSetView();
        setContainerAmin(this.mIsExpand);
    }

    private View inflaterView(int i) {
        if (i == 0) {
            return null;
        }
        return ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(i, (ViewGroup) null);
    }

    public void setStateChangedListener(StretchableWidgetStateChangedListener stretchableWidgetStateChangedListener) {
        this.mStretchableWidgetStateChangedListener = stretchableWidgetStateChangedListener;
    }
}
