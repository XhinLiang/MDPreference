package io.github.xhinliang.mdpreference;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ArrayRes;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.rey.material.dialog.SimpleDialog;

/**
 * Created by xhinliang on 16-2-23.
 * List
 */
public class ListPreference extends DialogPreference {

    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private String mValue;
    private String mFormat;
    private int mClickedDialogEntryIndex;
    private boolean mValueSet;

    public ListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public ListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ListPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.list_preference, defStyleAttr, defStyleRes);
        mEntries = a.getTextArray(R.styleable.list_preference_entry_arr);
        mEntryValues = a.getTextArray(R.styleable.list_preference_value_arr);
        mFormat = a.getString(R.styleable.list_preference_format_str);
        a.recycle();
    }

    @Override
    protected void onShowDialog(Bundle state) {
        if (mEntries == null || mEntryValues == null) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array.");
        }

        mClickedDialogEntryIndex = getValueIndex();
        com.rey.material.dialog.Dialog.Builder mBuilder = new SimpleDialog.Builder()
                .items(mEntries, mClickedDialogEntryIndex)
                .title(mDialogTitle)
                .positiveAction(mPositiveButtonText, new com.rey.material.dialog.Dialog.Action1() {
                    @Override
                    public void onAction(com.rey.material.dialog.Dialog dialog) {
                        SimpleDialog simpleDialog = (SimpleDialog) dialog;
                        mClickedDialogEntryIndex = simpleDialog.getSelectedIndex();
                        String value = mEntryValues[mClickedDialogEntryIndex].toString();
                        if (callChangeListener(value)) {
                            setValue(value);
                        }
                    }
                })
                .negativeAction(mNegativeButtonText, null);
        final Dialog dialog = mDialog = mBuilder.build(getContext());
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        dialog.show();
    }


    public void setEntries(CharSequence[] entries) {
        mEntries = entries;
    }

    public void setEntries(@ArrayRes int entriesResId) {
        setEntries(getContext().getResources().getTextArray(entriesResId));
    }

    public CharSequence[] getEntries() {
        return mEntries;
    }

    public void setEntryValues(CharSequence[] entryValues) {
        mEntryValues = entryValues;
    }

    public void setEntryValues(@ArrayRes int entryValuesResId) {
        setEntryValues(getContext().getResources().getTextArray(entryValuesResId));
    }

    public CharSequence[] getEntryValues() {
        return mEntryValues;
    }

    public void setValue(String value) {
        final boolean changed = !TextUtils.equals(mValue, value);
        if (changed || !mValueSet) {
            mValue = value;
            mValueSet = true;
            persistString(value);
            if (changed) {
                notifyChanged();
            }
        }
    }

    @Override
    public CharSequence getSummary() {
        final CharSequence entry = getEntry();
        if (mFormat == null || entry == null) {
            return super.getSummary();
        } else {
            return String.format(mFormat, entry);
        }
    }

    public String getValue() {
        return mValue;
    }

    public CharSequence getEntry() {
        int index = getValueIndex();
        return index >= 0 && mEntries != null ? mEntries[index] : null;
    }

    public int findIndexOfValue(String value) {
        if (value != null && mEntryValues != null) {
            for (int i = mEntryValues.length - 1; i >= 0; i--) {
                if (mEntryValues[i].equals(value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getValueIndex() {
        return findIndexOfValue(mValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedString(mValue) : (String) defaultValue);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.value = getValue();
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
        setValue(myState.value);
    }

    private static class SavedState extends BaseSavedState {
        String value;
        public SavedState(Parcel source) {
            super(source);
            value = source.readString();
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(value);
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
