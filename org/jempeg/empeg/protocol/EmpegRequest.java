/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

import java.io.FileNotFoundException;

import org.jempeg.empeg.protocol.packet.AbstractEmpegRequestPacket;
import org.jempeg.empeg.protocol.packet.AbstractEmpegResponsePacket;
import org.jempeg.empeg.protocol.packet.BasicEmpegResponsePacket;
import org.jempeg.empeg.protocol.packet.CommandRequestPacket;
import org.jempeg.empeg.protocol.packet.CommandResponsePacket;
import org.jempeg.empeg.protocol.packet.DeleteRequestPacket;
import org.jempeg.empeg.protocol.packet.DeleteResponsePacket;
import org.jempeg.empeg.protocol.packet.EmpegPacketHeader;
import org.jempeg.empeg.protocol.packet.FsckRequestPacket;
import org.jempeg.empeg.protocol.packet.FsckResponsePacket;
import org.jempeg.empeg.protocol.packet.GrabScreenRequestPacket;
import org.jempeg.empeg.protocol.packet.GrabScreenResponsePacket;
import org.jempeg.empeg.protocol.packet.InitiateSessionRequestPacket;
import org.jempeg.empeg.protocol.packet.InitiateSessionResponsePacket;
import org.jempeg.empeg.protocol.packet.MountRequestPacket;
import org.jempeg.empeg.protocol.packet.MountResponsePacket;
import org.jempeg.empeg.protocol.packet.NotifySyncCompleteRequestPacket;
import org.jempeg.empeg.protocol.packet.NotifySyncCompleteResponsePacket;
import org.jempeg.empeg.protocol.packet.PacketConstants;
import org.jempeg.empeg.protocol.packet.PingRequestPacket;
import org.jempeg.empeg.protocol.packet.PingResponsePacket;
import org.jempeg.empeg.protocol.packet.PrepareRequestPacket;
import org.jempeg.empeg.protocol.packet.ProgressResponsePacket;
import org.jempeg.empeg.protocol.packet.QuitRequestPacket;
import org.jempeg.empeg.protocol.packet.RebuildRequestPacket;
import org.jempeg.empeg.protocol.packet.RebuildResponsePacket;
import org.jempeg.empeg.protocol.packet.SessionHeartbeatRequestPacket;
import org.jempeg.empeg.protocol.packet.SessionHeartbeatResponsePacket;
import org.jempeg.empeg.protocol.packet.StatFSRequestPacket;
import org.jempeg.empeg.protocol.packet.StatFSResponsePacket;
import org.jempeg.empeg.protocol.packet.StatRequestPacket;
import org.jempeg.empeg.protocol.packet.StatResponsePacket;
import org.jempeg.empeg.protocol.packet.TerminateSessionRequestPacket;
import org.jempeg.empeg.protocol.packet.TerminateSessionResponsePacket;
import org.jempeg.empeg.protocol.packet.TransferReadRequestPacket;
import org.jempeg.empeg.protocol.packet.TransferResponsePacket;
import org.jempeg.empeg.protocol.packet.TransferWriteRequestPacket;
import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.ProtocolException;

import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.STATUS;
import com.inzyme.typeconv.UINT16;
import com.inzyme.typeconv.UINT32;

/**
* Request is the lowest-level interface to 
* communicating with the Empeg.  Each method
* represents a single request that can be
* made (and its return value).
*
* @author Mike Schrag
* @version $Revision: 1.6 $
*/
public class EmpegRequest {
	public static final long COM_RESTART = 0;
	public static final long COM_LOCKUI = 1;
	public static final long COM_SLUMBER = 2;
	public static final long COM_PLAYFID = 3;
	public static final long COM_PLAYSTATE = 4;
	public static final long COM_BUILDMULTFIDS = 5;
	public static final long COM_PLAYMULTFIDS = 6;

	public static final int PLAYMODE_INSERT = 0;
	public static final int PLAYMODE_APPEND = 1;
	public static final int PLAYMODE_REPLACE = 2;

	public static final int PLAYSTATE_PAUSE = 0;
	public static final int PLAYSTATE_PLAY = 1;
	public static final int PLAYSTATE_TOGGLE = -1;

	public static final long RESTART_EXIT = 0x00;
	public static final long RESTART_PLAYER = 0x01;
	public static final long RESTART_UNIT = 0x02;
	public static final long RESTART_HALT = 0x03;
	public static final long RESTART_UPGRADE_CD = 0x04;
	public static final long RESTART_UPGRADE_DL = 0x05;
	public static final long RESTART_SHELL = 0x06;
	public static final long RESTART_IN_SLUMBER = 0xf00;

