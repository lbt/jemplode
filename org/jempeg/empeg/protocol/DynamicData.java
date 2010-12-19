/**
 * This file is licensed under the GPL.
 *
 * See the LICENSE0 file included in this release, or
 * http://www.opensource.org/licenses/gpl-license.html
 * for the details of the license.
 */
package org.jempeg.empeg.protocol;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jempeg.nodestore.DatabaseTags;
import org.jempeg.nodestore.NodeTags;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT16;
import com.inzyme.typeconv.UINT32;
import com.inzyme.util.Debug;

/**
 * Dynamic Data
 *
 * @author Mike Schrag
 * @version $Revision: 1.5 $
 */
public class DynamicData {
  private static final int DYNAMICDATA_VERSION = 1;

  private byte[] myUnused; /* spare bits - not used yet */
  private UINT16 myNormalisation; /* not used yet */
  private UINT16 myPlayCount; /* number of times played */

  /* Fields in 'bpm_data' */
  public static final int BPM_BPM_MASK = 0x000003ff; /* actual BPM multiplied by 4 */
  public static final int BPM_BIN_MASK = 0x0007fc00; /* see bpm_monitor.cpp */
  public static final int BPM_AMP_MASK = 0xfff80000; /* see bpm_monitor.cpp */

  /*
   * Last Time Played : time_t
   *
   * This is tricky, so pay attention:  The player has TZ set to GMT.
   * The user doesn't know this, and assumes that it's set to local time,
   * which would generally be PDT for most of our client base.
   *
   * Thus, when retrieving any times from the player, we must assume
   * that they're in local time.
   *
   * In short, treat the times on the player as local time.
   */
  private UINT32 myPlayLast; /* last time played */

  private UINT32 myBPMData; /* stuff needed for bpm & beat detection */
  private UINT32 mySkippedCount; /* number of times skipped */

  public DynamicData() {
    myUnused = new byte[4];
    myNormalisation = new UINT16();
    myPlayCount = new UINT16();
    myPlayLast = new UINT32();
    myBPMData = new UINT32();
    mySkippedCount = new UINT32();
  }

  public void write(LittleEndianOutputStream _outputStream) throws IOException {
    _outputStream.write(myUnused);
    myNormalisation.write(_outputStream);
    myPlayCount.write(_outputStream);
    myPlayLast.write(_outputStream);
    myBPMData.write(_outputStream);
    mySkippedCount.write(_outputStream);
  }

  public void read(LittleEndianInputStream _inputStream) throws IOException {
    _inputStream.read(myUnused);
    myNormalisation.read(_inputStream);
    myPlayCount.read(_inputStream);
    myPlayLast.read(_inputStream);
    myBPMData.read(_inputStream);
    mySkippedCount.read(_inputStream);
  }

  public int getDrive() {
    int drive = (myUnused[0] >> 7);
    return drive;
  }

  public void setDrive(int _drive) {
    _drive <<= 7;
    if (_drive == 1) {
      myUnused[0] |= _drive;
    }
    else {
      myUnused[0] &= ~_drive;
    }
  }

  public boolean isMarked() {
    boolean mark = ((myUnused[0] & 0x02) != 0);
    return mark;
  }

  public void setMarked(boolean _marked) {
    if (_marked) {
      myUnused[0] |= 0x02;
    }
    else {
      myUnused[0] &= ~0x02;
    }
  }

  public int getNormalisation() {
    return myNormalisation.getValue();
  }

  public void setNormalisation(int _normalisation) {
    myNormalisation.setValue(_normalisation);
  }

  public int getPlayCount() {
    return myPlayCount.getValue();
  }

  public void setPlayCount(int _playCount) {
    myPlayCount.setValue(_playCount);
  }

  public long getPlayLast() {
    return myPlayLast.getValue();
  }

  public void setPlayLast(long _playLast) {
    myPlayLast.setValue(_playLast);
  }

