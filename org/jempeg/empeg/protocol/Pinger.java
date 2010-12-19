package org.jempeg.empeg.protocol;

import com.inzyme.util.Debug;

public class Pinger {
  private static final long TIMEOUT = 20 * 1000;

  private EmpegProtocolClient myProtocolClient;
  private long myLastPing;

  public Pinger(EmpegProtocolClient _protocolClient) {
    myProtocolClient = _protocolClient;
    myLastPing = System.currentTimeMillis();
  }

  public void pingIfNecessary() {
    if ((System.currentTimeMillis() - myLastPing) > Pinger.TIMEOUT) {
      Debug.println(Debug.INFORMATIVE, "Pinger.pingIfNecessary: Pinging " + myProtocolClient.getConnection() + " ...");
      myProtocolClient.isDeviceConnected();
      myLastPing = System.currentTimeMillis();
    }
  }
}