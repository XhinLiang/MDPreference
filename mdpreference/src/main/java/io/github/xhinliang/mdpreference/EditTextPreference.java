package io.github.xhinliang.mdpreference;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.rey.material.dialog.SimpleDialog;
import com.rey.material.widget.EditText;

/**
 * Created by xhinliang on 16-2-29.
 * sf
 */
public class EditTextPreference extends DialogPreference {
    public EditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public EditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreference(Context context) {
        super(context);
    }


    private String mText;

    public void setText(String text) {
        final boolean wasBlocking = shouldDisableDependents();
        mText = text;
        persistString(text);
        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
        notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
        return mText == null ? super.getSummary() : mText;
    }

    public String getText() {
        return mText;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setText(restoreValue ? getPersistedString(mText) : (String) defaultValue);
    }

    @Override
    protected void onShowDialog(Bundle state) {
        com.rey.material.dialog.Dialog.Builder mBuilder = new SimpleDialog.Builder()
                .title(mDialogTitle)
                .contentView(R.layout.mp_edittext)
                .positiveAction(mPositiveButtonText, new com.rey.material.dialog.Dialog.Action1() {
                    @Override
                    public void onAction(com.rey.material.dialog.Dialog dialog) {
                        String value = ((com.rey.material.widget.EditText) dialog.findViewById(R.id.custom_et)).getText().toString();
                        if (callChangeListener(value)) {
                            setText(value);
                        }
                    }
                })
                .negativeAction(mNegativeButtonText, null);

        final Dialog dialog = mDialog = mBuilder.build(getContext());
        EditText editText = (com.rey.material.widget.EditText) dialog.findViewById(R.id.custom_et);
        editText.setText(getSummary());
        editText.setSelection(getSummary().length());
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        dialog.show();
    }

    @Override
    public boolean shouldDisableDependents() {
        return TextUtils.isEmpty(mText) || super.shouldDisableDependents();
    }

    @Override
    protected boolean needInputMethod() {
        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.text = getText();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setText(myState.text);
    }


    private static class SavedState extends BaseSavedState {
        String text;

        public SavedState(Parcel source) {
            super(source);
            text = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(text);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

    }
}
