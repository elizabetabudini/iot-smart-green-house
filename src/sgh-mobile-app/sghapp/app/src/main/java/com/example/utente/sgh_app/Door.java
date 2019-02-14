package com.smartdoor.moduloandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.UUID;

public class Door {

    // Campi per l'utilizzo del bluetooth.
    // UUID: Identificatore Univoco Applicazione Bluetooth.
    private ConnectionTask task;

    // Campi propri della smartdoor.
    private static Door instance = null;
    private DoorState state;
    private int intensity = 0;

    private Door() {
        this.state = DoorState.DOOR_CLOSE;
    }

    public static Door getInstance() {
        if(instance == null) {
            instance = new Door();
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

    //TODO: Trovare un modo di controllo.
    public boolean open(final String name, final String password) {
        if(!ConnectionManager.getInstance().getStreamSet()) {
            return false;
        }
        this.sendMessage(name + "/" + password);
        return true;
    }

    public boolean close() {
        if(!ConnectionManager.getInstance().getStreamSet()) {
            return false;
        }
        this.sendMessage("exit");
        this.state = DoorState.DOOR_CLOSE;
        return true;
    }

    public boolean setIntensity(int value) {
        try {
            this.sendMessage(String.valueOf(value));
            intensity = value;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public int getIntensity() {
        return intensity;
    }

    private void sendMessage(String message) {
        ConnectionManager.getInstance().write(message.getBytes());
    }

    public boolean getConnectionState() {
        return this.task.getConnectionState();
    }
}
