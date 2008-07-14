package se.slackers.locality.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import se.slackers.locality.media.queue.MediaQueue;
import se.slackers.locality.media.queue.MediaQueueProcessorListener;
import se.slackers.locality.model.Media;
import se.slackers.locality.model.Metadata;
import se.slackers.locality.model.MetadataType;

/**
 * Controls when a full metadata chunk should be rendered and in which format.
 * @author bysse
 *
 */
public class MetadataManager implements MediaQueueProcessorListener {
	private static Logger log = Logger.getLogger(MetadataManager.class);
	private final static int maximumMetadataLength = 4095;
	
	private final static Pattern field = Pattern.compile("(\\$\\{([^\\}\\$]+)\\})|(\\$([^\\s\\?\\$]+))");
	private final static Pattern condition = Pattern.compile("\\?\\(([^,]+),([^\\)]+)\\)");	

	private String format = "$artist ?(album,- )$album ?(title,- )$title";	
	private String cachedMetadataString = "";
	private Metadata currentMetadata = null;
	
	private long sendMetadataInterval = 15000;
	private long lastMetadataChunk = 0;
	
	/**
	 * Default constructor. Sets the metadata to "Nothing playing"
	 */
	public MetadataManager() {
		setMetadata(Metadata.create("Nothing playing", null, null));
	}

	/**
	 * Formats and returns a byte array containing metadata information from the MediaQueue.
	 * @param mediaQueue
	 * @return
	 */
	public byte [] getMetaData(MediaQueue mediaQueue) {
		long time = System.currentTimeMillis();
		
		if (time - lastMetadataChunk > sendMetadataInterval) {
			log.debug("Send full metadata chunk ("+cachedMetadataString+")");
			
			//.. return a full metadata string
			lastMetadataChunk = time;
		
			// restrict the length of the metadata
			if (cachedMetadataString.length() > maximumMetadataLength ) {
				cachedMetadataString = cachedMetadataString.substring(0, maximumMetadataLength);
			}
			
			int metadataLenth = cachedMetadataString.length();
			int encodedLength = ((int)Math.ceil(metadataLenth / 16.0));
			int blockLength = 16 * encodedLength;		
			
			byte [] result = new byte[blockLength+1];
			result[0] = (byte)encodedLength;
			
			System.arraycopy(cachedMetadataString.getBytes(), 0, result, 1, metadataLenth);
			
			// add padding to the block
			for (int i=metadataLenth+1;i<blockLength;i++) {
				result[i] = 0;
			}
			
			return result;
		} else {
			log.debug("Sending zero length metadata chunk");
			// return a zero length metadata chunk
			return new byte[] {0};
		}
	}
	
	
	/**
	 * Renders the metadata string sent to the client.
	 * @param format
	 * @param info
	 * @return
	 */
	protected String parseFormat(String format, Metadata info) {
		String result = format;
		
		//.. replace all fields in the format string
		Matcher fieldmatch = field.matcher(format);		
		while (fieldmatch.find()) {
			String fieldname = fieldmatch.group(2);
			
			if (fieldname == null) {
				fieldname = fieldmatch.group(4);
			}
			
			if (fieldname == null) {
				log.warn("Metadata format uses invalid field syntax '"+fieldmatch.group(0)+"'");
				continue;
			}
			
			try {
				MetadataType type = MetadataType.valueOf(fieldname.toUpperCase());
				
				// if there is metadata for this field, insert the data into the string
				// otherwise just remove the expression from the string
				if ( StringUtils.hasText( info.get(type) ) ) {
					result = result.replace(fieldmatch.group(0), info.get(type));
				} else {
					result = result.replace(fieldmatch.group(0), "");
				}
			} catch (IllegalArgumentException e) {
				log.warn("Metadata format uses invalid field name '"+fieldname+"' in expression '"+fieldmatch.group(0)+"'");
			}
		}

		//.. replace all conditionals in the format string
		Matcher conditionmatch = condition.matcher(format);		
		while (conditionmatch.find()) {
			String fieldname = conditionmatch.group(1);
			String text = conditionmatch.group(2);
			
			if (fieldname == null) {
				log.warn("Metadata format uses invalid field syntax '"+fieldmatch.group(0)+"'");
				continue;
			}
			
			try {
				MetadataType type = MetadataType.valueOf(fieldname.toUpperCase());
				if ( StringUtils.hasText( info.get(type) ) ) {
					result = result.replace(conditionmatch.group(0), text);
				} else {
					result = result.replace(conditionmatch.group(0), "");
				}
			} catch (IllegalArgumentException e) {
				log.warn("Metadata format uses invalid field name '"+fieldname+"' in expression '"+conditionmatch.group(0)+"'");
			}
		}
	
		return result;
	}


	public String getFormat() {
		return format;
	}


	public void setFormat(String format) {
		this.format = format;
		
		// call setMetadata to rerender the metadata string
		setMetadata(currentMetadata);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void nextMedia(Media media, Metadata metadata) {
		log.debug("nextMedia event triggered");
		setMetadata(metadata);
		
		// trigger a full metadata chunk
		lastMetadataChunk = 0;
	}
	
	/**
	 * Sets and renders new metadata with the current format. 
	 * @param metadata
	 */
	public void setMetadata(Metadata metadata) {
		log.debug("New metadata set ["+metadata+"] Thread: "+Thread.currentThread());
		currentMetadata = metadata;
		cachedMetadataString = parseFormat("StreamTitle='"+getFormat(), currentMetadata);		
	}	
}
