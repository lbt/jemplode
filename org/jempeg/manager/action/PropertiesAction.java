/* PropertiesAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.AbstractAction;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.dialog.AbstractPropertiesDialog;
import org.jempeg.manager.dialog.MixedPropertiesDialog;
import org.jempeg.manager.dialog.PlaylistPropertiesDialog;
import org.jempeg.manager.dialog.TunePropertiesDialog;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;

public class PropertiesAction extends AbstractAction implements MouseListener
{
    private ApplicationContext myContext;
    
    public PropertiesAction(ApplicationContext _context) {
	super("Properties");
	myContext = _context;
    }
    
    public void performAction() {
	boolean playlistsSelected = false;
	boolean tunesSelected = false;
	ContainerSelection selection = myContext.getSelection();
	if (selection != null) {
	    int size = selection.getSize();
	    Vector nodeVec = new Vector();
	    for (int i = 0; i < size; i++) {
		Object obj = selection.getValueAt(i);
		if (obj instanceof IFIDNode) {
		    if (obj instanceof FIDPlaylist)
			playlistsSelected = true;
		    else
			tunesSelected = true;
		    IFIDNode node = (IFIDNode) obj;
		    nodeVec.addElement(node);
		}
	    }
	    IFIDNode[] selectedNodes = new IFIDNode[nodeVec.size()];
	    nodeVec.copyInto(selectedNodes);
	    if (selectedNodes.length > 0) {
		AbstractPropertiesDialog dlg = null;
		if (playlistsSelected) {
		    if (tunesSelected)
			dlg = createMixedDialog();
		    else
			dlg = createPlaylistDialog();
		} else if (tunesSelected)
		    dlg = createTuneDialog();
		if (dlg != null) {
		    show(dlg, selectedNodes);
		    dlg.dispose();
		}
	    }
	}
    }
    
    protected AbstractPropertiesDialog createTuneDialog() {
	TunePropertiesDialog tuneDialog
	    = new TunePropertiesDialog(myContext.getFrame());
	return tuneDialog;
    }
    
    protected AbstractPropertiesDialog createPlaylistDialog() {
	PlaylistPropertiesDialog playlistDialog
	    = new PlaylistPropertiesDialog(myContext.getFrame());
	return playlistDialog;
    }
    
    protected AbstractPropertiesDialog createMixedDialog() {
	MixedPropertiesDialog mixedDialog
	    = new MixedPropertiesDialog(myContext.getFrame());
	return mixedDialog;
    }
    
    protected void show(AbstractPropertiesDialog _dialog,
			IFIDNode[] _selectedNodes) {
	_dialog.setNodes(_selectedNodes);
	_dialog.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent _event) {
	performAction();
    }
    
    public void mousePressed(MouseEvent _event) {
	/* empty */
    }
    
    public void mouseReleased(MouseEvent _event) {
	/* empty */
    }
    
    public void mouseEntered(MouseEvent _event) {
	/* empty */
    }
    
    public void mouseExited(MouseEvent _event) {
	/* empty */
    }
    
    public void mouseClicked(MouseEvent _event) {
	if (!_event.isConsumed() && _event.getClickCount() == 2
	    && (_event.getModifiers() & 0x10) > 0) {
	    boolean doubleClickedPlaylists = false;
	    ContainerSelection selection = myContext.getSelection();
	    if (selection != null) {
		Object[] values = selection.getSelectedValues();
		for (int i = 0; !doubleClickedPlaylists && i < values.length;
		     i++) {
		    if (values[i] instanceof IContainer)
			doubleClickedPlaylists = true;
		}
		if (!doubleClickedPlaylists)
		    performAction();
	    }
	}
    }
}
