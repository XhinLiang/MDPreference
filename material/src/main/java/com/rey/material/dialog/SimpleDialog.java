package com.rey.material.dialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ScrollView;

import com.rey.material.R;
import com.rey.material.drawable.BlankDrawable;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.ListView;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.TextView;

@SuppressWarnings("unused")
public class SimpleDialog extends Dialog {

    private TextView mMessage;
    private InternalScrollView mScrollView;
    private InternalListView mListView;
    private InternalAdapter mAdapter;

    private int mMessageTextAppearanceId;
    private int mMessageTextColor;

    private int mRadioButtonStyle;
    private int mCheckBoxStyle;
    private int mItemHeight;
    private int mItemTextAppearance;

    private int mMode;

    protected static final int MODE_NONE = 0;
    protected static final int MODE_MESSAGE = 1;
    protected static final int MODE_ITEMS = 2;
    protected static final int MODE_MULTI_ITEMS = 3;
    protected static final int MODE_CUSTOM = 4;

    /**
     * Interface definition for a callback to be invoked when the checked state of an item changed.
     */
    public interface OnSelectionChangedListener {
        /**
         * Called when the checked state of an item changed.
         *
         * @param index    The index of item.
         * @param selected The checked state.
         */
        void onSelectionChanged(int index, boolean selected);
    }

    private OnSelectionChangedListener mOnSelectionChangedListener;

    public SimpleDialog(Context context) {
        super(context, R.style.Material_App_Dialog_Simple_Light);
    }

    public SimpleDialog(Context context, int style) {
        super(context, style);
    }

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate() {
        messageTextAppearance(R.style.TextAppearance_AppCompat_Body1);
        itemHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        itemTextAppearance(R.style.TextAppearance_AppCompat_Body1);
    }

    @Override
    public Dialog applyStyle(int resId) {
        super.applyStyle(resId);

        if (resId == 0)
            return this;

        TypedArray a = getContext().obtainStyledAttributes(resId, R.styleable.SimpleDialog);
        int textAppearance = 0;
        int textColor = 0;
        boolean textColorDefined = false;

        for (int i = 0, count = a.getIndexCount(); i < count; i++) {
            int attr = a.getIndex(i);

            if (attr == R.styleable.SimpleDialog_di_messageTextAppearance)
                textAppearance = a.getResourceId(attr, 0);
            else if (attr == R.styleable.SimpleDialog_di_messageTextColor) {
                textColor = a.getColor(attr, 0);
                textColorDefined = true;
            } else if (attr == R.styleable.SimpleDialog_di_radioButtonStyle)
                radioButtonStyle(a.getResourceId(attr, 0));
            else if (attr == R.styleable.SimpleDialog_di_checkBoxStyle)
                checkBoxStyle(a.getResourceId(attr, 0));
            else if (attr == R.styleable.SimpleDialog_di_itemHeight)
                itemHeight(a.getDimensionPixelSize(attr, 0));
            else if (attr == R.styleable.SimpleDialog_di_itemTextAppearance)
                itemTextAppearance(a.getResourceId(attr, 0));
        }

        a.recycle();

        if (textAppearance != 0)
            messageTextAppearance(textAppearance);

        if (textColorDefined)
            messageTextColor(textColor);

        return this;
    }

    @Override
    public Dialog clearContent() {
        super.clearContent();
        mMode = MODE_NONE;
        return this;
    }

    @Override
    public Dialog title(CharSequence title) {
        boolean titleVisible = !TextUtils.isEmpty(title);
        contentMargin(mContentPadding, titleVisible ? 0 : mContentPadding, mContentPadding, 0);
        return super.title(title);
    }

    @Override
    public Dialog contentView(View v) {
        if (mScrollView == null)
            initScrollView();

        if (mScrollView.getChildAt(0) != v && v != null) {
            mScrollView.removeAllViews();
            mScrollView.addView(v);
            mMode = MODE_CUSTOM;
            super.contentView(mScrollView);
        }

        return this;
    }

    private void initScrollView() {
        mScrollView = new InternalScrollView(getContext());
        mScrollView.setPadding(0, 0, 0, mContentPadding - mActionPadding);
        mScrollView.setClipToPadding(false);
        mScrollView.setFillViewport(true);
        mScrollView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        ViewCompat.setLayoutDirection(mScrollView, ViewCompat.LAYOUT_DIRECTION_INHERIT);
    }

