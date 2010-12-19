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
  
/**
* A Queue of pixel data for flood filling.
*
* @author Mike Schrag
*/
class PixelQueue {
  private int myLength;
  public Pixel head;
  public Pixel tail;

  public PixelQueue() {
  }

  public void enqueue(int _x, int _y) {
    Pixel item = new Pixel(_x, _y);
    if (isEmpty()) {
      head = tail = item;
      myLength = 1;
    } else {
      tail.next = item;
      tail = tail.next;
      myLength ++;
    }
  }

  public Pixel dequeue() {
    Pixel item;
    if (isEmpty()) {
      item = null;
    } else {
      item = head;
      head = head.next;
      item.next = null;
      if (tail == item) {
        tail = null;
      }
      myLength --;
    }
    return item;
  }

  public boolean isEmpty() {
    return (myLength == 0);
  }

  class Pixel {
    public int x;
    public int y;
    public Pixel next;
  
    Pixel(int _x, int _y) {
      x = _x;
      y = _y;
    }
  }
}

