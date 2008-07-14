package se.slackers.locality.media.queue;

import se.slackers.locality.model.Media;
import se.slackers.locality.model.Metadata;

public interface MediaQueueProcessorListener {
	
	/**
	 * Called when the MediaQueue advances to the next Media in queue.
	 * @param media The media that will be played.
	 * @param metadata The metadata for the media
	 */
	public void nextMedia(Media media, Metadata metadata);
}
