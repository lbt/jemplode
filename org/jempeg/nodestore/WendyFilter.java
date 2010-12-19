package org.jempeg.nodestore;

import java.util.Hashtable;

/**
 * WendyFilter represents a single Wendy
 * Filter on the Empeg.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.2 $
 */
public class WendyFilter {
	private String myName;
	private Hashtable myFlagToState;
	
	/**
	 * Constructs a new WendyFilter
	 */
	public WendyFilter(String _name) {
		myName = _name;
		myFlagToState = new Hashtable();
	}

	/**
	 * Creates a ConfigFile format String out of this Filter.
	 * 
	 * @param _wendyFlags the flags to use to create the string
	 * @return a ConfigFile format String out of this Filter
	 */
	public String toConfigFormat(WendyFlags _wendyFlags) {
		int left = 0;
		int right = 0;
		
		String[] flags = _wendyFlags.getFlags();
		for (int i = 0; i < flags.length; i ++) {
			int bitmask = 1 << i;
			int state = getState(flags[i]);
			if (state == 1 || state == 2) {
				left |= bitmask;
			}
			if (state == 1) {
				right |= bitmask;
			}
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(myName);
		sb.append(",");
		sb.append(left);
		sb.append(",");
		sb.append(right);
		return sb.toString();
	}
	
	/**
	 * Fills in all the settings for this Wendy Filter given a
	 * ConfigFile format String.
	 * 
	 * @param _wendyFlags the flags to use to create the filter
	 * @param _configFormat the ConfigFile format String
	 */
	public void fromConfigFormat(WendyFlags _wendyFlags, String _configFormat) {
		removeAllFlags();
		
		int nameIndex = _configFormat.indexOf(",");
		myName = _configFormat.substring(0, nameIndex);

		int commaIndex = _configFormat.indexOf(",", nameIndex + 1);
		int left = Integer.parseInt(_configFormat.substring(nameIndex + 1, commaIndex));
		int right = Integer.parseInt(_configFormat.substring(commaIndex + 1));
		
		String[] flags = _wendyFlags.getFlags();
		for (int i = 0; i < flags.length; i ++) {
			int state;
			int bitmask = 1 << i;
			boolean leftOn = (left & bitmask) != 0;
			boolean rightOn = (right & bitmask) != 0;
			if (leftOn) {
				if (rightOn) {
					state = 1;
				} else {
					state = 2;
				}
			} else {
				if (rightOn) {
					state = 2;
				} else {
					state = 0;
				}
			}
			setState(flags[i], state);
		}
	}

	/**
	 * Sets the name of this Filter.
	 * 
	 * @param _name the name of this Filter
	 */			
	public void setName(String _name) {
		myName = _name;
	}
	
	/**
	 * Returns the name of this Filter.
	 * 
	 * @return the name of this Filter
	 */			
	public String getName() {
		return myName;
	}

	/**
	 * Gets the state of the given Wendy Flag.
	 * 
	 * @param _wendyFlag the flag to lookup
	 * @return the state of the given Wendy Flag
	 */
	public int getState(String _wendyFlag) {
		Integer stateInteger = (Integer)myFlagToState.get(_wendyFlag);
		int state;
		if (stateInteger == null) {
			state = 0;
		} else {
			state = stateInteger.intValue();
		}
		return state;
	}
	
	/**
	 * Sets the state of the given Wendy Flag.
	 * 
	 * @param _wendyFlag the flag to set
	 * @param _state the new state of the Wendy Flag
	 */
	public void setState(String _wendyFlag, int _state) {
		myFlagToState.put(_wendyFlag, new Integer(_state));
	}
	
	/**
	 * Returns a clone of the FlagToState Hashtable.
	 * 
	 * @return a clone of the FlagToState Hashtable
	 */
	public Hashtable getFlagToState() {
		return (Hashtable)myFlagToState.clone();
	}
	
	/**
	 * Sets the FlagToState Hashtable.
	 * 
	 * @param _flagToState the FlagToState Hashtable
	 */
	public void setFlagToState(Hashtable _flagToState) {
		myFlagToState = _flagToState;
	}
	
	/**
	 * Removes the given Flag.
	 * 
	 * @param _wendyFlag the flag to remove
	 */
	public void removeFlag(String _wendyFlag) {
		myFlagToState.remove(_wendyFlag);
	}

	/**
	 * Renames the given Flag.
	 * 
	 * @param _origFlagName the original flag name
	 * @param _newFlagName the new flag name
	 */
	public void renameFlag(String _origFlagName, String _newFlagName) {
		int state = getState(_origFlagName);
		removeFlag(_origFlagName);
		setState(_newFlagName, state);
	}

	/**
	 * Removes all flags from this filter.
	 */
	public void removeAllFlags() {
		myFlagToState.clear();
	}
	
	public String toString() {
		return myName;
	}
}
