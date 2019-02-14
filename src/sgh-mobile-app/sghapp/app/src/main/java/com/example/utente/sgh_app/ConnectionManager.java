package com.smartdoor.moduloandroid;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ConnectionManager extends Thread implements Runnable {
    private BluetoothSocket socket = null;
    private InputStream inStream;
    private OutputStream outStream;

    private boolean stop;

    private static ConnectionManager instance = null;

    private ConnectionManager() {
        stop = true;
    }

    public static ConnectionManager getInstance() {
        if(instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public void setChannel(BluetoothSocket socket) {
        this.socket = socket;
        Log.v("ModuloAnd SetChannel", "Ho settato la socket.");
        try {
            Log.v("ModuloAnd SetChannel", "Sto prendendo l'inputStream.");
            inStream = socket.getInputStream();
            Log.v("ModuloAnd SetChannel", "Sto prendendo l'outputStream.");
            outStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("ModuloAnd SetChannel", "Non ho settato il canale. Message: " + e.getMessage());
        }

        stop = false;
        Log.v("ModuloAndroid", "Ho settato il canale, stop è a false.");
    }

    public void run() {
        Log.i("ModuloAnd Run", "Sto eseguendo la run.");
        // byte[] buffer = new byte[1024];
        // int nBytes = 0;
        while(!stop) {
            try {
                StringBuilder buffer = new StringBuilder("");
                Log.i("ModuloAnd Run", "Message inizio a leggere");
                char c = (char) inStream.read();
                Log.i("ModuloAnd Run", "Ho letto il carattere " + c);
                if(c == '@') {
                    Log.i("ModuloAnd Run", "Inizio a leggere seriamente.");
                    char d;
                    do {
                        d = (char) inStream.read();
                        if(d != '@') {
                            buffer.append(d);
                        }
                    } while (d != '@');
                    Log.i("ModuloAnd Run", "Ho finito di leggere, adesso switcho: " + buffer + ".");
                    String result = buffer.toString();
                    if(result.trim().equals("pres") ||
                            result.trim().equals("logok") ||
                            result.trim().equals("logno") ||
                            result.trim().equals("visto") ||
                            result.trim().equals("ack")) {
                        Log.i("ModuloAnd Run", "Ho trovato il matchup MainActivity");
                        Message msg = new Message();
                        msg.obj = result;
                        MainActivity.getHandler().sendMessage(msg);
                    } else {
                        Log.i("ModuloAnd Run", "Ho trovato il matchup InDoorActivity");
                        Message msg = new Message();
                        msg.obj = result;
                        try {
                            InDoorActivity.getHandler().sendMessage(msg);
                        } catch (NullPointerException e) {
                            try {
                                MainActivity.getHandler().sendMessage(msg);
                            } catch (NullPointerException ex) {
                                Log.e("ModuloAnd Run", "NullPointerException: Messaggio errore: " + ex.getMessage() + ". Causa errore: " + ex.getCause());
                            } catch (Exception exe) {
                                Log.e("ModuloAnd Run", "Messaggio errore: " + exe.getMessage() + ". Causa errore: " + exe.getCause());
                            }
                        } catch (Exception exec) {
                            Log.e("ModuloAnd Run", "Messaggio errore: " + exec.getMessage() + ". Causa errore: " + exec.getCause());
                        }
                    }
                }
            } catch (IOException e) {
                stop = true;
                Log.e("ModuloAnd Run", "Sono finito in errore di lettura." + e.getMessage());
                if(this.getStreamSet()) {
                    Log.e("ModuloAnd Run", "Gli stream sono ancora attivi.");
                } else {
                    Log.e("ModuloAnd Run", "Gli stream non sono più attivi.");
                }
            }
        }
    }

    public boolean write(byte[] bytes) {
        if(outStream == null) {
            return false;
        }
        try {
            outStream.write(bytes);
            outStream.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void cancel() {
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // ...
            }
        }
    }

    public boolean getStreamSet() {
        return inStream != null && outStream != null;
    }

    @Override
    public String toString() {
        return String.valueOf(this.stop);
    }

}
