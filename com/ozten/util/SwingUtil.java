/* SwingUtil - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.ozten.util;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SwingUtil
{
    public static Frame getFrame(Component c) {
	Frame rv = null;
	if (c instanceof Frame)
	    rv = (Frame) c;
	Container test = null;
	Container parent = c.getParent();
	if (parent == null)
	    return rv;
	for (/**/; !(parent instanceof Frame); parent = test) {
	    test = parent.getParent();
	    if (test == null)
		return rv;
	}
	if (parent instanceof Frame)
	    rv = (Frame) parent;
	return rv;
    }
    
    public static void testGetFrame() {
	JFrame jf = new JFrame("Testing");
	JButton jb = new JButton("Fishing for Bobby Searcher");
	JButton jb2 = new JButton("Neigh");
	jf.getContentPane().add(jb, "Center");
	Frame f = getFrame(jb);
	if (f instanceof JFrame)
	    System.out.println("PASSED");
	else
	    System.out.println("FAILED");
	Frame f2 = getFrame(jb2);
	if (f2 == null)
	    System.out.println("PASSED");
	else
	    System.out.println("FAILED");
	Frame f3 = new Frame("ANother Test");
	JPanel jp = new JPanel();
	JLabel jl = new JLabel("If you can read this, your testing to much");
	f3.add(jp);
	jp.add(jl);
	Frame f4 = getFrame(jl);
	if (f4 instanceof Frame)
	    System.out.println("PASSED");
	else
	    System.out.println("FAILED");
    }
    
    public static void main(String[] args) {
	testGetFrame();
    }
}
