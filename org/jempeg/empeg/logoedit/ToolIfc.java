/* ToolIfc - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public interface ToolIfc
{
    public String getName();
    
    public void start(LogoEditPanel logoeditpanel, Graphics graphics);
    
    public void stop(LogoEditPanel logoeditpanel, Graphics graphics);
    
    public void click(LogoEditPanel logoeditpanel, Graphics graphics, int i,
		      int i_0_, int i_1_);
    
    public void drag(LogoEditPanel logoeditpanel, Graphics graphics, int i,
		     int i_2_, int i_3_);
    
    public void release(LogoEditPanel logoeditpanel, Graphics graphics, int i,
			int i_4_, int i_5_);
    
    public void type(LogoEditPanel logoeditpanel, Graphics graphics,
		     KeyEvent keyevent);
    
    public void paintOverlay(LogoEditPanel logoeditpanel, Graphics graphics);
}
