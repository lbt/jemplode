/* GrabScreenDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import com.inzyme.progress.SilentProgressListener;
import com.inzyme.ui.DialogUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.logoedit.action.SaveScreenGrabAction;
import org.jempeg.protocol.ProtocolException;

public class GrabScreenDialog extends JDialog
{
    public GrabScreenDialog(ApplicationContext _context)
	throws ProtocolException {
	super(_context.getFrame(), "Screen Grab", true);
	EmpegScreen screen
	    = new EmpegScreen(_context.getSynchronizeClient().getProtocolClient
			      (new SilentProgressListener()));
	JButton grabButton = new JButton("Refresh");
	JButton saveButton = new JButton("Save");
	JCheckBox repeatButton = new JCheckBox("Start/Stop");
	grabButton.addActionListener(screen.createGrabAction());
	repeatButton.addActionListener(screen.createRepeatingGrabAction());
	saveButton.addActionListener(new SaveScreenGrabAction(screen));
	Box vbox = Box.createVerticalBox();
	Box hbox = Box.createHorizontalBox();
	hbox.add(Box.createHorizontalGlue());
	hbox.add(grabButton);
	hbox.add(Box.createHorizontalStrut(5));
	hbox.add(saveButton);
	hbox.add(Box.createHorizontalStrut(5));
	hbox.add(repeatButton);
	hbox.add(Box.createHorizontalGlue());
	vbox.add(Box.createVerticalStrut(5));
	vbox.add(hbox);
	vbox.add(Box.createVerticalStrut(5));
	addWindowListener(screen.createStopGrabbingWindowListener());
	setDefaultCloseOperation(2);
	getContentPane().add(screen, "North");
	getContentPane().add(vbox, "South");
	pack();
	DialogUtils.centerWindow(this);
    }
}
