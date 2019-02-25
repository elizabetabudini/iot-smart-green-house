package com.example.sgh_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothChannel btChannel;
    private SeekBar seekBar;
    private BluetoothAdapter btAdapter;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null && !btAdapter.isEnabled()){
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), C.bluetooth.ENABLE_BT_REQUEST);
        }

        initUI();
    }

    private void initUI() {
        seekBar= findViewById(R.id.seekBar);
        findViewById(R.id.btn_ON).setEnabled(false);
        findViewById(R.id.btn_OFF).setEnabled(false);
        findViewById(R.id.seekBar).setEnabled(false);
        findViewById(R.id.connectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btAdapter != null && !btAdapter.isEnabled()){
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), C.bluetooth.ENABLE_BT_REQUEST);
                }
                try {
                    connectToBTServer();
                    //new notify().execute();

                } catch (BluetoothDeviceNotFound bluetoothDeviceNotFound) {
                    bluetoothDeviceNotFound.printStackTrace();
                }
            }
        });

        findViewById(R.id.btn_OFF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btChannel != null){
                    btChannel.sendMessage("2");
                }

            }
        });

        findViewById(R.id.btn_ON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btChannel != null){
                    btChannel.sendMessage("1");
                }
            }
        });

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(btChannel != null){
                    int portata = seekBar.getProgress()+3;
                    String message = String.valueOf(portata);
                    btChannel.sendMessage(message);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(btChannel != null){
            btChannel.close();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if(requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_OK){
            Log.d(C.APP_LOG_TAG, "Bluetooth enabled!");
        }

        if(requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_CANCELED){
            Log.d(C.APP_LOG_TAG, "Bluetooth not enabled!");
        }
    }


    private void connectToBTServer() throws BluetoothDeviceNotFound {
        final BluetoothDevice serverDevice = BluetoothUtils.getPairedDeviceByName(C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME);
        final UUID uuid = BluetoothUtils.generateUuidFromString(C.bluetooth.BT_SERVER_UUID);

        AsyncTask<Void, Void, Integer> execute = new ConnectToBluetoothServerTask(serverDevice, uuid, new ConnectionTask.EventListener() {
            @Override
            public void onConnectionActive(final BluetoothChannel channel) {

                ((TextView) findViewById(R.id.statusLabel)).setText(String.format("> STATO: Connesso alla serra %s",
                        serverDevice.getName()));

                findViewById(R.id.connectBtn).setEnabled(false);
                btChannel = channel;
                btChannel.registerListener(new RealBluetoothChannel.Listener() {
                    @Override
                    public void onMessageReceived(String receivedMessage) {

                        if(receivedMessage.equals("ManIn")){
                            ((TextView) findViewById(R.id.statusLabel)).setText("> STATO: Modalità manuale");
                            findViewById(R.id.btn_ON).setEnabled(true);
                            findViewById(R.id.btn_OFF).setEnabled(true);
                            findViewById(R.id.seekBar).setEnabled(true);
                        } else if(receivedMessage.equals("ManOut")){
                            ((TextView) findViewById(R.id.statusLabel)).setText("> STATO: Modalità automatica");
                            findViewById(R.id.btn_ON).setEnabled(false);
                            findViewById(R.id.btn_OFF).setEnabled(false);
                            findViewById(R.id.seekBar).setEnabled(false);

                        } else if(receivedMessage.equals("Start")){
                            ((TextView) findViewById(R.id.statusLabel)).setText("> STATO: Irrigando");
                            findViewById(R.id.btn_ON).setEnabled(false);
                            findViewById(R.id.btn_OFF).setEnabled(true);
                            findViewById(R.id.seekBar).setEnabled(false);
                        } else if(receivedMessage.equals("Stop")){
                            ((TextView) findViewById(R.id.statusLabel)).setText("> STATO: Terminato irrigazione");
                            findViewById(R.id.btn_ON).setEnabled(true);
                            findViewById(R.id.btn_OFF).setEnabled(false);
                            findViewById(R.id.seekBar).setEnabled(true);
                        } else{
                            ((TextView) findViewById(R.id.chatLabel)).setText((String.format("> UMIDITA: percepita in real-time %s",
                                    receivedMessage)));
                            ((TextView) findViewById(R.id.chatLabel)).append("%");
                        }
                    }

                    @Override
                    public void onMessageSent(String sentMessage) {
                    }
                });
            }

            @Override
            public void onConnectionCanceled() {
                ((TextView) findViewById(R.id.statusLabel)).setText(String.format("Status : unable to connect, device %s not found!",
                        C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME));
            }
        }).execute();

        new AsyncTask<Void, Void, Void>(){
            protected Void doInBackground(Void... voids) {
                while (!isCancelled()) {
                    if(btChannel != null){
                        btChannel.sendMessage("6");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
                return null;
            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
        }.execute();


    }
}
