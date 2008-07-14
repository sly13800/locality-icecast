package se.slackers.locality.media.reader.mp3;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import se.slackers.locality.media.Frame;
import se.slackers.locality.model.Media;

public class Mp3MediaReaderTest extends TestCase {

	private static final File base = new File("target/test-classes");
	private Media media = null;
	private Mp3MediaReader reader = null;

	@Before
	public void setUp() {
		media = new Media();
		media.setMediaFile(new File(base, "test.mp3"));

		reader = new Mp3MediaReader();
	}

	@Test
	public void testRead() {
		try {
			reader.open(media);			
			Frame frame = new Frame(4096);
			
			reader.readFrame(frame);
			reader.readFrame(frame);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
