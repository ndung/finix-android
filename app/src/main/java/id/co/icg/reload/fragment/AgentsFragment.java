package id.co.icg.reload.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.activity.TransferBalanceAddAmountActivity;
import id.co.icg.reload.adapter.DownlineAdapter;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.AuthService;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.util.DialogUtils;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.READ_CONTACTS;
import static android.app.Activity.RESULT_OK;

public class AgentsFragment extends BaseFragment{

    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;

    private DownlineAdapter downlineAdapter;
    private RecyclerView rvDownlines;

    private AuthService authService;
    private List<Reseller> downlines;

    private TextView tvDownlines;
    private TextView tvActiveDownlines;
    private ImageView ivAdd;
    private ImageView ivSort;

    private Reseller rs;

    DialogUtils dialogUtils;

    private static final String TAG = AgentsFragment.class.toString();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agents, container, false);

        rs = Preferences.getUser(getActivity());

        downlines = new ArrayList<>();
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        ivAdd = view.findViewById(R.id.iv_add);
        ivSort = view.findViewById(R.id.iv_sort);

        tvDownlines = view.findViewById(R.id.tv_downlines);
        tvActiveDownlines = view.findViewById(R.id.tv_downlines_active);

        authService = ApiUtils.AuthService(getActivity());

        searchView = view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                downlineAdapter.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            searchView.onActionViewCollapsed();
            downlineAdapter.getFilter().filter("");
            return true;
        });

        rvDownlines = view.findViewById(R.id.rvDownlines);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvDownlines.setLayoutManager(layoutManager);
        rvDownlines.setItemAnimator(new DefaultItemAnimator());

        downlines = new ArrayList<>();
        downlineAdapter = new DownlineAdapter(getActivity(), downlines, new DownlineAdapter.OnItemSelectedListener() {
            @Override
            public void onTransfer(Reseller reseller) {
                transferBalance(reseller);
            }

            @Override
            public void onDelete(Reseller reseller) {
                removeDownline(reseller.getId());
            }
        });
        rvDownlines.setAdapter(downlineAdapter);

        getDownlines();

        refreshLayout.setOnRefreshListener(() -> getDownlines());

        ivAdd.setOnClickListener(v -> addDownlines());

        dialogUtils = new DialogUtils(getActivity());

        ivSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = dialogUtils.showDialog("Urut Berdasarkan", R.layout.dialog_sort_agent, true);

                final RadioGroup radioGroup = dialog.findViewById(R.id.rg_sort);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                    }
                });
            }
        });

        return view;
    }

    private EditText editText;

    private void addDownlines(){

        if (rs.getVerified().equals("Y")) {
            final Dialog dialog = dialogUtils.createDialog(R.layout.dialog_add_new_agent, true);
            Button button = dialog.findViewById(R.id.btn_add);
            final TextInputLayout textInputLayout = dialog.findViewById(R.id.til_mobile_number);
            editText = dialog.findViewById(R.id.et_mobile_number);
            editText.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        pickContacts(editText);
                        return true;
                    }
                }
                return false;
            });
            button.setOnClickListener(v -> {
                String phoneNumber = editText.getText().toString();
                boolean isPhoneNumberValid = !phoneNumber.equals("") && phoneNumber.length() >= 10 && phoneNumber.startsWith("0");
                if (!isPhoneNumberValid) {
                    textInputLayout.setError("Harap isi nomor telepon yang ingin didaftarkan");
                } else {
                    textInputLayout.setError("");
                    addDownline(dialog, phoneNumber);
                }
            });
            dialog.show();
        }else{
            final Dialog dialog = dialogUtils.createDialog(R.layout.dialog_account_not_verified, true);
            TextView tvDescription = dialog.findViewById(R.id.tv_description);
            tvDescription.setText("Untuk menambahkan downline baru, akun kamu harus diverifikasi terlebih dahulu");
            Button btnVerify = dialog.findViewById(R.id.btn_verify);
            btnVerify.setOnClickListener(v -> {
                dialog.dismiss();
                startAccountInformationActivity();
            });
            dialog.show();
        }
    }

    private void addDownline(final Dialog dialog, String mobileNumber){
        showPleaseWaitDialog();

        authService.addDownline(mobileNumber).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        refreshDownlines(response);
                        dialog.dismiss();
                    } else if (response.errorBody() != null) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string().trim());
                        showMessage(jObjError.getString("message"));
                    } else {
                        showMessage(Static.SOMETHING_WRONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                dissmissPleaseWaitDialog();
                showMessage(t.getMessage());
            }
        });
    }

    private void removeDownline(String mobileNumber){
        showPleaseWaitDialog();

        authService.removeDownline(mobileNumber).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        refreshDownlines(response);
                    } else if (response.errorBody() != null) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string().trim());
                        showMessage(jObjError.getString("message"));
                    } else {
                        showMessage(Static.SOMETHING_WRONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                dissmissPleaseWaitDialog();
                showMessage(t.getMessage());
            }
        });
    }

    private void getDownlines(){
        showPleaseWaitDialog();

        authService.getDownlines().enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                refreshLayout.setRefreshing(false);
                try {
                    if (response.isSuccessful()) {
                        refreshDownlines(response);
                    } else if (response.errorBody() != null) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string().trim());
                        showMessage(jObjError.getString("message"));
                    } else {
                        showMessage(Static.SOMETHING_WRONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                dissmissPleaseWaitDialog();
                showMessage(t.getMessage());
            }
        });
    }

    private void refreshDownlines(retrofit2.Response<Response> response){
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
        List<Reseller> list = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Reseller>>() {
        }.getType());
        downlines.clear();
        tvDownlines.setText(String.valueOf(list.size()));
        int active = 0;
        if (list!=null && list.size()>0){
            for (Reseller rs : list) {
                if (rs.getStatus()==1){
                    active = active+1;
                }
                downlines.add(rs);
            }
        }
        tvActiveDownlines.setText(String.valueOf(active));
        downlineAdapter.notifyDataSetChanged();
    }

    private void transferBalance(Reseller rs){
        Intent intent = new Intent(getActivity(), TransferBalanceAddAmountActivity.class);
        intent.putExtra("user", rs);
        startActivity(intent);
    }

    private static final int CONTACT_PICKER_RESULT = 99;

    protected void pickContacts(EditText editText) {
        this.editText = editText;
        TedPermission.with(getActivity())
                .setPermissionListener(
                        new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                                getActivity().startActivityForResult(intent, CONTACT_PICKER_RESULT);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {

                            }

                        })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(READ_CONTACTS)
                .check();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICKER_RESULT) {
                Cursor cursor = null;
                String phoneNumber = "";
                List<String> allNumbers = new ArrayList<String>();
                int phoneIdx = 0;
                try {
                    Uri result = data.getData();
                    String id = result.getLastPathSegment();
                    cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Choose a number");
                    builder.setItems(items, (dialog, item) -> {
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

}
