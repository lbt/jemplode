/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
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

/**
* The "advanced" panel for the configure player dialog.
*
* @author Mike Schrag
* @author Daniel M. Zimmerman
* @author Ron Forrester
* @version $Revision: 1.5 $
**/
public class ConfigAdvancedPanel extends AbstractChangeablePanel {
	private JCheckBox myHardDiskIconTB;
	private JCheckBox myDisplayVisualTB;
  private JCheckBox myDisableKenwoodTB;
  private JCheckBox myDisableVolumeRampTB;
  private JCheckBox myCapAt0dBTB;
  
  private JSlider   myHushSlider;
  private JLabel    myHushAmount;

	public ConfigAdvancedPanel() {
		super();

		setName("Advanced");
		
		JPanel formPanel = new JPanel();
			GridLayout gl = new GridLayout(0, 1, 0, 5);
			formPanel.setLayout(gl);
			
      myHardDiskIconTB = new JCheckBox("Display Hard Disk Icon");
      myDisplayVisualTB = new JCheckBox("Display Visual Names When Switching");
      myDisableKenwoodTB = new JCheckBox("Disable Kenwood remote control");
      myDisableVolumeRampTB = new JCheckBox("Disable volume ramp at power-on");
      myCapAt0dBTB = new JCheckBox("Cap volume at 0dB");
      
      // Create the slider and the label that will display the current
      // hush setting.
      //
      myHushSlider = new JSlider(0, 90, 30);
      myHushAmount = new JLabel();
      
      // Set the initial parameters for the slider
      //
      myHushSlider.setLabelTable(myHushSlider.createStandardLabels(10));
      myHushSlider.setPaintLabels(true);
      myHushSlider.setMajorTickSpacing(10);
      myHushSlider.setMinorTickSpacing(5);
      myHushSlider.setPaintTicks(true);
      
      // Note: can't just make the listener 'this', cuz it will screw up
      // the listenTo() stuff... Why? Because we are derived from the
      // AbstractChangeablePanel -- if we declare a stateChanged()
      // method as part of this panel, it will override the base
      // implementation, and things will be unhappy. So we just create
      // an inner class here to deal with updating the %-age field.
      //
      myHushSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent event) {
          // As the hush slider is adjusted, update the jlabel with the current value:
          //
            myHushAmount.setText(formatHushString(myHushSlider.getValue()));
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
		myHardDiskIconTB.setSelected(_configFile.getBooleanValue("display", "caching", true));
		listenTo(myHardDiskIconTB);

		unlistenTo(myDisplayVisualTB);
		myDisplayVisualTB.setSelected(_configFile.getBooleanValue("display", "visual_names", true));
		listenTo(myDisplayVisualTB);
    
    unlistenTo(myDisableKenwoodTB);
    myDisableKenwoodTB.setSelected(_configFile.getBooleanValue("kenwood", "disabled", false));
    listenTo(myDisableKenwoodTB);
    
    unlistenTo(myDisableVolumeRampTB);
    myDisableVolumeRampTB.setSelected(_configFile.getBooleanValue("ramp", "disabled", false));
    listenTo(myDisableVolumeRampTB);
    
    unlistenTo(myCapAt0dBTB);
    myCapAt0dBTB.setSelected(_configFile.getBooleanValue("volumecap", "enabled", false));
    listenTo(myCapAt0dBTB);
    
    // Read in the current hush value and prime the UI with it.
    //
    unlistenTo(myHushSlider);
    String hushValue = _configFile.getStringValue("hush", "hushpercent", "30");
    myHushAmount.setText( formatHushString(hushValue) );
    myHushSlider.setValue(Integer.parseInt(hushValue));
    listenTo(myHushSlider);
	}

	protected void write0(IDeviceSettings _configFile) {
		_configFile.setBooleanValue("display", "visual_names", myDisplayVisualTB.isSelected());
		_configFile.setBooleanValue("display", "caching", myHardDiskIconTB.isSelected());
    _configFile.setBooleanValue("kenwood",  "disabled",     myDisableKenwoodTB.isSelected());
    _configFile.setBooleanValue("ramp",     "disabled",     myDisableVolumeRampTB.isSelected());
    _configFile.setBooleanValue("volumecap","enabled",      myCapAt0dBTB.isSelected());
    
    // Get the hush % from the slider, and write it out...
    //
    String newHush = String.valueOf(myHushSlider.getValue());
    _configFile.setStringValue("hush", "hushpercent", newHush);
	}

  private String formatHushString(int hushValue) {
    String hushString = "On hush, reduce volume by: " + String.valueOf(hushValue) + "%";
    return hushString;
  }
  
  private String formatHushString(String hushValue) {
    String hushString = formatHushString(Integer.parseInt(hushValue));
    return hushString;
  }
}
