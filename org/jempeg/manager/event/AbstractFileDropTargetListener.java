/* AbstractFileDropTargetListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.manager.event;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.util.Debug;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.ui.ContainerModifierUI;
import org.jempeg.nodestore.model.ContainerModifierFactory;

public abstract class AbstractFileDropTargetListener
    implements DropTargetListener
{
    private ApplicationContext myContext;
    
    public AbstractFileDropTargetListener(ApplicationContext _context) {
	myContext = _context;
    }
    
    protected ApplicationContext getContext() {
	return myContext;
    }
    
    public void dragEnter(DropTargetDragEvent _event) {
	if (!isValid(_event)
	    || !_event.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
	    _event.rejectDrag();
    }
    
    public void dragOver(DropTargetDragEvent _event) {
	if (!isValid(_event)
	    || !_event.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
	    _event.rejectDrag();
    }
    
    public void dropActionChanged(DropTargetDragEvent _event) {
	/* empty */
    }
    
    public void dragExit(DropTargetEvent _event) {
	/* empty */
    }
    
    public void drop(DropTargetDropEvent _event) {
	if (!isValid(_event))
	    _event.rejectDrop();
	if (!_event.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
	    _event.rejectDrop();
	else {
	    try {
		_event.acceptDrop(1);
		Transferable tf = _event.getTransferable();
		List fileList
		    = (List) tf.getTransferData(DataFlavor.javaFileListFlavor);
		Iterator fileIterator = fileList.iterator();
		Vector fileVector = new Vector();
		while (fileIterator.hasNext()) {
		    Object obj = fileIterator.next();
		    if (obj instanceof IImportFile)
			fileVector.addElement(obj);
		    else {
			File file = (File) obj;
			IImportFile importFile
			    = ImportFileFactory.createImportFile(file);
			fileVector.addElement(importFile);
		    }
		}
		IImportFile[] files = new IImportFile[fileVector.size()];
		fileVector.copyInto(files);
		IContainer target = getTargetContainer(_event);
		org.jempeg.nodestore.model.IContainerModifier nodeModifier
		    = new ContainerModifierUI(myContext,
					      ContainerModifierFactory
						  .getInstance(target));
		nodeModifier.importFiles(files, null,
					 myContext
					     .getImportFilesProgressListener(),
					 true);
		_event.dropComplete(true);
	    } catch (Throwable e) {
		_event.dropComplete(false);
		Debug.handleError(myContext.getFrame(), e, true);
	    }
	}
    }
    
    protected abstract boolean isValid(DropTargetEvent droptargetevent);
    
    protected abstract IContainer getTargetContainer
	(DropTargetDropEvent droptargetdropevent);
}
