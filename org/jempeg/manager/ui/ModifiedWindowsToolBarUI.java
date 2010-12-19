package org.jempeg.manager.ui;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.sun.java.swing.plaf.windows.WindowsToolBarUI;

/**
 * ModifiedWindowsToolBarUI fixes some problems with
 * the implementation of the ToolBarUI for the 
 * Windows Look-And-Feel.
 * 
 * 1.4 _claims_ that hot tracking toolbar items is a builtin
 * feature, but it doesn't appear that it works... At least
 * if it does, I sure as hell can't find out how to enable 
 * it.  They talk about a windows desktop property that
 * enables it.... Oh well.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.1 $
 */
public class ModifiedWindowsToolBarUI extends WindowsToolBarUI {
	public ModifiedWindowsToolBarUI() {
		super();
	}

  protected void installDefaults() {
  	super.installDefaults();
  	setRolloverBorders(true);
  }
  
	protected Border createRolloverBorder() {
		return new ToolBarFlushBorder();
	}

	protected Border createNonRolloverBorder() {
		return new ToolBarFlushBorder();
	}
	
	public class ToolBarFlushBorder extends BevelBorder {
		public ToolBarFlushBorder() {
			super(BevelBorder.LOWERED);
		}
		
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			highlightInner = c.getBackground();
			shadowInner = c.getBackground();
			
			AbstractButton button = (AbstractButton)c;
			ButtonModel buttonModel = button.getModel();
			if (buttonModel.isArmed()) {
				super.paintLoweredBevel(c, g, x, y, width, height);
			} else if (buttonModel.isRollover()) {
				super.paintRaisedBevel(c, g, x, y, width, height);
			}
    }
	}
}
