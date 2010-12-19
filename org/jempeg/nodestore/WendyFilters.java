package org.jempeg.nodestore;

import java.util.Vector;

/**
 * WendyFilters provides an interface to managing
 * Wendy Filters on the Empeg.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.2 $
 */
public class WendyFilters {
	private WendyFlags myWendyFlags;
	private Vector myWendyFilters;

	/**
	 * Consturcts a new WendyFilters
	 */
	public WendyFilters() {
		myWendyFlags = new WendyFlags();
		myWendyFilters = new Vector();
	}
	
	/**
	 * Returns the WendyFlags for these filters.
	 * 
	 * @return the WendyFlags for these filters
	 */
	public WendyFlags getWendyFlags() {
		return myWendyFlags;
	}
	
	/**
	 * Adds a new Wendy Filter to this model.
	 * 
	 * @param _filter the Wendy filter to add
	 */
	public void addFilter(WendyFilter _filter) {
		myWendyFilters.addElement(_filter);
	}
	
	/**
	 * Removes a Wendy Filter from this model.
	 * 
	 * @param _filter the Wendy filter to remove
	 */
	public void removeFilter(WendyFilter _filter) {
		myWendyFilters.removeElement(_filter);
	}

	/**
	 * Returns the number of Filters in this model.
	 * 
	 * @return the number of Filters in this model
	 */
	public int getSize() {
		int size = myWendyFilters.size();
		return size;
	}
	
	/**
	 * Returns the Wendy Filter at the given index.
	 * 
	 * @param _index the index to lookup
	 * @return the Wendy Filter at the given index
	 */
	public WendyFilter getFilterAt(int _index) {
		WendyFilter filter = (WendyFilter)myWendyFilters.elementAt(_index);
		return filter;
	}
	
	/**
	 * Returns the WendyFilter that has the given name.
	 * 
	 * @param _name the name of the Filter to lookup
	 * @return the WendyFilter that has the given name
	 */
	public WendyFilter getFilter(String _name) {
		WendyFilter matchingFilter = null;
		int size = myWendyFilters.size();
		for (int i = 0; matchingFilter == null && i < size; i ++) {
			WendyFilter filter = (WendyFilter)myWendyFilters.elementAt(i);
			if (filter.getName().equals(_name)) {
				matchingFilter = filter;
			}
		}
		return matchingFilter;
	}
	
	/**
	 * Returns the index of the given WendyFilter.
	 * 
	 * @param _name the name of the filter to lookup
	 * @return the index of the given WendyFilter
	 */
	public int getIndexOf(WendyFilter _filter) {
		int index = myWendyFilters.indexOf(_filter);
		return index;
	}
	
	/**
	 * Removes a Wendy Flag from this model.
	 * 
	 * @param _wendyFlag the Wendy flag to remove
	 */
	public void removeFlag(String _wendyFlag) {
		myWendyFlags.removeFlag(_wendyFlag);
		int size = getSize();
		for (int i = 0; i < size; i ++) {
			WendyFilter filter = getFilterAt(i);
			filter.removeFlag(_wendyFlag);
		}
	}

	/**
	 * Rename a flag.
	 * 
	 * @param _origianlFlag the original name of the flag
	 * @param _newFlag the new name of the flag
	 */
	public void renameFlag(String _originalFlag, String _newFlag) {
		myWendyFlags.renameFlag(_originalFlag, _newFlag);
		int size = getSize();
		for (int i = 0; i < size; i ++) {
			WendyFilter filter = getFilterAt(i);
			filter.renameFlag(_originalFlag, _newFlag);
		}
	}
}
