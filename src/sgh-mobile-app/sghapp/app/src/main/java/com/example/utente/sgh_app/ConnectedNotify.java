package com.example.utente.sgh_app;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class ConnectedNotify extends AsyncTask {

    private volatile boolean stop = false;

    private TextView label;

    private WeakReference<MainActivity> ref;

    public WeakReference<MainActivity> getRef() {
        return ref;
    }

    public ConnectedNotify(MainActivity activity) {
        this.ref= new WeakReference<>(activity);
    }

    @Override
    protected Void doInBackground(Object... objs) {
        while(!stop){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Serra.getInstance().connetti();
        }
        return null;
    }

    public void stop(){
        stop = true;
    }
}


