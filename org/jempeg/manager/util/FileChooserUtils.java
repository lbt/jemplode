package org.jempeg.manager.util;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.freehep.swing.JDirectoryChooser;
import org.jempeg.JEmplodeProperties;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

/**
 * Handy-dandy utilities for manipulating a JFileChooser.
 * 
 * @author Mike Schrag
 */
public class FileChooserUtils {
	/**
	* Saves the most recent open directory.
	*
	* @param _jfc the JFileChooser to save from
	*/
	public static void saveLastDirectory(JFileChooser _jfc) {
		File currentDirectory = _jfc.getCurrentDirectory();
		FileChooserUtils.saveLastDirectory(currentDirectory);
	}

	/**
	* Saves the most recent open directory.
	*
	* @param _jfc the JFileChooser to save from
	*/
	public static void saveLastDirectory(JDirectoryChooser _jdc) {
		File currentDirectory = _jdc.getSelectedFile();
		FileChooserUtils.saveLastDirectory(currentDirectory);
	}

	/**
	* Sets the current directory of the given File Chooser to 
	* the most recent directory that was open.
	*
	* @param _jfc the last open directory
	*/
	public static void setToLastDirectory(JFileChooser _jfc) {
		File lastDirectory = FileChooserUtils.getLastDirectory();
		_jfc.setCurrentDirectory(lastDirectory);
	}

	/**
	* Sets the current directory of the given Directory Chooser to 
	* the most recent directory that was open.
	*
	* @param _jfc the last open directory
	*/
	public static void setToLastDirectory(JDirectoryChooser _jdc) {
		File lastDirectory = FileChooserUtils.getLastDirectory();
		_jdc.setCurrentDirectory(lastDirectory);
	}

	/**
	* Returns the last directory that the user had open in a File Dialog.
	*
	* @returns the last directory that the user had open in a File Dialog
	*/
	public static File getLastDirectory() {
		String lastDirectoryStr = PropertiesManager.getInstance().getProperty(JEmplodeProperties.LAST_OPEN_DIRECTORY_KEY);
		File lastDirectory = new File(lastDirectoryStr);
		if (!lastDirectory.exists()) {
			lastDirectory = new File(PropertiesManager.getDefaults().getProperty(JEmplodeProperties.LAST_OPEN_DIRECTORY_KEY));
		}
		return lastDirectory;
	}
	
	/**
	* Saves the most recent open directory.
	*
	* @param _jfc the JFileChooser to save from
	*/
	public static void saveLastDirectory(File _lastDirectory) {
		PropertiesManager.getInstance().setProperty(JEmplodeProperties.LAST_OPEN_DIRECTORY_KEY, _lastDirectory.getAbsolutePath());

		try {
			PropertiesManager.getInstance().save();
		}
		catch (IOException e) {
			Debug.println(e);
		}
	}

	private static JList findList(Container _container) {
		JList list = null;
		int count = _container.getComponentCount();
		for (int i = 0; list == null && i < count; i++) {
			Component comp = _container.getComponent(count);
			if (comp instanceof JList) {
				list = (JList) comp;
			}
			else if (comp instanceof Container) {
				list = findList((Container) comp);
			}
		}
		return list;
	}

	/**
	* JFileChooser.getSelectedFiles is screwed up, so this is
	* a workaround for the Metal LnF.
	*
	* @param _chooser the file chooser
	* @returns the selected set of files
	*/
	public static File[] getSelectedFiles(JFileChooser _chooser) {
		File[] selectedFiles = _chooser.getSelectedFiles();
		if (selectedFiles == null || selectedFiles.length == 0) {
			selectedFiles = null;

			try {
				if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel) {
					_chooser.getComponentCount();
					JList list = findList(_chooser);
					if (list != null) {
						Object[] selectedValues = list.getSelectedValues();
						if (selectedValues.length > 0 && (selectedValues[0] instanceof File)) {
							selectedFiles = new File[selectedValues.length];
							for (int i = 0; i < selectedValues.length; i++) {
								selectedFiles[i] = (File) selectedValues[i];
							}
						}
					}
				}
			}
			finally {
				if (selectedFiles == null) {
					File selectedFile = _chooser.getSelectedFile();
					if (selectedFile == null) {
						selectedFiles = new File[0];
					}
					else {
						selectedFiles = new File[] { selectedFile };
					}
				}
			}
		}
		return selectedFiles;
	}
}
