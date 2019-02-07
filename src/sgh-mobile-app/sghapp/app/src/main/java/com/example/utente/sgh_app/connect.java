package com.example.utente.sgh_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Set;

public class connect extends AppCompatActivity {
    BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        if(btAdapter.isEnabled()) {
            Set<BluetoothDevice> devices = btAdapter.getBondedDevices();

            for (BluetoothDevice device : devices) {
                paired.append("\nDevice " + device.getName());
            }
        }

    }
}
