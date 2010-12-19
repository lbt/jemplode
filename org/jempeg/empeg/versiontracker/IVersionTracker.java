package org.jempeg.empeg.versiontracker;

import java.io.IOException;

import com.inzyme.model.Reason;

/**
* IVersionTracker is implemented by anything that can
* track the latest version of a product.
*
* @author Mike Schrag
*/
public interface IVersionTracker {
  /**
  * Returns the name of the product that this tracks.
  *
  * @returns the name of the product that this tracks
  */
  public String getProductName();

  /**
  * Returns whether or not this product should be auto-updated.
  *
  * @returns whether or not this product should be auto-updated
  */
  public boolean shouldAutoUpdate();

  /**
  * Sets whether or not the user wants to auto-update this product.
  *
  * @param _autoUpdate whether or not the user wants to auto-update this product
  */
  public void setAutoUpdate(boolean _autoUpdate);

  /**
  * Returns whether or not this tracker should prompt before auto-updating.
  *
  * @returns whether or not this tracker should prompt before auto-updating
  */
  public boolean shouldPromptBeforeUpdate();

  /**
  * Sets whether or not this tracker should prompt before auto-updating.
  *
  * @param _promtpsBeforeUpdate whether or not this tracker should prompt before auto-updating
  */
  public void setPromptsBeforeUpdate(boolean _promptsBeforeUpdate);

  /**
  * Returns whether or not this product is installed.
  *
  * @returns whether or not this product is installed
  * @throws IOException if it cannot be determined
  */
  public boolean isInstalled() throws IOException;

  /**
  * Returns an explanation of why this product failed.
  *
  * @param _operation the operation that was performed
  * @returns an explanation of why this product failed
  */
  public Reason getFailureReason(String _operation);

  /**
  * Returns the version string for the currently
  * installed version of this product.
  *
  * @returns the installed version
  * @throws IOException if the installed version cannot be determined
  */
  public String getInstalledVersion() throws IOException;

  /**
  * Returns the version string for the latest
  * version of this product.
  *
  * @returns the latest version
  * @throws IOException if the latest version cannot be determined
  */
  public String getLatestVersion() throws IOException;

  /**
  * Returns whether or not a newer version is available.
  *
  * @returns whether or not a newer version is available
  * @throws IOException if the versions cannot be compared
  */
  public boolean isNewerVersionAvailable() throws IOException;

  /**
  * Returns the release notes for this version.
  *
  * @returns the release notes for this version
  * @throws IOException if the release notes cannot be retrieved
  */
  public String getReleaseNotes() throws IOException;

  /**
  * Returns a list of changes from your installed version to
  * the latest version.
  *
  * @returns a list of changes
  * @throws IOException if the changes cannot be determined
  */
  public VersionChange[] getChanges() throws IOException;

  /**
  * Installs the latest version.
  *
  * @throws IOException if the install fails
  */
  public void installLatestVersion() throws IOException;

  /**
  * Returns whether or not a restart is required.
  *
  * @returns whether or not a restart is required
  */
  public boolean isRestartRequired();
}
