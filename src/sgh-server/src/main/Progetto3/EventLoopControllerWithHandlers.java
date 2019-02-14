package Progetto3;


import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class EventLoopControllerWithHandlers extends BasicEventLoopController {
	
	protected HashMap<Class,Handler> handlers;
	
	protected EventLoopControllerWithHandlers(int size){
		eventQueue = new ArrayBlockingQueue<Event>(size);
		handlers = new HashMap<>();
	}

	protected EventLoopControllerWithHandlers(){
		this(defaultEventQueueSize);
		setupHandlers();
	}
	
	abstract protected void setupHandlers();
	
	public EventLoopControllerWithHandlers addHandler(Class evc, Handler h) {
		handlers.put(evc, h);
		return this;
	}
	
	protected void processEvent(Event ev) {
		Handler hnd = handlers.get(ev.getClass());
		if (hnd != null) {
			hnd.handle(ev);
		}
	}
	
}
