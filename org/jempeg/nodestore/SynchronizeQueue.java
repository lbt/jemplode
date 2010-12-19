package org.jempeg.nodestore;

import java.util.Vector;

import org.jempeg.nodestore.event.ISynchronizeQueueListener;

public class SynchronizeQueue {
	private Vector myQueue;
	private Vector myListeners;
	private long myLength;
	private boolean mySynchronizingNow;

	public SynchronizeQueue() {
		myQueue = new Vector();
		myListeners = new Vector();
	}
	
	public Object getLockObject() {
		return myQueue;
	}

	public void addSynchronizeQueueListener(ISynchronizeQueueListener _listener) {
		myListeners.addElement(_listener);
	}

	public void removeSynchronizeQueueListener(ISynchronizeQueueListener _listener) {
		myListeners.removeElement(_listener);
	}

	void fireDatabaseChangeEnqueued(IDatabaseChange _databaseChange) {
		for (int i = myListeners.size() - 1; i >= 0; i--) {
			ISynchronizeQueueListener listener = (ISynchronizeQueueListener) myListeners.elementAt(i);
			listener.databaseChangeEnqueued(this, _databaseChange);
		}
	}

	void fireDatabaseChangeDequeued(IDatabaseChange _databaseChange) {
		for (int i = myListeners.size() - 1; i >= 0; i--) {
			ISynchronizeQueueListener listener = (ISynchronizeQueueListener) myListeners.elementAt(i);
			listener.databaseChangeDequeued(this, _databaseChange);
		}
	}

	void fireDatabaseChangeRequeued(IDatabaseChange _databaseChange) {
		for (int i = myListeners.size() - 1; i >= 0; i--) {
			ISynchronizeQueueListener listener = (ISynchronizeQueueListener) myListeners.elementAt(i);
			listener.databaseChangeRequeued(this, _databaseChange);
		}
	}

