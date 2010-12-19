package org.jempeg.nodestore;

import java.io.IOException;

import org.jempeg.protocol.IProtocolClient;

import com.inzyme.filesystem.IImportFile;
import com.inzyme.filesystem.IImportFolder;

public class RemoteImportFolder extends RemoteImportFile implements IImportFolder {
  protected RemoteImportFolder(FIDPlaylist _remotePlaylist, IProtocolClient _protocolClient) {
    super(_remotePlaylist, _protocolClient);
  }

  public IImportFile[] getChildren() throws IOException {
    IImportFile[] children;
    if (isContainer()) {
      FIDPlaylist playlist = (FIDPlaylist) getNode();
      int size = playlist.size();
      children = new IImportFile[size];
      for (int i = 0; i < size; i++) {
        IFIDNode child = playlist.getNodeAt(i);
        children[i] = RemoteImportFile.createInstance(child, getProtocolClient());
      }
    }
    else {
      children = null;
    }
    return children;
  }
}