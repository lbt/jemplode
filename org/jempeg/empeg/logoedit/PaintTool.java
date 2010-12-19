/* PaintTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Graphics;

public class PaintTool extends AbstractPenTool
{
    public String getName() {
	return "Paint";
    }
    
    protected void paintPoint(LogoEditPanel _editPanel, Graphics _g, int _x,
			      int _y, int _strokeWidth, int _strokeHeight) {
	_g.fillOval(_x, _y, _strokeWidth, _strokeHeight);
    }
}
