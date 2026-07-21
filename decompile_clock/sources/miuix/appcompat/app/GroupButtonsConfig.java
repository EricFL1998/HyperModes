package miuix.appcompat.app;

import android.text.TextUtils;
import android.view.View;
import miuix.appcompat.widget.Button;

/* JADX INFO: loaded from: classes2.dex */
public class GroupButtonsConfig {
    private Button mPrimaryButton;
    private Button mSecondaryButton;
    private Button mTertiaryButton;
    private int mOrientation = 1;
    private CharSequence mPrimaryButtonText = "";
    private int mPrimaryButtonVisibility = 8;
    private boolean mPrimaryButtonEnableState = true;
    private View.OnClickListener mOnPrimaryButtonClickListener = null;
    private View.OnLongClickListener mOnPrimaryButtonLongClickListener = null;
    private CharSequence mSecondaryButtonText = "";
    private int mSecondaryButtonVisibility = 8;
    private boolean mSecondaryButtonEnableState = true;
    private View.OnClickListener mOnSecondaryButtonClickListener = null;
    private View.OnLongClickListener mOnSecondaryButtonLongClickListener = null;
    private CharSequence mTertiaryButtonText = "";
    private int mTertiaryButtonVisibility = 8;
    private boolean mTertiaryButtonEnableState = true;
    private View.OnClickListener mOnTertiaryButtonClickListener = null;
    private View.OnLongClickListener mOnTertiaryButtonLongClickListener = null;

    public static class ButtonLayoutType {
        public static final int HORIZONTAL = 0;
        public static final int VERTICAL = 1;
    }

    public static class ButtonType {
        public static final int PRIMARY = 0;
        public static final int SECONDARY = 1;
        public static final int TERTIARY = 2;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public Button getPrimaryButton() {
        return this.mPrimaryButton;
    }

    public Button getSecondaryButton() {
        return this.mSecondaryButton;
    }

    public Button getTertiaryButton() {
        return this.mTertiaryButton;
    }

    public void setButtonVisible(int i, boolean z) {
        int i2 = z ? 0 : 8;
        if (i == 0) {
            if (this.mPrimaryButtonVisibility != i2) {
                this.mPrimaryButtonVisibility = i2;
                this.mPrimaryButton.setVisibility(i2);
                return;
            }
            return;
        }
        if (i == 1) {
            if (this.mSecondaryButtonVisibility != i2) {
                this.mSecondaryButtonVisibility = i2;
                this.mSecondaryButton.setVisibility(i2);
                return;
            }
            return;
        }
        if (i == 2 && this.mTertiaryButtonVisibility != i2) {
            this.mTertiaryButtonVisibility = i2;
            this.mTertiaryButton.setVisibility(i2);
        }
    }

