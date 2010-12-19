/* ColumnEditorAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JTable;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.dialog.ColumnEditorDialog;
import org.jempeg.nodestore.IFIDPlaylistWrapper;
import org.jempeg.nodestore.model.IPlaylistTableModel;
import org.jempeg.nodestore.model.NodeTag;
import org.jempeg.nodestore.model.PlaylistTableUtils;

public class ColumnEditorAction extends MouseAdapter implements ActionListener
{
    private ApplicationContext myContext;
    private JTable myTable;
    
    public ColumnEditorAction(ApplicationContext _context, JTable _table) {
	myContext = _context;
	myTable = _table;
    }
    
    public void mouseClicked(MouseEvent _event) {
	if ((_event.getModifiers() & 0x4) > 0)
	    editColumns();
    }
    
    public void actionPerformed(ActionEvent _event) {
	editColumns();
    }
    
    public void editColumns() {
	JTable table = myTable == null ? myContext.getTable() : myTable;
	IPlaylistTableModel playlistTableModel
	    = PlaylistTableUtils.getPlaylistTableModel(table.getModel());
	IFIDPlaylistWrapper playlistWrapper
	    = PlaylistTableUtils.getPlaylistWrapper(table.getModel());
	int fidType;
	if (playlistWrapper != null)
	    fidType = playlistWrapper.getPlaylist().getContainedType();
	else
	    fidType = -1;
	String[] originalColumns;
	if (playlistTableModel != null)
	    originalColumns = playlistTableModel.getColumnTagNames();
	else
	    originalColumns = PlaylistTableUtils.getColumnTagNames();
	ColumnEditorDialog columnEditor
	    = new ColumnEditorDialog(myContext.getFrame(), originalColumns);
	columnEditor.setVisible(true);
	String[] newColumns = columnEditor.getColumns();
	PlaylistTableUtils.setColumnTagNames(fidType, newColumns);
	NodeTag[] nodeTags = NodeTag.getNodeTags();
	for (int i = 0; i < nodeTags.length; i++) {
	    String tagName = nodeTags[i].getName();
	    int width = columnEditor.getColumnWidth(tagName);
	    PlaylistTableUtils.setColumnWidth(tagName, width);
	}
	try {
	    PropertiesManager.getInstance().save();
	} catch (IOException e) {
	    Debug.println(e);
	}
	if (playlistTableModel != null)
	    playlistTableModel.setColumnTagNames(newColumns);
	columnEditor.dispose();
    }
}
