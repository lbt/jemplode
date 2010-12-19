package com.inzyme.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

public class BusyGlassPanel extends JPanel {
  public BusyGlassPanel() {
    super.setOpaque(false);
    super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    //Suck up them events!!!
    super.addKeyListener(new KeyAdapter() {
    });
    super.addMouseListener(new MouseAdapter() {
    });
    super.addMouseMotionListener(new MouseMotionAdapter() {
    });
  }

  public static final Color COLOR_WASH = new Color(64, 64, 64, 32);

  public final void paintComponent(Graphics p_graphics) {
    Dimension l_size = super.getSize();
    p_graphics.setColor(COLOR_WASH);
    p_graphics.fillRect(0, 0, l_size.width, l_size.height);
  }
}