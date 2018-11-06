package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.ganfra.materialspinner.MaterialSpinner;
import id.co.icg.reload.R;
import id.co.icg.reload.adapter.ProductAdapter;
import id.co.icg.reload.model.Product;
import id.co.icg.reload.util.Global;

public class MobileRechargeInquiryActivity extends BaseActivity {

    private static final String TAG = MobileRechargeInquiryActivity.class.toString();

    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etMobileNumber;
    private RecyclerView rvProducts;
    private MaterialSpinner spinner;
    private TextInputLayout tilMobileNumber;

    private List<Product> list;
    private ProductAdapter adapter;
    private Map<String, List<Product>> billerProductsMap;

    private Map<String, String> prefixBillerMap;
    private String currentBillerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mobile_recharge_inquiry);

        prefixBillerMap = new TreeMap<>();
        for (String prefix : Global.tselPrefixes) {
            prefixBillerMap.put(prefix, "001");
        }
        for (String prefix : Global.isatPrefixes) {
            prefixBillerMap.put(prefix, "013");
        }
        for (String prefix : Global.xlPrefixes) {
            prefixBillerMap.put(prefix, "003");
        }
        for (String prefix : Global.threePrefixes) {
            prefixBillerMap.put(prefix, "018");
        }
        for (String prefix : Global.smartfrenPrefixes) {
            prefixBillerMap.put(prefix, "005");
        }
        for (String prefix : Global.boltPrefixes) {
            prefixBillerMap.put(prefix, "048");
        }

        tilMobileNumber = findViewById(R.id.til_mobile_number);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        etMobileNumber = findViewById(R.id.et_mobile_number);

        etMobileNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etMobileNumber.getRight() - etMobileNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        pickContacts(etMobileNumber);
                        return true;
                    }
                }
                return false;
            }
        });

        spinner = findViewById(R.id.spinner);
        List<String> strings = Arrays.asList("1","2","3","4","5","6");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(1);

        billerProductsMap = new TreeMap<>();
        List<Product> productList = (List<Product>) getIntent().getSerializableExtra("products");
        Log.d(TAG, "productList:"+productList);
        for (Product product : productList){
            List<Product> products = billerProductsMap.get(product.getBillerId());
            if (products==null){
                products = new ArrayList<>();
            }
            products.add(product);
            billerProductsMap.put(product.getBillerId(), products);
        }
        Log.d(TAG, "billerProductsMap:"+billerProductsMap);

        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 4 && s.toString().startsWith("0")) {
                    if (s.toString().length()>=10){
                        tilMobileNumber.setError("");
                    }
                    String prefix = s.toString().substring(1, 4);
                    String billerId = prefixBillerMap.get(prefix);
                    if (billerId==null || !billerId.equals(currentBillerId)) {
                        list.clear();
                        if (billerId!=null) {
                            currentBillerId = billerId;
                            List<Product> productList = billerProductsMap.get(billerId);
                            if (productList != null) {
                                for (Product product : productList) {
                                    list.add(product);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }else{
                            currentBillerId = "";
                        }
                    }
                }
            }
        });
        rvProducts = findViewById(R.id.rv_products);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTitle.setText(R.string.mobile_reguler_recharge);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvProducts.setLayoutManager(layoutManager);
        rvProducts.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();

        adapter = new ProductAdapter(this, list, new ProductAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(Product model) {
                return confirm(etMobileNumber.getText().toString(), model);
            }
        });
        rvProducts.setAdapter(adapter);
    }

    private boolean confirm(String phoneNumber, Product product) {
        boolean isPhoneNumberValid = !phoneNumber.equals("") && phoneNumber.length()>=10 && phoneNumber.startsWith("0");
        String noTrx = "1";
        if (spinner.getSelectedItem()!=null){
            noTrx = spinner.getSelectedItem().toString();
        }
        if (isPhoneNumberValid){
            tilMobileNumber.setError("");
            List<Product> productList = new ArrayList<>(list);
            list.clear();
            for (Product p : productList){
                if (!p.getId().equals(product.getId())){
                    p.setSelected(false);
                }else {
                    p.setSelected(true);
                }
                list.add(p);
            }
            adapter.notifyDataSetChanged();
            Intent intent = new Intent(this, MobileRechargeConfirmationActivity.class);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("product", product);
            intent.putExtra("trxNo", noTrx);
            startActivity(intent);
        }else{
            tilMobileNumber.setError("Mobile phone number is not valid");
            return false;
        }
        return true;
    }
}
