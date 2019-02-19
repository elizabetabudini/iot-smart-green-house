package com.example.utente.sgh_app;

import android.os.AsyncTask;

public class ConnectedNotify extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        while(true){
            Serra.getInstance().connetti();
        }
    }
}
