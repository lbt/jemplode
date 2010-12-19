/* RmmlMain - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.net.URL;

import com.inzyme.util.Debug;
import com.rio.PearlUtils;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.IManagerUI;

public class RmmlMain extends AbstractMain
{
    protected URL getSplashImage() {
	URL splashImageURL
	    = this.getClass().getResource("/com/rio/rmmlite/icons/splash.jpg");
	return splashImageURL;
    }
    
    protected void initializeTagExtractors() {
	PearlUtils.initializeTagExtractors(false);
    }
    
    protected void initializeTagWriters() {
	/* empty */
    }
    
    protected IManagerUI createUI(ApplicationContext _context,
				  boolean _applet) {
	RioMusicManagerLiteUI rmmlUI
	    = new RioMusicManagerLiteUI(_context, _applet);
	return rmmlUI;
    }
    
    public static void main(String[] _args) {
	String hostAddress;
	if (_args.length > 0)
	    hostAddress = _args[0];
	else
	    hostAddress = null;
	try {
	    RmmlMain main = new RmmlMain();
	    main.init(hostAddress, false);
	} catch (Throwable e) {
	    Debug.println(e);
	}
    }
}
