/* UpgradeListenerIfc - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;

public interface UpgradeListenerIfc
{
    public void showStatus(int i, int i_0_, int i_1_, int i_2_);
    
    public void showError(int i, int i_3_);
    
    public void textLoaded(String string, String string_4_, String string_5_,
			   String string_6_);
}
