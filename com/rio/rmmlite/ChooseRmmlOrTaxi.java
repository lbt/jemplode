/* ChooseRmmlOrTaxi - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.DialogUtils;
import com.rio.PearlUtils;

public class ChooseRmmlOrTaxi
{
    private abstract static class AbstractLaunchListener
	implements ActionListener, Runnable
    {
	private JFrame myFrame;
	protected String[] myArgs;
	
	public AbstractLaunchListener(JFrame _frame, String[] _args) {
	    myFrame = _frame;
	    myArgs = _args;
	}
	
	public void actionPerformed(ActionEvent _event) {
	    myFrame.dispose();
	    Thread t = new Thread(this);
	    t.start();
	}
    }
    
    private static class LaunchRMMLListener extends AbstractLaunchListener
    {
	public LaunchRMMLListener(JFrame _frame, String[] _args) {
	    super(_frame, _args);
	}
	
	public void run() {
	    RmmlMain.main(myArgs);
	}
    }
    
    private static class LaunchTaxiListener extends AbstractLaunchListener
    {
	public LaunchTaxiListener(JFrame _frame, String[] _args) {
	    super(_frame, _args);
	}
	
	public void run() {
	    TaxiMain.main(myArgs);
	}
    }
    
    public static void main(String[] _args) {
	PearlUtils.initializeApplication();
	final JFrame chooseRmmlOrTaxiFrame
	    = new JFrame(ResourceBundleUtils.getUIString("select"));
	JPanel buttonPanel = new JPanel();
	buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	buttonPanel.setLayout(new FlowLayout());
	JButton rmmlButton = new JButton("Rio Music Manager Lite");
	rmmlButton.addActionListener
	    (new LaunchRMMLListener(chooseRmmlOrTaxiFrame, _args));
	JButton taxiButton = new JButton("Rio Taxi Lite");
	taxiButton.addActionListener
	    (new LaunchTaxiListener(chooseRmmlOrTaxiFrame, _args));
	JButton cancelButton
	    = new JButton(ResourceBundleUtils.getUIString("cancel"));
	cancelButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent _event) {
		chooseRmmlOrTaxiFrame.dispose();
		System.exit(0);
	    }
	});
	buttonPanel.add(rmmlButton);
	buttonPanel.add(taxiButton);
	buttonPanel.add(cancelButton);
	chooseRmmlOrTaxiFrame.getContentPane().add(buttonPanel);
	chooseRmmlOrTaxiFrame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent _event) {
		chooseRmmlOrTaxiFrame.dispose();
		System.exit(0);
	    }
	});
	chooseRmmlOrTaxiFrame.pack();
	DialogUtils.centerWindow(chooseRmmlOrTaxiFrame);
	chooseRmmlOrTaxiFrame.show();
	rmmlButton.requestFocus();
    }
}
