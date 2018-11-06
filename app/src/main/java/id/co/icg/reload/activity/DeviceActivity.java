package id.co.icg.reload.activity;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.ItemAdapter;

public class DeviceActivity extends Activity {

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_DEVICE_NAME = "device_name";

    // Member fields
    private BluetoothAdapter mBtAdapter;

    private List<String> paired = new ArrayList<>();
    private List<String> newDevices = new ArrayList<>();

    ItemAdapter pairedAdapter;
    ItemAdapter newAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
//        Button scanButton = (Button) findViewById(R.id.button_scan);
//        scanButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                doDiscovery();
//                v.setVisibility(View.GONE);
//            }
//        });


        // Find and set up the ListView for paired devices
        RecyclerView pairedListView = findViewById(R.id.paired_devices);
        pairedListView.setLayoutManager(new LinearLayoutManager(this));
        pairedListView.setItemAnimator(new DefaultItemAnimator());
        pairedAdapter = new ItemAdapter(this, paired, item -> connect(item));
        pairedListView.setAdapter(pairedAdapter);

        // Find and set up the ListView for newly discovered devices
        RecyclerView newDevicesListView = findViewById(R.id.new_devices);
        newDevicesListView.setLayoutManager(new LinearLayoutManager(this));
        newDevicesListView.setItemAnimator(new DefaultItemAnimator());
        newAdapter = new ItemAdapter(this, newDevices, item -> connect(item));
        newDevicesListView.setAdapter(newAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter != null && mBtAdapter.getBondedDevices().size() > 0) {
            // Get a set of currently paired devices
            // If there are paired devices, add each one to the ArrayAdapter
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                paired.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            paired.add(noDevices);
        }
        pairedAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }


    public void connect(String info) {
        // Cancel discovery because it's costly and we're about to connect


        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        String no_device = getResources().getText(R.string.none_paired).toString();
        Intent intent = new Intent();
        if (!info.equals(no_device)) {
            // Get the device MAC address, which is the last 17 chars in the View
            String address = info.substring(info.length() - 17);
            String name = info.substring(0, info.length() - 18);
            //Global.DEVICE_NAME = info.substring(0, info.length() - 18);
            //Global.DEVICE_MAC = address;

            // Create the result Intent and include the MAC address
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            intent.putExtra(EXTRA_DEVICE_NAME, name);

        }
        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDevices.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (newDevices.size() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    newDevices.add(noDevices);
                }
            }
            newAdapter.notifyDataSetChanged();
        }
    };

}