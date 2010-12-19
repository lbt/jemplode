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
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.jempeg.empeg.protocol.EmpegProtocolClient;
import org.jempeg.empeg.protocol.EmpegSynchronizeClient;
import org.jempeg.empeg.util.HijackUtils;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.SocketConnection;
import org.jempeg.protocol.SocketConnectionFactory;

import com.enterprisedt.net.ftp.FTPException;
import com.inzyme.io.StreamUtils;
import com.inzyme.model.Reason;
import com.inzyme.progress.IProgressListener;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.progress.TextProgressListener;
import com.inzyme.properties.PropertiesManager;
import com.inzyme.util.Debug;

/**
* VersionTrackerIfc is implemented by anything that can
* track the latest version of a product.
*
* @author Mike Schrag
*/
public class HijackVersionTracker implements IVersionTracker {
	private IProgressListener myListener;

	private InetAddress myAddress;
	private int myInstalledVersion;
	private int myLatestVersion;
	private String myLatestReleaseFile;
	private Reason myFailureReason;

	public HijackVersionTracker() {
	}

	public HijackVersionTracker(IConnectionFactory _connection) {
		this(new TextProgressListener(), _connection);
	}

	public HijackVersionTracker(IProgressListener _listener, IConnectionFactory _connection) {
		init(_listener, _connection);
	}

	public HijackVersionTracker(InetAddress _address) {
		this(new TextProgressListener(), _address);
	}

	public HijackVersionTracker(IProgressListener _listener, InetAddress _address) {
		init(_listener, _address);
	}

	protected void init(IProgressListener _listener, IConnectionFactory _connection) {
		if (_connection instanceof SocketConnectionFactory) {
			SocketConnectionFactory socketConn = (SocketConnectionFactory) _connection;
			init(_listener, socketConn.getAddress());
		}
		else {
			init(_listener, (InetAddress) null);
		}
	}

	protected void init(IProgressListener _listener, InetAddress _address) {
		myListener = _listener;
		myAddress = _address;
	}

	public String getProductName() {
		return "Hijack";
	}

	public boolean shouldAutoUpdate() {
		return PropertiesManager.getInstance().getBooleanProperty("jempeg.autoUpdateHijack", false);
	}

	public void setAutoUpdate(boolean _autoUpdate) {
		PropertiesManager.getInstance().setBooleanProperty("jempeg.autoUpdateHijack", _autoUpdate);
	}

	public boolean shouldPromptBeforeUpdate() {
		return PropertiesManager.getInstance().getBooleanProperty("jempeg.promptsBeforeUpdate", true);
	}

	public void setPromptsBeforeUpdate(boolean _promptsBeforeUpdate) {
		PropertiesManager.getInstance().setBooleanProperty("jempeg.promptsBeforeUpdate", _promptsBeforeUpdate);
	}

	public String getHijackURL() {
		return PropertiesManager.getInstance().getProperty("jempeg.hijackURL", "http://empeg-hijack.sourceforge.net");
	}

	public void setHijackURL(String _hijackURL) {
		PropertiesManager.getInstance().setProperty("jempeg.hijackURL", _hijackURL);
	}

	public void setMkVersion(String _mkVersion) {
		PropertiesManager.getInstance().setProperty("jempeg.hijackMkVersion", _mkVersion);
	}

	public String getMkVersion() {
		return PropertiesManager.getInstance().getProperty("jempeg.hijackMkVersion", "mk2");
	}

	public String getHijackSaveLocation() {
		return PropertiesManager.getInstance().getProperty("jempeg.hijackSaveLocation", "");
	}

	public void setHijackSaveLocation(String _hijackSaveLocation) {
		PropertiesManager.getInstance().setProperty("jempeg.hijackSaveLocation", _hijackSaveLocation);
	}

	public boolean isInstalled() throws IOException {
		boolean installed;
		try {
			if (myAddress != null) {
				getInstalledVersion();
				installed = true;
			}
			else {
				installed = false;
				Debug.println(Debug.WARNING, "You do not have an address set for Hijack.");
			}
		}
		catch (FTPException e) {
			installed = false;
			e.printStackTrace();
			myFailureReason = new Reason("You must set your Hijack password in Tools=>Options... prior to using Hijack.");
		}
		catch (Throwable t) {
			installed = false;
			Debug.println(Debug.INFORMATIVE, t);
		}
		return installed;
	}

