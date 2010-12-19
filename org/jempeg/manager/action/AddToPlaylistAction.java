/* AddToPlaylistAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.container.ContainerSelection;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.dialog.SelectPlaylistDialog;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public class AddToPlaylistAction extends AbstractAction
{
    private ApplicationContext myContext;
    private boolean myAllowPlaylistSelection;
    
    public AddToPlaylistAction(ApplicationContext _context,
			       boolean _allowPlaylistSelection) {
	myContext = _context;
	myAllowPlaylistSelection = _allowPlaylistSelection;
    }
    
    public void actionPerformed(ActionEvent _event) {
	ContainerSelection selection = myContext.getSelection();
	int size = selection.getSize();
	if (selection != null && size > 0) {
	    SelectPlaylistDialog selectPlaylistDialog
		= new SelectPlaylistDialog(myContext.getFrame(), myContext,
					   (ResourceBundleUtils.getUIString
					    ("addToPlaylist.frameTitle")));
	    selectPlaylistDialog.setVisible(true);
	    FIDPlaylist selectedPlaylist
		= selectPlaylistDialog.getSelectedPlaylist();
	    if (selectedPlaylist != null) {
		CollationKeyCache cache
		    = CollationKeyCache.createDefaultCache();
		for (int i = 0; i < size; i++) {
		    IFIDNode node = (IFIDNode) selection.getValueAt(i);
		    if (node instanceof FIDPlaylist
			&& !myAllowPlaylistSelection)
			selectedPlaylist
			    .addPlaylistChildren((FIDPlaylist) node, cache);
		    else
			selectedPlaylist.addNode(node, true, cache);
		}
	    }
	    selectPlaylistDialog.dispose();
	}
    }
}
