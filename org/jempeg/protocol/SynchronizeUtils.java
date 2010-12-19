/* SynchronizeUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.tree.TreePath;

import com.inzyme.filesystem.FileUtils;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.io.StreamUtils;
import com.inzyme.progress.IProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.Debug;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTagStringEvaluator;
import org.jempeg.nodestore.RemoteImportFile;
import org.jempeg.nodestore.model.FIDChangeSet;
import org.jempeg.tags.ITagWriter;
import org.jempeg.tags.TagWriterFactory;

public class SynchronizeUtils
{
    public static void downloadFile
	(TreePath _path, IFIDNode _node, int _index,
	 IProtocolClient _protocolClient, File _baseDir,
	 FIDChangeSet _changeSet, int _packetSize, boolean _useHijack,
	 IProgressListener _progressListener)
	throws IOException {
	if (!_progressListener.isStopRequested()) {
	    PropertiesManager propertiesManager
		= PropertiesManager.getInstance();
	    do {
		try {
		    _progressListener.operationStarted
			(ResourceBundleUtils
			     .getUIString("download.operation"));
		    boolean downloadFullPath
			= propertiesManager
			      .getBooleanProperty("jempeg.downloadFullPath");
		    File targetDir;
		    if (downloadFullPath) {
			StringBuffer fullPath = new StringBuffer();
			int size = _path.getPathCount();
			for (int i = size - 1; i >= 0; i--) {
			    IFIDNode node
				= (IFIDNode) _path.getPathComponent(i);
			    fullPath.insert(0, File.separator);
			    fullPath.insert
				(0, FileUtils.cleanseFilename(node.getTitle(),
							      true));
			}
			targetDir = new File(_baseDir, fullPath.toString());
		    } else
			targetDir = _baseDir;
		    File targetFile = null;
		    if (_node instanceof FIDPlaylist) {
			FIDPlaylist playlist = (FIDPlaylist) _node;
			String filenameFormatBase
			    = (playlist.isTransient()
			       ? "jempeg.soupFilenameFormat."
			       : "jempeg.filenameFormat.");
			String filenameFormat
			    = (propertiesManager.getProperty
			       (filenameFormatBase
				+ playlist.getTags().getValue("type")));
			String parsedName
			    = (FileUtils.cleanseFilename
			       (new NodeTagStringEvaluator(playlist)
				    .evaluate(filenameFormat),
				false));
			if (filenameFormat == null
			    || filenameFormat.indexOf('{') == -1)
			    filenameFormat
				= (PropertiesManager.getDefaults().getProperty
				   ("jempeg.filenameFormat.playlist"));
			targetFile = new File(targetDir, parsedName);
			targetFile.mkdirs();
			int size = playlist.getSize();
			_progressListener.operationUpdated(0L, (long) size);
			for (int i = 0; i < size; i++) {
			    IFIDNode node = playlist.getNodeAt(i);
			    File newDir
				= downloadFullPath ? _baseDir : targetFile;
			    downloadFile(_path.pathByAddingChild(playlist),
					 node, i, _protocolClient, newDir,
					 _changeSet, _packetSize, _useHijack,
					 _progressListener);
			    _progressListener.operationStarted
				(ResourceBundleUtils.getUIString
				 ("download.downloadFile.operation",
				  new Object[] { parsedName }));
			    _progressListener.operationUpdated((long) (i + 1),
							       (long) size);
			}
		    } else {
			try {
			    FIDPlaylist parentPlaylist
				= (FIDPlaylist) _path.getLastPathComponent();
			    IFIDNode child = parentPlaylist.getNodeAt(_index);
			    String filenameFormatBase
				= (parentPlaylist.isTransient()
				   ? "jempeg.soupFilenameFormat."
				   : "jempeg.filenameFormat.");
			    String filenameFormat
				= (propertiesManager.getProperty
				   (filenameFormatBase
				    + child.getTags().getValue("type")));
			    if (filenameFormat == null
				|| filenameFormat.indexOf('{') == -1)
				filenameFormat
				    = (PropertiesManager.getDefaults()
					   .getProperty
				       ("jempeg.filenameFormat.taxi"));
			    String parsedName
				= (FileUtils.cleanseFilename
				   (new NodeTagStringEvaluator
					(parentPlaylist, _index)
					.evaluate(filenameFormat),
				    false));
			    targetFile = new File(targetDir, parsedName);
			    String targetFileStr = targetFile.getParent();
			    if (targetFileStr != null)
				new File(targetFileStr).mkdirs();
			    if (!targetFile.exists()) {
				java.io.OutputStream os
				    = (new BufferedOutputStream
				       (new FileOutputStream(targetFile)));
				_progressListener.taskStarted
				    (ResourceBundleUtils.getUIString
				     ("download.downloadFile.operation",
				      new Object[] { parsedName }));
				RemoteImportFile remoteFile
				    = (RemoteImportFile.createInstance
				       (_node, _protocolClient));
				InputStream is
				    = remoteFile.getInputStream(_useHijack);
				try {
				    StreamUtils.copy(is, os, _packetSize,
						     (long) _node.getLength(),
						     _progressListener);
				} catch (Object object) {
				    is.close();
				    throw object;
				}
				is.close();
				os.close();
				_changeSet.nodeAdded(_node);
				try {
				    ITagWriter tagWriter
					= TagWriterFactory
					      .createTagWriter(_node);
				    tagWriter.writeTags(_node, parentPlaylist,
							targetFile);
				    break;
				} catch (Throwable t) {
				    Debug.println(8, t);
				    break;
				}
			    }
			    IImportFile importFile
				= ImportFileFactory
				      .createImportFile(targetFile);
			    _changeSet.fileSkipped
				(importFile,
				 (ResourceBundleUtils.getUIString
				  ("download.downloadFile.skipped")));
			} catch (IOException e) {
			    Debug.println(e);
			    IImportFile importFile
				= ImportFileFactory
				      .createImportFile(targetFile);
			    _changeSet.fileFailed(importFile, e);
			    try {
				targetFile.delete();
			    } catch (Throwable t) {
				Debug.println(e);
			    }
			}
		    }
		} catch (Object object) {
		    _progressListener.operationUpdated(1L, 1L);
		    throw object;
		}
	    } while (false);
	    _progressListener.operationUpdated(1L, 1L);
	}
    }
}
