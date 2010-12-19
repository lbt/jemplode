package org.jempeg.nodestore.model;

import java.util.List;
import java.util.Vector;

import org.jempeg.JEmplodeProperties;
import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.FIDLocalFile;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.NodeTags;
import org.jempeg.nodestore.PlayerDatabase;

import com.inzyme.container.AllowAllFilter;
import com.inzyme.container.ContainerUtils;
import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.IImportFolder;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.sort.QuickSort;
import com.inzyme.text.CollationKeyCache;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.tree.AllowAllTraversalFilter;
import com.inzyme.util.Debug;

/**
 * AbstractPlaylistContainerModifier is a modifier for FIDPlaylists and
 * their child IFIDNodes.
 * 
 * @author Mike Schrag
 */
public abstract class AbstractContainerModifier implements IContainerModifier {
  private PlayerDatabase myPlayerDatabase;
  private IContainer myTargetContainer;

  public AbstractContainerModifier(PlayerDatabase _playerDatabase, IContainer _targetContainer) {
    myPlayerDatabase = _playerDatabase;
    myTargetContainer = _targetContainer;
  }

  protected PlayerDatabase getPlayerDatabase() {
    return myPlayerDatabase;
  }

  public IContainer getTargetContainer() {
    return myTargetContainer;
  }

  public FIDChangeSet importFiles(IImportFile[] _sourceFiles, IConfirmationListener _confirmationListener, IProgressListener _progressListener, boolean _identifyImmediately) {
    boolean alreadyInProgress = false;

    synchronized (myPlayerDatabase.getSynchronizeQueue()) {
      FIDChangeSet changeSet = new FIDChangeSet();

      _progressListener.setStopEnabled(true);
      try {
        Vector importVec = new Vector();
        for (int i = 0; i < _sourceFiles.length; i ++ ) {
          ContainerUtils.traverse(_sourceFiles[i], importVec, new AllowAllFilter(), new AllowAllTraversalFilter());
        }
        int totalCount = importVec.size();

        alreadyInProgress = _progressListener.isInProgress();
        _progressListener.progressStarted();
        _progressListener.operationStarted(ResourceBundleUtils.getUIString("import.operation"));
        _progressListener.operationUpdated(0, totalCount);

        importFiles(myTargetContainer, _sourceFiles, changeSet, _progressListener, _identifyImmediately);
      }
      finally {
        if (!alreadyInProgress) {
          _progressListener.progressCompleted();
        }
      }

      return changeSet;
    }
  }

