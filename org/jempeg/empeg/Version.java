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
package org.jempeg.empeg;

/**
* The file containing version information for the jempeg.empeg package 
* (and its descendents).
*
* @author Daniel M. Zimmerman
* @version $Revision: 1.15 $
**/

public class Version
{
  // Static Fields
  
  /**
   * The version string of the jempeg.empeg package. Examples of version
   * strings are 1.0b1, or 2.1. 
   **/
  
  public static String VERSION_STRING = "69";
  
  
  /**
   * The lowest version of the Empeg software supported by this
   * version of jempeg.empeg. This is a String; it will typically only
   * be used for presentation in an informational dialog.
   **/
  
  public static String MIN_EMPEG_VERSION = "2.0";
  
  
  /**
   * The highest version of the Empeg software supported by this
   * version of jempeg.empeg. This is a String; it will typically only
   * be used for presentation in an informational dialog.
   **/
  
  public static String MAX_EMPEG_VERSION = "3.01";
}
