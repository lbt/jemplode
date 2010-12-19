/* JFontChooser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.ozten.font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ozten.util.SwingUtil;

public class JFontChooser extends JComponent
{
    private String[] sizes;
    private static String[] fonts;
    private JDialog jd;
    private JPanel p;
    private JLabel font;
    private JList fontChoices;
    private JList sizeChoices;
    private JScrollPane fontChoiceScroll;
    private JScrollPane previewScroll;
    private JScrollPane sizeScroll;
    private JTextArea preview;
    private static JButton apply;
    private static JButton cancel;
    private Font fPreview;
    private JCheckBox checkBold;
    private JCheckBox checkItalic;
    
    static {
	try {
	    fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames();
	} catch (Throwable t) {
	    fonts = Toolkit.getDefaultToolkit().getFontList();
	}
    }
    
    public JFontChooser() {
	sizes = new String[] { "8", "9", "10", "11", "12", "13", "14", "16",
			       "18", "20", "24", "26", "28", "32", "36", "40",
			       "48", "56", "64", "72" };
	doJFCLayout();
	setPreviewText("How does this font fit?");
	setPreviewFont(getAvailableFont());
    }
    
    public JFontChooser(Font init) {
	sizes = new String[] { "8", "9", "10", "11", "12", "13", "14", "16",
			       "18", "20", "24", "26", "28", "32", "36", "40",
			       "48", "56", "64", "72" };
	setPreviewFont(init);
	setPreviewText("How does this font fit?");
    }
    
    public JFontChooser(Font init, String sampleText) {
	sizes = new String[] { "8", "9", "10", "11", "12", "13", "14", "16",
			       "18", "20", "24", "26", "28", "32", "36", "40",
			       "48", "56", "64", "72" };
	setPreviewFont(init);
	setPreviewText(sampleText);
    }
    
    public static Font showDialog(Component component, String title,
				  String sampleText, Font initial) {
	final JFontChooser jfc = new JFontChooser(initial, sampleText);
	jfc.doJFCLayout();
	jfc.setJDialog(initDialog(component, title, true));
	jfc.getJDialog().getContentPane().add(jfc, "Center");
	JPanel buttonsOuter = new JPanel();
	buttonsOuter.setLayout(new BorderLayout());
	JPanel buttons = new JPanel(new GridLayout(0, 1));
	buttons.add(apply = new JButton("OK"));
	apply.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		jfc.setPreviewFont(jfc.buildFont());
		if (jfc.getJDialog() != null) {
		    jfc.getJDialog().dispose();
		    jfc.getJDialog().setVisible(false);
		}
	    }
	});
	buttons.add(cancel = new JButton("Cancel"));
	cancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		jfc.setPreviewFont(null);
		if (jfc.getJDialog() != null) {
		    jfc.getJDialog().dispose();
		    jfc.getJDialog().setVisible(false);
		}
	    }
	});
	buttonsOuter.add(buttons, "North");
	jfc.getJDialog().getContentPane().add(buttonsOuter, "East");
	jfc.getJDialog().pack();
	jfc.getJDialog().setResizable(false);
	jfc.getJDialog().setVisible(true);
	return jfc.getPreviewFont();
    }
    
    private static JDialog initDialog(Component c, String title,
				      boolean modal) {
	JDialog rv = null;
	Frame f = SwingUtil.getFrame(c);
	if (f != null)
	    rv = new JDialog(f, title, modal);
	else
	    rv = new JDialog();
	return rv;
    }
    
    public static Font getAvailableFont() {
	Font rv = null;
	String[] sysFonts = getSystemFonts();
	rv = new Font(sysFonts[0], 0, 18);
	return rv;
    }
    
    private void doJFCLayout() {
	setLayout(new BorderLayout());
	p = new JPanel(new FlowLayout());
	add(p, "Center");
	fontChoices = new JList(fonts);
	boolean initIsValid = false;
	if (getPreviewFont() != null) {
	    String[] fontsTemp = getSystemFonts();
	    for (int i = 0; i < fontsTemp.length; i++) {
		if (fontsTemp[i]
			.equalsIgnoreCase(getPreviewFont().getFontName())) {
		    fontChoices.setSelectedIndex(i);
		    initIsValid = true;
		}
	    }
	}
	if (!initIsValid)
	    fontChoices.setSelectedIndex(0);
	fontChoices.addListSelectionListener(new ListSelectionListener() {
	    public void valueChanged(ListSelectionEvent ae) {
		JFontChooser.this.updateGUI();
	    }
	});
	fontChoiceScroll = new JScrollPane(fontChoices);
	fontChoiceScroll.setPreferredSize(new Dimension(200, 150));
	p.add(fontChoiceScroll);
	sizeChoices = new JList(getFontSizes());
	initIsValid = false;
	if (getPreviewFont() != null) {
	    for (int j = 0; j < getFontSizes().length; j++) {
		if (Integer.parseInt(sizes[j]) == getPreviewFont().getSize()) {
		    sizeChoices.setSelectedIndex(j);
		    initIsValid = true;
		}
	    }
	}
	if (!initIsValid)
	    sizeChoices.setSelectedIndex(12);
	sizeChoices.addListSelectionListener(new ListSelectionListener() {
	    public void valueChanged(ListSelectionEvent ae) {
		JFontChooser.this.updateGUI();
	    }
	});
	sizeScroll = new JScrollPane(sizeChoices);
	sizeScroll.setPreferredSize(new Dimension(48, 150));
	p.add(sizeScroll);
	JPanel stylePanel = new JPanel(new GridLayout(1, 2));
	checkBold = new JCheckBox("Bold");
	checkBold.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		JFontChooser.this.updateGUI();
	    }
	});
	stylePanel.add(checkBold);
	checkItalic = new JCheckBox("Italic");
	checkItalic.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		JFontChooser.this.updateGUI();
	    }
	});
	stylePanel.add(checkItalic);
	JPanel upperRight = new JPanel(new GridLayout(3, 1, 5, 3));
	upperRight.add(stylePanel);
	JPanel status = new JPanel(new GridLayout(2, 1));
	JLabel jl = new JLabel("Current Selection:");
	status.add(jl);
	font = new JLabel();
	font.setForeground(Color.black);
	status.add(font);
	upperRight.add(status);
	p.add(upperRight);
	previewScroll = new JScrollPane(preview);
	previewScroll.setPreferredSize(new Dimension(400, 80));
	add(previewScroll, "South");
	updateGUI();
    }
    
    private void updateStatus() {
	Font tmp = buildFont();
	String name;
	if (tmp.getName().length() > 11)
	    name = tmp.getName().substring(0, 12);
	else
	    name = tmp.getName();
	font.setText(name + " " + tmp.getSize() + "pt.");
    }
    
    private Font buildFont() {
	Font rv = null;
	String s = (String) sizeChoices.getSelectedValue();
	if (s != null) {
	    int i = Integer.parseInt(s);
	    int style = 0;
	    if (checkBold.isSelected() && checkItalic.isSelected()) {
		style = 1;
		style |= 0x2;
	    } else if (checkBold.isSelected())
		style = 1;
	    else if (checkItalic.isSelected())
		style = 2;
	    return new Font((String) fontChoices.getSelectedValue(), style, i);
	}
	return rv;
    }
    
    private void updateGUI() {
	Font fnew = buildFont();
	if (fnew != null && getPreviewText() != null) {
	    getPreviewText().setFont(fnew);
	    if (fnew != null)
		setPreviewFont(fnew);
	    updateStatus();
	}
    }
    
    public static String[] getSystemFonts() {
	return fonts;
    }
    
    public String[] getFontSizes() {
	return sizes;
    }
    
    public JDialog getJDialog() {
	return jd;
    }
    
    public void setJDialog(JDialog d) {
	jd = d;
    }
    
    public JTextArea getPreviewText() {
	return preview;
    }
    
    public void setPreviewText(String s) {
	preview = new JTextArea(s);
    }
    
    public Font getPreviewFont() {
	return fPreview;
    }
    
    public void setPreviewFont(Font f) {
	fPreview = f;
    }
    
    public static Font showDialog(Component c, String title,
				  String sampleText) {
	Font rv = showDialog(c, title, sampleText, c.getFont());
	return rv;
    }
    
    public static Font showDialog(Component c, String title) {
	Font rv = showDialog(c, title, "How about this font?", c.getFont());
	return rv;
    }
    
    public static Font showDialog(Component c) {
	Font rv = showDialog(c, "Choose a font", "How about this font?",
			     c.getFont());
	return rv;
    }
    
    public static void main(String[] args) {
	int DEMO_SHOWDIALOG = 666;
	int DEMO_ASCOMPONENT = 667;
	int test_mode = 666;
	final JFrame test = new JFrame("Testing JFontChooser");
	Container c = test.getContentPane();
	switch (test_mode) {
	case 666: {
	    final JLabel l
		= new JLabel("Well now, what font do we have here?");
	    c.add(l, "Center");
	    JButton testButton = new JButton("Test");
	    c.add(testButton, "North");
	    testButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    Font f = showDialog(test, "Chooser a font",
					"Fishing for Bobby Searcher",
					getAvailableFont());
		    if (f != null) {
			l.setFont(f);
			l.repaint();
		    } else
			System.out.println("f was " + f);
		}
	    });
	    JButton quitButton = new JButton("Quit Testing");
	    quitButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    test.dispose();
		    test.setVisible(false);
		    System.exit(0);
		}
	    });
	    c.add(quitButton, "South");
	    test.setDefaultCloseOperation(3);
	    test.setSize(c.getPreferredSize());
	    break;
	}
	case 667: {
	    final JFontChooser jfc
		= new JFontChooser(getAvailableFont(), "Testosterone");
	    jfc.doJFCLayout();
	    c.add(jfc, "Center");
	    JPanel bottom = new JPanel(new FlowLayout());
	    JButton bApply = new JButton("Apply");
	    JButton bCancel = new JButton("Cancel");
	    bottom.add(bApply);
	    bApply.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    System.out.println(jfc.getPreviewFont().toString());
		}
	    });
	    bottom.add(bCancel);
	    bCancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    System.out.println(jfc.getPreviewFont().toString());
		    test.dispose();
		    test.setVisible(false);
		    System.exit(0);
		}
	    });
	    c.add(bottom, "South");
	    test.setDefaultCloseOperation(3);
	    test.setSize(jfc.getPreferredSize());
	    break;
	}
	}
	test.setVisible(true);
    }
}
