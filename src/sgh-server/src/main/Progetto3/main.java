package Progetto3;
import java.io.IOException;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import io.vertx.core.Vertx;

public class main {

    public main() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) throws IOException {
        final GreenHouse gh;
        final GreenHouseController ghc;
        ESP esp = new ESP();
        ObservablePump pump = new ObservablePump();
        MsgService msgService;
        

		Vertx vertx = Vertx.vertx();
		DataService service = new DataService(8080,esp);
		
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
        msgService = new MsgService(port,9600);
        gh = new GreenHouse(msgService,pump);
        ghc = new GreenHouseController(msgService,pump,esp);
        
        gh.start();
        msgService.init();
        vertx.deployVerticle(service);
        
        while(true){            
        }
    }

}
