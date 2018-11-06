package id.co.icg.reload.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class DialogUtils {
    Context context;

    public DialogUtils(Context context) {
        this.context = context;
    }

    public Dialog createDialog(int id, boolean cancelable){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(id);
        dialog.setCancelable(cancelable);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public Dialog showDialog(int id, boolean cancelable){
        Dialog dialog = createDialog(id, cancelable);
        dialog.show();

        return dialog;
    }

}
