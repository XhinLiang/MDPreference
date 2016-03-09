package io.github.xhinliang.mdpreference;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ArrayRes;
import android.util.AttributeSet;

import com.rey.material.dialog.SimpleDialog;

import java.util.HashSet;
import java.util.Set;

public class MultiSelectListPreference extends DialogPreference {

    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private boolean mValueSet;
    private int selects;

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.list_preference, defStyleAttr, defStyleRes);
        mEntries = a.getTextArray(R.styleable.list_preference_entry_arr);
        mEntryValues = a.getTextArray(R.styleable.list_preference_value_arr);
        a.recycle();
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public MultiSelectListPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }


    @Override
    protected void onShowDialog(Bundle state) {
        if (mEntries == null || mEntryValues == null) {
            throw new IllegalStateException(
                    "MultiSelectListPreference requires an entries array and an entryValues array.");
        }
        com.rey.material.dialog.Dialog.Builder mBuilder = new SimpleDialog.Builder()
                .multiChoiceItems(mEntries, getIndexes())
                .title(mDialogTitle)
                .positiveAction(mPositiveButtonText, new com.rey.material.dialog.Dialog.Action1() {
                    @Override
                    public void onAction(com.rey.material.dialog.Dialog dialog) {
                        SimpleDialog simpleDialog = (SimpleDialog) dialog;
                        int[] indexes = simpleDialog.getSelectedIndexes();
                        if (callChangeListener(indexes)) {
                            setIndexes(indexes);
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

    public void setSelects(int indexes) {
        if (indexes != selects || !mValueSet) {
            selects = indexes;
            mValueSet = true;
            persistInt(selects);
            if (indexes != selects) {
                notifyChanged();
            }
        }
    }

    public void setIndexes(int[] indexes) {
        setSelects(getBit(indexes));
    }

    private int getBit(int[] indexes) {
        int selected = 0x0;
        for (int item : indexes) {
            int temp = 1;
            temp <<= item;
            selected |= temp;
        }
        return selected;
    }

    private Set<Integer> getArray(int bit) {
        Set<Integer> set = new HashSet<>();
        int temp = 1;
        for (int i = 0; i < 32; ++i) {
            if ((temp & bit) == temp) {
                set.add(i);
            }
            temp <<= 1;
        }
        return set;
    }


    public int getSelects() {
        return selects;
    }

    public Set<Integer> getSelectIndexes() {
        return getArray(getSelects());
    }

    public int[] getIndexes() {
        Set<Integer> set = getSelectIndexes();
        int[] result = new int[set.size()];
        Integer[] array = new Integer[set.size()];
        set.toArray(array);
        for (int i = 0; i < result.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }

    public Set<Integer> getValue() {
        return getArray(selects);
    }


    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setSelects(restoreValue ? getPersistedInt(0) : 0);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        super.onRestoreInstanceState(myState.getSuperState());
        myState.value = getSelects();
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
        setSelects(myState.value);
    }


    private static class SavedState extends BaseSavedState {
        int value;

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
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
