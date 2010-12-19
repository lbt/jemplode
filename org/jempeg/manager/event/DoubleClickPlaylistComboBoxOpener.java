package org.jempeg.manager.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDPlaylistWrapper;

import com.inzyme.container.ContainerSelection;

public class DoubleClickPlaylistComboBoxOpener extends MouseAdapter implements ActionListener {
	private ApplicationContext myContext;
	private JComboBox myComboBox;

	/**
	* Constructs a DoubleClickPlaylistComboBoxOpener.
	*
	* @param _context the context to work within
	* @param _comboBox the ComboBox to modify selections for
	*/
	public DoubleClickPlaylistComboBoxOpener(ApplicationContext _context, JComboBox _comboBox) {
		myContext = _context;
		myComboBox = _comboBox;
	}
	
	public void actionPerformed(ActionEvent _event) {
		open();
	}

	public void mouseClicked(MouseEvent _event) {
		if (_event.getClickCount() == 2 && ((_event.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)) {
			open();
		}
	}
	
	protected void open() {
		ContainerSelection selection = myContext.getSelection();
		if (selection != null && selection.getSize() == 1) {
			Object selectedObj = selection.getValueAt(0);
			if (selectedObj instanceof IFIDPlaylistWrapper) {
				FIDPlaylist selectedPlaylist = ((IFIDPlaylistWrapper)selectedObj).getPlaylist();
				ComboBoxModel model = myComboBox.getModel();
				int foundIndex = -1;
				for (int i = 0; foundIndex == -1 && i < model.getSize(); i ++) {
					Object obj = model.getElementAt(i);
					if (obj instanceof IFIDPlaylistWrapper) {
						FIDPlaylist playlist = ((IFIDPlaylistWrapper)obj).getPlaylist();
						if (playlist.equals(selectedPlaylist)) {
							foundIndex = i;
						}
					}
				}
				if (foundIndex != -1) {
					myComboBox.setSelectedIndex(foundIndex);
				}
			}
		}
	} 
}
