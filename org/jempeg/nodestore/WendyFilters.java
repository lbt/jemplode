/* WendyFilters - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import java.util.Vector;

public class WendyFilters
{
    private WendyFlags myWendyFlags = new WendyFlags();
    private Vector myWendyFilters = new Vector();
    
    public WendyFlags getWendyFlags() {
	return myWendyFlags;
    }
    
    public void addFilter(WendyFilter _filter) {
	myWendyFilters.addElement(_filter);
    }
    
    public void removeFilter(WendyFilter _filter) {
	myWendyFilters.removeElement(_filter);
    }
    
    public int getSize() {
	int size = myWendyFilters.size();
	return size;
    }
    
    public WendyFilter getFilterAt(int _index) {
	WendyFilter filter = (WendyFilter) myWendyFilters.elementAt(_index);
	return filter;
    }
    
    public WendyFilter getFilter(String _name) {
	WendyFilter matchingFilter = null;
	int size = myWendyFilters.size();
	for (int i = 0; matchingFilter == null && i < size; i++) {
	    WendyFilter filter = (WendyFilter) myWendyFilters.elementAt(i);
	    if (filter.getName().equals(_name))
		matchingFilter = filter;
	}
	return matchingFilter;
    }
    
    public int getIndexOf(WendyFilter _filter) {
	int index = myWendyFilters.indexOf(_filter);
	return index;
    }
    
    public void removeFlag(String _wendyFlag) {
	myWendyFlags.removeFlag(_wendyFlag);
	int size = getSize();
	for (int i = 0; i < size; i++) {
	    WendyFilter filter = getFilterAt(i);
	    filter.removeFlag(_wendyFlag);
	}
    }
    
    public void renameFlag(String _originalFlag, String _newFlag) {
	myWendyFlags.renameFlag(_originalFlag, _newFlag);
	int size = getSize();
	for (int i = 0; i < size; i++) {
	    WendyFilter filter = getFilterAt(i);
	    filter.renameFlag(_originalFlag, _newFlag);
	}
    }
}
