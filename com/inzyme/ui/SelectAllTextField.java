/* SelectAllTextField - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.ui;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.text.Document;

import com.inzyme.properties.PropertiesManager;

public class SelectAllTextField extends JTextField
{
    public SelectAllTextField() {
	/* empty */
    }
    
    public SelectAllTextField(int columns) {
	super(columns);
    }
    
    public SelectAllTextField(String text) {
	super(text);
    }
    
    public SelectAllTextField(String text, int columns) {
	super(text, columns);
    }
    
    public SelectAllTextField(Document doc, String text, int columns) {
	super(doc, text, columns);
    }
    
    protected void processFocusEvent(FocusEvent _event) {
	super.processFocusEvent(_event);
	if (PropertiesManager.getInstance()
		.getBooleanProperty("tonySelections")
	    && _event.getID() == 1004)
	    selectAll();
    }
}
