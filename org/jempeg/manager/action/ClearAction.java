/* ClearAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.action;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jempeg.manager.util.EmplodeClipboard;

public class ClearAction extends AbstractAction
{
    public void actionPerformed(ActionEvent _event) {
	EmplodeClipboard.getInstance().clear();
    }
}
