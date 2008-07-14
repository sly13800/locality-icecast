package se.slackers.locality.shout;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

import se.slackers.locality.data.FixedFrameSizeFrameStorage;
import se.slackers.locality.media.queue.MediaQueue;
import se.slackers.locality.media.queue.MediaQueueImpl;
import se.slackers.locality.media.queue.MediaQueueProcessor;
import se.slackers.locality.media.queue.PreloadDataMediaQueueProcessor;
import se.slackers.locality.media.reader.MediaReaderFactory;
import se.slackers.locality.media.reader.mp3.Mp3MediaReader;
import se.slackers.locality.model.Media;
import se.slackers.locality.shout.manager.ShoutRequestManager;
import se.slackers.locality.shout.manager.ShoutRequestManagerImpl;


public class ShoutServerTest extends TestCase {

	@Test
	public void testServerStart() {
		Media media = new Media();
		media.setMediaFile(new File("test.mp3"));		
		
		MediaReaderFactory mediaReaderFactory = new MediaReaderFactory();
		mediaReaderFactory.addMediaReader(new Mp3MediaReader());
		
		MediaQueue queue = new MediaQueueImpl("mediaQueue");
		queue.setMountPoint("/media.mp3");
		queue.add(media);
		
		MediaQueueProcessor processor = new PreloadDataMediaQueueProcessor();
		processor.setMediaQueue(queue);
		processor.setMediaReaderFactory(mediaReaderFactory);
		
		queue.setMediaQueueProcessor(processor);
		queue.setFrameStorage( new FixedFrameSizeFrameStorage() );
		
		ShoutRequestManager manager = new ShoutRequestManagerImpl();
		manager.registerQueue(queue);
		
		ShoutServer server = new ShoutServer();
		server.setServerPort(8001);
		
		server.setRequestManager(manager);
		server.run();
	}
}
