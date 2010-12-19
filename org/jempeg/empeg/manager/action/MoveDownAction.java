/* MoveDownAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.inzyme.container.ContainerSelection;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDPlaylistWrapper;

public class MoveDownAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public MoveDownAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	ContainerSelection selection = myContext.getSelection();
	if (selection != null) {
	    com.inzyme.container.IContainer container
		= selection.getContainer();
	    if (container instanceof IFIDPlaylistWrapper) {
		FIDPlaylist playlist
		    = ((IFIDPlaylistWrapper) container).getPlaylist();
		int[] selectedIndexes = selection.getSelectedIndexes();
		int[] newIndexes = playlist.reposition(selectedIndexes, 1);
		myContext.setSelection(this,
				       new ContainerSelection(myContext,
							      playlist,
							      newIndexes));
	    }
	}
    }
}
