/**
 *  Copyright (C) 2000 Enterprise Distributed Technologies Ltd.
 *
 *
 *  Change Log:
 *
 *        $Log: FTPTransferType.java,v $
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
 *        Revision 1.3  2001/10/09 20:54:08  bruceb
 *        No change
 *
 *        Revision 1.1  2001/10/05 14:42:04  bruceb
 *        moved from old project
 *
 *
 */

package com.enterprisedt.net.ftp;

/**
 *  Enumerates the transfer types possible. We
 *  support only the two common types, ASCII and
 *  Image (often called binary).
 *
 *  @author             Bruce Blackshaw
 *  @version        $Revision: 1.2 $
 *
 */
 public class FTPTransferType {

     /**
      *   Represents ASCII transfer type
      */
     public static FTPTransferType ASCII = new FTPTransferType();

     /**
      *   Represents Image (or binary) transfer type
      */
     public static FTPTransferType BINARY = new FTPTransferType();

     /**
      *   The char sent to the server to set ASCII
      */
     static String ASCII_CHAR = "A";

     /**
      *   The char sent to the server to set BINARY
      */
     static String BINARY_CHAR = "I";

     /**
      *  Private so no-one else can instantiate this class
      */
     private FTPTransferType() {
     }
 }
