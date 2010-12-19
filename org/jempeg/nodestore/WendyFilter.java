/* WendyFilter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.util.Hashtable;

public class WendyFilter
{
    private String myName;
    private Hashtable myFlagToState;
    
    public WendyFilter(String _name) {
	myName = _name;
	myFlagToState = new Hashtable();
    }
    
    public String toConfigFormat(WendyFlags _wendyFlags) {
	int left = 0;
	int right = 0;
	String[] flags = _wendyFlags.getFlags();
	for (int i = 0; i < flags.length; i++) {
	    int bitmask = 1 << i;
	    int state = getState(flags[i]);
	    if (state == 1 || state == 2)
		left |= bitmask;
	    if (state == 1)
		right |= bitmask;
	}
	StringBuffer sb = new StringBuffer();
	sb.append(myName);
	sb.append(",");
	sb.append(left);
	sb.append(",");
	sb.append(right);
	return sb.toString();
    }
    
    public void fromConfigFormat(WendyFlags _wendyFlags,
				 String _configFormat) {
	removeAllFlags();
	int nameIndex = _configFormat.indexOf(",");
	myName = _configFormat.substring(0, nameIndex);
	int commaIndex = _configFormat.indexOf(",", nameIndex + 1);
	int left = Integer.parseInt(_configFormat.substring(nameIndex + 1,
							    commaIndex));
	int right = Integer.parseInt(_configFormat.substring(commaIndex + 1));
	String[] flags = _wendyFlags.getFlags();
	for (int i = 0; i < flags.length; i++) {
	    int bitmask = 1 << i;
	    boolean leftOn = (left & bitmask) != 0;
	    boolean rightOn = (right & bitmask) != 0;
	    int state;
	    if (leftOn) {
		if (rightOn)
		    state = 1;
		else
		    state = 2;
	    } else if (rightOn)
		state = 2;
	    else
		state = 0;
	    setState(flags[i], state);
	}
    }
    
    public void setName(String _name) {
	myName = _name;
    }
    
    public String getName() {
	return myName;
    }
    
    public int getState(String _wendyFlag) {
	Integer stateInteger = (Integer) myFlagToState.get(_wendyFlag);
	int state;
	if (stateInteger == null)
	    state = 0;
	else
	    state = stateInteger.intValue();
	return state;
    }
    
    public void setState(String _wendyFlag, int _state) {
	myFlagToState.put(_wendyFlag, new Integer(_state));
    }
    
    public Hashtable getFlagToState() {
	return (Hashtable) myFlagToState.clone();
    }
    
    public void setFlagToState(Hashtable _flagToState) {
	myFlagToState = _flagToState;
    }
    
    public void removeFlag(String _wendyFlag) {
	myFlagToState.remove(_wendyFlag);
    }
    
    public void renameFlag(String _origFlagName, String _newFlagName) {
	int state = getState(_origFlagName);
	removeFlag(_origFlagName);
	setState(_newFlagName, state);
    }
    
    public void removeAllFlags() {
	myFlagToState.clear();
    }
    
    public String toString() {
	return myName;
    }
}
