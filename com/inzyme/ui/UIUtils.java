package com.inzyme.ui;

import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.inzyme.text.ResourceBundleKey;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.ApplicationUtils;
import com.inzyme.util.Debug;

public class UIUtils {
	public static JMenu createMenu(String _name) {
		JMenu menu = new JMenu();
		UIUtils.initializeButton(menu, _name);
		return menu;
	}

	public static JMenuItem createMenuItem(String _name) {
		JMenuItem menuItem = new JMenuItem();
		UIUtils.initializeButton(menuItem, _name);
		return menuItem;
	}

	public static void initializeButton(AbstractButton _button, String _name) {
		String nameWithMnemonic = new ResourceBundleKey(ResourceBundleUtils.UI_KEY, _name + ".text", "").getString();
		NameAndMnemonic nameAndMnemonic = UIUtils.parseNameWithMnemonic(nameWithMnemonic);
		_button.setText(nameAndMnemonic.getName());
		if (nameAndMnemonic.hasMnemonic()) {
			_button.setMnemonic(nameAndMnemonic.getMnemonic());
		} else {
			String mnemonic = new ResourceBundleKey(ResourceBundleUtils.UI_KEY, _name + ".mnemonic", "").getString();
			initializeMnemonic(_name, mnemonic, _button);
		}

		String iconLocation = new ResourceBundleKey(ResourceBundleUtils.UI_KEY, _name + ".icon", "").getString();
		initializeIcon(_name, iconLocation, _button);

		if (_button instanceof JMenuItem) {
			String accelerator = new ResourceBundleKey(ResourceBundleUtils.UI_KEY, _name + ".accelerator", "").getString();
			initializeAccelerator(_name, accelerator, _button);
		}
	}

	public static JMenu createMenu(String _name, Properties _props) {
		JMenu menu = new JMenu();
		UIUtils.initializeButton(menu, _name, _props);
		return menu;
	}

	public static JMenuItem createMenuItem(String _name, Properties _props) {
		JMenuItem menuItem = new JMenuItem();
		UIUtils.initializeButton(menuItem, _name, _props);
		return menuItem;
	}

	public static void initializeButton(AbstractButton _button, String _name, Properties _props) {
		String nameWithMnemonic = _props.getProperty(_name + ".text");
		NameAndMnemonic nameAndMnemonic = UIUtils.parseNameWithMnemonic(nameWithMnemonic);
		_button.setText(nameAndMnemonic.getName());
		if (nameAndMnemonic.hasMnemonic()) {
			_button.setMnemonic(nameAndMnemonic.getMnemonic());
		} else {
			String mnemonic = _props.getProperty(_name + ".mnemonic");
			initializeMnemonic(_name, mnemonic, _button);
		}

		String iconLocation = _props.getProperty(_name + ".icon");
		initializeIcon(_name, iconLocation, _button);

		if (_button instanceof JMenuItem) {
			String accelerator = _props.getProperty(_name + ".accelerator");
			initializeAccelerator(_name, accelerator, _button);
		}
	}

	public static void initializeAccelerator(String _keyName, String _accelerator, AbstractButton _button) {
		try {
			if (_button instanceof JMenuItem) {
				if (_accelerator != null && _accelerator.length() > 0) {
					String modifiedAccelerator;
					if (_accelerator.startsWith("ctrl ")) {
						if (ApplicationUtils.isMac()) {
							modifiedAccelerator = "meta " + _accelerator.substring("ctrl ".length());
						}
						else {
							modifiedAccelerator = _accelerator;
						}
					} else if (_accelerator.equals("DELETE")) {
						if (ApplicationUtils.isMac()) {
							modifiedAccelerator = "BACK_SPACE";
						} else {
							modifiedAccelerator = _accelerator;
						}
					} else if (_accelerator.equals("PROPERTIES")) {
						if (ApplicationUtils.isMac()) {
							modifiedAccelerator = "meta I";
						} else {
							modifiedAccelerator = "alt ENTER";
						}
					} else {
						modifiedAccelerator = _accelerator;
					}
					((JMenuItem) _button).setAccelerator(KeyStroke.getKeyStroke(modifiedAccelerator));
				}
			}
		}
		catch (Throwable t) {
			// no accelerator for you ...
			Debug.println(Debug.WARNING, _keyName + ".accelerator: " + t.getMessage());
		}
	}

	public static void initializeIcon(String _keyName, String _iconLocation, AbstractButton _button) {
		if (_iconLocation != null && _iconLocation.length() > 0) {
			try {
				_button.setIcon(new ImageIcon(UIUtils.class.getResource(_iconLocation)));
			}
			catch (Throwable t) {
				Debug.println(Debug.WARNING, t);
			}
		}
	}

	public static void initializeMnemonic(String _keyName, String _mnemonic, AbstractButton _button) {
		try {
			if (_mnemonic != null && _mnemonic.length() > 0) {
				String modifiedMnemonic;
				// NTS: I have to do this because .getKeyCode() changed to java.awt.AWTKeyStroke in 1.3+, which doesn't 
				// exist in 1.1 and causes terrible problems class loading.
				if (_mnemonic.startsWith("typed ")) {
					modifiedMnemonic = _mnemonic.substring("typed ".length()).toUpperCase();
				}
				else {
					modifiedMnemonic = _mnemonic;
				}
				KeyStroke mnemonicKeyStroke = KeyStroke.getKeyStroke(modifiedMnemonic);
				Integer keyCode = (Integer) KeyStroke.class.getMethod("getKeyCode", null).invoke(mnemonicKeyStroke, null);
				_button.setMnemonic(keyCode.intValue());
			}
		}
		catch (Throwable t) {
			// no mnemonic for you ...
			Debug.println(Debug.WARNING, _keyName + ".mnemonic: " + t.getMessage());
		}
	}

	public static void initializeOSX(String _applicationName) {
		System.getProperties().setProperty("com.apple.mrj.application.apple.menu.about.name", _applicationName);
		System.getProperties().setProperty("apple.laf.useScreenMenuBar", "true");
		// System.getProperties().setProperty("apple.awt.brushMetalLook", "true");
		// System.getProperties().setProperty("apple.awt.brushMetalRounded", "true");
		System.getProperties().setProperty("apple.awt.showGrowBox", "false");
	}

	public static NameAndMnemonic parseNameWithMnemonic(String _name) {
		char mnemonic = 0;
		StringBuffer strWithoutAmpsBuf = new StringBuffer();
		boolean done = false;
		int lastAmpIndex = -1;
		do {
			int ampIndex = _name.indexOf('&', lastAmpIndex + 1);
			if (ampIndex == -1) {
				strWithoutAmpsBuf.append(_name.substring(lastAmpIndex + 1));
				done = true;
			} else {
				mnemonic = _name.charAt(ampIndex + 1);
				strWithoutAmpsBuf.append(_name.substring(lastAmpIndex + 1, ampIndex));
				lastAmpIndex = ampIndex;
			}
		} while (!done);
		String strWithoutAmps = strWithoutAmpsBuf.toString();
		NameAndMnemonic nam = new NameAndMnemonic(strWithoutAmps, mnemonic);
		return nam;
	}
	
	public static class NameAndMnemonic {
		private String myName;
		private char myMnemonic;
		
		public NameAndMnemonic(String _name, char _mnemonic) {
			myName = _name;
			myMnemonic = _mnemonic;
		}
		
		public String getName() {
			return myName;
		}
		
		public char getMnemonic() {
			return myMnemonic;
		}
		
		public boolean hasMnemonic() {
			return myMnemonic != 0;
		}
	}
}
