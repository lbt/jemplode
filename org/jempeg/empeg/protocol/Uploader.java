
/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

import java.io.File;

import javax.comm.CommPortIdentifier;


import com.inzyme.io.FileSeekableInputStream;

/**
* Uploader is similar to Upgrader, but rather than
* upgrading an Empeg, it simply uploads a chunk of data
* to a particular address of the Empeg.  This is
* basically equivalent to Hugo's download.c and
* Tony's upload.exe.
*
* @author Mike Schrag
*/
public class Uploader {
  public static void main(String[] _args) throws Throwable {
    FileSeekableInputStream fsis = null;
    SerialConnectionFactory connFactory = null;
    try {
      if (_args.length < 3) {
        System.err.println("Usage: java org.jempeg.empeg.protocol.Uploader <sourcefile> <flash address> <com port>");
        System.exit(0);
      }
      File uploadFile = new File(_args[0]);
      Upgrader u = new Upgrader();
      u.setListener(new BasicUpgradeListener());
      fsis = new FileSeekableInputStream(uploadFile);
      CommPortIdentifier commPort = CommPortIdentifier.getPortIdentifier(_args[2]);
      connFactory = new SerialConnectionFactory(commPort, 115200);
      int address = Integer.parseInt(_args[1], 16);
      u.upload(connFactory.createConnection(), fsis, address);
    }
    finally {
      if (fsis != null) {
        fsis.close();
      }
    }
  }
}

