package id.co.icg.reload.ui.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import fr.ganfra.materialspinner.MaterialSpinner;

public class MaterialSpinner_Roboto_Reguler extends MaterialSpinner{
    public MaterialSpinner_Roboto_Reguler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MaterialSpinner_Roboto_Reguler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaterialSpinner_Roboto_Reguler(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            setTypeface(tf);
        }
    }
}
