/**
 *  Copyright (C) 2000 Enterprise Distributed Technologies Ltd.
 *
 *
 *  Change Log:
 *
 *        $Log: FTPConnectMode.java,v $
 *        Revision 1.2  2003/03/22 01:25:15  mschrag
 *        one thousand little cleanups
 *
 *        Revision 1.1  2003/02/01 10:04:54  mschrag
 *        jempeg-empeg
 *
 *        Revision 1.2  2002/07/04 09:43:16  mschrag
 *        misc
 *
 *        Revision 1.1  2002/06/30 05:50:29  mschrag
 *        jempeg
 *
 *        Revision 1.1  2002/06/29 03:06:05  mschrag
 *        36
 *
 *        Revision 1.1  2002/03/09 19:44:14  mschrag
 *        new FTP client
 *
 *        Revision 1.1  2001/10/09 20:53:46  bruceb
 *        Active mode changes
 *
 *        Revision 1.1  2001/10/05 14:42:04  bruceb
 *        moved from old project
 *
 *
 */

package com.enterprisedt.net.ftp;

/**
 *  Enumerates the connect modes that are possible,
 *  active & PASV
 *
 *  @author     Bruce Blackshaw
 *  @version    $Revision: 1.2 $
 *
 */
 public class FTPConnectMode {
     /**
      *   Represents active connect mode
      */
     public static FTPConnectMode ACTIVE = new FTPConnectMode();

     /**
      *   Represents PASV connect mode
      */
     public static FTPConnectMode PASV = new FTPConnectMode();

     /**
      *  Private so no-one else can instantiate this class
      */
     private FTPConnectMode() {
     }
 }
