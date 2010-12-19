package com.inzyme.progress;

/**
 * Provides the minimum interface necessary to receive progress information from
 * a provider of such data.
 *  
 * @author Mike Schrag
 */
public interface ISimpleProgressListener {
	/**
	 * Called when a progress event has occurred.
	 * 
	 * @param _current the current progress position
	 * @param _maximum the maximum progress position
	 */
	public void progressReported(long _current, long _maximum);

	/**
	 * Called when a progress event has occurred.
	 * 
	 * @param _description a description of the current state of progress 
	 * @param _current the current progress position
	 * @param _maximum the maximum progress position
	 */
	public void progressReported(String _description, long _current, long _maximum);
}
