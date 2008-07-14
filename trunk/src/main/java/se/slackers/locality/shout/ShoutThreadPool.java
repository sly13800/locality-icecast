package se.slackers.locality.shout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ShoutThreadPool implements ShoutThreadListener {
	private static Logger log = Logger.getLogger(ShoutThreadPool.class);
	
	private List<ShoutThread> available = new ArrayList<ShoutThread>();

	private List<ShoutThread> running = new ArrayList<ShoutThread>();

	public ShoutThreadPool(int size, String prefix) {
		log.debug("Starting threads");
		for (int i = 0; i < size; i++) {
			ShoutThread thread = new ShoutThread(prefix + i);
			thread.addShoutThreadListener(this);
			thread.start();
			available.add(thread);
		}
	}

	public synchronized void shutdown() {
		for (ShoutThread thread : available) {
			thread.setExit(true);
			thread.interrupt();
		}

		for (ShoutThread thread : running) {
			thread.setExit(true);
			thread.interrupt();
		}
	}

	public synchronized void shoutThreadComplete(ShoutThread thread) {
		log.debug("Recycling thread "+thread.getName());
		running.remove(thread);
		available.add(thread);
	}

	public synchronized void startShoutRunnable(ShoutRunnable runnable) {
		if (available.isEmpty()) {
			throw new RuntimeException("No available threads in pool");
		}

		ShoutThread thread = available.remove(0);
		running.add(thread);
		
		log.debug("Using thread "+thread.getName()+" for execution");
		
		thread.setExit(false);
		thread.setTarget(runnable);
	}

}