	void fireDatabaseChangesCleared() {
		for (int i = myListeners.size() - 1; i >= 0; i--) {
			ISynchronizeQueueListener listener = (ISynchronizeQueueListener) myListeners.elementAt(i);
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
		synchronized (myQueue) {
			return myQueue.size() == 0;
		}
	}

	public void enqueue(IDatabaseChange _databaseChange) {
		boolean queueModified = false;

		synchronized (myQueue) {
			boolean shouldBeIgnored = false;

			/*
			 *	if NodeRemovedDatabaseChange then
			 *		reverse loop through queue
			 *			if INodeDatabaseChange.node equals NodeRemovedDatabaseChange.node then
			 *				if AbstractNodeAddedDatabaseChange, then
			 *					ignore NodeRemovedDatabaseChange (since it was never on the player)
			 *				remove INodeDatabaseChange
			 *		if not ignore NodeRemovedDatabaseChange then
			 *			enqueue
			 *	else if FileInfoDatabaseChange then
			 *		loop through queue
			 *			
			 */
			if (_databaseChange instanceof NodeRemovedDatabaseChange) {
				NodeRemovedDatabaseChange removeDatabaseChange = (NodeRemovedDatabaseChange) _databaseChange;
				IFIDNode removedNode = removeDatabaseChange.getNode();
				for (int i = myQueue.size() - 1; i >= 0; i--) {
					IDatabaseChange queuedDatabaseChange = (IDatabaseChange) myQueue.elementAt(i);
					if (queuedDatabaseChange instanceof INodeDatabaseChange) {
						INodeDatabaseChange queuedNodeDatabaseChange = (INodeDatabaseChange) queuedDatabaseChange;
						if (queuedNodeDatabaseChange.nodeEquals(removedNode)) {
							if (queuedNodeDatabaseChange instanceof AbstractNodeAddedDatabaseChange) {
								shouldBeIgnored = true;
								// System.out.println("SynchronizeQueue.enqueue: ignore new " + _databaseChange + " because of " + queuedDatabaseChange);
							}
							myQueue.removeElementAt(i);
							queueModified = true;
							// System.out.println("SynchronizeQueue.enqueue (" + myQueue.size() + " changes): removed " + queuedNodeDatabaseChange + " because of " + _databaseChange);
						}
						else if (queuedNodeDatabaseChange instanceof IPlaylistMemberDatabaseChange) {
							IPlaylistMemberDatabaseChange queuedPlaylistMemberDatabaseChange = (IPlaylistMemberDatabaseChange) queuedNodeDatabaseChange;
							queuedPlaylistMemberDatabaseChange.setPlaylistPairs(PlaylistPair.removeNode(queuedPlaylistMemberDatabaseChange.getPlaylistPairs(), removedNode));
						}
					}
				}
			}
			else if (_databaseChange instanceof FileInfoDatabaseChange) {
				FileInfoDatabaseChange fileInfoDatabaseChange = (FileInfoDatabaseChange) _databaseChange;
				PlaylistPair[] playlistPairs = fileInfoDatabaseChange.getPlaylistPairs();
				IFIDNode fileInfoNode = fileInfoDatabaseChange.getNode();

				boolean playlistPairsContainAddedNode = false;
				for (int i = myQueue.size() - 1; !playlistPairsContainAddedNode && !shouldBeIgnored && i >= 0; i--) {
					IDatabaseChange queuedDatabaseChange = (IDatabaseChange) myQueue.elementAt(i);
					if (queuedDatabaseChange instanceof AbstractNodeAddedDatabaseChange) {
						AbstractNodeAddedDatabaseChange queuedNodeAddedDatabaseChange = (AbstractNodeAddedDatabaseChange) queuedDatabaseChange;
						if (queuedNodeAddedDatabaseChange.nodeEquals(fileInfoNode)) {
							shouldBeIgnored = true;

							if (queuedNodeAddedDatabaseChange instanceof PlaylistAddedDatabaseChange) {
								PlaylistAddedDatabaseChange queuedPlaylistAddedDatabaseChange = (PlaylistAddedDatabaseChange) queuedNodeAddedDatabaseChange;
								queuedPlaylistAddedDatabaseChange.setPlaylistPairs(playlistPairs);
								queueModified = true;
								// System.out.println("SynchronizeQueue.enqueue: replace playlist pairs then ignore new " + _databaseChange + " because of " + queuedNodeAddedDatabaseChange);
							} else {
								// System.out.println("SynchronizeQueue.enqueue: ignore new " + _databaseChange + " because of " + queuedNodeAddedDatabaseChange);
							}
						}
						else {
							playlistPairsContainAddedNode = PlaylistPair.containsNode(playlistPairs, queuedNodeAddedDatabaseChange.getNode());
							
							if (playlistPairsContainAddedNode) {
								// System.out.println("SynchronizeQueue.enqueue: stop looking for duplicates of " + _databaseChange + " because playlist contains new node " + queuedNodeAddedDatabaseChange);
							}
						}
					}
					else if (queuedDatabaseChange instanceof FileInfoDatabaseChange) {
						FileInfoDatabaseChange queuedFileInfoDatabaseChange = (FileInfoDatabaseChange) queuedDatabaseChange;
						if (queuedFileInfoDatabaseChange.nodeEquals(fileInfoNode)) {
							myQueue.setElementAt(fileInfoDatabaseChange, i);
							// System.out.println("SynchronizeQueue.enqueue (" + myQueue.size() + " changes): replaced " + queuedFileInfoDatabaseChange + " with " + _databaseChange);
							shouldBeIgnored = true;
							queueModified = true;
						}
					}
				}
			}

			if (!shouldBeIgnored) {
				if (shouldBePrepended(_databaseChange)) {
					myQueue.insertElementAt(_databaseChange, 0);
				} else {
					myQueue.addElement(_databaseChange);
				}
				queueModified = true;
				// System.out.println("SynchronizeQueue.enqueue (" + myQueue.size() + " changes): added " + _databaseChange);
			}
		}

		if (queueModified) {
			recomputeLength();
			fireDatabaseChangeEnqueued(_databaseChange);
		}
	}
	
	protected boolean shouldBePrepended(IDatabaseChange _databaseChange) {
		return (_databaseChange instanceof NodeRemovedDatabaseChange);
	}

	public void requeue(IDatabaseChange _databaseChange) {
		synchronized (myQueue) {
			int requeueIndex = -1;
			
			if (shouldBePrepended(_databaseChange)) {
				requeueIndex = 0;
			} else {
				int size = myQueue.size();
				for (int i = 0; requeueIndex == -1 && i < size; i ++) {
					IDatabaseChange queuedDatabaseChange = (IDatabaseChange)myQueue.elementAt(i);
					if (!shouldBePrepended(queuedDatabaseChange)) {
						requeueIndex = i;
					}
				}
			}
			if (requeueIndex == -1) {
				requeueIndex = 0;
			}
			myQueue.insertElementAt(_databaseChange, requeueIndex);
			recomputeLength();
			// System.out.println("SynchronizeQueue.requeue (" + myQueue.size() + "): requeued " + _databaseChange);
		}
		fireDatabaseChangeRequeued(_databaseChange);
	}

	public int getSize() {
		return myQueue.size();
	}

	protected void recomputeLength() {
		synchronized (myQueue) {
			long length = 0;
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
		IDatabaseChange databaseChange = (IDatabaseChange) myQueue.elementAt(_index);
		return databaseChange;
	}

	public synchronized IDatabaseChange dequeue() {
		IDatabaseChange databaseChange;
		synchronized (myQueue) {
			if (isEmpty()) {
				return null;
			}
			
			databaseChange = (IDatabaseChange) myQueue.elementAt(0);
			myQueue.removeElementAt(0);
		}
		recomputeLength();
		// System.out.println("SynchronizeQueue.dequeue (" + myQueue.size() + " changes): dequeued " + databaseChange);
		fireDatabaseChangeDequeued(databaseChange);
		return databaseChange;
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
