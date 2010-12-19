package com.inzyme.model;

import java.util.Iterator;

import com.inzyme.exception.MethodNotImplementedException;

public class LongLinkedList {
  private ListItem myFirst;
  private ListItem myLast;
  private int mySize;

  public LongLinkedList() {
  }

  public void add(long _value) {
    if (myFirst == null) {
      myFirst = new ListItem(_value);
      myLast = myFirst;
    }
    else {
      ListItem next = new ListItem(_value);
      myLast.myNext = next;
      next.myPrev = myLast;
      myLast = next;
    }
    mySize ++;
  }

  public int size() {
    return mySize;
  }

  public ListItem getFirst() {
    return myFirst;
  }

  public ListItem removeFirst() {
    ListItem temp = myFirst;
    myFirst = myFirst.myNext;
    if (myFirst != null) {
      myFirst.myPrev = null;
    }
    if (myLast == temp) {
      myLast = null;
    }
    mySize --;
    return temp;
  }

  public long getLast() {
    if (myLast != null) {
      return myLast.myValue;
    }
    else {
      return -1;
    }
  }

  public boolean contains(long _value) {
    ListItem item = myFirst;
    while (item != null) {
      if (item.myValue == _value) {
        return true;
      }
      item = item.myNext;
    }
    return false;
  }

  public boolean remove(long _value) {
    ListItem item = myFirst;
    while (item != null) {
      if (item.myValue == _value) {
        if (item == myFirst) {
          removeFirst();
        }
        else {
          item.myPrev.myNext = item.myNext;
          if (item == myLast) {
            myLast = item.myPrev;
          }
          else {
            item.myNext.myPrev = item.myPrev;
          }
          mySize --;
        }
        return true;
      }
      item = item.myNext;
    }
    return false;
  }

  public void clear() {
    myFirst = null;
    myLast = null;
    mySize = 0;
  }

  public long[] toArray() {
    long[] data = new long[mySize];
    ListItem next = myFirst;
    int i = 0;
    while (next != null) {
      data[i ++] = next.myValue;
      next = next.myNext;
    }
    return data;
  }

  public Iterator iterator() {
    return new LongLinkedListIterator();
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    ListItem item = myFirst;
    while (item != null) {
      buf.append(item.myValue).append(' ');
      item = item.myNext;
    }
    return buf.toString();
  }

  public static class ListItem implements Comparable {
    public long myValue;

    public ListItem myNext;
    public ListItem myPrev;

    public ListItem(long _value) {
      this.myValue = _value;
    }

    public long getValue() {
      return myValue;
    }

    public ListItem getNext() {
      return myNext;
    }

    public int compareTo(Object _obj) {
      final long ol = ((ListItem) _obj).myValue;
      if (ol == myValue) {
        return 0;
      }
      else if (myValue > ol) {
        return 1;
      }
      else {
        return -1;
      }
    }
  }

  private final class LongLinkedListIterator implements Iterator {
    private ListItem myItem = myFirst;

    public boolean hasNext() {
      return myItem != null;
    }

    public Object next() {
      Object temp = myItem;
      myItem = myItem.myNext;
      return temp;
    }

    public void remove() {
      throw new MethodNotImplementedException();
    }
  }

  /*
   public static void main(String[] args) {
   LongLinkedList ll = new LongLinkedList();
   for (int i = 0; i < 10; i ++) {
   ll.add((long)i);
   }

   System.out.println("LongLinkedList.main: Added ...");
   System.out.println("LongLinkedList.main: size = " + ll.size());
   Iterator parentPlaylistFIDIter = ll.iterator();
   while (parentPlaylistFIDIter.hasNext()) {
   LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
   System.out.println("LongLinkedList.main: " + listItem.getValue());
   }
   
   System.out.println("LongLinkedList.main: Removing 5 ...");
   ll.remove(5);
   System.out.println("LongLinkedList.main: size = " + ll.size());
   parentPlaylistFIDIter = ll.iterator();
   while (parentPlaylistFIDIter.hasNext()) {
   LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
   System.out.println("LongLinkedList.main: " + listItem.getValue());
   }
   
   System.out.println("LongLinkedList.main: Removing 9 ...");
   ll.remove(9);
   System.out.println("LongLinkedList.main: size = " + ll.size());
   parentPlaylistFIDIter = ll.iterator();
   while (parentPlaylistFIDIter.hasNext()) {
   LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
   System.out.println("LongLinkedList.main: " + listItem.getValue());
   }
   
   System.out.println("LongLinkedList.main: Removing 0 ...");
   ll.remove(0);
   System.out.println("LongLinkedList.main: size = " + ll.size());
   parentPlaylistFIDIter = ll.iterator();
   while (parentPlaylistFIDIter.hasNext()) {
   LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
   System.out.println("LongLinkedList.main: " + listItem.getValue());
   }
   
   System.out.println("LongLinkedList.main: Removing 0 Again ...");
   ll.remove(0);
   System.out.println("LongLinkedList.main: size = " + ll.size());
   parentPlaylistFIDIter = ll.iterator();
   while (parentPlaylistFIDIter.hasNext()) {
   LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
   System.out.println("LongLinkedList.main: " + listItem.getValue());
   }
   
   System.out.println("LongLinkedList.main: Removing 1 ...");
   ll.remove(1);
   System.out.println("LongLinkedList.main: size = " + ll.size());
   parentPlaylistFIDIter = ll.iterator();
   while (parentPlaylistFIDIter.hasNext()) {
   LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
   System.out.println("LongLinkedList.main: " + listItem.getValue());
   }
   
   System.out.println("LongLinkedList.main: Adding 9 ...");
   ll.add(9);
   System.out.println("LongLinkedList.main: size = " + ll.size());
   parentPlaylistFIDIter = ll.iterator();
   while (parentPlaylistFIDIter.hasNext()) {
   LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
   System.out.println("LongLinkedList.main: " + listItem.getValue());
   }

   for (int i = 0; i < 11; i ++) {
   System.out.println("LongLinkedList.main: " + i + "> " + ll.contains((long)i));
   }
   
   System.out.println("LongLinkedList.main: Clearing ...");
   ll.clear();
   System.out.println("LongLinkedList.main: size = " + ll.size());
   parentPlaylistFIDIter = ll.iterator();
   while (parentPlaylistFIDIter.hasNext()) {
   LongLinkedList.ListItem listItem = (LongLinkedList.ListItem) parentPlaylistFIDIter.next();
   System.out.println("LongLinkedList.main: " + listItem.getValue());
   }
   }
   */
}