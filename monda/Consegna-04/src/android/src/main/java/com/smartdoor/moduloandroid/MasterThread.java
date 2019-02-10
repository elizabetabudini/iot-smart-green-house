package com.smartdoor.moduloandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by daniele.tentoni2 on 06/02/2018.
 */

public class MasterThread extends Thread {
    private BluetoothAdapter adapter;
    private BluetoothServerSocket server = null;

    private boolean connectionAccepted = false;
    private boolean stop = false;

    public MasterThread(UUID uuid, String name) {
        adapter = BluetoothAdapter.getDefaultAdapter();

        try {
            server = adapter.listenUsingRfcommWithServiceRecord(name, uuid);
        } catch (IOException e) {
            // ...
        }
    }

    public void run() {
        BluetoothSocket socket = null;

        while(!connectionAccepted && !stop) {
            try {
                socket = server.accept();
            } catch (IOException e) {
                stop = true;
            }
            if(socket != null) {
                connectionAccepted = true;
                ConnectionManager cm = ConnectionManager.getInstance();
                cm.setChannel(socket);
                cm.start();

                try {
                    server.close();
                } catch (IOException e) {
                    // ...
                }

            }
        }
    }
}
