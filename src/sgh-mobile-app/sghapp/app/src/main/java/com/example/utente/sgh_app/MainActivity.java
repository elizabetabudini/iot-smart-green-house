package com.example.utente.sgh_app;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    // Identificatori delle richieste
    final int REQUEST_CONNECT_SERRA = 2;

    // Abilitazione Bluetooth
    private final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private final int REQUEST_ENABLE_BT = 1;
    final String DEVICE_NAME = "HC-06";

    private static MainActivityHandler uiHandler;
    Button btn_assoc;
    ImageView iv_iconBT;
    Switch switch_bluetooth;

    // UUID: Identificatore Univoco Applicazione Bluetooth Con Arduino
    private static final String APP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_iconBT = findViewById(R.id.imageBluetooth);
        switch_bluetooth= findViewById(R.id.switchBluetooth);
        uiHandler = new MainActivityHandler(this);

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

        // Verifico che l'utente possa usare il bluetooth
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            finish();
        } else {
            // Abilito il bluetooth.
            if (!adapter.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, REQUEST_ENABLE_BT);
            }
        }


        btn_assoc = findViewById(R.id.assoc_btn);
        btn_assoc.setEnabled(true);
        btn_assoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trovo il server giusto.
                BluetoothDevice device = null;
                //String dev_name = ((TextView) findViewById(R.id.device_name_text)).getText().toString().trim();

                try {
                    device = checkPairedDevice(DEVICE_NAME);
                } catch (NullPointerException e) {
                    showToast("Device "+ DEVICE_NAME +"not found");
                }
                if(device==null) {
                    showToast("Non è stato trovato nessun dispositivo associato con questo nome.");

                } else {
                    btn_assoc.setEnabled(false);
                    // Dico alla porta di creare una connessione.
                    if (Serra.getInstance().setConnection(device, APP_UUID)) {
                        // Settata la connessione
                        showToast("Connesso");
                        btn_assoc.setEnabled(false);
                        showToast("Attesa risposta connessione.");
                    } else {
                        showToast("Impossibile connettersi");
                    }
                }

            }
        });
    }


    @Override
    public void onActivityResult(int reqID, int res, Intent data) {

        if (reqID == REQUEST_CONNECT_SERRA) {
            Log.i("ModuloAnd Main", "Ricevuta risposta.");
            finish();
        }
    }

    //toast message function
    private void showToast(String msg){
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT ).show();

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
        Set<BluetoothDevice> pairedList = btAdapter.getBondedDevices();
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
                    case "vicino":
                        Intent i = new Intent(context.get(), Gestione_Activity.class);
                        context.get().startActivityForResult(i, context.get().REQUEST_CONNECT_SERRA);
                        break;
                    default:
                        break;
                }
            }
        }
    }

/*
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

    }*/
}
