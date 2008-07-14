package se.slackers.locality.data;

import junit.framework.TestCase;

import org.junit.Test;


public class CircularBufferTest extends TestCase {
	private CircularBuffer buffer = new CircularBuffer(1000);	
	private byte [] temp = new byte[1000];
	
	@Test
	public void testReadEmpty() {
		buffer.reset();
		assertEquals(0, buffer.read(temp, 0, 10));
	}
	
	@Test
	public void testWriteRead() {
		buffer.reset();		
		int length = 100;
		assertEquals(true, buffer.write(temp, 0, length));		
		assertEquals(length, buffer.read(temp, 0, length));
	}

	@Test
	public void testWriteReadOverflow() {
		buffer.reset();		
		int length = 100;
		assertEquals(true, buffer.write(temp, 0, length));		
		assertEquals(length, buffer.read(temp, 0, length*2));
	}

	@Test
	public void testWriteOverEdge() {
		buffer.reset();		
		assertEquals(true, buffer.write(temp, 0, 900));
		assertEquals(100, buffer.read(temp, 0, 100));
		// should be 200 bytes free in buffer		
		assertEquals(true, buffer.write(temp, 0, 150));
	}
	
	@Test
	public void testWriteOverEdgeLimit() {
		buffer.reset();		
		assertEquals(true, buffer.write(temp, 0, 900));
		assertEquals(100, buffer.read(temp, 0, 100));
		// should be 200 bytes free in buffer		
		assertEquals(false, buffer.write(temp, 0, 250));
	}	
	
	@Test
	public void testReadOverEdge() {
		buffer.reset();		
		assertEquals(true, buffer.write(temp, 0, 900));
		assertEquals(800, buffer.read(temp, 0, 800));
		// readIndex = 800, writeIndex = 900		
		assertEquals(true, buffer.write(temp, 0, 500));
		assertEquals(500, buffer.read(temp, 0, 500));
	}		

	@Test
	public void testReadOverEdgeLimit() {
		buffer.reset();		
		assertEquals(true, buffer.write(temp, 0, 900));
		assertEquals(800, buffer.read(temp, 0, 800));
		// readIndex = 800, writeIndex = 900		
		assertEquals(true, buffer.write(temp, 0, 500));
		assertEquals(600, buffer.read(temp, 0, 700));
	}
	
	@Test
	public void testRandomness() {
		buffer.reset();
		int size = 0;
		
		for (int i=0;i<100;i++) {			
			size = 1000;		
			while (true) {
				int len = (int)(Math.random()*400.0+100.0);
				if (!buffer.write(temp, 0, len))
					break;
				size -= len;
			}
			
			assertTrue(size >= 0);
	
			size = 1000-size;		
			while (true) {
				int len = (int)(Math.random()*400.0+100.0);
				int bytes = buffer.read(temp, 0, len);
				size -= bytes;
				if (0 == bytes)
					break;
			}
			
			assertTrue(size == 0);
		}			
	}
}
