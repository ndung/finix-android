package id.co.icg.reload.activity;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebamboo.p25library.ControlPrint;
import com.bluebamboo.p25library.P25Session;
import com.bluebamboo.p25library.P25Session.SessionListener;
import com.bluebamboo.p25library.util.DataConstants;

import id.co.icg.reload.R;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.PrinterUtil;

public class PrinterActivity extends BaseActivity {

    private static final int REQUEST_CONNECT_DEVICE = 1;

    private ImageView ivBack;
    private TextView tvTitle;
    private String device_name = "", device_address = "";
    final P25Session session = P25Session.getInstance();
    final ControlPrint control = new ControlPrint();

    private CardView tvSetPdf;
    private TextView tvPdfDefault;
    private CardView tvSetBt;
    private TextView tvBtDefault;
    private CardView tvTestBt;
    private TextView tvBtPrinter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_printer);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvSetPdf = findViewById(R.id.ll_set_pdf);
        tvPdfDefault = findViewById(R.id.tv_pdf_default);
        tvSetBt = findViewById(R.id.ll_set_bluetooth);
        tvBtDefault = findViewById(R.id.tv_bluetooth_default);
        tvTestBt = findViewById(R.id.ll_test_bluetooth);
        tvBtPrinter = findViewById(R.id.tv_bluetooth_printer);

        tvSetPdf.setOnClickListener(v -> setPdfAsDefault());

        tvSetBt.setOnClickListener(v -> connectSetDefaultBluetoothDevice());

        tvTestBt.setOnClickListener(v -> testBluetoothPrinter());

        if (session.isConnected()) {
            tvBtPrinter.setText("Connected to bluetooth printer "+device_name);
        } //else {
          //  showFailedNotification();
        //}

        String defaultPrinter = Preferences.getDefaultPrinter(this);

        if (defaultPrinter.contains("PDF")||defaultPrinter.contains("pdf")){
            tvPdfDefault.setText("Default");
            tvBtDefault.setText("");
        }else{
            tvPdfDefault.setText("");
            tvBtDefault.setText("Default "+defaultPrinter);
        }

        tvTitle.setText("Setting printer");
        ivBack.setOnClickListener(v -> finish());

        setResponseListener();
        Log.d("p10session", "session.connected: " + session.isConnected());
    }

    private void setPdfAsDefault() {
        Preferences.setDefaultPrinter(this, "pdf");
        tvPdfDefault.setText("Default");
        tvBtDefault.setText("");
        new AlertDialog.Builder(PrinterActivity.this)
                .setTitle(R.string.ok)
                .setMessage(R.string.success)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // TODO Auto-generated method stub
                })
                .create()
                .show();
    }

    private void connectSetDefaultBluetoothDevice() {
        if (session.isConnected()) {
            session.DisconnectFromPrinter();
        }
        Intent intent = new Intent(PrinterActivity.this, DeviceActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
    }

    private void testBluetoothPrinter() {
        if (session.isConnected()) {
            printDemoContent();
        } else {
            new AlertDialog.Builder(PrinterActivity.this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.bluetooth_title_not_connected)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        // TODO Auto-generated method stub
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == RESULT_OK) {
                    // Get the device MAC address
                    if (data.getExtras() != null) {
                        device_name = data.getExtras().getString(DeviceActivity.EXTRA_DEVICE_NAME);
                        device_address = data.getExtras().getString(DeviceActivity.EXTRA_DEVICE_ADDRESS);
                        // Do device connection using p25library
                        P25Session session = P25Session.getInstance();
                        session.ConnectToPrinter(device_address);
                    }
                    break;
                }
            default:
                break;
        }
    }

    private void showFailedNotification(){
        showMessage("Connecting to bluetooth printer failed");
    }

    private void setBluetoothPrinter(){
        tvPdfDefault.setText("");
        tvBtDefault.setText("Default "+device_address);
        tvBtPrinter.setText("Connected to bluetooth printer "+device_name);
        Preferences.setDefaultPrinter(this, device_address);
    }

    private void setResponseListener() {
        session.setSessionListener(new SessionListener() {
            @Override
            public void onConnected() {
                setBluetoothPrinter();
            }

            @Override
            public void onConnectFailed() {
                showFailedNotification();
            }

            @Override
            public void onDisconnected() {
                showFailedNotification();
            }
        });
    }

    private void printDemoContent() {
        /*********** print head *******/
        String receiptHead = "************************" + "   Receipt Test" + "\n" + "************************" + "\n";

        StringBuffer buffer = new StringBuffer();
        String dateString = (new Date()).toString();
        String dateTime[] = PrinterUtil.filterDate(dateString);
        String month = dateTime[1];

        String monthName = "";
        if ("123456789101112".indexOf(month) != -1) // it means month is digit
        {
            int monthIndex = Integer.parseInt(month);
            monthName = PrinterUtil.getMonthName(monthIndex);
            buffer.append(monthName);
        } else {
            buffer.append(month);
        }
        buffer.append(" ");
        String dateNum = dateTime[2];

        buffer.append(dateNum);

        buffer.append(", ");

        String year = dateTime[5];

        buffer.append(year);

        String stringDate = buffer.toString();

        buffer = new StringBuffer(20);
        buffer.append("");
        buffer.append(PrinterUtil.getHHMM(dateTime[3])); // hhmm

        String stringTime = buffer.toString();

        StringBuffer receiptHeadBuffer = new StringBuffer(100);
        receiptHeadBuffer.append(receiptHead);
        receiptHeadBuffer.append(PrinterUtil.nameLeftValueRightJustify(stringDate, stringTime, DataConstants.RECEIPT_WIDTH) + "\n");
        receiptHead = receiptHeadBuffer.toString();

        /*********** print English text *******/
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < 128; i++)
            sb.append((char) i);
        String content = sb.toString().trim();

        /*********** print Tail *******/
        String receiptTail = "Test Completed" + "\n" + "************************" + "\n\n\n";

        control.printData(receiptHead.concat(content).concat(receiptTail));
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}