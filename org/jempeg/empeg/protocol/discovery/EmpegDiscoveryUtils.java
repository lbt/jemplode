package org.jempeg.empeg.protocol.discovery;

import org.jempeg.empeg.protocol.EmpegSynchronizeClient;
import org.jempeg.nodestore.IDeviceSettings;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.IProtocolClient;

import com.inzyme.io.ChainedIOException;
import com.inzyme.progress.SilentProgressListener;


public class EmpegDiscoveryUtils {
	/**
	* Retrieves the type of the Empeg using the given connection.
	*
	* @param _conn the connection on which to request the type
	* @returns the type of the Empeg on the given connection
	* @throws EmpegDiscoveryException if the type cannot be retrieved
	*/
	public static String getPlayerType(IConnectionFactory _connFactory) throws ChainedIOException {
		try {
			EmpegSynchronizeClient client = new EmpegSynchronizeClient(_connFactory);
			IProtocolClient protocolClient = client.getProtocolClient(new SilentProgressListener());
			try {
				String type = "unknown";
				type = protocolClient.getPlayerType();
				return type;
			}
			finally {
				protocolClient.close();
			}
		}
		catch (Exception e) {
			throw new ChainedIOException("Unable to get the player type for this device.", e);
		}
	}

	/**
	* Returns whether or not an Empeg is connected on the given Connection.
	*
	* @param _conn the connection to ping an Empeg on
	* @returns whether ot not an Empeg is connected
	* @throws EmpegDiscoveryException if the ping fails
	*/
	public static boolean isEmpegConnected(IConnectionFactory _connFactory) throws ChainedIOException {
		try {
			EmpegSynchronizeClient client = new EmpegSynchronizeClient(_connFactory);
			IProtocolClient protocolClient = client.getProtocolClient(new SilentProgressListener());
			try {
				boolean connected = protocolClient.isDeviceConnected();
				return connected;
			}
			finally {
				protocolClient.close();
			}
		}
		catch (Exception e) {
			throw new ChainedIOException("Unable to determine if a device is connected.", e);
		}
	}

	/**
	* Returns the default name for this player type.
	*
	* @param _playerType the type of player that is connected
	* @returns the default name for this player type
	*/
	public static String getDefaultName(String _playerType) {
		String name;
		if ("empeg-car-1".equals(_playerType)) {
			name = "empeg car";
		}
		else if ("empeg-car-2".equals(_playerType)) {
			name = "empeg car";
		}
		else if ("rio-car".equals(_playerType)) {
			name = "Rio Car";
		}
		else if ("jupiter".equals(_playerType)) {
			name = "Jupiter";
		}
		else {
			name = "Unknown Player";
		}
		return name;
	}

	/**
	* Retrieves the name of the Empeg using the given connection (this
	* uses getPlayerConfiguration of the ProtocolClient.
	*
	* @param _conn the connection on which to request the name
	* @param _defaultName the default name to use if one isn't discovered
	* @returns the name of the Empeg on the given connection
	* @throws EmpegDiscoveryException if the name cannot be retrieved
	*/
	public static String getEmpegName(IConnectionFactory _connFactory, String _defaultName) throws ChainedIOException {
		try {
			String name;

			EmpegSynchronizeClient client = new EmpegSynchronizeClient(_connFactory);
			IProtocolClient protocolClient = client.getProtocolClient(new SilentProgressListener());
			try {
				IDeviceSettings dcf = protocolClient.getDeviceSettings();
				name = dcf.getName();
				if (name == null || name.length() == 0) {
					name = _defaultName;
				}
			}
			finally {
				protocolClient.close();
			}

			return name;
		}
		catch (Exception e) {
			throw new ChainedIOException("Unable to get the device name.", e);
		}
	}

}
