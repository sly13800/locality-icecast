package se.slackers.locality.data;

import se.slackers.locality.media.Frame;

/**
 * Immutable class that wraps a frame with start and stop times.
 * @author bysse
 *
 */
public class FrameStorageEntry implements Comparable<FrameStorageEntry> {
	private long startTime;
	private long stopTime;
	private Frame frame;

	public FrameStorageEntry(long time, Frame frame) {
		this.startTime = time;
		this.stopTime = time + frame.getLength();
		this.frame = frame;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getStopTime() {
		return stopTime;
	}

	public Frame getFrame() {
		return frame;
	}
	
	/**
	 * This implementation considers overlapping intervals to be equal.
	 */	
	public int compareTo(FrameStorageEntry o) {		
		if (stopTime < o.startTime)
			return -1;
		
		if (startTime >= o.stopTime)
			return 1;
		
		return 0;
	}
	
	@Override
	public String toString() {
		return "Spans from "+getStartTime()+" to "+getStopTime() +" ("+getFrame()+")";
	}
}
