/* NodeFinder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.model;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Vector;

import com.inzyme.container.ContainerUtils;
import com.inzyme.text.CollationKeyCache;

import org.jempeg.nodestore.FIDNodeMap;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.predicate.IPredicate;
import org.jempeg.nodestore.predicate.PredicateParser;
import org.jempeg.nodestore.soup.ISoupLayer;
import org.jempeg.nodestore.soup.SearchSoupLayer;
import org.jempeg.nodestore.soup.SoupUtils;

public class NodeFinder
{
    private PlayerDatabase myDB;
    
    public NodeFinder(PlayerDatabase _db) {
	myDB = _db;
    }
    
    public IFIDNode[] findMatchingNodes(IPredicate _predicate) {
	Vector fidNodesVec = new Vector();
	CollationKeyCache cache = CollationKeyCache.createDefaultCache();
	FIDNodeMap nodeMap = myDB.getNodeMap();
	Enumeration fidNodesEnum = nodeMap.elements();
	while (fidNodesEnum.hasMoreElements()) {
	    IFIDNode fidNode = (IFIDNode) fidNodesEnum.nextElement();
	    if (_predicate.evaluate(fidNode)) {
		TagValueSortableContainer sortedFidNodesVec
		    = new TagValueSortableContainer(fidNodesVec);
		int sortedIndex
		    = ContainerUtils.getSortedIndex(sortedFidNodesVec,
						    NodeTag.TITLE_TAG, fidNode,
						    cache);
		fidNodesVec.insertElementAt(fidNode, sortedIndex);
	    }
	}
	IFIDNode[] fidNodes = new IFIDNode[fidNodesVec.size()];
	fidNodesVec.copyInto(fidNodes);
	return fidNodes;
    }
    
    public IFIDNode[] findMatchingNodes(String _queryString)
	throws ParseException {
	IPredicate predicate = new PredicateParser().parse(_queryString);
	return findMatchingNodes(predicate);
    }
    
    public FIDPlaylist findMatches(String _name, String _searchString,
				   boolean _temporary) throws ParseException {
	IPredicate predicate = new PredicateParser().parse(_searchString);
	FIDPlaylist searchResultsPlaylist
	    = findMatches(_name, predicate, _temporary);
	return searchResultsPlaylist;
    }
    
    public FIDPlaylist findMatches(String _name, IPredicate _predicate,
				   boolean _temporary) throws ParseException {
	FIDPlaylist searchResultsPlaylist
	    = (SoupUtils.createTransientSoupPlaylist
	       (myDB, _name == null ? _predicate.toString() : _name,
		new ISoupLayer[] { new SearchSoupLayer(_predicate) }, false,
		_temporary, false, null));
	return searchResultsPlaylist;
    }
}
