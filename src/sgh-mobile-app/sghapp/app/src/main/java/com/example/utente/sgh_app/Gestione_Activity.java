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

import java.lang.ref.WeakReference;

public class Gestione_Activity extends AppCompatActivity {
    TextView logView;
    //EditText intensity_t;
    TextView intensity_l;

    private static InDoorActivityHandler uiHandler;

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
        uiHandler = new InDoorActivityHandler(this);

        Button btn_close = findViewById(R.id.btnOFF);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if(intensity_v < 0 || intensity_v > 3) {
                    logView.setText(R.string.intensity_value_err);
                } else {
                    if(Serra.getInstance().setPortata(intensity_v)) {
                        // Ho cambiato l'intensità.
                        logView.setText(R.string.change_intensity_ok);
                        intensity_l.setText(getString(R.string.intensity_label_text) + ": " + intensity_v);
                        //intensity_t.setText(intensity_v);
                    } else {
                        // Non ho cambiato l'intensità.
                        Log.e("ModuloAnd InDoor Bar", "No value change.");
                    }
                }
            }
        });

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

    public static InDoorActivityHandler getHandler() {
        return uiHandler;
    }

    public static class InDoorActivityHandler extends Handler {
        private WeakReference<Gestione_Activity> context;

        private InDoorActivityHandler(Gestione_Activity context) {
            this.context = new WeakReference<>(context);
        }

        public void handleMessage(Message msg) {
            Object obj = msg.obj;
            if(obj instanceof String) {
                String message = obj.toString();
                switch(message) {
                    //se clicca il bottone arduino
                    case "exit":
                        Log.w("ModuloAnd Handle InDoor", "Sono dentro a exit.");
                        context.get().setResult(RESULT_OK);
                        context.get().finish();
                        break;
                }
            }
        }

    }
}

