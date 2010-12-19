/* DrawTool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.Graphics;

public class DrawTool extends AbstractPenTool
{
    public String getName() {
	return "Draw";
    }
    
    protected void paintPoint(LogoEditPanel _editPanel, Graphics _g, int _x,
			      int _y, int _strokeWidth, int _strokeHeight) {
	_g.fillRect(_x, _y, _strokeWidth, _strokeHeight);
    }
}
