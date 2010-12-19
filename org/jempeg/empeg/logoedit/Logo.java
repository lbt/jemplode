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
package org.jempeg.empeg.logoedit;

import java.awt.Image;

/**
* Encapsulates a home image and car image for
* sending and receiving to/from the Empeg.
*
* @author Mike Schrag
*/
public class Logo {
  public static final String TYPE_EMPEG_NOCUSTOM = "emp ";
  public static final String TYPE_EMPEG_CUSTOM   = "empg";
  public static final String TYPE_RIO_NOCUSTOM   = "rio ";
  public static final String TYPE_RIO_CUSTOM     = "rioc";

  private String myType;
  private Image myCarImage;
  private Image myHomeImage;
  
  /**
  * Constructs a new Logo
  */
  public Logo() {
  }
  
  /**
  * Constructs a new Logo.
  * 
  * @param _type one of "emp ", "empg", "rio ", "rioc"
  * @param _carImage the car image to use
  * @param _homeImage the home image to use
  */
  public Logo(String _type, Image _carImage, Image _homeImage) {
    myType = _type;
    myCarImage = _carImage;
    myHomeImage = _homeImage;
  }
  
  /**
  * Returns the type of this Logo.
  *
  * @returns the type of this Logo
  */
  public String getType() {
    return myType;
  }
  
  /**
  * Sets the type of this Logo.
  *
  * @param _type the type of this Logo
  */
  public void setType(String _type) {
    myType = _type;
  }

  /**
  * Returns the Car Image for this Logo.
  *
  * @returns the Car Image for this Logo
  */
  public Image getCarImage() {
    return myCarImage;
  }
  
  /**
  * Returns the Home Image for this Logo.
  *
  * @returns the Home Image for this Logo
  */
  public Image getHomeImage() {
    return myHomeImage;
  }
}
