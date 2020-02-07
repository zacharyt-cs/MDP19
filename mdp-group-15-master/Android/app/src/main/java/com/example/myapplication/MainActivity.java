package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Bluetooth Connection";
    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mNewBTDevices;
    public ArrayList<BluetoothDevice> mPairedBTDevices;
    public DeviceListAdapter mNewDeviceListAdapter;
    public DeviceListAdapter mPairedDeviceListAdapter;
    private String connStatus;
    ListView otherDevicesListView;
    ListView pairedDevicesListView;
    Button connectBtn, forgetBtn;
    ImageButton backBtn;
    ProgressDialog myDialog;
    TextView mTitle, connStatusTextView;
    Toolbar toolbar;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    BluetoothConnectionService mBluetoothConnection;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice mBTDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        toolbar = findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Bluetooth");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        connStatusTextView = (TextView) findViewById(R.id.connStatusTextView);
        connStatus = "Disconnected";

        sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("connStatus"))
            connStatus = sharedPreferences.getString("connStatus", "");

        connStatusTextView.setText(connStatus);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Switch bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitch);
        if (mBluetoothAdapter.isEnabled()) {
            bluetoothSwitch.setChecked(true);
            bluetoothSwitch.setText("ON");
        }

        otherDevicesListView = (ListView) findViewById(R.id.devicesFoundListView);
        pairedDevicesListView = (ListView) findViewById(R.id.pairedDevicesListView);
        mNewBTDevices = new ArrayList<>();
        mPairedBTDevices = new ArrayList<>();

        connectBtn = (Button) findViewById(R.id.connectBtn);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        forgetBtn = (Button) findViewById(R.id.forgetBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        IntentFilter filter2 = new IntentFilter("ConnectionStatus");
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver5, filter2);


        otherDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothAdapter.cancelDiscovery();
                pairedDevicesListView.setAdapter(mPairedDeviceListAdapter);

                String deviceName = mNewBTDevices.get(i).getName();
                String deviceAddress = mNewBTDevices.get(i).getAddress();

                Log.d(TAG, "onItemClick: A device is selected.");
                Log.d(TAG, "onItemClick: DEVICE NAME: " + deviceName);
                Log.d(TAG, "onItemClick: DEVICE ADDRESS: " + deviceAddress);
                ToastUtil.showToast(MainActivity.this, mNewBTDevices.get(i).getName() + " Selected");
                view.setSelected(true);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Log.d(TAG, "onItemClick: Initiating pairing with " + deviceName);
                    mNewBTDevices.get(i).createBond();
                    mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
                    mBTDevice = mNewBTDevices.get(i);

                }
            }
        });

        pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothAdapter.cancelDiscovery();
                otherDevicesListView.setAdapter(mNewDeviceListAdapter);

                String deviceName = mPairedBTDevices.get(i).getName();
                String deviceAddress = mPairedBTDevices.get(i).getAddress();
                Log.d(TAG, "onItemClick: A device is selected.");
                Log.d(TAG, "onItemClick: DEVICE NAME: " + deviceName);
                Log.d(TAG, "onItemClick: DEVICE ADDRESS: " + deviceAddress);
                ToastUtil.showToast(MainActivity.this, mPairedBTDevices.get(i).getName() + " Selected");
                view.setSelected(true);
                mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
                mBTDevice = mPairedBTDevices.get(i);

            }
        });

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.d(TAG, "onChecked: Switch button toggled. Enabling/Disabling Bluetooth");
                if (isChecked) {
                    compoundButton.setText("ON");
                } else {
                    compoundButton.setText("OFF");
                }

                if (mBluetoothAdapter == null) {
                    Log.d(TAG, "enableDisableBT: Device does not support Bluetooth capabilities!");
                    ToastUtil.showToast(MainActivity.this, "Device Does Not Support Bluetooth capabilities!");
                    compoundButton.setChecked(false);
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Log.d(TAG, "enableDisableBT: enabling Bluetooth");
                        Log.d(TAG, "enableDisableBT: Making device discoverable for 600 seconds.");

                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
                        startActivity(discoverableIntent);

                        compoundButton.setChecked(true);

                        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                        registerReceiver(mBroadcastReceiver1, BTIntent);

                        IntentFilter discoverIntent = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                        registerReceiver(mBroadcastReceiver2, discoverIntent);
                    }
                    if (mBluetoothAdapter.isEnabled()) {
                        Log.d(TAG, "enableDisableBT: disabling Bluetooth");
                        mBluetoothAdapter.disable();

                        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                        registerReceiver(mBroadcastReceiver1, BTIntent);
                    }
                }
            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBTDevice == null) {
                    ToastUtil.showToast(MainActivity.this, "Please select a device to connect");
                } else {
                    startConnection();
                }
            }
        });

        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBTDevice == null) {
                    ToastUtil.showToast(MainActivity.this, "Please Select a device to forget");
                } else {
                    unpairDevice();
                }
            }
        });

        myDialog = new ProgressDialog(MainActivity.this);
        myDialog.setMessage("Waiting for other device to reconnect...");
        myDialog.setCancelable(false);
        myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


    }

    public void toggleButtonScan(View view) {
        Log.d(TAG, "toggleButton: Scanning for unpaired devices.");
        mNewBTDevices.clear();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                ToastUtil.showToast(MainActivity.this, "Please turn on Bluetooth first!");
            }
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "toggleButton: Cancelling Discovery.");

                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            } else if (!mBluetoothAdapter.isDiscovering()) {
                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }
            mPairedBTDevices.clear();
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            Log.d(TAG, "toggleButton: Number of paired devices found: " + pairedDevices.size());
            for (BluetoothDevice d : pairedDevices) {
                Log.d(TAG, "Paired Devices: " + d.getName() + " : " + d.getAddress());
                mPairedBTDevices.add(d);
                mPairedDeviceListAdapter = new DeviceListAdapter(this, R.layout.activity_device_list_adapter, mPairedBTDevices);
                pairedDevicesListView.setAdapter(mPairedDeviceListAdapter);

            }
        }
    }

    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");

        }
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mNewBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + " : " + device.getAddress());
                mNewDeviceListAdapter = new DeviceListAdapter(context, R.layout.activity_device_list_adapter, mNewBTDevices);
                otherDevicesListView.setAdapter(mNewDeviceListAdapter);


            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BOND_BONDED.");
                    ToastUtil.showToast(MainActivity.this, "Successfully paired with " + mDevice.getName());
                    mBTDevice = mDevice;
                    mPairedBTDevices.add(mDevice);
                    mPairedDeviceListAdapter.notifyDataSetChanged();
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BOND_BONDING.");
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BOND_NONE.");
                }
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice mDevice = intent.getParcelableExtra("Device");
            String status = intent.getStringExtra("Status");
            sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            if (status.equals("connected")) {
                try {
                    myDialog.dismiss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "mBroadcastReceiver5: Device now connected to " + mDevice.getName());
                ToastUtil.showToast(MainActivity.this, "Device now connected to " + mDevice.getName());
                editor.putString("connStatus", "Connected to " + mDevice.getName());
                connStatusTextView.setText("Connected to " + mDevice.getName());
            } else if (status.equals("disconnected")) {
                Log.d(TAG, "mBroadcastReceiver5: Disconnected from " + mDevice.getName());
                ToastUtil.showToast(MainActivity.this, "Disconnected from " + mDevice.getName());
                mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
                mBluetoothConnection.startAcceptThread();

                sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("connStatus", "Disconnected");
                TextView connStatusTextView = findViewById(R.id.connStatusTextView);
                connStatusTextView.setText("Disconnected");
                editor.commit();

                try {
                    myDialog.show();
                } catch (Exception e) {
                    Log.d(TAG, "BluetoothPopUp: mBroadcastReceiver5 Dialog show failure");
                }


            }
            editor.commit();
        }
    };

    public void startConnection() {
        startBTConnection(mBTDevice, myUUID);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection");

        mBluetoothConnection.startClientThread(device, uuid);
    }

    public void unpairDevice() {
        unpairBTDevice(mBTDevice);
    }

    public void unpairBTDevice(BluetoothDevice device) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice d : pairedDevices) {
                try {
                    Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                    mPairedBTDevices.remove(mBTDevice);
                    mPairedDeviceListAdapter = new DeviceListAdapter(this, R.layout.activity_device_list_adapter, mPairedBTDevices);
                    pairedDevicesListView.setAdapter(mPairedDeviceListAdapter);
                    mPairedDeviceListAdapter.notifyDataSetChanged();
                    mNewDeviceListAdapter = new DeviceListAdapter(this, R.layout.activity_device_list_adapter, mNewBTDevices);
                    otherDevicesListView.setAdapter(mNewDeviceListAdapter);
                    mNewDeviceListAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e("Removing failed", e.getMessage());
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        try {
            unregisterReceiver(mBroadcastReceiver1);
            unregisterReceiver(mBroadcastReceiver2);
            unregisterReceiver(mBroadcastReceiver3);
            unregisterReceiver(mBroadcastReceiver4);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver5);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("mBTDevice", mBTDevice);
        data.putExtra("myUUID", myUUID);
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.messages) {
            Intent intent1 = new Intent(this, MessagingActivity.class);
            this.startActivity(intent1);
            return true;
        }
        if (id == R.id.menu) {
            Intent intent3 = new Intent(this, Dashboard.class);
            this.startActivity(intent3);
            return true;

        }
        if (id == R.id.arena) {
            Intent intent4 = new Intent(this, ArenaActivity.class);
            this.startActivity(intent4);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