	private static final byte[] EMPEG_WAKEUP = new byte[] { 25, 13 };

	private static final int MAX_WRONG_PACKETS = 16;

	private UINT32 myPacketID; // transaction ID

	private UINT16 myProtocolVersionMinor;
	private UINT16 myProtocolVersionMajor;

	private IConnection myConn;
	private ISimpleProgressListener myProtocolProgressListener;

	public EmpegRequest(IConnection _conn, ISimpleProgressListener _protocolProgressListener) {
		myConn = _conn;
		myProtocolProgressListener = _protocolProgressListener;
		myProtocolVersionMajor = new UINT16(-1);
		myProtocolVersionMinor = new UINT16(-1);
		newRequest();
	}

	public void newRequest() {
		//		org.jempeg.util.Debug.println("NEW request");
		myPacketID = RequestIdentifierFactory.reserve();
	}

	public void setRequest(UINT32 _packetID) {
		myPacketID = _packetID;
	}

	public void send(AbstractEmpegRequestPacket _toSend) throws ProtocolException {
		try {
			//		Debug.println(Debug.PEDANTIC, "Sending packet " + _toSend);
			LittleEndianOutputStream eos = myConn.getOutputStream();
			_toSend.write(eos);
			eos.flush();
		}
		catch (Exception e) {
			throw new ProtocolException("Unable to send packet to device.", e);
		}
	}

	public AbstractEmpegResponsePacket waitForReply(UINT32 _packetID) throws ProtocolException {
		//		org.jempeg.util.Debug.println(Debug.PEDANTIC, "Request.waitForReply (id = " + _packetID + ")");

		long timeoutMillis = 5000;

		int wrongPackets = 0; // count of packets with Wrong ID

		AbstractEmpegResponsePacket packet;

		// The first timeout period is 2 seconds, as we should have a
		// PROGRESS or RESPONSE packet returned immediately. If we get
		// a PROGRESS reply then we use the timeout value enclosed to
		// set the timer for the next reply.
		while (true) {
			//			org.jempeg.util.Debug.println("waitForReply loop");

			//			org.jempeg.util.Debug.println(Debug.PEDANTIC, "Waiting for reply with id " + _packetID + " and timeout " + timeoutMillis);

			packet = receive(timeoutMillis);
			EmpegPacketHeader rxHeader = packet.getHeader();

			// Check the packet ID
			if (!rxHeader.getPacketID().equals(_packetID)) {
				String errorMsg = "Packet ID didn't match: received (" + rxHeader + "), expected (id=" + _packetID + ")";
				if (++wrongPackets > MAX_WRONG_PACKETS) {
					throw new ProtocolException(errorMsg);
				}
				else {
					//          Debug.println(Debug.WARNING, errorMsg);
					continue;
				}
			}
			//			Debug.println(Debug.PEDANTIC, "Got a response to packet #" + _packetID + " => " + packet);

			// Check the type
			if (rxHeader.getType() == PacketConstants.OPTYPE_PROGRESS) {
				// We have a progress reply
				ProgressResponsePacket response = (ProgressResponsePacket) packet;

				// Use new supplied timeout
				timeoutMillis = response.getNewTimeout().getValue() * 1000;
				
				progressReported(response.getMaximum().getValue(), response.getStage().getValue(), response.getStageMaximum().getValue(), response.getCurrent().getValue());
			}
			else if (rxHeader.getType() == PacketConstants.OPTYPE_RESPONSE) {
				return packet;
			}
			else {
				throw new ProtocolException("Unknown packet type in header: " + rxHeader);
			}
		}
	}
	
	protected void progressReported(long _maximum, long _stage, long _stageMaximum, long _current) {
		if (myProtocolProgressListener != null) {
			long progress;
			long total = _maximum * _stageMaximum;
			if ((_stage != _stageMaximum) || (_current != _maximum)) {
				progress = (_stage - 1) * _maximum + _current;
			}
			else {
				progress = total;
			}

			if (progress > total) {
				progress = total;
			}
			
			if (progress >= 0 && total >= 0) {
				myProtocolProgressListener.progressReported(progress, total);
			}
		}
	}

