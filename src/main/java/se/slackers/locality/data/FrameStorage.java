package se.slackers.locality.data;

import se.slackers.locality.exception.FrameHasNotBeenLoadedException;

public interface FrameStorage {

	/**
	 * Adds a frame to the FrameStorage. This method only adds the frame to the
	 * end of the storage. So adding out-of-order frames will cause error in
	 * playback.
	 * 
	 * @param entry
	 */
	public void add(FrameStorageEntry entry);

	/**
	 * Returns the frame that overlaps the given time. If the FrameStorage is
	 * empty {@link FrameStorageIsEmptyException} is be thrown. If no frame
	 * could be found for the specified time {@link FrameHasNotBeenLoadedException} or 
	 * {@link FrameIsTooOldException} is thrown.
	 * 
	 * @param time
	 * @return A FrameStorageEntry that overlapped the given time.
	 */
	public FrameStorageEntry find(long time);

	/**
	 * Removes all frames that doesn't overlap the given time.
	 * 
	 * @param time
	 */
	public void purgeUntil(long time);
	
	/**
	 * Clears the frame storage.
	 */
	public void clear();

	/**
	 * Returns the start time of the first frame. If the storage is empty
	 * {@link FrameStorageIsEmptyException} will be thrown.
	 * 
	 * @return Start time of first frame.
	 */
	public long getFirstFrameTime();

	/**
	 * Returns the end time of the last frame. If the storage is empty
	 * {@link FrameStorageIsEmptyException} will be thrown.
	 * 
	 * @return End time of the last frame in storage.
	 */
	public long getLastFrameTime();
}
