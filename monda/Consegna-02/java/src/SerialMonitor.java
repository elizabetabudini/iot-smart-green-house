import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.swing.JLabel;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * Simple Serial Monitor, adaptation from:
 * 
 * http://playground.arduino.cc/Interfacing/Java
 *
 */
public class SerialMonitor implements SerialPortEventListener {
    SerialPort serialPort;
    String msg = "";
    TextArea log;
    JLabel dist;

    /**
     * A BufferedReader which will be fed by a InputStreamReader converting the
     * bytes into characters making the displayed results codepage independent
     */
    private BufferedReader input;
    /** The output stream to the port */
    public OutputStream output;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 2000;

    //When the monitor starts
    public void start(String portName, int dataRate, JLabel distance, TextArea txtLog) {
        CommPortIdentifier portId = null;

        try {
            portId = CommPortIdentifier.getPortIdentifier(portName);
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(dataRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            // input = new BufferedReader(new
            // InputStreamReader(serialPort.getInputStream()));
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            
            log = txtLog;
            dist = distance;

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }   

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
    
    //Send data to Arduino
    public void Send(String s) throws InterruptedException, IOException{
        Thread.sleep(50);
        output.write(s.getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                if (input.ready() && (msg=input.readLine()) != null){
                    if(msg.length()>15) {
                        dist.setText(msg);
                    }
                    else {
                        log.append(msg+"\n");
                    }
                    msg="";
                }


            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other
        // ones.
    }

}