    private void initMessageView() {
        mMessage = new TextView(getContext());
        mMessage.setTextAppearance(mMessageTextAppearanceId);
        mMessage.setTextColor(mMessageTextColor);
        mMessage.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
    }

    /**
     * Set a message text to this SimpleDialog.
     *
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog message(CharSequence message) {
        if (mScrollView == null)
            initScrollView();

        if (mMessage == null)
            initMessageView();

        if (mScrollView.getChildAt(0) != mMessage) {
            mScrollView.removeAllViews();
            mScrollView.addView(mMessage);
        }

        mMessage.setText(message);
        if (!TextUtils.isEmpty(message)) {
            mMode = MODE_MESSAGE;
            super.contentView(mScrollView);
        }
        return this;
    }

    /**
     * Set a message text to this SimpleDialog.
     *
     * @param id The resourceId of text.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog message(int id) {
        return message(id == 0 ? null : getContext().getResources().getString(id));
    }

    /**
     * Sets the text color, size, style of the message view from the specified TextAppearance resource.
     *
     * @param resId The resourceId value.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog messageTextAppearance(int resId) {
        if (mMessageTextAppearanceId != resId) {
            mMessageTextAppearanceId = resId;
            if (mMessage != null)
                mMessage.setTextAppearance(mMessageTextAppearanceId);
        }
        return this;
    }

    /**
     * Sets the text color of the message view.
     *
     * @param color The color value.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog messageTextColor(int color) {
        if (mMessageTextColor != color) {
            mMessageTextColor = color;
            if (mMessage != null)
                mMessage.setTextColor(color);
        }
        return this;
    }

    /**
     * Sets the style of radio button.
     *
     * @param resId The resourceId of style.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog radioButtonStyle(int resId) {
        if (mRadioButtonStyle != resId) {
            mRadioButtonStyle = resId;
            if (mAdapter != null && mMode == MODE_ITEMS)
                mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * Sets the style of check box.
     *
     * @param resId The resourceId of style.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog checkBoxStyle(int resId) {
        if (mCheckBoxStyle != resId) {
            mCheckBoxStyle = resId;
            if (mAdapter != null && mMode == MODE_MULTI_ITEMS)
                mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * Sets the height of item
     *
     * @param height The size in pixels.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog itemHeight(int height) {
        if (mItemHeight != height) {
            mItemHeight = height;
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * Sets the text color, size, style of the item view from the specified TextAppearance resource.
     *
     * @param resId The resourceId value.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog itemTextAppearance(int resId) {
        if (mItemTextAppearance != resId) {
            mItemTextAppearance = resId;
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    private void initListView() {
        mListView = new InternalListView(getContext());
        mListView.setDividerHeight(0);
        mListView.setCacheColorHint(0x00000000);
        mListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mListView.setClipToPadding(false);
        mListView.setSelector(BlankDrawable.getInstance());
        mListView.setPadding(0, 0, 0, mContentPadding - mActionPadding);
        mListView.setVerticalFadingEdgeEnabled(false);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        ViewCompat.setLayoutDirection(mListView, ViewCompat.LAYOUT_DIRECTION_INHERIT);

        mAdapter = new InternalAdapter();
        mListView.setAdapter(mAdapter);
    }

    /**
     * Set the list of items in single-choice mode.
     *
     * @param items         The list of items.
     * @param selectedIndex The index of selected item.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog items(CharSequence[] items, int selectedIndex) {
        if (mListView == null)
            initListView();

        mMode = MODE_ITEMS;
        mAdapter.setItems(items, selectedIndex);
        super.contentView(mListView);
        return this;
    }

    /**
     * Set the list of items in multi-choice mode.
     *
     * @param items           The list of items.
     * @param selectedIndexes The indexes of selected items.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog multiChoiceItems(CharSequence[] items, int... selectedIndexes) {
        if (mListView == null)
            initListView();

        mMode = MODE_MULTI_ITEMS;
        mAdapter.setItems(items, selectedIndexes);
        super.contentView(mListView);
        return this;
    }

    /**
     * Set a listener will be called when the checked state of a item is changed.
     *
     * @param listener The {@link OnSelectionChangedListener} will be called.
     * @return The SimpleDialog for chaining methods.
     */
    public SimpleDialog onSelectionChangedListener(OnSelectionChangedListener listener) {
        mOnSelectionChangedListener = listener;
        return this;
    }

