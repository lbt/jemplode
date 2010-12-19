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
package org.jempeg;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTable;

import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IContextListener;
import org.jempeg.protocol.ISynchronizeClient;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.SilentProgressListener;

/**
 * EmplodeContext represents a "central point"
 * for all of the jEmplode components to communicate
 * with eachother.  It provides access to all of the
 * "app-global" resources.
 * 
 * @author Mike Schrag
 */
public class ApplicationContext {
	private JFrame myFrame;

	private JTable myTable;

	private PlayerDatabase myPlayerDatabase;
	private ISynchronizeClient mySynchronizeClient;
	private IProgressListener myDownloadProgressListener;
	private IProgressListener mySynchronizeProgressListener;
	private IProgressListener myImportFilesProgressListener;
	private IProgressListener myDownloadFilesProgressListener;
	private IProgressListener myDefaultProgressListener;

	private IContainer mySelectedContainer;
	private ContainerSelection mySelection;

	private Vector myContextListeners;

	/**
	 * Instantiantes an Emplode
	 *
	 * @throws IOException if the properties fail or the database can not be opened
	 */
	public ApplicationContext() {
		myContextListeners = new Vector();
		myDefaultProgressListener = new SilentProgressListener();
	}

	public void addContextListener(IContextListener _contextListener) {
		myContextListeners.addElement(_contextListener);
	}

	public void removeContextListener(IContextListener _contextListener) {
		myContextListeners.removeElement(_contextListener);
	}

	/**
	 * Returns the current PlayerDatabase.
	 *
	 * @returns the current PlayerDatabase
	 */
	public PlayerDatabase getPlayerDatabase() {
		return myPlayerDatabase;
	}

	/**
	 * Returns the current SynchronizeClient (or null if there isn't one)
	 *
	 * @returns the current SynchronizeClient
	 */
	public ISynchronizeClient getSynchronizeClient() {
		return mySynchronizeClient;
	}

	/**
	 * Sets the current synchronize client.  This will check for new versions of
	 * the various connection-dependent components and switch the PlayerDatabase.
	 *
	 * @param _protocolClient the ProtocolClient to use
	 */
	public void setSynchronizeClient(PlayerDatabase _playerDatabase, ISynchronizeClient _synchronizeClient) {
		ISynchronizeClient oldSynchronizeClient = mySynchronizeClient;
		mySynchronizeClient = _synchronizeClient;
		PlayerDatabase oldPlayerDatabase = myPlayerDatabase;
		myPlayerDatabase = _playerDatabase;
		fireDatabaseChanged(oldPlayerDatabase, myPlayerDatabase);
		fireSynchronizeClientChanged(oldSynchronizeClient, mySynchronizeClient);
	}

	/**
	 * Returns the primary Emplode frame.
	 *
	 * @returns the primary Emplode frame
	 */
	public JFrame getFrame() {
		return myFrame;
	}

	/**
	 * Sets the primary Emplode frame.
	 *
	 * @param _frame the primary Emplode frame
	 */
	public void setFrame(JFrame _frame) {
		myFrame = _frame;
	}

	/**
	 * Sets the playlist table.
	 *
	 * @param _table the playlist table
	 */
	public void setTable(JTable _table) {
		myTable = _table;
	}

	/**
	 * Returns the playlist table.
	 * 
	 * @return the playlist table
	 */
	public JTable getTable() {
		return myTable;
	}

	public IProgressListener getDownloadProgressListener() {
		return (myDownloadProgressListener == null) ? myDefaultProgressListener : myDownloadProgressListener;
	}

	public void setDownloadProgressListener(IProgressListener _progressListener) {
		myDownloadProgressListener = _progressListener;
	}

	public IProgressListener getSynchronizeProgressListener() {
		return (mySynchronizeProgressListener == null) ? myDefaultProgressListener : mySynchronizeProgressListener;
	}

	public void setSynchronizeProgressListener(IProgressListener _progressListener) {
		mySynchronizeProgressListener = _progressListener;
	}

	public IProgressListener getDownloadFilesProgressListener() {
		return (myDownloadFilesProgressListener == null) ? myDefaultProgressListener : myDownloadFilesProgressListener;
	}

	public void setDownloadFilesProgressListener(IProgressListener _progressListener) {
		myDownloadFilesProgressListener = _progressListener;
	}

	public IProgressListener getImportFilesProgressListener() {
		return (myImportFilesProgressListener == null) ? myDefaultProgressListener : myImportFilesProgressListener;
	}

	public void setImportFilesProgressListener(IProgressListener _progressListener) {
		myImportFilesProgressListener = _progressListener;
	}

	public IProgressListener getDefaultProgressListener() {
		return myDefaultProgressListener;
	}

	public void setDefaultProgressListener(IProgressListener _progressListener) {
		myDefaultProgressListener = _progressListener;
	}

	/**
	 * Returns the currently selected container.
	 * 
	 * @return the currently selected container
	 */
	public IContainer getSelectedContainer() {
		return mySelectedContainer;
	}

	/**
	 * Sets the currently selected container.
	 * 
	 * @param _selectedContainer the currently selected container
	 */
	public void setSelectedContainer(IContainer _selectedContainer) {
		IContainer oldSelectedContainer = mySelectedContainer;
		mySelectedContainer = _selectedContainer;
		if (oldSelectedContainer != _selectedContainer) {
			fireSelectedContainerChanged(oldSelectedContainer, mySelectedContainer);
		}
	}
	
	/**
	 * Sets the current selection.
	 * 
	 * @param _selection the current selection
	 */
	public void setSelection(Object _source, ContainerSelection _selection) {
		ContainerSelection oldSelection = mySelection;
		mySelection = _selection;
		fireSelectionChanged(_source, oldSelection, mySelection);
	}

	/**
	 * Returns the currently active selection.
	 * 
	 * @return the currently active selection
	 */
	public ContainerSelection getSelection() {
		return mySelection;
	}

	/**
	 * Clears the selection from the active container
	 */
	public void clearSelection(Object _source) {
		setSelection(_source, null);
	}

	void fireDatabaseChanged(PlayerDatabase _oldPlayerDatabase, PlayerDatabase _newPlayerDatabase) {
		for (int i = myContextListeners.size() - 1; i >= 0; i--) {
			IContextListener listener = (IContextListener) myContextListeners.elementAt(i);
			listener.databaseChanged(_oldPlayerDatabase, _newPlayerDatabase);
		}
	}

	void fireSynchronizeClientChanged(ISynchronizeClient _oldSynchronizeClient, ISynchronizeClient _newSynchronizeClient) {
		for (int i = myContextListeners.size() - 1; i >= 0; i--) {
			IContextListener listener = (IContextListener) myContextListeners.elementAt(i);
			listener.synchronizeClientChanged(_oldSynchronizeClient, _newSynchronizeClient);
		}
	}
	
	void fireSelectedContainerChanged(IContainer _oldContainer, IContainer _newContainer) {
		for (int i = myContextListeners.size() - 1; i >= 0; i--) {
			IContextListener listener = (IContextListener) myContextListeners.elementAt(i);
			listener.selectedContainerChanged(_oldContainer, _newContainer);
		}
	}
	
	void fireSelectionChanged(Object _source, ContainerSelection _oldSelection, ContainerSelection _newSelection) {
		for (int i = myContextListeners.size() - 1; i >= 0; i--) {
			IContextListener listener = (IContextListener) myContextListeners.elementAt(i);
			listener.selectionChanged(_source, _oldSelection, _newSelection);
		}
	}
}
