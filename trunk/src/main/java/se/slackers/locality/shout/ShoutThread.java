package se.slackers.locality.shout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ShoutThread extends Thread implements Runnable {
	private static Logger log = Logger.getLogger(ShoutThread.class);

	private Object targetLock = new Object();

	protected List<ShoutThreadListener> listeners = new ArrayList<ShoutThreadListener>();
	protected ShoutRunnable target = null;
	protected boolean exit = false;
	
	public ShoutThread(String name) {
		super(name);
	}

	public void run() {
		log.debug("Thread "+this.getName()+" is started");
		
		while (!isExit()) {
			synchronized (targetLock) {
				try {
					log.debug(this.getName()+": Waiting for task");
					targetLock.wait();
				} catch (InterruptedException e) {
					log.error("Error when waiting for lock");
					log.error(e);
					continue;
				}
			}
			
			assert target != null : "The ShoutRunnable is null for thread "+this.getName();
			
			try {
				log.debug(this.getName()+": Running task");
				target.run();
			} catch(Exception e) {
				log.error("Thread "+this.getName()+" was aborted due to an exception", e);
			}
			
			synchronized(targetLock) {
				target = null;
			}
			
			log.debug(this.getName()+": Task complete, notifying listeners");
			notifyListeners();
		}
		
		log.debug("Thread "+this.getName()+" is stopped");
	}
	
	public void addShoutThreadListener(ShoutThreadListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners() {
		for (ShoutThreadListener listener : listeners) {
			listener.shoutThreadComplete(this);
		}		
	}

	public synchronized boolean isExit() {
		return exit;
	}

	public synchronized void setExit(boolean exit) {
		this.exit = exit;
		
		synchronized(targetLock) {
			if (target != null) {
				target.setExit(exit);
			}
		}
	}

	public void setTarget(ShoutRunnable target) {
		synchronized(targetLock) {
			this.target = target;
			targetLock.notifyAll();
		}
	}

}
