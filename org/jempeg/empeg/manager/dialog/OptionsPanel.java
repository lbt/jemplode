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
package org.jempeg.empeg.manager.dialog;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jempeg.ApplicationContext;
import org.jempeg.JEmplodeProperties;
import org.jempeg.empeg.versiontracker.HijackVersionTracker;
import org.jempeg.empeg.versiontracker.JEmplodeVersionTracker;
import org.jempeg.manager.ui.NodeColorizer;
import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.nodestore.PlayerDatabase;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.ui.DialogUtils;

/**
 * The "Options" panel for configuring JEmplode.
 *
 * @author Mike Schrag
 * @version $Revision: 1.14 $
 **/
public class OptionsPanel extends AbstractChangeablePanel {
  private ApplicationContext myContext;
  private JTextField myFilenameFormatTF;
  private JTextField mySoupFilenameFormatTF;
  private JTextField myDownloadDirTF;
  private JCheckBox myDownloadFullPathCB;
  private JCheckBox myAutoUpdateJEmplodeCB;
  private JCheckBox myPromptBeforeJEmplodeUpdateCB;
  private JTextField myDoNotBroadcastToTF;
  private JCheckBox myUseHijackCB;
  private JCheckBox myWriteID3v2CB;
  private JCheckBox myPreserveID3v2CB;
  private JCheckBox myRemoveID3v1CB;
  private JCheckBox myAutoUpdateHijackCB;
  private JCheckBox myPromptBeforeHijackUpdateCB;
  private JTextField myHijackURLTF;
  private JTextField myHijackMkVersionTF;
  private JTextField myHijackSaveLocationTF;
  private JTextField myHijackLoginTF;
  private JTextField myHijackPasswordTF;
  private JCheckBox myUseFastConnectionCB;
  private JCheckBox myAllowUnknownTypesCB;
  private JCheckBox myOnlyShowFailedImportsCB;
  private JCheckBox myAutoSelectCB;
  private JCheckBox myDisableSoupUpdatingCB;
  private JCheckBox myAutoSortCB;
  private JTextField myAutoSortTagTF;
  private JCheckBox myAutoSortDirectionCB;
  private JCheckBox myRecursiveColorsCB;
  private JCheckBox myRecursiveSoupColorsCB;
  private JCheckBox myGlobalDedupeCB;
  private JCheckBox myImportM3UCB;
  private JCheckBox myUpdateTagsOfDuplicateCB;
  private JCheckBox myStripArticlesWhenComparingCB;
  private JCheckBox myTonySelectionsCB;
  private JCheckBox myRebuildFromMemoryCB;
  private JCheckBox mySortPlaylistsAboveTunesCB;
  private JTextField myLookAndFeelTF;