	public Reason getFailureReason(String _operation) {
		Reason r;
		if (myFailureReason == null) {
			r = new Reason("You must have Hijack version 242 or higher, be connecting with jEmplode over Ethernet, and 'Use Hijack when Possible' enabled under Tools=>Options... to " + _operation + ".");
		} else {
			r = myFailureReason;
		}
		return r;
	}

	public String getInstalledVersion() throws IOException {
		myFailureReason = null;
		if (myInstalledVersion == 0) {
			String fullVersion = HijackUtils.getString(myAddress, "/proc/version");
			String versionToken = "-hijack-v";
			int hijackVersionStart = fullVersion.indexOf(versionToken) + versionToken.length();
			int hijackVersionEnd = fullVersion.indexOf(" ", hijackVersionStart);
			String installedVersionStr = fullVersion.substring(hijackVersionStart, hijackVersionEnd);
			myInstalledVersion = Integer.parseInt(installedVersionStr);
		}
		return String.valueOf(myInstalledVersion);
	}

	public String getLatestVersion() throws IOException {
		findLatestRelease();
		return String.valueOf(myLatestVersion);
	}

	public boolean isNewerVersionAvailable() throws IOException {
		findLatestRelease();
		getInstalledVersion();
		boolean newerVersion = (myLatestVersion > myInstalledVersion);
		return newerVersion;
	}

	public String getReleaseNotes() throws IOException {
		findLatestRelease();
		return "None";
	}

	public VersionChange[] getChanges() throws IOException {
		findLatestRelease();

		Vector versionChangesVec = new Vector();
		int sinceVersion = myInstalledVersion;
		URL hijackURL = new URL(getHijackURL());
		BufferedReader br = new BufferedReader(new InputStreamReader(hijackURL.openStream()));
		String releaseNoteStart = "<TR><TD>v";
		boolean done = false;
		do {
			String line = br.readLine();
			if (line == null) {
				done = true;
			}
			else {
				if (line.startsWith(releaseNoteStart)) {
					int versionStop = line.indexOf("<", releaseNoteStart.length());
					String entryVersionStr = line.substring(releaseNoteStart.length(), versionStop);
					String entryFeature = line.substring(versionStop + "<TD>".length());
					int entryVersion = Integer.parseInt(entryVersionStr);
					if (entryVersion > sinceVersion) {
						VersionChange change = new VersionChange(entryVersionStr, entryFeature);
						versionChangesVec.addElement(change);
					}
				}
			}
		}
		while (!done);
		br.close();
		VersionChange[] changes = new VersionChange[versionChangesVec.size()];
		versionChangesVec.copyInto(changes);
		return changes;
	}

