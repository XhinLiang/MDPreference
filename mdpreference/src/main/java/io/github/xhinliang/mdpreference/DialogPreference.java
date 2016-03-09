package io.github.xhinliang.mdpreference;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Window;
import android.view.WindowManager;

import com.rey.material.dialog.Dialog;

/**
 * Created by xhinliang on 16-2-23.
 * DialogPreference
 */
public abstract class DialogPreference extends Preference {

    protected CharSequence mDialogTitle;
    protected CharSequence mDialogMessage;
    protected CharSequence mPositiveButtonText;
    protected CharSequence mNegativeButtonText;

    protected Dialog mDialog;

    public DialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mDialogTitle = getTitle();
        mPositiveButtonText = context.getText(R.string.confirm);
        mNegativeButtonText = context.getText(R.string.cancel);
    }

    public DialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DialogPreference(Context context) {
        super(context);
        init(context);
    }

    public void setDialogTitle(CharSequence dialogTitle) {
        mDialogTitle = dialogTitle;
    }

    public void setDialogTitle(int dialogTitleResId) {
        setDialogTitle(getContext().getString(dialogTitleResId));
    }

    public CharSequence getDialogTitle() {
        return mDialogTitle;
    }

    public void setDialogMessage(CharSequence dialogMessage) {
        mDialogMessage = dialogMessage;
    }

    public CharSequence getDialogMessage() {
        return mDialogMessage;
    }

    public void setPositiveButtonText(CharSequence positiveButtonText) {
        mPositiveButtonText = positiveButtonText;
    }

    public void setPositiveButtonText(@StringRes int positiveButtonTextResId) {
        setPositiveButtonText(getContext().getString(positiveButtonTextResId));
    }

    public void setNegativeButtonText(CharSequence negativeButtonText) {
        mNegativeButtonText = negativeButtonText;
    }

    public void setNegativeButtonText(@StringRes int negativeButtonTextResId) {
        setNegativeButtonText(getContext().getString(negativeButtonTextResId));
    }

    @Override
    protected void onClick() {
        if (mDialog != null && mDialog.isShowing()) return;
        showDialog(null);
    }

    protected abstract void onShowDialog(Bundle state);

    private void showDialog(Bundle bundle){
        onShowDialog(bundle);
        if (needInputMethod()) {
            requestInputMethod(mDialog);
        }
    }

    protected boolean needInputMethod() {
        return false;
    }

    private void requestInputMethod(android.app.Dialog dialog) {
        Window window = dialog.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public Dialog getDialog() {
        return mDialog;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (mDialog == null || !mDialog.isShowing()) {
            return superState;
        }

        final DialogSavedState myState = new DialogSavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = mDialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(DialogSavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        DialogSavedState myState = (DialogSavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    protected static class DialogSavedState extends BaseSavedState {
        boolean isDialogShowing;
        Bundle dialogBundle;

        public DialogSavedState(Parcel source) {
            super(source);
            isDialogShowing = source.readInt() == 1;
            dialogBundle = source.readBundle();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isDialogShowing ? 1 : 0);
            dest.writeBundle(dialogBundle);
        }

        public DialogSavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<DialogSavedState> CREATOR =
                new Parcelable.Creator<DialogSavedState>() {
                    public DialogSavedState createFromParcel(Parcel in) {
                        return new DialogSavedState(in);
                    }

                    public DialogSavedState[] newArray(int size) {
                        return new DialogSavedState[size];
                    }
                };
    }

}
