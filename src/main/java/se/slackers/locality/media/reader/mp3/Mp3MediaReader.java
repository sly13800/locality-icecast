package se.slackers.locality.media.reader.mp3;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;

import se.slackers.locality.media.Frame;
import se.slackers.locality.media.reader.ByteStreamReader;
import se.slackers.locality.media.reader.MediaReader;
import se.slackers.locality.model.Media;
import se.slackers.locality.model.Metadata;
import se.slackers.locality.model.MetadataType;

/*
 * 128kbps 44.1kHz layer II uses a lot of 418 bytes and some of 417 bytes long.
 * Regardless of the bitrate of the file, a frame in an MPEG-1 file lasts for 26ms (26/1000 of a second). 
 */
public class Mp3MediaReader implements MediaReader {
	private static final Logger log = Logger.getLogger(Mp3MediaReader.class);
	
	private ByteStreamReader reader = null;
	private Mp3FrameHeader header = new Mp3FrameHeader();
	private Metadata metadata = null;
	
	public Mp3MediaReader() {
		super();
		
		metadata = Metadata.create("Unknown", "", "");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean supports(Media media) {	
		return media.getMediaFile().toString().toLowerCase().endsWith(".mp3");
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws IOException {
		if (reader != null) {
			reader.getInputStream().close();
			reader.setInputStream(null);
			reader = null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void open(Media media) throws IOException {
		if (reader != null) {
			close();
		}

		reader = new ByteStreamReader();
		reader.setInputStream(new FileInputStream(media.getMediaFile()));
		
		// try to read some id3 tags from the media
		try {
			AudioFile audioFile = AudioFileIO.read(media.getMediaFile());
			Tag tag = audioFile.getTag();
			
			metadata.set(MetadataType.ARTIST, tag.getFirstArtist() );
			metadata.set(MetadataType.ALBUM, tag.getFirstAlbum() );
			metadata.set(MetadataType.TITLE, tag.getFirstTitle() );
			
			log.info("Opening "+metadata);
		} catch (Exception e) {
			log.error("Can't read tag from "+media);
			e.printStackTrace();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public boolean eof() {
		try {
			return reader.getInputStream().available() <= 0;
		} catch (IOException e) {
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Frame readFrame(Frame frame) throws IOException {
		Mp3FrameHeader header = findMp3Header();
		//log.info(header.toString());

		frame.setSize(header.getFrameSize());
		frame.setLength(26);
		
		System.arraycopy(header.getData(), 0, frame.getData(), 0, 4);		
		reader.read(frame.getData(), 4, (int)frame.getSize()-4);

		return frame;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Metadata getMetadata() {
		return metadata;
	}	

	private Mp3FrameHeader findMp3Header() throws IOException {
		byte lastByte = 0;
		byte currentByte = 0;

		while (true) {
			lastByte = currentByte;
			currentByte = reader.read();
			// Check for the start of the Mp3 Frame Header 
			if (lastByte == (byte) 0xff && (currentByte & 0xE0) == 0xE0) {
				header.setOffset(reader.getOffset() - 2);
				header.setData(lastByte, currentByte, reader.read(), reader.read());
				
				//log.info("Found frame start at index [" + (header.getOffset())
				//			+ "]");
				return header;
			}
		}
	}
}
