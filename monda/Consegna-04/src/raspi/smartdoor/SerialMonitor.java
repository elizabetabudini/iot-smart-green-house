package smartdoor;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Stack;

/**
 * Simple Serial Monitor, adaptation from:
 * http://playground.arduino.cc/Interfacing/Java.
 *
 */
public abstract class SerialMonitor implements SerialPortEventListener {
    private SerialPort serialPort;
    Stack<String> stack = new Stack<String>();

    /**
     * A BufferedReader which will be fed by a InputStreamReader converting the
     * bytes into characters making the displayed results codepage independent.
     */
    public BufferedReader input;

    /** The output stream to the port. */
    public OutputStream output;
    /** Milliseconds to block while waiting for port open. */
    private static final int TIME_OUT = 2000;

    /**
     * Metodo per avviare il monitor.
     * 
     * @param portName
     *            Porta su cui avviare il monitor.
     * @param dataRate
     *            DataRate della porta.
     */
    public void start(final String portName, final int dataRate) {
        CommPortIdentifier portId = null;

        try {
            portId = CommPortIdentifier.getPortIdentifier(portName);
            // Apri la porta seriale e usa il nome della classe come nome.
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
            int dataBit = SerialPort.DATABITS_8;
            int stopBit = SerialPort.STOPBITS_1;
            int parity = SerialPort.PARITY_NONE;
            // Setta i parametri della porta.
            serialPort.setSerialPortParams(dataRate, dataBit, stopBit, parity);

            // Apre lo stream
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception exception) {
            System.err.println(exception.toString());
        }
    }

    /**
     * Metodo da richiamare quando si smette di usare la porta. Previene
     * problemi di bloccaggio se si usa il software su Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Metodo per inviare un carattere ad Arduino sulla porta specificata.
     * 
     * @param charByte
     *            Carattere da inviare.
     * @throws InterruptedException
     *             Eccezione generata quando viene interrotto il collegamento.
     * @throws IOException
     *             Interruzione generica di IO.
     */
    public void send(final char charByte) throws InterruptedException, IOException {
        output.write(charByte);
        output.flush();
    }

    /**
     * Metodo per inviare dati ad Arduino sulla porta specificata.
     * 
     * @param stringByte
     *            Messaggio da inviare.
     * @throws InterruptedException
     *             Eccezione generata quando viene interrotto il collegamento.
     * @throws IOException
     *             Interruzione generica di IO.
     */
    public void send(final String stringByte) throws InterruptedException, IOException {
        output.write(stringByte.getBytes(Charset.forName("UTF-8")));
        output.flush();
    }

}