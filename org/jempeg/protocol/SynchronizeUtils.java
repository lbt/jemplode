package org.jempeg.protocol;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.tree.TreePath;

import org.jempeg.JEmplodeProperties;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTagStringEvaluator;
import org.jempeg.nodestore.RemoteImportFile;
import org.jempeg.nodestore.model.FIDChangeSet;
import org.jempeg.tags.ITagWriter;
import org.jempeg.tags.TagWriterFactory;

import com.inzyme.filesystem.FileUtils;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.ImportFileFactory;
import com.inzyme.io.StreamUtils;
import com.inzyme.progress.IProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.util.Debug;

/**
 * Handy synchronization utility methods.
 * 
 * @author Mike Schrag
 */
public class SynchronizeUtils {
  /**
   * Downloads the given node from the device onto your local machine, performing filename evaluation and tag rewriting if you have any tagwriters installed. Normally this would be in SynchronizeUI with all the other downloading code, but if you ever wanted to initiate a download without a UI, you could call this method.
   * 
   * @param _path a treepath that points to the desired node (this can be a path of one entry that is the node's parent if you have EmplodeConstants.DOWNLOAD_FULL_PATH_PROPERTY disabled)
   * @param _node the node to download
   * @param _index the index of the node in its parent (some evaluated tags care about this)
   * @param _protocolClient the ProtocolClient to download with
   * @param _baseDir the base folder to download into
   * @param _changeSet the change set that will contain the results of the download
   * @param _packetSize the packet size to download with
   * @param _useHijack lame, but it has to come from somewhere -- this says whether or not the hijack API's should be used
   * @param _progressListener the progress listener to report download status to
   * @throws IOException if the download files
   */
  public static void downloadFile(TreePath _path, IFIDNode _node, int _index, IProtocolClient _protocolClient, File _baseDir, FIDChangeSet _changeSet, int _packetSize, boolean _useHijack, IProgressListener _progressListener) throws IOException {
    if (!_progressListener.isStopRequested()) {
      PropertiesManager propertiesManager = PropertiesManager.getInstance();
      try {
        _progressListener.operationStarted(ResourceBundleUtils.getUIString("download.operation"));

        File targetDir;
        boolean downloadFullPath = propertiesManager.getBooleanProperty(JEmplodeProperties.DOWNLOAD_FULL_PATH_PROPERTY);
        if (downloadFullPath) {
          StringBuffer fullPath = new StringBuffer();
          int size = _path.getPathCount();
          for (int i = size - 1; i >= 0; i -- ) {
            IFIDNode node = (IFIDNode) _path.getPathComponent(i);
            fullPath.insert(0, File.separator);
            fullPath.insert(0, FileUtils.cleanseFilename(node.getTitle(), true));
          }
          targetDir = new File(_baseDir, fullPath.toString());
        }
        else {
          targetDir = _baseDir;
        }

        File targetFile = null;
        if (_node instanceof FIDPlaylist) {
          FIDPlaylist playlist = (FIDPlaylist) _node;

          String filenameFormatBase = (playlist.isTransient()) ? JEmplodeProperties.FILENAME_FORMAT_SOUP_BASE : JEmplodeProperties.FILENAME_FORMAT_BASE;
          String filenameFormat = propertiesManager.getProperty(filenameFormatBase + playlist.getTags().getValue(DatabaseTags.TYPE_TAG));
          String parsedName = FileUtils.cleanseFilename(new NodeTagStringEvaluator(playlist).evaluate(filenameFormat), false);
          if (filenameFormat == null || filenameFormat.indexOf('{') == -1) {
            filenameFormat = PropertiesManager.getDefaults().getProperty(JEmplodeProperties.FILENAME_FORMAT_PLAYLIST_KEY);
          }
          targetFile = new File(targetDir, parsedName);
          targetFile.mkdirs();

          int size = playlist.getSize();
          _progressListener.operationUpdated(0, size);
          for (int i = 0; i < size; i ++ ) {
            IFIDNode node = playlist.getNodeAt(i);
            File newDir = (downloadFullPath) ? _baseDir : targetFile;
            downloadFile(_path.pathByAddingChild(playlist), node, i, _protocolClient, newDir, _changeSet, _packetSize, _useHijack, _progressListener);
            _progressListener.operationStarted(ResourceBundleUtils.getUIString("download.downloadFile.operation", new Object[] {
              parsedName
            }));
            _progressListener.operationUpdated(i + 1, size);
          }
        }
        else {
          try {
            FIDPlaylist parentPlaylist = (FIDPlaylist) _path.getLastPathComponent();
            IFIDNode child = parentPlaylist.getNodeAt(_index);
            String filenameFormatBase = (parentPlaylist.isTransient()) ? JEmplodeProperties.FILENAME_FORMAT_SOUP_BASE : JEmplodeProperties.FILENAME_FORMAT_BASE;
            String filenameFormat = propertiesManager.getProperty(filenameFormatBase + child.getTags().getValue(DatabaseTags.TYPE_TAG));
            if (filenameFormat == null || filenameFormat.indexOf('{') == -1) {
              filenameFormat = PropertiesManager.getDefaults().getProperty(JEmplodeProperties.FILENAME_FORMAT_TAXI_KEY);
            }
            String parsedName = FileUtils.cleanseFilename(new NodeTagStringEvaluator(parentPlaylist, _index).evaluate(filenameFormat), false);

            targetFile = new File(targetDir, parsedName);
            String targetFileStr = targetFile.getParent();
            if (targetFileStr != null) {
              // make sure all directories are created
              new File(targetFileStr).mkdirs();
            }

            if (!targetFile.exists()) {
              OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile));
              _progressListener.taskStarted(ResourceBundleUtils.getUIString("download.downloadFile.operation", new Object[] {
                parsedName
              }));
              RemoteImportFile remoteFile = RemoteImportFile.createInstance(_node, _protocolClient);
              InputStream is = remoteFile.getInputStream(_useHijack);
              try {
                StreamUtils.copy(is, os, _packetSize, _node.getLength(), _progressListener);
              }
              finally {
                is.close();
              }
              os.close();
              _changeSet.nodeAdded(_node);

              try {
                ITagWriter tagWriter = TagWriterFactory.createTagWriter(_node);
                tagWriter.writeTags(_node, parentPlaylist, targetFile);
              }
              catch (Throwable t) {
                // That's OK ...
                Debug.println(Debug.WARNING, t);
              }
            }
            else {
              IImportFile importFile = ImportFileFactory.createImportFile(targetFile);
              _changeSet.fileSkipped(importFile, ResourceBundleUtils.getUIString("download.downloadFile.skipped"));
            }
          }
          catch (IOException e) {
            Debug.println(e);
            IImportFile importFile = ImportFileFactory.createImportFile(targetFile);
            _changeSet.fileFailed(importFile, e);
            try {
              targetFile.delete();
            }
            catch (Throwable t) {
              Debug.println(e);
            }
            //myEmplode.handleError("Failed to download " + targetFile.getName(), e);
          }
        }
      }
      finally {
        _progressListener.operationUpdated(1, 1);
      }
    }
  }
}