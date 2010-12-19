/* AnimEdit - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.logoedit;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.inzyme.io.FileSeekableInputStream;
import com.inzyme.model.Reason;
import com.inzyme.model.VectorListModel;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.ui.BusyGlassPanel;
import com.inzyme.ui.DialogUtils;
import com.inzyme.util.ApplicationUtils;
import com.inzyme.util.Debug;

import org.jempeg.empeg.protocol.EmpegSocketConnectionFactory;
import org.jempeg.empeg.util.HijackUtils;
import org.jempeg.manager.event.FileChooserKeyListener;
import org.jempeg.manager.util.FileChooserUtils;
import org.jempeg.protocol.IConnection;

public class AnimEdit extends JPanel
{
    private static final int MAX_FRAMES = 32;
    private IConnection myConn;
    private LogoEditPanel myEditPanel;
    private JSlider myFrameSlider;
    private JList mySequenceList;
    private boolean myPlaying;
    private int myLastFrameIndex;
    private Animation myAnimation;
    private VectorListModel mySequenceListModel;
    
    protected class ImageListCellRenderer extends JLabel
	implements ListCellRenderer
    {
	public ImageListCellRenderer() {
	    setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
	    setHorizontalAlignment(0);
	}
	
	public Component getListCellRendererComponent
	    (JList _list, Object _value, int _index, boolean _isSelected,
	     boolean _cellHasFocus) {
	    int listWidth = _list.getWidth();
	    int smallWidth = listWidth / 2;
	    double scale
		= (double) smallWidth / (double) myEditPanel.getImageWidth();
	    int smallHeight
		= (int) ((double) myEditPanel.getImageHeight() * scale);
	    if (_value != null) {
		ScaledImageIcon imageIcon
		    = new ScaledImageIcon((Image) _value, smallWidth,
					  smallHeight, _isSelected);
		setIcon(imageIcon);
		int frameNum = myAnimation.getFrameIndex(_value) + 1;
		setText(String.valueOf(_index + 1) + " ("
			+ String.valueOf(frameNum) + ")");
	    } else {
		setIcon(null);
		setText(null);
	    }
	    setSize(listWidth, smallHeight + 2);
	    return this;
	}
    }
    
    protected class PlayRunnable implements Runnable
    {
	public void run() {
	    while (myPlaying) {
		AnimEdit animedit;
		MONITORENTER (animedit = AnimEdit.this);
		MISSING MONITORENTER
		synchronized (animedit) {
		    int max = myAnimation.getSequenceCount();
		    int value = mySequenceList.getSelectedIndex();
		    if (value >= max - 1)
			value = 0;
		    else
			value++;
		    mySequenceList.setSelectedIndex(value);
		}
		try {
		    Thread.sleep(100L);
		} catch (Throwable throwable) {
		    /* empty */
		}
	    }
	}
    }
    
    protected class NewListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    if (confirmLoseChanges("animation"))
		clear(true);
	}
    }
    
    protected class ClearAnimationListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    Thread t = new Thread(new Runnable() {
		public void run() {
		    try {
			try {
			    DialogUtils.busyWait(AnimEdit.this, true);
			    clearHijack();
			} catch (IOException e) {
			    Debug.println(e);
			}
		    } catch (Object object) {
			DialogUtils.busyWait(AnimEdit.this, false);
			throw object;
		    }
		    DialogUtils.busyWait(AnimEdit.this, false);
		}
	    });
	    t.start();
	}
    }
    
    protected class UploadListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    Thread t = new Thread(new Runnable() {
		public void run() {
		    try {
			try {
			    DialogUtils.busyWait(AnimEdit.this, true);
			    uploadHijack();
			} catch (IOException e) {
			    Debug.println(e);
			}
		    } catch (Object object) {
			DialogUtils.busyWait(AnimEdit.this, false);
			throw object;
		    }
		    DialogUtils.busyWait(AnimEdit.this, false);
		}
	    });
	    t.start();
	}
    }
    
    protected class DownloadListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    if (confirmLoseChanges("animation")) {
		Thread t = new Thread(new Runnable() {
		    public void run() {
			try {
			    try {
				DialogUtils.busyWait(AnimEdit.this, true);
				downloadHijack(false);
			    } catch (IOException e) {
				Debug.println(e);
			    }
			} catch (Object object) {
			    DialogUtils.busyWait(AnimEdit.this, false);
			    throw object;
			}
			DialogUtils.busyWait(AnimEdit.this, false);
		    }
		});
		t.start();
	    }
	}
    }
    
    protected class LoadImageListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    if (confirmLoseChanges("frame")) {
		try {
		    Object obj = ImageUtils.loadImage(AnimEdit.this);
		    if (obj != null) {
			Image image;
			if (obj instanceof Logo)
			    image = ((Logo) obj).getCarImage();
			else
			    image = (Image) obj;
			myEditPanel.setImage(image, true);
			myEditPanel.setChanged(true);
		    }
		} catch (IOException e) {
		    Debug.println(e);
		}
	    }
	}
    }
    
    protected class LogoEditFocusListener extends FocusAdapter
    {
	public void focusLost(FocusEvent e) {
	    frameChanged();
	}
    }
    
    protected class PlayListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    play();
	}
    }
    
    protected class StopListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    stop();
	}
    }
    
    protected class SequenceChangeListener implements ListSelectionListener
    {
	public void valueChanged(ListSelectionEvent e) {
	    sequenceChanged();
	}
    }
    
    protected class FrameChangeListener implements ChangeListener
    {
	public void stateChanged(ChangeEvent _event) {
	    sliderChanged();
	}
    }
    
    protected class AddFrameListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    addFrame();
	}
    }
    
    protected class DuplicateFrameListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    duplicateFrame();
	}
    }
    
    protected class RemoveFrameListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    if ((JOptionPane.showConfirmDialog
		 (AnimEdit.this,
		  "Are you sure you want to delete this frame?"))
		== 0)
		removeFrame();
	}
    }
    
    protected class StepBackListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    stepBack();
	}
    }
    
    protected class StepForwardListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    stepForward();
	}
    }
    
    protected class MoveFrameBackListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    moveFrameBack();
	}
    }
    
    protected class MoveFrameForwardListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    moveFrameForward();
	}
    }
    
    protected class AddToSequenceListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    addToSequence(true);
	}
    }
    
    protected class RemoveFromSequenceListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    removeFromSequence();
	}
    }
    
    protected class MoveSequenceBackListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    moveSequenceBack();
	}
    }
    
    protected class MoveSequenceForwardListener implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    moveSequenceForward();
	}
    }
    
    protected class UndoListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    myEditPanel.undo();
	}
    }
    
    protected class LoadListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    if (confirmLoseChanges("animation")) {
		try {
		    JFileChooser jfc = new JFileChooser();
		    jfc.addKeyListener(new FileChooserKeyListener(jfc));
		    jfc.setMultiSelectionEnabled(false);
		    FileChooserUtils.setToLastDirectory(jfc);
		    int results = jfc.showOpenDialog(AnimEdit.this);
		    if (results == 0) {
			FileChooserUtils.saveLastDirectory(jfc);
			File animFile = jfc.getSelectedFile();
			load(animFile);
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }
    
    protected class SaveListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    try {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(false);
		FileChooserUtils.setToLastDirectory(jfc);
		int results = jfc.showSaveDialog(AnimEdit.this);
		if (results == 0) {
		    FileChooserUtils.saveLastDirectory(jfc);
		    File animFile = jfc.getSelectedFile();
		    FileOutputStream fos = new FileOutputStream(animFile);
		    LittleEndianOutputStream eos
			= new LittleEndianOutputStream(fos);
		    save(eos);
		    eos.close();
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
    
    protected class SaveAnimatedGifListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    try {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(false);
		FileChooserUtils.setToLastDirectory(jfc);
		int results = jfc.showSaveDialog(AnimEdit.this);
		if (results == 0) {
		    FileChooserUtils.saveLastDirectory(jfc);
		    File animFile = jfc.getSelectedFile();
		    FileOutputStream fos = new FileOutputStream(animFile);
		    myAnimation.saveAnimatedGif(fos);
		    fos.close();
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
    
    protected class SavePngListener implements ActionListener
    {
	public void actionPerformed(ActionEvent _event) {
	    try {
		ImageUtils.savePngImage(myEditPanel.getImage(), AnimEdit.this);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
    
    protected static class HelpListener extends WindowAdapter
	implements ActionListener
    {
	public void actionPerformed(ActionEvent e) {
	    JFrame jf = new JFrame("Help");
	    jf.addWindowListener(this);
	    JTextPane ta = new JTextPane();
	    ta.setContentType("text/html");
	    ta.setText
		("<html><body><b>JAnimEdit</b><p>JAnimEdit is an editor for Empeg bootup animations.  To modify your bootup animation, you must have a Hijack kernel version 242 or later.  Additionally, you must have 'Use Hijack when Possible' enable in your JEmplode Tools...Options panel.<p>To understand JAnimEdit, you need to understand a bit about how the Empeg represents animations internally.  Because animations take up precious memory on the player, and because many animations repeat frames, the Empeg animation player allows you to define frames that are duplicated in the animation.  This allows you to, for instance, have a single frame of animation appear to pause onscreen for a longer period of time by duplicating it multiple times.  A good example of frame reuses is the Empeg Tux animation -- As Tux is waving, there are only two waving frames, a wave to the left, and a wave to the right.  To make him wave back and forth several times, the animation simply references the left-right wave pictures several times in a row.  Less memory for more animation is the key.<p>So what does all this mean to JAnimEdit?  It means that you can edit two separate things.  One is the Frame Pool, and the the other is the Sequence List.  There are two panes in JAnimEdit that are separated by a divider.  The left pane is the editor for the Frame Pool and the right pane is the editor for the Sequence List.<p><i>The Frame Pool</i><br>The Frame Pool Editor is the editor on the left-hand side of JAnimEdit.  The Frame Pool is essentially a set of unique drawings that you can reference in the sequence list to construct a sequence of animations.  You start out with a single unique frame in the Frame Pool that is blank.  You can draw in the frame pool just like you do in JLogoEdit with all of the same controls.  Use the controls above the Frame Editor to adjusting drawing pen settings.  Go ahead and try it ... Every time the Frame Editor loses focus, it will save your drawing to memory, so anytime you click on a button after drawing your frame will be saved (log this away for later -- you'll see why :) ).<p>Now you have a single frame in your Frame Pool.  To make another frame, you can either hit the 'New Frame' button (the left-most button on the bottom control panel).  This will append a new blank frame to the end of the Frame Pool.  Notice that when you press this button, the slider at the bottom changes.  The slider at the bottom lets you select which frame in the pool you want to edit. You can have at most 32 unique frames in your Frame Pool, so you'll notice the slider won't go above 32 (and JAnimEdit will just beep at you if you keep hitting 'New Frame').  So you know how to add a blank frame, but in most animation, you want to just slightly modify the previous frame.  That's what the second button is for -- it will make a copy of the currently selected frame and insert it right after the current frame.  Now you can just tweak the copy and animate away.  Notice that 'New Frame' appends to the end of the animation, but 'Duplicate Frame' inserts a frame right after where the slider is.  Both operations cause the new frame to be selected.The last useful button is the trashcan button.  Use the trashcan toolbar button to delete a frame from the frame pool (it's gone forever, so we careful).<p>The left/right arrows on the Frame Pool toolbar are there just to help keep your Frame Pool organized.  The order of the frames in the pool don't matter at all -- They don't define your animation, they only define the frames that you are allowed to <i>use</i> in your animation.  In fact, if you were to try and play your animation right now, nothing would happen.  So how do you get your animation to play?<p><i>The Sequence List</i><br>Enter the Sequence List.  The Sequence List is that are on the right side of JAnimEdit.  This defines the actual animation as it will play on your Empeg.  So let's say you have four different unique frames in your Frame Pool.  If you want them to play in-order with no pauses, drag the Frame Pool slider to Frame #1 and press the 'Add to Sequence' button -- the left-most button on the Sequence Editor toolbar (it is the same icon as the 'New Frame' button on the Frame Pool toolbar).  You'll notice a tiny picture of the frame you selected appears in the sequence list.  The numbers next to it mean you've added one frame that references Frame Pool entry #1 to your animation. Now go through each frame in the frame pool and add it to the Sequence List.  You should now see one entry in your Sequence List for each frame in the pool.  If you hit the play button on the Sequence List toolbar, you'll see it (very quickly) go through your animation.  Note that the animation speed in JAnimEdit will not match the speed on the Empeg (the Empeg is fixed at 12 frames per second).  To stop playing, press the pause button (or one of the step-forward, step-backward buttons).<p>So now you have an animation.  But say you wanted the first frame of animation to pause on screen for several frames.  The beauty of the Empeg's animation system is that you can do this without using much memory at all (just one more reference to the paused frame).  Go ahead and click on the first entry in your sequence list.  You'll notice it automatically selects the corresponding frame in your Frame Pool.  Now press the 'Add to Sequence' button a few more times.  You'll that several copies of the frame appear in your Sequence List (but notice that they all reference the same Frame Pool entry number -- that's the number in parenthesis).  Click 'play' again and notice that the frame you duplicated hangs around for a bit.  Go ahead and stop the animation.  By the way, when you're playing an animation, most features are disabled.  So stop the animation before you start editing again.  Back to the Sequence Pool --- You're not limited to only duplicating frames together, you can reuse any frame at any point in your sequence.  Simply click the entry in the Sequence List that you want to append a frame after, choose the frame in the Frame Pool (with the slider) that you want to add to the sequence, and press the 'Add to Sequence' button.  Use the trashcan in the sequence toolbar to delete a sequence entry (this will <i>not</i> delete the frame from the Frame Pool, it will just remove the reference to it -- just like tunes on your Empeg).  Use the up/down arrows to reorder the frames in your Sequence List.  Unlike the left/right arrows in the Frame Pool, this <i>will</i> actually change the order of frames in your animation.  Basically, if you read the Sequence List from top-down, that is how your animation will play.<p>That's it for actually constructing an animation!<p><i>Using another Editor</i><br>Under the File menu, you'll notice you can Save as PNG... and Open Image...  This allows you to export a single frame from your Frame Pool to a PNG file and to load a single frame back in from a GIF, JPEG, or PNG.  Note that when you use Open Image ... it will replace whatever Frame you currently have selected in the Frame Pool.  Also be careful about the color palette that you use -- the Empeg only supports a small set of colors (the ones that are available in the pulldown).  It's up to you to stick to those colors in your external editor.<p><i>Uploading to/Downloading from the Empeg</i><br>Once you are done with your animation, you should always save it disk -- Just to be on the safe side (for instance, if you were to update your Empeg to the latest Empeg beta, it would likely wipe your animation.  <i>Keep a backup!</i>).  After that, assuming you connected to a Hijack'd Empeg from the jEmplode connection dialog at the beginning, you should be able to click on File ... Upload and your animation should upload to your empeg in a couple seconds.  It will tell you when it's done.  That's it.  Reboot and you should see your animation.  <b>Remember, if you didn't add anything to the Sequence List, nothing will play.  Just having frames in the Frame Pool doesn't do anything!!</b><p>Likewise, you can hit File .. Download and the current custom animation will download from your Empeg.  It will replace whatever animation is currently open, so <i>remember to save!</i>  This will only download <i>custom</i> animations, not the Tux or Rio animations.<p>Lastly, you can choose to File... Clear and it will remove the custom animation from your Empeg (returning you to whatever your original built-in animation was).<p>That's all there is to JAnimEdit ... Enjoy!</body></html>");
	    JScrollPane jsp = new JScrollPane(ta);
	    jf.getContentPane().add(jsp);
	    jf.setSize(500, 500);
	    jf.show();
	}
	
	public void windowClosing(WindowEvent _event) {
	    JFrame frame = (JFrame) _event.getSource();
	    frame.dispose();
	}
    }
    
    public AnimEdit() {
	this((IConnection) null);
    }
    
    public AnimEdit(IConnection _conn) {
	myConn = _conn;
	myAnimation = new Animation(this, 128, 32);
	myEditPanel = new LogoEditPanel(myAnimation.getWidth(),
					myAnimation.getHeight());
	mySequenceListModel = new VectorListModel(myAnimation.getSequence());
	mySequenceList = new JList(mySequenceListModel);
	mySequenceList.setCellRenderer(new ImageListCellRenderer());
	mySequenceList.addListSelectionListener(new SequenceChangeListener());
	mySequenceList.setSelectionMode(0);
	setLayout(new BorderLayout());
	JPanel editPanel = new JPanel();
	editPanel.setLayout(new BorderLayout());
	LogoEditToolBar toolBar = new LogoEditToolBar();
	toolBar.setFloatable(false);
	toolBar.addToolChangeListener(myEditPanel.createToolChangeListener());
	toolBar.addScaleListener(myEditPanel.createScaleListener());
	toolBar.addForegroundColorListener
	    (myEditPanel.createForegroundColorListener());
	toolBar.addBackgroundColorListener
	    (myEditPanel.createBackgroundColorListener());
	toolBar.initialize();
	toolBar.setScale(5);
	JToolBar frameToolBar = new JToolBar();
	myFrameSlider = new JSlider();
	myFrameSlider.setMinimum(1);
	myFrameSlider.setMaximum(1);
	myFrameSlider.setMajorTickSpacing(1);
	myFrameSlider.setMinorTickSpacing(1);
	myFrameSlider.setPaintLabels(true);
	myFrameSlider.setPaintTrack(true);
	myFrameSlider.setSnapToTicks(true);
	JButton addFrameButton = new JButton();
	addFrameButton.addActionListener(new AddFrameListener());
	addFrameButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/New24.gif")));
	addFrameButton.setToolTipText("Append a Blank Frame");
	JButton duplicateFrameButton = new JButton();
	duplicateFrameButton.addActionListener(new DuplicateFrameListener());
	duplicateFrameButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/Copy24.gif")));
	duplicateFrameButton.setToolTipText("Duplicate this Frame");
	JButton removeFrameButton = new JButton();
	removeFrameButton.addActionListener(new RemoveFrameListener());
	removeFrameButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/Delete24.gif")));
	removeFrameButton.setToolTipText("Delete this Frame");
	JButton moveFrameBackButton = new JButton();
	moveFrameBackButton.addActionListener(new MoveFrameBackListener());
	moveFrameBackButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/Back24.gif")));
	moveFrameBackButton.setToolTipText("Move this Frame Back");
	JButton moveFrameForwardButton = new JButton();
	moveFrameForwardButton
	    .addActionListener(new MoveFrameForwardListener());
	moveFrameForwardButton.setIcon
	    (new ImageIcon
	     (this.getClass().getResource
	      ("/org/jempeg/empeg/logoedit/icons/Forward24.gif")));
	moveFrameForwardButton.setToolTipText("Move this Frame Forward");
	frameToolBar.setFloatable(false);
	frameToolBar.add(myFrameSlider);
	frameToolBar.addSeparator();
	frameToolBar.add(addFrameButton);
	frameToolBar.add(duplicateFrameButton);
	frameToolBar.add(removeFrameButton);
	frameToolBar.addSeparator();
	frameToolBar.add(moveFrameBackButton);
	frameToolBar.add(moveFrameForwardButton);
	editPanel.add(toolBar, "North");
	editPanel.add(new JScrollPane(myEditPanel), "Center");
	editPanel.add(frameToolBar, "South");
	JPanel sequencePanel = new JPanel();
	sequencePanel.setLayout(new BorderLayout());
	JToolBar sequenceControlPanel = new JToolBar();
	sequenceControlPanel.setFloatable(false);
	JButton addSequenceButton = new JButton();
	addSequenceButton.addActionListener(new AddToSequenceListener());
	addSequenceButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/New24.gif")));
	addSequenceButton
	    .setToolTipText("Add the current frame to the sequence");
	JButton removeSequenceButton = new JButton();
	removeSequenceButton
	    .addActionListener(new RemoveFromSequenceListener());
	removeSequenceButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/Delete24.gif")));
	removeSequenceButton
	    .setToolTipText("Delete the selected sequence entry");
	JButton moveSequenceBackButton = new JButton();
	moveSequenceBackButton
	    .addActionListener(new MoveSequenceBackListener());
	moveSequenceBackButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/Up24.gif")));
	moveSequenceBackButton
	    .setToolTipText("Move the selected sequence entry up");
	JButton moveSequenceForwardButton = new JButton();
	moveSequenceForwardButton
	    .addActionListener(new MoveSequenceForwardListener());
	moveSequenceForwardButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/Down24.gif")));
	moveSequenceForwardButton
	    .setToolTipText("Move the selected sequence entry down");
	JButton playButton = new JButton();
	playButton.addActionListener(new PlayListener());
	playButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/Play24.gif")));
	playButton.setToolTipText("Play");
	JButton stopButton = new JButton();
	stopButton.addActionListener(new StopListener());
	stopButton.setIcon
	    (new ImageIcon(this.getClass().getResource
			   ("/org/jempeg/empeg/logoedit/icons/Pause24.gif")));
	stopButton.setToolTipText("Pause/Stop");
	JButton stepForwardButton = new JButton();
	stepForwardButton.addActionListener(new StepForwardListener());
	stepForwardButton.setIcon
	    (new ImageIcon
	     (this.getClass().getResource
	      ("/org/jempeg/empeg/logoedit/icons/StepForward24.gif")));
	stepForwardButton.setToolTipText("Step Forward");
	JButton stepBackButton = new JButton();
	stepBackButton.addActionListener(new StepBackListener());
	stepBackButton.setIcon
	    (new ImageIcon
	     (this.getClass().getResource
	      ("/org/jempeg/empeg/logoedit/icons/StepBack24.gif")));
	stepBackButton.setToolTipText("Step Back");
	sequenceControlPanel.add(addSequenceButton);
	sequenceControlPanel.add(removeSequenceButton);
	sequenceControlPanel.addSeparator();
	sequenceControlPanel.add(moveSequenceBackButton);
	sequenceControlPanel.add(moveSequenceForwardButton);
	sequenceControlPanel.addSeparator();
	sequenceControlPanel.add(playButton);
	sequenceControlPanel.add(stopButton);
	sequenceControlPanel.add(stepBackButton);
	sequenceControlPanel.add(stepForwardButton);
	sequencePanel.add(new JScrollPane(mySequenceList), "Center");
	sequencePanel.add(sequenceControlPanel, "South");
	JSplitPane splitPane = new JSplitPane(1);
	splitPane.setOneTouchExpandable(true);
	splitPane.setLeftComponent(editPanel);
	splitPane.setRightComponent(sequencePanel);
	add(splitPane, "Center");
	myFrameSlider.addChangeListener(new FrameChangeListener());
	myEditPanel.addFocusListener(new LogoEditFocusListener());
	clear(false);
    }
    
    public synchronized void clearHijack() throws IOException {
	Reason shouldUseHijack = HijackUtils.shouldUseHijack(myConn);
	if (shouldUseHijack == null) {
	    byte[] animBytes = new byte[4];
	    uploadHijack(animBytes);
	    JOptionPane.showMessageDialog(this,
					  "Animation successfully cleared.");
	} else
	    JOptionPane.showMessageDialog(this, shouldUseHijack.getReason());
    }
    
    public synchronized void uploadHijack() throws IOException {
	Reason shouldUseHijack = HijackUtils.shouldUseHijack(myConn);
	do {
	    if (shouldUseHijack == null) {
		if (myAnimation.getSequenceCount() == 0)
		    JOptionPane.showMessageDialog
			(this,
			 "You haven't added any frames to the sequence, so your animation will be blank.");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LittleEndianOutputStream eos
		    = new LittleEndianOutputStream(baos);
		save(eos);
		eos.flush();
		int size = baos.size();
		eos.writeUnsigned32((long) (655360 - (size + 8)));
		eos.write(new byte[] { 65, 78, 73, 77 });
		eos.close();
		byte[] animBytes = baos.toByteArray();
		uploadHijack(animBytes);
		try {
		    downloadHijack(true);
		    JOptionPane
			.showMessageDialog(this, "Animation upload complete.");
		    break;
		} catch (Throwable t) {
		    JOptionPane.showMessageDialog(this,
						  "Animation upload failed.");
		    break;
		}
	    }
	    JOptionPane.showMessageDialog(this, shouldUseHijack.getReason());
	} while (false);
    }
    
    protected synchronized void uploadHijack(byte[] _animBytes)
	throws IOException {
	if (myAnimation.getSequenceCount() > 64)
	    JOptionPane.showMessageDialog
		(this,
		 "Your animation exceeds 64 sequence entries.  Just to be safe, we're not sending it.");
	else {
	    InetAddress inetAddress = HijackUtils.getAddress(myConn);
	    ByteArrayInputStream bais = new ByteArrayInputStream(_animBytes);
	    HijackUtils.upload(inetAddress, "/proc/empeg_kernel", bais,
			       655360 - _animBytes.length, _animBytes.length,
			       null);
	}
    }
    
    protected synchronized void downloadHijack(boolean _testDownload)
	throws IOException {
	Reason shouldUseHijack = HijackUtils.shouldUseHijack(myConn);
	if (shouldUseHijack == null) {
	    InetAddress inetAddress = HijackUtils.getAddress(myConn);
	    File logoGrabFile = new File(System.getProperty("user.home")
					 + File.separator + "logoGrab.raw");
	    FileOutputStream logoGrabOS = new FileOutputStream(logoGrabFile);
	    HijackUtils.download(inetAddress, "/proc/empeg_kernel", logoGrabOS,
				 null);
	    logoGrabOS.close();
	    FileSeekableInputStream fsis
		= new FileSeekableInputStream(logoGrabFile);
	    fsis.seek(fsis.length() - 4L);
	    if (fsis.read() == 65 && fsis.read() == 78 && fsis.read() == 73
		&& fsis.read() == 77) {
		fsis.seek(fsis.length() - 8L);
		LittleEndianInputStream eis = null;
		try {
		    eis = new LittleEndianInputStream(fsis);
		    long animOffset = eis.readUnsigned32();
		    fsis.seek(animOffset);
		    long length = 655360L - animOffset - 8L;
		    if (_testDownload) {
			Animation anim
			    = new Animation(this, myAnimation.getWidth(),
					    myAnimation.getHeight());
			anim.load(eis, length);
		    } else {
			load(eis, length);
			JOptionPane.showMessageDialog
			    (this, "Animation download complete.");
		    }
		} catch (Object object) {
		    if (eis != null)
			eis.close();
		    throw object;
		}
		if (eis != null)
		    eis.close();
	    } else
		JOptionPane.showMessageDialog
		    (this, "There is no animation on this Empeg.");
	} else
	    JOptionPane.showMessageDialog(this, shouldUseHijack.getReason());
    }
    
    public synchronized void save(LittleEndianOutputStream _os)
	throws IOException {
	if (!myPlaying)
	    myAnimation.save(_os);
	else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public synchronized void load
	(LittleEndianInputStream _is, long _totalLength) throws IOException {
	if (!myPlaying) {
	    myAnimation.load(_is, _totalLength);
	    animationChanged();
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public synchronized void load(File _file) throws IOException {
	if (!myPlaying) {
	    String name = _file.getName();
	    if (name.toLowerCase().endsWith(".gif")) {
		FileInputStream fis = new FileInputStream(_file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		myAnimation.loadAnimatedGif(bis);
		bis.close();
		animationChanged();
	    } else {
		FileInputStream fis = new FileInputStream(_file);
		LittleEndianInputStream eis = new LittleEndianInputStream(fis);
		load(eis, _file.length());
		eis.close();
	    }
	}
    }
    
    protected synchronized void animationChanged() throws IOException {
	myLastFrameIndex = -1;
	myEditPanel.setChanged(false);
	mySequenceList.setListData(myAnimation.getSequence());
	syncSlider();
	for (int i = myAnimation.getFrameCount() - 1; i >= 0; i--)
	    frameChangedTo(i, true);
	myFrameSlider.setValue(1);
    }
    
    public boolean isPlaying() {
	return myPlaying;
    }
    
    protected boolean confirmLoseChanges(String _frameOrAnimation) {
	boolean loseChanges
	    = (!myEditPanel.isChanged()
	       || JOptionPane.showConfirmDialog(this,
						("You've made changes to this "
						 + _frameOrAnimation
						 + ", are you sure?")) == 0);
	return loseChanges;
    }
    
    public synchronized void clear(boolean _updateFrame) {
	if (!myPlaying) {
	    myEditPanel.setChanged(false);
	    myAnimation.clear();
	    myAnimation.addFrameAt(0);
	    syncSlider();
	    myFrameSlider.setValue(0);
	    mySequenceList.revalidate();
	    mySequenceList.repaint();
	    if (_updateFrame)
		updateFrame(0);
	}
    }
    
    public synchronized void play() {
	if (!myPlaying && myAnimation.getSequenceCount() > 1) {
	    myEditPanel.setEnabled(false);
	    mySequenceList.setEnabled(false);
	    myPlaying = true;
	    Thread playThread = new Thread(new PlayRunnable());
	    playThread.start();
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public synchronized void stop() {
	if (myPlaying) {
	    myEditPanel.setEnabled(true);
	    mySequenceList.setEnabled(true);
	    myPlaying = false;
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public void moveFrameForward() {
	int currentFrameIndex = getSliderIndex();
	if (!isPlaying()
	    && currentFrameIndex < myAnimation.getFrameCount() - 1) {
	    Image img = myAnimation.getFrameAt(currentFrameIndex);
	    myAnimation.removeFrameAt(currentFrameIndex);
	    myAnimation.insertFrameAt(img, currentFrameIndex + 1);
	    myLastFrameIndex = currentFrameIndex + 1;
	    myFrameSlider.setValue(currentFrameIndex + 2);
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public void moveFrameBack() {
	int currentFrameIndex = getSliderIndex();
	if (!isPlaying() && currentFrameIndex > 0) {
	    Image img = myAnimation.getFrameAt(currentFrameIndex);
	    myAnimation.removeFrameAt(currentFrameIndex);
	    myAnimation.insertFrameAt(img, currentFrameIndex - 1);
	    myLastFrameIndex = currentFrameIndex - 1;
	    myFrameSlider.setValue(currentFrameIndex);
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public void moveSequenceForward() {
	int currentSequenceIndex = mySequenceList.getSelectedIndex();
	if (!isPlaying()
	    && currentSequenceIndex < myAnimation.getSequenceCount() - 1) {
	    Image img = myAnimation.getSequenceAt(currentSequenceIndex);
	    myAnimation.removeSequenceAt(currentSequenceIndex);
	    myAnimation.insertSequenceAt(img, currentSequenceIndex + 1);
	    mySequenceListModel.fireContentsChanged(mySequenceList,
						    currentSequenceIndex,
						    currentSequenceIndex + 1);
	    mySequenceList.setSelectedIndex(currentSequenceIndex + 1);
	    mySequenceList.ensureIndexIsVisible(currentSequenceIndex + 1);
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public void moveSequenceBack() {
	int currentSequenceIndex = mySequenceList.getSelectedIndex();
	if (!isPlaying() && currentSequenceIndex > 0) {
	    Image img = myAnimation.getSequenceAt(currentSequenceIndex);
	    myAnimation.removeSequenceAt(currentSequenceIndex);
	    myAnimation.insertSequenceAt(img, currentSequenceIndex - 1);
	    mySequenceListModel.fireContentsChanged(mySequenceList,
						    currentSequenceIndex - 1,
						    currentSequenceIndex);
	    mySequenceList.setSelectedIndex(currentSequenceIndex - 1);
	    mySequenceList.ensureIndexIsVisible(currentSequenceIndex - 1);
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public void stepForward() {
	if (isPlaying())
	    stop();
	int sequenceIndex = mySequenceList.getSelectedIndex();
	if (sequenceIndex < myAnimation.getSequenceCount() - 1) {
	    mySequenceList.setSelectedIndex(sequenceIndex + 1);
	    mySequenceList.ensureIndexIsVisible(sequenceIndex + 1);
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public void stepBack() {
	if (isPlaying())
	    stop();
	int sequenceIndex = mySequenceList.getSelectedIndex();
	if (sequenceIndex > 0) {
	    mySequenceList.setSelectedIndex(sequenceIndex - 1);
	    mySequenceList.ensureIndexIsVisible(sequenceIndex - 1);
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public void addFrame() {
	int frameCount = myAnimation.getFrameCount();
	if (!isPlaying() && frameCount < 32) {
	    myAnimation.addFrameAt(frameCount);
	    syncSlider();
	    myFrameSlider.setValue(frameCount + 1);
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public void duplicateFrame() {
	if (!isPlaying() && myAnimation.getFrameCount() < 32) {
	    int currentFrameIndex = getSliderIndex();
	    Image img = myEditPanel.getImage();
	    myAnimation.addFrameAt(currentFrameIndex + 1);
	    myAnimation.setFrameAt(img, currentFrameIndex + 1);
	    syncSlider();
	    myFrameSlider.setValue(currentFrameIndex + 2);
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public void removeFrame() {
	if (!isPlaying()) {
	    int currentFrameIndex = getSliderIndex();
	    Image image = myAnimation.getFrameAt(currentFrameIndex);
	    myAnimation.removeFrameAt(currentFrameIndex);
	    int size = myAnimation.getSequenceCount();
	    for (int i = size - 1; i >= 0; i--) {
		if (myAnimation.getSequenceAt(i) == image) {
		    myAnimation.removeSequenceAt(i);
		    mySequenceListModel.fireIntervalRemoved(mySequenceList, i,
							    i);
		}
	    }
	    mySequenceList.revalidate();
	    mySequenceList.repaint();
	    if (myAnimation.getFrameCount() == 0)
		myAnimation.addFrameAt(0);
	    if (currentFrameIndex >= myAnimation.getFrameCount()) {
		updateFrame(currentFrameIndex - 1);
		myFrameSlider.setValue(currentFrameIndex);
	    } else
		updateFrame(currentFrameIndex);
	    syncSlider();
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    public int getSliderIndex() {
	int index = myFrameSlider.getValue() - 1;
	return index;
    }
    
    public int getSequenceIndex() {
	int sequenceIndex = mySequenceList.getSelectedIndex();
	return myAnimation.getSequenceIndex(sequenceIndex);
    }
    
    public Image getFrame() {
	return myAnimation.getFrameAt(getSliderIndex());
    }
    
    public void addToSequence(boolean _append) {
	int currentFrameIndex = getSliderIndex();
	int selectedIndex = mySequenceList.getSelectedIndex();
	if (selectedIndex == -1)
	    selectedIndex = myAnimation.getSequenceCount();
	else if (_append)
	    selectedIndex++;
	Image image = myAnimation.getFrameAt(currentFrameIndex);
	myAnimation.insertSequenceAt(image, selectedIndex);
	mySequenceListModel.fireIntervalAdded(mySequenceList, selectedIndex,
					      selectedIndex);
	mySequenceList.repaint();
	mySequenceList.setSelectedIndex(selectedIndex);
	mySequenceList.ensureIndexIsVisible(selectedIndex);
    }
    
    public void removeFromSequence() {
	int selectedIndex = mySequenceList.getSelectedIndex();
	if (selectedIndex > -1 && myAnimation.getSequenceCount() > 0) {
	    myAnimation.removeSequenceAt(selectedIndex);
	    mySequenceListModel.fireIntervalRemoved(mySequenceList,
						    selectedIndex,
						    selectedIndex);
	    int size = myAnimation.getSequenceCount();
	    if (size > 0)
		mySequenceList.setSelectedIndex(Math.min(selectedIndex,
							 size - 1));
	    mySequenceList.revalidate();
	    mySequenceList.repaint();
	} else
	    Toolkit.getDefaultToolkit().beep();
    }
    
    protected synchronized void sliderChanged() {
	int index = getSliderIndex();
	frameChangedTo(index);
    }
    
    protected synchronized void sequenceChanged() {
	int index = getSequenceIndex();
	if (index != -1)
	    myFrameSlider.setValue(index + 1);
    }
    
    protected synchronized void frameChanged() {
	frameChangedTo(getSliderIndex(), true);
    }
    
    protected synchronized void frameChangedTo(int _index) {
	frameChangedTo(_index, false);
    }
    
    protected synchronized void frameChangedTo(int _index, boolean _force) {
	boolean indexChanged = _index != myLastFrameIndex;
	if (_force || indexChanged) {
	    myEditPanel.stopTool();
	    if (myEditPanel.isChanged()) {
		Image img = myAnimation.getFrameAt(myLastFrameIndex);
		myEditPanel.getImage(img);
		mySequenceList.repaint();
	    }
	    if (indexChanged) {
		Image img = myAnimation.getFrameAt(_index);
		myEditPanel.setImage(img, false);
		myLastFrameIndex = _index;
	    }
	    myEditPanel.startTool();
	}
    }
    
    protected void syncSlider() {
	int max = myAnimation.getFrameCount();
	myFrameSlider.setMaximum(max);
	if (myFrameSlider.getValue() > max)
	    myFrameSlider.setValue(max);
    }
    
    protected void updateFrame(int _index) {
	myEditPanel.setImage(myAnimation.getFrameAt(_index), false);
    }
    
    public static JFrame createFrame(ActionListener _exitListener) {
	return createFrame(_exitListener, null);
    }
    
    public static JFrame createFrame(ActionListener _exitListener,
				     IConnection _conn) {
	JFrame jf = new JFrame("JAnimEdit");
	jf.setGlassPane(new BusyGlassPanel());
	AnimEdit animEdit = new AnimEdit(_conn);
	jf.getContentPane().add(animEdit);
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenuItem newAnimation = new JMenuItem("New...");
	newAnimation.addActionListener(animEdit.createNewListener());
	fileMenu.add(newAnimation);
	JMenuItem open = new JMenuItem("Open...");
	open.addActionListener(animEdit.createLoadListener());
	fileMenu.add(open);
	JMenuItem openImage = new JMenuItem("Open Frame Image...");
	openImage.addActionListener(animEdit.createLoadImageListener());
	fileMenu.add(openImage);
	fileMenu.addSeparator();
	JMenuItem save = new JMenuItem("Save As...");
	save.addActionListener(animEdit.createSaveListener());
	fileMenu.add(save);
	JMenuItem saveAnimatedGif = new JMenuItem("Save As Animated GIF...");
	saveAnimatedGif
	    .addActionListener(animEdit.createSaveAnimatedGifListener());
	fileMenu.add(saveAnimatedGif);
	JMenuItem savePng = new JMenuItem("Save Frame As PNG...");
	savePng.addActionListener(animEdit.createSavePngListener());
	fileMenu.add(savePng);
	fileMenu.addSeparator();
	JMenuItem upload = new JMenuItem("Upload Animation...");
	upload.addActionListener(animEdit.createUploadListener());
	fileMenu.add(upload);
	JMenuItem download = new JMenuItem("Download Animation...");
	download.addActionListener(animEdit.createDownloadListener());
	fileMenu.add(download);
	JMenuItem clear = new JMenuItem("Clear Animation...");
	clear.addActionListener(animEdit.createClearListener());
	fileMenu.add(clear);
	fileMenu.addSeparator();
	final JMenuItem exit = new JMenuItem("Exit");
	exit.addActionListener(_exitListener);
	fileMenu.add(exit);
	JMenu editMenu = new JMenu("Edit");
	JMenuItem undo = new JMenuItem("Undo/Redo");
	undo.addActionListener(animEdit.createUndoListener());
	editMenu.add(undo);
	JMenu helpMenu = new JMenu("Help");
	JMenuItem help = new JMenuItem("What the hell am I doing?");
	help.addActionListener(new HelpListener());
	helpMenu.add(help);
	menuBar.add(fileMenu);
	menuBar.add(editMenu);
	menuBar.add(helpMenu);
	jf.setJMenuBar(menuBar);
	jf.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		exit.doClick();
	    }
	});
	jf.pack();
	return jf;
    }
    
    public static void main(String[] _args) throws Throwable {
	PropertiesManager.initializeInstance("jEmplode Properties",
					     new File(ApplicationUtils
							  .getSettingsFolder(),
						      "jempegrc"));
	IConnection conn
	    = new EmpegSocketConnectionFactory
		  (InetAddress.getByName("10.0.0.101")).createConnection();
	JFrame jf = createFrame(new ActionListener() {
	    public void actionPerformed(ActionEvent _event) {
		System.exit(0);
	    }
	}, conn);
	DialogUtils.centerWindow(jf);
	jf.setVisible(true);
    }
    
    protected ActionListener createUndoListener() {
	return new UndoListener();
    }
    
    protected ActionListener createNewListener() {
	return new NewListener();
    }
    
    protected ActionListener createLoadListener() {
	return new LoadListener();
    }
    
    protected ActionListener createLoadImageListener() {
	return new LoadImageListener();
    }
    
    protected ActionListener createSaveListener() {
	return new SaveListener();
    }
    
    protected ActionListener createSaveAnimatedGifListener() {
	return new SaveAnimatedGifListener();
    }
    
    protected ActionListener createSavePngListener() {
	return new SavePngListener();
    }
    
    protected ActionListener createUploadListener() {
	return new UploadListener();
    }
    
    protected ActionListener createDownloadListener() {
	return new DownloadListener();
    }
    
    protected ActionListener createClearListener() {
	return new ClearAnimationListener();
    }
}
