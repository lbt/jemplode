/* AbstractChangeablePanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import com.inzyme.event.ActionSource;

import org.jempeg.empeg.manager.event.DocumentActionAdapter;
import org.jempeg.nodestore.IDeviceSettings;

public abstract class AbstractChangeablePanel extends JPanel
    implements ActionListener, ChangeListener
{
    private ActionSource myActionSource = new ActionSource(this);
    private DocumentActionAdapter myDocumentActionAdapter
	= new DocumentActionAdapter(myActionSource);
    
    public void addActionListener(ActionListener _listener) {
	myActionSource.addActionListener(_listener);
    }
    
    public void removeActionListener(ActionListener _listener) {
	myActionSource.removeActionListener(_listener);
    }
    
    public void listenTo(JTextComponent _textComponent) {
	myDocumentActionAdapter.listenTo(_textComponent);
    }
    
    public void unlistenTo(JTextComponent _textComponent) {
	myDocumentActionAdapter.unlistenTo(_textComponent);
    }
    
    public void listenTo(AbstractButton _button) {
	_button.addActionListener(this);
    }
    
    public void unlistenTo(AbstractButton _button) {
	_button.removeActionListener(this);
    }
    
    public void listenTo(JComboBox _comboBox) {
	_comboBox.addActionListener(this);
    }
    
    public void unlistenTo(JComboBox _comboBox) {
	_comboBox.removeActionListener(this);
    }
    
    public void listenTo(JSlider _slider) {
	_slider.addChangeListener(this);
    }
    
    public void unlistenTo(JSlider _slider) {
	_slider.removeChangeListener(this);
    }
    
    public void read(IDeviceSettings _configFile) {
	myDocumentActionAdapter.setEnabled(false);
	read0(_configFile);
	myDocumentActionAdapter.setEnabled(true);
    }
    
    public void write(IDeviceSettings _configFile) {
	write0(_configFile);
    }
    
    protected abstract void read0(IDeviceSettings idevicesettings);
    
    protected abstract void write0(IDeviceSettings idevicesettings);
    
    public void actionPerformed(ActionEvent _event) {
	myActionSource.fireActionPerformed();
    }
    
    public void stateChanged(ChangeEvent _event) {
	myActionSource.fireActionPerformed();
    }
}
