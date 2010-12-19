/* PlaylistComboBoxSelectionListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTable;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.model.AbstractFIDPlaylistModel;

public class PlaylistComboBoxSelectionListener implements ItemListener
{
    private ApplicationContext myContext;
    private JTable myTable;
    
    public PlaylistComboBoxSelectionListener(ApplicationContext _context,
					     JTable _table) {
	myContext = _context;
	myTable = _table;
    }
    
    public void itemStateChanged(ItemEvent _event) {
	org.jempeg.nodestore.FIDPlaylist selectedPlaylist;
	if (_event.getStateChange() == 2)
	    selectedPlaylist = null;
	else {
	    AbstractFIDPlaylistModel selectedPlaylistModel
		= (AbstractFIDPlaylistModel) _event.getItem();
	    selectedPlaylist = selectedPlaylistModel.getPlaylist();
	}
	if (myTable == myContext.getTable())
	    myContext.setSelectedContainer(selectedPlaylist);
    }
}
