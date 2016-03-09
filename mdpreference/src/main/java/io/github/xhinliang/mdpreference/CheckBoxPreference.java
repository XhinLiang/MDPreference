package io.github.xhinliang.mdpreference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.rey.material.widget.CheckBox;

public class CheckBoxPreference extends TwoStatePreference {
    public CheckBoxPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public CheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super.init(context, attrs, defStyleAttr, defStyleRes);
        setWidgetLayoutResource(R.layout.mp_checkbox_preference);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkable);
        checkBox.setCheckedImmediately(isChecked());
        syncSummaryView();
    }
}
