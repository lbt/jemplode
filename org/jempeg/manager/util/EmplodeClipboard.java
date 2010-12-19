/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package org.jempeg.manager.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.jempeg.nodestore.model.ContainerModifierFactory;
import org.jempeg.nodestore.model.IContainerModifier;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;
import com.inzyme.util.Debug;

/**
* The EmplodeClipboard provides cut/copy/paste support for AbstractFIDTreeNodes
*
* @author Mike Schrag
* @version $Revision: 1.2 $
*/
public class EmplodeClipboard {
	public static final String SELECTION_PROPERTY = "selection";

	private static EmplodeClipboard myInstance;
	private PropertyChangeSupport myPropertyChangeSupport;

	private ContainerSelection mySelection;
	private boolean myCut;

	/**
	* Instantiates an EmplodeClipboard.
	*/
	protected EmplodeClipboard() {
		myPropertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Adds a property change listener to this clipboard.
	 * 
	 * @param _listener the listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener _listener) {
		myPropertyChangeSupport.addPropertyChangeListener(_listener);
	}

	/**
	 * Removes a property change listener from this clipboard.
	 * 
	 * @param _listener the listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener _listener) {
		myPropertyChangeSupport.removePropertyChangeListener(_listener);
	}

	/**
	 * Returns an instance of EmplodeClipboard.
	 * 
	 * @return an instance of EmplodeClipboard
	 */
	public static synchronized EmplodeClipboard getInstance() {
		if (myInstance == null) {
			myInstance = new EmplodeClipboard();
		}
		return myInstance;
	}

	/**
	 * Returns whether or not this clipboard is in "cut mode".
	 * 
	 * @return whether or not this clipboard is in "cut mode"
	 */
	public synchronized boolean isCut() {
		return myCut;
	}

	/**
	* Returns whether or not the given _node is being cut.
	*
	* @param _node the node to look for in the cut list
	* @returns whether or not the node is being cut
	*/
	public synchronized boolean isCut(Object _obj) {
		return (myCut && mySelection != null && mySelection.contains(_obj));
	}

	/**
	* Cuts the currently selected nodes and places them on the clipboard
	*
	* @param _selection the selection to cut
	*/
	public synchronized void cut(ContainerSelection _selection) {
		Debug.println(Debug.VERBOSE, "Cutting " + _selection + " to clipboard...");
		clear();
		myCut = true;
		setSelection(_selection);
	}

	/**
	* Copies the selected elements and places them on the clipboard
	*
	* @param _selection the selection to copy
	*/
	public synchronized void copy(ContainerSelection _selection) {
		Debug.println(Debug.VERBOSE, "Copying " + _selection + " to clipboard...");
		clear();
		myCut = false;
		setSelection(_selection);
	}

	/**
	* Pastes the clipboard contents into the currently selected container
	*
	* @param _nodeModifier the node modifier to use
	* @param _confirmationListener the confirmation listener to use
	* @param _deepCopy whether or not to deep copy container (deep copy will create copies of playlists instead of refcount increases)
	*/
	public synchronized int[] paste(IContainerModifier _containerModifier, IConfirmationListener _confirmationListener, boolean _deepCopy, IProgressListener _progressListener) {
		int[] indexes;
		Debug.println(Debug.VERBOSE, "Pasting from clipboard into " + _containerModifier.getTargetContainer() + "...");
		if (mySelection != null) {
			if (myCut) {
				indexes = _containerModifier.moveTo(mySelection, _confirmationListener, _progressListener);
				clear();
			}
			else {
				indexes = _containerModifier.copyTo(mySelection, _confirmationListener, _deepCopy, _progressListener);
			}
		}
		else {
			indexes = new int[0];
		}
		return indexes;
	}

	/**
	* Pastes the clipboard contents into the currently selected container
	*
	* @param _targetContainer the target container to paste into
	* @param _confirmationListener the confirmation listener to use
	* @param _deepCopy whether or not to deep copy container (deep copy will create copies of playlists instead of refcount increases)
	*/
	public synchronized int[] paste(IContainer _targetContainer, IConfirmationListener _confirmationListener, boolean _deepCopy, IProgressListener _progressListener) {
		IContainerModifier nodeModifier = ContainerModifierFactory.getInstance(_targetContainer);
		int[] indexes = paste(nodeModifier, _confirmationListener, _deepCopy, _progressListener);
		return indexes;
	}

	/**
	* Clears the clipboard.
	*/
	public synchronized void clear() {
		Debug.println(Debug.VERBOSE, "Clearing clipboard");
		setSelection(null);
	}

	/**
	 * Returns the current selection for this clipboard.
	 * 
	 * @return the current selection for this clipboard
	 */
	public ContainerSelection getSelection() {
		return mySelection;
	}

	protected void setSelection(ContainerSelection _selection) {
		ContainerSelection oldSelection = mySelection;
		mySelection = _selection;
		myPropertyChangeSupport.firePropertyChange(SELECTION_PROPERTY, oldSelection, _selection);
	}
}
