/* TaxiMain - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import java.net.URL;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.IManagerUI;

public class TaxiMain extends AbstractMain
{
    protected URL getSplashImage() {
	URL splashImageURL
	    = this.getClass().getResource("/com/rio/rmmlite/icons/splash.jpg");
	return splashImageURL;
    }
    
    protected void initializeTagExtractors() {
	PropertiesManager.getInstance()
	    .setBooleanProperty("jempeg.allowUnknownTypes", true);
    }
    
    protected void initializeTagWriters() {
	/* empty */
    }
    
    protected IManagerUI createUI(ApplicationContext _context,
				  boolean _applet) {
	TaxiManagerUI taxiUI = new TaxiManagerUI(_context, _applet);
	return taxiUI;
    }
    
    public static void main(String[] _args) {
	String hostAddress;
	if (_args.length > 0)
	    hostAddress = _args[0];
	else
	    hostAddress = null;
	try {
	    TaxiMain main = new TaxiMain();
	    main.init(hostAddress, false);
	} catch (Throwable e) {
	    Debug.println(e);
	}
    }
}
