/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package com.inzyme.progress;

import java.awt.event.ActionListener;
import java.util.EventListener;

/**
* IProgressListener adds more fine grain reporting onto a SimpleProgressListener along
* with the concept of operations and tasks (for two-progress-bar dialogs).
*
* @author Mike Schrag
*/
public interface IProgressListener extends EventListener, ISimpleProgressListener {
	/**
	 * Sets the wait state for this progress listener.  This might 
	 * correspond to turning on a wait cursor.  This is used during
	 * progresses that might take some time but not so long that they
	 * deserve actual statuses messages.
	 * 
	 * @param _waitState the wait state to set
	 */
	public void setWaitState(boolean _waitState);
	
	/**
	 * Sets whether or not this operation can be stopped.
	 * 
	 * @param _stopEnabled whether or not thie operation can be stopped
	 */
  public void setStopEnabled(boolean _stopEnabled);
  
  /**
   * Sets whether or not the user has requested to stop this operation.
   * 
   * @param _stopRequested whether or not the user has requested to stop this operation
   */
  public void setStopRequested(boolean _stopRequested);
  
  /**
   * Returns whether or not the user has requested to stop this operation.
   * 
   * @return whether or not the user has requested to stop this operation
   */
  public boolean isStopRequested();

	/**
	 * Called when some progress begins.
	 */
	public void progressStarted();
	
	/**
	 * Returns whether or not there is an operation in progress.
	 */
	public boolean isInProgress();
	
	/**
	 * Called when some progress completes.
	 */
	public void progressCompleted();
	
	/**
	 * Returns whether or not the user wants interaction
	 * about progress and results.
	 * 
	 * @return whether or not the user wants interaction
	 */
	public boolean isInteractive();
	
	/**
	 * Adds a listener for the stop button.
	 * 
	 * @param _stopListener a listener for the stop button
	 */
	public void addStopListener(ActionListener _stopListener);
	
	public void operationStarted(String _fmt);
	public void operationUpdated(long _relativeProgress);
	public void operationUpdated(long _progress, long _total);
	
	public void taskStarted(String _fmt);
	public void taskUpdated(long _relativeProgress);
	public void taskUpdated(long _progress, long _total);
}
