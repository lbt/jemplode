/* IConfirmationListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.progress;

public interface IConfirmationListener
{
    public static final int YES = 0;
    public static final int NO = 1;
    public static final int CANCEL = 3;
    public static final int YES_CHECKED = 4;
    
    public int confirm(String string, Object object);
    
    public int confirm(String string, String string_0_, Object object);
}
