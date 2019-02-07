package com.example.utente.sgh_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.Toast;
import android.content.Intent;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView statusBluetooth;
    TextView paired;
    ImageView iconBT;
    Button accendiBluetooth, spegniBluetooth, ricercaDispositivi, dispositiviAccopiati;

    BluetoothAdapter btAdapter;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusBluetooth = findViewById(R.id.statusBluetooth);
        paired = findViewById(R.id.paired);
        accendiBluetooth = findViewById(R.id.BluetoothON);
        spegniBluetooth = findViewById(R.id.bluetoothOFF);
        dispositiviAccopiati = findViewById(R.id.pairedBluetooth);
        ricercaDispositivi = findViewById(R.id.discover);
        iconBT = findViewById(R.id.imageBluetooth);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if bluetooth is available

        if(btAdapter == null){
            statusBluetooth.setText("Bluetooth non disponibile");
        } /*else {
            statusBluetooth.setText("Bluetooth disponibile");
        }*/

        //set image bluetooth
        if(btAdapter.isEnabled()){
            iconBT.setImageResource(R.drawable.ic_action_on);

        } else {
            iconBT.setImageResource(R.drawable.ic_action_off);
        }

        //ON button click
        accendiBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btAdapter.isEnabled()){
                    showToast("Accendo il bluetooth");
                    //intent che accende
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    showToast("Bluetooth già acceso");
                }
            }
        });

        //OFF button click
        spegniBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btAdapter.isEnabled()){
                    btAdapter.disable();
                    showToast("Bluetooth spento");
                    iconBT.setImageResource(R.drawable.ic_action_off);
                } else {
                    showToast("Il bluetooth è già spento");
                }
            }
        });

        //discover button click
        ricercaDispositivi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btAdapter.isDiscovering()){
                    showToast("Inizio ricerca");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }

            }
        });

        //paired button click
        dispositiviAccopiati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btAdapter.isEnabled()){
                    Set<BluetoothDevice> devices = btAdapter.getBondedDevices();

                    // Explicit Intent to launch the connect activity
                    intent = new Intent(MainActivity.this, connect.class);

                    startActivity(intent);

                } else {
                    //il bluetooth è spento quindi non può visualizza i dispositivi accoppiati
                    showToast("Accendi il bluetooth per visualizzare i dispositivi accoppiati");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    //bluetooth is on
                    iconBT.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth acceso");
                } else {
                    //l'utente non ha acconsentito ad accendere il bluetooth
                    showToast("Non è stato possibile accendere il bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //toast message function
    private void showToast(String msg){
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT ).show();

    }
}