    /**
     * @return The list of index of all selected items.
     */
    public int[] getSelectedIndexes() {
        return mAdapter == null ? null : mAdapter.getSelectedIndexes();
    }

    /**
     * @return The list of value of all selected items.
     */
    public CharSequence[] getSelectedValues() {
        return mAdapter.getSelectedValues();
    }

    /**
     * @return The index of selected item.
     */
    public int getSelectedIndex() {
        return mAdapter == null ? -1 : mAdapter.getLastSelectedIndex();
    }

    /**
     * @return The value of selected item.
     */
    public CharSequence getSelectedValue() {
        return mAdapter.getLastSelectedValue();
    }

    private class InternalScrollView extends ScrollView {

        private boolean mIsRtl = false;

        public InternalScrollView(Context context) {
            super(context);
        }


        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void onRtlPropertiesChanged(int layoutDirection) {
            boolean rtl = layoutDirection == LAYOUT_DIRECTION_RTL;
            if (mIsRtl != rtl) {
                mIsRtl = rtl;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    View v = getChildAt(0);
                    if (v != null && v == mMessage)
                        mMessage.setTextDirection(mIsRtl ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
                }
                requestLayout();
            }
        }

        public boolean isLayoutRtl() {
            return mIsRtl;
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);

            View child = getChildAt(0);
            showDivider(child != null && child.getMeasuredHeight() > getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
        }
    }

    private class InternalListView extends ListView {

        private boolean mIsRtl = false;

        public InternalListView(Context context) {
            super(context);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void onRtlPropertiesChanged(int layoutDirection) {
            boolean rtl = layoutDirection == LAYOUT_DIRECTION_RTL;
            if (mIsRtl != rtl) {
                mIsRtl = rtl;
                requestLayout();
            }
        }

        public boolean isLayoutRtl() {
            return mIsRtl;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
            if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                if (mItemHeight != ViewGroup.LayoutParams.WRAP_CONTENT)
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mItemHeight * getAdapter().getCount() + getPaddingTop() + getPaddingBottom(), View.MeasureSpec.EXACTLY);
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);

            int totalHeight = 0;
            int childCount = getChildCount();

            for (int i = 0; i < childCount; i++)
                totalHeight += getChildAt(i).getMeasuredHeight();

