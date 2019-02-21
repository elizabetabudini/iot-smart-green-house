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
		
		String port = "/dev/ttyACM0"; /* replace with the name of the serial port */
		
        msgService = new MsgService(port,9600);


	Vertx vertx = Vertx.vertx();
	DataService service = new DataService(8081,esp,msgService);

        gh = new GreenHouse(msgService,pump);
        ghc = new GreenHouseController(msgService,pump,esp);
        
        msgService.init();
        gh.start();
        ghc.start();
        vertx.deployVerticle(service);
        
        while(true){            
        }
    }

}
