package org.jempeg.nodestore.model;

import com.inzyme.container.ContainerSelection;
import com.inzyme.container.IContainer;
import com.inzyme.filesystem.IImportFile;
import com.inzyme.progress.IConfirmationListener;
import com.inzyme.progress.IProgressListener;

/**
 * IContainerModifier provides the implementations of
 * higher level operations on various combinations of
 * a parent and child type.
 * 
 * @author Mike Schrag
 */
public interface IContainerModifier {
  /**
   * Returns the container that this is modifying.
   * 
   * @return the container that this is modifying
   */
  public IContainer getTargetContainer();

  /**
   * Deletes the given selection.
   * 
   * @param _selection the selection to delete
   * @param _confirmationListener the confirmation listener to use
   */
  public void delete(ContainerSelection _selection, IConfirmationListener _confirmationListener);

  /**
   * Imports the given files into a container.
   * 
   * @param _sourceFiles the files to import
   * @param _target the target container to put them in
   * @param _confirmationListener the confirmation listener to use
   * @param _progressListener the progress listener to update with
   * @param _identifyImmediately whether or not to immediately identify the imported files
   * @return the FIDChangeSet for the import
   */
  public FIDChangeSet importFiles(IImportFile[] _sourceFiles, IConfirmationListener _confirmationListener, IProgressListener _progressListener, boolean _identifyImmediately);

  /**
   * Imports the given files into a container.
   * 
   * @param _target the target container to put them in
   * @param _sourceFile the files to import
   * @param _changeSet the change set to write to
   * @param _confirmationListener the confirmation listener to use
   * @param _progressListener the progress listener to update with
   * @param _identifyImmediately whether or not to immediately identify the imported files
   */
  public void importFiles(IContainer _targetContainer, IImportFile[] _sourceFiles, FIDChangeSet _changeSet, IProgressListener _progressListener, boolean _identifyImmediately);

  /**
   * Called by the target after it receives a call to moveTo.  This gives the source an opportunity
   * to clean up the moved files if necessary.
   * 
   * @param _selection the selection that was moved
   */
  public void moveFrom(ContainerSelection _selection);

  /**
   * Called to move a selection into a container.  After performing the copy, 
   * this method should lookup the NodeModifier for the source playlist and
   * call moveFrom(_selection).
   * 
   * @param _target the target container to move the selection into
   * @param _sourceSelection the selection to move into this target
   * @param _confirmationListener the confirmation listener to use
   * @return the set of indexes that were added to the target container
   */
  public int[] moveTo(ContainerSelection _sourceSelection, IConfirmationListener _confirmationListener, IProgressListener _progressListener);

  /**
   * Called to copy a selection into a container.
   * 
   * @param _target the target container to copy the selection into
   * @param _sourceSelection the selection to copy into this target
   * @param _confirmationListener the confirmation listener to use
   * @param _deepCopy should the playlists be deeply copied?
   * @return the set of indexes that were added to the target container
   */
  public int[] copyTo(ContainerSelection _sourceSelection, IConfirmationListener _confirmationListener, boolean _deepCopy, IProgressListener _progressListener);
}