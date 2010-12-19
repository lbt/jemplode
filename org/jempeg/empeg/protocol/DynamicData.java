/* DynamicData - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT16;
import com.inzyme.typeconv.UINT32;
import com.inzyme.util.Debug;

import org.jempeg.nodestore.NodeTags;

public class DynamicData
{
    private static final int DYNAMICDATA_VERSION = 1;
    private byte[] myUnused = new byte[4];
    private UINT16 myNormalisation = new UINT16();
    private UINT16 myPlayCount = new UINT16();
    public static final int BPM_BPM_MASK = 1023;
    public static final int BPM_BIN_MASK = 523264;
    public static final int BPM_AMP_MASK = -524288;
    private UINT32 myPlayLast = new UINT32();
    private UINT32 myBPMData = new UINT32();
    private UINT32 mySkippedCount = new UINT32();
    
    public void write(LittleEndianOutputStream _outputStream)
	throws IOException {
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
	int drive = myUnused[0] >> 7;
	return drive;
    }
    
    public void setDrive(int _drive) {
	_drive <<= 7;
	if (_drive == 1)
	    myUnused[0] |= _drive;
	else
	    myUnused[0] &= _drive ^ 0xffffffff;
    }
    
    public boolean isMarked() {
	boolean mark = (myUnused[0] & 0x2) != 0;
	return mark;
    }
    
    public void setMarked(boolean _marked) {
	if (_marked)
	    myUnused[0] |= 0x2;
	else
	    myUnused[0] &= ~0x2;
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
	long playLast = myPlayLast.getValue() * 1000L;
	Calendar playLastCal
	    = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
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
	return (myUnused.length + myNormalisation.getLength()
		+ myPlayCount.getLength() + myPlayLast.getLength()
		+ myBPMData.getLength() + mySkippedCount.getLength());
    }
    
    public int getVersion() {
	return 1;
    }
    
    public boolean checkVersion(long _version) {
	boolean legitVersion;
	if (_version != 1L) {
	    Debug.println(8, ("DynamicData version mismatch (received "
			      + _version + ", but expected " + 1));
	    legitVersion = false;
	} else
	    legitVersion = true;
	return legitVersion;
    }
    
    public String toString() {
	return ("[DynamicData: drive = " + getDrive() + "; mark = "
		+ isMarked() + "; normalisation = " + myNormalisation
		+ "; playCount = " + myPlayCount + "; playLast = "
		+ DateFormat.getDateInstance()
		      .format(getPlayLastAsCalendar().getTime())
		+ "; bpmData = " + myBPMData + "; skippedCount = "
		+ mySkippedCount + "]");
    }
    
    public static DynamicData createDynamicData(NodeTags _nodeTags) {
	DynamicData data = new DynamicData();
	data.setDrive(_nodeTags.getIntValue("drive", 0));
	data.setPlayCount(_nodeTags.getIntValue("play_count", 0));
	data.setSkippedCount((long) _nodeTags.getIntValue("skip_count", 0));
	data.setPlayLast(_nodeTags.getLongValue("play_last", 0L));
	data.setMarked(_nodeTags.getBooleanValue("marked"));
	return data;
    }
    
    public void fillInDynamicData(NodeTags _tags) {
	_tags.setIntValue("drive", getDrive());
	_tags.setIntValue("play_count", getPlayCount());
	_tags.setLongValue("skip_count", getSkippedCount());
	_tags.setLongValue("play_last", getPlayLast());
	_tags.setYesNoValue("marked", isMarked());
    }
    
    public static void removeDynamicData(NodeTags _tags) {
	_tags.setValue("drive", null);
	_tags.setValue("play_count", null);
	_tags.setValue("skip_count", null);
	_tags.setValue("play_last", null);
	_tags.setValue("marked", null);
    }
}
