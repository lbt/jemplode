/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package org.jempeg.nodestore.model;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.Vector;

import org.jempeg.nodestore.FIDNodeMap;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.predicate.IPredicate;
import org.jempeg.nodestore.predicate.PredicateParser;
import org.jempeg.nodestore.soup.ISoupLayer;
import org.jempeg.nodestore.soup.SearchSoupLayer;
import org.jempeg.nodestore.soup.SoupUtils;

import com.inzyme.container.ContainerUtils;
import com.inzyme.text.CollationKeyCache;

/**
 * NodeFinder is used to perform queries
 * against a PlayerDatabase.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.5 $
 */
public class NodeFinder {
  private PlayerDatabase myDB;

  public NodeFinder(PlayerDatabase _db) {
    myDB = _db;
  }
	
	/**
	 * Returns the set of IFIDNodes that match the given predicate.
	 * 
	 * @param _predicate the predicate to search for
	 * @return the set of IFIDNodes that match the given predicate
	 */
	public IFIDNode[] findMatchingNodes(IPredicate _predicate) {
		Vector fidNodesVec = new Vector();
		CollationKeyCache cache = CollationKeyCache.createDefaultCache();
    FIDNodeMap nodeMap = myDB.getNodeMap();
    Enumeration fidNodesEnum = nodeMap.elements();
    while (fidNodesEnum.hasMoreElements()) {
      IFIDNode fidNode = (IFIDNode)fidNodesEnum.nextElement();
      if (_predicate.evaluate(fidNode)) {
				TagValueSortableContainer sortedFidNodesVec = new TagValueSortableContainer(fidNodesVec);
				int sortedIndex = ContainerUtils.getSortedIndex(sortedFidNodesVec, NodeTag.TITLE_TAG, fidNode, cache);
				fidNodesVec.insertElementAt(fidNode, sortedIndex);
      }
    }
    IFIDNode[] fidNodes = new IFIDNode[fidNodesVec.size()];
    fidNodesVec.copyInto(fidNodes);
    return fidNodes;
	}
	
	/**
	 * Returns the set of IFIDNodes that match the given query string.
	 * 
	 * @param _queryString the query string to search for
	 * @return the set of IFIDNodes that match the given predicate
	 * @throws ParseException if the query string cannot be parsed
	 */
	public IFIDNode[] findMatchingNodes(String _queryString) throws ParseException {
		IPredicate predicate = new PredicateParser().parse(_queryString);
		return findMatchingNodes(predicate);
	}

  public FIDPlaylist findMatches(String _name, String _searchString, boolean _temporary) throws ParseException {
		IPredicate predicate = new PredicateParser().parse(_searchString);
    FIDPlaylist searchResultsPlaylist = findMatches(_name, predicate, _temporary);
    return searchResultsPlaylist;
  }

  public FIDPlaylist findMatches(String _name, IPredicate _predicate, boolean _temporary) throws ParseException {
		FIDPlaylist searchResultsPlaylist = SoupUtils.createTransientSoupPlaylist(myDB, (_name == null) ? _predicate.toString() : _name, new ISoupLayer[] { new SearchSoupLayer(_predicate) }, false, _temporary, false, null);
		return searchResultsPlaylist;
  }
}

