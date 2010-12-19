package org.jempeg.nodestore.exporter;

import java.io.IOException;
import java.io.OutputStream;

import org.jempeg.nodestore.PlayerDatabase;

/**
 * Implemented by anything that can export a
 * PlayerDatabase to a data file.
 * 
 * @author Mike Schrag
 */
public interface IExporter {
	/**
	 * Writes a PlayerDatabase to a data stream.
	 * 
	 * @param _db the Database to write
	 * @param _outputStream the stream to write to
	 * @throws IOException if the writing files
	 */
	public abstract void write(PlayerDatabase _db, OutputStream _outputStream) throws IOException;
}