	public AbstractEmpegResponsePacket receive(long _timeoutMillis) throws ProtocolException {
		try {
			//		Debug.println(Debug.PEDANTIC, "Request.receive (timeout = " + _timeoutMillis + ")");

			AbstractEmpegResponsePacket response;
			myConn.setTimeout(_timeoutMillis);
			LittleEndianInputStream eis = myConn.getInputStream();

			EmpegPacketHeader header = new EmpegPacketHeader();
			header.read(eis);
			//		Debug.println(Debug.PEDANTIC, "Request.receive: got header = " + header);

			int type = header.getType();
			if (type == PacketConstants.OPTYPE_PROGRESS) {
				//org.jempeg.util.Debug.println("We have a progress reply.");
				response = new ProgressResponsePacket(header);
				response.read(eis);
			}
			else {
				//org.jempeg.util.Debug.println("Got a real reply.");
				int opCode = header.getOpcode();
				switch (opCode) {
					case PacketConstants.OP_PING :
						response = new PingResponsePacket(header);
						break;
					case PacketConstants.OP_MOUNT :
						response = new MountResponsePacket(header);
						break;
					case PacketConstants.OP_STATFID :
						response = new StatResponsePacket(header);
						break;
					case PacketConstants.OP_WRITEFID :
					case PacketConstants.OP_READFID :
					case PacketConstants.OP_PREPAREFID :
						response = new TransferResponsePacket(header);
						break;
					case PacketConstants.OP_DELETEFID :
						response = new DeleteResponsePacket(header);
						break;
					case PacketConstants.OP_REBUILD :
						response = new RebuildResponsePacket(header);
						break;
					case PacketConstants.OP_FSCK :
						response = new FsckResponsePacket(header);
						break;
					case PacketConstants.OP_STATFS :
						response = new StatFSResponsePacket(header);
						break;
					case PacketConstants.OP_COMMAND :
						response = new CommandResponsePacket(header);
						break;
					case PacketConstants.OP_GRABSCREEN :
						response = new GrabScreenResponsePacket(header);
						break;
					case PacketConstants.OP_INITIATESESSION :
						response = new InitiateSessionResponsePacket(header);
						break;
					case PacketConstants.OP_SESSIONHEARTBEAT :
						response = new SessionHeartbeatResponsePacket(header);
						break;
					case PacketConstants.OP_TERMINATESESSION :
						response = new TerminateSessionResponsePacket(header);
						break;
					case PacketConstants.OP_COMPLETE :
						response = new NotifySyncCompleteResponsePacket(header);
						break;
					default :
						response = new BasicEmpegResponsePacket(header);
						break;
				}
				response.read(eis);
			}

			//		Debug.println(Debug.PEDANTIC, "Request.receive: response = " + response);
			return response;
		}
		catch (Exception e) {
			throw new ProtocolException("Failed to receive packet from device.", e);
		}
	}

	public void ping() throws ProtocolException {
		try {
			//		org.jempeg.util.Debug.println("Request.ping()");

			LittleEndianOutputStream eos = myConn.getOutputStream();
			eos.write(EMPEG_WAKEUP);

			AbstractEmpegRequestPacket pingRequest;
			while (true) {
				UINT32 packetID = myPacketID;
				byte[] p = packetID.toArray();

				if (((p[0] != 0) && (p[0] < 32)) || ((p[1] != 0) && (p[1] < 32)) || ((p[2] != 0) && (p[2] < 32)) || ((p[3] != 0) && (p[3] < 32))) {
					newRequest();
					continue;
				}

				pingRequest = new PingRequestPacket(packetID);

				// Make CRC
				UINT16 crc = pingRequest.getCRC();
				p = crc.toArray();
				if (((p[0] != 0) && (p[0] < 32)) || ((p[1] != 0) && (p[1] < 32))) {
					newRequest();
					continue;
				}
				break;
			}

			flush();

			send(pingRequest);
			PingResponsePacket pingResponse = (PingResponsePacket) waitForReply(myPacketID);

			myProtocolVersionMinor = pingResponse.getVersionMinor();
			myProtocolVersionMajor = pingResponse.getVersionMajor();
		}
		catch (Exception e) {
			throw new ProtocolException("Unable to ping device.", e);
		}
	}

	public void quit() throws ProtocolException {
		try {
			LittleEndianOutputStream eos = myConn.getOutputStream();
			eos.write(EMPEG_WAKEUP);

			UINT32 packetID = myPacketID;
			QuitRequestPacket quitRequest = new QuitRequestPacket(packetID);
			flush();
			send(quitRequest);

			// Wait for reply
			waitForReply(packetID);
		}
		catch (Exception e) {
			throw new ProtocolException("Unable to quit.", e);
		}
	}

