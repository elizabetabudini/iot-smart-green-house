package Progetto3;
public class GreenHouseController extends BasicEventLoopController{
	private enum State {AUTO,MANUAL};
	private State state;
	private final ObservablePump pump;
	private final MsgService monitor;
	private final ESP esp;

	public GreenHouseController(MsgService monitor,ObservablePump pump,ESP esp){
		this.pump = pump;
		this.monitor = monitor;
		this.esp = esp;
		pump.addObserver(this);
		monitor.addObserver(this);
		esp.addObserver(this);
		
		state = State.AUTO;
	}
	
	@Override
	protected void processEvent(Event ev) {
		try {
			switch (state){
			    case MANUAL:
			      if (ev instanceof AutoMode && state != State.AUTO){
			        state = State.AUTO;
			        esp.setRegular();
			        log("AUTO MODE");
			      }
			      break;
			    case AUTO:
			      if (ev instanceof ManualMode && state != State.MANUAL){
			        state = State.MANUAL; 
			        esp.setManual();
			        log("MANUAL MODE");
				  } else if (ev instanceof AlarmPump){
			          pump.setOpen(((AlarmPump) ev).getU());
			          log("Pump Open");
				  } else if (ev instanceof DonePump){
					  pump.setClose();
			          log("Pump Close");
			      } else if (ev instanceof OvertimePump){
			    	  pump.overtimeClose();
			    	  esp.setRegular();
			          log("Overtime Pump Close");
			      }
			      break;
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}		
	}

	private void log(String msg){
		System.out.println("[GHCONTROLLER] "+msg);
	}
}
