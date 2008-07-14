package se.slackers.locality.shout.manager;

import se.slackers.locality.media.queue.MediaQueue;
import se.slackers.locality.net.HttpRequest;

/**
 * 
 * @author bysse
 *
 */
public interface ShoutRequestManager {
	/**
	 * Returns a MediaQueue object for the request or throws an exception.
	 * @param request
	 * @return
	 */
	public MediaQueue processRequest(HttpRequest request) throws SecurityException;
	
	/**
	 * Registers a new MediaQueue. 
	 * @param mediaQueue
	 */
	public void registerQueue(MediaQueue mediaQueue);
}
