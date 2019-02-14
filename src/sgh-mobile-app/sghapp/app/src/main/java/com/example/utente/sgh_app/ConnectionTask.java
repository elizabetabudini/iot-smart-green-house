package com.example.utente.sgh_app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectionTask extends AsyncTask<Void, Void, Void> {
    private BluetoothSocket socket = null;

    public ConnectionTask(BluetoothDevice server, UUID uuid) {
        try {
            //socket = server.createRfcommSocketToServiceRecord(uuid);
            socket = server.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e("ModuloAndroid", "Non sono uscito dal costruttore.");
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            socket.connect();
            if(socket.isConnected()) {
                Log.i("ModuloAndroid", "Sono connesso.");
            } else {
                Log.i("ModuloAndroid", "Non sono connesso.");
            }
        } catch (IOException connectException) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("ModuloAnd IOException", e.getMessage());
            }
            Log.e("ModuloAnd", connectException.getMessage());
            return null;
        }
        ConnectionManager cm = ConnectionManager.getInstance();
        cm.setChannel(socket);
        cm.start();
        cm.write("connesso\n".getBytes());
        Log.i("ModuloAnd Task", "Ho avviato il ConnectionManager.");

        return null;
    }

    public boolean getConnectionState() {
        return this.socket.isConnected();
    }
}
