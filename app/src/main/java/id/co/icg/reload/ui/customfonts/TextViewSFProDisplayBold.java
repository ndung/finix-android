package id.co.icg.reload.ui.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewSFProDisplayBold extends android.support.v7.widget.AppCompatTextView {

    public TextViewSFProDisplayBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewSFProDisplayBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewSFProDisplayBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SF-Pro-Display-Bold.otf");
            setTypeface(tf);
        }
    }
}

