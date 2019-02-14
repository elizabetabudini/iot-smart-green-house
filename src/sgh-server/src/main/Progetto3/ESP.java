package Progetto3;
public class ESP extends Observable{
	private enum State{REGULAR,IRRIGATION};
	private State state;
	private final int Umin = 30;
	private final int delta = 5;
	
	public ESP() {
		state = State.REGULAR;
	}
	
	public void checkMin(int U) {
		switch(state) {
		case REGULAR:
			if(U < Umin) {
				this.notifyEvent(new AlarmPump(U));
				state = State.IRRIGATION;
			}
			break;
		case IRRIGATION:
			if(U > (Umin+delta)) {
				this.notifyEvent(new DonePump());
				state = State.REGULAR;
			}
			break;
		}
	}
	
	public void Overtime() {
		this.state = state.REGULAR;
	}
}
