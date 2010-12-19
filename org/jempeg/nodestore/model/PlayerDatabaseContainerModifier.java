package org.jempeg.nodestore.model;

import org.jempeg.nodestore.IFIDNode;
import org.jempeg.nodestore.PlayerDatabase;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFolder;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;
import com.inzyme.text.CollationKeyCache;

public class PlayerDatabaseContainerModifier extends AbstractContainerModifier {
  public PlayerDatabaseContainerModifier(PlayerDatabase _playerDatabase) {
    super(_playerDatabase, _playerDatabase);
  }

  public int[] copyTo(ContainerSelection _sourceSelection, IConfirmationListener _confirmationListener, boolean _deepCopy, IProgressListener _progressListener) {
    return new int[0];
  }

  public void delete(ContainerSelection _selection, IConfirmationListener _confirmationListener) {
  }

  protected IContainer importContainer(IContainer _targetContainer, IImportFolder _sourceFolder, FIDChangeSet _changeSet, IProgressListener _progressListener) {
    return _targetContainer;
  }

  protected boolean importFile(IContainer _targetContainer, IFIDNode _node, FIDChangeSet _changeSet, CollationKeyCache _cache) {
    return true;
  }

  public void moveFrom(ContainerSelection _selection) {
  }

  public int[] moveTo(ContainerSelection _sourceSelection, IConfirmationListener _confirmationListener, IProgressListener _progressListener) {
    return new int[0];
  }
}