package se.slackers.locality.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.slackers.locality.model.Metadata;


public class MetadataManagerTest {
	private MetadataManager mm = new MetadataManager();
	
	
	@Test
	public void testParse() {
		Metadata info = Metadata.create("ARTIST", "ALBUM", "TITLE");
		
		String result = mm.parseFormat("${artist} $album$track ?(title,-) ${title}", info);
		assertEquals("ARTIST ALBUM - TITLE", result);
	}
}