            showDivider(totalHeight > getMeasuredHeight() || (totalHeight == getMeasuredHeight() && getAdapter().getCount() > childCount));
        }

    }

    private class InternalAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

        private CharSequence[] mItems;
        private boolean[] mSelected;
        private int mLastSelectedIndex;

        public void setItems(CharSequence[] items, int... selectedIndexes) {
            mItems = items;

            if (mSelected == null || mSelected.length != items.length)
                mSelected = new boolean[items.length];

            for (int i = 0; i < mSelected.length; i++)
                mSelected[i] = false;

            if (selectedIndexes != null)
                for (int index : selectedIndexes)
                    if (index >= 0 && index < mSelected.length) {
                        mSelected[index] = true;
                        mLastSelectedIndex = index;
                    }

            notifyDataSetChanged();
        }

        public int getLastSelectedIndex() {
            return mLastSelectedIndex;
        }

        public CharSequence getLastSelectedValue() {
            return mItems[mLastSelectedIndex];
        }

        public int[] getSelectedIndexes() {
            int count = 0;
            for (boolean aMSelected : mSelected)
                if (aMSelected)
                    count++;

            if (count == 0)
                return null;

            int[] result = new int[count];
            count = 0;
            for (int i = 0; i < mSelected.length; i++)
                if (mSelected[i]) {
                    result[count] = i;
                    count++;
                }

            return result;
        }

        public CharSequence[] getSelectedValues() {
            int count = 0;
            for (boolean aMSelected : mSelected)
                if (aMSelected)
                    count++;

            if (count == 0)
                return null;

            CharSequence[] result = new CharSequence[count];
            count = 0;
            for (int i = 0; i < mSelected.length; i++)
                if (mSelected[i]) {
                    result[count] = mItems[i];
                    count++;
                }

            return result;
        }

        @Override
        public int getCount() {
            return mItems == null ? 0 : mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems == null ? 0 : mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CompoundButton v = (CompoundButton) convertView;
            if (v == null) {
                if (mMode == MODE_MULTI_ITEMS) {
                    v = new CheckBox(parent.getContext());
                    v.applyStyle(mCheckBoxStyle);
                } else {
                    v = new RadioButton(parent.getContext());
                    v.applyStyle(mRadioButtonStyle);
                }
                if (mItemHeight != ViewGroup.LayoutParams.WRAP_CONTENT)
                    v.setMinHeight(mItemHeight);
                v.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    v.setTextDirection(((InternalListView) parent).isLayoutRtl() ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
                v.setTextAppearance(mItemTextAppearance);
                v.setCompoundDrawablePadding(mContentPadding);
            }
            v.setTag(position);
            v.setText(mItems[position]);
            if (v instanceof CheckBox)
                ((CheckBox) v).setCheckedImmediately(mSelected[position]);
            else
                ((RadioButton) v).setCheckedImmediately(mSelected[position]);
            v.setOnCheckedChangeListener(this);
            return v;
        }

        @Override
        public void onCheckedChanged(android.widget.CompoundButton v, boolean isChecked) {
            int position = (Integer) v.getTag();
            if (mSelected[position] != isChecked) {
                mSelected[position] = isChecked;
                if (mOnSelectionChangedListener != null)
                    mOnSelectionChangedListener.onSelectionChanged(position, mSelected[position]);
            }
            if (mMode == MODE_ITEMS && isChecked && mLastSelectedIndex != position) {
                mSelected[mLastSelectedIndex] = false;
                if (mOnSelectionChangedListener != null)
                    mOnSelectionChangedListener.onSelectionChanged(mLastSelectedIndex, false);
                CompoundButton child = (CompoundButton) mListView.getChildAt(mLastSelectedIndex - mListView.getFirstVisiblePosition());
                if (child != null)
                    child.setChecked(false);
                mLastSelectedIndex = position;
            }
        }
    }

    public static class Builder extends Dialog.Builder {

        protected int mMode;
        protected CharSequence mMessage;
        protected CharSequence[] mItems;
        protected int[] mSelectedIndexes;

        public Builder() {
            super(R.style.Material_App_Dialog_Simple_Light);
        }

        public Builder(int styleId) {
            super(styleId);
        }

        public Builder message(CharSequence message) {
            mMode = MODE_MESSAGE;
            mMessage = message;
            return this;
        }

        @Override
        protected void onBuildDone(Dialog dialog) {
            dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public Builder items(CharSequence[] items, int selectedIndex) {
            mMode = MODE_ITEMS;
            mItems = items;
            mSelectedIndexes = new int[]{selectedIndex};
            return this;
        }

        public Builder multiChoiceItems(CharSequence[] items, int... selectedIndexes) {
            mMode = MODE_MULTI_ITEMS;
            mItems = items;
            mSelectedIndexes = selectedIndexes;
            return this;
        }

        @Override
        protected Dialog onBuild(Context context, int styleId) {
            SimpleDialog dialog = new SimpleDialog(context, styleId);
            OnSelectionChangedListener listener = getListener(dialog);
            switch (mMode) {
                case MODE_MESSAGE:
                    dialog.message(mMessage);
                    break;
                case MODE_ITEMS:
                    dialog.items(mItems, mSelectedIndexes == null ? 0 : mSelectedIndexes[0]);
                    dialog.onSelectionChangedListener(listener);
                    break;
                case MODE_MULTI_ITEMS:
                    dialog.multiChoiceItems(mItems, mSelectedIndexes);
                    dialog.onSelectionChangedListener(listener);
                    break;
            }
            return dialog;
        }

        @NonNull
        private OnSelectionChangedListener getListener(final SimpleDialog dialog) {
            return new OnSelectionChangedListener() {
                @Override
                public void onSelectionChanged(int index, boolean selected) {
                    switch (mMode) {
                        case MODE_ITEMS:
                            if (selected) {
                                if (mSelectedIndexes == null)
                                    mSelectedIndexes = new int[]{index};
                                else
                                    mSelectedIndexes[0] = index;
                            }
                            break;
                        case MODE_MULTI_ITEMS:
                            mSelectedIndexes = dialog.getSelectedIndexes();
                            break;
                    }
                }
            };
        }


    }
}
