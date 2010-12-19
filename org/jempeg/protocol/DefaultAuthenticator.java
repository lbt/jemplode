/* DefaultAuthenticator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.inzyme.text.ResourceBundleUtils;

public class DefaultAuthenticator implements IAuthenticator
{
    private static Window myTopLevelWindow;
    
    public static void setTopLevelWindow(Window _topLevelWindow) {
	myTopLevelWindow = _topLevelWindow;
    }
    
    public PasswordAuthentication requestPassword(String _prompt) {
	JPasswordField passwordField = new JPasswordField();
	JCheckBox savePasswordCheckBox
	    = new JCheckBox(ResourceBundleUtils
				.getUIString("password.savePasswordPrompt"));
	JPanel passwordPanel = new JPanel();
	passwordPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5));
	passwordPanel.setLayout(new GridLayout(0, 1, 3, 3));
	passwordPanel.add(new JLabel(_prompt));
	passwordPanel.add(passwordField);
	passwordPanel.add(savePasswordCheckBox);
	JOptionPane optionPane = new JOptionPane(passwordPanel, -1);
	JDialog dialog
	    = optionPane.createDialog(myTopLevelWindow,
				      ResourceBundleUtils
					  .getUIString("password.frameTitle"));
	dialog.pack();
	dialog.show();
	String password = new String(passwordField.getPassword());
	PasswordAuthentication authentication
	    = new PasswordAuthentication(password,
					 savePasswordCheckBox.isSelected());
	return authentication;
    }
}
