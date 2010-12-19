/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package org.jempeg.manager.event;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jempeg.ApplicationContext;
import org.jempeg.manager.ui.ContainerModifierUI;
import org.jempeg.nodestore.model.ContainerModifierFactory;
import org.jempeg.nodestore.model.IContainerModifier;

import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.util.Debug;

/**
* AbstractFileDropTargetListener defines the capabilites that
* are common to dragging-and-dropping files from the native
* filesystem onto either the playlist tree or playlist table.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public abstract class AbstractFileDropTargetListener implements DropTargetListener {
	private ApplicationContext myContext;
	
	public AbstractFileDropTargetListener(ApplicationContext _context) {
		myContext = _context;
	}
	
	protected ApplicationContext getContext() {
		return myContext;
	}

	public void dragEnter(DropTargetDragEvent _event) {
		if (!isValid(_event) || !_event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			_event.rejectDrag();
		}
	}

	public void dragOver(DropTargetDragEvent _event) {
		if (!isValid(_event) || !_event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			_event.rejectDrag();
		}
	}

  public void dropActionChanged(DropTargetDragEvent _event) {
	}
  
  public void dragExit(DropTargetEvent _event) {
	}

	public void drop(DropTargetDropEvent _event) {
    if (!isValid(_event)) {
      _event.rejectDrop();
    }

		if (!_event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			_event.rejectDrop();
		} else {
			try {
				_event.acceptDrop(DnDConstants.ACTION_COPY);
				
				Transferable tf = _event.getTransferable();
				List fileList = (List)tf.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator fileIterator = fileList.iterator();
				Vector fileVector = new Vector();
				while (fileIterator.hasNext()) {
          Object obj = fileIterator.next();
          if (obj instanceof IImportFile) {
					  fileVector.addElement(obj);
          } else {
            File file = (File)obj;
            IImportFile importFile = ImportFileFactory.createImportFile(file);
            fileVector.addElement(importFile);
          }
				}
				IImportFile[] files = new IImportFile[fileVector.size()];
				fileVector.copyInto(files);
				
				IContainer target = getTargetContainer(_event);
				IContainerModifier nodeModifier = new ContainerModifierUI(myContext, ContainerModifierFactory.getInstance(target));
				nodeModifier.importFiles(files, null, myContext.getImportFilesProgressListener(), true);

				_event.dropComplete(true);
			}
				catch (Throwable e) {
					_event.dropComplete(false);
					Debug.handleError(myContext.getFrame(), e, true);
				}
		}
	}

  protected abstract boolean isValid(DropTargetEvent _event);

	protected abstract IContainer getTargetContainer(DropTargetDropEvent _event);
}