	public STATUS mount(UINT32 _mode) throws ProtocolException {
		UINT32 packetID = myPacketID;
		MountRequestPacket mountRequest = new MountRequestPacket(packetID, new UINT32(0), _mode);
		flush();
		send(mountRequest);

		MountResponsePacket mountResponse = (MountResponsePacket) waitForReply(packetID);
		STATUS result = mountResponse.getResult();
		return result;
	}

	public UINT32 statFID(UINT32 _fid) throws ProtocolException, FileNotFoundException {
		UINT32 packetID = myPacketID;

		StatRequestPacket statRequest = new StatRequestPacket(packetID, _fid);
		send(statRequest);

		StatResponsePacket statResponse = (StatResponsePacket) waitForReply(packetID);
		UINT32 size = statResponse.getSize();
		if (size.getValue() < 0) {
			throw new FileNotFoundException(_fid + " did not exist.");
		}
		return size;
	}

	public void writeFID(UINT32 _fid, UINT32 _offset, CharArray _buffer) throws ProtocolException {
		writeFID(_fid, _offset, new UINT32(_buffer.getLength()), _buffer);
	}

	public void writeFID(UINT32 _fid, UINT32 _offset, UINT32 _size, CharArray _buffer) throws ProtocolException {
		UINT32 packetID = myPacketID;
		TransferWriteRequestPacket writeRequest = new TransferWriteRequestPacket(packetID, _fid, _offset, _size, _buffer);
		genericWrite(writeRequest);
	}

	protected UINT32 genericWrite(AbstractEmpegRequestPacket _request) throws ProtocolException {
		UINT32 packetID = _request.getHeader().getPacketID();
		send(_request);

		TransferResponsePacket response = (TransferResponsePacket) waitForReply(packetID);
		UINT32 chunkSize = response.getChunkSize();
		return chunkSize;
	}

	public CharArray readFID(UINT32 _fid, UINT32 _offset, UINT32 _size) throws ProtocolException {
		UINT32 packetID = myPacketID;
		TransferReadRequestPacket readRequest = new TransferReadRequestPacket(packetID, _fid, _offset, _size);
		send(readRequest);

		TransferResponsePacket readResponse = (TransferResponsePacket) waitForReply(packetID);

		UINT32 chunkSize = readResponse.getChunkSize();
		if (chunkSize.getValue() < 0) {
			throw new ProtocolException("Chunk size of " + chunkSize + " was less than zero.");
		}

		if (chunkSize.getValue() > _size.getValue()) {
			throw new ProtocolException("Chunk size of " + chunkSize + " was greater than the size " + _size + ".");
		}

		CharArray buffer = readResponse.getBuffer();
		return buffer;
	}

	public UINT32 prepareFID(UINT32 _fid, UINT32 _size) throws ProtocolException {
		UINT32 packetID = myPacketID;
		PrepareRequestPacket prepareRequest = new PrepareRequestPacket(packetID, _fid, _size);
		send(prepareRequest);

		TransferResponsePacket prepareResponse = (TransferResponsePacket) waitForReply(packetID);
		UINT32 chunkSize = prepareResponse.getChunkSize();
		if (chunkSize.getValue() != _size.getValue()) {
			throw new ProtocolException("Unable to write the whole file.  Managed " + chunkSize + " of " + _size + ".");
		}

		if (chunkSize.getValue() < 0) {
			throw new ProtocolException("Chunk size of " + chunkSize + " was less than zero.");
		}
		else {
			return chunkSize;
		}
	}

	public void deleteFID(UINT32 _fid, UINT32 _idMask) throws ProtocolException {
		UINT32 packetID = myPacketID;
		DeleteRequestPacket deleteRequest = new DeleteRequestPacket(packetID, _fid, _idMask);
		send(deleteRequest);

		waitForReply(packetID);
	}

	public void buildAndSaveDatabase(UINT32 _options) throws ProtocolException {
		UINT32 packetID = myPacketID;
		RebuildRequestPacket rebuildRequest = new RebuildRequestPacket(packetID, _options);
		flush();
		send(rebuildRequest);

		waitForReply(packetID);
	}

