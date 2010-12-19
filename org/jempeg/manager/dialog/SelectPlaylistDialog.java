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
import javax.swing.ListSelectionModel;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.action.NewRootLevelPlaylistAction;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDConstants;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.model.NodeFinder;

import com.inzyme.model.VectorListModel;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.ConfirmationPanel;
import com.inzyme.ui.DialogUtils;
import com.inzyme.util.Debug;

public class SelectPlaylistDialog extends JDialog {
	private ConfirmationPanel myConfirmationPanel;
	private ApplicationContext myContext;
	private JList myPlaylistList;
	private IFIDNode[] myNodes;

	public SelectPlaylistDialog(JFrame _frame, ApplicationContext _context, String _title) {
		super(_frame, _title, true);

		myContext = _context;

		try {
			String createNewPlaylist = ResourceBundleUtils.getUIString("selectPlaylist.createNewPlaylist");
			myNodes = new NodeFinder(_context.getPlayerDatabase()).findMatchingNodes("type=playlist and !" + DatabaseTags.JEMPLODE_IS_SOUP_TAG + " and fid != " + FIDConstants.FID_ROOTPLAYLIST);
			Vector playlistTitlesVec = new Vector();
			playlistTitlesVec.addElement(createNewPlaylist);
			for (int i = 0; i < myNodes.length; i++) {
				playlistTitlesVec.addElement(myNodes[i].getTitle());
			}
			// myPlaylist = SoupUtils.createTransientSoupPlaylist(_context.getPlayerDatabase(), ResourceBundleUtils.getUIString("selectPlaylist.createNewPlaylist"), "search:type=playlist and fid != " + FIDConstants.FID_ROOTPLAYLIST, false, false, null);
			VectorListModel listModel = new VectorListModel(playlistTitlesVec);

			myPlaylistList = new JList(listModel);
			myPlaylistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			myPlaylistList.addMouseListener(new OkListener());
			JScrollPane jsp = new JScrollPane(myPlaylistList);

			myConfirmationPanel = new ConfirmationPanel(jsp);
			myConfirmationPanel.addOkListener(new OkListener());
			myConfirmationPanel.addCancelListener(new CancelListener());

			getContentPane().add(myConfirmationPanel);
			pack();
			DialogUtils.centerWindow(this);
		}
		catch (ParseException e) {
			Debug.handleError(_frame, e, true);
		}
	}

	public FIDPlaylist getSelectedPlaylist() {
		FIDPlaylist selectedPlaylist;
		int selectedIndex = myPlaylistList.getSelectedIndex();
		if (selectedIndex != -1) {
			if (selectedIndex == 0) {
				selectedPlaylist = NewRootLevelPlaylistAction.createPlaylist(myContext);
			}
			else {
				selectedPlaylist = (FIDPlaylist) myNodes[selectedIndex - 1];
			}
		}
		else {
			selectedPlaylist = null;
		}
		return selectedPlaylist;
	}

	protected class OkListener extends MouseAdapter implements ActionListener {
		public void mouseClicked(MouseEvent _event) {
			if (((_event.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) && _event.getClickCount() == 2) {
				myConfirmationPanel.ok();
			}
		}

		public void actionPerformed(ActionEvent _event) {
			setVisible(false);
		}
	}

	protected class CancelListener implements ActionListener {
		public void actionPerformed(ActionEvent _event) {
			setVisible(false);
		}
	}
}