	public void installLatestVersion() throws IOException {
		synchronized (myAddress.getHostAddress().intern()) {
			myListener.operationStarted("Upgrading Hijack Kernel...");
			myListener.operationUpdated(0, 7);

			myListener.taskStarted("Checking your Hijack Version...");
			myListener.taskUpdated(0, 1);
			getInstalledVersion();
			myListener.taskUpdated(1, 1);
			myListener.operationUpdated(1, 7);

			findLatestRelease();

			if (isNewerVersionAvailable()) {
				URL latestReleaseURL = new URL(new URL(getHijackURL()), myLatestReleaseFile);
				URLConnection conn = latestReleaseURL.openConnection();
				int length = conn.getContentLength();
				InputStream latestReleaseIS = new BufferedInputStream(conn.getInputStream());

				boolean shouldSave = false;
				String saveLocation = getHijackSaveLocation();
				if (saveLocation.equals("")) {
					saveLocation = System.getProperty("user.home");
				}
				else {
					shouldSave = true;
				}

				File hijackKernel = new File(saveLocation + File.separator + myLatestReleaseFile);
				OutputStream fos = new BufferedOutputStream(new FileOutputStream(hijackKernel));
				myListener.taskStarted("Downloading " + myLatestReleaseFile + " to " + hijackKernel + "...");
				StreamUtils.copy(latestReleaseIS, fos, SocketConnection.TCP_MAXPAYLOAD, length, myListener);
				fos.close();
				latestReleaseIS.close();
				myListener.operationUpdated(3, 7);

				InputStream fis = new BufferedInputStream(new FileInputStream(hijackKernel));
				myListener.taskStarted("Updating Empeg to " + myLatestReleaseFile + "...");
				HijackUtils.upload(myAddress, "/proc/empeg_kernel", fis, 0, length, myListener);
				fis.close();
				myListener.operationUpdated(4, 7);

				myListener.taskStarted("Download kernel to test it " + myLatestReleaseFile + "...");
				File hijackKernelCheck = new File(System.getProperty("user.home") + File.separator + myLatestReleaseFile + ".check");
				FileOutputStream hijackKernelCheckOS = new FileOutputStream(hijackKernelCheck);
				HijackUtils.download(myAddress, "/proc/empeg_kernel", hijackKernelCheckOS, myListener);
				hijackKernelCheckOS.close();

				myListener.operationUpdated(5, 7);

				myListener.taskStarted("Testing kernel " + myLatestReleaseFile + "...");
				InputStream originalIS = null;
				InputStream checkIS = null;
				try {
					originalIS = new BufferedInputStream(new FileInputStream(hijackKernel));
					checkIS = new BufferedInputStream(new FileInputStream(hijackKernelCheck));
					int pos = 0;
					while (originalIS.available() > 0) {
						myListener.taskUpdated(pos, length);
						if (originalIS.read() != checkIS.read()) {
							throw new IOException("The kernel update failed.  Your kernel is probably hosed.  Fix this before you reboot!!!");
						}
						pos++;
					}
				}
				finally {
					if (originalIS != null) {
						originalIS.close();
					}
					if (checkIS != null) {
						checkIS.close();
					}
					if (!shouldSave) {
						hijackKernel.delete();
					}
					hijackKernelCheck.delete();
				}
				myListener.operationUpdated(6, 7);

				myListener.taskStarted("Waiting for reboot...");
				myListener.taskUpdated(0, 1);
				try {
					EmpegSynchronizeClient syncClient = new EmpegSynchronizeClient(new SocketConnectionFactory(myAddress, EmpegProtocolClient.PROTOCOL_TCP_PORT));
					IProtocolClient client = syncClient.getProtocolClient(new SilentProgressListener());
					((EmpegProtocolClient)client).restartUnit(false, true);
					//client.restartPlayer(false);
					client.close();
				}
				catch (Throwable e) {
					Debug.println(e);
				}
				myListener.taskUpdated(1, 1);
			}
			myListener.operationUpdated(6, 7);

			myListener.taskStarted("Done.");
			myListener.taskUpdated(1, 1);
			myListener.operationUpdated(7, 7);
		}
	}

	protected void findLatestRelease() throws IOException {
		if (myLatestReleaseFile == null) {
			myListener.operationStarted("Upgrading Hijack Kernel...");
			myListener.operationUpdated(0, 7);

			myListener.taskStarted("Checking for new Hijack Version...");
			myListener.taskUpdated(0, 3);

			Vector hijackFilenames = new Vector();
			URL newestReleaseURL = new URL(new URL(getHijackURL()), "NEWEST_VERSION");
			InputStream is = newestReleaseURL.openStream();
			myListener.taskUpdated(1, 3);

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while (br.ready()) {
				String line = br.readLine();
				if (line.indexOf("zImage") != -1) {
					hijackFilenames.addElement(line);
				}
			}
			br.close();
			myListener.taskUpdated(2, 3);

			int currentVersion = 0;
			String latestReleaseFile = null;
			String mkVersion = getMkVersion();
			for (int i = 0; i < hijackFilenames.size(); i++) {
				try {
					String hijackFilename = (String) hijackFilenames.elementAt(i);
					String latestVersionStr = hijackFilename.substring(1, hijackFilename.indexOf("."));
					int hijackVersion = Integer.parseInt(latestVersionStr);
					if (hijackVersion > currentVersion && (hijackFilename.indexOf(mkVersion) >= 0)) {
						currentVersion = hijackVersion;
						latestReleaseFile = hijackFilename;
					}
				}
				catch (Throwable t) {
					Debug.println(t);
				}
			}

			myLatestVersion = currentVersion;
			myLatestReleaseFile = latestReleaseFile;

			myListener.taskUpdated(3, 3);
		}
	}

	public boolean isRestartRequired() {
		return false;
	}
}
