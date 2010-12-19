/* IContextListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.event;
import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;

import org.jempeg.nodestore.PlayerDatabase;
import org.jempeg.protocol.ISynchronizeClient;

public interface IContextListener
{
    public void databaseChanged(PlayerDatabase playerdatabase,
				PlayerDatabase playerdatabase_0_);
    
    public void synchronizeClientChanged
	(ISynchronizeClient isynchronizeclient,
	 ISynchronizeClient isynchronizeclient_1_);
    
    public void selectedContainerChanged(IContainer icontainer,
					 IContainer icontainer_2_);
    
    public void selectionChanged(Object object,
				 ContainerSelection containerselection,
				 ContainerSelection containerselection_3_);
}
