package com.inzyme.ui;

import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.text.Document;

import org.jempeg.JEmplodeProperties;

import com.inzyme.properties.PropertiesManager;

public class SelectAllTextField extends JTextField {
  public SelectAllTextField() {
    super();
  }

  public SelectAllTextField(int columns) {
    super(columns);
  }

  public SelectAllTextField(String text) {
    super(text);
  }

  public SelectAllTextField(String text, int columns) {
    super(text, columns);
  }

  public SelectAllTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
  }

  protected void processFocusEvent(FocusEvent _event) {
    super.processFocusEvent(_event);
    if (PropertiesManager.getInstance().getBooleanProperty(JEmplodeProperties.TONY_SELECTIONS_KEY)) {
      if (_event.getID() == FocusEvent.FOCUS_GAINED) {
        selectAll();
      }
    }
  }
}