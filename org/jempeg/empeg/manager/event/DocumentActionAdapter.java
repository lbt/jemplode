/* DocumentActionAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.event;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import com.inzyme.event.ActionSource;

public class DocumentActionAdapter implements DocumentListener
{
    private ActionSource mySource;
    private boolean myEnabled;
    
    public DocumentActionAdapter(ActionSource _source) {
	mySource = _source;
	myEnabled = true;
    }
    
    public void listenTo(JTextComponent _textComponent) {
	listenTo(_textComponent.getDocument());
    }
    
    public void unlistenTo(JTextComponent _textComponent) {
	unlistenTo(_textComponent.getDocument());
    }
    
    public void listenTo(Document _document) {
	_document.addDocumentListener(this);
    }
    
    public void unlistenTo(Document _document) {
	_document.removeDocumentListener(this);
    }
    
    public void setEnabled(boolean _enabled) {
	myEnabled = _enabled;
    }
    
    public void changedUpdate(DocumentEvent _event) {
	if (myEnabled)
	    mySource.fireActionPerformed();
    }
    
    public void insertUpdate(DocumentEvent _event) {
	if (myEnabled)
	    mySource.fireActionPerformed();
    }
    
    public void removeUpdate(DocumentEvent _event) {
	if (myEnabled)
	    mySource.fireActionPerformed();
    }
}
