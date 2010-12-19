/* HijackDownloadAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.manager.action;
import org.jempeg.ApplicationContext;
import org.jempeg.empeg.util.HijackUtils;
import org.jempeg.manager.action.DownloadAction;
import org.jempeg.protocol.IConnection;

public class HijackDownloadAction extends DownloadAction
{
    public HijackDownloadAction(ApplicationContext _context) {
	super(_context);
    }
    
    protected boolean shouldUseHijack(IConnection _conn) {
	boolean useHijack = HijackUtils.shouldUseHijack(_conn) == null;
	return useHijack;
    }
}
