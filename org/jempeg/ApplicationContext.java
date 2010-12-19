/* ApplicationContext - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTable;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.SilentProgressListener;

import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.IContextListener;
import org.jempeg.protocol.ISynchronizeClient;

public class ApplicationContext
{
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
    private Vector myContextListeners = new Vector();
    
    public ApplicationContext() {
	myDefaultProgressListener = new SilentProgressListener();
    }
    
    public void addContextListener(IContextListener _contextListener) {
	myContextListeners.addElement(_contextListener);
    }
    
    public void removeContextListener(IContextListener _contextListener) {
	myContextListeners.removeElement(_contextListener);
    }
    
    public PlayerDatabase getPlayerDatabase() {
	return myPlayerDatabase;
    }
    
    public ISynchronizeClient getSynchronizeClient() {
	return mySynchronizeClient;
    }
    
    public void setSynchronizeClient(PlayerDatabase _playerDatabase,
				     ISynchronizeClient _synchronizeClient) {
	ISynchronizeClient oldSynchronizeClient = mySynchronizeClient;
	mySynchronizeClient = _synchronizeClient;
	PlayerDatabase oldPlayerDatabase = myPlayerDatabase;
	myPlayerDatabase = _playerDatabase;
	fireDatabaseChanged(oldPlayerDatabase, myPlayerDatabase);
	fireSynchronizeClientChanged(oldSynchronizeClient,
				     mySynchronizeClient);
    }
    
    public JFrame getFrame() {
	return myFrame;
    }
    
    public void setFrame(JFrame _frame) {
	myFrame = _frame;
    }
    
    public void setTable(JTable _table) {
	myTable = _table;
    }
    
    public JTable getTable() {
	return myTable;
    }
    
    public IProgressListener getDownloadProgressListener() {
	return (myDownloadProgressListener == null ? myDefaultProgressListener
		: myDownloadProgressListener);
    }
    
    public void setDownloadProgressListener
	(IProgressListener _progressListener) {
	myDownloadProgressListener = _progressListener;
    }
    
    public IProgressListener getSynchronizeProgressListener() {
	return (mySynchronizeProgressListener == null
		? myDefaultProgressListener : mySynchronizeProgressListener);
    }
    
    public void setSynchronizeProgressListener
	(IProgressListener _progressListener) {
	mySynchronizeProgressListener = _progressListener;
    }
    
    public IProgressListener getDownloadFilesProgressListener() {
	return (myDownloadFilesProgressListener == null
		? myDefaultProgressListener : myDownloadFilesProgressListener);
    }
    
    public void setDownloadFilesProgressListener
	(IProgressListener _progressListener) {
	myDownloadFilesProgressListener = _progressListener;
    }
    
    public IProgressListener getImportFilesProgressListener() {
	return (myImportFilesProgressListener == null
		? myDefaultProgressListener : myImportFilesProgressListener);
    }
    
    public void setImportFilesProgressListener
	(IProgressListener _progressListener) {
	myImportFilesProgressListener = _progressListener;
    }
    
    public IProgressListener getDefaultProgressListener() {
	return myDefaultProgressListener;
    }
    
    public void setDefaultProgressListener
	(IProgressListener _progressListener) {
	myDefaultProgressListener = _progressListener;
    }
    
    public IContainer getSelectedContainer() {
	return mySelectedContainer;
    }
    
    public void setSelectedContainer(IContainer _selectedContainer) {
	IContainer oldSelectedContainer = mySelectedContainer;
	mySelectedContainer = _selectedContainer;
	if (oldSelectedContainer != _selectedContainer)
	    fireSelectedContainerChanged(oldSelectedContainer,
					 mySelectedContainer);
    }
    
    public void setSelection(Object _source, ContainerSelection _selection) {
	ContainerSelection oldSelection = mySelection;
	mySelection = _selection;
	fireSelectionChanged(_source, oldSelection, mySelection);
    }
    
    public ContainerSelection getSelection() {
	return mySelection;
    }
    
    public void clearSelection(Object _source) {
	setSelection(_source, null);
    }
    
    void fireDatabaseChanged(PlayerDatabase _oldPlayerDatabase,
			     PlayerDatabase _newPlayerDatabase) {
	for (int i = myContextListeners.size() - 1; i >= 0; i--) {
	    IContextListener listener
		= (IContextListener) myContextListeners.elementAt(i);
	    listener.databaseChanged(_oldPlayerDatabase, _newPlayerDatabase);
	}
    }
    
    void fireSynchronizeClientChanged
	(ISynchronizeClient _oldSynchronizeClient,
	 ISynchronizeClient _newSynchronizeClient) {
	for (int i = myContextListeners.size() - 1; i >= 0; i--) {
	    IContextListener listener
		= (IContextListener) myContextListeners.elementAt(i);
	    listener.synchronizeClientChanged(_oldSynchronizeClient,
					      _newSynchronizeClient);
	}
    }
    
    void fireSelectedContainerChanged(IContainer _oldContainer,
				      IContainer _newContainer) {
	for (int i = myContextListeners.size() - 1; i >= 0; i--) {
	    IContextListener listener
		= (IContextListener) myContextListeners.elementAt(i);
	    listener.selectedContainerChanged(_oldContainer, _newContainer);
	}
    }
    
    void fireSelectionChanged(Object _source, ContainerSelection _oldSelection,
			      ContainerSelection _newSelection) {
	for (int i = myContextListeners.size() - 1; i >= 0; i--) {
	    IContextListener listener
		= (IContextListener) myContextListeners.elementAt(i);
	    listener.selectionChanged(_source, _oldSelection, _newSelection);
	}
    }
}
