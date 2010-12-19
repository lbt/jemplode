package com.inzyme.tree;

import javax.swing.tree.MutableTreeNode;

import com.inzyme.container.IMutableTypeContainer;

/**
 * IContainerTreeNode is the interface that is implemented
 * by any non-leaf tree node.  This may or may not be a playlist
 * that is actually on the Empeg (i.e. soup)
 * 
 * @author Mike Schrag
 */
public interface IContainerTreeNode extends IMutableTypeContainer, MutableTreeNode {
}