  public OptionsPanel(ApplicationContext _context) {
    myContext = _context;

    setName("Options");

    BorderLayout bl = new BorderLayout();
    setLayout(bl);

    JPanel fieldsPanel = new JPanel();
    JLabel filenameFormatLabel = new JLabel("Filename Format:");
    filenameFormatLabel.setToolTipText("When files are downloaded from the Empeg, they will be saved in this format.");
    myFilenameFormatTF = new JTextField();

    JLabel soupFilenameFormatLabel = new JLabel("Soup Filename Format:");
    soupFilenameFormatLabel.setToolTipText("When files are downloaded from the soup playlists, they will be saved in this format.");
    mySoupFilenameFormatTF = new JTextField();

    JLabel downloadDirLabel = new JLabel("Download Directory:");
    downloadDirLabel.setToolTipText("If you don't want to be asked where to save downloads every time, set this location.");
    myDownloadDirTF = new JTextField();

    JLabel downloadFullPathLabel = new JLabel("Download with Full Path:");
    downloadFullPathLabel.setToolTipText("If you want downloads to be saved with their full Empeg playlist path, check this.");
    myDownloadFullPathCB = new JCheckBox();

    JLabel writeID3v2Label = new JLabel("Write ID3v2 Tags to Downloaded Files:");
    writeID3v2Label.setToolTipText("Check this box to write Empeg database information to ID3v2 tags in downloaded tracks.");
    myWriteID3v2CB = new JCheckBox();

    JLabel preserveID3v2Label = new JLabel("Preserve Existing Tag Frames in Downloaded Files:");
    preserveID3v2Label.setToolTipText("Check this box to preserve information in existing ID3v2 tags (information from the Empeg database, such as Title and Artist, will still overwrite old information if tag writing is enabled).");
    myPreserveID3v2CB = new JCheckBox();

    JLabel removeID3v1Label = new JLabel("Remove ID3v1 Tags from Downloaded Files:");
    removeID3v1Label.setToolTipText("Check this box to remove ID3v1 tags from all downloaded files.");
    myRemoveID3v1CB = new JCheckBox();

    JLabel autoUpdateJEmplodeLabel = new JLabel("Autoupdate jEmplode:");
    autoUpdateJEmplodeLabel.setToolTipText("If you want your jEmplode to be auto-updated every time you run, check this.");
    myAutoUpdateJEmplodeCB = new JCheckBox();

    JLabel promptBeforeJEmplodeUpdateLabel = new JLabel("Prompt Before jEmplode Autoupdate:");
    promptBeforeJEmplodeUpdateLabel.setToolTipText("If you want to be prompted prior to auto-updating jEmplode, then check this.");
    myPromptBeforeJEmplodeUpdateCB = new JCheckBox();

    JLabel useHijackLabel = new JLabel("Use HiJack when Possible:");
    useHijackLabel.setToolTipText("If you want HiJack to be used whenever possible (rather than the Empeg protocol), check this.");
    myUseHijackCB = new JCheckBox();

    JLabel autoUpdateHijackLabel = new JLabel("Autoupdate HiJack:");
    autoUpdateHijackLabel.setToolTipText("If you want your HiJack kernel to be auto-updated every time you run jEmplode, check this.");
    myAutoUpdateHijackCB = new JCheckBox();

    JLabel promptBeforeHijackUpdateLabel = new JLabel("Prompt Before Hijack Autoupdate:");
    promptBeforeHijackUpdateLabel.setToolTipText("If you want to be prompted prior to auto-updating Hijack, then check this.");
    myPromptBeforeHijackUpdateCB = new JCheckBox();

    JLabel hijackURLLabel = new JLabel("Hijack URL:");
    hijackURLLabel.setToolTipText("The URL to the Hijack website (if you answered yes to the above).");
    myHijackURLTF = new JTextField();

    JLabel hijackMkVersionLabel = new JLabel("Hijack Mk Version:");
    hijackMkVersionLabel.setToolTipText("The Mk1/2 version in Hijack format (i.e. 'mk2' in 'v235.hijack.v200b11.mk2.zImage' format).");
    myHijackMkVersionTF = new JTextField();

    JLabel hijackLoginLabel = new JLabel("Hijack FTP Login:");
    hijackLoginLabel.setToolTipText("The login name for your Hijack FTP server.");
    myHijackLoginTF = new JTextField();

    JLabel hijackPasswordLabel = new JLabel("Hijack FTP Password:");
    hijackPasswordLabel.setToolTipText("The password for your Hijack FTP server.");
    myHijackPasswordTF = new JPasswordField();

    JLabel hijackSaveLocationLabel = new JLabel("Hijack Save Location:");
    hijackSaveLocationLabel.setToolTipText("The directory to save Hijack kernesl into (or blank to just delete them after downloading).");
    myHijackSaveLocationTF = new JTextField();

    JLabel useFastConnectionLabel = new JLabel("Use Fast Connection:");
    useFastConnectionLabel.setToolTipText("Should Fast Connection be used for uploads?  (May be unstable!)");
    myUseFastConnectionCB = new JCheckBox();

    JLabel allowUnknownTypesLabel = new JLabel("Allow Unknown Types:");
    allowUnknownTypesLabel.setToolTipText("If you drop a unidentified (non-media) file onto jEmplode, should it allow it (EmpegTaxi)?");
    myAllowUnknownTypesCB = new JCheckBox();

    JLabel onlyShowFailedImportsLabel = new JLabel("Only Show Failed Imports:");
    onlyShowFailedImportsLabel.setToolTipText("Only show Import Results for failed/skipped imports");
    myOnlyShowFailedImportsCB = new JCheckBox();

    JLabel doNotBroadcastToLabel = new JLabel("Do Not Broadcast To:");
    doNotBroadcastToLabel.setToolTipText("Comma separated list of broadcast addressees to not send requests to (i.e. 192.168.1.255,10.1.255.255)");
    myDoNotBroadcastToTF = new JTextField();

    JLabel disableSoupUpdatingLabel = new JLabel("Disable Soup Updating:");
    disableSoupUpdatingLabel.setToolTipText("If you are having problems with the soups, you can turn off autoupdating -- this doesn't work till you restart or redownload.");
    myDisableSoupUpdatingCB = new JCheckBox();

    JLabel autoSortLabel = new JLabel("Auto Sort on Import:");
    autoSortLabel.setToolTipText("If you want your playlists resorted after you import files, turn this on.");
    myAutoSortCB = new JCheckBox();

    JLabel autoSortTagLabel = new JLabel("Tag Name to Auto Sort on:");
    autoSortTagLabel.setToolTipText("If Auto Sort is enabled, enter the name of the tag to autosort on ('title', 'artist', 'source', etc.)");
    myAutoSortTagTF = new JTextField();

    JLabel autoSortDirectionLabel = new JLabel("Auto Sort Ascending:");
    autoSortDirectionLabel.setToolTipText("If Auto Sort is enabled, turning this on sorts ascending.  Turning it off sorts descending.");
    myAutoSortDirectionCB = new JCheckBox();

    JLabel autoSelectLabel = new JLabel("Autoselect if 1 Empeg Found:");
    autoSelectLabel.setToolTipText("If you only have one Empeg and you're tired of selecting it in the connection dialog, check this.");
    myAutoSelectCB = new JCheckBox();

    JLabel recursiveColorsLabel = new JLabel("Colorize playlists recursively:");
    recursiveColorsLabel.setToolTipText("If you want a plsylist to appear dirty if any of its children are dirty, check this (note: you take a performance hit for this)");
    myRecursiveColorsCB = new JCheckBox();

    JLabel recursiveSoupColorsLabel = new JLabel("Colorize soup playlists recursively:");
    recursiveSoupColorsLabel.setToolTipText("If you want a soup plsylist to appear dirty if any of its soup children are dirty, check this (note: you take a performance hit for this)");
    myRecursiveSoupColorsCB = new JCheckBox();

    JLabel globalDedupeLabel = new JLabel("Deduplicate on Import:");
    globalDedupeLabel.setToolTipText("If you want a hash codes to be computed and checked at import time, then turn this on.  You will take a memory and performance hit to compute the and store the hashes.");
    myGlobalDedupeCB = new JCheckBox();

    JLabel importM3ULabel = new JLabel("Import M3U's:");
    importM3ULabel.setToolTipText("If you want to import M3U files as playlists, check this.");
    myImportM3UCB = new JCheckBox();

    JLabel updateTagsOfDuplicateLabel = new JLabel("Update Tags of Duplicates:");
    updateTagsOfDuplicateLabel.setToolTipText("If a duplicate tune is imported, should the tags be checked for changes?");
    myUpdateTagsOfDuplicateCB = new JCheckBox();

    JLabel stripArticlesWhenComparingLabel = new JLabel("Strip Articles when Comparing:");
    stripArticlesWhenComparingLabel.setToolTipText("Do you want The Beetles to be in the T's instead of the B's?");
    myStripArticlesWhenComparingCB = new JCheckBox();

    JLabel tonySelectionsLabel = new JLabel("Tony Selections:");
    tonySelectionsLabel.setToolTipText("Automatically select text in properties dialog text fields?");
    myTonySelectionsCB = new JCheckBox();

    JLabel rebuildFromMemoryLabel = new JLabel("Rebuild from Memory:");
    rebuildFromMemoryLabel.setToolTipText("Rebuild database from the in-memory version instead of actually looking at the Empeg (ALPHA)?  Requires Rebuild on PC to be set.");
    myRebuildFromMemoryCB = new JCheckBox();

    JLabel sortPlaylistsAboveTunesLabel = new JLabel("Sort Playlists Above Tunes:");
    sortPlaylistsAboveTunesLabel.setToolTipText("If checked, playlists sort above tunes; Otherwise, playlists and tunes will sort together.");
    mySortPlaylistsAboveTunesCB = new JCheckBox();

    JLabel lookAndFeelLabel = new JLabel("Look And Feel:");
    lookAndFeelLabel.setToolTipText("Enter the name of the look and feel to use (or \"system\" for the system lnf)");
    myLookAndFeelTF = new JTextField();

    GridBagLayout gridBagLayout = new GridBagLayout();
    fieldsPanel.setLayout(gridBagLayout);
    DialogUtils.addRow(filenameFormatLabel, myFilenameFormatTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(soupFilenameFormatLabel, mySoupFilenameFormatTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(downloadDirLabel, myDownloadDirTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(downloadFullPathLabel, myDownloadFullPathCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(writeID3v2Label, myWriteID3v2CB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(preserveID3v2Label, myPreserveID3v2CB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(removeID3v1Label, myRemoveID3v1CB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(autoUpdateJEmplodeLabel, myAutoUpdateJEmplodeCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(promptBeforeJEmplodeUpdateLabel, myPromptBeforeJEmplodeUpdateCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(useHijackLabel, myUseHijackCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(autoUpdateHijackLabel, myAutoUpdateHijackCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(promptBeforeHijackUpdateLabel, myPromptBeforeHijackUpdateCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(hijackURLLabel, myHijackURLTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(hijackMkVersionLabel, myHijackMkVersionTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(hijackLoginLabel, myHijackLoginTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(hijackPasswordLabel, myHijackPasswordTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(hijackSaveLocationLabel, myHijackSaveLocationTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(useFastConnectionLabel, myUseFastConnectionCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(allowUnknownTypesLabel, myAllowUnknownTypesCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(onlyShowFailedImportsLabel, myOnlyShowFailedImportsCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(doNotBroadcastToLabel, myDoNotBroadcastToTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(disableSoupUpdatingLabel, myDisableSoupUpdatingCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(autoSortLabel, myAutoSortCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(autoSortTagLabel, myAutoSortTagTF, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(autoSortDirectionLabel, myAutoSortDirectionCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(autoSelectLabel, myAutoSelectCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(recursiveColorsLabel, myRecursiveColorsCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(recursiveSoupColorsLabel, myRecursiveSoupColorsCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(globalDedupeLabel, myGlobalDedupeCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(importM3ULabel, myImportM3UCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(updateTagsOfDuplicateLabel, myUpdateTagsOfDuplicateCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(stripArticlesWhenComparingLabel, myStripArticlesWhenComparingCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(tonySelectionsLabel, myTonySelectionsCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(rebuildFromMemoryLabel, myRebuildFromMemoryCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(sortPlaylistsAboveTunesLabel, mySortPlaylistsAboveTunesCB, gridBagLayout, fieldsPanel);
    DialogUtils.addRow(lookAndFeelLabel, myLookAndFeelTF, gridBagLayout, fieldsPanel);
    add(fieldsPanel, BorderLayout.SOUTH);

    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
  }

  protected void read0(IDeviceSettings _configFile) {
    PropertiesManager propertiesManager = PropertiesManager.getInstance();

    fillInTextField(propertiesManager, myFilenameFormatTF, JEmplodeProperties.FILENAME_FORMAT_TUNE_KEY);
    fillInTextField(propertiesManager, mySoupFilenameFormatTF, JEmplodeProperties.FILENAME_FORMAT_SOUP_TUNE_KEY);
    fillInTextField(propertiesManager, myDownloadDirTF, JEmplodeProperties.DOWNLOAD_DIRECTORY_PROPERTY);
    fillInCheckBox(propertiesManager, myDownloadFullPathCB, JEmplodeProperties.DOWNLOAD_FULL_PATH_PROPERTY);
    fillInCheckBox(propertiesManager, myWriteID3v2CB, JEmplodeProperties.WRITE_ID3v2_PROPERTY);
    fillInCheckBox(propertiesManager, myPreserveID3v2CB, JEmplodeProperties.PRESERVE_ID3v2_PROPERTY);
    fillInCheckBox(propertiesManager, myRemoveID3v1CB, JEmplodeProperties.REMOVE_ID3v1_PROPERTY);

    JEmplodeVersionTracker jemplodeVersionTracker = new JEmplodeVersionTracker();
    fillInCheckBox(myAutoUpdateJEmplodeCB, jemplodeVersionTracker.shouldAutoUpdate());
    fillInCheckBox(myPromptBeforeJEmplodeUpdateCB, jemplodeVersionTracker.shouldPromptBeforeUpdate());
    fillInCheckBox(propertiesManager, myUseHijackCB, JEmplodeProperties.USE_HIJACK_KEY);

    HijackVersionTracker hijackVersionTracker = new HijackVersionTracker();
    fillInCheckBox(myAutoUpdateHijackCB, hijackVersionTracker.shouldAutoUpdate());
    fillInCheckBox(myPromptBeforeHijackUpdateCB, hijackVersionTracker.shouldPromptBeforeUpdate());
    fillInTextField(myHijackURLTF, hijackVersionTracker.getHijackURL());
    fillInTextField(myHijackMkVersionTF, hijackVersionTracker.getMkVersion());
    fillInTextField(propertiesManager, myHijackLoginTF, JEmplodeProperties.HIJACK_LOGIN_KEY);
    fillInTextField(propertiesManager, myHijackPasswordTF, JEmplodeProperties.HIJACK_PASSWORD_KEY);
    fillInTextField(myHijackSaveLocationTF, hijackVersionTracker.getHijackSaveLocation());

    fillInCheckBox(propertiesManager, myUseFastConnectionCB, JEmplodeProperties.USE_FAST_CONNECTIONS_PROPERTY);
    fillInCheckBox(propertiesManager, myAllowUnknownTypesCB, JEmplodeProperties.ALLOW_UNKNOWN_TYPES_PROPERTY);
    fillInCheckBox(propertiesManager, myOnlyShowFailedImportsCB, JEmplodeProperties.SHOW_ONLY_FAILED_IMPORTS_PROPERTY);
    fillInTextField(propertiesManager, myDoNotBroadcastToTF, JEmplodeProperties.DO_NOT_BROADCAST_TO_PROPERTY);
    fillInCheckBox(propertiesManager, myDisableSoupUpdatingCB, JEmplodeProperties.DISABLE_SOUP_UPDATING_PROPERTY);
    fillInCheckBox(propertiesManager, myAutoSelectCB, JEmplodeProperties.AUTO_SELECT_PROPERTY);
    fillInCheckBox(propertiesManager, myRecursiveColorsCB, JEmplodeProperties.RECURSIVE_COLORS_PROPERTY);
    fillInCheckBox(propertiesManager, myRecursiveSoupColorsCB, JEmplodeProperties.RECURSIVE_SOUP_COLORS_PROPERTY);
    fillInCheckBox(propertiesManager, myGlobalDedupeCB, JEmplodeProperties.DEDUPLICATION_PROPERTY);
    fillInCheckBox(propertiesManager, myImportM3UCB, JEmplodeProperties.IMPORT_M3U_PROPERTY);
    fillInCheckBox(propertiesManager, myUpdateTagsOfDuplicateCB, JEmplodeProperties.UPDATE_TAGS_ON_DUPLICATES);
    fillInCheckBox(propertiesManager, myStripArticlesWhenComparingCB, JEmplodeProperties.STRIP_ARTICLES_WHEN_COMPARING_KEY);
    fillInCheckBox(propertiesManager, myTonySelectionsCB, JEmplodeProperties.TONY_SELECTIONS_KEY);
    fillInCheckBox(propertiesManager, myRebuildFromMemoryCB, JEmplodeProperties.REBUILD_FROM_MEMORY_KEY);
    fillInCheckBox(propertiesManager, mySortPlaylistsAboveTunesCB, JEmplodeProperties.SORT_PLAYLISTS_ABOVE_TUNES);
    fillInTextField(propertiesManager, myLookAndFeelTF, JEmplodeProperties.LOOK_AND_FEEL_PROPERTY);
  }

  protected void write0(IDeviceSettings _configFile) {
    PropertiesManager propertiesManager = PropertiesManager.getInstance();

    String filenameFormat = myFilenameFormatTF.getText();
    if (filenameFormat == null || filenameFormat.indexOf('{') == -1) {
      filenameFormat = PropertiesManager.getDefaults().getProperty(JEmplodeProperties.FILENAME_FORMAT_TUNE_KEY);
    }
    propertiesManager.setProperty(JEmplodeProperties.FILENAME_FORMAT_TUNE_KEY, filenameFormat);

    String soupFilenameFormat = mySoupFilenameFormatTF.getText();
    if (soupFilenameFormat == null || soupFilenameFormat.indexOf('{') == -1) {
      soupFilenameFormat = PropertiesManager.getDefaults().getProperty(JEmplodeProperties.FILENAME_FORMAT_SOUP_TUNE_KEY);
    }
    propertiesManager.setProperty(JEmplodeProperties.FILENAME_FORMAT_SOUP_TUNE_KEY, soupFilenameFormat);

    propertiesManager.setProperty(JEmplodeProperties.DOWNLOAD_DIRECTORY_PROPERTY, myDownloadDirTF.getText());
    propertiesManager.setBooleanProperty(JEmplodeProperties.DOWNLOAD_FULL_PATH_PROPERTY, myDownloadFullPathCB.isSelected());

    propertiesManager.setBooleanProperty(JEmplodeProperties.WRITE_ID3v2_PROPERTY, myWriteID3v2CB.isSelected());
    propertiesManager.setBooleanProperty(JEmplodeProperties.PRESERVE_ID3v2_PROPERTY, myPreserveID3v2CB.isSelected());
    propertiesManager.setBooleanProperty(JEmplodeProperties.REMOVE_ID3v1_PROPERTY, myRemoveID3v1CB.isSelected());

    JEmplodeVersionTracker jemplodeVersionTracker = new JEmplodeVersionTracker();
    jemplodeVersionTracker.setAutoUpdate(myAutoUpdateJEmplodeCB.isSelected());
    jemplodeVersionTracker.setPromptsBeforeUpdate(myPromptBeforeJEmplodeUpdateCB.isSelected());

    propertiesManager.setBooleanProperty(JEmplodeProperties.USE_HIJACK_KEY, myUseHijackCB.isSelected());
    HijackVersionTracker hijackVersionTracker = new HijackVersionTracker();
    hijackVersionTracker.setAutoUpdate(myAutoUpdateHijackCB.isSelected());
    hijackVersionTracker.setPromptsBeforeUpdate(myPromptBeforeHijackUpdateCB.isSelected());
    hijackVersionTracker.setHijackURL(myHijackURLTF.getText());
    hijackVersionTracker.setMkVersion(myHijackMkVersionTF.getText());
    propertiesManager.setProperty(JEmplodeProperties.HIJACK_LOGIN_KEY, myHijackLoginTF.getText());
    propertiesManager.setProperty(JEmplodeProperties.HIJACK_PASSWORD_KEY, myHijackPasswordTF.getText());
    hijackVersionTracker.setHijackSaveLocation(myHijackSaveLocationTF.getText());
    propertiesManager.setBooleanProperty(JEmplodeProperties.USE_FAST_CONNECTIONS_PROPERTY, myUseFastConnectionCB.isSelected());
    propertiesManager.setBooleanProperty(JEmplodeProperties.ALLOW_UNKNOWN_TYPES_PROPERTY, myAllowUnknownTypesCB.isSelected());
    propertiesManager.setBooleanProperty(JEmplodeProperties.SHOW_ONLY_FAILED_IMPORTS_PROPERTY, myOnlyShowFailedImportsCB.isSelected());
    propertiesManager.setProperty(JEmplodeProperties.DO_NOT_BROADCAST_TO_PROPERTY, myDoNotBroadcastToTF.getText());
    propertiesManager.setBooleanProperty(JEmplodeProperties.DISABLE_SOUP_UPDATING_PROPERTY, myDisableSoupUpdatingCB.isSelected());
    propertiesManager.setBooleanProperty(JEmplodeProperties.AUTO_SELECT_PROPERTY, myAutoSelectCB.isSelected());

    propertiesManager.setBooleanProperty(JEmplodeProperties.RECURSIVE_COLORS_PROPERTY, myRecursiveColorsCB.isSelected());
    propertiesManager.setBooleanProperty(JEmplodeProperties.RECURSIVE_SOUP_COLORS_PROPERTY, myRecursiveSoupColorsCB.isSelected());
    NodeColorizer.reloadColorProperties();

    propertiesManager.setBooleanProperty(JEmplodeProperties.DEDUPLICATION_PROPERTY, myGlobalDedupeCB.isSelected());

    propertiesManager.setBooleanProperty(JEmplodeProperties.IMPORT_M3U_PROPERTY, myImportM3UCB.isSelected());

    propertiesManager.setBooleanProperty(JEmplodeProperties.UPDATE_TAGS_ON_DUPLICATES, myUpdateTagsOfDuplicateCB.isSelected());

    propertiesManager.setBooleanProperty(JEmplodeProperties.STRIP_ARTICLES_WHEN_COMPARING_KEY, myStripArticlesWhenComparingCB.isSelected());

    propertiesManager.setBooleanProperty(JEmplodeProperties.TONY_SELECTIONS_KEY, myTonySelectionsCB.isSelected());

    propertiesManager.setBooleanProperty(JEmplodeProperties.REBUILD_FROM_MEMORY_KEY, myRebuildFromMemoryCB.isSelected());

    boolean sortPlaylistsAboveTunes = mySortPlaylistsAboveTunesCB.isSelected();
    propertiesManager.setBooleanProperty(JEmplodeProperties.SORT_PLAYLISTS_ABOVE_TUNES, sortPlaylistsAboveTunes);
    PlayerDatabase playerDatabase = myContext.getPlayerDatabase();
    FIDPlaylist.setSortPlaylistsAboveTunes(playerDatabase, sortPlaylistsAboveTunes);

    propertiesManager.setProperty(JEmplodeProperties.LOOK_AND_FEEL_PROPERTY, myLookAndFeelTF.getText());
  }

  protected void fillInTextField(JTextField _textField, String _propertyValue) {
    unlistenTo(_textField);
    _textField.setText(_propertyValue);
    _textField.setCaretPosition(0);
    listenTo(_textField);
  }

  protected void fillInTextField(PropertiesManager _propertiesManager, JTextField _textField, String _propertyName) {
    unlistenTo(_textField);
    _textField.setText(_propertiesManager.getProperty(_propertyName));
    _textField.setCaretPosition(0);
    listenTo(_textField);
  }

  protected void fillInCheckBox(JCheckBox _checkBox, boolean _propertyValue) {
    unlistenTo(_checkBox);
    _checkBox.setSelected(_propertyValue);
    listenTo(_checkBox);
  }

  protected void fillInCheckBox(PropertiesManager _propertiesManager, JCheckBox _checkBox, String _propertyName) {
    unlistenTo(_checkBox);
    _checkBox.setSelected(_propertiesManager.getBooleanProperty(_propertyName));
    listenTo(_checkBox);
  }
}