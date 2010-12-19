package org.jempeg.nodestore;

import java.util.Vector;

import com.inzyme.util.ReflectionUtils;

public class PlaylistPair {
	private long myFID;
	private int myGeneration;
  private String myGenerationStr;
	
	public PlaylistPair(IFIDNode _node) {
    myFID = _node.getFID();
    myGeneration = -1;
    myGenerationStr = _node.getTags().getValue(DatabaseTags.GENERATION_TAG);
	}
	
	public PlaylistPair(long _fid, int _generation) {
		myFID = _fid;
		myGeneration = _generation;
	}
	
	public long getFID() {
		return myFID;
	}
	
	public synchronized int getGeneration() {
    if (myGenerationStr != null) {
      if (myGenerationStr.length() == 0) {
        myGeneration = 0;
      }
      else {
        myGeneration = Integer.parseInt(myGenerationStr);
      }
      myGenerationStr = null;
    }
		return myGeneration;
	}
	
	public int hashCode() {
		return (int)(myFID ^ (myFID >>> 32));
	}
	
	public boolean equals(Object _obj) {
		boolean equals;
		if (_obj instanceof PlaylistPair) {
			PlaylistPair otherPair = (PlaylistPair)_obj;
			equals = otherPair.myFID == myFID;
		} else {
			equals = false;
		}
		return equals;
	}
	
	public boolean isStale(PlayerDatabase _database) {
		IFIDNode node = _database.getNode(myFID);
		boolean isStale = myGeneration != 0 && (node == null || (node.getGeneration() != myGeneration));
		return isStale;
	}
	
	public boolean isStale(IFIDNode _node) {
		boolean isStale = myGeneration != 0 && (_node == null || (_node.getFID() == myFID && _node.getGeneration() != myGeneration));
		return isStale;
	}
	
	public String toString() {
		return ReflectionUtils.toString(this);
	}
	
	public static boolean containsNode(PlaylistPair[] _originalPairs, IFIDNode _node) {
		boolean containsNode = false;
		if (_originalPairs != null) {
			long fid = _node.getFID();
			for (int i = 0; !containsNode && i < _originalPairs.length; i ++) {
				containsNode = _originalPairs[i].getFID() == fid;
			}
		}
		return containsNode;
	}
	
	public static PlaylistPair[] removeNode(PlaylistPair[] _originalPairs, IFIDNode _removedNode) {
		PlaylistPair[] newPairs;
		Vector newPairsVec = null;
		
		if (_originalPairs != null) {
			long removedFID = _removedNode.getFID();
			for (int i = 0; i < _originalPairs.length; i ++) {
				if (_originalPairs[i].myFID == removedFID) {
					if (newPairsVec == null) {
						newPairsVec = new Vector();
						for (int previousPairsNum = 0; previousPairsNum < i; previousPairsNum ++) {
							newPairsVec.addElement(_originalPairs[previousPairsNum]);
						}
					}
				} else if (newPairsVec != null) {
					newPairsVec.addElement(_originalPairs[i]);
				}
			}
		
			if (newPairsVec == null) {
				newPairs = _originalPairs;
			} else {
				newPairs = new PlaylistPair[newPairsVec.size()];
				newPairsVec.copyInto(newPairs);
			}
		} else {
			newPairs = null;
		}
		
		return newPairs;
	}
}
