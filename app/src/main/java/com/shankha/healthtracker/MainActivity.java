package com.shankha.healthtracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final int UPDATE_READING = 1;
    private TextView editTextTemp;
    private TextView  editTextHeartRate;
    private TextView  editTextSpo2;
    private Button buttonanalysis;
    // Global variables we will use in the
    private static final String TAG = "FrugalLogs";
    private int heartRate ;
    private int spo2;
    private float temperature;
    private static final int REQUEST_ENABLE_BT = 1;
    public  Handler handler;
    private final static int ERROR_READ = 0;
    private static final int MESSAGE_READ = 2;
    BluetoothDevice arduinoBTModule = null;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private TextView btReadings;

    private FirebaseAuth auth;
    private FirebaseDatabase database=FirebaseDatabase.getInstance() ;
    private UserModel userDetails;
    private HealthData healthData;

    private String name;
    private int age, weight;
    UUID arduinoUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //We declare a default UUID to create the global variable
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth= FirebaseAuth.getInstance();
        DatabaseReference userRef= database.getReference("Users");

        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            String userId=auth.getCurrentUser().getUid();
            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        userDetails =snapshot.getValue(UserModel.class);
                        if (userDetails != null) {
                            // Use userDetails object as needed
                            Log.d("firebase", "UserName: " + userDetails.getUserName());
                            Log.d("firebase", "Email: " + userDetails.getEmail());
                            Log.d("firebase", "PhoneNo: " + userDetails.getPhoneNo());
                            try {
                                name = userDetails.getUserName();
                                // Ensure userModel.getAge() is not null or empty before parsing
                                String ageString = userDetails.getAge();
                                age = (ageString != null && !ageString.isEmpty()) ? Integer.parseInt(ageString) : 0;
                                // Ensure userModel.getWeight() is not null or empty before parsing
                                String weightString = userDetails.getWeight();
                                weight = (weightString != null && !weightString.isEmpty()) ? Integer.parseInt(weightString) : 0;
                            } catch (NumberFormatException e) {
                                Log.e("firebase", "Error parsing age or weight", e);
                            }
                        }else {
                            Log.e("firebase", "UserModel is null");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("firebase", "Error getting data", error.toException());
                }
            });
        }else {
            Log.e("firebase", "User is not authenticated");
        }

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        btReadings = findViewById(R.id.btReadings);
        TextView btDevices = findViewById(R.id.btDevices);
        Button connectToDevice = (Button) findViewById(R.id.connectToDevice);
        Button seachDevices = (Button) findViewById(R.id.seachDevices);
        Button clearValues = (Button) findViewById(R.id.refresh);

        editTextTemp = findViewById(R.id.editTextTemperature);
        editTextHeartRate= findViewById(R.id.editTextHeartRate);
        editTextSpo2 =findViewById(R.id.editTextSpo2);
        buttonanalysis =findViewById(R.id.buttonAnalysis);

        buttonanalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (heartRate >= 30 && heartRate <=250 && spo2<101 && spo2 >=50 && temperature<43.00 && temperature>25.00) {

                        Intent intent2 = new Intent(MainActivity.this, AnalysisActivity.class);
                        intent2.putExtra("userId",auth.getCurrentUser().getUid());
                        startActivity(intent2);

                    } else {
                        Toast.makeText(MainActivity.this, "Please wait for valid data", Toast.LENGTH_SHORT).show();
                    }
            }
        });


        Log.d(TAG, "Begin Execution");

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    case MESSAGE_READ:
                        String arduinoMsg = (String) msg.obj;
                        processReceivedData(arduinoMsg);
                        break;
                    case ERROR_READ:
                        btReadings.setText("Error reading data    :");
                        break;

                }
            }
        };

        clearValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btReadings.setText("");
                editTextHeartRate.setText("Heart Rate : ");
                editTextTemp.setText("Temperature :");
                editTextSpo2.setText("SpO2 :  ");
                editTextHeartRate.setVisibility(View.INVISIBLE);
                editTextSpo2.setVisibility(View.INVISIBLE);
                editTextTemp.setVisibility(View.INVISIBLE);
            }
        });


        connectToDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btReadings.setText("");
                if (arduinoBTModule != null) {


                    if (arduinoBTModule != null) {
                        synchronized (this) {
                            if (connectThread == null || !connectThread.isAlive()) {
                                connectThread = new ConnectThread(arduinoBTModule, arduinoUUID, handler);
                                connectThread.start();
                            }
                        }
                    }

                }
            }
        });


        seachDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bluetoothAdapter == null) {
                    Log.d(TAG, "Device doesn't support Bluetooth");
                } else {
                    Log.d(TAG, "Device support Bluetooth");
                    if (!bluetoothAdapter.isEnabled()) {
                        Log.d(TAG, "Bluetooth is disabled");
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                            Log.d(TAG, "We don't BT Permissions");
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            Log.d(TAG, "Bluetooth is enabled now");
                        } else {
                            Log.d(TAG, "We have BT Permissions");
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            Log.d(TAG, "Bluetooth is enabled now");
                        }

                    } else {
                        Log.d(TAG, "Bluetooth is enabled");
                    }
                    String btDevicesString="";
                    Set< BluetoothDevice > pairedDevices = bluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {

                        for (BluetoothDevice device: pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address
                            Log.d(TAG, "deviceName:" + deviceName);
                            Log.d(TAG, "deviceHardwareAddress:" + deviceHardwareAddress);

                            btDevicesString=btDevicesString+deviceName+" || "+deviceHardwareAddress+"\n";

                            if (deviceName.equals("HC-05")) {
                                Log.d(TAG, "HC-05 found");
                                arduinoUUID = device.getUuids()[0].getUuid();
                                arduinoBTModule = device;
                                connectToDevice.setEnabled(true);
                            }
                            btDevices.setText(btDevicesString);
                        }
                    }
                }
                Log.d(TAG, "Button Pressed");
            }
        });
    }


    private void processReceivedData(String data) {
        data = data.trim();
        String[] parts = data.split("\\s+");

        try {
            heartRate = Integer.parseInt(parts[0]);
            spo2 = Integer.parseInt(parts[1]);
            temperature = Float.parseFloat(parts[2]);
            temperature= temperature+5;





            if(heartRate==-1 || spo2==-1 || temperature ==-1 ){
                btReadings.setText("Please wait for sensor to stabilize !!");
            }else{
                editTextHeartRate.setText("Heart Rate :  " +heartRate +"  bpm" );
                editTextSpo2.setText("SpO2 :  " +spo2 + "  %");
                editTextTemp.setText("Temperature : " +temperature);
                editTextHeartRate.setVisibility(View.VISIBLE);
                editTextSpo2.setVisibility(View.VISIBLE);
                editTextTemp.setVisibility(View.VISIBLE);
                btReadings.setText("");
                saveHealthData(temperature,heartRate,spo2);
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "Error parsing received data", e);
            btReadings.setText("Error parsing data");
        }
    }

    private void saveHealthData(float temperature, int heartRate, int spo2) {
        healthData=new HealthData(temperature,heartRate,spo2);
        DatabaseReference healthRef= database.getReference("HealthData");
        healthRef.child(auth.getCurrentUser().getUid()).child(dateName()).setValue(healthData);
    }


    public static String dateName() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            return simpleDateFormat.format(new Date());}

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulist, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,PersonalDetailsActivity.class));
            return true;
        } else if (id == R.id.editProfile) {
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }else{
            startActivity(new Intent(MainActivity.this,OtherUserActivity.class));
        }

        // Handle other menu item clicks if needed
        return super.onOptionsItemSelected(item);
    }
}