/* PixelQueue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;

class PixelQueue
{
    private int myLength;
    public Pixel head;
    public Pixel tail;
    
    class Pixel
    {
	public int x;
	public int y;
	public Pixel next;
	
	Pixel(int _x, int _y) {
	    x = _x;
	    y = _y;
	}
    }
    
    public PixelQueue() {
	/* empty */
    }
    
    public void enqueue(int _x, int _y) {
	Pixel item = new Pixel(_x, _y);
	if (isEmpty()) {
	    head = tail = item;
	    myLength = 1;
	} else {
	    tail.next = item;
	    tail = tail.next;
	    myLength++;
	}
    }
    
    public Pixel dequeue() {
	Pixel item;
	if (isEmpty())
	    item = null;
	else {
	    item = head;
	    head = head.next;
	    item.next = null;
	    if (tail == item)
		tail = null;
	    myLength--;
	}
	return item;
    }
    
    public boolean isEmpty() {
	if (myLength == 0)
	    return true;
	return false;
    }
}
