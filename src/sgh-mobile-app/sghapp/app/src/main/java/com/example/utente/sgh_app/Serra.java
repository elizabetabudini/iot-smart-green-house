package com.example.utente.sgh_app;

import android.bluetooth.BluetoothDevice;
import java.util.UUID;

public class Serra {

    // Campi per l'utilizzo del bluetooth.
    // UUID: Identificatore Univoco Applicazione Bluetooth.
    private ConnectionTask task;

    private static Serra instance = null;
    private int portata = 0;

    public static Serra getInstance() {
        if(instance == null) {
            instance = new Serra();
        }
        return instance;
    }

    public boolean setConnection(final BluetoothDevice server, final String uuid) {
        try {
            UUID my_uuid = UUID.fromString(uuid);
            this.task = new ConnectionTask(server, my_uuid);
            this.task.execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean spegni_pompa() {
        if(!ConnectionManager.getInstance().getStreamSet()) {
            return false;
        }
        this.sendMessage("OFF\n");
        return true;
    }
    public boolean accendi_pompa() {
        if(!ConnectionManager.getInstance().getStreamSet()) {
            return false;
        }
        this.sendMessage("ON\n");
        return true;
    }

    public boolean setPortata(int value) {
        try {
            this.sendMessage(String.valueOf(value));
            portata = value;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public int getPortata() {
        return portata;
    }

    private void sendMessage(String message) {
        ConnectionManager.getInstance().write(message.getBytes());
    }

    public boolean getConnectionState() {
        return this.task.getConnectionState();
    }
}
