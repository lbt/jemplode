package org.jempeg.empeg.versiontracker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.jempeg.empeg.Version;
import org.jempeg.protocol.SocketConnection;

import com.inzyme.filesystem.FileUtils;
import com.inzyme.io.StreamUtils;
import com.inzyme.model.Reason;
import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.progress.TextProgressListener;
import com.inzyme.properties.PropertiesManager;

/**
* JEmplodeVersionTracker is a VersionTracker that can update
* jEmplode itself.
*
* @author Mike Schrag
*/
public class JEmplodeVersionTracker implements IVersionTracker {
  private ISimpleProgressListener myListener;

  private String myLatestVersion;
  private String myJarName;
  private String myJEmplodeURL;
  private String myJEmplodeJarURL;

  public JEmplodeVersionTracker() {
    this(new TextProgressListener());
  }

  public JEmplodeVersionTracker(ISimpleProgressListener _listener) {
    myListener = _listener;
    myJarName = "jemplode20.jar";
    myJEmplodeURL = "http://www.jempeg.org";
    myJEmplodeJarURL = "http://www.jempeg.org/jemplode20.jar";
  }

  public String getProductName() {
    return "jEmplode";
  }

  public boolean shouldAutoUpdate() {
    return PropertiesManager.getInstance().getBooleanProperty("jempeg.jEmplode.autoUpdateHijack", false);
  }

  public void setAutoUpdate(boolean _autoUpdate) {
    PropertiesManager.getInstance().setBooleanProperty("jempeg.jEmplode.autoUpdateHijack", _autoUpdate);
  }

  public boolean shouldPromptBeforeUpdate() {
    return PropertiesManager.getInstance().getBooleanProperty("jempeg.jEmplode.promptsBeforeUpdate", true);
  }

  public void setPromptsBeforeUpdate(boolean _promptsBeforeUpdate) {
    PropertiesManager.getInstance().setBooleanProperty("jempeg.jEmplode.promptsBeforeUpdate", _promptsBeforeUpdate);
  }

  public boolean isInstalled() throws IOException {
    return true;
  }

  public Reason getFailureReason(String _operation) {
    Reason r = new Reason("You got me why it failed...");
    return r;
  }

  public String getInstalledVersion() throws IOException {
    return Version.VERSION_STRING;
  }

  public String getLatestVersion() throws IOException {
    findLatestVersion();
    return myLatestVersion;
  }

  public boolean isNewerVersionAvailable() throws IOException {
    findLatestVersion();
    boolean newerVersion = !getInstalledVersion().equals(myLatestVersion);
    return newerVersion;
  }

  public String getReleaseNotes() throws IOException {
    findLatestVersion();
    return "None";
  }

  public VersionChange[] getChanges() throws IOException {
    findLatestVersion();

    Vector versionChangesVec = new Vector();
    URL jemplodeURL = new URL(myJEmplodeURL);
    BufferedReader br = new BufferedReader(new InputStreamReader(jemplodeURL.openStream()));
    String releaseNoteStart = "\t<tr><td>";
    double sinceVersion = Double.parseDouble(Version.VERSION_STRING);
    boolean done = false;
    do {
      String line = br.readLine();
      if (line == null) {
        done = true;
      } else {
        if (line.startsWith(releaseNoteStart)) {
          try {
            int versionStop = line.indexOf("<", releaseNoteStart.length());
            String entryVersionStr = line.substring(releaseNoteStart.length(), versionStop);
            int entryFeatureBegin = versionStop + "</td><td>".length();
            int entryFeatureEnd = line.indexOf("</td>", entryFeatureBegin);
            String entryFeature = line.substring(entryFeatureBegin, entryFeatureEnd);
            double entryVersion = Double.parseDouble(entryVersionStr);
            if (entryVersion > sinceVersion) {
              VersionChange change = new VersionChange(entryVersionStr, entryFeature);
              versionChangesVec.addElement(change);
            }
          } catch (Throwable e) {
            // Oh well ..
          }
        }
      }
    } while (!done);
    br.close();
    VersionChange[] changes = new VersionChange[versionChangesVec.size()];
    versionChangesVec.copyInto(changes);
    return changes;
  }

  public void installLatestVersion() throws IOException {
    File currJarFile = FileUtils.findInClasspath(myJarName);
    if (currJarFile == null) {
      throw new IOException("You don't have a file called jemplode20.jar anywhere in your classpath, so it can't be updated.");
    }

    findLatestVersion();

    if (isNewerVersionAvailable()) {
      File tempFile = null;
      FileInputStream tempJarIS = null;
      FileOutputStream currJarOS = null;
      try {
        String tempFileName = new String("jemplode20.tmp");
        URL newJarURL = new URL(myJEmplodeJarURL);
        URLConnection conn = newJarURL.openConnection();
        int length = conn.getContentLength();
        InputStream newJarIS = new BufferedInputStream(conn.getInputStream());
        tempFile = new File(System.getProperty("user.home") + File.separator + tempFileName);
        OutputStream fos = new BufferedOutputStream(new FileOutputStream(tempFile));
        //myListener.taskStarted("Downloading " + myJEmplodeJarURL + " to " + tempFile + "...");
        StreamUtils.copy(newJarIS, fos, SocketConnection.TCP_MAXPAYLOAD, length, myListener);
        fos.close();
        newJarIS.close();
        //myListener.operationUpdated(2, 4);

        //myListener.taskStarted("Updating jEmplode to " + myLatestVersion + "...");
        currJarFile = FileUtils.findInClasspath(myJarName);
        tempJarIS = new FileInputStream(tempFile);
        currJarOS = new FileOutputStream(currJarFile);
        StreamUtils.copy(tempJarIS, currJarOS, SocketConnection.TCP_MAXPAYLOAD, length, myListener);
        //myListener.operationUpdated(3, 4);
        Version.VERSION_STRING = myLatestVersion;
      } finally {
        if (tempFile != null) {
          tempFile.delete();
        }
        if (tempJarIS != null) {
          tempJarIS.close();
        }
        if (currJarOS != null) {
          currJarOS.close();
        }
      }
    }

    //myListener.taskStarted("Done.");
    //myListener.taskUpdated(1, 1);
    //myListener.operationUpdated(4, 4);
  }

  protected void findLatestVersion() throws IOException {
    if (myLatestVersion == null) {
      //myListener.operationStarted("Upgrading jEmplode...");
      //myListener.operationUpdated(0, 4);

      //myListener.taskStarted("Checking for new jEmplode Version...");
      //myListener.taskUpdated(0, 2);

      URL newestVersionURL = new URL(new URL(myJEmplodeURL), "NEWEST_VERSION");
      InputStream is = newestVersionURL.openStream();
      //myListener.taskUpdated(1, 2);

      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      myLatestVersion = br.readLine().trim();
      br.close();
      //myListener.taskUpdated(2, 2);
    }
  }

  public boolean isRestartRequired() {
    return true;
  }
}
