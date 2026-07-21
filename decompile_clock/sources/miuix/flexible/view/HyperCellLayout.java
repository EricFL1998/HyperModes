package miuix.flexible.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.GravityCompat;
import miuix.flexible.R;
import miuix.flexible.template.IHyperCellTemplate;
import miuix.flexible.template.SimpleMarkTemplate;
import miuix.flexible.template.TemplateFactory;

/* JADX INFO: loaded from: classes2.dex */
public class HyperCellLayout extends ViewGroup {
    private IHyperCellTemplate mTemplate;

    public interface LevelCallback {
        void onLevelApply(int i, Object... objArr);
    }

    public HyperCellLayout(Context context) {
        super(context);
        init(context, null);
    }

    public HyperCellLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public HyperCellLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HyperCellLayout);
            int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArrayObtainStyledAttributes.getIndex(i);
                if (index == R.styleable.HyperCellLayout_template) {
                    this.mTemplate = createTemplate(context, typedArrayObtainStyledAttributes.getString(index), attributeSet);
                }
            }
            typedArrayObtainStyledAttributes.recycle();
        }
        if (this.mTemplate == null) {
            this.mTemplate = new SimpleMarkTemplate();
        }
        this.mTemplate.init(this, context, attributeSet);
    }

    public IHyperCellTemplate getTemplate() {
        return this.mTemplate;
    }

    public int getLevel() {
        return this.mTemplate.getLevel();
    }

    public void setLevelCallback(LevelCallback levelCallback) {
        this.mTemplate.setLevelCallback(levelCallback);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTemplate.onFinishInflate(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mTemplate.onAttachedToWindow(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mTemplate.onDetachedFromWindow(this);
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View view) {
        super.onViewAdded(view);
        this.mTemplate.onViewAdded(this, view);
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        this.mTemplate.onViewRemoved(this, view);
    }

    protected IHyperCellTemplate createTemplate(Context context, String str, AttributeSet attributeSet) {
        return TemplateFactory.get(context, str);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int[] iArrOnMeasure = this.mTemplate.onMeasure(this, i, i2);
        setMeasuredDimension(iArrOnMeasure[0], iArrOnMeasure[1]);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mTemplate.onLayout(this, z, i, i2, i3, i4);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mTemplate.onConfigurationChanged(this, configuration);
    }

    public <T extends View> T findViewByAreaId(int i) {
        if (i == -1) {
            return null;
        }
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            T t = (T) getChildAt(i2);
            ViewGroup.LayoutParams layoutParams = t.getLayoutParams();
            if ((layoutParams instanceof LayoutParams) && ((LayoutParams) layoutParams).getAreaId() == i) {
                return t;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public static final int ANIMATION_ALPHA = 4;
        public static final int ANIMATION_AUTO = 8;
        public static final int ANIMATION_TRANSLATE_X = 1;
        public static final int ANIMATION_TRANSLATE_Y = 2;
        public static final int CUSTOM_PARAM_GROUP_PRIORITY = 256;
        public static final int CUSTOM_PARAM_GROUP_WEIGHT = 1024;
        public static final int CUSTOM_PARAM_HEIGHT = 64;
        public static final int CUSTOM_PARAM_LAYOUT_GRAVITY = 1;
        public static final int CUSTOM_PARAM_MARGIN_BOTTOM = 16;
        public static final int CUSTOM_PARAM_MARGIN_END = 4;
        public static final int CUSTOM_PARAM_MARGIN_START = 2;
        public static final int CUSTOM_PARAM_MARGIN_TOP = 8;
        public static final int CUSTOM_PARAM_PRIORITY = 128;
        public static final int CUSTOM_PARAM_WEIGHT = 512;
        public static final int CUSTOM_PARAM_WIDTH = 32;
        private int animSpec;
        private int animationGravity;
        private float animationProgress;
        private int areaId;
        private int customParams;
        private int gravity;
        private int groupPriority;
        private float groupWeight;
        private boolean isAnimating;
        private int mark;
        private int order;
        private int priority;
        private float weight;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.gravity = 0;
            this.isAnimating = false;
            this.animSpec = 7;
            this.animationGravity = GravityCompat.START;
            this.customParams = 0;
            if (attributeSet != null) {
                TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.HyperCellLayout_Layout);
                int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
                for (int i = 0; i < indexCount; i++) {
                    int index = typedArrayObtainStyledAttributes.getIndex(i);
                    if (index == R.styleable.HyperCellLayout_Layout_mark) {
                        int i2 = typedArrayObtainStyledAttributes.getInt(index, 1);
                        this.mark = i2;
                        if (i2 < 1) {
                            throw new IllegalArgumentException("Layout Parameter 'mark' can not be smaller than 1");
                        }
                    } else if (index == R.styleable.HyperCellLayout_Layout_node_weight) {
                        this.weight = typedArrayObtainStyledAttributes.getFloat(index, 0.0f);
                    } else if (index == R.styleable.HyperCellLayout_Layout_group_weight) {
                        this.groupWeight = typedArrayObtainStyledAttributes.getFloat(index, 0.0f);
                    } else if (index == R.styleable.HyperCellLayout_Layout_android_layout_gravity) {
                        this.gravity = typedArrayObtainStyledAttributes.getInt(index, 0);
                    } else if (index == R.styleable.HyperCellLayout_Layout_node_order) {
                        this.order = typedArrayObtainStyledAttributes.getInt(index, 0);
                    } else if (index == R.styleable.HyperCellLayout_Layout_node_priority) {
                        this.priority = typedArrayObtainStyledAttributes.getInt(index, 0);
                    } else if (index == R.styleable.HyperCellLayout_Layout_group_priority) {
                        this.groupPriority = typedArrayObtainStyledAttributes.getInt(index, 0);
                    } else if (index == R.styleable.HyperCellLayout_Layout_area_id) {
                        this.areaId = typedArrayObtainStyledAttributes.getResourceId(index, 0);
                    } else if (index == R.styleable.HyperCellLayout_Layout_custom_params) {
                        this.customParams = typedArrayObtainStyledAttributes.getInt(index, 0);
                    }
                }
                typedArrayObtainStyledAttributes.recycle();
            }
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = 0;
            this.isAnimating = false;
            this.animSpec = 7;
            this.animationGravity = GravityCompat.START;
            this.customParams = 0;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = 0;
            this.isAnimating = false;
            this.animSpec = 7;
            this.animationGravity = GravityCompat.START;
            this.customParams = 0;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = 0;
            this.isAnimating = false;
            this.animSpec = 7;
            this.animationGravity = GravityCompat.START;
            this.customParams = 0;
        }

        public LayoutParams(int i, float f, float f2, int i2, int i3) {
            super(0, 0);
            this.isAnimating = false;
            this.animSpec = 7;
            this.animationGravity = GravityCompat.START;
            this.customParams = 0;
            this.mark = i;
            this.weight = f;
            this.groupWeight = f2;
            this.gravity = i2;
            this.order = i3;
        }

        public LayoutParams(int i) {
            super(0, 0);
            this.gravity = 0;
            this.isAnimating = false;
            this.animSpec = 7;
            this.animationGravity = GravityCompat.START;
            this.customParams = 0;
            this.areaId = i;
        }

        public int getMark() {
            return this.mark;
        }

        public float getWeight() {
            return this.weight;
        }

        public float getGroupWeight() {
            return this.groupWeight;
        }

        public int getGravity() {
            return this.gravity;
        }

        public int getOrder() {
            return this.order;
        }

        public int getAreaId() {
            return this.areaId;
        }

        public LayoutParams setMark(int i) {
            this.mark = i;
            return this;
        }

        public LayoutParams setWeight(float f) {
            this.weight = f;
            return this;
        }

        public LayoutParams setGroupWeight(float f) {
            this.groupWeight = f;
            return this;
        }

        public LayoutParams setGravity(int i) {
            this.gravity = i;
            return this;
        }

        public LayoutParams setOrder(int i) {
            this.order = i;
            return this;
        }

        public int getPriority() {
            return this.priority;
        }

        public LayoutParams setPriority(int i) {
            this.priority = i;
            return this;
        }

        public int getGroupPriority() {
            return this.groupPriority;
        }

        public LayoutParams setGroupPriority(int i) {
            this.groupPriority = i;
            return this;
        }

        public LayoutParams setAreaId(int i) {
            this.areaId = i;
            return this;
        }

        public boolean isAnimating() {
            return this.isAnimating;
        }

        public void setAnimating(boolean z) {
            this.isAnimating = z;
        }

        public float getAnimationProgress() {
            return this.animationProgress;
        }

        public void setAnimationProgress(float f) {
            this.animationProgress = f;
        }

        public int getAnimSpec() {
            return this.animSpec;
        }

        public LayoutParams setAnimSpec(int i) {
            this.animSpec = i;
            return this;
        }

        public int getAnimationGravity() {
            return this.animationGravity;
        }

        public LayoutParams setAnimationGravity(int i) {
            this.animationGravity = i;
            return this;
        }

        public int getCustomParams() {
            return this.customParams;
        }

        public LayoutParams setCustomParams(int i) {
            this.customParams = i;
            return this;
        }

        public LayoutParams setMargin(int i, int i2, int i3, int i4) {
            setMarginStart(i);
            setMarginEnd(i3);
            this.topMargin = i2;
            this.bottomMargin = i4;
            return this;
        }
    }
}
