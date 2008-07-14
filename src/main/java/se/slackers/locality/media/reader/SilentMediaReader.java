package se.slackers.locality.media.reader;

import java.io.IOException;

import se.slackers.locality.media.Frame;
import se.slackers.locality.model.Media;
import se.slackers.locality.model.Metadata;

public class SilentMediaReader implements MediaReader {
	
	byte [] emptyFrame = new byte[] {(byte) 0xff, (byte) 0xf2, 0x10, (byte) 0xc4, 0x1b, 0x27, 0x0, 0x0, 0x0, 0x3, (byte) 0xfc, 0x0, 0x0, 0x0, 0x0, 0x4c, 0x41, 0x4d, 0x45, 0x33, 0x2e, 0x39, 0x37, 0x0, 0x0, 0x0, (byte) 0xff, (byte) 0xf2, 0x10, (byte) 0xc4, 0x1b, 0x27, 0x0, 0x0, 0x0, 0x3, (byte) 0xfc, 0x0, 0x0, 0x0, 0x0, 0x4c, 0x41, 0x4d, 0x45, 0x33, 0x2e, 0x39, 0x37, 0x0, 0x0, 0x0};

	public void close() throws IOException {
	}

	public boolean eof() {
		return true;
	}

	public void open(Media media) throws IOException {
	}

	public Frame readFrame(Frame frame) throws IOException {
		
		frame.setLength(26);
		frame.setSize(emptyFrame.length);
		System.arraycopy(emptyFrame, 0, frame.getData(), 0, emptyFrame.length);
		
		return frame;
	}

	/**
	 * This method always returns false so the reader isn't used by mistake.
	 */
	public boolean supports(Media media) {
		return false;
	}

	public Metadata getMetadata() {
		return Metadata.create("", "", "No media playing");
	}

}
