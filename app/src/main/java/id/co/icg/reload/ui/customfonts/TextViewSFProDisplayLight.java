package id.co.icg.reload.ui.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewSFProDisplayLight extends android.support.v7.widget.AppCompatTextView {

    public TextViewSFProDisplayLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewSFProDisplayLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewSFProDisplayLight(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SF-Pro-Display-Light.otf");
            setTypeface(tf);
        }
    }
}
