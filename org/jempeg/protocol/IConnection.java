/* IConnection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;

public interface IConnection
{
    public int getPacketSize();
    
    public LittleEndianInputStream getInputStream() throws ConnectionException;
    
    public LittleEndianOutputStream getOutputStream()
	throws ConnectionException;
    
    public IConnection getFastConnection() throws ConnectionException;
    
    public void open() throws ConnectionException;
    
    public void close() throws ConnectionException;
    
    public void pause() throws ConnectionException;
    
    public void unpause() throws ConnectionException;
    
    public void flushReceiveBuffer() throws ConnectionException;
    
    public void setTimeout(long l) throws ConnectionException;
    
    public boolean isOpen();
}
