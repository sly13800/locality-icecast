package se.slackers.locality.media.reader;

import java.io.IOException;

import se.slackers.locality.media.Frame;
import se.slackers.locality.model.Media;
import se.slackers.locality.model.Metadata;

public interface MediaReader {
	/**
	 * Returns true if this reader supports the format of the given media.
	 * @param media
	 * @return
	 */
	public boolean supports(Media media);
	
	/**
	 * Opens the media file.
	 * @param media
	 * @throws IOException
	 */
	public void open(Media media) throws IOException ;
	
	/**
	 * Reads one frame of the media.
	 * @param frame
	 * @return
	 * @throws IOException
	 */
	public Frame readFrame(Frame frame) throws IOException;
	
	/**
	 * Closes the media.
	 * @throws IOException
	 */
	public void close() throws IOException;
	
	/**
	 * Returns true if the end of the media is reached.
	 * @return
	 */
	public boolean eof();
	
	/**
	 * Returns information about the media, artist, title. This function can be
	 * called multiple times but it only needs to have valid metadata after open has
	 * been called. It must never return null.
	 * @return
	 */
	public Metadata getMetadata();
}
