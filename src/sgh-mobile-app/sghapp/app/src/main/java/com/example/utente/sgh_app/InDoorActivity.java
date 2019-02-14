package com.smartdoor.moduloandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class InDoorActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_in_door);
        logView = findViewById(R.id.log_text);
        //intensity_t = findViewById(R.id.intensity_text);
        intensity_l = findViewById(R.id.intensity_label);
        uiHandler = new InDoorActivityHandler(this);

        Button btn_close = findViewById(R.id.close_btn);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Door.getInstance().close();
            }
        });

        final SeekBar bar = (SeekBar)findViewById(R.id.seekBar);
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
                if(intensity_v < 0 || intensity_v > 100) {
                    logView.setText(R.string.intensity_value_err);
                } else {
                    if(Door.getInstance().setIntensity(intensity_v)) {
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
        private WeakReference<InDoorActivity> context;

        private InDoorActivityHandler(InDoorActivity context) {
            this.context = new WeakReference<>(context);
        }

        public void handleMessage(Message msg) {
            Object obj = msg.obj;
            if(obj instanceof String) {
                String message = obj.toString();
                switch(message) {
                    //TODO: Quando non si fa vedere dal pir o se clicca il bottone arduino o se clicca sul bottone android.
                    case "exit":
                        Log.w("ModuloAnd Handle InDoor", "Sono dentro a exit.");
                        context.get().setResult(RESULT_OK);
                        context.get().finish();
                        break;
                    //TODO: Quando arriva il messaggio di intensita' o di temperatura
                    default:
                        // TODO:Switchare tra temperatura e intensita'
                        TextView tlabel = context.get().findViewById(R.id.temperature_label);
                        tlabel.setText(context.get().getString(R.string.temperature_label_text) + message);
                        break;
                }
            }
        }

    }
}

