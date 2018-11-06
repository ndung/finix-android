package id.co.icg.reload.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;

import id.co.icg.reload.R;
import id.co.icg.reload.util.Global;
import id.co.icg.reload.util.Helper;
import id.co.icg.reload.util.HttpClientRequest;

import static android.Manifest.permission.READ_CONTACTS;

public class BaseActivity  extends AppCompatActivity {

    protected FingerprintManagerCompat mFingerprintManager;
    protected KeyStore mKeyStore;
    protected KeyGenerator mKeyGenerator;
    protected ProgressDialog mProgressDialog;

    private static final String TAG = BaseActivity.class.toString();

    private MaterialDialog materialDialogPleaseWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ToDo add your GUI initialization code here
        if (Global.PLATFORM_ID.equals("")) {
            Global.PLATFORM_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        mFingerprintManager = FingerprintManagerCompat.from(this);

        materialDialogPleaseWait = new MaterialDialog.Builder(BaseActivity.this)
                .content("Please wait...")
                .progress(true, 0)
                .build();
    }

    public void showMessage(String text) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        //View view = snackbar.getView();
        //view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        //TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        //textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private static final int CONTACT_PICKER_RESULT = 99;

    EditText editText;

    protected void pickContacts(EditText editText) {
        this.editText = editText;
        TedPermission.with(this)
                .setPermissionListener(
                        new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                                startActivityForResult(intent, CONTACT_PICKER_RESULT);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {

                            }

                        })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(READ_CONTACTS)
                .check();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                Cursor cursor = null;
                String phoneNumber = "";
                List<String> allNumbers = new ArrayList<String>();
                int phoneIdx = 0;
                try {
                    Uri result = data.getData();
                    String id = result.getLastPathSegment();
                    cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                    phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            phoneNumber = cursor.getString(phoneIdx);
                            allNumbers.add(phoneNumber);
                            cursor.moveToNext();
                        }
                    } else {
                        //no results actions
                    }
                } catch (Exception e) {
                    //error actions
                    Log.e("PICK_CONTACT", "err", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }

                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Choose a number");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            String selectedNumber = items[item].toString();
                            selectedNumber = selectedNumber.replaceAll("-", "").replaceAll(" ", "").trim();
                            if (selectedNumber.startsWith("62")) {
                                selectedNumber = selectedNumber.replaceFirst("62", "0");
                            } else if (selectedNumber.startsWith("+62")) {
                                selectedNumber = selectedNumber.replaceFirst("\\+62", "0");
                            } else if (selectedNumber.startsWith("8")) {
                                selectedNumber = "0".concat(selectedNumber);
                            }
                            editText.setText(selectedNumber);
                        }
                    });
                    AlertDialog alert = builder.create();
                    if (allNumbers.size() > 1) {
                        alert.show();
                    } else {
                        String selectedNumber = phoneNumber.toString();
                        selectedNumber = selectedNumber.replaceAll("-", "").replaceAll(" ", "").trim();
                        if (selectedNumber.startsWith("62")) {
                            selectedNumber = selectedNumber.replaceFirst("62", "0");
                        } else if (selectedNumber.startsWith("+62")) {
                            selectedNumber = selectedNumber.replaceFirst("\\+62", "0");
                        } else if (selectedNumber.startsWith("8")) {
                            selectedNumber = "0".concat(selectedNumber);
                        }
                        editText.setText(selectedNumber);
                    }

                    if (phoneNumber.length() == 0) {
                        showMessage("No phone numbers found");
                    }
                }
            }
        } else {
            //activity result error actions

        }
    }

    public void showPleaseWaitDialog(){
        materialDialogPleaseWait.show();
    }

    public void dissmissPleaseWaitDialog(){
        if(materialDialogPleaseWait != null){
            materialDialogPleaseWait.dismiss();
        }
    }

    protected class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showPleaseWaitDialog();
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                String[] arr = aurl[0].split("\\/");
                String name = arr[arr.length-1];
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/"
                        + name);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                showMessage("file is saved to " + Environment.getExternalStorageDirectory().getPath() + "/" + name);
            } catch (Exception e) {
                showMessage( "failed to save file: " + e.getMessage());
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);

        }

        @Override
        protected void onPostExecute(String unused) {
            dissmissPleaseWaitDialog();
        }
    }
}
