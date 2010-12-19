/* Uploader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.io.File;

import javax.comm.CommPortIdentifier;

import com.inzyme.io.FileSeekableInputStream;

public class Uploader
{
    public static void main(String[] _args) throws Throwable {
	FileSeekableInputStream fsis = null;
	SerialConnectionFactory connFactory = null;
	try {
	    if (_args.length < 3) {
		System.err.println
		    ("Usage: java org.jempeg.empeg.protocol.Uploader <sourcefile> <flash address> <com port>");
		System.exit(0);
	    }
	    File uploadFile = new File(_args[0]);
	    Upgrader u = new Upgrader();
	    u.setListener(new BasicUpgradeListener());
	    fsis = new FileSeekableInputStream(uploadFile);
	    CommPortIdentifier commPort
		= CommPortIdentifier.getPortIdentifier(_args[2]);
	    connFactory = new SerialConnectionFactory(commPort, 115200);
	    int address = Integer.parseInt(_args[1], 16);
	    u.upload(connFactory.createConnection(), fsis, address);
	} catch (Object object) {
	    if (fsis != null)
		fsis.close();
	    throw object;
	}
	if (fsis != null)
	    fsis.close();
    }
}
