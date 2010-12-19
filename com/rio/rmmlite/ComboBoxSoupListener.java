/* ComboBoxSoupListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.rmmlite;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import org.jempeg.nodestore.FIDPlaylist;
import org.jempeg.nodestore.soup.ISoupListener;
import org.jempeg.nodestore.soup.SoupUpdater;

public class ComboBoxSoupListener implements ISoupListener
{
    private JComboBox myComboBox;
    
    public ComboBoxSoupListener(JComboBox _comboBox) {
	myComboBox = _comboBox;
    }
    
    public void soupInitialized(SoupUpdater _soupUpdater,
				FIDPlaylist _soupPlaylist) {
	ComboBoxModel model = myComboBox.getModel();
	if (model != null && model.getSize() > 0
	    && model.getSelectedItem() == null)
	    myComboBox.setSelectedIndex(0);
    }
}
