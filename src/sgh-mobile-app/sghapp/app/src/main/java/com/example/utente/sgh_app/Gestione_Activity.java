package com.example.utente.sgh_app;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Timer;

public class Gestione_Activity extends AppCompatActivity {

    TextView intensity_l;
    Button btn_ON, btn_OFF;

    private static gestioneActivityHandler uiHandler;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestione);
        intensity_l = findViewById(R.id.intensity_label);
        uiHandler = new gestioneActivityHandler(this);
        btn_ON = findViewById(R.id.btnON);
        btn_OFF = findViewById(R.id.btnON);
        btn_ON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Serra.getInstance().accendi_pompa();
            }
        });
        btn_OFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Serra.getInstance().spegni_pompa();
            }
        });


        final SeekBar bar = findViewById(R.id.seekBar);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                intensity_l.setText(getString(R.string.intensity_label_text) + ": " + bar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int intensity_v = bar.getProgress();

                if(Serra.getInstance().setPortata(intensity_v)) {
                    // Ho cambiato l'intensità
                    intensity_l.setText(getString(R.string.intensity_label_text) + ": " + intensity_v);
                    //intensity_t.setText(intensity_v);
                } else {
                    // Non ho cambiato l'intensità.
                    Log.e("ModuloAnd", "No value change.");
                }

            }
        });

        new ConnectedNotify(this).execute();

    }

    public void start(View v){
        Serra.getInstance().accendi_pompa();
    }
    public void end(View v){
        Serra.getInstance().spegni_pompa();
    }

    public void onStart() {
        super.onStart();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public static gestioneActivityHandler getHandler() {
        return uiHandler;
    }

    public static class gestioneActivityHandler extends Handler {
        private WeakReference<Gestione_Activity> context;

        private gestioneActivityHandler(Gestione_Activity context) {
            this.context = new WeakReference<>(context);
        }

        public void handleMessage(Message msg) {
            Object obj = msg.obj;
            if(obj instanceof String) {
                String message = obj.toString();
                switch(message) {
                    case "ManOut":
                        Log.w("ModuloAnd", "Sono dentro a exit.");
                        context.get().finish();
                        break;
                }
            }
        }

    }
    //toast message function
    private void showToast(String msg){
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT ).show();

    }
}