  public void importFiles(IContainer _targetContainer, IImportFile[] _sourceFiles, FIDChangeSet _changeSet, IProgressListener _progressListener, boolean _identifyImmediately) {
    boolean updateTagsOnDuplicates = PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.UPDATE_TAGS_ON_DUPLICATES);
    boolean deduplication = PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.DEDUPLICATION_PROPERTY);

    _progressListener.operationStarted(ResourceBundleUtils.getUIString("import.container.operation", new Object[] {
      _targetContainer.getName()
    }));
    CollationKeyCache cache = CollationKeyCache.createDefaultCache();
    for (int i = 0; !_progressListener.isStopRequested() && i < _sourceFiles.length; i ++ ) {
      IImportFile sourceFile = _sourceFiles[i];

      try {
        // ... Skip over Unix hidden files
        String sourceFileName = sourceFile.getName();
        if (sourceFileName.startsWith(".")) {
          continue;
        }

        _progressListener.taskStarted(ResourceBundleUtils.getUIString("import.container.task", new Object[] {
          sourceFileName
        }));
        _progressListener.taskUpdated(0, 1);

        if (sourceFile instanceof IImportFolder) {
          cache.clear();

          IImportFolder sourceFolder = (IImportFolder) sourceFile;
          IImportFile[] childFiles = sourceFolder.getChildren();
          if (childFiles != null) {
            QuickSort qs = new QuickSort(CollationKeyCache.createDefaultCache());
            qs.sort(childFiles);

            IContainer parentPlaylist = importContainer(_targetContainer, sourceFolder, _changeSet, _progressListener);
            System.out.println("AbstractContainerModifier.importFiles: targetContainer = " + _targetContainer + "; sourceFolder = " + sourceFolder + "; parentPlaylist = " + parentPlaylist);
            if (parentPlaylist instanceof FIDPlaylist) {
              FIDPlaylist playlist = (FIDPlaylist) parentPlaylist;
              if (playlist.isDirty()) {
                playlist.copyTagsIfNotEmpty(sourceFolder);
              }
            }
            ContainerModifierFactory.getInstance(parentPlaylist).importFiles(parentPlaylist, childFiles, _changeSet, _progressListener, _identifyImmediately);
          }
        }
        else {
          boolean alreadyExists = true;
          boolean shouldDeleteCreatedNode = true;
          FIDLocalFile createdNode = null;
          FIDLocalFile cachedLocalFileNode = FIDLocalFile.getExistingInstance(myPlayerDatabase, sourceFile);

          try {
            IFIDNode nodeToAdd;
            if (cachedLocalFileNode == null) {
              createdNode = FIDLocalFile.createInstance(myPlayerDatabase, sourceFile, _identifyImmediately, true);

              Object duplicateNodesObj = myPlayerDatabase.getNodeMap().getDuplicateNodes(createdNode);
              if (deduplication && duplicateNodesObj != null) {
                if (duplicateNodesObj instanceof List) {
                  nodeToAdd = (IFIDNode) ((List) duplicateNodesObj).get(0);
                }
                else {
                  nodeToAdd = (IFIDNode) duplicateNodesObj;
                }
                if (updateTagsOnDuplicates) {
                  NodeTags oldTags = nodeToAdd.getTags();
                  NodeTags newTags = createdNode.getTags();
                  for (int coreTagNum = 0; coreTagNum < DatabaseTags.CORE_TAGS.length; coreTagNum ++ ) {
                    String tagName = DatabaseTags.CORE_TAGS[coreTagNum];
                    String oldValue = oldTags.getValue(tagName);
                    String newValue = newTags.getValue(tagName);
                    if (!oldValue.equals(newValue)) {
                      oldTags.setValue(tagName, newValue);
                    }
                  }
                }
              }
              else {
                nodeToAdd = createdNode;
                alreadyExists = false;
              }
            }
            else {
              nodeToAdd = cachedLocalFileNode;
            }

            importFile(_targetContainer, nodeToAdd, _changeSet, cache);

            if (alreadyExists) {
              Debug.println(Debug.WARNING, "Skipping duplicate " + sourceFile + " of " + nodeToAdd);
              _changeSet.fileSkipped(sourceFile, ResourceBundleUtils.getUIString("import.skipped.duplicate", new Object[] {
                nodeToAdd.getTitle()
              }));
            }
            else {
              createdNode.addToDatabase(true);
              _changeSet.nodeAdded(createdNode);
              shouldDeleteCreatedNode = false;
            }
          }
          finally {
            if (createdNode != null && shouldDeleteCreatedNode) {
              createdNode.delete();
            }
          }
        }
      }
      catch (Throwable t) {
        Debug.println(t);
        _changeSet.fileFailed(sourceFile, t);
      }
      finally {
        _progressListener.taskUpdated(1, 1);
        _progressListener.operationUpdated(1);
      }
    }
  }

  /**
   * Imports a single file into a playlist.
   * 
   * @param _targetPlaylist the playlist to import into
   * @param _node the node to import
   * @param _changeSet the change set to record changes with
   * @return the duplicate node if there was one (or null if there wasn't)
   */
  protected abstract boolean importFile(IContainer _targetContainer, IFIDNode _node, FIDChangeSet _changeSet, CollationKeyCache _cache);

  /**
   * Imports a single playlist
   * 
   * @param _targetPlaylist the playlist to import into
   * @param _sourceFolder the folder to import
   * @param _changeSet the change set to record changes with
   * @param _progressListener the progress listener to record status with
   * @return the playlist that was created or already existed
   */
  protected abstract IContainer importContainer(IContainer _targetContainer, IImportFolder _sourceFolder, FIDChangeSet _changeSet, IProgressListener _progressListener);
}