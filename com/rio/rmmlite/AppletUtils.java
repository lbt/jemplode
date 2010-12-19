/* AppletUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import com.ms.security.PermissionID;
import com.ms.security.PolicyEngine;

import netscape.security.PrivilegeManager;

public class AppletUtils
{
    public static void requestFullPermissions() {
	try {
	    PolicyEngine.assertPermission(PermissionID.CLIENTSTORE);
	    PolicyEngine.assertPermission(PermissionID.EXEC);
	    PolicyEngine.assertPermission(PermissionID.FILEIO);
	    PolicyEngine.assertPermission(PermissionID.MULTIMEDIA);
	    PolicyEngine.assertPermission(PermissionID.NETIO);
	    PolicyEngine.assertPermission(PermissionID.PRINTING);
	    PolicyEngine.assertPermission(PermissionID.PROPERTY);
	    PolicyEngine.assertPermission(PermissionID.REFLECTION);
	    PolicyEngine.assertPermission(PermissionID.REGISTRY);
	    PolicyEngine.assertPermission(PermissionID.SECURITY);
	    PolicyEngine.assertPermission(PermissionID.SYSSTREAMS);
	    PolicyEngine.assertPermission(PermissionID.SYSTEM);
	    PolicyEngine.assertPermission(PermissionID.THREAD);
	    PolicyEngine.assertPermission(PermissionID.UI);
	    PolicyEngine.assertPermission(PermissionID.USERFILEIO);
	    System.out.println("Running in a Microsoft VM");
	} catch (Throwable t) {
	    System.out.println("Not running in a Microsoft VM");
	}
	try {
	    String[] permissions = { "UniversalFileAccess" };
	    for (int i = 0; i < permissions.length; i++)
		PrivilegeManager.enablePrivilege(permissions[i]);
	    System.out.println("Running in a Netscape VM");
	} catch (Throwable t) {
	    t.printStackTrace();
	    System.out.println("Not running in a Netscape VM");
	}
    }
}
