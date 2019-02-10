package smartdoor;

import java.io.IOException;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPortEvent;

public class SmartDoor {

    public SmartDoor() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) throws IOException {
        final Door door = new Door();

        /*
         * Mi trovo tutte le porte su cui si trova un Arduino e la apro.
         */
        String port = "";
        Enumeration<?> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portIdentifiers.nextElement();
            port = currPortId.getName();
            break;
        }

        door.start(port, 9600);
        
        while(true){
            
        }
    }

}
