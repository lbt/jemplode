/* NewPlaylistAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.inzyme.container.ContainerSelection;
import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.dialog.PlaylistPropertiesDialog;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.IFIDPlaylistWrapper;

public class NewPlaylistAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public NewPlaylistAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	com.inzyme.container.IContainer container
	    = myContext.getSelectedContainer();
	if (container instanceof IFIDPlaylistWrapper) {
	    FIDPlaylist parentPlaylist
		= ((IFIDPlaylistWrapper) container).getPlaylist();
	    String playlistTitle
		= ((String)
		   (JOptionPane.showInputDialog
		    (myContext.getFrame(),
		     ResourceBundleUtils.getUIString("newPlaylist.prompt"),
		     ResourceBundleUtils.getUIString("newPlaylist.frameTitle"),
		     3, null, null,
		     ResourceBundleUtils.getUIString("newPlaylist.title"))));
	    if (playlistTitle != null) {
		int index = parentPlaylist.createPlaylist(playlistTitle);
		myContext.setSelection(this,
				       new ContainerSelection(myContext,
							      parentPlaylist,
							      (new int[]
							       { index })));
		PlaylistPropertiesDialog playlistDialog
		    = new PlaylistPropertiesDialog(myContext.getFrame());
		playlistDialog.setNodes(new IFIDNode[]
					{ parentPlaylist
					      .getPlaylistAt(index) });
		playlistDialog.setVisible(true);
		playlistDialog.dispose();
	    }
	}
    }
}
