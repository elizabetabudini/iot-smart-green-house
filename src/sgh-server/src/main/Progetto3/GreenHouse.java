package Progetto3;
import gnu.io.SerialPortEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

/**
 * Radar montato su Arduino, dotato di posizione e stato.
 */
public class GreenHouse extends BasicEventLoopController{
	private MsgService monitor;
	private ObservablePump pump;

    /**
     * Costruttore che inizializza il radar allo stato di IDLE.
     * 
     * @throws IOException
     */
    public GreenHouse(final MsgService monitor,final ObservablePump pump) throws IOException {   	
    	save("[INITIALIZING]");
        
        this.pump = pump;
        this.monitor = monitor;
        monitor.addObserver(this);
        pump.addObserver(this);
    }
    
    
    @Override
	protected void processEvent(Event ev) {
		try {
			if(ev instanceof MsgEvent) {
				if(((MsgEvent) ev).getMsg().equals("Start")) {
					save("[PUMP START]");
				} else if(((MsgEvent) ev).getMsg().equals("Stop")) {
					save("[PUMP STOP]");
				} else if(((MsgEvent) ev).getMsg().equals("StopT")) {
					save("[PUMP STOP OVERTIME]");
		            monitor.notifyEvent(new OvertimePump());
				} else if(((MsgEvent) ev).getMsg().equals("ManIn")) {
					save("[MANUAL MODE START]");
		            monitor.notifyEvent(new ManualMode());
				} else if(((MsgEvent) ev).getMsg().equals("ManOut")) {
		            save("[AUTO MODE START]");
		            monitor.notifyEvent(new AutoMode());
				}
			} else if (ev instanceof StartPump) {
				monitor.sendMsg(((StartPump) ev).getMessage());
			} else if (ev instanceof StopPump) {
				monitor.sendMsg("Stop");
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}		
	}
    
    private void save(String msg) throws IOException {
		final BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/home/tumore/Documents/log.txt"), true));
        bw.append(Instant.now() + " " + msg + "\n");
        bw.close();
	}

}
