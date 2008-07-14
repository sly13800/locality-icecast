package se.slackers.locality.media.queue;

import se.slackers.locality.data.FrameStorage;
import se.slackers.locality.model.Media;

public interface MediaQueue {
	
	/**
	 * Returns the name of the MediaQueue
	 * @return The name of the MediaQueue
	 */
	public String getName();
	
	public void add(Media media);
	public Media get(int index);
	public void remove(int index);
	public int size();	
	
	public void setMountPoint(String mountPoint);
	public String getMountPoint();	
	
	public void startProcessor();
	public void stopProcessor();
	public boolean isProcessorRunning();	
	
	public MediaQueueProcessor getMediaQueueProcessor();
	public void setMediaQueueProcessor(MediaQueueProcessor queueProcessor);
	
	public FrameStorage getFrameStorage();
	public void setFrameStorage(FrameStorage frameStorage);
}
