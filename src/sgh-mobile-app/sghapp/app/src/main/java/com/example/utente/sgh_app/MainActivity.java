package com.example.utente.sgh_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView tv_statusBluetooth, tv_elenco_dispositivi;
    ImageView iv_iconBT;
    Button btn_ricercaDispositivi;
    Switch switch_bluetooth;

    BluetoothAdapter btAdapter;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_statusBluetooth = findViewById(R.id.switchBluetooth);
        tv_elenco_dispositivi = findViewById(R.id.tv_elenco_dispositivi);
        btn_ricercaDispositivi = findViewById(R.id.btn_ricerca);
        iv_iconBT = findViewById(R.id.imageBluetooth);
        switch_bluetooth= findViewById(R.id.switchBluetooth);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            showToast("Questo dispositivo non supporta il bluetooth");
        }

        //set image bluetooth
        if(btAdapter.isEnabled()){
            iv_iconBT.setImageResource(R.drawable.ic_action_on);
            switch_bluetooth.setChecked(true);
            switch_bluetooth.setText(R.string.switch_status_on);

        } else {
            iv_iconBT.setImageResource(R.drawable.ic_action_off);
            switch_bluetooth.setChecked(false);
            switch_bluetooth.setText(R.string.switch_status_off);
        }

        switch_bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!btAdapter.isEnabled()){
                        showToast("Accendo il bluetooth");
                        //intent che accende
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BT);
                    } else {
                        showToast("Bluetooth già acceso");
                    }
                }else{
                    if(btAdapter.isEnabled()){
                        btAdapter.disable();
                        showToast("Bluetooth spento");
                        iv_iconBT.setImageResource(R.drawable.ic_action_off);
                        switch_bluetooth.setText(R.string.switch_status_off);
                    } else {
                        showToast("Il bluetooth è già spento");
                    }

                }
            }
        });

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        //discover button click
        btn_ricercaDispositivi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!btAdapter.isDiscovering()){
                    showToast("Inizio ricerca");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                    //startActivityForResult(intent, REQU);
                    btAdapter.getScanMode();
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
                    iv_iconBT.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth acceso");
                    switch_bluetooth.setText(R.string.switch_status_on);
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
