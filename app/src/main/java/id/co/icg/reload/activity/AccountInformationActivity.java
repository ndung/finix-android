package id.co.icg.reload.activity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import net.igenius.customcheckbox.CustomCheckBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.co.icg.reload.R;
import id.co.icg.reload.model.Tuple;
import id.co.icg.reload.ui.FixedHoloDatePickerDialog;
import id.co.icg.reload.util.CityRegencies;
import id.co.icg.reload.util.Provinces;

public class AccountInformationActivity extends BaseActivity{

    private EditText etName;
    private EditText etBod;
    private EditText etEmail;
    private CustomCheckBox cbMan;
    private CustomCheckBox cbWoman;
    private AutoCompleteTextView tvProvince;
    private AutoCompleteTextView tvCityRegency;

    private Calendar myCalendar = Calendar.getInstance();

    private static final String TAG = AccountInformationActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_information);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etBod = findViewById(R.id.et_bod);

        etBod.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        cbMan = findViewById(R.id.cb_man);
        cbMan.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if (isChecked){
                    cbWoman.setChecked(false);
                }
            }
        });

        cbWoman = findViewById(R.id.cb_woman);
        cbWoman.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if (isChecked){
                    cbMan.setChecked(false);
                }
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
        tvProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tuple province = (Tuple) parent.getItemAtPosition(position);
                tvCityRegency.setText("");
                List<Tuple> list = CityRegencies.getCityRegencies(province.getKey());
                cityRegencies.clear();
                for (Tuple tuple : list){
                    cityRegencies.add(tuple);
                }
                cityRegenciesAdapter.notifyDataSetChanged();
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
}
