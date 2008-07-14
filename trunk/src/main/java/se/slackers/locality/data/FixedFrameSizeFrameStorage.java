package se.slackers.locality.data;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import se.slackers.locality.exception.FrameHasNotBeenLoadedException;
import se.slackers.locality.exception.FrameIsTooOldException;
import se.slackers.locality.exception.FrameStorageIsEmptyException;

/**
 * Threadsafe.
 * 
 * @author bysse
 * 
 */
public class FixedFrameSizeFrameStorage implements FrameStorage {
	private static final Logger log = Logger.getLogger(FixedFrameSizeFrameStorage.class);
	
	private LinkedList<FrameStorageEntry> frames = new LinkedList<FrameStorageEntry>();
	private long frameLength = 26; // MP3 frame length

	/**
	 * {@inheritDoc}
	 */
	public synchronized FrameStorageEntry find(long time) {
		if (frames.isEmpty()) {
			throw new FrameStorageIsEmptyException();
		}

		long firstFrameTime = frames.getFirst().getStartTime();
		long lastFrameTime = frames.getLast().getStopTime();

		// make sure the frame is within the represented interval
		if (lastFrameTime <= time) {
			//log.debug("Request: "+time+", LastFrame: "+lastFrameTime+", Diff: "+(time-lastFrameTime));			
			throw new FrameHasNotBeenLoadedException();
		}
		
		if (time < firstFrameTime) {
			throw new FrameIsTooOldException();
		}		

		int index = (int) ((time - firstFrameTime) / frameLength);

		return frames.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void add(FrameStorageEntry entry) {
		frames.add(entry);
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void purgeUntil(long time) {
		//log.debug("Purging framestorage until "+time);
		
		Iterator<FrameStorageEntry> iterator = frames.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().getStopTime() <= time) {
				iterator.remove();
			} else {
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized long getFirstFrameTime() {
		if (frames.isEmpty()) {
			throw new FrameStorageIsEmptyException();
		}
		
		return frames.getFirst().getStartTime();
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized long getLastFrameTime() {
		if (frames.isEmpty()) {
			throw new FrameStorageIsEmptyException();
		}
				
		return frames.getLast().getStopTime();
	}
	
	/**
	 * Returns the frame length that is used by the instance.
	 * @return The frame length in milliseconds
	 */
	public long getFrameLength() {
		return frameLength;
	}

	public void setFrameLength(long frameLength) {
		this.frameLength = frameLength;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void clear() {
		log.debug("Clearing frame storage");
		frames.clear();		
	}
}
