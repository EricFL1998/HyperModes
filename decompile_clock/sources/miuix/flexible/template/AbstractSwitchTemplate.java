package miuix.flexible.template;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import miuix.flexible.R;
import miuix.flexible.template.level.FontLevelSupplier;
import miuix.flexible.template.level.LevelSupplier;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes2.dex */
public abstract class AbstractSwitchTemplate implements IHyperCellTemplate {
    protected Context mContext;
    private boolean mIsViewStub = true;
    private HyperCellLayout.LevelCallback mLevelCallback;
    private LevelSupplier mLevelSupplier;
    private ViewGroup mRoot;

    public abstract int getLayoutResId(int i);

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onConfigurationChanged(ViewGroup viewGroup, Configuration configuration) {
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onDetachedFromWindow(ViewGroup viewGroup) {
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onViewAdded(ViewGroup viewGroup, View view) {
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onViewRemoved(ViewGroup viewGroup, View view) {
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void init(ViewGroup viewGroup, Context context, AttributeSet attributeSet) {
        this.mContext = context;
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HyperCellLayout);
            int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArrayObtainStyledAttributes.getIndex(i);
                if (index == R.styleable.HyperCellLayout_viewStub) {
                    this.mIsViewStub = typedArrayObtainStyledAttributes.getBoolean(index, true);
                }
            }
            typedArrayObtainStyledAttributes.recycle();
        }
        this.mLevelSupplier = createLevelSupplier();
        this.mRoot = (ViewGroup) LayoutInflater.from(context).inflate(getLayoutResId(getLevel()), viewGroup, false);
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public int getLevel() {
        return this.mLevelSupplier.getLevel();
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public LevelSupplier createLevelSupplier() {
        return new FontLevelSupplier(this.mContext);
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void setLevelCallback(HyperCellLayout.LevelCallback levelCallback) {
        this.mLevelCallback = levelCallback;
        applyLevel();
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void applyLevel() {
        HyperCellLayout.LevelCallback levelCallback = this.mLevelCallback;
        if (levelCallback != null) {
            levelCallback.onLevelApply(getLevel(), new Object[0]);
        }
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onFinishInflate(ViewGroup viewGroup) {
        if (!this.mIsViewStub) {
            viewGroup.addView(this.mRoot);
        }
        applyLevel();
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onAttachedToWindow(ViewGroup viewGroup) {
        if (this.mIsViewStub) {
            replaceSelfWithView(this.mRoot, viewGroup, (ViewGroup) viewGroup.getParent());
        }
    }

    private void replaceSelfWithView(View view, ViewGroup viewGroup, ViewGroup viewGroup2) {
        int iIndexOfChild = viewGroup2.indexOfChild(viewGroup);
        viewGroup2.removeViewInLayout(viewGroup);
        ViewGroup.LayoutParams layoutParams = viewGroup.getLayoutParams();
        if (layoutParams != null) {
            viewGroup2.addView(view, iIndexOfChild, layoutParams);
        } else {
            viewGroup2.addView(view, iIndexOfChild);
        }
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public int[] onMeasure(ViewGroup viewGroup, int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int childCount = viewGroup.getChildCount();
        int paddingStart = (size - viewGroup.getPaddingStart()) - viewGroup.getPaddingEnd();
        int paddingTop = (size2 - viewGroup.getPaddingTop()) - viewGroup.getPaddingBottom();
        int iMax = 0;
        int iMax2 = 0;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = viewGroup.getChildAt(i3);
            childAt.measure(View.MeasureSpec.makeMeasureSpec(paddingStart, mode), View.MeasureSpec.makeMeasureSpec(paddingTop, mode2));
            iMax = Math.max(iMax, childAt.getMeasuredWidth());
            iMax2 = Math.max(iMax2, childAt.getMeasuredHeight());
        }
        if (mode != 1073741824) {
            size = iMax + viewGroup.getPaddingStart() + viewGroup.getPaddingEnd();
        }
        if (mode2 != 1073741824) {
            size2 = iMax2 + viewGroup.getPaddingTop() + viewGroup.getPaddingBottom();
        }
        return new int[]{size, size2};
    }

    @Override // miuix.flexible.template.IHyperCellTemplate
    public void onLayout(ViewGroup viewGroup, boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            int childCount = viewGroup.getChildCount();
            boolean z2 = viewGroup.getLayoutDirection() == 1;
            int i5 = i3 - i;
            for (int i6 = 0; i6 < childCount; i6++) {
                int paddingStart = viewGroup.getPaddingStart();
                int paddingTop = viewGroup.getPaddingTop();
                View childAt = viewGroup.getChildAt(i6);
                layoutChildView(childAt, z2, i5, paddingStart, paddingTop, paddingStart + childAt.getMeasuredWidth(), paddingTop + childAt.getMeasuredHeight());
            }
        }
    }

    private void layoutChildView(View view, boolean z, int i, int i2, int i3, int i4, int i5) {
        int i6 = z ? i - i4 : i2;
        if (z) {
            i4 = i - i2;
        }
        view.layout(i6, i3, i4, i5);
    }
}
