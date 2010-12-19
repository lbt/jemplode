/* ConfigAdvancedPanel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.dialog;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jempeg.nodestore.IDeviceSettings;

public class ConfigAdvancedPanel extends AbstractChangeablePanel
{
    private JCheckBox myHardDiskIconTB;
    private JCheckBox myDisplayVisualTB;
    private JCheckBox myDisableKenwoodTB;
    private JCheckBox myDisableVolumeRampTB;
    private JCheckBox myCapAt0dBTB;
    private JSlider myHushSlider;
    private JLabel myHushAmount;
    
    public ConfigAdvancedPanel() {
	setName("Advanced");
	JPanel formPanel = new JPanel();
	GridLayout gl = new GridLayout(0, 1, 0, 5);
	formPanel.setLayout(gl);
	myHardDiskIconTB = new JCheckBox("Display Hard Disk Icon");
	myDisplayVisualTB
	    = new JCheckBox("Display Visual Names When Switching");
	myDisableKenwoodTB = new JCheckBox("Disable Kenwood remote control");
	myDisableVolumeRampTB
	    = new JCheckBox("Disable volume ramp at power-on");
	myCapAt0dBTB = new JCheckBox("Cap volume at 0dB");
	myHushSlider = new JSlider(0, 90, 30);
	myHushAmount = new JLabel();
	myHushSlider.setLabelTable(myHushSlider.createStandardLabels(10));
	myHushSlider.setPaintLabels(true);
	myHushSlider.setMajorTickSpacing(10);
	myHushSlider.setMinorTickSpacing(5);
	myHushSlider.setPaintTicks(true);
	myHushSlider.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent event) {
		myHushAmount.setText(ConfigAdvancedPanel.this.formatHushString
				     (myHushSlider.getValue()));
	    }
	});
	formPanel.add(myHardDiskIconTB);
	formPanel.add(myDisplayVisualTB);
	formPanel.add(myDisableKenwoodTB);
	formPanel.add(myDisableVolumeRampTB);
	formPanel.add(myCapAt0dBTB);
	formPanel.add(myHushAmount);
	formPanel.add(myHushSlider);
	setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	add(formPanel);
    }
    
    protected void read0(IDeviceSettings _configFile) {
	unlistenTo(myHardDiskIconTB);
	myHardDiskIconTB.setSelected(_configFile.getBooleanValue("display",
								 "caching",
								 true));
	listenTo(myHardDiskIconTB);
	unlistenTo(myDisplayVisualTB);
	myDisplayVisualTB.setSelected
	    (_configFile.getBooleanValue("display", "visual_names", true));
	listenTo(myDisplayVisualTB);
	unlistenTo(myDisableKenwoodTB);
	myDisableKenwoodTB.setSelected(_configFile.getBooleanValue("kenwood",
								   "disabled",
								   false));
	listenTo(myDisableKenwoodTB);
	unlistenTo(myDisableVolumeRampTB);
	myDisableVolumeRampTB.setSelected
	    (_configFile.getBooleanValue("ramp", "disabled", false));
	listenTo(myDisableVolumeRampTB);
	unlistenTo(myCapAt0dBTB);
	myCapAt0dBTB.setSelected(_configFile.getBooleanValue("volumecap",
							     "enabled",
							     false));
	listenTo(myCapAt0dBTB);
	unlistenTo(myHushSlider);
	String hushValue
	    = _configFile.getStringValue("hush", "hushpercent", "30");
	myHushAmount.setText(formatHushString(hushValue));
	myHushSlider.setValue(Integer.parseInt(hushValue));
	listenTo(myHushSlider);
    }
    
    protected void write0(IDeviceSettings _configFile) {
	_configFile.setBooleanValue("display", "visual_names",
				    myDisplayVisualTB.isSelected());
	_configFile.setBooleanValue("display", "caching",
				    myHardDiskIconTB.isSelected());
	_configFile.setBooleanValue("kenwood", "disabled",
				    myDisableKenwoodTB.isSelected());
	_configFile.setBooleanValue("ramp", "disabled",
				    myDisableVolumeRampTB.isSelected());
	_configFile.setBooleanValue("volumecap", "enabled",
				    myCapAt0dBTB.isSelected());
	String newHush = String.valueOf(myHushSlider.getValue());
	_configFile.setStringValue("hush", "hushpercent", newHush);
    }
    
    private String formatHushString(int hushValue) {
	String hushString
	    = "On hush, reduce volume by: " + String.valueOf(hushValue) + "%";
	return hushString;
    }
    
    private String formatHushString(String hushValue) {
	String hushString = formatHushString(Integer.parseInt(hushValue));
	return hushString;
    }
}
