/**
 * Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
 * other contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jempeg.empeg.manager;

import java.io.File;
import java.net.URL;

import org.jempeg.ApplicationContext;
import org.jempeg.JEmplodeProperties;
import org.jempeg.empeg.manager.dialog.ConnectionSelectionDialog;
import org.jempeg.empeg.tags.JOrbisTagExtractor;
import org.jempeg.empeg.tags.TFFID3TagWriter;
import org.jempeg.manager.ui.Splash;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.tags.ASFTagExtractor;
import org.jempeg.tags.ITagExtractor;
import org.jempeg.tags.TagExtractorFactory;
import org.jempeg.tags.TagWriterFactory;
import org.jempeg.tags.id3.ID3TagExtractor;

import com.inzyme.properties.PropertiesManager;
import com.inzyme.text.ResourceBundleUtils;
import com.inzyme.ui.UIUtils;
import com.inzyme.util.ApplicationUtils;
import com.inzyme.util.Debug;

/**
 * Startup Emplode; optionally restore from the file named in _args[0]
 *
 * @author Mike Schrag
 * @version $Revision: 1.16 $
 */
public class Main {
  protected static void initializeTagExtractors() {
    try {
      Class.forName("com.rio.rmmlite.RmmlMain");
      TagExtractorFactory.addTagExtractor((ITagExtractor) Class.forName("com.rio.tags.FlacTagExtractor").newInstance());
      TagExtractorFactory.addTagExtractor(new ID3TagExtractor());
      TagExtractorFactory.addTagExtractor(new ASFTagExtractor());
      TagExtractorFactory.addTagExtractor((ITagExtractor) Class.forName("com.rio.tags.OggTagExtractor").newInstance());
    }
    catch (Throwable t) {
      TagExtractorFactory.addTagExtractor(new ID3TagExtractor());
      TagExtractorFactory.addTagExtractor(new ASFTagExtractor());
      TagExtractorFactory.addTagExtractor(new JOrbisTagExtractor());
    }
  }

  protected static void initializeTagWriters() {
    TagWriterFactory.addTagWriter(new TFFID3TagWriter());
  }

  public static void start() {
    final ApplicationContext context = new ApplicationContext();
    final EmplodeUI emplodeUI = new EmplodeUI(context);
    emplodeUI.getFrame().setVisible(true);

    //    PlayerDatabase playerDatabase = new PlayerDatabase();
    //    ISynchronizeClient synchronizeClient = new LocalSynchronizeClient();
    //    context.setSynchronizeClient(playerDatabase, synchronizeClient);
    //    new SynchronizeUI(playerDatabase, synchronizeClient, context.getFrame()).downloadInBackground(context.getDownloadProgressListener());

    Thread t = new Thread(new Runnable() {
      public void run() {
        try {
          ConnectionSelectionDialog.showConnectionSelectionDialog(context);
        }
        catch (ProtocolException e) {
          Debug.handleError(emplodeUI.getFrame(), e, true);
        }
      }
    }, "Connection Selection");
    t.start();
  }

  public static void main(String[] _args) throws Throwable {
    PropertiesManager.initializeInstance("jEmplode Properties", new File(ApplicationUtils.getSettingsFolder(), "jempegrc"));
    JEmplodeProperties.initializeDefaults();

    UIUtils.initializeOSX("jEmplode");
    ResourceBundleUtils.putBaseName(ResourceBundleUtils.ERRORS_KEY, "org.jempeg.empeg.manager.errors");
    ResourceBundleUtils.putBaseName(ResourceBundleUtils.UI_KEY, "org.jempeg.empeg.manager.ui");

    Splash splash = null;
    try {
      URL splashImageURL = Main.class.getResource("/org/jempeg/empeg/manager/icons/splash.gif");
      splash = new Splash(splashImageURL);
      splash.show();
    }
    catch (Throwable t) {
      t.printStackTrace();
      // Who cares...
    }

    for (int i = 0; i < _args.length; i ++ ) {
      if (_args[i].equalsIgnoreCase("-nometal")) {
        PropertiesManager.getInstance().setProperty(JEmplodeProperties.LOOK_AND_FEEL_PROPERTY, JEmplodeProperties.LOOK_AND_FEEL_SYSTEM);
      }
    }

    initializeTagExtractors();

    initializeTagWriters();

    start();

    if (splash != null) {
      splash.hide();
    }
  }
}