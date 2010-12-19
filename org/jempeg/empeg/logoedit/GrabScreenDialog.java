package org.jempeg.empeg.logoedit;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jempeg.ApplicationContext;
import org.jempeg.empeg.logoedit.action.SaveScreenGrabAction;
import org.jempeg.protocol.ProtocolException;

import com.inzyme.progress.SilentProgressListener;
import com.inzyme.ui.DialogUtils;

/**
 * GrabScreenDialog is a standalone dialog that wraps
 * an EmpegScreen (which grabs a "screenshot" of what is
 * currently on the Empeg's display.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.3 $
 */
public class GrabScreenDialog extends JDialog {
	/**
	 * Constructs a new GrabScreenDialog.
	 * 
	 * @param _context the EmplodeContext to use
	 * @throws IOException if the screen cannot be grabbed
	 */
	public GrabScreenDialog(ApplicationContext _context) throws ProtocolException {
		super(_context.getFrame(), "Screen Grab", true);
		
    EmpegScreen screen = new EmpegScreen(_context.getSynchronizeClient().getProtocolClient(new SilentProgressListener()));
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
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    getContentPane().add(screen, BorderLayout.NORTH);
    getContentPane().add(vbox, BorderLayout.SOUTH);
    pack();
    DialogUtils.centerWindow(this);
	}
}
