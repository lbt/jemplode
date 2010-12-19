/* SoupEditorAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.manager.dialog.SoupEditorDialog;
import org.jempeg.nodestore.soup.ISoupLayer;
import org.jempeg.nodestore.soup.SoupUtils;

public class SoupEditorAction implements ActionListener
{
    private ApplicationContext myContext;
    
    public SoupEditorAction(ApplicationContext _context) {
	myContext = _context;
    }
    
    public void actionPerformed(ActionEvent _event) {
	editSoup();
    }
    
    public void editSoup() {
	editSoup(null);
    }
    
    public void editSoup(ISoupLayer _soupLayer) {
	try {
	    SoupEditorDialog soupEditor
		= new SoupEditorDialog(myContext, myContext.getFrame(),
				       _soupLayer);
	    soupEditor.setVisible(true);
	    if (soupEditor.shouldCreateSoupPlaylist()) {
		ISoupLayer[] soupLayers = soupEditor.getSoupLayers();
		if (soupLayers != null && soupLayers.length > 0) {
		    String name = soupEditor.getPlaylistName();
		    boolean isTransient = soupEditor.isTransient();
		    if (isTransient)
			SoupUtils.createTransientSoupPlaylist
			    (myContext.getPlayerDatabase(), name, soupLayers,
			     true, false, true, null);
		    else
			SoupUtils.createPersistentSoupPlaylist
			    (myContext.getPlayerDatabase(), name, soupLayers,
			     true);
		}
	    }
	    soupEditor.dispose();
	} catch (Throwable t) {
	    Debug.handleError(myContext.getFrame(), t, true);
	}
    }
}
