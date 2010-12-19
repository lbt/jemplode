/* LogoEditToolBar - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.ozten.font.JFontChooser;

public class LogoEditToolBar extends JToolBar
{
    private ToolChangeListenerIfc[] myToolChangeListeners;
    private ButtonGroup myToolGroup;
    private ToolIfc[] myTools
	= { new SelectTool(), new DrawTool(), new PaintTool(), new FillTool(),
	    new TextTool() };
    private JComboBox myScaleComboBox;
    private JComboBox myStrokeWidthComboBox;
    private JComboBox myForegroundComboBox;
    private JComboBox myBackgroundComboBox;
    
    protected class StrokeWidthListener implements ItemListener
    {
	public void itemStateChanged(ItemEvent _event) {
	    if (_event.getStateChange() == 1) {
		Integer strokeWidthInteger = (Integer) _event.getItem();
		int strokeWidth = strokeWidthInteger.intValue();
		for (int i = 0; i < myTools.length; i++) {
		    if (myTools[i] instanceof AbstractPenTool)
			((AbstractPenTool) myTools[i])
			    .setStrokeWidth(strokeWidth);
		}
	    }
	}
    }
    
    protected class ToolSelectionListener implements ActionListener
    {
	private ToolIfc myTool;
	
	public ToolSelectionListener(ToolIfc _tool) {
	    myTool = _tool;
	}
	
	public void actionPerformed(ActionEvent _event) {
	    if (myTool instanceof TextTool) {
		TextTool textTool = (TextTool) myTool;
		Font font = JFontChooser.showDialog(LogoEditToolBar.this,
						    "Font Chooser", "jEmplode",
						    textTool.getFont());
		if (font != null)
		    textTool.setFont(font);
	    }
	    for (int i = myToolChangeListeners.length - 1; i >= 0; i--)
		myToolChangeListeners[i].toolChanged(myTool);
	}
    }
    
    public LogoEditToolBar() {
	myToolChangeListeners = new ToolChangeListenerIfc[0];
	myToolGroup = new ButtonGroup();
	for (int i = 0; i < myTools.length; i++) {
	    JToggleButton toolButton = new JToggleButton(myTools[i].getName());
	    toolButton
		.addActionListener(new ToolSelectionListener(myTools[i]));
	    myToolGroup.add(toolButton);
	    add(toolButton);
	}
	Integer[] scales = new Integer[20];
	for (int i = 0; i < scales.length; i++)
	    scales[i] = new Integer(i + 1);
	myScaleComboBox = new JComboBox(scales);
	Integer[] strokeWidths = new Integer[20];
	for (int i = 0; i < strokeWidths.length; i++)
	    strokeWidths[i] = new Integer(i + 1);
	myStrokeWidthComboBox = new JComboBox(scales);
	myStrokeWidthComboBox.addItemListener(new StrokeWidthListener());
	Color[] colors = { new Color(0, 0, 0), new Color(80, 80, 80),
			   new Color(96, 96, 96), new Color(255, 255, 255) };
	myForegroundComboBox = new JComboBox(colors);
	myForegroundComboBox.setEditable(false);
	myForegroundComboBox.setRenderer(new SolidColorListCellRenderer());
	myBackgroundComboBox = new JComboBox(colors);
	myBackgroundComboBox.setEditable(false);
	myBackgroundComboBox.setRenderer(new SolidColorListCellRenderer());
	addSeparator();
	add(new JLabel("Scale: "));
	add(myScaleComboBox);
	addSeparator();
	add(new JLabel("Stroke Width: "));
	add(myStrokeWidthComboBox);
	addSeparator();
	add(new JLabel("FG: "));
	add(myForegroundComboBox);
	addSeparator();
	add(new JLabel("BG: "));
	add(myBackgroundComboBox);
    }
    
    public void initialize() {
	myScaleComboBox.setSelectedIndex(6);
	myStrokeWidthComboBox.setSelectedIndex(0);
	myForegroundComboBox
	    .setSelectedIndex(myForegroundComboBox.getItemCount() - 1);
	myBackgroundComboBox.setSelectedIndex(0);
    }
    
    public synchronized void addToolChangeListener
	(ToolChangeListenerIfc _listener) {
	ToolChangeListenerIfc[] listeners
	    = new ToolChangeListenerIfc[myToolChangeListeners.length + 1];
	System.arraycopy(myToolChangeListeners, 0, listeners, 0,
			 myToolChangeListeners.length);
	listeners[listeners.length - 1] = _listener;
	myToolChangeListeners = listeners;
    }
    
    public void setScale(int _scale) {
	myScaleComboBox.setSelectedIndex(_scale - 1);
    }
    
    public void addScaleListener(ItemListener _listener) {
	myScaleComboBox.addItemListener(_listener);
    }
    
    public void addStrokeWidthListener(ItemListener _listener) {
	myStrokeWidthComboBox.addItemListener(_listener);
    }
    
    public void addForegroundColorListener(ItemListener _listener) {
	myForegroundComboBox.addItemListener(_listener);
    }
    
    public void addBackgroundColorListener(ItemListener _listener) {
	myBackgroundComboBox.addItemListener(_listener);
    }
}