	public STATUS fsck(String _partition, UINT32 _force) throws ProtocolException {
		UINT32 packetID = myPacketID;
		FsckRequestPacket fsckRequest = new FsckRequestPacket(packetID, _partition, _force);
		flush();
		send(fsckRequest);

		FsckResponsePacket fsckResponse = (FsckResponsePacket) waitForReply(packetID);
		STATUS response = fsckResponse.getResult();
		return response;
	}

	public StatFSResponsePacket freeSpace() throws ProtocolException {
		UINT32 packetID = myPacketID;
		StatFSRequestPacket statFSRequest = new StatFSRequestPacket(packetID);
		flush();
		send(statFSRequest);

		StatFSResponsePacket statFSResponse = (StatFSResponsePacket) waitForReply(packetID);
		return statFSResponse;
	}

	public void sendCommand(UINT32 _command, UINT32 _parameter0, UINT32 _parameter1, String _parameter2) throws ProtocolException {
		UINT32 packetID = myPacketID;
		CommandRequestPacket commandRequest = new CommandRequestPacket(packetID, _command, _parameter0, _parameter1, _parameter2);
		flush();
		send(commandRequest);

		waitForReply(packetID);
	}

	public CharArray grabScreen(UINT32 _command) throws ProtocolException {
		UINT32 packetID = myPacketID;
		GrabScreenRequestPacket grabScreenRequest = new GrabScreenRequestPacket(packetID, _command);
		flush();
		send(grabScreenRequest);

		GrabScreenResponsePacket grabScreenResponse = (GrabScreenResponsePacket) waitForReply(packetID);
		CharArray screen = grabScreenResponse.getScreen();
		return screen;
	}

	public InitiateSessionResponsePacket initiateSession(CharArray _hostDescription, CharArray _password) throws ProtocolException {
		UINT32 packetID = myPacketID;
		InitiateSessionRequestPacket initiateSessionRequest = new InitiateSessionRequestPacket(packetID, _password, _hostDescription);
		flush();
		send(initiateSessionRequest);

		InitiateSessionResponsePacket initiateSessionResponse = (InitiateSessionResponsePacket) waitForReply(packetID);
		return initiateSessionResponse;
	}

	public STATUS sessionHeartbeat(UINT32 _sessionCookie) throws ProtocolException {
		UINT32 packetID = myPacketID;
		SessionHeartbeatRequestPacket sessionHeartbeatRequest = new SessionHeartbeatRequestPacket(packetID, _sessionCookie);
		flush();
		send(sessionHeartbeatRequest);

		SessionHeartbeatResponsePacket sessionHeartbeatResponse = (SessionHeartbeatResponsePacket) waitForReply(packetID);
		STATUS result = sessionHeartbeatResponse.getResult();
		return result;
	}

	public STATUS terminateSession(UINT32 _sessionCookie) throws ProtocolException {
		UINT32 packetID = myPacketID;
		TerminateSessionRequestPacket terminateSessionRequest = new TerminateSessionRequestPacket(packetID, _sessionCookie);
		flush();
		send(terminateSessionRequest);

		TerminateSessionResponsePacket terminateSessionResponse = (TerminateSessionResponsePacket) waitForReply(packetID);
		STATUS result = terminateSessionResponse.getResult();
		return result;
	}

	public STATUS notifySyncComplete() throws ProtocolException {
		UINT32 packetID = myPacketID;
		NotifySyncCompleteRequestPacket notifySyncCompleteRequest = new NotifySyncCompleteRequestPacket(packetID);
		flush();
		send(notifySyncCompleteRequest);

		NotifySyncCompleteResponsePacket notifySyncCompleteResponse = (NotifySyncCompleteResponsePacket) waitForReply(packetID);
		STATUS result = notifySyncCompleteResponse.getResult();
		return result;
	}

	public void flush() throws ProtocolException {
		try {
			//		org.jempeg.util.Debug.println("FLUSH");

			myConn.flushReceiveBuffer();
		}
		catch (ConnectionException e) {
			throw new ProtocolException("Unable to flush connection.", e);
		}
	}

	public UINT16 getProtocolVersionMajor() {
		if (myProtocolVersionMajor.getValue() >= 0) {
			return myProtocolVersionMajor;
		}
		else {
			return new UINT16(-1);
		}
	}

	public UINT16 getProtocolVersionMinor() {
		if (myProtocolVersionMajor.getValue() >= 0) {
			return myProtocolVersionMinor;
		}
		else {
			return new UINT16(-1);
		}
	}
}
