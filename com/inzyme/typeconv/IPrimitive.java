/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package com.inzyme.typeconv;

import java.io.IOException;

/**
* This interface is implemented by any
* primitive type.  This exists to abstract
* the reading/writing/crcing of the Empeg
* primitive types.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/ 
public interface IPrimitive {
  /**
  * Updates the given CRC with the value
  * of this primitive type.
  *
  * @param _crc the CRC16 to update
  */
  public void updateCRC(CRC16 _crc);

  /**
  * Reads the value of this primitive from
  * the given InputStream.
  *
  * @param _inputStream the input stream to read from
  * @throws IOException if the primitive cannot be read from the stream
  */
  public void read(LittleEndianInputStream _inputStream) throws IOException;

  /**
  * Writes the value of this primitive to
  * the given OutputStream.
  *
  * @param _outputStream the output stream to write to
  * @throws IOException if the primitive cannot be written to the stream
  */
  public void write(LittleEndianOutputStream _outputStream) throws IOException;

  /**
  * Returns the length of this primitive (in bytes).
  *
  * @returns the length of this primitive (in bytes)
  */
  public int getLength();
}
