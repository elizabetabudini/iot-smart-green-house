package smartdoor;

import gnu.io.SerialPortEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Radar montato su Arduino, dotato di posizione e stato.
 */
public class Door {
    private SerialMonitor monitor;
    static final int MAX_ACCOUNT = 2;
    boolean logged = false;
    final Light ledLog = new Led(0);
    final Light ledErr = new Led(2);
    BufferedWriter bw;


    /**
     * Costruttore che inizializza il radar allo stato di IDLE.
     * 
     * @throws IOException
     */
    public Door() throws IOException {
        String[] account = new String[MAX_ACCOUNT];
        String[] passwords = new String[MAX_ACCOUNT];
        int i = 0;
        String r;
        ledLog.switchOff();
        ledErr.switchOff();
        Log.getLog().logReset("");
        
        final BufferedReader br = new BufferedReader(new FileReader(new File("account.txt")));
        while ((r = br.readLine()) != null) {
            String splitted[] = r.split("/");
            account[i] = splitted[0];
            passwords[i] = splitted[1];
            i++;
        }
        
        bw = new BufferedWriter(new FileWriter(new File("/var/www/html/IoT/temp.txt")));
        bw.write("0");
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(new File("/var/www/html/IoT/int.txt")));
        bw.write("0");
        bw.close();

        this.monitor = new SerialMonitor() {

            /**
             * Handler per l'evento DataAvailable.
             */
            @Override
            public synchronized void serialEvent(final SerialPortEvent event) {
                String msg = null;
                String[] usr = null;
               
                if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                    try {
                        if (this.input.ready() && (msg = this.input.readLine()) != null) {
                            String s[] = msg.split("@");
                            String state = s[0];
                            String mess = s[1];
                            
                            switch(state) {
                                case "present":
                                    Log.getLog().log(mess);
                                    break;
                                case "account":
                                    usr = mess.split("/");
                                    for (int i = 0; i < account.length; i++) {
                                        if (account[i].equals(usr[0]) && passwords[i].equals(usr[1])) {
                                            Log.getLog().log("Accesso di " + usr[0]);
                                            logged = true;
                                            this.send("logok");
                                            break;
                                        }
                                    }
                                    if(!logged) {
                                        ledErr.pulse(100);
                                        Log.getLog().log("Log errato da parte di: " + usr[0]);
                                        this.send("logno");
                                    }
                                    break;
                                case "visto":
                                    ledLog.switchOn();
                                    Log.getLog().log(mess);
                                    break;
                                case "temp":
                                    bw = new BufferedWriter(new FileWriter(new File("/var/www/html/IoT/temp.txt")));
                                    bw.write(mess);
                                    bw.close();
                                    break;
                                case "int":
                                    bw = new BufferedWriter(new FileWriter(new File("/var/www/html/IoT/int.txt")));
                                    bw.write(mess);
                                    bw.close();
                                    break;
                                case "exitpir":
                                    ledErr.pulse(100);
                                case "exit":
                                    bw = new BufferedWriter(new FileWriter(new File("/var/www/html/IoT/int.txt")));
                                    bw.write("0");
                                    bw.close();
                                    bw = new BufferedWriter(new FileWriter(new File("/var/www/html/IoT/temp.txt")));
                                    bw.write("0");
                                    bw.close();
                                    Log.getLog().log(mess);
                                    ledLog.switchOff();
                                    logged = false;
                                    break;
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void start(final String portName, final int dataRate) {
        monitor.start(portName, dataRate);
    }

    public void close() {
        monitor.close();
    }

}
