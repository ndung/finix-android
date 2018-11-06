package id.co.icg.reload.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.PriceAdapter;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.TransactionService;
import id.co.icg.reload.model.Product;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class PriceFragment extends BaseFragment{

    private SwipeRefreshLayout refreshLayout;
    private TransactionService transactionService;
    private List<Product> list;
    private Map<Integer,List<Product>> productMaps;
    private PriceAdapter priceAdapter;
    private RecyclerView rvPrices;

    private int currentCategory = 1;

    private TextView tvProductReguler;
    private TextView tvProductData;
    private TextView tvProductPpob;
    private TextView tvProductOther;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;
    private SearchView searchView;
    private NestedScrollView nestedScrollView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prices, container, false);

        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        final CardView llProductReguler = view.findViewById(R.id.ll_product_reguler);
        final CardView llProductData = view.findViewById(R.id.ll_product_data);
        final CardView llProductPpob = view.findViewById(R.id.ll_product_ppob);
        final CardView llProductOther = view.findViewById(R.id.ll_product_other);

        final TextView labelProductReguler = view.findViewById(R.id.label_product_reguler);
        final TextView labelProductData = view.findViewById(R.id.label_product_data);
        final TextView labelProductPpob = view.findViewById(R.id.label_product_ppob);
        final TextView labelProductOther = view.findViewById(R.id.label_product_other);

        tvProductReguler = view.findViewById(R.id.tv_product_reguler);
        tvProductData = view.findViewById(R.id.tv_product_data);
        tvProductPpob = view.findViewById(R.id.tv_product_ppob);
        tvProductOther = view.findViewById(R.id.tv_product_other);

        llProductReguler.setOnClickListener(v -> {
            llProductReguler.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            llProductReguler.setCardElevation(4f);
            llProductReguler.setRadius(8f);
            llProductData.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductData.setCardElevation(0f);
            llProductData.setRadius(0f);
            llProductPpob.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductPpob.setCardElevation(0f);
            llProductPpob.setRadius(0f);
            llProductOther.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductOther.setCardElevation(0f);
            llProductOther.setRadius(0f);

            labelProductReguler.setTextColor(getResources().getColor(R.color.text_black));
            labelProductData.setTextColor(getResources().getColor(android.R.color.white));
            labelProductPpob.setTextColor(getResources().getColor(android.R.color.white));
            labelProductOther.setTextColor(getResources().getColor(android.R.color.white));

            tvProductReguler.setTextColor(getResources().getColor(R.color.text_black));
            tvProductData.setTextColor(getResources().getColor(android.R.color.white));
            tvProductPpob.setTextColor(getResources().getColor(android.R.color.white));
            tvProductOther.setTextColor(getResources().getColor(android.R.color.white));

            currentCategory = 1;
            refreshProducts();
        });

        llProductData.setOnClickListener(v -> {
            llProductData.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            llProductData.setCardElevation(4f);
            llProductData.setRadius(8f);
            llProductReguler.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductReguler.setCardElevation(0f);
            llProductReguler.setRadius(0f);
            llProductPpob.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductPpob.setCardElevation(0f);
            llProductPpob.setRadius(0f);
            llProductOther.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductOther.setCardElevation(0f);
            llProductOther.setRadius(0f);

            labelProductData.setTextColor(getResources().getColor(R.color.text_black));
            labelProductReguler.setTextColor(getResources().getColor(android.R.color.white));
            labelProductPpob.setTextColor(getResources().getColor(android.R.color.white));
            labelProductOther.setTextColor(getResources().getColor(android.R.color.white));

            tvProductData.setTextColor(getResources().getColor(R.color.text_black));
            tvProductReguler.setTextColor(getResources().getColor(android.R.color.white));
            tvProductPpob.setTextColor(getResources().getColor(android.R.color.white));
            tvProductOther.setTextColor(getResources().getColor(android.R.color.white));

            currentCategory = 2;
            refreshProducts();
        });

        llProductPpob.setOnClickListener(v -> {
            llProductPpob.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            llProductPpob.setCardElevation(4f);
            llProductPpob.setRadius(8f);
            llProductReguler.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductReguler.setCardElevation(0f);
            llProductReguler.setRadius(0f);
            llProductData.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductData.setCardElevation(0f);
            llProductData.setRadius(0f);
            llProductOther.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductOther.setCardElevation(0f);
            llProductOther.setRadius(0f);

            labelProductPpob.setTextColor(getResources().getColor(R.color.text_black));
            labelProductReguler.setTextColor(getResources().getColor(android.R.color.white));
            labelProductData.setTextColor(getResources().getColor(android.R.color.white));
            labelProductOther.setTextColor(getResources().getColor(android.R.color.white));

            tvProductPpob.setTextColor(getResources().getColor(R.color.text_black));
            tvProductReguler.setTextColor(getResources().getColor(android.R.color.white));
            tvProductData.setTextColor(getResources().getColor(android.R.color.white));
            tvProductOther.setTextColor(getResources().getColor(android.R.color.white));

            currentCategory = 3;
            refreshProducts();
        });

        llProductOther.setOnClickListener(v -> {
            llProductOther.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            llProductOther.setCardElevation(4f);
            llProductOther.setRadius(8f);
            llProductReguler.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductReguler.setCardElevation(0f);
            llProductReguler.setRadius(0f);
            llProductData.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductData.setCardElevation(0f);
            llProductData.setRadius(0f);
            llProductPpob.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            llProductPpob.setCardElevation(0f);
            llProductPpob.setRadius(0f);

            labelProductOther.setTextColor(getResources().getColor(R.color.text_black));
            labelProductReguler.setTextColor(getResources().getColor(android.R.color.white));
            labelProductData.setTextColor(getResources().getColor(android.R.color.white));
            labelProductPpob.setTextColor(getResources().getColor(android.R.color.white));

            tvProductOther.setTextColor(getResources().getColor(R.color.text_black));
            tvProductReguler.setTextColor(getResources().getColor(android.R.color.white));
            tvProductData.setTextColor(getResources().getColor(android.R.color.white));
            tvProductPpob.setTextColor(getResources().getColor(android.R.color.white));

            currentCategory = 4;
            refreshProducts();
        });

        productMaps = new HashMap<>();
        transactionService = ApiUtils.TransactionService(getActivity());

        list = new ArrayList<>();
        priceAdapter = new PriceAdapter(getActivity(), list);

        rvPrices = view.findViewById(R.id.rvPrices);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvPrices.setLayoutManager(layoutManager);
        rvPrices.setItemAnimator(new DefaultItemAnimator());

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    loadMore();
                }
            }
        });

        getProducts();

        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(() -> getProducts());

        searchView = view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                priceAdapter.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            searchView.onActionViewCollapsed();
            priceAdapter.getFilter().filter("");
            return true;
        });
        return view;
    }

    int PAGE = 5;

    private void loadMore() {
        if (!isLastPage) {
            List<Product> productList = productMaps.get(currentCategory);
            currentPage += 1;

            int size = productList.size();
            if (currentPage * PAGE < productList.size()) {
                size = currentPage*PAGE;
            }

            int init = ((currentPage - 1) * PAGE);

            for (int i = init ; i < size; i++) {
                Product product = productList.get(i);
                list.add(product);
            }
            priceAdapter.notifyDataSetChanged();
            if (currentPage == TOTAL_PAGES) isLastPage = true;
        }
    }


    private static final String TAG = PriceFragment.class.toString();

    private void refreshProducts(){
        isLastPage = false;
        list.clear();
        List<Product> productList = productMaps.get(currentCategory);
        TOTAL_PAGES = (int)Math.ceil(productList.size()/Double.valueOf(PAGE));
        currentPage = 1;
        int size = productList.size();
        if (currentPage*PAGE<productList.size()){
            size = currentPage*PAGE;
        }
        for (int i =((currentPage-1)*PAGE);i<size;i++){
            Product product = productList.get(i);
            list.add(product);
        }
        priceAdapter.notifyDataSetChanged();
        if (currentPage > TOTAL_PAGES) isLastPage = true;
    }

    private void getProducts(){
        showPleaseWaitDialog();

        transactionService.getAllProducts().enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                refreshLayout.setRefreshing(false);
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();

                        List<Product> products = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<Product>>() {
                        }.getType());
                        if (products != null && products.size() > 0) {
                            List<Product> regulerProducts = new ArrayList<>();
                            List<Product> dataProducts = new ArrayList<>();
                            List<Product> ppobProducts = new ArrayList<>();
                            List<Product> otherProducts = new ArrayList<>();
                            for (Product product : products){
                                Integer category = product.getCategory();
                                if (category==1){
                                    regulerProducts.add(product);
                                }else if (category==2){
                                    dataProducts.add(product);
                                }else if (category==3){
                                    ppobProducts.add(product);
                                }else if (category==4){
                                    otherProducts.add(product);
                                }
                            }
                            productMaps.put(1, regulerProducts);
                            productMaps.put(2, dataProducts);
                            productMaps.put(3, ppobProducts);
                            productMaps.put(4, otherProducts);
                            tvProductReguler.setText(String.valueOf(regulerProducts.size()));
                            tvProductData.setText(String.valueOf(dataProducts.size()));
                            tvProductPpob.setText(String.valueOf(ppobProducts.size()));
                            tvProductOther.setText(String.valueOf(otherProducts.size()));
                            refreshProducts();
                        }else{
                            showMessage("Tidak ada produk yang aktif saat ini");
                        }
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
}