  public Calendar getPlayLastAsCalendar() {
    long playLast = myPlayLast.getValue() * 1000;
    Calendar playLastCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    playLastCal.setTime(new Date(playLast));
    return playLastCal;
  }

  public long getBPMData() {
    return myBPMData.getValue();
  }

  public void setBPMData(long _bpmData) {
    myBPMData.setValue(_bpmData);
  }

  public long getSkippedCount() {
    return mySkippedCount.getValue();
  }

  public void setSkippedCount(long _skippedCount) {
    mySkippedCount.setValue(_skippedCount);
  }

  public int getLength() {
    return myUnused.length + myNormalisation.getLength() + myPlayCount.getLength() + myPlayLast.getLength() + myBPMData.getLength() + mySkippedCount.getLength();
  }

  public int getVersion() {
    return DynamicData.DYNAMICDATA_VERSION;
  }

  public boolean checkVersion(long _version) {
    boolean legitVersion;
    if (_version != DynamicData.DYNAMICDATA_VERSION) {
      Debug.println(Debug.WARNING, "DynamicData version mismatch (received " + _version + ", but expected " + DynamicData.DYNAMICDATA_VERSION);
      legitVersion = false;
    }
    else {
      legitVersion = true;
    }
    return legitVersion;
  }

  public String toString() {
    return "[DynamicData: drive = " + getDrive() + "; mark = " + isMarked() + "; normalisation = " + myNormalisation + "; playCount = " + myPlayCount + "; playLast = " + DateFormat.getDateInstance().format(getPlayLastAsCalendar().getTime()) + "; bpmData = " + myBPMData + "; skippedCount = " + mySkippedCount + "]";
  }

  /**
   * Creates a DynamicData for this NodeTags. Some of the tags that are in this
   * object are "virtual" in that they do not actually appear as tags on the
   * Empeg. An example of this is the "marked" field. This data is modified at
   * play time, and is thus stored in a different location on the player.
   * 
   * @return a DynamicData for this NodeTags
   */
  public static DynamicData createDynamicData(NodeTags _nodeTags) {
    DynamicData data = new DynamicData();
    data.setDrive(_nodeTags.getIntValue(DatabaseTags.DRIVE_TAG, 0));
    data.setPlayCount(_nodeTags.getIntValue(DatabaseTags.PLAY_COUNT_TAG, 0));
    data.setSkippedCount(_nodeTags.getIntValue(DatabaseTags.SKIP_COUNT_TAG, 0));
    data.setPlayLast(_nodeTags.getLongValue(DatabaseTags.PLAY_LAST_TAG, 0));
    data.setMarked(_nodeTags.getBooleanValue(DatabaseTags.MARKED_TAG));
    return data;
  }

  /**
   * Loads the data from a DynamicData in this NodeTags.
   * 
   * @param _data
   *          the DynamicData to load into this NodeTags
   */
  public void fillInDynamicData(NodeTags _tags) {
    _tags.setIntValue(DatabaseTags.DRIVE_TAG, getDrive());
    _tags.setIntValue(DatabaseTags.PLAY_COUNT_TAG, getPlayCount());
    _tags.setLongValue(DatabaseTags.SKIP_COUNT_TAG, getSkippedCount());
    _tags.setLongValue(DatabaseTags.PLAY_LAST_TAG, getPlayLast());
    _tags.setYesNoValue(DatabaseTags.MARKED_TAG, isMarked());
  }

  /**
   * Removes the DynamicData from thie NodeTags. This is called on a clone of
   * this NodeTags prior to synchronising, so dynamic data does not end up on
   * the player as tags.
   */
  public static void removeDynamicData(NodeTags _tags) {
    _tags.setValue(DatabaseTags.DRIVE_TAG, null);
    _tags.setValue(DatabaseTags.PLAY_COUNT_TAG, null);
    _tags.setValue(DatabaseTags.SKIP_COUNT_TAG, null);
    _tags.setValue(DatabaseTags.PLAY_LAST_TAG, null);
    _tags.setValue(DatabaseTags.MARKED_TAG, null);
  }

}