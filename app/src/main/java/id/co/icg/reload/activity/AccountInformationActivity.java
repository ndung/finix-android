package id.co.icg.reload.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.igenius.customcheckbox.CustomCheckBox;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.icg.reload.R;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.AuthService;
import id.co.icg.reload.model.ChatMessage;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.model.Tuple;
import id.co.icg.reload.ui.CircleTransformer;
import id.co.icg.reload.ui.FixedHoloDatePickerDialog;
import id.co.icg.reload.util.CityRegencies;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.PartUtils;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Provinces;
import id.co.icg.reload.util.Static;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AccountInformationActivity extends BaseActivity{

    private TextInputLayout tilName;
    private TextInputLayout tilKtp;
    private TextInputLayout tilBod;
    private TextInputLayout tilAddress;
    private TextInputLayout tilProvince;
    private TextInputLayout tilCityRegency;
    private TextInputLayout tilZipCode;
    private TextInputLayout tilEmail;
    private TextInputLayout tilWhatsappNo;
    private TextInputLayout tilDistrict;
    private TextInputLayout tilSubdistrict;
    private TextInputLayout tilReferralCode;
    private TextInputLayout tilH2hAccount;
    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etName;
    private EditText etKtp;
    private EditText etBod;
    private EditText etEmail;
    private EditText etZipcode;
    private EditText etAddress;
    private EditText etWhatsappNo;
    private EditText etReferralCode;
    private EditText etH2hAccount;
    private CustomCheckBox cbMan;
    private CustomCheckBox cbWoman;
    private AutoCompleteTextView tvProvince;
    private AutoCompleteTextView tvCityRegency;
    private AutoCompleteTextView tvDistrict;
    private AutoCompleteTextView tvSubdistrict;
    private CardView cvID;
    private ImageView ivID;
    private ImageView ivPP;
    private ImageView ivPlusID;
    private TextView tvUploadID;
    private TextView tvSave;

    private Calendar myCalendar = Calendar.getInstance();
    private AuthService authService;

    private static final String TAG = AccountInformationActivity.class.toString();

    Reseller user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_information);

        authService = ApiUtils.AuthService(this);
        user = Preferences.getUser(this);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        ivPP = findViewById(R.id.iv_pp);
        ivID = findViewById(R.id.iv_id);

        ivBack.setOnClickListener(v -> finish());

        tvTitle.setText("Verifikasi akun");

        etName = findViewById(R.id.et_name);
        etKtp = findViewById(R.id.et_ktp);
        etEmail = findViewById(R.id.et_email);
        etBod = findViewById(R.id.et_bod);
        etZipcode = findViewById(R.id.et_zip_code);
        etAddress = findViewById(R.id.et_address);
        etWhatsappNo = findViewById(R.id.et_whatsapp_no);
        etReferralCode = findViewById(R.id.et_referral_code);
        etH2hAccount = findViewById(R.id.et_h2h_account);

        tilName = findViewById(R.id.til_name);
        tilKtp = findViewById(R.id.til_ktp);
        tilBod = findViewById(R.id.til_bod);
        tilAddress = findViewById(R.id.til_address);
        tilProvince = findViewById(R.id.til_province);
        tilCityRegency = findViewById(R.id.til_city_regency);
        tilZipCode = findViewById(R.id.til_zip_code);
        tilEmail = findViewById(R.id.til_email);
        tilWhatsappNo = findViewById(R.id.til_whatsapp_no);
        tilDistrict = findViewById(R.id.til_district);
        tilSubdistrict = findViewById(R.id.til_subdistrict);
        tilReferralCode = findViewById(R.id.til_referral_code);
        tilH2hAccount = findViewById(R.id.til_h2h_account);

        etBod.setOnClickListener(v -> showDateDialog());

        cbMan = findViewById(R.id.cb_man);
        cbMan.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked){
                cbWoman.setChecked(false);
            }
        });

        cbWoman = findViewById(R.id.cb_woman);
        cbWoman.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked){
                cbMan.setChecked(false);
            }
        });

        tvProvince = findViewById(R.id.tv_province);
        tvCityRegency = findViewById(R.id.tv_city_regency);
        ArrayAdapter<Tuple> provincesAdapter = new ArrayAdapter<> (this, android.R.layout.simple_spinner_dropdown_item, Provinces.getList());
        final List<Tuple> cityRegencies = new ArrayList<>();
        final ArrayAdapter<Tuple> cityRegenciesAdapter = new ArrayAdapter<> (this, android.R.layout.simple_spinner_dropdown_item, cityRegencies);
        tvProvince.setThreshold(1);
        tvProvince.setAdapter(provincesAdapter);
        tvCityRegency.setThreshold(1);
        tvCityRegency.setAdapter(cityRegenciesAdapter);
        tvProvince.setOnItemClickListener((parent, view, position, id) -> {
            Tuple province = (Tuple) parent.getItemAtPosition(position);
            tvCityRegency.setText("");
            List<Tuple> list = CityRegencies.getCityRegencies(province.getKey());
            cityRegencies.clear();
            for (Tuple tuple : list){
                cityRegencies.add(tuple);
            }
            cityRegenciesAdapter.notifyDataSetChanged();
        });

        tvDistrict = findViewById(R.id.tv_district);
        tvSubdistrict = findViewById(R.id.tv_subdistrict);

        cvID = findViewById(R.id.cv_id);
        ivPlusID = findViewById(R.id.iv_plus_id);
        tvUploadID = findViewById(R.id.tv_upload_id);

        etName.setText(user.getName());
        etKtp.setText(user.getIdentityNumber());
        etEmail.setText(user.getEmail());

        String gender = user.getGender();
        if (gender.equalsIgnoreCase("L")){
            cbMan.setChecked(true);
            cbWoman.setChecked(false);
        }else if (gender.equalsIgnoreCase("P")){
            cbMan.setChecked(false);
            cbWoman.setChecked(true);
        }

        tvProvince.setText(user.getProvince());
        tvCityRegency.setText(user.getCity());
        String bod = user.getBirthDate();
        etBod.setText(bod.substring(0,4)
                .concat("-").concat(bod.substring(4,6))
                .concat("-").concat(bod.substring(6)));
        etZipcode.setText(user.getZipCode());
        etAddress.setText(user.getAddress());
        etWhatsappNo.setText(user.getMobileNumber());
        etReferralCode.setText(user.getUniqueCode());
        etH2hAccount.setText(user.getTelegramUsername());
        tvDistrict.setText(user.getDistrict());
        tvSubdistrict.setText(user.getSubdistrict());

        tvSave = findViewById(R.id.tv_save);
        tvSave.setOnClickListener(view -> save());

        if (user.getProfilePicture()!=null){
            Glide.with(AccountInformationActivity.this).load(Static.BASE_URL+"files/"+user.getProfilePicture())
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransformer(AccountInformationActivity.this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivPP);
        }

        if (user.getIdentityPhoto()!=null){
            Glide.with(getApplicationContext()).load(Static.BASE_URL+"files/"+user.getIdentityPhoto()).into(ivID);
            ivID.setVisibility(View.VISIBLE);
            ivPlusID.setVisibility(View.GONE);
            tvUploadID.setVisibility(View.GONE);
        }

        if (user.getVerified().equalsIgnoreCase("Y")){
            etKtp.setEnabled(false);
            cbMan.setEnabled(false);
            cbWoman.setEnabled(false);
            etBod.setEnabled(false);

        }else{
            ivPP.setOnClickListener(v -> changeImage(CODE_PP));
            ivID.setOnClickListener(v -> changeImage(CODE_ID));
            cvID.setOnClickListener(v -> changeImage(CODE_ID));
        }
    }

    private static final int CODE_PP = 3321;
    private static final int CODE_ID = 3322;

    private void changeImage(int type) {
        TedPermission.with(this)
                .setPermissionListener(
                        new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                String[] list = new String[]{"Camera", "Gallery"};
                                new MaterialDialog.Builder(AccountInformationActivity.this)
                                        .title("Pilih")
                                        .items(list)
                                        .itemsCallback((dialog, itemView, position, text) -> {
                                            if(position == 0){
                                                EasyImage.openCameraForImage(AccountInformationActivity.this, type);
                                            } else if(position == 1){
                                                EasyImage.openGallery(AccountInformationActivity.this, type);
                                            }
                                        }).show();
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {

                            }

                        })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
                .check();
    }

    File ppFile;
    File idFile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagesPicked(@NonNull List<File> list, EasyImage.ImageSource imageSource, int type) {
                switch (type) {
                    case CODE_PP:
                        if (list!=null && !list.isEmpty()) {
                            ppFile = list.get(0);
                            Glide.with(AccountInformationActivity.this).load(ppFile)
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .bitmapTransform(new CircleTransformer(AccountInformationActivity.this))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivPP);
                        }
                        break;
                    case CODE_ID:
                        if (list!=null && !list.isEmpty()) {
                            idFile = list.get(0);
                            Glide.with(getApplicationContext()).load(idFile).into(ivID);
                            ivID.setVisibility(View.VISIBLE);
                            ivPlusID.setVisibility(View.GONE);
                            tvUploadID.setVisibility(View.GONE);
                        }
                        break;
                }
            }


        });
    }

    private void showDateDialog(){
        if (Build.VERSION.SDK_INT >= 24) {
            new FixedHoloDatePickerDialog(this, onDateSetListener,
                    myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }else{
            new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    onDateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    private SimpleDateFormat bodFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private DatePickerDialog.OnDateSetListener onDateSetListener = new FixedHoloDatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etBod.setText(bodFormatter.format(myCalendar.getTime()));
        }

    };

    private void save(){
        tilName.setError("");
        tilKtp.setError("");
        tilBod.setError("");
        tilAddress.setError("");
        tilProvince.setError("");
        tilCityRegency.setError("");
        tilZipCode.setError("");
        tilEmail.setError("");
        tilWhatsappNo.setError("");
        tilDistrict.setError("");
        tilSubdistrict.setError("");
        tilReferralCode.setError("");
        tilH2hAccount.setError("");

        boolean bool = true;
        if (TextUtils.isEmpty(etName.getText())){
            tilName.setError("Nama harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(etAddress.getText())){
            tilAddress.setError("Alamat jalan harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(tvProvince.getText())){
            tilProvince.setError("Alamat provinsi harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(tvCityRegency.getText())){
            tilCityRegency.setError("Alamat kota/kabupaten harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(etZipcode.getText())){
            tilZipCode.setError("Alamat kode pos harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(etEmail.getText())){
            tilEmail.setError("Alamat email harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(etKtp.getText())){
            tilKtp.setError("No. KTP harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(tvDistrict.getText())){
            tilDistrict.setError("Alamat kecamatan harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(tvSubdistrict.getText())){
            tilSubdistrict.setError("Alamat kelurahan harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(etBod.getText())){
            tilBod.setError("Tanggal lahir harus diisi");
            bool = false;
        }
        if (TextUtils.isEmpty(etReferralCode.getText())){
            tilReferralCode.setError("Kode referral harus diisi");
            bool = false;
        }
        if (!cbMan.isChecked() && !cbWoman.isChecked()){
            showMessage("Jenis kelamin harus dipilih");
            bool = false;
        }
        if ((user.getProfilePicture()==null || user.getProfilePicture().equals("")) && ppFile==null){
            showMessage("Foto profil harus diganti dengan foto selfie");
            bool = false;
        }
        if ((user.getIdentityPhoto()==null || user.getIdentityPhoto().equals("")) && idFile==null){
            showMessage("Foto ktp harus di-upload");
            bool = false;
        }
        if (bool){
            Map<String, RequestBody> bodyMap = new HashMap<>();
            bodyMap.put("name", RequestBody.create(MediaType.parse("text/plain"),etName.getText().toString()));
            bodyMap.put("address", RequestBody.create(MediaType.parse("text/plain"),etAddress.getText().toString()));
            bodyMap.put("province", RequestBody.create(MediaType.parse("text/plain"),tvProvince.getText().toString()));
            bodyMap.put("city", RequestBody.create(MediaType.parse("text/plain"),tvCityRegency.getText().toString()));
            bodyMap.put("district", RequestBody.create(MediaType.parse("text/plain"),tvDistrict.getText().toString()));
            bodyMap.put("subdistrict", RequestBody.create(MediaType.parse("text/plain"),tvSubdistrict.getText().toString()));
            bodyMap.put("zipcode", RequestBody.create(MediaType.parse("text/plain"),etZipcode.getText().toString()));
            if (cbMan.isChecked()){
                bodyMap.put("gender", RequestBody.create(MediaType.parse("text/plain"),"L"));
            }else if (cbWoman.isChecked()){
                bodyMap.put("gender", RequestBody.create(MediaType.parse("text/plain"),"P"));
            }
            bodyMap.put("identityNo", RequestBody.create(MediaType.parse("text/plain"),etKtp.getText().toString()));
            bodyMap.put("email", RequestBody.create(MediaType.parse("text/plain"),etEmail.getText().toString()));
            bodyMap.put("mobileNumber", RequestBody.create(MediaType.parse("text/plain"),etWhatsappNo.getText().toString()));
            bodyMap.put("birthDate", RequestBody.create(MediaType.parse("text/plain"),etBod.getText().toString()));
            bodyMap.put("referralCode", RequestBody.create(MediaType.parse("text/plain"),etReferralCode.getText().toString()));
            bodyMap.put("h2hAccount", RequestBody.create(MediaType.parse("text/plain"),etH2hAccount.getText().toString()));

            MultipartBody.Part pp = null;
            MultipartBody.Part id = null;
            if (ppFile!=null) {
                pp = PartUtils.prepareFilePart("pp", ppFile);
            }
            if (idFile!=null) {
                id = PartUtils.prepareFilePart("id", idFile);
            }
            authService.updateAccount(bodyMap, pp, id).enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    dissmissPleaseWaitDialog();
                    try {
                        if (response.isSuccessful()) {

                            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                            Response body = response.body();
                            JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                            Reseller rs = gson.fromJson(jsonObject, Reseller.class);
                            Preferences.setUser(AccountInformationActivity.this, rs);
                            showMessage("Berhasil!");

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
    }
}
