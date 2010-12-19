/* PersistentSoupAttacher - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.soup;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Vector;

import com.inzyme.util.Debug;

import org.jempeg.nodestore.FIDNodeMap;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IDatabaseChange;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.nodestore.event.ISynchronizeClientListener;

public class PersistentSoupAttacher implements ISynchronizeClientListener
{
    private boolean myThreaded;
    
    public PersistentSoupAttacher(boolean _threaded) {
	myThreaded = _threaded;
    }
    
    public void downloadCompleted(PlayerDatabase _playerDatabase) {
	Vector soupPlaylistsVec = new Vector();
	FIDNodeMap nodeMap = _playerDatabase.getNodeMap();
	Enumeration nodesEnum = nodeMap.elements();
	while (nodesEnum.hasMoreElements()) {
	    IFIDNode node = (IFIDNode) nodesEnum.nextElement();
	    if (node instanceof FIDPlaylist) {
		FIDPlaylist playlist = (FIDPlaylist) node;
		if (!playlist.isTransient()
		    && playlist.getTags().getValue("soup").length() > 0) {
		    playlist.makeSoupy();
		    soupPlaylistsVec.addElement(playlist);
		}
	    }
	}
	Enumeration soupPlaylistsEnum = soupPlaylistsVec.elements();
	while (soupPlaylistsEnum.hasMoreElements()) {
	    try {
		FIDPlaylist soupPlaylist
		    = (FIDPlaylist) soupPlaylistsEnum.nextElement();
		String soupExternalForm
		    = soupPlaylist.getTags().getValue("soup");
		ISoupLayer[] soupLayers
		    = SoupLayerFactory.fromExternalForm(soupExternalForm);
		SoupUtils.attachSoup(soupPlaylist, soupLayers, myThreaded,
				     null);
	    } catch (ParseException e) {
		Debug.println(e);
	    }
	}
    }
    
    public void downloadStarted(PlayerDatabase _playerDatabase) {
	/* empty */
    }
    
    public void synchronizeCompleted(IDatabaseChange _databaseChange,
				     boolean _successfully) {
	/* empty */
    }
    
    public void synchronizeCompleted(PlayerDatabase _playerDatabase,
				     boolean _succesfully) {
	/* empty */
    }
    
    public void synchronizeInProgress(IDatabaseChange _databaseChange,
				      long _current, long _total) {
	/* empty */
    }
    
    public void synchronizeStarted(IDatabaseChange _databaseChange) {
	/* empty */
    }
    
    public void synchronizeStarted(PlayerDatabase _playerDatabase) {
	/* empty */
    }
}
