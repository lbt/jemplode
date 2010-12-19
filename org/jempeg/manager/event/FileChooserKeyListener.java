/* FileChooserKeyListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import com.inzyme.util.Timer;

public class FileChooserKeyListener extends KeyAdapter
{
    private JFileChooser myFileChooser;
    private StringBuffer mySelection;
    private Timer myTimer;
    
    public FileChooserKeyListener(JFileChooser _fileChooser) {
	myFileChooser = _fileChooser;
	addKeyListener(myFileChooser);
	mySelection = new StringBuffer();
	myTimer = new Timer(1000, this, "clear");
    }
    
    protected void addKeyListener(Component _comp) {
	if (_comp instanceof Container) {
	    Component[] components = ((Container) _comp).getComponents();
	    for (int i = 0; i < components.length; i++) {
		if (!(components[i] instanceof JTextField)) {
		    components[i].addKeyListener(this);
		    addKeyListener(components[i]);
		}
	    }
	}
    }
    
    public synchronized void keyTyped(KeyEvent _event) {
	mySelection.append(_event.getKeyChar());
	File dir = myFileChooser.getCurrentDirectory();
	String[] files = dir.list();
	if (files != null) {
	    String selectionStr = mySelection.toString().toLowerCase();
	    int mode = myFileChooser.getFileSelectionMode();
	    boolean selected = false;
	    for (int i = 0; !selected && i < files.length; i++) {
		File file = new File(dir, files[i]);
		if ((mode == 2 || file.isDirectory() && mode == 1
		     || file.isFile() && mode == 0)
		    && files[i].toLowerCase().startsWith(selectionStr)) {
		    myFileChooser.setSelectedFile(file);
		    selected = true;
		}
	    }
	}
	myTimer.mark();
    }
    
    public void clear() {
	mySelection.setLength(0);
    }
}
