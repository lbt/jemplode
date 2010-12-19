/* ModifiedWindowsToolBarUI - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.ui;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.sun.java.swing.plaf.windows.WindowsToolBarUI;

public class ModifiedWindowsToolBarUI extends WindowsToolBarUI
{
    public class ToolBarFlushBorder extends BevelBorder
    {
	public ToolBarFlushBorder() {
	    super(1);
	}
	
	public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
	    highlightInner = c.getBackground();
	    shadowInner = c.getBackground();
	    AbstractButton button = (AbstractButton) c;
	    ButtonModel buttonModel = button.getModel();
	    if (buttonModel.isArmed())
		super.paintLoweredBevel(c, g, x, y, width, height);
	    else if (buttonModel.isRollover())
		super.paintRaisedBevel(c, g, x, y, width, height);
	}
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
}
