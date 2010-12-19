/* SelectPlaylistDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.inzyme.model.VectorListModel;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.action.NewRootLevelPlaylistAction;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeFinder;

public class SelectPlaylistDialog extends JDialog
{
    private ConfirmationPanel myConfirmationPanel;
    private ApplicationContext myContext;
    private JList myPlaylistList;
    private IFIDNode[] myNodes;
    
    protected class OkListener extends MouseAdapter implements ActionListener
    {
	public void mouseClicked(MouseEvent _event) {
	    if ((_event.getModifiers() & 0x10) != 0
		&& _event.getClickCount() == 2)
		myConfirmationPanel.ok();
	}
	
	public void actionPerformed(ActionEvent _event) {
	    SelectPlaylistDialog.this.setVisible(false);
	}
    }
    
    protected class CancelListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    SelectPlaylistDialog.this.setVisible(false);
	}
    }
    
    public SelectPlaylistDialog(JFrame _frame, ApplicationContext _context,
				String _title) {
	super(_frame, _title, true);
	myContext = _context;
	try {
	    String createNewPlaylist
		= ResourceBundleUtils
		      .getUIString("selectPlaylist.createNewPlaylist");
	    myNodes = (new NodeFinder(_context.getPlayerDatabase())
			   .findMatchingNodes
		       ("type=playlist and !jemplode_issoup and fid != 256"));
	    Vector playlistTitlesVec = new Vector();
	    playlistTitlesVec.addElement(createNewPlaylist);
	    for (int i = 0; i < myNodes.length; i++)
		playlistTitlesVec.addElement(myNodes[i].getTitle());
	    VectorListModel listModel = new VectorListModel(playlistTitlesVec);
	    myPlaylistList = new JList(listModel);
	    myPlaylistList.setSelectionMode(0);
	    myPlaylistList.addMouseListener(new OkListener());
	    JScrollPane jsp = new JScrollPane(myPlaylistList);
	    myConfirmationPanel = new ConfirmationPanel(jsp);
	    myConfirmationPanel.addOkListener(new OkListener());
	    myConfirmationPanel.addCancelListener(new CancelListener());
	    getContentPane().add(myConfirmationPanel);
	    pack();
	    DialogUtils.centerWindow(this);
	} catch (ParseException e) {
	    Debug.handleError(_frame, e, true);
	}
    }
    
    public FIDPlaylist getSelectedPlaylist() {
	int selectedIndex = myPlaylistList.getSelectedIndex();
	FIDPlaylist selectedPlaylist;
	if (selectedIndex != -1) {
	    if (selectedIndex == 0)
		selectedPlaylist
		    = NewRootLevelPlaylistAction.createPlaylist(myContext);
	    else
		selectedPlaylist = (FIDPlaylist) myNodes[selectedIndex - 1];
	} else
	    selectedPlaylist = null;
	return selectedPlaylist;
    }
}
