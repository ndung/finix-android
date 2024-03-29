package id.co.icg.reload.ui.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TextViewSFProDisplayRegular extends android.support.v7.widget.AppCompatTextView {

    public TextViewSFProDisplayRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewSFProDisplayRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewSFProDisplayRegular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SF-Pro-Display-Regular.otf");
            setTypeface(tf);
        }
    }
}