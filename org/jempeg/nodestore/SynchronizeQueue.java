/* SynchronizeQueue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.util.Vector;

import org.jempeg.nodestore.event.ISynchronizeQueueListener;

public class SynchronizeQueue
{
    private Vector myQueue = new Vector();
    private Vector myListeners = new Vector();
    private long myLength;
    private boolean mySynchronizingNow;
    
    public Object getLockObject() {
	return myQueue;
    }
    
    public void addSynchronizeQueueListener
	(ISynchronizeQueueListener _listener) {
	myListeners.addElement(_listener);
    }
    
    public void removeSynchronizeQueueListener
	(ISynchronizeQueueListener _listener) {
	myListeners.removeElement(_listener);
    }
    
    void fireDatabaseChangeEnqueued(IDatabaseChange _databaseChange) {
	for (int i = myListeners.size() - 1; i >= 0; i--) {
	    ISynchronizeQueueListener listener
		= (ISynchronizeQueueListener) myListeners.elementAt(i);
	    listener.databaseChangeEnqueued(this, _databaseChange);
	}
    }
    
    void fireDatabaseChangeDequeued(IDatabaseChange _databaseChange) {
	for (int i = myListeners.size() - 1; i >= 0; i--) {
	    ISynchronizeQueueListener listener
		= (ISynchronizeQueueListener) myListeners.elementAt(i);
	    listener.databaseChangeDequeued(this, _databaseChange);
	}
    }
    
    void fireDatabaseChangeRequeued(IDatabaseChange _databaseChange) {
	for (int i = myListeners.size() - 1; i >= 0; i--) {
	    ISynchronizeQueueListener listener
		= (ISynchronizeQueueListener) myListeners.elementAt(i);
	    listener.databaseChangeRequeued(this, _databaseChange);
	}
    }
    
    void fireDatabaseChangesCleared() {
	for (int i = myListeners.size() - 1; i >= 0; i--) {
	    ISynchronizeQueueListener listener
		= (ISynchronizeQueueListener) myListeners.elementAt(i);
	    listener.databaseChangesCleared(this);
	}
    }
    
    public void setSynchronizingNow(boolean _synchronizingNow) {
	mySynchronizingNow = _synchronizingNow;
    }
    
    public boolean isSynchronizingNow() {
	return mySynchronizingNow;
    }
    
    public boolean isEmpty() {
	Vector vector;
	MONITORENTER (vector = myQueue);
	boolean bool;
	MISSING MONITORENTER
	synchronized (vector) {
	    bool = myQueue.size() == 0;
	}
	return bool;
    }
    
    public void enqueue(IDatabaseChange _databaseChange) {
	boolean queueModified = false;
	Vector vector;
	MONITORENTER (vector = myQueue);
	MISSING MONITORENTER
	synchronized (vector) {
	    boolean shouldBeIgnored = false;
	    if (_databaseChange instanceof NodeRemovedDatabaseChange) {
		NodeRemovedDatabaseChange removeDatabaseChange
		    = (NodeRemovedDatabaseChange) _databaseChange;
		IFIDNode removedNode = removeDatabaseChange.getNode();
		for (int i = myQueue.size() - 1; i >= 0; i--) {
		    IDatabaseChange queuedDatabaseChange
			= (IDatabaseChange) myQueue.elementAt(i);
		    if (queuedDatabaseChange instanceof INodeDatabaseChange) {
			INodeDatabaseChange queuedNodeDatabaseChange
			    = (INodeDatabaseChange) queuedDatabaseChange;
			if (queuedNodeDatabaseChange.nodeEquals(removedNode)) {
			    if (queuedNodeDatabaseChange
				instanceof AbstractNodeAddedDatabaseChange)
				shouldBeIgnored = true;
			    myQueue.removeElementAt(i);
			    queueModified = true;
			} else if (queuedNodeDatabaseChange
				   instanceof IPlaylistMemberDatabaseChange) {
			    IPlaylistMemberDatabaseChange queuedPlaylistMemberDatabaseChange
				= ((IPlaylistMemberDatabaseChange)
				   queuedNodeDatabaseChange);
			    queuedPlaylistMemberDatabaseChange.setPlaylistPairs
				(PlaylistPair.removeNode
				 (queuedPlaylistMemberDatabaseChange
				      .getPlaylistPairs(),
				  removedNode));
			}
		    }
		}
	    } else if (_databaseChange instanceof FileInfoDatabaseChange) {
		FileInfoDatabaseChange fileInfoDatabaseChange
		    = (FileInfoDatabaseChange) _databaseChange;
		PlaylistPair[] playlistPairs
		    = fileInfoDatabaseChange.getPlaylistPairs();
		IFIDNode fileInfoNode = fileInfoDatabaseChange.getNode();
		boolean playlistPairsContainAddedNode = false;
		for (int i = myQueue.size() - 1;
		     (!playlistPairsContainAddedNode && !shouldBeIgnored
		      && i >= 0);
		     i--) {
		    IDatabaseChange queuedDatabaseChange
			= (IDatabaseChange) myQueue.elementAt(i);
		    if (queuedDatabaseChange
			instanceof AbstractNodeAddedDatabaseChange) {
			AbstractNodeAddedDatabaseChange queuedNodeAddedDatabaseChange
			    = ((AbstractNodeAddedDatabaseChange)
			       queuedDatabaseChange);
			if (queuedNodeAddedDatabaseChange
				.nodeEquals(fileInfoNode)) {
			    shouldBeIgnored = true;
			    if (queuedNodeAddedDatabaseChange
				instanceof PlaylistAddedDatabaseChange) {
				PlaylistAddedDatabaseChange queuedPlaylistAddedDatabaseChange
				    = ((PlaylistAddedDatabaseChange)
				       queuedNodeAddedDatabaseChange);
				queuedPlaylistAddedDatabaseChange
				    .setPlaylistPairs(playlistPairs);
				queueModified = true;
			    }
			} else
			    playlistPairsContainAddedNode
				= (PlaylistPair.containsNode
				   (playlistPairs,
				    queuedNodeAddedDatabaseChange.getNode()));
		    } else if (queuedDatabaseChange
			       instanceof FileInfoDatabaseChange) {
			FileInfoDatabaseChange queuedFileInfoDatabaseChange
			    = (FileInfoDatabaseChange) queuedDatabaseChange;
			if (queuedFileInfoDatabaseChange
				.nodeEquals(fileInfoNode)) {
			    myQueue.setElementAt(fileInfoDatabaseChange, i);
			    System.out.println("SynchronizeQueue.enqueue ("
					       + myQueue.size()
					       + " changes): replaced "
					       + queuedFileInfoDatabaseChange
					       + " with " + _databaseChange);
			    shouldBeIgnored = true;
			    queueModified = true;
			}
		    }
		}
	    }
	    if (!shouldBeIgnored) {
		if (shouldBePrepended(_databaseChange))
		    myQueue.insertElementAt(_databaseChange, 0);
		else
		    myQueue.addElement(_databaseChange);
		queueModified = true;
	    }
	}
	if (queueModified) {
	    recomputeLength();
	    fireDatabaseChangeEnqueued(_databaseChange);
	}
    }
    
    protected boolean shouldBePrepended(IDatabaseChange _databaseChange) {
	return _databaseChange instanceof NodeRemovedDatabaseChange;
    }
    
    public void requeue(IDatabaseChange _databaseChange) {
	Vector vector;
	MONITORENTER (vector = myQueue);
	MISSING MONITORENTER
	synchronized (vector) {
	    int requeueIndex = -1;
	    if (shouldBePrepended(_databaseChange))
		requeueIndex = 0;
	    else {
		int size = myQueue.size();
		for (int i = 0; requeueIndex == -1 && i < size; i++) {
		    IDatabaseChange queuedDatabaseChange
			= (IDatabaseChange) myQueue.elementAt(i);
		    if (!shouldBePrepended(queuedDatabaseChange))
			requeueIndex = i;
		}
	    }
	    if (requeueIndex == -1)
		requeueIndex = 0;
	    myQueue.insertElementAt(_databaseChange, requeueIndex);
	    recomputeLength();
	}
	fireDatabaseChangeRequeued(_databaseChange);
    }
    
    public int getSize() {
	return myQueue.size();
    }
    
    protected void recomputeLength() {
	Vector vector;
	MONITORENTER (vector = myQueue);
	MISSING MONITORENTER
	synchronized (vector) {
	    long length = 0L;
	    int size = getSize();
	    for (int i = 0; i < size; i++) {
		IDatabaseChange databaseChange = get(i);
		length += databaseChange.getLength();
	    }
	    myLength = length;
	}
    }
    
    public long getLength() {
	return myLength;
    }
    
    public IDatabaseChange get(int _index) {
	IDatabaseChange databaseChange
	    = (IDatabaseChange) myQueue.elementAt(_index);
	return databaseChange;
    }
    
    public synchronized IDatabaseChange dequeue() {
	object = object_1_;
	break;
    }
    
    public synchronized void clear() {
	myQueue.removeAllElements();
	recomputeLength();
	fireDatabaseChangesCleared();
    }
    
    public String toString() {
	return "[SynchronizeQueue: " + myQueue + "]";
    }
}
