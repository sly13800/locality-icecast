package se.slackers.locality.data;

import junit.framework.TestCase;

import org.junit.Test;


public class ExpandOnWriteCircularBufferTest extends TestCase {
	private static final int SIZE = 1000;
	private ExpandOnWriteCircularBuffer buffer = new ExpandOnWriteCircularBuffer(SIZE);	
	private byte [] temp = new byte[SIZE];
	
	@Test
	public void testReadEmpty() {
		buffer.reset();
		assertEquals(0, buffer.read(0, temp, 0, 10));
	}
	
	@Test
	public void testWriteRead() {
		buffer.reset();		
		int length = 100;
		assertEquals(true, buffer.write(temp, 0, length));		
		assertEquals(length, buffer.read(0, temp, 0, length));
	}

	@Test
	public void testRandomness() {
		buffer.reset();
		int size = 0;
		
		for (int i=0;i<100;i++) {			
			size = 0;	
			for (int j=0;j<10;j++) {
				int len = (int)(Math.random()*400.0+100.0);
				buffer.write(temp, 0, len);
				size += len;
			}
			
			assertTrue(size-SIZE+1 == buffer.getReadOffset());
			
			buffer.reset();
		}			
	}
}
