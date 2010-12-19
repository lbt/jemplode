package com.inzyme.util;

import java.io.File;

public class ApplicationUtils {
	public static boolean isMac() {
		return (System.getProperty("mrj.version") != null);
	}

	public static File getSettingsFolder() {
		File settingsFolder;
		if (isMac()) {
			settingsFolder = new File(System.getProperty("user.home"), "Library/Application Support/Open Rio");
		}
		else {
			settingsFolder = new File(System.getProperty("user.home"), ".openrio");
		}
		if (!settingsFolder.exists()) {
			settingsFolder.mkdirs();
		}
		return settingsFolder;
	}
}
