package id.co.icg.reload.fragment;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.TransactionAdapter;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.HistoryService;
import id.co.icg.reload.model.AccountLog;
import id.co.icg.reload.ui.FixedHoloDatePickerDialog;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.Static;
import retrofit2.Call;
import retrofit2.Callback;

public class TransactionsFragment extends BaseFragment {

    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;
    private ImageView ivCalendar;
    private TextView tvDate;
    private TextView tvDebit;
    private TextView tvCredit;
    private TextView tvOpeningBalance;
    private TextView tvClosingBalance;

    private RecyclerView rvTransactions;
    private TransactionAdapter transactionsAdapter;
    private List<AccountLog> accountLogs;

    private Date date;
    private Calendar myCalendar = Calendar.getInstance();
    private HistoryService historyService;

    private DecimalFormat decimalFormat = new DecimalFormat("Rp ###,###,###");

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        date = new Date();
        tvDate = view.findViewById(R.id.tv_date);
        tvDebit = view.findViewById(R.id.tv_debit);
        tvCredit = view.findViewById(R.id.tv_credit);
        tvOpeningBalance = view.findViewById(R.id.tv_opening_balance);
        tvClosingBalance = view.findViewById(R.id.tv_closing_balance);

        tvDate.setText(sdf.format(date));
        ivCalendar = view.findViewById(R.id.iv_calendar);
        searchView = view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                transactionsAdapter.getFilter().filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            searchView.onActionViewCollapsed();
            transactionsAdapter.getFilter().filter("");
            return true;
        });

        ivCalendar.setOnClickListener(v -> showDateDialog());

        rvTransactions = view.findViewById(R.id.rvTransactions);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTransactions.setLayoutManager(layoutManager);
        rvTransactions.setItemAnimator(new DefaultItemAnimator());

        historyService = ApiUtils.HistoryService(getActivity());

        accountLogs = new ArrayList<>();
        transactionsAdapter = new TransactionAdapter(getActivity(), accountLogs, model -> false);
        rvTransactions.setAdapter(transactionsAdapter);

        getHistory(sdf1.format(date));

        refreshLayout.setOnRefreshListener(() -> getHistory(sdf1.format(date)));

        return view;
    }

    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

    private void getHistory(String date){
        //check if there is a pending balance
        showPleaseWaitDialog();

        historyService.getAccountLogs(date).enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                refreshLayout.setRefreshing(false);
                try {
                    if (response.isSuccessful()) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                        List<AccountLog> logs = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<AccountLog>>() {
                        }.getType());
                        accountLogs.clear();
                        if (logs!=null && logs.size()>0){
                            Double debit=0d;
                            Double credit=0d;
                            AccountLog close = logs.get(0);
                            AccountLog open = logs.get(logs.size() - 1);
                            Double openingBalance = open.getCurrentBalance() - open.getCredit() + open.getDebit();
                            Double closingBalance = close.getCurrentBalance();
                            Double d = 0d, x=0d, y=0d, r=0d, tc=0d, td=0d;
                            for (AccountLog log : logs) {
                                if (log.getStan().startsWith("D")){
                                    d = d+log.getCredit();
                                }else if (log.getStan().startsWith("X")){
                                    x = x+log.getCredit();
                                }else if (log.getStan().startsWith("Y")){
                                    y = y+log.getCredit();
                                }else if (log.getStan().startsWith("R")){
                                    r = r+log.getDebit();
                                }else if (log.getStan().startsWith("T")){
                                    tc = tc+log.getCredit();
                                    td = td+log.getDebit();
                                }else{
                                    credit = credit+log.getCredit();
                                    debit = debit+log.getDebit();
                                }
                                accountLogs.add(log);
                            }
                            Double incoming = d + x + y + tc;
                            Double outgoing = r + td;
                            Double deposits = d + x + y - r + tc - td;
                            Double transactions = openingBalance + deposits - closingBalance;
                            tvDebit.setText(decimalFormat.format(transactions));
                            tvCredit.setText(decimalFormat.format(deposits));
                            tvOpeningBalance.setText(decimalFormat.format(openingBalance));
                            tvClosingBalance.setText(decimalFormat.format(closingBalance));
                        }
                        transactionsAdapter.notifyDataSetChanged();
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


    private void showDateDialog(){
        if (Build.VERSION.SDK_INT >= 24) {
            new FixedHoloDatePickerDialog(getActivity(), onDateSetListener,
                    myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }else{
            new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    onDateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");

    private DatePickerDialog.OnDateSetListener onDateSetListener = new FixedHoloDatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date = myCalendar.getTime();
            getHistory(sdf1.format(date));
            tvDate.setText(sdf.format(date));
        }

    };
}
