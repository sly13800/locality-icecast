package se.slackers.locality.shout.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import se.slackers.locality.exception.IllegalRequestException;
import se.slackers.locality.media.queue.MediaQueue;
import se.slackers.locality.net.HttpRequest;

/**
 * Handles the mapping between a request and a MediaQueue.
 * @author bysse
 *
 */
public class ShoutRequestManagerImpl implements ShoutRequestManager {
	private static final Logger log = Logger
			.getLogger(ShoutRequestManagerImpl.class);

	private Map<String, MediaQueue> mountpoints = new HashMap<String, MediaQueue>();

	/**
	 * {@inheritDoc}
	 */
	public MediaQueue processRequest(HttpRequest request)
			throws SecurityException {		
		try {
			String path = request.getRequestPath();
			if (mountpoints.containsKey(path)) {
				return mountpoints.get(path);
			}
		} catch (IllegalRequestException e) {
			log.error("IllegalRequest ["+request+"]");
		}
		
		throw new SecurityException("The media queue "+request+" could not be found");
	}

	/**
	 * {@inheritDoc}
	 */	
	public void registerQueue(MediaQueue mediaQueue) {
		String mountPoint = mediaQueue.getMountPoint();
		
		if (mountpoints.containsKey(mountPoint)) {
			mountpoints.remove(mountPoint);
		}
		
		mountpoints.put(mountPoint, mediaQueue);
	}
}
