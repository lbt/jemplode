package com.ozten.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SwingUtil {

  /**
  *  Walks through a components heirarcy and tries to find top level<br>
  * <code>java.awt.Frame</code> object.
  * @param c - the component
  * @return Frame - the frame it is living in, or null.
  */
  public static Frame getFrame(Component c){
    Frame rv = null;
    if(c instanceof Frame)
      rv = (Frame)c;
    Container test = null;
    Container parent = c.getParent();
    if(parent == null) // Component wasn't in a GUI
	return rv;

    while(! (parent instanceof Frame) ){

      test = parent.getParent();
      if(test == null)// Component wasn't in a GUI
	return rv;
      parent = test;

    }

    if(parent instanceof Frame)
	rv = (Frame)parent;


    return rv;
  }

  public static void testGetFrame(){
    JFrame jf = new JFrame("Testing");
    JButton jb = new JButton("Fishing for Bobby Searcher");
    JButton jb2 = new JButton("Neigh");
    jf.getContentPane().add(jb, BorderLayout.CENTER);

//    jf.setVisible(true);

    Frame f = SwingUtil.getFrame(jb);
    if(f instanceof JFrame)
      System.out.println("PASSED");
    else
      System.out.println("FAILED");

    Frame f2 = SwingUtil.getFrame(jb2);
    if(f2 == null)
      System.out.println("PASSED");
    else
      System.out.println("FAILED");

    Frame f3 = new Frame("ANother Test");
    JPanel jp = new JPanel();
    JLabel jl = new JLabel("If you can read this, your testing to much");
    
    f3.add(jp);
    jp.add(jl);

//    f3.setVisible(true);

    Frame f4 = SwingUtil.getFrame(jl);
    if(f4 instanceof Frame)
      System.out.println("PASSED");
    else
      System.out.println("FAILED");
  }

  public static void main(String[] args){
    SwingUtil.testGetFrame();
  }
}