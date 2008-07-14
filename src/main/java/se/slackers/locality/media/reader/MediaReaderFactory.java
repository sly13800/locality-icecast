package se.slackers.locality.media.reader;

import java.util.ArrayList;
import java.util.List;

import se.slackers.locality.exception.CantCreateMediaReaderException;
import se.slackers.locality.model.Media;

public class MediaReaderFactory {
	private List<MediaReader> mediaReaders = new ArrayList<MediaReader>();
	
	public void addMediaReader(MediaReader mediaReader) {
		mediaReaders.add(mediaReader);
	}
	
	/**
	 * Creates a MediaReader that can handle the given media file.
	 * @throws CantCreateMediaReaderException
	 * @param media
	 * @return
	 */
	public MediaReader getMediaReader(Media media) {
		for (MediaReader reader : mediaReaders) {
			if (reader.supports(media)) {
				try {
					return (MediaReader) reader.getClass().newInstance();
				} catch (InstantiationException e) {
					throw new CantCreateMediaReaderException(e);					
				} catch (IllegalAccessException e) {
					throw new CantCreateMediaReaderException(e);
				}
			}
		}
		throw new CantCreateMediaReaderException("No supported reader found for the media file ["+media.getMediaFile()+"]");
	}
}
