package se.slackers.locality.media.queue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import se.slackers.locality.data.FrameStorage;
import se.slackers.locality.model.Media;

public class MediaQueueImpl implements MediaQueue {
	private static final Logger log = Logger.getLogger(MediaQueueImpl.class);
	
	private String mountPoint;	
	private String queueName;
	
	private List<Media> queue;
	
	private Semaphore startStop = new Semaphore(1);

	private MediaQueueProcessor processor = null;
	private FrameStorage frameStorage = null;
	private Thread processorThread = null;
	
	public MediaQueueImpl(String queueName) {
		this.queueName = queueName;
		queue = Collections.synchronizedList(new LinkedList<Media>());		
	}
	
	public MediaQueueProcessor getMediaQueueProcessor() {
		return processor;
	}

	/**
	 * Sets the MediaQueueProcessor to be used by the media queue. This method
	 * ensures that the reverse dependency is correct.
	 */
	public void setMediaQueueProcessor(MediaQueueProcessor processor) {
		this.processor = processor;
		
		if (this.processor.getMediaQueue() != this) {
			this.processor.setMediaQueue(this);
		}
	}

	public FrameStorage getFrameStorage() {
		return frameStorage;
	}

	public void setFrameStorage(FrameStorage frameStorage) {
		this.frameStorage = frameStorage;
	}

	/**
	 * Add media to the queue.
	 * @param media
	 */
	public void add(Media media) {
		queue.add(media);
	}
	
	/**
	 * Returns media from a certain position in the queue.
	 */
	public Media get(int index) {
		return queue.get(index);
	}
	
	public void remove(int index) {
		if (!queue.isEmpty()) {		
			queue.remove(index);
		}
	}
	
	public int size() {
		return queue.size();
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	public synchronized boolean isProcessorRunning() {
		if (processorThread == null)
			return false;
		
		return processorThread.isAlive();
	}

	public synchronized void startProcessor() {
		log.info("Waiting to start processor ["+(processorThread == null || processorThread.isAlive() == false)+"]");
		startStop.acquireUninterruptibly();		
		log.info("Starting processor ["+(processorThread == null || processorThread.isAlive() == false)+"]");
		
		if (processorThread == null || processorThread.isAlive() == false) {
			processor.init();
			processorThread = new Thread(processor, "QueueProcessor["+getMountPoint()+"]");
			processorThread.start();
		}		
	}

	public void stopProcessor() {		
/*
		if (isProcessorRunning()) {
			processor.stopProcessing();
			processor.deinit();
			processorThread = null;
		}
*/
		log.info("Releasing startStop lock");
		startStop.release();
	}

	public String getName() {
		return queueName;
	}
}
