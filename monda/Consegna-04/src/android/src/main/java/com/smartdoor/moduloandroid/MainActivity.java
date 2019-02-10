package com.smartdoor.moduloandroid;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    // Identificatori delle richieste
    final int REQUEST_OPEN_DOOR = 2;

    // Abilitazione Bluetooth
    private final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private final int REQUEST_ENABLE_BT = 1;
    final String DEVICE_NAME = "isi21";

    private static MainActivityHandler uiHandler;

    TextView logView;
    Button btn_open, btn_assoc;

    // UUID: Identificatore Univoco Applicazione Bluetooth Con Arduino
    private static final String APP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logView = findViewById(R.id.log_text);
        uiHandler = new MainActivityHandler(this);

        // Verifico che l'utente possa usare il bluetooth
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            // Il bluetooth non è disponibile
            Log.e("Modulo Android", getString(R.string.no_bt_text));
            finish();
        } else {
            // Abilito il bluetooth.
            if (!adapter.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, REQUEST_ENABLE_BT);
            } else {
                logView.setText(R.string.yes_bt_text);
            }
        }

        btn_open = findViewById(R.id.open_btn);
        btn_open.setEnabled(false);
        btn_open.setVisibility(View.INVISIBLE);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Door.getInstance().getConnectionState()) {
                    String username = ((TextView) findViewById(R.id.name_text)).getText().toString().trim();
                    String password = ((TextView) findViewById(R.id.pass_text)).getText().toString().trim();
                    if (username.isEmpty() || password.isEmpty()) {
                        logView.setText(R.string.empty_user);
                    } else {
                        // Apertura della porta.
                        Door.getInstance().open(username, password);
                        /*
                        if(Door.getInstance().open(username, password)) {
                            inDoorActivityShow();
                        } else {
                            logView.setText(R.string.wrong_user);
                        }*/
                    }
                } else {
                    logView.setText(R.string.no_socket_bt_text);
                }
            }
        });

        btn_assoc = findViewById(R.id.assoc_btn);
        btn_assoc.setEnabled(true);
        btn_assoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trovo il server giusto.
                BluetoothDevice device = null;
                String dev_name = ((TextView) findViewById(R.id.device_name_text)).getText().toString().trim();
                if (dev_name.equals("")) {
                    String seq = "Inserire il nome di un dispositivo.";
                    Toast toast = Toast.makeText(getApplicationContext(), seq, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    try {
                        device = checkPairedDevice(dev_name);
                    } catch (NullPointerException e) {
                        logView.setText(R.string.no_bt_text);
                    }
                    if(device.equals(null)) {
                        String seq = "Non è stato trovato nessun dispositivo associato con questo nome.";
                        Toast toast = Toast.makeText(getApplicationContext(), seq, Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        btn_assoc.setEnabled(false);
                        // Dico alla porta di creare una connessione.
                        if (Door.getInstance().setConnection(device, APP_UUID)) {
                            // Settata la connessione
                            logView.setText(R.string.assoc_bt_text);
                            btn_open.setEnabled(true);
                            btn_assoc.setEnabled(false);
                            logView.setText("Attesa risposta connessione.");
                        } else {
                            logView.setText(R.string.no_assoc_bt_text);
                        }
                    }
                }
            }
        });
    }

    /**
     * Metodo per richiamare l'activity per l'autenticazione.
     */
    /*public void inDoorActivityShow() {
        Intent i = new Intent(this, InDoorActivity.class);
        startActivityForResult(i, REQUEST_OPEN_DOOR);
    }*/

    // TODO:Correggere questi messaggi di errore.
    @Override
    public void onActivityResult(int reqID, int res, Intent data) {
        //Risultati dell'activity del bluetooth.
        if (reqID == REQUEST_ENABLE_BT && res == Activity.RESULT_OK) {
            // Il bluetooth è abilitato.
            logView.setText(R.string.yes_bt_text);
            final Button btnOpen = findViewById(R.id.open_btn);
            btnOpen.setEnabled(true);
        } else if (reqID == REQUEST_ENABLE_BT && res == Activity.RESULT_CANCELED) {
            // Il processo è stato interrotto.
            logView.setText(R.string.ab_bt_text);
        }

        if (reqID == REQUEST_OPEN_DOOR) {
            Log.i("ModuloAnd Main", "Ricevuta risposta da InDoorActivity.");
            finish();
        }
        /*
        // Risultati dell'activity della porta.
        if(reqID == REQUEST_OPEN_DOOR) {
            if(!Door.getInstance().getConnectionState()){
                findViewById(R.id.open_btn).setEnabled(false);
                findViewById(R.id.assoc_btn).setEnabled(true);
            }
        }
        if (reqID == REQUEST_OPEN_DOOR && res == Activity.RESULT_OK) {
            if(data.getStringExtra("door").equals("Closed")) {
                logView.setText(R.string.door_close);
            } else {
                logView.setText((R.string.door_close_error));
            }
        } else if (reqID == REQUEST_OPEN_DOOR && res == Activity.RESULT_CANCELED) {
            if(data.getStringExtra("error").equals("WrongUsPs")) {
                logView.setText(R.string.wrong_user);
            } else if(data.getStringExtra("error").equals("EmptyUsPs")) {
                logView.setText(R.string.empty_user);
            } else if(data.getStringExtra("error").equals("NoDevice")) {
                logView.setText(R.string.default_error);
            } else {
                logView.setText(R.string.default_error);
            }
        } else if (reqID == REQUEST_OPEN_DOOR) {
            logView.setText(res);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectionManager.getInstance().cancel();
    }

    /**
     * Metodo per ottenere il device il cui nome corrisponde a quello inserito.
     *
     * @param deviceName Nome del device.
     * @return Istanza del device.
     */
    public BluetoothDevice checkPairedDevice(String deviceName) {
        // Cerco tra i dispositivi accoppiati se uno ha il nome giusto. Esso sarà il server.
        Set<BluetoothDevice> pairedList = adapter.getBondedDevices();
        if (pairedList.size() > 0) {
            for (BluetoothDevice elem : pairedList) {
                if (elem.getName().equals(deviceName)) {
                    return elem;
                }
            }
        }
        return null;
    }

    public static MainActivityHandler getHandler() {
        return uiHandler;
    }

    public static class MainActivityHandler extends Handler {
        private WeakReference<MainActivity> context;

        private MainActivityHandler(MainActivity context) {
            this.context = new WeakReference<>(context);
        }

        public void handleMessage(Message msg) {
            Object obj = msg.obj;
            if (obj instanceof String) {
                String message = obj.toString();
                switch (message) {
                    case "pres":
                        String seq = "Bentornato Inquilino!";
                        Toast toast = Toast.makeText(context.get(), seq, Toast.LENGTH_LONG);
                        toast.show ();
                        context.get().findViewById(R.id.name_text).setEnabled(true);
                        context.get().findViewById(R.id.pass_text).setEnabled(true);
                        context.get().btn_open.setEnabled(true);
                        context.get().btn_assoc.setEnabled(false);
                        context.get().btn_assoc.setVisibility(View.INVISIBLE);
                        context.get().btn_open.setVisibility(View.VISIBLE);
                        context.get().logView.setText(R.string.empty_user);
                        break;
                    case "ack":
                        context.get().logView.setText(R.string.wait_text);
                        break;
                    case "logok":
                        // TODO:Login corretto
                        context.get().logView.setText(R.string.logok_text);
                        break;
                    case "exit":
                        Log.e("ModuloAnd Handle Main", "Esco senza neanche entrare.");
                        context.get().finish();
                        break;
                    case "logno":
                        // TODO:Login incorretto
                        context.get().logView.setText(R.string.wrong_user);
                        break;
                    case "visto":
                        Intent i = new Intent(context.get(), InDoorActivity.class);
                        context.get().startActivityForResult(i, context.get().REQUEST_OPEN_DOOR);
                        break;
                    default:
                        // Login corretto
                        break;
                }
            }
        }
    }
}