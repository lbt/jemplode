/* IProgressListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.progress;
import java.awt.event.ActionListener;
import java.util.EventListener;

public interface IProgressListener
    extends EventListener, ISimpleProgressListener
{
    public void setWaitState(boolean bool);
    
    public void setStopEnabled(boolean bool);
    
    public void setStopRequested(boolean bool);
    
    public boolean isStopRequested();
    
    public void progressStarted();
    
    public boolean isInProgress();
    
    public void progressCompleted();
    
    public boolean isInteractive();
    
    public void addStopListener(ActionListener actionlistener);
    
    public void operationStarted(String string);
    
    public void operationUpdated(long l);
    
    public void operationUpdated(long l, long l_0_);
    
    public void taskStarted(String string);
    
    public void taskUpdated(long l);
    
    public void taskUpdated(long l, long l_1_);
}
