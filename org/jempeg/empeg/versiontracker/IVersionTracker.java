/* IVersionTracker - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.versiontracker;
import java.io.IOException;

import com.inzyme.model.Reason;

public interface IVersionTracker
{
    public String getProductName();
    
    public boolean shouldAutoUpdate();
    
    public void setAutoUpdate(boolean bool);
    
    public boolean shouldPromptBeforeUpdate();
    
    public void setPromptsBeforeUpdate(boolean bool);
    
    public boolean isInstalled() throws IOException;
    
    public Reason getFailureReason(String string);
    
    public String getInstalledVersion() throws IOException;
    
    public String getLatestVersion() throws IOException;
    
    public boolean isNewerVersionAvailable() throws IOException;
    
    public String getReleaseNotes() throws IOException;
    
    public VersionChange[] getChanges() throws IOException;
    
    public void installLatestVersion() throws IOException;
    
    public boolean isRestartRequired();
}