    public void setButtonEnabled(int i, boolean z) {
        if (i == 0) {
            if (this.mPrimaryButtonEnableState != z) {
                this.mPrimaryButtonEnableState = z;
                this.mPrimaryButton.setEnabled(z);
                return;
            }
            return;
        }
        if (i == 1) {
            if (this.mSecondaryButtonEnableState != z) {
                this.mSecondaryButtonEnableState = z;
                this.mSecondaryButton.setEnabled(z);
                return;
            }
            return;
        }
        if (i == 2 && this.mTertiaryButtonEnableState != z) {
            this.mTertiaryButtonEnableState = z;
            this.mTertiaryButton.setEnabled(z);
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x002c  */
    public void updateText(int i, CharSequence charSequence) {
        Button button;
        if (i != 0) {
            if (i == 1) {
                if (TextUtils.equals(this.mSecondaryButtonText, charSequence)) {
                    button = null;
                } else {
                    this.mSecondaryButtonText = charSequence;
                    button = this.mSecondaryButton;
                }
            } else if (TextUtils.equals(this.mTertiaryButtonText, charSequence)) {
                button = null;
            } else {
                this.mTertiaryButtonText = charSequence;
                button = this.mTertiaryButton;
            }
        } else if (TextUtils.equals(this.mPrimaryButtonText, charSequence)) {
            button = null;
        } else {
            this.mPrimaryButtonText = charSequence;
            button = this.mPrimaryButton;
        }
        if (button != null) {
            button.setText(charSequence);
            button.requestLayout();
        }
    }

    public void updateOnClickListener(int i, View.OnClickListener onClickListener) {
        if (i == 0) {
            if (this.mOnPrimaryButtonClickListener != onClickListener) {
                this.mOnPrimaryButtonClickListener = onClickListener;
                this.mPrimaryButton.setOnClickListener(onClickListener);
                return;
            }
            return;
        }
        if (i == 1) {
            if (this.mOnSecondaryButtonClickListener != onClickListener) {
                this.mOnSecondaryButtonClickListener = onClickListener;
                this.mSecondaryButton.setOnClickListener(onClickListener);
                return;
            }
            return;
        }
        if (i == 2 && this.mOnTertiaryButtonClickListener != onClickListener) {
            this.mOnTertiaryButtonClickListener = onClickListener;
            this.mTertiaryButton.setOnClickListener(onClickListener);
        }
    }

    public void updateOnLongClickListener(int i, View.OnLongClickListener onLongClickListener) {
        if (i == 0) {
            if (this.mOnPrimaryButtonLongClickListener != onLongClickListener) {
                this.mOnPrimaryButtonLongClickListener = onLongClickListener;
                this.mPrimaryButton.setOnLongClickListener(onLongClickListener);
                return;
            }
            return;
        }
        if (i == 1) {
            if (this.mOnSecondaryButtonLongClickListener != onLongClickListener) {
                this.mOnSecondaryButtonLongClickListener = onLongClickListener;
                this.mSecondaryButton.setOnLongClickListener(onLongClickListener);
                return;
            }
            return;
        }
        if (i == 2 && this.mOnTertiaryButtonLongClickListener != onLongClickListener) {
            this.mOnTertiaryButtonLongClickListener = onLongClickListener;
            this.mTertiaryButton.setOnLongClickListener(onLongClickListener);
        }
    }

    public void updateContentDescription(int i, CharSequence charSequence) {
        if (i == 0) {
            if (TextUtils.equals(this.mPrimaryButton.getContentDescription(), charSequence)) {
                return;
            }
            this.mPrimaryButton.setContentDescription(charSequence);
        } else if (i == 1) {
            if (TextUtils.equals(this.mSecondaryButton.getContentDescription(), charSequence)) {
                return;
            }
            this.mSecondaryButton.setContentDescription(charSequence);
        } else if (i == 2 && !TextUtils.equals(this.mTertiaryButton.getContentDescription(), charSequence)) {
            this.mTertiaryButton.setContentDescription(charSequence);
        }
    }

    public void initButton(int i, Button button) {
        if (i == 0) {
            button.setText(this.mPrimaryButtonText);
            button.setOnClickListener(this.mOnPrimaryButtonClickListener);
            button.setOnLongClickListener(this.mOnPrimaryButtonLongClickListener);
            button.setEnabled(this.mPrimaryButtonEnableState);
            button.setVisibility(this.mPrimaryButtonVisibility);
            this.mPrimaryButton = button;
            return;
        }
        if (i == 1) {
            button.setText(this.mSecondaryButtonText);
            button.setOnClickListener(this.mOnSecondaryButtonClickListener);
            button.setOnLongClickListener(this.mOnSecondaryButtonLongClickListener);
            button.setEnabled(this.mSecondaryButtonEnableState);
            button.setVisibility(this.mSecondaryButtonVisibility);
            this.mSecondaryButton = button;
            return;
        }
        if (i != 2) {
            return;
        }
        button.setText(this.mTertiaryButtonText);
        button.setOnClickListener(this.mOnTertiaryButtonClickListener);
        button.setOnLongClickListener(this.mOnTertiaryButtonLongClickListener);
        button.setEnabled(this.mTertiaryButtonEnableState);
        button.setVisibility(this.mTertiaryButtonVisibility);
        this.mTertiaryButton = button;
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final GroupButtonsConfig mConfig = new GroupButtonsConfig();

        public Builder setOrientation(int i) {
            if (i == 1 || i == 0) {
                this.mConfig.mOrientation = i;
            }
            return this;
        }

        public Builder setButton(int i, CharSequence charSequence) {
            return setButton(i, charSequence, null, null);
        }

        public Builder setButton(int i, CharSequence charSequence, View.OnClickListener onClickListener) {
            return setButton(i, charSequence, onClickListener, null);
        }

        public Builder setButton(int i, CharSequence charSequence, View.OnLongClickListener onLongClickListener) {
            return setButton(i, charSequence, null, onLongClickListener);
        }

        public Builder setButton(int i, CharSequence charSequence, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
            return setButton(i, charSequence, onClickListener, onLongClickListener, true);
        }

        public Builder setButton(int i, CharSequence charSequence, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, boolean z) {
            return setButton(i, charSequence, onClickListener, onLongClickListener, z, true);
        }

        public Builder setButton(int i, CharSequence charSequence, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, boolean z, boolean z2) {
            int i2 = z2 ? 0 : 8;
            if (i == 0) {
                this.mConfig.mPrimaryButtonText = charSequence;
                this.mConfig.mOnPrimaryButtonClickListener = onClickListener;
                this.mConfig.mOnPrimaryButtonLongClickListener = onLongClickListener;
                this.mConfig.mPrimaryButtonEnableState = z;
                this.mConfig.mPrimaryButtonVisibility = i2;
            } else if (i == 1) {
                this.mConfig.mSecondaryButtonText = charSequence;
                this.mConfig.mOnSecondaryButtonClickListener = onClickListener;
                this.mConfig.mOnSecondaryButtonLongClickListener = onLongClickListener;
                this.mConfig.mSecondaryButtonEnableState = z;
                this.mConfig.mSecondaryButtonVisibility = i2;
            } else if (i == 2) {
                this.mConfig.mTertiaryButtonText = charSequence;
                this.mConfig.mOnTertiaryButtonClickListener = onClickListener;
                this.mConfig.mOnTertiaryButtonLongClickListener = onLongClickListener;
                this.mConfig.mTertiaryButtonEnableState = z;
                this.mConfig.mTertiaryButtonVisibility = i2;
            }
            return this;
        }

        public GroupButtonsConfig build() {
            return this.mConfig;
        }
    }
}
