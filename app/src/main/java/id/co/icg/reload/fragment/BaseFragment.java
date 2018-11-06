package id.co.icg.reload.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import id.co.icg.reload.R;
import id.co.icg.reload.activity.AccountInformationActivity;

public class BaseFragment extends Fragment {

    private MaterialDialog materialDialogPleaseWait;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        materialDialogPleaseWait = new MaterialDialog.Builder(getActivity())
                .content("Please wait...")
                .progress(true, 0)
                .build();
    }

    public void showPleaseWaitDialog(){
        materialDialogPleaseWait.show();
    }

    public void dissmissPleaseWaitDialog(){
        if(materialDialogPleaseWait != null){
            materialDialogPleaseWait.dismiss();
        }
    }

    public void showMessage(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    protected void startAccountInformationActivity(){
        Intent intent = new Intent(getActivity(), AccountInformationActivity.class);
        startActivity(intent);
    }
}
