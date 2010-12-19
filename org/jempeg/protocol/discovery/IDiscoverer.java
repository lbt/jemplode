/* IDiscoverer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol.discovery;

public interface IDiscoverer
{
    public void addDiscoveryListener(IDiscoveryListener idiscoverylistener);
    
    public void removeDiscoveryListener(IDiscoveryListener idiscoverylistener);
    
    public void startDiscovery();
    
    public void stopDiscovery();
    
    public boolean isRunning();
}
