/* ShowHideSyncDetailsAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite.action;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;

import org.jempeg.manager.dialog.SynchronizeQueueDialog;

public class ShowHideSyncDetailsAction extends AbstractAction
    implements MouseListener
{
    private SynchronizeQueueDialog mySynchronizeQueueDialog;
    
    public ShowHideSyncDetailsAction
	(SynchronizeQueueDialog _synchronizeQueueDialog) {
	mySynchronizeQueueDialog = _synchronizeQueueDialog;
    }
    
    public void showHideDetails() {
	mySynchronizeQueueDialog
	    .setVisible(!mySynchronizeQueueDialog.isVisible());
    }
    
    public void actionPerformed(ActionEvent _event) {
	showHideDetails();
    }
    
    public void mouseClicked(MouseEvent _event) {
	/* empty */
    }
    
    public void mouseEntered(MouseEvent _event) {
	/* empty */
    }
    
    public void mouseExited(MouseEvent _event) {
	/* empty */
    }
    
    public void mousePressed(MouseEvent _event) {
	showHideDetails();
    }
    
    public void mouseReleased(MouseEvent _event) {
	/* empty */
    }
}
