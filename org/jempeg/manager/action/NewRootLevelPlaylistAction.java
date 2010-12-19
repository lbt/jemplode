/* NewRootLevelPlaylistAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.inzyme.text.ResourceBundleUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.nodestore.FIDPlaylist;

public class NewRootLevelPlaylistAction extends AbstractAction
{
    private ApplicationContext myContext;
    
    public NewRootLevelPlaylistAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public static FIDPlaylist createPlaylist(ApplicationContext _context) {
	String playlistTitle
	    = ((String)
	       (JOptionPane.showInputDialog
		(_context.getFrame(),
		 ResourceBundleUtils.getUIString("newPlaylist.prompt"),
		 ResourceBundleUtils.getUIString("newPlaylist.frameTitle"), 3,
		 null, null,
		 ResourceBundleUtils.getUIString("newPlaylist.title"))));
	FIDPlaylist newPlaylist;
	if (playlistTitle != null) {
	    FIDPlaylist rootPlaylist
		= _context.getPlayerDatabase().getRootPlaylist();
	    int newPlaylistIndex = rootPlaylist.createPlaylist(playlistTitle);
	    newPlaylist = rootPlaylist.getPlaylistAt(newPlaylistIndex);
	} else
	    newPlaylist = null;
	return newPlaylist;
    }
    
    public void actionPerformed(ActionEvent _event) {
	createPlaylist(myContext);
    }
}
