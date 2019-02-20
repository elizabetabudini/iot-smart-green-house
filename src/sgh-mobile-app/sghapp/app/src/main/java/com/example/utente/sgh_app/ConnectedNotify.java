package com.example.utente.sgh_app;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class ConnectedNotify extends AsyncTask {


    private WeakReference<Gestione_Activity> ref;

    public WeakReference<Gestione_Activity> getRef() {
        return ref;
    }

    public ConnectedNotify(Gestione_Activity activity) {
        this.ref= new WeakReference<>(activity);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        while(true){
            Serra.getInstance().connetti();
        }
    }
}
