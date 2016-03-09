package io.github.xhinliang.mdpreference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Preference extends android.preference.Preference {

    protected TextView titleText;
    protected TextView summaryText;

    protected ImageView imageView;
    protected View imageFrame;

    private int iconResId;
    private Drawable icon;

    public Preference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public Preference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public Preference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(LOLLIPOP)
    public Preference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context
                .obtainStyledAttributes(attrs, new int[]{android.R.attr.icon}, defStyleAttr, defStyleRes);
        iconResId = typedArray.getResourceId(0, 0);
        typedArray.recycle();
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.mp_preference, parent, false);

        ViewGroup widgetFrame = (ViewGroup) layout.findViewById(R.id.widget_frame);
        int widgetLayoutResId = getWidgetLayoutResource();
        if (widgetLayoutResId != 0) {
            layoutInflater.inflate(widgetLayoutResId, widgetFrame);
        }
        widgetFrame.setVisibility(widgetLayoutResId != 0 ? VISIBLE : GONE);

        return layout;
    }

    @Override
    protected void onBindView(View view) {
        CharSequence title = getTitle();
        titleText = (TextView) view.findViewById(R.id.title);
        titleText.setText(title);
        titleText.setVisibility(!isEmpty(title) ? VISIBLE : GONE);
        titleText.setTypeface(Typefaces.getRobotoRegular(getContext()));

        CharSequence summary = getSummary();
        summaryText = (TextView) view.findViewById(R.id.summary);
        summaryText.setText(summary);
        summaryText.setVisibility(!isEmpty(summary) ? VISIBLE : GONE);
        summaryText.setTypeface(Typefaces.getRobotoRegular(getContext()));

        if (icon == null && iconResId > 0) {
            icon = getContext().getResources().getDrawable(iconResId);
        }
        imageView = (ImageView) view.findViewById(R.id.icon);
        imageView.setImageDrawable(icon);
        imageView.setVisibility(icon != null ? VISIBLE : GONE);

        imageFrame = view.findViewById(R.id.icon_frame);
        imageFrame.setVisibility(icon != null ? VISIBLE : GONE);
    }

    @Override
    public void setIcon(int iconResId) {
        super.setIcon(iconResId);
        this.iconResId = iconResId;
    }

    @Override
    public void setIcon(Drawable icon) {
        super.setIcon(icon);
        this.icon = icon;
    }
}
