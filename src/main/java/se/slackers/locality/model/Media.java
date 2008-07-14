package se.slackers.locality.model;

import java.io.File;

public class Media {
	private File mediaFile;

	public File getMediaFile() {
		return mediaFile;
	}

	public void setMediaFile(File mediaFile) {
		this.mediaFile = mediaFile;
	}

	@Override
	public String toString() {
		return "Media: "+mediaFile.toString();
	}
}
