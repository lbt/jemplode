/* WendyFlags - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.util.Enumeration;
import java.util.Vector;

public class WendyFlags
{
    private Vector myWendyFlags = new Vector();
    private Vector myRemovedIndexes = new Vector();
    private int myOriginalCount;
    
    public void readFlags(IDeviceSettings _configFile) {
	int flagAmount = _configFile.getWendyFlagCount();
	myWendyFlags.removeAllElements();
	myRemovedIndexes.removeAllElements();
	for (int i = 0; i < flagAmount; i++) {
	    String flag = _configFile.getWendyFlag(i);
	    myWendyFlags.addElement(flag);
	}
	myOriginalCount = flagAmount;
    }
    
    public void writeFlags(IDeviceSettings _configFile, PlayerDatabase _db) {
	String[] wendyFlags = getFlags();
	_configFile.setWendyFlagCount(wendyFlags.length);
	for (int i = 0; i < wendyFlags.length; i++)
	    _configFile.setWendyFlag(i, wendyFlags[i]);
	int[] removedIndexes = getRemovedIndexes();
	for (int i = 0; i < removedIndexes.length; i++) {
	    int index = removedIndexes[i];
	    removeFlag(index, _db);
	    for (int j = i + 1; j < removedIndexes.length; j++) {
		int nextIndex = removedIndexes[j];
		if (nextIndex > index)
		    removedIndexes[j] = nextIndex - 1;
	    }
	}
    }
    
    public String[] getFlags() {
	String[] wendyFlags = new String[myWendyFlags.size()];
	myWendyFlags.copyInto(wendyFlags);
	return wendyFlags;
    }
    
    public void addFlag(String _wendyFlag) {
	if (myWendyFlags.contains(_wendyFlag))
	    throw new IllegalArgumentException
		      ("There is already a WendyFlag named '" + _wendyFlag
		       + "'.");
	myWendyFlags.addElement(_wendyFlag);
    }
    
    public void removeFlag(String _wendyFlag) {
	int flagIndex = getIndexOf(_wendyFlag);
	if (flagIndex < myOriginalCount)
	    myRemovedIndexes.addElement(new Integer(flagIndex));
	myWendyFlags.removeElement(_wendyFlag);
    }
    
    public void removeFlag(int _flagIndex, PlayerDatabase _db) {
	Enumeration elements = _db.getNodeMap().elements();
	while (elements.hasMoreElements()) {
	    IFIDNode node = (IFIDNode) elements.nextElement();
	    NodeTags tags = node.getTags();
	    int wendy = tags.getIntValue("wendy", 0);
	    if (wendy > 0) {
		int lowerPart = wendy & (1 << _flagIndex) - 1;
		int upperPart = wendy >> _flagIndex + 1 << _flagIndex;
		int newWendy = lowerPart | upperPart;
		tags.setIntValue("wendy", newWendy);
	    }
	}
    }
    
    public void renameFlag(String _originalFlag, String _newFlag) {
	if (myWendyFlags.contains(_newFlag))
	    throw new IllegalArgumentException
		      ("There is already a WendyFlag named '" + _newFlag
		       + "'.");
	int index = myWendyFlags.indexOf(_originalFlag);
	myWendyFlags.setElementAt(_newFlag, index);
    }
    
    public int getIndexOf(String _wendyFlag) {
	int index = myWendyFlags.indexOf(_wendyFlag);
	return index;
    }
    
    public void removeAllFlags() {
	myWendyFlags.removeAllElements();
	myRemovedIndexes.removeAllElements();
	myOriginalCount = 0;
    }
    
    public boolean isWendyFlagSet(int _nodeWendyValue, int _flagIndex) {
	if (_nodeWendyValue > 0 && (_nodeWendyValue & 1 << _flagIndex) != 0)
	    return true;
	return false;
    }
    
    public boolean isWendyFlagSet(int _nodeWendyValue, String _wendyFlag) {
	int index = getIndexOf(_wendyFlag);
	return isWendyFlagSet(_nodeWendyValue, index);
    }
    
    protected int[] getRemovedIndexes() {
	int size = myRemovedIndexes.size();
	int[] removedIndexes = new int[size];
	for (int i = 0; i < size; i++) {
	    Integer removedIndexInteger
		= (Integer) myRemovedIndexes.elementAt(i);
	    removedIndexes[i] = removedIndexInteger.intValue();
	}
	return removedIndexes;
    }
}
