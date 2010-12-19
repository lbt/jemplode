/* FTPTransferType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.enterprisedt.net.ftp;

public class FTPTransferType
{
    public static FTPTransferType ASCII = new FTPTransferType();
    public static FTPTransferType BINARY = new FTPTransferType();
    static String ASCII_CHAR = "A";
    static String BINARY_CHAR = "I";
    
    private FTPTransferType() {
	/* empty */
    }
}
