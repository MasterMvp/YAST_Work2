package ly006.yn.read;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Set;

public class ListActivity extends Activity {
    // 调试
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    // 返回别的意图
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    // 适配器
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                // v.setVisibility(View.GONE);
            }
        });
        Button clearButton = (Button) findViewById(R.id.butclearbluetooth);
        clearButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                clearbluetoothlist();
            }
        });
        // 初始化数组适配器。一个已配对装置和

        // 一个新发现的设备
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // 寻找和建立配对设备列表
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // 寻找和建立为新发现的设备列表
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // 注册时发送广播给设备
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // 广播时发现已完成注册
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // 获取本地蓝牙适配器
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // 得到一套目前配对设备
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            // Log.e(TAG, e.getMessage());
        }
    }

    private void clearbluetoothlist() {

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // 得到一套目前配对设备
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                unpairDevice(device);
            }
            Toast.makeText(this, "清除配对列表成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "没有配对过的设备", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保我们没有发现了
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        // 注销广播听众
        this.unregisterReceiver(mReceiver);
    }

    /**
     * 发现与bluetoothadapter启动装置
     */
    private void doDiscovery() {
        if (D)
            Log.d(TAG, "doDiscovery()");

        // 显示扫描的称号
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // 打开新设备的字幕
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // 如果我们已经发现，阻止它
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        // 要求从bluetoothadapter发现
        mBtAdapter.startDiscovery();
    }

    // 点击听众的所有设备在listviews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // 因为它是浪费的，取消发现我们的连接
            mBtAdapter.cancelDiscovery();
            // 获得设备地址，这是近17字的
            // 视图
            String info = ((TextView) v).getText().toString();
            if (info.equals("没有已配对设备") || info.equals("没有建立连接")) {
                return;
            }
            String address = info.substring(info.length() - 17);
            BluetoothDevice btDev = mBtAdapter.getRemoteDevice(address);
            try {
                Boolean returnValue = false;
                if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
                    // 利用反射方法调用BluetoothDevice.createBond(BluetoothDevice
                    // remoteDevice);
                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    Log.d("BlueToothTestActivity", "开始配对");
                    returnValue = (Boolean) createBondMethod.invoke(btDev);
                    Thread.sleep(2000);// 等待配对成功
                    for (int i = 0; i < 3; i++) {
                        if (btDev.getBondState() == BluetoothDevice.BOND_BONDED)
                            break;
                        else
                            Thread.sleep(1000);
                    }
                } else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
                    // connect(btDev); //已经配过对了
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 传蓝牙地址给主界面
            Intent intent = new Intent();
            intent.putExtra("address", address);
            // 结果，完成这项活动
            setResult(2, intent);
            finish();
        }
    };

    // 该broadcastreceiver监听设备和
    // 变化的标题时，发现完成
    String[] adress = new String[500];
    int counts = 0;
    boolean add = true;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 当发现设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 把蓝牙设备对象的意图
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果它已经配对，跳过它，因为它的上市
                // 早已
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    add = true;
                    String s = "dd";
                    for (int i = 0; i < counts; i++) {
                        if (device.getAddress().trim().toUpperCase().equals(adress[i])) {

                            add = false;
                            s = "ff";
                            // break;
                        }
                    }
                    if (add == true) {

                        Log.i("dddddddd", device.getAddress());
                        adress[counts] = device.getAddress().trim().toUpperCase();
                        mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        counts = counts + 1;

                        // add=false;
                    }

                }
                // 当发现后，改变活动名称
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

}
