package org.jempeg.nodestore;

import java.util.Enumeration;
import java.util.Vector;

/**
 * WendyFlags provides an interface to managing
 * Wendy Flags on the Empeg.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.2 $
 */
public class WendyFlags {
	private Vector myWendyFlags;
	private Vector myRemovedIndexes;
	private int myOriginalCount;

	/**
	 * Consturcts a new WendyFlags
	 */
	public WendyFlags() {
		myWendyFlags = new Vector();
		myRemovedIndexes = new Vector();
	}
	
	/**
	 * Reads the Wendy Flags from the given ConfigFile.
	 * 
	 * @param _configFile the ConfigFile to read Wendy Flags from
	 * @throws IOException if the flags cannot be read
	 */
	public void readFlags(IDeviceSettings _configFile) {
		int flagAmount = _configFile.getWendyFlagCount();
		myWendyFlags.removeAllElements();
		myRemovedIndexes.removeAllElements();
		for (int i = 0; i < flagAmount; i ++) {
			String flag = _configFile.getWendyFlag(i);
			myWendyFlags.addElement(flag);
		}
		myOriginalCount = flagAmount;
	}

	/**
	 * Writes the Wendy Flags to the given ConfigFile.
	 * 
	 * @param _configFile the ConfigFile to write Wendy Flags to
	 * @throws IOException if the flags cannot be written
	 */
	public void writeFlags(IDeviceSettings _configFile, PlayerDatabase _db) {
		String[] wendyFlags = getFlags();
		_configFile.setWendyFlagCount(wendyFlags.length);
		for (int i = 0; i < wendyFlags.length; i ++) {
			_configFile.setWendyFlag(i, wendyFlags[i]);
		}
		
		int[] removedIndexes = getRemovedIndexes();
		for (int i = 0; i < removedIndexes.length; i ++) {
			int index = removedIndexes[i];
			removeFlag(index, _db);
			for (int j = i + 1; j < removedIndexes.length; j ++) {
				int nextIndex = removedIndexes[j];
				if (nextIndex > index) {
					removedIndexes[j] = nextIndex - 1;
				}
			}
		}
	}
	
	/**
	 * Returns the Wendy Flags that were read from the PlayerDatabase.
	 * 
	 * @return the Wendy Flags that were read from the PlayerDatabase
	 */
	public String[] getFlags() {
		String[] wendyFlags = new String[myWendyFlags.size()];
		myWendyFlags.copyInto(wendyFlags);
		return wendyFlags;
	}

	/**
	 * Adds a new Wendy Flag to this model.
	 * 
	 * @param _wendyFlag the Wendy flag to add
	 */
	public void addFlag(String _wendyFlag) {
		if (myWendyFlags.contains(_wendyFlag)) {
			throw new IllegalArgumentException("There is already a WendyFlag named '" + _wendyFlag + "'.");
		}
		myWendyFlags.addElement(_wendyFlag);
	}
	
	/**
	 * Removes a Wendy Flag from this model.
	 * 
	 * @param _wendyFlag the Wendy flag to remove
	 */
	public void removeFlag(String _wendyFlag) {
		int flagIndex = getIndexOf(_wendyFlag);
		if (flagIndex < myOriginalCount) {
			myRemovedIndexes.addElement(new Integer(flagIndex));
		}
		myWendyFlags.removeElement(_wendyFlag);
	}
	
	/**
	 * Removes a Wendy Flag from all the nodes in the 
	 * database that reference that flag.
	 * 
	 * @param _flagIndex the Wendy flag index to remove
	 * @param _db the PlayerDatabase to update
	 */
	public void removeFlag(int _flagIndex, PlayerDatabase _db) {
    Enumeration elements = _db.getNodeMap().elements();
    while (elements.hasMoreElements()) {
    	IFIDNode node = (IFIDNode)elements.nextElement();
      NodeTags tags = node.getTags();
      int wendy = tags.getIntValue(DatabaseTags.WENDY_TAG, 0);
      if (wendy > 0) {
				int lowerPart = wendy & ((1 << _flagIndex) - 1);
				int upperPart = (wendy >> (_flagIndex + 1)) << _flagIndex;
				int newWendy = lowerPart | upperPart;
      	tags.setIntValue(DatabaseTags.WENDY_TAG, newWendy);
      }
    }
	}

	/**
	 * Rename a flag.
	 * 
	 * @param _origianlFlag the original name of the flag
	 * @param _newFlag the new name of the flag
	 */
	public void renameFlag(String _originalFlag, String _newFlag) {
		if (myWendyFlags.contains(_newFlag)) {
			throw new IllegalArgumentException("There is already a WendyFlag named '" + _newFlag + "'.");
		}
		int index = myWendyFlags.indexOf(_originalFlag);
		myWendyFlags.setElementAt(_newFlag, index);
	}
	
	/**
	 * Returns the index of the given wendy flag.
	 * 
	 * @param _wendyFlag the flag to lookup
	 * @returns the index of the given wendy flag
	 */
	public int getIndexOf(String _wendyFlag) {
		int index = myWendyFlags.indexOf(_wendyFlag);
		return index;
	}
	
	/**
	 * Clears all the Wendy flags.
	 */
	public void removeAllFlags() {
		myWendyFlags.removeAllElements();
		myRemovedIndexes.removeAllElements();
		myOriginalCount = 0;
	}
	
	/**
	 * Returns whether or not the given Wendy flag is set.
	 * 
	 * @param _nodeWendyValue the wendy value bitmask for a node
	 * @param _flagIndex the index of the flag to check
	 * @return whether or not the given Wendy flag is set
	 */
	public boolean isWendyFlagSet(int _nodeWendyValue, int _flagIndex) {
		return _nodeWendyValue > 0 && (_nodeWendyValue & (1 << _flagIndex)) != 0;
	}
	
	/**
	 * Returns whether or not the given Wendy flag is set.
	 * 
	 * @param _nodeWendyValue the wendy value bitmask for a node
	 * @param _wendyFlag the name of the flag to check
	 * @return whether or not the given Wendy flag is set
	 */
	public boolean isWendyFlagSet(int _nodeWendyValue, String _wendyFlag) {
		int index = getIndexOf(_wendyFlag);
		return isWendyFlagSet(_nodeWendyValue, index);
	}
	
	/**
	 * Returns the set of flags that have been removed.
	 * 
	 * @return the set of flags that have been removed
	 */
	protected int[] getRemovedIndexes() {
		int size = myRemovedIndexes.size();
		int[] removedIndexes = new int[size];
		for (int i = 0; i < size; i ++) {
			Integer removedIndexInteger = (Integer)myRemovedIndexes.elementAt(i);
			removedIndexes[i] = removedIndexInteger.intValue();
		}
		return removedIndexes;
	}
}
