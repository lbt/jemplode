package org.jempeg.manager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * JTriState button is a quick implementation of a 
 * button with three states (nothing, checked, and X'd).  It
 * doesn't do plaf, it barely does events (only updates in response
 * to doClick, which is called by ButtonListMouseListener).
 * 
 * @author Mike Schrag
 * @version $Revision: 1.1 $
 */
public class JTriStateButton extends AbstractButton {
	private int myTriState;
	private JLabel myLabel;
	private boolean myActLikeCheckBox;
	
	public JTriStateButton(String _name) {
		setOpaque(true);
		setModel(new DefaultButtonModel());
		setLayout(new BorderLayout());
		
		myLabel = new JLabel();
		myLabel.setOpaque(true);
		myLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		myLabel.setIconTextGap(10);
		myLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		setText(_name);
		
		add(myLabel, BorderLayout.CENTER);
		updateTriState();
		setEnabled(true);
		addMouseListener(new TriStateListener());
	}
	
	public void setActLikeCheckBox(boolean _actLikeCheckBox) {
		myActLikeCheckBox = _actLikeCheckBox;
		updateTriState();
	}
	
	public void setText(String _name) {
		super.setText(_name);
		myLabel.setText(_name);
	}
	
	public void setBackground(Color _background) {
		myLabel.setBackground(_background);
	}
	
	public void doClick() {
		cycleTriState();
		super.doClick();
	}
	
	public void setTriState(int _triState) {
		myTriState = _triState;
		updateTriState();
	}
	
	public int getTriState() {
		return myTriState;
	}
	
	public void cycleTriState() {
		if (myActLikeCheckBox) {
			if (myTriState == 1) {
				myTriState = 2;
			} else {
				myTriState = 1;
			}
		} else {
			myTriState = (myTriState + 1) % 3;
		}
		updateTriState();
	}
	
	public void updateTriState() {
		if (myActLikeCheckBox && myTriState == 0) {
			myTriState = 2;
		}
		switch (myTriState) {
			case 0:
				myLabel.setIcon(new NoIcon());
				break;
			case 1:
				myLabel.setIcon(new CheckIcon());
				break;
			case 2:
				if (myActLikeCheckBox) {
					myLabel.setIcon(new NoIcon());
				} else {
					myLabel.setIcon(new XIcon());
				}
				break;
		}
		
	}

	protected class TriStateIcon implements Icon {
		public int getIconHeight() {
			return 10;
		}

		public int getIconWidth() {
			return 10;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(c.getForeground());
			g.drawRect(x, y, getIconWidth(), getIconHeight());
		}
	}
	
	protected class NoIcon extends TriStateIcon {
		public void paintIcon(Component c, Graphics g, int x, int y) {
			super.paintIcon(c, g, x, y);		
		}
	}
	
	protected class CheckIcon extends TriStateIcon {
		public void paintIcon(Component c, Graphics g, int x, int y) {
			super.paintIcon(c, g, x, y);
			int margin = 2;
			g.drawLine(x + margin, y + getIconHeight() / 2, x + getIconWidth() / 2, y + getIconHeight() - margin);
			g.drawLine(x + getIconWidth() / 2, y + getIconHeight() - margin, x + getIconWidth() - margin, y + margin);
		}
	}
	
	protected class XIcon extends TriStateIcon {
		public void paintIcon(Component c, Graphics g, int x, int y) {
			super.paintIcon(c, g, x, y);
			g.drawLine(x, y, x + getIconWidth(), y + getIconHeight());
			g.drawLine(x, y + getIconHeight(), x + getIconWidth(), y);
		}
	}
	
	protected class TriStateListener extends MouseAdapter {
		private boolean myPressed;
		private boolean myEntered;
		
		public void mousePressed(MouseEvent e) {
			myPressed = true;
		}

		public void mouseReleased(MouseEvent _event) {
			if (myEntered && myPressed) {
				doClick();
			}
			myPressed = false;
		}
		
		public void mouseEntered(MouseEvent e) {
			myEntered = true;
		}
		
		public void mouseExited(MouseEvent e) {
			myEntered = false;
		}
	}
	
	public static void main(String[] _args) {
		JFrame jf = new JFrame("Test");
		JTriStateButton button = new JTriStateButton("Test");
		button.setActLikeCheckBox(true);
		jf.getContentPane().add(button);
		jf.pack();
		jf.show();
	}
}
